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
            if (value != null && value.trim().length()>0) {
                value = EfaUtil.correctTime(value,true);
            }
            this.value = TMJ.parseTMJ(value);
        } catch (Exception e) {
            if (efaConfigFrame == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                           "EfaConfig: Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public String toString() {
        if (value.tag < 0 || value.monat < 0 || value.jahr < 0) {
            return "";
        }
        return EfaUtil.int2String(value.tag,2) + ":" + EfaUtil.int2String(value.monat,2) + ":" + EfaUtil.int2String(value.jahr,2);
    }

    public boolean isSet() {
        return value.tag != -1 && value.monat != -1 && value.jahr != -1;
    }

    public int getValueHour() {
        return value.tag;
    }

    public int getValueMinute() {
        return value.monat;
    }

    public int getValueSecond() {
        return value.jahr;
    }

    public TMJ getTime() {
        return new TMJ(value.tag, value.monat, value.jahr);
    }

    public void setValueHour(int hour) {
        if (hour < 0) {
            hour = 0;
        }
        if (hour > 23) {
            hour = 23;
        }
        value.tag = hour;
    }

    public void setValueMinute(int minute) {
        if (minute < 0) {
            minute = 0;
        }
        if (minute > 59) {
            minute = 59;
        }
        value.monat = minute;
    }

    public void setValueSecond(int second) {
        if (second < 0) {
            second = 0;
        }
        if (second > 59) {
            second = 59;
        }
        value.jahr = second;
    }

    public void unset() {
        value.tag = -1;
        value.monat = -1;
        value.jahr = -1;
    }

}
