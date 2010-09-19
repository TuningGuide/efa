/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.types;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public abstract class ItemTypeLabelValue extends ItemType {

    protected BaseDialog dlg;
    protected JTextField textfield;

    public int displayOnGui(BaseDialog dlg, JPanel panel, int y) {
        this.dlg = dlg;
        
        textfield = new JTextField();
        textfield.setText(toString());
        Dialog.setPreferredSize(textfield, 300, 19);
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(textfield);
        if (type == IItemType.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }
        textfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) { textfield_focusLost(e); }
        });
        panel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(textfield, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        if (textfield != null) {
            parseValue(textfield.getText().trim());
        }
    }

    protected void textfield_focusLost(FocusEvent e) {
        getValueFromGui();
        textfield.setText(toString());
    }

    public void requestFocus() {
        textfield.requestFocus();
    }

}
