package de.nmichael.efa;

import java.util.GregorianCalendar;

public class TMJ implements Cloneable {

  public int tag,monat,jahr;

  public TMJ(int t, int m, int j) {
    tag = t;
    monat = m;
    jahr = j;
  }

  public GregorianCalendar toCalendar() {
    return new GregorianCalendar(jahr,monat-1,tag);
  }



}
