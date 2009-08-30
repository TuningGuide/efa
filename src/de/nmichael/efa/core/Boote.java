package de.nmichael.efa.core;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Liste der Boote, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

import de.nmichael.efa.*;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import java.io.*;
import java.util.*;

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

  // Konstruktor
  public Boote(String pdat) {
    super(pdat,_ANZFELDER,1,false);
    kennung = KENNUNG170;
  }


  // Key-Wert ermitteln
  public String constructKey(DatenFelder d) {
    String k = d.get(key);
    if (!d.get(VEREIN).equals("")) k = k+" ("+d.get(VEREIN)+")";
    return k;
  }


  public static String getDetailBezeichnung(DatenFelder boot) {
    if (boot == null) return null;
    String bezeichnung = boot.get(ART);
    if (Daten.bezeichnungen == null ||
        (!bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_SKIFF)) &&
         !bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_WHERRY)) &&
         !bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_TRIMMY)) &&
         !bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_BARKE)) &&
         !bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_KIRCHBOOT)) &&
         !bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_ERGO)) &&
         !bezeichnung.equals(Daten.bezeichnungen.bArt.get(Bezeichnungen.BART_MOTORBOOT)) ) )
       bezeichnung += " " + boot.get(Boote.RIGGER) + "-" + boot.get(Boote.ANZAHL) + " " + boot.get(Boote.STM);
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
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG170;
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

}
