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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import java.util.*;

// @i18n complete

public class BoatStatus extends Persistence {

    public static final String DATATYPE = "efa2boatstatus";

    public BoatStatus(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Bootsstatus"));
        BoatStatusRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatStatusRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatStatusRecord createBoatStatusRecord(UUID id, String boatText) {
        BoatStatusRecord r = new BoatStatusRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(id);
        r.setBoatText(boatText);
        r.setBaseStatus(BoatStatusRecord.STATUS_AVAILABLE);
        r.setCurrentStatus(BoatStatusRecord.STATUS_AVAILABLE);
        return r;
    }

    public BoatStatusRecord getBoatStatus(UUID id) {
        try {
            return (BoatStatusRecord)data().get(BoatStatusRecord.getKey(id));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public Vector<BoatStatusRecord> getBoats(String status) {
        return getBoats(status, false);
    }

    /*
     * @param getBoatsForLists - if true, this will return boats not necessarily according
     * to their status, but rather which *list* they should appear in. It might be that
     * some boats which have status ONTHEWATER are supposed to be displayed as NOTAVAILABLE
     * and therefore returned for status=NOTAVAILABLE instead.
     */
    public Vector<BoatStatusRecord> getBoats(String status, boolean getBoatsForLists) {
        try {
            Vector<BoatStatusRecord> v = new Vector<BoatStatusRecord>();
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                BoatStatusRecord r = (BoatStatusRecord) data().get(k);
                if (r != null && !r.getDeletedOrInvisible()) {
                    String s = (getBoatsForLists ? r.getShowInList() : r.getCurrentStatus());
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
