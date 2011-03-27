/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.importefa1;

import de.nmichael.efa.gui.ProgressDialog;
import de.nmichael.efa.util.*;
import de.nmichael.efa.efa1.Synonyme;
import de.nmichael.efa.data.storage.DataKey;
import de.nmichael.efa.*;
import java.util.*;

public class ImportTask extends ProgressTask {

    private HashMap<String, ImportMetadata> importData;
    private Hashtable<String,String> synMitglieder;
    private Hashtable<String,String> synBoote;
    private Hashtable<String,String> synZiele;
    private Hashtable<String,String> addresses;
    private Hashtable<DataKey,String> boatsAllowedGroups;
    private Hashtable<DataKey,String> boatsRequiredGroup;
    private Hashtable<String,UUID> groupMapping;

    private String newestLogbookName; // name of the logbook to be opened when this dialog is completed

    public ImportTask(HashMap<String, ImportMetadata> importData) {
        super();
        this.importData = importData;
    }

    public void run() {
        setRunning(true);
        int i = 0;
        int errorCnt = 0;
        int successCnt = 0;
        String logfile = Daten.efaLogDirectory + Daten.fileSep + "import_" + EfaUtil.getCurrentTimeStampYYYYMMDD_HHMMSS() + ".log";
        setLogfile(logfile);
        logInfo(International.getString("Protokoll") + ": " + logfile + "\n");
        String[] keys = importData.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        int totalWarnings = 0;
        int totalErrors = 0;
        for (int run = 1; run <= 4; run++) {
            for (String key : keys) {
                ImportMetadata meta = importData.get(key);
                if (!meta.selected) {
                    continue;
                }
                ImportBase importJob = null;
                if (run == 1) {
                    switch (meta.type) {
                        case ImportMetadata.TYPE_SYNONYME_MITGLIEDER:
                            importJob = new ImportSynonyms(this, key, meta);
                            break;
                        case ImportMetadata.TYPE_SYNONYME_BOOTE:
                            importJob = new ImportSynonyms(this, key, meta);
                            break;
                        case ImportMetadata.TYPE_SYNONYME_ZIELE:
                            importJob = new ImportSynonyms(this, key, meta);
                            break;
                        case ImportMetadata.TYPE_ADRESSEN:
                            importJob = new ImportAddresses(this, key, meta);
                            break;
                    }
                }
                if (run == 2) {
                    switch (meta.type) {
                        case ImportMetadata.TYPE_FAHRTENBUCH:
                            importJob = new ImportLogbook(this, key, meta);
                            newestLogbookName = meta.name;
                            break;
                    }
                }
                if (run == 3) {
                    switch (meta.type) {
                        case ImportMetadata.TYPE_GRUPPEN:
                            importJob = new ImportGroups(this, key, meta);
                            break;
                        case ImportMetadata.TYPE_MANNSCHAFTEN:
                            importJob = new ImportCrews(this, key, meta);
                            break;
                        case ImportMetadata.TYPE_BOOTSTATUS:
                            importJob = new ImportBoatStatus(this, key, meta);
                            break;
                        case ImportMetadata.TYPE_FAHRTENABZEICHEN:
                            importJob = new ImportFahrtenabzeichen(this, key, meta);
                            break;
                        // @todo TYPE_NACHRICHTEN
                    }
                }
                if (run == 4) {
                    // Postprocessing after all data has been imported
                    ImportBoats.runPostprocessing(boatsAllowedGroups, boatsRequiredGroup, groupMapping);

                    break; // exit loop
                }

                boolean result = false;
                if (importJob != null) {
                    result = importJob.runImport();
                    if (result) {
                        successCnt++;
                    } else {
                        errorCnt++;
                    }
                    setCurrentWorkDone(i++);
                    totalWarnings += importJob.getWarningCount();
                    totalErrors += importJob.getErrorCount();
                }
            }
        }
        String msg = International.getMessage("{count} Dateien wurden importiert.", successCnt);
        if (errorCnt > 0) {
            msg += "\n" + International.getMessage("Der Import von {count} Dateien wurde wegen Fehlern abgebrochen.", errorCnt);
        }
        msg += "\n" + International.getMessage("Es traten {count} Warnungen und {count} Fehler auf.", totalWarnings, totalErrors);
        logInfo(International.getString("Import beendet.\n"));
        logInfo(msg+"\n");
        Dialog.infoDialog(msg);
        setDone();
    }

    public void setSynonymeMitglieder(Hashtable<String,String> syn) {
        this.synMitglieder = syn;
    }

    public void setSynonymeBoote(Hashtable<String,String> syn) {
        this.synBoote = syn;
    }

    public void setSynonymeZiele(Hashtable<String,String> syn) {
        this.synZiele = syn;
    }

    public String synMitglieder_genMainName(String syn) {
        return ( synMitglieder.get(syn) != null ? synMitglieder.get(syn) : syn);
    }

    public String synBoote_genMainName(String syn) {
        return ( synBoote.get(syn) != null ? synBoote.get(syn) : syn);
    }

    public String synZiele_genMainName(String syn) {
        return ( synZiele.get(syn) != null ? synZiele.get(syn) : syn);
    }

    public void setAddresses(Hashtable<String,String> addr) {
        this.addresses = addr;
    }

    public String getAddress(String name) {
        return addresses.get(name);
    }

    public void setBoatsAllowedGroups(Hashtable<DataKey,String> boatsAllowedGroups) {
        this.boatsAllowedGroups = boatsAllowedGroups;
    }

    public void setBoatsRequiredGroup(Hashtable<DataKey,String> boatsRequiredGroup) {
        this.boatsRequiredGroup = boatsRequiredGroup;
    }

    public void setGroupMapping(Hashtable<String,UUID> groupMapping) {
        this.groupMapping = groupMapping;
    }

    public int getAbsoluteWork() {
        return importData.size();
    }

    public String getSuccessfullyDoneMessage() {
        return International.getString("Import abgeschlossen.");
    }

    public String getNewestLogbookName() {
        return newestLogbookName;
    }

}
