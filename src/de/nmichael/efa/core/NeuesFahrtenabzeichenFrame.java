/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

// @i18n complete (needs no internationalization -- only relevant for Germany)

public class NeuesFahrtenabzeichenFrame extends JDialog implements ActionListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JButton SaveButton = new JButton();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextField vorname = new JTextField();
  JTextField nachname = new JTextField();
  JTextField jahrgang = new JTextField();// EfaFrame
  JLabel jLabel7 = new JLabel();
  JTextField gesKm = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField anzAbzeichen = new JTextField();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel4 = new JLabel();
  JLabel lm_jahr = new JLabel();
  JLabel lm_signaturOk = new JLabel();
  JButton lm_editButton = new JButton();
  TitledBorder titledBorder1;

  AuswahlFrame auswahlFrame;
  int editnr;
  DatenFelder d;
  DRVSignatur drvSignatur = null;
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JTextField anzAbzeichenAB = new JTextField();
  JTextField gesKmAB = new JTextField();


  void startNeuerEintrag() {
    d = null; // neuer Eintrag
    vorname.setText("");
    nachname.setText("");
    jahrgang.setText("");
    anzAbzeichen.setText("0");
    gesKm.setText("0");
    anzAbzeichenAB.setText("0");
    gesKmAB.setText("0");
    lm_jahr.setText("- keine Meldung vorhanden -");
    lm_signaturOk.setText("");
    vorname.requestFocus();
  }

  void editiereEintrag(DatenFelder d) {
    if (d == null) { // sollte eigentlich nicht passieren
      Logger.log(Logger.ERROR,"NeuesFahrtenabzeichenFrame.editiereEintrag(null): Eintrag nicht gefunden --- diesen Fehler bitte an software@nmichael.de melden! Danke!");
      cancel();
      return;
    }
    this.d = d; // vorhandenen Eintrag ändern
    vorname.setText(d.get(Fahrtenabzeichen.VORNAME));
    nachname.setText(d.get(Fahrtenabzeichen.NACHNAME));
    jahrgang.setText(d.get(Fahrtenabzeichen.JAHRGANG));
    anzAbzeichen.setText(d.get(Fahrtenabzeichen.ANZABZEICHEN));
    gesKm.setText(d.get(Fahrtenabzeichen.GESKM));
    anzAbzeichenAB.setText(d.get(Fahrtenabzeichen.ANZABZEICHENAB));
    gesKmAB.setText(d.get(Fahrtenabzeichen.GESKMAB));
    if (d.get(Fahrtenabzeichen.LETZTEMELDUNG).length()>0) {
      try {
        drvSignatur = new DRVSignatur(d.get(Fahrtenabzeichen.LETZTEMELDUNG));
      } catch(Exception e) {
        drvSignatur = null;
      }
    } else {
      drvSignatur = null;
    }
    setSignatureState(drvSignatur);

    anzAbzeichen.setEditable(drvSignatur == null);
    gesKm.setEditable(drvSignatur == null);
    anzAbzeichenAB.setEditable(drvSignatur == null);
    gesKmAB.setEditable(drvSignatur == null);

    vorname.requestFocus();
  }



  // Konstruktor (aus AuswahlFrame)
  public NeuesFahrtenabzeichenFrame(AuswahlFrame f, DatenFelder d, boolean neu, int editnr) {
    super(f);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    auswahlFrame = f;
    this.editnr = editnr;
    if (neu) startNeuerEintrag();
    else editiereEintrag(d);
    EfaUtil.pack(this);
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

    titledBorder1 = new TitledBorder("");
    SaveButton.setNextFocusableComponent(vorname);
    SaveButton.setMnemonic('S');
    SaveButton.setText("Speichern");
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveButton_actionPerformed(e);
      }
    });
    this.setTitle("Fahrtenabzeichen bearbeiten");
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setDisplayedMnemonic('V');
    jLabel1.setLabelFor(vorname);
    jLabel1.setText("Vorname: ");
    jLabel2.setDisplayedMnemonic('N');
    jLabel2.setLabelFor(nachname);
    jLabel2.setText("Nachname: ");
    jLabel3.setDisplayedMnemonic('J');
    jLabel3.setLabelFor(jahrgang);
    jLabel3.setText("Jahrgang: ");
    vorname.setNextFocusableComponent(nachname);
    vorname.setPreferredSize(new Dimension(200, 19));
    vorname.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        vorname_focusLost(e);
      }
    });
    vorname.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        name_keyReleased(e,vorname);
      }
    });
    nachname.setNextFocusableComponent(jahrgang);
    nachname.setPreferredSize(new Dimension(200, 19));
    nachname.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nachname_focusLost(e);
      }
    });
    nachname.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        name_keyReleased(e,nachname);
      }
    });
    jahrgang.setNextFocusableComponent(anzAbzeichen);
    jahrgang.setPreferredSize(new Dimension(200, 19));
    jahrgang.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        jahrgang_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        jahrgang_focusGained(e);
      }
    });
    jLabel7.setDisplayedMnemonic('K');
    jLabel7.setLabelFor(gesKm);
    jLabel7.setText("Insgesamt bereits nachgewiesene Kilometer: ");
    gesKm.setNextFocusableComponent(anzAbzeichenAB);
    gesKm.setPreferredSize(new Dimension(200, 19));
    gesKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gesKm_focusLost(e);
      }
    });
    jLabel8.setToolTipText("Kurzname für diese Person");
    jLabel8.setDisplayedMnemonic('A');
    jLabel8.setLabelFor(anzAbzeichen);
    jLabel8.setText("Anzahl der bereits erfüllten Abzeichen: ");
    anzAbzeichen.setNextFocusableComponent(gesKm);
    anzAbzeichen.setPreferredSize(new Dimension(200, 19));
    anzAbzeichen.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        anzAbzeichen_focusLost(e);
      }
    });
    jPanel2.setLayout(gridBagLayout2);
    jLabel4.setText("Letzte elektronische Meldung: ");
    this.setSignatureState(null);
    lm_editButton.setNextFocusableComponent(SaveButton);
    lm_editButton.setMnemonic('B');
    lm_editButton.setText("elektronisches Fahrtenheft anzeigen/bearbeiten");
    lm_editButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        lm_editButton_actionPerformed(e);
      }
    });
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    lm_jahr.setForeground(Color.black);
    lm_jahr.setText("-keine Meldung vorhanden-");
    jLabel5.setDisplayedMnemonic('H');
    jLabel5.setLabelFor(anzAbzeichenAB);
    jLabel5.setText("... davon Abzeichen in den Jugend-Gruppen A/B: ");
    jLabel6.setDisplayedMnemonic('L');
    jLabel6.setLabelFor(gesKmAB);
    jLabel6.setText("... davon Kilometer in den Jugend-Gruppen A/B: ");
    anzAbzeichenAB.setNextFocusableComponent(gesKmAB);
    anzAbzeichenAB.setPreferredSize(new Dimension(200, 19));
    anzAbzeichenAB.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        anzAbzeichenAB_focusLost(e);
      }
    });
    gesKmAB.setNextFocusableComponent(lm_editButton);
    gesKmAB.setPreferredSize(new Dimension(200, 19));
    gesKmAB.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gesKmAB_focusLost(e);
      }
    });
    this.getContentPane().add(SaveButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(vorname,      new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(nachname,      new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel2,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel3,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jahrgang,         new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel7,         new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(gesKm,        new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel8,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(anzAbzeichen,       new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jPanel2,        new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel4,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    jPanel2.add(lm_jahr,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    jPanel2.add(lm_signaturOk,     new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    jPanel2.add(lm_editButton,    new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    jPanel1.add(jLabel5,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel6,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(anzAbzeichenAB,  new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(gesKmAB,  new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
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
    if (editnr>0) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  // Speichern & Schliessen
  void SaveButton_actionPerformed(ActionEvent e) {
    if (this.vorname.getText().trim().length() == 0) {
      Dialog.error("Bitte gib Vornamen ein!");
      this.vorname.requestFocus();
      return;
    }
    if (this.nachname.getText().trim().length() == 0) {
      Dialog.error("Bitte gib Nachnamen ein!");
      this.nachname.requestFocus();
      return;
    }

    DatenFelder d_neu = new DatenFelder(Fahrtenabzeichen._ANZ);
    d_neu.set(Fahrtenabzeichen.VORNAME,EfaUtil.removeSepFromString(vorname.getText().trim()));
    d_neu.set(Fahrtenabzeichen.NACHNAME,EfaUtil.removeSepFromString(nachname.getText().trim()));
    d_neu.set(Fahrtenabzeichen.JAHRGANG,EfaUtil.removeSepFromString(jahrgang.getText().trim()));
    d_neu.set(Fahrtenabzeichen.ANZABZEICHEN,EfaUtil.removeSepFromString(anzAbzeichen.getText().trim()));
    d_neu.set(Fahrtenabzeichen.GESKM,EfaUtil.removeSepFromString(gesKm.getText().trim()));
    d_neu.set(Fahrtenabzeichen.ANZABZEICHENAB,EfaUtil.removeSepFromString(anzAbzeichenAB.getText().trim()));
    d_neu.set(Fahrtenabzeichen.GESKMAB,EfaUtil.removeSepFromString(gesKmAB.getText().trim()));
    d_neu.set(Fahrtenabzeichen.LETZTEMELDUNG,(drvSignatur != null ? drvSignatur.toString() : ""));

    if (d == null && Daten.fahrtenabzeichen.getExact(Daten.fahrtenabzeichen.constructKey(d_neu)) != null) {
      Dialog.infoDialog("Fehler","Es existiert bereits ein Eintrag gleichen Namens!");
      return;
    }

    if (d != null) Daten.fahrtenabzeichen.delete(Daten.fahrtenabzeichen.constructKey(d));

    Daten.fahrtenabzeichen.add(d_neu);

    auswahlFrame.doEdit(editnr+1);
    editnr = 0;
    cancel();
  }

  void setSignatureState(DRVSignatur sig) {
    if (sig == null) {
      lm_signaturOk.setText("");
      lm_jahr.setText("- keine Meldung vorhanden -");
      return;
    }
    switch(sig.getSignatureState()) {
      case DRVSignatur.SIG_VALID:
        lm_signaturOk.setText("Die DRV-Signatur des Fahrtenhefts ist gültig!");
        lm_signaturOk.setForeground(Daten.colorGreen);
        break;
      case DRVSignatur.SIG_INVALID:
        lm_signaturOk.setText("Die DRV-Signatur des Fahrtenhefts ist ungültig!");
        lm_signaturOk.setForeground(Color.red);
        break;
      default:
        lm_signaturOk.setText("Die DRV-Signatur des Fahrtenhefts kann nicht geprüft werden!");
        lm_signaturOk.setForeground(Color.red);
        break;
    }
    if (drvSignatur.getVersion() < 3) lm_jahr.setText(Integer.toString(drvSignatur.getJahr()));
    else lm_jahr.setText(drvSignatur.getJahr() + " (" + drvSignatur.getLetzteKm() + " Km)");
  }

  void vorname_focusLost(FocusEvent e) {
    vorname.setText(vorname.getText().trim());
  }

  void nachname_focusLost(FocusEvent e) {
    nachname.setText(nachname.getText().trim());
  }

  void jahrgang_focusGained(FocusEvent e) {
    if (jahrgang.getText().trim().length() == 0 &&
        vorname.getText().trim().length() > 0 && nachname.getText().trim().length() > 0 &&
        Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null) {
      DatenFelder d = Daten.fahrtenbuch.getDaten().mitglieder.getExactComplete(EfaUtil.getFullName(vorname.getText().trim(),nachname.getText().trim(),""));
      if (d != null) jahrgang.setText(d.get(Mitglieder.JAHRGANG));
    }
  }

  void jahrgang_focusLost(FocusEvent e) {
    int jahr = EfaUtil.string2date(jahrgang.getText(),-1,0,0).tag;
    if (jahr<0) {
      jahrgang.setText("");
      return;
    }
    if (jahr<1900) jahr += 1900;
    jahrgang.setText(Integer.toString(jahr));
  }

  void anzAbzeichen_focusLost(FocusEvent e) {
    anzAbzeichen.setText(Integer.toString(EfaUtil.string2date(anzAbzeichen.getText(),0,0,0).tag));
  }

  void gesKm_focusLost(FocusEvent e) {
    gesKm.setText(Integer.toString(EfaUtil.string2date(gesKm.getText(),0,0,0).tag));
  }

  void anzAbzeichenAB_focusLost(FocusEvent e) {
    anzAbzeichenAB.setText(Integer.toString(EfaUtil.string2date(anzAbzeichenAB.getText(),0,0,0).tag));
  }

  void gesKmAB_focusLost(FocusEvent e) {
    gesKmAB.setText(Integer.toString(EfaUtil.string2date(gesKmAB.getText(),0,0,0).tag));
  }

  void lm_editButton_actionPerformed(ActionEvent e) {
    this.drvSignatur = DRVSignaturFrame.showDlg(this,this.drvSignatur);
    this.setSignatureState(this.drvSignatur);
    anzAbzeichen.setEditable(drvSignatur == null);
    gesKm.setEditable(drvSignatur == null);
    anzAbzeichenAB.setEditable(drvSignatur == null);
    gesKmAB.setEditable(drvSignatur == null);
    if (drvSignatur != null) {
      if (vorname.getText().trim().length() == 0) vorname.setText(drvSignatur.getVorname());
      if (nachname.getText().trim().length() == 0) nachname.setText(drvSignatur.getNachname());
      if (jahrgang.getText().trim().length() == 0) jahrgang.setText(drvSignatur.getJahrgang());
      anzAbzeichen.setText(Integer.toString(drvSignatur.getAnzAbzeichen()));
      gesKm.setText(Integer.toString(drvSignatur.getGesKm()));
      anzAbzeichenAB.setText(Integer.toString(drvSignatur.getAnzAbzeichenAB()));
      gesKmAB.setText(Integer.toString(drvSignatur.getGesKmAB()));
    }
    EfaUtil.pack(this);
  }

  DatenListe makeNamensliste(boolean vornamen, boolean nachnamen, String vorname, String nachname) {
    if (Daten.fahrtenbuch == null) return null;
    DatenListe m = Daten.fahrtenbuch.getDaten().mitglieder;
    if (m == null) return null;
    DatenListe m2 = new DatenListe(Daten.efaTmpDirectory+"dummy",Mitglieder._ANZAHL,1,false);
    int FIELD = Mitglieder.VORNAME; // immer als Vornamen hinzufügen, da sonst das Verfollständigen nicht funktioniert
    for (DatenFelder d = m.getCompleteFirst(); d != null; d = m.getCompleteNext()) {
      DatenFelder d2 = null;
      if (vornamen && (nachname == null || nachname.equals(d.get(Mitglieder.NACHNAME)))) {
        if (d2 == null) d2 = new DatenFelder(Mitglieder._ANZAHL);
        d2.set(FIELD,d.get(Mitglieder.VORNAME));
      }
      if (nachnamen && (vorname == null || vorname.equals(d.get(Mitglieder.VORNAME)))) {
        if (d2 == null) d2 = new DatenFelder(Mitglieder._ANZAHL);
        d2.set(FIELD,d.get(Mitglieder.NACHNAME));
      }
      if (d2 != null) m2.add(d2);
    }
    return m2;
  }

  void name_keyReleased(KeyEvent e, JTextField field) {
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().mitglieder == null) return;
    if (field == vorname) {
      String auswahl = (nachname.getText().trim().length() > 0 ? nachname.getText().trim() : null);
      EfaFrame.vervollstaendige(field,null,makeNamensliste(true,false,null,auswahl),e,null,false);
    } else {
      String auswahl = (vorname.getText().trim().length() > 0 ? vorname.getText().trim() : null);
      EfaFrame.vervollstaendige(field,null,makeNamensliste(false,true,auswahl,null),e,null,false);
    }
  }

}
