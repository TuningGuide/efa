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

import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.config.Admins;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class AdminListDialog extends DataListDialog {

    private Admins admins;

    public AdminListDialog(Frame parent, Admins admins) {
        super(parent, International.getString("Administratoren"), admins, 0);
        this.admins = admins;
        actionText = null; // only ADD, EDIT, DELETE (no IMPORT, EXPORT)
        actionType = null; // only ADD, EDIT, DELETE (no IMPORT, EXPORT)
    }

    public AdminListDialog(JDialog parent, Admins admins) {
        super(parent, International.getString("Administratoren"), admins, 0);
        this.admins = admins;
        actionText = null; // only ADD, EDIT, DELETE (no IMPORT, EXPORT)
        actionType = null; // only ADD, EDIT, DELETE (no IMPORT, EXPORT)
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, StorageObject persistence, DataRecord record) {
        boolean newRecord = (record == null);
        if (record == null) {
            record = admins.createAdminRecord(null, null);
        }
        return new AdminEditDialog(parent, (AdminRecord)record, newRecord);
    }
}
