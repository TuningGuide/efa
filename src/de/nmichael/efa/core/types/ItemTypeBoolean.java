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
import javax.swing.*;

// @i18n complete

public class ItemTypeBoolean extends ItemType {

    private boolean value;
    private JCheckBox checkbox;

    public ItemTypeBoolean(String name, boolean value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
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

    public int displayOnGui(BaseDialog dlg, JPanel panel, int y) {
        this.dlg = dlg;
        
        checkbox = new JCheckBox();
        Mnemonics.setButton(dlg, checkbox, getDescription());
        checkbox.setSelected(value);
        if (type == IItemType.TYPE_EXPERT) {
            checkbox.setForeground(Color.red);
        }
        if (color != null) {
            checkbox.setForeground(color);
        }
        panel.add(checkbox, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, padX, padYafter, 0), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        if (checkbox != null) {
            value = checkbox.isSelected();
        }
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void requestFocus() {
        checkbox.requestFocus();
    }

}
