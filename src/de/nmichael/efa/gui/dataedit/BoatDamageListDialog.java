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
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class BoatDamageListDialog extends DataListDialog {

    public BoatDamageListDialog(Frame parent) {
        super(parent, International.getString("Bootsschäden"), Daten.project.getBoatDamages(false), 0);
        actionText = new String[] { ItemTypeDataRecordTable.ACTIONTEXT_EDIT, ItemTypeDataRecordTable.ACTIONTEXT_DELETE };
        actionType = new int[] { ItemTypeDataRecordTable.ACTION_EDIT, ItemTypeDataRecordTable.ACTION_DELETE };
    }

    public BoatDamageListDialog(JDialog parent) {
        super(parent, International.getString("Bootsschäden"), Daten.project.getBoatDamages(false), 0);
        actionText = new String[] { ItemTypeDataRecordTable.ACTIONTEXT_EDIT, ItemTypeDataRecordTable.ACTIONTEXT_DELETE };
        actionType = new int[] { ItemTypeDataRecordTable.ACTION_EDIT, ItemTypeDataRecordTable.ACTION_DELETE };
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, Persistence persistence, DataRecord record) {
        if (record == null) {
            return null;
        }
        return new BoatDamageEditDialog(parent, (BoatDamageRecord)record, false);
    }
}
