package de.nmichael.efa;

import de.nmichael.efa.statistics.Statistik;
import javax.swing.UIManager;
import java.awt.*;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  efa - Hauptprogramm
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class Efa {
  boolean packFrame = true;
  static boolean ignore  = false;
  static boolean cmdmode = false;

  static String fb = null;   // Fahrtenbuch zum Starten
  static String stat = null; // Statistik, die berechnet werden soll
  static String importFb = null;
  static String lfdNrAdd = null;
  static String lfdNrAddChar = null;
  static String lfdNrVon = null;
  static String lfdNrBis = null;
  static String datumVon = null;
  static String datumBis = null;
  static String gruppe = null;
  static boolean importDatenlisten = false;
  static String beiDoppelterLfdNr = null;




  /**Construct the application*/
  public Efa() {
    StartLogo logo = new StartLogo("/de/nmichael/efa/img/efaIntro.gif");
    logo.show();


    EfaSec efaSec = new EfaSec(Daten.efaProgramDirectory+Daten.EFA_SECFILE);
    if (!efaSec.secFileExists()) { // efa Secure Mode
      // Stop on Checksum Errors

      // Eigentlich darf efa bei fehlerhafter Konfigurationsdatei überhaupt nicht mehr Starten, denn ein
      // Angreifer könnte ja das Paßwort der admins ausgetauscht haben. Wenn efa dann beim Start die
      // modifizierte Konfigurationsdatei einliest, wird das Abnicken der geänderten Datei durch das
      // geänderte Paßwort möglich!
      // Gleiches gilt für efa im Bootshaus.
      // Um sicher zu sein, muß efa jegliche Änderungen an der Konfigurationsdatei künftig verbieten.
      //      Daten.actionOnChecksumError = Daten.CHECKSUM_HALT_PROGRAM;
      Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_REQUIRE_ADMIN;

      // Admin erforderlich, um nicht vorhandene Datenliste neu zu erstellen
      Daten.actionOnDatenlisteNotFound = Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
      // Admin erforderlich, wenn Backupdatei geladen werden soll
      Daten.actionOnDatenlisteIsBackup = Daten.BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
    }

    Dialog.initializeScreenSize();

    // efa-Config
    Daten.efaConfig = new EfaConfig(Daten.efaCfgDirectory+Daten.CONFIGFILE);
    boolean openWelcome = false;
    if (!EfaUtil.canOpenFile(Daten.efaCfgDirectory+Daten.CONFIGFILE)) {
      Daten.efaConfig.writeFile();
      openWelcome = true;
    }
    Daten.efaConfig.readFile();

    if (!efaSec.secFileExists()) { // efa Secure Mode: Jetzt, da Config gelesen wurde: Nur noch require Admin
      // Admit on Checksum Errors
      Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_REQUIRE_ADMIN;
    }

    // Look&Feel
    try {
      if (Daten.efaConfig.lookAndFeel.equals(EfaConfig.LOOKANDFEEL_STANDARD)) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      else UIManager.setLookAndFeel(Daten.efaConfig.lookAndFeel);
    } catch(Exception e) {
      Logger.log(Logger.WARNING,e.toString());
    }

    EfaFrame frame = new EfaFrame(fb,openWelcome);

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

    frame.checkStartTour();
  }






  // Grund-Initialisierung der Applikation
  static void efaIni() {
    Daten.ini(Daten.APPL_EFA);
    Daten.mainDirIni();
    setupLog();

    if (!Daten.dirsIni(true)) {
      // Directory nicht gefunden
      if (!ignore)
        Dialog.error("FEHLER: Eines oder mehrere erforderlichen Verzeichnisse konnten nicht gefunden werden.\n"+
                     "Es wird nicht empfohlen, efa mit fehlenden Verzeichnissen zu benutzen!\n"+
                     "Um diese Meldung dauerhaft zu unterdrücken, starte efa mit der Option '-ignore'.");
    }

    Daten.printEfaInfos();
  }






  // stderr in die Logdatei umleiten
  static void setupLog() {
    Logger.ini("efa.log",false);
    Logger.log(Logger.INFO,"--- Hier werden Status- und Fehlermeldungen von efa ausgegeben. ---");
  }






  // Argumentliste ausgeben (überflüssig, da jetzt standardmäßig Logdatei angelegt wird)
  static void printArgs() {
    System.out.println("efa "+Daten.VERSION+"\n");
    System.out.println("Syntax: java [javaopt] de.nmichael.efa.Efa [option]");
    System.out.println("    javaopt - Optionen der Java Virtual Machine (s. 'java -help')");
    System.out.println("    option:");
    System.out.println("      -help     Diese Meldung anzeigen");
    System.out.println("      -verbose  Diverse Meldungen ausgeben");
    System.out.println("      -ignore   Fehler beim Starten ignorieren");
    System.out.println("      -ws       Window-Stack überwachen");
    System.out.println("      -fb <datei> Beim Starten das Fahrtenbuch <datei> öffnen");
    System.out.println("      [-fb <datei>] -stat <statistik>\n            Fahrtenbuch <datei> öffnen und Statistik <statistik> erstellen");
    System.out.println("      [-fb <datei>] -import <datei2> [optionen]    Fahrtenbuch <datei> öffnen und Fahrtenbuch <datei2> importieren\n"+
                       "          <datei> kann sein:\n"+
                       "           efa         zuletzt von efa geöffnetete Datei\n"+
                       "           efadirekt   zuletzt von efadirekt (efa im Bootshaus) geöffnete Datei\n"+
                       "          [optionen] können sein:\n"+
                       "           -lfdNrAdd <n>\n"+
                       "           -lfdNrAddChar <c>\n"+
                       "           -lfdNrVon <n>\n"+
                       "           -lfdNrBis <n>\n"+
                       "           -datumVon <TT.MM.JJJJ>\n"+
                       "           -datumBis <TT.MM.JJJJ>\n"+
                       "           -gruppe <gruppenname>\n"+
                       "           -importDatenlisten\n"+
                       "           -beiDoppelterLfdNr [buchstAnhaengen|amEndeEinfuegen|nichtImportieren]\n"+
                       "                        Fahrtenbuch <datei> öffnen und Fahrtenbuch <datei2> in <datei> importieren.\n"+
                       "                        Optionen wie beim Import von Fahrtenbüchern in der Online-Hilfe beschrieben.");
    System.exit(0);
  }





  // Argumente überprüfen
  static void checkargs(String[] args) {
    if (args.length == 0) return;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-verbose")) Daten.verbose = true;
      if (args[i].equals("-ignore"))  ignore = true;
      if (args[i].equals("-ws"))      Daten.watchWindowStack = true;
      if (args[i].equals("-help"))    printArgs();
      if (args[i].equals("-fb") && i+1<args.length) fb = args[++i];
      if (args[i].equals("-stat") && i+1<args.length) stat = args[++i];
      if (args[i].equals("-debug")) Logger.debugLogging = true;
      if (args[i].equals("-exception")) Daten.exceptionTest = true;
      if (args[i].equals("-import"))  importFb = args[++i];
      if (args[i].equals("-lfdNrAdd")) lfdNrAdd = args[++i];
      if (args[i].equals("-lfdNrAddChar")) lfdNrAddChar = args[++i];
      if (args[i].equals("-lfdNrVon")) lfdNrVon = args[++i];
      if (args[i].equals("-lfdNrBis")) lfdNrBis = args[++i];
      if (args[i].equals("-datumVon")) datumVon = args[++i];
      if (args[i].equals("-datumBis")) datumBis = args[++i];
      if (args[i].equals("-gruppe")) gruppe = args[++i];
      if (args[i].equals("-importDatenlisten")) importDatenlisten = true;
      if (args[i].equals("-beiDoppelterLfdNr")) beiDoppelterLfdNr = args[++i];
    }
  }


  // Aufgrund von Aufruf-Parametern eine Statistik erstellen
  static void createStat() {
    cmdmode = true;
    Daten.bezeichnungen = new Bezeichnungen(Daten.efaCfgDirectory+Daten.BEZEICHFILE);
    if (!Daten.bezeichnungen.readFile()) {
      System.out.println("Bezeichnungen-Datei '"+Daten.bezeichnungen.getFileName()+"' kann nicht gelesen werden!");
      System.exit(2);
    }
    Daten.wettDefs = new WettDefs(Daten.efaCfgDirectory+Daten.WETTDEFS);
    if (!Daten.wettDefs.readFile()) {
      System.out.println("Wettbewerbsdefinitions-Datei '"+Daten.wettDefs.getFileName()+"' kann nicht gelesen werden!");
      System.exit(2);
    }
    Daten.fahrtenbuch = new Fahrtenbuch(fb);
    if (!Daten.fahrtenbuch.readFile()) {
      System.out.println("Fahrtenbuch '"+fb+"' kann nicht gelesen werden!");
      System.exit(2);
    }
    Daten.fahrtenbuch.getDaten().statistik.getFirst(stat);
    DatenFelder f = (DatenFelder)Daten.fahrtenbuch.getDaten().statistik.getComplete();
    StatistikDaten[] sd = new StatistikDaten[1];
    if (f != null) {
      try {
        sd[0] = StatistikFrame.getSavedValues(f);
        StatistikFrame.allgStatistikDaten(sd[0]);
        System.out.print("Erstelle '"+stat+"'...");
        Statistik.create(sd);
        System.out.println("fertig.");
        System.exit(0);
      } catch(StringIndexOutOfBoundsException e) {
        System.out.println("Fehler beim Lesen der gespeicherten Konfiguration!");
      }
    } else {
      System.out.println("Statistik '"+stat+"' nicht gefunden!");
      System.exit(3);
    }
  }

  static void doImportFb() {
    cmdmode = true;
    if (importFb == null) { System.out.println("Kein zu importierendes Fahrtenbuch angegeben."); printArgs(); }
    if (lfdNrAdd == null) { lfdNrAdd = ""; }
    if (lfdNrAddChar == null) { lfdNrAddChar = ""; }
    if (lfdNrVon == null) { lfdNrVon = ""; }
    if (lfdNrBis == null) { lfdNrBis = ""; }
    if (datumVon == null) { datumVon = ""; }
    if (datumBis == null) { datumBis = ""; }
    if (beiDoppelterLfdNr == null) { beiDoppelterLfdNr = "amEndeEinfuegen"; }
    int idopp = -1;
    if (beiDoppelterLfdNr.equals("buchstAnhaengen")) idopp = 0;
    else if (beiDoppelterLfdNr.equals("amEndeEinfuegen")) idopp = 1;
    else if (beiDoppelterLfdNr.equals("nichtImportieren")) idopp = 2;
    else { System.out.println("Ungültiger Wert für 'beiDoppelterLfdNr'."); printArgs(); }
    if (!EfaUtil.canOpenFile(importFb)) { System.out.println("Kann Fahrtenbuchdatei '"+importFb+"' nicht öffnen."); printArgs(); }
    if (!EfaUtil.canOpenFile(fb)) { System.out.println("Kann Fahrtenbuchdatei '"+fb+"' nicht öffnen."); printArgs(); }
    Fahrtenbuch quellFb = new Fahrtenbuch(importFb);
    Fahrtenbuch zielFb = new Fahrtenbuch(fb);
    if (!quellFb.readFile()) { System.out.println("Kann Fahrtenbuchdatei '"+quellFb.getFileName()+"' nicht lesen."); printArgs(); }
    if (!zielFb.readFile()) { System.out.println("Kann Fahrtenbuchdatei '"+zielFb.getFileName()+"' nicht lesen."); printArgs(); }
    String result = ImportFrame.doImport(quellFb,zielFb,lfdNrAdd,lfdNrAddChar,lfdNrVon,lfdNrBis,datumVon,datumBis,gruppe,importDatenlisten,idopp);
    if (!zielFb.writeFile()) { System.out.println("Kann Fahrtenbuchdatei '"+zielFb.getFileName()+"' nicht speichern."); printArgs(); }
    if (result == null) {
      System.out.println("Import erfolgreich abgeschlossen. "+ImportFrame.anzImportierteFahrten+" Einträge importiert.");
      System.exit(0);
    } else {
      System.out.println("Fehler beim Import: "+result);
      System.exit(1);
    }
  }


  /**Main method*/
  public static void main(String[] args) {
    if (args.length > 0) checkargs(args);

    efaIni();

    EfaSec efaSec = new EfaSec(Daten.efaProgramDirectory+Daten.EFA_SECFILE);
    // if (!efaSec.secFileExists()) ... wird in EfaFrame getestet.
    if (efaSec.secFileExists() && !efaSec.secValueValid()) {
      Dialog.error("Die Sicherheitsdatei ist korrupt!\n"+
                   "Aus Gründen der Sicherheit verweigert efa daher den Dienst.\n"+
                   "Um efa zu reaktivieren, wende Dich an den Entwickler: "+Daten.EFAEMAIL);
      System.exit(100);
    }

    if (stat != null || importFb != null) {
      if (fb == null || fb.equals("efa") || fb.equals("efadirekt")) {
        // efa-Config
        Daten.efaConfig = new EfaConfig(Daten.efaCfgDirectory+Daten.CONFIGFILE);
        if (EfaUtil.canOpenFile(Daten.efaCfgDirectory+Daten.CONFIGFILE)) {
          Daten.efaConfig.readFile();
          if (fb == null || fb.equals("efa")) {
            fb = Daten.efaConfig.letzteDatei;
          } else if (fb != null && fb.equals("efadirekt")) fb = Daten.efaConfig.direkt_letzteDatei;
        }
        if (fb == null) {
          System.out.println("Kein Fahrtenbuch angegeben.");
          printArgs();
        }
      }
      System.out.println("Fahrtenbuch: "+fb);
      if (!EfaUtil.canOpenFile(fb)) {
        System.out.println("Fahrtenbuch '"+fb+"' nicht gefunden!");
        System.exit(1);
      }
      if (stat != null) {
        createStat();
      } else if (importFb != null) {
        doImportFb();
      }
    } else {
      new Efa();
    }

  }
}
