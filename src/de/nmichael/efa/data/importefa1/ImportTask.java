/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.importefa1;

import de.nmichael.efa.gui.ProgressDialog;
import de.nmichael.efa.util.*;
import java.util.*;

public class ImportTask extends ProgressTask {

    private HashMap<String, ImportMetadata> importData;

    public ImportTask(HashMap<String, ImportMetadata> importData) {
        super();
        this.importData = importData;
    }

    public void run() {
        setRunning(true);
        int i = 0;
        int errorCnt = 0;
        for (String key : importData.keySet()) {
            ImportMetadata meta = importData.get(key);
            if (!meta.selected) {
                continue;
            }
            logInfo(International.getMessage("Importiere {file} ...", meta.toString()) + "\n");

            ImportBase importJob = null;
            switch (meta.type) {
                case ImportMetadata.TYPE_FAHRTENBUCH:
                    importJob = new ImportLogbook(this, key, meta);
                    break;
            }
            boolean result = false;
            if (importJob != null) {
                importJob.runImport();
            }
            if (!result) {
                errorCnt++;
            }

            setCurrentWorkDone(i++);
        }
        if (errorCnt > 0) {
            Dialog.error(International.getMessage("Es traten {count} Fehler auf.", errorCnt));
        }
        setDone();
    }

    public int getAbsoluteWork() {
        return importData.size();
    }

    public String getSuccessfullyDoneMessage() {
        return International.getString("Daten erfolgreich importiert.");
    }

}
