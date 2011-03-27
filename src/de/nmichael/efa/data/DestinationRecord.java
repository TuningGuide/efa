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
import de.nmichael.efa.core.config.EfaTypes;
import java.util.*;

// @i18n complete

public class DestinationRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String START               = "Start";
    public static final String END                 = "End";
    public static final String STARTISBOATHOUSE    = "StartIsBoathouse";
    public static final String ROUNDTRIP           = "Roundtrip";
    public static final String DESTINATIONAREA     = "DestinationArea";
    public static final String PASSEDLOCKS         = "PassedLocks";
    public static final String DISTANCE            = "Distance";
    public static final String WATERSIDLIST        = "WatersIdList";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(START);                             t.add(IDataAccess.DATA_STRING);
        f.add(END);                               t.add(IDataAccess.DATA_STRING);
        f.add(STARTISBOATHOUSE);                  t.add(IDataAccess.DATA_BOOLEAN);
        f.add(ROUNDTRIP);                         t.add(IDataAccess.DATA_BOOLEAN);
        f.add(DESTINATIONAREA);                   t.add(IDataAccess.DATA_INTEGER);
        f.add(PASSEDLOCKS);                       t.add(IDataAccess.DATA_INTEGER);
        f.add(DISTANCE);                          t.add(IDataAccess.DATA_DISTANCE);
        f.add(WATERSIDLIST);                      t.add(IDataAccess.DATA_LIST);
        MetaData metaData = constructMetaData(Destinations.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(new String[] { NAME });
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

    public void setDestinationArea(int destinationArea) {
        setInt(DESTINATIONAREA, destinationArea);
    }
    public int getDestinationArea() {
        return getInt(DESTINATIONAREA);
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

    public String getQualifiedName() {
        String name = getName();
        return (name != null ? name : "");
    }

}
