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

public class BoatTypes { //extends Persistence {
/*
    public static final String DATATYPE = "efa2boattypes";

    public BoatTypes(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Bootstypen"));
        BoatTypeRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatTypeRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatTypeRecord createBoatTypeRecord(UUID boatId, int variant) {
        BoatTypeRecord r = new BoatTypeRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(boatId);
        r.setVariant(variant);
        return r;
    }

    public BoatTypeRecord getBoatType(UUID id, int variant, long validAt) {
        try {
            return (BoatTypeRecord)data().getValidAt(BoatTypeRecord.getKey(id, variant, validAt), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public BoatTypeRecord[] getBoatTypes(UUID id, long validAt) {
        try {
            DataKey[] keys = data().getByFields(BoatTypeRecord.IDX_BOATID, new Object[] { id }, validAt);
            if (keys == null || keys.length == 0) {
                return null;
            }
            BoatTypeRecord[] recs = new BoatTypeRecord[keys.length];
            for (int i=0; i<keys.length; i++) {
                recs[i] = (BoatTypeRecord)data().get(keys[i]);
            }
            return recs;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }
*/
}
