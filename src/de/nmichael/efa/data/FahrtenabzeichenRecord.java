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

public class FahrtenabzeichenRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String PERSONID            = "PersonId";
    public static final String ABZEICHEN           = "Abzeichen";
    public static final String ABZEICHENAB         = "AbzeichenAB";
    public static final String KILOMETER           = "Kilometer";
    public static final String KILOMETERAB         = "KilometerAB";
    public static final String FAHRTENHEFT         = "Fahrtenheft";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(PERSONID);                          t.add(IDataAccess.DATA_UUID);
        f.add(ABZEICHEN);                         t.add(IDataAccess.DATA_INTEGER);
        f.add(ABZEICHENAB);                       t.add(IDataAccess.DATA_INTEGER);
        f.add(KILOMETER);                         t.add(IDataAccess.DATA_INTEGER);
        f.add(KILOMETERAB);                       t.add(IDataAccess.DATA_INTEGER);
        f.add(FAHRTENHEFT);                       t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Fahrtenabzeichen.DATATYPE, f, t, false);
        metaData.setKey(new String[] { PERSONID });
    }

    public FahrtenabzeichenRecord(Fahrtenabzeichen fahrtenabzeichen, MetaData metaData) {
        super(fahrtenabzeichen, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getPersonId(),null,null);
    }

    public void setPersonId(UUID id) {
        setUUID(PERSONID, id);
    }
    public UUID getPersonId() {
        return getUUID(PERSONID);
    }

    public void setAbzeichen(int abzeichen) {
        setInt(ABZEICHEN, abzeichen);
    }
    public int getAbzeichen() {
        return getInt(ABZEICHEN);
    }

    public void setAbzeichenAB(int abzeichen) {
        setInt(ABZEICHENAB, abzeichen);
    }
    public int getAbzeichenAB() {
        return getInt(ABZEICHENAB);
    }

    public void setKilometer(int km) {
        setInt(KILOMETER, km);
    }
    public int getKilometer() {
        return getInt(KILOMETER);
    }

    public void setKilometerAB(int km) {
        setInt(KILOMETERAB, km);
    }
    public int getKilometerAB() {
        return getInt(KILOMETERAB);
    }

    public void setFahrtenheft(String data) {
        setString(FAHRTENHEFT, data);
    }
    public String getFahrtenheft() {
        return getString(FAHRTENHEFT);
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
