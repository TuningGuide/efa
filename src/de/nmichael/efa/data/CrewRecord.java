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

public class CrewRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID               = "Id";
    public static final String NAME             = "Name";
    public static final String COXID            = "CoxId";
    public static final String CREW1ID          = "Crew1Id";
    public static final String CREW2ID          = "Crew2Id";
    public static final String CREW3ID          = "Crew3Id";
    public static final String CREW4ID          = "Crew4Id";
    public static final String CREW5ID          = "Crew5Id";
    public static final String CREW6ID          = "Crew6Id";
    public static final String CREW7ID          = "Crew7Id";
    public static final String CREW8ID          = "Crew8Id";
    public static final String CREW9ID          = "Crew9Id";
    public static final String CREW10ID         = "Crew10Id";
    public static final String CREW11ID         = "Crew11Id";
    public static final String CREW12ID         = "Crew12Id";
    public static final String CREW13ID         = "Crew13Id";
    public static final String CREW14ID         = "Crew14Id";
    public static final String CREW15ID         = "Crew15Id";
    public static final String CREW16ID         = "Crew16Id";
    public static final String CREW17ID         = "Crew17Id";
    public static final String CREW18ID         = "Crew18Id";
    public static final String CREW19ID         = "Crew19Id";
    public static final String CREW20ID         = "Crew20Id";
    public static final String CREW21ID         = "Crew21Id";
    public static final String CREW22ID         = "Crew22Id";
    public static final String CREW23ID         = "Crew23Id";
    public static final String CREW24ID         = "Crew24Id";
    public static final String BOATCAPTAIN      = "BoatCaptain";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                  t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                t.add(IDataAccess.DATA_STRING);
        f.add(COXID);               t.add(IDataAccess.DATA_UUID);
        f.add(CREW1ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW2ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW3ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW4ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW5ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW6ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW7ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW8ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW9ID);             t.add(IDataAccess.DATA_UUID);
        f.add(CREW10ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW11ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW12ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW13ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW14ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW15ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW16ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW17ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW18ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW19ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW20ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW21ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW22ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW23ID);            t.add(IDataAccess.DATA_UUID);
        f.add(CREW24ID);            t.add(IDataAccess.DATA_UUID);
        f.add(BOATCAPTAIN);         t.add(IDataAccess.DATA_INTEGER);
        MetaData metaData = constructMetaData(Crews.DATATYPE, f, t, false);
        metaData.setKey(new String[] { ID });
        metaData.addIndex(new String[] { NAME });
    }

    public CrewRecord(Crews crews, MetaData metaData) {
        super(crews, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,String,String>(getId(),null,null);
    }

    private static String getCrewFieldNameId(int pos) {
        return "Crew"+pos+"Id";
    }

    private static String getCrewFieldNameName(int pos) {
        return "Crew"+pos+"Name";
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

    public void setCoxId(UUID id) {
        setUUID(COXID, id);
    }
    public UUID getCoxId() {
        return getUUID(COXID);
    }

    public void setCrewId(int pos, UUID id) {
        setUUID(getCrewFieldNameId(pos), id);
    }
    public UUID getCrewId(int pos) {
        return getUUID(getCrewFieldNameId(pos));
    }

    public void setBoatCaptainPosition(int pos) {
        setInt(BOATCAPTAIN, pos);
    }
    public int getBoatCaptainPosition() {
        return getInt(BOATCAPTAIN);
    }

}