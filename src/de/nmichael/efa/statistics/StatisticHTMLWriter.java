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
import java.awt.image.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.data.types.DataTypeDistance;

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

            if (sr.sStatisticType == StatisticsRecord.StatisticTypes.competition) {
                f.write("<p align=\"center\"><b style=\"color:red\">Wettbewerbsauswertungen sind derzeit noch nicht möglich.<br>Ich bitte vielmals um Entschuldigung!<br>In Kürze werden die Auswertungen wieder zur Verfügung stehen!</b><p>\n");
            }

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
                    + International.getString("Auswertung erstellt am")
                    + ":</td><td><b>" + sr.pStatCreationDate + "</b></td></tr>\n");
            f.write("<td>"
                    + International.getString("Auswertung erstellt von")
                    + ":</td><td><b><a href=\"" + sr.pStatCreatedByUrl + "\">" + sr.pStatCreatedByName + "</a></b></td></tr>\n");
            f.write("<tr><td>"
                    + International.getString("Art der Auswertung")
                    + ":</td><td><b>" + sr.pStatDescription + "</b></td></tr>\n");
            f.write("<tr><td>"
                    + International.getString("Zeitraum für Auswertung")
                    + ":</td><td><b>" + sr.pStatDateRange + "</b></td></tr>\n");
            f.write("<tr><td>"
                    + International.getString("Ausgewertete Einträge")
                    + ":</td><td><b>" + sr.pStatConsideredEntries + "</b></td></tr>\n");
            /*
            f.write("<tr><td>"
                    + International.getString("Auswertung für")
                    + ":</td><td><b>");
            for (int i = 0; i < ad.auswertungFuer.length; i++) {
                f.write((i > 0 ? "<br>" : "") + ad.auswertungFuer[i]);
            }
            f.write("</b></td></tr>\n");
            if (ad.auswertungNurFuer != null) {
                f.write("<tr><td>"
                        + International.getString("nur für")
                        + " " + ad.auswertungNurFuerBez + ":</td><td><b>" + ad.auswertungNurFuer + "</b></td></tr>\n");
            }
            if (ad.auswertungWettNur != null) {
                f.write("<tr><td>"
                        + International.getString("Ausgabe, wenn")
                        + ":</td><td><b>" + ad.auswertungWettNur + "</b></td></tr>\n");
            }
            */
            f.write("</table>\n<br><br>\n");


            // Auswertung von Wettbewerbseinträgen
            // Wettbewerbsbedingungen
            /*
            if (ad.wett_bedingungen != null) {
                f.write("<table align=\"center\" bgcolor=\"#eeeeee\" border><tr><td>\n");
                for (int i = 0; i < ad.wett_bedingungen.length; i++) {
                    if (ad.wett_bedingungen_fett.get(new Integer(i)) != null) {
                        f.write("<b>");
                    }
                    if (ad.wett_bedingungen_kursiv.get(new Integer(i)) != null) {
                        f.write("<i>");
                    }
                    f.write(ad.wett_bedingungen[i] + "<br>");
                    if (ad.wett_bedingungen_kursiv.get(new Integer(i)) != null) {
                        f.write("</i>");
                    }
                    if (ad.wett_bedingungen_fett.get(new Integer(i)) != null) {
                        f.write("</b>");
                    }
                }
                f.write("</table>\n<br><br>\n");
            }

            if (sr.wettOhneDetail) {
                f.write("<table align=\"center\" width=\"500\">\n");
                f.write("<tr><th colspan=\"2\" bgcolor=\"#ddddff\">Legende</th></tr>\n");
                f.write("<tr><td bgcolor=\"#00ff00\" width=\"250\" align=\"center\">"
                        + International.getString("Bedingungen erfüllt") + "</td>");
                f.write("<td bgcolor=\"#ffff00\" width=\"250\" align=\"center\">"
                        + International.getString("Bedingungen noch nicht erfüllt") + "</td></tr>\n");
                f.write("</table>\n<br><br>\n");
            }

            if (ad.ausgabeZeilenOben != null) {
                schreibeHTMLZeilen(f, ad.ausgabeZeilenOben);
            }

            if (ad.wett_gruppennamen != null && ad.wett_teilnehmerInGruppe != null) {
                if (ad.wett_zeitraumWarnung != null) {
                    f.write("<p align=\"center\"><font color=\"#ff0000\"><b>"
                            + ad.wett_zeitraumWarnung + "</b></font></p>\n");
                }

                f.write("<table width=\"100%\">\n");
                for (int i = 0; i < ad.wett_gruppennamen.length; i++) {
                    f.write("<tr><th align=\"left\" colspan=\"3\" bgcolor=\"#ddddff\">"
                            + ad.wett_gruppennamen[i][0] + " " + ad.wett_gruppennamen[i][1]
                            + " (<i>gefordert: " + ad.wett_gruppennamen[i][2] + "</i>)</th></tr>\n");
                    for (ae = ad.wett_teilnehmerInGruppe[i]; ae != null; ae = ae.next) {
                        f.write("<tr><td width=\"10%\">&nbsp;</td>\n");
                        if (ae.w_detail == null) {
                            // kurze Ausgabe
                            if (sr.wettOhneDetail) {
                                f.write("<td width=\"45%\" bgcolor=\"" + (ae.w_erfuellt ? "#00ff00" : "#ffff00") + "\"><b>" + ae.w_name + "</b></td>"
                                        + "<td width=\"45%\" bgcolor=\"" + (ae.w_erfuellt ? "#aaffaa" : "#ffffaa") + "\">" + ae.w_kilometer + " Km"
                                        + (ae.w_additional == null || ae.w_additional.equals("") ? "" : "; " + ae.w_additional) + "</td>\n");
                            } else {
                                String additional = (ae.w_additional == null || ae.w_additional.equals("") ? "" : ae.w_additional)
                                        + (ae.w_warnung == null ? "" : "; <font color=\"red\">" + ae.w_warnung + "</font>");
                                f.write("<td width=\"90%\" colspan=\"2\">" + (ae.w_erfuellt
                                        ? International.getString("erfüllt") + ": "
                                        : International.getString("noch nicht erfüllt") + ": ") + "<b>" + ae.w_name + "</b>"
                                        + (ae.w_jahrgang != null ? " (" + ae.w_jahrgang + ")" : "")
                                        + ": " + ae.w_kilometer + " Km"
                                        + (additional.length() > 0 ? " (" + additional + ")" : "")
                                        + "</td>\n");
                            }
                        } else {
                            // ausführliche Ausgabe
                            f.write("<td width=\"90%\" colspan=\"2\">\n");
                            int colspan = 1;
                            if (ae.w_detail.length > 0) {
                                colspan = ae.w_detail[0].length;
                            }
                            f.write("<table border>\n<tr><td colspan=\"" + colspan + "\"><b>" + ae.w_name + " (" + ae.w_jahrgang + "): "
                                    + ae.w_kilometer + " Km" + (ae.w_additional != null ? "; " + ae.w_additional : "") + "</b></td></tr>\n");
                            if (ae.w_detail.length > 0) {
                                for (int j = 0; j < ae.w_detail.length; j++) {
                                    f.write("<tr>");
                                    if (ae.w_detail[j] != null && ae.w_detail[j][0] != null) {
                                        for (int k = 0; k < ae.w_detail[j].length; k++) {
                                            f.write("<td>" + ae.w_detail[j][k] + "</td>");
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
            */

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
                    /*
                    if (ae.fahrtenbuch != null) {
                        for (int i = 0; i < ae.fahrtenbuch.length; i++) {
                            if (ae.fahrtenbuch[i] != null) {
                                outHTML(f, ae.fahrtenbuch[i], false, null);
                            }
                        }
                    }
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

            /*
            // Zusatzdaten
            if (ad.additionalTable != null) {
                f.write("<br><table border align=\"center\">\n");
                for (int i = 0; i < ad.additionalTable.length; i++) {
                    f.write("<tr>");
                    for (int j = 0; j < ad.additionalTable[i].length; j++) {
                        f.write("<td>"
                                + ((i == 0 && ad.additionalTable_1stRowBold) || (i == ad.additionalTable.length - 1 && ad.additionalTable_lastRowBold) ? "<b>" : "")
                                + ad.additionalTable[i][j]
                                + ((i == 0 && ad.additionalTable_1stRowBold) || (i == ad.additionalTable.length - 1 && ad.additionalTable_lastRowBold) ? "</b>" : "")
                                + "</td>");
                    }
                    f.write("</tr>\n");
                }
                f.write("</table><br>\n");
            }

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

    void outHTML(BufferedWriter f, String s, boolean right, String color) throws IOException {
        if (s != null) {
            f.write("<td"
                    + (color != null ? " bgcolor=\"#" + color + "\"" : "")
                    + (right ? " align=\"right\"" : "")
                    + ">"
                    + (s.length() > 0 ? s : "&nbsp;")
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
                f.write("<img src=\"" + saveImage("color_" + colorBar + ".gif", "gif", sr.sOutputDir) + "\" width=\"" + width + "\" height=\"20\" alt=\"\">&nbsp;");
            }
            if (s.length() > 0) {
                f.write(s);
            }
            f.write("</td>\n");
        }
    }

    static void outHTMLgra(BufferedWriter f, AusgabeEintrag ae, String[] s, int colspan) throws IOException {
        if (s != null && s[0] != null) {
            if (colspan == 1 || s[1] == null || s[2] == null) {
                // "normale" Ausgabe (kein Vorjahresvergleich) oder nicht grafische Ausgabe
                f.write("<td" + (s[1] == null ? " align=\"right\"" : "") + ">");
                if (s[1] != null && s[2] != null && !s[2].equals("0")) {
                    f.write("<img src=\"" + s[1] + "\" width=\"" + s[2] + "\" height=\"20\" alt=\"\">&nbsp;");
                }
                f.write(s[0] + "</td>\n");
            } else {
                // Ausgabe für Vorjahresvergleich bei grafischer Ausgabe mit zwei Tabellenfeldern
                int wert = EfaUtil.zehntelString2Int(s[0]);
                // Null-Wert zentriert über beide Spalten
                if (wert == 0) {
                    f.write("<td align=\"center\" colspan=\"2\">" + s[0] + "</td>");
                } else {
                    // linke Spalte für negative Werte
                    f.write("<td align=\"right\">");
                    if (wert < 0) {
                        f.write(s[0]);
                        if (!s[2].equals("0")) {
                            f.write("&nbsp;<img src=\"" + s[1] + "\" width=\"" + Math.abs(EfaUtil.string2int(s[2], 0)) + "\" height=\"20\" alt=\"\">");
                        }
                    } else {
                        f.write("&nbsp;");
                    }
                    f.write("</td>");
                    // rechte Spalte für positive Werte
                    f.write("<td align=\"left\">");
                    if (wert > 0) {
                        if (!s[2].equals("0")) {
                            f.write("<img src=\"" + s[1] + "\" width=\"" + s[2] + "\" height=\"20\" alt=\"\">&nbsp;");
                        }
                        f.write(s[0]);
                    } else {
                        f.write("&nbsp;");
                    }
                    f.write("</td>");
                }
            }
        }
    }

    private String saveImage(String image, String format, String dir) {
        String fname = dir + image;
        if (!EfaUtil.canOpenFile(fname)) {
            try {
                BufferedImage img = javax.imageio.ImageIO.read(StatisticHTMLWriter.class.getResource(Daten.IMAGEPATH + image));
                javax.imageio.ImageIO.write(img, format, new File(fname));
            } catch (Exception e) {
                Logger.logdebug(e);
            }
        }
        if (Daten.fileSep.equals("\\")) {
            fname = "/" + EfaUtil.replace(fname, "\\", "/", true);
        }
        return "file://" + fname;
    }
}
