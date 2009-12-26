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
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.config.EfaTypes;
import java.io.*;
import java.util.*;

// @i18n complete

public class Boote extends DatenListe {

  public static final int NAME = 0;
  public static final int VEREIN = 1;
  public static final int ART = 2;
  public static final int ANZAHL = 3;
  public static final int RIGGER = 4;
  public static final int STM = 5;
  public static final int GRUPPEN = 6;
  public static final int MAX_NICHT_IN_GRUPPE = 7;
  public static final int MIND_1_IN_GRUPPE = 8;
  public static final int FREI1 = 9;
  public static final int FREI2 = 10;
  public static final int FREI3 = 11;

  public static final int _ANZFELDER = 12;

  public static final String KENNUNG060 = "##EFA.060.BOOTE##";
  public static final String KENNUNG170 = "##EFA.170.BOOTE##";
  public static final String KENNUNG190 = "##EFA.190.BOOTE##";

  // Konstruktor
  public Boote(String pdat) {
    super(pdat,_ANZFELDER,1,false);
    kennung = KENNUNG190;
  }


  // Key-Wert ermitteln
  public String constructKey(DatenFelder d) {
    String k = d.get(key);
    if (!d.get(VEREIN).equals("")) k = k+" ("+d.get(VEREIN)+")";
    return k;
  }


  public static String getDetailBezeichnung(DatenFelder boot) {
    if (boot == null || Daten.efaTypes == null) return null;
    String bezeichnung = International.getMessage("{boattype} {riggering}-{numrowers} {coxedornot}", 
            Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT,      boot.get(Boote.ART)),
            Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING,   boot.get(Boote.RIGGER)),
            Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, boot.get(Boote.ANZAHL)),
            Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING,    boot.get(Boote.STM)));
    return bezeichnung;
  }

  // Einträge auf Gültigkeit prüfen
  public void validateValues(DatenFelder d) {
    String s;
    if ( (s = d.get(NAME)).indexOf("[")>=0 ) d.set(NAME, EfaUtil.replace(s,"[","",true) );
    if ( (s = d.get(NAME)).indexOf("]")>=0 ) d.set(NAME, EfaUtil.replace(s,"]","",true) );
    if ( (s = d.get(VEREIN)).indexOf("[")>=0 ) d.set(VEREIN, EfaUtil.replace(s,"[","",true) );
    if ( (s = d.get(VEREIN)).indexOf("]")>=0 ) d.set(VEREIN, EfaUtil.replace(s,"]","",true) );
  }

  public static String makeGruppen(String gruppe1, String gruppe2, String gruppe3, String gruppe4, String gruppe5) {
    String g = "";
    if (gruppe1.trim().length()>0) g += (g.length()>0 ? "; " : "") + EfaUtil.removeSepFromString(EfaUtil.removeSepFromString(gruppe1.trim(),";"));
    if (gruppe2.trim().length()>0) g += (g.length()>0 ? "; " : "") + EfaUtil.removeSepFromString(EfaUtil.removeSepFromString(gruppe2.trim(),";"));
    if (gruppe3.trim().length()>0) g += (g.length()>0 ? "; " : "") + EfaUtil.removeSepFromString(EfaUtil.removeSepFromString(gruppe3.trim(),";"));
    if (gruppe4.trim().length()>0) g += (g.length()>0 ? "; " : "") + EfaUtil.removeSepFromString(EfaUtil.removeSepFromString(gruppe4.trim(),";"));
    if (gruppe5.trim().length()>0) g += (g.length()>0 ? "; " : "") + EfaUtil.removeSepFromString(EfaUtil.removeSepFromString(gruppe5.trim(),";"));
    return g;
  }

  public static Vector getGruppen(DatenFelder d) {
    Vector v = EfaUtil.split(d.get(Boote.GRUPPEN),';');
    for (int i=0; i<v.size(); i++) v.set(i,((String)v.get(i)).trim());
    return v;
  }

  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN: 060 -> 170
        if (s != null && s.trim().startsWith(KENNUNG060)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"060");
          iniList(this.dat,11,1,false); // Rahmenbedingungen von v1.7.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s = s+"||||||";
              DatenFelder d = constructFields(s);
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

        // KONVERTIEREN: 170 -> 190
        if (s != null && s.trim().startsWith(KENNUNG170)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"170");
          iniList(this.dat,11,1,false); // Rahmenbedingungen von v1.9.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              String art = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_BOAT, d.get(ART));
              String anz = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_NUMROWERS, d.get(ANZAHL));
              String rig = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_RIGGING, d.get(RIGGER));
              String stm = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_COXING, d.get(STM));
              if (art == null) {
                  art = EfaTypes.TYPE_BOAT_OTHER;
                  Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORCONVERTING,
                          getFileName() + ": " +
                          International.getMessage("Fehler beim Konvertieren von Eintrag '{key}'!",constructKey(d)) + " " +
                          International.getMessage("Unbekannte Eigenschaft '{original_property}' korrigiert zu '{new_property}'.",
                          d.get(ART), Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, art)));
              }
              if (anz == null) {
                  anz = EfaTypes.TYPE_NUMROWERS_OTHER;
                  Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORCONVERTING,
                          getFileName() + ": " +
                          International.getMessage("Fehler beim Konvertieren von Eintrag '{key}'!",constructKey(d)) + " " +
                          International.getMessage("Unbekannte Eigenschaft '{original_property}' korrigiert zu '{new_property}'.",
                          d.get(ANZAHL), Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, art)));
              }
              if (rig == null) {
                  rig = EfaTypes.TYPE_RIGGING_OTHER;
                  Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORCONVERTING,
                          getFileName() + ": " +
                          International.getMessage("Fehler beim Konvertieren von Eintrag '{key}'!",constructKey(d)) + " " +
                          International.getMessage("Unbekannte Eigenschaft '{original_property}' korrigiert zu '{new_property}'.",
                          d.get(RIGGER), Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, art)));
              }
              if (stm == null) {
                  stm = EfaTypes.TYPE_COXING_OTHER;
                  Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORCONVERTING,
                          getFileName() + ": " +
                          International.getMessage("Fehler beim Konvertieren von Eintrag '{key}'!",constructKey(d)) + " " +
                          International.getMessage("Unbekannte Eigenschaft '{original_property}' korrigiert zu '{new_property}'.",
                          d.get(STM), Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, art)));
              }
              d.set(ART, art);
              d.set(ANZAHL, anz);
              d.set(RIGGER, rig);
              d.set(STM, stm);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG190;
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

}
