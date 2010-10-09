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
import java.awt.Color;
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

    protected Color color = null;
    protected int padX = 0;
    protected int padYbefore = 0;
    protected int padYafter = 0;

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

    public void setColor(Color c) {
        this.color = c;
    }

    public void setPadding(int padX, int padYbefore, int padYafter) {
        this.padX = padX;
        this.padYbefore = padYbefore;
        this.padYafter = padYafter;
    }

}
