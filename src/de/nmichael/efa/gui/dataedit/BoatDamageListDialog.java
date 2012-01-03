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
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.SimpleInputDialog;
import de.nmichael.efa.gui.util.AutoCompleteList;
import de.nmichael.efa.util.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class BoatDamageListDialog extends DataListDialog {

    public BoatDamageListDialog(Frame parent, AdminRecord admin) {
        super(parent, International.getString("Bootsschäden"), Daten.project.getBoatDamages(false), 0, admin);
        iniValues(null);
    }

    public BoatDamageListDialog(JDialog parent, AdminRecord admin) {
        super(parent, International.getString("Bootsschäden"), Daten.project.getBoatDamages(false), 0, admin);
        iniValues(null);
    }

    public BoatDamageListDialog(Frame parent, UUID boatId, AdminRecord admin) {
        super(parent, International.getString("Bootsschäden"), Daten.project.getBoatDamages(false), 0, admin);
        iniValues(boatId);
    }

    public BoatDamageListDialog(JDialog parent, UUID boatId, AdminRecord admin) {
        super(parent, International.getString("Bootsschäden"), Daten.project.getBoatDamages(false), 0, admin);
        iniValues(boatId);
    }

    private void iniValues(UUID boatId) {
        if (boatId != null) {
            this.filterFieldName  = BoatReservationRecord.BOATID;
            this.filterFieldValue = boatId.toString();
        }
        super.sortByColumn = 4;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, StorageObject persistence, DataRecord record) {
        boolean newRecord = (record == null);
        if (record == null && persistence != null && filterFieldValue != null) {
            record = ((BoatDamages)persistence).createBoatDamageRecord(UUID.fromString(filterFieldValue));
        }
        if (record == null) {
            long now = System.currentTimeMillis();
            ItemTypeStringAutoComplete boat = new ItemTypeStringAutoComplete("BOAT", "", IItemType.TYPE_PUBLIC,
                    "", International.getString("Boot"), false);
            boat.setAutoCompleteData(new AutoCompleteList(Daten.project.getBoats(false).data(), now, now));
            if (SimpleInputDialog.showInputDialog(this, International.getString("Boot auswählen"), boat)) {
                String s = boat.toString();
                try {
                    if (s != null && s.length() > 0) {
                        Boats boats = Daten.project.getBoats(false);
                        record = ((BoatDamages)persistence).createBoatDamageRecord(boats.getBoat(s, now).getId());
                    }
                } catch(Exception e) {
                    Logger.logdebug(e);
                }
            }
        }
        if (record == null) {
            return null;
        }
        return new BoatDamageEditDialog(parent, (BoatDamageRecord)record, newRecord, admin);
    }
}
