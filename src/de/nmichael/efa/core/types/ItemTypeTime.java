/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.types;

import de.nmichael.efa.core.types.ItemTypeLabelValue;
import de.nmichael.efa.data.types.DataTypeTime;
import de.nmichael.efa.util.*;

// @i18n complete

public class ItemTypeTime extends ItemTypeLabelValue {

    DataTypeTime value;

    public ItemTypeTime(String name, DataTypeTime value, int type,
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
            this.value = DataTypeTime.parseTime(value);
        } catch (Exception e) {
            if (dlg == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                           "Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public String toString() {
        return value.toString();
    }

    public boolean isSet() {
        return value.isSet();
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

}
