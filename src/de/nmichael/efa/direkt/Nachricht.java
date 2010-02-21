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

import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class Nachricht {

  public static final int ADMIN = 0;
  public static final int BOOTSWART = 1;
  public static final int ALLE = 99; // nur für Check auf ungelesene Nachrichten, wenn Nachrichten für beide vorliegen

  public int empfaenger=ADMIN;
  public String datum=null;
  public String name=null;
  public String betreff=null;
  public String nachricht=null;
  public boolean gelesen=false;

  public Nachricht() {
    datum = EfaUtil.getCurrentTimeStamp();
  }

  public Nachricht(int empfaenger, String datum, String name, String betreff, String nachricht) {
    this.empfaenger = empfaenger;
    this.datum = datum;
    this.name = name;
    this.betreff = betreff;
    this.nachricht = nachricht;
  }

  public static String getEmpfaengerName(int empfaenger) {
    switch(empfaenger) {
      case ADMIN: return International.getString("Administrator");
      case BOOTSWART: return International.getString("Bootswart");
    }
    return null;
  }

  public void sendEmail(String[] admins) {
    try {
      String adressen = "";
      for (int i=0; i<admins.length; i++) {
        Admin admin = Daten.efaConfig.admins.get(admins[i]);
        if ( (this.empfaenger == Nachricht.ADMIN && admin.allowedNachrichtenAnzeigenAdmin) ||
             (this.empfaenger == Nachricht.BOOTSWART && admin.allowedNachrichtenAnzeigenBootswart) ) {
          if (admin.email != null && admin.email.length()>0) adressen += (adressen.length() > 0 ? ", " : "") + admin.name + " <" + admin.email + ">";
        }
      }
      if (adressen.length() > 0) EmailSender.sendEmail(this,adressen);
    } catch(Exception e) {
    }
  }

}


