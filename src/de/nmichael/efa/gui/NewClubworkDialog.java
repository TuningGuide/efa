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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JDialog;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeDate;
import de.nmichael.efa.core.items.ItemTypeHours;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.data.ProjectRecord;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeHours;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.LogString;

public class NewClubworkDialog extends StepwiseDialog {

    private static final String LOGBOOKNAME        = "LOGBOOKNAME";

    public static final String DEFAULTCLUBWORKTARGETHOURS   = "DefaultClubworkTargetHours";
    public static final String TRANSFERABLECLUBWORKHOURS    = "TransferableClubworkHours";
    public static final String FINEFORTOOLITTLECLUBWORK     = "FineForTooLittleClubwork";

    private String year = EfaUtil.getCurrentTimeStampYYYY();
    private String newLogbookName;

    public NewClubworkDialog(JDialog parent) {
        super(parent, International.getString("Neues Vereinsbuch"));
    }

    public NewClubworkDialog(Frame parent) {
        super(parent, International.getString("Neues Vereinsbuch"));
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Name und Informationen")
        };
    }

    String getDescription(int step) {
        switch(step) {
            case 0:
                return International.getString("Bitte gebe den Namen eines bereits erstellten Fahrtenbuchs an und fülle die für deinen Verein notwendigen Felder aus oder lasse Felder frei.");
       }
        return "";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        // Items for Step 0
        item = new ItemTypeString(LOGBOOKNAME, year, IItemType.TYPE_PUBLIC, "0", International.getString("Name des Vereinsbuchs"));
        ((ItemTypeString)item).setAllowedCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_");
        ((ItemTypeString)item).setReplacementCharacter('_');
        ((ItemTypeString)item).setNotNull(true);
        item.setFieldSize(150, -1);
        items.add(item);
        
        // Items for Step 0: Further information
        items.add(item = new ItemTypeHours(ProjectRecord.DEFAULTCLUBWORKTARGETHOURS, new DataTypeHours(0,0,0),
                IItemType.TYPE_PUBLIC, "0", International.getString("Standard Sollstunden für die Vereinsarbeit")));
        item.setFieldSize(150, -1);
        ((ItemTypeHours)item).enableSeconds(false);
        
        items.add(item = new ItemTypeHours(ProjectRecord.TRANSFERABLECLUBWORKHOURS, new DataTypeHours(0,0,0),
                IItemType.TYPE_PUBLIC, "0", International.getString("Übertragbare Vereinsarbeitsstunden")));
        item.setFieldSize(150, -1);
        ((ItemTypeHours)item).enableSeconds(false);
        
        items.add(item = new ItemTypeInteger(ProjectRecord.FINEFORTOOLITTLECLUBWORK, 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "0",
                International.getString("Bußgeld für Vereinsarbeit unter Sollstunden")));
        item.setFieldSize(150, -1);
    }

    boolean checkInput(int direction) {
        boolean ok = super.checkInput(direction);
        if (!ok) {
            return false;
        }

        if (step == 0) {
            ItemTypeString item = (ItemTypeString)getItemByName(LOGBOOKNAME);
            String name = item.getValue();
            
            if (Daten.project.getClubworkSettingsRecord(name) != null) {
                Dialog.error(LogString.fileAlreadyExists(name, International.getString("Vereinsbuch")));
                item.requestFocus();
                return false;
            }
            
            if (Daten.project.getLoogbookRecord(name) == null) {
            		Dialog.error(LogString.fileNotFound(name, International.getString("Fahrtenbuch")));
                    item.requestFocus();
                    return false;
            }
        }

        return true;
    }

    boolean finishButton_actionPerformed(ActionEvent e) {
        if (!super.finishButton_actionPerformed(e)) {
            return false;
        }

        ItemTypeString logName = (ItemTypeString)getItemByName(LOGBOOKNAME);        
        ItemTypeHours defaultClubworkTargetHours = (ItemTypeHours)getItemByName(DEFAULTCLUBWORKTARGETHOURS);
        ItemTypeHours transferableClubworkHours = (ItemTypeHours)getItemByName(TRANSFERABLECLUBWORKHOURS);
        ItemTypeInteger fineForTooLittleClubwork = (ItemTypeInteger)getItemByName(FINEFORTOOLITTLECLUBWORK);
        
        ProjectRecord rec2 = Daten.project.createNewClubworkRecord(logName.getValue());
        rec2.setDefaultClubworkTargetHours(defaultClubworkTargetHours.getValue());
        rec2.setTransferableClubworkHours(transferableClubworkHours.getValue());
        rec2.setFineForTooLittleClubwork(fineForTooLittleClubwork.getValue());

        try {
            Daten.project.addClubworkRecord(rec2);
            newLogbookName = logName.getValue();
            Daten.project.getClubwork(newLogbookName, true);
            Dialog.infoDialog(LogString.fileSuccessfullyCreated(logName.getValue(),
                    International.getString("Vereinsarbeit")));
            setDialogResult(true);
        } catch(EfaException ee) {
            newLogbookName = null;
            Dialog.error(ee.getMessage());
            ee.log();
            setDialogResult(false);
        }
        return true;
    }

    public String getUsedLogbookName() {
        return newLogbookName;
    }

    public String newClubworkDialog() {
        showDialog();
        return getUsedLogbookName();
    }

}
