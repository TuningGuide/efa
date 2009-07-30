package de.nmichael.efa.eddi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

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

    this.setSize(new Dimension(700,520));
    this.setTitle("Fehler in zu importierendem Eintrag");
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setText("Zu importierender Eintrag:");
    eintragLabel.setBackground(Color.blue);
    eintragLabel.setForeground(Color.white);
    eintragLabel.setOpaque(true);
    eintragLabel.setText("---Eintrag---");
    okButton.setNextFocusableComponent(vorname);
    okButton.setMnemonic('O');
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout2);
    jLabel2.setDisplayedMnemonic('V');
    jLabel2.setLabelFor(vorname);
    jLabel2.setText("Vorname: ");
    jLabel3.setDisplayedMnemonic('N');
    jLabel3.setLabelFor(nachname);
    jLabel3.setText("Nachname: ");
    jLabel4.setDisplayedMnemonic('K');
    jLabel4.setLabelFor(kuerzel);
    jLabel4.setText("Eingabekürzel: ");
    jLabel5.setDisplayedMnemonic('J');
    jLabel5.setLabelFor(jahrgang);
    jLabel5.setText("Jahrgang: ");
    jLabel6.setDisplayedMnemonic('G');
    jLabel6.setLabelFor(geschlecht);
    jLabel6.setText("Geschlecht: ");
    jLabel7.setDisplayedMnemonic('S');
    jLabel7.setLabelFor(status);
    jLabel7.setText("Status: ");
    jLabel8.setDisplayedMnemonic('E');
    jLabel8.setLabelFor(verein);
    jLabel8.setText("Verein: ");
    jLabel9.setDisplayedMnemonic('B');
    jLabel9.setLabelFor(behinderung);
    jLabel9.setText("Behinderung: ");
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
    replGeschlBox.setToolTipText("Bei künftigen Einträgen diese Ersetzung immer durchführen");
    fehlerText.setBackground(new Color(204, 204, 204));
    fehlerText.setFont(new java.awt.Font("Dialog", 1, 12));
    fehlerText.setForeground(Color.red);
    fehlerText.setEditable(false);
    replStatusBox.setPreferredSize(new Dimension(300, 17));
    replStatusBox.setToolTipText("Bei künftigen Einträgen diese Ersetzung immer durchführen");
    replBehBox.setPreferredSize(new Dimension(300, 17));
    replBehBox.setToolTipText("Bei künftigen Einträgen diese Ersetzung immer durchführen");
    status.setNextFocusableComponent(verein);
    status.setPreferredSize(new Dimension(200, 17));
    status.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        status_actionPerformed(e);
      }
    });
    jLabel10.setText("Mitgliedsnummer: ");
    jLabel11.setText("Paßwort: ");
    jLabel12.setText("Frei 1: ");
    jLabel13.setText("Frei 2: ");
    jLabel14.setText("Frei 3: ");
    mitglnr.setNextFocusableComponent(password);
    password.setNextFocusableComponent(frei1);
    frei1.setNextFocusableComponent(frei2);
    frei2.setNextFocusableComponent(frei3);
    frei3.setNextFocusableComponent(okButton);
    skipButton.setText("Eintrag überspringen");
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

    geschlecht.addItem("ungültiger Wert");
    for (int i=0; i<Daten.bezeichnungen.geschlecht.size(); i++) geschlecht.addItem(Daten.bezeichnungen.geschlecht.get(i));
    status.addItem("ungültiger Wert");
    for (int i=0; i<statusList.length; i++) status.addItem(statusList[i]);
    behinderung.addItem("ungültiger Wert");
    behinderung.addItem("ja");
    behinderung.addItem("nein");
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
    if (geschlecht.getSelectedIndex()>0) this.replGeschlBox.setText("'"+felder[Mitglieder.GESCHLECHT]+"' immer durch '"+geschlecht.getSelectedItem()+"' ersetzen");
  }

  void status_actionPerformed(ActionEvent e) {
    if (status.getSelectedIndex()==0) this.replStatusBox.setEnabled(false);
    else this.replStatusBox.setEnabled(true);
    if (status.getSelectedIndex()>0) this.replStatusBox.setText("'"+felder[Mitglieder.STATUS]+"' immer durch '"+status.getSelectedItem()+"' ersetzen");
  }

  void behinderung_actionPerformed(ActionEvent e) {
    if (behinderung.getSelectedIndex()==0) this.replBehBox.setEnabled(false);
    else this.replBehBox.setEnabled(true);
    if (behinderung.getSelectedIndex()>0) this.replBehBox.setText("'"+felder[Mitglieder.BEHINDERUNG]+"' immer durch '"+behinderung.getSelectedItem()+"' ersetzen");
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
