/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
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

public class BoatRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    private static final String ID                  = "Id";
    private static final String NAME                = "Name";
    private static final String OWNER               = "Owner";
    // TYPES stored in BoatTypes
    // RESERVATIONS stored in BoatReservations
    // DAMAGES stored in BoatDamages
    private static final String DEFAULTCREWID         = "DefaultCrewId";
    private static final String ALLOWEDGROUPIDLIST    = "AllowedGroupIdList";
    private static final String MAXNOTINGROUP         = "MaxNotInGroup";
    private static final String REQUIREDGROUPID       = "RequiredGroupId";
    private static final String ONLYWITHBOATCAPTAIN   = "OnlyWithBoatCaptain";
    private static final String MANUFACTURER          = "Manufacturer";
    private static final String MODEL                 = "Model";
    private static final String MANUFACTIONDATE       = "ManufactionDate";
    private static final String PURCHASEDATE          = "PurchaseDate";
    private static final String PURCHASEPRICE         = "PurchasePrice";
    private static final String PURCHASEPRICECURRENCY = "PurchasePriceCurrency";
    private static final String FREEUSE1              = "FreeUse1";
    private static final String FREEUSE2              = "FreeUse2";
    private static final String FREEUSE3              = "FreeUse3";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_LONGINT);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(OWNER);                             t.add(IDataAccess.DATA_STRING);
        f.add(DEFAULTCREWID);                     t.add(IDataAccess.DATA_LONGINT);
        f.add(ALLOWEDGROUPIDLIST);                t.add(IDataAccess.DATA_STRING);
        f.add(MAXNOTINGROUP);                     t.add(IDataAccess.DATA_INTEGER);
        f.add(REQUIREDGROUPID);                   t.add(IDataAccess.DATA_LONGINT);
        f.add(ONLYWITHBOATCAPTAIN);               t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MANUFACTURER);                      t.add(IDataAccess.DATA_STRING);
        f.add(MODEL);                             t.add(IDataAccess.DATA_STRING);
        f.add(MANUFACTIONDATE);                   t.add(IDataAccess.DATA_DATE);
        f.add(PURCHASEDATE);                      t.add(IDataAccess.DATA_DATE);
        f.add(PURCHASEPRICE);                     t.add(IDataAccess.DATA_DECIMAL);
        f.add(PURCHASEPRICECURRENCY);             t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE1);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE2);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE3);                          t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Boats.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID });
    }

    public BoatRecord(MetaData metaData) {
        super(metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return BoatRecord.createBoatRecord();
    }

    public static BoatRecord createBoatRecord() {
        return new BoatRecord(MetaData.getMetaData(Boats.DATATYPE));
    }

    public static BoatRecord createBoatRecord(long id) {
        BoatRecord rec = new BoatRecord(MetaData.getMetaData(Boats.DATATYPE));
        rec.setLong(ID, id);
        return rec;
    }

    public DataKey getKey() {
        return new DataKey<Long,String,String>(getId(),null,null);
    }

    public void setId(long id) {
        setLong(ID, id);
    }
    public long getId() {
        return getLong(ID);
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

    public void setDefaultCrewId(long id) {
        setLong(DEFAULTCREWID, id);
    }
    public long getDefaultCrewId() {
        return getLong(DEFAULTCREWID);
    }


    public void setAllowedGroupIdList(ArrayList<Long> list) {
        StringBuilder s = new StringBuilder();
        for (long l : list) {
            s.append( (s.length() == 0 ? Long.toString(l) :
                ";" + Long.toString(l)) );
        }
        setString(ALLOWEDGROUPIDLIST, s.toString());
    }
    public ArrayList<Long> getAllowedGroupIdListp() {
        String s = getString(ALLOWEDGROUPIDLIST);
        StringTokenizer tok = new StringTokenizer(s,";");
        ArrayList<Long> l = new ArrayList<Long>();
        while(tok.hasMoreTokens()) {
            l.add(Long.parseLong(tok.nextToken()));
        }
        return l;
    }

    public void setMaxNotInGroup(int maxNotInGroup) {
        setInt(MAXNOTINGROUP, maxNotInGroup);
    }
    public int getMaxNotInGroup() {
        return getInt(MAXNOTINGROUP);
    }

    public void setRequiredGroupId(long id) {
        setLong(REQUIREDGROUPID, id);
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

}
