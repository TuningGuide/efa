/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.elwiz;

import javax.swing.UIManager;
import java.awt.*;
import java.io.*;
import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.DownloadFrame;

// @i18n complete

public class Main {
  boolean packFrame = false;
  static boolean ignore  = false;

  //Construct the application
  public Main() {
    ElwizFrame frame = null;

    try {
      frame = new ElwizFrame();
    } catch (NoClassDefFoundError e) {
      // Plugin-Dialog
      if (!DownloadFrame.getPlugin("elwiz",Daten.PLUGIN_JAXP_NAME,Daten.PLUGIN_JAXP_FILE,Daten.PLUGIN_JAXP_HTML,e.toString(),null,true))
        System.exit(1);
      return;
    }

    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }

    Dialog.initializeScreenSize();

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
  }



  // Grund-Initialisierung der Applikation
  static void efaIni() {
    Daten.ini(Daten.APPL_ELWIZ);
    Daten.mainDirIni();
    setupLog();

    if (!Daten.dirsIni(true)) {
      // Directory nicht gefunden
      if (!ignore)
        Dialog.error(International.getMessage("{program} kann nicht gestartet werden, "+
                "da eines oder mehrere erforderliche Verzeichnisse nicht gefunden werden konnten.",
                "elwiz"));
        System.exit(1);
    }

    Daten.printEfaInfos();
  }






  // stderr in die Logdatei umleiten
  static void setupLog() {
    Logger.ini("elwiz.log",false);
 }






  // Argumentliste ausgeben (überflüssig, da jetzt standardmäßig Logdatei angelegt wird)
  static void printArgs() {
    // @todo internationalisieren ... oder nur auf Englisch?
    System.out.println("elwiz "+Daten.EMIL_VERSION+"\n");
    System.out.println("Syntax: java [javaopt] de.nmichael.efa.elwiz.Elwiz [option]");
    System.out.println("    javaopt - Optionen der Java Virtual Machine (s. 'java -help')");
    System.out.println("    option:");
    System.out.println("      -help     Diese Meldung anzeigen");
    System.out.println("      -verbose  Beim Start Verzeichnisse ausgeben auf stderr");
    System.out.println("      -ignore   Fehler beim Starten ignorieren");
    System.exit(0);
  }



  // Argumente überprüfen
  static void checkargs(String[] args) {
    if (args.length == 0) return;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-verbose")) Daten.verbose = true;
      if (args[i].equals("-ignore"))  ignore  = true;
      if (args[i].equals("-help"))    printArgs();
      if (args[i].equals("-debug")) Logger.debugLogging = true;
    }
  }





  //Main method
  public static void main(String[] args) {
    if (args.length > 0) checkargs(args);

    efaIni();

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    new Main();
  }
}