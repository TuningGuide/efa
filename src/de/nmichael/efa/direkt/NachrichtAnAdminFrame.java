/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.efa1.NachrichtenAnAdmin;
import de.nmichael.efa.core.EfaFrame;
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

public class NachrichtAnAdminFrame extends JDialog implements ActionListener {

  boolean gesendet = false;
  Nachricht message = null;
  NachrichtenAnAdmin nachrichten;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField name = new JTextField();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea nachricht = new JTextArea();
  JLabel jLabel3 = new JLabel();
  JTextField betreff = new JTextField();
  JLabel jLabel4 = new JLabel();
  JComboBox empfaenger = new JComboBox();
  JPanel southPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton closeButton = new JButton();


  public NachrichtAnAdminFrame(Frame parent, NachrichtenAnAdmin nachrichten) {
    super(parent);
    frini(parent,nachrichten);
  }
  public NachrichtAnAdminFrame(Frame parent, NachrichtenAnAdmin nachrichten, int empfaenger,
                               String absender, String betreff, String text) {
    super(parent);
    frini(parent,nachrichten);
    this.empfaenger.setSelectedIndex(empfaenger);
    this.empfaenger.setEnabled(false);
    if (absender != null) this.name.setText(absender);
    if (betreff != null) this.betreff.setText(betreff);
    if (text != null) {
      this.nachricht.setText(text);
      this.nachricht.requestFocus();
      this.nachricht.setCaretPosition(this.nachricht.getText().length());
    }
  }

  void frini(Frame parent, NachrichtenAnAdmin nachrichten) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      empfaenger.addItem(Nachricht.getEmpfaengerName(Nachricht.ADMIN));
      empfaenger.addItem(Nachricht.getEmpfaengerName(Nachricht.BOOTSWART));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.nachrichten = nachrichten;
    this.name.requestFocus();
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
      jPanel1.setLayout(borderLayout1);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Nachricht abschicken"));
      okButton.setNextFocusableComponent(closeButton);
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Dein Name")+": ");
      jLabel1.setLabelFor(name);
      Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Nachricht")+": ");
      jLabel2.setLabelFor(nachricht);
      Mnemonics.setLabel(this, jLabel3, International.getStringWithMnemonic("Betreff")+": ");
      jLabel3.setLabelFor(betreff);
      name.setNextFocusableComponent(betreff);
      Dialog.setPreferredSize(empfaenger,400,17);
      Dialog.setPreferredSize(name,400,17);
      Dialog.setPreferredSize(jLabel1,100,13);
      Dialog.setPreferredSize(jLabel2,100,13);
      Dialog.setPreferredSize(jLabel3,100,13);
      name.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          name_keyReleased(e);
        }
      });
      name.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          name_focusLost(e);
        }
      });
      betreff.setNextFocusableComponent(nachricht);
      Dialog.setPreferredSize(betreff,400,17);
      Dialog.setPreferredSize(jScrollPane1,400,150);
      nachricht.setNextFocusableComponent(okButton);
      nachricht.setLineWrap(true);
      this.setTitle(International.getString("Nachricht an Admin"));
      Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Empfänger")+": ");
      jLabel4.setLabelFor(empfaenger);
      empfaenger.setNextFocusableComponent(name);
      southPanel.setLayout(borderLayout2);
      Mnemonics.setButton(this, closeButton, International.getStringWithMnemonic("Schließen"));
      closeButton.setNextFocusableComponent(empfaenger);
      closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeButton_actionPerformed(e);
        }
    });
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      southPanel.add(okButton,  BorderLayout.NORTH);
      southPanel.add(closeButton,  BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(name,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jScrollPane1,     new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel3,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(betreff,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel4,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(empfaenger, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(southPanel,  BorderLayout.SOUTH);
      jScrollPane1.getViewport().add(nachricht, null);
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

  void okButton_actionPerformed(ActionEvent e) {
    if (name.getText().trim().length()==0) {
      Dialog.error(International.getString("Bitte gib einen Namen ein!"));
      name.requestFocus();
      return;
    }
    if (betreff.getText().trim().length()==0) {
      Dialog.error(International.getString("Bitte gib einen Betreff ein!"));
      betreff.requestFocus();
      return;
    }
    if (nachricht.getText().trim().length()==0) {
      Dialog.error(International.getString("Bitte gib einen Nachrichtentext ein!"));
      nachricht.requestFocus();
      return;
    }
    Nachricht n = new Nachricht();
    n.empfaenger = empfaenger.getSelectedIndex();
    n.name = name.getText().trim();
    n.betreff = betreff.getText().trim();
    n.nachricht = nachricht.getText();
    nachrichten.add(n);
    nachrichten.writeFile();
    this.gesendet = true;
    this.message = n;

    if (Daten.efaConfig.admins.size() > 0) {
      n.sendEmail(Daten.efaConfig.admins.getKeysArray());
    }

    cancel();
  }

  void name_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().mitglieder == null) return;
    if (name.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(name,null,Daten.fahrtenbuch.getDaten().mitglieder,e,null,true);
  }

  void name_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

  public boolean isGesendet() {
    return gesendet;
  }

  public Nachricht getLastMessage() {
    return message;
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }

}

