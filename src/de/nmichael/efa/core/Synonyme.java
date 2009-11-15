/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.core.DatenListe;

// @i18n complete

public class Synonyme extends DatenListe {

  public static final int _FELDERANZAHL = 2; // Anzahl der Felder für DatenListe

  public static final int SYNONYM = 0;
  public static final int ORIGINAL = 1;


  // Konstruktor
  public Synonyme(String pdat) {
    super(pdat,_FELDERANZAHL,1,false);
    kennung = "##EFA.091.SYNONYME##";
  }


  // alle Einträge löschen
  public void removeAllSyns() {
    this.l = new SortedList(false);
  }

}
