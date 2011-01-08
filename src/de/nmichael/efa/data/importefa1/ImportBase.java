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

public abstract class ImportBase {

    protected ImportTask task;

    public ImportBase(ImportTask task) {
        this.task = task;
    }

    public abstract String getDescription();
    public abstract boolean runImport();

    protected void logInfo(String s) {
        task.logInfo("INFO  - " + getDescription()+ " - " + s + "\n");
    }

    protected void logWarning(String s) {
        task.logInfo("WARN  - " + getDescription()+ " - " + s + "\n");
    }

    protected void logError(String s) {
        task.logInfo("ERROR - " + getDescription()+ " - " + s + "\n");
    }

}
