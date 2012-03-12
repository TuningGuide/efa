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
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.gui.DataPrintRecordDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete
public class UnversionizedDataEditDialog extends DataEditDialog {

    protected DataRecord dataRecord;
    protected boolean newRecord;
    protected AdminRecord admin;
    protected boolean _dontSaveRecord = false;

    public UnversionizedDataEditDialog(Frame parent, String title, 
            DataRecord dataRecord, boolean newRecord, AdminRecord admin) {
        super(parent, title, null);
        this.dataRecord = dataRecord;
        this.newRecord = newRecord;
        this.admin = admin;
        if (admin != null) {
            setPrintButton();
        }
        iniDefaults();
        setItems((dataRecord != null ? dataRecord.getGuiItems(admin) : null));
    }

    public UnversionizedDataEditDialog(JDialog parent, String title, 
            DataRecord dataRecord, boolean newRecord, AdminRecord admin) {
        super(parent, title, null);
        this.dataRecord = dataRecord;
        this.newRecord = newRecord;
        this.admin = admin;
        if (admin != null) {
            setPrintButton();
        }
        iniDefaults();
        setItems((dataRecord != null ? dataRecord.getGuiItems(admin) : null));
    }

    protected void setPrintButton() {
        JButton printButton = new JButton();
        printButton.setIcon(BaseDialog.getIcon("button_print.png"));
        printButton.setMargin(new Insets(2,2,2,2));
        printButton.setSize(35, 20);
        printButton.setToolTipText(International.getString("Drucken"));
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { printRecord(); }
        });
        dataNorthEastComponent = printButton;
    }

    protected void iniDefaults() {
        // implement in subclass if necessary
    }

    protected void warnIfVersionizedRecordOfThatNameAlreadyExists() throws InvalidValueException {
        String conflict = null;
        try {
            if (!dataRecord.getPersistence().data().getMetaData().isVersionized()) {
                return;
            }
            DataKey[] keys = dataRecord.getPersistence().data().getByFields(dataRecord.getQualifiedNameFields(),
                    dataRecord.getQualifiedNameValues(dataRecord.getQualifiedName()));
            for (int i=0; keys != null && i<keys.length; i++) {
                DataRecord r = dataRecord.getPersistence().data().get(keys[i]);
                if (!r.getDeleted()) {
                    conflict = r.getQualifiedName() + " (" + r.getValidRangeString() + ")";
                }
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        if (conflict != null) {
            if (Dialog.yesNoDialog(International.getString("Warnung"),
                    International.getString("Es existiert bereits ein gleichnamiger Datensatz!") + "\n" +
                    conflict + "\n" +
                    International.getString("Möchtest Du diesen Datensatz trotzdem erstellen?")) != Dialog.YES) {
                throw new InvalidValueException(null, null);
            }
        }
    }

    protected boolean saveRecord() throws InvalidValueException {
        for (IItemType item : getItems()) {
            if (!item.isValidInput() && item.isVisible()) {
                throw new InvalidValueException(item, item.getInvalidErrorText());
            }
        }
        try {
            dataRecord.saveGuiItems(getItems());
            if (!_dontSaveRecord) {
                if (newRecord) {
                    warnIfVersionizedRecordOfThatNameAlreadyExists();
                    dataRecord.getPersistence().data().add(dataRecord);
                } else {
                    dataRecord.getPersistence().data().update(dataRecord);
                }
                for (IItemType item : getItems()) {
                    item.setUnchanged();
                }
            }
            return true;
        } catch(EfaModifyException emodify) {
            emodify.displayMessage();
            return false;
        } catch(Exception e) {
            Logger.logdebug(e);
            if (e.toString() != null) {
                Dialog.error("Die Änderungen konnten nicht gespeichert werden." + "\n" + e.toString());
            }
            return false;
        }
    }

    boolean checkAndSaveChanges(boolean promptForInvalidValues) {
        if (getValuesFromGui()) {
            if (_dontSaveRecord) {
                try {
                    // this looks as if it doesn't make sense... but it's correct;
                    // we have to call saveRecord() to read the GUI values into the data
                    // record; there is another check in saveRecord() which will not
                    // save the record in the persistence media.
                    // Here in checkAndSaveChanges(), we would just like to skip the
                    // user prompt whether we want to save any changes. When we don't do
                    // a physical save, then we don't need to ask - we just get the GUI
                    // values into the record and that's it!
                    return saveRecord();
                } catch (InvalidValueException einv) {
                    if (promptForInvalidValues) {
                        einv.displayMessage();
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            switch (Dialog.yesNoCancelDialog(International.getString("Änderungen speichern"),
                    International.getString("Die Daten wurden verändert.") + "\n"
                    + International.getString("Möchtest Du die Änderungen jetzt speichern?"))) {
                case Dialog.YES:
                    try {
                        return saveRecord();
                    } catch (InvalidValueException einv) {
                        einv.displayMessage();
                        return false;
                    }
                case Dialog.NO:
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        if (getValuesFromGui() || this.newRecord) {
            try {
                if (!saveRecord()) {
                    return;
                }
            } catch (InvalidValueException einv) {
                einv.displayMessage();
                return;
            }
        }
        super.closeButton_actionPerformed(e);
    }

    public boolean cancel() {
        if (!checkAndSaveChanges(false)) {
            return false;
        }
        return super.cancel();
    }

    protected void printRecord() {
        DataPrintRecordDialog dlg = new DataPrintRecordDialog(this, admin, dataRecord);
        dlg.showDialog();
    }


}
