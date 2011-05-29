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

    private String getBoatName() {
        Boats boats = getPersistence().getProject().getBoats(false);
        String boatName = "?";
        if (boats != null) {
            BoatRecord r = boats.getBoat(getBoatId(), System.currentTimeMillis());
            if (r != null) {
                boatName = r.getQualifiedName();
            }
        }
        return boatName;
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Reservierung");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        String boatName = getBoatName();

        ItemTypeDate dateFrom;
        ItemTypeTime timeFrom;

        v.add(item = new ItemTypeLabel("GUI_BOAT_NAME",
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getMessage("Reservierung für {boat}", boatName)));
        item.setPadding(0, 0, 0, 10);
        v.add(item = new ItemTypeRadioButtons(BoatReservationRecord.TYPE, (getType() != null && getType().length() > 0 ? getType() : TYPE_ONETIME),
                new String[] {
                    TYPE_ONETIME,
                    TYPE_WEEKLY
                },
                new String[] {
                    International.getString("einmalig"),
                    International.getString("wöchentlich"),
                },
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Art der Reservierung")));
        v.add(item = new ItemTypeStringList(BoatReservationRecord.DAYOFWEEK, getDayOfWeek(),
                    EfaTypes.makeDayOfWeekArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeDayOfWeekArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Wochentag")));
        item.setNotNull(true);
        v.add(item = new ItemTypeDate(BoatReservationRecord.DATEFROM, getDateFrom(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Von") + " (" +
                International.getString("Tag") + ")"));
        item.setNotNull(true);
        dateFrom = (ItemTypeDate)item;
        v.add(item = new ItemTypeTime(BoatReservationRecord.TIMEFROM, getTimeFrom(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Von") + " (" +
                International.getString("Zeit") + ")"));
        ((ItemTypeTime)item).enableSeconds(false);
        item.setNotNull(true);
        timeFrom = (ItemTypeTime)item;
        v.add(item = new ItemTypeDate(BoatReservationRecord.DATETO, getDateTo(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Bis") + " (" +
                International.getString("Tag") + ")"));
        item.setNotNull(true);
        ((ItemTypeDate)item).setMustBeAfter(dateFrom, true);
        v.add(item = new ItemTypeTime(BoatReservationRecord.TIMETO, getTimeTo(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Bis") + " (" +
                International.getString("Zeit") + ")"));
        ((ItemTypeTime)item).enableSeconds(false);
        item.setNotNull(true);
        ((ItemTypeTime)item).setMustBeAfter(timeFrom, false);
        v.add(item = getGuiItemTypeStringAutoComplete(BoatReservationRecord.PERSONID, null,
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("Reserviert für")));
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(BoatReservationRecord.PERSONNAME);
        if (getPersonId() != null) {
            ((ItemTypeStringAutoComplete)item).setId(getPersonId());
        } else {
            ((ItemTypeStringAutoComplete)item).parseAndShowValue(getPersonName());
        }
        item.setNotNull(true);
        v.add(item = new ItemTypeString(BoatReservationRecord.REASON, getReason(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Reservierungsgrund")));

        return v;
    }

    public void saveGuiItems(Vector<IItemType> items) {
        super.saveGuiItems(items);
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[5];
        header[0] = new TableItemHeader(International.getString("Boot"));
        header[1] = new TableItemHeader(International.getString("Von"));
        header[2] = new TableItemHeader(International.getString("Bis"));
        header[3] = new TableItemHeader(International.getString("Reserviert für"));
        header[4] = new TableItemHeader(International.getString("Grund"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[5];
        items[0] = new TableItem(getBoatName());
        items[1] = new TableItem(getDateTimeFromDescription());
        items[2] = new TableItem(getDateTimeToDescription());
        items[3] = new TableItem(getPersonDescription());
        items[4] = new TableItem(getReason());
        return items;
    }

    public String getQualifiedName() {
        return International.getMessage("Reservierung für {boat}", getBoatName());
    }
  
}
