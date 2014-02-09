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
import de.nmichael.efa.core.items.ItemTypeDataRecordTable;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class ClubworkListDialog extends DataListDialog {

	public static final int ACTION_CARRYOVER = 4;

	public ClubworkListDialog(Frame parent, AdminRecord admin) {
		super(parent, International.getString("Vereinsarbeit"), Daten.project.getCurrentClubwork(), 0, admin);
	}

	public ClubworkListDialog(JDialog parent, AdminRecord admin) {
		super(parent, International.getString("Vereinsarbeit"), Daten.project.getCurrentClubwork(), 0, admin);
	}

	public void keyAction(ActionEvent evt) {
		_keyAction(evt);
	}

	protected void iniActions() {
		if(admin == null) {
			actionText = new String[] {
					International.getString("Erfassen")
			};

			actionType = new int[] {
					ItemTypeDataRecordTable.ACTION_NEW
			};

			actionImage = new String[] {
					BaseDialog.IMAGE_ADD
			};
		}
		else {
			actionText = new String[] {
					ItemTypeDataRecordTable.ACTIONTEXT_NEW,
					ItemTypeDataRecordTable.ACTIONTEXT_EDIT,
					ItemTypeDataRecordTable.ACTIONTEXT_DELETE,
					International.getString("Liste ausgeben"),
					International.getString("Übertrag berechnen")
			};

			actionType = new int[] {
					ItemTypeDataRecordTable.ACTION_NEW,
					ItemTypeDataRecordTable.ACTION_EDIT,
					ItemTypeDataRecordTable.ACTION_DELETE,
					ACTION_PRINTLIST,
					ACTION_CARRYOVER
			};

			actionImage = new String[] {
					BaseDialog.IMAGE_ADD,
					BaseDialog.IMAGE_EDIT,
					BaseDialog.IMAGE_DELETE,
					BaseDialog.IMAGE_LIST,
					BaseDialog.IMAGE_MERGE
			};
		}
	}

	public DataEditDialog createNewDataEditDialog(JDialog parent, StorageObject persistence, DataRecord record) {
		boolean newRecord = (record == null);
		if (record == null) {
			record = Daten.project.getClubwork(Daten.project.getCurrentClubwork().getName(), false).createClubworkRecord(UUID.randomUUID());
		}
		return new ClubworkEditDialog(parent, (ClubworkRecord)record, newRecord, admin);
	}

	public void itemListenerActionTable(int actionId, DataRecord[] records) {
		if(actionId == ACTION_CARRYOVER) {
			Clubwork clubwork = Daten.project.getCurrentClubwork();
			clubwork.doCarryOver(this);
		}
		else {
			super.itemListenerActionTable(actionId, records);
		}
	}
}
