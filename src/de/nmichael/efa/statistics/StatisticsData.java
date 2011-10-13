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
import de.nmichael.efa.data.StatisticsRecord;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.util.Zielfahrt;
import de.nmichael.efa.util.ZielfahrtFolge;
import java.util.Vector;

public class StatisticsData implements Comparable {

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
    String sCompWarning;


    long distance = 0;
    long sessions = 0;
    long avgDistance = 0;
    SessionHistory sessionHistory;

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

    public long getMainAggregationValue() {
        return distance; // @todo (P2) statistics - sorting and aggregating based on any kind of value
    }

    public void getAllDestinationAreas() {
        destinationAreas = new ZielfahrtFolge();
        destinationAreaVector = new Vector<Zielfahrt>();
        for (int i=0; sessionHistory != null && i<sessionHistory.size(); i++) {
            LogbookRecord r = sessionHistory.get(i);
            if (r != null && r.zielfahrt != null) {
                r.zielfahrt.setDatum(r.getDate().toString());
                r.zielfahrt.setZiel(r.getDestinationAndVariantName());
                r.zielfahrt.setKm(r.getDistance().getValueInKilometers(true, 0, 1));
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
            this.sDistance = DataTypeDistance.getDistance(this.distance).getValueInKilometers(true, 0, 1);
        }
        if (sr.sIsAggrSessions) {
            this.sSessions = Long.toString(this.sessions);
        }
        if (sr.sIsAggrAvgDistance) {
            if (this.sessions > 0) {
                this.avgDistance = this.distance / this.sessions;
                this.sAvgDistance = DataTypeDistance.getDistance(this.avgDistance).getValueInKilometers(true, 1, 1);
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
        StatisticsData osd = (StatisticsData)o;

        if (this.distance < osd.distance) {
            return 1;
        } else if (this.distance > osd.distance) {
            return -1;
        } else {
            return 0;
        }

        /*
        if (this.text == null && osd.text == null) {
            return 0;
        } else if (this.text == null) {
            return -1;
        } else if (osd.text == null) {
            return -1;
        } else {
            return this.text.compareTo(osd.text);
        }
        */
    }

}
