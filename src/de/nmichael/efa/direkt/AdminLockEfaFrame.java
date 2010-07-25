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

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.*;
import de.nmichael.efa.util.Dialog;

// @i18n complete

public class AdminLockEfaFrame extends JDialog implements ActionListener {
  private String _datumAnfang,_zeitAnfang,_datumEnde,_zeitEnde;

  JDialog parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField html = new JTextField();
  JButton htmlButton = new JButton();
  JLabel jLabel2 = new JLabel();
  JTextField endeSperrung = new JTextField();
  JLabel jLabel3 = new JLabel();
  JCheckBox vollbild = new JCheckBox();
  JLabel jLabel4 = new JLabel();
  JTextField anfangSperrung = new JTextField();
  JLabel jLabel5 = new JLabel();


  public AdminLockEfaFrame(JDialog parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      ini();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.parent = parent;
    // this.requestFocus();
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


  // Initialisierung des Frames
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(html);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("efa sperren"));
      okButton.addActionListener(new AdminLockEfaFrame_okButton_actionAdapter(this));
      jPanel2.setLayout(gridBagLayout1);
      Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("HTML-Seite anzeigen")+": ");
      jLabel1.setLabelFor(html);
      htmlButton.setText("");
      htmlButton.addActionListener(new AdminLockEfaFrame_htmlButton_actionAdapter(this));
      htmlButton.setPreferredSize(new Dimension(59,25));
      htmlButton.setIcon(new ImageIcon(AdminLockEfaFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
      Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Sperrung automatisch beenden")+": ");
      jLabel2.setLabelFor(endeSperrung);
      this.setTitle(International.getString("efa sperren"));
      html.setNextFocusableComponent(vollbild);
      html.setPreferredSize(new Dimension(400, 19));
      html.setText("");
      jLabel3.setText("("+International.getString("TT.MM.JJJJ SS:MM")+")");
      endeSperrung.setNextFocusableComponent(okButton);
      endeSperrung.setPreferredSize(new Dimension(200, 19));
      endeSperrung.setText("");
      endeSperrung.addFocusListener(new AdminLockEfaFrame_endeSperrung_focusAdapter(this));
      vollbild.setNextFocusableComponent(anfangSperrung);
      Mnemonics.setButton(this, vollbild, International.getStringWithMnemonic("Vollbild"));
      jLabel4.setDisplayedMnemonic('G');
      jLabel4.setLabelFor(anfangSperrung);
      Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Sperrung automatisch beginnen")+": ");
      jLabel5.setText("("+International.getString("TT.MM.JJJJ SS:MM")+")");
      anfangSperrung.setNextFocusableComponent(endeSperrung);
      anfangSperrung.setPreferredSize(new Dimension(200, 19));
      anfangSperrung.setText("");
      anfangSperrung.addFocusListener(new AdminLockEfaFrame_anfangSperrung_focusAdapter(this));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(html,   new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(htmlButton,   new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(endeSperrung,     new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel3,     new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      jPanel2.add(vollbild,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel4,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(anfangSperrung,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel5,   new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
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

  void ini() {
      if (Daten.efaConfig.efaDirekt_lockEfaShowHtml.getValue().length() > 0)
        html.setText(Daten.efaConfig.efaDirekt_lockEfaShowHtml.getValue());
      vollbild.setSelected(Daten.efaConfig.efaDirekt_lockEfaVollbild.getValue());
      if (Daten.efaConfig.efaDirekt_lockEfaFromDatum.isSet()) {
        setDatumZeit(anfangSperrung,
                     Daten.efaConfig.efaDirekt_lockEfaFromDatum.getDate(),
                     Daten.efaConfig.efaDirekt_lockEfaFromZeit.getTime());
      }
      if (Daten.efaConfig.efaDirekt_lockEfaUntilDatum.isSet()) {
        setDatumZeit(endeSperrung,
                     Daten.efaConfig.efaDirekt_lockEfaUntilDatum.getDate(),
                     Daten.efaConfig.efaDirekt_lockEfaUntilZeit.getTime());
      }
  }

  void htmlButton_actionPerformed(ActionEvent e) {
    String dat =Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("HTML-Datei")),
            International.getString("HTML-Datei")+" (*.html)","html",null,false);
    if (dat != null) {
      html.setText(dat);
    }
  }

  void okButton_actionPerformed(ActionEvent e) {
    endeSperrung_focusLost(null);
    if (Daten.efaConfig != null) {
      Daten.efaConfig.efaDirekt_lockEfaShowHtml.setValue(html.getText().trim());
      Daten.efaConfig.efaDirekt_lockEfaVollbild.setValue(vollbild.isSelected());
      if (_datumAnfang != null) {
          Daten.efaConfig.efaDirekt_lockEfaFromDatum.parseValue(_datumAnfang);
      } else {
          Daten.efaConfig.efaDirekt_lockEfaFromDatum.unset();
      }
      if (_zeitAnfang != null) {
          Daten.efaConfig.efaDirekt_lockEfaFromZeit.parseValue(_zeitAnfang);
      } else {
          Daten.efaConfig.efaDirekt_lockEfaFromZeit.unset();
      }
      if (_datumEnde != null) {
          Daten.efaConfig.efaDirekt_lockEfaUntilDatum.parseValue(_datumEnde);
      } else {
          Daten.efaConfig.efaDirekt_lockEfaUntilDatum.unset();
      }
      if (_zeitEnde != null) {
          Daten.efaConfig.efaDirekt_lockEfaUntilZeit.parseValue(_zeitEnde);
      } else {
          Daten.efaConfig.efaDirekt_lockEfaUntilZeit.unset();
      }
      Daten.efaConfig.efaDirekt_locked.setValue(!Daten.efaConfig.efaDirekt_lockEfaFromDatum.isSet());
      Daten.efaConfig.writeFile();
    }
    cancel();
  }

  void setDatumZeit(JTextField field, TMJ datum, TMJ zeit) {
    if (datum == null) return;
    field.setText(datum.tag+"."+datum.monat+"."+datum.jahr+" "+
                         (zeit != null ?
                           (zeit.tag<10 ? "0" : "")+zeit.tag+":"+(zeit.monat<10 ? "0" : "")+zeit.monat : "") );
  }

  void endeSperrung_focusLost(FocusEvent e) {
    String s = endeSperrung.getText().trim();

    int dateTimeSep;
    int c = 0;
    boolean inNumber = false;
    for (dateTimeSep = 0; dateTimeSep<s.length(); dateTimeSep++) {
      if (Character.isDigit(s.charAt(dateTimeSep))) {
        if (!inNumber) {
          c++;
          inNumber = true;
        }
      } else {
        inNumber = false;
      }
      if (c == 4) break;
    }
    TMJ datum = null;
    if (s.length() > 0) {
      datum = EfaUtil.correctDate(s, 0, 0, 0);
    }
    TMJ zeit = null;
    if (c == 4) {
      zeit = EfaUtil.string2date(s.substring(dateTimeSep), 0, 0, 0);
    }
    setDatumZeit(endeSperrung,datum,zeit);

    this._datumEnde = (datum != null ? datum.toString() : null);
    this._zeitEnde = (zeit != null ? zeit.toString() : null);
  }

  void anfangSperrung_focusLost(FocusEvent e) {
    String s = anfangSperrung.getText().trim();

    int dateTimeSep;
    int c = 0;
    boolean inNumber = false;
    for (dateTimeSep = 0; dateTimeSep<s.length(); dateTimeSep++) {
      if (Character.isDigit(s.charAt(dateTimeSep))) {
        if (!inNumber) {
          c++;
          inNumber = true;
        }
      } else {
        inNumber = false;
      }
      if (c == 4) break;
    }
    TMJ datum = null;
    if (s.length() > 0) {
      datum = EfaUtil.correctDate(s, 0, 0, 0);
    }
    TMJ zeit = null;
    if (c == 4) {
      zeit = EfaUtil.string2date(s.substring(dateTimeSep), 0, 0, 0);
    }
    setDatumZeit(anfangSperrung,datum,zeit);

    this._datumAnfang = (datum != null ? datum.toString() : null);
    this._zeitAnfang = (zeit != null ? zeit.toString() : null);
  }


}

class AdminLockEfaFrame_htmlButton_actionAdapter implements java.awt.event.ActionListener {
  AdminLockEfaFrame adaptee;

  AdminLockEfaFrame_htmlButton_actionAdapter(AdminLockEfaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.htmlButton_actionPerformed(e);
  }
}

class AdminLockEfaFrame_okButton_actionAdapter implements java.awt.event.ActionListener {
  AdminLockEfaFrame adaptee;

  AdminLockEfaFrame_okButton_actionAdapter(AdminLockEfaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class AdminLockEfaFrame_endeSperrung_focusAdapter extends java.awt.event.FocusAdapter {
  AdminLockEfaFrame adaptee;

  AdminLockEfaFrame_endeSperrung_focusAdapter(AdminLockEfaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.endeSperrung_focusLost(e);
  }
}

class AdminLockEfaFrame_anfangSperrung_focusAdapter extends java.awt.event.FocusAdapter {
  AdminLockEfaFrame adaptee;

  AdminLockEfaFrame_anfangSperrung_focusAdapter(AdminLockEfaFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.anfangSperrung_focusLost(e);
  }
}
