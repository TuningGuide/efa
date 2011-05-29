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
public class StatusListDialog extends DataListDialog {

    public StatusListDialog(Frame parent) {
        super(parent, International.getString("Status"), Daten.project.getStatus(false), 0);
    }

    public StatusListDialog(JDialog parent) {
        super(parent, International.getString("Status"), Daten.project.getStatus(false), 0);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, Persistence persistence, DataRecord record) {
        boolean newRecord = (record == null);
        if (record == null) {
            record = Daten.project.getStatus(false).createStatusRecord(UUID.randomUUID());
            ((StatusRecord)record).setType(StatusRecord.TYPE_USER);
        }
        return new StatusEditDialog(parent, (StatusRecord)record, newRecord);
    }

}
