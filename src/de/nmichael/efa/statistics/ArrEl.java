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

// Ein Element des Arrays
class ArrEl implements Comparable {

  public static int KM_TYP_ALL = 0;
  public static int KM_TYP_RUD = 1;
  public static int KM_TYP_STM = 2;

  public static int sortierKriterium;
  public static int sortierFolge;
  public static boolean sortVorNachname;
  public static boolean gaesteHinten; // Gäste mit Status _GAST_ hinten einsortieren
  public static int kmTypSortKrit;

  public String name;
  public String jahrgang;
  public String status;
  public String bezeichnung;
  public int rudKm;
  public int stmKm;
  public int mannschKm;
  public int dauer;
  public int anz;
  ZielfahrtFolge zf;
  public Hashtable ww;
  public String[] fahrtenbuch;
  public KmWettInfo kmwett;

  public ArrEl(String name, String jahrgang, String status, String bezeichnung, int rudKm, int stmKm, int mannschKm, int dauer, int anz, ZielfahrtFolge zf, Hashtable ww, String[] fahrtenbuch, KmWettInfo kmwett) {
    this.name = name;
    this.jahrgang = jahrgang;
    this.status = status;
    this.bezeichnung = bezeichnung;
    this.stmKm = stmKm;
    this.rudKm = rudKm;
    this.mannschKm = mannschKm;
    this.dauer = dauer;
    this.anz = anz;
    this.zf = zf;
    this.ww = ww;
    this.fahrtenbuch = fahrtenbuch;
    this.kmwett = kmwett;
  }

  public int compareTo(Object o) throws ClassCastException {
    ArrEl b = (ArrEl)o;

    if (gaesteHinten) {
      if (this.status.equals(Statistik.ANDERE) && !b.status.equals(Statistik.ANDERE)) return 1;
      if (b.status.equals(Statistik.ANDERE) && !this.status.equals(Statistik.ANDERE)) return -1;
    }
    if (gaesteHinten) {
      if (this.status.equals(Statistik.GAST) && !b.status.equals(Statistik.GAST)) return 1;
      if (b.status.equals(Statistik.GAST) && !this.status.equals(Statistik.GAST)) return -1;
    }

    int aufab; // um aufsteigen/absteigend zu sortieren
    if (sortierFolge == StatistikDaten.SORTFOLGE_AB) aufab = -1;
    else aufab = 1;

    switch (sortierKriterium) {
      case StatistikDaten.SORTKRIT_NACHNAME:
        if (sortVorNachname)
          return EfaUtil.getNachname(this.name).toUpperCase().compareTo(EfaUtil.getNachname(b.name).toUpperCase()) * aufab;
        else
          return this.name.toUpperCase().compareTo(b.name.toUpperCase()) * aufab;
      case StatistikDaten.SORTKRIT_VORNAME:
        return EfaUtil.getVorname(this.name).toUpperCase().compareTo(EfaUtil.getVorname(b.name).toUpperCase()) * aufab;
      case StatistikDaten.SORTKRIT_JAHRGANG:
        return this.jahrgang.compareTo(b.jahrgang) * aufab;
      case StatistikDaten.SORTKRIT_KM:
        int this_km = (kmTypSortKrit == KM_TYP_RUD ? this.rudKm : (kmTypSortKrit == KM_TYP_STM ? this.stmKm : this.rudKm + this.stmKm));
        int b_km = (kmTypSortKrit == KM_TYP_RUD ? b.rudKm : (kmTypSortKrit == KM_TYP_STM ? b.stmKm : b.rudKm + b.stmKm));
        if (this_km > b_km) return 1 * aufab;
        else if (this_km < b_km) return -1 * aufab;
        else return 0;
      case StatistikDaten.SORTKRIT_FAHRTEN:
        if (this.anz > b.anz) return 1 * aufab;
        else if (this.anz < b.anz) return -1 * aufab;
        else return 0;
      case StatistikDaten.SORTKRIT_KMFAHRT:
        this_km = this.rudKm + this.stmKm; // da "Fahrten" immer die gesamten Fahrten sind, müssen auch hier immer die gesamten Kilometer herangezogen werden
        b_km = b.rudKm + b.stmKm;          // da "Fahrten" immer die gesamten Fahrten sind, müssen auch hier immer die gesamten Kilometer herangezogen werden
        if ( EfaUtil.div(this_km,this.anz) > EfaUtil.div(b_km,b.anz) ) return 1 * aufab;
        else if ( EfaUtil.div(this_km,this.anz) < EfaUtil.div(b_km,b.anz) ) return -1 * aufab;
        else return 0;
      case StatistikDaten.SORTKRIT_DAUER:
        if (this.dauer > b.dauer) return 1 * aufab;
        else if (this.dauer < b.dauer) return -1 * aufab;
        else return 0;
      case StatistikDaten.SORTKRIT_KMH:
        if ( EfaUtil.fdiv(this.rudKm+this.stmKm,this.dauer) > EfaUtil.fdiv(b.rudKm+b.stmKm,b.dauer) ) return 1 * aufab;
        else if ( EfaUtil.fdiv(this.rudKm+this.stmKm,this.dauer) < EfaUtil.fdiv(b.rudKm+b.stmKm,b.dauer) ) return -1 * aufab;
        else return 0;
      case StatistikDaten.SORTKRIT_ANZVERSCH:
        if (this.ww != null && b.ww != null && this.ww.size() > b.ww.size()) return 1 * aufab;
        else if (this.ww != null && b.ww != null && this.ww.size() < b.ww.size()) return -1 * aufab;
        else return 0;
      case StatistikDaten.SORTKRIT_STATUS:
        if (this.status == null && b.status == null) return 0;
        if (this.status == null || b.status == null) return aufab;
        return this.status.compareTo(b.status) * aufab;
      default: return 0;
    }
  }

}
