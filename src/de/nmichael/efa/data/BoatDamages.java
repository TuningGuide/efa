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

    public BoatDamageRecord createBoatDamageRecord(UUID id) {
        AutoIncrement autoIncrement = getProject().getAutoIncrement(false);
        int val = autoIncrement.nextAutoIncrementIntValue(data().getStorageObjectType());
        if (val > 0) {
            return createBoatDamageRecord(id, val);
        }
        return null;
    }

    public BoatDamageRecord createBoatDamageRecord(UUID id, int damage) {
        BoatDamageRecord r = new BoatDamageRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(id);
        r.setDamage(damage);
        return r;
    }

    public BoatDamageRecord[] getBoatDamages(UUID boatId) {
        return getBoatDamages(boatId, false, false);
    }

    public BoatDamageRecord[] getBoatDamages(UUID boatId, boolean onlyOpenDamages, boolean mostSevereFirst) {
        try {
            DataKey[] keys = data().getByFields(BoatDamageRecord.IDX_BOATID, new Object[] { boatId });
            if (keys == null || keys.length == 0) {
                return null;
            }

            Vector<BoatDamageRecord> damages = new Vector<BoatDamageRecord>();
            for (int severe=2; severe>=0; severe--) {
                for (int i=0; i<keys.length; i++) {
                    BoatDamageRecord r = (BoatDamageRecord)data().get(keys[i]);
                    if (!mostSevereFirst ||
                        (severe == 2 && BoatDamageRecord.SEVERITY_NOTUSEABLE.equals(r.getSeverity())) ||
                        (severe == 1 && BoatDamageRecord.SEVERITY_LIMITEDUSEABLE.equals(r.getSeverity())) ||
                        (severe == 0 && !BoatDamageRecord.SEVERITY_NOTUSEABLE.equals(r.getSeverity()) && !BoatDamageRecord.SEVERITY_LIMITEDUSEABLE.equals(r.getSeverity())) ) {
                        if (onlyOpenDamages == false || !r.getFixed()) {
                            damages.add(r);
                        }
                    }
                }
                if (!mostSevereFirst) {
                    break; // stop after 1st iteration
                }
            }

            BoatDamageRecord[] recs = new BoatDamageRecord[damages.size()];
            for (int i=0; i<recs.length; i++) {
                recs[i] = damages.get(i);
            }
            return recs;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

}
