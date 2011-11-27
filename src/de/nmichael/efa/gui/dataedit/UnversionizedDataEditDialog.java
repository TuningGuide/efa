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

import de.nmichael.efa.gui.dataedit.DataEditDialog;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.gui.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class UnversionizedDataEditDialog extends DataEditDialog {

    protected DataRecord dataRecord;
    protected boolean newRecord;
    protected boolean _dontSaveRecord = false;

    public UnversionizedDataEditDialog(Frame parent, String title, DataRecord dataRecord, boolean newRecord) {
        super(parent, title, null);
        this.dataRecord = dataRecord;
        this.newRecord = newRecord;
        iniDefaults();
        setItems((dataRecord != null ? dataRecord.getGuiItems() : null));
    }

    public UnversionizedDataEditDialog(JDialog parent, String title, DataRecord dataRecord, boolean newRecord) {
        super(parent, title, null);
        this.dataRecord = dataRecord;
        this.newRecord = newRecord;
        iniDefaults();
        setItems((dataRecord != null ? dataRecord.getGuiItems() : null));
    }

    protected void iniDefaults() {
        // implement in subclass if necessary
    }

    boolean saveRecord() throws InvalidValueException {
        for (IItemType item : getItems()) {
            if (!item.isValidInput() && item.isVisible()) {
                throw new InvalidValueException(item, item.getInvalidErrorText());
            }
        }
        try {
            dataRecord.saveGuiItems(getItems());
            if (!_dontSaveRecord) {
                if (newRecord) {
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
            Dialog.error("Die Änderungen konnten nicht gespeichert werden." + "\n" + e.toString());
            return false;
        }
    }

    boolean checkAndSaveChanges() {
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
                    einv.displayMessage();
                    return false;
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
        if (!checkAndSaveChanges()) {
            return false;
        }
        return super.cancel();
    }


}
