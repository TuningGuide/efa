package de.nmichael.efa;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Liste der Ziele, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

import java.io.IOException;

public class Ziele extends DatenListe {

  public static final int NAME = 0;
  public static final int KM = 1;
  public static final int BEREICH = 2;
  public static final int STEGZIEL = 3; // neu in v1.2.0
  public static final int GEWAESSER = 4; // neu in v1.4.1


  public static final String KENNUNG060 = "##EFA.060.ZIELE##";
  public static final String KENNUNG120 = "##EFA.120.ZIELE##";
  public static final String KENNUNG141 = "##EFA.141.ZIELE##";
  public static final String KENNUNG152 = "##EFA.152.ZIELE##";
  public static final String KENNUNG174 = "##EFA.174.ZIELE##";

  // Konstruktor
  public Ziele(String pdat) {
    super(pdat,5,1,false);
    kennung = KENNUNG174;
  }


  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {


        // KONVERTIEREN: 060 -> 120
        if (s != null && s.trim().startsWith(KENNUNG060)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"060");
          iniList(this.dat,4,1,false); // Rahmenbedingungen von v1.2.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(constructFields(s+"|+"));
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG120;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN: 120 -> 141
        if (s != null && s.trim().startsWith(KENNUNG120)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"120");
          iniList(this.dat,5,1,false); // Rahmenbedingungen von v1.4.1 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(constructFields(s+"|"));
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG141;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 141 -> 152
        if (s != null && s.trim().startsWith(KENNUNG141)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"141");
          iniList(this.dat,5,1,false); // Rahmenbedingungen von v1.5.2 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              d.set(BEREICH,EfaUtil.replace(d.get(BEREICH),";","/",true));
              add(d);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG152;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN: 152 -> 174
        if (s != null && s.trim().startsWith(KENNUNG152)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"152");
          iniList(this.dat,5,1,false); // Rahmenbedingungen von v1.7.4 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);
              if (Daten.efaConfig != null && Daten.efaConfig.zielfahrtSeparatorFahrten != null &&
                  Daten.efaConfig.zielfahrtSeparatorFahrten.length() == 1 &&
                  !Daten.efaConfig.zielfahrtSeparatorFahrten.equals("/")) {
                d.set(BEREICH,EfaUtil.replace(d.get(BEREICH),"/",Daten.efaConfig.zielfahrtSeparatorFahrten,true));
              }
              add(d);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG174;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(kennung)) {
          Dialog.error("Datei '"+dat+"' hat ungültiges Format!");
          fclose(false);
          return false;
        }
      }
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht gelesen werden!");
      return false;
    }
    return true;
  }




  // Einträge auf Gültigkeit prüfen
  public void validateValues(DatenFelder d) {
    d.set(KM,EfaUtil.zehntelInt2String(EfaUtil.zehntelString2Int(d.get(KM))));
    if (Daten.efaConfig != null && Daten.efaConfig.zielfahrtSeparatorFahrten != null &&
        Daten.efaConfig.zielfahrtSeparatorFahrten.length() == 1 &&
        d.get(BEREICH).indexOf(Daten.efaConfig.zielfahrtSeparatorFahrten)>=0) d.set(STEGZIEL,"-");
  }


  public void checkAllZielbereiche(String _heimZb) {
    if (_heimZb == null || _heimZb.length()==0) return;
    boolean changes = false;
    int heimZb = EfaUtil.string2int(_heimZb,0);
    if (heimZb < 1 || heimZb > Zielfahrt.ANZ_ZIELBEREICHE) return;
    for (DatenFelder d = getCompleteFirst(); d != null; d = getCompleteNext()) {
      if (d.get(Ziele.STEGZIEL).equals("+")) {
        ZielfahrtFolge zff = new ZielfahrtFolge(d.get(Ziele.BEREICH));
        boolean eigenerBereichErreicht = false;
        for (int i=0; i<zff.getAnzZielfahrten(); i++) {
          if (zff.getZielfahrt(i).isErreicht(heimZb)) eigenerBereichErreicht = true;
        }
        if (eigenerBereichErreicht) {
          switch (Dialog.auswahlDialog("Fehlerhafter Zieleintrag",
                                       "Name des Ziels: "+d.get(NAME)+" ("+d.get(KM)+" Km)\n"+
                                       "Zielbereiche: "+d.get(BEREICH)+"\n"+
                                       "Steg und Ziel ist eigenes Bootshaus: JA\n\n"+
                                       "Sollte es sich bei dem genannten Ziel tatsächlich um eine Fahrt handeln,\n"+
                                       "deren Start und Zielpunkt das eigene Bootshaus ist, so wird für dieses\n"+
                                       "Ziel der eigene Zielbereich \""+_heimZb+"\" NICHT gewertet und darf\n"+
                                       "folglich auch NICHT angegeben werden.\n\n"+
                                       "Um den Fehler zu beheben, kann efa nun den Zielbereich \""+_heimZb+"\"\n"+
                                       "aus der Aufzählung der Zielbereiche entfernen oder die Eigenschaft\n"+
                                       "'Start und Ziel ist eigenes Bootshaus' auf 'Nein' setzen, falls\n"+
                                       "es sich doch nicht um eine Fahrt vom und zum eigenen Bootshaus handelt.\n",
                                       "Zielbereich "+_heimZb+" entfernen","Start und Ziel ist NICHT Bootshaus","Nichts tun")) {
            case 0: Zielfahrt zf = new Zielfahrt();
                    zf.setBereich(d.get(BEREICH));
                    zf.setBereich(heimZb,false);
                    d.set(BEREICH,zf.getBereiche());
                    changes = true;
                    break;
            case 1: d.set(STEGZIEL,"-");
                    changes = true;
                    break;
            default: // nothing to do
          }
        }
      }
    }
    if (changes) writeFile();
  }

}
