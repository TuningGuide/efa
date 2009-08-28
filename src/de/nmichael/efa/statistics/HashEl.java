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


// Ein Element der Hashtable
class HashEl {


  public String jahrgang;
  public String status;
  public String bezeichnung;
  public int rudKm; // Ruder-Kilometer
  public int stmKm; // Steuer-Kilometer
  public int mannschKm; // Mannschafts-Kilometer
  public int dauer; // Fahrtdauer
  public int anz;   // Anzahl der Fahrten
  public ZielfahrtFolge zf; // Zielfahrten
  public Hashtable ww; // Wer mit Wem bzw. Wer Wohin
  public String[] fahrtenbuch; // Art: Fahrtenbuch
  public KmWettInfo kmwett; // Infos zu Km-Wettbewerben

  public HashEl() {
    this.jahrgang = "";
    this.status = "";
    this.bezeichnung = "";
    this.rudKm = 0;
    this.stmKm = 0;
    this.mannschKm = 0;
    this.dauer = 0;
    this.anz = 0;
    this.zf = null;
    this.ww = null;
    this.fahrtenbuch = null;
    this.kmwett = null;
  }

  public HashEl(String jahrgang, String status, String bezeichnung, int rudKm, int stmKm, int mannschKm, int dauer, int anz, ZielfahrtFolge zf, Hashtable ww, String[] fahrtenbuch, KmWettInfo kmwett) {
    this.jahrgang = jahrgang;
    this.status = status;
    this.bezeichnung = bezeichnung;
    this.rudKm = rudKm;
    this.stmKm = stmKm;
    this.mannschKm = mannschKm;
    this.dauer = dauer;
    this.anz = anz;
    this.zf = zf;
    this.ww = ww;
    this.fahrtenbuch = fahrtenbuch;
    this.kmwett = kmwett;
  }

}
