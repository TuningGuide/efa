package de.nmichael.efa;

/**
 * <p>Title: efa - Elektronisches Fahrtenbuch</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Nicolas Michael
 * @version 1.0
 */

import java.io.*;

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

      String text = "Unerwarteter Programmfehler: "+o.toString();
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
          text += "\nStack Trace:\n";
          for (int i=0; stack != null && i<stack.length; i++) stacktrace += stack[i].toString() + "\n";
          text += stacktrace;
        }
      } catch(NoSuchMethodError j13) {
        EfaUtil.foo(); // StackTraceElement erst ab Java 1.4
      }
      text += "\nBitte melde diesen Fehler an "+Daten.EFAEMAIL+"!";

      Logger.log(Logger.ERROR,text);
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