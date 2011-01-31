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
        ProjectRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new ProjectRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public ProjectRecord createProjectRecord(String type, String logbookName) {
        ProjectRecord p = new ProjectRecord(this, MetaData.getMetaData(DATATYPE));
        p.setType(type);
        p.setLogbookName(logbookName);
        return p;
    }

    public ProjectRecord createNewLogbookRecord(String logbookName) {
        return createProjectRecord(ProjectRecord.TYPE_LOGBOOK, logbookName);
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

    public DataKey getProjectRecordKey() {
        return ProjectRecord.getDataKey(ProjectRecord.TYPE_PROJECT, null);
    }

    public DataKey getClubRecordKey() {
        return ProjectRecord.getDataKey(ProjectRecord.TYPE_CLUB, null);
    }

    public DataKey getLoogbookRecordKey(String logbookName) {
        return ProjectRecord.getDataKey(ProjectRecord.TYPE_LOGBOOK, logbookName);
    }

    public ProjectRecord getProjectRecord() {
        try {
            return (ProjectRecord)dataAccess.get(getProjectRecordKey());
        } catch(Exception e) {
            return null;
        }
    }

    public ProjectRecord getClubRecord() {
        try {
            return (ProjectRecord)dataAccess.get(getClubRecordKey());
        } catch(Exception e) {
            return null;
        }
    }

    public ProjectRecord getLoogbookRecord(String logbookName) {
        try {
            return (ProjectRecord)dataAccess.get(getLoogbookRecordKey(logbookName));
        } catch(Exception e) {
            return null;
        }
    }

    public void addLogbookRecord(ProjectRecord rec) throws EfaException {
        if (!rec.getType().equals(ProjectRecord.TYPE_LOGBOOK)) {
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION, dataAccess.getUID()+": Attempt to add a Record as a Logbook Record which is not a Logbook Record", Thread.currentThread().getStackTrace());
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
                p.setProject(this);
            }
            if (!p.isOpen()) {
                p.open(createNewIfDoesntExist);
            }
            if (p.isOpen()) {
                persistence.put(key, p);
            }
        } catch(Exception e) {
            Logger.log(Logger.ERROR,Logger.MSG_DATA_OPENFAILED,
                    LogString.logstring_fileOpenFailed( (p != null ? p.toString(): "<?>"), description, e.toString()));
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

    public SessionGroups getSessionGroups(boolean createNewIfDoesntExist) {
        return (SessionGroups)getPersistence(SessionGroups.class, "sessiongroups",
                createNewIfDoesntExist, International.getString("Fahrtengruppen"));
    }

    public Persons getPersons(boolean createNewIfDoesntExist) {
        return (Persons)getPersistence(Persons.class, "persons",
                createNewIfDoesntExist, International.getString("Personen"));
    }

    public Groups getGroups(boolean createNewIfDoesntExist) {
        return (Groups)getPersistence(Groups.class, "groups",
                createNewIfDoesntExist, International.getString("Gruppen"));
    }

    public Fahrtenabzeichen getFahrtenabzeichen(boolean createNewIfDoesntExist) {
        return (Fahrtenabzeichen)getPersistence(Crews.class, "fahrtenabzeichen",
                createNewIfDoesntExist, International.onlyFor("Fahrtenabzeichen","de"));
    }

    public Boats getBoats(boolean createNewIfDoesntExist) {
        return (Boats)getPersistence(Boats.class, "boats",
                createNewIfDoesntExist, International.getString("Boote"));
    }

    public BoatTypes getBoatTypes(boolean createNewIfDoesntExist) {
        return (BoatTypes)getPersistence(BoatTypes.class, "boattypes",
                createNewIfDoesntExist, International.getString("Bootstypen"));
    }

    public Crews getCrews(boolean createNewIfDoesntExist) {
        return (Crews)getPersistence(Crews.class, "crews",
                createNewIfDoesntExist, International.getString("Mannschaften"));
    }

    public BoatStatus getBoatStatus(boolean createNewIfDoesntExist) {
        return (BoatStatus)getPersistence(BoatStatus.class, "boatstatus",
                createNewIfDoesntExist, International.getString("Bootsstatus"));
    }

    public BoatReservations getBoatReservations(boolean createNewIfDoesntExist) {
        return (BoatReservations)getPersistence(BoatReservations.class, "boatreservations",
                createNewIfDoesntExist, International.getString("Bootsreservierungen"));
    }

    public BoatDamages getBoatDamages(boolean createNewIfDoesntExist) {
        return (BoatDamages)getPersistence(BoatDamages.class, "boatdamages",
                createNewIfDoesntExist, International.getString("Bootsschäden"));
    }

    public Destinations getDestinations(boolean createNewIfDoesntExist) {
        return (Destinations)getPersistence(Destinations.class, "destinations",
                createNewIfDoesntExist, International.getString("Ziele"));
    }

    public Waters getWaters(boolean createNewIfDoesntExist) {
        return (Waters)getPersistence(Waters.class, "waters",
                createNewIfDoesntExist, International.getString("Gewässer"));
    }

    public void setProjectDescription(String description) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setDescription(description);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    // set the storageType for this project's content
    public void setProjectStorageType(int storageType) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setStorageType(storageType);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setAdminName(String adminName) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setAdminName(adminName);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setAdminEmail(String adminEmail) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setAdminEmail(adminEmail);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setClubName(String clubName) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setClubName(clubName);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubAddressStreet(String street) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setAddressStreet(street);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubAddressCity(String city) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setAddressCity(city);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubAreaId(int areaId) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setAreaId(areaId);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }


    // set the storageLocation for this project's content
    public void setProjectStorageLocation(String storageLocation) {
        if (getProjectStorageType() == IDataAccess.TYPE_FILE_XML) {
            // for file-based projects: storageLocation of content is always relative to this project file!
            storageLocation = null;
        }
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setStorageLocation(storageLocation);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
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

    private void closePersistence(Persistence p) {
        try {
            if (p.isOpen()) {
                p.close();
            }
        } catch(Exception e) {
            Logger.log(Logger.ERROR,Logger.MSG_DATA_CLOSEFAILED,
            LogString.logstring_fileCloseFailed(persistence.toString(), p.getDescription(), e.toString()));
        }
    }

    public void closeAllStorageObjects() throws Exception {
        // close all of this project's storage objects
        Set<String> keys = persistence.keySet();
        for (String key: keys) {
            closePersistence(persistence.get(key));
        }
        // close the project storage object itself
        closePersistence(this);
    }

}
