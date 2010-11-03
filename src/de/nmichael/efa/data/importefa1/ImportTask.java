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
        for (int i=0; i<importData.size() && running; i++) {
            try { Thread.sleep(1000); } catch(InterruptedException e) {}
            setCurrentWorkDone(i);
            this.logInfo("Step "+i+" done.\n");
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
