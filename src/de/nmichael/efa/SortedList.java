package de.nmichael.efa;

import java.util.Hashtable;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  SortedList speichern Elemente automatisch in einer sortierten, verketteten Liste ab
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

// Ein Element von SortedList
class Element {
  public String key;
  public Object value;
  public Element next,prev;

  // Konstruktor: String-Key "k" und restliche Daten "v"
  public Element(String k, Object v) {
    key = k;
    value = v;
    next = prev = null;
  }

}





public class SortedList {

  private Element head;        // erstes Element (Dummy-Element)
  private Element tail;        // letztes Element
  private Element lastElement; // letztes bei Suche geliefertes Element
  private String lastString;   // Suchmuster der letzten Suche
  private boolean numeric;     // Key-Feld als numerischen Wert betrachten
  private boolean ignoreCase=false;  // Gro�- und Kleinschreibung ignorieren
  private Hashtable hash;      // Hashtable f�r schnelleren Zugriff auf "Element"-Elemente
  private Element lastElementSaved=null; // gespeichertes letztes Element zum Wiederherstellen


  // Konstruktor
  public SortedList(boolean num) {
    head = tail = new Element("",null); // Dummy-Element
    lastElement = null;
    lastString = "";
    numeric = num;
    hash = new Hashtable();
  }


  // Liste l�schen
  public void clear() {
    // Referenzen l�schen (ggf. sonst memory leak)
    for (Element e = head; e != null; e = e.next) {
      if (e.prev != null) e.prev.next = null;
      e.prev = null;
    }

    head = tail = new Element("",null); // neues Dummy-Element
    lastElement = null;
    lastString = "";
    hash = new Hashtable();
  }


  // Eintrag zur Liste hinzuf�gen
  // @returns true, wenn das eingef�gte Element als letztes Element der Liste hinzugef�gt wurde
  public boolean put(String k, Object v) {
    Element e = new Element(k,v);
    Element c = tail;

    // an der richtigen Stelle einf�gen
    // von hinten beginnen --> bessere Performance, da Daten i.d.R. bereits sortiert vorliegen!!
    if (!numeric) // Key als String betrachten
      while (c.prev != null && e.key.compareTo(c.key)<0) c = c.prev;
    else          // Key als int-Wert betrachten
      while (c.prev != null && EfaUtil.compareIntString(e.key,c.key) < 0) c = c.prev;

    if ( (!numeric && e.key.compareTo(c.key)==0) || (numeric && EfaUtil.compareIntString(e.key,c.key)==0) ) {
      // Elemente sind gleich: Element ersetzen!
      e.next = c.next;
      e.prev = c.prev;
      if (c.next != null) c.next.prev = e;
      if (c.prev != null) c.prev.next = e;
    } else {
      // ungleiches Element einf�gen
      e.next = c.next;
      e.prev = c;
      if (c.next != null) c.next.prev = e;
      c.next = e;
    }
    if (e.next == null) tail = e;

    hash.put(k,e);

    return tail == e; // true, wenn eingef�gtes Element das letzte Element ist
  }


  // Element s l�schen;
  // true, wenn erfolgreich; sonst false
  public boolean delete(String k) {
    Element c = head;

    // Element suchen
    while (c.next != null && !k.equals(c.key)) c = c.next;

    // Element l�schen
    if (c.key.equals(k)) {
      if (c == tail) tail = c.prev;
      c.prev.next = c.next;
      if (c.next != null) c.next.prev = c.prev;
      hash.remove(k);
      return true;
    }
    return false;
  }


  // Alle Listenelemente ausgeben
  public void printAll() {
    Element e = head.next;
    while (e != null) {
       System.out.println(e.key+"  --  "+e.value);
       e = e.next;
    }
  }


  // Anzahl der Elemente ermitteln
  public int countElements() {
    /*
    Element c = head;
    int i = 0;
    while (c.next != null) {
      c = c.next;
      i++;
    }
    return i;
    */
    return hash.size();
  }


  // Element suchen, das mit "such" anf�ngt, beginnend mit Element "e"
  // "vorwaerts" bestimmt die Suchrichtung, "nurAnfang" bedeutet, da� es gen�gt,
  // wenn der Treffer entsprechend anf�ngt
  public String search(Element e, String such, boolean vorwaerts, boolean nurAnfang) {
    if (ignoreCase) such = such.toLowerCase();
    while (e != null) {
      if (ignoreCase) {
        if ((e.key.toLowerCase().startsWith(such) && nurAnfang) || (e.key.toLowerCase().equals(such) && !nurAnfang)) {
  	  lastElement = e;
  	  return e.key;
        }
      } else {
        if ((e.key.startsWith(such) && nurAnfang) || (e.key.equals(such) && !nurAnfang)) {
	  lastElement = e;
	  return e.key;
        }
      }
      if (vorwaerts) e = e.next;
      else e = e.prev;
    }
    return null;
  }


  // erstes Element suchen, das mit "such" anf�ngt
  // wie startsWith(such), nur da� "such" nicht als letzter Suchbegriff nicht gespeichert wird und somit
  // kein Weitersuchen m�glich ist, aber insb. auch nicht das n�chste Weitersuchen durch diesen Ruf beeinflu�t wird
  public String selectStartsWith(String such) {
    Element e = head.next;
    return search(e,such,true,true);
  }

  // erstes Element suchen, das mit "such" anf�ngt
  public String startsWith(String such) {
    Element e = head.next;
    lastString = such;  // f�r's Weitersuchen n�tig
    lastElement = null; // f�r's Weitersuchen n�tig
    return search(e,such,true,true);
  }


  // n�chstes Element suchen, das mit "such" anf�ngt
  public String nextStartsWith(String such) {
    if (lastElement == null || !lastString.equals(such)) return startsWith(such);
    Element e = lastElement.next;
    return search(e,such,true,true);
  }


  // vorheriges Element suchen, das mit "such" anf�ngt
  public String prevStartsWith(String such) {
    if (lastElement == null || !lastString.equals(such)) return startsWith(such);
    Element e = lastElement.prev;
    return search(e,such,false,true);
  }


  // letztes Element suchen, das mit "such" anf�ngt
  public String lastStartsWith(String such) {
    Element e = tail;
    lastString = such;  // f�r's Weitersuchen n�tig
    lastElement = null; // f�r's Weitersuchen n�tig
    return search(e,such,false,true);
  }


  // n�chstes Element suchen
  public String next() {
    if (lastElement == null) lastElement = head.next;
    else lastElement = lastElement.next;
    if (lastElement == null) lastElement = tail;
    if (lastElement != null) return lastElement.key;
    return null;
  }


  // vorheriges Element suchen
  public String prev() {
    if (lastElement == null) lastElement = tail;
    else lastElement = lastElement.prev;
    if (lastElement == null || lastElement == head) lastElement = head.next;
    if (lastElement != null) return lastElement.key;
    return null;
  }


  // ohne Suchkriterien das n�chste Element aus der Liste liefern
  // (um einzeln der Reihe nach alle Elemente auszugeben)
  public String get() {
    if (!lastString.equals("")) lastElement = null;
    lastString = "";
    if (lastElement == null) lastElement = head;
    lastElement = lastElement.next;
    if (lastElement != null) {
      return lastElement.key;
    }
    return null;
  }


  // aktuellen Datensatz komplett liefern
  public Object getComplete() {
    if (lastElement == null) return null;
    return lastElement.value;
  }


  // ersten Datensatz komplett liefern
  public Object getCompleteFirst() {
    lastElement = null;
    return getCompleteNext();
  }


  // komplette Datens�tze der Reihe nach liefern - vorw�rts
  public Object getCompleteNext() {
//    if (!lastString.equals("")) lastElement = null; // ????
    lastString = "";
    if (lastElement == null) lastElement = head;
    lastElement = lastElement.next;
    if (lastElement != null) {
      return lastElement.value;
    }
    return null;
  }


  // komplette Datens�tze der Reihe nach liefern - r�ckw�rts
  public Object getCompletePrev() {
//    if (!lastString.equals("")) lastElement = null; // ????
    lastString = "";
    if (lastElement == null) lastElement = head.next; // v0.81: ... = head;
    lastElement = lastElement.prev; // v0.81: ohne if
    if (lastElement != null && lastElement != head) {
      return lastElement.value;
    }
    return null;
  }


  // den letzten Datensatz komplett liefern
  public Object getCompleteLast() {
    lastElement = tail;
    lastString = "";
    if (tail == head) return null;
    else return lastElement.value;
  }


  // einen exakten Treffer liefern
  public String getExact(String such) {
    lastString = such;  // f�r's Weitersuchen n�tig
    lastElement = null; // f�r's Weitersuchen n�tig

    Element h = (Element)hash.get(such);
    if (h != null) {
      lastElement = h;
      return such;
    }

    Element e = head.next;
    return search(e,such,true,false);
  }


  // zu einem bestimmten Datensatz springen
  public void goTo(String such) {
    startsWith(such);
    lastString = "";
  }


    // Suchstrings l�schen, damit n�chste Suche eine neue Suche ist
  public void clearSearch() {
    lastString = "";
    lastElement = null;
  }



  // legt fest, ob Gro�- und Kleinschreibung beim Suchen ignoriert werden soll
  public void ignoreCase(boolean ic) {
    this.ignoreCase = ic;
  }


  // gibt zur�ck, ob die Liste leer ist
  public boolean isEmpty() {
    return (head == tail);
  }


  // speichere lastElement, um es sp�ter mit restoreLastElement wiederherzustellen
  public void saveLastElement() {
    lastElementSaved = lastElement;
  }


  // stelle den Wert von lastElement, wie er zuvor mittels saveLastElement gesichert wurde, wieder her
  public void restoreLastElement() {
    lastElement = lastElementSaved;
  }


}
