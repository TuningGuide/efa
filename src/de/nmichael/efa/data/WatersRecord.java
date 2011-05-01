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

public class WatersRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";

    public static final String[] IDX_NAME = new String[] { NAME };

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Waters.DATATYPE, f, t, false);
        metaData.setKey(new String[] { ID });
        metaData.addIndex(IDX_NAME);
    }

    public WatersRecord(Waters waters, MetaData metaData) {
        super(waters, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getId(),null,null);
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
