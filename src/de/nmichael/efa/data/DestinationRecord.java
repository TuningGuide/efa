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
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import java.util.*;

// @i18n complete

public class DestinationRecord extends DataRecord implements IItemFactory {

    public static final String DESTINATION_VARIANT_SEPARATOR = "+";

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String START               = "Start";
    public static final String END                 = "End";
    public static final String STARTISBOATHOUSE    = "StartIsBoathouse";
    public static final String ROUNDTRIP           = "Roundtrip";
    public static final String DESTINATIONAREAS    = "DestinationAreas";
    public static final String PASSEDLOCKS         = "PassedLocks";
    public static final String DISTANCE            = "Distance";
    public static final String WATERSIDLIST        = "WatersIdList";

    public static final String[] IDX_NAME = new String[] { NAME };

    private static String GUIITEM_DESTINATIONAREAS = "GUIITEM_DESTINATIONAREAS";
    private static String GUIITEM_WATERSIDLIST = "GUIITEM_WATERSIDLIST";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(START);                             t.add(IDataAccess.DATA_STRING);
        f.add(END);                               t.add(IDataAccess.DATA_STRING);
        f.add(STARTISBOATHOUSE);                  t.add(IDataAccess.DATA_BOOLEAN);
        f.add(ROUNDTRIP);                         t.add(IDataAccess.DATA_BOOLEAN);
        f.add(DESTINATIONAREAS);                  t.add(IDataAccess.DATA_STRING);
        f.add(PASSEDLOCKS);                       t.add(IDataAccess.DATA_INTEGER);
        f.add(DISTANCE);                          t.add(IDataAccess.DATA_DISTANCE);
        f.add(WATERSIDLIST);                      t.add(IDataAccess.DATA_LIST_UUID);
        MetaData metaData = constructMetaData(Destinations.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(IDX_NAME);
    }

    public DestinationRecord(Destinations destinations, MetaData metaData) {
        super(destinations, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Long,String>(getId(),getValidFrom(),null);
    }

    public static DataKey getKey(UUID id, long validFrom) {
        return new DataKey<UUID,Long,String>(id,validFrom,null);
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

    public void setStart(String name) {
        setString(START, name);
    }
    public String getStart() {
        return getString(START);
    }

    public void setEnd(String name) {
        setString(END, name);
    }
    public String getEnd() {
        return getString(END);
    }

    public void setStartIsBoathouse(boolean startIsBoathouse) {
        setBool(STARTISBOATHOUSE, startIsBoathouse);
    }
    public boolean getStartIsBoathouse() {
        return getBool(STARTISBOATHOUSE);
    }

    public void setRoundtrip(boolean roundtrip) {
        setBool(ROUNDTRIP, roundtrip);
    }
    public boolean getRoundtrip() {
        return getBool(ROUNDTRIP);
    }

    public void setDestinationAreas(ZielfahrtFolge zf) {
        setString(DESTINATIONAREAS, zf.toString());
    }
    public ZielfahrtFolge getDestinationAreas() {
        String s = getString(DESTINATIONAREAS);
        if (s == null || s.length() == 0) {
            return null;
        }
        ZielfahrtFolge zf = new ZielfahrtFolge(s);
        return zf;
    }

    public void setPassedLocks(int passedLocks) {
        setInt(PASSEDLOCKS, passedLocks);
    }
    public int getPassedLocks() {
        return getInt(PASSEDLOCKS);
    }

    public void setDistance(DataTypeDistance distance) {
        setDistance(DISTANCE, distance);
    }
    public DataTypeDistance getDistance() {
        return getDistance(DISTANCE);
    }

    public void setWatersIdList(DataTypeList<UUID> list) {
        setList(WATERSIDLIST, list);
    }
    public DataTypeList<UUID> getWatersIdList() {
        return getList(WATERSIDLIST, IDataAccess.DATA_UUID);
    }
    public String getWatersNamesStringList() {
        StringBuilder s = new StringBuilder();
        try {
            DataTypeList<UUID> wlist = getWatersIdList();
            if (wlist == null || wlist.length() == 0) {
                return "";
            }
            Waters waters = getPersistence().getProject().getWaters(false);
            for (int i=0; i<wlist.length(); i++) {
                WatersRecord w = waters.getWaters(wlist.get(i));
                if (w != null && w.getName() != null && w.getName().length() > 0) {
                    s.append( (s.length() > 0 ? ", " : "") + w.getName());
                }
            }
            return s.toString();
        } catch(Exception e) {
            Logger.logdebug(e);
            return "";
        }
    }

    public String getQualifiedName() {
        String name = getName();
        return (name != null ? name : "");
    }

    public String[] getQualifiedNameFields() {
        return IDX_NAME;
    }

    public static String[] tryGetNameAndVariant(String s) {
        if (s == null) {
            return null;
        }
        int pos = s.indexOf(DESTINATION_VARIANT_SEPARATOR);
        if (pos < 0) {
            return new String[] { s.trim(), null };
        } else {
            return new String[] { s.substring(0, pos).trim(), s.substring(pos+1).trim() };
        }
    }

    public Object getUniqueIdForRecord() {
        return getId();
    }

    public String getAsText(String fieldName) {
        if (fieldName.equals(WATERSIDLIST)) {
            DataTypeList list = (DataTypeList)get(fieldName);
            if (list == null) {
                return null;
            } else {
                return getWatersNamesStringList();
            }
        }
        return super.getAsText(fieldName);
    }

    public void setFromText(String fieldName, String value) {
        if (fieldName.equals(WATERSIDLIST)) {
            Vector<String> values = EfaUtil.split(value, ',');
            DataTypeList<UUID> list = new DataTypeList<UUID>();
            Waters waters = getPersistence().getProject().getWaters(false);
            for (int i=0; i<values.size(); i++) {
                WatersRecord wr = waters.findWatersByName(values.get(i).trim());
                if (wr != null) {
                    list.add(wr.getId());
                }
            }
            if (list.length() > 0) {
                set(fieldName, list);
            }
            return;
        }
        set(fieldName, value);
    }

    public IItemType[] getDefaultItems(String itemName) {
        if (itemName.equals(GUIITEM_WATERSIDLIST)) {
            IItemType[] items = new IItemType[1];
            String CAT_WATERS   = "%02%" + International.getString("Gewässer");
            items[0] = getGuiItemTypeStringAutoComplete(DestinationRecord.WATERSIDLIST, null,
                    IItemType.TYPE_PUBLIC, CAT_WATERS,
                    getPersistence().getProject().getWaters(false), getValidFrom(), getInvalidFrom()-1,
                    International.getString("Gewässer"));
            items[0].setFieldSize(300, -1);
            return items;
        }
        return null;
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA = "%01%" + International.getString("Basisdaten");
        String CAT_WATERS   = "%02%" + International.getString("Gewässer");

        Waters waters = getPersistence().getProject().getWaters(false);
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        // CAT_BASEDATA
        v.add(item = new ItemTypeString(DestinationRecord.NAME, getName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Name")));
        ((ItemTypeString)item).setNotAllowedCharacters(DESTINATION_VARIANT_SEPARATOR);
        v.add(item = new ItemTypeString(DestinationRecord.START, getStart(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Start")));
        v.add(item = new ItemTypeString(DestinationRecord.END, getEnd(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Ende")));
        v.add(item = new ItemTypeBoolean(DestinationRecord.STARTISBOATHOUSE, getStartIsBoathouse(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Start ist Bootshaus")));
        v.add(item = new ItemTypeBoolean(DestinationRecord.ROUNDTRIP, getRoundtrip(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Start gleich Ziel")));
        if (Daten.efaConfig.getValueUseFunctionalityRowingBerlin()) {
            v.add(item = new ItemTypeString(DestinationRecord.GUIITEM_DESTINATIONAREAS, (getDestinationAreas() == null ? "" : getDestinationAreas().toString()),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.onlyFor("Zielbereiche","de")));
            ((ItemTypeString)item).setAllowedCharacters("0123456789,;");
        }
        v.add(item = new ItemTypeInteger(DestinationRecord.PASSEDLOCKS, getPassedLocks(), 0, Integer.MAX_VALUE, true,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Passierte Schleusen")));
        v.add(item = new ItemTypeDistance(DestinationRecord.DISTANCE, getDistance(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Distanz")));

        // CAT_WATERS
        Vector<IItemType[]>itemList = new Vector<IItemType[]>();
        DataTypeList<UUID> watersList = getWatersIdList();
        for (int i=0; watersList != null && i<watersList.length(); i++) {
            IItemType[] items = getDefaultItems(GUIITEM_WATERSIDLIST);
            ((ItemTypeStringAutoComplete)items[0]).setId(watersList.get(i));
            itemList.add(items);
        }
        v.add(item = new ItemTypeItemList(GUIITEM_WATERSIDLIST, itemList, this,
                IItemType.TYPE_PUBLIC, CAT_WATERS, International.getString("Gewässer")));
        ((ItemTypeItemList)item).setXForAddDelButtons(3);
        ((ItemTypeItemList)item).setPadYbetween(0);
        ((ItemTypeItemList)item).setRepeatTitle(false);
        ((ItemTypeItemList)item).setAppendPositionToEachElement(true);

        return v;
    }

    public void saveGuiItems(Vector<IItemType> items) {
        Waters waters = getPersistence().getProject().getWaters(false);

        for(IItemType item : items) {
            String name = item.getName();
            String cat = item.getCategory();
            if (name.equals(GUIITEM_DESTINATIONAREAS) && item.isChanged()) {
                String s = item.toString().trim();
                if (s.length() == 0) {
                    setDestinationAreas(null);
                } else {
                    setDestinationAreas(new ZielfahrtFolge(s));
                }
            }
            if (name.equals(GUIITEM_WATERSIDLIST) && item.isChanged()) {
                ItemTypeItemList list = (ItemTypeItemList)item;
                Hashtable<String,UUID> uuidList = new Hashtable<String,UUID>();
                for (int i=0; i<list.size(); i++) {
                    IItemType[] typeItems = list.getItems(i);
                    Object uuid = ((ItemTypeStringAutoComplete)typeItems[0]).getId(typeItems[0].toString());
                    if (uuid != null && uuid.toString().length() > 0) {
                        uuidList.put(uuid.toString(), (UUID)uuid);
                    }
                }
                String[] uuidArr = uuidList.keySet().toArray(new String[0]);
                DataTypeList<UUID> watersList = new DataTypeList<UUID>();
                for (String uuid : uuidArr) {
                    watersList.add(uuidList.get(uuid));
                }
                this.setWatersIdList(watersList);
            }
        }
        super.saveGuiItems(items);
    }

    public TableItemHeader[] getGuiTableHeader() {
        int col = (Daten.efaConfig.getValueUseFunctionalityRowingBerlin() ? 4 : 3);
        TableItemHeader[] header = new TableItemHeader[col];
        header[0] = new TableItemHeader(International.getString("Name"));
        header[1] = new TableItemHeader(International.getString("Entfernung"));
        header[2] = new TableItemHeader(International.getString("Gewässer"));
        if (col > 3) {
            header[3] = new TableItemHeader(International.onlyFor("Zielbereiche","de"));
        }
        return header;
    }

    public TableItem[] getGuiTableItems() {
        int col = (Daten.efaConfig.getValueUseFunctionalityRowingBerlin() ? 4 : 3);
        TableItem[] items = new TableItem[col];
        items[0] = new TableItem(getName());
        items[1] = new TableItem(getDistance());
        items[2] = new TableItem(getWatersNamesStringList());
        if (col > 3) {
            items[3] = new TableItem(getDestinationAreas());
        }
        return items;
    }

}
