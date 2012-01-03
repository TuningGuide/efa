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

import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.ItemTypeBoolean;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete
public class MessageEditDialog extends UnversionizedDataEditDialog {

    public MessageEditDialog(Frame parent, MessageRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Nachricht"), r, newRecord, admin);
        ini(admin);
    }

    public MessageEditDialog(JDialog parent, MessageRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Nachricht"), r, newRecord, admin);
        ini(admin);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void ini (AdminRecord admin) {
        ItemTypeBoolean msgRead = (ItemTypeBoolean)getItem(MessageRecord.READ);
        if (msgRead != null) {
            MessageRecord r = (MessageRecord)dataRecord;
            if (r.getTo() == null || r.getTo().equals(MessageRecord.TO_ADMIN)) {
                msgRead.setEnabled(admin != null && admin.isAllowedMsgMarkReadAdmin());
                if (admin != null && admin.isAllowedMsgAutoMarkReadAdmin()) {
                    msgRead.setValue(true);
                    msgRead.showValue();
                }
            } else {
                msgRead.setEnabled(admin != null && admin.isAllowedMsgMarkReadBoatMaintenance());
                if (admin != null && admin.isAllowedMsgAutoMarkReadBoatMaintenance()) {
                    msgRead.setValue(true);
                    msgRead.showValue();
                }
            }
        }
        // @todo (P4) add a "print message" button
    }

    public void updateGui() {
        super.updateGui();
        if (newRecord && getItem(MessageRecord.FROM) != null) {
            this.setRequestFocus(getItem(MessageRecord.FROM));
        }
    }


}
