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

// @i18n complete

public abstract class DataAccess implements IDataAccess {

    protected String storageLocation;
    protected String storageObjectName;
    protected String storageObjectType;
    protected String storageUsername;
    protected String storagePassword;
    protected String storageObjectVersion;

    protected final LinkedHashMap<String,Integer> fieldTypes = new LinkedHashMap<String,Integer>();
    protected String[] keyFields;

    public static IDataAccess createDataAccess(int type, String storageLocation, String storageObjectName, String storageObjectType) {
        switch(type) {
            case IDataAccess.TYPE_FILE_CSV:
                return (IDataAccess)new CSVFile(storageLocation, storageObjectName, storageObjectType);
            case IDataAccess.TYPE_FILE_XML:
                return (IDataAccess)new XMLFile(storageLocation, storageObjectName, storageObjectType);
            case IDataAccess.TYPE_DB_SQL:
                return null; // @todo not yet implemented
        }
        return null;
    }


    public void setStorageLocation(String location) {
        this.storageLocation = location;
    }

    public String getStorageLocation() {
        return this.storageLocation;
    }

    public void setStorageObjectName(String name) {
        this.storageObjectName = name;
    }

    public String getStorageObjectName() {
        return this.storageObjectName;
    }

    public void setStorageObjectType(String type) {
        this.storageObjectType = type;
    }

    public String getStorageObjectType() {
        return this.storageObjectType;
    }

    public void setStorageUsername(String username) {
        this.storageUsername = username;
    }

    public void setStoragePassword(String password) {
        this.storagePassword = password;
    }

    public String getStorageUsername() {
        return this.storageUsername;
    }

    public String getStoragePassword() {
        return this.storagePassword;
    }

    public String getStorageObjectVersion() {
        return this.storageObjectVersion;
    }

    public void setStorageObjectVersion(String version) {
        this.storageObjectVersion = version;
    }

    public void registerDataField(String fieldName, int dataType) throws Exception {
        if (fieldTypes.containsKey(fieldName)) {
            throw new Exception("Field Name is already in use: "+fieldName);
        }
        synchronized(fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            fieldTypes.put(fieldName, dataType);
        }
    }


    public void setKey(long transactionID, String[] fieldNames) throws Exception {
        synchronized (fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            for (int i = 0; i < fieldNames.length; i++) {
                getFieldType(fieldNames[i]); // just to check for existence
            }
            this.keyFields = fieldNames;
        }
    }

    public String[] getKeyFieldNames() {
        String[] names = null;
        synchronized (fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            names = new String[this.keyFields.length];
            for (int i=0; i<names.length; i++) {
                names[i] = this.keyFields[i];
            }
        }
        return names;
    }

    public String[] getFieldNames() {
        synchronized (fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            String[] keys = new String[fieldTypes.size()];
            return fieldTypes.keySet().toArray(keys);
        }
    }

    public int getFieldType(String fieldName) throws Exception {
        Integer i = null;
        synchronized (fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            i = fieldTypes.get(fieldName);
        }
        if (i == null) {
            throw new Exception("Field Name does not exist: "+fieldName);
        }
        return i.intValue();
    }

    public DataKey constructKey(DataRecord record) {
        Object v1 = null;
        Object v2 = null;
        Object v3 = null;

        if (keyFields.length >= 1) {
            v1 = record.get(keyFields[0]);
        }
        if (keyFields.length >= 2) {
            v2 = record.get(keyFields[1]);
        }
        if (keyFields.length >= 3) {
            v3 = record.get(keyFields[2]);
        }

        return new DataKey(v1, v2, v3);
    }

}
