/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa;

import de.nmichael.efa.core.WettDefs;
import de.nmichael.efa.core.Gruppen;
import de.nmichael.efa.core.Fahrtenabzeichen;
import de.nmichael.efa.core.VereinsConfig;
import de.nmichael.efa.core.Synonyme;
import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.core.Fahrtenbuch;
import de.nmichael.efa.core.Mannschaften;
import de.nmichael.efa.core.Adressen;
import de.nmichael.efa.core.Bezeichnungen;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.EfaKeyStore;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import de.nmichael.efa.statistics.FTPWriter;
import de.nmichael.efa.statistics.PDFWriter;
import de.nmichael.efa.statistics.XMLWriter;
import java.io.*;
import java.util.jar.*;
import java.util.*;
import java.awt.Color;

// @i18n complete

public class Daten {


  public       static String EFA_SHORTNAME = "efa";                              // dummy, will be set in International.ininitalize()
  public       static String EFA_LONGNAME  = "efa - elektronisches Fahrtenbuch"; // dummy, will be set in International.ininitalize()

  public final static String VERSION = "v1.9.0_dev01"; // Version für die Ausgabe (i.d.R. gleich VERSIONID, kann aber auch Zusätze wie "alpha" o.ä. enthalten)
  public final static String VERSIONID = "1.9.0_#1";   // VersionsID: Format: "X.Y.Z_MM"; final-Version z.B. 1.4.0_00; beta-Version z.B. 1.4.0_#1
  public final static String VERSIONRELEASEDATE = "09.08.2009";  // Release Date: TT.MM.JJJJ
  public final static String PROGRAMMID = "EFA.183"; // Versions-ID für Wettbewerbsmeldungen
  public final static String PROGRAMMID_DRV = "EFADRV.183"; // Versions-ID für Wettbewerbsmeldungen
  public final static String COPYRIGHTYEAR = "09";   // aktuelles Jahr (Copyright (c) 2001-COPYRIGHTYEAR)

  public final static String EMIL_VERSION = VERSION; // Version
  public final static String EMIL_KENNUNG = "EMIL.183";
  public final static String ELWIZ_VERSION = VERSION; // Version
  public final static String EDDI_VERSION = VERSION; // Version

  public final static String EFA_JAVA_ARGUMENTS = "EFA_JAVA_ARGUMENTS"; // Environment Variable Name containing all arguments passed to the "java" command
  public       static String efa_java_arguments = null;                 // Environment Variable Contents containing all arguments passed to the "java" command
  public final static String EFADIREKT_MAINCLASS = "de.nmichael.efa.direkt.EfaDirekt";

  public final static String EFAURL = "http://efa.nmichael.de/";
  public final static String EFAWETTURL = "http://efa.rudern.de/";
  public final static String NICOLASURL = "http://www.nmichael.de/";
  public final static String EFAEMAIL = "software@nmichael.de";

  public final static String CONFIGFILE = "efa.cfg";                // ./cfg/efa.cfg            Konfigurationsdatei
  public final static String DRVCONFIGFILE = "drv.cfg";             // ./cfg/drv.cfg            DRV-Konfigurationsdatei
  public static final String WETTFILE = "wett.cfg";                 // ./cfg/wett.cfg           Konfiguration für Wettbewerbe
  public static final String BEZEICHFILE = "bezeichnungen.cfg";     // ./cfg/bezeichnungen.cfg  Konfiguration für Bezeichnungen
  public final static String VEREINSCONFIG = "verein.efv";          // ./daten/verein.efv       Konfigurationsdatei für Vereinseinstellungen
  public static final String ADRESSENFILE = "adressen.efd";         // ./daten/adressen.efd     gespeicherte Adressen von Teilnehmern
  public static final String MITGLIEDER_SYNONYM = "mitglieder.efs"; // ./daten/mitglieder.efs   Synonymdatei
  public static final String BOOTE_SYNONYM = "boote.efs";           // ./daten/boote.efs        Synonymdatei
  public static final String ZIELE_SYNONYM = "ziele.efs";           // ./daten/ziele.efs        Synonymdatei
  public static final String MANNSCHAFTENFILE = "mannschaften.efm"; // ./daten/mannschaften.efm Standardmannschaften
  public static final String FAHRTENABZEICHEN ="fahrtenabzeichen.eff"; // ./daten/fahrtenabzeichen.eff DRV Fahrtenabzeichen
  public static final String GRUPPEN ="gruppen.efg";                // ./daten/gruppen.efg      Gruppendatei
  public static final String WETTDEFS = "wettdefs.cfg";             // ./cfg/wettdefs.cfg       Wettbewerbs-Definitionen
  public static final String EFA_LICENSE = "lizenz.html";           // ./doc/lizenz.html
  public static final String PUBKEYSTORE = "keystore_pub.dat";      // ./daten/keystore_pub.dat

  public final static String DIREKTBOOTSTATUS = "bootstatus.efdb";  // ./daten/bootstatus.efdb  Status der Boote
  public final static String DIREKTNACHRICHTEN= "nachrichten.efdn"; // ./daten/nachrichten.efdn Nachrichten an Admin

  public static final String EFA_SECFILE = "efa.sec";               // ./program/efa.sec        Hash von efa.jar: für Erstellen des Admins
  public static final String EFA_RUNNUNG = "efa.run";               // ./program/efa.run        Indiz, daß efaDirekt läuft (enthält Port#)

  public static final String CLUBLOGO = "clublogo.gif";             // ./program/clublogo.gif   Clublogo für efaDirekt

  public static String efaUserHome = null;
  public static String efaLogfile = null; // Logdatei für Java-Konsole
  public static String efaMainDirectory = null;    // Efa-Hauptverzeichnis, immer mit "/" am Ende
  public static String efaProgramDirectory = null; // Programmverzeichnis, immer mit "/" am Ende     ("./program/")
  public static String efaPluginDirectory = null;  // Programmverzeichnis, immer mit "/" am Ende     ("./program/plugins")
  public static String efaDataDirectory = null;    // Efa-Datenverzeichnis, immer mit "/" am Ende    ("./daten/")
  public static String efaCfgDirectory = null;     // Efa-Configverzeichnis, immer mit "/" am Ende   ("./cfg/")
  public static String efaDocDirectory = null;     // Efa-Doku-Verzeichnis,  immer mit "/" am Ende   ("./doc/")
  public static String efaAusgabeDirectory = null; // Efa-Ausgabe-Verzeichnis, immer mit "/" am Ende ("./ausgabe/")
  public static String efaBakDirectory = null;     // Efa-Backupverzeichnis, immer mit "/" am Ende   ("./backup/")
  public static String efaTmpDirectory = null;     // Efa-Tempverzeichnis,   immer mit "/" am Ende   ("./tmp/")
  public static String efaStyleDirectory = null;   // Efa-Stylesheetverzeichnis,   mit "/" am Ende   ("./ausgabe/layout/")
  public static String fileSep = "/"; // Verzeichnis-Separator (wird in ini() ermittelt)
  public static String javaVersion = "";
  public static String jvmVersion = "";
  public static String osName = "";
  public static String osVersion = "";

  public static final int ZIELFAHRTKM = 200; // nötige Kilometer für eine Zielfahrt (in 100m)
  public static final int WAFAKM = 300;      // nötige Kilometer für eine Eintages-DRV-Wanderfahrt (in 100m)
  public static final int FART_TRAINING = 1;
  public static final int FART_REGATTA = 2;

  public final static String PLUGIN_WWW_URL = "http://efa.nmichael.de/plugins/plugins.url"; // in dieser Datei muß eine gültige Plugin-Download-URL stehen!
  public static String pluginWWWdirectory = "http://efa.nmichael.de/plugins/"; // wird automatisch auf das in der o.g. Datei stehende gesetzt
  public final static String PLUGIN_JAXP_NAME = "JAXP-Plugin";
  public final static String PLUGIN_JAXP_FILE = "jaxp.plugin";
  public final static String PLUGIN_JAXP_HTML = "jaxp.html";
  public final static String PLUGIN_FOP_NAME = "FOP-Plugin";
  public final static String PLUGIN_FOP_FILE = "fop.plugin";
  public final static String PLUGIN_FOP_HTML = "fop.html";
  public final static String PLUGIN_FTP_NAME = "FTP-Plugin";
  public final static String PLUGIN_FTP_FILE = "ftp.plugin";
  public final static String PLUGIN_FTP_HTML = "ftp.html";
  public final static String PLUGIN_EMAIL_NAME = "EMAIL-Plugin";
  public final static String PLUGIN_EMAIL_FILE = "email.plugin";
  public final static String PLUGIN_EMAIL_HTML = "email.html";
  public final static String PLUGIN_JSUNTIMES_NAME = "JSUNTIMES-Plugin";
  public final static String PLUGIN_JSUNTIMES_FILE = "jsuntimes.plugin";
  public final static String PLUGIN_JSUNTIMES_HTML = "jsuntimes.html";

  public final static String ONLINEUPDATE_INFO = "http://efa.nmichael.de/efa.eou";
  public final static String ONLINEUPDATE_INFO_DRV = "http://efa.nmichael.de/efadrv.eou";
  public final static String EFW_UPDATE_DATA = "http://efa.nmichael.de/efw.data";

  public final static int AUTO_EXIT_MIN_RUNTIME = 60; // Minuten, die efa mindestens gelaufen sein muß, damit es zu einem automatischen Beenden/Restart kommt (60)
  public final static int AUTO_EXIT_MIN_LAST_USED = 5; // Minuten, die efa mindestens nicht benutzt wurde, damit Beenden/Neustart nicht verzögert wird (muß kleiner als AUTO_EXIT_MIN_RUNTIME sein!!!) (5)
  public final static int WINDOWCLOSINGTIMEOUT = 600; // Timeout in Sekunden, nach denen im Direkt-Modus manche Fenster automatisch geschlossen werden

  public final static int MIN_FREEMEM_PERCENTAGE = 90;
  public final static int WARN_FREEMEM_PERCENTAGE = 70;
  public final static int MIN_FREEMEM_COLLECTION_THRESHOLD = 99;
  public static boolean DONT_SAVE_ANY_FILES_DUE_TO_OOME = false;
  public static boolean javaRestart = false;

  public static UserHome efaConfigUserHome; // UserHome-Konfigurationsdatei
  public static EfaConfig efaConfig;         // Konfigurationsdatei
  public static Bezeichnungen bezeichnungen; // Bezeichnungen
  public static VereinsConfig vereinsConfig; // Konfigurationsdatei für Vereinseinstellungen
  public static Adressen adressen;           // gespeicherte Teilnehmer-Adressen
  public static Synonyme synMitglieder;      // Synonymliste für Mitglieder
  public static Synonyme synBoote;           // Synonymliste für Boote
  public static Synonyme synZiele;           // Synonymliste für Ziele
  public static Mannschaften mannschaften;   // Standardmannschaften
  public static Fahrtenbuch fahrtenbuch;     // Fahrtenbuch
  public static Fahrtenabzeichen fahrtenabzeichen; // DRV Fahrtenabzeichen
  public static Gruppen gruppen;             // Gruppen
  public static WettDefs wettDefs;           // WettDefs
  public static de.nmichael.efa.direkt.NachrichtenAnAdmin nachrichten; // Nachrichten an Admin
  public static EfaKeyStore keyStore;        // KeyStore

  public static String dateiHTML = "";
  public static String dateiTXT = "";

  public static Color colorGreen = new Color(0,150,0);
  public static Color colorOrange = new Color(255,100,0);


  public static final int SUCH_NORMAL = 1;
  public static final int SUCH_ERROR = 2;
  public static int suchMode = SUCH_NORMAL;
  public static String such = "";
  public static String defaultWriteProtectPw = null;
  public static boolean such_lfdnr=true, such_datum=true, such_stm=true, such_mannsch=true,
                        such_boot=true, such_ziel=true, such_abfahrt=true, such_ankunft=true,
                        such_bootskm=true, such_mannschkm=true, such_bemerk=true, such_fahrtart=true;
  public static boolean such_errUnvollst=true, such_errKm=true, such_errUnbekRuderer=true,
                        such_errUnbekRudererOhneGast=false,
                        such_errUnbekBoot=true, such_errUnbekZiel=true, such_errWafa=true, such_errZielfahrten=true,
                        such_errVieleKm=false,
                        such_errNichtZurueckgetragen=true,such_errNichtKonfMTours=true;
  public static int     such_errVieleKmKm = 0;

  public static Backup backup=null;
  public static long efaStartTime;

  public static boolean verbose=false; // wenn true, wird stderr (Datei) auch auf stdout ausgegeben
  public static boolean exceptionTest = false; // Exceptions beim Drücken von F1 produzieren (für Exception-Test)
  public static boolean watchWindowStack  = false; // Window-Stack überwachen

  // Verhalten, wenn Checksumme nicht stimmt
  public static final int CHECKSUM_LOAD_NO_ACTION = 0;
  public static final int CHECKSUM_LOAD_PRINT_WARNING = 1;
  public static final int CHECKSUM_LOAD_SHOW_WARNING = 2;
  public static final int CHECKSUM_LOAD_REQUIRE_ADMIN = 3;
  public static final int CHECKSUM_LOAD_PRINT_WARNING_AND_AUTO_REWRITE = 4;
  public static final int CHECKSUM_LOAD_HALT_PROGRAM = 5;
  public static int actionOnChecksumLoadError = CHECKSUM_LOAD_PRINT_WARNING_AND_AUTO_REWRITE;
  public static final int CHECKSUM_SAVE_PRINT_ERROR = 0;
  public static final int CHECKSUM_SAVE_HALT_PROGRAM = 1;
  public static final int CHECKSUM_SAVE_NO_ACTION = 2;
  public static int actionOnChecksumSaveError = CHECKSUM_SAVE_PRINT_ERROR;

  // Verhalten, wenn beim Öffnen einer Datenliste diese nicht existiert
  public static final int DATENLISTE_FRAGE_NUTZER = 0;
  public static final int DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN = 1;
  public static final int DATENLISTE_FRAGE_REQUIRE_ADMIN_RETURN_FALSE_ON_NEIN = 2;
  public static int actionOnDatenlisteNotFound = DATENLISTE_FRAGE_NUTZER;

  // Verhalten, wenn beim Öffnen einer Datenliste diese sich als Backup herausstellt
  public static final int BACKUP_LOAD_WITHOUT_QUESTION = 0;
  public static final int BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN = 1;
  public static int actionOnDatenlisteIsBackup = BACKUP_LOAD_WITHOUT_QUESTION;

  // Encoding zum Lesen und Schreiben von Dateien
  public static final String ENCODING = "ISO-8859-1";

  // Applikations-IDs
  public static int applID = -1;
  public static final int APPL_EFA = 1;
  public static final int APPL_EFADIREKT = 2;
  public static final int APPL_EMIL = 3;
  public static final int APPL_ELWIZ = 4;
  public static final int APPL_EDDI = 5;
  public static final int APPL_DRV = 6;

  // Applikations-Mode
  public static final int APPL_MODE_NORMAL = 1;
  public static final int APPL_MODE_ADMIN = 2;
  public static int applMode = APPL_MODE_NORMAL;

  // Daten initialisieren; Frame setzen
  public static void ini(int _applID) {
    fahrtenbuch = null;
    fileSep = System.getProperty("file.separator");
    javaVersion = System.getProperty("java.version");
    jvmVersion = System.getProperty("java.vm.version");
    osName = System.getProperty("os.name");
    osVersion = System.getProperty("os.version");
    efaUserHome = System.getProperty("user.home");
    applID = _applID;
    efaStartTime = System.currentTimeMillis();
  }

  private static boolean checkAndCreateDirectory(String dir, Vector errors) {
    File f = new File(dir);
    if (!f.isDirectory()) {
      boolean result = f.mkdirs();
      if (result == true) {
          errors.add(International.getString("Warnung") + ": " +
                  International.getMessage("Verzeichnis '{directory}' konnte nicht gefunden werden und wurde neu erstellt.",dir));
      } else {
          errors.add(International.getString("Fehler") + ": " +
                  International.getMessage("Verzeichnis '{directory}' konnte weder gefunden, noch neu erstellt werden.",dir));
      }
      return result;
    }
    return true;
  }

  public static void mainDirIni() {
    // ./
    Daten.efaMainDirectory = System.getProperty("user.dir");
    if (!Daten.efaMainDirectory.endsWith(Daten.fileSep)) Daten.efaMainDirectory += Daten.fileSep;
    if (Daten.efaMainDirectory.endsWith("/program/") && !new File(Daten.efaMainDirectory+"program/").isDirectory())
      Daten.efaMainDirectory = Daten.efaMainDirectory.substring(0,Daten.efaMainDirectory.length()-8);
    if (Daten.efaMainDirectory.endsWith("/classes/") && !new File(Daten.efaMainDirectory+"program/").isDirectory())
      Daten.efaMainDirectory = Daten.efaMainDirectory.substring(0,Daten.efaMainDirectory.length()-8);

    String userHomeConfigDir = (Daten.efaUserHome != null ? Daten.efaUserHome : "") + (Daten.fileSep != null && !Daten.efaUserHome.endsWith(Daten.fileSep) ? Daten.fileSep : "");
    UserHome.setEfaConfigUserHomeFilename(userHomeConfigDir);
    Daten.efaConfigUserHome = new UserHome();
    if (!EfaUtil.canOpenFile(Daten.efaConfigUserHome.getFileName())) {
      Daten.efaConfigUserHome.writeFile();
    }
    Daten.efaConfigUserHome.readFile();
  }

  public static boolean dirsIni(boolean showMsg) {
    boolean dirs_ok = true;
    Vector errors = new Vector();

    // ./program
    Daten.efaProgramDirectory = Daten.efaMainDirectory+"program"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaProgramDirectory,errors) ) { dirs_ok = false; Daten.efaProgramDirectory=Daten.efaMainDirectory; }

    // ./program/plugins
    Daten.efaPluginDirectory = Daten.efaProgramDirectory+"plugins"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaPluginDirectory,errors) ) { dirs_ok = false; Daten.efaPluginDirectory=Daten.efaMainDirectory; }

    // ./daten
    Daten.efaDataDirectory = Daten.efaConfigUserHome.efaUserDirectory+"daten"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaDataDirectory,errors) ) { dirs_ok = false; Daten.efaDataDirectory=Daten.efaMainDirectory; }

    // ./cfg
    Daten.efaCfgDirectory = Daten.efaConfigUserHome.efaUserDirectory+"cfg"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaCfgDirectory,errors) ) { dirs_ok = false; Daten.efaCfgDirectory=Daten.efaMainDirectory; }

    // ./doc
    Daten.efaDocDirectory = Daten.efaMainDirectory+"doc"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaDocDirectory,errors) ) { dirs_ok = false; Daten.efaDocDirectory=Daten.efaMainDirectory; }

    // ./ausgabe
    Daten.efaAusgabeDirectory = Daten.efaMainDirectory+"ausgabe"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaAusgabeDirectory,errors) ) { dirs_ok = false; Daten.efaAusgabeDirectory=Daten.efaMainDirectory; }

    // ./ausgabe/layout
    Daten.efaStyleDirectory = Daten.efaAusgabeDirectory+"layout"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaStyleDirectory,errors) ) { dirs_ok = false; Daten.efaStyleDirectory=Daten.efaMainDirectory; }

    // ./bak
    Daten.efaBakDirectory = Daten.efaConfigUserHome.efaUserDirectory+"backup"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaBakDirectory,errors) ) { dirs_ok = false; Daten.efaBakDirectory=Daten.efaMainDirectory; }

    // ./tmp
    Daten.efaTmpDirectory = Daten.efaConfigUserHome.efaUserDirectory+"tmp"+Daten.fileSep;
    if ( !checkAndCreateDirectory(Daten.efaTmpDirectory,errors) ) { dirs_ok = false; Daten.efaTmpDirectory=Daten.efaMainDirectory; }

    if (showMsg && errors.size()>0) {
      String s = "";
      for (int i=0; i<errors.size(); i++) s += (s.length()>0 ? "\n" : "") + errors.get(i);
      Dialog.meldung( (dirs_ok ? International.getString("Warnung") : International.getString("Fehler")), s);
    }

    for (int i=0; i<errors.size(); i++) { Logger.log(Logger.WARNING,Logger.MSG_CORE_SETUPDIRS,(String)errors.get(i)); }
    if (!dirs_ok) Logger.log(Logger.ERROR,Logger.MSG_CORE_SETUPDIRS,International.getString("Ein oder mehrere Verzeichnisse konnten weder gefunden, noch neu erstellt werden!"));

    return dirs_ok;
  }

  public static Vector getEfaInfos() {
    Vector infos = new Vector();

    // efa-Infos
    infos.add("efa.version="+Daten.VERSIONID);
    if (applID != APPL_EFADIREKT) {
      infos.add("efa.dir.main="+Daten.efaMainDirectory);
      infos.add("efa.dir.user="+Daten.efaConfigUserHome.efaUserDirectory);
      infos.add("efa.dir.program="+Daten.efaProgramDirectory);
      infos.add("efa.dir.plugin="+Daten.efaPluginDirectory);
      infos.add("efa.dir.doc="+Daten.efaDocDirectory);
      infos.add("efa.dir.ausgabe="+Daten.efaAusgabeDirectory);
      infos.add("efa.dir.layout="+Daten.efaStyleDirectory);
      infos.add("efa.dir.data="+Daten.efaDataDirectory);
      infos.add("efa.dir.cfg="+Daten.efaCfgDirectory);
      infos.add("efa.dir.bak="+Daten.efaBakDirectory);
      infos.add("efa.dir.tmp="+Daten.efaTmpDirectory);
    }

    // efa Plugin-Infos
    try {
      File dir = new File(Daten.efaPluginDirectory);
      if (applID != APPL_EFADIREKT) {
        File[] files = dir.listFiles();
        for (int i=0; i<files.length; i++)
          if (files[i].isFile())
            infos.add("efa.plugin.file="+files[i].getName()+":"+files[i].length());
      }
      try {
        XMLWriter tmp = new XMLWriter(null,null);
        infos.add("efa.plugin.xml=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.xml=NOT INSTALLED");
      }
      try {
        PDFWriter tmp = new PDFWriter(null,null);
        infos.add("efa.plugin.fop=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.fop=NOT INSTALLED");
      }
      try {
        FTPWriter tmp = new FTPWriter(null);
        infos.add("efa.plugin.ftp=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.ftp=NOT INSTALLED");
      }
      try {
        de.nmichael.efa.direkt.EmailSender tmp = new de.nmichael.efa.direkt.EmailSender();
        infos.add("efa.plugin.email=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.email=NOT INSTALLED");
      }
      try {
        de.nmichael.efa.direkt.SunRiseSet tmp = new de.nmichael.efa.direkt.SunRiseSet();
        infos.add("efa.plugin.jsuntimes=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.jsuntimes=NOT INSTALLED");
      }
    } catch(Exception e) {
      Logger.log(Logger.ERROR,Logger.MSG_CORE_INFOFAILED,International.getString("Programminformationen konnten nicht ermittelt werden")+": "+e.toString());
      return null;
    }

    // Java Infos
    infos.add("java.version="+System.getProperty("java.version"));
    infos.add("java.vendor="+System.getProperty("java.vendor"));
    infos.add("java.home="+System.getProperty("java.home"));
    infos.add("java.vm.version="+System.getProperty("java.vm.version"));
    infos.add("java.vm.vendor="+System.getProperty("java.vm.vendor"));
    infos.add("java.vm.name="+System.getProperty("java.vm.name"));
    infos.add("os.name="+System.getProperty("os.name"));
    infos.add("os.arch="+System.getProperty("os.arch"));
    infos.add("os.version="+System.getProperty("os.version"));
    if (applID != APPL_EFADIREKT) {
      infos.add("user.dir="+System.getProperty("user.dir"));
      infos.add("java.class.path="+System.getProperty("java.class.path"));
    }

    // JAR methods
    if (Daten.verbose) {
      try {
        String cp = System.getProperty("java.class.path");
        while (cp != null && cp.length() > 0) {
          int pos = cp.indexOf(";");
          if (pos<0) pos = cp.indexOf(":");
          String jarfile;
          if (pos>=0) {
            jarfile = cp.substring(0,pos);
            cp = cp.substring(pos+1);
          } else {
            jarfile = cp;
            cp = null;
          }
          if (jarfile != null && jarfile.length()>0 && new File(jarfile).isFile()) {
            try {
              infos.add("java.jar.filename="+jarfile);
              JarFile jar = new JarFile(jarfile);
              Enumeration _enum = jar.entries();
              Object o;
              while ( _enum.hasMoreElements() && ( o = _enum.nextElement()) != null ) {
                infos.add("java.jar.content="+o+":"+ ( jar.getEntry(o.toString()) == null ? "null" : Long.toString(jar.getEntry(o.toString()).getSize()) ) );
              }
            } catch (Exception e) {
              Logger.log(Logger.ERROR,Logger.MSG_CORE_INFOFAILED,e.toString());
              return null;
            }
          }
        }
      } catch(Exception e) {
        Logger.log(Logger.ERROR,Logger.MSG_CORE_INFOFAILED,International.getString("Programminformationen konnten nicht ermittelt werden")+": "+e.toString());
        return null;
      }
    }
    return infos;
  }

  public static void printEfaInfos() {
    Vector infos = getEfaInfos();
    for (int i=0; infos != null && i<infos.size(); i++) Logger.log(Logger.INFO,Logger.MSG_INFO_CONFIGURATION,(String)infos.get(i));
  }

  public static String getEfaImage(int size) {
    int birthday = EfaUtil.getEfaBirthday();
    switch(size) {
      case 1:  if (birthday == 5) return "/de/nmichael/efa/img/efa_small_5jahre.gif";
               else return "/de/nmichael/efa/img/efa_small.gif";
      default: if (birthday == 5) return "/de/nmichael/efa/img/efa_logo_5jahre.gif";
               else return "/de/nmichael/efa/img/efa_logo.gif";
    }
  }

  public static void checkJavaVersion(boolean alsoCheckForOptimalVersion) {
    if (Daten.javaVersion == null) return;

    TMJ tmj = EfaUtil.string2date(Daten.javaVersion,0,0,0);
    int version = tmj.tag*100 + tmj.monat*10 + tmj.jahr;

    if (version < 140) {
      if (Dialog.yesNoDialog(International.getString("Java-Version zu alt"),
              International.getMessage("Die von Dir verwendete Java-Version {version} wird von efa "+
                             "offiziell nicht mehr unterstützt. Einige Funktionen von efa stehen "+
                             "unter dieser Java-Version nicht zur Verfügung oder funktionieren nicht "+
                             "richtig. Vom Einsatz von efa mit dieser Java-Version wird dringend abgeraten. "+
                             "Für den optimalen Einsatz von efa wird Java-Version 5 oder neuer empfohlen.\n\n"+
                             "Sollen jetzt die Download-Anleitung für eine neue Java-Version "+
                             "angezeigt werden?",Daten.javaVersion)) == Dialog.YES) {
        showJavaDownloadHints();
      }
      return;
    }

    if (!alsoCheckForOptimalVersion) return;

    if (version < 150) {
      if (Dialog.yesNoDialog(International.getString("Java-Version alt"),
              International.getMessage("Die von Dir verwendete Java-Version {version} ist bereits relativ alt. "+
                             "Für den optimalen Einsatz von efa wird Java 5 (Version 1.5.0) oder neuer empfohlen. "+
                             "efa funktioniert zwar auch mit älteren Java-Versionen weiterhin, jedoch gibt es einige "+
                             "Funktionen, die nur unter neueren Java-Versionen unterstützt werden. Außerdem werden "+
                             "Java-Fehler oft nur noch in den neueren Versionen korrigiert, so daß auch aus diesem "+
                             "Grund immer der Einsatz einer möglichst neuen Java-Version empfohlen ist.\n\n"+
                             "Sollen jetzt die Download-Anleitung für eine neue Java-Version "+
                             "angezeigt werden?",Daten.javaVersion)) == Dialog.YES) {
        showJavaDownloadHints();
      }
    }
  }

  private static void showJavaDownloadHints() {
    if (Daten.efaDocDirectory == null) return;
    Dialog.infoDialog(International.getString("Download-Anleitung"),
                      International.getString("Bitte folge in der folgenden Anleitung den Hinweisen unter Punkt 5, "+
                      "um eine neue Java-Version zu installieren."));
    Dialog.neuBrowserDlg((javax.swing.JFrame)null,International.getString("Java-Installation"),"file:"+Daten.efaDocDirectory+"installation.html");
  }

}
