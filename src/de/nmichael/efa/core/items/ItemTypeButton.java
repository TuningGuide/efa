/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public class ItemTypeButton extends ItemType {

    protected JButton button;

    public ItemTypeButton(String name, 
            int type, String category, String description) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.description = description;
        fieldGridAnchor = GridBagConstraints.CENTER;
        fieldGridFill = GridBagConstraints.HORIZONTAL;
    }

    protected void iniDisplay() {
        button = new JButton();
        Dialog.setPreferredSize(button, fieldWidth, fieldHeight);
        button.setMargin(new Insets(1, 1, 1, 1));
        showValue();
        if (type == IItemType.TYPE_EXPERT) {
            button.setForeground(Color.red);
        }
        if (color != null) {
            button.setForeground(color);
        }
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { actionEvent(e); }
        });
        button.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e) { field_focusGained(e); }
            public void focusLost(FocusEvent e) { field_focusLost(e); }
        });
        this.field = button;
        saveBackgroundColor();
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        this.dlg = dlg;
        iniDisplay();
        panel.add(field, new GridBagConstraints(x, y, fieldGridWidth, fieldGridHeight, 0.0, 0.0,
                fieldGridAnchor, fieldGridFill, new Insets(padYbefore, padXbefore, padYafter, padXafter), 0, 0));
        return 1;
    }

    public int displayOnGui(Window dlg, JPanel panel, String borderLayoutPosition) {
        this.dlg = dlg;
        iniDisplay();
        panel.add(field, borderLayoutPosition);
        return 1;
    }

    public void showValue() {
        if (button != null) {
            Mnemonics.setButton(dlg, button, description);
        }
    }

    public void parseValue(String value) {
        description = value;
    }

    public String toString() {
        return "";
    }

    public void getValueFromGui() {
    }

    public String getValueFromField() {
        return "";  // this ConfigType does not store any values
    }

    public boolean isValidInput() {
        return true;
    }

    public void setVisible(boolean visible) {
        button.setVisible(visible);
    }

    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }
}
