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
import de.nmichael.efa.data.types.*;
import java.util.*;

// @i18n complete

public class LogbookRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    private static final String ENTRYID          = "EntryId";
    private static final String DATE             = "Date";

    // Boat is either represented by BOATID,BOATVARIANTID or by BOATNAME
    private static final String BOATID           = "BoatId";
    private static final String BOATVARIANTID    = "BoatVariantId";
    private static final String BOATNAME         = "BoatName";

    // each person is either represented by xxxID or xxxNAME
    private static final String COXID            = "CoxId";
    private static final String COXNAME          = "CoxName";
    private static final String CREW1ID          = "Crew1Id";
    private static final String CREW1NAME        = "Crew1Name";
    private static final String CREW2ID          = "Crew2Id";
    private static final String CREW2NAME        = "Crew2Name";
    private static final String CREW3ID          = "Crew3Id";
    private static final String CREW3NAME        = "Crew3Name";
    private static final String CREW4ID          = "Crew4Id";
    private static final String CREW4NAME        = "Crew4Name";
    private static final String CREW5ID          = "Crew5Id";
    private static final String CREW5NAME        = "Crew5Name";
    private static final String CREW6ID          = "Crew6Id";
    private static final String CREW6NAME        = "Crew6Name";
    private static final String CREW7ID          = "Crew7Id";
    private static final String CREW7NAME        = "Crew7Name";
    private static final String CREW8ID          = "Crew8Id";
    private static final String CREW8NAME        = "Crew8Name";
    private static final String CREW9ID          = "Crew9Id";
    private static final String CREW9NAME        = "Crew9Name";
    private static final String CREW10ID         = "Crew10Id";
    private static final String CREW10NAME       = "Crew10Name";
    private static final String CREW11ID         = "Crew11Id";
    private static final String CREW11NAME       = "Crew11Name";
    private static final String CREW12ID         = "Crew12Id";
    private static final String CREW12NAME       = "Crew12Name";
    private static final String CREW13ID         = "Crew13Id";
    private static final String CREW13NAME       = "Crew13Name";
    private static final String CREW14ID         = "Crew14Id";
    private static final String CREW14NAME       = "Crew14Name";
    private static final String CREW15ID         = "Crew15Id";
    private static final String CREW15NAME       = "Crew15Name";
    private static final String CREW16ID         = "Crew16Id";
    private static final String CREW16NAME       = "Crew16Name";
    private static final String CREW17ID         = "Crew17Id";
    private static final String CREW17NAME       = "Crew17Name";
    private static final String CREW18ID         = "Crew18Id";
    private static final String CREW18NAME       = "Crew18Name";
    private static final String CREW19ID         = "Crew19Id";
    private static final String CREW19NAME       = "Crew19Name";
    private static final String CREW20ID         = "Crew20Id";
    private static final String CREW20NAME       = "Crew20Name";
    private static final String CREW21ID         = "Crew21Id";
    private static final String CREW21NAME       = "Crew21Name";
    private static final String CREW22ID         = "Crew22Id";
    private static final String CREW22NAME       = "Crew22Name";
    private static final String CREW23ID         = "Crew23Id";
    private static final String CREW23NAME       = "Crew23Name";
    private static final String CREW24ID         = "Crew24Id";
    private static final String CREW24NAME       = "Crew24Name";

    // BoatCaptain is the Number of the Boats's Captain (0 = Cox, 1 = Crew1, ...)
    private static final String BOATCAPTAIN      = "BoatCaptain";

    private static final String STARTTIME        = "StartTime";
    private static final String ENDTIME          = "EndTime";

    // Destination is either represented as DestinationId or DestinationName
    private static final String DESTINATIONID    = "DestinationId";
    private static final String DESTINATIONNAME  = "DestinationName";

    private static final String DISTANCEUNIT     = "DistanceUnit"; // default: "km"
    private static final String DISTANCEFRACTION = "DistanceFraction"; // default: 1000
    private static final String BOATDISTANCE     = "BoatDistance";
    private static final String COMMENTS         = "Comments";
    private static final String SESSIONTYPE      = "SessionType";
    private static final String MULTIDAYTOURID   = "MultiDayTourId";

    // =========================================================================
    // Supplementary Constants
    // =========================================================================

    // Distance Units
    private static final String DIST_KILOMETERS  = "km"; // default if not otherwise specified
    private static final String DIST_MILES       = "mi";


    private static String getCrewFieldNameId(int pos) {
        return "Crew"+pos+"Id";
    }

    private static String getCrewFieldNameName(int pos) {
        return "Crew"+pos+"Name";
    }

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ENTRYID);             t.add(IDataAccess.DATA_STRING);
        f.add(DATE);                t.add(IDataAccess.DATA_DATE);
        f.add(BOATID);              t.add(IDataAccess.DATA_INTEGER);
        f.add(BOATVARIANTID);       t.add(IDataAccess.DATA_INTEGER);
        f.add(BOATNAME);            t.add(IDataAccess.DATA_STRING);
        f.add(COXID);               t.add(IDataAccess.DATA_INTEGER);
        f.add(COXNAME);             t.add(IDataAccess.DATA_STRING);
        f.add(CREW1ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW1NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW2ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW2NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW3ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW3NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW4ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW4NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW5ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW5NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW6ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW6NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW7ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW7NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW8ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW8NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW9ID);             t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW9NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW10ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW10NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW11ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW11NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW12ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW12NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW13ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW13NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW14ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW14NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW15ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW15NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW16ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW16NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW17ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW17NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW18ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW18NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW19ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW19NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW20ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW20NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW21ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW21NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW22ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW22NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW23ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW23NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW24ID);            t.add(IDataAccess.DATA_INTEGER);
        f.add(CREW24NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(BOATCAPTAIN);         t.add(IDataAccess.DATA_INTEGER);
        f.add(STARTTIME);           t.add(IDataAccess.DATA_TIME);
        f.add(ENDTIME);             t.add(IDataAccess.DATA_TIME);
        f.add(DESTINATIONID);       t.add(IDataAccess.DATA_INTEGER);
        f.add(DESTINATIONNAME);     t.add(IDataAccess.DATA_STRING);
        f.add(DISTANCEUNIT);        t.add(IDataAccess.DATA_STRING);
        f.add(DISTANCEFRACTION);    t.add(IDataAccess.DATA_INTEGER);
        f.add(BOATDISTANCE);        t.add(IDataAccess.DATA_INTEGER);
        f.add(COMMENTS);            t.add(IDataAccess.DATA_STRING);
        f.add(SESSIONTYPE);         t.add(IDataAccess.DATA_STRING);
        f.add(MULTIDAYTOURID);      t.add(IDataAccess.DATA_INTEGER);
        constructArrays(f, t, false);

        KEY = new String[] { ENTRYID };
    }

    public LogbookRecord() {
    }

    public LogbookRecord(String entryId) {
        setString(ENTRYID, entryId);
    }

    public LogbookRecord(LogbookRecord orig) {
        synchronized(orig.data) {
            for (int i = 0; i < data.length; i++) {
                data[i] = orig.data[i];
            }
        }
    }

    public DataKey getKey() {
        return new DataKey<String,String,String>(getEntryId(),null,null);
    }

    public String getEntryId() {
        return getString(ENTRYID);
    }

    public void setDate(DataTypeDate date) {
        setDate(DATE, date);
    }
    public DataTypeDate getDate() {
        return getDate(DATE);
    }

    public void setBoatId(int id) {
        setInt(BOATID, id);
    }
    public int getBoatId() {
        return getInt(BOATID);
    }

    public void setBoatVariantId(int id) {
        setInt(BOATVARIANTID, id);
    }
    public int getBoatVariantId() {
        return getInt(BOATVARIANTID);
    }

    public void setBoatName(String name) {
        setString(BOATNAME, name);
    }
    public String getBoatName() {
        return getString(BOATNAME);
    }

    public void setCoxId(int id) {
        setInt(COXID, id);
    }
    public int getCoxId() {
        return getInt(COXID);
    }

    public void setCoxName(String name) {
        setString(COXNAME, name);
    }
    public String getCoxName() {
        return getString(COXNAME);
    }

    public void setCrewId(int pos, int id) {
        setInt(getCrewFieldNameId(pos), id);
    }
    public int getCrewId(int pos) {
        return getInt(getCrewFieldNameId(pos));
    }

    public void setCrewName(int pos, String name) {
        setString(getCrewFieldNameName(pos), name);
    }
    public String getCrewName(int pos) {
        return getString(getCrewFieldNameName(pos));
    }

    public void setBoatCaptainPosition(int id) {
        setInt(BOATCAPTAIN, id);
    }
    public int getBoatCaptainPosition() {
        return getInt(BOATCAPTAIN);
    }

    public void setStartTime(DataTypeTime time) {
        setTime(STARTTIME, time);
    }
    public DataTypeTime getStartTime() {
        return getTime(STARTTIME);
    }

    public void setEndTime(DataTypeTime time) {
        setTime(ENDTIME, time);
    }
    public DataTypeTime getEndTime() {
        return getTime(ENDTIME);
    }

    public void setDestinationId(int id) {
        setInt(DESTINATIONID, id);
    }
    public int getDestinationId() {
        return getInt(DESTINATIONID);
    }

    public void setDestinationName(String name) {
        setString(DESTINATIONNAME, name);
    }
    public String getDestinationName() {
        return getString(DESTINATIONNAME);
    }

    public void setDistanceUnit(String name) {
        if (name != null && name.equals(DIST_KILOMETERS)) {
            setString(DISTANCEUNIT, DIST_KILOMETERS);
            return;
        }
        if (name != null && name.equals(DIST_MILES)) {
            setString(DISTANCEUNIT, DIST_MILES);
            return;
        }
        setString(DISTANCEUNIT, null);
    }
    public String getDistanceUnit() {
        return getString(DISTANCEUNIT);
    }

    public void setDistanceFraction(int frac) {
        if (frac < 1) {
            setInt(DISTANCEFRACTION, IDataAccess.UNDEFINED_INT);
        } else {
            setInt(DISTANCEFRACTION, frac);
        }
    }
    public int getDistanceFraction() {
        int frac = getInt(DISTANCEFRACTION);
        if (frac < 1) {
            return IDataAccess.UNDEFINED_INT;
        }
        return frac;
    }

    public void setBoatDistance(int dist, String distUnit, int distFraction) {
        setString(DISTANCEUNIT, distUnit);
        setInt(DISTANCEFRACTION, distFraction);
        setInt(BOATDISTANCE, dist);
    }
    public int getBoatDistanceAsIs() {
        return getInt(BOATDISTANCE);
    }

    public void setBoatDistanceMeters(int dist) {
        setString(DISTANCEUNIT, null);                       // default: km
        setInt(DISTANCEFRACTION, IDataAccess.UNDEFINED_INT); // default: 1000
        setInt(BOATDISTANCE, dist);
    }
    public int getBoatDistanceMeters() {
        if (getDistanceUnit() == null && getDistanceFraction() == IDataAccess.UNDEFINED_INT) {
            return getInt(BOATDISTANCE);
        } else {
            throw new UnsupportedOperationException("Boat Distance Unit Conversion");
        }
    }

    public void setComments(String comments) {
        setString(COMMENTS, comments);
    }
    public String getComments() {
        return getString(COMMENTS);
    }

    public void setSessionType(String type) {
        setString(SESSIONTYPE, type);
    }
    public String getSessionType() {
        return getString(SESSIONTYPE);
    }

    public void setMultiDayTourId(int id) {
        setInt(MULTIDAYTOURID, id);
    }
    public int getMultiDayTourId() {
        return getInt(MULTIDAYTOURID);
    }

}
