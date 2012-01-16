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
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.data.efawett.WettDefs;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.data.types.DataTypeList;
import de.nmichael.efa.data.types.DataTypeTime;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.gui.BaseTabbedDialog;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.statistics.*;
import de.nmichael.efa.util.*;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
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
    public static final String STATISTICKEY        = "StatisticKey";
    public static final String FILTERGENDER        = "FilterGender";
    public static final String FILTERGENDERALL     = "FilterGenderAll";
    public static final String FILTERSTATUS        = "FilterStatus";
    public static final String FILTERSTATUSALL     = "FilterStatusAll";
    public static final String FILTERSESSIONTYPE   = "FilterSessionType";
    public static final String FILTERSESSIONTYPEALL= "FilterSessionTypeAll";
    public static final String FILTERBOATTYPE      = "FilterBoatType";
    public static final String FILTERBOATTYPEALL   = "FilterBoatTypeAll";
    public static final String FILTERBOATSEATS     = "FilterBoatSeats";
    public static final String FILTERBOATSEATSALL  = "FilterBoatSeatsAll";
    public static final String FILTERBOATRIGGING   = "FilterBoatRigging";
    public static final String FILTERBOATRIGGINGALL= "FilterBoatRiggingAll";
    public static final String FILTERBOATCOXING    = "FilterBoatCoxing";
    public static final String FILTERBOATCOXINGALL = "FilterBoatCoxingAll";
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
    public static final String OUTPUTENCODING      = "OutputEncoding";
    public static final String OUTPUTHTMLUPDATETABLE = "OutputHtmlUpdateTable";
    public static final String OUTPUTCSVSEPARATOR  = "OutputCsvSeparator";
    public static final String OUTPUTCSVQUOTES     = "OutputCsvQuotes";
    public static final String OPTIONDISTANCEWITHUNIT = "OptionDistanceWithUnit";
    public static final String OPTIONTRUNCATEDIST  = "OptionTruncateDistance";


    public static final String[] IDX_NAME = new String[] { NAME };

    // =========================================================================
    // Various GUI-related Fields
    // =========================================================================
    public static boolean TABLE_HEADER_LONG = true;

    // =========================================================================
    // Field Value Constants
    // =========================================================================

    public static final String SCAT_LIST            = "List";
    public static final String SCAT_LOGBOOK         = "Logbook";
    public static final String SCAT_COMPETITION     = "Competition";

    public static final String STYPE_PERSONS        = "Persons";
    public static final String STYPE_BOATS          = "Boats";

    public static final String SKEY_NAME            = "Name";            // based on Persons or Boats
    public static final String SKEY_STATUS          = "Status";          // based on Persons
    public static final String SKEY_YEAROFBIRTH     = "YearOfBirth";     // based on Persons
    public static final String SKEY_GENDER          = "Gender";          // based on Persons
    public static final String SKEY_BOATTYPE        = "BoatType";        // based on Boats
    public static final String SKEY_BOATSEATS       = "BoatSeats";       // based on Boats
    public static final String SKEY_BOATTYPEDETAIL  = "BoatTypeDetail";  // based on Boats
    public static final String SKEY_DESTINATION     = "Destination";     // based on Persons or Boats
    public static final String SKEY_DISTANCE        = "Distance";        // based on Persons or Boats
    public static final String SKEY_MONTH           = "Month";           // based on Persons or Boats
    public static final String SKEY_WEEKDAY         = "Weekday";         // based on Persons or Boats
    public static final String SKEY_TIMEOFDAY       = "TimeOfDay";       // based on Persons or Boats
    public static final String SKEY_SESSIONTYPE     = "SessionType";     // based on Persons or Boats
    public static final String SKEY_YEAR            = "Year";            // based on Persons or Boats

    public static final String OTYPE_INTERNAL       = "Internal";
    public static final String OTYPE_HTML           = "Html";
    public static final String OTYPE_PDF            = "Pdf";
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
    public static final String AGGR_WANDERFAHRTEN  = "Wanderfahrten";

    public static final String SORTING_DISTANCE    = "Distance";
    public static final String SORTING_SESSIONS    = "Sessions";
    public static final String SORTING_AVGDISTANCE = "AvgDistance";
    public static final String SORTING_NAME        = "Name";
    public static final String SORTING_STATUS      = "Status";
    public static final String SORTING_YEAROFBIRTH = "YearOfBirth";
    public static final String SORTING_BOATTYPE    = "BoatType";
    public static final String SORTING_ENTRYNO     = "EntryNo";
    public static final String SORTING_DATE        = "Date";

    public static final String SORTINGORDER_ASC    = "Ascending";
    public static final String SORTINGORDER_DESC   = "Descending";

    private static final int ARRAY_STRINGLIST_VALUES = 1;
    private static final int ARRAY_STRINGLIST_DISPLAY = 2;

    public enum StatisticCategory {
        UNKNOWN,
        list,
        logbook,
        competition
    }

    public enum StatisticType {
        persons,
        boats,
        anythingElse // if this is selected, it's most likely a competition; refer to the String value
    }

    public enum StatisticKey {
        name,
        status,
        yearOfBirth,
        gender,
        boatType,
        boatSeats,
        boatTypeDetail,
        destination,
        distance,
        month,
        weekday,
        timeOfDay,
        sessionType,
        year
    }

    public enum SortingCriteria {
        UNKNOWN,
        distance,
        sessions,
        avgDistance,
        name,
        status,
        yearOfBirth,
        boatType,
        entryNo,
        date
    }

    public enum OutputTypes {
        UNKNOWN,
        internal,
        html,
        csv,
        xml,
        pdf,
        efawett
    }

    // =========================================================================
    // Internal Variables for various pusposes
    // =========================================================================
    private ItemTypeStringList itemStatisticType;
    private ItemTypeStringList itemStatisticKey;
    private ItemTypeMultiSelectList<String> itemFilterGender;
    private ItemTypeMultiSelectList<String> itemFilterStatus;
    private ItemTypeMultiSelectList<String> itemFilterSessionType;
    private ItemTypeMultiSelectList<String> itemFilterBoatType;
    private ItemTypeMultiSelectList<String> itemFilterBoatSeats;
    private ItemTypeMultiSelectList<String> itemFilterBoatRigging;
    private ItemTypeMultiSelectList<String> itemFilterBoatCoxing;
    private ItemTypeFile itemOutputFile;
    private ItemTypeStringList itemOutputEncoding;
    private ItemTypeBoolean itemOutputHtmlUpdateTable;
    private ItemTypeString itemOutputCsvSeparator;
    private ItemTypeString itemOutputCsvQuotes;

    // =========================================================================
    // Statistic Settings (for easier access)
    // =========================================================================

    // filled by StatisticsRecord.prepareStatisticSettings()
    // --- Statistic Settings
    public DataTypeDate sStartDate;
    public DataTypeDate sEndDate;
    public long sTimestampBegin;
    public long sTimestampEnd;
    public long sValidAt;
    public StatisticCategory sStatisticCategory;
    public String sStatisticType;
    public StatisticType sStatisticTypeEnum;
    public StatisticKey sStatistikKey;
    // --- Filter Settings
    public Hashtable<String,String> sFilterGender;
    public Hashtable<UUID,String> sFilterStatus;
    public boolean sFilterStatusOther;
    public Hashtable<String,String> sFilterSessionType;
    public boolean sFilterSessionTypeAll;
    public Hashtable<String,String> sFilterBoatType;
    public Hashtable<String,String> sFilterBoatSeats;
    public Hashtable<String,String> sFilterBoatRigging;
    public Hashtable<String,String> sFilterBoatCoxing;
    public UUID sFilterByPersonId;
    public String sFilterByPersonText;
    public UUID sFilterByBoatId;
    public String sFilterByBoatText;
    public UUID sFilterByGroupId;
    // --- Field Settings
    public boolean sIsFieldsPosition;
    public boolean sIsFieldsName;
    public boolean sIsFieldsStatus;
    public boolean sIsFieldsYearOfBirth;
    public boolean sIsFieldsBoatType;
    public boolean sIsAggrDistance;
    public boolean sIsAggrSessions;
    public boolean sIsAggrAvgDistance;
    public boolean sIsAggrZielfahrten;
    public boolean sIsAggrWanderfahrten;
    public boolean sIsAggrWinterfahrten;
    public boolean sIsAggrGigfahrten;
    public int sAggrDistanceBarSize;
    public int sAggrSessionsBarSize;
    public int sAggrAvgDistanceBarSize;
    // --- Sorting Settings
    public SortingCriteria sSortingCriteria;
    public boolean sSortingOrderAscending;
    // --- Output Settings
    public OutputTypes sOutputType;
    public String sOutputDir;
    public String sOutputFile;
    public String sOutputEncoding;
    public boolean sOutputHtmlUpdateTable;
    public String sOutputCsvSeparator;
    public String sOutputCsvQuotes;
    public String sFileExecBefore;
    public String sFileExecAfter;
    // --- Competition Settings
    public int sCompYear;
    public int sCompPercentFulfilled;
    public boolean sIsOutputCompShort;
    public boolean sIsOutputCompRules;
    public boolean sIsOutputCompAdditionalWithRequirements;
    public boolean sIsOutputCompWithoutDetails;
    public boolean sIsOutputCompAllDestinationAreas;
    // --- Options
    public boolean sDistanceWithUnit;
    public boolean sTruncateDistanceToFullValue;

    // filled during statistics creation in StatistikTask
    public int cNumberOfEntries = 0;
    public DataTypeIntString cEntryNoFirst;
    public DataTypeIntString cEntryNoLast;
    public DataTypeDate cEntryDateFirst;
    public DataTypeDate cEntryDateLast;
    public Hashtable<String,String> cWarnings = new Hashtable<String,String>();
    public Competition cCompetition;

    // filled by StatistikTask.runPostprocessing()
    public BaseDialog pParentDialog;
    public String pStatTitle;
    public String pStatCreationDate;
    public String pStatCreatedByUrl;
    public String pStatCreatedByName;
    public String pStatDescription;
    public String pStatDateRange;
    public String pStatConsideredEntries;
    public String pStatFilter;
    public Vector<String> pTableColumns;

    // filled by Competition.calculate()
    public String[] pCompRules;
    public Hashtable pCompRulesBold = new Hashtable();
    public Hashtable pCompRulesItalics = new Hashtable();
    public String pCompWarning;
    public String[][] pCompGroupNames;
    public StatisticsData[] pCompParticipants;
    public boolean pCompAbortEfaWett = false;

    public String[][] pAdditionalTable1;
    public String[]   pAdditionalTable1Title;
    public boolean    pAdditionalTable1FirstRowBold = false;
    public boolean    pAdditionalTable1LastRowBold = false;
    public String[][] pAdditionalTable2;
    public String[]   pAdditionalTable2Title;
    public boolean    pAdditionalTable2FirstRowBold = false;
    public boolean    pAdditionalTable2LastRowBold = false;
    public StatOutputLines pOutputLinesAbove;
    public StatOutputLines pOutputLinesBelow;



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
        f.add(STATISTICKEY);                      t.add(IDataAccess.DATA_STRING);
        f.add(FILTERGENDER);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERGENDERALL);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERSTATUS);                      t.add(IDataAccess.DATA_LIST_UUID);
        f.add(FILTERSTATUSALL);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERSESSIONTYPE);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERSESSIONTYPEALL);              t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERBOATTYPE);                    t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATTYPEALL);                 t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERBOATSEATS);                   t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATSEATSALL);                t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERBOATRIGGING);                 t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATRIGGINGALL);              t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERBOATCOXING);                  t.add(IDataAccess.DATA_LIST_STRING);
        f.add(FILTERBOATCOXINGALL);               t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FILTERBYPERSONID);                  t.add(IDataAccess.DATA_UUID);
        f.add(FILTERBYPERSONTEXT);                t.add(IDataAccess.DATA_STRING);
        f.add(FILTERBYBOATID);                    t.add(IDataAccess.DATA_UUID);
        f.add(FILTERBYBOATTEXT);                  t.add(IDataAccess.DATA_STRING);
        f.add(FILTERBYGROUPID);                   t.add(IDataAccess.DATA_UUID);
        f.add(SHOWFIELDS);                        t.add(IDataAccess.DATA_LIST_STRING);
        f.add(AGGREGATIONS);                      t.add(IDataAccess.DATA_LIST_STRING);
        f.add(AGGRDISTANCEBARSIZE);               t.add(IDataAccess.DATA_INTEGER);
        f.add(AGGRSESSIONSBARSIZE);               t.add(IDataAccess.DATA_INTEGER);
        f.add(AGGRAVGDISTBARSIZE);                t.add(IDataAccess.DATA_INTEGER);
        f.add(COMPYEAR);                          t.add(IDataAccess.DATA_INTEGER);
        f.add(COMPPERCENTFULFILLED);              t.add(IDataAccess.DATA_INTEGER);
        f.add(COMPOUTPUTSHORT);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTRULES);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTADDITIONALWITHREQUIREMENTS); t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTWITHOUTDETAILS);          t.add(IDataAccess.DATA_BOOLEAN);
        f.add(COMPOUTPUTALLDESTINATIONAREAS);     t.add(IDataAccess.DATA_BOOLEAN);
        f.add(SORTINGCRITERIA);                   t.add(IDataAccess.DATA_STRING);
        f.add(SORTINGORDER);                      t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTTYPE);                        t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTFILE);                        t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTENCODING);                    t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTHTMLUPDATETABLE);             t.add(IDataAccess.DATA_BOOLEAN);
        f.add(OUTPUTCSVSEPARATOR);                t.add(IDataAccess.DATA_STRING);
        f.add(OUTPUTCSVQUOTES);                   t.add(IDataAccess.DATA_STRING);
        f.add(OPTIONDISTANCEWITHUNIT);            t.add(IDataAccess.DATA_BOOLEAN);
        f.add(OPTIONTRUNCATEDIST);                t.add(IDataAccess.DATA_BOOLEAN);
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
        setFilterGenderAll(true);
        setFilterStatus(new DataTypeList<UUID>(getFilterStatusListValues(false)));
        setFilterStatusAll(false);
        setFilterSessionType(new DataTypeList<String>(Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterSessionTypeAll(true);
        setFilterBoatType(new DataTypeList<String>(Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatTypeAll(true);
        setFilterBoatSeats(new DataTypeList<String>(Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatSeatsAll(true);
        setFilterBoatRigging(new DataTypeList<String>(Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatRiggingAll(true);
        setFilterBoatCoxing(new DataTypeList<String>(Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES)));
        setFilterBoatCoxingAll(true);
        setShowFields(new DataTypeList<String>(new String[] { FIELDS_POSITION, FIELDS_NAME }));
        setAggregations(new DataTypeList<String>(new String[] { AGGR_DISTANCE, AGGR_SESSIONS, AGGR_AVGDISTANCE }));
        setAggrDistanceBarSize(200);
        setAggrSessionsBarSize(0);
        setAggrAvgDistanceBarSize(0);
        setSortingCriteria(SORTING_DISTANCE);
        setSortingOrder(SORTINGORDER_DESC);
        setCompYear(DataTypeDate.today().getYear());
        setCompPercentFulfilled(100);
        setOptionDistanceWithUnit(true);
        setOptionTruncateDistance(true);
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
            return SCAT_LIST;
        }
        return s;
    }

    public static StatisticCategory getStatisticCategoryEnum(String type) {
        if (type == null) {
            return StatisticCategory.UNKNOWN;
        } else if (type.equals(SCAT_LIST)) {
            return StatisticCategory.list;
        } else if (type.equals(SCAT_LOGBOOK)) {
            return StatisticCategory.logbook;
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
            case list:
                return International.getString("Kilometerliste");
            case logbook:
                return International.getString("Fahrtenbuch");
            case competition:
                return International.getString("Wettbewerb");
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    public String[] getStatisticCategories(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                SCAT_LIST,
                SCAT_LOGBOOK,
                SCAT_COMPETITION
            };
        } else {
            return new String[] {
                International.getString("Kilometerliste"),
                International.getString("Fahrtenbuch"),
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
            return STYPE_PERSONS;
        }
        return s;
    }

    public StatisticType getStatisticTypeEnum() {
        String s = getStatisticType();
        if (s.equals(STYPE_PERSONS)) {
            return StatisticType.persons;
        }
        if (s.equals(STYPE_BOATS)) {
            return StatisticType.boats;
        }
        return StatisticType.anythingElse;
    }

    public String getStatisticTypeDefault(StatisticCategory cat) {
        String[] types = getStatisticTypes(cat, ARRAY_STRINGLIST_VALUES);
        if (types != null && types.length > 0) {
            return types[0];
        }
        return null;
    }

    public String[] getStatisticTypes(StatisticCategory category, int valuesOrDisplay) {
        if (category == StatisticCategory.list) {
            if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
                return new String[]{
                            STYPE_PERSONS,
                            STYPE_BOATS
                        };
            } else {
                return new String[]{
                            International.getString("Personen"),
                            International.getString("Boote")
                        };
            }
        }
        if (category == StatisticCategory.logbook) {
            return new String[]{};
        }
        if (category == StatisticCategory.competition) {
            if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
                return (Daten.wettDefs != null
                        ? Daten.wettDefs.getAllWettDefKeys() : new String[0]);
            } else {
                return (Daten.wettDefs != null
                        ? Daten.wettDefs.getAllWettDefNames() : new String[0]);

            }
        }
        return new String[]{};
    }

    public String getStatisticTypeDescription() {
        String type = getStatisticType();
        String[] allTypes = getStatisticTypes(getStatisticCategoryEnum(), ARRAY_STRINGLIST_VALUES);
        for (int i=0; allTypes != null && type != null && i < allTypes.length; i++) {
            if (type.equals(allTypes[i])) {
                String[] allDisplay = getStatisticTypes(getStatisticCategoryEnum(), ARRAY_STRINGLIST_DISPLAY);
                if (allDisplay != null && i<allDisplay.length) {
                    return allDisplay[i];
                }
                return null;
            }
        }
        return null;
    }

    public void setStatisticKey(String key) {
        setString(STATISTICKEY, key);
    }

    public String getStatisticKey() {
        String s = getString(STATISTICKEY);
        if (s == null || s.length() == 0) {
            return SKEY_NAME;
        }
        return s;
    }

    public static StatisticKey getStatisticKeyEnum(String key) {
        if (key == null) {
            return StatisticKey.name;
        } else if (key.equals(SKEY_NAME)) {
            return StatisticKey.name;
        } else if (key.equals(SKEY_STATUS)) {
            return StatisticKey.status;
        } else if (key.equals(SKEY_YEAROFBIRTH)) {
            return StatisticKey.yearOfBirth;
        } else if (key.equals(SKEY_GENDER)) {
            return StatisticKey.gender;
        } else if (key.equals(SKEY_BOATTYPE)) {
            return StatisticKey.boatType;
        } else if (key.equals(SKEY_BOATSEATS)) {
            return StatisticKey.boatSeats;
        } else if (key.equals(SKEY_BOATTYPEDETAIL)) {
            return StatisticKey.boatTypeDetail;
        } else if (key.equals(SKEY_DESTINATION)) {
            return StatisticKey.destination;
        } else if (key.equals(SKEY_DISTANCE)) {
            return StatisticKey.distance;
        } else if (key.equals(SKEY_MONTH)) {
            return StatisticKey.month;
        } else if (key.equals(SKEY_WEEKDAY)) {
            return StatisticKey.weekday;
        } else if (key.equals(SKEY_TIMEOFDAY)) {
            return StatisticKey.timeOfDay;
        } else if (key.equals(SKEY_SESSIONTYPE)) {
            return StatisticKey.sessionType;
        } else if (key.equals(SKEY_YEAR)) {
            return StatisticKey.year;
        }
        return StatisticKey.name;
    }

    public StatisticKey getStatisticKeyEnum() {
        String key = getStatisticKey();
        return getStatisticKeyEnum(key);
    }

    public String getStatisticKeyDescription() {
        String key = getStatisticKey();
        String[] allKeys = getStatisticKeys(null, ARRAY_STRINGLIST_VALUES);
        for (int i=0; allKeys != null && key != null && i < allKeys.length; i++) {
            if (key.equals(allKeys[i])) {
                String[] allDisplay = getStatisticKeys(null, ARRAY_STRINGLIST_DISPLAY);
                if (allDisplay != null && i<allDisplay.length) {
                    return allDisplay[i];
                }
                return null;
            }
        }
        return null;
    }

    public String getStatisticKeyDescriptionPlural() {
        switch(getStatisticKeyEnum()) {
            case name:
                return International.getString("Namen");
            case status:
                return International.getString("Status");
            case yearOfBirth:
                return International.getString("Jahrgänge");
            case gender:
                return International.getString("Geschlechter");
            case boatType:
                return International.getString("Bootstypen");
            case boatSeats:
                return International.getString("Bootsplätze");
            case boatTypeDetail:
                return International.getString("Bootstypen") + " (" +
                       International.getString("Detail") + ")";
            case destination:
                return International.getString("Ziele");
            case distance:
                return International.getString("Entfernungen");
            case month:
                return International.getString("Monate");
            case weekday:
                return International.getString("Wochentage");
            case timeOfDay:
                return International.getString("Tageszeiten");
            case sessionType:
                return International.getString("Fahrtarten");
            case year:
                return International.getString("Jahre");
        }
        return "";
    }

    public String getStatisticKeyDefault(String sType) {
        String[] keys = getStatisticKeys(sType, ARRAY_STRINGLIST_VALUES);
        if (keys != null && keys.length > 0) {
            return keys[0];
        }
        return null;
    }

    public String[] getStatisticKeys(String sType, int valuesOrDisplay) {
        Hashtable<String,String> allKeys = new Hashtable<String,String>();
        allKeys.put(SKEY_NAME, International.getString("Name"));
        allKeys.put(SKEY_STATUS, International.getString("Status"));
        allKeys.put(SKEY_YEAROFBIRTH, International.getString("Jahrgang"));
        allKeys.put(SKEY_GENDER, International.getString("Geschlecht"));
        allKeys.put(SKEY_BOATTYPE, International.getString("Bootstyp"));
        allKeys.put(SKEY_BOATSEATS, International.getString("Bootsplätze"));
        allKeys.put(SKEY_BOATTYPEDETAIL, International.getString("Bootstyp") + " (" +
                                    International.getString("Detail") + ")");
        allKeys.put(SKEY_DESTINATION, International.getString("Ziel"));
        allKeys.put(SKEY_DISTANCE, International.getString("Entfernung"));
        allKeys.put(SKEY_MONTH, International.getString("Monat"));
        allKeys.put(SKEY_WEEKDAY, International.getString("Wochentag"));
        allKeys.put(SKEY_TIMEOFDAY, International.getString("Tageszeit"));
        allKeys.put(SKEY_SESSIONTYPE, International.getString("Fahrtart"));
        allKeys.put(SKEY_YEAR, International.getString("Jahr"));

        Vector<String> selectedKeys = new Vector<String>();
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_NAME);
        }
        if (sType == null || sType.equals(STYPE_PERSONS)) {
            selectedKeys.add(SKEY_STATUS);
        }
        if (sType == null || sType.equals(STYPE_PERSONS)) {
            selectedKeys.add(SKEY_YEAROFBIRTH);
        }
        if (sType == null || sType.equals(STYPE_PERSONS)) {
            selectedKeys.add(SKEY_GENDER);
        }
        if (sType == null || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_BOATTYPE);
        }
        if (sType == null || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_BOATSEATS);
        }
        if (sType == null || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_BOATTYPEDETAIL);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_DESTINATION);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_DISTANCE);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_MONTH);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_WEEKDAY);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_TIMEOFDAY);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_SESSIONTYPE);
        }
        if (sType == null || sType.equals(STYPE_PERSONS) || sType.equals(STYPE_BOATS)) {
            selectedKeys.add(SKEY_YEAR);
        }
        String[] result = new String[selectedKeys.size()];
        for (int i=0; i<result.length; i++) {
            if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
                result[i] = selectedKeys.get(i);
            } else {
                result[i] = allKeys.get(selectedKeys.get(i));
            }
        }
        return result;
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

    public OutputTypes getOutputTypeEnumFromString(String type) {
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
        } else if (type.equals(OTYPE_PDF)) {
            return OutputTypes.pdf;
        } else if (type.equals(OTYPE_EFAWETT)) {
            return OutputTypes.efawett;
        }
        return OutputTypes.UNKNOWN;
    }

    public OutputTypes getOutputTypeEnum() {
        return getOutputTypeEnumFromString(getOutputType());
    }

    public String getOutputTypeDescription() {
        switch(getOutputTypeEnum()) {
            case internal:
                return International.getString("intern");
            case html:
                return "HTML";
            case csv:
                return "CSV";
            case xml:
                return "XML";
            case pdf:
                return "PDF";
            case efawett:
                return Daten.EFA_WETT;
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    public String getOutputTypeFileExtensionForEnum(OutputTypes output) {
        switch(output) {
            case internal:
                return "html";
            case html:
                return "html";
            case csv:
                return "csv";
            case xml:
                return "xml";
            case pdf:
                return "pdf";
            case efawett:
                return "efw";
        }
        return "out";
    }
    public String getOutputTypeFileExtension() {
        return getOutputTypeFileExtensionForEnum(getOutputTypeEnum());
    }

    public String[] getOutputTypes(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                OTYPE_INTERNAL,
                OTYPE_HTML,
                OTYPE_PDF,
                OTYPE_CSV,
                OTYPE_XML,
                OTYPE_EFAWETT
            };
        } else {
            return new String[] {
                International.getString("intern"),
                "HTML",
                "PDF",
                "CSV",
                "XML",
                Daten.EFA_WETT,

            };
        }
    }

    public void setFilterGender(DataTypeList<String> list) {
        setList(FILTERGENDER, list);
    }

    public DataTypeList<String> getFilterGender() {
        if (getFilterGenderAll()) {
            return new DataTypeList<String>(Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_VALUES));
        }
        return getList(FILTERGENDER, IDataAccess.DATA_STRING);
    }

    public void setFilterGenderAll(boolean all) {
        setBool(FILTERGENDERALL, all);
    }

    public boolean getFilterGenderAll() {
        return getBool(FILTERGENDERALL);
    }

    public boolean isFilterGenderAllSelected() {
        DataTypeList list = getFilterGender();
        return getFilterGenderAll() || (list != null && list.length() == Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_VALUES).length);
    }

    private String getFilterEfaTypesSelectedListAsText(DataTypeList<String> list, String cat) {
        if (list == null || list.length() == 0) {
            return "<" + International.getString("leer") + ">";
        }
        String slist = null;
        for (int i=0; i<list.length(); i++) {
            slist = (slist != null ? slist + "; " : "") + Daten.efaTypes.getValue(cat, list.get(i));
        }
        return slist;
    }

    public String getFilterGenderSelectedListAsText() {
        return getFilterEfaTypesSelectedListAsText(getFilterGender(), EfaTypes.CATEGORY_GENDER);
    }

    public void setFilterStatus(DataTypeList<UUID> list) {
        setList(FILTERSTATUS, list);
    }

    public DataTypeList<UUID> getFilterStatus() {
        if (getFilterStatusAll()) {
            return new DataTypeList<UUID>(getFilterStatusListValues());
        }
        return getList(FILTERSTATUS, IDataAccess.DATA_UUID);
    }

    public void setFilterStatusAll(boolean all) {
        setBool(FILTERSTATUSALL, all);
    }

    public boolean getFilterStatusAll() {
        return getBool(FILTERSTATUSALL);
    }

    public UUID[] getFilterStatusListValues(boolean withGuestAndOther) {
        return getPersistence().getProject().getStatus(false).makeStatusArrayUUID(withGuestAndOther);
    }

    public UUID[] getFilterStatusListValues() {
        return getFilterStatusListValues(true);
    }

    public String[] getFilterStatusListDisplay() {
        return getPersistence().getProject().getStatus(false).makeStatusArray(Status.ARRAY_STRINGLIST_DISPLAY);
    }

    public boolean isFilterStatusAllSelected() {
        DataTypeList list = getFilterStatus();
        return getFilterStatusAll() || (list != null && list.length() == getFilterStatusListValues().length);
    }

    public String getFilterStatusSelectedListAsText() {
        DataTypeList<UUID> list = getFilterStatus();
        if (list == null || list.length() == 0) {
            return "<" + International.getString("leer") + ">";
        }
        Status status = getPersistence().getProject().getStatus(false);
        String slist = null;
        for (int i=0; i<list.length(); i++) {
            StatusRecord statusRecord = status.getStatus(list.get(i));
            if (statusRecord == null) {
                continue;
            }
            slist = (slist != null ? slist + "; " : "") + statusRecord.getQualifiedName();
        }
        return slist;
    }

    public void setFilterSessionType(DataTypeList<String> list) {
        setList(FILTERSESSIONTYPE, list);
    }

    public DataTypeList<String> getFilterSessionType() {
        if (getFilterSessionTypeAll()) {
            return new DataTypeList<String>(Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES));
        }
        return getList(FILTERSESSIONTYPE, IDataAccess.DATA_STRING);
    }

    public void setFilterSessionTypeAll(boolean all) {
        setBool(FILTERSESSIONTYPEALL, all);
    }

    public boolean getFilterSessionTypeAll() {
        return getBool(FILTERSESSIONTYPEALL);
    }

    public boolean isFilterSessionTypeAllSelected() {
        DataTypeList list = getFilterSessionType();
        return getFilterSessionTypeAll() || (list != null && list.length() == Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES).length);
    }

    public String getFilterSessionTypeSelectedListAsText() {
        return getFilterEfaTypesSelectedListAsText(getFilterSessionType(), EfaTypes.CATEGORY_SESSION);
    }

    public void setFilterBoatType(DataTypeList<String> list) {
        setList(FILTERBOATTYPE, list);
    }

    public DataTypeList<String> getFilterBoatType() {
        if (getFilterBoatTypeAll()) {
            return new DataTypeList<String>(Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES));
        }
        return getList(FILTERBOATTYPE, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatTypeAll(boolean all) {
        setBool(FILTERBOATTYPEALL, all);
    }

    public boolean getFilterBoatTypeAll() {
        return getBool(FILTERBOATTYPEALL);
    }

    public boolean isFilterBoatTypeAllSelected() {
        DataTypeList list = getFilterBoatType();
        return getFilterBoatTypeAll() || (list != null && list.length() == Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES).length);
    }

    public String getFilterBoatTypeSelectedListAsText() {
        return getFilterEfaTypesSelectedListAsText(getFilterBoatType(), EfaTypes.CATEGORY_BOAT);
    }

    public void setFilterBoatSeats(DataTypeList<String> list) {
        setList(FILTERBOATSEATS, list);
    }

    public DataTypeList<String> getFilterBoatSeats() {
        if (getFilterBoatSeatsAll()) {
            return new DataTypeList<String>(Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES));
        }
        return getList(FILTERBOATSEATS, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatSeatsAll(boolean all) {
        setBool(FILTERBOATSEATSALL, all);
    }

    public boolean getFilterBoatSeatsAll() {
        return getBool(FILTERBOATSEATSALL);
    }

    public boolean isFilterBoatSeatsAllSelected() {
        DataTypeList list = getFilterBoatSeats();
        return getFilterBoatSeatsAll() || (list != null && list.length() == Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES).length);
    }

    public String getFilterBoatSeatsSelectedListAsText() {
        return getFilterEfaTypesSelectedListAsText(getFilterBoatSeats(), EfaTypes.CATEGORY_NUMSEATS);
    }

    public void setFilterBoatRigging(DataTypeList<String> list) {
        setList(FILTERBOATRIGGING, list);
    }

    public DataTypeList<String> getFilterBoatRigging() {
        if (getFilterBoatRiggingAll()) {
            return new DataTypeList<String>(Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES));
        }
        return getList(FILTERBOATRIGGING, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatRiggingAll(boolean all) {
        setBool(FILTERBOATRIGGINGALL, all);
    }

    public boolean getFilterBoatRiggingAll() {
        return getBool(FILTERBOATRIGGINGALL);
    }

    public boolean isFilterBoatRiggingAllSelected() {
        DataTypeList list = getFilterBoatRigging();
        return getFilterBoatRiggingAll() || (list != null && list.length() == Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES).length);
    }

    public String getFilterBoatRiggingSelectedListAsText() {
        return getFilterEfaTypesSelectedListAsText(getFilterBoatRigging(), EfaTypes.CATEGORY_RIGGING);
    }

    public void setFilterBoatCoxing(DataTypeList<String> list) {
        setList(FILTERBOATCOXING, list);
    }

    public DataTypeList<String> getFilterBoatCoxing() {
        if (getFilterBoatCoxingAll()) {
            return new DataTypeList<String>(Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES));
        }
        return getList(FILTERBOATCOXING, IDataAccess.DATA_STRING);
    }

    public void setFilterBoatCoxingAll(boolean all) {
        setBool(FILTERBOATCOXINGALL, all);
    }

    public boolean getFilterBoatCoxingAll() {
        return getBool(FILTERBOATCOXINGALL);
    }

    public boolean isFilterBoatCoxingAllSelected() {
        DataTypeList list = getFilterBoatCoxing();
        return getFilterBoatCoxingAll() || (list != null && list.length() == Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES).length);
    }

    public String getFilterBoatCoxingSelectedListAsText() {
        return getFilterEfaTypesSelectedListAsText(getFilterBoatCoxing(), EfaTypes.CATEGORY_COXING);
    }

    public void setFilterByPersonId(UUID id) {
        setUUID(FILTERBYPERSONID, id);
    }
    public UUID getFilterByPersonId() {
        return getUUID(FILTERBYPERSONID);
    }

    public String getFilterByPersonIdAsString(long validAt) {
        try {
            return getPersistence().getProject().getPersons(false).getPerson(getFilterByPersonId(), validAt).getQualifiedName();
        } catch (Exception e) {
            return null;
        }
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

    public String getFilterByBoatIdAsString(long validAt) {
        try {
            return getPersistence().getProject().getBoats(false).getBoat(getFilterByBoatId(), validAt).getQualifiedName();
        } catch (Exception e) {
            return null;
        }
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

    public String getFilterByGroupIdAsString(long validAt) {
        try {
            return getPersistence().getProject().getGroups(false).findGroupRecord(getFilterByGroupId(), validAt).getQualifiedName();
        } catch (Exception e) {
            return null;
        }
    }

    public String getFilterCriteriaAsStringDescription() {
        String filter = null;
        if (!isFilterGenderAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Geschlecht") + ": " + getFilterGenderSelectedListAsText();
        }
        if (!isFilterStatusAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Status") + ": " + getFilterStatusSelectedListAsText();
        }
        if (!isFilterSessionTypeAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Fahrtart") + ": " + getFilterSessionTypeSelectedListAsText();
        }
        if (!isFilterBoatTypeAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Bootstyp") + ": " + getFilterBoatTypeSelectedListAsText();
        }
        if (!isFilterBoatSeatsAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Bootsplätze") + ": " + getFilterBoatSeatsSelectedListAsText();
        }
        if (!isFilterBoatRiggingAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Riggerung") + ": " + getFilterBoatRiggingSelectedListAsText();
        }
        if (!isFilterBoatCoxingAllSelected()) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Steuerung") + ": " + getFilterBoatCoxingSelectedListAsText();
        }
        if (sFilterByPersonId != null) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Person") + ": " + getFilterByPersonIdAsString(sValidAt);
        }
        if (sFilterByPersonText != null) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Person") + ": " + sFilterByPersonText;
        }
        if (sFilterByBoatId != null) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Boot") + ": " + getFilterByBoatIdAsString(sValidAt);
        }
        if (sFilterByBoatText != null) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Boot") + ": " + sFilterByBoatText;
        }
        if (sFilterByGroupId != null) {
            filter = (filter == null ? "" : filter + "\n") +
                    International.getString("Gruppe") + ": " + getFilterByGroupIdAsString(sValidAt);
        }
        return filter;
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
        Hashtable<String,String> allKeys = new Hashtable<String,String>();
        allKeys.put(AGGR_DISTANCE, DataTypeDistance.getDefaultUnitName());
        allKeys.put(AGGR_SESSIONS, International.getString("Fahrten"));
        allKeys.put(AGGR_AVGDISTANCE, DataTypeDistance.getDefaultUnitAbbrevation(true) + "/" + International.getString("Fahrt"));
        allKeys.put(AGGR_WANDERFAHRTEN, International.onlyFor("Wanderfahrten", "de"));
        allKeys.put(AGGR_ZIELFAHRTEN, International.onlyFor("Zielfahrten", "de"));

        Vector<String> selectedKeys = new Vector<String>();
        selectedKeys.add(AGGR_DISTANCE);
        selectedKeys.add(AGGR_SESSIONS);
        selectedKeys.add(AGGR_AVGDISTANCE);
        if (Daten.efaConfig.getValueUseFunctionalityRowingGermany()) {
            selectedKeys.add(AGGR_WANDERFAHRTEN);
        }
        if (Daten.efaConfig.getValueUseFunctionalityRowingBerlin()) {
            selectedKeys.add(AGGR_ZIELFAHRTEN);
        }
        String[] result = new String[selectedKeys.size()];
        for (int i=0; i<result.length; i++) {
            if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
                result[i] = selectedKeys.get(i);
            } else {
                result[i] = allKeys.get(selectedKeys.get(i));
            }
        }
        return result;
    }

    public void setAggrDistanceBarSize(int size) {
        setInt(AGGRDISTANCEBARSIZE, size);
    }
    public int getAggrDistanceBarSize() {
        int size = getInt(AGGRDISTANCEBARSIZE);
        return (size > 0 ? size : 0);
    }

    public void setAggrSessionsBarSize(int size) {
        setInt(AGGRSESSIONSBARSIZE, size);
    }
    public int getAggrSessionsBarSize() {
        int size = getInt(AGGRSESSIONSBARSIZE);
        return (size > 0 ? size : 0);
    }

    public void setAggrAvgDistanceBarSize(int size) {
        setInt(AGGRAVGDISTBARSIZE, size);
    }
    public int getAggrAvgDistanceBarSize() {
        int size = getInt(AGGRAVGDISTBARSIZE);
        return (size > 0 ? size : 0);
    }

    public String getSortingCriteria() {
        return getString(SORTINGCRITERIA);
    }
    public void setSortingCriteria(String sorting) {
        setString(SORTINGCRITERIA, sorting);
    }

    public static SortingCriteria getSortingCriteriaEnum(String sort) {
        if (sort == null) {
            return SortingCriteria.UNKNOWN;
        } else if (sort.equals(SORTING_DISTANCE)) {
            return SortingCriteria.distance;
        } else if (sort.equals(SORTING_SESSIONS)) {
            return SortingCriteria.sessions;
        } else if (sort.equals(SORTING_AVGDISTANCE)) {
            return SortingCriteria.avgDistance;
        } else if (sort.equals(SORTING_NAME)) {
            return SortingCriteria.name;
        } else if (sort.equals(SORTING_STATUS)) {
            return SortingCriteria.status;
        } else if (sort.equals(SORTING_YEAROFBIRTH)) {
            return SortingCriteria.yearOfBirth;
        } else if (sort.equals(SORTING_BOATTYPE)) {
            return SortingCriteria.boatType;
        } else if (sort.equals(SORTING_ENTRYNO)) {
            return SortingCriteria.entryNo;
        } else if (sort.equals(SORTING_DATE)) {
            return SortingCriteria.date;
        }
        return SortingCriteria.UNKNOWN;
    }

    public SortingCriteria getSortingCriteriaEnum() {
        String sort = getSortingCriteria();
        return getSortingCriteriaEnum(sort);
    }

    public String getSortingCriteriaDescription() {
        switch(getSortingCriteriaEnum()) {
            case distance:
                return International.getString("Kilometer");
            case sessions:
                return International.getString("Fahrten");
            case avgDistance:
                return International.getString("Km/Fahrt");
            case name:
                return International.getString("Name");
            case status:
                return International.getString("Status");
            case yearOfBirth:
                return International.getString("Jahrgang");
            case boatType:
                return International.getString("Bootstyp");
            case entryNo:
                return International.getString("LfdNr");
            case date:
                return International.getString("Datum");
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    public String[] getSortingCriteria(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                SORTING_DISTANCE,
                SORTING_SESSIONS,
                SORTING_AVGDISTANCE,
                SORTING_NAME,
                SORTING_STATUS,
                SORTING_YEAROFBIRTH,
                SORTING_BOATTYPE,
                SORTING_ENTRYNO,
                SORTING_DATE 
            };
        } else {
            return new String[] {
                International.getString("Kilometer"),
                International.getString("Fahrten"),
                International.getString("Km/Fahrt"),
                International.getString("Name"),
                International.getString("Status"),
                International.getString("Jahrgang"),
                International.getString("Bootstyp"),
                International.getString("LfdNr"),
                International.getString("Datum")
            };
        }
    }

    public String getSortingOrder() {
        return getString(SORTINGORDER);
    }
    public void setSortingOrder(String order) {
        setString(SORTINGORDER, order);
    }

    public static boolean getSortingOrderAscending(String sort) {
        return (sort == null || sort.equals(SORTINGORDER_ASC));
    }

    public boolean getSortingOrderAscending() {
        String sort = getSortingOrder();
        return getSortingOrderAscending(sort);
    }

    public String getSortingOrderDescription() {
        if(getSortingOrderAscending()) {
            return International.getString("aufsteigend");
        } else {
            return International.getString("absteigend");
        }
    }

    public String[] getSortingOrders(int valuesOrDisplay) {
        if (valuesOrDisplay == ARRAY_STRINGLIST_VALUES) {
            return new String[] {
                SORTINGORDER_ASC,
                SORTINGORDER_DESC
            };
        } else {
            return new String[] {
                International.getString("aufsteigend"),
                International.getString("absteigend")
            };
        }
    }

    public String getOutputFile() {
        String fname = getString(OUTPUTFILE);
        if (fname == null || fname.length() == 0) {
            fname = Daten.efaTmpDirectory + "output." + getOutputTypeFileExtension();
        }
        return fname;
    }
    public void setOutputFile(String file) {
        setString(OUTPUTFILE, file);
    }
    
    public boolean getOutputHtmlUpdateTable() {
        return getBool(OUTPUTHTMLUPDATETABLE);
    }    
    public void setOutputHtmlUpdateTable(boolean updateOnlyTable) {
        setBool(OUTPUTHTMLUPDATETABLE, updateOnlyTable);
    }

    public String getOutputEncoding() {
        String encoding = getString(OUTPUTENCODING);
        if (encoding == null || encoding.length() == 0) {
            return Daten.ENCODING_UTF;
        }
        return encoding;
    }
    public void setOutputEncoding(String encoding) {
        setString(OUTPUTENCODING, encoding);
    }

    public String getOutputCsvSeparator() {
        String separator = getString(OUTPUTCSVSEPARATOR);
        if (separator == null || separator.length() == 0) {
            return "|";
        }
        return separator;
    }
    public void setOutputCsvSeparator(String separator) {
        setString(OUTPUTCSVSEPARATOR, separator);
    }

    public String getOutputCsvQuotes() {
        String quotes = getString(OUTPUTCSVQUOTES);
        if (quotes == null) {
            return "";
        }
        return quotes;
    }
    public void setOutputCsvQuotes(String quotes) {
        setString(OUTPUTCSVQUOTES, quotes);
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

    public boolean getOptionDistanceWithUnit() {
        return getBool(OPTIONDISTANCEWITHUNIT);
    }
    public void setOptionDistanceWithUnit(boolean enabled) {
        setBool(OPTIONDISTANCEWITHUNIT, enabled);
    }

    public boolean getOptionTruncateDistance() {
        return getBool(OPTIONTRUNCATEDIST);
    }
    public void setOptionTruncateDistance(boolean enabled) {
        setBool(OPTIONTRUNCATEDIST, enabled);
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

    public Vector<IItemType> getGuiItems(AdminRecord admin) {
        String CAT_BASEDATA     = "%01%" + International.getString("Statistik");
        String CAT_FILTER       = "%02%" + International.getString("Filter");
        String CAT_FILTERGENDER        = "%021%" + International.getString("Geschlecht");
        String CAT_FILTERSTATUS        = "%022%" + International.getString("Status");
        String CAT_FILTERSESSIONTYPE   = "%023%" + International.getString("Fahrtart");
        String CAT_FILTERBOATTYPE      = "%024%" + International.getString("Bootstyp");
        String CAT_FILTERBOATSEAT      = "%025%" + International.getString("Bootsplätze");
        String CAT_FILTERBOATRIGG      = "%026%" + International.getString("Riggerung");
        String CAT_FILTERBOATCOXING    = "%027%" + International.getString("Steuerung");
        String CAT_FILTERINDIVIDUAL    = "%028%" + International.getString("individuell");
        String CAT_FIELDS       = "%03%" + International.getString("Felder");
        String CAT_SORTING      = "%04%" + International.getString("Sortierung");
        String CAT_COMP         = "%05%" + International.getString("Wettbewerbe");
        String CAT_OUTPUT       = "%06%" + International.getString("Ausgabe");
        String CAT_OPTIONS      = "%07%" + International.getString("Optionen");
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
        ((ItemTypeBoolean)item).registerItemListener(this);
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
        item.registerItemListener(this);
        this.itemStatisticType = (ItemTypeStringList)item;
        v.add(item = new ItemTypeStringList(StatisticsRecord.STATISTICKEY, getStatisticKey(),
                    getStatisticKeys(getStatisticType(), ARRAY_STRINGLIST_VALUES),
                    getStatisticKeys(getStatisticType(), ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Statistikschlüssel")));
        this.itemStatisticKey = (ItemTypeStringList)item;
        
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERGENDER, getFilterGender(),
                    Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERGENDER),
                    International.getString("Geschlecht")));
        itemFilterGender = (ItemTypeMultiSelectList<String>)item;
        itemFilterGender.setEnabled(!getFilterGenderAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERGENDERALL, getFilterGenderAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERGENDER),
                    International.getString("alle")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeMultiSelectList<UUID>(StatisticsRecord.FILTERSTATUS, getFilterStatus(),
                    getFilterStatusListValues(), getFilterStatusListDisplay(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERSTATUS),
                    International.getString("Status")));
        itemFilterStatus = (ItemTypeMultiSelectList<String>)item;
        itemFilterStatus.setEnabled(!getFilterStatusAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERSTATUSALL, getFilterStatusAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERSTATUS),
                    International.getString("alle")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERSESSIONTYPE, getFilterSessionType(),
                    Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERSESSIONTYPE),
                    International.getString("Fahrtart")));
        itemFilterSessionType = (ItemTypeMultiSelectList<String>)item;
        itemFilterSessionType.setEnabled(!getFilterSessionTypeAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERSESSIONTYPEALL, getFilterSessionTypeAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERSESSIONTYPE),
                    International.getString("alle")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATTYPE, getFilterBoatType(),
                    Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATTYPE),
                    International.getString("Bootstyp")));
        itemFilterBoatType = (ItemTypeMultiSelectList<String>)item;
        itemFilterBoatType.setEnabled(!getFilterBoatTypeAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERBOATTYPEALL, getFilterBoatTypeAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATTYPE),
                    International.getString("alle")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATSEATS, getFilterBoatSeats(),
                    Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATSEAT),
                    International.getString("Bootsplätze")));
        itemFilterBoatSeats = (ItemTypeMultiSelectList<String>)item;
        itemFilterBoatSeats.setEnabled(!getFilterBoatSeatsAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERBOATSEATSALL, getFilterBoatSeatsAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATSEAT),
                    International.getString("alle")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATRIGGING, getFilterBoatRigging(),
                    Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATRIGG),
                    International.getString("Riggerung")));
        itemFilterBoatRigging = (ItemTypeMultiSelectList<String>)item;
        itemFilterBoatRigging.setEnabled(!getFilterBoatRiggingAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERBOATRIGGINGALL, getFilterBoatRiggingAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATRIGG),
                    International.getString("alle")));
        item.registerItemListener(this);
        v.add(item = new ItemTypeMultiSelectList<String>(StatisticsRecord.FILTERBOATCOXING, getFilterBoatCoxing(),
                    Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), Daten.efaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATCOXING),
                    International.getString("Steuerung")));
        itemFilterBoatCoxing = (ItemTypeMultiSelectList<String>)item;
        itemFilterBoatCoxing.setEnabled(!getFilterBoatCoxingAll());
        v.add(item = new ItemTypeBoolean(StatisticsRecord.FILTERBOATCOXINGALL, getFilterBoatCoxingAll(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERBOATCOXING),
                    International.getString("alle")));
        item.registerItemListener(this);

        v.add(item = getGuiItemTypeStringAutoComplete(StatisticsRecord.FILTERBYPERSONID, null,
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERINDIVIDUAL),
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("nur Person")));
        if (getFilterByPersonId() != null) {
            ((ItemTypeStringAutoComplete)item).setId(getFilterByPersonId());
        } else {
            ((ItemTypeStringAutoComplete)item).parseAndShowValue(getFilterByPersonText());
        }
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(StatisticsRecord.FILTERBYPERSONTEXT);
        v.add(item = getGuiItemTypeStringAutoComplete(StatisticsRecord.FILTERBYBOATID, null,
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERINDIVIDUAL),
                    getPersistence().getProject().getBoats(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("nur Boot")));
        if (getFilterByBoatId() != null) {
            ((ItemTypeStringAutoComplete)item).setId(getFilterByBoatId());
        } else {
            ((ItemTypeStringAutoComplete)item).parseAndShowValue(getFilterByBoatText());
        }
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(StatisticsRecord.FILTERBYBOATTEXT);
        v.add(item = getGuiItemTypeStringAutoComplete(StatisticsRecord.FILTERBYGROUPID, getFilterByGroupId(),
                    IItemType.TYPE_PUBLIC, BaseTabbedDialog.makeCategory(CAT_FILTER,CAT_FILTERINDIVIDUAL),
                    getPersistence().getProject().getGroups(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("nur Gruppe")));

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
                IItemType.TYPE_PUBLIC, CAT_FIELDS, International.getString("Balkengröße") + ": " +
                International.getString("Kilometer")));
        v.add(item = new ItemTypeInteger(StatisticsRecord.AGGRSESSIONSBARSIZE, getAggrSessionsBarSize(), 0, 1000,
                IItemType.TYPE_PUBLIC, CAT_FIELDS, International.getString("Balkengröße") + ": " +
                International.getString("Fahrten")));
        v.add(item = new ItemTypeInteger(StatisticsRecord.AGGRAVGDISTBARSIZE, getAggrAvgDistanceBarSize(), 0, 1000,
                IItemType.TYPE_PUBLIC, CAT_FIELDS, International.getString("Balkengröße") + ": " +
                International.getString("Km/Fahrt")));

        // CAT_SORTING
        v.add(item = new ItemTypeStringList(StatisticsRecord.SORTINGCRITERIA, getSortingCriteria(),
                    getSortingCriteria(ARRAY_STRINGLIST_VALUES), getSortingCriteria(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_SORTING,
                    International.getString("Sortierkriterium")));
        v.add(item = new ItemTypeStringList(StatisticsRecord.SORTINGORDER, getSortingOrder(),
                    getSortingOrders(ARRAY_STRINGLIST_VALUES), getSortingOrders(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_SORTING,
                    International.getString("Sortierreihenfolge")));

        // CAT_COMP
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

        // CAT_OUTPUT
        v.add(item = new ItemTypeStringList(StatisticsRecord.OUTPUTTYPE, getOutputType(),
                    getOutputTypes(ARRAY_STRINGLIST_VALUES), getOutputTypes(ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_OUTPUT,
                    International.getString("Ausgabeart")));
        item.registerItemListener(this);
        item.setNotNull(true);
        v.add(item = new ItemTypeFile(StatisticsRecord.OUTPUTFILE, getOutputFile(),
                International.getString("Ausgabedatei"),
                International.getString("alle Dateien"),
                null, ItemTypeFile.MODE_SAVE, ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_PUBLIC, CAT_OUTPUT, International.getString("Ausgabedatei")));
        item.setNotNull(true);
        this.itemOutputFile = (ItemTypeFile)item;
        v.add(item = new ItemTypeStringList(StatisticsRecord.OUTPUTENCODING, getOutputEncoding(),
                new String[] { Daten.ENCODING_UTF, Daten.ENCODING_ISO },
                new String[] { Daten.ENCODING_UTF, Daten.ENCODING_ISO },
                IItemType.TYPE_PUBLIC, CAT_OUTPUT,
                International.getStringWithMnemonic("Zeichensatz")
                ));
        item.setNotNull(true);
        this.itemOutputEncoding = (ItemTypeStringList)item;
        v.add(item = new ItemTypeBoolean(StatisticsRecord.OUTPUTHTMLUPDATETABLE, getOutputHtmlUpdateTable(),
                    IItemType.TYPE_PUBLIC, CAT_OUTPUT,
                    International.getString("in existierenden HTML-Dateien nur Tabelle ersetzen")));
        this.itemOutputHtmlUpdateTable = (ItemTypeBoolean)item;
        v.add(item = new ItemTypeString(StatisticsRecord.OUTPUTCSVSEPARATOR, getOutputCsvSeparator(),
                    IItemType.TYPE_PUBLIC, CAT_OUTPUT,
                    International.getString("Feldtrenner") + " (CSV)"));
        item.setNotNull(true);
        this.itemOutputCsvSeparator = (ItemTypeString)item;
        v.add(item = new ItemTypeString(StatisticsRecord.OUTPUTCSVQUOTES, getOutputCsvQuotes(),
                    IItemType.TYPE_PUBLIC, CAT_OUTPUT,
                    International.getString("Texttrenner") + " (CSV)"));
        this.itemOutputCsvQuotes = (ItemTypeString)item;

        // CAT_OPTIONS
        v.add(item = new ItemTypeBoolean(StatisticsRecord.OPTIONDISTANCEWITHUNIT, getOptionDistanceWithUnit(),
                    IItemType.TYPE_PUBLIC, CAT_OPTIONS,
                    International.getString("Entfernungen mit Längeneinheit ausgeben")));
        v.add(item = new ItemTypeBoolean(StatisticsRecord.OPTIONTRUNCATEDIST, getOptionTruncateDistance(),
                    IItemType.TYPE_PUBLIC, CAT_OPTIONS,
                    International.getString("Nachkommastellen bei Ausgabe von Entfernungen abschneiden")));

        setVisibleItems(getOutputTypeEnum());
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[(TABLE_HEADER_LONG ? 6 : 2)];
        header[0] = new TableItemHeader(International.getString("Nr."));
        header[1] = new TableItemHeader(International.getString("Name"));
        if (TABLE_HEADER_LONG) {
            header[2] = new TableItemHeader(International.getString("Statistiktyp"));
            header[3] = new TableItemHeader(International.getString("Statistikart"));
            header[4] = new TableItemHeader(International.getString("Von"));
            header[5] = new TableItemHeader(International.getString("Bis"));
        }
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[(TABLE_HEADER_LONG ? 6 : 2)];
        items[0] = new TableItem(getPosition());
        items[1] = new TableItem(getName());
        if (TABLE_HEADER_LONG) {
            items[2] = new TableItem(getStatisticCategoryDescription());
            items[3] = new TableItem(getStatisticTypeDescription());
            items[4] = new TableItem(getDateFrom());
            items[5] = new TableItem(getDateTo());
            if (getPubliclyAvailable()) {
                items[0].setMarked(true);
                items[1].setMarked(true);
                items[2].setMarked(true);
                items[3].setMarked(true);
                items[4].setMarked(true);
                items[5].setMarked(true);
            }
        }
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
        sStatisticTypeEnum = getStatisticTypeEnum();
        sStatistikKey = getStatisticKeyEnum();

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
        if (sStatisticCategory == StatisticCategory.list &&
            (fields == null || fields.length() == 0)) {
            // at least show these fields, if for (whatever reason) no fields were selected
            sIsFieldsPosition = true;
            sIsFieldsName = true;
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
            } else if (s.equals(AGGR_WANDERFAHRTEN)) {
                sIsAggrWanderfahrten = true;
            }
        }

        sAggrDistanceBarSize = getAggrDistanceBarSize();
        sAggrSessionsBarSize = getAggrSessionsBarSize();
        sAggrAvgDistanceBarSize = getAggrAvgDistanceBarSize();

        sSortingCriteria = getSortingCriteriaEnum();
        sSortingOrderAscending = getSortingOrderAscending();

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
            this.sIsFieldsName = true;
            this.sIsFieldsStatus = true;
            this.sIsFieldsYearOfBirth = true;
            if (sStatisticType.equals(WettDefs.STR_LRVBERLIN_SOMMER)) {
                this.sIsAggrZielfahrten = true;
            }
            if (sStatisticType.equals(WettDefs.STR_DRV_FAHRTENABZEICHEN)) {
                this.sIsAggrWanderfahrten = true;
            }
            if (sStatisticType.equals(WettDefs.STR_LRVBERLIN_WINTER)) {
                this.sIsAggrWinterfahrten = true;
            }
            if (sStatisticType.equals(WettDefs.STR_LRVBRB_FAHRTENWETT) ||
                sStatisticType.equals(WettDefs.STR_LRVBRB_WANDERRUDERWETT) ||
                sStatisticType.equals(WettDefs.STR_LRVMVP_WANDERRUDERWETT)) {
                this.sIsAggrGigfahrten = true;
            }
        }

        sOutputType = getOutputTypeEnum();
        if (sOutputType == OutputTypes.internal) {
            sOutputFile = Daten.efaTmpDirectory + "output.html";
        } else {
            sOutputFile = getOutputFile();
        }
        sOutputDir = (new File(sOutputFile)).getAbsolutePath();
        sOutputEncoding = getOutputEncoding();
        sOutputHtmlUpdateTable = getOutputHtmlUpdateTable();
        sOutputCsvSeparator = getOutputCsvSeparator();
        sOutputCsvQuotes = getOutputCsvQuotes();

        sDistanceWithUnit = getOptionDistanceWithUnit();
        sTruncateDistanceToFullValue = getOptionTruncateDistance();

        sTimestampBegin = sStartDate.getTimestamp(new DataTypeTime(0,0,0));
        sTimestampEnd   = sEndDate.getTimestamp(new DataTypeTime(23,59,59));
        sValidAt = sEndDate.getTimestamp(new DataTypeTime(23,59,59));
    }

    public void prepareTableColumns() {
        pTableColumns = new Vector<String>();
        if (sStatisticCategory == StatisticCategory.list) {
            if (sIsFieldsPosition) {
                pTableColumns.add(International.getString("Platz"));
            }
            if (sIsFieldsName) {
                pTableColumns.add(getStatisticKeyDescription());
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
                pTableColumns.add(International.onlyFor("Zielfahrten", "de"));
            }
            if (sIsAggrWanderfahrten) {
                pTableColumns.add(International.onlyFor("Wanderfahrten", "de"));
            }
        }
        if (sStatisticCategory == StatisticCategory.logbook) {
            pTableColumns.add(International.getString("Lfd. Nr."));
            pTableColumns.add(International.getString("Datum"));
            pTableColumns.add(International.getString("Boot"));
            pTableColumns.add(International.getString("Steuermann"));
            pTableColumns.add(International.getString("Mannschaft"));
            pTableColumns.add(International.getString("Abfahrt"));
            pTableColumns.add(International.getString("Ankunft"));
            pTableColumns.add(International.getString("Ziel"));
            pTableColumns.add(International.getString("Kilometer"));
            pTableColumns.add(International.getString("Bemerkungen"));
        }
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType.getName().equals(STATISTICCATEGORY) && event instanceof ItemEvent) {
            String cats = itemType.getValueFromField();
            StatisticCategory cat = getStatisticCategoryEnum(cats);
            String defaultStatisticType = getStatisticTypeDefault(cat);
            String defaultStatisticKey = getStatisticKeyDefault(defaultStatisticType);
            if (itemStatisticType != null) {
                itemStatisticType.setListData(getStatisticTypes(cat, ARRAY_STRINGLIST_VALUES),
                                              getStatisticTypes(cat, ARRAY_STRINGLIST_DISPLAY));
                if (defaultStatisticType != null) {
                    itemStatisticType.parseAndShowValue(defaultStatisticType);
                }
            }
            if (itemStatisticKey != null) {
                if (defaultStatisticType == null) {
                    defaultStatisticType = "other"; // null means we get all, but we want none!
                }
                itemStatisticKey.setListData(getStatisticKeys(defaultStatisticType, ARRAY_STRINGLIST_VALUES),
                                             getStatisticKeys(defaultStatisticType, ARRAY_STRINGLIST_DISPLAY));
                if (defaultStatisticKey != null) {
                    itemStatisticKey.parseAndShowValue(defaultStatisticKey);
                }
            }
        }
        if (itemType.getName().equals(STATISTICTYPE) && event instanceof ItemEvent) {
            String type = itemType.getValueFromField();
            String defaultStatisticKey = getStatisticKeyDefault(type);
            if (itemStatisticKey != null) {
                itemStatisticKey.setListData(getStatisticKeys(type, ARRAY_STRINGLIST_VALUES),
                                             getStatisticKeys(type, ARRAY_STRINGLIST_DISPLAY));
                if (defaultStatisticKey != null) {
                    itemStatisticKey.parseAndShowValue(defaultStatisticKey);
                }
            }
        }
        if (itemType.getName().equals(FILTERGENDERALL) && event instanceof ActionEvent) {
            if (itemFilterGender != null && itemType.getValueFromField() != null) {
                itemFilterGender.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(FILTERSTATUSALL) && event instanceof ActionEvent) {
            if (itemFilterStatus != null && itemType.getValueFromField() != null) {
                itemFilterStatus.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(FILTERSESSIONTYPEALL) && event instanceof ActionEvent) {
            if (itemFilterSessionType != null && itemType.getValueFromField() != null) {
                itemFilterSessionType.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(FILTERBOATTYPEALL) && event instanceof ActionEvent) {
            if (itemFilterBoatType != null && itemType.getValueFromField() != null) {
                itemFilterBoatType.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(FILTERBOATSEATSALL) && event instanceof ActionEvent) {
            if (itemFilterBoatSeats != null && itemType.getValueFromField() != null) {
                itemFilterBoatSeats.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(FILTERBOATRIGGINGALL) && event instanceof ActionEvent) {
            if (itemFilterBoatRigging != null && itemType.getValueFromField() != null) {
                itemFilterBoatRigging.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(FILTERBOATCOXINGALL) && event instanceof ActionEvent) {
            if (itemFilterBoatCoxing != null && itemType.getValueFromField() != null) {
                itemFilterBoatCoxing.setEnabled(itemType.getValueFromField().equals(Boolean.toString(false)));
            }
        }
        if (itemType.getName().equals(OUTPUTTYPE) && event instanceof ItemEvent) {
            OutputTypes newOutputType = getOutputTypeEnumFromString(itemType.getValueFromField());
            setVisibleItems(newOutputType);
            if (itemOutputFile != null) {
                String fname = itemOutputFile.getValueFromField();
                int pos = (fname != null ? fname.lastIndexOf(".") : -1);
                if (pos > 0) {
                    fname = fname.substring(0, pos) + "." + this.getOutputTypeFileExtensionForEnum(newOutputType);
                    itemOutputFile.parseAndShowValue(fname);
                }
            }
        }
        if (itemType.getName().equals(PUBLICLYAVAILABLE) && event instanceof ActionEvent) {
            ((ItemTypeBoolean)itemType).setColor(
                    (itemType.getValueFromField().equals(Boolean.toString(true)) ?
                        Color.blue : Color.black));
        }
    }

    private void setVisibleItems(OutputTypes output) {
        if (itemOutputFile != null) {
            itemOutputFile.setVisible(output != OutputTypes.internal && output != OutputTypes.efawett);
        }
        if (itemOutputEncoding != null) {
            itemOutputEncoding.setVisible(output == OutputTypes.csv);
        }
        if (itemOutputHtmlUpdateTable != null) {
            itemOutputHtmlUpdateTable.setVisible(output == OutputTypes.html);
        }
        if (itemOutputCsvSeparator != null) {
            itemOutputCsvSeparator.setVisible(output == OutputTypes.csv);
        }
        if (itemOutputCsvQuotes != null) {
            itemOutputCsvQuotes.setVisible(output == OutputTypes.csv);
        }
    }

}
