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

public class Boats extends Persistence {

    public static final String DATATYPE = "e2boats";

    public Boats(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Boote"));
        BoatRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatRecord createBoatRecord(UUID id) {
        BoatRecord r = new BoatRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public BoatRecord getBoat(UUID id, long validAt) {
        try {
            return (BoatRecord)data().getValidAt(BoatRecord.getKey(id, validAt), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public BoatRecord getBoat(String boatName, long validAt) {
        try {
            DataKey[] keys = data().getByFields(
                BoatRecord.IDX_NAME_OWNER, BoatRecord.getValuesForIndexFromQualifiedName(boatName), validAt);
            if (keys == null || keys.length < 1) {
                return null;
            }
            return (BoatRecord)data().get(keys[0]);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }
    
}
