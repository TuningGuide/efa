package de.nmichael.efa.core;

import de.nmichael.efa.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

// @i18n complete

public class AusgabeFrame extends JDialog implements ActionListener {
  public BorderLayout borderLayout1 = new BorderLayout();
  public JButton closeButton = new JButton();
  public JScrollPane jScrollPane1 = new JScrollPane();
  public JTable out = new JTable();
  public JTextArea outText = null;

  public AusgabeFrame(JFrame parent) { // für Aufruf aus Dialog
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
//    Dialog.frameOpened(this); // Nicht!!!
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
  }
  public AusgabeFrame(JDialog parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
//    Dialog.frameOpened(this); // Nicht!!!
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
  }
  public AusgabeFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
//    Dialog.frameOpened(this); // Nicht!!!
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
  }

  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    Mnemonics.setButton(this, closeButton, International.getStringWithMnemonic("Schließen"));
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });
    this.setTitle(International.getString("Ausgabe"));
    this.getContentPane().setLayout(borderLayout1);
    jScrollPane1.setMinimumSize(new Dimension(300, 200));
    jScrollPane1.setPreferredSize(new Dimension(600, 300));
    this.getContentPane().add(closeButton, BorderLayout.SOUTH);
    this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    this.requestFocus();
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
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }



}
