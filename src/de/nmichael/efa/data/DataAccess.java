/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.data;

import java.util.*;

/**
 *
 * @author nick
 */
public abstract class DataAccess implements IDataAccess {

    protected String storageLocation;
    protected String storageObjectName;
    protected String storageObjectType;
    protected String storageUsername;
    protected String storagePassword;

    protected LinkedHashMap<String,Integer> fieldTypes = new LinkedHashMap<String,Integer>();
    protected String[] keyFields;

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

    public void registerDataField(String fieldName, int dataType) throws Exception {
        if (fieldTypes.containsKey(fieldName)) {
            throw new Exception("Field Name is already in use: "+fieldName);
        }
        fieldTypes.put(fieldName, dataType);
    }

    public void setKey(String[] fieldNames) throws Exception {
        for (int i=0; i<fieldNames.length; i++) {
            getFieldType(fieldNames[i]); // just to check for existence
        }
        this.keyFields = fieldNames;
    }

    public String[] getFieldNames() {
        String[] keys = new String[fieldTypes.size()];
        return fieldTypes.keySet().toArray(keys);
    }

    public int getFieldType(String fieldName) throws Exception {
        Integer i = fieldTypes.get(fieldName);
        if (i == null) {
            throw new Exception("Field Name does not exist: "+fieldName);
        }
        return i.intValue();
    }

    public String[] getKeyFieldNames() {
        return this.keyFields;
    }

}
