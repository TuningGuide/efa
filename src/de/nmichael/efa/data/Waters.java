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

public class Waters extends Persistence {

    public static final String DATATYPE = "e2waters";

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

}
