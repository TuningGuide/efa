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
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class LogbookListDialog extends DataListDialog {

    private Logbook logbook;

    public LogbookListDialog(Frame parent, AdminRecord admin, Logbook logbook) {
        super(parent, International.getString("Fahrtenbuch") + " " + logbook.getName(),
                logbook, -1, admin);
        iniSettings();
    }

    public LogbookListDialog(JDialog parent, AdminRecord admin, Logbook logbook) {
        super(parent, International.getString("Fahrtenbuch") + " " + logbook.getName(),
                logbook, -1, admin);
        iniSettings();
    }

    private void iniSettings() {
        removeAction(ItemTypeDataRecordTable.ACTION_NEW);
        removeAction(ItemTypeDataRecordTable.ACTION_EDIT);
        removeAction(ItemTypeDataRecordTable.ACTION_DELETE);
        minColumnWidths = new int[] { 80, 150, 150, 200, 150, 80 };
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, StorageObject persistence, DataRecord record) {
        if (record == null) {
            return null;
        }
        return null;
    }
}
