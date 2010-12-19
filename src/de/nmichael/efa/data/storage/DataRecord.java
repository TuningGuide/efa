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
    }

    protected static MetaData constructMetaData(String dataType, Vector<String> fields, Vector<Integer> types, boolean versionized) {
        fields.add(DataRecord.LASTMODIFIED);        types.add(IDataAccess.DATA_LONGINT);
        if (versionized) {
            fields.add(DataRecord.VALIDFROM);       types.add(IDataAccess.DATA_LONGINT);
            fields.add(DataRecord.INVALIDFROM);     types.add(IDataAccess.DATA_LONGINT);
        }
        return MetaData.constructMetaData(dataType, fields, types);
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

    public abstract DataKey getKey();
    
    protected void set(int fieldIdx, Object data) {
        if (data != null) {
            switch (getFieldType(fieldIdx)) {
                case IDataAccess.DATA_STRING:
                    if (!(data instanceof String)) {
                        throw new IllegalArgumentException("Data Type DATA_STRING expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_INTEGER:
                    if (!(data instanceof Integer)) {
                        throw new IllegalArgumentException("Data Type DATA_INTEGER expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_LONGINT:
                    if (!(data instanceof Long)) {
                        throw new IllegalArgumentException("Data Type DATA_LONGINT expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_DECIMAL:
                    if (!(data instanceof DataTypeDecimal)) {
                        throw new IllegalArgumentException("Data Type DATA_DECIMAL expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_BOOLEAN:
                    if (!(data instanceof Boolean)) {
                        throw new IllegalArgumentException("Data Type DATA_BOOLEAN expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_DATE:
                    if (!(data instanceof DataTypeDate)) {
                        throw new IllegalArgumentException("Data Type DATA_DATE expected for Data Field " + fieldIdx + ".");
                    }
                    break;
                case IDataAccess.DATA_TIME:
                    if (!(data instanceof DataTypeTime)) {
                        throw new IllegalArgumentException("Data Type DATA_TIME expected for Data Field " + fieldIdx + ".");
                    }
                    break;
            }
        }
        synchronized (this.data) {
            this.data[fieldIdx] = data;
        }
    }

    protected void set(String fieldName, Object data) {
        set(metaData.getIndex(fieldName), data);
    }

    protected Object get(int fieldIdx) {
        synchronized(data) {
            return this.data[fieldIdx];
        }
    }

    protected Object get(String fieldName) {
        return get(metaData.getIndex(fieldName));
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
            return null;
        }
        return new Boolean(bool);
    }

}
