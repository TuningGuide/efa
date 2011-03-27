/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.direkt.Admin;
import de.nmichael.efa.core.DownloadFrame;
import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;

// @i18n complete

public class EfaConfig extends DatenListe {

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
    public static final String CATEGORY_TYPES_SESS    = "131TYPES_SESS";
    public static final String CATEGORY_TYPES_BOAT    = "132TYPES_BOAT";
    public static final String CATEGORY_TYPES_SEAT    = "133TYPES_SEAT";
    public static final String CATEGORY_TYPES_RIGG    = "134TYPES_RIGG";
    public static final String CATEGORY_TYPES_COXD    = "135TYPES_COXD";
    public static final String CATEGORY_TYPES_GEND    = "136TYPES_GEND";
    public static final String CATEGORY_TYPES_STAT    = "137TYPES_STAT";
    public static final String CATEGORY_LOCALE        = "14LOCALE";

    private static final int STRINGLIST_VALUES  = 1;
    private static final int STRINGLIST_DISPLAY = 2;

    // Werte für NameFormat
    public static final String NAMEFORMAT_FIRSTLAST = "FIRSTLAST";
    public static final String NAMEFORMAT_LASTFIRST = "LASTFIRST";

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

    public static final String KENNUNG190 = "##EFA.190.CONFIGURATION##";

    // private internal data
    private HashMap<String,String> categories;
    private HashMap<String,ItemType> configValues;
    private Vector<String> configValueNames;
    private CustSettings custSettings = null;
    
    // public configuration data
    public ItemTypeFile letzteDatei; // @todo remove efa1
    public ItemTypeFile direkt_letzteDatei; // @todo remove efa1
    public ItemTypeString lastProjectEfaBase;
    public ItemTypeString lastProjectEfaBoathouse;
    public ItemTypeBoolean autogenAlias;
    public ItemTypeString aliasFormat;
    public ItemTypeString bakDir;
    public ItemTypeBoolean bakSave;
    public ItemTypeBoolean bakMonat;
    public ItemTypeBoolean bakTag;
    public ItemTypeBoolean bakKonv;
    public ItemTypeFile browser;
    public ItemTypeFile acrobat;
    public ItemTypeInteger printPageWidth;
    public ItemTypeInteger printPageHeight;
    public ItemTypeInteger printLeftMargin;
    public ItemTypeInteger printTopMargin;
    public ItemTypeInteger printPageOverlap;
    public ItemTypeHashtable<String> keys;
    public ItemTypeInteger countEfaStarts;
    public ItemTypeString registeredProgramID;
    public ItemTypeInteger registrationChecks;
    public ItemTypeBoolean autoStandardmannsch;
    public ItemTypeBoolean manualStandardmannsch;
    public ItemTypeBoolean showObmann;
    public ItemTypeBoolean autoObmann;
    public ItemTypeStringList defaultObmann;
    public ItemTypeBoolean popupComplete;
    public ItemTypeStringList nameFormat;
    public ItemTypeBoolean correctMisspelledMitglieder;
    public ItemTypeBoolean correctMisspelledBoote;
    public ItemTypeBoolean correctMisspelledZiele;
    public ItemTypeBoolean skipUhrzeit;
    public ItemTypeBoolean skipZiel;
    public ItemTypeBoolean skipMannschKm;
    public ItemTypeBoolean skipBemerk;
    public ItemTypeBoolean fensterZentriert;
    public ItemTypeInteger windowXOffset;
    public ItemTypeInteger windowYOffset;
    public ItemTypeInteger screenWidth;
    public ItemTypeInteger screenHeight;
    public ItemTypeInteger maxDialogHeight;
    public ItemTypeInteger maxDialogWidth;
    public ItemTypeStringList lookAndFeel;
    public ItemTypeString zielfahrtSeparatorBereiche;
    public ItemTypeString zielfahrtSeparatorFahrten;
    public ItemTypeStringList standardFahrtart;
    public ItemTypeStringList defaultDistanceUnit;
    public ItemTypeBoolean debugLogging;
    public ItemTypeString traceTopic;
    public ItemTypeString efaVersionLastCheck;
    public ItemTypeString version;
    public ItemTypeHashtable<Admin> admins;
    public ItemTypeBoolean efaDirekt_zielBeiFahrtbeginnPflicht;
    public ItemTypeBoolean efaDirekt_eintragErzwingeObmann;
    public ItemTypeBoolean efaDirekt_eintragErlaubeNurMaxRudererzahl;
    public ItemTypeBoolean efaDirekt_eintragNichtAenderbarUhrzeit;
    public ItemTypeBoolean efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen;
    public ItemTypeBoolean efaDirekt_eintragNurBekannteBoote;
    public ItemTypeBoolean efaDirekt_eintragNurBekannteRuderer;
    public ItemTypeBoolean efaDirekt_eintragNurBekannteZiele;
    public ItemTypeBoolean efaDirekt_eintragHideUnnecessaryInputFields;
    public ItemTypeInteger efaDirekt_plusMinutenAbfahrt;
    public ItemTypeInteger efaDirekt_minusMinutenAnkunft;
    public ItemTypeBoolean efaDirekt_mitgliederDuerfenReservieren;
    public ItemTypeBoolean efaDirekt_mitgliederDuerfenReservierenZyklisch;
    public ItemTypeBoolean efaDirekt_mitgliederDuerfenReservierungenEditieren;
    public ItemTypeBoolean efaDirekt_mitgliederDuerfenEfaBeenden;
    public ItemTypeBoolean efaDirekt_mitgliederDuerfenNamenHinzufuegen;
    public ItemTypeBoolean efaDirekt_resBooteNichtVerfuegbar;
    public ItemTypeBoolean efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar;
    public ItemTypeInteger efaDirekt_resLookAheadTime;
    public ItemTypeString efaDirekt_execOnEfaExit;
    public ItemTypeTime efaDirekt_exitTime;
    public ItemTypeInteger efaDirekt_exitIdleTime;
    public ItemTypeString efaDirekt_execOnEfaAutoExit;
    public ItemTypeTime efaDirekt_restartTime;
    public ItemTypeBoolean efaDirekt_checkRunning;
    public ItemTypeConfigButton efaDirekt_butFahrtBeginnen;
    public ItemTypeConfigButton efaDirekt_butFahrtBeenden;
    public ItemTypeConfigButton efaDirekt_butFahrtAbbrechen;
    public ItemTypeConfigButton efaDirekt_butNachtrag;
    public ItemTypeConfigButton efaDirekt_butBootsreservierungen;
    public ItemTypeConfigButton efaDirekt_butFahrtenbuchAnzeigen;
    public ItemTypeConfigButton efaDirekt_butStatistikErstellen;
    public ItemTypeConfigButton efaDirekt_butNachrichtAnAdmin;
    public ItemTypeConfigButton efaDirekt_butAdminModus;
    public ItemTypeConfigButton efaDirekt_butSpezial;
    public ItemTypeString efaDirekt_butSpezialCmd;
    public ItemTypeBoolean efaDirekt_showButtonHotkey;
    public ItemTypeBoolean efaDirekt_showUhr;
    public ItemTypeBoolean efaDirekt_sunRiseSet_show;
    public ItemTypeLongLat efaDirekt_sunRiseSet_latitude;
    public ItemTypeLongLat efaDirekt_sunRiseSet_longitude;
    public ItemTypeBoolean efaDirekt_sortByAnzahl;
    public ItemTypeBoolean efaDirekt_autoPopupOnBoatLists;
    public ItemTypeBoolean efaDirekt_listAllowToggleBoatsPersons;
    public ItemTypeBoolean efaDirekt_showEingabeInfos;
    public ItemTypeBoolean efaDirekt_showBootsschadenButton;
    public ItemTypeInteger efaDirekt_maxFBAnzeigenFahrten;
    public ItemTypeInteger efaDirekt_anzFBAnzeigenFahrten;
    public ItemTypeBoolean efaDirekt_FBAnzeigenAuchUnvollstaendige;
    public ItemTypeDate efaDirekt_autoNewFb_datum;
    public ItemTypeString efaDirekt_autoNewFb_datei;
    public ItemTypeInteger efaDirekt_fontSize;
    public ItemTypeStringList efaDirekt_fontStyle;
    public ItemTypeBoolean efaDirekt_colorizeInputField;
    public ItemTypeBoolean efaDirekt_showZielnameFuerBooteUnterwegs;
    public ItemTypeString efadirekt_adminLastOsCommand;
    public ItemTypeImage efaDirekt_vereinsLogo;
    public ItemTypeString efaDirekt_newsText;
    public ItemTypeBoolean efaDirekt_startMaximized;
    public ItemTypeBoolean efaDirekt_fensterNichtVerschiebbar;
    public ItemTypeBoolean efaDirekt_immerImVordergrund;
    public ItemTypeBoolean efaDirekt_immerImVordergrundBringToFront;
    public ItemTypeBoolean efaDirekt_bnrError_admin;
    public ItemTypeBoolean efaDirekt_bnrError_bootswart;
    public ItemTypeBoolean efaDirekt_bnrWarning_admin;
    public ItemTypeBoolean efaDirekt_bnrWarning_bootswart;
    public ItemTypeBoolean efaDirekt_bnrBootsstatus_admin;
    public ItemTypeBoolean efaDirekt_bnrBootsstatus_bootswart;
    public ItemTypeLong efaDirekt_bnrWarning_lasttime;
    public ItemTypeString efaDirekt_emailServer;
    public ItemTypeInteger efaDirekt_emailPort;
    public ItemTypeString efaDirekt_emailAbsender;
    public ItemTypeString efaDirekt_emailUsername;
    public ItemTypeString efaDirekt_emailPassword;
    public ItemTypeString efaDirekt_emailAbsenderName;
    public ItemTypeString efaDirekt_emailBetreffPraefix;
    public ItemTypeString efaDirekt_emailSignatur;
    public ItemTypeString efaDirekt_lockEfaShowHtml;
    public ItemTypeBoolean efaDirekt_lockEfaVollbild;
    public ItemTypeDate efaDirekt_lockEfaFromDatum;
    public ItemTypeTime efaDirekt_lockEfaFromZeit;
    public ItemTypeDate efaDirekt_lockEfaUntilDatum;
    public ItemTypeTime efaDirekt_lockEfaUntilZeit;
    public ItemTypeBoolean efaDirekt_locked;
    public ItemTypeBoolean showGermanOptions;
    public ItemTypeBoolean showBerlinOptions;
    public ItemTypeBoolean useForRowing;
    public ItemTypeBoolean useForCanoeing;
    public ItemTypeFile efaUserDirectory;
    public ItemTypeStringList language;
    public ItemTypeAction typesResetToDefault;
    public ItemTypeAction typesAddAllDefaultRowingBoats;
    public ItemTypeAction typesAddAllDefaultCanoeingBoats;
    public ItemTypeHashtable<String> typesGender;
    public ItemTypeHashtable<String> typesBoat;
    public ItemTypeHashtable<String> typesNumSeats;
    public ItemTypeHashtable<String> typesRigging;
    public ItemTypeHashtable<String> typesCoxing;
    public ItemTypeHashtable<String> typesSession;
    public ItemTypeHashtable<String> typesStatus;

    // Default Contructor (with Customization Settings)
    public EfaConfig(String filename, CustSettings custSettings) {
        super(filename, 0, 0, false);
        kennung = KENNUNG190;
        if (Logger.isTraceOn(Logger.TT_CORE)) {
            Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFACONFIG, "EfaConfig("+filename+")");
        }
        this.custSettings = custSettings;
        initialize();
    }

    // Default Contructor (without Customization Settings)
    public EfaConfig(String filename) {
        super(filename, 0, 0, false);
        kennung = KENNUNG190;
        if (Logger.isTraceOn(Logger.TT_CORE)) {
            Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFACONFIG, "EfaConfig("+filename+")");
        }
        initialize();
    }

    // Copy Constructor
    public EfaConfig(EfaConfig efaConfig) {
        super(efaConfig.getFileName(), 0, 0, false);
        kennung = efaConfig.kennung;
        initialize();
        String[] pnames = efaConfig.getParameterNames();
        for (int i=0; i<pnames.length; i++) {
            ItemType configValue = getParameter(pnames[i]);
            configValue.parseValue(efaConfig.getParameter(pnames[i]).toString());
        }
    }

    // clean-up and re-initialize data structures
    private void initialize() {
        categories   = new HashMap<String,String>();
        configValues = new HashMap<String,ItemType>();
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
        categories.put(CATEGORY_TYPES_SESS,    International.getString("Fahrtart"));
        categories.put(CATEGORY_TYPES_BOAT,    International.getString("Bootsart"));
        categories.put(CATEGORY_TYPES_SEAT,    International.getString("Anzahl Bootsplätze"));
        categories.put(CATEGORY_TYPES_RIGG,    International.getString("Riggerung"));
        categories.put(CATEGORY_TYPES_COXD,    International.getString("mit/ohne Stm."));
        categories.put(CATEGORY_TYPES_GEND,    International.getString("Geschlecht"));
        categories.put(CATEGORY_TYPES_STAT,    International.getString("Status"));
    }

    // initialize all configuration parameters with their default values
    private void iniParameters() {

        // ============================= INTERNAL =============================
        addParameter(version = new ItemTypeString("EFA_VERSION", "100",
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa version"));
        addParameter(efaVersionLastCheck = new ItemTypeString("EFA_VERSION_LASTCHECK", "",
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa last checked for new version"));
        addParameter(countEfaStarts = new ItemTypeInteger("EFA_STARTS_COUNTER", 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa start counter"));
        addParameter(registeredProgramID = new ItemTypeString("EFA_REGISTRATION_PROGRAMMID", "",
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa registered programm ID"));
        addParameter(registrationChecks = new ItemTypeInteger("EFA_REGISTRATION_CHECKS", 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa registration checks counter"));

        // ============================= COMMON:COMMON =============================
        addParameter(efaUserDirectory = new ItemTypeFile("_EFAUSERDIRECTORY", Daten.efaBaseConfig.efaUserDirectory,
                International.getString("Verzeichnis für Nutzerdaten"),
                International.getString("Verzeichnisse"),
                null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_DIR,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                International.getString("Verzeichnis für Nutzerdaten")));
        addParameter(letzteDatei = new ItemTypeFile("LASTFILE_EFABASE", "",
                International.getString("Fahrtenbuch"),
                International.getString("Fahrtenbuch")+" (*.efb)",
                "efb",ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                "Last logbook opened by efa Base"));
        addParameter(lastProjectEfaBase = new ItemTypeString("LASTPROJECT_EFABASE", "",
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                "Last project opened by efa Base"));
        addParameter(aliasFormat = new ItemTypeString("ALIAS_FORMAT", "{V1}{V2}-{N1}",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                International.getString("Format der Eingabe-Kürzel")));
        addParameter(autogenAlias = new ItemTypeBoolean("ALIAS_AUTOGENERATE", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                International.getString("Eingabe-Kürzel automatisch beim Anlegen neuer Mitglieder generieren")));
        addParameter(debugLogging = new ItemTypeBoolean("DEBUG_LOGGING", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON),
                International.getString("Debug-Logging aktivieren")));
        addParameter(traceTopic = new ItemTypeString("TRACE_TOPIC", "",
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON),
                International.getString("Trace-Topic")));

        // ============================= COMMON:INPUT =============================
        addParameter(nameFormat = new ItemTypeStringList("NAMEFORMAT", NAMEFORMAT_LASTFIRST,
                new String[] { NAMEFORMAT_FIRSTLAST, NAMEFORMAT_LASTFIRST },
                new String[] { International.getString("Vorname") + " " +
                               International.getString("Nachname"),
                               International.getString("Nachname") + ", " +
                               International.getString("Vorname"),
                },
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Namensformat")));

        addParameter(standardFahrtart = new ItemTypeStringList("SESSIONTYPE_DEFAULT", EfaTypes.TYPE_SESSION_NORMAL,
                EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standard-Fahrtart")));
        addParameter(defaultObmann = new ItemTypeStringList("CREWSHEAD_DEFAULT", OBMANN_BOW,
                makeObmannArray(STRINGLIST_VALUES), makeObmannArray(STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standard-Obmann für ungesteuerte Boote")));
        addParameter(showObmann = new ItemTypeBoolean("CREWSHEAD_SHOW", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Obmann-Auswahlliste anzeigen")));
        addParameter(autoObmann = new ItemTypeBoolean("CREWSHEAD_AUTOSELECT", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Obmann bei Eingabe automatisch auswählen")));
        addParameter(autoStandardmannsch = new ItemTypeBoolean("DEFAULTCREW_AUTOSELECT", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standardmannschaft automatisch eintragen")));
        addParameter(manualStandardmannsch = new ItemTypeBoolean("DEFAULTCREW_MANUALSELECT", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Manuelle Auswahl einer Standardmannschaft erlauben")));
        addParameter(correctMisspelledMitglieder = new ItemTypeBoolean("SPELLING_CHECKMEMBERS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Mitglieder"))));
        addParameter(correctMisspelledBoote = new ItemTypeBoolean("SPELLING_CHECKBOATS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Boote"))));
        addParameter(correctMisspelledZiele = new ItemTypeBoolean("SPELLING_CHECKDESTINATIONS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Ziele"))));
        addParameter(skipUhrzeit = new ItemTypeBoolean("INPUT_SKIPTIME", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Uhrzeit"))));
        addParameter(skipZiel = new ItemTypeBoolean("INPUT_SKIPDESTINATION", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Ziel"))));
        addParameter(skipMannschKm = new ItemTypeBoolean("INPUT_SKIPCREWKM", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Mannschafts-Km"))));
        addParameter(skipBemerk = new ItemTypeBoolean("INPUT_SKIPCOMMENTS", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Bemerkungen"))));
        addParameter(keys = new ItemTypeHashtable<String>("HOTKEYS", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Tastenbelegungen für Bemerkungs-Feld")));
        addParameter(zielfahrtSeparatorBereiche = new ItemTypeString("ZIELFAHRT_SEPARATORBEREICHE", ",",
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.onlyFor("Trennzeichen für Bereiche einer Zielfahrt","de")));
        addParameter(zielfahrtSeparatorFahrten = new ItemTypeString("ZIELFAHRT_SEPARATORFAHRTEN", ";", // was "/" before
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.onlyFor("Trennzeichen für Zielfahrten","de")));

        // ============================= COMMON:GUI =============================
        addParameter(lookAndFeel = new ItemTypeStringList("LOOK_AND_FEEL", getDefaultLookAndFeel(),
                makeLookAndFeelArray(STRINGLIST_VALUES), makeLookAndFeelArray(STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Look & Feel")));
        addParameter(popupComplete = new ItemTypeBoolean("AUTOCOMPLETEPOPUP_SHOW", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Beim Vervollständigen Popup-Liste anzeigen")));
        addParameter(fensterZentriert = new ItemTypeBoolean("WINDOW_CENTERED", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Alle Fenster in Bildschirmmitte zentrieren")));
        addParameter(windowXOffset = new ItemTypeInteger("WINDOW_OFFSETX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Fenster-Offset")+ " X" +
                " (" + International.getString("Pixel") + ")"));
        addParameter(windowYOffset = new ItemTypeInteger("WINDOW_OFFSETY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Fenster-Offset")+ " Y" +
                " (" + International.getString("Pixel") + ")"));
        addParameter(screenWidth = new ItemTypeInteger("SCREEN_WIDTH", 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Bildschirmbreite") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(screenHeight = new ItemTypeInteger("SCREEN_HEIGHT", 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Bildschirmhöhe") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(maxDialogHeight = new ItemTypeInteger("DIALOG_MAXHEIGHT", 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("maximale Dialog-Höhe") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(maxDialogWidth = new ItemTypeInteger("DIALOG_MAXWIDTH", 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("maximale Dialog-Breite") +
                " (" + International.getString("Pixel") + ")"));

        // ============================= COMMON:BACKUP =============================
        addParameter(bakDir = new ItemTypeFile("BACKUP_DIRECTORY", "",
                International.getString("Backup-Verzeichnis"),
                International.getString("Verzeichnis"),
                null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_DIR,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup-Verzeichnis")));
        addParameter(bakSave = new ItemTypeBoolean("BACKUP_ON_SAVE", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup bei jedem Speichern")));
        addParameter(bakMonat = new ItemTypeBoolean("BACKUP_ON_MONTHLY_SAVE", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim ersten Speichern jeden Monat")));
        addParameter(bakTag = new ItemTypeBoolean("BACKUP_ON_DAYLY_SAVE", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim ersten Speichern jeden Tag")));
        addParameter(bakKonv = new ItemTypeBoolean("BACKUP_ON_CONVERTING", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim Konvertieren")));

        // ============================= COMMON:EXTTOOLS =============================
        addParameter(browser = new ItemTypeFile("WEBBROWSER", searchForProgram(DEFAULT_BROWSER),
                International.getString("Webbrowser"),
                International.getString("Windows-Programme")+" (*.exe)",
                "exe",ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_EXTTOOLS),
                International.getString("Webbrowser")));
        addParameter(acrobat = new ItemTypeFile("ACROBAT_READER", searchForProgram(DEFAULT_ACROBAT),
                International.getString("Acrobat Reader"),
                International.getString("Windows-Programme")+" (*.exe)",
                "exe",ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_EXTTOOLS),
                International.getString("Acrobat Reader")));

        // ============================= COMMON:PRINT =============================
        addParameter(printPageWidth = new ItemTypeInteger("PRINT_PAGEWIDTH", 210, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenbreite")));
        addParameter(printPageHeight = new ItemTypeInteger("PRINT_PAGEHEIGHT", 297, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenhöhe")));
        addParameter(printLeftMargin = new ItemTypeInteger("PRINT_PAGEMARGIN_LEFTRIGHT", 15, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("linker und rechter Rand")));
        addParameter(printTopMargin = new ItemTypeInteger("PRINT_PAGEMARGIN_TOPBOTTOM", 15, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("oberer und unterer Rand")));
        addParameter(printPageOverlap = new ItemTypeInteger("PRINT_PAGEOVERLAP", 5, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenüberlappung")));

        // ============================= BOATHOUSE:COMMON =============================
        addParameter(direkt_letzteDatei = new ItemTypeFile("LASTFILE_EFABOATHOUSE", "",
                International.getString("Fahrtenbuch"),
                International.getString("Fahrtenbuch")+" (*.efb)",
                "efb",ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                "Last logbook opened by efa Boathouse"));
        addParameter(lastProjectEfaBoathouse = new ItemTypeString("LASTPROJECT_EFABOATHOUSE", "",
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_COMMON,CATEGORY_COMMON),
                "Last project opened by efa Boathouse"));
        addParameter(admins = new ItemTypeHashtable<Admin>("ADMINS", new Admin("name","password"), false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Administratoren")));
        addParameter(efaDirekt_resBooteNichtVerfuegbar = new ItemTypeBoolean("SHOWASNOTAVAILABLE_RESERVEDBOATS", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Reservierte Boote als 'nicht verfügbar' anzeigen")));
        addParameter(efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar = new ItemTypeBoolean("SHOWASNOTAVAILABLE_MULTIDAY_REGATTA", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Boote auf Regatta, Trainingslager oder Mehrtagesfahrt als 'nicht verfügbar' anzeigen")));
        addParameter(efaDirekt_resLookAheadTime = new ItemTypeInteger("RESERVATION_LOOKAHEADTIME", 120, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Bei Fahrtbeginn auf Reservierungen bis zu x Minuten in der Zukunft prüfen")));
        addParameter(efaDirekt_showBootsschadenButton = new ItemTypeBoolean("BOATDAMAGE_ENABLEREPORTING", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Melden von Bootsschäden erlauben")));
        addParameter(efaDirekt_lockEfaShowHtml = new ItemTypeString("LOCKEFA_PAGE", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("HTML-Seite anzeigen")));
        addParameter(efaDirekt_lockEfaVollbild = new ItemTypeBoolean("LOCKEFA_FULLSCREEN", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Vollbild")));
        addParameter(efaDirekt_lockEfaFromDatum = new ItemTypeDate("LOCKEFA_FROMDATE", new DataTypeDate(-1,-1,-1),
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beginnen") + " ("+
                International.getString("Datum")+")"));
        addParameter(efaDirekt_lockEfaFromZeit = new ItemTypeTime("LOCKEFA_FROMTIME", new DataTypeTime(-1,-1,-1),
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beginnen") + " ("+
                International.getString("Zeit")+")"));
        addParameter(efaDirekt_lockEfaUntilDatum = new ItemTypeDate("LOCKEFA_TODATE", new DataTypeDate(-1,-1,-1),
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beenden") + " ("+
                International.getString("Datum")+")"));
        addParameter(efaDirekt_lockEfaUntilZeit = new ItemTypeTime("LOCKEFA_TOTIME", new DataTypeTime(-1,-1,-1),
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa sperren")+": "+
                International.getString("Sperrung automatisch beenden") + " ("+
                International.getString("Zeit")+")"));
        addParameter(efaDirekt_locked = new ItemTypeBoolean("LOCKEFA_LOCKED", false,
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("efa ist für die Benutzung gesperrt")));
        addParameter(efaDirekt_autoNewFb_datum = new ItemTypeDate("NEWLOGBOOK_DATE", new DataTypeDate(-1,-1,-1),
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Neues Fahrtenbuch erstellen am")));
        addParameter(efaDirekt_autoNewFb_datei = new ItemTypeFile("NEWLOGBOOK_FILE", "",
                International.getString("Fahrtenbuch"),
                International.getString("Fahrtenbuch")+" (*.efb)",
                "efb",ItemTypeFile.MODE_SAVE,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Dateiname des neuen Fahrtenbuchs")));
        addParameter(efadirekt_adminLastOsCommand = new ItemTypeString("ADMIN_LAST_OSCOMMAND", "",
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Betriebssystemkommando")));

        // ============================= BOATHOUSE:INPUT =============================
        addParameter(efaDirekt_eintragNurBekannteBoote = new ItemTypeBoolean("ONLY_KNOWNBOATS", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Boote"))));
        addParameter(efaDirekt_eintragNurBekannteRuderer = new ItemTypeBoolean("ONLY_KNOWNPERSONS", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Personen"))));
        addParameter(efaDirekt_eintragNurBekannteZiele = new ItemTypeBoolean("ONLY_KNOWNDESTINATIONS", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Ziele"))));
        addParameter(efaDirekt_eintragErlaubeNurMaxRudererzahl = new ItemTypeBoolean("ALLOW_ONLY_MAXCREWNUMBER", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Nur für das Boot maximal mögliche Anzahl an Personen erlauben")));
        addParameter(efaDirekt_eintragErzwingeObmann = new ItemTypeBoolean("MUST_SELECT_CREWSHEAD", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Obmann muß ausgewählt werden")));
        addParameter(efaDirekt_eintragNichtAenderbarUhrzeit = new ItemTypeBoolean("NOTEDITABLE_TIME", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Vorgeschlagene Uhrzeiten können nicht geändert werden")));
        addParameter(efaDirekt_plusMinutenAbfahrt = new ItemTypeInteger("TIME_SESSIONSTART_ADD", 10, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Für Abfahrt x Minuten zur aktuellen Zeit hinzuaddieren")));
        addParameter(efaDirekt_minusMinutenAnkunft = new ItemTypeInteger("TIME_SESSIONEND_SUBSTRACT", 10, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Für Ankunft x Minuten von aktueller Zeit abziehen")));
        addParameter(efaDirekt_zielBeiFahrtbeginnPflicht = new ItemTypeBoolean("MUST_DESTINATION_AT_SESSIONSTART", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Ziel muß bereits bei Fahrtbeginn angegeben werden")));
        addParameter(efaDirekt_eintragHideUnnecessaryInputFields = new ItemTypeBoolean("HIDE_UNNECESSARY_INPUTFIELDS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Beim Eintrag von Fahrten unnötige Eingabefelder ausblenden")));
        addParameter(efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen = new ItemTypeBoolean("NOTEDITABLE_KMFORKNOWNDESTINATIONS", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Vorgeschlagene Kilometer bei bekannten Zielen können nicht geändert werden")));

        // ============================= BOATHOUSE:GUI =============================
        addParameter(efaDirekt_startMaximized = new ItemTypeBoolean("WINDOW_MAXIMIZED", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("efa maximiert starten")));
        addParameter(efaDirekt_fensterNichtVerschiebbar = new ItemTypeBoolean("WINDOW_FIXED", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Hauptfenster nicht verschiebbar")));
        addParameter(efaDirekt_immerImVordergrund = new ItemTypeBoolean("WINDOW_ALWAYSONTOP", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("efa immer im Vordergrund")));
        addParameter(efaDirekt_immerImVordergrundBringToFront = new ItemTypeBoolean("WINDOW_ALWAYSONTOP_BRINGTOFRONT", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("efa immer im Vordergrund") + " (bringToFront)"));
        addParameter(efaDirekt_fontSize = new ItemTypeInteger("FONT_SIZE", (Dialog.screenSize.width >= 1024 ? 16 : 12), 6, 32, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Schriftgröße in Punkten (6 bis 32, Standard: 12)")));
        addParameter(efaDirekt_fontStyle = new ItemTypeStringList("FONT_STYLE", "",
                makeFontStyleArray(STRINGLIST_VALUES), makeFontStyleArray(STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Schriftstil")));
        addParameter(efaDirekt_colorizeInputField = new ItemTypeBoolean("COLORIZE_INPUTFIELDS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("aktuelles Eingabefeld farblich hervorheben")));
        addParameter(efaDirekt_showEingabeInfos = new ItemTypeBoolean("SHOW_INPUTHINTS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Eingabehinweise anzeigen")));
        addParameter(efaDirekt_showZielnameFuerBooteUnterwegs = new ItemTypeBoolean("DISPLAY_DESTINATION_FORBOATSONWATER", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Fahrtziel in der Liste {list} anzeigen",
                International.getString("Boote auf Fahrt"))));
        addParameter(efaDirekt_listAllowToggleBoatsPersons = new ItemTypeBoolean("LISTS_TOGGLE_BOATSPERSONS", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("erlaube Auswahl in Bootslisten alternativ auch über Personennamen")));
        addParameter(efaDirekt_sortByAnzahl = new ItemTypeBoolean("BOATLIST_SORTBYSEATS", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("sortiere Boote nach Anzahl der Bootsplätze")));
        addParameter(efaDirekt_autoPopupOnBoatLists = new ItemTypeBoolean("BOATLIST_AUTOPOPUP", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("automatisches Popup-Menü für Mausclicks in den Bootslisten")));
        addParameter(efaDirekt_vereinsLogo = new ItemTypeImage("CLUBLOGO", "", 192, 64,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Vereinslogo")));
        addParameter(efaDirekt_showUhr = new ItemTypeBoolean("SHOW_TIME", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Uhr anzeigen")));
        addParameter(efaDirekt_sunRiseSet_show = new ItemTypeBoolean("SUNRISESET_SHOW", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Sonnenaufgangs- und -untergangszeit anzeigen")));
        addParameter(efaDirekt_sunRiseSet_latitude = new ItemTypeLongLat("SUNRISESET_LATITUDE",
                ItemTypeLongLat.ORIENTATION_NORTH,52,25,9,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("geographische Breite")));
        addParameter(efaDirekt_sunRiseSet_longitude = new ItemTypeLongLat("SUNRISESET_LONGITUDE",
                ItemTypeLongLat.ORIENTATION_EAST,13,10,15,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("geographische Länge")));
        addParameter(efaDirekt_newsText = new ItemTypeString("NEWS_TEXT", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("News-Text")));
        addParameter(efaDirekt_maxFBAnzeigenFahrten = new ItemTypeInteger("LOGBOOK_DISPLAYEDENTRIES_MAXNUMBER", 100, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Fahrtenbuch anzeigen")+": "+International.getString("maximale Anzahl von Fahrten")));
        addParameter(efaDirekt_anzFBAnzeigenFahrten = new ItemTypeInteger("LOGBOOK_DISPLAYEDENTRIES_DEFAULTNUMBER", 50, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Fahrtenbuch anzeigen")+": "+International.getString("Anzahl von Fahrten")));
        addParameter(efaDirekt_FBAnzeigenAuchUnvollstaendige = new ItemTypeBoolean("LOGBOOK_DISPLAYEDENTRIES_DEFAULTALSOINCOMPLETE", false,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Fahrtenbuch anzeigen")+": "+International.getString("auch unvollständige Fahrten")));

        // ============================= BOATHOUSE:GUIBUTTONS =============================
        addParameter(efaDirekt_butFahrtBeginnen = new ItemTypeConfigButton("BUTTON_STARTSESSION",
                International.getString("Fahrt beginnen")+" >>>", "CCFFCC", true, true, true, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrt beginnen"))));
        addParameter(efaDirekt_butFahrtBeenden = new ItemTypeConfigButton("BUTTON_FINISHSESSION",
                "<<< "+International.getString("Fahrt beenden"), "CCFFCC", true, true, true, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrt beenden"))));
        addParameter(efaDirekt_butFahrtAbbrechen = new ItemTypeConfigButton("BUTTON_CANCELSESSION",
                International.getString("Fahrt abbrechen"), "FFCCCC", true, false, true, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrt abbrechen"))));
        addParameter(efaDirekt_butNachtrag = new ItemTypeConfigButton("BUTTON_LATEENTRY",
                International.getString("Nachtrag"), "CCFFFF", true, false, true, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Nachtrag"))));
        addParameter(efaDirekt_butBootsreservierungen = new ItemTypeConfigButton("BUTTON_BOATRESERVATIONS",
                International.getString("Bootsreservierungen"), "FFFFCC", true, false, true, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Bootsreservierungen"))));
        addParameter(efaDirekt_butFahrtenbuchAnzeigen = new ItemTypeConfigButton("BUTTON_SHOWLOGBOOK",
                International.getString("Fahrtenbuch anzeigen"), "CCCCFF", true, false, true, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Fahrtenbuch anzeigen"))));
        addParameter(efaDirekt_butStatistikErstellen = new ItemTypeConfigButton("BUTTON_CREATESTATISTICS",
                International.getString("Statistiken erstellen"), "CCCCFF", true, false, true, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Statistiken erstellen"))));
        addParameter(efaDirekt_butNachrichtAnAdmin = new ItemTypeConfigButton("BUTTON_MESSAGETOADMIN",
                International.getString("Nachricht an Admin"), "FFF197", true, false, true, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Nachricht an Admin"))));
        addParameter(efaDirekt_butAdminModus = new ItemTypeConfigButton("BUTTON_ADMINMODE",
                International.getString("Admin-Modus"), "CCCCCC", true, false, true, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Admin-Modus"))));
        addParameter(efaDirekt_butSpezial = new ItemTypeConfigButton("BUTTON_SPECIAL",
                International.getString("Spezial-Button"), "CCCCCC", false, true, true, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Button '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_butSpezialCmd = new ItemTypeString("BUTTON_SPECIAL_COMMAND", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getMessage("Auszuführendes Kommando für '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_showButtonHotkey = new ItemTypeBoolean("BUTTON_SHOWHOTKEYS", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUIBUTTONS),
                International.getString("Hotkeys für Buttons anzeigen")));

        // ============================= BOATHOUSE:STARTSTOP =============================
        addParameter(efaDirekt_restartTime = new ItemTypeTime("EFAEXIT_RESTARTTIME", new DataTypeTime(4,0,0),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Uhrzeit zum automatischen Neustart von efa")));
        addParameter(efaDirekt_exitTime = new ItemTypeTime("EFAEXIT_TIME", new DataTypeTime(-1,-1,-1),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Uhrzeit zum automatischen Beenden von efa")));
        addParameter(efaDirekt_exitIdleTime = new ItemTypeInteger("EFAEXIT_IDLETIME", ItemTypeInteger.UNSET, 0, Integer.MAX_VALUE, true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("efa automatisch nach Inaktivität beenden") +
                " [" + International.getString("Minuten") + "]"));
        addParameter(efaDirekt_execOnEfaAutoExit = new ItemTypeString("EFAEXIT_EXECONAUTOEXIT", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Folgendes Kommando beim automatischen Beenden von efa ausführen")));
        addParameter(efaDirekt_execOnEfaExit = new ItemTypeString("EFAEXIT_EXEC", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Folgendes Kommando beim Beenden von efa durch Mitglieder ausführen")));

        // ============================= BOATHOUSE:PERMISSIONS =============================
        addParameter(efaDirekt_mitgliederDuerfenReservieren = new ItemTypeBoolean("ALLOW_MEMBERS_BOATRESERVATION_ONCE", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Boote reservieren")));
        addParameter(efaDirekt_mitgliederDuerfenReservierenZyklisch = new ItemTypeBoolean("ALLOW_MEMBERS_BOATRESERVATION_REOCCURRING", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Boote reservieren") +
                " (" + International.getString("einmalige Reservierungen") + ")"));
        addParameter(efaDirekt_mitgliederDuerfenReservierungenEditieren = new ItemTypeBoolean("ALLOW_MEMBERS_BOATRESERVATION_CHANGE", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Bootsreservierungen verändern und löschen")));
        addParameter(efaDirekt_mitgliederDuerfenNamenHinzufuegen = new ItemTypeBoolean("ALLOW_MEMBERS_ADDNAMES", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Namen zur Mitgliederliste hinzufügen")));
        addParameter(efaDirekt_mitgliederDuerfenEfaBeenden = new ItemTypeBoolean("ALLOW_MEMBERS_EXITEFA", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen efa beenden")));

        // ============================= BOATHOUSE:NOTIFICATIONS =============================
        addParameter(efaDirekt_bnrError_admin = new ItemTypeBoolean("NOTIFICATION_ERROR_ADMIN", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Admins"),International.getString("bei Fehlern") + " (ERROR)")));
        addParameter(efaDirekt_bnrWarning_admin = new ItemTypeBoolean("NOTIFICATION_WARNING_ADMIN", true,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Admins"),International.getString("bei Warnungen (WARNING) einmal pro Woche"))));
        addParameter(efaDirekt_bnrBootsstatus_admin = new ItemTypeBoolean("NOTIFICATION_BOATSTATUS_ADMIN", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Admins"),International.getString("bei Bootsstatus-Änderungen"))));
        addParameter(efaDirekt_bnrError_bootswart = new ItemTypeBoolean("NOTIFICATION_ERROR_BOATMAINTENANCE", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Bootswarte"),International.getString("bei Fehlern") + " (ERROR)")));
        addParameter(efaDirekt_bnrWarning_bootswart = new ItemTypeBoolean("NOTIFICATION_WARNING_BOATMAINTENANCE", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Bootswarte"),International.getString("bei Warnungen (WARNING) einmal pro Woche"))));
        addParameter(efaDirekt_bnrBootsstatus_bootswart = new ItemTypeBoolean("NOTIFICATION_BOATSTATUS_BOATMAINTENANCE", false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getMessage("Benachrichtigungen verschicken an {to} {on_event}",
                International.getString("Bootswarte"),International.getString("bei Bootsstatus-Änderungen"))));
        addParameter(efaDirekt_bnrWarning_lasttime = new ItemTypeLong("NOTIFICATION_LASTWARNINGS", System.currentTimeMillis() - 7l*24l*60l*60l*1000l, 0, Long.MAX_VALUE, // one week ago
                IItemType.TYPE_INTERNAL, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("letzte Benachrichtigungen")));
        addParameter(efaDirekt_emailServer = new ItemTypeString("EMAIL_SERVER", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("SMTP-Server")));
        addParameter(efaDirekt_emailPort = new ItemTypeInteger("EMAIL_PORT", 25, 0, 65535, false,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("SMTP-Port")));
        addParameter(efaDirekt_emailUsername = new ItemTypeString("EMAIL_USERNAME", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Username")));
        addParameter(efaDirekt_emailPassword = new ItemTypeString("EMAIL_PASSWORD", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Paßwort")));
        addParameter(efaDirekt_emailAbsenderName = new ItemTypeString("EMAIL_FROMNAME", Daten.EFA_SHORTNAME,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Absender-Name")));
        addParameter(efaDirekt_emailAbsender = new ItemTypeString("EMAIL_FROMEMAIL", "",
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Absender-Adresse")));
        addParameter(efaDirekt_emailBetreffPraefix = new ItemTypeString("EMAIL_SUBJECTPREFIX", Daten.EFA_SHORTNAME,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Betreff (Präfix)")));
        addParameter(efaDirekt_emailSignatur = new ItemTypeString("EMAIL_SIGNATURE", International.getString("Diese Nachricht wurde von efa verschickt."),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_NOTIFICATIONS),
                International.getString("email")+": "+
                International.getString("Signatur")));

        // ============================= LOCALE =============================
        addParameter(language = new ItemTypeStringList("_LANGUAGE", Daten.efaBaseConfig.language,
                makeLanguageArray(STRINGLIST_VALUES), makeLanguageArray(STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getString("Sprache")));
        addParameter(defaultDistanceUnit = new ItemTypeStringList("DISTANCEUNIT_DEFAULT", DataTypeDistance.KILOMETERS,
                DataTypeDistance.makeDistanceUnitValueArray(), DataTypeDistance.makeDistanceUnitNamesArray(),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getString("Standardeinheit für Entfernungen")));
        addParameter(showGermanOptions = new ItemTypeBoolean("REGIONAL_GERMANY",
                (custSettings != null ? custSettings.activateGermanRowingOptions : International.getLanguageID().startsWith("de") ),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Regionale Funktionalitäten aktivieren für {region}.",
                International.getString("Deutschland") +
                " (" + International.getString("Rudern") + ")")));
        addParameter(showBerlinOptions = new ItemTypeBoolean("REGIONAL_BERLIN",
                (custSettings != null ? custSettings.activateBerlinRowingOptions : International.getLanguageID().startsWith("de") ),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Regionale Funktionalitäten aktivieren für {region}.",
                International.getString("Berlin") +
                " (" + International.getString("Rudern") + ")")));
        addParameter(useForRowing = new ItemTypeBoolean("CUSTUSAGE_ROWING",
                (custSettings != null ? custSettings.activateRowingOptions : true ),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Funktionalitäten aktivieren für {sport}.",
                International.getString("Rudern"))));
        addParameter(useForCanoeing = new ItemTypeBoolean("CUSTUSAGE_CANOEING",
                (custSettings != null ? custSettings.activateCanoeingOptions : false ),
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Funktionalitäten aktivieren für {sport}.",
                International.getString("Kanufahren"))));

        // ============================= TYPES =============================
        addParameter(typesResetToDefault = new ItemTypeAction("ACTION_TYPES_RESETTODEFAULT", ItemTypeAction.ACTION_TYPES_RESETTODEFAULT,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_TYPES,CATEGORY_COMMON),
                International.getString("Alle Standard-Typen zurücksetzen")));
        addParameter(typesAddAllDefaultRowingBoats = new ItemTypeAction("ACTION_ADDTYPES_ROWING", ItemTypeAction.ACTION_GENERATE_ROWING_BOAT_TYPES,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_BOAT),
                International.getMessage("Alle Standard-Bootstypen für {rowing_or_canoeing} neu hinzufügen",
                International.getString("Rudern"))));
        addParameter(typesAddAllDefaultCanoeingBoats = new ItemTypeAction("ACTION_ADDTYPES_CANOEING", ItemTypeAction.ACTION_GENERATE_CANOEING_BOAT_TYPES,
                IItemType.TYPE_PUBLIC, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_BOAT),
                International.getMessage("Alle Standard-Bootstypen für {rowing_or_canoeing} neu hinzufügen",
                International.getString("Kanufahren"))));
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


    private void addParameter(ItemType configValue) {
        if (configValues.get(configValue.getName()) != null) {
            // should never happen (program error); no need to translate
            Logger.log(Logger.ERROR, Logger.MSG_ERROR_EXCEPTION, "EfaConfig: duplicate parameter: "+configValue.getName());
        } else {
            configValues.put(configValue.getName(), configValue);
            configValueNames.add(configValue.getName());
        }
    }

    public ItemType getParameter(String name) {
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

                ItemType configValue = getParameter(name);
                if (configValue == null) {
                    Logger.log(Logger.WARNING, Logger.MSG_CORE_UNKNOWNDATAFIELD, "EfaConfig(" + getFileName() + "): "+
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
                ItemType configValue = configValues.get(keys[i]);
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

    public void checkNewConfigValues(EfaConfig newConfig) {
        String changedSettings = null;
        if (newConfig.efaDirekt_fontSize.getValue() != this.efaDirekt_fontSize.getValue() ||
            !newConfig.efaDirekt_fontStyle.getValue().equals(this.efaDirekt_fontStyle.getValue())) {
            changedSettings = (changedSettings == null ? "" : changedSettings + "\n") +
                    International.getString("Schriftgröße");
        }
        if (changedSettings != null) {
            Dialog.infoDialog(International.getString("Geänderte Einstellungen"),
                    International.getString("Folgende geänderte Einstellungen werden erst nach einem Neustart von efa wirksam:") +
                    "\n" + changedSettings);
        }
    }

    public void setExternalParameters(boolean isGuiConfigChange) {
        // first, set all external parameters that have to be set in any case, e.g. also if EfaConfig has been read after startup

        // set Debug Logging and Trace Topic (will only take effect if it has not been set through command line previously!!)
        Logger.setDebugLogging(debugLogging.getValue(),false);
        Logger.setTraceTopic(traceTopic.getValue(),false);

        // if isGuiConfigChange, i.e. if interactive changes have been made by the user, set other parameters as well
        if (!isGuiConfigChange) {
            return;
        }
        
        // Types
        EfaTypes newEfaTypes = null;
        if (Daten.efaTypes != null) {
            newEfaTypes = new EfaTypes(Daten.efaTypes);
            boolean changed = false;
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_GENDER, typesGender)) {
                changed = true;
            }
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_BOAT, typesBoat)) {
                changed = true;
            }
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_NUMSEATS, typesNumSeats)) {
                changed = true;
            }
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_RIGGING, typesRigging)) {
                changed = true;
            }
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_COXING, typesCoxing)) {
                changed = true;
            }
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_SESSION, typesSession)) {
                changed = true;
            }
            if (updateTypes(newEfaTypes, EfaTypes.CATEGORY_STATUS, typesStatus)) {
                changed = true;
            }
            if (changed) {
                newEfaTypes.writeFile();
                Dialog.infoDialog(International.getString("Bezeichnungen"),
                        International.getString("Die geänderten Bezeichnungen werden erst nach einem Neustart von efa wirksam."));
            }
        }

        // Language & efa User Data
        String newLang = language.toString();
        String newUserData = efaUserDirectory.toString();
        if (!newUserData.endsWith(Daten.fileSep)) {
            newUserData += Daten.fileSep;
        }
        boolean changedLang = Daten.efaBaseConfig.language == null || !Daten.efaBaseConfig.language.equals(newLang);
        boolean changedUserDir = Daten.efaBaseConfig.efaUserDirectory == null || !Daten.efaBaseConfig.efaUserDirectory.equals(newUserData);
        if (changedLang || changedUserDir) {
            if (changedLang) {
                Daten.efaBaseConfig.language = newLang;
            }
            if (changedUserDir) {
                if (Daten.efaBaseConfig.efaCanWrite(newUserData, false)) {
                    Daten.efaBaseConfig.efaUserDirectory = newUserData;
                } else {
                    Dialog.infoDialog(International.getString("Verzeichnis für Nutzerdaten"),
                    International.getString("efa kann in dem geänderten Verzeichnis für Nutzerdaten nicht schreiben. Die Änderung wird ignoriert."));
                    changedUserDir = false;
                }
            }
            if (changedLang || changedUserDir) {
                Daten.efaBaseConfig.writeFile();
            }
            if (changedLang) {
                if (newEfaTypes == null) {
                    newEfaTypes = new EfaTypes(Daten.efaTypes);
                }
                this.setToLanguate(newLang);
                newEfaTypes.setToLanguage(newLang);
                newEfaTypes.writeFile(false);
                Dialog.infoDialog(International.getString("Sprache"),
                        International.getString("Die geänderten Spracheinstellungen werden erst nach einem Neustart von efa wirksam."));
            }
            if (changedUserDir) {
                Dialog.infoDialog(International.getString("Verzeichnis für Nutzerdaten"),
                        International.getString("Das geänderte Verzeichnis für Nutzerdaten wird erst nach einem Neustart von efa wirksam."));
            }
        }

    }

    public boolean setToLanguate(String lang) {
        ResourceBundle bundle = null;
        if (lang != null) {
            try {
                bundle = ResourceBundle.getBundle(International.BUNDLE_NAME, new Locale(lang));
            } catch (Exception e) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDSTVALUES,
                        "Failed to set EfaConfig values for language " + lang + ".");
                return false;
            }
        } else {
            bundle = International.getResourceBundle();
        }
        efaDirekt_butFahrtBeginnen.setValueText(International.getString("Fahrt beginnen", bundle) + " >>>");
        efaDirekt_butFahrtBeenden.setValueText("<<< " + International.getString("Fahrt beenden", bundle));
        return true;
    }

    public void checkForRequiredPlugins() {
        if (efaDirekt_sunRiseSet_show.getValue() && !de.nmichael.efa.direkt.SunRiseSet.sunrisePluginInstalled()) {
            DownloadFrame.getPlugin("efa", Daten.PLUGIN_JSUNTIMES_NAME, Daten.PLUGIN_JSUNTIMES_FILE, Daten.PLUGIN_JSUNTIMES_HTML, "NoClassDefFoundError", null, false);
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
        String[] lang = International.getLanguageBundles();
        String[] languages = new String[lang.length + 1];
        languages[0] = (type == STRINGLIST_VALUES ? "" : International.getString("Default") );
        for (int i = 0; i < lang.length; i++) {
            Locale loc = new Locale(lang[i]);
            languages[i + 1] = (type == STRINGLIST_VALUES ? lang[i] : loc.getDisplayName() );
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

    private void buildTypes() {
        if (Daten.efaTypes == null) {
            return;
        }
        addParameter(typesGender = new ItemTypeHashtable<String>("_TYPES_GENDER", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_GEND),
                International.getString("Geschlecht")));
        addParameter(typesBoat = new ItemTypeHashtable<String>("_TYPES_BOAT", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_BOAT),
                International.getString("Bootsart")));
        addParameter(typesNumSeats = new ItemTypeHashtable<String>("_TYPES_NUMSEATS", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_SEAT),
                International.getString("Anzahl Bootsplätze")));
        addParameter(typesRigging = new ItemTypeHashtable<String>("_TYPES_RIGGING", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_RIGG),
                International.getString("Riggerung")));
        addParameter(typesCoxing = new ItemTypeHashtable<String>("_TYPES_COXING", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_COXD),
                International.getString("mit/ohne Stm.")));
        addParameter(typesSession = new ItemTypeHashtable<String>("_TYPES_SESSION", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_SESS),
                International.getString("Fahrtart")));
        addParameter(typesStatus = new ItemTypeHashtable<String>("_TYPES_STATUS", "", true,
                IItemType.TYPE_EXPERT, makeCategory(CATEGORY_TYPES,CATEGORY_TYPES_STAT),
                International.getString("Status")));
        iniTypes(typesGender, EfaTypes.CATEGORY_GENDER);
        iniTypes(typesBoat, EfaTypes.CATEGORY_BOAT);
        iniTypes(typesNumSeats, EfaTypes.CATEGORY_NUMSEATS);
        iniTypes(typesRigging, EfaTypes.CATEGORY_RIGGING);
        iniTypes(typesCoxing, EfaTypes.CATEGORY_COXING);
        iniTypes(typesSession, EfaTypes.CATEGORY_SESSION);
        iniTypes(typesStatus, EfaTypes.CATEGORY_STATUS);
    }

    private void iniTypes(ItemTypeHashtable<String> types, String cat) {
        for (int i=0; i<Daten.efaTypes.size(cat); i++) {
            types.put(Daten.efaTypes.getType(cat, i),
                      Daten.efaTypes.getValue(cat, i));
        }
    }

    private boolean updateTypes(EfaTypes efaTypes, String cat, ItemTypeHashtable<String> newTypes) {
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
