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
    protected boolean isShowOptional = false;
    protected String optionalButtonText = "+";
    protected JButton expandButton;

    protected abstract JComponent initializeField();

    protected void iniDisplay() {
        if (getDescription() != null) {
            label = new JLabel();
            Mnemonics.setLabel(dlg, label, getDescription() + ": ");
            label.setLabelFor(field);
            if (type == IItemType.TYPE_EXPERT) {
                label.setForeground(Color.red);
            }
            if (color != null) {
                label.setForeground(color);
            }
            labelFont = label.getFont();
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(MouseEvent e) { actionEvent(e); }
            });
        } else {
            labelGridWidth = 0;
        }
        field = initializeField();
        Dialog.setPreferredSize(field, fieldWidth, fieldHeight);
        if (backgroundColor != null) {
            field.setBackground(backgroundColor);
        }
        fieldFont = field.getFont();
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e) { field_focusGained(e); }
            public void focusLost(FocusEvent e) { field_focusLost(e); }
        });
        if (isShowOptional) {
            expandButton = new JButton();
            if (optionalButtonText.length() == 1) {
                Dialog.setPreferredSize(expandButton, 15, 15);
                expandButton.setFont(expandButton.getFont().deriveFont(Font.PLAIN, 8));
                expandButton.setMargin(new Insets(0, 0, 0, 0));
            } else {
                expandButton.setMargin(new Insets(0, 10, 0, 10));
            }
            expandButton.setText(optionalButtonText);
            expandButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) { expandButton_actionEvent(e); }
            });
        }
        showValue();
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        this.dlg = dlg;
        iniDisplay();
        if (label != null) {
            panel.add(label, new GridBagConstraints(x, y, labelGridWidth, fieldGridHeight, 0.0, 0.0,
                    labelGridAnchor, labelGridFill, new Insets(padYbefore, padXbefore, padYafter, 0), 0, 0));
        }
        if (expandButton != null) {
            int gridWidth = labelGridWidth + (optionalButtonText.length() > 1 ? fieldGridWidth : 0);
            panel.add(expandButton, new GridBagConstraints(x, y, gridWidth, fieldGridHeight, 0.0, 0.0,
                    labelGridAnchor, labelGridFill, new Insets(padYbefore, padXbefore, padYafter, 0), 0, 0));
        }
        panel.add(field, new GridBagConstraints(x+labelGridWidth, y, fieldGridWidth, fieldGridHeight, 0.0, 0.0,
                fieldGridAnchor, fieldGridFill, new Insets(padYbefore, 0, padYafter, padXafter), 0, 0));
        if (!isEnabled) {
            setEnabled(isEnabled);
        }
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
        return (label != null ? label.getFont() : null);
    }

    public void setLabelFont(Font font) {
        if (label != null) {
            label.setFont(font);
        }
    }

    public void restoreLabelFont() {
        if (label != null) {
            label.setFont(labelFont);
        }
    }

    public void setDescription(String s) {
        super.setDescription(s);
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
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

    private boolean showExpandButton(boolean isExpandButtonHit, boolean calledForExpandButton) {
        if (isShowOptional && toString().length() == 0 && !isExpandButtonHit) {
            return (calledForExpandButton); // show expandButton
        } else {
            return (!calledForExpandButton); // show label
        }
    }

    public void showValue() {
        setVisibleInternal(false);
    }

    private void setVisibleInternal(boolean isExpandButtonHit) {
        if (label != null) {
            label.setVisible(isVisible && showExpandButton(isExpandButtonHit, false));
        }
        if (expandButton != null) {
            expandButton.setVisible(isVisible && showExpandButton(isExpandButtonHit, true));
        }
        if (field != null) {
            field.setVisible(isVisible && showExpandButton(isExpandButtonHit, false));
        }
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setVisibleInternal(false);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (label != null) {
            label.setForeground((enabled ? (new JLabel()).getForeground() : Color.gray));
        }
        if (expandButton != null) {
            expandButton.setEnabled(enabled);
        }
        if (field != null) {
            field.setEnabled(enabled);
        }
    }

    public void showOptional(boolean optional) {
        isShowOptional = optional;
    }

    public void setOptionalButtonText(String text) {
        this.optionalButtonText = text;
    }

    private void expandButton_actionEvent(ActionEvent e) {
        setVisibleInternal(true);
        if (field != null) {
            field.requestFocus();
        }
    }
}