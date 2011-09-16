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

import de.nmichael.efa.core.items.*;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.config.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class EfaConfigDialog extends BaseDialog {

    private JTabbedPane tabbedPane;
    private JCheckBox expertMode;
    private String _selectedPanel; // selected panel specified in constructor

    private Hashtable<String,Hashtable> categories;
    private Hashtable<String,Vector<IItemType>> items;
    private Vector<IItemType> configItems;
    private Hashtable<JPanel,String> panels;

    public EfaConfigDialog(Frame parent) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
    }

    public EfaConfigDialog(JDialog parent) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
    }

    public EfaConfigDialog(JDialog parent, String selectedPanel) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
        this._selectedPanel = selectedPanel;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }
    
    protected void iniDialog() throws Exception {
        //myEfaConfig = new EfaConfig(Daten.efaConfig);
        categories = new Hashtable<String,Hashtable>();                // category          -> sub-categories
        items = new Hashtable<String,Vector<IItemType>>(); // categoryhierarchy -> config items

        // build category hierarchy
        String[] names = Daten.efaConfig.getParameterNames();
        for (int i=0; i<names.length; i++) {
            IItemType cfg = Daten.efaConfig.getExternalGuiItem(names[i]);
            String[] cats = Daten.efaConfig.getCategoryKeyArray(cfg.getCategory());
            Hashtable<String,Hashtable> h = categories;
            for (int j=0; j<cats.length; j++) {
                Hashtable hnext = h.get(cats[j]);
                if (hnext == null) {
                    hnext = new Hashtable<String,Hashtable>();
                    h.put(cats[j], hnext);
                }
                h = hnext;
            }
        }

        // build config items per category
        for (int i=0; i<names.length; i++) {
            IItemType cfg = Daten.efaConfig.getExternalGuiItem(names[i]);
            cfg.setUnchanged();
            String cat = cfg.getCategory();
            String[] cats = EfaConfig.getCategoryKeyArray(cat);
            Hashtable<String,Hashtable> h = categories;
            for (int j=0; j<cats.length; j++) {
                Hashtable hnext = h.get(cats[j]);

                // check whether there are subcategories for the parameter's level
                if (j == cats.length-1 && hnext.size() != 0) {
                    // yes, there are subcategories for this level
                    // --> place this parameter into a subcategory CATEGORY_COMMON
                    cat = EfaConfig.makeCategory(cat, EfaConfig.CATEGORY_COMMON);

                    // is there already a level CATEGORY_COMMON on this level?
                    if (hnext.get(EfaConfig.CATEGORY_COMMON) != null) {
                        // ok, there already is a level CATEGORY_COMMON
                    } else {
                        // there is no level CATEGORY_COMMON yet --> add one
                        hnext.put(EfaConfig.CATEGORY_COMMON, new Hashtable<String,Hashtable>());
                    }
                }
                h = hnext;
            }

            // build config items per category
            Vector<IItemType> v = items.get(cat);
            if (v == null) {
                v = new Vector<IItemType>();
            }
            v.add(cfg);
            items.put(cat, v);
        }

        // create GUI items
        mainPanel.setLayout(new BorderLayout());
        expertMode = new JCheckBox();
        expertMode.setText(International.getString("Expertenmodus") + 
                " (" + International.getString("alle Parameter anzeigen") + ")");
        expertMode.setSelected(false);
        expertMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { expertModeChanged(e); }
        });
        mainPanel.add(expertMode,BorderLayout.NORTH);
        updateGui();
    }

    public void updateGui() {
        updateGui(true);
    }

    public void updateGui(boolean readValuesFromGui) {
        if (readValuesFromGui) {
            getValuesFromGui();
        }
        String selectedPanel = getSelectedPanel(tabbedPane);

        configItems = new Vector<IItemType>();
        if (tabbedPane != null) {
            mainPanel.remove(tabbedPane);
        }
        tabbedPane = new JTabbedPane();
        panels = new Hashtable<JPanel,String>();
        recursiveBuildGui(categories,items,"",tabbedPane, selectedPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        this.validate();
    }

    private int recursiveBuildGui(Hashtable<String,Hashtable> categories,
                                   Hashtable<String,Vector<IItemType>> items,
                                   String catKey,
                                   JTabbedPane tabbedPane,
                                   String selectedPanel) {
        int itmcnt = 0;
        int pos = (selectedPanel != null && selectedPanel.length() > 0 ? selectedPanel.indexOf(EfaConfig.CATEGORY_SEPARATOR) : -1);
        String selectThisCat = (pos < 0 ? selectedPanel : selectedPanel.substring(0,pos));
        String selectNextCat = (pos < 0 ? null : selectedPanel.substring(pos+1));

        Object[] cats = categories.keySet().toArray();
        Arrays.sort(cats);
        for (int i=0; i<cats.length; i++) {
            String key = (String)cats[i];
            String thisCatKey = (catKey.length() == 0 ? key : EfaConfig.makeCategory(catKey, key));
            String catName = Daten.efaConfig.getCategoryName(key);
            Hashtable<String,Hashtable> subCat = categories.get(key);
            if (subCat.size() != 0) {
                JTabbedPane subTabbedPane = new JTabbedPane();
                if (recursiveBuildGui(subCat, items, thisCatKey, subTabbedPane, selectNextCat) > 0) {
                    tabbedPane.add(subTabbedPane, catName);
                    if (key.equals(selectThisCat)) {
                        tabbedPane.setSelectedComponent(subTabbedPane);
                    }
                }
            } else {
                JPanel panel = new JPanel();
                panels.put(panel, thisCatKey);
                panel.setLayout(new GridBagLayout());
                Vector<IItemType> v = items.get(thisCatKey);
                int y = 0;
                for (int j=0; v != null && j<v.size(); j++) {
                    IItemType itm = v.get(j);
                    if (itm.getType() == IItemType.TYPE_PUBLIC ||
                        (itm.getType() == IItemType.TYPE_EXPERT && expertMode.isSelected())) {
                        y += itm.displayOnGui(this,panel,y);
                        configItems.add(itm);
                        itmcnt++;
                    }
                }
                if (y > 0) {
                    tabbedPane.add(panel, catName);
                    if (key.equals(selectThisCat)) {
                        tabbedPane.setSelectedComponent(panel);
                    }
                }
            }
        }
        return itmcnt;
    }

    void getValuesFromGui() {
        if (configItems == null) {
            return;
        }
        for (int i=0; i<configItems.size(); i++) {
            IItemType item = configItems.get(i);
            item.getValueFromGui();
        }
    }

    void expertModeChanged(ActionEvent e) {
        if (expertMode.isSelected()) {
            expertMode.setForeground(Color.red);
        } else {
            expertMode.setForeground(Color.black);
        }
        updateGui();
    }

    private String getSelectedPanel(JTabbedPane pane) {
        if (_selectedPanel != null) {
            String s = _selectedPanel;
            _selectedPanel = null;
            return s;
        }
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
            try {
                return getSelectedPanel((JTabbedPane)c);
            } catch(Exception ee) {
                return null;
            }
        }
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        getValuesFromGui();
        for (int i=0; i<configItems.size(); i++) {
            IItemType item = configItems.get(i);
            if (item.isChanged()) {
                Daten.efaConfig.setValue(item.getName(), item.toString());
            }
        }
        Daten.efaConfig.checkNewConfigValues();
        Daten.efaConfig.updateConfigValuesWithPersistence();
        Daten.efaConfig.setExternalParameters(true);
        Daten.efaConfig.checkForRequiredPlugins();
        super.closeButton_actionPerformed(e);
        setDialogResult(true);
    }

    public IItemType getItem(String name) {
        for (int i=0; i<configItems.size(); i++) {
            if (configItems.get(i).getName().equals(name)) {
                return configItems.get(i);
            }
        }
        return null;
    }

    /*
     * The following methods will return the current working items (needed by ItemTypeAction to
     * generate new types), by first fetching the name of the item from the real EfaConfig, and
     * then find the current working item by this name.
     */
    public ItemTypeHashtable<String> getTypesBoat() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesBoat().getName());
    }

    public ItemTypeHashtable<String> getTypesNumSeats() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesNumSeats().getName());
    }

    public ItemTypeHashtable<String> getTypesRigging() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesRigging().getName());
    }

    public ItemTypeHashtable<String> getTypesCoxing() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesCoxing().getName());
    }

    public ItemTypeHashtable<String> getTypesGender() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesGender().getName());
    }

    public ItemTypeHashtable<String> getTypesSession() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesSession().getName());
    }

    public ItemTypeHashtable<String> getTypesStatus() {
        return (ItemTypeHashtable<String>)getItem(Daten.efaConfig.getValueTypesStatus().getName());
    }

}
