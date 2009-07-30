package de.nmichael.efa;

import java.io.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class VereinsConfig extends DatenListe {

  public static final String KENNUNG090 = "##EFA.090.VEREIN##";
  public static final String KENNUNG150 = "##EFA.150.VEREIN##";

  public static final String VEREINSNAME="VEREINSNAME";
  public static final String VEREINSORT="VEREINSORT";
  public static final String LRVNAME="LRVNAME";
  public static final String MITGL_DRV="MITGL_DRV";
  public static final String MITGL_SRV="MITGL_SRV";
  public static final String MITGL_ADH="MITGL_ADH";
  public static final String MITGLIEDSNUMMERDRV="MITGLIEDSNUMMERDRV";
  public static final String ZIELBEREICH="ZIELBEREICH";
  public static final String MELDENDERNAME="MELDENDERNAME";
  public static final String MELDENDEREMAIL="MELDENDEREMAIL";
  public static final String MELDENDERBANK="MELDENDERBANK";
  public static final String MELDENDERBLZ="MELDENDERBLZ";
  public static final String MELDENDERKTO="MELDENDERKTO";
  public static final String VERSANDNAME="VERSANDNAME";
  public static final String VERSANDSTRASSE="VERSANDSTRASSE";
  public static final String VERSANDORT="VERSANDORT";
  public static final String USER_DRV="USER_DRV";
  public static final String USER_LRV="USER_LRV";

  public static final int VERBAND_DRV = 1;
  public static final int VERBAND_LRVBLN = 2;
  public static final int ACTION_EINSENDEN = 1;
  public static final int ACTION_ABRUFEN = 2;
  public static final int ACTION_QNRLIST = 3;

  private static boolean _reusePasswordForNextRequest = false;
  private static char[] pwd = null;

  public String vereinsname="";
  public String vereinsort="";
  public String lrvname="";
  public boolean mitglDRV=true;
  public boolean mitglSRV=false;
  public boolean mitglADH=false;
  public String mitgliedsnummerDRV="";
  public String zielbereich="";
  public String meldenderName="";
  public String meldenderEmail="";
  public String meldenderBank="";
  public String meldenderBLZ="";
  public String meldenderKto="";
  public String versandName="";
  public String versandStrasse="";
  public String versandOrt="";
  public String userDRV="";
  public String userLRV="";

  // Konstruktor
  public VereinsConfig(String pdat) {
    super(pdat,0,0,true);
    kennung = KENNUNG150;
  }

  // Einstellungen aus dem Fahrtenbuch auslesen
  public synchronized boolean readEinstellungen() {
    String s;
    vereinsname="";
    vereinsort="";
    lrvname="";
    mitglDRV=true;
    mitglSRV=false;
    mitglADH=false;
    mitgliedsnummerDRV="";
    zielbereich="";
    meldenderName="";
    meldenderEmail="";
    meldenderBank="";
    meldenderBLZ="";
    meldenderKto="";
    versandName="";
    versandStrasse="";
    versandOrt="";
    userDRV="";
    userLRV="";

    try {
      while ( (s = freadLine()) != null) {
        s = s.trim();

        if (s.startsWith(VEREINSNAME+"="))
          vereinsname=s.substring(VEREINSNAME.length()+1,s.length());

        if (s.startsWith(VEREINSORT+"="))
          vereinsort=s.substring(VEREINSORT.length()+1,s.length());

        if (s.startsWith(LRVNAME+"="))
          lrvname=s.substring(LRVNAME.length()+1,s.length());

        if (s.startsWith(MITGL_DRV+"="))
          mitglDRV=s.substring(MITGL_DRV.length()+1,s.length()).trim().equals("+");

        if (s.startsWith(MITGL_SRV+"="))
          mitglSRV=s.substring(MITGL_SRV.length()+1,s.length()).trim().equals("+");

        if (s.startsWith(MITGL_ADH+"="))
          mitglADH=s.substring(MITGL_ADH.length()+1,s.length()).trim().equals("+");

        if (s.startsWith(MITGLIEDSNUMMERDRV+"="))
          mitgliedsnummerDRV=s.substring(MITGLIEDSNUMMERDRV.length()+1,s.length());

        if (s.startsWith(ZIELBEREICH+"="))
          zielbereich=s.substring(ZIELBEREICH.length()+1,s.length());

        if (s.startsWith(MELDENDERNAME+"="))
          meldenderName=s.substring(MELDENDERNAME.length()+1,s.length());

        if (s.startsWith(MELDENDEREMAIL+"="))
          meldenderEmail=s.substring(MELDENDEREMAIL.length()+1,s.length());

        if (s.startsWith(MELDENDERBANK+"="))
          meldenderBank=s.substring(MELDENDERBANK.length()+1,s.length());

        if (s.startsWith(MELDENDERBLZ+"="))
          meldenderBLZ=s.substring(MELDENDERBLZ.length()+1,s.length());

        if (s.startsWith(MELDENDERKTO+"="))
          meldenderKto=s.substring(MELDENDERKTO.length()+1,s.length());

        if (s.startsWith(VERSANDNAME+"="))
          versandName=s.substring(VERSANDNAME.length()+1,s.length());

        if (s.startsWith(VERSANDSTRASSE+"="))
          versandStrasse=s.substring(VERSANDSTRASSE.length()+1,s.length());

        if (s.startsWith(VERSANDORT+"="))
          versandOrt=s.substring(VERSANDORT.length()+1,s.length());

        if (s.startsWith(USER_DRV+"="))
          userDRV=s.substring(USER_DRV.length()+1,s.length());

        if (s.startsWith(USER_LRV+"="))
          userLRV=s.substring(USER_LRV.length()+1,s.length());

      }
    } catch(IOException e) {
      Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
      return false;
    }
    return true;
  }

  public synchronized boolean writeEinstellungen() {
    try {
      fwrite(VEREINSNAME + "=" + vereinsname + "\n");
      fwrite(VEREINSORT + "=" + vereinsort + "\n");
      fwrite(LRVNAME + "=" + lrvname + "\n");
      fwrite(MITGL_DRV + "=" + (mitglDRV ? "+" : "-") + "\n");
      fwrite(MITGL_SRV + "=" + (mitglSRV ? "+" : "-") + "\n");
      fwrite(MITGL_ADH + "=" + (mitglADH ? "+" : "-") + "\n");
      fwrite(MITGLIEDSNUMMERDRV + "=" + mitgliedsnummerDRV + "\n");
      fwrite(ZIELBEREICH + "=" + zielbereich + "\n");
      fwrite(MELDENDERNAME + "=" + meldenderName + "\n");
      fwrite(MELDENDEREMAIL + "=" + meldenderEmail + "\n");
      fwrite(MELDENDERBANK + "=" + meldenderBank + "\n");
      fwrite(MELDENDERBLZ + "=" + meldenderBLZ + "\n");
      fwrite(MELDENDERKTO + "=" + meldenderKto + "\n");
      fwrite(VERSANDNAME + "=" + versandName + "\n");
      fwrite(VERSANDSTRASSE + "=" + versandStrasse + "\n");
      fwrite(VERSANDORT + "=" + versandOrt + "\n");
      fwrite(USER_DRV + "=" + userDRV + "\n");
      fwrite(USER_LRV + "=" + userLRV + "\n");
    }
    catch (IOException e) {
      Dialog.error("Datei '" + dat + "' kann nicht geschrieben werden!");
      return false;
    }
    return true;
  }


  public void askForZielbereichOnStart() {
    if (zielbereich.length() > 0) return;
    zielbereich = Dialog.inputDialog("eigener Zielbereich","Nur für Berliner Vereine (andere Vereine bitte leer lassen!):\n\n"+
                                                           "Bitte gib für den LRV Berlin Sommer-Fahrten-Wettbewerb den\n"+
                                                           "Zielbereich ein, in dem sich das Bootshaus Deines Vereins\n"+
                                                           "befindet, z.B. \"3\".\n"+
                                                           "(Diese Angabe wird dazu benutzt, um Fehleingaben der Zielbereiche\n"+
                                                           "zu verhindern.)");
    if (zielbereich == null) zielbereich="";
    int i = EfaUtil.string2date(zielbereich,0,0,0).tag;
    if (i<1 || i>10) zielbereich = "";
    else zielbereich = Integer.toString(i);
    writeFile();

    // testen, ob alle Zielbereiche korrekt eingetragen sind
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().ziele != null)
      Daten.fahrtenbuch.getDaten().ziele.checkAllZielbereiche(zielbereich);
  }



  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN: 090 -> 150
        if (s != null && s.trim().startsWith(KENNUNG090)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"090");
          // Datei lesen
          String verein="";
          try {
            while ((s = freadLine()) != null) {
              s= s.trim();
              if (s.startsWith("VEREIN=")) {
                verein = s.substring(7,s.length());
                break;
              }
            }
            resetf();
            readEinstellungen();
            this.userLRV = verein;
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG150;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(kennung)) {
          Dialog.error("Datei '"+dat+"' hat ungültiges Format!");
          fclose(false);
          return false;
        }
      }
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht gelesen werden!");
      return false;
    }
    return true;
  }

  public void reusePasswordForNextRequest() {
    _reusePasswordForNextRequest = true;
  }

  public String makeScriptRequestString(int _verband, int _action, String param1, String param2) {
    if (Daten.wettDefs == null) {
      Dialog.error("Es ist keine Wettbewerbskonfiguration vorhanden.\n"+
                   "Bitte öffne im Menü 'Administration' den Punkt 'Wettbewerbskonfiguration'\n"+
                   "und aktualisiere die Konfigurationsdaten.");
      return null;
    }
    String verbandName = null;
    String verband = null;
    String user = null;
    switch(_verband) {
      case VERBAND_DRV:    verbandName = "DRV";        verband = "drv";    user = userDRV; break;
      case VERBAND_LRVBLN: verbandName = "LRV Berlin"; verband = "lrvbln"; user = userLRV; break;
    }
    if (verband == null || user == null) return null;
    if (user.length()==0) {
      Dialog.error("Es ist kein Benutzername zum Abrufen der Daten konfiguriert.\n"+
                   "Bitte öffne im Menü 'Administration' den Punkt 'Wettbewerbskonfiguration'\n"+
                   "und trage einen Benutzernamen für efaWett ein.");
      return null;
    }

    String action = null;
    String script = null;
    switch(_action) {
      case ACTION_EINSENDEN:
        action = "efa_uploadMeldung";
        if (_verband == VERBAND_DRV) script = Daten.wettDefs.efw_url_einsenden;
        break;
      case ACTION_ABRUFEN :
        action = "efa_getBestaetigung";
        if (_verband == VERBAND_DRV) script = Daten.wettDefs.efw_url_abrufen;
        break;
      case ACTION_QNRLIST :
        action = "efa_getAllQnr";
        if (_verband == VERBAND_DRV) script = Daten.wettDefs.efw_url_abrufen;
        break;
    }
    if (action == null || script == null) return null;
    if (script.length()==0) {
      Dialog.error("Es ist keine Adresse zum Abrufen der Daten konfiguriert.\n"+
                   "Bitte öffne im Menü 'Administration' den Punkt 'Wettbewerbskonfiguration'\n"+
                   "und aktualisiere die Konfigurationsdaten.");
      return null;
    }

    if (!_reusePasswordForNextRequest) {
      pwd = null;
    }
    if (pwd == null) {
      pwd = EnterPasswordFrame.enterPassword(Dialog.frameCurrent(),
          "Bitte gib das efaWett-Paßwort für den Benutzernamen '" + user +
                                             "' beim "+verbandName+" an:");
    }
    if (pwd == null) return null;
    String password = new String(pwd);
    String s = script+"?verband="+verband+"&agent=efa&username="+user+"&password="+password+"&action="+action;
    if (param1 != null) s += "&"+param1;
    if (param2 != null) s += "&"+param2;
    _reusePasswordForNextRequest = false;
    return s;
  }

}
