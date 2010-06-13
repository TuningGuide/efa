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
    protected final HashMap<DataKey,DataRecord> data = new HashMap<DataKey,DataRecord>();
    protected final DataLocks dataLocks = new DataLocks();
    protected DataFileWriter fileWriter;
    protected volatile boolean isOpen = false;
    protected static final String ENCODING = Daten.ENCODING_UTF;

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

    public void createStorageObject() throws Exception {
        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename,false), ENCODING));
        writeFile(fw);
        fw.close();
        isOpen = true;
        fileWriter = new DataFileWriter(this);
        fileWriter.start();
    }

    public void openStorageObject() throws Exception {
        BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(filename), ENCODING));
        readFile(fr);
        fr.close();
        isOpen = true;
        fileWriter = new DataFileWriter(this);
        fileWriter.start();
    }

    public void closeStorageObject() throws Exception {
        fileWriter.save(true);
        isOpen = false;
        fileWriter.exit();
        fileWriter = null;
    }

    public void saveStorageObject() throws Exception {
        if (!isStorageObjectOpen()) {
            throw new Exception("Storage Object is not open");
        }
        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), ENCODING));
        writeFile(fw);
        fw.close();
    }

    public boolean isStorageObjectOpen() {
        return isOpen;
    }

    protected abstract void readFile(BufferedReader fr) throws Exception;
    protected abstract void writeFile(BufferedWriter fw) throws Exception;

    private long getLock(DataKey object) throws Exception {
        if (!isStorageObjectOpen()) {
            throw new Exception("Storage Object is not open");
        }
        long lockID = (object == null ? dataLocks.getGlobalLock() :
                                        dataLocks.getLocalLock(object) );
        if (lockID < 0) {
            throw new Exception("Could not acquire " +
                    (object == null ? "global lock" :
                                      "local lock on "+object));
        }
        return lockID;
    }

    public long acquireGlobalLock() throws Exception {
        return getLock(null);
    }

    public long acquireLocalLock(DataKey key) throws Exception {
        return getLock(key);
    }

    public void releaseGlobalLock(long lockID) throws Exception {
        dataLocks.releaseGlobalLock(lockID);
    }

    public void releaseLocalLock(long lockID) throws Exception {
        dataLocks.releaseLocalLock(lockID);
    }

    public long getSCN() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void modifyRecord(DataRecord record, DataKey key, long lockID, boolean add, boolean update, boolean delete) throws Exception {
        long myLock = -1;
        if (key == null) {
            key = constructKey(record);
        }
        if (lockID <= 0) {
            // acquire a new local lock
            myLock = acquireLocalLock(key);
        } else {
            // verify existing lock
            myLock = (dataLocks.hasGlobalLock(lockID) || dataLocks.hasLocalLock(lockID, key) ? lockID : -1);
        }
        if (myLock > 0) {
            try {
                synchronized (data) {
                    if (data.get(key) == null) {
                        if ( (update && !add) || delete) {
                            throw new Exception("Data Record does not exist");
                        }
                    } else {
                        if ( (add && !update)) {
                            throw new Exception("Data Record already exists");
                        }
                    }
                    if (add || update) {
                        data.put(key, record.clone());
                    } else {
                        if (delete) {
                            data.remove(key);
                        }
                    }
                }
            } finally {
                if (lockID <= 0) {
                    releaseLocalLock(myLock);
                }
            }
            if (fileWriter != null) { // may be null while reading (opening) a file
                fileWriter.save(false);
            }
        } else {
            throw new Exception("Data Record Operation failed: No Write Access");
        }
    }

    public void add(DataRecord record) throws Exception {
        modifyRecord(record, null, 0, true, false, false);
    }

    public void add(DataRecord record, long lockID) throws Exception {
        modifyRecord(record, null, lockID, true, false, false);
    }

    public void addOrUpdate(DataRecord record) throws Exception {
        modifyRecord(record, null, 0, true, true, false);
    }

    public void addOrUpdate(DataRecord record, long lockID) throws Exception {
        modifyRecord(record, null, lockID, true, true, false);
    }

    public void delete(DataKey key) throws Exception {
        modifyRecord(null, key, 0, false, false, true);
    }

    public void delete(DataKey key, long lockID) throws Exception {
        modifyRecord(null, key, lockID, false, false, true);
    }

    public DataRecord get(DataKey key) throws Exception {
        synchronized (data) {
            return data.get(key);
        }
    }

    public long getNumberOfRecords() throws Exception {
        synchronized(data) {
            return data.size();
        }
    }

    public DataKeyIterator getIterator() throws Exception {
        DataKey[] keys = new DataKey[data.size()];
        keys = data.keySet().toArray(keys);
        Arrays.sort(keys);
        return new DataKeyIterator(keys);
    }

    private DataRecord getIteratorDataRecord(DataKey key) throws Exception {
        if (key != null) {
            return get(key);
        } else {
            return null;
        }
    }

    public DataRecord getCurrent(DataKeyIterator it) throws Exception {
        return getIteratorDataRecord(it.getCurrent());
    }

    public DataRecord getFirst(DataKeyIterator it) throws Exception {
        return getIteratorDataRecord(it.getFirst());
    }

    public DataRecord getLast(DataKeyIterator it) throws Exception {
        return getIteratorDataRecord(it.getLast());
    }

    public DataRecord getNext(DataKeyIterator it) throws Exception {
        return getIteratorDataRecord(it.getNext());
    }

    public DataRecord getPrev(DataKeyIterator it) throws Exception {
        return getIteratorDataRecord(it.getPrev());
    }


}
