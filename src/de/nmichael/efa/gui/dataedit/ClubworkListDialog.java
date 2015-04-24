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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeBoolean;
import de.nmichael.efa.core.items.ItemTypeDataRecordTable;
import de.nmichael.efa.data.Clubwork;
import de.nmichael.efa.data.ClubworkRecord;
import de.nmichael.efa.data.ProjectRecord;
import de.nmichael.efa.data.storage.DataRecord;
import de.nmichael.efa.data.storage.StorageObject;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeTime;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.util.International;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;


// @i18n complete
public class ClubworkListDialog extends DataListDialog {

    public static final int ACTION_CARRYOVER = 4;
    public static final int ACTION_APPROVE = 5;

    protected ClubworkItemTypeDataRecordTable table;
    protected JPanel mainTablePanel;
    private ItemTypeBoolean showNonApproved;

    public ClubworkListDialog(Frame parent, AdminRecord admin) {
        super(parent, International.getString("Vereinsarbeit"), Daten.project.getCurrentClubwork(), 0, admin);
        iniValues();
    }

    public ClubworkListDialog(JDialog parent, AdminRecord admin) {
        super(parent, International.getString("Vereinsarbeit"), Daten.project.getCurrentClubwork(), 0, admin);
        iniValues();
    }

    private void iniValues() {
        super.sortByColumn = 2;
        super.sortAscending = false;
        super.filterFieldName = "Flag";
        super.filterFieldValue = ""+ClubworkRecord.Flags.Normal.ordinal();
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniActions() {
        if(admin == null) {
            actionText = new String[] {
                    International.getString("Erfassen")
            };

            actionType = new int[] {
                    ItemTypeDataRecordTable.ACTION_NEW
            };

            actionImage = new String[] {
                    BaseDialog.IMAGE_ADD
            };
        }
        else {
            actionText = new String[] {
                    ItemTypeDataRecordTable.ACTIONTEXT_NEW,
                    ItemTypeDataRecordTable.ACTIONTEXT_EDIT,
                    ItemTypeDataRecordTable.ACTIONTEXT_DELETE,
                    International.getString("Liste ausgeben"),
                    International.getString("Übertrag berechnen"),
                    International.getString("Neue Einträge annehmen")
            };

            actionType = new int[] {
                    ItemTypeDataRecordTable.ACTION_NEW,
                    ItemTypeDataRecordTable.ACTION_EDIT,
                    ItemTypeDataRecordTable.ACTION_DELETE,
                    ACTION_PRINTLIST,
                    ACTION_CARRYOVER,
                    ACTION_APPROVE
            };

            actionImage = new String[] {
                    BaseDialog.IMAGE_ADD,
                    BaseDialog.IMAGE_EDIT,
                    BaseDialog.IMAGE_DELETE,
                    BaseDialog.IMAGE_LIST,
                    BaseDialog.IMAGE_MERGE,
                    BaseDialog.IMAGE_ACCEPT
            };
        }
	}

    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new BorderLayout());

        mainTablePanel = new JPanel();
        mainTablePanel.setLayout(new BorderLayout());

        if (filterFieldDescription != null) {
            JLabel filterName = new JLabel();
            filterName.setText(filterFieldDescription);
            filterName.setHorizontalAlignment(SwingConstants.CENTER);
            mainTablePanel.add(filterName, BorderLayout.NORTH);
            mainTablePanel.setBorder(new EmptyBorder(10,0,0,0));
        }

        table = new ClubworkItemTypeDataRecordTable("TABLE",
                ((ClubworkRecord)persistence.createNewRecord()).getGuiTableHeader(admin),
                persistence, validAt, admin,
                filterFieldName, filterFieldValue, // defaults are null
                actionText, actionType, actionImage, // default actions: new, edit, delete
                this,
                IItemType.TYPE_PUBLIC, "BASE_CAT", getTitle());
        table.setSorting(sortByColumn, sortAscending);
        table.setFontSize(tableFontSize);
        table.setMarkedCellColor(markedCellColor);
        table.setMarkedCellBold(markedCellBold);
        table.disableIntelligentColumnWidth(!intelligentColumnWidth);
        if (minColumnWidth > 0) {
            table.setMinColumnWidth(minColumnWidth);
        }
        if (minColumnWidths != null) {
            table.setMinColumnWidths(minColumnWidths);
        }
        table.setButtonPanelPosition(buttonPanelPosition);
        table.setFieldSize(600, 500);
        table.setPadding(0, 0, 10, 0);
        table.displayOnGui(this, mainTablePanel, BorderLayout.CENTER);

        boolean hasEditAction = false;
        for (int i=0; actionType != null && i < actionType.length; i++) {
            if (actionType[i] == ItemTypeDataRecordTable.ACTION_EDIT) {
                hasEditAction = true;
            }
        }
        if (!hasEditAction) {
            table.setDefaultActionForDoubleclick(-1);
        }

        this.iniControlPanel();
        mainPanel.add(mainTablePanel, BorderLayout.CENTER);

        setRequestFocus(table);
        this.validate();
    }

    protected void iniControlPanel() {
        if (persistence != null && admin != null) {
            JPanel mainControlPanel = new JPanel();
            mainControlPanel.setLayout(new GridBagLayout());

            ProjectRecord r = Daten.project.getCurrentClubwork().getProjectRecord();
            DataTypeDate approvedDate = new DataTypeDate(r.getClubworkApprovedLong());
            DataTypeTime approvedTime = new DataTypeTime(r.getClubworkApprovedLong());

            String lastApprovedMsg = approvedDate != null ? " ("+International.getMessage("zuletzt kontrolliert am {date}", approvedDate+" "+approvedTime)+")" : "";

            showNonApproved = new ItemTypeBoolean("SHOW_NONAPPROVED",
                    false,
                    IItemType.TYPE_PUBLIC, "", International.getString("nur nicht kontrollierte Einträge anzeigen")+ lastApprovedMsg);
            showNonApproved.setPadding(0, 0, 0, 0);
            showNonApproved.displayOnGui(this, mainControlPanel, 0, 0);
            showNonApproved.registerItemListener(this);

            mainTablePanel.add(mainControlPanel, BorderLayout.SOUTH);
        }
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, StorageObject persistence, DataRecord record) {
        boolean newRecord = (record == null);
        if (record == null) {
            record = Daten.project.getClubwork(Daten.project.getCurrentClubwork().getName(), false).createClubworkRecord(UUID.randomUUID());
        }
        return new ClubworkEditDialog(parent, (ClubworkRecord)record, newRecord, admin);
    }

    public void itemListenerActionTable(int actionId, DataRecord[] records) {
        if(actionId == ACTION_CARRYOVER) {
            Clubwork clubwork = Daten.project.getCurrentClubwork();
            clubwork.doCarryOver(this);
        }
        else if(actionId == ACTION_APPROVE) {
            Clubwork clubwork = Daten.project.getCurrentClubwork();

            long l = 0;
            try {
                l = Daten.project.data().acquireLocalLock(Daten.project.getClubworkBookRecordKey(clubwork.getName()));
                ProjectRecord r = clubwork.getProjectRecord();
                r.setClubworkApprovedLong(System.currentTimeMillis());
                Daten.project.data().update(r, l);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Daten.project.data().releaseLocalLock(l);
            }
        }
        else {
            super.itemListenerActionTable(actionId, records);
        }
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == showNonApproved) {
            if (event.getID() == ActionEvent.ACTION_PERFORMED) {
                showNonApproved.getValueFromGui();
                if (showNonApproved.getValue()) {
                    showNonApproved.saveColor();
                    showNonApproved.setColor(Color.gray);
                } else {
                    showNonApproved.restoreColor();
                }

                table.showOnlyNonApproved(showNonApproved.getValue());
                table.showValue();
            }
        }
    }
}
