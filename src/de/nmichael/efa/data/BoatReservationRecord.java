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
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
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

    public static final String[] IDX_BOATID = new String[] { BOATID };

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
        metaData.addIndex(IDX_BOATID);
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

    private String getDateDescription(DataTypeDate date, String weekday, DataTypeTime time) {
        if (date == null && weekday == null) {
            return "";
        }
        return (date != null ? date.toString() : Daten.efaTypes.getValueWeekday(weekday)) +
                (time != null ? " " + time.toString() : "");
    }

    public String getDateTimeFromDescription() {
        String type = getType();
        if (type != null && type.equals(TYPE_ONETIME)) {
            return getDateDescription(getDateFrom(), null, getTimeFrom());
        }
        if (type != null && type.equals(TYPE_WEEKLY)) {
            return getDateDescription(null, getDayOfWeek(), getTimeFrom());
        }
        return "";
    }

    public String getDateTimeToDescription() {
        String type = getType();
        if (type != null && type.equals(TYPE_ONETIME)) {
            return getDateDescription(getDateTo(), null, getTimeTo());
        }
        if (type != null && type.equals(TYPE_WEEKLY)) {
            return getDateDescription(null, getDayOfWeek(), getTimeTo());
        }
        return "";
    }

    public String getPersonDescription() {
        UUID id = getPersonId();
        try {
            PersonRecord p = getPersistence().getProject().getPersons(false).getPerson(id, System.currentTimeMillis());
            if (p != null) {
                return p.getQualifiedName();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return getPersonName();
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Reservierung");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        // @todo
        //v.add(item = new ItemTypeString(BoatRecord.NAME, getName(),
        //        IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Name")));
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[4];
        header[0] = new TableItemHeader(International.getString("Von"));
        header[1] = new TableItemHeader(International.getString("Bis"));
        header[2] = new TableItemHeader(International.getString("Reserviert für"));
        header[3] = new TableItemHeader(International.getString("Grund"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[4];
        items[0] = new TableItem(getDateTimeFromDescription());
        items[1] = new TableItem(getDateTimeToDescription());
        items[2] = new TableItem(getPersonDescription());
        items[3] = new TableItem(getReason());
        return items;
    }
    
}
