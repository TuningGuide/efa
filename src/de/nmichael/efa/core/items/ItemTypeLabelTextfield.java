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

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public abstract class ItemTypeLabelTextfield extends ItemTypeLabelValue {

    protected JComponent initializeField() {
        JTextField f = new JTextField();
        return f;
    }

    public String getValueFromField() {
        if (field != null) {
            return ((JTextField)field).getText();
        } else {
            return null;
        }
    }

    public void showValue() {
        super.showValue();
        if (field != null) {
            ((JTextField)field).setText(toString());
        }
    }

    protected void iniDisplay() {
        super.iniDisplay();
        JTextField f = (JTextField)field;
        f.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { actionEvent(e); }
        });
    }

    public void setSelection(int beginIndex, int endIndex) {
        if (field == null) {
            return;
        }
        JTextField f = (JTextField)field;
        if (endIndex > f.getText().length()) {
            endIndex = f.getText().length();
        }
        if (beginIndex < 0 || beginIndex > endIndex) {
            f.select(0, 0);
        } else {
            f.select(beginIndex, endIndex);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        try {
            if (field != null && Daten.lookAndFeel.equals("Metal")) {
                ((JTextField) field).setDisabledTextColor(Color.darkGray);
                ((JTextField) field).setBackground((enabled ? (new JTextField()).getBackground() : new Color(234, 234, 234)));
            }
        } catch (Exception e) {
            EfaUtil.foo();
        }
    }
}
