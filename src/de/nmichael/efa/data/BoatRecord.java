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
import de.nmichael.efa.Daten;
import java.util.*;

// @i18n complete

public class BoatRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String OWNER               = "Owner";
    // TYPES stored in BoatTypes
    // RESERVATIONS stored in BoatReservations
    // DAMAGES stored in BoatDamages
    public static final String ALLOWEDGROUPIDLIST    = "AllowedGroupIdList";
    public static final String MAXNOTINGROUP         = "MaxNotInGroup";
    public static final String REQUIREDGROUPID       = "RequiredGroupId";
    public static final String ONLYWITHBOATCAPTAIN   = "OnlyWithBoatCaptain";
    public static final String MANUFACTURER          = "Manufacturer";
    public static final String MODEL                 = "Model";
    public static final String MANUFACTIONDATE       = "ManufactionDate";
    public static final String PURCHASEDATE          = "PurchaseDate";
    public static final String PURCHASEPRICE         = "PurchasePrice";
    public static final String PURCHASEPRICECURRENCY = "PurchasePriceCurrency";
    public static final String DEFAULTCREWID         = "DefaultCrewId";
    public static final String DEFAULTSESSIONTYPE    = "DefaultSessionType";
    public static final String DEFAULTDESTINATIONID  = "DefaultDestinationId";
    public static final String FREEUSE1              = "FreeUse1";
    public static final String FREEUSE2              = "FreeUse2";
    public static final String FREEUSE3              = "FreeUse3";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(OWNER);                             t.add(IDataAccess.DATA_STRING);
        f.add(ALLOWEDGROUPIDLIST);                t.add(IDataAccess.DATA_LIST);
        f.add(MAXNOTINGROUP);                     t.add(IDataAccess.DATA_INTEGER);
        f.add(REQUIREDGROUPID);                   t.add(IDataAccess.DATA_UUID);
        f.add(ONLYWITHBOATCAPTAIN);               t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MANUFACTURER);                      t.add(IDataAccess.DATA_STRING);
        f.add(MODEL);                             t.add(IDataAccess.DATA_STRING);
        f.add(MANUFACTIONDATE);                   t.add(IDataAccess.DATA_DATE);
        f.add(PURCHASEDATE);                      t.add(IDataAccess.DATA_DATE);
        f.add(PURCHASEPRICE);                     t.add(IDataAccess.DATA_DECIMAL);
        f.add(PURCHASEPRICECURRENCY);             t.add(IDataAccess.DATA_STRING);
        f.add(DEFAULTCREWID);                     t.add(IDataAccess.DATA_UUID);
        f.add(DEFAULTSESSIONTYPE);                t.add(IDataAccess.DATA_STRING);
        f.add(DEFAULTDESTINATIONID);              t.add(IDataAccess.DATA_UUID);
        f.add(FREEUSE1);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE2);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE3);                          t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Boats.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(new String[] { NAME, OWNER });
    }

    public BoatRecord(Boats boats, MetaData metaData) {
        super(boats, metaData);
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

    public void setOwner(String owner) {
        setString(OWNER, owner);
    }
    public String getOwner() {
        return getString(OWNER);
    }

    public void setAllowedGroupIdList(DataTypeList<UUID> list) {
        setList(ALLOWEDGROUPIDLIST, list);
    }
    public DataTypeList<UUID> getAllowedGroupIdListp() {
        return getList(ALLOWEDGROUPIDLIST, IDataAccess.DATA_UUID);
    }

    public void setMaxNotInGroup(int maxNotInGroup) {
        setInt(MAXNOTINGROUP, maxNotInGroup);
    }
    public int getMaxNotInGroup() {
        return getInt(MAXNOTINGROUP);
    }

    public void setRequiredGroupId(UUID id) {
        setUUID(REQUIREDGROUPID, id);
    }
    public long getRequiredGroupId() {
        return getLong(REQUIREDGROUPID);
    }

    public void setOnlyWithBoatCaptain(boolean onlyWithBoatCaptain) {
        setBool(ONLYWITHBOATCAPTAIN, onlyWithBoatCaptain);
    }
    public boolean getOnlyWithBoatCaptain() {
        return getBool(ONLYWITHBOATCAPTAIN);
    }

    public void setManufacturer(String manufacturer) {
        setString(MANUFACTURER, manufacturer);
    }
    public String getManufacturer() {
        return getString(MANUFACTURER);
    }

    public void setModel(String model) {
        setString(MODEL, model);
    }
    public String getModel() {
        return getString(MODEL);
    }

    public void setManufactionDate(DataTypeDate date) {
        setDate(MANUFACTIONDATE, date);
    }
    public DataTypeDate getManufactionDate() {
        return getDate(MANUFACTIONDATE);
    }

    public void setPurchaseDate(DataTypeDate date) {
        setDate(PURCHASEDATE, date);
    }
    public DataTypeDate getPurchaseDate() {
        return getDate(PURCHASEDATE);
    }

    public void setPurchasePrice(DataTypeDecimal price) {
        setDecimal(PURCHASEPRICE, price);
    }
    public DataTypeDecimal getPurchasePrice() {
        return getDecimal(PURCHASEPRICE);
    }


    public void setPurchasePriceCurrency(String currency) {
        setString(PURCHASEPRICECURRENCY, currency);
    }
    public String getPurchasePriceCurrency() {
        return getString(PURCHASEPRICECURRENCY);
    }

    public void setDefaultCrewId(UUID id) {
        setUUID(DEFAULTCREWID, id);
    }
    public UUID getDefaultCrewId() {
        return getUUID(DEFAULTCREWID);
    }

    public void setDefaultSessionType(String type) {
        setString(DEFAULTSESSIONTYPE, type);
    }
    public String getDefaultSessionType() {
        return getString(DEFAULTSESSIONTYPE);
    }

    public void setDefaultDestinationId(UUID id) {
        setUUID(DEFAULTDESTINATIONID, id);
    }
    public UUID getDefaultDestinationId() {
        return getUUID(DEFAULTDESTINATIONID);
    }

    public void setFreeUse1(String s) {
        setString(FREEUSE1, s);
    }
    public String getFreeUse1() {
        return getString(FREEUSE1);
    }

    public void setFreeUse2(String s) {
        setString(FREEUSE2, s);
    }
    public String getFreeUse2() {
        return getString(FREEUSE2);
    }

    public void setFreeUse3(String s) {
        setString(FREEUSE3, s);
    }
    public String getFreeUse3() {
        return getString(FREEUSE3);
    }

    public BoatTypeRecord[] getAllBoatTypes(boolean anyValidity) {
        try {
            BoatTypes boatTypes = getPersistence().getProject().getBoatTypes(false);
            DataKey[] keys = boatTypes.data().getByFields(new String[] { BoatTypeRecord.BOATID }, new UUID[] { getId() });
            ArrayList<BoatTypeRecord> list = new ArrayList<BoatTypeRecord>();
            for (DataKey key : keys) {
                BoatTypeRecord r = (BoatTypeRecord)boatTypes.data().get(key);
                if (r != null && (anyValidity || (getValidFrom() == r.getValidFrom() && getInvalidFrom() == r.getInvalidFrom()))) {
                    list.add(r);
                }
            }
            return list.toArray(new BoatTypeRecord[0]);
        } catch(Exception e) {
        }
        return null;
    }

}
