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

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Vector;

// @i18n complete

public class NeuesFahrtenbuchFrame extends JDialog implements ActionListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JButton SaveButton = new JButton();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField fahrtenbuch = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField boote = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField mitglieder = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField ziele = new JTextField();
  JButton fahrtenbuchButton = new JButton();
  JButton booteButton = new JButton();
  JButton mitgliederButton = new JButton();
  JButton zieleButton = new JButton();
  JLabel jLabel5 = new JLabel();
  JRadioButton vorNach = new JRadioButton();
  JRadioButton nachVor = new JRadioButton();
  ButtonGroup namensformat = new ButtonGroup();
  EfaFrame efaFrame; // EfaFrame
  boolean neu;
  JLabel jLabel6 = new JLabel();
  JTextField status1 = new JTextField();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JTextField statistik = new JTextField();
  JTextField prevFb = new JTextField();
  JTextField nextFb = new JTextField();
  JButton statistikButton = new JButton();
  JButton prevFbButton = new JButton();
  JButton nextFbButton = new JButton();
  JLabel jLabel10 = new JLabel();
  JTextField anzMitglieder = new JTextField();

  // Neues Fahrtenbuch (ggf. fortsetzen) aus EfaFrame heraus
  public NeuesFahrtenbuchFrame(EfaFrame parent, boolean neu) {
    super(parent);
    efaFrame = parent;
    construct(neu);
  }

  // Neues Fahrtenbuch (nicht fortseten) aus anderen Frames heraus
  public NeuesFahrtenbuchFrame(Frame parent) {
    super(parent);
    construct(true);
  }
  public NeuesFahrtenbuchFrame(JDialog parent) {
    super(parent);
    construct(true);
  }

  void construct(boolean neu) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.neu = neu;
    if (!neu) {
      fahrtenbuch.disable();
      fahrtenbuchButton.disable();
      Mnemonics.setButton(this, SaveButton, International.getStringWithMnemonic("Änderungen übernehmen"));
      fahrtenbuch.setText(Daten.fahrtenbuch.getFileName());
      mitglieder.setText(Daten.fahrtenbuch.getDaten().mitgliederDatei);
      boote.setText(Daten.fahrtenbuch.getDaten().bootDatei);
      ziele.setText(Daten.fahrtenbuch.getDaten().zieleDatei);
      statistik.setText(Daten.fahrtenbuch.getDaten().statistikDatei);
      nextFb.setText(Daten.fahrtenbuch.getNextFb(false));
      prevFb.setText(Daten.fahrtenbuch.getPrevFb(false));
      vorNach.setSelected(Daten.fahrtenbuch.getDaten().erstVorname);
      nachVor.setSelected(!Daten.fahrtenbuch.getDaten().erstVorname);
      status1.setText(EfaUtil.arr2KommaList(Daten.fahrtenbuch.getDaten().status));
      anzMitglieder.setText(Integer.toString(Daten.fahrtenbuch.getDaten().anzMitglieder));
      this.setTitle(International.getString("Einstellungen zum Fahrtenbuch"));
      SaveButton.requestFocus();
    } else fahrtenbuch.requestFocus();
    EfaUtil.pack(this);
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

    SaveButton.setNextFocusableComponent(fahrtenbuch);
    Mnemonics.setButton(this, SaveButton, International.getStringWithMnemonic("Fahrtenbuch erstellen"));
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveButton_actionPerformed(e);
      }
    });
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, jLabel1, International.getString("Dateiname") + " " +
            International.getStringWithMnemonic("Fahrtenbuch")+": ");
    jLabel1.setLabelFor(fahrtenbuch);
    Mnemonics.setLabel(this, jLabel2, International.getString("Dateiname") + " " +
            International.getStringWithMnemonic("Bootsliste")+": ");
    jLabel2.setLabelFor(boote);
    boote.setNextFocusableComponent(booteButton);
    Dialog.setPreferredSize(boote,200,19);
    boote.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        boote_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        boote_focusGained(e);
      }
    });
    Mnemonics.setLabel(this, jLabel3, International.getString("Dateiname") + " " +
            International.getStringWithMnemonic("Mitgliederliste")+": ");
    jLabel3.setLabelFor(mitglieder);
    Mnemonics.setLabel(this, jLabel4, International.getString("Dateiname") + " " +
            International.getStringWithMnemonic("Zielliste")+": ");
    jLabel4.setLabelFor(ziele);
    ziele.setNextFocusableComponent(zieleButton);
    Dialog.setPreferredSize(ziele,200,19);
    ziele.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ziele_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        ziele_focusGained(e);
      }
    });
    mitglieder.setNextFocusableComponent(mitgliederButton);
    Dialog.setPreferredSize(mitglieder,200,19);
    mitglieder.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        mitglieder_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        mitglieder_focusGained(e);
      }
    });
    fahrtenbuch.setNextFocusableComponent(fahrtenbuchButton);
    Dialog.setPreferredSize(fahrtenbuch,200,19);
    fahrtenbuch.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        fahrtenbuch_focusLost(e);
      }
    });
    fahrtenbuchButton.setNextFocusableComponent(boote);
    fahrtenbuchButton.setPreferredSize(new Dimension(59, 25));
    fahrtenbuchButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_save.gif")));
    fahrtenbuchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtenbuchButton_actionPerformed(e);
      }
    });
    booteButton.setNextFocusableComponent(mitglieder);
    booteButton.setPreferredSize(new Dimension(59, 25));
    booteButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    booteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        booteButton_actionPerformed(e);
      }
    });
    mitgliederButton.setNextFocusableComponent(ziele);
    mitgliederButton.setPreferredSize(new Dimension(59, 25));
    mitgliederButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    mitgliederButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mitgliederButton_actionPerformed(e);
      }
    });
    zieleButton.setNextFocusableComponent(statistik);
    zieleButton.setPreferredSize(new Dimension(59, 25));
    zieleButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    zieleButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        zieleButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel5, International.getStringWithMnemonic("Angabe von Mitgliedernamen")+": ");
    Mnemonics.setButton(this, vorNach, International.getStringWithMnemonic("Vorname") + " " +
            International.getString("Nachname"));
    vorNach.setSelected(true);
    vorNach.setNextFocusableComponent(nachVor);
    Mnemonics.setButton(this, nachVor, International.getStringWithMnemonic("Nachname") + ", " +
            International.getString("Vorname"));
    nachVor.setNextFocusableComponent(status1);
    Mnemonics.setLabel(this, jLabel6, International.getStringWithMnemonic("Liste für Status")+": ");
    jLabel6.setLabelFor(status1);
    status1.setNextFocusableComponent(anzMitglieder);
    Dialog.setPreferredSize(status1,200,19);
    String stati = International.getString("Junior(in)") + "," +
            International.getString("Senior(in)");
    stati = stati + "," + Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST);
    stati = stati + "," + Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_OTHER);
    status1.setText(stati);
    status1.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        status1_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel7, International.getString("Dateiname") + " " +
            International.getStringWithMnemonic("Statistikeinstellungen")+": ");
    jLabel7.setLabelFor(statistik);
    statistik.setNextFocusableComponent(statistikButton);
    Dialog.setPreferredSize(statistik,200,19);
    statistik.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        statistik_focusGained(e);
      }
      public void focusLost(FocusEvent e) {
        statistik_focusLost(e);
      }
    });
    statistikButton.setNextFocusableComponent(prevFb);
    statistikButton.setPreferredSize(new Dimension(59, 25));
    statistikButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    statistikButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        statistikButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel8, International.getStringWithMnemonic("vorheriges Fahrtenbuch")+": ");
    jLabel8.setLabelFor(prevFb);
    Mnemonics.setLabel(this, jLabel9, International.getStringWithMnemonic("nächstes Fahrtenbuch")+": ");
    jLabel9.setLabelFor(nextFb);
    prevFb.setNextFocusableComponent(prevFbButton);
    Dialog.setPreferredSize(prevFb,200,19);
    nextFb.setNextFocusableComponent(nextFbButton);
    Dialog.setPreferredSize(nextFb,200,19);
    nextFbButton.setNextFocusableComponent(vorNach);
    nextFbButton.setPreferredSize(new Dimension(59, 25));
    nextFbButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    nextFbButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nextFbButton_actionPerformed(e);
      }
    });
    prevFbButton.setNextFocusableComponent(nextFb);
    prevFbButton.setPreferredSize(new Dimension(59, 25));
    prevFbButton.setIcon(new ImageIcon(NeuesFahrtenbuchFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    prevFbButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        prevFbButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel10, International.getStringWithMnemonic("Mitgliederzahl am 01.01. des Jahres")+": ");
    jLabel10.setLabelFor(anzMitglieder);
    anzMitglieder.setNextFocusableComponent(SaveButton);
    Dialog.setPreferredSize(anzMitglieder,200,19);
    anzMitglieder.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        anzMitglieder_focusLost(e);
      }
    });
    jPanel1.setMinimumSize(new Dimension(500, 300));
    this.getContentPane().add(SaveButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(fahrtenbuch, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(boote, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(mitglieder, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(ziele, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(fahrtenbuchButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(booteButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(mitgliederButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(zieleButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel5, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    jPanel1.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 1, 0, 5), 0, 0));
    jPanel1.add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    jPanel1.add(jLabel4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    jPanel1.add(nachVor, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 3, 0, 3), 0, 0));
    jPanel1.add(jLabel6, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(status1, new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel7, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel8, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel9, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(vorNach, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(statistik, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(prevFb, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(nextFb, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(statistikButton, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(prevFbButton, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(nextFbButton, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel10, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(anzMitglieder, new GridBagConstraints(2, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    namensformat.add(vorNach);
    namensformat.add(nachVor);
    this.setTitle(International.getString("Neues Fahrtenbuch erstellen"));
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


  // Vor- und Nachname vertauschen
  String switchVorNach(String s) {
    return EfaUtil.getFullName(EfaUtil.getVorname(s),EfaUtil.getNachname(s),EfaUtil.getVerein(s),!Daten.fahrtenbuch.getDaten().erstVorname);
  }


  // Reihenfolge von Vor- und Nachnamen im Fahrtenbuch umdrehen
  void updateFb() {
    DatenFelder d;
    String s;
    d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    if (d == null) return;
    do {
      if ( !(s = d.get(Daten.fahrtenbuch.STM)).equals("") )
        d.set(Daten.fahrtenbuch.STM,switchVorNach(s));
      for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++)
        if ( !(s = d.get(i)).equals("") )
          d.set(i,switchVorNach(s));
      Daten.fahrtenbuch.delete(d.get(Daten.fahrtenbuch.LFDNR));
      Daten.fahrtenbuch.add(d);
    } while( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null);
  }


  // Änderungen speicher, Fahrtenbuch erstellen
  void SaveButton_actionPerformed(ActionEvent e) {
    fahrtenbuch_focusLost(null);
    if (fahrtenbuch.getText().trim().length()==0) {
      Dialog.infoDialog(International.getMessage("{listname}-Dateiname fehlt",
              International.getString("Fahrtenbuch")),
              International.getMessage("Kein {listname}-Dateiname eingetragen!",
              International.getString("Fahrtenbuch")));
      fahrtenbuch.requestFocus(); return;
    }
    if (boote.getText().trim().length()==0) {
      Dialog.infoDialog(International.getMessage("{listname}-Dateiname fehlt",
              International.getString("Bootsliste")),
              International.getMessage("Kein {listname}-Dateiname eingetragen!",
              International.getString("Bootsliste")));
      boote.requestFocus(); return;
    }
    if (mitglieder.getText().trim().length()==0) {
      Dialog.infoDialog(International.getMessage("{listname}-Dateiname fehlt",
              International.getString("Mitgliederliste")),
              International.getMessage("Kein {listname}-Dateiname eingetragen!",
              International.getString("Mitgliederliste")));
      mitglieder.requestFocus(); return;
    }
    if (ziele.getText().trim().length()==0) {
      Dialog.infoDialog(International.getMessage("{listname}-Dateiname fehlt",
              International.getString("Zielliste")),
              International.getMessage("Kein {listname}-Dateiname eingetragen!",
              International.getString("Zielliste")));
      ziele.requestFocus(); return;
    }
    if (statistik.getText().trim().length()==0) {
      Dialog.infoDialog(International.getMessage("{listname}-Dateiname fehlt",
              International.getString("Statistikeinstellungen")),
              International.getMessage("Kein {listname}-Dateiname eingetragen!",
              International.getString("Statistikeinstellungen")));
      statistik.requestFocus(); return;
    }

    if (neu && EfaUtil.canOpenFile(fahrtenbuch.getText().trim()))
      if (!(Dialog.yesNoDialog(International.getString("Warnung"),
              LogString.logstring_fileAlreadyExists(fahrtenbuch.getText().trim(), International.getString("Fahrtenbuch")) +
              " " + International.getString("Überschreiben?")) == Dialog.YES)) return;

    FBDaten daten;
    if (neu) daten = new FBDaten();
    else daten = Daten.fahrtenbuch.getDaten();
    daten.bootDatei = boote.getText().trim();
    daten.mitgliederDatei = mitglieder.getText().trim();
    daten.zieleDatei = ziele.getText().trim();
    daten.statistikDatei = statistik.getText().trim();
    daten.status = EfaUtil.statusList2Arr(EfaUtil.removeSepFromString(status1.getText().trim()));
    daten.anzMitglieder = EfaUtil.string2int(anzMitglieder.getText().trim(),0);
    if (neu) {
      Daten.fahrtenbuch = new Fahrtenbuch(fahrtenbuch.getText().trim());
      Daten.fahrtenbuch.setNextFb(nextFb.getText().trim());
      Daten.fahrtenbuch.setPrevFb(prevFb.getText().trim());
      daten.erstVorname = vorNach.isSelected();
      Daten.fahrtenbuch.setDaten(daten);
      Daten.fahrtenbuch.writeFile();
      Daten.fahrtenbuch.readZusatzdatenbanken(true); // lies Zusatzdatenbanken, ggf. ohne Nachfrage neu erstellen
      if (efaFrame != null) efaFrame.neuesFahrtenbuch();
    } else {
      if (Daten.fahrtenbuch.getDaten().erstVorname != vorNach.isSelected()) updateFb();
      daten.erstVorname = vorNach.isSelected();
      Daten.fahrtenbuch.setDaten(daten);
      Daten.fahrtenbuch.setNextFb(nextFb.getText().trim());
      Daten.fahrtenbuch.setPrevFb(prevFb.getText().trim());
      Daten.fahrtenbuch.writeFile();
      Daten.fahrtenbuch.readFile();
      if (efaFrame != null) efaFrame.SetFields((DatenFelder)Daten.fahrtenbuch.getCompleteFirst());
      Daten.fahrtenbuch.setChanged();
    }
    if (Daten.fahrtenbuch.getDaten().mitglieder != null) Daten.fahrtenbuch.getDaten().mitglieder.getAliases();
    cancel();
  }


  // Fahrtenbuch-Datei auswählen
  void fahrtenbuchButton_actionPerformed(ActionEvent e) {
    if (!neu) return;

    String dat;
    if (Daten.fahrtenbuch != null && !Daten.fahrtenbuch.getFileName().equals(""))
      dat = Dialog.dateiDialog(this,International.getString("Fahrtenbuch erstellen"),
              International.getString("Fahrtenbuch")+" (*.efb)","efb",Daten.fahrtenbuch.getFileName(),true);
    else dat = Dialog.dateiDialog(this,International.getString("Fahrtenbuch erstellen"),
            International.getString("Fahrtenbuch")+" (*.efb)","efb",Daten.efaDataDirectory,true);

    if (dat != null) {
      fahrtenbuch.setText(dat);
      if (!fahrtenbuch.getText().endsWith(".") && !fahrtenbuch.getText().toUpperCase().endsWith(".EFB"))
        fahrtenbuch.setText(fahrtenbuch.getText()+".efb");
      setBoote(); setMitglieder(); setZiele(); setStatistik();
    }
  }


  // Datei-Öffnen-Dialog mit übergebenen Daten
  void dateiOeffnen(JTextField feld, String titel, String filedescr, String ext) {
    String dat;
    if (!feld.getText().trim().equals(""))
      dat = Dialog.dateiDialog(this,titel,filedescr,ext,EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fahrtenbuch.getText().trim()),feld.getText().trim()),false);
    else if (!fahrtenbuch.getText().trim().equals("")) dat = Dialog.dateiDialog(this,titel,filedescr,ext,fahrtenbuch.getText().trim(),false);
    else dat = Dialog.dateiDialog(this,titel,filedescr,ext,null,false);

    if (dat != null) {
      String s;
      if (! (s = fahrtenbuch.getText().trim()).equals(""))
        feld.setText(EfaUtil.makeRelativePath(dat,s));
      else feld.setText(dat);
    }
  }

  // Boots-Datei auswählen
  void booteButton_actionPerformed(ActionEvent e) {
    dateiOeffnen(boote,
            International.getMessage("{item} auswählen",
            International.getString("Bootsliste")),
            International.getString("Bootsliste")+" (*.efbb)","efbb");
  }
  // Mitglieder-Datei auswählen
  void mitgliederButton_actionPerformed(ActionEvent e) {
    dateiOeffnen(mitglieder,
            International.getMessage("{item} auswählen",
            International.getString("Mitgliederliste")),
            International.getString("Mitgliederliste")+" (*.efbm)","efbm");
  }
  // Ziel-Datei auswählen
  void zieleButton_actionPerformed(ActionEvent e) {
    dateiOeffnen(ziele,
            International.getMessage("{item} auswählen",
            International.getString("Zielliste")),
            International.getString("Zielliste")+" (*.efbz)","efbz");
  }
  // Statistikeinstellungen-Datei auswählen
  void statistikButton_actionPerformed(ActionEvent e) {
    dateiOeffnen(statistik,
            International.getMessage("{item} auswählen",
            International.getString("Statistikeinstellungen")),
            International.getString("Statistikeinstellungen")+" (*.efbs)","efbs");
  }
  // vorherige Fahrtenbuchdatei auswählen
  void prevFbButton_actionPerformed(ActionEvent e) {
    dateiOeffnen(prevFb,
            International.getMessage("{item} auswählen",
            International.getString("vorheriges Fahrtenbuch")),
            International.getString("Fahrtenbuch")+" (*.efb)","efb");
  }
  // nächste Fahrtenbuchdatei auswählen
  void nextFbButton_actionPerformed(ActionEvent e) {
    dateiOeffnen(nextFb,
            International.getMessage("{item} auswählen",
            International.getString("nächstes Fahrtenbuch")),
            International.getString("Fahrtenbuch")+" (*.efb)","efb");
  }


  // Fahrtenbuch-Datei ggf. korrigieren
  void fahrtenbuch_focusLost(FocusEvent e) {
    if (!neu) return;
    if (!fahrtenbuch.getText().trim().equals("") && !fahrtenbuch.getText().endsWith(".") && !fahrtenbuch.getText().toUpperCase().endsWith(".EFB"))
      fahrtenbuch.setText(fahrtenbuch.getText().trim()+".efb");
    if (!fahrtenbuch.getText().trim().equals("") && fahrtenbuch.getText().indexOf(Daten.fileSep)<0) fahrtenbuch.setText(Daten.efaDataDirectory+fahrtenbuch.getText().trim());
   setBoote(); setMitglieder(); setZiele(); setStatistik();
  }


  // Boots-Datei ggf. korrigieren
  void boote_focusLost(FocusEvent e) {
    if (!neu) return;
    if (!boote.getText().trim().equals("") && !boote.getText().endsWith(".") && !boote.getText().toUpperCase().endsWith(".EFBB"))
      boote.setText(boote.getText().trim()+".efbb");
  }


  // Mitglieder-Datei ggf. korrigieren
  void mitglieder_focusLost(FocusEvent e) {
    if (!neu) return;
    if (!mitglieder.getText().trim().equals("") && !mitglieder.getText().endsWith(".") && !mitglieder.getText().toUpperCase().endsWith(".EFBM"))
      mitglieder.setText(mitglieder.getText().trim()+".efbm");
  }


  // Ziel-Datei ggf. korrigieren
  void ziele_focusLost(FocusEvent e) {
    if (!neu) return;
    if (!ziele.getText().trim().equals("") && !ziele.getText().endsWith(".") && !ziele.getText().toUpperCase().endsWith(".EFBZ"))
      ziele.setText(ziele.getText().trim()+".efbz");
  }


  // Statistik-Datei ggf. korrigieren
  void statistik_focusLost(FocusEvent e) {
    if (!neu) return;
    if (!statistik.getText().trim().equals("") && !statistik.getText().endsWith(".") && !statistik.getText().toUpperCase().endsWith(".EFBS"))
      statistik.setText(ziele.getText().trim()+".efbs");
  }


  // wenn Feld für Bootsdatei leer, dann auf "Fahrtenbuchdatei"+".efbb" setzen
  void setBoote() {
    if (boote.getText().trim().equals("")) {
      String d = fahrtenbuch.getText().trim();
      int to;
      if ((to = d.toUpperCase().lastIndexOf(".EFB")) >= 0) {
        d = d.substring(0,to)+".efbb";
        d = EfaUtil.makeRelativePath(d,fahrtenbuch.getText().trim());
        boote.setText(d);
      }
    }
  }
  void boote_focusGained(FocusEvent e) {
    setBoote();
  }


  // wenn Feld für Mitgliederdatei leer, dann auf "Fahrtenbuchdatei"+".efbm" setzen
  void setMitglieder() {
    if (mitglieder.getText().trim().equals("")) {
      String d = fahrtenbuch.getText().trim();
      int to;
      if ((to = d.toUpperCase().lastIndexOf(".EFB")) >= 0) {
        d = d.substring(0,to)+".efbm";
        d = EfaUtil.makeRelativePath(d,fahrtenbuch.getText().trim());
        mitglieder.setText(d);
      }
    }
  }
  void mitglieder_focusGained(FocusEvent e) {
    setMitglieder();
  }


  // wenn Feld für Zieldatei leer, dann auf "Fahrtenbuchdatei"+".efbz" setzen
  void setZiele() {
    if (ziele.getText().trim().equals("")) {
      String d = fahrtenbuch.getText().trim();
      int to;
      if ((to = d.toUpperCase().lastIndexOf(".EFB")) >= 0) {
        d = d.substring(0,to)+".efbz";
        d = EfaUtil.makeRelativePath(d,fahrtenbuch.getText().trim());
        ziele.setText(d);
      }
    }
  }
  void ziele_focusGained(FocusEvent e) {
    setZiele();
  }


  // wenn Feld für Statistikdatei leer, dann auf "Fahrtenbuchdatei"+".efbs" setzen
  void setStatistik() {
    if (statistik.getText().trim().equals("")) {
      String d = fahrtenbuch.getText().trim();
      int to;
      if ((to = d.toUpperCase().lastIndexOf(".EFB")) >= 0) {
        d = d.substring(0,to)+".efbs";
        d = EfaUtil.makeRelativePath(d,fahrtenbuch.getText().trim());
        statistik.setText(d);
      }
    }
  }
  void statistik_focusGained(FocusEvent e) {
    setStatistik();
  }

  void status1_focusLost(FocusEvent e) {
    String[] a = EfaUtil.statusList2Arr(status1.getText().trim());
    Vector v = new Vector();
    String added = "";
    for (int i=0; i<a.length; i++) v.add(a[i]);
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null) {
      for (DatenFelder d = Daten.fahrtenbuch.getDaten().mitglieder.getCompleteFirst(); d != null;
           d = Daten.fahrtenbuch.getDaten().mitglieder.getCompleteNext()) {
        if (d.get(Mitglieder.STATUS).length() > 0 && !v.contains(d.get(Mitglieder.STATUS))) {
          v.add(0,d.get(Mitglieder.STATUS));
          added += (added.length() > 0 ? "; " : "") + d.get(Mitglieder.STATUS);
        }
      }
    }
    a = new String[v.size()];
    for (int i=0; i<v.size(); i++) a[i] = (String)v.get(i);
    status1.setText(EfaUtil.arr2KommaList(a));
    if (added.length() > 0) {
      Dialog.infoDialog(International.getString("Statusliste ergänzt"),
              International.getString("Es dürfen keine Stati entfernt werden, die in der "+
                                      "Mitgliederliste noch verwendet werden. Diese Stati "+
                                      "wurden wieder hinzugefügt:")+"\n"+added);
    }
  }

  void anzMitglieder_focusLost(FocusEvent e) {
    TMJ tmj = EfaUtil.string2date(anzMitglieder.getText().trim(),0,0,0);
    anzMitglieder.setText(Integer.toString(tmj.tag));
  }




}
