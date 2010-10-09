/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.eddi;

import de.nmichael.efa.efa1.Mitglieder;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import de.nmichael.efa.*;

// @i18n complete

public class MitgliederFehlerFrame extends JDialog implements ActionListener {
  String[] felder;
  Hashtable replGeschl;
  Hashtable replStatus;
  Hashtable replBeh;
  String[] statusList;
  EddiFrame parent;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel eintragLabel = new JLabel();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JTextField vorname = new JTextField();
  JTextField nachname = new JTextField();
  JTextField kuerzel = new JTextField();
  JTextField jahrgang = new JTextField();
  JTextField verein = new JTextField();
  JComboBox geschlecht = new JComboBox();
  JComboBox behinderung = new JComboBox();
  JCheckBox replGeschlBox = new JCheckBox();
  JTextArea fehlerText = new JTextArea();
  JCheckBox replStatusBox = new JCheckBox();
  JCheckBox replBehBox = new JCheckBox();
  JComboBox status = new JComboBox();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JLabel jLabel14 = new JLabel();
  JTextField mitglnr = new JTextField();
  JTextField password = new JTextField();
  JTextField frei1 = new JTextField();
  JTextField frei2 = new JTextField();
  JTextField frei3 = new JTextField();
  JButton skipButton = new JButton();


  public MitgliederFehlerFrame(EddiFrame parent, String eintrag, String fehler, String[] felder, String[] statusList, Hashtable replGeschl, Hashtable replStatus, Hashtable replBeh) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);

    this.parent = parent;
    this.felder = felder;
    this.statusList = statusList;
    this.replGeschl = replGeschl;
    this.replStatus = replStatus;
    this.replBeh = replBeh;


    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    this.eintragLabel.setText(eintrag);
    this.fehlerText.append(fehler);
    this.vorname.setText(felder[Mitglieder.VORNAME]);
    this.nachname.setText(felder[Mitglieder.NACHNAME]);
    this.kuerzel.setText(felder[Mitglieder.ALIAS]);
    this.jahrgang.setText(felder[Mitglieder.JAHRGANG]);
    this.geschlecht.setSelectedItem(felder[Mitglieder.GESCHLECHT]);
    this.status.setSelectedItem(felder[Mitglieder.STATUS]);
    this.verein.setText(felder[Mitglieder.VEREIN]);
    if (felder[Mitglieder.BEHINDERUNG].length()==0) this.behinderung.setSelectedIndex(2);
    else if (felder[Mitglieder.BEHINDERUNG].equals("+")) this.behinderung.setSelectedIndex(1);
    else this.behinderung.setSelectedIndex(0);
    this.mitglnr.setText(felder[Mitglieder.MITGLNR]);
    this.password.setText(felder[Mitglieder.PASSWORT]);
    this.frei1.setText(felder[Mitglieder.FREI1]);
    this.frei2.setText(felder[Mitglieder.FREI2]);
    this.frei3.setText(felder[Mitglieder.FREI3]);
    this.vorname.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      parent.abort = true;
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

    this.setSize(new Dimension(700,520));
    this.setTitle(International.getString("Fehler in zu importierendem Eintrag"));
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setText(International.getString("Zu importierender Eintrag")+":");
    eintragLabel.setBackground(Color.blue);
    eintragLabel.setForeground(Color.white);
    eintragLabel.setOpaque(true);
    eintragLabel.setText("---"+International.getString("Eintrag")+"---");
    okButton.setNextFocusableComponent(vorname);
    Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("OK"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout2);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Vorname")+": ");
    jLabel2.setLabelFor(vorname);
    Mnemonics.setLabel(this, jLabel3, International.getStringWithMnemonic("Nachname")+": ");
    jLabel3.setLabelFor(nachname);
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Eingabekürzel")+": ");
    jLabel4.setLabelFor(kuerzel);
    Mnemonics.setLabel(this, jLabel5, International.getStringWithMnemonic("Jahrgang")+": ");
    jLabel5.setLabelFor(jahrgang);
    Mnemonics.setLabel(this, jLabel6, International.getStringWithMnemonic("Geschlecht")+": ");
    jLabel6.setLabelFor(geschlecht);
    Mnemonics.setLabel(this, jLabel7, International.getStringWithMnemonic("Status")+": ");
    jLabel7.setLabelFor(status);
    Mnemonics.setLabel(this, jLabel8, International.getStringWithMnemonic("Verein")+": ");
    jLabel8.setLabelFor(verein);
    Mnemonics.setLabel(this, jLabel9, International.getStringWithMnemonic("Behinderung")+": ");
    jLabel9.setLabelFor(behinderung);
    vorname.setNextFocusableComponent(nachname);
    vorname.setPreferredSize(new Dimension(200, 17));
    nachname.setNextFocusableComponent(kuerzel);
    nachname.setPreferredSize(new Dimension(200, 17));
    kuerzel.setNextFocusableComponent(jahrgang);
    kuerzel.setPreferredSize(new Dimension(200, 17));
    jahrgang.setNextFocusableComponent(geschlecht);
    jahrgang.setPreferredSize(new Dimension(200, 17));
    verein.setNextFocusableComponent(behinderung);
    verein.setPreferredSize(new Dimension(200, 17));
    geschlecht.setNextFocusableComponent(status);
    geschlecht.setPreferredSize(new Dimension(200, 17));
    geschlecht.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        geschlecht_actionPerformed(e);
      }
    });
    behinderung.setNextFocusableComponent(mitglnr);
    behinderung.setPreferredSize(new Dimension(200, 17));
    behinderung.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        behinderung_actionPerformed(e);
      }
    });
    replGeschlBox.setPreferredSize(new Dimension(300, 17));
    replGeschlBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    fehlerText.setBackground(new Color(204, 204, 204));
    fehlerText.setFont(new java.awt.Font("Dialog", 1, 12));
    fehlerText.setForeground(Color.red);
    fehlerText.setEditable(false);
    replStatusBox.setPreferredSize(new Dimension(300, 17));
    replStatusBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    replBehBox.setPreferredSize(new Dimension(300, 17));
    replBehBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    status.setNextFocusableComponent(verein);
    status.setPreferredSize(new Dimension(200, 17));
    status.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        status_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel10, International.getStringWithMnemonic("Mitgliedsnummer")+": ");
    jLabel10.setLabelFor(mitglnr);
    Mnemonics.setLabel(this, jLabel11, International.getStringWithMnemonic("Paßwort")+": ");
    jLabel11.setLabelFor(password);
    Mnemonics.setLabel(this, jLabel12, International.getStringWithMnemonic("Frei")+" 1: ");
    jLabel12.setLabelFor(frei1);
    Mnemonics.setLabel(this, jLabel13, International.getStringWithMnemonic("Frei")+" 2: ");
    jLabel13.setLabelFor(frei2);
    Mnemonics.setLabel(this, jLabel14, International.getStringWithMnemonic("Frei")+" 3: ");
    jLabel14.setLabelFor(frei3);
    mitglnr.setNextFocusableComponent(password);
    password.setNextFocusableComponent(frei1);
    frei1.setNextFocusableComponent(frei2);
    frei2.setNextFocusableComponent(frei3);
    frei3.setNextFocusableComponent(okButton);
    skipButton.setText(International.getString("Eintrag überspringen"));
    skipButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        skipButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(eintragLabel,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(fehlerText,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    this.getContentPane().add(okButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jLabel2,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel3,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel4,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel5,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel6,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel7,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel8,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel9,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(vorname,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(nachname,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(kuerzel,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jahrgang,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(verein,  new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(geschlecht,  new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(behinderung, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replGeschlBox,  new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replStatusBox,  new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replBehBox,  new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(status, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel10,   new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel11,   new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel12,   new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel13,   new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel14,   new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(mitglnr,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(password,   new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(frei1,   new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(frei2,   new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(frei3,   new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(skipButton,    new GridBagConstraints(0, 13, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

    geschlecht.addItem(International.getString("ungültiger Wert"));
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++) {
        geschlecht.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, i));
    }
    status.addItem(International.getString("ungültiger Wert"));
    for (int i=0; i<statusList.length; i++) status.addItem(statusList[i]);
    behinderung.addItem(International.getString("ungültiger Wert"));
    behinderung.addItem(International.getString("ja"));
    behinderung.addItem(International.getString("nein"));
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      parent.abort = true;
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


  void geschlecht_actionPerformed(ActionEvent e) {
    if (geschlecht.getSelectedIndex()==0) this.replGeschlBox.setEnabled(false);
    else this.replGeschlBox.setEnabled(true);
    if (geschlecht.getSelectedIndex()>0) {
        this.replGeschlBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Mitglieder.GESCHLECHT],geschlecht.getSelectedItem().toString()));
    }
  }

  void status_actionPerformed(ActionEvent e) {
    if (status.getSelectedIndex()==0) this.replStatusBox.setEnabled(false);
    else this.replStatusBox.setEnabled(true);
    if (status.getSelectedIndex()>0) {
        this.replStatusBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Mitglieder.STATUS],status.getSelectedItem().toString()));
    }
  }

  void behinderung_actionPerformed(ActionEvent e) {
    if (behinderung.getSelectedIndex()==0) this.replBehBox.setEnabled(false);
    else this.replBehBox.setEnabled(true);
    if (behinderung.getSelectedIndex()>0) {
        this.replBehBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Mitglieder.BEHINDERUNG],behinderung.getSelectedItem().toString()));
    }
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (this.replGeschlBox.isSelected() && this.replGeschlBox.isEnabled()) replGeschl.put(felder[Mitglieder.GESCHLECHT],geschlecht.getSelectedItem());
    if (this.replStatusBox.isSelected() && this.replStatusBox.isEnabled()) replStatus.put(felder[Mitglieder.STATUS],status.getSelectedItem());
    if (this.replBehBox.isSelected() && this.replBehBox.isEnabled()) {
      if (this.behinderung.getSelectedIndex()==1) replBeh.put(felder[Mitglieder.BEHINDERUNG],"+");
      if (this.behinderung.getSelectedIndex()==2) replBeh.put(felder[Mitglieder.BEHINDERUNG],"");
    }
    felder[Mitglieder.VORNAME] = this.vorname.getText().trim();
    felder[Mitglieder.NACHNAME] = this.nachname.getText().trim();
    felder[Mitglieder.ALIAS] = this.kuerzel.getText().trim();
    felder[Mitglieder.JAHRGANG] = this.jahrgang.getText().trim();
    felder[Mitglieder.GESCHLECHT] = (String)this.geschlecht.getSelectedItem();
    felder[Mitglieder.STATUS] = (String)this.status.getSelectedItem();
    felder[Mitglieder.VEREIN] = this.verein.getText().trim();
    if (this.behinderung.getSelectedIndex()==1) felder[Mitglieder.BEHINDERUNG] = "+";
    else if (this.behinderung.getSelectedIndex()==2) felder[Mitglieder.BEHINDERUNG] = "";
    else felder[Mitglieder.BEHINDERUNG] = "?";
    felder[Mitglieder.MITGLNR] = this.mitglnr.getText().trim();
    felder[Mitglieder.PASSWORT] = this.password.getText().trim();
    felder[Mitglieder.FREI1] = this.frei1.getText().trim();
    felder[Mitglieder.FREI2] = this.frei2.getText().trim();
    felder[Mitglieder.FREI3] = this.frei3.getText().trim();
    cancel();
  }

  void skipButton_actionPerformed(ActionEvent e) {
    parent.skip = true;
    cancel();
  }


}
