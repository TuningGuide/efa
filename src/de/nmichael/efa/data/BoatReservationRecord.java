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

public class BoatReservationRecord extends DataRecord {

    // =========================================================================
    // Value Constants
    // =========================================================================
    public static final String TYPE_ONETIME        = "ONETIME";
    public static final String TYPE_WEEKLY         = "WEEKLY";

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String BOATID              = "BoatId";
    public static final String RESERVATION         = "Reservation";
    public static final String TYPE                = "Type";
    public static final String DATEFROM            = "DateFrom";
    public static final String DATETO              = "DateTo";
    public static final String DAYOFWEEK           = "DayOfWeek";
    public static final String TIMEFROM            = "TimeFrom";
    public static final String TIMETO              = "TimeTo";
    public static final String PERSONID            = "PersonId";
    public static final String PERSONNAME          = "PersonName";
    public static final String REASON              = "Reason";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                   t.add(IDataAccess.DATA_UUID);
        f.add(RESERVATION);              t.add(IDataAccess.DATA_INTEGER);
        f.add(TYPE);                     t.add(IDataAccess.DATA_STRING);
        f.add(DATEFROM);                 t.add(IDataAccess.DATA_DATE);
        f.add(DATETO);                   t.add(IDataAccess.DATA_DATE);
        f.add(DAYOFWEEK);                t.add(IDataAccess.DATA_STRING);
        f.add(TIMEFROM);                 t.add(IDataAccess.DATA_TIME);
        f.add(TIMETO);                   t.add(IDataAccess.DATA_TIME);
        f.add(PERSONID);                 t.add(IDataAccess.DATA_UUID);
        f.add(PERSONNAME);               t.add(IDataAccess.DATA_STRING);
        f.add(REASON);                   t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(BoatReservations.DATATYPE, f, t, false);
        metaData.setKey(new String[] { BOATID, RESERVATION });
    }

    public BoatReservationRecord(BoatReservations boatReservation, MetaData metaData) {
        super(boatReservation, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Integer,String>(getBoatId(),getReservation(),null);
    }

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public void setReservation(int no) {
        setInt(RESERVATION, no);
    }
    public int getReservation() {
        return getInt(RESERVATION);
    }

    public void setType(String type) {
        setString(TYPE, type);
    }
    public String getType() {
        return getString(TYPE);
    }

    public void setDateFrom(DataTypeDate date) {
        setDate(DATEFROM, date);
    }
    public DataTypeDate getDateFrom() {
        return getDate(DATEFROM);
    }

    public void setDateTo(DataTypeDate date) {
        setDate(DATETO, date);
    }
    public DataTypeDate getDateTo() {
        return getDate(DATETO);
    }

    public void setDayOfWeek(String dayOfWeek) {
        setString(DAYOFWEEK, dayOfWeek);
    }
    public String getDayOfWeek() {
        return getString(DAYOFWEEK);
    }

    public void setTimeFrom(DataTypeTime time) {
        setTime(TIMEFROM, time);
    }
    public DataTypeTime getTimeFrom() {
        return getTime(TIMEFROM);
    }
    
    public void setTimeTo(DataTypeTime time) {
        setTime(TIMETO, time);
    }
    public DataTypeTime getTimeTo() {
        return getTime(TIMETO);
    }

    public void setPersonId(UUID id) {
        setUUID(PERSONID, id);
    }
    public UUID getPersonId() {
        return getUUID(PERSONID);
    }

    public void setPersonName(String name) {
        setString(PERSONNAME, name);
    }
    public String getPersonName() {
        return getString(PERSONNAME);
    }

    public void setReason(String reason) {
        setString(REASON, reason);
    }
    public String getReason() {
        return getString(REASON);
    }

}
