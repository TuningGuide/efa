/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.cli;

import de.nmichael.efa.*;
import de.nmichael.efa.core.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.statistics.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;

// @i18n complete

public class Main extends Program {

    static String fb = null;   // Fahrtenbuch zum Starten
    static String stat = null; // Statistik, die berechnet werden soll
    static String importFb = null;
    static String lfdNrAdd = null;
    static String lfdNrAddChar = null;
    static String lfdNrVon = null;
    static String lfdNrBis = null;
    static String datumVon = null;
    static String datumBis = null;
    static String gruppe = null;
    static boolean importDatenlisten = false;
    static String beiDoppelterLfdNr = null;

    public Main(String[] args) {
        super(args);
        if (args.length == 0) {
            printUsage(null);
        }
        Daten.initialize(Daten.APPL_CLI);
        if (stat != null || importFb != null) {
            if (fb == null || fb.equals("efa") || fb.equals("efadirekt")) {
                if (fb == null || fb.equals("efa")) {
                    fb = Daten.efaConfig.letzteDatei.getValue();
                } else if (fb != null && fb.equals("efadirekt")) {
                    fb = Daten.efaConfig.direkt_letzteDatei.getValue();
                }
                if (fb == null) {
                    Logger.log(Logger.ERROR, Logger.MSG_GENERIC_ERROR, International.getString("Kein Fahrtenbuch ausgewählt"));
                    printUsage(null);
                }
            }
            Logger.log(Logger.INFO, Logger.MSG_GENERIC, International.getString("Fahrtenbuch") + ": " + fb);
            if (!EfaUtil.canOpenFile(fb)) {
                LogString.logError_fileNotFound(fb, International.getString("Fahrtenbuch"));
                Daten.haltProgram(Daten.HALT_FILEOPEN);
            }
            if (stat != null) {
                createStat();
            } else if (importFb != null) {
                doImportFb();
            }
        }
        Daten.haltProgram(0);
    }

    public void printUsage(String wrongArgument) {
        super.printUsage(wrongArgument);
        printOption("-open <file>", International.getString("Fahrtenbuch <file> öffnen") +
                " [efa/efadirekt = " + International.getString("zuletzt geöffnetes Fahrtenbuch") + "]");
        printOption("-stat <stat>", International.getString("Statistik <stat> erstellen"));
        printOption("-import <file>", International.getString("Fahrtenbuch <file> importieren"));
        printOption("-noAdd <n>", International.getString("<n> zu LfdNr hinzuaddieren"));
        printOption("-noAddChar <c>", International.getString("<c> an LfdNr anfügen"));
        printOption("-noFrom <n>", International.getString("ab LfdNr <n> importieren"));
        printOption("-noTo <n>", International.getString("bis LfdNr <n> importieren"));
        printOption("-dateFrom <DD.MM.YYYY>", International.getString("ab Datum importieren"));
        printOption("-dateTo <DD.MM.YYYY>", International.getString("bis zu Datum importieren"));
        printOption("-group <name>", International.getString("nur Gruppe <name> importieren"));
        printOption("-importLists", International.getString("alle Datenlisten importieren"));
        printOption("-duplicates [c|e|n]", International.getString("bei doppelten Einträgen c=Bustaben anhängen; e=am Ende einfügen; n=nicht importieren"));
        System.exit(0);
    }

    public void checkArgs(String[] args) {
        super.checkArgs(args);
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue; // argument already handled by super class
            }
            if (args[i].equals("-open") && i + 1 < args.length) {
                args[i] = null;
                fb = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-stat") && i + 1 < args.length) {
                args[i] = null;
                stat = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-import") && i + 1 < args.length) {
                args[i] = null;
                importFb = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-noAdd") && i + 1 < args.length) {
                args[i] = null;
                lfdNrAdd = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-noAddChar") && i + 1 < args.length) {
                args[i] = null;
                lfdNrAddChar = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-noFrom") && i + 1 < args.length) {
                args[i] = null;
                lfdNrVon = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-noTo") && i + 1 < args.length) {
                args[i] = null;
                lfdNrBis = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-dateFrom") && i + 1 < args.length) {
                args[i] = null;
                datumVon = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-dateTo") && i + 1 < args.length) {
                args[i] = null;
                datumBis = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-group") && i + 1 < args.length) {
                args[i] = null;
                gruppe = args[++i];
                args[i] = null;
                continue;
            }
            if (args[i].equals("-importLists") && i + 1 < args.length) {
                args[i] = null;
                importDatenlisten = true;
                continue;
            }
            if (args[i].equals("-duplicates") && i + 1 < args.length) {
                args[i] = null;
                beiDoppelterLfdNr = args[++i];
                args[i] = null;
                continue;
            }
}
        checkRemainingArgs(args);
    }

    public static void main(String[] args) {
        new Main(args);
    }

    // Aufgrund von Aufruf-Parametern eine Statistik erstellen
    void createStat() {
        Daten.wettDefs = new WettDefs(Daten.efaCfgDirectory + Daten.WETTDEFS);
        if (!Daten.wettDefs.readFile()) {
            LogString.logError_fileOpenFailed(Daten.wettDefs.getFileName(), International.getString("Wettbewerbsdefinitionen"));
            Daten.haltProgram(Daten.HALT_FILEOPEN);
        }
        Daten.fahrtenbuch = new Fahrtenbuch(fb);
        if (!Daten.fahrtenbuch.readFile()) {
            LogString.logError_fileOpenFailed(fb, International.getString("Fahrtenbuch"));
            Daten.haltProgram(Daten.HALT_FILEOPEN);
        }
        Daten.fahrtenbuch.getDaten().statistik.getFirst(stat);
        DatenFelder f = (DatenFelder) Daten.fahrtenbuch.getDaten().statistik.getComplete();
        StatistikDaten[] sd = new StatistikDaten[1];
        if (f != null) {
            try {
                sd[0] = StatistikFrame.getSavedValues(f);
                StatistikFrame.allgStatistikDaten(sd[0]);
                Logger.log(Logger.INFO, Logger.MSG_GENERIC, International.getMessage("Erstelle Statistik {stat} ...", stat));
                Statistik.create(sd);
                Logger.log(Logger.INFO, Logger.MSG_GENERIC, International.getString("Fertig") + ".");
                Daten.haltProgram(0);
            } catch (StringIndexOutOfBoundsException e) {
                Logger.log(Logger.ERROR, Logger.MSG_GENERIC_ERROR, International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
            }
        } else {
            Logger.log(Logger.ERROR, Logger.MSG_GENERIC_ERROR, International.getMessage("Statistik {stat} nicht gefunden!", stat));
            Daten.haltProgram(Daten.HALT_MISCONFIG);
        }
    }

    void doImportFb() {
        if (importFb == null) {
            Logger.log(Logger.ERROR, Logger.MSG_GENERIC_ERROR, International.getString("Kein zu importierendes Fahrtenbuch ausgewählt"));
            printUsage(null);
        }
        if (lfdNrAdd == null) {
            lfdNrAdd = "";
        }
        if (lfdNrAddChar == null) {
            lfdNrAddChar = "";
        }
        if (lfdNrVon == null) {
            lfdNrVon = "";
        }
        if (lfdNrBis == null) {
            lfdNrBis = "";
        }
        if (datumVon == null) {
            datumVon = "";
        }
        if (datumBis == null) {
            datumBis = "";
        }
        if (beiDoppelterLfdNr == null) {
            beiDoppelterLfdNr = "e";
        }
        int idopp = -1;
        if (beiDoppelterLfdNr.equals("c")) {
            idopp = 0;
        } else if (beiDoppelterLfdNr.equals("e")) {
            idopp = 1;
        } else if (beiDoppelterLfdNr.equals("n")) {
            idopp = 2;
        } else {
            Logger.log(Logger.ERROR, Logger.MSG_GENERIC_ERROR,
                    International.getMessage("Ungültiger Wert im Feld '{fieldname}'","duplicates"));
            printUsage(null);
        }
        if (!EfaUtil.canOpenFile(importFb)) {
            LogString.logError_fileOpenFailed(importFb, International.getString("Fahrtenbuch"));
            printUsage(null);
        }
        if (!EfaUtil.canOpenFile(fb)) {
            LogString.logError_fileOpenFailed(fb, International.getString("Fahrtenbuch"));
            printUsage(null);
        }
        Fahrtenbuch quellFb = new Fahrtenbuch(importFb);
        Fahrtenbuch zielFb = new Fahrtenbuch(fb);
        if (!quellFb.readFile()) {
            LogString.logError_fileReadFailed(quellFb.getFileName(), International.getString("Fahrtenbuch"));
            printUsage(null);
        }
        if (!zielFb.readFile()) {
            LogString.logError_fileReadFailed(zielFb.getFileName(), International.getString("Fahrtenbuch"));
            printUsage(null);
        }
        String result = ImportFrame.doImport(quellFb, zielFb, lfdNrAdd, lfdNrAddChar, lfdNrVon, lfdNrBis, datumVon, datumBis, gruppe, importDatenlisten, idopp);
        if (!zielFb.writeFile()) {
            LogString.logError_fileWritingFailed(zielFb.getFileName(), International.getString("Fahrtenbuch"));
            printUsage(null);
        }
        if (result == null) {
            Logger.log(Logger.INFO, Logger.MSG_GENERIC,
                    International.getMessage("{count} Einträge erfolgreich importiert.",ImportFrame.anzImportierteFahrten));
            Daten.haltProgram(0);
        } else {
            Logger.log(Logger.ERROR, Logger.MSG_GENERIC_ERROR,
                    International.getString("Fehler") + ": " + result);
            Daten.haltProgram(Daten.HALT_ERROR);
        }
    }

}