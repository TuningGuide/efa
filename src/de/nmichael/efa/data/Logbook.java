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

// @i18n complete

public class Logbook extends Persistence {

    public static final String DATATYPE = "e2logbook";

    public Logbook(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Fahrtenbuch"));
        LogbookRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public LogbookRecord createLogbookRecord(String entryNo) {
        LogbookRecord r = new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
        r.setEntryId(entryNo);
        return r;
    }

}
