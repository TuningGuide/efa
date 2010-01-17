/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.util.EfaErrorPrintStream;
import de.nmichael.efa.core.config.EfaBaseConfig;
import de.nmichael.efa.util.SimpleSelectFrame;
import java.util.*;
import java.io.*;
import java.text.*;

// @i18n complete

public class International {

    private static boolean MARK_MISSING_KEYS = false; // default for production: false
    private static boolean LOG_MISSING_KEYS = false; // default for production: false (requires "-debug" flag for debug logging)
    private static boolean STACKTRACE_MISSING_KEYS = false; // default for production: false
    private static boolean SHOW_KEY_INSTEAD_OF_TRANSLATION = false;  // default for production: false

    public static final String BUNDLE_NAME = "efa";
    private static Locale locale = null;
    private static ResourceBundle bundle = null;
    private static MessageFormat msgFormat = null;
    private static NumberFormat numberFormat = null;
    private static char decimalSeparator = '.';
    private static boolean initializationFailed = false;

    private static void initializeData() {
        try {
            if (Daten.efaBaseConfig != null && Daten.efaBaseConfig.language != null &&
                Daten.efaBaseConfig.language.length() > 0) {
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Using configured language setting: "+Daten.efaBaseConfig.language);
                locale = new Locale(Daten.efaBaseConfig.language);
            } else {
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Using default language.");
                locale = Locale.getDefault();
            }
            Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Language is now: "+locale.getDisplayName());

            bundle = ResourceBundle.getBundle(BUNDLE_NAME,locale);
            msgFormat = new MessageFormat("",locale);
            numberFormat = NumberFormat.getNumberInstance(locale);
            decimalSeparator = ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
            Daten.EFA_SHORTNAME = International.getString("efa");
            Daten.EFA_LONGNAME = International.getString("efa - elektronisches Fahrtenbuch");
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_INTERNATIONAL_FAILEDSETUP, "Failed to set up internationalization: "+e.toString()); // no need for translation
            initializationFailed = true;
        }
    }

    public static void initialize() {
        if (initializationFailed) {
            return;
        }
        
        Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Initializing Language Support ...");
        try {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String trace = "";
            for (int i=stack.length-1; i>=0; i--) {
                trace = trace + " -> " + stack[i].toString();
                if (stack[i].toString().startsWith(International.class.getCanonicalName())) {
                    break;
                }
            }
            Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, trace);
        } catch(Exception e) {
            // nothing to do
        }
        initializeData();

        try {
            if (Daten.efaBaseConfig != null && Daten.efaBaseConfig.language == null) {
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "No preferred Language configured!");
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Available Languages:");
                Vector<String> bundles = getLanguageBundles();
                if (Daten.isGuiAppl() && bundles.size() > 0) {
                    String[] items = new String[bundles.size() + 1];
                    items[0] = International.getString("Default"); // must be in English (in case user's language is not supported)
                    int preselect = 0;
                    for (int i=0; i<bundles.size(); i++) {
                        Locale loc = new Locale(bundles.get(i));
                        items[i+1] = loc.getDisplayName();
                        if (Locale.getDefault().getLanguage().equals(loc.getLanguage())) {
                            preselect = i+1;
                        }
                    }
                    int selected = SimpleSelectFrame.showInputDialog(
                            International.getString("Select Language"),                 // must be in English (in case user's language is not supported)
                            International.getString("Please select your language")+":", // must be in English (in case user's language is not supported)
                            items,
                            preselect);
                    String lang = null;
                    if (selected >= 0) {
                        if (selected == 0) {
                            lang = ""; // auto default language
                            Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Selected Language: <default>");
                        } else {
                            lang = bundles.get(selected - 1);
                            Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Selected Language: " + lang);
                        }
                    } else {
                        Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Selected Language: <none>");
                    }
                    
                    if (lang != null) {
                        Daten.efaBaseConfig.language = lang;
                        Daten.efaBaseConfig.writeFile();
                        initializeData();
                    }

                } else {
                    Logger.log(Logger.WARNING, Logger.MSG_CORE_LANGUAGESUPPORT,
                    "No Language Bundles found in "+Daten.efaProgramDirectory+"!");
                }
            }

        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_CORE_LANGUAGESUPPORT,
                    "Failed to determine available languages: "+e.toString());
        }
    }

    public static Vector<String> getLanguageBundles() {
        File dir = new File(Daten.efaProgramDirectory);
        File[] files = dir.listFiles();
        Vector<String> bundles = new Vector<String>();
        for (File f : files) {
            String name = f.getName();
            if (name.startsWith(BUNDLE_NAME + "_") && name.endsWith(".properties")) {
                int pos = name.indexOf(".properties");
                String lang = name.substring(BUNDLE_NAME.length() + 1, pos);
                if (lang.length() > 0) {
                    Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "    " + lang);
                    bundles.add(lang);
                }
            }
        }
        return bundles;
    }

    private static String getArrayStrings(Object[] a) {
        String s = "";
        for (int i=1; a != null && i<a.length; i++) {
            if (a[i] != null) {
                s += (s.length() > 0 ? ", " : "") + a[i].toString();
            }
        }
        return s;
    }

    public static String makeKey(String s) {
        if (s == null) return "null";
        
        // @todo: To be extended by further characters? And maybe some performance optimization needed.
        char[] key = s.toCharArray();
        for (int i=0; i<key.length; i++) {
            switch(key[i]) {
                case ' ':
                case '=':
                case ':':
                case '#':
                case '\'':
                case '\\':
                case '\n':
                    key[i] = '_';
                    break;
            }
        }
        return new String(key);
    }

    private static String getString(String s, boolean defaultIfNotFound, boolean includingMnemonics) {
        if (bundle == null) {
            initialize();
        }
        try {
            String key = makeKey(s);
            if (SHOW_KEY_INSTEAD_OF_TRANSLATION) {
                return (MARK_MISSING_KEYS ? "#" : "") + key + (MARK_MISSING_KEYS ? "#" : "");
            } else {
                String t = bundle.getString(key);
                if (!includingMnemonics) {
                    if (Mnemonics.containsMnemonics(t)) {
                        t = Mnemonics.stripMnemonics(t);
                    }
                }
                return t;
            }
        } catch(Exception e) {
            if (defaultIfNotFound) {
                if (LOG_MISSING_KEYS) {
                    Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_MISSINGKEY, "Missing Key: "+makeKey(s)); // no need for translation!
                }
                if (STACKTRACE_MISSING_KEYS) {
                    EfaErrorPrintStream.ignoreExceptions = true;
                    e.printStackTrace();
                    EfaErrorPrintStream.ignoreExceptions = false;
                }
                return (MARK_MISSING_KEYS ? "#" : "") + s + (MARK_MISSING_KEYS ? "#" : "");
            } else {
                return null;
            }
        }
    }

    /**
     * Retrieves an internationalized string.
     * This method strips any mnemonics from the translated string before returning it.
     * @param s key to be retrieved
     * @return translated string
     */
    public static String getString(String s) {
        return getString(s, true, false);
    }

    public static String onlyFor(String s, String lang) {
        if (getLanguageID().startsWith(lang)) {
            return s;
        }
//        return "<" + getMessage("only [{language}]",lang) + ">";
        return International.getMessage("nur für [{language}]",lang);
    }

    /**
     * Retrieves an internationalized variant of a string.
     * This method is intended for situations where the same key may be translated in different ways
     * depending on the context it is being used in. For such situations, an additional variant discriminator
     * may be specified to distinguish (otheriwse same) keys in different contexts. If no translation for
     * this key including its variant discriminator is found, this method will attempt to return a translation for
     * the key without the variant discriminator.
     * This method strips any mnemonics from the translated string before returning it.
     *
     * @param s key to be retrieved
     * @param variant discriminator to specify the context of the key
     * @return the translation for "s___variant", if existing; translation for "s" otherwise.
     */
    public static String getString(String s, String variant) {
        String t = getString(s + "___" + variant, false, false);
        if (t != null) {
            return t;
        }
        return getString(s, true, false);
    }

    /**
     * Retrieves an internationalized string.
     * This method keeps all mnemonics in the translated string that is returned.
     * @param s key to be retrieved
     * @return translated string including mnemonics marked with "&"
     */
    public static String getStringWithMnemonic(String s) {
        return getString(s, true, true);
    }

    /**
     * Retrieves an internationalized variant of a string (see getString(String, String).
     * This method keeps all mnemonics in the translated string that is returned.
     *
     * @param s key to be retrieved
     * @param variant discriminator to specify the context of the key
     * @return the translation for "s___variant", if existing; translation for "s" otherwise -- including mnemonics marked with "&".
     */
    public static String getStringWithMnemonic(String s, String variant) {
        String t = getString(s + "___" + variant, false, true);
        if (t != null) {
            return t;
        }
        return getString(s, true, true);
    }

    private static String getMessage(String s, Object[] args) {
        if (msgFormat == null) {
            initialize();
        }
        try {
            msgFormat.applyPattern(getString(s));
            return msgFormat.format(args);
        } catch(Exception e) {
            if (LOG_MISSING_KEYS) {
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_INCORRECTKEY,"Incorrect Compound Key: "+s); // no need for translation!
            }
            if (STACKTRACE_MISSING_KEYS) {
                EfaErrorPrintStream.ignoreExceptions = true;
                e.printStackTrace();
                EfaErrorPrintStream.ignoreExceptions = false;
            }
            return (MARK_MISSING_KEYS ? "#" : "") + s + ": " + getArrayStrings(args) + (MARK_MISSING_KEYS ? "#" : "");
        }
    }

    /**
     * Retrieves an internationalized compound message.
     * All arguments inside this message have to be masked by "{arg}", where "arg" may be any text
     * that is not further interpreted or used (other than being part of the key). For each of the
     * arguments masked by "{arg}", and argument arg1, arg2, ...., argn has to be supplied.
     * @param s key to be retrieved
     * @param arg1 argument to be placed into compound message
     * @return translated message string
     */
    // ========================= String only arguments =========================
    public static String getMessage(String s, String arg1) {
        Object[] args = { "dummy", arg1 };
        return getMessage(s,args);
    }

    public static String getMessage(String s, String arg1, String arg2) {
        Object[] args = { "dummy", arg1, arg2 };
        return getMessage(s,args);
    }

    public static String getMessage(String s, String arg1, String arg2, String arg3) {
        Object[] args = {"dummy", arg1, arg2, arg3};
        return getMessage(s, args);
    }

    public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4) {
        Object[] args = {"dummy", arg1, arg2, arg3, arg4};
        return getMessage(s, args);
    }

    public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4, String arg5) {
        Object[] args = {"dummy", arg1, arg2, arg3, arg4, arg5};
        return getMessage(s, args);
    }

    public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) {
        Object[] args = {"dummy", arg1, arg2, arg3, arg4, arg5, arg6, arg7};
        return getMessage(s, args);
    }

    public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8) {
        Object[] args = {"dummy", arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8};
        return getMessage(s, args);
    }

    // ========================= Integer only arguments =========================
    public static String getMessage(String s, int arg1) {
        Object[] args = {"dummy", new Integer(arg1)};
        return getMessage(s, args);
    }

    public static String getMessage(String s, int arg1, int arg2) {
        Object[] args = { "dummy", new Integer(arg1), new Integer(arg2) };
        return getMessage(s,args);
    }

    public static String getMessage(String s, int arg1, int arg2, int arg3) {
        Object[] args = { "dummy", new Integer(arg1), new Integer(arg2), new Integer(arg3) };
        return getMessage(s,args);
    }

    public static String getMessage(String s, int arg1, int arg2, int arg3, int arg4) {
        Object[] args = { "dummy", new Integer(arg1), new Integer(arg2), new Integer(arg3), new Integer(arg4) };
        return getMessage(s,args);
    }

    public static String getMessage(String s, int arg1, int arg2, int arg3, int arg4, int arg5) {
        Object[] args = { "dummy", new Integer(arg1), new Integer(arg2), new Integer(arg3), new Integer(arg4), new Integer(arg5) };
        return getMessage(s,args);
    }
    public static String getMessage(String s, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
        Object[] args = { "dummy", new Integer(arg1), new Integer(arg2), new Integer(arg3), new Integer(arg4), new Integer(arg5), new Integer(arg6) };
        return getMessage(s,args);
    }

    // ========================= mixed Type arguments =========================
    public static String getMessage(String s, int arg1, String arg2) {
        Object[] args = {"dummy", new Integer(arg1), arg2};
        return getMessage(s, args);
    }

    public static String getMessage(String s, String arg1, int arg2) {
        Object[] args = { "dummy", arg1, new Integer(arg2) };
        return getMessage(s,args);
    }

    public static String getMessage(String s, String arg1, int arg2, String arg3) {
        Object[] args = {"dummy", arg1, new Integer(arg2), arg3};
        return getMessage(s, args);
    }

    public static char getDecimalSeparator() {
        if (numberFormat == null) {
            initialize();
        }
        return decimalSeparator;
    }

    public static String getLanguageDescription() {
        if (locale == null) {
            initialize();
        }
        try {
            return locale.getDisplayName();
        } catch(Exception e) {
            return "unknown"; // do not internationalize
        }
    }

    public static String getLanguageID() {
        if (locale == null) {
            initialize();
        }
        try {
            return locale.toString();
        } catch(Exception e) {
            return "";
        }
    }

    public static ResourceBundle getResourceBundle() {
        return bundle;
    }

    public static void setMarkMissingKeys(boolean enabled) {
        MARK_MISSING_KEYS = enabled;
    }
    public static void setLogMissingKeys(boolean enabled) {
        LOG_MISSING_KEYS = enabled;
    }
    public static void setTraceMissingKeys(boolean enabled) {
        STACKTRACE_MISSING_KEYS = enabled;
    }
    public static void setShowKeys(boolean enabled) {
        SHOW_KEY_INSTEAD_OF_TRANSLATION = enabled;
    }

}
