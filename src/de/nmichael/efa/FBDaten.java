package de.nmichael.efa;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

// Fahrtenbuch-Zusatzdaten (individuell für jedes FB verwaltet!)
public class FBDaten {

  public String bootDatei;        // Boote-Dateiname
  public String mitgliederDatei;  // Mitglieder-Dateiname
  public String zieleDatei;       // Ziele-Dateiname
  public String statistikDatei;   // gespeicherte Statistikeinstellungen
  public Boote boote;             // Boote
  public Mitglieder mitglieder;   // Mitglieder
  public Ziele ziele;             // Ziele
  public StatSave statistik;      // gespeicherte Statistikeinstellungen
  public boolean erstVorname;     // "Vorname Nachname" oder "Nachname, Vorname"
  public String[] status;         // Liste von "Status"
  public int anzMitglieder;       // Anzahl der Mitglieder am 01.01. des Jahres

  public FBDaten() {
    bootDatei="";
    mitgliederDatei="";
    zieleDatei="";
    statistikDatei="";
    boote=null;
    mitglieder=null;
    ziele=null;
    statistik=null;
    erstVorname=true;
    status = new String[2];
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.gast != null && Daten.bezeichnungen.gast.length()>0) {
      status[0] = Daten.bezeichnungen.gast;
    } else {
      status[0] = "Gast";
    }
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.andere != null && Daten.bezeichnungen.andere.length()>0) {
      status[1] = Daten.bezeichnungen.andere;
    } else {
      status[1] = "andere";
    }
    anzMitglieder=0;
  }

  public FBDaten(FBDaten d) {
    bootDatei = d.bootDatei;
    mitgliederDatei = d.mitgliederDatei;
    zieleDatei = d.zieleDatei;
    statistikDatei = d.statistikDatei;
    boote = d.boote;
    mitglieder = d.mitglieder;
    ziele = d.ziele;
    statistik = d.statistik;
    erstVorname = d.erstVorname;
    status = d.status;
    anzMitglieder = d.anzMitglieder;
  }

}
