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
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class PersonListDialog extends DataListDialog {

    public PersonListDialog(Frame parent, long validAt) {
        super(parent, International.getString("Personen"), Daten.project.getPersons(false), validAt);
    }

    public PersonListDialog(JDialog parent, long validAt) {
        super(parent, International.getString("Personen"), Daten.project.getPersons(false), validAt);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, DataRecord record) {
        if (record == null) {
            record = Daten.project.getPersons(false).createPersonRecord(UUID.randomUUID());
        }
        return new PersonEditDialog(parent, (PersonRecord)record);
    }
}
