package de.nmichael.efa.core;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.Clubwork;
import de.nmichael.efa.data.types.DataTypeDate;

/**
 * Copyright 2013 All rights reserved by author
 * Author: velten
 * Date: 08.10.13
 * Time: 16:03
 */
public class AdminAudit {
	public AdminAudit() {
		runTasks();
	}

	private void runTasks() {
		checkForClubworkCarryOver();
	}

	private void checkForClubworkCarryOver() {
		/*if (Daten.project != null && Daten.project.isOpen()) {
			DataTypeDate date = Daten.project.getClubworkCarryOverDate();
			if (date != null && date.isSet() && DataTypeDate.today().isAfterOrEqual(date)) {
				Clubwork clubwork = Daten.project.getClubwork(Daten.project.getCurrentLogbook().getName(), false);
				clubwork.doCarryOver();
			}
		}*/
	}
}
