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
import de.nmichael.efa.core.config.EfaTypes;
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
    private Destinations destinations;
    private SessionGroups sessionGroups;
    private String[] boatIdx = new String[] { BoatRecord.NAME, BoatRecord.OWNER };
    private String[] personIdx = new String[] { PersonRecord.FIRSTNAME, PersonRecord.LASTNAME, PersonRecord.ASSOCIATION };
    private String[] destinationIdx = new String[] { DestinationRecord.NAME };
    
    public ImportLogbook(ImportTask task, String efa1fname, ImportMetadata meta) {
        super(task);
        this.meta = meta;
        this.efa1fname = efa1fname;
    }

    public String getDescription() {
        return International.getString("Fahrtenbuch");
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
            long validAt = DataAccess.getTimestampFromDate(logbookRec.getStartDate());

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

            ImportDestinations destinationsImport = new ImportDestinations(task, fahrtenbuch.getDaten().ziele, logbookRec);
            if (!destinationsImport.runImport()) {
                logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", destinationsImport.getDescription(), fahrtenbuch.getDaten().zieleDatei));
                logError(International.getMessage("Import von {list} aus {file} wird abgebrochen.", getDescription(), efa1fname));
                return false;
            }
            // @todo: StatisticSettings

            logbook = Daten.project.getLogbook(meta.name, true);
            sessionGroups = Daten.project.getSessionGroups(true);
            boats = Daten.project.getBoats(false);
            persons = Daten.project.getPersons(false);
            destinations = Daten.project.getDestinations(false);

            Hashtable<String,UUID> sessionGroupMapping = new Hashtable<String,UUID>();

            DatenFelder d = fahrtenbuch.getCompleteFirst();
            while (d != null) {
                LogbookRecord r = logbook.createLogbookRecord(d.get(Fahrtenbuch.LFDNR));
                r.setDate(DataTypeDate.parseDate(d.get(Fahrtenbuch.DATUM)));


                if (d.get(Fahrtenbuch.BOOT).length() > 0) {
                    String b = task.synBoote_genMainName(d.get(Fahrtenbuch.BOOT));
                    UUID id = findBoat(boats, boatIdx, b);
                    if (id != null) {
                        r.setBoatId(id);
                        BoatRecord boat = boats.getBoat(id, validAt);
                        if (boat != null) {
                            BoatTypeRecord[] types = boat.getAllBoatTypes(false);
                            if (types != null) {
                                if (types.length == 1) {
                                    r.setBoatVariant(types[0].getVariant());
                                } else {
                                    for (BoatTypeRecord type : types) {
                                        if (type.getDescription().equals(d.get(Fahrtenbuch.BOOT))) {
                                            r.setBoatVariant(type.getVariant());
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                        r.setBoatName(b);
                    }
                }
                if (d.get(Fahrtenbuch.STM).length() > 0) {
                    UUID id = findPerson(persons, personIdx, d.get(Fahrtenbuch.STM));
                    if (id != null) {
                        r.setCoxId(id);
                    } else {
                        r.setCoxName(d.get(Fahrtenbuch.STM));
                    }
                }
                for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
                    if (d.get(Fahrtenbuch.MANNSCH1 + i).length() > 0) {
                        UUID id = findPerson(persons, personIdx, d.get(Fahrtenbuch.MANNSCH1 + i));
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
                if (d.get(Fahrtenbuch.ZIEL).length() > 0) {
                    UUID id = findDestination(destinations, destinationIdx, d.get(Fahrtenbuch.ZIEL));
                    if (id != null) {
                        r.setDestinationId(id);
                    } else {
                        r.setDestinationName(d.get(Fahrtenbuch.ZIEL));
                    }
                }
                
                r.setBoatDistance(EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.BOOTSKM)), 1, null);
                if (d.get(Fahrtenbuch.BEMERK).length() > 0) {
                    r.setComments(d.get(Fahrtenbuch.BEMERK));
                }

                if (d.get(Fahrtenbuch.FAHRTART).length() > 0) {
                    String fahrtArt = d.get(Fahrtenbuch.FAHRTART).trim();
                    String mtourName = null;
                    Mehrtagesfahrt mtour = null;
                    if (fahrtArt.startsWith(EfaTypes.TYPE_SESSION_MULTIDAY+":")) {
                        mtourName = Fahrtenbuch.getMehrtagesfahrtName(fahrtArt);
                        mtour = (mtourName != null && mtourName.length() > 0 ? fahrtenbuch.getMehrtagesfahrt(mtourName) : null);
                        fahrtArt = EfaTypes.TYPE_SESSION_MULTIDAY;
                    }
                    if (Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_SESSION, fahrtArt)) {
                        r.setSessionType(fahrtArt);
                        if (mtour != null) {
                            // if all in one entry: update fields in LogbookRecord
                            if (!mtour.isEtappen) {
                                if (mtour.ende != null && mtour.ende.length() > 0) {
                                    r.setEndDate(DataTypeDate.parseDate(mtour.ende));
                                }
                                if (mtour.rudertage > 0) {
                                    r.setActiveDays(mtour.rudertage);
                                }
                            }
                            // set/update SessionGroup
                            UUID id = sessionGroupMapping.get(mtourName);
                            if (id == null) {
                                SessionGroupRecord sg = sessionGroups.createSessionGroupRecord(UUID.randomUUID());
                                id = sg.getId();
                                sg.setName(mtourName);
                                sg.setSessionType(EfaTypes.TYPE_SESSION_MULTIDAY);
                                if (mtour.start != null && mtour.start.length() > 0) {
                                    sg.setStartDate(DataTypeDate.parseDate(mtour.start));
                                }
                                if (mtour.ende != null && mtour.ende.length() > 0) {
                                    sg.setEndDate(DataTypeDate.parseDate(mtour.ende));
                                }
                                if (mtour.rudertage > 0) {
                                    sg.setActiveDays(mtour.rudertage);
                                }
                                if (!mtour.isEtappen) {
                                    sg.setDistance((int)r.getBoatDistance(1), 1, null);
                                }
                                // Waters from MultiDayTour's are not imported, but get lost during import.
                                // Should this be fixed? It's only relevant for DRV-Wanderruderstatistik, so it only
                                // matters for years to come, not for any logbooks in the past.
                                // Currently, Waters are only stored in Destinations, but not in SessionGroups, which
                                // is better from a modeling perspective, but incompatible to efa1.
                                try {
                                    sessionGroups.data().add(sg);
                                    sessionGroupMapping.put(mtourName, id);
                                } catch(Exception e) {
                                    logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", sg.toString()));
                                }
                            }
                            r.setSessionGroupId(id);
                        }
                    } else {
                        r.setSessionType(EfaTypes.TYPE_SESSION_NORMAL);
                    }
                } else {
                    r.setSessionType(EfaTypes.TYPE_SESSION_NORMAL);
                }

                try {
                    logbook.data().add(r);
                    logInfo(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                } catch(Exception e) {
                    logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", r.toString()));
                }
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
