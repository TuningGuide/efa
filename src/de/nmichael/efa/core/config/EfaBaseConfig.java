/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.core.config;

import de.nmichael.efa.*;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.EfaUtil;
import java.io.*;

// @i18n complete

public class EfaBaseConfig {

    private String filename;
    public String efaUserDirectory; // Verzeichnis für alle User-Daten von efa (daten, cfg, tmp)
    public String language;         // Sprache

    private String normalize(String sin) {
        String sout = "";
        for (int i = 0; sin != null && i < sin.length(); i++) {
            char c = sin.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                sout += c;
            }
        }
        return sout;
    }

    public void setEfaConfigUserHomeFilename(String dir) {
        filename = dir + ".efa_" + normalize(Daten.efaMainDirectory);
    }

    // Konstruktor
    public EfaBaseConfig(String dir) {
        setEfaConfigUserHomeFilename(dir);
        if (Logger.isTraceOn(Logger.TT_CORE)) {
            Logger.log(Logger.DEBUG, Logger.MSG_CORE_BASICCONFIG, "EfaBaseConfig=" + filename);
        }
        reset();
    }

    public boolean efaCanWrite(String path, boolean createDir) {
        if (!path.endsWith(Daten.fileSep)) {
            path += Daten.fileSep;
        }
        String testfile = "efa.test.file";
        try {
            if (createDir) {
                File f = new File(path);
                f.mkdirs();
            }
            BufferedWriter f = new BufferedWriter(new FileWriter(path + testfile));
            f.write("efa can write!");
            f.close();
            EfaUtil.deleteFile(path + testfile);
        } catch (Exception e) {
            if (Logger.isTraceOn(Logger.TT_CORE)) {
                Logger.log(Logger.DEBUG, Logger.MSG_CORE_BASICCONFIG, "efaCanWrite(" + path + ") = false: " + e.toString());
            }
            return false;
        }
        return true;
    }

    public boolean trySetUserDir(String dir, boolean createDir) {
        if (dir == null || dir.length() == 0) {
            return false;
        }
        dir = dir.trim();
        if (!dir.endsWith(Daten.fileSep)) {
            dir = dir + Daten.fileSep;
        }
        if (efaCanWrite(dir, createDir)) {
            efaUserDirectory = dir;
            Logger.log(Logger.DEBUG, Logger.MSG_CORE_BASICCONFIG, "efa.dir.user=" + efaUserDirectory);
            return true;
        }
        return false;
    }

    public String getFileName() {
        return filename;
    }

    // Einstellungen zurücksetzen
    void reset() {
        language = null;
        efaUserDirectory = Daten.userHomeDir + (!Daten.userHomeDir.endsWith(Daten.fileSep) ? Daten.fileSep : "") + Daten.EFA_USERDATA_DIR + Daten.fileSep;
        if (!trySetUserDir(efaUserDirectory, true)) {
            efaUserDirectory = Daten.efaMainDirectory;
            if (!trySetUserDir(efaUserDirectory, false)) {
                efaUserDirectory = null;
                if (Logger.isTraceOn(Logger.TT_CORE)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_CORE_BASICCONFIG, "efa.dir.user=<null>");
                }
            }
        }
    }

    public synchronized boolean readFile() {
        reset();

        // Konfiguration lesen
        BufferedReader f = null;
        String s;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(filename),Daten.ENCODING_UTF));
            while ((s = f.readLine()) != null) {
                s = s.trim();
                if (s.startsWith("USERHOME=")) {
                    String newUserHome = s.substring(9, s.length()).trim();
                    if (efaCanWrite(newUserHome, true)) {
                        efaUserDirectory = newUserHome;
                        if (!efaUserDirectory.endsWith(Daten.fileSep)) {
                            efaUserDirectory += Daten.fileSep;
                        }
                    }
                }
                if (s.startsWith("LANGUAGE=")) {
                    language = s.substring(9).trim();
                }
            }
            f.close();
        } catch (IOException e) {
            Logger.log(e);
            try {
                f.close();
            } catch (Exception ee) {
                return false;
            }
        }
        return true;
    }

    // Konfigurationsdatei speichern
    public synchronized boolean writeFile() {
        // Datei schreiben
        BufferedWriter f = null;
        try {
            f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename,false), Daten.ENCODING_UTF ));
            if (efaUserDirectory != null && efaUserDirectory.length() > 0) {
                f.write("USERHOME=" + efaUserDirectory + "\n");
            }
            if (language != null) {
                f.write("LANGUAGE=" + language + "\n");
            }
            f.close();
        } catch (Exception e) {
            Logger.log(e);
            try {
                f.close();
            } catch (Exception ee) {
                return false;
            }
            return false;
        }
        return true;
    }

}
