/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

// @i18n complete

import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.*;
import javax.swing.*;

public interface IItemListenerActionTable {

    public void itemListenerActionTable(int actionId, DataRecord[] records);
    public DataEditDialog createNewDataEditDialog(JDialog parent, DataRecord record);

}
