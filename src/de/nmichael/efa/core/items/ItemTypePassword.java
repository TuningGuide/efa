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

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.BaseDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ItemTypePassword extends ItemTypeString {

    public ItemTypePassword(String name, String value, int type,
            String category, String description) {
        super(name, value, type, category, description);
    }
    
    protected JComponent initializeField() {
        JPasswordField f = new JPasswordField();
        return f;
    }

}
