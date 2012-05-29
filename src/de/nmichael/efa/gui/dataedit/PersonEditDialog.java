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

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.UUID;

import javax.swing.JDialog;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.IItemListener;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.data.Clubwork;
import de.nmichael.efa.data.ClubworkRecord;
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.data.PersonRecord;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDecimal;
import de.nmichael.efa.data.types.DataTypeHours;
import de.nmichael.efa.ex.InvalidValueException;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

// @i18n complete
public class PersonEditDialog extends VersionizedDataEditDialog implements IItemListener {

    public PersonEditDialog(Frame parent, PersonRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Person"), r, newRecord, admin);
        ini4Permissions(admin);
        initListener();
    }

    public PersonEditDialog(JDialog parent, PersonRecord r, boolean newRecord, AdminRecord admin) {
        super(parent, International.getString("Person"), r, newRecord, admin);
        ini4Permissions(admin);
        initListener();
    }

    private void ini4Permissions(AdminRecord admin) {
        if (admin == null || !admin.isAllowedEditPersons()) {
            setShowVersionPanel(false);
            setPromptToEnterValidity(false);
            allowConflicts = false;
        }
    }


    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void initListener() {
        IItemType item;
        item = getItem(PersonRecord.FIRSTNAME);
        if (item != null) {
            item.registerItemListener(this);
        }
        item = getItem(PersonRecord.LASTNAME);
        if (item != null) {
            item.registerItemListener(this);
        }
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        super.itemListenerAction(itemType, event);
        if (itemType.getName().equals(PersonRecord.FIRSTNAME) ||
            itemType.getName().equals(PersonRecord.LASTNAME)) {
            if (newRecord && Daten.efaConfig.getValueAutogenAlias() &&
                event instanceof FocusEvent && event.getID() == FocusEvent.FOCUS_LOST) {
                ItemTypeString firstName = (ItemTypeString)getItem(PersonRecord.FIRSTNAME);
                ItemTypeString lastName = (ItemTypeString)getItem(PersonRecord.LASTNAME);
                ItemTypeString inputShortcut = (ItemTypeString)getItem(PersonRecord.INPUTSHORTCUT);
                if (firstName != null && lastName != null && inputShortcut != null) {
                    String sf = firstName.getValueFromField();
                    String sl = lastName.getValueFromField();
                    inputShortcut.parseAndShowValue(EfaUtil.getInputShortcut(sf, sl));
                }
            }
        }
    }
    
    protected boolean saveRecord() throws InvalidValueException {
    	boolean saved = super.saveRecord();
        if (newRecord) {
        	PersonRecord personRecord = (PersonRecord)dataRecord;
        	
            // save clubwork credit for getting into the club + yearly credit
        	DataTypeHours credit = personRecord.getYearlyClubworkCredit();

            DataTypeDate date = new DataTypeDate(personRecord.getValidFrom());
            int month = date.getDay() < 15 ? date.getMonth()-1 : date.getMonth();
            
            if(month > 0 || (credit != null && !credit.isEmpty())) {
    	    	Logbook logbook = Daten.project.getCurrentLogbook();
    	    	Clubwork clubwork = Daten.project.getClubwork(logbook.getName(), false);
    	    	
    	    	if(month > 0) {
	    	    	ClubworkRecord record = clubwork.createClubworkRecord(UUID.randomUUID());
	    	    	record.setPersonId(personRecord.getId());
	    	    	record.setWorkDate(date);
	    	    	record.setDescription(International.getString("Gutschrift für Vereinseintritt")+": #"+personRecord.getId());
	    	    	record.setHours(new DataTypeHours(0,0,month*clubwork.getProjectRecord().getDefaultClubworkTargetHours().getTimeAsSeconds()/12));
	    	    	
	    	    	try {
	    				clubwork.data().add(record);
	    	    	} catch (Exception eignore) {
	    	            Logger.logdebug(eignore);
	    	        }
    	    	}
    	    	if(credit != null && !credit.isEmpty()) {
	    	    	ClubworkRecord record = clubwork.createClubworkRecord(UUID.randomUUID());
	    	    	record.setPersonId(personRecord.getId());
	    	    	record.setWorkDate(date);
	    	    	record.setDescription(International.getString("Gutschrift")+": #"+personRecord.getId());
	    	    	record.setHours(credit);
	    	    	
	    	    	try {
	    				clubwork.data().add(record);
	    	    	} catch (Exception eignore) {
	    	            Logger.logdebug(eignore);
	    	        }
    	    	}
            }
        }
        return saved;
    }
}
