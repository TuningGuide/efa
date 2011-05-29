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

import de.nmichael.efa.gui.dataedit.DataEditDialog;
import de.nmichael.efa.gui.dataedit.BoatReservationEditDialog;
import de.nmichael.efa.gui.dataedit.BoatDamageEditDialog;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.Daten;
import java.util.regex.*;
import java.util.*;
import javax.swing.*;

// @i18n complete

public class BoatRecord extends DataRecord implements IItemFactory, IItemListenerDataRecordTable {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                    = "Id";
    public static final String NAME                  = "Name";
    public static final String NAMEAFFIX             = "NameAffixow";
    public static final String OWNER                 = "Owner";
    public static final String LASTVARIANT           = "LastVariant";
    public static final String TYPEVARIANT           = "TypeVariant";
    public static final String TYPEDESCRIPTION       = "TypeDescription";
    public static final String TYPETYPE              = "TypeType";
    public static final String TYPESEATS             = "TypeSeats";
    public static final String TYPERIGGING           = "TypeRigging";
    public static final String TYPECOXING            = "TypeCoxing";
    // RESERVATIONS stored in BoatReservations
    // DAMAGES stored in BoatDamages
    public static final String ALLOWEDGROUPIDLIST    = "AllowedGroupIdList";
    public static final String MAXNOTINGROUP         = "MaxNotInGroup";
    public static final String REQUIREDGROUPID       = "RequiredGroupId";
    public static final String ONLYWITHBOATCAPTAIN   = "OnlyWithBoatCaptain";
    public static final String MANUFACTURER          = "Manufacturer";
    public static final String MODEL                 = "Model";
    public static final String MAXCREWWEIGHT         = "MaxCrewWeight";
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

    public static final String[] IDX_NAME_NAMEAFFIX = new String[] { NAME, NAMEAFFIX };

    private static String GUIITEM_BOATTYPES          = "GUIITEM_BOATTYPES";
    private static String GUIITEM_ALLOWEDGROUPIDLIST = "GUIITEM_ALLOWEDGROUPIDLIST";
    private static String GUIITEM_RESERVATIONS       = "GUIITEM_RESERVATIONS";
    private static String GUIITEM_DAMAGES            = "GUIITEM_DAMAGES";

    private static Pattern qnamePattern = Pattern.compile("(.+) \\(([^\\(\\)]+)\\)");

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(NAMEAFFIX);                         t.add(IDataAccess.DATA_STRING);
        f.add(OWNER);                             t.add(IDataAccess.DATA_STRING);
        f.add(LASTVARIANT);                       t.add(IDataAccess.DATA_INTEGER);
        f.add(TYPEVARIANT);                       t.add(IDataAccess.DATA_LIST_INTEGER);
        f.add(TYPEDESCRIPTION);                   t.add(IDataAccess.DATA_LIST_STRING);
        f.add(TYPETYPE);                          t.add(IDataAccess.DATA_LIST_STRING);
        f.add(TYPESEATS);                         t.add(IDataAccess.DATA_LIST_STRING);
        f.add(TYPERIGGING);                       t.add(IDataAccess.DATA_LIST_STRING);
        f.add(TYPECOXING);                        t.add(IDataAccess.DATA_LIST_STRING);
        f.add(ALLOWEDGROUPIDLIST);                t.add(IDataAccess.DATA_LIST_UUID);
        f.add(MAXNOTINGROUP);                     t.add(IDataAccess.DATA_INTEGER);
        f.add(REQUIREDGROUPID);                   t.add(IDataAccess.DATA_UUID);
        f.add(ONLYWITHBOATCAPTAIN);               t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MANUFACTURER);                      t.add(IDataAccess.DATA_STRING);
        f.add(MODEL);                             t.add(IDataAccess.DATA_STRING);
        f.add(MAXCREWWEIGHT);                     t.add(IDataAccess.DATA_INTEGER);
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
        metaData.addIndex(IDX_NAME_NAMEAFFIX);
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

    public void setNameAffix(String affix) {
        setString(NAMEAFFIX, affix);
    }
    public String getNameAffix() {
        return getString(NAMEAFFIX);
    }

    public void setOwner(String owner) {
        setString(OWNER, owner);
    }
    public String getOwner() {
        return getString(OWNER);
    }

    public int getNumberOfVariants() {
        DataTypeList l = getList(TYPEVARIANT, IDataAccess.DATA_INTEGER);
        return (l != null ? l.length() : 0);
    }
    
    public int getVariantIndex(int variant) {
        DataTypeList l = getList(TYPEVARIANT, IDataAccess.DATA_INTEGER);
        for (int i=0; l != null && i<l.length(); i++) {
            Integer v = (Integer)l.get(i);
            if (v.intValue() == variant) {
                return i;
            }
        }
        return -1;
    }

    public int getTypeVariant(int idx) {
        DataTypeList l = getList(TYPEVARIANT, IDataAccess.DATA_INTEGER);
        if (l == null || idx < 0 || idx >= l.length()) {
            return -1;
        } else {
            return ((Integer)l.get(idx)).intValue();
        }
    }

    public String getTypeDescription(int idx) {
        DataTypeList l = getList(TYPEDESCRIPTION, IDataAccess.DATA_STRING);
        if (l == null || idx < 0 || idx >= l.length()) {
            return null;
        } else {
            return ((String)l.get(idx));
        }
    }

    public String getTypeType(int idx) {
        DataTypeList l = getList(TYPETYPE, IDataAccess.DATA_STRING);
        if (l == null || idx < 0 || idx >= l.length()) {
            return null;
        } else {
            return (String)l.get(idx);
        }
    }

    public String getTypeSeats(int idx) {
        DataTypeList l = getList(TYPESEATS, IDataAccess.DATA_STRING);
        if (l == null || idx < 0 || idx >= l.length()) {
            return null;
        } else {
            return (String)l.get(idx);
        }
    }

    public String getTypeRigging(int idx) {
        DataTypeList l = getList(TYPERIGGING, IDataAccess.DATA_STRING);
        if (l == null || idx < 0 || idx >= l.length()) {
            return null;
        } else {
            return (String)l.get(idx);
        }
    }

    public String getTypeCoxing(int idx) {
        DataTypeList l = getList(TYPECOXING, IDataAccess.DATA_STRING);
        if (l == null || idx < 0 || idx >= l.length()) {
            return null;
        } else {
            return (String)l.get(idx);
        }
    }

    public int addTypeVariant(String description, String type, String seats, String rigging, String coxing) {
        int variant = getInt(LASTVARIANT);
        if (variant == IDataAccess.UNDEFINED_INT || variant < 0) {
            variant = 0;
        }
        variant++; // start with 1 as first variant

        if (description == null) {
            description = "";
        }
        if (type == null) {
            type = EfaTypes.TYPE_BOAT_OTHER;
        }
        if (seats == null) {
            seats = EfaTypes.TYPE_NUMSEATS_OTHER;
        }
        if (rigging == null) {
            rigging = EfaTypes.TYPE_RIGGING_OTHER;
        }
        if (coxing == null) {
            coxing = EfaTypes.TYPE_COXING_OTHER;
        }

        DataTypeList lvariant = getList(TYPEVARIANT, IDataAccess.DATA_INTEGER);
        DataTypeList ldescription = getList(TYPEDESCRIPTION, IDataAccess.DATA_STRING);
        DataTypeList ltype = getList(TYPETYPE, IDataAccess.DATA_STRING);
        DataTypeList lseats = getList(TYPESEATS, IDataAccess.DATA_STRING);
        DataTypeList lrigging = getList(TYPERIGGING, IDataAccess.DATA_STRING);
        DataTypeList lcoxing = getList(TYPECOXING, IDataAccess.DATA_STRING);

        if (lvariant == null) {
            lvariant = DataTypeList.parseList(Integer.toString(variant), IDataAccess.DATA_INTEGER);
        } else {
            lvariant.add(variant);
        }
        if (ldescription == null) {
            ldescription = DataTypeList.parseList(description, IDataAccess.DATA_STRING);
        } else {
            ldescription.add(description);
        }
        if (ltype == null) {
            ltype = DataTypeList.parseList(type, IDataAccess.DATA_STRING);
        } else {
            ltype.add(type);
        }
        if (lseats == null) {
            lseats = DataTypeList.parseList(seats, IDataAccess.DATA_STRING);
        } else {
            lseats.add(seats);
        }
        if (lrigging == null) {
            lrigging = DataTypeList.parseList(rigging, IDataAccess.DATA_STRING);
        } else {
            lrigging.add(rigging);
        }
        if (lcoxing == null) {
            lcoxing = DataTypeList.parseList(coxing, IDataAccess.DATA_STRING);
        } else {
            lcoxing.add(coxing);
        }

        setInt(LASTVARIANT, variant);
        setList(TYPEVARIANT, lvariant);
        setList(TYPEDESCRIPTION, ldescription);
        setList(TYPETYPE, ltype);
        setList(TYPESEATS, lseats);
        setList(TYPERIGGING, lrigging);
        setList(TYPECOXING, lcoxing);

        return variant;
    }

    public boolean setTypeVariant(int idx, String description, String type, String seats, String rigging, String coxing) {
        if (description == null) {
            description = "";
        }
        if (type == null) {
            type = EfaTypes.TYPE_BOAT_OTHER;
        }
        if (seats == null) {
            seats = EfaTypes.TYPE_NUMSEATS_OTHER;
        }
        if (rigging == null) {
            rigging = EfaTypes.TYPE_RIGGING_OTHER;
        }
        if (coxing == null) {
            coxing = EfaTypes.TYPE_COXING_OTHER;
        }

        DataTypeList ldescription = getList(TYPEDESCRIPTION, IDataAccess.DATA_STRING);
        DataTypeList ltype = getList(TYPETYPE, IDataAccess.DATA_STRING);
        DataTypeList lseats = getList(TYPESEATS, IDataAccess.DATA_STRING);
        DataTypeList lrigging = getList(TYPERIGGING, IDataAccess.DATA_STRING);
        DataTypeList lcoxing = getList(TYPECOXING, IDataAccess.DATA_STRING);

        if (idx < 0 ||
                ldescription == null || idx >= ldescription.length() ||
                ltype == null || idx >= ltype.length() ||
                lseats == null || idx >= lseats.length() ||
                lrigging == null || idx >= lrigging.length() ||
                lcoxing == null || idx >= lcoxing.length() ) {
            return false;
        }

        ldescription.set(idx, description);
        ltype.set(idx, type);
        lseats.set(idx, seats);
        lrigging.set(idx, rigging);
        lcoxing.set(idx, coxing);

        setList(TYPEDESCRIPTION, ldescription);
        setList(TYPETYPE, ltype);
        setList(TYPESEATS, lseats);
        setList(TYPERIGGING, lrigging);
        setList(TYPECOXING, lcoxing);

        return true;
    }

    public boolean deleteTypeVariant(int idx) {
        DataTypeList lvariant = getList(TYPEVARIANT, IDataAccess.DATA_INTEGER);
        DataTypeList ldescription = getList(TYPEDESCRIPTION, IDataAccess.DATA_STRING);
        DataTypeList ltype = getList(TYPETYPE, IDataAccess.DATA_STRING);
        DataTypeList lseats = getList(TYPESEATS, IDataAccess.DATA_STRING);
        DataTypeList lrigging = getList(TYPERIGGING, IDataAccess.DATA_STRING);
        DataTypeList lcoxing = getList(TYPECOXING, IDataAccess.DATA_STRING);

        if (idx < 0 ||
                lvariant == null || idx >= lvariant.length() ||
                ldescription == null || idx >= ldescription.length() ||
                ltype == null || idx >= ltype.length() ||
                lseats == null || idx >= lseats.length() ||
                lrigging == null || idx >= lrigging.length() ||
                lcoxing == null || idx >= lcoxing.length() ) {
            return false;
        }

        lvariant.remove(idx);
        ldescription.remove(idx);
        ltype.remove(idx);
        lseats.remove(idx);
        lrigging.remove(idx);
        lcoxing.remove(idx);

        setList(TYPEVARIANT, lvariant);
        setList(TYPEDESCRIPTION, ldescription);
        setList(TYPETYPE, ltype);
        setList(TYPESEATS, lseats);
        setList(TYPERIGGING, lrigging);
        setList(TYPECOXING, lcoxing);

        return true;
    }

    public void setAllowedGroupIdList(DataTypeList<UUID> list) {
        setList(ALLOWEDGROUPIDLIST, list);
    }
    public DataTypeList<UUID> getAllowedGroupIdList() {
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
    public UUID getRequiredGroupId() {
        return getUUID(REQUIREDGROUPID);
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

    public void setMaxCrewWeight(int weight) {
        setInt(MAXCREWWEIGHT, weight);
    }
    public int getMaxCrewWeight() {
        return getInt(MAXCREWWEIGHT);
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

    public String getQualifiedVariantName(int variant) {
        int idx = getVariantIndex(variant);
        String s = getTypeDescription(idx);
        if (s != null) {
            return s;
        }
        return "";
    }

    public String getQualifiedName() {
        String name = getName();
        if (name != null & name.length() > 0 && getNameAffix() != null && getNameAffix().length() > 0) {
            name = name + " (" + getNameAffix() + ")";
        }
        return (name != null ? name : "");
    }

    public String[] getQualifiedNameFields() {
        return IDX_NAME_NAMEAFFIX;
    }

    public String[] getQualifiedNameValues(String qname) {
        Matcher m = qnamePattern.matcher(qname);
        if (m.matches()) {
            return new String[] {
                m.group(1).trim(),
                m.group(2).trim()
            };
        } else {
            return new String[] {
                qname.trim(),
                null
            };
        }
    }
    public Object getUniqueIdForRecord() {
        return getId();
    }

    public int getNumberOfSeats(int idx) {
        return EfaUtil.stringFindInt(getTypeSeats(idx), 0);
    }

    public static String getDetailedBoatType(String tBoatType, String tNumSeats, String tCoxing) {
        return International.getMessage("{boattype} {numseats} {coxedornot}",
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, tBoatType),
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, tNumSeats),
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, tCoxing));
    }

    public static String getDetailedBoatType(BoatRecord r, int idx) {
        if (r == null || Daten.efaTypes == null) {
            return null;
        }
        return getDetailedBoatType(r.getTypeType(idx), r.getTypeSeats(idx), r.getTypeCoxing(idx));
    }

    public String getDetailedBoatType(int idx) {
        return getDetailedBoatType(this, idx);
    }

    public String getShortBoatType(int idx) {
        int seats = getNumberOfSeats(idx);
        String rig = getTypeRigging(idx);
        String cox = getTypeCoxing(idx);
        if (rig == null || cox == null || seats == 0) {
            return getDetailedBoatType(idx);
        }
        if (!rig.equals(EfaTypes.TYPE_RIGGING_SCULL) &&
            !rig.equals(EfaTypes.TYPE_RIGGING_SWEEP)) {
            return getDetailedBoatType(idx);
        }
        if (!cox.equals(EfaTypes.TYPE_COXING_COXED) &&
            !cox.equals(EfaTypes.TYPE_COXING_COXLESS)) {
            return getDetailedBoatType(idx);
        }
        boolean skull = rig.equals(EfaTypes.TYPE_RIGGING_SCULL);
        boolean coxed = rig.equals(EfaTypes.TYPE_COXING_COXED);
        if (seats % 2 == 1 && !skull) {
            return getDetailedBoatType(idx);
        }
        return Integer.toString(seats) + (skull ? "x" : "") + (coxed ? "+" : "-");
    }

    public String getGeneralNumberOfSeatsType(int idx) {
        String numSeats = getTypeSeats(idx);
        if (numSeats.equals(EfaTypes.TYPE_NUMSEATS_2X)) {
            return EfaTypes.TYPE_NUMSEATS_2;
        }
        if (numSeats.equals(EfaTypes.TYPE_NUMSEATS_4X)) {
            return EfaTypes.TYPE_NUMSEATS_4;
        }
        if (numSeats.equals(EfaTypes.TYPE_NUMSEATS_6X)) {
            return EfaTypes.TYPE_NUMSEATS_6;
        }
        if (numSeats.equals(EfaTypes.TYPE_NUMSEATS_8X)) {
            return EfaTypes.TYPE_NUMSEATS_8;
        }
        return numSeats;
    }

    public String getGeneralNumberOfSeatsValue(int idx) {
        return Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, getGeneralNumberOfSeatsType(idx));
    }

    public String getQualifiedBoatTypeName(int idx) {
        String name = getTypeDescription(idx);
        String type = getDetailedBoatType(idx);
        if (name == null || name.length() == 0) {
            return (type != null && type.length() > 0 ? type : toString());
        }
        return name + (type != null && type.length() > 0 ? " (" + type + ")" : "");
    }

    public String getQualifiedBoatTypeShortName(int idx) {
        String name = getTypeDescription(idx);
        String type = getShortBoatType(idx);
        if (name == null || name.length() == 0) {
            return (type != null && type.length() > 0 ? type : toString());
        }
        return name + (type != null && type.length() > 0 ? " (" + type + ")" : "");
    }


    public IItemType[] getDefaultItems(String itemName) {
        if (itemName.equals(BoatRecord.GUIITEM_BOATTYPES)) {
            IItemType[] items = new IItemType[6];
            String CAT_BASEDATA = "%01%" + International.getString("Basisdaten");
            items[0] = new ItemTypeInteger(BoatRecord.TYPEVARIANT, 0, 0, Integer.MAX_VALUE,
                    IItemType.TYPE_INTERNAL, CAT_BASEDATA, International.getString("Variante"));
            items[1] = new ItemTypeString(BoatRecord.TYPEDESCRIPTION, "",
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Beschreibung"));
            items[2] = new ItemTypeStringList(BoatRecord.TYPETYPE, Daten.efaTypes.TYPE_BOAT_OTHER,
                    EfaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Bootstyp"));
            items[3] = new ItemTypeStringList(BoatRecord.TYPESEATS, Daten.efaTypes.TYPE_NUMSEATS_OTHER,
                    EfaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Bootsplätze"));
            items[4] = new ItemTypeStringList(BoatRecord.TYPERIGGING, Daten.efaTypes.TYPE_RIGGING_OTHER,
                    EfaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Riggerung"));
            items[5] = new ItemTypeStringList(BoatRecord.TYPECOXING, Daten.efaTypes.TYPE_COXING_OTHER,
                    EfaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("Steuerung"));
            return items;
        }
        if (itemName.equals(BoatRecord.GUIITEM_ALLOWEDGROUPIDLIST)) {
            IItemType[] items = new IItemType[1];
            String CAT_USAGE = "%03%" + International.getString("Benutzung");
            items[0] = getGuiItemTypeStringAutoComplete(BoatRecord.ALLOWEDGROUPIDLIST, null,
                    IItemType.TYPE_PUBLIC, CAT_USAGE,
                    getPersistence().getProject().getGroups(false), getValidFrom(), getInvalidFrom()-1,
                    International.getString("Gruppe"));
            items[0].setFieldSize(300, -1);
            return items;
        }
        return null;
    }

    public Hashtable<String,TableItem[]> getTableItems(String itemName, Hashtable<String,IItemType[]> data) {
        if (itemName.equals(BoatRecord.GUIITEM_RESERVATIONS)) {
            if (data == null) {
                return null;
            }
            String[] keys = data.keySet().toArray(new String[0]);
            Hashtable<String,TableItem[]> tableItems = new Hashtable<String,TableItem[]>();
            for (int i=0; i<keys.length; i++) {
                
            }
        }
        return null;
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Basisdaten");
        String CAT_MOREDATA     = "%02%" + International.getString("Weitere Daten");
        String CAT_USAGE        = "%03%" + International.getString("Benutzung");
        String CAT_RESERVATIONS = "%04%" + International.getString("Reservierungen");
        String CAT_DAMAGES      = "%05%" + International.getString("Bootsschäden");
        String CAT_FREEUSE      = "%07%" + International.getString("Freie Verwendung");

        Groups groups = getPersistence().getProject().getGroups(false);
        Crews crews = getPersistence().getProject().getCrews(false);
        Destinations destinations = getPersistence().getProject().getDestinations(false);
        BoatStatus boatStatus = getPersistence().getProject().getBoatStatus(false);
        BoatReservations boatReservations = getPersistence().getProject().getBoatReservations(false);
        BoatDamages boatDamages = getPersistence().getProject().getBoatDamages(false);
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        Vector<IItemType[]> itemList;

        // CAT_BASEDATA
        v.add(item = new ItemTypeString(BoatRecord.NAME, getName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Name")));
        ((ItemTypeString)item).setNotAllowedCharacters("()");
        v.add(item = new ItemTypeString(BoatRecord.NAMEAFFIX, getNameAffix(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Namenszusatz")));
        ((ItemTypeString)item).setNotAllowedCharacters("()");
        v.add(item = new ItemTypeString(BoatRecord.OWNER, getOwner(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Eigentümer")));
        ((ItemTypeString)item).setNotAllowedCharacters("()");

        itemList = new Vector<IItemType[]>();
        for (int i=0; i<getNumberOfVariants(); i++) {
            IItemType[] items = getDefaultItems(GUIITEM_BOATTYPES);
            items[0].parseValue(Integer.toString(getTypeVariant(i)));
            items[1].parseValue(getTypeDescription(i));
            items[2].parseValue(getTypeType(i));
            items[3].parseValue(getTypeSeats(i));
            items[4].parseValue(getTypeRigging(i));
            items[5].parseValue(getTypeCoxing(i));
            itemList.add(items);
        }
        v.add(item = new ItemTypeItemList(GUIITEM_BOATTYPES, itemList, this,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Bootstyp")));
        ((ItemTypeItemList)item).setMinNumberOfItems(1);
        item.setPadding(0, 0, 20, 0);

        v.add(item = new ItemTypeLabel(BoatRecord.LASTMODIFIED, IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getMessage("zuletzt geändert am {datetime}", EfaUtil.date2String(new Date(this.getLastModified())))));
        item.setPadding(0, 0, 20, 0);
        
        // CAT_MOREDATA
        v.add(item = new ItemTypeString(BoatRecord.MANUFACTURER, getManufacturer(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Hersteller")));
        v.add(item = new ItemTypeString(BoatRecord.MODEL, getModel(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Modell")));
        v.add(item = new ItemTypeInteger(BoatRecord.MAXCREWWEIGHT, getMaxCrewWeight(), 0, Integer.MAX_VALUE, true,
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Maximales Mannschaftsgewicht")));
        v.add(item = new ItemTypeDate(BoatRecord.MANUFACTIONDATE, getManufactionDate(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Herstellungsdatum")));
        v.add(item = new ItemTypeDate(BoatRecord.PURCHASEDATE, getPurchaseDate(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Kaufdatum")));
        v.add(item = new ItemTypeDecimal(BoatRecord.PURCHASEPRICE, getPurchasePrice(), 2, true,
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Kaufpreis")));
        v.add(item = new ItemTypeString(BoatRecord.PURCHASEPRICECURRENCY, getPurchasePriceCurrency(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Währung")));

        // CAT_USAGE
        itemList = new Vector<IItemType[]>();
        DataTypeList<UUID> agList = getAllowedGroupIdList();
        for (int i=0; agList != null && i<agList.length(); i++) {
            IItemType[] items = getDefaultItems(GUIITEM_ALLOWEDGROUPIDLIST);
            ((ItemTypeStringAutoComplete)items[0]).setId(agList.get(i));
            itemList.add(items);
        }
        v.add(item = new ItemTypeItemList(GUIITEM_ALLOWEDGROUPIDLIST, itemList, this,
                IItemType.TYPE_PUBLIC, CAT_USAGE, International.getString("Gruppen, die dieses Boot benutzen dürfen")));
        ((ItemTypeItemList)item).setXForAddDelButtons(3);
        ((ItemTypeItemList)item).setPadYbetween(0);
        ((ItemTypeItemList)item).setRepeatTitle(false);
        ((ItemTypeItemList)item).setAppendPositionToEachElement(true);
        v.add(item = new ItemTypeInteger(BoatRecord.MAXNOTINGROUP, getMaxNotInGroup(), 0, Integer.MAX_VALUE, true,
                IItemType.TYPE_PUBLIC, CAT_USAGE, International.getString("Maxmimale Personenzahl nicht aus erlaubten Gruppen")));
        v.add(item = getGuiItemTypeStringAutoComplete(BoatRecord.REQUIREDGROUPID, getRequiredGroupId(),
                IItemType.TYPE_PUBLIC, CAT_USAGE, 
                groups, getValidFrom(), getInvalidFrom()-1,
                International.getString("Gruppe, der mindestens eine Person angehören muß")));
        item.setFieldSize(300, -1);
        v.add(item = new ItemTypeBoolean(BoatRecord.ONLYWITHBOATCAPTAIN, getOnlyWithBoatCaptain(),
                IItemType.TYPE_PUBLIC, CAT_USAGE, International.getString("Boot darf nur mit Obmann genutzt werden")));
        v.add(item = getGuiItemTypeStringAutoComplete(BoatRecord.DEFAULTCREWID, getDefaultCrewId(),
                IItemType.TYPE_PUBLIC, CAT_USAGE,
                crews, getValidFrom(), getInvalidFrom()-1,
                International.getString("Standard-Mannschaft")));
        item.setFieldSize(300, -1);
        v.add(item = new ItemTypeStringList(BoatRecord.DEFAULTSESSIONTYPE, getDefaultSessionType(),
                EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_USAGE,
                International.getString("Standard-Fahrtart")));
        v.add(item = getGuiItemTypeStringAutoComplete(BoatRecord.DEFAULTDESTINATIONID, getDefaultDestinationId(),
                IItemType.TYPE_PUBLIC, CAT_USAGE,
                destinations, getValidFrom(), getInvalidFrom()-1,
                International.getString("Standard-Ziel")));
        item.setFieldSize(300, -1);

        // CAT_RESERVATIONS
        v.add(item = new ItemTypeDataRecordTable(GUIITEM_RESERVATIONS,
            boatReservations.createNewRecord().getGuiTableHeader(),
            boatReservations, 0,
            BoatReservationRecord.BOATID, getId().toString(),
            null, null, this,
            IItemType.TYPE_PUBLIC, CAT_RESERVATIONS, International.getString("Reservierungen")));

        // CAT_DAMAGES
        v.add(item = new ItemTypeDataRecordTable(GUIITEM_DAMAGES,
            boatDamages.createNewRecord().getGuiTableHeader(),
            boatDamages, 0,
            BoatDamageRecord.BOATID, getId().toString(),
            null, null, this,
            IItemType.TYPE_PUBLIC, CAT_DAMAGES, International.getString("Bootsschäden")));

        // CAT_STATUS
        BoatStatusRecord boatStatusRecord = boatStatus.getBoatStatus(getId());
        if (boatStatusRecord != null) {
            v.addAll(boatStatusRecord.getGuiItems());
        }

        // CAT_FREEUSE
        v.add(item = new ItemTypeString(BoatRecord.FREEUSE1, getFreeUse1(),
                IItemType.TYPE_PUBLIC, CAT_FREEUSE, International.getString("Freie Verwendung") + " 1"));
        v.add(item = new ItemTypeString(BoatRecord.FREEUSE2, getFreeUse2(),
                IItemType.TYPE_PUBLIC, CAT_FREEUSE, International.getString("Freie Verwendung") + " 2"));
        v.add(item = new ItemTypeString(BoatRecord.FREEUSE3, getFreeUse3(),
                IItemType.TYPE_PUBLIC, CAT_FREEUSE, International.getString("Freie Verwendung") + " 3"));
  
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[3];
        header[0] = new TableItemHeader(International.getString("Name"));
        header[1] = new TableItemHeader(International.getString("Bootstyp"));
        header[2] = new TableItemHeader(International.getString("Eigentümer"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[3];
        items[0] = new TableItem(getQualifiedName());
        String type = "";
        if (getNumberOfVariants() > 0) {
            type = getDetailedBoatType(0);
        }
        if (getNumberOfVariants() > 1) {
            type = type + " ...";
        }
        items[1] = new TableItem(type);
        items[2] = new TableItem(getOwner());
        return items;
    }


    public void saveGuiItems(Vector<IItemType> items) {
        BoatStatus boatStatus = getPersistence().getProject().getBoatStatus(false);
        BoatReservations boatReservations = getPersistence().getProject().getBoatReservations(false);
        BoatDamages boatDamages = getPersistence().getProject().getBoatDamages(false);

        Vector<IItemType> boatStatusItems = null; // changed BoatStatus items

        for(IItemType item : items) {
            String name = item.getName();
            String cat = item.getCategory();
            if (name.equals(GUIITEM_BOATTYPES) && item.isChanged()) {
                ItemTypeItemList list = (ItemTypeItemList)item;
                for (int i=0; i<list.deletedSize(); i++) {
                    IItemType[] typeItems = list.getDeletedItems(i);
                    int variant = EfaUtil.stringFindInt(typeItems[0].toString(), -1);
                    int idx = getVariantIndex(variant);
                    deleteTypeVariant(idx);
                }
                for (int i=0; i<list.size(); i++) {
                    IItemType[] typeItems = list.getItems(i);
                    int variant = EfaUtil.stringFindInt(typeItems[0].toString(), -1);
                    if (variant < 0) {
                        continue;
                    }
                    if (variant == 0) {
                        addTypeVariant(typeItems[1].toString(), typeItems[2].toString(), typeItems[3].toString(), typeItems[4].toString(), typeItems[5].toString());
                    } else {
                        int idx = getVariantIndex(variant);
                        if (typeItems[1].isChanged() || typeItems[2].isChanged() || typeItems[3].isChanged() || typeItems[4].isChanged() || typeItems[5].isChanged()) {
                            setTypeVariant(idx, typeItems[1].toString(), typeItems[2].toString(), typeItems[3].toString(), typeItems[4].toString(), typeItems[5].toString());
                        }
                    }
                }
            }
            if (name.equals(GUIITEM_ALLOWEDGROUPIDLIST) && item.isChanged()) {
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
                DataTypeList<UUID> agList = new DataTypeList<UUID>();
                for (String uuid : uuidArr) {
                    agList.add(uuidList.get(uuid));
                }
                setAllowedGroupIdList(agList);
            }
            if (cat.equals(BoatStatusRecord.CAT_STATUS) && item.isChanged()) {
                if (boatStatusItems == null) {
                    boatStatusItems = new Vector<IItemType>();
                }
                boatStatusItems.add(item);
            }
        }
        if (boatStatus != null && boatStatusItems != null) {
            BoatStatusRecord boatStatusRecord = boatStatus.getBoatStatus(getId());
            if (boatStatusRecord != null) {
                boatStatusRecord.saveGuiItems(boatStatusItems);
                try {
                    boatStatus.data().update(boatStatusRecord);
                } catch(Exception estatus) {
                    Logger.logdebug(estatus);
                }
            }
        }
        super.saveGuiItems(items);
    }

    public void itemListenerActionTable(int actionId, DataRecord[] records) {
        // nothing to do
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, Persistence persistence, DataRecord record) {
        boolean newRecord = false;
        if (persistence != null && persistence instanceof BoatReservations) {
            if (record == null) {
                BoatReservations boatReservations = (BoatReservations)persistence;
                AutoIncrement autoIncrement = getPersistence().getProject().getAutoIncrement(false);
                int val = autoIncrement.nextAutoIncrementValue(boatReservations.data().getStorageObjectType());
                if (val > 0) {
                    record = boatReservations.createBoatReservationsRecord(getId(), val);
                }
                newRecord = true;
            }
            return new BoatReservationEditDialog(parent, (BoatReservationRecord) record, newRecord);
        }

        if (persistence != null && persistence instanceof BoatDamages) {
            if (record == null) {
                BoatDamages boatDamages = (BoatDamages)persistence;
                AutoIncrement autoIncrement = getPersistence().getProject().getAutoIncrement(false);
                int val = autoIncrement.nextAutoIncrementValue(boatDamages.data().getStorageObjectType());
                if (val > 0) {
                    record = boatDamages.createBoatDamageRecord(getId(), val);
                }
                newRecord = true;
            }
            return new BoatDamageEditDialog(parent, (BoatDamageRecord) record, newRecord);
        }

        return null;
    }
    
}
