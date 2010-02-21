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
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.*;

// @i18n complete

public class AdminLoginFrame extends JDialog implements ActionListener {
  static Admin result;

  Window parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel nameLabel = new JLabel();
  JTextField name = new JTextField();
  JLabel passwortLabel = new JLabel();
  JPasswordField passwort = new JPasswordField();
  JLabel jLabel1 = new JLabel();
  JLabel grundLabel = new JLabel();


  public AdminLoginFrame(Frame parent, String grund) {
    super(parent);
    this.parent = parent;
    construct(grund);
  }
  public AdminLoginFrame(JDialog parent, String grund) {
    super(parent);
    this.parent = parent;
    construct(grund);
  }
  public AdminLoginFrame(String grund) {
    construct(grund);
  }

  void construct(String grund) {
    Dialog.frameOpened(this);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    grundLabel.setText( (grund==null ? "" : grund) );

    EfaUtil.pack(this);
    name.requestFocus();
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
      okButton.dispatchEvent(evt);
      try { Thread.sleep(100); } catch(Exception ee) {}
      okButton_actionPerformed(evt);
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
    okButton.setNextFocusableComponent(name);
    Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("OK"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, nameLabel, International.getStringWithMnemonic("Admin-Name")+": ");
    nameLabel.setLabelFor(name);
    Mnemonics.setLabel(this, passwortLabel, International.getStringWithMnemonic("Paßwort")+": ");
    passwortLabel.setLabelFor(passwort);
    name.setNextFocusableComponent(passwort);
    Dialog.setPreferredSize(name,100,17);
    name.setMinimumSize(name.getPreferredSize()); // neu (Bugfix 1.7.0_03)
    name.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        name_focusLost(e);
      }
    });
    passwort.setNextFocusableComponent(okButton);
    Dialog.setPreferredSize(passwort,100,17);
    passwort.setMinimumSize(passwort.getPreferredSize()); // neu (Bugfix 1.7.0_03)
    this.setTitle(International.getString("Admin-Login"));
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText(International.getString("Admin-Login erforderlich."));
    grundLabel.setHorizontalAlignment(SwingConstants.CENTER);
    grundLabel.setText(International.getString("Grund"));
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(okButton, BorderLayout.SOUTH);
    jPanel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(nameLabel,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
    jPanel2.add(name,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
    jPanel2.add(passwortLabel,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 0), 0, 0));
    jPanel2.add(passwort,     new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
    jPanel2.add(jLabel1,    new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
    jPanel2.add(grundLabel,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
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


  public static Admin login(Frame parent, String grund) {
    return login(parent,grund,null);
  }

  public static Admin login(Window parent, String grund, String admin) {
    AdminLoginFrame dlg = null;
    if (parent == null) dlg = new AdminLoginFrame(grund);
    else try {
      dlg = new AdminLoginFrame((JDialog)parent,grund);
    } catch(ClassCastException e) {
      dlg = new AdminLoginFrame((JFrame)parent,grund);
    }
    Dialog.setDlgLocation(dlg,parent);
    return login(dlg,grund,admin);
  }

  public static Admin login(Frame parent, String grund, String admin) {
    AdminLoginFrame dlg = null;
    if (parent != null) dlg = new AdminLoginFrame(parent,grund);
    else dlg = new AdminLoginFrame(grund);
    Dialog.setDlgLocation(dlg,parent);
    return login(dlg,grund,admin);
  }

  public static Admin login(AdminLoginFrame dlg, String grund, String admin) {
    result = null;
    dlg.setModal(true);

    if (admin != null) {
      dlg.name.setText(admin);
      dlg.name.setEditable(false);
      dlg.passwort.requestFocus();
    }

    dlg.show();
    return result;
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (name.getText().trim().length()==0) {
      Dialog.error(International.getString("Kein Admin-Name eingegeben!"));
      name.requestFocus();
      cancel();
      return;
    }
    String pwd = new String(passwort.getPassword()).trim();
    if (pwd.length()==0) {
      Dialog.error(International.getString("Kein Paßwort eingegeben!"));
      passwort.requestFocus();
      return;
    }
    Admin admin = null;
    if ( (admin = Admin.login(name.getText().trim(),pwd)) == null) {
      Dialog.error(International.getString("Admin-Name oder Paßwort ungültig!"));
      Logger.log(Logger.WARNING,Logger.MSG_ADMIN_LOGINFAILURE,International.getString("Admin-Login")+": "+
              International.getMessage("Name {name} oder Paßwort ungültig!",name.getText().trim()));
      passwort.requestFocus();
      return;
    }
    Logger.log(Logger.INFO,Logger.MSG_ADMIN_LOGIN,International.getString("Admin-Login")+": "+
            International.getString("Name")+": "+name.getText().trim());
    result = admin;
    cancel();
  }

  void name_focusLost(FocusEvent e) {
    name.setText(name.getText().trim().toLowerCase());
  }


}
