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

public class ImportLogbook extends ImportBase {

    private ImportMetadata meta;
    private String efa1fname;

    private Logbook logbook;
    private Boats boats;
    private Persons persons;
    private String[] boatIdx = new String[] { BoatRecord.NAME, BoatRecord.OWNER };
    private String[] personIdx = new String[] { PersonRecord.FIRSTNAME, PersonRecord.LASTNAME, PersonRecord.ASSOCIATION };
    
    public ImportLogbook(ImportTask task, String efa1fname, ImportMetadata meta) {
        super(task);
        this.meta = meta;
        this.efa1fname = efa1fname;
    }

    public String getDescription() {
        return International.getString("Fahrtenbuch");
    }

    private UUID findBoat(String name) {
        String boat = EfaUtil.getName(name);
        String owner = EfaUtil.getVerein(name);
        if (owner != null && owner.length() == 0) {
            owner = null;
        }
        if (boat != null && boat.length() > 0) {
            try {
                DataKey[] keys = boats.data().getByFields(boatIdx, new String[]{boat, owner});
                if (keys != null && keys.length > 0) {
                    return (UUID) (keys[0].getKeyPart1());
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private UUID findPerson(String name) {
        String firstName = EfaUtil.getVorname(name);
        String lastName = EfaUtil.getNachname(name);
        String association = EfaUtil.getVerein(name);
        if (firstName != null && firstName.length() == 0) {
            firstName = null;
        }
        if (lastName != null && lastName.length() == 0) {
            lastName = null;
        }
        if (association != null && association.length() == 0) {
            association = null;
        }
        if ((firstName != null && firstName.length() > 0) ||
            (lastName != null && lastName.length() > 0)) {
            try {
                DataKey[] keys = persons.data().getByFields(personIdx, new String[]{firstName, lastName, association});
                if (keys != null && keys.length > 0) {
                    return (UUID) (keys[0].getKeyPart1());
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    public boolean runImport() {
        Fahrtenbuch origFahrtenbuch = Daten.fahrtenbuch;
        try {
            Fahrtenbuch fahrtenbuch = new Fahrtenbuch(efa1fname);
            Daten.fahrtenbuch = fahrtenbuch;
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), efa1fname));
            if (!fahrtenbuch.readFile()) {
                logError(LogString.logstring_fileOpenFailed(efa1fname, getDescription()));
                return false;
            }

            ProjectRecord logbookRec = Daten.project.createNewLogbookRecord(meta.name);
            logbookRec.setDescription(meta.description);
            logbookRec.setStartDate(meta.firstDate);
            logbookRec.setEndDate(meta.lastDate);
            Daten.project.addLogbookRecord(logbookRec);

            ImportBoats boatsImport = new ImportBoats(task, fahrtenbuch.getDaten().boote, logbookRec);
            if (!boatsImport.runImport()) {
                logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", boatsImport.getDescription(), fahrtenbuch.getDaten().bootDatei));
                logError(International.getMessage("Import von {list} aus {file} wird abgebrochen.", getDescription(), efa1fname));
                return false;
            }

            ImportPersons personsImport = new ImportPersons(task, fahrtenbuch.getDaten().mitglieder, logbookRec);
            if (!personsImport.runImport()) {
                logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", personsImport.getDescription(), fahrtenbuch.getDaten().mitgliederDatei));
                logError(International.getMessage("Import von {list} aus {file} wird abgebrochen.", getDescription(), efa1fname));
                return false;
            }

            logbook = Daten.project.getLogbook(meta.name, true);
            boats = Daten.project.getBoats(false);
            persons = Daten.project.getPersons(false);


            DatenFelder d = fahrtenbuch.getCompleteFirst();
            while (d != null) {
                LogbookRecord r = LogbookRecord.createLogbookRecord(d.get(Fahrtenbuch.LFDNR));
                r.setDate(DataTypeDate.parseDate(d.get(Fahrtenbuch.DATUM)));


                if (d.get(Fahrtenbuch.BOOT).length() > 0) {
                    UUID id = findBoat(d.get(Fahrtenbuch.BOOT));
                    if (id != null) {
                        r.setBoatId(id);
                        // @todo set BoatVariantId
                    } else {
                        r.setBoatName(d.get(Fahrtenbuch.BOOT));
                    }
                }
                if (d.get(Fahrtenbuch.STM).length() > 0) {
                    UUID id = findPerson(d.get(Fahrtenbuch.STM));
                    if (id != null) {
                        r.setCoxId(id);
                    } else {
                        r.setCoxName(d.get(Fahrtenbuch.STM));
                    }
                }
                for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
                    if (d.get(Fahrtenbuch.MANNSCH1 + i).length() > 0) {
                        UUID id = findPerson(d.get(Fahrtenbuch.MANNSCH1 + i));
                        if (id != null) {
                            r.setCrewId(i+1, id);
                        } else {
                            r.setCrewName(i+1, d.get(Fahrtenbuch.MANNSCH1 + i));
                        }
                    }
                }
                r.setBoatCaptainPosition(EfaUtil.string2int(d.get(Fahrtenbuch.OBMANN),-1));
                r.setStartTime(DataTypeTime.parseTime(d.get(Fahrtenbuch.ABFAHRT)));
                r.setEndTime(DataTypeTime.parseTime(d.get(Fahrtenbuch.ANKUNFT)));
                r.setDestinationName(d.get(Fahrtenbuch.ZIEL));
                r.setBoatDistance(EfaUtil.string2int(d.get(Fahrtenbuch.BOOTSKM), 0), 1, null); //@todo stimmt nicht
                if (d.get(Fahrtenbuch.BEMERK).length() > 0) {
                    r.setComments(d.get(Fahrtenbuch.BEMERK));
                }
                // @todo set SessionType
                // @todo set MultiDay
                logbook.data().add(r);
                logInfo(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                d = fahrtenbuch.getCompleteNext();
            }
            logbook.close();
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), efa1fname));
            logError(e.toString());
            e.printStackTrace();
            return false;
        } finally {
            Daten.fahrtenbuch = origFahrtenbuch;
        }
        return true;
    }

}
