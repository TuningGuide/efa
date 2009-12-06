/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.drv;

import de.nmichael.efa.core.DatenListe;

// @i18n complete (needs no internationalization -- only relevant for Germany)

public class Teilnehmer extends DatenListe {

  public static final int _ANZFELDER = 5;

  public static final int TEILNNR = 0;
  public static final int VORNAME = 1;
  public static final int NACHNAME = 2;
  public static final int JAHRGANG = 3;
  public static final int FAHRTENHEFT = 4;

  // Konstruktor
  public Teilnehmer(String pdat) {
    super(pdat,_ANZFELDER,1,false);
    kennung = "##EFA.151.DRVTEILNEHMER##";
  }

}
