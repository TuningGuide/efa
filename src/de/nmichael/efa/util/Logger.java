/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.EfaErrorPrintStream;
import java.io.*;
import java.util.*;
import de.nmichael.efa.direkt.NachrichtenAnAdmin;
import de.nmichael.efa.direkt.Nachricht;

// @i18n complete

public class Logger {

  // Message Types
  public static final String ERROR   = "ERROR  ";
  public static final String INFO    = "INFO   ";
  public static final String WARNING = "WARNING";
  public static final String ACTION  = "ACTION ";
  public static final String DEBUG   = "DEBUG  ";

  // Message Keys
  public static final String MSG_GENERIC                     = "GEN001";

  // Core Functionality Informations (multiple source files)
  public static final String MSG_INFO_CONFIGURATION          = "INF001";

  // Core Functionality Errors and Warnings (multiple source files)
  public static final String MSG_CORE_SETUPDIRS              = "COR001";
  public static final String MSG_CORE_INFOFAILED             = "COR002";
  public static final String MSG_CORE_USERHOME               = "COR003";

  // Activities performed in Admin Mode
  public static final String MSG_ADMIN_LOGBOOKENTRYDELETED   = "ADM001";

  // Data Administration (not only Admin Mode)
  public static final String MSG_DATA_NEWMEMBERADDED         = "DAT001";

  // de.nmichael.efa.Logger
  public static final String MSG_LOGGER_FAILEDCREATELOG      = "LOG001";
  public static final String MSG_LOGGER_DEBUGACTIVATED       = "LOG002";

  // de.nmichael.efa.EfaErrorPrintStream
  public static final String MSG_ERROR_EXCEPTION             = "EXC001";

  // de.nmichael.efa.International
  public static final String MSG_INTERNATIONAL_DEBUG         = "INT001";
  public static final String MSG_INTERNATIONAL_FAILEDSETUP   = "INT002";
  public static final String MSG_INTERNATIONAL_MISSINGKEY    = "INT003";
  public static final String MSG_INTERNATIONAL_INCORRECTKEY  = "INT004";

  // de.nmichael.efa.core.DatenListe (and subclasses)
  public static final String MSG_CSVFILE_FILECONVERTED       = "CSV001";
  public static final String MSG_CSVFILE_ERRORCONVERTING     = "CSV002";
  public static final String MSG_CSVFILE_ERRORINVALIDFORMAT  = "CSV003";
  public static final String MSG_CSVFILE_ERRORREADINGFILE    = "CSV004";
  public static final String MSG_CSVFILE_ERRORWRITEFILE      = "CSV005";
  public static final String MSG_CSVFILE_ERRORCREATEFILE     = "CSV006";
  public static final String MSG_CSVFILE_ERRORCLOSINGFILE    = "CSV007";
  public static final String MSG_CSVFILE_INCONSISTENTDATA    = "CSV008";
  public static final String MSG_CSVFILE_CHECKSUMERROR       = "CSV009";
  public static final String MSG_CSVFILE_CHECKSUMCORRECTED   = "CSV010";
  public static final String MSG_CSVFILE_CHECKSUMNOTCORRECTED= "CSV011";
  public static final String MSG_CSVFILE_FILEISBACKUP        = "CSV012";
  public static final String MSG_CSVFILE_FILENEWCREATED      = "CSV013";
  public static final String MSG_CSVFILE_BACKUPERROR         = "CSV014";
  public static final String MSG_CSVFILE_OOMSAVEERROR        = "CSV015";

  // efa in the Boat House - Events (multiple source files)
  public static final String MSG_BHEVENTS_UNLOCKED           = "EVT001";

  // efa in the Boat House - Fehler
  public static final String MSG_BHERROR_PANIC               = "ERR001";

  // Debug Logging
  public static final String MSG_DEBUG_GENERIC               = "DBG001";
  public static final String MSG_DEBUG_EFAWETT               = "DBG002";
  public static final String MSG_DEBUG_STATISTICS            = "DBG003";


  public static boolean debugLogging = false;

  private static NachrichtenAnAdmin nachrichten = null;

  public static String getLogfileName(String logfile) {
    return Daten.efaConfigUserHome.efaUserDirectory+logfile;
  }

  public static void ini(String logfile, boolean append) {
    try {
      Daten.efaLogfile = getLogfileName(logfile);
      System.setErr(new EfaErrorPrintStream(new FileOutputStream(Daten.efaLogfile,append)));
    } catch (FileNotFoundException e) {
      Logger.log(Logger.ERROR,
              Logger.MSG_LOGGER_FAILEDCREATELOG,
              International.getString("Fehler") + ": " + International.getMessage("Logdatei '{logfile}' konnte nicht erstellt werden!",Daten.efaLogfile));
    }

    if (debugLogging) log(Logger.INFO,
            Logger.MSG_LOGGER_DEBUGACTIVATED,
            "Debug Logging activated."); // do not internationalize!
  }

  /**
   * Log a message.
   * Use this method for loggin!
   * @param type the type of the message, see Logger: Message Types
   * @param key the key for this message, see Logger: Message Keys
   * @param msg the message to be logged
   */
  public static void log(String type, String key, String msg) {
    if (type != null && type.equals(DEBUG) && !debugLogging) return;

    Calendar cal = new GregorianCalendar();
    String t = "[" + EfaUtil.getCurrentTimeStamp() + "] - " + type + " - " + key +  " - " + msg;
    EfaErrorPrintStream.ignoreExceptions = true; // Damit Exception-Ausschriften nicht versehentlich als echte Exceptions gemeldet werden
    System.err.println(EfaUtil.replace(t,"\n"," ",true));
    EfaErrorPrintStream.ignoreExceptions = false;

    if (type != null && type.equals(ERROR) && nachrichten != null) {
      if (Daten.efaConfig == null || Daten.efaConfig.efaDirekt_bnrError_admin) {
        mailError(key, t, Nachricht.ADMIN);
      }
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_bnrError_bootswart) {
        mailError(key, t, Nachricht.BOOTSWART);
      }
    }
  }

  /**
   * Log a message with the key "GENERIC".
   * @deprecated use log(String, String, String) instead!
   * @param type the type of the message, see Logger: Message Types
   * @param msg the message to be logged
   */
  public static void log(String type, String msg) {
      log(type, Logger.MSG_GENERIC, msg);
  }

  private static void mailError(String key, String msg, int to) {
    String txt = International.getString("Dies ist eine automatisch erstellte Fehlermeldung von efa.\nFolgender Fehler ist aufgetreten:\n")+msg;
    if (key != null && key.equals(Logger.MSG_ERROR_EXCEPTION)) {
      txt += "\n\n" + International.getString("Programm-Information") + ":\n============================================\n";
      Vector info = Daten.getEfaInfos();
      for (int i=0; info != null && i<info.size(); i++) txt += (String)info.get(i)+"\n";
    }
    nachrichten.createNachricht(Daten.EFA_SHORTNAME,to,International.getString("FEHLER"),txt);
  }

  public static void setNachrichtenAnAdmin(NachrichtenAnAdmin _nachrichten) {
    nachrichten = _nachrichten;
  }

  public static boolean isWarningLine(String s) {
    return (s != null && s.indexOf(Logger.WARNING) == 24);
  }

  public static long getLineTimestamp(String s) {
    if (s == null || s.length() < 21) return 0;
    TMJ datum = EfaUtil.string2date(s.substring(1,11),1,1,1980);
    TMJ zeit = EfaUtil.string2date(s.substring(12,20),0,0,0);
    return EfaUtil.dateTime2Cal(datum,zeit).getTimeInMillis();
  }

}