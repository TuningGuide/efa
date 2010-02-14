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

public class ConfigTypeDate extends ConfigTypeLabelValue {

    private TMJ value;

    public ConfigTypeDate(String name, TMJ value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        try {
            this.value = TMJ.parseTMJ(value);
        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                       "EfaConfig: Invalid value for parameter "+name+": "+value);
        }
    }

    public String toString() {

        if (value.tag < 0 || value.monat < 0 || value.jahr < 0) {
            return "";
        }
        return EfaUtil.int2String(value.tag,2) + "." + EfaUtil.int2String(value.monat,2) + "." + value.jahr;
    }

}
