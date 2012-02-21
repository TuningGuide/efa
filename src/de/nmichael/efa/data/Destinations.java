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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaModifyException;
import java.util.*;

// @i18n complete

public class Destinations extends StorageObject {

    public static final String DATATYPE = "efa2destinations";
    public DestinationRecord staticDestinationRecord;

    public Destinations(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE,
                International.getString("Ziele") + " / " +
                International.getString("Strecken"));
        DestinationRecord.initialize();
        staticDestinationRecord = (DestinationRecord)createNewRecord();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new DestinationRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public DestinationRecord createDestinationRecord(UUID id) {
        DestinationRecord r = new DestinationRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public DestinationRecord getDestination(UUID id, long validAt) {
        try {
            return (DestinationRecord)data().getValidAt(DestinationRecord.getKey(id, validAt), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    // find a record being valid at the specified time
    public DestinationRecord getDestination(String destinationName, long validAt) {
        try {
            DataKey[] keys = data().getByFields(
                staticDestinationRecord.getQualifiedNameFields(), staticDestinationRecord.getQualifiedNameValues(destinationName), validAt);
            if (keys == null || keys.length < 1) {
                return null;
            }
            for (int i=0; i<keys.length; i++) {
                DestinationRecord r = (DestinationRecord)data().get(keys[i]);
                if (r.isValidAt(validAt)) {
                    return r;
                }
            }
            return null;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    // find any record being valid at least partially in the specified range
    public DestinationRecord getDestination(String destinationName, long validFrom, long validUntil, long preferredValidAt) {
        try {
            DataKey[] keys = data().getByFields(
                staticDestinationRecord.getQualifiedNameFields(), staticDestinationRecord.getQualifiedNameValues(destinationName));
            if (keys == null || keys.length < 1) {
                return null;
            }
            DestinationRecord candidate = null;
            for (int i=0; i<keys.length; i++) {
                DestinationRecord r = (DestinationRecord)data().get(keys[i]);
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

    public boolean isDestinationDeleted(UUID destinationId) {
        try {
            DataRecord[] records = data().getValidAny(DestinationRecord.getKey(destinationId, -1));
            if (records != null && records.length > 0) {
                return records[0].getDeleted();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, DestinationRecord.ID);
            assertFieldNotEmpty(record, DestinationRecord.NAME);
            if (Daten.efaConfig.getValueUseFunctionalityRowingBerlin() &&
                getProject().getClubAreaID() > 0) {
                DestinationRecord dr = ((DestinationRecord)record);
                if (dr.getStartIsBoathouse() && dr.getDestinationAreas() != null &&
                    dr.getDestinationAreas().findZielbereich(getProject().getClubAreaID()) >= 0) {
                    throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                            "Eigener Zielbereich "+getProject().getClubAreaID()+" bei Fahrten ab eigenem Bootshaus nicht erlaubt.",
                            Thread.currentThread().getStackTrace());

                }
            }
        }
        if (delete) {
            assertNotReferenced(record, getProject().getBoats(false), new String[] { BoatRecord.DEFAULTDESTINATIONID });
            String[] logbooks = getProject().getAllLogbookNames();
            for (int i=0; logbooks != null && i<logbooks.length; i++) {
                assertNotReferenced(record, getProject().getLogbook(logbooks[i], false), new String[] { LogbookRecord.DESTINATIONID } );
            }
        }
    }

}
