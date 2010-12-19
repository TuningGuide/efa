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
import de.nmichael.efa.util.*;

public class MetaData {

    private String dataType;
    protected String[] FIELDS;
    protected int[] TYPES;
    protected HashMap<String,Integer> FIELDIDX;
    protected String[] KEY;

    private static Hashtable<String,MetaData> metaData = new Hashtable<String,MetaData>();

    private MetaData(String dataType) {
        this.dataType = dataType;
    }

    public static MetaData constructMetaData(String dataType, Vector<String> fields, Vector<Integer> types) {
        MetaData m = metaData.get(dataType);
        if (m != null) {
            metaData.remove(dataType);
        }
        m = new MetaData(dataType);
        m.FIELDS = new String[fields.size()];
        m.TYPES = new int[types.size()];
        m.FIELDIDX = new HashMap<String,Integer>();
        for (int i=0; i<m.FIELDS.length; i++) {
            m.FIELDS[i] = fields.get(i);
            m.TYPES[i] = types.get(i).intValue();
            m.FIELDIDX.put(m.FIELDS[i], i);
        }
        metaData.put(dataType, m);
        return m;
    }

    public static MetaData getMetaData(String dataType) {
        return metaData.get(dataType);
    }

    public static void removeMetaData(String dataType) {
        if (metaData.get(dataType) != null) {
            metaData.remove(dataType);
        }
    }

    public void setKey(String[] key) {
        KEY = key;
    }

    public int getNumberOfFields() {
        return FIELDS.length;
    }
    
    public int getIndex(String fieldName) {
        try {
            return FIELDIDX.get(fieldName).intValue();
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_DATA_FIELDDOESNOTEXIST, "MetaData.getIndex(\""+fieldName+"\") - Field does not exist!");
            Logger.log(e);
            return -1;
        }
    }

    public String getFieldName(int i) {
        return FIELDS[i];
    }

    public int getFieldType(int i) {
        return TYPES[i];
    }

    public String[] getKeyFields() {
        return Arrays.copyOf(KEY, KEY.length);
    }

}
