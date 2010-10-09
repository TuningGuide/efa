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

import de.nmichael.efa.efa1.Mannschaften;
import de.nmichael.efa.*;
import de.nmichael.efa.efa1.DatenFelder;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class MannschaftAuswahlFrame extends JDialog implements ActionListener {
  EfaFrame efaFrame;
  Mannschaften mannschaften;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField mannschaft = new JTextField();
  JButton okButton = new JButton();


  public MannschaftAuswahlFrame(EfaFrame efaFrame, Mannschaften mannschaften) {
    super(efaFrame);
    this.mannschaften = mannschaften;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.efaFrame = efaFrame;
    mannschaft.requestFocus();
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
      jPanel2.setLayout(gridBagLayout1);
      Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Mannschaft")+": ");
      jLabel1.setLabelFor(mannschaft);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("auswählen"));
      okButton.setNextFocusableComponent(mannschaft);
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
      });
      mannschaft.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(mannschaft,300,17);
      mannschaft.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        mannschaft_keyReleased(e);
      }
      });
      mannschaft.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          mannschaft_focusLost(e);
        }
      });
      this.setTitle(International.getString("Mannschaft auswählen"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(mannschaft,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(okButton,  BorderLayout.SOUTH);
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

  void mannschaft_keyReleased(KeyEvent e) {
    EfaFrame.vervollstaendige(mannschaft,null,mannschaften,e,null,true);
    if (e.getKeyCode() == KeyEvent.VK_ENTER) okButton_actionPerformed(null);
  }

  void mannschaft_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

  void okButton_actionPerformed(ActionEvent e) {
    DatenFelder d = mannschaften.getExactComplete(this.mannschaft.getText().trim());
    if (d != null) {
      efaFrame.setStandardMannschaft(d);
      efaFrame.abfahrt.requestFocus();
    }
    cancel();
  }


}
