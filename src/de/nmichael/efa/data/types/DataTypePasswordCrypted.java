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

import de.nmichael.efa.util.*;

public class DataTypePasswordCrypted {

    private static final String CRYPTED = "*~c:";

    private String password;

    // Default Constructor
    public DataTypePasswordCrypted() {
        unset();
    }

    // Regular Constructor
    public DataTypePasswordCrypted(String password) {
        setPassword(password);
    }

    // Copy Constructor
    public DataTypePasswordCrypted(DataTypePasswordCrypted pwd) {
        this.password = pwd.password;
    }

    public static DataTypePasswordCrypted parsePassword(String s) {
        return new DataTypePasswordCrypted(s);
    }

    // @todo (P3) dummy encryption - implement real algorithm
    public static String encrypt(String sd) {
        StringBuffer se = new StringBuffer();
        for (int i=0; i<sd.length(); i++) {
            char c = sd.charAt(i);
            se.append(++c);
            
        }
        return se.toString();
    }

    // @todo (P3) dummy encryption - implement real algorithm
    public static String decrypt(String se) {
        StringBuffer sd = new StringBuffer();
        for (int i=0; i<se.length(); i++) {
            char c = se.charAt(i);
            sd.append(--c);
        }
        return sd.toString();
    }

    public void setPassword(String s) {
        if (s != null) {
            s = s.trim();
            if (s.startsWith(CRYPTED)) {
                password = decrypt(s.substring(CRYPTED.length()));
            } else {
                password = s;
            }
        } else {
            password = null;
        }
    }

    public String getPassword() {
        if (isSet()) {
            return password;
        } else {
            return null;
        }
    }

    public String toString() {
        if (isSet()) {
            return CRYPTED + encrypt(password);
        }
        return "";
    }

    public boolean isSet() {
        return password != null && password.length() > 0;
    }

    public void unset() {
        password = null;
    }

    public boolean equals(DataTypePasswordCrypted pwd) {
        return this.isSet() && pwd.isSet() && this.password.equals(pwd.password);
    }

 }
