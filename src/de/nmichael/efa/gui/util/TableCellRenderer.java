/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.gui.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.Vector;

public class TableCellRenderer extends DefaultTableCellRenderer {

    private static Color markedColor = new Color(0xff,0xff,0xaa);

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            if (value == null) {
                return null;
            }
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean isMarked = value instanceof TableItem && ((TableItem)value).isMarked();
            String txt = value.toString();
            if (isMarked) {
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }
            Color color = Color.white;
            if (isSelected) {
                color = table.getSelectionBackground();
            } else {
                if (isMarked) {
                    color = markedColor;
                }
            }

            c.setBackground(color);
            return this;
        } catch (Exception e) {
            return null;
        }
    }
}

