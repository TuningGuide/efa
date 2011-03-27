/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewLogbookDialog extends StepwiseDialog {

    private static final String LOGBOOKNAME        = "LOGBOOKNAME";
    private static final String LOGBOOKDESCRIPTION = "LOGBOOKDESCRIPTION";
    private static final String DATEFROM           = "DATEFROM";
    private static final String DATETO             = "DATETO";

    private String year = EfaUtil.getCurrentTimeStampYYYY();
    private String newLogbookName;

    public NewLogbookDialog(JDialog parent) {
        super(parent, International.getString("Neues Fahrtenbuch"));
    }

    public NewLogbookDialog(Frame parent) {
        super(parent, International.getString("Neues Fahrtenbuch"));
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Name und Beschreibung"),
            International.getString("Zeitraum für Fahrtenbuch")
        };
    }

    String getDescription(int step) {
        switch(step) {
            case 0:
                return International.getString("Ein Fahrtenbuch sollte üblicherweise alle Fahrten eines Jahr enthalten. "+
                        "Vereine mit mehreren Bootshäusern sollten pro Bootshaus ein eigenes Fahrtenbuch (in demselben Projekt) anlegen.");
            case 1:
                return International.getString("Bitte wähle den Zeitraum für Fahrten dieses Fahrtenbuches aus. efa wird später nur Fahrten "+
                        "innerhalb dieses Zeitraums für dieses Fahrtenbuch zulassen.");
        }
        return "";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        // Items for Step 0
        item = new ItemTypeString(LOGBOOKNAME, year, IItemType.TYPE_PUBLIC, "0", International.getString("Name des Fahrtenbuchs"));
        ((ItemTypeString)item).setAllowedCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_");
        ((ItemTypeString)item).setReplacementCharacter('_');
        ((ItemTypeString)item).setNotNull(true);
        items.add(item);
        item = new ItemTypeString(LOGBOOKDESCRIPTION, "", IItemType.TYPE_PUBLIC, "0", International.getString("Beschreibung"));
        items.add(item);

        // Items for Step 1
        item = new ItemTypeDate(DATEFROM, new DataTypeDate(1, 1, EfaUtil.string2int(year, 2010)), IItemType.TYPE_PUBLIC, "1", International.getString("Beginn des Zeitraums"));
        ((ItemTypeDate)item).setNotNull(true);
        items.add(item);
        item = new ItemTypeDate(DATETO, new DataTypeDate(31, 12, EfaUtil.string2int(year, 2010)), IItemType.TYPE_PUBLIC, "1", International.getString("Ende des Zeitraums"));
        ((ItemTypeDate)item).setNotNull(true);
        items.add(item);
    }

    boolean checkInput(int direction) {
        boolean ok = super.checkInput(direction);
        if (!ok) {
            return false;
        }

        if (step == 0) {
            ItemTypeString item = (ItemTypeString)getItemByName(LOGBOOKNAME);
            String name = item.getValue();
            if (Daten.project.getLoogbookRecord(name) != null) {
                    Dialog.error(LogString.logstring_fileAlreadyExists(name, International.getString("Fahrtenbuch")));
                    item.requestFocus();
                    return false;
            }
        }

        return true;
    }

    void finishButton_actionPerformed(ActionEvent e) {
        super.finishButton_actionPerformed(e);

        ItemTypeString logName = (ItemTypeString)getItemByName(LOGBOOKNAME);
        ItemTypeString logDescription = (ItemTypeString)getItemByName(LOGBOOKDESCRIPTION);
        ItemTypeDate logFromDate = (ItemTypeDate)getItemByName(DATEFROM);
        ItemTypeDate logFromTo = (ItemTypeDate)getItemByName(DATETO);

        ProjectRecord rec = Daten.project.createNewLogbookRecord(logName.getValue());
        rec.setDescription(logDescription.getValue());
        rec.setStartDate(logFromDate.getDate());
        rec.setEndDate(logFromTo.getDate());

        try {
            Daten.project.addLogbookRecord(rec);
            newLogbookName = logName.getValue();
            Daten.project.getLogbook(newLogbookName, true);
            Dialog.infoDialog(LogString.logstring_fileSuccessfullyCreated(logName.getValue(),
                    International.getString("Fahrtenbuch")));
            setDialogResult(true);
        } catch(EfaException ee) {
            newLogbookName = null;
            Dialog.error(ee.getMessage());
            ee.log();
            setDialogResult(false);
        }
    }

    public String getNewLogbookName() {
        return newLogbookName;
    }

    public String newLogbookDialog() {
        showDialog();
        return getNewLogbookName();
    }

}
