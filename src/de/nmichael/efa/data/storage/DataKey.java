/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import java.util.*;

// @i18n complete

public class DataKey<T1,T2,T3> implements Comparable {

    private T1 v1;
    private T2 v2;
    private T3 v3;

    public DataKey(T1 v1, T2 v2, T3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    private static int compare(Object x, Object y) throws ClassCastException {
        if (x == null && y == null) {
            return 0;
        }
        if (x == null) {
            return -1;
        }
        if (y == null) {
            return 1;
        }
        return ((Comparable)x).compareTo((Comparable)y);
    }

    public int compareTo(Object o) throws ClassCastException {
        if (o == null) {
            return -1;
        }
        try {
            DataKey<T1, T2, T3> b = (DataKey<T1, T2, T3>) o;

            int cmp;
            cmp = compare(v1,b.v1);
            if (cmp != 0) {
                return cmp;
            }
            cmp = compare(v2,b.v2);
            if (cmp != 0) {
                return cmp;
            }
            cmp = compare(v3,b.v3);
            if (cmp != 0) {
                return cmp;
            }
        } catch(ClassCastException e) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        return compareTo(o) == 0;
    }

    public int hashCode() {
        return (
                v1 != null && v2 == null ? v1.hashCode() :
                    v1 != null && v2 != null && v3 == null ? v1.hashCode() + v2.hashCode() :
                        v1 != null && v2 != null && v3 != null ? v1.hashCode() + v2.hashCode() + v3.hashCode() :
                            0
            );
    }

    public String toString() {
        String s = "";
        if (v1 != null) {
            s = s + (s.length() > 0 ? "," : "") + v1.toString();
        }
        if (v2 != null) {
            s = s + (s.length() > 0 ? "," : "") + v2.toString();
        }
        if (v3 != null) {
            s = s + (s.length() > 0 ? "," : "") + v3.toString();
        }
        return s;
    }

}