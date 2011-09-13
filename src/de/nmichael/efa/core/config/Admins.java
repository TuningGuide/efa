/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.DataTypePasswordHashed;

// @i18n complete

public class Admins extends StorageObject {

    public static final String DATATYPE = "efa2admins";

    public static final String SUPERADMIN = "admin";

    public Admins(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, null, null, storageObjectName, DATATYPE, International.getString("Administratoren"));
        AdminRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public Admins() {
        super(IDataAccess.TYPE_FILE_XML, Daten.efaCfgDirectory, null, null, "admins", DATATYPE, International.getString("Administratoren"));
        AdminRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new AdminRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public AdminRecord createAdminRecord(String name, String password) {
        AdminRecord r = new AdminRecord(this, MetaData.getMetaData(DATATYPE));
        r.setName(name);
        r.setPassword(password);
        r.makeSurePermissionsAreCorrect();
        r.setAllowedChangePassword(true);
        return r;
    }

    public AdminRecord getAdmin(String name) {
        try {
            AdminRecord r = (AdminRecord)data().get(AdminRecord.getKey(name));
            if (r != null) {
                r.makeSurePermissionsAreCorrect();
            }
            return r;
        } catch(Exception e) {
            return null;
        }
    }

    public AdminRecord login(String name, String password) {
        if (name == null || password == null) {
            return null;
        }
        AdminRecord admin = getAdmin(name);
        if (admin == null || admin.getPassword() == null) {
            return null;
        }
        if (admin.getPassword().equals(new DataTypePasswordHashed(password))) {
            return admin;
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        AdminRecord ar = (AdminRecord) record;
        if (add || update) {
            if (ar.getName() == null || ar.getName().trim().length() == 0) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getMessage("Das Feld '{field}' darf nicht leer sein.", AdminRecord.NAME),
                        Thread.currentThread().getStackTrace());
            }
            if (ar.getPassword() == null || !ar.getPassword().isSet()) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getMessage("Das Feld '{field}' darf nicht leer sein.", AdminRecord.PASSWORD),
                        Thread.currentThread().getStackTrace());
            }
            ar.makeSurePermissionsAreCorrect();
        }
        if (delete) {
            if (ar.getName() != null && ar.getName().equals(SUPERADMIN)) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getString("Dieser Datensatz kann nicht gelöscht werden."),
                        Thread.currentThread().getStackTrace());
            }
        }
    }

}
