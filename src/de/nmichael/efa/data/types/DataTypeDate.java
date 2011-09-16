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

    public DataTypeDate(long timestamp) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timestamp);
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.month = cal.get(Calendar.MONTH)+1;
        this.year = cal.get(Calendar.YEAR);
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
        if (day > 0 && month == -1 && year == -1) { // String with year only
            year = day;
            day = month = 0;
        }
        if (day > 0 && month > 0 && year == -1) { // String with month and year only
            year = month;
            month = day;
            day = 0;
        }
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

    public boolean isBeforeOrEqual(DataTypeDate o) {
        return compareTo(o) <= 0;
    }

    public boolean isAfter(DataTypeDate o) {
        return compareTo(o) > 0;
    }

    public boolean isAfterOrEqual(DataTypeDate o) {
        return compareTo(o) >= 0;
    }

    public boolean isInRange(DataTypeDate from, DataTypeDate to) {
        return (compareTo(from) >= 0) && (compareTo(to) <= 0);
    }

    public static boolean isRangeOverlap(DataTypeDate r1From, DataTypeDate r1To, DataTypeDate r2From, DataTypeDate r2To) {
        return (r2From.isBeforeOrEqual(r1From) && r2To.isAfterOrEqual(r1From)) ||
               (r2From.isBeforeOrEqual(r1To) && r2To.isAfterOrEqual(r1To)) ||
               (r2From.isAfterOrEqual(r1From) && r2To.isBeforeOrEqual(r1To)) ||
               (r1From.isBeforeOrEqual(r2From) && r1To.isAfterOrEqual(r2From)) ||
               (r1From.isBeforeOrEqual(r2To) && r1To.isAfterOrEqual(r2To)) ||
               (r1From.isAfterOrEqual(r2From) && r1To.isBeforeOrEqual(r2To));
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

    private static long daysBetween(final Calendar startDate, final Calendar endDate) {
        Calendar sDate = (Calendar) startDate.clone();
        long daysBetween = 0;

        int y1 = sDate.get(Calendar.YEAR);
        int y2 = endDate.get(Calendar.YEAR);
        int m1 = sDate.get(Calendar.MONTH);
        int m2 = endDate.get(Calendar.MONTH);

        //**year optimization**
        while (((y2 - y1) * 12 + (m2 - m1)) > 12) {

            //move to Jan 01
            if (sDate.get(Calendar.MONTH) == Calendar.JANUARY
                    && sDate.get(Calendar.DAY_OF_MONTH) == sDate.getActualMinimum(Calendar.DAY_OF_MONTH)) {

                daysBetween += sDate.getActualMaximum(Calendar.DAY_OF_YEAR);
                sDate.add(Calendar.YEAR, 1);
            } else {
                int diff = 1 + sDate.getActualMaximum(Calendar.DAY_OF_YEAR) - sDate.get(Calendar.DAY_OF_YEAR);
                sDate.add(Calendar.DAY_OF_YEAR, diff);
                daysBetween += diff;
            }
            y1 = sDate.get(Calendar.YEAR);
        }

        //** optimize for month **
        //while the difference is more than a month, add a month to start month
        while ((m2 - m1) % 12 > 1) {
            daysBetween += sDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            sDate.add(Calendar.MONTH, 1);
            m1 = sDate.get(Calendar.MONTH);
        }

        // process remainder date
        while (sDate.before(endDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }

        return daysBetween;
    }

    public long getDifferenceDays(DataTypeDate o) {
        Calendar c1 = toCalendar();
        Calendar c2 = o.toCalendar();
        if (c1.before(c2)) {
            return daysBetween(c1, c2);
        }
        if (c2.before(c1)) {
            return daysBetween(c2, c1);
        }
        return 0;
    }

    public static String getDateTimeString(DataTypeDate date, DataTypeTime time) {
        String s = null;
        if (date != null && date.isSet()) {
            s = date.toString();
        }
        if (time != null && time.isSet()) {
            s = (s == null ? time.toString() : s + " " + time.toString());
        }
        return s;
    }

    /**
     * formats the date as a string
     * @param format the format, which *must* contain DD, MM and YYYY, e.g. "DD.MM.YYYY" or "YYYY-MM-DD"
     * @return
     */
    public String getDateString(String format) {
        if (format == null) {
            return null;
        }
        int posDay = format.indexOf("DD");
        int posMonth = format.indexOf("MM");
        int posYear = format.indexOf("YYYY");
        if (posDay < 0 || posMonth < 0 || posYear < 0) {
            return null;
        }
        String s = format;
        s = EfaUtil.replace(s, "DD", EfaUtil.int2String(day,2));
        s = EfaUtil.replace(s, "MM", EfaUtil.int2String(month,2));
        s = EfaUtil.replace(s, "YYYY", EfaUtil.int2String(year,4));
        return s;
    }
    
}
