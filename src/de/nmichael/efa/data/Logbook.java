/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
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

    public Logbook(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, "e2log", International.getString("Fahrtenbuch"));
        try {
            LogbookRecord.initialize();
            for (int i=0; i<LogbookRecord.getFieldCount(); i++) {
                dataAccess.registerDataField(LogbookRecord.getFieldName(i), LogbookRecord.getFieldType(i));
            }
            dataAccess.setKey(LogbookRecord.getKeyFields());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public DataRecord createNewRecord() {
        return new LogbookRecord();
    }

}
