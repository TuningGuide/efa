/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.data.storage.*;

// @i18n complete

public class Logbook extends Persistence {

    public Logbook(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, "e2lb");

        try {
            dataAccess.registerDataField("EntryNo", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("Date", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("Boat", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("BoatVariant", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("Cox", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("CrewList", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("CrewsHead", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("StartTime", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("EndTime", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("Destination", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("DistanceUnit", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("BoatDistance", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("CrewDistanceList", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("Comments", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("SessionType", IDataAccess.DATA_STRING);
            dataAccess.registerDataField("MultiDayTour", IDataAccess.DATA_STRING);
        } catch(Exception e) {

        }
    }

}
