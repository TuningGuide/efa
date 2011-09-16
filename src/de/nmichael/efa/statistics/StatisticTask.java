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


    private StatisticTask(StatisticsRecord[] statisticsRecords) {
        this.statisticsRecords = statisticsRecords;
    }

    private void calculateAggregations(LogbookRecord r, Object key, int crewPos) {
        if (key == null) {
            return;
        }
        StatisticsData sd = data.get(key);
        if (sd == null) {
            sd = new StatisticsData();
            sd.key = key;
        }
        
        if (sr.sIsAggrDistance || sr.sIsAggrAvgDistance) {
            sd.distance += r.getDistance().getValueInDefaultUnit();
        }
        if (sr.sIsAggrSessions || sr.sIsAggrAvgDistance) {
            sd.sessions++;
        }

        data.put(key, sd);
    }

    private void calculateEntry(LogbookRecord r) {
        if (!isInRange(r) ||
            !isInFilter(r) ||
            r.getSessionIsOpen()) {
            return;
        }

        // update number of evaluated entries
        sr.cNumberOfEntries++;

        // update date range of evaluated entries
        DataTypeDate date = r.getDate();
        if (date != null && date.isSet()) {
            if (sr.cEntryDateFirst == null || date.isBefore(sr.cEntryDateFirst)) {
                sr.cEntryDateFirst = date;
            }
            if (sr.cEntryDateLast == null || date.isAfter(sr.cEntryDateLast)) {
                sr.cEntryDateLast = date;
            }
            date = r.getEndDate();
            if (date != null && date.isSet() && (sr.cEntryDateLast == null || date.isAfter(sr.cEntryDateLast))) {
                sr.cEntryDateLast = date;
            }
        }

        DataTypeIntString entryNo = r.getEntryId();
        if (entryNo != null) {
            if (sr.cEntryNoFirst == null || entryNo.intValue() < sr.cEntryNoFirst.intValue()) {
                sr.cEntryNoFirst = entryNo;
            }
            if (sr.cEntryNoLast == null || entryNo.intValue() > sr.cEntryNoLast.intValue()) {
                sr.cEntryNoLast = entryNo;
            }
        }

        long validAt = r.getValidAtTimestamp();
        switch(sr.sStatisticType) {
            case persons:
                for (int i=0; i<LogbookRecord.CREW_MAX; i++) {
                    UUID id = r.getCrewId(i);
                    PersonRecord p = persons.getPerson(id, validAt);
                    if (isInPersonFilter(p, (id == null ? r.getCrewName(i) : null))) {
                        if (id != null) {
                            calculateAggregations(r, id, i);
                        } else {
                            calculateAggregations(r, r.getCrewName(i), i);
                        }
                    }
                }
                break;
            case boats:

        }
    }

    private boolean isInRange(LogbookRecord r) {
        DataTypeDate d1 = r.getDate();
        DataTypeDate d2 = r.getEndDate();
        if (d1 == null || !d1.isSet()) {
            return false;
        }
        if (d2 == null || !d2.isSet()) {
            return d1.isInRange(sr.sStartDate, sr.sEndDate);
        } else {
            return d1.isInRange(sr.sStartDate, sr.sEndDate) && // both start *and* end date must be in range!
                   d2.isInRange(sr.sStartDate, sr.sEndDate);
        }
    }

    private boolean isInFilter(LogbookRecord r) {
        return true;
    }

    private boolean isInPersonFilter(PersonRecord p, String name) {
        if (p != null) {
            // known person
            UUID statusID = p.getStatusId();
            if (statusID == null || !sr.sFilterStatus.contains(statusID)) {
                return false;
            }
            return true;
        } else {
            // unknown person
            if (name == null) {
                return false;
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
        Object[] keys = data.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
            StatisticsData sd = data.get(keys[i]);
            switch(sr.sStatisticType) {
                case persons:
                    if (sd.key instanceof UUID) {
                        PersonRecord pr = persons.getPerson((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
                        if (pr != null) {
                            sd.text = pr.getQualifiedName();
                        } else {
                            sd.text = "*** " + International.getString("ungültiger Eintrag") + " ***";
                        }
                    } else {
                        sd.text = sd.key.toString();
                    }
                    break;
                case boats:
                    if (sd.key instanceof UUID) {
                        BoatRecord br = boats.getBoat((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
                        if (br != null) {
                            sd.text = br.getQualifiedName();
                        } else {
                            sd.text = "*** " + International.getString("ungültiger Eintrag") + " ***";
                        }
                    } else {
                        sd.text = sd.key.toString();
                    }
                    break;
            }
        }

        StatisticsData[] sdArray = new StatisticsData[keys.length];
        for (int i=0; i<keys.length; i++) {
            sdArray[i] = data.get(keys[i]);
        }
        Arrays.sort(sdArray);
        for (int i=0; i<sdArray.length; i++) {
            sdArray[i].absPosition = i;
            sdArray[i].position = i+1;
        }

        sr.pParentDialog = this.progressDialog;

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
