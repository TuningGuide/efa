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

public class BoatDamageRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String BOATID               = "BoatId";
    public static final String DAMAGE               = "Damage";
    public static final String DESCRIPTION          = "Description";
    public static final String REPORTDATE           = "ReportDate";
    public static final String REPORTTIME           = "ReportTime";
    public static final String FIXDATE              = "FixDate";
    public static final String FIXTIME              = "FixTime";
    public static final String REPORTEDBYPERSONID   = "ReportedByPersonId";
    public static final String REPORTEDBYPERSONNAME = "ReportedByPersonName";
    public static final String FIXEDBYPERSONID      = "FixedByPersonId";
    public static final String FIXEDBYPERSONNAME    = "FixedByPersonName";
    public static final String NOTES                = "Notes";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                   t.add(IDataAccess.DATA_UUID);
        f.add(DAMAGE);                   t.add(IDataAccess.DATA_INTEGER);
        f.add(DESCRIPTION);              t.add(IDataAccess.DATA_STRING);
        f.add(REPORTDATE);               t.add(IDataAccess.DATA_DATE);
        f.add(REPORTTIME);               t.add(IDataAccess.DATA_TIME);
        f.add(FIXDATE);                  t.add(IDataAccess.DATA_DATE);
        f.add(FIXTIME);                  t.add(IDataAccess.DATA_TIME);
        f.add(REPORTEDBYPERSONID);       t.add(IDataAccess.DATA_UUID);
        f.add(REPORTEDBYPERSONNAME);     t.add(IDataAccess.DATA_STRING);
        f.add(FIXEDBYPERSONID);          t.add(IDataAccess.DATA_UUID);
        f.add(FIXEDBYPERSONNAME);        t.add(IDataAccess.DATA_STRING);
        f.add(NOTES);                    t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(BoatDamages.DATATYPE, f, t, false);
        metaData.setKey(new String[] { BOATID, DAMAGE });
    }

    public BoatDamageRecord(BoatDamages boatDamage, MetaData metaData) {
        super(boatDamage, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Integer,String>(getBoatId(),getDamage(),null);
    }

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public void setDamage(int no) {
        setInt(DAMAGE, no);
    }
    public int getDamage() {
        return getInt(DAMAGE);
    }

    public void setDescription(String description) {
        setString(DESCRIPTION, description);
    }
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setReportDate(DataTypeDate date) {
        setDate(REPORTDATE, date);
    }
    public DataTypeDate getReportDate() {
        return getDate(REPORTDATE);
    }

    public void setReportTime(DataTypeTime time) {
        setTime(REPORTTIME, time);
    }
    public DataTypeTime getReportTime() {
        return getTime(REPORTTIME);
    }

    public void setFixDate(DataTypeDate date) {
        setDate(FIXDATE, date);
    }
    public DataTypeDate getFixDate() {
        return getDate(FIXDATE);
    }

    public void setFixTime(DataTypeTime time) {
        setTime(FIXTIME, time);
    }
    public DataTypeTime getFixTime() {
        return getTime(FIXTIME);
    }

    public void setReportedByPersonId(UUID id) {
        setUUID(REPORTEDBYPERSONID, id);
    }
    public UUID getReportedByPersonId() {
        return getUUID(REPORTEDBYPERSONID);
    }

    public void setReportedByPersonName(String name) {
        setString(REPORTEDBYPERSONNAME, name);
    }
    public String getReportedByPersonName() {
        return getString(REPORTEDBYPERSONNAME);
    }

    public void setFixedByPersonId(UUID id) {
        setUUID(FIXEDBYPERSONID, id);
    }
    public UUID getFixedByPersonId() {
        return getUUID(FIXEDBYPERSONID);
    }

    public void setFixedByPersonName(String name) {
        setString(FIXEDBYPERSONNAME, name);
    }
    public String getFixedByPersonName() {
        return getString(FIXEDBYPERSONNAME);
    }

    public void setNotes(String reason) {
        setString(NOTES, reason);
    }
    public String getNotes() {
        return getString(NOTES);
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
        // @todo
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[4];
        // @todo
        return items;
    }

}
