/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

import java.util.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.gui.*;

public class StatisticTask extends ProgressTask {

    private static final int WORK_PER_STATISTIC  = 100;
    private static final int WORK_POSTPROCESSING =  50;
    
    private StatisticsRecord[] statisticsRecords;
    private StatisticsRecord sr;
    private Hashtable<Object,StatisticsData> data = new Hashtable<Object,StatisticsData>();
    private Persons persons = Daten.project.getPersons(false);
    private Boats boats = Daten.project.getBoats(false);
    private Destinations destinations = Daten.project.getDestinations(false);

    // values from current logbook entry
    private DataTypeIntString entryNo;
    private DataTypeDate entryDate;
    private DataTypeDate entryEndDate;
    private long entryNumberOfDays;
    private long entryValidAt;
    private UUID entryBoatId;
    private BoatRecord entryBoatRecord;
    private String entryBoatName;
    private String entryBoatType;
    private String entryBoatSeats;
    private String entryBoatRigging;
    private String entryBoatCoxing;
    private UUID entryPersonId;
    private PersonRecord entryPersonRecord;
    private String entryPersonName;
    private UUID entryPersonStatusId;
    private String entryPersonGender;
    private UUID entryDestinationId;
    private DestinationRecord entryDestinationRecord;
    private String entryDestinationVariant;
    private String entryDestinationName;
    private ZielfahrtFolge entryDestinationAreas;
    private long entryDistanceInDefaultUnit;
    private String entrySessionType;
    private SessionGroupRecord entrySessionGroup;



    private StatisticTask(StatisticsRecord[] statisticsRecords) {
        this.statisticsRecords = statisticsRecords;
    }

    private void resetEntryValues() {
        entryNo = null;
        entryDate = null;
        entryEndDate = null;
        entryNumberOfDays = 1;
        entryValidAt = -1;
        entryBoatId = null;
        entryBoatRecord = null;
        entryBoatName = null;
        entryBoatType = null;
        entryBoatSeats = null;
        entryBoatRigging = null;
        entryBoatCoxing = null;
        entryPersonId = null;
        entryPersonRecord = null;
        entryPersonName = null;
        entryPersonStatusId = null;
        entryPersonGender = null;
        entryDestinationId = null;
        entryDestinationRecord = null;
        entryDestinationVariant = null;
        entryDestinationName = null;
        entryDestinationAreas = null;
        entryDistanceInDefaultUnit = 0;
        entrySessionType = null;
        entrySessionGroup = null;
    }

    private void calculateSessionHistory(LogbookRecord r, Object key, StatisticsData sd) {
        if (sd.sessionHistory == null) {
            sd.sessionHistory = new SessionHistory();
        }
        if (entryNumberOfDays > 1) {
            long distancePerDay = entryDistanceInDefaultUnit / entryNumberOfDays;
            long remainingDistance = entryDistanceInDefaultUnit;
            DataTypeDate date = entryDate;
            for (int i=1; i<=entryNumberOfDays; i++) {
                long dist = distancePerDay;
                if (i > 1 && i == entryNumberOfDays) {
                    dist = remainingDistance;
                    date = entryEndDate;
                }
                DataTypeDistance distance = new DataTypeDistance(dist);
                Zielfahrt destArea = null;
                if (entryDestinationAreas != null && i <= entryDestinationAreas.getAnzZielfahrten()) {
                    destArea = entryDestinationAreas.getZielfahrt(i-1);
                }
                sd.sessionHistory.addSession(r, i, date, distance, destArea);
                remainingDistance -= dist;
                date.addDays(1);
            }
        } else {
            Zielfahrt destArea = null;
            if (entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
                destArea = entryDestinationAreas.getZielfahrt(0); // @todo warn if more than 1!!
            }
            if (destArea != null) {
                sd.sessionHistory.addSession(r, destArea);
            } else {
                sd.sessionHistory.addSession(r);
            }
        }
    }

    private void calculateAggregations(LogbookRecord r, Object key) {
        if (key == null) {
            return;
        }
        StatisticsData sd = data.get(key);
        if (sd == null) {
            sd = new StatisticsData();
            sd.key = key;
        }

        // aggregate
        if (sr.sIsAggrDistance || sr.sIsAggrAvgDistance) {
            sd.distance += entryDistanceInDefaultUnit;
        }
        if (sr.sIsAggrSessions || sr.sIsAggrAvgDistance) {
            sd.sessions += entryNumberOfDays;
        }
        if (sr.sIsAggrZielfahrten &&
            entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
            calculateSessionHistory(r, key, sd);
        }

        data.put(key, sd);
    }

    private void calculateEntry(LogbookRecord r) {
        resetEntryValues();
        getEntryBasic(r);
        if (!isInRange(r) ||
            !isInFilter(r) ||
            r.getSessionIsOpen()) {

            return;
        }

        // update number of evaluated entries
        sr.cNumberOfEntries++;

        // update date range of evaluated entries
        if (entryDate != null && entryDate.isSet()) {
            if (sr.cEntryDateFirst == null || entryDate.isBefore(sr.cEntryDateFirst)) {
                sr.cEntryDateFirst = entryDate;
            }
            if (sr.cEntryDateLast == null || entryDate.isAfter(sr.cEntryDateLast)) {
                sr.cEntryDateLast = entryDate;
            }
            if (entryEndDate != null && entryEndDate.isSet() && (sr.cEntryDateLast == null || entryEndDate.isAfter(sr.cEntryDateLast))) {
                sr.cEntryDateLast = entryEndDate;
            }
        }

        // update entryno for first and last evaluated entry
        if (entryNo != null) {
            if (sr.cEntryNoFirst == null || entryNo.intValue() < sr.cEntryNoFirst.intValue()) {
                sr.cEntryNoFirst = entryNo;
            }
            if (sr.cEntryNoLast == null || entryNo.intValue() > sr.cEntryNoLast.intValue()) {
                sr.cEntryNoLast = entryNo;
            }
        }

        // get further data
        if (sr.sIsAggrZielfahrten) {
            getEntryDestination(r);
        }
        getEntryDistance(r);

        switch(sr.sStatisticType) {
            case persons:
            case competition:
                for (int i=0; i<LogbookRecord.CREW_MAX; i++) {
                    getEntryPerson(r, i);
                    if (isInPersonFilter()) {
                        if (entryPersonId != null) {
                            calculateAggregations(r, entryPersonId);
                        } else {
                            if (entryPersonName != null) {
                                calculateAggregations(r, entryPersonName);
                            }
                        }
                    }
                }
                break;
            case boats:
                if (entryBoatId != null) {
                    calculateAggregations(r, entryBoatId);
                } else {
                    if (entryBoatName != null) {
                        calculateAggregations(r, entryBoatName);
                    }
                }
                break;
        }
    }

    private void getEntryBasic(LogbookRecord r) {
        entryNo = r.getEntryId();
        entryValidAt = r.getValidAtTimestamp();
    }
    
    private void getEntryDates(LogbookRecord r) {
        entryDate = r.getDate();
        entryEndDate = r.getEndDate();
        entryNumberOfDays = 1;
        if (entryDate != null && entryDate.isSet() &&
            entryEndDate != null && entryEndDate.isSet()) {
            entryNumberOfDays = entryEndDate.getDifferenceDays(entryDate) + 1;
            if (entryNumberOfDays > 1) {
                getSessionGroup(r);
                if (entrySessionGroup != null &&
                    entrySessionGroup.checkLogbookRecordFitsIntoRange(r) &&
                    entryDate.equals(entrySessionGroup.getStartDate()) &&
                    entryEndDate.equals(entrySessionGroup.getEndDate())) {
                    entryNumberOfDays = entrySessionGroup.getActiveDays();
                }
            }
        }
    }

    private void getSessionGroup(LogbookRecord r) {
        entrySessionGroup = r.getSessionGroup();
    }

    private void getEntrySessionType(LogbookRecord r) {
        entrySessionType = r.getSessionType();
        if (entrySessionType == null) {
            entrySessionType = EfaTypes.TYPE_SESSION_NORMAL;
        }
    }

    private void getEntryBoat(LogbookRecord r) {
        entryBoatId = r.getBoatId();
        entryBoatRecord = (entryBoatId != null ? boats.getBoat(entryBoatId, entryValidAt) : null);
        entryBoatName = (entryBoatId != null ? null : r.getBoatName());
        int boatVariant = r.getBoatVariant();
        int vidx = -1;
        if (entryBoatRecord != null) {
            if (entryBoatRecord.getNumberOfVariants() == 1) {
                vidx = 0;
            } else {
                vidx = entryBoatRecord.getVariantIndex(boatVariant);
            }
        }
        if (vidx >= 0) {
            entryBoatType = entryBoatRecord.getTypeType(vidx);
            entryBoatSeats = entryBoatRecord.getTypeSeats(vidx);
            entryBoatRigging = entryBoatRecord.getTypeRigging(vidx);
            entryBoatCoxing = entryBoatRecord.getTypeCoxing(vidx);
        }
        if (entryBoatType == null) {
            entryBoatType = EfaTypes.TYPE_BOAT_OTHER;
        }
        if (entryBoatSeats == null) {
            entryBoatSeats = EfaTypes.TYPE_NUMSEATS_OTHER;
        }
        if (entryBoatRigging == null) {
            entryBoatRigging = EfaTypes.TYPE_RIGGING_OTHER;
        }
        if (entryBoatCoxing == null) {
            entryBoatCoxing = EfaTypes.TYPE_COXING_OTHER;
        }
    }

    private void getEntryPerson(LogbookRecord r, int pos) {
        entryPersonId = r.getCrewId(pos);
        entryPersonRecord = (entryPersonId != null ? persons.getPerson(entryPersonId, entryValidAt) : null);
        entryPersonName = (entryPersonId != null ? null : r.getCrewName(pos));
        entryPersonStatusId = (entryPersonRecord != null ? entryPersonRecord.getStatusId() : null);
        entryPersonGender = (entryPersonRecord != null ? entryPersonRecord.getGender() : null);
    }

    private void getEntryDestination(LogbookRecord r) {
        entryDestinationId = r.getDestinationId();
        entryDestinationRecord = (entryDestinationId != null ? destinations.getDestination(entryDestinationId, entryValidAt) : null);
        entryDestinationVariant  = (entryDestinationId != null ? r.getDestinationVariantName() : null);
        entryDestinationName = (entryDestinationId != null ? null : r.getDestinationName());
        entryDestinationAreas = (entryDestinationRecord != null ? entryDestinationRecord.getDestinationAreas() : null);
    }

    private void getEntryDistance(LogbookRecord r) {
        entryDistanceInDefaultUnit = (r.getDistance() != null ?
            r.getDistance().getValueInDefaultUnit() : 0);
    }

    private boolean isInRange(LogbookRecord r) {
        getEntryDates(r);
        if (entryDate == null || !entryDate.isSet()) {
            return false;
        }
        if (entryEndDate == null || !entryEndDate.isSet()) {
            return entryDate.isInRange(sr.sStartDate, sr.sEndDate);
        } else {
            return entryDate.isInRange(sr.sStartDate, sr.sEndDate) && // both start *and* end date must be in range!
                   entryEndDate.isInRange(sr.sStartDate, sr.sEndDate);
        }
    }

    private boolean isInFilter(LogbookRecord r) {
        getEntrySessionType(r);
        if (!sr.sFilterSessionType.containsKey(entrySessionType)) {
            return false;
        }

        getEntryBoat(r);
        if (!sr.sFilterBoatType.containsKey(entryBoatType) ||
            !sr.sFilterBoatSeats.containsKey(entryBoatSeats) ||
            !sr.sFilterBoatRigging.containsKey(entryBoatRigging) ||
            !sr.sFilterBoatCoxing.containsKey(entryBoatCoxing)) {
            return false;
        }

        return true;
    }

    private boolean isInPersonFilter() {
        if (entryPersonRecord != null) {
            // known person
            if (entryPersonStatusId == null || !sr.sFilterStatus.containsKey(entryPersonStatusId)) {
                return false;
            }
            if (entryPersonGender == null || !sr.sFilterGender.containsKey(entryPersonGender)) {
                return false;
            }
            return true;
        } else {
            // unknown person
            if (entryPersonName == null) {
                return false;
            }
            if (!sr.sFilterStatusOther) {
                return false;
            }
            if (sr.sFilterGender.size() != 2) {
                return false; // both MALE and FEMALE must be selected
            }
            return true;
        }
    }

    private Vector<Logbook> getAllLogbooks() {
        Vector<Logbook> logbooks = new Vector<Logbook>();
        if (Daten.project == null) {
            return logbooks;
        }
        String[] names = Daten.project.getAllLogbookNames();
        for (int i=0; names != null && i<names.length; i++) {
            ProjectRecord pr = Daten.project.getLoogbookRecord(names[i]);
            if (pr != null) {
                DataTypeDate lbStart = pr.getStartDate();
                DataTypeDate lbEnd = pr.getEndDate();
                if (lbStart == null || lbEnd == null) {
                    continue; // should never happen
                }
                if (DataTypeDate.isRangeOverlap(lbStart, lbEnd, sr.sStartDate, sr.sEndDate)) {
                    Logbook l = Daten.project.getLogbook(names[i], false);
                    if (l != null) {
                        logbooks.add(l);
                    }
                }
            }
        }
        return logbooks;
    }

    private StatisticsData[] runPostprocessing() {
        logInfo(International.getString("Aufbereiten der Daten") + " ...\n");
        int workBeforePostprocessing = this.getCurrentWorkDone();

        String statusOtherText;
        try {
            statusOtherText = persons.getProject().getStatus(false).getStatusOther().getQualifiedName();
        } catch(Exception eignore) {
            statusOtherText = International.getString("andere");
        }


        StatisticsData sdSummary = new StatisticsData();
        sdSummary.isSummary = true;
        sdSummary.sName = "--- " + International.getString("gesamt") + " (" + data.size() + ") ---";
        StatisticsData sdMaximum = new StatisticsData();
        sdMaximum.isMaximum = true;

        Object[] keys = data.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
            StatisticsData sd = data.get(keys[i]);
            boolean isUUID = false;
            switch(sr.sStatisticType) {
                case persons:
                case competition:
                    PersonRecord pr = null;
                    if (sd.key instanceof UUID) {
                        pr = persons.getPerson((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
                        isUUID = true;
                    }
                    if (sr.sIsFieldsName) {
                        sd.sName = (pr != null ? pr.getQualifiedName() : 
                            (isUUID ? "*** " + International.getString("ungültiger Eintrag") + " ***" : sd.key.toString()));
                    }
                    if (sr.sIsFieldsStatus) {
                        sd.sStatus = (pr != null ? pr.getStatusName() : statusOtherText);
                    }
                    if (sr.sIsFieldsYearOfBirth) {
                        DataTypeDate birthday = (pr != null ? pr.getBirthday() : null);
                        if (birthday != null && birthday.isSet()) {
                            sd.sYearOfBirth = Integer.toString(birthday.getYear());
                        }
                    }
                    if (sr.sStatisticType == StatisticsRecord.StatisticTypes.competition && pr != null) {
                        sd.gender = pr.getGender();
                        sd.disabled = pr.getDisability();
                    }
                    break;
                case boats:
                    BoatRecord br = null;
                    if (sd.key instanceof UUID) {
                        br = boats.getBoat((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
                        isUUID = true;
                    }
                    if (sr.sIsFieldsName) {
                        sd.sName = (br != null ? br.getQualifiedName() :
                            (isUUID ? "*** " + International.getString("ungültiger Eintrag") + " ***" : sd.key.toString()));
                    }
                    if (sr.sIsFieldsBoatType) {
                        sd.sBoatType = (br != null ? br.getDetailedBoatType(0) : Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, EfaTypes.TYPE_BOAT_OTHER));
                    }
                    break;
            }

            // Calculate Summary and Maximum
            sdSummary.updateSummary(sd);
            sdMaximum.updateMaximum(sd);
        }
        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*1);

        // Create Array and sort
        StatisticsData[] sdArray = new StatisticsData[keys.length + 2];
        for (int i=0; i<keys.length; i++) {
            sdArray[i] = data.get(keys[i]);
        }
        Arrays.sort(sdArray, 0, keys.length);
        sdArray[sdArray.length - 2] = sdSummary;
        sdArray[sdArray.length - 1] = sdMaximum;
        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*2);

        // Calculate String Output Values
        for (int i=0; i<sdArray.length; i++) {
            sdArray[i].createStringOutputValues(sr, i,
                    (i > 0 && sdArray[i].getMainAggregationValue() == sdArray[i - 1].getMainAggregationValue() ? sdArray[i - 1].sPosition : Integer.toString(i + 1) + "."));
        }
        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*3);

        if (sr.sStatisticType == StatisticsRecord.StatisticTypes.competition) {
            Competition competition = Competition.getCompetition(sr);
            if (competition != null) {
                competition.calculate(sr, sdArray);
            }
        }

        sr.pParentDialog = this.progressDialog;

        // Statistics Base Data
        sr.pStatTitle = International.getString("Kilometerliste");
        if (sr.cEntryDateFirst != null && sr.cEntryDateFirst.isSet()) {
            sr.pStatTitle += " " + sr.cEntryDateFirst.getYear();
            if (sr.cEntryDateLast != null && sr.cEntryDateLast.isSet() && sr.cEntryDateFirst.getYear() != sr.cEntryDateLast.getYear()) {
                sr.pStatTitle += " - " + sr.cEntryDateLast.getYear();
            }
        }
        sr.pStatCreationDate = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
        sr.pStatCreatedByUrl = Daten.EFAURL;
        sr.pStatCreatedByName = Daten.EFA_LONGNAME + " " + Daten.VERSION;
        sr.pStatDescription = sr.getStatisticTypeDescription();
        sr.pStatDateRange = sr.sStartDate.toString() + " - " + sr.sEndDate.toString();
        sr.pStatConsideredEntries = International.getMessage("{n} Einträge", sr.cNumberOfEntries);
        if (sr.cNumberOfEntries > 0 && sr.cEntryNoFirst != null && sr.cEntryNoLast != null) {
            sr.pStatConsideredEntries += ": #" + sr.cEntryNoFirst.toString() + " - #" + sr.cEntryNoLast.toString();
            if (sr.cEntryDateFirst != null && sr.cEntryDateFirst.isSet() && sr.cEntryDateLast != null && sr.cEntryDateLast.isSet()) {
                sr.pStatConsideredEntries += " (" + International.getMessage("vom {day_from} bis {day_to}",
                        sr.cEntryDateFirst.toString(), sr.cEntryDateLast.toString()) + ")";
            }
        }

        if (sr.sStatisticType != StatisticsRecord.StatisticTypes.competition) {
            // Table Columns
            sr.prepareTableColumns();
        }

        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*4);
        return sdArray;
    }

    private void writeStatistic(StatisticsData[] sd) {
        logInfo(International.getString("Ausgabe der Daten") + " ...\n");
        StatisticInternalWriter writer = new StatisticInternalWriter(sr, sd);
        writer.write();
    }

    private void createStatistic(StatisticsRecord sr, int statisticsNumber) {
        this.sr = sr;
        sr.prepareStatisticSettings();
        logInfo(International.getMessage("Erstelle Statistik für den Zeitraum {from} bis {to} ...", sr.sStartDate.toString(), sr.sEndDate.toString()) + "\n");
        Vector<Logbook> logbooks = getAllLogbooks();
        if (logbooks.size() == 0) {
            return;
        }
        int WORK_PER_LOGBOOK = WORK_PER_STATISTIC / logbooks.size();
        for (int i=0; i<logbooks.size(); i++) {
            try {
                Logbook logbook = logbooks.get(i);
                logInfo(International.getString("Fahrtenbuch") + " " + logbook.getName() + " ...\n");
                DataKeyIterator it = logbook.data().getStaticIterator();
                int size = it.size();
                DataKey k = it.getFirst();
                int pos = 0;
                while (k != null) {
                    LogbookRecord r = (LogbookRecord)logbook.data().get(k);
                    if (r != null) {
                        calculateEntry(r);
                    }
                    this.setCurrentWorkDone( ((++pos * WORK_PER_LOGBOOK) / size) + (i * WORK_PER_LOGBOOK) + (statisticsNumber *  WORK_PER_STATISTIC));
                    k = it.getNext();
                }
            } catch(Exception e) {
                logInfo("ERROR: " + e.toString() + "\n");
            }
        }
        StatisticsData[] sd = runPostprocessing();
        writeStatistic(sd);
    }

    private void createStatistics(ProgressDialog progressDialog) {
        this.start();
        if (progressDialog != null) {
            progressDialog.showDialog();
        }
    }

    public void run() {
        setRunning(true);
        for (int i=0; i<statisticsRecords.length; i++) {
            createStatistic(statisticsRecords[i], i);
            setCurrentWorkDone((i+1) * WORK_PER_STATISTIC);
        }
        setDone();
    }

    public int getAbsoluteWork() {
        return (statisticsRecords != null ? statisticsRecords.length : 0) * WORK_PER_STATISTIC + WORK_POSTPROCESSING;
    }

    public String getSuccessfullyDoneMessage() {
        return null; // avoid info dialog at the end
    }

    public static void createStatisticsTask(BaseFrame parentFrame, BaseDialog parentDialog, StatisticsRecord[] sr) {
            StatisticTask statisticTask = new StatisticTask(sr);
            ProgressDialog progressDialog = (parentFrame != null ?
                new ProgressDialog(parentFrame, International.getString("Statistik erstellen"), statisticTask, true, true) :
                new ProgressDialog(parentDialog, International.getString("Statistik erstellen"), statisticTask, true, true) );
            statisticTask.createStatistics(progressDialog);
    }
}
