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
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class KonsoleFrame extends JDialog implements ActionListener {
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea out = new JTextArea();
  JButton okButton = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();


  public KonsoleFrame(EfaFrame parent, String logfile) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);

    readLog(logfile);
    okButton.requestFocus();
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


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    okButton.setNextFocusableComponent(out);
    Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Schließen"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel1.setLayout(borderLayout1);
    jScrollPane1.setMinimumSize(new Dimension(200, 100));
    jScrollPane1.setPreferredSize(new Dimension(600, 200));
    this.setTitle(International.getString("Java-Konsole"));
    out.setNextFocusableComponent(okButton);
    out.setDisabledTextColor(Color.black);
    out.setEditable(false);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    jPanel1.add(okButton, BorderLayout.SOUTH);
    jScrollPane1.getViewport().add(out, null);
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


  void readLog(String logfile) {
    if (logfile == null) {
      out.append("\n"+
              LogString.logstring_fileReadFailed(logfile, International.getString("Logdatei")));
      return;
    }
    BufferedReader f;
    String s;
    try {
      f = new BufferedReader(new InputStreamReader(new FileInputStream(logfile)));
      while ((s = f.readLine()) != null) {
        out.append(s+"\n");
      }
      f.close();
    } catch(FileNotFoundException e) {
    } catch(IOException e) {
      out.append("\n"+International.getString("FEHLER beim Lesen der Logdatei '{logfile}'.",logfile));
    }
    out.setCaretPosition(0);
  }

  void okButton_actionPerformed(ActionEvent e) {
    cancel();
  }


}
