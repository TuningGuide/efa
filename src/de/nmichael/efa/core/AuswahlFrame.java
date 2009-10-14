package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Hashtable;
import java.beans.*;
import java.util.Vector;
import de.nmichael.efa.direkt.BootStatus;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

// @i18n complete

public class AuswahlFrame extends JDialog implements ActionListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JButton SaveButton = new JButton();
  EfaFrame efaFrame;
  BootStatus bootstatus;
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton editBut = new JButton();
  JButton delBut = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable out=null;
  JPanel contentPane = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton addBut = new JButton();
  BorderLayout borderLayout3 = new BorderLayout();

  public static final int MITGLIEDER = 0;
  public static final int BOOTE = 1;
  public static final int ZIELE = 2;
  public static final int MEHRTAGESFAHRTEN = 3;
  public static final int MANNSCHAFTEN = 4;
  public static final int FAHRTENABZEICHEN = 5;
  public static final int GRUPPEN = 6;

  String[][] tabelle=null;
  String[] tabelleTitel=null;
  int datenArt;
  int[] selected;
  boolean firstclick=false;
  JButton createListButton = new JButton();
  JButton updateStatusButton = new JButton();
  JButton getSigBestaetigungenButton = new JButton();


  // Konstruktor
  public AuswahlFrame(EfaFrame parent, int daten) {
    super(parent);
    this.bootstatus = null;
    efaFrame = parent;
    frIni(daten);
  }

  // Konstruktor
  public AuswahlFrame(JDialog parent, int daten) {
    super(parent);
    this.bootstatus = null;
    efaFrame = null;
    frIni(daten);
  }

  // Konstruktor
  public AuswahlFrame(JDialog parent, int daten, BootStatus bootstatus) {
    super(parent);
    efaFrame = null;
    this.bootstatus = bootstatus;
    frIni(daten);
  }

  private void frIni(int daten) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.datenArt = daten;

    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);

    if (daten == MEHRTAGESFAHRTEN) {
      addBut.setVisible(false);
      delBut.setVisible(false);
      createListButton.setVisible(false);
    }
    if (daten != MITGLIEDER) {
      updateStatusButton.setVisible(false);
    }
    if (daten != FAHRTENABZEICHEN) {
      getSigBestaetigungenButton.setVisible(false);
    }
    if (daten == FAHRTENABZEICHEN) {
      if (Daten.javaVersion.startsWith("1.3")) {
        Daten.checkJavaVersion(false);
      }
    }

    speichern();
    ladeDaten();

    SaveButton.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      switch (datenArt) {
        case MITGLIEDER:       Help.getHelp(this,"AuswahlFrame_Mitglieder"); break;
        case BOOTE:            Help.getHelp(this,"AuswahlFrame_Boote"); break;
        case ZIELE:            Help.getHelp(this,"AuswahlFrame_Ziele"); break;
        case MEHRTAGESFAHRTEN: Help.getHelp(this,"AuswahlFrame_Mehrtagesfahrten"); break;
        case MANNSCHAFTEN:     Help.getHelp(this,"AuswahlFrame_Standardmannschaften"); break;
        case FAHRTENABZEICHEN: Help.getHelp(this,"AuswahlFrame_Fahrtenabzeichen"); break;
        case GRUPPEN:          Help.getHelp(this,"AuswahlFrame_Gruppen"); break;
      }

    }
  }


  // Initialisierung
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    switch (datenArt) {
      case MITGLIEDER: this.setTitle(International.getString("Mitglieder")); break;
      case BOOTE: this.setTitle(International.getString("Boote")); break;
      case ZIELE: this.setTitle(International.getString("Ziele")); break;
      case MEHRTAGESFAHRTEN: this.setTitle(International.getString("Mehrtagesfahrten")); break;
      case MANNSCHAFTEN: this.setTitle(International.getString("Standardmannschaften")); break;
      case FAHRTENABZEICHEN: this.setTitle(International.getString("DRV-Fahrtenabzeichen")); break;
      case GRUPPEN: this.setTitle(International.getString("Gruppen")); break;
    }
    SaveButton.setNextFocusableComponent(addBut);
    Mnemonics.setButton(this, SaveButton, International.getStringWithMnemonic("Schließen"));
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveButton_actionPerformed(e);
      }
    });
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout3);
    jPanel2.setLayout(gridBagLayout2);
    editBut.setNextFocusableComponent(delBut);
    Mnemonics.setButton(this, editBut, International.getStringWithMnemonic("Bearbeiten"));
    editBut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editBut_actionPerformed(e);
      }
    });
    delBut.setNextFocusableComponent(createListButton);
    Dialog.setPreferredSize(delBut,100,25);
    Mnemonics.setButton(this, delBut, International.getStringWithMnemonic("Löschen"));
    delBut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        delBut_actionPerformed(e);
      }
    });
    jScrollPane1.setPreferredSize(new Dimension(350, 500));
    contentPane.setLayout(borderLayout2);
    addBut.setMaximumSize(new Dimension(101, 25));
    addBut.setMinimumSize(new Dimension(101, 25));
    addBut.setNextFocusableComponent(editBut);
    Dialog.setPreferredSize(addBut,100,25);
    Mnemonics.setButton(this, addBut, International.getStringWithMnemonic("Hinzufügen"));
    addBut.setMargin(new Insets(2, 10, 2, 10));
    addBut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addBut_actionPerformed(e);
      }
    });
    contentPane.setPreferredSize(new Dimension(600, 525));
    jPanel1.setPreferredSize(new Dimension(500, 500));
    createListButton.setNextFocusableComponent(updateStatusButton);
    Mnemonics.setButton(this, createListButton, International.getStringWithMnemonic("Liste ausgeben"));
    createListButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createListButton_actionPerformed(e);
      }
    });
    updateStatusButton.setNextFocusableComponent(SaveButton);
    Mnemonics.setButton(this, updateStatusButton, International.getStringWithMnemonic("Status aktualisieren"));
    updateStatusButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateStatusButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, getSigBestaetigungenButton, International.getStringWithMnemonic("Bestätigungsdatei abrufen"));
    getSigBestaetigungenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getSigBestaetigungenButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(contentPane, BorderLayout.CENTER);
    contentPane.add(SaveButton, BorderLayout.SOUTH);
    contentPane.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    contentPane.add(jPanel2, BorderLayout.EAST);
    jPanel2.add(delBut, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(editBut, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(addBut, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(createListButton,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(updateStatusButton,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
    jPanel2.add(getSigBestaetigungenButton,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }


  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }


  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  // Datenliste laden und anzeigen
  public void ladeDaten() {
    int tabelleBreite=1, anz=1;

    switch (datenArt) {
      case MITGLIEDER:
        tabelleBreite=3;
        anz = Daten.fahrtenbuch.getDaten().mitglieder.countElements();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Name"); tabelleTitel[1]=International.getString("Jahrgang"); tabelleTitel[2]=International.getString("Status");
        break;
      case BOOTE:
        tabelleBreite=2;
        anz = Daten.fahrtenbuch.getDaten().boote.countElements();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Boot"); tabelleTitel[1]=International.getString("Art");
        break;
      case ZIELE:
        tabelleBreite=4;
        anz = Daten.fahrtenbuch.getDaten().ziele.countElements();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Ziel"); tabelleTitel[1]=International.getString("Kilometer"); tabelleTitel[2]=International.getString("Gewässer"); tabelleTitel[3]=International.getString("Zielbereiche");
        break;
      case MEHRTAGESFAHRTEN:
        tabelleBreite=4;
        anz = Daten.fahrtenbuch.getAnzahlMehrtagesfahrten();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Fahrt"); tabelleTitel[1]=International.getString("Startdatum"); tabelleTitel[2]=International.getString("Enddatum"); tabelleTitel[3]=International.getString("Rudertage");
        break;
      case MANNSCHAFTEN:
        tabelleBreite=1;
        anz = Daten.mannschaften.countElements();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Mannschaft");
        break;
      case FAHRTENABZEICHEN:
        tabelleBreite=3;
        anz = Daten.fahrtenabzeichen.countElements();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Name"); tabelleTitel[1]=International.getString("Anz. Abzeichen"); tabelleTitel[2]=International.getString("Gesamt-Km.");
        break;
      case GRUPPEN:
        tabelleBreite=2;
        anz = Daten.gruppen.getGruppen().size();
        tabelleTitel = new String[tabelleBreite];
        tabelleTitel[0]=International.getString("Gruppe"); tabelleTitel[1]=International.getString("Anz. Mitglieder");
        break;
    }
    tabelle = new String[anz][tabelleBreite];

    DatenFelder f = null;

    // Wenn datenArt == FAHRTENABZEICHEN, dann eine komplette Mitgliederliste einlesen um herausfinden zu könne,
    // ob alle in der Fahrtenabezeichenliste eingetragenen Personen auch in der Mitgliederliste stehen!
    Hashtable alleMitglieder = null;
    String mitglError="";
    if (datenArt == FAHRTENABZEICHEN && Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null) {
      alleMitglieder = new Hashtable();
      for (DatenFelder d = Daten.fahrtenbuch.getDaten().mitglieder.getCompleteFirst(); d != null; d = Daten.fahrtenbuch.getDaten().mitglieder.getCompleteNext()) {
        String name = EfaUtil.getFullName(d.get(Mitglieder.VORNAME), d.get(Mitglieder.NACHNAME), "");
        alleMitglieder.put(name, d.get(Mitglieder.JAHRGANG));
      }

    }

    int i=0;
    if (datenArt != MEHRTAGESFAHRTEN && datenArt != GRUPPEN) do {
      switch (datenArt) {
        case MITGLIEDER:   if (i == 0) f = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getCompleteFirst();
                           else f = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getCompleteNext(); break;
        case BOOTE:        if (i == 0) f = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteFirst();
                           else f = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteNext(); break;
        case ZIELE:        if (i == 0) f = (DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getCompleteFirst();
                           else f = (DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getCompleteNext(); break;
        case MANNSCHAFTEN: if (i == 0) f = (DatenFelder)Daten.mannschaften.getCompleteFirst();
                           else f = (DatenFelder)Daten.mannschaften.getCompleteNext(); break;
        case FAHRTENABZEICHEN: if (i == 0) f = (DatenFelder)Daten.fahrtenabzeichen.getCompleteFirst();
                           else f = (DatenFelder)Daten.fahrtenabzeichen.getCompleteNext(); break;
      }
      if (f != null) {
        switch (datenArt) {
          case MITGLIEDER:
            tabelle[i][0] = EfaUtil.getFullName(f.get(Mitglieder.VORNAME),f.get(Mitglieder.NACHNAME),f.get(Mitglieder.VEREIN));
            tabelle[i][1] = f.get(Mitglieder.JAHRGANG);
            tabelle[i][2] = f.get(Mitglieder.STATUS);
            break;
          case BOOTE:
            if (f.get(Boote.VEREIN).equals("")) tabelle[i][0] = f.get(Boote.NAME);
            else tabelle[i][0] = f.get(Boote.NAME)+" ("+f.get(Boote.VEREIN)+")";
            tabelle[i][1] = f.get(Boote.ART) + "-" + f.get(Boote.RIGGER) + "-" + f.get(Boote.ANZAHL) + " " + f.get(Boote.STM);
            break;
          case ZIELE:
            tabelle[i][0] = f.get(Ziele.NAME);
            tabelle[i][1] = f.get(Ziele.KM);
            tabelle[i][2] = f.get(Ziele.GEWAESSER);
            tabelle[i][3] = f.get(Ziele.BEREICH);
            break;
          case MANNSCHAFTEN:
            tabelle[i][0] = f.get(Mannschaften.BOOT);
            break;
          case FAHRTENABZEICHEN:
            String name = EfaUtil.getFullName(f.get(Fahrtenabzeichen.VORNAME), f.get(Fahrtenabzeichen.NACHNAME), "");
            if (alleMitglieder != null) {
              String jahrgang = (String)alleMitglieder.get(name);
              if (jahrgang == null) mitglError += name + " (" + International.getString("nicht in Mitgliederliste gefunden") + ")\n";
              else if (!jahrgang.equals(f.get(Fahrtenabzeichen.JAHRGANG))) mitglError += name + " (" + International.getString("unterschiedlicher Jahrgang in beiden Listen") +")\n";
            }
            tabelle[i][0] = name;
            tabelle[i][1] = f.get(Fahrtenabzeichen.ANZABZEICHEN);
            tabelle[i][2] = f.get(Fahrtenabzeichen.GESKM);
            break;
        }
        i++;
      }
    } while (f != null);
    else { // Mehrtagestour
      if (datenArt == MEHRTAGESFAHRTEN) {
        String[] fahrten = Daten.fahrtenbuch.getAllMehrtagesfahrtNamen();
        if (fahrten != null) for (int j=0; j<fahrten.length; j++) {
          Mehrtagesfahrt m = Daten.fahrtenbuch.getMehrtagesfahrt(fahrten[j]);
          tabelle[j][0] = m.name;
          tabelle[j][1] = m.start;
          tabelle[j][2] = m.ende;
          tabelle[j][3] = Integer.toString(m.rudertage);
        }
      }
      if (datenArt == GRUPPEN) {
        Vector gruppen = Daten.gruppen.getGruppen();
        for (int j=0; j<gruppen.size(); j++) {
          Vector mitglieder = Daten.gruppen.getGruppenMitglieder((String)gruppen.get(j));
          if (mitglieder != null) {
            tabelle[j][0] = (String)gruppen.get(j);
            tabelle[j][1] = Integer.toString(mitglieder.size());
          }
        }
      }
    }

    zeigeTabelle();

    if (datenArt == FAHRTENABZEICHEN && mitglError.length() > 0) {
      Dialog.error(International.getString("Folgende Personen sind in der Fahrtenabzeichenliste eingetragen, "+
                   "können aber in der aktuellen Mitgliederliste nicht gefunden werden "+
                   "oder sind dort mit anderen Daten eingetragen. "+
                   "Für diese Mitglieder ist keine korrekte Auswertung der DRV-Fahrtenabzeichen möglich!\n"+
                   "Bitte überprüfe die Namen und Jahrgänge der angegebenen Personen:")+"\n\n"+mitglError);
    }
  }

  void zeigeTabelle() {
    out = new JTable(tabelle,tabelleTitel);
    jScrollPane1.getViewport().add(out, null);
//    out.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    out.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    out.setCellEditor(null);
    out.removeEditor();
    out.setColumnSelectionAllowed(false);

    out.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        tabelle_mouseClicked(e);
      }
    });
    out.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        out_propertyChange(e);
      }
    });
  }


  // Einträge aktualisieren
  public void update() {
    if (datenArt != MEHRTAGESFAHRTEN) ladeDaten();
    else zeigeTabelle();
  }


  // Alle geänderten Dateien speichern
  void speichern() {
      String listError = null;
    if (this.datenArt == this.MITGLIEDER && Daten.fahrtenbuch.getDaten().mitglieder.isChanged())
      if (!Daten.fahrtenbuch.getDaten().mitglieder.writeFile())
          listError = International.getString("Mitgliederliste");
    if (this.datenArt == this.BOOTE && Daten.fahrtenbuch.getDaten().boote.isChanged())
      if (!Daten.fahrtenbuch.getDaten().boote.writeFile())
          listError = International.getString("Bootsliste");
    if (this.datenArt == this.BOOTE && Daten.synBoote.isChanged())
      if (!Daten.synBoote.writeFile())
          listError = International.getString("Boots-Synonymliste");
    if (this.datenArt == this.ZIELE && Daten.fahrtenbuch.getDaten().ziele.isChanged()) {
      if (Daten.vereinsConfig != null) Daten.fahrtenbuch.getDaten().ziele.checkAllZielbereiche(Daten.vereinsConfig.zielbereich);
      if (!Daten.fahrtenbuch.getDaten().ziele.writeFile())
          listError = International.getString("Zielliste");
    }
    if (this.datenArt == this.FAHRTENABZEICHEN && Daten.fahrtenabzeichen.isChanged())
      if (!Daten.fahrtenabzeichen.writeFile())
          listError = International.getString("DRV-Fahrtenabezeichenliste");
    if (Daten.fahrtenbuch.isChanged()) {
      if (!Daten.fahrtenbuch.writeFile())
          listError = International.getString("Fahrtenbuch-Datei");
      if (this.datenArt == this.MEHRTAGESFAHRTEN && efaFrame != null) efaFrame.getAllFahrtDauer();
      if (efaFrame != null) efaFrame.datensatzGeaendert=false;
    }
    if (this.datenArt == this.MANNSCHAFTEN && Daten.mannschaften.isChanged())
      if (!Daten.mannschaften.writeFile())
          listError = International.getString("Liste der Standardmannschaften");
    if (this.datenArt == this.GRUPPEN && Daten.gruppen.isChanged())
      if (!Daten.gruppen.writeFile())
          listError = International.getString("Gruppenliste");
    if (listError != null) {
        Dialog.error(International.getMessage("Änderungen an der {list} konnten nicht gespeichert werden!",listError));

    }
  }


  // Schliessen, ggf. speichern
  void cancel() {
    if ( (datenArt == MITGLIEDER && Daten.fahrtenbuch.getDaten().mitglieder != null && Daten.fahrtenbuch.getDaten().mitglieder.isChanged()) ||
         (datenArt == BOOTE && Daten.fahrtenbuch.getDaten().boote != null && Daten.fahrtenbuch.getDaten().boote.isChanged()) ||
         (datenArt == ZIELE && Daten.fahrtenbuch.getDaten().ziele != null && Daten.fahrtenbuch.getDaten().ziele.isChanged()) ||
         (datenArt == MANNSCHAFTEN && Daten.mannschaften != null && Daten.mannschaften.isChanged()) ||
         (datenArt == FAHRTENABZEICHEN && Daten.fahrtenabzeichen != null && Daten.fahrtenabzeichen.isChanged()) ||
         (datenArt == GRUPPEN && Daten.gruppen != null && Daten.gruppen.isChanged()) ||
        Daten.fahrtenbuch.isChanged()) {
      String pos = null;
      try { pos = efaFrame.aktDatensatz.get(Fahrtenbuch.LFDNR); } catch(Exception e) { pos=null; }
      switch(Dialog.yesNoCancelDialog(International.getString("Änderungen speichern?"),International.getString("Sollen alle Änderungen gespeichert werden?"))) {
        case Dialog.YES: { // Speichern
          speichern();
          break;
        }
        case Dialog.NO: { // Nicht speichern
          if (Daten.fahrtenbuch.isChanged()) Daten.fahrtenbuch.readFile();
          if (this.datenArt == this.MITGLIEDER && Daten.fahrtenbuch.getDaten().mitglieder.isChanged()) Daten.fahrtenbuch.getDaten().mitglieder.readFile();
          if (this.datenArt == this.BOOTE && Daten.fahrtenbuch.getDaten().boote.isChanged()) Daten.fahrtenbuch.getDaten().boote.readFile();
          if (this.datenArt == this.ZIELE && Daten.fahrtenbuch.getDaten().ziele.isChanged()) Daten.fahrtenbuch.getDaten().ziele.readFile();
          if (this.datenArt == this.MANNSCHAFTEN && Daten.mannschaften.isChanged()) Daten.mannschaften.readFile();
          if (this.datenArt == this.FAHRTENABZEICHEN && Daten.fahrtenabzeichen.isChanged()) Daten.fahrtenabzeichen.readFile();
          if (this.datenArt == this.GRUPPEN && Daten.gruppen.isChanged()) Daten.gruppen.readFile();
          break;
        }
        default: return;
      }
      if (datenArt == MEHRTAGESFAHRTEN && pos != null && efaFrame != null) {
        efaFrame.geheZuNr(pos);
        efaFrame.getAllFahrtDauer();
        if (efaFrame.aktDatensatz != null) efaFrame.SetFields(efaFrame.aktDatensatz);
      }
//    if (datenArt == MEHRTAGESFAHRTEN) efaFrame.FirstButton_actionPerformed(null);
    }
    Dialog.frameClosed(this);
    dispose();
  }


  // Eintrag bearbeiten mit dem Index "nr" in der Liste "selected"
  // Jedes geöffnete Fenster ruft anschließend doedit(nr+1) auf, bis die gesamte Liste abgearbeitet ist
  void doEdit(int nr) {
    if (selected == null || nr>0 && nr>=selected.length) { update(); return; } // Ende
    String item = (String)tabelle[selected[nr]][0];
    DatenFelder d;
    switch (datenArt) {
      case MITGLIEDER:
         NeuesMitgliedFrame dlg1 = null;
         Daten.fahrtenbuch.getDaten().mitglieder.getExact(item);
         d = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getComplete();
         if (d == null) { doEdit(nr+1); return; }
         dlg1 = new NeuesMitgliedFrame(this,d,false,nr);
         Dialog.setDlgLocation(dlg1,this);
         dlg1.setModal(!Dialog.tourRunning);
         dlg1.show();
         break;
      case BOOTE:
         NeuesBootFrame dlg2 = null;
         Daten.fahrtenbuch.getDaten().boote.getExact(item);
         d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getComplete();
         if (d == null) { doEdit(nr+1); return; }
         dlg2 = new NeuesBootFrame(this,d,false,nr,bootstatus);
         Dialog.setDlgLocation(dlg2,this);
         dlg2.setModal(!Dialog.tourRunning);
         dlg2.show();
         break;
      case ZIELE:
         NeuesZielFrame dlg3 = null;
         Daten.fahrtenbuch.getDaten().ziele.getExact(item);
         d = (DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getComplete();
         if (d == null) { doEdit(nr+1); return; }
         dlg3 = new NeuesZielFrame(this,d,false,nr);
         Dialog.setDlgLocation(dlg3,this);
         dlg3.setModal(!Dialog.tourRunning);
         dlg3.show();
         break;
      case MEHRTAGESFAHRTEN:
         MehrtagestourFrame dlg4 = new MehrtagestourFrame(this,efaFrame,tabelle,selected[nr],nr);
         Dialog.setDlgLocation(dlg4,this);
         dlg4.setModal(!Dialog.tourRunning);
         dlg4.show();
         break;
      case MANNSCHAFTEN:
         MannschaftFrame dlg5 = null;
         Daten.mannschaften.getExact(item);
         d = (DatenFelder)Daten.mannschaften.getComplete();
         if (d == null) { doEdit(nr+1); return; }
         dlg5 = new MannschaftFrame(this,d.get(Mannschaften.BOOT),this,nr);
         Dialog.setDlgLocation(dlg5,this);
         dlg5.setModal(!Dialog.tourRunning);
         dlg5.show();
         break;
      case FAHRTENABZEICHEN:
         NeuesFahrtenabzeichenFrame dlg6 = null;
         item = (Daten.fahrtenbuch.getDaten().erstVorname ? item : EfaUtil.getVorname(item)+" "+EfaUtil.getNachname(item));
         d = Daten.fahrtenabzeichen.getExactComplete(item);
         if (d == null) { doEdit(nr+1); return; }
         dlg6 = new NeuesFahrtenabzeichenFrame(this,d,false,nr);
         Dialog.setDlgLocation(dlg6,this);
         dlg6.setModal(!Dialog.tourRunning);
         dlg6.show();
         break;
      case GRUPPEN:
         NeueGruppeFrame dlg7 = null;
         dlg7 = new NeueGruppeFrame(this,item,false,nr);
         Dialog.setDlgLocation(dlg7,this);
         dlg7.setModal(!Dialog.tourRunning);
         dlg7.show();
//         doEdit(nr+1);
         break;
    }
  }

  void editBut_actionPerformed(ActionEvent e) {
    if (out.getSelectedRow() < 0) return;
    selected = out.getSelectedRows();
    if (selected == null || selected.length == 0) return; // Sicher ist sicher... ;-)
    doEdit(0);
  }


  // Eintrag löschen
  void delBut_actionPerformed(ActionEvent e) {
    if (datenArt == MEHRTAGESFAHRTEN) return;
    if (out.getSelectedRow() < 0) return;
    int[] sel = out.getSelectedRows();
    if (sel == null || sel.length == 0) return; // Sicher ist sicher... ;-)
    String s;
    if (sel.length == 1) s = International.getMessage("Möchtest Du den Eintrag '{entry}' wirklich löschen?",tabelle[out.getSelectedRow()][0]);
    else s = International.getMessage("Möchtest Du wirklich {count} Einträge löschen?",sel.length);
    switch(Dialog.yesNoDialog(International.getString("Warnung"),s)) {
      case Dialog.YES:
        for (int i=0; i<sel.length; i++) {
          s = (String)tabelle[sel[i]][0];
          switch(datenArt) {
            case MITGLIEDER:

              // Alias entfernen
              DatenFelder d=null;
              if (Daten.fahrtenbuch.getDaten().mitglieder.getExact(s) != null)
                d = (DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getComplete();
              if (d != null) Daten.fahrtenbuch.getDaten().mitglieder.removeAlias(d.get(Mitglieder.ALIAS)); // macht nix, falls Alias == ""

              // Mitglied entfernen
              Daten.fahrtenbuch.getDaten().mitglieder.delete(s);

              if (d != null)
                if (Daten.adressen.getExact(d.get(Mitglieder.VORNAME)+" "+d.get(Mitglieder.NACHNAME)) != null)
                  if (Dialog.yesNoDialog(International.getString("Löschen"),
                               International.getMessage("Soll die gespeicherte Anschrift von {firstname} {lastname} gelöscht werden?",
                             d.get(Mitglieder.VORNAME),d.get(Mitglieder.NACHNAME))) == Dialog.YES) {
                    Daten.adressen.delete(d.get(Mitglieder.VORNAME)+" "+d.get(Mitglieder.NACHNAME));
                    Daten.adressen.writeFile();
                  }
              break;
            case BOOTE:
              if (Daten.fahrtenbuch.getDaten().boote.delete(s)) {
              }
              break;
            case ZIELE:
              if (Daten.fahrtenbuch.getDaten().ziele.delete(s)) {
              }
              break;
            case MANNSCHAFTEN:
              if (Daten.mannschaften.delete(s)) {
              }
              break;
            case FAHRTENABZEICHEN:
              if (Daten.fahrtenabzeichen.delete( (Daten.fahrtenbuch.getDaten().erstVorname ? s : EfaUtil.getVorname(s)+" "+EfaUtil.getNachname(s)) )) {
              }
              break;
            case GRUPPEN:
              Daten.gruppen.deleteGruppe(s);
              break;
          }
        }
        update();
        break;
      default: break;
    }
  }


  // unreferenzierte Mehrtagesfahrten löschen
  void deleteUnrefMtour() {
    // alle Mehrtagesfahrten-Namen ermitteln
    String[] a = Daten.fahrtenbuch.getAllMehrtagesfahrtNamen();
    if (a == null) return;
    Vector alleMtours = new Vector();
    for (int i=0; i<a.length; i++) alleMtours.add(a[i]);

    // Fahrtenbuch durchgehen
    DatenFelder d;
    d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    if (d == null) return;
    do {
      // gefundene Mehrtagesfahrten aus dem Vector entfernen
      if (alleMtours.contains(d.get(Daten.fahrtenbuch.FAHRTART))) alleMtours.remove(d.get(Daten.fahrtenbuch.FAHRTART));
    } while( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null);

    // unreferenzierte Mehrtagesfahrten löschen
    if (alleMtours.size()>0) {
      String s = "";
      for (int i=0; i<alleMtours.size(); i++) s += alleMtours.get(i) + "\n";
      if (Dialog.yesNoCancelDialog(International.getString("Unreferenzierte Mehrtagesfahrten"),
                                    International.getString("Es wurden unreferenzierte Mehrtagesfahrten gefunden "+
                                    "(konfigurierte Mehrtagesfahrten, die im Fahrtenbuch nicht verwendet werden):")+
                                    s+
                                    International.getString("Sollen diese Mehrtagesfahrten gelöscht werden?")) == Dialog.YES) {
        for (int i=0; i<alleMtours.size(); i++) Daten.fahrtenbuch.removeMehrtagesfahrt((String)alleMtours.get(i));
        Daten.fahrtenbuch.setChanged();
      }
    }
  }

  // Schließen
  void SaveButton_actionPerformed(ActionEvent e) {
    if (datenArt == MEHRTAGESFAHRTEN && Daten.fahrtenbuch != null) {
      deleteUnrefMtour();
    }

    cancel();
  }

  void addBut_actionPerformed(ActionEvent e) {
    if (datenArt == MEHRTAGESFAHRTEN) return;
    selected = null;
    Point loc;
    Dimension dlgSize;
    Dimension frmSize;
    switch (datenArt) {
      case MITGLIEDER:
         NeuesMitgliedFrame dlg1 = new NeuesMitgliedFrame(this,null,true,0);
         Dialog.setDlgLocation(dlg1,this);
         dlg1.setModal(!Dialog.tourRunning);
         dlg1.show();
         break;
      case BOOTE:
         NeuesBootFrame dlg2 =  new NeuesBootFrame(this,null,true,0,bootstatus);
         Dialog.setDlgLocation(dlg2,this);
         dlg2.setModal(!Dialog.tourRunning);
         dlg2.show();
         break;
      case ZIELE:
         NeuesZielFrame dlg3 = new NeuesZielFrame(this,null,true,0);
         Dialog.setDlgLocation(dlg3,this);
         dlg3.setModal(!Dialog.tourRunning);
         dlg3.show();
         break;
      case MANNSCHAFTEN:
         MannschaftFrame dlg5 = new MannschaftFrame(this,null,this,0);
         Dialog.setDlgLocation(dlg5,this);
         dlg5.setModal(!Dialog.tourRunning);
         dlg5.show();
         break;
      case FAHRTENABZEICHEN:
         NeuesFahrtenabzeichenFrame dlg6 = new NeuesFahrtenabzeichenFrame(this,null,true,0);;
         Dialog.setDlgLocation(dlg6,this);
         dlg6.setModal(!Dialog.tourRunning);
         dlg6.show();
         break;
      case GRUPPEN:
         NeueGruppeFrame dlg7 =  new NeueGruppeFrame(this,null,true,0);
         Dialog.setDlgLocation(dlg7,this);
         dlg7.setModal(!Dialog.tourRunning);
         dlg7.show();
         break;
    }
  }


  // Edit bei Doppelklick
  void tabelle_mouseClicked(MouseEvent e) {
    firstclick=true;
  }

  // komisch, manchmal scheine diese Methode irgendwie nicht zu ziehen.....
  void out_propertyChange(PropertyChangeEvent e) {
    if (out.isEditing()) {
      if (firstclick) editBut_actionPerformed(null);
      firstclick=false;
    }
  }

  // aus 5 Arrays durch Konkatenation ein neues Array erstellen
  String[] createArray(String[] a1, String[] a2, String[] a3, String[] a4, String[] a5) {
    int len = 0;
    if (a1 != null) len += a1.length;
    if (a2 != null) len += a2.length;
    if (a3 != null) len += a3.length;
    if (a4 != null) len += a4.length;
    if (a5 != null) len += a5.length;
    String[] a = new String[len];
    int i = 0;
    if (a1 != null) for (int j=0; j<a1.length; j++) a[i++] = a1[j];
    if (a2 != null) for (int j=0; j<a2.length; j++) a[i++] = a2[j];
    if (a3 != null) for (int j=0; j<a3.length; j++) a[i++] = a3[j];
    if (a4 != null) for (int j=0; j<a4.length; j++) a[i++] = a4[j];
    if (a5 != null) for (int j=0; j<a5.length; j++) a[i++] = a5[j];
    return a;
  }

  void createListButton_actionPerformed(ActionEvent e) {
    if (datenArt == MEHRTAGESFAHRTEN) return;
    ListenausgabeFrame dlg = null;
    switch (datenArt) {
      case MITGLIEDER:
         String[] felder1 = { International.getString("Vorname"),
                              International.getString("Nachname"),
                              International.getString("Eingabekürzel"),
                              International.getString("Jahrgang"),
                              International.getString("Geschlecht"),
                              International.getString("Status"),
                              International.getString("Verein"),
                              International.getString("Behinderung"),
                              International.getString("Mitgliedsnummer"),
                              International.getString("Paßwort"),
                              International.getString("Frei 1"),
                              International.getString("Frei 2"),
                              International.getString("Frei 3"),
                              International.getString("Für Wettbewerbe melden")
         };
         boolean[] select1 = {  true   ,  true     ,  false         ,  true     ,  true       ,  true   ,  true   ,  false       , true             , false    , false   , false   , false   , false};
         String[] nur1 = createArray(Daten.fahrtenbuch.getDaten().status,Daten.bezeichnungen.geschlecht.toArray(),null,null,null);
         int[] nurCheck1 = { 4 , 5 };
         if (Daten.fahrtenbuch.getDaten().mitglieder != null)
           dlg = new ListenausgabeFrame(this,International.getString("Mitgliederliste"),Daten.fahrtenbuch.getDaten().mitglieder,felder1,select1,1,nur1,nurCheck1);
         break;
      case BOOTE:
         String[] felder2 = { International.getString("Bootsname"),
                              International.getString("Verein"),
                              International.getString("Art"),
                              International.getString("Anzahl"),
                              International.getString("Riggerung"),
                              International.getString("Steuermann"),
                              International.getString("erlaubte Gruppen"),
                              International.getString("max. nicht in Gruppe"),
                              International.getString("mind. 1 aus Gruppe"),
                              International.getString("Frei 1"),
                              International.getString("Frei 2"),
                              International.getString("Frei 3")
         };
         boolean[] select2 = {  true     ,  true   , true ,  true   ,  true      ,  true       , false             , false                 , false               , false   , false   , false };
         String[] tmp = { International.getString("eigene Boote"),
                          International.getString("fremde Boote")
         };
         String[] nur2 = createArray(tmp,Daten.bezeichnungen.bArt.toArray(), Daten.bezeichnungen.bAnzahl.toArray(), Daten.bezeichnungen.bRigger.toArray(), Daten.bezeichnungen.bStm.toArray());
         int[] nurCheck2 = { 1, 2, 3, 4 , 5 };
         if (Daten.fahrtenbuch.getDaten().boote != null)
           dlg = new ListenausgabeFrame(this,International.getString("Bootsliste"),Daten.fahrtenbuch.getDaten().boote,felder2,select2,0,nur2,nurCheck2);
         break;
      case ZIELE:
         String[] felder3 = { International.getString("Ziel"),
                              International.getString("Kilometer"),
                              International.getString("Zielbereich"),
                              International.getString("Steg-Ziel"),
                              International.getString("Gewässer")
         };
         boolean[] select3 = { true ,  true      ,  true        ,  false     ,  true};
         if (Daten.fahrtenbuch.getDaten().ziele != null)
           dlg = new ListenausgabeFrame(this,International.getString("Zielliste"),Daten.fahrtenbuch.getDaten().ziele,felder3,select3,0,null,null);
         break;
      case MANNSCHAFTEN:
         String[] felder5 = { International.getString("Mannschaft"),
                              International.getString("Steuermann"),
                              International.getString("Mannschaft") + " 1",
                              International.getString("Mannschaft") + " 2",
                              International.getString("Mannschaft") + " 3",
                              International.getString("Mannschaft") + " 4",
                              International.getString("Mannschaft") + " 5",
                              International.getString("Mannschaft") + " 6",
                              International.getString("Mannschaft") + " 7",
                              International.getString("Mannschaft") + " 8",
                              International.getString("Mannschaft") + " 9",
                              International.getString("Mannschaft") + " 10",
                              International.getString("Mannschaft") + " 11",
                              International.getString("Mannschaft") + " 12",
                              International.getString("Mannschaft") + " 13",
                              International.getString("Mannschaft") + " 14",
                              International.getString("Mannschaft") + " 15",
                              International.getString("Mannschaft") + " 16",
                              International.getString("Mannschaft") + " 17",
                              International.getString("Mannschaft") + " 18",
                              International.getString("Mannschaft") + " 19",
                              International.getString("Mannschaft") + " 20",
                              International.getString("Mannschaft") + " 21",
                              International.getString("Mannschaft") + " 22",
                              International.getString("Mannschaft") + " 23",
                              International.getString("Mannschaft") + " 24",
                              International.getString("Ziel"),
                              International.getString("Fahrtart")
         };
         boolean[] select5 = { true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true };
         if (Daten.mannschaften != null)
           dlg = new ListenausgabeFrame(this,International.getString("Standardmannschaftsliste"),Daten.mannschaften,felder5,select5,0,null,null);
         break;
      case FAHRTENABZEICHEN:
         String[] felder6 = { International.getString("Vorname"),
                              International.getString("Nachname"),
                              International.getString("Jahrgang"),
                              International.getString("Anz. Abzeichen"),
                              International.getString("Gesamt-Km"),
                              International.getString("Anz. Abzeichen AB"),
                              International.getString("Gesamt-Km AB"),
                              International.getString("elektronisches Fahrtenheft")
         };
         boolean[] select6 = {  true   ,  true     ,  true     ,  true           ,  true       ,  false             , false          , false           };
         if (Daten.fahrtenabzeichen != null)
           dlg = new ListenausgabeFrame(this,International.getString("DRV-Fahrtenabzeichenliste"),Daten.fahrtenabzeichen,felder6,select6,1,null,null);
         break;
      case GRUPPEN:
         String[] felder7 = { International.getString("Gruppe"),
                              International.getString("Mitglieder")
         };
         boolean[] select7 = {  true  ,  true        };
         if (Daten.gruppen != null) {
           // virtuelle Datenliste erzeugen
           DatenListe dl = new DatenListe("",2,1,false);
           Vector gruppen = Daten.gruppen.getGruppen();
           for (int j=0; j<gruppen.size(); j++) {
             Vector mitglieder = Daten.gruppen.getGruppenMitglieder((String)gruppen.get(j));
             String mitgl = "";
             if (mitglieder != null) {
               for (int k=0; k<mitglieder.size(); k++) {
                 GruppenMitglied m = (GruppenMitglied)mitglieder.get(k);
                 mitgl += EfaUtil.getFullName(m.vorname,m.nachname,m.verein) + (k+1<mitglieder.size() ? "<br>" : "");
               }
               DatenFelder d = new DatenFelder(2);
               d.set(0,(String)gruppen.get(j));
               d.set(1,mitgl);
               dl.add(d);
             }
           }
           dlg = new ListenausgabeFrame(this,International.getString("Gruppenliste"),dl,felder7,select7,0,null,null);
         }
         break;
    }
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void updateStatusButton_actionPerformed(ActionEvent e) {
    if (datenArt != MITGLIEDER) return;
    StatusUpdateFrame dlg = new StatusUpdateFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void getSigBestaetigungenButton_actionPerformed(ActionEvent e) {
    if (datenArt != FAHRTENABZEICHEN) return;
    if (DRVSignaturFrame.getSignierteFahrtenhefte()) update();
  }


}
