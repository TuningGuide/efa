package de.nmichael.efa.drv;

import java.io.*;
import java.util.Hashtable;
import de.nmichael.efa.DatenListe;
import de.nmichael.efa.EfaUtil;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class DRVConfig extends DatenListe {


  public static final String KENNUNG150 = "##EFA.150.DRVKONFIGURATION##";

  public static final byte VERSION = 3;

  public static final int ACTION_LIST = 1;
  public static final int ACTION_GET = 2;
  public static final int ACTION_UPLOAD = 3;
  public static final int ACTION_REJECT = 4;
  public static final int ACTION_UPLCERT = 5;

  public static final String MELDUNGEN_FA_FILE = "meldungen.idx";
  public static final String MELDUNGEN_WS_FILE = "meldungen_ws.idx";
  public static final String TEILNEHMER_FILE = "teilnehmer.efh";
  public static final String MELDESTATISTIK_FA_FILE = "meldestatistik.ems";
  public static final String MELDESTATISTIK_WS_FILE = "meldestatistik_ws.ems";
  public static final String KEYSTORE_FILE  = "keystore.dat";

  // Daten, die gespeichert werden
  public String datenDirectory;
  public int aktJahr;
  public String schluessel;
  public String efw_script;
  public String efw_user;
  public String efw_password;
  public String acrobat;
  public String openssl;
  public boolean darfFAbearbeiten;
  public boolean darfWSbearbeiten;
  public int eur_meld_erw;
  public int eur_meld_jug;
  public int eur_nadel_erw_silber;
  public int eur_nadel_erw_gold;
  public int eur_nadel_jug_silber;
  public int eur_nadel_jug_gold;
  public int eur_stoff_erw;
  public int eur_stoff_jug;
  public boolean testmode;
  public boolean readOnlyMode;

  // Daten, die *nicht* gespeichert werden
  public char[] keyPassword;
  public MeldungenIndex meldungenIndex;
  public Teilnehmer teilnehmer;
  public Meldestatistik meldestatistik;

  // Konstruktor
  public DRVConfig(String pdat) {
    super(pdat,0,0,false);
    kennung = KENNUNG150;
    reset();
    this.backupEnabled = true;
  }

  // Einstellungen zurücksetzen
  void reset() {
    datenDirectory = "";
    aktJahr = 0;
    schluessel = "";
    efw_script = "";
    efw_user = "";
    efw_password = "";
    acrobat = "";
    openssl = "";
    darfFAbearbeiten = true;
    darfWSbearbeiten = true;
    eur_meld_erw = 200;
    eur_meld_jug = 150;
    eur_nadel_erw_silber = 360;
    eur_nadel_erw_gold = 475;
    eur_nadel_jug_silber = 300;
    eur_nadel_jug_gold = 300;
    eur_stoff_erw = 481;
    eur_stoff_jug = 348;
    testmode = false;
    readOnlyMode = false;
  }


  // Konfigurationsdatei einlesen
  public synchronized boolean readFile() {
    if (openFile() && readEinstellungen() && closeFile()) return true;
    return false;
  }

  public synchronized boolean writeFile(boolean fuerKonvertieren) {
    if (openWFile(fuerKonvertieren) && writeEinstellungen() && closeWFile()) return true;
    return false;
  }
  public boolean writeFile() {
    return writeFile(false);
  }


  public boolean readEinstellungen() {
    reset();

    // Konfiguration lesen
    String s;
    try {
      while ((s = freadLine()) != null) {
        s = s.trim();

        if (s.startsWith("DATEN_DIRECTORY="))
            datenDirectory=s.substring(16,s.length()).trim();
        if (s.startsWith("AKTJAHR="))
            aktJahr=EfaUtil.string2int(s.substring(8,s.length()).trim(),0);
        if (s.startsWith("SCHLUESSEL="))
            schluessel=s.substring(11,s.length()).trim();
        if (s.startsWith("EFW_SCRIPT="))
            efw_script=s.substring(11,s.length()).trim();
        if (s.startsWith("EFW_USER="))
            efw_user=s.substring(9,s.length()).trim();
        if (s.startsWith("EFW_PASSWORD="))
            efw_password=s.substring(13,s.length()).trim();
        if (s.startsWith("ACROBAT="))
            acrobat=s.substring(8,s.length()).trim();
        if (s.startsWith("OPENSSL="))
            openssl=s.substring(8,s.length()).trim();
        if (s.startsWith("FA_BEARBEITEN="))
            darfFAbearbeiten=s.substring(14,s.length()).trim().equals("+");
        if (s.startsWith("WS_BEARBEITEN="))
            darfWSbearbeiten=s.substring(14,s.length()).trim().equals("+");
        if (s.startsWith("EUR_MELD_ERW="))
            eur_meld_erw=EfaUtil.string2int(s.substring(13,s.length()).trim(),200);
        if (s.startsWith("EUR_MELD_JUG="))
            eur_meld_jug=EfaUtil.string2int(s.substring(13,s.length()).trim(),150);
        if (s.startsWith("EUR_NADEL_ERW_SILBER="))
            eur_nadel_erw_silber=EfaUtil.string2int(s.substring(21,s.length()).trim(),360);
        if (s.startsWith("EUR_NADEL_ERW_GOLD="))
            eur_nadel_erw_gold=EfaUtil.string2int(s.substring(19,s.length()).trim(),475);
        if (s.startsWith("EUR_NADEL_JUG_SILBER="))
            eur_nadel_jug_silber=EfaUtil.string2int(s.substring(21,s.length()).trim(),300);
        if (s.startsWith("EUR_NADEL_JUG_GOLD="))
            eur_nadel_jug_gold=EfaUtil.string2int(s.substring(19,s.length()).trim(),300);
        if (s.startsWith("EUR_STOFF_ERW="))
            eur_stoff_erw=EfaUtil.string2int(s.substring(14,s.length()).trim(),481);
        if (s.startsWith("EUR_STOFF_JUG="))
            eur_stoff_jug=EfaUtil.string2int(s.substring(14,s.length()).trim(),348);
        if (s.startsWith("TESTMODE="))
            testmode=s.substring(9,s.length()).equals("+");
        if (s.startsWith("READONLYMODE="))
            readOnlyMode=s.substring(13,s.length()).equals("+");
      }
    } catch(IOException e) {
      try {
        fclose(false);
      } catch(Exception ee) {
        return false;
      }
    }
    return true;
  }


  // Konfigurationsdatei speichern
  public boolean writeEinstellungen() {
    // Datei schreiben
    try {
      fwrite("DATEN_DIRECTORY=" + datenDirectory + "\n");
      fwrite("AKTJAHR=" + aktJahr + "\n");
      fwrite("SCHLUESSEL=" + schluessel + "\n");
      fwrite("EFW_SCRIPT=" + efw_script + "\n");
      fwrite("EFW_USER=" + efw_user + "\n");
      fwrite("EFW_PASSWORD=" + efw_password + "\n");
      fwrite("ACROBAT=" + acrobat + "\n");
      fwrite("OPENSSL=" + openssl + "\n");
      fwrite("FA_BEARBEITEN=" + (darfFAbearbeiten ? "+" : "-") + "\n");
      fwrite("WS_BEARBEITEN=" + (darfWSbearbeiten ? "+" : "-") + "\n");
      fwrite("EUR_MELD_ERW=" + Integer.toString(eur_meld_erw) + "\n");
      fwrite("EUR_MELD_JUG=" + Integer.toString(eur_meld_jug) + "\n");
      fwrite("EUR_NADEL_ERW_SILBER=" + Integer.toString(eur_nadel_erw_silber) + "\n");
      fwrite("EUR_NADEL_ERW_GOLD=" + Integer.toString(eur_nadel_erw_gold) + "\n");
      fwrite("EUR_NADEL_JUG_SILBER=" + Integer.toString(eur_nadel_jug_silber) + "\n");
      fwrite("EUR_NADEL_JUG_GOLD=" + Integer.toString(eur_nadel_jug_gold) + "\n");
      fwrite("EUR_STOFF_ERW=" + Integer.toString(eur_stoff_erw) + "\n");
      fwrite("EUR_STOFF_JUG=" + Integer.toString(eur_stoff_jug) + "\n");
      fwrite("TESTMODE=" + (testmode ? "+" : "-") + "\n");
      fwrite("READONLYMODE=" + (readOnlyMode ? "+" : "-") + "\n");
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


  public String makeScriptRequestString(int action, String param1, String param2, String param3, String param4) {
    String saction = null;
    switch(action) {
      case ACTION_LIST   : saction = "efa_listMeldungen"; break;
      case ACTION_GET    : saction = "efa_getMeldung"; break;
      case ACTION_UPLOAD : saction = "efa_bestaetigeMeldung"; break;
      case ACTION_REJECT : saction = "efa_rejectMeldung"; break;
      case ACTION_UPLCERT: saction = "efa_uploadCert"; break;
    }
    if (saction == null) return null;
    String s = efw_script+"?verband=drv&agent=efa&name="+efw_user+"&password="+efw_password+"&action="+saction;
    if (param1 != null) s += "&"+param1;
    if (param2 != null) s += "&"+param2;
    if (param3 != null) s += "&"+param3;
    if (param4 != null) s += "&"+param4;
    return s;
  }


}
