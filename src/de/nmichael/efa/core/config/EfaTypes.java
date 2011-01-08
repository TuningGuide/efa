/* Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import de.nmichael.efa.efa1.Fahrtenbuch;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.*;
import java.io.*;
import java.util.*;
import java.text.*;

// @i18n complete

public class EfaTypes extends DatenListe {

    public static final String CATEGORY_GENDER            = "GENDER";       // GESCHLECHT
    public static final String CATEGORY_BOAT              = "BOAT";         // BART
    public static final String CATEGORY_NUMSEATS          = "NUMSEATS";     // BANZAHL
    public static final String CATEGORY_RIGGING           = "RIGGING";      // BRIGGER
    public static final String CATEGORY_COXING            = "COXING";       // BSTM
    public static final String CATEGORY_SESSION           = "SESSION";      // FAHRT
    public static final String CATEGORY_STATUS            = "STATUS";       // n/a

    public static final String TYPE_GENDER_MALE           = "MALE";         // MAENNLICH
    public static final String TYPE_GENDER_FEMALE         = "FEMALE";       // WEIBLICH

    public static final String TYPE_BOAT_RACING           = "RACING";       // RENNBOOT
    public static final String TYPE_BOAT_WHERRY           = "WHERRY";       // WHERRY
    public static final String TYPE_BOAT_TRIMMY           = "TRIMMY";       // TRIMMY
    public static final String TYPE_BOAT_AGIG             = "AGIG";         // AGIG
    public static final String TYPE_BOAT_BGIG             = "BGIG";         // BGIG
    public static final String TYPE_BOAT_CGIG             = "CGIG";         // CGIG
    public static final String TYPE_BOAT_DGIG             = "DGIG";         // DGIG
    public static final String TYPE_BOAT_EGIG             = "EGIG";         // EGIG
    public static final String TYPE_BOAT_INRIGGER         = "INRIGGER";     // INRIGGER
    public static final String TYPE_BOAT_BARQUE           = "BARQUE";       // BARKE
    public static final String TYPE_BOAT_CHURCHBOAT       = "CHURCHBOAT";   // KIRCHBOOT
    public static final String TYPE_BOAT_MOTORBOAT        = "MOTORBOAT";    // MOTORBOOT
    public static final String TYPE_BOAT_ERG              = "ERG";          // ERGO
    public static final String TYPE_BOAT_SEAKAYAK         = "SEAKAYAK";     // neu für Kanuten: Seekajak
    public static final String TYPE_BOAT_RACINGKAYAK      = "RACINGKAYAK";  // neu für Kanuten: Rennkajak
    public static final String TYPE_BOAT_WHITEWATERKAYAK  = "WHITEWATERKAYAK"; // neu für Kanuten: Wildwasserkajak
    public static final String TYPE_BOAT_CANADIANTOURINGCANOE = "CANADIANTOURINGCANOE"; // neu für Kanuten: Tourenkanadier
    public static final String TYPE_BOAT_POLOBOAT         = "POLOBOAT"; // neu für Kanuten: Tourenkanadier
    public static final String TYPE_BOAT_FOLDINGCANOE     = "FOLDINGCANOE"; // neu für Kanuten: Tourenkanadier
    public static final String TYPE_BOAT_CANADIANTEAMCANOE= "CANADIANTEAMCANOE"; // neu für Kanuten: Mannschaftskanadier
    public static final String TYPE_BOAT_DRAGONBOAT       = "DRAGONBOAT";   // neu für Kanuten: Drachenboot
    public static final String TYPE_BOAT_OTHER            = "OTHER";        // other

    public static final String TYPE_NUMSEATS_1            = "1";            // 1
    public static final String TYPE_NUMSEATS_2            = "2";            // 2
    public static final String TYPE_NUMSEATS_2X           = "2X";           // 2
    public static final String TYPE_NUMSEATS_3            = "3";            // 3
    public static final String TYPE_NUMSEATS_4            = "4";            // 4
    public static final String TYPE_NUMSEATS_4X           = "4X";           // 4
    public static final String TYPE_NUMSEATS_5            = "5";            // 5
    public static final String TYPE_NUMSEATS_6            = "6";            // 6
    public static final String TYPE_NUMSEATS_6X           = "6X";           // 6
    public static final String TYPE_NUMSEATS_8            = "8";            // 8
    public static final String TYPE_NUMSEATS_8X           = "8X";           // 8
    public static final String TYPE_NUMSEATS_OTHER        = "OTHER";        // other

    public static final String TYPE_RIGGING_SCULL         = "SCULL";        // SKULL
    public static final String TYPE_RIGGING_SWEEP         = "SWEEP";        // RIEMEN
    public static final String TYPE_RIGGING_PADDLE        = "PADDLE";       // neu für Kanuten: Paddel
    public static final String TYPE_RIGGING_OTHER         = "OTHER";        // other

    public static final String TYPE_COXING_COXED          = "COXED";        // MIT
    public static final String TYPE_COXING_COXLESS        = "COXLESS";      // OHNE
    public static final String TYPE_COXING_OTHER          = "OTHER";        // other

    public static final String TYPE_SESSION_NORMAL        = "NORMAL";       // NORMAL
    public static final String TYPE_SESSION_TRAINING      = "TRAINING";     // TRAINING
    public static final String TYPE_SESSION_REGATTA       = "REGATTA";      // REGATTA
    public static final String TYPE_SESSION_JUMREGATTA    = "JUMREGATTA";   // JUMREGATTA
    public static final String TYPE_SESSION_TRAININGCAMP  = "TRAININGCAMP"; // TRAININGSLAGER
    public static final String TYPE_SESSION_INSTRUCTION   = "INSTRUCTION";  // AUSBILDUNG
    public static final String TYPE_SESSION_LATEENTRY     = "LATEENTRY";    // KILOMETERNACHTRAG
    public static final String TYPE_SESSION_MOTORBOAT     = "MOTORBOAT";    // MOTORBOOT
    public static final String TYPE_SESSION_ERG           = "ERG";          // ERGO
    public static final String TYPE_SESSION_MULTIDAY      = "MULTIDAY";     // MEHRTAGESFAHRT

    public static final String TYPE_STATUS_GUEST          = "GUEST";        // Gast
    public static final String TYPE_STATUS_OTHER          = "OTHER";        // andere

    public static final int SELECTION_ROWING = 1;
    public static final int SELECTION_CANOEING = 2;


    public static final String KENNUNG190 = "##EFA.190.TYPES##";

    private Vector<String> categories;
    private Hashtable<String,Vector<EfaType>> values;
    private CustSettings custSettings = null;

    // Default Construktor
    public EfaTypes(String pdat) {
        super(pdat,0,0,false);
        kennung = KENNUNG190;
        iniCategories();
        reset();
    }

    // Copy Constructor
    public EfaTypes(EfaTypes efaTypes) {
        super(efaTypes.getFileName(),0,0,false);
        kennung = efaTypes.kennung;
        iniCategories();
        reset();
        for (int c=0; c<categories.size(); c++) {
            Vector<EfaType> types = efaTypes.getItems(categories.get(c));
            for (int i=0; i<types.size(); i++) {
                EfaType type = types.get(i);
                setValue(type.category, type.type, type.value);
            }
        }
    }

    private void iniCategories() {
        categories = new Vector<String>();
        categories.add(CATEGORY_GENDER);
        categories.add(CATEGORY_BOAT);
        categories.add(CATEGORY_NUMSEATS);
        categories.add(CATEGORY_RIGGING);
        categories.add(CATEGORY_COXING);
        categories.add(CATEGORY_SESSION);
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

    public void removeValue(String cat, String typ) {
        if (!isConfigured(cat, typ)) {
            return;
        }
        Vector<EfaType> types = values.get(cat);
        for (int i=0; i<types.size(); i++) {
            EfaType t = types.get(i);
            if (typ.equals(t.type)) {
                types.remove(t);
                return;
            }
        }
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

    public String[] getTypesArray(String cat) {
        Vector<EfaType> types = getItems(cat);
        if (types == null) {
            return new String[0];
        }
        String[] a = new String[types.size()];
        for (int i=0; i<types.size(); i++) {
            a[i] = types.get(i).type;
        }
        return a;
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
                type.equals(TYPE_BOAT_BARQUE) ||
                type.equals(TYPE_BOAT_CHURCHBOAT) ||
                type.equals(TYPE_BOAT_WHERRY) ||
                type.equals(TYPE_BOAT_TRIMMY)
                );
    }

    public static int getNumberOfRowers(String key) {
        if (key.equals(EfaTypes.TYPE_NUMSEATS_1)) {
            return 1;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_2) ||
            key.equals(EfaTypes.TYPE_NUMSEATS_2X)) {
            return 2;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_3)) {
            return 3;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_4) ||
            key.equals(EfaTypes.TYPE_NUMSEATS_4X)) {
            return 4;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_5)) {
            return 5;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_6) ||
            key.equals(EfaTypes.TYPE_NUMSEATS_6X)) {
            return 6;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_8) ||
            key.equals(EfaTypes.TYPE_NUMSEATS_8X)) {
            return 8;
        }
        if (key.equals(EfaTypes.TYPE_NUMSEATS_OTHER)) {
            return Fahrtenbuch.ANZ_MANNSCH;
        }

        // ok, no key found. Now try to extract some numbers from the key itself (as in "6X")
        int num = EfaUtil.stringFindInt(key, 0);
        if (num > 0 && num <= Fahrtenbuch.ANZ_MANNSCH) {
            return num;
        }

        return Fahrtenbuch.ANZ_MANNSCH;
    }

    public static String getStringUnknown() {
        return International.getString("unbekannt");
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
                if (Logger.isTraceOn(Logger.TT_EFATYPES)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_TYPES, "cat="+cat+" typ="+typ+" val="+val);
                }
                if (cat.length() == 0 || typ.length() == 0 || val.length() == 0 || !categories.contains(cat) ||
                    typ.indexOf('_')>=0 || typ.indexOf('=')>=0 || typ.indexOf(':')>=0) {
                    Logger.log(Logger.ERROR, Logger.MSG_CSVFILE_ERRORINVALIDRECORD,
                            getFileName() + ": " + International.getString("Ungültiges Format für Bezeichnung") + ": " + s);
                    continue;
                }

                setValue(cat, typ, val);
            }

            // move TYPE_TRIP_MULTIDAY to the last position (if it exists)
            Vector<EfaType> v = getItems(CATEGORY_SESSION);
            EfaType mtour = null;
            for (int i=0; i<v.size(); i++) {
                if (v.get(i).type.equals(TYPE_SESSION_MULTIDAY)) {
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
            if (!isConfigured(CATEGORY_NUMSEATS, TYPE_NUMSEATS_OTHER)) {
                setValue(CATEGORY_NUMSEATS, TYPE_NUMSEATS_OTHER, International.getString("andere"));
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

    public void setCustSettings(CustSettings custSettings) {
        if (custSettings != null) {
            this.custSettings = custSettings;
        } else {
            this.custSettings = new CustSettings();
        }
    }

    public boolean createNewIfDoesntExist(CustSettings custSettings) {
        if ((new File(dat)).exists()) {
            return true;
        }

        // make sure that this.custSettings != null when creating from scratch!
        setCustSettings(custSettings);

        // Datei existiert noch nicht: Neu erstellen mit Default-Werten
        reset();
        if (!setToLanguage(null)) {
            return false;
        }

        return writeFile(false);
    }

    private int setToLanguage(String cat, String typ, String itxt, String otxt, ResourceBundle bundle, boolean createNewIfNotExists) {
        if ((!isConfigured(cat, typ) && createNewIfNotExists) ||
            (isConfigured(cat, typ) && getValue(cat, typ).equals(itxt))) {
            // value not yet configured or unchanged (has default value for current language)
            String key = International.makeKey(otxt);
            try {
                String val = bundle.getString(key);
                setValue(cat, typ, val);
            } catch(Exception e) {
                setValue(cat, typ, itxt); // use itxt as value if target language bundle does not contain translation
            }
            return 1;
        }
        return 0;
    }

    public int setToLanguage_Boats(ResourceBundle bundle, int typeSelection, boolean createNew) {
        int count = 0;
        switch(typeSelection) {
            case SELECTION_ROWING:
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_RACING, International.getString("Rennboot"),"Rennboot",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_WHERRY, International.getString("Wherry"),"Wherry",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_TRIMMY, International.getString("Trimmy"),"Trimmy",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_AGIG, International.getString("A-Gig"),"A-Gig",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_BGIG, International.getString("B-Gig"),"B-Gig",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_CGIG, International.getString("C-Gig"),"C-Gig",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_DGIG, International.getString("D-Gig"),"D-Gig",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_EGIG, International.getString("E-Gig"),"E-Gig",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_INRIGGER, International.getString("Inrigger"),"Inrigger",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_BARQUE, International.getString("Barke"),"Barke",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_CHURCHBOAT, International.getString("Kirchboot"),"Kirchboot",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_ERG, International.getString("Ergo"),"Ergo",bundle,createNew);

                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_1, International.getString("Einer"),"Einer",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_2, International.getString("Zweier"),"Zweier",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_2X, International.getString("Doppelzweier"),"Doppelzweier",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_3, International.getString("Dreier"),"Dreier",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_4, International.getString("Vierer"),"Vierer",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_4X, International.getString("Doppelvierer"),"Doppelvierer",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_5, International.getString("Fünfer"),"Fünfer",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_6, International.getString("Sechser"),"Sechser",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_6X, International.getString("Doppelsechser"),"Doppelsechser",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_8, International.getString("Achter"),"Achter",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_8X, International.getString("Doppelachter"),"Doppelachter",bundle,createNew);

                count += setToLanguage(CATEGORY_RIGGING, TYPE_RIGGING_SCULL, International.getString("Skull"),"Skull",bundle,createNew);
                count += setToLanguage(CATEGORY_RIGGING, TYPE_RIGGING_SWEEP, International.getString("Riemen"),"Riemen",bundle,createNew);
                break;
            case SELECTION_CANOEING:
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_SEAKAYAK, International.getString("Seekajak"),"Seekajak",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_RACINGKAYAK, International.getString("Rennkajak"),"Rennkajak",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_WHITEWATERKAYAK, International.getString("Wildwasserkajak"),"Wildwasserkajak",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_CANADIANTOURINGCANOE, International.getString("Tourenkanadier"),"Tourenkanadier",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_POLOBOAT, International.getString("Poloboot"),"Poloboot",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_FOLDINGCANOE, International.getString("Faltboot"),"Faltboot",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_CANADIANTEAMCANOE, International.getString("Mannschaftskanadier"),"Mannschaftskanadier",bundle,createNew);
                count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_DRAGONBOAT, International.getString("Drachenboot"),"Drachenboot",bundle,createNew);

                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_1, International.getString("Einer"),"Einer",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_2, International.getString("Zweier"),"Zweier",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_3, International.getString("Dreier"),"Dreier",bundle,createNew);
                count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_4, International.getString("Vierer"),"Vierer",bundle,createNew);

                count += setToLanguage(CATEGORY_RIGGING, TYPE_RIGGING_PADDLE, International.getString("Paddel"),"Paddel",bundle,createNew);
                break;
        }

        count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_MOTORBOAT, International.getString("Motorboot"),"Motorboot",bundle,createNew);
        count += setToLanguage(CATEGORY_BOAT, TYPE_BOAT_OTHER, International.getString("andere"),"andere",bundle,createNew);
        count += setToLanguage(CATEGORY_NUMSEATS, TYPE_NUMSEATS_OTHER, International.getString("andere"),"andere",bundle,createNew);
        count += setToLanguage(CATEGORY_RIGGING, TYPE_RIGGING_OTHER, International.getString("andere"),"andere",bundle,createNew);
        count += setToLanguage(CATEGORY_COXING, TYPE_COXING_COXED, International.getString("mit Stm."),"mit Stm.",bundle,createNew);
        count += setToLanguage(CATEGORY_COXING, TYPE_COXING_COXLESS, International.getString("ohne Stm."),"ohne Stm.",bundle,createNew);
        count += setToLanguage(CATEGORY_COXING, TYPE_COXING_OTHER, International.getString("andere"),"andere",bundle,createNew);

        return count;
    }

    public boolean setToLanguage(String lang) {
        ResourceBundle bundle = null;
        if (lang != null) {
            try {
                bundle = ResourceBundle.getBundle(International.BUNDLE_NAME, new Locale(lang));
            } catch (Exception e) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFATYPESFAILEDSETVALUES,
                        "Failed to set EfaTypes values for language " + lang + ".");
                return false;
            }
        } else {
            bundle = International.getResourceBundle();
        }

        boolean createNew = (custSettings != null ? true : false);

        setToLanguage(CATEGORY_GENDER, TYPE_GENDER_MALE, International.getString("männlich"),"männlich",bundle,createNew);
        setToLanguage(CATEGORY_GENDER, TYPE_GENDER_FEMALE, International.getString("weiblich"),"weiblich",bundle,createNew);

        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_NORMAL, International.getString("normale Fahrt"),"normale Fahrt",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_TRAINING, International.getString("Training"),"Training",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_REGATTA, International.getString("Regatta"),"Regatta",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_JUMREGATTA, International.getString("JuM-Regatta"),"JuM-Regatta",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_TRAININGCAMP, International.getString("Trainingslager"),"Trainingslager",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_INSTRUCTION, International.getString("Ausbildung"),"Ausbildung",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_LATEENTRY, International.getString("Kilometernachtrag"),"Kilometernachtrag",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_MOTORBOAT, International.getString("Motorboot"),"Motorboot",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_ERG, International.getString("Ergo"),"Ergo",bundle,createNew);
        setToLanguage(CATEGORY_SESSION, TYPE_SESSION_MULTIDAY, International.getString("Mehrtagesfahrt"),"Mehrtagesfahrt",bundle,createNew);

        setToLanguage(CATEGORY_STATUS, TYPE_STATUS_GUEST, International.getString("Gast"),"Gast",bundle,createNew);
        setToLanguage(CATEGORY_STATUS, TYPE_STATUS_OTHER, International.getString("andere"),"andere",bundle,createNew);

        setToLanguage_Boats(bundle, SELECTION_ROWING, (custSettings != null ? custSettings.activateRowingOptions : false));
        setToLanguage_Boats(bundle, SELECTION_CANOEING, (custSettings != null ? custSettings.activateCanoeingOptions : false));

        return true;
    }

}
