/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
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
import de.nmichael.efa.core.config.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class EfaConfigFrame extends BaseDialog {

    private JTabbedPane tabbedPane;
    private JCheckBox expertMode;

    private EfaConfig myEfaConfig;
    private Hashtable<String,Hashtable> categories;
    private Hashtable<String,Vector<ConfigValue>> items;
    private Vector<ConfigValue> configItems;
    private Hashtable<JPanel,String> panels;

    public EfaConfigFrame(Frame parent) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
    }

    public EfaConfigFrame(JDialog parent) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
    }

    protected void iniDialog() throws Exception {
        myEfaConfig = new EfaConfig(Daten.efaConfig);
        categories = new Hashtable<String,Hashtable>();                // category          -> sub-categories
        items = new Hashtable<String,Vector<ConfigValue>>(); // categoryhierarchy -> config items

        // build category hierarchy
        String[] names = myEfaConfig.getParameterNames();
        for (int i=0; i<names.length; i++) {
            ConfigValue cfg = myEfaConfig.getParameter(names[i]);
            String[] cats = myEfaConfig.getCategoryKeyArray(cfg.getCategory());
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
            ConfigValue cfg = myEfaConfig.getParameter(names[i]);
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
            Vector<ConfigValue> v = items.get(cat);
            if (v == null) {
                v = new Vector<ConfigValue>();
            }
            v.add(cfg);
            items.put(cat, v);
        }

        // create GUI items
        mainPanel.setLayout(new BorderLayout());
        expertMode = new JCheckBox();
        expertMode.setText("Expertenmodus (alle Parameter anzeigen)");
        expertMode.setSelected(false);
        expertMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { expertModeChanged(e); }
        });
        mainPanel.add(expertMode,BorderLayout.NORTH);
        updateGui();
    }

    public void updateGui() {
        getValuesFromGui();
        String selectedPanel = getSelectedPanel(tabbedPane);

        configItems = new Vector<ConfigValue>();
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
                                   Hashtable<String,Vector<ConfigValue>> items,
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
            String catName = myEfaConfig.getCategoryName(key);
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
                Vector<ConfigValue> v = items.get(thisCatKey);
                int y = 0;
                for (int j=0; v != null && j<v.size(); j++) {
                    ConfigValue itm = v.get(j);
                    if (itm.getType() == EfaConfig.TYPE_PUBLIC ||
                        (itm.getType() == EfaConfig.TYPE_EXPERT && expertMode.isSelected())) {
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
            ConfigValue item = configItems.get(i);
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

    void closeButton_actionPerformed(ActionEvent e) {
        getValuesFromGui();
        Daten.efaConfig.checkNewConfigValues(myEfaConfig);
        Daten.efaConfig = myEfaConfig;
        Daten.efaConfig.writeFile();
        Daten.efaConfig.setExternalParameters(true);
        Daten.efaConfig.checkForRequiredPlugins();
        super.closeButton_actionPerformed(e);
    }

    // @todo remove again, just for test purposes!!
    public static void main(String[] args) {
        Daten.initialize(Daten.APPL_CLI);
        Daten.efaConfig = new EfaConfig(Daten.efaCfgDirectory + "efa2.cfg");
        if (!EfaUtil.canOpenFile(Daten.efaConfig.getFileName())) {
            if (!Daten.efaConfig.writeFile()) {
                String msg = LogString.logstring_fileCreationFailed(Daten.efaConfig.getFileName(),
                        International.getString("Konfigurationsdatei"));
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDCREATE, msg);
            }
            String msg = LogString.logstring_fileNewCreated(Daten.efaConfig.getFileName(),
                    International.getString("Konfigurationsdatei"));
            Logger.log(Logger.WARNING, Logger.MSG_CORE_EFACONFIGCREATEDNEW, msg);
        }
        if (!Daten.efaConfig.readFile()) {
            String msg = LogString.logstring_fileOpenFailed(Daten.efaConfig.getFileName(),
                    International.getString("Konfigurationsdatei"));
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDOPEN, msg);
        }
        Daten.efaConfig.keys.put("F6", "teste mich!");
        Daten.efaConfig.keys.put("F7", "mich bitte auch!");
        Daten.efaConfig.keys.put("F8", "und hoffentlich geht's bei mir auch trotz Sonderzeichen wie @@@ und -->!");
        EfaConfigFrame dlg = new EfaConfigFrame((Frame)null);
        Dialog.setDlgLocation(dlg,null);
        dlg.setModal(true);
        Daten.iniSplashScreen(false);
        dlg.show();
        System.exit(0);
    }

    public EfaConfig getWorkingConfig() {
        return myEfaConfig;
    }

}
