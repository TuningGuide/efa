/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.direkt.Admin;
import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;

// @i18n complete

public class EfaConfig extends DatenListe {

    // Parameter Types
    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXPERT = 1;
    public static final int TYPE_PUBLIC = 2;

    // Parameter Categories
    public static final char CATEGORY_SEPARATOR = ':';
    public static final String CATEGORY_INTERNAL      = "00INTERNAL";
    public static final String CATEGORY_COMMON        = "01COMMON";
    public static final String CATEGORY_INPUT         = "02INPUT";
    public static final String CATEGORY_BASE          = "03BASE";
    public static final String CATEGORY_BOATHOUSE     = "04BOATHOUSE";
    public static final String CATEGORY_GUI           = "05GUI";
    public static final String CATEGORY_GUIBUTTONS    = "06GUIBUTTONS";
    public static final String CATEGORY_BACKUP        = "07BACKUP";
    public static final String CATEGORY_EXTTOOLS      = "08EXTTOOLS";
    public static final String CATEGORY_PRINTING      = "09PRINTING";
    public static final String CATEGORY_STARTSTOP     = "10STARTSTOP";
    public static final String CATEGORY_PERMISSIONS   = "11PERMISSIONS";
    public static final String CATEGORY_NOTIFICATIONS = "12NOTIFICATIONS";
    public static final String CATEGORY_TYPES         = "13TYPES";
    public static final String CATEGORY_LOCALE        = "14LOCALE";

    // Default-Obmann für ungesteuerte Boote
    public static final String OBMANN_BOW = "BOW";
    public static final String OBMANN_STROKE = "STROKE";

    // Werte für FontType
    public static final String FONT_PLAIN = "PLAIN";
    public static final String FONT_BOLD = "BOLD";

    // some default values
    private static final String[] DEFAULT_BROWSER = {
        "/usr/bin/firefox",
        "/usr/bin/mozilla",
        "/usr/bin/netscape",
        "c:\\Programme\\Mozilla Firefox\\firefox.exe",
        "c:\\Programme\\Internet Explorer\\iexplore.exe",
        "c:\\Program Files\\Mozilla Firefox\\firefox.exe",
        "c:\\Program Files\\Internet Explorer\\iexplore.exe"
    };
    private static final String[] DEFAULT_ACROBAT = {
        "/usr/bin/acroread",
        "c:\\Programme\\Adobe\\Reader 9.0\\Reader\\AcroRd32.exe",
        "c:\\Program Files\\Adobe\\Reader 9.0\\Reader\\AcroRd32.exe"
    };

    private static final int STRINGLIST_VALUES  = 1;
    private static final int STRINGLIST_DISPLAY = 2;

    public static final String KENNUNG190 = "##EFA.190.CONFIGURATION##";

    // private internal data
    private HashMap<String,String> categories;
    private HashMap<String,ConfigValue> configValues;
    private Vector<String> configValueNames;
    
    // public configuration data
    public ConfigTypeString letzteDatei;
    public ConfigTypeBoolean autogenAlias;
    public ConfigTypeString aliasFormat;
    public ConfigTypeString bakDir;
    public ConfigTypeBoolean bakSave;
    public ConfigTypeBoolean bakMonat;
    public ConfigTypeBoolean bakTag;
    public ConfigTypeBoolean bakKonv;
    public ConfigTypeString browser;
    public ConfigTypeString acrobat;
    public ConfigTypeInteger printPageWidth;
    public ConfigTypeInteger printPageHeight;
    public ConfigTypeInteger printLeftMargin;
    public ConfigTypeInteger printTopMargin;
    public ConfigTypeInteger printPageOverlap;
    public ConfigTypeHashtable<String> keys;
    public ConfigTypeInteger countEfaStarts;
    public ConfigTypeString registeredProgramID;
    public ConfigTypeInteger registrationChecks;
    public ConfigTypeBoolean autoStandardmannsch;
    public ConfigTypeBoolean showObmann;
    public ConfigTypeBoolean autoObmann;
    public ConfigTypeStringList defaultObmann;
    public ConfigTypeBoolean popupComplete;
    public ConfigTypeBoolean correctMisspelledMitglieder;
    public ConfigTypeBoolean correctMisspelledBoote;
    public ConfigTypeBoolean correctMisspelledZiele;
    public ConfigTypeBoolean skipUhrzeit;
    public ConfigTypeBoolean skipZiel;
    public ConfigTypeBoolean skipMannschKm;
    public ConfigTypeBoolean skipBemerk;
    public ConfigTypeBoolean fensterZentriert;
    public ConfigTypeInteger windowXOffset;
    public ConfigTypeInteger windowYOffset;
    public ConfigTypeInteger screenWidth;
    public ConfigTypeInteger screenHeight;
    public ConfigTypeInteger maxDialogHeight;
    public ConfigTypeInteger maxDialogWidth;
    public ConfigTypeStringList lookAndFeel;
    public ConfigTypeString zielfahrtSeparatorBereiche;
    public ConfigTypeString zielfahrtSeparatorFahrten;
    public ConfigTypeStringList standardFahrtart;
    public ConfigTypeBoolean debugLogging;
    public ConfigTypeString efaVersionLastCheck;
    public ConfigTypeString version;
    public ConfigTypeString direkt_letzteDatei;
    public ConfigTypeHashtable<Admin> admins;
    public ConfigTypeBoolean efaDirekt_zielBeiFahrtbeginnPflicht;
    public ConfigTypeBoolean efaDirekt_eintragErzwingeObmann;
    public ConfigTypeBoolean efaDirekt_eintragErlaubeNurMaxRudererzahl;
    public ConfigTypeBoolean efaDirekt_eintragNichtAenderbarUhrzeit;
    public ConfigTypeBoolean efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen;
    public ConfigTypeBoolean efaDirekt_eintragNurBekannteBoote;
    public ConfigTypeBoolean efaDirekt_eintragNurBekannteRuderer;
    public ConfigTypeBoolean efaDirekt_eintragNurBekannteZiele;
    public ConfigTypeInteger efaDirekt_plusMinutenAbfahrt;
    public ConfigTypeInteger efaDirekt_minusMinutenAnkunft;
    public ConfigTypeBoolean efaDirekt_mitgliederDuerfenReservieren;
    public ConfigTypeBoolean efaDirekt_mitgliederDuerfenReservierenZyklisch;
    public ConfigTypeBoolean efaDirekt_mitgliederDuerfenReservierungenEditieren;
    public ConfigTypeBoolean efaDirekt_mitgliederDuerfenEfaBeenden;
    public ConfigTypeBoolean efaDirekt_mitgliederDuerfenNamenHinzufuegen;
    public ConfigTypeBoolean efaDirekt_resBooteNichtVerfuegbar;
    public ConfigTypeBoolean efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar;
    public ConfigTypeInteger efaDirekt_resLookAheadTime;
    public ConfigTypeString efaDirekt_execOnEfaExit;
    public ConfigTypeTime efaDirekt_exitTime;
    public ConfigTypeString efaDirekt_execOnEfaAutoExit;
    public ConfigTypeTime efaDirekt_restartTime;
    public ConfigTypeBoolean efaDirekt_checkRunning;
    public ConfigTypeButton efaDirekt_butFahrtBeginnen;
    public ConfigTypeButton efaDirekt_butFahrtBeenden;
    public ConfigTypeButton efaDirekt_butFahrtAbbrechen;
    public ConfigTypeButton efaDirekt_butNachtrag;
    public ConfigTypeButton efaDirekt_butBootsreservierungen;
    public ConfigTypeButton efaDirekt_butFahrtenbuchAnzeigen;
    public ConfigTypeButton efaDirekt_butStatistikErstellen;
    public ConfigTypeButton efaDirekt_butNachrichtAnAdmin;
    public ConfigTypeButton efaDirekt_butAdminModus;
    public ConfigTypeButton efaDirekt_butSpezial;
    public ConfigTypeString efaDirekt_butSpezialCmd;
    public ConfigTypeBoolean efaDirekt_showButtonHotkey;
    public ConfigTypeBoolean efaDirekt_showUhr;
    public ConfigTypeBoolean efaDirekt_sunRiseSet_show;
    public ConfigTypeLongLat efaDirekt_sunRiseSet_latitude;
    public ConfigTypeLongLat efaDirekt_sunRiseSet_longitude;
    public ConfigTypeBoolean efaDirekt_sortByAnzahl;
    public ConfigTypeBoolean efaDirekt_showEingabeInfos;
    public ConfigTypeBoolean efaDirekt_showBootsschadenButton;
    public ConfigTypeInteger efaDirekt_maxFBAnzeigenFahrten;
    public ConfigTypeInteger efaDirekt_anzFBAnzeigenFahrten;
    public ConfigTypeBoolean efaDirekt_FBAnzeigenAuchUnvollstaendige;
    public ConfigTypeDate efaDirekt_autoNewFb_datum;
    public ConfigTypeString efaDirekt_autoNewFb_datei;
    public ConfigTypeInteger efaDirekt_fontSize;
    public ConfigTypeStringList efaDirekt_fontStyle;
    public ConfigTypeBoolean efaDirekt_colorizeInputField;
    public ConfigTypeBoolean efaDirekt_showZielnameFuerBooteUnterwegs;
    public ConfigTypeString efadirekt_adminLastOsCommand;
    public ConfigTypeImage efaDirekt_vereinsLogo;
    public ConfigTypeString efaDirekt_newsText;
    public ConfigTypeBoolean efaDirekt_startMaximized;
    public ConfigTypeBoolean efaDirekt_fensterNichtVerschiebbar;
    public ConfigTypeBoolean efaDirekt_immerImVordergrund;
    public ConfigTypeBoolean efaDirekt_immerImVordergrundBringToFront;
    public ConfigTypeBoolean efaDirekt_bnrError_admin;
    public ConfigTypeBoolean efaDirekt_bnrError_bootswart;
    public ConfigTypeBoolean efaDirekt_bnrWarning_admin;
    public ConfigTypeBoolean efaDirekt_bnrWarning_bootswart;
    public ConfigTypeBoolean efaDirekt_bnrBootsstatus_admin;
    public ConfigTypeBoolean efaDirekt_bnrBootsstatus_bootswart;
    public ConfigTypeLong efaDirekt_bnrWarning_lasttime;
    public ConfigTypeString efaDirekt_emailServer;
    public ConfigTypeString efaDirekt_emailAbsender;
    public ConfigTypeString efaDirekt_emailUsername;
    public ConfigTypeString efaDirekt_emailPassword;
    public ConfigTypeString efaDirekt_emailAbsenderName;
    public ConfigTypeString efaDirekt_emailBetreffPraefix;
    public ConfigTypeString efaDirekt_emailSignatur;
    public ConfigTypeString efaDirekt_lockEfaShowHtml;
    public ConfigTypeBoolean efaDirekt_lockEfaVollbild;
    public ConfigTypeDate efaDirekt_lockEfaFromDatum;
    public ConfigTypeTime efaDirekt_lockEfaFromZeit;
    public ConfigTypeDate efaDirekt_lockEfaUntilDatum;
    public ConfigTypeTime efaDirekt_lockEfaUntilZeit;
    public ConfigTypeBoolean efaDirekt_locked;
    public ConfigTypeBoolean showGermanOptions;
    public ConfigTypeBoolean showBerlinOptions;
    public ConfigTypeStringList language;
    public ConfigTypeHashtable<String> typesGender;
    public ConfigTypeHashtable<String> typesBoat;
    public ConfigTypeHashtable<String> typesNumSeats;
    public ConfigTypeHashtable<String> typesRigging;
    public ConfigTypeHashtable<String> typesCoxing;
    public ConfigTypeHashtable<String> typesSession;
    public ConfigTypeHashtable<String> typesStatus;

    // Default Contructor
    public EfaConfig(String filename) {
        super(filename, 0, 0, false);
        kennung = KENNUNG190;
        Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFACONFIG, "EfaConfig("+filename+")");
        initialize();
    }

    // Copy Constructor
    public EfaConfig(EfaConfig efaConfig) {
        super(efaConfig.getFileName(), 0, 0, false);
        kennung = efaConfig.kennung;
        initialize();
        String[] pnames = efaConfig.getParameterNames();
        for (int i=0; i<pnames.length; i++) {
            ConfigValue configValue = getParameter(pnames[i]);
            configValue.parseValue(efaConfig.getParameter(pnames[i]).toString());
        }
    }

    // clean-up and re-initialize data structures
    private void initialize() {
        categories   = new HashMap<String,String>();
        configValues = new HashMap<String,ConfigValue>();
        configValueNames = new Vector<String>();
        iniCategories();
        iniParameters();
    }

    // initializa all category strings
    private void iniCategories() {
        categories.put(CATEGORY_INTERNAL,      International.getString("intern"));
        categories.put(CATEGORY_BASE,          International.getString("efa-Basis"));
        categories.put(CATEGORY_BOATHOUSE,     International.getString("efa-Bootshaus"));
        categories.put(CATEGORY_GUI,           International.getString("Erscheinungsbild"));
        categories.put(CATEGORY_GUIBUTTONS,    International.getString("Buttons"));
        categories.put(CATEGORY_COMMON,        International.getString("Allgemein"));
        categories.put(CATEGORY_BACKUP,        International.getString("Backup"));
        categories.put(CATEGORY_EXTTOOLS,      International.getString("externe Programme"));
        categories.put(CATEGORY_PRINTING,      International.getString("Drucken"));
        categories.put(CATEGORY_INPUT,         International.getString("Eingabe"));
        categories.put(CATEGORY_LOCALE,        International.getString("Regionale Anpassung"));
        categories.put(CATEGORY_STARTSTOP,     International.getString("Starten und Beenden"));
        categories.put(CATEGORY_PERMISSIONS,   International.getString("Berechtigungen"));
        categories.put(CATEGORY_NOTIFICATIONS, International.getString("Benachrichtigungen"));
        categories.put(CATEGORY_TYPES,         International.getString("Bezeichnungen"));
    }

    // initialize all configuration parameters with their default values
    private void iniParameters() {

        // ============================= INTERNAL =============================
        addParameter(version = new ConfigTypeString("EFA_VERSION", "100",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa version"));
        addParameter(efaVersionLastCheck = new ConfigTypeString("EFA_VERSION_LASTCHECK", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa last checked for new version"));
        addParameter(countEfaStarts = new ConfigTypeInteger("EFA_STARTS_COUNTER", 0, 0, Integer.MAX_VALUE,
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa start counter"));
        addParameter(registeredProgramID = new ConfigTypeString("EFA_REGISTRATION_PROGRAMMID", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa registered programm ID"));
        addParameter(registrationChecks = new ConfigTypeInteger("EFA_REGISTRATION_CHECKS", 0, 0, Integer.MAX_VALUE,
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa registration checks counter"));

        // ============================= COMMON:COMMON =============================
        addParameter(letzteDatei = new ConfigTypeFile("LASTFILE_EFABASE", "",
                International.getString("Fahrtenbuch"),
                International.getString("Fahrtenbuch")+" (*.efb)",
                "efb",ConfigTypeFile.MODE_OPEN,ConfigTypeFile.TYPE_FILE,
                TYPE_INTERNAL, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                "Last logbook opened by efa Base"));
        addParameter(aliasFormat = new ConfigTypeString("ALIAS_FORMAT", "{V1}{V2}-{N1}",
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                International.getString("Format der Eingabe-Kürzel")));
        addParameter(autogenAlias = new ConfigTypeBoolean("ALIAS_AUTOGENERATE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                International.getString("Eingabe-Kürzel automatisch beim Anlegen neuer Mitglieder generieren")));
        addParameter(debugLogging = new ConfigTypeBoolean("DEBUG_LOGGING", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON),
                International.getString("Debug-Logging aktivieren")));

        // ============================= COMMON:INPUT =============================
        addParameter(standardFahrtart = new ConfigTypeStringList("SESSIONTYPE_DEFAULT", EfaTypes.TYPE_SESSION_NORMAL,
                makeSessionTypeArray(STRINGLIST_VALUES), makeSessionTypeArray(STRINGLIST_DISPLAY),
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standard-Fahrtart")));
        addParameter(defaultObmann = new ConfigTypeStringList("CREWSHEAD_DEFAULT", OBMANN_BOW,
                makeObmannArray(STRINGLIST_VALUES), makeObmannArray(STRINGLIST_DISPLAY),
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standard-Obmann für ungesteuerte Boote")));
        addParameter(showObmann = new ConfigTypeBoolean("CREWSHEAD_SHOW", true,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Obmann-Auswahlliste anzeigen")));
        addParameter(autoObmann = new ConfigTypeBoolean("CREWSHEAD_AUTOSELECT", true,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Obmann bei Eingabe automatisch auswählen")));
        addParameter(autoStandardmannsch = new ConfigTypeBoolean("DEFAULTCREW_AUTOSELECT", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standardmannschaft automatisch eintragen")));
        addParameter(correctMisspelledMitglieder = new ConfigTypeBoolean("SPELLING_CHECKMEMBERS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Mitglieder"))));
        addParameter(correctMisspelledBoote = new ConfigTypeBoolean("SPELLING_CHECKBOATS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Boote"))));
        addParameter(correctMisspelledZiele = new ConfigTypeBoolean("SPELLING_CHECKDESTINATIONS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Ziele"))));
        addParameter(skipUhrzeit = new ConfigTypeBoolean("INPUT_SKIPTIME", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Uhrzeit"))));
        addParameter(skipZiel = new ConfigTypeBoolean("INPUT_SKIPDESTINATION", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Ziel"))));
        addParameter(skipMannschKm = new ConfigTypeBoolean("INPUT_SKIPCREWKM", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Mannschafts-Km"))));
        addParameter(skipBemerk = new ConfigTypeBoolean("INPUT_SKIPCOMMENTS", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Bemerkungen"))));
        addParameter(keys = new ConfigTypeHashtable<String>("HOTKEYS", "",
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Tastenbelegungen für Bemerkungs-Feld")));
        addParameter(zielfahrtSeparatorBereiche = new ConfigTypeString("ZIELFAHRT_SEPARATORBEREICHE", ",",
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.onlyFor("Trennzeichen für Bereiche einer Zielfahrt","de")));
        addParameter(zielfahrtSeparatorFahrten = new ConfigTypeString("ZIELFAHRT_SEPARATORFAHRTEN", ";", // was "/" before
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.onlyFor("Trennzeichen für Zielfahrten","de")));

        // ============================= COMMON:GUI =============================
        addParameter(lookAndFeel = new ConfigTypeStringList("LOOK_AND_FEEL", getDefaultLookAndFeel(),
                makeLookAndFeelArray(STRINGLIST_VALUES), makeLookAndFeelArray(STRINGLIST_DISPLAY),
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Look & Feel")));
        addParameter(popupComplete = new ConfigTypeBoolean("AUTOCOMPLETEPOPUP_SHOW", true,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Beim Vervollständigen Popup-Liste anzeigen")));
        addParameter(fensterZentriert = new ConfigTypeBoolean("WINDOW_CENTERED", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Alle Fenster in Bildschirmmitte zentrieren")));
        addParameter(windowXOffset = new ConfigTypeInteger("WINDOW_OFFSETX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Fenster-Offset")+ " X" +
                " (" + International.getString("Pixel") + ")"));
        addParameter(windowYOffset = new ConfigTypeInteger("WINDOW_OFFSETY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Fenster-Offset")+ " Y" +
                " (" + International.getString("Pixel") + ")"));
        addParameter(screenWidth = new ConfigTypeInteger("SCREEN_WIDTH", 0, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Bildschirmbreite") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(screenHeight = new ConfigTypeInteger("SCREEN_HEIGHT", 0, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Bildschirmhöhe") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(maxDialogHeight = new ConfigTypeInteger("DIALOG_MAXHEIGHT", 0, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("maximale Dialog-Höhe") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(maxDialogWidth = new ConfigTypeInteger("DIALOG_MAXWIDTH", 0, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("maximale Dialog-Breite") +
                " (" + International.getString("Pixel") + ")"));

        // ============================= COMMON:BACKUP =============================
        addParameter(bakDir = new ConfigTypeFile("BACKUP_DIRECTORY", "",
                International.getString("Backup-Verzeichnis"),
                International.getString("Verzeichnis"),
                null,ConfigTypeFile.MODE_OPEN,ConfigTypeFile.TYPE_DIR,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup-Verzeichnis")));
        addParameter(bakSave = new ConfigTypeBoolean("BACKUP_ON_SAVE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup bei jedem Speichern")));
        addParameter(bakMonat = new ConfigTypeBoolean("BACKUP_ON_MONTHLY_SAVE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim ersten Speichern jeden Monat")));
        addParameter(bakTag = new ConfigTypeBoolean("BACKUP_ON_DAYLY_SAVE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim ersten Speichern jeden Tag")));
        addParameter(bakKonv = new ConfigTypeBoolean("BACKUP_ON_CONVERTING", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim Konvertieren")));

        // ============================= COMMON:EXTTOOLS =============================
        addParameter(browser = new ConfigTypeFile("WEBBROWSER", searchForProgram(DEFAULT_BROWSER),
                International.getString("Webbrowser"),
                International.getString("Windows-Programme")+" (*.exe)",
                "exe",ConfigTypeFile.MODE_OPEN,ConfigTypeFile.TYPE_FILE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_EXTTOOLS),
                International.getString("Webbrowser")));
        addParameter(acrobat = new ConfigTypeFile("ACROBAT_READER", searchForProgram(DEFAULT_ACROBAT),
                International.getString("Acrobat Reader"),
                International.getString("Windows-Programme")+" (*.exe)",
                "exe",ConfigTypeFile.MODE_OPEN,ConfigTypeFile.TYPE_FILE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_EXTTOOLS),
                International.getString("Acrobat Reader")));

        // ============================= COMMON:PRINT =============================
        addParameter(printPageWidth = new ConfigTypeInteger("PRINT_PAGEWIDTH", 210, 1, Integer.MAX_VALUE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenbreite")));
        addParameter(printPageHeight = new ConfigTypeInteger("PRINT_PAGEHEIGHT", 297, 1, Integer.MAX_VALUE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenhöhe")));
        addParameter(printLeftMargin = new ConfigTypeInteger("PRINT_PAGEMARGIN_LEFTRIGHT", 15, 0, Integer.MAX_VALUE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("linker und rechter Rand")));
        addParameter(printTopMargin = new ConfigTypeInteger("PRINT_PAGEMARGIN_TOPBOTTOM", 15, 0, Integer.MAX_VALUE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("oberer und unterer Rand")));
        addParameter(printPageOverlap = new ConfigTypeInteger("PRINT_PAGEOVERLAP", 5, 0, Integer.MAX_VALUE,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenüberlappung")));

        // ============================= BOATHOUSE:COMMON =============================
        addParameter(direkt_letzteDatei = new ConfigTypeFile("LASTFILE_EFABOATHOUSE", "",
                International.getString("Fahrtenbuch"),
                International.getString("Fahrtenbuch")+" (*.efb)",
                "efb",ConfigTypeFile.MODE_OPEN,ConfigTypeFile.TYPE_FILE,
                TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                "Last logbook opened by efa Boathouse"));
        addParameter(admins = new ConfigTypeHashtable<Admin>("ADMINS", new Admin("name","password"),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Administratoren")));
        addParameter(efaDirekt_resBooteNichtVerfuegbar = new ConfigTypeBoolean("SHOWASNOTAVAILABLE_RESERVEDBOATS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Reservierte Boote als 'nicht verfügbar' anzeigen")));
        addParameter(efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar = new ConfigTypeBoolean("SHOWASNOTAVAILABLE_MULTIDAY_REGATTA", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Boote auf Regatta, Trainingslager oder Mehrtagesfahrt als 'nicht verfügbar' anzeigen")));
        addParameter(efaDirekt_resLookAheadTime = new ConfigTypeInteger("RESERVATION_LOOKAHEADTIME", 120, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Bei Fahrtbeginn auf Reservierungen bis zu x Minuten in der Zukunft prüfen")));
        addParameter(efaDirekt_showBootsschadenButton = new ConfigTypeBoolean("BOATDAMAGE_ENABLEREPORTING", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Melden von Bootsschäden erlauben")));
        addParameter(efaDirekt_lockEfaShowHtml = new ConfigTypeString("LOCKEFA_PAGE", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("HTML-Seite anzeigen")));
        addParameter(efaDirekt_lockEfaVollbild = new ConfigTypeBoolean("LOCKEFA_FULLSCREEN", false,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Vollbild")));
        addParameter(efaDirekt_lockEfaFromDatum = new ConfigTypeDate("LOCKEFA_FROMDATE", new TMJ(-1,-1,-1),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beginnen") + " ("+
                International.getString("Datum")+")"));
        addParameter(efaDirekt_lockEfaFromZeit = new ConfigTypeTime("LOCKEFA_FROMTIME", new TMJ(-1,-1,-1),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beginnen") + " ("+
                International.getString("Zeit")+")"));
        addParameter(efaDirekt_lockEfaUntilDatum = new ConfigTypeDate("LOCKEFA_TODATE", new TMJ(-1,-1,-1),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beenden") + " ("+
                International.getString("Datum")+")"));
        addParameter(efaDirekt_lockEfaUntilZeit = new ConfigTypeTime("LOCKEFA_TOTIME", new TMJ(-1,-1,-1),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beenden") + " ("+
                International.getString("Zeit")+")"));
        addParameter(efaDirekt_locked = new ConfigTypeBoolean("LOCKEFA_LOCKED", false,
                TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa ist für die Benutzung gesperrt")));
        addParameter(efaDirekt_autoNewFb_datum = new ConfigTypeDate("NEWLOGBOOK_DATE", new TMJ(-1,-1,-1),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Neues Fahrtenbuch erstellen am")));
        addParameter(efaDirekt_autoNewFb_datei = new ConfigTypeFile("NEWLOGBOOK_FILE", "",
                International.getString("Fahrtenbuch"),
                International.getString("Fahrtenbuch")+" (*.efb)",
                "efb",ConfigTypeFile.MODE_SAVE,ConfigTypeFile.TYPE_FILE,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Dateiname des neuen Fahrtenbuchs")));
        addParameter(efadirekt_adminLastOsCommand = new ConfigTypeString("ADMIN_LAST_OSCOMMAND", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Betriebssystemkommando")));

        // ============================= BOATHOUSE:INPUT =============================
        addParameter(efaDirekt_eintragNurBekannteBoote = new ConfigTypeBoolean("ONLY_KNOWNBOATS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Boote"))));
        addParameter(efaDirekt_eintragNurBekannteRuderer = new ConfigTypeBoolean("ONLY_KNOWNPERSONS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Personen"))));
        addParameter(efaDirekt_eintragNurBekannteZiele = new ConfigTypeBoolean("ONLY_KNOWNDESTINATIONS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Ziele"))));
        addParameter(efaDirekt_eintragErlaubeNurMaxRudererzahl = new ConfigTypeBoolean("ALLOW_ONLY_MAXCREWNUMBER", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Nur für das Boot maximal mögliche Anzahl an Ruderern erlauben")));
        addParameter(efaDirekt_eintragErzwingeObmann = new ConfigTypeBoolean("MUST_SELECT_CREWSHEAD", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Obmann muß ausgewählt werden")));
        addParameter(efaDirekt_eintragNichtAenderbarUhrzeit = new ConfigTypeBoolean("NOTEDITABLE_TIME", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Vorgeschlagene Uhrzeiten können nicht geändert werden")));
        addParameter(efaDirekt_plusMinutenAbfahrt = new ConfigTypeInteger("TIME_SESSIONSTART_ADD", 10, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Für Abfahrt x Minuten zur aktuellen Zeit hinzuaddieren")));
        addParameter(efaDirekt_minusMinutenAnkunft = new ConfigTypeInteger("TIME_SESSIONEND_SUBSTRACT", 10, 0, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Für Ankunft x Minuten von aktueller Zeit abziehen")));
        addParameter(efaDirekt_zielBeiFahrtbeginnPflicht = new ConfigTypeBoolean("MUST_DESTINATION_AT_SESSIONSTART", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Ziel muß bereits bei Fahrtbeginn angegeben werden")));
        addParameter(efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen = new ConfigTypeBoolean("NOTEDITABLE_KMFORKNOWNDESTINATIONS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Vorgeschlagene Kilometer bei bekannten Zielen können nicht geändert werden")));

        // ============================= BOATHOUSE:GUI =============================
        addParameter(efaDirekt_startMaximized = new ConfigTypeBoolean("WINDOW_MAXIMIZED", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("efa maximiert starten")));
        addParameter(efaDirekt_fensterNichtVerschiebbar = new ConfigTypeBoolean("WINDOW_FIXED", false,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Hauptfenster nicht verschiebbar")));
        addParameter(efaDirekt_immerImVordergrund = new ConfigTypeBoolean("WINDOW_ALWAYSONTOP", false,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("efa immer im Vordergrund")));
        addParameter(efaDirekt_immerImVordergrundBringToFront = new ConfigTypeBoolean("WINDOW_ALWAYSONTOP_BRINGTOFRONT", false,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("efa immer im Vordergrund") + " (bringToFront)"));
        addParameter(efaDirekt_fontSize = new ConfigTypeInteger("FONT_SIZE", 12, 6, 32,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Schriftgröße in Punkten (6 bis 32, Standard: 12)")));
        addParameter(efaDirekt_fontStyle = new ConfigTypeStringList("FONT_STYLE", "",
                makeFontStyleArray(STRINGLIST_VALUES), makeFontStyleArray(STRINGLIST_DISPLAY),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Schriftstil")));
        addParameter(efaDirekt_colorizeInputField = new ConfigTypeBoolean("COLORIZE_INPUTFIELDS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("aktuelles Eingabefeld farblich hervorheben")));
        addParameter(efaDirekt_showEingabeInfos = new ConfigTypeBoolean("SHOW_INPUTHINTS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Eingabehinweise anzeigen")));
        addParameter(efaDirekt_showZielnameFuerBooteUnterwegs = new ConfigTypeBoolean("DISPLAY_DESTINATION_FORBOATSONWATER", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Fahrtziel in der Liste {list} anzeigen",
                International.getString("Boote auf Fahrt"))));
        addParameter(efaDirekt_sortByAnzahl = new ConfigTypeBoolean("BOATLIST_SORTBYSEATS", true,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("sortiere Boote nach Anzahl der Ruderplätze")));
        addParameter(efaDirekt_vereinsLogo = new ConfigTypeImage("CLUBLOGO", "", 192, 64,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Vereinslogo")));
        addParameter(efaDirekt_showUhr = new ConfigTypeBoolean("SHOW_TIME", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Uhr anzeigen")));
        addParameter(efaDirekt_sunRiseSet_show = new ConfigTypeBoolean("SUNRISESET_SHOW", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Sonnenaufgangs- und -untergangszeit anzeigen")));
        addParameter(efaDirekt_sunRiseSet_latitude = new ConfigTypeLongLat("SUNRISESET_LATITUDE",
                ConfigTypeLongLat.ORIENTATION_NORTH,52,25,9,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("geographische Breite")));
        addParameter(efaDirekt_sunRiseSet_longitude = new ConfigTypeLongLat("SUNRISESET_LONGITUDE",
                ConfigTypeLongLat.ORIENTATION_EAST,13,10,15,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("geographische Länge")));
        addParameter(efaDirekt_newsText = new ConfigTypeString("NEWS_TEXT", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("News-Text")));
        addParameter(efaDirekt_maxFBAnzeigenFahrten = new ConfigTypeInteger("LOGBOOK_DISPLAYEDENTRIES_MAXNUMBER", 100, 1, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Fahrtenbuch anzeigen")+": "+International.getString("maximale Anzahl von Fahrten")));
        addParameter(efaDirekt_anzFBAnzeigenFahrten = new ConfigTypeInteger("LOGBOOK_DISPLAYEDENTRIES_DEFAULTNUMBER", 50, 1, Integer.MAX_VALUE,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Fahrtenbuch anzeigen")+": "+International.getString("Anzahl von Fahrten")));
        addParameter(efaDirekt_FBAnzeigenAuchUnvollstaendige = new ConfigTypeBoolean("LOGBOOK_DISPLAYEDENTRIES_DEFAULTALSOINCOMPLETE", false,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Fahrtenbuch anzeigen")+": "+International.getString("auch unvollständige Fahrten")));

        // ============================= BOATHOUSE:GUIBUTTONS =============================
        addParameter(efaDirekt_butFahrtBeginnen = new ConfigTypeButton("BUTTON_STARTSESSION",
                International.getString("Fahrt beginnen")+" >>>", "CCFFCC", true, true, true, false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrt beginnen"))));
        addParameter(efaDirekt_butFahrtBeenden = new ConfigTypeButton("BUTTON_FINISHSESSION",
                "<<< "+International.getString("Fahrt beenden"), "CCFFCC", true, true, true, false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrt beenden"))));
        addParameter(efaDirekt_butFahrtAbbrechen = new ConfigTypeButton("BUTTON_CANCELSESSION",
                International.getString("Fahrt abbrechen"), "FFCCCC", true, false, true, false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrt abbrechen"))));
        addParameter(efaDirekt_butNachtrag = new ConfigTypeButton("BUTTON_LATEENTRY",
                International.getString("Nachtrag"), "CCFFFF", true, false, true, false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Nachtrag"))));
        addParameter(efaDirekt_butBootsreservierungen = new ConfigTypeButton("BUTTON_BOATRESERVATIONS",
                International.getString("Bootsreservierungen"), "FFFFCC", true, false, true, true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Bootsreservierungen"))));
        addParameter(efaDirekt_butFahrtenbuchAnzeigen = new ConfigTypeButton("BUTTON_SHOWLOGBOOK",
                International.getString("Fahrtenbuch anzeigen"), "CCCCFF", true, false, true, true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrtenbuch anzeigen"))));
        addParameter(efaDirekt_butStatistikErstellen = new ConfigTypeButton("BUTTON_CREATESTATISTICS",
                International.getString("Statistiken erstellen"), "CCCCFF", true, false, true, true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Statistiken erstellen"))));
        addParameter(efaDirekt_butNachrichtAnAdmin = new ConfigTypeButton("BUTTON_MESSAGETOADMIN",
                International.getString("Nachricht an Admin"), "FFF197", true, false, true, true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Nachricht an Admin"))));
        addParameter(efaDirekt_butAdminModus = new ConfigTypeButton("BUTTON_ADMINMODE",
                International.getString("Admin-Modus"), "CCCCCC", true, false, true, true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Admin-Modus"))));
        addParameter(efaDirekt_butSpezial = new ConfigTypeButton("BUTTON_SPECIAL",
                International.getString("Spezial-Button"), "CCCCCC", true, true, true, true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_butSpezialCmd = new ConfigTypeString("BUTTON_SPECIAL_COMMAND", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Auszuführendes Kommando für '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_showButtonHotkey = new ConfigTypeBoolean("BUTTON_SHOWHOTKEYS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getString("Hotkeys für Buttons anzeigen")));

        // ============================= BOATHOUSE:STARTSTOP =============================
        addParameter(efaDirekt_restartTime = new ConfigTypeTime("EFAEXIT_RESTARTTIME", new TMJ(4,0,0),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Uhrzeit zum automatischen Neustart von efa")));
        addParameter(efaDirekt_exitTime = new ConfigTypeTime("EFAEXIT_TIME", new TMJ(-1,-1,-1),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Uhrzeit zum automatischen Beenden von efa")));
        addParameter(efaDirekt_execOnEfaAutoExit = new ConfigTypeString("EFAEXIT_EXECONAUTOEXIT", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Folgendes Kommando beim automatischen Beenden von efa ausführen")));
        addParameter(efaDirekt_execOnEfaExit = new ConfigTypeString("EFAEXIT_EXEC", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Folgendes Kommando beim Beenden von efa durch Mitglieder ausführen")));

        // ============================= BOATHOUSE:PERMISSIONS =============================
        addParameter(efaDirekt_mitgliederDuerfenReservieren = new ConfigTypeBoolean("ALLOW_MEMBERS_BOATRESERVATION_ONCE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Boote reservieren")));
        addParameter(efaDirekt_mitgliederDuerfenReservierenZyklisch = new ConfigTypeBoolean("ALLOW_MEMBERS_BOATRESERVATION_REOCCURRING", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Boote reservieren") +
                " (" + International.getString("einmalige Reservierungen") + ")"));
        addParameter(efaDirekt_mitgliederDuerfenReservierungenEditieren = new ConfigTypeBoolean("ALLOW_MEMBERS_BOATRESERVATION_CHANGE", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Bootsreservierungen verändern und löschen")));
        addParameter(efaDirekt_mitgliederDuerfenNamenHinzufuegen = new ConfigTypeBoolean("ALLOW_MEMBERS_ADDNAMES", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Namen zur Mitgliederliste hinzufügen")));
        addParameter(efaDirekt_mitgliederDuerfenEfaBeenden = new ConfigTypeBoolean("ALLOW_MEMBERS_EXITEFA", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen efa beenden")));

        // ============================= BOATHOUSE:NOTIFICATIONS =============================
        addParameter(efaDirekt_bnrError_admin = new ConfigTypeBoolean("NOTIFICATION_ERROR_ADMIN", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Admins"),International.getString("bei Fehlern") + " (ERROR)")));
        addParameter(efaDirekt_bnrWarning_admin = new ConfigTypeBoolean("NOTIFICATION_WARNING_ADMIN", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Admins"),International.getString("bei Warnungen (WARNING) einmal pro Woche"))));
        addParameter(efaDirekt_bnrBootsstatus_admin = new ConfigTypeBoolean("NOTIFICATION_BOATSTATUS_ADMIN", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Admins"),International.getString("bei Bootsstatus-Änderungen"))));
        addParameter(efaDirekt_bnrError_bootswart = new ConfigTypeBoolean("NOTIFICATION_ERROR_BOATMAINTENANCE", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Bootswarte"),International.getString("bei Fehlern") + " (ERROR)")));
        addParameter(efaDirekt_bnrWarning_bootswart = new ConfigTypeBoolean("NOTIFICATION_WARNING_BOATMAINTENANCE", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Bootswarte"),International.getString("bei Warnungen (WARNING) einmal pro Woche"))));
        addParameter(efaDirekt_bnrBootsstatus_bootswart = new ConfigTypeBoolean("NOTIFICATION_BOATSTATUS_BOATMAINTENANCE", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Bootswarte"),International.getString("bei Bootsstatus-Änderungen"))));
        addParameter(efaDirekt_bnrWarning_lasttime = new ConfigTypeLong("NOTIFICATION_LASTWARNINGS", System.currentTimeMillis() - 7l*24l*60l*60l*1000l, 0, Long.MAX_VALUE, // one week ago
                TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("letzte Benachrichtigungen")));
        addParameter(efaDirekt_emailServer = new ConfigTypeString("EMAIL_SERVER", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("SMTP-Server")));
        addParameter(efaDirekt_emailUsername = new ConfigTypeString("EMAIL_USERNAME", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Username")));
        addParameter(efaDirekt_emailPassword = new ConfigTypeString("EMAIL_PASSWORD", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Paßwort")));
        addParameter(efaDirekt_emailAbsenderName = new ConfigTypeString("EMAIL_FROMNAME", Daten.EFA_SHORTNAME,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Absender-Name")));
        addParameter(efaDirekt_emailAbsender = new ConfigTypeString("EMAIL_FROMEMAIL", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Absender-Adresse")));
        addParameter(efaDirekt_emailBetreffPraefix = new ConfigTypeString("EMAIL_SUBJECTPREFIX", Daten.EFA_SHORTNAME,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Betreff (Präfix)")));
        addParameter(efaDirekt_emailSignatur = new ConfigTypeString("EMAIL_SIGNATURE", International.getString("Diese Nachricht wurde von efa verschickt."),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Signatur")));

        // ============================= LOCALE =============================
        addParameter(language = new ConfigTypeStringList("_LANGUAGE", Daten.efaBaseConfig.language,
                makeLanguageArray(STRINGLIST_VALUES), makeLanguageArray(STRINGLIST_DISPLAY),
                TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getString("Sprache")));
        addParameter(showGermanOptions = new ConfigTypeBoolean("REGIONAL_GERMANY", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Regionale Funktionalitäten aktivieren für {region}.",
                International.getString("Deutschland"))));
        addParameter(showBerlinOptions = new ConfigTypeBoolean("REGIONAL_BERLIN", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Regionale Funktionalitäten aktivieren für {region}.",
                International.getString("Berlin"))));

        // ============================= TYPES =============================
        buildTypes();

    }

    public static String makeCategory(String c1) {
        return c1;
    }
    public static String makeCategory(String c1, String c2) {
        return c1 + ":" + c2;
    }
    public static String makeCategory(String c1, String c2, String c3) {
        return c1 + ":" + c2 + ":" + c3;
    }


    private void addParameter(ConfigValue configValue) {
        if (configValues.get(configValue.getName()) != null) {
            // should never happen (program error); no need to translate
            Logger.log(Logger.ERROR, Logger.MSG_ERROR_EXCEPTION, "EfaConfig: duplicate parameter: "+configValue.getName());
        } else {
            configValues.put(configValue.getName(), configValue);
            configValueNames.add(configValue.getName());
        }
    }

    public ConfigValue getParameter(String name) {
        return configValues.get(name);
    }

    public String[] getParameterNames() {
        String[] names = new String[configValueNames.size()];
        for (int i=0; i<names.length; i++) {
            names[i] = configValueNames.get(i);
        }
        return names;
    }

    public String getCategoryName(String key) {
        return categories.get(key);
    }

    public static String[] getCategoryKeyArray(String keystring) {
        Vector v = EfaUtil.split(keystring, CATEGORY_SEPARATOR);
        String[] a = new String[v.size()];
        for (int i=0; i<v.size(); i++) {
            a[i] = (String)v.get(i);
        }
        return a;
    }


    public synchronized boolean readEinstellungen() {
        initialize();
        String s;
        try {
            while ((s = freadLine()) != null) {
                s = s.trim();
                if (s.startsWith("#")) {
                    continue;
                }
                int pos = s.indexOf("=");
                if (pos <= 0) {
                    continue;
                }

                String name = s.substring(0, pos);
                String value = s.substring(pos + 1);

                ConfigValue configValue = getParameter(name);
                if (configValue == null) {
                    Logger.log(Logger.WARNING, Logger.MSG_CORE_EFACONFIGUNKNOWNPARAM, "EfaConfig(" + getFileName() + "): "+
                            International.getString("Unbekannter Parameter") + ": " + name);
                    continue;
                }

                configValue.parseValue(value);

            }
        } catch (IOException e) {
            try {
                fclose(false);
            } catch (Exception ee) {
                return false;
            }
        }

        return true;
    }

    public synchronized boolean writeEinstellungen() {
        // Datei schreiben
        try {
            String[] keys = new String[configValues.size()];
            keys = configValues.keySet().toArray(keys);
            Arrays.sort(keys);
            for (int i = 0; i < keys.length; i++) {
                ConfigValue configValue = configValues.get(keys[i]);
                if (!configValue.getName().startsWith("_") ) { // parameter names starting with "_" are not stored in config file!
                    fwrite(configValue.getName() + "=" + configValue.toString() + "\n");
                }
            }
        } catch (Exception e) {
            try {
                fcloseW();
            } catch (Exception ee) {
                return false;
            }
            return false;
        }
        return true;
    }

    public void setExternalParameters() {
        // Language
        String newLang = language.toString();
        if (Daten.efaBaseConfig.language == null || !Daten.efaBaseConfig.language.equals(newLang)) {
            Daten.efaBaseConfig.language = newLang;
            Daten.efaBaseConfig.writeFile();
            Daten.efaTypes.setToLanguage(newLang);
            Dialog.infoDialog(International.getString("Sprache"),
                    International.getString("Die geänderten Spracheinstellungen werden erst nach einem Neustart von efa wirksam."));
        }

        // Types
        if (Daten.efaTypes != null) {
            EfaTypes efaTypes = new EfaTypes(Daten.efaTypes);
            boolean changed = false;
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_GENDER, typesGender)) {
                changed = true;
            }
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_BOAT, typesBoat)) {
                changed = true;
            }
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_NUMSEATS, typesNumSeats)) {
                changed = true;
            }
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_RIGGING, typesRigging)) {
                changed = true;
            }
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_COXING, typesCoxing)) {
                changed = true;
            }
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_SESSION, typesSession)) {
                changed = true;
            }
            if (updateTypes(efaTypes, EfaTypes.CATEGORY_STATUS, typesStatus)) {
                changed = true;
            }
            if (changed) {
                efaTypes.writeFile();
                Dialog.infoDialog(International.getString("Bezeichnungen"),
                        International.getString("Die geänderten Bezeichnungen werden erst nach einem Neustart von efa wirksam."));
            }
        }
    }

    private String searchForProgram(String[] programs) {
        for (int i = 0; i < programs.length; i++) {
            if (new File(programs[i]).isFile()) {
                return programs[i];
            }
        }
        return "";
    }

    private String[] makeLookAndFeelArray(int type) {
        UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        String[] lookAndFeelArray = new String[info.length + 1];
        lookAndFeelArray[0] = (type == STRINGLIST_VALUES ? "" : International.getString("Standard") );
        for (int i = 0; i < info.length; i++) {
            String s = info[i].getClassName();
            if (type == STRINGLIST_DISPLAY) {
                int pos = (s != null ? s.lastIndexOf(".") : -1);
                if (pos>0 && pos+1<s.length()) {
                    s = s.substring(pos+1,s.length());
                }
            }
            lookAndFeelArray[i + 1] = s;
        }
        return lookAndFeelArray;
    }

    private String getDefaultLookAndFeel() {
        String[] laf = makeLookAndFeelArray(STRINGLIST_VALUES);
        if (Daten.osName.equals("Linux")) {
            for (int i=0; i<laf.length; i++) {
                if (laf[i].endsWith("MetalLookAndFeel")) {
                    return laf[i];
                }
            }
        }
        return ""; // default
    }

    private String[] makeLanguageArray(int type) {
        Vector<String> lang = International.getLanguageBundles();
        String[] languages = new String[lang.size() + 1];
        languages[0] = (type == STRINGLIST_VALUES ? "" : International.getString("Default") );
        for (int i = 0; i < lang.size(); i++) {
            Locale loc = new Locale(lang.get(i));
            languages[i + 1] = (type == STRINGLIST_VALUES ? lang.get(i) : loc.getDisplayName() );
        }
        return languages;
    }

    private String[] makeFontStyleArray(int type) {
        String[] styles = new String[3];
        styles[0] = (type == STRINGLIST_VALUES ? "" : International.getString("Default") );
        styles[1] = (type == STRINGLIST_VALUES ? FONT_PLAIN : International.getString("normal") );
        styles[2] = (type == STRINGLIST_VALUES ? FONT_BOLD : International.getString("fett") );
        return styles;
    }

    private String[] makeObmannArray(int type) {
        String[] obmann = new String[2];
        obmann[0] = (type == STRINGLIST_VALUES ? OBMANN_BOW : International.getString("Bugmann") );
        obmann[1] = (type == STRINGLIST_VALUES ? OBMANN_STROKE : International.getString("Schlagmann") );
        return obmann;
    }

    private String[] makeSessionTypeArray(int type) {
        if (Daten.efaTypes == null) {
            // EfaTypes are not available when efa is started (will be initialized after EfaConfig).
            // This doesn't matter ... when EfaConfigFrame is opened, a new EfaConfig instance will be created
            // (as a copy from the static instance). This will also initialize all lists, including this one.
            return null; 
        }
        String[] sessions = new String[Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION)];
        for(int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++) {
            sessions[i] = (type == STRINGLIST_VALUES ?
                Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, i) :
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, i));
        }
        return sessions;
    }

    private void buildTypes() {
        if (Daten.efaTypes == null) {
            return;
        }
        addParameter(typesGender = new ConfigTypeHashtable<String>("_TYPES_GENDER", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("Geschlecht")));
        addParameter(typesBoat = new ConfigTypeHashtable<String>("_TYPES_BOAT", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("Bootsart")));
        addParameter(typesNumSeats = new ConfigTypeHashtable<String>("_TYPES_NUMSEATS", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("Anzahl Ruderplätze")));
        addParameter(typesRigging = new ConfigTypeHashtable<String>("_TYPES_RIGGING", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("Riggerung")));
        addParameter(typesCoxing = new ConfigTypeHashtable<String>("_TYPES_COXING", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("mit/ohne Stm.")));
        addParameter(typesSession = new ConfigTypeHashtable<String>("_TYPES_SESSION", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("Fahrtart")));
        addParameter(typesStatus = new ConfigTypeHashtable<String>("_TYPES_STATUS", "",
                TYPE_EXPERT, makeCategory(CATEGORY_TYPES),
                International.getString("Status")));
        iniTypes(typesGender, EfaTypes.CATEGORY_GENDER);
        iniTypes(typesBoat, EfaTypes.CATEGORY_BOAT);
        iniTypes(typesNumSeats, EfaTypes.CATEGORY_NUMSEATS);
        iniTypes(typesRigging, EfaTypes.CATEGORY_RIGGING);
        iniTypes(typesCoxing, EfaTypes.CATEGORY_COXING);
        iniTypes(typesSession, EfaTypes.CATEGORY_SESSION);
        iniTypes(typesStatus, EfaTypes.CATEGORY_STATUS);
    }

    private void iniTypes(ConfigTypeHashtable<String> types, String cat) {
        for (int i=0; i<Daten.efaTypes.size(cat); i++) {
            types.put(Daten.efaTypes.getType(cat, i),
                      Daten.efaTypes.getValue(cat, i));
        }
    }

    private boolean updateTypes(EfaTypes efaTypes, String cat, ConfigTypeHashtable<String> newTypes) {
        if (newTypes == null || newTypes.size() == 0) {
            return false;
        }
        boolean changed = false;

        // check for changes in current types (changed values or removed types)
        String[] oldTypes = efaTypes.getTypesArray(cat);
        for (int i=0; oldTypes != null && i<oldTypes.length; i++) {
            String newValue = newTypes.get(oldTypes[i]);
            if (newValue != null) {
                String oldValue = efaTypes.getValue(cat, oldTypes[i]);
                if (!newValue.equals(oldValue)) {
                    efaTypes.setValue(cat, oldTypes[i], newValue);
                    changed = true;
                }
            } else {
                efaTypes.removeValue(cat, oldTypes[i]);
                changed = true;
            }
        }

        // check for added types
        String[] newTypeNames = newTypes.getKeysArray();
        for (int i=0; newTypeNames != null && i<newTypeNames.length; i++) {
            if (!efaTypes.isConfigured(cat, newTypeNames[i])) {
                efaTypes.setValue(cat, newTypeNames[i], newTypes.get(newTypeNames[i]));
                changed = true;
            }
        }

        return changed;
    }

}
