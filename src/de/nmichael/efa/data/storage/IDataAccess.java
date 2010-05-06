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

    public static final int DATA_STRING = 0;
    public static final int DATA_INTEGER = 1;
    public static final int DATA_LONGINT = 2;
    public static final int DATA_DECIMAL = 3;
    public static final int DATA_BOOLEAN = 4;
    public static final int DATA_DATE = 5;
    public static final int DATA_TIME = 6;


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
     * Creates a new storage object (overwrites existing objects).
     * For file systems, this method may imply recursive creation of directories as well.
     * In order to succeed, the storage object location, object name and objecet type must have been specified before.
     * @throws Exception if the creation of the object failed.
     */
    public void createStorageObject() throws Exception;

    /**
     * Opens an exisiting storage object.
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
     * Locks the entire storage object for exclusive access and starts a transaction.
     * Locking only affects DDL and DML operations. Storage Object operations
     * (as for example closing the storage object) are still permitted.
     * Locking may time out depending on the implementation of the underlying storage object.
     * @return a transaction ID
     * @throws Exception if the storage object is already locked or cannot be
     * locked at the moment (e.g. because it has already been closed)
     */
    public long lock() throws Exception;

    /**
     * Locks one data record in the storage object for exclusive access and starts a transaction.
     * Locking only affects DDL and DML operations. Storage Object operations
     * (as for example closing the storage object) are still permitted.
     * Locking may time out depending on the implementation of the underlying storage object.
     * @return a transaction ID
     * @throws Exception if the data record or the storage object is already locked or cannot be
     * locked at the moment (e.g. because it has already been closed)
     */
    public long lock(DataKey key) throws Exception;

    /**
     * Commits a previously started transaction.
     * @param transactionID the transaction ID
     * @return the number of successfully committed data records
     * @throws Exception if the transaction ID is invalid, the lock has expired or
     * the transaction cannot be completed for some other reason.
     */
    public long commit(long transactionID) throws Exception;

    /**
     * Rolls back all modifications from a previously started transaction.
     * Only DML operations can be rolled back.
     * @param transactionID the transaction ID
     * @return the number of successfully rolled back data records
     * @throws Exception if the transaction ID is invalid, the lock has expired or
     * the transaction cannot be completed for some other reason.
     */
    public long rollback(long transactionID) throws Exception;

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
     * The storage object must be locked through the lock() method previous to this operation.
     * @param transactionID the transaction ID
     * @param fieldNames an array of existing fields to be used as key.
     * @throws Exception
     */
    public void setKey(long transactionID, String[] fieldNames) throws Exception;

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
     */
    public DataKey constructKey(DataRecord record);

    /**
     * Adds a new data record to this storage object.
     * This is an atomic operation and requires no transaction.
     * @param record the data record to add
     * @throws Exception if the data record already exists or the operation fails for another reason
     */
    public void add(DataRecord record) throws Exception;

    /**
     * Adds a new data record to this storage object.
     * This operation is part of a previously opened transaction.
     * @param record the data record to add
     * @throws Exception if the data record already exists or the operation fails for another reason
     */
    public void add(long transactionID, DataRecord record) throws Exception;

    /**
     * Adds a new data record to or updates an existing one in this storage object.
     * This is an atomic operation and requires no transaction.
     * @param record the data record to add or update
     * @throws Exception if the data record is locked or the operation fails for another reason
     */
    public void addOrUpdate(DataRecord record) throws Exception;

    /**
     * Adds a new data record to or updates an existing one in this storage object.
     * This operation is part of a previously opened transaction.
     * @param record the data record to add or update
     * @throws Exception if the data record is locked or the operation fails for another reason
     */
    public void addOrUpdate(long transactionID, DataRecord record) throws Exception;

    /**
     * Deletes an existing data record from this storage object.
     * This is an atomic operation and requires no transaction.
     * @param key the key of the data record to delete
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public void delete(DataKey key) throws Exception;

    /**
     * Deletes an existing data record from this storage object.
     * This operation is part of a previously opened transaction.
     * @param key the key of the data record to delete
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public void delete(long transactionID, DataKey key) throws Exception;

    /**
     * Retrieves an existing data record from this storage object.
     * @param key the key of the data record to retrieve
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public DataRecord get(DataKey key) throws Exception;

    /**
     * Retrieves an existing data record from this storage object.
     * This operation is part of a previously opened transaction.
     * @param key the key of the data record to retrieve
     * @throws Exception if the data record does not exist, is locked or the operation fails for another reason
     */
    public DataRecord get(long transactionID, DataKey key) throws Exception;

    /**
     * Returns the number of data records in this storage object.
     * @return the number of data records
     * @throws Exception
     */
    public long getNumberOfRecords() throws Exception;

    public long getIterator() throws Exception;
    public DataRecord getExact(long iterator, DataKey key) throws Exception;
    public DataRecord getFirst(long iterator) throws Exception;
    public DataRecord getNext(long iterator) throws Exception;
    public DataRecord getPrev(long iterator) throws Exception;
    public DataRecord getLast(long iterator) throws Exception;


    /*
     * @todo
     * - Callback Mechanism for Data Changes?
     */


}
