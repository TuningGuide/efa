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

    private static final boolean MARK_MISSING_KEYS = true;
    private static final boolean STACKTRACE_MISSING_KEYS = false;

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

    public static String getString(String s) {
        if (bundle == null) {
            initialize();
        }
        try {
            return bundle.getString(EfaUtil.replace(s, " ", "_", true));
        } catch(Exception e) {
            if (STACKTRACE_MISSING_KEYS) {
                e.printStackTrace();
            }
            return (MARK_MISSING_KEYS ? "#" : "") + s + (MARK_MISSING_KEYS ? "#" : "");
        }
    }

    public static String getMessage(String s, Object[] args) {
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

    public static String getMessage(String s, String arg1) {
        Object[] args = { "dummy", arg1 };
        return getMessage(s,args);
    }

    public static String getMessage(String s, String arg1, String arg2) {
        Object[] args = { "dummy", arg1, arg2 };
        return getMessage(s,args);
    }

    public static String getMessage(String s, String arg1, int arg2) {
        Object[] args = { "dummy", arg1, Integer.toString(arg2) };
        return getMessage(s,args);
    }

    // todo:
    // - what to do about mnemonics?
    // - what to do about compound messages with strings and variables?
    // - what tool to use for replacing strings?
    // - how to easily recognize strings that only contain special characters and do not need translation (without prompting)?
    // - how to handle "formatted strings", e.g. some with \n in them? --> efa can automatically generate \n by now!
    // - how to split existing strings which contain ":" or ">>>" into two strings where only one of them is being translated?
    // - how to deal with concatenated strings (e.g. "asd" + "qwe")? --> "asdqwe"

}
