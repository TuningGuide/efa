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
import de.nmichael.efa.data.types.*;

// @i18n complete

public class Logbook extends Persistence {

    public static final String DATATYPE = "efa2logbook";

    private String name;
    private ProjectRecord projectRecord;

    public Logbook(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Fahrtenbuch"));
        LogbookRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProjectRecord(ProjectRecord r) {
        this.projectRecord = r;
    }

    public DataTypeDate getStartDate() {
        return (projectRecord != null ? projectRecord.getStartDate() : null);
    }

    public DataTypeDate getEndDate() {
        return (projectRecord != null ? projectRecord.getEndDate() : null);
    }

    public LogbookRecord createLogbookRecord(DataTypeIntString entryNo) {
        LogbookRecord r = new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
        r.setEntryId(entryNo);
        return r;
    }

    public LogbookRecord getLogbookRecord(DataTypeIntString entryNo) {
        return getLogbookRecord(LogbookRecord.getKey(entryNo));
    }

    public LogbookRecord getLogbookRecord(DataKey key) {
        try {
            return (LogbookRecord)(data().get(key));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

}
