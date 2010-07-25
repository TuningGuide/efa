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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;
import javax.help.*;
import java.net.URL;
import java.util.Locale;

// @i18n complete
public class Help {

    private static HelpSet helpSet;
    private static HelpBroker helpBroker;

    public static HelpSet getHelpSet() {
        if (helpSet == null) {
            try {
                ClassLoader cl = Help.class.getClassLoader();
                URL helpUrl = HelpSet.findHelpSet(cl, Daten.EFA_HELPSET, International.getLocale());
                helpSet = new HelpSet(null, helpUrl);
            } catch(Exception e) {
                Logger.log(Logger.ERROR, Logger.MSG_HELP_ERRORHELPSET, "Cannot create HelpSet: "+e.toString());
            }
        }
        return helpSet;
    }

    public static HelpBroker getHelpBroker() {
        getHelpSet();
        if (helpBroker == null && helpSet != null) {
            try {
                helpBroker = helpSet.createHelpBroker();
            } catch(Exception e) {
                Logger.log(Logger.ERROR, Logger.MSG_HELP_ERRORHELPBROKER, "Cannot create HelpBroker: "+e.toString());
            }
        }
        return helpBroker;
    }

    public static void showHelp(String topic) {
        try {
            ((javax.help.DefaultHelpBroker)Help.getHelpBroker()).setActivationWindow(Dialog.frameCurrent());
            try {
                Help.getHelpBroker().setCurrentID(topic);
            } catch(Exception e) {
                Help.getHelpBroker().setCurrentID("default");
            }
            Help.getHelpBroker().setDisplayed(true);
        } catch(Exception e) {
            Dialog.infoDialog(International.getString("Hilfe"),
                              International.getString("Keine Hilfe verfügbar."));
        }
    }

}
