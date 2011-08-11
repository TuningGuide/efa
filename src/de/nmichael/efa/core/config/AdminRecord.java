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

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.awt.GridBagConstraints;
import java.util.*;

// @i18n complete

public class AdminRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String NAME                  = "Name";
    public static final String PASSWORD              = "Password";
    public static final String EMAIL                 = "Email";
    public static final String EDITADMINS            = "EditAdmins";
    public static final String CHANGEPASSWORD        = "ChangePassword";
    public static final String FULLACCESS            = "FullAccess";
    public static final String CONFIGURATION         = "Configuration";
    public static final String ADMINPROJECTLOGBOOK   = "AdministerProjectLogbook";
    public static final String EDITLOGBOOK           = "EditLogbook";
    public static final String EDITBOATSTATUS        = "EditBoatStatus";
    public static final String EDITBOATRESERVATION   = "EditBoatReservation";
    public static final String EDITBOATDAMAGES       = "EditBoatDamages";
    public static final String EDITBOATS             = "EditBoats";
    public static final String EDITPERSONS           = "EditPersons";
    public static final String EDITDESTINATIONS      = "EditDestinations";
    public static final String EDITGROUPS            = "EditGroups";
    public static final String EDITCREWS             = "EditCrews";
    public static final String EDITFAHRTENABZEICHEN  = "EditFahrtenabzeichen";
    public static final String MSGREADADMIN          = "MsgReadAdmin";
    public static final String MSGREADBOATMAINT      = "MsgReadBoatMaintenance";
    public static final String MSGMARKREADADMIN      = "MsgMarkReadAdmin";
    public static final String MSGMARKREADBOATMAINT  = "MsgMarkReadBoatMaintenance";
    public static final String MSGAUTOREADADMIN      = "MsgAutoMarkReadAdmin";
    public static final String MSGAUTOREADBOATMAINT  = "MsgAutoMarkReadBoatMaintenance";
    public static final String EDITSTATISTICS        = "EditStatistics";
    public static final String SYNCKANUEFB           = "SyncKanuEfb";
    public static final String SHOWLOGFILE           = "ShowLogfile";
    public static final String EXITEFA               = "ExitEfa";
    public static final String LOCKEFA               = "LockEfa";
    public static final String EXECCOMMAND           = "ExecCommand";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(PASSWORD);                          t.add(IDataAccess.DATA_STRING);
        f.add(EMAIL);                             t.add(IDataAccess.DATA_STRING);
        f.add(EDITADMINS);                        t.add(IDataAccess.DATA_BOOLEAN);
        f.add(CHANGEPASSWORD);                    t.add(IDataAccess.DATA_BOOLEAN);
        f.add(FULLACCESS);                        t.add(IDataAccess.DATA_BOOLEAN);
        f.add(CONFIGURATION);                     t.add(IDataAccess.DATA_BOOLEAN);
        f.add(ADMINPROJECTLOGBOOK);               t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITLOGBOOK);                       t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITBOATSTATUS);                    t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITBOATRESERVATION);               t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITBOATDAMAGES);                   t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITBOATS);                         t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITPERSONS);                       t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITDESTINATIONS);                  t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITGROUPS);                        t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITCREWS);                         t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITFAHRTENABZEICHEN);              t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MSGREADADMIN);                      t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MSGREADBOATMAINT);                  t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MSGMARKREADADMIN);                  t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MSGMARKREADBOATMAINT);              t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MSGAUTOREADADMIN);                  t.add(IDataAccess.DATA_BOOLEAN);
        f.add(MSGAUTOREADBOATMAINT);              t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EDITSTATISTICS);                    t.add(IDataAccess.DATA_BOOLEAN);
        f.add(SYNCKANUEFB);                       t.add(IDataAccess.DATA_BOOLEAN);
        f.add(SHOWLOGFILE);                       t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EXITEFA);                           t.add(IDataAccess.DATA_BOOLEAN);
        f.add(LOCKEFA);                           t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EXECCOMMAND);                       t.add(IDataAccess.DATA_BOOLEAN);
        MetaData metaData = constructMetaData(Admins.DATATYPE, f, t, false);
        metaData.setKey(new String[] { NAME });
    }

    public AdminRecord(Admins admins, MetaData metaData) {
        super(admins, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<String,String,String>(getName(),null,null);
    }

    public static DataKey getKey(String name) {
        return new DataKey<String,String,String>(name,null,null);
    }

    protected void setName(String name) {
        setString(NAME, name);
    }
    public String getName() {
        return getString(NAME);
    }

    public void setPassword(String password) {
        setString(PASSWORD, password);
    }
    public String getPassword() {
        return getString(PASSWORD);
    }

    public void setEmail(String email) {
        setString(EMAIL, email);
    }
    public String getEmail() {
        return getString(EMAIL);
    }

    public void setAllowedEditAdmins(boolean allowed) {
        setBool(EDITADMINS, allowed);
    }
    public Boolean isAllowedEditAdmins() {
        return getBool(EDITADMINS);
    }

    public void setAllowedChangePassword(boolean allowed) {
        setBool(CHANGEPASSWORD, allowed);
    }
    public Boolean isAllowedChangePassword() {
        return getBool(CHANGEPASSWORD);
    }

    public void setAllowedFullAccess(boolean allowed) {
        setBool(FULLACCESS, allowed);
    }
    public Boolean isAllowedFullAccess() {
        return getBool(FULLACCESS);
    }

    public void setAllowedConfiguration(boolean allowed) {
        setBool(CONFIGURATION, allowed);
    }
    public Boolean isAllowedConfiguration() {
        return getBool(CONFIGURATION);
    }

    public void setAllowedAdministerProjectLogbook(boolean allowed) {
        setBool(ADMINPROJECTLOGBOOK, allowed);
    }
    public Boolean isAllowedAdministerProjectLogbook() {
        return getBool(ADMINPROJECTLOGBOOK);
    }

    public void setAllowedEditLogbook(boolean allowed) {
        setBool(EDITLOGBOOK, allowed);
    }
    public Boolean isAllowedEditLogbook() {
        return getBool(EDITLOGBOOK);
    }

    public void setAllowedEditBoatStatus(boolean allowed) {
        setBool(EDITBOATSTATUS, allowed);
    }
    public Boolean isAllowedEditBoatStatus() {
        return getBool(EDITBOATSTATUS);
    }

    public void setAllowedEditBoatReservation(boolean allowed) {
        setBool(EDITBOATRESERVATION, allowed);
    }
    public Boolean isAllowedEditBoatReservation() {
        return getBool(EDITBOATRESERVATION);
    }

    public void setAllowedEditBoatDamages(boolean allowed) {
        setBool(EDITBOATDAMAGES, allowed);
    }
    public Boolean isAllowedEditBoatDamages() {
        return getBool(EDITBOATDAMAGES);
    }

    public void setAllowedEditBoats(boolean allowed) {
        setBool(EDITBOATS, allowed);
    }
    public Boolean isAllowedEditBoats() {
        return getBool(EDITBOATS);
    }

    public void setAllowedEditPersons(boolean allowed) {
        setBool(EDITPERSONS, allowed);
    }
    public Boolean isAllowedEditPersons() {
        return getBool(EDITPERSONS);
    }

    public void setAllowedEditDestinations(boolean allowed) {
        setBool(EDITDESTINATIONS, allowed);
    }
    public Boolean isAllowedEditDestinations() {
        return getBool(EDITDESTINATIONS);
    }

    public void setAllowedEditGroups(boolean allowed) {
        setBool(EDITGROUPS, allowed);
    }
    public Boolean isAllowedEditGroups() {
        return getBool(EDITGROUPS);
    }

    public void setAllowedEditCrews(boolean allowed) {
        setBool(EDITCREWS, allowed);
    }
    public Boolean isAllowedEditCrews() {
        return getBool(EDITCREWS);
    }

    public void setAllowedEditFahrtenabzeichen(boolean allowed) {
        setBool(EDITFAHRTENABZEICHEN, allowed);
    }
    public Boolean isAllowedEditFahrtenabzeichen() {
        return getBool(EDITFAHRTENABZEICHEN);
    }

    public void setAllowedMsgReadAdmin(boolean allowed) {
        setBool(MSGREADADMIN, allowed);
    }
    public Boolean isAllowedMsgReadAdmin() {
        return getBool(MSGREADADMIN);
    }

    public void setAllowedMsgReadBoatMaintenance(boolean allowed) {
        setBool(MSGREADBOATMAINT, allowed);
    }
    public Boolean isAllowedMsgReadBoatMaintenance() {
        return getBool(MSGREADBOATMAINT);
    }

    public void setAllowedMsgMarkReadAdmin(boolean allowed) {
        setBool(MSGMARKREADADMIN, allowed);
    }
    public Boolean isAllowedMsgMarkReadAdmin() {
        return getBool(MSGMARKREADADMIN);
    }

    public void setAllowedMsgMarkReadBoatMaintenance(boolean allowed) {
        setBool(MSGMARKREADBOATMAINT, allowed);
    }
    public Boolean isAllowedMsgMarkReadBoatMaintenance() {
        return getBool(MSGMARKREADBOATMAINT);
    }

    public void setAllowedMsgAutoMarkReadAdmin(boolean allowed) {
        setBool(MSGAUTOREADADMIN, allowed);
    }
    public Boolean isAllowedMsgAutoMarkReadAdmin() {
        return getBool(MSGAUTOREADADMIN);
    }

    public void setAllowedMsgAutoMarkReadBoatMaintenance(boolean allowed) {
        setBool(MSGAUTOREADBOATMAINT, allowed);
    }
    public Boolean isAllowedMsgAutoMarkReadBoatMaintenance() {
        return getBool(MSGAUTOREADBOATMAINT);
    }

    public void setAllowedEditStatistics(boolean allowed) {
        setBool(EDITSTATISTICS, allowed);
    }
    public Boolean isAllowedEditStatistics() {
        return getBool(EDITSTATISTICS);
    }

    public void setAllowedSyncKanuEfb(boolean allowed) {
        setBool(SYNCKANUEFB, allowed);
    }
    public Boolean isAllowedSyncKanuEfb() {
        return getBool(SYNCKANUEFB);
    }

    public void setAllowedShowLogfile(boolean allowed) {
        setBool(SHOWLOGFILE, allowed);
    }
    public Boolean isAllowedShowLogfile() {
        return getBool(SHOWLOGFILE);
    }

    public void setAllowedExitEfa(boolean allowed) {
        setBool(EXITEFA, allowed);
    }
    public Boolean isAllowedExitEfa() {
        return getBool(EXITEFA);
    }

    public void setAllowedLockEfa(boolean allowed) {
        setBool(LOCKEFA, allowed);
    }
    public Boolean isAllowedLockEfa() {
        return getBool(LOCKEFA);
    }

    public void setAllowedExecCommand(boolean allowed) {
        setBool(EXECCOMMAND, allowed);
    }
    public Boolean isAllowedExecCommand() {
        return getBool(EXECCOMMAND);
    }

    public String[] getQualifiedNameFields() {
        return new String[] { NAME };
    }

    public String getQualifiedName() {
        return getName();
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Administrator");
        String CAT_PERMISSIONS  = "%02%" + International.getString("Berechtigungen");
        String CAT_MESSAGES     = "%03%" + International.getString("Nachrichten");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        v.add(item = new ItemTypeString(NAME, getName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Name")));
        item.setEnabled(getName() == null || getName().length() == 0);
        ((ItemTypeString)item).setNotNull(true);
        ((ItemTypeString)item).setAllowedCharacters("abcdefghijklmnopqrstuvwxyz1234567890");
        if (getPassword() != null && getPassword().length() > 0) {
            v.add(item = new ItemTypeButton("PASSWORDBUTTON",
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Paßwort ändern")));
            ((ItemTypeButton)item).setFieldGrid(2, GridBagConstraints.EAST, GridBagConstraints.NONE);
        } else {
            v.add(item = new ItemTypePassword(PASSWORD, "",
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Paßwort")));
            ((ItemTypePassword)item).setNotNull(true);
            ((ItemTypePassword)item).setMinCharacters(6);
            v.add(item = new ItemTypePassword(PASSWORD + "_REPEAT", "",
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Paßwort") +
                    " (" + International.getString("Wiederholung") + ")"));
            ((ItemTypePassword)item).setNotNull(true);
            ((ItemTypePassword)item).setMinCharacters(6);
        }
        v.add(item = new ItemTypeString(EMAIL, getEmail(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("email-Adresse")));

        v.add(item = new ItemTypeBoolean(EDITADMINS, isAllowedEditAdmins(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Admins verwalten")));
        v.add(item = new ItemTypeBoolean(CHANGEPASSWORD, isAllowedChangePassword(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Paßwort ändern")));
        v.add(item = new ItemTypeBoolean(FULLACCESS, isAllowedFullAccess(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Vollzugriff")));
        v.add(item = new ItemTypeBoolean(ADMINPROJECTLOGBOOK, isAllowedAdministerProjectLogbook(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Projekte und Fahrtenbücher administrieren")));
        v.add(item = new ItemTypeBoolean(EDITLOGBOOK, isAllowedEditLogbook(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Fahrtenbuch bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITBOATS, isAllowedEditBoats(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Boote bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITBOATSTATUS, isAllowedEditBoatStatus(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Bootsstatus bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITBOATRESERVATION, isAllowedEditBoatReservation(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Bootsreservierungen bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITBOATDAMAGES, isAllowedEditBoatDamages(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Bootsschäden bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITPERSONS, isAllowedEditPersons(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Personen und Status bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITGROUPS, isAllowedEditGroups(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Gruppen bearbeiten")));
        v.add(item = new ItemTypeBoolean(EDITCREWS, isAllowedEditCrews(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Mannschaften bearbeiten")));
        if (Daten.efaConfig.useFunctionalityRowingGermany.getValue()) {
            v.add(item = new ItemTypeBoolean(EDITFAHRTENABZEICHEN, isAllowedEditFahrtenabzeichen(),
                    IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.onlyFor("Fahrtenabzeichen bearbeiten","de")));
        }
        v.add(item = new ItemTypeBoolean(EDITDESTINATIONS, isAllowedEditDestinations(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Ziele und Gewässer bearbeiten")));
        v.add(item = new ItemTypeBoolean(CONFIGURATION, isAllowedConfiguration(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("efa konfigurieren")));
        v.add(item = new ItemTypeBoolean(EDITSTATISTICS, isAllowedEditStatistics(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Statistiken erstellen")));
        if (Daten.efaConfig.useFunctionalityCanoeingGermany.getValue()) {
            v.add(item = new ItemTypeBoolean(SYNCKANUEFB, isAllowedSyncKanuEfb(),
                    IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.onlyFor("mit KanuEfb synchonisieren","de")));
        }
        v.add(item = new ItemTypeBoolean(SHOWLOGFILE, isAllowedShowLogfile(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Logdatei anzeigen")));
        v.add(item = new ItemTypeBoolean(EXITEFA, isAllowedExitEfa(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("efa beenden")));
        v.add(item = new ItemTypeBoolean(LOCKEFA, isAllowedLockEfa(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("efa sperren")));
        v.add(item = new ItemTypeBoolean(EXECCOMMAND, isAllowedExecCommand(),
                IItemType.TYPE_PUBLIC, CAT_PERMISSIONS, International.getString("Betriebssystem-Kommando ausführen")));

        v.add(item = new ItemTypeBoolean(MSGREADADMIN, isAllowedMsgReadAdmin(),
                IItemType.TYPE_PUBLIC, CAT_MESSAGES, International.getMessage("Nachrichten an {recipient} lesen",
                International.getString("Admin"))));
        v.add(item = new ItemTypeBoolean(MSGMARKREADADMIN, isAllowedMsgMarkReadAdmin(),
                IItemType.TYPE_PUBLIC, CAT_MESSAGES, International.getMessage("Nachrichten an {recipient} als gelesen markieren",
                International.getString("Admin"))));
        v.add(item = new ItemTypeBoolean(MSGAUTOREADADMIN, isAllowedMsgAutoMarkReadAdmin(),
                IItemType.TYPE_PUBLIC, CAT_MESSAGES, International.getMessage("Nachrichten an {recipient} automatisch als gelesen markieren",
                International.getString("Admin"))));
        v.add(item = new ItemTypeBoolean(MSGREADBOATMAINT, isAllowedMsgReadBoatMaintenance(),
                IItemType.TYPE_PUBLIC, CAT_MESSAGES, International.getMessage("Nachrichten an {recipient} lesen",
                International.getString("Bootswart"))));
        v.add(item = new ItemTypeBoolean(MSGMARKREADBOATMAINT, isAllowedMsgMarkReadBoatMaintenance(),
                IItemType.TYPE_PUBLIC, CAT_MESSAGES, International.getMessage("Nachrichten an {recipient} als gelesen markieren",
                International.getString("Bootswart"))));
        v.add(item = new ItemTypeBoolean(MSGAUTOREADBOATMAINT, isAllowedMsgAutoMarkReadBoatMaintenance(),
                IItemType.TYPE_PUBLIC, CAT_MESSAGES, International.getMessage("Nachrichten an {recipient} automatisch als gelesen markieren",
                International.getString("Bootswart"))));
        return v;
    }


    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[1];
        header[0] = new TableItemHeader(International.getString("Name"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[1];
        items[0] = new TableItem(getName());
        return items;
    }

}
