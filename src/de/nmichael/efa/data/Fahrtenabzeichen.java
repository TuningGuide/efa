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

public class Fahrtenabzeichen extends StorageObject {

    public static final String DATATYPE = "efa2fahrtenabzeichen";

    public Fahrtenabzeichen(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.onlyFor("Fahrtenabzeichen","de"));
        FahrtenabzeichenRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new FahrtenabzeichenRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public FahrtenabzeichenRecord createFahrtenabzeichenRecord(UUID id) {
        FahrtenabzeichenRecord r = new FahrtenabzeichenRecord(this, MetaData.getMetaData(DATATYPE));
        r.setPersonId(id);
        return r;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, FahrtenabzeichenRecord.PERSONID);
        }
    }

}
