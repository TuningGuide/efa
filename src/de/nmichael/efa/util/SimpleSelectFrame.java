/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.core.*;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class SimpleSelectFrame extends JDialog implements ActionListener {
  private static String result;
  private static int resultID;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel label = new JLabel();
  JComboBox select = new JComboBox();


  public SimpleSelectFrame(String title, String message, String[] items, int preselect) {
    iniFrame(title,message,items,preselect);
  }
  public SimpleSelectFrame(JFrame parent, String title, String message, String[] items, int preselect) {
    super(parent);
    iniFrame(title,message,items,preselect);
  }
  public SimpleSelectFrame(JDialog parent, String title, String message, String[] items, int preselect) {
    super(parent);
    iniFrame(title,message,items,preselect);
  }

  void iniFrame(String title, String message, String[] items, int preselect) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      for (int i=0; i<items.length; i++) {
          select.addItem(items[i]);
      }
      if (items.length > 0) {
          if (preselect >= 0 && preselect < items.length) {
              select.setSelectedIndex(preselect);
          } else {
              select.setSelectedIndex(0);
          }
      }
      this.setTitle(title);
      label.setText(message);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    select.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      result = null;
      resultID = -1;
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
  }


  // Initialisierung des Frames
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("OK"));
      okButton.setNextFocusableComponent(select);
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      label.setText("jLabel1");
      select.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(select,400,17);
      this.setTitle(International.getString("Eingabe"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(label,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
      jPanel2.add(select,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      result = null;
      resultID = -1;
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
    if (select.getSelectedIndex()>=0) {
        result = (String)select.getSelectedItem();
        resultID = select.getSelectedIndex();
    }
    cancel();
  }

  public static int showInputDialog(String title, String message, String[] items, int preselect) {
    try {
      JDialog parent = (JDialog)Dialog.frameCurrent();
      if (parent != null) return showInputDialog(title, message, items, parent,preselect);
    } catch(Exception e) {
      try {
        JFrame parent = (JFrame)Dialog.frameCurrent();
        if (parent != null) showInputDialog(title, message, items, parent,preselect);
      } catch(Exception ee) {
      }
    }
    return showInputDialog(title, message, items, (JDialog)null,preselect);
  }

  public static int showInputDialog(String title, String message, String[] items, JDialog parent, int preselect) {
    result = null;
    resultID = -1;

    SimpleSelectFrame dlg;
    if (parent != null) dlg = new SimpleSelectFrame(parent,title,message,items,preselect);
    else dlg = new SimpleSelectFrame(title,message,items,preselect);

    Dialog.setDlgLocation(dlg);
    dlg.setModal(true);
    dlg.setEnabled(true);
    dlg.toFront();
    dlg.show();

    return resultID;
  }

  public static int showInputDialog(String title, String message, String[] items, JFrame parent, int preselect) {
    result = null;
    resultID = -1;

    SimpleSelectFrame dlg;
    if (parent != null) dlg = new SimpleSelectFrame(parent,title,message,items,preselect);
    else dlg = new SimpleSelectFrame(title,message,items,preselect);

    Dialog.setDlgLocation(dlg);
    dlg.setModal(true);
    dlg.setEnabled(true);
    dlg.toFront();
    dlg.show();

    return resultID;
  }

  public static String getLastResult() {
      return result;
  }

  public static int getLastResultID() {
      return resultID;
  }

}
