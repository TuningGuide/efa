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
public class BoatStatusListDialog extends DataListDialog {

    public BoatStatusListDialog(Frame parent) {
        super(parent, International.getString("Bootsstatus"), Daten.project.getBoatStatus(false), 0);
        actionText = new String[] { ItemTypeDataRecordTable.ACTIONTEXT_EDIT };
        actionType = new int[] { ItemTypeDataRecordTable.ACTION_EDIT };
    }

    public BoatStatusListDialog(JDialog parent) {
        super(parent, International.getString("Bootsstatus"), Daten.project.getBoatStatus(false), 0);
        actionText = new String[] { ItemTypeDataRecordTable.ACTIONTEXT_EDIT };
        actionType = new int[] { ItemTypeDataRecordTable.ACTION_EDIT };
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, Persistence persistence, DataRecord record) {
        if (record == null) {
            return null;
        }
        return new BoatStatusEditDialog(parent, (BoatStatusRecord)record, false);
    }
}
