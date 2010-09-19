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

import java.util.Vector;
import java.util.Hashtable;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.gui.BaseDialog;

// @i18n complete

public abstract class ItemType implements IItemType {

    protected String name;
    protected int type;
    protected String category;
    protected String description;
    protected BaseDialog dlg;

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

    public boolean isValidInput() {
        return true;
    }

}
