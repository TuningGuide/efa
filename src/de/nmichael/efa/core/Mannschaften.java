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
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import java.io.IOException;

public class Mannschaften extends DatenListe {

  public static final int BOOT      =  0;
  public static final int STM       =  1; // Felder STM bis MANNSCH16 müssen fortlaufende Nummern haben!!! (s. MannschaftFrame.show())
  public static final int MANNSCH1  =  2;
  public static final int MANNSCH2  =  3;
  public static final int MANNSCH3  =  4;
  public static final int MANNSCH4  =  5;
  public static final int MANNSCH5  =  6;
  public static final int MANNSCH6  =  7;
  public static final int MANNSCH7  =  8;
  public static final int MANNSCH8  =  9;
  public static final int MANNSCH9  = 10;
  public static final int MANNSCH10 = 11;
  public static final int MANNSCH11 = 12;
  public static final int MANNSCH12 = 13;
  public static final int MANNSCH13 = 14;
  public static final int MANNSCH14 = 15;
  public static final int MANNSCH15 = 16;
  public static final int MANNSCH16 = 17;
  public static final int MANNSCH17 = 18; // neu in v1.4.0
  public static final int MANNSCH18 = 19; // neu in v1.4.0
  public static final int MANNSCH19 = 20; // neu in v1.4.0
  public static final int MANNSCH20 = 21; // neu in v1.4.0
  public static final int MANNSCH21 = 22; // neu in v1.4.0
  public static final int MANNSCH22 = 23; // neu in v1.4.0
  public static final int MANNSCH23 = 24; // neu in v1.4.0
  public static final int MANNSCH24 = 25; // neu in v1.4.0
  public static final int ZIEL      = 26; // vor 1.4.0: 18
  public static final int FAHRTART  = 27; // vor 1.4.0: 19
  public static final int OBMANN    = 28; // neu in v1.7.3

  public static final String NO_FAHRTART = "--- keine Auswahl ---";
  public static final String NO_OBMANN   = "--- keine Auswahl ---";

  public static final String KENNUNG120 = "##EFA.120.MANNSCHAFTEN##";
  public static final String KENNUNG135 = "##EFA.135.MANNSCHAFTEN##";
  public static final String KENNUNG173 = "##EFA.173.MANNSCHAFTEN##";

  // Konstruktor
  public Mannschaften(String pdat) {
    super(pdat,29,1,false);
    kennung = KENNUNG173;
  }



  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN 120 -> 135
        if ( s != null && s.trim().startsWith(KENNUNG120)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"120");
          iniList(this.dat,20,1,true); // Rahmenbedingungen von v1.2.0 schaffen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              d.set(26,d.get(18)); // ZIEL kopieren
              d.set(27,d.get(19)); // FAHRTART kopieren
              for (int i=18; i<=25; i++) d.set(i,""); // neue MANNSCH-Felder initialisieren
              add(d);
            }

          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG135;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN 135 -> 173
        if ( s != null && s.trim().startsWith(KENNUNG135)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"135");
          iniList(this.dat,29,1,true); // Rahmenbedingungen von v1.7.3 schaffen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              d.set(OBMANN,NO_OBMANN);
              add(d);
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



}
