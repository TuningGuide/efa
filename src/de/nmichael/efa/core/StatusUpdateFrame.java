/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.efa1.Mitglieder;
import de.nmichael.efa.*;
import de.nmichael.efa.core.AuswahlFrame;
import de.nmichael.efa.efa1.DatenFelder;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class StatusUpdateFrame extends JDialog implements ActionListener {
  AuswahlFrame parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel panel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel[][] label;
  JTextField[] jahrgang;
  JComboBox[] statusAlt;
  JComboBox[] statusNeu;


  public StatusUpdateFrame(AuswahlFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      frameIni();
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
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Status aktualisieren"));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      panel.setLayout(gridBagLayout1);
      this.setTitle(International.getString("Mitglieder-Status aktualisieren"));
      jLabel1.setText(International.getString("Der Status von Mitgliedern, die folgende Eigenschaften erfüllen, " +
    "wird geändert:"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(panel, BorderLayout.CENTER);
      panel.add(jLabel1,     new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
  }

  void setupCombo(JComboBox combo) {
    for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++) combo.addItem(Daten.fahrtenbuch.getDaten().status[i]);
  }

  void frameIni() {
    String[] status = Daten.fahrtenbuch.getDaten().status;
    label = new JLabel[status.length][3];
    jahrgang = new JTextField[status.length];
    statusAlt = new JComboBox[status.length];
    statusNeu = new JComboBox[status.length];
    for (int i=0; i<status.length; i++) {
      statusAlt[i] = new JComboBox();
      jahrgang[i] = new JTextField();
      statusNeu[i] = new JComboBox();
    }
    for (int i=0; i<status.length; i++) {
      label[i][0] = new JLabel(); label[i][0].setText(International.getString("Ändere den Status von Mitgliedern mit aktuellem Status {status}",""));
      panel.add(label[i][0], new GridBagConstraints(0, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, (i == status.length-1 ? 10 : 0), 0), 0, 0));

      setupCombo(statusAlt[i]); statusAlt[i].setSelectedIndex(i);
      statusAlt[i].setNextFocusableComponent(jahrgang[i]);
      panel.add(statusAlt[i], new GridBagConstraints(1, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, (i == status.length-1 ? 10 : 0), 0), 0, 0));

      label[i][1] = new JLabel(); label[i][1].setText(" "+International.getString("und") + " " +
              International.getString("Jahrgang")+" ");
      panel.add(label[i][1], new GridBagConstraints(2, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, (i == status.length-1 ? 10 : 0), 0), 0, 0));

      jahrgang[i].setPreferredSize(new Dimension(60,21));
      jahrgang[i].setNextFocusableComponent(statusNeu[i]);
      jahrgang[i].addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          jahrgang_focusLost(e);
        }
      });
      jahrgang[i].addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          updateLabels();
        }
      });
      panel.add(jahrgang[i], new GridBagConstraints(3, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, (i == status.length-1 ? 10 : 0), 0), 0, 0));

      label[i][2] = new JLabel(); label[i][2].setText(" "+International.getString("zu Status")+": ");
      panel.add(label[i][2], new GridBagConstraints(4, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, (i == status.length-1 ? 10 : 0), 0), 0, 0));

      setupCombo(statusNeu[i]); statusNeu[i].setSelectedIndex(i);
      statusNeu[i].setNextFocusableComponent( (i == status.length-1 ? (Component)okButton : (Component)statusAlt[i+1]) );
      panel.add(statusNeu[i], new GridBagConstraints(5, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, (i == status.length-1 ? 10 : 0), 10), 0, 0));
    }
    jahrgang[0].requestDefaultFocus();

    okButton.setNextFocusableComponent(statusAlt[0]);
    updateLabels();
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

  int updateLabels() {
    int c=0;
    for (int i=0; i<jahrgang.length; i++) {
      if (jahrgang[i].getText().trim().length() == 0) {
        label[i][0].setForeground(Color.gray);
        label[i][1].setForeground(Color.gray);
        label[i][2].setForeground(Color.gray);
      } else {
        label[i][0].setForeground(Color.black);
        label[i][1].setForeground(Color.black);
        label[i][2].setForeground(Color.black);
        c++;
      }
    }
    return c;
  }

  void jahrgang_focusLost(FocusEvent e) {
    if (e == null) return;
    JTextField field = (JTextField)e.getSource();
    if (field.getText().trim().length()==0) {
      field.setText("");
      return;
    }
    int year = EfaUtil.string2date(field.getText(),0,0,0).tag;
    if (year<0) year = 0;
    if (year<100) year += 1900;
    field.setText(Integer.toString(year));
    updateLabels();
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (updateLabels() == 0) {
      Dialog.error(International.getString("Bitte trage mindestens einen Jahrgang in eines der Felder ein, "+
                   "um mindestens eine Ersetzung anzugeben!"));
      return;
    }
    for (int i=0; i<statusAlt.length; i++) {
      if (jahrgang[i].getText().trim().length()>0 && statusAlt[i].getSelectedIndex() == statusNeu[i].getSelectedIndex()) {
        Dialog.error(International.getMessage("In Zeile {row} sind alter und neuer Status identisch! "+
                     "Bitte wähle als neuen Status einen anderen Wert.",(i+1)));
        return;
      }
    }

    // RUN
    String mitgl = ""; // geänderte Mitglieder (Namen)
    int anz=0; // geänderte Mitglieder
    int ges=0; // Mitglieder insgesamt
    for (DatenFelder d = Daten.fahrtenbuch.getDaten().mitglieder.getCompleteFirst();
         d != null;
         d = Daten.fahrtenbuch.getDaten().mitglieder.getCompleteNext()) {
      ges++;
      for (int i=0; i<jahrgang.length; i++) {
        if (jahrgang[i].getText().trim().length() == 0) continue;
        if (jahrgang[i].getText().trim().equals(d.get(Mitglieder.JAHRGANG)) &&
            d.get(Mitglieder.STATUS).equals(statusAlt[i].getSelectedItem())) {
          // passender Eintrag gefunden!
          d.set(Mitglieder.STATUS,(String)statusNeu[i].getSelectedItem());
          anz++;
          mitgl += EfaUtil.getFullName(d.get(Mitglieder.VORNAME),d.get(Mitglieder.NACHNAME),d.get(Mitglieder.VEREIN)) + ": " +
                   statusAlt[i].getSelectedItem() + " -> " + statusNeu[i].getSelectedItem() + "\n";
          break;
        }
      }
    }
    Dialog.infoDialog(International.getString("Fertig"),
                      International.getMessage("Von insgesamt {total} Mitgliedern wurden {count} aktualisiert",ges,anz)+":\n"+mitgl);
    if (anz>0) Daten.fahrtenbuch.getDaten().mitglieder.setChanged();
    parent.update();
    cancel();
  }


}
