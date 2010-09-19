/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.data.types;

import java.util.*;
import de.nmichael.efa.util.*;

public class DataTypeTime implements Cloneable {

    private int hour, minute, second;

    public DataTypeTime() {
        unset();
    }

    public DataTypeTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        ensureCorrectTime();
    }

    public static DataTypeTime parseTime(String s) {
        DataTypeTime time = new DataTypeTime();
        time.setTime(s);
        return time;
    }

    public void setTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        ensureCorrectTime();
    }

    public void setTime(String s) {
        TMJ tmj = EfaUtil.string2date(s, -1, -1, -1);
        this.hour = tmj.tag;
        this.minute = tmj.monat;
        this.second = tmj.jahr;
        ensureCorrectTime();
    }

    public void ensureCorrectTime() {
        if (hour < 0) {
            hour = 0;
        }
        if (minute < 0) {
            minute = 0;
        }
        if (second < -1) { // -1 = unset!
            second = 0;
        }
        if (hour > 23) {
            hour = 23;
        }
        if (minute > 59) {
            minute = 59;
        }
        if (second > 59) {
            second = 59;
        }
    }

    public String toString() {
        if (hour < 0 || minute < 0) {
            return "";
        }
        if (second < 0) {
            return EfaUtil.int2String(hour,2) + ":" + EfaUtil.int2String(minute,2);
        }
        return EfaUtil.int2String(hour,2) + ":" + EfaUtil.int2String(minute,2) + ":" + EfaUtil.int2String(second,4);
    }

    public boolean isSet() {
        return hour != -1 && minute != -1;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public void setHour(int hour) {
        this.hour = hour;
        ensureCorrectTime();
    }

    public void setMinute(int minute) {
        this.minute = minute;
        ensureCorrectTime();
    }

    public void setSecond(int second) {
        this.second = second;
        ensureCorrectTime();
    }

    public void unset() {
        hour = -1;
        minute = -1;
        second = -1;
    }

}
