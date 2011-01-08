/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.types;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

// @i18n complete

public class ItemTypeFile extends ItemTypeString {
    
    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_DIR  = 2;

    private String fileItem;
    private String fileTypes;
    private String fileExtensions;
    private int fileOpenSave;
    private int fileOrDir;

    public ItemTypeFile(String name, String value,
            String fileItem, String fileTypes, String fileExtensions, int fileOpenSave, int fileOrDir,
            int type, String category, String description) {
        super(name,value,type,category,description);
        this.fileItem = fileItem;
        this.fileTypes = fileTypes;
        this.fileExtensions = fileExtensions;
        this.fileOpenSave = fileOpenSave;
        this.fileOrDir = fileOrDir;
    }

    public int displayOnGui(BaseDialog dlg, JPanel panel, int y) {
        this.dlg = dlg;

        textfield = new JTextField();
        textfield.setText(toString());
        Dialog.setPreferredSize(textfield, 300, 19);
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(textfield);
        if (type == IItemType.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }
        JButton button = new JButton();
        if (fileOpenSave == MODE_OPEN) {
            button.setIcon(new ImageIcon(de.nmichael.efa.Daten.class.getResource("/de/nmichael/efa/img/menu_open.gif")));
        } else {
            button.setIcon(new ImageIcon(de.nmichael.efa.Daten.class.getResource("/de/nmichael/efa/img/menu_save.gif")));
        }
        button.setMargin(new Insets(0,0,0,0));
        Dialog.setPreferredSize(button, 19, 19);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { buttonHit(e); }
        });
        textfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) { textfield_focusLost(e); }
        });
        panel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(textfield, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(button, new GridBagConstraints(2, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        return 1;
    }

    private void buttonHit(ActionEvent e) {
        String startDirectory = null;
        String selectedFile = null;
        String currentValue = textfield.getText().trim();

        if (currentValue.length() > 0) {
            startDirectory = EfaUtil.getPathOfFile(currentValue);
            selectedFile = EfaUtil.getNameOfFile(currentValue);
        }

        String file = Dialog.dateiDialog(dlg,
                International.getMessage("{item} auswählen", fileItem),
                fileTypes, fileExtensions, startDirectory, selectedFile, null, 
                fileOpenSave == MODE_SAVE, fileOrDir == TYPE_DIR);
        if (file != null) {
            textfield.setText(file);
        }
        
    }

    protected void textfield_focusLost(FocusEvent e) {
        getValueFromGui();
        String filename = toString().trim();
        if (fileOpenSave == MODE_OPEN && filename.length() > 0) {
            if (fileOrDir == TYPE_FILE && !(new File(filename)).isFile()) {
                Dialog.error(International.getMessage("{filedescription} '{filename}' existiert nicht",
                        International.getString("Datei"),filename)+".");
            }
            if (fileOrDir == TYPE_DIR && !(new File(filename)).isDirectory()) {
                Dialog.error(International.getMessage("{directorydescription} '{directoryname}' existiert nicht",
                        International.getString("Verzeichnis"),filename)+".");
            }
        }
        textfield.setText(filename);
    }

}
