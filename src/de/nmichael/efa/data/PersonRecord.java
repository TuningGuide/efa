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

public class PersonRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String FIRSTNAME           = "FirstName";
    public static final String LASTNAME            = "LastName";
    public static final String TITLE               = "Title";
    public static final String GENDER              = "Gender";
    public static final String BIRTHDAY            = "Birthday";
    public static final String ASSOCIATION         = "Association";
    public static final String STATUS              = "Status";
    public static final String ADDRESSSTREET       = "AddressStreet";
    public static final String ADDRESSADDITIONAL   = "AddressAdditional";
    public static final String ADDRESSCITY         = "AddressCity";
    public static final String ADDRESSZIP          = "AddressZip";
    public static final String ADDRESSCOUNTRY      = "AddressCountry";
    public static final String MEMBERSHIPNO        = "MembershipNo";
    public static final String PASSWORD            = "Password";
    public static final String EXTERNALID          = "ExternalId";
    public static final String DISABILITY          = "Disability";
    public static final String EXCLUDEFROMCOMPETE  = "ExcludeFromCompetition";
    public static final String INPUTSHORTCUT       = "InputShortcut";
    public static final String FREEUSE1            = "FreeUse1";
    public static final String FREEUSE2            = "FreeUse2";
    public static final String FREEUSE3            = "FreeUse3";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(FIRSTNAME);                         t.add(IDataAccess.DATA_STRING);
        f.add(LASTNAME);                          t.add(IDataAccess.DATA_STRING);
        f.add(TITLE);                             t.add(IDataAccess.DATA_STRING);
        f.add(GENDER);                            t.add(IDataAccess.DATA_STRING);
        f.add(BIRTHDAY);                          t.add(IDataAccess.DATA_DATE);
        f.add(ASSOCIATION);                       t.add(IDataAccess.DATA_STRING);
        f.add(STATUS);                            t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSSTREET);                     t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSADDITIONAL);                 t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSCITY);                       t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSZIP);                        t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSCOUNTRY);                    t.add(IDataAccess.DATA_STRING);
        f.add(MEMBERSHIPNO);                      t.add(IDataAccess.DATA_STRING);
        f.add(PASSWORD);                          t.add(IDataAccess.DATA_STRING);
        f.add(EXTERNALID);                        t.add(IDataAccess.DATA_STRING);
        f.add(DISABILITY);                        t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EXCLUDEFROMCOMPETE);                t.add(IDataAccess.DATA_BOOLEAN);
        f.add(INPUTSHORTCUT);                     t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE1);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE2);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE3);                          t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Persons.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(new String[] { FIRSTNAME, LASTNAME, ASSOCIATION });
    }

    public PersonRecord(Persons persons, MetaData metaData) {
        super(persons, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Long,String>(getId(),getValidFrom(),null);
    }

    public void setId(UUID id) {
        setUUID(ID, id);
    }
    public UUID getId() {
        return getUUID(ID);
    }

    public void setFirstName(String name) {
        setString(FIRSTNAME, name);
    }
    public String getFirstName() {
        return getString(FIRSTNAME);
    }

    public void setLastName(String name) {
        setString(LASTNAME, name);
    }
    public String getLastName() {
        return getString(LASTNAME);
    }

    public void setTitle(String title) {
        setString(TITLE, title);
    }
    public String getTitle() {
        return getString(TITLE);
    }

    public void setGender(String gender) {
        setString(GENDER, gender);
    }
    public String getGender() {
        return getString(GENDER);
    }

    public void setBirthday(DataTypeDate date) {
        setDate(BIRTHDAY, date);
    }
    public DataTypeDate getBirthday() {
        return getDate(BIRTHDAY);
    }

    public void setAssocitation(String name) {
        setString(ASSOCIATION, name);
    }
    public String getAssocitation() {
        return getString(ASSOCIATION);
    }

    public void setStatus(String name) {
        setString(STATUS, name);
    }
    public String getStatus() {
        return getString(STATUS);
    }

    public void setAddressStreet(String street) {
        setString(ADDRESSSTREET, street);
    }
    public String getAddressStreet() {
        return getString(ADDRESSSTREET);
    }

    public void setAddressAdditional(String addressAdditional) {
        setString(ADDRESSADDITIONAL, addressAdditional);
    }
    public String getAddressAdditional() {
        return getString(ADDRESSADDITIONAL);
    }

    public void setAddressCity(String city) {
        setString(ADDRESSCITY, city);
    }
    public String getAddressCity() {
        return getString(ADDRESSCITY);
    }

    public void setAddressZip(String zip) {
        setString(ADDRESSZIP, zip);
    }
    public String getAddressZip() {
        return getString(ADDRESSZIP);
    }

    public void setAddressCountry(String country) {
        setString(ADDRESSCOUNTRY, country);
    }
    public String geAddressCountry() {
        return getString(ADDRESSCOUNTRY);
    }

    public void setMembershipNo(String no) {
        setString(MEMBERSHIPNO, no);
    }
    public String getMembershipNo() {
        return getString(MEMBERSHIPNO);
    }

    public void setPassword(String password) {
        setString(PASSWORD, password);
    }
    public String getPassword() {
        return getString(PASSWORD);
    }

    public void setExternalId(String id) {
        setString(EXTERNALID, id);
    }
    public String getExternalId() {
        return getString(EXTERNALID);
    }

    public void setDisability(boolean disabled) {
        setBool(DISABILITY, disabled);
    }
    public boolean getDisability() {
        return getBool(DISABILITY);
    }

    public void setExcludeFromCompetition(boolean exclude) {
        setBool(EXCLUDEFROMCOMPETE, exclude);
    }
    public boolean getExcludeFromCompetition() {
        return getBool(EXCLUDEFROMCOMPETE);
    }

    public void setInputShortcut(String shortcut) {
        setString(INPUTSHORTCUT, shortcut);
    }
    public String getInputShortcut() {
        return getString(INPUTSHORTCUT);
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
