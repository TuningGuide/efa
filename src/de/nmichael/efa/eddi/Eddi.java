package de.nmichael.efa.eddi;

import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;
import javax.swing.UIManager;
import java.awt.*;
import java.io.*;

public class Eddi {
  static boolean ignore  = false;
  boolean packFrame = false;

  //Construct the application
  public Eddi() {
    EddiFrame frame = new EddiFrame();
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
    Daten.ini(Daten.APPL_EDDI);
    Daten.mainDirIni();
    setupLog();

    if (!Daten.dirsIni(true)) {
      // Directory nicht gefunden
      if (!ignore)
        Dialog.error("FEHLER: Eines oder mehrere erforderlichen Verzeichnisse konnten nicht gefunden werden!\n"+
                     "Es wird nicht empfohlen, eddi mit fehlenden Verzeichnissen zu benutzen!\n"+
                     "Um diese Meldung dauerhaft zu unterdr�cken, starte eddi mit der Option '-ignore'.");
    }

    Daten.printEfaInfos();

    Daten.bezeichnungen = new Bezeichnungen(Daten.efaCfgDirectory+Daten.BEZEICHFILE);
    Daten.bezeichnungen.createNewIfDoesntExist();
    Daten.bezeichnungen.readFile();
  }






  // stderr in die Logdatei umleiten
  static void setupLog() {
    Logger.ini("eddi.log",false);
    Logger.log(Logger.INFO,"--- Hier werden Fehlermeldungen von emil ausgegeben. ---");
  }






  // Argumentliste ausgeben (�berfl�ssig, da jetzt standardm��ig Logdatei angelegt wird)
  static void printArgs() {
    System.out.println("eddi "+Daten.EDDI_VERSION+"\n");
    System.out.println("Syntax: java [javaopt] de.nmichael.efa.eddi.Eddi [option]");
    System.out.println("    javaopt - Optionen der Java Virtual Machine (s. 'java -help')");
    System.out.println("    option:");
    System.out.println("      -help     Diese Meldung anzeigen");
    System.out.println("      -verbose  Beim Start Verzeichnisse ausgeben auf stderr");
    System.out.println("      -ignore   Fehler beim Starten ignorieren");
    System.exit(0);
  }



  // Argumente �berpr�fen
  static void checkargs(String[] args) {
    if (args.length == 0) return;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-verbose")) Daten.verbose = true;
      if (args[i].equals("-ignore"))  ignore  = true;
      if (args[i].equals("-help"))    printArgs();
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
    new Eddi();
  }
}
