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

public class SessionGroups extends Persistence {

    public static final String DATATYPE = "efa2sessiongroups";

    public SessionGroups(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Fahrtengruppen"));
        SessionGroupRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new SessionGroupRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public SessionGroupRecord createSessionGroupRecord(UUID id, String logbook) {
        SessionGroupRecord r = new SessionGroupRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        r.setLogbook(logbook);
        return r;
    }

    public SessionGroupRecord findSessionGroupRecord(UUID id) {
        try {
            return (SessionGroupRecord)data().get(SessionGroupRecord.getKey(id));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public DataKey[] findAllSessionGroupKeys(String logbookName) {
        try {
            return data().getByFields(SessionGroupRecord.IDX_LOGBOOK, new String[] { logbookName });
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public String getSessionGroupName(UUID id) {
        SessionGroupRecord r = findSessionGroupRecord(id);
        if (r != null) {
            return r.getName();
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, SessionGroupRecord.NAME);
            assertUnique(record, new String[] { SessionGroupRecord.NAME, SessionGroupRecord.LOGBOOK });
            assertFieldNotEmpty(record, SessionGroupRecord.LOGBOOK);
        }
        if (delete) {
            assertNotReferenced(record, getProject().getLogbook(((SessionGroupRecord)record).getLogbook(), false),
                    new String[] { LogbookRecord.SESSIONGROUPID });
        }
    }

}
