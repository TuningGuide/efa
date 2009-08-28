/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.statistics;

import java.util.*;
import de.nmichael.efa.*;

/**
 *
 * @author nick
 */
// Weitere Informationen für Km-Wettbewerbe
class KmWettInfo {

  public int geschlecht = 0; // 0 = m, 1 = w
  public boolean behinderung = false;

  public Zielfahrt[] zielfahrtenFinal = new Zielfahrt[4]; // die vier besten Zielfahrten
  public Zielfahrt[] zielfahrtenAdditional = null;        // beliebig viele weitere Zielfahren
  public Vector zielfahrten = new Vector(); // Vector of Zielfahrt

  public String[][][] winterfahrten = new String[6][31][3]; // 6 * 31 Einträge mit jeweils Datum/Ziel/Km (max. 31 pro Monat)
  public int anzMonate = 0;  // Anzahl der Wintermonate, in denen bereits eine Fahrt gefunden wurde
  public int winterAnz = 0;  // Anzahl der Fahrten an verschiedenen Tagen im Winter

  public Hashtable wafa = new Hashtable(); // mögliche Kandidaten für Mehrtagestouren (DRV)
  public int wafaKm = 0;     // Anzahl der Wanderfahrtkilometer (DRV) (werden nicht automatisch berechnet)

  // Daten für DRV Wanderruderstatistik
  public Hashtable drvWafaStat_etappen = new Hashtable(); // Etappenname(String) -> Kilometer(Integer)
  public int drvWafaStat_gesTage = 0;                           // Anzahl der gesamten Rudertage
  public Hashtable drvWafaStat_teilnMueber18 = new Hashtable(); // Teilnehmername(String) -> Kilometer(Integer)
  public Hashtable drvWafaStat_teilnMbis18   = new Hashtable(); // Teilnehmername(String) -> Kilometer(Integer)
  public Hashtable drvWafaStat_teilnFueber18 = new Hashtable(); // Teilnehmername(String) -> Kilometer(Integer)
  public Hashtable drvWafaStat_teilnFbis18   = new Hashtable(); // Teilnehmername(String) -> Kilometer(Integer)
  public int drvWafaStat_mannschKm = 0;
  public String drvWafaStat_gewaesser = null;                   // befahrene Gewässer

  // Für Brandenburg & Mecklenburg-Vorpommern
  public int gigbootkm = 0;      // Gigboot-Kilometer
  public int gigbootanz = 0;     // Anzahl der Fahrten im Gigboot
  public int gigboot20plus = 0;  // Anzahl der Fahrten im Gigboot mit mind. 20 Kilometern Länge
  public int gigboot30plus = 0;  // Anzahl der Fahrten im Gigboot mit mind. 30 Kilometern Länge
  public Vector gigfahrten = new Vector(); // Vector of String[6] mit LfdNr, Datum, Boot, Ziel, Km, Bemerkungen


}
