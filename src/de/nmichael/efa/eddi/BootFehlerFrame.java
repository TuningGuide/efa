/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.eddi;

import de.nmichael.efa.efa1.Boote;
import de.nmichael.efa.core.*;
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

public class BootFehlerFrame extends JDialog implements ActionListener {
  String[] felder;
  Hashtable replArt;
  Hashtable replAnzahl;
  Hashtable replRigger;
  Hashtable replStm;
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
  JTextField name = new JTextField();
  JTextField verein = new JTextField();
  JComboBox rigger = new JComboBox();
  JCheckBox replRiggerBox = new JCheckBox();
  JTextArea fehlerText = new JTextArea();
  JCheckBox replStmBox = new JCheckBox();
  JComboBox stm = new JComboBox();
  JComboBox art = new JComboBox();
  JComboBox anzahl = new JComboBox();
  JCheckBox replArtBox = new JCheckBox();
  JCheckBox replAnzahlBox = new JCheckBox();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JTextField gruppen = new JTextField();
  JTextField maxNichtInGruppe = new JTextField();
  JTextField mindInGruppe = new JTextField();
  JTextField frei1 = new JTextField();
  JTextField frei2 = new JTextField();
  JTextField frei3 = new JTextField();
  JButton skipButton = new JButton();


  public BootFehlerFrame(EddiFrame parent, String eintrag, String fehler, String[] felder, Hashtable replArt, Hashtable replAnzahl, Hashtable replRigger, Hashtable replStm) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);

    this.parent = parent;
    this.felder = felder;
    this.replArt = replArt;
    this.replAnzahl = replAnzahl;
    this.replRigger = replRigger;
    this.replStm = replStm;


    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    this.eintragLabel.setText(eintrag);
    this.fehlerText.append(fehler);
    this.name.setText(felder[Boote.NAME]);
    this.verein.setText(felder[Boote.VEREIN]);
    this.art.setSelectedItem(felder[Boote.ART]);
    this.anzahl.setSelectedItem(felder[Boote.ANZAHL]);
    this.rigger.setSelectedItem(felder[Boote.RIGGER]);
    this.stm.setSelectedItem(felder[Boote.STM]);
    this.gruppen.setText(felder[Boote.GRUPPEN]);
    this.maxNichtInGruppe.setText(felder[Boote.MAX_NICHT_IN_GRUPPE]);
    this.mindInGruppe.setText(felder[Boote.MIND_1_IN_GRUPPE]);
    this.frei1.setText(felder[Boote.FREI1]);
    this.frei2.setText(felder[Boote.FREI2]);
    this.frei3.setText(felder[Boote.FREI3]);
    this.name.requestFocus();
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
    okButton.setNextFocusableComponent(name);
    Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("OK"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout2);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Bootsname")+": ");
    jLabel2.setLabelFor(name);
    Mnemonics.setLabel(this, jLabel3, International.getStringWithMnemonic("Verein")+": ");
    jLabel3.setLabelFor(verein);
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Bootsart")+": ");
    jLabel4.setLabelFor(art);
    Mnemonics.setLabel(this, jLabel5, International.getStringWithMnemonic("Anzahl Bootsplätze")+": ");
    jLabel5.setLabelFor(anzahl);
    Mnemonics.setLabel(this, jLabel6, International.getStringWithMnemonic("Riggerung")+": ");
    jLabel6.setLabelFor(rigger);
    Mnemonics.setLabel(this, jLabel7, International.getStringWithMnemonic("mit/ohne Stm.")+": ");
    jLabel7.setLabelFor(stm);
    name.setNextFocusableComponent(verein);
    name.setPreferredSize(new Dimension(200, 17));
    verein.setNextFocusableComponent(art);
    verein.setPreferredSize(new Dimension(200, 17));
    rigger.setNextFocusableComponent(stm);
    rigger.setPreferredSize(new Dimension(200, 17));
    rigger.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rigger_actionPerformed(e);
      }
    });
    replRiggerBox.setPreferredSize(new Dimension(300, 17));
    replRiggerBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    fehlerText.setBackground(new Color(204, 204, 204));
    fehlerText.setFont(new java.awt.Font("Dialog", 1, 12));
    fehlerText.setForeground(Color.red);
    fehlerText.setEditable(false);
    replStmBox.setPreferredSize(new Dimension(300, 17));
    replStmBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    stm.setNextFocusableComponent(gruppen);
    stm.setPreferredSize(new Dimension(200, 17));
    stm.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stm_actionPerformed(e);
      }
    });
    art.setNextFocusableComponent(anzahl);
    art.setPreferredSize(new Dimension(200, 17));
    art.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        art_actionPerformed(e);
      }
    });
    anzahl.setNextFocusableComponent(rigger);
    anzahl.setPreferredSize(new Dimension(200, 17));
    anzahl.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        anzahl_actionPerformed(e);
      }
    });
    replArtBox.setPreferredSize(new Dimension(300, 17));
    replArtBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    replAnzahlBox.setPreferredSize(new Dimension(300, 17));
    replAnzahlBox.setToolTipText(International.getString("Bei weiteren Einträgen diese Ersetzung immer durchführen"));
    Mnemonics.setLabel(this, jLabel8, International.getStringWithMnemonic("Gruppen")+": ");
    jLabel8.setLabelFor(gruppen);
    Mnemonics.setLabel(this, jLabel9, International.getStringWithMnemonic("max. nicht in Gruppe")+": ");
    jLabel8.setLabelFor(maxNichtInGruppe);
    Mnemonics.setLabel(this, jLabel10, International.getStringWithMnemonic("mind. in Gruppe")+": ");
    jLabel8.setLabelFor(mindInGruppe);
    Mnemonics.setLabel(this, jLabel11, International.getStringWithMnemonic("Frei")+" 1: ");
    jLabel8.setLabelFor(frei1);
    Mnemonics.setLabel(this, jLabel12, International.getStringWithMnemonic("Frei")+" 2: ");
    jLabel8.setLabelFor(frei2);
    Mnemonics.setLabel(this, jLabel13, International.getStringWithMnemonic("Frei")+" 3: ");
    jLabel8.setLabelFor(frei3);
    gruppen.setNextFocusableComponent(maxNichtInGruppe);
    maxNichtInGruppe.setNextFocusableComponent(mindInGruppe);
    mindInGruppe.setNextFocusableComponent(frei1);
    frei1.setNextFocusableComponent(frei2);
    frei2.setNextFocusableComponent(frei3);
    frei3.setNextFocusableComponent(okButton);
    Mnemonics.setButton(this, skipButton, International.getStringWithMnemonic("Eintrag überspringen"));
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
    jPanel2.add(jLabel2,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel3,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel4,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel5,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel6,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel7,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(name,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(verein,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(rigger,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replRiggerBox,   new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replStmBox,   new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(stm,  new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(art, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(anzahl,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replArtBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(replAnzahlBox,  new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel8,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel9,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel10,   new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel11,   new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel12,   new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel13,   new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(gruppen,   new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(maxNichtInGruppe,   new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(mindInGruppe,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(frei1,   new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(frei2,   new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(frei3,   new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(skipButton,    new GridBagConstraints(0, 12, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));

    art.addItem(International.getString("ungültiger Wert"));
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++) {
        art.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT,i));
    }
    anzahl.addItem(International.getString("ungültiger Wert"));
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++) {
        anzahl.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS,i));
    }
    rigger.addItem(International.getString("ungültiger Wert"));
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING); i++) {
        rigger.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING,i));
    }
    stm.addItem(International.getString("ungültiger Wert"));
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_COXING); i++) {
        stm.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING,i));
    }
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


  void art_actionPerformed(ActionEvent e) {
    if (art.getSelectedIndex()==0) this.replArtBox.setEnabled(false);
    else this.replArtBox.setEnabled(true);
    if (art.getSelectedIndex()>0) {
        this.replArtBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Boote.ART],art.getSelectedItem().toString()));
    }
  }

  void anzahl_actionPerformed(ActionEvent e) {
    if (anzahl.getSelectedIndex()==0) this.replAnzahlBox.setEnabled(false);
    else this.replAnzahlBox.setEnabled(true);
    if (anzahl.getSelectedIndex()>0) {
        this.replAnzahlBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Boote.ANZAHL],anzahl.getSelectedItem().toString()));
    }
  }

  void rigger_actionPerformed(ActionEvent e) {
    if (rigger.getSelectedIndex()==0) this.replRiggerBox.setEnabled(false);
    else this.replRiggerBox.setEnabled(true);
    if (rigger.getSelectedIndex()>0) {
        this.replRiggerBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Boote.RIGGER],rigger.getSelectedItem().toString()));
    }
  }

  void stm_actionPerformed(ActionEvent e) {
    if (stm.getSelectedIndex()==0) this.replStmBox.setEnabled(false);
    else this.replStmBox.setEnabled(true);
    if (stm.getSelectedIndex()>0) {
        this.replStmBox.setText(International.getMessage("'{original_text}' immer durch '{new_text}' ersetzen",
                felder[Boote.STM],stm.getSelectedItem().toString()));
    }
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (this.replArtBox.isSelected() && this.replArtBox.isEnabled()) replArt.put(felder[Boote.ART],art.getSelectedItem());
    if (this.replAnzahlBox.isSelected() && this.replAnzahlBox.isEnabled()) replAnzahl.put(felder[Boote.ANZAHL],anzahl.getSelectedItem());
    if (this.replRiggerBox.isSelected() && this.replRiggerBox.isEnabled()) replRigger.put(felder[Boote.RIGGER],rigger.getSelectedItem());
    if (this.replStmBox.isSelected() && this.replStmBox.isEnabled()) replStm.put(felder[Boote.STM],stm.getSelectedItem());
    felder[Boote.NAME] = this.name.getText().trim();
    felder[Boote.VEREIN] = this.verein.getText().trim();
    felder[Boote.ART] = (String)this.art.getSelectedItem();
    felder[Boote.ANZAHL] = (String)this.anzahl.getSelectedItem();
    felder[Boote.RIGGER] = (String)this.rigger.getSelectedItem();
    felder[Boote.STM] = (String)this.stm.getSelectedItem();
    felder[Boote.GRUPPEN] = this.gruppen.getText().trim();
    felder[Boote.MAX_NICHT_IN_GRUPPE] = Integer.toString(EfaUtil.string2int(this.maxNichtInGruppe.getText().trim(),0));
    felder[Boote.MIND_1_IN_GRUPPE] = this.mindInGruppe.getText().trim();
    felder[Boote.FREI1] = this.frei1.getText().trim();
    felder[Boote.FREI2] = this.frei2.getText().trim();
    felder[Boote.FREI3] = this.frei3.getText().trim();
    cancel();
  }

  void skipButton_actionPerformed(ActionEvent e) {
    parent.skip = true;
    cancel();
}


}
