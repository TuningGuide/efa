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
import javax.swing.*;

// @i18n complete

public class ConfigTypeStringList extends ConfigValue {

    private String value;
    private String[] valueList;
    private String[] displayList;
    protected EfaConfigFrame efaConfigFrame;
    protected JComboBox combobox;

    public ConfigTypeStringList(String name, String value, 
            String[] valueList, String[] displayList,
            int type, String category, String description) {
        this.name = name;
        this.value = value;
        this.valueList = valueList;
        this.displayList = displayList;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public int displayOnGui(EfaConfigFrame dlg, JPanel panel, int y) {
        efaConfigFrame = dlg;

        combobox = new JComboBox();
        for (int i=0; displayList != null && i<displayList.length; i++) {
            combobox.addItem(displayList[i]);
        }
        for (int i=0; valueList != null && i<valueList.length; i++) {
            if (value != null && value.equals(valueList[i])) {
                combobox.setSelectedIndex(i);
            }
        }
        
        Dialog.setPreferredSize(combobox, 200, 19);
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(combobox);
        if (type == EfaConfig.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }
        panel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(combobox, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        if (combobox != null && combobox.getSelectedIndex() >= 0) {
            parseValue(valueList[combobox.getSelectedIndex()]);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        for (int i=0; i<valueList.length; i++) {
            if (value.equals(valueList[i])) {
                this.value = value;
            }
        }
    }

}
