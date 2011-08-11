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
import java.util.*;

// @i18n complete

public class Persons extends Persistence {

    public static final String DATATYPE = "efa2persons";
    public PersonRecord staticPersonRecord;

    public Persons(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Personen"));
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

}
