/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.efa1.Mitglieder;
import de.nmichael.efa.efa1.Fahrtenbuch;
import de.nmichael.efa.efa1.FBDaten;
import de.nmichael.efa.*;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.efa1.DatenFelder;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

// @i18n complete

public class ImportFrame extends JDialog implements ActionListener {
  public static int anzImportierteFahrten=0;
  EfaFrame efaFrame;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField quellFb = new JTextField();
  JButton dateiButton = new JButton();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextField lfdNrVon = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField lfdNrBis = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField datumVon = new JTextField();
  JLabel jLabel6 = new JLabel();
  JTextField datumBis = new JTextField();
  JLabel jLabel7 = new JLabel();
  JTextField lfdNrAdd = new JTextField();
  JLabel jLabel8 = new JLabel();
  JComboBox doppelteLfdNr = new JComboBox();
  JCheckBox zusatzImport = new JCheckBox();
  JLabel jLabel9 = new JLabel();
  JTextField gruppe = new JTextField();
  private DatenListe gruppen = null;
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JTextField characterAdd = new JTextField();

  public ImportFrame(EfaFrame efaFrame) {
    super(efaFrame);
    this.efaFrame = efaFrame;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      iniFelder();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    quellFb.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.showHelp(getClass().getCanonicalName());
    }
  }


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    jPanel1.setLayout(borderLayout1);
    okButton.setNextFocusableComponent(quellFb);

    Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Fahrten importieren"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Fahrten importieren aus")+": ");
    jLabel1.setLabelFor(quellFb);
    quellFb.setNextFocusableComponent(dateiButton);
    quellFb.setPreferredSize(new Dimension(200, 19));
    dateiButton.setNextFocusableComponent(lfdNrVon);
    dateiButton.setPreferredSize(new Dimension(45, 25));
    dateiButton.setIcon(new ImageIcon(ImportFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    dateiButton.setMargin(new Insets(0, 14, 0, 14));
    dateiButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dateiButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("nur Fahrten importieren mit")+": ");
    jLabel3.setLabelFor(lfdNrVon);
    Mnemonics.setLabel(this, jLabel3, International.getStringWithMnemonic("Lfd. Nr. von")+": ");
    jLabel4.setLabelFor(lfdNrBis);
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Lfd. Nr. bis")+": ");
    jLabel5.setLabelFor(datumVon);
    Mnemonics.setLabel(this, jLabel5, International.getStringWithMnemonic("Datum von")+": ");
    jLabel6.setLabelFor(datumBis);
    Mnemonics.setLabel(this, jLabel6, International.getStringWithMnemonic("Datum bis")+": ");
    lfdNrVon.setNextFocusableComponent(lfdNrBis);
    lfdNrVon.setPreferredSize(new Dimension(80, 19));
    lfdNrVon.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        lfdNrVon_focusLost(e);
      }
    });
    lfdNrBis.setNextFocusableComponent(datumVon);
    lfdNrBis.setPreferredSize(new Dimension(80, 19));
    lfdNrBis.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        lfdNrBis_focusLost(e);
      }
    });
    datumVon.setNextFocusableComponent(jLabel6);
    datumVon.setPreferredSize(new Dimension(80, 19));
    datumVon.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        datumVon_focusLost(e);
      }
    });
    datumBis.setNextFocusableComponent(gruppe);
    datumBis.setPreferredSize(new Dimension(80, 19));
    datumBis.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        datumBis_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel7, International.getStringWithMnemonic("Zu allen importierten Fahrten")+": ");
    lfdNrAdd.setNextFocusableComponent(characterAdd);
    lfdNrAdd.setPreferredSize(new Dimension(80, 19));
    lfdNrAdd.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        lfdNrAdd_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel8, International.getStringWithMnemonic("Bei doppelten Lfd. Nr.")+": ");
    jLabel8.setLabelFor(doppelteLfdNr);
    zusatzImport.setNextFocusableComponent(okButton);
    Mnemonics.setButton(this, zusatzImport, International.getStringWithMnemonic("auch neue Daten aus Zusatzdatenlisten importieren")+": ");
    zusatzImport.setSelected(true);
    jPanel1.setPreferredSize(new Dimension(700, 260));
    this.setTitle(International.getString("Fahrten importieren"));
    doppelteLfdNr.setNextFocusableComponent(zusatzImport);
    Mnemonics.setLabel(this, jLabel9, International.getStringWithMnemonic("Personen der Gruppe")+": ");
    jLabel9.setLabelFor(gruppe);
    gruppe.setNextFocusableComponent(lfdNrAdd);
    gruppe.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppe.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel10, International.getStringWithMnemonic("Lfd. Nr. hinzuaddieren")+": ");
    jLabel10.setLabelFor(lfdNrAdd);
    Mnemonics.setLabel(this, jLabel11, International.getStringWithMnemonic("Buchstaben anhängen")+": ");
    jLabel11.setLabelFor(characterAdd);
    characterAdd.setNextFocusableComponent(doppelteLfdNr);
    characterAdd.setPreferredSize(new Dimension(80, 19));
    characterAdd.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        characterAdd_focusLost(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(okButton, BorderLayout.SOUTH);
    jPanel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jLabel1,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(quellFb,   new GridBagConstraints(2, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(dateiButton,   new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel2,   new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel3,   new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
    jPanel2.add(lfdNrVon,   new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel4,   new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
    jPanel2.add(lfdNrBis,   new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel5,   new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel2.add(datumVon,   new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel6,   new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel2.add(datumBis,   new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel7,     new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(lfdNrAdd,    new GridBagConstraints(4, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel8,     new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(doppelteLfdNr,    new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(zusatzImport,   new GridBagConstraints(0, 7, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel9,     new GridBagConstraints(2, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel2.add(gruppe,    new GridBagConstraints(4, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel10,     new GridBagConstraints(2, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel11,   new GridBagConstraints(2, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(characterAdd,  new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void iniFelder() {
    doppelteLfdNr.addItem(International.getString("Buchstaben anhängen"));
    doppelteLfdNr.addItem(International.getString("Fahrt am Ende hinzufügen"));
    doppelteLfdNr.addItem(International.getString("Fahrt nicht importieren"));
    doppelteLfdNr.setSelectedIndex(2);
  }

  void lfdNrVon_focusLost(FocusEvent e) {
    int i;
    if ( (i = EfaUtil.string2date(lfdNrVon.getText().trim(),0,0,0).tag) != 0)
      lfdNrVon.setText(Integer.toString(i));
    else lfdNrVon.setText("");
  }

  void lfdNrBis_focusLost(FocusEvent e) {
    int i;
    if ( (i = EfaUtil.string2date(lfdNrBis.getText().trim(),0,0,0).tag) != 0)
      lfdNrBis.setText(Integer.toString(i));
    else lfdNrBis.setText("");
  }

  void datumVon_focusLost(FocusEvent e) {
    if (datumVon.getText().trim().equals("")) return;
    Calendar cal = GregorianCalendar.getInstance();
    TMJ c = EfaUtil.correctDate(datumVon.getText().trim(),cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH),cal.get(GregorianCalendar.YEAR));
    datumVon.setText(c.tag+"."+c.monat+"."+c.jahr);
  }

  void datumBis_focusLost(FocusEvent e) {
    if (datumBis.getText().trim().equals("")) return;
    Calendar cal = GregorianCalendar.getInstance();
    TMJ c = EfaUtil.correctDate(datumBis.getText().trim(),cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH),cal.get(GregorianCalendar.YEAR));
    datumBis.setText(c.tag+"."+c.monat+"."+c.jahr);
  }

  void lfdNrAdd_focusLost(FocusEvent e) {
    try {
      int i = Integer.parseInt(lfdNrAdd.getText().trim());
      lfdNrAdd.setText(Integer.toString(i));
    } catch(Exception ee) {
      lfdNrAdd.setText("");
    }
  }

  void characterAdd_focusLost(FocusEvent e) {
    String s = characterAdd.getText().trim();
    if (s.length() > 1) s = s.substring(0,1);
    s = s.toUpperCase();
    if (s.length()>0) {
      char c = s.charAt(0);
      if (c < 'A' || c > 'Z') s = "";
    }
    characterAdd.setText(s);
  }

  void dateiButton_actionPerformed(ActionEvent e) {
    String dat;
    if (Daten.fahrtenbuch != null && !Daten.fahrtenbuch.getFileName().equals("")) {
      dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Fahrtenbuch")),
              International.getString("efa Fahrtenbuch")+" (*.efb)","efb",Daten.fahrtenbuch.getFileName(),false);
    } else {
        dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Fahrtenbuch")),
                International.getString("efa Fahrtenbuch")+" (*.efb)","efb",null,false);
    }
    if (dat != null) {
      quellFb.setText(dat);
      if (!quellFb.getText().endsWith(".") && !quellFb.getText().toUpperCase().endsWith(".EFB"))
        quellFb.setText(quellFb.getText()+".efb");
    }

  }

  void okButton_actionPerformed(ActionEvent e) {
    String quelle = quellFb.getText().trim();
    if (quelle.equals("")) {
      Dialog.infoDialog(International.getString("Keine Quelle ausgewählt"),
              International.getString("Kein zu importierendes Fahrtenbuch ausgewählt."));
      return;
    }
    if (!EfaUtil.canOpenFile(quelle)) {
      Dialog.error(LogString.logstring_fileOpenFailed(quelle, International.getString("Quell-Fahrtenbuch")));
      return;
    }
    Fahrtenbuch q = new Fahrtenbuch(quelle);
    if (!q.readFile()) {
      Dialog.error(LogString.logstring_fileReadFailed(quelle, International.getString("Quell-Fahrtenbuch")));
      return;
    }

    String result =
    doImport(q,Daten.fahrtenbuch,
             lfdNrAdd.getText().trim(),characterAdd.getText().trim(),
             lfdNrVon.getText().trim(),lfdNrBis.getText().trim(),
             datumVon.getText().trim(),datumBis.getText().trim(),
             gruppe.getText().trim(),
             zusatzImport.isSelected(),doppelteLfdNr.getSelectedIndex());
    if (result != null) {
      Dialog.error(result);
    } else {
      Dialog.infoDialog(International.getMessage("{count} Einträge erfolgreich importiert.",anzImportierteFahrten));
    }


    // fertig!
    efaFrame.getAllFahrtDauer();
    efaFrame.datensatzGeaendert=false; // tja... wird von getAllFahrtDauer() auf true gesetzt (weil fahrtDauer.itemStateChanged() ausgelöst wird)
    efaFrame.FirstButton_actionPerformed(null);
    cancel();
  }


  public static String doImport(Fahrtenbuch quellFb, Fahrtenbuch zielFb,
                         String lfdNrAdd, String characterAdd,
                         String lfdNrVon, String lfdNrBis,
                         String datumVon, String datumBis,
                         String gruppe,
                         boolean zusatzImport, int doppelteLfdNr) {

    anzImportierteFahrten = 0;

    // legt fest, ob Namen bzgl. Vor-Nachname-Darstellung umgewandelt werden sollen
    boolean switchName = (zielFb.getDaten().erstVorname != quellFb.getDaten().erstVorname);

    int add = EfaUtil.string2int(lfdNrAdd,0);
    char addC = 0;
    if (characterAdd.length() > 0) {
      addC = characterAdd.toUpperCase().charAt(0);
      if (addC < 'A' || addC > 'Z') addC = 0;
    }

    int vonL = EfaUtil.string2int(lfdNrVon,0);
    int bisL = EfaUtil.string2int(lfdNrBis,0);
    TMJ vonD, bisD;
    GregorianCalendar vonCal = null;
    GregorianCalendar bisCal = null;
    if (!datumVon.equals("")) {
      vonD = EfaUtil.correctDate(datumVon,0,0,0);
      vonCal = new GregorianCalendar(vonD.jahr,vonD.monat-1,vonD.tag);
      vonCal.set(vonD.jahr,vonD.monat-1+vonCal.getMinimum(GregorianCalendar.MONTH),vonD.tag);
    }
    if (!datumBis.equals("")) {
      bisD = EfaUtil.correctDate(datumBis,99,99,9999);
      bisCal = new GregorianCalendar(bisD.jahr,bisD.monat-1,bisD.tag);
      bisCal.set(bisD.jahr,bisD.monat-1+bisCal.getMinimum(GregorianCalendar.MONTH),bisD.tag);
    }

    boolean nurBestimmteNamen = false;
    if (gruppe != null && gruppe.length()>0 && Daten.gruppen != null) {
      Vector v = Daten.gruppen.getGruppenMitglieder(gruppe);
      nurBestimmteNamen = v!=null && v.size()>0;
    }

    DatenFelder d = (DatenFelder)quellFb.getCompleteFirst();
    while (d != null) {
      // Daten (LfdNr und Datum) der aktuellen Fahrt ermitteln
      TMJ dateF = EfaUtil.string2date(d.get(Fahrtenbuch.DATUM),0,0,0);
      GregorianCalendar dateCal = new GregorianCalendar(dateF.jahr,dateF.monat-1,dateF.tag);
      dateCal.set(dateF.jahr,dateF.monat-1+dateCal.getMinimum(GregorianCalendar.MONTH),dateF.tag);
      int lfdnr = EfaUtil.string2date(d.get(Fahrtenbuch.LFDNR),0,0,0).tag;

      // LfdNr und Datum prüfen, ob Fahrt berücksichtigt werden soll
      if (vonL != 0 && lfdnr<vonL) { d = (DatenFelder)quellFb.getCompleteNext(); continue; }
      if (bisL != 0 && lfdnr>bisL) { d = (DatenFelder)quellFb.getCompleteNext(); continue; }
      if (dateCal.before(vonCal) || dateCal.after(bisCal)) { d = (DatenFelder)quellFb.getCompleteNext(); continue; }

      // Mitgliedernamen prüfen, ob Fahrt berücksichtigt werden soll
      if (nurBestimmteNamen) {
        boolean found=false;
        if (isInGroup(d.get(Fahrtenbuch.STM),gruppe,quellFb.getDaten().erstVorname)) found=true;
        for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH && !found; i++) {
          if (isInGroup(d.get(Fahrtenbuch.MANNSCH1+i),gruppe,quellFb.getDaten().erstVorname)) found=true;
        }
        if (!found) { d = (DatenFelder)quellFb.getCompleteNext(); continue; }
      }

      // neue LfdNr basteln
      String s = d.get(Fahrtenbuch.LFDNR);
      if (s.length() > Integer.toString(lfdnr).length())
        s = Integer.toString(lfdnr+add)+s.charAt(s.length()-1);
      else s = Integer.toString(lfdnr+add);
      if (addC != 0) s += Character.toString(addC);

      // ggf. Konflikte mit bestehenden LfdNr vermeiden
      String t;
      while ( (t =zielFb.getExact(s)) != null) {
        switch (doppelteLfdNr) {
          case 0: // Buchstaben anhängen
            char cc = t.charAt(t.length()-1);
            if (cc >= 'A' && cc < 'Z') s = Integer.toString(lfdnr+add)+(++cc);
            else if (cc != 'Z') s = Integer.toString(lfdnr+add)+"A";
            else {
              return International.getString("Zu viele gleiche Laufende Nummern");
            }
            break;
          case 1: // Fahrt hinten anhängen
            DatenFelder dd = (DatenFelder)zielFb.getCompleteLast();
            if (dd != null) {
              TMJ hhmm = EfaUtil.string2date(dd.get(Fahrtenbuch.LFDNR),1,1,1); // TMJ mißbraucht für die Auswertung von Zahlen
              s = Integer.toString(hhmm.tag+1);
            }
            break;
          case 2: // Fahrt nicht importieren
          default:
            s="quit"; // schmutzig!!!! nur, damit diese while-Schleife beendet wird und dann neue Fahrt genommen werden kann...
        }
      }
      if (s.equals("quit")) { d = (DatenFelder)quellFb.getCompleteNext(); continue; }

      d.set(Fahrtenbuch.LFDNR,s);
      if (switchName) {
        d.set(Fahrtenbuch.STM,flipName(quellFb,zielFb,d.get(Fahrtenbuch.STM)));
        for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++) d.set(i,flipName(quellFb,zielFb,d.get(i)));
      }
      zielFb.add(d);
      anzImportierteFahrten++;

      // ggf. Mehrtagesfahrten importieren
      if (d.get(Fahrtenbuch.FAHRTART).length()>0) {
        String fahrtart = d.get(Fahrtenbuch.FAHRTART);
        if (!Mehrtagesfahrt.isVordefinierteFahrtart(fahrtart) && zielFb.getMehrtagesfahrt(fahrtart)==null) {
          // neue, nicht vordefinierte Fahrtart
          zielFb.addMehrtagesfahrt(quellFb.getMehrtagesfahrt(fahrtart));
        }
      }

      // ggf. Zusatzdaten ebenfalls importieren
      if (zusatzImport) {
        if (zielFb.getDaten().boote.getExact(d.get(Fahrtenbuch.BOOT)) == null)
          if (quellFb.getDaten().boote != null && quellFb.getDaten().boote.getExact(d.get(Fahrtenbuch.BOOT)) != null)
            zielFb.getDaten().boote.add((DatenFelder)quellFb.getDaten().boote.getComplete());

        checkAddName(quellFb,zielFb,d.get(Fahrtenbuch.STM));
        for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++) checkAddName(quellFb,zielFb,d.get(i));

        if (zielFb.getDaten().ziele.getExact(d.get(Fahrtenbuch.ZIEL)) == null)
          if (quellFb.getDaten().ziele != null && quellFb.getDaten().ziele.getExact(d.get(Fahrtenbuch.ZIEL)) != null)
            zielFb.getDaten().ziele.add((DatenFelder)quellFb.getDaten().ziele.getComplete());
      }

      // nächste Fahrt...
      d = (DatenFelder)quellFb.getCompleteNext();
    }
    return null;
  }

  static boolean isInGroup(String name, String gruppe, boolean erstVorname) {
    if (Daten.gruppen == null) return false;
    if (name == null || name.length() == 0) return false;
    if (gruppe == null || gruppe.length() == 0) return false;

    String[] n = EfaUtil.zerlegeNamen(name,erstVorname);
    return Daten.gruppen.isInGroup(gruppe,n[0],n[1],n[2]);
  }


  // prüft, ob Mitglied s im Zielfahrtenbuch schon vorhanden ist und fügt ihn ggf. aus Quellfahrtenbuch q hinzu
  static void checkAddName(Fahrtenbuch quellFb, Fahrtenbuch zielFb, String s) {
    // hinzufügen
    if (zielFb.getDaten().mitglieder.getExact(s) == null)
      if (quellFb.getDaten().mitglieder != null && quellFb.getDaten().mitglieder.getExact(s) != null) {
        // Mitglied hinzufügen
        DatenFelder d = (DatenFelder)quellFb.getDaten().mitglieder.getComplete();
        zielFb.getDaten().mitglieder.add(d);

        // überprüfen, ob Status im Fahrtenbuch in der Statusliste schon vorhanden ist
        String status = d.get(Mitglieder.STATUS);
        if (status!=null) {
          boolean found=false;
          for (int i=0; i<zielFb.getDaten().status.length; i++)
            if (status.equals(zielFb.getDaten().status[i])) found=true;
          if (!found) {
            // Status in die Statusliste aufnehmen
            FBDaten fbdaten = zielFb.getDaten();
            String[] statusneu = new String[fbdaten.status.length+1];
            for (int i=0; i<fbdaten.status.length-2; i++) // Kopiere alle bisherigen Status (außer Gast, andere)
              statusneu[i] = fbdaten.status[i];
            statusneu[statusneu.length-3] = status; // füge neuen Status ein
            for (int i=fbdaten.status.length-2; i<fbdaten.status.length; i++) // Kopiere Gast, andere
              statusneu[i+1] = fbdaten.status[i];
            fbdaten.status = statusneu;
            zielFb.setDaten(fbdaten);
          }
        }
      }
  }

  // Namensdarstellung bzgl. Vor-Nach umwandeln
  static String flipName(Fahrtenbuch quellFb, Fahrtenbuch zielFb, String s) {
    if (s == null) return "";
    String[] sn = EfaUtil.zerlegeNamen(s,quellFb.getDaten().erstVorname);
    return EfaUtil.getFullName(sn[0],sn[1],sn[2],zielFb.getDaten().erstVorname);
  }


  // Namen vervollständigen
  void gruppe_keyReleased(KeyEvent e) {
    if (Daten.gruppen == null) return;
    if (gruppen == null) {
      Vector g = Daten.gruppen.getGruppen();
      gruppen = new DatenListe("foo",1,1,false);
      for (int i=0; i<g.size(); i++) {
        DatenFelder d = new DatenFelder(1);
        d.set(0,(String)g.get(i));
        gruppen.add(d);
      }
    }
    EfaFrame.vervollstaendige(gruppe,null,gruppen,e,null,true);
  }

  void gruppe_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

}
