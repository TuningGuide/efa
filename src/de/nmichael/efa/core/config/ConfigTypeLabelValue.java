/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.EfaConfigFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public abstract class ConfigTypeLabelValue extends ConfigValue {

    protected EfaConfigFrame efaConfigFrame;
    protected JTextField textfield;

    public int displayOnGui(EfaConfigFrame dlg, JPanel panel, int y) {
        efaConfigFrame = dlg;
        
        textfield = new JTextField();
        textfield.setText(toString());
        Dialog.setPreferredSize(textfield, 300, 19);
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(textfield);
        if (type == EfaConfig.TYPE_EXPERT) {
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

}
