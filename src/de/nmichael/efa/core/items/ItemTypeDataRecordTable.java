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

import de.nmichael.efa.gui.dataedit.VersionizedDataDeleteDialog;
import de.nmichael.efa.gui.dataedit.DataEditDialog;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// @i18n complete

public class ItemTypeDataRecordTable extends ItemTypeTable implements IItemListener {

    public static final int ACTION_NEW    = 0;
    public static final int ACTION_EDIT   = 1;
    public static final int ACTION_DELETE = 2;
    public static final int ACTION_OTHER = -1;

    public static final String ACTIONTEXT_NEW    = International.getString("Neu");
    public static final String ACTIONTEXT_EDIT   = International.getString("Bearbeiten");
    public static final String ACTIONTEXT_DELETE = International.getString("Löschen");

    private static final String[] DEFAULT_ACTIONS = new String[] {
        ACTIONTEXT_NEW,
        ACTIONTEXT_EDIT,
        ACTIONTEXT_DELETE
    };

    protected Persistence persistence;
    protected long validAt = -1; // configured validAt
    protected long myValidAt = -1; // actually used validAt in updateData(); if validAt == -1, then myValidAt is "now" each time the data is updated
    protected boolean showAll = false;
    protected boolean showDeleted = false;
    protected String filterFieldName;
    protected String filterFieldValue;
    
    protected Vector<DataRecord> data;
    protected Hashtable<String,DataRecord> mappingKeyToRecord;
    protected IItemListenerDataRecordTable itemListenerActionTable;

    protected JPanel myPanel;
    protected JPanel tablePanel;
    protected JPanel buttonPanel;

    protected Hashtable<ItemTypeButton,String> actionButtons;
    protected static final String ACTION_BUTTON = "ACTION_BUTTON";
    protected int[] actionTypes;

    public ItemTypeDataRecordTable(String name,
            TableItemHeader[] tableHeader, 
            Persistence persistence,
            long validAt,
            String filterFieldName, String filterFieldValue,
            String[] actions, int[] actionTypes,
            IItemListenerDataRecordTable itemListenerActionTable,
            int type, String category, String description) {
        super(name, tableHeader, null, null, type, category, description);
        setData(persistence, validAt, filterFieldName, filterFieldValue);
        setActions(actions, actionTypes);
        this.itemListenerActionTable = itemListenerActionTable;
        renderer = new de.nmichael.efa.gui.util.TableCellRenderer();
        renderer.setMarkedBold(false);
        renderer.setMarkedForegroundColor(Color.red);
        renderer.setMarkedBackgroundColor(null);
    }

    protected void setData(Persistence persistence, long validAt, String filterFieldName, String filterFieldValue) {
        this.persistence = persistence;
        this.validAt = validAt;
        this.filterFieldName = filterFieldName;
        this.filterFieldValue = filterFieldValue;
    }

    public void setAndUpdateData(long validAt, boolean showAll, boolean showDeleted) {
        this.validAt = validAt;
        this.showAll = showAll;
        this.showDeleted = showDeleted;
        updateData();
    }

    public void setActions(String[] actions, int[] actionTypes) {
        if (actions == null || actionTypes == null) {
            super.setPopupActions(DEFAULT_ACTIONS);
            this.actionTypes = new int[] { ACTION_NEW, ACTION_EDIT, ACTION_DELETE };
        } else {
            super.setPopupActions(actions);
            this.actionTypes = actionTypes;
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
            String action = ACTION_BUTTON + "_" + actionTypes[i];
            ItemTypeButton button = new ItemTypeButton(action, IItemType.TYPE_PUBLIC, "BUTTON_CAT", popupActions[i]);
            button.registerItemListener(this);
            button.setPadding(20, 20, 0, 5);
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
        if (data == null && persistence != null) {
            updateData();
        }
        boolean isVersionized = persistence.data().getMetaData().isVersionized();
        for (int i=0; data != null && i<data.size(); i++) {
            DataRecord r = data.get(i);
            TableItem[] content = r.getGuiTableItems();

            // mark deleted records
            if (r.getDeleted()) {
                for (TableItem it : content) {
                    it.setMarked(true);
                }
            }

            // mark invalid records
            if (isVersionized && !r.isValidAt(myValidAt)) {
                for (TableItem it : content) {
                    it.setDisabled(true);
                }
            }
            items.put(r.getKey().toString(), content);
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
                try {
                    actionId = actionTypes[EfaUtil.stringFindInt(cmd, -1)];
                } catch(Exception eignore) {}
            }
            if (cmd != null && cmd.startsWith(EfaMouseListener.EVENT_MOUSECLICKED_2x)) {
                actionId = ACTION_EDIT;
            }
            if (itemType != null && itemType instanceof ItemTypeButton) {
                actionId = EfaUtil.stringFindInt(actionButtons.get((ItemTypeButton)itemType), -1);
            }
            if (actionId == -1) {
                return;
            }
            int[] rows = table.getSelectedRows();
            DataRecord[] records = null;
            if (rows != null && rows.length > 0) {
                records = new DataRecord[rows.length];
                for (int i=0; i<rows.length; i++) {
                    records[i] = mappingKeyToRecord.get(keys[rows[i]]);
                }
            }
            if (persistence != null && itemListenerActionTable != null) {
                DataEditDialog dlg;
                switch(actionId) {
                    case ACTION_NEW:
                        dlg = itemListenerActionTable.createNewDataEditDialog(getParentDialog(), persistence, null);
                        if (dlg == null) {
                            return;
                        }
                        dlg.showDialog();
                        break;
                    case ACTION_EDIT:
                        for (int i=0; records != null && i<records.length; i++) {
                            if (records[i] != null) {
                                if (records[i].getDeleted()) {
                                    switch(Dialog.yesNoCancelDialog(International.getString("Datensatz wiederherstellen"),
                                            International.getMessage("Der Datensatz '{record}' wurde gelöscht. Möchtest Du ihn wiederherstellen?", records[i].getQualifiedName()))) {
                                        case Dialog.YES:
                                            try {
                                                DataRecord[] rall = persistence.data().getValidAny(records[i].getKey());
                                                for (int j=0; rall != null && j<rall.length; j++) {
                                                    rall[j].setDeleted(false);
                                                    persistence.data().update(rall[j]);
                                                }
                                            } catch(Exception exr) {
                                                Dialog.error(exr.toString());
                                                return;
                                            }
                                            break;
                                        case Dialog.NO:
                                            continue;
                                        case Dialog.CANCEL:
                                            return;
                                    }
                                }
                                dlg = itemListenerActionTable.createNewDataEditDialog(getParentDialog(), persistence, records[i]);
                                if (dlg == null) {
                                    return;
                                }
                                dlg.showDialog();
                                if (!dlg.getDialogResult()) {
                                    break;
                                }
                            }
                        }
                        break;
                    case ACTION_DELETE:
                        if (records == null || records.length == 0) {
                            return;
                        }
                        int res = -1;
                        if (records.length == 1) {
                            res = Dialog.yesNoDialog(International.getString("Wirklich löschen?"),
                                    International.getMessage("Möchtest Du den Datensatz '{record}' wirklich löschen?", records[0].getQualifiedName()));
                        } else {
                            res = Dialog.yesNoDialog(International.getString("Wirklich löschen?"),
                                    International.getMessage("Möchtest Du {count} ausgewählte Datensätze wirklich löschen?", records.length));
                        }
                        if (res != Dialog.YES) {
                            return;
                        }
                        long deleteAt = Long.MAX_VALUE;
                        if (persistence.data().getMetaData().isVersionized()) {
                            VersionizedDataDeleteDialog ddlg = new VersionizedDataDeleteDialog(getParentDialog());
                            ddlg.showDialog();
                            deleteAt = ddlg.getDeleteAtResult();
                            if (deleteAt == Long.MAX_VALUE || deleteAt < -1) {
                                return;
                            }
                        }
                        try {
                            for (int i = 0; records != null && i < records.length; i++) {
                                if (records[i] != null) {
                                    if (persistence.data().getMetaData().isVersionized()) {
                                        persistence.data().deleteVersionizedAll(records[i].getKey(), deleteAt);
                                    } else {
                                        persistence.data().delete(records[i].getKey());
                                    }
                                }
                            }
                        } catch (EfaModifyException exmodify) {
                            exmodify.displayMessage();
                        } catch (Exception ex) {
                            Logger.logdebug(ex);
                            Dialog.error(ex.toString());
                        }
                        break;
                }
            }
            if (itemListenerActionTable != null) {
                itemListenerActionTable.itemListenerActionTable(actionId, records);
            }
            updateData();
            showValue();
        }
    }

    protected void updateData() {
        if (persistence == null) {
            return;
        }
        try {
            myValidAt = (validAt >= 0 ? validAt : System.currentTimeMillis());
            data = new Vector<DataRecord>();
            IDataAccess dataAccess = persistence.data();
            boolean isVersionized = dataAccess.getMetaData().isVersionized();
            DataKeyIterator it = dataAccess.getStaticIterator();
            DataKey key = it.getFirst();
            Hashtable<DataKey,String> uniqueHash = new Hashtable<DataKey,String>();
            while (key != null) {
                // avoid duplicate versionized keys for the same record
                if (isVersionized) {
                    DataKey ukey = dataAccess.getUnversionizedKey(key);
                    if (uniqueHash.get(ukey) != null) {
                        key = it.getNext();
                        continue;
                    }
                    uniqueHash.put(ukey, "");
                }

                DataRecord r;
                if (isVersionized) {
                    r = dataAccess.getValidAt(key, myValidAt);
                    if (r == null && showAll) {
                        r = dataAccess.getValidLatest(key);
                    }
                } else {
                    r = dataAccess.get(key);
                }
                if (r != null && (!r.getDeleted() || showDeleted)) {
                    if (filterFieldName == null || filterFieldValue == null ||
                        filterFieldValue.equals(r.getAsString(filterFieldName))) {
                        data.add(r);
                    }
                }
                key = it.getNext();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
    }

    public void actionPerformed(ActionEvent e) {
        itemListenerAction(this, e);
    }

}
