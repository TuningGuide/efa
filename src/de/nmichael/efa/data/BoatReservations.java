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

    public static final String DATATYPE = "e2boatreservations";

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

}
