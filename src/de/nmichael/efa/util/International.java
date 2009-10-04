/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.util.EfaErrorPrintStream;
import java.util.*;
import java.text.*;

/**
 *
 * @author nick
 */

// @i18n complete

public class International {

    private static final boolean MARK_MISSING_KEYS = true; // default for production: false
    private static final boolean LOG_MISSING_KEYS = true; // default for production: false (requires "-debug" flag for debug logging)
    private static final boolean STACKTRACE_MISSING_KEYS = false; // default for production: false
    private static final boolean SHOW_KEY_INSTEAD_OF_TRANSLATION = false;  // default for production: false

    private static Locale locale = null;
    private static ResourceBundle bundle = null;
    private static MessageFormat msgFormat = null;
    private static NumberFormat numberFormat = null;
    private static char decimalSeparator = '.';

    public static void initialize() {
        Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Initializing Language Support ...");
        try {
            if (Daten.efaConfig != null && Daten.efaConfig.language != null) {
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Using configured language setting: "+Daten.efaConfig.language);
                locale = new Locale(Daten.efaConfig.language);
            } else {
                Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Using default language.");
                locale = Locale.getDefault();
            }
            Logger.log(Logger.DEBUG, Logger.MSG_INTERNATIONAL_DEBUG, "Language is now: "+locale.getDisplayName());
            
            bundle = ResourceBundle.getBundle("efa",locale);
            msgFormat = new MessageFormat("",locale);
            numberFormat = NumberFormat.getNumberInstance(locale);
            decimalSeparator = ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_INTERNATIONAL_FAILEDSETUP, "Failed to set up internationalization: "+e.toString()); // no need for translation
        }
        
        Daten.EFA_SHORTNAME = International.getString("efa");
        Daten.EFA_LONGNAME = International.getString("efa - elektronisches Fahrtenbuch");
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

    private static String makeKey(String s) {
        if (s == null) return "null";
        
        // @todo: To be extended by further characters? And maybe some performance optimization needed.
        String key = s;
        for (int i=0; i<key.length(); i++) {
            switch(key.charAt(i)) {
                case ' ':
                case '=':
                case ':':
                case '#':
                case '\'':
                    key = key.substring(0,i) + "_" + (i+1 < key.length() ? key.substring(i+1,key.length()) : "");
                    break;
            }
        }
        return key;
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
    public static String getMessage(String s, String arg1) {
        Object[] args = { "dummy", arg1 };
        return getMessage(s,args);
    }

    /**
     * Retrieves an internationalized compound message.
     * All arguments inside this message have to be masked by "{arg}", where "arg" may be any text
     * that is not further interpreted or used (other than being part of the key). For each of the
     * arguments masked by "{arg}", and argument arg1, arg2, ...., argn has to be supplied.
     * @param s key to be retrieved
     * @param arg1 argument to be placed into compound message
     * @param arg2 argument to be placed into compound message
     * @return translated message string
     */
    public static String getMessage(String s, String arg1, String arg2) {
        Object[] args = { "dummy", arg1, arg2 };
        return getMessage(s,args);
    }

    /**
     * Retrieves an internationalized compound message.
     * All arguments inside this message have to be masked by "{arg}", where "arg" may be any text
     * that is not further interpreted or used (other than being part of the key). For each of the
     * arguments masked by "{arg}", and argument arg1, arg2, ...., argn has to be supplied.
     * @param s key to be retrieved
     * @param arg1 argument to be placed into compound message
     * @param arg2 argument to be placed into compound message
     * @return translated message string
     */
    public static String getMessage(String s, String arg1, int arg2) {
        Object[] args = { "dummy", arg1, new Integer(arg2) };
        return getMessage(s,args);
    }

    public static String getMessage(String s, String arg1,
            int arg2, String arg3) {
        Object[] args = {"dummy", arg1, new Integer(arg2), arg3};
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

    public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4) {
        Object[] args = {"dummy", arg1, arg2, arg3, arg4};
        return getMessage(s, args);
    }

    public static String getMessage(String s, String arg1, String arg2, String arg3) {
        Object[] args = {"dummy", arg1, arg2, arg3};
        return getMessage(s, args);
    }

    public static String getMessage(String s, int arg1) {
        Object[] args = {"dummy", new Integer(arg1)};
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

}
