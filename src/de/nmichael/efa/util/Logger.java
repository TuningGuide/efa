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
  public static final String MSG_ADMIN_LOGIN                 = "ADM001";
  public static final String MSG_ADMIN_LOGINFAILURE          = "ADM002";
  public static final String MSG_ADMIN_ADMINMODEEXITED       = "ADM003";
  public static final String MSG_ADMIN_ACTION_ADMINS         = "ADM004";
  public static final String MSG_ADMIN_ACTION_CONFNEWLOGBOOK = "ADM005";
  public static final String MSG_ADMIN_ACTION_OPENLOGBOOK    = "ADM006";
  public static final String MSG_ADMIN_ACTION_EDITLOGBOOK    = "ADM007";
  public static final String MSG_ADMIN_ACTION_EDITLOGBOOKDONE= "ADM008";
  public static final String MSG_ADMIN_ACTION_EDITBOATSTATUS = "ADM009";
  public static final String MSG_ADMIN_ACTION_VIEWMESSAGES   = "ADM010";
  public static final String MSG_ADMIN_ACTION_VIEWLOGFILE    = "ADM011";
  public static final String MSG_ADMIN_ACTION_EDITCONFIG     = "ADM012";
  public static final String MSG_ADMIN_ACTION_STATISTICS     = "ADM013";
  public static final String MSG_ADMIN_ACTION_EDITBOATLIST   = "ADM014";
  public static final String MSG_ADMIN_ACTION_EDITMEMBERLIST = "ADM015";
  public static final String MSG_ADMIN_ACTION_EDITDESTLIST   = "ADM016";
  public static final String MSG_ADMIN_ACTION_EDITGROUPS     = "ADM017";
  public static final String MSG_ADMIN_ACTION_FULLACCESS     = "ADM018";
  public static final String MSG_ADMIN_ACTION_LOCKEFA        = "ADM019";
  public static final String MSG_ADMIN_ACTION_EXECCMD        = "ADM019";
  public static final String MSG_ADMIN_ACTION_EXECCMDFAILED  = "ADM019";
  public static final String MSG_ADMIN_LOGBOOKENTRYDELETED   = "ADM020";
  public static final String MSG_ADMIN_ACTION_ADMINSMODIFIED = "ADM021";
  public static final String MSG_ADMIN_ACTION_ADMINCREATED   = "ADM022";
  public static final String MSG_ADMIN_ACTION_ADMINRENAMED   = "ADM023";
  public static final String MSG_ADMIN_ACTION_ADMINCHANGED   = "ADM024";
  public static final String MSG_ADMIN_ACTION_ADMINDELETED   = "ADM025";
  public static final String MSG_ADMIN_BOATSTATECHANGED      = "ADM026";
  public static final String MSG_ADMIN_ALLBOATSTATECHANGED   = "ADM027";
  public static final String MSG_ADMIN_NOBOATSTATECHANGED    = "ADM028";


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
  public static final String MSG_BHEVENTS_EFAEXIT            = "EVT002";
  public static final String MSG_BHEVENTS_EFAEXITABORTED     = "EVT003";
  public static final String MSG_BHEVENTS_EFAEXITEXECCMD     = "EVT004";
  public static final String MSG_BHEVENTS_EFARESTART         = "EVT005";
  public static final String MSG_BHEVENTS_SUPERADMINCREATED  = "EVT005";
  public static final String MSG_BHEVENTS_EFASECURE          = "EVT006";
  public static final String MSG_BHEVENTS_NEWLOGBOOKOPENED   = "EVT007";
  public static final String MSG_BHEVENTS_EFAREADY           = "EVT008";
  public static final String MSG_BHEVENTS_LOGBOOKOPENED      = "EVT009";

  // efa in the Boat House - Errors
  public static final String MSG_BHERR_GENERIC               = "ERR001";
  public static final String MSG_BHERR_PANIC                 = "ERR002";
  public static final String MSG_BHERR_EFARUNNING_FAILED     = "ERR003";
  public static final String MSG_BHERR_SENDMAILFAILED_PLUGIN = "ERR004";
  public static final String MSG_BHERR_SENDMAILFAILED_CFG    = "ERR005";
  public static final String MSG_BHERR_SENDMAILFAILED_ERROR  = "ERR006";
  public static final String MSG_BHERR_EFAEXITEXECCMD_FAILED = "ERR007";
  public static final String MSG_BHERR_EFARESTARTEXEC_FAILED = "ERR008";
  public static final String MSG_BHERR_EXITLOWMEMORY         = "ERR009";
  public static final String MSG_BHERR_EXITONERROR           = "ERR010";
  public static final String MSG_BHERR_NOSUPERADMIN          = "ERR011";
  public static final String MSG_BHERR_FILEOPENFAILED        = "ERR012";
  public static final String MSG_BHERR_FILECREATEFAILED      = "ERR013";

  // efa in the Boat House - Warnings
  public static final String MSG_BHWARN_EFARUNNING_FAILED    = "WRN001";
  public static final String MSG_BHWARN_JAVA_VERSION         = "WRN002";
  public static final String MSG_BHWARN_EFAUNSECURE          = "WRN003";
  public static final String MSG_BHWARN_FILENEWCREATED       = "WRN003";

  // Debug Logging
  public static final String MSG_DEBUG_GENERIC               = "DBG001";
  public static final String MSG_DEBUG_EFAWETT               = "DBG002";
  public static final String MSG_DEBUG_STATISTICS            = "DBG003";
  public static final String MSG_DEBUG_EFARUNNING            = "DBG004";


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