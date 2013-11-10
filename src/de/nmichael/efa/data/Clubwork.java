/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Velten Heyn
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.Daten;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.gui.NewClubworkBookDialog;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.ex.EfaModifyException;
import de.nmichael.efa.util.Dialog;

import javax.swing.*;
import java.awt.*;
import java.util.*;

// @i18n complete

public class Clubwork extends StorageObject {

	public static final String DATATYPE = "efa2clubwork";
	//    public ClubworkRecord staticClubworkRecord;
	private String name;
	private ProjectRecord projectRecord;

	public Clubwork(int storageType,
					String storageLocation,
					String storageUsername,
					String storagePassword,
					String storageObjectName) {
		super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE,
				International.getString("Vereinsarbeit") + " " + storageObjectName);
		ClubworkRecord.initialize();
//        staticClubworkRecord = (ClubworkRecord)createNewRecord();
		dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
	}

	public DataRecord createNewRecord() {
		return new ClubworkRecord(this, MetaData.getMetaData(DATATYPE));
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DataTypeDate getStartDate() {
		if(projectRecord != null) {
			return projectRecord.getStartDate();
		}
		else {
			ProjectRecord pr = Daten.project.getClubworkBookRecord(name);
			return (pr != null ? pr.getStartDate() : null);
		}
	}

	public DataTypeDate getEndDate() {
		if(projectRecord != null) {
			return projectRecord.getEndDate();
		}
		else {
			ProjectRecord pr = Daten.project.getClubworkBookRecord(name);
			return (pr != null ? pr.getEndDate() : null);
		}
	}

	public ProjectRecord getProjectRecord() {
		return this.projectRecord;
	}

	public void setProjectRecord(ProjectRecord r) {
		this.projectRecord = r;
	}

	public ClubworkRecord createClubworkRecord(UUID id) {
		ClubworkRecord r = new ClubworkRecord(this, MetaData.getMetaData(DATATYPE));
		r.setId(id);
		return r;
	}

	public ClubworkRecord getClubworkRecord(UUID id, long validAt) {
		try {
			return (ClubworkRecord)data().getValidAt(ClubworkRecord.getKey(id, validAt), validAt);
		} catch(Exception e) {
			Logger.logdebug(e);
			return null;
		}
	}

	public ClubworkRecord getClubworkRecord(UUID id, long earliestValidAt, long latestValidAt, long preferredValidAt) {
		try {
			return (ClubworkRecord)data().getValidNearest(ClubworkRecord.getKey(id, preferredValidAt), earliestValidAt, latestValidAt, preferredValidAt);
		} catch(Exception e) {
			Logger.logdebug(e);
			return null;
		}
	}

//    // find a record being valid at the specified time
//    public ClubworkRecord getClubworkRecord(String personName, long validAt) {
//        try {
//            DataKey[] keys = data().getByFields(
//                staticClubworkRecord.getQualifiedNameFields(), staticClubworkRecord.getQualifiedNameValues(personName), validAt);
//            if (keys == null || keys.length < 1) {
//                return null;
//            }
//            for (int i=0; i<keys.length; i++) {
//            	ClubworkRecord r = (ClubworkRecord)data().get(keys[i]);
//                if (r.isValidAt(validAt)) {
//                    return r;
//                }
//            }
//            return null;
//        } catch(Exception e) {
//            Logger.logdebug(e);
//            return null;
//        }
//    }
//
//    // find any record being valid at least partially in the specified range
//    public ClubworkRecord getClubworkRecord(String personName, long validFrom, long validUntil, long preferredValidAt) {
//        try {
//            DataKey[] keys = data().getByFields(
//                staticClubworkRecord.getQualifiedNameFields(), staticClubworkRecord.getQualifiedNameValues(personName));
//            if (keys == null || keys.length < 1) {
//                return null;
//            }
//            ClubworkRecord candidate = null;
//            for (int i=0; i<keys.length; i++) {
//                ClubworkRecord r = (ClubworkRecord)data().get(keys[i]);
//                if (r != null) {
//                    if (r.isInValidityRange(validFrom, validUntil)) {
//                        candidate = r;
//                        if (preferredValidAt >= r.getValidFrom() && preferredValidAt < r.getInvalidFrom()) {
//                            return r;
//                        }
//                    }
//                }
//            }
//            return candidate;
//        } catch(Exception e) {
//            Logger.logdebug(e);
//            return null;
//        }
//    }

	public Vector<ClubworkRecord> getAllClubworkRecords(long validAt, boolean alsoDeleted, boolean alsoInvisible) {
		try {
			Vector<ClubworkRecord> v = new Vector<ClubworkRecord>();
			DataKeyIterator it = data().getStaticIterator();
			DataKey k = it.getFirst();
			while (k != null) {
				ClubworkRecord r = (ClubworkRecord) data().get(k);
				if (r != null && (r.isValidAt(validAt) || (r.getDeleted() && alsoDeleted)) && (!r.getInvisible() || alsoInvisible)) {
					v.add(r);
				}
				k = it.getNext();
			}
			return v;
		} catch (Exception e) {
			Logger.logdebug(e);
			return null;
		}
	}

	public boolean isClubworkRecordDeleted(UUID id) {
		try {
			DataRecord[] records = data().getValidAny(ClubworkRecord.getKey(id, -1));
			if (records != null && records.length > 0) {
				return records[0].getDeleted();
			}
		} catch(Exception e) {
			Logger.logdebug(e);
		}
		return false;
	}

	public int getNumberOfClubworkRecords(long tstmp) {
		try {
			DataKeyIterator it = dataAccess.getStaticIterator();
			DataKey k = it.getFirst();
			// actually, checking for records valid at tstmp should already
			// give us unique records, so there should be no need to use
			// a Hashtable to make sure we don't cound a person twice. But, well,
			// you never know...
			Hashtable<UUID,DataKey> uuids = new Hashtable<UUID,DataKey>();
			while (k != null) {
				ClubworkRecord p = (ClubworkRecord) dataAccess.get(k);
				if (p != null && p.isValidAt(tstmp) && !p.getDeleted()) {
					uuids.put(p.getId(), k);
				}
				k = it.getNext();
			}
			return uuids.size();
		} catch (Exception e) {
			Logger.log(e);
			return -1;
		}
	}

	public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
		if (add || update) {
			assertFieldNotEmpty(record, ClubworkRecord.ID);
			assertFieldNotEmpty(record, ClubworkRecord.PERSONID);
			assertFieldNotEmpty(record, ClubworkRecord.WORKDATE);
			assertFieldNotEmpty(record, ClubworkRecord.DESCRIPTION);
			assertFieldNotEmpty(record, ClubworkRecord.HOURS);
		}
	}

	public void doCarryOver(JDialog parent) {
		String title = International.getString("Vereinsarbeit-Übertrag");
		String message = International.getString("Möchtest du den Übertrag für das letzte Vereinsarbeitsbuch erstellen und in das aktuell offene " +
				"übertragen? Oder möchtest du den Übertrag für das aktuelle Buch erstellen und in das kommende Buch übertragen?");
		String inDieses = International.getString("Letztes in Dieses");
		String inNächstes = International.getString("Dieses in Kommendes");
		int num = Dialog.auswahlDialog(title, message, inDieses, inNächstes);

		doCarryOver(num, parent);
	}

	public void doCarryOver(int thisOrNext, JDialog parent) {
		if (Daten.project == null || !Daten.project.isOpen()) {
			Dialog.error(International.getString("Kein Projekt geöffnet."));
			return;
		}
		Clubwork current = Daten.project.getCurrentClubwork();
		if (current == null || !current.isOpen()) {
			Dialog.error(International.getString("Kein Vereinsarbeitsbuch geöffnet."));
			return;
		}

		String[] names = Daten.project.getAllClubworkNames();
		DataTypeDate date = (thisOrNext == 0) ? new DataTypeDate(0) : new DataTypeDate(1,1,3000);
		Clubwork sourceOrTarget = null;
		for (int i=0; i < names.length; i++) {
			if(names[i] == null) continue;

			Clubwork clubwork = Daten.project.getClubwork(names[i], false);
			if(thisOrNext == 0) {
				DataTypeDate start = clubwork.getStartDate();
				if(start.isBefore(current.getStartDate())) {
					if(start.isAfter(date)) {
						date = start;
						sourceOrTarget = clubwork;
					}
				}
			}
			else {
				DataTypeDate end = clubwork.getEndDate();
				if(end.isAfter(current.getEndDate())) {
					if(end.isBefore(date)) {
						date = end;
						sourceOrTarget = clubwork;
					}
				}
			}
		}

		if(thisOrNext == 0) {
			if(sourceOrTarget == null) {
				Dialog.error(International.getString("Kein vorheriges Vereinsarbeitsbuch gefunden."));
				return;
			}
			doCarryOver(sourceOrTarget, current);
		}
		else {
			if(sourceOrTarget == null) {
				int res = Dialog.yesNoDialog("Hinweis", International.getString("Kein kommendes Vereinsarbeitsbuch gefunden. Soll eines erstellt " +
						"werden?"));
				if(res == Dialog.YES) {
					NewClubworkBookDialog dlg = new NewClubworkBookDialog(parent);
					String clubworkName = dlg.newClubworkBookDialog();
					sourceOrTarget = Daten.project.getClubwork(clubworkName, false);
				}
				else {
					return;
				}
			}
			doCarryOver(current, sourceOrTarget);
		}
	}

	public void doCarryOver(Clubwork from, Clubwork to) {
		ProjectRecord pr = Daten.project.getClubworkBookRecord(from.getName());
		if (pr == null) {
			Dialog.error(International.getMessage("Kein Vereinsarbeitsbuch '{name}' gefunden.", from.getName()));
			return;
		}

		Hashtable<UUID, Double> hourAggregation = new Hashtable<UUID, Double>();
		try {
			DataKeyIterator it = from.data().getStaticIterator();
			for(DataKey k = it.getFirst(); k != null; k = it.getNext()) {
				ClubworkRecord r = (ClubworkRecord) from.data().get(k);
				UUID personId = r.getPersonId();

				if (personId == null) {
					continue;
				}
				Double hours = hourAggregation.get(k);
				if (hours == null) {
					hours = new Double(0);
				}
				// aggregate
				hours += r.getHours();

				hourAggregation.put(personId, hours);
			}
		} catch (EfaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double sDefaultClubworkTargetHours = pr.getDefaultClubworkTargetHours();
		double sTransferableClubworkHours = pr.getTransferableClubworkHours();

		double max = sDefaultClubworkTargetHours+sTransferableClubworkHours;
		double min = sDefaultClubworkTargetHours-sTransferableClubworkHours;
		long lock = -1;

		try {
			lock = to.data().acquireGlobalLock();

			// Save Carry Over
			Set<UUID> set = hourAggregation.keySet();
			for(UUID person : set) {

				ClubworkRecord record = to.createClubworkRecord(UUID.randomUUID());
				record.setPersonId(person);
				record.setWorkDate(DataTypeDate.today());
				record.setDescription(International.getString("Übertrag")+" ("+DataTypeDate.today()+")");
				record.setFlag(ClubworkRecord.Flags.CarryOver);


				Double hours = hourAggregation.get(person);
				if(hours == null) {
					hours = 0.0;
				}
				else if(hours > max) {
					record.setHours(sTransferableClubworkHours);
				}
				else if(hours < min) {
					record.setHours(-sTransferableClubworkHours);
				}
				else {
					record.setHours(hours-sDefaultClubworkTargetHours);
				}

				try {
					to.data().add(record);
				} catch (Exception eignore) {
					Logger.logdebug(eignore);
				}
			}

			// Save Yearly Credit
			/*Vector<PersonRecord> persons = Daten.project.getPersons(false).getAllPersons(-1, false, false);

			Iterator<PersonRecord> personItr = persons.iterator();
			while (personItr.hasNext()) {
				PersonRecord person = personItr.next();

				ClubworkRecord record = clubwork.createClubworkRecord(UUID.randomUUID());
				record.setPersonId(person.getId());
				record.setWorkDate(DataTypeDate.today());
				record.setDescription(International.getString("Gutschrift (jährlich)"));
				record.setHours(person.getYearlyClubworkCredit());

				try {
					clubwork.data().add(record);
				} catch (Exception eignore) {
					Logger.logdebug(eignore);
				}
			}*/
		}
		catch (Exception e) {
			Logger.logdebug(e);
			Logger.log(Logger.ERROR, Logger.MSG_ERR_GENERIC,
					LogString.operationAborted(International.getString("Vereinsarbeit-Übertrag")));
			Messages messages = Daten.project.getMessages(false);
			messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_ADMIN,
					International.getString("Vereinsarbeit-Übertrag"),
					International.getString("efa hat soeben versucht den Übertrag für die Vereinsarbeit zu berechnen.") + "\n"
							+ International.getString("Bei diesem Vorgang traten jedoch FEHLER auf.") + "\n\n"
							+ International.getString("Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden."));
		}
		finally {
			if (to != null && lock >= 0) {
				to.data().releaseGlobalLock(lock);
			}
		}
	}
}
