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
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.data.types.DataTypeList;
import de.nmichael.efa.data.types.DataTypeTime;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.statistics.StatisticsData;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class StatisticsRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String POSITION            = "Position";
    public static final String PUBLICLYAVAILABLE   = "PubliclyAvailable";
    public static final String DATEFROM            = "DateFrom";
    public static final String DATETO              = "DateTo";
    public static final String STATISTICTYPE       = "StatisticType";
    public static final String OUTPUTTYPE          = "OutputType";
    public static final String FILTERGENDER        = "FilterGender";
    public static final String FILTERSTATUS        = "FilterStatus";
    public static final String FILTERSESSIONTYPE   = "FilterSessionType";
    public static final String FILTERBOATTYPE      = "FilterBoatType";
    public static final String FILTERBOATSEATS     = "FilterBoatSeats";
    public static final String FILTERBOATRIGGING   = "FilterBoatRigging";
    public static final String FILTERBOATCOXING    = "FilterBoatCoxing";
    public static final String FILTERBYNAME        = "FilterByName";
    public static final String FILTERBYGROUP       = "FilterByGroup";
    public static final String SHOWFIELDS          = "ShowFields";  // like Name, Status, Gender, BoatType, ...
    public static final String AGGREGATIONS        = "Aggregations"; // like Distance, Sessions, AvgDistance, ...
    public static final String SORTINGCRITERIA     = "SortingCriteria";
    public static final String SORTINGORDER        = "SortingOrder";

    public static final String[] IDX_NAME = new String[] { NAME };

    // =========================================================================
    // Field Value Constants
    // =========================================================================

    public static final String STYPE_PERSONS       = "Persons";
    public static final String STYPE_BOATS         = "Boats";
    public static final String STYPE_COMPETITION   = "Competition";

    public static final String OTYPE_INTERNAL       = "Internal";
    public static final String OTYPE_HTML           = "Html";
    public static final String OTYPE_CSV            = "Csv";
    public static final String OTYPE_XML            = "Xml";
    public static final String OTYPE_EFAWETT        = "EfaWett";

    public static final String FIELDS_POSITION     = "Position";
    public static final String FIELDS_NAME         = "Name";
    public static final String FIELDS_STATUS       = "Status";
    public static final String FIELDS_YEAROFBIRTH  = "YearOfBirth";
    public static final String FIELDS_BOATTYPE     = "BoatType";

    public static final String AGGR_DISTANCE       = "Distance";
    public static final String AGGR_SESSIONS       = "Sessions";
    public static final String AGGR_AVGDISTANCE    = "AvgDistance";
    public static final String AGGR_ZIELFAHRTEN    = "Zielfahrten";

    public static final String SORTINGORDER_ASC    = "Ascending";
    public static final String SORTINGORDER_DESC   = "Descending";

    private static final int ARRAY_STRINGLIST_VALUES = 1;
    private static final int ARRAY_STRINGLIST_DISPLAY = 2;

    public enum StatisticTypes {
        UNKNOWN,
        persons,
        boats,
        competition
    }

    public enum OutputTypes {
        UNKNOWN,
        internal,
        html,
        csv,
        xml,
        efawett
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
    public OutputTypes sOutputType;
    public Hashtable<String,String> sFilterGender;
    public Hashtable<UUID,String> sFilterStatus;
    public boolean sFilterStatusOther;
    public Hashtable<String,String> sFilterSessionType;
    public Hashtable<String,String> sFilterBoatType;
    public Hashtable<String,String> sFilterBoatSeats;
    public Hashtable<String,String> sFilterBoatRigging;
    public Hashtable<String,String> sFilterBoatCoxing;
    public UUID sFilterByNameId; // @todo (P4) statistics - filter by name
    public String sFilterByNameText; // @todo (P4) statistics - filter by name
    public UUID sFilterByGroup; // @todo (P4) statistics - filter by group
    public boolean sIsFieldsPosition;
    public boolean sIsFieldsName;
    public boolean sIsFieldsStatus;
    public boolean sIsFieldsYearOfBirth;
    public boolean sIsFieldsBoatType;
    public boolean sIsAggrDistance;
    public boolean sIsAggrSessions;
    public boolean sIsAggrAvgDistance;
    public boolean sIsAggrZielfahrten;
    public boolean sIsOutputCompShort;
    public boolean sIsOutputCompRules;
    public boolean sIsOutputCompAdditionalWithRequirements;
    public boolean sIsOutputCompWithoutDetails;
    public boolean sIsOutputCompAllDestinationAreas;
    public int sAggrDistanceBarSize = 200; // @todo (P2) statistics - bar sizes
    public int sAggrSessionsBarSize = 0; // @todo (P2) statistics - bar sizes
    public int sAggrAvgDistanceBarSize = 0; // @todo (P2) statistics - bar sizes
    public String sOutputDir = Daten.efaTmpDirectory; // @todo (P2) statistics - real output file fot statistics
    public String sOutputFile = Daten.efaTmpDirectory + "output.html"; // @todo (P2) statistics - real output file fot statistics
    public boolean sOutputHtmlUpdateTable = false;
    public String sFileExecBefore;
    public String sFileExecAfter;
    public int sCompYear;
    public int sCompPercentFulfilled;

    // filled during statistics creation in StatistikTask
    public int cNumberOfEntries = 0;
    public DataTypeIntString cEntryNoFirst;
    public DataTypeIntString cEntryNoLast;
    public DataTypeDate cEntryDateFirst;
    public DataTypeDate cEntryDateLast;

    // filled by StatistikTask.runPostprocessing()
    public BaseDialog pParentDialog;
    public String pStatTitle;
    public String pStatCreationDate;
    public String pStatCreatedByUrl;
    public String pStatCreatedByName;
    public String pStatDescription;
    public String pStatDateRange;
    public String pStatConsideredEntries;
    public Vector<String> pTableColumns;

    // filled by Competition.calculate()
    public String[] pCompRules;
    public Hashtable pCompRulesBold = new Hashtable();
    public Hashtable pCompRulesItalics = new Hashtable();
    public String pCompWarning;
    public String[][] pCompGroupNames;
    public StatisticsData[] pCompParticipants;

    public String[][] pAdditionalTable;



    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(POSITION);                          t.add(IDataAccess.DATA_INTEGER);
        f.add(PUBLICLYAVAILABLE);                 t.add(IDataAccess.DATA_BOOLEAN);
        f.add(DATEFROM);                          t.add(IDataAccess.DATA_DATE);
        f.add(DATETO);                            t.add(IDataAccess.DATA_DATE);
        f.add(STATISTICTYPE);                     t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTTYPE);                        t.add(IDataAccess.DATA_STRING);
        f.add(FILTERGENDER);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERSTATUS);                      t.add(IDataAccess.DATA_LIST_UUID);
        f.add(FILTERSESSIONTYPE);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATTYPE);                    t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATSEATS);                   t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATRIGGING);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATCOXING);                  t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBYNAME);                      t.add(IDataAccess.DATA_STRING);
        f.add(FILTERBYGROUP);                     t.add(IDataAccess.DATA_STRING);
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

    public void setDefaults() {
        setFilterGender(new DataTypeList<String>(Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterStatus(new DataTypeList<UUID>(getFilterStatusListValues()));
        setFilterSessionType(new DataTypeList<String>(Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatType(new DataTypeList<String>(Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatSeats(new DataTypeList<String>(Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatRigging(new DataTypeList<String>(Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatCoxing(new DataTypeList<String>(Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
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

    public void setPosition(int position) {
        setInt(POSITION, position);
    }
    public int getPosition() {
        int position = getInt(POSITION);
        if (position < 0) {
            return 0;
        }
        return position;
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
            return STYPE_PERSONS;
        }
        return s;
    }

    public StatisticTypes getStatisticTypeEnum() {
        String type = getStatisticType();
        if (type == null) {
            return StatisticTypes.UNKNOWN;
        } else if (type.equals(STYPE_PERSONS)) {
            return StatisticTypes.persons;
        } else if (type.equals(STYPE_BOATS)) {
            return StatisticTypes.boats;
        } else if (type.equals(STYPE_COMPETITION)) {
            return StatisticTypes.competition;
        }
        return StatisticTypes.UNKNOWN;
    }

    public String getStatisticTypeDescription() {
        switch(getStatisticTypeEnum()) {
            case persons:
                return International.getString("Personen");
            case boats:
                return International.getString("Boote");
            case competition:
                return International.getString("Wettbewerb");
        }
        return International.getString("unbekannt");
    }

    public String[] getStatisticTypes(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                STYPE_PERSONS,
                STYPE_BOATS,
                STYPE_COMPETITION
            };
        } else {
            return new String[] {
                International.getString("Personen"),
                International.getString("Boote"),
                International.getString("Wettbewerb")
            };
        }
    }

    public void setOutputType(String type) {
        setString(OUTPUTTYPE, type);
    }

    public String getOutputType() {
        String s = getString(OUTPUTTYPE);
        if (s == null || s.length() == 0) {
            return OTYPE_INTERNAL;
        }
        return s;
    }

    public OutputTypes getOutputTypeEnum() {
        String type = getOutputType();
        if (type == null) {
            return OutputTypes.UNKNOWN;
        } else if (type.equals(OTYPE_INTERNAL)) {
            return OutputTypes.internal;
        } else if (type.equals(OTYPE_HTML)) {
            return OutputTypes.html;
        } else if (type.equals(OTYPE_CSV)) {
            return OutputTypes.csv;
        } else if (type.equals(OTYPE_XML)) {
            return OutputTypes.xml;
        } else if (type.equals(OTYPE_EFAWETT)) {
            return OutputTypes.efawett;
        }
        return OutputTypes.UNKNOWN;
    }

    public String getOutputTypeDescription() {
        switch(getOutputTypeEnum()) {
            case internal:
                return International.getString("intern");
            case html:
                return International.getString("HTML");
            case csv:
                return International.getString("CSV");
            case xml:
                return International.getString("XML");
            case efawett:
                return International.getString("efaWett");
        }
        return International.getString("unbekannt");
    }

    public String[] getOutputTypes(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                OTYPE_INTERNAL,
                OTYPE_HTML,
                OTYPE_CSV,
                OTYPE_XML,
                OTYPE_EFAWETT
            };
        } else {
            return new String[] {
                International.getString("intern"),
                International.getString("HTML"),
                International.getString("CSV"),
                International.getString("XML"),
                International.getString("efaWett"),

            };
        }
    }

    public void setFilterGender(DataTypeList<String> list) {
        setList(FILTERGENDER, list);
    }

    public DataTypeList<String> getFilterGender() {
        return getList(FILTERGENDER, IDataAccess.DATA_STRING);
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

    public void setFilterSessionType(DataTypeList<String> list) {
        setList(FILTERSESSIONTYPE, list);
    }

    public DataTypeList<String> getFilterSessionType() {
        return getList(FILTERSESSIONTYPE, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatType(DataTypeList<String> list) {
        setList(FILTERBOATTYPE, list);
    }

    public DataTypeList<String> getFilterBoatType() {
        return getList(FILTERBOATTYPE, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatSeats(DataTypeList<String> list) {
        setList(FILTERBOATSEATS, list);
    }

    public DataTypeList<String> getFilterBoatSeats() {
        return getList(FILTERBOATSEATS, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatRigging(DataTypeList<String> list) {
        setList(FILTERBOATRIGGING, list);
    }

    public DataTypeList<String> getFilterBoatRigging() {
        return getList(FILTERBOATRIGGING, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatCoxing(DataTypeList<String> list) {
        setList(FILTERBOATCOXING, list);
    }

    public DataTypeList<String> getFilterBoatCoxing() {
        return getList(FILTERBOATCOXING, IDataAccess.DATA_STRING);
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
                FIELDS_POSITION,
                FIELDS_NAME,
                FIELDS_STATUS,
                FIELDS_YEAROFBIRTH,
                FIELDS_BOATTYPE
            };
        } else {
            return new String[] {
                International.getString("Position"),
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
                AGGR_AVGDISTANCE,
                AGGR_ZIELFAHRTEN
            };
        } else {
            return new String[] {
                International.getString("Kilometer"), // @todo (P3) default unit for km
                International.getString("Fahrten"),
                International.getString("Km/Fahrt"), // @todo (P3) default unit for km
                International.onlyFor("Zielfahrten", "de")
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
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        // CAT_BASEDATA
        v.add(item = new ItemTypeInteger(StatisticsRecord.POSITION, getPosition(), 0, Integer.MAX_VALUE,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Position")));
        item.setNotNull(true);
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
        v.add(item = new ItemTypeStringList(StatisticsRecord.OUTPUTTYPE, getOutputType(),
                    getOutputTypes(ARRAY_STRINGLIST_VALUES), getOutputTypes(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Ausgabeart")));
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
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERGENDER, getFilterGender(),
                    Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Geschlecht")));
        v.add(item = new ItemTypeMultiSelectList<UUID>(StatisticsRecord.FILTERSTATUS, getFilterStatus(),
                    getFilterStatusListValues(), getFilterStatusListDisplay(),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Status")));
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERSESSIONTYPE, getFilterSessionType(),
                    Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Fahrtart")));
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATTYPE, getFilterBoatType(),
                    Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Bootstyp")));
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATSEATS, getFilterBoatSeats(),
                    Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Bootsplätze")));
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATRIGGING, getFilterBoatRigging(),
                    Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Riggerung")));
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATCOXING, getFilterBoatCoxing(),
                    Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    International.getString("Steuerung")));
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[5];
        header[0] = new TableItemHeader(International.getString("Nr."));
        header[1] = new TableItemHeader(International.getString("Name"));
        header[2] = new TableItemHeader(International.getString("Statistikart"));
        header[3] = new TableItemHeader(International.getString("Von"));
        header[4] = new TableItemHeader(International.getString("Bis"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[5];
        items[0] = new TableItem(getPosition());
        items[1] = new TableItem(getName());
        items[2] = new TableItem(getStatisticTypeDescription());
        items[3] = new TableItem(getDateFrom());
        items[4] = new TableItem(getDateTo());
        return items;
    }

    public void prepareStatisticSettings() {
        sStartDate = getDateFrom();
        if (sStartDate == null || !sStartDate.isSet()) {
            sStartDate = DataTypeDate.today();
            sStartDate.setDay(1);
            sStartDate.setMonth(1);
        }

        sEndDate = getDateTo();
        if (sEndDate == null || !sEndDate.isSet()) {
            sEndDate = DataTypeDate.today();
        }

        sStatisticType = getStatisticTypeEnum();
        sOutputType = getOutputTypeEnum();

        sFilterGender = new Hashtable<String,String>();
        DataTypeList<String> listString = getFilterGender();
        for (int i=0; listString != null && i<listString.length(); i++) {
            sFilterGender.put(listString.get(i), "foo");
        }

        sFilterStatus = new Hashtable<UUID,String>();
        DataTypeList<UUID> listUUID = getFilterStatus();
        for (int i=0; listUUID != null && i<listUUID.length(); i++) {
            sFilterStatus.put(listUUID.get(i), "foo");
        }
        try {
            sFilterStatusOther = sFilterStatus.containsKey(getPersistence().getProject().getStatus(false).getStatusOther().getId());
        } catch(Exception eignore) {
            sFilterStatusOther = false;
        }

        sFilterSessionType = new Hashtable<String,String>();
        listString = getFilterSessionType();
        for (int i=0; listString != null && i<listString.length(); i++) {
            sFilterSessionType.put(listString.get(i), "foo");
        }

        sFilterBoatType = new Hashtable<String,String>();
        listString = getFilterBoatType();
        for (int i=0; listString != null && i<listString.length(); i++) {
            sFilterBoatType.put(listString.get(i), "foo");
        }

        sFilterBoatSeats = new Hashtable<String,String>();
        listString = getFilterBoatSeats();
        for (int i=0; listString != null && i<listString.length(); i++) {
            sFilterBoatSeats.put(listString.get(i), "foo");
        }

        sFilterBoatRigging = new Hashtable<String,String>();
        listString = getFilterBoatRigging();
        for (int i=0; listString != null && i<listString.length(); i++) {
            sFilterBoatRigging.put(listString.get(i), "foo");
        }

        sFilterBoatCoxing = new Hashtable<String,String>();
        listString = getFilterBoatCoxing();
        for (int i=0; listString != null && i<listString.length(); i++) {
            sFilterBoatCoxing.put(listString.get(i), "foo");
        }

        DataTypeList<String> fields = getShowFields();
        for (int i=0; fields != null && i<fields.length(); i++) {
            String s = fields.get(i);
            if (s.equals(FIELDS_POSITION)) {
                sIsFieldsPosition = true;
            } else if (s.equals(FIELDS_NAME)) {
                sIsFieldsName = true;
            } else if (s.equals(FIELDS_STATUS)) {
                sIsFieldsStatus = true;
            } else if (s.equals(FIELDS_YEAROFBIRTH)) {
                sIsFieldsYearOfBirth = true;
            } else if (s.equals(FIELDS_BOATTYPE)) {
                sIsFieldsBoatType = true;
            }
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
            } else if (s.equals(AGGR_ZIELFAHRTEN)) {
                sIsAggrZielfahrten = true;
            }
        }

        sCompYear = 2011; // @todo (P2) - statistics comp
        sCompPercentFulfilled = 10; // @todo (P2) - statistics comp
        sIsOutputCompShort = false; // @todo (P2) - statistics comp
        sIsOutputCompRules = false; // @todo (P2) - statistics comp
        sIsOutputCompAdditionalWithRequirements = false; // @todo (P2) - statistics comp
        sIsOutputCompWithoutDetails = true; // @todo (P2) - statistics comp
        sIsOutputCompAllDestinationAreas = false; // @todo (P2) - statistics comp
        if (sStatisticType == StatisticTypes.competition) {
            this.sIsAggrDistance = true;
            this.sIsAggrSessions = true;
            this.sIsAggrZielfahrten = true;
            this.sIsFieldsName = true;
            this.sIsFieldsStatus = true;
            this.sIsFieldsYearOfBirth = true;
        }


        sTimestampBegin = sStartDate.getTimestamp(new DataTypeTime(0,0,0));
        sTimestampEnd   = sEndDate.getTimestamp(new DataTypeTime(23,59,59));
        sValidAt = sEndDate.getTimestamp(new DataTypeTime(23,59,59));
    }

    public void prepareTableColumns() {
        pTableColumns = new Vector<String>();
        if (sIsFieldsPosition) {
            pTableColumns.add(International.getString("Platz"));
        }
        if (sIsFieldsName) {
            pTableColumns.add(International.getString("Name"));
        }
        if (sIsFieldsStatus) {
            pTableColumns.add(International.getString("Status"));
        }
        if (sIsFieldsYearOfBirth) {
            pTableColumns.add(International.getString("Jahrgang"));
        }
        if (sIsFieldsBoatType) {
            pTableColumns.add(International.getString("Bootstyp"));
        }
        if (sIsAggrDistance) {
            pTableColumns.add(DataTypeDistance.getDefaultUnitName());
        }
        if (sIsAggrSessions) {
            pTableColumns.add(International.getString("Fahrten"));
        }
        if (sIsAggrAvgDistance) {
            pTableColumns.add(DataTypeDistance.getDefaultUnitAbbrevation() + "/" + International.getString("Fahrt"));
        }
        if (sIsAggrZielfahrten) {
            pTableColumns.add(International.getString("Zielfahrten"));
        }
    }

}
