/* Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.data.*;
import de.nmichael.efa.util.Logger;
import java.util.Hashtable;
import java.util.UUID;

public class Audit extends Thread {
    
    private Project project;

    public Audit(Project project) {
        this.project = project;
    }

    private int runAuditPersistence(StorageObject p, String dataType) {
        if (p != null && p.isOpen()) {
            Logger.log(Logger.INFO, Logger.MSG_DATA_PROJECTCHECK, dataType + " open (" + p.toString() + ")");
            return 0;
        } else {
            Logger.log(Logger.ERROR, Logger.MSG_DATA_PROJECTCHECK, dataType + " not open");
            return 1;
        }
    }

    private int runAuditBoats() {
        int errors = 0;
        try {
            Boats boats = project.getBoats(false);
            BoatStatus boatStatus = project.getBoatStatus(false);
            BoatReservations boatReservations = project.getBoatReservations(false);
            BoatDamages boatDamages = project.getBoatDamages(false);

            Hashtable<UUID,Integer> boatVersions = new Hashtable<UUID,Integer>();

            DataKeyIterator it = boats.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                BoatRecord boat = (BoatRecord)boats.data().get(k);
                if (boat.getId() == null ||
                        boat.getValidFrom() < 0 || boat.getInvalidFrom() < 0 ||
                        boat.getValidFrom() >= boat.getInvalidFrom()) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"Boat Record is invalid: " + boat.toString());
                    errors++;
                }
                Integer versions = boatVersions.get(boat.getId());
                if (versions == null) {
                    boatVersions.put(boat.getId(), 1);
                } else {
                    boatVersions.put(boat.getId(), versions.intValue() + 1);
                }

                BoatStatusRecord status = boatStatus.getBoatStatus(boat.getId());
                if (status == null) {
                    Logger.log(Logger.WARNING,Logger.MSG_DATA_PROJECTCHECK,"No Boat Status found for Boat "+boat.getQualifiedName()+": " + boat.toString());
                    boatStatus.data().add(boatStatus.createBoatStatusRecord(boat.getId(), ""));
                    Logger.log(Logger.INFO,Logger.MSG_DATA_PROJECTCHECK,"New Boat Status added for Boat "+boat.getQualifiedName());
                }

                k = it.getNext();
            }

            it = boatStatus.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatStatusRecord status = (BoatStatusRecord)boatStatus.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(status.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"No Boat found for Boat Status: " + status.toString());
                    errors++;
                }
                k = it.getNext();
            }

            it = boatReservations.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatReservationRecord reservation = (BoatReservationRecord)boatReservations.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(reservation.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"No Boat found for Boat Reservation: " + reservation.toString());
                    errors++;
                }
                k = it.getNext();
            }

            it = boatDamages.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatDamageRecord damage = (BoatDamageRecord)boatDamages.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(damage.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"No Boat found for Boat Damage: " + damage.toString());
                    errors++;
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    public boolean runAudit() {
        Logger.log(Logger.INFO,Logger.MSG_DATA_PROJECTCHECK,"Starting Project Audit for Project: " + project.getProjectName());
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
            errors += runAuditPersistence(project.getMessages(false), Messages.DATATYPE); // @todo (P2) make sure to truncate message file once in a while

            errors += runAuditBoats();
            // @todo (P3) AuditLogbook: check whether any name has a matching ID and replace by ID; also, check for deleted entries
        } catch(Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"runAudit() Caught Exception: " + e.toString());
        }
        Logger.log( (errors == 0 ? Logger.INFO : Logger.ERROR) ,Logger.MSG_DATA_PROJECTCHECK,"Project Audit completed with " + errors + " Errors.");
        return errors == 0;
    }

    public void run() {
        runAudit();
    }

}
