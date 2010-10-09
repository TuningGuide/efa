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

public class ItemTypeStringList extends ItemType {

    private String value;
    private String[] valueList;
    private String[] displayList;
    protected BaseDialog dlg;
    protected JComboBox combobox;
    protected int width = 300;
    protected boolean twoRows = false;

    public ItemTypeStringList(String name, String value,
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

    public int displayOnGui(BaseDialog dlg, JPanel panel, int y) {
        this.dlg = dlg;

        combobox = new JComboBox();
        for (int i=0; displayList != null && i<displayList.length; i++) {
            combobox.addItem(displayList[i]);
        }
        selectValue();
        combobox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) { combobox_focusLost(e); }
        });
        
        Dialog.setPreferredSize(combobox, width, 19);
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(combobox);
        if (type == IItemType.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }
        if (color != null) {
            label.setForeground(color);
        }
        if (!twoRows) {
            panel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, padX, padYafter, 0), 0, 0));
            panel.add(combobox, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, 0, padYafter, 0), 0, 0));
            return 1;
        } else {
            panel.add(label, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, padX, 0, 0), 0, 0));
            panel.add(combobox, new GridBagConstraints(0, y+1, 2, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, padX, padYafter, 0), 0, 0));
            return 2;
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setTwoRows(boolean twoRows) {
        this.twoRows = twoRows;
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

    private void selectValue() {
        for (int i=0; valueList != null && i<valueList.length; i++) {
            if (value != null && value.equals(valueList[i])) {
                combobox.setSelectedIndex(i);
            }
        }
    }

    private void combobox_focusLost(FocusEvent e) {
        getValueFromGui();
        selectValue();
    }

    public void requestFocus() {
        combobox.requestFocus();
    }

}
