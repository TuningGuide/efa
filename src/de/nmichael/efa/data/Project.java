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
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.util.Dialog;
import java.util.*;

// @i18n complete

public class Project extends Persistence {


    public static final String DATATYPE = "efa2project";
    private Hashtable<String,Persistence> persistence = new Hashtable<String,Persistence>();

    // Note: storageType and storageLocation are only type and location for the project file itself
    // (which is always being stored in the file system). The storageType and storageLocation for
    // the project's content may differ.
    public Project(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Projekt"));
        ProjectRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public Project(String projectName) {
        super(IDataAccess.TYPE_FILE_XML, Daten.efaDataDirectory, projectName, DATATYPE, International.getString("Projekt"));
        ProjectRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public static boolean openProject(String projectName) {
        try {
            Project p = new Project(projectName);
            p.open(false);
            Daten.project = p;
            p.openAllData();
        } catch (Exception ee) {
            Logger.log(ee);
            Dialog.error(LogString.logstring_fileOpenFailed(projectName, International.getString("Projekt"), ee.toString()));
            return false;
        }
        return true;
    }

    public boolean openAllData() {
        try {
            if (!isOpen()) {
                open(false);
            }
            getAutoIncrement(true);
            getSessionGroups(true);
            getPersons(true);
            getStatus(true);
            getGroups(true);
            getFahrtenabzeichen(true);
            getBoats(true);
            getCrews(true);
            getBoatStatus(true);
            getBoatReservations(true);
            getBoatDamages(true);
            getDestinations(true);
            getWaters(true);
            return true;
        } catch(Exception e) {
            Logger.logdebug(e);
            return false;
        }
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
            Logger.logdebug(e);
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
            Logger.logdebug(e);
            return null;
        }
    }

    public ProjectRecord getClubRecord() {
        try {
            return (ProjectRecord)dataAccess.get(getClubRecordKey());
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public ProjectRecord getLoogbookRecord(String logbookName) {
        try {
            return (ProjectRecord)dataAccess.get(getLoogbookRecordKey(logbookName));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public void addLogbookRecord(ProjectRecord rec) throws EfaException {
        if (!rec.getType().equals(ProjectRecord.TYPE_LOGBOOK)) {
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION, dataAccess.getUID()+": Attempt to add a Record as a Logbook Record which is not a Logbook Record", Thread.currentThread().getStackTrace());
        }
        dataAccess.add(rec);
    }

    private void closePersistence(Persistence p) {
        try {
            if (p.isOpen()) {
                p.close();
            }
        } catch(Exception e) {
            Logger.log(Logger.ERROR,Logger.MSG_DATA_CLOSEFAILED,
            LogString.logstring_fileCloseFailed(persistence.toString(), p.getDescription(), e.toString()));
            Logger.log(e);
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

    private Persistence getPersistence(Class c, String name, boolean createNewIfDoesntExist, String description) {
        Persistence p = null;
        try {
            String key = c.getCanonicalName()+":"+name;
            p = persistence.get(key);
            if (p != null && p.isOpen()) {
                return p; // fast path (would happen anyhow a few lines further down, but let's optimize for the most frequent use-case
            }
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
            Logger.log(e);
            return null;
        }
        return p;
    }

    public Logbook getLogbook(String logbookName, boolean createNewIfDoesntExist) {
        ProjectRecord rec = getLoogbookRecord(logbookName);
        if (rec == null) {
            return null;
        }
        Logbook logbook = (Logbook)getPersistence(Logbook.class, logbookName,
                createNewIfDoesntExist, International.getString("Fahrtenbuch"));
        if (logbook != null) {
            logbook.setName(logbookName);
            logbook.setProjectRecord(rec);
        }
        return logbook;
    }

    public String[] getAllLogbookNames() {
        try {
            DataKeyIterator it = data().getStaticIterator();
            ArrayList<String> a = new ArrayList<String>();
            DataKey k = it.getFirst();
            while (k != null) {
                ProjectRecord r = (ProjectRecord)data().get(k);
                if (r != null && r.getType() != null &&
                        r.getType().equals(ProjectRecord.TYPE_LOGBOOK) &&
                        r.getLogbookName() != null && r.getLogbookName().length() > 0) {
                    a.add(r.getLogbookName());
                }
                k = it.getNext();
            }
            return a.toArray(new String[0]);
        } catch (Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public AutoIncrement getAutoIncrement(boolean createNewIfDoesntExist) {
        return (AutoIncrement)getPersistence(AutoIncrement.class, "autoincrement",
                createNewIfDoesntExist, "AutoIncrement");
    }

    public SessionGroups getSessionGroups(boolean createNewIfDoesntExist) {
        return (SessionGroups)getPersistence(SessionGroups.class, "sessiongroups",
                createNewIfDoesntExist, International.getString("Fahrtengruppen"));
    }

    public Persons getPersons(boolean createNewIfDoesntExist) {
        return (Persons)getPersistence(Persons.class, "persons",
                createNewIfDoesntExist, International.getString("Personen"));
    }

    public Status getStatus(boolean createNewIfDoesntExist) {
        return (Status)getPersistence(Status.class, "status",
                createNewIfDoesntExist, International.getString("Status"));
    }

    public Groups getGroups(boolean createNewIfDoesntExist) {
        return (Groups)getPersistence(Groups.class, "groups",
                createNewIfDoesntExist, International.getString("Gruppen"));
    }

    public Fahrtenabzeichen getFahrtenabzeichen(boolean createNewIfDoesntExist) {
        return (Fahrtenabzeichen)getPersistence(Fahrtenabzeichen.class, "fahrtenabzeichen",
                createNewIfDoesntExist, International.onlyFor("Fahrtenabzeichen","de"));
    }

    public Boats getBoats(boolean createNewIfDoesntExist) {
        return (Boats)getPersistence(Boats.class, "boats",
                createNewIfDoesntExist, International.getString("Boote"));
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

    public void setCurrentLogbookEfaBase(String currentLogbook) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setCurrentLogbookEfaBase(currentLogbook);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setCurrentLogbookEfaBoathouse(String currentLogbook) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setCurrentLogbookEfaBoathouse(currentLogbook);
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
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
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
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
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
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setAddressCity(city);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubRegionalAssociationName(String name) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setRegionalAssociationName(name);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubRegionalAssociationMemberNo(String memberNo) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setRegionalAssociationMemberNo(memberNo);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubRegionalAssociationLogin(String login) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setRegionalAssociationLogin(login);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubGlobalAssociationName(String name) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setGlobalAssociationName(name);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubGlobalAssociationMemberNo(String memberNo) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setGlobalAssociationMemberNo(memberNo);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubGlobalAssociationLogin(String login) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setGlobalAssociationLogin(login);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubMemberOfDRV(boolean member) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setMemberOfDRV(member);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubMemberOfSRV(boolean member) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setMemberOfSRV(member);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }
    public void setClubMemberOfADH(boolean member) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setMemberOfADH(member);
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
            l = data().acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
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

    public String getAdminName() {
        return getProjectRecord().getAdminName();
    }

    public String getAdminEmail() {
        return getProjectRecord().getAdminEmail();
    }

    public String getCurrentLogbookEfaBase() {
        return getProjectRecord().getCurrentLogbookEfaBase();
    }

    public String getCurrentLogbookEfaBoathouse() {
        return getProjectRecord().getCurrentLogbookEfaBoathouse();
    }

    public String getClubName() {
        return getClubRecord().getClubName();
    }

    public String getClubAddressStreet() {
        return getClubRecord().getAddressStreet();
    }

    public String getClubAddressCity() {
        return getClubRecord().getAddressCity();
    }

    public String getClubRegionalAssociationName() {
        return getClubRecord().getRegionalAssociationName();
    }

    public String getClubRegionalAssociationMemberNo() {
        return getClubRecord().getRegionalAssociationMemberNo();
    }

    public String getClubRegionalAssociationLogin() {
        return getClubRecord().getRegionalAssociationLogin();
    }

    public String getClubGlobalAssociationName() {
        return getClubRecord().getGlobalAssociationName();
    }

    public String getClubGlobalAssociationMemberNo() {
        return getClubRecord().getGlobalAssociationMemberNo();
    }

    public String getClubGlobalAssociationLogin() {
        return getClubRecord().getGlobalAssociationLogin();
    }

    public boolean getClubMemberOfDRV() {
        return getClubRecord().getMemberOfDRV();
    }

    public boolean getClubMemberOfSRV() {
        return getClubRecord().getMemberOfSRV();
    }

    public boolean getClubMemberOfADH() {
        return getClubRecord().getMemberOfADH();
    }

    public int getClubAreaID() {
        return getClubRecord().getAreaId();
    }

    public Vector<DataItem> getGuiItems() {
        Vector<DataItem> v = new Vector<DataItem>();
        try {
            DataKeyIterator it = dataAccess.getStaticIterator();
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
    
    private int runAuditPersistence(Persistence p, String dataType) {
        if (p != null && p.isOpen()) {
            Logger.log(Logger.INFO, Logger.MSG_DATA_PROJECTCHECK, dataType + " open (" + p.toString() + ")");
            return 0;
        } else {
            Logger.log(Logger.ERROR, Logger.MSG_DATA_PROJECTCHECK, dataType + " not open");
            return 1;
        }
    }

    private int runAuditBoats() {
        int errors = 0;
        try {
            Boats boats = getBoats(false);
            BoatStatus boatStatus = getBoatStatus(false);
            BoatReservations boatReservations = getBoatReservations(false);
            BoatDamages boatDamages = getBoatDamages(false);

            Hashtable<UUID,Integer> boatVersions = new Hashtable<UUID,Integer>();

            DataKeyIterator it = boats.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                BoatRecord boat = (BoatRecord)boats.data().get(k);
                if (boat.getId() == null ||
                        boat.getValidFrom() < 0 || boat.getInvalidFrom() < 0 ||
                        boat.getValidFrom() >= boat.getInvalidFrom()) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"Boat Record is invalid: " + boat.toString());
                    errors++;
                }
                Integer versions = boatVersions.get(boat.getId());
                if (versions == null) {
                    boatVersions.put(boat.getId(), 1);
                } else {
                    boatVersions.put(boat.getId(), versions.intValue() + 1);
                }

                BoatStatusRecord status = boatStatus.getBoatStatus(boat.getId());
                if (status == null) {
                    Logger.log(Logger.WARNING,Logger.MSG_DATA_PROJECTCHECK,"No Boat Status found for Boat "+boat.getQualifiedName()+": " + boat.toString());
                }

                k = it.getNext();
            }

            it = boatStatus.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatStatusRecord status = (BoatStatusRecord)boatStatus.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(status.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"No Boat found for Boat Status: " + status.toString());
                    errors++;
                }
                k = it.getNext();
            }

            it = boatReservations.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatReservationRecord reservation = (BoatReservationRecord)boatReservations.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(reservation.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"No Boat found for Boat Reservation: " + reservation.toString());
                    errors++;
                }
                k = it.getNext();
            }

            it = boatDamages.data().getStaticIterator();
            k = it.getFirst();
            while (k != null) {
                BoatDamageRecord damage = (BoatDamageRecord)boatDamages.data().get(k);
                DataRecord[] boat = boats.data().getValidAny(BoatRecord.getKey(damage.getBoatId(), 0));
                if (boat == null || boat.length == 0) {
                    Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"No Boat found for Boat Damage: " + damage.toString());
                    errors++;
                }
                k = it.getNext();
            }

            return errors;
        } catch (Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"runAuditBoats() Caught Exception: " + e.toString());
            return ++errors;
        }
    }

    public boolean runAudit() {
        Logger.log(Logger.INFO,Logger.MSG_DATA_PROJECTCHECK,"Starting Project Audit for Project: " + getProjectName());
        int errors = 0;
        try {
            errors += runAuditPersistence(getSessionGroups(false), SessionGroups.DATATYPE);
            errors += runAuditPersistence(getPersons(false), Persons.DATATYPE);
            errors += runAuditPersistence(getStatus(false), Status.DATATYPE);
            errors += runAuditPersistence(getGroups(false), Groups.DATATYPE);
            errors += runAuditPersistence(getFahrtenabzeichen(false), Fahrtenabzeichen.DATATYPE);
            errors += runAuditPersistence(getBoats(false), Boats.DATATYPE);
            errors += runAuditPersistence(getCrews(false), Crews.DATATYPE);
            errors += runAuditPersistence(getBoatStatus(false), BoatStatus.DATATYPE);
            errors += runAuditPersistence(getBoatReservations(false), BoatReservations.DATATYPE);
            errors += runAuditPersistence(getBoatDamages(false), BoatDamages.DATATYPE);
            errors += runAuditPersistence(getDestinations(false), Destinations.DATATYPE);
            errors += runAuditPersistence(getWaters(false), Waters.DATATYPE);

            errors += runAuditBoats();
        } catch(Exception e) {
            Logger.logdebug(e);
            Logger.log(Logger.ERROR,Logger.MSG_DATA_PROJECTCHECK,"runAudit() Caught Exception: " + e.toString());
        }
        Logger.log( (errors == 0 ? Logger.INFO : Logger.ERROR) ,Logger.MSG_DATA_PROJECTCHECK,"Project Audit completed with " + errors + " Errors.");
        return errors == 0;
    }

}
