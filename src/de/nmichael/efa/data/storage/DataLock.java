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

// @i18n complete

public class DataLock {

    public final static DataKey GLOBAL_LOCK = new DataKey<String,String,String>("%%%GLOBAL_LOCK%%%", null, null);
    public final static DataKey NONEXCLUSIVE_LOCK = new DataKey<String,String,String>("%%%NONEXCLUSIVE_LOCK%%%", null, null);

    private long lockID;
    private DataKey lockObject;
    private boolean exclusive;
    private Thread lockOwner;
    private long lockTime;

    public DataLock(long lockID, DataKey lockObject, boolean exclusive) {
        this.lockObject = lockObject;
        this.exclusive = exclusive;
        this.lockTime = System.currentTimeMillis();
        this.lockID = lockID;
        if (exclusive) {
            this.lockOwner = Thread.currentThread();
        }
    }

    public long getLockID() {
        return lockID;
    }

    public DataKey getLockObject() {
        return lockObject;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public long getLockTime() {
        return lockTime;
    }

    public String toString() {
        String threadInfo;
        try {
            threadInfo = lockOwner.getName() + "[" + lockOwner.getId() + "]";
        } catch(Exception e) {
            threadInfo = "<unknown";
        }
        return (exclusive ?
               "Lock[" + lockID + "] Type=" + (lockObject.equals(GLOBAL_LOCK) ? "global" : "local Object=" + lockObject) +
               " acquired at " + lockTime + " owned by " + threadInfo :
               "Nonexclusive Lock[" + lockID + "] last acquired at " + lockTime);
    }

}
