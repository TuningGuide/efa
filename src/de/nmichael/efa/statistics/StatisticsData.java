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

import java.util.Vector;

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.PersonRecord;
import de.nmichael.efa.data.StatisticsRecord;
import de.nmichael.efa.data.efawett.Zielfahrt;
import de.nmichael.efa.data.efawett.ZielfahrtFolge;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.data.types.DataTypeHours;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;

public class StatisticsData implements Comparable {

    public static final int LOGBOOK_FIELD_COUNT_NORMAL = 10;
    public static final int LOGBOOK_FIELD_COUNT_EXTENDED = 11;

    public static final String SORTING_PREFIX  = "%%";
    public static final String SORTING_POSTFIX = "$$";
    public static final String SORTTOEND_PREFIX = "$END$";

    private StatisticsRecord sr;

    Object key;
    String sPosition;
    String sName;
    String sGender;
    String sStatus;
    String sYearOfBirth;
    String sBoatType;
    String sDistance;
    String sRowDistance;
    String sCoxDistance;
    String sSessions;
    String sAvgDistance;
    String sDestinationAreas;
    String sWanderfahrten;
    String sAdditional;
    String[][] sDetailsArray;
    String sCompAttr1;
    String sCompAttr2;
    String sCompWarning;
    String sClubwork;
    String sClubworkRelativeToTarget;
    String sClubworkOverUnderCarryOver;
    String sClubworkCredit;

    long distance = 0;
    long rowdistance = 0;
    long coxdistance = 0;
    long sessions = 0;
    long avgDistance = 0;
    SessionHistory sessionHistory;
    double clubwork = 0;
    double clubworkRelativeToTarget = 0;
    double clubworkOverUnderCarryOver = 0;
    double clubworkCredit = 0;

    DataTypeIntString entryNo;
    DataTypeDate date;
    String[] logbookFields;
    CompetitionData compData;

    PersonRecord personRecord; // filled by postprocessing if this is a person
    String gender;
    boolean disabled;

    boolean sortToEnd = false;
    int absPosition = 0;
    Vector<Zielfahrt> destinationAreaVector;
    ZielfahrtFolge destinationAreas;
    Zielfahrt[] bestDestinationAreas;
    Zielfahrt[] additionalDestinationAreas;
    
    boolean isSummary = false;
    boolean isMaximum = false;
    boolean compFulfilled = false;

    StatisticsData next; // used for chained lists of competition participants

    public StatisticsData(StatisticsRecord sr, Object key) {
        this.sr = sr;
        this.key = key;
        if (key != null && key instanceof String && ((String)key).startsWith(SORTTOEND_PREFIX)) {
            this.sortToEnd = true;
        }
    }

    public void updateSummary(StatisticsData sd) {
        this.distance += sd.distance;
        this.rowdistance += sd.rowdistance;
        this.coxdistance += sd.coxdistance;
        this.sessions += sd.sessions;
        this.clubwork += sd.clubwork;
    }

    public void updateMaximum(StatisticsData sd) {
        if (sd.distance > this.distance) {
            this.distance = sd.distance;
        }
        if (sd.rowdistance > this.rowdistance) {
            this.rowdistance = sd.rowdistance;
        }
        if (sd.coxdistance > this.coxdistance) {
            this.coxdistance = sd.coxdistance;
        }
        if (sd.sessions > this.sessions) {
            this.sessions = sd.sessions;
        }
        if (sd.clubwork > this.clubwork) {
            this.clubwork = sd.clubwork;
        }
    }

    public void getAllDestinationAreas() {
        destinationAreas = new ZielfahrtFolge();
        destinationAreaVector = new Vector<Zielfahrt>();
        for (int i=0; sessionHistory != null && i<sessionHistory.size(); i++) {
            LogbookRecord r = sessionHistory.get(i);
            if (r != null && r.zielfahrt != null) {
                r.zielfahrt.setDatum(r.getDate().toString());
                r.zielfahrt.setZiel(r.getDestinationAndVariantName());
                r.zielfahrt.setKm(r.getDistance().getStringValueInKilometers(true, 0, 1));
                destinationAreas.addZielfahrt(r.zielfahrt);
                destinationAreaVector.add(r.zielfahrt);
                //System.out.println(sName + ": " + r.getDate().toString() + " " +
                //        r.getDestinationAndVariantName() + " (" + r.getDistance().toString() + ") -> " + r.zielfahrt.getBereiche() );
            }
        }
        destinationAreas.reduceToMinimun();
    }

    public void createStringOutputValues(StatisticsRecord sr, int absPos, String sPosition) {
        this.absPosition = absPos;
        if (sr.sIsFieldsPosition) {
            if (!this.isMaximum && !this.isSummary) {
                this.sPosition = sPosition;
            } else {
                this.sPosition = "";
            }
        }
        if (sr.sIsFieldsName && sName == null) {
            this.sName = "";
        }
        if (sr.sIsFieldsName && sName.startsWith(SORTING_PREFIX)) {
            int pos = sName.indexOf(SORTING_POSTFIX);
            if (pos > 0) {
                sName = sName.substring(pos + SORTING_POSTFIX.length());
            }
        }
        if (sr.sIsFieldsName && sName.startsWith(SORTTOEND_PREFIX)) {
            sName = sName.substring(SORTTOEND_PREFIX.length());
        }
        if (sr.sIsFieldsGender && sGender == null) {
            this.sGender = "";
        }
        if (sr.sIsFieldsStatus && sStatus == null) {
            this.sStatus = "";
        }
        if (sr.sIsFieldsYearOfBirth && sYearOfBirth == null) {
            this.sYearOfBirth = "";
        }
        if (sr.sIsFieldsBoatType && sBoatType == null) {
            this.sBoatType = "";
        }
        if (sr.sIsAggrDistance) {
            int decimals = 1;
            if (sr.sTruncateDistanceToFullValue) {
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                    decimals = 0;
                }
            }
            if (sr.sIgnoreNullValues && distance == 0) {
                sDistance = "";
            } else {
                this.sDistance = DataTypeDistance.getDistance(this.distance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, decimals);
            }
        }
        if (sr.sIsAggrRowDistance) {
            int decimals = 1;
            if (sr.sTruncateDistanceToFullValue) {
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                    decimals = 0;
                }
            }
            if (sr.sIgnoreNullValues && rowdistance == 0) {
                sRowDistance = "";
            } else {
                this.sRowDistance = DataTypeDistance.getDistance(this.rowdistance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, decimals);
            }
        }
        if (sr.sIsAggrCoxDistance) {
            int decimals = 1;
            if (sr.sTruncateDistanceToFullValue) {
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                    decimals = 0;
                }
            }
            if (sr.sIgnoreNullValues && coxdistance == 0) {
                sCoxDistance = "";
            } else {
                this.sCoxDistance = DataTypeDistance.getDistance(this.coxdistance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, decimals);
            }
        }
        if (sr.sIsAggrSessions) {
            if (sr.sIgnoreNullValues && sessions == 0) {
                this.sSessions = "";
            } else {
                this.sSessions = Long.toString(this.sessions);
            }
        }
        if (sr.sIsAggrAvgDistance) {
            if (this.sessions > 0) {
                this.avgDistance = this.distance / this.sessions;
                this.sAvgDistance = DataTypeDistance.getDistance(this.avgDistance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 1, 1);
            } else {
                this.avgDistance = 0;
                this.sAvgDistance = "";
            }
        }
        if (sr.sIsAggrZielfahrten) {
            if (destinationAreas == null) {
                getAllDestinationAreas();
            }
            this.sDestinationAreas = destinationAreas.toString();
        }
        if (sr.sIsAggrWanderfahrten) {
            int decimals = 1;
            if (sr.sTruncateDistanceToFullValue) {
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list) {
                    decimals = 0;
                }
            }
            long meters = CompetitionDRVFahrtenabzeichen.getWanderfahrtenMeter(this);
            if (sr.sIgnoreNullValues && meters == 0) {
                sWanderfahrten = "";
            } else {
                sWanderfahrten = DataTypeDistance.getDistance(meters).
                        getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, decimals);
            }
        }
        if (sr.sIsAggrClubwork) {
            if (sr.sIgnoreNullValues && clubwork == 0) {
                this.sClubwork = "";
            } else {
            	int month = -1+sr.sStartDate.getMonth() + sr.sEndDate.getMonth() + Math.abs(sr.sEndDate.getYear() - sr.sStartDate.getYear())*12;
            	double targetHours = sr.sDefaultClubworkTargetHours/12*month;
                this.sClubwork = this.clubwork + " " + "h"+" / "+(isSummary ? absPosition*targetHours : targetHours) + " " + "h";
            }
        }
        if (sr.sIsAggrClubworkRelativeToTarget) {
            if (sr.sIgnoreNullValues && clubwork == 0) {
                this.sClubworkRelativeToTarget = "";
            } else {
            	int month = -1+sr.sStartDate.getMonth() + sr.sEndDate.getMonth() + Math.abs(sr.sEndDate.getYear() - sr.sStartDate.getYear())*12;
            	double targetHours = sr.sDefaultClubworkTargetHours/12*month;
            	this.clubworkRelativeToTarget = this.clubwork - (this.isSummary ? absPosition*targetHours : targetHours);
                this.sClubworkRelativeToTarget = this.clubworkRelativeToTarget + " " + "h";
            }
        }
        if (sr.sIsAggrClubworkOverUnderCarryOver) {
            if (this.isSummary || (sr.sIgnoreNullValues && clubwork == 0)) {
                this.sClubworkOverUnderCarryOver = "";
            } else {
            	int month = -1+sr.sStartDate.getMonth() + sr.sEndDate.getMonth() + Math.abs(sr.sEndDate.getYear() - sr.sStartDate.getYear())*12;
            	double targetHours = sr.sDefaultClubworkTargetHours/12*month;
            	
            	this.clubworkOverUnderCarryOver = this.clubwork - targetHours;
            	double t_hours = sr.sTransferableClubworkHours;
            	if(this.clubworkOverUnderCarryOver < - t_hours) {
            		this.clubworkOverUnderCarryOver += t_hours;
            	}
            	else if(this.clubworkOverUnderCarryOver > t_hours) {
            		this.clubworkOverUnderCarryOver+= t_hours;
            	}
            	else {
            		this.clubworkOverUnderCarryOver = 0;
            	}
            		
                this.sClubworkOverUnderCarryOver = this.clubworkOverUnderCarryOver + " " + "h";
            }
        }
        if (sr.sIsAggrClubworkCredit) {
            if (this.isSummary || (sr.sIgnoreNullValues && clubwork == 0)) {
                this.sClubworkCredit = "";
            } else {            		
                this.sClubworkCredit = this.clubworkCredit + " " + "h";
            }
        }
    }

    public int compareTo(Object o) {
        return compareTo(o, true);
    }

    public int compareTo(Object o, boolean withSecondCriterion) {
        StatisticsData osd = (StatisticsData)o;

        int order = (sr.sSortingOrderAscending ? 1 : -1);

        // first check whether we have
        if (this.sortToEnd != osd.sortToEnd) {
            if (this.sortToEnd) {
                return 1;
            } else {
                return -1;
            }
        }

        switch(sr.sSortingCriteria) {
            case distance:
                if (this.distance > osd.distance) {
                    return 1 * order;
                } else if (this.distance < osd.distance) {
                    return -1 * order;
                }
                break;
            case rowdistance:
                if (this.rowdistance > osd.rowdistance) {
                    return 1 * order;
                } else if (this.rowdistance < osd.rowdistance) {
                    return -1 * order;
                }
                break;
            case coxdistance:
                if (this.coxdistance > osd.coxdistance) {
                    return 1 * order;
                } else if (this.coxdistance < osd.coxdistance) {
                    return -1 * order;
                }
                break;
            case sessions:
                if (this.sessions > osd.sessions) {
                    return 1 * order;
                } else if (this.sessions < osd.sessions) {
                    return -1 * order;
                }
                break;
            case avgDistance:
                if (this.avgDistance > osd.avgDistance) {
                    return 1 * order;
                } else if (this.avgDistance < osd.avgDistance) {
                    return -1 * order;
                }
                break;
            case name:
                if (this.sName != null && osd.sName != null) {
                    int res = this.sName.compareTo(osd.sName);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case gender:
                if (this.sGender != null && osd.sGender != null) {
                    int res = this.sGender.compareTo(osd.sGender);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case status:
                if (this.sStatus != null && osd.sStatus != null) {
                    int res = this.sStatus.compareTo(osd.sStatus);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case yearOfBirth:
                if (this.sYearOfBirth != null && osd.sYearOfBirth != null) {
                    int res = EfaUtil.string2int(this.sYearOfBirth, 0) -
                              EfaUtil.string2int(osd.sYearOfBirth, 0);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case boatType:
                if (this.sBoatType != null && osd.sBoatType != null) {
                    int res = this.sBoatType.compareTo(osd.sBoatType);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case entryNo:
                if (this.entryNo != null && osd.entryNo != null) {
                    int res = this.entryNo.compareTo(osd.entryNo);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case date:
                if (this.date != null && osd.date != null) {
                    int res = this.date.compareTo(osd.date);
                    if (res != 0) {
                        return res * order;
                    }
                }
                break;
            case clubwork:
                if (this.clubwork > osd.clubwork) {
                    return 1 * order;
                } else if (this.clubwork < osd.clubwork) {
                    return -1 * order;
                }
                break;
        }

        if (withSecondCriterion && this.sName != null && osd.sName != null) {
            return this.sName.compareTo(osd.sName);
        }
        return 0;

    }

}
