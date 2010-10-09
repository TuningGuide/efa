/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.efa1;

public class GruppenMitglied {


  public GruppenMitglied(String vorname, String nachname, String verein) {
    this.vorname = vorname;
    this.nachname = nachname;
    this.verein = verein;
  }

  public String vorname;
  public String nachname;
  public String verein;
}
