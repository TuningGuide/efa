/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.dataedit;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.IItemListener;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete
public class ClubworkEditDialog extends UnversionizedDataEditDialog implements IItemListener {

    public ClubworkEditDialog(Frame parent, ClubworkRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Vereinsarbeit"), r, newRecord, admin);
        ini4Permissions(admin);
        initListener();
    }

    public ClubworkEditDialog(JDialog parent, ClubworkRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Vereinsarbeit"), r, newRecord, admin);
        ini4Permissions(admin);
        initListener();
    }

    private void ini4Permissions(AdminRecord admin) {
//        if (admin != null && !admin.isAllowedEditClubwork()) {
//            setShowVersionPanel(false);
//            setPromptToEnterValidity(false);
//            allowConflicts = false;
//        }
    }


    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void initListener() {
        IItemType item;
        item = getItem(ClubworkRecord.DESCRIPTION);
        if (item != null) {
            item.registerItemListener(this);
        }
        item = getItem(ClubworkRecord.HOURS);
        if (item != null) {
            item.registerItemListener(this);
        }
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType.getName().equals(ClubworkRecord.DESCRIPTION) ||
            itemType.getName().equals(ClubworkRecord.HOURS)) {
            if (newRecord && Daten.efaConfig.getValueAutogenAlias() &&
                event instanceof FocusEvent && event.getID() == FocusEvent.FOCUS_LOST) {
                ItemTypeString firstName = (ItemTypeString)getItem(ClubworkRecord.DESCRIPTION);
                ItemTypeString lastName = (ItemTypeString)getItem(ClubworkRecord.HOURS);
                ItemTypeString inputShortcut = (ItemTypeString)getItem(ClubworkRecord.INPUTSHORTCUT);
                if (firstName != null && lastName != null && inputShortcut != null) {
                    String sf = firstName.getValueFromField();
                    String sl = lastName.getValueFromField();
                    inputShortcut.parseAndShowValue(EfaUtil.getInputShortcut(sf, sl));
                }
            }
        }
    }
}
