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
import java.util.*;

// @i18n complete

public class SessionGroupRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String SESSIONTYPE         = "SessionType";
    public static final String STARTDATE           = "StartDate";
    public static final String ENDDATE             = "EndDate";
    public static final String ACTIVEDAYS          = "ActiveDays";
    public static final String DISTANCE            = "Distance";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(SESSIONTYPE);                       t.add(IDataAccess.DATA_STRING);
        f.add(STARTDATE);                         t.add(IDataAccess.DATA_DATE);
        f.add(ENDDATE);                           t.add(IDataAccess.DATA_DATE);
        f.add(ACTIVEDAYS);                        t.add(IDataAccess.DATA_INTEGER);
        f.add(DISTANCE);                          t.add(IDataAccess.DATA_DISTANCE);

        MetaData metaData = constructMetaData(SessionGroups.DATATYPE, f, t, false);
        metaData.setKey(new String[] { ID });
        metaData.addIndex(new String[] { NAME });
    }

    public SessionGroupRecord(SessionGroups sessionGroups, MetaData metaData) {
        super(sessionGroups, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getId(),null,null);
    }

    public void setId(UUID id) {
        setUUID(ID, id);
    }
    public UUID getId() {
        return getUUID(ID);
    }

    public void setName(String name) {
        setString(NAME, name);
    }
    public String getName() {
        return getString(NAME);
    }

    public void setSessionType(String type) {
        setString(SESSIONTYPE, type);
    }
    public String getSessionType() {
        return getString(SESSIONTYPE);
    }

    public void setStartDate(DataTypeDate date) {
        setDate(STARTDATE, date);
    }
    public DataTypeDate getStartDate() {
        return getDate(STARTDATE);
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

    public void setDistance(DataTypeDistance distance) {
        setDistance(DISTANCE, distance);
    }
    public DataTypeDistance getDistance() {
        return getDistance(DISTANCE);
    }
}
