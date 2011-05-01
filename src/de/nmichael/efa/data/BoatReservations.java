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

public class BoatReservations extends Persistence {

    public static final String DATATYPE = "efa2boatreservations";

    public BoatReservations(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Bootsreservierungen"));
        BoatReservationRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new BoatReservationRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public BoatReservationRecord createBoatReservationsRecord(UUID id, int reservation) {
        BoatReservationRecord r = new BoatReservationRecord(this, MetaData.getMetaData(DATATYPE));
        r.setBoatId(id);
        r.setReservation(reservation);
        return r;
    }

    public BoatReservationRecord[] getBoatReservations(UUID boatId) {
        try {
            DataKey[] keys = data().getByFields(BoatReservationRecord.IDX_BOATID, new Object[] { boatId });
            if (keys == null || keys.length == 0) {
                return null;
            }
            BoatReservationRecord[] recs = new BoatReservationRecord[keys.length];
            for (int i=0; i<keys.length; i++) {
                recs[i] = (BoatReservationRecord)data().get(keys[i]);
            }
            return recs;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

}
