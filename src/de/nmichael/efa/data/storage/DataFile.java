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
import java.util.*;
import java.io.*;

// @i18n complete

public abstract class DataFile extends DataAccess {

    protected static final String ENCODING = Daten.ENCODING_UTF;
    protected String filename;
    protected volatile boolean isOpen = false;
    private final HashMap<DataKey,DataRecord> data = new HashMap<DataKey,DataRecord>();
    private final HashMap<DataKey,ArrayList<DataKey>> versionizedKeyList = new HashMap<DataKey,ArrayList<DataKey>>();
    private final ArrayList<DataIndex> indices = new ArrayList<DataIndex>();
    private final DataLocks dataLocks = new DataLocks();
    private DataFileWriter fileWriter;

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
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION, "No StorageObject name specified.", Thread.currentThread().getStackTrace());
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
            throw new EfaException(Logger.MSG_DATA_CREATEFAILED, LogString.logstring_fileCreationFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
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
            throw new EfaException(Logger.MSG_DATA_OPENFAILED, LogString.logstring_fileOpenFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        }
    }

    // This method must *not* be synchronized;
    // that would result in a deadlock between fileWriter running save(true) and the thread calling closeStorageObject()
    public void closeStorageObject() throws EfaException {
        try {
            fileWriter.save(true);
            synchronized(data) {
                data.clear();
                versionizedKeyList.clear();
                for (DataIndex idx: indices) {
                    idx.clear();
                }
            }
            isOpen = false;
            fileWriter.exit();
            fileWriter = null;
        } catch(Exception e) {
            throw new EfaException(Logger.MSG_DATA_CLOSEFAILED, LogString.logstring_fileCloseFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        }
    }

    public synchronized void saveStorageObject() throws EfaException {
        if (!isStorageObjectOpen()) {
            throw new EfaException(Logger.MSG_DATA_SAVEFAILED, LogString.logstring_fileWritingFailed(filename, storageLocation, "Storage Object is not open"), Thread.currentThread().getStackTrace());
        }
        try {
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), ENCODING));
            writeFile(fw);
            fw.close();
        } catch(Exception e) {
            Logger.log(e);
            throw new EfaException(Logger.MSG_DATA_SAVEFAILED, LogString.logstring_fileWritingFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        }
    }

    public boolean isStorageObjectOpen() {
        return isOpen;
    }

    protected abstract void readFile(BufferedReader fr) throws EfaException;
    protected abstract void writeFile(BufferedWriter fw) throws EfaException;

    private long getLock(DataKey object) throws EfaException {
        if (!isStorageObjectOpen()) {
            throw new EfaException(Logger.MSG_DATA_GETLOCKFAILED, getUID() + ": Storage Object is not open", Thread.currentThread().getStackTrace());
        }
        long lockID = (object == null ? dataLocks.getGlobalLock() :
                                        dataLocks.getLocalLock(object) );
        if (lockID < 0) {
            throw new EfaException(Logger.MSG_DATA_GETLOCKFAILED, getUID() + ": Could not acquire " +
                    (object == null ? "global lock" :
                                      "local lock on "+object), Thread.currentThread().getStackTrace());
        }
        return lockID;
    }

    public long acquireGlobalLock() throws EfaException {
        return getLock(null);
    }

    public long acquireLocalLock(DataKey key) throws EfaException {
        return getLock(key);
    }

    public boolean releaseGlobalLock(long lockID) {
        return dataLocks.releaseGlobalLock(lockID);
    }

    public boolean releaseLocalLock(long lockID) {
        return dataLocks.releaseLocalLock(lockID);
    }

    public long getSCN() throws EfaException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createIndex(String[] fieldNames) throws EfaException {
        int[] idxFields = new int[fieldNames.length];
        for (int i=0; i<idxFields.length; i++) {
            idxFields[i] = meta.getFieldIndex(fieldNames[i]);
        }
        indices.add(new DataIndex(idxFields));
    }

    private void modifyRecord(DataRecord record, long lockID, boolean add, boolean update, boolean delete) throws EfaException {
        long myLock = -1;
        if (record == null) {
            throw new EfaException(Logger.MSG_DATA_RECORDNOTFOUND, getUID() + ": Data Record is 'null'", Thread.currentThread().getStackTrace());
        }
        DataKey key = constructKey(record);
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
                            throw new EfaException(Logger.MSG_DATA_RECORDNOTFOUND, getUID() + ": Data Record '"+key.toString()+"' does not exist", Thread.currentThread().getStackTrace());
                        }
                    } else {
                        if ( (add && !update)) {
                            throw new EfaException(Logger.MSG_DATA_DUPLICATERECORD, getUID() + ": Data Record '"+key.toString()+"' already exists", Thread.currentThread().getStackTrace());
                        }
                    }
                    if (add || update) {
                        data.put(key, record.cloneRecord());
                        if (meta.versionized) {
                            modifyVersionizedKeys(key, add, update, delete);
                        }
                        for (DataIndex idx: indices) {
                            idx.add(record);
                        }
                    } else {
                        if (delete) {
                            data.remove(key);
                            if (meta.versionized) {
                                modifyVersionizedKeys(key, add, update, delete);
                            }
                            for (DataIndex idx: indices) {
                                idx.delete(record); // needs record, but record must not be null
                            }
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
            throw new EfaException(Logger.MSG_DATA_MODIFICATIONFAILED, getUID() + ": Data Record Operation failed: No Write Access", Thread.currentThread().getStackTrace());
        }
    }

    private void modifyVersionizedKeys(DataKey key, boolean add, boolean update, boolean delete) {
        DataKey keyUnversionized = getUnversionizedKey(key);
        synchronized(data) { // always synchronize on data to ensure integrity!
            ArrayList<DataKey> list = versionizedKeyList.get(keyUnversionized);
            if (list == null) {
                if (add || update) {
                    list = new ArrayList<DataKey>();
                }
                if (delete) {
                    return; // nothing to do
                }
            }
            if (add || update) {
                if (!list.contains(key)) {
                    list.add(key);
                    versionizedKeyList.put(keyUnversionized, list);
                }
            }
            if (delete) {
                list.remove(key);
                if (list.size() == 0) {
                    versionizedKeyList.remove(keyUnversionized); // last key removed
                } else {
                    // no "versionizedKeyList.put(keyUnversionized, list)" necessary (we're working on the same reference of "list")
                }
            }
        }
    }

    public void add(DataRecord record) throws EfaException {
        if (meta.versionized) {
            addValidAt(record, -1, 0);
        } else {
            modifyRecord(record, 0, true, false, false);
        }
    }

    public void add(DataRecord record, long lockID) throws EfaException {
        if (meta.versionized) {
            addValidAt(record, -1, lockID);
        } else {
            modifyRecord(record, lockID, true, false, false);
        }
    }

    public DataKey addValidAt(DataRecord record, long t) throws EfaException {
        return addValidAt(record, t, 0);
    }

    public DataKey addValidAt(DataRecord record, long t, long lockID) throws EfaException {
        if (!meta.versionized) {
            throw new EfaException(Logger.MSG_DATA_INVALIDVERSIONIZEDDATA, getUID() + ": Attempt to add versionized data to an unversionized storage object", Thread.currentThread().getStackTrace());
        }
        long myLock = -1;
        if (lockID <= 0) {
            // acquire a new global lock
            myLock = acquireGlobalLock();
        } else {
            // verify existing lock
            myLock = (dataLocks.hasGlobalLock(lockID) ? lockID : -1);
        }
        if (myLock > 0) {
            try {
                synchronized (data) {
                    if (isValidAny(record.getKey())) {
                        if (t < 0) {
                            t = System.currentTimeMillis();
                        }
                        DataRecord r1 = getValidAt(record.getKey(), t);
                        if (r1 != null) {
                            if (t == r1.getValidFrom()) {
                                throw new EfaException(Logger.MSG_DATA_VERSIONIZEDDATACONFLICT, getUID() + ": Versionized Data Conflict (Duplicate?) for Record " + record.toString() + " at ValidFrom=" + t, Thread.currentThread().getStackTrace());
                            }
                            // add new record
                            record.setValidFrom(t);
                            record.setInvalidFrom(r1.getInvalidFrom());
                            modifyRecord(record, myLock, true, false, false);
                            // adjust InvalidFrom field for existing record
                            modifyRecord(r1, myLock, false, false, true);
                            r1.setInvalidFrom(t);
                            modifyRecord(r1, myLock, true, false, false);
                        }
                    } else {
                        record.setAlwaysValid();
                        if (t >= 0) {
                            record.setValidFrom(t);
                        }
                        modifyRecord(record, myLock, true, false, false);
                    }
                }
            } finally {
                if (lockID <= 0) {
                    releaseGlobalLock(myLock);
                }
            }
        }
        return record.getKey();
    }

    public void update(DataRecord record) throws EfaException {
        modifyRecord(record, 0, false, true, false);
    }

    public void update(DataRecord record, long lockID) throws EfaException {
        modifyRecord(record, lockID, false, true, false);
    }

    public void delete(DataKey key) throws EfaException {
        modifyRecord(get(key), 0, false, false, true);
    }

    public void delete(DataKey key, long lockID) throws EfaException {
        modifyRecord(get(key), lockID, false, false, true);
    }

    public void deleteVersionized(DataKey key, int merge) throws EfaException {
        deleteVersionized(key, merge, 0);
    }

    public void deleteVersionized(DataKey key, int merge, long lockID) throws EfaException {
        if (!meta.versionized) {
            throw new EfaException(Logger.MSG_DATA_INVALIDVERSIONIZEDDATA, getUID() + ": Attempt to add versionized data to an unversionized storage object", Thread.currentThread().getStackTrace());
        }
        long myLock = -1;
        if (lockID <= 0) {
            // acquire a new global lock
            myLock = acquireGlobalLock();
        } else {
            // verify existing lock
            myLock = (dataLocks.hasGlobalLock(lockID) ? lockID : -1);
        }
        if (myLock > 0) {
            try {
                synchronized (data) {
                    DataRecord r = getValidAt(key, (Long)key.getKeyPart(keyFields.length - 1)); // VALID_FROM is always the last key field!
                    modifyRecord(r, myLock, false, false, true);
                    if (r != null && merge != 0 && isValidAny(key)) {
                        if (merge == -1) { // merge with left record
                            DataRecord r2 = getValidAt(key, r.getValidFrom()-1);
                            if (r2 != null) {
                                r2.setInvalidFrom(r.getInvalidFrom());
                                modifyRecord(r2, myLock, false, true, false);
                            }
                        }
                        if (merge == 1) { // merge with right record
                            DataRecord r2 = getValidAt(key, r.getInvalidFrom());
                            if (r2 != null) {
                                modifyRecord(r2, myLock, false, false, true);
                                r2.setValidFrom(r.getValidFrom());
                                modifyRecord(r2, myLock, true, false, false);
                            }
                        }
                    }
                }
            } finally {
                if (lockID <= 0) {
                    releaseGlobalLock(myLock);
                }
            }
        }
    }

    public DataRecord get(DataKey key) throws EfaException {
        synchronized (data) {
            DataRecord rec = data.get(key);
            if (rec != null) {
                return rec.cloneRecord();
            }
            return null;
        }
    }

    public DataRecord[] getValidAny(DataKey key) throws EfaException {
        DataRecord[] recs;
        synchronized(data) { // always synchronize on data to ensure integrity!
            ArrayList<DataKey> list = versionizedKeyList.get(getUnversionizedKey(key));
            if (list == null || list.size() == 0) {
                return null;
            }
            recs = new DataRecord[list.size()];
            int i=0;
            for (DataKey k : list) {
                recs[i++] = get(k);
            }
        }
        return recs;
    }

    public DataRecord getValidAt(DataKey key, long t) throws EfaException {
        int validFromField;
        if (meta.versionized) {
            validFromField = keyFields.length - 1; // VALID_FROM is always the last key field!
        } else {
            return null;
        }
        synchronized(data) { // always synchronize on data to ensure integrity!
            ArrayList<DataKey> list = versionizedKeyList.get(getUnversionizedKey(key));
            if (list == null) {
                return null;
            }
            for (DataKey k : list) {
                long validFrom = (Long)k.getKeyPart(validFromField);
                if (t >= validFrom) {
                    DataRecord rec = get(k);
                    if (rec != null && t >= rec.getValidFrom() && t < rec.getInvalidFrom()) {
                        return rec;
                    }
                }
            }
        }
        return null;
    }

    public boolean isValidAny(DataKey key) throws EfaException {
        synchronized(data) { // always synchronize on data to ensure integrity!
            ArrayList<DataKey> list = versionizedKeyList.get(getUnversionizedKey(key));
            if (list == null || list.size() == 0) {
                return false;
            }
        }
        return true;
    }

    private DataIndex findIndex(int[] idxFields) {
        for (DataIndex idx : indices) {
            if (Arrays.equals(idxFields, idx.getIndexFields())) {
                return idx;
            }
        }
        return null;
    }

    public DataKey[] getByFields(String[] fieldNames, Object[] values) throws EfaException {
        int[] idxFields = new int[fieldNames.length];
        for (int i=0; i<idxFields.length; i++) {
            idxFields[i] = meta.getFieldIndex(fieldNames[i]);
        }
        DataIndex idx = findIndex(idxFields);
        if (idx != null) {
            // Search by using index
            return idx.search(values);
        } else {
            // Search without index
            // @todo
        }
        return null;
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
                versionizedKeyList.clear();
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
