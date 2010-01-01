/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.core.config;

import java.util.Hashtable;
import java.util.StringTokenizer;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;

public class ConfigTypeHashtable<E> {

    public static int TYPE_STRING = 0;
    public static int TYPE_ADMIN = 1;
    public static int NUMBER_OF_TYPES = 2;

    private static final String DUMMY = "%%%DUMMY%%%";
    private static final String DELIM_KEYVALUE = "-->";
    private static final String DELIM_ELEMENTS = "@@@";
    private Hashtable<String,E> hash;
    private E e;
    
    public ConfigTypeHashtable(E e) {
        this.e = e;
        iniHash();
    }

    private void iniHash() {
        hash = new Hashtable<String,E>();
        hash.put(DUMMY, e);
    }



    public void put(String s, E value) {
        hash.put(s, value);
    }

    public E get(String s) {
        return hash.get(s);
    }

    public int size() {
        return hash.size();
    }

    public ConfigTypeHashtable parseHashtable(String s) throws Exception {
        iniHash();
        StringTokenizer tok = new StringTokenizer(s,DELIM_ELEMENTS);
        while(tok.hasMoreTokens()) {
            String t = tok.nextToken();
            int pos = t.indexOf(DELIM_KEYVALUE);
            String key = t.substring(0,pos);
            key = new String(Base64.decode(key), Daten.ENCODING_UTF);
            String val = t.substring(pos+DELIM_KEYVALUE.length());
            val = new String(Base64.decode(val), Daten.ENCODING_UTF);
            E e = hash.get(DUMMY);
            Class c = e.getClass();
            Object v = null;
            boolean matchingTypeFound = false;
            for (int i = 0; i < NUMBER_OF_TYPES; i++) {
                switch (i) {
                    case 0: // TYPE_STRING
                        v = val;
                        break;
                    case 1: // TYPE_ADMIN
/*
                        try {
                        v = Admin.parseAdmin(val);
                        } catch (Exception e) {
                        }
                         */
                        break;
                }
                if (c.isInstance(v)) {
                    hash.put(key, (E) v);
                    matchingTypeFound = true;
                    break;
                }
            }
            if (!matchingTypeFound) {
                // should never happen (program error); no need to translate
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                        "ConfigTypesHashtable: unsupported value type for key " + key + ": " + c.getCanonicalName());
            }
        }

        return this;
    }

    public String toString() {
        String s = "";

        String[] keys = new String[hash.size()];
        keys = hash.keySet().toArray(keys);
        for (int i=0; i<keys.length; i++) {
            E value = hash.get(keys[i]);
            if (keys[i].equals(DUMMY)) {
                continue;
            }
            try {
                String key = Base64.encodeBytes(keys[i].getBytes(Daten.ENCODING_UTF));
                String val = Base64.encodeBytes(value.toString().getBytes(Daten.ENCODING_UTF));
                s += (s.length() > 0 ? DELIM_ELEMENTS : "") +
                     key + DELIM_KEYVALUE + val;
            } catch(Exception e) {
                // should never happen (program error); no need to translate
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGINVALIDVALUE,
                         "ConfigTypesHashtable: cannot create string for value '"+keys[i]+"': "+e.toString());
            }
        }
        return s;
    }

}
