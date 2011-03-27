/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.data.types;

import java.util.*;
import de.nmichael.efa.util.*;

public class DataTypeDate implements Cloneable, Comparable<DataTypeDate> {

    private int day, month, year;

    // Default Constructor
    public DataTypeDate() {
        unset();
    }

    // Regular Constructor
    public DataTypeDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        ensureCorrectDate();
    }

    // Copy Constructor
    public DataTypeDate(DataTypeDate date) {
        this.day = date.day;
        this.month = date.month;
        this.year = date.year;
        ensureCorrectDate();
    }

    public static DataTypeDate parseDate(String s) {
        DataTypeDate date = new DataTypeDate();
        date.setDate(s);
        return date;
    }

    public static DataTypeDate today() {
        DataTypeDate date = new DataTypeDate();
        Calendar cal = new GregorianCalendar();
        date.setDay(cal.get(Calendar.DAY_OF_MONTH));
        date.setMonth(cal.get(Calendar.MONTH)+1);
        date.setYear(cal.get(Calendar.YEAR));
        return date;
    }

    public void setDate(DataTypeDate date) {
        this.day = date.day;
        this.month = date.month;
        this.year = date.year;
        ensureCorrectDate();
    }

    public void setDate(Calendar date) {
        this.day = date.get(GregorianCalendar.DAY_OF_MONTH);
        this.month = date.get(GregorianCalendar.MONTH) + 1;
        this.year = date.get(GregorianCalendar.YEAR);
        ensureCorrectDate();
    }

    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        ensureCorrectDate();
    }

    public void setDate(String s) {
        TMJ tmj = EfaUtil.string2date(s, -1, -1, -1);
        this.day = tmj.tag;
        this.month = tmj.monat;
        this.year = tmj.jahr;
        ensureCorrectDate();
    }

    public void setMonthAndYear(String s) {
        TMJ tmj = EfaUtil.string2date(s, -1, -1, -1);
        this.day = 0;
        this.month = tmj.tag;
        this.year = tmj.monat;
        ensureCorrectDate();
    }

    public void setYear(String s) {
        TMJ tmj = EfaUtil.string2date(s, -1, -1, -1);
        this.day = 0;
        this.month = 0;
        this.year = tmj.tag;
        ensureCorrectDate();
    }

    public GregorianCalendar toCalendar() {
        int _day = (day > 0 ? day : 1);       // if day is not specified (day==0), assume first of month
        int _month = (month > 0 ? month : 1); // if month is not specified (month==0), assume first of year
        return new GregorianCalendar(year, _month - 1, _day);
    }

    public void ensureCorrectDate() {
        if (!isSet()) {
            return;
        }
        boolean fourdigit = year >= 1000 && year <= 9999;
        if (year < 0 || year > 9999) {
            year = 0;
        }
        if (!fourdigit && year < 1900) {
            year += 1900;
        }
        if (!fourdigit && year < 1920) {
            year += 100;
        }
        if (month < 0 || month > 12) { // treat month==0 as "unset month" and don't correct it
            month = 1;
        }
        if (day < 0 || day > 31) { // treat day==0 as "unset day" and don't correct it
            day = 1;
        }
        switch (month) {
            case 4:
            case 6:
            case 9:
            case 11:
                if (day > 30) {
                    day = 30;
                }
                break;
            case 2:
                if (day > 29) {
                    day = 29;
                }
                if (day > 28 && year % 4 != 0) {
                    day = 28;
                }
                break;
            default:
        }
    }

    public String toString() {
        if (day < 0 || month < 0 || year < 0) {
            return "";
        }
        if (month == 0) {
            return EfaUtil.int2String(year,4);
        }
        if (day == 0) {
            return EfaUtil.int2String(month,2) + "/" + EfaUtil.int2String(year,4);
        }
        return EfaUtil.int2String(day,2) + "." + EfaUtil.int2String(month,2) + "." + EfaUtil.int2String(year,4);
    }

    public boolean isSet() {
        return day != -1 && month != -1 && year != -1;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
        ensureCorrectDate();
    }

    public void setMonth(int month) {
        this.month = month;
        ensureCorrectDate();
    }

    public void setYear(int year) {
        this.year = year;
        ensureCorrectDate();
    }

    public void setDayMonth(int day, int month) {
        this.day = day;
        this.month = month;
        ensureCorrectDate();
    }

    public void addDays(int days) {
        if (!isSet()) {
            return;
        }
        Calendar cal = toCalendar();
        cal.add(GregorianCalendar.DATE, days);
        setDate(cal);
    }

    public void unset() {
        day = -1;
        month = -1;
        year = -1;
    }

    public boolean equals(Object o) {
        try {
            return compareTo((DataTypeDate)o) == 0;
        } catch(Exception e) {
            return false;
        }
    }

    public int compareTo(DataTypeDate o) {
        if (year < o.year) {
            return -1;
        }
        if (year > o.year) {
            return 1;
        }
        if (month < o.month) {
            return -1;
        }
        if (month > o.month) {
            return 1;
        }
        if (day < o.day) {
            return -1;
        }
        if (day > o.day) {
            return 1;
        }
        return 0;
    }

    public int hashCode() {
        return (new Integer(year*10000 + month*100 + day)).hashCode();
    }
    
    public boolean isBefore(DataTypeDate o) {
        return compareTo(o) < 0;
    }

    public boolean isAfter(DataTypeDate o) {
        return compareTo(o) > 0;
    }

    public long getTimestamp(DataTypeTime time) {
        if (isSet()) {
            Calendar cal = toCalendar();
            if (time != null && time.isSet()) {
                cal.set(Calendar.HOUR_OF_DAY, time.getHour());
                cal.set(Calendar.MINUTE, time.getMinute());
                cal.set(Calendar.SECOND, time.getSecond());
            }
            return cal.getTimeInMillis();
        }
        return 0;
    }
    
}
