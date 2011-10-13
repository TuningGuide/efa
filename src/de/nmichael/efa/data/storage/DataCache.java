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

import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.util.Logger;
import java.util.*;

public class DataCache {
    
    public static long MAX_AGE;

    private IDataAccess dataAccess;
    private long lastScnUpdate = -1;
    private long scn = -1;
    private long totalNumberOfRecords = -1;
    private Hashtable<DataKey,DataRecord> cache = new Hashtable<DataKey,DataRecord>();

    public DataCache(IDataAccess dataAccess, long cacheExpiryTime) {
        this.dataAccess = dataAccess;
        this.MAX_AGE = cacheExpiryTime;
    }

    public synchronized void updateCache(DataRecord record, long scn, long totalNumberOfRecords) {
        updateScn(scn, totalNumberOfRecords);
        cache.put(record.getKey(), record);
    }

    public synchronized void updateScn(long newScn, long newTotalNumberOfRecords){
        lastScnUpdate = System.currentTimeMillis();
        if (scn != newScn) {
            cache.clear();
        }
        scn = newScn;
        totalNumberOfRecords = newTotalNumberOfRecords;
    }

    private boolean isTooOld() {
        return (scn < 0 || System.currentTimeMillis() - lastScnUpdate > MAX_AGE);
    }
    
    private synchronized void fetchScnIfTooOld() {
        if (isTooOld()) {
            try {
                long newScn = dataAccess.getSCN();
                long numberOfRecords = dataAccess.getNumberOfRecords();
                if (newScn >= 0) {
                    updateScn(newScn, numberOfRecords);
                }
            } catch(Exception e) {
                Logger.logdebug(e);
            }
        }
    }

    public synchronized long getScnIfNotTooOld() {
        if (isTooOld()) {
            return -1;
        }
        return scn;
    }

    public synchronized long getTotalNumberOfRecordsIfNotTooOld() {
        if (isTooOld()) {
            return -1;
        }
        return totalNumberOfRecords;
    }

    public synchronized DataRecord get(DataKey key) {
        fetchScnIfTooOld();
        return cache.get(key);
    }

    private synchronized void getAllRecordsFromRemote() throws EfaException {
        // the following call will automatically also prefetch all records
        // and thus update the entire cache
        DataKey[] keys = dataAccess.getAllKeys();
    }

    public synchronized DataRecord getValidAt(DataKey key, long t) throws EfaException {
        DataRecord r = getValidAtFromCache(key, t);
        if (r == null) {
            if (isTooOld() || totalNumberOfRecords != cache.size()) {
                getAllRecordsFromRemote();
                r = getValidAtFromCache(key, t);
            }
        }
        return r;
    }

    private synchronized DataRecord getValidAtFromCache(DataKey key, long t) {
        int validFromField;
        if (dataAccess.getMetaData().versionized) {
            validFromField = dataAccess.getKeyFieldNames().length - 1; // VALID_FROM is always the last key field!
        } else {
            // wrong call: not versionized
            return null;
        }
        DataKey[] keys = cache.keySet().toArray(new DataKey[0]);
        if (keys == null) {
            return null;
        }
        for (DataKey k : keys) {
            boolean sameRecord = true;
            for (int i=0; i<validFromField; i++) {
                if (k.getKeyPart(i) == null || !k.getKeyPart(i).equals(key.getKeyPart(i))) {
                    sameRecord = false;
                }
            }
            if (!sameRecord) {
                continue;
            }
            long validFrom = (Long) k.getKeyPart(validFromField);
            if (t >= validFrom) {
                DataRecord rec = get(k);
                if (rec != null && t >= rec.getValidFrom() && t < rec.getInvalidFrom()) {
                    return rec;
                }
            }
        }
        return null;
    }

}