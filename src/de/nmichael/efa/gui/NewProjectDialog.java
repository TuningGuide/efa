/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewProjectDialog extends StepwiseDialog implements IItemListener {

    public NewProjectDialog(JDialog parent) {
        super(parent, International.getString("Neues Projekt"));
    }

    public NewProjectDialog(Frame parent) {
        super(parent, International.getString("Neues Projekt"));
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Name und Beschreibung"),
            International.getString("Speichertyp auswählen"),
            International.getString("Speicherort festlegen"),
            International.getString("Angaben zum Verein"),
            International.getString("Verbände")
        };
    }
    
    String getDescription(int step) {
        switch(step) {
            case 0:
                return International.getString("In efa2 werden alle Daten in Projekten zusammengefaßt. Üblicherweise solltest Du für einen Verein "+
                        "genau ein Projekt erstellen, welches dann sämtliche Fahrtenbücher, Mitglieder-, Boots- und Ziellisten sowie sonstige Daten enthält.");
            case 1:
                return International.getString("Bitte wähle, wo die Daten des Projekts gespeichert werden sollen") + ":\n"+
                        "  "+International.getString("lokales Dateisystem - speichert die Daten lokal auf Deinem Computer")+"\n"+
                        "  "+International.getString("efaRemote - greift auf Daten in einem entfernt laufenden efa zu")+"\n"+
                        "  "+International.getString("SQL-Datenbank - speichert die Daten in einer beliebigen SQL-Datenbank");
            case 2:
                return International.getString("Bitte gib an, wo die Daten gespeichert werden sollen und wie der Zugriff erfolgen soll");// + ":\n"+
                        //"  "+International.getString("lokales Dateisystem - speichert die Daten lokal auf Deinem Computer")+"\n"+
                        //"  "+International.getString("SQL-Datenbank - speichert die Daten in einer beliebigen SQL-Datenbank");
            case 3:
                return International.getString("Bitte vervollständige die Angaben zu Deinem Verein.");
            case 4:
                return International.getString("Bitte gib an, in welchen Dachverbänden Dein Verein Mitglied ist, und (falls vorhanden) die Benutzernamen "+
                        "für elektronische Meldung und ähnliche Dienste.");
        }
        return "";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        ProjectRecord r;

        r = Project.createNewRecordFromStatic(ProjectRecord.TYPE_PROJECT);
        items.addAll(r.getGuiItems(1, "0", true));
        items.addAll(r.getGuiItems(2, "1", true));
        items.addAll(r.getGuiItems(3, "2", true));
        r = Project.createNewRecordFromStatic(ProjectRecord.TYPE_CLUB);
        items.addAll(r.getGuiItems(1, "3", true));
        items.addAll(r.getGuiItems(2, "4", true));
    }

    boolean checkInput(int direction) {
        boolean ok = super.checkInput(direction);
        if (!ok) {
            return false;
        }
        
        if (step == 0) {
            ItemTypeString item = (ItemTypeString)getItemByName(ProjectRecord.PROJECTNAME);
            String name = item.getValue();
            Project prj = new Project(IDataAccess.TYPE_FILE_XML, Daten.efaDataDirectory, name);
            try {
                if (prj.data().existsStorageObject()) {
                    Dialog.error(LogString.logstring_fileAlreadyExists(name, International.getString("Projekt")));
                    item.requestFocus();
                    return false;
                }
            } catch (Exception e) {
            }
        }


        if (step == 1) {
            ItemTypeStringList item = (ItemTypeStringList)getItemByName(ProjectRecord.STORAGETYPE);
            if (!item.getValue().equals(IDataAccess.TYPESTRING_FILE_XML) &&
                !item.getValue().equals(IDataAccess.TYPESTRING_EFA_REMOTE)) {
                Dialog.error(International.getMessage("Die ausgewählte Option '{option}' wird zur Zeit noch nicht unterstützt.",
                        item.getValue()));
                item.requestFocus();
                return false;
            }

            // remove all StorageType-specific config options
            ProjectRecord r = Project.createNewRecordFromStatic(ProjectRecord.TYPE_PROJECT);
            Vector<IItemType> itemsToBeDeleted = new Vector<IItemType>();
            itemsToBeDeleted.addAll(r.getGuiItems(3, "2", true));
            r.setStorageType(IDataAccess.TYPE_EFA_REMOTE);
            itemsToBeDeleted.addAll(r.getGuiItems(3, "2", true));
            r.setStorageType(IDataAccess.TYPE_DB_SQL);
            itemsToBeDeleted.addAll(r.getGuiItems(3, "2", true));
            for (int i=0; i<items.size(); i++) {
                for (int j=0; j<itemsToBeDeleted.size(); j++) {
                    if (items.get(i).getName().equals(itemsToBeDeleted.get(j).getName())) {
                        items.remove(i--);
                    }
                }
            }

            // add all StorageType-specific config options
            r.setStorageType(IDataAccess.TYPE_FILE_XML);
            if (item.getValue().equals(IDataAccess.TYPESTRING_EFA_REMOTE)) {
                r.setStorageType(IDataAccess.TYPE_EFA_REMOTE);
            }
            if (item.getValue().equals(IDataAccess.TYPESTRING_DB_SQL)) {
                r.setStorageType(IDataAccess.TYPE_DB_SQL);
            }
            items.addAll(r.getGuiItems(3, "2", true));
            if (item.getValue().equals(IDataAccess.TYPESTRING_EFA_REMOTE)) {
                IItemType checkbox = getItemByName(ProjectRecord.EFAONLINECONNECT);
                if (checkbox != null) {
                    checkbox.registerItemListener(this);
                    itemListenerAction(checkbox, null);
                }
            }


        }
        return true;
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType.getName().equals(ProjectRecord.EFAONLINECONNECT) && (event == null || event instanceof ActionEvent)) {
            boolean efaOnline = Boolean.parseBoolean(itemType.getValueFromField());
            IItemType item;
            item = getItemByName(ProjectRecord.STORAGELOCATION);
            if (item != null) {
                item.setNotNull(!efaOnline);
                item.setEnabled(!efaOnline);
            }
            item = getItemByName(ProjectRecord.EFAONLINEUSERNAME);
            if (item != null) {
                item.setNotNull(efaOnline);
                item.setEnabled(efaOnline);
            }
            item = getItemByName(ProjectRecord.EFAONLINEPASSWORD);
            if (item != null) {
                item.setNotNull(efaOnline);
                item.setEnabled(efaOnline);
            }
        }
    }

    boolean finishButton_actionPerformed(ActionEvent e) {
        if (!super.finishButton_actionPerformed(e)) {
            return false;
        }

        ItemTypeString prjName = (ItemTypeString)getItemByName(ProjectRecord.PROJECTNAME);

        ItemTypeStringList storType = (ItemTypeStringList)getItemByName(ProjectRecord.STORAGETYPE);
        int storageType = -1;
        if (storType.getValue().equals(IDataAccess.TYPESTRING_FILE_XML)) {
            storageType = IDataAccess.TYPE_FILE_XML;
        }
        if (storType.getValue().equals(IDataAccess.TYPESTRING_EFA_REMOTE)) {
            storageType = IDataAccess.TYPE_EFA_REMOTE;
        }
        if (storType.getValue().equals(IDataAccess.TYPESTRING_DB_SQL)) {
            storageType = IDataAccess.TYPE_DB_SQL;
        }
        // Note: The storageType of the project file itself is always TYPE_FILE_XML.
        // The storageType of the project's content (set through prj.setProjectStorageType(storageType)) may differ.
        Project prj = new Project(prjName.getValue());
        try {
            prj.open(true);
            prj.setEmptyProject(prjName.getValue());

            // Project Properties
            prj.setProjectDescription(((ItemTypeString)getItemByName(ProjectRecord.DESCRIPTION)).getValue());
            prj.setProjectStorageType(storageType);
            prj.setAdminName(((ItemTypeString)getItemByName(ProjectRecord.ADMINNAME)).getValue());
            prj.setAdminEmail(((ItemTypeString)getItemByName(ProjectRecord.ADMINEMAIL)).getValue());
            if (getItemByName(ProjectRecord.STORAGELOCATION) != null) {
                prj.setProjectStorageLocation(((ItemTypeString)getItemByName(ProjectRecord.STORAGELOCATION)).getValue());
            }
            if (getItemByName(ProjectRecord.STORAGEUSERNAME) != null) {
                prj.setProjectStorageUsername(((ItemTypeString)getItemByName(ProjectRecord.STORAGEUSERNAME)).getValue());
            }
            if (getItemByName(ProjectRecord.STORAGEPASSWORD) != null) {
                prj.setProjectStoragePassword(((ItemTypeString)getItemByName(ProjectRecord.STORAGEPASSWORD)).getValue());
            }
            if (getItemByName(ProjectRecord.REMOTEPROJECTNAME) != null) {
                prj.setProjectRemoteProjectName(((ItemTypeString)getItemByName(ProjectRecord.REMOTEPROJECTNAME)).getValue());
            }
            if (getItemByName(ProjectRecord.EFAONLINECONNECT) != null) {
                prj.setProjectEfaOnlineConnect(((ItemTypeBoolean)getItemByName(ProjectRecord.EFAONLINECONNECT)).getValue());
            }
            if (getItemByName(ProjectRecord.EFAONLINEUSERNAME) != null) {
                prj.setProjectEfaOnlineUsername(((ItemTypeString)getItemByName(ProjectRecord.EFAONLINEUSERNAME)).getValue());
            }
            if (getItemByName(ProjectRecord.EFAONLINEPASSWORD) != null) {
                prj.setProjectEfaOnlinePassword(((ItemTypePassword)getItemByName(ProjectRecord.EFAONLINEPASSWORD)).getValue());
            }

            // Club Properties (1)
            prj.setClubName(((ItemTypeString)getItemByName(ProjectRecord.CLUBNAME)).getValue());
            prj.setClubAddressStreet(((ItemTypeString)getItemByName(ProjectRecord.ADDRESSSTREET)).getValue());
            prj.setClubAddressCity(((ItemTypeString)getItemByName(ProjectRecord.ADDRESSCITY)).getValue());
            if (getItemByName(ProjectRecord.AREAID) != null) {
                prj.setClubAreaId(((ItemTypeInteger)getItemByName(ProjectRecord.AREAID)).getValue());
            }

            // Club Properties (2)
            prj.setClubGlobalAssociationName(((ItemTypeString)getItemByName(ProjectRecord.ASSOCIATIONGLOBALNAME)).getValue());
            prj.setClubGlobalAssociationMemberNo(((ItemTypeString)getItemByName(ProjectRecord.ASSOCIATIONGLOBALMEMBERNO)).getValue());
            prj.setClubGlobalAssociationLogin(((ItemTypeString)getItemByName(ProjectRecord.ASSOCIATIONGLOBALLOGIN)).getValue());
            prj.setClubRegionalAssociationName(((ItemTypeString)getItemByName(ProjectRecord.ASSOCIATIONREGIONALNAME)).getValue());
            prj.setClubRegionalAssociationMemberNo(((ItemTypeString)getItemByName(ProjectRecord.ASSOCIATIONREGIONALMEMBERNO)).getValue());
            prj.setClubRegionalAssociationLogin(((ItemTypeString)getItemByName(ProjectRecord.ASSOCIATIONREGIONALLOGIN)).getValue());
            if (getItemByName(ProjectRecord.MEMBEROFDRV) != null) {
                prj.setClubMemberOfDRV(((ItemTypeBoolean)getItemByName(ProjectRecord.MEMBEROFDRV)).getValue());
            }
            if (getItemByName(ProjectRecord.MEMBEROFSRV) != null) {
                prj.setClubMemberOfSRV(((ItemTypeBoolean)getItemByName(ProjectRecord.MEMBEROFSRV)).getValue());
            }
            if (getItemByName(ProjectRecord.MEMBEROFADH) != null) {
                prj.setClubMemberOfADH(((ItemTypeBoolean)getItemByName(ProjectRecord.MEMBEROFADH)).getValue());
            }
            if (getItemByName(ProjectRecord.KANUEFBUSERNAME) != null) {
                prj.setClubKanuEfbUsername(((ItemTypeString)getItemByName(ProjectRecord.KANUEFBUSERNAME)).getValue());
            }
            if (getItemByName(ProjectRecord.KANUEFBPASSWORD) != null) {
                prj.setClubKanuEfbPassword(((ItemTypeString)getItemByName(ProjectRecord.KANUEFBPASSWORD)).getValue());
            }

            prj.close();
            Project.openProject(prjName.getValue());
            if (Daten.project != null) {
                Dialog.infoDialog(LogString.logstring_fileSuccessfullyCreated(prjName.getValue(),
                        International.getString("Projekt")));
            }
            setDialogResult(Daten.project != null);
        } catch(EfaException ee) {
            Dialog.error(ee.getMessage());
            ee.log();
            setDialogResult(false);
        }
        return true;
    }

    public String createNewProjectAndLogbook() {
        showDialog();
        if (!getDialogResult()) {
            return null;
        }
        if (Daten.project != null && Daten.project.getProjectStorageType() != IDataAccess.TYPE_FILE_XML) {
            return null;
        }
        String logbookName = null;
        switch(Dialog.auswahlDialog(International.getString("Fahrtenbuch erstellen"),
                International.getString("Das Projekt enthält noch keine Daten.") + " " +
                International.getString("Was möchtest Du tun?"),
                International.getString("Neues (leeres) Fahrtenbuch erstellen"),
                International.getString("Daten aus efa 1.x importieren"))) {
            case 0:
                NewLogbookDialog dlg0 = null;
                if (getParentJDialog() != null) {
                    dlg0 = new NewLogbookDialog(getParentJDialog());
                }
                if (getParentFrame() != null) {
                    dlg0 = new NewLogbookDialog(getParentFrame());
                }
                logbookName = dlg0.newLogbookDialog();
                break;
            case 1:
                ImportEfa1DataDialog dlg1 = null;
                if (getParentJDialog() != null) {
                    dlg1 = new ImportEfa1DataDialog(getParentJDialog());
                }
                if (getParentFrame() != null) {
                    dlg1 = new ImportEfa1DataDialog(getParentFrame());
                }
                dlg1.showDialog();
                logbookName = dlg1.getNewestLogbookName();
                break;
        }
        return logbookName;
    }

}
