package de.nmichael.efa.statistics;

import de.nmichael.efa.core.StatistikFrame;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.*;
import java.util.GregorianCalendar;
import javax.swing.JDialog;
import java.util.Hashtable;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class StatistikDaten implements Cloneable {

  public static final int AUSGABE_APPLICATION = 0;
  public static final int AUSGABE_APPLET = 1;

  public static final int SORTKRIT_NACHNAME = 0;
  public static final int SORTKRIT_VORNAME = 1;
  public static final int SORTKRIT_JAHRGANG = 2;
  public static final int SORTKRIT_KM = 3;
  public static final int SORTKRIT_FAHRTEN = 4;
  public static final int SORTKRIT_KMFAHRT = 5;
  public static final int SORTKRIT_DAUER = 6;
  public static final int SORTKRIT_KMH = 7;
  public static final int SORTKRIT_ANZVERSCH = 8;
  public static final int SORTKRIT_STATUS = 9;

  public static final int SORTFOLGE_AUF = 0;
  public static final int SORTFOLGE_AB = 1;

  public static final int ART_MITGLIEDER = 0;        // 070: 0
  public static final int ART_STATUS = 1;            // 070: 1
  public static final int ART_JAHRGANG = 2;          // 070: 2
  public static final int ART_GESCHLECHT = 3;        // 070: 3
  public static final int ART_ZIELE = 4;             // 070: 5
  public static final int ART_KMFAHRT = 5;           // 070: 12
  public static final int ART_MONATE = 6;            // 070: 6
  public static final int ART_WOTAGE = 7;            // 070: 7
  public static final int ART_TAGESZEIT = 8;         // neu in 130
  public static final int ART_BOOTE = 9;             // 070: 8;     120: 8
  public static final int ART_BOOTSART = 10;         // neu in 141
  public static final int ART_FAHRTART = 11;         // neu in 141
  public static final int ART_MITRUDERER = 12;       // 070: 4;     120: 9   140: 10  152: 11
  public static final int ART_WERMITWEM = 13;        // 070: 9;     120: 10  140: 11  152: 12
  public static final int ART_WERWOHIN = 14;         // 070: 10;    120: 11  140: 12  152: 13
  public static final int ART_WERMITBOOTSART = 15;   // neu in 170
  public static final int ART_FAHRTENBUCH = 16;      // 070: 11;    120: 12  140: 13  152: 14  160: 15
  public static final int ART_JAHRE = 17;            // neu in 090; 120: 13  140: 14  152: 15  160: 16
  public static final int ART_MONATSUEBERSICHT = 18; // neu in 141;          140: 15  152: 16  160: 17
  public static final int ART_WERUNERLAUBT = 19;     // neu in 170
  public static final int ART_WERMITFAHRTART = 20;   // neu in 173

  public static final int BART_BOOTE = 100;       // 070: 100
  public static final int BART_ART = 101;         // 070: 101
  public static final int BART_PLAETZE = 102;     // 070: 102
  public static final int BART_ARTDETAIL = 103;   // neu in 110
  public static final int BART_ZIELE = 104;       // 070: 103;   101: 103;
  public static final int BART_KMFAHRT = 105;     // 070: 108;   101: 104
  public static final int BART_MONATE = 106;      // 070: 104;   101: 105
  public static final int BART_WOTAGE = 107;      // 070: 105;   101: 106
  public static final int BART_TAGESZEIT = 108;   // neu in 130
  public static final int BART_RUDERER = 109;     // 070: 106;   101: 107  120: 108
  public static final int BART_WELCHESWOHIN = 110;// neu in 155
  public static final int BART_FAHRTENBUCH = 111; // 070: 107;   101: 108  120: 109  152: 110
  public static final int BART_JAHRE = 112;       // neu in 090; 101: 109  120: 110  152: 111

  public static final int WETT_DRV = 200;        // 085: 13 (STAT_MITGLIEDER)
  public static final int WETT_LRVBSOMMER = 201; // 085: 14 (STAT_MITGLIEDER)
  public static final int WETT_LRVBWINTER = 202; // 085: 15 (STAT_MITGLIEDER)
  public static final int WETT_LRVBWIMPEL = 203;
  public static final int WETT_DRV_WAFASTATISTIK = 204;
  public static final int WETT_LRVBRB_WANDERRUDERWETT = 205;
  public static final int WETT_LRVBRB_FAHRTENWETT = 206;
  public static final int WETT_LRVMVP_WANDERRUDERWETT = 207;

  public static final int AUSGABE_INTERN_GRAFIK = 0;
  public static final int AUSGABE_INTERN_TEXT   = 1;
  public static final int AUSGABE_BROWSER       = 2;
  public static final int AUSGABE_HTML          = 3;
  public static final int AUSGABE_PDF           = 4;
  public static final int AUSGABE_XML           = 5;
  public static final int AUSGABE_TXT           = 6;
  public static final int AUSGABE_CSV           = 7;
  public static final int AUSGABE_EFAWETT       = 8;

  public static final int NG_NAME   = 1;
  public static final int NG_GRUPPE = 2;


// nur zum Test
//  public static final int ART_DRV = 200;
//  public static final int ART_LRVBSOMMER = 201;
//  public static final int ART_LRVBWINTER = 202;
// nur zum Test

  public static final int STAT_MITGLIEDER = 0;
  public static final int STAT_BOOTE = 1;
  public static final int STAT_WETT = 2;

  public int art = ART_MITGLIEDER;
  public int stat = STAT_MITGLIEDER;

  public int ausgabe=AUSGABE_APPLICATION;
  public StatistikFrame statistikFrame;
  public JDialog parent;

  public String ausgabeDatei = null;
  public String ausgabeDateiTmp = null; // Name für Zwischendatei (z.B. XSL-FO-Datei), wird in XMLWriter geschrieben, sofern != null
  public int ausgabeArt = AUSGABE_INTERN_GRAFIK;
  public int ausgabeArtPrimaer = AUSGABE_HTML;
  public String stylesheet = null;
  public boolean ausgabeOverwriteWarnung=true;

  public String ftpServer = null;
  public String ftpUser, ftpPassword, ftpDirectory, ftpFilename;
  public int ftpPort;

//  public String dateiHTML="ausgabe.html";
//  public String dateiTXT ="ausgabe.txt";
//  public String dateiEfaWett="STANDARD.EFW";
//  public boolean outputHTML=false, outputTXT=false, formatiertTXT=false, outputProgram=false,
//                 outputEWett=false, outputBrowser=false, outputExtBrowser=false;
//  public boolean warnungHTML=true, warnungTXT=true;
  public boolean tabelleHTML=false;
  public String fileExecBefore = "";
  public String fileExecAfter = "";

  public TMJ von = new TMJ(1,1,1);
  public TMJ bis = new TMJ(31,12,9999);
  public GregorianCalendar vonCal, bisCal;
  public int wettJahr = 0; // Jahr (des Anfangs) für Wettbewerb
  public boolean zeitFbUebergreifend = false;
  public boolean vorjahresvergleich = false;
  public String[] nurFb;

  public boolean[] geschlecht = new boolean[Daten.bezeichnungen.geschlecht.size()];
  public boolean[] status; // Initialisierung im Konstruktor
  public String[]  statusNames; // Initialisierung im Konstruktor
  public boolean[] fahrtart = new boolean[Daten.bezeichnungen.fahrtart.size()];
  public boolean[] bArt = new boolean[Daten.bezeichnungen.bArt.size()];
  public boolean[] bAnzahl = new boolean[Daten.bezeichnungen.bAnzahl.size()];
  public boolean[] bRigger = new boolean[Daten.bezeichnungen.bRigger.size()];
  public boolean[] bStm = new boolean[Daten.bezeichnungen.bStm.size()];
  public boolean[] bVerein = new boolean[2];

  public String name = "";
  public boolean nameTeil = false;
  public int nameOderGruppe = NG_NAME;
  public String nurBemerk = "";
  public String nurBemerkNicht = "";
  public boolean nurStegKm = false;
  public int nurMindKm = 0;
  public String nurBooteFuerGruppe = "";

  public boolean ausgebenNr=false,
                 ausgebenName=false,ausgebenJahrgang=false,ausgebenStatus=false,
                 ausgebenArt=false,ausgebenPlaetze=false,ausgebenBezeichnung=false,
                 ausgebenKm=false,ausgebenRudKm=false,ausgebenStmKm=false,
                 ausgebenFahrten=false,ausgebenKmFahrt=false,
                 ausgebenDauer=false,ausgebenKmH=false,
                 ausgebenZielfahrten=false,
                 ausgebenWWAnzVersch=false,   // Anzahl verschiedener Mitruderer oder Ziele
                 ausgebenWWNamen=false,       // Pseudo; ist wahr, wenn eines der anderen ausgeben... wahr ist
                 ausgebenWettBedingung=false, // Wettbewerbsbedingungen ausgeben
                 ausgebenXMLalle=false,       // bei XML-Ausgabe immer alle Felder ausgeben
                 ausgebenWafaKm=false,
                 ausgebenMitglnrStattName=false; // statt des Namens die Mitgliedsnummer ausgeben

  public boolean graphischKm=false,graphischRudKm=false,graphischStmKm=false,
                 graphischFahrten=false,graphischKmFahrt=false,
                 graphischDauer=false,graphischKmH=false;

  public boolean[] numeriere; // wird im Konstruktor initialisiert

  public int sortierKriterium=SORTKRIT_KM,sortierFolge=SORTFOLGE_AB;
  public boolean sortVorNachname = true; // Vor- und Nachnamen, oder Name so wie er ist?

  public int graSizeKm=200,graSizeStmKm=200,graSizeRudKm=200,graSizeFahrten=200,graSizeKmFahrt=200,graSizeDauer=200,graSizeKmH=200; // Balkengröße für 100%
  public int maxSizeKm=200,maxSizeStmKm=200,maxSizeRudKm=200,maxSizeFahrten=200,maxSizeKmFahrt=200,maxSizeDauer=200,maxSizeKmH=200; // maximale Balkengröße (darüber: abschneiden)
  public boolean cropToMaxSize=false; // wenn true, dann werden zu lange Balken abgeschnitten, d.h. "<farbe>big.gif" benutzt
  public boolean zusammengefassteDatenOhneBalken=false; // "Gäste" und "andere" ohne Balken ausgeben

  public boolean zusammenAddieren = true;

  public boolean ww_horiz_alle = false; // Bei "Wer mit Wem" horizontal immer alle Namen anzeigen (sonst: nur die, die ausgewählt sind)
  public boolean kmfahrt_gruppiert = false; // Bei "Km / Fahrt" Km-Gruppen bilden
  public boolean ziele_gruppiert = false;  // Ziele, getrennt durch "+" zu einzelnen Teilzielen gruppieren (zusammenfassen)
  public boolean gasteAlsEinePerson = false; // Gäste als eine Person zusammenfassen und am Ende ausgeben
  public boolean gaesteVereinsweise = false; // Gäste vereinsweise zusammenfassen
  public boolean auchNullWerte = false; // auch immer alle Null-Werte ausgeben (d.h. Personen etc., die nicht gerudert sind, aber in den Datenlisten stehen)
  public boolean alleZielfahrten = false; // Alle Zielfahrten ausgeben (auch die, die über die 4 geforderten hinausgehen)

  public int wettProz = 70;   // Prozent, die erfüllt sein müssen, damit Ruderer ausgegeben wird
  public int wettFahrten = 0; // Anzahl von Fahrten, die absolviert sein müssen, damit Ruderer ausgegeben wird
  public int anzMitglieder = 0; // Anzahl der Mitglieder des Vereins am 01.01. des Jahres (für Blauen Wimpel)
  public boolean wettOhneDetail = false; // Details bei Erfüllung weglassen, d.h. nur keine einzelnen Fahrten auflisten

  // folgende Werte geben an, welche Felder bei der Statistikart "Fahrtenbuch" ausgegeben werden sollen
  public boolean fbLfdNr = true;
  public boolean fbDatum = true;
  public boolean fbBoot = true;
  public boolean fbStm = true;
  public boolean fbMannsch = true;
  public boolean fbAbfahrt = true;
  public boolean fbAnkunft = true;
  public boolean fbZiel = true;
  public boolean fbBootsKm = true;
  public boolean fbMannschKm = true;
  public boolean fbBemerkungen = true;
  public boolean fbFahrtartInBemerkungen = false;
  public boolean fbZielbereichInBemerkungen = true;

  public int[]    zusatzWett     = new int[3]; // Nummern der Wettbewerbe (>=200), die zusätzlich ausgewertet werden sollen
  public int[] zusatzWettjahr = new int[3];    // Wettbewerbsjahre für die zusätzlichen Wettbewerbe
  public boolean zusatzWettMitAnforderung = false; // bei Zusatzwettbewerben zus. auch die geforderten Km u.a. ausgeben


  // folgende Daten werden nicht beim Speichern der Statistikeinstellungen mitgespeichert
  public int maxKm=1,maxRudKm=1,maxStmKm=1,maxFahrten=1,maxDauer=1;
  public float maxKmFahrt=1.0f,maxKmH=1.0f;

  public TMJ fruehesteFahrt,spaetesteFahrt;
  public int ersterEintrag=1,letzterEintrag=1;
  public String erstesFb="",letztesFb="";

  public Hashtable alleAusgewertetenEintraege = new Hashtable();

  public boolean wettKurzAusgabe = false;

  public int browserCloseTimeout = 0; // wenn > 0, wird der Browser nach x Sekunden automatisch geschlossen

  public boolean abbruchEfaWett = false;

  public StatistikDaten() {
    if (Daten.fahrtenbuch != null) {
      status = new boolean[Daten.fahrtenbuch.getDaten().status.length];
      statusNames = Daten.fahrtenbuch.getDaten().status; // damit, falls anderes Fahrtenbuch geöffnet wird, urspr. Auswahl nicht verloren geht
    } else {
      status = new boolean[1];
      statusNames = new String[1]; statusNames[0] = "unbekannt";
    }
    numeriere = new boolean[status.length];
  }

  public StatistikDaten cloneSD() {
    try {
      return (StatistikDaten)this.clone();
    } catch(CloneNotSupportedException e) {
      return null;
    }
  }

}
