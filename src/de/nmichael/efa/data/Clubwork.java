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
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.ex.EfaModifyException;
import java.util.*;

// @i18n complete

public class Clubwork extends StorageObject {

    public static final String DATATYPE = "efa2clubwork";
//    public ClubworkRecord staticClubworkRecord;
    private String name;
	private ProjectRecord projectRecord;

    public Clubwork(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.getString("Vereinsarbeit"));
        ClubworkRecord.initialize();
//        staticClubworkRecord = (ClubworkRecord)createNewRecord();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new ClubworkRecord(this, MetaData.getMetaData(DATATYPE));
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public DataTypeDate getStartDate() {
    	ProjectRecord logbook = Daten.project.getLoogbookRecord(name);
        return (logbook != null ? logbook.getStartDate() : null);
    }

    public DataTypeDate getEndDate() {
    	ProjectRecord logbook = Daten.project.getLoogbookRecord(name);
        return (logbook != null ? logbook.getEndDate() : null);
    }

    public ProjectRecord getProjectRecord() {
       return this.projectRecord;
    }
    
    public void setProjectRecord(ProjectRecord r) {
        this.projectRecord = r;
    }
    
    public ClubworkRecord createClubworkRecord(UUID id) {
        ClubworkRecord r = new ClubworkRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public ClubworkRecord getClubworkRecord(UUID id, long validAt) {
        try {
            return (ClubworkRecord)data().getValidAt(ClubworkRecord.getKey(id, validAt), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public ClubworkRecord getClubworkRecord(UUID id, long earliestValidAt, long latestValidAt, long preferredValidAt) {
        try {
            return (ClubworkRecord)data().getValidNearest(ClubworkRecord.getKey(id, preferredValidAt), earliestValidAt, latestValidAt, preferredValidAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

//    // find a record being valid at the specified time
//    public ClubworkRecord getClubworkRecord(String personName, long validAt) {
//        try {
//            DataKey[] keys = data().getByFields(
//                staticClubworkRecord.getQualifiedNameFields(), staticClubworkRecord.getQualifiedNameValues(personName), validAt);
//            if (keys == null || keys.length < 1) {
//                return null;
//            }
//            for (int i=0; i<keys.length; i++) {
//            	ClubworkRecord r = (ClubworkRecord)data().get(keys[i]);
//                if (r.isValidAt(validAt)) {
//                    return r;
//                }
//            }
//            return null;
//        } catch(Exception e) {
//            Logger.logdebug(e);
//            return null;
//        }
//    }
//
//    // find any record being valid at least partially in the specified range
//    public ClubworkRecord getClubworkRecord(String personName, long validFrom, long validUntil, long preferredValidAt) {
//        try {
//            DataKey[] keys = data().getByFields(
//                staticClubworkRecord.getQualifiedNameFields(), staticClubworkRecord.getQualifiedNameValues(personName));
//            if (keys == null || keys.length < 1) {
//                return null;
//            }
//            ClubworkRecord candidate = null;
//            for (int i=0; i<keys.length; i++) {
//                ClubworkRecord r = (ClubworkRecord)data().get(keys[i]);
//                if (r != null) {
//                    if (r.isInValidityRange(validFrom, validUntil)) {
//                        candidate = r;
//                        if (preferredValidAt >= r.getValidFrom() && preferredValidAt < r.getInvalidFrom()) {
//                            return r;
//                        }
//                    }
//                }
//            }
//            return candidate;
//        } catch(Exception e) {
//            Logger.logdebug(e);
//            return null;
//        }
//    }

    public Vector<ClubworkRecord> getAllClubworkRecords(long validAt, boolean alsoDeleted, boolean alsoInvisible) {
        try {
            Vector<ClubworkRecord> v = new Vector<ClubworkRecord>();
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                ClubworkRecord r = (ClubworkRecord) data().get(k);
                if (r != null && (r.isValidAt(validAt) || (r.getDeleted() && alsoDeleted)) && (!r.getInvisible() || alsoInvisible)) {
                    v.add(r);
                }
                k = it.getNext();
            }
            return v;
        } catch (Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public boolean isClubworkRecordDeleted(UUID id) {
        try {
            DataRecord[] records = data().getValidAny(ClubworkRecord.getKey(id, -1));
            if (records != null && records.length > 0) {
                return records[0].getDeleted();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public int getNumberOfClubworkRecords(long tstmp) {
        try {
            DataKeyIterator it = dataAccess.getStaticIterator();
            DataKey k = it.getFirst();
            // actually, checking for records valid at tstmp should already
            // give us unique records, so there should be no need to use
            // a Hashtable to make sure we don't cound a person twice. But, well,
            // you never know...
            Hashtable<UUID,DataKey> uuids = new Hashtable<UUID,DataKey>();
            while (k != null) {
                ClubworkRecord p = (ClubworkRecord) dataAccess.get(k);
                if (p != null && p.isValidAt(tstmp) && !p.getDeleted()) {
                    uuids.put(p.getId(), k);
                }
                k = it.getNext();
            }
            return uuids.size();
        } catch (Exception e) {
            Logger.log(e);
            return -1;
        }
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, ClubworkRecord.ID);
            assertFieldNotEmpty(record, ClubworkRecord.PERSONID);
            assertFieldNotEmpty(record, ClubworkRecord.WORKDATE);
            assertFieldNotEmpty(record, ClubworkRecord.DESCRIPTION);
            assertFieldNotEmpty(record, ClubworkRecord.HOURS);
        }
    }

}
