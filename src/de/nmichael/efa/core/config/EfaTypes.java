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
import de.nmichael.efa.core.*;
import java.io.*;
import java.util.*;

// @i18n complete

public class EfaTypes extends DatenListe {

    public static final String CATEGORY_GENDER            = "GENDER";       // GESCHLECHT
    public static final String CATEGORY_BOAT              = "BOAT";         // BART
    public static final String CATEGORY_NUMROWERS         = "NUMROWERS";    // BANZAHL
    public static final String CATEGORY_RIGGING           = "RIGGING";      // BRIGGER
    public static final String CATEGORY_COXING            = "COXING";       // BSTM
    public static final String CATEGORY_TRIP              = "TRIP";         // FAHRT @todo other name???
    public static final String CATEGORY_STATUS            = "STATUS";       // n/a

    public static final String TYPE_GENDER_MALE           = "MALE";         // MAENNLICH
    public static final String TYPE_GENDER_FEMALE         = "FEMALE";       // WEIBLICH

    public static final String TYPE_BOAT_RACING           = "RACING";       // RENNBOOT
    public static final String TYPE_BOAT_SINGLERACING     = "SINGLERACING"; // SKIFF
    public static final String TYPE_BOAT_WHERRY           = "WHERRY";       // WHERRY
    public static final String TYPE_BOAT_TRIMMY           = "TRIMMY";       // TRIMMY
    public static final String TYPE_BOAT_AGIG             = "AGIG";         // AGIG
    public static final String TYPE_BOAT_BGIG             = "BGIG";         // BGIG
    public static final String TYPE_BOAT_CGIG             = "CGIG";         // CGIG
    public static final String TYPE_BOAT_DGIG             = "DGIG";         // DGIG
    public static final String TYPE_BOAT_EGIG             = "EGIG";         // EGIG
    public static final String TYPE_BOAT_INRIGGER         = "INRIGGER";     // INRIGGER
    public static final String TYPE_BOAT_BARKE            = "BARKE";        // BARKE
    public static final String TYPE_BOAT_KIRCHBOOT        = "KIRCHBOOT";    // KIRCHBOOT
    public static final String TYPE_BOAT_MOTORBOAT        = "MOTORBOAT";    // MOTORBOOT
    public static final String TYPE_BOAT_ERG              = "ERG";          // ERGO
    public static final String TYPE_BOAT_OTHER            = "OTHER";        // other

    public static final String TYPE_NUMROWERS_1           = "1";            // 1
    public static final String TYPE_NUMROWERS_2           = "2";            // 2
    public static final String TYPE_NUMROWERS_3           = "3";            // 3
    public static final String TYPE_NUMROWERS_4           = "4";            // 4
    public static final String TYPE_NUMROWERS_5           = "5";            // 5
    public static final String TYPE_NUMROWERS_6           = "6";            // 6
    public static final String TYPE_NUMROWERS_8           = "8";            // 8
    public static final String TYPE_NUMROWERS_OTHER       = "OTHER";        // other

    public static final String TYPE_RIGGING_SCULL         = "SCULL";        // SKULL
    public static final String TYPE_RIGGING_SWEEP         = "SWEEP";        // RIEMEN
    public static final String TYPE_RIGGING_OTHER         = "OTHER";        // other

    public static final String TYPE_COXING_COXED          = "COXED";        // MIT
    public static final String TYPE_COXING_COXLESS        = "COXLESS";      // OHNE
    public static final String TYPE_COXING_OTHER          = "OTHER";        // other

    public static final String TYPE_TRIP_NORMAL           = "NORMAL";       // NORMAL
    public static final String TYPE_TRIP_TRAINING         = "TRAINING";     // TRAINING
    public static final String TYPE_TRIP_REGATTA          = "REGATTA";      // REGATTA
    public static final String TYPE_TRIP_JUMREGATTA       = "JUMREGATTA";   // JUMREGATTA
    public static final String TYPE_TRIP_TRAININGCAMP     = "TRAININGCAMP"; // TRAININGSLAGER
    public static final String TYPE_TRIP_INSTRUCTION      = "INSTRUCTION";  // AUSBILDUNG
    public static final String TYPE_TRIP_LATEENTRY        = "LATEENTRY";    // KILOMETERNACHTRAG @todo???
    public static final String TYPE_TRIP_MOTORBOAT        = "MOTORBOAT";    // MOTORBOOT
    public static final String TYPE_TRIP_ERG              = "ERG";          // ERGO
    public static final String TYPE_TRIP_MULTIDAY         = "MULTIDAY";     // MEHRTAGESFAHRT

    public static final String TYPE_STATUS_GUEST          = "GUEST";        // Gast
    public static final String TYPE_STATUS_OTHER          = "OTHER";        // andere


    public static final String KENNUNG190 = "##EFA.190.TYPES##";

    private Vector<String> categories;
    private Hashtable<String,Vector<EfaType>> values;

    // Construktor
    public EfaTypes(String pdat) {
        super(pdat,0,0,false);
        kennung = KENNUNG190;
        categories = new Vector<String>();
        categories.add(CATEGORY_GENDER);
        categories.add(CATEGORY_BOAT);
        categories.add(CATEGORY_NUMROWERS);
        categories.add(CATEGORY_RIGGING);
        categories.add(CATEGORY_COXING);
        categories.add(CATEGORY_TRIP);
        categories.add(CATEGORY_STATUS);
    }

    private void reset() {
        values = new Hashtable<String,Vector<EfaType>>();
    }

    public void setValue(String cat, String typ, String val) {
        if (cat == null || typ == null || val == null ||
                cat.length() == 0 || typ.length() == 0 || val.length() == 0 ||
                !categories.contains(cat)) {
            return;
        }

        EfaType type = new EfaType(cat,typ,val);
        Vector<EfaType> types = values.get(cat);
        if (types == null) {
            types = new Vector<EfaType>();
        }
        
        if (!isConfigured(cat, typ)) {
            types.add(type);
        } else {
            for (int i=0; i<types.size(); i++) {
                if (types.get(i).type.equals(typ)) {
                    types.get(i).value = val;
                }
            }
        }

        values.put(cat, types);
    }

    public boolean isConfigured(String cat, String typ) {
        if (cat == null || typ == null || cat.length() == 0 || typ.length() == 0) {
            return false;
        }
        Vector<EfaType> types = values.get(cat);
        if (types == null) {
            return false;
        }
        for (int i=0; i<types.size(); i++) {
            EfaType t = types.get(i);
            if (typ.equals(t.type)) {
                return true;
            }
        }
        return false;
    }

    public String getValue(String cat, String typ) {
        if (cat == null || typ == null || cat.length() == 0 || typ.length() == 0) {
            return International.getString("unbekannt");
        }
        Vector<EfaType> types = values.get(cat);
        if (types == null) {
            return International.getString("unbekannt");
        }
        for (int i=0; i<types.size(); i++) {
            EfaType t = types.get(i);
            if (typ.equals(t.type)) {
                return t.value;
            }
        }
        return International.getString("unbekannt");
    }

    public String getValue(String cat, int idx) {
        if (cat == null || cat.length() == 0 || idx < 0) {
            return International.getString("unbekannt");
        }
        Vector<EfaType> types = values.get(cat);
        if (types == null || idx >= types.size()) {
            return International.getString("unbekannt");
        }
        return types.get(idx).value;
    }

    public String getType(String cat, int idx) {
        if (cat == null || cat.length() == 0 || idx < 0) {
            return null;
        }
        Vector<EfaType> types = values.get(cat);
        if (types == null || idx >= types.size()) {
            return null;
        }
        return types.get(idx).type;
    }

    public String getTypeForValue(String cat, String val) {
        if (cat == null || cat.length() == 0 || val == null || val.length() == 0) {
            return null;
        }
        Vector<EfaType> types = values.get(cat);
        if (types == null) {
            return null;
        }
        for (int i=0; i<types.size(); i++) {
            if (val.equals(types.get(i).value)) {
                return types.get(i).type;
            }
        }
        return null;
    }

    private Vector<EfaType> getItems(String cat) {
        if (cat == null || cat.length() == 0) {
            return null;
        }
        Vector<EfaType> types = values.get(cat);
        if (types == null) {
            return new Vector<EfaType>();
        }
        return types;
    }

    public int size(String cat) {
        Vector<EfaType> types = getItems(cat);
        if (types == null) {
            return 0;
        }
        return types.size();
    }

    public String[] getValueArray(String cat) {
        Vector<EfaType> types = getItems(cat);
        if (types == null) {
            return new String[0];
        }
        String[] a = new String[types.size()];
        for (int i=0; i<types.size(); i++) {
            a[i] = types.get(i).value;
        }
        return a;
    }

    public static boolean isGigBoot(String key) {
        if (key == null || key.length()==0) {
            return false;
        }
        int sep = key.indexOf("_");
        if (sep<=0) {
            return false;
        }
        String type = key.substring(sep+1);
        if (type.length() == 0) {
            return false;
        }

        return (type.equals(TYPE_BOAT_AGIG) ||
                type.equals(TYPE_BOAT_BGIG) ||
                type.equals(TYPE_BOAT_CGIG) ||
                type.equals(TYPE_BOAT_DGIG) ||
                type.equals(TYPE_BOAT_EGIG) ||
                type.equals(TYPE_BOAT_INRIGGER) ||
                type.equals(TYPE_BOAT_BARKE) ||
                type.equals(TYPE_BOAT_KIRCHBOOT) ||
                type.equals(TYPE_BOAT_WHERRY) ||
                type.equals(TYPE_BOAT_TRIMMY)
                );
    }

    public static int getNumberOfRowers(String key) {
        if (key.equals(EfaTypes.TYPE_NUMROWERS_1)) {
            return 1;
        }
        if (key.equals(EfaTypes.TYPE_NUMROWERS_2)) {
            return 2;
        }
        if (key.equals(EfaTypes.TYPE_NUMROWERS_3)) {
            return 3;
        }
        if (key.equals(EfaTypes.TYPE_NUMROWERS_4)) {
            return 4;
        }
        if (key.equals(EfaTypes.TYPE_NUMROWERS_5)) {
            return 5;
        }
        if (key.equals(EfaTypes.TYPE_NUMROWERS_6)) {
            return 6;
        }
        if (key.equals(EfaTypes.TYPE_NUMROWERS_8)) {
            return 8;
        }
        return 0;
    }

    public synchronized boolean readEinstellungen() {
        reset();

        // Konfiguration lesen
        String s;
        try {
            while ((s = freadLine()) != null) {
                s = s.trim();
                if (s.length() == 0 || s.startsWith("#")) {
                    continue; // Kommentare ignorieren
                }
                int sepCatTyp = s.indexOf("_");
                int sepKeyVal = s.indexOf("=");
                if (sepCatTyp <= 0 || sepKeyVal <= 0) {
                    Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORINVALIDRECORD,
                            getFileName() + ": " + International.getString("Ungültiges Format für Bezeichnung") + ": " + s);
                    continue;
                }
                String cat = s.substring(0, sepCatTyp);
                String typ = s.substring(sepCatTyp + 1, sepKeyVal);
                String val = s.substring(sepKeyVal + 1);
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_TYPES, "cat="+cat+" typ="+typ+" val="+val);
                if (cat.length() == 0 || typ.length() == 0 || val.length() == 0 || !categories.contains(cat) ||
                    typ.indexOf('_')>=0 || typ.indexOf('=')>=0 || typ.indexOf(':')>=0) {
                    Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORINVALIDRECORD,
                            getFileName() + ": " + International.getString("Ungültiges Format für Bezeichnung") + ": " + s);
                    continue;
                }

                setValue(cat, typ, val);
            }

            // move TYPE_TRIP_MULTIDAY to the last position (if it exists)
            Vector<EfaType> v = getItems(CATEGORY_TRIP);
            EfaType mtour = null;
            for (int i=0; i<v.size(); i++) {
                if (v.get(i).type.equals(TYPE_TRIP_MULTIDAY)) {
                    mtour = v.get(i);
                    v.removeElementAt(i);
                    i--;
                }
            }
            if (mtour != null) {
                v.add(mtour);
            }

            // add types "OTHER"
            if (!isConfigured(CATEGORY_BOAT, TYPE_BOAT_OTHER)) {
                setValue(CATEGORY_BOAT, TYPE_BOAT_OTHER, International.getString("andere"));
            }
            if (!isConfigured(CATEGORY_NUMROWERS, TYPE_NUMROWERS_OTHER)) {
                setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_OTHER, International.getString("andere"));
            }
            if (!isConfigured(CATEGORY_RIGGING, TYPE_RIGGING_OTHER)) {
                setValue(CATEGORY_RIGGING, TYPE_RIGGING_OTHER, International.getString("andere"));
            }
            if (!isConfigured(CATEGORY_COXING, TYPE_COXING_OTHER)) {
                setValue(CATEGORY_COXING, TYPE_COXING_OTHER, International.getString("andere"));
            }

            // add types GUEST and OTHER
            if (!isConfigured(CATEGORY_STATUS, TYPE_STATUS_GUEST)) {
                setValue(CATEGORY_STATUS, TYPE_STATUS_GUEST, International.getString("Gast"));
            }
            if (!isConfigured(CATEGORY_STATUS, TYPE_STATUS_OTHER)) {
                setValue(CATEGORY_STATUS, TYPE_STATUS_OTHER, International.getString("andere"));
            }

        } catch (IOException e) {
            try {
                fclose(false);
            } catch (Exception ee) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean writeEinstellungen() {
        try {
            for (int i = 0; i < categories.size(); i++) {
                Vector<EfaType> items = values.get(categories.get(i));
                if (items == null) {
                    continue;
                }
                for (int j = 0; j < items.size(); j++) {
                    EfaType item = items.get(j);
                    fwrite(item.category + "_" + item.type + "=" + item.value + "\n");
                }
            }
        } catch (IOException e) {
            LogString.logError_fileWritingFailed(dat, International.getString("Datei"));
            Dialog.error(LogString.logstring_fileWritingFailed(dat, International.getString("Datei")));
            return false;
        }
        return true;
    }

    public boolean createNewIfDoesntExist() {
        if ((new File(dat)).exists()) {
            return true;
        }

        // Datei existiert noch nicht: Neu erstellen mit Default-Werten
        reset();

        setValue(CATEGORY_GENDER, TYPE_GENDER_MALE, International.getString("männlich"));
        setValue(CATEGORY_GENDER, TYPE_GENDER_FEMALE, International.getString("weiblich"));

        setValue(CATEGORY_BOAT, TYPE_BOAT_RACING, International.getString("Rennboot"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_SINGLERACING, International.getString("Skiff"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_WHERRY, International.getString("Wherry"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_TRIMMY, International.getString("Trimmy"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_AGIG, International.getString("A-Gig"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_BGIG, International.getString("B-Gig"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_CGIG, International.getString("C-Gig"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_DGIG, International.getString("D-Gig"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_EGIG, International.getString("E-Gig"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_INRIGGER, International.getString("Inrigger"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_BARKE, International.getString("Barke"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_KIRCHBOOT, International.getString("Kirchboot"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_MOTORBOAT, International.getString("Motorboot"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_ERG, International.getString("Ergo"));
        setValue(CATEGORY_BOAT, TYPE_BOAT_OTHER, International.getString("andere"));

        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_1, International.getString("1er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_2, International.getString("2er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_3, International.getString("3er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_4, International.getString("4er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_5, International.getString("5er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_6, International.getString("6er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_8, International.getString("8er"));
        setValue(CATEGORY_NUMROWERS, TYPE_NUMROWERS_OTHER, International.getString("andere"));

        setValue(CATEGORY_RIGGING, TYPE_RIGGING_SCULL, International.getString("Skull"));
        setValue(CATEGORY_RIGGING, TYPE_RIGGING_SWEEP, International.getString("Riemen"));
        setValue(CATEGORY_RIGGING, TYPE_RIGGING_OTHER, International.getString("andere"));

        setValue(CATEGORY_COXING, TYPE_COXING_COXED, International.getString("mit Stm."));
        setValue(CATEGORY_COXING, TYPE_COXING_COXLESS, International.getString("ohne Stm."));
        setValue(CATEGORY_COXING, TYPE_COXING_OTHER, International.getString("andere"));

        setValue(CATEGORY_TRIP, TYPE_TRIP_NORMAL, International.getString("normale Fahrt"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_TRAINING, International.getString("Training"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_REGATTA, International.getString("Regatta"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_JUMREGATTA, International.getString("JuM-Regatta"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_TRAININGCAMP, International.getString("Trainingslager"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_INSTRUCTION, International.getString("Ausbildung"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_LATEENTRY, International.getString("Kilometernachtrag"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_MOTORBOAT, International.getString("Motorboot"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_ERG, International.getString("Ergo"));
        setValue(CATEGORY_TRIP, TYPE_TRIP_MULTIDAY, International.getString("Mehrtagesfahrt"));

        setValue(CATEGORY_STATUS, TYPE_STATUS_GUEST, International.getString("Gast"));
        setValue(CATEGORY_STATUS, TYPE_STATUS_OTHER, International.getString("andere"));

        return writeFile(false);
    }
}
