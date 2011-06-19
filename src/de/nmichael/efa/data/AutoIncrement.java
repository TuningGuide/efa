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

public class AutoIncrement extends Persistence {

    public static final String DATATYPE = "efa2autoincrement";

    public AutoIncrement(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, "AutoIncrement");
        AutoIncrementRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new AutoIncrementRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public AutoIncrementRecord createAutoIncrementRecord(String sequence) {
        AutoIncrementRecord r = new AutoIncrementRecord(this, MetaData.getMetaData(DATATYPE));
        r.setSequence(sequence);
        return r;
    }

    public int nextAutoIncrementValue(String sequence) {
        long lock = -1;
        try {
            DataKey k = AutoIncrementRecord.getKey(sequence);
            lock = data().acquireLocalLock(k);
            AutoIncrementRecord r = (AutoIncrementRecord)data().get(k);
            if (r != null) {
                int seq = r.getValue() + 1;
                r.setValue(seq);
                data().update(r, lock);
                return seq;
            } else {
                r = createAutoIncrementRecord(sequence);
                r.setValue(1);
                data().add(r, lock);
                return 1;
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        } finally {
            if (lock != -1) {
                data().releaseLocalLock(lock);
            }
        }
        return -1;
    }

}
