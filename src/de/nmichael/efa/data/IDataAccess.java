/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.data;

/**
 *
 * @author nick
 */
public interface IDataAccess {

    public static final int TYPE_FILE_CSV = 0;
    public static final int TYPE_FILE_XML = 1;
    public static final int TYPE_DB_SQL = 2;

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

    /*
     * @todo
     * - Versioning
     * - Field Names and Field Types
     * - Data Manipulation Methods: add record, delete record, update record
     * - Callback Mechanism for Data Changes?
     */


}
