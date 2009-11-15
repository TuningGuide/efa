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

import java.io.*;
import java.util.Hashtable;

// @i18n complete

public class Adressen extends DatenListe {


  public static final int NAME = 0;
  public static final int ADRESSE = 1;

  public static final String KENNUNG091 = "##EFA.091.ADRESSEN##";


  // Konstruktor
  public Adressen(String pdat) {
    super(pdat,2,1,false);
    kennung = KENNUNG091;
  }


  // Key-Wert ermitteln
  public String constructKey(DatenFelder d) {
    return d.get(NAME);
  }



}