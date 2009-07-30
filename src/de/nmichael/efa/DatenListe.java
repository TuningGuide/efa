package de.nmichael.efa;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import de.nmichael.efa.direkt.Admin;
import de.nmichael.efa.direkt.AdminLoginFrame;
import de.nmichael.efa.direkt.EfaDirektFrame;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  DatenListe - Oberklasse für Boote, Mitglieder, Ziele, Fahrtenbuch
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class DatenListe {

  protected static final int CT_UNCHANGED = 0;
  protected static final int CT_ONLYONEAPPENDED = 1;
  protected static final int CT_CHANGED = 2;

  protected String dat;       // Dateiname
  protected String kennung;   // Datei-Kennung im Kopf der Datei
  protected int felder;       // Anzahl der Datenfelder in Datei, getrennt durch "|"
  protected int key;          // Feld, das als Key verwendet werden soll
  private   BufferedReader f; // Datei zum Lesen
  private   BufferedWriter ff;// Datei zum Schreiben
  protected SortedList l;     // Inhalt der Datei als sortierte Liste
  protected int changeType;   // ob Daten geändert wurden
  protected boolean writeProtect; // ob die Datei schreibgeschützt ist
  protected boolean backup;   // ob die Datei ein Backup ist
  protected String password;  // Paßwort, um den Schreibschutz aufzuheben
  private boolean temporarilyWriteAllowed = false; // damit nach Setzten des Schreibschutzes ein Schreiben möglich ist
  protected boolean backupEnabled = true; // ob von dieser Datei Backups angelegt werden sollen oder nicht
  protected int backupFailures = 0; // Anzahl der in Folge fehlgeschlagenen Backups
  private int scn;            // Change Number: Wird am Konstruktion bei jeder verändernden Operation hochgezählt

  private   String checksum;  // String, der als Checksumme in der Datei gefunden wurde
  private static final int HASHLENGTH = 11+40; // ##CHECKSUM=<40 Characters Hash>


  // Konstruktor; dat: Datei; pf: Anzahl der Datenfelder; pk: Position des Key-Feldes
  public DatenListe(String pdat, int pf, int pk, boolean numeric) {
    iniList(pdat,pf,pk,numeric);
    writeProtect = false;
    backup = false;
    backupFailures = 0;
    password = null;
    scn = 0;
  }


  // Initialisierung (normalerweise nur vom Konstruktor oder bei Konvertierung gerufen)
  public synchronized void iniList(String pdat, int pf, int pk, boolean numeric) {
    dat = pdat;
    kennung = "";
    felder = pf;
    key = pk-1; // Felder zählen ab Null. Als Parameter wird 1 für erstes Feld angegeben
    l = new SortedList(numeric);
    changeType = CT_UNCHANGED;
    scn++;
  }


  protected synchronized void openf() throws FileNotFoundException {
    try {
      f = new BufferedReader(new InputStreamReader(new FileInputStream(dat),Daten.ENCODING));
    } catch(UnsupportedEncodingException e) {
      f = new BufferedReader(new FileReader(dat));
    }
    checksum = "";
  }

  protected synchronized void resetf() throws IOException {
    if (f != null) f.reset();
  }

  protected synchronized boolean validChecksum() {
    if (checksum == null) return true; // file has not previously been saved
    String error = null;
    try {
      if (!(new File(dat).exists())) return true; // file does not exist
      RandomAccessFile file = new RandomAccessFile(dat, "r");
      if (file.length() == 0) { // file is empty
        file.close();
        return true;
      }
      if (file.length() < 51) { // ##CHECKSUM=<40 digits>
        file.close();
        return false;
      }
      file.seek(file.length() - 40); // only 40 Checksum Digits
      String hash = file.readLine();
      file.close();
      if (hash == null || hash.length() == 0) {
        error = "Keine Checksumme in Datei gefunden!";
      }
      else {
        if (!checksum.equals(hash)) {
          error = "Checksumme ist ungültig (Datei: " + hash + " -- Erwartet: " + checksum + ")";
        }
      }
    } catch(Exception e) {
      error = e.toString();
    }
    if (error != null) {
      Logger.log(Logger.ERROR,"Fehler beim Schreiben der Datei " + dat + ": " + error+ " (Ein anderes Programm hat die Datei verändert. Um inkonsistente Änderungen zu vermeiden, wird efa die Datei NICHT speichern.)");
      switch (Daten.actionOnChecksumSaveError) {
        case Daten.CHECKSUM_SAVE_NO_ACTION:
          error = null;
          break;
        case Daten.CHECKSUM_SAVE_PRINT_ERROR:
          Dialog.error("Fehler beim Schreiben der Datei " + dat + ":\n"+
                       error + "\n"+
                       "Ein anderes Programm hat die Datei verändert.\n"+
                       "Um inkonsistente Änderungen zu vermeiden, wird efa\n"+
                       "die Datei " + dat + " NICHT speichern.");
          break;
        case Daten.CHECKSUM_SAVE_HALT_PROGRAM:
          Dialog.infoDialog("Fataler Fehler","Um Dateninkonsistenz zu vermeiden, beendet sich efa JETZT.\nBitte informiere den Administrator.");
          Logger.log(Logger.ERROR,"Möglicherweise laufen zwei Instanzen von efa zeitgleich. Um Inkonsistenzen zu vermeiden, beendet sich efa JETZT.");
          Logger.log(Logger.INFO,"PROGRAMMENDE durch efa (PANIC)");
          System.exit(7);
          break;
      }
    }
    return error == null;
  }

  protected synchronized void openfW() throws IOException {
    openfW(false);
  }
  private synchronized void openfW(boolean append) throws IOException {
    try {
      ff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dat,append),Daten.ENCODING));
    } catch(UnsupportedEncodingException e) {
      ff = new BufferedWriter(new FileWriter(dat,append));
    }
    checksum = "";
  }

  protected synchronized String freadLine() throws IOException {
    String s = f.readLine();
    while (s != null && s.startsWith("##CHECKSUM=")) {
      checksum = s.substring(11,s.length());
      s = f.readLine();
    }
    return s;
  }

  protected synchronized void fwrite(String s) throws IOException {
    ff.write(s);
  }

  protected synchronized void fclose(boolean checkHash) throws IOException {
    f.close();

    if (!checkHash) return;

    File _f = new File(dat);
    String hash = EfaUtil.getSHA(_f,(int)_f.length()-this.HASHLENGTH);

    if (hash != null && checksum != null && !checksum.equals(hash)) {
      if (checksum.length()==0) checksum = "<leer>";
        switch(Daten.actionOnChecksumLoadError) {
          case Daten.CHECKSUM_LOAD_NO_ACTION:
               break;
          case Daten.CHECKSUM_LOAD_PRINT_WARNING:
               Logger.log(Logger.WARNING,"Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmt nicht. Die Datei wurde von einem externen Programm verändert.");
               break;
          case Daten.CHECKSUM_LOAD_SHOW_WARNING:
               Logger.log(Logger.WARNING,"Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmt nicht. Die Datei wurde von einem externen Programm verändert.");
               Dialog.meldung("Warnung","Die Prüfsumme der Datei\n"+dat+"\nstimmt nicht. Die Datei wurde von einem externen Programm verändert.");
               break;
          case Daten.CHECKSUM_LOAD_REQUIRE_ADMIN:
               Logger.log(Logger.ERROR,"Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmt nicht. Die Datei wurde von einem externen Programm verändert.");
               Dialog.meldung("Warnung","Die Prüfsumme der Datei\n"+dat+"\nstimmt nicht. Die Datei wurde von einem externen Programm verändert.\n"+
                                        "Um nicht mit unbefugt manipulierten Daten weiterzuarbeiten,\n"+
                                        "stellt efa hiermit den Dienst ein, bis es vom Super-Admin\n"+
                                        "wieder freigeschaltet wird.");
               Admin admin = AdminLoginFrame.login(Dialog.frameCurrent(),"Datei-Prüfsummenfehler: Freischalten von efa",EfaConfig.SUPERADMIN);
               if (admin == null) EfaDirektFrame.haltProgram("Programmende, da Datei-Prüfsummenfehler vorliegt und Admin-Login nicht erfolgreich war.");
               String oldChecksum = checksum;
               if (writeFile(false,true)) {
                 Dialog.meldung("Hinweis","Die Prüfsumme der Datei\n"+dat+"\nstimmte nicht. Die Datei wurde von einem externen Programm verändert.\n"+
                                "efa hat die Datei jetzt neu geschrieben und eine neue Prüfsumme berechnet.\n"+
                                "Die alte Prüfsumme lautete: "+oldChecksum);
                 Logger.log(Logger.INFO,"Die Datei '"+dat+"' wurde neu geschrieben; ihre neue Prüfsumme lautet "+checksum+".");
               } else {
                 Dialog.error("Die Datei '"+dat+"'\n konnte nicht geschrieben werden!");
               }
               break;
          case Daten.CHECKSUM_LOAD_PRINT_WARNING_AND_AUTO_REWRITE:
               if (writeFile(false,true)) {
                 String s = "Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmte nicht.\n"+
                            "Die Datei wurde von einem externen Programm verändert.\n"+
                            "efa hat die Prüfsumme nun korrigiert.";
                 Logger.log(Logger.WARNING,s);
                 Dialog.infoDialog(s);
                } else {
                 String s = "Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmt nicht.\n"+
                            "Die Datei wurde von einem externen Programm verändert.\n"+
                            "Der Versuch, die Prüfsumme zu korrigieren und die Datei neu zu schreiben, schlug fehl.";
                 Logger.log(Logger.WARNING,s);
                 Dialog.infoDialog("Warnung",s);
                }
               break;
          case Daten.CHECKSUM_LOAD_HALT_PROGRAM:
               Logger.log(Logger.ERROR,"Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmt nicht. Das Programm wurde angehalten.");
               System.exit(7);
               break;
          default:
               Logger.log(Logger.ERROR,"Die Prüfsumme "+checksum+" der Datei '"+dat+"' stimmt nicht. Das Programm wurde angehalten.");
               System.exit(7);
               break;
        }
//      }
    }
  }

  protected synchronized void fcloseW() throws IOException {
    ff.close();
    String hash = EfaUtil.getSHA(new File(dat));
    try {
      ff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dat,true),Daten.ENCODING));
    } catch(UnsupportedEncodingException e) {
      ff = new BufferedWriter(new FileWriter(dat,true));
    }
    ff.write("##CHECKSUM="+hash);
    ff.close();
    checksum = hash;
  }


  // neuen Dateinamen festlegen (für Speichern unter)
  public synchronized void setFileName(String fname) {
    if (dat != null && fname != null && !dat.equals(fname)) // nur bei geänderten Dateinamen
      if (!Daten.fileSep.equals("\\") || !dat.toLowerCase().equals(fname.toLowerCase())) { // unter Win: case insensitive
        writeProtect = false;
        backup = false;
        password = null;
      }
    dat = fname;
    changeType = CT_CHANGED;
  }


  // Dateinamen ermitteln
  public synchronized String getFileName() {
    return this.dat;
  }


  public synchronized boolean writeAllowed(boolean fuerKonvertieren) {
    if (temporarilyWriteAllowed) return true;
    if (writeProtect) {
      // Schreibschutz entfernen?
      int c;
      if ( (c = Dialog.removeWriteProtection(dat,fuerKonvertieren)) != Dialog.WRITE_IGNORE && c != Dialog.WRITE_REMOVE) return false;

      // Ja, Schreibschutz soll übergangen oder entfernt werden

      // Paßwort abfragen, falls gesetzt
      if (!enterCorrectPassword(null)) return false;

      // ok, richtiges Paßwort eingegeben
      if (c == Dialog.WRITE_REMOVE) {
        // Schreibschutz entfernen
        writeProtect = false;
        password = null;
      }
    }
    return true;
  }

  // ermittelt, ob die Datei schreibgeschützt ist
  public synchronized boolean isWriteProtected() {
    return this.writeProtect;
  }

  // ermittelt, ob die Datei durch ein Paßwort schreibgeschützt ist
  public synchronized boolean isPassword() {
    return this.writeProtect && this.password != null;
  }

  // bittet den Benutzer um Angabe eines korrekten Paßworts, wobei der Parameter pwd als erster Versuch genommen wird, sofern != null
  public synchronized boolean enterCorrectPassword(String pwd) {
    if (password == null) return true;
    if (EfaUtil.getSHA(Daten.defaultWriteProtectPw) != null && password.equals(EfaUtil.getSHA(Daten.defaultWriteProtectPw))) return true;
    if (pwd != null && password.equals(EfaUtil.getSHA(pwd))) return true;
    do {
      pwd = Dialog.getWriteProtectionPasswort(dat,pwd==null);
    } while (pwd != null && !password.equals(EfaUtil.getSHA(pwd)));
    if (pwd == null) return false;
    return true;
  }

  // setzt den Schreibschutz der Datei "von außen". Ist sie paßwortgeschützt, so wird ein Paßwort verlangt (per default als 1. Versuch wird das zu setzende probiert)
  public synchronized boolean setWriteProtect(boolean protect, String pwd) {
    if (this.writeProtect && this.password != null)
      if (!enterCorrectPassword(pwd)) return false;

    this.writeProtect = protect;
    if (protect)
      this.password = (pwd == null || pwd.equals("") ? null : EfaUtil.getSHA(pwd));
    else this.password = null;

    this.temporarilyWriteAllowed = true;
    boolean ret = writeFile();
    this.temporarilyWriteAllowed = false;
    return ret;
  }


  // Key-Wert ermitteln
  public synchronized String constructKey(DatenFelder d) {
    return d.get(key);
  }


  // Splitte Felder anhand Trennzeichen "|"
  public synchronized DatenFelder constructFields(String s) {
    return new DatenFelder(felder,s.trim());
  }


  // Diese Methode kann von abgeleiteten Klassen überschrieben werden
  public void validateValues(DatenFelder d) {
  }


  // Eintrag zur Liste hinzufügen
  public synchronized DatenFelder add(String s) {
    scn++;
    return add(constructFields(s));
  }


  // Eintrag hinzufügen
  public synchronized DatenFelder add(DatenFelder d) {
    validateValues(d);
    if (d.get(0).startsWith("##CHECKSUM")) d.set(0,d.get(0).substring(10,d.get(0).length())); // nur zur Sicherheit!!
    String key = constructKey(d);
    if (key.equals("")) return null;
    boolean addedAsLast = l.put(key,d); // Eintrag abspeichern
    if (addedAsLast && changeType == CT_UNCHANGED) changeType = CT_ONLYONEAPPENDED;
    else changeType = CT_CHANGED;
    scn++;
    return d;
  }


  // Eintrag löschen
  public synchronized boolean delete(String s) {
    if (l.delete(s)) {
      scn++;
      changeType = CT_CHANGED;
      return true;
    } else return false;
  }


  // Anzahl der Elemente ermitteln
  public synchronized int countElements() {
    return l.countElements();
  }


  // Dateiformat überprüfen (ggf. überschrieben durch Unterklassen)
  public synchronized boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {
        Dialog.error("Datei '"+dat+"' hat ungültiges Format!");
        fclose(false);
        return false;
      }
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht gelesen werden!");
      return false;
    }
    return true;
  }


  // ermitteln, ob Schreibschutzt gesetzt ist (wird direkt bei openFile() aufgerufen)
  public synchronized boolean getWriteProtect() {
    writeProtect=false;
    backup=false;
    password=null;
    String s;
    try {
      s = freadLine();
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht gelesen werden!");
      return false;
    }
    if (s == null) return true;

    int pos;
    if (s.indexOf("%%WRITEPROTECT%%")>=0) writeProtect=true;
    if (s.indexOf(Backup.BACKUP)>=0) backup=true;
    if ( (pos = s.indexOf("%%PASSWORD=")) >= 0) {
      s = s.substring(pos+11);
      pos = s.indexOf("%%");
      if (pos>=0) s = s.substring(0,pos);
      if (s.length()>0) {
        password = s;
        writeProtect=true;
      }
    }
    return true;
  }

  // Schreibschutz-Informationen in die Datei schreiben (wird direkt in openWFile() aufgerufen)
  public synchronized void writeHeader() throws IOException {
    fwrite(kennung);
    if (writeProtect) {
      fwrite(" %%WRITEPROTECT%%");
      if (password != null)
        fwrite(" %%PASSWORD="+password+"%%");
    }
    fwrite(" - Bitte nicht von Hand bearbeiten!\n");
  }

  // Datei zum Lesen öffnen
  public synchronized final boolean openFile() {
    String s;

    // Versuchen, die Datei zu öffnen
    if (dat == null) {
      Dialog.error("Datei (#) kann nicht geöffnet werden!");
      return false;
    }
    try {
      openf();
      try {
        f.mark(8192);
        if (!getWriteProtect()) return false;
        if (backup && Daten.actionOnDatenlisteIsBackup == Daten.BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN) {
          Logger.log(Logger.WARNING,"Die Datei '"+dat+"'ist eine Sicherungskopie (Backup).");
          Dialog.error("Die Datei '"+dat+"'\n"+
                       "ist eine Sicherungskopie (Backup). Um sie wieder zu benutzen,\n"+
                       "ist die Zustimmung des Administrators notwendig.");
          Admin admin = AdminLoginFrame.login(Dialog.frameCurrent(),"Backupdatei "+dat+" benutzen?",EfaConfig.SUPERADMIN);
          if (admin == null) EfaDirektFrame.haltProgram("Programmende, da Datenliste '"+dat+"' eine Sicherungskopie (Backup) ist und die Verwendung eines Backups durch den Administrator genehmigt werden muß.");
        }
        resetf();
      } catch(IOException e) {
        return false;
      }
    } catch(FileNotFoundException e) {
      switch (Dialog.DateiErstellen(dat)) {
        case Dialog.YES: {
          if (Daten.actionOnDatenlisteNotFound == Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN) {
            Admin admin = AdminLoginFrame.login(Dialog.frameCurrent(),"Datenliste neu erstellen",EfaConfig.SUPERADMIN);
            if (admin == null) EfaDirektFrame.haltProgram("Programmende, da Datenliste '"+dat+"' nicht gefunden und Admin-Login nicht erfolgreich war.");
            Logger.log(Logger.INFO,"Datenliste '"+dat+"' neu erstellt.");
          }
          if (!openWFile(false) || !closeWFile()) return false; // neue Datei erstellen
          try {
            openf();
          } catch(FileNotFoundException ee) {
            return false;
          }
          break; }
        default: {
          if (Daten.actionOnDatenlisteNotFound == Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN) {
            EfaDirektFrame.haltProgram("Programmende, da Datenliste '"+dat+"' nicht gefunden.");
          }
          Dialog.error("Datei '"+dat+"' nicht gefunden!");
          return false;
        }
      }
    }

    // Dateiformat überprüfen
    if (!checkFileFormat()) return false;

    changeType = CT_UNCHANGED;
    return true;
  }


  // gesamnten Inhalt der Datei einlesen
  public synchronized boolean _readFile() {
    String s;

    l.clear(); // ggf. alte Liste löschen

    // Datei lesen
    try {
      while ((s = freadLine()) != null) {
        s = s.trim();
        if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
        add(s);

      }
    } catch(IOException e) {
      Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
      return false;
    }
    changeType = CT_UNCHANGED;
    return true;
  }


  // Datei (lesen) schließen
  public synchronized boolean closeFile() {
    try {
      fclose(true);
      return true;
    } catch(IOException e) {
      Dialog.error("Schliessen der Datei '"+dat+"' fehlgeschlagen!");
      return false;
    }
  }


  // Datei, die nur temporär geöffnet wurde, wieder schließen, ohne den Hash zu vergleichen
  protected synchronized boolean closeFileWithoutHash() {
    try {
      f.close();
      return true;
    } catch(IOException e) {
      return false;
    }
  }


  // Datei zum Schreiben öffnen
  public synchronized final boolean openWFile(boolean fuerKonvertieren) {
    return openWFile(fuerKonvertieren,false,false);
  }
  private synchronized final boolean openWFile(boolean fuerKonvertieren, boolean append) {
    return openWFile(fuerKonvertieren,append,false);
  }
  private synchronized final boolean openWFile(boolean fuerKonvertieren, boolean append, boolean force) {
    if (!writeAllowed(fuerKonvertieren)) {
      Dialog.error("Die Datei wurde nicht gespeichert!");
      return false;
    }

    // ggf. ein Backup erzeugen
    if (Daten.backup != null && this.backupEnabled) {
      if (backupFailures < 10) {
        backupFailures += 10; // weil sonst rekursiver Aufruf dazu führt, daß backupFailures nie erhöht wird
        if (Daten.backup.create(dat, Backup.SAVE, null)) backupFailures=0;
        else backupFailures-=9; // also insg. plus 1
      } else {
        Logger.log(Logger.WARNING,"Wegen zu vieler fehlgeschlagener Backups wurde kein Backup von '"+dat+"' angelegt. Ich versuche es später erneut.");
        backupFailures++;
      }
      if (backupFailures == 30) backupFailures=9; // mal wieder versuchen...
    }

    // Versuchen, die Datei zu öffnen
    if (!force && !validChecksum()) {
      Dialog.error("Datei '"+dat+"' konnte nicht erstellt werden!");
      return false;
    }
    boolean success = false;
    while (true) {
      try {
        openfW(append);
        success = true;
        break;
      } catch(IOException e) {
        File f = new File(dat);
        if (f.isFile() && !f.canWrite()) {
          if (!Dialog.okAbbrDialog("Datei ist schreibgeschützt",
                                   "Datei '"+dat+"' ist schreibgeschützt und kann von efa nicht überschrieben werden!\n"+
                                   "Bitte entferne den Schreibschutz und versuche es erneut.")) break;
        } else break;
      }
    }
    if (!success) {
      Dialog.error("Datei '"+dat+"' konnte nicht erstellt werden!");
      return false;
    }

    if (append) return true; // bei append schon hier aufhören; keinen Header schreiben!

    // Dateikennung schreiben
    try {
      writeHeader();
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht geschrieben werden!");
      return false;
    }

    return true;
  }


  // gesamnten Inhalt der Datei schreiben
  public synchronized boolean _writeFile() {
    DatenFelder d;
    String s;
    d = (DatenFelder)l.getCompleteFirst();
    if (d != null) do {
      s = "";
      for (int i=0; i<felder; i++) {
        s = s + ( d.get(i) == null ? "" : d.get(i) );
        if (i+1 < felder) s = s + "|";
      }
      try {
        fwrite(s+"\n");
      } catch(IOException e) {
        Dialog.error("Schreiben der Datei '"+dat+"' fehlgeschlagen!");
        return false;
      }
    } while ( (d = (DatenFelder)l.getCompleteNext()) != null);

    changeType = CT_UNCHANGED;
    return true;
  }


  // Datei (schreiben) schließen
  public synchronized boolean closeWFile() {
    try {
      fcloseW();
      return true;
    } catch(IOException e) {
      Dialog.error("Schliessen der Datei '"+dat+"' fehlgeschlagen!");
      return false;
    }
  }


  public synchronized boolean readEinstellungen() {
    return true; // to be overwritten by classes extending this class (if needed)
  }

  public synchronized boolean writeEinstellungen() {
    return true; // to be overwritten by classes extending this class (if needed)
  }

  // Datei öffnen und lesen
  public synchronized boolean readFile() {
    if (openFile() && readEinstellungen() && _readFile() && closeFile()) return true;
    return false;
  }


  // Datei öffnen und schreiben
  public synchronized boolean writeFile(boolean fuerKonvertieren, boolean force) {
    if (Daten.DONT_SAVE_ANY_FILES_DUE_TO_OOME) {
      Logger.log(Logger.WARNING,"Änderungen an der Datei "+getFileName()+" konnten wegen Speicherknappheit NICHT gesichert werden.");
      return false;
    }
    if (openWFile(fuerKonvertieren, false, force) && writeEinstellungen() && _writeFile() && closeWFile()) return true;
    else return false;
  }
  public synchronized boolean writeFile(boolean fuerKonvertieren) {
    return writeFile(fuerKonvertieren,fuerKonvertieren); // this is *not* a typo! when writeFile is called for converting files, the force mode needs to be enabled as well!
  }
  public synchronized boolean writeFile() {
    return writeFile(false);
  }

  // Datei so speichern, daß nur der letzte Record geschrieben werden muß. Dies ist nur möglich,
  // falls seit dem letzten Speichern als einzige Änderung genau ein Record hinzugefügt wurde.
  // Diese Methode dient der Performance-Steigerung für efaDirekt, da dort i.d.R. immer nur
  // jeweils ein Datensatz angefügt wird
  public synchronized boolean writeFileOnlyLastRecordChanged() {
    if (!writeAllowed(false)) {
      Dialog.error("Die Datei wurde nicht gespeichert!");
      return false;
    }

    if (changeType != CT_ONLYONEAPPENDED) return writeFile();

    if (!validChecksum()) {
      Dialog.error("Datei '"+dat+"' konnte nicht geschrieben werden!");
      return false;
    }
    try {
      // Checksumme am Dateiende abschneiden
      RandomAccessFile file = new RandomAccessFile(dat,"rw");
      if (file.length()<52) {
        file.close();
        return writeFile(); // Datei kürzer als erwartet
      }
      file.seek(file.length()-52);
      byte lf = file.readByte();
      if ((lf != 10 && lf != 13) || file.readByte() != '#') {
        file.close();
        return writeFile(); // keine Checksumme an erwarteter Stelle
      }
      file.setLength(file.length()-51); // truncate: Checksumme abschneiden
      file.close();

      // Datei öffnen im Append-Modus (checksumme nicht überprüfen, da zuvor abgeschnitten!)
      if (!openWFile(false,true,true)) return false;

      // Letzten Datensatz in die Datei schreiben
      DatenFelder d = (DatenFelder)l.getCompleteLast();
      String s;
      if (d != null) {
        s = "";
        for (int i=0; i<felder; i++) {
          s = s + ( d.get(i) == null ? "" : d.get(i) );
          if (i+1 < felder) s = s + "|";
        }
        fwrite(s+"\n");
      }

      // Datei schließen, Hash berechnen und Checksumme anfügen
      if (!closeWFile()) return false;
      changeType = CT_UNCHANGED;
    } catch(Exception e) {
      return false;
    }
    return true;
  }


  // Der Reihe nach jeweils einen Eintrag liefern
  public synchronized String get() {
    return l.get();
  }


  // Ersten Eintrag liefern, der mit "such" beginnt
  // wie getFirst(such), nur daß "such" nicht als letzter Suchbegriff nicht gespeichert wird und somit
  // kein Weitersuchen möglich ist, aber insb. auch nicht das nächste Weitersuchen durch diesen Ruf beeinflußt wird
  public synchronized String selectFirst(String such) {
    return l.selectStartsWith(such);
  }


  // Ersten Eintrag liefern, der mit "such" beginnt
  public synchronized String getFirst(String such) {
    return l.startsWith(such);
  }


  // Nächsten Eintrag liefern, der mit "such" beginnt
  public synchronized String getNext(String such) {
    return l.nextStartsWith(such);
  }


  // Vorherigen Eintrag liefern, der mit "such" beginnt
  public synchronized String getPrev(String such) {
    return l.prevStartsWith(such);
  }


  // Letzten Eintrag liefern, der mit "such" beginnt
  public synchronized String getLast(String such) {
    return l.lastStartsWith(such);
  }


  // Nächsten Eintrag liefern
  public synchronized String getNext() {
    return l.next();
  }


  // Vorherigen Eintrag liefern
  public synchronized String getPrev() {
    return l.prev();
  }


  // gesamten aktuellen Datensatz liefern
  public synchronized DatenFelder getComplete() {
    return (DatenFelder)l.getComplete();
  }


  // gesamten 1. Datensatz liefern
  public synchronized DatenFelder getCompleteFirst() {
    return (DatenFelder)l.getCompleteFirst();
  }


  // gesamten nächsten Datensatz liefern
  public synchronized DatenFelder getCompleteNext() {
    return (DatenFelder)l.getCompleteNext();
  }


  // gesamten vorherigen Datensatz liefern
  public synchronized DatenFelder getCompletePrev() {
    return (DatenFelder)l.getCompletePrev();
  }


  // gesamten letzten Datensatz liefern
  public synchronized DatenFelder getCompleteLast() {
    return (DatenFelder)l.getCompleteLast();
  }


  // exakten Treffer liefern
  public synchronized String getExact(String such) {
    return l.getExact(such);
  }


  // ersten kompletten Treffer liefern
  public synchronized DatenFelder getCompleteFirst(String such) {
    if (l.startsWith(such)==null) return null;
    return (DatenFelder)l.getComplete();
  }


  // exakten Treffer liefern
  public synchronized DatenFelder getExactComplete(String such) {
    if (l.getExact(such)==null) return null;
    return (DatenFelder)l.getComplete();
  }

  // Suchstrings löschen, damit nächste Suche eine neue Suche ist
  public synchronized void clearSearch() {
    l.clearSearch();
  }

  // zu einem bestimmden Datensatz springen
  public synchronized void goTo(String such) {
    l.goTo(such);
  }


  // legt fest, ob Groß- und Kleinschreibung beim Suchen ignoriert werden soll
  public synchronized void ignoreCase(boolean ic) {
    l.ignoreCase(ic);
  }


  // setzt changed auf true (z.B. wenn irgendwelche Zusatzdaten von außen geändert wurde)
  public synchronized void setChanged() {
    changeType = CT_CHANGED;
  }


  // gibt zurück, ob die Daten geändert und noch nicht gespeichert wurden
  public synchronized boolean isChanged() {
    return changeType != CT_UNCHANGED;
  }

  // gibt zurück, ob die Liste leer ist
  public synchronized boolean isEmpty() {
    return l.isEmpty();
  }

  // alle Daten der Liste (in Form der Keys) zurückgeben
  public synchronized String[] getData() {
    String[] data = new String[countElements()];
    DatenFelder d = getCompleteFirst();
    for (int i=0; i<data.length; i++) {
      if (d != null) data[i] = constructKey(d);
      d = getCompleteNext();
    }
    return data;
  }

  // speichere lastElement, um es später mit restoreLastElement wiederherzustellen
  public synchronized void saveLastElement() {
    l.saveLastElement();
  }


  // stelle den Wert von lastElement, wie er zuvor mittels saveLastElement gesichert wurde, wieder her
  public synchronized void restoreLastElement() {
    l.restoreLastElement();
  }


  // liefere die System Change Number
  public int getSCN() {
    return scn;
  }

 /**
   * Creates a vector containing all neigbours of a String. The distance
   * is measured by using EditDistance - number of keboard-hits to transform
   * rower into neighbour.

   * @param rower String who's neighbours are searched
   * @param radius
   * @return vector containing DatenFelder as elements.
   * @author Thil A. Coblenzer
   */
  public synchronized Vector getNeighbours(String rower, int radius) {
          Vector neighbours = new Vector();
          String neighboursName;
          DatenFelder neighbour = (DatenFelder) l.getCompleteFirst();
          while (neighbour != null) {
                  neighboursName = this.constructKey(neighbour);
                  if (EditDistance.getDistance(neighboursName,rower) <= radius)
                          neighbours.add(neighbour);

                  neighbour = (DatenFelder) l.getCompleteNext();
          }

          if (neighbours.size() == 0) return null;
          else return neighbours;
  }


}
