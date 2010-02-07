/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.direkt;

import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.util.*;
import java.util.Vector;

// @i18n complete
public class Admin {

    public String name = null;
    public String password = null;
    public String email = null;
    public boolean allowedAdminsVerwalten = false;
    public boolean allowedPasswortAendern = false;
    public boolean allowedVollzugriff = false;
    public boolean allowedEfaConfig = false;
    public boolean allowedFahrtenbuchAuswaehlen = false;
    public boolean allowedFahrtenbuchBearbeiten = false;
    public boolean allowedBootsstatusBearbeiten = false;
    public boolean allowedBootsreservierung = false;
    public boolean allowedBootslisteBearbeiten = false;
    public boolean allowedMitgliederlisteBearbeiten = false;
    public boolean allowedZiellisteBearbeiten = false;
    public boolean allowedGruppenBearbeiten = false;
    public boolean allowedNachrichtenAnzeigenAdmin = false;
    public boolean allowedNachrichtenAnzeigenBootswart = false;
    public boolean allowedStatistikErstellen = false;
    public boolean allowedLogdateiAnzeigen = false;
    public boolean allowedEfaBeenden = false;
    public boolean allowedEfaSperren = false;
    public boolean allowedExecCommand = false;
    public boolean nachrichtenAdminGelesenMarkierenDefault = false;
    public boolean nachrichtenAdminAllowedGelesenMarkieren = false;
    public boolean nachrichtenBootswartGelesenMarkierenDefault = false;
    public boolean nachrichtenBootswartAllowedGelesenMarkieren = false;

    public Admin(String name, String pwd) {
        this.name = name;
        this.password = pwd;
        this.email = "";

        boolean superAdmin = name.equals(EfaConfig.SUPERADMIN);

        this.allowedAdminsVerwalten = this.allowedPasswortAendern =
                this.allowedVollzugriff = this.allowedBootsstatusBearbeiten =
                this.allowedBootsreservierung = this.allowedFahrtenbuchBearbeiten =
                this.allowedBootslisteBearbeiten = this.allowedMitgliederlisteBearbeiten =
                this.allowedZiellisteBearbeiten = this.allowedGruppenBearbeiten =
                this.allowedFahrtenbuchAuswaehlen = this.allowedLogdateiAnzeigen =
                this.allowedNachrichtenAnzeigenAdmin =
                this.allowedNachrichtenAnzeigenBootswart = this.allowedStatistikErstellen =
                this.allowedEfaConfig = this.allowedEfaBeenden = this.allowedEfaSperren =
                this.allowedExecCommand =
                superAdmin;

        this.nachrichtenAdminAllowedGelesenMarkieren = this.nachrichtenAdminGelesenMarkierenDefault = superAdmin;
        this.nachrichtenBootswartAllowedGelesenMarkieren = this.nachrichtenBootswartGelesenMarkierenDefault = false;
    }

    public static Admin parseAdmin(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        Vector v = EfaUtil.split(s, '|');
        if (v.size() < 3 || v.size() > 4) {
            return null; // altes Format (<170): 3 Felder; neues Format (>=170): 4 Felder
        }
        String name = (String) v.get(0);
        String pwd = (String) v.get(1);
        if (name.length() == 0 || pwd.length() == 0) {
            return null;
        }
        String email = "";
        if (v.size() == 3) {
            s = (String) v.get(2);
        } else {
            email = (String) v.get(2);
            s = (String) v.get(3);
        }
        Admin a = new Admin(name, pwd);
        a.allowedAdminsVerwalten = EfaUtil.isOptionSet(s, 0);
        a.allowedEfaConfig = EfaUtil.isOptionSet(s, 1);
        a.allowedFahrtenbuchAuswaehlen = EfaUtil.isOptionSet(s, 2);
        a.allowedVollzugriff = EfaUtil.isOptionSet(s, 3);
        a.allowedBootsstatusBearbeiten = EfaUtil.isOptionSet(s, 4);
        a.allowedNachrichtenAnzeigenAdmin = EfaUtil.isOptionSet(s, 5);
        a.allowedLogdateiAnzeigen = EfaUtil.isOptionSet(s, 6);
        a.nachrichtenAdminGelesenMarkierenDefault = EfaUtil.isOptionSet(s, 7);
        a.nachrichtenAdminAllowedGelesenMarkieren = EfaUtil.isOptionSet(s, 8);
        a.allowedEfaBeenden = EfaUtil.isOptionSet(s, 9);
        a.allowedNachrichtenAnzeigenBootswart = EfaUtil.isOptionSet(s, 10) ||
                (s.length() < 11 && a.allowedNachrichtenAnzeigenAdmin); // weil dieses Feld neu ist: Falls
        a.nachrichtenBootswartGelesenMarkierenDefault = EfaUtil.isOptionSet(s, 11);
        a.nachrichtenBootswartAllowedGelesenMarkieren = EfaUtil.isOptionSet(s, 12);
        a.allowedStatistikErstellen = EfaUtil.isOptionSet(s, 13) ||
                (s.length() < 14 && a.allowedVollzugriff); // neu in v1.7.0
        a.allowedBootslisteBearbeiten = EfaUtil.isOptionSet(s, 14) ||
                (s.length() < 15 && a.allowedVollzugriff); // neu in v1.7.0
        a.allowedMitgliederlisteBearbeiten = EfaUtil.isOptionSet(s, 15) ||
                (s.length() < 16 && a.allowedVollzugriff); // neu in v1.7.0
        a.allowedZiellisteBearbeiten = EfaUtil.isOptionSet(s, 16) ||
                (s.length() < 17 && a.allowedVollzugriff); // neu in v1.7.0
        a.allowedGruppenBearbeiten = EfaUtil.isOptionSet(s, 17) ||
                (s.length() < 18 && a.allowedVollzugriff); // neu in v1.7.0
        a.allowedFahrtenbuchBearbeiten = EfaUtil.isOptionSet(s, 18) ||
                (s.length() < 19 && a.allowedVollzugriff); // neu in v1.7.1
        a.allowedBootsreservierung = EfaUtil.isOptionSet(s, 19) ||
                (s.length() < 20 && a.allowedVollzugriff); // neu in v1.8.3
        a.allowedPasswortAendern = EfaUtil.isOptionSet(s, 20) ||
                (s.length() < 21 && a.allowedVollzugriff); // neu in v1.8.3

        a.email = email;
        return a;
    }

    public String toString() {
        return name + "|" +
               password + "|" +
               email + "|" +
               (allowedAdminsVerwalten ? "+" : "-") +
               (allowedEfaConfig ? "+" : "-") +
               (allowedFahrtenbuchAuswaehlen ? "+" : "-") +
               (allowedVollzugriff ? "+" : "-") +
               (allowedBootsstatusBearbeiten ? "+" : "-") +
               (allowedNachrichtenAnzeigenAdmin ? "+" : "-") +
               (allowedLogdateiAnzeigen ? "+" : "-") +
               (nachrichtenAdminGelesenMarkierenDefault ? "+" : "-") +
               (nachrichtenAdminAllowedGelesenMarkieren ? "+" : "-") +
               (allowedEfaBeenden ? "+" : "-") +
               (allowedNachrichtenAnzeigenBootswart ? "+" : "-") +
               (nachrichtenBootswartGelesenMarkierenDefault ? "+" : "-") +
               (nachrichtenBootswartAllowedGelesenMarkieren ? "+" : "-") +
               (allowedStatistikErstellen ? "+" : "-") +
               (allowedBootslisteBearbeiten ? "+" : "-") +
               (allowedMitgliederlisteBearbeiten ? "+" : "-") +
               (allowedZiellisteBearbeiten ? "+" : "-") +
               (allowedGruppenBearbeiten ? "+" : "-") +
               (allowedFahrtenbuchBearbeiten ? "+" : "-") +
               (allowedBootsreservierung ? "+" : "-") +
               (allowedPasswortAendern ? "+" : "-");
    }
}
