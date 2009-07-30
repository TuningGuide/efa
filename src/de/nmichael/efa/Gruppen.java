package de.nmichael.efa;

import java.util.Vector;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Liste der Boote, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

class GruppenMitglied {

  public GruppenMitglied(String vorname, String nachname, String verein) {
    this.vorname = vorname;
    this.nachname = nachname;
    this.verein = verein;
  }

  public String vorname;
  public String nachname;
  public String verein;
}

public class Gruppen extends DatenListe {

  public static final int GRUPPE = 0;
  public static final int VORNAME = 1;
  public static final int NACHNAME = 2;
  public static final int VEREIN = 3;
  public static final int _ANZ_FELDER = 4;

  public static final String KENNUNG170 = "##EFA.170.GRUPPEN##";

  // Konstruktor
  public Gruppen(String pdat) {
    super(pdat,_ANZ_FELDER,1,false);
    kennung = KENNUNG170;
  }

  // Key-Wert ermitteln
  public String constructKey(DatenFelder d) {
    return d.get(GRUPPE)+"#"+d.get(NACHNAME)+"#"+d.get(VORNAME)+"#"+d.get(VEREIN);
  }

  public Vector getGruppen() {
    Vector v = new Vector();
    DatenFelder d = getCompleteFirst();
    while (d != null) {
      if (!v.contains(d.get(GRUPPE))) v.add(d.get(GRUPPE));
      d = getCompleteNext();
    }
    return v;
  }

  public Vector getGruppenMitglieder(String gruppe) {
    Vector v = new Vector();
    DatenFelder d = getCompleteFirst();
    while (d != null) {
      if (d.get(GRUPPE).equals(gruppe)) {
        GruppenMitglied m = new GruppenMitglied(d.get(VORNAME),d.get(NACHNAME),d.get(VEREIN));
        v.add(m);
      }
      d = getCompleteNext();
    }
    if (v.size() == 0) return null;
    return v;
  }

  public void setGruppenMitglieder(String gruppe, Vector mitglieder) {
    for (int i=0; i<mitglieder.size(); i++) {
      GruppenMitglied m = (GruppenMitglied)mitglieder.get(i);
      DatenFelder d = new DatenFelder(_ANZ_FELDER);
      d.set(GRUPPE,gruppe);
      d.set(VORNAME,m.vorname);
      d.set(NACHNAME,m.nachname);
      d.set(VEREIN,m.verein);
      delete(constructKey(d));
      add(d);
    }
  }

  public void deleteGruppenMitglied(String gruppe, GruppenMitglied mitglied) {
    Vector m = getGruppenMitglieder(gruppe);
    if (m == null) return;
    for (int i=0; i<m.size(); i++) {
      GruppenMitglied gm = (GruppenMitglied)m.get(i);
      if (gm.vorname != null && gm.vorname.equals(mitglied.vorname) &&
          gm.nachname != null && gm.nachname.equals(mitglied.nachname) &&
          gm.verein != null && gm.verein.equals(mitglied.verein)) {
        m.remove(i);
        i--;
      }
    }
    deleteGruppe(gruppe);
    setGruppenMitglieder(gruppe,m);
  }

  public void addGruppenMitglied(String gruppe, GruppenMitglied mitglied) {
    Vector m = getGruppenMitglieder(gruppe);
    if (m == null) m = new Vector();
    for (int i=0; i<m.size(); i++) {
      GruppenMitglied gm = (GruppenMitglied)m.get(i);
      if (gm.vorname != null && gm.vorname.equals(mitglied.vorname) &&
          gm.nachname != null && gm.nachname.equals(mitglied.nachname) &&
          gm.verein != null && gm.verein.equals(mitglied.verein)) {
        return; // ist bereits Mitglied
      }
    }
    m.add(mitglied);
    setGruppenMitglieder(gruppe,m);
  }

  public void deleteGruppe(String gruppe) {
    Vector delete = new Vector();
    DatenFelder d = getCompleteFirst();
    while (d != null) {
      if (d.get(GRUPPE).equals(gruppe)) {
        delete.add(constructKey(d));
      }
      d = getCompleteNext();
    }
    for (int i=0; i<delete.size(); i++) {
      this.delete((String)delete.get(i));
    }
  }

  public boolean isInGroup(String gruppe, String vorname, String nachname, String verein) {
    return getExact(gruppe+"#"+nachname+"#"+vorname+"#"+verein) != null;
  }

}
