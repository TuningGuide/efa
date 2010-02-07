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
import java.awt.*;
import javax.swing.*;

// @i18n complete

public class ConfigTypeBoolean extends ConfigValue {

    private Boolean value;
    private JCheckBox checkbox;

    public ConfigTypeBoolean(String name, Boolean value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        try {
            this.value = Boolean.parseBoolean(value);
        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                       "EfaConfig: Invalid value for parameter "+name+": "+value);
        }
    }

    public String toString() {
        return value.toString();
    }

    public int displayOnGui(JDialog dlg, JPanel panel, int y) {
        checkbox = new JCheckBox();
        Mnemonics.setButton(dlg, checkbox, getDescription());
        checkbox.setSelected(value);
        panel.add(checkbox, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        return 1;
    }

    public void getValueFromGui() {
        if (checkbox != null) {
            value = checkbox.isSelected();
        }
    }

}
