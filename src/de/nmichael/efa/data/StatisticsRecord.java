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
import java.awt.AWTEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.*;

// @i18n complete

public class StatisticsRecord extends DataRecord implements IItemListener {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String POSITION            = "Position";
    public static final String PUBLICLYAVAILABLE   = "PubliclyAvailable";
    public static final String DATEFROM            = "DateFrom";
    public static final String DATETO              = "DateTo";
    public static final String STATISTICCATEGORY   = "StatisticCategory";
    public static final String STATISTICTYPE       = "StatisticType";
    public static final String FILTERGENDER        = "FilterGender";
    public static final String FILTERSTATUS        = "FilterStatus";
    public static final String FILTERSESSIONTYPE   = "FilterSessionType";
    public static final String FILTERBOATTYPE      = "FilterBoatType";
    public static final String FILTERBOATSEATS     = "FilterBoatSeats";
    public static final String FILTERBOATRIGGING   = "FilterBoatRigging";
    public static final String FILTERBOATCOXING    = "FilterBoatCoxing";
    public static final String FILTERBYPERSONID    = "FilterByPersonId";
    public static final String FILTERBYPERSONTEXT  = "FilterByPersonText";
    public static final String FILTERBYBOATID      = "FilterByBoatId";
    public static final String FILTERBYBOATTEXT    = "FilterByBoatText";
    public static final String FILTERBYGROUPID     = "FilterByGroupId";
    public static final String SHOWFIELDS          = "ShowFields";  // like Name, Status, Gender, BoatType, ...
    public static final String AGGREGATIONS        = "Aggregations"; // like Distance, Sessions, AvgDistance, ...
    public static final String AGGRDISTANCEBARSIZE = "AggregationDistanceBarSize";
    public static final String AGGRSESSIONSBARSIZE = "AggregationSessionsBarSize";
    public static final String AGGRAVGDISTBARSIZE  = "AggregationAvgDistanceBarSize";
    public static final String COMPYEAR            = "CompYear";
    public static final String COMPPERCENTFULFILLED = "CompPercentFulfilled";
    public static final String COMPOUTPUTSHORT     = "CompOutputShort";
    public static final String COMPOUTPUTRULES     = "CompOutputRules";
    public static final String COMPOUTPUTADDITIONALWITHREQUIREMENTS = "CompOutputAdditionalWithRequirements";
    public static final String COMPOUTPUTWITHOUTDETAILS = "CompOutputWithoutDetails";
    public static final String COMPOUTPUTALLDESTINATIONAREAS = "CompOutputAllDestinationAreas";
    public static final String SORTINGCRITERIA     = "SortingCriteria";
    public static final String SORTINGORDER        = "SortingOrder";
    public static final String OUTPUTTYPE          = "OutputType";
    public static final String OUTPUTFILE          = "OutputFile";

    public static final String[] IDX_NAME = new String[] { NAME };

    // =========================================================================
    // Field Value Constants
    // =========================================================================

    public static final String SCAT_PERSONS         = "Persons";
    public static final String SCAT_BOATS           = "Boats";
    public static final String SCAT_COMPETITION     = "Competition";

    public static final String STYPE_LIST           = "List";
    public static final String STYPE_LOGBOOK        = "Logbook";

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

    public enum StatisticCategory {
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
    // Internal Variables for various pusposes
    // =========================================================================
    private ItemTypeStringList itemStatisticType;

    // =========================================================================
    // Statistic Settings (for easier access)
    // =========================================================================

    // filled by StatisticsRecord.prepareStatisticSettings()
    public DataTypeDate sStartDate;
    public DataTypeDate sEndDate;
    public long sTimestampBegin;
    public long sTimestampEnd;
    public long sValidAt;
    public StatisticCategory sStatisticCategory;
    public String sStatisticType;
    public OutputTypes sOutputType;
    public Hashtable<String,String> sFilterGender;
    public Hashtable<UUID,String> sFilterStatus;
    public boolean sFilterStatusOther;
    public Hashtable<String,String> sFilterSessionType;
    public Hashtable<String,String> sFilterBoatType;
    public Hashtable<String,String> sFilterBoatSeats;
    public Hashtable<String,String> sFilterBoatRigging;
    public Hashtable<String,String> sFilterBoatCoxing;
    public UUID sFilterByPersonId;
    public String sFilterByPersonText;
    public UUID sFilterByBoatId;
    public String sFilterByBoatText;
    public UUID sFilterByGroupId;
    public boolean sIsFieldsPosition;
    public boolean sIsFieldsName;
    public boolean sIsFieldsStatus;
    public boolean sIsFieldsYearOfBirth;
    public boolean sIsFieldsBoatType;
    public boolean sIsAggrDistance;
    public boolean sIsAggrSessions;
    public boolean sIsAggrAvgDistance;
    public boolean sIsAggrZielfahrten;
    public int sAggrDistanceBarSize;
    public int sAggrSessionsBarSize;
    public int sAggrAvgDistanceBarSize;
    public String sOutputDir;
    public String sOutputFile;
    public boolean sOutputHtmlUpdateTable = false;
    public String sFileExecBefore;
    public String sFileExecAfter;
    public int sCompYear;
    public int sCompPercentFulfilled;
    public boolean sIsOutputCompShort;
    public boolean sIsOutputCompRules;
    public boolean sIsOutputCompAdditionalWithRequirements;
    public boolean sIsOutputCompWithoutDetails;
    public boolean sIsOutputCompAllDestinationAreas;

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
        f.add(STATISTICCATEGORY);                 t.add(IDataAccess.DATA_STRING);
        f.add(STATISTICTYPE);                     t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTTYPE);                        t.add(IDataAccess.DATA_STRING);
        f.add(FILTERGENDER);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERSTATUS);                      t.add(IDataAccess.DATA_LIST_UUID);
        f.add(FILTERSESSIONTYPE);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATTYPE);                    t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATSEATS);                   t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATRIGGING);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATCOXING);                  t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBYPERSONID);                    t.add(IDataAccess.DATA_UUID);
        f.add(FILTERBYPERSONTEXT);                  t.add(IDataAccess.DATA_STRING);
        f.add(FILTERBYBOATID);                    t.add(IDataAccess.DATA_UUID);
        f.add(FILTERBYBOATTEXT);                  t.add(IDataAccess.DATA_STRING);
        f.add(FILTERBYGROUPID);                   t.add(IDataAccess.DATA_UUID);
        f.add(SHOWFIELDS);                        t.add(IDataAccess.DATA_LIST_STRING);
        f.add(AGGREGATIONS);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(AGGRDISTANCEBARSIZE);               t.add(IDataAccess.DATA_INTEGER);
        f.add(AGGRSESSIONSBARSIZE);               t.add(IDataAccess.DATA_INTEGER);
        f.add(AGGRAVGDISTBARSIZE);                t.add(IDataAccess.DATA_INTEGER);
        f.add(OUTPUTFILE);                        t.add(IDataAccess.DATA_STRING);
        f.add(COMPYEAR);                          t.add(IDataAccess.DATA_INTEGER);
        f.add(COMPPERCENTFULFILLED);              t.add(IDataAccess.DATA_INTEGER);
        f.add(COMPOUTPUTSHORT);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTRULES);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTADDITIONALWITHREQUIREMENTS); t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTWITHOUTDETAILS);          t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTALLDESTINATIONAREAS);     t.add(IDataAccess.DATA_BOOLEAN);
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
        setCompYear(DataTypeDate.today().getYear());
        setCompPercentFulfilled(100);
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

    public void setStatisticCategory(String type) {
        setString(STATISTICCATEGORY, type);
    }

    public String getStatisticCategory() {
        String s = getString(STATISTICCATEGORY);
        if (s == null || s.length() == 0) {
            return SCAT_PERSONS;
        }
        return s;
    }

    public static StatisticCategory getStatisticCategoryEnum(String type) {
        if (type == null) {
            return StatisticCategory.UNKNOWN;
        } else if (type.equals(SCAT_PERSONS)) {
            return StatisticCategory.persons;
        } else if (type.equals(SCAT_BOATS)) {
            return StatisticCategory.boats;
        } else if (type.equals(SCAT_COMPETITION)) {
            return StatisticCategory.competition;
        }
        return StatisticCategory.UNKNOWN;
    }

    public StatisticCategory getStatisticCategoryEnum() {
        String type = getStatisticCategory();
        return getStatisticCategoryEnum(type);
    }

    public String getStatisticCategoryDescription() {
        switch(getStatisticCategoryEnum()) {
            case persons:
                return International.getString("Personen");
            case boats:
                return International.getString("Boote");
            case competition:
                return International.getString("Wettbewerb");
        }
        return International.getString("unbekannt");
    }

    public String[] getStatisticCategories(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                SCAT_PERSONS,
                SCAT_BOATS,
                SCAT_COMPETITION
            };
        } else {
            return new String[] {
                International.getString("Personen"),
                International.getString("Boote"),
                International.getString("Wettbewerb")
            };
        }
    }

    public void setStatisticType(String type) {
        setString(STATISTICTYPE, type);
    }

    public String getStatisticType() {
        String s = getString(STATISTICTYPE);
        if (s == null || s.length() == 0) {
            return STYPE_LIST;
        }
        return s;
    }

    public String[] getStatisticTypes(StatisticCategory category, int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            if (category != StatisticCategory.competition) {
                return new String[]{
                            STYPE_LIST,
                            STYPE_LOGBOOK
                        };
            } else {
                return (Daten.wettDefs != null ?
                    Daten.wettDefs.getAllWettDefKeys() : new String[0]);
            }
        } else {
            if (category != StatisticCategory.competition) {
                return new String[]{
                            International.getString("Kilometerliste"),
                            International.getString("Fahrtenbuch")
                        };
            } else {
                return (Daten.wettDefs != null ?
                    Daten.wettDefs.getAllWettDefNames() : new String[0]);
            }
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

    public String getOutputTypeFileExtension() {
        switch(getOutputTypeEnum()) {
            case internal:
                return "html";
            case html:
                return "html";
            case csv:
                return "csv";
            case xml:
                return "xml";
            case efawett:
                return "efw";
        }
        return "out";
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

    public void setFilterByPersonId(UUID id) {
        setUUID(FILTERBYPERSONID, id);
    }
    public UUID getFilterByPersonId() {
        return getUUID(FILTERBYPERSONID);
    }

    public void setFilterByPersonText(String name) {
        setString(FILTERBYPERSONTEXT, name);
    }
    public String getFilterByPersonText() {
        return getString(FILTERBYPERSONTEXT);
    }

    public void setFilterByBoatId(UUID id) {
        setUUID(FILTERBYBOATID, id);
    }
    public UUID getFilterByBoatId() {
        return getUUID(FILTERBYBOATID);
    }

    public void setFilterByBoatText(String name) {
        setString(FILTERBYBOATTEXT, name);
    }
    public String getFilterByBoatText() {
        return getString(FILTERBYBOATTEXT);
    }

    public void setFilterByGroupId(UUID id) {
        setUUID(FILTERBYGROUPID, id);
    }
    public UUID getFilterByGroupId() {
        return getUUID(FILTERBYGROUPID);
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
                International.getString("Kilometer"), // @todo (P3) statistics - default unit for km
                International.getString("Fahrten"),
                International.getString("Km/Fahrt"), // @todo (P3) statistics - default unit for km
                International.onlyFor("Zielfahrten", "de")
            };
        }
    }

    public void setAggrDistanceBarSize(int size) {
        setInt(AGGRDISTANCEBARSIZE, size);
    }
    public int getAggrDistanceBarSize() {
        return getInt(AGGRDISTANCEBARSIZE);
    }

    public void setAggrSessionsBarSize(int size) {
        setInt(AGGRSESSIONSBARSIZE, size);
    }
    public int getAggrSessionsBarSize() {
        return getInt(AGGRSESSIONSBARSIZE);
    }

    public void setAggrAvgDistanceBarSize(int size) {
        setInt(AGGRAVGDISTBARSIZE, size);
    }
    public int getAggrAvgDistanceBarSize() {
        return getInt(AGGRAVGDISTBARSIZE);
    }

    public String getOutputFile() {
        return getString(OUTPUTFILE);
    }
    public void setOutputFile(String file) {
        setString(OUTPUTFILE, file);
    }

    public int getCompYear() {
        return getInt(COMPYEAR);
    }
    public void setCompYear(int year) {
        setInt(COMPYEAR, year);
    }

    public int getCompPercentFulfilled() {
        return getInt(COMPPERCENTFULFILLED);
    }
    public void setCompPercentFulfilled(int pct) {
        setInt(COMPPERCENTFULFILLED, pct);
    }

    public boolean getCompOutputShort() {
        return getBool(COMPOUTPUTSHORT);
    }
    public void setCompOutputShort(boolean bool) {
        setBool(COMPOUTPUTSHORT, bool);
    }

    public boolean getCompOutputRules() {
        return getBool(COMPOUTPUTRULES);
    }
    public void setCompOutputRules(boolean bool) {
        setBool(COMPOUTPUTRULES, bool);
    }

    public boolean getCompOutputAdditionalWithRequirements() {
        return getBool(COMPOUTPUTADDITIONALWITHREQUIREMENTS);
    }
    public void setCompOutputAdditionalWithRequirements(boolean bool) {
        setBool(COMPOUTPUTADDITIONALWITHREQUIREMENTS, bool);
    }

    public boolean getCompOutputWithoutDetails() {
        return getBool(COMPOUTPUTWITHOUTDETAILS);
    }
    public void setCompOutputWithoutDetails(boolean bool) {
        setBool(COMPOUTPUTWITHOUTDETAILS, bool);
    }

    public boolean getCompOutputAllDestinationAreas() {
        return getBool(COMPOUTPUTALLDESTINATIONAREAS);
    }
    public void setCompOutputAllDestinationAreas(boolean bool) {
        setBool(COMPOUTPUTALLDESTINATIONAREAS, bool);
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
        String CAT_COMP         = "%04%" + International.getString("Wettbewerbe");
        String CAT_OUTPUT       = "%05%" + International.getString("Ausgabe");
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
        v.add(item = new ItemTypeStringList(StatisticsRecord.STATISTICCATEGORY, getStatisticCategory(),
                    getStatisticCategories(ARRAY_STRINGLIST_VALUES), getStatisticCategories(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Statistiktyp")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeStringList(StatisticsRecord.STATISTICTYPE, getStatisticType(),
                    getStatisticTypes(getStatisticCategoryEnum(), ARRAY_STRINGLIST_VALUES),
                    getStatisticTypes(getStatisticCategoryEnum(), ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Statistikart")));
        this.itemStatisticType = (ItemTypeStringList)item;
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
        v.add(item = new ItemTypeInteger(StatisticsRecord.AGGRDISTANCEBARSIZE, getAggrDistanceBarSize(), 0, 1000,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Balkengröße") + ": " +
                International.getString("Kilometer")));
        v.add(item = new ItemTypeInteger(StatisticsRecord.AGGRSESSIONSBARSIZE, getAggrSessionsBarSize(), 0, 1000,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Balkengröße") + ": " +
                International.getString("Fahrten")));
        v.add(item = new ItemTypeInteger(StatisticsRecord.AGGRAVGDISTBARSIZE, getAggrAvgDistanceBarSize(), 0, 1000,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Balkengröße") + ": " +
                International.getString("Km/Fahrt")));

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

        v.add(item = getGuiItemTypeStringAutoComplete(StatisticsRecord.FILTERBYPERSONID, null,
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("nur Person")));
        if (getFilterByPersonId() != null) {
            ((ItemTypeStringAutoComplete)item).setId(getFilterByPersonId());
        } else {
            ((ItemTypeStringAutoComplete)item).parseAndShowValue(getFilterByPersonText());
        }
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(StatisticsRecord.FILTERBYPERSONTEXT);
        v.add(item = getGuiItemTypeStringAutoComplete(StatisticsRecord.FILTERBYBOATID, null,
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    getPersistence().getProject().getBoats(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("nur Boot")));
        if (getFilterByBoatId() != null) {
            ((ItemTypeStringAutoComplete)item).setId(getFilterByBoatId());
        } else {
            ((ItemTypeStringAutoComplete)item).parseAndShowValue(getFilterByBoatText());
        }
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(StatisticsRecord.FILTERBYBOATTEXT);
        v.add(item = getGuiItemTypeStringAutoComplete(StatisticsRecord.FILTERBYGROUPID, getFilterByGroupId(),
                    IItemType.TYPE_PUBLIC, CAT_FILTER,
                    getPersistence().getProject().getGroups(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("nur Gruppe")));

        v.add(item = new ItemTypeInteger(StatisticsRecord.COMPYEAR, getCompYear(),
                    1900, 2100,
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.getString("Wettbewerbsjahr")));
        v.add(item = new ItemTypeInteger(StatisticsRecord.COMPPERCENTFULFILLED, getCompPercentFulfilled(),
                    0, 100,
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.getString("Prozent erfüllt")));
        v.add(item = new ItemTypeBoolean(StatisticsRecord.COMPOUTPUTRULES, getCompOutputRules(),
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.getString("Wettbewerbsbedingungen ausgeben")));
        v.add(item = new ItemTypeBoolean(StatisticsRecord.COMPOUTPUTSHORT, getCompOutputShort(),
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.getString("Ausgabe im Kurzformat")));
        v.add(item = new ItemTypeBoolean(StatisticsRecord.COMPOUTPUTWITHOUTDETAILS, getCompOutputWithoutDetails(),
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.getString("Ausgabe ohne Details")));
        v.add(item = new ItemTypeBoolean(StatisticsRecord.COMPOUTPUTADDITIONALWITHREQUIREMENTS, getCompOutputAdditionalWithRequirements(),
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.getString("Ausgabe zusätzlich mit Anforderungen")));
        v.add(item = new ItemTypeBoolean(StatisticsRecord.COMPOUTPUTALLDESTINATIONAREAS, getCompOutputAllDestinationAreas(),
                    IItemType.TYPE_PUBLIC, CAT_COMP,
                    International.onlyFor("Alle Zielbereiche ausgeben", "de")));

        v.add(item = new ItemTypeFile(StatisticsRecord.OUTPUTFILE, getOutputFile(),
                International.getString("Ausgabedatei"),
                International.getString("alle Dateien"),
                null, ItemTypeFile.MODE_OPEN, ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_PUBLIC, CAT_OUTPUT, International.getString("Ausgabedatei")));

        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[6];
        header[0] = new TableItemHeader(International.getString("Nr."));
        header[1] = new TableItemHeader(International.getString("Name"));
        header[2] = new TableItemHeader(International.getString("Statistiktyp"));
        header[3] = new TableItemHeader(International.getString("Statistikart"));
        header[4] = new TableItemHeader(International.getString("Von"));
        header[5] = new TableItemHeader(International.getString("Bis"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[6];
        items[0] = new TableItem(getPosition());
        items[1] = new TableItem(getName());
        items[2] = new TableItem(getStatisticCategoryDescription());
        items[3] = new TableItem(getStatisticCategoryDescription());
        items[4] = new TableItem(getDateFrom());
        items[5] = new TableItem(getDateTo());
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

        sStatisticCategory = getStatisticCategoryEnum();
        sStatisticType = getStatisticType();
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

        sFilterByPersonId = getFilterByPersonId();
        sFilterByPersonText = (sFilterByPersonId != null ? null : getFilterByPersonText());
        sFilterByBoatId = getFilterByBoatId();
        sFilterByBoatText = (sFilterByBoatId != null ? null : getFilterByBoatText());
        sFilterByGroupId = getFilterByGroupId();

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

        sAggrDistanceBarSize = getAggrDistanceBarSize();
        sAggrSessionsBarSize = getAggrSessionsBarSize();
        sAggrAvgDistanceBarSize = getAggrAvgDistanceBarSize();

        sCompYear = getCompYear();
        sCompPercentFulfilled = getCompPercentFulfilled();
        sIsOutputCompShort = getCompOutputShort();
        sIsOutputCompRules = getCompOutputRules();
        sIsOutputCompAdditionalWithRequirements = getCompOutputAdditionalWithRequirements();
        sIsOutputCompWithoutDetails = getCompOutputWithoutDetails();
        sIsOutputCompAllDestinationAreas = this.getCompOutputAllDestinationAreas();
        if (sStatisticCategory == StatisticCategory.competition) {
            this.sIsAggrDistance = true;
            this.sIsAggrSessions = true;
            this.sIsAggrZielfahrten = true;
            this.sIsFieldsName = true;
            this.sIsFieldsStatus = true;
            this.sIsFieldsYearOfBirth = true;
        }

        sOutputFile = getOutputFile();
        if (sOutputFile == null || sOutputFile.length() == 0) {
            sOutputFile = Daten.efaTmpDirectory + Daten.fileSep + "output." + getOutputTypeFileExtension();
        }
        sOutputDir = (new File(sOutputFile)).getAbsolutePath();

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

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType.getName().equals(STATISTICCATEGORY) && event instanceof ItemEvent &&
            itemStatisticType != null) {
            String cats = itemType.getValueFromField();
            StatisticCategory cat = getStatisticCategoryEnum(cats);
            itemStatisticType.setListData(getStatisticTypes(cat, ARRAY_STRINGLIST_VALUES),
                                          getStatisticTypes(cat, ARRAY_STRINGLIST_DISPLAY));
        }
    }

}
