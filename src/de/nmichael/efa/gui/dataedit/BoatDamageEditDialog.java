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
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete
public class BoatDamageEditDialog extends UnversionizedDataEditDialog implements IItemListener {

    public BoatDamageEditDialog(Frame parent, BoatDamageRecord r, boolean newRecord) {
        super(parent, International.getString("Bootsschaden"), r, newRecord);
        initListener();
    }

    public BoatDamageEditDialog(JDialog parent, BoatDamageRecord r, boolean newRecord) {
        super(parent, International.getString("Bootsschaden"), r, newRecord);
        initListener();
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void initListener() {
        IItemType itemType = null;
        for (IItemType item : items) {
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
                r.getCompleteDamageInfo());
    }

    public static void newBoatDamage(Window parent, BoatRecord boat) {
        BoatDamages boatDamages = Daten.project.getBoatDamages(false);
        AutoIncrement autoIncrement = Daten.project.getAutoIncrement(false);
        int val = autoIncrement.nextAutoIncrementIntValue(boatDamages.data().getStorageObjectType());
        BoatDamageRecord r = boatDamages.createBoatDamageRecord(boat.getId(), val);
        r.setReportDate(DataTypeDate.today());
        r.setReportTime(DataTypeTime.now());
        r.setShowOnlyAddDamageFields(true);
        BoatDamageEditDialog dlg = (parent instanceof JDialog ? 
            new BoatDamageEditDialog((JDialog)parent, r, true) :
            new BoatDamageEditDialog((JFrame)parent, r, true));
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            dlg.sendNotification();
        }
    }

}
