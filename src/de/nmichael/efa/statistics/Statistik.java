/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

import de.nmichael.efa.gui.util.TableSorter;
import de.nmichael.efa.efa1.Ziele;
import de.nmichael.efa.efa1.Mitglieder;
import de.nmichael.efa.efa1.Fahrtenbuch;
import de.nmichael.efa.efa1.Fahrtenabzeichen;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.efa1.DatenFelder;
import de.nmichael.efa.efa1.Boote;
import de.nmichael.efa.efa1.Adressen;
import de.nmichael.efa.core.*;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.*;
import java.util.*;
import java.io.*;
import java.awt.*;

// @i18n complete

public class Statistik {

  public static boolean isCreateRunning = false; // soll verhindern, daß u.U. zwei Statistikerstellungen parallel laufen
                                                 // Bugfix 13.02.2006 für MG

  // Farbdefinitionen für die Balken (entsprechen den Dateinamen der *.gif-Dateien)
  // @todo (P4) change names for image files??
  protected static final String COLORKM      = "km";
  protected static final String COLORRUDKM   = "rudkm";
  protected static final String COLORSTMKM   = "stmkm";
  protected static final String COLORFAHRTEN = "fahrten";
  protected static final String COLORKMFAHRT = "kmfahrt";
  protected static final String COLORDAUER   = "dauer";
  protected static final String COLORKMH     = "kmh";

  // Status-Kennzeichnungen für Personen, die ans Ende sortiert werden sollen
  protected static final String GAST = "_GUEST_";
  protected static final String ANDERE = "_OTHERS_";

  // Status für Gäste und andere
  protected static String GASTBEZ   = null; // "Gäste";  --> wird in create() initialisiert
  protected static String ANDEREBEZ = null; // "andere"; --> wird in create() initialisiert

  // aktive Mitglieder
  protected static final String AKTIV_M_AB19  = "M19";
  protected static final String AKTIV_M_BIS18 = "M18";
  protected static final String AKTIV_W_AB19  = "W19";
  protected static final String AKTIV_W_BIS18 = "W18";

  // sonstige Konstanten für Wettbewerbe
  private static final int LRVMVP_NEU = 2009; // neue Bedingungen ab 2009 gültig!

  protected static Hashtable alleWW;
  protected static AlleWWArrEl[] alleWWArr;
  protected static Hashtable alleAktive;

  protected static String lastLfdNr; // für Art: Fahrtenbuch (damit Einträge nicht doppelt)

  protected static Hashtable nichtBeruecksichtigt; // Bei Wettbewerben nicht berücksichtigte Mitglieder (z.B. weil Jahrgang fehlt oder Wettbewerbsmeldungen deaktiviert sind)
  protected static String warnungen;            // Warnungen, die am Ende ausgegeben werden

  protected static EfaWett efaWett; // Zusammenstellung aller Wettbewerbsdaten für Erstellung einer Meldedatei

  public static volatile int progressDone=0;
  public static volatile int progressCurrent=0;
  public static volatile int progressLength=1;
  public static volatile int progressInc=1000;
  public static volatile String progressMessage="";

  protected static boolean abort=false;


  public static AusgabeEintrag letzterAusgabeEintrag;








  // Alle zusätzlichen Daten berechnen, die nicht direkt mit den einzelnen Teilnehmern zusammenhängen
  static AusgabeDaten createHeaderInformation(StatistikDaten sd, int anz, ArrEl[] a) {
    AusgabeDaten ad = new AusgabeDaten();
    letzterAusgabeEintrag = null;

    // Titel
    ad.titel = International.getString("Kilometerliste");

    // ausgewertet am
    Calendar c = GregorianCalendar.getInstance();
    TMJ tmj = new TMJ(c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1-c.getMinimum(Calendar.MONTH),c.get(Calendar.YEAR));
    ad.ausgewertetAm = tmj.tag+"."+tmj.monat+"."+tmj.jahr;

    // ausgewertet von
    ad.ausgewertetVon = Daten.EFA_LONGNAME + " " + Daten.VERSION;
    ad.ausgewertetVonURL = Daten.EFAURL;

    // Art der Auswertung
    switch (sd.stat) {
      case StatistikDaten.STAT_MITGLIEDER: ad.auswertungsArt = International.getString("Mannschafts-Kilometer")+": "; break;
      case StatistikDaten.STAT_BOOTE: ad.auswertungsArt = International.getString("Boots-Kilometer")+": "; break;
      case StatistikDaten.STAT_WETT: ad.auswertungsArt = International.getString("Wettbewerb")+": "; break;
      default: ad.auswertungsArt = International.getString("unbekannt")+": "; break;
    }
    switch (sd.art) {
      case StatistikDaten.ART_MITGLIEDER:
      case StatistikDaten.BART_RUDERER:
        ad.auswertungsArt += International.getString("Personen"); break;
      case StatistikDaten.ART_ZIELE:
      case StatistikDaten.BART_ZIELE:
        ad.auswertungsArt += International.getString("Ziele");
        if (sd.ziele_gruppiert) ad.auswertungsArt += " (" + International.getString("Teilziele einzeln") + ")";
        break;
      case StatistikDaten.ART_MONATE:
      case StatistikDaten.BART_MONATE:
        ad.auswertungsArt += International.getString("Monate"); break;
      case StatistikDaten.ART_WOTAGE:
      case StatistikDaten.BART_WOTAGE:
        ad.auswertungsArt += International.getString("Wochentage"); break;
      case StatistikDaten.ART_TAGESZEIT:
      case StatistikDaten.BART_TAGESZEIT:
        ad.auswertungsArt += International.getString("Tageszeit"); break;
      case StatistikDaten.ART_JAHRE:
      case StatistikDaten.BART_JAHRE:
        ad.auswertungsArt += International.getString("Jahre"); break;
      case StatistikDaten.ART_BOOTE:
      case StatistikDaten.BART_BOOTE:
        ad.auswertungsArt += International.getString("Boote"); break;
      case StatistikDaten.ART_BOOTSART:
        ad.auswertungsArt += International.getString("Bootsart"); break;
      case StatistikDaten.ART_FAHRTART:
        ad.auswertungsArt += International.getString("Fahrtart"); break;
      case StatistikDaten.ART_MITRUDERER:
        ad.auswertungsArt += International.getString("Mannschaft"); break;
      case StatistikDaten.ART_STATUS:
        ad.auswertungsArt += International.getString("Status"); break;
      case StatistikDaten.ART_JAHRGANG:
        ad.auswertungsArt += International.getString("Jahrgang"); break;
      case StatistikDaten.ART_GESCHLECHT:
        ad.auswertungsArt += International.getString("Geschlecht"); break;
      case StatistikDaten.ART_WERMITWEM:
        ad.auswertungsArt += International.getString("Wer mit Wem"); break;
      case StatistikDaten.ART_WERWOHIN:
        ad.auswertungsArt += International.getString("Wer Wohin");
        if (sd.ziele_gruppiert) ad.auswertungsArt += " (" + International.getString("Teilziele einzeln") + ")";
        break;
      case StatistikDaten.ART_WERMITBOOTSART:
        ad.auswertungsArt += International.getString("Wer mit Bootsart"); break;
      case StatistikDaten.ART_WERUNERLAUBT:
        ad.auswertungsArt += International.getString("Wer unerlaubt"); break;
      case StatistikDaten.ART_WERMITFAHRTART:
        ad.auswertungsArt += International.getString("Wer mit Fahrtart"); break;
      case StatistikDaten.ART_FAHRTENBUCH:
      case StatistikDaten.BART_FAHRTENBUCH:
        ad.auswertungsArt += International.getString("Fahrtenbuch"); break;
      case StatistikDaten.ART_MONATSUEBERSICHT:
        ad.auswertungsArt += International.getString("Monatsübersicht"); break;
      case StatistikDaten.ART_KMFAHRT:
      case StatistikDaten.BART_KMFAHRT:
        ad.auswertungsArt += International.getString("Km/Fahrt"); break;
      case StatistikDaten.BART_ART:
        ad.auswertungsArt += International.getString("Art"); break;
      case StatistikDaten.BART_PLAETZE:
        ad.auswertungsArt += International.getString("Bootsplätze"); break;
      case StatistikDaten.BART_ARTDETAIL:
        ad.auswertungsArt += International.getString("Art")+
                " (" + International.getString("Detail") + ")"; break;
      case StatistikDaten.BART_WELCHESWOHIN:
        ad.auswertungsArt += International.getString("Welches Boot Wohin");
        if (sd.ziele_gruppiert) ad.auswertungsArt += " (" + International.getString("Teilziele einzeln") + ")";
        break;
    }
    if (sd.art >= 200 && sd.art - 200 < WettDefs.ANZWETT)
      ad.auswertungsArt += Daten.wettDefs.getWettDef(sd.art - StatistikDaten.WETT_DRV,sd.wettJahr).name;

    // Auswertungszeitraum
    if (sd.von.jahr == 1 && sd.bis.jahr == 9999) {
        ad.auswertungsZeitraum = International.getString("gesamter Zeitraum");
    } else if (sd.von.jahr == 1) {
        ad.auswertungsZeitraum = International.getMessage("bis {day}.{month}.{year}",sd.bis.tag,sd.bis.monat,sd.bis.jahr);
    } else if (sd.bis.jahr == 9999) {
        ad.auswertungsZeitraum = International.getMessage("vom {day}.{month}.{year}",sd.von.tag,sd.von.monat,sd.von.jahr);
    } else {
        ad.auswertungsZeitraum = International.getMessage("vom {day}.{month}.{year}",sd.von.tag,sd.von.monat,sd.von.jahr) +
                " " +
                International.getMessage("bis {day}.{month}.{year}",sd.bis.tag,sd.bis.monat,sd.bis.jahr);
    }
    if (sd.vorjahresvergleich) {
        ad.auswertungsZeitraum = International.getString("Jahresvergleich") +
                " "+sd.von.jahr+"/"+sd.bis.jahr+": "+
                International.getMessage("vom {day}.{month}",sd.von.tag,sd.von.monat) +
                " " +
                International.getMessage("bis {day}.{month}",sd.bis.tag,sd.bis.monat);
    }

    // Ausgewertete Einträge
    if (sd.fruehesteFahrt == null || sd.spaetesteFahrt == null) {
      ad.ausgewerteteEintraege = International.getString("keine passenden Einträge gefunden");
    } else {
      ad.ausgewerteteEintraege = 
              International.getMessage("{n} Einträge",sd.alleAusgewertetenEintraege.keySet().size()) +
              ": #"+sd.ersterEintrag+" - #"+sd.letzterEintrag+
              " ("+
              International.getMessage("vom {day}.{month}.{year}",sd.fruehesteFahrt.tag,sd.fruehesteFahrt.monat,sd.fruehesteFahrt.jahr) +
              " " +
              International.getMessage("bis {day}.{month}.{year}",sd.spaetesteFahrt.tag,sd.spaetesteFahrt.monat,sd.spaetesteFahrt.jahr)
              +")";
    }

    // Titel (2)
    if (ad.titel != null && sd.fruehesteFahrt != null && sd.spaetesteFahrt != null) {
      if (sd.fruehesteFahrt.jahr == sd.spaetesteFahrt.jahr) ad.titel += " " + sd.fruehesteFahrt.jahr;
      else ad.titel += " " + sd.fruehesteFahrt.jahr + " - " + sd.spaetesteFahrt.jahr;
    }

    // Auswertung fuer
    int j;
    boolean alle;
    if (sd.stat == StatistikDaten.STAT_MITGLIEDER || sd.stat == StatistikDaten.STAT_WETT) {
      ad.auswertungFuer = new String[3];
      ad.auswertungFuer[0] = ad.auswertungFuer[1] = ad.auswertungFuer[2] = "";
      alle=true;
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++)
        if (!sd.geschlecht[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++) {
          if (sd.geschlecht[i]) {
            if (j++>0) ad.auswertungFuer[0] += ", ";
            // @todo (P5) statistics ad.auswertungFuer[0] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER,i);
          }
        }
      } else {
          ad.auswertungFuer[0] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER,EfaTypes.TYPE_GENDER_MALE) + ", " +
                                  Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER,EfaTypes.TYPE_GENDER_FEMALE);
      }
      alle=true;
      for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++)
        if (!sd.status[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.fahrtenbuch.getDaten().status.length; i++) {
          if (sd.status[i]) {
            if (j++>0) ad.auswertungFuer[1] += ", ";
            ad.auswertungFuer[1] += Daten.fahrtenbuch.getDaten().status[i];
          }
        }
      } else ad.auswertungFuer[1] += International.getString("alle");
      alle=true;
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++)
        if (!sd.fahrtart[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++) {
          if (sd.fahrtart[i]) {
            if (j++>0) ad.auswertungFuer[2] += ", ";
            // @todo (P5) statistics ad.auswertungFuer[2] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION,i);
          }
        }
      } else ad.auswertungFuer[2] += International.getString("alle Arten von Fahrten");
    } else { // STAT_BOOTE
      ad.auswertungFuer = new String[5];
      ad.auswertungFuer[0] = ad.auswertungFuer[1] = ad.auswertungFuer[2] = ad.auswertungFuer[3] = ad.auswertungFuer[4] = "";
      alle=true;
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++)
        if (!sd.bArt[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++) {
          if (sd.bArt[i]) {
            if (j++>0) ad.auswertungFuer[0] += ", ";
            // @todo (P5) statistics ad.auswertungFuer[0] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT,i);
          }
        }
      } else ad.auswertungFuer[0] += International.getString("alle Bootsarten");
      alle=true;
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++)
        if (!sd.bAnzahl[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++) {
          if (sd.bAnzahl[i]) {
            if (j++>0) ad.auswertungFuer[1] += ", ";
            // @todo (P5) statistics ad.auswertungFuer[1] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS,i);
          }
        }
      } else ad.auswertungFuer[1] += International.getString("alle Bootsgrößen");
      alle=true;
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING); i++)
        if (!sd.bRigger[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING); i++) {
          if (sd.bRigger[i]) {
            if (j++>0) ad.auswertungFuer[2] += ", ";
            // @todo (P5) statistics ad.auswertungFuer[2] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING,i);
          }
        }
      } else ad.auswertungFuer[2] += International.getString("alle Riggertypen");
      alle=true;
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_COXING); i++)
        if (!sd.bStm[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_COXING); i++) {
          if (sd.bStm[i]) {
            if (j++>0) ad.auswertungFuer[3] += ", ";
            // @todo (P5) statistics ad.auswertungFuer[3] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING,i);
          }
        }
      } else ad.auswertungFuer[3] += Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXED) + ", " +
                                     Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXLESS);
      alle=true;
      for (int i=0; i<sd.bVerein.length; i++)
        if (!sd.bVerein[i]) alle=false;
      if (!alle) {
        for (int i=j=0; i<2; i++) {
          if (sd.bVerein[i]) {
            if (j++>0) ad.auswertungFuer[4] += ", ";
            if (i==0) ad.auswertungFuer[4] += International.getString("eigene Boote");
            else ad.auswertungFuer[4] += International.getString("fremde Boote");
          }
        }
      } else ad.auswertungFuer[4] += International.getString("alle Vereine");
    }

    // Auswertung nur für Name
    if (!sd.name.equals("")) {
      if (sd.stat == StatistikDaten.STAT_MITGLIEDER || sd.stat == StatistikDaten.STAT_WETT) {
        if (sd.nameOderGruppe == StatistikDaten.NG_NAME) ad.auswertungNurFuerBez = International.getString("Name");
        else ad.auswertungNurFuerBez = International.getString("Gruppe");
      } else {
        ad.auswertungNurFuerBez = International.getString("Boot");
      }
      ad.auswertungNurFuer = sd.name;
    } else if (sd.nurBemerk.length()>0 || sd.nurBemerkNicht.length()>0) {
      ad.auswertungNurFuerBez = International.getString("Bemerkungen");
      ad.auswertungNurFuer = sd.nurBemerk;
      if (sd.nurBemerkNicht.length()>0) {
        if (ad.auswertungNurFuer.length()>0) ad.auswertungNurFuer+= "; ";
        ad.auswertungNurFuer+= International.getMessage("nicht {something}",sd.nurBemerkNicht);
      }
    } else if (sd.nurStegKm) {
      ad.auswertungNurFuerBez = International.getString("Fahrten");
      ad.auswertungNurFuer = International.getString("Start und Ziel ist eigenes Bootshaus");
    } else if (sd.nurMindKm>0) {
      ad.auswertungNurFuerBez = International.getString("Fahrten mit mind.");
      ad.auswertungNurFuer = EfaUtil.zehntelInt2String(sd.nurMindKm)+" Km";
    }

    // Ausgabe für Wettbewerbe nur bei ...
    if (sd.art == StatistikDaten.WETT_DRV ||
        sd.art == StatistikDaten.WETT_LRVBSOMMER || sd.art == StatistikDaten.WETT_LRVBWINTER ||
        sd.art == StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT || sd.art == StatistikDaten.WETT_LRVBRB_FAHRTENWETT ||
        sd.art == StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT) {
      ad.auswertungWettNur = International.getString("erfüllt");
      switch(sd.art) {
        case StatistikDaten.WETT_DRV:
        case StatistikDaten.WETT_LRVBSOMMER:
        case StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT:
        case StatistikDaten.WETT_LRVBRB_FAHRTENWETT:
        case StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT:
          if (sd.wettProz != 100) ad.auswertungWettNur += " "+
                  International.getMessage("oder mind. {percent}% der geforderten Km erreicht",sd.wettProz);
          if (sd.wettProz == 0)   ad.auswertungWettNur = International.getString("alle ausgeben");
          break;
        case StatistikDaten.WETT_LRVBWINTER:
          if (sd.wettProz != 100) ad.auswertungWettNur += " " +
                  International.getMessage("oder mind. {percent}% der geforderten Km und {count} Fahrten erreicht",sd.wettProz,sd.wettFahrten);
          if (sd.wettProz == 0   && sd.wettFahrten == 0) ad.auswertungWettNur = International.getString("alle ausgeben");
          break;
      }
    }

    // TabellenTitel
    String nameBez = International.getString("Name");
    switch (sd.art) {
        case StatistikDaten.ART_MITGLIEDER:
        case StatistikDaten.BART_RUDERER:
        case StatistikDaten.ART_MITRUDERER:
        case StatistikDaten.ART_WERMITWEM:
        case StatistikDaten.ART_WERWOHIN:
        case StatistikDaten.ART_WERMITBOOTSART:
        case StatistikDaten.ART_WERUNERLAUBT:
        case StatistikDaten.ART_WERMITFAHRTART:
            if (!sd.ausgebenMitglnrStattName) {
                nameBez=International.getString("Name");
            } else {
                nameBez=International.getString("MitglNr.");
            }
            break;
        case StatistikDaten.ART_MONATE:
        case StatistikDaten.BART_MONATE:
            nameBez=International.getString("Monat");
            break;
        case StatistikDaten.ART_WOTAGE:
        case StatistikDaten.BART_WOTAGE:
            nameBez=International.getString("Wochentag");
            break;
        case StatistikDaten.ART_TAGESZEIT:
        case StatistikDaten.BART_TAGESZEIT:
            nameBez=International.getString("Tageszeit");
            break;
        case StatistikDaten.ART_JAHRE:
        case StatistikDaten.BART_JAHRE:
            nameBez=International.getString("Jahr");
            break;
        case StatistikDaten.ART_ZIELE:
        case StatistikDaten.BART_ZIELE:
            nameBez=International.getString("Ziel");
            break;
        case StatistikDaten.ART_BOOTE:
        case StatistikDaten.BART_BOOTE:
        case StatistikDaten.BART_WELCHESWOHIN:
            nameBez=International.getString("Boot");
            break;
        case StatistikDaten.ART_BOOTSART:
          nameBez=International.getString("Bootsart");
          break;
        case StatistikDaten.ART_FAHRTART:
          nameBez=International.getString("Fahrtart");
          break;
        case StatistikDaten.ART_STATUS:
          nameBez=International.getString("Status");
          break;
        case StatistikDaten.ART_JAHRGANG:
          nameBez=International.getString("Jahrgang");
          break;
        case StatistikDaten.ART_GESCHLECHT:
          nameBez=International.getString("Geschlecht");
          break;
        case StatistikDaten.BART_ART:
          nameBez=International.getString("Art");
          break;
        case StatistikDaten.BART_ARTDETAIL:
          nameBez=International.getString("Art") +
                  " (" + International.getString("Detail") + ")";
          break;
        case StatistikDaten.BART_PLAETZE:
          nameBez=International.getString("Bootsplätze");
          break;
        case StatistikDaten.ART_KMFAHRT:
        case StatistikDaten.BART_KMFAHRT:
            nameBez=International.getString("Entfernung")+" (Km)";
            break;
    }

    int tabelleBreite;
    if (sd.art != StatistikDaten.ART_FAHRTENBUCH && sd.art != StatistikDaten.BART_FAHRTENBUCH &&
        sd.art != StatistikDaten.ART_MONATSUEBERSICHT &&
            sd.art != StatistikDaten.WETT_DRV && sd.art != StatistikDaten.WETT_LRVBSOMMER &&
            sd.art != StatistikDaten.WETT_LRVBWINTER && sd.art != StatistikDaten.WETT_LRVBWIMPEL &&
            sd.art != StatistikDaten.WETT_DRV_WAFASTATISTIK &&
            sd.art != StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT && sd.art != StatistikDaten.WETT_LRVBRB_FAHRTENWETT &&
            sd.art != StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT) {
          tabelleBreite = 0;
          if (sd.ausgebenNr) tabelleBreite++;
          if (sd.ausgebenName) tabelleBreite++;
          if (sd.ausgebenJahrgang) tabelleBreite++;
          if (sd.ausgebenStatus) tabelleBreite++;
          if (sd.ausgebenBezeichnung && sd.stat == StatistikDaten.STAT_BOOTE) tabelleBreite++;
          if (sd.ausgebenWWAnzVersch) tabelleBreite++;
          if (sd.ausgebenKm || sd.graphischKm) tabelleBreite++;
          if (sd.ausgebenRudKm || sd.graphischRudKm) tabelleBreite++;
          if (sd.ausgebenStmKm || sd.graphischStmKm) tabelleBreite++;
          if (sd.ausgebenFahrten || sd.graphischFahrten) tabelleBreite++;
          if (sd.ausgebenKmFahrt || sd.graphischKmFahrt) tabelleBreite++;
          if (sd.ausgebenDauer || sd.graphischDauer) tabelleBreite++;
          if (sd.ausgebenKmH || sd.graphischKmH) tabelleBreite++;
          if (sd.ausgebenWafaKm && sd.art == StatistikDaten.ART_MITGLIEDER) tabelleBreite++;
          if (sd.ausgebenZielfahrten) tabelleBreite++;
          for (int jj=0; jj<sd.zusatzWett.length; jj++) if (sd.zusatzWett[jj] > 0) tabelleBreite++;
          if (sd.ausgebenWWNamen &&
             (sd.art == sd.ART_WERMITWEM || sd.art == sd.ART_WERWOHIN || sd.art == sd.ART_WERMITBOOTSART ||
              sd.art == sd.ART_WERUNERLAUBT || sd.art == sd.ART_WERMITFAHRTART ||
              sd.art == sd.BART_WELCHESWOHIN)) tabelleBreite = tabelleBreite + alleWW.size();

          ad.tabellenTitel = new String[tabelleBreite];
          ad.tabellenTitelBreite = new int[tabelleBreite];
          int i=0;
          if (sd.ausgebenNr) {
            if (sd.sortierKriterium == StatistikDaten.SORTKRIT_JAHRGANG || sd.sortierKriterium == StatistikDaten.SORTKRIT_NACHNAME ||
                sd.sortierKriterium == StatistikDaten.SORTKRIT_STATUS || sd.sortierKriterium == StatistikDaten.SORTKRIT_VORNAME)
              ad.tabellenTitel[i++] = International.getString("LfdNr");
            else ad.tabellenTitel[i++] = International.getString("Platz");
            ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.ausgebenName){ ad.tabellenTitel[i++] = nameBez; ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.ausgebenJahrgang) {
            if (sd.art != StatistikDaten.ART_BOOTSART) ad.tabellenTitel[i++] = International.getString("Jahrgang");
            else ad.tabellenTitel[i++] = International.getString("Bootsplätze");
            ad.tabellenTitelBreite[i-1] = 1;
          }
          if (sd.ausgebenStatus) {
            if (sd.stat == StatistikDaten.STAT_MITGLIEDER && sd.art != StatistikDaten.ART_BOOTSART) ad.tabellenTitel[i++] = International.getString("Status");
            else ad.tabellenTitel[i++] = International.getString("Art");
             ad.tabellenTitelBreite[i-1] = 1;
          }
          if (sd.ausgebenBezeichnung && sd.stat == StatistikDaten.STAT_BOOTE) {
            ad.tabellenTitel[i++] = International.getString("Bezeichnung");
            ad.tabellenTitelBreite[i-1] = 1;
          }
          if (sd.ausgebenWWAnzVersch) { ad.tabellenTitel[i++] = International.getString("Anzahl Verschiedene"); ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.ausgebenKm || sd.graphischKm) {
            ad.tabellenTitel[i++] = International.getString("Kilometer");
            ad.tabellenTitelBreite[i-1] = (sd.vorjahresvergleich && sd.graphischKm ? 2 : 1);
          }
          if (sd.ausgebenRudKm || sd.graphischRudKm) {
            ad.tabellenTitel[i++] = International.getString("Ruderkilometer");
            ad.tabellenTitelBreite[i-1] = (sd.vorjahresvergleich && sd.graphischRudKm ? 2 : 1);
          }
          if (sd.ausgebenStmKm || sd.graphischStmKm) {
            ad.tabellenTitel[i++] = International.getString("Steuerkilometer");
            ad.tabellenTitelBreite[i-1] = (sd.vorjahresvergleich && sd.graphischStmKm ? 2 : 1);
          }
          if (sd.ausgebenFahrten || sd.graphischFahrten) {
            ad.tabellenTitel[i++] = International.getString("Fahrten");
            ad.tabellenTitelBreite[i-1] = (sd.vorjahresvergleich && sd.graphischFahrten ? 2 : 1);
          }
          if (sd.ausgebenKmFahrt || sd.graphischKmFahrt) { ad.tabellenTitel[i++] = International.getString("Km/Fahrt"); ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.ausgebenDauer || sd.graphischDauer) {
            ad.tabellenTitel[i++] = International.getString("Stunden");
            ad.tabellenTitelBreite[i-1] = (sd.vorjahresvergleich && sd.graphischDauer ? 2 : 1);
          }
          if (sd.ausgebenKmH || sd.graphischKmH) { ad.tabellenTitel[i++] = International.getString("Km/h"); ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.ausgebenWafaKm && sd.art == StatistikDaten.ART_MITGLIEDER) { ad.tabellenTitel[i++] = International.getString("Wafa-Km"); ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.ausgebenZielfahrten) { ad.tabellenTitel[i++] = International.onlyFor("Zielfahrten","de"); ad.tabellenTitelBreite[i-1] = 1; }

          for (int iw=0; iw<Daten.wettDefs.ANZWETT; iw++) {
            boolean wfound = false;
            int wjahr = 0;
            for (int jj=0; !wfound && jj<sd.zusatzWett.length; jj++) {
              if (sd.zusatzWett[jj]-200 == iw) {
                wfound = true;
                wjahr = sd.zusatzWettjahr[jj];
              }
            }
            if (wfound) {
              ad.tabellenTitel[i++] = Daten.wettDefs.getWettDef(iw,wjahr).kurzname + " " + wjahr +
                                      (iw+200 == StatistikDaten.WETT_LRVBWINTER ? "/"+(wjahr+1) : "");
              ad.tabellenTitelBreite[i-1] = 1;
            }
          }

          if (sd.ausgebenWWNamen &&
              (sd.art == sd.ART_WERMITWEM || sd.art == sd.ART_WERWOHIN || sd.art == sd.ART_WERMITBOOTSART ||
               sd.art == sd.ART_WERUNERLAUBT || sd.art == sd.ART_WERMITFAHRTART ||
               sd.art == sd.BART_WELCHESWOHIN)) {
            for (int k=0; k<alleWWArr.length; k++) { ad.tabellenTitel[i++] = alleWWArr[k].name; ad.tabellenTitelBreite[i-1] = 1; }
          }
    }
    if (sd.art == StatistikDaten.ART_FAHRTENBUCH || sd.art == StatistikDaten.BART_FAHRTENBUCH) { // Art: Fahrtenbuch
          tabelleBreite = 0;
          if (sd.fbLfdNr) tabelleBreite++;
          if (sd.fbDatum) tabelleBreite++;
          if (sd.fbBoot) tabelleBreite++;
          if (sd.fbStm) tabelleBreite++;
          if (sd.fbMannsch) tabelleBreite++;
          if (sd.fbAbfahrt) tabelleBreite++;
          if (sd.fbAnkunft) tabelleBreite++;
          if (sd.fbZiel) tabelleBreite++;
          if (sd.fbBootsKm) tabelleBreite++;
          if (sd.fbMannschKm) tabelleBreite++;
          if (sd.fbBemerkungen) tabelleBreite++;

          ad.tabellenTitel = new String[tabelleBreite];
          ad.tabellenTitelBreite = new int[tabelleBreite];

          int i=0;
          if (sd.fbLfdNr)       { ad.tabellenTitel[i++] = International.getString("Lfd. Nr.");       ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbDatum)       { ad.tabellenTitel[i++] = International.getString("Datum");          ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbBoot)        { ad.tabellenTitel[i++] = International.getString("Boot");           ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbStm)         { ad.tabellenTitel[i++] = International.getString("Steuermann");     ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbMannsch)     { ad.tabellenTitel[i++] = International.getString("Mannschaft");     ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbAbfahrt)     { ad.tabellenTitel[i++] = International.getString("Abfahrt");        ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbAnkunft)     { ad.tabellenTitel[i++] = International.getString("Ankunft");        ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbZiel)        { ad.tabellenTitel[i++] = International.getString("Ziel");           ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbBootsKm)     { ad.tabellenTitel[i++] = International.getString("Boots-Km");       ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbMannschKm)   { ad.tabellenTitel[i++] = International.getString("Mannschafts-Km"); ad.tabellenTitelBreite[i-1] = 1; }
          if (sd.fbBemerkungen) { ad.tabellenTitel[i++] = International.getString("Bemerkungen");    ad.tabellenTitelBreite[i-1] = 1; }
    }
    if (sd.art == StatistikDaten.ART_MONATSUEBERSICHT) { // Art: Monatsübersicht
          // nothing to do
    }

    return ad;
  }








  // Berechnung der Ausgabewerte für graphische Ausgabe eines Eintrags:
  // ret[0] = Wert
  // ret[1] = Image-Datei
  // ret[2] = Balkenbreite
  static void ausgabeWert(StatistikDaten sd, boolean ausWert, boolean ausGraWert,
           int wert, int divisor, float fwert, float max, int graSize, int maxSize, String farbe, int typ,
           String[] ret, boolean mitBalken) {
    if (!ausWert && !ausGraWert) return;
    if (divisor == 0) {
        ret[0] = "0";
    } else if (divisor == 1) {
        ret[0] = Integer.toString(wert);
    } else if (divisor == 10) {
      if (sd.nurGanzeKm) {
        ret[0] = Integer.toString(wert/10);
      } else {
        ret[0] = EfaUtil.zehntelInt2String(wert);
      }
    } else {
        ret[0] = Float.toString(EfaUtil.div(wert,divisor));
    }
    if (sd.vorjahresvergleich && wert >0) ret[0] = "+" + ret[0];
    if (sd.vorjahresvergleich && wert==0) ret[0] = "+/- " + ret[0];
    if (!ausGraWert) return;

    int width = (int)((fwert/max)*graSize); // kann ruhig negativ sein, wird bei Ausgabe in Absolutbetrag umgewandelt!
    int basewidth = (maxSize*9/10);
    if (width == 0 && wert != 0) width = 1;
    if (typ == -2) width=0;
    if (!mitBalken) width=0;
    if (Math.abs(graSize)>0)
      if (!sd.cropToMaxSize || width <= basewidth || Math.abs(graSize) <= maxSize) {
        ret[1] = farbe+".gif";
        ret[2] = Integer.toString(width);
      } else {
        ret[1] = farbe+"big.gif";
        ret[2] = Integer.toString(basewidth + (width-basewidth)*(maxSize-basewidth)/(graSize-basewidth));
      }
  }



  // Berechnung der gesamten Wafa-Km in der Datenstruktur (Rückgabe: 100m-Einheiten)
  static int getAllWafaKm(KmWettInfo kmwett) {
    if (kmwett == null || kmwett.wafa == null) return 0;
    Object[] keys = kmwett.wafa.keySet().toArray();
    int km = 0;
    for (int i=0; keys != null && i<keys.length; i++) {
      DRVFahrt fahrt = (DRVFahrt)kmwett.wafa.get(keys[i]);

      // wenn ein Ruderer an einer Mehrtagesfahrt (als einzelne Etappen eingetragen) nur einen Tag
      // mitgerudert ist, werden nur 30 Km gefordert (Dennis, 02.05.03)
      // @gehtnichtmehrinefa2 if (fahrt != null && fahrt.jum == false && fahrt.km >= Daten.WAFAKM && fahrt.days == 1) fahrt.ok = true;

      // @gehtnichtmehrinefa2 if (fahrt.ok && !fahrt.jum) km += fahrt.km;
    }
    return km;
  }



  // Berechnung der Ausgabewerte für die Ausgabe eines Eintrags a:
  // nr ist aktuelle Nummer, -1, wenn Nummer nicht ausgegeben werden soll und -2 beim Gesamtergebnis
  // pos ist der Index von a in arr[]
  static void ausgabeEintrag(AusgabeDaten ad, int absnr, int nr, StatistikDaten sd, ArrEl a, ArrEl[] arr, int pos) {
    AusgabeEintrag ae = new AusgabeEintrag();

    ae.absnr = absnr;

    // Fahrtenbuch?
    if (sd.art == StatistikDaten.ART_FAHRTENBUCH || sd.art == StatistikDaten.BART_FAHRTENBUCH) {
      if (nr != -2) {
        ae.fahrtenbuch = a.fahrtenbuch;
      } else {
        ae.fahrtenbuch = new String[11];
        if (sd.fbLfdNr) ae.fahrtenbuch[0] = "-" + International.getString("ges") + "-";
        if (sd.fbDatum) ae.fahrtenbuch[1] = International.getMessage("{n} Einträge",arr.length);
        if (sd.fbBoot) ae.fahrtenbuch[2] = "";
        if (sd.fbStm) ae.fahrtenbuch[3] = "";
        if (sd.fbMannsch) ae.fahrtenbuch[4] = "";
        if (sd.fbAbfahrt) ae.fahrtenbuch[5] = "";
        if (sd.fbAnkunft) ae.fahrtenbuch[6] = "";
        if (sd.fbZiel) ae.fahrtenbuch[7] = "";
        if (sd.fbBootsKm) ae.fahrtenbuch[8] = EfaUtil.zehntelInt2String(a.rudKm+a.stmKm);
        if (sd.fbMannschKm) ae.fahrtenbuch[9] = EfaUtil.zehntelInt2String(a.mannschKm);
        if (sd.fbBemerkungen) {
          if (sd.fbZielbereichInBemerkungen && !a.zf.toString().equals("")) {
              ae.fahrtenbuch[10] = International.onlyFor("Zielbereiche","de") + ": "+a.zf.toString();
          } else ae.fahrtenbuch[10] = "";
        }
      }
    } else { // kein Fahrtenbuch, keine Monatsübersicht --> normale Felder ausgeben

      // Nummer
      if (sd.ausgebenNr || sd.ausgebenXMLalle) {
        if (nr >= 0) ae.nr = Integer.toString(nr);
        else ae.nr = "";
      }

      // Name
      if (sd.ausgebenName || sd.ausgebenXMLalle) ae.name = a.name;
      if (sd.ausgebenMitglnrStattName && nr != -2 &&
          ( sd.art == StatistikDaten.ART_MITGLIEDER ||
            sd.art == StatistikDaten.ART_MITRUDERER ||
            sd.art == StatistikDaten.ART_WERMITBOOTSART ||
            sd.art == StatistikDaten.ART_WERMITFAHRTART ||
            sd.art == StatistikDaten.ART_WERMITWEM ||
            sd.art == StatistikDaten.ART_WERUNERLAUBT ||
            sd.art == StatistikDaten.ART_WERWOHIN ||
            sd.art == StatistikDaten.BART_RUDERER ) &&
          Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null &&
          ae.name != null) {
        DatenFelder mitgl = Daten.fahrtenbuch.getDaten().mitglieder.getExactComplete(ae.name);
        if (mitgl != null && mitgl.get(Mitglieder.MITGLNR).length() > 0) ae.name = mitgl.get(Mitglieder.MITGLNR);
//        else ae.name = "ohne Mitglnr.";
      }

      // Jahrgang
      if (sd.ausgebenJahrgang || sd.ausgebenXMLalle) ae.jahrgang = a.jahrgang;

      // Status1
      if (sd.ausgebenStatus || sd.ausgebenXMLalle) {
        ae.status = a.status;
        if (ae.status.equals(GAST)) ae.status = GASTBEZ;
        if (ae.status.equals(ANDERE)) ae.status = ANDEREBEZ;
      }

      // Status2
      if (sd.ausgebenBezeichnung || sd.ausgebenXMLalle) ae.bezeichnung = a.bezeichnung;

      // Anzahl Verschiedene
       if (sd.ausgebenWWAnzVersch || sd.ausgebenXMLalle)
        if (a.ww != null)
          if (sd.art == sd.ART_WERMITWEM) ae.anzversch = Integer.toString(a.ww.size()-1);
          else if (sd.art == sd.ART_WERWOHIN || sd.art == sd.BART_WELCHESWOHIN ||
                   sd.art == sd.ART_WERMITBOOTSART || sd.art == sd.ART_WERMITFAHRTART || sd.art == sd.ART_WERUNERLAUBT) ae.anzversch = Integer.toString(a.ww.size());
          else ae.anzversch = "oops"; // no need to translate ;-)
        else ae.anzversch = "";


      boolean mitBalken = true;
      if ((sd.gaesteVereinsweise || sd.gasteAlsEinePerson) && sd.zusammengefassteDatenOhneBalken &&
          a != null && (a.status.equals(GAST) || a.status.equals(ANDERE))) mitBalken = false;

      // Km
      ausgabeWert(sd,sd.ausgebenKm || sd.ausgebenXMLalle,sd.graphischKm,
                  a.rudKm+a.stmKm,10,a.rudKm+a.stmKm,sd.maxKm,sd.graSizeKm,sd.maxSizeKm,COLORKM,nr,
                  ae.km,mitBalken);
      if (sd.graphischKm && sd.vorjahresvergleich) ae.colspanKm = 2;

      // Ruderkilometer
      ausgabeWert(sd,sd.ausgebenRudKm || sd.ausgebenXMLalle,sd.graphischRudKm,
                  a.rudKm,10,a.rudKm,sd.maxRudKm,sd.graSizeRudKm,sd.maxSizeRudKm,COLORRUDKM,nr,
                  ae.rudkm,mitBalken);
      if (sd.graphischRudKm && sd.vorjahresvergleich) ae.colspanRudKm = 2;

      // Steuerkilometer
      ausgabeWert(sd,sd.ausgebenStmKm || sd.ausgebenXMLalle,sd.graphischStmKm,
                  a.stmKm,10,a.stmKm,sd.maxStmKm,sd.graSizeStmKm,sd.maxSizeStmKm,COLORSTMKM,nr,
                  ae.stmkm,mitBalken);
      if (sd.graphischStmKm && sd.vorjahresvergleich) ae.colspanStmKm = 2;

      // Fahrten
      ausgabeWert(sd,sd.ausgebenFahrten || sd.ausgebenXMLalle,sd.graphischFahrten,
                  a.anz,1,a.anz,sd.maxFahrten,sd.graSizeFahrten,sd.maxSizeFahrten,COLORFAHRTEN,nr,
                  ae.fahrten,mitBalken);
      if (sd.graphischFahrten && sd.vorjahresvergleich) ae.colspanFahrten = 2;

      // Km/Fahrt
      ausgabeWert(sd,sd.ausgebenKmFahrt || sd.ausgebenXMLalle,sd.graphischKmFahrt,
                  a.rudKm+a.stmKm,a.anz*10,((float)(a.rudKm+a.stmKm))/((float)a.anz),sd.maxKmFahrt,sd.graSizeKmFahrt,sd.maxSizeKmFahrt,COLORKMFAHRT,nr,
                  ae.kmfahrt,mitBalken);
      if (ae.kmfahrt != null && ae.kmfahrt[0] != null && ae.kmfahrt[0].indexOf(".")<0) ae.kmfahrt[0]+=".0";
      if (sd.graphischKmFahrt && sd.vorjahresvergleich) ae.colspanKmFahrt = 2;

      // Dauer
      ausgabeWert(sd,sd.ausgebenDauer || sd.ausgebenXMLalle,sd.graphischDauer,
                  a.dauer,60,((float)a.dauer)/(float)60.0,sd.maxDauer,sd.graSizeDauer,sd.maxSizeDauer,COLORDAUER,nr,
                  ae.dauer,mitBalken);
      if (ae.dauer != null && ae.dauer[0] != null && ae.dauer[0].indexOf(".")<0) ae.dauer[0]+=".0";
      if (sd.graphischDauer && sd.vorjahresvergleich) ae.colspanDauer = 2;

      // Km/h
      ausgabeWert(sd,sd.ausgebenKmH || sd.ausgebenXMLalle,sd.graphischKmH,
                  (a.rudKm+a.stmKm)*6,a.dauer,((float)((a.rudKm+a.stmKm)*6))/((float)a.dauer),sd.maxKmH,sd.graSizeKmH,sd.maxSizeKmH,COLORKMH,nr,
                  ae.kmh,mitBalken);
      if (ae.kmh != null && ae.kmh[0] != null && ae.kmh[0].indexOf(".")<0) ae.kmh[0]+=".0";
      if (sd.graphischDauer && sd.vorjahresvergleich) ae.colspanDauer = 2;

      // MannschKm
      if (sd.ausgebenXMLalle) ae.mannschKm = EfaUtil.zehntelInt2String(a.mannschKm);
      if (ae.mannschKm != null && ae.mannschKm.indexOf(".")<0) ae.mannschKm+=".0";

      // WafaKm
      if ((sd.ausgebenWafaKm || sd.ausgebenXMLalle) && sd.art == StatistikDaten.ART_MITGLIEDER) ae.wafaKm = EfaUtil.zehntelInt2String(getAllWafaKm(a.kmwett));

      // Zielfahrten
      if (sd.ausgebenZielfahrten || sd.ausgebenXMLalle) ae.zielfahrten = a.zf.toString();

      // Wer mit Wem, Wer Wohin
      if (sd.ausgebenWWNamen &&
          (sd.art == sd.ART_WERMITWEM || sd.art == sd.ART_WERWOHIN || sd.art == sd.ART_WERMITBOOTSART ||
           sd.art == sd.ART_WERMITFAHRTART || sd.art == sd.ART_WERUNERLAUBT || sd.art == sd.BART_WELCHESWOHIN)) ausgabeWW(sd,a,arr,pos,ae);

    }

    // Zusammenfassung?
    if (nr == -2) ae.zusammenfassung = true;

    // Eintrag hinzufügen
    if (letzterAusgabeEintrag == null) ad.ae = ae;
    else letzterAusgabeEintrag.next = ae;
    letzterAusgabeEintrag = ae;
  }




  // Berechnung der Werte für die Ausgabe von WW-Listen
  static void ausgabeWW(StatistikDaten sd, ArrEl a, ArrEl[] arr, int pos, AusgabeEintrag ae) {
    HashEl h;
    String such;
    ae.ww = new String[alleWWArr.length];
    ae.ww_selbst = new boolean[alleWWArr.length];

    for (int i=0; i<alleWWArr.length; i++) {
      if (a.ww != null) {
        such = alleWWArr[i].name;
        if ( (h = (HashEl)a.ww.get(such)) == null) ae.ww[i] = "";
        else { // Tabelleneintrag für Wer mit Wem, Wer Wohin
          boolean c = false;  // ob Semikolon vor nächster Zahl ausgegeben werden muß
          String s = "";      // Ausgabestring
          if (sd.ausgebenKm || sd.graphischKm) {
            s = s + EfaUtil.zehntelInt2String(h.rudKm + h.stmKm);
            c = true;
          }
          if (sd.ausgebenRudKm || sd.graphischRudKm) {
            if (c) s = s + "; ";
            s = s + EfaUtil.zehntelInt2String(h.rudKm);
            c = true;
          }
          if (sd.ausgebenStmKm || sd.graphischStmKm) {
            if (c) s = s + "; ";
            s = s + EfaUtil.zehntelInt2String(h.stmKm);
            c = true;
          }
          if (sd.ausgebenFahrten || sd.graphischFahrten) {
            if (c) s = s + "; ";
            s = s + Integer.toString(h.anz);
            c = true;
          }
          if (sd.ausgebenKmFahrt || sd.graphischKmFahrt) {
            if (c) s = s + "; ";
            s = s + Float.toString(EfaUtil.div(h.rudKm+h.stmKm,h.anz*10));
            c = true;
          }

          ae.ww[i] = s;
          if (a.name.equals(alleWWArr[i].name)) ae.ww_selbst[i] = true;
          else ae.ww_selbst[i] = false;
        }
      } else ae.ww[i] = ""; // Gesamtkilometer (Zusammenrechnung) - hier wird nix ausgegeben
    }
  }




  // Erstellen der Daten für die Ausgabe
  static void ausgabe(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a, ArrEl ges)  {
    if (sd.art == StatistikDaten.WETT_LRVBSOMMER) {
      ausgabeKmWettLRVBSommer(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_LRVBWINTER) {
      ausgabeKmWettLRVBWinter(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_DRV) {
// @gehtnichtmehrinefa2       ausgabeKmWettDRV(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_LRVBWIMPEL) {
      ausgabeKmWettLRVBWimpel(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_DRV_WAFASTATISTIK) {
      ausgabeKmWettDRVWafaStat(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT) {
// @gehtnichtmehrinefa2       ausgabeKmWettLRVBrbWanderruderWett(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_LRVBRB_FAHRTENWETT) {
      ausgabeKmWettLRVBrbFahrtenWett(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT) {
      ausgabeKmWettLRVMVpWanderruderWett(ad,sd,a);
      return;
    }
    if (sd.art == StatistikDaten.ART_MONATSUEBERSICHT) {
      ausgabeMonatsuebersicht(ad,sd,a);
      return;
    }
    int gesnr = 1, nr = 1;
    for (int i=0; i<a.length; i++) {
      boolean count = false;
      if (sd.stat == StatistikDaten.STAT_MITGLIEDER) {
        for (int j=0; j<Daten.fahrtenbuch.getDaten().status.length; j++) {
          if (sd.numeriere[j] && a[i].status.equals(Daten.fahrtenbuch.getDaten().status[j])) count = true;
        }
        if (sd.art != StatistikDaten.ART_MITGLIEDER && sd.art != StatistikDaten.ART_MITRUDERER &&
            sd.art != StatistikDaten.ART_WERMITWEM && sd.art != StatistikDaten.ART_WERWOHIN &&
            sd.art != StatistikDaten.ART_WERMITBOOTSART && sd.art != StatistikDaten.ART_WERMITFAHRTART &&
            sd.art != StatistikDaten.ART_WERUNERLAUBT &&
            sd.art != StatistikDaten.BART_WELCHESWOHIN &&
            sd.numeriere[0]) count = true;
      }
      if (sd.stat == StatistikDaten.STAT_BOOTE) {
        // zuerst aus dem Originalnamen ggf. ein Synonymnamen machen, damit der Name in der Bootsliste (bei Kombibooten) überhaupt gefunden werden kann
        String name_tmp = a[i].name;
        Vector namen_tmp = EfaUtil.org2syn(Daten.synBoote,name_tmp);
        if (namen_tmp != null && namen_tmp.size()>0) name_tmp = (String)namen_tmp.get(0); // Synoym? Wenn ja, dann 1. Synonym verwenden
        if (sd.numeriere[0]) { // numeriere eigene Boote
          if (a[i].name.indexOf("(")<0 &&
              (Daten.fahrtenbuch.getDaten().boote.getExact(a[i].name) != null || Daten.fahrtenbuch.getDaten().boote.getExact(name_tmp) != null)) count = true;
        }
        if (sd.numeriere[1]) { // numeriere fremde Botoe
          if (a[i].name.indexOf("(")>=0 ||
              (Daten.fahrtenbuch.getDaten().boote.getExact(a[i].name) == null && Daten.fahrtenbuch.getDaten().boote.getExact(name_tmp) == null)) count = true;
        }
      }
      if (count) {
        ausgabeEintrag(ad,i,nr,sd,a[i],a,i);
        gesnr++;
      }
      else ausgabeEintrag(ad,i,-1,sd,a[i],a,i);
      if (i+1<a.length) {
        switch(sd.sortierKriterium) {
          case StatistikDaten.SORTKRIT_KM:
            if (a[i+1].rudKm + a[i+1].stmKm != a[i].rudKm + a[i].stmKm) nr = gesnr;
            break;
          case StatistikDaten.SORTKRIT_FAHRTEN:
            if (a[i+1].anz != a[i].anz) nr = gesnr;
            break;
          case StatistikDaten.SORTKRIT_KMFAHRT:
            if (EfaUtil.div(a[i+1].rudKm+a[i+1].stmKm,a[i+1].anz) != EfaUtil.div(a[i].rudKm+a[i].stmKm,a[i].anz)) nr = gesnr;
            break;
          case StatistikDaten.SORTKRIT_ANZVERSCH:
            if (a[i+1].ww.size() != a[i].ww.size()) nr = gesnr;
            break;
          default:
            nr = gesnr;
        }
      }
    }
    if (ges != null)
      ausgabeEintrag(ad,-1,-2,sd,ges,a,-1);
  }





  // Zusammenrechnen der Daten
  static ArrEl zusammenrechnen(StatistikDaten sd, ArrEl[] a)  {
    Object[] wwKeys = null;
    if (alleWW != null) {
      wwKeys = alleWW.keySet().toArray();
    }

    ArrEl ges = new ArrEl("--- " + International.getString("gesamt") + " ("+a.length+") ---","","","",0,0,0,0,0,new ZielfahrtFolge(),(alleWW != null ? new Hashtable() : null),null,null);
    int gesWafaKm = 0;
    for (int i=0; i<a.length; i++) {
      ges.anz += a[i].anz;
      ges.rudKm += a[i].rudKm;
      ges.stmKm += a[i].stmKm;
      ges.mannschKm += a[i].mannschKm;
      ges.dauer += a[i].dauer;
      ges.zf.addZielfahrten(a[i].zf);
      if (sd.ausgebenWafaKm && sd.art == StatistikDaten.ART_MITGLIEDER) gesWafaKm += getAllWafaKm(a[i].kmwett);

      // Summenbildung für WW-Statistiken
      for (int j=0; wwKeys != null && j<wwKeys.length; j++) {
        HashEl sww = (HashEl)(a[i].ww != null ? a[i].ww.get(wwKeys[j]) : null);
        HashEl ges_sww = (HashEl)(ges.ww != null ? ges.ww.get(wwKeys[j]) : null);
        if (sww != null) {
          if (ges_sww == null) {
            ges_sww = new HashEl();
            ges_sww.zf = new ZielfahrtFolge();
          }
          ges_sww.anz += sww.anz;
          ges_sww.rudKm += sww.rudKm;
          ges_sww.stmKm += sww.stmKm;
          ges_sww.mannschKm += sww.mannschKm;
          ges_sww.dauer += sww.dauer;
          ges_sww.zf.addZielfahrten(sww.zf);
          ges.ww.put(wwKeys[j],ges_sww);
        }
      }

    }
    if (sd.ausgebenWafaKm && sd.art == StatistikDaten.ART_MITGLIEDER && gesWafaKm > 0) {
      ges.kmwett = new KmWettInfo();
      DRVFahrt fahrt = new DRVFahrt();
      // @gehtnichtmehrinefa2 fahrt.days = 1; fahrt.km = gesWafaKm; fahrt.ok = true;
      ges.kmwett.wafa.put("dummy",fahrt);
    }
    ges.zf.reduceToMinimun();

    return ges;
  }





  // Eine int-Zahl (Jahrgang) in einen String umwandeln
  static String makeJahrgang(int jahr) {
    if (jahr<=0) return "????";
    else return Integer.toString(jahr);
  }



  // Eine int-Zahl (Geschlecht) in einen String umwandeln
  static String makeGeschlecht(int g) {
    switch (g) {
      case 0: return International.getString("m","gender");
      case 1: return International.getString("w","gender");
      default: return International.getString("m/w","gender");
    }
  }




  // Prüfen, ob gewählter Zeitraum tatsächlich den Wettbewerbsbedingungen entspricht; true, falls korrekt
  public static boolean checkWettZeitraum(int wettJahr, TMJ von, TMJ bis, int wettnr) {
    WettDef wett = Daten.wettDefs.getWettDef(wettnr,wettJahr);
    if (wett == null) return false;
    return (von.tag == wett.von.tag &&
            von.monat == wett.von.monat &&
            bis.tag == wett.bis.tag &&
            bis.monat == wett.bis.monat &&
            von.jahr == wettJahr+wett.von.jahr &&
            bis.jahr == wettJahr+wett.bis.jahr);
  }




  // String für Ausgabe der Zielfahrten
  static String zfAusgabeString(String szf, int izf, boolean bzf, boolean kurz, boolean mitAnford, int geforderteFahrten) {
    if (szf.length() == 0) izf=0;
    if (!bzf) return null;
    String s;
    if (kurz) {
        // not translated (only Berlin, GER
      if (mitAnford) s = izf + "/" + geforderteFahrten + " Zf";
      else s = izf + " Zf";
    } else s = izf + " Zielfahrt" + (izf != 1 ? "en" : "");
    return s + (izf==0 ? "" : ": ") + szf;
  }

// @ZF@
  static boolean zfErfuellt(Zielfahrt[] zf) {
    if (zf == null || zf.length<4 || zf[0] == null || zf[1] == null || zf[2] == null || zf[3] == null) return false;

    // auf zu geringe Km und doppeltes Datum prüfen
    Vector datum = new Vector();
    for (int i=0; i<4; i++) {
      if (EfaUtil.zehntelString2Int(zf[i].getKm()) < 200) return false;
      if (zf[i].getDatum().length()==0 || datum.contains(zf[i].getDatum())) return false;
      datum.add(zf[i].getDatum());
    }

    // Zielbereiche der einzelnen Fahrten in Arrays umwandeln
    String[] zb0 = zf[0].getBereicheAsArray();
    String[] zb1 = zf[1].getBereicheAsArray();
    String[] zb2 = zf[2].getBereicheAsArray();
    String[] zb3 = zf[3].getBereicheAsArray();

    // wurden vier unterschiedliche Zielbereiche in je einer der Fahrten erreicht?
    for (int b0=0; b0<zb0.length; b0++)
      for (int b1=0; b1<zb1.length; b1++)
        for (int b2=0; b2<zb2.length; b2++)
          for (int b3=0; b3<zb3.length; b3++) {
            Vector zbs = new Vector();
            if (!zbs.contains(zb0[b0])) zbs.add(zb0[b0]);
            if (!zbs.contains(zb1[b1])) zbs.add(zb1[b1]);
            if (!zbs.contains(zb2[b2])) zbs.add(zb2[b2]);
            if (!zbs.contains(zb3[b3])) zbs.add(zb3[b3]);
            if (zbs.size() == 4) return true;
          }

    return false;
  }


// @ZF@
  static Zielfahrt[] getBestZf(Vector zielfahrten) {
    int size = zielfahrten.size();
    if (size<4) return null;

    Zielfahrt[] zf = new Zielfahrt[4];

    for (int f0=0; f0<size-3; f0++)
      for (int f1=f0+1; f1<size-2; f1++)
        for (int f2=f1+1; f2<size-1; f2++)
          for (int f3=f2+1; f3<size; f3++) {
            zf[0] = (Zielfahrt)zielfahrten.get(f0);
            zf[1] = (Zielfahrt)zielfahrten.get(f1);
            zf[2] = (Zielfahrt)zielfahrten.get(f2);
            zf[3] = (Zielfahrt)zielfahrten.get(f3);
            if (zfErfuellt(zf)) return zf;
          }

    return null;
  }

  static Zielfahrt[] getAdditionalZf(Vector zielfahrten, Zielfahrt[] bestZf) {
    Vector additional = new Vector();
    for (int i=0; i<zielfahrten.size(); i++) {
      Zielfahrt zf = (Zielfahrt)zielfahrten.get(i);
      if (zf == null) continue;
      if (EfaUtil.zehntelString2Int(zf.getKm()) < 200) continue;
      boolean doppelt = false;
      for (int j=0; bestZf != null && j<bestZf.length; j++) {
        if (zf == bestZf[j]) {
          doppelt = true;
          break;
        }
      }
      if (doppelt) continue;
      additional.add(zf);
    }
    if (additional.size() == 0) return null;
    Zielfahrt[] _additional = new Zielfahrt[additional.size()];
    for (int i=0; i<additional.size(); i++) {
      _additional[i] = (Zielfahrt)additional.get(i);
    }
    return _additional;
  }


  // Ausgabedaten für Kilometerwettbewerbe erstellen (LRV Sommer)
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettLRVBSommer(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.LRVBERLIN_SOMMER,sd.wettJahr);
    WettDefGruppe[] gruppen = wett.gruppen;
    DatenFelder akt;
    int jahrgang;
    int anzInGruppe; // wievielter Ruderer in der Gruppe: die ersten 3 brauchen eine Adresse!
    int geskm=0;
    int gesanz=0;

    // Zielfahrten für alle Ruderer aufbereiten und auswählen
    for (int i=0; i<a.length; i++) {
      // suche vier passende Zielfahrten
      a[i].kmwett.zielfahrtenFinal = getBestZf(a[i].kmwett.zielfahrten);
      a[i].kmwett.zielfahrtenAdditional = getAdditionalZf(a[i].kmwett.zielfahrten,a[i].kmwett.zielfahrtenFinal);
    }

      if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

      if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.LRVBERLIN_SOMMER))
        ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

      ad.wett_gruppennamen = new String[gruppen.length][3];
      ad.wett_teilnehmerInGruppe = new AusgabeEintrag[gruppen.length];
      for (int g=0; g<gruppen.length; g++) {
        ad.wett_gruppennamen[g][0] = "Gruppe "+gruppen[g].bezeichnung+")";
        ad.wett_gruppennamen[g][1] = "Jahrgänge "+makeJahrgang(sd.wettJahr-gruppen[g].hoechstalter)+
                                     " - "+makeJahrgang(sd.wettJahr-gruppen[g].mindalter)+
                                     " ("+makeGeschlecht(gruppen[g].geschlecht)+")";
        ad.wett_gruppennamen[g][2] = gruppen[g].km+" Kilometer"+
                            (gruppen[g].zusatz>0 ? "; mind. "+gruppen[g].zusatz+" Zielfahrten" : "");

        // Alle Teilnehmer in einer gegebenen Gruppe durchlaufen
        anzInGruppe=0;
        for (int i=0; i<a.length; i++) {
          if (!a[i].jahrgang.equals("") &&
              Daten.wettDefs.inGruppe(WettDefs.LRVBERLIN_SOMMER,sd.wettJahr,g,Integer.parseInt(a[i].jahrgang),a[i].kmwett.geschlecht,a[i].kmwett.behinderung)) {
            // Teilnehmer ist in der Gruppe!
            int countedZf = countZf(a[i].kmwett.zielfahrtenFinal,a[i].zf);
            boolean erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.LRVBERLIN_SOMMER,sd.wettJahr,g,Integer.parseInt(a[i].jahrgang),a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,countedZf,0,0,0);

            if (erfuellt) {
              gesanz++;
              geskm += a[i].rudKm+a[i].stmKm;
            }

            // sollen Daten für den Teilnehmer ausgegeben werden?
            if (erfuellt ||
                (((a[i].rudKm+a[i].stmKm)/10 >= gruppen[g].km*sd.wettProz/100) && sd.wettProz<100)) {
              anzInGruppe++;
              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                // Ausgabe für efaWett
                if (erfuellt) {
                  EfaWettMeldung ewm = new EfaWettMeldung();
                  ewm.nachname = EfaUtil.getNachname(a[i].name);
                  ewm.vorname = EfaUtil.getVorname(a[i].name);
                  ewm.jahrgang = a[i].jahrgang;
                  ewm.gruppe = gruppen[g].bezeichnung;
                  if (a[i].kmwett.geschlecht == 0) ewm.geschlecht = EfaWettMeldung.GESCHLECHT_M;
                  else if (a[i].kmwett.geschlecht == 1) ewm.geschlecht = EfaWettMeldung.GESCHLECHT_W;
                  else ewm.geschlecht = "X";
                  ewm.kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                  if (anzInGruppe<=3) {
                    DatenFelder d = (Daten.adressen != null ? Daten.adressen.getExactComplete(ewm.vorname+" "+ewm.nachname) : null);
                    if (d != null) {
                      ewm.anschrift = d.get(Adressen.ADRESSE);
                    } else {
                      ewm.anschrift=""; // muß noch eingetragen werden! (im Gegensatz zu "null": wird nicht mehr abgefragt!)
                    }
                  }
                  for (int j=0; a[i].kmwett.zielfahrtenFinal != null && j<a[i].kmwett.zielfahrtenFinal.length; j++) {
                    if (a[i].kmwett.zielfahrtenFinal[j] != null) {
                      for (int jj=0; jj<4; jj++) {
                        switch(jj) {
                          case 0: ewm.fahrt[j][jj] = a[i].kmwett.zielfahrtenFinal[j].getDatum(); break;
                          case 1: ewm.fahrt[j][jj] = a[i].kmwett.zielfahrtenFinal[j].getZiel(); break;
                          case 2: ewm.fahrt[j][jj] = a[i].kmwett.zielfahrtenFinal[j].getKm(); break;
                          case 3: ewm.fahrt[j][jj] = a[i].kmwett.zielfahrtenFinal[j].getBereiche(); break;
                        }
                      }
                    }
                  }
                  if (a[i].kmwett.zielfahrtenFinal != null && a[i].kmwett.zielfahrtenAdditional != null && a[i].kmwett.zielfahrtenAdditional.length > 0) {
                    int lengthBefore = a[i].kmwett.zielfahrtenFinal.length;
                    for (int j=0; j<a[i].kmwett.zielfahrtenAdditional.length; j++) {
                      if (a[i].kmwett.zielfahrtenAdditional[j] != null && j+lengthBefore < ewm.fahrt.length) {
                      for (int jj=0; jj<4; jj++) {
                          switch(jj) {
                            case 0: ewm.fahrt[j+lengthBefore][jj] = a[i].kmwett.zielfahrtenAdditional[j].getDatum(); break;
                            case 1: ewm.fahrt[j+lengthBefore][jj] = a[i].kmwett.zielfahrtenAdditional[j].getZiel(); break;
                            case 2: ewm.fahrt[j+lengthBefore][jj] = a[i].kmwett.zielfahrtenAdditional[j].getKm(); break;
                            case 3: ewm.fahrt[j+lengthBefore][jj] = a[i].kmwett.zielfahrtenAdditional[j].getBereiche(); break;
                          }
                        }
                      }
                    }
                  }
                  if (efaWett.letzteMeldung() == null) efaWett.meldung = ewm;
                  else efaWett.letzteMeldung().next = ewm;
                }
              } else {
                // normale Ausgabe des Teilnehmers
                AusgabeEintrag ae = new AusgabeEintrag();
                ae.w_name      = a[i].name;
                ae.w_kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                if (!erfuellt && sd.zusatzWettMitAnforderung) ae.w_kilometer+="/"+gruppen[g].km;
                if (!sd.wettOhneDetail && erfuellt) {
                  ae.w_jahrgang = a[i].jahrgang;
                  int _ausgabeZfAnzahl = (a[i].kmwett.zielfahrtenFinal != null ? a[i].kmwett.zielfahrtenFinal.length : 0) + 
                                         (a[i].kmwett.zielfahrtenAdditional != null && a[i].kmwett.zielfahrtenAdditional.length > 0 ?
                                             (sd.alleZielfahrten ? a[i].kmwett.zielfahrtenAdditional.length : 1) : 0);
                  ae.w_detail   = new String[_ausgabeZfAnzahl][4];
                  for (int j=0; a[i].kmwett.zielfahrtenFinal != null && j<a[i].kmwett.zielfahrtenFinal.length; j++) {
                    if (a[i].kmwett.zielfahrtenFinal[j] != null) {
                      ae.w_detail[j] = a[i].kmwett.zielfahrtenFinal[j].toStringArray();
                    }
                  }
                  for (int j=0; sd.alleZielfahrten && a[i].kmwett.zielfahrtenAdditional != null && j<a[i].kmwett.zielfahrtenAdditional.length; j++) {
                    if (a[i].kmwett.zielfahrtenAdditional[j] != null) {
                      ae.w_detail[j + (a[i].kmwett.zielfahrtenFinal != null ? a[i].kmwett.zielfahrtenFinal.length : 0)] = a[i].kmwett.zielfahrtenAdditional[j].toStringArray();
                    }
                  }
                } else {
                  ae.w_additional = zfAusgabeString(a[i].zf.toString(),countedZf,gruppen[g].zusatz>0,sd.wettKurzAusgabe,sd.zusatzWettMitAnforderung,gruppen[g].zusatz);
                  ae.w_attr1 = Integer.toString(countedZf);
                  if (!erfuellt && countedZf < a[i].zf.getAnzZielfahrten()) ae.w_warnung = "möglicherweise mehrere Zielfahrten am selben Tag";
                }
                ae.w_erfuellt = erfuellt;

                // Eintrag hinzufügen
                if (ad.wett_teilnehmerInGruppe[g] == null) ad.wett_teilnehmerInGruppe[g] = ae;
                else letzterAusgabeEintrag.next = ae;
                letzterAusgabeEintrag = ae;
              }
            }
          } else {
            // Teilnehmer ohne Jahrgang
            if (a[i].jahrgang.equals("") &&
                Daten.wettDefs.erfuellt(WettDefs.LRVBERLIN_SOMMER,sd.wettJahr,0,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,countZf(a[i].kmwett.zielfahrtenFinal,a[i].zf),0,0,0) != null &&
                nichtBeruecksichtigt.get(a[i].name) == null) {
                  nichtBeruecksichtigt.put(a[i].name,"Wegen fehlenden Jahrgangs ignoriert (" + EfaUtil.zehntelInt2String(a[i].stmKm+a[i].rudKm) + " Km)");
                  continue;
            }
          }
        }
      }
      if (sd.ausgabeArt != sd.AUSGABE_EFAWETT) {
        ad.additionalTable = new String[2][2];
        ad.additionalTable[0][0] = "Anzahl der Erfüller:";
        ad.additionalTable[0][1] = Integer.toString(gesanz);
        ad.additionalTable[1][0] = "Kilometer aller Erfüller:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
      }
  }





  // Ausgabedaten für Kilometerwettbewerbe erstellen (LRV Winter)
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettLRVBWinter(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.LRVBERLIN_WINTER,sd.wettJahr);
    WettDefGruppe[] gruppen = wett.gruppen;
    DatenFelder akt;
    int jahrgang;
    int geskm=0;
    int gesanz=0;

      if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

      if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.LRVBERLIN_WINTER))
        ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

      ad.wett_gruppennamen = new String[gruppen.length][3];
      ad.wett_teilnehmerInGruppe = new AusgabeEintrag[gruppen.length];

      for (int g=0; g<gruppen.length; g++) {
        ad.wett_gruppennamen[g][0] = "Gruppe "+gruppen[g].bezeichnung+")";
        ad.wett_gruppennamen[g][1] = "Jahrgänge "+makeJahrgang(sd.wettJahr-gruppen[g].hoechstalter)+
                                     " - "+makeJahrgang(sd.wettJahr-gruppen[g].mindalter);
        ad.wett_gruppennamen[g][2] = gruppen[g].km+" Kilometer"+
                            (gruppen[g].zusatz>0 ? "; mind. "+gruppen[g].zusatz+" Fahrten" : "") +
                            (gruppen[g].zusatz2>0 ? " in "+gruppen[g].zusatz2+" Monaten" : "");

        // Alle Teilnehmer in einer gegebenen Gruppe durchlaufen
        for (int i=0; i<a.length; i++) {
          if (!a[i].jahrgang.equals("") &&
              Daten.wettDefs.inGruppe(WettDefs.LRVBERLIN_WINTER,sd.wettJahr,g,Integer.parseInt(a[i].jahrgang),a[i].kmwett.geschlecht,a[i].kmwett.behinderung)) {

            // Teilnehmer ist in der Gruppe!
            boolean erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.LRVBERLIN_WINTER,sd.wettJahr,g,Integer.parseInt(a[i].jahrgang),a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,a[i].kmwett.winterAnz,a[i].kmwett.anzMonate,0,0);

            if (erfuellt) {
              gesanz++;
              geskm += a[i].rudKm+a[i].stmKm;
            }

            // sollen Daten für den Teilnehmer ausgegeben werden?
            if (erfuellt ||
                (((a[i].rudKm+a[i].stmKm)/10 >= gruppen[g].km*sd.wettProz/100 && a[i].kmwett.winterAnz>=sd.wettFahrten && sd.wettProz<100)) ) {

              EfaWettMeldung ewm = null;


              // bereits geruderte Monate ermitteln (für Ausgabe, wenn nicht erfüllt)
              String monate="";
              for (int m=0; m<a[i].kmwett.winterfahrten.length; m++)
                if (a[i].kmwett.winterfahrten[m][0][0] != null)
                  switch (m) {
                    case 0: monate = monate + (monate.equals("") ? "" : ", ") + (sd.wettKurzAusgabe?"Nov":"November"); break;
                    case 1: monate = monate + (monate.equals("") ? "" : ", ") + (sd.wettKurzAusgabe?"Dez":"Dezember"); break;
                    case 2: monate = monate + (monate.equals("") ? "" : ", ") + (sd.wettKurzAusgabe?"Jan":"Januar"); break;
                    case 3: monate = monate + (monate.equals("") ? "" : ", ") + (sd.wettKurzAusgabe?"Feb":"Februar"); break;
                    case 4: monate = monate + (monate.equals("") ? "" : ", ") + (sd.wettKurzAusgabe?"Mar":"März"); break;
                    case 5: monate = monate + (monate.equals("") ? "" : ", ") + (sd.wettKurzAusgabe?"Apr":"April"); break;
                  }

              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                // Ausgabe für efaWett
                if (erfuellt) {
                  ewm = new EfaWettMeldung();
                  ewm.nachname = EfaUtil.getNachname(a[i].name);
                  ewm.vorname = EfaUtil.getVorname(a[i].name);
                  ewm.jahrgang = a[i].jahrgang;
                  ewm.gruppe = gruppen[g].bezeichnung;
                  if (a[i].kmwett.geschlecht == 0) ewm.geschlecht = EfaWettMeldung.GESCHLECHT_M;
                  else if (a[i].kmwett.geschlecht == 1) ewm.geschlecht = EfaWettMeldung.GESCHLECHT_W;
                  else ewm.geschlecht = "X";
                  ewm.kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                }
              }

              // normale Ausgabe des Teilnehmers
              AusgabeEintrag ae = new AusgabeEintrag();
              ae.w_name      = a[i].name;
              ae.w_kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
              if (!erfuellt && sd.zusatzWettMitAnforderung) ae.w_kilometer+="/"+gruppen[g].km;
              if (!sd.wettOhneDetail && erfuellt) {
                ae.w_jahrgang = a[i].jahrgang;
                ae.w_detail   = new String[gruppen[g].zusatz + 
                        (a[i].kmwett.winterAnz > gruppen[g].zusatz ? 1 : 0)][3]; // eine Fahrt mehr für den Hinweis "weitere Fahrten"
              } else {
                // Warnung, wenn Fahrten nicht gewertet wurden
                boolean warnung = !erfuellt && a[i].anz > a[i].kmwett.winterAnz;

                if (sd.wettKurzAusgabe) {
                  if (sd.zusatzWettMitAnforderung) {
                    ae.w_additional = a[i].anz+
                                      (warnung ? " ("+a[i].kmwett.winterAnz+")" : "") +
                                      "/"+gruppen[g].zusatz+" F in "+
                                      a[i].kmwett.anzMonate+"/"+gruppen[g].zusatz2+" M";
                  } else {
                    ae.w_additional = a[i].anz+
                                      (warnung ? " ("+a[i].kmwett.winterAnz+")" : "") +
                                      " F in "+a[i].kmwett.anzMonate+" M";
                  }
                } else {
                  ae.w_additional = a[i].anz+" Fahrten in "+a[i].kmwett.anzMonate+" Monaten";
                }
                if (!erfuellt)
                  ae.w_additional += (monate.equals("") ? "" : " ("+monate+")");

                if (!sd.wettKurzAusgabe && warnung) ae.w_additional += " (davon nur "+a[i].kmwett.winterAnz+" wertbare Fahrten, da mehrere Fahrten am selben Tag)";
              }
              ae.w_attr1 = Integer.toString(a[i].anz);
              ae.w_attr2 = Integer.toString(a[i].kmwett.anzMonate);
              ae.w_erfuellt = erfuellt;

              // Berechnung der Winterfahrten (beide Ausgabemodi)
              if (erfuellt && !sd.wettOhneDetail) {
                int c=0;
                int teilkm=0; // Km in den gruppen[g][WettDefs.G_ZUSATZ] (8) Fahrten
                int gefm=0;  // Anzahl der bereits gefundenen Monate, in denen Fahrten vorlegen
                for (int m=0; m<a[i].kmwett.winterfahrten.length; m++)
                  for (int j=0; j<a[i].kmwett.winterfahrten[m].length; j++) {
                    if (j == 0 && a[i].kmwett.winterfahrten[m][j][0] != null) gefm++;
                    if (c<gruppen[g].zusatz && gruppen[g].zusatz-c>a[i].kmwett.anzMonate-gefm && a[i].kmwett.winterfahrten[m][j][0] != null) {
                      if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                        ewm.fahrt[c] = a[i].kmwett.winterfahrten[m][j];
                      } else {
                        ae.w_detail[c] = a[i].kmwett.winterfahrten[m][j];
                      }
                      c++;
                      teilkm += EfaUtil.zehntelString2Int(a[i].kmwett.winterfahrten[m][j][2]);
                    }
                  }
                }

              // Eintrag hinzufügen

              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                if (efaWett.letzteMeldung() == null) efaWett.meldung = ewm;
                else efaWett.letzteMeldung().next = ewm;
              } else {
                if (ad.wett_teilnehmerInGruppe[g] == null) ad.wett_teilnehmerInGruppe[g] = ae;
                else letzterAusgabeEintrag.next = ae;
                letzterAusgabeEintrag = ae;
              }

            }
          } else {
            // Teilnehmer ohne Jahrgang
            if (a[i].jahrgang.equals("") &&
                Daten.wettDefs.erfuellt(WettDefs.LRVBERLIN_WINTER,sd.wettJahr,0,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,a[i].kmwett.winterAnz,a[i].kmwett.anzMonate,0,0) != null &&
                nichtBeruecksichtigt.get(a[i].name) == null) {
                  nichtBeruecksichtigt.put(a[i].name,"Wegen fehlenden Jahrgangs ignoriert (" + EfaUtil.zehntelInt2String(a[i].stmKm+a[i].rudKm) + " Km)");
                  continue;
            }
          }
        }
      }
      if (sd.ausgabeArt != sd.AUSGABE_EFAWETT) {
        ad.additionalTable = new String[2][2];
        ad.additionalTable[0][0] = "Anzahl der Erfüller:";
        ad.additionalTable[0][1] = Integer.toString(gesanz);
        ad.additionalTable[1][0] = "Kilometer aller Erfüller:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
      }
  }




/*
  // Ausgabedaten für Kilometerwettbewerbe erstellen (DRV)
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettDRV(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.DRV_FAHRTENABZEICHEN,sd.wettJahr);
    WettDefGruppe[] gruppen = wett.gruppen;
    DatenFelder akt;
    int jahrgang;
    Vector ungueltigeFahrtenhefte = new Vector();
    Vector zumErstenMalGemeldet = new Vector();
    int letzteElektronischeMeldung = 0;
    int anzahlGemeldeteTeilnehmer = 0;
    int geskm=0;
    int gesanz=0;

      if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

    if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.DRV_FAHRTENABZEICHEN))
      ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

    if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
      efaWett.verein_mitglnr = Daten.vereinsConfig.mitgliedsnummerDRV;
      efaWett.meld_kto = Daten.vereinsConfig.meldenderKto;
      efaWett.meld_bank = Daten.vereinsConfig.meldenderBank;
      efaWett.meld_blz = Daten.vereinsConfig.meldenderBLZ;
    }

    ad.wett_gruppennamen = new String[gruppen.length][3];
    ad.wett_teilnehmerInGruppe = new AusgabeEintrag[gruppen.length];

      for (int g=0; g<gruppen.length; g++) {
        if (gruppen[g].gruppe == 1)
          ad.wett_gruppennamen[g][0] = "Männer "+gruppen[g].bezeichnung+")";
        else if (gruppen[g].gruppe == 2)
          ad.wett_gruppennamen[g][0] = "Frauen "+gruppen[g].bezeichnung+")";
        else
          ad.wett_gruppennamen[g][0] = "Jugend "+gruppen[g].bezeichnung+")";
        ad.wett_gruppennamen[g][1] = "Jahrgänge "+makeJahrgang(sd.wettJahr-gruppen[g].hoechstalter)+
                                     " - "+makeJahrgang(sd.wettJahr-gruppen[g].mindalter)+
                                     " ("+makeGeschlecht(gruppen[g].geschlecht)+")";
        ad.wett_gruppennamen[g][2] = gruppen[g].km+" Kilometer"+
                            (gruppen[g].zusatz>0 ? "; davon "+gruppen[g].zusatz+
                                     (gruppen[g].gruppe == 3 ? " Tage Wanderfahrten" : " Km Wanderfahrten") : "");

        // Alle Teilnehmer in einer gegebenen Gruppe durchlaufen
        for (int i=0; i<a.length; i++) {
          if (!a[i].jahrgang.equals("")) jahrgang = Integer.parseInt(a[i].jahrgang);
          else jahrgang = -1;

          if (!a[i].jahrgang.equals("") &&
              Daten.wettDefs.inGruppe(WettDefs.DRV_FAHRTENABZEICHEN,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung)) {
            // Teilnehmer ist in der Gruppe!

            // Wanderfahrten zusammenstellen
            boolean mehrFahrten = false;
            String[][] wafa = new String[7][6]; // 7 Einträge mit jeweils LfdNr/Abfahrt/Ankunft/Ziel/Km/Bemerk
            Object[] keys = a[i].kmwett.wafa.keySet().toArray(); // Keys ermitteln
            boolean[] ausg = new boolean[keys.length]; // merken, welche Fahrt schon zur Ausgabe markiert wurde
            for (int k=0; k<ausg.length; k++) ausg[k]=false; // erstmal: keine Fahrt markiert
            int hoechst,hoechstEl=0; // zum Ermitteln des höchsten verbleibenden Elements
            int wafaKm=0; // Wafa-Km aller auszugebenden Fahrten
            int wafaAnzMTour=0; // für Gruppe 3: Anzahl der Tage durch Mehrtagestouren
            int jumAnz=0;       // für Gruppe 3 a/b: Anzahl der JuM-Regatten
            DRVFahrt drvel=null,bestEl=null;
            for (int nr=0; nr<wafa.length+1; nr++) { // max. für 7 auszufüllende Felder Fahrten suchen (plus 1 weitere, die aber nicht gemerkt wird)
              hoechst=0; // höchste verbleibende KmZahl oder Tagezahl

              // nächste geeignete Fahrt heraussuchen (meiste Km (Gruppe<3) oder längste Tour (Gruppe 3))
              for (int k=0; k<ausg.length; k++) {
                drvel = (DRVFahrt)a[i].kmwett.wafa.get(keys[k]);

                // wenn ein Ruderer an einer Mehrtagesfahrt (als einzelne Etappen eingetragen) nur einen Tag
                // mitgerudert ist, werden nur 30 Km gefordert (Dennis, 02.05.03)
                // @gehtnichtmehrinefa2 if (drvel != null && drvel.jum == false && drvel.km >= Daten.WAFAKM && drvel.days == 1) drvel.ok = true;

                if (!ausg[k] &&                                                   // Fahrt, die noch nicht ausgegeben wurde, ...
                     drvel != null &&                                             // und die wirklich vorhanden ist, außerdem:
                      // @gehtnichtmehrinefa2 (  (gruppen[g].gruppe!=3 && drvel.ok && drvel.km>hoechst) ||    // Gruppe 1/2: Fahrt "ok", d.h. >30 bzw. >40 Km
                         ( gruppen[g].gruppe == 3 && drvel.days>hoechst &&         // Gruppe 3:
                            (drvel.days>1 || drvel.jum && gruppen[g].untergruppe<=2)    // echte Mehrtagesfahrt oder JuM bei Gr. 3 a/b
                         )
                       )
                   ) {
                  bestEl = drvel;
                  if (gruppen[g].gruppe != 3) hoechst = drvel.km;
                  // @gehtnichtmehrinefa2 else hoechst = drvel.days;
                  hoechstEl = k;
                }
              }
              if (hoechst != 0 && nr >= wafa.length) {
                  hoechst = 0;
                  mehrFahrten = true; // merken, daß es mehr Fahrten als die ausgegebenen gibt
              }
              if (hoechst>0 && // was gefunden?
                  (nr<5 ||     // weniger als 5 Einträge, oder ...
                   (wafaKm/10 < gruppen[g].zusatz && gruppen[g].gruppe != 3) || // noch Km nötig
                   (wafaAnzMTour<3 && gruppen[g].gruppe == 3) ) ) {         // noch Fahrten nötig
                ausg[hoechstEl] = true;
                wafa[nr][0] = bestEl.entryNo;
                wafa[nr][1] = bestEl.dateStart;
                wafa[nr][2] = bestEl.dateEnd;
                wafa[nr][3] = bestEl.destination;
                wafa[nr][4] = EfaUtil.zehntelInt2String(bestEl.km);
                wafa[nr][5] = bestEl.comments;
                if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && bestEl.jum) wafa[nr][5] = EfaWettMeldung.JUM;
                if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && !bestEl.jum && wafa[nr][5].equals(EfaWettMeldung.JUM)) wafa[nr][5] = "";
                wafaKm += bestEl.km;
                if (!bestEl.jum) wafaAnzMTour += bestEl.days;
                else jumAnz++;
              }
            }
            for (int sx=0; sx<wafa.length; sx++)
              for (int sy=sx+1; sy<wafa.length; sy++)
                if (wafa[sx][0] != null && wafa[sy][0] != null &&
                    EfaUtil.compareIntString(wafa[sy][0],wafa[sx][0]) < 0) {
                  String[] tmp = wafa[sx];
                  wafa[sx] = wafa[sy];
                  wafa[sy] = tmp;
                }


            // sollen für den Teilnehmer Daten ausgegeben werden?
            boolean erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.DRV_FAHRTENABZEICHEN,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,wafaKm/10,wafaAnzMTour,jumAnz,0);

            if (erfuellt) {
              gesanz++;
              geskm += a[i].rudKm+a[i].stmKm;
            }

            if (erfuellt ||
                (((a[i].rudKm+a[i].stmKm)/10 >= gruppen[g].km*sd.wettProz/100) && sd.wettProz<100) ) {

              int wafaLength;
              for (wafaLength=0; wafaLength<7 && wafa[wafaLength][0] != null; wafaLength++);

              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                // Ausgabe für efaWett
                if (erfuellt) {
                  EfaWettMeldung ewm = new EfaWettMeldung();
                  ewm.nachname = EfaUtil.getNachname(a[i].name);
                  ewm.vorname = EfaUtil.getVorname(a[i].name);
                  ewm.jahrgang = a[i].jahrgang;
                  ewm.gruppe = gruppen[g].bezeichnung;
                  if (a[i].kmwett.geschlecht == 0) ewm.geschlecht = EfaWettMeldung.GESCHLECHT_M;
                  else if (a[i].kmwett.geschlecht == 1) ewm.geschlecht = EfaWettMeldung.GESCHLECHT_W;
                  else ewm.geschlecht = "X";
                  int hundertm = (a[i].rudKm+a[i].stmKm) % 10;
                  // Kilometer auf- oder abrunden auf ganze Kilometer!
                  ewm.kilometer = Integer.toString((a[i].rudKm+a[i].stmKm+ (hundertm >= 5 ? 10-hundertm : 0) ) / 10);

                  // Fahrtenheft
                  boolean goldErw = false;
                  boolean goldJug = false;
                  int gesKm = 0;
                  int gesKmAB = 0;
                  int anzAbzeichen = 0;
                  int anzAbzeichenAB = 0;
                  if (Daten.fahrtenabzeichen != null) {
                    DatenFelder fahrtenheft = Daten.fahrtenabzeichen.getExactComplete(ewm.vorname+" "+ewm.nachname);
                    if (fahrtenheft != null) {
                      if (fahrtenheft.get(Fahrtenabzeichen.LETZTEMELDUNG).length()>0) {
                        DRVSignatur drvSignatur = new DRVSignatur(fahrtenheft.get(Fahrtenabzeichen.LETZTEMELDUNG));
                        if (drvSignatur.getSignatureState() == DRVSignatur.SIG_UNKNOWN_KEY) {
                          Dialog.infoDialog("Schlüssel nicht bekannt",
                                            "efa hat für den Teilnehmer "+ewm.vorname+" "+ewm.nachname+"\n"+
                                            "ein elektronisches Fahrtenheft gefunden, kann dessen Gültigkeit\n"+
                                            "aber nicht prüfen, da der Schlüssel unbekannt ist.\n"+
                                            "Im folgenden Dialog wirst Du daher aufgefordert, den Schlüssel\n"+
                                            "aus dem Internet herunterzuladen.");
                          if (DRVSignaturFrame.downloadKey(drvSignatur.getKeyName())) {
                            drvSignatur.checkSignature();
                          }
                        }
                        if (drvSignatur.getSignatureState() == DRVSignatur.SIG_VALID) {
                          ewm.drv_fahrtenheft = drvSignatur.toString();
                          gesKm = drvSignatur.getGesKm();
                          gesKmAB = drvSignatur.getGesKmAB();
                          anzAbzeichen = drvSignatur.getAnzAbzeichen();
                          anzAbzeichenAB = drvSignatur.getAnzAbzeichenAB();
                        } else {
                          ungueltigeFahrtenhefte.add(ewm.vorname+" "+ewm.nachname);
                        }
                        if (drvSignatur.getJahr() > letzteElektronischeMeldung) letzteElektronischeMeldung = drvSignatur.getJahr();
                      }
                      if (ewm.drv_fahrtenheft == null) {
                        anzAbzeichen = EfaUtil.string2int(fahrtenheft.get(Fahrtenabzeichen.ANZABZEICHEN),0);
                        gesKm = EfaUtil.string2int(fahrtenheft.get(Fahrtenabzeichen.GESKM),0);
                        anzAbzeichenAB = EfaUtil.string2int(fahrtenheft.get(Fahrtenabzeichen.ANZABZEICHENAB),0);
                        gesKmAB = EfaUtil.string2int(fahrtenheft.get(Fahrtenabzeichen.GESKMAB),0);
                        if (anzAbzeichen > 0 && gesKm > 0) {
                          ewm.drv_anzAbzeichen = fahrtenheft.get(Fahrtenabzeichen.ANZABZEICHEN);
                          ewm.drv_gesKm = fahrtenheft.get(Fahrtenabzeichen.GESKM);
                          ewm.drv_anzAbzeichenAB = fahrtenheft.get(Fahrtenabzeichen.ANZABZEICHENAB);
                          ewm.drv_gesKmAB = fahrtenheft.get(Fahrtenabzeichen.GESKMAB);
                        } else {
                          zumErstenMalGemeldet.add(ewm.vorname+" "+ewm.nachname);
                        }
                      }
                    } else {
                      zumErstenMalGemeldet.add(ewm.vorname+" "+ewm.nachname);
                    }
                  }

                  // Äquatorpreis
                  int aeqKm = gesKm; // - gesKmAB; (seit 2007 zählen auch die Kilometer AB zum Äquatorpreis)
                  int anzAeqBefore = aeqKm / WettDefs.DRV_AEQUATOR_KM;
                  int anzAeqJetzt  = (aeqKm + EfaUtil.string2int(ewm.kilometer,0)) / WettDefs.DRV_AEQUATOR_KM;
                  if (anzAeqJetzt > anzAeqBefore) ewm.drv_aequatorpreis = Integer.toString(anzAeqJetzt);

                  // Abzeichen
                  ewm.abzeichen = WettDefs.getDRVAbzeichen(gruppen[g].gruppe != 3,anzAbzeichen,anzAbzeichenAB,sd.wettJahr);

                  for (int j=0; j<wafaLength; j++)
                    ewm.fahrt[j] = wafa[j];
                  if (efaWett.letzteMeldung() == null) efaWett.meldung = ewm;
                  else efaWett.letzteMeldung().next = ewm;

                  anzahlGemeldeteTeilnehmer++;
                }
              } else {
                // normale Ausgabe des Teilnehmers
                AusgabeEintrag ae = new AusgabeEintrag();
                ae.w_name      = a[i].name;
                ae.w_kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                if (!erfuellt && sd.zusatzWettMitAnforderung) ae.w_kilometer+="/"+gruppen[g].km;
                if (!sd.wettOhneDetail && erfuellt) {
                  ae.w_jahrgang = a[i].jahrgang;
                  ae.w_detail   = new String[wafaLength + (mehrFahrten ? 1 : 0)][6]; // ein zusätzliches Arrayelement (nicht gefüllt), wenn weitere Fahrten vorliegen
                  for (int j=0; j<wafaLength; j++) {
                    ae.w_detail[j] = wafa[j];
                  }
                } else {
                  if (gruppen[g].gruppe<3) {
                    if (sd.wettKurzAusgabe) {
                      if (sd.zusatzWettMitAnforderung) ae.w_additional = EfaUtil.zehntelInt2String(getAllWafaKm(a[i].kmwett))+"/"+gruppen[g].zusatz+" Wafa-Km";
                      else ae.w_additional = EfaUtil.zehntelInt2String(getAllWafaKm(a[i].kmwett))+" Wafa-Km";
                    } else ae.w_additional = EfaUtil.zehntelInt2String(getAllWafaKm(a[i].kmwett))+" Wanderfahrt-Km";
                    ae.w_attr1 = EfaUtil.zehntelInt2String(getAllWafaKm(a[i].kmwett));
                  } else {
                    if (sd.wettKurzAusgabe) {
                      if (sd.zusatzWettMitAnforderung) ae.w_additional = wafaAnzMTour+"/"+gruppen[g].zusatz+" Wafa-Tage";
                      else ae.w_additional = wafaAnzMTour+" Wafa-Tage";
                    } else ae.w_additional = wafaAnzMTour+" Tage durch Wanderfahrten";
                    ae.w_attr2 = Integer.toString(wafaAnzMTour);
                  }
                  if (gruppen[g].gruppe==3 && gruppen[g].untergruppe<=2) {
                    if (sd.wettKurzAusgabe) {
                      ae.w_additional += ", "+jumAnz+" JuM";
                    } else ae.w_additional += ", "+jumAnz+" JuM-Regatten";
                    ae.w_attr1 = Integer.toString(jumAnz);
                  }
                }
                ae.w_erfuellt = erfuellt;

                // Eintrag hinzufügen
                if (ad.wett_teilnehmerInGruppe[g] == null) ad.wett_teilnehmerInGruppe[g] = ae;
                else letzterAusgabeEintrag.next = ae;
                letzterAusgabeEintrag = ae;
              }
            }
          } else {
            // Teilnehmer ohne Jahrgang
            if (a[i].jahrgang.equals("") &&
                Daten.wettDefs.erfuellt(WettDefs.DRV_FAHRTENABZEICHEN,sd.wettJahr,0,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,9999,9999,9999,0) != null &&
                nichtBeruecksichtigt.get(a[i].name) == null) {
                  nichtBeruecksichtigt.put(a[i].name,"Wegen fehlenden Jahrgangs ignoriert (" + EfaUtil.zehntelInt2String(a[i].stmKm+a[i].rudKm) + " Km)");
                  continue;
            }
          }
        }
      }
    if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && anzahlGemeldeteTeilnehmer > 0 && ungueltigeFahrtenhefte.size() > 0) {
      Dialog.infoDialog("Warnung",
                        "Die elektronischen Fahrtenhefte folgender Teilnehmer wurden\n"+
                        "NICHT berücksichtigt, da ihre Gültigkeit nicht überprüft werden\n"+
                        "konnte. Bitte prüfe unter ->Administration->DRV-Fahrtenhefte\n"+
                        "den Grund für die Ungültigkeit der Fahrtenhefte.\n"+
                        "Hinweis: Für die elektronisch Meldung müssen die elektronischen Fahrtenhefte\n"+
                        "aller Teilnehmer gültig sein, da sonst der Nachweis der bereits erworbenen\n"+
                        "Fahrtenabzeichen für diese Teilnehmer auf herkömmlichem Weg (durch Einsenden\n"+
                        "der Papier-Fahrtenhefte) erfolgen muß.\n"+
                        "Teilnehmer mit ungültigen Fahrtenheften:\n"+
                        EfaUtil.vector2string(ungueltigeFahrtenhefte,"\n"));
    }
    if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && anzahlGemeldeteTeilnehmer > 0 && zumErstenMalGemeldet.size() > 0) {
      String info;
      if (anzahlGemeldeteTeilnehmer == zumErstenMalGemeldet.size()) {
        info = "Keiner der gemeldeten Teilnehmer hat jemals zuvor ein\n"+
               "Fahrtenabzeichen erworben, weder auf elektronische, noch\n"+
               "auf herkömmliche Weise.";
      } else {
        info = "Folgende Teilnehmer haben bislang noch nie ein\n"+
               "Fahrtenabzeichen erworben, weder auf elektronische,\n"+
               "noch auf herkömmliche Weise:\n"+
               EfaUtil.vector2string(zumErstenMalGemeldet,"\n");
      }
      if (Dialog.yesNoDialog("Erstes Fahrtenabzeichen?",
                             info+"\n"+
                             "Ist dies korrekt?") != Dialog.YES) {
        Dialog.infoDialog("Erworbene Fahrtenabzeichen nachtragen",
                          "Bitte trage unter ->Administration->DRV-Fahrtenabzeichen die\n"+
                          "bereits erworbenen Fahrtenabzeichen aller Teilnehmer ein und\n"+
                          "erstelle anschließend eine neue Meldedatei.");
        sd.abbruchEfaWett = true;
        return;
      }
    }
    if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && anzahlGemeldeteTeilnehmer > 0 && letzteElektronischeMeldung+1 != sd.wettJahr) {
      if (letzteElektronischeMeldung == 0) {
        if (Dialog.yesNoDialog("Erste elektronische Meldung?",
                               "efa hat keine elektronischen Fahrtenhefte für die Teilnehmer\n"+
                               "finden können. Sollte es sich bei dieser Meldung um die erste\n"+
                               "elektronische Meldung handeln, so ist dies korrekt.\n"+
                               "Ist dies die erste elektronische Meldung des Vereins?") != Dialog.YES) {
          Dialog.infoDialog("Bestätigungsdatei abrufen",
                            "Wenn Du bereits zuvor elektronisch gemeldet hast, so rufe bitte\n"+
                            "zunächst unter ->Administration->DRV-Fahrtenabzeichen über den Punkt\n"+
                            "'Bestätigungsdatei abrufen' die Bestätigungsdatei der letzten Meldung\n"+
                            "ab. Sie enthält die elektronischen Fahrtenhefte der damals gemeldeten\n"+
                            "Teilnehmer. Anschließend erstelle bitte eine neue Meldedatei.");
          sd.abbruchEfaWett = true;
          return;
        }
      } else if (letzteElektronischeMeldung+1 < sd.wettJahr) {
        if (Dialog.yesNoDialog("Letzte elektronische Meldung?",
                               "Die letzte in efa gespeicherte Bestätigungsdatei stammt aus dem Jahr\n"+
                               letzteElektronischeMeldung+". Eventuell in späteren Jahren gemeldete\n"+
                               "Fahrtenabzeichen liegen efa nicht vor und werden daher NICHT berücksichtigt.\n\n"+
                               "Ist es richtig, daß Du zum letzten Mal für das Jahr "+letzteElektronischeMeldung+" elektronisch gemeldet hast?") != Dialog.YES) {
          Dialog.infoDialog("Bestätigungsdatei abrufen",
                            "Anscheinend hast Du nach Deiner letzten elektronischen Meldung vergessen,\n"+
                            "die Bestätigungsdatei abzurufen. Rufe daher bitte zunächst unter\n"+
                            "->Administration->DRV-Fahrtenabzeichen über den Punkt 'Bestätigungsdatei abrufen'\n"+
                            "die Bestätigungsdatei der letzten Meldung ab. Sie enthält die elektronischen\n"+
                            "Fahrtenhefte der damals gemeldeten Teilnehmer. Anschließend erstelle bitte eine\n"+
                            "neue Meldedatei.");
          sd.abbruchEfaWett = true;
          return;
        }
      } else {
        Dialog.infoDialog("Bereits für dieses Jahr gemeldet",
                          "Es liegen für einige Teilnehmer bereits vom DRV bestätigte Fahrtenhefte aus dem Jahr "+sd.wettJahr+" vor.\n"+
                          "Du kannst daher für das Jahr "+sd.wettJahr+" nicht erneut melden.\n\n"+
                          "Falls Deine Meldung nachträglich vom DRV zurückgewiesen wurde und Du nun eine korrigierte Meldung\n"+
                          "einsenden möchtest, mußt Du zuvor alle elektronischen Fahrtenhefte des Jahres "+sd.wettJahr+" in efa\n"+
                          "löschen und die elektronischen Fahrtenhefte bis "+(sd.wettJahr-1)+" erneut einspielen. Gehe dazu in\n"+
                          "die Übersicht der DRV-Fahrtenabzeichen unter 'Administration - DRV-Fahrtenabzeichen', lösche dort alle\n"+
                          "Fahrtenabzeichen des Jahres "+sd.wettJahr+" und spiele anschließend alle Bestätiungsdateien bis zum\n"+
                          "Jahr "+(sd.wettJahr-1)+" erneut ein.");
        sd.abbruchEfaWett = true;
        return;
      }
    }
      if (sd.ausgabeArt != sd.AUSGABE_EFAWETT) {
        ad.additionalTable = new String[2][2];
        ad.additionalTable[0][0] = "Anzahl der Erfüller:";
        ad.additionalTable[0][1] = Integer.toString(gesanz);
        ad.additionalTable[1][0] = "Kilometer aller Erfüller:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
      }
  }
*/





  // Ausgabedaten für Kilometerwettbewerbe erstellen (Blauer Wimpel)
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettLRVBWimpel(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.LRVBERLIN_BLAUERWIMPEL,sd.wettJahr);
    int anzWertung = 20 + (int)(0.1 * sd.anzMitglieder); // Anzahl der zu wertenden Mitglieder

      if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

    if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.LRVBERLIN_BLAUERWIMPEL))
      ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

      int geskm=0;


      if (!sd.wettOhneDetail) {
        ad.tabellenTitel = new String[3];
        ad.tabellenTitel[0] = "Nummer";
        ad.tabellenTitel[1] = "Name";
        ad.tabellenTitel[2] = "Kilometer";
        ad.tabellenTitelBreite = new int[3];
        Arrays.fill(ad.tabellenTitelBreite,1);
      }

      for (int i=0; i<a.length && i<anzWertung; i++) {
        if (!sd.wettOhneDetail) {
          if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
            EfaWettMeldung ewm = new EfaWettMeldung();
            ewm.nachname = EfaUtil.getNachname(a[i].name);
            ewm.vorname = EfaUtil.getVorname(a[i].name);
            ewm.kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
            if (efaWett.letzteMeldung() == null) efaWett.meldung = ewm;
            else efaWett.letzteMeldung().next = ewm;
          } else {
            AusgabeEintrag ae = new AusgabeEintrag();
            ae.absnr = i;
            ae.nr = Integer.toString(i+1);
            ae.name = a[i].name;
            ae.km[0] = EfaUtil.zehntelInt2String(a[i].rudKm + a[i].stmKm);
            if (letzterAusgabeEintrag == null) ad.ae = ae;
            else letzterAusgabeEintrag.next = ae;
            letzterAusgabeEintrag = ae;
          }

        }
        geskm += a[i].rudKm + a[i].stmKm;
      }

      if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
        // setzen der Gesamtwerte in der efaWett-Datei
        // wird beim OK-Klicken in EfaWettSelectAndCompleteFrame gesetzt
      } else {
        ad.additionalTable = new String[3][2];
        ad.additionalTable[0][0] = "Anzahl der ausgewerteten Ruderer:";
        ad.additionalTable[0][1] = anzWertung+" (von "+sd.anzMitglieder+" Mitgliedern)";
        ad.additionalTable[1][0] = "Gesamtkilometer der ersten "+anzWertung+" Ruderer:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
        ad.additionalTable[2][0] = "Durchschnittskilometer pro Ruderer:";
        ad.additionalTable[2][1] = EfaUtil.zehntelInt2String(EfaUtil.intdiv(geskm,anzWertung));
      }
  }



  // Ausgabedaten für Kilometerwettbewerbe erstellen (DRV Wanderruderstatistik)
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettDRVWafaStat(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.DRV_WANDERRUDERSTATISTIK,sd.wettJahr);

    if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

    if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.DRV_WANDERRUDERSTATISTIK))
      ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

    if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
      efaWett.verein_mitglnr = Daten.vereinsConfig.mitgliedsnummerDRV;
      efaWett.verein_ort = Daten.vereinsConfig.vereinsort;
      efaWett.verein_lrv = Daten.vereinsConfig.lrvname;
      efaWett.verein_mitgl_in = (Daten.vereinsConfig.mitglDRV ? "DRV" : "") + ";" +
                                (Daten.vereinsConfig.mitglSRV ? "SRV" : "") + ";" +
                                (Daten.vereinsConfig.mitglADH ? "ADH" : "");
    }

    ad.ausgabeZeilenOben = new StatOutputLines();
    ad.ausgabeZeilenOben.addLine("Name des Vereins:|"+Daten.vereinsConfig.vereinsname,2,StatOutputLines.FONT_BOLD);
    ad.ausgabeZeilenOben.addLine("Ort:|"+Daten.vereinsConfig.vereinsort,2,StatOutputLines.FONT_BOLD);
    ad.ausgabeZeilenOben.addLine("DRV Mitgliedsnummer:|"+Daten.vereinsConfig.mitgliedsnummerDRV,2,StatOutputLines.FONT_BOLD);
    ad.ausgabeZeilenOben.addLine("LRV:|"+Daten.vereinsConfig.lrvname,2,StatOutputLines.FONT_BOLD);
    String mitgl = "";
    if (Daten.vereinsConfig.mitglDRV) mitgl = "DRV";
    if (Daten.vereinsConfig.mitglSRV) mitgl += (mitgl.length()>0 ? ", " : "") + "SRV";
    if (Daten.vereinsConfig.mitglADH) mitgl += (mitgl.length()>0 ? ", " : "") + "ADH";
    ad.ausgabeZeilenOben.addLine("Mitglied im:|"+mitgl,2,StatOutputLines.FONT_BOLD);

    ad.ausgabeZeilenUnten = new StatOutputLines();
    ad.ausgabeZeilenUnten.addLine("Anschrift des Wanderruderwartes bzw. des Ausfüllers: ",1,StatOutputLines.FONT_BOLD);
    ad.ausgabeZeilenUnten.addLine(Daten.vereinsConfig.versandName,1,StatOutputLines.FONT_NORMAL);
    if (!Daten.vereinsConfig.versandName.equals(Daten.vereinsConfig.meldenderName)) ad.ausgabeZeilenUnten.addLine("c/o "+Daten.vereinsConfig.meldenderName,1,StatOutputLines.FONT_NORMAL);
    ad.ausgabeZeilenUnten.addLine(Daten.vereinsConfig.versandStrasse,1,StatOutputLines.FONT_NORMAL);
    ad.ausgabeZeilenUnten.addLine(Daten.vereinsConfig.versandOrt,1,StatOutputLines.FONT_NORMAL);
    ad.ausgabeZeilenUnten.addLine("",1,StatOutputLines.FONT_NORMAL);
    ad.ausgabeZeilenUnten.addLine("Unterschrift, Vereinsstempel: ",1,StatOutputLines.FONT_BOLD);

    String[] tabellentitel = new String[14];
    tabellentitel[ 0] = "Start + Ziel der Fahrt";
    tabellentitel[ 1] = "Gewässer";
    tabellentitel[ 2] = "Gesamt Km";
    tabellentitel[ 3] = "Gesamt Tage";
    tabellentitel[ 4] = "Anz. Teilnehmer";
    tabellentitel[ 5] = "Mannsch-Km";
    tabellentitel[ 6] = "Männer (Anz)";
    tabellentitel[ 7] = "Männer (Km)";
    tabellentitel[ 8] = "Junioren (Anz)";
    tabellentitel[ 9] = "Junioren (Km)";
    tabellentitel[10] = "Frauen (Anz)";
    tabellentitel[11] = "Frauen (Km)";
    tabellentitel[12] = "Juniorinnen (Anz)";
    tabellentitel[13] = "Juniorinnen (Km)";
    ad.tabellenTitelBreite = new int[14];
    Arrays.fill(ad.tabellenTitelBreite,1);

    // lösche Mehrtagesfahrten, die gar keine sind
    int anzMtours = 0;
    for (int i=0; i<a.length; i++) {
      if (a[i] != null && a[i].kmwett != null) {
        int tmpGesKm = getSumOfAllHashEntries(a[i].kmwett.drvWafaStat_etappen);
        if ((a[i].kmwett.drvWafaStat_gesTage <= 1 && tmpGesKm < 300) || // mind. 30 Km
            (a[i].kmwett.drvWafaStat_gesTage >  1 && tmpGesKm < 400)) { // mind. 40 Km
          a[i] = null;
        } else {
          anzMtours++;
        }
      } else a[i] = null;
    }

    final int SPALTENTITEL_UNTEN_AB_EINTRAEGEN = 15;
    ad.additionalTable = new String[anzMtours+2 + (anzMtours>SPALTENTITEL_UNTEN_AB_EINTRAEGEN ? 1 : 0)][14];
    ad.additionalTable_1stRowBold = true;
    ad.additionalTable[0] = tabellentitel;
    if (anzMtours>SPALTENTITEL_UNTEN_AB_EINTRAEGEN) { // Tabellentitel unten wiederholen
      ad.additionalTable[ad.additionalTable.length-1] = tabellentitel;
      ad.additionalTable_lastRowBold = true;
    }

    int _gesKm = 0, _gesTage = 0, _gesMannschKm = 0,
        _gesTeilnMueber18 = 0, _gesTeilnMbis18 = 0, _gesTeilnFueber18 = 0, _gesTeilnFbis18 = 0,
        _gesKmTeilnMueber18 = 0, _gesKmTeilnMbis18 = 0, _gesKmTeilnFueber18 = 0, _gesKmTeilnFbis18 = 0;
    Vector gewaesser = new Vector();
    int pos = 0; // Position in ad.additionalTable
    int nichtGewerteteEintraege = 0;
    for (int i=0; i<a.length; i++) {
      if (a[i] == null) continue; // Wafa gelöscht, da sie die Kriterien nicht erfüllte!
      pos++;
      int tmp;
      ad.additionalTable[pos][ 0] = a[i].name;
      if (ad.additionalTable[pos][0].indexOf(" ##")>=0 && ad.additionalTable[pos][0].endsWith("##"))
          ad.additionalTable[pos][0] = ad.additionalTable[pos][0].substring(0,ad.additionalTable[pos][0].indexOf(" ##"));

      if (a[i].kmwett != null) {
        boolean wirdGewertet = true;

        if (ad.additionalTable[pos][0].trim().length() == 0) {
          wirdGewertet = false;
          ad.additionalTable[pos][0] = "KEIN ZIELNAME (WIRD NICHT ANERKANNT)";
        }

        ad.additionalTable[pos][ 1] = a[i].kmwett.drvWafaStat_gewaesser;
        String stmp = EfaUtil.replace(a[i].kmwett.drvWafaStat_gewaesser,";",",",true);
        Vector v = EfaUtil.split(stmp,',');
        for (int j=0; v != null && j<v.size(); j++) if (v.get(j).toString().trim().length()>0 && !gewaesser.contains(v.get(j))) gewaesser.add(v.get(j).toString().trim());

        int tmpGesKm = getSumOfAllHashEntries(a[i].kmwett.drvWafaStat_etappen); _gesKm += tmpGesKm;
        ad.additionalTable[pos][ 2] = EfaUtil.zehntelInt2String(tmpGesKm);

        _gesTage += a[i].kmwett.drvWafaStat_gesTage;
        ad.additionalTable[pos][ 3] = Integer.toString(a[i].kmwett.drvWafaStat_gesTage);

        int tmpAnzTeiln = a[i].kmwett.drvWafaStat_teilnFbis18.size()+a[i].kmwett.drvWafaStat_teilnFueber18.size()+
                          a[i].kmwett.drvWafaStat_teilnMbis18.size()+a[i].kmwett.drvWafaStat_teilnMueber18.size();
        ad.additionalTable[pos][ 4] = Integer.toString(tmpAnzTeiln);

        _gesMannschKm += a[i].kmwett.drvWafaStat_mannschKm;
        ad.additionalTable[pos][ 5] = EfaUtil.zehntelInt2String(a[i].kmwett.drvWafaStat_mannschKm);

        // Plausi-Test
        if (tmpGesKm * tmpAnzTeiln < a[i].kmwett.drvWafaStat_mannschKm) {
          warnungen += "Die berechneten Gesamt- und Mannschaftskilometer für die Mehrtagesfahrt '"+a[i].name+"'\n"+
                       "sind unstimmig. Bitte überprüfe, ob alle Einträge zu dieser Fahrt korrekt sind, insb. ob\n"+
                       "alle Etappennamen unterschiedlich sind und kein Ruderer auf einer Etappe mehrfach vorkommt!\n";
          ad.additionalTable[pos][ 0] += " (UNSTIMMIG - WIRD NICHT ANERKANNT)";
          wirdGewertet = false;
        }

        _gesTeilnMueber18 += a[i].kmwett.drvWafaStat_teilnMueber18.size();
        ad.additionalTable[pos][ 6] = Integer.toString(a[i].kmwett.drvWafaStat_teilnMueber18.size());

        tmp = getSumOfAllHashEntries(a[i].kmwett.drvWafaStat_teilnMueber18); _gesKmTeilnMueber18 += tmp;
        ad.additionalTable[pos][ 7] = EfaUtil.zehntelInt2String(tmp);

        _gesTeilnMbis18 += a[i].kmwett.drvWafaStat_teilnMbis18.size();
        ad.additionalTable[pos][ 8] = Integer.toString(a[i].kmwett.drvWafaStat_teilnMbis18.size());

        tmp = getSumOfAllHashEntries(a[i].kmwett.drvWafaStat_teilnMbis18); _gesKmTeilnMbis18 += tmp;
        ad.additionalTable[pos][ 9] = EfaUtil.zehntelInt2String(tmp);

        _gesTeilnFueber18 += a[i].kmwett.drvWafaStat_teilnFueber18.size();
        ad.additionalTable[pos][10] = Integer.toString(a[i].kmwett.drvWafaStat_teilnFueber18.size());

        tmp = getSumOfAllHashEntries(a[i].kmwett.drvWafaStat_teilnFueber18); _gesKmTeilnFueber18 += tmp;
        ad.additionalTable[pos][11] = EfaUtil.zehntelInt2String(tmp);

        _gesTeilnFbis18 += a[i].kmwett.drvWafaStat_teilnFbis18.size();
        ad.additionalTable[pos][12] = Integer.toString(a[i].kmwett.drvWafaStat_teilnFbis18.size());

        tmp = getSumOfAllHashEntries(a[i].kmwett.drvWafaStat_teilnFbis18); _gesKmTeilnFbis18 += tmp;
        ad.additionalTable[pos][13] = EfaUtil.zehntelInt2String(tmp);

        if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && wirdGewertet) {
          EfaWettMeldung ewm = new EfaWettMeldung();
          ewm.drvWS_StartZiel = ad.additionalTable[pos][0];
          ewm.drvWS_Gewaesser = ad.additionalTable[pos][1];
          ewm.drvWS_Km = ad.additionalTable[pos][2];
          ewm.drvWS_Tage = ad.additionalTable[pos][3];
          ewm.drvWS_Teilnehmer = ad.additionalTable[pos][4];
          ewm.drvWS_MannschKm = ad.additionalTable[pos][5];
          ewm.drvWS_MaennerAnz = ad.additionalTable[pos][6];
          ewm.drvWS_MaennerKm = ad.additionalTable[pos][7];
          ewm.drvWS_JuniorenAnz = ad.additionalTable[pos][8];
          ewm.drvWS_JuniorenKm = ad.additionalTable[pos][9];
          ewm.drvWS_FrauenAnz = ad.additionalTable[pos][10];
          ewm.drvWS_FrauenKm = ad.additionalTable[pos][11];
          ewm.drvWS_JuniorinnenAnz = ad.additionalTable[pos][12];
          ewm.drvWS_JuniorinnenKm = ad.additionalTable[pos][13];
          if (efaWett.meldung == null) efaWett.meldung = ewm;
          else efaWett.letzteMeldung().next = ewm;
        }

        if (!wirdGewertet) nichtGewerteteEintraege++;

      }
    }

    if (nichtGewerteteEintraege>0) {
      warnungen += ""+nichtGewerteteEintraege+" Fahrten enthalten ungültige Eintragungen "+
                   "und können daher nicht gewertet werden.\n";
    }

    Object[] ga = gewaesser.toArray();
    Arrays.sort(ga);
    String tmp = ""; for (int i=0; i<ga.length; i++) tmp += (i>0 ? ", " : "") + ga[i];
    ad.additionalTable[anzMtours+1][ 0] = "--- Zusammenfassung ---";
    ad.additionalTable[anzMtours+1][ 1] = tmp;
    ad.additionalTable[anzMtours+1][ 2] = EfaUtil.zehntelInt2String(_gesKm);
    ad.additionalTable[anzMtours+1][ 3] = Integer.toString(_gesTage);
    ad.additionalTable[anzMtours+1][ 4] = Integer.toString(_gesTeilnMueber18+_gesTeilnMbis18+_gesTeilnFueber18+_gesTeilnFbis18);
    ad.additionalTable[anzMtours+1][ 5] = EfaUtil.zehntelInt2String(_gesMannschKm);
    ad.additionalTable[anzMtours+1][ 6] = Integer.toString(_gesTeilnMueber18);
    ad.additionalTable[anzMtours+1][ 7] = EfaUtil.zehntelInt2String(_gesKmTeilnMueber18);
    ad.additionalTable[anzMtours+1][ 8] = Integer.toString(_gesTeilnMbis18);
    ad.additionalTable[anzMtours+1][ 9] = EfaUtil.zehntelInt2String(_gesKmTeilnMbis18);
    ad.additionalTable[anzMtours+1][10] = Integer.toString(_gesTeilnFueber18);
    ad.additionalTable[anzMtours+1][11] = EfaUtil.zehntelInt2String(_gesKmTeilnFueber18);
    ad.additionalTable[anzMtours+1][12] = Integer.toString(_gesTeilnFbis18);
    ad.additionalTable[anzMtours+1][13] = EfaUtil.zehntelInt2String(_gesKmTeilnFbis18);

    // Anzahl der aktiven Mitglieder
    if (sd.ausgabeArt == sd.AUSGABE_EFAWETT && alleAktive != null) {
      int aktMab19  = 0;
      int aktMbis18 = 0;
      int aktWab19  = 0;
      int aktWbis18 = 0;
      Object[] aktive = alleAktive.keySet().toArray();
      for (int i=0; i<aktive.length; i++) {
        String s = (String)alleAktive.get(aktive[i]);
        if (s != null) {
          if (s.equals(AKTIV_M_AB19))  aktMab19++;
          if (s.equals(AKTIV_M_BIS18)) aktMbis18++;
          if (s.equals(AKTIV_W_AB19))  aktWab19++;
          if (s.equals(AKTIV_W_BIS18)) aktWbis18++;
        }
      }
      efaWett.aktive_M_ab19  = Integer.toString(aktMab19);
      efaWett.aktive_M_bis18 = Integer.toString(aktMbis18);
      efaWett.aktive_W_ab19  = Integer.toString(aktWab19);
      efaWett.aktive_W_bis18 = Integer.toString(aktWbis18);
    }
  }

  static int getSumOfAllHashEntries(Hashtable h) {
    try {
      Object[] keys = h.keySet().toArray();
      int sum = 0;
      for (int i=0; i<keys.length; i++) {
        sum += ((Integer)h.get(keys[i])).intValue();
      }
      return sum;
    } catch(Exception e) {
    e.printStackTrace();
      return -1;
    }
  }


/*
  // Ausgabedaten für Kilometerwettbewerbe erstellen (LRV Brandenburg Wanderruderwettbewerb: "Großer Wettbewerb")
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettLRVBrbWanderruderWett(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.LRVBRB_WANDERRUDERWETT,sd.wettJahr);
    WettDefGruppe[] gruppen = wett.gruppen;
    int jahrgang;
    int geskm=0;
    int gesanz=0;

    if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

    if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.LRVBRB_WANDERRUDERWETT))
      ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

    ad.wett_gruppennamen = new String[gruppen.length][3];
    ad.wett_teilnehmerInGruppe = new AusgabeEintrag[gruppen.length];

    for (int g=0; g<gruppen.length; g++) {
        ad.wett_gruppennamen[g][0] = gruppen[g].bezeichnung+")";
        ad.wett_gruppennamen[g][1] = "Jahrgänge "+makeJahrgang(sd.wettJahr-gruppen[g].hoechstalter)+
                                     " - "+makeJahrgang(sd.wettJahr-gruppen[g].mindalter)+
                                     " ("+makeGeschlecht(gruppen[g].geschlecht)+")";
        ad.wett_gruppennamen[g][2] = gruppen[g].km+" Kilometer"+
                            "; davon "+gruppen[g].zusatz+" Gigboot-Kilometer und "+gruppen[g].zusatz2+" Wanderfahrt-Tage";

        // Alle Teilnehmer in einer gegebenen Gruppe durchlaufen
        for (int i=0; i<a.length; i++) {
          if (!a[i].jahrgang.equals("")) jahrgang = Integer.parseInt(a[i].jahrgang);
          else jahrgang = -1;

          if (!a[i].jahrgang.equals("") &&
              Daten.wettDefs.inGruppe(WettDefs.LRVBRB_WANDERRUDERWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung)) {
            // Teilnehmer ist in der Gruppe!

            // Wanderfahrten zusammenstellen
            int anzWafaTage = 0;
            int anzWafa = 0; // max. 3 ausgeben
            String[][] wafa = new String[gruppen[g].zusatz2][6]; // 3 Einträge mit jeweils LfdNr/Abfahrt/Ankunft/Ziel/Km/Bemerk
            Object[] keys = a[i].kmwett.wafa.keySet().toArray(); // Keys ermitteln
            int fahrtnr = 0;
            for (int nr=0; nr<wafa.length; nr++) { // max. für 3 auszufüllende Felder Fahrten suchen
              DRVFahrt drvel = null;
              if (fahrtnr < keys.length) do {
                drvel = (DRVFahrt)a[i].kmwett.wafa.get(keys[fahrtnr]);
                // wenn ein Ruderer an einer Mehrtagesfahrt (als einzelne Etappen eingetragen) nur einen Tag
                // mitgerudert ist, werden nur 30 Km gefordert (Dennis, 02.05.03)
                if (drvel != null && drvel.jum == false && drvel.km >= Daten.WAFAKM && drvel.days == 1) drvel.ok = true;
                fahrtnr++;
              } while ((drvel == null || !drvel.ok) && fahrtnr < keys.length);
              if (drvel != null && drvel.ok) {
                wafa[nr][0] = drvel.entryNo;
                wafa[nr][1] = drvel.dateStart;
                wafa[nr][2] = drvel.dateEnd;
                wafa[nr][3] = drvel.destination;
                wafa[nr][4] = EfaUtil.zehntelInt2String(drvel.km);
                wafa[nr][5] = drvel.comments;
                anzWafaTage += drvel.days;
                anzWafa++;
              }
            }
            for (int sx=0; sx<wafa.length; sx++)
              for (int sy=sx+1; sy<wafa.length; sy++)
                if (wafa[sx][0] != null && wafa[sy][0] != null &&
                    EfaUtil.compareIntString(wafa[sy][0],wafa[sx][0]) < 0) {
                  String[] tmp = wafa[sx];
                  wafa[sx] = wafa[sy];
                  wafa[sy] = tmp;
                }


            // sollen für den Teilnehmer Daten ausgegeben werden?
            boolean erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.LRVBRB_WANDERRUDERWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,a[i].kmwett.gigbootkm/10,anzWafaTage,0,0);

            if (erfuellt) {
              gesanz++;
              geskm += a[i].rudKm+a[i].stmKm;
            }

            if (erfuellt ||
                (((a[i].rudKm+a[i].stmKm)/10 >= gruppen[g].km*sd.wettProz/100) && sd.wettProz<100) ) {

              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                // Ausgabe für efaWett
              } else {
                // normale Ausgabe des Teilnehmers
                AusgabeEintrag ae = new AusgabeEintrag();
                ae.w_name      = a[i].name;
                ae.w_kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                if (!erfuellt && sd.zusatzWettMitAnforderung) ae.w_kilometer+="/"+gruppen[g].km;
                if (!sd.wettOhneDetail && erfuellt) {
                  ae.w_jahrgang = a[i].jahrgang;
                  ae.w_additional = "davon "+EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+" Gigboot-Km";
                  ae.w_detail   = new String[anzWafa][6];
                  for (int j=0; j<wafa.length && j<anzWafa; j++)
                    ae.w_detail[j] = wafa[j];
                } else {
                  if (sd.wettKurzAusgabe) {
                    if (sd.zusatzWettMitAnforderung) ae.w_additional = EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+"/"+gruppen[g].zusatz+" Gig-Km; "+
                                                                       anzWafaTage+"/"+gruppen[g].zusatz2+" WafaTage";
                    else ae.w_additional = EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+" Gig-Km; "+anzWafaTage+" WafaTage";
                  } else ae.w_additional = "davon "+EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+" Gigboot-Km und "+anzWafaTage+" Wanderfahrt-Tage";
                }
                ae.w_erfuellt = erfuellt;

                // Eintrag hinzufügen
                if (ad.wett_teilnehmerInGruppe[g] == null) ad.wett_teilnehmerInGruppe[g] = ae;
                else letzterAusgabeEintrag.next = ae;
                letzterAusgabeEintrag = ae;
              }
            }
          } else {
            // Teilnehmer ohne Jahrgang
            if (a[i].jahrgang.equals("") &&
                Daten.wettDefs.erfuellt(WettDefs.LRVBRB_WANDERRUDERWETT,sd.wettJahr,0,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,9999,9999,9999,0) != null &&
                nichtBeruecksichtigt.get(a[i].name) == null) {
                  nichtBeruecksichtigt.put(a[i].name,"Wegen fehlenden Jahrgangs ignoriert (" + EfaUtil.zehntelInt2String(a[i].stmKm+a[i].rudKm) + " Km)");
                  continue;
            }
          }
        }
    }
      if (sd.ausgabeArt != sd.AUSGABE_EFAWETT) {
        ad.additionalTable = new String[2][2];
        ad.additionalTable[0][0] = "Anzahl der Erfüller:";
        ad.additionalTable[0][1] = Integer.toString(gesanz);
        ad.additionalTable[1][0] = "Kilometer aller Erfüller:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
      }
  }
*/
  // Ausgabedaten für Kilometerwettbewerbe erstellen (LRV Brandenburg Fahrtenwettbewerb: "Kleiner Wettbewerb")
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettLRVBrbFahrtenWett(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.LRVBRB_FAHRTENWETT,sd.wettJahr);
    WettDefGruppe[] gruppen = wett.gruppen;
    int jahrgang;
    int geskm=0;
    int gesanz=0;

    if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

    if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.LRVBRB_FAHRTENWETT))
      ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

    ad.wett_gruppennamen = new String[gruppen.length][3];
    ad.wett_teilnehmerInGruppe = new AusgabeEintrag[gruppen.length];

    for (int g=0; g<gruppen.length; g++) {
        ad.wett_gruppennamen[g][0] = gruppen[g].bezeichnung+")";
        ad.wett_gruppennamen[g][1] = "Jahrgänge "+makeJahrgang(sd.wettJahr-gruppen[g].hoechstalter)+
                                     " - "+makeJahrgang(sd.wettJahr-gruppen[g].mindalter)+
                                     " ("+makeGeschlecht(gruppen[g].geschlecht)+")";
        ad.wett_gruppennamen[g][2] = gruppen[g].km+" Kilometer" +
            (gruppen[g].zusatz>0 && gruppen[g].zusatz2>0 ?
             "; davon "+gruppen[g].zusatz2+" Fahrten im Gigboot mit je mind. "+gruppen[g].zusatz+" Km" : "");

        // Alle Teilnehmer in einer gegebenen Gruppe durchlaufen
        for (int i=0; i<a.length; i++) {
          if (!a[i].jahrgang.equals("")) jahrgang = Integer.parseInt(a[i].jahrgang);
          else jahrgang = -1;

          if (!a[i].jahrgang.equals("") &&
              Daten.wettDefs.inGruppe(WettDefs.LRVBRB_FAHRTENWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung)) {
            // Teilnehmer ist in der Gruppe!

            // Wanderfahrten zusammenstellen
            int anzWafaTage = 0;
            Object[] keys = a[i].kmwett.wafa.keySet().toArray(); // Keys ermitteln
            for (int nr=0; nr<keys.length; nr++) {
              DRVFahrt drvel = (DRVFahrt)a[i].kmwett.wafa.get(keys[nr]);
              // wenn ein Ruderer an einer Mehrtagesfahrt (als einzelne Etappen eingetragen) nur einen Tag
              // mitgerudert ist, werden nur 30 Km gefordert (Dennis, 02.05.03)
// @gehtnichtmehrinefa2               if (drvel != null && drvel.jum == false && drvel.km >= Daten.WAFAKM && drvel.days == 1) drvel.ok = true;
              if (drvel != null && drvel.ok) anzWafaTage += drvel.days;
            }

            // Anzahl der geforderten Gig-Fahrten ermitteln
            int gigfahrten = 0;
            for (int gigid=0; gigid < a[i].kmwett.gigfahrten.size(); gigid++) {
              String[] fahrt = (String[])a[i].kmwett.gigfahrten.get(gigid);
              if (EfaUtil.zehntelString2Int(fahrt[4]) / 10 >= gruppen[g].zusatz) gigfahrten++;
            }

            // sollen für den Teilnehmer Daten ausgegeben werden?
            boolean erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.LRVBRB_FAHRTENWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,gruppen[g].zusatz,gigfahrten,0,0);
            if (Daten.wettDefs.erfuellt(WettDefs.LRVBRB_WANDERRUDERWETT,sd.wettJahr,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,a[i].kmwett.gigbootkm/10,anzWafaTage,0,0) != null) {
              erfuellt = false;
            }

            if (erfuellt) {
              gesanz++;
              geskm += a[i].rudKm+a[i].stmKm;
            }

            if (erfuellt ||
                (((a[i].rudKm+a[i].stmKm)/10 >= gruppen[g].km*sd.wettProz/100) && sd.wettProz<100) ) {

              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                // Ausgabe für efaWett
              } else {
                // normale Ausgabe des Teilnehmers
                AusgabeEintrag ae = new AusgabeEintrag();
                ae.w_name      = a[i].name;
                ae.w_kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                if (!erfuellt && sd.zusatzWettMitAnforderung) ae.w_kilometer+="/"+gruppen[g].km;
                if (!sd.wettOhneDetail && erfuellt) {
                  ae.w_jahrgang = a[i].jahrgang;

                  // Gig-Fahrten ausgeben (exakt gruppen[g].zusatz2 Fahrten ausgeben)
                  // Dazu die n Fahrten mit den meisten Kilometern raussuchen
                  int[] fahrtIDs = new int[gruppen[g].zusatz2];
                  for (int fid=0; fahrtIDs != null && fid < fahrtIDs.length; fid++) {
                    int maxkm = 0;
                    int maxkmId = -1;
                    for (int gigid=0; gigid < a[i].kmwett.gigfahrten.size(); gigid++) {
                      String[] fahrt = (String[])a[i].kmwett.gigfahrten.get(gigid);
                      int km = EfaUtil.zehntelString2Int(fahrt[4]);
                      if (km/10 < gruppen[g].zusatz) continue; // nur Fahrten mit genügend Km zulassen
                      if (km > maxkm) {
                        boolean doppelt = false;
                        for (int ijk=0; ijk<fid; ijk++) {
                          if (gigid == fahrtIDs[ijk]) doppelt = true;
                        }
                        if (!doppelt) {
                          maxkm = km;
                          maxkmId = gigid;
                        }
                      }
                    }
                    if (maxkmId < 0 || maxkm == 0) { // kann eigentlich nie vorkommen
                      fahrtIDs = null; // Abbruch; in diesem Fall gar keine Fahrten ausgeben
                    } else {
                      fahrtIDs[fid] = maxkmId;
                    }
                  }
                  if (fahrtIDs != null) {
                    Arrays.sort(fahrtIDs);
                    ae.w_detail   = new String[fahrtIDs.length][6];
                    for (int j=0; j<fahrtIDs.length; j++) {
                      ae.w_detail[j] = (String[])a[i].kmwett.gigfahrten.get(fahrtIDs[j]);
                    }
                  }
                } else {
                  if (sd.wettKurzAusgabe) {
                    if (sd.zusatzWettMitAnforderung) ae.w_additional = gigfahrten+"/"+gruppen[g].zusatz2+" Gig-Fahrten";
                    else ae.w_additional = gigfahrten+" Gig-Fahrten";
                  } else ae.w_additional = gigfahrten+" Gig-Fahrten";

                }
                ae.w_erfuellt = erfuellt;

                // Eintrag hinzufügen
                if (ad.wett_teilnehmerInGruppe[g] == null) ad.wett_teilnehmerInGruppe[g] = ae;
                else letzterAusgabeEintrag.next = ae;
                letzterAusgabeEintrag = ae;
              }
            }
          } else {
            // Teilnehmer ohne Jahrgang
            if (a[i].jahrgang.equals("") &&
                Daten.wettDefs.erfuellt(WettDefs.LRVBRB_FAHRTENWETT,sd.wettJahr,0,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,9999,9999,9999,0) != null &&
                nichtBeruecksichtigt.get(a[i].name) == null) {
                  nichtBeruecksichtigt.put(a[i].name,"Wegen fehlenden Jahrgangs ignoriert (" + EfaUtil.zehntelInt2String(a[i].stmKm+a[i].rudKm) + " Km)");
                  continue;
            }
          }
        }
    }
      if (sd.ausgabeArt != sd.AUSGABE_EFAWETT) {
        ad.additionalTable = new String[2][2];
        ad.additionalTable[0][0] = "Anzahl der Erfüller:";
        ad.additionalTable[0][1] = Integer.toString(gesanz);
        ad.additionalTable[1][0] = "Kilometer aller Erfüller:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
      }
  }

  // Ausgabedaten für Kilometerwettbewerbe erstellen (LRV Mecklenburg-Vorpommern Wanderruderwettbewerb)
  // @i18n Methode wird nicht internationalisiert
  static void ausgabeKmWettLRVMVpWanderruderWett(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    WettDef wett = Daten.wettDefs.getWettDef(WettDefs.LRVMVP_WANDERRUDERWETT,sd.wettJahr);
    WettDefGruppe[] gruppen = wett.gruppen;
    int jahrgang;
    int geskm=0;
    int gesanz=0;

    if (sd.wettJahr < LRVMVP_NEU) {
      ad.wett_zeitraumWarnung = "Es haben nur Personen diesen Wettbewerb erfüllt, die NICHT das Fahrtenabzeichen des DRV erfüllen. Diese Zusatzbedingung wird von efa NICHT überprüft. Die von efa erstellte Liste enthällt alle potentiellen Erfüller.";
    }

    if (sd.ausgebenWettBedingung) ad.wett_bedingungen = createAusgabeBedingungen(sd,wett.key,ad.wett_bedingungen_fett,ad.wett_bedingungen_kursiv);

    if (!checkWettZeitraum(sd.wettJahr,sd.von,sd.bis,WettDefs.LRVMVP_WANDERRUDERWETT))
      ad.wett_zeitraumWarnung = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";

    ad.wett_gruppennamen = new String[gruppen.length][3];
    ad.wett_teilnehmerInGruppe = new AusgabeEintrag[gruppen.length];

    for (int g=0; g<gruppen.length; g++) {
        ad.wett_gruppennamen[g][0] = gruppen[g].bezeichnung+")";
        ad.wett_gruppennamen[g][1] = "Jahrg�nge "+makeJahrgang(sd.wettJahr-gruppen[g].hoechstalter)+
                                     " - "+makeJahrgang(sd.wettJahr-gruppen[g].mindalter)+
                                     " ("+makeGeschlecht(gruppen[g].geschlecht)+")";
        ad.wett_gruppennamen[g][2] = gruppen[g].km+" Kilometer"+
                            (gruppen[g].zusatz > 0 ? "; davon "+gruppen[g].zusatz+" Gigboot-Kilometer in mind. "+gruppen[g].zusatz2+" Fahrten" : // bis 2008
                                                     " in mind. "+gruppen[g].zusatz2+" Fahrten" ) +                                              // ab 2009
                            (gruppen[g].zusatz3 > 0 ? " mit "+gruppen[g].zusatz3+" Fahrten von mind. 20 Km" : "") +
                            (gruppen[g].zusatz3 > 0 && gruppen[g].zusatz4 > 0 ? " und" : (gruppen[g].zusatz4 > 0 ? " mit" : "") ) +
                            (gruppen[g].zusatz4 > 0 ? " "+gruppen[g].zusatz4+" Fahrten von mind. 30 Km" : "");

        // Alle Teilnehmer in einer gegebenen Gruppe durchlaufen
        for (int i=0; i<a.length; i++) {
          if (!a[i].jahrgang.equals("")) jahrgang = Integer.parseInt(a[i].jahrgang);
          else jahrgang = -1;

          if (!a[i].jahrgang.equals("") &&
              Daten.wettDefs.inGruppe(WettDefs.LRVMVP_WANDERRUDERWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung)) {
            // Teilnehmer ist in der Gruppe!


            // Überprüfung, ob der Teilnehmer den DRV-Wettbewerb erfüllt hat
            int wafaKm = 0;
            int wafaAnzMTour = 0;
            int jumAnz = 0;
            if (sd.wettJahr >= LRVMVP_NEU) { // erst ab 2009 haben LRV und DRV den gleichen Zeitraum, sonst ist dieser Abgleich nicht möglich!
              Object[] keys = a[i].kmwett.wafa.keySet().toArray(); // Keys ermitteln
              for (int nr = 0; nr < keys.length; nr++) {
                DRVFahrt drvel = (DRVFahrt) a[i].kmwett.wafa.get(keys[nr]);
                // wenn ein Ruderer an einer Mehrtagesfahrt (als einzelne Etappen eingetragen) nur einen Tag
                // mitgerudert ist, werden nur 30 Km gefordert (Dennis, 02.05.03)
                if (drvel != null && drvel.jum == false &&
// @gehtnichtmehrinefa2                     drvel.km >= Daten.WAFAKM &&
                    drvel.days == 1)
                  drvel.ok = true;
                if (drvel != null && drvel.ok) {
// @gehtnichtmehrinefa2                   wafaKm += drvel.km;
                  if (drvel.jum)
                    jumAnz++;
                  else
                    wafaAnzMTour++;
                }
              }
            }

            // sollen für den Teilnehmer Daten ausgegeben werden?
            boolean erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.LRVMVP_WANDERRUDERWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,a[i].kmwett.gigbootkm/10,a[i].kmwett.gigbootanz,a[i].kmwett.gigboot20plus,a[i].kmwett.gigboot30plus);
            if (!erfuellt && a[i].kmwett.gigboot30plus>1) {
              erfuellt = Daten.wettDefs.erfuelltGruppe(WettDefs.LRVMVP_WANDERRUDERWETT,sd.wettJahr,g,jahrgang,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,a[i].kmwett.gigbootkm/10,a[i].kmwett.gigbootanz,a[i].kmwett.gigboot20plus+a[i].kmwett.gigboot30plus-1,1);
            }

            if (sd.wettJahr >= LRVMVP_NEU) {
              if (Daten.wettDefs.erfuellt(WettDefs.DRV_FAHRTENABZEICHEN,
                                          sd.wettJahr, jahrgang,
                                          a[i].kmwett.geschlecht,
                                          a[i].kmwett.behinderung,
                                          a[i].rudKm + a[i].stmKm, wafaKm / 10,
                                          wafaAnzMTour, jumAnz, 0) != null) {
                erfuellt = false;
              }
            }

            if (erfuellt) {
              gesanz++;
              geskm += a[i].rudKm+a[i].stmKm;
            }

            if (erfuellt ||
                (((a[i].rudKm+a[i].stmKm)/10 >= gruppen[g].km*sd.wettProz/100) && sd.wettProz<100) ) {

              if (sd.ausgabeArt == sd.AUSGABE_EFAWETT) {
                // Ausgabe für efaWett
              } else {
                // normale Ausgabe des Teilnehmers
                AusgabeEintrag ae = new AusgabeEintrag();
                ae.w_name      = a[i].name;
                ae.w_kilometer = EfaUtil.zehntelInt2String(a[i].rudKm+a[i].stmKm);
                if (!erfuellt && sd.zusatzWettMitAnforderung) ae.w_kilometer+="/"+gruppen[g].km;
                if (!sd.wettOhneDetail && erfuellt) {
                  ae.w_jahrgang = a[i].jahrgang;
                  ae.w_additional = "davon "+
                                    (sd.wettJahr < LRVMVP_NEU ? EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+" Gigboot-Km in "+a[i].kmwett.gigbootanz+" Fahrten mit " : "") +
                                    a[i].kmwett.gigboot20plus+" Fahrten >= 20 Km und "+a[i].kmwett.gigboot30plus+" Fahrten >= 30 Km";

                  // Gig-Fahrten ausgeben (exakt gruppen[g].zusatz2 Fahrten ausgeben)
                  // Dazu die n Fahrten mit den meisten Kilometern raussuchen
                  int[] fahrtIDs = new int[gruppen[g].zusatz2];
                  for (int fid=0; fahrtIDs != null && fid < fahrtIDs.length; fid++) {
                    int maxkm = 0;
                    int maxkmId = -1;
                    for (int gigid=0; gigid < a[i].kmwett.gigfahrten.size(); gigid++) {
                      String[] fahrt = (String[])a[i].kmwett.gigfahrten.get(gigid);
                      int km = EfaUtil.zehntelString2Int(fahrt[4]);
                      if (km > maxkm) {
                        boolean doppelt = false;
                        for (int ijk=0; ijk<fid; ijk++) {
                          if (gigid == fahrtIDs[ijk]) doppelt = true;
                        }
                        if (!doppelt) {
                          maxkm = km;
                          maxkmId = gigid;
                        }
                      }
                    }
                    if (maxkmId < 0 || maxkm == 0) { // kann eigentlich nie vorkommen
                      fahrtIDs = null; // Abbruch; in diesem Fall gar keine Fahrten ausgeben
                    } else {
                      fahrtIDs[fid] = maxkmId;
                    }
                  }
                  if (fahrtIDs != null) {
                    Arrays.sort(fahrtIDs);
                    ae.w_detail   = new String[fahrtIDs.length][6];
                    for (int j=0; j<fahrtIDs.length; j++) {
                      ae.w_detail[j] = (String[])a[i].kmwett.gigfahrten.get(fahrtIDs[j]);
                    }
                  }

                } else {
                  if (sd.wettKurzAusgabe) {
                    if (sd.zusatzWettMitAnforderung) ae.w_additional = (sd.wettJahr < LRVMVP_NEU ?
                                                                       EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+"/"+gruppen[g].zusatz+" Gig-Km; " : "") +
                                                                       a[i].kmwett.gigbootanz+"/"+gruppen[g].zusatz2+" Fahrten; "+
                                                                       a[i].kmwett.gigboot20plus+"/"+gruppen[g].zusatz3+" >= 20 Km; "+
                                                                       a[i].kmwett.gigboot30plus+"/"+gruppen[g].zusatz4+" >= 30 Km";
                    else ae.w_additional = (sd.wettJahr < LRVMVP_NEU ? EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+" Gig-Km; " : "") +a[i].kmwett.gigbootanz+" Fahrten; " +
                                                                       a[i].kmwett.gigboot20plus+" >= 20 Km; "+
                                                                       a[i].kmwett.gigboot30plus+" >= 30 Km";
                  } else ae.w_additional = "davon "+ (sd.wettJahr < LRVMVP_NEU ? EfaUtil.zehntelInt2String(a[i].kmwett.gigbootkm)+" Gigboot-Km in " : "") +a[i].kmwett.gigbootanz+
                                    " Fahrten mit "+a[i].kmwett.gigboot20plus+" Fahrten >= 20 Km und "+a[i].kmwett.gigboot30plus+" Fahrten >= 30 Km";
                }
                ae.w_erfuellt = erfuellt;

                // Eintrag hinzufügen
                if (ad.wett_teilnehmerInGruppe[g] == null) ad.wett_teilnehmerInGruppe[g] = ae;
                else letzterAusgabeEintrag.next = ae;
                letzterAusgabeEintrag = ae;
              }
            }
          } else {
            // Teilnehmer ohne Jahrgang
            if (a[i].jahrgang.equals("") &&
                Daten.wettDefs.erfuellt(WettDefs.LRVMVP_WANDERRUDERWETT,sd.wettJahr,0,a[i].kmwett.geschlecht,a[i].kmwett.behinderung,a[i].rudKm+a[i].stmKm,9999,9999,9999,0) != null &&
                nichtBeruecksichtigt.get(a[i].name) == null) {
                  nichtBeruecksichtigt.put(a[i].name,"Wegen fehlenden Jahrgangs ignoriert (" + EfaUtil.zehntelInt2String(a[i].stmKm+a[i].rudKm) + " Km)");
                  continue;
            }
          }
        }
    }
      if (sd.ausgabeArt != sd.AUSGABE_EFAWETT) {
        ad.additionalTable = new String[2][2];
        ad.additionalTable[0][0] = "Anzahl der Erfüller:";
        ad.additionalTable[0][1] = Integer.toString(gesanz);
        ad.additionalTable[1][0] = "Kilometer aller Erfüller:";
        ad.additionalTable[1][1] = EfaUtil.zehntelInt2String(geskm);
      }
  }






  // Ausgabedaten für Monatsübersicht erstellen
  static void ausgabeMonatsuebersicht(AusgabeDaten ad, StatistikDaten sd, ArrEl[] a) {
    TMJ lastDatum = null;
    int[] monat = null;
    String[] fahrtart = null;
    String[] tit = null;
    String[] txt = null;
    String[] color = null;
    TabellenFolgenEintrag lastCol = null;
    TabellenFolgenEintrag col = null;

    String[] wotage = new String[7];
    wotage[0] = International.getString("Montag");
    wotage[1] = International.getString("Dienstag");
    wotage[2] = International.getString("Mittwoch");
    wotage[3] = International.getString("Donnerstag");
    wotage[4] = International.getString("Freitag");
    wotage[5] = International.getString("Samstag");
    wotage[6] = International.getString("Sonntag");

    boolean[] allBold7 = new boolean[7];
    Arrays.fill(allBold7,true);

    boolean[] noneBold7 = new boolean[7];
    Arrays.fill(noneBold7,false);

    boolean[] allBold1 = new boolean[1];
    Arrays.fill(allBold1,true);

    String[] allAA7 = new String[7];
    Arrays.fill(allAA7,"aaaaaa");

    String[] allAA1 = new String[1];
    allAA1[0] = "aaaaaa";

    col = new TabellenFolgenEintrag(0,0,null,null,null);
    ad.tfe = col; lastCol = col;
    tit = new String[1]; tit[0] = International.getString("Legende");
    col = new TabellenFolgenEintrag(4,4,tit,allAA1,allBold1);
    lastCol.next = col; lastCol = col;
    txt = new String[4];
    color = new String[4];
    txt[0] = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_NORMAL); color[0] = "88ff88"; // grün
    txt[1] = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_INSTRUCTION); color[1] = "88ffff"; // türkis
    txt[2] = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_JUMREGATTA); color[2] = "ff8888"; // rot
    txt[3] = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_REGATTA); color[3] = "ff8888"; // rot
    col = new TabellenFolgenEintrag(4,1,txt,color,noneBold7);
    lastCol.next = col; lastCol = col;
    txt = new String[4];
    color = new String[4];
    txt[0] = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_TRAINING); color[0] = "ff88ff"; // lila
    txt[1] = International.getString("mehrere Fahrtarten"); color[1] = "ffff88"; // gelb
    txt[2] = International.getString("Mehrtagesfahrt") + " & " +
            International.getString("andere"); color[2] = "8888ff"; // blau
    txt[3] = International.getString("keine Fahrt"); color[3] = "eeeeee"; // grau
    col = new TabellenFolgenEintrag(4,1,txt,color,noneBold7);
    lastCol.next = col; lastCol = col;

    for (int i=0; a != null && i<=a.length; i++) {
      int tmp;
      TMJ aktDatum = null;

      if (i<a.length) {
        aktDatum = EfaUtil.string2date(a[i].name,0,0,0);
        tmp = aktDatum.tag; aktDatum.tag = aktDatum.jahr; aktDatum.jahr = tmp; // Dreieckstausch, da Datumsformat Jahr/Monat/Tag
      }
      if (monat == null || lastDatum == null || i == a.length ||
          aktDatum.monat != lastDatum.monat || aktDatum.jahr != lastDatum.jahr) {
        // neuer Monat

        // wurde schon ein Monat berechnet?
        if (monat != null) {
          // Monat ausgeben!

          // eine Zeile Abstand
          col = new TabellenFolgenEintrag(0,0,null,null,null);
          lastCol.next = col;
          lastCol = col;

          // Monatsüberschrift
          tit = new String[1];
          switch (lastDatum.monat) {
            case  1: tit[0] = International.getString("Januar");    break;
            case  2: tit[0] = International.getString("Februar");   break;
            case  3: tit[0] = International.getString("März");      break;
            case  4: tit[0] = International.getString("April");     break;
            case  5: tit[0] = International.getString("Mai");       break;
            case  6: tit[0] = International.getString("Juni");      break;
            case  7: tit[0] = International.getString("Juli");      break;
            case  8: tit[0] = International.getString("August");    break;
            case  9: tit[0] = International.getString("September"); break;
            case 10: tit[0] = International.getString("Oktober");   break;
            case 11: tit[0] = International.getString("November");  break;
            case 12: tit[0] = International.getString("Dezember");  break;
            default: tit[0] = ANDEREBEZ;   break;
          }
          tit[0] += " " + lastDatum.jahr;
          col = new TabellenFolgenEintrag(7,7,tit,allAA1,allBold1);
          lastCol.next = col;
          lastCol = col;

          // Wochentagsüberschrift
          col = new TabellenFolgenEintrag(7,1,wotage,allAA7,allBold7);
          lastCol.next = col;
          lastCol = col;

          // Wochentag des ersten Tages des Monats ermitteln
          Calendar cal = new GregorianCalendar(lastDatum.jahr,lastDatum.monat-1,1);
          int start = cal.get(Calendar.DAY_OF_WEEK) - 1;
          if (start == 0) start = 7; // Sonntag
          int maxDays = 31;
          if (lastDatum.monat == 4 || lastDatum.monat == 6 || lastDatum.monat == 9 || lastDatum.monat == 11) maxDays = 30;
          if (lastDatum.monat == 2) {
            if (lastDatum.jahr % 4 == 0) maxDays = 29;
            else maxDays = 28;
          }


          int j = 0;
          while (j-start+1<maxDays) {
            txt = new String[7];
            color = new String[7];
            for (int z=0; z<7; z++) {
              j++;
              if (j-start+1>=1 && j-start+1<=maxDays) {
                if (monat[j-start+1]>0) txt[(j-1) % 7] = (j-start+1)+".\n"+EfaUtil.zehntelInt2String(monat[j-start+1])+" Km";
                else txt[(j-1) % 7] = (j-start+1)+".";
                if (monat[j-start+1]>0) {
                  String c = "0000ff"; // blau
                  if (fahrtart[j-start+1] == null || fahrtart[j-start+1].equals("")) c = "00ff00"; // grün
                  else if (fahrtart[j-start+1].equals("MEHRERE_FAHRTARTEN")) c = "ffff00"; // gelb
                  else if (fahrtart[j-start+1].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_INSTRUCTION))) c = "00ffff"; // türkis
                  else if (fahrtart[j-start+1].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_JUMREGATTA))) c = "ff0000"; // rot
                  else if (fahrtart[j-start+1].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_REGATTA))) c = "ff0000"; // rot
                  else if (fahrtart[j-start+1].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_TRAINING))) c = "ff00ff"; // lila
//                  int intens = 9 - (monat[j-start+1] / 100);
//                  if (intens<0) intens = 0;
                  int intens = 8; // alle Fahrten mit gleicher Intensität färben!
                  color[(j-1) % 7] = EfaUtil.replace(c,"0",Integer.toString(intens),true);
                }
                else color[(j-1) % 7] = "eeeeee";
              } else {
                txt[(j-1) % 7] = "";
                color[(j-1) % 7] = "ffffff";
              }
            }
            col = new TabellenFolgenEintrag(7,1,txt,color,noneBold7);
            lastCol.next = col;
            lastCol = col;
          }

        }

        monat = new int[32];
        fahrtart = new String[32];
        Arrays.fill(monat,0);
      }

      if (i<a.length) {
        if (aktDatum.tag>=1 && aktDatum.tag<=31) {
          monat[aktDatum.tag] += a[i].rudKm+a[i].stmKm;
          if (fahrtart[aktDatum.tag] == null) fahrtart[aktDatum.tag] = a[i].status;
          else if (fahrtart[aktDatum.tag].equals(a[i].status)); // nothing
          else fahrtart[aktDatum.tag] = "MEHRERE_FAHRTARTEN";
        } else monat[0] += a[i].rudKm+a[i].stmKm;
      }

      lastDatum = aktDatum;

    }
  }




  // Anzahl der Zielfahrten in einem String[][] ermitteln, bzw. wenn null aus einem String
  static int countZf(Zielfahrt[] z, ZielfahrtFolge zf) {
    int zfAnz = 0;
    if (zf != null) zfAnz = zf.getAnzZielfahrten();
    if (z == null) {
      if (zf == null) return 0;
      else return (zfAnz<4 ? zfAnz : 3); // wenn z==null, dann maximal 3 zurückgeben (weil: zf könnte mehrere Fahrten an einem Tag enthalten)
    }
    for (int c=0; c<z.length; c++) {
      if (z[c] == null) return c;
    }
    // wenn z != null (also eigentlich erfüllt), dann den größeren der beiden Werte zurückgeben
    if (z.length < zfAnz) return zfAnz;
    else return z.length;
  }


  // erste und letzte Fahrt ermitteln
  static void getErsteLetzteFahrt(StatistikDaten sd, DatenFelder d, TMJ dateF) {
    // hier wird absichtlich der Buchstabe der LfdNr ignoriert, da dieser nur efa-intern ist
    TMJ tmp = EfaUtil.string2date(d.get(Fahrtenbuch.LFDNR),1,1,1);
    if (sd.fruehesteFahrt == null) {
      sd.ersterEintrag = tmp.tag;
      sd.erstesFb = Daten.fahrtenbuch.getFileName();
    }
    if (sd.fruehesteFahrt == null || EfaUtil.getRealDateDiff(sd.fruehesteFahrt,dateF)<0) {
      sd.fruehesteFahrt = dateF;
    }
    if (sd.spaetesteFahrt == null || EfaUtil.getRealDateDiff(sd.spaetesteFahrt,dateF)>0) {
      sd.spaetesteFahrt = dateF;
    }
    sd.letzterEintrag = tmp.tag;
    sd.letztesFb = Daten.fahrtenbuch.getFileName();
  }




  // prüft, ob für die angegebene Person "pers" mit Namen "name" Einträge berechnet werden sollen
  static boolean ignorePerson(StatistikDaten sd, DatenFelder pers, String name, boolean mitruderer) {
    if (sd.art != StatistikDaten.BART_RUDERER) {
      for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++) {
        // @todo (P5) statistics if (!sd.geschlecht[i] && (pers == null ||
                // @todo (P5) statistics pers.get(Mitglieder.GESCHLECHT).equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_GENDER, i)))) return true;
      }

      // folgender Code dient als Ersatz für die unten ausgeklammerten drei Zeilen!
      // Es soll sichergestellt werden, daß auch bei Fahrtenbüchern mit verschiedenen Statuslisten nur diejenigen Personen
      // ausgewertet werden, die einen der ausgewählten (der angezeigten) Status haben.
      if (pers == null) {
        if (!sd.status[sd.status.length-1]) return true; // nicht "andere" gewählt
      } else {
        boolean _found = false;
        for (int i=0; !_found && i<sd.statusNames.length; i++) {
          if (pers.get(Mitglieder.STATUS).equals(sd.statusNames[i])) {
            if (!sd.status[i]) return true;
            _found = true;
          }
        }
        if (!_found && !sd.status[sd.status.length-1]) return true; // nicht "andere" gewählt
      }
/*
      for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++)
        if (i<sd.status.length && !sd.status[i] && pers != null && pers.get(Mitglieder.STATUS).equals(Daten.fahrtenbuch.getDaten().status[i])) return true;
      if (!sd.status[Daten.fahrtenbuch.getDaten().status.length-1] && pers == null) return true;
*/
      // prüfen, ob spezieller Name angegeben wurde
      if (!mitruderer && !sd.name.equals("")) {
        if (sd.nameOderGruppe == StatistikDaten.NG_NAME) {
          // nur Name
          if (sd.nameTeil &&
             name.toUpperCase().indexOf(sd.name.toUpperCase()) < 0 && EfaUtil.syn2org(Daten.synMitglieder,name).toUpperCase().indexOf(sd.name.toUpperCase()) < 0) return true;
          if (!sd.nameTeil &&
             !name.toUpperCase().equals(sd.name.toUpperCase()) && !EfaUtil.syn2org(Daten.synMitglieder,name).toUpperCase().equals(sd.name.toUpperCase())) return true;
        } else {
          // nur Gruppe
          if (Daten.gruppen == null) return true;
          String n = EfaUtil.syn2org(Daten.synMitglieder,name);
          return !Daten.gruppen.isInGroup(sd.name,EfaUtil.getVorname(n),EfaUtil.getNachname(n),EfaUtil.getVerein(n));
        }
      }
    }
    return false;
  }
  static boolean ignorePerson(StatistikDaten sd, String name, boolean mitruderer) {
    DatenFelder pers = Daten.fahrtenbuch.getDaten().mitglieder.getExactComplete(name);
    return ignorePerson(sd,pers,name,mitruderer);
  }

  // prüft, ob für das angegebene Boot "boot" Einträge berechnet werden sollen
  static boolean ignoreBoot(StatistikDaten sd, DatenFelder boot) {
    if (boot == null) {
      if (!sd.bArt[sd.bArt.length-1]) return true;
      if (!sd.bAnzahl[sd.bAnzahl.length-1]) return true;
      if (!sd.bStm[sd.bStm.length-1]) return true;
      if (!sd.bRigger[sd.bRigger.length-1]) return true;
      if (!sd.bVerein[sd.bVerein.length-1]) return true;
      return false;
    }
    // @todo (P5) statistics for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++)
      // @todo (P5) statistics if (!sd.bArt[i] && (boot.get(Boote.ART).equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_BOAT,i)))) return true;
    // @todo (P5) statistics for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++)
      // @todo (P5) statistics if (!sd.bAnzahl[i] && (boot.get(Boote.ANZAHL).equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_NUMSEATS,i)))) return true;
    // @todo (P5) statistics for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_COXING); i++)
      // @todo (P5) statistics if (!sd.bStm[i] && (boot.get(Boote.STM).equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_COXING,i)))) return true;
    // @todo (P5) statistics for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING); i++)
      // @todo (P5) statistics if (!sd.bRigger[i] && (boot.get(Boote.RIGGER).equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_RIGGING,i)))) return true;
    if (!sd.bVerein[0] && (boot.get(Boote.VEREIN).equals(""))) return true;
    if (!sd.bVerein[1] && (!boot.get(Boote.VEREIN).equals(""))) return true;
    return false;
  }


  static void fillNullWerte(StatistikDaten sd, Hashtable h) {
    DatenFelder d;
    String key=null;
    switch (sd.art) {
      case StatistikDaten.ART_ZIELE: case StatistikDaten.BART_ZIELE:
        d = (DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getCompleteFirst();
        if (d != null) do {
          String zielArr[] = makeZielArr(sd,d.get(Ziele.NAME));
          for (int i=0; i<zielArr.length; i++)
            if (!h.containsKey(key=EfaUtil.syn2org(Daten.synZiele,zielArr[i]))) h.put(key,new HashEl("","","",0,0,0,0,0,new ZielfahrtFolge(d.get(Ziele.BEREICH)),null,null,null));
        } while ( (d = (DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getCompleteNext()) != null);
        break;
      case StatistikDaten.ART_KMFAHRT: case StatistikDaten.BART_KMFAHRT:
        // nichts zu tun
        break;
      case StatistikDaten.ART_WOTAGE: case StatistikDaten.BART_WOTAGE:
        if (!h.containsKey(International.getString("Montag")))
            h.put(International.getString("Montag"),new HashEl("01","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Dienstag")))
            h.put(International.getString("Dienstag"),new HashEl("02","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Mittwoch")))
            h.put(International.getString("Mittwoch"),new HashEl("03","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Donnerstag")))
            h.put(International.getString("Donnerstag"),new HashEl("04","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Freitag")))
            h.put(International.getString("Freitag"),new HashEl("05","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Samstag")))
            h.put(International.getString("Samstag"),new HashEl("06","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Sonntag")))
            h.put(International.getString("Sonntag"),new HashEl("07","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.ART_MONATE: case StatistikDaten.BART_MONATE:
        if (!h.containsKey(International.getString("Januar")))
            h.put(International.getString("Januar"),new HashEl("01","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Februar")))
            h.put(International.getString("Februar"),new HashEl("02","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("März")))
            h.put(International.getString("März"),new HashEl("03","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("April")))
            h.put(International.getString("April"),new HashEl("04","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Mai")))
            h.put(International.getString("Mai"),new HashEl("05","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Juni")))
            h.put(International.getString("Juni"),new HashEl("06","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Juli")))
            h.put(International.getString("Juli"),new HashEl("07","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("August")))
            h.put(International.getString("August"),new HashEl("08","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("September")))
            h.put(International.getString("September"),new HashEl("09","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Oktober")))
            h.put(International.getString("Oktober"),new HashEl("10","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("November")))
            h.put(International.getString("November"),new HashEl("11","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        if (!h.containsKey(International.getString("Dezember")))
            h.put(International.getString("Dezember"),new HashEl("12","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.ART_TAGESZEIT: case StatistikDaten.BART_TAGESZEIT:
        for (int hour=0; hour<24; hour++) {
          String name = International.getMessage("{hour} Uhr",hour);
          String kurz = (hour < 10 ? "0" : "") + hour;
          if (!h.containsKey(name)) h.put(name,new HashEl(kurz,"","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        }
        break;
      case StatistikDaten.ART_JAHRE: case StatistikDaten.BART_JAHRE:
        // nichts zu tun
        break;
      case StatistikDaten.ART_BOOTE: case StatistikDaten.BART_BOOTE:
      case StatistikDaten.BART_WELCHESWOHIN:
        d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteFirst();
        String fullname;
        if (d != null) do {
          fullname = d.get(Boote.NAME);
          if (d.get(Boote.VEREIN).length()>0) fullname += " ("+ d.get(Boote.VEREIN) + ")";
          Hashtable ww = null;
          if (sd.art == StatistikDaten.BART_WELCHESWOHIN) ww = new Hashtable();
          if (!ignoreBoot(sd,d))
            if (!h.containsKey(key=EfaUtil.syn2org(Daten.synBoote,fullname))) h.put(key,new HashEl(
                    Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, d.get(Boote.ANZAHL)),
                    Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, d.get(Boote.ART)),
                    Boote.getDetailBezeichnung(d),
                    0,0,0,0,0,new ZielfahrtFolge(),ww,null,null));
        } while ( (d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteNext()) != null);
        break;
      case StatistikDaten.ART_STATUS:
        for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++)
          if (!h.containsKey(key=Daten.fahrtenbuch.getDaten().status[i])) h.put(key,new HashEl("",Daten.fahrtenbuch.getDaten().status[i],"",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.BART_ART: case StatistikDaten.ART_BOOTSART:
        for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++)
          // @todo (P5) statistics if (!h.containsKey(Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT,i))) h.put(Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT,i),new HashEl("",Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT,i),"",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.ART_FAHRTART:
        for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++)
          // @todo (P5) statistics if (!h.containsKey(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION,i))) h.put(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION,i),new HashEl("",Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION,i),"",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.ART_JAHRGANG:
        // nichts zu tun
        break;
      case StatistikDaten.BART_PLAETZE:
        for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++)
          // @todo (P5) statistics if (!h.containsKey(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS,i))) h.put(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS,i),new HashEl(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS,i),"","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.ART_GESCHLECHT:
        for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++)
          // @todo (P5) statistics if (!h.containsKey(Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER,i))) h.put(Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER,i),new HashEl("","","",0,0,0,0,0,new ZielfahrtFolge(),null,null,null));
        break;
      case StatistikDaten.ART_FAHRTENBUCH: case StatistikDaten.BART_FAHRTENBUCH:
        // nichts zu tun
        break;
      case StatistikDaten.ART_MONATSUEBERSICHT:
        // nichts zu tun
        break;
      case StatistikDaten.WETT_DRV:
      case StatistikDaten.WETT_LRVBSOMMER:
      case StatistikDaten.WETT_LRVBWINTER:
      case StatistikDaten.WETT_LRVBWIMPEL:
      case StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT:
      case StatistikDaten.WETT_LRVBRB_FAHRTENWETT:
      case StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT:
        // nichts zu tun
        break;
      default: // Mitglieder, Mitruderer, WerMitWem, WerWohin etc.
        d = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getCompleteFirst();
        String s,status,jahrgang,orgname;

        do {
          orgname = EfaUtil.getFullName(d.get(Mitglieder.VORNAME),d.get(Mitglieder.NACHNAME),d.get(Mitglieder.VEREIN),Daten.fahrtenbuch.getDaten().erstVorname);
          s = EfaUtil.syn2org(Daten.synMitglieder,orgname);
          status = d.get(Mitglieder.STATUS);
          jahrgang = d.get(Mitglieder.JAHRGANG);
          Hashtable ww = null;
          if (sd.art == StatistikDaten.ART_WERMITWEM || sd.art == StatistikDaten.ART_WERWOHIN ||
              sd.art == StatistikDaten.ART_WERMITBOOTSART || sd.art == StatistikDaten.ART_WERMITFAHRTART ||
              sd.art == StatistikDaten.ART_WERUNERLAUBT)
            ww = new Hashtable();
          // Gäste und andere ggf. zusammenfassen (auch vereinsweise)
          if (sd.art == StatistikDaten.ART_MITGLIEDER || sd.art == StatistikDaten.ART_MITRUDERER ||
              sd.art == StatistikDaten.BART_RUDERER ||
              sd.art == StatistikDaten.ART_WERMITWEM || sd.art == StatistikDaten.ART_WERWOHIN ||
              sd.art == StatistikDaten.ART_WERMITBOOTSART || sd.art == StatistikDaten.ART_WERMITFAHRTART ||
              sd.art == StatistikDaten.ART_WERUNERLAUBT) {
            String tmp = gastAndereName(sd,s,status);
            if (sd.gaesteVereinsweise && tmp.equals("")) tmp = d.get(Mitglieder.VEREIN);
            if (!tmp.equals(s)) {
              s = tmp;
              if (s.equals(ANDEREBEZ)) status = ANDERE;
              else status = GAST;
              jahrgang="";
            }
          }
          if ((sd.stat == StatistikDaten.STAT_MITGLIEDER && !ignorePerson(sd,d,orgname,false)) ||
              (sd.stat == StatistikDaten.STAT_BOOTE && !ignoreBoot(sd,d)) ) // nur Nullwerte für die ausgeben, die auch in die Berechnung mit einbezogen sind
            if (!h.containsKey(s)) h.put(s,new HashEl(jahrgang,status,"",0,0,0,0,0,new ZielfahrtFolge(),ww,null,null));
        } while ( (d = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getCompleteNext()) != null);
    }

  }

  // Teilziele ermitteln und in zielArr[] speichern;
  // falls Teilziele nicht erwünscht, dann ist Hauptziel einziges Arr-Element
  static String[] makeZielArr(StatistikDaten sd, String zz) {
    String zielArr[];
    if (sd.ziele_gruppiert) {
      zielArr = new String[EfaUtil.countCharInString(zz,'+')+1];
      int wo;
      int anz=0;
      String kurzzz;
      if (zz.length() == 0) zielArr[0]="";
      while (zz.length() > 0) {
        if ( (wo = zz.indexOf("+")) >=0) {
          kurzzz = zz.substring(0,wo).trim();
          zz = EfaUtil.replace(zz,kurzzz,"").trim();
          zz = EfaUtil.replace(zz,"+","").trim(); // erstes "+" löschen
        } else {
          kurzzz = zz;
          zz = "";
        }
        zielArr[anz++] = EfaUtil.syn2org(Daten.synZiele,kurzzz);
      }
    } else {
      zielArr = new String[1];
      zielArr[0] = EfaUtil.syn2org(Daten.synZiele,zz);
    }
    return zielArr;
  }

  static void tageszeitAddRemaining(int[] a, int total) {
    int _addedUp=0;
    for (int hour=0; hour<24; hour++) _addedUp += a[hour];
    if (_addedUp == 0) return; // nicht möglich, wenn das gesamte a-array auf Null steht
    _addedUp = Math.abs(total - _addedUp); // positiver Rest der verbleibenden, aufzuteilenden Zeit
    while (_addedUp>0)
      for (int hour=0; hour<24 && _addedUp>0; hour++)
        if (a[hour] != 0) { a[hour]+= (a[hour]>0? 1 : -1); _addedUp--; } // beachte, daß bei Vorjahresvergleich auch mit negativen Werten gerechnet werden muß!!
  }






  // Fahrt berechnen
  // 'mitruderer' ist true, wenn es sich um einen Mitruderer eines Fahrtenbucheintrags bei ART_MITRUDERER handelt; sonst false
  static void calc(String name, boolean stm, DatenFelder d, Hashtable h, StatistikDaten sd, boolean mitruderer, String zf,
                   int forcedMTourTage) {
    // Datum überprüfen
    TMJ dateF = EfaUtil.string2date(d.get(Fahrtenbuch.DATUM),0,0,0);
    GregorianCalendar dateCal = new GregorianCalendar(dateF.jahr,dateF.monat-1,dateF.tag);
    dateCal.set(dateF.jahr,dateF.monat-1+dateCal.getMinimum(GregorianCalendar.MONTH),dateF.tag);

    // Art der Fahrt überprüfen
    String mtour = d.get(Fahrtenbuch.FAHRTART);
    boolean mtourfound = false; // true, wenn Mehrtagestour in Liste gefunden und in SD auf true gesetzt ist
    if (mtour.equals(EfaTypes.TYPE_SESSION_NORMAL)) {
      mtourfound = true;
      if (!sd.fahrtart[0]) return; // normale Fahrt == false
    }

    if (d.get(Fahrtenbuch.BOOT).length()>0) {
      DatenFelder boot = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT));
      if (boot != null &&
          boot.get(Boote.ART).equals(EfaTypes.TYPE_BOAT_MOTORBOAT)) {
        if (sd.stat == StatistikDaten.STAT_WETT) return; // zur Sicherheit (falls Bezeichnungen.FAHRT_MOTORBOOT == -1)
        if (Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_MOTORBOAT)) mtour = EfaTypes.TYPE_SESSION_MOTORBOAT;
      }
      if (boot != null &&
          boot.get(Boote.ART).equals(EfaTypes.TYPE_BOAT_ERG)) {
        if (sd.stat == StatistikDaten.STAT_WETT) return; // zur Sicherheit (falls Bezeichnungen.FAHRT_MOTORBOOT == -1)
        if (Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_ERG)) mtour = EfaTypes.TYPE_SESSION_ERG;
      }
    }

    for (int i=1; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION)-1; i++)
      // @todo (P5) statistics if (mtour.equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION,i))) {
      // @todo (P5) statistics   mtourfound = true;
      // @todo (P5) statistics   if (!sd.fahrtart[i]) return; // Fahrtart[1..length-1] == false
      // @todo (P5) statistics   break;
      // @todo (P5) statistics }
    if (!mtourfound && !sd.fahrtart[Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION)-1]) return; // Wanderfahrt == false

    // Infos zu Mehrtagesfahrt holen
    Mehrtagesfahrt mehrtagesfahrt = null;
    if (d.get(Fahrtenbuch.FAHRTART).length()>0) {
        mehrtagesfahrt = Daten.fahrtenbuch.getMehrtagesfahrt(d.get(Fahrtenbuch.FAHRTART));
    }

    // Zeitraum bzgl. Mehrtagestouren überprüfen
    if (mehrtagesfahrt != null) {
      String _datum = d.get(Fahrtenbuch.DATUM);
      if ( (mehrtagesfahrt.start != null && mehrtagesfahrt.start.length()>0 &&
            EfaUtil.secondDateIsAfterFirst(_datum,mehrtagesfahrt.start)) ||
           (mehrtagesfahrt.ende != null && mehrtagesfahrt.ende.length()>0 &&
            EfaUtil.secondDateIsAfterFirst(mehrtagesfahrt.ende,_datum)) ) {
        Dialog.error(International.getMessage("Das Datum des Fahrtenbucheintrags ({entry}) liegt außerhalb des Zeitraums " +
                " ({date_from} - {date_to}), der für die ausgewählte Mehrtagesfahrt '{name}' angegeben wurde.",
                     "#" + d.get(Fahrtenbuch.LFDNR) + " " + _datum,
                     mehrtagesfahrt.start,
                     mehrtagesfahrt.ende,
                     mehrtagesfahrt.name) + "\n" +
                     International.getString("Der Eintrag wird daher NICHT ausgewertet."));
        return;
      }

      if( !mehrtagesfahrt.isEtappen) { // Mehrtagestour, die nicht in Form von Etappen eingegeben wurde!
        TMJ dateM = EfaUtil.string2date(mehrtagesfahrt.ende,0,0,0);
        GregorianCalendar dateMCal = new GregorianCalendar(dateM.jahr,dateM.monat-1,dateM.tag);
        dateMCal.set(dateM.jahr,dateM.monat-1+dateMCal.getMinimum(GregorianCalendar.MONTH),dateM.tag);
        if ( (dateCal.before(sd.vonCal) && dateMCal.after(sd.vonCal)) ||
             (dateCal.before(sd.bisCal) && dateMCal.after(sd.bisCal)) ) {
          if (d.get(Fahrtenbuch.LFDNR).equals(lastLfdNr)) return;
          lastLfdNr = d.get(Fahrtenbuch.LFDNR);
          Dialog.meldung(International.getMessage("Die Mehrtagesfahrt #{number} liegt nur zum Teil im Berechnungszeitraum "+
                         "und wird daher NICHT ausgewertet!",d.get(Fahrtenbuch.LFDNR)));
          return;
        }
      }
    }


    // Anzahl der tatsächlichen Rudertage dieser Fahrt (oder Etappe) ermitteln;
    // Hinweis: Für Mehrtagestouren, die in Form von Etappen eingetragen sind, ist für jede Etappe anzRuderTage == 1!
    int anzRuderTage = 1;
    if (mehrtagesfahrt != null && !mehrtagesfahrt.isEtappen) anzRuderTage = mehrtagesfahrt.rudertage;
    if (forcedMTourTage > 0) anzRuderTage = forcedMTourTage; // wird nur benutzt, wenn eine Mehrtages.Zielfahrt nicht in Form von Etappen berechnet wird und calc() rekursiv aufgerufen wird

    // Datum: Wenn außerhalb Zeitraum, dann raus
    if (dateCal.before(sd.vonCal) || dateCal.after(sd.bisCal)) return;

    // Bei Vorjahresvergleich: Zeitraum prüfen
    if (sd.vorjahresvergleich &&
       ( dateCal.get(Calendar.MONTH)+1 < sd.von.monat ||
         ( dateCal.get(Calendar.MONTH)+1 == sd.von.monat && dateCal.get(Calendar.DAY_OF_MONTH) < sd.von.tag) ) ) return;
    if (sd.vorjahresvergleich &&
       ( dateCal.get(Calendar.MONTH)+1 > sd.bis.monat ||
         ( dateCal.get(Calendar.MONTH)+1 == sd.bis.monat && dateCal.get(Calendar.DAY_OF_MONTH) > sd.bis.tag) ) ) return;

    // Wenn nur Fahrten mit bestimmten Bemerkungen...
    if (sd.nurBemerk != null && sd.nurBemerk.length()>0) {
      Vector nur = EfaUtil.split(sd.nurBemerk.trim(),' ');
      for (int i=0; i<nur.size(); i++)
        if (d.get(Fahrtenbuch.BEMERK).indexOf((String)nur.get(i))<0) return;
    }
    if (sd.nurBemerkNicht != null && sd.nurBemerkNicht.length()>0) {
      Vector nur = EfaUtil.split(sd.nurBemerkNicht.trim(),' ');
      for (int i=0; i<nur.size(); i++)
        if (d.get(Fahrtenbuch.BEMERK).indexOf((String)nur.get(i))>=0) return;
    }


    // Standarddaten setzen
    String jahrgang = "";
    String geschlecht = "";
    String status = "";
    String bezeichnung = "";
    String orgVerein = ""; // Vereinsname vor Wandlung durch syn2org
    String orgName = "";   // Name vor Wandlung durch syn2org
    boolean behinderung = false;

    if (sd.stat == StatistikDaten.STAT_MITGLIEDER || sd.stat == StatistikDaten.STAT_WETT ||
        (mitruderer && sd.art == StatistikDaten.BART_RUDERER) ) {
      // Standarddaten
      status = Daten.fahrtenbuch.getDaten().status[Daten.fahrtenbuch.getDaten().status.length-1];

      // Personendaten ermitteln
      DatenFelder pers = Daten.fahrtenbuch.getDaten().mitglieder.getExactComplete(name);

      // prüfen, ob für die Person Daten berechnet werden sollen
      if (ignorePerson(sd,pers,name,mitruderer)) return;

      // prüfen, ob bei Wettbewerben diese Person ignoriert werden soll
      if (sd.stat == StatistikDaten.STAT_WETT && pers != null && !Mitglieder.isKmwettMelden(pers)) {
        nichtBeruecksichtigt.put(EfaUtil.getFullName(pers.get(Mitglieder.VORNAME),pers.get(Mitglieder.NACHNAME),pers.get(Mitglieder.VEREIN),true), "Soll nicht für Wettbewerbe gemeldet werden");
        return;
      }

      // ggf. Original zum Synonymnamen ermitteln
      orgVerein = EfaUtil.getVerein(name); // Vereinsnamen sichern, falls ein Gast durch ein Synonymnamen seine Vereinsbezeichnung verliert
      orgName = name; // Namen sichern, damit dieser für Unteraufrufe von calc() verwendet werden kann
      name = EfaUtil.syn2org(Daten.synMitglieder,name);

      // prüfen, ob Mitruderer sich selbst berechnet? ;-)
      if (mitruderer && sd.art == StatistikDaten.ART_MITRUDERER) {
        if (name.toUpperCase().equals(sd.name.toUpperCase())) return;
      }

      // hier jetzt: Person, für die die Mitruderer berechnet werden sollen!
      if (!mitruderer && sd.art == StatistikDaten.ART_MITRUDERER) {
        String s;
        if ( !(s = d.get(Fahrtenbuch.STM)).equals("") ) calc(s,true,d,h,sd,true,null,-1);
        for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
          if ( !(s = d.get(i)).equals("") ) calc(s,false,d,h,sd,true,null,-1);
        return;
      }

      if (sd.art == StatistikDaten.ART_MITGLIEDER || sd.art == StatistikDaten.ART_MITRUDERER ||
          sd.art == StatistikDaten.ART_STATUS || sd.art == StatistikDaten.ART_JAHRGANG ||
          sd.art == StatistikDaten.ART_GESCHLECHT || sd.art == StatistikDaten.BART_RUDERER ||
          sd.art == StatistikDaten.ART_WERMITWEM || sd.art == StatistikDaten.ART_WERWOHIN ||
          sd.art == StatistikDaten.ART_WERMITBOOTSART || sd.art == StatistikDaten.ART_WERMITFAHRTART ||
          sd.art == StatistikDaten.ART_WERUNERLAUBT ||
          sd.art == StatistikDaten.WETT_DRV || sd.art == StatistikDaten.WETT_LRVBSOMMER ||
          sd.art == StatistikDaten.WETT_LRVBWINTER || sd.art == StatistikDaten.WETT_LRVBWIMPEL ||
          sd.art == StatistikDaten.WETT_DRV_WAFASTATISTIK ||
          sd.art == StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT || sd.art == StatistikDaten.WETT_LRVBRB_FAHRTENWETT ||
          sd.art == StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT) {
        // relevante Personendaten speichern
        if (pers != null) jahrgang = pers.get(Mitglieder.JAHRGANG);
        if (pers != null) status = pers.get(Mitglieder.STATUS);
        if (pers != null) geschlecht = pers.get(Mitglieder.GESCHLECHT);
        if (pers != null) behinderung = pers.get(Mitglieder.BEHINDERUNG).equals("+");
      }
    } else { // STAT_BOOTE
      // Standarddaten
      status = ANDEREBEZ;
      bezeichnung = ANDEREBEZ;
      jahrgang = ANDEREBEZ;

      // Bootsdaten ermitteln
      DatenFelder boot = Daten.fahrtenbuch.getDaten().boote.getExactComplete(name);

      // ggf. Original zum Synonymnamen ermitteln
      orgVerein = EfaUtil.getVerein(name); // Vereinsnamen sichern, falls ein Gast durch ein Synonymnamen seine Vereinsbezeichnung verliert
      orgName = name; // Namen sichern, damit dieser für Unteraufrufe von calc() verwendet werden kann
      name = EfaUtil.syn2org(Daten.synBoote,name);

      // prüfen, ob für das Boot Daten berechnet werden sollen
      if (ignoreBoot(sd,boot)) return;

      // prüfen, ob spezieller Name angegeben wurde
      if (!mitruderer && !sd.name.equals("")) {
        if (!name.toUpperCase().equals(sd.name.toUpperCase())) return;
      }

      // hier jetzt: Ruderer, für die berechnet werden sollen!
      if (!mitruderer && sd.art == StatistikDaten.BART_RUDERER) {
        String s;
        if ( !(s = d.get(Fahrtenbuch.STM)).equals("") ) calc(s,true,d,h,sd,true,null,-1);
        for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
          if ( !(s = d.get(i)).equals("") ) calc(s,false,d,h,sd,true,null,-1);
        return;
      }

      if (sd.art != StatistikDaten.BART_RUDERER) {
        // relevante Bootsdaten speichern
        if (boot != null) status = Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, boot.get(Boote.ART));
        if (boot != null) {
          bezeichnung = Boote.getDetailBezeichnung(boot);
        }
        if (boot != null) jahrgang = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, boot.get(Boote.ANZAHL));
      }
    }

    // erste und letzte berechnete Fahrt ermitteln
    getErsteLetzteFahrt(sd,d,dateF);

    HashEl ges;

    // Kilometer der Fahrt ermitteln
    int km = EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.BOOTSKM)); // Einheit: 100m (nicht Km)!!
    int mannschKm = EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.MANNSCHKM)); // Einheit: 100m (nicht Km)!!
    int stmKm=0;
    int rudKm=0;
    if (stm) stmKm = km;
    else rudKm = km;

    // Dauer der Fahrt ermitteln
    int abfahrt = -1;
    if (d.get(Fahrtenbuch.ABFAHRT).trim().length() > 0) {
      abfahrt = EfaUtil.string2date(d.get(Fahrtenbuch.ABFAHRT),0,0,0).tag*60 + EfaUtil.string2date(d.get(Fahrtenbuch.ABFAHRT),0,0,0).monat;
      if (abfahrt>=1440) abfahrt = 1339;
    }
    int ankunft = -1;
    if (d.get(Fahrtenbuch.ANKUNFT).trim().length() > 0) {
      ankunft = EfaUtil.string2date(d.get(Fahrtenbuch.ANKUNFT),0,0,0).tag*60 + EfaUtil.string2date(d.get(Fahrtenbuch.ANKUNFT),0,0,0).monat;
      if (ankunft>=1440) ankunft = 1339;
    }
    if (ankunft - abfahrt < 0 && ankunft - abfahrt >= -10) {
      // Annahme: Bei einer solchen Fahrt handelt es sich um eine Fahrt, bei der "Fahrt beginnen" und "Fahrt beenden"
      // kurz nacheinander gedrückt wurden. Diese Fahrt wird korrigiert auf ankuft=abfahrt.
      ankunft = abfahrt;
    }
    int dauer = 0;
    if (abfahrt >= 0 && ankunft >= 0) {
      dauer = ankunft-abfahrt;
    }
    if (dauer<0) dauer+= 1440;

    // Zielfahrt ermitteln
    DatenFelder ziel = Daten.fahrtenbuch.getDaten().ziele.getExactComplete(d.get(Fahrtenbuch.ZIEL));
    if (zf == null) { // falls zf nicht durch calc()-Aufruf vorbelegt ist (bei mehrtägigen Zielfahrten der Fall)
      zf = "";
      if (ziel != null && km>=Daten.ZIELFAHRTKM) zf = ziel.get(Ziele.BEREICH);
    }

    // nur Steg-Km berechnen?
    if (sd.nurStegKm && (ziel == null || !ziel.get(Ziele.STEGZIEL).equals("+"))) return;

    // nur Fahrten mit mind x. Km
    if (km < sd.nurMindKm) return;

    // nur Boote für Gruppe
    if (sd.nurBooteFuerGruppe != null && sd.nurBooteFuerGruppe.length() > 0) {
      DatenFelder boot = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT));
      if (boot == null) return;
      Vector gruppen = Boote.getGruppen(boot);
      boolean found = false;
      for (int i=0; i<gruppen.size(); i++) {
        if (sd.nurBooteFuerGruppe.toUpperCase().equals(((String)gruppen.get(i)).toUpperCase())) found = true;
      }
      if (!found) return;
    }

    // Ziel-Array aufbauen
    String zielArr[] = makeZielArr(sd,d.get(Fahrtenbuch.ZIEL));

    // Gäste und andere ggf. zusammenfassen (auch vereinsweise)
    if (sd.art == StatistikDaten.ART_MITGLIEDER || sd.art == StatistikDaten.ART_MITRUDERER ||
        sd.art == StatistikDaten.BART_RUDERER ||
        sd.art == StatistikDaten.ART_WERMITWEM || sd.art == StatistikDaten.ART_WERWOHIN ||
        sd.art == StatistikDaten.ART_WERMITBOOTSART || sd.art == StatistikDaten.ART_WERMITFAHRTART ||
        sd.art == StatistikDaten.ART_WERUNERLAUBT) {
      String tmp = gastAndereName(sd,name,status);
      if (sd.gaesteVereinsweise && tmp.equals("")) tmp = orgVerein;
      if (!tmp.equals(name)) {
        name = tmp;
        if (name.equals(ANDEREBEZ)) status = ANDERE;
        else status = GAST;
        jahrgang=""; geschlecht="";
      }
    }


    // Mehrtagesfahrten mit mehreren Zielbereichen in einzelne Teilfahrten aufspalten
    // Bugfix in 1.4.1_03: Aufspaltung für ALLE Statistikarten mit Ausnahme der aufgezählten durchführen!
    if (sd.art != StatistikDaten.ART_ZIELE &&
        sd.art != StatistikDaten.ART_FAHRTENBUCH &&
        sd.art != StatistikDaten.BART_ZIELE &&
        sd.art != StatistikDaten.BART_FAHRTENBUCH &&
        sd.art != StatistikDaten.ART_WERWOHIN &&
        sd.art != StatistikDaten.BART_WELCHESWOHIN &&
        sd.art != StatistikDaten.WETT_DRV &&
        sd.art != StatistikDaten.WETT_DRV_WAFASTATISTIK &&
        sd.art != StatistikDaten.WETT_LRVBRB_FAHRTENWETT &&
        sd.art != StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT &&
        sd.art != StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT &&
        anzRuderTage>1) {

      int anzZB = 0;
      if (zf != null && zf.length() > 0) {
        anzZB = new ZielfahrtFolge(zf).getAnzZielfahrten();
      }
      if (anzZB>anzRuderTage) {
        // @i18n only relevant for Berlin, GER (no need to internationalize)
        Dialog.error("Für Fahrt #"+d.get(Fahrtenbuch.LFDNR)+" sind Zielbereiche für "+anzZB+" Etappen angegeben, aber "+
                     "die Mehrtagesfahrt hat nur "+anzRuderTage+" Rudertage! "+
                     "Die Zielbereiche der Fahrt #"+d.get(Fahrtenbuch.LFDNR)+" wurden daher bei der Auswertung NICHT berücksichtigt! "+
                     "(Bitte korrigiere die Anzahl der Rudertage oder die Zielbereiche!)");
        anzZB = 0;
        zf = null;
      }

      int _km;
      _km = ( (km / anzRuderTage) / 5 ) * 5; // Grundsätzlich werden alle Teiletappen immer auf ganze oder halbe Kilometer gerundet (Bugfix in 1.8.1_05)

      if (anzZB >= 1 && _km < Daten.ZIELFAHRTKM) {
        // @i18n only relevant for Berlin, GER (no need to internationalize)
        Dialog.error("Beim Versuch, die Fahrt #"+d.get(Fahrtenbuch.LFDNR)+" in "+anzRuderTage+" Teilfahrten aufzuteilen, um die "+
                     "Zielfahrten der einzelnen Etappen zu berechnen, hat efa festgestellt, daß die einzelnen "+
                     "Etappen weniger als "+(Daten.ZIELFAHRTKM / 10)+ " Km lang sind und somit keine Zielfahrten darstellen. "+
                     "Die Zielbereiche der Fahrt #"+d.get(Fahrtenbuch.LFDNR)+" wurden daher bei der Auswertung NICHT berücksichtigt! "+
                     "(Bitte gib im Zweifelsfall die Fahrt in Form von einzelnen Etappen ein!)");
        anzZB = 0;
        zf = null;
      }

      DatenFelder ddd = new DatenFelder(d); // Datenfelder kopieren
      String mtourStart = null;
      String mtourEnde = null;
      if (mehrtagesfahrt != null && mehrtagesfahrt.start != null && mehrtagesfahrt.start.length()>0) mtourStart = mehrtagesfahrt.start;
      if (mehrtagesfahrt != null && mehrtagesfahrt.ende != null && mehrtagesfahrt.ende.length()>0) mtourEnde = mehrtagesfahrt.ende;
      for (int i=0; i<anzRuderTage; i++) {
        int _mtourtage;
        if (i<anzRuderTage-1) { // nicht die letzte Fahrt
          _mtourtage = 1;
        } else { // letzte Fahrt
          if (_km*anzRuderTage<km) _km += km - _km*anzRuderTage;
          _mtourtage = anzRuderTage-i;
        }
        ddd.set(Fahrtenbuch.BOOTSKM,EfaUtil.zehntelInt2String(_km));
        String startDatum = d.get(Fahrtenbuch.DATUM);
        if (mtourStart != null) startDatum = mtourStart;
        TMJ dateM = EfaUtil.string2date(startDatum,0,0,0);
        GregorianCalendar dateMCal = new GregorianCalendar(dateM.jahr,dateM.monat-1,dateM.tag);
        dateMCal.add(GregorianCalendar.DAY_OF_YEAR,i);
        String thisDate = dateMCal.get(GregorianCalendar.DAY_OF_MONTH)+"."+(dateMCal.get(GregorianCalendar.MONTH)+1)+"."+dateMCal.get(GregorianCalendar.YEAR);
        if ( (mtourEnde != null && EfaUtil.secondDateIsAfterFirst(mtourEnde,thisDate) ) ||
             (mtourStart != null && EfaUtil.secondDateIsAfterFirst(thisDate,mtourStart) ) ) {
          Dialog.error(International.getMessage("Die Angaben zu Anfang und Ende der Mehrtagesfahrt #{number} "+
                       "bzw. die Anzahl der angegebenen Rudertage sind unstimmig.",d.get(Fahrtenbuch.LFDNR)) +
                       "\n" +
                       International.getMessage("Alle Etappen ab Etappe {number} ({date}) werden ignoriert.",i+1,thisDate));
          return;
        }
        ddd.set(Fahrtenbuch.DATUM,thisDate);
        ddd.set(Fahrtenbuch.ZIEL,d.get(Fahrtenbuch.ZIEL)+" (" + International.getMessage("Teil {n}",i+1)+")");
        String teilzf = "";
        ZielfahrtFolge zff = new ZielfahrtFolge(zf);
        if (zff.getAnzZielfahrten() > i) teilzf = zff.getZielfahrt(i).getBereiche();
        calc(orgName,stm,ddd,h,sd,mitruderer,teilzf,_mtourtage);
      }
      return;
    }
    // Bugfix in 1.4.1_03: Semikolons nicht generell, sondern nur in 1tägigen Fahrten verbieten
    // in 1.7.4_00: Grund für nachfolgendes Bugfix nicht mehr ersichtlich; der Bugfix wurde daher entfernt.
//    else if (anzRuderTage==1 && zf.indexOf(ZielfahrtFolge.ZIELFAHRT_MTOUR_TRENNER_S)>=0) zf = EfaUtil.replace(zf,ZielfahrtFolge.ZIELFAHRT_MTOUR_TRENNER_S,",",true); // keine Semikolons in Zielfahrten erlauben

    int eins = 1; // Wert für "1" (positiv, oder ggf. bei sd.vorjahresvergleich auch negativ)

    // Bei Vorjahresvergleich: Auswertung einer Fahrt im Vorjahr
    if (sd.vorjahresvergleich && dateCal.get(Calendar.YEAR) == sd.von.jahr) {
      // Liegt die Fahrt hinter dem Tag und Monat des Ende des Berechnungszeitraums?
      if (dateCal.get(Calendar.MONTH)+1 > sd.bis.monat ||
          (dateCal.get(Calendar.MONTH)+1 == sd.bis.monat && dateCal.get(Calendar.DAY_OF_MONTH) > sd.bis.tag) ) return;
      km *= -1;
      rudKm *= -1;
      stmKm *= -1;
      mannschKm *= -1;
      dauer *= -1;
      eins *= -1;
    }

    sd.alleAusgewertetenEintraege.put(Daten.fahrtenbuch.getFileName()+"#"+d.get(Fahrtenbuch.LFDNR),"foo");


    // Fahrt hinzufügen
    switch (sd.art) {
      case StatistikDaten.ART_ZIELE: case StatistikDaten.BART_ZIELE:
        for (int i=0; i<zielArr.length; i++) { // alle Teilziele (falls keine Teilziele, dann ist nur das 1 Hauptziel im Array
          if ( (ges = (HashEl)h.get(zielArr[i])) == null) h.put(zielArr[i],new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
          else h.put(zielArr[i],new HashEl("","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm+mannschKm, ges.dauer+dauer, eins+ges.anz,new ZielfahrtFolge(zf),null,null,null));
        }
        break;
      case StatistikDaten.ART_KMFAHRT: case StatistikDaten.BART_KMFAHRT:
        if (sd.kmfahrt_gruppiert) {
          for (int k=0; k<=Math.abs(km); k+=50) {
            if (Math.abs(km)<=k+49) {
                name = International.getMessage("{begin} bis {end}",EfaUtil.zehntelInt2String(k),EfaUtil.zehntelInt2String(k+49));
            }
          }
        } else name = d.get(Fahrtenbuch.BOOTSKM);
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl("","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins+ges.anz,ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_WOTAGE: case StatistikDaten.BART_WOTAGE:
        switch (dateCal.get(GregorianCalendar.DAY_OF_WEEK)) {
          case GregorianCalendar.MONDAY:    name = International.getString("Montag");    jahrgang = "01"; break;
          case GregorianCalendar.TUESDAY:   name = International.getString("Dienstag");  jahrgang = "02"; break;
          case GregorianCalendar.WEDNESDAY: name = International.getString("Mittwoch");  jahrgang = "03"; break;
          case GregorianCalendar.THURSDAY:  name = International.getString("Donnerstag");jahrgang = "04"; break;
          case GregorianCalendar.FRIDAY:    name = International.getString("Freitag");   jahrgang = "05"; break;
          case GregorianCalendar.SATURDAY:  name = International.getString("Samstag");   jahrgang = "06"; break;
          case GregorianCalendar.SUNDAY:    name = International.getString("Sonntag");   jahrgang = "07"; break;
          default:                          name = ANDEREBEZ;   jahrgang = "99"; break;
        }
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl(jahrgang,"","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl(jahrgang,"","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins+ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_MONATE: case StatistikDaten.BART_MONATE:
        switch (dateCal.get(GregorianCalendar.MONTH)) {
          case GregorianCalendar.JANUARY:   name = International.getString("Januar");    jahrgang = "01"; break;
          case GregorianCalendar.FEBRUARY:  name = International.getString("Februar");   jahrgang = "02"; break;
          case GregorianCalendar.MARCH:     name = International.getString("März");      jahrgang = "03"; break;
          case GregorianCalendar.APRIL:     name = International.getString("April");     jahrgang = "04"; break;
          case GregorianCalendar.MAY:       name = International.getString("Mai");       jahrgang = "05"; break;
          case GregorianCalendar.JUNE:      name = International.getString("Juni");      jahrgang = "06"; break;
          case GregorianCalendar.JULY:      name = International.getString("Juli");      jahrgang = "07"; break;
          case GregorianCalendar.AUGUST:    name = International.getString("August");    jahrgang = "08"; break;
          case GregorianCalendar.SEPTEMBER: name = International.getString("September"); jahrgang = "09"; break;
          case GregorianCalendar.OCTOBER:   name = International.getString("Oktober");   jahrgang = "10"; break;
          case GregorianCalendar.NOVEMBER:  name = International.getString("November");  jahrgang = "11"; break;
          case GregorianCalendar.DECEMBER:  name = International.getString("Dezember");  jahrgang = "12"; break;
          default:                          name = ANDEREBEZ;   jahrgang = "99"; break;
        }
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl(jahrgang,"","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl(jahrgang,"","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins+ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_TAGESZEIT: case StatistikDaten.BART_TAGESZEIT:
        if (abfahrt < 0 || ankunft < 0) {
          if ( (ges = (HashEl)h.get(name)) == null) h.put(ANDEREBEZ,new HashEl("99","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
          else h.put(ANDEREBEZ,new HashEl("99","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins+ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        } else {
          int[] _rudKm = new int[24], _stmKm = new int[24], _mannschKm = new int[24], _dauer = new int[24];
          boolean[] _hour = new boolean[24];
          for (int hour=0; hour<24; hour++) {
            if (hour>=abfahrt/60 && hour<=ankunft/60 || // Fahrt innerhalb desselben Tages
                (abfahrt>ankunft && (hour>=abfahrt/60 || hour<=ankunft/60)) ) { // Fahrt über Mitternacht hinaus
              int anteilAnStunde = 60;
              if (abfahrt>hour*60 && abfahrt<(hour+1)*60) anteilAnStunde -= abfahrt - hour*60;
              if (ankunft>=hour*60 && ankunft<(hour+1)*60) anteilAnStunde -= (hour+1)*60 - ankunft;
              if (anteilAnStunde == 0) anteilAnStunde = 1;
              _rudKm[hour]     = (rudKm     * anteilAnStunde) / (dauer == 0 ? 1 : dauer) * eins;
              _stmKm[hour]     = (stmKm     * anteilAnStunde) / (dauer == 0 ? 1 : dauer) * eins;
              _mannschKm[hour] = (mannschKm * anteilAnStunde) / (dauer == 0 ? 1 : dauer) * eins;
              _dauer[hour]     = (dauer     * anteilAnStunde) / (dauer == 0 ? 1 : dauer) * eins;
              _hour[hour] = true;
            } else {
              _rudKm[hour]     = 0;
              _stmKm[hour]     = 0;
              _mannschKm[hour] = 0;
              _dauer[hour]     = 0;
              _hour[hour] = false;
            }
          }
          tageszeitAddRemaining(_rudKm,rudKm);
          tageszeitAddRemaining(_stmKm,stmKm);
          tageszeitAddRemaining(_mannschKm,mannschKm);
          tageszeitAddRemaining(_dauer,dauer);

          for (int hour=0; hour<24; hour++) {
            if (_hour[hour]) {
              name = International.getMessage("{hour} Uhr",hour);
              jahrgang = (hour<10 ? "0" : "") + hour;
              if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl(jahrgang,"","",_rudKm[hour],_stmKm[hour],_mannschKm[hour],_dauer[hour],eins,new ZielfahrtFolge(zf),null,null,null));
              else h.put(name,new HashEl(jahrgang,"","",_rudKm[hour] + ges.rudKm, _stmKm[hour] + ges.stmKm, ges.mannschKm + _mannschKm[hour], ges.dauer + _dauer[hour], eins+ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
            }
          }
        }
        break;
      case StatistikDaten.ART_JAHRE: case StatistikDaten.BART_JAHRE:
        name = Integer.toString(dateF.jahr);
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl("","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_BOOTE:
        name = EfaUtil.syn2org(Daten.synBoote,d.get(Fahrtenbuch.BOOT));
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl("","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_STATUS: case StatistikDaten.BART_ART:
        name = status;
        if (name.equals("")) name=International.getString("unbekannt");
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl("",status,"",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl("",status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer, eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_JAHRGANG: case StatistikDaten.BART_PLAETZE:
        name = jahrgang;
        if (name.equals("")) name=International.getString("unbekannt");
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl(jahrgang,"","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl(jahrgang,"","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.BART_ARTDETAIL: case StatistikDaten.ART_BOOTSART:
        // Bei StatistiKDaten.ART_BOOTSART wurde "bezeichnung" noch nicht ermittelt, daher muß dies hier nachgeholt werden!
        if (sd.art == StatistikDaten.ART_BOOTSART) {
          DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT));
          if (b != null) {
            status = Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, b.get(Boote.ART));
            jahrgang = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, b.get(Boote.ANZAHL));
            bezeichnung = Boote.getDetailBezeichnung(b);
          } else {
            status = "";
            jahrgang = "";
            bezeichnung = "";
          }
        }
        name = bezeichnung;
        if (name.trim().equals("")) name=International.getString("unbekannt");
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl(jahrgang,status,bezeichnung,rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl(jahrgang,status,bezeichnung,rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_GESCHLECHT:
        name = Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, geschlecht);
        if (name.equals("")) name=International.getString("unbekannt");
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl("","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_FAHRTART:
        if (mtour.equals("")) mtour = EfaTypes.TYPE_SESSION_NORMAL;
        name = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, mtour);
        if (!mtourfound) name = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_TOUR);
        if ( (ges = (HashEl)h.get(name)) == null) h.put(name,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
        else h.put(name,new HashEl("","","",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,null));
        break;
      case StatistikDaten.ART_WERMITWEM:
        if ( (ges = (HashEl)h.get(name)) == null) { // schauen, ob für akt. Person bereits Eintrag vorhanden
          Hashtable ww = new Hashtable(); // --> nein, neue Person --> dann auch alle Mitruderer neu
          String s;
          if ( !(s = d.get(Fahrtenbuch.STM)).equals("") ) {
            if (sd.ww_horiz_alle || !ignorePerson(sd,s,false)) {
              if (sd.gasteAlsEinePerson) s = gastAndereName(sd,s,null);
              s = EfaUtil.syn2org(Daten.synMitglieder,s);
              ww.put(s,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
              alleWW.put(s,new Object());
            }
          }
          for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
            if ( !(s = d.get(i)).equals("") ) {
              if (sd.ww_horiz_alle || !ignorePerson(sd,s,false)) {
                if (sd.gasteAlsEinePerson) s = gastAndereName(sd,s,null);
                s = EfaUtil.syn2org(Daten.synMitglieder,s);
                ww.put(s,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
                alleWW.put(s,new Object());
              }
            }
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),ww,null,null));
        } else { // --> ja, akt. Person bereits vorhanden
          HashEl gesww; // --> dann prüfen, ob Mitruderer jeweils schon vorhanden (werden in gesww gespeichert)
          String s;
          if ( !(s = d.get(Fahrtenbuch.STM)).equals("") ) {
            if (sd.ww_horiz_alle || !ignorePerson(sd,s,false)) {
              if (sd.gasteAlsEinePerson) s = gastAndereName(sd,s,null);
              s = EfaUtil.syn2org(Daten.synMitglieder,s);
              if ( (gesww = (HashEl)ges.ww.get(s)) == null) ges.ww.put(s,new HashEl("","","",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
              else ges.ww.put(s,new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm, gesww.dauer+dauer,eins*anzRuderTage + gesww.anz,new ZielfahrtFolge(zf),null,null,null));
              alleWW.put(s,new Object());
            }
          }
          for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
            if ( !(s = d.get(i)).equals("") ) {
              if (sd.ww_horiz_alle || !ignorePerson(sd,s,false)) {
                if (sd.gasteAlsEinePerson) s = gastAndereName(sd,s,null);
                s = EfaUtil.syn2org(Daten.synMitglieder,s);
                if ( (gesww = (HashEl)ges.ww.get(s)) == null) ges.ww.put(s,new HashEl("","","",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,null));
                else ges.ww.put(s,new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm,gesww.dauer+dauer,eins*anzRuderTage + gesww.anz,gesww.zf.addZielfahrten(zf),null,null,null));
                alleWW.put(s,new Object());
              }
            }
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),ges.ww,null,null));
        }
        break;
      case StatistikDaten.ART_WERWOHIN:
        if ( (ges = (HashEl)h.get(name)) == null) { // schauen, ob für akt. Person bereits Eintrag vorhanden
          Hashtable ww = new Hashtable(); // --> nein, neue Person --> dann auch alles neue Ziele
          for (int i=0; i<zielArr.length; i++) {
            if ( !zielArr[i].equals("") )
              ww.put(zielArr[i],new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
            alleWW.put(zielArr[i],new Object());
          }
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),ww,null,null));
        } else { // --> ja, akt. Person bereits vorhanden
          HashEl gesww; // --> dann prüfen, ob Ziel schon vorhanden (wird in gesww gespeichert)
          for (int i=0; i<zielArr.length; i++) {
            if ( !zielArr[i].equals("") )
              if ( (gesww = (HashEl)ges.ww.get(zielArr[i])) == null) ges.ww.put(zielArr[i],new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
              else ges.ww.put(zielArr[i],new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm, gesww.dauer+dauer,eins+gesww.anz,gesww.zf.addZielfahrten(zf),null,null,null));
            alleWW.put(zielArr[i],new Object());
          }
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins+ges.anz, ges.zf.addZielfahrten(zf),ges.ww,null,null));
        }
        break;
      case StatistikDaten.ART_WERMITBOOTSART:
        String bootsart = International.getString("unbekannt");
        if (d.get(Fahrtenbuch.BOOT).trim().length()>0) {
          DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT).trim());
          if (b != null) bootsart = Boote.getDetailBezeichnung(b);
        }

        if ( (ges = (HashEl)h.get(name)) == null) { // schauen, ob für akt. Person bereits Eintrag vorhanden
          Hashtable ww = new Hashtable(); // --> nein, neue Person --> dann auch alles neue Ziele
          if (bootsart.length()>0) {
            ww.put(bootsart,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
            alleWW.put(bootsart,new Object());
          }
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),ww,null,null));
        } else { // --> ja, akt. Person bereits vorhanden
          HashEl gesww; // --> dann prüfen, ob Ziel schon vorhanden (wird in gesww gespeichert)
          if (bootsart.length()>0) {
            if ( (gesww = (HashEl)ges.ww.get(bootsart)) == null) ges.ww.put(bootsart,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
            else ges.ww.put(bootsart,new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm, gesww.dauer+dauer,eins+gesww.anz,gesww.zf.addZielfahrten(zf),null,null,null));
            alleWW.put(bootsart,new Object());
          }
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins+ges.anz, ges.zf.addZielfahrten(zf),ges.ww,null,null));
        }
        break;
      case StatistikDaten.ART_WERUNERLAUBT:
        String boot = d.get(Fahrtenbuch.BOOT).trim();
        if (Daten.gruppen != null && name.length() > 0 && boot.length()>0 && !stm) {
          DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot);
          if (b != null) {
            Vector gruppen = Boote.getGruppen(b);
            boolean inGruppe = false;
            for (int i=0; i<gruppen.size() && !inGruppe; i++) {
              if (Daten.gruppen.isInGroup((String)gruppen.get(i),EfaUtil.getVorname(name),EfaUtil.getNachname(name),EfaUtil.getVerein(name))) inGruppe = true;
            }
            if (!inGruppe && gruppen.size()>0) {
              if ( (ges = (HashEl)h.get(name)) == null) { // schauen, ob für akt. Person bereits Eintrag vorhanden
                Hashtable ww = new Hashtable(); // --> nein, neue Person --> dann auch alles neue Ziele
                ww.put(boot,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
                alleWW.put(boot,new Object());
                h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),ww,null,null));
              } else { // --> ja, akt. Person bereits vorhanden
                HashEl gesww; // --> dann prüfen, ob Ziel schon vorhanden (wird in gesww gespeichert)
                if ( (gesww = (HashEl)ges.ww.get(boot)) == null) ges.ww.put(boot,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
                else ges.ww.put(boot,new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm, gesww.dauer+dauer,eins+gesww.anz,gesww.zf.addZielfahrten(zf),null,null,null));
                alleWW.put(boot,new Object());
                h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins+ges.anz, ges.zf.addZielfahrten(zf),ges.ww,null,null));
              }
            }
          }
        }
        break;
      case StatistikDaten.ART_WERMITFAHRTART:
        String fahrtart = mtour;
        if (!mtourfound) name = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_TOUR);
        if (name.equals("")) name = Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_NORMAL);

        if ( (ges = (HashEl)h.get(name)) == null) { // schauen, ob für akt. Person bereits Eintrag vorhanden
          Hashtable ww = new Hashtable(); // --> nein, neue Person --> dann auch alles neue Ziele
          ww.put(fahrtart,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
          alleWW.put(fahrtart,new Object());
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),ww,null,null));
        } else { // --> ja, akt. Person bereits vorhanden
          HashEl gesww; // --> dann prüfen, ob Ziel schon vorhanden (wird in gesww gespeichert)
          if ( (gesww = (HashEl)ges.ww.get(fahrtart)) == null) ges.ww.put(fahrtart,new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
          else ges.ww.put(fahrtart,new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm, gesww.dauer+dauer,eins+gesww.anz,gesww.zf.addZielfahrten(zf),null,null,null));
          alleWW.put(fahrtart,new Object());
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins+ges.anz, ges.zf.addZielfahrten(zf),ges.ww,null,null));
        }
        break;
      case StatistikDaten.BART_WELCHESWOHIN:
        if ( (ges = (HashEl)h.get(name)) == null) { // schauen, ob für akt. Person bereits Eintrag vorhanden
          Hashtable ww = new Hashtable(); // --> nein, neue Person --> dann auch alles neue Ziele
          for (int i=0; i<zielArr.length; i++) {
            if ( !zielArr[i].equals("") )
              ww.put(zielArr[i],new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
            alleWW.put(zielArr[i],new Object());
          }
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),ww,null,null));
        } else { // --> ja, akt. Person bereits vorhanden
          HashEl gesww; // --> dann prüfen, ob Ziel schon vorhanden (wird in gesww gespeichert)
          for (int i=0; i<zielArr.length; i++) {
            if ( !zielArr[i].equals("") )
              if ( (gesww = (HashEl)ges.ww.get(zielArr[i])) == null) ges.ww.put(zielArr[i],new HashEl("","","",rudKm,stmKm,mannschKm,dauer,eins,new ZielfahrtFolge(zf),null,null,null));
              else ges.ww.put(zielArr[i],new HashEl("","","",rudKm + gesww.rudKm, stmKm + gesww.stmKm, gesww.mannschKm+mannschKm, gesww.dauer+dauer,eins+gesww.anz,gesww.zf.addZielfahrten(zf),null,null,null));
            alleWW.put(zielArr[i],new Object());
          }
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins+ges.anz, ges.zf.addZielfahrten(zf),ges.ww,null,null));
        }
        break;
      case StatistikDaten.ART_FAHRTENBUCH: case StatistikDaten.BART_FAHRTENBUCH:

        // LfdNr holen
        String lfdnr = d.get(Fahrtenbuch.LFDNR);                         // lfdnr:      Original LfdNr aus dem Fahrtenbuch
        int lfdnri = EfaUtil.string2date(lfdnr,1,0,0).tag;               // lfdnri:     LfdNr ohne Buchstabe
        TMJ tmp = EfaUtil.string2date(d.get(Fahrtenbuch.DATUM),0,0,0);
        String lfdnr_key = tmp.jahr+"/"+(tmp.monat<10 ? "0"+tmp.monat : ""+tmp.monat)+"/"+(tmp.tag<10 ? "0"+tmp.tag : ""+tmp.tag)+"-"+EfaUtil.leadingZeroString(lfdnri,7)+lfdnr;
        String lfdnr_show = Integer.toString(lfdnri);                    // lfdnr_show: LfdNr, die angezeigt werden soll

        // String für Sortierkriterium "Lfd. Nr" generieren
        String sort_lfdnr = EfaUtil.leadingZeroString(lfdnri,7)+lfdnr;
        sort_lfdnr = tmp.jahr+"-"+sort_lfdnr+"-"+(tmp.monat<10 ? "0"+tmp.monat : ""+tmp.monat)+"/"+(tmp.tag<10 ? "0"+tmp.tag : ""+tmp.tag);


        if (lfdnr_key.equals(lastLfdNr)) return; // Abbruch, wenn Eintrag schon bearbeitet
        lastLfdNr = lfdnr_key;

        while (h.get(lfdnr_key) != null) lfdnr_key = lfdnr_key + "+"; // Doppelte Keys vermeiden

        // Felder für Fahrtenbuch aufbauen
        String[] as = new String[11];

        if (sd.fbLfdNr) as[0] = lfdnr_show; // Ein ggf. vorhandener Buchstabe in der LfdNr wird in der Ausgabe ignoriert!
        if (sd.fbDatum) as[1] = d.get(Fahrtenbuch.DATUM);
        if (sd.fbBoot) as[2] = EfaUtil.syn2org(Daten.synBoote,d.get(Fahrtenbuch.BOOT));
        if (sd.fbStm) as[3] = EfaUtil.syn2org(Daten.synMitglieder,d.get(Fahrtenbuch.STM));
        String s = EfaUtil.syn2org(Daten.synMitglieder,d.get(Fahrtenbuch.MANNSCH1));
        for (int i=Fahrtenbuch.MANNSCH2; i<=Fahrtenbuch.MANNSCH24; i++)
          if (!d.get(i).equals("")) s = s+"; "+EfaUtil.syn2org(Daten.synMitglieder,d.get(i));
        if (sd.fbMannsch) as[4] = s;
        if (sd.fbAbfahrt) as[5] =d.get(Fahrtenbuch.ABFAHRT);
        if (sd.fbAnkunft) as[6] =d.get(Fahrtenbuch.ANKUNFT);
        if (sd.fbZiel) as[7] =d.get(Fahrtenbuch.ZIEL);
        if (sd.fbBootsKm) as[8] =d.get(Fahrtenbuch.BOOTSKM);
        if (sd.fbMannschKm) as[9] =d.get(Fahrtenbuch.MANNSCHKM);
        if (sd.fbBemerkungen) {
          as[10] = d.get(Fahrtenbuch.BEMERK);
          if (sd.fbZielbereichInBemerkungen && zf.length()>0) as[10] = (as[10].length() > 0 ? as[10]+"; " : "") + 
                  International.onlyFor("Zielbereiche","de")+": "+zf;
          if (sd.fbFahrtartInBemerkungen && mtour.length()>0) as[10] =
            (as[10].length() > 0 ? as[10]+"; " : "") + mtour;

        }
        h.put(lfdnr_key,new HashEl(sort_lfdnr,null,null,rudKm,stmKm,mannschKm,0,0,new ZielfahrtFolge(zf),null,as,null));
        break;
      case StatistikDaten.ART_MONATSUEBERSICHT:
        TMJ tmp2 = EfaUtil.string2date(d.get(Fahrtenbuch.DATUM),0,0,0);
        if (tmp2.jahr == 0 || tmp2.monat == 0 || tmp2.tag == 0) name = International.getString("unbekannt");
        else name = tmp2.jahr + "/" + (tmp2.monat<10 ? "0" : "")+tmp2.monat + "/" + (tmp2.tag<10 ? "0" : "")+tmp2.tag;
        int iii = 0;
        while (h.get(name) != null) name = name + (iii == 0 ? " " : "") + (iii++);
        h.put(name,new HashEl(name,mtour,"",rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,null,null,null,null));
        break;
      case StatistikDaten.WETT_DRV:
        if ( (ges = (HashEl)h.get(name)) == null) {
          KmWettInfo kmwett = new KmWettInfo();
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) kmwett.geschlecht = 0;
          else kmwett.geschlecht = 1;
          kmwett.behinderung = behinderung;

// @gehtnichtmehrinefa2           wettAddWafa(kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                   Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,new ZielfahrtFolge(),null,null,kmwett));
        } else {
// @gehtnichtmehrinefa2           wettAddWafa(ges.kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                   Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, mannschKm,0,eins*anzRuderTage+ges.anz, new ZielfahrtFolge(),null,null,ges.kmwett));
        }
        break;
      case StatistikDaten.WETT_LRVBSOMMER:
        if ( (rudKm+stmKm) % 5 > 0) rudKm += 5 - (rudKm+stmKm) % 5; // auf .5 oder .0 aufrunden
        if ( (ges = (HashEl)h.get(name)) == null) {
          KmWettInfo kmwett = new KmWettInfo();
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) kmwett.geschlecht = 0;
          else kmwett.geschlecht = 1;
          kmwett.behinderung = behinderung;
          if (zf.length()>0) wettAddZf(kmwett,d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),zf);
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,kmwett));
        } else {
          if (zf.length()>0) wettAddZf(ges.kmwett,d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),zf);
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm,mannschKm,0, eins*anzRuderTage+ges.anz,ges.zf.addZielfahrten(zf),null,null,ges.kmwett));
        }
        break;
      case StatistikDaten.WETT_LRVBWINTER:
        if ( (rudKm+stmKm) % 5 > 0) rudKm += 5 - (rudKm+stmKm) % 5; // auf .5 oder .0 aufrunden
        if (rudKm + stmKm == 0) return; // keine 0Km-Fahrten werten, da diese sonst unberechtigt zur Erfüllung der Regel "mind 8 Fahrten / mind 3 Monate" führen könnte
        if ( (ges = (HashEl)h.get(name)) == null) {
          KmWettInfo kmwett = new KmWettInfo();
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) kmwett.geschlecht = 0;
          else kmwett.geschlecht = 1;
          kmwett.behinderung = behinderung;
          wettAddWinter(kmwett,d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),mehrtagesfahrt,sd);
          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,new ZielfahrtFolge(),null,null,kmwett));
        } else {
          wettAddWinter(ges.kmwett,d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),mehrtagesfahrt,sd);
          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, mannschKm,0,eins*anzRuderTage+ges.anz, new ZielfahrtFolge(),null,null,ges.kmwett));
        }
        break;
      case StatistikDaten.WETT_DRV_WAFASTATISTIK:
        // Berechnung der Teilnehmerkilometer
        int jjj = EfaUtil.string2int(jahrgang,0);
        if (jjj == 0) {
          // @i18n only Germany, no need to translate
          String wtext = "Das Alter des Teilnehmers '"+name+"' konnte nicht ermittelt werden, da sein/ihr Jahrgang "+
                         "nicht in efa erfaßt ist! Fahrten dieses Teilnehmers werden ignoriert.\n";
          if (warnungen.indexOf(wtext)<0) warnungen += wtext; // keine doppelten Warnungen hinzuf�gen
          return;
        }

        // Alter des Teilnehmers
        int alter = sd.wettJahr - jjj;

        // Anzahl der aktiven Ruderer ermitteln
        if (km >= 10) { // mind. 1 Km gerudert
          if (alter>18) { // über 18 Jahre
            if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
              alleAktive.put(name,AKTIV_M_AB19);
            } else { // weiblich
              alleAktive.put(name,AKTIV_W_AB19);
            }
          } else { // bis 18 Jahre
            if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
              alleAktive.put(name,AKTIV_M_BIS18);
            } else { // weiblich
              alleAktive.put(name,AKTIV_W_BIS18);
            }
          }
        }

        // Mehrtagesfahrt berechnen
        String fahrtname = d.get(Fahrtenbuch.FAHRTART);
// @gehtnichtmehrinefa2         if (!mayBeWafa(fahrtname,km)) return;
        if (fahrtname.length() == 0 || Mehrtagesfahrt.isVordefinierteFahrtart(fahrtname)) {
            fahrtname = d.get(Fahrtenbuch.ZIEL);
        } else {
            if (mehrtagesfahrt != null) {
                fahrtname = mehrtagesfahrt.name;
            }
        }

        if (mehrtagesfahrt == null) fahrtname += " ##"+d.get(Fahrtenbuch.DATUM)+"##"+d.get(Fahrtenbuch.BOOTSKM)+"##";

        // vorhandene Fahrt suchen bzw. neue erstellen
        if (true) { // nur, damit die Variable kmwett einen lokalen Gültigkeitsbereich erhält
        KmWettInfo kmwett = null;
        if ( (ges = (HashEl)h.get(fahrtname)) != null) kmwett = ges.kmwett;
        if (kmwett == null) {
          kmwett = new KmWettInfo(); // nur zur Sicherheit, sollte eigentlich nicht passieren!
          if (mehrtagesfahrt != null) { // gefundene Mehrtagesfahrt
            kmwett.drvWafaStat_gesTage = mehrtagesfahrt.rudertage;
            kmwett.drvWafaStat_gewaesser = mehrtagesfahrt.gewaesser;
          } else { // keine Mehrtagestour (oder nicht gefunden)
            kmwett.drvWafaStat_gesTage = 1;
            DatenFelder zdf = Daten.fahrtenbuch.getDaten().ziele.getExactComplete(d.get(Fahrtenbuch.ZIEL));
            if (zdf != null && zdf.get(Ziele.GEWAESSER).length()>0) kmwett.drvWafaStat_gewaesser = zdf.get(Ziele.GEWAESSER);
            else kmwett.drvWafaStat_gewaesser = "";
          }
        }

        // Daten der Fahrt füllen
        Integer integer;

        String etappenName = null;
        if (mehrtagesfahrt == null || mehrtagesfahrt.isEtappen) {
          // Fahrt in Form von Etappen: Etappenname ist eingetragenes Ziel
          etappenName = d.get(Fahrtenbuch.ZIEL);
        } else {
          // Fahrt als ein einziger Eintrag: "Etappenname" ist Fahrtart (Name der MTour)
          etappenName = mehrtagesfahrt.name; //fahrtname;
        }

        // keine Teilnehmer berücksichtigen, die jünger als 13 Jahre sind
        // seit 2007 entfällt diese Beschränkung: Es werden jetzt alle Teilnehmer gewertet, die
        // Wanderfahrten gemäß der Regel "30Km 1tägig / 40Km mehrtägig" absolviert haben, unabhängig
        // ihres Alters
        // Daher ENTFÄLLT (seit 1.8.1_06):
        // if (alter < 13) {
        //   return;
        // }

        // Kilometer dieser Etappe (Etappe kann auch gesamte Fahrt sein)
        integer = (Integer)kmwett.drvWafaStat_etappen.get(etappenName);
        if (integer == null) {
          kmwett.drvWafaStat_etappen.put(etappenName,new Integer(km));
        } else {
          if (integer.intValue() != km) {
            // Dies kann vorkommen, wenn eine Etappe aufgespalten wurde:
            // Zwei Boote rudern eine Ruderfahrt
            // Boot 1 rudert Etappe A  80 Km
            // Boot 2 rudert Etappe A1 50 Km und wechselt dann
            // Boot 2 rudert Etappe A2 30 Km nach dem Wechsel

            // immer den größeren Km-Wert verwenden (falls eine Etappe wegen Landdienstwechsel aufgeteilt wurde)
            int oldvalue = integer.intValue();
            if (km > integer.intValue()) {
              integer = new Integer(km);
              kmwett.drvWafaStat_etappen.put(etappenName,new Integer(km));
            }

            // @i18n only Germany, no need to translate
            String newWarn = "Etappe '"+fahrtname+": "+etappenName+"' kommt mit unterschiedlichen Entfernungen ("+
                             EfaUtil.zehntelInt2String(oldvalue)+" und "+EfaUtil.zehntelInt2String(km)+
                             " Km) vor (Wert '"+EfaUtil.zehntelInt2String(integer.intValue())+" Km' wird verwendet)!";
            if (warnungen.indexOf(newWarn)<0) warnungen += newWarn+"\n"; // nur neue Warnungen hinzufügen
          }
        }

        // Berechnung der Mannschaftskilometer
        kmwett.drvWafaStat_mannschKm += km;

        // Kilometer für einzelne Altersgruppen hinzufügen
        if (alter>18) { // über 18 Jahre
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
            integer = (Integer)kmwett.drvWafaStat_teilnMueber18.get(name);
            if (integer == null) kmwett.drvWafaStat_teilnMueber18.put(name,new Integer(km));
            else kmwett.drvWafaStat_teilnMueber18.put(name,new Integer(integer.intValue()+km));
          } else { // weiblich
            integer = (Integer)kmwett.drvWafaStat_teilnFueber18.get(name);
            if (integer == null) kmwett.drvWafaStat_teilnFueber18.put(name,new Integer(km));
            else kmwett.drvWafaStat_teilnFueber18.put(name,new Integer(integer.intValue()+km));
          }
        } else { // bis 18 Jahre
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
            integer = (Integer)kmwett.drvWafaStat_teilnMbis18.get(name);
            if (integer == null) kmwett.drvWafaStat_teilnMbis18.put(name,new Integer(km));
            else kmwett.drvWafaStat_teilnMbis18.put(name,new Integer(integer.intValue()+km));
          } else { // weiblich
            integer = (Integer)kmwett.drvWafaStat_teilnFbis18.get(name);
            if (integer == null) kmwett.drvWafaStat_teilnFbis18.put(name,new Integer(km));
            else kmwett.drvWafaStat_teilnFbis18.put(name,new Integer(integer.intValue()+km));
          }
        }

        // Hinzufügen der Daten
        h.put(fahrtname,new HashEl("","","",0,0,0,0,0,null,null,null,kmwett));
        } // end if(true)

        break;
      case StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT:
      case StatistikDaten.WETT_LRVBRB_FAHRTENWETT:
        // Gigboot-Kilometer
        int gigbootkm = 0;
        if (Daten.fahrtenbuch.getDaten().boote != null) {
          DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT));
          if (b != null && EfaTypes.isGigBoot(b.get(Boote.ART))) gigbootkm += rudKm+stmKm;
        }

        String[] gigfahrtBRB = null;
        if (Daten.fahrtenbuch.getDaten().boote != null) {
          DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT));
          if (b != null && EfaTypes.isGigBoot(b.get(Boote.ART)) &&
              rudKm+stmKm >= 200) {
            gigfahrtBRB = new String[6];
            gigfahrtBRB[0] = d.get(Fahrtenbuch.LFDNR);
            gigfahrtBRB[1] = d.get(Fahrtenbuch.DATUM);
            gigfahrtBRB[2] = d.get(Fahrtenbuch.BOOT);
            gigfahrtBRB[3] = d.get(Fahrtenbuch.ZIEL);
            gigfahrtBRB[4] = d.get(Fahrtenbuch.BOOTSKM);
            gigfahrtBRB[5] = d.get(Fahrtenbuch.BEMERK);
          }
        }

        if ( (ges = (HashEl)h.get(name)) == null) {
          KmWettInfo kmwett = new KmWettInfo();
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) kmwett.geschlecht = 0;
          else kmwett.geschlecht = 1;
          kmwett.behinderung = behinderung;

// @gehtnichtmehrinefa2           wettAddWafa(kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                   Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          kmwett.gigbootkm += gigbootkm;
          if (gigfahrtBRB != null) kmwett.gigfahrten.add(gigfahrtBRB);

          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,null,null,null,kmwett));
        } else {
// @gehtnichtmehrinefa2           wettAddWafa(ges.kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                   Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          ges.kmwett.gigbootkm += gigbootkm;
          if (gigfahrtBRB != null) ges.kmwett.gigfahrten.add(gigfahrtBRB);

          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, mannschKm,0,eins*anzRuderTage+ges.anz, null,null,null,ges.kmwett));
        }
        break;
      case StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT:
        // Gigboot-fAHRTEN
        int gigbootkm2 = 0;
        int gigbootanz = 0;
        int gigboot20plus = 0;
        int gigboot30plus = 0;
        String[] gigfahrt = null;
        boolean isGigFahrt = false;
        if (Daten.fahrtenbuch.getDaten().boote != null) {
          DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(d.get(Fahrtenbuch.BOOT));
          if (b != null && Daten.efaTypes.isGigBoot(b.get(Boote.ART))) {
            isGigFahrt = true;
          }
        }
        if (isGigFahrt || sd.wettJahr >= LRVMVP_NEU) {
          gigbootkm2 += rudKm+stmKm;
          gigbootanz++;
          if (gigbootkm2 >= 300) {
            gigboot30plus++;
          } else if (gigbootkm2 >= 200) {
            gigboot20plus++;
          }
          gigfahrt = new String[6];
          gigfahrt[0] = d.get(Fahrtenbuch.LFDNR);
          gigfahrt[1] = d.get(Fahrtenbuch.DATUM);
          gigfahrt[2] = d.get(Fahrtenbuch.BOOT);
          gigfahrt[3] = d.get(Fahrtenbuch.ZIEL);
          gigfahrt[4] = d.get(Fahrtenbuch.BOOTSKM);
          gigfahrt[5] = d.get(Fahrtenbuch.BEMERK);
        }

        if ( (ges = (HashEl)h.get(name)) == null) {
          KmWettInfo kmwett = new KmWettInfo();
          if (geschlecht.equals(EfaTypes.TYPE_GENDER_MALE)) kmwett.geschlecht = 0;
          else kmwett.geschlecht = 1;
          kmwett.behinderung = behinderung;

// @gehtnichtmehrinefa2           wettAddWafa(kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                   Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          kmwett.gigbootkm += gigbootkm2;
          kmwett.gigbootanz += gigbootanz;
          kmwett.gigboot20plus += gigboot20plus;
          kmwett.gigboot30plus += gigboot30plus;
          if (gigfahrt != null) kmwett.gigfahrten.add(gigfahrt);

          h.put(name,new HashEl(jahrgang,status,"",rudKm,stmKm,mannschKm,0,eins*anzRuderTage,null,null,null,kmwett));
        } else {
// @gehtnichtmehrinefa2           wettAddWafa(ges.kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                   Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          ges.kmwett.gigbootkm += gigbootkm2;
          ges.kmwett.gigbootanz += gigbootanz;
          ges.kmwett.gigboot20plus += gigboot20plus;
          ges.kmwett.gigboot30plus += gigboot30plus;
          if (gigfahrt != null) ges.kmwett.gigfahrten.add(gigfahrt);

          h.put(name,new HashEl(jahrgang,status,"",rudKm + ges.rudKm, stmKm + ges.stmKm, mannschKm,0,eins*anzRuderTage+ges.anz, null,null,null,ges.kmwett));
        }
        break;
      default: // Mitglieder, Mitruderer, BlauerWimpel etc.
        if (sd.art == StatistikDaten.WETT_LRVBWIMPEL) {
          if ( (rudKm + stmKm) % 5 > 0) {
            rudKm += 5 - (rudKm + stmKm) % 5; // auf .5 oder .0 aufrunden
          }
        }

        ges = (HashEl)h.get(name);

        if (true) { // nur, damit die Variable kmwett einen lokalen Gültigkeitsbereich erhält
          KmWettInfo kmwett = null;
          if (sd.art == StatistikDaten.ART_MITGLIEDER && sd.ausgebenWafaKm) {
            if (ges == null || ges.kmwett == null) kmwett = new KmWettInfo();
            else kmwett = ges.kmwett;
// @gehtnichtmehrinefa2             wettAddWafa(kmwett,d.get(Fahrtenbuch.LFDNR),d.get(Fahrtenbuch.DATUM),d.get(Fahrtenbuch.ZIEL),d.get(Fahrtenbuch.BOOTSKM),d.get(Fahrtenbuch.BEMERK),rudKm+stmKm,
// @gehtnichtmehrinefa2                     Fahrtenbuch.getMehrtagesfahrtName(d.get(Fahrtenbuch.FAHRTART)),mehrtagesfahrt);
          }

          if (ges == null) h.put(name,new HashEl(jahrgang,status,bezeichnung,rudKm,stmKm,mannschKm,dauer,eins*anzRuderTage,new ZielfahrtFolge(zf),null,null,kmwett));
          else h.put(name,new HashEl(jahrgang,status,bezeichnung,rudKm + ges.rudKm, stmKm + ges.stmKm, ges.mannschKm + mannschKm, ges.dauer + dauer,eins*anzRuderTage + ges.anz, ges.zf.addZielfahrten(zf),null,null,kmwett));
        }
    }
  }





  // Einen Namen in "Gäste", "<Vereinsname>" oder "andere" umwandeln, falls zutreffend
  static String gastAndereName(StatistikDaten sd, String name, String status) {
    if (!sd.gasteAlsEinePerson && !sd.gaesteVereinsweise) return name;

    if (status == null)
      if (Daten.fahrtenbuch.getDaten().mitglieder.getExact(name) != null) {
        DatenFelder pp = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getComplete();
        if (pp != null) status = pp.get(Mitglieder.STATUS);
      } else return ANDEREBEZ;
    if (status == null) return ANDEREBEZ;

    if (sd.gaesteVereinsweise && status != null && status.equals(Daten.fahrtenbuch.getDaten().status[Daten.fahrtenbuch.getDaten().status.length-2]))
      return EfaUtil.getVerein(name);
    if (status.equals(Daten.fahrtenbuch.getDaten().status[Daten.fahrtenbuch.getDaten().status.length-2]))
      return GASTBEZ;
    if (status.equals(Daten.fahrtenbuch.getDaten().status[Daten.fahrtenbuch.getDaten().status.length-1]))
      return ANDEREBEZ;

    return name;
  }






  // Zielfahrt für Auswertung der Kilometerwettbewerbe zum Array hinzufügen
  // @return true, wenn Zielfahrt erfolgreich hinzugefügt wurde
  static boolean wettAddZf(KmWettInfo kmwett, String datum, String ziel, String km, String zf) {
    if (kmwett == null) return false;
    if (datum == null || km == null || EfaUtil.zehntelString2Int(km)<200 || zf == null) return false;
    kmwett.zielfahrten.add(new Zielfahrt(datum,ziel,km,zf));
    return true;
  }





  // Winterfahrt für Auswertung der Kilometerwettbewerbe zum Array hinzufügen
  static void wettAddWinter(KmWettInfo kmwett, String datum, String ziel, String km, Mehrtagesfahrt mtour, StatistikDaten sd) {
    if (kmwett == null) return;
    TMJ tmj = EfaUtil.string2date(datum,0,0,0);;

    int _km = EfaUtil.string2date(km,0,0,0).tag*10 + EfaUtil.string2date(km,0,0,0).monat;

      int monat = (tmj.monat+1) % 12;; // akt Monat (relative Reihenfolge) ermitteln
      if (monat>=kmwett.winterfahrten.length) return;

      if (kmwett.winterfahrten[monat][0][0] == null) kmwett.anzMonate++; // Anzahl der gefundenen (verschiedenen) Monate

      // nicht zwei Fahrten an demselben Tag erlauben!
      boolean doppelt=false;
      for (int i=0; i<kmwett.winterfahrten[monat].length; i++) {
        if (kmwett.winterfahrten[monat][i][0] != null) {
          TMJ d2 = EfaUtil.string2date(kmwett.winterfahrten[monat][i][0],0,0,0);
          if (tmj.tag == d2.tag && tmj.monat == d2.monat && tmj.jahr == d2.jahr) doppelt = true;
        }
      }

      if (!doppelt)
        for (int i=0; i<kmwett.winterfahrten[monat].length; i++) {
          if (kmwett.winterfahrten[monat][i][0] == null) {
            kmwett.winterAnz++;
            kmwett.winterfahrten[monat][i][0] = tmj.tag+"."+tmj.monat+"."+tmj.jahr;
            kmwett.winterfahrten[monat][i][1] = ziel;
            kmwett.winterfahrten[monat][i][2] = EfaUtil.zehntelInt2String(_km);
            break;
          }
        }
  }



/*
  static boolean mayBeWafa(String mTour, int bootskm) {
    if (mTour == null) return false;
    if (mTour.equals(EfaTypes.TYPE_SESSION_TRAINING) ||
        mTour.equals(EfaTypes.TYPE_SESSION_TRAININGCAMP) ||
        mTour.equals(EfaTypes.TYPE_SESSION_LATEENTRY) ||
        mTour.equals(EfaTypes.TYPE_SESSION_REGATTA) ||
        mTour.equals(EfaTypes.TYPE_SESSION_JUMREGATTA)) return false; // Trainings-Fahrten und Regatten zählen nicht
    if (Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_SESSION, mTour)) mTour=""; // vordefinierte Fahrtarten sind keine Mehrtagesfahrten
    if (mTour.equals("") && bootskm < Daten.WAFAKM) return false; // Eintagestour
    return true; // könnte eine Mehrtagesfahrt sein (bei Mehrtagesfahrten aber nur ab 40 Km!)
  }


  // Wanderfahrt für DRV-Wettbewerb hinzufügen
  static void wettAddWafa(KmWettInfo kmwett, String lfdnr, String datum, String ziel, String km, String bemerk, int bootskm, String mtourName, Mehrtagesfahrt mtour) {
    if (kmwett == null) return;
    boolean jum = mtourName.equals(EfaTypes.TYPE_SESSION_JUMREGATTA);

    if (!mayBeWafa(mtourName,bootskm) && !jum) return;

    DRVFahrt fahrt;
    if (mtour == null && !jum) { // Eintagestour
      if (bootskm < Daten.WAFAKM) return;
      fahrt = new DRVFahrt();
      fahrt.entryNo = lfdnr;
      fahrt.dateStart = datum;
      fahrt.dateEnd = datum;
      fahrt.destination = ziel;
      fahrt.km = bootskm;
      fahrt.comments = bemerk;
      fahrt.ok = true;
      fahrt.days = 1;
      kmwett.wafa.put(lfdnr+datum,fahrt); // bei Eintagestouren dient "lfdnr+datum" als Key
    } else { // Mehrtagestour
      if ( (fahrt = (DRVFahrt)kmwett.wafa.get(mtourName)) == null) { // neue MTour
        fahrt = new DRVFahrt();
        fahrt.entryNo = lfdnr;
        fahrt.dateStart = (mtour != null && !mtour.isEtappen ? mtour.start : datum);
        fahrt.dateEnd =  (mtour != null && !mtour.isEtappen ? mtour.ende : datum);
        fahrt.destination = mtourName;
        fahrt.km = bootskm;
        fahrt.comments = bemerk;

        // *** ACHTUNG ***
        // In diesem Abschnitt müßten Änderungen vorgenommen werden, wenn die Implementierung
        // dahingehend geändert werden soll, daß einem Teilnehmer an einer Mehrtagesfahrt immer
        // alle Tage der Mehrtagesfahrt als Rudertage angerechnet werden sollen. Dies ist zwar
        // in bestimmten Fällen (Trainingslager) die Ansicht des DRV, verträgt sich aber nicht
        // mit anderen Szenarien und wurde daher bislang nicht realisiert.
        // Folgende Zeilen sind der Original-Code:
        if (mtour != null && !mtour.isEtappen) {
          if (mtour.rudertage > 0) fahrt.days = mtour.rudertage;
          else fahrt.days = EfaUtil.getDateDiff(fahrt.dateStart,fahrt.dateEnd);
        } else fahrt.days = 1; // erst 1 Tag der Mehrtagesfahrt gerudert
        // Folgende Zeilen entpsrächen der DRV-Implementierung:
        // if (mtour != null) fahrt.anzTage = mtour.rudertage;
        // else fahrt.anzTage = 1;
        // *** ACHTUNG ***

        if (fahrt.km>=400) fahrt.ok = true;
        else fahrt.ok = false;
        fahrt.jum = false;

        if (jum) {
          fahrt.destination = ziel + " ("+Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, EfaTypes.TYPE_SESSION_JUMREGATTA)+")";
          fahrt.days = 1;
          fahrt.ok = false; // denn: ok wird als gültige Mehrtagesfahrt gewertet...
          fahrt.jum = true;
          mtourName = lfdnr+fahrt.destination; // Key bei JuM-Regatten
        }

        kmwett.wafa.put(mtourName,fahrt); // bei MTour dient "mtourName" als Key
      } else { // vorhandene MTour

        // *** ACHTUNG ***
        // In diesem Abschnitt müßten Änderungen vorgenommen werden, wenn die Implementierung
        // dahingehend geändert werden soll, daß einem Teilnehmer an einer Mehrtagesfahrt immer
        // alle Tage der Mehrtagesfahrt als Rudertage angerechnet werden sollen. Dies ist zwar
        // in bestimmten Fällen (Trainingslager) die Ansicht des DRV, verträgt sich aber nicht
        // mit anderen Szenarien und wurde daher bislang nicht realisiert.
        // Folgende Zeilen sind der Original-Code und müßten für den DRV entfernt werden:
        if (!fahrt.dateEnd.equals(datum)) fahrt.days++; // 1 weiterer Tag der Mehrtagesfahrt gerudert
        if (EfaUtil.getRealDateDiff(EfaUtil.string2date(fahrt.dateEnd,0,0,0),EfaUtil.string2date(datum,0,0,0)) > 0)
          fahrt.dateEnd = datum;
        if (EfaUtil.getRealDateDiff(EfaUtil.string2date(fahrt.dateStart,0,0,0),EfaUtil.string2date(datum,0,0,0)) < 0)
          fahrt.dateStart = datum; // Bugfix: War nötig, weil die Einträge einer Fahrt nicht immer in chronologischer Reihenfolge vorliegen müssen
        // *** ACHTUNG ***

        fahrt.km += bootskm;

        // wenn ein Ruderer an einer Mehrtagesfahrt (als einzelne Etappen eingetragen) nur einen Tag
        // mitgerudert ist, werden nur 30 Km gefordert ==> Dieser Fall wird in ausgabeKmWettDRV() behandelt
        if (fahrt.km>=400) fahrt.ok = true;

        kmwett.wafa.put(mtourName,fahrt); // bei MTour dient "mtourName" als Key
      }
    }
  }
*/

// ==============================================================================================






  // Einträge berechnen
  static void berechnung(StatistikDaten sd, Hashtable h) {
    int count = 0;
    int fbSize = Daten.fahrtenbuch.countElements()+1;
    if (sd.auchNullWerte) fillNullWerte(sd,h);
    DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    String s;
    int progressBefore = progressCurrent;
    if (d != null) do {
      progressCurrent = progressBefore + (( ++count * progressInc ) / fbSize);
      if (progressCurrent > 999) progressCurrent=999;

      if (sd.stat == StatistikDaten.STAT_MITGLIEDER || sd.stat == StatistikDaten.STAT_WETT) {
        if ( (s = d.get(Fahrtenbuch.STM)).length()>0 || sd.art == StatistikDaten.ART_FAHRTENBUCH) calc(s,true,d,h,sd,false,null,-1);
        for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
          if ( (s = d.get(i)).length()>0 ) calc(s,false,d,h,sd,false,null,-1);
      } else {
        s = d.get(Fahrtenbuch.BOOT);
        if (s.length()==0) {
          s = "<" + International.getString("unbekanntes Boot") + ">";
        }
        calc(s,false,d,h,sd,false,null,-1);
      }

    } while ( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null && !abort);
  }






  // Berechnungsdaten sortieren, Maxima berechnen etc.
  static ArrEl[][] aufbereiten(StatistikDaten sd, Hashtable[] h) {
    ArrEl[][] a = new ArrEl[h.length][];

    // Hash in Array umwandeln
    for (int ih=0; ih<a.length; ih++) {
      if (h[ih] == null) continue;
      Object[] keys = h[ih].keySet().toArray();
      a[ih] = new ArrEl[h[ih].size()];
      for (int i=0; i<keys.length; i++) {
        HashEl ges = (HashEl)h[ih].get((String)keys[i]);
        a[ih][i] = new ArrEl((String)keys[i],ges.jahrgang,ges.status,ges.bezeichnung,ges.rudKm,ges.stmKm,ges.mannschKm,ges.dauer,ges.anz,ges.zf,ges.ww,ges.fahrtenbuch,ges.kmwett);
        if (a[ih][i].zf != null) a[ih][i].zf.reduceToMinimun();
      }
    }

    // alleWW-Hash in Array umwandeln
    if (sd.art == sd.ART_WERMITWEM || sd.art == sd.ART_WERWOHIN || sd.art == sd.ART_WERMITBOOTSART ||
        sd.art == sd.ART_WERMITFAHRTART || sd.art == sd.ART_WERUNERLAUBT || sd.art == sd.BART_WELCHESWOHIN) {
      Object[] keys2 = alleWW.keySet().toArray();
      alleWWArr = new AlleWWArrEl[alleWW.size()];
      for (int i=0; i<keys2.length; i++) {
        alleWWArr[i] = new AlleWWArrEl((String)keys2[i]);
      }
      AlleWWArrEl.sortVorNachname = (sd.art == sd.ART_WERMITWEM);
      Arrays.sort(alleWWArr,0,alleWW.size());
    }

    // Ergebnisse sortieren (nur a[0})
    ArrEl.kmTypSortKrit = ArrEl.KM_TYP_ALL;
    if (sd.art != StatistikDaten.ART_FAHRTENBUCH && sd.art != StatistikDaten.BART_FAHRTENBUCH && sd.art != StatistikDaten.ART_MONATSUEBERSICHT) {

      if (sd.art == StatistikDaten.ART_MITGLIEDER || sd.art == StatistikDaten.ART_MITRUDERER ||
          sd.art == StatistikDaten.ART_WERMITWEM || sd.art == StatistikDaten.ART_WERWOHIN ||
          sd.art == StatistikDaten.ART_WERMITBOOTSART || sd.art == StatistikDaten.ART_WERMITFAHRTART ||
          sd.art == StatistikDaten.ART_WERUNERLAUBT ||
          sd.art == StatistikDaten.BART_RUDERER) {
        ArrEl.sortVorNachname = sd.sortVorNachname;
        ArrEl.sortierFolge = StatistikDaten.SORTFOLGE_AUF;
        ArrEl.sortierKriterium = StatistikDaten.SORTKRIT_VORNAME;
        if (!sd.ausgebenKm && !sd.graphischKm) {
          if ((sd.ausgebenRudKm || sd.graphischRudKm) && (!sd.ausgebenStmKm && !sd.graphischStmKm)) ArrEl.kmTypSortKrit = ArrEl.KM_TYP_RUD;
          if ((sd.ausgebenStmKm || sd.graphischStmKm) && (!sd.ausgebenRudKm && !sd.graphischRudKm)) ArrEl.kmTypSortKrit = ArrEl.KM_TYP_STM;
        }
        ArrEl.gaesteHinten = sd.gasteAlsEinePerson;
        Arrays.sort(a[0],0,h[0].size());
        ArrEl.sortierKriterium = StatistikDaten.SORTKRIT_NACHNAME;
        Arrays.sort(a[0],0,h[0].size());
      }

      if (sd.sortierKriterium == StatistikDaten.SORTKRIT_STATUS) {
        ArrEl.sortierFolge = StatistikDaten.SORTFOLGE_AB;
        ArrEl.sortierKriterium = StatistikDaten.SORTKRIT_KM;
        Arrays.sort(a[0],0,h[0].size());
      }

      ArrEl.sortierKriterium = sd.sortierKriterium;
      ArrEl.sortierFolge = sd.sortierFolge;
    } else { // Art: Fahrtenbuch oder Monatsübersicht
//      ArrEl.sortierKriterium = StatistikDaten.SORTKRIT_JAHRGANG; // schmutzig! Es wird nach LfdNr_Key sortiert! ;-)
//      ArrEl.sortierFolge = StatistikDaten.SORTFOLGE_AUF;
      ArrEl.sortVorNachname = false;
      ArrEl.sortierKriterium = sd.sortierKriterium;
      ArrEl.sortierFolge = sd.sortierFolge;
      ArrEl.gaesteHinten = false;
    }
    Arrays.sort(a[0],0,h[0].size());

    // Maximalwerte berechnen (für graphische Ausgabe, nur a[0])
    for (int i=0; i<a[0].length; i++) {
      if (sd.zusammengefassteDatenOhneBalken && a[0][i].status != null &&
          (a[0][i].status.equals(GAST) || a[0][i].status.equals(ANDERE)) ) continue;
      if (Math.abs(a[0][i].rudKm + a[0][i].stmKm) > sd.maxKm) sd.maxKm = Math.abs(a[0][i].rudKm + a[0][i].stmKm);
      if (Math.abs(a[0][i].rudKm) > sd.maxRudKm) sd.maxRudKm = Math.abs(a[0][i].rudKm);
      if (Math.abs(a[0][i].stmKm) > sd.maxStmKm) sd.maxStmKm = Math.abs(a[0][i].stmKm);
      if (Math.abs(a[0][i].anz) > sd.maxFahrten) sd.maxFahrten = Math.abs(a[0][i].anz);
      if (Math.abs(EfaUtil.div(a[0][i].rudKm + a[0][i].stmKm,a[0][i].anz)) > sd.maxKmFahrt) sd.maxKmFahrt = Math.abs(EfaUtil.div(a[0][i].rudKm + a[0][i].stmKm,a[0][i].anz));
      if (Math.abs(a[0][i].dauer / 60) > sd.maxDauer) sd.maxDauer = Math.abs(a[0][i].dauer / 60);
      if (Math.abs(EfaUtil.div((a[0][i].rudKm + a[0][i].stmKm)*6,a[0][i].dauer)) > sd.maxKmH) sd.maxKmH = Math.abs(EfaUtil.div((a[0][i].rudKm + a[0][i].stmKm)*6,a[0][i].dauer));
    }

    return a;
  }



  static AusgabeEintrag sucheNamenInZusatzWett(String name, AusgabeDaten ad, int wett) {
    if (ad == null //  || name == null
    ) return null;
    if (ad.wett_teilnehmerInGruppe == null) return null;
    for (int gi=0; gi<ad.wett_teilnehmerInGruppe.length; gi++) {
      AusgabeEintrag ae;
      for (ae = ad.wett_teilnehmerInGruppe[gi]; ae != null; ae = ae.next) {
        if (name.equals(ae.w_name)) {
          return ae;
        }
      }
    }
    return null;
  }


  // Zusatz-Wettbewerbsausgabe aus ad[1] .. ad[3] zu ad[0] hinzufügen
  static void addZusatzWettToOutput(AusgabeDaten[] ad, StatistikDaten sd) {
    boolean zusatz = false;
    for (int i=0; i<sd.zusatzWett.length; i++) if (sd.zusatzWett[i]>=200) zusatz = true;
    if (!zusatz) return;

    // Anzahl der erfüllten Teilnehmer pro Zusatzwettbewerb ermitteln
    int[] ok = new int[sd.zusatzWett.length];
    for (int i=0; i<ok.length; i++) ok[i]=0;

    // Alle Einträge in Haupt-Daten durchgehen und passende Wettbewerbseinträge dazu suchen
    AusgabeEintrag ae;
    for (ae = ad[0].ae; ae != null; ae = ae.next)
      for (int i=0; i<sd.zusatzWett.length; i++) {
        AusgabeEintrag w = sucheNamenInZusatzWett(ae.name,ad[i+1],sd.zusatzWett[i]-200);
        String s = "";
        if (w != null) {
          if (w.w_erfuellt) s = w.w_kilometer+" (ok)";
          else if (w.w_additional != null) s = w.w_kilometer+" ("+w.w_additional+")";
          else s = w.w_kilometer;
          if (w.w_erfuellt) ok[i]++;
        }
        if (ae.zusammenfassung) s = International.getString("erfüllt")+": "+ok[i];
        if (sd.zusatzWett[i]-200 == WettDefs.DRV_FAHRTENABZEICHEN)      ae.zusatzDRV = s;
        if (sd.zusatzWett[i]-200 == WettDefs.LRVBERLIN_SOMMER)          ae.zusatzLRVBSommer = s;
        if (sd.zusatzWett[i]-200 == WettDefs.LRVBERLIN_WINTER)          ae.zusatzLRVBWinter = s;
        if (sd.zusatzWett[i]-200 == WettDefs.LRVBRB_WANDERRUDERWETT)    ae.zusatzLRVBrbWanderWett = s;
        if (sd.zusatzWett[i]-200 == WettDefs.LRVBRB_FAHRTENWETT)        ae.zusatzLRVBrbFahrtenWett = s;
        if (sd.zusatzWett[i]-200 == WettDefs.LRVMVP_WANDERRUDERWETT)    ae.zusatzLRVMVpWanderWett = s;
      }
  }




  // Alle Variablen zurücksetzen (sicherheitshalber)
  static void clearAllVars() {
    alleWW=null;
    alleWWArr=null;
    alleAktive=null;
    lastLfdNr=null;
    nichtBeruecksichtigt=null;
    warnungen=null;
    efaWett=null;
    progressDone=0;
    progressCurrent=0;
    progressLength=1;
    progressInc=1000;
    progressMessage="";
    abort=false;
    AusgabeDaten ad=null;
    letzterAusgabeEintrag=null;
  }


  // Kilometerstatistik erstellen
  public static void create(StatistikDaten[] sd) {
    if (isCreateRunning) return;
    isCreateRunning = true;

    if (GASTBEZ == null) {
        GASTBEZ = Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST);
    }
    if (ANDEREBEZ == null) {
        ANDEREBEZ = Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_OTHER);
    }

    for (int i=0; i<sd.length; i++) {
      if (sd[i].art >= 200) {
        if (Daten.wettDefs == null) {
          Dialog.error(International.getString("Keine Wettbewerbsdefinitionen gefunden!"));
          return;
        }
        if (Daten.wettDefs.getWettDef(sd[i].art-200,sd[i].wettJahr) == null) {
          Dialog.error(International.getMessage("Keine Wettbewerbsdefinition für Wettbewerbsjahr {year} gefunden!",sd[i].wettJahr));
          return;
        }
      }
      if (sd[i].zusatzWett != null) {
        for (int j=0; j<sd[i].zusatzWett.length; j++) {
          if (sd[i].zusatzWett[j] >= 200 && Daten.wettDefs == null) {
            Dialog.error(International.getString("Keine Wettbewerbsdefinitionen gefunden!"));
            return;
          }
          if (sd[i].zusatzWett[j] >= 200 && Daten.wettDefs.getWettDef(sd[i].zusatzWett[j]-200,sd[i].zusatzWettjahr[j]) == null) {
            Dialog.error(International.getMessage("Keine Wettbewerbsdefinition für Wettbewerbsjahr {year} gefunden!",sd[i].zusatzWettjahr[j]));
            return;
          }
        }
      }
    }

    clearAllVars();

    progressLength = 1000 * sd.length;


    for (int i=0; i<sd.length; i++) {
      progressMessage = International.getString("Vorbereiten ...");
      progressCurrent = 1;

      // temporäre Datei erstellen?
      if (sd[i].ausgabeArt == StatistikDaten.AUSGABE_BROWSER ||
          sd[i].ausgabeArt == StatistikDaten.AUSGABE_INTERN_GRAFIK) {
        sd[i].ausgabeDatei = Daten.efaTmpDirectory+"browser"+i+".html"; // Tmp-Browser Datei
        sd[i].ausgabeOverwriteWarnung = false;
      }
      if (sd[i].ausgabeArt == StatistikDaten.AUSGABE_PDF) {
        sd[i].ausgabeDateiTmp = Daten.efaTmpDirectory+"pdf"+i+".fo"; // Tmp-XSL-FO Datei
      }
      if ( (sd[i].ausgabeArtPrimaer == StatistikDaten.AUSGABE_HTML || (sd[i].ausgabeArt == StatistikDaten.AUSGABE_HTML && sd[i].stylesheet != null) )
           && sd[i].tabelleHTML) {
        sd[i].ausgabeDateiTmp = Daten.efaTmpDirectory+"html"+i+".html"; // Tmp-Datei für Tabelle ersetzen
        sd[i].ausgabeOverwriteWarnung = false;
      }

      // FTP-Upload
        if (sd[i].ausgabeDatei != null && sd[i].ausgabeDatei.startsWith("ftp://")) {
          String s = sd[i].ausgabeDatei.substring(6,sd[i].ausgabeDatei.length());
          int pos;
          pos = s.indexOf(":"); // Username
          if (pos>0) {
            sd[i].ftpUser = s.substring(0,pos);
            s = s.substring(pos+1,s.length());
          } else {
            progressCurrent = progressLength; // damit Progressbar den Dialog nicht verdeckt
            Dialog.error(International.getString("Kein Nutzername für FTP-Upload angegeben!") +
                    "\n" + International.getString("Format: ftp://nutzername:passwort@mein.server.de/ein/verzeichnis/datei.html"));
            continue;
          }
          pos = s.indexOf("@"); // Password
          if (pos>0) {
            sd[i].ftpPassword = s.substring(0,pos);
            s = s.substring(pos+1,s.length());
          } else {
            progressCurrent = progressLength; // damit Progressbar den Dialog nicht verdeckt
            Dialog.error(International.getString("Kein Passwort für FTP-Upload angegeben!") +
                    "\n" + International.getString("Format: ftp://nutzername:passwort@mein.server.de/ein/verzeichnis/datei.html"));
            continue;
          }
          pos = s.indexOf("/"); // Hostname
          if (pos>0) {
            sd[i].ftpServer = s.substring(0,pos);
            s = s.substring(pos,s.length());
          } else {
            progressCurrent = progressLength; // damit Progressbar den Dialog nicht verdeckt
            Dialog.error(International.getString("Kein Servername für FTP-Upload angegeben!") +
                    "\n" + International.getString("Format: ftp://nutzername:passwort@mein.server.de/ein/verzeichnis/datei.html"));
            continue;
          }
          pos = s.lastIndexOf("/"); // Directory & Filename
          if (pos>0) {
            sd[i].ftpDirectory = s.substring(0,pos);
            sd[i].ftpFilename = s.substring(pos+1,s.length());
          } else {
            sd[i].ftpDirectory = "/";
            sd[i].ftpFilename = s.substring(1,s.length());
          }
          if (sd[i].ftpFilename.length()==0) {
            progressCurrent = progressLength; // damit Progressbar den Dialog nicht verdeckt
            Dialog.error(International.getString("Kein Dateiname für FTP-Upload angegeben!") +
                    "\n" + International.getString("Format: ftp://nutzername:passwort@mein.server.de/ein/verzeichnis/datei.html"));
            continue;
          }
          sd[i].ausgabeDatei = Daten.efaTmpDirectory+sd[i].ftpFilename;
          sd[i].ausgabeOverwriteWarnung = false;
        }

      // vor Überschreiben warnen?
      if (sd[i].ausgabeOverwriteWarnung && sd[i].ausgabeDatei != null && new File(sd[i].ausgabeDatei).isFile()) {
        progressCurrent = progressLength; // Damit ProgressBar den Überschreiben-Dialog nicht verdeckt, sondern kurz verschwindet, da max>cur
        if (sd[i].statistikFrame != null) {
          sd[i].statistikFrame.setEnabled(true);
          // @todo (P5) statistics if (!sd[i].statistikFrame.allowedWriteFile(sd[i])) continue;
          progressCurrent = 1;
        }
      }

      // variablen Namen für "nur Name" eingeben
      if (sd[i].name != null && (sd[i].name.equals("$$") || sd[i].name.equals("$?"))) {
        boolean requirePassword = sd[i].name.equals("$?");
        DatenListe d = null;
        if (Daten.fahrtenbuch != null) {
          if (sd[i].stat == StatistikDaten.STAT_BOOTE) d = Daten.fahrtenbuch.getDaten().boote;
          else if (sd[i].nameOderGruppe == StatistikDaten.NG_NAME) {
            d = Daten.fahrtenbuch.getDaten().mitglieder;
          } else {
            Vector g;
            if (Daten.gruppen != null) g = Daten.gruppen.getGruppen();
            else g = new Vector();
            d = new DatenListe("foo",1,1,false);
            for (int j=0; j<g.size(); j++) {
              DatenFelder df = new DatenFelder(1);
              df.set(0,(String)g.get(j));
              d.add(df);
            }
          }
        }
        int progressBefore = progressCurrent;
        progressCurrent = progressLength; // damit Progressbar den Dialog nicht verdeckt
        String msg = null;
        if (sd[i].stat == StatistikDaten.STAT_BOOTE) {
            msg = International.getString("Bitte gib den Namen des Boots an, für das diese Statistik erstellt werden soll:");
        } else if (sd[i].nameOderGruppe == StatistikDaten.NG_NAME) {
            msg = International.getString("Bitte gib den Namen der Person an, für die diese Statistik erstellt werden soll:");
        } else {
            msg = International.getString("Bitte gib den Namen der Gruppe an, für die diese Statistik erstellt werden soll:");
        }
        sd[i].name = SimpleInputFrame.showInputDialog(International.getString("Namen eingeben"),msg,d,(javax.swing.JDialog)null);
        progressCurrent = progressBefore;
        if (sd[i].name == null) sd[i].name="";

        if (sd[i].name.length() > 0) {
            if (sd[i].stat == StatistikDaten.STAT_BOOTE) {
                sd[i].name = EfaUtil.syn2org(Daten.synBoote,sd[i].name);
            }
            if (sd[i].stat == StatistikDaten.STAT_MITGLIEDER) {
                sd[i].name = EfaUtil.syn2org(Daten.synMitglieder,sd[i].name);
            }
        }

        // ggf. Paßwort abfragen
        if (requirePassword &&
            sd[i].stat != StatistikDaten.STAT_BOOTE && sd[i].nameOderGruppe == StatistikDaten.NG_NAME &&
            sd[i].name.length() > 0 && Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null) {
          DatenFelder dm = Daten.fahrtenbuch.getDaten().mitglieder.getExactComplete(sd[i].name);
          if (dm != null && dm.get(Mitglieder.PASSWORT).length()>0) {
            char[] pwd = EnterPasswordFrame.enterPassword(Dialog.frameCurrent(),
                    International.getMessage("Bitte Paßwort für {name} eingeben",sd[i].name)+ ":");
            if (pwd == null || !(new String(pwd)).equals(dm.get(Mitglieder.PASSWORT))) {
              Dialog.error(International.getString("Ungültiges Paßwort!"));
              sd[i].name = "";
            }
          }
        }
      }

      // alle Felder nur in XML-Ausgabe ausgeben
      if (sd[i].ausgebenXMLalle && sd[i].ausgabeArtPrimaer != StatistikDaten.AUSGABE_XML) sd[i].ausgebenXMLalle=false;

      // bei Vorjahresvergleich die "Durschnittsfelder" nicht ausgeben
      if (sd[i].vorjahresvergleich) {
        sd[i].ausgebenKmFahrt = false;
        sd[i].ausgebenKmH = false;
        sd[i].graphischKmFahrt = false;
        sd[i].graphischKmH = false;

        if (sd[i].von.jahr != 1) { // Jahr angegeben!
          sd[i].von.jahr--;
          sd[i].vonCal = sd[i].von.toCalendar();
        }

        if (sd[i].von.tag == 1 && sd[i].von.monat == 1 && sd[i].von.jahr == 1) { // keine Angabe des Startzeitraums
          sd[i].von = new TMJ(1,1,EfaUtil.correctDate(EfaUtil.correctDate("today"),0,0,0).jahr-1); // 1.1. des Vorjahres
          sd[i].vonCal = sd[i].von.toCalendar();
        }
        if (sd[i].bis.tag == 31 && sd[i].bis.monat == 12 && sd[i].bis.jahr == 9999) { // keine Angabe des Startzeitraums
          sd[i].bis = EfaUtil.correctDate(EfaUtil.correctDate("today"),0,0,0); // heute
          sd[i].bisCal = sd[i].bis.toCalendar();
        }
        if (sd[i].von.jahr+1 != sd[i].bis.jahr) {
          Dialog.error(International.getString("Beginn und Ende der Auswertung müssen im gleichen Jahr liegen!"));
          continue;
        }
        if (sd[i].von.monat > sd[i].bis.monat || (sd[i].von.monat == sd[i].bis.monat && sd[i].von.tag > sd[i].bis.tag)) {
          Dialog.error(International.getString("Tag und Monat des Beginns der Auswertung müssen vor Tag und Monat des Endes liegen!"));
          continue;
        }
      }

      // bei Wettbewerben Motorboote und Ergos ignorieren
      if (sd[i].stat == StatistikDaten.STAT_WETT) {
        for (int fi = 0; fi < sd[i].fahrtart.length; fi++) {
          // @todo (P5) statistics if (EfaTypes.TYPE_SESSION_MOTORBOAT.equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, fi)) ||
          // @todo (P5) statistics     EfaTypes.TYPE_SESSION_ERG.equals(Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, fi)))
          // @todo (P5) statistics   sd[i].fahrtart[fi] = false;
        }
      }



      Fahrtenbuch orgFahrtenbuch = Daten.fahrtenbuch;      // Ursprüngliches FB, falls Berechnung über die FB-Grenzen hinaus

      String s;

      // Hash für Sammeln der Ergebnisse: h[0] - "normale" Ergebnisse; h[1] .. h[3] Zusatzwettbewerbe 1-3
      Hashtable[] h = new Hashtable[1+sd[i].zusatzWett.length];
      h[0] = new Hashtable();

      // Zusatzwettbewerbe berechnen?
      boolean berechneZusatzwett = sd[i].art == StatistikDaten.ART_MITGLIEDER ||
                                   sd[i].art == StatistikDaten.ART_WERMITWEM  ||
                                   sd[i].art == StatistikDaten.ART_WERWOHIN ||
                                   sd[i].art == StatistikDaten.ART_WERMITBOOTSART ||
                                   sd[i].art == StatistikDaten.ART_WERMITFAHRTART ||
                                   sd[i].art == StatistikDaten.ART_WERUNERLAUBT;
      StatistikDaten[] sd_zusatz = new StatistikDaten[sd[i].zusatzWett.length];
      for (int z=0; z<sd[i].zusatzWett.length; z++) {
        if (sd[i].zusatzWett[z]>=200 && berechneZusatzwett) {
          h[z+1] = new Hashtable();
          sd_zusatz[z] = sd[i].cloneSD();
          sd_zusatz[z].fruehesteFahrt = sd_zusatz[z].spaetesteFahrt = null;
          sd_zusatz[z].ersterEintrag = sd_zusatz[z].letzterEintrag = 1;
          sd_zusatz[z].erstesFb = sd_zusatz[z].letztesFb = "";
          sd_zusatz[z].art = sd[i].zusatzWett[z];
          sd_zusatz[z].stat = StatistikDaten.STAT_WETT;
          sd_zusatz[z].wettKurzAusgabe = true;
          sd_zusatz[z].wettJahr = sd[i].zusatzWettjahr[z];
          sd_zusatz[z].wettProz = sd_zusatz[z].wettFahrten = 0;
          sd_zusatz[z].wettOhneDetail = true;
          WettDef w = Daten.wettDefs.getWettDef(sd[i].zusatzWett[z]-200,sd[i].zusatzWettjahr[z]);
          if (w != null) {
            sd_zusatz[z].von = new TMJ(w.von.tag,w.von.monat,w.von.jahr + sd[i].zusatzWettjahr[z]);
            sd_zusatz[z].vonCal = sd_zusatz[z].von.toCalendar();

            sd_zusatz[z].bis = new TMJ(w.bis.tag,w.bis.monat,w.bis.jahr + sd[i].zusatzWettjahr[z]);
            sd_zusatz[z].bisCal = sd_zusatz[z].bis.toCalendar();
            // wenn Berechnung nur bis zu einem bestimmten Enddatum geht, dann auch den Wettbewerb nicht
            // über dieses Datum hinaus berechnen (anders als beim Anfang, der immer "pünktlich" anfängt)
            if (sd[i].bisCal.before(sd_zusatz[z].bisCal)) {
              sd_zusatz[z].bis = new TMJ(sd[i].bis.tag,sd[i].bis.monat,sd[i].bis.jahr);
              sd_zusatz[z].bisCal = sd_zusatz[z].bis.toCalendar();
            }
          }
        } else sd[i].zusatzWett[z] = 0;
      }


      alleWW = new Hashtable(); // alle Ziele für "Wer Wohin"
      alleAktive = new Hashtable(); // alle aktive Ruderer (DRV Wafa-Statistik)
      lastLfdNr = "";
      nichtBeruecksichtigt = new Hashtable();
      warnungen = "";

      // sollen nur bestimmte Fahrtenbücher ausgewertet werden?
      boolean nurBestimmteFb = false;
      int nurBestimmteFbCnt = 0;
      if (sd[i].nurFb != null) {
        for (int j=0; j<sd[i].nurFb.length; j++) {
          if (sd[i].nurFb[j].trim().length()>0) nurBestimmteFb = true;
        }
      }

      // Erstes Fahrtenbuch für die Berechnung ermitteln
      Fahrtenbuch neu;
      Hashtable alleFb = new Hashtable(); // um sicherzustellen, daß keine FB doppelt vorkommen
      int counterToAktFb=0; // um bei Berechnung festzustellen, wann aktuelles Fb wieder erreicht ist
      if (!nurBestimmteFb) {
        alleFb.put(EfaUtil.upcaseFileName(Daten.fahrtenbuch.getFileName()),"");
        boolean cont=false; // um ein continue in der umgebenden for-Schleife auszulösen
        while (sd[i].zeitFbUebergreifend && !(s = EfaUtil.upcaseFileName(Daten.fahrtenbuch.getPrevFb(true))).equals("") ) {
  	  if (EfaUtil.canOpenFile(s)) {
            neu = new Fahrtenbuch(s);
            if (alleFb.get(s) != null) {
              Dialog.error(International.getMessage("Fahrtenbuch {logbook} kommt mehrfach als vorangehendes Fahrtenbuch vor!",s));
              Daten.fahrtenbuch = orgFahrtenbuch;
              cont=true; break;
            }
            if (!EfaUtil.upcaseFileName(neu.getNextFb(true)).equals(EfaUtil.upcaseFileName(Daten.fahrtenbuch.getFileName()))) {
              Dialog.error(International.getMessage("Fahrtenbuch {logbook} verweist nicht auf {filename}!",s,EfaUtil.upcaseFileName(Daten.fahrtenbuch.getFileName())));
              Daten.fahrtenbuch = orgFahrtenbuch;
              cont=true; break;
            }
            alleFb.put(s,"");
            Daten.fahrtenbuch = neu;
            counterToAktFb++; // ein zusätzliches Fb *vor* dem aktuellen mehr
	  } else {
            Dialog.error(LogString.logstring_fileOpenFailed(s, International.getString("Fahrtenbuch")));
            break;
  	  }
        }
        if (cont) continue; // die breaks sollen zu einem continue in der umgebenden for-Schleife führen!
        alleFb = new Hashtable(); // Hashtable wieder löschen
      } else {
        boolean cont=false;
        counterToAktFb = -1; // da es kein "aktuelles Fahrtenbuch" gibt, das ausgewertet werden soll!
        for (int j=0; j<sd[i].nurFb.length; j++) {
          String fbs = null;
          if (sd[i].nurFb[j].trim().length()>0) fbs = sd[i].nurFb[j].trim();
          if (fbs != null) {
            if (alleFb.get(fbs) != null) {
              Dialog.error(International.getMessage("Fahrtenbuch {logbook} kommt mehrfach in der Liste der auszuwertenden Fahrtenbücher vor!",fbs));
              cont=true; break;
            }
            alleFb.put(fbs,"");
            counterToAktFb++;
          }
        }
        if (cont) continue; // die breaks sollen zu einem continue in der umgebenden for-Schleife führen!
      }

      progressCurrent = 1;
      int anzFb = counterToAktFb + 1;
      int anzZusWett = 0;
      for (int j=0; j<sd[i].zusatzWett.length; j++) if (sd[i].zusatzWett[j]>=200) anzZusWett++;

      do { // Schleife über alle zu berechnenden Fahrtenbücher
        if (nurBestimmteFb) {
          while (nurBestimmteFbCnt<sd[i].nurFb.length && sd[i].nurFb[nurBestimmteFbCnt].trim().length()==0) nurBestimmteFbCnt++;
          if (nurBestimmteFbCnt>=sd[i].nurFb.length) {
            break; // letztes Fahrtenbuch erreicht
          }
          String fbs = sd[i].nurFb[nurBestimmteFbCnt];
          if (fbs.indexOf(Daten.fileSep)<0) fbs = EfaUtil.getPathOfFile(orgFahrtenbuch.getFileName()) + Daten.fileSep + fbs;
          Daten.fahrtenbuch = new Fahrtenbuch(fbs);
          if (!EfaUtil.canOpenFile(fbs) || !Daten.fahrtenbuch.readFile()) {
              Dialog.error(LogString.logstring_fileOpenFailed(fbs, International.getString("Fahrtenbuch")) +
                      " " + International.getString("Berechnung abgebrochen!"));
            break;
          }
          nurBestimmteFbCnt++;
        } else {
  	  if (sd[i].zeitFbUebergreifend) {
            if (counterToAktFb == 0) { // aktuelles Fahrtenbuch nicht neu lesen, sondern Kopie im Speicher verwenden!
              Daten.fahrtenbuch = orgFahrtenbuch;
            } else if (!Daten.fahrtenbuch.readFile()) {
              Dialog.error(LogString.logstring_fileOpenFailed(Daten.fahrtenbuch.getFileName(), International.getString("Fahrtenbuch")) +
                      " " + International.getString("Berechnung abgebrochen!"));
              break;
            }
          }
        }
	alleFb.put(EfaUtil.upcaseFileName(Daten.fahrtenbuch.getFileName()),"");

	String fname = Daten.fahrtenbuch.getFileName();
	int spos;
	if ( (spos = fname.lastIndexOf(Daten.fileSep)) >= 0) fname = fname.substring(spos+1,fname.length());
	progressMessage = International.getMessage("Auswertung {logbook} ...",fname);
        progressInc = 1000 / (anzFb > 0 ? anzFb : 1);
        progressInc = progressInc / (anzZusWett+1);

        // -----------------------------------------------------
        // Berechnungsrouting
	berechnung(sd[i],h[0]);
        // -----------------------------------------------------

        // Zusatzwettbewerbe berechnen?
        for (int z=0; z<sd[i].zusatzWett.length; z++) {
          if (sd[i].zusatzWett[z]>=200 && berechneZusatzwett) {
            berechnung(sd_zusatz[z],h[z+1]);
          }
        }


	counterToAktFb--; // wieder runterzählen bis zu aktuellem Fahrtenbuch

        if (nurBestimmteFb) {
          // nothing to do
        } else {
  	  if (sd[i].zeitFbUebergreifend && !(s = EfaUtil.upcaseFileName(Daten.fahrtenbuch.getNextFb(true))).equals("") ) {
            if (counterToAktFb!=0)
              if (EfaUtil.canOpenFile(s)) {
                neu = new Fahrtenbuch(s);
              } else {
                Dialog.error(LogString.logstring_fileOpenFailed(s, International.getString("Fahrtenbuch")) +
                      " " + International.getString("Berechnung abgebrochen!"));
                break;
              }
            else neu=null; // aktFahrtenbuch soll verwendet werden!
            if (alleFb.get(s) != null) {
              Dialog.error(International.getMessage("Fahrtenbuch {logbook} kommt mehrfach in der Liste der auszuwertenden Fahrtenbücher vor!",s) +
                      International.getString("Berechnung abgebrochen!"));
              break;
            }
            alleFb.put(s,"");
            Daten.fahrtenbuch = neu;
	  } else break; // Abbruch für Normalmodus, oder wenn Ende erreicht!
        }
      } while (!abort);
      Daten.fahrtenbuch = orgFahrtenbuch;

      progressCurrent = 0;
      progressDone += 1000;
      if (sd.length == i+1) progressDone = progressLength;
      progressMessage = International.getString("Aufbereiten der Daten ...");
      ArrEl[][] a = aufbereiten(sd[i],h);


      // Berechnung beendet
      if (!abort) {
	// Ergebnis ausgeben
        if (sd[i].ausgabeArt == StatistikDaten.AUSGABE_EFAWETT) initEfaWett(sd[i]);

        // AusgabeDaten für alle a[0] .. a[3] erstellen (normaler Durchlauf und Zusatz-Wettbewerbsausgabe)
        AusgabeDaten[] ad = new AusgabeDaten[a.length];
        for (int ia=0; ia<a.length; ia++) {
          if (a[ia] == null) continue;
          StatistikDaten sd_tmp = ( ia == 0 ? sd[i] : sd_zusatz[ia-1] );
          ad[ia] = createHeaderInformation(sd_tmp,a[ia].length+1,a[ia]);

  	  if (ia == 0 && sd_tmp.zusammenAddieren) ausgabe(ad[ia],sd_tmp,a[ia],zusammenrechnen(sd_tmp,a[ia]));
	  else ausgabe(ad[ia],sd_tmp,a[ia],null);
        }

        addZusatzWettToOutput(ad,sd[i]); // Spalten für Zusatz-Wettbewerbsausgabe zu a[0] hinzufügen

	if (nichtBeruecksichtigt != null && nichtBeruecksichtigt.size()>0 && sd[i].statistikFrame != null) {
          String sn = "";
          Object[] keys = nichtBeruecksichtigt.keySet().toArray();
          for (int in=0; in<keys.length; in++) {
            sn = sn + (sn.length() > 0 ? "\n" : "") + keys[in] + " ("+nichtBeruecksichtigt.get(keys[in])+")";
          }
          Dialog.meldung(International.getString("Folgende Teilnehmer könnten die Bedingungen erfüllt haben, " +
                  "wurden aber bei der Auswertung ignoriert:") + "\n"+sn);
        }
        if (!warnungen.equals(""))
          Dialog.meldung(International.getString("Warnungen")+":\n"+warnungen);

        if (sd.length == i+1) progressCurrent = -2; // Done

        progressMessage = International.getString("Ausgabe der Daten");
        schreibeAusgabe(ad[0],sd[i]);

        progressMessage = International.getString("Statistik erstellt!");
      }
    } // end for

    isCreateRunning = false;
    clearAllVars();
    progressCurrent = -2; // Done
  }





// ==============================================================================================
// ==================== A U S G A B E - R O U T I N E N =========================================
// ==============================================================================================





  static void schreibeAusgabe(AusgabeDaten ad, StatistikDaten sd) {

    // HTML
    if (sd.ausgabeArtPrimaer == StatistikDaten.AUSGABE_HTML)
      if (!schreibeHTML(ad,sd)) return;

    // EFAWETT
    if (sd.ausgabeArtPrimaer == StatistikDaten.AUSGABE_EFAWETT)
      // @todo (P5) statistics if (!sd.abbruchEfaWett) Dialog.statistikFrame.efaWettVervollständigen(efaWett);

    // XML
    if (sd.ausgabeArtPrimaer == StatistikDaten.AUSGABE_XML)
      if (!schreibeXML(ad,sd)) return;

    // CSV
    if (sd.ausgabeArtPrimaer == StatistikDaten.AUSGABE_CSV)
      schreibeCSV(ad,sd);

    // HTML-TABELLE
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_HTML &&
        sd.ausgabeArtPrimaer != StatistikDaten.AUSGABE_HTML &&
        sd.tabelleHTML)
      schreibeHTMLtabelle(sd);

    // HTML-GRAFIKEN
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_HTML ||
        sd.ausgabeArt == StatistikDaten.AUSGABE_INTERN_GRAFIK ||
        sd.ausgabeArt == StatistikDaten.AUSGABE_BROWSER)
      schreibeHTMLgrafiken(sd);

    // INTERN (GRAFIK)
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_INTERN_GRAFIK)
      schreibeInternGrafik(sd);

    // BROWSER
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_BROWSER)
      schreibeBrowser(sd);

    // PDF
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_PDF)
      schreibePDF(ad,sd);

    // TEXT
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_TXT)
      schreibeTXT(ad,sd);

    // INTERN (TEXT)
    if (sd.ausgabeArt == StatistikDaten.AUSGABE_INTERN_TEXT)
      schreibeInternTXT(ad,sd);

    // FTP
    if (sd.ftpServer != null)
      schreibeFTP(sd);
  }






// ============================== H T M L =======================================================

  static void outHTML(BufferedWriter f, String s, boolean right, String color) throws IOException {
    if (s != null)
      f.write("<td" +
               (color != null ? " bgcolor=\"#"+color+"\"" : "") +
               (right ? " align=\"right\"" : "") +
               ">" +
               (s.length() > 0 ? s : "&nbsp;" ) +
               "</td>\n");
  }
  static void outHTMLgra(BufferedWriter f, AusgabeEintrag ae, String[] s, int colspan) throws IOException {
    if (s != null && s[0] != null) {
      if (colspan==1 || s[1] == null || s[2]==null) {
        // "normale" Ausgabe (kein Vorjahresvergleich) oder nicht grafische Ausgabe
        f.write("<td" + (s[1] == null ? " align=\"right\"" : "") + ">");
        if (s[1] != null && s[2]!=null && !s[2].equals("0")) f.write("<img src=\""+s[1]+"\" width=\""+s[2]+"\" height=\"20\" alt=\"\">&nbsp;");
        f.write(s[0] + "</td>\n");
      } else {
        // Ausgabe für Vorjahresvergleich bei grafischer Ausgabe mit zwei Tabellenfeldern
        int wert = EfaUtil.zehntelString2Int(s[0]);
        // Null-Wert zentriert über beide Spalten
        if (wert == 0) {
          f.write("<td align=\"center\" colspan=\"2\">"+s[0]+"</td>");
        } else {
          // linke Spalte für negative Werte
          f.write("<td align=\"right\">");
          if (wert<0) {
            f.write(s[0]);
            if (!s[2].equals("0")) f.write("&nbsp;<img src=\""+s[1]+"\" width=\""+Math.abs(EfaUtil.string2int(s[2],0))+"\" height=\"20\" alt=\"\">");
          } else f.write("&nbsp;");
          f.write("</td>");
          // rechte Spalte für positive Werte
          f.write("<td align=\"left\">");
          if (wert>0) {
            if (!s[2].equals("0")) f.write("<img src=\""+s[1]+"\" width=\""+s[2]+"\" height=\"20\" alt=\"\">&nbsp;");
            f.write(s[0]);
          } else f.write("&nbsp;");
          f.write("</td>");
        }
      }
    }
  }


  static void schreibeHTMLZeilen(BufferedWriter f, StatOutputLines zeilen) throws IOException {
    int maxCols = 1;
    for (int i=0; i<zeilen.size(); i++) if (zeilen.getLineColumns(i)>maxCols) maxCols = zeilen.getLineColumns(i);

    if (maxCols>1) f.write("<table align=\"center\">\n");
    else f.write("<p>\n");
    for (int i=0; i<zeilen.size(); i++) {
      if (maxCols>1) f.write("<tr><td>" + (zeilen.getLineFont(i)==StatOutputLines.FONT_BOLD ? "<b>" : "") );
      String s;
      if (zeilen.getLineColumns(i)>1) {
        s = EfaUtil.replace(zeilen.getLine(i),"|",
                            (zeilen.getLineFont(i)==StatOutputLines.FONT_BOLD ? "</b>" : "")+"</td><td>"+(zeilen.getLineFont(i)==StatOutputLines.FONT_BOLD ? "<b>" : ""),
                            true);
      } else {
        s = (zeilen.getLineFont(i)==StatOutputLines.FONT_BOLD ? "<b>" : "") +
             zeilen.getLine(i) +
            (zeilen.getLineFont(i)==StatOutputLines.FONT_BOLD ? "</b>" : "");
      }
      f.write(s);
      if (maxCols>1) f.write( (zeilen.getLineFont(i)==StatOutputLines.FONT_BOLD ? "</b>" : "") + "</td></tr>\n");
      else f.write("<br>\n");
    }
    if (maxCols>1) f.write("</table>\n");
    else f.write("</p>\n");
  }


  static void schreibeHTMLTabellenFolge(BufferedWriter f, TabellenFolgenEintrag column) throws IOException {
    for (TabellenFolgenEintrag col = column; col != null; col = col.next) {
      if (col.cols == 0 && col != column) {
        f.write("</table>\n");
        f.write("<br>\n");
      }
      if (col.cols == 0) {
        f.write("<table border align=\"center\">\n");
      } else {
        f.write("<tr>");
        for (int i=0; i<col.fields.length; i++) {
          f.write("<td valign=\"top\"" +
                    (col.colspan==1 ? " width=\""+(100.0f / (float)col.cols)+"%\"" : " colspan=\""+col.colspan+"\" align=\"center\"") +
                    " bgcolor=\"#"+col.colors[i]+"\"" +
                    ">" +
                    (col.bold[i] ? "<b>" : "") +
                    EfaUtil.replace(col.fields[i],"\n","<br>",true)+
                    (col.bold[i] ? "</b>" : "") +
                    "</td>");
        }
        f.write("</tr>\n");
      }
    }
    if (column != null) f.write("</table>");
  }

  static boolean schreibeHTML(AusgabeDaten ad, StatistikDaten sd) {
    BufferedWriter f=null;
    BufferedReader fo=null;
    String tempdatei = sd.ausgabeDatei+".efatmp";

    if (sd.fileExecBefore != null && sd.fileExecBefore.length() > 0) execCmd(sd.fileExecBefore);
    try {

      // Nur Tabelle ersetzen?
      if (sd.tabelleHTML && ! new File(sd.ausgabeDatei).isFile()) sd.tabelleHTML = false;
      if (sd.tabelleHTML) {
        File bak = new File(sd.ausgabeDatei);
        bak.renameTo(new File(tempdatei));
        fo = new BufferedReader(new InputStreamReader(new FileInputStream(tempdatei),Daten.ENCODING_UTF));
      }

      // Datei erstellen und Kopf schreiben
      f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sd.ausgabeDatei),Daten.ENCODING_UTF));
      if (sd.tabelleHTML) {
        String zz;
        while ( (zz = fo.readLine()) != null && !zz.trim().equals("<!--EFA-START-->") )
          f.write(zz+"\n");
      } else {
        f.write("<html>\n");
        f.write("<head>\n");
        f.write("<meta http-equiv=\"content-type\" content=\"text/html; charset="+Daten.ENCODING_UTF+"\">\n");
        f.write("<title>"+ad.titel+"</title>\n");
        f.write("</head>\n");
        f.write("<body>\n");
        f.write("<h1 align=\"center\">"+ad.titel+"</h1>\n");
      }

      // Start des eigentlichen Bereichs
      f.write("<!--EFA-START-->\n");

      f.write("<table align=\"center\" bgcolor=\"#ffffff\" border>\n");
      f.write("<tr>");
      // Ausgabe des efa-Logos
//      int rowspan=5; // @@LOGO
//      if (ad.auswertungNurFuer != null) rowspan++; // @@LOGO
//      if (ad.auswertungWettNur != null) rowspan++; // @@LOGO
//      if.write("<td rowspan=\""+rowspan+"\"><a href=\""+ad.ausgewertetVonURL+"\"><img src=\"efa.gif\" width=\"128\" height=\"128\" alt=\"efa\" border=\"0\"></a></td>\n"); // @@LOGO
      f.write("<td>" + 
              International.getString("Kilometerliste erstellt am") +
              ":</td><td><b>"+ad.ausgewertetAm+
              ", <i><a href=\""+ad.ausgewertetVonURL+"\">"+ad.ausgewertetVon+"</a></i></b></td></tr>\n");
      f.write("<tr><td>" +
              International.getString("Art der Auswertung") +
              ":</td><td><b>"+ad.auswertungsArt+"</b></td></tr>\n");
      f.write("<tr><td>" + 
              International.getString("Zeitraum für Auswertung") +
              ":</td><td><b>"+ad.auswertungsZeitraum+"</b></td></tr>\n");
      f.write("<tr><td>" +
              International.getString("Ausgewertete Einträge") +
              ":</td><td><b>"+ad.ausgewerteteEintraege+"</b></td></tr>\n");
      f.write("<tr><td>" + 
              International.getString("Auswertung für") +
              ":</td><td><b>");
      for (int i=0; i<ad.auswertungFuer.length; i++)
        f.write( (i>0 ? "<br>" : "") + ad.auswertungFuer[i]);
      f.write("</b></td></tr>\n");
      if (ad.auswertungNurFuer != null)
        f.write("<tr><td>" + 
                International.getString("nur für") +
                " "+ad.auswertungNurFuerBez+":</td><td><b>"+ad.auswertungNurFuer+"</b></td></tr>\n");
      if (ad.auswertungWettNur != null)
        f.write("<tr><td>" + 
                International.getString("Ausgabe, wenn") +
                ":</td><td><b>"+ad.auswertungWettNur+"</b></td></tr>\n");
      f.write("</table>\n<br><br>\n");

      AusgabeEintrag ae;

      // Auswertung von Wettbewerbseinträgen
      // Wettbewerbsbedingungen
      if (ad.wett_bedingungen != null) {
        f.write("<table align=\"center\" bgcolor=\"#eeeeee\" border><tr><td>\n");
        for (int i=0; i<ad.wett_bedingungen.length; i++) {
          if (ad.wett_bedingungen_fett.get(new Integer(i)) != null) f.write("<b>");
          if (ad.wett_bedingungen_kursiv.get(new Integer(i)) != null) f.write("<i>");
          f.write(ad.wett_bedingungen[i]+"<br>");
          if (ad.wett_bedingungen_kursiv.get(new Integer(i)) != null) f.write("</i>");
          if (ad.wett_bedingungen_fett.get(new Integer(i)) != null) f.write("</b>");
        }
        f.write("</table>\n<br><br>\n");
      }

      if (sd.wettOhneDetail) {
        f.write("<table align=\"center\" width=\"500\">\n");
        f.write("<tr><th colspan=\"2\" bgcolor=\"#ddddff\">Legende</th></tr>\n");
        f.write("<tr><td bgcolor=\"#00ff00\" width=\"250\" align=\"center\">" +
                International.getString("Bedingungen erfüllt") + "</td>");
        f.write("<td bgcolor=\"#ffff00\" width=\"250\" align=\"center\">" +
                International.getString("Bedingungen noch nicht erfüllt") + "</td></tr>\n");
        f.write("</table>\n<br><br>\n");
      }

      if (ad.ausgabeZeilenOben != null) schreibeHTMLZeilen(f,ad.ausgabeZeilenOben);

      if (ad.wett_gruppennamen != null && ad.wett_teilnehmerInGruppe != null) {
        if (ad.wett_zeitraumWarnung != null) {
          f.write("<p align=\"center\"><font color=\"#ff0000\"><b>"+
                  ad.wett_zeitraumWarnung+"</b></font></p>\n");
        }

        f.write("<table width=\"100%\">\n");
        for (int i=0; i<ad.wett_gruppennamen.length; i++) {
          f.write("<tr><th align=\"left\" colspan=\"3\" bgcolor=\"#ddddff\">"+
                   ad.wett_gruppennamen[i][0]+" "+ad.wett_gruppennamen[i][1]+
                   " (<i>gefordert: "+ad.wett_gruppennamen[i][2]+"</i>)</th></tr>\n");
          for (ae = ad.wett_teilnehmerInGruppe[i]; ae != null; ae = ae.next) {
            f.write("<tr><td width=\"10%\">&nbsp;</td>\n");
            if (ae.w_detail == null) {
              // kurze Ausgabe
              if (sd.wettOhneDetail) {
                f.write("<td width=\"45%\" bgcolor=\""+(ae.w_erfuellt ? "#00ff00" : "#ffff00")+"\"><b>"+ae.w_name+"</b></td>"+
                        "<td width=\"45%\" bgcolor=\""+(ae.w_erfuellt ? "#aaffaa" : "#ffffaa")+"\">"+ae.w_kilometer+" Km"+
                                        ( ae.w_additional == null || ae.w_additional.equals("") ? "" : "; "+ae.w_additional ) +"</td>\n");
              } else {
                String additional = ( ae.w_additional == null || ae.w_additional.equals("") ? "" : ae.w_additional ) +
                                    ( ae.w_warnung == null ? "" : "; <font color=\"red\">"+ae.w_warnung+"</font>");
                f.write("<td width=\"90%\" colspan=\"2\">"+(ae.w_erfuellt ? 
                    International.getString("erfüllt")+": " :
                    International.getString("noch nicht erfüllt") + ": ")+"<b>"+ae.w_name+"</b>"+
                                       (ae.w_jahrgang != null ? " ("+ae.w_jahrgang+")" : "") +
                                       ": "+ae.w_kilometer+" Km"+
                                       (additional.length() > 0 ? " ("+additional+")" : "") +
                                       "</td>\n");
              }
            } else {
              // ausführliche Ausgabe
              f.write("<td width=\"90%\" colspan=\"2\">\n");
              int colspan = 1;
              if (ae.w_detail.length>0) colspan = ae.w_detail[0].length;
              f.write("<table border>\n<tr><td colspan=\""+colspan+"\"><b>"+ae.w_name+" ("+ae.w_jahrgang+"): "+
                      ae.w_kilometer+" Km"+(ae.w_additional != null ? "; "+ae.w_additional : "")+"</b></td></tr>\n");
              if (ae.w_detail.length>0)
                for (int j=0; j<ae.w_detail.length; j++) {
                  f.write("<tr>");
                  if (ae.w_detail[j] != null && ae.w_detail[j][0] != null) {
                      for (int k=0; k<ae.w_detail[j].length; k++) {
                          f.write("<td>"+ae.w_detail[j][k]+"</td>");
                      }
                  } else {
                      f.write("<td colspan=\""+colspan+"\">und weitere Fahrten</td>");
                  }
                  f.write("</tr>\n");
                }
              f.write("</table>\n</td>\n");
            }
            f.write("</tr>\n");
          }
          f.write("<tr colspan=\"3\"><td>&nbsp;</td></tr>\n");
        }
        f.write("</table>\n");
      }

      // Auswertung normaler Einträge
      if (ad.tabellenTitel != null) {
        f.write("<table align=\"center\" bgcolor=\"#ffffff\" border>\n<tr>\n");
        for (int i=0; i<ad.tabellenTitel.length; i++)
          f.write("<th"+ (ad.tabellenTitelBreite != null ? " colspan=\""+ad.tabellenTitelBreite[i]+"\"" : "") +">"+ad.tabellenTitel[i]+"</th>");
        f.write("</tr>\n");

        // Einträge auswerten
        for (ae = ad.ae; ae != null; ae = ae.next) {
          if (!ae.zusammenfassung) f.write("<tr bgcolor=\""+ (ae.absnr % 2 == 0 ? "#eeeeff" : "#ccccff" ) +"\">");
          else f.write("<tr>");
          outHTML(f,ae.nr, false,null);
          outHTML(f,ae.name,false,null);
          outHTML(f,ae.jahrgang,false,null);
          outHTML(f,ae.status,false,null);
          outHTML(f,ae.bezeichnung,false,null);
          outHTML(f,ae.anzversch,false,null);
          outHTMLgra(f,ae,ae.km,ae.colspanKm);
          outHTMLgra(f,ae,ae.rudkm,ae.colspanRudKm);
          outHTMLgra(f,ae,ae.stmkm,ae.colspanStmKm);
          outHTMLgra(f,ae,ae.fahrten,ae.colspanFahrten);
          outHTMLgra(f,ae,ae.kmfahrt,ae.colspanKmFahrt);
          outHTMLgra(f,ae,ae.dauer,ae.colspanDauer);
          outHTMLgra(f,ae,ae.kmh,ae.colspanKmH);
          outHTML(f,ae.wafaKm,false,null);
          outHTML(f,ae.zielfahrten,false,null);
          outHTML(f,ae.zusatzDRV,false,null);
          outHTML(f,ae.zusatzLRVBSommer,false,null);
          outHTML(f,ae.zusatzLRVBWinter,false,null);
          outHTML(f,ae.zusatzLRVBrbWanderWett,false,null);
          outHTML(f,ae.zusatzLRVBrbFahrtenWett,false,null);
          outHTML(f,ae.zusatzLRVMVpWanderWett,false,null);
          if (ae.fahrtenbuch != null)
            for (int i=0; i<ae.fahrtenbuch.length; i++) if (ae.fahrtenbuch[i] != null) outHTML(f,ae.fahrtenbuch[i],false,null);
          if (ae.ww != null)
            for (int i=0; i<ae.ww.length; i++) outHTML(f,ae.ww[i],false, (ae.ww_selbst[i] ? "ffdddd" : null) );
          f.write("</tr>\n");
        }

        f.write("</table>\n");
      }

      // Zusatzdaten
      if (ad.additionalTable != null) {
        f.write("<br><table border align=\"center\">\n");
        for (int i=0; i<ad.additionalTable.length; i++) {
          f.write("<tr>");
          for (int j=0; j<ad.additionalTable[i].length; j++)
            f.write("<td>" +
                     ( (i == 0 && ad.additionalTable_1stRowBold) || (i == ad.additionalTable.length-1 && ad.additionalTable_lastRowBold) ? "<b>" : "") +
                     ad.additionalTable[i][j] +
                     ( (i == 0 && ad.additionalTable_1stRowBold) || (i == ad.additionalTable.length-1 && ad.additionalTable_lastRowBold) ? "</b>" : "") +
                     "</td>");
          f.write("</tr>\n");
        }
        f.write("</table><br>\n");
      }

      if (ad.tfe != null) schreibeHTMLTabellenFolge(f,ad.tfe);

      if (ad.ausgabeZeilenUnten != null) schreibeHTMLZeilen(f,ad.ausgabeZeilenUnten);

      // Ende des eigentlichen Bereichs
      if (sd.tabelleHTML) {
        String zz;
        while ( (zz = fo.readLine()) != null && !zz.trim().equals("<!--EFA-ENDE-->") );
        f.write("\n<!--EFA-ENDE-->\n");
        while ( (zz = fo.readLine()) != null) f.write(zz+"\n");
        fo.close();
        File bak = new File(tempdatei);
        bak.delete();
      } else {
        f.write("\n<!--EFA-ENDE-->\n");
        f.write("</body>\n");
        f.write("</html>\n");
      }
    } catch(IOException e) {
      Dialog.error(LogString.logstring_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei")));
      LogString.logError_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei"));
      return false;
    } finally {
      try { f.close(); } catch(Exception ee) { f = null; }
    }
    if (sd.fileExecAfter != null && sd.fileExecAfter.length() > 0) execCmd(sd.fileExecAfter);
    return true;
 }

  static void schreibeHTMLgrafiken(StatistikDaten sd) {
    String q,z;
        if (sd.graphischKm)
          if (new File(q = Daten.efaAusgabeDirectory+COLORKM+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORKM+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischRudKm)
          if (new File(q = Daten.efaAusgabeDirectory+COLORRUDKM+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORRUDKM+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischStmKm)
          if (new File(q = Daten.efaAusgabeDirectory+COLORSTMKM+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORSTMKM+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischFahrten)
          if (new File(q = Daten.efaAusgabeDirectory+COLORFAHRTEN+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORFAHRTEN+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischKmFahrt)
          if (new File(q = Daten.efaAusgabeDirectory+COLORKMFAHRT+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORKMFAHRT+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischDauer)
          if (new File(q = Daten.efaAusgabeDirectory+COLORDAUER+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORDAUER+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischKmH)
          if (new File(q = Daten.efaAusgabeDirectory+COLORKMH+".gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORKMH+".gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischKm && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORKM+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORKM+"big.gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischRudKm && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORRUDKM+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORRUDKM+"big.gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischStmKm && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORSTMKM+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORSTMKM+"big.gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischFahrten && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORFAHRTEN+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORFAHRTEN+"big.gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischKmFahrt && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORKMFAHRT+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORKMFAHRT+"big.gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischDauer && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORDAUER+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORDAUER+"big.gif").exists())
            EfaUtil.copyFile(q,z);
        if (sd.graphischKmH && sd.cropToMaxSize)
          if (new File(q = Daten.efaAusgabeDirectory+COLORKMH+"big.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+COLORKMH+"big.gif").exists())
            EfaUtil.copyFile(q,z);

        // Bei mitgeliefertem Layout "Ausdruck-SW" auch die grauen Balkendateien kopieren
        if (sd.stylesheet != null && sd.stylesheet.toLowerCase().endsWith("ausdruck-sw.xsl")) {
          if (sd.graphischDauer || sd.graphischFahrten || sd.graphischKm || sd.graphischKmFahrt || sd.graphischKmH || sd.graphischRudKm || sd.graphischStmKm) {
            if (new File(q = Daten.efaAusgabeDirectory+"grau.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+"grau.gif").exists())
              EfaUtil.copyFile(q,z);
            if (sd.cropToMaxSize)
              if (new File(q = Daten.efaAusgabeDirectory+"graubig.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+"graubig.gif").exists())
              EfaUtil.copyFile(q,z);
          }
        }

        // Bei Verwendung von Stylesheets die Datei efa.gif kopieren
        if (sd.stylesheet != null) {
          if (new File(q = Daten.efaAusgabeDirectory+"efa.gif").exists() && !new File(z = EfaUtil.getPathOfFile(sd.ausgabeDatei)+Daten.fileSep+"efa.gif").exists()) // @@LOGO
          EfaUtil.copyFile(q,z);// @@LOGO
        }
  }

  static boolean schreibeHTMLtabelle(StatistikDaten sd) {
    BufferedWriter f;       // zu schreibende Datei
    BufferedReader fo=null; // alte Originaldatei, in der die Tabelle ersetzt werden soll
    BufferedReader fn=null; // neue Temp-Datei, deren Tabelle kopiert werden soll
    String tempdatei = sd.ausgabeDatei+".efatmp";

    try {
      // existiert die Originaldatei überhaupt?
      if (! new File(sd.ausgabeDatei).isFile()) {
        new File(sd.ausgabeDateiTmp).renameTo(new File(sd.ausgabeDatei));
        return true;
      }

      String zz;

      // Originaldatei umbennen
      File bak = new File(sd.ausgabeDatei);
      bak.renameTo(new File(tempdatei));

      // Dateien öffnen und neue Datei erstellen
      fo = new BufferedReader(new InputStreamReader(new FileInputStream(tempdatei),Daten.ENCODING_UTF));
      fn = new BufferedReader(new InputStreamReader(new FileInputStream(sd.ausgabeDateiTmp),Daten.ENCODING_UTF));
      f =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sd.ausgabeDatei),Daten.ENCODING_UTF));

      // Dateikopf von fo nach f kopieren
      while ( (zz = fo.readLine()) != null && !zz.equals("<!--EFA-START-->") )
        f.write(zz+"\n");
      f.write("<!--EFA-START-->\n");

      // Beginn der eigentlichen Daten in fn suchen
      while ( (zz = fn.readLine()) != null && !zz.equals("<!--EFA-START-->") );

      // eigentliche Daten von fn nach f kopieren
      while ( (zz = fn.readLine()) != null && !zz.equals("<!--EFA-ENDE-->") )
        f.write(zz+"\n");
      f.write("<!--EFA-ENDE-->\n");

      // Dateifuß in fo suchen
      while ( (zz = fo.readLine()) != null && !zz.equals("<!--EFA-ENDE-->") );

      // Dateifuß von fo nach f kopieren
      while ( (zz = fo.readLine()) != null)
        f.write(zz+"\n");

      // Dateien schließen
      f.close();
      fo.close();
      fn.close();

      // Alte Datei (fo) und Temp-Datei (fn) löschen
      new File(tempdatei).delete();
      new File(sd.ausgabeDateiTmp).delete();

    } catch(IOException e) {
      Dialog.error(LogString.logstring_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei")));
      LogString.logError_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei"));
      return false;
    }
    return true;
  }




// ============================== C S V =========================================================

  static void outCSV(BufferedWriter f, String s) throws IOException {
    if (s != null) f.write(s + "|");
  }

  static boolean schreibeCSV(AusgabeDaten ad, StatistikDaten sd) {
    if (sd.fileExecBefore != null && sd.fileExecBefore.length() > 0) execCmd(sd.fileExecBefore);

    BufferedWriter f;
    try {

      // Datei erstellen und Kopf schreiben
      f = new BufferedWriter(new FileWriter(sd.ausgabeDatei));

      // Einträge auswerten
      AusgabeEintrag ae;
      for (ae = ad.ae; ae != null; ae = ae.next) {
        outCSV(f,ae.nr);
        outCSV(f,ae.name);
        outCSV(f,ae.jahrgang);
        outCSV(f,ae.status);
        outCSV(f,ae.bezeichnung);
        outCSV(f,ae.km[0]);
        outCSV(f,ae.rudkm[0]);
        outCSV(f,ae.stmkm[0]);
        outCSV(f,ae.fahrten[0]);
        outCSV(f,ae.kmfahrt[0]);
        outCSV(f,ae.dauer[0]);
        outCSV(f,ae.kmh[0]);
        outCSV(f,ae.anzversch);
        outCSV(f,ae.wafaKm);
        outCSV(f,ae.zielfahrten);
        outCSV(f,ae.zusatzDRV);
        outCSV(f,ae.zusatzLRVBSommer);
        outCSV(f,ae.zusatzLRVBWinter);
        outCSV(f,ae.zusatzLRVBrbWanderWett);
        outCSV(f,ae.zusatzLRVBrbFahrtenWett);
        outCSV(f,ae.zusatzLRVMVpWanderWett);
        if (ae.fahrtenbuch != null)
          for (int i=0; i<ae.fahrtenbuch.length; i++) if (ae.fahrtenbuch[i] != null) outCSV(f,ae.fahrtenbuch[i]);
        if (ae.ww != null)
          for (int i=0; i<ae.ww.length; i++) outCSV(f,ae.ww[i]);
        f.write("\n");
      }

      TabellenFolgenEintrag col;
      for (col = ad.tfe; col != null; col = col.next) {
        for (int i=0; col.fields != null && i<col.fields.length; i++) outCSV(f,EfaUtil.replace(col.fields[i],"\n"," ",true));
        f.write("\n");
      }

      if (ad.wett_teilnehmerInGruppe != null)
        for (int g=0; g<ad.wett_teilnehmerInGruppe.length; g++)
          for (ae = ad.wett_teilnehmerInGruppe[g]; ae != null; ae = ae.next) {
            outCSV(f,ae.w_name);
            outCSV(f,ae.w_jahrgang);
            outCSV(f,ae.w_kilometer);
//            if (ae.w_detail != null)
//              for (int i=0; i<ae.w_detail.length; i++)
//                for (int j=0; j<ae.w_detail[i].length; j++)
//                  outCSV(f,ae.w_detail[i][j]);
            outCSV(f, (ae.w_attr1==null ? "" : ae.w_attr1));
            outCSV(f, (ae.w_attr2==null ? "" : ae.w_attr2));
            outCSV(f, (ae.w_erfuellt ? 
                International.getString("erfüllt") :
                International.getString("nicht erfüllt")) );
            if (ae.w_additional != null && ae.w_warnung != null) outCSV(f,ae.w_additional+"; "+ae.w_warnung);
            else if (ae.w_additional != null) outCSV(f,ae.w_additional);
            else if (ae.w_warnung != null) outCSV(f,ae.w_warnung);
            else outCSV(f,null);
            f.write("\n");
          }

      if (ad.additionalTable != null) {
        for (int i=0; i<ad.additionalTable.length; i++) {
          for (int j=0; j<ad.additionalTable[i].length; j++)
            outCSV(f,ad.additionalTable[i][j]);
          f.write("\n");
        }
      }

      f.close();
    } catch(IOException e) {
      Dialog.error(LogString.logstring_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei")));
      LogString.logError_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei"));
      return false;
    }

    if (sd.fileExecAfter != null && sd.fileExecAfter.length() > 0) execCmd(sd.fileExecAfter);
    return true;
 }







// ============================== I N T E R N - G R A F I K =====================================

  static void schreibeInternGrafik(StatistikDaten sd) {
    if (!new File(sd.ausgabeDatei).isFile()) {
      Dialog.error(LogString.logstring_fileNotFound(sd.ausgabeDatei, International.getString("Ausgabedatei")));
    } else {
      Dialog.neuBrowserDlg(sd.parent,
              International.getString("Ausgabe"),
              "file:"+sd.ausgabeDatei,sd.browserCloseTimeout);
    }
  }






// ============================== B R O W S E R =================================================

  static void schreibeBrowser(StatistikDaten sd) {
    if (!new File(sd.ausgabeDatei).isFile()) {
        Dialog.error(LogString.logstring_fileNotFound(sd.ausgabeDatei, International.getString("Ausgabedatei")));
    } else {
      if (!new File(Daten.efaConfig.getValueBrowser()).isFile()) {
          Dialog.error(LogString.logstring_cantExecCommand(Daten.efaConfig.getValueBrowser(), International.getString("für Browser"), ""));
      } else try {
        String[] cmd = new String[2];
        cmd[0] = Daten.efaConfig.getValueBrowser();
        cmd[1] = sd.ausgabeDatei;
        Runtime.getRuntime().exec(cmd);
      } catch(Exception ee) {
          Dialog.error(LogString.logstring_cantExecCommand(Daten.efaConfig.getValueBrowser(), International.getString("für Browser"), ee.toString()));
      }
    }
  }




// ============================== P D F =========================================================

  static boolean schreibePDF(AusgabeDaten ad, StatistikDaten sd) {
    try {
      PDFWriter pdf = new PDFWriter(sd,ad);
      String s;
      if (sd.fileExecBefore != null && sd.fileExecBefore.length() > 0) execCmd(sd.fileExecBefore);
      // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(false,
              // @todo (P5) statistics International.getString("FOP erstellt die PDF Datei ..."),false);
      s = pdf.run();
      if (s != null) {
        // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(true,null,false);
        for (int i=0, c=0; i<s.length(); i++, c++) {
          if ( (c>70 && s.charAt(i)==' ') || c>100) {
            s = s.substring(0,i)+"\n"+s.substring(i+1,s.length());
            c=0;
          }
        }
        Dialog.error(LogString.logstring_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei")));
        return false;
      } else {
        if (Daten.efaConfig.getValueAcrobat().length()>0 && sd.statistikFrame != null) {
          // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(true,null,false);
          try {
            String[] cmd = new String[2];
            cmd[0] = Daten.efaConfig.getValueAcrobat();
            cmd[1] = sd.ausgabeDatei;
            if (sd.statistikFrame != null) {
                // @todo (P5) statistics sd.statistikFrame.enableFrame(false,International.getString("Starte Acrobat ..."),false);
            }
            Runtime.getRuntime().exec(cmd);
          } catch(Exception ee) {
            Dialog.error(LogString.logstring_cantExecCommand(Daten.efaConfig.getValueAcrobat(), International.getString("für Acrobat Reader"), ee.toString()));
          }
        }
        // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(true,
        // @todo (P5) statistics         International.getString("PDF-Datei erfolgreich erstellt") +
        // @todo (P5) statistics         " (" + International.getMessage("{n} Seiten",pdf.getPageCount()) + ")!",false);
      }
    } catch (NoClassDefFoundError e) {
      // Plugin-Dialoge
      if (sd.statistikFrame != null) sd.statistikFrame.setEnabled(true);
      DownloadFrame.getPlugin(Daten.EFA_SHORTNAME,Daten.PLUGIN_FOP_NAME,Daten.PLUGIN_FOP_FILE,Daten.PLUGIN_FOP_HTML,e.toString(),sd.statistikFrame,false);
      return false;
    }
    if (sd.fileExecAfter != null && sd.fileExecAfter.length() > 0) execCmd(sd.fileExecAfter);
    return true;
  }






// ============================== X M L ==================== ====================================

  static boolean schreibeXML(AusgabeDaten ad, StatistikDaten sd) {

    try {
      XMLWriter efaxml = new XMLWriter(sd,ad);
      String s;
      if (sd.fileExecBefore != null && sd.fileExecBefore.length() > 0) execCmd(sd.fileExecBefore);
      if ( (s = efaxml.run()) != null) {
        for (int i=0, c=0; i<s.length(); i++, c++) {
          if ( (c>70 && s.charAt(i)==' ') || c>100) {
            s = s.substring(0,i)+"\n"+s.substring(i+1,s.length());
            c=0;
          }
        }
        Dialog.error(LogString.logstring_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei")));
        return false;
      }
    } catch (NoClassDefFoundError e) {
      // Plugin-Dialoge
      if (sd.statistikFrame != null) sd.statistikFrame.setEnabled(true);
      DownloadFrame.getPlugin(Daten.EFA_SHORTNAME,Daten.PLUGIN_JAXP_NAME,Daten.PLUGIN_JAXP_FILE,Daten.PLUGIN_JAXP_HTML,e.toString(),sd.statistikFrame,false);
      return false;
    }
    if (sd.fileExecAfter != null && sd.fileExecAfter.length() > 0) execCmd(sd.fileExecAfter);
    return true;
  }





// ============================== E F A W E T T =================================================

  static void initEfaWett(StatistikDaten sd) {
        int wettNr = sd.art - 200;
        WettDef wett = Daten.wettDefs.getWettDef(wettNr,sd.wettJahr);
        efaWett = new EfaWett();
        efaWett.wettId = wettNr;
        efaWett.allg_programm = Daten.PROGRAMMID;
        if (wett.von.jahr == wett.bis.jahr)
          efaWett.allg_wettjahr = Integer.toString(sd.wettJahr+wett.von.jahr);
        else
          efaWett.allg_wettjahr = Integer.toString(sd.wettJahr+wett.von.jahr)+"/"+
                                  Integer.toString(sd.wettJahr+wett.bis.jahr);
        efaWett.allg_wett=wett.key;
        if (Daten.vereinsConfig != null) {
          switch(wettNr) {
            case WettDefs.DRV_FAHRTENABZEICHEN:
            case WettDefs.DRV_WANDERRUDERSTATISTIK:
              efaWett.verein_user = Daten.vereinsConfig.userDRV;
              break;
            case WettDefs.LRVBERLIN_SOMMER:
            case WettDefs.LRVBERLIN_WINTER:
            case WettDefs.LRVBERLIN_BLAUERWIMPEL:
              efaWett.verein_user = Daten.vereinsConfig.userLRV;
              break;
          }
          efaWett.verein_name = Daten.vereinsConfig.vereinsname;
          efaWett.meld_name = Daten.vereinsConfig.meldenderName;
          efaWett.meld_email = Daten.vereinsConfig.meldenderEmail;
          efaWett.versand_name = Daten.vereinsConfig.versandName;
          efaWett.versand_strasse = Daten.vereinsConfig.versandStrasse;
          efaWett.versand_ort = Daten.vereinsConfig.versandOrt;
        }
  }

    public static void schreibeEfaWett(EfaWett d) {
        if (Logger.isTraceOn(Logger.TT_STATISTICS)) {
            Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_STATISTICS, "Statistik.schreibeEfaWett(...) - START");
        }
        try {
            d.writeFile();
            if (Logger.isTraceOn(Logger.TT_STATISTICS)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_STATISTICS, "Statistik.schreibeEfaWett(...): Done with writing without Exception");
            }
        } catch (IOException e) {
            if (Logger.isTraceOn(Logger.TT_STATISTICS)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_STATISTICS, "Statistik.schreibeEfaWett(...): Exception Handler: " + e.toString());
            }
            Dialog.error("Fehler beim Schreiben der Datei: " + d.datei);
        }
        if (Logger.isTraceOn(Logger.TT_STATISTICS)) {
            Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_STATISTICS, "Statistik.schreibeEfaWett(...) - END");
        }
    }



// ============================== T E X T =======================================================

  static void outTXT(BufferedWriter f, String indent, String[][] t, boolean hrAfter1stRow, boolean hrBeforeLastRow) throws IOException {
    int lineLength = normalizeAusgabeTabelle(t) + (t[0].length-1)*2; // (t[0].length-1)*2 == Anzahl der Trennzeichen zwischen den Feldern!
    for (int x=0; x<t.length; x++) {
      if (t[x] == null || t[x][0] == null) {
          continue;
      }
      f.write(indent);
      if ( (x == 1 && hrAfter1stRow) || (x == t.length-1 && hrBeforeLastRow) ) {
        for (int y=0; y<lineLength; y++) f.write("-");
        f.write("\n"+indent);
      }
      for (int y=0; y<t[x].length; y++) {
        f.write(t[x][y]);
        if (y+1 < t[x].length) f.write("  ");
        else f.write("\n");
      }
    }
    f.write("\n");
  }


  static boolean schreibeTXT(AusgabeDaten ad, StatistikDaten sd) {
    if (sd.fileExecBefore != null && sd.fileExecBefore.length() > 0) execCmd(sd.fileExecBefore);

    BufferedWriter f;
    try {
      f = new BufferedWriter(new FileWriter(sd.ausgabeDatei));


      int kopfzeilen = 4 + ad.auswertungFuer.length +
          (ad.auswertungNurFuer != null ? 1 : 0) + (ad.auswertungWettNur != null ? 1 : 0);
      String[][] kt = new String[kopfzeilen][2];
      kt[0][0] = International.getString("Kilometerliste erstellt am")+":";
      kt[0][1] = ad.ausgewertetAm+", "+ad.ausgewertetVon;
      kt[1][0] = International.getString("Art der Auswertung")+":";
      kt[1][1] = ad.auswertungsArt;
      kt[2][0] = International.getString("Zeitraum für Auswertung")+":";
      kt[2][1] = ad.auswertungsZeitraum;
      kt[3][0] = International.getString("Ausgewertete Einträge")+":";
      kt[3][1] = ad.ausgewerteteEintraege;
      for (int i=0; i<ad.auswertungFuer.length; i++) {
        kt[4+i][0] = (i>0 ? International.getString("Auswertung für")+":" : "");
        kt[4+i][1] = ad.auswertungFuer[i];
      }
      if (ad.auswertungNurFuer != null) {
        kt[4 + ad.auswertungFuer.length][0] = International.getMessage("nur für {something}",ad.auswertungNurFuerBez)+":";
        kt[4 + ad.auswertungFuer.length][1] = ad.auswertungNurFuer;
      }
      if (ad.auswertungWettNur != null) {
        kt[4 + ad.auswertungFuer.length + (ad.auswertungNurFuer != null ? 1 : 0)][0] = International.getString("Ausgabe, wenn")+":";
        kt[4 + ad.auswertungFuer.length + (ad.auswertungNurFuer != null ? 1 : 0)][1] = ad.auswertungWettNur;
      }
      outTXT(f,"",kt,false,false);
      f.write("\n\n");


      AusgabeEintrag ae;

      // Auswertung von Wettbewerbseinträgen
      // Wettbewerbsbedingungen
      if (ad.wett_bedingungen != null) {
        for (int i=0; i<ad.wett_bedingungen.length; i++) {
          f.write(ad.wett_bedingungen[i]+"\n");
        }
        f.write("\n");
      }

      if (ad.wett_gruppennamen != null && ad.wett_teilnehmerInGruppe != null) {
        if (ad.wett_zeitraumWarnung != null) {
          f.write(ad.wett_zeitraumWarnung+"\n\n");
        }

        for (int i=0; i<ad.wett_gruppennamen.length; i++) {
          f.write(ad.wett_gruppennamen[i][0]+" "+ad.wett_gruppennamen[i][1]+
                  " ("+International.getString("gefordert")+": "+ad.wett_gruppennamen[i][2]+")\n\n");
          for (ae = ad.wett_teilnehmerInGruppe[i]; ae != null; ae = ae.next) {
            if (ae.w_detail == null) {
              // kurze Ausgabe
              f.write("  "+(ae.w_erfuellt ? 
                  International.getString("erfüllt")+": " :
                  International.getString("noch nicht erfüllt")+": ")+ae.w_name+" ("+ae.w_kilometer+" Km"+
                      ( ae.w_additional == null || ae.w_additional.equals("") ? "" : "; "+ae.w_additional ) +")\n");
              if (ae.w_warnung != null) f.write("    "+ae.w_warnung+"\n");
            } else {
              // ausführliche Ausgabe
              f.write("  "+ae.w_name+" ("+ae.w_jahrgang+"): "+ae.w_kilometer+" Km\n");
              if (ae.w_detail.length>0) {
                outTXT(f,"    ",ae.w_detail,false,false);
              }
              if (ae.w_warnung != null) f.write("    "+ae.w_warnung+"\n");
            }
            f.write("\n");
          }
          f.write("\n");
        }
        f.write("\n");
      }

      // normale Tabellendaten
      if (ad.tabellenTitel != null) {
        String[][] t = createAusgabeTabelle(ad,true);
        outTXT(f,"",t,true,false);
      }

      // Spezialtabellen
      if (ad.tfe != null) {
        String[][] t = createAusgabeTabelle(ad.tfe);
        outTXT(f,"",t,true,false);
      }

      // Zusatzdaten
      if (ad.additionalTable != null) {
        outTXT(f,"",ad.additionalTable,ad.additionalTable_1stRowBold,ad.additionalTable_lastRowBold);
      }

      f.close();
    } catch (IOException e) {
      Dialog.error(LogString.logstring_fileCreationFailed(sd.ausgabeDatei, International.getString("Ausgabedatei")));
      return false;
    }
    if (sd.fileExecAfter != null && sd.fileExecAfter.length() > 0) execCmd(sd.fileExecAfter);
    return true;
  }



// ============================== I N T E R N - T E X T =========================================

  static void outInternTXT(String indent, String[][] t) {
    normalizeAusgabeTabelle(t);
    for (int x=0; x<t.length; x++) {
      Dialog.programOutText.append(indent);
      if (t[x] == null || t[x][0] == null) {
          continue;
      }
      for (int y=0; y<t[x].length; y++) {
        Dialog.programOutText.append(t[x][y]);
        if (y+1 < t[x].length) Dialog.programOutText.append("  ");
        else Dialog.programOutText.append("\n");
      }
    }
    Dialog.programOutText.append("\n");
  }

  static boolean schreibeInternTXT(AusgabeDaten ad, StatistikDaten sd) {

    if (sd.art < StatistikDaten.WETT_DRV) {
      AusgabeEintrag ae;
      // normale Ausgabe (nur Tabelle, keine Wettbewerbe)
      if (ad.tabellenTitel != null || ad.tfe != null) {
        TableSorter sorter = null;
        if (ad.tabellenTitel != null) {
          sorter = new TableSorter(new javax.swing.table.DefaultTableModel(createAusgabeTabelle(ad,false),ad.tabellenTitel));
        } else {
          String[][] t = createAusgabeTabelle(ad.tfe);
          String[] tit = new String[t[0].length]; for (int i=0; i<tit.length; i++) tit[i] = "";
          sorter = new TableSorter(new javax.swing.table.DefaultTableModel(t,tit));
        }
        Dialog.programOut = new javax.swing.JTable(sorter);
        sorter.addMouseListenerToHeaderInTable(Dialog.programOut);
        Dialog.neuProgramDlg(sd.parent);
        Dialog.programDlg.jScrollPane1.getViewport().add(Dialog.programOut,null);
        Dialog.programDlg.show();
        return true;
      }
    } else {
      // Wettbewerbs-Ausgabe
      Dialog.programOutText = new javax.swing.JTextArea();

      AusgabeEintrag ae;
      // Auswertung von Wettbewerbseinträgen
      // Wettbewerbsbedingungen
      if (ad.wett_bedingungen != null) {
        for (int i=0; i<ad.wett_bedingungen.length; i++) {
          Dialog.programOutText.append(ad.wett_bedingungen[i]+"\n");
        }
        Dialog.programOutText.append("\n");
      }

      if (ad.wett_gruppennamen != null && ad.wett_teilnehmerInGruppe != null) {
        if (ad.wett_zeitraumWarnung != null) {
          Dialog.programOutText.append(ad.wett_zeitraumWarnung+"\n\n");
        }

        for (int i=0; i<ad.wett_gruppennamen.length; i++) {
          Dialog.programOutText.append(ad.wett_gruppennamen[i][0]+" "+ad.wett_gruppennamen[i][1]+
                  " ("+International.getString("gefordert")+": "+ad.wett_gruppennamen[i][2]+")\n\n");
          for (ae = ad.wett_teilnehmerInGruppe[i]; ae != null; ae = ae.next) {
            if (ae.w_detail == null) {
              // kurze Ausgabe
              Dialog.programOutText.append("  "+(ae.w_erfuellt ? 
                  International.getString("erfüllt")+": " :
                  International.getString("noch nicht erfüllt")+": ")+ae.w_name+" ("+ae.w_kilometer+" Km"+
                      ( ae.w_additional == null || ae.w_additional.equals("") ? "" : "; "+ae.w_additional ) +")\n");
            } else {
              // ausführliche Ausgabe
              Dialog.programOutText.append("  "+ae.w_name+" ("+ae.w_jahrgang+"): "+ae.w_kilometer+" Km\n");
              if (ae.w_detail.length>0) {
                outInternTXT("    ",ae.w_detail);
              }
            }
            if (ae.w_warnung != null) Dialog.programOutText.append("    "+ae.w_warnung+"\n");
            Dialog.programOutText.append("\n");
          }
          Dialog.programOutText.append("\n");
        }
        Dialog.programOutText.append("\n");
      }


      // normale Tabellendaten
      if (ad.tabellenTitel != null) {
        String[][] t = createAusgabeTabelle(ad,true);
        outInternTXT("",t);
      }

      // spezielle Tabellendaten
      if (ad.tfe != null) {
        String[][] t = createAusgabeTabelle(ad.tfe);
        outInternTXT("",t);
      }

      // Zusatzdaten
      if (ad.additionalTable != null) {
        outInternTXT("",ad.additionalTable);
      }

      Dialog.neuProgramDlg(sd.parent);
      Dialog.programDlg.jScrollPane1.getViewport().add(Dialog.programOutText,null);
      Dialog.programDlg.show();
      return true;
    }

    return false;
  }



// ============================== F T P =========================================================

  static boolean schreibeFTP(StatistikDaten sd) {
    try {
      FTPWriter ftp = new FTPWriter(sd);
      // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(false,
      // @todo (P5) statistics         International.getMessage("FTP-Upload der Datei {filename} ...",sd.ftpFilename),false);
      String s;
      s = ftp.run();
      if (s != null) {
        // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(true,null,false);
        for (int i=0, c=0; i<s.length(); i++, c++) {
          if ( (c>70 && s.charAt(i)==' ') || c>100) {
            s = s.substring(0,i)+"\n"+s.substring(i+1,s.length());
            c=0;
          }
        }
        Dialog.error(International.getString("Der FTP-Upload ist fehlgeschlagen.")+"\n"+s);
        return false;
      } else {
        // @todo (P5) statistics if (sd.statistikFrame != null) sd.statistikFrame.enableFrame(true,null,false);
      }
    } catch (NoClassDefFoundError e) {
      // Plugin-Dialoge
      if (sd.statistikFrame != null) sd.statistikFrame.setEnabled(true);
      DownloadFrame.getPlugin(Daten.EFA_SHORTNAME,Daten.PLUGIN_FTP_NAME,Daten.PLUGIN_FTP_FILE,Daten.PLUGIN_FTP_HTML,e.toString(),sd.statistikFrame,false);
      return false;
    }
    return true;
  }



// ============================== D I V E R S E =================================================


  // Anzahl der verketteten AusgabeEintraege zählen
  static int count(AusgabeEintrag a) {
    int i;
    for (i=0; a != null; a = a.next, i++);
    return i;
  }


  // Aus den AusgabeDaten eine Tabelle machen
  static String[][] createAusgabeTabelle(AusgabeDaten ad, boolean withTitle) {
    String[][] t = new String[count(ad.ae) + (withTitle ? 1 : 0) ][ad.tabellenTitel.length];
    AusgabeEintrag ae = ad.ae;
    if (withTitle) t[0] = ad.tabellenTitel;
    for (int i=(withTitle ? 1 : 0); i<t.length; i++) {
      int c=0;
      if (ae.nr != null) t[i][c++] = ae.nr;
      if (ae.name != null) t[i][c++] = ae.name;
      if (ae.jahrgang != null) t[i][c++] = ae.jahrgang;
      if (ae.status != null) t[i][c++] = ae.status;
      if (ae.bezeichnung != null) t[i][c++] = ae.bezeichnung;
      if (ae.km != null && ae.km[0] != null) t[i][c++] = ae.km[0];
      if (ae.rudkm != null && ae.rudkm[0] != null) t[i][c++] = ae.rudkm[0];
      if (ae.stmkm != null && ae.stmkm[0] != null) t[i][c++] = ae.stmkm[0];
      if (ae.fahrten != null && ae.fahrten[0] != null) t[i][c++] = ae.fahrten[0];
      if (ae.kmfahrt != null && ae.kmfahrt[0] != null) t[i][c++] = ae.kmfahrt[0];
      if (ae.dauer != null && ae.dauer[0] != null) t[i][c++] = ae.dauer[0];
      if (ae.kmh != null && ae.kmh[0] != null) t[i][c++] = ae.kmh[0];
      if (ae.anzversch != null) t[i][c++] = ae.anzversch;
      if (ae.wafaKm != null) t[i][c++] = ae.wafaKm;
      if (ae.zielfahrten != null) t[i][c++] = ae.zielfahrten;
      if (ae.zusatzDRV != null) t[i][c++] = ae.zusatzDRV;
      if (ae.zusatzLRVBSommer != null) t[i][c++] = ae.zusatzLRVBSommer;
      if (ae.zusatzLRVBWinter != null) t[i][c++] = ae.zusatzLRVBWinter;
      if (ae.zusatzLRVBrbWanderWett != null) t[i][c++] = ae.zusatzLRVBrbWanderWett;
      if (ae.zusatzLRVBrbFahrtenWett != null) t[i][c++] = ae.zusatzLRVBrbFahrtenWett;
      if (ae.zusatzLRVMVpWanderWett != null) t[i][c++] = ae.zusatzLRVMVpWanderWett;
      if (ae.fahrtenbuch != null)
        for (int j=0; j<ae.fahrtenbuch.length; j++) if (ae.fahrtenbuch[j] != null) t[i][c++] = ae.fahrtenbuch[j];
      if (ae.ww != null)
        for (int j=0; j<ae.ww.length; j++) t[i][c++] = ae.ww[j];
      ae = ae.next;
    }
    return t;
  }

  static String[][] createAusgabeTabelle(TabellenFolgenEintrag tfe) {
    int c = 0;
    int maxWidth = 0;
    for (TabellenFolgenEintrag col = tfe; col != null; col = col.next) {
      c++;
      if (col.fields != null && col.fields.length > maxWidth) maxWidth = col.fields.length;
    }
    String[][] t = new String[c][maxWidth];
    c=0;
    for (TabellenFolgenEintrag col = tfe; col != null; col = col.next) {
      int j = (col.colspan-1) / 2;
      for (int i=0; i < maxWidth; i++) {
        if (col.fields != null && i-j>=0 && i-j<col.fields.length) t[c][i] = EfaUtil.replace(col.fields[i-j],"\n"," ",true);
        else t[c][i] = "";
      }
      c++;
    }
    return t;
  }


  // Tabelle t auf Felder mit gleicher Länge bringen
  static int normalizeAusgabeTabelle(String[][] t) {
    int[] maxBreite = new int[t[0].length];
    for (int x=0; x<t[0].length; x++) maxBreite[x] = 0;
    for (int x=0; x<t[0].length; x++)
      for (int y=0; y<t.length; y++)
        if (t[y] != null && t[y][x] != null && t[y][x].length() > maxBreite[x]) maxBreite[x] = t[y][x].length();
    for (int x=0; x<t[0].length; x++)
      for (int y=0; y<t.length; y++)
        if (t[y] != null && t[y][x] != null) {
            while (t[y][x].length() < maxBreite[x]) t[y][x] += " ";
        }
    int totalLength = 0;
    for (int x=0; x<t[0].length; x++) totalLength += maxBreite[x];
    return totalLength;
  }



  // Wettbewerbsbedingungen ausgeben
  static String[] createAusgabeBedingungen(StatistikDaten sd, String bezeich, Hashtable fett, Hashtable kursiv) {
    if (!sd.ausgebenWettBedingung) return null;

    Vector _zeil = new Vector(); // Zeilen

    BufferedReader f;
    String s;
    String dir = Daten.efaCfgDirectory;
    try {
      if (! new File(dir+Daten.WETTFILE).isFile() && new File(Daten.efaProgramDirectory+Daten.WETTFILE).isFile())
        dir=Daten.efaProgramDirectory;
      f = new BufferedReader(new InputStreamReader(new FileInputStream(dir+Daten.WETTFILE),Daten.ENCODING_ISO));
      while ((s = f.readLine()) != null) {
        if (s.startsWith("["+bezeich+"]")) {
          while ((s = f.readLine()) != null) {
            if (s.length() > 0 && s.charAt(0) == '[') break;
            if (s.length() > 0 && s.charAt(0) == '*') {
              fett.put(new Integer(_zeil.size()),"fett");
              s = s.substring(1,s.length());
            }
            if (s.length() > 0 && s.charAt(0) == '#') {
              kursiv.put(new Integer(_zeil.size()),"kursiv");
              s = s.substring(1,s.length());
            }
            if (s.length()>0) s = EfaUtil.replace(s,"%Y+",Integer.toString(sd.wettJahr+1),true);
            if (s.length()>0) s = EfaUtil.replace(s,"%Y",Integer.toString(sd.wettJahr),true);
            _zeil.add(s);
          }
        }
      }
      f.close();
    } catch(FileNotFoundException e) {
        Dialog.error(LogString.logstring_fileNotFound(dir+Daten.WETTFILE, International.getString("Wettbewerbskonfiguration")));
    } catch(IOException e) {
        Dialog.error(LogString.logstring_fileReadFailed(dir+Daten.WETTFILE, International.getString("Wettbewerbskonfiguration")));
    }
    String[] zeilen = new String[_zeil.size()];
    _zeil.toArray(zeilen);
    return zeilen;
  }

  static boolean execCmd(String cmd) {
    try {
      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();
    } catch(Exception e) {
      return false;
    }
    return true;
  }


}
