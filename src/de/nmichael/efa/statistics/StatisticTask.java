/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

import de.nmichael.efa.data.efawett.ZielfahrtFolge;
import de.nmichael.efa.data.efawett.Zielfahrt;
import java.util.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.data.efawett.WettDefs;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.gui.*;

public class StatisticTask extends ProgressTask {

	private static final int WORK_PER_STATISTIC  = 100;
	private static final int WORK_POSTPROCESSING =  50;

	private StatisticsRecord[] statisticsRecords;
	private AdminRecord admin;
	private StatisticsRecord sr;
	private Hashtable<Object,StatisticsData> data = new Hashtable<Object,StatisticsData>();
	private Persons persons = Daten.project.getPersons(false);
	private Boats boats = Daten.project.getBoats(false);
	private Destinations destinations = Daten.project.getDestinations(false);
	private Waters waters = Daten.project.getWaters(false);
	private Groups groups = Daten.project.getGroups(false);
	private Status status = Daten.project.getStatus(false);
	private StatusRecord statusGuest = null;
	private StatusRecord statusOther = null;

	// values from current logbook entry
	private Logbook logbook;
	private DataTypeIntString entryNo;
	private DataTypeDate entryDate;
	private DataTypeDate entryEndDate;
	private long entryNumberOfDays;
	private long entryValidAt;
	// ---
	private UUID entryBoatId;
	private BoatRecord entryBoatRecord;
	private String entryBoatName;
	private String entryBoatType;
	private String entryBoatSeats;
	private String entryBoatRigging;
	private String entryBoatCoxing;
	private String entryBoatOwner;
	private boolean entryBoatExclude;
	// ---
	private UUID entryPersonId;
	private PersonRecord entryPersonRecord;
	private String entryPersonName;
	private UUID entryPersonStatusId;
	private boolean entryPersonIsGuest;
	private boolean entryPersonIsOther;
	private String entryPersonGender;
	private boolean entryPersonExclude;
	// ---
	private UUID entryDestinationId;
	private DestinationRecord entryDestinationRecord;
	private String entryDestinationVariant;
	private String entryDestinationName;
	private String entryDestinationNameAndVariant;
	private ZielfahrtFolge entryDestinationAreas;
	private long entryDistanceInDefaultUnit;
	private String entrySessionType;
	private SessionGroupRecord entrySessionGroup;
	// --- TODO einbinden der Variablen
	private UUID entryClubworkId;
	private ClubworkRecord entryClubworkRecord;
	private UUID entryClubworkPersonId;
	private Date entryClubworkDate;
	private String entryClubworkDescription;
	private int entryClubworkHours;

	// internal variables
	private ArrayList<String> successfulDoneMessages = new ArrayList<String>();

	private StatisticTask(StatisticsRecord[] statisticsRecords, AdminRecord admin) {
		this.statisticsRecords = statisticsRecords;
		this.admin = admin;

		statusGuest = status.getStatusGuest();
		statusOther = status.getStatusOther();
	}

	private void resetEntryValues() {
		entryNo = null;
		entryDate = null;
		entryEndDate = null;
		entryNumberOfDays = 1;
		entryValidAt = -1;
		entryBoatId = null;
		entryBoatRecord = null;
		entryBoatName = null;
		entryBoatType = null;
		entryBoatSeats = null;
		entryBoatRigging = null;
		entryBoatCoxing = null;
		entryBoatOwner = null;
		entryBoatExclude = false;
		entryPersonId = null;
		entryPersonRecord = null;
		entryPersonName = null;
		entryPersonStatusId = null;
		entryPersonIsGuest = false;
		entryPersonIsOther = false;
		entryPersonGender = null;
		entryPersonExclude = false;
		entryDestinationId = null;
		entryDestinationRecord = null;
		entryDestinationVariant = null;
		entryDestinationName = null;
		entryDestinationNameAndVariant = null;
		entryDestinationAreas = null;
		entryDistanceInDefaultUnit = 0;
		entrySessionType = null;
		entrySessionGroup = null;
		// ---
		entryClubworkId = null;
		entryClubworkRecord = null;
		entryClubworkPersonId = null;
		entryClubworkDate = null;
		entryClubworkDescription = null;
		entryClubworkHours = 0;
	}

	private void calculateSessionHistory(LogbookRecord r, Object key, StatisticsData sd, long distanceOfEntry) {
		if (sd.sessionHistory == null) {
			sd.sessionHistory = new SessionHistory();
		}

		boolean splitSessionIntoDays = entryNumberOfDays > 1;
		if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition &&
				(sr.sStatisticType.equals(WettDefs.STR_DRV_FAHRTENABZEICHEN) ||
						sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK))) {
			splitSessionIntoDays = false;
		}

		if (splitSessionIntoDays) {
			long distancePerDay = distanceOfEntry / entryNumberOfDays;
			long remainingDistance = distanceOfEntry;
			DataTypeDate date = entryDate;
			for (int i=1; i<=entryNumberOfDays; i++) {
				long dist = distancePerDay;
				if (i > 1 && i == entryNumberOfDays) {
					dist = remainingDistance;
					date = entryEndDate;
				}
				DataTypeDistance distance = new DataTypeDistance(dist);
				Zielfahrt destArea = null;
				if (entryDestinationAreas != null && i <= entryDestinationAreas.getAnzZielfahrten()) {
					destArea = entryDestinationAreas.getZielfahrt(i-1);
				}
				sd.sessionHistory.addSession(r, i, date, distance, destArea);
				remainingDistance -= dist;
				date.addDays(1);
			}
		} else {
			Zielfahrt destArea = null;
			if (entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
				destArea = entryDestinationAreas.getZielfahrt(0);
				if (entryDestinationAreas.getAnzZielfahrten() > 1) {
					sr.cWarnings.put("Fahrt #"+entryNo.toString() + " hat zu viele Zielfahrten; überzählige Zielbereiche werden ignoriert.",
							"foo");
				}
			}
			if (distanceOfEntry != entryDistanceInDefaultUnit) {
				// happens if we have already split the session, for example for calculating waters
				r = (LogbookRecord)r.cloneRecord();
				r.setDistance(new DataTypeDistance(distanceOfEntry));
			}
			if (destArea != null) {
				sd.sessionHistory.addSession(r, destArea);
			} else {
				sd.sessionHistory.addSession(r);
			}
		}
	}

	private void calculateAggregations(LogbookRecord r, Object key, long distance) {
		if (key == null) {
			return;
		}
		StatisticsData sd = data.get(key);
		if (sd == null) {
			sd = new StatisticsData(sr, key);
		}

		// aggregate
		if (sr.sIsAggrDistance || sr.sIsAggrAvgDistance) {
			sd.distance += distance;
		}
		if (sr.sIsAggrSessions || sr.sIsAggrAvgDistance) {
			sd.sessions += entryNumberOfDays;
		}
		if (sr.sIsAggrZielfahrten &&
				entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
			calculateSessionHistory(r, key, sd, distance);
		}
		if (sr.sIsAggrWanderfahrten &&
				(CompetitionDRVFahrtenabzeichen.mayBeWafa(r) ||
						entrySessionType.equals(EfaTypes.TYPE_SESSION_JUMREGATTA))) {
			calculateSessionHistory(r, key, sd, distance);
		}
		if (sr.sIsAggrWinterfahrten &&
				(CompetitionLRVBerlinWinter.mayBeWinterfahrt(r) &&
						entryPersonRecord != null)) {
			calculateSessionHistory(r, key, sd, distance);
		}
		if (sr.sIsAggrGigfahrten &&
				EfaTypes.isGigBoot(entryBoatType) &&
				entryPersonRecord != null) {
			calculateSessionHistory(r, key, sd, distance);
		}

		data.put(key, sd);
	}

	private void calculateAggregations(ClubworkRecord r, Object key, double hours) {
		if (key == null) {
			return;
		}
		StatisticsData sd = data.get(key);
		if (sd == null) {
			sd = new StatisticsData(sr, key);
		}

		// aggregate
		if (sr.sIsAggrClubwork || sr.sIsAggrClubworkRelativeToTarget || sr.sIsAggrClubworkOverUnderCarryOver) {
			sd.clubwork += hours;
		}
		if(sr.sIsAggrClubworkCredit && r.getDescription().startsWith(International.getString("Gutschrift"))) {
			sd.clubworkCredit += hours;
		}

		data.put(key, sd);
	}

	private void calculateAggregationsForList(LogbookRecord r, DataTypeList list) {
		if (list == null || list.length() == 0) {
			return;
		}
		int size = list.length();
		long distance = 0;

		for (int i = 0; i < size; i++) {
			Object key = list.get(i);
			long myDistance = entryDistanceInDefaultUnit / size;
			if (i+1 == size) {
				myDistance = entryDistanceInDefaultUnit - distance;
			}
			calculateAggregations(r, key, myDistance);
			distance += myDistance;
		}
	}

	private Object getAggregationKeyForList(LogbookRecord r) {
		// Note: This method only provides the aggregation key for StatisticCategory=LIST!
		switch (sr.sStatistikKey) {
		case name:
			switch(sr.sStatisticTypeEnum) {
			case persons:
				if (sr.sSumGuestsByClub && entryPersonIsGuest) {
					String clubName = International.getString("unbekannt");
					if (entryPersonRecord != null && entryPersonRecord.getAssocitation() != null &&
							entryPersonRecord.getAssocitation().length() > 0) {
						clubName = entryPersonRecord.getAssocitation();
					}
					return StatisticsData.SORTTOEND_PREFIX + International.getMessage("Gäste von {club}", clubName);
				}
				if (sr.sSumGuestsAndOthers && (entryPersonIsGuest || entryPersonIsOther)) {
					if (entryPersonIsGuest) {
						return StatisticsData.SORTTOEND_PREFIX + International.getString("Gäste");
					}
					if (entryPersonIsOther) {
						return StatisticsData.SORTTOEND_PREFIX + International.getString("andere");
					}
				}
				if (entryPersonId != null) {
					return entryPersonId;
				}
				if (entryPersonName != null) {
					return entryPersonName;
				}
				break;
			case boats:
				if (sr.sSumGuestsByClub && entryBoatRecord != null) {
					String owner = entryBoatRecord.getOwner();
					if (owner != null && owner.length() > 0) {
						return StatisticsData.SORTTOEND_PREFIX +
								International.getMessage("Fremdboote von {owner}", owner);
					}
				}
				if (entryBoatId != null) {
					return entryBoatId;
				}
				if (entryBoatName != null) {
					return entryBoatName;
				}
				break;
			}
			break;
		case status:
			if (entryPersonStatusId != null) {
				StatusRecord statusRecord = status.getStatus(entryPersonStatusId);
				if (statusRecord != null) {
					return statusRecord.getQualifiedName();
				}
			}
			return EfaTypes.TYPE_STATUS_OTHER;
		case yearOfBirth:
			if (entryPersonRecord != null) {
				DataTypeDate birthday = entryPersonRecord.getBirthday();
				if (birthday != null && birthday.getYear() > 0) {
					return birthday.getYear();
				}
			}
			return EfaTypes.TEXT_UNKNOWN;
		case gender:
			if (entryPersonGender != null) {
				return Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, entryPersonGender);
			}
			return EfaTypes.TEXT_UNKNOWN;
		case boatType:
			if (entryBoatType != null) {
				return Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, entryBoatType);
			}
			return EfaTypes.TEXT_UNKNOWN;
		case boatSeats:
			if (entryBoatSeats != null) {
				return Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, entryBoatSeats);
			}
			return EfaTypes.TEXT_UNKNOWN;
		case boatTypeDetail:
			if (entryBoatType != null && entryBoatSeats != null && entryBoatCoxing != null) {
				return entryBoatRecord.getDetailedBoatType(entryBoatType, entryBoatSeats, entryBoatCoxing);
			}
			return EfaTypes.TEXT_UNKNOWN;
		case destination:
			if (entryDestinationNameAndVariant != null && entryDestinationNameAndVariant.length() > 0) {
				return entryDestinationNameAndVariant;
			}
			break;
		case waters:
			DataTypeList<String> watersText = new DataTypeList<String>();
			DataTypeList<UUID> watersList = null;
			if (entryDestinationRecord != null) {
				watersList = entryDestinationRecord.getWatersIdList();
			}
			if (r.getWatersIdList() != null) {
				if (watersList != null) {
					watersList.addAll(r.getWatersIdList());
				} else {
					watersList = r.getWatersIdList();
				}
			}
			if (watersList != null && watersList.length() > 0) {
				for (int i = 0; i < watersList.length(); i++) {
					WatersRecord w = waters.getWaters(watersList.get(i));
					if (w != null) {
						watersText.add(w.getQualifiedName());
					}
				}
			}
			if (r.getWatersNameList() != null) {
				for (int i = 0; i < r.getWatersNameList().length(); i++) {
					if (!watersText.contains(r.getWatersNameList().get(i))) {
						watersText.add(r.getWatersNameList().get(i));
					}
				}
			}
			if (watersText.length() > 0) {
				return watersText;
			}
			break;
		case distance:
			if (entryDistanceInDefaultUnit > 0) {
				DataTypeDistance dist = DataTypeDistance.getDistance(entryDistanceInDefaultUnit);
				dist.truncateToMainDistanceUnit();
				return "<= " + dist.getAsFormattedString();
			}
			break;
		case month:
			if (entryDate != null && entryDate.isSet()) {
				return entryDate.getMonthAsStringWithIntMarking(StatisticsData.SORTING_PREFIX, StatisticsData.SORTING_POSTFIX);
			}
			break;
		case weekday:
			if (entryDate != null && entryDate.isSet()) {
				return entryDate.getWeekdayAsStringWithIntMarking(StatisticsData.SORTING_PREFIX, StatisticsData.SORTING_POSTFIX);
			}
			break;
		case timeOfDay:
			// @todo (P6) statistics - time of day
			break;
		case sessionType:
			if (entrySessionType != null) {
				return Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, entrySessionType);
			}
			break;
		case year:
			if (entryDate != null && entryDate.isSet()) {
				return entryDate.getYear();
			}
			break;
		}
		return null;
	}

	private Object getAggregationKeyForCompetition(LogbookRecord r) {
		// Note: This method only provides the aggregation key for StatisticCategory=COMPETITION!
		if (!sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK)) {
			return this.entryPersonId;
		} else {
			return CompetitionDRVWanderruderstatistik.getAggregationKey(r, sr.sValidAt);
		}
	}

	private Object getAggregationKeyForClubwork(ClubworkRecord r) {
		// Note: This method only provides the aggregation key for StatisticCategory=LIST!
		switch (sr.sStatistikKey) {
		case name:
			if (sr.sSumGuestsByClub && entryPersonIsGuest) {
				String clubName = International.getString("unbekannt");
				if (entryPersonRecord != null && entryPersonRecord.getAssocitation() != null &&
						entryPersonRecord.getAssocitation().length() > 0) {
					clubName = entryPersonRecord.getAssocitation();
				}
				return StatisticsData.SORTTOEND_PREFIX + International.getMessage("Gäste von {club}", clubName);
			}
			if (sr.sSumGuestsAndOthers && (entryPersonIsGuest || entryPersonIsOther)) {
				if (entryPersonIsGuest) {
					return StatisticsData.SORTTOEND_PREFIX + International.getString("Gäste");
				}
				if (entryPersonIsOther) {
					return StatisticsData.SORTTOEND_PREFIX + International.getString("andere");
				}
			}
			if (entryPersonId != null) {
				return entryPersonId;
			}
			if (entryPersonName != null) {
				return entryPersonName;
			}
		case gender:
			if (entryPersonGender != null) {
				return Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, entryPersonGender);
			}
			return EfaTypes.TEXT_UNKNOWN;
		case month:
			if (entryDate != null && entryDate.isSet()) {
				return entryDate.getMonthAsStringWithIntMarking(StatisticsData.SORTING_PREFIX, StatisticsData.SORTING_POSTFIX);
			}
			break;
		case year:
			if (entryDate != null && entryDate.isSet()) {
				return entryDate.getYear();
			}
			break;
		}
		return null;
	}

	private void calculateEntryForList(LogbookRecord r) {
		if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons) {
			for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
				getEntryPerson(r, i);
				if (entryPersonId == null && entryPersonName == null) {
					continue;
				}
				if (isInPersonFilter() && isInGroupFilter()) {
					Object aggregationKey = getAggregationKeyForList(r);
					if (aggregationKey != null) {
						if (sr.sStatistikKey != StatisticsRecord.StatisticKey.waters) {
							calculateAggregations(r, aggregationKey, entryDistanceInDefaultUnit);
						} else {
							calculateAggregationsForList(r, (DataTypeList)aggregationKey);
						}
					}
				}
			}
		}
		if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats) {
			Object aggregationKey = getAggregationKeyForList(r);
			if (aggregationKey != null) {
				if (sr.sStatistikKey != StatisticsRecord.StatisticKey.waters) {
					calculateAggregations(r, aggregationKey, entryDistanceInDefaultUnit);
				} else {
					calculateAggregationsForList(r, (DataTypeList) aggregationKey);
				}
			}
		}
	}

	private void calculateEntryForLogbook(LogbookRecord r) {
		boolean isAtLeastOneInFilter = false;
		for (int i = 0; !isAtLeastOneInFilter && i < LogbookRecord.CREW_MAX; i++) {
			getEntryPerson(r, i);
			if (entryPersonId == null && entryPersonName == null) {
				continue;
			}
			if (isInPersonFilter() && isInGroupFilter()) {
				isAtLeastOneInFilter = true;
			}
		}
		if (!isAtLeastOneInFilter) {
			return;
		}

		StatisticsData sd = new StatisticsData(sr, logbook.getName() + ":" + entryNo.toString());
		sd.entryNo = entryNo;
		sd.date = entryDate;
		sd.sessions = 1; // we count every entry as one session
		int fieldCount = sr.getLogbookFieldCount();
		if (fieldCount < 2) {
			fieldCount = 2; // at least sIsLFieldsEntryNo and sIsLFieldsDate are always enabled
		}
		sd.logbookFields = new String[fieldCount];
		int col = 0;
		if (sr.sIsLFieldsEntryNo) {
			sd.logbookFields[col++] = entryNo.toString();
		}
		if (sr.sIsLFieldsDate) {
			sd.logbookFields[col++] = entryDate.toString();
		}
		if (sr.sIsLFieldsEndDate) {
			sd.logbookFields[col++] = (entryEndDate != null && entryEndDate.isSet() ? entryEndDate.toString() : "");
		}
		if (sr.sIsLFieldsBoat) {
			sd.logbookFields[col++] = (entryBoatRecord != null ? entryBoatRecord.getQualifiedName() : entryBoatName);
		}
		String coxName = "";
		String crewNames = "";
		for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
			getEntryPerson(r, i);
			String name = (entryPersonRecord != null ? entryPersonRecord.getQualifiedName() : entryPersonName);
			if (name == null) {
				name = "";
			}
			if (sr.sPublicStatistic && entryPersonExclude) {
				name = "<" + International.getString("anonym") + ">"; // < and > will be correctly escaped in output!
			}
			if (sr.sOutputType == StatisticsRecord.OutputTypes.html ||
					sr.sOutputType == StatisticsRecord.OutputTypes.internal) {
				if (i == r.getBoatCaptainPosition()) {
					name = StatisticWriter.TEXTMARK_BOLDSTART + name + StatisticWriter.TEXTMARK_BOLDEND;
				}
			}
			if (i == 0) {
				coxName = name;
			} else {
				crewNames = (name.length() > 0
						? (crewNames == null || crewNames.length() == 0 ? name
								: crewNames + "; " + name) : crewNames);
			}
		}
		if (sr.sIsLFieldsCox) {
			sd.logbookFields[col++] = coxName;
		}
		if (sr.sIsLFieldsCrew) {
			sd.logbookFields[col++] = crewNames;
		}
		if (sr.sIsLFieldsStartTime) {
			sd.logbookFields[col++] = (r.getStartTime() != null ? r.getStartTime().toString(false) : "");
		}
		if (sr.sIsLFieldsEndTime) {
			sd.logbookFields[col++] = (r.getEndTime() != null ? r.getEndTime().toString(false) : "");
		}
		if (sr.sIsLFieldsWaters) {
			String w = null;
			if (entryDestinationRecord != null) {
				w = DestinationRecord.getWatersNamesStringList(
						r.getPersistence().getProject().getWaters(false),
						entryDestinationRecord.getWatersIdList(),
						r.getWatersIdList(), r.getWatersNameList());
			} else {
				w = DestinationRecord.getWatersNamesStringList(
						r.getPersistence().getProject().getWaters(false),
						null,
						r.getWatersIdList(), r.getWatersNameList());
			}
			sd.logbookFields[col++] = (w != null ? w : "");
		}
		if (sr.sIsLFieldsDestination) {
			sd.logbookFields[col++] = r.getDestinationAndVariantName(entryValidAt);
		}
		if (sr.sIsLFieldsDestinationDetails) {
			sd.logbookFields[col++] = (entryDestinationRecord != null ?
					entryDestinationRecord.getDestinationDetailsAsString() : "");
		}
		if (sr.sIsLFieldsDestinationAreas) {
			sd.logbookFields[col++] = (entryDestinationAreas != null ?
					entryDestinationAreas.toString() : "");
		}
		if (sr.sIsLFieldsDistance) {
			sd.logbookFields[col++] = (r.getDistance() != null ? 
					r.getDistance().getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, 3) : "");
			// we update the sd.distance because we use this field to summarize all output data
			sd.distance = (r.getDistance() != null ? 
					r.getDistance().getValueInDefaultUnit() : 0);
		}
		if (sr.sIsLFieldsMultiDay) {
			sd.logbookFields[col++] = (entrySessionGroup != null ?
					entrySessionGroup.getSessionTypeDescription() : "");
		}
		if (sr.sIsLFieldsSessionType) {
			sd.logbookFields[col++] = (entrySessionType != null ?
					Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, entrySessionType) : "");
		}
		if (sr.sIsLFieldsNotes) {
			sd.logbookFields[col++] = (r.getComments() != null ? r.getComments() : "");
		}
		data.put(sd.key, sd);
	}

	private void calculateEntryForCompetition(LogbookRecord r) {
		for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
			getEntryPerson(r, i);
			if (entryPersonRecord == null) {
				continue;
			}
			if (entryPersonRecord.getExcludeFromCompetition()) {
				sr.pStatIgnored.put((entryPersonRecord != null
						? entryPersonRecord.getQualifiedName() : entryPersonName), "foo");
				continue;
			}
			if (isInPersonFilter() && isInGroupFilter()) {
				// Statistics for Competitions are only calculated based on known Names
				Object aggregationKey = getAggregationKeyForCompetition(r);
				if (aggregationKey != null) {
					if (!sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK)) {
						calculateAggregations(r, aggregationKey, entryDistanceInDefaultUnit);
					} else {
						if (sr.cCompetition != null) {
							((CompetitionDRVWanderruderstatistik)sr.cCompetition).calculateAggregation(data,
									r, aggregationKey, entryPersonRecord);
						}
					}
				}
			}
		}
	}

	private void calculateEntryForClubwork(ClubworkRecord r) {
		resetEntryValues();
		getEntryBasic(r);
		if (!isInRange(r) //||
				/*TODO: !isInFilter(r) ||
				r.getSessionIsOpen()*/) {
			return;
		}

		// update number of evaluated entries
		sr.cNumberOfEntries++;

		// update date range of evaluated entries
		if (entryDate != null && entryDate.isSet()) {
			if (sr.cEntryDateFirst == null || entryDate.isBefore(sr.cEntryDateFirst)) {
				sr.cEntryDateFirst = entryDate;
			}
			if (sr.cEntryDateLast == null || entryDate.isAfter(sr.cEntryDateLast)) {
				sr.cEntryDateLast = entryDate;
			}
		}

		// update entryno for first and last evaluated entry
		if (entryNo != null) {
			if (sr.cEntryNoFirst == null/* || entryNo.intValue() < sr.cEntryNoFirst.intValue()*/) {
				sr.cEntryNoFirst = entryNo;
			}
			//if (sr.cEntryNoLast == null || entryNo.intValue() > sr.cEntryNoLast.intValue()) {
				sr.cEntryNoLast = entryNo;
			//}
		}

		getEntryPerson(r);
		if (entryPersonId == null && entryPersonName == null) {
			return;
		}
		if (isInPersonFilter() && isInGroupFilter()) {
			Object aggregationKey = getAggregationKeyForClubwork(r);
			if (aggregationKey != null) {
				if (sr.sStatistikKey != StatisticsRecord.StatisticKey.waters) {
					calculateAggregations(r, aggregationKey, r.getHours());
				}
			}
		}
	}


	private void calculateEntry(LogbookRecord r) {
		resetEntryValues();
		getEntryBasic(r);
		if (!isInRange(r) ||
				!isInFilter(r) ||
				r.getSessionIsOpen()) {
			return;
		}

		// update number of evaluated entries
		sr.cNumberOfEntries++;

		// update date range of evaluated entries
		if (entryDate != null && entryDate.isSet()) {
			if (sr.cEntryDateFirst == null || entryDate.isBefore(sr.cEntryDateFirst)) {
				sr.cEntryDateFirst = entryDate;
			}
			if (sr.cEntryDateLast == null || entryDate.isAfter(sr.cEntryDateLast)) {
				sr.cEntryDateLast = entryDate;
			}
			if (entryEndDate != null && entryEndDate.isSet() && (sr.cEntryDateLast == null || entryEndDate.isAfter(sr.cEntryDateLast))) {
				sr.cEntryDateLast = entryEndDate;
			}
		}

		// update entryno for first and last evaluated entry
		if (entryNo != null) {
			if (sr.cEntryNoFirst == null || entryNo.intValue() < sr.cEntryNoFirst.intValue()) {
				sr.cEntryNoFirst = entryNo;
			}
			if (sr.cEntryNoLast == null || entryNo.intValue() > sr.cEntryNoLast.intValue()) {
				sr.cEntryNoLast = entryNo;
			}
		}

		// get further data
		if (sr.sIsAggrZielfahrten || 
				sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook ||
				sr.sStatistikKey == StatisticsRecord.StatisticKey.destination ||
				sr.sStatistikKey == StatisticsRecord.StatisticKey.waters) {
			getEntryDestination(r);
		}

		if (!getEntryDistance(r)) {
			return;
		}

		switch (sr.sStatisticCategory) {
		case list:
			calculateEntryForList(r);
			break;
		case logbook:
			calculateEntryForLogbook(r);
			break;
		case competition:
			calculateEntryForCompetition(r);
			break;
		}
	}

	private void getEntryBasic(LogbookRecord r) {
		entryNo = r.getEntryId();
		entryValidAt = r.getValidAtTimestamp();
	}
	
	private void getEntryBasic(ClubworkRecord r) {
		entryNo = new DataTypeIntString((sr.cNumberOfEntries+1)+""); // TODO: new DataTypeIntString(r.getQualifiedName());
		entryValidAt = r.getWorkDate().getTimestamp(new DataTypeTime(0, 0, 0));
	}

	private void getEntryDates(LogbookRecord r) {
		entryDate = r.getDate();
		entryEndDate = r.getEndDate();
		entryNumberOfDays = 1;
		if (entryDate != null && entryDate.isSet() &&
				entryEndDate != null && entryEndDate.isSet()) {
			entryNumberOfDays = entryEndDate.getDifferenceDays(entryDate) + 1;
			if (entryNumberOfDays > 1) {
				getSessionGroup(r);
				if (entrySessionGroup != null &&
						entrySessionGroup.checkLogbookRecordFitsIntoRange(r) &&
						entryDate.equals(entrySessionGroup.getStartDate()) &&
						entryEndDate.equals(entrySessionGroup.getEndDate())) {
					entryNumberOfDays = entrySessionGroup.getActiveDays();
				}
			}
		}
	}
	
	private void getEntryDates(ClubworkRecord r) {
		entryDate = r.getWorkDate();
		entryNumberOfDays = 1;
	}

	private void getSessionGroup(LogbookRecord r) {
		entrySessionGroup = r.getSessionGroup();
	}

	private void getEntrySessionType(LogbookRecord r) {
		entrySessionType = r.getSessionType();
		if (entrySessionType == null) {
			entrySessionType = EfaTypes.TYPE_SESSION_NORMAL;
		}
	}

	private void getEntryBoat(LogbookRecord r) {
		entryBoatId = r.getBoatId();
		entryBoatRecord = (entryBoatId != null ? boats.getBoat(entryBoatId, entryValidAt) : null);
		entryBoatName = (entryBoatId != null ? null : r.getBoatName());
		int boatVariant = r.getBoatVariant();
		int vidx = -1;
		if (entryBoatRecord != null) {
			if (entryBoatRecord.getNumberOfVariants() == 1) {
				vidx = 0;
			} else {
				vidx = entryBoatRecord.getVariantIndex(boatVariant);
			}
		}
		if (vidx >= 0) {
			entryBoatType = entryBoatRecord.getTypeType(vidx);
			entryBoatSeats = entryBoatRecord.getTypeSeats(vidx);
			entryBoatRigging = entryBoatRecord.getTypeRigging(vidx);
			entryBoatCoxing = entryBoatRecord.getTypeCoxing(vidx);
		}
		if (entryBoatType == null) {
			entryBoatType = EfaTypes.TYPE_BOAT_OTHER;
		}
		if (entryBoatSeats == null) {
			entryBoatSeats = EfaTypes.TYPE_NUMSEATS_OTHER;
		}
		if (entryBoatRigging == null) {
			entryBoatRigging = EfaTypes.TYPE_RIGGING_OTHER;
		}
		if (entryBoatCoxing == null) {
			entryBoatCoxing = EfaTypes.TYPE_COXING_OTHER;
		}
		entryBoatOwner = StatisticsRecord.BOWNER_UNKNOWN;
		if (entryBoatRecord != null) {
			if (entryBoatRecord.getOwner() == null || entryBoatRecord.getOwner().length() == 0) {
				entryBoatOwner = StatisticsRecord.BOWNER_OWN;
			} else {
				entryBoatOwner = StatisticsRecord.BOWNER_OTHER;
			}
		}
		entryBoatExclude = (entryBoatRecord != null && entryBoatRecord.getExcludeFromPublicStatistics() &&
				sr.getPubliclyAvailable());
	}

	private void getEntryPerson(LogbookRecord r, int pos) {
		entryPersonId = r.getCrewId(pos);
		entryPersonRecord = (entryPersonId != null ? persons.getPerson(entryPersonId, entryValidAt) : null);
		entryPersonName = (entryPersonId != null ? null : r.getCrewName(pos));
		entryPersonStatusId = (entryPersonRecord != null ? entryPersonRecord.getStatusId() : null);
		entryPersonIsGuest = (entryPersonStatusId != null && entryPersonStatusId.equals(this.statusGuest.getId()));
		entryPersonIsOther = (entryPersonStatusId == null || entryPersonStatusId.equals(this.statusOther.getId()));
		entryPersonGender = (entryPersonRecord != null ? entryPersonRecord.getGender() : null);
		entryPersonExclude = (entryPersonRecord != null && entryPersonRecord.getExcludeFromPublicStatistics() &&
				sr.getPubliclyAvailable());
	}
	
	private void getEntryPerson(ClubworkRecord r) {
		entryPersonId = r.getPersonId();
		entryPersonRecord = (entryPersonId != null ? persons.getPerson(entryPersonId, entryValidAt) : null);
		entryPersonName = (entryPersonId != null ? null : entryPersonRecord.getFirstLastName());
		entryPersonStatusId = (entryPersonRecord != null ? entryPersonRecord.getStatusId() : null);
		entryPersonIsGuest = (entryPersonStatusId != null && entryPersonStatusId.equals(this.statusGuest.getId()));
		entryPersonIsOther = (entryPersonStatusId == null || entryPersonStatusId.equals(this.statusOther.getId()));
		entryPersonGender = (entryPersonRecord != null ? entryPersonRecord.getGender() : null);
		entryPersonExclude = (entryPersonRecord != null && entryPersonRecord.getExcludeFromPublicStatistics() &&
				sr.getPubliclyAvailable());
	}

	private void getEntryDestination(LogbookRecord r) {
		entryDestinationId = r.getDestinationId();
		entryDestinationRecord = (entryDestinationId != null ? destinations.getDestination(entryDestinationId, entryValidAt) : null);
		entryDestinationVariant  = (entryDestinationId != null ? r.getDestinationVariantName() : null);
		entryDestinationName = (entryDestinationId != null ? null : r.getDestinationName());
		entryDestinationNameAndVariant = r.getDestinationAndVariantName(entryValidAt);
		entryDestinationAreas = (entryDestinationRecord != null ? entryDestinationRecord.getDestinationAreas() : null);
	}

	private boolean getEntryDistance(LogbookRecord r) {
		entryDistanceInDefaultUnit = (r.getDistance() != null ?
				r.getDistance().getValueInDefaultUnit() : 0);
		return entryDistanceInDefaultUnit != 0;
	}

	private boolean isInRange(LogbookRecord r) {
		getEntryDates(r);
		if (entryDate == null || !entryDate.isSet()) {
			return false;
		}
		if (entryEndDate == null || !entryEndDate.isSet()) {
			return entryDate.isInRange(sr.sStartDate, sr.sEndDate);
		} else {
			return entryDate.isInRange(sr.sStartDate, sr.sEndDate) && // both start *and* end date must be in range!
					entryEndDate.isInRange(sr.sStartDate, sr.sEndDate);
		}
	}
	
	private boolean isInRange(ClubworkRecord r) {
		getEntryDates(r);
		if (entryDate == null || !entryDate.isSet()) {
			return false;
		} else {
			return entryDate.isInRange(sr.sStartDate, sr.sEndDate);
		}
	}

	private boolean isInFilter(LogbookRecord r) {
		getEntrySessionType(r);
		if (!sr.sFilterSessionTypeAll && !sr.sFilterSessionType.containsKey(entrySessionType)) {
			return false;
		}

		getEntryBoat(r);
		if ((!sr.sFilterBoatTypeAll && !sr.sFilterBoatType.containsKey(entryBoatType)) ||
				(!sr.sFilterBoatSeatsAll && !sr.sFilterBoatSeats.containsKey(entryBoatSeats)) ||
				(!sr.sFilterBoatRiggingAll && !sr.sFilterBoatRigging.containsKey(entryBoatRigging)) ||
				(!sr.sFilterBoatCoxingAll && !sr.sFilterBoatCoxing.containsKey(entryBoatCoxing)) ||
				(!sr.sFilterBoatOwnerAll && !sr.sFilterBoatOwner.containsKey(entryBoatOwner))) {
			return false;
		}

		if (sr.sFilterByBoatId != null && !sr.sFilterByBoatId.equals(r.getBoatId())) {
			return false;
		}
		if (sr.sFilterByBoatText != null && !sr.sFilterByBoatText.equals(r.getBoatAsName())) {
			return false;
		}
		if (sr.sPublicStatistic && entryBoatExclude) {
			sr.pStatIgnored.put( (entryBoatRecord != null ?
					entryBoatRecord.getQualifiedName() : entryBoatName), "foo");
			return false;
		}

		return true;
	}

	private boolean isInPersonFilter() {
		if (entryPersonRecord != null) {
			// known person
			if (!sr.sFilterStatusAll &&
					(entryPersonStatusId == null || !sr.sFilterStatus.containsKey(entryPersonStatusId))) {
				return false;
			}
			if (!sr.sFilterGenderAll &&
					(entryPersonGender == null || !sr.sFilterGender.containsKey(entryPersonGender))) {
				return false;
			}
			if (sr.sFilterByPersonId != null && !sr.sFilterByPersonId.equals(entryPersonRecord.getId())) {
				return false;
			}
			if (sr.sFilterByPersonText != null && !sr.sFilterByPersonText.equals(entryPersonRecord.getQualifiedName())) {
				return false;
			}
			if (sr.sPublicStatistic && entryPersonExclude) {
				sr.pStatIgnored.put((entryPersonRecord != null
						? entryPersonRecord.getQualifiedName() : entryPersonName), "foo");
				return false;
			}
			return true;
		} else {
			// unknown person
			if (entryPersonName == null) {
				return false;
			}
			if (!sr.sFilterStatusAll && !sr.sFilterStatusOther) {
				return false;
			}
			if (!sr.sFilterGenderAll) {
				return false; // both MALE and FEMALE must be selected
			}
			if (sr.sFilterByPersonId != null) {
				return false;
			}
			if (sr.sFilterByPersonText != null && !sr.sFilterByPersonText.equals(entryPersonName)) {
				return false;
			}
			return true;
		}
	}

	private boolean isInGroupFilter() {
		if (sr.sFilterByGroupId != null) {
			if (entryPersonRecord == null) {
				return false;
			}
			GroupRecord gr = groups.findGroupRecord(sr.sFilterByGroupId, entryValidAt);
			if (gr == null) {
				return false;
			}
			DataTypeList<UUID> glist = gr.getMemberIdList();
			if (glist == null) {
				return false;
			}
			return glist.contains(entryPersonRecord.getId());
		}
		return true;
	}

	private Vector<Logbook> getAllLogbooks() {
		Vector<Logbook> logbooks = new Vector<Logbook>();
		if (Daten.project == null) {
			return logbooks;
		}
		String[] names = Daten.project.getAllLogbookNames();
		for (int i=0; names != null && i<names.length; i++) {
			ProjectRecord pr = Daten.project.getLoogbookRecord(names[i]);
			if (pr != null) {
				DataTypeDate lbStart = pr.getStartDate();
				DataTypeDate lbEnd = pr.getEndDate();
				if (lbStart == null || lbEnd == null) {
					continue; // should never happen
				}
				if (DataTypeDate.isRangeOverlap(lbStart, lbEnd, sr.sStartDate, sr.sEndDate)) {
					Logbook l = Daten.project.getLogbook(names[i], false);
					if (l != null) {
						logbooks.add(l);
					}
				}
			}
		}
		return logbooks;
	}

	private StatisticsData[] runPostprocessing() {
		logInfo(International.getString("Aufbereiten der Daten") + " ...\n");
		int workBeforePostprocessing = this.getCurrentWorkDone();

		String statusOtherText;
		try {
			statusOtherText = persons.getProject().getStatus(false).getStatusOther().getQualifiedName();
		} catch (Exception eignore) {
			statusOtherText = International.getString("andere");
		}


		StatisticsData sdSummary = new StatisticsData(sr, null);
		sdSummary.isSummary = true;
		sdSummary.sName = International.getString("gesamt") + " (" + data.size() + ")";
		StatisticsData sdMaximum = new StatisticsData(sr, null);
		sdMaximum.isMaximum = true;

		Object[] keys = data.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			StatisticsData sd = data.get(keys[i]);
			boolean isUUID = false;

			// replace UUID by Person Name
			if ((sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition &&
					!sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK))
					||
					(sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons &&
					sr.sStatistikKey == StatisticsRecord.StatisticKey.name) ||
					sr.sStatisticCategory == StatisticsRecord.StatisticCategory.clubwork) {
				PersonRecord pr = null;
				if (sd.key instanceof UUID) {
					pr = persons.getPerson((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
					isUUID = true;
					sd.personRecord = pr;
				}
				if (sr.sIsFieldsName) {
					sd.sName = (pr != null ? pr.getQualifiedName()
							: (isUUID ? "*** " + International.getString("ungültiger Eintrag") + " ***" : sd.key.toString()));
				}
				if (sr.sIsFieldsGender) {
					sd.sGender = (pr != null ? pr.getGenderAsString() : null);
					if (sd.sGender == null) {
						sd.sGender = International.getString("unbekannt");
					}
				}
				if (sr.sIsFieldsStatus) {
					sd.sStatus = (pr != null ? pr.getStatusName() : statusOtherText);
				}
				if (sr.sIsFieldsYearOfBirth) {
					DataTypeDate birthday = (pr != null ? pr.getBirthday() : null);
					if (birthday != null && birthday.isSet()) {
						sd.sYearOfBirth = Integer.toString(birthday.getYear());
					}
				}
				if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition && pr != null) {
					sd.gender = pr.getGender();
					sd.disabled = pr.getDisability();
				}
			}

			// replace UUID by Boat Name
			if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats &&
					sr.sStatistikKey == StatisticsRecord.StatisticKey.name) {

				BoatRecord br = null;
				if (sd.key instanceof UUID) {
					br = boats.getBoat((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
					isUUID = true;
				}
				if (sr.sIsFieldsName) {
					sd.sName = (br != null ? br.getQualifiedName()
							: (isUUID ? "*** " + International.getString("ungültiger Eintrag") + " ***" : sd.key.toString()));
				}
				if (sr.sIsFieldsBoatType) {
					sd.sBoatType = (br != null ? br.getDetailedBoatType(0) : Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, EfaTypes.TYPE_BOAT_OTHER));
				}

			}

			// use Key as Name for any other Data
			if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list &&
					sr.sStatistikKey != StatisticsRecord.StatisticKey.name) {
				sd.sName = sd.key.toString();
			}

			// Calculate Summary and Maximum
			sdSummary.updateSummary(sd);
			sdMaximum.updateMaximum(sd);
		}
		setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*1);

		// summary for Logbook
		if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
			if (sdSummary.logbookFields == null) {
				sdSummary.logbookFields = new String[sr.getLogbookFieldCount()];
			}
			if (sr.sLFieldDistancePos >= 0 && sr.sLFieldDistancePos < sdSummary.logbookFields.length) {
				sdSummary.logbookFields[0] = sdSummary.sName;
				sdSummary.logbookFields[sr.sLFieldDistancePos] =
						DataTypeDistance.getDistance(sdSummary.distance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0,
								(sr.sTruncateDistanceToFullValue ? 0 : 1));
			}
		}


		// Create Array and sort
		StatisticsData[] sdArray = new StatisticsData[keys.length + 2];
		for (int i=0; i<keys.length; i++) {
			sdArray[i] = data.get(keys[i]);
		}
		Arrays.sort(sdArray, 0, keys.length);
		sdArray[sdArray.length - 2] = sdSummary;
		sdArray[sdArray.length - 1] = sdMaximum;
		setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*2);

		// Calculate String Output Values
		for (int i=0; i<sdArray.length; i++) {
			sdArray[i].createStringOutputValues(sr, i,
					(i > 0 && sdArray[i].compareTo(sdArray[i-1], false) == 0 ? sdArray[i - 1].sPosition : Integer.toString(i + 1) + "."));
		}
		setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*3);

		if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition) {
			if (sr.cCompetition != null) {
				sr.cCompetition.calculate(sr, sdArray);
			}
		}

		sr.pParentDialog = this.progressDialog;

		// Statistics Base Data
		String statDescrShort;
		String statDescrLong;
		switch(sr.sStatisticCategory) {
		case list:
			statDescrShort = sr.getStatisticCategoryDescription() + " " +
					(sr.sStatistikKey == StatisticsRecord.StatisticKey.name ?
							sr.getStatisticTypeDescription() : sr.getStatisticKeyDescriptionPlural());
			statDescrLong = statDescrShort +
					(sr.sStatistikKey != StatisticsRecord.StatisticKey.name ?
							" (" + sr.getStatisticTypeDescription() + ")" : "");
			break;
		case logbook:
			statDescrShort =  sr.getStatisticCategoryDescription();
			statDescrLong = statDescrShort;
			break;
		case competition:
			statDescrShort =  sr.getStatisticTypeDescription();
			statDescrLong = statDescrShort;
			break;
		default:
			statDescrShort =  International.getString("Statistik");
			statDescrLong = statDescrShort;
		}
		sr.pStatTitle = statDescrShort;
		if (sr.cEntryDateFirst != null && sr.cEntryDateFirst.isSet()) {
			sr.pStatTitle += " " + sr.cEntryDateFirst.getYear();
			if (sr.cEntryDateLast != null && sr.cEntryDateLast.isSet() && sr.cEntryDateFirst.getYear() != sr.cEntryDateLast.getYear()) {
				sr.pStatTitle += " - " + sr.cEntryDateLast.getYear();
			}
		}
		sr.pStatCreationDate = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
		sr.pStatCreatedByUrl = Daten.EFAURL;
		sr.pStatCreatedByName = Daten.EFA_LONGNAME + " " + Daten.VERSION;
		sr.pStatDescription = statDescrLong;
		sr.pStatDateRange = sr.sStartDate.toString() + " - " + sr.sEndDate.toString();
		sr.pStatFilter = sr.getFilterCriteriaAsStringDescription();
		sr.pStatConsideredEntries = International.getMessage("{n} Einträge", sr.cNumberOfEntries);
		if (sr.cNumberOfEntries > 0 && sr.cEntryNoFirst != null && sr.cEntryNoLast != null) {
			sr.pStatConsideredEntries += ": #" + sr.cEntryNoFirst.toString() + " - #" + sr.cEntryNoLast.toString();
			if (sr.cEntryDateFirst != null && sr.cEntryDateFirst.isSet() && sr.cEntryDateLast != null && sr.cEntryDateLast.isSet()) {
				sr.pStatConsideredEntries += " (" + International.getMessage("vom {day_from} bis {day_to}",
						sr.cEntryDateFirst.toString(), sr.cEntryDateLast.toString()) + ")";
			}
		}

		if (sr.sStatisticCategory != StatisticsRecord.StatisticCategory.competition) {
			// Table Columns
			sr.prepareTableColumns();
		}

		setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING/5)*4);
		return sdArray;
	}

	private String writeStatistic(StatisticsData[] sd) {
		logInfo(International.getString("Ausgabe der Daten") + " ...\n");
		StatisticWriter writer = StatisticWriter.getWriter(sr, sd);
		if (writer.write()) {
			if (sr.sOutputFtpClient != null) {
				logInfo(International.getString("FTP-Upload") + " ...\n");
				return sr.sOutputFtpClient.write();
			}
		}
		return writer.getResultMessage();
	}

	private String createStatistic(StatisticsRecord sr, int statisticsNumber) {
		this.sr = sr;
		if (!sr.prepareStatisticSettings(admin)) {
			return null;
		}
		logInfo(International.getMessage("Erstelle Statistik für den Zeitraum {from} bis {to} ...", 
				sr.sStartDate.toString(), sr.sEndDate.toString()) + "\n", false, true);
		logInfo(International.getString("Erstelle Statistik ..."),
				true, false);

		if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.clubwork) {
			if (Daten.project == null) {
				return null;
			}
			String[] names = Daten.project.getAllLogbookNames();
			if (names.length == 0) {
				Dialog.error(International.getMessage("Es wurden keine Vereinsarbeits-Einstellungen im Fahrtenbuch vom {fromdate} bis {todate} gefunden.",
						sr.sStartDate.toString(), sr.sEndDate.toString()));
				return null;
			}
			for (int i=0; names != null && i<names.length; i++) {
				ProjectRecord pr = Daten.project.getLoogbookRecord(names[i]);
				if (pr != null) {
					logInfo(International.getString("Fahrtenbuch") + " " + pr.getLogbookName() + " ...\n");
					sr.sDefaultClubworkTargetHours = pr.getDefaultClubworkTargetHours();
					sr.sTransferableClubworkHours = pr.getTransferableClubworkHours();
					sr.sFineForTooLittleClubwork = pr.getFineForTooLittleClubwork();

					Clubwork clubwork = Daten.project.getClubwork(names[i], false);
					DataTypeDate lbStart = clubwork.getStartDate();
					DataTypeDate lbEnd = clubwork.getEndDate();
					if (lbStart == null || lbEnd == null) {
						continue; // should never happen
					}
		
					if(clubwork != null && DataTypeDate.isRangeOverlap(sr.sStartDate, sr.sEndDate, lbStart, lbEnd)) {
						DataKeyIterator it;
						try {
							it = clubwork.data().getStaticIterator();
							int WORK_PER_LOGBOOK = WORK_PER_STATISTIC / it.size();
							int size = it.size();
							DataKey k = it.getFirst();
							int pos = 0;
							while (k != null) {
								ClubworkRecord r = (ClubworkRecord) clubwork.data().get(k);
		
								DataTypeDate date = r.getWorkDate();
								if(sr.sStartDate.compareTo(date) <= 0 && sr.sEndDate.compareTo(date) >= 0) {
									calculateEntryForClubwork(r);
								}
								this.setCurrentWorkDone( ((++pos * WORK_PER_LOGBOOK) / size) + (i * WORK_PER_LOGBOOK) + (statisticsNumber *  WORK_PER_STATISTIC));
								k = it.getNext();
							}
						} catch (Exception e) {
							logInfo("ERROR: " + e.toString() + "\n");
						}
					}
				}
			}
		} else {
			if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition) {
				sr.cCompetition = Competition.getCompetition(sr);
			}
			Vector<Logbook> logbooks = getAllLogbooks();
			if (logbooks.size() == 0) {
				Dialog.error(International.getMessage("Keine Fahrten im Zeitraum {fromdate} bis {todate} gefunden.",
						sr.sStartDate.toString(), sr.sEndDate.toString()));
				return null;
			}
			int WORK_PER_LOGBOOK = WORK_PER_STATISTIC / logbooks.size();
			for (int i=0; i<logbooks.size(); i++) {
				try {
					logbook = logbooks.get(i);
					logInfo(International.getString("Fahrtenbuch") + " " + logbook.getName() + " ...\n");
					DataKeyIterator it = logbook.data().getStaticIterator();
					int size = it.size();
					DataKey k = it.getFirst();
					int pos = 0;
					while (k != null) {
						LogbookRecord r = (LogbookRecord)logbook.data().get(k);
						if (r != null) {
							calculateEntry(r);
						}
						this.setCurrentWorkDone( ((++pos * WORK_PER_LOGBOOK) / size) + (i * WORK_PER_LOGBOOK) + (statisticsNumber *  WORK_PER_STATISTIC));
						k = it.getNext();
					}
				} catch(Exception e) {
					logInfo("ERROR: " + e.toString() + "\n");
				}
			}
		}

		StatisticsData[] sd = runPostprocessing();
		return writeStatistic(sd);
	}

	private void createStatistics(ProgressDialog progressDialog) {
		this.start();
		if (progressDialog != null) {
			progressDialog.showDialog();
		} else {
			try {
				this.join();
			} catch(Exception e) {
				Logger.logdebug(e);
			}
		}
	}

	public void run() {
		setRunning(true);
		try {
			// if we finish creating the statistics before progressDialog.showDialog()
			// in createStatistics() has completed, and at the end of creating the statistic
			// we open a new window (like a Browser for the internal statistics), then
			// the ProgressDialog will end up on top of the Window stack above the Browser.
			// When the browser is then closed, it's not top of stack, and efa's WindowStack
			// check will notice that. It's not really bad when that happens, but it's better
			// to avoid this as it will result in a warning.
			// In the time I have written this explanation, I might have coded a better and
			// safer solution, but it's been a long day and I'm tired, so sleeping half a
			// second must do.
			Thread.sleep(500);
		} catch(Exception eignore) {
		}
		for (int i=0; i<statisticsRecords.length; i++) {
			String msg = createStatistic(statisticsRecords[i], i);
			if (msg != null && msg.length() > 0) {
				successfulDoneMessages.add(msg);
			}
			setCurrentWorkDone((i+1) * WORK_PER_STATISTIC);
		}
		setDone();
	}

	public int getAbsoluteWork() {
		return (statisticsRecords != null ? statisticsRecords.length : 0) * WORK_PER_STATISTIC + WORK_POSTPROCESSING;
	}

	public String getSuccessfullyDoneMessage() {
		if (successfulDoneMessages != null && successfulDoneMessages.size() > 0) {
			StringBuffer s = new StringBuffer();
			for (int i=0; i<successfulDoneMessages.size(); i++) {
				if (i == 10) {
					s.append("\n...");
					break;
				}
				s.append( (s.length() > 0 ? "\n" : "") + successfulDoneMessages.get(i));
			}
			return s.toString();
		}
		return null; // avoid info dialog at the end
	}

	public static void createStatisticsTask(BaseFrame parentFrame, BaseDialog parentDialog, StatisticsRecord[] sr, AdminRecord admin) {
		StatisticTask statisticTask = new StatisticTask(sr, admin);
		ProgressDialog progressDialog = (parentFrame != null ?
				new ProgressDialog(parentFrame, International.getString("Statistik erstellen"), statisticTask, true, true) :
					(parentDialog != null ? 
							new ProgressDialog(parentDialog, International.getString("Statistik erstellen"), statisticTask, true, true)
					: null));
		statisticTask.createStatistics(progressDialog);
	}
}
