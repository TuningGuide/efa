/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.core.config;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.gui.EfaConfigFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.direkt.Admin;

public class ConfigTypeHashtable<E> extends ConfigValue {

    public static int TYPE_STRING = 0;
    public static int TYPE_ADMIN = 1;
    public static int NUMBER_OF_TYPES = 2;

    private static final String DUMMY = "%%%DUMMY%%%";
    private static final String DELIM_KEYVALUE = "-->";
    private static final String DELIM_ELEMENTS = "@@@";
    private Hashtable<String,E> hash;
    private E e;
    private boolean fieldsEditable;
    
    private JTextField[] textfield;
    private Hashtable<JButton,String> delButtons;
    private EfaConfigFrame efaConfigFrame;

    public ConfigTypeHashtable(String name, E value, boolean fieldsEditable,
            int type, String category, String description) {
        this.name = name;
        this.e = value;
        this.fieldsEditable = fieldsEditable;
        this.type = type;
        this.category = category;
        this.description = description;
        iniHash();
    }

    private void iniHash() {
        hash = new Hashtable<String,E>();
        hash.put(DUMMY, e);
    }

    public void put(String s, E value) {
        hash.put(s, value);
    }

    public void remove(String s) {
        hash.remove(s);
    }

    public E get(String s) {
        return hash.get(s);
    }

    public int size() {
        return hash.size() - 1; // without dummy element
    }

    public String[] getKeysArray() {
        String[] keys = new String[size()];
        Object[] a = hash.keySet().toArray();
        Arrays.sort(a);
        int j=0;
        for (int i=0; i<a.length; i++) {
            if (!((String)a[i]).equals(DUMMY)) {
                keys[j++] = (String)a[i];
            }
        }
        return keys;
    }

    private void addToHash(Hashtable<String,E> hash, String key, String val) {
        E e = hash.get(DUMMY);
        Class c = e.getClass();
        Object v = null;
        boolean matchingTypeFound = false;
        for (int i = 0; i < NUMBER_OF_TYPES; i++) {
            switch (i) {
                case 0: // TYPE_STRING
                    v = val;
                    break;
                case 1: // TYPE_ADMIN
                    v = Admin.parseAdmin(val);
                    ((Admin)v).name = key; // make sure that Admin's name always equals the key!!
                    break;
            }
            if (v != null && c.isInstance(v)) {
                hash.put(key, (E) v);
                matchingTypeFound = true;
                break;
            }
        }
        if (!matchingTypeFound) {
            // should never happen (program error); no need to translate
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                    "ConfigTypesHashtable: unsupported value type for key " + key + ": " + c.getCanonicalName());
        }
    }

    public void parseValue(String value) {
        iniHash();
        try {
            StringTokenizer tok = new StringTokenizer(value, DELIM_ELEMENTS);
            while (tok.hasMoreTokens()) {
                String t = tok.nextToken();
                int pos = t.indexOf(DELIM_KEYVALUE);
                String key = t.substring(0, pos);
                key = new String(Base64.decode(key), Daten.ENCODING_UTF);
                String val = t.substring(pos + DELIM_KEYVALUE.length());
                val = new String(Base64.decode(val), Daten.ENCODING_UTF);
                addToHash(hash, key,val);
            }
        } catch (Exception e) {
            if (efaConfigFrame == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                        "EfaConfig: Invalid value for parameter " + name + ": " + value);
            }

        }
    }

    public String toString() {
        String s = "";

        String[] keys = new String[hash.size()];
        keys = hash.keySet().toArray(keys);
        for (int i=0; i<keys.length; i++) {
            E value = hash.get(keys[i]);
            if (keys[i].equals(DUMMY)) {
                continue;
            }
            try {
                String key = Base64.encodeBytes(keys[i].getBytes(Daten.ENCODING_UTF));
                String val = Base64.encodeBytes(value.toString().getBytes(Daten.ENCODING_UTF));
                s += (s.length() > 0 ? DELIM_ELEMENTS : "") +
                     key + DELIM_KEYVALUE + val;
            } catch(Exception e) {
                // should never happen (program error); no need to translate
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGINVALIDVALUE,
                         "ConfigTypesHashtable: cannot create string for value '"+keys[i]+"': "+e.toString());
            }
        }
        return s;
    }

    public int displayOnGui(EfaConfigFrame dlg, JPanel panel, int y) {
        efaConfigFrame = dlg;
        int padBottom = 0;

        JLabel titlelabel = new JLabel();
        Mnemonics.setLabel(dlg, titlelabel, getDescription() + ": ");
        if (type == EfaConfig.TYPE_EXPERT) {
            titlelabel.setForeground(Color.red);
        }
        JButton addButton = new JButton();
        addButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/menu_plus.gif")));
        addButton.setMargin(new Insets(0,0,0,0));
        Dialog.setPreferredSize(addButton, 19, 19);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { addButtonHit(e); }
        });

        String[] keys = getKeysArray();
        if (keys.length == 0) {
            padBottom = 20;
        }

        panel.add(titlelabel, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
                  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, padBottom, 0), 0, 0));
        panel.add(addButton, new GridBagConstraints(2, y, 2, 1, 0.0, 0.0,
                  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, padBottom, 0), 0, 0));

        textfield = new JTextField[size()];
        delButtons = new Hashtable();
        for (int i=0; i<keys.length; i++) {
            textfield[i] = new JTextField();
            textfield[i].setText(get(keys[i]).toString());
            textfield[i].setEditable(fieldsEditable);
            Dialog.setPreferredSize(textfield[i], 200, 19);
            JLabel label = new JLabel();
            Mnemonics.setLabel(dlg, label, keys[i] + ": ");
            label.setLabelFor(textfield[i]);
            if (type == EfaConfig.TYPE_EXPERT) {
                label.setForeground(Color.red);
            }
            JButton delButton = new JButton();
            delButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/menu_minus.gif")));
            delButton.setMargin(new Insets(0,0,0,0));
            Dialog.setPreferredSize(delButton, 19, 19);
            delButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) { delButtonHit(e); }
            });

            if (i+1 == keys.length) {
                padBottom = 20;
            }
            panel.add(label, new GridBagConstraints(0, y+i+1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, padBottom, 0), 0, 0));
            panel.add(textfield[i], new GridBagConstraints(1, y+i+1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, padBottom, 0), 0, 0));
            panel.add(delButton, new GridBagConstraints(2, y+i+1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, padBottom, 0), 0, 0));

            delButtons.put(delButton, keys[i]);
        }
        return keys.length+1;
    }

    private void addButtonHit(ActionEvent e) {
        String key = null;
        key = Dialog.inputDialog(International.getString("Neuen Eintrag hinzufügen"),
                                 International.getString("Bezeichnung") + ": ");
        if (key == null || key.length() == 0 || efaConfigFrame == null) {
            return;
        }
        if (hash.get(key) != null) {
            Dialog.error(International.getString("Name bereits vergeben"+"!"));
            return;
        }
        getValueFromGui();

        // instead of simply using hash.put(key, hash.get(DUMMY)), we
        // need to invoke addToHash(...) instead because of some special handling
        // implemented there (i.e. making sure for Admin data that the admin's name
        // equals the key value!).
        addToHash(hash, key, hash.get(DUMMY).toString());

        efaConfigFrame.updateGui();
    }

    private void delButtonHit(ActionEvent e) {
        String key = delButtons.get(e.getSource());
        if (key == null || efaConfigFrame == null) {
            return;
        }
        if (Dialog.yesNoDialog(International.getString("Eintrag löschen"),
                               International.getMessage("Möchtest Du den Eintrag '{entry}' wirklich löschen?",key)) == Dialog.YES) {
            getValueFromGui();
            hash.remove(key);
            efaConfigFrame.updateGui();
        }
    }

    public void getValueFromGui() {
        Hashtable<String,E> newHash = new Hashtable<String,E>();
        newHash.put(DUMMY, hash.get(DUMMY));
        String[] keys = getKeysArray();
        if (keys.length != textfield.length) {
            // This happens when an element has been added or removed from the hash.
            // Therefore, in addButtonHit(e) resp. delButtonHit(e), we first call getValueFromGui()
            // before we add or remove an item, in order to retrieve all current values, then add
            // or remove an item, and then call efaConfigFrame.updateGui(). After that, updateGui()
            // will invoke getValueFromGui() again, this time with a mismatch of keys.length and
            // textfield.length. Since we already got the values, we can abort here.
            return;
        }
        for (int i=0; i<keys.length; i++) {
            if (textfield[i] != null) {
                addToHash(newHash,keys[i],textfield[i].getText().trim());
            }
        }
        hash = newHash;
    }

}
