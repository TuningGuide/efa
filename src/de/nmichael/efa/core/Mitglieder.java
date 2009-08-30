package de.nmichael.efa.core;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Mitgliederliste, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import java.io.*;
import java.util.Hashtable;

public class Mitglieder extends DatenListe {


  public static final int _ANZAHL = 14;
  public static final int VORNAME = 0;
  public static final int NACHNAME = 1;
  public static final int ALIAS = 2;      // in 060: JAHRGANG
  public static final int JAHRGANG = 3;   // in 060: GESCHLECHT
  public static final int GESCHLECHT = 4; // in 060: STATUS1
  public static final int STATUS = 5;     // in 060: STATUS2
  public static final int VEREIN = 6;
  public static final int BEHINDERUNG = 7;// neu in 110
  public static final int MITGLNR = 8;    // neu in 170
  public static final int PASSWORT = 9;   // neu in 170
  public static final int FREI1 = 10;     // neu in 170
  public static final int FREI2 = 11;     // neu in 170
  public static final int FREI3 = 12;     // neu in 170
  public static final int KMWETT_MELDEN = 13; // neu in 173

  public static final String KENNUNG060 = "##EFA.060.MITGLIEDER##";
  public static final String KENNUNG090 = "##EFA.090.MITGLIEDER##";
  public static final String KENNUNG110 = "##EFA.110.MITGLIEDER##";
  public static final String KENNUNG170 = "##EFA.170.MITGLIEDER##";
  public static final String KENNUNG173 = "##EFA.173.MITGLIEDER##";

  Hashtable aliases=null;       // Alias-Namen der Mitglieder


  // Konstruktor
  public Mitglieder(String pdat) {
    super(pdat,_ANZAHL,1,false);
    kennung = KENNUNG173;
  }


  // Key-Wert ermitteln
  public String constructKey(DatenFelder d) {
    return EfaUtil.getFullName(d.get(VORNAME),d.get(NACHNAME),d.get(VEREIN));
  }


  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {


        // KONVERTIEREN: 060 -> 090
        if (s != null && s.trim().startsWith(KENNUNG060)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"060");
          iniList(this.dat,7,1,false); // Rahmenbedingungen von v0.90 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              s = d.get(0)+"|"+d.get(1)+"||"+d.get(2)+"|"+d.get(3)+"|"; // an Pos 2 "Alias" einfügen
              if (d.get(5).equals("Gast")) s = s+"Gast"+"|";            // status2==Gast nach status übernehmen
              else if (d.get(5).equals("andere")) s = s+"andere"+"|";   // status2==andere nach status übernehmen
              else s = s+d.get(4)+"|";                                  // status1 nach status übernehmen
              s = s+d.get(6);                                           // Vereinsnamen anhängen
              add(s);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG090;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 090 -> 110
        if (s != null && s.trim().startsWith(KENNUNG090)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"090");
          iniList(this.dat,8,1,false); // Rahmenbedingungen von v1.1.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(constructFields(s+"|"));
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG110;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN: 110 -> 170
        if (s != null && s.trim().startsWith(KENNUNG110)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"110");
          iniList(this.dat,13,1,false); // Rahmenbedingungen von v1.7.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(constructFields(s+"||||||"));
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG170;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 170 -> 173
        if (s != null && s.trim().startsWith(KENNUNG170)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"170");
          iniList(this.dat,14,1,false); // Rahmenbedingungen von v1.7.3 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(constructFields(s+"|+"));
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG173;
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


  // einen Alias zur Aliasliste hinzufügen
  public void addAlias(String alias, String name) {
    if (aliases == null) aliases = new Hashtable();
    aliases.put(alias,name);
  }


  // einen Alias aus der Aliasliste entfernen
  public void removeAlias(String alias) {
    if (aliases != null && aliases.get(alias) != null)
      aliases.remove(alias);
  }


  // testet, ob der angegebene Alias bereits verwendet wird
  boolean isAlias(String alias) {
    if (aliases != null) return aliases.get(alias) != null;
    return false;
  }


  // Aliasnamen aus Mitgliederliste holen
  public void getAliases() {
    aliases = new Hashtable();
    DatenFelder d;
    d = (DatenFelder)getCompleteFirst();
    String s;
    while (d != null) {
      if (!( s = d.get(ALIAS).trim()).equals(""))
        addAlias(s,EfaUtil.getFullName(d.get(VORNAME),d.get(NACHNAME),d.get(VEREIN)));
      d = (DatenFelder)getCompleteNext();
    }
  }

  // Einträge auf Gültigkeit prüfen
  public void validateValues(DatenFelder d) {
    String s;
//    if ( !(s = d.get(JAHRGANG)).equals("") ) d.set(JAHRGANG,Integer.toString(EfaUtil.string2date(s,0,0,0).tag));
    if ( !(s = d.get(JAHRGANG)).equals("") ) d.set(JAHRGANG, Integer.toString( EfaUtil.yy2yyyy( EfaUtil.string2date(s,0,0,0).tag ) ) );
  }

  public static boolean isKmwettMelden(DatenFelder d) {
    if (d == null || d.get(KMWETT_MELDEN) == null) return true;
    return !d.get(KMWETT_MELDEN).equals("-");
  }

  public static void setKmwettMelden(DatenFelder d, boolean melden) {
    d.set(KMWETT_MELDEN, (melden ? "+" : "-") );
  }


}
