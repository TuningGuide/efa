/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import java.util.*;
import java.io.*;

// @i18n complete

public abstract class DataFile extends DataAccess {

    protected String filename;
    protected final LinkedHashMap<DataKey,DataRecord> data = new LinkedHashMap<DataKey,DataRecord>();
    protected final DataLocks dataLocks = new DataLocks();

    public DataFile(String directory, String name, String extension) {
        setStorageLocation(directory);
        setStorageObjectName(name);
        setStorageObjectType(extension);
        filename = directory + (directory.endsWith(Daten.fileSep) ? "" : Daten.fileSep) +
                name + "." + extension;
    }

    public DataFile(String filename) {
        setStorageLocation(EfaUtil.getPathOfFile(filename));
        String fname = EfaUtil.getNameOfFile(filename);
        if (fname.indexOf(".") > 0) {
            setStorageObjectName(fname.substring(0,fname.indexOf(".")));
            setStorageObjectType(fname.substring(fname.indexOf(".")+1));
        } else {
            setStorageObjectName(fname);
            setStorageObjectType("");
        }
    }

    public boolean isStorageObjectOpen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private long getLock(DataKey object) throws Exception {
        if (!isStorageObjectOpen()) {
            throw new Exception("Storage Object is not open");
        }
        long transactionID = (object == null ? dataLocks.getGlobalLock() :
                                               dataLocks.getLocalLock(object) );
        if (transactionID < 0) {
            throw new Exception("Could not acquire " +
                    (object == null ? "global lock" :
                                      "local lock on "+object));
        }
        return transactionID;

    }

    public long lock() throws Exception {
        return getLock(null);
    }

    public long lock(DataKey key) throws Exception {
        return getLock(key);
    }

    public long commit(long transactionID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long rollback(long transactionID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getSCN() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void add(DataRecord record) throws Exception {
        if (dataLocks.getNonexclusiveAccess()) {
            try {
                DataKey key = constructKey(record);
                synchronized (data) {
                    if (data.get(key) != null) {
                        throw new Exception("Data Record already exists");
                    }
                    data.put(key, record);
                }
            } finally {
                dataLocks.releaseNonexclusiveAccess();
            }
        } else {
            throw new Exception("Adding Data Record failed: No Access");
        }
    }

    public void add(long transactionID, DataRecord record) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addOrUpdate(DataRecord record) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addOrUpdate(long transactionID, DataRecord record) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord get(DataKey key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord get(long transactionID, DataKey key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(DataKey key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(long transactionID, DataKey key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getNumberOfRecords() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getIterator() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord getExact(long iterator, DataKey key) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord getFirst(long iterator) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord getLast(long iterator) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord getNext(long iterator) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataRecord getPrev(long iterator) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
