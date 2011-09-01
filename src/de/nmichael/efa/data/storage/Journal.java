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

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.EfaException;
import java.io.*;

public class Journal {

    enum Operation {
        add,
        update,
        delete,
        truncate
    }

    private String storageObjectName;
    private String storageObjectFilename;
    private long scnsPerJournal;
    private int numberOfJournals;

    private long fwnr = -1;
    private BufferedWriter fw;
    private String fwname = null;

    public Journal(String storageObjectName, String storageObjectFilename, long scnsPerJournal, int numberOfJournals) {
        this.storageObjectName = storageObjectName;
        this.storageObjectFilename = storageObjectFilename;
        this.scnsPerJournal = scnsPerJournal;
        this.numberOfJournals = numberOfJournals;
    }

    public static String getOperationName(Operation operation) {
        switch (operation) {
            case add:
                return "Add";
            case update:
                return "Upd";
            case delete:
                return "Del";
            case truncate:
                return "Trc";
        }
        return null;
    }

    public static String encodeCommand(Operation operation, DataRecord r) {
        return encodeCommand(new StringBuffer(), operation, r);
    }

    public static String encodeCommand(StringBuffer s, Operation operation, DataRecord r) {
        s.append(getOperationName(operation) + ":");
        if (operation != Operation.truncate) {
            s.append(r.encodeAsString());
        }
        return s.toString();
    }

    public boolean close() {
        try {
            if (fw != null) {
                fw.close();
                fwnr = -1;
                fwname = null;
            }
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_DATA_JOURNALOPENFAILED,
                        LogString.logstring_fileCloseFailed(fwname, International.getString("Journal"), e.toString()));
            return false;
        }
        return true;
    }

    public long getJournalNumber(long scn) {
        return scn / scnsPerJournal;
    }

    public long getJournalGroup(long scn) {
        return getJournalNumber(scn) % numberOfJournals;
    }

    public String getJournalGroupName(long group) {
        return storageObjectFilename + ".j" + group;
    }

    public String getJournalName(long scn) {
        long jg = getJournalGroup(scn);
        return getJournalGroupName(jg);
    }

    public boolean isOpenNewJournal(long scn) {
        return scn == 1 || (scn % scnsPerJournal) == 0;
    }

    private BufferedWriter openForAppend(long scn) {
        if (scn < 1) {
            return null;
        }
        long jnr = getJournalNumber(scn);
        if (jnr != fwnr || fw == null) {
            String journalName = getJournalName(scn);
            try {
                if (isOpenNewJournal(scn)) {
                    // open with overwrite
                    fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalName, false), Daten.ENCODING_UTF));
                    fw.write("###Journal opened: " + EfaUtil.getCurrentTimeStampYYYY_MM_DD_HH_MM_SS() + "\n");
                    fw.write("###Storage Object: " + storageObjectName + "\n");
                    fw.write("###Journal Number: " + jnr + "\n");
                    fw.write("###Journal Group : " + getJournalGroup(scn) + "\n");
                    fw.write("###Journal 1stSCN: " + scn + "\n");
                } else {
                    // open with append
                    fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalName, true), Daten.ENCODING_UTF));
                }
                fwnr = jnr;
                fwname = journalName;
            } catch (Exception e) {
                Logger.log(Logger.ERROR, Logger.MSG_DATA_JOURNALOPENFAILED,
                        LogString.logstring_fileCreationFailed(journalName, International.getString("Journal"), e.toString()));
                fw = null;
                fwnr = -1;
                fwname = null;
            }
        }
        return fw;
    }

    public String getLogString(long scn, Operation operation, DataRecord r) {
        StringBuffer s = new StringBuffer();
        s.append("#" + scn + ":");
        s.append(System.currentTimeMillis() + ":");
        return encodeCommand(s, operation, r);
    }

    public boolean log(long scn, Operation operation, DataRecord r) {
        try {
            BufferedWriter f = openForAppend(scn);
            String s = getLogString(scn, operation, r);
            if (s == null || s.length() == 0) {
                Logger.log(Logger.ERROR, Logger.MSG_DATA_JOURNALWRITEFAILED,
                        LogString.logstring_fileWritingFailed(fwname, International.getString("Journal"), "empty log string"));
                return false;
            }
            f.write(s + "\n");
            f.flush();
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_DATA_JOURNALWRITEFAILED,
                        LogString.logstring_fileWritingFailed(fwname, International.getString("Journal"), e.toString()));
            return false;
        }
        return true;
    }

    public void deleteAllJournals() throws EfaException {
        for (int i = 0; i < numberOfJournals; i++) {
            String filename = getJournalGroupName(i);
            try {
                File f = new File(filename);
                if (f.isFile()) {
                    if (!f.delete()) {
                        throw new Exception(LogString.logstring_fileDeletionFailed(filename, International.getString("Journal")));
                    }
                }
            } catch (Exception e) {
                throw new EfaException(Logger.MSG_DATA_DELETEFAILED, LogString.logstring_fileDeletionFailed(filename, International.getString("Journal"), e.toString()), Thread.currentThread().getStackTrace());
            }
        }
    }

}
