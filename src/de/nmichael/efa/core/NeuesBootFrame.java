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
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import de.nmichael.efa.direkt.BootStatus;

// @i18n complete

public class NeuesBootFrame extends JDialog implements ActionListener {
  JButton SaveButton = new JButton();
  JPanel bootDataPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JComboBox anzahl = new JComboBox();
  JComboBox stm = new JComboBox();
  JComboBox rigger = new JComboBox();

  EfaFrame efaFrame;
  AuswahlFrame auswahlFrame;
  boolean neu;
  boolean efamain;
  String oldKey;
  int editnr;
  DatenListe gruppen = null;
  BootStatus bootstatus;

  private String _kombiRig_Rig = null;
  private String _kombiRig_Anz = null;
  private String _kombiRig_Stm = null;
  private String _kombiAnz_Rig = null;
  private String _kombiAnz_Anz = null;
  private String _kombiAnz_Stm = null;

  private String[] typesArt = null;
  private String[] typesAnz = null;
  private String[] typesRig = null;
  private String[] typesStm = null;

  JCheckBox kombiAnzahlBoot = new JCheckBox();
  JLabel kombiBootSyn = new JLabel();
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JTextField name = new JTextField();
  JTextField verein = new JTextField();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel1 = new JLabel();
  JComboBox art = new JComboBox();
  JCheckBox kombiRiggerBoot = new JCheckBox();
  JButton standardmannschaftButton = new JButton();
  JTabbedPane tabbedPane = new JTabbedPane();
  JPanel rudererlaubnisPanel = new JPanel();
  JPanel weiterePanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField gruppe1 = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField gruppe2 = new JTextField();
  JLabel jLabel6 = new JLabel();
  JTextField gruppe3 = new JTextField();
  JLabel jLabel7 = new JLabel();
  JTextField gruppe4 = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField gruppe5 = new JTextField();
  JLabel jLabel9 = new JLabel();
  JTextField maxAnzahlNichtInGruppe = new JTextField();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JTextField frei1 = new JTextField();
  JTextField frei2 = new JTextField();
  JTextField frei3 = new JTextField();
  JLabel jLabel13 = new JLabel();
  JLabel jLabel14 = new JLabel();
  JTextField gruppeMind1 = new JTextField();

  void startNeuesBoot(String boot) {
    // Überprüfen, ob wirklich noch nicht vorhanden
    if (Daten.fahrtenbuch.getDaten().boote.getExact(boot) != null) {
      editiereBoot((DatenFelder)Daten.fahrtenbuch.getDaten().boote.getComplete());
      return;
    }

    oldKey = "";
    neu = true; // Neues Boot
    name.setText(EfaUtil.getName(boot));
    verein.setText(EfaUtil.getVerein(boot));
    this.setTitle(International.getString("Neues Boot hinzufügen"));
    Mnemonics.setButton(this, SaveButton, International.getStringWithMnemonic("Boot hinzufügen"));
    kombiBootSyn.setVisible(false);
    kombiBootLabel();
    name.requestFocus();
  }

  void editiereBoot(DatenFelder d) {
    if (d == null) { // sollte eigentlich nicht passieren
      cancel(false);
      return;
    }
    neu = false; // Eintrag ändern
    if (d.get(Boote.VEREIN).equals("")) oldKey = d.get(Boote.NAME);
    else oldKey = d.get(Boote.NAME)+" ("+d.get(Boote.VEREIN)+")";
    // Bugfix: Kann passieren, wenn in EfaFrame der Button noch grün ist, inzwischen das Boot aber gelöscht wurde
    if (oldKey == null) { // @neu
      oldKey = "";
      neu = true;
    }

    name.setText(d.get(Boote.NAME));
    verein.setText(d.get(Boote.VEREIN));
    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, d.get(Boote.ANZAHL)));
    rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, d.get(Boote.RIGGER)));
    stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, d.get(Boote.STM)));
    art.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, d.get(Boote.ART)));

    Vector v = Boote.getGruppen(d);
    for (int i=0; i<v.size(); i++) {
      String s = (String)v.get(i);
      if (s.length()>0) {
        switch(i) {
          case 0: gruppe1.setText(s); break;
          case 1: gruppe2.setText(s); break;
          case 2: gruppe3.setText(s); break;
          case 3: gruppe4.setText(s); break;
          case 4: gruppe5.setText(s); break;
        }
      }
    }
    maxAnzahlNichtInGruppe.setText(Integer.toString(EfaUtil.string2int(d.get(Boote.MAX_NICHT_IN_GRUPPE),0)));
    gruppeMind1.setText(d.get(Boote.MIND_1_IN_GRUPPE));

    frei1.setText(d.get(Boote.FREI1));
    frei2.setText(d.get(Boote.FREI2));
    frei3.setText(d.get(Boote.FREI3));

    this.setTitle(International.getString("Boot bearbeiten"));
    Mnemonics.setButton(this, SaveButton, International.getStringWithMnemonic("Eintrag übernehmen"));

    if (EfaUtil.syn2org(Daten.synBoote,oldKey).equals(oldKey)) {
      kombiBootSyn.setVisible(false); // kein Synonym
    } else {
      kombiBootSyn.setText(International.getString("Synonym für")+": "+EfaUtil.syn2org(Daten.synBoote,oldKey));
    }
    kombiBootLabel();
    name.requestFocus();
  }

  // Konstruktor (aus EfaFrame)
  public NeuesBootFrame(EfaFrame parent, String boot, boolean neu) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);

    efaFrame = parent;
    efamain = true;
    this.bootstatus = null;

    if (neu) startNeuesBoot(boot);
    else {
      if (Daten.fahrtenbuch.getDaten().boote.getExact(boot) != null)
        editiereBoot((DatenFelder)Daten.fahrtenbuch.getDaten().boote.getComplete());
      else {
        // Bugfix: Kann passieren, wenn in EfaFrame der Button noch grün ist, inzwischen das Boot aber gelöscht wurde
        startNeuesBoot(boot);
      }
    }
  }

  // Konstruktor (aus AuswahlFrame)
  public NeuesBootFrame(AuswahlFrame f, DatenFelder d, boolean neu, int editnr, BootStatus bootstatus) {
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
    this.bootstatus = bootstatus;

    if (neu) startNeuesBoot("");
    else editiereBoot(d);
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel(false);
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

    SaveButton.setNextFocusableComponent(name);
    Mnemonics.setButton(this, SaveButton, International.getStringWithMnemonic("Boot hinzufügen"));
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveButton_actionPerformed(e);
      }
    });
    this.setTitle(International.getString("Neues Boot hinzufügen"));
    bootDataPanel.setLayout(gridBagLayout1);
    art.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        art_focusLost(e);
      }
    });
    anzahl.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        anzahl_focusLost(e);
      }
    });
    kombiAnzahlBoot.setNextFocusableComponent(kombiBootSyn);
    Mnemonics.setButton(this, kombiAnzahlBoot, International.getStringWithMnemonic("Kombi-Boot"));
    anzahl.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        anzahl_itemStateChanged(e);
      }
    });
    stm.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        stm_itemStateChanged(e);
      }
    });
    Mnemonics.setLabel(this, kombiBootSyn, International.getStringWithMnemonic("Synonym für"));
    art.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        art_itemStateChanged(e);
      }
    });
    mainPanel.setLayout(borderLayout2);
    name.setNextFocusableComponent(verein);
    Dialog.setPreferredSize(name,200,19);
    name.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        name_focusLost(e);
      }
    });
    verein.setNextFocusableComponent(art);
    Dialog.setPreferredSize(verein,200,19);
    verein.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        verein_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Bootsname")+": ");
    jLabel1.setLabelFor(name);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("falls auswärtig, Verein")+": ");
    jLabel2.setLabelFor(verein);
    kombiRiggerBoot.setNextFocusableComponent(SaveButton);
    kombiRiggerBoot.setText(International.getString("Kombi-Boot"));
    rigger.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        rigger_itemStateChanged(e);
      }
    });
    rigger.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        rigger_focusLost(e);
      }
    });
    art.setNextFocusableComponent(anzahl);
    anzahl.setNextFocusableComponent(rigger);
    rigger.setNextFocusableComponent(stm);
    stm.setNextFocusableComponent(standardmannschaftButton);
    standardmannschaftButton.setNextFocusableComponent(SaveButton);
    Mnemonics.setButton(this, standardmannschaftButton, International.getStringWithMnemonic("Standardmannschaft konfigurieren"));
    standardmannschaftButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        standardmannschaftButton_actionPerformed(e);
      }
    });
    rudererlaubnisPanel.setLayout(gridBagLayout3);
    Mnemonics.setLabel(this, jLabel3, International.getStringWithMnemonic("Dieses Boot darf nur von Mitgliedern der folgenden Gruppen genutzt werden")+": ");
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Gruppe")+" 1: ");
    Mnemonics.setLabel(this, jLabel5, International.getStringWithMnemonic("Gruppe")+" 2: ");
    Mnemonics.setLabel(this, jLabel6, International.getStringWithMnemonic("Gruppe")+" 3: ");
    Mnemonics.setLabel(this, jLabel7, International.getStringWithMnemonic("Gruppe")+" 4: ");
    Mnemonics.setLabel(this, jLabel8, International.getStringWithMnemonic("Gruppe")+" 5: ");
    jLabel4.setLabelFor(gruppe1);
    jLabel5.setLabelFor(gruppe2);
    jLabel6.setLabelFor(gruppe3);
    jLabel7.setLabelFor(gruppe4);
    jLabel8.setLabelFor(gruppe5);
    Mnemonics.setLabel(this, jLabel9, International.getStringWithMnemonic("Max. erlaubte Anzahl an Personen, die NICHT einer der Gruppen angehören")+": ");
    jLabel9.setLabelFor(maxAnzahlNichtInGruppe);
    maxAnzahlNichtInGruppe.setNextFocusableComponent(gruppeMind1);
    Dialog.setPreferredSize(maxAnzahlNichtInGruppe,50,19);
    maxAnzahlNichtInGruppe.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        maxAnzahlNichtInGruppe_focusLost(e);
      }
    });
    gruppe1.setNextFocusableComponent(gruppe2);
    gruppe2.setNextFocusableComponent(gruppe3);
    gruppe3.setNextFocusableComponent(gruppe4);
    gruppe4.setNextFocusableComponent(gruppe5);
    gruppe5.setNextFocusableComponent(maxAnzahlNichtInGruppe);
    gruppe1.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppe2.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppe3.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppe4.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppe5.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppeMind1.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    gruppe1.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    gruppe2.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    gruppe3.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    gruppe4.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    gruppe5.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    gruppeMind1.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    weiterePanel.setLayout(gridBagLayout4);
    Mnemonics.setLabel(this, jLabel10, International.getStringWithMnemonic("Freie Verwendung")+" 1: ");
    Mnemonics.setLabel(this, jLabel11, International.getStringWithMnemonic("Freie Verwendung")+" 2: ");
    Mnemonics.setLabel(this, jLabel12, International.getStringWithMnemonic("Freie Verwendung")+" 3: ");
    jLabel10.setLabelFor(frei1);
    jLabel11.setLabelFor(frei2);
    jLabel12.setLabelFor(frei3);
    frei2.setNextFocusableComponent(frei3);
    Dialog.setPreferredSize(frei2, 400, 19);
    frei3.setNextFocusableComponent(SaveButton);
    Dialog.setPreferredSize(frei3, 400, 19);
    frei1.setNextFocusableComponent(frei2);
    Dialog.setPreferredSize(frei1, 400, 19);
    jLabel13.setText(International.getString("Mindestens eine Person muß folgender Gruppe angehören")+": ");
    Mnemonics.setLabel(this, jLabel14, International.getStringWithMnemonic("Gruppe")+": ");
    jLabel14.setLabelFor(gruppeMind1);
    gruppeMind1.setNextFocusableComponent(SaveButton);
    mainPanel.add(SaveButton, BorderLayout.SOUTH);
    mainPanel.add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.add(bootDataPanel, International.getString("Bootsdaten"));
    tabbedPane.add(rudererlaubnisPanel, International.getString("Bootsbenutzung"));
    rudererlaubnisPanel.add(jLabel3,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel4,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(gruppe1,  new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel5,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(gruppe2,  new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel6,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(gruppe3,  new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel7,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(gruppe4,  new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel8,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(gruppe5,  new GridBagConstraints(1, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel9,  new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(maxAnzahlNichtInGruppe,  new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel13,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(jLabel14,  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererlaubnisPanel.add(gruppeMind1,  new GridBagConstraints(1, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(name,    new GridBagConstraints(1, 0, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(verein,   new GridBagConstraints(1, 1, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(anzahl,       new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    bootDataPanel.add(rigger,        new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    bootDataPanel.add(stm,        new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    bootDataPanel.add(kombiAnzahlBoot,         new GridBagConstraints(0, 3, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(kombiBootSyn,         new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    bootDataPanel.add(art,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    bootDataPanel.add(kombiRiggerBoot,   new GridBagConstraints(0, 5, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bootDataPanel.add(standardmannschaftButton,     new GridBagConstraints(0, 7, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    tabbedPane.add(weiterePanel,  International.getString("Weitere"));
    weiterePanel.add(jLabel10,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel11,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel12,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(frei1,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(frei2,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(frei3,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    // Initialisierung der Auswahllisten (rückwärts, da sonst ItemStateChanged von anzahl und art zuschlägt
    typesArt = new String[Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT)];
    typesAnz = new String[Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS)];
    typesRig = new String[Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING)];
    typesStm = new String[Daten.efaTypes.size(EfaTypes.CATEGORY_COXING)];
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++) {
        art.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, i));
        typesArt[i] = Daten.efaTypes.getType(EfaTypes.CATEGORY_BOAT, i);
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++) {
        anzahl.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, i));
        typesAnz[i] = Daten.efaTypes.getType(EfaTypes.CATEGORY_NUMSEATS, i);
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING); i++) {
        rigger.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, i));
        typesRig[i] = Daten.efaTypes.getType(EfaTypes.CATEGORY_RIGGING, i);
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_COXING); i++) {
        stm.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, i));
        typesStm[i] = Daten.efaTypes.getType(EfaTypes.CATEGORY_COXING, i);
    }
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel(false);
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel(boolean kombi) {
    if (efamain) {
      if (!kombi) efaFrame.focusManager.processKeyEvent(efaFrame.boot,null);
      else efaFrame.boot.requestFocus();
    }
    Dialog.frameClosed(this);
    dispose();
    if (!efamain && editnr>0) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  // Fahrtenbuch aktualisieren, d.h. Boot "alt" durch Boot "neu" in
  // allen Einträgen abändern
  void updateFb(String alt, String neu) {
    DatenFelder d;
    boolean changed;
    d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    if (d != null) do {
      changed = false;
      if (d.get(Fahrtenbuch.BOOT).equals(alt)) {
        d.set(Fahrtenbuch.BOOT,neu);
        changed = true;
      }
      if (changed) {
        Daten.fahrtenbuch.delete(d.get(Fahrtenbuch.LFDNR));
        Daten.fahrtenbuch.add(d);
      }
    } while( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null);
  }

  // Bootstatus anpassen: Boot "alt" durch Boot "neu" ersetzen
  void updateBootstatus(String alt, String neu) {
    if (bootstatus == null) return;
    DatenFelder d = bootstatus.getExactComplete(alt);
    if (d == null) return;
    bootstatus.delete(alt);
    d.set(BootStatus.NAME,neu);
    bootstatus.add(d);
    bootstatus.writeFile();
  }


  // Änderungen übernehmen, speichern
  void SaveButton_actionPerformed(ActionEvent e) {
    if (this.name.getText().trim().length()==0) {
      Dialog.error(International.getString("Bitte gib einen Bootsnamen ein!"));
      this.name.requestFocus();
      return;
    }

    String bname = EfaUtil.replace(EfaUtil.replace(name.getText().trim(),"[","",true),"]","",true);
    String vname = EfaUtil.replace(EfaUtil.replace(verein.getText().trim(),"[","",true),"]","",true);

    String k;
    if (vname.equals("")) k = bname;
    else k = bname+" ("+vname+")";

    if (!kombiAnzahlBoot.isVisible()) kombiAnzahlBoot.setSelected(false);
    if (!kombiRiggerBoot.isVisible()) kombiRiggerBoot.setSelected(false);
    boolean newKombi = kombiAnzahlBoot.isSelected() || kombiRiggerBoot.isSelected();

    if (neu && Daten.fahrtenbuch.getDaten().boote.getExact(k) != null && !newKombi) {
      Dialog.infoDialog(International.getString("Fehler"),
              International.getString("Es existiert bereits ein Boot gleichen Namens!"));
      return;
    }

    if (!neu && !k.equals(oldKey)) {
      if (Daten.fahrtenbuch != null) {

        if (Daten.fahrtenbuch.getDaten().boote.getExact(k) != null)
          switch(Dialog.yesNoCancelDialog(International.getString("Gleichnamiger Eintrag"),
                  International.getString("Ein Eintrag mit gleichem Namen existiert bereits. Soll dieser durch den aktuellen Eintrag ersetzt werden?"))) {
            case Dialog.YES: Daten.fahrtenbuch.getDaten().boote.delete(k); break;
            default: return;
          }

        switch(Dialog.yesNoCancelDialog(International.getString("Einträge anpassen"),
                International.getString("Sollen Einträge im Fahrtenbuch an die Änderungen angepaßt werden?"))) {
          case Dialog.YES: updateFb(oldKey,k); break;
          case Dialog.CANCEL: return;
          default: break;
        }

        if (Daten.applID == Daten.APPL_EFADIREKT && bootstatus != null) {
          switch(Dialog.yesNoCancelDialog(International.getString("Statusliste anpassen"),
                  International.getString("Soll die Bootsstatusliste an die Änderung angepaßt werden?"))) {
            case Dialog.YES: updateBootstatus(oldKey,k); break;
            case Dialog.CANCEL: return;
            default: break;
          }
        }


      } else {
        switch(Dialog.yesNoCancelDialog(International.getString("Warnung"),
                International.getString("Durch die Änderung kann eine Inkonsistenz zu bestehenden Fahrtenbüchern entstehen. "+
                                                  "Soll der Eintrag trotzdem geändert werden?"))) {
          case Dialog.YES: break;
          case Dialog.CANCEL: return;
          default: cancel(false); return;
        }
      }
    }

    if (!neu) Daten.fahrtenbuch.getDaten().boote.delete(oldKey);

    if (newKombi) addKombi();
    else {
      DatenFelder d = new DatenFelder(Boote._ANZFELDER);
      d.set(Boote.NAME,EfaUtil.removeSepFromString(bname));
      d.set(Boote.VEREIN,EfaUtil.removeSepFromString(vname));
      d.set(Boote.ART, typesArt[art.getSelectedIndex()]);
      d.set(Boote.ANZAHL, typesAnz[anzahl.getSelectedIndex()]);
      d.set(Boote.RIGGER, typesRig[rigger.getSelectedIndex()]);
      d.set(Boote.STM, typesStm[stm.getSelectedIndex()]);
      d.set(Boote.GRUPPEN,Boote.makeGruppen(gruppe1.getText(),gruppe2.getText(),gruppe3.getText(),gruppe4.getText(),gruppe5.getText()));
      d.set(Boote.MAX_NICHT_IN_GRUPPE,Integer.toString(EfaUtil.string2int(maxAnzahlNichtInGruppe.getText(),0)));
      d.set(Boote.MIND_1_IN_GRUPPE,EfaUtil.removeSepFromString(gruppeMind1.getText().trim()));
      d.set(Boote.FREI1,EfaUtil.removeSepFromString(frei1.getText().trim()));
      d.set(Boote.FREI2,EfaUtil.removeSepFromString(frei2.getText().trim()));
      d.set(Boote.FREI3,EfaUtil.removeSepFromString(frei3.getText().trim()));
      Daten.fahrtenbuch.getDaten().boote.add(d);
    }

    if (efamain) {
      efaFrame.boot.setText(k);
      efaFrame.vervollstaendige(efaFrame.boot,efaFrame.bootButton,Daten.fahrtenbuch.getDaten().boote,null,null,false);
      if (newKombi && Daten.synBoote.isChanged()) Daten.synBoote.writeFile();
    } else {
      auswahlFrame.doEdit(editnr+1);
      editnr = 0;
    }
    cancel(newKombi);
  }


  void addKombiBoot(String orgName, String tArt, String tAnz, String tRig, String tStm) {
    String name, nameMitVerein;

    name = EfaUtil.removeSepFromString(this.name.getText().trim());
    if (kombiRiggerBoot.isSelected()) name += " - " + Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, tRig);
    if (kombiAnzahlBoot.isSelected()) name += " - " + Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, tAnz) + " " +
                                                      Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, tStm);
    nameMitVerein = name;
    if (!verein.getText().trim().equals("")) nameMitVerein += " ("+EfaUtil.removeSepFromString(verein.getText().trim())+")";

    if (Daten.fahrtenbuch.getDaten().boote.getExact(nameMitVerein) != null) {
      Dialog.infoDialog(International.getString("Fehler"),
              International.getString("Es existiert bereits ein Boot gleichen Namens!")+":\n"+
              International.getString("Name") + ": " + nameMitVerein);
    } else {
      DatenFelder d = new DatenFelder(Boote._ANZFELDER);
      d.set(Boote.NAME,EfaUtil.removeSepFromString(name));
      d.set(Boote.VEREIN,EfaUtil.removeSepFromString(EfaUtil.removeSepFromString(verein.getText().trim())));
      d.set(Boote.ART,tArt);
      d.set(Boote.ANZAHL,tAnz);
      d.set(Boote.RIGGER,tRig);
      d.set(Boote.STM,tStm);
      d.set(Boote.GRUPPEN,Boote.makeGruppen(gruppe1.getText(),gruppe2.getText(),gruppe3.getText(),gruppe4.getText(),gruppe5.getText()));
      d.set(Boote.MAX_NICHT_IN_GRUPPE,Integer.toString(EfaUtil.string2int(maxAnzahlNichtInGruppe.getText(),0)));
      d.set(Boote.MIND_1_IN_GRUPPE,EfaUtil.removeSepFromString(gruppeMind1.getText().trim()));
      d.set(Boote.FREI1,EfaUtil.removeSepFromString(frei1.getText().trim()));
      d.set(Boote.FREI2,EfaUtil.removeSepFromString(frei2.getText().trim()));
      d.set(Boote.FREI3,EfaUtil.removeSepFromString(frei3.getText().trim()));
      Daten.fahrtenbuch.getDaten().boote.add(d);
    }
    // Synonym hinzufügen
    DatenFelder ds = new DatenFelder(Daten.synBoote._FELDERANZAHL);
    ds.set(Synonyme.ORIGINAL,orgName);
    ds.set(Synonyme.SYNONYM,nameMitVerein);
    if (EfaUtil.syn2org(Daten.synBoote,nameMitVerein).equals(nameMitVerein)) Daten.synBoote.add(ds);
  }


  void addKombi() {
    String tArt = typesArt[art.getSelectedIndex()];
    String tAnz = typesAnz[anzahl.getSelectedIndex()];
    String tRig = typesRig[rigger.getSelectedIndex()];
    String tStm = typesStm[stm.getSelectedIndex()];

    int anz = EfaTypes.getNumberOfRowers(tAnz);

    String orgname;
    if (verein.getText().trim().equals("")) orgname = EfaUtil.removeSepFromString(name.getText().trim());
    else orgname = EfaUtil.removeSepFromString(name.getText().trim())+" ("+EfaUtil.removeSepFromString(verein.getText().trim())+")";

    // Kombi-Boot (so wie angezeigt)
    addKombiBoot(orgname,tArt,tAnz,tRig,tStm);

    // Kombi-Boot (Anzahl)
    if (kombiAnzahlBoot.isSelected() && _kombiAnz_Anz != null && _kombiAnz_Rig != null && _kombiAnz_Stm != null) {
      addKombiBoot(orgname, tArt, _kombiAnz_Anz, _kombiAnz_Rig, _kombiAnz_Stm);
    }

    // Kombi-Boot (Rigger)
    if (kombiRiggerBoot.isSelected() && _kombiRig_Anz != null && _kombiRig_Rig != null && _kombiRig_Stm != null) {
      addKombiBoot(orgname, tArt, _kombiRig_Anz, _kombiRig_Rig, _kombiRig_Stm);
    }
  }


    void art_focusLost(FocusEvent e) {
        if (art.getSelectedIndex() < 0) {
            return;
        }
        try {
            String tArt = typesArt[art.getSelectedIndex()];
            if (tArt.equals(EfaTypes.TYPE_BOAT_WHERRY) ||
                tArt.equals(EfaTypes.TYPE_BOAT_TRIMMY)) {
                anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_1));
                rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SCULL));
                stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXLESS));
            }
            if (tArt.equals(EfaTypes.TYPE_BOAT_BARQUE) ||
                    tArt.equals(EfaTypes.TYPE_BOAT_CHURCHBOAT)) {
                anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_OTHER));
                rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SWEEP));
                stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXED));
            }
            if (tArt.equals(EfaTypes.TYPE_BOAT_MOTORBOAT)) {
                anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_OTHER));
                rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_OTHER));
                stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_OTHER));
            }
            if (tArt.equals(EfaTypes.TYPE_BOAT_ERG)) {
                anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_1));
                rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_OTHER));
                stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_OTHER));
            }
        } catch (Exception ee) {
            // nothing to do
        }
    }

    void anzahl_focusLost(FocusEvent e) {
        if (anzahl.getSelectedIndex()<0) return;
        if (rigger.getSelectedIndex()<0) return;
        try {
            String tAnz = typesAnz[anzahl.getSelectedIndex()];
            String tRig = typesRig[rigger.getSelectedIndex()];

            if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_1)) {
                rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SCULL));
                stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXLESS));
            }
            if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_8) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_8X)) {
                stm.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXED));
            }
            if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_2X) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_3) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_4X) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_5) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_6X) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_8X)) {
                if (tRig.equals(EfaTypes.TYPE_RIGGING_SWEEP)) {
                    rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SCULL));
                }
            }
            if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_2) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_4) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_6) ||
                tAnz.equals(EfaTypes.TYPE_NUMSEATS_8)) {
                if (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL)) {
                    rigger.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SWEEP));
                }
            }
        } catch (Exception ee) {
            // nothing to do
        }
    }

    void rigger_focusLost(FocusEvent e) {
        if (anzahl.getSelectedIndex()<0) return;
        if (rigger.getSelectedIndex()<0) return;
        try {
            String tAnz = typesAnz[anzahl.getSelectedIndex()];
            String tRig = typesRig[rigger.getSelectedIndex()];
            if (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL)) {
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_2)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_2X));
                }
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_4)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_4X));
                }
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_6)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_6X));
                }
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_8)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_8X));
                }
            }
            if (tRig.equals(EfaTypes.TYPE_RIGGING_SWEEP)) {
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_2X)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_2));
                }
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_4X)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_4));
                }
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_6X)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_6));
                }
                if (tAnz.equals(EfaTypes.TYPE_NUMSEATS_8X)) {
                    anzahl.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_8));
                }
            }
        } catch (Exception ee) {
            // nothing to do
        }
    }

  void kombiBootLabel() {
    if (!neu) {
      kombiRiggerBoot.setVisible(false);
      kombiAnzahlBoot.setVisible(false);
      return;
    }

    if (anzahl.getSelectedIndex()<0 || stm.getSelectedIndex()<0 || rigger.getSelectedIndex()<0) return;
    String tArt = typesArt[art.getSelectedIndex()];
    String tAnz = typesAnz[anzahl.getSelectedIndex()];
    String tRig = typesRig[rigger.getSelectedIndex()];
    String tStm = typesStm[stm.getSelectedIndex()];

    int anz = EfaTypes.getNumberOfRowers(tAnz);

    // Kombi-Boot Skull/Riemen??
    _kombiRig_Rig = null;
    _kombiRig_Anz = null;
    _kombiRig_Stm = null;
    if (anz != 0 && anz % 2 == 0 &&
        (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL) || tRig.equals(EfaTypes.TYPE_RIGGING_SWEEP))) {
      _kombiRig_Rig = (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL) ? EfaTypes.TYPE_RIGGING_SWEEP : EfaTypes.TYPE_RIGGING_SCULL);
      _kombiRig_Anz = anzahlString(anz, _kombiRig_Rig);
      _kombiRig_Stm = tStm;
      kombiRiggerBoot.setText(International.getString("Kombi-Boot") +
              " (" + International.getMessage("auch nutzbar als {boattype}",Boote.getDetailBezeichnung(tArt, _kombiRig_Anz, _kombiRig_Stm)) + ")");
      kombiRiggerBoot.setVisible(true);
    } else {
        kombiRiggerBoot.setVisible(false);
    }

    // Kombi-Boot Anzahl Ruderer/Steuermann??
    _kombiAnz_Rig = null;
    _kombiAnz_Anz = null;
    _kombiAnz_Stm = null;
    int kombi_anz = 0;
    if (anz != 0 && tStm.equals(EfaTypes.TYPE_COXING_COXED)) {
        kombi_anz = anz + 1;
    } 
    if (anz != 0 && tStm.equals(EfaTypes.TYPE_COXING_COXLESS)) {
        kombi_anz = anz - 1;
    }
    if (kombi_anz==7 || kombi_anz>8 || kombi_anz<1) {
        kombiAnzahlBoot.setVisible(false);
    } else {
      _kombiAnz_Anz = anzahlString(kombi_anz, tRig);
      _kombiAnz_Stm = (tStm.equals(EfaTypes.TYPE_COXING_COXED) ? EfaTypes.TYPE_COXING_COXLESS : EfaTypes.TYPE_COXING_COXED);
      _kombiAnz_Rig = (kombi_anz % 2 == 1 ? EfaTypes.TYPE_RIGGING_SCULL : tRig);
      kombiAnzahlBoot.setText(International.getString("Kombi-Boot") +
              " (" + International.getMessage("auch nutzbar als {boattype}",Boote.getDetailBezeichnung(tArt, _kombiAnz_Anz, _kombiAnz_Stm)) + ")");
      kombiAnzahlBoot.setVisible(true);
    }
  }

  String anzahlString(int anz, String tRig) {
    switch(anz) {
      case 1: 
          return EfaTypes.TYPE_NUMSEATS_1;
      case 2:
          if (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL)) {
              return EfaTypes.TYPE_NUMSEATS_2X;
          } else {
              return EfaTypes.TYPE_NUMSEATS_2;
          }
      case 3:
          return EfaTypes.TYPE_NUMSEATS_3;
      case 4:
          if (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL)) {
              return EfaTypes.TYPE_NUMSEATS_4X;
          } else {
              return EfaTypes.TYPE_NUMSEATS_4;
          }
      case 5:
          return EfaTypes.TYPE_NUMSEATS_5;
      case 6:
          if (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL)) {
              return EfaTypes.TYPE_NUMSEATS_6X;
          } else {
              return EfaTypes.TYPE_NUMSEATS_6;
          }
      case 8:
          if (tRig.equals(EfaTypes.TYPE_RIGGING_SCULL)) {
              return EfaTypes.TYPE_NUMSEATS_8X;
          } else {
              return EfaTypes.TYPE_NUMSEATS_8;
          }
      default:
          return EfaTypes.TYPE_NUMSEATS_OTHER;
    }
  }

  void anzahl_itemStateChanged(ItemEvent e) {
    this.anzahl_focusLost(null); // weil focuslost irgendwie nicht funktioniert...
    kombiBootLabel();
  }

  void stm_itemStateChanged(ItemEvent e) {
    kombiBootLabel();
  }

  void art_itemStateChanged(ItemEvent e) {
    this.art_focusLost(null); // weil focuslost irgendwie nicht funktioniert...
  }
  void stm1_itemStateChanged(ItemEvent e) {

  }

  void rigger_itemStateChanged(ItemEvent e) {
    this.rigger_focusLost(null); // weil focuslost irgendwie nicht funktioniert...
    kombiBootLabel();
  }

  void name_focusLost(FocusEvent e) {
    name.setText(EfaUtil.removeBracketsFromString(name.getText().trim()));
  }

  void verein_focusLost(FocusEvent e) {
    verein.setText(EfaUtil.removeBracketsFromString(verein.getText().trim()));
  }

  void standardmannschaftButton_actionPerformed(ActionEvent e) {
    String b = name.getText().trim();
    String v = verein.getText().trim();
    if (b.length() == 0) return;
    if (v.length()>0) b += " ("+v+")";
    MannschaftFrame dlg = new MannschaftFrame(this,EfaUtil.removeSepFromString(b),null,0);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void maxAnzahlNichtInGruppe_focusLost(FocusEvent e) {
    maxAnzahlNichtInGruppe.setText(Integer.toString(EfaUtil.string2date(maxAnzahlNichtInGruppe.getText(),0,0,0).tag));
  }

  void gruppe_keyReleased(KeyEvent e) {
    if (Daten.gruppen == null) return;
    if (e == null) return;
    JTextField field;
    try {
      field = (JTextField)e.getSource();
    } catch(Exception ee) {
      return;
    }
    if (gruppen == null) {
      Vector g = Daten.gruppen.getGruppen();
      gruppen = new DatenListe("foo",1,1,false);
      for (int i=0; i<g.size(); i++) {
        DatenFelder d = new DatenFelder(1);
        d.set(0,(String)g.get(i));
        gruppen.add(d);
      }
    }
    if (field.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(field,null,gruppen,e,null,true);
  }

  void gruppe_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

}
