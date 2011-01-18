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

import java.util.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.util.*;

// @i18n complete

public abstract class DataRecord implements Cloneable {

    protected static final String LASTMODIFIED     = "LastModified";
    protected static final String VALIDFROM        = "ValidFrom";
    protected static final String INVALIDFROM      = "InvalidFrom";

    protected MetaData metaData;
    protected Object[] data;

    public DataRecord(MetaData metaData) {
        this.metaData = metaData;
        data = new Object[metaData.getNumberOfFields()];
        if (metaData.versionized) {
            setAlwaysValid();
        }
    }

    protected static MetaData constructMetaData(String dataType, Vector<String> fields, Vector<Integer> types, boolean versionized) {
        if (versionized) {
            fields.add(DataRecord.VALIDFROM);       types.add(IDataAccess.DATA_LONGINT);
            fields.add(DataRecord.INVALIDFROM);     types.add(IDataAccess.DATA_LONGINT);
        }
        // LastModified must always be the last field; this class's set(int, Object) method implicitly uses this to update the timestamp!
        fields.add(DataRecord.LASTMODIFIED);        types.add(IDataAccess.DATA_LONGINT);
        return MetaData.constructMetaData(dataType, fields, types, versionized);
    }

/*
    public DataRecord clone()  {
        try {
            return this.getClass().getConstructor(this.getClass(), metaData.getClass()).newInstance(this, metaData);
        } catch (Exception e) {
            throw new InternalError(e.toString());
        }
    }
 */

    public abstract DataRecord createDataRecord();

    public DataRecord cloneRecord() {
        DataRecord rec = createDataRecord();
        synchronized(this.data) {
            for (int i = 0; i < this.data.length; i++) {
                rec.data[i] = this.data[i];
            }
        }
        return rec;
    }

    public int getFieldCount() {
        return metaData.getNumberOfFields();
    }

    public String getFieldName(int i) {
        return metaData.getFieldName(i);
    }

    public int getFieldType(int i) {
        return metaData.getFieldType(i);
    }

    public String[] getKeyFields() {
        return metaData.getKeyFields();
    }

    public boolean isKeyField(int fieldIdx) {
        return metaData.isKeyField(fieldIdx);
    }

    public boolean isKeyField(String fieldName) {
        return metaData.isKeyField(fieldName);
    }

    public abstract DataKey getKey();
    
    protected void set(int fieldIdx, Object data) {
        if (data != null) {
            int type = getFieldType(fieldIdx);
            if (data instanceof String && type != IDataAccess.DATA_STRING) {
                data = transformDataStringToType((String)data, type);
                if (data == null) {
                    throw new IllegalArgumentException("Data of Type String could not be transformed to Type " + type + " for Data Field " + fieldIdx + ".");
                }
            }
            switch (type) {
                case IDataAccess.DATA_STRING:
                    if (!(data instanceof String)) {
                        throw new IllegalArgumentException("Data Type STRING expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_INTEGER:
                    if (!(data instanceof Integer)) {
                        throw new IllegalArgumentException("Data Type INTEGER expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_LONGINT:
                    if (!(data instanceof Long)) {
                        throw new IllegalArgumentException("Data Type LONGINT expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_DECIMAL:
                    if (!(data instanceof DataTypeDecimal)) {
                        throw new IllegalArgumentException("Data Type DECIMAL expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_BOOLEAN:
                    if (!(data instanceof Boolean)) {
                        throw new IllegalArgumentException("Data Type BOOLEAN expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_DATE:
                    if (!(data instanceof DataTypeDate)) {
                        throw new IllegalArgumentException("Data Type DATE expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_TIME:
                    if (!(data instanceof DataTypeTime)) {
                        throw new IllegalArgumentException("Data Type TIME expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_UUID:
                    if (!(data instanceof UUID)) {
                        throw new IllegalArgumentException("Data Type UUID expected for Data Field " + fieldIdx + ".");
                    }
                    break;
            }
        }
        synchronized (this.data) {
            this.data[fieldIdx] = data;
            this.data[getFieldCount() - 1] = (Long)System.currentTimeMillis(); // LastModified timestamp
        }
    }

    protected void set(String fieldName, Object data) {
        set(metaData.getFieldIndex(fieldName), data);
    }

    protected Object get(int fieldIdx) {
        synchronized(data) {
            return this.data[fieldIdx];
        }
    }

    protected Object get(String fieldName) {
        return get(metaData.getFieldIndex(fieldName));
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[");
        for (int i=0; i<getFieldCount(); i++) {
            Object v = get(i);
            if (v == null && !isKeyField(i)) {
                continue;
            }
            if (b.length() > 1) {
                b.append(";");
            }
            if (isKeyField(i)) { // Output for Key Field
                b.append("#" + getFieldName(i) + "#" + "=" + 
                        (v != null ? v.toString() : "<UNSET>") );
            } else { // Output for normal Field
                b.append(getFieldName(i) + "=" + v.toString());
            }
        }
        b.append("]");
        return b.toString();
    }

    // =========================================================================
    // Methods for versionized data
    // =========================================================================

    protected void setValidFrom(long t) {
        setLong(VALIDFROM, t);
    }

    protected void setInvalidFrom(long t) {
        setLong(INVALIDFROM, t);
    }

    protected void setAlwaysValid() {
        setValidFrom(0);
        setInvalidFrom(Long.MAX_VALUE);
    }
    
    protected long getValidFrom() {
        long t = getLong(VALIDFROM);
        if (t == IDataAccess.UNDEFINED_LONG || t < 0) {
            return 0;
        }
        return t;
    }

    protected long getInvalidFrom() {
        long t = getLong(INVALIDFROM);
        if (t == IDataAccess.UNDEFINED_LONG || t < 0) {
            return Long.MAX_VALUE;
        }
        return t;
    }

    // =========================================================================
    // DataType specific get and set functions
    // =========================================================================

    protected void setString(String fieldName, String s) {
        set(fieldName, s);
    }

    protected void setDate(String fieldName, DataTypeDate date) {
        set(fieldName, new DataTypeDate(date));
    }

    protected void setTime(String fieldName, DataTypeTime time) {
        set(fieldName, new DataTypeTime(time));
    }

    protected void setDecimal(String fieldName, DataTypeDecimal decimal) {
        set(fieldName, new DataTypeDecimal(decimal));
    }

    protected void setInt(String fieldName, int i) {
        set(fieldName, new Integer(i));
    }

    protected void setLong(String fieldName, long l) {
        set(fieldName, new Long(l));
    }

    protected void setBool(String fieldName, boolean b) {
        set(fieldName, new Boolean(b));
    }

    protected void setUUID(String fieldName, UUID uuid) {
        set(fieldName, uuid);
    }

    protected void setList(String fieldName, DataTypeList list) {
        set(fieldName, list);
    }

    protected String getString(String fieldName) {
        return (String)get(fieldName);
    }

    protected DataTypeDate getDate(String fieldName) {
        DataTypeDate date = (DataTypeDate)get(fieldName);
        if (date == null) {
            return null;
        }
        return new DataTypeDate(date);
    }

    protected DataTypeTime getTime(String fieldName) {
        DataTypeTime time = (DataTypeTime)get(fieldName);
        if (time == null) {
            return null;
        }
        return new DataTypeTime(time);
    }

    protected DataTypeDecimal getDecimal(String fieldName) {
        DataTypeDecimal d = (DataTypeDecimal)get(fieldName);
        if (d == null) {
            return null;
        }
        return new DataTypeDecimal(d);
    }

    protected int getInt(String fieldName) {
        Integer i = (Integer)get(fieldName);
        if (i == null) {
            return IDataAccess.UNDEFINED_INT;
        }
        return i.intValue();
    }

    protected long getLong(String fieldName) {
        Long l = (Long)get(fieldName);
        if (l == null) {
            return IDataAccess.UNDEFINED_LONG;
        }
        return l.longValue();
    }

    protected Boolean getBool(String fieldName) {
        Boolean bool = (Boolean)get(fieldName);
        if (bool == null) {
            return false; // default is false
        }
        return new Boolean(bool);
    }

    protected UUID getUUID(String fieldName) {
        UUID uuid = (UUID)get(fieldName);
        if (uuid == null) {
            return null;
        }
        return uuid;
    }

    protected DataTypeList getList(String fieldName, int dataType) {
        String s = (String)get(fieldName);
        if (s == null) {
            return null;
        }
        return DataTypeList.parseList(s, dataType);
    }

    public static Object transformDataStringToType(String s, int type) {
        switch (type) {
            case IDataAccess.DATA_STRING:
                return s;
            case IDataAccess.DATA_INTEGER:
                return Integer.parseInt(s);
            case IDataAccess.DATA_LONGINT:
                return Long.parseLong(s);
            case IDataAccess.DATA_DECIMAL:
                return DataTypeDecimal.parseDecimal(s);
            case IDataAccess.DATA_BOOLEAN:
                return Boolean.parseBoolean(s);
            case IDataAccess.DATA_DATE:
                return DataTypeDate.parseDate(s);
            case IDataAccess.DATA_TIME:
                return DataTypeTime.parseTime(s);
            case IDataAccess.DATA_UUID:
                return UUID.fromString(s);
        }
        return null;
    }

}
