/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa;

import java.util.*;
import java.text.*;

/**
 *
 * @author nick
 */
public class International {

    private static final boolean MARK_MISSING_KEYS = true; // default for production: false
    private static final boolean STACKTRACE_MISSING_KEYS = false; // default for production: false
    private static final boolean SHOW_KEY_INSTEAD_OF_TRANSLATION = false;  // default for production: false

    private static ResourceBundle bundle = null;
    private static MessageFormat msgFormat = null;

    private static void initialize() {
        try {
            if (Daten.efaConfig != null && Daten.efaConfig.language != null) {
                bundle = ResourceBundle.getBundle("efa",new Locale(Daten.efaConfig.language));
                msgFormat = new MessageFormat("",new Locale(Daten.efaConfig.language));
            } else {
                bundle = ResourceBundle.getBundle("efa");
                msgFormat = new MessageFormat("");
            }
        } catch(Exception e) {
            // @todo: to be handled (maybe Logging?)!
        }
    }

    private static String getArrayStrings(Object[] a) {
        String s = "";
        for (int i=1; a != null && i<a.length; i++) {
            if (a[i] != null) {
                s += (s.length() > 0 ? ", " : "") + a[i];
            }
        }
        return s;
    }

    private static String makeKey(String s) {
        if (s == null) return "null";
        // @todo: To be extended by further characters? And maybe some performance optimization needed.
        String key = EfaUtil.replace(s, " ", "_", true);
        key = EfaUtil.replace(key, "=", "_", true);
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
                if (STACKTRACE_MISSING_KEYS) {
                    e.printStackTrace();
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
            if (STACKTRACE_MISSING_KEYS) {
                e.printStackTrace();
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
        Object[] args = { "dummy", arg1, Integer.toString(arg2) };
        return getMessage(s,args);
    }

	public static String getMessage(String s, String arg1,
			int arg2, String arg3) {
		// @todo: This function should probably *not* convert arg2 to string, since EfaFrame uses it in a "Choice", which I think only works for numbers...
        Object[] args = { "dummy", arg1, Integer.toString(arg2), arg3 };
        return getMessage(s,args);
	}

	public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) {
        Object[] args = { "dummy", arg1, arg2, arg3, arg4, arg5, arg6, arg7 };
        return getMessage(s,args);
	}
	
	public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8) {
        Object[] args = { "dummy", arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8 };
        return getMessage(s,args);
	}
	
	public static String getMessage(String s, String arg1, String arg2, String arg3, String arg4) {
        Object[] args = { "dummy", arg1, arg2, arg3, arg4 };
        return getMessage(s,args);
	}
	
	public static String getMessage(String s, String arg1, String arg2, String arg3) {
        Object[] args = { "dummy", arg1, arg2, arg3 };
        return getMessage(s,args);
	}
	
	public static String getMessage(String s, int arg1) {
        Object[] args = { "dummy", Integer.toString(arg1) };
        return getMessage(s,args);
	}

    // todo:
    // - how to handle "formatted strings", e.g. some with \n in them? --> efa can automatically generate \n by now!

}
