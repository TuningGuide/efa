/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.gui.util;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.ItemTypeHours;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.statistics.StatisticsData;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.*;

import java.awt.*;
import java.io.*;
import java.util.*;

public class EfaBoathouseBackgroundTask extends Thread {

	private static final int CHECK_INTERVAL = 60;
	private static final int ONCE_AN_HOUR = 60;
	EfaBoathouseFrame efaBoathouseFrame;
	int onceAnHour;
	Date date;
	Calendar cal;
	Calendar lockEfa;
	boolean framePacked;
	long lastEfaConfigScn = -1;

	public EfaBoathouseBackgroundTask(EfaBoathouseFrame efaBoathouseFrame) {
		this.efaBoathouseFrame = efaBoathouseFrame;
		this.onceAnHour = 5; // initial nach 5 Schleifendurchläufen zum ersten Mal hier reingehen
		this.cal = new GregorianCalendar();
		this.lockEfa = null;
		this.date = new Date();
		this.framePacked = false;
	}

	public void setEfaLockBegin(DataTypeDate datum, DataTypeTime zeit) {
		if (Daten.efaConfig.getValueEfaDirekt_locked()) {
			lockEfa = null; // don't lock twice
			return;
		}
		if (datum == null || !datum.isSet()) {
			lockEfa = null;
		} else {
			if (zeit != null && zeit.isSet()) {
				lockEfa = new GregorianCalendar(datum.getYear(), datum.getMonth() - 1, datum.getDay(), zeit.getHour(), zeit.getMinute());
			} else {
				lockEfa = new GregorianCalendar(datum.getYear(), datum.getMonth() - 1, datum.getDay());
			}
		}
	}

	private void mailWarnings() {
		try {
			BufferedReader f = new BufferedReader(new FileReader(Daten.efaLogfile));
			String s;
			Vector warnings = new Vector();
			while ((s = f.readLine()) != null) {
				if (Logger.isWarningLine(s) && Logger.getLineTimestamp(s) > Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime()) {
					warnings.add(s);
				}
			}
			f.close();
			if (warnings.size() == 0) {
				Logger.log(Logger.INFO, Logger.MSG_EVT_CHECKFORWARNINGS,
						International.getMessage("Seit {date} sind keinerlei Warnungen in efa verzeichnet worden.", 
								EfaUtil.getTimeStamp(Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime())));
			} else {
				Logger.log(Logger.INFO, Logger.MSG_EVT_CHECKFORWARNINGS,
						International.getMessage("Seit {date} sind {n} Warnungen in efa verzeichnet worden.",
								EfaUtil.getTimeStamp(Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime()), warnings.size()));
				String txt = International.getMessage("Folgende Warnungen sind seit {date} in efa verzeichnet worden:",
						EfaUtil.getTimeStamp(Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime())) + "\n"
						+ International.getMessage("{n} Warnungen", warnings.size()) + "\n\n";
				for (int i = 0; i < warnings.size(); i++) {
					txt += ((String) warnings.get(i)) + "\n";
				}
				if (Daten.project != null && Daten.efaConfig != null) {
					Messages messages = Daten.project.getMessages(false);
					if (messages != null && Daten.efaConfig.getValueEfaDirekt_bnrWarning_admin()) {
						messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_ADMIN, International.getString("Warnungen"), txt);
					}
					if (messages != null && Daten.efaConfig.getValueEfaDirekt_bnrWarning_bootswart()) {
						messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_BOATMAINTENANCE, International.getString("Warnungen"), txt);
					}
				}
			}
			if (Daten.efaConfig != null) {
				Daten.efaConfig.setValueEfaDirekt_bnrWarning_lasttime(System.currentTimeMillis());
				//@efaconfig Daten.efaConfig.writeFile();
			}

		} catch (Exception e) {
			Logger.log(Logger.ERROR, Logger.MSG_ERR_CHECKFORWARNINGS,
					"Checking Logfile for Warnings and mailing them to Admin failed: " + e.toString());
		}
	}

	public void run() {
		// Diese Schleife läuft i.d.R. einmal pro Minute
		while (true) {
			try {
				if (Logger.isTraceOn(Logger.TT_BACKGROUND, 5)) {
					Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFABACKGROUNDTASK, "EfaDirektBackgroundTask: alive!");
				}

				// Update GUI on Config Changes
				checkUpdateGui();

				// Reservierungs-Checker
				checkBoatStatus();

				// Nach ungelesenen Nachrichten für den Admin suchen
				checkForUnreadMessages();

				// automatisches Beenden von efa
				checkForExitOrRestart();

				// efa zeitgesteuert sperren
				checkForLockEfa();

				// automatisches Beginnen eines neuen Fahrtenbuchs (z.B. zum Jahreswechsel)
				checkForAutoCreateNewLogbook();

				// immer im Vordergrund
				checkAlwaysInFront();

				// Fokus-Kontrolle
				checkFocus();

				// Speicher-Überwachung
				checkMemory();

				// Aktivitäten einmal pro Stunde
				if (--onceAnHour <= 0) {
					System.gc(); // Damit Speicherüberwachung funktioniert (anderenfalls wird CollectionUsage nicht aktualisiert; Java-Bug)
					onceAnHour = ONCE_AN_HOUR;
					if (Logger.isTraceOn(Logger.TT_BACKGROUND)) {
						Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFABACKGROUNDTASK, "EfaDirektBackgroundTask: alive!");
					}

					checkWarnings();
				}

				try {
					Thread.sleep(CHECK_INTERVAL * 1000);
				} catch (Exception e) {
					// wenn unterbrochen, dann versuch nochmal, kurz zu schlafen, und arbeite dann weiter!! ;-)
					try {
						Thread.sleep(100);
					} catch (Exception ee) {
						EfaUtil.foo();
					}
				}
			} catch (Exception eglobal) {
				Logger.log(eglobal);
			}
		} // end: while(true)
	} // end: run

	private void checkUpdateGui() {
		if (Daten.efaConfig != null) {
			try {
				long scn = Daten.efaConfig.data().getSCN();
				if (scn != lastEfaConfigScn) {
					efaBoathouseFrame.updateGuiElements();
					lastEfaConfigScn = scn;
				}
			} catch(Exception e) {
				Logger.logdebug(e);
			}
		}
	}

	private void checkBoatStatus() {
		BoatStatus boatStatus = (Daten.project != null ? Daten.project.getBoatStatus(false) : null);
		BoatReservations boatReservations = (Daten.project != null ? Daten.project.getBoatReservations(false) : null);
		BoatDamages boatDamages  = (Daten.project != null ? Daten.project.getBoatDamages(false) : null);
		if (boatStatus == null || boatReservations == null || boatDamages == null) {
			return;
		}

		long now = System.currentTimeMillis();
		boolean listChanged = false;
		try {
			DataKeyIterator it = boatStatus.data().getStaticIterator();
			for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
				BoatStatusRecord boatStatusRecord = (BoatStatusRecord) boatStatus.data().get(k);
				if (boatStatusRecord == null) {
					continue;
				}
				try {
					String oldCurrentStatus = boatStatusRecord.getCurrentStatus();
					String oldShowInList = boatStatusRecord.getShowInList();
					String oldComment = boatStatusRecord.getComment();

					// set CurrentStatus correctly
					if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER) &&
							!boatStatusRecord.getCurrentStatus().equals(boatStatusRecord.getBaseStatus())) {
						boatStatusRecord.setCurrentStatus(boatStatusRecord.getBaseStatus());
					}

					if (boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_HIDE)) {
						if (boatStatusRecord.getShowInList() != null && !boatStatusRecord.getShowInList().equals(BoatStatusRecord.STATUS_HIDE)) {
							boatStatusRecord.setShowInList(null);
							boatStatus.data().update(boatStatusRecord);
							listChanged = true;
						}
						continue;
					}
					if (boatStatusRecord.getUnknownBoat()) {
						if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
							boatStatus.data().delete(boatStatusRecord.getKey());
							listChanged = true;
						}
						continue;
					}

					// delete any obsolete revervations
					int purgedRes = boatReservations.purgeObsoleteReservations(boatStatusRecord.getBoatId(), now);

					// find all currently valid reservations
					BoatReservationRecord[] reservations = boatReservations.getBoatReservations(boatStatusRecord.getBoatId(), now, 0);
					if (reservations == null || reservations.length == 0) {
						// no reservations at the moment - nothing to do
						if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER) &&
								!boatStatusRecord.getShowInList().equals(boatStatusRecord.getCurrentStatus())) {
							boatStatusRecord.setShowInList(null);
						}

						if (purgedRes > 0) {
							boatStatusRecord.setComment(null);
						}
					} else {
						// reservations found
						if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
							if (Daten.efaConfig.getValueEfaDirekt_resBooteNichtVerfuegbar()) {
								if (!boatStatusRecord.getShowInList().equals(BoatStatusRecord.STATUS_NOTAVAILABLE)) {
									boatStatusRecord.setShowInList(BoatStatusRecord.STATUS_NOTAVAILABLE);
								}
							} else {
								if (!boatStatusRecord.getShowInList().equals(boatStatusRecord.getBaseStatus())) {
									boatStatusRecord.setShowInList(boatStatusRecord.getBaseStatus());
								}
							}
						}
						String s = International.getMessage("reserviert für {name} ({reason}) {from_to}",
								reservations[0].getPersonAsName(),
								reservations[0].getReason(),
								reservations[0].getReservationTimeDescription());
						boatStatusRecord.setComment(s);
					}

					// find all current damages
					BoatDamageRecord[] damages = boatDamages.getBoatDamages(boatStatusRecord.getBoatId());
					for (int i=0; damages != null && i<damages.length; i++) {
						if (!damages[i].getFixed() && damages[i].getSeverity() != null &&
								damages[i].getSeverity().equals(BoatDamageRecord.SEVERITY_NOTUSEABLE)) {
							boatStatusRecord.setComment(damages[i].getShortDamageInfo());
							if (!boatStatusRecord.getShowInList().equals(BoatStatusRecord.STATUS_NOTAVAILABLE)) {
								boatStatusRecord.setShowInList(BoatStatusRecord.STATUS_NOTAVAILABLE);
							}
							break; // stop after first severe damage
						}
					}

					// make sure that if the boat is on the water, this status overrides any other list settings
					if (boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
						boatStatusRecord.setShowInList(BoatStatusRecord.STATUS_ONTHEWATER);
					}

					boolean statusRecordChanged = false;
					if (oldCurrentStatus == null ||
							!oldCurrentStatus.equals(boatStatusRecord.getCurrentStatus())) {
						statusRecordChanged = true;
					}
					if (oldShowInList == null ||
							!oldShowInList.equals(boatStatusRecord.getShowInList())) {
						statusRecordChanged = true;
					}
					if ((oldComment == null && boatStatusRecord.getComment() != null && boatStatusRecord.getComment().length() > 0) ||
							(oldComment != null && !oldComment.equals(boatStatusRecord.getComment()))) {
						statusRecordChanged = true;
					}

					if (statusRecordChanged) {
						boatStatus.data().update(boatStatusRecord);
						listChanged = true;
					}
					if (statusRecordChanged && Logger.isTraceOn(Logger.TT_BACKGROUND, 2)) {
						Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFABACKGROUNDTASK,
								"BoatStatus changed for Boat " + boatStatusRecord.getBoatNameAsString(now) +
								", new Status: " + boatStatusRecord.toString());
					}
				} catch (Exception ee) {
					Logger.logdebug(ee);
				}
			}
			efaBoathouseFrame.updateBoatLists(listChanged);
		} catch (Exception e) {
			Logger.logdebug(e);
		}
	}

	private void checkForUnreadMessages() {
		boolean admin = false;
		boolean boatmaintenance = false;

		Messages messages = (Daten.project != null ? Daten.project.getMessages(false) : null);

		if (messages != null) {
			// durchsuche die letzten 50 Nachrichten nach ungelesenen (aus Performancegründen immer nur die letzen 50)
			int i=0;
			try {
				DataKeyIterator it = messages.data().getStaticIterator();
				DataKey k = it.getLast();
				while (k != null) {
					MessageRecord msg = (MessageRecord)messages.data().get(k);
					if (msg != null && !msg.getRead()) {
						if (msg.getTo().equals(MessageRecord.TO_ADMIN)) {
							admin = true;
						}
						if (msg.getTo().equals(MessageRecord.TO_BOATMAINTENANCE)) {
							boatmaintenance = true;
						}
					}
					if (++i ==50 || (admin && boatmaintenance)) {
						break;
					}
					k = it.getPrev();
				}
			} catch(Exception e) {
				Logger.logdebug(e);
			}
		}
		efaBoathouseFrame.setUnreadMessages(admin, boatmaintenance);
	}

	private void checkForExitOrRestart() {
		// automatisches, zeitgesteuertes Beenden von efa ?
		if (Daten.efaConfig.getValueEfaDirekt_exitTime().isSet()
				&& System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME + 1) * 60 * 1000) {
			date.setTime(System.currentTimeMillis());
			cal.setTime(date);
			int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
			int exitTime = Daten.efaConfig.getValueEfaDirekt_exitTime().getHour() * 60 + Daten.efaConfig.getValueEfaDirekt_exitTime().getMinute();
			if ((now >= exitTime && now < exitTime + Daten.AUTO_EXIT_MIN_RUNTIME) || (now + (24 * 60) >= exitTime && now + (24 * 60) < exitTime + Daten.AUTO_EXIT_MIN_RUNTIME)) {
				Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDEXIT,
						International.getString("Eingestellte Uhrzeit zum Beenden von efa erreicht!"));
				if (System.currentTimeMillis() - efaBoathouseFrame.getLastUserInteraction() < Daten.AUTO_EXIT_MIN_LAST_USED * 60 * 1000) {
					Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDEXITDELAY,
							International.getMessage("Beenden von efa wird verzögert, da efa innerhalb der letzten {n} Minuten noch benutzt wurde ...",
									Daten.AUTO_EXIT_MIN_LAST_USED));
				} else {
					EfaExitFrame.exitEfa(International.getString("Zeitgesteuertes Beenden von efa"), false, EfaBoathouseFrame.EFA_EXIT_REASON_TIME);
				}
			}
		}

		// automatisches Beenden nach Inaktivität ?
		if (Daten.efaConfig.getValueEfaDirekt_exitIdleTime() > 0
				&& System.currentTimeMillis() - efaBoathouseFrame.getLastUserInteraction() > Daten.efaConfig.getValueEfaDirekt_exitIdleTime() * 60 * 1000) {
			Logger.log(Logger.INFO, Logger.MSG_EVT_INACTIVITYBASEDEXIT,
					International.getString("Eingestellte Inaktivitätsdauer zum Beenden von efa erreicht!"));
			EfaExitFrame.exitEfa(International.getString("Zeitgesteuertes Beenden von efa"), false, EfaBoathouseFrame.EFA_EXIT_REASON_TIME);
		}

		// automatischer, zeitgesteuerter Neustart von efa ?
		if (Daten.efaConfig.getValueEfaDirekt_restartTime().isSet()
				&& System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME + 1) * 60 * 1000) {
			date.setTime(System.currentTimeMillis());
			cal.setTime(date);
			int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
			int restartTime = Daten.efaConfig.getValueEfaDirekt_restartTime().getHour() * 60 + Daten.efaConfig.getValueEfaDirekt_restartTime().getMinute();
			if ((now >= restartTime && now < restartTime + Daten.AUTO_EXIT_MIN_RUNTIME) || (now + (24 * 60) >= restartTime && now + (24 * 60) < restartTime + Daten.AUTO_EXIT_MIN_RUNTIME)) {
				Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDRESTART, "Automatischer Neustart von efa (einmal täglich).");
				if (System.currentTimeMillis() - efaBoathouseFrame.getLastUserInteraction() < Daten.AUTO_EXIT_MIN_LAST_USED * 60 * 1000) {
					Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDRESTARTDELAY, "Neustart von efa wird verzögert, da efa innerhalb der letzten " + Daten.AUTO_EXIT_MIN_LAST_USED + " Minuten noch benutzt wurde ...");
				} else {
					EfaExitFrame.exitEfa("Automatischer Neustart von efa", true, EfaBoathouseFrame.EFA_EXIT_REASON_AUTORESTART);
				}
			}
		}
	}

	private void checkForLockEfa() {
		if (Daten.efaConfig != null) {
			if (Daten.efaConfig.getValueEfaDirekt_locked()) {
				efaBoathouseFrame.lockEfa();
				return;
			}
			setEfaLockBegin(Daten.efaConfig.getValueEfaDirekt_lockEfaFromDatum(), 
					Daten.efaConfig.getValueEfaDirekt_lockEfaFromZeit());
		}

		if (lockEfa != null) {
			date.setTime(System.currentTimeMillis());
			cal.setTime(date);
			if (cal.after(lockEfa) && efaBoathouseFrame != null) {
				efaBoathouseFrame.lockEfa();
				lockEfa = null;
			}
		}
	}

	private void checkForAutoCreateNewLogbook() {
		if (Daten.project != null && Daten.project.isOpen()) {
			DataTypeDate date = Daten.project.getAutoNewLogbookDate();
			if (date != null && date.isSet() && DataTypeDate.today().isAfterOrEqual(date)) {
				autoOpenNewLogbook();
			}
		}
	}

	private void checkAlwaysInFront() {
		if (Daten.efaConfig.getValueEfaDirekt_immerImVordergrund() && this.efaBoathouseFrame != null
				&& Dialog.frameCurrent() == this.efaBoathouseFrame) {
			Window[] windows = this.efaBoathouseFrame.getOwnedWindows();
			boolean topWindow = true;
			if (windows != null) {
				for (int i = 0; i < windows.length; i++) {
					if (windows[i] != null && windows[i].isVisible()) {
						topWindow = false;
					}
				}
			}
			if (topWindow && Daten.efaConfig.getValueEfaDirekt_immerImVordergrundBringToFront()) {
				efaBoathouseFrame.bringFrameToFront();
			}
		}

	}

	private void checkFocus() {
		if (this.efaBoathouseFrame != null && this.efaBoathouseFrame.getFocusOwner() == this.efaBoathouseFrame) {
			// das Frame selbst hat den Fokus: Das soll nicht sein! Gib einer Liste den Fokus!
			efaBoathouseFrame.boatListRequestFocus(0);
		}
	}

	private void checkMemory() {
		try {
			// System.gc(); // !!! ONLY ENABLE FOR DEBUGGING PURPOSES !!!
			if (de.nmichael.efa.java15.Java15.isMemoryLow(Daten.MIN_FREEMEM_PERCENTAGE, Daten.WARN_FREEMEM_PERCENTAGE)) {
				efaBoathouseFrame.exitOnLowMemory("EfaBoathouseBackgroundTask: MemoryLow", false);
			}
		} catch (UnsupportedClassVersionError e) {
			EfaUtil.foo();
		} catch (NoClassDefFoundError e) {
			EfaUtil.foo();
		}
	}

	private void checkWarnings() {
		// WARNINGs aus Logfile an Admins verschicken
		if (System.currentTimeMillis() >= Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime() + 7l * 24l * 60l * 60l * 1000l
				&& (Daten.efaConfig.getValueEfaDirekt_bnrWarning_admin() || Daten.efaConfig.getValueEfaDirekt_bnrWarning_bootswart()) && Daten.efaLogfile != null) {
			mailWarnings();
		}
	}

	private void autoOpenNewLogbook() {
		if (Daten.project == null || !Daten.project.isOpen()) {
			return;
		}
		String newLogbookName = Daten.project.getAutoNewLogbookName();
		if (newLogbookName == null || newLogbookName.length() == 0) {
			return;
		}
		Daten.project.setAutoNewLogbookDate(null);
		Daten.project.setAutoNewLogbookName(null);
		Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLOGBOOK,
				International.getString("Fahrtenbuchwechsel wird begonnen ..."));

		Logbook currentLogbook = efaBoathouseFrame.getLogbook();
		BoatStatus boatStatus = Daten.project.getBoatStatus(false);
		long lockLogbook = -1;
		long lockStatus = -1;
		try {
			Logbook newLogbook = Daten.project.getLogbook(newLogbookName, false);

			// Step 1: Try to find and open new Logbook
			if (newLogbook == null) {
				Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTOSTARTNEWLOGBOOK,
						LogString.fileNotFound(newLogbookName, International.getString("Fahrtenbuch")));
				throw new Exception("New Logbook not found");
			}
			if (currentLogbook != null && newLogbook.getName().equals(currentLogbook.getName())) {
				Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTOSTARTNEWLOGBOOK,
						International.getMessage("Fahrtenbuch {name} ist bereits geöffnet.", newLogbook.getName()));
				throw new Exception("New Logbook already opened");
			}

			// Step 2: Abort open Sessions
			boolean sessionsAborted = false;
			if (currentLogbook != null) {
				Vector<BoatStatusRecord> boatsOnTheWater = boatStatus.getBoats(BoatStatusRecord.STATUS_ONTHEWATER);
				if (boatsOnTheWater.size() > 0) {
					lockStatus = boatStatus.data().acquireGlobalLock();
					lockLogbook = currentLogbook.data().acquireGlobalLock();
					sessionsAborted = true;
					Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLBSTEP,
							International.getString("Offene Fahrten werden abgebrochen ..."));
					for (int i = 0; i < boatsOnTheWater.size(); i++) {
						BoatStatusRecord sr = boatsOnTheWater.get(i);
						LogbookRecord r = null;
						if (sr.getEntryNo() != null && sr.getEntryNo().isSet()) {
							r = currentLogbook.getLogbookRecord(sr.getEntryNo());
							r.setSessionIsOpen(false);
							currentLogbook.data().update(r, lockLogbook);
						}
						sr.setEntryNo(null);
						sr.setCurrentStatus(sr.getBaseStatus());
						boatStatus.data().update(sr, lockStatus);
						EfaBaseFrame.logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_TRIPABORT,
								International.getString("Fahrtabbruch"), r);
						boatStatus.data().releaseGlobalLock(lockStatus);
						lockStatus = -1;
						currentLogbook.data().releaseGlobalLock(lockLogbook);
						lockLogbook = -1;
					}
				}
			}

			// Step 3: Activate the new Logbook
			if (efaBoathouseFrame.openLogbook(newLogbook.getName())) {
				Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLBDONE,
						LogString.operationSuccessfullyCompleted(International.getString("Fahrtenbuchwechsel")));
			} else {
				throw new Exception("Failed to open new Logbook");
			}
			
			Messages messages = Daten.project.getMessages(false);
			messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_ADMIN,
					International.getString("Fahrtenbuchwechsel"),
					International.getString("efa hat soeben wie konfiguriert ein neues Fahrtenbuch geöffnet.") + "\n"
							+ International.getMessage("Das neue Fahrtenbuch heißt {name} und ist gültig vom {fromdate} bis {todate}.",
									newLogbook.getName(), newLogbook.getStartDate().toString(), newLogbook.getEndDate().toString())+"\n"
									+ LogString.operationSuccessfullyCompleted(International.getString("Fahrtenbuchwechsel")) + "\n\n"
									+ (sessionsAborted ? International.getString("Zum Zeitpunkt des Fahrtenbuchwechsels befanden sich noch einige Boote "
											+ "auf dem Wasser. Diese Fahrten wurden ABGEBROCHEN. Die abgebrochenen "
											+ "Fahrten sind in der Logdatei verzeichnet.") : ""));
			EfaUtil.sleep(500);
			efaBoathouseFrame.updateBoatLists(true);
			EfaUtil.sleep(500);
			interrupt();
		} catch (Exception e) {
			Logger.logdebug(e);
			Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTOSTARTNEWLOGBOOK,
					LogString.operationAborted(International.getString("Fahrtenbuchwechsel")));
			Messages messages = Daten.project.getMessages(false);
			messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_ADMIN,
					International.getString("Fahrtenbuchwechsel"),
					International.getString("efa hat soeben versucht, wie konfiguriert ein neues Fahrtenbuch anzulegen.") + "\n"
							+ International.getString("Bei diesem Vorgang traten jedoch FEHLER auf.") + "\n\n"
							+ International.getString("Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden."));
		} finally {
			if (boatStatus != null && lockStatus >= 0) {
				boatStatus.data().releaseGlobalLock(lockStatus);
			}
			if (currentLogbook != null && lockLogbook >= 0) {
				currentLogbook.data().releaseGlobalLock(lockLogbook);
			}
		}

	}

}
