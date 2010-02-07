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

// @i18n complete

public class ConfigTypeTime extends ConfigTypeLabelValue {

    TMJ value;

    public ConfigTypeTime(String name, TMJ value, int type,
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
        return value.tag + ":" + value.monat + ":" + value.jahr;
    }


}
