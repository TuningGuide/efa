package de.nmichael.efa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class NeuesMitgliedFrame extends JDialog implements ActionListener {
  EfaFrame efaFrame;
  AuswahlFrame auswahlFrame;
  boolean neu;
  boolean directMode;
  boolean efamain; // ob aus efa-Hauptformular aufgerufen
  String oldKey;
  String oldAlias;
  int editnr;
  Vector gruppen = null;
  Vector gruppenCombos = null;
  boolean gruppenChanged = false;

  BorderLayout borderLayout1 = new BorderLayout();
  JButton SaveButton = new JButton();
  JPanel mitglDataPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextField vorname = new JTextField();
  JTextField nachname = new JTextField();
  JTextField verein = new JTextField();// EfaFrame
  JTextField efaFeld;
  JButton efaButton;
  JLabel jLabel4 = new JLabel();
  JComboBox status1 = new JComboBox();
  JLabel jLabel5 = new JLabel();
  JComboBox geschlecht = new JComboBox();
  JLabel jLabel7 = new JLabel();
  JTextField jahrgang = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField alias = new JTextField();
  JLabel jLabel6 = new JLabel();
  JCheckBox behinderung = new JCheckBox();
  JLabel jLabel9 = new JLabel();
  JLabel jLabel10 = new JLabel();
  JTextField mitgliedsnummer = new JTextField();
  JTextField passwort = new JTextField();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel weitereDatenPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JTextField frei1 = new JTextField();
  JTextField frei2 = new JTextField();
  JTextField frei3 = new JTextField();
  JCheckBox wettbewerbe = new JCheckBox();
  JPanel gruppenPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel14 = new JLabel();
  JButton weitereGruppeButton = new JButton();


  void startNeuesMitglied(String name) {
    // �berpr�fen, ob wirklich noch nicht vorhanden
    if (Daten.fahrtenbuch.getDaten().mitglieder.getExact(name) != null) {
      editiereMitglied((DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getComplete());
      return;
    }

    neu = true; // neuer Eintrag
    oldKey = "";
    oldAlias = "";
    vorname.setText(EfaUtil.getVorname(name));
    nachname.setText(EfaUtil.getNachname(name));
    verein.setText(EfaUtil.getVerein(name));
    if (!EfaUtil.getVerein(name).equals("")) status1.setSelectedIndex(Daten.fahrtenbuch.getDaten().status.length-2);
    vorname.requestFocus();
    this.setTitle("Neues Mitglied hinzuf�gen");
    iniGruppen();
    SaveButton.setText("Person hinzuf�gen");
  }

  void editiereMitglied(DatenFelder d) {
    if (d == null) { // sollte eigentlich nicht passieren
      Logger.log(Logger.ERROR,"NeuesMitgliedFrame.editiereMitglied(null): Mitglied nicht gefunden --- diesen Fehler bitte an software@nmichael.de melden! Danke!");
      cancel();
      return;
    }
    neu = false; // Eintrag �ndern
    oldKey = EfaUtil.getFullName(d.get(Mitglieder.VORNAME),
                     d.get(Mitglieder.NACHNAME),d.get(Daten.fahrtenbuch.getDaten().mitglieder.VEREIN));
    oldAlias = d.get(Mitglieder.ALIAS);
    // Bugfix: Kann passieren, wenn in EfaFrame der Button noch gr�n ist, inzwischen das Mitglied aber gel�scht wurde
    if (oldKey == null) { // @neu
      oldKey = "";
      oldAlias = "";
      neu = true;
    }
    vorname.setText(d.get(Mitglieder.VORNAME));
    nachname.setText(d.get(Mitglieder.NACHNAME));
    alias.setText(d.get(Mitglieder.ALIAS));
    jahrgang.setText(d.get(Mitglieder.JAHRGANG));
    geschlecht.setSelectedItem(d.get(Mitglieder.GESCHLECHT));
    status1.setSelectedItem(d.get(Mitglieder.STATUS));
    verein.setText(d.get(Mitglieder.VEREIN));
    behinderung.setSelected(d.get(Mitglieder.BEHINDERUNG).equals("+"));
    mitgliedsnummer.setText(d.get(Mitglieder.MITGLNR));
    passwort.setText(d.get(Mitglieder.PASSWORT));
    wettbewerbe.setSelected(!Mitglieder.isKmwettMelden(d));
    frei1.setText(d.get(Mitglieder.FREI1));
    frei2.setText(d.get(Mitglieder.FREI2));
    frei3.setText(d.get(Mitglieder.FREI3));
    vorname.requestFocus();
    this.setTitle("Mitglied bearbeiten");
    iniGruppen();
    SaveButton.setText("Eintrag �bernehmen");
  }



  // Konstruktor (aus EfaFrame)
  public NeuesMitgliedFrame(EfaFrame parent, String name, JTextField feld, JButton button, boolean neu, boolean directMode) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    this.directMode = directMode;
    try {
      jbInit();
      if (directMode) {
        this.jLabel10.setVisible(false);
        this.passwort.setVisible(false);
        this.wettbewerbe.setVisible(false);
        this.jTabbedPane1.remove(weitereDatenPanel);
        this.jTabbedPane1.remove(gruppenPanel);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);

    efamain = true;
    efaFrame = parent;
    efaFeld = feld;
    efaButton = button;

    if (neu) startNeuesMitglied(name);
    else {
      if (Daten.fahrtenbuch.getDaten().mitglieder.getExact(name) != null)
        editiereMitglied((DatenFelder)Daten.fahrtenbuch.getDaten().mitglieder.getComplete());
      else {
        // Bugfix: Kann passieren, wenn in EfaFrame der Button noch gr�n ist, inzwischen das Mitglied aber gel�scht wurde
        startNeuesMitglied(name);
      }
    }
  }


  // Konstruktor (aus AuswahlFrame)
  public NeuesMitgliedFrame(AuswahlFrame f, DatenFelder d, boolean neu, int editnr) {
    super(f);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);

    efamain = false;
    auswahlFrame = f;
    this.editnr = editnr;
    if (neu) startNeuesMitglied("");
    else editiereMitglied(d);
  }

  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
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

    SaveButton.setNextFocusableComponent(vorname);
    SaveButton.setMnemonic('H');
    SaveButton.setText("Person hinzuf�gen");
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveButton_actionPerformed(e);
      }
    });
    this.setTitle("Neues Mitglied hinzuf�gen");
    this.getContentPane().setLayout(borderLayout1);
    mitglDataPanel.setLayout(gridBagLayout1);
    jLabel1.setDisplayedMnemonic('V');
    jLabel1.setLabelFor(vorname);
    jLabel1.setText("Vorname: ");
    jLabel2.setDisplayedMnemonic('N');
    jLabel2.setLabelFor(nachname);
    jLabel2.setText("Nachname: ");
    jLabel3.setDisplayedMnemonic('E');
    jLabel3.setLabelFor(verein);
    jLabel3.setText("falls ausw�rtig, Verein: ");
    vorname.setNextFocusableComponent(nachname);
    Dialog.setPreferredSize(vorname,200,19);
    vorname.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        vorname_focusLost(e);
      }
    });
    nachname.setNextFocusableComponent(verein);
    Dialog.setPreferredSize(nachname,200,19);
    nachname.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nachname_focusLost(e);
      }
    });
    verein.setNextFocusableComponent(alias);
    Dialog.setPreferredSize(verein,200,19);
    verein.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        verein_focusLost(e);
      }
    });
    jLabel4.setDisplayedMnemonic('S');
    jLabel4.setLabelFor(status1);
    jLabel4.setText("Status: ");
    status1.setNextFocusableComponent(behinderung);
    Dialog.setPreferredSize(status1,200,19);
    jLabel5.setDisplayedMnemonic('G');
    jLabel5.setLabelFor(geschlecht);
    jLabel5.setText("Geschlecht: ");
    geschlecht.setNextFocusableComponent(status1);
    Dialog.setPreferredSize(geschlecht,200,19);
    jLabel7.setDisplayedMnemonic('J');
    jLabel7.setLabelFor(jahrgang);
    jLabel7.setText("Jahrgang: ");
    jahrgang.setNextFocusableComponent(geschlecht);
    Dialog.setPreferredSize(jahrgang,200,19);
    jahrgang.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        jahrgang_focusLost(e);
      }
    });
    jLabel8.setToolTipText("Kurzname f�r diese Person");
    jLabel8.setDisplayedMnemonic('A');
    jLabel8.setLabelFor(alias);
    jLabel8.setText("Eingabe-K�rzel: ");
    alias.setNextFocusableComponent(jahrgang);
    Dialog.setPreferredSize(alias,200,19);
    alias.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        alias_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        alias_focusGained(e);
      }
    });
    jLabel6.setText("Behinderung: ");
    behinderung.setNextFocusableComponent(mitgliedsnummer);
    behinderung.setMnemonic('B');
    behinderung.setText("50% oder mehr Behinderung");
    jLabel9.setDisplayedMnemonic('M');
    jLabel9.setLabelFor(mitgliedsnummer);
    jLabel9.setText("Mitgliedsnummer: ");
    jLabel10.setDisplayedMnemonic('P');
    jLabel10.setLabelFor(passwort);
    jLabel10.setText("Pa�wort: ");
    mitgliedsnummer.setNextFocusableComponent(passwort);
    Dialog.setPreferredSize(mitgliedsnummer,200,19);
    passwort.setNextFocusableComponent(wettbewerbe);
    Dialog.setPreferredSize(passwort,200,19);
    weitereDatenPanel.setLayout(gridBagLayout2);
    jLabel11.setDisplayedMnemonic('1');
    jLabel11.setLabelFor(frei1);
    jLabel11.setText("Freie Verwendung 1: ");
    jLabel12.setDisplayedMnemonic('2');
    jLabel12.setLabelFor(frei2);
    jLabel12.setText("Freie Verwendung 2: ");
    jLabel13.setDisplayedMnemonic('3');
    jLabel13.setLabelFor(frei3);
    jLabel13.setText("Freie Verwendung 3: ");
    frei1.setNextFocusableComponent(frei2);
    frei2.setNextFocusableComponent(frei3);
    frei3.setNextFocusableComponent(SaveButton);
    Dialog.setPreferredSize(frei1,300,19);
    Dialog.setPreferredSize(frei2,300,19);
    Dialog.setPreferredSize(frei3,300,19);
    wettbewerbe.setNextFocusableComponent(SaveButton);
    wettbewerbe.setToolTipText("Wenn aktiviert, dann wird dieses Mitglied NICHT f�r Meldungen zu " +
    "Wettbewerben ber�cksichtigt");
    wettbewerbe.setMnemonic('W');
    wettbewerbe.setText("von Wettbewerbsmeldungen ausnehmen");
    mitglDataPanel.setToolTipText("");
    gruppenPanel.setLayout(gridBagLayout3);
    jLabel14.setText("Ist Mitglied in folgenden Gruppen:");
    weitereGruppeButton.setMnemonic('W');
    weitereGruppeButton.setText("Weitere Gruppe");
    weitereGruppeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        weitereGruppeButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(SaveButton, BorderLayout.SOUTH);
    this.getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
    jTabbedPane1.add(mitglDataPanel, "Mitgliederdaten");
    jTabbedPane1.add(weitereDatenPanel,  "Weitere");
    mitglDataPanel.add(vorname,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mitglDataPanel.add(nachname,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel3,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel4,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel5,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(verein,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(status1,     new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(geschlecht,     new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel7,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jahrgang,    new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel8,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(alias,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel6,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(behinderung,   new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel9,   new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(jLabel10,     new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(mitgliedsnummer,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(passwort,     new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mitglDataPanel.add(wettbewerbe,    new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    weitereDatenPanel.add(jLabel11,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weitereDatenPanel.add(jLabel12,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weitereDatenPanel.add(jLabel13,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weitereDatenPanel.add(frei1,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weitereDatenPanel.add(frei2,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weitereDatenPanel.add(frei3,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jTabbedPane1.add(gruppenPanel,   "Gruppen");
    gruppenPanel.add(jLabel14,  new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    gruppenPanel.add(weitereGruppeButton,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    for (int i=0; i<Daten.bezeichnungen.geschlecht.size(); i++)
      geschlecht.addItem(Daten.bezeichnungen.geschlecht.get(i));
    for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++)
      status1.addItem(Daten.fahrtenbuch.getDaten().status[i]);
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
    if (efamain) {
      efaFrame.focusManager.processKeyEvent(efaFeld,null);
    }
    if (!efamain && editnr>0) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void iniGruppen() {
    if (Daten.gruppen == null || Daten.gruppen.countElements() == 0) return;
    String vorname=this.vorname.getText().trim();
    String nachname=this.nachname.getText().trim();
    String verein=this.verein.getText().trim();
    gruppen = Daten.gruppen.getGruppen();
    gruppen.insertElementAt("--- keine Gruppe ---",0);
    for (int i=0; i<gruppen.size(); i++) {
      if (Daten.gruppen.isInGroup((String)gruppen.get(i),vorname,nachname,verein)) {
        addGruppenFeld((String)gruppen.get(i));
      }
    }
    gruppenChanged = false;
  }

  void addGruppenFeld(String gruppe) {
    if (gruppen == null) return;
    if (gruppenCombos == null) {
      gruppenCombos = new Vector();
    }
    JLabel label = new JLabel("Gruppe "+(gruppenCombos.size()+1)+": ");
    JComboBox combo = new JComboBox(gruppen);
    if (gruppe != null) {
      combo.setSelectedItem(gruppe);
    } else {
      combo.setSelectedIndex(0);
    }
    combo.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        gruppenCombo_itemStateChanged(e);
      }
    });
    gruppenCombos.add(combo);

    gruppenPanel.remove(weitereGruppeButton);
    gruppenPanel.add(label,  new GridBagConstraints(0, gruppenCombos.size(), 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    gruppenPanel.add(combo,  new GridBagConstraints(1, gruppenCombos.size(), 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    gruppenPanel.add(weitereGruppeButton,  new GridBagConstraints(1, gruppenCombos.size()+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.validate();
  }


  // Alias-Namen immer nur in Kleinbuchstaben zulassen
  void alias_focusLost(FocusEvent e) {
    String s = alias.getText().trim().toLowerCase();
    alias.setText(s);
    if (s.equals("")) return;
    if (Daten.fahrtenbuch.getDaten().mitglieder.isAlias(s) && !s.equals(oldAlias) && !e.isTemporary()) {
      Dialog.infoDialog("Ung�ltiges Eingabe-K�rzel","Das angegebene Eingabe-K�rzel ist bereits vergeben");
      alias.requestFocus();
    }
  }


  // Fahrtenbuch aktualisieren, d.h. Person "alt" durch Person "neu" in
  // allen Eintr�gen ab�ndern
  void updateFb(String alt, String neu) {
    DatenFelder d;
    boolean changed;
    d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    if (d != null) do {
      changed = false;
      if (d.get(Fahrtenbuch.STM).equals(alt)) {
        d.set(Fahrtenbuch.STM,neu);
        changed = true;
      }
      for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
        if (d.get(i).equals(alt)) {
          d.set(i,neu);
          changed = true;
        }
      if (changed) {
        Daten.fahrtenbuch.delete(d.get(Fahrtenbuch.LFDNR));
        Daten.fahrtenbuch.add(d);
      }
    } while( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null);
  }

  void updateGruppen(String alt, String neu) {
    if (Daten.gruppen == null || gruppen == null || gruppenCombos == null || gruppenCombos.size() == 0) return;
    GruppenMitglied gmAlt;
    if (alt.length() > 0 && !alt.equals(neu)) {
      gmAlt = new GruppenMitglied(EfaUtil.getVorname(alt),EfaUtil.getNachname(alt),EfaUtil.getVerein(alt));
    } else {
      gmAlt = new GruppenMitglied(vorname.getText().trim(),nachname.getText().trim(),verein.getText().trim());
    }
    for (int i=0; i<gruppen.size(); i++) {
      Daten.gruppen.deleteGruppenMitglied((String)gruppen.get(i),gmAlt);
    }
    GruppenMitglied gmNeu = new GruppenMitglied(vorname.getText().trim(),nachname.getText().trim(),verein.getText().trim());
    for (int i=0; i<gruppenCombos.size(); i++) {
      JComboBox combo = (JComboBox)gruppenCombos.get(i);
      if (combo.getSelectedIndex()>0) {
        Daten.gruppen.addGruppenMitglied((String)combo.getSelectedItem(),gmNeu);
      }
    }
  }


  // Speichern & Schliessen
  void SaveButton_actionPerformed(ActionEvent e) {
    if (this.vorname.getText().trim().length() + this.nachname.getText().trim().length() == 0) {
      Dialog.error("Bitte gib einen Namen ein!");
      this.vorname.requestFocus();
      return;
    }


    String k = EfaUtil.getFullName(vorname.getText().trim(),nachname.getText().trim(),verein.getText().trim());

    if (neu && Daten.fahrtenbuch.getDaten().mitglieder.getExact(k) != null) {
      Dialog.infoDialog("Fehler","Es existiert bereits ein Mitglied gleichen Namens!");
      return;
    }

    if (!neu && !k.equals(oldKey)) {
      if (Daten.fahrtenbuch != null) {

        if (Daten.fahrtenbuch.getDaten().mitglieder.getExact(k) != null)
          switch(Dialog.yesNoCancelDialog("Frage","Ein Eintrag mit gleichem Namen existiert bereits.\nSoll dieser durch den aktuellen Eintrag ersetzt werden?")) {
            case Dialog.YES: Daten.fahrtenbuch.getDaten().mitglieder.delete(k); break;
            default: return;
          }

        switch(Dialog.yesNoCancelDialog("Frage","Sollen Eintr�ge im Fahrtenbuch an den neuen Namen angepa�t werden?")) {
          case Dialog.YES: updateFb(oldKey,k); break;
          case Dialog.CANCEL: return;
          default: break;
        }
        switch(Dialog.yesNoCancelDialog("Frage","Sollen Eintr�ge in der Gruppenliste an den neuen Namen angepa�t werden?")) {
          case Dialog.YES: updateGruppen(oldKey,k); break;
          case Dialog.CANCEL: return;
          default: break;
        }
      } else {
        switch(Dialog.yesNoCancelDialog("Warnung","Durch die �nderung kann eine Inkonsistenz zu bestehenden Fahrtenb�chern entstehen.\n"+
                                                  "Soll der Eintrag trotzdem ge�ndert werden?")) {
          case Dialog.YES: break;
          case Dialog.CANCEL: return;
          default: cancel(); return;
        }
      }
    } else {
      if (gruppenChanged && !directMode) {
        switch(Dialog.yesNoCancelDialog("Frage","Sollen Eintr�ge in der Gruppenliste an die �nderungen angepa�t werden?")) {
          case Dialog.YES: updateGruppen(oldKey,k); break;
          case Dialog.CANCEL: return;
          default: break;
        }
      }
    }

    if (!neu) Daten.fahrtenbuch.getDaten().mitglieder.delete(oldKey);

    // Alias ggf. aktualisieren
    String neuerAlias = EfaUtil.removeSepFromString(alias.getText().trim());
    if (!neu && !neuerAlias.equals(oldAlias) && !oldAlias.equals(""))
      Daten.fahrtenbuch.getDaten().mitglieder.removeAlias(oldAlias);
    if (!neuerAlias.equals(oldAlias) && !neuerAlias.equals(""))
      Daten.fahrtenbuch.getDaten().mitglieder.addAlias(neuerAlias,k); // wennn ge�ndert oder neu (neu: oldAlias="")


    DatenFelder d = new DatenFelder(Mitglieder._ANZAHL);
    d.set(Mitglieder.VORNAME,EfaUtil.removeSepFromString(vorname.getText().trim()));
    d.set(Mitglieder.NACHNAME,EfaUtil.removeSepFromString(nachname.getText().trim()));
    d.set(Mitglieder.ALIAS,EfaUtil.removeSepFromString(alias.getText().trim()));
    d.set(Mitglieder.JAHRGANG,EfaUtil.removeSepFromString(jahrgang.getText().trim()));
    d.set(Mitglieder.GESCHLECHT,(String)geschlecht.getSelectedItem());
    d.set(Mitglieder.STATUS,(String)status1.getSelectedItem());
    d.set(Mitglieder.VEREIN,EfaUtil.removeSepFromString(verein.getText().trim()));
    d.set(Mitglieder.BEHINDERUNG,(behinderung.isSelected() ? "+" : "-"));
    d.set(Mitglieder.MITGLNR,EfaUtil.removeSepFromString(mitgliedsnummer.getText().trim()));
    d.set(Mitglieder.PASSWORT,EfaUtil.removeSepFromString(passwort.getText().trim()));
    Mitglieder.setKmwettMelden(d,!wettbewerbe.isSelected());
    d.set(Mitglieder.FREI1,EfaUtil.removeSepFromString(frei1.getText().trim()));
    d.set(Mitglieder.FREI2,EfaUtil.removeSepFromString(frei2.getText().trim()));
    d.set(Mitglieder.FREI3,EfaUtil.removeSepFromString(frei3.getText().trim()));
    Daten.fahrtenbuch.getDaten().mitglieder.add(d);

    if (directMode && Daten.fahrtenbuch.getDaten().mitglieder.writeFile()) {
      Logger.log(Logger.INFO,"Ein neues Mitglied wurde zur Mitgliederliste hinzugef�gt: "+EfaUtil.getFullName(d.get(Mitglieder.VORNAME),d.get(Mitglieder.NACHNAME),d.get(Mitglieder.VEREIN)));
    }

    if (efamain) {
      efaFeld.setText(k);
      efaFrame.vervollstaendige(efaFeld,efaButton,Daten.fahrtenbuch.getDaten().mitglieder,null,null,false);
    } else {
      auswahlFrame.doEdit(editnr+1);
      editnr = 0;
    }
    cancel();
  }

  void alias_focusGained(FocusEvent e) {
    if (!alias.getText().equals("")) return;
    String s="";
    if (Daten.efaConfig.autogenAlias) {
      s = EfaUtil.genAlias(Daten.efaConfig.aliasFormat,vorname.getText(),nachname.getText(),verein.getText());
    }
    alias.setText(s);
  }

  void jahrgang_focusLost(FocusEvent e) {
    if (jahrgang.getText().trim().equals("")) return;
    jahrgang.setText(Integer.toString( EfaUtil.yy2yyyy( EfaUtil.string2date(jahrgang.getText(),0,0,0).tag ) ));
  }

  void verein_focusLost(FocusEvent e) {
    verein.setText(EfaUtil.removeBracketsFromString(verein.getText().trim()));
    if (neu && verein.getText().trim().length() != 0)
      status1.setSelectedIndex(Daten.fahrtenbuch.getDaten().status.length-2);
  }

  void vorname_focusLost(FocusEvent e) {
    vorname.setText(EfaUtil.removeBracketsFromString(vorname.getText().trim()));
  }

  void nachname_focusLost(FocusEvent e) {
    nachname.setText(EfaUtil.removeBracketsFromString(nachname.getText().trim()));
  }

  void weitereGruppeButton_actionPerformed(ActionEvent e) {
    if (gruppen != null) {
      addGruppenFeld(null);
      gruppenChanged = true;
    }
  }

  void gruppenCombo_itemStateChanged(ItemEvent e) {
    gruppenChanged = true;
  }


}
