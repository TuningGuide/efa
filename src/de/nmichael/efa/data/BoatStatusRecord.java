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

public class BoatStatusRecord extends DataRecord {

    // Status Keys (identical to old efa 1.x BootStatus (if changed, make sure to adapt import!)
    public static final String STATUS_HIDE  = "HIDE";
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_ONTHEWATER = "ONTHEWATER";
    public static final String STATUS_NOTAVAILABLE = "NOTAVAILABLE";
    public static final String STATUS_CURRENTLYHIDDEN = "CURRENTLYHIDDEN";

    public static final int ARRAY_STRINGLIST_VALUES  = 1;
    public static final int ARRAY_STRINGLIST_DISPLAY = 2;

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String BOATID              = "BoatId";
    public static final String STATUS              = "Status";
    public static final String LOGBOOK             = "Logbook"; // the name of the logbook EntryNo is pointing to
    public static final String ENTRYNO             = "EntryNo";
    public static final String COMMENT             = "Comment";

    protected static String CAT_STATUS       = "%06%" + International.getString("Bootsstatus");
    
    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                   t.add(IDataAccess.DATA_UUID);
        f.add(STATUS);                   t.add(IDataAccess.DATA_STRING);
        f.add(LOGBOOK);                  t.add(IDataAccess.DATA_STRING);
        f.add(ENTRYNO);                  t.add(IDataAccess.DATA_INTSTRING);
        f.add(COMMENT);                  t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(BoatStatus.DATATYPE, f, t, false);
        metaData.setKey(new String[] { BOATID });
        metaData.addIndex(new String[] { STATUS });
    }

    public BoatStatusRecord(BoatStatus boatStatus, MetaData metaData) {
        super(boatStatus, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getBoatId(),null,null);
    }

    public static DataKey getKey(UUID id) {
        return new DataKey<UUID,String,String>(id,null,null);
    }

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public String getBoatNameAsString(long validAt) {
        Boats b = getPersistence().getProject().getBoats(false);
        if (b != null) {
            BoatRecord r = b.getBoat(getBoatId(), validAt);
            if (r != null) {
                return r.getQualifiedName();
            }
        }
        return null;
    }

    public void setStatus(String status) {
        setString(STATUS, status);
    }
    public String getStatus() {
        return getString(STATUS);
    }
    public String getStatusDescription() {
        return getStatusDescription(getStatus());
    }

    public void setLogbook(String logbook) {
        setString(LOGBOOK, logbook);
    }
    public String getLogbook() {
        return getString(LOGBOOK);
    }

    public void setEntryNo(DataTypeIntString entryNo) {
        setIntString(ENTRYNO, entryNo);
    }
    public DataTypeIntString getEntryNo() {
        return getIntString(ENTRYNO);
    }

    public void setComment(String comment) {
        setString(COMMENT, comment);
    }
    public String getComment() {
        return getString(COMMENT);
    }

    private String getBoatName() {
        Boats boats = getPersistence().getProject().getBoats(false);
        String boatName = "?";
        if (boats != null) {
            BoatRecord r = boats.getBoat(getBoatId(), System.currentTimeMillis());
            if (r != null) {
                boatName = r.getQualifiedName();
            }
        }
        return boatName;
    }

    public Vector<IItemType> getGuiItems() {
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        v.add(item = new ItemTypeStringList(BoatStatusRecord.STATUS, getStatus(),
                makeStatusTypeArray(ARRAY_STRINGLIST_VALUES), makeStatusTypeArray(ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_STATUS,
                International.getString("Status")));
        if (getStatus() != null && getStatus().equals(STATUS_ONTHEWATER)) {
            v.add(item = new ItemTypeLabel(BoatStatusRecord.ENTRYNO,
                    IItemType.TYPE_PUBLIC, CAT_STATUS,
                    International.getMessage("Eintrag in Lfd. Nr. {entryNo} in Fahrtenbuch {logbook}", getEntryNo().toString(), getLogbook())));
        }
        v.add(item = new ItemTypeString(BoatStatusRecord.COMMENT, getComment(),
                IItemType.TYPE_PUBLIC, CAT_STATUS, International.getString("Bemerkung")));
        
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[3];
        header[0] = new TableItemHeader(International.getString("Boot"));
        header[1] = new TableItemHeader(International.getString("Status"));
        header[2] = new TableItemHeader(International.getString("Bemerkung"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[3];
        items[0] = new TableItem(getBoatName());
        items[1] = new TableItem(getStatusDescription(getStatus()));
        items[2] = new TableItem(getComment());
        return items;
    }

    public static String getStatusDescription(String stype) {
        if (stype == null) {
            return null;
        }
        if (stype.equals(STATUS_HIDE)) {
            return International.getString("nicht anzeigen");
        }
        if (stype.equals(STATUS_AVAILABLE)) {
            return International.getString("verfügbar");
        }
        if (stype.equals(STATUS_ONTHEWATER)) {
            return International.getString("unterwegs");
        }
        if (stype.equals(STATUS_NOTAVAILABLE)) {
            return International.getString("nicht verfügbar");
        }
        if (stype.equals(STATUS_CURRENTLYHIDDEN)) {
            return International.getString("vorübergehend verstecken");
        }
        return null;
    }

    public static String[] makeStatusTypeArray(int type) {
        String[] status = new String[5];
        for(int i=0; i<status.length; i++) {
            String stype = null;
            switch(i) {
                case 0:
                    stype = STATUS_HIDE;
                    break;
                case 1:
                    stype = STATUS_AVAILABLE;
                    break;
                case 2:
                    stype = STATUS_ONTHEWATER;
                    break;
                case 3:
                    stype = STATUS_NOTAVAILABLE;
                    break;
                case 4:
                    stype = STATUS_CURRENTLYHIDDEN;
                    break;
            }
            status[i] = (type == ARRAY_STRINGLIST_VALUES ?
                stype :
                getStatusDescription(stype));
        }
        return status;
    }

}
