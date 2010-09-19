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

public class ProjectRecord extends DataRecord {

    protected static final String TYPE_PROJECT = "Project";
    protected static final String TYPE_CLUB    = "Club";
    protected static final String TYPE_LOGBOOK = "Logbook";

    protected static final String TYPE                         = "Type"; // one of TYPE_XXX constants
    protected static final String NAME                         = "Name"; // Project Name, Logbook Name
    protected static final String DESCRIPTION                  = "Description";

    // Fields for Type=Project
    // NAME
    // DESCRIPTION
    protected static final String ADMINNAME                    = "AdminName";
    protected static final String ADMINEMAIL                   = "AdminEmail";

    // Fields for Type=Club
    protected static final String CLUBNAME                     = "ClubName";
    protected static final String ADDRESSSTREET                = "AddressStreet";
    protected static final String ADDRESSCITY                  = "AddressCity";
    protected static final String ASSOCIATIONREGIONALNAME      = "RegionalAssociationName";
    protected static final String ASSOCIATIONREGIONALMEMBERNO  = "RegionalAssociationMemberNo";
    protected static final String ASSOCIATIONREGIONALLOGIN     = "RegionalAssociationLogin";
    protected static final String ASSOCIATIONGLOBALNAME        = "GlobalAssociationName";
    protected static final String ASSOCIATIONGLOBALMEMBERNO    = "GlobalAssociationMemberNo";
    protected static final String ASSOCIATIONGLOBALLOGIN       = "GlobalAssociationLogin";
    protected static final String MEMBEROFDRV                  = "MemberOfDRV";
    protected static final String MEMBEROFSRV                  = "MemberOfSRV";
    protected static final String MEMBEROFADH                  = "MemberOfADH";
    protected static final String AREAID                       = "AreaID"; // Zielbereich

    // Fields for Type=Logbook
    // NAME (StorageObject Name)
    // DESCRIPTION
    protected static final String STARTDATE                    = "StartDate";
    protected static final String ENDDATE                      = "EndDate";


    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(TYPE);                          t.add(IDataAccess.DATA_STRING);
        f.add(NAME);                          t.add(IDataAccess.DATA_STRING);
        f.add(DESCRIPTION);                   t.add(IDataAccess.DATA_STRING);
        f.add(ADMINNAME);                     t.add(IDataAccess.DATA_STRING);
        f.add(ADMINEMAIL);                    t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSSTREET);                 t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSCITY);                   t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONREGIONALNAME);       t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONREGIONALMEMBERNO);   t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONREGIONALLOGIN);      t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONGLOBALNAME);         t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONGLOBALMEMBERNO);     t.add(IDataAccess.DATA_STRING);
        f.add(ASSOCIATIONGLOBALLOGIN);        t.add(IDataAccess.DATA_STRING);
        f.add(MEMBEROFDRV);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MEMBEROFSRV);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MEMBEROFADH);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(AREAID);                        t.add(IDataAccess.DATA_INTEGER);
        f.add(STARTDATE);                     t.add(IDataAccess.DATA_DATE);
        f.add(ENDDATE);                       t.add(IDataAccess.DATA_DATE);
        constructArrays(f, t, false);

        KEY = new String[] { TYPE, NAME };
    }

    public ProjectRecord() {
    }

    public ProjectRecord(String type, String name) {
        set(TYPE, type);
        set(NAME, name);
    }

    public ProjectRecord(ProjectRecord orig) {
        synchronized(orig.data) {
            for (int i = 0; i < data.length; i++) {
                data[i] = orig.data[i];
            }
        }
    }

    public static DataKey getDataKey(String type, String name) {
        return new DataKey<String,String,String>(type,name,null);
    }

    public DataKey getKey() {
        return new DataKey<String,String,String>(getType(),getName(),null);
    }

    public void setType(String type) {
        set(TYPE, type);
    }
    public void setName(String name) {
        set(NAME, name);
    }
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }
    public void setAdminName(String adminName) {
        set(ADMINNAME, adminName);
    }
    public void setAdminEmail(String adminEmail) {
        set(ADMINEMAIL, adminEmail);
    }
    public void setStartDate(DataTypeDate startDate) {
        set(STARTDATE, startDate);
    }
    public void setEndDate(DataTypeDate endDate) {
        set(ENDDATE, endDate);
    }

    public String getType() {
        return (String)get(TYPE);
    }
    public String getName() {
        return (String)get(NAME);
    }
    public String getDescription() {
        return (String)get(DESCRIPTION);
    }
    public String getAdminName() {
        return (String)get(ADMINNAME);
    }
    public String getAdminEmail() {
        return (String)get(ADMINEMAIL);
    }
    public String getAddressStreet() {
        return (String)get(ADDRESSSTREET);
    }
    public String getAddressCity() {
        return (String)get(ADDRESSCITY);
    }
    public DataTypeDate getStartDate() {
        return (DataTypeDate)get(STARTDATE);
    }
    public DataTypeDate getEndDate() {
        return (DataTypeDate)get(ENDDATE);
    }


}
