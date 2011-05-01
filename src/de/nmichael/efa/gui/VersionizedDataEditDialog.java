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
import de.nmichael.efa.gui.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class VersionizedDataEditDialog extends DataEditDialog implements IItemListener {

    // private ItemTypeHtmlList versionList;
    private ItemTypeTable versionList;
    private Hashtable<Integer,DataRecord> versions;
    private JLabel selectedVersionLabel;
    protected DataRecord dataRecord;
    protected int thisVersion;

    public VersionizedDataEditDialog(Frame parent, String title, DataRecord dataRecord) {
        super(parent, title, dataRecord.getGuiItems());
        this.dataRecord = dataRecord;
    }

    public VersionizedDataEditDialog(JDialog parent, String title, DataRecord dataRecord) {
        super(parent, title, dataRecord.getGuiItems());
        this.dataRecord = dataRecord;
    }
    
    protected void iniDialog() throws Exception {
        super.iniDialog();

        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new GridBagLayout());

        JLabel versionLabel = new JLabel();
        Mnemonics.setLabel(this, versionLabel, International.getString("Versionen"));
        versionPanel.add(versionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));

        //versionList = new ItemTypeHtmlList("VERSION_LIST", null, null, null, IItemType.TYPE_PUBLIC, null, International.getString("Versionen"));
        versionList = new ItemTypeTable("VERSION_LIST", new String[] {
            International.getString("Version"),
            International.getString("gültig von"),
            International.getString("gültig bis") },
            null, null, IItemType.TYPE_PUBLIC, null, International.getString("Versionen"));
        String[] actions = {
            International.getString("Auswählen"),
            International.getString("Neu"),
            International.getString("Löschen")
        };
        versionList.setPopupActions(actions);
        versionList.registerItemListener(this);
        versionList.setFieldGrid(1, 3, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        versionList.setPadding(10, 10, 0, 10);
        versionList.setFieldSize(500, 100);
        versionList.displayOnGui(_parent, versionPanel, 0, 1);

        JButton selectButton = new JButton();
        Mnemonics.setButton(this, selectButton, International.getString("Auswählen"));
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectButton_actionPerformed(e, false);
            }
        });
        versionPanel.add(selectButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0));

        JButton newButton = new JButton();
        Mnemonics.setButton(this, newButton, International.getString("Neu"));
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newButton_actionPerformed(e, false);
            }
        });
        versionPanel.add(newButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0));

        JButton deleteButton = new JButton();
        Mnemonics.setButton(this, deleteButton, International.getString("Löschen"));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteButton_actionPerformed(e, false);
            }
        });
        versionPanel.add(deleteButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0));

        selectedVersionLabel = new JLabel();
        Mnemonics.setLabel(this, selectedVersionLabel, International.getString("Version"));
        versionPanel.add(selectedVersionLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 5, 0), 0, 0));


        mainPanel.add(versionPanel, BorderLayout.NORTH);
        updateGui();
    }

    public void updateGui() {
        if (versionList == null) {
            return; // don't update yet, wait for iniDialog() to run ...
        }
        super.updateGui();

        //Hashtable<String,String> items = new Hashtable<String,String>();
        Hashtable<String,TableItem[]> items = new Hashtable<String,TableItem[]>();

        String curValue = null;
        if (dataRecord != null) {
            try {
                DataRecord[] recs = dataRecord.getPersistence().data().getValidAny(dataRecord.getKey());
                Arrays.sort(recs);
                versions = new Hashtable<Integer,DataRecord>();
                for (int i=0; i<recs.length; i++) {
                    DataRecord r = recs[i];
                    //StringBuilder content = new StringBuilder();
                    //String boldStart = (r.getKey().equals(dataRecord.getKey()) ? "<b style=\"font-color:blue\">" : "");
                    //String boldEnd = (r.getKey().equals(dataRecord.getKey()) ? "</b>" : "");
                    //String bgcol = (r.getKey().equals(dataRecord.getKey()) ? " style=\"background-color:#ffffaa;\"" : "");
                    //content.append("<table width=\"100%\" style=\"border-width:1px; font-family:sans-serif; font-size:12pt; padding:0px; margin:0px;\"><tr>");
                    //content.append("<td width=\"20%\"" + bgcol + " cellpadding=\"0\">" + boldStart + International.getString("Version") + " " + (i+1) + boldEnd + "</td>");
                    //content.append("<td width=\"38%\"" + bgcol + " cellpadding=\"0\">" + boldStart + r.getValidFromTimeString() + boldEnd + "</td>");
                    //content.append("<td width=\"4%\"" + bgcol + " cellpadding=\"0\">" + boldStart + "-" + boldEnd + "</td>");
                    //content.append("<td width=\"38%\"" + bgcol + " cellpadding=\"0\">" + boldStart + r.getValidUntilTimeString() + boldEnd + "</td>");
                    //content.append("</table>");
                    String key = getListKey(i);
                    //items.put(key, content.toString());
                    boolean selected = r.getKey().equals(dataRecord.getKey());
                    TableItem[] content = new TableItem[3];
                    content[0] = new TableItem(Integer.toString(i+1), selected);
                    content[1] = new TableItem(r.getValidFromTimeString(), selected);
                    content[2] = new TableItem(r.getValidUntilTimeString(), selected);
                    items.put(key, content);
                    versions.put(i, r);
                    if (r.getKey().equals(dataRecord.getKey())) {
                        curValue = key;
                        thisVersion = i;
                    }
                }
            } catch(Exception e) {
                Logger.logdebug(e);
            }

        }
        
        versionList.setValues(items);
        versionList.parseAndShowValue(curValue);
        setSelectedVersionLabel();
    }


    public void itemListenerAction(IItemType item, AWTEvent event) {
        if (item != null && event != null && item == versionList) {
            if (event instanceof ActionEvent) {
                ActionEvent e = (ActionEvent)event;
                String cmd = e.getActionCommand();
                if (cmd != null && cmd.equals(EfaMouseListener.EVENT_MOUSECLICKED_2x)) {
                    selectButton_actionPerformed(e, true);
                }
                if (cmd != null && cmd.startsWith(EfaMouseListener.EVENT_POPUP_CLICKED)) {
                    int id = EfaUtil.string2date(cmd, -1, -1, -1).tag;
                    switch(id) {
                        case 0:
                            selectButton_actionPerformed(e, true);
                            break;
                        case 1:
                            newButton_actionPerformed(e, true);
                            break;
                        case 2:
                            deleteButton_actionPerformed(e, true);
                            break;
                    }

                }
            }
        }
    }

    String getListKey(int i) {
        return EfaUtil.int2String(i, 5);
    }

    void showRecord(DataRecord r) {
        this.dataRecord = r;
        setItems(dataRecord.getGuiItems());
        for (IItemType item : getItems()) {
            item.setUnchanged();
        }
        updateGui();
    }

    boolean saveRecord() {
        try {
            dataRecord.saveGuiItems(getItems());
            dataRecord.getPersistence().data().update(dataRecord);
            for(IItemType item : getItems()) {
                item.setUnchanged();
            }
            return true;
        } catch(Exception e) {
            Logger.logdebug(e);
            Dialog.error("Die Änderungen konnten nicht gespeichert werden." + "\n" + e.toString());
            return false;
        }
    }

    boolean checkAndSaveChanges() {
        if (getValuesFromGui()) {
            switch (Dialog.yesNoCancelDialog(International.getString("Änderungen speichern"),
                                             International.getString("Die Daten wurden verändert.")+"\n"+
                                             International.getString("Möchtest Du die Änderungen jetzt speichern?"))) {
                case Dialog.YES:
                    return saveRecord();
                case Dialog.NO:
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    boolean createRecord(DataRecord r, long newValidFrom) {
        try {
            // create version
            DataKey key = dataRecord.getPersistence().data().addValidAt(r, newValidFrom);

            // find new record to be shown (if any)
            dataRecord = dataRecord.getPersistence().data().get(key);
            return true;
        } catch(Exception e) {
            Logger.logdebug(e);
            Dialog.error("Der Datensatz konnte nicht erstellt werden." + "\n" + e.toString());
            return false;
        }
    }

    boolean deleteRecord(DataRecord r, int merge) {
        try {
            // find current record position in version list
            DataRecord[] recs = dataRecord.getPersistence().data().getValidAny(dataRecord.getKey());
            Arrays.sort(recs);
            int ri = -1;
            for (int i=0; i<recs.length; i++) {
                if (r.getKey().equals(recs[i].getKey())) {
                    ri = i; 
                }
            }

            // delete version
            dataRecord.getPersistence().data().deleteVersionized(r.getKey(), merge);

            // find new record to be shown (if any)
            recs = dataRecord.getPersistence().data().getValidAny(dataRecord.getKey());
            if (recs == null) {
                dataRecord = null;
                return true;
            }
            Arrays.sort(recs);
            ri = (merge == -1 ? ri-1 : ri);
            if (ri < 0 || ri >= recs.length) {
                dataRecord = null;
            } else {
                dataRecord = recs[ri];
            }
            return true;
        } catch(Exception e) {
            Logger.logdebug(e);
            Dialog.error("Der Datensatz konnte nicht gelöscht werden." + "\n" + e.toString());
            return false;
        }
    }

    void setSelectedVersionLabel() {
        if (versionList != null) {
            int v = EfaUtil.stringFindInt(versionList.getValueFromField(), -1);
            if (v >= 0) {
                DataRecord r = versions.get(v);
                if (r != null) {
                    selectedVersionLabel.setText(International.getString("Version") + " " + (v + 1) + ": " + r.getValidRangeString());
                }
            }
        }
    }

    void selectButton_actionPerformed(ActionEvent e, boolean fromPopupMenu) {
        if (!checkAndSaveChanges()) {
            return;
        }
        int v = EfaUtil.stringFindInt(versionList.getValueFromField(), -1);
        if (v >= 0) {
            DataRecord r = versions.get(v);
            if (r != null) {
                showRecord(r);
            }
        }
    }

    void newButton_actionPerformed(ActionEvent e, boolean fromPopupMenu) {
        if (!checkAndSaveChanges()) {
            return;
        }
        int v = EfaUtil.stringFindInt(versionList.getValueFromField(), -1);
        if (v >= 0) {
            DataRecord r = versions.get(v);
            if (r != null) {
                VersionizedDataCreateVersionDialog dlg = new VersionizedDataCreateVersionDialog(this, r, v+1);
                dlg.showDialog();
                long newValidFrom = dlg.getValidFromResult();
                if (newValidFrom > r.getValidFrom() && newValidFrom < r.getInvalidFrom()) {
                    createRecord(r, newValidFrom);
                    if (dataRecord != null) {
                        showRecord(dataRecord);
                    }
                }
            }
        }
    }

    void deleteButton_actionPerformed(ActionEvent e, boolean fromPopupMenu) {
        int v = EfaUtil.stringFindInt(versionList.getValueFromField(), -1);
        if (v != thisVersion && !checkAndSaveChanges()) {
            return;
        }
        if (v >= 0) {
            DataRecord r = versions.get(v);
            if (r != null) {
                switch (Dialog.yesNoDialog(International.getString("Version löschen"),
                                           International.getMessage("Möchtest Du Version {version} dieses Datensatzes wirklich löschen?", (v+1)))) {
                    case Dialog.YES:
                        int versionCnt = versions.keySet().size();
                        int prevVersion = (v > 0 ? v-1 : -1);
                        int nextVersion = (v < versionCnt-1 ? v+1 : -1);
                        int merge;
                        if (prevVersion >= 0 && nextVersion >= 0) {
                            switch(Dialog.auswahlDialog(International.getString("Version auswählen"),
                                                 International.getString("Welche Version dieses Datensatzes soll den Gültigkeitszeitraum der gelöschten Version übernehmen?"),
                                                 International.getString("Version") + " " + (prevVersion+1),
                                                 International.getString("Version") + " " + (nextVersion+1),
                                                 true)) {
                                case 0:
                                    merge = -1;
                                    break;
                                case 1:
                                    merge = 1;
                                    break;
                                default:
                                    return;
                            }
                        } else {
                            if (prevVersion == -1) {
                                merge = 1;
                            } else {
                                merge = -1;
                            }
                        }
                        if (prevVersion < 0 && nextVersion < 0) {
                            if (Dialog.yesNoDialog(International.getString("Datensatz löschen"),
                                                   International.getString("Dies ist die letzte Version dieses Datensatzes. "+
                                                                           "Wenn Du diese Version löschst, wird damit der gesamte Datensatz gelöscht.") + "\n"+
                                                   International.getString("Möchtest Du diesen Datensatz wirklich löschen?")) != Dialog.YES) {
                                return;
                            }
                            // don't delete last reference to a record, but just mark it as deleted
                            dataRecord.setDeleted(true);
                            saveRecord();
                            return;
                        }

                        if (!deleteRecord(r, merge)) {
                            return;
                        }
                        if (dataRecord != null) {
                            showRecord(dataRecord);
                        } else {
                            cancel();
                        }
                        break;
                    default:
                        return;
                }
            }
        }
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        if (getValuesFromGui()) {
            saveRecord();
        }
        super.closeButton_actionPerformed(e);
    }

    public boolean cancel() {
        if (!checkAndSaveChanges()) {
            return false;
        }
        return super.cancel();
    }
    

}
