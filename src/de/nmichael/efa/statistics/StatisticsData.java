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

public class StatisticsData implements Comparable {

    Object key;
    String text;
    long distance = 0;
    long sessions = 0;

    int position = 0;
    int absPosition = 0;
    boolean isSummary = false;

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
