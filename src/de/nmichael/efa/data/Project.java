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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.core.types.*;
import de.nmichael.efa.ex.EfaException;
import java.util.*;

// @i18n complete

public class Project extends Persistence {


    public static final String DATATYPE = "e2prj";
    private Hashtable<String,Persistence> persistence = new Hashtable<String,Persistence>();

    // Note: storageType and storageLocation are only type and location for the project file itself
    // (which is always being stored in the file system). The storageType and storageLocation for
    // the project's content may differ.
    public Project(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Projekt"));
        try {
            ProjectRecord.initialize();
            MetaData meta = MetaData.getMetaData(DATATYPE);
            for (int i=0; i<meta.getNumberOfFields(); i++) {
                dataAccess.registerDataField(meta.getFieldName(i), meta.getFieldType(i));
            }
            dataAccess.setKey(meta.getKeyFields());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public DataRecord createNewRecord() {
        return ProjectRecord.createProjectRecord();
    }

    public ProjectRecord createNewLogbookRecord(String logbookName) {
        return ProjectRecord.createProjectRecord(ProjectRecord.TYPE_LOGBOOK, logbookName);
    }

    public void setEmptyProject(String name) {
        try {
            dataAccess.truncateAllData();
            ProjectRecord rec;
            rec = (ProjectRecord)createNewRecord();
            rec.setType(ProjectRecord.TYPE_PROJECT);
            rec.setProjectName(name);
            dataAccess.add(rec);
            rec = (ProjectRecord)createNewRecord();
            rec.setType(ProjectRecord.TYPE_CLUB);
            dataAccess.add(rec);
        } catch(Exception e) {
        }
    }

    public ProjectRecord getProjectRecord() {
        try {
            return (ProjectRecord)dataAccess.get(ProjectRecord.getDataKey(ProjectRecord.TYPE_PROJECT, null));
        } catch(Exception e) {
            return null;
        }
    }

    public ProjectRecord getClubRecord() {
        try {
            return (ProjectRecord)dataAccess.get(ProjectRecord.getDataKey(ProjectRecord.TYPE_CLUB, null));
        } catch(Exception e) {
            return null;
        }
    }

    public ProjectRecord getLoogbookRecord(String logbookName) {
        try {
            return (ProjectRecord)dataAccess.get(ProjectRecord.getDataKey(ProjectRecord.TYPE_LOGBOOK, logbookName));
        } catch(Exception e) {
            return null;
        }
    }

    public void addLogbookRecord(ProjectRecord rec) throws EfaException {
        if (!rec.getType().equals(ProjectRecord.TYPE_LOGBOOK)) {
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION, dataAccess.getUID()+": Attempt to add a Record as a Logbook Record which is not a Logbook Record");
        }
        dataAccess.add(rec);
    }

    private Persistence getPersistence(Class c, String name, boolean createNewIfDoesntExist, String description) {
        Persistence p = null;
        try {
            String key = c.getCanonicalName()+":"+name;
            p = persistence.get(key);
            if (p == null) {
                p = (Persistence)c.getConstructor(int.class, String.class, String.class).newInstance(getProjectStorageType(), getProjectStorageLocation(), name);
            }
            if (!p.isOpen()) {
                p.open(createNewIfDoesntExist);
            }
            if (p != null && p.isOpen()) {
                persistence.put(key, p);
            }
        } catch(Exception e) {
            Logger.log(Logger.ERROR,Logger.MSG_DATA_OPENFAILED,
                    LogString.logstring_fileOpenFailed(p.toString(), description, e.toString()));
            return null;
        }
        return p;
    }

    public Logbook getLogbook(String logbookName, boolean createNewIfDoesntExist) {
        ProjectRecord rec = getLoogbookRecord(logbookName);
        if (rec == null) {
            return null;
        }
        return (Logbook)getPersistence(Logbook.class, logbookName,
                createNewIfDoesntExist, International.getString("Fahrtenbuch"));
    }

    public Boats getBoats(boolean createNewIfDoesntExist) {
        return (Boats)getPersistence(Boats.class, "boats",
                createNewIfDoesntExist, International.getString("Boote"));
    }

    public void setProjectDescription(String description) {
        getProjectRecord().setDescription(description);
    }

    // set the storageType for this project's content
    public void setProjectStorageType(int storageType) {
        getProjectRecord().setStorageType(storageType);
    }

    public void setAdminName(String adminName) {
        getProjectRecord().setAdminName(adminName);
    }

    public void setAdminEmail(String adminEmail) {
        getProjectRecord().setAdminEmail(adminEmail);
    }

    public void setClubName(String clubName) {
        getClubRecord().setClubName(clubName);
    }
    public void setClubAddressStreet(String street) {
        getClubRecord().setAddressStreet(street);
    }
    public void setClubAddressCity(String city) {
        getClubRecord().setAddressCity(city);
    }
    public void setClubAreaId(int areaId) {
        getClubRecord().setAreaId(areaId);
    }


    // set the storageLocation for this project's content
    public void setProjectStorageLocation(String storageLocation) {
        if (getProjectStorageType() == IDataAccess.TYPE_FILE_XML) {
            // for file-based projects: storageLocation of content is always relative to this project file!
            getProjectRecord().setStorageLocation(null);
        } else {
            getProjectRecord().setStorageLocation(storageLocation);
        }
    }

    public String getProjectName() {
        return getProjectRecord().getProjectName();
    }

    public String getProjectDescription() {
        return getProjectRecord().getDescription();
    }

    // get the storageType for this project's content
    public int getProjectStorageType() {
        return getProjectRecord().getStorageType();
    }

    // get the storageLocation for this project's content
    public String getProjectStorageLocation() {
        if (getProjectStorageType() == IDataAccess.TYPE_FILE_XML) {
            // for file-based projects: storageLocation of content is always relative to this project file!
            return dataAccess.getStorageLocation() + getProjectName() + Daten.fileSep;
        }
        return getProjectRecord().getStorageLocation();
    }





    public Vector<DataItem> getGuiItems() {
        Vector<DataItem> v = new Vector<DataItem>();
        try {
            DataKeyIterator it = dataAccess.getIterator();
            ProjectRecord rec = (ProjectRecord)dataAccess.getFirst(it);
            while (rec != null) {
                String type = rec.getType();
                if (type == null) {
                    continue;
                }
                if (type.equals(ProjectRecord.TYPE_PROJECT)) {
                    // Name
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.PROJECTNAME,
                            rec.getProjectName(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Name"))));

                    // Description
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.DESCRIPTION,
                            rec.getDescription(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Beschreibung"))));

                    // AdminName
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.ADMINNAME,
                            rec.getAdminName(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Dein Name"))));

                    // AdminEmail
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.ADMINEMAIL,
                            rec.getAdminEmail(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Deine email-Adresse"))));
                }

                if (type.equals(ProjectRecord.TYPE_LOGBOOK)) {
                    // Name
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.LOGBOOKNAME,
                            rec.getLogbookName(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Name"))));

                    // Description
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.DESCRIPTION,
                            rec.getDescription(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Beschreibung"))));

                    // StartDate
                    v.add(new DataItem(rec.getKey(),new ItemTypeDate(rec.getKey().toString()+":"+ProjectRecord.STARTDATE,
                            rec.getStartDate(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Beginn des Zeitraums"))));

                    // EndDate
                    v.add(new DataItem(rec.getKey(),new ItemTypeDate(rec.getKey().toString()+":"+ProjectRecord.ENDDATE,
                            rec.getEndDate(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Ende des Zeitraums"))));

                }

                rec = (ProjectRecord)dataAccess.getNext(it);
            }
        } catch(Exception e) {
        }
        return v;
    }

}
