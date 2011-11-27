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

import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaModifyException;
import java.util.*;

// @i18n complete

public class Persons extends StorageObject {

    public static final String DATATYPE = "efa2persons";
    public PersonRecord staticPersonRecord;

    public Persons(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.getString("Personen"));
        PersonRecord.initialize();
        staticPersonRecord = (PersonRecord)createNewRecord();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new PersonRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public PersonRecord createPersonRecord(UUID id) {
        PersonRecord r = new PersonRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public PersonRecord getPerson(UUID id, long validAt) {
        try {
            return (PersonRecord)data().getValidAt(PersonRecord.getKey(id, validAt), validAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public PersonRecord getPerson(UUID id, long earliestValidAt, long latestValidAt, long preferredValidAt) {
        try {
            return (PersonRecord)data().getValidNearest(PersonRecord.getKey(id, preferredValidAt), earliestValidAt, latestValidAt, preferredValidAt);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    // find a record being valid at the specified time
    public PersonRecord getPerson(String personName, long validAt) {
        try {
            DataKey[] keys = data().getByFields(
                staticPersonRecord.getQualifiedNameFields(), staticPersonRecord.getQualifiedNameValues(personName), validAt);
            if (keys == null || keys.length < 1) {
                return null;
            }
            return (PersonRecord)data().get(keys[0]);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    // find any record being valid at least partially in the specified range
    public PersonRecord getPerson(String personName, long validFrom, long validUntil, long preferredValidAt) {
        try {
            DataKey[] keys = data().getByFields(
                staticPersonRecord.getQualifiedNameFields(), staticPersonRecord.getQualifiedNameValues(personName));
            if (keys == null || keys.length < 1) {
                return null;
            }
            PersonRecord candidate = null;
            for (int i=0; i<keys.length; i++) {
                PersonRecord r = (PersonRecord)data().get(keys[i]);
                if (r != null) {
                    if (r.isInValidityRange(validFrom, validUntil)) {
                        candidate = r;
                        if (preferredValidAt >= r.getValidFrom() && preferredValidAt < r.getInvalidFrom()) {
                            return r;
                        }
                    }
                }
            }
            return candidate;
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public Vector<PersonRecord> getAllPersons(long validAt, boolean alsoDeleted, boolean alsoInvisible) {
        try {
            Vector<PersonRecord> v = new Vector<PersonRecord>();
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                PersonRecord r = (PersonRecord) data().get(k);
                if (r != null && r.isValidAt(validAt) && (!r.getDeleted() || alsoDeleted) && (!r.getInvisible() || alsoInvisible)) {
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

    public boolean isPersonDeleted(UUID personId) {
        try {
            DataRecord[] records = data().getValidAny(PersonRecord.getKey(personId, -1));
            if (records != null && records.length > 0) {
                return records[0].getDeleted();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public int getNumberOfMembers(long tstmp) {
        try {
            DataKeyIterator it = dataAccess.getStaticIterator();
            DataKey k = it.getFirst();
            // actually, checking for records valid at tstmp should already
            // give us unique records, so there should be no need to use
            // a Hashtable to make sure we don't cound a person twice. But, well,
            // you never know...
            Hashtable<UUID,DataKey> uuids = new Hashtable<UUID,DataKey>();
            while (k != null) {
                PersonRecord p = (PersonRecord) dataAccess.get(k);
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
            assertFieldNotEmpty(record, PersonRecord.ID);
            assertFieldNotEmpty(record, PersonRecord.FIRSTLASTNAME);
        }
        if (delete) {
            assertNotReferenced(record, getProject().getFahrtenabzeichen(false), new String[] { FahrtenabzeichenRecord.PERSONID });
            assertNotReferenced(record, getProject().getGroups(false), new String[] { GroupRecord.MEMBERIDLIST });
            assertNotReferenced(record, getProject().getCrews(false), new String[] { CrewRecord.COXID,
                                                                                     CrewRecord.CREW1ID,
                                                                                     CrewRecord.CREW2ID,
                                                                                     CrewRecord.CREW3ID,
                                                                                     CrewRecord.CREW4ID,
                                                                                     CrewRecord.CREW5ID,
                                                                                     CrewRecord.CREW6ID,
                                                                                     CrewRecord.CREW7ID,
                                                                                     CrewRecord.CREW8ID,
                                                                                     CrewRecord.CREW9ID,
                                                                                     CrewRecord.CREW10ID,
                                                                                     CrewRecord.CREW11ID,
                                                                                     CrewRecord.CREW12ID,
                                                                                     CrewRecord.CREW14ID,
                                                                                     CrewRecord.CREW11ID,
                                                                                     CrewRecord.CREW16ID,
                                                                                     CrewRecord.CREW11ID,
                                                                                     CrewRecord.CREW17ID,
                                                                                     CrewRecord.CREW18ID,
                                                                                     CrewRecord.CREW19ID,
                                                                                     CrewRecord.CREW20ID,
                                                                                     CrewRecord.CREW21ID,
                                                                                     CrewRecord.CREW22ID,
                                                                                     CrewRecord.CREW23ID,
                                                                                     CrewRecord.CREW24ID
                                                                                   }, false);
            assertNotReferenced(record, getProject().getBoatDamages(false), new String[] { BoatDamageRecord.REPORTEDBYPERSONID,
                                                                                           BoatDamageRecord.FIXEDBYPERSONID},
                                                                                           false);
            assertNotReferenced(record, getProject().getBoatReservations(false), new String[] { BoatReservationRecord.PERSONID });
            String[] logbooks = getProject().getAllLogbookNames();
            for (int i=0; logbooks != null && i<logbooks.length; i++) {
                assertNotReferenced(record, getProject().getLogbook(logbooks[i], false), new String[] {
                                                                                       LogbookRecord.COXID,
                                                                                       LogbookRecord.CREW1ID,
                                                                                       LogbookRecord.CREW2ID,
                                                                                       LogbookRecord.CREW3ID,
                                                                                       LogbookRecord.CREW4ID,
                                                                                       LogbookRecord.CREW5ID,
                                                                                       LogbookRecord.CREW6ID,
                                                                                       LogbookRecord.CREW7ID,
                                                                                       LogbookRecord.CREW8ID,
                                                                                       LogbookRecord.CREW9ID,
                                                                                       LogbookRecord.CREW10ID,
                                                                                       LogbookRecord.CREW11ID,
                                                                                       LogbookRecord.CREW12ID,
                                                                                       LogbookRecord.CREW13ID,
                                                                                       LogbookRecord.CREW14ID,
                                                                                       LogbookRecord.CREW15ID,
                                                                                       LogbookRecord.CREW16ID,
                                                                                       LogbookRecord.CREW17ID,
                                                                                       LogbookRecord.CREW18ID,
                                                                                       LogbookRecord.CREW19ID,
                                                                                       LogbookRecord.CREW20ID,
                                                                                       LogbookRecord.CREW21ID,
                                                                                       LogbookRecord.CREW22ID,
                                                                                       LogbookRecord.CREW23ID,
                                                                                       LogbookRecord.CREW24ID,
                                                                                      }, false );
            }
        }
    }

}
