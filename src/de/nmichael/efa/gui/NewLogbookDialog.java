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

public class NewLogbookDialog extends StepwiseDialog {

    private static final String LOGBOOKNAME        = "LOGBOOKNAME";
    private static final String LOGBOOKDESCRIPTION = "LOGBOOKDESCRIPTION";
    private static final String DATEFROM           = "DATEFROM";
    private static final String DATETO             = "DATETO";
    // further information not related to logbook, copied from ProjectRecord
    public static final String DEFAULTCLUBWORKTARGETHOURS   = "DefaultClubworkTargetHours";
    public static final String TRANSFERABLECLUBWORKHOURS    = "TransferableClubworkHours";
    public static final String FINEFORTOOLITTLECLUBWORK     = "FineForTooLittleClubwork";

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
            International.getString("Zeitraum für Fahrtenbuch"),
            International.getString("Informationen für weitere Bücher")
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
            case 2:
                return International.getString("Neben dem Fahrtenbuch wird auch ein Buch für Vereinsarbeit mit dem selben Zeitraum angelegt. Bitte fülle die für deinen Verein notwendigen Felder aus oder lasse die Felder frei.");
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
        
        // Items for Step 2: Further information not directly related to Logbook 
        items.add(item = new ItemTypeHours(ProjectRecord.DEFAULTCLUBWORKTARGETHOURS, new DataTypeHours(0,0,0),
                IItemType.TYPE_PUBLIC, "2", International.getString("Standard Sollstunden für die Vereinsarbeit")));
        item.setFieldSize(150, -1);
        ((ItemTypeHours)item).enableSeconds(false);
        
        items.add(item = new ItemTypeHours(ProjectRecord.TRANSFERABLECLUBWORKHOURS, new DataTypeHours(0,0,0),
                IItemType.TYPE_PUBLIC, "2", International.getString("Übertragbare Vereinsarbeitsstunden")));
        item.setFieldSize(150, -1);
        ((ItemTypeHours)item).enableSeconds(false);
        
        items.add(item = new ItemTypeInteger(ProjectRecord.FINEFORTOOLITTLECLUBWORK, 0, 0, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "2",
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
            if (Daten.project.getLoogbookRecord(name) != null) {
                    Dialog.error(LogString.fileAlreadyExists(name, International.getString("Fahrtenbuch")));
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
        ItemTypeString logDescription = (ItemTypeString)getItemByName(LOGBOOKDESCRIPTION);
        ItemTypeDate logFromDate = (ItemTypeDate)getItemByName(DATEFROM);
        ItemTypeDate logFromTo = (ItemTypeDate)getItemByName(DATETO);
        
        ItemTypeHours defaultClubworkTargetHours = (ItemTypeHours)getItemByName(DEFAULTCLUBWORKTARGETHOURS);
        ItemTypeHours transferableClubworkHours = (ItemTypeHours)getItemByName(TRANSFERABLECLUBWORKHOURS);
        ItemTypeInteger fineForTooLittleClubwork = (ItemTypeInteger)getItemByName(FINEFORTOOLITTLECLUBWORK);

        ProjectRecord rec = Daten.project.createNewLogbookRecord(logName.getValue());
        rec.setDescription(logDescription.getValue());
        rec.setStartDate(logFromDate.getDate());
        rec.setEndDate(logFromTo.getDate());
        
        rec.setDefaultClubworkTargetHours(defaultClubworkTargetHours.getValue());
        rec.setTransferableClubworkHours(transferableClubworkHours.getValue());
        rec.setFineForTooLittleClubwork(fineForTooLittleClubwork.getValue());
        
        try {
            Daten.project.addLogbookRecord(rec);
            newLogbookName = logName.getValue();
            Daten.project.getLogbook(newLogbookName, true);
            Daten.project.getClubwork(newLogbookName, true);
            Dialog.infoDialog(LogString.fileSuccessfullyCreated(logName.getValue(),
                    International.getString("Fahrtenbuch")));
            setDialogResult(true);
        } catch(EfaException ee) {
            newLogbookName = null;
            Dialog.error(ee.getMessage());
            ee.log();
            setDialogResult(false);
        }
        
        return true;
    }

    public String getNewLogbookName() {
        return newLogbookName;
    }

    public String newLogbookDialog() {
        showDialog();
        return getNewLogbookName();
    }

}
