/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

import de.nmichael.efa.core.items.ItemTypeLabelValue;
import de.nmichael.efa.data.types.DataTypeTime;
import de.nmichael.efa.util.*;

// @i18n complete

public class ItemTypeTime extends ItemTypeLabelTextfield {

    DataTypeTime value;
    boolean withSeconds = true;

    public ItemTypeTime(String name, DataTypeTime value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void enableSeconds(boolean withSeconds) {
        this.withSeconds = withSeconds;
        if (value != null) {
            value.enableSeconds(withSeconds);
        }
    }

    public void parseValue(String value) {
        try {
            if (value != null && value.trim().length()>0) {
                value = EfaUtil.correctTime(value,true);
            }
            this.value = DataTypeTime.parseTime(value);
            this.value.enableSeconds(withSeconds);
        } catch (Exception e) {
            if (dlg == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                           "Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public String toString() {
        return (value != null ? value.toString() : "");
    }

    public boolean isSet() {
        return value != null && value.isSet();
    }

    public int getValueHour() {
        return value.getHour();
    }

    public int getValueMinute() {
        return value.getMinute();
    }

    public int getValueSecond() {
        return value.getSecond();
    }

    public DataTypeTime getTime() {
        return new DataTypeTime(value.getHour(), value.getMinute(), value.getSecond());
    }

    public void setValueHour(int hour) {
        value.setHour(hour);
    }

    public void setValueMinute(int minute) {
        value.setMinute(minute);
    }

    public void setValueSecond(int second) {
        value.setSecond(second);
    }

    public void unset() {
        value.unset();
    }

    public boolean isValidInput() {
        if (isNotNullSet()) {
            return isSet();
        }
        return true;
    }

}
