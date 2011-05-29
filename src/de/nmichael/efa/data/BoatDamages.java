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

public class BoatDamages extends Persistence {

    public static final String DATATYPE = "efa2boatdamages";

    public BoatDamages(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Bootsschäden"));
        BoatDamageRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatDamageRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatDamageRecord createBoatDamageRecord(UUID id, int damage) {
        BoatDamageRecord r = new BoatDamageRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(id);
        r.setDamage(damage);
        return r;
    }

    public BoatDamageRecord[] getBoatDamages(UUID boatId) {
        try {
            DataKey[] keys = data().getByFields(BoatDamageRecord.IDX_BOATID, new Object[] { boatId });
            if (keys == null || keys.length == 0) {
                return null;
            }
            BoatDamageRecord[] recs = new BoatDamageRecord[keys.length];
            for (int i=0; i<keys.length; i++) {
                recs[i] = (BoatDamageRecord)data().get(keys[i]);
            }
            return recs;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

}
