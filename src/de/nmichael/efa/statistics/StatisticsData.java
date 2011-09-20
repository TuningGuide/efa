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

import java.util.UUID;

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

    long distance = 0;
    long sessions = 0;
    long avgDistance = 0;
    int absPosition = 0;
    
    boolean isSummary = false;
    boolean isMaximum = false;

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
