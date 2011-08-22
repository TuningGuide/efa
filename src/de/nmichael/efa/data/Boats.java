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

import de.nmichael.efa.core.config.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.data.storage.*;
import java.util.*;

// @i18n complete

public class Boats extends Persistence {

    public static final String DATATYPE = "efa2boats";
    public BoatRecord staticBoatRecord;

    public Boats(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Boote"));
        BoatRecord.initialize();
        staticBoatRecord = (BoatRecord)createNewRecord();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatRecord createBoatRecord(UUID id) {
        BoatRecord r = new BoatRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public DataKey addNewBoatRecord(BoatRecord boat, long validFrom) throws EfaException {
        DataKey k = data().addValidAt(boat, validFrom);
        getProject().getBoatStatus(false).data().add(getProject().getBoatStatus(false).createBoatStatusRecord(boat.getId(), boat.getQualifiedName()));
        return k;
    }

    public BoatRecord getBoat(UUID id, long validAt) {
        try {
            return (BoatRecord)data().getValidAt(BoatRecord.getKey(id, validAt), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    // find a record being valid at the specified time
    public BoatRecord getBoat(String boatName, long validAt) {
        try {
            DataKey[] keys = data().getByFields(
                staticBoatRecord.getQualifiedNameFields(), staticBoatRecord.getQualifiedNameValues(boatName), validAt);
            if (keys == null || keys.length < 1) {
                return null;
            }
            return (BoatRecord)data().get(keys[0]);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }
    
    // find any record being valid at least partially in the specified range
    public BoatRecord getBoat(String boatName, long validFrom, long validUntil, long preferredValidAt) {
        try {
            DataKey[] keys = data().getByFields(
                    staticBoatRecord.getQualifiedNameFields(), staticBoatRecord.getQualifiedNameValues(boatName));
            if (keys == null || keys.length < 1) {
                return null;
            }
            BoatRecord candidate = null;
            for (int i=0; i<keys.length; i++) {
                BoatRecord r = (BoatRecord)data().get(keys[i]);
                if (r != null) {
                    if (r.isInValidityRange(validFrom, validUntil)) {
                        candidate = r;
                        if (preferredValidAt >= r.getValidFrom() && preferredValidAt < r.getInvalidFrom()) {
                            return r;
                        }
                    }
                }
            }
            return candidate;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public boolean isBoatDeleted(UUID boatId) {
        try {
            DataRecord[] records = data().getValidAny(BoatRecord.getKey(boatId, -1));
            if (records != null && records.length > 0) {
                return records[0].getDeleted();

            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public boolean isBoatInvisible(UUID boatId) {
        try {
            DataRecord[] records = data().getValidAny(BoatRecord.getKey(boatId, -1));
            if (records != null && records.length > 0) {
                return records[0].getInvisible();

            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public boolean isBoatDeletedOrInvisible(UUID boatId) {
        try {
            DataRecord[] records = data().getValidAny(BoatRecord.getKey(boatId, -1));
            if (records != null && records.length > 0) {
                return records[0].getDeleted() || records[0].getInvisible();

            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, BoatRecord.NAME);
        }
        if (delete) {
            assertNotReferenced(record, getProject().getBoatDamages(false), new String[] { BoatDamageRecord.BOATID } );
            assertNotReferenced(record, getProject().getBoatReservations(false), new String[] { BoatReservationRecord.BOATID } );
            assertNotReferenced(record, getProject().getBoatStatus(false), new String[] { BoatStatusRecord.BOATID } );
            String[] logbooks = getProject().getAllLogbookNames();
            for (int i=0; logbooks != null && i<logbooks.length; i++) {
                assertNotReferenced(record, getProject().getLogbook(logbooks[i], false), new String[] { LogbookRecord.BOATID } );
            }
        }
    }

}
