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

import de.nmichael.efa.*;
import de.nmichael.efa.efa1.DatenListe;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class WriteProtectFrame extends JDialog implements ActionListener {
  DatenListe[] files;
  String[] filenames;
  JLabel[] dateityp;
  JLabel[] dateiname;
  JLabel[] dateistatus;
  JCheckBox[] dateiselect;

  JPanel allPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  JButton closeButton = new JButton();
  JPanel actionPanel = new JPanel();
  JPanel actionPanel1 = new JPanel();
  JPanel actionPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton selectAllButton = new JButton();
  JButton selectNoneButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JButton setWriteProtectButton = new JButton();
  JButton removeWriteProtectButton = new JButton();
  JPanel workPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel filesPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JPanel defaultPwPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JPasswordField defaultPw = new JPasswordField();
  JLabel jLabel2 = new JLabel();
  JButton selectFbButton = new JButton();


  public WriteProtectFrame(JFrame parent) {
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
    closeButton.requestFocus();
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


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    allPanel.setLayout(borderLayout1);
    Mnemonics.setButton(this, closeButton, International.getStringWithMnemonic("Schließen"));
    closeButton.setNextFocusableComponent(defaultPw);
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });
    actionPanel.setLayout(borderLayout3);
    actionPanel1.setLayout(gridBagLayout1);
    actionPanel2.setLayout(gridBagLayout4);
    Mnemonics.setButton(this, selectAllButton, International.getStringWithMnemonic("alle auswählen"));
    selectAllButton.setNextFocusableComponent(selectFbButton);
    selectAllButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectAllButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, selectNoneButton, International.getStringWithMnemonic("keine auswählen"));
    selectNoneButton.setNextFocusableComponent(selectAllButton);
    selectNoneButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectNoneButton_actionPerformed(e);
      }
    });
    jLabel1.setText(International.getString("für alle ausgewählten Dateien")+": ");
    Mnemonics.setButton(this, setWriteProtectButton, International.getStringWithMnemonic("Schreibschutz aktivieren"));
    setWriteProtectButton.setBackground(Color.red);
    setWriteProtectButton.setNextFocusableComponent(removeWriteProtectButton);
    setWriteProtectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setWriteProtectButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, removeWriteProtectButton, International.getStringWithMnemonic("Schreibschutz deaktivieren"));
    removeWriteProtectButton.setBackground(Color.green);
    removeWriteProtectButton.setNextFocusableComponent(closeButton);
    removeWriteProtectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeWriteProtectButton_actionPerformed(e);
      }
    });
    workPanel.setLayout(borderLayout2);
    filesPanel.setLayout(gridBagLayout2);
    defaultPwPanel.setLayout(gridBagLayout3);
    defaultPw.setNextFocusableComponent(selectNoneButton);
    defaultPw.setPreferredSize(new Dimension(150, 17));
    defaultPw.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        defaultPw_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("bis zum Beenden von efa für alle Paßwortfragen folgendes Paßwort verwenden")+": ");
    jLabel2.setLabelFor(defaultPw);
    filesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    Mnemonics.setButton(this, selectFbButton, International.getStringWithMnemonic("Fahrtenbuch auswählen"));
    selectFbButton.setNextFocusableComponent(setWriteProtectButton);
    selectFbButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectFbButton_actionPerformed(e);
      }
    });
    this.setTitle(International.getString("Zugriffsschutz für Dateien"));
    this.getContentPane().add(allPanel, BorderLayout.CENTER);
    allPanel.add(closeButton, BorderLayout.SOUTH);
    workPanel.add(actionPanel,  BorderLayout.SOUTH);
    actionPanel.add(actionPanel1, BorderLayout.NORTH);
    actionPanel.add(actionPanel2, BorderLayout.CENTER);
    actionPanel1.add(selectAllButton,              new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    actionPanel1.add(selectNoneButton,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    actionPanel1.add(selectFbButton,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    actionPanel2.add(jLabel1,     new GridBagConstraints(0, 21, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    actionPanel2.add(setWriteProtectButton,     new GridBagConstraints(1, 21, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    actionPanel2.add(removeWriteProtectButton,      new GridBagConstraints(2, 21, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    allPanel.add(workPanel, BorderLayout.EAST);
    workPanel.add(filesPanel, BorderLayout.CENTER);
    workPanel.add(defaultPwPanel, BorderLayout.NORTH);
    defaultPwPanel.add(defaultPw,      new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    defaultPwPanel.add(jLabel2,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));



    iniData();

  }

  void iniData() {

    // Files
    files = new DatenListe[11];
    for (int i=0; i<files.length; i++) files[i] = null;
    if (Daten.fahrtenbuch != null) files[0] = Daten.fahrtenbuch;
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().boote != null) files[1] = Daten.fahrtenbuch.getDaten().boote;
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null) files[2] = Daten.fahrtenbuch.getDaten().mitglieder;
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().ziele != null) files[3] = Daten.fahrtenbuch.getDaten().ziele;
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().statistik != null) files[4] = Daten.fahrtenbuch.getDaten().statistik;
    if (Daten.synBoote != null) files[5] = Daten.synBoote;
    if (Daten.synMitglieder != null) files[6] = Daten.synMitglieder;
    if (Daten.synZiele != null) files[7] = Daten.synZiele;
    if (Daten.vereinsConfig != null) files[8] = Daten.vereinsConfig;
    if (Daten.adressen != null) files[9] = Daten.adressen;
    if (Daten.efaConfig != null) files[10] = Daten.efaConfig;

    // Filenames
    filenames = new String[files.length];
    for (int i=0; i<filenames.length; i++)
      if (files[i] == null) filenames[i] = null;
      else filenames[i] = files[i].getFileName();

    // Dateitypen
    dateityp = new JLabel[files.length];
    for (int i=0; i<dateityp.length; i++) {
      dateityp[i] = new JLabel();
      switch(i) {
        case  0: dateityp[i].setText(International.getString("Fahrtenbuch")+": "); break;
        case  1: dateityp[i].setText(International.getString("Bootsliste")+": "); break;
        case  2: dateityp[i].setText(International.getString("Mitgliederliste")+": "); break;
        case  3: dateityp[i].setText(International.getString("Zielliste")+": "); break;
        case  4: dateityp[i].setText(International.getString("Statistikeinstellungen")+": "); break;
        case  5: dateityp[i].setText(International.getString("Boots-Synonymliste")+": "); break;
        case  6: dateityp[i].setText(International.getString("Mitglieder-Synonymliste")+": "); break;
        case  7: dateityp[i].setText(International.getString("Ziel-Synonymliste")+": "); break;
        case  8: dateityp[i].setText(International.getString("Vereinskonfiguration")+": "); break;
        case  9: dateityp[i].setText(International.getString("Adreßliste")+": "); break;
        case 10: dateityp[i].setText(International.getString("efa-Konfiguration")+": "); break;
      }
      filesPanel.add(dateityp[i],  new GridBagConstraints(1, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
    }

    // Dateinamen
    dateiname = new JLabel[files.length];
    for (int i=0; i<dateiname.length; i++) {
      dateiname[i] = new JLabel();
      dateiname[i].setForeground(Color.black);
      dateiname[i].setText( (filenames[i] == null ? "" : filenames[i]) );
      filesPanel.add(dateiname[i],  new GridBagConstraints(2, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
    }

    // Dateistatus
    dateistatus = new JLabel[files.length];
    for (int i=0; i<dateistatus.length; i++) {
      dateistatus[i] = new JLabel();
      dateistatus[i].setForeground(Color.black);
      dateistatus[i].setOpaque(true);
      filesPanel.add(dateistatus[i],  new GridBagConstraints(3, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
    }

    // Dateiselect
    dateiselect = new JCheckBox[files.length];
    for (int i=0; i<dateiselect.length; i++) {
      dateiselect[i] = new JCheckBox();
      if (i+1<dateiselect.length) dateiselect[i].setSelected(true);
//      dateiselect[i].setText("auswählen");
      filesPanel.add(dateiselect[i],  new GridBagConstraints(0, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0));
    }

    updateFileList();

  }

  void updateFileList() {
    for (int i=0; i<dateistatus.length; i++) {
      if (files[i] == null) {
        dateistatus[i].setText(International.getString("nicht verfügbar"));
        dateiselect[i].setEnabled(false);
        dateiselect[i].setSelected(false);
      } else if (files[i].isWriteProtected()) {
        dateistatus[i].setText(International.getString("schreibgeschützt") + (files[i].isPassword() ? "(PW)" : "") );
        dateistatus[i].setBackground(Color.red);
      } else {
        dateistatus[i].setText(International.getString("nicht schreibgeschützt"));
        dateistatus[i].setBackground(Color.green);
      }
    }
  }

  // Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  // Close the dialog
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
  }

  //Close the dialog on a button event
  public void actionPerformed(ActionEvent e) {
  }

  void selectDatei(int i, boolean select) {
    if (files[i] == null) {
      dateiselect[i].setEnabled(false);
      dateiselect[i].setSelected(false);
    } else dateiselect[i].setSelected(select);
  }

  void selectAllButton_actionPerformed(ActionEvent e) {
    for (int i=0; i<dateiselect.length; i++)
      selectDatei(i,true);
  }

  void selectNoneButton_actionPerformed(ActionEvent e) {
    for (int i=0; i<dateiselect.length; i++)
      selectDatei(i,false);
  }

  void selectFbButton_actionPerformed(ActionEvent e) {
    for (int i=0; i<dateiselect.length; i++)
      selectDatei(i,i<5);
  }

  void setWriteProtectButton_actionPerformed(ActionEvent e) {
    boolean empty = true;
    for (int i=0; i<files.length; i++) if (dateiselect[i].isSelected()) empty = false;
    if (empty) {
      Dialog.infoDialog(International.getString("Keine Dateien ausgewählt"),
              International.getString("Bitte wähle zuerst mindestens eine Datei aus!"));
      return;
    }

    String pwd = Dialog.inputDialog(International.getString("Paßwort für Schreibschutz"),
            International.getString("Paßwort für Schreibschutz") +
            " (" + International.getString("kann leergelassen werden") + "):");
    if (pwd == null) return;

    for (int i=0; i<files.length; i++)
      if (dateiselect[i].isSelected())
        files[i].setWriteProtect(true,pwd);
    updateFileList();

  }

  void removeWriteProtectButton_actionPerformed(ActionEvent e) {
    boolean empty = true;
    for (int i=0; i<files.length; i++) if (dateiselect[i].isSelected()) empty = false;
    if (empty) {
      Dialog.infoDialog(International.getString("Keine Dateien ausgewählt"),
              International.getString("Bitte wähle zuerst mindestens eine Datei aus!"));
      return;
    }

    for (int i=0; i<files.length; i++)
      if (dateiselect[i].isSelected()) {
        files[i].setWriteProtect(false, new String(defaultPw.getPassword()));
      }
    updateFileList();
  }

  void defaultPw_focusLost(FocusEvent e) {
    String s="";
    for (int i=0; i< defaultPw.getPassword().length; i++) s += defaultPw.getPassword()[i];

    if (s.length() == 0) Daten.defaultWriteProtectPw = null;
    else Daten.defaultWriteProtectPw = s;
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }


}
