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

public class ItemTypeImage extends ItemType {

    private String value;
    private int maxX, maxY;

    protected BaseDialog dlg;
    protected JLabel image;


    public ItemTypeImage(String name, String value, int maxX, int maxY,
            int type, String category, String description) {
        this.name = name;
        this.value = value;
        this.maxX = maxX;
        this.maxY = maxY;
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

        image = new JLabel();
        image.setBorder(BorderFactory.createEtchedBorder());
        image.setPreferredSize(new Dimension(maxX+10, maxY+10));
        image.setToolTipText(getDescription());
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setHorizontalTextPosition(SwingConstants.CENTER);
        setImage(toString());
        JLabel label = new JLabel();
        Mnemonics.setLabel(dlg, label, getDescription() + ": ");
        label.setLabelFor(image);
        if (type == IItemType.TYPE_EXPERT) {
            label.setForeground(Color.red);
        }
        if (color != null) {
            label.setForeground(color);
        }
        JButton selectButton = new JButton();
        selectButton.setIcon(new ImageIcon(de.nmichael.efa.Daten.class.getResource("/de/nmichael/efa/img/menu_open.gif")));
        selectButton.setMargin(new Insets(0,0,0,0));
        Dialog.setPreferredSize(selectButton, 19, 19);
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { selectButtonHit(e); }
        });
        JButton removeButton = new JButton();
        removeButton.setIcon(new ImageIcon(de.nmichael.efa.Daten.class.getResource("/de/nmichael/efa/img/menu_minus.gif")));
        removeButton.setMargin(new Insets(0,0,0,0));
        Dialog.setPreferredSize(removeButton, 19, 19);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { removeButtonHit(e); }
        });

        panel.add(label, new GridBagConstraints(0, y, 1, 2, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, padX, 0, 0), 0, 0));
        panel.add(image, new GridBagConstraints(1, y, 1, 2, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, 0, padYafter, 0), 0, 0));
        panel.add(selectButton, new GridBagConstraints(2, y+0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(padYbefore, 0, 0, 0), 0, 0));
        panel.add(removeButton, new GridBagConstraints(2, y+1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        return 2;
    }

    public void getValueFromGui() {
        // nothing to do (value always has current value!)
    }

    private void setImage(String filename) {
        value = null;
        if (filename == null || filename.length() == 0) {
            image.setText(International.getMessage("max. {width} x {height} Pixel",maxX,maxY));
            image.setIcon(null);
            return;
        }
        try {
            image.setText("");
            image.setIcon(new ImageIcon(filename));
            value = filename;
        } catch (Exception ee) {
            EfaUtil.foo();
        }
    }

    private void selectButtonHit(ActionEvent e) {
        String startDirectory = null;
        String selectedFile = null;

        if (value != null && value.length() > 0) {
            startDirectory = EfaUtil.getPathOfFile(value);
            selectedFile = EfaUtil.getNameOfFile(value);
        }

        String file = Dialog.dateiDialog(dlg,
                International.getMessage("{item} auswählen",
                getDescription()),
                International.getString("Bild-Datei")+" (*.gif, *.jpg)",
                "gif|jpg",
                startDirectory, selectedFile, null,
                false, false);
        if (file != null) {
            setImage(file);
        }

    }

    private void removeButtonHit(ActionEvent e) {
        if (value == null) {
            return;
        }
        if (Dialog.yesNoDialog(International.getString("Wirklich entfernen"),
                International.getString("Soll das Bild wirklich entfernt werden?")) == Dialog.YES) {
            setImage(null);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String filename) {
        this.value = filename;
    }

    public void requestFocus() {
        // nothing to do
    }
    
}
