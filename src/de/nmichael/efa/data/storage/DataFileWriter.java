/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class DataFileWriter extends Thread {

    public static final long SLEEP_INTERVAL = 10000; // 10.000 ms
    public static final long SAVE_INTERVAL = 10000; // 10.000 ms

    private DataFile dataFile;
    private volatile boolean writedata = false;
    private long lastSave = 0;

    public DataFileWriter(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public void run() {
        if (Logger.isTraceOn(Logger.TT_FILEIO)) {
            Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_RUNNING, "DataFileWriter["+dataFile.filename+"] running.");
        }
        while(dataFile.isStorageObjectOpen()) {
            try {
                if (writedata && System.currentTimeMillis() - lastSave > SAVE_INTERVAL) {
                    if (Logger.isTraceOn(Logger.TT_FILEIO)) {
                        Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_SAVING, "DataFileWriter["+dataFile.filename+"] found new data to be saved.");
                    }
                    long lock = dataFile.acquireGlobalLock();
                    if (lock > 0) {
                        try {
                            if (Logger.isTraceOn(Logger.TT_FILEIO)) {
                                Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_SAVING, "DataFileWriter["+dataFile.filename+"] got global lock, now saving ...");
                            }
                            dataFile.saveStorageObject();
                            if (Logger.isTraceOn(Logger.TT_FILEIO)) {
                                Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_SAVING, "DataFileWriter["+dataFile.filename+"] data successfully saved.");
                            }
                        } catch(Exception e) {
                            Logger.log(Logger.ERROR, Logger.MSG_FILE_WRITETHREAD_ERROR, "DataFileWriter["+dataFile.filename+"] failed to save data: "+e.toString());
                        } finally {
                            dataFile.releaseGlobalLock(lock);
                            if (Logger.isTraceOn(Logger.TT_FILEIO)) {
                                Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_SAVING, "DataFileWriter["+dataFile.filename+"] released global lock.");
                            }
                        }
                        lastSave = System.currentTimeMillis();
                        writedata = false;
                    }
                } else {
                    Thread.sleep(SLEEP_INTERVAL);
                }
            } catch(Exception eglob) {
                // nothing to do
            }
        }
        if (Logger.isTraceOn(Logger.TT_FILEIO)) {
            Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_EXIT, "DataFileWriter["+dataFile.filename+"] exited.");
        }
    }

   synchronized public void save(boolean synchronous) {
       if (Logger.isTraceOn(Logger.TT_FILEIO)) {
           Logger.log(Logger.DEBUG, Logger.MSG_FILE_WRITETHREAD_SAVING, "DataFileWriter["+dataFile.filename+"] new save request queued" + (synchronous ? " (sync)" : "") + ".");
       }
       if (synchronous) {
           lastSave = 0;
       }
        writedata = true;
        if (System.currentTimeMillis() - lastSave > SAVE_INTERVAL) {
            this.interrupt();
        }
        while (synchronous && writedata) {
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {
                // nothing to do
            }
        }
    }

    public void exit() {
        this.interrupt();
    }

}
