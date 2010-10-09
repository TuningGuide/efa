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

import de.nmichael.efa.util.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import javax.swing.*;

// @i18n complete

public class ItemTypeDate extends ItemTypeLabelValue {

    private DataTypeDate value;
    private boolean notNull = false;

    public ItemTypeDate(String name, DataTypeDate value, int type,
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
                value = EfaUtil.correctDate(value);
            }
            this.value = DataTypeDate.parseDate(value);
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

    public int getValueDay() {
        return value.getDay();
    }

    public int getValueMonth() {
        return value.getMonth();
    }

    public int getValueYear() {
        return value.getYear();
    }

    public DataTypeDate getDate() {
        return new DataTypeDate(value.getDay(), value.getMonth(), value.getYear());
    }

    public void setValueDay(int day) {
        value.setDay(day);
    }

    public void setValueMonth(int month) {
        value.setMonth(month);
    }

    public void setValueYear(int year) {
        value.setYear(year);
    }

    public void unset() {
        value.unset();
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isNotNullSet() {
        return notNull;
    }

    public boolean isValidInput() {
        if (isNotNullSet()) {
            return isSet();
        }
        return true;
    }

}
