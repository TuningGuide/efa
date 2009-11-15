/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import javax.swing.UIManager;
import java.awt.*;
import de.nmichael.efa.*;
import java.io.*;
import java.util.*;

public class Main {
  public static String STARTARGS = "";
  boolean packFrame = false;
  static boolean ignore  = false;


  //Construct the application
  public Main() {
    StartLogo logo = new StartLogo("/de/nmichael/efa/img/efaIntro.gif");
    logo.show();

    try {
      Thread.sleep(1000); // Damit nach automatischem Restart genügend Zeit vergeht
    } catch(InterruptedException e) {
    }

    // Stop on Checksum Errors
    Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_REQUIRE_ADMIN;
    Daten.actionOnChecksumSaveError = Daten.CHECKSUM_SAVE_HALT_PROGRAM;
    // Admin erforderlich, um nicht vorhandene Datenliste neu zu erstellen
    Daten.actionOnDatenlisteNotFound = Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
    // Admin erforderlich, wenn Backupdatei geladen werden soll
    Daten.actionOnDatenlisteIsBackup = Daten.BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;

    Dialog.initializeScreenSize();

    // efa-Config
    Daten.efaConfig = new EfaConfig(Daten.efaCfgDirectory+Daten.CONFIGFILE);
    if (!EfaUtil.canOpenFile(Daten.efaConfig.getFileName())) {
      if (!Daten.efaConfig.writeFile()) {
        EfaDirektFrame.haltProgram(Daten.efaConfig.getFileName()+" kann nicht erstellt werden!");
      }
      Logger.log(Logger.INFO,"Konfigurationsdatei neu erstellt.");
    };
    if (!Daten.efaConfig.readFile()) {
      EfaDirektFrame.haltProgram(Daten.efaConfig.getFileName()+" kann nicht gelesen werden!");
    }

    // Look&Feel
    try {
      if (Daten.efaConfig.lookAndFeel.equals(EfaConfig.LOOKANDFEEL_STANDARD)) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      else UIManager.setLookAndFeel(Daten.efaConfig.lookAndFeel);
    } catch(Exception e) {
      Logger.log(Logger.WARNING,"Konnte Look&Feel nicht setzen: "+e.toString());
    }

    // Schriftgröße
    try {
      if (Daten.efaConfig.efaDirekt_fontSize != 0 || Daten.efaConfig.efaDirekt_fontStyle != -1)
        Dialog.setGlobalFontSize(Daten.efaConfig.efaDirekt_fontSize,Daten.efaConfig.efaDirekt_fontStyle);
    } catch(Exception e) {
      Logger.log(Logger.WARNING,"Schriftgröße konnte nicht geändert werden: "+e.toString());
    }

    EfaDirektFrame frame = new EfaDirektFrame();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension frameSize = frame.getSize();
    if (frameSize.height > Dialog.screenSize.height) {
      frameSize.height = Dialog.screenSize.height;
    }
    if (frameSize.width > Dialog.screenSize.width) {
      frameSize.width = Dialog.screenSize.width;
    }
    Dialog.setDlgLocation(frame);
    frame.setVisible(true);

    logo.remove();
  }



  // Grund-Initialisierung der Applikation
  static void efaIni() {
    Daten.ini(Daten.APPL_EFADIREKT);
    Daten.mainDirIni();
    setupLog();

    if (!Daten.dirsIni(false)) {
      // nothing to do
    }
  }






  // stderr in die Logdatei umleiten
  static void setupLog() {
    String baklog = null;

    try {
      Daten.efaLogfile = Logger.getLogfileName("efadirekt.log");
      // Wenn Logdatei zu groß ist, die alte Logdatei verschieben
      File log = new File(Daten.efaLogfile);
      if (log.exists() && log.length()>1048576) {
        baklog = EfaUtil.moveAndEmptyFile(Daten.efaLogfile,Daten.efaConfigUserHome.efaUserDirectory+"backup"+Daten.fileSep);
      }
    } catch (Exception e) {
      Logger.log(Logger.ERROR,"Logdatei '"+Daten.efaLogfile+"' konnte nicht archiviert werden!");
    }

    Logger.ini("efadirekt.log",true);

    Logger.log(Logger.INFO,"PROGRAMMSTART");
    Logger.log(Logger.INFO,"efa-Version: "+Daten.VERSIONID+" -- Java-Version: "+Daten.javaVersion+" (JVM "+Daten.jvmVersion+") -- OS-Version: "+Daten.osName+" "+Daten.osVersion);
    try {
      Daten.efa_java_arguments = System.getenv(Daten.EFA_JAVA_ARGUMENTS);
      Logger.log(Logger.DEBUG,Daten.EFA_JAVA_ARGUMENTS + "=" + Daten.efa_java_arguments);
    } catch(Error e) {
      Logger.log(Logger.WARNING, "Abfragen der Environment-Variable "+Daten.EFA_JAVA_ARGUMENTS+" nicht möglich: "+e.toString());
    }

    if (baklog != null) {
      Logger.log(Logger.INFO,"Alte Logdatei wurde nach '"+baklog+"' verschoben.");
    }
  }






  // Argumentliste ausgeben (überflüssig, da jetzt standardmäßig Logdatei angelegt wird)
  static void printArgs() {
    System.out.println("efaDirekt "+Daten.VERSION+"\n");
    System.out.println("Syntax: java [javaopt] de.nmichael.efa.direkt.EfaDirekt [option]");
    System.out.println("    javaopt - Optionen der Java Virtual Machine (s. 'java -help')");
    System.out.println("    option:");
    System.out.println("      -help            Diese Meldung anzeigen");
    System.out.println("      -verbose         Diverse Meldungen ausgeben");
    System.out.println("      -ignore          Fehler beim Starten ignorieren");
    System.out.println("      -ws              Window-Stack überwachen");
    System.out.println("      -javaRestart     Neustart von efa durch Java (anstelle Shell)");
    System.exit(0);
  }





  // Argumente überprüfen
  static void checkargs(String[] args) {
    if (args.length == 0) return;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-verbose")) Daten.verbose = true;
      if (args[i].equals("-ignore"))  ignore = true;
      if (args[i].equals("-ws"))      Daten.watchWindowStack = true;
      if (args[i].equals("-javaRestart")) Daten.javaRestart = true;
      if (args[i].equals("-debug")) Logger.debugLogging = true;
      if (args[i].equals("-help"))    printArgs();
    }
  }






  //Main method
  public static void main(String[] args) {
/*
    Properties prop = System.getProperties();
    Object[] keys = prop.keySet().toArray();
    for (int i=0; i<keys.length; i++) System.out.println(keys[i] + ": " + prop.getProperty((String)keys[i]));
*/
/*
System.setProperty("os.name","Windows XP");
System.setProperty("os.arch","x86");
System.setProperty("os.version","5.1");
*/
    for (int i=0; i<args.length; i++) {
      STARTARGS += " "+args[i];
    }
    if (args.length > 0) checkargs(args);

    efaIni();

    new Main();
  }
}