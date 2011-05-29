/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.importefa1;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.efa1.*;
import de.nmichael.efa.util.*;
import java.util.*;

public class ImportBoats extends ImportBase {

    private Boats boats;
    private BoatTypes boatTypes;
    private Boote boote;
    private ProjectRecord logbookRec;
    private Hashtable<DataKey,String> boatsAllowedGroups;
    private Hashtable<DataKey,String> boatsRequiredGroup;

    public ImportBoats(ImportTask task, Boote boote, ProjectRecord logbookRec) {
        super(task);
        this.boote = boote;
        this.logbookRec = logbookRec;
    }

    public String getDescription() {
        return International.getString("Boote");
    }

    private boolean isIdentical(Object o, String s) {
        if (o == null && (s == null || s.length() == 0)) {
            return true;
        }
        if (o == null || s == null) {
            return false;
        }
        return (o.toString().equals(s));
    }

    private boolean isChanged(BoatRecord r, DatenFelder d) {
        if (!isIdentical(r.getName(), task.synBoote_genMainName(d.get(Boote.NAME)))) {
            return true;
        }
        if (!isIdentical(r.getNameAffix(), d.get(Boote.VEREIN))) {
            return true;
        }
        if (!isIdentical(boatsAllowedGroups.get(r.getKey()) , d.get(Boote.GRUPPEN))) {
            return true;
        }
        if (!isIdentical( (r.getMaxNotInGroup() == DataAccess.UNDEFINED_INT ? null : r.getMaxNotInGroup()), d.get(Boote.MAX_NICHT_IN_GRUPPE))) {
            return true;
        }
        if (!isIdentical(boatsRequiredGroup.get(r.getKey()) , d.get(Boote.MIND_1_IN_GRUPPE))) {
            return true;
        }

        if (!isIdentical(r.getFreeUse1(), d.get(Boote.FREI1))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse2(), d.get(Boote.FREI2))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse3(), d.get(Boote.FREI3))) {
            return true;
        }
        return false;
    }

    private boolean findBoatType(BoatRecord r, DatenFelder d) {
        for (int i=0; i<r.getNumberOfVariants(); i++) {
            if (!isIdentical(r.getTypeType(i), d.get(Boote.ART))) {
                continue;
            }
            if (!isIdentical(r.getTypeSeats(i), d.get(Boote.ANZAHL))) {
                continue;
            }
            if (!isIdentical(r.getTypeRigging(i), d.get(Boote.RIGGER))) {
                continue;
            }
            if (!isIdentical(r.getTypeCoxing(i), d.get(Boote.STM))) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void updateHashes(DataKey k, DatenFelder d) {
        if (k != null) {
            if (d.get(Boote.GRUPPEN).length() > 0) {
                boatsAllowedGroups.put(k, d.get(Boote.GRUPPEN));
            }
            if (d.get(Boote.MIND_1_IN_GRUPPE).length() > 0) {
                boatsRequiredGroup.put(k, d.get(Boote.MIND_1_IN_GRUPPE));
            }
        }
    }

    public boolean runImport() {
        try {
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), boote.getFileName()));

            boats = Daten.project.getBoats(true);
            long validFrom = logbookRec.getStartDate().getTimestamp(null);

            boatsAllowedGroups = new Hashtable<DataKey,String>();
            boatsRequiredGroup = new Hashtable<DataKey,String>();

            DatenFelder d = boote.getCompleteFirst();
            String[] IDX = BoatRecord.IDX_NAME_NAMEAFFIX;
            while (d != null) {
                String boatName = d.get(Boote.NAME);
                String boatNameMain = task.synBoote_genMainName(d.get(Boote.NAME));

                // First search, whether we have imported this boat already
                BoatRecord boatRecord = null;
                DataKey[] keys = boats.data().getByFields(IDX, 
                        new String[] {
                                        boatNameMain,
                                        (d.get(Boote.VEREIN).length() > 0 ? d.get(Boote.VEREIN) : null) });
                if (keys != null && keys.length > 0) {
                    // We've found one or more boats with same Name and Owner.
                    // Since we're importing data from efa1, these boats are all identical, i.e. have the same ID.
                    // Therefore their key is identical, so we can just retrieve one boat record with keys[0], which
                    // is valid for this logbook.
                    boatRecord = (BoatRecord)boats.data().getValidAt(keys[0], validFrom);
                }

                String description = null;
                if (!boatName.equals(boatNameMain)) {
                    description = boatName;
                }
                boolean newBoatRecord = false;
                boolean changedBoatRecord = false;
                if (boatRecord == null || isChanged(boatRecord, d)) {
                    newBoatRecord = (boatRecord == null);
                    changedBoatRecord = (boatRecord != null);
                    boatRecord = boats.createBoatRecord((boatRecord != null ? boatRecord.getId() : UUID.randomUUID()));
                     
                    boatRecord.setName(task.synBoote_genMainName(d.get(Boote.NAME)));
                    if (d.get(Boote.VEREIN).length() > 0) {
                        boatRecord.setNameAffix(d.get(Boote.VEREIN));
                        boatRecord.setOwner(d.get(Boote.VEREIN));
                    }
                    if (d.get(Boote.MAX_NICHT_IN_GRUPPE).length() > 0) {
                        boatRecord.setMaxNotInGroup(EfaUtil.string2int(d.get(Boote.MAX_NICHT_IN_GRUPPE), 99));
                    }
                    if (d.get(Boote.FREI1).length() > 0) {
                        boatRecord.setFreeUse1(d.get(Boote.FREI1));
                    }
                    if (d.get(Boote.FREI2).length() > 0) {
                        boatRecord.setFreeUse1(d.get(Boote.FREI2));
                    }
                    if (d.get(Boote.FREI3).length() > 0) {
                        boatRecord.setFreeUse1(d.get(Boote.FREI3));
                    }
                    boatRecord.setDefaultSessionType(EfaTypes.TYPE_SESSION_NORMAL);
                }

                if (!findBoatType(boatRecord, d)) {
                    boatRecord.addTypeVariant(description, d.get(Boote.ART), d.get(Boote.ANZAHL), d.get(Boote.RIGGER), d.get(Boote.STM));
                    changedBoatRecord = true;
                }

                if (newBoatRecord) {
                    try {
                        DataKey k = boats.addNewBoatRecord(boatRecord, validFrom);
                        updateHashes(k, d);
                        logDetail(International.getMessage("Importiere Eintrag: {entry}", boatRecord.toString()));
                    } catch(Exception e) {
                        logError(International.getMessage("Import von Eintrag fehlgeschlagen: {entry} ({error})", boatRecord.toString(), e.toString()));
                    }
                } else {
                    if (changedBoatRecord) {
                        try {
                            if (boatRecord.getValidFrom() == validFrom) {
                                boats.data().update(boatRecord);
                            } else {
                                DataKey k = boats.data().addValidAt(boatRecord, validFrom);
                                updateHashes(k, d);
                            }
                            logDetail(International.getMessage("Importiere Eintrag: {entry}", boatRecord.toString()));
                        } catch (Exception e) {
                            logError(International.getMessage("Import von Eintrag fehlgeschlagen: {entry} ({error})", boatRecord.toString(), e.toString()));
                        }
                    } else {
                        logDetail(International.getMessage("Identischer Eintrag: {entry}", boatRecord.toString()));
                    }
                }
                d = boote.getCompleteNext();
            }
            task.setBoatsAllowedGroups(boatsAllowedGroups);
            task.setBoatsRequiredGroup(boatsRequiredGroup);
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), boote.getFileName()));
            logError(e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean runPostprocessing(Hashtable<DataKey,String> boatsAllowedGroups, Hashtable<DataKey,String> boatsRequiredGroup, 
            Hashtable<String,UUID> groupMapping) {

        Boats boats = Daten.project.getBoats(false);
        Groups groups = Daten.project.getGroups(false);

        for (DataKey k : boatsAllowedGroups.keySet()) {
            Vector<String> gruppen = EfaUtil.split(boatsAllowedGroups.get(k), ';');
            DataTypeList<UUID> groupList = new DataTypeList<UUID>();
            for (String g : gruppen) {
                g = g.trim();
                UUID id = groupMapping.get(g);
                if (id != null) {
                    groupList.add(id);
                }
            }
            if (groupList.length() > 0) {
                try {
                    BoatRecord boat = (BoatRecord)boats.data().get(k);
                    if (boat != null) {
                        boat.setAllowedGroupIdList(groupList);
                    }
                    boats.data().update(boat);
                } catch(Exception e) {
                    // no special handling
                }
            }
        }

        for (DataKey k : boatsRequiredGroup.keySet()) {
            String g = boatsRequiredGroup.get(k).trim();
            UUID id = groupMapping.get(g);
            if (id != null) {
                try {
                    BoatRecord boat = (BoatRecord)boats.data().get(k);
                    if (boat != null) {
                        boat.setRequiredGroupId(id);
                    }
                    boats.data().update(boat);
                } catch(Exception e) {
                    // no special handling
                }
            }
        }

        return true;
    }

}
