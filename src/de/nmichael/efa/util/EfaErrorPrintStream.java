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
import de.nmichael.efa.util.Dialog;
import java.io.*;

// @i18n complete

public class EfaErrorPrintStream extends PrintStream {

  public static boolean ignoreExceptions = false;
  private Object lastErrorObject = null;

  public EfaErrorPrintStream(FileOutputStream f) {
    super(f);
  }

  public void print(Object o) {
    errorPrint(o);
    super.print(o);
  }

  public void println(Object o) {
    errorPrint(o);
    super.println(o);
  }

  private void errorPrint(Object o) {
    if (Daten.verbose) System.out.println(o);
    if (!ignoreExceptions &&
        (o.getClass().toString().indexOf("Exception")>0 ||
         o.getClass().toString().indexOf("java.lang.NoSuchMethodError")>0 ||
         o.getClass().toString().indexOf("java.lang.NoClassDefFoundError")>0) &&
         o.toString().indexOf("java.lang.Exception: Stack trace") != 0) { // für Stack-Inkonsistenzen keine Fehlermeldung auf dem Bildschirm anzeigen

      if (o == lastErrorObject) return;
      lastErrorObject = o;

      String text = International.getString("Unerwarteter Programmfehler")+": "+o.toString();
      String stacktrace = "";

      try {
        StackTraceElement[] stack = null;
        try {
          stack = ((Exception)o).getStackTrace();
        } catch(Exception e1) {
          try {
            stack = ((NoSuchMethodError)o).getStackTrace();
          } catch(Exception e2) {
            try {
              stack = ((NoClassDefFoundError)o).getStackTrace();
            } catch(Exception e3) {
            };
          };
        };
        if (stack != null) {
          text += "\n"+International.getString("Stack Trace")+":\n";
          for (int i=0; stack != null && i<stack.length; i++) stacktrace += stack[i].toString() + "\n";
          text += stacktrace;
        }
      } catch(NoSuchMethodError j13) {
        EfaUtil.foo(); // StackTraceElement erst ab Java 1.4
      }
      
      // if the stack trace concerns classes from efa, ask for bug reports
      // (some other purely java (especially awt/swing) related bugs do not necessarily need to be reported...
      if (stacktrace.indexOf("de.nmichael.efa") >= 0) {
          text += "\n"+International.getString("Bitte melde diesen Fehler an")+": "+Daten.EFAEMAIL;
      } else {
          text += "\n"+International.getString("Dieser Fehler ist möglicherweise ein Fehler in Java, der durch ein Java-Update behoben werden kann.");
      }

      Logger.log(Logger.ERROR,Logger.MSG_ERROR_EXCEPTION,text);
      new ErrorThread(o.toString(),stacktrace).start();
    }
  }

  public void print(String s) {
    if (Daten.verbose) System.out.println(s);
    super.print(s);
  }



  class ErrorThread extends Thread {
    String message;
    String stacktrace;
      ErrorThread(String message, String stacktrace) {
        this.message = message;
        this.stacktrace = stacktrace;
      }
    public void run() {
      Dialog.exceptionError(message,stacktrace);
    }
  }

}