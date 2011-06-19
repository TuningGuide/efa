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

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class SimpleInputDialog extends BaseDialog {

    private IItemType item;

    public SimpleInputDialog(Frame parent, String title, IItemType item) {
        super(parent, title, International.getStringWithMnemonic("OK"));
        this.item = item;
    }

    public SimpleInputDialog(JDialog parent, String title, IItemType item) {
        super(parent, title, International.getStringWithMnemonic("OK"));
        this.item = item;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        // create GUI items
        mainPanel.setLayout(new GridBagLayout());

        int y=0;
        if (item.getDescription().endsWith("?")) {
            ItemTypeLabel label = new ItemTypeLabel("LABEL", IItemType.TYPE_PUBLIC, "", item.getDescription());
            y += label.displayOnGui(this, mainPanel, 0, y);
            item.setDescription(null);
        }

        item.displayOnGui(this, mainPanel, 0, y);
        item.requestFocus();
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        item.getValueFromGui();
        if (!item.isValidInput()) {
            Dialog.error(International.getMessage("Ungültige oder fehlende Eingabe im Feld '{field}'.", item.getDescription()));
            item.requestFocus();
            return;
        }
        setDialogResult(true);
        super.closeButton_actionPerformed(e);
    }

    public static boolean showInputDialog(JDialog parent, String title, IItemType item) {
        SimpleInputDialog dlg = new SimpleInputDialog(parent, title, item);
        dlg.showDialog();
        return dlg.resultSuccess;
    }

    public static boolean showInputDialog(JFrame parent, String title, IItemType item) {
        SimpleInputDialog dlg = new SimpleInputDialog(parent, title, item);
        dlg.showDialog();
        return dlg.resultSuccess;
    }

}
