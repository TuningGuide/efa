/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

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

    public void setStorageUsername(String username);
    public void setStoragePassword(String password);
    public String getStorageUsername();
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

    public void lock() throws Exception;
    public void lock(String key) throws Exception;
    public int commit() throws Exception;
    public int rollback() throws Exception;
    public long getSCN() throws Exception;

    public String getStorageObjectVersion() throws Exception;
    public void setStorageObjectVersion(String version) throws Exception;

    public void registerDataField(String fieldName, int dataType) throws Exception;
    public void setKey(String[] fieldNames) throws Exception;
    public String[] getKeyFieldNames();
    public String[] getFieldNames();
    public int getFieldType(String fieldName) throws Exception;

    public void add(DataRecord record) throws Exception;
    public void addOrUpdate(DataRecord record) throws Exception;
    public DataRecord get(String key) throws Exception;
    public void delete(String key) throws Exception;

    public DataRecord getFirst() throws Exception;
    public DataRecord getNext() throws Exception;
    public DataRecord getPrev() throws Exception;
    public DataRecord getLast() throws Exception;

    public int getNumberOfRecords() throws Exception;

    /*
     * @todo
     * - Callback Mechanism for Data Changes?
     */


}
