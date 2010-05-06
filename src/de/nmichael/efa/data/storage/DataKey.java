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

public class DataKey<T1,T2,T3> {

    private T1 v1;
    private T2 v2;
    private T3 v3;

    public DataKey(T1 v1, T2 v2, T3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        try {
            DataKey<T1, T2, T3> b = (DataKey<T1, T2, T3>) o;

            // compare value 1
            if (v1 == null && b.v1 == null) {
                return true;
            }
            if (v1 == null || b.v1 == null) {
                return false;
            }
            if (!v1.equals(b.v1)) {
                return false;
            }

            // compare value 2
            if (v2 == null && b.v2 == null) {
                return true;
            }
            if (v2 == null || b.v2 == null) {
                return false;
            }
            if (!v2.equals(b.v2)) {
                return false;
            }

            // compare value 3
            if (v3 == null && b.v3 == null) {
                return true;
            }
            if (v3 == null || b.v3 == null) {
                return false;
            }
            if (!v3.equals(b.v3)) {
                return false;
            }
        } catch(ClassCastException e) {
            return false;
        }
        return true;
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
