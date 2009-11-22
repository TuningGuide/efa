/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.*;
import java.util.*;
import java.io.IOException;

// @i18n @todo Separation of display data from file data!

class Reservierung implements Comparable {
  boolean einmalig;
  String vonTag,vonZeit,bisTag,bisZeit,name,grund;
  long gueltigInMinuten = 0; // internes Feld, wird nicht gespeichert, sondern nur von getReservierung(...) verwendet

  public int compareTo(Object o) throws ClassCastException {
    Reservierung b = (Reservierung)o;
    if (this.einmalig != b.einmalig) return (this.einmalig ? -1 : 1);
    if (this.einmalig) { // beides einmalige Reservierungen
      if (!this.vonTag.equals(b.vonTag)) return (EfaUtil.secondDateIsAfterFirst(this.vonTag,b.vonTag) ? -1 : 1);
      if (!this.vonZeit.equals(b.vonZeit)) return (EfaUtil.secondTimeIsAfterFirst(this.vonZeit,b.vonZeit) ? -1 : 1);
      if (!this.bisTag.equals(b.bisTag)) return (EfaUtil.secondDateIsAfterFirst(this.bisTag,b.bisTag) ? -1 : 1);
      if (!this.bisZeit.equals(b.bisZeit)) return (EfaUtil.secondTimeIsAfterFirst(this.bisZeit,b.bisZeit) ? -1 : 1);
      return 0;
    }
    // beide Reservierungen wöchentlich
    if (!this.vonTag.equals(b.vonTag)) {
      if (this.vonTag.equals("Montag")) return -1;
      if (   b.vonTag.equals("Montag")) return  1;
      if (this.vonTag.equals("Dienstag")) return -1;
      if (   b.vonTag.equals("Dienstag")) return  1;
      if (this.vonTag.equals("Mittwoch")) return -1;
      if (   b.vonTag.equals("Mittwoch")) return  1;
      if (this.vonTag.equals("Donnerstag")) return -1;
      if (   b.vonTag.equals("Donnerstag")) return  1;
      if (this.vonTag.equals("Freitag")) return -1;
      if (   b.vonTag.equals("Freitag")) return  1;
      if (this.vonTag.equals("Samstag")) return -1;
      if (   b.vonTag.equals("Samstag")) return  1;
      if (this.vonTag.equals("Sonntag")) return -1;
      if (   b.vonTag.equals("Sonntag")) return  1;
    }
    if (!this.vonZeit.equals(b.vonZeit)) return (EfaUtil.secondTimeIsAfterFirst(this.vonZeit,b.vonZeit) ? -1 : 1);
    if (!this.bisZeit.equals(b.bisZeit)) return (EfaUtil.secondTimeIsAfterFirst(this.bisZeit,b.bisZeit) ? -1 : 1);
    return 0;
  }

}

public class BootStatus extends DatenListe {

  public static final int _FELDANZ = 7;

  public static final int NAME = 0;
  public static final int STATUS = 1;
  public static final int LFDNR = 2;
  public static final int BEMERKUNG = 3;
  public static final int UNBEKANNTESBOOT = 4;
  public static final int RESERVIERUNGEN = 5;
  public static final int BOOTSSCHAEDEN = 6;                // neu in 1.6.0

  public static final int STAT_HIDE = 0;
  public static final int STAT_VERFUEGBAR = 1;
  public static final int STAT_UNTERWEGS = 2;
  public static final int STAT_NICHT_VERFUEGBAR = 3;
  public static final int STAT_VORUEBERGEHEND_VERSTECKEN = 4; // wird intern für Kombiboote verwendet
  protected static final String[] STATUSNAMES = { "nicht anzeigen", "verfügbar", "unterwegs", "nicht verfügbar", "vorübergehend verstecken" };

  public static final String KENNUNG120 = "##EFA.120.BOOTSTATUS##";
  public static final String KENNUNG160 = "##EFA.160.BOOTSTATUS##";
  public static final String KENNUNG170 = "##EFA.170.BOOTSTATUS##";

  public static final String RES_LFDNR = "RES";


  // Konstruktor
  public BootStatus(String pdat) {
    super(pdat,_FELDANZ,1,false);
    kennung = KENNUNG170;
  }


  public Vector getBoote(int status) {
    Vector v = new Vector();
    for (DatenFelder d = (DatenFelder)this.getCompleteFirst(); d != null; d = (DatenFelder)this.getCompleteNext()) {
      if (EfaUtil.string2int(d.get(STATUS),-1) == status) v.add(d.get(NAME));
    }
    return v;
  }

  public static Vector getReservierungen(DatenFelder boot, int version) {
    Vector v = new Vector();
    String s = boot.get(RESERVIERUNGEN);
    if (s == null) return v;
    StringTokenizer tok = new StringTokenizer(s,";");
    int pos=0;
    Reservierung r = null;
    while (tok.hasMoreTokens()) {
      if (pos == 0) {
        r = new Reservierung();
        r.einmalig = true;
      }
      switch(pos) {
        case 0: r.vonTag  = tok.nextToken(); break;
        case 1: r.vonZeit = tok.nextToken(); break;
        case 2: r.bisTag  = tok.nextToken(); break;
        case 3: r.bisZeit = tok.nextToken(); break;
        case 4: r.name    = tok.nextToken(); break;
        case 5: r.grund   = tok.nextToken(); break;
        case 6: r.einmalig= tok.nextToken().equals("+"); break;
      }
      pos++;
      if ((version == 160 && pos>5) || (version == 170 && pos>6)) {
       v.add(r);
       pos = 0;
      }
    }
    return v;
  }

  public static Vector getReservierungen(DatenFelder boot) {
    return getReservierungen(boot,170);
  }

  // gibt die Reservierung zurück, die zum Zeitpunkt now gültig ist oder max. minutesAhead beginnt
  // null, wenn keine Reservierung diese Kriterien erfüllt
  public static Reservierung getReservierung(DatenFelder boot, long now, long minutesAhead) {
    if (boot == null) return null;
    Vector res = getReservierungen(boot);
    if (res.size() == 0) return null;

    GregorianCalendar cal = new GregorianCalendar();
    cal.setTimeInMillis(now);
    int weekday = cal.get(Calendar.DAY_OF_WEEK);

    for (int i=0; i<res.size(); i++) {
      Reservierung r = (Reservierung)res.get(i);
      TMJ vonTag  = EfaUtil.string2date(r.vonTag,0,0,0);
      TMJ vonZeit = EfaUtil.string2date(r.vonZeit,0,0,0);
      TMJ bisTag  = EfaUtil.string2date(r.bisTag,0,0,0);
      TMJ bisZeit = EfaUtil.string2date(r.bisZeit,0,0,0);
      if (!r.einmalig) {
        switch(weekday) {
          case Calendar.MONDAY: if (!r.vonTag.equals("Montag")) continue; break;
          case Calendar.TUESDAY: if (!r.vonTag.equals("Dienstag")) continue; break;
          case Calendar.WEDNESDAY: if (!r.vonTag.equals("Mittwoch")) continue; break;
          case Calendar.THURSDAY: if (!r.vonTag.equals("Donnerstag")) continue; break;
          case Calendar.FRIDAY: if (!r.vonTag.equals("Freitag")) continue; break;
          case Calendar.SATURDAY: if (!r.vonTag.equals("Samstag")) continue; break;
          case Calendar.SUNDAY: if (!r.vonTag.equals("Sonntag")) continue; break;
        }
        vonTag.tag   = bisTag.tag   = cal.get(Calendar.DAY_OF_MONTH);
        vonTag.monat = bisTag.monat = cal.get(Calendar.MONTH)+1;
        vonTag.jahr  = bisTag.jahr  = cal.get(Calendar.YEAR);
      }

      long von = EfaUtil.dateTime2Cal(vonTag,vonZeit).getTimeInMillis();
      long bis = EfaUtil.dateTime2Cal(bisTag,bisZeit).getTimeInMillis();

      // ist die vorliegende Reservierung jetzt gültig
      if (now >= von && now <= bis) {
        r.gueltigInMinuten = 0;
        return r;
      }

      // ist die vorliegende Reservierung innerhalb von minutesAhead gültig
      if (now < von && now + minutesAhead*60*1000 >= von) {
        r.gueltigInMinuten = (von-now)/(60*1000);
        return r;
      }
    }
    return null;
  }

  public static String makeReservierungText(Reservierung r) {
    String s = null;
    if (r.einmalig) {
      if (r.vonTag.equals(r.bisTag)) s = "am "+r.vonTag+" von "+r.vonZeit+" bis "+r.bisZeit;
      else s = "vom "+r.vonTag+" "+r.vonZeit+" bis "+r.bisTag+"  "+r.bisZeit;
    } else {
      s = "jeden "+r.vonTag+" von "+r.vonZeit+" bis "+r.bisZeit;
    }
    return s;
  }

  // alle verfallenen Reservierungen löschen;
  // gibt true zurück, falls Reservierungen gelöscht wurden; sonst false.
  public static boolean deleteObsoleteReservierungen(DatenFelder boot) {
    if (boot == null) return false;
    Vector res = getReservierungen(boot);
    if (res.size() == 0) return false;

    long now = System.currentTimeMillis();

    boolean geloscht = false;
    for (int i=0; i<res.size(); i++) {
      Reservierung r = (Reservierung)res.get(i);
      if (!r.einmalig) continue; // zyklische Reservierungen werden nicht gelöscht

      TMJ bisTag  = EfaUtil.string2date(r.bisTag,0,0,0);
      TMJ bisZeit = EfaUtil.string2date(r.bisZeit,0,0,0);
      long bis = EfaUtil.dateTime2Cal(bisTag,bisZeit).getTimeInMillis();

      // ist die vorliegende Reservierung verfallen?
      if (now > bis) {
        res.remove(i);
        i--;
        geloscht = true;
      }
    }
    if (geloscht) setReservierungen(boot,res);
    return geloscht;
  }

  public static void clearReservierungen(DatenFelder boot) {
    boot.set(RESERVIERUNGEN,"");
  }

  public static void addReservierung(DatenFelder boot, Reservierung r) {
    Vector v = getReservierungen(boot);
    v.add(r);
    setReservierungen(boot,v);
  }

  public static void setReservierungen(DatenFelder boot, Vector v) {
    String s = "";
    Reservierung[] a = new Reservierung[v.size()];
    for (int i=0; i<v.size(); i++) a[i] = (Reservierung)v.get(i);
    Arrays.sort(a);
    for (int i=0; i<a.length; i++) {
      Reservierung r = a[i];
      s += EfaUtil.removeSepFromString(
             EfaUtil.removeSepFromString(r.vonTag,";")+";"+
             EfaUtil.removeSepFromString(r.vonZeit,";")+";"+
             EfaUtil.removeSepFromString(r.bisTag,";")+";"+
             EfaUtil.removeSepFromString(r.bisZeit,";")+";"+
             EfaUtil.removeSepFromString(r.name,";")+";"+
             EfaUtil.removeSepFromString(r.grund,";")+";"+
             (r.einmalig ? "+" : "-")+";"
           );
    }
    boot.set(RESERVIERUNGEN,s);
  }


  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN: 120 -> 160
        if (s != null && s.trim().startsWith(KENNUNG120)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"120");
          iniList(this.dat,7,1,false); // Rahmenbedingungen von v1.6.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s = s+"|";
              DatenFelder d = constructFields(s);
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG160;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 160 -> 170
        if (s != null && s.trim().startsWith(KENNUNG160)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"160");
          iniList(this.dat,7,1,false); // Rahmenbedingungen von v1.7.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              setReservierungen(d,getReservierungen(d,160)); // Reservierungen ins neue Format konvertieren
              add(d);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG170;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(kennung)) {
          errInvalidFormat(dat, EfaUtil.trimto(s, 20));
          fclose(false);
          return false;
        }
      }
    } catch(IOException e) {
      errReadingFile(dat,e.getMessage());
      return false;
    }
    return true;
  }

  public static String getStatusName(int status) {
    if (STATUSNAMES != null && status >= 0 && status < STATUSNAMES.length) return STATUSNAMES[status];
    return "unbekannt";
  }


}
