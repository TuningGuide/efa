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

    protected DataTypeTime value;
    protected boolean withSeconds = true;
    protected ItemTypeTime mustBeBefore;
    protected ItemTypeTime mustBeAfter;
    protected boolean mustBeCanBeEqual = false;

    public ItemTypeTime(String name, DataTypeTime value, int type,
            String category, String description) {
        this.name = name;
        this.value = (value != null ? value : new DataTypeTime());
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
        if (mustBeBefore != null && isSet() && value.isSet() && !value.isBefore(mustBeBefore.value)) {
            return mustBeCanBeEqual && value.equals(mustBeBefore.value);
        }
        if (mustBeAfter != null && isSet() && value.isSet() && !value.isAfter(mustBeAfter.value)) {
            return mustBeCanBeEqual && value.equals(mustBeAfter.value);
        }
        if (isNotNullSet()) {
            return isSet();
        }
        return true;
    }

    public void setMustBeBefore(ItemTypeTime item, boolean mayAlsoBeEqual) {
        mustBeBefore = item;
        mustBeCanBeEqual = mayAlsoBeEqual;
    }

    public void setMustBeAfter(ItemTypeTime item, boolean mayAlsoBeEqual) {
        mustBeAfter = item;
        mustBeCanBeEqual = mayAlsoBeEqual;
    }

}
