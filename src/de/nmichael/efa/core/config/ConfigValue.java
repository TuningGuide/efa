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

public abstract class ConfigValue implements IConfigValue {

    protected String name;
    protected int type;
    protected String category;
    protected String description;

    public String getName() {
        return name;
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

/*
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
*/
}
