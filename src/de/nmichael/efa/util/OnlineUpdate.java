/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.Daten;
import de.nmichael.efa.gui.OnlineUpdateDialog;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;
import javax.swing.JDialog;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

public class OnlineUpdate {

    public static boolean runOnlineUpdate(JDialog parent, String eouFile) {
        Vector<OnlineUpdateInfo> versions = null;

        // Online Update
        if (!Dialog.okAbbrDialog(International.getString("Online-Update"),
                International.getString("Prüfen auf neue Programmversion") + "\n\n" +
                International.getString("Bitte stelle eine Verbindung zum Internet her und klicke OK."))) {
            return false;
        }

        // aktuelle Versionsnummer aus dem Internet besorgen
        String versionFile = Daten.efaTmpDirectory + "eou.xml";
        if (!DownloadThread.getFile(parent, eouFile, versionFile, true)) {
            return false;
        }

        try {
            XMLReader parser = EfaUtil.getXMLReader();
            OnlineUpdateFileParser ou = new OnlineUpdateFileParser();
            parser.setContentHandler(ou);
            parser.parse(versionFile);
            versions = ou.getVersions();
        } catch (Exception ee) {
            Dialog.error(International.getString("Keine neue Version gefunden!")
                    + "\n" + ee.getMessage());
            EfaUtil.deleteFile(versionFile);
            return false;
        }
        if (versions == null || versions.size() == 0) {
            Dialog.error(International.getString("Keine neue Version gefunden!"));
            EfaUtil.deleteFile(versionFile);
            return false;
        }

        if (Daten.efaConfig != null) {
            Daten.efaConfig.setValueEfaVersionLastCheck(System.currentTimeMillis());
        }

        // ist die installierte Version aktuell?
        OnlineUpdateInfo newestVersion = versions.get(0); // first version is always newest one!
        if (Daten.VERSIONID.equals(newestVersion.versionId) ||
            Daten.VERSIONID.compareTo(newestVersion.versionId) > 0) {
            Dialog.infoDialog(International.getString("Es liegt derzeit keine neuere Version von efa vor.") + "\n"
                    + International.getMessage("Die von Dir benutzte Version {version} ist noch aktuell.",
                    Daten.VERSIONID));
            EfaUtil.deleteFile(versionFile);
            return true;
        }

        // Ok, es gibt eine neue Version --> Infos über diese Version einlesen
        Vector<String> changes = new Vector<String>();
        for (int i=0; i<versions.size(); i++) {
            OnlineUpdateInfo version = versions.get(i);
            if (Daten.VERSIONID.compareTo(version.versionId) >= 0) {
                break;
            }
            changes.addAll(version.getChanges());
        }

        // Ok, Informationen gelesen: Jetzt auf dem Bildschirm anzeigen
        OnlineUpdateDialog dlg = new OnlineUpdateDialog(parent,
                newestVersion.versionId, newestVersion.downloadSize, changes);
        dlg.showDialog();
        if (!dlg.getDialogResult()) {
            return false;
        }

        // Ok, jetzt pruefen, ob Benutzer Schreibrechte im efa-Directory hat
        String writeTestFile = Daten.efaMainDirectory + "writetest.tmp";
        boolean canWrite = true;
        try {
            EfaUtil.deleteFile(writeTestFile); // just to make sure there is no such file
            if (!(new File(writeTestFile)).createNewFile()) {
                canWrite = false;
            }
        } catch (Exception e) {
            canWrite = false;
        } finally {
            EfaUtil.deleteFile(writeTestFile);
        }
        if (!canWrite) {
            Dialog.error(LogString.logstring_directoryNoWritePermission(Daten.efaMainDirectory, International.getString("Verzeichnis")) +
                    "\n\n" +
                    International.getMessage("Bitte wiederhole das Online-Update als {osname}-Administrator.", Daten.osName));
            return false;
        }

        // Download des Updates
        String zipFile = Daten.efaTmpDirectory + "eou.zip";
        ExecuteAfterDownload afterDownload = new ExecuteAfterDownloadImpl(parent,
                zipFile, newestVersion.downloadSize);
        if (parent != null) {
            parent.setEnabled(false);
            if (!DownloadThread.getFile(parent, newestVersion.downloadUrl, zipFile, afterDownload)) {
                return false;
            }
        }
        return true;
    }

}

class OnlineUpdateFileParser extends XmlHandler {

    public static String XML_ONLINEUPDATE = "efaOnlineUpdate";
    public static String XML_VERSION = "Version";
    public static String XML_VERSION_ID = "VersionID";
    public static String XML_RELEASE_DATE = "ReleaseDate";
    public static String XML_DOWNLOAD_URL = "DownloadUrl";
    public static String XML_DOWNLOAD_SIZE = "DownloadSize";
    public static String XML_CHANGES = "Changes";
    public static String XML_CHANGES_PROPERTY_LANG = "lang";
    public static String XML_CHANGE_ITEM = "ChangeItem";

    private OnlineUpdateInfo version;
    private String changeItemLang;
    private Vector<OnlineUpdateInfo> versions = new Vector<OnlineUpdateInfo>();

    public OnlineUpdateFileParser() {
        super(XML_ONLINEUPDATE);
    }

    public Vector<OnlineUpdateInfo> getVersions() {
        return versions;
    }

    public void startElement(String uri, String localName, String qname, Attributes atts) {
        super.startElement(uri, localName, qname, atts);

        if (localName.equals(XML_VERSION)) {
            version = new OnlineUpdateInfo();
        }

        if (localName.equals(XML_CHANGES)) {
            changeItemLang = atts.getValue(XML_CHANGES_PROPERTY_LANG);
        }
    }

    public void endElement(String uri, String localName, String qname) {
        super.endElement(uri, localName, qname);

        if (version != null) {
            // end of field
            if (fieldName.equals(XML_VERSION_ID)) {
                version.versionId = fieldValue;
            }
            if (fieldName.equals(XML_RELEASE_DATE)) {
                version.releaseDate = fieldValue;
            }
            if (fieldName.equals(XML_DOWNLOAD_URL)) {
                version.downloadUrl = fieldValue;
            }
            if (fieldName.equals(XML_DOWNLOAD_SIZE)) {
                version.downloadSize = EfaUtil.stringFindInt(fieldValue, 0);
            }

            if (localName.equals(XML_CHANGE_ITEM)) {
                if (changeItemLang == null || changeItemLang.length() == 0) {
                    changeItemLang = "en";
                }
                Vector<String> changes = version.changeItems.get(changeItemLang);
                if (changes == null) {
                    changes = new Vector<String>();
                }
                changes.add(fieldValue);
                version.changeItems.put(changeItemLang, changes);
            }
            if (fieldName.equals(XML_VERSION)) {
                versions.add(version);
            }
        }

    }

}


class ExecuteAfterDownloadImpl implements ExecuteAfterDownload {

    Window parent;
    String zipFile;
    long fileSize;

    public ExecuteAfterDownloadImpl(Window parent, String zipFile, long fileSize) {
        this.parent = parent;
        this.zipFile = zipFile;
        this.fileSize = fileSize;
    }

    public void success() {
        parent.setEnabled(true);
        File f = new File(zipFile);
        if (f.length() < fileSize) {
            Dialog.error(International.getString("Update abgebrochen!") + "\n" +
                    International.getString("Der Download ist unvollständig."));
            return;
        }
        if (f.length() > fileSize) {
            Dialog.error(International.getString("Update abgebrochen!") + "\n" +
                    International.getString("Der Download ist unvollständig."));
            return;
        }

        // Download war erfolgreich
        Dialog.infoDialog(International.getString("Der Download war erfolgreich.") + "\n" +
                International.getString("Es werden jetzt alle Daten gesichert und anschließend die neue Version installiert."));

        // ZIP-Archiv mit bisherigen Daten sichern
        // @todo (P4) replace by new Backup Functionality
        Vector sourceDirs = new Vector();
        Vector inclSubdirs = new Vector();
        sourceDirs.add(Daten.efaDataDirectory);
        inclSubdirs.add(new Boolean(true));
        String backup = Daten.efaBakDirectory + "Backup_" + Daten.VERSIONID + "_beforeUpdate.zip";
        String result = EfaUtil.createZipArchive(sourceDirs, inclSubdirs, backup);
        if (result != null) {
            if (Dialog.yesNoDialog(International.getString("Fehler bei Datensicherung"),
                    International.getMessage("Sicherung der Daten in der Datei {filename} fehlgeschlagen: {error}", backup,result) +
                    "\n" +
                    International.getString("Soll der Update-Vorgang trotzdem fortgesetzt werden?")) != Dialog.YES) {
                return;
            }
        }

        // Neue Version entpacken
        result = EfaUtil.unzip(zipFile, Daten.efaMainDirectory);
        if (result != null) {
            if (result.length() > 1000) {
                result = result.substring(0, 1000);
            }
            Dialog.error(International.getString("Die Installation der neuen Version ist fehlgeschlagen.") +
                    "\n" + result);
            return;
        }

        // Erfolgreich
        Dialog.infoDialog(International.getString("Version aktualisiert"),
                International.getString("Die Installation des Updates wurde erfolgreich abgeschlossen.") + "\n"
                +
                International.getString("efa wird nun neu gestartet."));
        if (Daten.program != null) {
            Daten.haltProgram(Daten.program.restart());
        } else {
            Daten.haltProgram(Daten.HALT_SHELLRESTART);
        }
    }

    public void failure(String text) {
        parent.setEnabled(true);
        Dialog.infoDialog("Der Download der neuen Version ist fehlgeschlagen: " + text);
    }
}
