package de.nmichael.efa.direkt;

import java.io.*;
import java.util.Vector;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.Daten;
import de.nmichael.efa.util.EfaUtil;


public class NachrichtenAnAdmin extends DatenListe {

  private Vector nachrichten;

  public static final String KENNUNG130 = "##EFA.130.NACHRICHTEN##";

  private static final int MAXSIZE = 262144;
  private static final int MAXMAXSIZE = 2 * MAXSIZE;

  // Konstruktor
  public NachrichtenAnAdmin(String pdat) {
    super(pdat,0,0,false);
    kennung = KENNUNG130;
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
      Nachricht n = new Nachricht(Nachricht.ADMIN,EfaUtil.getCurrentTimeStamp(),"efa",
                    "alte Nachrichten aussortiert",
                    "Da die Nachrichtendatei ihre maximale Größe erreicht hat,\n"+
                    "hat efa soeben alle alten" + (archiveAlsoOldUnread ? "" : ", gelesenen") + " Nachrichten aussortiert.\n"+
                    nArchive.size()+ " alte Nachrichten wurden in die Datei\n"+
                    bakdat+"\n"+
                    "verschoben.");
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
    boolean success = writeFile();

    if (Daten.efaConfig != null && Daten.efaConfig.admins != null) {
      n.sendEmail(Daten.efaConfig.admins);
    }

    return success;
  }

}