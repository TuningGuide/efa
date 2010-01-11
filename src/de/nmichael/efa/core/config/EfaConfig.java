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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;

// @i18n complete

public class EfaConfig extends DatenListe {

    // Parameter Types
    public static final int TYPE_ = -1; // @todo remove!!!
    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXPERT = 1;
    public static final int TYPE_PUBLIC = 2;

    // Parameter Categories
    public static final String CATEGORY_           = "!!!@TODO!!!";
    public static final String CATEGORY_INTERNAL   = "INTERNAL";
    public static final String CATEGORY_BOATHOUSE  = "BOATHOUSE";
    public static final String CATEGORY_GUI        = "GUI";
    public static final String CATEGORY_COMMON     = "COMMON";
    public static final String CATEGORY_BACKUP     = "BACKUP";
    public static final String CATEGORY_EXTTOOLS   = "EXTTOOLS";
    public static final String CATEGORY_PRINTING   = "PRINTING";
    
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
    public ConfigValue<Hashtable> admins;
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
        categories.put(CATEGORY_INTERNAL,  International.getString("intern"));
        categories.put(CATEGORY_BOATHOUSE, International.getString("Bootshaus"));
        categories.put(CATEGORY_GUI,       International.getString("Erscheinungsbild"));
        categories.put(CATEGORY_COMMON,    International.getString("Allgemein"));
        categories.put(CATEGORY_BACKUP,    International.getString("Backup"));
        categories.put(CATEGORY_EXTTOOLS,  International.getString("externe Programme"));
        categories.put(CATEGORY_PRINTING,  International.getString("Drucken"));
    }

    // initialize all configuration parameters with their default values
    private void iniParameters() {
        addParameter(letzteDatei = new ConfigValue<String>("LAST_EFABASE_FILE", "",
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                International.getString("zuletzt geöffnetes Fahrtenbuch")));
        addParameter(autogenAlias = new ConfigValue<Boolean>("ALIAS_AUTO_GENERATE", true,
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
                TYPE_PUBLIC, makeCategory(CATEGORY_COMMON),
                International.getString("Tastenbelegungen für Bemerkungs-Feld")));
        addParameter(countEfaStarts = new ConfigValue<Integer>("EFA_STARTS_COUNTER", 0,
                TYPE_INTERNAL, makeCategory(CATEGORY_INTERNAL),
                International.getString("Anzahl der Starts von efa")));


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
