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

import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.LogString;

// @i18n complete
public class Program {

    private AdminRecord newlyCreatedAdminRecord;

    public Program(int applId, String[] args) {
        Daten.program = this;
        Daten.iniBase(applId);
        checkArgs(args);
        newlyCreatedAdminRecord = Daten.initialize();
    }

    protected AdminRecord getNewlyCreatedAdminRecord() {
        return newlyCreatedAdminRecord;
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
        System.out.println(Daten.EFA_LONGNAME + " " + Daten.VERSION + " (" + Daten.VERSIONID + ")\n");
        if (wrongArgument != null) {
            System.out.println("ERROR: Unknown Argument" + ": " + wrongArgument+"\n");
        }
        System.out.println("Usage: " +
                           Daten.applName + " [options]");
        System.out.println("    List of options:");
        printOption("-help","Show this help");
        if (showHelpDev) {
            printOption("-javaRestart", "efa restart by Java instead of Shell");
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

    public int restart() {
        int exitCode;
        if (Daten.javaRestart) {
            exitCode = Daten.HALT_JAVARESTART;
            String restartcmd = System.getProperty("java.home") + Daten.fileSep
                    + "bin" + Daten.fileSep + "java "
                    + (Daten.efa_java_arguments != null ? Daten.efa_java_arguments
                    : "-cp " + System.getProperty("java.class.path")
                    + " " + Daten.EFADIREKT_MAINCLASS + de.nmichael.efa.boathouse.Main.STARTARGS);
            Logger.log(Logger.INFO, Logger.MSG_EVT_EFARESTART,
                    International.getMessage("Neustart mit Kommando: {cmd}", restartcmd));
            try {
                Runtime.getRuntime().exec(restartcmd);
            } catch (Exception ee) {
                Logger.log(Logger.ERROR, Logger.MSG_ERR_EFARESTARTEXEC_FAILED,
                        LogString.cantExecCommand(restartcmd, International.getString("Kommando")));
            }
        } else {
            exitCode = Daten.HALT_SHELLRESTART;
        }
        return exitCode;
    }

    public void exit(int exitCode) {
        if (Daten.efaRunning != null) {
            Daten.efaRunning.closeServer();
            Daten.efaRunning.stopDataLockThread();
        }
        System.exit(exitCode);
    }

}
