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

import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.util.Logger;
import java.util.*;

// @i18n complete

public abstract class DataAccess implements IDataAccess {

    protected Persistence persistence;
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

    public static IDataAccess createDataAccess(Persistence persistence, int type, String storageLocation, String storageObjectName, 
            String storageObjectType, String storageObjectDescription) {
        switch(type) {
            case IDataAccess.TYPE_FILE_XML:
                IDataAccess dataAccess = (IDataAccess)new XMLFile(storageLocation, storageObjectName, storageObjectType, storageObjectDescription);
                dataAccess.setPersistence(persistence);
                return dataAccess;
            case IDataAccess.TYPE_DB_SQL:
                return null; // @todo not yet implemented
        }
        return null;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public Persistence getPersistence() {
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
        } catch(Exception e) {
            e.printStackTrace();
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

    public DataKey getUnversionizedKey(DataKey key) {
        boolean[] bUnversionized = new boolean[keyFields.length];
        for (int i=0; i<keyFields.length; i++) {
            bUnversionized[i] = !keyFields[i].equals(DataRecord.VALIDFROM);
        }
        return new DataKey(key,bUnversionized); // this is the corresponding "unversionized" key (i.e. key with only unversionized fields)
    }

    public static long getTimestampFromDate(DataTypeDate date) {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(date.getYear(), date.getMonth()-1, date.getDay());
        return cal.getTimeInMillis();
    }

}
