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
import de.nmichael.efa.ex.*;
import java.util.*;

// @i18n complete

public class Waters extends Persistence {

    public static final String DATATYPE = "efa2waters";

    public Waters(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Gewässer"));
        WatersRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new WatersRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public WatersRecord createWatersRecord(UUID id) {
        WatersRecord r = new WatersRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public WatersRecord getWaters(UUID id) {
        try {
            return (WatersRecord)data().get(WatersRecord.getKey(id));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public WatersRecord findWatersByName(String name) {
        try {
            DataKey[] keys = data().getByFields(new String[] { WatersRecord.NAME }, new String[] { name });
            if (keys != null && keys.length > 0) {
                return (WatersRecord) data().get(keys[0]);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, WatersRecord.NAME);
            assertUnique(record, WatersRecord.NAME);
        }
        if (delete) {
            assertNotReferenced(record, getProject().getDestinations(false), new String[] { DestinationRecord.WATERSIDLIST });
        }
    }

}
