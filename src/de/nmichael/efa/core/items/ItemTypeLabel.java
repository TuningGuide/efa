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
import java.util.Vector;
import java.awt.*;
import javax.swing.*;

public class ItemTypeLabel extends ItemType {

    private JLabel[] labels;
    
    public ItemTypeLabel(String name, int type,
            String category, String description) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
    }

    public String toString() {
        return description;
    }

    protected void iniDisplay() {
        Vector v = EfaUtil.split(description, '\n');
        labels = new JLabel[v.size()];
        for (int i=0; i<v.size(); i++) {
            JLabel l = new JLabel();
            l.setText((String)v.get(i));
            if (color != null) {
                l.setForeground(color);
            }
            labels[i] = l;
        }
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        this.dlg = dlg;
        iniDisplay();
        for (int i=0; i<labels.length; i++) {
            panel.add(labels[i], new GridBagConstraints(x, y + i, 2, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets((i == 0 ? padYbefore : 0), padXbefore, (i+1 == labels.length ? padYafter : 0), padXafter), 0, 0));
        }
        return labels.length;
    }

    public void getValueFromGui() {
    }

    public void requestFocus() {
    }

    public String getValueFromField() {
        return null;
    }

    public void showValue() {
    }

    public boolean isValidInput() {
        return true;
    }
    
    public void setVisible(boolean visible) {
        for (int i=0; i<labels.length; i++) {
            labels[i].setVisible(visible);
        }
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }


    public void setEnabled(boolean enabled) {
        for (int i=0; i<labels.length; i++) {
            labels[i].setForeground((enabled ? (new JLabel()).getForeground() : Color.gray));
        }
    }
}
