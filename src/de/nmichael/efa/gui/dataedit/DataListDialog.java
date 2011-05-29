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
import de.nmichael.efa.gui.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public abstract class DataListDialog extends BaseDialog implements IItemListener, IItemListenerDataRecordTable {

    private Persistence persistence;
    private long validAt;

    protected String[] actionText;
    protected int[] actionType;
    private ItemTypeDataRecordTable table;
    private ItemTypeDateTime validAtDateTime;
    private ItemTypeBoolean showAll;
    private ItemTypeBoolean showDeleted;
    private JPanel tablePanel;
    private JPanel buttonPanel;
    private Hashtable<ItemTypeButton,String> actionButtons;

    public DataListDialog(Frame parent, String title, Persistence persistence, long validAt) {
        super(parent, title, International.getStringWithMnemonic("Schließen"));
        setPersistence(persistence, validAt);
    }

    public DataListDialog(JDialog parent, String title, Persistence persistence, long validAt) {
        super(parent, title, International.getStringWithMnemonic("Schließen"));
        setPersistence(persistence, validAt);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new BorderLayout());
        
        JPanel mainTablePanel = new JPanel();
        mainTablePanel.setLayout(new BorderLayout());
        table = new ItemTypeDataRecordTable("TABLE",
                persistence.createNewRecord().getGuiTableHeader(),
                persistence, validAt,
                null, null,
                actionText, actionType, // default actions: new, edit, delete
                this,
                IItemType.TYPE_PUBLIC, "BASE_CAT", getTitle());
        table.setFieldSize(800, 600);
        table.setPadding(0, 0, 10, 0);
        table.displayOnGui(this, mainTablePanel, BorderLayout.CENTER);

        if (persistence != null && persistence.data().getMetaData().isVersionized()) {
            JPanel mainControlPanel = new JPanel();
            mainControlPanel.setLayout(new GridBagLayout());
            validAtDateTime = new ItemTypeDateTime("VALID_AT",
                    (validAt < 0 ? null : new DataTypeDate(validAt)),
                    (validAt < 0 ? null : new DataTypeTime(validAt)),
                    IItemType.TYPE_PUBLIC, "", International.getString("zeige Datensätze gültig am"));
            validAtDateTime.setNotNull(true);
            validAtDateTime.setPadding(0, 0, 10, 0);
            validAtDateTime.displayOnGui(this, mainControlPanel, 0, 0);
            validAtDateTime.registerItemListener(this);
            showAll = new ItemTypeBoolean("SHOW_ALL",
                    false,
                    IItemType.TYPE_PUBLIC, "", International.getString("auch derzeit ungültige Datensätze zeigen"));
            showAll.setPadding(0, 0, 0, 0);
            showAll.displayOnGui(this, mainControlPanel, 0, 1);
            showAll.registerItemListener(this);
            showDeleted = new ItemTypeBoolean("SHOW_DELETED",
                    false,
                    IItemType.TYPE_PUBLIC, "", International.getString("auch gelöschte Datensätze zeigen"));
            showDeleted.setPadding(0, 0, 0, 0);
            showDeleted.displayOnGui(this, mainControlPanel, 0, 2);
            showDeleted.registerItemListener(this);

            mainPanel.add(mainControlPanel, BorderLayout.NORTH);
        }
        mainPanel.add(mainTablePanel, BorderLayout.CENTER);

        this.validate();
    }

    public void setPersistence(Persistence persistence, long validAt) {
        this.persistence = persistence;
        this.validAt = validAt;
    }

    public void itemListenerActionTable(int actionId, DataRecord[] records) {
        // usually nothing to be done (handled in ItemTypeDataRecordTable itself).
        // override if necessary
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == validAtDateTime) {
            if (event.getID() == FocusEvent.FOCUS_LOST ||
                (event.getID() == KeyEvent.KEY_PRESSED && ((KeyEvent)event).getKeyCode() == KeyEvent.VK_ENTER)) {
                validAtDateTime.getValueFromGui();
                validAtDateTime.parseAndShowValue(validAtDateTime.getValueFromField());
                long _validAt = (validAtDateTime.isSet() ? validAtDateTime.getTimeStamp() : -1);
                if (_validAt != validAt) {
                    validAt = _validAt;
                    showDeleted.getValueFromGui();
                    showAll.getValueFromGui();
                    table.setAndUpdateData(validAt, showAll.getValue(), showDeleted.getValue());
                    table.showValue();
                }
            }
        }
        if (itemType == showAll || itemType == showDeleted) {
            if (event.getID() == ActionEvent.ACTION_PERFORMED) {
                showAll.getValueFromGui();
                showDeleted.getValueFromGui();
                table.setAndUpdateData(validAt, showAll.getValue(), showDeleted.getValue());
                table.showValue();
            }
        }
    }


}
