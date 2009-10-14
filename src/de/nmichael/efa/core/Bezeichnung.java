package de.nmichael.efa.core;

import java.util.Vector;
import de.nmichael.efa.util.International;

// @i18n complete

public class Bezeichnung {

  Vector values;

  public Bezeichnung() {
    values = new Vector();
  }

  public int add(String s) {
    values.add(s);
    return values.size()-1;
  }

  public int addFirst(String s) {
    values.add(0,s);
    return values.size()-1;
  }

  public String get(int i) {
    if (i<0 || i>=values.size()) return International.getString("unbekannt");
    return (String)values.get(i);
  }

  public int get(String s) {
    for (int i=0; i<values.size(); i++) {
      if (((String)values.get(i)).equals(s)) return i;
    }
    return -1;
  }

  public int size() {
    return values.size();
  }

  public String[] toArray() {
    String[] a = new String[values.size()];
    for (int i=0; i<a.length; i++) a[i] = (String)values.get(i);
    return a;
  }

}
