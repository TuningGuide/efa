/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa;

import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.International;

// @i18n complete
public class Program {

    public Program(String[] args) {
        checkArgs(args);
        Daten.program = this;
    }

    public void printOption(String option, String description) {
        while(option.length() < 15) {
            option = option + " ";
        }
        System.out.println("      " + option + "   " + description);
    }

    public void printUsage(String wrongArgument) {
        boolean showHelpDev = false;
        if (wrongArgument != null && wrongArgument.equals("-helpdev")) {
            wrongArgument = null;
            showHelpDev = true;
        }
        System.out.println(Daten.EFA_SHORTNAME + " " + Daten.VERSION + " (" + Daten.VERSIONID + ")\n");
        if (wrongArgument != null) {
            System.out.println(International.getString("Unbekanntes Argument") + ": " + wrongArgument);
        }
        System.out.println(International.getString("Benutzung") + ": " +
                           "java [javaopt] " + 
                           this.getClass().getCanonicalName() +
                           " [option]");
        System.out.println("    [javaopt]:");
        System.out.println("      " + International.getString("Optionen der Java Virtual Machine") + " ('java -help')");
        System.out.println("    [option]:");
        printOption("-help",International.getString("diese Hilfemeldung anzeigen"));
        printOption("-javaRestart", International.getString("Neustart von efa durch Java statt Shell"));
        if (showHelpDev) {
            System.out.println("    Parameters for development use:");
            printOption("-debug","Activate Debug Logging");
            printOption("-traceTopic <topic>","Set Trace Topic <topic> for Debug Logging");
            printOption("-wws", "Watch Window Stack (report window stack inconsistencies)");
            printOption("-exc", "Exception Test (press [F1] in main window)");
            printOption("-emulateWin", "Emulate Windows Environment");
            printOption("-i18nMarkMissing", "i18n: Mark Missing Keys");
            printOption("-i18nLogMissing", "i18n: Log Missing Keys (requires flag -debug as well)");
            printOption("-i18nTraceMissing", "i18n: Stack Trace Missing Keys");
            printOption("-i18nShowKeys", "i18n: Show Keys instead of Translation");
        }
    }

    public void checkArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {

            // "official" options
            if (args[i].equals("-help")) {
                printUsage(null);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-javaRestart")) {
                Daten.javaRestart = true;
                args[i] = null;
                continue;
            }

            // developer options
            if (args[i].equals("-debug")) {
                Logger.setDebugLogging(true,true);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-traceTopic")) {
                if (args.length > i+1) {
                    Logger.setTraceTopic(args[i+1],true);
                    args[i] = null;
                    args[++i] = null;
                }
                continue;
            }
            if (args[i].equals("-helpdev")) {
                printUsage(args[i]);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-wws")) {
                Daten.watchWindowStack = true;
                args[i] = null;
                continue;
            }
            if (args[i].equals("-exc")) {
                Daten.exceptionTest = true;
                args[i] = null;
                continue;
            }
            if (args[i].equals("-emulateWin")) {
                System.setProperty("os.name","Windows XP");
                System.setProperty("os.arch","x86");
                System.setProperty("os.version","5.1");
            }
            if (args[i].equals("-i18nMarkMissing")) {
                International.setMarkMissingKeys(true);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-i18nLogMissing")) {
                International.setLogMissingKeys(true);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-i18nTraceMissing")) {
                International.setTraceMissingKeys(true);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-i18nShowKeys")) {
                International.setShowKeys(true);
                args[i] = null;
                continue;
            }
        }
    }

    public void checkRemainingArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                printUsage(args[i]);
            }
        }
    }


    public void exit(int exitCode) {
        if (Daten.efaRunning != null) {
            Daten.efaRunning.closeServer();
        }
        System.exit(exitCode);
    }

}
