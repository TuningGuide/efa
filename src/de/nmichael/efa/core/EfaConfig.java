package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import de.nmichael.efa.direkt.Admin;
import java.awt.Font;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class EfaConfig extends DatenListe {

  // Default-Obmann für ungesteuerte Boote
  public static final int OBMANN_NR1 = 1;
  public static final int OBMANN_SCHLAG = 2;

  // Werte für Longitude/Latitude
  public static final int LL_NORTH = 0;
  public static final int LL_SOUTH = 1;
  public static final int LL_WEST = 2;
  public static final int LL_EAST = 3;


  // ------------- Intern -------------
  public String language=null;              // Sprache: null=default; de=deutsch, en=english etc.
  public String letzteDatei="";             // zuletzt bearbeitete Fahrtenbuchdatei
  public String version = "100";            // efa Versionsnummer
  public int countEfaStarts=0;              // Wie oft wurde efa schon gestartet
  public String direkt_letzteDatei;         // zuletzt bearbeitete Fahrtenbuchdatei
  public Hashtable admins;                  // Liste von Administratoren (Hash of String(Name)->Admin)
  public static final String SUPERADMIN = "admin";// Name des Super-Administrators

  // ------------- Allgemein -------------
  public boolean autogenAlias;              // automatisches Generieren von Aliasnamen zulassen
  public String aliasFormat;                // Format der automatisch generierten Aliasnamen
                                            // Dieser String darf beliebige Zeichen enthalten, die als solche interpretiert
                                            // werden, sowie Variablen der Gestalt {<Feld><Position>}, wobei
                                            //   <Feld>     := "V" | "N" | "C" . (V: Vorname; N: Nachname; C: Club=Verein)
                                            //   <Position> := {1..9} .
                                            // <Position> gibt die Position des Zeichens in <Feld> an.
                                            // Beispiel: "{V1}{V2}{N1}": die ersten zwei Buchstaben des Vornamens und der erste des Nachnamens
  public boolean autoStandardmannsch=false; // Standardmannschaft sofort bei Bootseingabe ausfüllen
  public boolean showObmann=true;           // Obmann-Auswahlliste anzeigen
  public boolean autoObmann=true;           // Obmann automatisch bei Eingabe auswählen
  public int     defaultObmann=OBMANN_NR1;  // Default-Obmann für ungesteuerte Boote
  public boolean popupComplete=true;        // Popup-Window zum Vervollständigen von Eingaben
  public boolean correctMisspelledMitglieder=true; // korrigiere Einträge mit Tippfehlern bei Mitgliedern
  public boolean correctMisspelledBoote=true;      // korrigiere Einträge mit Tippfehlern bei Booten
  public boolean correctMisspelledZiele=true;      // korrigiere Einträge mit Tippfehlern bei Zielen
  public boolean skipUhrzeit=false;         // "Abfahrt" und "Ankunft" überspringen
  public boolean skipZiel=false;            // "Ziel" überspringen
  public boolean skipMannschKm=false;       // "Mannsch-Km" überspringen
  public boolean skipBemerk=false;          // "Bemerkungen" überspringen
  public Hashtable keys = null;             // Hash der Strings für eine Funktionstaste
  public boolean fensterZentriert=false;    // Fenster immer zentriert auf Bildschirm anzeigen (unabh. von akt. Fensterposition)
  public int windowXOffset=0;               // Fenster bekommt bei Positionierung immer diesen Wert zur X-Koordinate hinzuaddiert
  public int windowYOffset=0;               // Fenster bekommt bei Positionierung immer diesen Wert zur Y-Koordinate hinzuaddiert
  public int screenWidth=0;                 // Bildschirm-Breite (0=default)
  public int screenHeight=0;                // Bildschirm-Höhe (0=default)
  public int maxDialogWidth=0;              // maximale Breite von JOptionPane-Dialogen (Anzahl von Zeichen; 0=auto)
  public int maxDialogHeight=0;             // maximale Höhe von JOptionPane-Dialogen (Anzahl von Zeilen; 0=auto)
  public String lookAndFeel = null;         // Klassenname des Look&Feel
  public boolean showBerlinOptions = true;  // Optionen für Berliner Vereine anzeigen
  public boolean debugLogging = false;      // Debug-Logging-Ausschriften aktivieren
  public String efaVersionLastCheck = "";   // Datum, wann zum letzten Mal geprüft wurde, ob eine aktuelle Version vorliegt
  public String zielfahrtSeparatorBereiche = ","; // Separator für Zielbereiche einer Zielfahrt
  public String zielfahrtSeparatorFahrten = "/";  // Separator für Zielbereiche verschiedener Zielfahrten
  public String standardFahrtart = "";      //Standard Art der Fahrt

  // ------------- Backup -------------
  public String bakDir="";                  // Backup-Verzeichnis
  public boolean bakSave;                   // bei jedem Speichern Bakup anlegen
  public boolean bakMonat;                  // jeden Monat Backup anlegen
  public boolean bakTag;                    // jeden Tag Backup anlegen
  public boolean bakKonv;                   // bei jedem Konvertieren Backup anlegen

  // ------------- Drucken -------------
  public int printPageWidth   = 210;        // Seitenbreite (mm)
  public int printPageHeight  = 297;        // Seitenhöhe (mm)
  public int printLeftMargin  = 15;         // linker und rechter Rand (mm)
  public int printTopMargin   = 15;         // oberer und unterer Rand (mm)
  public int printPageOverlap = 5;          // Überlappung bei aufeinanderfolgenden Seiten (mm)

  // ------------- efa im Bootshaus -------------
  public boolean efaDirekt_zielBeiFahrtbeginnPflicht; // bei Abfahrt muß immer ein Ziel angegeben werden
  public boolean efaDirekt_eintragErzwingeObmann;     // beim Eintragen von Fahrten muß ein Obmann ausgewählt werden
  public boolean efaDirekt_eintragErlaubeNurMaxRudererzahl; // beim Eintragen von Fahrten darf maximal die zum Boot passende Anzahl von Ruderern eingegeben werden
  public boolean efaDirekt_eintragNichtAenderbarUhrzeit; // beim Beginnen und Beenden von Fahrten ist die vorgeschlagene Uhrzeit nicht änderbar
  public boolean efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen; // beim Eintragen von Fahrten ist für bekannte Ziele die vorgeschlagene Kilometerzahl nicht änderbar
  public boolean efaDirekt_eintragNurBekannteBoote;   // beim Eintragen von Fahrten nur bekannte Boote erlauben
  public boolean efaDirekt_eintragNurBekannteRuderer; // beim Eintragen von Fahrten nur bekannte Ruderer erlauben
  public boolean efaDirekt_eintragNurBekannteZiele;   // beim Eintragen von Fahrten nur bekannte Ziele erlauben
  public int efaDirekt_plusMinutenAbfahrt;            // x Minuten zu aktueller Zeit bei Abfahrt hinzuaddieren
  public int efaDirekt_minusMinutenAnkunft;           // x Minuten von aktueller Zeit bei Ankunft abziehen
  public boolean efaDirekt_mitgliederDuerfenReservieren; // Mitglieder dürfen Bootsreservierungen vornehmen (einmalige Reservierungen)
  public boolean efaDirekt_mitgliederDuerfenReservierenZyklisch; // Mitglieder dürfen Bootsreservierungen vornehmen (zyklische Reservierungen)
  public boolean efaDirekt_mitgliederDuerfenReservierungenEditieren; // Mitglieder dürfen Bootsreservierungen bearbeiten
  public boolean efaDirekt_mitgliederDuerfenEfaBeenden; // dürfen normale Mitglieder efa beenden?
  public boolean efaDirekt_mitgliederDuerfenNamenHinzufuegen; // dürfen normale Mitglieder Namen zur Namensliste hinzufügen?
  public boolean efaDirekt_resBooteNichtVerfuegbar;   // reservierte Boote auf NICHT_VERFUEGBAR (true) oder VERFUEGBAR (false) setzen
  public boolean efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar; // Boote, die auf Wanderfahrt oder Regatta unterwegs sind, als "nicht verfügbar" anzeigen
  public int efaDirekt_resLookAheadTime;              // LookAheadTime in Minuten für zukünftig beginnende Reservierungen
  public String efaDirekt_execOnEfaExit;              // Kommando, das beim Beenden von efa durch Mitglieder ausgeführt werden soll
  public TMJ efaDirekt_exitTime;                      // Uhrzeit, zu der efa automatisch beendet werden soll
  public String efaDirekt_execOnEfaAutoExit;          // Kommando, das beim automatischen Beenden von efa ausgeführt werden soll
  public TMJ efaDirekt_restartTime;                   // Uhrzeit, zu der efa automatisch einmal täglich neu gestartet werden soll (default 4:00 Uhr)
  public boolean efaDirekt_checkRunning;

  public String efaDirekt_butFahrtBeginnenFarbe;
  public String efaDirekt_butFahrtBeendenFarbe;
  public String efaDirekt_butFahrtAbbrechenFarbe;
  public String efaDirekt_butNachtragFarbe;
  public String efaDirekt_butBootsreservierungenFarbe;
  public String efaDirekt_butFahrtenbuchAnzeigenFarbe;
  public String efaDirekt_butStatistikErstellenFarbe;
  public String efaDirekt_butNachrichtAnAdminFarbe;
  public String efaDirekt_butAdminModusFarbe;
  public String efaDirekt_butSpezialFarbe;

  public String efaDirekt_butFahrtBeginnenText;
  public String efaDirekt_butFahrtBeendenText;
  public String efaDirekt_butSpezialText;

  public boolean efaDirekt_butBootsreservierungenAnzeigen;
  public boolean efaDirekt_butFahrtenbuchAnzeigenAnzeigen;
  public boolean efaDirekt_butStatistikErstellenAnzeigen;
  public boolean efaDirekt_butNachrichtAnAdminAnzeigen;
  public boolean efaDirekt_butAdminModusAnzeigen;
  public boolean efaDirekt_butSpezialAnzeigen;

  public String efaDirekt_lockEfaShowHtml;
  public boolean efaDirekt_lockEfaVollbild;
  public TMJ efaDirekt_lockEfaFromDatum;
  public TMJ efaDirekt_lockEfaFromZeit;
  public TMJ efaDirekt_lockEfaUntilDatum;
  public TMJ efaDirekt_lockEfaUntilZeit;
  public boolean efaDirekt_locked;

  public String efaDirekt_butSpezialCmd;

  public boolean efaDirekt_showButtonHotkey;
  public boolean efaDirekt_showUhr;
  public boolean efaDirekt_sunRiseSet_show;
  public int[] efaDirekt_sunRiseSet_ll;
  public String efaDirekt_vereinsLogo;
  public String efaDirekt_newsText;

  public boolean efaDirekt_startMaximized;
  public boolean efaDirekt_fensterNichtVerschiebbar;
  public boolean efaDirekt_immerImVordergrund;
  public boolean efaDirekt_immerImVordergrundBringToFront;
  public boolean efaDirekt_sortByAnzahl;
  public boolean efaDirekt_showEingabeInfos;
  public boolean efaDirekt_showBootsschadenButton;
  public int efaDirekt_maxFBAnzeigenFahrten;          // maximale Anzahl der Fahrten, die bei "Fahrtenbuch anzeigen" angezeigt werden können
  public int efaDirekt_anzFBAnzeigenFahrten;          // standardmäßige Anzahl der Fahrten, die bei "Fahrtenbuch anzeigen" angezeigt werden können
  public boolean efaDirekt_FBAnzeigenAuchUnvollstaendige;
  public TMJ efaDirekt_autoNewFb_datum;
  public String efaDirekt_autoNewFb_datei;
  public int efaDirekt_fontSize;
  public int efaDirekt_fontStyle;
  public boolean efaDirekt_colorizeInputField;
  public boolean efaDirekt_showZielnameFuerBooteUnterwegs;

  public boolean efaDirekt_bnrError_admin;
  public boolean efaDirekt_bnrError_bootswart;
  public boolean efaDirekt_bnrWarning_admin;
  public boolean efaDirekt_bnrWarning_bootswart;
  public boolean efaDirekt_bnrBootsstatus_admin;
  public boolean efaDirekt_bnrBootsstatus_bootswart;
  public long efaDirekt_bnrWarning_lasttime;

  public String efaDirekt_emailServer;
  public String efaDirekt_emailUsername;
  public String efaDirekt_emailPassword;
  public String efaDirekt_emailAbsender;
  public String efaDirekt_emailAbsenderName;
  public String efaDirekt_emailBetreffPraefix;
  public String efaDirekt_emailSignatur;


  // ------------- Externe Programme -------------
  public String browser="";                 // Pfad zum Webbrowser
  public String acrobat="";                 // Pfad zum Acrobat Reader




  public static final String LOOKANDFEEL_STANDARD = "Standard";

  public static final String KENNUNG100 = "##EFA.100.KONFIGURATION##";

  // Konstruktor
  public EfaConfig(String pdat) {
    super(pdat,0,0,false);
    kennung = KENNUNG100;
    reset();
    this.backupEnabled = false; // Aus Sicherheitsgründen kein Backup von efa.cfg anlegen!!
  }

  // Einstellungen zurücksetzen
  void reset() {
    language = null;
    letzteDatei="";
    autogenAlias = true;
    aliasFormat = "{V1}{V2}-{N1}";
    if (new File(Daten.efaMainDirectory + "backup").isDirectory())
      bakDir = Daten.efaMainDirectory + "backup" + Daten.fileSep;
    else bakDir = Daten.efaMainDirectory;
    bakSave = true;
    bakMonat = true;
    bakTag = false;
    bakKonv = true;
    browser = "";
    acrobat = "";
    printPageWidth   = 210;
    printPageHeight  = 297;
    printLeftMargin  = 15;
    printTopMargin   = 15;
    printPageOverlap = 5;
    keys = new Hashtable();
    countEfaStarts=0;
    autoStandardmannsch=false;
    showObmann=true;
    autoObmann=true;
    defaultObmann=OBMANN_NR1;
    popupComplete=true;
    correctMisspelledMitglieder=true;
    correctMisspelledBoote=true;
    correctMisspelledZiele=true;
    skipUhrzeit=false;
    skipZiel=false;
    skipMannschKm=false;
    skipBemerk=false;
    fensterZentriert=false;
    windowXOffset=0;
    windowYOffset=0;
    screenWidth=0;
    screenHeight=0;
    maxDialogHeight=0;
    maxDialogWidth=0;
    lookAndFeel = (Daten.osName != null && Daten.osName.equals("Linux") ? "javax.swing.plaf.metal.MetalLookAndFeel" : LOOKANDFEEL_STANDARD);
    showBerlinOptions = true;
    zielfahrtSeparatorBereiche = ",";
    zielfahrtSeparatorFahrten = "/";
    standardFahrtart = "";
    debugLogging = false;
    efaVersionLastCheck = "";
    version = "100";

    direkt_letzteDatei="";
    admins = new Hashtable();
    efaDirekt_zielBeiFahrtbeginnPflicht = false;
    efaDirekt_eintragErzwingeObmann = false;
    efaDirekt_eintragErlaubeNurMaxRudererzahl = false;
    efaDirekt_eintragNichtAenderbarUhrzeit = false;
    efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen = false;
    efaDirekt_eintragNurBekannteBoote = false;
    efaDirekt_eintragNurBekannteRuderer = false;
    efaDirekt_eintragNurBekannteZiele = false;
    efaDirekt_plusMinutenAbfahrt = 10;
    efaDirekt_minusMinutenAnkunft = 10;
    efaDirekt_mitgliederDuerfenReservieren = true;
    efaDirekt_mitgliederDuerfenReservierenZyklisch = false;
    efaDirekt_mitgliederDuerfenReservierungenEditieren = false;
    efaDirekt_mitgliederDuerfenEfaBeenden = true;
    efaDirekt_mitgliederDuerfenNamenHinzufuegen = false;
    efaDirekt_resBooteNichtVerfuegbar = false;
    efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar = false;
    efaDirekt_resLookAheadTime = 120;
    efaDirekt_execOnEfaExit="";
    efaDirekt_exitTime = new TMJ(-1,-1,-1);
    efaDirekt_execOnEfaAutoExit="";
    efaDirekt_restartTime = new TMJ(4,0,-1);
    efaDirekt_checkRunning=true;
    efaDirekt_butFahrtBeginnenFarbe="CCFFCC";
    efaDirekt_butFahrtBeendenFarbe="CCFFCC";
    efaDirekt_butFahrtAbbrechenFarbe="FFCCCC";
    efaDirekt_butNachtragFarbe="CCFFFF";
    efaDirekt_butBootsreservierungenFarbe="FFFFCC";
    efaDirekt_butFahrtenbuchAnzeigenFarbe="CCCCFF";
    efaDirekt_butStatistikErstellenFarbe="CCCCFF";
    efaDirekt_butNachrichtAnAdminFarbe="FFF197";
    efaDirekt_butAdminModusFarbe="CCCCCC";
    efaDirekt_butSpezialFarbe="CCCCCC";
    efaDirekt_butFahrtBeginnenText="Fahrt beginnen >>>";
    efaDirekt_butFahrtBeendenText="<<< Fahrt beenden";
    efaDirekt_butSpezialText="Spezial-Button";
    efaDirekt_butBootsreservierungenAnzeigen=true;
    efaDirekt_butFahrtenbuchAnzeigenAnzeigen=true;
    efaDirekt_butStatistikErstellenAnzeigen=true;
    efaDirekt_butNachrichtAnAdminAnzeigen=true;
    efaDirekt_butAdminModusAnzeigen=true;
    efaDirekt_butSpezialAnzeigen=false;
    efaDirekt_butSpezialCmd="";
    efaDirekt_showButtonHotkey=false;
    efaDirekt_showUhr=true;
    efaDirekt_sunRiseSet_show=false;
    efaDirekt_sunRiseSet_ll = new int[8];
    efaDirekt_sunRiseSet_ll[0] = LL_NORTH; efaDirekt_sunRiseSet_ll[1] = 52; efaDirekt_sunRiseSet_ll[2] = 25; efaDirekt_sunRiseSet_ll[3] = 9;
    efaDirekt_sunRiseSet_ll[4] = LL_EAST;  efaDirekt_sunRiseSet_ll[5] = 13; efaDirekt_sunRiseSet_ll[6] = 10; efaDirekt_sunRiseSet_ll[7] = 15;
    efaDirekt_sortByAnzahl=true;
    efaDirekt_showEingabeInfos=true;
    efaDirekt_showBootsschadenButton=true;
    efaDirekt_maxFBAnzeigenFahrten = 100;
    efaDirekt_anzFBAnzeigenFahrten = 50;
    efaDirekt_FBAnzeigenAuchUnvollstaendige = false;
    efaDirekt_autoNewFb_datum = null;
    efaDirekt_autoNewFb_datei = "";
    efaDirekt_fontSize=0;
    efaDirekt_fontStyle=-1;
    efaDirekt_colorizeInputField=true;
    efaDirekt_showZielnameFuerBooteUnterwegs=false;
    efaDirekt_vereinsLogo="";
    efaDirekt_newsText="";
    efaDirekt_startMaximized=false;
    efaDirekt_fensterNichtVerschiebbar=false;
    efaDirekt_immerImVordergrund=false;
    efaDirekt_immerImVordergrundBringToFront=false;
    efaDirekt_bnrError_admin = true;
    efaDirekt_bnrError_bootswart = false;
    efaDirekt_bnrWarning_admin = true;
    efaDirekt_bnrWarning_bootswart = false;
    efaDirekt_bnrBootsstatus_admin = false;
    efaDirekt_bnrBootsstatus_bootswart = false;
    efaDirekt_bnrWarning_lasttime = System.currentTimeMillis() - 7l*24l*60l*60l*1000l; // vor 1 Woche

    efaDirekt_emailServer="";
    efaDirekt_emailAbsender="";
    efaDirekt_emailUsername="";
    efaDirekt_emailPassword="";
    efaDirekt_emailAbsenderName="efa";
    efaDirekt_emailBetreffPraefix="efa";
    efaDirekt_emailSignatur="Diese Nachricht wurde von efa verschickt.";

    efaDirekt_lockEfaShowHtml = "";
    efaDirekt_lockEfaVollbild = false;
    efaDirekt_lockEfaFromDatum = null;
    efaDirekt_lockEfaFromZeit = null;
    efaDirekt_lockEfaUntilDatum = null;
    efaDirekt_lockEfaUntilZeit = null;
    efaDirekt_locked = false;
  }


  private void getTaste(String s) {
    if (s == null || s.length()==0 || keys == null) return;
    int pos = s.indexOf("|");
    if (pos<0) return;
    keys.put(s.substring(0,pos),s.substring(pos+1,s.length()));
  }


  public synchronized boolean readEinstellungen() {
    reset();

    // Konfiguration lesen
    String s;
    try {
      while ((s = freadLine()) != null) {
        s = s.trim();
        if (s.startsWith("VERSION="))
            version=s.substring(8,s.length()).trim();
        if (s.startsWith("LANGUAGE=")) {
            language=s.substring(9,s.length()).trim();
            if (language.length() == 0) language = null;
            if (language != null) {
                International.initialize();
            }
        }
        if (s.startsWith("DATEI="))
            letzteDatei=s.substring(6,s.length()).trim();
        if (s.startsWith("AUTOGEN_ALIAS="))
            autogenAlias= s.substring(14,s.length()).trim().equals("Y");
        if (s.startsWith("ALIASFORMAT="))
            aliasFormat=s.substring(12,s.length()).trim();

        if (s.startsWith("BACKUPDIR="))
            bakDir=s.substring(10,s.length()).trim();
        if (s.startsWith("BACKUP_ONSAVE="))
            bakSave= s.substring(14,s.length()).trim().equals("Y");
        if (s.startsWith("BACKUP_MONTH="))
            bakMonat= s.substring(13,s.length()).trim().equals("Y");
        if (s.startsWith("BACKUP_DAY="))
            bakTag= s.substring(11,s.length()).trim().equals("Y");
        if (s.startsWith("BACKUP_ONCONV="))
            bakKonv= s.substring(14,s.length()).trim().equals("Y");

        if (s.startsWith("BROWSER="))
            browser= s.substring(8,s.length()).trim();
        if (s.startsWith("ACROBAT="))
            acrobat= s.substring(8,s.length()).trim();

        if (s.startsWith("PRINT_PAGEWIDTH="))
            printPageWidth= EfaUtil.string2int(s.substring(16,s.length()).trim(),0);
        if (s.startsWith("PRINT_PAGEHEIGHT="))
            printPageHeight= EfaUtil.string2int(s.substring(17,s.length()).trim(),0);
        if (s.startsWith("PRINT_LEFTMARGIN="))
            printLeftMargin= EfaUtil.string2int(s.substring(17,s.length()).trim(),0);
        if (s.startsWith("PRINT_TOPMARGIN="))
            printTopMargin= EfaUtil.string2int(s.substring(16,s.length()).trim(),0);
        if (s.startsWith("PRINT_PAGEOVERLAP="))
            printPageOverlap= EfaUtil.string2int(s.substring(18,s.length()).trim(),0);

        if (s.startsWith("TASTE="))
            getTaste(s.substring(6,s.length()).trim());

        if (s.startsWith("AUTO_STANDARDMANNSCH="))
            autoStandardmannsch= s.substring(21,s.length()).trim().equals("Y");
        if (s.startsWith("SHOW_OBMANN="))
            showObmann= s.substring(12,s.length()).trim().equals("Y");
        if (s.startsWith("AUTO_OBMANN="))
            autoObmann= s.substring(12,s.length()).trim().equals("Y");
        if (s.startsWith("DEFAULT_OBMANN="))
            defaultObmann= EfaUtil.string2int(s.substring(15,s.length()).trim(),OBMANN_NR1);
        if (s.startsWith("POPUP_COMPLETE="))
            popupComplete= s.substring(15,s.length()).trim().equals("Y");
        if (s.startsWith("CORRECT_MISSPELLED=")) // altes Format <= 1.7.0
            correctMisspelledMitglieder=correctMisspelledBoote=correctMisspelledZiele= s.substring(19,s.length()).trim().equals("Y");
        if (s.startsWith("CORRECT_MISSPELLED_MITGLIEDER="))
            correctMisspelledMitglieder= s.substring(30,s.length()).trim().equals("Y");
        if (s.startsWith("CORRECT_MISSPELLED_BOOTE="))
            correctMisspelledBoote= s.substring(25,s.length()).trim().equals("Y");
        if (s.startsWith("CORRECT_MISSPELLED_ZIELE="))
            correctMisspelledZiele= s.substring(25,s.length()).trim().equals("Y");
        if (s.startsWith("SKIP_UHRZEIT="))
            skipUhrzeit= s.substring(13,s.length()).trim().equals("Y");
        if (s.startsWith("SKIP_ZIEL="))
            skipZiel= s.substring(10,s.length()).trim().equals("Y");
        if (s.startsWith("SKIP_MANNSCHKM="))
            skipMannschKm= s.substring(15,s.length()).trim().equals("Y");
        if (s.startsWith("SKIP_BEMERK="))
            skipBemerk= s.substring(12,s.length()).trim().equals("Y");
        if (s.startsWith("FENSTER_IMMER_ZENTRIERT="))
            fensterZentriert= s.substring(24,s.length()).trim().equals("Y");
        if (s.startsWith("WINDOW_X_OFFSET="))
            windowXOffset= EfaUtil.string2int(s.substring(16,s.length()).trim(),0);
        if (s.startsWith("WINDOW_Y_OFFSET="))
            windowYOffset= EfaUtil.string2int(s.substring(16,s.length()).trim(),0);
        if (s.startsWith("SCREEN_WIDTH="))
            screenWidth= EfaUtil.string2int(s.substring(13,s.length()).trim(),0);
        if (s.startsWith("SCREEN_HEIGHT="))
            screenHeight= EfaUtil.string2int(s.substring(14,s.length()).trim(),0);
        if (s.startsWith("MAX_DIALOG_WIDTH="))
            maxDialogWidth= EfaUtil.string2int(s.substring(17,s.length()).trim(),0);
        if (s.startsWith("MAX_DIALOG_HEIGHT="))
            maxDialogHeight= EfaUtil.string2int(s.substring(18,s.length()).trim(),0);
        if (s.startsWith("LOOKANDFEEL="))
            lookAndFeel= s.substring(12,s.length()).trim();
        if (s.startsWith("SHOWBERLINOPTIONS="))
            showBerlinOptions= s.substring(18,s.length()).trim().equals("Y");
        if (s.startsWith("ZIELFAHRT_SEPARATOR_BEREICHE="))
            zielfahrtSeparatorBereiche= s.substring(29,s.length()).trim(); if (zielfahrtSeparatorBereiche.length() > 1) zielfahrtSeparatorBereiche = ",";
        if (s.startsWith("ZIELFAHRT_SEPARATOR_FAHRTEN="))
            zielfahrtSeparatorFahrten= s.substring(28,s.length()).trim(); if (zielfahrtSeparatorFahrten.length() > 1) zielfahrtSeparatorFahrten = "/";
        if (s.startsWith("STANDARD_FAHRTART="))
            standardFahrtart= s.substring(18,s.length()).trim();
        if (s.startsWith("DEBUG_LOGGING="))
            debugLogging = s.substring(14,s.length()).trim().equals("Y");
        if (debugLogging) Logger.debugLogging = true; // für das Loggen wird nur der Wert Logger.debugLogging verwendet (welcher auch über Kommandozeilen-Parameter gesetzt werden kann);
                                                      // der Wert EfaConfig.debugLogging wird nur innerhalb der Konfigurationsdatei verwendet und ist *eine* Möglichkeit, das Debug-Logging zu aktivieren!

        if (s.startsWith("EFASTARTS="))
            countEfaStarts= EfaUtil.string2int(s.substring(10,s.length()).trim(),0);
        if (s.startsWith("EFAVERSIONLASTCHECK="))
            efaVersionLastCheck= s.substring(20,s.length()).trim();


        // ------------------- EFA DIREKT -------------------
        if (s.startsWith("DIREKTDATEI="))
            direkt_letzteDatei=s.substring(12,s.length()).trim();
        if (s.startsWith("ADMIN="))
            addAdmin(s.substring(6,s.length()).trim());

        if (s.startsWith("ZIEL_BEI_FAHRTBEGINN="))
            efaDirekt_zielBeiFahrtbeginnPflicht=s.substring(21,s.length()).trim().equals("Y");
        if (s.startsWith("ERZWINGE_OBMANN="))
            efaDirekt_eintragErzwingeObmann=s.substring(16,s.length()).trim().equals("Y");
        if (s.startsWith("ERLAUBE_NUR_MAX_RUDERERZAHL="))
            efaDirekt_eintragErlaubeNurMaxRudererzahl=s.substring(28,s.length()).trim().equals("Y");
        if (s.startsWith("FAHRT_BEGINNEN_BEENDEN_UHRZEIT_NICHT_AENDERBAR="))
            efaDirekt_eintragNichtAenderbarUhrzeit=s.substring(47,s.length()).trim().equals("Y");
        if (s.startsWith("BEI_BEKANNTEN_ZIELEN_KM_NICHT_AENDERBAR="))
            efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen=s.substring(40,s.length()).trim().equals("Y");
        if (s.startsWith("NUR_BEKANNTE_BOOTE="))
            efaDirekt_eintragNurBekannteBoote=s.substring(19,s.length()).trim().equals("Y");
        if (s.startsWith("NUR_BEKANNTE_RUDERER="))
            efaDirekt_eintragNurBekannteRuderer=s.substring(21,s.length()).trim().equals("Y");
        if (s.startsWith("NUR_BEKANNTE_ZIELE="))
            efaDirekt_eintragNurBekannteZiele=s.substring(19,s.length()).trim().equals("Y");

        if (s.startsWith("ABFAHRT_PLUS_MINUTEN="))
            efaDirekt_plusMinutenAbfahrt=EfaUtil.string2int(s.substring(21,s.length()).trim(),0);
        if (s.startsWith("ANKUNFT_MINUS_MINUTEN="))
            efaDirekt_minusMinutenAnkunft=EfaUtil.string2int(s.substring(22,s.length()).trim(),0);
        if (s.startsWith("BOOTSRESERVIERUNGEN_MITGLIEDER1="))
            efaDirekt_mitgliederDuerfenReservieren=s.substring(32,s.length()).trim().equals("Y");
        if (s.startsWith("BOOTSRESERVIERUNGEN_MITGLIEDER1_ZYKLISCH="))
            efaDirekt_mitgliederDuerfenReservierenZyklisch=s.substring(41,s.length()).trim().equals("Y");
        if (s.startsWith("BOOTSRESERVIERUNGEN_MITGLIEDER="))
            efaDirekt_mitgliederDuerfenReservierungenEditieren=s.substring(31,s.length()).trim().equals("Y");
        if (s.startsWith("EFA_BEENDEN_MITGLIEDER="))
            efaDirekt_mitgliederDuerfenEfaBeenden=s.substring(23,s.length()).trim().equals("Y");
        if (s.startsWith("MITGLIEDER_NAMEN_HINZUFUEGEN="))
            efaDirekt_mitgliederDuerfenNamenHinzufuegen=s.substring(29,s.length()).trim().equals("Y");
        if (s.startsWith("RES_BOOTE_NICHT_VERFUEGBAR="))
            efaDirekt_resBooteNichtVerfuegbar=s.substring(27,s.length()).trim().equals("Y");
        if (s.startsWith("WAFA_REGATTA_BOOTE_NICHT_VERFUEGBAR="))
            efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar=s.substring(36,s.length()).trim().equals("Y");
        if (s.startsWith("RES_LOOK_AHEAD_TIME="))
            efaDirekt_resLookAheadTime=EfaUtil.string2int(s.substring(20,s.length()).trim(),120);
        if (s.startsWith("EXEC_ON_EFA_EXIT="))
            efaDirekt_execOnEfaExit=s.substring(17,s.length()).trim();
        if (s.startsWith("EXIT_TIME="))
            efaDirekt_exitTime=EfaUtil.string2date(s.substring(10,s.length()).trim(),-1,-1,-1);
        if (s.startsWith("EXEC_ON_EFA_AUTO_EXIT="))
            efaDirekt_execOnEfaAutoExit=s.substring(22,s.length()).trim();
        if (s.startsWith("RESTART_TIME="))
            efaDirekt_restartTime=EfaUtil.string2date(s.substring(13,s.length()).trim(),-1,-1,-1);
        if (s.startsWith("DOPPELSTART_VERHINDERN="))
            efaDirekt_checkRunning=s.substring(23,s.length()).trim().equals("Y");

        if (s.startsWith("BUTTON_FAHRT_BEGINNEN=")) {
            efaDirekt_butFahrtBeginnenFarbe=getButtonConfig(s.substring(22,s.length()).trim())[0];
            efaDirekt_butFahrtBeginnenText =getButtonConfig(s.substring(22,s.length()).trim())[1];
        }
        if (s.startsWith("BUTTON_FAHRT_BEENDEN=")) {
            efaDirekt_butFahrtBeendenFarbe=getButtonConfig(s.substring(21,s.length()).trim())[0];
            efaDirekt_butFahrtBeendenText =getButtonConfig(s.substring(21,s.length()).trim())[1];
        }
        if (s.startsWith("BUTTON_FAHRT_ABBRECHEN=")) {
            efaDirekt_butFahrtAbbrechenFarbe=getButtonConfig(s.substring(23,s.length()).trim())[0];
        }
        if (s.startsWith("BUTTON_NACHTRAG=")) {
            efaDirekt_butNachtragFarbe=getButtonConfig(s.substring(16,s.length()).trim())[0];
        }
        if (s.startsWith("BUTTON_BOOTSRESERVIERUNGEN=")) {
            efaDirekt_butBootsreservierungenFarbe=getButtonConfig(s.substring(27,s.length()).trim())[0];
            efaDirekt_butBootsreservierungenAnzeigen=getButtonConfig(s.substring(27,s.length()).trim())[1].equals("+");
        }
        if (s.startsWith("BUTTON_FAHRTENBUCH_ANZEIGEN=")) {
            efaDirekt_butFahrtenbuchAnzeigenFarbe=getButtonConfig(s.substring(28,s.length()).trim())[0];
            efaDirekt_butFahrtenbuchAnzeigenAnzeigen=getButtonConfig(s.substring(28,s.length()).trim())[1].equals("+");
        }
        if (s.startsWith("BUTTON_STATISTIK_ERSTELLEN=")) {
            efaDirekt_butStatistikErstellenFarbe=getButtonConfig(s.substring(27,s.length()).trim())[0];
            efaDirekt_butStatistikErstellenAnzeigen=getButtonConfig(s.substring(27,s.length()).trim())[1].equals("+");
        }
        if (s.startsWith("BUTTON_NACHRICHT_AN_ADMIN=")) {
            efaDirekt_butNachrichtAnAdminFarbe=getButtonConfig(s.substring(26,s.length()).trim())[0];
            efaDirekt_butNachrichtAnAdminAnzeigen=getButtonConfig(s.substring(26,s.length()).trim())[1].equals("+");
        }
        if (s.startsWith("BUTTON_ADMIN_MODUS=")) {
            efaDirekt_butAdminModusFarbe=getButtonConfig(s.substring(19,s.length()).trim())[0];
            efaDirekt_butAdminModusAnzeigen=getButtonConfig(s.substring(19,s.length()).trim())[1].equals("+");
        }
        if (s.startsWith("BUTTON_SPEZIAL=")) {
            efaDirekt_butSpezialFarbe=getButtonConfig(s.substring(15,s.length()).trim())[0];
            efaDirekt_butSpezialAnzeigen=getButtonConfig(s.substring(15,s.length()).trim())[1].equals("+");
            efaDirekt_butSpezialText =getButtonConfig(s.substring(15,s.length()).trim())[2];
            efaDirekt_butSpezialCmd =getButtonConfig(s.substring(15,s.length()).trim())[3];
        }
        if (s.startsWith("BUTTON_HOTKEYS_ANZEIGEN="))
          efaDirekt_showButtonHotkey = s.substring(24,s.length()).trim().equals("+");
        if (s.startsWith("UHR_ANZEIGEN="))
          efaDirekt_showUhr = s.substring(13,s.length()).trim().equals("+");
        if (s.startsWith("SUNRISESET_ANZEIGEN="))
          efaDirekt_sunRiseSet_show = s.substring(20,s.length()).trim().equals("+");
        if (s.startsWith("SUNRISESET_LL="))
          efaDirekt_sunRiseSet_ll = EfaUtil.kommaList2IntArr(s.substring(14,s.length()).trim(),',');
        if (s.startsWith("BOOTE_SORTIEREN_NACH_ANZ_RUDERER="))
          efaDirekt_sortByAnzahl = s.substring(33,s.length()).trim().equals("+");
        if (s.startsWith("VEREINSLOGO="))
            efaDirekt_vereinsLogo= s.substring(12,s.length()).trim();
        if (s.startsWith("NEWSTEXT="))
            efaDirekt_newsText= s.substring(9,s.length()).trim();

        if (s.startsWith("STARTMAXIMIZED="))
            efaDirekt_startMaximized = s.substring(15,s.length()).trim().equals("+");
        if (s.startsWith("FENSTER_NICHT:VERSCHIEBBAR="))
            efaDirekt_fensterNichtVerschiebbar = s.substring(27,s.length()).trim().equals("+");
        if (s.startsWith("IMMER_IM_VORDERGRUND="))
            efaDirekt_immerImVordergrund = s.substring(21,s.length()).trim().equals("+");
        if (s.startsWith("IMMER_IM_VORDERGRUND_BRING_TO_FRONT="))
            efaDirekt_immerImVordergrundBringToFront = s.substring(36,s.length()).trim().equals("+");
        if (s.startsWith("SHOW_EINGABE_INFOS="))
            efaDirekt_showEingabeInfos = s.substring(19,s.length()).trim().equals("+");
        if (s.startsWith("SHOW_BOOTSSCHADEN_BUTTON="))
            efaDirekt_showBootsschadenButton = s.substring(25,s.length()).trim().equals("+");
        if (s.startsWith("SHOW_ZIELNAME_BOOTEUNTERWEGS="))
            efaDirekt_showZielnameFuerBooteUnterwegs = s.substring(29,s.length()).trim().equals("+");

        if (s.startsWith("FB_ANZEIGEN_MAX_FAHRTEN="))
            efaDirekt_maxFBAnzeigenFahrten = EfaUtil.string2int(s.substring(24,s.length()).trim(),1000);
        if (s.startsWith("FB_ANZEIGEN_ANZ_FAHRTEN="))
            efaDirekt_anzFBAnzeigenFahrten = EfaUtil.string2int(s.substring(24,s.length()).trim(),1000);
        if (s.startsWith("FB_ANZEIGEN_AUCH_UNVOLLSTAENDIGE_FAHRTEN="))
            efaDirekt_FBAnzeigenAuchUnvollstaendige = s.substring(41,s.length()).trim().equals("+");

        if (s.startsWith("AUTO_NEUES_FAHRTENBUCH_DATUM=")) {
          String tmp = s.substring(29,s.length()).trim();
          if (tmp.length() == 0) efaDirekt_autoNewFb_datum = null;
          else {
            efaDirekt_autoNewFb_datum = EfaUtil.string2date(tmp,-1,-1,-1);
            if (efaDirekt_autoNewFb_datum.tag == -1 || efaDirekt_autoNewFb_datum.monat == -1 || efaDirekt_autoNewFb_datum.jahr == -1) efaDirekt_autoNewFb_datum = null;
          }
        }
        if (s.startsWith("AUTO_NEUES_FAHRTENBUCH_DATEI=")) efaDirekt_autoNewFb_datei = s.substring(29,s.length()).trim();


        if (s.startsWith("FONT_SIZE="))
            efaDirekt_fontSize = EfaUtil.string2int(s.substring(10,s.length()).trim(),0);
        if (s.startsWith("FONT_STYLE="))
            efaDirekt_fontStyle = EfaUtil.string2int(s.substring(11,s.length()).trim(),0);
        if (s.startsWith("COLORIZE_INPUT_FIELD="))
            efaDirekt_colorizeInputField = s.substring(21,s.length()).trim().equals("+");

        if (s.startsWith("BENACHRICHTIGE_ERROR=")) {
          efaDirekt_bnrError_admin     = s.substring(21, s.length()).trim().startsWith("+");
          efaDirekt_bnrError_bootswart = s.substring(22, s.length()).trim().startsWith("+");
        }
        if (s.startsWith("BENACHRICHTIGE_WARNING=")) {
          efaDirekt_bnrWarning_admin     = s.substring(23, s.length()).trim().startsWith("+");
          efaDirekt_bnrWarning_bootswart = s.substring(24, s.length()).trim().startsWith("+");
        }
        if (s.startsWith("BENACHRICHTIGE_BOOTSSTATUS=")) {
          efaDirekt_bnrBootsstatus_admin     = s.substring(27, s.length()).trim().startsWith("+");
          efaDirekt_bnrBootsstatus_bootswart = s.substring(28, s.length()).trim().startsWith("+");
        }
        if (s.startsWith("BENACHRICHTIGE_WARNING_LASTTIME="))
            efaDirekt_bnrWarning_lasttime = EfaUtil.string2long(s.substring(32,s.length()).trim(),0);

        if (s.startsWith("EMAIL_SERVER="))
            efaDirekt_emailServer = s.substring(13,s.length()).trim();
        if (s.startsWith("EMAIL_ABSENDER="))
            efaDirekt_emailAbsender = s.substring(15,s.length()).trim();
        if (s.startsWith("EMAIL_USERNAME="))
            efaDirekt_emailUsername = s.substring(15,s.length()).trim();
        if (s.startsWith("EMAIL_PASSWORD="))
            efaDirekt_emailPassword = s.substring(15,s.length()).trim();
        if (s.startsWith("EMAIL_ABSENDER_NAME="))
            efaDirekt_emailAbsenderName = s.substring(20,s.length()).trim();
        if (s.startsWith("EMAIL_BETREFF_PRAEFIX="))
            efaDirekt_emailBetreffPraefix = s.substring(22,s.length()).trim();
        if (s.startsWith("EMAIL_SIGNATUR="))
            efaDirekt_emailSignatur = s.substring(15,s.length()).trim();

        if (s.startsWith("LOCK_EFA_SHOW_HTML="))
            efaDirekt_lockEfaShowHtml = s.substring(19,s.length()).trim();
        if (s.startsWith("LOCK_EFA_VOLLBILD="))
            efaDirekt_lockEfaVollbild = s.substring(18,s.length()).equals("+");
        if (s.startsWith("LOCK_EFA_VON_DATUM=") && s.length()>19)
            efaDirekt_lockEfaFromDatum = EfaUtil.correctDate(s.substring(19,s.length()).trim(),0,0,0);
        if (s.startsWith("LOCK_EFA_VON_ZEIT=") && s.length()>18)
            efaDirekt_lockEfaFromZeit = EfaUtil.string2date(s.substring(18,s.length()).trim(),0,0,0);
        if (s.startsWith("LOCK_EFA_BIS_DATUM=") && s.length()>19)
            efaDirekt_lockEfaUntilDatum = EfaUtil.correctDate(s.substring(19,s.length()).trim(),0,0,0);
        if (s.startsWith("LOCK_EFA_BIS_ZEIT=") && s.length()>18)
            efaDirekt_lockEfaUntilZeit = EfaUtil.string2date(s.substring(18,s.length()).trim(),0,0,0);
        if (s.startsWith("LOCK_EFA_LOCKED="))
            efaDirekt_locked = s.substring(16,s.length()).equals("+");

      }
    } catch(IOException e) {
      try {
        fclose(false);
      } catch(Exception ee) {
        return false;
      }
    }
    if ((screenWidth>0 || screenHeight>0) && Dialog.screenSize == null) {
      Dialog.initializeScreenSize();
    }
    if (screenWidth>0) {
      Dialog.screenSize.width = screenWidth;
    }
    if (screenHeight>0) {
      Dialog.screenSize.height = screenHeight;
    }
    if (screenWidth>0 || screenHeight>0) {
      Dialog.initializeMaxDialogSizes();
    }
    if (maxDialogWidth>0 || maxDialogHeight>0) {
      Dialog.setMaxDialogSizes(maxDialogWidth,maxDialogHeight);
    }
    return true;
  }


  // Konfigurationsdatei speichern
  public synchronized boolean writeEinstellungen() {
    // Datei schreiben
    try {
      fwrite("VERSION=" + Daten.PROGRAMMID + "\n");
      fwrite("LANGUAGE=" + (language != null ? language : "") + "\n");
      fwrite("DATEI=" + letzteDatei + "\n");
      fwrite("AUTOGEN_ALIAS=" + ( autogenAlias ? "Y" : "N" ) + "\n");
      fwrite("ALIASFORMAT=" + aliasFormat + "\n");

      fwrite("BACKUPDIR=" + bakDir + "\n");
      fwrite("BACKUP_ONSAVE=" + ( bakSave ? "Y" : "N" ) + "\n");
      fwrite("BACKUP_MONTH=" + ( bakMonat ? "Y" : "N" ) + "\n");
      fwrite("BACKUP_DAY=" + ( bakTag ? "Y" : "N" ) + "\n");
      fwrite("BACKUP_ONCONV=" + ( bakKonv ? "Y" : "N" ) + "\n");

      fwrite("BROWSER=" + browser + "\n");
      fwrite("ACROBAT=" + acrobat + "\n");

      fwrite("PRINT_PAGEWIDTH=" + Integer.toString(printPageWidth) + "\n");
      fwrite("PRINT_PAGEHEIGHT=" + Integer.toString(printPageHeight) + "\n");
      fwrite("PRINT_LEFTMARGIN=" + Integer.toString(printLeftMargin) + "\n");
      fwrite("PRINT_TOPMARGIN=" + Integer.toString(printTopMargin) + "\n");
      fwrite("PRINT_PAGEOVERLAP=" + Integer.toString(printPageOverlap) + "\n");

      if (keys != null) {
        Object[] k = keys.keySet().toArray();
        if (k != null && k.length>0) {
          for (int i=0; i<k.length; i++)
            if (keys.get(k[i]) != null && ((String)(keys.get(k[i]))).length()>0) fwrite("TASTE="+k[i]+"|"+keys.get(k[i])+"\n");
        }
      }

      fwrite("AUTO_STANDARDMANNSCH=" + (autoStandardmannsch ? "Y" : "N") + "\n");
      fwrite("SHOW_OBMANN=" + (showObmann ? "Y" : "N") + "\n");
      fwrite("AUTO_OBMANN=" + (autoObmann ? "Y" : "N") + "\n");
      fwrite("DEFAULT_OBMANN=" + defaultObmann + "\n");
      fwrite("POPUP_COMPLETE=" + (popupComplete ? "Y" : "N") + "\n");
      fwrite("CORRECT_MISSPELLED_MITGLIEDER=" + (correctMisspelledMitglieder ? "Y" : "N") + "\n");
      fwrite("CORRECT_MISSPELLED_BOOTE=" + (correctMisspelledBoote ? "Y" : "N") + "\n");
      fwrite("CORRECT_MISSPELLED_ZIELE=" + (correctMisspelledZiele ? "Y" : "N") + "\n");
      fwrite("SKIP_UHRZEIT=" + (skipUhrzeit ? "Y" : "N") + "\n");
      fwrite("SKIP_ZIEL=" + (skipZiel ? "Y" : "N") + "\n");
      fwrite("SKIP_MANNSCHKM=" + (skipMannschKm ? "Y" : "N") + "\n");
      fwrite("SKIP_BEMERK=" + (skipBemerk ? "Y" : "N") + "\n");
      fwrite("FENSTER_IMMER_ZENTRIERT=" + (fensterZentriert ? "Y" : "N") + "\n");
      fwrite("WINDOW_X_OFFSET=" + windowXOffset + "\n");
      fwrite("WINDOW_Y_OFFSET=" + windowYOffset + "\n");
      fwrite("SCREEN_WIDTH=" + screenWidth + "\n");
      fwrite("SCREEN_HEIGHT=" + screenHeight + "\n");
      fwrite("MAX_DIALOG_WIDTH=" + maxDialogWidth + "\n");
      fwrite("MAX_DIALOG_HEIGHT=" + maxDialogHeight + "\n");
      fwrite("LOOKANDFEEL=" + lookAndFeel + "\n");
      fwrite("SHOWBERLINOPTIONS=" + (showBerlinOptions ? "Y" : "N") + "\n");
      fwrite("ZIELFAHRT_SEPARATOR_BEREICHE=" + zielfahrtSeparatorBereiche + "\n");
      fwrite("ZIELFAHRT_SEPARATOR_FAHRTEN=" + zielfahrtSeparatorFahrten + "\n");
      fwrite("STANDARD_FAHRTART=" + standardFahrtart + "\n");
      fwrite("DEBUG_LOGGING=" + (debugLogging ? "Y" : "N") + "\n");

      fwrite("EFASTARTS=" + Integer.toString(countEfaStarts) + "\n");
      fwrite("EFAVERSIONLASTCHECK=" + efaVersionLastCheck + "\n");


      // ------------------- EFA DIREKT -------------------
      if (direkt_letzteDatei!=null) fwrite("DIREKTDATEI=" + direkt_letzteDatei + "\n");
      if (admins != null) {
        Object[] k = admins.keySet().toArray();
        if (k != null && k.length>0) {
          for (int i=0; i<k.length; i++) {
            Admin a = (Admin)admins.get(k[i]);
            if (a == null) continue; // sollte nie passieren...
            String s = k[i] + "|" + a.password + "|" + a.email + "|" +
                       ( a.allowedAdminsVerwalten ? "+" : "-" ) +
                       ( a.allowedEfaConfig ? "+" : "-" ) +
                       ( a.allowedFahrtenbuchAuswaehlen ? "+" : "-" ) +
                       ( a.allowedVollzugriff ? "+" : "-" ) +
                       ( a.allowedBootsstatusBearbeiten ? "+" : "-" ) +
                       ( a.allowedNachrichtenAnzeigenAdmin ? "+" : "-" ) +
                       ( a.allowedLogdateiAnzeigen ? "+" : "-" ) +
                       ( a.nachrichtenAdminGelesenMarkierenDefault ? "+" : "-" ) +
                       ( a.nachrichtenAdminAllowedGelesenMarkieren ? "+" : "-" ) +
                       ( a.allowedEfaBeenden ? "+" : "-" ) +
                       ( a.allowedNachrichtenAnzeigenBootswart ? "+" : "-" ) +
                       ( a.nachrichtenBootswartGelesenMarkierenDefault ? "+" : "-" ) +
                       ( a.nachrichtenBootswartAllowedGelesenMarkieren ? "+" : "-" ) +
                       ( a.allowedStatistikErstellen ? "+" : "-") +
                       ( a.allowedBootslisteBearbeiten ? "+" : "-") +
                       ( a.allowedMitgliederlisteBearbeiten ? "+" : "-") +
                       ( a.allowedZiellisteBearbeiten ? "+" : "-") +
                       ( a.allowedGruppenBearbeiten ? "+" : "-") +
                       ( a.allowedFahrtenbuchBearbeiten ? "+" : "-") +
                       ( a.allowedBootsreservierung ? "+" : "-") +
                       ( a.allowedPasswortAendern ? "+" : "-");
            fwrite("ADMIN="+s+"\n");
          }
        }
      }

      fwrite("ZIEL_BEI_FAHRTBEGINN=" + (efaDirekt_zielBeiFahrtbeginnPflicht ? "Y" : "N") + "\n");
      fwrite("ERZWINGE_OBMANN=" + (efaDirekt_eintragErzwingeObmann ? "Y" : "N") + "\n");
      fwrite("ERLAUBE_NUR_MAX_RUDERERZAHL=" + (efaDirekt_eintragErlaubeNurMaxRudererzahl ? "Y" : "N") + "\n");
      fwrite("FAHRT_BEGINNEN_BEENDEN_UHRZEIT_NICHT_AENDERBAR=" + (efaDirekt_eintragNichtAenderbarUhrzeit ? "Y" : "N") + "\n");
      fwrite("BEI_BEKANNTEN_ZIELEN_KM_NICHT_AENDERBAR=" + (efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen ? "Y" : "N") + "\n");
      fwrite("NUR_BEKANNTE_BOOTE=" + (efaDirekt_eintragNurBekannteBoote ? "Y" : "N") + "\n");
      fwrite("NUR_BEKANNTE_RUDERER=" + (efaDirekt_eintragNurBekannteRuderer ? "Y" : "N") + "\n");
      fwrite("NUR_BEKANNTE_ZIELE=" + (efaDirekt_eintragNurBekannteZiele ? "Y" : "N") + "\n");

      fwrite("ABFAHRT_PLUS_MINUTEN=" + efaDirekt_plusMinutenAbfahrt + "\n");
      fwrite("ANKUNFT_MINUS_MINUTEN=" + efaDirekt_minusMinutenAnkunft + "\n");
      fwrite("BOOTSRESERVIERUNGEN_MITGLIEDER1=" + (efaDirekt_mitgliederDuerfenReservieren ? "Y" : "N") + "\n");
      fwrite("BOOTSRESERVIERUNGEN_MITGLIEDER1_ZYKLISCH=" + (efaDirekt_mitgliederDuerfenReservierenZyklisch ? "Y" : "N") + "\n");
      fwrite("BOOTSRESERVIERUNGEN_MITGLIEDER=" + (efaDirekt_mitgliederDuerfenReservierungenEditieren ? "Y" : "N") + "\n");
      fwrite("EFA_BEENDEN_MITGLIEDER=" + (efaDirekt_mitgliederDuerfenEfaBeenden ? "Y" : "N") + "\n");
      fwrite("MITGLIEDER_NAMEN_HINZUFUEGEN=" + (efaDirekt_mitgliederDuerfenNamenHinzufuegen ? "Y" : "N") + "\n");
      fwrite("RES_BOOTE_NICHT_VERFUEGBAR=" + (efaDirekt_resBooteNichtVerfuegbar ? "Y" : "N") + "\n");
      fwrite("WAFA_REGATTA_BOOTE_NICHT_VERFUEGBAR=" + (efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar ? "Y" : "N") + "\n");
      fwrite("RES_LOOK_AHEAD_TIME=" + efaDirekt_resLookAheadTime + "\n");
      fwrite("EXEC_ON_EFA_EXIT=" + efaDirekt_execOnEfaExit + "\n");
      fwrite("EXIT_TIME=" + (efaDirekt_exitTime.tag<0 ? "" : EfaUtil.correctTime(efaDirekt_exitTime.tag+":"+efaDirekt_exitTime.monat)) + "\n");
      fwrite("EXEC_ON_EFA_AUTO_EXIT=" + efaDirekt_execOnEfaAutoExit + "\n");
      fwrite("RESTART_TIME=" + (efaDirekt_restartTime.tag<0 ? "" : EfaUtil.correctTime(efaDirekt_restartTime.tag+":"+efaDirekt_restartTime.monat)) + "\n");
      fwrite("DOPPELSTART_VERHINDERN=" + (efaDirekt_checkRunning ? "Y" : "N") + "\n");
      fwrite("BUTTON_FAHRT_BEGINNEN="+efaDirekt_butFahrtBeginnenFarbe+"|"+EfaUtil.removeSepFromString(efaDirekt_butFahrtBeginnenText) + "\n");
      fwrite("BUTTON_FAHRT_BEENDEN="+efaDirekt_butFahrtBeendenFarbe+"|"+EfaUtil.removeSepFromString(efaDirekt_butFahrtBeendenText) + "\n");
      fwrite("BUTTON_FAHRT_ABBRECHEN="+efaDirekt_butFahrtAbbrechenFarbe + "\n");
      fwrite("BUTTON_NACHTRAG="+efaDirekt_butNachtragFarbe + "\n");
      fwrite("BUTTON_BOOTSRESERVIERUNGEN="+efaDirekt_butBootsreservierungenFarbe+"|"+(efaDirekt_butBootsreservierungenAnzeigen ? "+" : "-") + "\n");
      fwrite("BUTTON_FAHRTENBUCH_ANZEIGEN="+efaDirekt_butFahrtenbuchAnzeigenFarbe+"|"+(efaDirekt_butFahrtenbuchAnzeigenAnzeigen ? "+" : "-") + "\n");
      fwrite("BUTTON_STATISTIK_ERSTELLEN="+efaDirekt_butStatistikErstellenFarbe+"|"+(efaDirekt_butStatistikErstellenAnzeigen ? "+" : "-") + "\n");
      fwrite("BUTTON_NACHRICHT_AN_ADMIN="+efaDirekt_butNachrichtAnAdminFarbe+"|"+(efaDirekt_butNachrichtAnAdminAnzeigen ? "+" : "-") + "\n");
      fwrite("BUTTON_ADMIN_MODUS="+efaDirekt_butAdminModusFarbe+"|"+(efaDirekt_butAdminModusAnzeigen ? "+" : "-") + "\n");
      fwrite("BUTTON_HOTKEYS_ANZEIGEN="+(efaDirekt_showButtonHotkey ? "+" : "-") + "\n");
      fwrite("BUTTON_SPEZIAL="+efaDirekt_butSpezialFarbe+"|"+(efaDirekt_butSpezialAnzeigen ? "+" : "-")+"|"+EfaUtil.removeSepFromString(efaDirekt_butSpezialText)+"|"+EfaUtil.removeSepFromString(efaDirekt_butSpezialCmd) + "\n");
      fwrite("UHR_ANZEIGEN="+(efaDirekt_showUhr ? "+" : "-") + "\n");
      fwrite("SUNRISESET_ANZEIGEN="+(efaDirekt_sunRiseSet_show ? "+" : "-") + "\n");
      fwrite("SUNRISESET_LL="+EfaUtil.arr2KommaList(efaDirekt_sunRiseSet_ll,0) + "\n");
      fwrite("BOOTE_SORTIEREN_NACH_ANZ_RUDERER="+(efaDirekt_sortByAnzahl ? "+" : "-") + "\n");
      fwrite("VEREINSLOGO="+efaDirekt_vereinsLogo + "\n");
      fwrite("NEWSTEXT="+efaDirekt_newsText + "\n");

      fwrite("STARTMAXIMIZED="+(efaDirekt_startMaximized ? "+" : "-") + "\n");
      fwrite("FENSTER_NICHT:VERSCHIEBBAR="+(efaDirekt_fensterNichtVerschiebbar ? "+" : "-") + "\n");
      fwrite("IMMER_IM_VORDERGRUND="+(efaDirekt_immerImVordergrund ? "+" : "-") + "\n");
      fwrite("IMMER_IM_VORDERGRUND_BRING_TO_FRONT="+(efaDirekt_immerImVordergrundBringToFront ? "+" : "-") + "\n");
      fwrite("SHOW_EINGABE_INFOS="+(efaDirekt_showEingabeInfos ? "+" : "-") + "\n");
      fwrite("SHOW_BOOTSSCHADEN_BUTTON="+(efaDirekt_showBootsschadenButton ? "+" : "-") + "\n");
      fwrite("SHOW_ZIELNAME_BOOTEUNTERWEGS="+(efaDirekt_showZielnameFuerBooteUnterwegs ? "+" : "-") + "\n");
      fwrite("FB_ANZEIGEN_MAX_FAHRTEN="+efaDirekt_maxFBAnzeigenFahrten+"\n");
      fwrite("FB_ANZEIGEN_ANZ_FAHRTEN="+efaDirekt_anzFBAnzeigenFahrten+"\n");
      fwrite("FB_ANZEIGEN_AUCH_UNVOLLSTAENDIGE_FAHRTEN="+(efaDirekt_FBAnzeigenAuchUnvollstaendige ? "+" : "-") + "\n");
      fwrite("AUTO_NEUES_FAHRTENBUCH_DATUM=" + (efaDirekt_autoNewFb_datum == null ? "" : efaDirekt_autoNewFb_datum.tag+"."+efaDirekt_autoNewFb_datum.monat+"."+efaDirekt_autoNewFb_datum.jahr) + "\n");
      fwrite("AUTO_NEUES_FAHRTENBUCH_DATEI=" + efaDirekt_autoNewFb_datei + "\n");
      fwrite("FONT_SIZE="+efaDirekt_fontSize + "\n");
      fwrite("FONT_STYLE="+efaDirekt_fontStyle + "\n");
      fwrite("COLORIZE_INPUT_FIELD=" + (efaDirekt_colorizeInputField ? "+" : "-") + "\n");

      fwrite("BENACHRICHTIGE_ERROR=" + (efaDirekt_bnrError_admin ? "+" : "-") + (efaDirekt_bnrError_bootswart ? "+" : "-") + "\n");
      fwrite("BENACHRICHTIGE_WARNING=" + (efaDirekt_bnrWarning_admin ? "+" : "-") + (efaDirekt_bnrWarning_bootswart ? "+" : "-") + "\n");
      fwrite("BENACHRICHTIGE_BOOTSSTATUS=" + (efaDirekt_bnrBootsstatus_admin ? "+" : "-") + (efaDirekt_bnrBootsstatus_bootswart ? "+" : "-") + "\n");
      fwrite("BENACHRICHTIGE_WARNING_LASTTIME=" + efaDirekt_bnrWarning_lasttime + "\n");

      fwrite("EMAIL_SERVER="+efaDirekt_emailServer + "\n");
      fwrite("EMAIL_ABSENDER="+efaDirekt_emailAbsender + "\n");
      fwrite("EMAIL_USERNAME="+efaDirekt_emailUsername + "\n");
      fwrite("EMAIL_PASSWORD="+efaDirekt_emailPassword + "\n");
      fwrite("EMAIL_ABSENDER_NAME="+efaDirekt_emailAbsenderName + "\n");
      fwrite("EMAIL_BETREFF_PRAEFIX="+efaDirekt_emailBetreffPraefix + "\n");
      fwrite("EMAIL_SIGNATUR="+efaDirekt_emailSignatur + "\n");

      fwrite("LOCK_EFA_SHOW_HTML="+efaDirekt_lockEfaShowHtml + "\n");
      fwrite("LOCK_EFA_VOLLBILD="+(efaDirekt_lockEfaVollbild ? "+" : "-")+ "\n");
      fwrite("LOCK_EFA_VON_DATUM="+(efaDirekt_lockEfaFromDatum != null ? efaDirekt_lockEfaFromDatum.tag+"."+efaDirekt_lockEfaFromDatum.monat+"."+efaDirekt_lockEfaFromDatum.jahr : "") + "\n");
      fwrite("LOCK_EFA_VON_ZEIT="+(efaDirekt_lockEfaFromZeit != null ? efaDirekt_lockEfaFromZeit.tag+":"+efaDirekt_lockEfaFromZeit.monat : "") + "\n");
      fwrite("LOCK_EFA_BIS_DATUM="+(efaDirekt_lockEfaUntilDatum != null ? efaDirekt_lockEfaUntilDatum.tag+"."+efaDirekt_lockEfaUntilDatum.monat+"."+efaDirekt_lockEfaUntilDatum.jahr : "") + "\n");
      fwrite("LOCK_EFA_BIS_ZEIT="+(efaDirekt_lockEfaUntilZeit != null ? efaDirekt_lockEfaUntilZeit.tag+":"+efaDirekt_lockEfaUntilZeit.monat : "") + "\n");
      fwrite("LOCK_EFA_LOCKED="+(efaDirekt_locked ? "+" : "-") + "\n");

    } catch(Exception e) {
      try {
        fcloseW();
      } catch(Exception ee) {
        return false;
      }
      return false;
    }
    return true;
  }




  private void addAdmin(String s) {
    if (s == null || s.length()==0 || admins == null) return;
    Vector v = EfaUtil.split(s,'|');
    if (v.size()<3 || v.size()>4) return; // altes Format (<170): 3 Felder; neues Format (>=170): 4 Felder
    String name = (String)v.get(0);
    String pwd = (String)v.get(1);
    if (name.length() == 0 || pwd.length() == 0) return;
    String email = "";
    if (v.size()==3) {
      s = (String)v.get(2);
    } else {
      email = (String)v.get(2);
      s = (String)v.get(3);
    }
    Admin a = new Admin(name,pwd);
    a.allowedAdminsVerwalten = EfaUtil.isOptionSet(s,0);
    a.allowedEfaConfig = EfaUtil.isOptionSet(s,1);
    a.allowedFahrtenbuchAuswaehlen = EfaUtil.isOptionSet(s,2);
    a.allowedVollzugriff = EfaUtil.isOptionSet(s,3);
    a.allowedBootsstatusBearbeiten = EfaUtil.isOptionSet(s,4);
    a.allowedNachrichtenAnzeigenAdmin = EfaUtil.isOptionSet(s,5);
    a.allowedLogdateiAnzeigen = EfaUtil.isOptionSet(s,6);
    a.nachrichtenAdminGelesenMarkierenDefault = EfaUtil.isOptionSet(s,7);
    a.nachrichtenAdminAllowedGelesenMarkieren = EfaUtil.isOptionSet(s,8);
    a.allowedEfaBeenden = EfaUtil.isOptionSet(s,9);
    a.allowedNachrichtenAnzeigenBootswart = EfaUtil.isOptionSet(s,10) ||
       (s.length()<11 && a.allowedNachrichtenAnzeigenAdmin); // weil dieses Feld neu ist: Falls
    a.nachrichtenBootswartGelesenMarkierenDefault = EfaUtil.isOptionSet(s,11);
    a.nachrichtenBootswartAllowedGelesenMarkieren = EfaUtil.isOptionSet(s,12);
    a.allowedStatistikErstellen = EfaUtil.isOptionSet(s,13) ||
       (s.length()<14 && a.allowedVollzugriff); // neu in v1.7.0
    a.allowedBootslisteBearbeiten = EfaUtil.isOptionSet(s,14) ||
       (s.length()<15 && a.allowedVollzugriff); // neu in v1.7.0
    a.allowedMitgliederlisteBearbeiten = EfaUtil.isOptionSet(s,15) ||
       (s.length()<16 && a.allowedVollzugriff); // neu in v1.7.0
    a.allowedZiellisteBearbeiten = EfaUtil.isOptionSet(s,16) ||
       (s.length()<17 && a.allowedVollzugriff); // neu in v1.7.0
    a.allowedGruppenBearbeiten = EfaUtil.isOptionSet(s,17) ||
       (s.length()<18 && a.allowedVollzugriff); // neu in v1.7.0
    a.allowedFahrtenbuchBearbeiten = EfaUtil.isOptionSet(s,18) ||
       (s.length()<19 && a.allowedVollzugriff); // neu in v1.7.1
    a.allowedBootsreservierung = EfaUtil.isOptionSet(s, 19) ||
       (s.length()<20 && a.allowedVollzugriff); // neu in v1.8.3
    a.allowedPasswortAendern = EfaUtil.isOptionSet(s, 20) ||
    (s.length()<21 && a.allowedVollzugriff); // neu in v1.8.3

    a.email = email;
    admins.put(name,a);
  }


  // Login eines Admin;
  // @return Admin, wenn erfolgreich eingeloggt oder null, wenn falsch
  public Admin login(String admin, String pwd) {
    if (admin == null || admin.length() == 0 || pwd == null || pwd.length() == 0) return null;
    if (admins == null || admins.get(admin) == null) return null;
    Admin a = (Admin)admins.get(admin);
    if (a.password != null && a.password.equals(EfaUtil.getSHA(pwd))) return a;
    return null;
  }


  // Daten eines gespeicherten Buttons ermitteln
  private String[] getButtonConfig(String s) {
    String[] a = new String[4];
    int pos;
    StringTokenizer tok = new StringTokenizer(s,"|");
    for (int i=0; i<4; i++) a[i] = "";
    for (int i=0; tok.hasMoreElements() && i<4; i++) {
      a[i] = tok.nextToken();
    }
    return a;
  }



}
