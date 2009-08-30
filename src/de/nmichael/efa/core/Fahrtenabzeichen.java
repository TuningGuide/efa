package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import java.io.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Liste der Boote, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class Fahrtenabzeichen extends DatenListe {

  public static final int _ANZ = 8;            // geändert in v1.5.1 (in 1.5.0: 6)

  public static final int VORNAME = 0;
  public static final int NACHNAME = 1;
  public static final int JAHRGANG = 2;
  public static final int ANZABZEICHEN = 3;
  public static final int GESKM = 4;
  public static final int ANZABZEICHENAB = 5;  // neu in v1.5.1
  public static final int GESKMAB = 6;         // neu in v1.5.1
  public static final int LETZTEMELDUNG = 7;   // geändert in v1.5.1 (in 1.5.0: 5)

  public static final String KENNUNG150 = "##EFA.150.FAHRTENABZEICHEN##";
  public static final String KENNUNG151 = "##EFA.151.FAHRTENABZEICHEN##";

  // globale Werte für die Datei
  private String quittungsnummer = null;                     // Quittungsnummer der letzten Meldung
  private int letzteMeldung = 0;                             // Jahr der letzten Meldung
  private boolean bestaetigungsdateiHeruntergeladen = false; // true, wenn die Bestätigungsdatei der letzten Meldung heruntergeladen wurde


  // Konstruktor
  public Fahrtenabzeichen(String pdat) {
    super(pdat,_ANZ,1,false);
    kennung = KENNUNG151;
  }


  // Key-Wert ermitteln
  public String constructKey(DatenFelder d) {
    return d.get(VORNAME)+" "+d.get(NACHNAME);
  }

  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {


        // KONVERTIEREN: 150 -> 151
        if (s != null && s.trim().startsWith(KENNUNG150)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"150");
          iniList(this.dat,8,1,false); // Rahmenbedingungen von v1.5.1 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s= s.trim();
              s = s+"||";
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              d.set(7,d.get(5));
              d.set(5,"0");
              d.set(6,"0");
              add(d);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG151;
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
