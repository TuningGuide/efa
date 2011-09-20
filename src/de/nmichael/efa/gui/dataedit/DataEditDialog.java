/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.dataedit;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class DataEditDialog extends BaseDialog {

    protected JCheckBox expertMode;
    protected JPanel dataPanel;
    protected JTabbedPane tabbedPane;

    protected Vector<IItemType> items;
    protected Hashtable<String,Vector<IItemType>> cat2items; // items per category
    protected Hashtable<JPanel,String> panels;

    public DataEditDialog(Frame parent, String title, Vector<IItemType> items) {
        super(parent, title, International.getStringWithMnemonic("Speichern"));
        setItems(items);
    }

    public DataEditDialog(JDialog parent, String title, Vector<IItemType> items) {
        super(parent, title, International.getStringWithMnemonic("Speichern"));
        setItems(items);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        // create GUI items
        mainPanel.setLayout(new BorderLayout());
        dataPanel = new JPanel();
        dataPanel.setLayout(new BorderLayout());
        expertMode = new JCheckBox();
        Mnemonics.setButton(this, expertMode, International.getString("Expertenmodus"));
        expertMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { updateGui(); }
        });
        dataPanel.add(expertMode, BorderLayout.NORTH);
        mainPanel.add(dataPanel, BorderLayout.CENTER);
        updateGui();
    }

    public void updateGui() {
        String selectedPanel = getSelectedPanel(tabbedPane);
        boolean expertMode = this.expertMode.isSelected();
        boolean expertModeItems = false;

        if (tabbedPane != null) {
            dataPanel.remove(tabbedPane);
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
            Vector<IItemType> v = cat2items.get(cat);
            int y = 0;
            for (IItemType item : v) {
                if (item.getType() == IItemType.TYPE_PUBLIC || expertMode) {
                    y += item.displayOnGui(this,panel,y);
                }
                if (item.getType() == IItemType.TYPE_EXPERT) {
                    expertModeItems = true;
                }
            }
            if (y > 0) {
                String catname = cat;
                if (catname.startsWith("%")) {
                    int pos = catname.indexOf("%", 1);
                    if (pos > 0) {
                        catname = catname.substring(pos+1);
                    }
                }
                tabbedPane.add(panel, catname);
                if (cat.equals(selectedPanel)) {
                    tabbedPane.setSelectedComponent(panel);
                }
            }
        }
        dataPanel.add(tabbedPane, BorderLayout.CENTER);
        this.expertMode.setVisible(expertModeItems);
        this.expertMode.setForeground( (expertMode ? Color.red : Color.black) );
        this.validate();
        Vector<IItemType> v = cat2items.get( (selectedPanel != null ? selectedPanel : cats[0]));
        for (int i=0; v != null && i<v.size(); i++) {
            if (!(v.get(i) instanceof ItemTypeLabel) && v.get(i).isVisible() && v.get(i).isEnabled()) {
                if (v.get(i) instanceof ItemTypeLabelTextfield && !v.get(i).isEditable()) {
                    break;
                }
                setRequestFocus(v.get(i));
                break;
            }
            
        }
    }

    protected boolean getValuesFromGui() {
        boolean changed = false;
        for (IItemType item : items) {
            item.getValueFromGui();
            if (item.isChanged()) {
                changed = true;
                if (Logger.isTraceOn(Logger.TT_GUI)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_GUI_DEBUGGUI, this.getClass().getCanonicalName()+".getValuesFromGui(): "+item.getName()+" has changed");
                }
            }
        }
        return changed;
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
        setDialogResult(true);
        super.closeButton_actionPerformed(e);
    }

    public Vector<IItemType> getItems() {
        return items;
    }

    public IItemType getItem(String name) {
        if (items == null) {
            return null;
        }
        for (IItemType item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public void setItems(Vector<IItemType> items) {
        this.items = items;
        cat2items = new Hashtable<String,Vector<IItemType>>();

        // build data item hierarchy across categories
        if (items == null) {
            return;
        }
        for (IItemType item : items) {
            String cat = item.getCategory();
            Vector<IItemType> v = cat2items.get(cat);
            if (v == null) {
                v = new Vector<IItemType>();
            }
            item.setUnchanged();
            v.add(item);
            cat2items.put(cat, v);
        }

    }

}
