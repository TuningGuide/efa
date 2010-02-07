/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import java.util.StringTokenizer;

// @i18n complete

public class ConfigTypeLongLat extends ConfigTypeLabelValue { // @todo change Superclass!!

    public static int TYPE_LONGITUDE = 0;
    public static int TYPE_LATITUDE = 1;

    public static int ORIENTATION_NORTH = 0;
    public static int ORIENTATION_SOUTH = 1;
    public static int ORIENTATION_WEST = 2;
    public static int ORIENTATION_EAST = 3;

    private static final String DELIM = ",";

    private int longLatType;
    private int orientation;
    private int[] coordinates = new int[3];
    
    public ConfigTypeLongLat(String name, int longLatType, int orientation, int c1, int c2, int c3, int type,
            String category, String description) {
        this.name = name;
        this.longLatType = type;
        this.orientation = orientation;
        coordinates[0] = c1;
        coordinates[1] = c2;
        coordinates[2] = c3;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public void parseValue(String value) {
        try {
            StringTokenizer tok = new StringTokenizer(value, DELIM);
            int i = 0;
            while (tok.hasMoreTokens()) {
                String t = tok.nextToken();
                switch (i) {
                    case 0:
                        type = EfaUtil.string2int(t, 0);
                        break;
                    case 1:
                        orientation = EfaUtil.string2int(t, 0);
                        break;
                    case 2:
                        coordinates[0] = EfaUtil.string2int(t, 0);
                        break;
                    case 3:
                        coordinates[1] = EfaUtil.string2int(t, 0);
                        break;
                    case 4:
                        coordinates[2] = EfaUtil.string2int(t, 0);
                        break;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGUNSUPPPARMTYPE,
                    "EfaConfig: Invalid value for parameter " + name + ": " + value);
        }
    }

    public String toString() {
        return type + DELIM + orientation + DELIM + coordinates[0] + DELIM + coordinates[1] + DELIM + coordinates[2];
    }

}
