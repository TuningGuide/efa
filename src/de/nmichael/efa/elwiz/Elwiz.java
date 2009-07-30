package de.nmichael.efa.elwiz;

import javax.swing.UIManager;
import java.awt.*;
import java.io.*;
import de.nmichael.efa.Daten;
import de.nmichael.efa.Dialog;
import de.nmichael.efa.EfaErrorPrintStream;
import de.nmichael.efa.DownloadFrame;
import de.nmichael.efa.Logger;

public class Elwiz {
  boolean packFrame = false;
  static boolean ignore  = false;

  //Construct the application
  public Elwiz() {
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
        Dialog.error("FEHLER: Eines oder mehrere erforderlichen Verzeichnisse konnten nicht gefunden werden!\n"+
                     "Es wird nicht empfohlen, elwiz mit fehlenden Verzeichnissen zu benutzen!\n"+
                     "Um diese Meldung dauerhaft zu unterdrücken, starte elwiz mit der Option '-ignore'.");
    }

    Daten.printEfaInfos();
  }






  // stderr in die Logdatei umleiten
  static void setupLog() {
    Logger.ini("elwiz.log",false);
    Logger.log(Logger.INFO,"--- Hier werden Fehlermeldungen von elwiz ausgegeben. ---");
 }






  // Argumentliste ausgeben (überflüssig, da jetzt standardmäßig Logdatei angelegt wird)
  static void printArgs() {
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

    new Elwiz();
  }
}