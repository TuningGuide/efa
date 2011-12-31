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

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.PersonRecord;
import de.nmichael.efa.data.StatisticsRecord;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.data.efawett.Zielfahrt;
import de.nmichael.efa.data.efawett.ZielfahrtFolge;
import java.util.Vector;

public class StatisticsData implements Comparable {

    public static final int LOGBOOK_FIELD_COUNT = 10;
    public static final String SORTING_PREFIX  = "%%";
    public static final String SORTING_POSTFIX = "$$";

    private StatisticsRecord sr;

    Object key;
    String sPosition;
    String sName;
    String sStatus;
    String sYearOfBirth;
    String sBoatType;
    String sDistance;
    String sSessions;
    String sAvgDistance;
    String sDestinationAreas;
    String sAdditional;
    String[][] sDetailsArray;
    String sCompAttr1;
    String sCompAttr2;
    String sCompWarning;

    long distance = 0;
    long sessions = 0;
    long avgDistance = 0;
    SessionHistory sessionHistory;

    DataTypeIntString entryNo;
    DataTypeDate date;
    String[] logbookFields;
    CompetitionData compData;

    PersonRecord personRecord; // filled by postprocessing if this is a person
    String gender;
    boolean disabled;

    int absPosition = 0;
    Vector<Zielfahrt> destinationAreaVector;
    ZielfahrtFolge destinationAreas;
    Zielfahrt[] bestDestinationAreas;
    Zielfahrt[] additionalDestinationAreas;
    
    boolean isSummary = false;
    boolean isMaximum = false;
    boolean compFulfilled = false;

    StatisticsData next; // used for chained lists of competition participants

    public StatisticsData(StatisticsRecord sr) {
        this.sr = sr;
    }

    public void updateSummary(StatisticsData sd) {
        this.distance += sd.distance;
        this.sessions += sd.sessions;
    }

    public void updateMaximum(StatisticsData sd) {
        if (sd.distance > this.distance) {
            this.distance = sd.distance;
        }
        if (sd.sessions > this.sessions) {
            this.sessions = sd.sessions;
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
            this.sDistance = DataTypeDistance.getDistance(this.distance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, decimals);
        }
        if (sr.sIsAggrSessions) {
            this.sSessions = Long.toString(this.sessions);
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

    }

    public int compareTo(Object o) {
        return compareTo(o, true);
    }

    public int compareTo(Object o, boolean withSecondCriterion) {
        StatisticsData osd = (StatisticsData)o;

        int order = (sr.sSortingOrderAscending ? 1 : -1);

        switch(sr.sSortingCriteria) {
            case distance:
                if (this.distance > osd.distance) {
                    return 1 * order;
                } else if (this.distance < osd.distance) {
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
        }

        if (withSecondCriterion && this.sName != null && osd.sName != null) {
            return this.sName.compareTo(osd.sName);
        }
        return 0;
    }

}
