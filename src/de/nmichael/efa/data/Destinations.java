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
import java.util.*;

// @i18n complete

public class Destinations extends Persistence {

    public static final String DATATYPE = "efa2destinations";
    public DestinationRecord staticDestinationRecord;

    public Destinations(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Ziele"));
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
            return (DestinationRecord)data().get(keys[0]);
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

}
