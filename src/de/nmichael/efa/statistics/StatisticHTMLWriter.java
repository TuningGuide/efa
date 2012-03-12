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
import java.util.Arrays;

public class StatisticHTMLWriter extends StatisticWriter {

    public StatisticHTMLWriter(StatisticsRecord sr, StatisticsData[] sd) {
        super(sr, sd);
    }

    public boolean write() {
        BufferedWriter f = null;
        BufferedReader fo = null;
        String tmpFile = sr.sOutputFile + ".efatmp";

        if (sr.sFileExecBefore != null && sr.sFileExecBefore.length() > 0) {
            EfaUtil.execCmd(sr.sFileExecBefore);
        }
        try {

            // Nur Tabelle ersetzen?
            if (sr.sOutputHtmlUpdateTable && !new File(sr.sOutputFile).isFile()) {
                sr.sOutputHtmlUpdateTable = false;
            }
            if (sr.sOutputHtmlUpdateTable) {
                File bak = new File(sr.sOutputFile);
                bak.renameTo(new File(tmpFile));
                fo = new BufferedReader(new InputStreamReader(new FileInputStream(tmpFile), Daten.ENCODING_UTF));
            }

            // Datei erstellen und Kopf schreiben
            f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sr.sOutputFile), Daten.ENCODING_UTF));
            if (sr.sOutputHtmlUpdateTable) {
                String zz;
                while ((zz = fo.readLine()) != null && !zz.trim().equals("<!--EFA-START-->")) {
                    f.write(zz + "\n");
                }
            } else {
                f.write("<html>\n");
                f.write("<head>\n");
                f.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=" + Daten.ENCODING_UTF + "\">\n");
                f.write("<title>" + sr.pStatTitle + "</title>\n");
                f.write("</head>\n");
                f.write("<body>\n");
                f.write("<h1 align=\"center\">" + sr.pStatTitle + "</h1>\n");
            }

            // Start des eigentlichen Bereichs
            f.write("<!--EFA-START-->\n");

            f.write("<table align=\"center\" border>\n");
            f.write("<tr>");
            // Ausgabe des efa-Logos
            int rowspan = 5;
            /*
            if (ad.auswertungNurFuer != null) {
                rowspan++;
            }
            if (ad.auswertungWettNur != null) {
                rowspan++;
            }
            */
            //f.write("<td rowspan=\"" + rowspan + "\" width=\"180\" align=\"center\"><a href=\"" + Daten.EFAURL + "\">"+
            //        "<img src=\"" + saveImage("efa_logo.png", "png", sr.sOutputDir) + "\" width=\"128\" height=\"87\" align=\"center\" alt=\"efa\" border=\"0\"></a></td>\n");

            f.write("<td>"
                    + EfaUtil.replace(International.getString("Auswertung erstellt am"), " ", "&nbsp;", true)
                    + ":</td><td><b>" + sr.pStatCreationDate + "</b></td></tr>\n");
            f.write("<td>"
                    + EfaUtil.replace(International.getString("Auswertung erstellt von"), " ", "&nbsp;", true)
                    + ":</td><td><b><a href=\"" + sr.pStatCreatedByUrl + "\">" + sr.pStatCreatedByName + "</a></b></td></tr>\n");
            f.write("<tr><td>"
                    + EfaUtil.replace(International.getString("Art der Auswertung"), " ", "&nbsp;", true)
                    + ":</td><td><b>" + sr.pStatDescription + "</b></td></tr>\n");
            f.write("<tr><td>"
                    + EfaUtil.replace(International.getString("Zeitraum für Auswertung"), " ", "&nbsp;", true)
                    + ":</td><td><b>" + sr.pStatDateRange + "</b></td></tr>\n");
            f.write("<tr><td>"
                    + EfaUtil.replace(International.getString("Ausgewertete Einträge"), " ", "&nbsp;", true)
                    + ":</td><td><b>" + sr.pStatConsideredEntries + "</b></td></tr>\n");
            if (sr.pStatFilter != null) {
                f.write("<tr><td>"
                        + EfaUtil.replace(International.getString("Filter"), " ", "&nbsp;", true)
                        + ":</td><td><b>" + EfaUtil.replace(sr.pStatFilter,"\n","<br>",true) + "</b></td></tr>\n");
            }
            if (sr.pStatIgnored != null && sr.pStatIgnored.size() > 0) {
                f.write("<tr><td colspan=\"2\">"
                        + International.getMessage("{count} Personen oder Boote wurden von der Auswertung explizit ausgenommen.",
                        sr.pStatIgnored.size())
                        + "</td></tr>\n");
            }
            f.write("</table>\n<br><br>\n");
            
            // Warnings
            if (sr.cWarnings != null && sr.cWarnings.size() > 0) {
                String[] keys = sr.cWarnings.keySet().toArray(new String[0]);
                Arrays.sort(keys);
                StringBuilder warning = new StringBuilder();
                for (String s : keys) {
                    warning.append( (warning.length() > 0 ? "<br>\n" : "") + s);
                }
                f.write("<p><b><font color=\"red\">" + warning.toString() + "</font></b></p>");
            }

            // Auswertung von Wettbewerbseinträgen
            // Wettbewerbsbedingungen

            if (sr.pCompRules != null) {
                f.write("<table align=\"center\" bgcolor=\"#eeeeee\" border><tr><td>\n");
                for (int i = 0; i < sr.pCompRules.length; i++) {
                    if (sr.pCompRulesBold.get(new Integer(i)) != null) {
                        f.write("<b>");
                    }
                    if (sr.pCompRulesItalics.get(new Integer(i)) != null) {
                        f.write("<i>");
                    }
                    f.write(sr.pCompRules[i] + "<br>");
                    if (sr.pCompRulesItalics.get(new Integer(i)) != null) {
                        f.write("</i>");
                    }
                    if (sr.pCompRulesBold.get(new Integer(i)) != null) {
                        f.write("</b>");
                    }
                }
                f.write("</table>\n<br><br>\n");
            }

            if (sr.sIsOutputCompWithoutDetails) {
                f.write("<table align=\"center\" width=\"500\">\n");
                f.write("<tr><th colspan=\"2\" bgcolor=\"#ddddff\">Legende</th></tr>\n");
                f.write("<tr><td bgcolor=\"#00ff00\" width=\"250\" align=\"center\">"
                        + International.getString("Bedingungen erfüllt") + "</td>");
                f.write("<td bgcolor=\"#ffff00\" width=\"250\" align=\"center\">"
                        + International.getString("Bedingungen noch nicht erfüllt") + "</td></tr>\n");
                f.write("</table>\n<br><br>\n");
            }

            if (sr.pCompGroupNames != null && sr.pCompParticipants != null) {
                if (sr.pCompWarning != null) {
                    f.write("<p align=\"center\"><font color=\"#ff0000\"><b>"
                            + sr.pCompWarning + "</b></font></p>\n");
                }

                f.write("<table width=\"100%\">\n");
                for (int i = 0; i < sr.pCompGroupNames.length; i++) {
                    f.write("<tr><th align=\"left\" colspan=\"3\" bgcolor=\"#ddddff\">"
                            + sr.pCompGroupNames[i][0] + " " + sr.pCompGroupNames[i][1]
                            + " (<i>gefordert: " + sr.pCompGroupNames[i][2] + "</i>)</th></tr>\n");
                    for (StatisticsData participant = sr.pCompParticipants[i]; participant != null; participant = participant.next) {
                        f.write("<tr><td width=\"10%\">&nbsp;</td>\n");
                        if (participant.sDetailsArray == null || sr.sIsOutputCompWithoutDetails) {
                            // kurze Ausgabe
                            if (sr.sIsOutputCompWithoutDetails) {
                                f.write("<td width=\"45%\" bgcolor=\"" + (participant.compFulfilled ? "#00ff00" : "#ffff00") + "\"><b>" + participant.sName + "</b></td>"
                                        + "<td width=\"45%\" bgcolor=\"" + (participant.compFulfilled ? "#aaffaa" : "#ffffaa") + "\">" + participant.sDistance + " Km"
                                        + (participant.sAdditional == null || participant.sAdditional.equals("") ? "" : "; " + participant.sAdditional) + "</td>\n");
                            } else {
                                String additional = (participant.sAdditional == null || participant.sAdditional.equals("") ? "" : participant.sAdditional)
                                        + (participant.sCompWarning == null ? "" : "; <font color=\"red\">" + participant.sCompWarning + "</font>");
                                f.write("<td width=\"90%\" colspan=\"2\">" + (participant.compFulfilled
                                        ? International.getString("erfüllt") + ": "
                                        : International.getString("noch nicht erfüllt") + ": ") + "<b>" + participant.sName + "</b>"
                                        + (participant.sYearOfBirth != null ? " (" + participant.sYearOfBirth + ")" : "")
                                        + ": " + participant.sDistance + " Km"
                                        + (additional.length() > 0 ? " (" + additional + ")" : "")
                                        + "</td>\n");
                            }
                        } else {
                            // ausführliche Ausgabe
                            f.write("<td width=\"90%\" colspan=\"2\">\n");
                            int colspan = 1;
                            if (participant.sDetailsArray.length > 0) {
                                colspan = participant.sDetailsArray[0].length;
                            }
                            f.write("<table border>\n<tr><td colspan=\"" + colspan + "\"><b>" + participant.sName + " (" + participant.sYearOfBirth + "): "
                                    + participant.sDistance + " Km" + (participant.sAdditional != null ? "; " + participant.sAdditional : "") + "</b></td></tr>\n");
                            if (participant.sDetailsArray.length > 0) {
                                for (int j = 0; j < participant.sDetailsArray.length; j++) {
                                    f.write("<tr>");
                                    if (participant.sDetailsArray[j] != null && participant.sDetailsArray[j][0] != null) {
                                        for (int k = 0; k < participant.sDetailsArray[j].length; k++) {
                                            f.write("<td>" + participant.sDetailsArray[j][k] + "</td>");
                                        }
                                    } else {
                                        f.write("<td colspan=\"" + colspan + "\">und weitere Fahrten</td>");
                                    }
                                    f.write("</tr>\n");
                                }
                            }
                            f.write("</table>\n</td>\n");
                        }
                        f.write("</tr>\n");
                    }
                    f.write("<tr colspan=\"3\"><td>&nbsp;</td></tr>\n");
                }
                f.write("</table>\n");
            }
            

            // Auswertung normaler Einträge
            if (sr.pTableColumns != null && sr.pTableColumns.size() > 0) {
                StatisticsData sdMaximum = sd[sd.length-1]; // Maximum is always last
                if (!sdMaximum.isMaximum) {
                    sdMaximum = null;
                }
                f.write("<table align=\"center\" bgcolor=\"#ffffff\" border>\n<tr>\n");
                for (int i=0; i<sr.pTableColumns.size(); i++) {
                    //f.write("<th" + (ad.tabellenTitelBreite != null ? " colspan=\"" + ad.tabellenTitelBreite[i] + "\"" : "") + ">" + ad.tabellenTitel[i] + "</th>");
                    f.write("<th>" + sr.pTableColumns.get(i) + "</th>");
                }
                f.write("</tr>\n");

                // Einträge auswerten
                for (int i=0; i<sd.length; i++) {
                    if (sd[i].isMaximum) {
                        continue;
                    }
                    if (!sd[i].isSummary) {
                        f.write("<tr bgcolor=\"" + (sd[i].absPosition % 2 == 0 ? "#eeeeff" : "#ccccff") + "\">");
                    } else {
                        f.write("<tr>");
                    }

                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                        outHTML(f, sd[i].sPosition, true, null);
                        outHTML(f, sd[i].sName, false, null);
                        outHTML(f, sd[i].sStatus, false, null);
                        outHTML(f, sd[i].sYearOfBirth, false, null);
                        outHTML(f, sd[i].sBoatType, false, null);
                        outHTML(f, sd[i].sDistance, sd[i].distance,
                                (sdMaximum != null && !sd[i].isSummary ? sdMaximum.distance : 0),
                                "blue", sr.sAggrDistanceBarSize);
                        outHTML(f, sd[i].sSessions, sd[i].sessions,
                                (sdMaximum != null && !sd[i].isSummary ? sdMaximum.sessions : 0),
                                "red", sr.sAggrSessionsBarSize);
                        outHTML(f, sd[i].sAvgDistance, sd[i].avgDistance,
                                (sdMaximum != null && !sd[i].isSummary ? sdMaximum.avgDistance : 0),
                                "green", sr.sAggrAvgDistanceBarSize);
                        outHTML(f, sd[i].sDestinationAreas, false, null);
                        outHTML(f, sd[i].sWanderfahrten, true, null);
                        /*
                        outHTML(f, ae.anzversch, false, null);
                        outHTMLgra(f, ae, ae.km, ae.colspanKm);
                        outHTMLgra(f, ae, ae.rudkm, ae.colspanRudKm);
                        outHTMLgra(f, ae, ae.stmkm, ae.colspanStmKm);
                        outHTMLgra(f, ae, ae.fahrten, ae.colspanFahrten);
                        outHTMLgra(f, ae, ae.kmfahrt, ae.colspanKmFahrt);
                        outHTMLgra(f, ae, ae.dauer, ae.colspanDauer);
                        outHTMLgra(f, ae, ae.kmh, ae.colspanKmH);
                        outHTML(f, ae.wafaKm, false, null);
                        outHTML(f, ae.zielfahrten, false, null);
                        outHTML(f, ae.zusatzDRV, false, null);
                        outHTML(f, ae.zusatzLRVBSommer, false, null);
                        outHTML(f, ae.zusatzLRVBWinter, false, null);
                        outHTML(f, ae.zusatzLRVBrbWanderWett, false, null);
                        outHTML(f, ae.zusatzLRVBrbFahrtenWett, false, null);
                        outHTML(f, ae.zusatzLRVMVpWanderWett, false, null);
                         */
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
                        if (sd[i].logbookFields != null) {
                            for (int j = 0; j < sd[i].logbookFields.length; j++) {
                                outHTML(f, (sd[i].logbookFields[j] != null
                                        ? sd[i].logbookFields[j] : ""), false, null);
                            }
                        }
                    }
                    /*
                    if (ae.ww != null) {
                        for (int i = 0; i < ae.ww.length; i++) {
                            outHTML(f, ae.ww[i], false, (ae.ww_selbst[i] ? "ffdddd" : null));
                        }
                    }
                    */
                    f.write("</tr>\n");
                }

                f.write("</table>\n");
            }

            // Zusatzdaten
            if (sr.pAdditionalTable1 != null) {
                printTable(f, sr.pAdditionalTable1Title, sr.pAdditionalTable1, 
                        sr.pAdditionalTable1FirstRowBold, sr.pAdditionalTable1LastRowBold);
            }
            if (sr.pAdditionalTable2 != null) {
                printTable(f, sr.pAdditionalTable2Title, sr.pAdditionalTable2,
                        sr.pAdditionalTable2FirstRowBold, sr.pAdditionalTable2LastRowBold);
            }

            /*
            if (ad.tfe != null) {
                schreibeHTMLTabellenFolge(f, ad.tfe);
            }

            if (ad.ausgabeZeilenUnten != null) {
                schreibeHTMLZeilen(f, ad.ausgabeZeilenUnten);
            }
            */

            // Ende des eigentlichen Bereichs
            if (sr.sOutputHtmlUpdateTable) {
                String zz;
                while ((zz = fo.readLine()) != null && !zz.trim().equals("<!--EFA-ENDE-->"));
                f.write("\n<!--EFA-ENDE-->\n");
                while ((zz = fo.readLine()) != null) {
                    f.write(zz + "\n");
                }
                fo.close();
                File bak = new File(tmpFile);
                bak.delete();
            } else {
                f.write("\n<!--EFA-ENDE-->\n");
                f.write("</body>\n");
                f.write("</html>\n");
            }
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

    void outHTML(BufferedWriter f, String s, boolean right, String color) throws IOException {
        if (s != null) {
            f.write("<td"
                    + (color != null ? " bgcolor=\"#" + color + "\"" : "")
                    + (right ? " align=\"right\"" : "")
                    + ">"
                    + (s.length() > 0 ? EfaUtil.escapeHtml(s) : "&nbsp;")
                    + "</td>\n");
        }
    }

    void outHTML(BufferedWriter f, String s, long value, long maximum, String colorBar, long barSize) throws IOException {
        if (s != null) {
            f.write("<td" + (colorBar == null || barSize == 0 ? " align=\"right\"" : "") + ">");
            if (colorBar != null && barSize != 0 && value != 0 && maximum > 0) {
                long width = value*barSize / maximum;
                if (width <= 0) {
                    width = 1;
                }
                f.write("<img src=\"" + 
                        EfaUtil.saveImage("color_" + colorBar + ".gif", "gif", sr.sOutputDir, true, false) +
                        "\" width=\"" + width + "\" height=\"20\" alt=\"\">&nbsp;");
            }
            if (s.length() > 0) {
                f.write(EfaUtil.escapeHtml(s));
            }
            f.write("</td>\n");
        }
    }

    private void printTable(BufferedWriter f, String[] header, String[][] data,
            boolean firstRowBold, boolean lastRowBold) throws IOException {
        f.write("<br><table border align=\"center\">\n");
        if (header != null) {
            f.write("<tr>");
            for (int i = 0; header != null && i < header.length; i++) {
                f.write("<th>" + header[i] + "</th>");
            }
            f.write("</tr>\n");
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                continue;
            }
            f.write("<tr>");
            for (int j = 0; j < data[i].length; j++) {
                f.write("<td>"
                        + ((i == 0 && firstRowBold) || (i == data.length - 1 && lastRowBold) ? "<b>" : "")
                        + data[i][j]
                        + ((i == 0 && firstRowBold) || (i == data.length - 1 && lastRowBold) ? "</b>" : "")
                        + "</td>");
            }
            f.write("</tr>\n");
        }
        f.write("</table><br>\n");
    }
}
