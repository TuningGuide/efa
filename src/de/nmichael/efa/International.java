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


    public static String getString(String s) {
        if (bundle == null) {
            initialize();
        }
        try {
            return bundle.getString(EfaUtil.replace(s, " ", "_", true));
        } catch(Exception e) {
            return "#"+s+"#";
        }
    }

    public static String getMessage(String s, String arg1, String arg2) {
        if (msgFormat == null) {
            initialize();
        }
        try {
            Object[] args = { "dummy", arg1, arg2 };
            msgFormat.applyPattern(getString(s));
            return msgFormat.format(args);
        } catch(Exception e) {
            return "#"+s+"#";
        }
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
