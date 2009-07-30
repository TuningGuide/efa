package de.nmichael.efa;

import java.io.*;
import java.util.*;
import de.nmichael.efa.direkt.NachrichtenAnAdmin;
import de.nmichael.efa.direkt.Nachricht;

public class Logger {

  public static final String ERROR = "ERROR";
  public static final String INFO = "INFO";
  public static final String WARNING = "WARNING";
  public static final String ACTION = "ACTION";
  public static final String DEBUG = "DEBUG";

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
      Logger.log(Logger.ERROR,"Fehler: Logdatei '"+Daten.efaLogfile+"' konnte nicht erstellt werden!");
    }

    if (debugLogging) log(INFO,"Debug Logging activated!");
  }

  public static void log(String typ, String s) {
    if (typ.equals(DEBUG) && !debugLogging) return;
    Calendar cal = new GregorianCalendar();
    String t = "["+EfaUtil.getCurrentTimeStamp()+"] - "+typ+" - "+s;
    EfaErrorPrintStream.ignoreExceptions = true; // Damit Exception-Ausschriften nicht versehentlich als echte Exceptions gemeldet werden
    System.err.println(EfaUtil.replace(t,"\n"," ",true));
    EfaErrorPrintStream.ignoreExceptions = false;

    if (typ != null && typ.equals(ERROR) && nachrichten != null) {
      if (Daten.efaConfig == null || Daten.efaConfig.efaDirekt_bnrError_admin) {
        mailError(t, Nachricht.ADMIN);
      }
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_bnrError_bootswart) {
        mailError(t, Nachricht.BOOTSWART);
      }
    }
  }

  private static void mailError(String t, int to) {
    String txt = "Dies ist eine automatisch erstellte Fehlermeldung von efa.\nFolgender Fehler ist aufgetreten:\n"+t;
    if (t.indexOf("Unerwarteter Programmfehler")>=0) {
      txt += "\n\nProgramm-Information:\n============================================\n";
      Vector info = Daten.getEfaInfos();
      for (int i=0; info != null && i<info.size(); i++) txt += (String)info.get(i)+"\n";
    }
    nachrichten.createNachricht("efa",to,"ERROR",txt);
  }

  public static void setNachrichtenAnAdmin(NachrichtenAnAdmin _nachrichten) {
    nachrichten = _nachrichten;
  }

  public static boolean isWarningLine(String s) {
    return (s != null && s.indexOf("WARNING") == 24);
  }

  public static long getLineTimestamp(String s) {
    if (s == null || s.length() < 21) return 0;
    TMJ datum = EfaUtil.string2date(s.substring(1,11),1,1,1980);
    TMJ zeit = EfaUtil.string2date(s.substring(12,20),0,0,0);
    return EfaUtil.dateTime2Cal(datum,zeit).getTimeInMillis();
  }

}