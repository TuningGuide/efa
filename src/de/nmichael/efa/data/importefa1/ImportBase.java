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

public abstract class ImportBase {

    protected ImportTask task;
    protected String efa1fname;
    protected ImportMetadata meta;

    public ImportBase(ImportTask task, String efa1fname, ImportMetadata meta) {
        this.task = task;
        this.efa1fname = efa1fname;
        this.meta = meta;
    }

    public abstract boolean runImport();

    private void logInfo(String s) {
        task.logInfo("INFO  - " + meta.toString(false)+ ": " + s);
    }

    private void logWarning(String s) {
        task.logInfo("WARN  - " + meta.toString(false)+ ": " + s);
    }

    private void logError(String s) {
        task.logInfo("ERROR - " + meta.toString(false)+ ": " + s);
    }

}
