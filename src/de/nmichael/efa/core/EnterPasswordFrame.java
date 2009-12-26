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

import de.nmichael.efa.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.util.Dialog;

// @i18n complete

public class EnterPasswordFrame extends JDialog implements ActionListener {
  static final int MIN_PASSWORD_LENGTH = 8;
  static char[] resultPassword;

  Window parent;
  boolean newPwd;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel passwortLabel = new JLabel();
  JPasswordField passwort = new JPasswordField();
  JTextArea grund = new JTextArea();
  JLabel passwort2Label = new JLabel();
  JPasswordField passwort2 = new JPasswordField();
  JLabel jLabel1 = new JLabel();


  public EnterPasswordFrame(Frame parent, String grund, boolean newPwd) {
    super(parent);
    this.parent = parent;
    construct(grund,newPwd);
  }
  public EnterPasswordFrame(JDialog parent, String grund, boolean newPwd) {
    super(parent);
    this.parent = parent;
    construct(grund,newPwd);
  }
  public EnterPasswordFrame(String grund, boolean newPwd) {
    construct(grund,newPwd);
  }

  void construct(String grund, boolean newPwd) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      if (!newPwd) {
        this.passwort2Label.setVisible(false);
        this.passwort2.setVisible(false);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.grund.append( (grund==null ? "" : grund) );
    this.newPwd = newPwd;

    EfaUtil.pack(this);
    this.passwort.requestFocus();
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
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_2")) { // ENTER
      okButton_actionPerformed(null);
    }
  }

  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1","ENTER"}, new String[] {"keyAction","keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    jPanel1.setLayout(borderLayout1);
    okButton.setMnemonic('O');
    okButton.setText(International.getString("OK"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, passwortLabel, International.getStringWithMnemonic("Paßwort")+": ");
    passwortLabel.setLabelFor(passwort);
    Dialog.setPreferredSize(passwort,150,19);
    this.setTitle(International.getString("Paßworteingabe"));
    grund.setBackground(Color.lightGray);
    grund.setEditable(false);
    Mnemonics.setLabel(this, passwort2Label, International.getStringWithMnemonic("Paßwort") +
            " (" + International.getString("Wiederholung") + "): ");
    jLabel1.setText(" ");
    Dialog.setPreferredSize(passwort2,150,19);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(okButton, BorderLayout.SOUTH);
    jPanel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(passwortLabel,         new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel2.add(passwort,        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
    jPanel2.add(grund,       new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 20, 20, 20), 0, 0));
    jPanel2.add(passwort2Label,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel2.add(passwort2,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
    jPanel2.add(jLabel1,  new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
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


  public static char[] enterPassword(Window parent, String grund) {
    EnterPasswordFrame dlg = null;
    if (parent == null) dlg = new EnterPasswordFrame(grund,false);
    else try {
      dlg = new EnterPasswordFrame((JDialog)parent,grund,false);
    } catch(ClassCastException e) {
      dlg = new EnterPasswordFrame((JFrame)parent,grund,false);
    }
    Dialog.setDlgLocation(dlg,parent);
    return _enterPassword(dlg);
  }

  public static char[] enterPassword(Frame parent, String grund) {
    EnterPasswordFrame dlg = null;
    if (parent != null) dlg = new EnterPasswordFrame(parent,grund,false);
    else dlg = new EnterPasswordFrame(grund,false);
    Dialog.setDlgLocation(dlg,parent);
    return _enterPassword(dlg);
  }

  private static char[] _enterPassword(EnterPasswordFrame dlg) {
    resultPassword = null;
    dlg.setModal(true);
    dlg.show();
    return resultPassword;
  }

  public static char[] enterNewPassword(Window parent, String grund) {
    EnterPasswordFrame dlg = null;
    if (parent == null) dlg = new EnterPasswordFrame(grund,true);
    else try {
      dlg = new EnterPasswordFrame((JDialog)parent,grund,true);
    } catch(ClassCastException e) {
      dlg = new EnterPasswordFrame((JFrame)parent,grund,true);
    }
    Dialog.setDlgLocation(dlg,parent);
    return _enterNewPassword(dlg);
  }

  public static char[] enterNewPassword(Frame parent, String grund) {
    EnterPasswordFrame dlg = null;
    if (parent != null) dlg = new EnterPasswordFrame(parent,grund,true);
    else dlg = new EnterPasswordFrame(grund,true);
    Dialog.setDlgLocation(dlg,parent);
    return _enterNewPassword(dlg);
  }

  private static char[] _enterNewPassword(EnterPasswordFrame dlg) {
    resultPassword = null;
    dlg.setModal(true);
    dlg.show();
    return resultPassword;
  }

  void okButton_actionPerformed(ActionEvent e) {
    String pwd = new String(passwort.getPassword());
    if (pwd.length()==0) {
      Dialog.error(International.getString("Kein Paßwort eingegeben!"));
      passwort.requestFocus();
      return;
    }
    if (newPwd) {
      String pwd2 = new String(passwort2.getPassword());
      if (!pwd.equals(pwd2)) {
        Dialog.error(International.getString("Die beiden eingegebenen Paßwörter sind verschieden. Du mußt in beide Felder dasselbe Paßwort eingeben!"));
        passwort2.requestFocus();
        return;
      }

      if (pwd.indexOf(" ") >= 0) {
        Dialog.error(International.getString("Das Paßwort darf keine Leerzeichen enthalten!"));
        passwort.requestFocus();
        return;
      }
      if (pwd.length()<MIN_PASSWORD_LENGTH) {
        Dialog.error(International.getMessage("Das Paßwort muß mindestens {n} Zeichen lang sein!",MIN_PASSWORD_LENGTH));
        passwort.requestFocus();
        return;
      }

      // Test, ob mindestens drei Zeichengruppen vorkommen
      boolean klein = false;
      boolean gross = false;
      boolean ziffer = false;
      boolean sonst = false;
      for (char c = 'a'; c<='z'; c++) if (pwd.indexOf(String.valueOf(c)) >= 0) klein = true;
      for (char c = 'A'; c<='Z'; c++) if (pwd.indexOf(String.valueOf(c)) >= 0) gross = true;
      for (char c = '0'; c<='9'; c++) if (pwd.indexOf(String.valueOf(c)) >= 0) ziffer = true;
      for (int i=0; i<pwd.length(); i++)
        if ( ! ( (pwd.charAt(i) >= 'a' && pwd.charAt(i) <= 'z') ||
                 (pwd.charAt(i) >= 'A' && pwd.charAt(i) <= 'Z') ||
                 (pwd.charAt(i) >= '0' && pwd.charAt(i) <= '9') ) ) sonst = true;
      int merkmale = (klein ? 1 : 0) + (gross ? 1 : 0) + (ziffer ? 1 : 0) + (sonst ? 1 : 0);
      if (merkmale<3) {
        Dialog.error(International.getString("Das Paßwort muß mindestens Zeichen aus drei der insgesamt vier Zeichengruppen "+
                     "'Kleinbuchstaben', 'Großbuchstaben', 'Ziffern' und 'sonstige Zeichen' enthalten!"));
        passwort.requestFocus();
        return;
      }
    }
    resultPassword = passwort.getPassword();
    cancel();
  }

}
