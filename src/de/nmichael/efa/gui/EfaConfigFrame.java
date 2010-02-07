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

// @i18n complete
public class EfaConfigFrame extends BaseDialog {

    private static EfaConfig efaConfig; // @todo remove again, just for test purposes!! --> replace by Daten.efaConfig
    private JTabbedPane tabbedPane;
    private JCheckBox expertMode;

    private Hashtable<String,Hashtable> categories;
    private Hashtable<String,Vector<ConfigValue>> items;
    private Vector<ConfigValue> configItems;

    public EfaConfigFrame(Frame parent) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
    }

    protected void iniDialog() throws Exception {
        categories = new Hashtable<String,Hashtable>();                // category          -> sub-categories
        items = new Hashtable<String,Vector<ConfigValue>>(); // categoryhierarchy -> config items

        // build category hierarchy
        String[] names = efaConfig.getParameterNames();
        for (int i=0; i<names.length; i++) {
            ConfigValue cfg = efaConfig.getParameter(names[i]);
            String[] cats = EfaConfig.getCategoryKeyArray(cfg.getCategory());
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
            ConfigValue cfg = efaConfig.getParameter(names[i]);
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

    private void updateGui() {
        // @todo: if configItems != null get current values!!
        // @todo: remember selected tabbed pane and restore
        configItems = new Vector<ConfigValue>();
        if (tabbedPane != null) {
            mainPanel.remove(tabbedPane);
        }
        tabbedPane = new JTabbedPane();
        recursiveBuildGui(categories,items,"",tabbedPane);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        this.validate();
    }

    private void recursiveBuildGui(Hashtable<String,Hashtable> categories, 
                                   Hashtable<String,Vector<ConfigValue>> items,
                                   String catKey,
                                   JTabbedPane tabbedPane) {
        Object[] cats = categories.keySet().toArray();
        Arrays.sort(cats);
        for (int i=0; i<cats.length; i++) {
            String key = (String)cats[i];
            String thisCatKey = (catKey.length() == 0 ? key : EfaConfig.makeCategory(catKey, key));
            String catName = efaConfig.getCategoryName(key);
            Hashtable<String,Hashtable> subCat = categories.get(key);
            if (subCat.size() != 0) {
                JTabbedPane subTabbedPane = new JTabbedPane();
                tabbedPane.add(subTabbedPane, catName);
                recursiveBuildGui(subCat, items, thisCatKey, subTabbedPane);
            } else {
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                Vector<ConfigValue> v = items.get(thisCatKey);
                int y = 0;
                for (int j=0; v != null && j<v.size(); j++) {
                    ConfigValue itm = v.get(j);
                    if (itm.getType() == EfaConfig.TYPE_PUBLIC ||
                        (itm.getType() == EfaConfig.TYPE_EXPERT && expertMode.isSelected())) {
                        y += itm.displayOnGui(this,panel,y);
                        configItems.add(itm);
                    }
                }
                tabbedPane.add(panel, catName);
            }
        }
    }

    void expertModeChanged(ActionEvent e) {
        updateGui();
    }

    void closeButton_actionPerformed(ActionEvent e) {
        for (int i=0; i<configItems.size(); i++) {
            ConfigValue item = configItems.get(i);
            item.getValueFromGui();
        }
        efaConfig.writeFile();
        super.closeButton_actionPerformed(e);
    }

    // @todo remove again, just for test purposes!!
    public static void main(String[] args) {
        Daten.initialize(Daten.APPL_CLI);
        efaConfig = new EfaConfig(Daten.efaCfgDirectory + "efa2.cfg");
        if (!EfaUtil.canOpenFile(efaConfig.getFileName())) {
            if (!efaConfig.writeFile()) {
                String msg = LogString.logstring_fileCreationFailed(efaConfig.getFileName(),
                        International.getString("Konfigurationsdatei"));
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDCREATE, msg);
            }
            String msg = LogString.logstring_fileNewCreated(efaConfig.getFileName(),
                    International.getString("Konfigurationsdatei"));
            Logger.log(Logger.WARNING, Logger.MSG_CORE_EFACONFIGCREATEDNEW, msg);
        }
        if (!efaConfig.readFile()) {
            String msg = LogString.logstring_fileOpenFailed(efaConfig.getFileName(),
                    International.getString("Konfigurationsdatei"));
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDOPEN, msg);
        }
        efaConfig.keys.put("F6", "teste mich!");
        efaConfig.keys.put("F7", "mich bitte auch!");
        efaConfig.keys.put("F8", "und hoffentlich geht's bei mir auch trotz Sonderzeichen wie @@@ und -->!");
        EfaConfigFrame dlg = new EfaConfigFrame(null);
        Dialog.setDlgLocation(dlg,null);
        dlg.setModal(true);
        Daten.iniSplashScreen(false);
        dlg.show();
        System.exit(0);
    }

}
