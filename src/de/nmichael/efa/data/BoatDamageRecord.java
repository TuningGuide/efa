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
    public static final String FIXED                = "Fixed";
    public static final String REPORTDATE           = "ReportDate";
    public static final String REPORTTIME           = "ReportTime";
    public static final String FIXDATE              = "FixDate";
    public static final String FIXTIME              = "FixTime";
    public static final String REPORTEDBYPERSONID   = "ReportedByPersonId";
    public static final String REPORTEDBYPERSONNAME = "ReportedByPersonName";
    public static final String FIXEDBYPERSONID      = "FixedByPersonId";
    public static final String FIXEDBYPERSONNAME    = "FixedByPersonName";
    public static final String NOTES                = "Notes";

    public static final String[] IDX_BOATID = new String[] { BOATID };

    private static final String GUIITEM_REPORTDATETIME = "GUIITEM_REPORTDATETIME";
    private static final String GUIITEM_FIXDATETIME    = "GUIITEM_FIXDATETIME";

    private boolean showOnlyAddDamageFields = false;

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                   t.add(IDataAccess.DATA_UUID);
        f.add(DAMAGE);                   t.add(IDataAccess.DATA_INTEGER);
        f.add(DESCRIPTION);              t.add(IDataAccess.DATA_STRING);
        f.add(FIXED);                    t.add(IDataAccess.DATA_BOOLEAN);
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
        metaData.addIndex(IDX_BOATID);
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

    public void setFixed(boolean isFixed) {
        setBool(FIXED, isFixed);
    }
    public boolean getFixed() {
        return getBool(FIXED);
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
        String CAT_BASEDATA     = "%01%" + International.getString("Bootsschaden");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        v.add(item = new ItemTypeLabel("GUI_BOAT_NAME",
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getMessage("Bootsschaden für {boat}", getBoatName())));
        item.setPadding(0, 0, 0, 10);
        v.add(item = new ItemTypeString(BoatDamageRecord.DESCRIPTION, getDescription(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Beschreibung")));
        v.add(item = new ItemTypeDateTime(GUIITEM_REPORTDATETIME, getReportDate(), getReportTime(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("gemeldet am")));
        if (showOnlyAddDamageFields) {
            item.setEnabled(false);
        }
        v.add(item = getGuiItemTypeStringAutoComplete(BoatDamageRecord.REPORTEDBYPERSONID, null,
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("gemeldet von")));
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(BoatDamageRecord.REPORTEDBYPERSONNAME);
        if (!showOnlyAddDamageFields) {
            v.add(item = new ItemTypeDateTime(GUIITEM_FIXDATETIME, getFixDate(), getFixTime(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("behoben am")));
            v.add(item = getGuiItemTypeStringAutoComplete(BoatDamageRecord.FIXEDBYPERSONID, null,
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("behoben von")));
            ((ItemTypeStringAutoComplete) item).setAlternateFieldNameForPlainText(BoatDamageRecord.FIXEDBYPERSONNAME);
            v.add(item = new ItemTypeString(BoatDamageRecord.NOTES, getNotes(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Bemerkungen")));
            v.add(item = new ItemTypeBoolean(BoatDamageRecord.FIXED, getFixed(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Schaden wurde behoben")));
        }
        return v;
    }

    public void saveGuiItems(Vector<IItemType> items) {
        for (IItemType item : items) {
            if (item.getName().equals(GUIITEM_REPORTDATETIME)) {
                setReportDate(((ItemTypeDateTime)item).getDate());
                setReportTime(((ItemTypeDateTime)item).getTime());
            }
            if (item.getName().equals(GUIITEM_FIXDATETIME)) {
                setFixDate(((ItemTypeDateTime)item).getDate());
                setFixTime(((ItemTypeDateTime)item).getTime());
            }
        }
        super.saveGuiItems(items);
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[5];
        header[0] = new TableItemHeader(International.getString("Boot"));
        header[1] = new TableItemHeader(International.getString("Schaden"));
        header[2] = new TableItemHeader(International.getString("gemeldet am"));
        header[3] = new TableItemHeader(International.getString("behoben am"));
        header[4] = new TableItemHeader(International.getString("Status"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[5];
        items[0] = new TableItem(getBoatName());
        items[1] = new TableItem(getDescription());
        items[2] = new TableItem(DataTypeDate.getDateTimeString(getReportDate(), getReportTime()));
        items[3] = new TableItem(DataTypeDate.getDateTimeString(getFixDate(), getFixTime()));
        items[4] = new TableItem( (getFixed() ? International.getString("behoben") :
                                                International.getString("offen")));
        return items;
    }

    public String getQualifiedName() {
        return International.getMessage("Schaden für {boat}", getBoatName());
    }

    public void setShowOnlyAddDamageFields(boolean showOnlyAddDamageFields) {
        this.showOnlyAddDamageFields = showOnlyAddDamageFields;
    }

}
