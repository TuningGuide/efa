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
import java.util.*;

// @i18n complete

public class LogbookRecord extends DataRecord {

    public static String AA = "AA";

    private static final String ENTRYNO          = "EntryNo";
    private static final String DATE             = "Date";
    private static final String BOAT             = "Boat";
    private static final String BOATVARIANT      = "BoatVariant";
    private static final String COX              = "Cox";
    private static final String CREWLIST         = "CrewList";
    private static final String CREWSHEAD        = "CrewsHead";
    private static final String STARTTIME        = "StartTime";
    private static final String ENDTIME          = "EndTime";
    private static final String DESTINATION      = "Destination";
    private static final String DISTANCEUNIT     = "DistanceUnit";
    private static final String BOATDISTANCE     ="BoatDistance";
    private static final String CREWDISTANCELIST = "CrewDistanceList";
    private static final String COMMENTS         = "Comments";
    private static final String SESSIONTYPE      = "SessionType";
    private static final String MULTIDAYTOUR     = "MultiDayTour";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ENTRYNO);             t.add(IDataAccess.DATA_STRING);
        f.add(DATE);                t.add(IDataAccess.DATA_STRING);
        f.add(BOAT);                t.add(IDataAccess.DATA_STRING);
        f.add(BOATVARIANT);         t.add(IDataAccess.DATA_STRING);
        f.add(COX);                 t.add(IDataAccess.DATA_STRING);
        f.add(CREWLIST);            t.add(IDataAccess.DATA_STRING);
        f.add(CREWSHEAD);           t.add(IDataAccess.DATA_STRING);
        f.add(STARTTIME);           t.add(IDataAccess.DATA_STRING);
        f.add(ENDTIME);             t.add(IDataAccess.DATA_STRING);
        f.add(DESTINATION);         t.add(IDataAccess.DATA_STRING);
        f.add(DISTANCEUNIT);        t.add(IDataAccess.DATA_STRING);
        f.add(BOATDISTANCE);        t.add(IDataAccess.DATA_STRING);
        f.add(CREWDISTANCELIST);    t.add(IDataAccess.DATA_STRING);
        f.add(COMMENTS);            t.add(IDataAccess.DATA_STRING);
        f.add(SESSIONTYPE);         t.add(IDataAccess.DATA_STRING);
        f.add(MULTIDAYTOUR);        t.add(IDataAccess.DATA_STRING);
        constructArrays(f,t);

        KEY = new String[] { ENTRYNO };
    }

    public LogbookRecord() {
    }

    public LogbookRecord(String entryNo) {
        set(ENTRYNO, entryNo);
    }

    public LogbookRecord(LogbookRecord orig) {
        for (int i = 0; i < data.length; i++) {
            data[i] = orig.data[i];
        }
    }


}
