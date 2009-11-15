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

import de.nmichael.efa.util.EfaUtil;

// Ein Element des AlleWWArr-Arrays
class AlleWWArrEl implements Comparable {

  public static boolean sortVorNachname;

  public String name;

  public AlleWWArrEl(String name) {
    this.name = name;
  }

  public int compareTo(Object o) throws ClassCastException {
    int ret=0;
    AlleWWArrEl b = (AlleWWArrEl)o;
    if (sortVorNachname) {
      ret = EfaUtil.getNachname(this.name).toUpperCase().compareTo(EfaUtil.getNachname(b.name).toUpperCase());
      if (ret == 0) return EfaUtil.getVorname(this.name).toUpperCase().compareTo(EfaUtil.getVorname(b.name).toUpperCase());
      else return ret;
    } else return this.name.toUpperCase().compareTo(b.name.toUpperCase());
  }

}
