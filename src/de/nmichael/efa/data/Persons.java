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

    public static final String DATATYPE = "e2persons";

    public Persons(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Personen"));
        PersonRecord.initialize();
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

    public PersonRecord getPerson(String personName, long validAt) {
        try {
            DataKey[] keys = data().getByFields(
                PersonRecord.IDX_NAME_ASSOC, PersonRecord.getValuesForIndexFromQualifiedName(personName), validAt);
            if (keys == null || keys.length < 1) {
                return null;
            }
            return (PersonRecord)data().get(keys[0]);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

}
