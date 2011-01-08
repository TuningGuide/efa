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

// @i18n complete

public class AusgabeEintrag {
  int absnr = -1;

  // normale Daten
  String nr = null;
  String name = null;
  String jahrgang = null;
  String status = null;
  String bezeichnung = null;
  String[] km = null;      int colspanKm = 1;
  String[] rudkm = null;   int colspanRudKm = 1;
  String[] stmkm = null;   int colspanStmKm = 1;
  String[] fahrten = null; int colspanFahrten = 1;
  String[] kmfahrt = null; int colspanKmFahrt = 1;
  String[] dauer = null;   int colspanDauer = 1;
  String[] kmh = null;     int colspanKmH = 1;
  String mannschKm = null;
  String anzversch = null;
  String wafaKm = null;
  String zielfahrten = null;
  String zusatzDRV = null;
  String zusatzLRVBSommer = null;
  String zusatzLRVBWinter = null;
  String zusatzLRVBrbWanderWett = null;
  String zusatzLRVBrbFahrtenWett = null;
  String zusatzLRVMVpWanderWett = null;

  // Fahrtenbuch
  String[] fahrtenbuch = null;

  // "Wer mit Wem" und "Wer wohin"
  String[] ww = null;
  boolean[] ww_selbst = null;

  // Wettbewerbe
  String w_name = null;
  String w_jahrgang = null;
  String w_kilometer = null;
  String[][] w_detail = null;
  String w_additional = null;
  String w_attr1 = null;
  String w_attr2 = null;
  String w_warnung = null;
  boolean w_erfuellt = false;




  boolean zusammenfassung = false;

  AusgabeEintrag next = null;


  public AusgabeEintrag() {
    km = new String[3];
    rudkm = new String[3];
    stmkm = new String[3];
    fahrten = new String[3];
    kmfahrt = new String[3];
    dauer = new String[3];
    kmh = new String[3];
  }

}
