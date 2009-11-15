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

public class SimpleInputFrame extends JDialog implements ActionListener {
  private static String input;

  DatenListe d;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel label = new JLabel();
  JTextField text = new JTextField();


  public SimpleInputFrame(String title, String message, DatenListe d) {
    iniFrame(title,message,d);
  }
  public SimpleInputFrame(JFrame parent, String title, String message, DatenListe d) {
    super(parent);
    iniFrame(title,message,d);
  }
  public SimpleInputFrame(JDialog parent, String title, String message, DatenListe d) {
    super(parent);
    iniFrame(title,message,d);
  }

  void iniFrame(String title, String message, DatenListe d) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      this.setTitle(title);
      label.setText(message);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
//    this.parent = parent;
    text.requestFocus();
    this.d = d;
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      input = null;
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
      okButton.setNextFocusableComponent(text);
      okButton.setText("OK");
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      label.setText("jLabel1");
      text.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(text,400,17);
      text.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          text_keyReleased(e);
        }
      });
      text.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          text_focusLost(e);
        }
      });
      this.setTitle("Eingabe");
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(label,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
      jPanel2.add(text,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      input = null;
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

  void text_keyReleased(KeyEvent e) {
    if (d == null) return;
    EfaFrame.vervollstaendige(text,null,d,e,null,true);
    if (e != null && e.getKeyCode() == KeyEvent.VK_ENTER) okButton_actionPerformed(null);
  }

  void okButton_actionPerformed(ActionEvent e) {
    input = text.getText().trim();
    cancel();
  }

  public static String showInputDialog(String title, String message, DatenListe d) {
    try {
      JDialog parent = (JDialog)Dialog.frameCurrent();
      if (parent != null) return showInputDialog(title, message, d, parent);
    } catch(Exception e) {
      try {
        JFrame parent = (JFrame)Dialog.frameCurrent();
        if (parent != null) showInputDialog(title, message, d, parent);
      } catch(Exception ee) {
      }
    }
    return showInputDialog(title, message, d, (JDialog)null);
  }

  public static String showInputDialog(String title, String message, DatenListe d, JDialog parent) {
    input = null;

    SimpleInputFrame dlg;
    if (parent != null) dlg = new SimpleInputFrame(parent,title,message,d);
    else dlg = new SimpleInputFrame(title,message,d);

    Dialog.setDlgLocation(dlg);
    dlg.setModal(true);
    dlg.setEnabled(true);
    dlg.toFront();
    dlg.show();

    return input;
  }

  public static String showInputDialog(String title, String message, DatenListe d, JFrame parent) {
    input = null;

    SimpleInputFrame dlg;
    if (parent != null) dlg = new SimpleInputFrame(parent,title,message,d);
    else dlg = new SimpleInputFrame(title,message,d);

    Dialog.setDlgLocation(dlg);
    dlg.setModal(true);
    dlg.setEnabled(true);
    dlg.toFront();
    dlg.show();

    return input;
  }


  void text_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
  }

}
