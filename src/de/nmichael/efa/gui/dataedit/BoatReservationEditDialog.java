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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class BoatReservationEditDialog extends UnversionizedDataEditDialog implements IItemListener {

    public BoatReservationEditDialog(Frame parent, BoatReservationRecord r, boolean newRecord, boolean allowWeeklyReservation) {
        super(parent, International.getString("Reservierung"), r, newRecord);
        initListener();
        setAllowWeeklyReservation(allowWeeklyReservation);
    }

    public BoatReservationEditDialog(JDialog parent, BoatReservationRecord r, boolean newRecord, boolean allowWeeklyReservation) {
        super(parent, International.getString("Reservierung"), r, newRecord);
        initListener();
        setAllowWeeklyReservation(allowWeeklyReservation);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void initListener() {
        IItemType itemType = null;
        for (IItemType item : items) {
            if (item.getName().equals(BoatReservationRecord.TYPE)) {
                ((ItemTypeRadioButtons)item).registerItemListener(this);
                itemType = item;
            }
        }
        itemListenerAction(itemType, null);
    }

    public void itemListenerAction(IItemType item, AWTEvent event) {
        if (item != null && item.getName().equals(BoatReservationRecord.TYPE)) {
            String type = item.getValueFromField();
            if (type == null) {
                return;
            }
            for (IItemType it : items) {
                if (it.getName().equals(BoatReservationRecord.DAYOFWEEK)) {
                    it.setVisible(type.equals(BoatReservationRecord.TYPE_WEEKLY));
                }
                if (it.getName().equals(BoatReservationRecord.DATEFROM)) {
                    it.setVisible(type.equals(BoatReservationRecord.TYPE_ONETIME));
                }
                if (it.getName().equals(BoatReservationRecord.DATETO)) {
                    it.setVisible(type.equals(BoatReservationRecord.TYPE_ONETIME));
                }
            }
        }
    }

    private void setAllowWeeklyReservation(boolean allowWeeklyReservation) {
        if (!allowWeeklyReservation) {
            for (IItemType it : items) {
                if (it.getName().equals(BoatReservationRecord.TYPE)) {
                    it.parseAndShowValue(BoatReservationRecord.TYPE_ONETIME);
                    it.setVisible(false);
                    it.setEditable(false);
                    itemListenerAction(it, null);
                    break;
                }
            }
        }
    }


}
