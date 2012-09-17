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

import de.nmichael.efa.*;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;
import javax.swing.*;

// @i18n complete
public class BoatDamageEditDialog extends UnversionizedDataEditDialog implements IItemListener {

    public BoatDamageEditDialog(Frame parent, BoatDamageRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Bootsschaden"), r, newRecord, admin);
        initListener();
    }

    public BoatDamageEditDialog(JDialog parent, BoatDamageRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Bootsschaden"), r, newRecord, admin);
        initListener();
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void initListener() {
        IItemType itemType = null;
        for (IItemType item : allGuiItems) {
            if (item.getName().equals(BoatDamageRecord.FIXED)) {
                ((ItemTypeBoolean)item).registerItemListener(this);
                itemType = item;
            }
        }
        itemListenerAction(itemType, null);
    }

    public void itemListenerAction(IItemType item, AWTEvent event) {
        if (item != null && item.getName().equals(BoatDamageRecord.FIXED)) {
            ((ItemTypeBoolean)item).getValueFromGui();
            boolean fixed = ((ItemTypeBoolean)item).getValue();
            getItem(BoatDamageRecord.GUIITEM_FIXDATETIME).setNotNull(fixed);
            getItem(BoatDamageRecord.FIXEDBYPERSONID).setNotNull(fixed);
            if (fixed) {
                ItemTypeDateTime fixedDate = (ItemTypeDateTime)getItem(BoatDamageRecord.GUIITEM_FIXDATETIME);
                fixedDate.getValueFromGui();
                if (!fixedDate.isSet()) {
                    fixedDate.parseAndShowValue(EfaUtil.getCurrentTimeStampYYYY_MM_DD_HH_MM_SS());
                }
                getItem(BoatDamageRecord.FIXEDBYPERSONID).requestFocus();
            }
        }
    }

    private void sendNotification() {
        BoatDamageRecord r = (BoatDamageRecord)dataRecord;
        Messages messages = r.getPersistence().getProject().getMessages(false);
        messages.createAndSaveMessageRecord(r.getReportedByPersonAsName(),
                MessageRecord.TO_BOATMAINTENANCE,
                International.getString("Neuer Bootsschaden") + " - " + r.getBoatAsName(),
                r.getCompleteDamageInfo() +
                (r.getLogbookText() != null && r.getLogbookText().length() > 0 ?
                    "\n" + International.getString("Fahrt") + ": " + r.getLogbookText() :
                    "")
                );
    }

    public static void newBoatDamage(Window parent, BoatRecord boat) {
        newBoatDamage(parent, boat, null, null);
    }

    public static void newBoatDamage(Window parent, BoatRecord boat, UUID personID, String logbookRecordText) {
        BoatDamages boatDamages = Daten.project.getBoatDamages(false);
        AutoIncrement autoIncrement = Daten.project.getAutoIncrement(false);
        int val = autoIncrement.nextAutoIncrementIntValue(boatDamages.data().getStorageObjectType());
        BoatDamageRecord r = boatDamages.createBoatDamageRecord(boat.getId(), val);
        r.setReportDate(DataTypeDate.today());
        r.setReportTime(DataTypeTime.now());
        r.setShowOnlyAddDamageFields(true);
        if (personID != null) {
            r.setReportedByPersonId(personID);
        }
        if (logbookRecordText != null) {
            r.setLogbookText(logbookRecordText);
        }
        BoatDamageEditDialog dlg = (parent instanceof JDialog ? 
            new BoatDamageEditDialog((JDialog)parent, r, true, null) :
            new BoatDamageEditDialog((JFrame)parent, r, true, null));
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            dlg.sendNotification();
            Dialog.infoDialog(International.getString("Vielen Dank!"),
                              International.getString("Der Bootsschaden wurde gemeldet."));
        }
    }

}
