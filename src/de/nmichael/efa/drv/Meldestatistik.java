package de.nmichael.efa.drv;

import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Backup;
import de.nmichael.efa.*;
import de.nmichael.efa.util.Dialog;
import java.io.*;

public class Meldestatistik extends DatenListe {

  public static final int _ANZFELDER = 22;

  public static final int KEY = 0; // VEREINSMITGLNR#VORNAME#NACHNAME#JAHRGANG
  public static final int VEREINSMITGLNR = 1;
  public static final int VEREIN = 2;
  public static final int VORNAME = 3;
  public static final int NACHNAME = 4;
  public static final int JAHRGANG = 5;
  public static final int GESCHLECHT = 6;
  public static final int KILOMETER = 7;
  public static final int GRUPPE = 8;
  public static final int ANZABZEICHEN = 9;
  public static final int ANZABZEICHENAB = 10;
  public static final int AEQUATOR = 11;
  public static final int GESKM = 12;
  public static final int WS_BUNDESLAND = 13;
  public static final int WS_MITGLIEDIN = 14;
  public static final int WS_GEWAESSER = 15;
  public static final int WS_TEILNEHMER = 16;
  public static final int WS_MANNSCHKM = 17;
  public static final int WS_MAENNERKM = 18;
  public static final int WS_JUNIORENKM = 19;
  public static final int WS_FRAUENKM = 20;
  public static final int WS_JUNIORINNENKM = 21;

  public static final String KENNUNG151 = "##EFA.151.MELDESTATISTIK##";
  public static final String KENNUNG160 = "##EFA.160.MELDESTATISTIK##";

  // Konstruktor
  public Meldestatistik(String pdat) {
    super(pdat,_ANZFELDER,1,false);
    kennung = KENNUNG160;
  }


  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN: 151 -> 160
        if (s != null && s.trim().startsWith(KENNUNG151)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"151");
          iniList(this.dat,22,1,true); // Rahmenbedingungen von v160 schaffen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s += "|||||||||";
              add(s);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG160;
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
