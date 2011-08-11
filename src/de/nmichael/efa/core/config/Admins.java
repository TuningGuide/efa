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
import java.util.*;

// @i18n complete

public class Admins extends Persistence {

    public static final String DATATYPE = "efa2admins";

    public static final String SUPERADMIN = "admin";

    public Admins(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Administratoren"));
        AdminRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public Admins() {
        super(IDataAccess.TYPE_FILE_XML, Daten.efaCfgDirectory, "admins", DATATYPE, International.getString("Administratoren"));
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
        boolean superAdmin = name != null && name.equals(SUPERADMIN);
        r.setAllowedAdministerProjectLogbook(superAdmin);
        r.setAllowedFullAccess(superAdmin);
        r.setAllowedEditLogbook(superAdmin);
        r.setAllowedExitEfa(superAdmin);
        r.setAllowedEditBoats(superAdmin);
        r.setAllowedEditBoatStatus(superAdmin);
        r.setAllowedEditBoatReservation(superAdmin);
        r.setAllowedEditBoatDamages(superAdmin);
        r.setAllowedEditPersons(superAdmin);
        r.setAllowedEditGroups(superAdmin);
        r.setAllowedEditCrews(superAdmin);
        r.setAllowedEditFahrtenabzeichen(superAdmin);
        r.setAllowedEditDestinations(superAdmin);
        r.setAllowedConfiguration(superAdmin);
        r.setAllowedEditAdmins(superAdmin);
        r.setAllowedChangePassword(superAdmin);
        r.setAllowedEditStatistics(superAdmin);
        r.setAllowedSyncKanuEfb(superAdmin);
        r.setAllowedShowLogfile(superAdmin);
        r.setAllowedLockEfa(superAdmin);
        r.setAllowedExecCommand(superAdmin);
        r.setAllowedMsgReadAdmin(superAdmin);
        r.setAllowedMsgReadBoatMaintenance(superAdmin);
        r.setAllowedMsgMarkReadAdmin(superAdmin);
        r.setAllowedMsgMarkReadBoatMaintenance(superAdmin);
        r.setAllowedMsgAutoMarkReadAdmin(superAdmin);
        r.setAllowedMsgAutoMarkReadBoatMaintenance(superAdmin);
        return r;
    }

    public AdminRecord getAdmin(String name) {
        try {
            return (AdminRecord)data().get(AdminRecord.getKey(name));
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
        if (admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

}
