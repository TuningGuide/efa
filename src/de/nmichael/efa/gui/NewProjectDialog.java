/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.types.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewProjectDialog extends StepwiseDialog {

    private static final String STORAGETYPE_LOCAL  = "LOCAL";
    private static final String STORAGETYPE_SQL    = "SQL";

    private static final String PROJECTNAME        = "PROJECTNAME";
    private static final String PROJECTDESCRIPTION = "PROJECTDESCRIPTION";
    private static final String STORAGETYPE        = "STORAGETYPE";

    public NewProjectDialog(JDialog parent) {
        super(parent, International.getString("Neues Projekt"));
    }

    public NewProjectDialog(Frame parent) {
        super(parent, International.getString("Neues Projekt"));
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Name und Beschreibung"),
            International.getString("Speicherort festlegen"),
            International.getString("Angaben zum Verein")
        };
    }
    
    String getDescription(int step) {
        switch(step) {
            case 0:
                return International.getString("In efa2 werden alle Daten in Projekten zusammengefaßt. Üblicherweise solltest Du für einen Verein "+
                        "genau ein Projekt erstellen, welches dann sämtliche Fahrtenbücher, Mitglieder-, Boots- und Ziellisten sowie sonstige Daten enthält.");
            case 1:
                return International.getString("Bitte wähle, wo die Daten des Projekts gespeichert werden sollen:\n"+
                        "  lokales Dateisystem - speichert die Daten lokal auf Deinem Computer\n"+
                        "  SQL-Datenbank - speichert die Daten in einer beliebigen SQL-Datenbank");
            case 2:
                return International.getString("Bitte vervollständige die Angaben zu Deinem Verein.");
        }
        return "";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        // Items for Step 0
        item = new ItemTypeString(PROJECTNAME, "", IItemType.TYPE_PUBLIC, "0", International.getString("Name des Projekts"));
        ((ItemTypeString)item).setAllowedCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_");
        ((ItemTypeString)item).setReplacementCharacter('_');
        ((ItemTypeString)item).setNotNull(true);
        items.add(item);
        item = new ItemTypeString(PROJECTDESCRIPTION, "", IItemType.TYPE_PUBLIC, "0", International.getString("Beschreibung"));
        items.add(item);

        // Items for Step 1
        item = new ItemTypeStringList(STORAGETYPE, STORAGETYPE_LOCAL,
                new String[] { STORAGETYPE_LOCAL, STORAGETYPE_SQL },
                new String[] { International.getString("lokales Dateisystem"),
                               International.getString("SQL-Datenbank") },
                IItemType.TYPE_PUBLIC, "1", International.getString("Speicherort"));
        items.add(item);
    }

    boolean checkInput(int direction) {
        boolean ok = super.checkInput(direction);
        if (!ok) {
            return false;
        }
        
        if (step == 0) {
            ItemTypeString item = (ItemTypeString)getItemByName(PROJECTNAME);
            String name = item.getValue();
            Project prj = new Project(IDataAccess.TYPE_FILE_XML, Daten.efaDataDirectory, name);
            try {
                if (prj.data().existsStorageObject()) {
                    Dialog.error(International.getMessage("Das Projekt {project} existiert bereits.",
                            name));
                    item.requestFocus();
                    return false;
                }
            } catch (Exception e) {
            }
        }


        if (step == 1) {
            ItemTypeStringList item = (ItemTypeStringList)getItemByName(STORAGETYPE);
            if (!item.getValue().equals(STORAGETYPE_LOCAL)) {
                Dialog.error(International.getMessage("Die ausgewählte Option '{option}' wird zur Zeit noch nicht unterstützt.",
                        International.getString("SQL-Datenbank")));
                item.requestFocus();
                return false;
            }
        }
        return true;
    }

    void finishButton_actionPerformed(ActionEvent e) {
        super.finishButton_actionPerformed(e);

        ItemTypeString prjName = (ItemTypeString)getItemByName(PROJECTNAME);
        ItemTypeStringList storType = (ItemTypeStringList)getItemByName(STORAGETYPE);
        int storageType = -1;
        if (storType.getValue().equals(STORAGETYPE_LOCAL)) {
            storageType = IDataAccess.TYPE_FILE_XML;
        }
        if (storType.getValue().equals(STORAGETYPE_SQL)) {
            storageType = IDataAccess.TYPE_DB_SQL;
        }
        Project prj = new Project(storageType, Daten.efaDataDirectory, prjName.getValue());
        try {
            prj.open(true);
            prj.setEmptyProject(prjName.getValue());
            prj.close();
            prj.open(false);
            Daten.project = prj;
            Dialog.infoDialog(International.getString("Das Projekt wurde erfolgreich angelegt."));
            setDialogResult(true);
        } catch(EfaException ee) {
            Dialog.error(ee.getMessage());
            ee.log();
            setDialogResult(false);
        }
    }

    public boolean createNewProjectAndLogbook() {
        showDialog();
        if (!getDialogResult()) {
            return false;
        }
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
                dlg0.showDialog();
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
                break;
        }
        return true;
    }

}
