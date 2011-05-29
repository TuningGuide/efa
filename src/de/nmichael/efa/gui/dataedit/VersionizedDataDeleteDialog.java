/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.dataedit;

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
public class VersionizedDataDeleteDialog extends BaseDialog implements IItemListener {

    private ItemTypeDateTime deleteAt;
    private ItemTypeBoolean deleteAll;
    private long deleteAtResult = Long.MAX_VALUE;

    public VersionizedDataDeleteDialog(Frame parent) {
        super(parent, International.getString("Daten löschen"), International.getStringWithMnemonic("Löschen"));
    }

    public VersionizedDataDeleteDialog(JDialog parent) {
        super(parent, International.getString("Daten löschen"), International.getStringWithMnemonic("Löschen"));
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        // create GUI items
        mainPanel.setLayout(new GridBagLayout());

        deleteAt = new ItemTypeDateTime("DELETE_AT",
                new DataTypeDate(System.currentTimeMillis()), new DataTypeTime(System.currentTimeMillis()),
                IItemType.TYPE_PUBLIC, "", International.getString("Datensätze als ungültig markieren ab") );
        deleteAt.displayOnGui(this, mainPanel, 0, 0);
        deleteAt.requestFocus();

        deleteAll = new ItemTypeBoolean("DELETE_ALL",
                false,
                IItemType.TYPE_PUBLIC, "", International.getString("Datensätze komplett löschen") );
        deleteAll.registerItemListener(this);
        deleteAll.displayOnGui(this, mainPanel, 0, 1);
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == deleteAll && event.getID() == ActionEvent.ACTION_PERFORMED) {
            deleteAll.getValueFromGui();
            deleteAt.setEnabled(!deleteAll.getValue());
        }
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        deleteAt.getValueFromGui();
        deleteAll.getValueFromGui();
        if (deleteAll.getValue()) {
            deleteAtResult = -1;
        } else {
            if (deleteAt.isSet()) {
                deleteAtResult = deleteAt.getTimeStamp();
            }
        }
        super.closeButton_actionPerformed(e);
    }

    public long getDeleteAtResult() {
        return deleteAtResult;
    }

}
