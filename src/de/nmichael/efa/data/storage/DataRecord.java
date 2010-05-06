/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import java.util.*;

// @i18n complete

public class DataRecord {

    private Hashtable<String,Object> data = new Hashtable<String,Object>();

    public void set(String fieldName, Object data) {
        this.data.put(fieldName, data);
    }

    public Object get(String fieldName) {
        return this.data.get(fieldName);
    }

}
