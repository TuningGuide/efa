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
public class BoatReservationListDialog extends DataListDialog {

    boolean allowNewReservationsWeekly = true;

    public BoatReservationListDialog(Frame parent) {
        super(parent, International.getString("Bootsreservierungen"), Daten.project.getBoatReservations(false), 0);
        iniValues(null, true, true, true);
    }

    public BoatReservationListDialog(JDialog parent) {
        super(parent, International.getString("Bootsreservierungen"), Daten.project.getBoatReservations(false), 0);
        iniValues(null, true, true, true);
    }

    public BoatReservationListDialog(Frame parent, UUID boatId) {
        super(parent, International.getString("Bootsreservierungen"), Daten.project.getBoatReservations(false), 0);
        iniValues(boatId, true, true, true);
    }

    public BoatReservationListDialog(JDialog parent, UUID boatId) {
        super(parent, International.getString("Bootsreservierungen"), Daten.project.getBoatReservations(false), 0);
        iniValues(boatId, true, true, true);
    }

    public BoatReservationListDialog(Frame parent, UUID boatId, boolean allowNewReservations, boolean allowNewReservationsWeekly, boolean allowEditDeleteReservations) {
        super(parent, International.getString("Bootsreservierungen"), Daten.project.getBoatReservations(false), 0);
        iniValues(boatId, allowNewReservations, allowNewReservationsWeekly, allowEditDeleteReservations);
    }

    public BoatReservationListDialog(JDialog parent, UUID boatId, boolean allowNewReservations, boolean allowNewReservationsWeekly, boolean allowEditDeleteReservations) {
        super(parent, International.getString("Bootsreservierungen"), Daten.project.getBoatReservations(false), 0);
        iniValues(boatId, allowNewReservations, allowNewReservationsWeekly, allowEditDeleteReservations);
    }

    private void iniValues(UUID boatId, boolean allowNewReservations, boolean allowNewReservationsWeekly, boolean allowEditDeleteReservations) {
        if (boatId != null) {
            this.filterFieldName  = BoatReservationRecord.BOATID;
            this.filterFieldValue = boatId.toString();
        }
        if (allowNewReservations && allowEditDeleteReservations) {
            actionText = null; // default: ADD, EDIT, DELETE
            actionType = null; // default: ADD, EDIT, DELETE
        } else if (allowNewReservations) {
            actionText = new String[] { ItemTypeDataRecordTable.ACTIONTEXT_NEW };
            actionType = new int[] { ItemTypeDataRecordTable.ACTION_NEW };
        } else if (allowEditDeleteReservations) {
            actionText = new String[] { ItemTypeDataRecordTable.ACTIONTEXT_EDIT, ItemTypeDataRecordTable.ACTIONTEXT_DELETE };
            actionType = new int[] { ItemTypeDataRecordTable.ACTION_EDIT, ItemTypeDataRecordTable.ACTION_DELETE };
        } else {
            actionText = new String[] { };
            actionType = new int[] { };
        }
        this.allowNewReservationsWeekly = allowNewReservationsWeekly;
    }


    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public DataEditDialog createNewDataEditDialog(JDialog parent, Persistence persistence, DataRecord record) {
        boolean newRecord = (record == null);
        if (record == null && persistence != null && filterFieldValue != null) {
            record = ((BoatReservations)persistence).createBoatReservationsRecord(UUID.fromString(filterFieldValue));
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
                        record = ((BoatReservations)persistence).createBoatReservationsRecord(boats.getBoat(s, now).getId());
                    }
                } catch(Exception e) {
                    Logger.logdebug(e);
                }
            }
        }
        if (record == null) {
            return null;
        }
        return new BoatReservationEditDialog(parent, (BoatReservationRecord)record, newRecord, allowNewReservationsWeekly);
    }
}
