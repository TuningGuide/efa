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

// @i18n complete

public abstract class DataRecord implements Cloneable {

    private static final String LASTMODIFIED     = "LastModified";
    private static final String VALIDFROM        = "ValidFrom";
    private static final String INVALIDFROM      = "InvalidFrom";

    protected static String[] FIELDS;
    protected static int[] TYPES;
    protected static HashMap<String,Integer> FIELDIDX;
    protected static String[] KEY;

    protected final Object[] data = new Object[FIELDS.length];

    protected static void constructArrays(Vector<String> fields, Vector<Integer> types, boolean versionized) {
        fields.add(LASTMODIFIED);        types.add(IDataAccess.DATA_LONGINT);
        if (versionized) {
            fields.add(VALIDFROM);       types.add(IDataAccess.DATA_LONGINT);
            fields.add(INVALIDFROM);     types.add(IDataAccess.DATA_LONGINT);
        }
        FIELDS = new String[fields.size()];
        TYPES = new int[types.size()];
        FIELDIDX = new HashMap<String,Integer>();
        for (int i=0; i<FIELDS.length; i++) {
            FIELDS[i] = fields.get(i);
            TYPES[i] = types.get(i).intValue();
            FIELDIDX.put(FIELDS[i], i);
        }
    }

    public DataRecord clone()  {
        try {
            return this.getClass().getConstructor(this.getClass()).newInstance(this);
        } catch (Exception e) {
            throw new InternalError(e.toString());
        }
    }

    protected static int getIndex(String fieldName) {
        return FIELDIDX.get(fieldName).intValue();
    }

    public static int getFieldCount() {
        return FIELDS.length;
    }

    public static String getFieldName(int i) {
        return FIELDS[i];
    }

    public static int getFieldType(int i) {
        return TYPES[i];
    }

    public static String[] getKeyFields() {
        return Arrays.copyOf(KEY, KEY.length);
    }

    public abstract DataKey getKey();
    
    protected void set(int fieldIdx, Object data) {
        if (data != null) {
            switch (TYPES[fieldIdx]) {
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
        set(getIndex(fieldName), data);
    }

    protected Object get(int fieldIdx) {
        synchronized(data) {
            return this.data[fieldIdx];
        }
    }

    protected Object get(String fieldName) {
        return get(getIndex(fieldName));
    }

}
