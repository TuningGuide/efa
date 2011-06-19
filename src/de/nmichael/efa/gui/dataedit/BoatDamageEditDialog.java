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
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class BoatDamageEditDialog extends UnversionizedDataEditDialog {

    public BoatDamageEditDialog(Frame parent, BoatDamageRecord r, boolean newRecord) {
        super(parent, International.getString("Bootsschaden"), r, newRecord);
    }

    public BoatDamageEditDialog(JDialog parent, BoatDamageRecord r, boolean newRecord) {
        super(parent, International.getString("Bootsschaden"), r, newRecord);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public static void newBoatDamage(JFrame parent, BoatRecord boat) {
        BoatDamages boatDamages = Daten.project.getBoatDamages(false);
        AutoIncrement autoIncrement = Daten.project.getAutoIncrement(false);
        int val = autoIncrement.nextAutoIncrementValue(boatDamages.data().getStorageObjectType());
        BoatDamageRecord r = boatDamages.createBoatDamageRecord(boat.getId(), val);
        r.setReportDate(DataTypeDate.today());
        r.setReportTime(DataTypeTime.now());
        r.setShowOnlyAddDamageFields(true);
        BoatDamageEditDialog dlg = new BoatDamageEditDialog(parent, r, true);
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            /* @todo (P3) send boat damage message to admin for
            String boot = this.boot.getText().trim();
            NachrichtAnAdminFrame dlg = new NachrichtAnAdminFrame(this, Daten.nachrichten, Nachricht.BOOTSWART,
                    (getObmannTextField(getObmann()) != null ? getObmannTextField(getObmann()).getText() : null),
                    International.getString("Bootsschaden"),
                    International.getString("LfdNr") + ": " + lfdnr.getText() + "\n"
                    + International.getString("Datum") + ": " + datum.getText() + "\n"
                    + International.getString("Boot") + ": " + boot + "\n"
                    + International.getString("Mannschaft") + ": " + stmMannsch2String() + "\n"
                    + "-----------------------\n"
                    + International.getString("Beschreibung des Schadens") + ":\n");
            Dialog.setDlgLocation(dlg, this);
            dlg.setModal(true);
            dlg.show();
            if (dlg.isGesendet()) {
                String s = this.bemerk.getText().trim();
                this.bemerk.setText(s + (s.length() > 0 ? "; " : "") + International.getString("Bootsschaden gemeldet") + ".");
                Nachricht n = dlg.getLastMessage();
                if (n != null && n.nachricht != null) {
                    String t = "";
                    int pos = n.nachricht.indexOf(International.getString("Beschreibung des Schadens") + ":");
                    if (pos >= 0) {
                        t = n.nachricht.substring(pos + (International.getString("Beschreibung des Schadens") + ":").length());
                    }
                    if (t.length() == 0) {
                        t = International.getString("Bootsschaden"); // generic
                    }
                    t = EfaUtil.replace(t, "\n", " ", true).trim();
                    efaDirektFrame.setBootstatusSchaden(boot, t);
                }
            }
             */
        }
    }

}
