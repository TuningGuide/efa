/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa;

import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.EfaUtil;
import java.io.*;

// @i18n complete

public class UserHome extends DatenListe {

  public static final String KENNUNG183 = "##EFA.183.USERHOME##";

  private static String _filename;
  public String efaUserDirectory; // Verzeichnis für alle User-Daten von efa (daten, cfg, tmp)

  public static void setEfaConfigUserHomeFilename(String dir) {
    String fname = "";
    for (int i=0; Daten.efaMainDirectory != null && i<Daten.efaMainDirectory.length(); i++) {
      char c = Daten.efaMainDirectory.charAt(i);
      if ( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') ) fname += c;
    }
    fname = ".efa_"+fname;
    _filename = dir + fname;
  }

  // Konstruktor
  public UserHome() {
    super(_filename,0,0,false);
    kennung = KENNUNG183;
    reset();
    this.backupEnabled = false; // Aus Sicherheitsgründen kein Backup von .efa.cfg anlegen!!
  }

  public boolean efaCanWrite(String path, boolean createDir) {
    String testfile = "efa.test.file";
    try {
      if (createDir) {
        File f = new File(path);
        f.mkdirs();
      }
      BufferedWriter f = new BufferedWriter(new FileWriter(path+testfile));
      f.write("efa can write!");
      f.close();
      EfaUtil.deleteFile(path+testfile);
    } catch(Exception e) {
      Logger.log(Logger.DEBUG,Logger.MSG_CORE_USERHOME,"efaCanWrite("+path+") = false: "+e.toString());
      return false;
    }
    return true;
  }

  // Einstellungen zurücksetzen
  void reset() {
    efaUserDirectory = Daten.efaMainDirectory;
    if (efaCanWrite(efaUserDirectory,false)) {
      Logger.log(Logger.DEBUG,Logger.MSG_CORE_USERHOME,"efa.dir.user="+efaUserDirectory);
    } else {
      efaUserDirectory = Daten.efaUserHome + (!Daten.efaUserHome.endsWith(Daten.fileSep) ? Daten.fileSep : "") + "efa" + Daten.fileSep;
      if (efaCanWrite(efaUserDirectory,true)) {
        Logger.log(Logger.DEBUG,Logger.MSG_CORE_USERHOME,"efa.dir.user="+efaUserDirectory);
      } else {
        efaUserDirectory = null;
        Logger.log(Logger.DEBUG,Logger.MSG_CORE_USERHOME,"efa.dir.user=<null>");
      }
    }
  }

  public synchronized boolean readEinstellungen() {
    reset();

    // Konfiguration lesen
    String s;
    try {
      while ((s = freadLine()) != null) {
        s = s.trim();
        if (s.startsWith("USERHOME=")) {
          String newUserHome = s.substring(9,s.length()).trim();
          if (efaCanWrite(newUserHome,true)) {
            efaUserDirectory = newUserHome;
            if (!efaUserDirectory.endsWith(Daten.fileSep)) efaUserDirectory += Daten.fileSep;
          }
        }
      }
    } catch(IOException e) {
      try {
        fclose(false);
      } catch(Exception ee) {
        return false;
      }
    }
    return true;
  }

  // Konfigurationsdatei speichern
  public synchronized boolean writeEinstellungen() {
    // Datei schreiben
    try {
      if (efaUserDirectory != null && efaUserDirectory.length()>0) {
        fwrite("USERHOME=" + efaUserDirectory + "\n");
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

}
