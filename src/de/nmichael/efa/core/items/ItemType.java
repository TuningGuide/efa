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

import java.util.Vector;
import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Logger;

// @i18n complete

public abstract class ItemType implements IItemType {

    protected String name;
    protected int type;
    protected String category;
    protected String description;

    protected Window dlg;
    protected JComponent field;
    protected IItemListener listener;
    protected String lastValue;

    protected Color color = null;
    protected Color backgroundColor = null;
    protected Color savedColor = null;
    protected int padXbefore = 0;
    protected int padXafter = 0;
    protected int padYbefore = 0;
    protected int padYafter = 0;
    protected boolean notNull = false;
    protected int fieldWidth = 300;
    protected int fieldHeight = 19;
    protected int fieldGridWidth = 1;
    protected int fieldGridHeight = 1;
    protected int fieldGridAnchor = GridBagConstraints.WEST;
    protected int fieldGridFill = GridBagConstraints.NONE;

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String s) {
        description = s;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public void setBackgroundColor(Color c) {
        this.backgroundColor = c;
        if (field != null) {
            field.setBackground(c);
        }
    }

    public void saveBackgroundColor() {
        if (field != null) {
            savedColor = field.getBackground();
        }
    }

    public void restoreBackgroundColor() {
        if (field != null && savedColor != null) {
            field.setBackground(savedColor);
        }
    }

    public void requestFocus() {
        if (field != null) {
            field.requestFocus();
        }
    }

    public void setPadding(int padXbefore, int padXafter, int padYbefore, int padYafter) {
        this.padXbefore = padXbefore;
        this.padXafter = padXafter;
        this.padYbefore = padYbefore;
        this.padYafter = padYafter;
    }

    public void setFieldSize(int width, int height) {
        fieldWidth = width;
        fieldHeight = height;
    }

    public void setFieldGrid(int gridWidth, int gridAnchor, int gridFill) {
        fieldGridWidth = gridWidth;
        fieldGridAnchor = gridAnchor;
        fieldGridFill = gridFill;
    }

    public void setFieldGrid(int gridWidth, int gridHeight, int gridAnchor, int gridFill) {
        fieldGridWidth = gridWidth;
        fieldGridHeight = gridHeight;
        fieldGridAnchor = gridAnchor;
        fieldGridFill = gridFill;
    }

    protected abstract void iniDisplay();

    public int displayOnGui(Window dlg, JPanel panel, int y) {
        return displayOnGui(dlg, panel, 0, y);
    }

    public void parseAndShowValue(String value) {
        if (value != null) {
            parseValue(value);
        } else {
            parseValue("");
        }
        showValue();
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isNotNullSet() {
        return notNull;
    }

    protected void field_focusGained(FocusEvent e) {
        actionEvent(e);
    }
    protected void field_focusLost(FocusEvent e) {
        actionEvent(e);
    }

    public void setUnchanged() {
        lastValue = toString();
    }

    public boolean isChanged() {
        return !toString().equals(lastValue);
    }

    public void registerItemListener(IItemListener listener) {
        this.listener = listener;
    }

    public void actionEvent(AWTEvent e) {
        if (listener != null && e != null) {
            listener.itemListenerAction(this, e);
        }
    }

}
