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

public class BoatStatus extends Persistence {

    public static final String DATATYPE = "e2boatstatus";

    public BoatStatus(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Bootsstatus"));
        BoatStatusRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatStatusRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatStatusRecord createBoatStatusRecord(UUID id) {
        BoatStatusRecord r = new BoatStatusRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(id);
        return r;
    }

    public Vector<BoatStatusRecord> getBoats(String status) {
        try {
            Vector<BoatStatusRecord> v = new Vector<BoatStatusRecord>();
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                BoatStatusRecord r = (BoatStatusRecord) data().get(k);
                if (r != null) {
                    String s = r.getStatus();
                    if (s != null && s.equals(status)) {
                        v.add(r);
                    }
                }
                k = it.getNext();
            }
            return v;
        } catch (Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public boolean areBoatsOutOnTheWater() {
        Vector v = getBoats(BoatStatusRecord.STATUS_ONTHEWATER);
        return (v != null && v.size() > 0);
    }

}
