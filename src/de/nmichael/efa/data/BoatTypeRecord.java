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

public class BoatTypeRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String BOATID              = "BoatId";
    public static final String VARIANT             = "Variant";
    public static final String DESCRIPTION         = "Description";
    public static final String TYPE                = "Type";
    public static final String SEATS               = "Seats";
    public static final String RIGGING             = "Rigging";
    public static final String COXING              = "Coxing";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                            t.add(IDataAccess.DATA_UUID);
        f.add(VARIANT);                           t.add(IDataAccess.DATA_INTEGER);
        f.add(DESCRIPTION);                       t.add(IDataAccess.DATA_STRING);
        f.add(TYPE);                              t.add(IDataAccess.DATA_STRING);
        f.add(SEATS);                             t.add(IDataAccess.DATA_STRING);
        f.add(RIGGING);                           t.add(IDataAccess.DATA_STRING);
        f.add(COXING);                            t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(BoatTypes.DATATYPE, f, t, true);
        metaData.setKey(new String[] { BOATID, VARIANT }); // plus VALID_FROM
        metaData.addIndex(new String[] { BOATID });
    }

    public BoatTypeRecord(BoatTypes boatTypes, MetaData metaData) {
        super(boatTypes, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Integer,Long>(getBoatId(),getVariant(),getValidFrom());
    }

    public static DataKey getKey(UUID id, int variant, long validFrom) {
        return new DataKey<UUID,Integer,Long>(id,variant,validFrom);
    }

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public void setVariant(int id) {
        setInt(VARIANT, id);
    }
    public int getVariant() {
        return getInt(VARIANT);
    }

    public void setDescription(String description) {
        setString(DESCRIPTION, description);
    }
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setType(String type) {
        setString(TYPE, type);
    }
    public String getType() {
        return getString(TYPE);
    }

    public void setSeats(String seats) {
        setString(SEATS, seats);
    }
    public String getSeats() {
        return getString(SEATS);
    }

    public void setRigging(String rigging) {
        setString(RIGGING, rigging);
    }
    public String getRigging() {
        return getString(RIGGING);
    }

    public void setCoxing(String coxing) {
        setString(COXING, coxing);
    }
    public String getCoxing() {
        return getString(COXING);
    }

}
