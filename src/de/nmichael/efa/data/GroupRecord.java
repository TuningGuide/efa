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
import java.util.*;

// @i18n complete

public class GroupRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String MEMBERIDLIST        = "MemberIdList";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(MEMBERIDLIST);                      t.add(IDataAccess.DATA_LIST);
        MetaData metaData = constructMetaData(Groups.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(new String[] { NAME });
    }

    public GroupRecord(Groups groups, MetaData metaData) {
        super(groups, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Long,String>(getId(),getValidFrom(),null);
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

    public void setMemberIdList(DataTypeList<UUID> list) {
        setList(MEMBERIDLIST, list);
    }
    public DataTypeList<UUID> getMemberIdListp() {
        return getList(MEMBERIDLIST, IDataAccess.DATA_UUID);
    }

    

}
