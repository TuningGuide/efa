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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.Backup;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class BackupDialog extends BaseTabbedDialog implements IItemListener {

    private static final String CREATE_BUTTON = "CREATE_BUTTON";

    private AdminRecord admin;
    private ItemTypeBoolean createSelectProject;
    private ItemTypeBoolean createSelectConfig;
    private ItemTypeFile createDirectory;


    public BackupDialog(Frame parent, AdminRecord admin) {
        super(parent,
                International.getStringWithMnemonic("Backup"),
                International.getStringWithMnemonic("Schließen"),
                null, true);
        iniItems(admin);
    }

    public BackupDialog(JDialog parent, AdminRecord admin) {
        super(parent,
                International.getStringWithMnemonic("Backup"),
                International.getStringWithMnemonic("Schließen"),
                null, true);
        iniItems(admin);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void iniItems(AdminRecord admin) {
        this.admin = admin;
        Vector<IItemType> guiItems = new Vector<IItemType>();
        String cat;
        IItemType item;

        // Create Backup
        cat = "%01%" + International.getString("Backup erstellen");

        guiItems.add(item = new ItemTypeLabel("CREATE_TITLE",
                IItemType.TYPE_PUBLIC, cat, International.getString("Backup erstellen")));

        guiItems.add(item = new ItemTypeBoolean("CREATE_SELECT_PROJECT", true,
                IItemType.TYPE_PUBLIC, cat, International.getMessage("{typeOfData} sichern",
                International.getMessage("Projekt '{name}'", Daten.project.getProjectName()))));
        createSelectProject = (ItemTypeBoolean)item;

        guiItems.add(item = new ItemTypeBoolean("CREATE_SELECT_CONFIG", true,
                IItemType.TYPE_PUBLIC, cat, International.getMessage("{typeOfData} sichern",
                International.getString("Konfiguration"))));
        createSelectConfig = (ItemTypeBoolean)item;

        guiItems.add(item = new ItemTypeFile("CREATE_DIRECTORY", Daten.efaBakDirectory,
                    International.getString("Verzeichnis"),
                    International.getString("Verzeichnisse"),
                    null, ItemTypeFile.MODE_OPEN, ItemTypeFile.TYPE_DIR,
                    IItemType.TYPE_PUBLIC, cat,
                    International.getString("Backupverzeichnis")));
        createDirectory = (ItemTypeFile)item;
        createDirectory.setNotNull(true);

        guiItems.add(item = new ItemTypeButton(CREATE_BUTTON,
                IItemType.TYPE_PUBLIC, cat, International.getString("Backup erstellen")));
        ((ItemTypeButton)item).registerItemListener(this);
        item.setFieldGrid(3, GridBagConstraints.CENTER, GridBagConstraints.NONE);

        super.setItems(guiItems);
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType.getName().equals(CREATE_BUTTON) && event instanceof ActionEvent) {
            getValuesFromGui();
            if (true) {
                Backup.runAsTask(this, 
                        createDirectory.getValue(), 
                        createSelectProject.getValue(), 
                        createSelectConfig.getValue());
            }
        }
    }

}
