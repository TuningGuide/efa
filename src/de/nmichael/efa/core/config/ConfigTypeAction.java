/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.EfaConfigFrame;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public class ConfigTypeAction extends ConfigValue {

    public static final int ACTION_GENERATE_ROWING_BOAT_TYPES = 1;
    public static final int ACTION_GENERATE_CANOEING_BOAT_TYPES = 2;

    private int action;
    protected EfaConfigFrame efaConfigFrame;
    protected JButton button;


    public ConfigTypeAction(String name, int action,
            int type, String category, String description) {
        this.name = name;
        this.action = action;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        return; // this ConfigType does not store any values
    }

    public String toString() {
        return ""; // this ConfigType does not store any values
    }

    public int displayOnGui(EfaConfigFrame dlg, JPanel panel, int y) {
        efaConfigFrame = dlg;

        button = new JButton();
        Dialog.setPreferredSize(button, 500, 21);
        button.setText(description);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { buttonHit(e); }
        });
        if (type == EfaConfig.TYPE_EXPERT) {
            button.setForeground(Color.red);
        }
        panel.add(button, new GridBagConstraints(0, y, 3, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        // nothing to do
        // this ConfigType does not store any values
    }

    private void buttonHit(ActionEvent e) {
        switch(action) {
            case ACTION_GENERATE_ROWING_BOAT_TYPES:
                generateTypes(EfaTypes.SELECTION_ROWING);
                break;
            case ACTION_GENERATE_CANOEING_BOAT_TYPES:
                generateTypes(EfaTypes.SELECTION_CANOEING);
                break;
        }
    }

    private void generateTypes(int selection) {
        String sel = "";
        switch(selection) {
            case ACTION_GENERATE_ROWING_BOAT_TYPES:
                sel = International.getString("Rudern");
                break;
            case ACTION_GENERATE_CANOEING_BOAT_TYPES:
                sel = International.getString("Kanufahren");
                break;
        }
        if (Dialog.yesNoDialog(International.getString("Frage"),
                International.getMessage("Möchtest Du alle Standard-Bootstypen für {rowing_or_canoeing} jetzt neu hinzufügen? "+
                "Manuell geänderte oder hinzugefügte Bootstypen bleiben dabei bestehen.", sel)) != Dialog.YES) {
            return;
        }

        EfaTypes newTypes = new EfaTypes((String)null);
        newTypes.setToLanguage_Boats(International.getResourceBundle(), selection, true);
        EfaConfig myEfaConfig = efaConfigFrame.getWorkingConfig();

        int count = 0;
        count += addNewTypes(newTypes,myEfaConfig,myEfaConfig.typesBoat,EfaTypes.CATEGORY_BOAT);
        count += addNewTypes(newTypes,myEfaConfig,myEfaConfig.typesNumSeats,EfaTypes.CATEGORY_NUMSEATS);
        count += addNewTypes(newTypes,myEfaConfig,myEfaConfig.typesRigging,EfaTypes.CATEGORY_RIGGING);
        count += addNewTypes(newTypes,myEfaConfig,myEfaConfig.typesCoxing,EfaTypes.CATEGORY_COXING);

        Dialog.infoDialog(International.getMessage("Es wurden {count} Bootstypen neu generiert (sichtbar im Expertenmodus).", count));
        efaConfigFrame.updateGui();
    }

    private int addNewTypes(EfaTypes types, EfaConfig config, ConfigTypeHashtable<String> cfgTypes, String cat) {
        int count = 0;
        for (int i=0; i<types.size(cat); i++) {
            String key = types.getType(cat, i);
            String val = types.getValue(cat, i);
            if (cfgTypes.get(key) == null) {
                cfgTypes.put(key, val);
                count++;
            }
        }
        return count;

    }

}
