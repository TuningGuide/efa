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

// @i18n complete

public class ConfigValue<E> {

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
