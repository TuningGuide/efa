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
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;

// @i18n complete

public abstract class DataRecord implements Cloneable, Comparable {

    public static final String ENCODING_RECORD = "Record";

    public static final String CHANGECOUNT      = "ChangeCount";
    public static final String LASTMODIFIED     = "LastModified";
    public static final String VALIDFROM        = "ValidFrom";
    public static final String INVALIDFROM      = "InvalidFrom";
    public static final String INVISIBLE        = "Invisible";
    public static final String DELETED          = "Deleted";

    protected StorageObject persistence;
    protected MetaData metaData;
    protected Object[] data;

    public DataRecord(StorageObject persistence, MetaData metaData) {
        this.persistence = persistence;
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
            fields.add(DataRecord.INVISIBLE);       types.add(IDataAccess.DATA_BOOLEAN);
            fields.add(DataRecord.DELETED);         types.add(IDataAccess.DATA_BOOLEAN);
        }
        fields.add(DataRecord.CHANGECOUNT);         types.add(IDataAccess.DATA_LONGINT);
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

    public int compareTo(Object o)  {
        return getKey().compareTo( (o != null ? ((DataRecord)o).getKey() : null) );
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
    
    protected void set(int fieldIdx, Object data, boolean updateTimestamp) {
        if (data != null) {
            int type = getFieldType(fieldIdx);
            if (data instanceof String && type != IDataAccess.DATA_STRING) {
                data = transformDataStringToType((String)data, type);
                if (data == null) {
                    throw new IllegalArgumentException(persistence.toString() + ": Data could not be transformed to Type " + persistence.data().getTypeName(type) + " for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                }
            }
            switch (type) {
                case IDataAccess.DATA_STRING:
                    if (!(data instanceof String)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type STRING expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_INTEGER:
                    if (!(data instanceof Integer)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type INTEGER expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_LONGINT:
                    if (!(data instanceof Long)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type LONGINT expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_DECIMAL:
                    if (!(data instanceof DataTypeDecimal)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type DECIMAL expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_DISTANCE:
                    if (!(data instanceof DataTypeDistance)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type DISTANCE expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_BOOLEAN:
                    if (!(data instanceof Boolean)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type BOOLEAN expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_DATE:
                    if (!(data instanceof DataTypeDate)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type DATE expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_TIME:
                    if (!(data instanceof DataTypeTime)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type TIME expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_UUID:
                    if (!(data instanceof UUID)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type UUID expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_INTSTRING:
                    if (!(data instanceof DataTypeIntString)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type INTSTRING expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_PASSWORDH:
                    if (!(data instanceof DataTypePasswordHashed)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type PASSWORDH expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_PASSWORDC:
                    if (!(data instanceof DataTypePasswordCrypted)) {
                        throw new IllegalArgumentException(persistence.toString() + ": Data Type PASSWORDC expected for Data Field " + metaData.getFieldName(fieldIdx) + ".");
                    }
                    break;
                case IDataAccess.DATA_VIRTUAL:
                    // nothing to do
                    return;
            }
        }
        synchronized (this.data) {
            this.data[fieldIdx] = data;
        }
    }

    protected void set(String fieldName, Object data, boolean updateTimestamp) {
        set(metaData.getFieldIndex(fieldName), data, updateTimestamp);
    }

    protected void set(String fieldName, Object data) {
        set(metaData.getFieldIndex(fieldName), data, true);
    }

    protected Object getVirtualColumn(int fieldIdx) {
        return null;
    }

    protected Object get(int fieldIdx) {
        int type = getFieldType(fieldIdx);
        synchronized(data) {
            if (type != IDataAccess.DATA_VIRTUAL) {
                return this.data[fieldIdx];
            } else {
                return getVirtualColumn(fieldIdx);
            }
        }
    }

    protected Object get(String fieldName) {
        return get(metaData.getFieldIndex(fieldName));
    }

    public String getAsString(String fieldName) {
        Object o = get(metaData.getFieldIndex(fieldName));
        return (o != null ? o.toString() : null);
    }

    public String getAsText(String fieldName) {
        return getAsString(fieldName);
    }

    public void setFromText(String fieldName, String value) {
        set(fieldName, value);
    }

    protected boolean isDefaultValue(int fieldIdx) {
        int type = getFieldType(fieldIdx);
        Object o = get(fieldIdx);
        switch(type) {
            case IDataAccess.DATA_STRING:
                return o == null || ((String)o).length() == 0;
            case IDataAccess.DATA_INTEGER:
                return o == null || ((Integer)o).intValue() == IDataAccess.UNDEFINED_INT;
            case IDataAccess.DATA_LONGINT:
                return o == null || ((Long)o).longValue() == IDataAccess.UNDEFINED_LONG;
            case IDataAccess.DATA_DECIMAL:
                return o == null || !((DataTypeDecimal)o).isSet();
            case IDataAccess.DATA_DISTANCE:
                return o == null || !((DataTypeDistance)o).isSet();
            case IDataAccess.DATA_BOOLEAN:
                return o == null || ((Boolean)o).booleanValue() == false;
            case IDataAccess.DATA_DATE:
                return o == null || !((DataTypeDate)o).isSet();
            case IDataAccess.DATA_TIME:
                return o == null || !((DataTypeTime)o).isSet();
            case IDataAccess.DATA_UUID:
                return o == null;
            case IDataAccess.DATA_INTSTRING:
                return o == null || !((DataTypeIntString)o).isSet();
            case IDataAccess.DATA_PASSWORDH:
                return o == null || !((DataTypePasswordHashed)o).isSet();
            case IDataAccess.DATA_PASSWORDC:
                return o == null || !((DataTypePasswordCrypted)o).isSet();
            case IDataAccess.DATA_VIRTUAL:
                return false;
        }
        return false;
    }

    protected boolean isDefaultValue(String fieldName) {
        return isDefaultValue(metaData.getFieldIndex(fieldName));
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[");
        for (int i=0; i<getFieldCount(); i++) {
            Object v = get(i);
            if (v == null && !isKeyField(i)) {
                continue;
            }
            if (getFieldType(i) == IDataAccess.DATA_VIRTUAL) {
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

    public String encodeAsString() {
        StringBuilder s = new StringBuilder();
        s.append("<" + ENCODING_RECORD + ">");
        for (int i=0; i<getFieldCount(); i++) {
            Object o = get(i);
            if (o != null && getFieldType(i) != IDataAccess.DATA_VIRTUAL && !isDefaultValue(i)) {
                s.append("<" + getFieldName(i) + ">" + EfaUtil.escapeXml(o.toString()) + "</" +  getFieldName(i) + ">");
            }
        }
        s.append("</" + ENCODING_RECORD + ">");
        return s.toString();
    }

    public String getQualifiedName() {
        return toString();
    }

    public String[] getQualifiedNameFields() {
        return null; // not supported - no qualified name; to be overridden in subclass if necessary
    }

    public String[] getQualifiedNameFieldsTranslateVirtualToReal() {
        return getQualifiedNameFields(); // not supported - no qualified name; to be overridden in subclass if necessary
    }

    public String[] getQualifiedNameValues(String qname) {
        return new String[] { qname } ; // default is qname itself; to be overridden in subclass if necessary
    }

    public Object getUniqueIdForRecord() {
        return null; // not supported - no qualified name; to be overridden in subclass if necessary
    }

    public void setLastModified() {
        synchronized (this.data) {
            this.data[getFieldCount() - 1] = (Long)System.currentTimeMillis(); // LastModified timestamp
        }
    }

    public long getLastModified() {
        return getLong(LASTMODIFIED);
    }

    protected void updateChangeCount() {
        long l = getChangeCount();
        setLong(CHANGECOUNT, l+1);
    }

    protected long getChangeCount() {
        long l = getLong(CHANGECOUNT);
        if (l < 0) {
            return 0;
        }
        return l;
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
    
    public long getValidFrom() {
        long t = (metaData.versionized ? getLong(VALIDFROM) : 0);
        if (t == IDataAccess.UNDEFINED_LONG || t < 0) {
            return 0;
        }
        return t;
    }

    public long getInvalidFrom() {
        long t = (metaData.versionized ? getLong(INVALIDFROM) : Long.MAX_VALUE);
        if (t == IDataAccess.UNDEFINED_LONG || t < 0) {
            return Long.MAX_VALUE;
        }
        return t;
    }

    public String getValidFromTimeString() {
        long t = getValidFrom();
        String s = (t == 0 ? "" : EfaUtil.getTimeStamp(t));
        return s;
    }

    public String getValidUntilTimeString() {
        long t = getInvalidFrom();
        String s = (t == 0 || t == Long.MAX_VALUE ? "" : EfaUtil.getTimeStamp(t-1));
        return s;
    }

    public String getValidRangeString() {
        String from = getValidFromTimeString();
        String to =   getValidUntilTimeString();
        if (from.length() > 0 && to.length() > 0) {
            return from + " - " + to;
        }
        if (from.length() == 0 && to.length() == 0) {
            return International.getString("unbegrenzt gültig");
        }
        if (from.length() == 0) {
            return International.getMessage("bis {timestamp}", to);
        }
        if (to.length() == 0) {
            return International.getMessage("ab {timestamp}", from);
        }
        return "";
    }

    public boolean isInValidityRange(long validStart, long validEnd) {
        long rValidFrom = getValidFrom();
        long rValidUntil = getInvalidFrom() - 1;
        if ( (rValidFrom >= validStart && rValidFrom <= validEnd) ||   // rValidFrom is in specified range
             (rValidUntil >= validStart && rValidUntil <= validEnd) || // rValidUntil is in specified range
             (rValidFrom < validStart && rValidUntil > validEnd) ) {   // rValidFrom is before specified range and rValidUntil is after specified range
            return true;
        }
        return false;
    }
    
    public boolean isValidAt(long validAt) {
        return (validAt >= getValidFrom() && validAt < getInvalidFrom());
    }

    public void setDeleted(boolean deleted) {
        setBool(DELETED, deleted);
    }

    public boolean getDeleted() {
        return (metaData.versionized ? getBool(DELETED) : false);
    }

    public void setInvisible(boolean invisible) {
        setBool(INVISIBLE, invisible);
    }

    public boolean getInvisible() {
        return (metaData.versionized ? getBool(INVISIBLE) : false);
    }

    // =========================================================================
    // DataType specific get and set functions
    // =========================================================================

    protected void setString(String fieldName, String s) {
        if (s != null && s.length() > 0) {
            set(fieldName, s);
        } else {
            set(fieldName, null);
        }
    }

    protected void setDate(String fieldName, DataTypeDate date) {
        if (date != null && date.isSet()) {
            set(fieldName, new DataTypeDate(date));
        } else {
            set(fieldName, null);
        }
    }

    protected void setTime(String fieldName, DataTypeTime time) {
        if (time != null && time.isSet()) {
            set(fieldName, new DataTypeTime(time));
        } else {
            set(fieldName, null);
        }
    }

    protected void setDecimal(String fieldName, DataTypeDecimal decimal) {
        if (decimal != null && decimal.isSet()) {
            set(fieldName, new DataTypeDecimal(decimal));
        } else {
            set(fieldName, null);
        }
}

    protected void setDistance(String fieldName, DataTypeDistance distance) {
        if (distance != null && distance.isSet()) {
            set(fieldName, new DataTypeDistance(distance));
        } else {
            set(fieldName, null);
        }
    }

    protected void setInt(String fieldName, int i) {
        if (i != IDataAccess.UNDEFINED_INT) {
            set(fieldName, new Integer(i));
        } else {
            set(fieldName, null);
        }
    }

    protected void setLong(String fieldName, long l) {
        if (l != IDataAccess.UNDEFINED_LONG) {
            set(fieldName, new Long(l));
        } else {
            set(fieldName, null);
        }
    }

    protected void setBool(String fieldName, boolean b) {
        set(fieldName, new Boolean(b));
    }

    protected void setUUID(String fieldName, UUID uuid) {
        set(fieldName, uuid);
    }

    protected void setList(String fieldName, DataTypeList list) {
        if (list != null && list.isSet()) {
            set(fieldName, list);
        } else {
            set(fieldName, null);
        }
    }

    protected void setIntString(String fieldName, DataTypeIntString s) {
        if (s != null && s.length() > 0) {
            set(fieldName, s);
        } else {
            set(fieldName, null);
        }
    }

    protected void setPasswordHashed(String fieldName, String pwd) {
        DataTypePasswordHashed p = new DataTypePasswordHashed(pwd);
        if (p != null && p.isSet()) {
            set(fieldName, p);
        } else {
            set(fieldName, null);
        }
    }

    protected void setPasswordCrypted(String fieldName, String pwd) {
        DataTypePasswordCrypted p = new DataTypePasswordCrypted(pwd);
        if (p != null && p.isSet()) {
            set(fieldName, p);
        } else {
            set(fieldName, null);
        }
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

    protected DataTypeDistance getDistance(String fieldName) {
        DataTypeDistance d = (DataTypeDistance)get(fieldName);
        if (d == null) {
            return null;
        }
        return new DataTypeDistance(d);
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
        DataTypeList list = (DataTypeList)get(fieldName);
        if (list == null) {
            return null;
        }
        return new DataTypeList(list);
    }

    protected DataTypeIntString getIntString(String fieldName) {
        DataTypeIntString s = (DataTypeIntString)get(fieldName);
        if (s == null) {
            return null;
        }
        return s;
    }

    protected DataTypePasswordHashed getPasswordHashed(String fieldName) {
        DataTypePasswordHashed pwd = (DataTypePasswordHashed)get(fieldName);
        if (pwd == null) {
            return null;
        }
        return pwd;
    }

    protected DataTypePasswordCrypted getPasswordCrypted(String fieldName) {
        DataTypePasswordCrypted pwd = (DataTypePasswordCrypted)get(fieldName);
        if (pwd == null) {
            return null;
        }
        return pwd;
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
            case IDataAccess.DATA_DISTANCE:
                return DataTypeDistance.parseDistance(s);
            case IDataAccess.DATA_BOOLEAN:
                return Boolean.parseBoolean(s);
            case IDataAccess.DATA_DATE:
                return DataTypeDate.parseDate(s);
            case IDataAccess.DATA_TIME:
                return DataTypeTime.parseTime(s);
            case IDataAccess.DATA_UUID:
                return UUID.fromString(s);
            case IDataAccess.DATA_INTSTRING:
                return DataTypeIntString.parseString(s);
            case IDataAccess.DATA_PASSWORDH:
                return DataTypePasswordHashed.parsePassword(s);
            case IDataAccess.DATA_PASSWORDC:
                return DataTypePasswordCrypted.parsePassword(s);
            case IDataAccess.DATA_LIST_STRING:
                return DataTypeList.parseList(s, IDataAccess.DATA_STRING);
            case IDataAccess.DATA_LIST_INTEGER:
                return DataTypeList.parseList(s, IDataAccess.DATA_INTEGER);
            case IDataAccess.DATA_LIST_UUID:
                return DataTypeList.parseList(s, IDataAccess.DATA_UUID);
            case IDataAccess.DATA_VIRTUAL:
                return "";
        }
        return null;
    }

    public StorageObject getPersistence() {
        return persistence;
    }

    protected ItemTypeStringAutoComplete getGuiItemTypeStringAutoComplete(String name, UUID value, int type, String category,
            StorageObject persistence, long validFrom, long validUntil,
            String description) {
        AutoCompleteList list = new AutoCompleteList();
        list.setDataAccess(persistence.data(), validFrom, validUntil);
        String svalue = (value != null ? list.getValueForId(value.toString()) : "");
        ItemTypeStringAutoComplete item = new ItemTypeStringAutoComplete(name, svalue, type, category, description, true);
        item.setFieldSize(200, 19);
        item.setAutoCompleteData(list);
        item.setChecks(true, true);
        return item;
    }

    public abstract Vector<IItemType> getGuiItems();
    public abstract TableItemHeader[] getGuiTableHeader();
    public abstract TableItem[] getGuiTableItems();

    public IItemType[] getGuiItemsAsArray() {
        Vector<IItemType> items = getGuiItems();
        return items.toArray(new IItemType[0]);
    }

    public void saveGuiItems(Vector<IItemType> items) {
        for(IItemType item : items) {
            String name = item.getName();
            if (!metaData.isField(name)) {
                continue; // skip this field - must be handled by subclass!
            }
            String value = item.getValueFromField();
            if (value != null && value.length() > 0) {
                if (item instanceof ItemTypeStringAutoComplete) {
                    ItemTypeStringAutoComplete acItem = (ItemTypeStringAutoComplete) item;
                    if (acItem.getAlwaysReturnPlainText()) {
                        set(item.getName(), value);
                    } else {
                        Object id = acItem.getId(value);
                        String alternateField = acItem.getAlternateFieldNameForPlainText();
                        if (id != null) {
                            set(item.getName(), id.toString());
                            if (alternateField != null) {
                                set(alternateField, null);
                            }
                        } else {
                            if (alternateField != null) {
                                set(alternateField, value);
                            } else {
                                set(item.getName(), value);
                            }
                            set(item.getName(), null);
                        }
                    }
                } else {
                    set(item.getName(), value);
                }
            } else {
                set(item.getName(), null);
            }
        }
    }

}
