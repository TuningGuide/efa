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

public class Boats extends Persistence {

    public static final String DATATYPE = "e2boats";

    public Boats(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Boote"));
        try {
            BoatRecord.initialize();
            MetaData meta = MetaData.getMetaData(DATATYPE);
            for (int i=0; i<meta.getNumberOfFields(); i++) {
                dataAccess.registerDataField(meta.getFieldName(i), meta.getFieldType(i));
            }
            dataAccess.setKey(meta.getKeyFields());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public DataRecord createNewRecord() {
        return BoatRecord.createBoatRecord();
    }

}
