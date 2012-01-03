/* Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.ex.EfaModifyException;
import de.nmichael.efa.util.Dialog;
import java.util.*;
import java.io.*;

// @i18n complete

public class Project extends StorageObject {

    public static final String DATATYPE = "efa2project";

    public static final String STORAGEOBJECT_AUTOINCREMENT        = "autoincrement";
    public static final String STORAGEOBJECT_SESSIONGROUPS        = "sessiongroups";
    public static final String STORAGEOBJECT_PERSONS              = "persons";
    public static final String STORAGEOBJECT_STATUS               = "status";
    public static final String STORAGEOBJECT_GROUPS               = "groups";
    public static final String STORAGEOBJECT_FAHRTENABZEICHEN     = "fahrtenabzeichen";
    public static final String STORAGEOBJECT_BOATS                = "boats";
    public static final String STORAGEOBJECT_CREWS                = "crews";
    public static final String STORAGEOBJECT_BOATSTATUS           = "boatstatus";
    public static final String STORAGEOBJECT_BOATRESERVATIONS     = "boatreservations";
    public static final String STORAGEOBJECT_BOATDAMAGES          = "boatdamages";
    public static final String STORAGEOBJECT_DESTINATIONS         = "destinations";
    public static final String STORAGEOBJECT_WATERS               = "waters";
    public static final String STORAGEOBJECT_STATISTICS           = "statistics";
    public static final String STORAGEOBJECT_MESSAGES             = "messages";

    private Hashtable<String,StorageObject> persistenceCache = new Hashtable<String,StorageObject>();
    protected IDataAccess remoteDataAccess; // used for ClubRecord and LogbookRecord, if TYPE_EFA_REMOTE
    private volatile boolean _inOpeningProject = false;
    private volatile boolean _inDeleteProject = false;

    // Note: storageType and storageLocation are only type and location for the project file itself
    // (which is always being stored in the file system). The storageType and storageLocation for
    // the project's content may differ.
    public Project(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, null, null, storageObjectName, DATATYPE, International.getString("Projekt"));
        ProjectRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public Project(String projectName) {
        super(IDataAccess.TYPE_FILE_XML, Daten.efaDataDirectory, null, null, projectName, DATATYPE, International.getString("Projekt"));
        ProjectRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public static boolean openProject(String projectName) {
        return openProject(new Project(projectName), projectName);
    }

    public static boolean openProject(Project p, String projectName) {
        try {
            p._inOpeningProject = true;
            p.open(false);
            Daten.project = p;
            p.openAllData();
            if (p.getProjectStorageType() == IDataAccess.TYPE_FILE_XML) {
                (new Audit(p)).start();
            }
            if (p.getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE) {
                p.remoteDataAccess = DataAccess.createDataAccess(p, IDataAccess.TYPE_EFA_REMOTE,
                        p.getProjectStorageLocation(), p.getProjectStorageUsername(), p.getProjectStoragePassword(),
                        p.getProjectRemoteProjectName(), p.dataAccess.getStorageObjectType(), p.dataAccess.getStorageObjectDescription());
                p.remoteDataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
                // since login into remote data is lazy, we should retrieve a project record here
                // to make the login happen. It's important to chose a record which is a remote
                // record, i.e. *not* the project record itself. Therefore we select the config
                // record.
                p.getConfigRecord();
            }
            p._inOpeningProject = false;
        } catch (Exception ee) {
            Logger.log(ee);
            Dialog.error(LogString.logstring_fileOpenFailed(projectName, International.getString("Projekt"), ee.toString()));
            Daten.project = null;
            return false;
        }
        return true;
    }

    public boolean openAllData() {
        if (getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE) {
            // in order to speed up initial login to a remote project,
            // we will only open the neccesary files on demand
            return true;
        }
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
            getStatistics(true);
            getMessages(true);
            return true;
        } catch(Exception e) {
            Logger.log(e);
            return false;
        }
    }

    public boolean isInOpeningProject() {
        return _inOpeningProject;
    }

    public boolean deleteProject() {
        // we need to cache this, later it's gone...
        String projectName = getProjectName();
        String projectDir = getProjectStorageLocation();
        
        try {
            // make sure that persistenceCache is filled properly
            try {
                openAllData();
                String[] logbookNames = getAllLogbookNames();
                for (String logbookName : logbookNames) {
                    getLogbook(logbookName, false);
                }
            } catch (Exception eignore) {
                Logger.logdebug(eignore);
            }

            setPreModifyRecordCallbackEnabled(false);
            _inDeleteProject = true;
            if (getProjectStorageType() == IDataAccess.TYPE_FILE_XML) {
                String[] keys = persistenceCache.keySet().toArray(new String[0]);
                for (String key : keys) {
                    StorageObject p = persistenceCache.get(key);
                    try {
                        p.data().deleteStorageObject();
                    } catch(Exception eignore) {
                        Logger.logdebug(eignore);
                        try {
                            (new File(((DataFile)p.data()).getFilename())).delete();
                        } catch(Exception eignore2) {}
                    }
                }
            }
            
            data().deleteStorageObject();
            (new File(projectDir)).delete(); // delete project directory
            if ((new File(projectDir)).exists()) {
                Dialog.error(International.getMessage("Das Projekt konnte nicht vollständig gelöscht werden. Es befinden sich noch Daten in {directory}.",
                        projectDir));
            }
        } catch(Exception e) {
            _inDeleteProject = false;
            Logger.log(e);
            Dialog.error(LogString.logstring_fileDeletionFailed(projectName, International.getString("Projekt"), e.toString()));
            return false;
        }
        _inDeleteProject = false;
        return true;
    }

    public Vector<StorageObject> getAllDataAndLogbooks() {
        Vector<StorageObject> data = new Vector<StorageObject>();
        data.add(getAutoIncrement(false));
        data.add(getSessionGroups(false));
        data.add(getPersons(false));
        data.add(getStatus(false));
        data.add(getGroups(false));
        data.add(getFahrtenabzeichen(false));
        data.add(getBoats(false));
        data.add(getCrews(false));
        data.add(getBoatStatus(false));
        data.add(getBoatReservations(false));
        data.add(getBoatDamages(false));
        data.add(getDestinations(false));
        data.add(getWaters(false));
        data.add(getStatistics(false));
        data.add(getMessages(false));
        String[] logbookNames = getAllLogbookNames();
        for (int i=0; logbookNames != null && i<logbookNames.length; i++) {
            data.add(getLogbook(logbookNames[i], false));
        }
        return data;
    }

    public static ProjectRecord createNewRecordFromStatic(String type) {
        if (MetaData.getMetaData(DATATYPE) == null) {
            ProjectRecord.initialize();
        }
        return new ProjectRecord(null, MetaData.getMetaData(DATATYPE), type);
    }

    public DataRecord createNewRecord() {
        return new ProjectRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public ProjectRecord createProjectRecord(String type, String logbookName) {
        ProjectRecord p = new ProjectRecord(this, MetaData.getMetaData(DATATYPE), type);
        if (type.equals(ProjectRecord.TYPE_LOGBOOK)) {
            p.setLogbookName(logbookName);
        }
        return p;
    }

    public ProjectRecord createNewLogbookRecord(String logbookName) {
        return createProjectRecord(ProjectRecord.TYPE_LOGBOOK, logbookName);
    }

    public IDataAccess getMyDataAccess(String recordType) {
        if (recordType.endsWith(ProjectRecord.TYPE_CLUB) ||
            recordType.endsWith(ProjectRecord.TYPE_LOGBOOK) ||
            recordType.endsWith(ProjectRecord.TYPE_CONFIG)) {
            if (getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE) {
                return (remoteDataAccess != null ? remoteDataAccess : dataAccess);
            } else {
                return dataAccess;
            }
        } else {
            return dataAccess;
        }

    }

    public IDataAccess getRemoteDataAccess() {
        return remoteDataAccess;
    }

    public AdminRecord getRemoteAdmin() {
        if (getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE
            && getRemoteDataAccess() != null) {
            return ((RemoteEfaClient)getRemoteDataAccess()).getAdminRecord();
        }
        return null;
    }

    public boolean deleteLogbookRecord(String logbookName) {
        try {
            getMyDataAccess(ProjectRecord.TYPE_LOGBOOK).delete(createProjectRecord(ProjectRecord.TYPE_LOGBOOK, logbookName).getKey());
        } catch(Exception e) {
            Logger.logdebug(e);
            if (e instanceof EfaModifyException) {
                ((EfaModifyException)e).displayMessage();
            }
            return false;
        }
        return true;
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
            rec = (ProjectRecord)createNewRecord();
            rec.setType(ProjectRecord.TYPE_CONFIG);
            dataAccess.add(rec);
        } catch(Exception e) {
            Logger.log(e);
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

    public DataKey getConfigRecordKey() {
        return ProjectRecord.getDataKey(ProjectRecord.TYPE_CONFIG, null);
    }

    public ProjectRecord getRecord(DataKey k) {
        try {
            return (ProjectRecord)getMyDataAccess((String)k.getKeyPart1()).get(k);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public ProjectRecord getProjectRecord() {
        ProjectRecord r = getRecord(getProjectRecordKey());
        if (r == null && isOpen()) {
            r = (ProjectRecord)createNewRecord();
            r.setType(ProjectRecord.TYPE_PROJECT);
            try {
                getMyDataAccess(ProjectRecord.TYPE_PROJECT).add(r);
            } catch(Exception e) {
                Logger.log(e);
            }
        }
        return r;
    }

    public ProjectRecord getClubRecord() {
        ProjectRecord r = getRecord(getClubRecordKey());
        if (r == null && isOpen()) {
            r = (ProjectRecord)createNewRecord();
            r.setType(ProjectRecord.TYPE_CLUB);
            try {
                getMyDataAccess(ProjectRecord.TYPE_CLUB).add(r);
            } catch(Exception e) {
                Logger.logdebug(e); // happens for remote projects which aren't yet open
            }
        }
        return r;
    }

    public ProjectRecord getLoogbookRecord(String logbookName) {
        return getRecord(getLoogbookRecordKey(logbookName));
    }

    public ProjectRecord getConfigRecord() {
        ProjectRecord r = getRecord(getConfigRecordKey());
        if (r == null && isOpen()) {
            r = (ProjectRecord)createNewRecord();
            r.setType(ProjectRecord.TYPE_CONFIG);
            try {
                getMyDataAccess(ProjectRecord.TYPE_CONFIG).add(r);
            } catch(Exception e) {
                Logger.logdebug(e); // happens for remote projects which aren't yet open
            }
        }
        return r;
    }

    public void addLogbookRecord(ProjectRecord rec) throws EfaException {
        if (!rec.getType().equals(ProjectRecord.TYPE_LOGBOOK)) {
            throw new EfaException(Logger.MSG_DATA_GENERICEXCEPTION, dataAccess.getUID()+": Attempt to add a Record as a Logbook Record which is not a Logbook Record", Thread.currentThread().getStackTrace());
        }
        getMyDataAccess(ProjectRecord.TYPE_LOGBOOK).add(rec);
    }

    private void closePersistence(StorageObject p) {
        try {
            // It's ok to just close the storage object; if it wasn't open at all, close will do nothing
            p.close();
        } catch(Exception e) {
            Logger.log(Logger.ERROR,Logger.MSG_DATA_CLOSEFAILED,
            LogString.logstring_fileCloseFailed(persistenceCache.toString(), p.getDescription(), e.toString()));
            Logger.log(e);
        }
    }

    public void closeAllStorageObjects() throws Exception {
        // close all of this project's storage objects
        Set<String> keys = persistenceCache.keySet();
        for (String key: keys) {
            closePersistence(persistenceCache.get(key));
        }
        // close the project storage object itself
        closePersistence(this);
    }

    private String getPersistenceCacheKey(String storageObjectName, String storageObjectType) {
        return storageObjectName + "." + storageObjectType;
    }

    private StorageObject getPersistence(Class c, String storageObjectName, String storageObjectType, boolean createNewIfDoesntExist, String description) {
        return getPersistence(c, storageObjectName, storageObjectType, createNewIfDoesntExist, description, false);
    }

    private StorageObject getPersistence(Class c, String storageObjectName, String storageObjectType, boolean createNewIfDoesntExist, String description, boolean silent) {
        if (_inDeleteProject) {
            return null;
        }
        StorageObject p = null;
        try {
            String key = getPersistenceCacheKey(storageObjectName, storageObjectType);
            p = persistenceCache.get(key);
            if (p != null) {
                return p; // fast path (would happen anyhow a few lines further down, but let's optimize for the most frequent use-case
            }
            if (p == null) {
                if (c == null) {
                    if (storageObjectType.equals(AutoIncrement.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_AUTOINCREMENT)) {
                        c = AutoIncrement.class;
                    }
                    if (storageObjectType.equals(SessionGroups.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_SESSIONGROUPS)) {
                        c = SessionGroups.class;
                    }
                    if (storageObjectType.equals(Persons.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_PERSONS)) {
                        c = Persons.class;
                    }
                    if (storageObjectType.equals(Status.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_STATUS)) {
                        c = Status.class;
                    }
                    if (storageObjectType.equals(Groups.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_GROUPS)) {
                        c = Groups.class;
                    }
                    if (storageObjectType.equals(Fahrtenabzeichen.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_FAHRTENABZEICHEN)) {
                        c = Fahrtenabzeichen.class;
                    }
                    if (storageObjectType.equals(Boats.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_BOATS)) {
                        c = Boats.class;
                    }
                    if (storageObjectType.equals(Crews.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_CREWS)) {
                        c = Crews.class;
                    }
                    if (storageObjectType.equals(BoatStatus.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_BOATSTATUS)) {
                        c = BoatStatus.class;
                    }
                    if (storageObjectType.equals(BoatReservations.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_BOATRESERVATIONS)) {
                        c = BoatReservations.class;
                    }
                    if (storageObjectType.equals(BoatDamages.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_BOATDAMAGES)) {
                        c = BoatDamages.class;
                    }
                    if (storageObjectType.equals(Destinations.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_DESTINATIONS)) {
                        c = Destinations.class;
                    }
                    if (storageObjectType.equals(Waters.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_WATERS)) {
                        c = Waters.class;
                    }
                    if (storageObjectType.equals(Statistics.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_STATISTICS)) {
                        c = Statistics.class;
                    }
                    if (storageObjectType.equals(Messages.DATATYPE) && storageObjectName.equals(STORAGEOBJECT_MESSAGES)) {
                        c = Messages.class;
                    }
                    if (storageObjectType.equals(Logbook.DATATYPE)) {
                        c = Logbook.class;
                    }
                }
                if (c == null) {
                    return null;
                }
                p = (StorageObject)c.getConstructor(
                        int.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class
                        ).newInstance(
                                      getProjectStorageType(),
                                      getProjectStorageLocation(),
                                      getProjectStorageUsername(),
                                      getProjectStoragePassword(),
                                      storageObjectName);
                p.setProject(this);
            }
            if (!p.isOpen()) {
                p.open(createNewIfDoesntExist);
            }
            if (p.isOpen()) {
                persistenceCache.put(key, p);
            }
            // we only have to do this in the slow path (usually when a new persistence object is created which hasn't been there before)
            p.data().setPreModifyRecordCallbackEnabled(data().isPreModifyRecordCallbackEnabled());
        } catch(Exception e) {
            if (!silent) {
                Logger.log(Logger.ERROR,Logger.MSG_DATA_OPENFAILED,
                        LogString.logstring_fileOpenFailed( (p != null ? p.toString(): "<?>"), description, e.toString()));
                if (getProjectStorageType() != IDataAccess.TYPE_EFA_REMOTE) {
                    Logger.log(e);
                }
            }
            return null;
        }
        return p;
    }

    public StorageObject getStorageObject(String storageObjectName, String storageObjectType, boolean createNewIfDoesntExist) {
        if (storageObjectName.equals(getProjectName()) && storageObjectType.equals(DATATYPE)) {
            return this;
        }
        return this.getPersistence(null, storageObjectName, storageObjectType, createNewIfDoesntExist, "Remote Request", true);
    }

    public Logbook getLogbook(String logbookName, boolean createNewIfDoesntExist) {
        ProjectRecord rec = getLoogbookRecord(logbookName);
        if (rec == null) {
            return null;
        }
        Logbook logbook = (Logbook)getPersistence(Logbook.class, logbookName, Logbook.DATATYPE,
                createNewIfDoesntExist, International.getString("Fahrtenbuch"));
        if (logbook != null) {
            logbook.setName(logbookName);
            logbook.setProjectRecord(rec);
        }
        return logbook;
    }

    public String[] getAllLogbookNames() {
        try {
            IDataAccess myAccess = getMyDataAccess(ProjectRecord.TYPE_LOGBOOK);
            if (myAccess == null) {
                return null; // happens for remote projects
            }
            DataKeyIterator it = myAccess.getStaticIterator();
            ArrayList<String> a = new ArrayList<String>();
            DataKey k = it.getFirst();
            while (k != null) {
                ProjectRecord r = (ProjectRecord)getMyDataAccess(ProjectRecord.TYPE_LOGBOOK).get(k);
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
        return (AutoIncrement)getPersistence(AutoIncrement.class, STORAGEOBJECT_AUTOINCREMENT, AutoIncrement.DATATYPE,
                createNewIfDoesntExist, "AutoIncrement");
    }

    public SessionGroups getSessionGroups(boolean createNewIfDoesntExist) {
        return (SessionGroups)getPersistence(SessionGroups.class, STORAGEOBJECT_SESSIONGROUPS, SessionGroups.DATATYPE,
                createNewIfDoesntExist, International.getString("Fahrtengruppen"));
    }

    public Persons getPersons(boolean createNewIfDoesntExist) {
        return (Persons)getPersistence(Persons.class, STORAGEOBJECT_PERSONS, Persons.DATATYPE,
                createNewIfDoesntExist, International.getString("Personen"));
    }

    public Status getStatus(boolean createNewIfDoesntExist) {
        return (Status)getPersistence(Status.class, STORAGEOBJECT_STATUS, Status.DATATYPE,
                createNewIfDoesntExist, International.getString("Status"));
    }

    public Groups getGroups(boolean createNewIfDoesntExist) {
        return (Groups)getPersistence(Groups.class, STORAGEOBJECT_GROUPS, Groups.DATATYPE,
                createNewIfDoesntExist, International.getString("Gruppen"));
    }

    public Fahrtenabzeichen getFahrtenabzeichen(boolean createNewIfDoesntExist) {
        return (Fahrtenabzeichen)getPersistence(Fahrtenabzeichen.class, STORAGEOBJECT_FAHRTENABZEICHEN, Fahrtenabzeichen.DATATYPE,
                createNewIfDoesntExist, International.onlyFor("Fahrtenabzeichen","de"));
    }

    public Boats getBoats(boolean createNewIfDoesntExist) {
        return (Boats)getPersistence(Boats.class, STORAGEOBJECT_BOATS, Boats.DATATYPE,
                createNewIfDoesntExist, International.getString("Boote"));
    }

   public Crews getCrews(boolean createNewIfDoesntExist) {
        return (Crews)getPersistence(Crews.class, STORAGEOBJECT_CREWS, Crews.DATATYPE,
                createNewIfDoesntExist, International.getString("Mannschaften"));
    }

    public BoatStatus getBoatStatus(boolean createNewIfDoesntExist) {
        return (BoatStatus)getPersistence(BoatStatus.class, STORAGEOBJECT_BOATSTATUS, BoatStatus.DATATYPE,
                createNewIfDoesntExist, International.getString("Bootsstatus"));
    }

    public BoatReservations getBoatReservations(boolean createNewIfDoesntExist) {
        return (BoatReservations)getPersistence(BoatReservations.class, STORAGEOBJECT_BOATRESERVATIONS, BoatReservations.DATATYPE,
                createNewIfDoesntExist, International.getString("Bootsreservierungen"));
    }

    public BoatDamages getBoatDamages(boolean createNewIfDoesntExist) {
        return (BoatDamages)getPersistence(BoatDamages.class, STORAGEOBJECT_BOATDAMAGES, BoatDamages.DATATYPE,
                createNewIfDoesntExist, International.getString("Bootsschäden"));
    }

    public Destinations getDestinations(boolean createNewIfDoesntExist) {
        return (Destinations)getPersistence(Destinations.class, STORAGEOBJECT_DESTINATIONS, Destinations.DATATYPE,
                createNewIfDoesntExist, International.getString("Ziele"));
    }

    public Waters getWaters(boolean createNewIfDoesntExist) {
        return (Waters)getPersistence(Waters.class, STORAGEOBJECT_WATERS, Waters.DATATYPE,
                createNewIfDoesntExist, International.getString("Gewässer"));
    }

    public Statistics getStatistics(boolean createNewIfDoesntExist) {
        return (Statistics)getPersistence(Statistics.class, STORAGEOBJECT_STATISTICS, Statistics.DATATYPE,
                createNewIfDoesntExist, International.getString("Statistiken"));
    }

    public Messages getMessages(boolean createNewIfDoesntExist) {
        return (Messages)getPersistence(Messages.class, STORAGEOBJECT_MESSAGES, Messages.DATATYPE,
                createNewIfDoesntExist, International.getString("Nachrichten"));
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

    public void setAutoNewLogbookDate(DataTypeDate date) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CONFIG);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getConfigRecordKey());
            ProjectRecord r = getConfigRecord();
            r.setAutoNewLogbookDate(date);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }

    public void setAutoNewLogbookName(String name) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CONFIG);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getConfigRecordKey());
            ProjectRecord r = getConfigRecord();
            r.setAutoNewLogbookName(name);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }

    public void setClubName(String clubName) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setClubName(clubName);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubAddressStreet(String street) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setAddressStreet(street);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubAddressCity(String city) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setAddressCity(city);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubRegionalAssociationName(String name) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setRegionalAssociationName(name);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubRegionalAssociationMemberNo(String memberNo) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setRegionalAssociationMemberNo(memberNo);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubRegionalAssociationLogin(String login) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setRegionalAssociationLogin(login);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubGlobalAssociationName(String name) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setGlobalAssociationName(name);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubGlobalAssociationMemberNo(String memberNo) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setGlobalAssociationMemberNo(memberNo);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubGlobalAssociationLogin(String login) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setGlobalAssociationLogin(login);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubMemberOfDRV(boolean member) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setMemberOfDRV(member);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubMemberOfSRV(boolean member) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setMemberOfSRV(member);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubMemberOfADH(boolean member) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setMemberOfADH(member);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }
    public void setClubAreaId(int areaId) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setAreaId(areaId);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }

    public void setClubKanuEfbUsername(String username) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setKanuEfbUsername(username);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }

    public void setClubKanuEfbPassword(String password) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setKanuEfbPassword(password);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
        }
    }

    public void setClubKanuEfbLastSync(long lastSync) {
        long l = 0;
        IDataAccess access = getMyDataAccess(ProjectRecord.TYPE_CLUB);
        if (access == null) {
            return;
        }
        try {
            l = access.acquireLocalLock(getClubRecordKey());
            ProjectRecord r = getClubRecord();
            r.setKanuEfbLastSync(lastSync);
            access.update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            access.releaseLocalLock(l);
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

    public void setProjectStorageUsername(String storageUsername) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setStorageUsername(storageUsername);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setProjectStoragePassword(String storagePassword) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setStoragePassword(storagePassword);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setProjectRemoteProjectName(String projectName) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setRemoteProjectName(projectName);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setProjectEfaOnlineConnect(boolean connectThroughEfaOnline) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setEfaOnlineConnect(connectThroughEfaOnline);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setProjectEfaOnlineUsername(String username) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setEfaOnlineUsername(username);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public void setProjectEfaOnlinePassword(String password) {
        long l = 0;
        try {
            l = data().acquireLocalLock(getProjectRecordKey());
            ProjectRecord r = getProjectRecord();
            r.setEfaOnlinePassword(password);
            data().update(r, l);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            data().releaseLocalLock(l);
        }
    }

    public String getProjectName() {
        try {
            return getProjectRecord().getProjectName();
        } catch(Exception e) {
            // can happen while opening a remote project
            return "";
        }
    }

    public String getProjectDescription() {
        return getProjectRecord().getDescription();
    }

    // get the storageType for this project's content
    public int getProjectStorageType() {
        return getProjectRecord().getStorageType();
    }

    public String getProjectStorageTypeTypeString() {
        switch(getProjectStorageType()) {
            case IDataAccess.TYPE_FILE_XML:
                return IDataAccess.TYPESTRING_FILE_XML;
            case IDataAccess.TYPE_EFA_REMOTE:
                return IDataAccess.TYPESTRING_EFA_REMOTE;
            case IDataAccess.TYPE_DB_SQL:
                return IDataAccess.TYPESTRING_DB_SQL;
        }
        return null;
    }

    // get the storageLocation for this project's content
    public String getProjectStorageLocation() {
        if (getProjectStorageType() == IDataAccess.TYPE_FILE_XML) {
            // for file-based projects: storageLocation of content is always relative to this project file!
            return dataAccess.getStorageLocation() + getProjectName() + Daten.fileSep;
        }
        if (getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE &&
            getProjectEfaOnlineConnect()) {
            String location = EfaOnlineClient.getRemoteAddress(getProjectEfaOnlineUsername(), getProjectEfaOnlinePassword());
            if (location != null) {
                return location;
            }
        }
        return getProjectRecord().getStorageLocation();
    }

    public String getProjectStorageUsername() {
        return getProjectRecord().getStorageUsername();
    }

    public String getProjectStoragePassword() {
        return getProjectRecord().getStoragePassword();
    }

    public String getProjectRemoteProjectName() {
        return getProjectRecord().getRemoteProjectName();
    }

    public boolean getProjectEfaOnlineConnect() {
        return getProjectRecord().getEfaOnlineConnect();
    }

    public String getProjectEfaOnlineUsername() {
        return getProjectRecord().getEfaOnlineUsername();
    }

    public String getProjectEfaOnlinePassword() {
        return getProjectRecord().getEfaOnlinePassword();
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

    public String getClubKanuEfbUsername() {
        return getClubRecord().getKanuEfbUsername();
    }
    
    public String getClubKanuEfbPassword() {
        return getClubRecord().getKanuEfbPassword();
    }

    public long getClubKanuEfbLastSync() {
        return getClubRecord().getKanuEfbLastSync();
    }

    public DataTypeDate getAutoNewLogbookDate() {
        return getConfigRecord().getAutoNewLogbookDate();
    }

    public String getAutoNewLogbookName() {
        return getConfigRecord().getAutoNewLogbookName();
    }


    public void setPreModifyRecordCallbackEnabled(boolean enabled) {
        this.data().setPreModifyRecordCallbackEnabled(enabled);
        Set<String> keys = persistenceCache.keySet();
        for (String key: keys) {
            persistenceCache.get(key).data().setPreModifyRecordCallbackEnabled(enabled);
        }
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, ProjectRecord.TYPE);
            assertUnique(record, ProjectRecord.PROJECTNAME);
            assertUnique(record, ProjectRecord.LOGBOOKNAME);
            if (((ProjectRecord) record).getType().equals(ProjectRecord.TYPE_CONFIG)) {
                ProjectRecord r = (ProjectRecord) record;
                String lName = r.getAutoNewLogbookName();
                if (lName != null && lName.length() > 0 && getLoogbookRecord(lName) == null) {
                    throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                            "Logbook " + lName + " not found!",
                            Thread.currentThread().getStackTrace());
                }
            }
        }
        if (delete) {
            if (((ProjectRecord) record).getType().equals(ProjectRecord.TYPE_LOGBOOK)) {
                ProjectRecord r = (ProjectRecord) record;
                String lName = getAutoNewLogbookName();
                if (lName != null && lName.length() > 0 && r.getLogbookName().equals(lName)) {
            throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                    International.getMessage("Der Datensatz kann nicht gelöscht werden, da er noch von {listtype} '{record}' genutzt wird.",
                    International.getString("Fahrtenbuchwechsel"), lName),
                    Thread.currentThread().getStackTrace());
                }
            }
        }
    }

}
