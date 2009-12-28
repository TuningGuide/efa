/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import java.io.*;
import java.util.Vector;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;

// @i18n complete

public class NachrichtenAnAdmin extends DatenListe {

  private Vector nachrichten;
  private int errorCount = 0; // to avoid stack overflow when recursively running into errors when writing files, mailing this to the admin and then writing the message file to disk, failing doing so, mailing this to the admin, ...

  public static final String KENNUNG130 = "##EFA.130.NACHRICHTEN##";
  public static final String KENNUNG190 = "##EFA.190.NACHRICHTEN##";

  private static final int MAXSIZE = 262144;
  private static final int MAXMAXSIZE = 2 * MAXSIZE;

  // Konstruktor
  public NachrichtenAnAdmin(String pdat) {
    super(pdat,0,0,false);
    kennung = KENNUNG190;
    reset();
    checkIfSizeExceeded();
  }

  // Einstellungen zurücksetzen
  void reset() {
    nachrichten = new Vector();
  }

  public boolean readEinstellungen() {
    reset();

    // Nachrichten lesen
    Nachricht nachricht = null;

    String s;
    try {
      while ((s = freadLine()) != null) {
        s = s.trim();

        if (nachricht == null && s.equals("@START")) {
          nachricht = new Nachricht();
          continue;
        }

        if (nachricht != null) {
          if (s.equals("@ENDE")) {
            nachrichten.add(nachricht);
            nachricht = null;
            continue;
          }
          if (s.startsWith("@EMPFAENGER=")) {
            nachricht.empfaenger = EfaUtil.string2int(s.substring(12,s.length()).trim(),Nachricht.ADMIN);
            continue;
          }
          if (s.startsWith("@DATUM=")) {
            nachricht.datum = s.substring(7,s.length()).trim();
            continue;
          }
          if (s.startsWith("@NAME=")) {
            nachricht.name = s.substring(6,s.length()).trim();
            continue;
          }
          if (s.startsWith("@BETREFF=")) {
            nachricht.betreff = s.substring(9,s.length()).trim();
            continue;
          }
          if (s.startsWith("@GELESEN=")) {
            nachricht.gelesen = s.substring(9,s.length()).trim().equals("ja");
            continue;
          }
          if (nachricht.nachricht == null) nachricht.nachricht = "";
          nachricht.nachricht += s+"\n";
        }

      }
      if (nachricht != null) nachrichten.add(nachricht);
    } catch(IOException e) {
      try {
        fclose(false);
      } catch(Exception ee) {
        return false;
      }
    }
    return true;
  }




  // Nachrichtendatei speichern
  public boolean writeEinstellungen() {
    // Datei schreiben
    try {
      for (int i=0; i<nachrichten.size(); i++) {
        Nachricht nachricht = (Nachricht)nachrichten.get(i);
        fwrite("@START\n");
        fwrite("@EMPFAENGER="+nachricht.empfaenger+"\n");
        fwrite("@DATUM="+nachricht.datum+"\n");
        fwrite("@NAME="+nachricht.name+"\n");
        fwrite("@BETREFF="+nachricht.betreff+"\n");
        fwrite("@GELESEN="+(nachricht.gelesen ? "ja" : "nein")+"\n");
        fwrite(nachricht.nachricht);
        if (!nachricht.nachricht.endsWith("\n")) fwrite("\n");
        fwrite("@ENDE\n");
      }
    } catch(Exception e) {
      try {
        fcloseW();
      } catch(Exception ee) {
        return false;
      }
      return false;
    }
    return true;
  }


  public void add(Nachricht nachricht) {
    nachrichten.add(nachricht);
  }

  public int size() {
    return nachrichten.size();
  }

  public Nachricht get(int i) {
    return (Nachricht)nachrichten.get(i);
  }

  public boolean delete(int i) {
    if (i<0 || i>nachrichten.size()) return false;
    nachrichten.remove(i);
    return true;
  }


  public boolean checkIfSizeExceeded() {
    File f = new File(dat);
    if (f.exists() && f.length() > MAXSIZE) {
      boolean archiveAlsoOldUnread = f.length() > MAXMAXSIZE;
      if (!readFile()) return false;

      // gelesene von ungelesenen Nachrichten trennen
      Vector alle = nachrichten;
      Vector nArchive = new Vector();
      Vector nKeep = new Vector();
      for (int i=0; i<nachrichten.size(); i++) {
        Nachricht n = (Nachricht)nachrichten.get(i);
        if (n.gelesen) {
          nArchive.add(n);
        } else {
          if (archiveAlsoOldUnread && n.datum != null && Math.abs(EfaUtil.getDateDiff(n.datum,EfaUtil.getCurrentTimeStampDD_MM_YYYY())) > 30) {
            nArchive.add(n);
          } else {
            nKeep.add(n);
          }
        }
      }
      if (nArchive.size() == 0) return false;

      // Dateinamen
      String orgdat = dat;
      String bakdat = Daten.efaBakDirectory + EfaUtil.getFilenameWithoutPath(dat) + EfaUtil.getCurrentTimeStampYYYYMMDD_HHMMSS();

      // alle gelesenen Nachrichten in Backupdatei verschieben
      nachrichten = nArchive;
      dat = bakdat;
      if (!writeFile(false)) {
        // alles rückgängig machen
        dat = orgdat;
        nachrichten = alle;
        return false;
      }

      // Backup war erfolgreich; in aktueller Datei nur ungelesene Nachrichten speichern
      dat = orgdat;
      nachrichten = nKeep;
      Nachricht n = new Nachricht(Nachricht.ADMIN,EfaUtil.getCurrentTimeStamp(),Daten.EFA_SHORTNAME,
                    International.getString("Alte Nachrichten aussortiert"),
                    International.getMessage("Da die Nachrichtendatei ihre maximale Größe erreicht hat, "+
                    "hat efa soeben alle alten Nachrichten aussortiert.\n"+
                    "{count} alte Nachrichten wurden in die Datei "+
                    bakdat+" verschoben.",nArchive.size()));
      add(n);
      writeFile(false,true);
      return true;
    } else return false;
  }

  public boolean createNachricht(String from, int to, String subject, String text) {
    Nachricht n = new Nachricht();
    n.name = from;
    n.empfaenger = to;
    n.betreff = subject;
    n.nachricht = text;

    add(n);

    boolean success = false;
    if (errorCount >= 0 && errorCount < 5) {
      success = writeFile();
    } else {
      if (errorCount > 0) {
        Logger.log(Logger.WARNING, Logger.MSG_WARN_ERRORCNTMSGEXCEEDED,
                International.getMessage("Anzahl von Fehlermeldungen überschritten: {errorcount}. "+
                "Weitere Fehlermeldungen werden vorerst nicht per Nachricht an den Admin zugestellt.",errorCount));
        errorCount = -5; // try again after 5 more attempts
      }
    }
    if (!success) {
      errorCount++;
      if (errorCount == 0) {
        Logger.log(Logger.INFO, Logger.MSG_EVT_ERRORCNTMSGCLEAR,
                International.getString("Fehlermeldungen werden ab jetzt wieder als Nachricht dem Admin zugestellt."));
      }
    } else {
      errorCount=0; // reset errorCount upon first successful write
    }

    if (Daten.efaConfig != null && Daten.efaConfig.admins != null) {
      n.sendEmail(Daten.efaConfig.admins);
    }

    return success;
  }

  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {
        // KONVERTIEREN: 130 -> 190
        if (s != null && s.trim().startsWith(KENNUNG130)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"130");
          iniList(this.dat,0,0,false); // Rahmenbedingungen von v1.9.0 schaffen
          // Datei lesen
          readEinstellungen();
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