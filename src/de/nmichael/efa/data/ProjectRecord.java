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

public class ProjectRecord extends DataRecord {

    public static final String TYPE_PROJECT = "Project";
    public static final String TYPE_CLUB    = "Club";
    public static final String TYPE_LOGBOOK = "Logbook";

    public static final String TYPE                         = "Type"; // one of TYPE_XXX constants
    public static final String PROJECTNAME                  = "ProjectName";
    public static final String LOGBOOKNAME                  = "LogbookName";
    public static final String DESCRIPTION                  = "Description";

    // Fields for Type=Project
    // PROJECTNAME
    // DESCRIPTION
    public static final String STORAGETYPE                  = "StorageType";
    public static final String STORAGELOCATION              = "StorageLocation";
    public static final String ADMINNAME                    = "AdminName";
    public static final String ADMINEMAIL                   = "AdminEmail";
    public static final String CURRENTLOGBOOKEFABASE        = "CurrentLogbookEfaBase";
    public static final String CURRENTLOGBOOKEFABOATHOUSE   = "CurrentLogbookEfaBoathouse";

    // Fields for Type=Club
    public static final String CLUBNAME                     = "ClubName";
    public static final String ADDRESSSTREET                = "AddressStreet";
    public static final String ADDRESSCITY                  = "AddressCity";
    public static final String ASSOCIATIONGLOBALNAME        = "GlobalAssociationName";
    public static final String ASSOCIATIONGLOBALMEMBERNO    = "GlobalAssociationMemberNo";
    public static final String ASSOCIATIONGLOBALLOGIN       = "GlobalAssociationLogin";
    public static final String ASSOCIATIONREGIONALNAME      = "RegionalAssociationName";
    public static final String ASSOCIATIONREGIONALMEMBERNO  = "RegionalAssociationMemberNo";
    public static final String ASSOCIATIONREGIONALLOGIN     = "RegionalAssociationLogin";
    public static final String MEMBEROFDRV                  = "MemberOfDRV";
    public static final String MEMBEROFSRV                  = "MemberOfSRV";
    public static final String MEMBEROFADH                  = "MemberOfADH";
    public static final String AREAID                       = "AreaID"; // Zielbereich

    // Fields for Type=Logbook
    // LOGBOOKNAME (StorageObject Name)
    // DESCRIPTION
    public static final String STARTDATE                    = "StartDate";
    public static final String ENDDATE                      = "EndDate";


    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(TYPE);                          t.add(IDataAccess.DATA_STRING);
        f.add(PROJECTNAME);                   t.add(IDataAccess.DATA_STRING);
        f.add(LOGBOOKNAME);                   t.add(IDataAccess.DATA_STRING);
        f.add(DESCRIPTION);                   t.add(IDataAccess.DATA_STRING);
        f.add(STORAGETYPE);                   t.add(IDataAccess.DATA_STRING);
        f.add(STORAGELOCATION);               t.add(IDataAccess.DATA_STRING);
        f.add(ADMINNAME);                     t.add(IDataAccess.DATA_STRING);
        f.add(ADMINEMAIL);                    t.add(IDataAccess.DATA_STRING);
        f.add(CURRENTLOGBOOKEFABASE);         t.add(IDataAccess.DATA_STRING);
        f.add(CURRENTLOGBOOKEFABOATHOUSE);    t.add(IDataAccess.DATA_STRING);
        f.add(CLUBNAME);                      t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSSTREET);                 t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSCITY);                   t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONGLOBALNAME);         t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONGLOBALMEMBERNO);     t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONGLOBALLOGIN);        t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONREGIONALNAME);       t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONREGIONALMEMBERNO);   t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONREGIONALLOGIN);      t.add(IDataAccess.DATA_STRING);
        f.add(MEMBEROFDRV);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MEMBEROFSRV);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MEMBEROFADH);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(AREAID);                        t.add(IDataAccess.DATA_INTEGER);
        f.add(STARTDATE);                     t.add(IDataAccess.DATA_DATE);
        f.add(ENDDATE);                       t.add(IDataAccess.DATA_DATE);
        MetaData metaData = constructMetaData(Project.DATATYPE, f, t, false);
        metaData.setKey(new String[] { TYPE, LOGBOOKNAME });
    }

    public ProjectRecord(Project project, MetaData metaData) {
        super(project, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public static DataKey getDataKey(String type, String logbookName) {
        return new DataKey<String,String,String>(type,logbookName,null);
    }

    public DataKey getKey() {
        return new DataKey<String,String,String>(getType(),getLogbookName(),null);
    }

    public void setType(String type) {
        setString(TYPE, type);
    }
    public void setProjectName(String projectName) {
        setString(PROJECTNAME, projectName);
    }
    public void setLogbookName(String logbookName) {
        setString(LOGBOOKNAME, logbookName);
    }
    public void setDescription(String description) {
        setString(DESCRIPTION, description);
    }
    public void setStorageType(int storageType) {
        switch(storageType) {
            case IDataAccess.TYPE_FILE_XML:
                setString(STORAGETYPE, IDataAccess.TYPESTRING_FILE_XML);
                break;
            case IDataAccess.TYPE_DB_SQL:
                setString(STORAGETYPE, IDataAccess.TYPESTRING_DB_SQL);
                break;
        }
    }
    public void setStorageLocation(String storageLocation) {
        setString(STORAGELOCATION, storageLocation);
    }
    public void setAdminName(String adminName) {
        setString(ADMINNAME, adminName);
    }
    public void setAdminEmail(String adminEmail) {
        setString(ADMINEMAIL, adminEmail);
    }
    public void setCurrentLogbookEfaBase(String currentLogbook) {
        setString(CURRENTLOGBOOKEFABASE, currentLogbook);
    }
    public void setCurrentLogbookEfaBoathouse(String currentLogbook) {
        setString(CURRENTLOGBOOKEFABOATHOUSE, currentLogbook);
    }
    public void setClubName(String clubName) {
        setString(CLUBNAME, clubName);
    }
    public void setAddressStreet(String addressStreet) {
        setString(ADDRESSSTREET, addressStreet);
    }
    public void setAddressCity(String addressCity) {
        setString(ADDRESSCITY, addressCity);
    }
    public void setRegionalAssociationName(String name) {
        setString(ASSOCIATIONREGIONALNAME, name);
    }
    public void setRegionalAssociationMemberNo(String memberNo) {
        setString(ASSOCIATIONREGIONALMEMBERNO, memberNo);
    }
    public void setRegionalAssociationLogin(String login) {
        setString(ASSOCIATIONREGIONALLOGIN, login);
    }
    public void setGlobalAssociationName(String name) {
        setString(ASSOCIATIONGLOBALNAME, name);
    }
    public void setGlobalAssociationMemberNo(String memberNo) {
        setString(ASSOCIATIONGLOBALMEMBERNO, memberNo);
    }
    public void setGlobalAssociationLogin(String login) {
        setString(ASSOCIATIONGLOBALLOGIN, login);
    }
    public void setMemberOfDRV(boolean member) {
        setBool(MEMBEROFDRV, member);
    }
    public void setMemberOfSRV(boolean member) {
        setBool(MEMBEROFSRV, member);
    }
    public void setMemberOfADH(boolean member) {
        setBool(MEMBEROFADH, member);
    }
    public void setAreaId(int areaId) {
        setInt(AREAID, areaId);
    }
    public void setStartDate(DataTypeDate startDate) {
        setDate(STARTDATE, startDate);
    }
    public void setEndDate(DataTypeDate endDate) {
        setDate(ENDDATE, endDate);
    }

    public String getType() {
        return getString(TYPE);
    }
    public String getProjectName() {
        return getString(PROJECTNAME);
    }
    public String getLogbookName() {
        return getString(LOGBOOKNAME);
    }
    public String getDescription() {
        return getString(DESCRIPTION);
    }
    public int getStorageType() {
        String s = getString(STORAGETYPE);
        if (s != null && s.equals(IDataAccess.TYPESTRING_FILE_XML)) {
            return IDataAccess.TYPE_FILE_XML;
        }
        if (s != null && s.equals(IDataAccess.TYPESTRING_DB_SQL)) {
            return IDataAccess.TYPE_DB_SQL;
        }
        return -1;
    }
    public String getStorageLocation() {
        return getString(STORAGELOCATION);
    }
    public String getAdminName() {
        return getString(ADMINNAME);
    }
    public String getAdminEmail() {
        return getString(ADMINEMAIL);
    }
    public String getCurrentLogbookEfaBase() {
        return getString(CURRENTLOGBOOKEFABASE);
    }
    public String getCurrentLogbookEfaBoathouse() {
        return getString(CURRENTLOGBOOKEFABOATHOUSE);
    }
    public String getClubName() {
        return getString(CLUBNAME);
    }
    public String getAddressStreet() {
        return getString(ADDRESSSTREET);
    }
    public String getAddressCity() {
        return getString(ADDRESSCITY);
    }
    public String getRegionalAssociationName() {
        return getString(ASSOCIATIONREGIONALNAME);
    }
    public String getRegionalAssociationMemberNo() {
        return getString(ASSOCIATIONREGIONALMEMBERNO);
    }
    public String getRegionalAssociationLogin() {
        return getString(ASSOCIATIONREGIONALLOGIN);
    }
    public String getGlobalAssociationName() {
        return getString(ASSOCIATIONGLOBALNAME);
    }
    public String getGlobalAssociationMemberNo() {
        return getString(ASSOCIATIONGLOBALMEMBERNO);
    }
    public String getGlobalAssociationLogin() {
        return getString(ASSOCIATIONGLOBALLOGIN);
    }
    public boolean getMemberOfDRV() {
        return getBool(MEMBEROFDRV);
    }
    public boolean getMemberOfSRV() {
        return getBool(MEMBEROFSRV);
    }
    public boolean getMemberOfADH() {
        return getBool(MEMBEROFADH);
    }
    public int getAreaId() {
        return getInt(AREAID);
    }
    public DataTypeDate getStartDate() {
        return getDate(STARTDATE);
    }
    public DataTypeDate getEndDate() {
        return getDate(ENDDATE);
    }
    
    public Vector<IItemType> getGuiItems() {
        return null; // not supported for ProjectRecord
    }

    public TableItemHeader[] getGuiTableHeader() {
        return null; // not supported for ProjectRecord
    }

    public TableItem[] getGuiTableItems() {
        return null; // not supported for ProjectRecord
    }

}
