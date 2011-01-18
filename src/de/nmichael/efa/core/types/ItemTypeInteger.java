/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.types;

import de.nmichael.efa.util.*;

// @i18n complete

public class ItemTypeInteger extends ItemTypeLabelValue {

    public static int UNSET = Integer.MIN_VALUE;

    private int value;
    private int min;
    private int max;
    private boolean allowUnset;

    public ItemTypeInteger(String name, int value, int min, int max, boolean allowUnset,
            int type, String category, String description) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.allowUnset = allowUnset;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        try {
            if (value.length() == 0 && allowUnset) {
                this.value = UNSET;
            } else {
                this.value = Integer.parseInt(value);
                if (this.value < min) {
                    this.value = min;
                }
                if (this.value > max) {
                    this.value = max;
                }
            }
        } catch (Exception e) {
            if (dlg == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                           "Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public String toString() {
        if (allowUnset && value == UNSET) {
            return "";
        }
        return Integer.toString(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isSet() {
        return (!allowUnset) || value != UNSET;
    }

}
