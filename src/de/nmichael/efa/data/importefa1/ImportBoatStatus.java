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

public class ImportBoatStatus extends ImportBase {

    private ImportMetadata meta;
    private String efa1fname;

    public ImportBoatStatus(ImportTask task, String efa1fname, ImportMetadata meta) {
        super(task);
        this.meta = meta;
        this.efa1fname = efa1fname;
    }

    public String getDescription() {
        return International.getString("Bootsstatus");
    }

    public boolean runImport() {
        try {
            BootStatus bootStatus = new BootStatus(efa1fname);
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), efa1fname));
            if (!bootStatus.readFile()) {
                logError(LogString.logstring_fileOpenFailed(efa1fname, getDescription()));
                return false;
            }

            BoatStatus boatStatus = Daten.project.getBoatStatus(true);
            BoatReservations boatReservations = Daten.project.getBoatReservations(true);
            BoatDamages boatDamages = Daten.project.getBoatDamages(true);
            Boats boats = Daten.project.getBoats(false); // must be imported first!
            Persons persons = Daten.project.getPersons(false); // must be imported first!
            String[] IDXB = new String[] { BoatRecord.NAME, BoatRecord.OWNER };
            String[] IDXP = new String[] { PersonRecord.FIRSTNAME, PersonRecord.LASTNAME, PersonRecord.ASSOCIATION };

            DatenFelder d = bootStatus.getCompleteFirst();
            while (d != null) {
                UUID boatID = findBoat(boats, IDXB, d.get(BootStatus.NAME));
                if (boatID != null) {
                    // create new BoatStatusRecord
                    BoatStatusRecord rs = boatStatus.createBoatStatusRecord(boatID);
                    try {
                        if (d.get(BootStatus.STATUS).length() > 0) {
                            rs.setStatus(d.get(BootStatus.STATUS));
                        }
                        if (d.get(BootStatus.LFDNR).length() > 0) {
                            rs.setEntryNo(d.get(BootStatus.LFDNR));
                            // @todo: set a reference to the corresponding logbook as well
                        }
                        if (d.get(BootStatus.BEMERKUNG).length() > 0) {
                            rs.setComment(d.get(BootStatus.BEMERKUNG));
                        }
                        boatStatus.data().add(rs);
                        logInfo(International.getMessage("Importiere Eintrag: {entry}", rs.toString()));
                    } catch(Exception e) {
                        logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", rs.toString()));
                    }

                    // BoatReservations
                    Vector<BoatReservation> reservierungen = BootStatus.getReservierungen(d);
                    for (int i=0; reservierungen != null && i<reservierungen.size(); i++) {
                        BoatReservationRecord rr = boatReservations.createBoatReservationsRecord(boatID, i+1);
                        BoatReservation r = reservierungen.get(i);
                        try {
                            if (r.isOneTimeReservation()) {
                                rr.setType(BoatReservationRecord.TYPE_ONETIME);
                                if (r.getDateFrom() != null && r.getDateFrom().length() > 0) {
                                    rr.setDateFrom(DataTypeDate.parseDate(r.getDateFrom()));
                                }
                                if (r.getDateTo() != null && r.getDateTo().length() > 0) {
                                    rr.setDateTo(DataTypeDate.parseDate(r.getDateTo()));
                                }
                                if (r.getTimeFrom() != null && r.getTimeFrom().length() > 0) {
                                    rr.setTimeFrom(DataTypeTime.parseTime(r.getTimeFrom()));
                                }
                                if (r.getTimeTo() != null && r.getTimeTo().length() > 0) {
                                    rr.setTimeTo(DataTypeTime.parseTime(r.getTimeTo()));
                                }
                            } else {
                                rr.setType(BoatReservationRecord.TYPE_WEEKLY);
                                if (r.getWeekdayFrom() != null && r.getWeekdayFrom().length() > 0) {
                                    rr.setDayOfWeek(r.getWeekdayFrom());
                                }
                            }
                            if (r.getForName() != null && r.getForName().length() > 0) {
                                UUID id = findPerson(persons, IDXP, r.getForName());
                                if (id != null) {
                                    rr.setPersonId(id);
                                } else {
                                    rr.setPersonName(r.getForName());
                                }
                            }
                            if (r.getReason() != null && r.getReason().length() > 0) {
                                rr.setReason(r.getReason());
                            }
                            boatReservations.data().add(rr);
                            logInfo(International.getMessage("Importiere Eintrag: {entry}", rr.toString()));
                        } catch (Exception e) {
                            logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", rr.toString()));
                        }
                    }

                    // BoatDamages
                    if (d.get(BootStatus.BOOTSSCHAEDEN).length() > 0) {
                        BoatDamageRecord rd = boatDamages.createBoatDamageRecord(boatID, 1);
                        try {
                            rd.setDescription(d.get(BootStatus.BOOTSSCHAEDEN));
                            boatReservations.data().add(rd);
                            logInfo(International.getMessage("Importiere Eintrag: {entry}", rd.toString()));
                        } catch (Exception e) {
                            logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", rd.toString()));
                        }
                    }
                }
                d = bootStatus.getCompleteNext();
            }
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), efa1fname));
            logError(e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
