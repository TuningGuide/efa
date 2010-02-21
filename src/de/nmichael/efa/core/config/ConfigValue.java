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

}
