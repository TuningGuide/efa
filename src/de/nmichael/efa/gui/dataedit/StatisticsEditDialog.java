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
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete
public class StatisticsEditDialog extends UnversionizedDataEditDialog {

    public StatisticsEditDialog(Frame parent, StatisticsRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Statistik"), r, newRecord, admin);
    }

    public StatisticsEditDialog(JDialog parent, StatisticsRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Statistik"), r, newRecord, admin);
    }

    public StatisticsEditDialog(JDialog parent, StatisticsRecord r, boolean newRecord, boolean dontSaveButRun, AdminRecord admin) {
        super(parent, International.getString("Statistik"), r, newRecord, admin);
        mainPanel.setMinimumSize(new Dimension(600, 400));
        _dontSaveRecord = dontSaveButRun;
        if (dontSaveButRun) {
            _closeButtonText = International.getString("Statistik erstellen");
            getItem(StatisticsRecord.POSITION).setVisible(false);
            getItem(StatisticsRecord.NAME).setVisible(false);
            getItem(StatisticsRecord.PUBLICLYAVAILABLE).setVisible(false);
        }
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

}
