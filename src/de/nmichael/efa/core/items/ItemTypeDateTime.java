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

import de.nmichael.efa.data.types.*;
import de.nmichael.efa.util.*;
import java.awt.event.*;

// @i18n complete

public class ItemTypeDateTime extends ItemTypeLabelTextfield {

    private DataTypeDate dateValue;
    private DataTypeTime timeValue;
    private DataTypeDate referenceDate;
    private DataTypeTime referenceTime;

    public ItemTypeDateTime(String name, DataTypeDate dateValue, DataTypeTime timeValue, int type,
            String category, String description) {
        this.name = name;
        this.dateValue = (dateValue != null ? dateValue : new DataTypeDate());
        this.timeValue = (timeValue != null ? timeValue : new DataTypeTime());
        this.referenceDate = (dateValue != null && dateValue.isSet() ? new DataTypeDate(dateValue) : DataTypeDate.today());
        this.referenceTime = (timeValue != null && timeValue.isSet() ? new DataTypeTime(timeValue) : DataTypeTime.now());
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        try {
            if (value != null && value.trim().length()>0) {
                String t = "";
                int pos = 0;
                int[] ia = new int[6];
                for (int i=0; i<ia.length; i++) {
                    ia[i] = -1;
                }
                for (int i = 0; i < value.length(); i++) {
                    boolean isDigit = Character.isDigit(value.charAt(i));
                    if (isDigit) {
                        t = t + value.charAt(i);
                    }
                    if (!isDigit || i+1 == value.length()) {
                        if (t.length() > 0) {
                            try {
                                ia[pos++] = Integer.parseInt(t);
                            } catch (Exception e) {
                            }
                        }
                        t = "";
                    }
                }
                dateValue.setDate( (ia[0] >  0 ? ia[0] : referenceDate.getDay()),
                                   (ia[1] >  0 ? ia[1] : referenceDate.getMonth()),
                                   (ia[2] >  0 ? ia[2] : referenceDate.getYear()) );
                timeValue.setTime( (ia[3] >= 0 ? ia[3] : referenceTime.getHour()),
                                   (ia[4] >= 0 ? ia[4] : referenceTime.getMinute()),
                                   (ia[5] >= 0 ? ia[5] : referenceTime.getSecond()) );
            }
        } catch (Exception e) {
            if (dlg == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                           "Invalid value for parameter "+name+": "+value);
            }
        }
    }

    protected void field_focusLost(FocusEvent e) {
        super.field_focusLost(e);
        if (dateValue.isSet()) {
            referenceDate.setDate(dateValue);
        }
        if (timeValue.isSet()) {
            referenceTime.setTime(timeValue);
        }
    }

    public String toString() {
        String ds = (dateValue != null ? dateValue.toString() : "");
        String ts = (timeValue != null ? timeValue.toString() : "");
        return ds + (ds.length() > 0 && ts.length() > 0 ? " " : "") + ts;
    }

    public boolean isSet() {
        return dateValue.isSet() && timeValue.isSet();
    }

    public DataTypeDate getDate() {
        return new DataTypeDate(dateValue);
    }

    public DataTypeTime getTime() {
        return new DataTypeTime(timeValue);
    }

    public long getTimeStamp() {
        if (!isSet()) {
            return -1;
        }
        return dateValue.getTimestamp(timeValue);
    }

    public void unset() {
        dateValue.unset();
        timeValue.unset();
    }

    public boolean isValidInput() {
        if (isNotNullSet()) {
            return isSet();
        }
        return true;
    }

}
