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

import java.util.Vector;
import java.util.Hashtable;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Logger;

// @i18n complete

public class ConfigValue<E> {

    public static int TYPE_STRING = 0;
    public static int TYPE_INTEGER = 1;
    public static int TYPE_LONG = 2;
    public static int TYPE_BOOLEAN = 3;
    public static int TYPE_TMJ = 4;
    public static int TYPE_HASHTABLE = 5;
    public static int NUMBER_OF_TYPES = 6;

    private String name;
    private E value;
    private int type;
    private String category;
    private String description;
    private Vector<E> range;

    public ConfigValue(String name, E value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public void setValueFromString(String value) {
        Class c = this.value.getClass();
        Object v = null;
        boolean matchingTypeFound = false;
        for (int i = 0; i < NUMBER_OF_TYPES; i++) {
            switch (i) {
                case 0: // TYPE_STRING
                    v = value;
                    break;
                case 1: // TYPE_INTEGER
                    try {
                        v = Integer.parseInt(value);
                    } catch (Exception e) {
                    }
                    break;
                case 2: // TYPE_LONG
                    try {
                        v = Long.parseLong(value);
                    } catch (Exception e) {
                    }
                    break;
                case 3: // TYPE_BOOLEAN
                    try {
                        v = Boolean.parseBoolean(value);
                    } catch (Exception e) {
                    }
                    break;
                case 4: // TYPE_TMJ
                    try {
                        v = TMJ.parseTMJ(value);
                    } catch (Exception e) {
                    }
                    break;
                case 5: // TYPE_HASHTABLE
                    try {
                        v = ((ConfigTypeHashtable)this.value).parseHashtable(value);
                    } catch (Exception e) {
                    }
                    break;
                // @todo: add type int[] ???
            }
            if (c.isInstance(v)) {
                setValue((E)v);
                matchingTypeFound = true;
                break;
            }
        }
        if (!matchingTypeFound) {
            // should never happen (program error); no need to translate
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                    "EfaConfig: unsupported parameter type for parameter "+name+": "+c.getCanonicalName());
        }
    }

    public E getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setRange(Vector<E> range) {
        this.range = range;
    }

    public Vector<E> getRange() {
        return range;
    }

    public void setMinValue(E value) {
        if (range == null) {
            range = new Vector<E>();
        }
        if (range.size() <= 0) {
            range.add(value);
        } else {
            range.setElementAt(value, 0);
        }
    }

    public void setMaxValue(E value) {
        if (range == null) {
            range = new Vector<E>();
        }
        if (range.size() <= 1) {
            if (range.size() <= 0) {
                range.add(value);
            }
            range.add(value);
        } else {
            range.setElementAt(value, 1);
        }
    }

    public E getMinValue() {
        if (range == null || range.size()<1) {
            return null;
        }
        return range.get(0);
    }

    public E getMaxValue() {
        if (range == null || range.size()<2) {
            return null;
        }
        return range.get(1);
    }

}
