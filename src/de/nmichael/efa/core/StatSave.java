/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.statistics.StatistikDaten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.io.*;

// @i18n complete

public class StatSave extends DatenListe {

  public static final int NAMESTAT = 0;
  public static final int ART = 1;
  public static final int STAT = 2;
  public static final int AUSGABEDATEI = 3;        // geändert in v1.0 (ehemals DATEIHTML)
  public static final int AUSGABEOVERWRITE = 4;    // geändert in v1.0 (ehemals DATEITXT)
  public static final int AUSGABEART = 5;          // geändert in v1.0 (ehemals OUTPUT)
  public static final int TABELLEHTML = 6;
  public static final int VON = 7;
  public static final int BIS = 8;
  public static final int GESCHLECHT = 9;
  public static final int STATUS = 10;
  public static final int FAHRTART = 11;           // neu in v1.0
  public static final int BART = 12;
  public static final int BANZAHL = 13;
  public static final int BRIGGER = 14;
  public static final int BSTM = 15;
  public static final int BVEREIN = 16;
  public static final int NAME = 17;
  public static final int NAMETEIL = 18;
  public static final int AUSGEBEN = 19;
  public static final int GRAPHISCH = 20;
  public static final int NUMERIERE = 21;
  public static final int SORTIERKRITERIUM = 22;
  public static final int SORTIERFOLGE = 23;
  public static final int SORTVORNACHNAME = 24;
  public static final int GRASIZEKM = 25;
  public static final int GRASIZESTMKM = 26;
  public static final int GRASIZERUDKM = 27;
  public static final int GRASIZEFAHRTEN = 28;
  public static final int GRASIZEKMFAHRT = 29;
  public static final int ZUSAMMENADDIEREN = 30;
  public static final int WW_OPTIONS = 31;          // geänderte Bedeutung in v1.0 (jetzt: ww_horiz_alle)
  public static final int AUCHNULLWERTE = 32;       // geändert in v1.0 (ehemals WARNUNG_OVERWRITE)
  public static final int KMFAHRT_GRUPPIERT = 33;   // neu in v0.80
  public static final int STYLESHEET = 34;          // geändert in v1.0 (ehemals TXTFORMATIERT (neu in v0.80))
  public static final int ZIELEGRUPPIERT = 35;      // neu in v0.80
  public static final int ZEITFBUEBERGREIFEND = 36; // neu in v0.85
  public static final int AUSWETTBEDINGUNGEN = 37;  // neu in v0.85
  public static final int WETTPROZENT = 38;         // neu in v0.90
  public static final int WETTFAHRTEN = 39;         // neu in v0.90
  public static final int GAESTEALSEIN = 40;        // neu in v0.90
  public static final int WETTOHNEDETAIL = 41;      // neu in v0.90
  public static final int WETTJAHR = 42;            // neu in v0.90
  public static final int CROPTOMAXSIZE = 43;       // neu in v0.91
  public static final int MAXSIZEKM = 44;           // neu in v0.91
  public static final int MAXSIZERUDKM = 45;        // neu in v0.91
  public static final int MAXSIZESTMKM = 46;        // neu in v0.91
  public static final int MAXSIZEFAHRTEN = 47;      // neu in v0.91
  public static final int MAXSIZEKMFAHRT = 48;      // neu in v0.91
  public static final int MAXSIZEDAUER = 49;        // neu in v1.00
  public static final int MAXSIZEKMH = 50;          // neu in v1.00
  public static final int GRASIZEDAUER = 51;        // neu in v1.00
  public static final int GRASIZEKMH = 52;          // neu in v1.00
  public static final int NURBEMERK = 53;           // neu in v1.1.0
  public static final int NURBEMERKNICHT = 54;      // neu in v1.1.0
  public static final int ZUSWETT1 = 55;            // neu in v1.2.0
  public static final int ZUSWETT2 = 56;            // neu in v1.2.0
  public static final int ZUSWETT3 = 57;            // neu in v1.2.0
  public static final int ZUSWETTJAHR1 = 58;        // neu in v1.2.0
  public static final int ZUSWETTJAHR2 = 59;        // neu in v1.2.0
  public static final int ZUSWETTJAHR3 = 60;        // neu in v1.2.0
  public static final int ZUSWETTMITANFORD = 61;    // neu in v1.2.0
  public static final int NURSTEGKM = 62;           // neu in v1.2.0
  public static final int ZEITVORJAHRESVERGLEICH=63;// neu in v1.3.0
  public static final int NURMINDKM=64;             // neu in v1.3.0
  public static final int AUCHINEFADIREKT=65;       // neu in v1.3.1
  public static final int FAHRTENBUCHFELDER=66;     // neu in v1.4.0
  public static final int ZUSAMMENGEFASSTEWERTEOHNEBALKEN=67; // neu in v1.4.1
  public static final int NAME_ODER_GRUPPE=68;      // neu in v1.7.0
  public static final int FILE_EXEC_BEFORE=69;      // neu in v1.7.0
  public static final int FILE_EXEC_AFTER=70;       // neu in v1.7.0
  public static final int NUR_FB=71;                // neu in v1.7.2
  public static final int NURBOOTEFUERGRUPPE=72;    // neu in v1.8.1
  public static final int ALLEZIELFAHRTEN = 73;     // neu in v1.8.2
  public static final int NURGANZEKM = 74;          // neu in v1.8.3


  public static final String KENNUNG070 = "##EFA.070.STATISTIK##";
  public static final String KENNUNG080 = "##EFA.080.STATISTIK##";
  public static final String KENNUNG085 = "##EFA.085.STATISTIK##";
  public static final String KENNUNG090 = "##EFA.090.STATISTIK##";
  public static final String KENNUNG091 = "##EFA.091.STATISTIK##";
  public static final String KENNUNG100 = "##EFA.100.STATISTIK##";
  public static final String KENNUNG110 = "##EFA.110.STATISTIK##";
  public static final String KENNUNG120 = "##EFA.120.STATISTIK##";
  public static final String KENNUNG130 = "##EFA.130.STATISTIK##";
  public static final String KENNUNG131 = "##EFA.131.STATISTIK##";
  public static final String KENNUNG140 = "##EFA.140.STATISTIK##";
  public static final String KENNUNG141 = "##EFA.141.STATISTIK##";
  public static final String KENNUNG160 = "##EFA.160.STATISTIK##";
  public static final String KENNUNG170 = "##EFA.170.STATISTIK##";
  public static final String KENNUNG172 = "##EFA.172.STATISTIK##";
  public static final String KENNUNG180 = "##EFA.180.STATISTIK##";
  public static final String KENNUNG181 = "##EFA.181.STATISTIK##";
  public static final String KENNUNG182 = "##EFA.182.STATISTIK##";

  // Konstruktor
  public StatSave(String pdat) {
    super(pdat,75,1,false);
    kennung = KENNUNG182;
  }


  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {


        // KONVERTIEREN: 070 -> 080
        if (s != null && s.trim().startsWith(KENNUNG070)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"070");
          iniList(this.dat,36,1,false); // Rahmenbedingungen von v0.80 schaffen
          // Datei lesen
          String[][] artkonv = { {"0","0"} , {"1","1"} , {"2","2"} , {"3","3"} , {"4","9"} , {"5","4"} , {"6","6"} , {"7","7"} , {"8","8"} , {"9","10"} ,
                                 {"10","11"} , {"11","12"} , {"12","5"} , {"100","100"} , {"101","101"} , {"102","102"} , {"103","103"} , {"104","105"} ,
                                 {"105","106"} , {"106","107"} , {"107","108"} , {"108","104"} }; // Vertauschen der Statistikart-Nummern
          try {
            while ((s = freadLine()) != null) {
              s= s.trim();
              s = s+"|-|-|-";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              for (int i=0; i<artkonv.length; i++) {
                if (d.get(this.ART).equals(artkonv[i][0])) {
                  d.set(this.ART,artkonv[i][1]);
                  break;
                }
              }
//              add(s+"|-|-|-");
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG080;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }


        // KONVERTIEREN: 080 -> 085
        if (s != null && s.trim().startsWith(KENNUNG080)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"080");
          iniList(this.dat,38,1,false); // Rahmenbedingungen von v0.85 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(s+"|-|-");
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG085;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }


        // KONVERTIEREN: 085 -> 090
        if (s != null && s.trim().startsWith(KENNUNG085)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"085");
          iniList(this.dat,43,1,false); // Rahmenbedingungen von v0.90 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s = s+"|60|4|-|-|0";
              DatenFelder d = constructFields(s);
              if (EfaUtil.string2int(d.get(StatSave.STAT),-1)  == StatistikDaten.STAT_MITGLIEDER &&
                  EfaUtil.string2int(d.get(StatSave.ART),-1) > 12) {
                d.set(StatSave.STAT,Integer.toString(StatistikDaten.STAT_WETT));
                d.set(StatSave.ART,Integer.toString(EfaUtil.string2int(d.get(StatSave.ART),-1)-13+200));
              }
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG090;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 090 -> 091
        if (s != null && s.trim().startsWith(KENNUNG090)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"090");
          iniList(this.dat,49,1,false); // Rahmenbedingungen von v0.91 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s = s+"|-|200|200|200|200|200";
              DatenFelder d = constructFields(s);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG091;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 091 -> 100
        if (s != null && s.trim().startsWith(KENNUNG091)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"091");
          iniList(this.dat,53,1,false); // Rahmenbedingungen von v1.00 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              s += "|200|200|200|200";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              // Ausgabedatei ermitteln
              String t="";
              if (EfaUtil.isOptionSet(d.get(5),0)) t = d.get(3);      // DATEIHTML verwenden
              else if (EfaUtil.isOptionSet(d.get(5),1)) t = d.get(4); // DATEITXT verwenden
              d.set(AUSGABEDATEI,t);

              // Vor Überschreiben warnen?
              if (EfaUtil.isOptionSet(d.get(32),0))
                d.set(AUSGABEOVERWRITE,"+");
              else
                d.set(AUSGABEOVERWRITE,"-");

              // Ausgabeart ermitteln
              int art;
              if (EfaUtil.isOptionSet(d.get(5),0)) art = StatistikDaten.AUSGABE_HTML;
              else if (EfaUtil.isOptionSet(d.get(5),1) && EfaUtil.isOptionSet(d.get(34),0)) art = StatistikDaten.AUSGABE_TXT;
              else if (EfaUtil.isOptionSet(d.get(5),1) && !EfaUtil.isOptionSet(d.get(34),0)) art = StatistikDaten.AUSGABE_CSV;
              else if (EfaUtil.isOptionSet(d.get(5),2)) art = StatistikDaten.AUSGABE_INTERN_TEXT;
              else art = StatistikDaten.AUSGABE_INTERN_GRAFIK;
              d.set(AUSGABEART,Integer.toString(art));

              // Sortierkriterium anpassen
              if (d.get(SORTIERKRITERIUM).equals("6")) d.set(SORTIERKRITERIUM,Integer.toString(StatistikDaten.SORTKRIT_ANZVERSCH));

              // erweiterte Ausgabefelder ergänzen
              d.set(AUSGEBEN,d.get(19)+"--");
              d.set(GRAPHISCH,d.get(20)+"--");

              // Default-Werte für neue Felder
              d.set(AUCHNULLWERTE,"-");
              d.set(STYLESHEET,"");
              d.set(FAHRTART,"++++++");
              d.set(WW_OPTIONS,"-");

              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG100;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 100 -> 110
        if (s != null && s.trim().startsWith(KENNUNG100)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"100");
          iniList(this.dat,55,1,false); // Rahmenbedingungen von v1.10 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              s += "||";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              // Die Artnummern 103 bis 109 haben sich in v1.1.0 um 1 erhöht
              int artnr = EfaUtil.string2int(d.get(this.ART),-1);
              if (artnr >= 103 && artnr <= 109) {
                d.set(this.ART,Integer.toString(artnr+1));
              }

              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG110;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 110 -> 120
        if (s != null && s.trim().startsWith(KENNUNG110)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"110");
          iniList(this.dat,63,1,false); // Rahmenbedingungen von v1.20 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              s += "|-1|-1|-1|0|0|0|-|-";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG120;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 120 -> 130
        if (s != null && s.trim().startsWith(KENNUNG120)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"120");
          iniList(this.dat,65,1,false); // Rahmenbedingungen von v1.30 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              s += "|-|0";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              int _art = EfaUtil.string2int(d.get(ART),-1);
              if (_art>=8 && _art<=13) d.set(ART,Integer.toString(_art+1)); // Art-Wert in diesem Bereich um eins nach hinten verschieben
              if (_art>=108 && _art<=110) d.set(ART,Integer.toString(_art+1)); // Art-Wert in diesem Bereich um eins nach hinten verschieben
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG130;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 130 -> 131
        if (s != null && s.trim().startsWith(KENNUNG130)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"130");
          iniList(this.dat,66,1,false); // Rahmenbedingungen von v1.3.1 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              s += "|-";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG131;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 131 -> 140
        if (s != null && s.trim().startsWith(KENNUNG131)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"131");
          iniList(this.dat,67,1,false); // Rahmenbedingungen von v1.4.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              s += "|+++++++++++";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG140;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 140 -> 141
        if (s != null && s.trim().startsWith(KENNUNG140)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"140");
          iniList(this.dat,68,1,false); // Rahmenbedingungen von v1.4.1 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s += "|-";
              DatenFelder d = constructFields(s);
              int art = EfaUtil.string2int(d.get(ART),0);
              if (art >= 10 && art<100) d.set(ART,Integer.toString(art+1)); // wegen eingefügter Art "Bootsart"
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG141;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 141 -> 160
        if (s != null && s.trim().startsWith(KENNUNG141)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"141");
          iniList(this.dat,68,1,false); // Rahmenbedingungen von v1.6.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              int art = EfaUtil.string2int(d.get(ART),0);
              if (art >= 11 && art<100) d.set(ART,Integer.toString(art+1)); // wegen eingefügter Art "Fahrtart"
              if (art == 110 || art == 111) d.set(ART,Integer.toString(art+1)); // wegen eingefügter Art "Welches Boot Wohin"
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG160;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 160 -> 170
        if (s != null && s.trim().startsWith(KENNUNG160)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"160");
          iniList(this.dat,71,1,false); // Rahmenbedingungen von v1.7.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s += "|"+StatistikDaten.NG_NAME+"||";
              DatenFelder d = constructFields(s);
              if (EfaUtil.string2int(d.get(ART),0) >= 15 && EfaUtil.string2int(d.get(ART),0)<100) d.set(ART,Integer.toString(EfaUtil.string2int(d.get(ART),0)+1));
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG170;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 170 -> 172
        if (s != null && s.trim().startsWith(KENNUNG170)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"170");
          iniList(this.dat,72,1,false); // Rahmenbedingungen von v1.7.2 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s += "|";
              DatenFelder d = constructFields(s);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG172;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 172 -> 180
        if (s != null && s.trim().startsWith(KENNUNG172)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"172");
          iniList(this.dat,72,1,false); // Rahmenbedingungen von v1.8.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              // wenn alle Fahrtarten selektiert sind, dann füge eine weitere selektierte hinzu, da es jetzt eine neue
              // Fahrtart "Trainingslager" gibt. Wenn nicht alle selektiert sind, dann füge als vorletzte Fahrtart
              // (Trainingslager) eine nicht selektierte ein.
              String fa_tmp = d.get(FAHRTART);
              if (fa_tmp.indexOf("-") < 0) d.set(FAHRTART,fa_tmp+"+");
              else if (fa_tmp.length()>2) d.set(FAHRTART,fa_tmp.substring(0,fa_tmp.length()-1) + "-" + fa_tmp.substring(fa_tmp.length()-1));

              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG180;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 180 -> 181
        if (s != null && s.trim().startsWith(KENNUNG180)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"180");
          iniList(this.dat,73,1,false); // Rahmenbedingungen von v1.8.1 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              // füge neue Fahrtarten "Motorboot" und "Ergo" als nicht selektierte Fahrtarten hinzu (falls vorhanden),
              // und füge "Kilometernachtrag" als selektierte Fahrtart hinzu
              String fa_tmp = d.get(FAHRTART);
              if (Daten.efaTypes != null &&
                  Daten.efaTypes.size(EfaTypes.CATEGORY_TRIP) > fa_tmp.length()) {
                d.set(FAHRTART,fa_tmp.
                      substring(0,fa_tmp.length() - 1) +
                      (Daten.efaTypes.size(EfaTypes.CATEGORY_TRIP) == fa_tmp.length() + 1 ? "+" : "+--") +
                      fa_tmp.substring(fa_tmp.length() - 1));
              }

              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG181;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 181 -> 182
        if (s != null && s.trim().startsWith(KENNUNG181)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"181");
          iniList(this.dat,74,1,false); // Rahmenbedingungen von v1.8.2 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              d.set(ALLEZIELFAHRTEN,"-");
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG182;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(kennung)) {
          errInvalidFormat(dat, EfaUtil.trimto(s, 20));
          fclose(false);
          return false;
        }
      }
    } catch(IOException e) {
      errReadingFile(dat,e.getMessage());
      return false;
    }
    return true;
  }

  // Einträge auf Gültigkeit prüfen
  public void validateValues(DatenFelder d) {

    // Überprüfen, ob die in bezeichnungen.cfg konfigurierten Fahrtarten mit denen in den gespeicherten Statistikeinstellungen übereinstimmen
    if (d != null && Daten.efaTypes != null) {
      String fahrtart = d.get(FAHRTART);
      if (fahrtart != null && Daten.efaTypes.size(EfaTypes.CATEGORY_TRIP) != fahrtart.length()) {
        if (fahrtart.length() < Daten.efaTypes.size(EfaTypes.CATEGORY_TRIP)) {
          if (fahrtart.indexOf("-") < 0) {
            // fehlende Fahrtarten, aber vorher alle selektiert
            while (fahrtart.length() < Daten.efaTypes.size(EfaTypes.CATEGORY_TRIP)) fahrtart += "+";
            d.set(FAHRTART, fahrtart);
          } else {
            // fehlende Fahrtarten, aber vorher nicht alle selektiert
            // Nothing we can do about it!
          }
        } else {
          // zu viele Fahrtarten
          // Nothing we should do in this case!
        }
      }
    }
  }


}
