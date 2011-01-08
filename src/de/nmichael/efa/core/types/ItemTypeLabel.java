/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
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
import java.util.Vector;
import java.awt.*;
import javax.swing.*;

public class ItemTypeLabel extends ItemType {
    
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

    public int displayOnGui(BaseDialog dlg, JPanel panel, int y) {
        this.dlg = dlg;

        Vector labels = EfaUtil.split(description, '\n');
        for (int i=0; i<labels.size(); i++) {
            JLabel l = new JLabel();
            l.setText((String)labels.get(i));
            if (color != null) {
                l.setForeground(color);
            }
            panel.add(l, new GridBagConstraints(0, y + i, 2, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets((i == 0 ? padYbefore : 0), padX, (i+1 == labels.size() ? padYafter : 0), 0), 0, 0));
        }
        return labels.size();
    }

    public void getValueFromGui() {
    }

    public void requestFocus() {
    }

}
