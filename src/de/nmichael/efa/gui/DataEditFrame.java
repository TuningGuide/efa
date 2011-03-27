/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class DataEditFrame extends BaseDialog {

    private JTabbedPane tabbedPane;

    private Vector<DataItem> items;
    private Hashtable<String,Vector<DataItem>> cat2items; // items per category
    private Hashtable<JPanel,String> panels;

    public DataEditFrame(Frame parent, String title, Vector<DataItem> items) {
        super(parent, title, International.getStringWithMnemonic("Speichern"));
        this.items = items;
    }

    public DataEditFrame(JDialog parent, String title, Vector<DataItem> items) {
        super(parent, title, International.getStringWithMnemonic("Speichern"));
        this.items = items;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        cat2items = new Hashtable<String,Vector<DataItem>>();

        // build data item hierarchy across categories
        for (DataItem item : items) {
            String cat = item.item.getCategory();
            Vector<DataItem> v = cat2items.get(cat);
            if (v == null) {
                v = new Vector<DataItem>();
            }
            v.add(item);
            cat2items.put(cat, v);
        }

        // create GUI items
        mainPanel.setLayout(new BorderLayout());
        updateGui();
    }

    public void updateGui() {
        getValuesFromGui();
        String selectedPanel = getSelectedPanel(tabbedPane);

        if (tabbedPane != null) {
            mainPanel.remove(tabbedPane);
        }
        tabbedPane = new JTabbedPane();
        panels = new Hashtable<JPanel,String>();

        Object[] cats = cat2items.keySet().toArray();
        Arrays.sort(cats);
        for (int i=0; i<cats.length; i++) {
            String cat = (String)cats[i];
            JPanel panel = new JPanel();
            panels.put(panel, cat);
            panel.setLayout(new GridBagLayout());
            Vector<DataItem> v = cat2items.get(cat);
            int y = 0;
            for (DataItem item : v) {
                y += item.item.displayOnGui(this,panel,y);
            }
            if (y > 0) {
                tabbedPane.add(panel, cat);
                if (cat.equals(selectedPanel)) {
                    tabbedPane.setSelectedComponent(panel);
                }
            }
        }
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        this.validate();
    }

    void getValuesFromGui() {
        for (DataItem item : items) {
            String oldVal = item.item.toString();
            item.item.getValueFromGui();
            item.changed = item.changed || !oldVal.equals(item.item.toString());
        }
    }

    private String getSelectedPanel(JTabbedPane pane) {
        if (pane == null) {
            return null;
        }
        Component c = pane.getSelectedComponent();
        if (c == null) {
            return null;
        }
        try {
            JPanel panel = (JPanel)c;
            return panels.get(panel);
        } catch(Exception e) {
            return null;
        }
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        getValuesFromGui();
        super.closeButton_actionPerformed(e);
    }

    public Vector<DataItem> getItems() {
        return items;
    }

}
