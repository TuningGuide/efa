/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

// @i18n complete

// Daten für DRV-Fahrten
public class DRVFahrt {

  public String lfdnr,datumStart,datumEnde,ziel,bemerk;
  int km,anzTage;
  boolean ok; // gültige Wanderfahrt, oder nicht (MTour < 40 Km)
  boolean jum; // ob JuM-Regatta

}
