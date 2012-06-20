/* Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.data.types.DataTypeList;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.LogString;
import de.nmichael.efa.util.Logger;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.UUID;

public class Audit extends Thread {

    private static final long MAX_MESSAGES_FILESIZE = 1024*1024;
    
    private Project project;
    private boolean correctErrors;
    private int warnings = 0;
    private int infos = 0;

    public Audit(Project project) {
        this.project = project;
        this.correctErrors = Daten.efaConfig.getValueDataAuditCorrectErrors();
    }

    /*
     * @todo (P3) Audit - SessionGroups
     */

    private int runAuditPersistence(StorageObject p, String dataType) {
        if (p != null && p.isOpen()) {
            Logger.log(Logger.DEBUG, Logger.MSG_DATA_AUDIT, dataType + " open (" + p.toString() + ")");
            return 0;
        } else {
            Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT, dataType + " not open");
            return 1;
        }
    }


    // this method constructs a DataKey based on the UUID. Other keys
    // are not supported
    private boolean isReferenceInvalid(UUID id, StorageObject so, long validAt) {
        if (id == null) {
            return false;
        }
        DataKey k = new DataKey(id, null, null);
        try {
            if (validAt >= 0) {
                return so.dataAccess.getValidAt(k, validAt) == null;
            } else {
                if (so.dataAccess.getMetaData().isVersionized()) {
                    return so.dataAccess.getValidLatest(k) == null;
                } else {
                    return so.dataAccess.get(k) == null;
                }
            }
        } catch(Exception e) {
            Logger.logdebug(e);
            return false;
        }
    }

    private UUID findValidReference(String name, StorageObject so, long validAt) {
        if (name == null || name.length() == 0) {
            return null;
        }
        if (so instanceof Boats) {
            BoatRecord r = ((Boats)so).getBoat(name, validAt);
            return (r != null ? r.getId() : null);
        }
        if (so instanceof Persons) {
            PersonRecord r = ((Persons)so).getPerson(name, validAt);
            return (r != null ? r.getId() : null);
        }
        if (so instanceof Destinations) {
            DestinationRecord r = ((Destinations)so).getDestination(name, validAt);
            return (r != null ? r.getId() : null);
        }
        return null;
    }

    private String getNameOfLatestInvalidRecord(UUID id, StorageObject so) {
        try {
            DataRecord[] recs = so.dataAccess.getValidAny(new DataKey(id, null, null));
            long latestValid = -1;
            DataRecord latestRecord = null;
            for (int i=0; recs != null && i<recs.length; i++) {
                if (recs[i].getValidFrom() > latestValid || latestValid < 0) {
                    latestValid = recs[i].getValidFrom();
                    latestRecord = recs[i];
                }
            }
            return (latestRecord != null ? latestRecord.getQualifiedName() : null);
        } catch (Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    private int runAuditBoats() {
        int errors = 0;
        try {
            Boats boats = project.getBoats(false);
            if (boats.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            BoatStatus boatStatus = project.getBoatStatus(false);
            BoatReservations boatReservations = project.getBoatReservations(false);
            BoatDamages boatDamages = project.getBoatDamages(false);
            Groups groups = project.getGroups(false);
            Crews crews = project.getCrews(false);
            Destinations destinations = project.getDestinations(false);
            Persons persons = project.getPersons(false);

            Hashtable<UUID,Integer> boatVersions = new Hashtable<UUID,Integer>();

            DataKeyIterator it = boats.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                BoatRecord boat = (BoatRecord)boats.data().get(k);
                if (boat.getId() == null ||
                        boat.getValidFrom() < 0 || boat.getInvalidFrom() < 0 ||
                        boat.getValidFrom() >= boat.getInvalidFrom()) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT_INVALIDREC,
                            "Boat Record is invalid: " + boat.toString());
                    errors++;
                }
                if (boat.getDeleted()) {
                    // if this boat is marked as deleted, treat it as if it wasn't there any more!

                    // clean up all references to this boat
                    DataRecord bsr = boatStatus.getBoatStatus(boat.getId());
                    if (bsr != null) {
                        if (correctErrors) {
                            boatStatus.dataAccess.delete(bsr.getKey());
                        }
                    }
                    BoatReservationRecord[] brr = boatReservations.getBoatReservations(boat.getId());
                    if (brr != null) {
                        for (BoatReservationRecord r : brr) {
                            if (correctErrors) {
                                boatReservations.dataAccess.delete(r.getKey());
                            }
                        }
                    }
                    BoatDamageRecord[] bdr = boatDamages.getBoatDamages(boat.getId());
                    if (bdr != null) {
                        for (BoatDamageRecord r : bdr) {
                            if (correctErrors) {
                                boatDamages.dataAccess.delete(r.getKey());
                            }
                        }
                    }
                    // don't do anything else for this boat; it's deleted
                    k = it.getNext();
                    continue;
                }
                Integer versions = boatVersions.get(boat.getId());
                if (versions == null) {
                    boatVersions.put(boat.getId(), 1);
                } else {
                    boatVersions.put(boat.getId(), versions.intValue() + 1);
                }

                // check References from BoatRecord
                boolean updated = false;
                if (groups.dataAccess.getNumberOfRecords() > 0) {
                    // run check only agains non-empty list (could be due to error opening list)
                    DataTypeList<UUID> uuidList = boat.getAllowedGroupIdList();
                    boolean listChanged = false;
                    for (int i = 0; uuidList != null && i < uuidList.length(); i++) {
                        if (isReferenceInvalid(uuidList.get(i), groups, -1)) {
                            uuidList.remove(i--);
                            listChanged = true;
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                    "runAuditBoats(): "
                                    + International.getString("Boot") + " " + boat.getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                    International.getString("Gruppe"),
                                    International.getString("Gruppen, die dieses Boot benutzen dürfen")));
                            warnings++;
                        }
                    }
                    if (listChanged) {
                        boat.setAllowedGroupIdList(uuidList);
                        updated = true;
                    }
                    if (isReferenceInvalid(boat.getRequiredGroupId(), groups, -1)) {
                        boat.setRequiredGroupId(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                "runAuditBoats(): "
                                + International.getString("Boot") + " " + boat.getQualifiedName() + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                International.getString("Gruppe"),
                                International.getString("Gruppe, der mindestens eine Person angehören muß")));
                        warnings++;
                    }
                    if (isReferenceInvalid(boat.getDefaultCrewId(), crews, -1)) {
                        boat.setDefaultCrewId(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                "runAuditBoats(): "
                                + International.getString("Boot") + " " + boat.getQualifiedName() + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                International.getString("Mannschaft"),
                                International.getString("Standard-Mannschaft")));
                        warnings++;
                    }
                    if (boat.getDefaultSessionType() != null &&
                        !Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_SESSION, boat.getDefaultSessionType())) {
                        boat.setDefaultSessionType(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                "runAuditBoats(): "
                                + International.getString("Boot") + " " + boat.getQualifiedName() + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                International.getString("Fahrtart"),
                                International.getString("Standard-Fahrtart")));
                        warnings++;
                    }
                }
                if (destinations.dataAccess.getNumberOfRecords() > 0) {
                    // run check only agains non-empty list (could be due to error opening list)
                    if (isReferenceInvalid(boat.getDefaultDestinationId(), destinations, -1)) {
                        boat.setDefaultDestinationId(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                "runAuditBoats(): "
                                + International.getString("Boot") + " " + boat.getQualifiedName() + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                International.getString("Ziel"),
                                International.getString("Standard-Ziel")));
                        warnings++;
                    }
                }
                if (updated) {
                    if (correctErrors) {
                        boats.data().update(boat);
                    }
                }

                BoatStatusRecord status = boatStatus.getBoatStatus(boat.getId());
                if (status == null) {
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                            "No Boat Status found for Boat " + boat.getQualifiedName() + ": " + boat.toString());
                    warnings++;
                    if (correctErrors) {
                        boatStatus.data().add(boatStatus.createBoatStatusRecord(boat.getId(),
                            boat.getQualifiedName()));
                    }
                    Logger.log(Logger.INFO, Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                            "New Boat Status added for Boat " + boat.getQualifiedName());
                    infos++;
                } else {
                    // fix text field in boat status to match the current boat name
                    if (!boat.getQualifiedName().equals(status.getBoatText())) {
                        status.setBoatText(boat.getQualifiedName());
                        if (correctErrors) {
                            boatStatus.data().update(status);
                        }
                    }
                }

                k = it.getNext();
            }

            // Boat Status
            it = boatStatus.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatStatusRecord status = (BoatStatusRecord) boatStatus.data().get(k);
                if (!status.getUnknownBoat()) {
                    DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(status.getBoatId(), 0));
                    if (boat == null || boat.length == 0) {
                        Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                                "No Boat found for Boat Status: " + status.toString());
                        errors++;
                        if (correctErrors) {
                            boatStatus.dataAccess.delete(status.getKey());
                        }
                        Logger.log(Logger.INFO, Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                                "Boat Status " + status.toString() + " deleted.");
                        infos++;
                        k = it.getNext();
                        continue;
                    }
                }

                // check References from BoatStatus
                boolean updated = false;
                if (Daten.applID == Daten.APPL_EFABH &&
                    BoatStatusRecord.STATUS_ONTHEWATER.equals(status.getCurrentStatus())) {
                    String logbookName = status.getLogbook();
                    DataTypeIntString entryNo = status.getEntryNo();
                    if (logbookName == null || entryNo == null) {
                        status.setCurrentStatus(status.getBaseStatus());
                        status.setEntryNo(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_BOATSTATUSCORRECTED,
                                "runAuditBoats(): "
                                + International.getString("Bootsstatus") + " " + status.getBoatText() + ": "
                                + International.getMessage("Bootsstatus '{status}' korrigiert nach '{status}'.",
                                    status.getStatusDescription(BoatStatusRecord.STATUS_ONTHEWATER),
                                    status.getStatusDescription(status.getBaseStatus()))
                                + "(Logbook or EntryNo not set)");
                        warnings++;
                    }
                    if (!logbookName.equals(project.getCurrentLogbookEfaBoathouse())) {
                        Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_INVALIDREFFOUND,
                                "runAuditBoats(): "
                                + International.getString("Bootsstatus") + " " + status.getBoatText() + ": "
                                + International.getMessage("Boot ist unterwegs in Fahrtenbuch {name}, aber Fahrtenbuch {name} ist geöffnet.",
                                                           logbookName, project.getCurrentLogbookEfaBoathouse()) + " "
                                + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
                        errors++;
                    } else {
                        Logbook logbook = project.getLogbook(logbookName, false);
                        if (logbook == null ||
                                logbook.dataAccess.getNumberOfRecords() == 0) {
                            status.setCurrentStatus(status.getBaseStatus());
                            status.setLogbook(null);
                            status.setEntryNo(null);
                            updated = true;
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_BOATSTATUSCORRECTED,
                                    "runAuditBoats(): "
                                    + International.getString("Bootsstatus") + " " + status.getBoatText() + ": "
                                    + International.getMessage("Bootsstatus '{status}' korrigiert nach '{status}'.",
                                    status.getStatusDescription(BoatStatusRecord.STATUS_ONTHEWATER),
                                    status.getStatusDescription(status.getBaseStatus()))
                                    + "(Logbook '" + logbookName + "' does not exist)");
                            warnings++;
                        } else {
                            LogbookRecord lr = logbook.getLogbookRecord(entryNo);
                            if (lr == null) {
                                status.setCurrentStatus(status.getBaseStatus());
                                status.setLogbook(null);
                                status.setEntryNo(null);
                                updated = true;
                                Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_BOATSTATUSCORRECTED,
                                        "runAuditBoats(): "
                                        + International.getString("Bootsstatus") + " " + status.getBoatText() + ": "
                                        + International.getMessage("Bootsstatus '{status}' korrigiert nach '{status}'.",
                                        status.getStatusDescription(BoatStatusRecord.STATUS_ONTHEWATER),
                                        status.getStatusDescription(status.getBaseStatus()))
                                        + "(Entry #" + entryNo.toString() + " in Logbook '" + logbookName + "' does not exist)");
                                warnings++;
                            }
                        }
                    }
                }

                k = it.getNext();
            }

            // Boat Reservations
            it = boatReservations.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatReservationRecord reservation = (BoatReservationRecord)boatReservations.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(reservation.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                            "No Boat found for Boat Reservation: " + reservation.toString());
                    errors++;
                    if (correctErrors) {
                        boatReservations.dataAccess.delete(reservation.getKey());
                    }
                    Logger.log(Logger.INFO, Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                            "Boat Reservation " + reservation.toString() + " deleted.");
                    infos++;
                    k = it.getNext();
                    continue;
                }

                // check References from BoatReservations
                if (persons.dataAccess.getNumberOfRecords() > 0) {
                    // run check only agains non-empty list (could be due to error opening list)
                    if (isReferenceInvalid(reservation.getPersonId(), persons, -1)) {
                        String name = getNameOfLatestInvalidRecord(reservation.getPersonId(), persons);
                        if (name != null) {
                            reservation.setPersonId(null);
                            reservation.setPersonName(name);
                            if (correctErrors) {
                                boatReservations.dataAccess.update(reservation);
                            }
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                                    "runAuditBoats(): "
                                    + International.getString("Reservierung") + " " + boat[0].getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                                    International.getString("Person"), name));
                            warnings++;
                        } else {
                            reservation.setPersonId(null);
                            if (correctErrors) {
                                boatReservations.dataAccess.update(reservation);
                            }
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                    "runAuditBoats(): "
                                    + International.getString("Reservierung") + " " + boat[0].getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                    International.getString("Person"),
                                    International.getString("Reserviert für")));
                            warnings++;
                        }
                    }
                }

                k = it.getNext();
            }

            it = boatDamages.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatDamageRecord damage = (BoatDamageRecord)boatDamages.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(damage.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                            "No Boat found for Boat Damage: " + damage.toString());
                    errors++;
                    if (correctErrors) {
                        boatStatus.dataAccess.delete(damage.getKey());
                    }
                    Logger.log(Logger.INFO, Logger.MSG_DATA_AUDIT_BOATINCONSISTENCY,
                            "Boat Damage " + damage.toString() + " deleted.");
                    infos++;
                    k = it.getNext();
                    continue;
                }

                // check References from BoatDamages
                boolean updated = false;
                if (persons.dataAccess.getNumberOfRecords() > 0) {
                    // run check only agains non-empty list (could be due to error opening list)
                    if (isReferenceInvalid(damage.getReportedByPersonId(), persons, -1)) {
                        String name = getNameOfLatestInvalidRecord(damage.getReportedByPersonId(), persons);
                        if (name != null) {
                            damage.setReportedByPersonId(null);
                            damage.setReportedByPersonName(name);
                            updated = true;
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                                    "runAuditBoats(): "
                                    + International.getString("Bootsschaden") + " " + boat[0].getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                                    International.getString("Person"), name));
                            warnings++;
                        } else {
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                    "runAuditBoats(): "
                                    + International.getString("Bootsschaden") + " " + boat[0].getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                    International.getString("Person"),
                                    International.getString("gemeldet von")));
                            damage.setReportedByPersonId(null);
                            updated = true;
                            warnings++;
                        }
                    }
                    if (isReferenceInvalid(damage.getFixedByPersonId(), persons, -1)) {
                        String name = getNameOfLatestInvalidRecord(damage.getFixedByPersonId(), persons);
                        if (name != null) {
                            damage.setFixedByPersonId(null);
                            damage.setFixedByPersonName(name);
                            updated = true;
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                                    "runAuditBoats(): "
                                    + International.getString("Bootsschaden") + " " + boat[0].getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                                    International.getString("Person"), name));
                            warnings++;
                        } else {
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                    "runAuditBoats(): "
                                    + International.getString("Bootsschaden") + " " + boat[0].getQualifiedName() + ": "
                                    + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                    International.getString("Person"),
                                    International.getString("behoben von")));
                            damage.setFixedByPersonId(null);
                            updated = true;
                        }
                    }
                    if (updated) {
                        if (correctErrors) {
                            boatDamages.dataAccess.update(damage);
                        }
                    }
                }

                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditCrews() {
        int errors = 0;
        try {
            Crews crews = project.getCrews(false);
            Persons persons = project.getPersons(false);
            if (persons.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            DataKeyIterator it = crews.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                CrewRecord crew = (CrewRecord)crews.data().get(k);
                boolean updated = false;
                for (int i=0; i<=LogbookRecord.CREW_MAX; i++) {
                    if (isReferenceInvalid(crew.getCrewId(i), persons, -1)) {
                        crew.setCrewId(i, null);
                        updated = true;
                    }
                }
                if (updated) {
                    if (correctErrors) {
                        crews.dataAccess.update(crew);
                    }
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditGroups() {
        int errors = 0;
        try {
            Groups groups = project.getGroups(false);
            Persons persons = project.getPersons(false);
            if (persons.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            DataKeyIterator it = groups.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                GroupRecord group = (GroupRecord) groups.data().get(k);
                DataTypeList<UUID> uuidList = group.getMemberIdList();
                boolean listChanged = false;
                for (int i = 0; uuidList != null && i < uuidList.length(); i++) {
                    if (isReferenceInvalid(uuidList.get(i), persons, -1)) {
                        uuidList.remove(i--);
                        listChanged = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                "runAuditGroups(): "
                                + International.getString("Gruppe") + " " + group.getQualifiedName() + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                International.getString("Person"),
                                International.getString("Mitglieder") + " " + (i+1)));
                    }
                }
                if (listChanged) {
                    group.setMemberIdList(uuidList);
                    if (correctErrors) {
                        groups.dataAccess.update(group);
                    }
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditDestinations() {
        int errors = 0;
        try {
            Destinations destinations = project.getDestinations(false);
            Waters waters = project.getWaters(false);
            if (waters.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            DataKeyIterator it = destinations.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                DestinationRecord destination = (DestinationRecord) destinations.data().get(k);
                DataTypeList<UUID> uuidList = destination.getWatersIdList();
                boolean listChanged = false;
                for (int i = 0; uuidList != null && i < uuidList.length(); i++) {
                    if (isReferenceInvalid(uuidList.get(i), waters, -1)) {
                        uuidList.remove(i--);
                        listChanged = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                                "runAuditDestinations(): "
                                + International.getString("Ziel") + " " + destination.getQualifiedName() + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                                International.getString("Gewässer"),
                                International.getString("Gewässer") + " " + (i+1)));
                    }
                }
                if (listChanged) {
                    destination.setWatersIdList(uuidList);
                    if (correctErrors) {
                        destinations.dataAccess.update(destination);
                    }
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditPersons() {
        int errors = 0;
        try {
            Persons persons = project.getPersons(false);
            Boats boats = project.getBoats(false);
            Status status = project.getStatus(false);
            if (boats.dataAccess.getNumberOfRecords() == 0 ||
                status.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            DataKeyIterator it = persons.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                PersonRecord person = (PersonRecord) persons.data().get(k);
                boolean updated = false;
                if (isReferenceInvalid(person.getStatusId(), status, -1)) {
                    person.setStatusId(status.getStatusOther().getId());
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                            "runAuditPersons(): "
                            + International.getString("Person") + " " + person.getQualifiedName() + ": "
                            + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                            International.getString("Status"),
                            status.getStatusOther().getQualifiedName()));
                }
                if (isReferenceInvalid(person.getDefaultBoatId(), boats, -1)) {
                    person.setDefaultBoatId(null);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                            "runAuditPersons(): "
                            + International.getString("Person") + " " + person.getQualifiedName() + ": "
                            + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                            International.getString("Boot"),
                            International.getString("Standard-Boot")));
                }
                if (updated) {
                    if (correctErrors) {
                        persons.dataAccess.update(person);
                    }
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditFahrtenabzeichen() {
        int errors = 0;
        try {
            Fahrtenabzeichen fahrtenabzeichen = project.getFahrtenabzeichen(false);
            Persons persons = project.getPersons(false);
            if (persons.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            DataKeyIterator it = fahrtenabzeichen.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                FahrtenabzeichenRecord abzeichen = (FahrtenabzeichenRecord) fahrtenabzeichen.data().get(k);
                if (persons.dataAccess.getValidLatest(PersonRecord.getKey(abzeichen.getPersonId(), -1)) == null) {
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_RECNOTFOUND,
                            "runAuditFahrtenabzeichen(): Keine Person zu Fahrtenabzeichen gefunden: " + abzeichen.toString());
                    if (correctErrors) {
                        fahrtenabzeichen.dataAccess.delete(abzeichen.getKey());
                    }
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_RECNOTFOUND,
                            "runAuditFahrtenabzeichen(): Fahrtenabzeichen " + abzeichen.toString() + " gelöscht.");
                    errors++;
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditMessages() {
        int errors = 0;
        try {
            Messages messages = project.getMessages(false);
            if (messages.data().getStorageType() == IDataAccess.TYPE_FILE_XML) {
                long size = ((DataFile)messages.data()).getFileSize();
                if (size > MAX_MESSAGES_FILESIZE) {
                    Logger.log(Logger.INFO,Logger.MSG_DATA_FILESIZEHIGH,
                            International.getMessage("Nachrichtendatei hat maximale Dateigröße überschritten. Derzeitige Größe: {size} byte", size));
                    infos++;
                    Messages archived = new Messages(messages.data().getStorageType(),
                                                     Daten.efaBakDirectory,
                                                     null, null,
                                                     "messages_"+EfaUtil.getCurrentTimeStampYYYYMMDD_HHMMSS());
                    archived.open(true);
                    long lock = messages.data().acquireGlobalLock();
                    int cntRead = 0;
                    int cntUnread = 0;
                    int cntMoved = 0;
                    try {
                        DataKeyIterator it = messages.data().getStaticIterator();
                        DataKey k = it.getFirst();
                        while (k != null) {
                            MessageRecord r = (MessageRecord) messages.data().get(k);
                            if (r.getRead()) {
                                cntRead++;
                                try {
                                    archived.data().add(r);
                                    messages.data().delete(k, lock);
                                    cntMoved++;
                                } catch (Exception e1) {
                                    Logger.log(e1);
                                }
                            } else {
                                cntUnread++;
                            }
                            k = it.getNext();
                        }
                        archived.close();
                        Logger.log(Logger.INFO,Logger.MSG_DATA_FILEARCHIVED,
                                International.getMessage("{count} gelesene Nachrichten wurden erfolgreich in die Archivdatei {filename} verschoben.",
                                cntMoved, ((DataFile)archived.data()).filename));
                        infos++;
                    } finally {
                        messages.data().releaseGlobalLock(lock);
                    }
                    ((DataFile) messages.data()).flush();
                    size = ((DataFile) messages.data()).getFileSize();
                    if (size > MAX_MESSAGES_FILESIZE / 2) {
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_FILESIZEHIGH,
                                International.getMessage("Nachrichtendatei ist nach Archivierung gelesener Nachrichten noch immer groß. Derzeitige Größe: {size} byte", size));
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_FILESIZEHIGH,
                                International.getMessage("Es gibt {count} ungelesene Nachrichten. Bitte lies die Nachrichten und markiere sie als gelesen.", cntUnread));
                    }
                }
            }
            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditMessages() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditStatistics() {
        int errors = 0;
        try {
            Statistics statistics = project.getStatistics(false);
            Hashtable<Integer,StatisticsRecord> hash = new Hashtable<Integer,StatisticsRecord>();
            DataKeyIterator it = statistics.dataAccess.getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                StatisticsRecord r = (StatisticsRecord)statistics.dataAccess.get(k);
                if ((r.getFilterBoatOwner() == null || r.getFilterBoatOwner().length() == 0) &&
                     !r.getFilterBoatOwnerAll()) {
                    r.setFilterBoatOwnerAll(true);
                    statistics.dataAccess.update(r);
                }
                hash.put(r.getPosition(), r);
                k = it.getNext();
            }
            Integer[] positions = hash.keySet().toArray(new Integer[0]);
            Arrays.sort(positions);
            boolean needsReordering = false;
            int expectedPos = 1;
            for (int i = 0; i<positions.length; i++) {
                if (positions[i] != expectedPos++) {
                    needsReordering = true;
                }
            }
            if (needsReordering && correctErrors) {
                // we should be locking, but well.. so what. This is just a cleanup at startup that
                // shouldn't happen anyhow
                try {
                    statistics.dataAccess.truncateAllData();
                    expectedPos = 1;
                    for (int i=0; i<positions.length; i++) {
                        StatisticsRecord r = hash.get(positions[i]);
                        r.setPosition(expectedPos++);
                        statistics.dataAccess.add(r);
                    }
                } catch(Exception e) {
                    Logger.logdebug(e);
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                            "runAuditStatistics() Caught Exception: " + e.toString());
                    errors++;
                }
            }
            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditStatistics() Caught Exception: " + e.toString());
            return ++errors;
        }
    }
    private int runAuditLogbook(String logbookName) {
        int errors = 0;
        try {
            Boats boats = project.getBoats(false);
            Persons persons = project.getPersons(false);
            Destinations destinations = project.getDestinations(false);
            ProjectRecord prjLogkoobRec = project.getLoogbookRecord(logbookName);
            SessionGroups sessionGroups = project.getSessionGroups(false);
            BoatStatus boatStatus = project.getBoatStatus(false);
            if (boats.dataAccess.getNumberOfRecords() == 0 ||
                persons.dataAccess.getNumberOfRecords() == 0 ||
                destinations.dataAccess.getNumberOfRecords() == 0) {
                return errors; // don't run check agains empty list (could be due to error opening list)
            }
            UUID id;

            boolean wasLogbookOpen = project.isLogbookOpen(logbookName);
            Logbook logbook = project.getLogbook(logbookName, false);
            DataKeyIterator it = logbook.dataAccess.getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                LogbookRecord r = (LogbookRecord)logbook.dataAccess.get(k);
                long validAt = r.getValidAtTimestamp();
                boolean updated = false;

                // Dates
                if (r.getDate() == null && !r.getDate().isSet()) {
                    Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_LOGBOOKERROR,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + "No Date set.");
                    errors++;
                } else {
                    if (!r.getDate().isInRange(prjLogkoobRec.getStartDate(), prjLogkoobRec.getEndDate())) {
                        Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_LOGBOOKERROR,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + "Date " + r.getDate().toString() + " is not within defined range for this logbook (" +
                                prjLogkoobRec.getStartDate() + " - " + prjLogkoobRec.getEndDate() + ").");
                        errors++;
                    }
                }
                
                // Boat References
                if (r.getBoatId() != null && r.getBoatName() != null && r.getBoatName().length() > 0
                        && !isReferenceInvalid(r.getBoatId(), boats, validAt)) {
                    String name = r.getBoatName();
                    r.setBoatName(null);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_TEXTTOREF,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("{item} '{name}' durch Referenz zu Datensatz '{name}' ersetzt.",
                            International.getString("Boot"), name + " [redundant]",
                            boats.getBoat(r.getBoatId(), validAt).getQualifiedName()));
                }
                if (isReferenceInvalid(r.getBoatId(), boats, validAt)) {
                    String name = getNameOfLatestInvalidRecord(r.getBoatId(), boats);
                    if (name == null) {
                        name = r.getBoatName(); // shouldn't be set, but we can at least try
                    }
                    if (name != null) {
                        r.setBoatName(name);
                        r.setBoatId(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                                International.getString("Boot"), name));
                    } else {
                        Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_INVALIDREFFOUND,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gefunden.",
                                International.getString("Boot"),
                                International.getString("Boot")
                                ));
                        errors++;
                    }
                } else if ( (id = findValidReference(r.getBoatName(), boats, validAt)) != null) {
                    String name = r.getBoatName();
                    r.setBoatId(id);
                    r.setBoatName(null);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_TEXTTOREF,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("{item} '{name}' durch Referenz zu Datensatz '{name}' ersetzt.",
                            International.getString("Boot"), name + " [" + International.getString("unbekannt") + "]",
                            boats.getBoat(id, validAt).getQualifiedName()));
                }

                // Persons
                for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                    if (r.getCrewId(i) != null && r.getCrewName(i) != null && r.getCrewName(i).length() > 0
                            && !isReferenceInvalid(r.getCrewId(i), persons, validAt)) {
                        String name = r.getCrewName(i);
                        r.setCrewName(i, null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_TEXTTOREF,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + International.getMessage("{item} '{name}' durch Referenz zu Datensatz '{name}' ersetzt.",
                                International.getString("Person"), name + " [redundant]",
                                persons.getPerson(r.getCrewId(i), validAt).getQualifiedName()));
                    }
                    if (isReferenceInvalid(r.getCrewId(i), persons, validAt)) {
                        String name = getNameOfLatestInvalidRecord(r.getCrewId(i), persons);
                        if (name != null) {
                            r.setCrewName(i, name);
                            r.setCrewId(i, null);
                            updated = true;
                            Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                                    "runAuditLogbook(): "
                                    + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                    + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                    + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                                    International.getString("Person"), name));
                        } else {
                            Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_INVALIDREFFOUND,
                                    "runAuditLogbook(): "
                                    + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                    + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                    + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gefunden.",
                                    International.getString("Person"),
                                    (i == 0 ? International.getString("Steuermann") :
                                              International.getString("Mannschaft") + " " + i)));
                            errors++;
                        }
                    } else if ((id = findValidReference(r.getCrewName(i), persons, validAt)) != null) {
                        String name = r.getCrewName(i);
                        r.setCrewId(i, id);
                        r.setCrewName(i, null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_TEXTTOREF,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + International.getMessage("{item} '{name}' durch Referenz zu Datensatz '{name}' ersetzt.",
                                International.getString("Person"), name + " [" + International.getString("unbekannt") + "]",
                                persons.getPerson(id, validAt).getQualifiedName()));
                    }
                }

                // Destination Reference
                if (r.getDestinationId() != null && r.getDestinationName() != null && r.getDestinationName().length() > 0
                        && !isReferenceInvalid(r.getDestinationId(), destinations, validAt)) {
                    String name = r.getDestinationName();
                    r.setDestinationName(null);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_TEXTTOREF,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("{item} '{name}' durch Referenz zu Datensatz '{name}' ersetzt.",
                            International.getString("Ziel"), name + " [redundant]",
                            destinations.getDestination(r.getDestinationId(), validAt).getQualifiedName()));
                }
                if (isReferenceInvalid(r.getDestinationId(), destinations, validAt)) {
                    String name = getNameOfLatestInvalidRecord(r.getDestinationId(), destinations);
                    if (name != null) {
                        r.setDestinationName(name);
                        r.setDestinationId(null);
                        updated = true;
                        Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                                International.getString("Ziel"), name));
                    } else {
                        Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_INVALIDREFFOUND,
                                "runAuditLogbook(): "
                                + International.getString("Fahrtenbuch") + " " + logbookName + " "
                                + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                                + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gefunden.",
                                International.getString("Ziel"),
                                International.getString("Ziel")
                                ));
                        errors++;
                    }
                } else if ( (id = findValidReference(r.getDestinationName(), destinations, validAt)) != null) {
                    String name = r.getDestinationName();
                    r.setDestinationId(id);
                    r.setDestinationName(null);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_TEXTTOREF,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("{item} '{name}' durch Referenz zu Datensatz '{name}' ersetzt.",
                            International.getString("Ziel"), name + " [" + International.getString("unbekannt") + "]",
                            destinations.getDestination(id, validAt).getQualifiedName()));
                }

                // Session Type
                if (r.getSessionType() == null ||
                    !Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_SESSION, r.getSessionType())) {
                    r.setSessionType(EfaTypes.TYPE_SESSION_NORMAL);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_REFTOTEXT,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("Ungültige Referenz für {item} durch '{name}' ersetzt.",
                            International.getString("Fahrtart"),
                            Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_NORMAL)));
                }

                // SessionGroup
                if (isReferenceInvalid(r.getSessionGroupId(), sessionGroups, -1) &&
                    sessionGroups.dataAccess.getNumberOfRecords() > 0) {
                    Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_INVALIDREFFOUND,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gefunden.",
                            International.getString("Fahrtgruppe"),
                            International.getString("Fahrtgruppe")));
                    errors++;
                }

                // Open Session?
                if (r.getSessionIsOpen() &&
                        boatStatus.getBoatStatus(logbookName, r.getEntryId()) == null) {
                    r.setSessionIsOpen(false);
                    updated = true;
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_INVALIDREFDELETED,
                            "runAuditLogbook(): "
                            + International.getString("Fahrtenbuch") + " " + logbookName + " "
                            + International.getMessage("Fahrtenbucheintrag #{entryno}", r.getEntryId().toString()) + ": "
                            + International.getMessage("Ungültige Referenz für {item} in Feld '{fieldname}' gelöscht.",
                            International.getString("Bootsstatus"),
                            International.getString("Fahrt offen (Boot unterwegs)")));
                }

                if (updated) {
                    if (correctErrors) {
                        logbook.dataAccess.update(r);
                    }
                }

                k = it.getNext();
            }

            if (!wasLogbookOpen && logbook != project.getCurrentLogbook()) {
                Logger.log(Logger.DEBUG, Logger.MSG_DATA_AUDIT,
                        "runAuditLogbook("+logbookName+"): Closing Logbook after Audit.");
                logbook.close();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditLogbook("+logbookName+") Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    private int runAuditClubworks() {
        int errors = 0;
        String[] logbookNames = project.getAllLogbookNames();
        if (logbookNames != null) {
            for (String s : logbookNames) {
                if (Daten.project.getClubwork(s, false) == null) {
                    Clubwork c = Daten.project.getClubwork(s, true);
                    if (c != null) {
                        Logger.log(Logger.INFO, Logger.MSG_DATA_AUDIT_OBJECTCREATIONFAILED,
                                "runAuditClubworks(): " +
                                LogString.fileNewCreated(s, International.getString("Vereinsarbeit")));
                    } else {
                        Logger.log(Logger.ERROR, Logger.MSG_DATA_AUDIT_OBJECTCREATIONFAILED,
                                "runAuditClubworks(): " +
                                LogString.fileCreationFailed(s, International.getString("Vereinsarbeit")));
                    }
                }
            }
        }
        return errors;
    }

    private int runAuditPurgeDeletedRecords(StorageObject so, String itemDescription) {
        int errors = 0;
        long now = System.currentTimeMillis();
        long purgeAfter = 0; /*( Daten.efaConfig.getValueDataDeletedRecordPurgeDays() < 0 ||
                            Daten.efaConfig.getValueDataDeletedRecordPurgeDays() == Long.MAX_VALUE ?
                                Long.MAX_VALUE : 
                                Math.abs(Daten.efaConfig.getValueDataDeletedRecordPurgeDays() * 24*60*60 ));*/
        try {
            DataKeyIterator it = so.dataAccess.getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                DataRecord r = (DataRecord)so.dataAccess.get(k);
                if (r != null && r.getDeleted() && r.getLastModified() > 0 &&
                    now - r.getLastModified() >= purgeAfter) {
                    if (correctErrors) {
                        so.dataAccess.delete(k);
                    }
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_AUDIT_RECPURGED,
                            "runAuditPurgeDeletedRecords(): "
                            + International.getMessage("{item} '{name}' endgültig gelöscht.",
                            itemDescription, r.getQualifiedName()));
                }
                k = it.getNext();
            }
            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAuditPurgeDeletedRecords() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    public boolean runAudit() {
        if (project == null || project.isInOpeningProject() || !project.isOpen() ||
                project.getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE) {
            return true;
        }
        warnings = 0;
        infos = 0;
        Logger.log(Logger.DEBUG,Logger.MSG_DATA_AUDIT,
                "Starting Project Audit for Project: " + project.getProjectName());
        int errors = 0;
        try {
            errors += runAuditPersistence(project.getSessionGroups(false), SessionGroups.DATATYPE);
            errors += runAuditPersistence(project.getPersons(false), Persons.DATATYPE);
            errors += runAuditPersistence(project.getStatus(false), Status.DATATYPE);
            errors += runAuditPersistence(project.getGroups(false), Groups.DATATYPE);
            errors += runAuditPersistence(project.getFahrtenabzeichen(false), Fahrtenabzeichen.DATATYPE);
            errors += runAuditPersistence(project.getBoats(false), Boats.DATATYPE);
            errors += runAuditPersistence(project.getCrews(false), Crews.DATATYPE);
            errors += runAuditPersistence(project.getBoatStatus(false), BoatStatus.DATATYPE);
            errors += runAuditPersistence(project.getBoatReservations(false), BoatReservations.DATATYPE);
            errors += runAuditPersistence(project.getBoatDamages(false), BoatDamages.DATATYPE);
            errors += runAuditPersistence(project.getDestinations(false), Destinations.DATATYPE);
            errors += runAuditPersistence(project.getWaters(false), Waters.DATATYPE);
            errors += runAuditPersistence(project.getMessages(false), Messages.DATATYPE);

            errors += runAuditBoats();
            errors += runAuditCrews();
            errors += runAuditGroups();
            errors += runAuditDestinations();
            errors += runAuditPersons();
            errors += runAuditFahrtenabzeichen();
            errors += runAuditMessages();
            errors += runAuditStatistics();
            String[] logbookNames = project.getAllLogbookNames();
            for (int i=0; logbookNames != null && i<logbookNames.length; i++) {
                errors += runAuditLogbook(logbookNames[i]);
            }
            if (errors == 0) {
                errors += runAuditPurgeDeletedRecords(project.getBoats(false),
                        International.getString("Boot"));
                errors += runAuditPurgeDeletedRecords(project.getPersons(false),
                        International.getString("Person"));
                errors += runAuditPurgeDeletedRecords(project.getDestinations(false),
                        International.getString("Ziel"));
                errors += runAuditPurgeDeletedRecords(project.getGroups(false),
                        International.getString("Gruppe"));
            }
            errors += runAuditClubworks();
        } catch(Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_AUDIT,
                    "runAudit() Caught Exception: " + e.toString());
            errors++;
        }
        boolean logEnd = (errors > 0 || warnings > 0 || infos > 0);
        Logger.log( (errors == 0 ? (logEnd ? Logger.INFO : Logger.DEBUG) : Logger.ERROR),
                Logger.MSG_DATA_AUDIT,
                "Project Audit completed with " + errors + " Errors, " + warnings+ " Warnings and "+
                infos +" Infos.");
        return errors == 0;
    }

    public void run() {
        runAudit();
    }

}
