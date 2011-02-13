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

public class BoatStatusRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String BOATID              = "BoatId";
    public static final String STATUS              = "Status";
    public static final String LOGBOOK             = "Logbook"; // the name of the logbook EntryNo is pointing to
    public static final String ENTRYNO             = "EntryNo";
    public static final String COMMENT             = "Comment";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                   t.add(IDataAccess.DATA_UUID);
        f.add(STATUS);                   t.add(IDataAccess.DATA_STRING);
        f.add(LOGBOOK);                  t.add(IDataAccess.DATA_STRING);
        f.add(ENTRYNO);                  t.add(IDataAccess.DATA_STRING);
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

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public void setStatus(String status) {
        setString(STATUS, status);
    }
    public String getStatus() {
        return getString(STATUS);
    }

    public void setLogbook(String logbook) {
        setString(LOGBOOK, logbook);
    }
    public String getLogbook() {
        return getString(LOGBOOK);
    }

    public void setEntryNo(String entryNo) {
        setString(ENTRYNO, entryNo);
    }
    public String getEntryNo() {
        return getString(ENTRYNO);
    }

    public void setComment(String comment) {
        setString(COMMENT, comment);
    }
    public String getComment() {
        return getString(COMMENT);
    }

}
