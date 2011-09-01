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

public class Groups extends StorageObject {

    public static final String DATATYPE = "efa2groups";
    public GroupRecord staticGroupRecord;

    public Groups(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.getString("Gruppen"));
        GroupRecord.initialize();
        staticGroupRecord = (GroupRecord)createNewRecord();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new GroupRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public GroupRecord createGroupRecord(UUID id) {
        GroupRecord r = new GroupRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public GroupRecord findGroupRecord(UUID id, long validAt) {
        try {
            return (GroupRecord)data().getValidAt(GroupRecord.getKey(id, -1), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public GroupRecord findGroupRecord(String groupName, long validAt) {
        try {
            DataKey[] keys = data().getByFields(
                staticGroupRecord.getQualifiedNameFields(), staticGroupRecord.getQualifiedNameValues(groupName), validAt);
            if (keys == null || keys.length < 1) {
                return null;
            }
            return (GroupRecord)data().get(keys[0]);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }


    public boolean isGroupDeleted(UUID groupId) {
        try {
            DataRecord[] records = data().getValidAny(GroupRecord.getKey(groupId, -1));
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
            assertFieldNotEmpty(record, GroupRecord.ID);
            assertFieldNotEmpty(record, GroupRecord.NAME);
            assertUnique(record, GroupRecord.NAME);
        }
        if (delete) {
            assertNotReferenced(record, getProject().getBoats(false), new String[] { BoatRecord.ALLOWEDGROUPIDLIST });
            assertNotReferenced(record, getProject().getBoats(false), new String[] { BoatRecord.REQUIREDGROUPID });
        }
    }

}
