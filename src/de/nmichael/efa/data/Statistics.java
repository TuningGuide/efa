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

public class Statistics extends StorageObject {

    public static final String DATATYPE = "efa2statistics";

    public Statistics(int storageType,
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.getString("Statistiken"));
        StatisticsRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new StatisticsRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public StatisticsRecord createStatisticsRecord(UUID id) {
        StatisticsRecord r = new StatisticsRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        r.setDefaults();
        return r;
    }

    public StatisticsRecord getStatistics(UUID id) {
        try {
            return (StatisticsRecord)data().get(StatisticsRecord.getKey(id));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public StatisticsRecord findStatisticsByName(String name) {
        try {
            DataKey[] keys = data().getByFields(new String[] { StatisticsRecord.NAME }, new String[] { name });
            if (keys != null && keys.length > 0) {
                return (StatisticsRecord) data().get(keys[0]);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, StatisticsRecord.ID);
            assertFieldNotEmpty(record, StatisticsRecord.NAME);
            assertUnique(record, StatisticsRecord.NAME);
            assertUnique(record, StatisticsRecord.POSITION);
        }
    }

}
