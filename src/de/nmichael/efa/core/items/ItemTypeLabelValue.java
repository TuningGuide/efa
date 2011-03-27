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
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public abstract class ItemTypeLabelValue extends ItemType {

    protected JLabel label;
    protected int labelGridWidth = 1;
    protected int labelGridAnchor = GridBagConstraints.WEST;
    protected int labelGridFill = GridBagConstraints.NONE;
    protected Font labelFont;
    protected Font fieldFont;

    protected abstract JComponent initializeField();

    protected void iniDisplay() {
        field = initializeField();
        Dialog.setPreferredSize(field, fieldWidth, fieldHeight);
        label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(field);
        if (type == IItemType.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }
        if (color != null) {
            label.setForeground(color);
        }
        if (backgroundColor != null) {
            field.setBackground(backgroundColor);
        }
        labelFont = label.getFont();
        fieldFont = field.getFont();
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e) { field_focusGained(e); }
            public void focusLost(FocusEvent e) { field_focusLost(e); }
        });
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) { actionEvent(e); }
        });
        showValue();
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        this.dlg = dlg;
        iniDisplay();
        panel.add(label, new GridBagConstraints(x, y, labelGridWidth, fieldGridHeight, 0.0, 0.0,
                labelGridAnchor, labelGridFill, new Insets(padYbefore, padXbefore, padYafter, 0), 0, 0));
        panel.add(field, new GridBagConstraints(x+labelGridWidth, y, fieldGridWidth, fieldGridHeight, 0.0, 0.0,
                fieldGridAnchor, fieldGridFill, new Insets(padYbefore, 0, padYafter, padXafter), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        if (field != null) {
            String s = getValueFromField();
            if (s != null) {
                parseValue(s);
            }
        }
    }

    protected void field_focusLost(FocusEvent e) {
        getValueFromGui();
        showValue();
        super.field_focusLost(e);
    }

    public void setLabelGrid(int gridWidth, int gridAnchor, int gridFill) {
        labelGridWidth = gridWidth;
        labelGridAnchor = gridAnchor;
        labelGridFill = gridFill;
    }

    public Font getLabelFont() {
        return label.getFont();
    }

    public void setLabelFont(Font font) {
        label.setFont(font);
    }

    public void restoreLabelFont() {
        label.setFont(labelFont);
    }

    public Font getFieldFont() {
        return field.getFont();
    }

    public void setFieldFont(Font font) {
        field.setFont(font);
    }

    public void restoreFieldFont() {
        field.setFont(fieldFont);
    }

    public void setVisible(boolean visible) {
        label.setVisible(visible);
        field.setVisible(visible);
    }

    public void setEnabled(boolean enabled) {
        label.setForeground((enabled ? (new JLabel()).getForeground() : Color.gray));
        field.setEnabled(enabled);
    }
}
