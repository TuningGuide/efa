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
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public class ItemTypeButton extends ItemType {

    private String text;
    private String color;
    private boolean show;
    private boolean isChangeableText;
    private boolean isChangeableColor;
    private boolean isChangeableShow;

    protected JButton button;
    protected JCheckBox checkbox;


    public ItemTypeButton(String name, String text, String color, boolean show,
            boolean isChangeableText, boolean isChangeableColor, boolean isChangeableShow,
            int type, String category, String description) {
        this.name = name;
        this.text = text;
        this.color = color;
        this.show = show;
        this.isChangeableText = isChangeableText;
        this.isChangeableColor = isChangeableColor;
        this.isChangeableShow = isChangeableShow;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        if (value == null) return;
        try {
            StringTokenizer tok = new StringTokenizer(value, "|");
            int i = 0;
            while (tok.hasMoreTokens()) {
                String t = tok.nextToken();
                if (t.length() > 0) {
                    switch(i) {
                        case 0:
                            if (isChangeableText) {
                                text = t;
                            }
                            break;
                        case 1:
                            if (isChangeableColor) {
                                color = t;
                            }
                            break;
                        case 2:
                            if (isChangeableShow) {
                                show = t.equals("+");
                            }
                            break;
                    }
                }
                i++;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                    "Invalid value for parameter " + name + ": " + value);

        }
    }

    public String toString() {
        return  (isChangeableText ? EfaUtil.removeSepFromString(text, "|") : "#") +
                "|" +
                (isChangeableColor ? color : "#") +
                "|" +
                (isChangeableShow ? (show ? "+" : "-") : "#");
    }

    public int displayOnGui(BaseDialog dlg, JPanel panel, int y) {
        this.dlg = dlg;

        button = new JButton();
        Dialog.setPreferredSize(button, 300, 21);
        button.setText(text);
        button.setBackground(EfaUtil.getColor(color));
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { buttonHit(e); }
        });
        if (isChangeableShow) {
            checkbox = new JCheckBox();
            checkbox.setText(International.getString("anzeigen"));
            checkbox.setSelected(show);
        }
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(button);
        if (type == IItemType.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }

        panel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(button, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        if (checkbox != null) {
            panel.add(checkbox, new GridBagConstraints(2, y, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        return 1;
    }

    public void getValueFromGui() {
        if (isChangeableText) {
            text = button.getText();
        }
        if (isChangeableColor) {
            color = EfaUtil.getColor(button.getBackground());
        }
        if (isChangeableShow) {
            show = checkbox.isSelected();
        }
    }

    private void buttonHit(ActionEvent e) {
        if (!isChangeableText && !isChangeableColor) return;
        if (!isChangeableColor) {
            chooseText();
            return;
        }
        if (!isChangeableText) {
            chooseColor();
            return;
        }
        switch(Dialog.auswahlDialog(International.getString("Auswahl"),
                International.getString("Was möchtest Du ändern?"),
                International.getString("Text"),
                International.getString("Farbe"))) {
            case 0:
                chooseText();
                break;
            case 1:
                chooseColor();
                break;
        }
    }

    private void chooseText() {
        if (!isChangeableText) return;
        String s = Dialog.inputDialog(getDescription(),
                International.getString("Bitte Text für Button eingeben") + ":",
                button.getText());
        if (s != null) {
            button.setText(s.trim());
        }
    }

    private void chooseColor() {
        if (!isChangeableColor) return;
        Color color = JColorChooser.showDialog(dlg,
                International.getMessage("{item} auswählen",
                International.getString("Farbe")),
                button.getBackground());
        if (color != null) {
            button.setBackground(color);
        }

    }

    public String getValueText() {
        return text;
    }

    public String getValueColor() {
        return color;
    }

    public boolean getValueShow() {
        return show;
    }

    public void setValueText(String text) {
        this.text = text;
    }

    public void setValueColor(String color) {
        this.color = color;
    }

    public void setValueShow(boolean show) {
        this.show = show;
    }

    public void requestFocus() {
        button.requestFocus();
    }

}
