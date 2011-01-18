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
import de.nmichael.efa.*;
import java.util.*;

public class ImportTask extends ProgressTask {

    private HashMap<String, ImportMetadata> importData;
    private Hashtable<String,ArrayList<String>> synMitglieder;
    private Hashtable<String,ArrayList<String>> synBoote;
    private Hashtable<String,ArrayList<String>> synZiele;

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
        for (int run = 1; run <= 3; run++) {
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
                    }
                }
                if (run == 2) {
                    switch (meta.type) {
                        case ImportMetadata.TYPE_FAHRTENBUCH:
                            importJob = new ImportLogbook(this, key, meta);
                            break;
                    }
                }
                if (run == 3) {
                    switch (meta.type) {
                        // @todo everything else
                    }
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
                }
            }
        }
        String msg = International.getMessage("{count} Dateien wurden erfolgreich importiert.", successCnt);
        if (errorCnt > 0) {
            msg += "\n" + International.getMessage("Es traten {count} Fehler auf.", errorCnt);
        } else {
            msg += "\n" + International.getString("Es traten keine Fehler auf.");
        }
        logInfo(International.getString("Import beendet.\n"));
        logInfo(msg+"\n");
        Dialog.infoDialog(msg);
        setDone();
    }

    public void setSynonymeMitglieder(Hashtable<String,ArrayList<String>> syn) {
        this.synMitglieder = syn;
    }

    public void setSynonymeBoote(Hashtable<String,ArrayList<String>> syn) {
        this.synBoote = syn;
    }

    public void setSynonymeZiele(Hashtable<String,ArrayList<String>> syn) {
        this.synZiele = syn;
    }

    public int getAbsoluteWork() {
        return importData.size();
    }

    public String getSuccessfullyDoneMessage() {
        return International.getString("Import abgeschlossen.");
    }

}
