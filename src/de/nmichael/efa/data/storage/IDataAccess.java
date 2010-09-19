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

public interface IDataAccess {

    public static final int TYPE_FILE_CSV = 0;
    public static final int TYPE_FILE_XML = 1;
    public static final int TYPE_DB_SQL = 2;

    // Data Types supported by IDataAccess
    //                      Data Type            Internal Java Type
    public static final int DATA_STRING = 0;  // String
    public static final int DATA_INTEGER = 1; // int, Integer
    public static final int DATA_LONGINT = 2; // long, Long
    public static final int DATA_DECIMAL = 3; // DataTypeDecimal
    public static final int DATA_BOOLEAN = 4; // boolean, Boolean
    public static final int DATA_DATE = 5;    // DataTypeDate
    public static final int DATA_TIME = 6;    // DataTypeTime


    /**
     * Sets the associated Persistence object for this Data Access.
     */
    public void setPersistence(Persistence persistence);

    /**
     * Returns the associated Persistence object for this Data Access.
     * @return the Persistence object
     */
    public Persistence getPersistence();

    /**
     * Returns the storage type of this implementation (e.g. CSV file, XML file or SQL database)
     * @return one of the TYPE_xxx constants
     */
    public int getStorageType();

    /**
     * Sets the storage location (e.g. file system directory or database connect string)
     * Examples:
     * /home/efa/data/ (with or without trailing file.separator)
     * jdbc:mysql://localhost:1234/efa
     * Note: the combination StorageLocation/StorageObjectName.StorageObjectType must be unique!
     * @param location the storage location
     */
    public void setStorageLocation(String location);

    /**
     * Returns the storage location (e.g. file system directory (always with trailing file.separator) or database connect string)
     * @return the storage location
     */
    public String getStorageLocation();

    /**
     * Sets the storage object name (e.g. file name (without file extension) or database table name (just postfix))
     * Examples:
     * 2009 (for a logbook with the name "2009")
     * Note: the combination StorageLocation/StorageObjectName.StorageObjectType must be unique!
     * @param name the storage object name
     */
    public void setStorageObjectName(String name);

    /**
     * Returns the storage object name (e.g. file name (without file extension) or database table name (just postfix))
     * @return the storage object name
     */
    public String getStorageObjectName();

    /**
     * Sets the storage object type (e.g. logbook, members list, boat list, ...) which may be used as a file extension or databale table name prefix
     * Examples:
     * efb (for a logbook)
     * efbm (for a members list)
     * efbb (for a boat list)
     * Note: the combination StorageLocation/StorageObjectName.StorageObjectType must be unique!
     * @param type the storage object name
     */
    public void setStorageObjectType(String type);

    /**
     * Returns the storage object type (e.g. logbook, members list, boat list, ...) which may be used as a file extension or databale table name prefix
     * @return the storage object type
     */
    public String getStorageObjectType();

    /**
     * Sets the username to access the storage object.
     * @param username the username to access the storage object
     */
    public void setStorageUsername(String username);

    /**
     * Sets the password to access the storage object.
     * @param password the password to access the storage object
     */
    public void setStoragePassword(String password);

    /**
     * Returns the username to access the storage object
     * @return the username to access the storage object
     */
    public String getStorageUsername();

    /**
     * Returns the password to access the storage object
     * @return the password to access the storage object
     */
    public String getStoragePassword();

    /**
     * Tests whether a storage object already exists.
     * For file systems, this tests whether the associated file in the file system exists.
     * In order to succeed, the storage object location, object name and objecet type must have been specified before.
     * @throws Exception if the existance of the storage object could not be verified.
     */
    public boolean existsStorageObject() throws Exception;

    /**
     * Creates a new storage object (overwrites existing objects).
     * For file systems, this method may imply recursive creation of directories as well.
     * In order to succeed, the storage object location, object name and objecet type must have been specified before.
     * @throws Exception if the creation of the object failed.
     */
    public void createStorageObject() throws Exception;

    /**
     * Opens an existing storage object.
     * @throws Exception if the opening of the object failed.
     */
    public void openStorageObject() throws Exception;

    /**
     * Closes this storage object. Uncommitted changes to this object will be lost.
     * @throws Exception if the closing of this object failed.
     */
    public void closeStorageObject() throws Exception;

    /**
     * Checks whether the storage object is currently open.
     * @return true if the storage object is open
     */
    public boolean isStorageObjectOpen();

    /**
     * Returns the current version of the storage object.
     * @return the version identifier
     * @throws Exception
     */
    public String getStorageObjectVersion() throws Exception;

    /**
     * Sets the current version of the storage object.
     * @param version the version identifier
     * @throws Exception
     */
    public void setStorageObjectVersion(String version) throws Exception;

    /**
     * Locks the entire storage object for exclusive write access.
     * Locking only affects DML operations. Storage Object operations
     * (as for example closing the storage object) are still permitted.
     * Locking may time out depending on the implementation of the underlying storage object.
     * @return a lock ID
     * @throws Exception if the storage object is already locked or cannot be
     * locked at the moment (e.g. because it has already been closed)
     */
    public long acquireGlobalLock() throws Exception;

    /**
     * Locks one data record in the storage object for exclusive write access.
     * Locking only affects DML operations. Storage Object operations
     * (as for example closing the storage object) are still permitted.
     * Locking may time out depending on the implementation of the underlying storage object.
     * @return a lock ID
     * @throws Exception if the data record or the storage object is already locked or cannot be
     * locked at the moment (e.g. because it has already been closed)
     */
    public long acquireLocalLock(DataKey key) throws Exception;

    /**
     * Releases a previous acquired global lock.
     * @param lockID the lock ID
     */
    public void releaseGlobalLock(long lockID) throws Exception;

    /**
     * Releases a previous acquired local lock.
     * @param lockID the lock ID
     */
    public void releaseLocalLock(long lockID) throws Exception;

    /**
     * Returns the current SCN.
     * @return the SCN
     * @throws Exception
     */
    public long getSCN() throws Exception;

    /**
     * Registers a new data field.
     * @param fieldName the name of the new field
     * @param dataType the type of the new field
     * @throws Exception
     */
    public void registerDataField(String fieldName, int dataType) throws Exception;


    /**
     * Specifies the key fields for this storage object. The combination of key field
     * values must be unique in the storage object.
     * @param fieldNames an array of existing fields to be used as key.
     * @throws Exception
     */
    public void setKey(String[] fieldNames) throws Exception;

    /**
     * Returns the names of the key fields of this storage object.
     * @return the key field names
     */
    public String[] getKeyFieldNames();

    /**
     * Returns all field names of this storage object.
     * @return the field names
     */
    public String[] getFieldNames();

    /**
     * Returns the field type for a given field name.
     * @param fieldName the field name
     * @return the field type
     * @throws Exception
     */
    public int getFieldType(String fieldName) throws Exception;

    /**
     * Constructs a key from a given (non-empty) DataRecord.
     * @param record the data record
     * @return the key
     * @throws Exception
     */
    public DataKey constructKey(DataRecord record) throws Exception;

    /**
     * Adds a new data record to this storage object.
     * @param record the data record to add
     * @throws Exception if the data record already exists or the operation fails for another reason
     */
    public void add(DataRecord record) throws Exception;

    /**
     * Adds a new data record to this storage object with a previously acquired local or global lock.
     * @param record the data record to add
     * @param lockID an ID of a previously acquired local or global lock
     * @throws Exception if the data record already exists or the operation fails for another reason
     */
    public void add(DataRecord record, long lockID) throws Exception;

    /**
     * Adds a new data record to or updates an existing one in this storage object.
     * @param record the data record to add or update
     * @throws Exception if the data record is locked or the operation fails for another reason
     */
    public void addOrUpdate(DataRecord record) throws Exception;

    /**
     * Adds a new data record to or updates an existing one in this storage object with a previously acquired local or global lock.
     * @param record the data record to add or update
     * @param lockID an ID of a previously acquired local or global lock
     * @throws Exception if the data record is locked or the operation fails for another reason
     */
    public void addOrUpdate(DataRecord record, long lockID) throws Exception;

    /**
     * Deletes an existing data record from this storage object.
     * @param key the key of the data record to delete
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public void delete(DataKey key) throws Exception;

    /**
     * Deletes an existing data record from this storage object with a previously acquired local or global lock.
     * @param key the key of the data record to delete
     * @param lockID an ID of a previously acquired local or global lock
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public void delete(DataKey key, long lockID) throws Exception;

    /**
     * Retrieves an existing data record from this storage object.
     * @param key the key of the data record to retrieve
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public DataRecord get(DataKey key) throws Exception;

    /**
     * Returns the number of data records in this storage object.
     * @return the number of data records
     * @throws Exception
     */
    public long getNumberOfRecords() throws Exception;

    /**
     * Truncates (deletes) all data records in this storage object.
     * @throws Exception
     */
    public void truncateAllData() throws Exception;

    public DataKeyIterator getIterator() throws Exception;
    public DataRecord getCurrent(DataKeyIterator it) throws Exception;
    public DataRecord getFirst(DataKeyIterator it) throws Exception;
    public DataRecord getLast(DataKeyIterator it) throws Exception;
    public DataRecord getNext(DataKeyIterator it) throws Exception;
    public DataRecord getPrev(DataKeyIterator it) throws Exception;


    /*
     * @todo
     * - Callback Mechanism for Data Changes?
     */


}
