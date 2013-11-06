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
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.ex.EfaModifyException;
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
    	ProjectRecord logbook = Daten.project.getLoogbookRecord(name);
        return (logbook != null ? logbook.getStartDate() : null);
    }

    public DataTypeDate getEndDate() {
    	ProjectRecord logbook = Daten.project.getLoogbookRecord(name);
        return (logbook != null ? logbook.getEndDate() : null);
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

	public void doCarryOver() {
		if (Daten.project == null || !Daten.project.isOpen()) {
			return;
		}
		Hashtable<UUID, Double> data = new Hashtable<UUID, Double>();

		String[] names = Daten.project.getAllLogbookNames();
		for (int i=0; i < names.length; i++) {
			if(names[i] == null) continue;

			Clubwork clubwork = Daten.project.getClubwork(names[i], false);
			DataTypeDate lbStart = clubwork.getStartDate();
			DataTypeDate lbEnd = clubwork.getEndDate();
			if (lbStart == null || lbEnd == null) {
				continue; // should never happen
			}

			int lastYear = DataTypeDate.today().getYear()-1;

			if(clubwork != null && (lbStart.getYear() == lastYear || lbEnd.getYear() == lastYear)) {
				try {
					DataKeyIterator it = clubwork.data().getStaticIterator();
					int size = it.size();
					DataKey k = it.getFirst();
					int pos = 0;
					while (k != null) {
						ClubworkRecord r = (ClubworkRecord) clubwork.data().get(k);

						DataTypeDate date = r.getWorkDate();
						if(date.getYear() == lastYear) {
							UUID key = r.getPersonId();

							if (key == null) {
								return;
							}
							Double hours = data.get(key);
							if (hours == null) {
								hours = new Double(0);
							}

							// aggregate
							hours += r.getHours();

							data.put(key, hours);
						}
						k = it.getNext();
					}
				} catch (EfaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Clubwork currentCW = Daten.project.getCurrentClubwork();

		if(currentCW != null) {
			ProjectRecord pr = Daten.project.getClubworkBookRecord(currentCW.getName());
			if (pr != null) {
				double sDefaultClubworkTargetHours = pr.getDefaultClubworkTargetHours();
				double sTransferableClubworkHours = pr.getTransferableClubworkHours();

				double max = sDefaultClubworkTargetHours+sTransferableClubworkHours;
				double min = sDefaultClubworkTargetHours-sTransferableClubworkHours;
				Clubwork clubwork = Daten.project.getClubwork(currentCW.getName(), false);
				long lock = -1;
				try {
					lock = clubwork.data().acquireGlobalLock();

					// Save Carry Over
					Set<UUID> set = data.keySet();

					Iterator<UUID> itr = set.iterator();
					while (itr.hasNext()) {
						UUID person = itr.next();

						ClubworkRecord record = clubwork.createClubworkRecord(UUID.randomUUID());
						record.setPersonId(person);
						record.setWorkDate(DataTypeDate.today());
						record.setDescription(International.getString("Übertrag"));

						Double hours = data.get(person);
						if(hours == null) {
							hours = new Double(0);
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
							clubwork.data().add(record);
						} catch (Exception eignore) {
							Logger.logdebug(eignore);
						}
					}

					// Save Yearly Credit
					Vector<PersonRecord> persons = Daten.project.getPersons(false).getAllPersons(-1, false, false);

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
					}
				} catch (Exception e) {
					Logger.logdebug(e);
					Logger.log(Logger.ERROR, Logger.MSG_ERR_GENERIC,
							LogString.operationAborted(International.getString("Vereinsarbeit-Übertrag")));
					Messages messages = Daten.project.getMessages(false);
					messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_ADMIN,
							International.getString("Vereinsarbeit-Übertrag"),
							International.getString("efa hat soeben versucht den Übertrag für die Vereinsarbeit zu berechnen.") + "\n"
									+ International.getString("Bei diesem Vorgang traten jedoch FEHLER auf.") + "\n\n"
									+ International.getString("Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden."));
				} finally {
					if (clubwork != null && lock >= 0) {
						clubwork.data().releaseGlobalLock(lock);
					}
				}
			}
		}
	}

}
