/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.util;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.data.sync.KanuEfbSyncTask;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.gui.dataedit.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.International;
import java.awt.Dialog.ModalExclusionType;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;

public class EfaMenuButton {
    
    public final static String SEPARATOR                = "SEPARATOR";

    public final static String MENU_FILE                = "FILE";
    public final static String BUTTON_PROJECTS          = "PROJECTS";
    public final static String BUTTON_LOGBOOKS          = "LOGBOOKS";
    public final static String BUTTON_UPDATE            = "UPDATE";
    public final static String BUTTON_OSCOMMAND         = "OSCOMMAND";
    public final static String BUTTON_EXIT              = "EXIT";

    public final static String MENU_ADMINISTRATION      = "ADMINISTRATION";
    public final static String BUTTON_LOGBOOK           = "LOGBOOK";
    public final static String BUTTON_SESSIONGROUPS     = "SESSIONGROUPS";
    public final static String BUTTON_BOATS             = "BOATS";
    public final static String BUTTON_BOATSTATUS        = "BOATSTATUS";
    public final static String BUTTON_BOATRESERVATIONS  = "BOATRESERVATIONS";
    public final static String BUTTON_BOATDAMAGES       = "BOATDAMAGES";
    public final static String BUTTON_PERSONS           = "PERSONS";
    public final static String BUTTON_STATUS            = "STATUS";
    public final static String BUTTON_GROUPS            = "GROUPS";
    public final static String BUTTON_CREWS             = "CREWS";
    public final static String BUTTON_FAHRTENABZEICHEN  = "FAHRTENABZEICHEN";
    public final static String BUTTON_DESTINATIONS      = "DESTINATIONS";
    public final static String BUTTON_WATERS            = "WATERS";
    public final static String BUTTON_CONFIGURATION     = "CONFIGURATION";
    public final static String BUTTON_ADMINS            = "ADMINS";
    public final static String BUTTON_PASSWORD          = "PASSWORD";
    public final static String BUTTON_LOCKEFA           = "LOCKEFA";

    public final static String MENU_OUTPUT              = "OUTPUT";
    public final static String BUTTON_STATISTICS        = "STATISTICS";
    public final static String BUTTON_SYNCKANUEFB       = "SYNCKANUEFB";

    public final static String MENU_INFO                = "INFO";
    public final static String BUTTON_HELP              = "HELP";
    public final static String BUTTON_MESSAGES          = "MESSAGES";
    public final static String BUTTON_LOGFILE           = "LOGFILE";
    public final static String BUTTON_ABOUT             = "ABOUT";

    public enum MenuMode {
        all,
        efaBaseGui,
        efaBthsGui,
        efaBthsLogbookGui
    }

    private static Hashtable<String,String> actionMapping;

    private String menuName;
    private String menuText;
    private String buttonName;
    private String buttonText;
    private ImageIcon icon;

    public EfaMenuButton(String menuName, String buttonName, String menuText, String buttonText, ImageIcon icon) {
        this.menuName = menuName;
        this.buttonName = buttonName;
        this.menuText = menuText;
        this.buttonText = buttonText;
        this.icon = icon;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuText() {
        return menuText;
    }

    public String getButtonName() {
        return buttonName;
    }

    public String getButtonText() {
        return buttonText;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public boolean isSeparator() {
        return buttonName.equals(SEPARATOR);
    }

    public static synchronized Vector<EfaMenuButton> getAllMenuButtons(AdminRecord admin, boolean adminMode) {
        Vector<EfaMenuButton> v = new Vector<EfaMenuButton>();

        if (admin == null || admin.isAllowedAdministerProjectLogbook()) {
            v.add(new EfaMenuButton(MENU_FILE, BUTTON_PROJECTS,
                    International.getStringWithMnemonic("Datei"),
                    International.getStringWithMnemonic("Projekte") + " ...",
                    BaseFrame.getIcon("menu_projects.png")));
        }
        if (admin == null || admin.isAllowedAdministerProjectLogbook()) {
            v.add(new EfaMenuButton(MENU_FILE, BUTTON_LOGBOOKS,
                    International.getStringWithMnemonic("Datei"),
                    International.getStringWithMnemonic("Fahrtenbücher") + " ...",
                    BaseFrame.getIcon("menu_logbooks.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_FILE) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_FILE, SEPARATOR,
                    null, null, null));
        }
        if (Daten.efaConfig.getValueUseFunctionalityCanoeingGermany()) {
            if (admin == null || admin.isAllowedSyncKanuEfb()) {
                v.add(new EfaMenuButton(MENU_FILE, BUTTON_SYNCKANUEFB,
                        International.getStringWithMnemonic("Datei"),
                        International.onlyFor("Mit Kanu-Efb synchronisieren", "de"),
                        BaseFrame.getIcon("menu_efbsync.png")));
            }
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_FILE) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_FILE, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedUpdateEfa()) {
            v.add(new EfaMenuButton(MENU_FILE, BUTTON_UPDATE,
                    International.getStringWithMnemonic("Datei"),
                    International.getStringWithMnemonic("Online-Update"),
                    BaseFrame.getIcon("menu_update.png")));
        }
        if (admin == null || admin.isAllowedExecCommand()) {
            v.add(new EfaMenuButton(MENU_FILE, BUTTON_OSCOMMAND,
                    International.getStringWithMnemonic("Datei"),
                    International.getStringWithMnemonic("Kommando ausführen"),
                    BaseFrame.getIcon("menu_command.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_FILE) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_FILE, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedExitEfa() || !adminMode) {
            v.add(new EfaMenuButton(MENU_FILE, BUTTON_EXIT,
                    International.getStringWithMnemonic("Datei"),
                    International.getStringWithMnemonic("Beenden"),
                    BaseFrame.getIcon("menu_exit.png")));
        }

        if (admin == null || (admin.isAllowedEditLogbook() && adminMode)) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_LOGBOOK,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Fahrtenbuch"),
                    BaseFrame.getIcon("menu_logbook.png")));
        }
        if (admin == null || admin.isAllowedEditLogbook() && adminMode) { // we have the same menu again at the end for non-admin mode...
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_SESSIONGROUPS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Fahrtgruppen"),
                    BaseFrame.getIcon("menu_sessiongroups.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_ADMINISTRATION) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedEditBoats()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_BOATS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Boote"),
                    BaseFrame.getIcon("menu_boats.png")));
        }
        if (admin == null || admin.isAllowedEditBoatStatus()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_BOATSTATUS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Bootsstatus"),
                    BaseFrame.getIcon("menu_boatstatus.png")));
        }
        if (admin == null || admin.isAllowedEditBoatReservation()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_BOATRESERVATIONS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Bootsreservierungen"),
                    BaseFrame.getIcon("menu_boatreservations.png")));
        }
        if (admin == null || admin.isAllowedEditBoatDamages()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_BOATDAMAGES,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Bootsschäden"),
                    BaseFrame.getIcon("menu_boatdamages.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_ADMINISTRATION) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedEditPersons()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_PERSONS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Personen"),
                    BaseFrame.getIcon("menu_persons.png")));
        }
        if (admin == null || admin.isAllowedEditPersons()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_STATUS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Status"),
                    BaseFrame.getIcon("menu_status.png")));
        }
        if (admin == null || admin.isAllowedEditGroups()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_GROUPS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Gruppen"),
                    BaseFrame.getIcon("menu_groups.png")));
        }
        if (admin == null || admin.isAllowedEditCrews()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_CREWS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Mannschaften"),
                    BaseFrame.getIcon("menu_crews2.png")));
        }
        if (Daten.efaConfig.getValueUseFunctionalityRowingGermany()) {
            if (admin == null || admin.isAllowedEditFahrtenabzeichen()) {
                v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_FAHRTENABZEICHEN,
                        International.getStringWithMnemonic("Administration"),
                        International.onlyFor("Fahrtenabzeichen", "de"),
                        BaseFrame.getIcon("menu_fahrtenabzeichen.png")));
            }
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_ADMINISTRATION) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedEditDestinations()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_DESTINATIONS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Ziele") + " / " +
                    International.getString("Strecken"),
                    BaseFrame.getIcon("menu_destinations.png")));
        }
        if (admin == null || admin.isAllowedEditDestinations()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_WATERS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Gewässer"),
                    BaseFrame.getIcon("menu_waters.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_ADMINISTRATION) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedEditLogbook() && !adminMode) { // we have the same menu again at the beginning for admin mode...
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_SESSIONGROUPS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Fahrtgruppen"),
                    BaseFrame.getIcon("menu_sessiongroups.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_ADMINISTRATION) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedConfiguration()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_CONFIGURATION,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Konfiguration"),
                    BaseFrame.getIcon("menu_configuration.png")));
        }
        if (admin == null || admin.isAllowedEditAdmins()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_ADMINS,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Administratoren"),
                    BaseFrame.getIcon("menu_admins.png")));
        }
        if (admin == null || admin.isAllowedChangePassword()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_PASSWORD,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("Paßwort ändern"),
                    BaseFrame.getIcon("menu_password.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_ADMINISTRATION) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || (admin.isAllowedLockEfa() && adminMode)) {
            v.add(new EfaMenuButton(MENU_ADMINISTRATION, BUTTON_LOCKEFA,
                    International.getStringWithMnemonic("Administration"),
                    International.getStringWithMnemonic("efa sperren"),
                    BaseFrame.getIcon("menu_lockefa.png")));
        }

        if (admin == null || admin.isAllowedEditStatistics()) {
            v.add(new EfaMenuButton(MENU_OUTPUT, BUTTON_STATISTICS,
                    International.getStringWithMnemonic("Ausgabe"),
                    International.getStringWithMnemonic("Statistiken"),
                    BaseFrame.getIcon("menu_statistics.png")));
        }

        v.add(new EfaMenuButton(MENU_INFO, BUTTON_HELP,
                International.getStringWithMnemonic("Info"),
                International.getStringWithMnemonic("Hilfe"),
                BaseFrame.getIcon("menu_help.png")));
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_INFO) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_INFO, SEPARATOR,
                    null, null, null));
        }
        if (admin == null || admin.isAllowedMsgReadAdmin() || admin.isAllowedMsgReadBoatMaintenance()) {
            v.add(new EfaMenuButton(MENU_INFO, BUTTON_MESSAGES,
                    International.getStringWithMnemonic("Info"),
                    International.getStringWithMnemonic("Nachrichten"),
                    BaseFrame.getIcon("menu_messages.png")));
        }
        if (admin == null || admin.isAllowedShowLogfile()) {
            v.add(new EfaMenuButton(MENU_INFO, BUTTON_LOGFILE,
                    International.getStringWithMnemonic("Info"),
                    International.getStringWithMnemonic("Logdatei"),
                    BaseFrame.getIcon("menu_logfile.png")));
        }
        if (v.size() > 0 && v.get(v.size()-1).getMenuName().equals(MENU_INFO) && !v.get(v.size()-1).isSeparator()) {
            v.add(new EfaMenuButton(MENU_INFO, SEPARATOR,
                    null, null, null));
        }
        v.add(new EfaMenuButton(MENU_INFO, BUTTON_ABOUT,
                International.getStringWithMnemonic("Info"),
                International.getStringWithMnemonic("Über"),
                BaseFrame.getIcon("menu_about.png")));

        if (actionMapping == null) {
            actionMapping = new Hashtable<String,String>();
        }
        for (EfaMenuButton b : v) {
            if (b.getButtonName() != null && b.getButtonText() != null) {
                actionMapping.put(b.getButtonName(), b.getButtonText());
            }
        }

        return v;
    }

    public static boolean menuAction(BaseFrame parent, String action, AdminRecord admin, Logbook logbook) {
        return menuAction(parent, null, action, admin, logbook);
    }

    public static boolean menuAction(BaseDialog parent, String action, AdminRecord admin, Logbook logbook) {
        return menuAction(null, parent, action, admin, logbook);
    }

    private static boolean menuAction(BaseFrame parentFrame, BaseDialog parentDialog, String action, AdminRecord admin, Logbook logbook) {
        if (action == null) {
            return false;
        }

        if (action.equals(BUTTON_PROJECTS)) {
            if (admin == null || (!admin.isAllowedAdministerProjectLogbook())) {
                insufficientRights(admin, action);
                return false;
            }
            return true; // Projects have to handled individually by the caller
        }

        if (action.equals(BUTTON_LOGBOOKS)) {
            if (admin == null || (!admin.isAllowedAdministerProjectLogbook())) {
                insufficientRights(admin, action);
                return false;
            }
            return true; // Logbooks have to handled individually by the caller
        }

        if (action.equals(BUTTON_UPDATE)) {
            if (admin == null || (!admin.isAllowedUpdateEfa())) {
                insufficientRights(admin, action);
                return false;
            }
            Dialog.infoDialog("Not yet implemented!");
            // @todo (P3) Implement Online Update
        }

        if (action.equals(BUTTON_OSCOMMAND)) {
            if (admin == null || (!admin.isAllowedExecCommand())) {
                insufficientRights(admin, action);
                return false;
            }
            Dialog.infoDialog("Not yet implemented!");
            // @todo (P3) Implement Exec OS Command
        }

        if (action.equals(BUTTON_EXIT)) {
            if (admin == null || (!admin.isAllowedExitEfa())) {
                insufficientRights(admin, action);
                return false;
            }
            if (parentFrame != null) {
                parentFrame.cancel();
            }
            if (parentDialog != null) {
                parentDialog.cancel();
            }
        }

        if (action.equals(BUTTON_LOGBOOK)) {
            if (Daten.project == null || logbook == null) {
                Dialog.error(International.getString("Kein Fahrtenbuch geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditLogbook())) {
                insufficientRights(admin, action);
                return false;
            }
            EfaBaseFrame dlg = new EfaBaseFrame(parentDialog, EfaBaseFrame.MODE_ADMIN);
            dlg.setDataForAdminAction(logbook, admin, (AdminDialog)parentDialog);
            dlg.efaBoathouseShowEfaFrame();
        }

        if (action.equals(BUTTON_SESSIONGROUPS)) {
            if (Daten.project == null || logbook == null) {
                Dialog.error(International.getString("Kein Fahrtenbuch geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditLogbook())) {
                insufficientRights(admin, action);
                return false;
            }
            SessionGroupListDialog dlg = (parentFrame != null ? new SessionGroupListDialog(parentFrame, logbook.getName()) : new SessionGroupListDialog(parentDialog, logbook.getName()));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_BOATS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditBoats())) {
                insufficientRights(admin, action);
                return false;
            }
            BoatListDialog dlg = (parentFrame != null ? new BoatListDialog(parentFrame, -1) : new BoatListDialog(parentDialog, -1));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_BOATSTATUS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditBoatStatus())) {
                insufficientRights(admin, action);
                return false;
            }
            BoatStatusListDialog dlg = (parentFrame != null ? new BoatStatusListDialog(parentFrame) : new BoatStatusListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_BOATRESERVATIONS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditBoatReservation())) {
                insufficientRights(admin, action);
                return false;
            }
            BoatReservationListDialog dlg = (parentFrame != null ? new BoatReservationListDialog(parentFrame) : new BoatReservationListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_BOATDAMAGES)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditBoatDamages())) {
                insufficientRights(admin, action);
                return false;
            }
            BoatDamageListDialog dlg = (parentFrame != null ? new BoatDamageListDialog(parentFrame) : new BoatDamageListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_PERSONS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditPersons())) {
                insufficientRights(admin, action);
                return false;
            }
            PersonListDialog dlg = (parentFrame != null ? new PersonListDialog(parentFrame, -1) : new PersonListDialog(parentDialog, -1));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_STATUS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditPersons())) {
                insufficientRights(admin, action);
                return false;
            }
            StatusListDialog dlg = (parentFrame != null ? new StatusListDialog(parentFrame) : new StatusListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_GROUPS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditGroups())) {
                insufficientRights(admin, action);
                return false;
            }
            GroupListDialog dlg = (parentFrame != null ? new GroupListDialog(parentFrame, -1) : new GroupListDialog(parentDialog, -1));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_CREWS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditCrews())) {
                insufficientRights(admin, action);
                return false;
            }
            CrewListDialog dlg = (parentFrame != null ? new CrewListDialog(parentFrame) : new CrewListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_FAHRTENABZEICHEN)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditFahrtenabzeichen())) {
                insufficientRights(admin, action);
                return false;
            }
            FahrtenabzeichenListDialog dlg = (parentFrame != null ? new FahrtenabzeichenListDialog(parentFrame) : new FahrtenabzeichenListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_DESTINATIONS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditDestinations())) {
                insufficientRights(admin, action);
                return false;
            }
            DestinationListDialog dlg = (parentFrame != null ? new DestinationListDialog(parentFrame, -1) : new DestinationListDialog(parentDialog, -1));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_WATERS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditDestinations())) {
                insufficientRights(admin, action);
                return false;
            }
            WatersListDialog dlg = (parentFrame != null ? new WatersListDialog(parentFrame) : new WatersListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_CONFIGURATION)) {
            if (admin == null || (!admin.isAllowedConfiguration())) {
                insufficientRights(admin, action);
                return false;
            }
            EfaConfigDialog dlg = (parentFrame != null ? new EfaConfigDialog(parentFrame) : new EfaConfigDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_ADMINS)) {
            if (admin == null || !admin.isAllowedEditAdmins()) {
                insufficientRights(admin, action);
                return false;
            }
            AdminListDialog dlg = (parentFrame != null ? new AdminListDialog(parentFrame) : new AdminListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_PASSWORD)) {
            if (admin == null || (!admin.isAllowedChangePassword())) {
                insufficientRights(admin, action);
                return false;
            }
            AdminPasswordChangeDialog dlg = (parentFrame != null ? new AdminPasswordChangeDialog(parentFrame, admin) : new AdminPasswordChangeDialog(parentDialog, admin));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_LOCKEFA)) {
            if (admin == null || (!admin.isAllowedLockEfa())) {
                insufficientRights(admin, action);
                return false;
            }
            Dialog.infoDialog("Not yet implemented!");
            // @todo (P3) Implement Lock Efa
        }

        if (action.equals(BUTTON_STATISTICS)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedEditStatistics())) {
                insufficientRights(admin, action);
                return false;
            }
            Dialog.infoDialog("Not yet implemented!");
            // @todo (P2) Implement Create Statistics
        }

        if (action.equals(BUTTON_SYNCKANUEFB)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedSyncKanuEfb())) {
                insufficientRights(admin, action);
                return false;
            }
            KanuEfbSyncTask syncTask = new KanuEfbSyncTask(logbook);
            ProgressDialog progressDialog = (parentFrame != null ?
                new ProgressDialog(parentFrame, International.getString("Mit Kanu-Efb synchronisieren"), syncTask, false) :
                new ProgressDialog(parentDialog, International.getString("Mit Kanu-Efb synchronisieren"), syncTask, false) );
            syncTask.startSynchronization(progressDialog);
        }

        if (action.equals(BUTTON_HELP)) {
            Help.showHelp((parentFrame != null ? parentFrame.getHelpTopic() : parentDialog.getHelpTopic()));
        }

        if (action.equals(BUTTON_MESSAGES)) {
            if (Daten.project == null) {
                Dialog.error(International.getString("Kein Projekt geöffnet."));
                return false;
            }
            if (admin == null || (!admin.isAllowedMsgReadAdmin() && !admin.isAllowedMsgReadBoatMaintenance())) {
                insufficientRights(admin, action);
                return false;
            }
            MessageListDialog dlg = (parentFrame != null ? new MessageListDialog(parentFrame) : new MessageListDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_LOGFILE)) {
            if (admin == null || (!admin.isAllowedShowLogfile())) {
                insufficientRights(admin, action);
                return false;
            }
            LogViewDialog dlg = (parentFrame != null ? new LogViewDialog(parentFrame) : new LogViewDialog(parentDialog));
            dlg.showDialog();
        }

        if (action.equals(BUTTON_ABOUT)) {
            EfaAboutDialog dlg = (parentFrame != null ? new EfaAboutDialog(parentFrame) : new EfaAboutDialog(parentDialog));
            dlg.showDialog();
        }
        
        return true;
    }

    public static void insufficientRights(AdminRecord admin, String action) {
        String actionText = (actionMapping != null ? actionMapping.get(action) : action);
        String msg = International.getMessage("Du hast als {user} nicht die Berechtigung, um die Funktion '{function}' auszuführen.",
                (admin != null ?
                    International.getString("Admin") + " '" + admin.getName() + "'" :
                    International.getString("normaler Nutzer")),
                actionText
                );
        if (Daten.isGuiAppl()) {
            Dialog.error(msg);
        }
    }
}
