/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import java.util.*;

// @i18n complete

public class DataRecord {

    private Hashtable data = new Hashtable();

    public void set(String fieldName, Object data) {
        this.data.put(fieldName, data);
    }

    public Object get(String fieldName) {
        return this.data.get(fieldName);
    }

}
