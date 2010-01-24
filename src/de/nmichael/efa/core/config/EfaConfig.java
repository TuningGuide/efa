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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;

// @i18n complete

public class EfaConfig extends DatenListe {

    // Parameter Types
    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXPERT = 1;
    public static final int TYPE_PUBLIC = 2;

    // Parameter Categories
    public static final String CATEGORY_INTERNAL    = "INTERNAL";
    public static final String CATEGORY_BASE        = "BASE";
    public static final String CATEGORY_BOATHOUSE   = "BOATHOUSE";
    public static final String CATEGORY_GUI         = "GUI";
    public static final String CATEGORY_COMMON      = "COMMON";
    public static final String CATEGORY_BACKUP      = "BACKUP";
    public static final String CATEGORY_EXTTOOLS    = "EXTTOOLS";
    public static final String CATEGORY_PRINTING    = "PRINTING";
    public static final String CATEGORY_INPUT       = "INPUT";
    public static final String CATEGORY_LOCALE      = "LOCALE";
    public static final String CATEGORY_STARTSTOP   = "STARTSTOP";
    public static final String CATEGORY_PERMISSIONS = "PERMISSIONS";

    // private internal data
    private HashMap<String,String> categories;
    private HashMap<String,ConfigValue> configValues;
    
    // public configuration data
    public ConfigValue<String> letzteDatei;
    public ConfigValue<Boolean> autogenAlias;
    public ConfigValue<String> aliasFormat;
    public ConfigValue<String> bakDir;
    public ConfigValue<Boolean> bakSave;
    public ConfigValue<Boolean> bakMonat;
    public ConfigValue<Boolean> bakTag;
    public ConfigValue<Boolean> bakKonv;
    public ConfigValue<String> browser;
    public ConfigValue<String> acrobat;
    public ConfigValue<Integer> printPageWidth;
    public ConfigValue<Integer> printPageHeight;
    public ConfigValue<Integer> printLeftMargin;
    public ConfigValue<Integer> printTopMargin;
    public ConfigValue<Integer> printPageOverlap;
    public ConfigValue<ConfigTypeHashtable<String>> keys;
    public ConfigValue<Integer> countEfaStarts;
    public ConfigValue<String> registeredProgramID;
    public ConfigValue<Integer> registrationChecks;
    public ConfigValue<Boolean> autoStandardmannsch;
    public ConfigValue<Boolean> showObmann;
    public ConfigValue<Boolean> autoObmann;
    public ConfigValue<Integer> defaultObmann;
    public ConfigValue<Boolean> popupComplete;
    public ConfigValue<Boolean> correctMisspelledMitglieder;
    public ConfigValue<Boolean> correctMisspelledBoote;
    public ConfigValue<Boolean> correctMisspelledZiele;
    public ConfigValue<Boolean> skipUhrzeit;
    public ConfigValue<Boolean> skipZiel;
    public ConfigValue<Boolean> skipMannschKm;
    public ConfigValue<Boolean> skipBemerk;
    public ConfigValue<Boolean> fensterZentriert;
    public ConfigValue<Integer> windowXOffset;
    public ConfigValue<Integer> windowYOffset;
    public ConfigValue<Integer> screenWidth;
    public ConfigValue<Integer> screenHeight;
    public ConfigValue<Integer> maxDialogHeight;
    public ConfigValue<Integer> maxDialogWidth;
    public ConfigValue<String> lookAndFeel;
    public ConfigValue<Boolean> showBerlinOptions;
    public ConfigValue<String> zielfahrtSeparatorBereiche;
    public ConfigValue<String> zielfahrtSeparatorFahrten;
    public ConfigValue<String> standardFahrtart;
    public ConfigValue<Boolean> debugLogging;
    public ConfigValue<String> efaVersionLastCheck;
    public ConfigValue<String> version;
    public ConfigValue<String> direkt_letzteDatei;
    public ConfigValue<ConfigTypeHashtable> admins;
    public ConfigValue<Boolean> efaDirekt_zielBeiFahrtbeginnPflicht;
    public ConfigValue<Boolean> efaDirekt_eintragErzwingeObmann;
    public ConfigValue<Boolean> efaDirekt_eintragErlaubeNurMaxRudererzahl;
    public ConfigValue<Boolean> efaDirekt_eintragNichtAenderbarUhrzeit;
    public ConfigValue<Boolean> efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen;
    public ConfigValue<Boolean> efaDirekt_eintragNurBekannteBoote;
    public ConfigValue<Boolean> efaDirekt_eintragNurBekannteRuderer;
    public ConfigValue<Boolean> efaDirekt_eintragNurBekannteZiele;
    public ConfigValue<Integer> efaDirekt_plusMinutenAbfahrt;
    public ConfigValue<Integer> efaDirekt_minusMinutenAnkunft;
    public ConfigValue<Boolean> efaDirekt_mitgliederDuerfenReservieren;
    public ConfigValue<Boolean> efaDirekt_mitgliederDuerfenReservierenZyklisch;
    public ConfigValue<Boolean> efaDirekt_mitgliederDuerfenReservierungenEditieren;
    public ConfigValue<Boolean> efaDirekt_mitgliederDuerfenEfaBeenden;
    public ConfigValue<Boolean> efaDirekt_mitgliederDuerfenNamenHinzufuegen;
    public ConfigValue<Boolean> efaDirekt_resBooteNichtVerfuegbar;
    public ConfigValue<Boolean> efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar;
    public ConfigValue<Integer> efaDirekt_resLookAheadTime;
    public ConfigValue<String> efaDirekt_execOnEfaExit;
    public ConfigValue<TMJ> efaDirekt_exitTime;
    public ConfigValue<String> efaDirekt_execOnEfaAutoExit;
    public ConfigValue<TMJ> efaDirekt_restartTime;
    public ConfigValue<Boolean> efaDirekt_checkRunning;
    public ConfigValue<String> efaDirekt_butFahrtBeginnenFarbe;
    public ConfigValue<String> efaDirekt_butFahrtBeendenFarbe;
    public ConfigValue<String> efaDirekt_butFahrtAbbrechenFarbe;
    public ConfigValue<String> efaDirekt_butNachtragFarbe;
    public ConfigValue<String> efaDirekt_butBootsreservierungenFarbe;
    public ConfigValue<String> efaDirekt_butFahrtenbuchAnzeigenFarbe;
    public ConfigValue<String> efaDirekt_butStatistikErstellenFarbe;
    public ConfigValue<String> efaDirekt_butNachrichtAnAdminFarbe;
    public ConfigValue<String> efaDirekt_butAdminModusFarbe;
    public ConfigValue<String> efaDirekt_butSpezialFarbe;
    public ConfigValue<String> efaDirekt_butFahrtBeginnenText;
    public ConfigValue<String> efaDirekt_butFahrtBeendenText;
    public ConfigValue<String> efaDirekt_butSpezialText;
    public ConfigValue<Boolean> efaDirekt_butBootsreservierungenAnzeigen;
    public ConfigValue<Boolean> efaDirekt_butFahrtenbuchAnzeigenAnzeigen;
    public ConfigValue<Boolean> efaDirekt_butStatistikErstellenAnzeigen;
    public ConfigValue<Boolean> efaDirekt_butNachrichtAnAdminAnzeigen;
    public ConfigValue<Boolean> efaDirekt_butAdminModusAnzeigen;
    public ConfigValue<Boolean> efaDirekt_butSpezialAnzeigen;
    public ConfigValue<String> efaDirekt_butSpezialCmd;
    public ConfigValue<Boolean> efaDirekt_showButtonHotkey;
    public ConfigValue<Boolean> efaDirekt_showUhr;
    public ConfigValue<Boolean> efaDirekt_sunRiseSet_show;
/*  @todo  efaDirekt_sunRiseSet_ll = new int[8];*/
    public ConfigValue<Boolean> efaDirekt_sortByAnzahl;
    public ConfigValue<Boolean> efaDirekt_showEingabeInfos;
    public ConfigValue<Boolean> efaDirekt_showBootsschadenButton;
    public ConfigValue<Integer> efaDirekt_maxFBAnzeigenFahrten;
    public ConfigValue<Integer> efaDirekt_anzFBAnzeigenFahrten;
    public ConfigValue<Boolean> efaDirekt_FBAnzeigenAuchUnvollstaendige;
    public ConfigValue<Boolean> efaDirekt_autoNewFb_datum;
    public ConfigValue<String> efaDirekt_autoNewFb_datei;
    public ConfigValue<Integer> efaDirekt_fontSize;
    public ConfigValue<Integer> efaDirekt_fontStyle;
    public ConfigValue<Boolean> efaDirekt_colorizeInputField;
    public ConfigValue<Boolean> efaDirekt_showZielnameFuerBooteUnterwegs;
    public ConfigValue<String> efadirekt_adminLastOsCommand;
    public ConfigValue<String> efaDirekt_vereinsLogo;
    public ConfigValue<String> efaDirekt_newsText;
    public ConfigValue<Boolean> efaDirekt_startMaximized;
    public ConfigValue<Boolean> efaDirekt_fensterNichtVerschiebbar;
    public ConfigValue<Boolean> efaDirekt_immerImVordergrund;
    public ConfigValue<Boolean> efaDirekt_immerImVordergrundBringToFront;
    public ConfigValue<Boolean> efaDirekt_bnrError_admin;
    public ConfigValue<Boolean> efaDirekt_bnrError_bootswart;
    public ConfigValue<Boolean> efaDirekt_bnrWarning_admin;
    public ConfigValue<Boolean> efaDirekt_bnrWarning_bootswart;
    public ConfigValue<Boolean> efaDirekt_bnrBootsstatus_admin;
    public ConfigValue<Boolean> efaDirekt_bnrBootsstatus_bootswart;
    public ConfigValue<Long> efaDirekt_bnrWarning_lasttime;
    public ConfigValue<String> efaDirekt_emailServer;
    public ConfigValue<String> efaDirekt_emailAbsender;
    public ConfigValue<String> efaDirekt_emailUsername;
    public ConfigValue<String> efaDirekt_emailPassword;
    public ConfigValue<String> efaDirekt_emailAbsenderName;
    public ConfigValue<String> efaDirekt_emailBetreffPraefix;
    public ConfigValue<String> efaDirekt_emailSignatur;
    public ConfigValue<String> efaDirekt_lockEfaShowHtml;
    public ConfigValue<Boolean> efaDirekt_lockEfaVollbild;
    public ConfigValue<TMJ> efaDirekt_lockEfaFromDatum;
    public ConfigValue<TMJ> efaDirekt_lockEfaFromZeit;
    public ConfigValue<TMJ> efaDirekt_lockEfaUntilDatum;
    public ConfigValue<TMJ> efaDirekt_lockEfaUntilZeit;
    public ConfigValue<Boolean> efaDirekt_locked;

    // public default values

    // Default-Obmann für ungesteuerte Boote
    public static final int OBMANN_NR1 = 1;
    public static final int OBMANN_SCHLAG = 2;


    // some default values
    private static String[] DEFAULT_BROWSER = {
        "/usr/bin/firefox",
        "/usr/bin/mozilla",
        "/usr/bin/netscape",
        "c:\\Programme\\Mozilla Firefox\\firefox.exe",
        "c:\\Programme\\Internet Explorer\\iexplore.exe",
        "c:\\Program Files\\Mozilla Firefox\\firefox.exe",
        "c:\\Program Files\\Internet Explorer\\iexplore.exe"
    };
    private static String[] DEFAULT_ACROBAT = {
        "/usr/bin/acroread",
        "c:\\Programme\\Adobe\\Reader 9.0\\Reader\\AcroRd32.exe",
        "c:\\Program Files\\Adobe\\Reader 9.0\\Reader\\AcroRd32.exe"
    };


    public static final String KENNUNG190 = "##EFA.190.CONFIGURATION##";

    public EfaConfig(String filename) {
        super(filename, 0, 0, false);
        kennung = KENNUNG190;
        Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFACONFIG, "EfaConfig("+filename+")");
        initialize();
    }

    // clean-up and re-initialize data structures
    private void initialize() {
        categories   = new HashMap<String,String>();
        configValues = new HashMap<String,ConfigValue>();
        iniCategories();
        iniParameters();
    }

    // initializa all category strings
    private void iniCategories() {
        categories.put(CATEGORY_INTERNAL,    International.getString("intern"));
        categories.put(CATEGORY_BASE,        International.getString("efa-Basis"));
        categories.put(CATEGORY_BOATHOUSE,   International.getString("efa-Bootshaus"));
        categories.put(CATEGORY_GUI,         International.getString("Erscheinungsbild"));
        categories.put(CATEGORY_COMMON,      International.getString("Allgemein"));
        categories.put(CATEGORY_BACKUP,      International.getString("Backup"));
        categories.put(CATEGORY_EXTTOOLS,    International.getString("externe Programme"));
        categories.put(CATEGORY_PRINTING,    International.getString("Drucken"));
        categories.put(CATEGORY_INPUT,       International.getString("Eingabe"));
        categories.put(CATEGORY_LOCALE,      International.getString("Regionale Anpassung"));
        categories.put(CATEGORY_STARTSTOP,   International.getString("Starten und Beenden"));
        categories.put(CATEGORY_PERMISSIONS, International.getString("Berechtigungen"));
    }

    // initialize all configuration parameters with their default values
    private void iniParameters() {
        addParameter(version = new ConfigValue<String>("EFA_VERSION", "100",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa version"));
        addParameter(efaVersionLastCheck = new ConfigValue<String>("EFA_VERSION_LASTCHECK", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa last checked for new version"));
        addParameter(countEfaStarts = new ConfigValue<Integer>("EFA_STARTS_COUNTER", 0,
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa start counter"));
        addParameter(registeredProgramID = new ConfigValue<String>("EFA_REGISTRATION_PROGRAMMID", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa registered programm ID"));
        addParameter(registrationChecks = new ConfigValue<Integer>("EFA_REGISTRATION_CHECKS", 0,
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "efa registration checks counter"));
        addParameter(letzteDatei = new ConfigValue<String>("LASTFILE_EFABASE", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "Last logbook opened by efa Base"));
        addParameter(direkt_letzteDatei = new ConfigValue<String>("LASTFILE_EFABOATHOUSE", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                "Last logbook opened by efa Boathouse"));

        addParameter(autogenAlias = new ConfigValue<Boolean>("ALIAS_AUTOGENERATE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON),
                International.getString("Eingabe-Kürzel automatisch beim Anlegen neuer Mitglieder generieren")));
        addParameter(aliasFormat = new ConfigValue<String>("ALIAS_FORMAT", "{V1}{V2}-{N1}",
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON),
                International.getString("Format der Eingabe-Kürzel")));
        addParameter(bakDir = new ConfigValue<String>("BACKUP_DIRECTORY", Daten.efaMainDirectory + "backup" + Daten.fileSep,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup-Verzeichnis")));
        addParameter(bakSave = new ConfigValue<Boolean>("BACKUP_ON_SAVE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup bei jedem Speichern")));
        addParameter(bakMonat = new ConfigValue<Boolean>("BACKUP_ON_MONTHLY_SAVE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim ersten Speichern jeden Monat")));
        addParameter(bakTag = new ConfigValue<Boolean>("BACKUP_ON_DAYLY_SAVE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim ersten Speichern jeden Tag")));
        addParameter(bakKonv = new ConfigValue<Boolean>("BACKUP_ON_CONVERTING", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_BACKUP),
                International.getString("Backup beim Konvertieren")));
        addParameter(browser = new ConfigValue<String>("WEBBROWSER", searchForProgram(DEFAULT_BROWSER),
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_EXTTOOLS),
                International.getString("Webbrowser")));
        addParameter(acrobat = new ConfigValue<String>("ACROBAT_READER", searchForProgram(DEFAULT_ACROBAT),
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_EXTTOOLS),
                International.getString("Acrobat Reader")));
        addParameter(printPageWidth = new ConfigValue<Integer>("PRINT_PAGEWIDTH", 210,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenbreite")));
        addParameter(printPageHeight = new ConfigValue<Integer>("PRINT_PAGEHEIGHT", 297,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenhöhe")));
        addParameter(printLeftMargin = new ConfigValue<Integer>("PRINT_PAGEMARGIN_LEFTRIGHT", 15,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("linker und rechter Rand")));
        addParameter(printTopMargin = new ConfigValue<Integer>("PRINT_PAGEMARGIN_TOPBOTTOM", 15,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("oberer und unterer Rand")));
        addParameter(printPageOverlap = new ConfigValue<Integer>("PRINT_PAGEOVERLAP", 5,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_PRINTING),
                International.getString("Seitenüberlappung")));
        addParameter(keys = new ConfigValue<ConfigTypeHashtable<String>>("HOTKEYS", new ConfigTypeHashtable<String>("foobar"),
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Tastenbelegungen für Bemerkungs-Feld")));
        addParameter(autoStandardmannsch = new ConfigValue<Boolean>("DEFAULTCREW_AUTOSELECT", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standardmannschaft automatisch eintragen")));
        addParameter(showObmann = new ConfigValue<Boolean>("CREWSHEAD_SHOW", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Obmann-Auswahlliste anzeigen")));
        addParameter(autoObmann = new ConfigValue<Boolean>("CREWSHEAD_AUTOSELECT", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Obmann bei Eingabe automatisch auswählen")));
        addParameter(defaultObmann = new ConfigValue<Integer>("CREWSHEAD_DEFAULT", OBMANN_NR1,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standard-Obmann für ungesteuerte Boote")));
        addParameter(popupComplete = new ConfigValue<Boolean>("AUTOCOMPLETEPOPUP_SHOW", true,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Beim Vervollständigen Popup-Liste anzeigen")));
        addParameter(correctMisspelledMitglieder = new ConfigValue<Boolean>("SPELLING_CHECKMEMBERS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Mitglieder"))));
        addParameter(correctMisspelledBoote = new ConfigValue<Boolean>("SPELLING_CHECKBOATS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Boote"))));
        addParameter(correctMisspelledZiele = new ConfigValue<Boolean>("SPELLING_CHECKDESTINATIONS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getMessage("Fahrtenbucheinträge auf Tippfehler prüfen für {types}",
                International.getString("Ziele"))));
        addParameter(skipUhrzeit = new ConfigValue<Boolean>("INPUT_SKIPTIME", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BASE,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Uhrzeit"))));
        addParameter(skipZiel = new ConfigValue<Boolean>("INPUT_SKIPDESTINATION", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BASE,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Ziel"))));
        addParameter(skipMannschKm = new ConfigValue<Boolean>("INPUT_SKIPCREWKM", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BASE,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Mannschafts-Km"))));
        addParameter(skipBemerk = new ConfigValue<Boolean>("INPUT_SKIPCOMMENTS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BASE,CATEGORY_INPUT),
                International.getMessage("Eingabefeld '{field}' überspringen",
                International.getString("Bemerkungen"))));
        addParameter(fensterZentriert = new ConfigValue<Boolean>("WINDOW_CENTERED", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Alle Fenster in Bildschirmmitte zentrieren")));
        addParameter(windowXOffset = new ConfigValue<Integer>("WINDOW_OFFSETX", 0,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Fenster-Offset")+ " X" +
                " (" + International.getString("Pixel") + ")"));
        addParameter(windowYOffset = new ConfigValue<Integer>("WINDOW_OFFSETY", 0,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Fenster-Offset")+ " Y" +
                " (" + International.getString("Pixel") + ")"));
        addParameter(screenWidth = new ConfigValue<Integer>("SCREEN_WIDTH", 0,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Bildschirmbreite") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(screenHeight = new ConfigValue<Integer>("SCREEN_HEIGHT", 0,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Bildschirmhöhe") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(maxDialogHeight = new ConfigValue<Integer>("DIALOG_MAXHEIGHT", 0,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("maximale Dialog-Höhe") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(maxDialogWidth = new ConfigValue<Integer>("DIALOG_MAXWIDTH", 0,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("maximale Dialog-Breite") +
                " (" + International.getString("Pixel") + ")"));
        addParameter(lookAndFeel = new ConfigValue<String>("LOOK_AND_FEEL", "", // @todo: default value was "%DEFAULT%" before!
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_GUI),
                International.getString("Look & Feel")));
        addParameter(showBerlinOptions = new ConfigValue<Boolean>("REGIONAL_BERLIN", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_LOCALE),
                International.getMessage("Regionale Funktionalitäten aktivieren für {region}.",
                International.getString("Berlin"))));
        addParameter(zielfahrtSeparatorBereiche = new ConfigValue<String>("ZIELFAHRT_SEPARATORBEREICHE", ",",
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.onlyFor("Trennzeichen für Bereiche einer Zielfahrt","de")));
        addParameter(zielfahrtSeparatorFahrten = new ConfigValue<String>("ZIELFAHRT_SEPARATORFAHRTEN", ";", // @todo: was "/" before
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.onlyFor("Trennzeichen für Zielfahrten","de")));
        addParameter(standardFahrtart = new ConfigValue<String>("SESSIONTYPE_DEFAULT", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON,CATEGORY_INPUT),
                International.getString("Standard-Fahrtart")));
        addParameter(debugLogging = new ConfigValue<Boolean>("DEBUG_LOGGING", false,
                TYPE_EXPERT, makeCategory(CATEGORY_COMMON),
                International.getString("Debug-Logging aktivieren")));
        addParameter(admins = new ConfigValue<ConfigTypeHashtable>("ADMINS", new ConfigTypeHashtable<Admin>(new Admin("foobar","foobar")),
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Administratoren")));
        addParameter(efaDirekt_zielBeiFahrtbeginnPflicht = new ConfigValue<Boolean>("MUST_DESTINATION_AT_SESSIONSTART", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Ziel muß bereits bei Fahrtbeginn angegeben werden")));
        addParameter(efaDirekt_eintragErzwingeObmann = new ConfigValue<Boolean>("MUST_SELECT_CREWSHEAD", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Obmann muß ausgewählt werden")));
        addParameter(efaDirekt_eintragErlaubeNurMaxRudererzahl = new ConfigValue<Boolean>("ALLOW_ONLY_MAXCREWNUMBER", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Nur für das Boot maximal mögliche Anzahl an Ruderern erlauben")));
        addParameter(efaDirekt_eintragNichtAenderbarUhrzeit = new ConfigValue<Boolean>("NOTEDITABLE_TIME", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Vorgeschlagene Uhrzeiten können nicht geändert werden")));
        addParameter(efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen = new ConfigValue<Boolean>("NOTEDITABLE_KMFORKNOWNDESTINATIONS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Vorgeschlagene Kilometer bei bekannten Zielen können nicht geändert werden")));
        addParameter(efaDirekt_eintragNurBekannteBoote = new ConfigValue<Boolean>("ONLY_KNOWNBOATS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Boote"))));
        addParameter(efaDirekt_eintragNurBekannteRuderer = new ConfigValue<Boolean>("ONLY_KNOWNPERSONS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Personen"))));
        addParameter(efaDirekt_eintragNurBekannteZiele = new ConfigValue<Boolean>("ONLY_KNOWNDESTINATIONS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getMessage("Beim Eintrag von Fahrten nur bekannte Namen erlauben für {type}",
                International.getString("Ziele"))));
        addParameter(efaDirekt_plusMinutenAbfahrt = new ConfigValue<Integer>("TIME_SESSIONSTART_ADD", 10,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Für Abfahrt x Minuten zur aktuellen Zeit hinzuaddieren")));
        addParameter(efaDirekt_minusMinutenAnkunft = new ConfigValue<Integer>("TIME_SESSIONEND_SUBSTRACT", 10,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_INPUT),
                International.getString("Für Ankunft x Minuten von aktueller Zeit abziehen")));
        addParameter(efaDirekt_mitgliederDuerfenReservieren = new ConfigValue<Boolean>("ALLOW_MEMBERS_BOATRESERVATION_ONCE", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Boote reservieren")));
        addParameter(efaDirekt_mitgliederDuerfenReservierenZyklisch = new ConfigValue<Boolean>("ALLOW_MEMBERS_BOATRESERVATION_REOCCURRING", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Boote reservieren") +
                " (" + International.getString("einmalige Reservierungen") + ")"));
        addParameter(efaDirekt_mitgliederDuerfenReservierungenEditieren = new ConfigValue<Boolean>("ALLOW_MEMBERS_BOATRESERVATION_CHANGE", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Bootsreservierungen verändern und löschen")));
        addParameter(efaDirekt_mitgliederDuerfenEfaBeenden = new ConfigValue<Boolean>("ALLOW_MEMBERS_EXITEFA", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen efa beenden")));
        addParameter(efaDirekt_mitgliederDuerfenNamenHinzufuegen = new ConfigValue<Boolean>("ALLOW_MEMBERS_ADDNAMES", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_PERMISSIONS),
                International.getString("Mitglieder dürfen Namen zur Mitgliederliste hinzufügen")));
        addParameter(efaDirekt_resBooteNichtVerfuegbar = new ConfigValue<Boolean>("SHOWASNOTAVAILABLE_RESERVEDBOATS", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Reservierte Boote als 'nicht verfügbar' anzeigen")));
        addParameter(efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar = new ConfigValue<Boolean>("SHOWASNOTAVAILABLE_MULTIDAY_REGATTA", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Boote auf Regatta, Trainingslager oder Mehrtagesfahrt als 'nicht verfügbar' anzeigen")));
        addParameter(efaDirekt_resLookAheadTime = new ConfigValue<Integer>("RESERVATION_LOOKAHEADTIME", 120,
                TYPE_EXPERT, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_COMMON),
                International.getString("Bei Fahrtbeginn auf Reservierungen bis zu x Minuten in der Zukunft prüfen")));
        addParameter(efaDirekt_execOnEfaExit = new ConfigValue<String>("EFAEXIT_EXEC", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Folgendes Kommando beim Beenden von efa durch Mitglieder ausführen")));
        addParameter(efaDirekt_exitTime = new ConfigValue<TMJ>("EFAEXIT_TIME", new TMJ(-1,-1,-1),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Uhrzeit zum automatischen Beenden von efa")));
        addParameter(efaDirekt_execOnEfaAutoExit = new ConfigValue<String>("EFAEXIT_EXECONAUTOEXIT", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Folgendes Kommando beim automatischen Beenden von efa ausführen")));
        addParameter(efaDirekt_restartTime = new ConfigValue<TMJ>("EFAEXIT_RESTARTTIME", new TMJ(4,0,-1),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_STARTSTOP),
                International.getString("Uhrzeit zum automatischen Neustart von efa")));
        addParameter(efaDirekt_butFahrtBeginnenFarbe = new ConfigValue<String>("BUTTON_STARTSESSION_COLOR", "CCFFCC",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Fahrt beginnen"))));
        addParameter(efaDirekt_butFahrtBeendenFarbe = new ConfigValue<String>("BUTTON_FINISHSESSION_COLOR", "CCFFCC",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Fahrt beenden"))));
        addParameter(efaDirekt_butFahrtAbbrechenFarbe = new ConfigValue<String>("BUTTON_CANCELSESSION_COLOR", "FFCCCC",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Fahrt abbrechen"))));
        addParameter(efaDirekt_butNachtragFarbe = new ConfigValue<String>("BUTTON_LATEENTRY_COLOR", "CCFFFF",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Nachtrag"))));
        addParameter(efaDirekt_butBootsreservierungenFarbe = new ConfigValue<String>("BUTTON_BOATRESERVATIONS_COLOR", "FFFFCC",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Bootsreservierungen"))));
        addParameter(efaDirekt_butFahrtenbuchAnzeigenFarbe = new ConfigValue<String>("BUTTON_SHOWLOGBOOK_COLOR", "CCCCFF",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Fahrtenbuch anzeigen"))));
        addParameter(efaDirekt_butStatistikErstellenFarbe = new ConfigValue<String>("BUTTON_CREATESTATISTICS_COLOR", "CCCCFF",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Statistiken erstellen"))));
        addParameter(efaDirekt_butNachrichtAnAdminFarbe = new ConfigValue<String>("BUTTON_MESSAGETOADMIN_COLOR", "FFF197",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Nachricht an Admin"))));
        addParameter(efaDirekt_butAdminModusFarbe = new ConfigValue<String>("BUTTON_ADMINMODE_COLOR", "CCCCCC",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Admin-Modus"))));
        addParameter(efaDirekt_butSpezialFarbe = new ConfigValue<String>("BUTTON_SPECIAL_COLOR", "CCCCCC",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Farbe für den Button '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_butFahrtBeginnenText = new ConfigValue<String>("BUTTON_STARTSESSION_TEXT", International.getString("Fahrt beginnen")+" >>>",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Text für den Button '{button}'",
                International.getString("Fahrt beginnen"))));
        addParameter(efaDirekt_butFahrtBeendenText = new ConfigValue<String>("BUTTON_FINISHSESSION_TEXT", "<<< "+International.getString("Fahrt beenden"),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Text für den Button '{button}'",
                International.getString("Fahrt beenden"))));
        addParameter(efaDirekt_butSpezialText = new ConfigValue<String>("BUTTON_SPECIAL_TEXT", International.getString("Spezial-Button"),
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Text für den Button '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_butBootsreservierungenAnzeigen = new ConfigValue<Boolean>("BUTTON_BOATRESERVATIONS_SHOW", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Button '{button}' anzeigen",
                International.getString("Bootsreservierungen"))));
        addParameter(efaDirekt_butFahrtenbuchAnzeigenAnzeigen = new ConfigValue<Boolean>("BUTTON_SHOWLOGBOOK_SHOW", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Button '{button}' anzeigen",
                International.getString("Fahrtenbuch anzeigen"))));
        addParameter(efaDirekt_butStatistikErstellenAnzeigen = new ConfigValue<Boolean>("BUTTON_CREATESTATISTICS_SHOW", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Button '{button}' anzeigen",
                International.getString("Statistiken erstellen"))));
        addParameter(efaDirekt_butNachrichtAnAdminAnzeigen = new ConfigValue<Boolean>("BUTTON_MESSAGETOADMIN_SHOW", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Button '{button}' anzeigen",
                International.getString("Nachricht an Admin"))));
        addParameter(efaDirekt_butAdminModusAnzeigen = new ConfigValue<Boolean>("BUTTON_ADMINMODE_SHOW", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Button '{button}' anzeigen",
                International.getString("Admin-Modus"))));
        addParameter(efaDirekt_butSpezialAnzeigen = new ConfigValue<Boolean>("BUTTON_SPECIAL_SHOW", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Button '{button}' anzeigen",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_butSpezialCmd = new ConfigValue<String>("BUTTON_SPECIAL_COMMAND", "",
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getMessage("Auszuführendes Kommando für '{button}'",
                International.getString("Spezial-Button"))));
        addParameter(efaDirekt_showButtonHotkey = new ConfigValue<Boolean>("BUTTON_SHOWHOTKEYS", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Hotkeys für Buttons anzeigen")));
        addParameter(efaDirekt_showUhr = new ConfigValue<Boolean>("SHOW_TIME", true,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Uhr anzeigen")));
        addParameter(efaDirekt_sunRiseSet_show = new ConfigValue<Boolean>("SHOW_SUNRISESET", false,
                TYPE_PUBLIC, makeCategory(CATEGORY_BOATHOUSE,CATEGORY_GUI),
                International.getString("Sonnenaufgangs- und -untergangszeit anzeigen")));

/*
    @todo  efaDirekt_sunRiseSet_ll = new int[8];
    public ConfigValue<Boolean> efaDirekt_sortByAnzahl;
    public ConfigValue<Boolean> efaDirekt_showEingabeInfos;
    public ConfigValue<Boolean> efaDirekt_showBootsschadenButton;
    public ConfigValue<Integer> efaDirekt_maxFBAnzeigenFahrten;
    public ConfigValue<Integer> efaDirekt_anzFBAnzeigenFahrten;
    public ConfigValue<Boolean> efaDirekt_FBAnzeigenAuchUnvollstaendige;
    public ConfigValue<Boolean> efaDirekt_autoNewFb_datum;
    public ConfigValue<String> efaDirekt_autoNewFb_datei;
    public ConfigValue<Integer> efaDirekt_fontSize;
    public ConfigValue<Integer> efaDirekt_fontStyle;
    public ConfigValue<Boolean> efaDirekt_colorizeInputField;
    public ConfigValue<Boolean> efaDirekt_showZielnameFuerBooteUnterwegs;
    public ConfigValue<String> efadirekt_adminLastOsCommand;
    public ConfigValue<String> efaDirekt_vereinsLogo;
    public ConfigValue<String> efaDirekt_newsText;
    public ConfigValue<Boolean> efaDirekt_startMaximized;
    public ConfigValue<Boolean> efaDirekt_fensterNichtVerschiebbar;
    public ConfigValue<Boolean> efaDirekt_immerImVordergrundBringToFront;
    public ConfigValue<Boolean> efaDirekt_bnrError_admin;
    public ConfigValue<Boolean> efaDirekt_bnrError_bootswart;
    public ConfigValue<Boolean> efaDirekt_bnrWarning_admin;
    public ConfigValue<Boolean> efaDirekt_bnrWarning_bootswart;
    public ConfigValue<Boolean> efaDirekt_bnrBootsstatus_admin;
    public ConfigValue<Boolean> efaDirekt_bnrBootsstatus_bootswart;
    public ConfigValue<Long> efaDirekt_bnrWarning_lasttime;
    public ConfigValue<String> efaDirekt_emailServer;
    public ConfigValue<String> efaDirekt_emailAbsender;
    public ConfigValue<String> efaDirekt_emailUsername;
    public ConfigValue<String> efaDirekt_emailPassword;
    public ConfigValue<String> efaDirekt_emailAbsenderName;
    public ConfigValue<String> efaDirekt_emailBetreffPraefix;
    public ConfigValue<String> efaDirekt_emailSignatur;
    public ConfigValue<String> efaDirekt_lockEfaShowHtml;
    public ConfigValue<Boolean> efaDirekt_lockEfaVollbild;
    public ConfigValue<TMJ> efaDirekt_lockEfaFromDatum;
    public ConfigValue<TMJ> efaDirekt_lockEfaFromZeit;
    public ConfigValue<TMJ> efaDirekt_lockEfaUntilDatum;
    public ConfigValue<TMJ> efaDirekt_lockEfaUntilZeit;
    public ConfigValue<Boolean> efaDirekt_locked;
*/

/*
        addParameter( = new ConfigValue<>("", "",
                TYPE_, makeCategory(CATEGORY_),
                International.getString("")));
*/
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
        }
    }

    public ConfigValue getParameter(String name) {
        return configValues.get(name);
    }

    public String[] getParameterNames() {
        String[] names = new String[configValues.size()];
        names = configValues.keySet().toArray(names);
        return names;
    }

    public String getCategoryName(String key) {
        return categories.get(key);
    }

    public static String[] getCategoryKeyArray(String keystring) {
        Vector v = EfaUtil.split(keystring, ':');
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

                configValue.setValueFromString(value);

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
                fwrite(configValue.getName() + "=" + configValue.getValue().toString() + "\n");
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

    private String searchForProgram(String[] programs) {
        for (int i = 0; i < programs.length; i++) {
            if (new File(programs[i]).isFile()) {
                return programs[i];
            }
        }
        return "";
    }

}
