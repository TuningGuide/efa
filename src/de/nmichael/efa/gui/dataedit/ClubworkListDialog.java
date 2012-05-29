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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// @i18n complete
public class ClubworkListDialog extends DataListDialog {

	public ClubworkListDialog(Frame parent, long validAt, AdminRecord admin) {
		super(parent, International.getString("Vereinsarbeit"), Daten.project.getClubwork(Daten.project.getCurrentLogbook().getName(), false), validAt, admin);
	}

	public ClubworkListDialog(JDialog parent, long validAt, AdminRecord admin) {
		super(parent, International.getString("Vereinsarbeit"), Daten.project.getClubwork(Daten.project.getCurrentLogbook().getName(), false), validAt, admin);
	}

	public void keyAction(ActionEvent evt) {
		_keyAction(evt);
	}

	protected void iniActions() {
		actionText = new String[] {
				ItemTypeDataRecordTable.ACTIONTEXT_NEW,
				ItemTypeDataRecordTable.ACTIONTEXT_DELETE,
				International.getString("Liste ausgeben")
		};
		actionType = new int[] {
				ItemTypeDataRecordTable.ACTION_NEW,
				ItemTypeDataRecordTable.ACTION_DELETE,
				ACTION_PRINTLIST
		};
		actionImage = new String[] {
				BaseDialog.IMAGE_ADD,
				BaseDialog.IMAGE_DELETE,
				BaseDialog.IMAGE_LIST,
		};
	}

	public DataEditDialog createNewDataEditDialog(JDialog parent, StorageObject persistence, DataRecord record) {
		boolean newRecord = (record == null);
		if (record == null) {
			record = Daten.project.getClubwork(Daten.project.getCurrentLogbook().getName(), false).createClubworkRecord(UUID.randomUUID());
		}
		return new ClubworkEditDialog(parent, (ClubworkRecord)record, newRecord, admin);
	}
}
