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
import java.util.*;
import de.nmichael.efa.*;

public class FahrtenbuchAutoContinueFrame extends JDialog implements ActionListener {
  AdminFrame parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField datum = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField filename = new JTextField();
  JTextArea info = new JTextArea();
  JButton fileSelectButton = new JButton();


  public FahrtenbuchAutoContinueFrame(AdminFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      dataIni();
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
      okButton.setNextFocusableComponent(datum);
      okButton.setMnemonic('S');
      okButton.setText("Speichern");
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      jLabel1.setText("Neues Fahrtenbuch erstellen am: ");
      jLabel2.setText("Dateiname des neuen Fahrtenbuchs: ");
      info.setMinimumSize(new Dimension(0, 100));
      info.setPreferredSize(new Dimension(50, 100));
      info.setEditable(false);
      filename.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(filename,200,19);
      filename.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(FocusEvent e) {
          filename_focusGained(e);
        }
      });
      datum.setNextFocusableComponent(filename);
      Dialog.setPreferredSize(datum,200,19);
      datum.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          datum_focusLost(e);
        }
      });
      fileSelectButton.setPreferredSize(new Dimension(59, 25));
      fileSelectButton.setIcon(new ImageIcon(FahrtenbuchAutoContinueFrame.class.getResource("/de/nmichael/efa/img/prog_save.gif")));
      fileSelectButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fileSelectButton_actionPerformed(e);
        }
    });
      this.setTitle("Automatisches Erstellen eines neuen Fahrtenbuchs konfigurieren");
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(datum,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(filename,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(info,    new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0));
      jPanel2.add(fileSelectButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

      info.append("Diese Funktion ist dafür gedacht, zu einem bestimmten Datum (z.B. Neujahr)\n"+
                  "die zur Zeit geöffnete Fahrtenbuchdatei abzuschließen und eine neue zu\n"+
                  "beginnen.\n"+
                  "Hinweis: Sollten zu dem Zeitpunkt des Wechsels noch Boote auf dem Wasser sein,\n"+
                  "so werden diese Fahrten abgebrochen und der Administrator per Nachricht über\n"+
                  "den Abbruch der Fahrten informiert.");

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

  void dataIni() {
    if (Daten.efaConfig == null) return;
    if (Daten.efaConfig.efaDirekt_autoNewFb_datum == null) {
      datum.setText("");
    } else {
      datum.setText(Daten.efaConfig.efaDirekt_autoNewFb_datum.tag+"."+Daten.efaConfig.efaDirekt_autoNewFb_datum.monat+"."+Daten.efaConfig.efaDirekt_autoNewFb_datum.jahr);
    }
    filename.setText(Daten.efaConfig.efaDirekt_autoNewFb_datei);
  }

  void filename_focusGained(FocusEvent e) {
    if (filename.getText().trim().length() == 0 && datum.getText().trim().length()>0 &&
        Daten.fahrtenbuch != null) {
      String s = Daten.fahrtenbuch.getFileName();
      Calendar cal = new GregorianCalendar();
      filename.setText(EfaUtil.getPathOfFile(s)+Daten.fileSep+(cal.get(Calendar.YEAR)+1)+".efb");
    }
  }

  void datum_focusLost(FocusEvent e) {
    String s = datum.getText().trim();
    if (s.length() == 0) datum.setText("");
    else {
      Calendar cal = new GregorianCalendar();
      TMJ tmj = EfaUtil.correctDate(s,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR));
      datum.setText(tmj.tag+"."+tmj.monat+"."+tmj.jahr);
    }
  }

  void fileSelectButton_actionPerformed(ActionEvent e) {
    String base = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName());
    if (filename.getText().trim().length()>0) base = EfaUtil.getPathOfFile(filename.getText().trim());
    String dat = Dialog.dateiDialog(this,"Fahrtenbuchdatei auswählen","efa Fahrtenbuch (*.efb)","efb",base,true);
    if (dat != null) {
      filename.setText(dat);
    }
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (Daten.efaConfig != null) {
      String tmp = datum.getText().trim();
      String datei = filename.getText().trim();
      if (tmp.length() > 0 && datei.length() > 0) {
        if ((new File(datei)).exists()) {
          Dialog.error("Die Datei\n"+
                       datei+"\n"+
                       "existiert bereits. Bitte gib eine noch nicht existierende\n"+
                       "Datei an.");
          return;
        }
      }
      if (tmp.length() > 0) {
        Daten.efaConfig.efaDirekt_autoNewFb_datum = EfaUtil.string2date(tmp,-1,-1,-1);
        if (Daten.efaConfig.efaDirekt_autoNewFb_datum.tag == -1 || Daten.efaConfig.efaDirekt_autoNewFb_datum.monat == -1 || Daten.efaConfig.efaDirekt_autoNewFb_datum.jahr == -1) Daten.efaConfig.efaDirekt_autoNewFb_datum = null;
      } else Daten.efaConfig.efaDirekt_autoNewFb_datum = null;
      Daten.efaConfig.efaDirekt_autoNewFb_datei = filename.getText().trim();
      Daten.efaConfig.writeFile();
    }
    cancel();
  }


}
