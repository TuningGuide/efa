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

public class Groups extends Persistence {

    public static final String DATATYPE = "efa2groups";

    public Groups(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Gruppen"));
        GroupRecord.initialize();
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

}
