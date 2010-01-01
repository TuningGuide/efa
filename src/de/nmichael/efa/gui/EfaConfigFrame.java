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
    private Hashtable<Component,ConfigValue> configItems;

    public EfaConfigFrame(Frame parent) {
        super(parent, International.getString("Konfiguration"), International.getStringWithMnemonic("Speichern"));
    }

    protected void iniDialog() throws Exception {
        Hashtable<String,Hashtable> categories = new Hashtable<String,Hashtable>();                // category          -> sub-categories
        Hashtable<String,Vector<ConfigValue>> items = new Hashtable<String,Vector<ConfigValue>>(); // categoryhierarchy -> config items

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
        JTabbedPane tabbedPane = new JTabbedPane();
        mainPanel.setLayout(new BorderLayout());
        configItems = new Hashtable<Component,ConfigValue>();
        recursiveBuildGui(categories,items,"",tabbedPane);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
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
                for (int j=0; v != null && j<v.size(); j++) {
                    ConfigValue itm = v.get(j);
                    JLabel label = new JLabel();
                    JTextField field = new JTextField();
                    Mnemonics.setLabel(this, label, itm.getDescription() + ": ");
                    label.setLabelFor(field);
                    field.setText(itm.getValue().toString());
                    Dialog.setPreferredSize(field, 200, 19);
                    panel.add(label, new GridBagConstraints(0, j, 1, 1, 0.0, 0.0,
                              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    panel.add(field, new GridBagConstraints(1, j, 1, 1, 0.0, 0.0,
                              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    configItems.put(field, itm);
                }
                tabbedPane.add(panel, catName);
            }
        }
    }

    void closeButton_actionPerformed(ActionEvent e) {
        Object[] keys = configItems.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
            ConfigValue item = configItems.get(keys[i]);
            item.setValue(((JTextField)keys[i]).getText().trim());
        }
        efaConfig.writeFile();
        super.closeButton_actionPerformed(e);
    }
    // @todo remove again, just for test purposes!!
    public static void main(String[] args) {
        Daten.initialize(Daten.APPL_EFA);
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
        EfaConfigFrame dlg = new EfaConfigFrame(null);
        Dialog.setDlgLocation(dlg,null);
        dlg.setModal(true);
        Daten.iniSplashScreen(false);
        dlg.show();
        System.exit(0);
    }

}
