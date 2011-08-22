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

import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class LogbookRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ENTRYID          = "EntryId";
    public static final String DATE             = "Date";
    public static final String ENDDATE          = "EndDate";
    public static final String ACTIVEDAYS       = "ActiveDays";

    // Boat is either represented by BOATID,BOATVARIANT or by BOATNAME
    public static final String BOATID           = "BoatId";
    public static final String BOATVARIANT      = "BoatVariant";
    public static final String BOATNAME         = "BoatName";

    // each person is either represented by xxxID or xxxNAME
    public static final String COXID            = "CoxId";
    public static final String COXNAME          = "CoxName";
    public static final String CREW1ID          = "Crew1Id";
    public static final String CREW1NAME        = "Crew1Name";
    public static final String CREW2ID          = "Crew2Id";
    public static final String CREW2NAME        = "Crew2Name";
    public static final String CREW3ID          = "Crew3Id";
    public static final String CREW3NAME        = "Crew3Name";
    public static final String CREW4ID          = "Crew4Id";
    public static final String CREW4NAME        = "Crew4Name";
    public static final String CREW5ID          = "Crew5Id";
    public static final String CREW5NAME        = "Crew5Name";
    public static final String CREW6ID          = "Crew6Id";
    public static final String CREW6NAME        = "Crew6Name";
    public static final String CREW7ID          = "Crew7Id";
    public static final String CREW7NAME        = "Crew7Name";
    public static final String CREW8ID          = "Crew8Id";
    public static final String CREW8NAME        = "Crew8Name";
    public static final String CREW9ID          = "Crew9Id";
    public static final String CREW9NAME        = "Crew9Name";
    public static final String CREW10ID         = "Crew10Id";
    public static final String CREW10NAME       = "Crew10Name";
    public static final String CREW11ID         = "Crew11Id";
    public static final String CREW11NAME       = "Crew11Name";
    public static final String CREW12ID         = "Crew12Id";
    public static final String CREW12NAME       = "Crew12Name";
    public static final String CREW13ID         = "Crew13Id";
    public static final String CREW13NAME       = "Crew13Name";
    public static final String CREW14ID         = "Crew14Id";
    public static final String CREW14NAME       = "Crew14Name";
    public static final String CREW15ID         = "Crew15Id";
    public static final String CREW15NAME       = "Crew15Name";
    public static final String CREW16ID         = "Crew16Id";
    public static final String CREW16NAME       = "Crew16Name";
    public static final String CREW17ID         = "Crew17Id";
    public static final String CREW17NAME       = "Crew17Name";
    public static final String CREW18ID         = "Crew18Id";
    public static final String CREW18NAME       = "Crew18Name";
    public static final String CREW19ID         = "Crew19Id";
    public static final String CREW19NAME       = "Crew19Name";
    public static final String CREW20ID         = "Crew20Id";
    public static final String CREW20NAME       = "Crew20Name";
    public static final String CREW21ID         = "Crew21Id";
    public static final String CREW21NAME       = "Crew21Name";
    public static final String CREW22ID         = "Crew22Id";
    public static final String CREW22NAME       = "Crew22Name";
    public static final String CREW23ID         = "Crew23Id";
    public static final String CREW23NAME       = "Crew23Name";
    public static final String CREW24ID         = "Crew24Id";
    public static final String CREW24NAME       = "Crew24Name";

    // BoatCaptain is the Number of the Boats's Captain (0 = Cox, 1 = Crew1, ...)
    public static final String BOATCAPTAIN      = "BoatCaptain";

    public static final String STARTTIME        = "StartTime";
    public static final String ENDTIME          = "EndTime";

    // Destination is either represented as DestinationId or DestinationName
    public static final String DESTINATIONID    = "DestinationId";
    public static final String DESTINATIONNAME  = "DestinationName";
    public static final String DESTINATIONVARIANTNAME = "DestinationVariantName";

    public static final String DISTANCE         = "Distance";
    public static final String COMMENTS         = "Comments";
    public static final String SESSIONTYPE      = "SessionType";
    public static final String SESSIONGROUPID   = "SessionGroupId";

    public static final String OPEN             = "Open";
    public static final String EFBSYNCSTATE     = "EfbSyncState";

    // =========================================================================
    // Supplementary Constants
    // =========================================================================

    // General
    public static final int CREW_MAX = 24;


    public static String getCrewFieldNameId(int pos) {
        if (pos == 0) {
            return COXID;
        }
        return "Crew"+pos+"Id";
    }

    public static String getCrewFieldNameName(int pos) {
        if (pos == 0) {
            return COXNAME;
        }
        return "Crew"+pos+"Name";
    }

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ENTRYID);             t.add(IDataAccess.DATA_INTSTRING);
        f.add(DATE);                t.add(IDataAccess.DATA_DATE);
        f.add(ENDDATE);             t.add(IDataAccess.DATA_DATE);
        f.add(ACTIVEDAYS);          t.add(IDataAccess.DATA_INTEGER);
        f.add(BOATID);              t.add(IDataAccess.DATA_UUID);
        f.add(BOATVARIANT);         t.add(IDataAccess.DATA_INTEGER);
        f.add(BOATNAME);            t.add(IDataAccess.DATA_STRING);
        f.add(COXID);               t.add(IDataAccess.DATA_UUID);
        f.add(COXNAME);             t.add(IDataAccess.DATA_STRING);
        f.add(CREW1ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW1NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW2ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW2NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW3ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW3NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW4ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW4NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW5ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW5NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW6ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW6NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW7ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW7NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW8ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW8NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW9ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW9NAME);           t.add(IDataAccess.DATA_STRING);
        f.add(CREW10ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW10NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW11ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW11NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW12ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW12NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW13ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW13NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW14ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW14NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW15ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW15NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW16ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW16NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW17ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW17NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW18ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW18NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW19ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW19NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW20ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW20NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW21ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW21NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW22ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW22NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW23ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW23NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(CREW24ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW24NAME);          t.add(IDataAccess.DATA_STRING);
        f.add(BOATCAPTAIN);         t.add(IDataAccess.DATA_INTEGER);
        f.add(STARTTIME);           t.add(IDataAccess.DATA_TIME);
        f.add(ENDTIME);             t.add(IDataAccess.DATA_TIME);
        f.add(DESTINATIONID);       t.add(IDataAccess.DATA_UUID);
        f.add(DESTINATIONNAME);     t.add(IDataAccess.DATA_STRING);
        f.add(DESTINATIONVARIANTNAME); t.add(IDataAccess.DATA_STRING);
        f.add(DISTANCE);            t.add(IDataAccess.DATA_DISTANCE);
        f.add(COMMENTS);            t.add(IDataAccess.DATA_STRING);
        f.add(SESSIONTYPE);         t.add(IDataAccess.DATA_STRING);
        f.add(SESSIONGROUPID);      t.add(IDataAccess.DATA_UUID);
        f.add(EFBSYNCSTATE);           t.add(IDataAccess.DATA_INTEGER);
        f.add(OPEN);                t.add(IDataAccess.DATA_BOOLEAN);
        MetaData metaData = constructMetaData(Logbook.DATATYPE, f, t, false);
        metaData.setKey(new String[] { ENTRYID });
    }

    public LogbookRecord(Logbook logbook, MetaData metaData) {
        super(logbook, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<DataTypeIntString,String,String>(getEntryId(),null,null);
    }

    public static DataKey getKey(DataTypeIntString entryNo) {
        return new DataKey<DataTypeIntString,String,String>(entryNo,null,null);
    }

    public void setEntryId(DataTypeIntString entryId) {
        setIntString(ENTRYID, entryId);
    }
    public DataTypeIntString getEntryId() {
        return getIntString(ENTRYID);
    }

    public void setDate(DataTypeDate date) {
        setDate(DATE, date);
    }
    public DataTypeDate getDate() {
        return getDate(DATE);
    }

    public void setEndDate(DataTypeDate date) {
        setDate(ENDDATE, date);
    }
    public DataTypeDate getEndDate() {
        return getDate(ENDDATE);
    }

    public void setActiveDays(int days) {
        setInt(ACTIVEDAYS, days);
    }
    public int getActiveDays() {
        return getInt(ACTIVEDAYS);
    }

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public void setBoatVariant(int variant) {
        setInt(BOATVARIANT, variant);
    }
    public int getBoatVariant() {
        return getInt(BOATVARIANT);
    }

    public void setBoatName(String name) {
        setString(BOATNAME, name);
    }
    public String getBoatName() {
        return getString(BOATNAME);
    }

    public void setCoxId(UUID id) {
        setUUID(COXID, id);
    }
    public UUID getCoxId() {
        return getUUID(COXID);
    }

    public void setCoxName(String name) {
        setString(COXNAME, name);
    }
    public String getCoxName() {
        return getString(COXNAME);
    }

    public void setCrewId(int pos, UUID id) {
        setUUID(getCrewFieldNameId(pos), id);
    }
    public UUID getCrewId(int pos) {
        return getUUID(getCrewFieldNameId(pos));
    }

    public void setCrewName(int pos, String name) {
        setString(getCrewFieldNameName(pos), name);
    }
    public String getCrewName(int pos) {
        return getString(getCrewFieldNameName(pos));
    }

    public void setBoatCaptainPosition(int pos) {
        setInt(BOATCAPTAIN, pos);
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

    public void setDestinationId(UUID id) {
        setUUID(DESTINATIONID, id);
    }
    public UUID getDestinationId() {
        return getUUID(DESTINATIONID);
    }

    public void setDestinationName(String name) {
        setString(DESTINATIONNAME, name);
    }
    public String getDestinationName() {
        return getString(DESTINATIONNAME);
    }

    public void setDestinationVariantName(String name) {
        setString(DESTINATIONVARIANTNAME, name);
    }
    public String getDestinationVariantName() {
        return getString(DESTINATIONVARIANTNAME);
    }

    public static String[] getDestinationNameAndVariantFromString(String s) {
        int pos = s.indexOf(DestinationRecord.DESTINATION_VARIANT_SEPARATOR);
        String[] names = new String[2];
        names[0] = (pos < 0 ? s.trim() : s.substring(0, pos).trim());
        names[1] = (pos >= 0 ? s.substring(pos+1).trim() : "");
        return names;
    }

    public void setDistance(DataTypeDistance distance) {
        setDistance(DISTANCE, distance);
    }
    public DataTypeDistance getDistance() {
        return getDistance(DISTANCE);
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

    public void setSessionGroupId(UUID id) {
        setUUID(SESSIONGROUPID, id);
    }
    public UUID getSessionGroupId() {
        return getUUID(SESSIONGROUPID);
    }

    public void setSyncState(int syncState) {
        setInt(EFBSYNCSTATE, syncState);
    }
    public int getSyncState() {
        return getInt(EFBSYNCSTATE);
    }

    public void setSessionIsOpen(boolean open) {
        setBool(OPEN, open);
    }
    public boolean getSessionIsOpen() {
        return getBool(OPEN);
    }

    public BoatRecord getBoatRecord(long validAt) {
        try {
            UUID id = getBoatId();
            if (id != null) {
                return getPersistence().getProject().getBoats(false).getBoat(id, validAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    private PersonRecord getPersonRecord(int pos, long validAt) {
        try {
            UUID id = null;
            if (pos == 0) {
                id = getCoxId();
            }
            if (pos >= 1 && pos <= CREW_MAX) {
                id = getCrewId(pos);
            }
            if (id != null) {
                return getPersistence().getProject().getPersons(false).getPerson(id, validAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public PersonRecord getCoxRecord(long validAt) {
        return getPersonRecord(0, validAt);
    }

    public PersonRecord getCrewRecord(int pos, long validAt) {
        return getPersonRecord(pos, validAt);
    }

    public DestinationRecord getDestinationRecord(long validAt) {
        try {
            UUID id = getDestinationId();
            if (id != null) {
                return getPersistence().getProject().getDestinations(false).getDestination(id, validAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public String getBoatAsName() {
        return getBoatAsName(getValidAtTimestamp());
    }

    public String getBoatAsName(long validAt) {
        String name = null;
        BoatRecord b = getBoatRecord(validAt);
        if (b != null) {
            name = b.getQualifiedName();
        }
        if (name == null || name.length() == 0) {
            name = getBoatName();
        }
        if (name != null) {
            return name;
        }
        return "";
    }

    private String getPersonAsName(int pos, long validAt) {
        String name = null;
        if (validAt < 0) {
            validAt = getValidAtTimestamp();
        }
        PersonRecord p = getPersonRecord(pos, validAt);
        if (p != null) {
            name = p.getQualifiedName();
        }
        if (name == null || name.length() == 0) {
            if (pos == 0) {
                name = getCoxName();
            }
            if (pos >= 1 && pos <= CREW_MAX) {
                name = getCrewName(pos);
            }
        }
        if (name != null) {
            return name;
        }
        return "";
    }

    public String getCoxAsName() {
        return getCoxAsName(getValidAtTimestamp());
    }

    public String getCoxAsName(long validAt) {
        return getPersonAsName(0, validAt);
    }

    public String getCrewAsName(int pos) {
        return getCrewAsName(pos, getValidAtTimestamp());
    }

    public String getCrewAsName(int pos, long validAt) {
        return getPersonAsName(pos, validAt);
    }

    public Vector<String> getAllCoxAndCrewAsNames() {
        return getAllCoxAndCrewAsNames(getValidAtTimestamp());
    }

    public Vector<String> getAllCoxAndCrewAsNames(long validAt) {
        Vector<String> v = new Vector<String>();
        String s;
        if ( (s = getCoxAsName(validAt)).length() > 0) {
            v.add(s);
        }
        for (int i=0; i<CREW_MAX; i++) {
            if ( (s = getPersonAsName(i, validAt)).length() > 0) {
                v.add(s);
            }
        }
        return v;
    }

    public String getAllCoxAndCrewAsNameString() {
        return getAllCoxAndCrewAsNameString(getValidAtTimestamp());
    }

    public String getAllCoxAndCrewAsNameString(long validAt) {
        Vector<String> v = getAllCoxAndCrewAsNames(validAt);
        StringBuffer s = new StringBuffer();
        for (int i=0; v != null && i < v.size(); i++) {
            s.append( (s.length() > 0 ? ", " : "") + v.get(i));
        }
        return s.toString();
    }

    public int getNumberOfCrewMembers() {
        int c = 0;
        long validAt = getValidAtTimestamp();
        for (int i=0; i<CREW_MAX; i++) {
            if (getPersonAsName(i, validAt).length() > 0) {
                c++;
            }
        }
        return c;
    }

    public String getDestinationAndVariantName() {
        return getDestinationAndVariantName(getValidAtTimestamp());
    }

    public String getDestinationAndVariantName(long validAt) {
        String name = null;
        if (validAt < 0) {
            validAt = getValidAtTimestamp();
        }
        DestinationRecord d = getDestinationRecord(validAt);
        if (d != null) {
            name = d.getName();
        }
        if (name == null || name.length() == 0) {
            name = getDestinationName();
        }
        String variant = getDestinationVariantName();
        if (variant != null && variant.length() > 0) {
            name = name + " " + DestinationRecord.DESTINATION_VARIANT_SEPARATOR + " " + variant;
        }
        if (name != null) {
            return name;
        }
        return "";
    }

    public static long getValidAtTimestamp(DataTypeDate d, DataTypeTime t) {
        if (d != null && d.isSet()) {
            return d.getTimestamp(t);
        }
        return 0;
    }

    public long getValidAtTimestamp() {
        return getValidAtTimestamp(getDate(), getStartTime());
    }

    public static int getCrewNoFromFieldName(String field) {
        if (field == null) {
            return -1;
        }
        if (field.equals(COXID) || field.equals(COXNAME)) {
            return 0;
        }
        if (field.equals(CREW1ID) || field.equals(CREW1NAME)) {
            return 1;
        }
        if (field.equals(CREW2ID) || field.equals(CREW2NAME)) {
            return 2;
        }
        if (field.equals(CREW3ID) || field.equals(CREW3NAME)) {
            return 3;
        }
        if (field.equals(CREW4ID) || field.equals(CREW4NAME)) {
            return 4;
        }
        if (field.equals(CREW5ID) || field.equals(CREW5NAME)) {
            return 5;
        }
        if (field.equals(CREW6ID) || field.equals(CREW6NAME)) {
            return 6;
        }
        if (field.equals(CREW7ID) || field.equals(CREW7NAME)) {
            return 7;
        }
        if (field.equals(CREW8ID) || field.equals(CREW8NAME)) {
            return 8;
        }
        if (field.equals(CREW9ID) || field.equals(CREW9NAME)) {
            return 9;
        }
        if (field.equals(CREW10ID) || field.equals(CREW10NAME)) {
            return 10;
        }
        if (field.equals(CREW11ID) || field.equals(CREW11NAME)) {
            return 11;
        }
        if (field.equals(CREW12ID) || field.equals(CREW12NAME)) {
            return 12;
        }
        if (field.equals(CREW13ID) || field.equals(CREW13NAME)) {
            return 13;
        }
        if (field.equals(CREW14ID) || field.equals(CREW14NAME)) {
            return 14;
        }
        if (field.equals(CREW15ID) || field.equals(CREW15NAME)) {
            return 15;
        }
        if (field.equals(CREW16ID) || field.equals(CREW16NAME)) {
            return 16;
        }
        if (field.equals(CREW17ID) || field.equals(CREW17NAME)) {
            return 17;
        }
        if (field.equals(CREW18ID) || field.equals(CREW18NAME)) {
            return 18;
        }
        if (field.equals(CREW19ID) || field.equals(CREW19NAME)) {
            return 19;
        }
        if (field.equals(CREW20ID) || field.equals(CREW20NAME)) {
            return 20;
        }
        if (field.equals(CREW21ID) || field.equals(CREW21NAME)) {
            return 21;
        }
        if (field.equals(CREW22ID) || field.equals(CREW22NAME)) {
            return 22;
        }
        if (field.equals(CREW23ID) || field.equals(CREW23NAME)) {
            return 23;
        }
        if (field.equals(CREW24ID) || field.equals(CREW24NAME)) {
            return 24;
        }
        return -1;
    }

    public Vector<IItemType> getGuiItems() {
        return null; // not supported for LogbokRecord
    }

    public TableItemHeader[] getGuiTableHeader() {
        return null; // not supported for LogbokRecord
    }

    public TableItem[] getGuiTableItems() {
        return null; // not supported for LogbokRecord
    }

}
