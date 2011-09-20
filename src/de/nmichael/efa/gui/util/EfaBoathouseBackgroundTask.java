/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.gui.util;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.*;
import java.awt.*;
import java.io.*;
import java.io.FileReader;
import java.util.*;

public class EfaBoathouseBackgroundTask extends Thread {

    private static final int CHECK_INTERVAL = 60;
    private static final int ONCE_AN_HOUR = 60;
    EfaBoathouseFrame efaBoathouseFrame;
    int onceAnHour;
    Date date;
    Calendar cal;
    Calendar lockEfa;
    boolean framePacked;
    long lastEfaConfigScn = -1;

    public EfaBoathouseBackgroundTask(EfaBoathouseFrame efaBoathouseFrame) {
        this.efaBoathouseFrame = efaBoathouseFrame;
        this.onceAnHour = 5; // initial nach 5 Schleifendurchläufen zum ersten Mal hier reingehen
        this.cal = new GregorianCalendar();
        this.lockEfa = null;
        this.date = new Date();
        this.framePacked = false;
    }

    private void lockEfaThread() {
        new Thread() {

            public void run() {
                efaBoathouseFrame.lockEfa();
            }
        }.start();
    }

    public void setEfaLockBegin(DataTypeDate datum, DataTypeTime zeit) {
        if (datum == null) {
            lockEfa = null;
        } else {
            if (zeit != null) {
                lockEfa = new GregorianCalendar(datum.getYear(), datum.getMonth() - 1, datum.getDay(), zeit.getHour(), zeit.getMinute());
            } else {
                lockEfa = new GregorianCalendar(datum.getYear(), datum.getMonth() - 1, datum.getDay());
            }
        }
    }

    private void mailWarnings() {
        try {
            BufferedReader f = new BufferedReader(new FileReader(Daten.efaLogfile));
            String s;
            Vector warnings = new Vector();
            while ((s = f.readLine()) != null) {
                if (Logger.isWarningLine(s) && Logger.getLineTimestamp(s) > Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime()) {
                    warnings.add(s);
                }
            }
            f.close();
            if (warnings.size() == 0) {
                Logger.log(Logger.INFO, Logger.MSG_EVT_CHECKFORWARNINGS,
                        International.getMessage("Seit {date} sind keinerlei Warnungen in efa verzeichnet worden.", 
                        EfaUtil.getTimeStamp(Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime())));
            } else {
                Logger.log(Logger.INFO, Logger.MSG_EVT_CHECKFORWARNINGS,
                        International.getMessage("Seit {date} sind {n} Warnungen in efa verzeichnet worden.",
                        EfaUtil.getTimeStamp(Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime()), warnings.size()));
                String txt = International.getMessage("Folgende Warnungen sind seit {date} in efa verzeichnet worden:",
                        EfaUtil.getTimeStamp(Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime())) + "\n"
                        + International.getMessage("{n} Warnungen", warnings.size()) + "\n\n";
                for (int i = 0; i < warnings.size(); i++) {
                    txt += ((String) warnings.get(i)) + "\n";
                }
                if (Daten.project != null && Daten.efaConfig != null) {
                    Messages messages = Daten.project.getMessages(false);
                    if (messages != null && Daten.efaConfig.getValueEfaDirekt_bnrWarning_admin()) {
                        messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_ADMIN, International.getString("Warnungen"), txt);
                    }
                    if (messages != null && Daten.efaConfig.getValueEfaDirekt_bnrWarning_bootswart()) {
                        messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, MessageRecord.TO_BOATMAINTENANCE, International.getString("Warnungen"), txt);
                    }
                }
            }
            if (Daten.efaConfig != null) {
                Daten.efaConfig.setValueEfaDirekt_bnrWarning_lasttime(System.currentTimeMillis());
                //@efaconfig Daten.efaConfig.writeFile();
            }

        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_ERR_CHECKFORWARNINGS,
                    International.getMessage("Benachrichtigung über WARNING's im Logfile ist fehlgeschlagen: {msg}", e.toString()));
        }
    }

    public void run() {
        // Diese Schleife läuft i.d.R. einmal pro Minute
        while (true) {
            // Update GUI on Config Changes
            checkUpdateGui();

            // Reservierungs-Checker
            checkBoatStatus();

            // Nach ungelesenen Nachrichten für den Admin suchen
            checkForUnreadMessages();

            // automatisches Beenden von efa
            checkForExitOrRestart();

            // efa zeitgesteuert sperren
            checkForLockEfa();

            // automatisches Beginnen eines neuen Fahrtenbuchs (z.B. zum Jahreswechsel)
            checkForAutoCreateNewLogbook();

            // immer im Vordergrund
            checkAlwaysInFront();

            // Fokus-Kontrolle
            checkFocus();

            // Speicher-Überwachung
            checkMemory();

            // Aktivitäten einmal pro Stunde
            if (--onceAnHour <= 0) {
                System.gc(); // Damit Speicherüberwachung funktioniert (anderenfalls wird CollectionUsage nicht aktualisiert; Java-Bug)
                onceAnHour = ONCE_AN_HOUR;
                if (Logger.isTraceOn(Logger.TT_BACKGROUND)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFABACKGROUNDTASK, "EfaDirektBackgroundTask: alive!");
                }

                checkWarnings();
            }

            try {
                Thread.sleep(CHECK_INTERVAL * 1000);
            } catch (Exception e) {
                // wenn unterbrochen, dann versuch nochmal, kurz zu schlafen, und arbeite dann weiter!! ;-)
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                    EfaUtil.foo();
                }

                checkPackFrame();
            }
        } // end: while(true)
    } // end: run

    private void checkUpdateGui() {
        if (Daten.efaConfig != null) {
            try {
                long scn = Daten.efaConfig.data().getSCN();
                if (scn != lastEfaConfigScn) {
                    efaBoathouseFrame.updateGuiElements();
                    lastEfaConfigScn = scn;
                }
            } catch(Exception e) {
                Logger.logdebug(e);
            }
        }
    }

    private void checkBoatStatus() {
        BoatStatus boatStatus = (Daten.project != null ? Daten.project.getBoatStatus(false) : null);
        BoatReservations boatReservations = (Daten.project != null ? Daten.project.getBoatReservations(false) : null);
        if (boatStatus == null || boatReservations == null) {
            return;
        }

        long now = System.currentTimeMillis();
        boolean listChanged = false;
        try {
            DataKeyIterator it = boatStatus.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                BoatStatusRecord boatStatusRecord = (BoatStatusRecord) boatStatus.data().get(k);
                try {
                    boolean statusRecordChanged = false;

                    // set CurrentStatus correctly
                    if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER) &&
                        !boatStatusRecord.getCurrentStatus().equals(boatStatusRecord.getBaseStatus())) {
                        boatStatusRecord.setCurrentStatus(boatStatusRecord.getBaseStatus());
                        statusRecordChanged = true;
                    }

                    if (boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_HIDE)) {
                        if (boatStatusRecord.getShowInList() != null && !boatStatusRecord.getShowInList().equals(BoatStatusRecord.STATUS_HIDE)) {
                            boatStatusRecord.setShowInList(null);
                            boatStatus.data().update(boatStatusRecord);
                            listChanged = true;
                        }
                        continue;
                    }
                    if (boatStatusRecord.getUnknownBoat()) {
                        if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
                            boatStatus.data().delete(boatStatusRecord.getKey());
                            listChanged = true;
                        }
                        continue;
                    }

                    // delete any obsolete revervations
                    boatReservations.purgeObsoleteReservations(boatStatusRecord.getBoatId(), now);

                    // find all currently valid reservations
                    BoatReservationRecord[] reservations = boatReservations.getBoatReservations(boatStatusRecord.getBoatId(), now, 0);
                    if (reservations == null || reservations.length == 0) {
                        // no reservations at the moment - nothing to do
                        if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER) &&
                            !boatStatusRecord.getShowInList().equals(boatStatusRecord.getCurrentStatus())) {
                            boatStatusRecord.setShowInList(null);
                            statusRecordChanged = true;
                        }
                    } else {
                        // reservations found
                        if (!boatStatusRecord.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
                            if (Daten.efaConfig.getValueEfaDirekt_resBooteNichtVerfuegbar()) {
                                if (!boatStatusRecord.getShowInList().equals(BoatStatusRecord.STATUS_NOTAVAILABLE)) {
                                    boatStatusRecord.setShowInList(BoatStatusRecord.STATUS_NOTAVAILABLE);
                                    statusRecordChanged = true;
                                }
                            } else {
                                if (!boatStatusRecord.getShowInList().equals(boatStatusRecord.getBaseStatus())) {
                                    boatStatusRecord.setShowInList(boatStatusRecord.getBaseStatus());
                                    statusRecordChanged = true;
                                }
                            }
                        }
                        String s = International.getMessage("reserviert für {name} ({reason}) {from_to}",
                                reservations[0].getPersonAsName(),
                                reservations[0].getReason(),
                                reservations[0].getReservationTimeDescription());
                        if (!s.equals(boatStatusRecord.getComment())) {
                            boatStatusRecord.setComment(s);
                            statusRecordChanged = true;
                        }
                    }
                    
                    if (statusRecordChanged) {
                        boatStatus.data().update(boatStatusRecord);
                        listChanged = true;
                    }
                } catch (Exception ee) {
                    Logger.logdebug(ee);
                }
                k = it.getNext();
            }
            efaBoathouseFrame.updateBoatLists(listChanged);
        } catch (Exception e) {
            Logger.logdebug(e);
        }
    }

    private void checkForUnreadMessages() {
        boolean admin = false;
        boolean boatmaintenance = false;

        Messages messages = (Daten.project != null ? Daten.project.getMessages(false) : null);

        if (messages != null) {
            // durchsuche die letzten 50 Nachrichten nach ungelesenen (aus Performancegründen immer nur die letzen 50)
            int i=0;
            try {
                DataKeyIterator it = messages.data().getStaticIterator();
                DataKey k = it.getLast();
                while (k != null) {
                    MessageRecord msg = (MessageRecord)messages.data().get(k);
                    if (msg != null && !msg.getRead()) {
                        if (msg.getTo().equals(MessageRecord.TO_ADMIN)) {
                            admin = true;
                        }
                        if (msg.getTo().equals(MessageRecord.TO_BOATMAINTENANCE)) {
                            boatmaintenance = true;
                        }
                    }
                    if (++i ==50 || (admin && boatmaintenance)) {
                        break;
                    }
                    k = it.getPrev();
                }
            } catch(Exception e) {
                Logger.logdebug(e);
            }
        }
        efaBoathouseFrame.setUnreadMessages(admin, boatmaintenance);
    }

    private void checkForExitOrRestart() {
        // automatisches, zeitgesteuertes Beenden von efa ?
        if (Daten.efaConfig.getValueEfaDirekt_exitTime().isSet()
                && System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME + 1) * 60 * 1000) {
            date.setTime(System.currentTimeMillis());
            cal.setTime(date);
            int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
            int exitTime = Daten.efaConfig.getValueEfaDirekt_exitTime().getHour() * 60 + Daten.efaConfig.getValueEfaDirekt_exitTime().getMinute();
            if ((now >= exitTime && now < exitTime + Daten.AUTO_EXIT_MIN_RUNTIME) || (now + (24 * 60) >= exitTime && now + (24 * 60) < exitTime + Daten.AUTO_EXIT_MIN_RUNTIME)) {
                Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDEXIT,
                        International.getString("Eingestellte Uhrzeit zum Beenden von efa erreicht!"));
                if (System.currentTimeMillis() - efaBoathouseFrame.getLastUserInteraction() < Daten.AUTO_EXIT_MIN_LAST_USED * 60 * 1000) {
                    Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDEXITDELAY,
                            International.getMessage("Beenden von efa wird verzögert, da efa innerhalb der letzten {n} Minuten noch benutzt wurde ...",
                            Daten.AUTO_EXIT_MIN_LAST_USED));
                } else {
                    EfaExitFrame.exitEfa(International.getString("Zeitgesteuertes Beenden von efa"), false, EfaBoathouseFrame.EFA_EXIT_REASON_TIME);
                }
            }
        }

        // automatisches Beenden nach Inaktivität ?
        if (Daten.efaConfig.getValueEfaDirekt_exitIdleTime() > 0
                && System.currentTimeMillis() - efaBoathouseFrame.getLastUserInteraction() > Daten.efaConfig.getValueEfaDirekt_exitIdleTime() * 60 * 1000) {
            Logger.log(Logger.INFO, Logger.MSG_EVT_INACTIVITYBASEDEXIT,
                    International.getString("Eingestellte Inaktivitätsdauer zum Beenden von efa erreicht!"));
            EfaExitFrame.exitEfa(International.getString("Zeitgesteuertes Beenden von efa"), false, EfaBoathouseFrame.EFA_EXIT_REASON_TIME);
        }

        // automatischer, zeitgesteuerter Neustart von efa ?
        if (Daten.efaConfig.getValueEfaDirekt_restartTime().isSet()
                && System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME + 1) * 60 * 1000) {
            date.setTime(System.currentTimeMillis());
            cal.setTime(date);
            int now = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
            int restartTime = Daten.efaConfig.getValueEfaDirekt_restartTime().getHour() * 60 + Daten.efaConfig.getValueEfaDirekt_restartTime().getMinute();
            if ((now >= restartTime && now < restartTime + Daten.AUTO_EXIT_MIN_RUNTIME) || (now + (24 * 60) >= restartTime && now + (24 * 60) < restartTime + Daten.AUTO_EXIT_MIN_RUNTIME)) {
                Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDRESTART, "Automatischer Neustart von efa (einmal täglich).");
                if (System.currentTimeMillis() - efaBoathouseFrame.getLastUserInteraction() < Daten.AUTO_EXIT_MIN_LAST_USED * 60 * 1000) {
                    Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDRESTARTDELAY, "Neustart von efa wird verzögert, da efa innerhalb der letzten " + Daten.AUTO_EXIT_MIN_LAST_USED + " Minuten noch benutzt wurde ...");
                } else {
                    EfaExitFrame.exitEfa("Automatischer Neustart von efa", true, EfaBoathouseFrame.EFA_EXIT_REASON_AUTORESTART);
                }
            }
        }
    }

    private void checkForLockEfa() {
        if (lockEfa != null) {
            date.setTime(System.currentTimeMillis());
            cal.setTime(date);
            if (cal.after(lockEfa)) {
                lockEfaThread();
                lockEfa = null;
            }
        }
    }
    
    private void checkForAutoCreateNewLogbook() {
        // @todo (P4) - EfaBoathouseBackgroundTask.checkForAutoCreateNewLogbook()
        /*
        if (Daten.applMode == Daten.APPL_MODE_NORMAL
                && Daten.efaConfig.efaDirekt_autoNewFb_datum.isSet()
                && Daten.efaConfig.efaDirekt_autoNewFb_datei.getValue().length() > 0) {
            if (EfaUtil.secondDateIsEqualOrAfterFirst(Daten.efaConfig.efaDirekt_autoNewFb_datum.toString(), EfaUtil.getCurrentTimeStampDD_MM_YYYY())) {
                efaBoathouseFrame.autoCreateNewFb();
            }
        }
        */
    }

    private void checkAlwaysInFront() {
        if (Daten.efaConfig.getValueEfaDirekt_immerImVordergrund() && this.efaBoathouseFrame != null
                && Dialog.frameCurrent() == this.efaBoathouseFrame) {
            Window[] windows = this.efaBoathouseFrame.getOwnedWindows();
            boolean topWindow = true;
            if (windows != null) {
                for (int i = 0; i < windows.length; i++) {
                    if (windows[i] != null && windows[i].isVisible()) {
                        topWindow = false;
                    }
                }
            }
            if (topWindow && Daten.efaConfig.getValueEfaDirekt_immerImVordergrundBringToFront()) {
                efaBoathouseFrame.bringFrameToFront();
            }
        }

    }

    private void checkFocus() {
        if (this.efaBoathouseFrame != null && this.efaBoathouseFrame.getFocusOwner() == this.efaBoathouseFrame) {
            // das Frame selbst hat den Fokus: Das soll nicht sein! Gib einer Liste den Fokus!
            efaBoathouseFrame.boatListRequestFocus(0);
        }
    }
    
    private void checkMemory() {
        try {
            // System.gc(); // !!! ONLY ENABLE FOR DEBUGGING PURPOSES !!!
            if (de.nmichael.efa.java15.Java15.isMemoryLow(Daten.MIN_FREEMEM_PERCENTAGE, Daten.WARN_FREEMEM_PERCENTAGE)) {
                efaBoathouseFrame.exitOnLowMemory("EfaBoathouseBackgroundTask: MemoryLow", false);
            }
        } catch (UnsupportedClassVersionError e) {
            EfaUtil.foo();
        } catch (NoClassDefFoundError e) {
            EfaUtil.foo();
        }
    }

    private void checkWarnings() {
        // WARNINGs aus Logfile an Admins verschicken
        if (System.currentTimeMillis() >= Daten.efaConfig.getValueEfaDirekt_bnrWarning_lasttime() + 7l * 24l * 60l * 60l * 1000l
                && (Daten.efaConfig.getValueEfaDirekt_bnrWarning_admin() || Daten.efaConfig.getValueEfaDirekt_bnrWarning_bootswart()) && Daten.efaLogfile != null) {
            mailWarnings();
        }
    }

    private void checkPackFrame() {
        // Bugfix, da efa unter manchen Versionen beim Start nicht richtig gepackt wird.
        // @todo (P4) - EfaBoathouseBackgroundTask.checkForAutoCreateNewLogbook() - STILL NECESSARY??
        /*
        if (!framePacked) {
            if (efaBoathouseFrame != null) {
                if (Daten.efaConfig != null) {
                    if (!Daten.efaConfig.efaDirekt_startMaximized.getValue()) {
                        efaBoathouseFrame.packFrame("EfaBoathouseBackgroundTask");
                    } else {
                        if (efaBoathouseFrame.jScrollPane1 != null && efaBoathouseFrame.westPanel != null && efaBoathouseFrame.contentPane != null) {
                            efaBoathouseFrame.jScrollPane1.setSize(efaBoathouseFrame.jScrollPane1.getPreferredSize());
                            efaBoathouseFrame.westPanel.validate();
                            efaBoathouseFrame.contentPane.validate();
                        }
                    }
                }
            }
            if (efaBoathouseFrame != null && efaBoathouseFrame.efaFrame != null) {
                efaBoathouseFrame.efaFrame.packFrame("EfaBoathouseBackgroundTask.run()");
            }
            framePacked = true; // nicht nochmal machen, sondern nur einmal beim Start
        }
        */
    }

}
