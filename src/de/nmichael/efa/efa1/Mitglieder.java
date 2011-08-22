/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.efa1;

import de.nmichael.efa.*;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.efa1.DatenFelder;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.io.*;
import java.util.*;

// @i18n complete

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
  public static final String KENNUNG190 = "##EFA.190.MITGLIEDER##";

  public Hashtable aliases=null;       // Alias-Namen der Mitglieder


  // Konstruktor
  public Mitglieder(String pdat) {
    super(pdat,_ANZAHL,1,false);
    kennung = KENNUNG190;
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


        // KONVERTIEREN: 173 -> 190
        if (s != null && s.trim().startsWith(KENNUNG173)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"173");
          iniList(this.dat,14,1,false); // Rahmenbedingungen von v1.9.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              String gender = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_GENDER, d.get(GESCHLECHT));
              if (gender == null) {
                  gender = EfaTypes.TYPE_GENDER_MALE;
                  Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORCONVERTING,
                          getFileName() + ": " +
                          International.getMessage("Fehler beim Konvertieren von Eintrag '{key}'!",constructKey(d)) + " " +
                          International.getMessage("Unbekannte Eigenschaft '{original_property}' korrigiert zu '{new_property}'.",
                          d.get(GESCHLECHT), Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, gender)));
              }
              d.set(GESCHLECHT, gender);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG190;
          if (closeFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }


        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(KENNUNG190)) {
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
  public boolean isAlias(String alias) {
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

  public Vector<String> getAllNames(boolean onlyMembers) {
      Vector<String> names = new Vector<String>();
      DatenFelder d = getCompleteFirst();
      while (d != null) {
          if (!onlyMembers || d.get(Mitglieder.VEREIN).length()==0) {
              String name = EfaUtil.getFullName(d.get(Mitglieder.VORNAME), d.get(Mitglieder.NACHNAME), d.get(Mitglieder.VEREIN));
              names.add(name);
          }
          d = getCompleteNext();
      }
      return names;
  }


}
