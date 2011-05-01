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

public abstract class DataListDialog extends BaseDialog implements IItemListenerActionTable {

    private Persistence persistence;
    private long validAt;
    private Vector<DataRecord> records;

    private ItemTypeDataRecordTable table;
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
        updateGui();
    }

    public void updateGui() {
        table = new ItemTypeDataRecordTable("TABLE",
                persistence.createNewRecord().getGuiTableHeader(),
                persistence,
                records,
                null, // default actions: new, edit, delete
                this,
                IItemType.TYPE_PUBLIC, "BASE_CAT", getTitle());
        table.setFieldSize(800, 600);
        table.setPadding(0, 0, 20, 0);
        table.displayOnGui(this, mainPanel, BorderLayout.CENTER);
        this.validate();
    }

    public void setPersistence(Persistence persistence, long validAt) {
        this.persistence = persistence;
        this.validAt = validAt;

        try {
            long myValidAt = (validAt >= 0 ? validAt : System.currentTimeMillis());
            records = new Vector<DataRecord>();
            DataKeyIterator it = persistence.data().getStaticIterator();
            DataKey key = it.getFirst();
            while (key != null) {
                DataRecord r;
                if (persistence.data().getMetaData().isVersionized()) {
                    r = persistence.data().getValidAt(key, myValidAt);
                } else {
                    r = persistence.data().get(key);
                }
                if (r != null) {
                    records.add(r);
                }
                key = it.getNext();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
    }

    public void itemListenerActionTable(int actionId, DataRecord[] records) {
        // usually nothing to be done (handled in ItemTypeDataRecordTable itself).
        // override if necessary
    }


}
