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
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

// @i18n complete

public class ItemTypeBoolean extends ItemType {

    private boolean value;

    public ItemTypeBoolean(String name, boolean value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
        this.fieldGridWidth = 3;
    }

    public IItemType copyOf() {
        return new ItemTypeBoolean(name, value, type, category, description);
    }

    public void parseValue(String value) {
        if (value != null) {
            value = value.trim();
        }
        try {
            this.value = Boolean.parseBoolean(value);
        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                       "Invalid value for parameter "+name+": "+value);
        }
    }

    public String toString() {
        return Boolean.toString(value);
    }

    protected void iniDisplay() {
        JCheckBox checkbox = new JCheckBox();
        Mnemonics.setButton(dlg, checkbox, getDescription());
        checkbox.setSelected(value);
        if (type == IItemType.TYPE_EXPERT) {
            checkbox.setForeground(Color.red);
        }
        if (color != null) {
            checkbox.setForeground(color);
        }
        checkbox.setEnabled(isEnabled);
        this.field = checkbox;
        checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionEvent(e);
            }
        });
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        this.dlg = dlg;
        iniDisplay();
        panel.add(field, new GridBagConstraints(x, y, fieldGridWidth, fieldGridHeight, 0.0, 0.0,
                fieldGridAnchor, fieldGridFill, new Insets(padYbefore, padXbefore, padYafter, padXafter), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        if (field != null) {
            value = ((JCheckBox)field).isSelected();
        }
    }

    public String getValueFromField() {
        if (field != null) {
            return Boolean.toString(((JCheckBox)field).isSelected());
        } else {
            return toString(); // otherwise a hidden field in expert mode might return null
        }
    }

    public void showValue() {
        if (field != null) {
            ((JTextField)field).setText(toString());
        }
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
        showValue();
    }

    public boolean isValidInput() {
        return true;
    }

    public void setVisible(boolean visible) {
        if (field != null) {
            field.setVisible(visible);
        }
        super.setVisible(visible);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (field != null) {
            field.setEnabled(enabled);
        }
    }

}
