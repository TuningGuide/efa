/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaModifyException;
import java.util.*;

// @i18n complete

public class BoatReservations extends StorageObject {

    public static final String DATATYPE = "efa2boatreservations";

    public BoatReservations(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.getString("Bootsreservierungen"));
        BoatReservationRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatReservationRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatReservationRecord createBoatReservationsRecord(UUID id) {
        AutoIncrement autoIncrement = getProject().getAutoIncrement(false);
        int val = autoIncrement.nextAutoIncrementIntValue(data().getStorageObjectType());
        if (val > 0) {
            return createBoatReservationsRecord(id, val);
        }
        return null;
    }

    public BoatReservationRecord createBoatReservationsRecord(UUID id, int reservation) {
        BoatReservationRecord r = new BoatReservationRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(id);
        r.setReservation(reservation);
        return r;
    }

    public BoatReservationRecord[] getBoatReservations(UUID boatId) {
        try {
            DataKey[] keys = data().getByFields(BoatReservationRecord.IDX_BOATID, new Object[] { boatId });
            if (keys == null || keys.length == 0) {
                return null;
            }
            BoatReservationRecord[] recs = new BoatReservationRecord[keys.length];
            for (int i=0; i<keys.length; i++) {
                recs[i] = (BoatReservationRecord)data().get(keys[i]);
            }
            return recs;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public BoatReservationRecord[] getBoatReservations(UUID boatId, long now, long lookAheadMinutes) {
        BoatReservationRecord[] reservations = getBoatReservations(boatId);

        Vector<BoatReservationRecord> activeReservations = new Vector<BoatReservationRecord>();
        for (int i = 0; reservations != null && i < reservations.length; i++) {
            BoatReservationRecord r = reservations[i];
            if (r.getReservationValidInMinutes(now, lookAheadMinutes) >= 0) {
                activeReservations.add(r);
            }
        }

        if (activeReservations.size() == 0) {
            return null;
        }
        BoatReservationRecord[] a = new BoatReservationRecord[activeReservations.size()];
        for (int i=0; i<a.length; i++) {
            a[i] = activeReservations.get(i);
        }
        return a;
    }

    public int purgeObsoleteReservations(UUID boatId, long now) {
        BoatReservationRecord[] reservations = getBoatReservations(boatId);
        int purged = 0;

        for (int i = 0; reservations != null && i < reservations.length; i++) {
            BoatReservationRecord r = reservations[i];
            if (r.isObsolete(now)) {
                try {
                    data().delete(r.getKey());
                    purged++;
                } catch(Exception e) {
                    Logger.log(e);
                }
            }
        }
        return purged;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, BoatReservationRecord.BOATID);
            assertFieldNotEmpty(record, BoatReservationRecord.RESERVATION);
        }
    }

}
