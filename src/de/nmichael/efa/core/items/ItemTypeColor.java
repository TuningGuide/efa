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

public class ItemTypeColor extends ItemTypeLabelValue {

    private String color;

    public ItemTypeColor(String name, String color,
            int type, String category, String description) {
        this.name = name;
        this.color = color;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public IItemType copyOf() {
        return new ItemTypeColor(name, color, type, category, description);
    }

    public void parseValue(String value) {
        this.color = value;
    }

    public String toString() {
        return color;
    }

    protected JComponent initializeField() {
        JButton f = new JButton();
        return f;
    }
    protected void iniDisplay() {
        super.iniDisplay();
        JButton f = (JButton)field;
        f.setEnabled(isEnabled);
        Dialog.setPreferredSize(f, fieldWidth, fieldHeight);
        f.setText(International.getMessage("{item} auswählen",
                International.getString("Farbe")));
        f.setBackground(EfaUtil.getColor(color));
        f.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { buttonHit(e); }
        });
    }

    public void getValueFromGui() {
        color = EfaUtil.getColor(field.getBackground());
    }

    public String getValueFromField() {
        return EfaUtil.getColor(field.getBackground());
    }

    public void showValue() {
    }

    private void buttonHit(ActionEvent e) {
        Color color = JColorChooser.showDialog(dlg,
                International.getMessage("{item} auswählen",
                International.getString("Farbe")),
                field.getBackground());
        if (color != null) {
            field.setBackground(color);
        }
    }

    public boolean isValidInput() {
        return true;
    }
    
}
