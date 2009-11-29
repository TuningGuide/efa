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

import de.nmichael.efa.util.*;
import java.util.HashMap;

// @i18n complete

public class EfaConfig {

    // Parameter Types
    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXPERT = 1;
    public static final int TYPE_PUBLIC = 2;

    // Parameter Categories
    private String CATEGORY_INTERNAL;
    private String CATEGORY_BOATHOUSE;
    private String CATEGORY_BOATHOUSE_GUI;
    
    // private internal data
    private String filename;
    private HashMap<String,ConfigValue> configValues = new HashMap<String,ConfigValue>();
    
    // public configuration data
    public ConfigValue<String> letzteDatei;
    public ConfigValue<Integer> countEfaStarts;

    private int iii = 0; // @todo -- remove, just for test

    public EfaConfig(String filename) {
        this.filename = filename;
        initialize();
    }

    // initialize all configuration parameters with their default values
    private void initialize() {
        iniCategories();
        addParameter(letzteDatei = new ConfigValue<String>("LAST_EFA_FILE", "",
                TYPE_INTERNAL, CATEGORY_INTERNAL,
                International.getString("zuletzt von efa geöffnete Datei")));
        addParameter(countEfaStarts = new ConfigValue<Integer>("COUNT_EFA_STARTS", 0,
                TYPE_INTERNAL, CATEGORY_INTERNAL,
                International.getString("Anzahl der Starts von efa")));
    }

    // initializa all category strings
    private void iniCategories() {
        CATEGORY_INTERNAL = International.getString("intern");
        CATEGORY_BOATHOUSE = International.getString("Bootshaus");
        CATEGORY_BOATHOUSE_GUI = International.getString("Bootshaus") + ":" + International.getString("Erscheinungsbild");
    }

    private void addParameter(ConfigValue configValue) {
        configValues.put(configValue.getName(), configValue);
    }

    public ConfigValue getParameter(String name) {
        return configValues.get(name);
    }

    // @todo not yet fully implemented
    public boolean readFile() {
        String s;

        while ( (s = readLine()) != null) {
            s = s.trim();
            if (s.startsWith("#")) {
                continue;
            }
            int pos = s.indexOf("=");
            if (pos <= 0) {
                continue;
            }

            String name = s.substring(0,pos);
            String value = s.substring(pos+1);

            ConfigValue configValue = getParameter(name);
            if (configValue == null) {
                continue;
            }

            Class c = configValue.getValue().getClass();
            Object v = null;
            for (int i=0; i<2; i++) {
                switch (i) {
                    case 0: v = value;
                            break;
                    case 1: try { v = Integer.parseInt(value); } catch(Exception e) {}
                            break;
                }
                if (c.isInstance(v)) {
                    configValue.setValue(v);
                    break;
                }
            }
            
        }
        return true;
    }

    // @todo not yet fully implemented
    public void writeFile() {
        String[] keys = new String[configValues.size()];
        keys = configValues.keySet().toArray(keys);
        for (int i=0; i<keys.length; i++) {
            ConfigValue configValue = configValues.get(keys[i]);
            writeLine(configValue.getName() + "=" + configValue.getValue());
        }
    }

    // @todo not yet implemented
    private String readLine() {
        // @todo just for test purposes
        switch (iii++) {
            case 0: return "LAST_EFA_FILE=/home/nick/efa/data/2010.efb";
            case 1: return "COUNT_EFA_STARTS=123";
        }
        return null;
    }
    
    // @todo not yet implemented
    private void writeLine(String s) {
        System.out.println(s);
    }



    // for test purposes only
    public static void main(String[] args) {
        EfaConfig efaConfig = new EfaConfig("dummy");
        System.out.println("writing initial config file:");
        efaConfig.writeFile();
        System.out.println("reading config file:");
        efaConfig.readFile();
        System.out.println("writing config file:");
        efaConfig.writeFile();
        System.out.println("letzteDatei = " + efaConfig.letzteDatei.getValue());
        System.out.println("countEfaStarts = " + efaConfig.countEfaStarts.getValue());
    }

}
