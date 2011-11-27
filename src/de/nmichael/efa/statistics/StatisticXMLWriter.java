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

public class StatisticXMLWriter extends StatisticWriter {

    public static final String FIELD_GLOBAL = "Statistic";

    public static final String FIELD_HEADER = "Header";
    public static final String FIELD_HEADER_STATISTICTITLE = "Title";
    public static final String FIELD_HEADER_STATISTICDESCRIPTION = "Description";
    public static final String FIELD_HEADER_STATISTICCATEGORY = "Category";
    public static final String FIELD_HEADER_STATISTICTYPE = "Type";
    public static final String FIELD_HEADER_STATISTICKEY = "Key";
    public static final String FIELD_HEADER_CREATEDAT = "Date";
    public static final String FIELD_HEADER_CREATEDBYURL = "ProgramUrl";
    public static final String FIELD_HEADER_CREATEDBYNAME = "ProgramName";
    public static final String FIELD_HEADER_DATARANGE = "DateRange";
    public static final String FIELD_HEADER_CONSIDEREDENTRIES = "ConsideredEntries";
    public static final String FIELD_HEADER_FILTER = "Filter";

    public static final String FIELD_DATA = "Data";
    public static final String FIELD_ITEM = "Item";
    public static final String FIELD_ITEM_POSITION = "Position";
    public static final String FIELD_ITEM_NAME = "Name";
    public static final String FIELD_ITEM_STATUS = "Status";
    public static final String FIELD_ITEM_YEAROFBIRTH = "YearOfBirth";
    public static final String FIELD_ITEM_BOATTYPE = "BoatType";
    public static final String FIELD_ITEM_DISTANCE = "Distance";
    public static final String FIELD_ITEM_SESSIONS = "Sessions";
    public static final String FIELD_ITEM_AVGDISTANCE = "AvgDistance";
    public static final String FIELD_ITEM_DESTINATIONAREAS = "DestinationAreas";

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

    public static final String FIELD_COMPETITION = "Competition";
    public static final String FIELD_COMPETITION_RULES = "CompetitionRules";
    public static final String FIELD_COMPETITION_WARNING = "CompetitionWarning";
    public static final String FIELD_COMPETITION_GROUP = "Group";
    public static final String FIELD_COMPETITION_GROUP_NAME = "GroupName";
    public static final String FIELD_COMPETITION_GROUP_DESCRIPTION = "GroupDescription";
    public static final String FIELD_COMPETITION_GROUP_REQUIREMENT = "GroupRequirements";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT = "Participant";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT_NAME = "Name";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT_YEAROFBIRTH = "YearOfBirth";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT_FULFILLED = "Fulfilled";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT_DISTANCE = "Distance";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT_ADDITIONAL = "Additional";
    public static final String FIELD_COMPETITION_GROUP_PARTICIPANT_DETAILS = "Details";

    public static final String FIELD_ADDITIONALTABLE = "AdditionalTable";
    public static final String FIELD_TABLE_ROW = "Row";
    public static final String FIELD_TABLE_CELL = "Cell";

    private static final boolean doIndent = true;
    private int indent = 0;

    public StatisticXMLWriter(StatisticsRecord sr, StatisticsData[] sd) {
        super(sr, sd);
    }

    public static String getLogbookField(int pos) {
        switch(pos) {
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

    private String xmltagStart(String tag) {
        indent++;
        return "<" + tag + ">";
    }

    private String xmltagStart(String tag, String attrib) {
        indent++;
        return "<" + tag + " type=\"" + EfaUtil.escapeXml(attrib) + "\">";
    }

    private String xmltagEnd(String tag) {
        indent--;
        return "</" + tag + ">";
    }

    private String xmltag(String tag, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return xmltagStart(tag) + EfaUtil.escapeXml(value) + xmltagEnd(tag);
    }

    private String xmltag(String tag, String value, String attrib) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return xmltagStart(tag, attrib) + EfaUtil.escapeXml(value) + xmltagEnd(tag);
    }

    protected String space(int indent) {
        if (doIndent) {
            String s = "";
            for (int i = 0; i < indent && i < this.indent; i++) {
                s += "  ";
            }
            return s;
        }
        return "";
    }

    private void writeTable(BufferedWriter f, String tableName, String[] header, String[][] data) throws IOException {
        write(f, indent, xmltagStart(tableName));
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null || data[i].length == 0) {
                continue;
            }
            write(f, indent, xmltagStart(FIELD_TABLE_ROW));
            for (int j = 0; j < data[i].length; j++) {
                if (header != null) {
                    write(f, indent, xmltag(FIELD_TABLE_CELL, data[i][j], header[j]));
                } else {
                    write(f, indent, xmltag(FIELD_TABLE_CELL, data[i][j]));
                }
            }
            write(f, indent, xmltagEnd(FIELD_TABLE_ROW));
        }
        write(f, indent, xmltagEnd(tableName));

    }

    protected synchronized void write(BufferedWriter fw, int indent, String s) throws IOException {
        if (s == null) {
            return;
        }
        fw.write(space(indent) + s + "\n");
    }

    public boolean write() {
        BufferedWriter f = null;

        if (sr.sFileExecBefore != null && sr.sFileExecBefore.length() > 0) {
            EfaUtil.execCmd(sr.sFileExecBefore);
        }
        try {
            // Create File
            f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sr.sOutputFile), Daten.ENCODING_UTF));
            write(f, 0, "<?xml version=\"1.0\" encoding=\"" + Daten.ENCODING_UTF + "\"?>");
            write(f, indent, xmltagStart(FIELD_GLOBAL));

            // Write Header
            write(f, indent, xmltagStart(FIELD_HEADER));
            write(f, indent, xmltag(FIELD_HEADER_STATISTICTITLE, sr.pStatTitle));
            write(f, indent, xmltag(FIELD_HEADER_STATISTICDESCRIPTION, sr.pStatDescription));
            write(f, indent, xmltag(FIELD_HEADER_STATISTICCATEGORY, sr.getStatisticCategory()));
            write(f, indent, xmltag(FIELD_HEADER_STATISTICTYPE, sr.getStatisticType()));
            write(f, indent, xmltag(FIELD_HEADER_STATISTICKEY, sr.getStatisticKey()));
            write(f, indent, xmltag(FIELD_HEADER_CREATEDAT, sr.pStatCreationDate));
            write(f, indent, xmltag(FIELD_HEADER_CREATEDBYURL, sr.pStatCreatedByUrl));
            write(f, indent, xmltag(FIELD_HEADER_CREATEDBYNAME, sr.pStatCreatedByName));
            write(f, indent, xmltag(FIELD_HEADER_DATARANGE, sr.pStatDateRange));
            write(f, indent, xmltag(FIELD_HEADER_CONSIDEREDENTRIES, sr.pStatConsideredEntries));
            write(f, indent, xmltag(FIELD_HEADER_FILTER, sr.pStatFilter));
            write(f, indent, xmltagEnd(FIELD_HEADER));

            // Write Competition
            if (sr.pCompGroupNames != null && sr.pCompParticipants != null) {
                write(f, indent, xmltagStart(FIELD_COMPETITION));
                if (sr.pCompRules != null) {
                    StringBuffer compRules = new StringBuffer();
                    for (int i = 0; i < sr.pCompRules.length; i++) {
                        compRules.append(sr.pCompRules[i] + " ");
                    }
                    write(f, indent, xmltag(FIELD_COMPETITION_RULES, compRules.toString()));
                }

                if (sr.pCompWarning != null) {
                    write(f, indent, xmltag(FIELD_COMPETITION_WARNING, sr.pCompWarning));
                }

                for (int i = 0; i < sr.pCompGroupNames.length; i++) {
                    write(f, indent, xmltagStart(FIELD_COMPETITION_GROUP));
                    write(f, indent, xmltag(FIELD_COMPETITION_GROUP_NAME, sr.pCompGroupNames[i][0]));
                    write(f, indent, xmltag(FIELD_COMPETITION_GROUP_DESCRIPTION, sr.pCompGroupNames[i][1]));
                    write(f, indent, xmltag(FIELD_COMPETITION_GROUP_REQUIREMENT, sr.pCompGroupNames[i][2]));
                    for (StatisticsData participant = sr.pCompParticipants[i]; participant != null; participant = participant.next) {
                        write(f, indent, xmltagStart(FIELD_COMPETITION_GROUP_PARTICIPANT));
                        write(f, indent, xmltag(FIELD_COMPETITION_GROUP_PARTICIPANT_NAME, participant.sName));
                        write(f, indent, xmltag(FIELD_COMPETITION_GROUP_PARTICIPANT_YEAROFBIRTH, participant.sYearOfBirth));
                        write(f, indent, xmltag(FIELD_COMPETITION_GROUP_PARTICIPANT_FULFILLED, Boolean.toString(participant.compFulfilled)));
                        write(f, indent, xmltag(FIELD_COMPETITION_GROUP_PARTICIPANT_DISTANCE, participant.sDistance));
                        write(f, indent, xmltag(FIELD_COMPETITION_GROUP_PARTICIPANT_ADDITIONAL, participant.sAdditional));
                        // ausführliche Ausgabe
                        if (participant.sDetailsArray.length > 0) {
                            writeTable(f, FIELD_COMPETITION_GROUP_PARTICIPANT_DETAILS, null, participant.sDetailsArray);
                        }
                        write(f, indent, xmltagEnd(FIELD_COMPETITION_GROUP_PARTICIPANT));
                    }
                    write(f, indent, xmltagEnd(FIELD_COMPETITION_GROUP));
                }
                write(f, indent, xmltagEnd(FIELD_COMPETITION));
            }

            // Write normal Output
            if (sr.pTableColumns != null && sr.pTableColumns.size() > 0) {
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                    write(f, indent, xmltagStart(FIELD_DATA));
                }
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
                    write(f, indent, xmltagStart(FIELD_LOGBOOK));
                }
                for (int i = 0; i < sd.length; i++) {
                    if (sd[i].isMaximum) {
                        continue;
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                        write(f, indent, xmltagStart(FIELD_ITEM));
                        write(f, indent, xmltag(FIELD_ITEM_POSITION, sd[i].sPosition));
                        write(f, indent, xmltag(FIELD_ITEM_NAME, sd[i].sName));
                        write(f, indent, xmltag(FIELD_ITEM_STATUS, sd[i].sStatus));
                        write(f, indent, xmltag(FIELD_ITEM_YEAROFBIRTH, sd[i].sYearOfBirth));
                        write(f, indent, xmltag(FIELD_ITEM_BOATTYPE, sd[i].sBoatType));
                        write(f, indent, xmltag(FIELD_ITEM_DISTANCE, sd[i].sDistance));
                        write(f, indent, xmltag(FIELD_ITEM_SESSIONS, sd[i].sSessions));
                        write(f, indent, xmltag(FIELD_ITEM_AVGDISTANCE, sd[i].sAvgDistance));
                        write(f, indent, xmltag(FIELD_ITEM_DESTINATIONAREAS, sd[i].sDestinationAreas));
                        write(f, indent, xmltagEnd(FIELD_ITEM));
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
                        write(f, indent, xmltagStart(FIELD_RECORD));
                        if (sd[i].logbookFields != null) {
                            for (int j = 0; j < sd[i].logbookFields.length; j++) {
                                write(f, indent, xmltag(getLogbookField(j), sd[i].logbookFields[j]));
                            }
                        }
                        write(f, indent, xmltagEnd(FIELD_RECORD));
                    }
                }
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                    write(f, indent, xmltagEnd(FIELD_DATA));
                }
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
                    write(f, indent, xmltagEnd(FIELD_LOGBOOK));
                }
            }

            // Zusatzdaten
            if (sr.pAdditionalTable1 != null) {
                writeTable(f, FIELD_ADDITIONALTABLE, sr.pAdditionalTable1Title, sr.pAdditionalTable1);
            }
            if (sr.pAdditionalTable2 != null) {
                writeTable(f, FIELD_ADDITIONALTABLE, sr.pAdditionalTable2Title, sr.pAdditionalTable2);
            }

            write(f, indent, xmltagEnd(FIELD_GLOBAL));
            f.close();
        } catch (IOException e) {
            Dialog.error(LogString.logstring_fileCreationFailed(sr.sOutputFile, International.getString("Ausgabedatei")));
            LogString.logError_fileCreationFailed(sr.sOutputFile, International.getString("Ausgabedatei"));
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
        return true;
    }
}
