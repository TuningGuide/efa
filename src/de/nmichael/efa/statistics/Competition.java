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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.EfaWett;
import de.nmichael.efa.core.WettDef;
import de.nmichael.efa.data.StatisticsRecord;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.LogString;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

public abstract class Competition {

    protected AusgabeEintrag letzterAusgabeEintrag;
    protected Hashtable nichtBeruecksichtigt = new Hashtable(); // Bei Wettbewerben nicht berücksichtigte Mitglieder (z.B. weil Jahrgang fehlt oder Wettbewerbsmeldungen deaktiviert sind)
    protected EfaWett efaWett; // Zusammenstellung aller Wettbewerbsdaten für Erstellung einer Meldedatei

    public static Competition getCompetition(StatisticsRecord sr) {
        if (sr.getName().indexOf("DRV") < 0) { // @todo (P2) statistics
            return new CompetitionLRVBSommer();
        } else {
            return null;
        }
    }

    public abstract void calculate(StatisticsRecord sr, StatisticsData[] sd);

  // Eine int-Zahl (Jahrgang) in einen String umwandeln
    protected String makeJahrgang(int jahr) {
        if (jahr <= 0) {
            return "????";
        } else {
            return Integer.toString(jahr);
        }
    }

    // Eine int-Zahl (Geschlecht) in einen String umwandeln
    protected String makeGeschlecht(int g) {
        switch (g) {
            case 0:
                return International.getString("m", "gender");
            case 1:
                return International.getString("w", "gender");
            default:
                return International.getString("m/w", "gender");
        }
    }

    // Prüfen, ob gewählter Zeitraum tatsächlich den Wettbewerbsbedingungen entspricht; true, falls korrekt
    protected boolean checkWettZeitraum(int wettJahr, DataTypeDate from, DataTypeDate to, int wettnr) {
        WettDef wett = Daten.wettDefs.getWettDef(wettnr, wettJahr);
        if (wett == null) {
            return false;
        }
        return (from.getDay() == wett.von.tag
                && from.getMonth() == wett.von.monat
                && to.getDay() == wett.bis.tag
                && to.getMonth() == wett.bis.monat
                && from.getYear() == wettJahr + wett.von.jahr
                && to.getYear() == wettJahr + wett.bis.jahr);
    }

    protected String[] createAusgabeBedingungen(StatisticsRecord sr, String bezeich, Hashtable fett, Hashtable kursiv) {
        if (!sr.sIsOutputCompRules) {
            return null;
        }

        Vector _zeil = new Vector(); // Zeilen

        BufferedReader f;
        String s;
        String dir = Daten.efaCfgDirectory;
        try {
            if (!new File(dir + Daten.WETTFILE).isFile() && new File(Daten.efaProgramDirectory + Daten.WETTFILE).isFile()) {
                dir = Daten.efaProgramDirectory;
            }
            f = new BufferedReader(new InputStreamReader(new FileInputStream(dir + Daten.WETTFILE), Daten.ENCODING_ISO));
            while ((s = f.readLine()) != null) {
                if (s.startsWith("[" + bezeich + "]")) {
                    while ((s = f.readLine()) != null) {
                        if (s.length() > 0 && s.charAt(0) == '[') {
                            break;
                        }
                        if (s.length() > 0 && s.charAt(0) == '*') {
                            fett.put(new Integer(_zeil.size()), "fett");
                            s = s.substring(1, s.length());
                        }
                        if (s.length() > 0 && s.charAt(0) == '#') {
                            kursiv.put(new Integer(_zeil.size()), "kursiv");
                            s = s.substring(1, s.length());
                        }
                        if (s.length() > 0) {
                            s = EfaUtil.replace(s, "%Y+", Integer.toString(sr.sCompYear + 1), true);
                        }
                        if (s.length() > 0) {
                            s = EfaUtil.replace(s, "%Y", Integer.toString(sr.sCompYear), true);
                        }
                        _zeil.add(s);
                    }
                }
            }
            f.close();
        } catch (FileNotFoundException e) {
            Dialog.error(LogString.logstring_fileNotFound(dir + Daten.WETTFILE, International.getString("Wettbewerbskonfiguration")));
        } catch (IOException e) {
            Dialog.error(LogString.logstring_fileReadFailed(dir + Daten.WETTFILE, International.getString("Wettbewerbskonfiguration")));
        }
        String[] zeilen = new String[_zeil.size()];
        _zeil.toArray(zeilen);
        return zeilen;
    }
}
