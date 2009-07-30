package de.nmichael.efa;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Liste der Boote, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

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
