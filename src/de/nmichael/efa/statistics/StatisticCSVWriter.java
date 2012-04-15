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

import java.io.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.ex.EfaException;

public class StatisticCSVWriter extends StatisticWriter {

    public static final String FIELD_ITEM_POSITION = "Position";
    public static final String FIELD_ITEM_NAME = "Name";
    public static final String FIELD_ITEM_STATUS = "Status";
    public static final String FIELD_ITEM_YEAROFBIRTH = "YearOfBirth";
    public static final String FIELD_ITEM_BOATTYPE = "BoatType";
    public static final String FIELD_ITEM_DISTANCE = "Distance";
    public static final String FIELD_ITEM_SESSIONS = "Sessions";
    public static final String FIELD_ITEM_AVGDISTANCE = "AvgDistance";
    public static final String FIELD_ITEM_DESTINATIONAREAS = "DestinationAreas";
    public static final String FIELD_ITEM_WANDERFARTEN = "WanderfahrtKm";
    public static final String FIELD_LOGBOOK = "Logbook";
    public static final String FIELD_RECORD = "Record";
    public static final String FIELD_RECORD_ENTRYNO = "EntryNo";
    public static final String FIELD_RECORD_DATE = "Date";
    public static final String FIELD_RECORD_BOAT = "Boat";
    public static final String FIELD_RECORD_COX = "Cox";
    public static final String FIELD_RECORD_CREW = "Crew";
    public static final String FIELD_RECORD_STARTTIME = "StartTime";
    public static final String FIELD_RECORD_ENDTIME = "EndTime";
    public static final String FIELD_RECORD_DESTINATION = "Destination";
    public static final String FIELD_RECORD_DISTANCE = "Distance";
    public static final String FIELD_RECORD_COMMENTS = "Comments";
    private String encoding;
    private String separator;
    private String quotes;
    private int linelength = 0;

    public StatisticCSVWriter(StatisticsRecord sr, StatisticsData[] sd) {
        super(sr, sd);
        this.encoding = sr.sOutputEncoding;
        this.separator = sr.sOutputCsvSeparator;
        this.quotes = sr.sOutputCsvQuotes;
        if (this.encoding == null || this.encoding.length() == 0) {
            this.encoding = Daten.ENCODING_UTF;
        }
        if (this.separator == null || this.separator.length() == 0) {
            this.separator = "|";
        }
        if (this.quotes != null && this.quotes.length() == 0) {
            this.quotes = null;
        }
    }

    public static String getLogbookField(int pos) {
        switch (pos) {
            case 0:
                return FIELD_RECORD_ENTRYNO;
            case 1:
                return FIELD_RECORD_DATE;
            case 2:
                return FIELD_RECORD_BOAT;
            case 3:
                return FIELD_RECORD_COX;
            case 4:
                return FIELD_RECORD_CREW;
            case 5:
                return FIELD_RECORD_STARTTIME;
            case 6:
                return FIELD_RECORD_ENDTIME;
            case 7:
                return FIELD_RECORD_DESTINATION;
            case 8:
                return FIELD_RECORD_DISTANCE;
            case 9:
                return FIELD_RECORD_COMMENTS;
        }
        return null;
    }

    protected synchronized void write(BufferedWriter fw, String s) throws IOException {
        if (s == null) {
            s = "";
        }
        if (quotes == null && s.indexOf(separator) >= 0) {
            String repl = (!separator.equals("_") ? "_" : "#");
            s = EfaUtil.replace(s, separator, repl, true);
        }
        fw.write((linelength > 0 ? separator : "") + 
                 (quotes != null ? quotes : "") + s + (quotes != null ? quotes : ""));
        linelength += s.length();
    }

    protected synchronized void writeln(BufferedWriter fw) throws IOException {
        fw.write("\n");
        linelength = 0;
    }

    public boolean write() {
        BufferedWriter f = null;

        if (sr.sFileExecBefore != null && sr.sFileExecBefore.length() > 0) {
            EfaUtil.execCmd(sr.sFileExecBefore);
        }
        try {
            // Create File
            f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sr.sOutputFile), encoding));

            // Write normal Output
            if (sr.pTableColumns != null && sr.pTableColumns.size() > 0) {
                for (int i = 0; i < sr.pTableColumns.size(); i++) {
                    write(f, sr.pTableColumns.get(i));
                }
                writeln(f);

                for (int i = 0; i < sd.length; i++) {
                    if (sd[i].isMaximum || sd[i].isSummary) {
                        continue;
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                        if (sr.sIsFieldsPosition) {
                            write(f, sd[i].sPosition);
                        }
                        if (sr.sIsFieldsName) {
                            write(f, sd[i].sName);
                        }
                        if (sr.sIsFieldsGender) {
                            write(f, sd[i].sGender);
                        }
                        if (sr.sIsFieldsStatus) {
                            write(f, sd[i].sStatus);
                        }
                        if (sr.sIsFieldsYearOfBirth) {
                            write(f, sd[i].sYearOfBirth);
                        }
                        if (sr.sIsFieldsBoatType) {
                            write(f, sd[i].sBoatType);
                        }
                        if (sr.sIsAggrDistance) {
                            write(f, sd[i].sDistance);
                        }
                        if (sr.sIsAggrSessions) {
                            write(f, sd[i].sSessions);
                        }
                        if (sr.sIsAggrAvgDistance) {
                            write(f, sd[i].sAvgDistance);
                        }
                        if (sr.sIsAggrZielfahrten) {
                            write(f, sd[i].sDestinationAreas);
                        }
                        if (sr.sIsAggrWanderfahrten) {
                            write(f, sd[i].sWanderfahrten);
                        }
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
                        if (sd[i].logbookFields != null) {
                            for (int j = 0; j < sd[i].logbookFields.length; j++) {
                                write(f, sd[i].logbookFields[j]);
                            }
                        }
                    }
                    writeln(f);
                }
            }
            f.close();
        } catch (IOException e) {
            Dialog.error(LogString.fileCreationFailed(sr.sOutputFile, International.getString("Ausgabedatei")));
            LogString.logError_fileCreationFailed(sr.sOutputFile, International.getString("Ausgabedatei"));
            resultMessage = LogString.fileCreationFailed(sr.sOutputFile, International.getString("Statistik"));
            return false;
        } finally {
            try {
                f.close();
            } catch (Exception ee) {
                f = null;
            }
        }
        if (sr.sFileExecAfter != null && sr.sFileExecAfter.length() > 0) {
            EfaUtil.execCmd(sr.sFileExecAfter);
        }
        resultMessage = LogString.fileSuccessfullyCreated(sr.sOutputFile, International.getString("Statistik"));
        return true;
    }
}
