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

public abstract class DataRecord implements Cloneable {

    protected static String[] FIELDS;
    protected static int[] TYPES;
    protected static HashMap<String,Integer> FIELDIDX;
    protected static String[] KEY;

    protected Object[] data = new Object[FIELDS.length];

    protected static void constructArrays(Vector<String> fields, Vector<Integer> types) {
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

    public static String[] getKey() {
        return KEY;
    }

    protected void set(String fieldName, Object data) {
        this.data[getIndex(fieldName)] = data;
    }

    protected Object get(String fieldName) {
        return this.data[getIndex(fieldName)];
    }

}
