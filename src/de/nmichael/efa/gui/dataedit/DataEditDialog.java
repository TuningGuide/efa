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

import de.nmichael.efa.util.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.BaseTabbedDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

// @i18n complete
public class DataEditDialog extends BaseTabbedDialog {

    public DataEditDialog(Frame parent, String title, Vector<IItemType> items) {
        super(parent, title, International.getStringWithMnemonic("Speichern"),
              items, false);
    }

    public DataEditDialog(JDialog parent, String title, Vector<IItemType> items) {
        super(parent, title, International.getStringWithMnemonic("Speichern"),
              items, false);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        getValuesFromGui();
        setDialogResult(true);
        super.closeButton_actionPerformed(e);
    }

}
