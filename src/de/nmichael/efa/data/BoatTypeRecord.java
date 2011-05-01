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
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import java.util.*;

// @i18n complete

public class BoatTypeRecord { // extends DataRecord implements IItemFactory {
/*
    // =========================================================================
    // Field Names
    // =========================================================================
    public static final String BOATID = "BoatId";
    public static final String VARIANT = "Variant";
    public static final String DESCRIPTION = "Description";
    public static final String TYPE = "Type";
    public static final String SEATS = "Seats";
    public static final String RIGGING = "Rigging";
    public static final String COXING = "Coxing";

    public static final String[] IDX_BOATID = new String[] { BOATID };

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);
        t.add(IDataAccess.DATA_UUID);
        f.add(VARIANT);
        t.add(IDataAccess.DATA_INTEGER);
        f.add(DESCRIPTION);
        t.add(IDataAccess.DATA_STRING);
        f.add(TYPE);
        t.add(IDataAccess.DATA_STRING);
        f.add(SEATS);
        t.add(IDataAccess.DATA_STRING);
        f.add(RIGGING);
        t.add(IDataAccess.DATA_STRING);
        f.add(COXING);
        t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(BoatTypes.DATATYPE, f, t, true);
        metaData.setKey(new String[]{BOATID, VARIANT}); // plus VALID_FROM
        metaData.addIndex(IDX_BOATID);
    }

    public BoatTypeRecord(BoatTypes boatTypes, MetaData metaData) {
        super(boatTypes, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID, Integer, Long>(getBoatId(), getVariant(), getValidFrom());
    }

    public static DataKey getKey(UUID id, int variant, long validFrom) {
        return new DataKey<UUID, Integer, Long>(id, variant, validFrom);
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

    public int getNumberOfSeats() {
        return EfaUtil.stringFindInt(getSeats(), 0);
    }

    public static String getDetailedBoatType(String tBoatType, String tNumSeats, String tCoxing) {
        return International.getMessage("{boattype} {numseats} {coxedornot}",
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, tBoatType),
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, tNumSeats),
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, tCoxing));
    }

    public static String getDetailedBoatType(BoatTypeRecord r) {
        if (r == null || Daten.efaTypes == null) {
            return null;
        }
        return getDetailedBoatType(r.getType(), r.getSeats(), r.getCoxing());
    }

    public String getDetailedBoatType() {
        return getDetailedBoatType(this);
    }

    public String getShortBoatType() {
        int seats = getNumberOfSeats();
        String rig = getRigging();
        String cox = getCoxing();
        if (rig == null || cox == null || seats == 0) {
            return getDetailedBoatType();
        }
        if (!rig.equals(EfaTypes.TYPE_RIGGING_SCULL) &&
            !rig.equals(EfaTypes.TYPE_RIGGING_SWEEP)) {
            return getDetailedBoatType();
        }
        if (!cox.equals(EfaTypes.TYPE_COXING_COXED) &&
            !cox.equals(EfaTypes.TYPE_COXING_COXLESS)) {
            return getDetailedBoatType();
        }
        boolean skull = rig.equals(EfaTypes.TYPE_RIGGING_SCULL);
        boolean coxed = rig.equals(EfaTypes.TYPE_COXING_COXED);
        if (seats % 2 == 1 && !skull) {
            return getDetailedBoatType();
        }
        return Integer.toString(seats) + (skull ? "x" : "") + (coxed ? "+" : "-");
    }

    public String getGeneralNumberOfSeatsType() {
        String numSeats = getSeats();
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

    public String getGeneralNumberOfSeatsValue() {
        return Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, getGeneralNumberOfSeatsType());
    }

    public String getQualifiedName() {
        String name = getDescription();
        String type = getDetailedBoatType();
        if (name == null || name.length() == 0) {
            return (type != null && type.length() > 0 ? type : toString());
        }
        return name + (type != null && type.length() > 0 ? " (" + type + ")" : "");
    }

    public String getQualifiedShortName() {
        String name = getDescription();
        String type = getShortBoatType();
        if (name == null || name.length() == 0) {
            return (type != null && type.length() > 0 ? type : toString());
        }
        return name + (type != null && type.length() > 0 ? " (" + type + ")" : "");
    }

    public Vector<IItemType> getGuiItems() {
        IItemType[] items = getDefaultItems();
        items[0].parseValue(Integer.toString(getVariant()));
        items[1].parseValue(getDescription());
        items[2].parseValue(getType());
        items[3].parseValue(getSeats());
        items[4].parseValue(getRigging());
        items[5].parseValue(getCoxing());
        Vector<IItemType> v = new Vector<IItemType>();
        for (IItemType item : items) {
            v.add(item);
        }
        return v;
    }

    public IItemType[] getDefaultItems() {
        IItemType[] items = new IItemType[6];
        String CAT_BASEDATA     = "%01%" + International.getString("Basisdaten");
        items[0] = new ItemTypeInteger(BoatTypeRecord.VARIANT, 0, 0, Integer.MAX_VALUE,
                IItemType.TYPE_INTERNAL, CAT_BASEDATA, International.getString("Variante"));
        items[1] = new ItemTypeString(BoatTypeRecord.DESCRIPTION, "",
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Beschreibung"));
        items[2] = new ItemTypeStringList(BoatTypeRecord.TYPE, Daten.efaTypes.TYPE_BOAT_OTHER,
                EfaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Bootstyp"));
        items[3] = new ItemTypeStringList(BoatTypeRecord.SEATS, Daten.efaTypes.TYPE_NUMSEATS_OTHER,
                EfaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatSeatsArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Bootsplätze"));
        items[4] = new ItemTypeStringList(BoatTypeRecord.RIGGING, Daten.efaTypes.TYPE_RIGGING_OTHER,
                EfaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatRiggingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Riggerung"));
        items[5] = new ItemTypeStringList(BoatTypeRecord.COXING, Daten.efaTypes.TYPE_COXING_OTHER,
                EfaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeBoatCoxingArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Steuerung"));
        return items;
    }
*/
}
