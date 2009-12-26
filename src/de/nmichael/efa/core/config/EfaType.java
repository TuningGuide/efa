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

// @i18n complete

public class EfaType {
    
    public String category;
    public String type;
    public String value;

    public EfaType(String category, String type, String value) {
        this.category = category;
        this.type = type;
        this.value = value;
    }

}
