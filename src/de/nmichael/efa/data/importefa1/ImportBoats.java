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
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.efa1.*;
import de.nmichael.efa.util.*;
import java.util.*;

public class ImportBoats extends ImportBase {

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
        if (!isIdentical(r.getOwner(), d.get(Boote.VEREIN))) {
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

    private boolean isChangedType(BoatTypeRecord r, DatenFelder d) {
        if (!isIdentical(r.getSeats(), d.get(Boote.ANZAHL))) {
            return true;
        }
        if (!isIdentical(r.getType(), d.get(Boote.ART))) {
            return true;
        }
        if (!isIdentical(r.getRigging(), d.get(Boote.RIGGER))) {
            return true;
        }
        if (!isIdentical(r.getCoxing(), d.get(Boote.STM))) {
            return true;
        }
        return false;
    }

    private BoatTypeRecord createBoatTypeRecord(BoatTypes boatTypes, BoatRecord boat, DatenFelder d, String description, int variant) {
        BoatTypeRecord r = boatTypes.createBoatTypeRecord(boat.getId(), variant);
        if (description != null && description.length() > 0) {
            r.setDescription(description);
        }
        if (d.get(Boote.ANZAHL).length() > 0) {
            r.setSeats(d.get(Boote.ANZAHL));
        }
        if (d.get(Boote.ART).length() > 0) {
            r.setType(d.get(Boote.ART));
        }
        if (d.get(Boote.RIGGER).length() > 0) {
            r.setRigging(d.get(Boote.RIGGER));
        }
        if (d.get(Boote.STM).length() > 0) {
            r.setCoxing(d.get(Boote.STM));
        }
        return r;
    }

    public boolean runImport() {
        try {
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), boote.getFileName()));

            Boats boats = Daten.project.getBoats(true);
            BoatTypes boatTypes = Daten.project.getBoatTypes(true);
            long validFrom = DataAccess.getTimestampFromDate(logbookRec.getStartDate());

            boatsAllowedGroups = new Hashtable<DataKey,String>();
            boatsRequiredGroup = new Hashtable<DataKey,String>();

            DatenFelder d = boote.getCompleteFirst();
            String[] IDX = new String[] { BoatRecord.NAME, BoatRecord.OWNER };
            while (d != null) {
                String boatName = d.get(Boote.NAME);
                String boatNameMain = task.synBoote_genMainName(d.get(Boote.NAME));
                // First search, whether we have imported this boat already
                BoatRecord r = null;
                DataKey[] keys = boats.data().getByFields(IDX, 
                        new String[] {
                                        boatNameMain,
                                        (d.get(Boote.VEREIN).length() > 0 ? d.get(Boote.VEREIN) : null) });
                if (keys != null && keys.length > 0) {
                    // We've found one or more boats with same Name and Owner.
                    // Since we're importing data from efa1, these boats are all identical, i.e. have the same ID.
                    // Therefore their key is identical, so we can just retrieve one boat record with keys[0], which
                    // is valid for this logbook.
                    r = (BoatRecord)boats.data().getValidAt(keys[0], validFrom);
                }

                if (r == null || isChanged(r, d)) {
                    r = boats.createBoatRecord((r != null ? r.getId() : UUID.randomUUID()));
                     
                    r.setName(task.synBoote_genMainName(d.get(Boote.NAME)));
                    if (d.get(Boote.VEREIN).length() > 0) {
                        r.setOwner(d.get(Boote.VEREIN));
                    }
                    if (d.get(Boote.MAX_NICHT_IN_GRUPPE).length() > 0) {
                        r.setMaxNotInGroup(EfaUtil.string2int(d.get(Boote.MAX_NICHT_IN_GRUPPE), 99));
                    }
                    if (d.get(Boote.FREI1).length() > 0) {
                        r.setFreeUse1(d.get(Boote.FREI1));
                    }
                    if (d.get(Boote.FREI2).length() > 0) {
                        r.setFreeUse1(d.get(Boote.FREI2));
                    }
                    if (d.get(Boote.FREI3).length() > 0) {
                        r.setFreeUse1(d.get(Boote.FREI3));
                    }
                    try {
                        DataKey k = boats.data().addValidAt(r, validFrom);
                        if (k != null) {
                            if (d.get(Boote.GRUPPEN).length() > 0) {
                                boatsAllowedGroups.put(k, d.get(Boote.GRUPPEN));
                            }
                            if (d.get(Boote.MIND_1_IN_GRUPPE).length() > 0) {
                                boatsRequiredGroup.put(k, d.get(Boote.MIND_1_IN_GRUPPE));
                            }
                        }
                        logInfo(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                    } catch(Exception e) {
                        logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", r.toString()));
                    }
                    
                } else {
                    logInfo(International.getMessage("Identischer Eintrag: {entry}", r.toString()));
                }

                BoatTypeRecord[] types = r.getAllBoatTypes(true);
                String description = null;
                if (!boatName.equals(boatNameMain)) {
                    description = boatName;
                }
                if (types == null || types.length == 0) {
                    boatTypes.data().addValidAt(createBoatTypeRecord(boatTypes, r, d, description, 1), validFrom);
                } else {
                    boolean found = false;
                    for (int i=0; i<types.length; i++) {
                        if ( ((description == null && types[i].getDescription() == null) ||
                             (description != null && description.equals(types[i].getDescription()))) &&
                             types[i].getValidFrom() <= validFrom && types[i].getInvalidFrom() > validFrom) {
                            if (isChangedType(types[i], d)) {
                                boatTypes.data().addValidAt(createBoatTypeRecord(boatTypes, r, d, description, types.length+1), validFrom);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        boatTypes.data().addValidAt(createBoatTypeRecord(boatTypes, r, d, description, types.length+1), validFrom);
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
