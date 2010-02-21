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

public class ConfigTypeLong extends ConfigTypeLabelValue {

    private long value;
    private long min;
    private long max;

    public ConfigTypeLong(String name, long value, long min, long max,
            int type, String category, String description) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        try {
            this.value = Long.parseLong(value);
            if (this.value < min) {
                this.value = min;
            }
            if (this.value > max) {
                this.value = max;
            }
        } catch (Exception e) {
            if (efaConfigFrame == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                           "EfaConfig: Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public String toString() {
        return Long.toString(value);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
