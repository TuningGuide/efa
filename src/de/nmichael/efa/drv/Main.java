/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.drv;

import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import javax.swing.UIManager;
import java.awt.*;
import java.io.*;
import de.nmichael.efa.*;

public class Main {
  boolean packFrame = true;

  //Construct the application
  public Main() {
    StartLogo logo = new StartLogo("/de/nmichael/efa/img/efaIntro.gif");
    logo.show();

    Dialog.initializeScreenSize();

    // efa-Config
    DRVConfig drvConfig = new DRVConfig(Daten.efaCfgDirectory+Daten.DRVCONFIGFILE);
    if (!EfaUtil.canOpenFile(Daten.efaCfgDirectory+Daten.DRVCONFIGFILE)) {
      drvConfig.writeFile();
    }
    drvConfig.readFile();

    // Daten Directory
    if (drvConfig.datenDirectory != null && drvConfig.datenDirectory.length() > 0 && (new File(drvConfig.datenDirectory)).isDirectory()) {
      Daten.efaDataDirectory = drvConfig.datenDirectory;
      if (!Daten.efaDataDirectory.endsWith(Daten.fileSep)) Daten.efaDataDirectory += Daten.fileSep;
    }

    // WettDefs.cfg
    Daten.wettDefs = new WettDefs(Daten.efaCfgDirectory+Daten.WETTDEFS);
    Daten.wettDefs.createNewIfDoesntExist();
    Daten.wettDefs.readFile();

    EfaDRVFrame frame = new EfaDRVFrame(drvConfig);

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


  // Argumentliste ausgeben (überflüssig, da jetzt standardmäßig Logdatei angelegt wird)
  static void printArgs() {
    System.out.println("efaDRV "+Daten.VERSION+"\n");
    System.out.println("Syntax: java [javaopt] de.nmichael.efa.drv.EfaDRV [option]");
    System.out.println("    javaopt - Optionen der Java Virtual Machine (s. 'java -help')");
    System.out.println("    option:");
    System.out.println("      -help     Diese Meldung anzeigen");
    System.out.println("      -verbose  Diverse Meldungen ausgeben");
    System.out.println("      -ws       Window-Stack überwachen");
    System.exit(0);
  }



  // Argumente überprüfen
  static void checkargs(String[] args) {
    if (args.length == 0) return;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-verbose")) Daten.verbose = true;
      if (args[i].equals("-ws"))      Daten.watchWindowStack = true;
      if (args[i].equals("-help"))    printArgs();
      if (args[i].equals("-debug")) Logger.debugLogging = true;
    }
  }

  // Grund-Initialisierung der Applikation
  static void efaIni() {
    Daten.ini(Daten.APPL_DRV);
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
      Daten.efaLogfile = Logger.getLogfileName("efaDRV.log");
      // Wenn Logdatei zu groß ist, die alte Logdatei verschieben
      File log = new File(Daten.efaLogfile);
      if (log.exists() && log.length()>1048576) {
        baklog = EfaUtil.moveAndEmptyFile(Daten.efaLogfile,Daten.efaConfigUserHome.efaUserDirectory+"backup"+Daten.fileSep);
      }
    } catch (Exception e) {
      Logger.log(Logger.ERROR,"Logdatei '"+Daten.efaLogfile+"' konnte nicht archiviert werden!");
    }

    Logger.ini("efaDRV.log",true);

    Logger.log(Logger.INFO,"PROGRAMMSTART");
    Logger.log(Logger.INFO,"Version "+Daten.VERSIONID);

    if (baklog != null) {
      Logger.log(Logger.INFO,"Alte Logdatei wurde nach '"+baklog+"' verschoben.");
    }
  }



  //Main method
  public static void main(String[] args) {
    if (args.length > 0) checkargs(args);

    efaIni();

    new Main();
  }
}