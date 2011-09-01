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

import de.nmichael.efa.Daten;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.util.Logger;
import java.util.*;

// @i18n complete

public abstract class DataAccess implements IDataAccess {

    protected StorageObject persistence;
    protected String storageLocation;
    protected String storageObjectName;
    protected String storageObjectType;
    protected String storageObjectDescription;
    protected String storageUsername;
    protected String storagePassword;
    protected String storageObjectVersion;

    protected final LinkedHashMap<String,Integer> fieldTypes = new LinkedHashMap<String,Integer>();
    protected String[] keyFields;
    protected MetaData meta;
    protected DataRecord referenceRecord;
    protected boolean inOpeningStorageObject = false;
    protected boolean isPreModifyRecordCallbackEnabled = true;

    public static IDataAccess createDataAccess(StorageObject persistence,
            int type,
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName,
            String storageObjectType,
            String storageObjectDescription) {
        IDataAccess dataAccess = null;
        switch(type) {
            case IDataAccess.TYPE_FILE_XML:
                dataAccess = (IDataAccess)new XMLFile(storageLocation, storageObjectName, storageObjectType, storageObjectDescription);
                dataAccess.setPersistence(persistence);
                return dataAccess;
            case IDataAccess.TYPE_DB_SQL:
                return null; // @todo (P4) TYPE_DB_SQL not yet implemented
            case IDataAccess.TYPE_EFA_REMOTE:
                 dataAccess = (IDataAccess)new RemoteEfaClient(storageLocation, storageUsername, storagePassword, storageObjectName, storageObjectType, storageObjectDescription);
                 dataAccess.setPersistence(persistence);
                return dataAccess;
        }
        return null;
    }

    public void setPersistence(StorageObject persistence) {
        this.persistence = persistence;
    }

    public StorageObject getPersistence() {
        return persistence;
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

    public void setStorageObjectDescription(String description) {
        this.storageObjectDescription = description;
    }

    public String getStorageObjectDescription() {
        return this.storageObjectDescription;
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

    public void registerDataField(String fieldName, int dataType) throws EfaException {
        if (fieldTypes.containsKey(fieldName)) {
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION,getUID() + ": Field Name is already in use: "+fieldName, Thread.currentThread().getStackTrace());
        }
        synchronized(fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            fieldTypes.put(fieldName, dataType);
        }
    }


    public void setKey(String[] fieldNames) throws EfaException {
        synchronized (fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            for (int i = 0; i < fieldNames.length; i++) {
                getFieldType(fieldNames[i]); // just to check for existence
            }
            this.keyFields = fieldNames;
        }
    }

    public void setMetaData(MetaData meta) {
        this.meta = meta;
        try {
            for (int i=0; i<meta.getNumberOfFields(); i++) {
                registerDataField(meta.getFieldName(i), meta.getFieldType(i));
            }
            setKey(meta.getKeyFields());
            String[][] indexFields = meta.getIndices();
            for (int i=0; i<indexFields.length; i++) {
                createIndex(indexFields[i]);
            }
            referenceRecord = persistence.createNewRecord();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public MetaData getMetaData() {
        return meta;
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

    public int getFieldType(String fieldName) throws EfaException {
        Integer i = null;
        synchronized (fieldTypes) { // fieldTypes used for synchronization of fieldTypes and keyFields as well
            i = fieldTypes.get(fieldName);
        }
        if (i == null) {
            throw new EfaException(Logger.MSG_DATA_FIELDDOESNOTEXIST, getUID() + ": Field Name does not exist: "+fieldName, Thread.currentThread().getStackTrace());
        }
        return i.intValue();
    }

    public DataKey constructKey(DataRecord record) throws EfaException {
        Object v1 = null;
        Object v2 = null;
        Object v3 = null;

        if (keyFields.length >= 1) {
            v1 = (record != null ? record.get(keyFields[0]) : null);
        }
        if (keyFields.length >= 2) {
            v2 = (record != null ? record.get(keyFields[1]) : null);
        }
        if (keyFields.length >= 3) {
            v3 = (record != null ? record.get(keyFields[2]) : null);
        }

        return new DataKey(v1, v2, v3);
    }

    public DataKey getUnversionizedKey(DataKey key) {
        boolean[] bUnversionized = new boolean[keyFields.length];
        for (int i=0; i<keyFields.length; i++) {
            bUnversionized[i] = !keyFields[i].equals(DataRecord.VALIDFROM);
        }
        return new DataKey(key,bUnversionized); // this is the corresponding "unversionized" key (i.e. key with only unversionized fields)
    }

    public String getTypeName(int type) {
        switch(type) {
            case DATA_STRING:
                return "STRING";
            case DATA_INTEGER:
                return "INTEGER";
            case DATA_LONGINT:
                return "LONGINT";
            case DATA_DECIMAL:
                return "DECIMAL";
            case DATA_DISTANCE:
                return "DISTANCE";
            case DATA_BOOLEAN:
                return "BOOLEAN";
            case DATA_DATE:
                return "DATE";
            case DATA_TIME:
                return "TIME";
            case DATA_UUID:
                return "UUID";
            case DATA_INTSTRING:
                return "INTSTRING";
            case DATA_LIST_STRING:
                return "LIST_STRING";
            case DATA_LIST_INTEGER:
                return "LIST_INTEGER";
            case DATA_LIST_UUID:
                return "LIST_UUID";
            case DATA_VIRTUAL:
                return "VIRTUAL";
            default: return "UNKNOWN";
        }
    }

    public boolean inOpeningStorageObject() {
        return this.inOpeningStorageObject;
    }

    public void setPreModifyRecordCallbackEnabled(boolean enabled) {
        this.isPreModifyRecordCallbackEnabled = enabled;
    }

    public boolean isPreModifyRecordCallbackEnabled() {
        return this.isPreModifyRecordCallbackEnabled && (Daten.efaConfig == null || Daten.efaConfig.getValueDataPreModifyRecordCallbackEnabled());
    }

}
