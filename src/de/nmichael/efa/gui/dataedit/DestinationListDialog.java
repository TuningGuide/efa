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

import de.nmichael.efa.gui.dataedit.DataListDialog;
import de.nmichael.efa.gui.dataedit.DataEditDialog;
import de.nmichael.efa.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class DestinationListDialog extends DataListDialog {

    public DestinationListDialog(Frame parent, long validAt) {
        super(parent, International.getString("Ziele"), Daten.project.getDestinations(false), validAt);
    }

    public DestinationListDialog(JDialog parent, long validAt) {
        super(parent, International.getString("Ziele"), Daten.project.getDestinations(false), validAt);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, Persistence persistence, DataRecord record) {
        boolean newRecord = (record == null);
        if (record == null) {
            record = Daten.project.getDestinations(false).createDestinationRecord(UUID.randomUUID());
        }
        return new DestinationEditDialog(parent, (DestinationRecord)record, newRecord);
    }
}
