/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
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
        System.out.println(Daten.EFA_SHORTNAME + " " + Daten.VERSION + "\n");
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
        printOption("-debug",International.getString("Debug-Logging aktivieren"));
    }

    public void checkArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-help")) {
                printUsage(null);
                args[i] = null;
                continue;
            }
            if (args[i].equals("-wws")) {
                Daten.watchWindowStack = true;
                args[i] = null;
                continue;
            }
            if (args[i].equals("-debug")) {
                Logger.debugLogging = true;
                args[i] = null;
                continue;
            }
            if (args[i].equals("-exc")) {
                Daten.exceptionTest = true;
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
