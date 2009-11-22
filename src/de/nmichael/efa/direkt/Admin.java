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
}