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

import java.util.*;
import de.nmichael.efa.util.Logger;

// @i18n complete

public class DataLocks {

    public static final long LOCK_TIMEOUT = 10000; // 10,000 ms
    public static final long SLEEP_RETRY  =    10; //     10 ms

    private final Hashtable<DataKey,DataLock> locks = new Hashtable<DataKey,DataLock>();
    private volatile long numberOfNonexclusiveAccesses = 0;
    private volatile long transactionID = 0;

    public int clearTimeouts() {
        int count = 0;
        long now = System.currentTimeMillis();
        synchronized (locks) {
            for (Iterator<DataKey> it = locks.keySet().iterator(); it.hasNext();) {
                DataLock lock = locks.get(it.next());
                if (now - lock.getLockTime() >= LOCK_TIMEOUT) {
                    Logger.log(Logger.WARNING, Logger.MSG_DATA_LOCKTIMEOUT,
                            "Lock Timeout [" + now + "]: " + lock.toString());
                    if (!lock.isExclusive()) {
                        numberOfNonexclusiveAccesses = 0;
                    }
                    locks.remove(lock.getLockObject());
                    count++;
                }
            }
        }
        return count;
    }

    private DataLock newDataLock(DataKey object, boolean exclusive) {
        DataLock lock;
        synchronized (locks) {
            lock = new DataLock(++transactionID, object, exclusive);
            locks.put(object, lock);
        }
        return lock;
    }

    private DataLock tryAcquireLock(boolean global, DataKey object) {
        try {
            long startTimestamp = System.currentTimeMillis();
            do {
                synchronized(locks) {
                    if (global) {
                        // try to acquire a global lock
                        if (locks.size() == 0) {
                            return newDataLock(DataLock.GLOBAL_LOCK, true);
                        }
                    } else {
                        // try to acquire a local lock
                        if (locks.get(object) == null &&
                            locks.get(DataLock.GLOBAL_LOCK) == null &&
                            locks.get(DataLock.NONEXCLUSIVE_LOCK) == null) {
                            return newDataLock(object, true);
                        }
                    }
                }
                try {
                    Thread.sleep(SLEEP_RETRY);
                } catch (InterruptedException ie) {
                }
                clearTimeouts();
            } while (System.currentTimeMillis() >= startTimestamp
                    && System.currentTimeMillis() - startTimestamp < LOCK_TIMEOUT);
        } catch (Exception e) {
        }
        return null;
    }

    public long getGlobalLock() {
        DataLock lock = tryAcquireLock(true, null);
        if (lock != null) {
            return lock.getLockID();
        }
        return -1;
    }

    public long getLocalLock(DataKey object) {
        if (object == null || object.equals(DataLock.GLOBAL_LOCK)) {
            return -1;
        }
        DataLock lock = tryAcquireLock(false, object);
        if (lock != null) {
            return lock.getLockID();
        }
        return -1;
    }

    public boolean hasGlobalLock(long transactionID) {
        synchronized(locks) {
            DataLock lock = locks.get(DataLock.GLOBAL_LOCK);
            return lock != null && lock.getLockID() == transactionID;
        }
    }

    public boolean hasLocalLock(long transactionID, DataKey object) {
        synchronized(locks) {
            DataLock lock = locks.get(object);
            return lock != null && lock.getLockID() == transactionID;
        }
    }

    public boolean releaseGlobalLock(long transactionID) {
        synchronized(locks) {
            DataLock lock = locks.get(DataLock.GLOBAL_LOCK);
            if (lock != null && lock.getLockID() == transactionID) {
                locks.remove(DataLock.GLOBAL_LOCK);
                return true;
            }
        }
        return false;
    }

    public boolean releaseLocalLock(long transactionID) {
        synchronized (locks) {
            for (Iterator<DataKey> it = locks.keySet().iterator(); it.hasNext();) {
                DataLock lock = locks.get(it.next());
                if (lock.getLockID() == transactionID) {
                    locks.remove(lock.getLockObject());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getNonexclusiveAccess() {
        try {
            long startTimestamp = System.currentTimeMillis();
            do {
                synchronized(locks) {
                    if (locks.size() == 0) {
                        // first non-exclusive access
                        newDataLock(DataLock.NONEXCLUSIVE_LOCK, false);
                        numberOfNonexclusiveAccesses = 1;
                        return true;
                    }
                    if (locks.size() == 1 &&
                        locks.get(DataLock.NONEXCLUSIVE_LOCK) != null) {
                        // another non-exclusive access
                        numberOfNonexclusiveAccesses++;
                        return true;
                    }
                }
                try {
                    Thread.sleep(SLEEP_RETRY);
                } catch (InterruptedException ie) {
                }
                clearTimeouts();
            } while (System.currentTimeMillis() >= startTimestamp
                    && System.currentTimeMillis() - startTimestamp < LOCK_TIMEOUT);
        } catch (Exception e) {
        }
        return false;
    }

    public boolean releaseNonexclusiveAccess() {
        synchronized(locks) {
            DataLock lock = locks.get(DataLock.NONEXCLUSIVE_LOCK);
            if (lock != null) {
                if (--numberOfNonexclusiveAccesses == 0) {
                    locks.remove(DataLock.NONEXCLUSIVE_LOCK);
                }
                return true;
            }
            numberOfNonexclusiveAccesses = 0;
        }
        return false;
    }

}
