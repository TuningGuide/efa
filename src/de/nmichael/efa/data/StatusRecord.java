/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class StatusRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String TYPE_GUEST          = "GUEST";
    public static final String TYPE_OTHER          = "OTHER";
    public static final String TYPE_USER           = "USER";

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String TYPE                = "Type";

    public static final String[] IDX_NAME = new String[] { NAME };

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(TYPE);                              t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Status.DATATYPE, f, t, false);
        metaData.setKey(new String[] { ID });
        metaData.addIndex(IDX_NAME);
    }

    public StatusRecord(Status status, MetaData metaData) {
        super(status, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getId(),null,null);
    }

    public static DataKey getKey(UUID id) {
        return new DataKey<UUID,String,String>(id,null,null);
    }

    public int compareTo(Object o)  {
        if (o == null) {
            return -1;
        }
        StatusRecord or = (StatusRecord)o;
        if (getType().equals(or.getType())) {
            return getName().compareTo(or.getName());
        } else {
            if (getType().equals(TYPE_OTHER)) { // OTHER type is always last
                return 1;
            }
            if (getType().equals(TYPE_GUEST)) { // GUEST type is always between USER and OTHER types
                if (or.getType().equals(TYPE_OTHER)) {
                    return -1;
                }
                if (or.getType().equals(TYPE_USER)) {
                    return 1;
                }
            }
            if (getType().equals(TYPE_USER)) { // USER types are always first
                return -1;
            }
        }
        return 0;
    }

    public void setId(UUID id) {
        setUUID(ID, id);
    }
    public UUID getId() {
        return getUUID(ID);
    }

    public void setName(String name) {
        setString(NAME, name);
    }
    public String getName() {
        return getString(NAME);
    }

    public void setType(String type) {
        if (type.equals(TYPE_GUEST) ||
            type.equals(TYPE_OTHER) ||
            type.equals(TYPE_USER))
        setString(TYPE, type);
    }
    public String getType() {
        String type = getString(TYPE);
        if (type == null ||
                (!type.equals(TYPE_GUEST) && !type.equals(TYPE_OTHER))) {
            return TYPE_USER;
        }
        return type;
    }

    public String[] getQualifiedNameFields() {
        return IDX_NAME;
    }

    public Object getUniqueIdForRecord() {
        return getId();
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Reservierung");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        // @todo
        //v.add(item = new ItemTypeString(BoatRecord.NAME, getName(),
        //        IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Name")));
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[4];
        // @todo
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[4];
        // @todo
        return items;
    }

}
