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
import de.nmichael.efa.ex.EfaException;
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

    public DataFile(String directory, String name, String extension, String description) {
        setStorageLocation(directory);
        setStorageObjectName(name);
        setStorageObjectType(extension);
        setStorageObjectDescription(description);
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

    public String getUID() {
        return "file:" + filename;
    }

    public synchronized boolean existsStorageObject() throws EfaException {
        if (filename == null) {
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION, "No StorageObject name specified.");
        }
        return (new File(filename).exists());
    }

    public synchronized void createStorageObject() throws EfaException {
        try {
            File f = new File(storageLocation);
            if (!f.exists()) {
                f.mkdirs();
            }
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename,false), ENCODING));
            writeFile(fw);
            fw.close();
            isOpen = true;
            fileWriter = new DataFileWriter(this);
            fileWriter.start();
        } catch(Exception e) {
            throw new EfaException(Logger.MSG_DATA_CREATEFAILED, LogString.logstring_fileCreationFailed(filename, storageLocation, e.toString()));
        }
    }

    public synchronized void openStorageObject() throws EfaException {
        try {
            BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(filename), ENCODING));
            readFile(fr);
            fr.close();
            isOpen = true;
            fileWriter = new DataFileWriter(this);
            fileWriter.start();
        } catch(Exception e) {
            throw new EfaException(Logger.MSG_DATA_OPENFAILED, LogString.logstring_fileOpenFailed(filename, storageLocation, e.toString()));
        }
    }

    // This method must *not* be synchronized;
    // that would result in a deadlock between fileWriter running save(true) and the thread calling closeStorageObject()
    public void closeStorageObject() throws EfaException {
        try {
            fileWriter.save(true);
            synchronized(data) {
                data.clear();
            }
            isOpen = false;
            fileWriter.exit();
            fileWriter = null;
        } catch(Exception e) {
            throw new EfaException(Logger.MSG_DATA_CLOSEFAILED, LogString.logstring_fileCloseFailed(filename, storageLocation, e.toString()));
        }
    }

    public synchronized void saveStorageObject() throws EfaException {
        if (!isStorageObjectOpen()) {
            throw new EfaException(Logger.MSG_DATA_SAVEFAILED, LogString.logstring_fileWritingFailed(filename, storageLocation, "Storage Object is not open"));
        }
        try {
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), ENCODING));
            writeFile(fw);
            fw.close();
        } catch(Exception e) {
            Logger.log(e);
            throw new EfaException(Logger.MSG_DATA_SAVEFAILED, LogString.logstring_fileWritingFailed(filename, storageLocation, e.toString()));
        }
    }

    public boolean isStorageObjectOpen() {
        return isOpen;
    }

    protected abstract void readFile(BufferedReader fr) throws EfaException;
    protected abstract void writeFile(BufferedWriter fw) throws EfaException;

    private long getLock(DataKey object) throws EfaException {
        if (!isStorageObjectOpen()) {
            throw new EfaException(Logger.MSG_DATA_GETLOCKFAILED, getUID() + ": Storage Object is not open");
        }
        long lockID = (object == null ? dataLocks.getGlobalLock() :
                                        dataLocks.getLocalLock(object) );
        if (lockID < 0) {
            throw new EfaException(Logger.MSG_DATA_GETLOCKFAILED, getUID() + ": Could not acquire " +
                    (object == null ? "global lock" :
                                      "local lock on "+object));
        }
        return lockID;
    }

    public long acquireGlobalLock() throws EfaException {
        return getLock(null);
    }

    public long acquireLocalLock(DataKey key) throws EfaException {
        return getLock(key);
    }

    public void releaseGlobalLock(long lockID) throws EfaException {
        dataLocks.releaseGlobalLock(lockID);
    }

    public void releaseLocalLock(long lockID) throws EfaException {
        dataLocks.releaseLocalLock(lockID);
    }

    public long getSCN() throws EfaException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void modifyRecord(DataRecord record, DataKey key, long lockID, boolean add, boolean update, boolean delete) throws EfaException {
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
                            throw new EfaException(Logger.MSG_DATA_RECORDNOTFOUND, getUID() + ": Data Record '"+key.toString()+"' does not exist");
                        }
                    } else {
                        if ( (add && !update)) {
                            throw new EfaException(Logger.MSG_DATA_DUPLICATERECORD, getUID() + ": Data Record '"+key.toString()+"' already exists");
                        }
                    }
                    if (add || update) {
                        data.put(key, record.cloneRecord());
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
            throw new EfaException(Logger.MSG_DATA_MODIFICATIONFAILED, getUID() + ": Data Record Operation failed: No Write Access");
        }
    }

    public void add(DataRecord record) throws EfaException {
        modifyRecord(record, null, 0, true, false, false);
    }

    public void add(DataRecord record, long lockID) throws EfaException {
        modifyRecord(record, null, lockID, true, false, false);
    }

    public void addOrUpdate(DataRecord record) throws EfaException {
        modifyRecord(record, null, 0, true, true, false);
    }

    public void addOrUpdate(DataRecord record, long lockID) throws EfaException {
        modifyRecord(record, null, lockID, true, true, false);
    }

    public void delete(DataKey key) throws EfaException {
        modifyRecord(null, key, 0, false, false, true);
    }

    public void delete(DataKey key, long lockID) throws EfaException {
        modifyRecord(null, key, lockID, false, false, true);
    }

    public DataRecord get(DataKey key) throws EfaException {
        synchronized (data) {
            return data.get(key);
        }
    }

    public long getNumberOfRecords() throws EfaException {
        synchronized(data) {
            return data.size();
        }
    }

    public void truncateAllData() throws EfaException {
        long lockID = acquireGlobalLock();
        try {
            synchronized (data) {
                data.clear();
            }
            fileWriter.save(false);
        } finally {
            this.releaseGlobalLock(lockID);
        }
    }

    public DataKeyIterator getIterator() throws EfaException {
        DataKey[] keys;
        synchronized(data) {
            keys = new DataKey[data.size()];
            keys = data.keySet().toArray(keys);
        }
        Arrays.sort(keys);
        return new DataKeyIterator(keys);
    }

    private DataRecord getIteratorDataRecord(DataKey key) throws EfaException {
        if (key != null) {
            return get(key);
        } else {
            return null;
        }
    }

    public DataRecord getCurrent(DataKeyIterator it) throws EfaException {
        return getIteratorDataRecord(it.getCurrent());
    }

    public DataRecord getFirst(DataKeyIterator it) throws EfaException {
        return getIteratorDataRecord(it.getFirst());
    }

    public DataRecord getLast(DataKeyIterator it) throws EfaException {
        return getIteratorDataRecord(it.getLast());
    }

    public DataRecord getNext(DataKeyIterator it) throws EfaException {
        return getIteratorDataRecord(it.getNext());
    }

    public DataRecord getPrev(DataKeyIterator it) throws EfaException {
        return getIteratorDataRecord(it.getPrev());
    }


}
