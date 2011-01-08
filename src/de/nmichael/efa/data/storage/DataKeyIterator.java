/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import java.util.*;

// @i18n complete

public class DataKeyIterator {
    
    private DataKey[] keys;
    private int i = 0;

    public DataKeyIterator(DataKey[] keys) {
        this.keys = keys;
    }

    public DataKey getCurrent() {
        if (i >= 0 && i < keys.length) {
            return keys[i];
        }
        return null;
    }

    public DataKey getFirst() {
        i = 0;
        return getCurrent();
    }

    public DataKey getLast() {
        i = keys.length - 1;
        return getCurrent();
    }

    public DataKey getNext() {
        i++;
        return getCurrent();
    }

    public DataKey getPrev() {
        i--;
        return getCurrent();
    }

}
