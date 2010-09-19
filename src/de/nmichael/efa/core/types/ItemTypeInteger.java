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

// @i18n complete

public class ItemTypeInteger extends ItemTypeLabelValue {

    private int value;
    private int min;
    private int max;

    public ItemTypeInteger(String name, int value, int min, int max,
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
            this.value = Integer.parseInt(value);
            if (this.value < min) {
                this.value = min;
            }
            if (this.value > max) {
                this.value = max;
            }
        } catch (Exception e) {
            if (dlg == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                           "Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public String toString() {
        return Integer.toString(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
