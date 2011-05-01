/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.data.storage.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// @i18n complete

public class ItemTypeDataRecordTable extends ItemTypeTable implements IItemListener {

    private static final int ACTION_NEW    = 0;
    private static final int ACTION_EDIT   = 1;
    private static final int ACTION_DELETE = 2;
    private static final String[] DEFAULT_ACTIONS = new String[] {
        International.getString("Neu"),
        International.getString("Bearbeiten"),
        International.getString("Löschen")
    };

    protected Persistence persistence;
    protected Vector<DataRecord> data;
    protected Hashtable<String,DataRecord> mappingKeyToRecord;
    protected IItemListenerActionTable itemListenerActionTable;

    protected JPanel myPanel;
    protected JPanel tablePanel;
    protected JPanel buttonPanel;

    protected Hashtable<ItemTypeButton,String> actionButtons;
    protected static final String ACTION_BUTTON = "ACTION_BUTTON";
    protected boolean isDefaultActions = true;

    public ItemTypeDataRecordTable(String name,
            TableItemHeader[] tableHeader, 
            Persistence persistence,
            Vector<DataRecord> data,
            String[] actions,
            IItemListenerActionTable itemListenerActionTable,
            int type, String category, String description) {
        super(name, tableHeader, null, null, type, category, description);
        setData(persistence, data);
        setPopupActions(actions);
        this.itemListenerActionTable = itemListenerActionTable;
    }

    public ItemTypeDataRecordTable(String name,
            TableItemHeader[] tableHeader,
            Persistence persistence,
            DataRecord[] data,
            String[] actions,
            IItemListenerActionTable itemListenerActionTable,
            int type, String category, String description) {
        super(name, tableHeader, null, null, type, category, description);
        setData(persistence, data);
        setPopupActions(actions);
        this.itemListenerActionTable = itemListenerActionTable;
    }

    protected void setData(Persistence persistence, Vector<DataRecord> data) {
        this.persistence = persistence;
        this.data = data;
    }

    protected void setData(Persistence persistence, DataRecord[] data) {
        this.persistence = persistence;
        if (data != null) {
            this.data = new Vector<DataRecord>();
            for (int i=0; i<data.length; i++) {
                this.data.add(data[i]);
            }
        } else {
            this.data = null;
        }
    }

    public void setPopupActions(String[] actions) {
        if (actions == null) {
            super.setPopupActions(DEFAULT_ACTIONS);
            isDefaultActions = true;
        } else {
            super.setPopupActions(actions);
            isDefaultActions = false;
        }
    }

    protected void iniDisplayActionTable(Window dlg) {
        this.dlg = dlg;
        myPanel = new JPanel();
        myPanel.setLayout(new BorderLayout());
        tablePanel = new JPanel();
        tablePanel.setLayout(new GridBagLayout());
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        myPanel.add(tablePanel, BorderLayout.CENTER);
        myPanel.add(buttonPanel, BorderLayout.EAST);
        actionButtons = new Hashtable<ItemTypeButton,String>();
        for (int i=0; popupActions != null && i<popupActions.length; i++) {
            String action = ACTION_BUTTON + "_" + i;
            ItemTypeButton button = new ItemTypeButton(action, IItemType.TYPE_PUBLIC, "BUTTON_CAT", popupActions[i]);
            button.registerItemListener(this);
            button.setPadding(20, 0, 0, 5);
            button.setFieldSize(200, -1);
            button.displayOnGui(dlg, buttonPanel, 0, i);
            actionButtons.put(button, action);
        }
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        iniDisplayActionTable(dlg);
        panel.add(myPanel, new GridBagConstraints(x, y, fieldGridWidth, fieldGridHeight, 0.0, 0.0,
                fieldGridAnchor, fieldGridFill, new Insets(padYbefore, padXbefore, padYafter, padXafter), 0, 0));
        super.displayOnGui(dlg, tablePanel, 0, 0);
        return 1;
    }

    public int displayOnGui(Window dlg, JPanel panel, String borderLayoutPosition) {
        iniDisplayActionTable(dlg);
        panel.add(myPanel, borderLayoutPosition);
        super.displayOnGui(dlg, tablePanel, 0, 0);
        return 1;
    }

    public void showValue() {
        items = new Hashtable<String,TableItem[]>();
        mappingKeyToRecord = new Hashtable<String,DataRecord>();
        for (int i=0; data != null && i<data.size(); i++) {
            DataRecord r = data.get(i);
            items.put(r.getKey().toString(), r.getGuiTableItems());
            mappingKeyToRecord.put(r.getKey().toString(), r);
        }
        keys = items.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        super.showValue();
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (event != null && event instanceof ActionEvent && event.getID() == ActionEvent.ACTION_PERFORMED) {
            ActionEvent e = (ActionEvent)event;
            String cmd = e.getActionCommand();
            int actionId = -1;
            if (cmd != null && cmd.startsWith(EfaMouseListener.EVENT_POPUP_CLICKED)) {
                actionId = EfaUtil.stringFindInt(cmd, -1);
            }
            if (itemType != null && itemType instanceof ItemTypeButton) {
                actionId = EfaUtil.stringFindInt(actionButtons.get((ItemTypeButton)itemType), -1);
            }
            int[] rows = table.getSelectedRows();
            DataRecord[] records = null;
            if (rows != null && rows.length > 0) {
                records = new DataRecord[rows.length];
                for (int i=0; i<rows.length; i++) {
                    records[i] = mappingKeyToRecord.get(keys[rows[i]]);
                }
            }
            System.out.println("ActionId="+actionId);
            if (isDefaultActions && persistence != null && itemListenerActionTable != null) {
                DataEditDialog dlg;
                switch(actionId) {
                    case ACTION_NEW:
                        dlg = itemListenerActionTable.createNewDataEditDialog(getParentDialog(), null);
                        dlg.showDialog();
                        break;
                    case ACTION_EDIT:
                        for (int i=0; records != null && i<records.length; i++) {
                            if (records[i] != null) {
                                dlg = itemListenerActionTable.createNewDataEditDialog(getParentDialog(), records[i]);
                                dlg.showDialog();
                                if (!dlg.getDialogResult()) {
                                    break;
                                }
                            }
                        }
                        break;
                    case ACTION_DELETE:
                        break;
                }
                showValue();
            }
            if (itemListenerActionTable != null) {
                itemListenerActionTable.itemListenerActionTable(actionId, records);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        itemListenerAction(this, e);
    }

}
