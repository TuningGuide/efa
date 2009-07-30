package de.nmichael.efa.drv;

import de.nmichael.efa.DatenListe;

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
