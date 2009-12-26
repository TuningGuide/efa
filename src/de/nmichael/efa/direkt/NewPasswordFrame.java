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

import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.*;

// @i18n complete

public class NewPasswordFrame extends JDialog implements ActionListener {
  static String result;
  Window parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JPasswordField pwd1 = new JPasswordField();
  JLabel jLabel2 = new JLabel();
  JPasswordField pwd2 = new JPasswordField();
  JLabel titleLabel = new JLabel();


  public NewPasswordFrame(Frame parent, String admin) {
    super(parent);
    this.parent = parent;
    construct(admin);
  }
  public NewPasswordFrame(JDialog parent, String admin) {
    super(parent);
    this.parent = parent;
    construct(admin);
  }
  public NewPasswordFrame(String admin) {
    construct(admin);
  }

  void construct(String admin) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    if (admin != null) {
      titleLabel.setText(International.getMessage("Bitte gib ein neues Paßwort für den {admin_description} '{admin_name}' ein!",
              (admin.equals(EfaConfig.SUPERADMIN) ? International.getString("Super-Administrator") : International.getString("Administrator")),admin));
    } else {
      titleLabel.setText(International.getString("Bitte gib ein neues Paßwort ein!"));
    }

    EfaUtil.pack(this);
    pwd1.requestFocus();
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
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(pwd1);
      okButton.setMnemonic('O');
      okButton.setText("Ok");
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Paßwort") +
              " (" + International.getString("mind. 6 Zeichen") + "): ");
      jLabel1.setLabelFor(pwd1);
      Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Paßwort") +
              " (" + International.getString("Wiederholung") + "): ");
      jLabel2.setLabelFor(pwd2);
      titleLabel.setText(International.getString("Bitte gib ein neues Paßwort ein!"));
      pwd1.setNextFocusableComponent(pwd2);
      Dialog.setPreferredSize(pwd1,120,17);
      pwd2.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(pwd2,120,17);
      this.setTitle(International.getString("Neues Paßwort eingeben"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));
      jPanel2.add(pwd1,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 40, 20, 0), 0, 0));
      jPanel2.add(pwd2,      new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
      jPanel2.add(titleLabel,    new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 20, 20, 20), 0, 0));
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


  public static String getNewPassword(Window parent, String admin) {
    NewPasswordFrame dlg = null;
    if (parent == null) dlg = new NewPasswordFrame(admin);
    else try {
      dlg = new NewPasswordFrame((JDialog)parent,admin);
    } catch(ClassCastException e) {
      dlg = new NewPasswordFrame((JFrame)parent,admin);
    }
    Dialog.setDlgLocation(dlg,parent);
    return getNewPassword(dlg,admin);
  }

  public static String getNewPassword(Frame parent, String admin) {
    NewPasswordFrame dlg = null;
    if (parent != null) dlg = new NewPasswordFrame(parent,admin);
    else dlg = new NewPasswordFrame(admin);
    Dialog.setDlgLocation(dlg,parent);
    return getNewPassword(dlg,admin);
  }

  public static String getNewPassword(NewPasswordFrame dlg, String admin) {
    result = null;
    dlg.setModal(true);
    dlg.show();
    return result;
  }

  void okButton_actionPerformed(ActionEvent e) {
    String p1 = new String (pwd1.getPassword()).trim();
    String p2 = new String (pwd2.getPassword()).trim();

    if (p1.length()<6) {
      Dialog.error(International.getMessage("Das Paßwort muß mindestens {n} Zeichen lang sein!",6));
      pwd1.requestFocus();
      return;
    }

    if (!p1.equals(p2)) {
      Dialog.error(International.getString("Das Paßwort im zweiten Feld muß mit dem im ersten Feld identisch sein!"));
      pwd2.requestFocus();
      return;
    }

    result = p1;
    cancel();
  }


}
