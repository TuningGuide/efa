package de.nmichael.efa;

import java.io.*;
import java.util.Vector;


public class Bezeichnungen extends DatenListe {

  public static int GESCHLECHT_MAENNLICH = -1,
                    GESCHLECHT_WEIBLICH  = -1;

  public static int BART_AGIG = -1,
                    BART_BGIG = -1,
                    BART_CGIG = -1,
                    BART_DGIG = -1,
                    BART_EGIG = -1,
                    BART_INRIGGER = -1,
                    BART_RENNBOOT = -1,
                    BART_SKIFF = -1,
                    BART_WHERRY = -1,
                    BART_TRIMMY = -1,
                    BART_BARKE = -1,
                    BART_KIRCHBOOT = -1,
                    BART_MOTORBOOT = -1,
                    BART_ERGO = -1;

  public static int BANZAHL_1 = -1,
                    BANZAHL_2 = -1,
                    BANZAHL_3 = -1,
                    BANZAHL_4 = -1,
                    BANZAHL_5 = -1,
                    BANZAHL_6 = -1,
                    BANZAHL_8 = -1;

  public static int BRIGGER_SKULL = -1,
                    BRIGGER_RIEMEN = -1;

  public static int BSTM_MIT = -1,
                    BSTM_OHNE = -1;

  public static int FAHRT_NORMAL = -1,
                    FAHRT_TRAINING = -1,
                    FAHRT_REGATTA = -1,
                    FAHRT_JUMREGATTA = -1,
                    FAHRT_TRAININGSLAGER = -1,
                    FAHRT_AUSBILDUNG = -1,
                    FAHRT_KILOMETERNACHTRAG = -1,
                    FAHRT_MOTORBOOT = -1,
                    FAHRT_ERGO = -1,
                    FAHRT_MEHRTAGESFAHRT = -1;

  public String andere;
  public String gast;
  public Bezeichnung geschlecht;
  public Bezeichnung bArt;
  public Bezeichnung bAnzahl;
  public Bezeichnung bRigger;
  public Bezeichnung bStm;
  public Bezeichnung fahrtart;


  public static final String KENNUNG120 = "##EFA.120.BEZEICHNUNGEN##";
  public static final String KENNUNG140 = "##EFA.140.BEZEICHNUNGEN##";
  public static final String KENNUNG174 = "##EFA.174.BEZEICHNUNGEN##";
  public static final String KENNUNG180 = "##EFA.180.BEZEICHNUNGEN##";
  public static final String KENNUNG181 = "##EFA.181.BEZEICHNUNGEN##";

  // Konstruktor
  public Bezeichnungen(String pdat) {
    super(pdat,0,0,false);
    kennung = KENNUNG181;
    reset();
  }

  // Einstellungen zurücksetzen
  void reset() {
    andere = "andere";
    gast = "Gast";
    geschlecht = new Bezeichnung();
    bArt = new Bezeichnung();
    bAnzahl = new Bezeichnung();
    bRigger = new Bezeichnung();
    bStm = new Bezeichnung();
    fahrtart = new Bezeichnung();
  }


  private int getValue(String s, Bezeichnung values, String field, int defaultValue) {
    if (s.startsWith(field+"=")) {
      return values.add( s.substring( field.length()+1 , s.length() ) );
    } else {
      return defaultValue;
    }
  }

  public synchronized boolean readEinstellungen() {
    reset();

    // Konfiguration lesen
    String s;
    Bezeichnung fahrt_tmp = new Bezeichnung();
    try {
      while ((s = freadLine()) != null) {
        s = s.trim();
        if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren

        if (s.startsWith("ANDERE="))
            andere=s.substring(7,s.length()).trim();
          if (s.startsWith("GAST="))
              gast=s.substring(5,s.length()).trim();

        GESCHLECHT_MAENNLICH = getValue(s,geschlecht,"GESCHLECHT_MAENNLICH",GESCHLECHT_MAENNLICH);
        GESCHLECHT_WEIBLICH  = getValue(s,geschlecht,"GESCHLECHT_WEIBLICH",GESCHLECHT_WEIBLICH);

        BART_AGIG      = getValue(s,bArt,"BART_AGIG",BART_AGIG);
        BART_BGIG      = getValue(s,bArt,"BART_BGIG",BART_BGIG);
        BART_CGIG      = getValue(s,bArt,"BART_CGIG",BART_CGIG);
        BART_DGIG      = getValue(s,bArt,"BART_DGIG",BART_DGIG);
        BART_EGIG      = getValue(s,bArt,"BART_EGIG",BART_EGIG);
        BART_INRIGGER  = getValue(s,bArt,"BART_INRIGGER",BART_INRIGGER);
        BART_RENNBOOT  = getValue(s,bArt,"BART_RENNBOOT",BART_RENNBOOT);
        BART_SKIFF     = getValue(s,bArt,"BART_SKIFF",BART_SKIFF);
        BART_WHERRY    = getValue(s,bArt,"BART_WHERRY",BART_WHERRY);
        BART_TRIMMY    = getValue(s,bArt,"BART_TRIMMY",BART_TRIMMY);
        BART_BARKE     = getValue(s,bArt,"BART_BARKE",BART_BARKE);
        BART_KIRCHBOOT = getValue(s,bArt,"BART_KIRCHBOOT",BART_KIRCHBOOT);
        BART_MOTORBOOT = getValue(s,bArt,"BART_MOTORBOOT",BART_MOTORBOOT);
        BART_ERGO      = getValue(s,bArt,"BART_ERGO",BART_ERGO);
        /* weitere */    getValue(s,bArt,"BART",0);

        BANZAHL_1 = getValue(s,bAnzahl,"BANZAHL_1",BANZAHL_1);
        BANZAHL_2 = getValue(s,bAnzahl,"BANZAHL_2",BANZAHL_2);
        BANZAHL_3 = getValue(s,bAnzahl,"BANZAHL_3",BANZAHL_3);
        BANZAHL_4 = getValue(s,bAnzahl,"BANZAHL_4",BANZAHL_4);
        BANZAHL_5 = getValue(s,bAnzahl,"BANZAHL_5",BANZAHL_5);
        BANZAHL_6 = getValue(s,bAnzahl,"BANZAHL_6",BANZAHL_6);
        BANZAHL_8 = getValue(s,bAnzahl,"BANZAHL_8",BANZAHL_8);
        /*weitere*/ getValue(s,bAnzahl,"BANZAHL",0);

        BRIGGER_SKULL  = getValue(s,bRigger,"BRIGGER_SKULL",BRIGGER_SKULL);
        BRIGGER_RIEMEN = getValue(s,bRigger,"BRIGGER_RIEMEN",BRIGGER_RIEMEN);
        /* weitere */    getValue(s,bRigger,"BRIGGER",0);

        BSTM_MIT  = getValue(s,bStm,"BSTM_MIT",BSTM_MIT);
        BSTM_OHNE = getValue(s,bStm,"BSTM_OHNE",BSTM_OHNE);
        /*weitere*/ getValue(s,bStm,"BSTM",0);

        FAHRT_NORMAL            = getValue(s,fahrt_tmp,"FAHRT_NORMAL",FAHRT_NORMAL);
        FAHRT_TRAINING          = getValue(s,fahrt_tmp,"FAHRT_TRAINING",FAHRT_TRAINING);
        FAHRT_REGATTA           = getValue(s,fahrt_tmp,"FAHRT_REGATTA",FAHRT_REGATTA);
        FAHRT_JUMREGATTA        = getValue(s,fahrt_tmp,"FAHRT_JUMREGATTA",FAHRT_JUMREGATTA);
        FAHRT_TRAININGSLAGER    = getValue(s,fahrt_tmp,"FAHRT_TRAININGSLAGER",FAHRT_TRAININGSLAGER);
        FAHRT_AUSBILDUNG        = getValue(s,fahrt_tmp,"FAHRT_AUSBILDUNG",FAHRT_AUSBILDUNG);
        FAHRT_KILOMETERNACHTRAG = getValue(s,fahrt_tmp,"FAHRT_KILOMETERNACHTRAG",FAHRT_KILOMETERNACHTRAG);
        FAHRT_MOTORBOOT         = getValue(s,fahrt_tmp,"FAHRT_MOTORBOOT",FAHRT_MOTORBOOT);
        FAHRT_ERGO              = getValue(s,fahrt_tmp,"FAHRT_ERGO",FAHRT_ERGO);
        FAHRT_MEHRTAGESFAHRT    = getValue(s,fahrt_tmp,"FAHRT_MEHRTAGESFAHRT",FAHRT_MEHRTAGESFAHRT);
        /* weitere */             getValue(s,fahrt_tmp,"FAHRT",0);

      }

      bArt.add(andere);
      bAnzahl.add(andere);
      bRigger.add(andere);
      bStm.add(andere);

      // Fahrtarten müssen immer NORMAL als ersten Eintrag und MEHRTAGESFAHRT als letzten Eintrag
      // haben, daher hier der Umweg über Zwischenspeicherung in fahrt_tmp.
      if (FAHRT_NORMAL            >= 0) FAHRT_NORMAL = fahrtart.add(fahrt_tmp.get(FAHRT_NORMAL));
      else FAHRT_NORMAL = fahrtart.add("normale Fahrt");
      if (FAHRT_TRAINING          >= 0) FAHRT_TRAINING = fahrtart.add(fahrt_tmp.get(FAHRT_TRAINING));
      if (FAHRT_REGATTA           >= 0) FAHRT_REGATTA = fahrtart.add(fahrt_tmp.get(FAHRT_REGATTA));
      if (FAHRT_JUMREGATTA        >= 0) FAHRT_JUMREGATTA = fahrtart.add(fahrt_tmp.get(FAHRT_JUMREGATTA));
      if (FAHRT_TRAININGSLAGER    >= 0) FAHRT_TRAININGSLAGER = fahrtart.add(fahrt_tmp.get(FAHRT_TRAININGSLAGER));
      if (FAHRT_AUSBILDUNG        >= 0) FAHRT_AUSBILDUNG = fahrtart.add(fahrt_tmp.get(FAHRT_AUSBILDUNG));
      if (FAHRT_KILOMETERNACHTRAG >= 0) FAHRT_KILOMETERNACHTRAG = fahrtart.add(fahrt_tmp.get(FAHRT_KILOMETERNACHTRAG));
      if (FAHRT_MOTORBOOT         >= 0) FAHRT_MOTORBOOT = fahrtart.add(fahrt_tmp.get(FAHRT_MOTORBOOT));
      if (FAHRT_ERGO              >= 0) FAHRT_ERGO = fahrtart.add(fahrt_tmp.get(FAHRT_ERGO));
      for (int i=0; i<fahrt_tmp.size(); i++) { // Fahrtarten ohne besondere Zuordnung hinzufügen
        if (!fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_NORMAL)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_TRAINING)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_REGATTA)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_JUMREGATTA)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_AUSBILDUNG)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_TRAININGSLAGER)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_KILOMETERNACHTRAG)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_MOTORBOOT)) &&
            !fahrt_tmp.get(i).equals(fahrtart.get(FAHRT_ERGO)) &&
            !fahrt_tmp.get(i).equals(fahrt_tmp.get(FAHRT_MEHRTAGESFAHRT))) fahrtart.add(fahrt_tmp.get(i));
      }
      if (FAHRT_MEHRTAGESFAHRT >= 0) FAHRT_MEHRTAGESFAHRT = fahrtart.add(fahrt_tmp.get(FAHRT_MEHRTAGESFAHRT));
      else FAHRT_MEHRTAGESFAHRT = fahrtart.add("Mehrtagesfahrt");

    } catch(IOException e) {
      try {
        fclose(false);
      } catch(Exception ee) {
        return false;
      }
    }
    return true;
  }




  public synchronized boolean writeEinstellungen() {
      try {
        fwrite("ANDERE="+andere+"\n");
        fwrite("GAST="+gast+"\n");

        for (int i=0; i<geschlecht.size(); i++) {
          if (i == GESCHLECHT_MAENNLICH) fwrite("GESCHLECHT_MAENNLICH="+geschlecht.get(i)+"\n"); else
          if (i == GESCHLECHT_WEIBLICH ) fwrite("GESCHLECHT_WEIBLICH="+geschlecht.get(i)+"\n");
        }

        for (int i=0; i<bArt.size(); i++) {
          if (i == BART_AGIG           ) fwrite("BART_AGIG="+bArt.get(i)+"\n"); else
          if (i == BART_BGIG           ) fwrite("BART_BGIG="+bArt.get(i)+"\n"); else
          if (i == BART_CGIG           ) fwrite("BART_CGIG="+bArt.get(i)+"\n"); else
          if (i == BART_DGIG           ) fwrite("BART_DGIG="+bArt.get(i)+"\n"); else
          if (i == BART_EGIG           ) fwrite("BART_EGIG="+bArt.get(i)+"\n"); else
          if (i == BART_INRIGGER       ) fwrite("BART_INRIGGER="+bArt.get(i)+"\n"); else
          if (i == BART_RENNBOOT       ) fwrite("BART_RENNBOOT="+bArt.get(i)+"\n"); else
          if (i == BART_SKIFF          ) fwrite("BART_SKIFF="+bArt.get(i)+"\n"); else
          if (i == BART_WHERRY         ) fwrite("BART_WHERRY="+bArt.get(i)+"\n"); else
          if (i == BART_TRIMMY         ) fwrite("BART_TRIMMY="+bArt.get(i)+"\n"); else
          if (i == BART_BARKE          ) fwrite("BART_BARKE="+bArt.get(i)+"\n"); else
          if (i == BART_KIRCHBOOT      ) fwrite("BART_KIRCHBOOT="+bArt.get(i)+"\n"); else
          if (i == BART_MOTORBOOT      ) fwrite("BART_MOTORBOOT="+bArt.get(i)+"\n"); else
          if (i == BART_ERGO           ) fwrite("BART_ERGO="+bArt.get(i)+"\n"); else
          if (!bArt.get(i).equals(andere)) fwrite("BART="+bArt.get(i)+"\n"); // "andere" nicht in die Datei schreiben
        }

        for (int i=0; i<bAnzahl.size(); i++) {
          if (i == BANZAHL_1           ) fwrite("BANZAHL_1="+bAnzahl.get(i)+"\n"); else
          if (i == BANZAHL_2           ) fwrite("BANZAHL_2="+bAnzahl.get(i)+"\n"); else
          if (i == BANZAHL_3           ) fwrite("BANZAHL_3="+bAnzahl.get(i)+"\n"); else
          if (i == BANZAHL_4           ) fwrite("BANZAHL_4="+bAnzahl.get(i)+"\n"); else
          if (i == BANZAHL_5           ) fwrite("BANZAHL_5="+bAnzahl.get(i)+"\n"); else
          if (i == BANZAHL_6           ) fwrite("BANZAHL_6="+bAnzahl.get(i)+"\n"); else
          if (i == BANZAHL_8           ) fwrite("BANZAHL_8="+bAnzahl.get(i)+"\n"); else
          if (!bAnzahl.get(i).equals(andere)) fwrite("BANZAHL="+bAnzahl.get(i)+"\n"); // "andere" nicht in die Datei schreiben
        }

        for (int i=0; i<bRigger.size(); i++) {
          if (i == BRIGGER_SKULL       ) fwrite("BRIGGER_SKULL="+bRigger.get(i)+"\n"); else
          if (i == BRIGGER_RIEMEN      ) fwrite("BRIGGER_RIEMEN="+bRigger.get(i)+"\n"); else
          if (!bRigger.get(i).equals(andere)) fwrite("BRIGGER="+bRigger.get(i)+"\n"); // "andere" nicht in die Datei schreiben
        }

        for (int i=0; i<bStm.size(); i++) {
          if (i == BSTM_MIT            ) fwrite("BSTM_MIT="+bStm.get(i)+"\n"); else
          if (i == BSTM_OHNE           ) fwrite("BSTM_OHNE="+bStm.get(i)+"\n"); else
          if (!bStm.get(i).equals(andere)) fwrite("BSTM="+bStm.get(i)+"\n"); // "andere" nicht in die Datei schreiben
        }

        for (int i=0; i<fahrtart.size(); i++) {
          if (i == FAHRT_NORMAL           ) fwrite("FAHRT_NORMAL="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_TRAINING         ) fwrite("FAHRT_TRAINING="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_REGATTA          ) fwrite("FAHRT_REGATTA="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_JUMREGATTA       ) fwrite("FAHRT_JUMREGATTA="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_TRAININGSLAGER   ) fwrite("FAHRT_TRAININGSLAGER="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_AUSBILDUNG       ) fwrite("FAHRT_AUSBILDUNG="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_KILOMETERNACHTRAG) fwrite("FAHRT_KILOMETERNACHTRAG="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_MOTORBOOT        ) fwrite("FAHRT_MOTORBOOT="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_ERGO             ) fwrite("FAHRT_ERGO="+fahrtart.get(i)+"\n"); else
          if (i == FAHRT_MEHRTAGESFAHRT   ) fwrite("FAHRT_MEHRTAGESFAHRT="+fahrtart.get(i)+"\n"); else
                                            fwrite("FAHRT="+fahrtart.get(i)+"\n");
        }

      } catch(IOException e) {
        Dialog.error("Datei '"+dat+"' kann nicht geschrieben werden!");
        return false;
      }
    return true;
  }


  public boolean createNewIfDoesntExist() {
    if ( (new File(dat)).exists() ) return true;

    // Datei existiert noch nicht: Neu erstellen mit Default-Werten
    reset();

    GESCHLECHT_MAENNLICH = geschlecht.add("männlich");
    GESCHLECHT_WEIBLICH  = geschlecht.add("weiblich");

    BART_AGIG      = bArt.add("A-Gig");
    BART_BGIG      = bArt.add("B-Gig");
    BART_CGIG      = bArt.add("C-Gig");
    BART_DGIG      = bArt.add("D-Gig");
    BART_EGIG      = bArt.add("E-Gig");
    BART_INRIGGER  = bArt.add("Inrigger");
    BART_RENNBOOT  = bArt.add("Rennboot");
    BART_SKIFF     = bArt.add("Skiff");
    BART_WHERRY    = bArt.add("Wherry");
    BART_TRIMMY    = bArt.add("Trimmy");
    BART_BARKE     = bArt.add("Barke");
    BART_KIRCHBOOT = bArt.add("Kirchboot");
    BART_MOTORBOOT = bArt.add("Motorboot");
    BART_ERGO      = bArt.add("Ergo");

    BANZAHL_1 = bAnzahl.add("1er"); // @todo: careful when translating:  the bAnzahl strings MUST contain the corresponding number as the first Arabic number in the text! This is needed by sortBootsList() in EfaDirektFrame.java
    BANZAHL_2 = bAnzahl.add("2er");
    BANZAHL_3 = bAnzahl.add("3er");
    BANZAHL_4 = bAnzahl.add("4er");
    BANZAHL_5 = bAnzahl.add("5er");
    BANZAHL_6 = bAnzahl.add("6er");
    BANZAHL_8 = bAnzahl.add("8er");

    BRIGGER_SKULL  = bRigger.add("Skull");
    BRIGGER_RIEMEN = bRigger.add("Riemen");

    BSTM_MIT  = bStm.add("mit Stm.");
    BSTM_OHNE = bStm.add("ohne Stm.");

    FAHRT_NORMAL            = fahrtart.add("normale Fahrt");
    FAHRT_TRAINING          = fahrtart.add("Training");
    FAHRT_REGATTA           = fahrtart.add("Regatta");
    FAHRT_JUMREGATTA        = fahrtart.add("JuM-Regatta");
    FAHRT_TRAININGSLAGER    = fahrtart.add("Trainingslager");
    FAHRT_AUSBILDUNG        = fahrtart.add("Ausbildung");
    FAHRT_KILOMETERNACHTRAG = fahrtart.add("Kilometernachtrag");
    if (Dialog.yesNoDialog("Fahrtarten für Motorboot und Ergo",
                           "Möchtest Du Fahrtarten für Motorboote und Ergos in efa verwenden?\n\n"+
                           "Hinweis: Dies ist nur dann notwendig, wenn Du vorhast, Fahrten von Motorbooten\n"+
                           "und Ergos in efa zu erfassen. efa fügt in diesem Fall zwei neue Fahrtarten\n"+
                           "'Motorboot' und 'Ergo' hinzu. Anderenfalls kannst Du auf diese Fahrtarten\n"+
                           "verzichten und diese Frage mit 'Nein' beantworten. (Es ist immer möglich,\n"+
                           "von Hand nachträglich diese Fahrtarten hinzuzufügen oder zu entfernen.)") == Dialog.YES) {
      FAHRT_MOTORBOOT       = fahrtart.add("Motorboot");
      FAHRT_ERGO            = fahrtart.add("Ergo");
    }
    FAHRT_MEHRTAGESFAHRT    = fahrtart.add("Mehrtagesfahrt");

    return writeFile(false);
  }

  public boolean isGigBoot(String art) {
    if (art == null || bArt == null) return false;
    return (BART_AGIG != -1 && art.equals(bArt.get(BART_AGIG))) ||
           (BART_BGIG != -1 && art.equals(bArt.get(BART_BGIG))) ||
           (BART_CGIG != -1 && art.equals(bArt.get(BART_CGIG))) ||
           (BART_DGIG != -1 && art.equals(bArt.get(BART_DGIG))) ||
           (BART_EGIG != -1 && art.equals(bArt.get(BART_EGIG))) ||
           (BART_INRIGGER != -1 && art.equals(bArt.get(BART_INRIGGER))) ||
           (BART_WHERRY != -1 && art.equals(bArt.get(BART_WHERRY))) ||
           (BART_TRIMMY != -1 && art.equals(bArt.get(BART_TRIMMY))) ||
           (BART_BARKE != -1 && art.equals(bArt.get(BART_BARKE))) ||
           (BART_KIRCHBOOT != -1 && art.equals(bArt.get(BART_KIRCHBOOT)));
  }

  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN v1.2.0 -> v1.4.0
        if ( s != null && s.trim().startsWith(KENNUNG120)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"120");
          if (!readEinstellungen()) return false;
          BART_ERGO      = bArt.add("Ergo");
          kennung = KENNUNG140;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN v1.4.0 -> v1.8.0 (urspr. würde in v1.8.0_00 in das Format 174 konvertiert)
        if ( s != null && s.trim().startsWith(KENNUNG140)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"140");
          if (!readEinstellungen()) return false;
          FAHRT_TRAININGSLAGER = fahrtart.add("Trainingslager");
          kennung = KENNUNG180;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN v1.7.4 -> v1.8.0 (Bugfix in 1.8.0_01: In v1.8.0_00 wurde ins Format 174 konvertiert, das jedoch einen Fehler enthielt)
        if ( s != null && s.trim().startsWith(KENNUNG174)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"174");
          if (!readEinstellungen()) return false;
          FAHRT_TRAININGSLAGER = fahrtart.get("Trainingslager");
          kennung = KENNUNG180;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN v1.8.0 -> v1.8.1
        if ( s != null && s.trim().startsWith(KENNUNG180)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"180");
          if (!readEinstellungen()) return false;
          if (Dialog.yesNoDialog("Fahrtarten für Motorboot und Ergo",
                                 "Möchtest Du Fahrtarten für Motorboote und Ergos in efa verwenden?\n\n"+
                                 "Hinweis: Dies ist nur dann notwendig, wenn Du vorhast, Fahrten von Motorbooten\n"+
                                 "und Ergos in efa zu erfassen. efa fügt in diesem Fall zwei neue Fahrtarten\n"+
                                 "'Motorboot' und 'Ergo' hinzu. Anderenfalls kannst Du auf diese Fahrtarten\n"+
                                 "verzichten und diese Frage mit 'Nein' beantworten. (Es ist immer möglich,\n"+
                                 "von Hand nachträglich diese Fahrtarten hinzuzufügen oder zu entfernen.)") == Dialog.YES) {
            FAHRT_MOTORBOOT = fahrtart.add("Motorboot");
            FAHRT_ERGO = fahrtart.add("Ergo");
          }
          BART_INRIGGER = bArt.add("Inrigger");
          FAHRT_KILOMETERNACHTRAG = fahrtart.add("Kilometernachtrag");
          kennung = KENNUNG181;
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


}
