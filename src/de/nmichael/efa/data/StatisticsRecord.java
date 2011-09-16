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

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.data.types.DataTypeList;
import de.nmichael.efa.data.types.DataTypeTime;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class StatisticsRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String PUBLICLYAVAILABLE   = "PubliclyAvailable";
    public static final String DATEFROM            = "DateFrom";
    public static final String DATETO              = "DateTo";
    public static final String STATISTICTYPE       = "StatisticType";
    public static final String FILTERGENDER        = "FilterGender";
    public static final String FILTERSTATUS        = "FilterStatus";
    public static final String FILTERSESSIONTYPE   = "FilterSessionType";
    public static final String FILTERBOATTYPE      = "FilterBoatType";
    public static final String FILTERBOATSEATS     = "FilterBoatSeats";
    public static final String FILTERBOATRIGGING   = "FilterBoatRigging";
    public static final String FILTERBOATCOXING    = "FilterBoatCoxing";
    public static final String FILTERBYNAME        = "FilterByName";
    public static final String SHOWFIELDS          = "ShowFields";  // like Name, Status, Gender, BoatType, ...
    public static final String AGGREGATIONS        = "Aggregations"; // like Distance, Sessions, AvgDistance, ...
    public static final String SORTINGCRITERIA     = "SortingCriteria";
    public static final String SORTINGORDER        = "SortingOrder";

    public static final String[] IDX_NAME = new String[] { NAME };

    // =========================================================================
    // Field Value Constants
    // =========================================================================

    public static final String TYPE_PERSONS        = "Persons";
    public static final String TYPE_BOATS          = "Boats";

    public static final String FIELDS_NAME         = "Name";
    public static final String FIELDS_STATUS       = "Status";
    public static final String FIELDS_YEAROFBIRTH  = "YearOfBirth";
    public static final String FIELDS_BOATTYPE     = "BoatType";

    public static final String AGGR_DISTANCE       = "Distance";
    public static final String AGGR_SESSIONS       = "Sessions";
    public static final String AGGR_AVGDISTANCE    = "AvgDistance";

    public static final String SORTINGORDER_ASC    = "Ascending";
    public static final String SORTINGORDER_DESC   = "Descending";

    private static final int ARRAY_STRINGLIST_VALUES = 1;
    private static final int ARRAY_STRINGLIST_DISPLAY = 2;

    public enum StatisticTypes {
        UNKNOWN,
        persons,
        boats
    }

    // =========================================================================
    // Statistic Settings (for easier access)
    // =========================================================================

    // filled by StatisticsRecord.prepareStatisticSettings()
    public DataTypeDate sStartDate;
    public DataTypeDate sEndDate;
    public long sTimestampBegin;
    public long sTimestampEnd;
    public long sValidAt;
    public StatisticTypes sStatisticType;
    public Hashtable<UUID,String> sFilterStatus;
    public boolean sIsFieldsName;
    public boolean sIsFieldsStatus;
    public boolean sIsFieldsYearOfBirth;
    public boolean sIsFieldsBoatType;
    public boolean sIsAggrDistance;
    public boolean sIsAggrSessions;
    public boolean sIsAggrAvgDistance;
    public String sOutputDir = Daten.efaTmpDirectory; // @todo (P2) real output file fot statistics
    public String sOutputFile = Daten.efaTmpDirectory + "output.html"; // @todo (P2) real output file fot statistics
    public boolean sOutputHtmlUpdateTable = false;
    public String sFileExecBefore;
    public String sFileExecAfter;

    // filled during statistics creation in StatistikTask
    public int cNumberOfEntries = 0;
    public DataTypeIntString cEntryNoFirst;
    public DataTypeIntString cEntryNoLast;
    public DataTypeDate cEntryDateFirst;
    public DataTypeDate cEntryDateLast;

    public String[] tableColumns;

    // filled by StatistikTask.runPostprocessing()
    public BaseDialog pParentDialog;
    public String pStatTitle;
    public String pStatCreationDate;
    public String pStatCreatedByUrl;
    public String pStatCreatedByName;
    public String pStatDescription;
    public String pStatDateRange;
    public String pStatConsideredEntries;



    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(PUBLICLYAVAILABLE);                 t.add(IDataAccess.DATA_BOOLEAN);
        f.add(DATEFROM);                          t.add(IDataAccess.DATA_DATE);
        f.add(DATETO);                            t.add(IDataAccess.DATA_DATE);
        f.add(STATISTICTYPE);                     t.add(IDataAccess.DATA_STRING);
        f.add(FILTERGENDER);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERSTATUS);                      t.add(IDataAccess.DATA_LIST_UUID);
        f.add(FILTERSESSIONTYPE);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATTYPE);                    t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATSEATS);                   t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATRIGGING);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATCOXING);                  t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBYNAME);                      t.add(IDataAccess.DATA_STRING);
        f.add(SHOWFIELDS);                        t.add(IDataAccess.DATA_LIST_STRING);
        f.add(AGGREGATIONS);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(SORTINGCRITERIA);                   t.add(IDataAccess.DATA_STRING);
        f.add(SORTINGORDER);                      t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Statistics.DATATYPE, f, t, false);
        metaData.setKey(new String[] { ID });
        metaData.addIndex(IDX_NAME);
    }

    public StatisticsRecord(Statistics statistics, MetaData metaData) {
        super(statistics, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getId(),null,null);
    }

    public static DataKey getKey(UUID id) {
        return new DataKey<UUID,String,String>(id,null,null);
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
        String s = getString(NAME);
        if (s == null || s.length() == 0) {
            return International.getString("Standard");
        }
        return s;
    }

    public void setPubliclyAvailable(boolean publiclyAvailable) {
        setBool(PUBLICLYAVAILABLE, publiclyAvailable);
    }

    public boolean getPubliclyAvailable() {
        return getBool(PUBLICLYAVAILABLE);
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

    public void setStatisticType(String type) {
        setString(STATISTICTYPE, type);
    }

    public String getStatisticType() {
        String s = getString(STATISTICTYPE);
        if (s == null || s.length() == 0) {
            return TYPE_PERSONS;
        }
        return s;
    }

    public StatisticTypes getStatisticTypeEnum() {
        String type = getStatisticType();
        if (type == null) {
            return StatisticTypes.UNKNOWN;
        } else if (type.equals(TYPE_PERSONS)) {
            return StatisticTypes.persons;
        } else if (type.equals(TYPE_BOATS)) {
            return StatisticTypes.boats;
        }
        return StatisticTypes.UNKNOWN;
    }

    public String getStatisticTypeDescription() {
        switch(getStatisticTypeEnum()) {
            case persons:
                return International.getString("Personen");
            case boats:
                return International.getString("Boote");
        }
        return International.getString("unbekannt");
    }

    public String[] getStatisticTypes(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                TYPE_PERSONS,
                TYPE_BOATS
            };
        } else {
            return new String[] {
                International.getString("Personen"),
                International.getString("Boote")
            };
        }
    }

    public void setFilterStatus(DataTypeList<UUID> list) {
        setList(FILTERSTATUS, list);
    }

    public DataTypeList<UUID> getFilterStatus() {
        return getList(FILTERSTATUS, IDataAccess.DATA_UUID);
    }

    public UUID[] getFilterStatusListValues() {
        return getPersistence().getProject().getStatus(false).makeStatusArrayUUID();
    }

    public String[] getFilterStatusListDisplay() {
        return getPersistence().getProject().getStatus(false).makeStatusArray(Status.ARRAY_STRINGLIST_DISPLAY);
    }

    public void setShowFields(DataTypeList<String> list) {
        setList(SHOWFIELDS, list);
    }

    public DataTypeList<String> getShowFields() {
        return getList(SHOWFIELDS, IDataAccess.DATA_STRING);
    }

    public String[] getFieldsList(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                FIELDS_NAME,
                FIELDS_STATUS,
                FIELDS_YEAROFBIRTH,
                FIELDS_BOATTYPE
            };
        } else {
            return new String[] {
                International.getString("Name"),
                International.getString("Status"),
                International.getString("Jahrgang"),
                International.getString("Bootstyp")
            };
        }
    }

    public void setAggregations(DataTypeList<String> list) {
        setList(AGGREGATIONS, list);
    }

    public DataTypeList<String> getAggregations() {
        return getList(AGGREGATIONS, IDataAccess.DATA_STRING);
    }

    public String[] getAggregationList(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                AGGR_DISTANCE,
                AGGR_SESSIONS,
                AGGR_AVGDISTANCE
            };
        } else {
            return new String[] {
                International.getString("Kilometer"), // @todo (P3) default unit for km
                International.getString("Fahrten"),
                International.getString("Km/Fahrt") // @todo (P3) default unit for km
            };
        }
    }

    public String[] getQualifiedNameFields() {
        return IDX_NAME;
    }

    public Object getUniqueIdForRecord() {
        return getId();
    }

    public String getQualifiedName() {
        String name = getName();
        return (name != null ? name : "");
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Statistik");
        String CAT_FIELDS       = "%02%" + International.getString("Felder");
        String CAT_FILTER       = "%03%" + International.getString("Filter");
        String CAT_F_STATUS     = "%031%" + International.getString("Status");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        // CAT_BASEDATA
        v.add(item = new ItemTypeString(StatisticsRecord.NAME, getName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Name")));
        item.setNotNull(true);
        v.add(item = new ItemTypeBoolean(StatisticsRecord.PUBLICLYAVAILABLE, getPubliclyAvailable(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Statistik allgemein verfügbar")));
        v.add(item = new ItemTypeDate(StatisticsRecord.DATEFROM, getDateFrom(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Von")));
        ItemTypeDate dateFrom = (ItemTypeDate)item;
        v.add(item = new ItemTypeDate(StatisticsRecord.DATETO, getDateTo(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Bis")));
        ((ItemTypeDate)item).setMustBeAfter(dateFrom, true);
        v.add(item = new ItemTypeStringList(StatisticsRecord.STATISTICTYPE, getStatisticType(),
                    getStatisticTypes(ARRAY_STRINGLIST_VALUES), getStatisticTypes(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Statistikart")));
        item.setNotNull(true);
        
        // CAT_FIELDS
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.SHOWFIELDS, getShowFields(),
                    getFieldsList(ARRAY_STRINGLIST_VALUES), getFieldsList(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FIELDS,
                    International.getString("Ausgabe")));
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.AGGREGATIONS, getAggregations(),
                    getAggregationList(ARRAY_STRINGLIST_VALUES), getAggregationList(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FIELDS,
                    International.getString("Berechnung")));

        // CAT_FILTER
        v.add(item = new ItemTypeMultiSelectList<UUID>(StatisticsRecord.FILTERSTATUS, getFilterStatus(),
                    getFilterStatusListValues(), getFilterStatusListDisplay(),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Status")));
        return v;
    }

    public static String makeCategory(String c1, String c2) {
        return c1 + ":" + c2;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[4];
        header[0] = new TableItemHeader(International.getString("Name"));
        header[1] = new TableItemHeader(International.getString("Statistikart"));
        header[2] = new TableItemHeader(International.getString("Von"));
        header[3] = new TableItemHeader(International.getString("Bis"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[4];
        items[0] = new TableItem(getName());
        items[1] = new TableItem(getStatisticTypeDescription());
        items[2] = new TableItem(getDateFrom());
        items[3] = new TableItem(getDateTo());
        return items;
    }

    public void prepareStatisticSettings() {
        sStartDate = getDateFrom();
        if (sStartDate == null || !sStartDate.isSet()) {
            sStartDate = DataTypeDate.today();
        }

        sEndDate = getDateTo();
        if (sEndDate == null || !sEndDate.isSet()) {
            sEndDate = DataTypeDate.today();
        }

        sStatisticType = getStatisticTypeEnum();

        sFilterStatus = new Hashtable<UUID,String>();
        DataTypeList<UUID> listUUID = getFilterStatus();
        for (int i=0; listUUID != null && i<listUUID.length(); i++) {
            sFilterStatus.put(listUUID.get(i), "foo");
        }

        DataTypeList<String> aggr = getAggregations();
        for (int i=0; aggr != null && i<aggr.length(); i++) {
            String s = aggr.get(i);
            if (s.equals(AGGR_DISTANCE)) {
                sIsAggrDistance = true;
            } else if (s.equals(AGGR_SESSIONS)) {
                sIsAggrSessions = true;
            } else if (s.equals(AGGR_AVGDISTANCE)) {
                sIsAggrAvgDistance = true;
            }
        }

        sTimestampBegin = sStartDate.getTimestamp(new DataTypeTime(0,0,0));
        sTimestampEnd   = sEndDate.getTimestamp(new DataTypeTime(23,59,59));
        sValidAt = sEndDate.getTimestamp(new DataTypeTime(23,59,59));
    }

}
