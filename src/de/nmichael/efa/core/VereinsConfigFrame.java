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
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

// @i18n complete (needs no internationalization -- only relevant for Germany)

public class VereinsConfigFrame extends JDialog implements ActionListener {
  VereinsConfig vc=null;
  EfaWett ew=null;
  boolean efwErstellen;
  boolean zielbereichGeaendert;
  JLabel jLabel1 = new JLabel();
  JLabel vereinsNameLabel = new JLabel();
  JLabel userDrvLabel = new JLabel();
  JLabel mitgliedsnummerDRVLabel = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel meldenderNameLabel = new JLabel();
  JLabel meldenderEmailLabel = new JLabel();
  JLabel meldenderKtoLabel = new JLabel();
  JTextField vereinsName = new JTextField();
  JTextField userDRV = new JTextField();
  JTextField mitgliedsnummerDRV = new JTextField();
  JLabel meldenderBankLabel = new JLabel();
  JLabel meldenderBLZLabel = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel versandNameLabel = new JLabel();
  JLabel versandStrasseLabel = new JLabel();
  JLabel versandOrtLabel = new JLabel();
  JTextField meldenderName = new JTextField();
  JTextField meldenderEmail = new JTextField();
  JTextField meldenderKto = new JTextField();
  JTextField meldenderBank = new JTextField();
  JTextField meldenderBLZ = new JTextField();
  JTextField versandName = new JTextField();
  JTextField versandStrasse = new JTextField();
  JTextField versandOrt = new JTextField();
  JButton okButton = new JButton();
  JPanel mainPanel = new JPanel();
  JPanel jPanel1 = new JPanel();
  JPanel vereinsPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  JTextArea info = new JTextArea();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  JLabel mitgliederLabel = new JLabel();
  JTextField mitglieder = new JTextField();
  JLabel jLabel2 = new JLabel();
  JLabel vereinsOrtLabel = new JLabel();
  JLabel lrvnameLabel = new JLabel();
  JCheckBox mitglSRV = new JCheckBox();
  JCheckBox mitglDRV = new JCheckBox();
  JCheckBox mitglADH = new JCheckBox();
  JLabel mitglImLabel = new JLabel();
  JTextField vereinsOrt = new JTextField();
  JTextField lrvname = new JTextField();
  JLabel zielbereichLabel = new JLabel();
  JTextField zielbereich = new JTextField();
  JLabel userLrvLabel = new JLabel();
  JTextField userLRV = new JTextField();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel efaConfigPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextArea efwErklaerung = new JTextArea();
  JLabel jLabel3 = new JLabel();
  JLabel efwLetzteAktualisierung = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel efwStandDerDaten = new JLabel();
  JButton efwAktualisierenButton = new JButton();
  JPanel efaWettPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JTextArea efaWettInfo = new JTextArea();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JLabel jLabel10 = new JLabel();
  JButton efaWettHomepageButton = new JButton();

  public VereinsConfigFrame(Frame parent, VereinsConfig vc) {
    super(parent);
    this.vc = vc;
    efwErstellen = false;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      iniFelder(vc);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    mitglieder.setVisible(false);
    mitgliederLabel.setVisible(false);
    vereinsName.requestFocus();
    zielbereichGeaendert = false;
  }

  public VereinsConfigFrame(StatistikFrame parent, EfaWett ew) {
    super(parent);
    this.ew = ew;
    this.vc = Daten.vereinsConfig;
    efwErstellen = true;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      iniFelder(ew);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    okButton.setText("OK");
    okButton.setMnemonic('O');
    this.setTitle("Elektronische Wettbewerbsmeldungen");
    zielbereichGeaendert = false;
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

    jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel1.setForeground(new Color(0, 0, 99));
    jLabel1.setText("Vereinsdaten");
    vereinsNameLabel.setText("Vereinsname: ");
    userDrvLabel.setText("Benutzername für efaWett (DRV): ");
    mitgliedsnummerDRVLabel.setText("Mitgliedsnummer im DRV: ");
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel5.setForeground(new Color(0, 0, 99));
    jLabel5.setText("Daten der meldenden Person (bei Wettbewerben)");
    meldenderNameLabel.setText("Name: ");
    meldenderEmailLabel.setText("email-Adresse: ");
    meldenderKtoLabel.setText("Kontonr: ");
    userDRV.setNextFocusableComponent(userLRV);
    userDRV.setPreferredSize(new Dimension(300, 19));
    meldenderBankLabel.setText("Bank: ");
    meldenderBLZLabel.setText("BLZ: ");
    jLabel11.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel11.setForeground(new Color(0, 0, 99));
    jLabel11.setText("Versandanschrift für Meldeergebnisse etc.");
    versandNameLabel.setText("Name: ");
    versandStrasseLabel.setText("Straße, Hausnr: ");
    versandOrtLabel.setText("PLZ, Ort: ");
    vereinsName.setNextFocusableComponent(vereinsOrt);
    vereinsName.setPreferredSize(new Dimension(300, 19));
    vereinsName.setToolTipText("");
    mitgliedsnummerDRV.setNextFocusableComponent(mitglieder);
    mitgliedsnummerDRV.setPreferredSize(new Dimension(300, 19));
    meldenderName.setNextFocusableComponent(meldenderEmail);
    meldenderName.setPreferredSize(new Dimension(300, 19));
    meldenderEmail.setNextFocusableComponent(meldenderKto);
    meldenderEmail.setPreferredSize(new Dimension(300, 19));
    meldenderKto.setNextFocusableComponent(meldenderBank);
    meldenderKto.setPreferredSize(new Dimension(300, 19));
    meldenderBank.setNextFocusableComponent(meldenderBLZ);
    meldenderBank.setPreferredSize(new Dimension(300, 19));
    meldenderBLZ.setNextFocusableComponent(versandName);
    meldenderBLZ.setPreferredSize(new Dimension(300, 19));
    versandName.setNextFocusableComponent(versandStrasse);
    versandName.setPreferredSize(new Dimension(300, 19));
    versandStrasse.setNextFocusableComponent(versandOrt);
    versandStrasse.setPreferredSize(new Dimension(300, 19));
    versandOrt.setNextFocusableComponent(userDRV);
    versandOrt.setPreferredSize(new Dimension(300, 19));
    okButton.setNextFocusableComponent(vereinsName);
    okButton.setMnemonic('S');
    okButton.setText("Speichern");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    this.setTitle("Elektronische Wettbewerbsmeldungen");
    this.getContentPane().setLayout(borderLayout1);
    vereinsPanel.setLayout(gridBagLayout2);
    vereinsPanel.setMinimumSize(new Dimension(580, 360));
    vereinsPanel.setPreferredSize(new Dimension(580, 400));
    info.setWrapStyleWord(true);
    info.setLineWrap(true);
    info.setBackground(new Color(204, 204, 204));
    info.setEditable(false);
    info.setVerifyInputWhenFocusTarget(false);
    jPanel1.setLayout(borderLayout2);
    mainPanel.setLayout(borderLayout3);
    mitgliederLabel.setText("Mitgliederzahl am 01.01.:");
    mitglieder.setNextFocusableComponent(zielbereich);
    mitglieder.setPreferredSize(new Dimension(300, 19));
    mitglieder.setEditable(false);
    mainPanel.setPreferredSize(new Dimension(580, 600));
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel2.setForeground(new Color(0, 0, 99));
    jLabel2.setText("efaWett (elektronische Wettbewerbsteilnahme)");
    vereinsOrtLabel.setText("Vereinsort: ");
    lrvnameLabel.setText("Landesruderverband: ");
    mitglSRV.setNextFocusableComponent(mitglADH);
    mitglSRV.setText("SRV");
    mitglDRV.setNextFocusableComponent(mitglSRV);
    mitglDRV.setText("DRV");
    mitglADH.setNextFocusableComponent(mitgliedsnummerDRV);
    mitglADH.setText("ADH");
    mitglImLabel.setText("Mitglied im: ");
    vereinsOrt.setNextFocusableComponent(lrvname);
    lrvname.setNextFocusableComponent(mitglDRV);
    zielbereichLabel.setToolTipText("der Zielbereich, in dem das eigene Bootshaus liegt");
    zielbereichLabel.setText("eigener Zielbereich (LRV Berlin): ");
    zielbereich.setNextFocusableComponent(meldenderName);
    zielbereich.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        zielbereich_KeyTyped(e);
      }
    });
    zielbereich.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        zielbereich_focusLost(e);
      }
    });
    userLrvLabel.setText("Benutzername für efaWett (LRV): ");
    userLRV.setNextFocusableComponent(okButton);
    efaConfigPanel.setLayout(gridBagLayout1);
    efwErklaerung.setEnabled(false);
    efwErklaerung.setPreferredSize(new Dimension(500, 150));
    efwErklaerung.setDisabledTextColor(Color.black);
    efwErklaerung.setWrapStyleWord(true);
    efwErklaerung.setLineWrap(true);
    efwErklaerung.setBackground(new Color(204, 204, 204));
    efwErklaerung.setEditable(false);
    efaWettInfo.setBackground(new Color(204, 204, 204));
    efaWettInfo.setEditable(false);
    efaWettInfo.setVerifyInputWhenFocusTarget(false);
    efaConfigPanel.setPreferredSize(new Dimension(600, 100));
    jLabel3.setText("letzte Aktualisierung: ");
    efwLetzteAktualisierung.setText("01.01.2004");
    jLabel6.setText("Stand der Daten: ");
    efwStandDerDaten.setText("01.01.2004");
    efwAktualisierenButton.setText("Daten jetzt aktualisieren");
    efwAktualisierenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efwAktualisierenButton_actionPerformed(e);
      }
    });
    efaWettPanel.setLayout(gridBagLayout3);
    jLabel4.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel4.setForeground(SystemColor.activeCaption);
    jLabel4.setText("efaWett - elektronisches Meldesystem für Wettbewerbe");
    jLabel7.setText("    - Für die elektronische Meldung anmelden");
    jLabel8.setText("    - Wettbewerbsmeldungen einsenden");
    jLabel9.setText("    - Status der letzten Wettbewerbsmeldung abfragen");
    jLabel10.setText("    - In efaWett gespeicherte Vereinsdaten einsehen/ändern");
    efaWettHomepageButton.setText("efaWett-Homepage öffnen");
    efaWettHomepageButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaWettHomepageButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(jPanel1, BorderLayout.NORTH);
    vereinsPanel.add(jLabel1,             new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    vereinsPanel.add(vereinsNameLabel,             new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(mitgliedsnummerDRVLabel,             new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(jLabel5,              new GridBagConstraints(0, 8, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderNameLabel,             new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderEmailLabel,             new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderKtoLabel,             new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(vereinsName,              new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(mitgliedsnummerDRV,                new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderBankLabel,             new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderBLZLabel,             new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(jLabel11,             new GridBagConstraints(0, 14, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    vereinsPanel.add(versandNameLabel,             new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(versandStrasseLabel,             new GridBagConstraints(0, 16, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(versandOrtLabel,             new GridBagConstraints(0, 17, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderName,              new GridBagConstraints(1, 9, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderEmail,              new GridBagConstraints(1, 10, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderKto,              new GridBagConstraints(1, 11, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderBank,              new GridBagConstraints(1, 12, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(meldenderBLZ,              new GridBagConstraints(1, 13, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(versandName,              new GridBagConstraints(1, 15, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(versandStrasse,              new GridBagConstraints(1, 16, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(versandOrt,              new GridBagConstraints(1, 17, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(mitgliederLabel,             new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(mitglieder,              new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(jLabel2,              new GridBagConstraints(0, 18, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    vereinsPanel.add(userDrvLabel,           new GridBagConstraints(0, 19, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(userDRV,           new GridBagConstraints(1, 19, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(vereinsOrtLabel,        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(lrvnameLabel,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(mitglSRV,      new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    vereinsPanel.add(mitglDRV,     new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(mitglADH,    new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    vereinsPanel.add(mitglImLabel,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(vereinsOrt,    new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(lrvname,    new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(zielbereichLabel,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(info, BorderLayout.CENTER);
    vereinsPanel.add(zielbereich,   new GridBagConstraints(1, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(userLrvLabel,   new GridBagConstraints(0, 20, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    vereinsPanel.add(userLRV,   new GridBagConstraints(1, 20, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(jTabbedPane1,  BorderLayout.CENTER);
    mainPanel.add(okButton, BorderLayout.SOUTH);
    jTabbedPane1.add(vereinsPanel, "Vereinsdaten");
    jTabbedPane1.add(efaConfigPanel,   "efa-Konfiguration");
    efaConfigPanel.add(efwErklaerung,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    efaConfigPanel.add(jLabel3,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaConfigPanel.add(efwLetzteAktualisierung,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaConfigPanel.add(jLabel6,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaConfigPanel.add(efwStandDerDaten,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaConfigPanel.add(efwAktualisierenButton,   new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    jTabbedPane1.add(efaWettPanel,  "efaWett");
    efaWettPanel.add(efaWettInfo,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaWettPanel.add(jLabel4,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    efaWettPanel.add(jLabel7,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaWettPanel.add(jLabel8,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaWettPanel.add(jLabel9,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaWettPanel.add(jLabel10,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaWettPanel.add(efaWettHomepageButton,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
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


   void iniFelder(VereinsConfig vc) {
     if (Daten.efaConfig != null && !Daten.efaConfig.showBerlinOptions) {
       this.zielbereich.setVisible(false);
       this.zielbereichLabel.setVisible(false);
     }

     if (vc != null) {
       vereinsName.setText(vc.vereinsname);
       vereinsOrt.setText(vc.vereinsort);
       lrvname.setText(vc.lrvname);
       mitglDRV.setSelected(vc.mitglDRV);
       mitglSRV.setSelected(vc.mitglSRV);
       mitglADH.setSelected(vc.mitglADH);
       mitgliedsnummerDRV.setText(vc.mitgliedsnummerDRV);
       zielbereich.setText(vc.zielbereich);
       meldenderName.setText(vc.meldenderName);
       meldenderEmail.setText(vc.meldenderEmail);
       meldenderKto.setText(vc.meldenderKto);
       meldenderBank.setText(vc.meldenderBank);
       meldenderBLZ.setText(vc.meldenderBLZ);
       versandName.setText(vc.versandName);
       versandStrasse.setText(vc.versandStrasse);
       versandOrt.setText(vc.versandOrt);
       userDRV.setText(vc.userDRV);
       userLRV.setText(vc.userLRV);
     }
     if (Daten.wettDefs != null) {
       efwLetzteAktualisierung.setText(Daten.wettDefs.efw_letzte_aktualisierung);
       efwStandDerDaten.setText(Daten.wettDefs.efw_stand_der_daten);
     }
    info.append("Die folgenden Daten werden zum Erstellen von elektronischen Wettbewerbsmeldungen benötigt.\n"+
                "Vereinsdaten: Daten des Vereins, die für Wettbewerbsmeldungen benötigt werden\n"+
                "efa-Konfiguraion: Konfiguration von efa für elektronische Wettbewerbsmeldungen aktualisieren\n"+
                "efa-Wett: Für elektronische Wettbewerbsmeldung anmelden, Status von Meldungen abrufen etc.");
    efwErklaerung.append("efa speichert diverse Konfigurationswerte, die für die elektronische Meldung\n"+
                         "erforderlich sind. Hierzu zählen insbesondere Adressen zum elektronischen\n"+
                         "Einsenden der Daten sowie Informationen über die anfallenden Meldegebühren.\n"+
                         "Damit diese Daten stets korrekt sind, sollten sie jeweils vor der Erstellung einer\n"+
                         "Meldedatei aktualisiert werden.\n");
    efaWettInfo.append("efaWett ist ein elektronisches Meldesystem im Internet zum Einsenden von\n"+
                       "Wettbewerbsmeldungen. Um Wettbewerbe elektronisch melden zu können, muß sich\n"+
                       "jeder Verein zunächst bei efaWett kostenlos anmelden. Anschließend kann der\n"+
                       "Verein für die angebotenen Wettbewerbe mit Hilfe von efa elektronisch melden.\n"+
                       "Weitere Informationen findest Du auf der efaWett-Homepage.");
   }

   void iniFelder(EfaWett ew) {
     if (ew != null) {
       if (ew.verein_name != null) vereinsName.setText(ew.verein_name);
       else {
         vereinsName.setVisible(false); vereinsNameLabel.setVisible(false);
       }
       if (ew.verein_user != null) {
         switch(ew.wettId) {
           case WettDefs.DRV_FAHRTENABZEICHEN:
           case WettDefs.DRV_WANDERRUDERSTATISTIK:
             userDRV.setText(ew.verein_user);
             userLRV.setVisible(false); userLrvLabel.setVisible(false);
             break;
           case WettDefs.LRVBERLIN_SOMMER:
           case WettDefs.LRVBERLIN_WINTER:
           case WettDefs.LRVBERLIN_BLAUERWIMPEL:
             userLRV.setText(ew.verein_user);
             userDRV.setVisible(false); userDrvLabel.setVisible(false);
             break;
         }
       } else {
         userDRV.setVisible(false); userDrvLabel.setVisible(false);
         userLRV.setVisible(false); userLrvLabel.setVisible(false);
       }
       if (ew.verein_mitglnr != null) mitgliedsnummerDRV.setText(ew.verein_mitglnr);
       else {
         mitgliedsnummerDRV.setVisible(false); mitgliedsnummerDRVLabel.setVisible(false);
       }
       if (ew.verein_ort != null) vereinsOrt.setText(ew.verein_ort);
       else {
         vereinsOrt.setVisible(false); vereinsOrtLabel.setVisible(false);
       }
       if (ew.verein_lrv != null) lrvname.setText(ew.verein_lrv);
       else {
         lrvname.setVisible(false); lrvnameLabel.setVisible(false);
       }
       if (ew.verein_mitgl_in != null) {
         mitglDRV.setSelected(ew.verein_mitgl_in.indexOf("DRV")>=0);
         mitglADH.setSelected(ew.verein_mitgl_in.indexOf("ADH")>=0);
         mitglSRV.setSelected(ew.verein_mitgl_in.indexOf("SRV")>=0);
       } else {
         mitglImLabel.setVisible(false); mitglDRV.setVisible(false);
         mitglSRV.setVisible(false); mitglADH.setVisible(false);
       }
       if (ew.verein_mitglieder != null) {
         mitglieder.setText(ew.verein_mitglieder);
         mitgliederLabel.setText("Mitgliederanzahl am 01.01."+ew.allg_wettjahr.substring(0,4)+": ");
      } else {
         mitglieder.setVisible(false); mitgliederLabel.setVisible(false);
       }
       if (ew.meld_name != null) meldenderName.setText(ew.meld_name);
       else {
         meldenderName.setVisible(false); meldenderNameLabel.setVisible(false);
       }
       if (ew.meld_email != null) meldenderEmail.setText(ew.meld_email);
       else {
         meldenderEmail.setVisible(false); meldenderEmailLabel.setVisible(false);
       }
       if (ew.meld_kto != null) { meldenderKto.setText(ew.meld_kto); meldenderNameLabel.setText("Name des Kontoinhabers: "); }
       else {
         meldenderKto.setVisible(false); meldenderKtoLabel.setVisible(false);
       }
       if (ew.meld_bank != null) meldenderBank.setText(ew.meld_bank);
       else {
         meldenderBank.setVisible(false); meldenderBankLabel.setVisible(false);
       }
       if (ew.meld_blz != null) meldenderBLZ.setText(ew.meld_blz);
       else {
         meldenderBLZ.setVisible(false); meldenderBLZLabel.setVisible(false);
       }
       if (ew.versand_name != null) versandName.setText(ew.versand_name);
       else {
         versandName.setVisible(false); versandNameLabel.setVisible(false);
       }
       if (ew.versand_strasse != null) versandStrasse.setText(ew.versand_strasse);
       else {
         versandStrasse.setVisible(false); versandStrasseLabel.setVisible(false);
       }
       if (ew.versand_ort != null) versandOrt.setText(ew.versand_ort);
       else {
         versandOrt.setVisible(false); versandOrtLabel.setVisible(false);
       }

       zielbereichLabel.setVisible(false); zielbereich.setVisible(false);

       if (Daten.wettDefs != null) {
         efwLetzteAktualisierung.setText(Daten.wettDefs.efw_letzte_aktualisierung);
         efwStandDerDaten.setText(Daten.wettDefs.efw_stand_der_daten);
       }
     }
    info.append("Die folgenden Daten sind für die Meldung erforderlich.\n"+
                "Bitte überprüfe (und vervollständige ggf.) die Angaben und klicke anschließend OK.");
    efwErklaerung.append("efa speichert diverse Konfigurationswerte, die für die elektronische Meldung\n"+
                         "erforderlich sind. Hierzu zählen insbesondere Adressen zum elektronischen\n"+
                         "Einsenden der Daten sowie Informationen über die anfallenden Meldegebühren.\n"+
                         "Damit diese Daten stets korrekt sind, sollten sie jeweils vor der Erstellung einer\n"+
                         "Meldedatei aktualisiert werden.\n");
    efaWettInfo.append("efaWett ist ein elektronisches Meldesystem im Internet zum Einsenden von\n"+
                       "Wettbewerbsmeldungen. Um Wettbewerbe elektronisch melden zu können, muß sich\n"+
                       "jeder Verein zunächst bei efaWett kostenlos anmelden. Anschließend kann der\n"+
                       "Verein für die angebotenen Wettbewerbe mit Hilfe von efa elektronisch melden.\n"+
                       "Weitere Informationen findest Du auf der efaWett-Homepage.");
   }

  boolean checkNotEmpty(JTextField t) {
    if (t.isVisible() && t.getText().trim().length()==0) {
      Dialog.error("Bitte fülle alle angezeigten Felder vollständig aus!");
      t.requestFocus();
      return false;
    }
    return true;
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (efwErstellen) {
      if (!checkNotEmpty(vereinsName)) return;
      if (!checkNotEmpty(vereinsOrt)) return;
      if (!checkNotEmpty(lrvname)) return;
      if (!checkNotEmpty(mitgliedsnummerDRV)) return;
      if (!checkNotEmpty(zielbereich)) return;
      if (!checkNotEmpty(meldenderName)) return;
      if (!checkNotEmpty(meldenderEmail)) return;
      if (!checkNotEmpty(meldenderKto)) return;
      if (!checkNotEmpty(meldenderBank)) return;
      if (!checkNotEmpty(meldenderBLZ)) return;
      if (!checkNotEmpty(versandName)) return;
      if (!checkNotEmpty(versandStrasse)) return;
      if (!checkNotEmpty(versandOrt)) return;
      if (!checkNotEmpty(userDRV)) return;
      if (!checkNotEmpty(userLRV)) return;
    }
    if (vc != null) {
      if (vereinsName.isVisible()) vc.vereinsname=vereinsName.getText().trim();
      if (vereinsOrt.isVisible()) vc.vereinsort=vereinsOrt.getText().trim();
      if (lrvname.isVisible()) vc.lrvname=lrvname.getText().trim();
      if (mitglDRV.isVisible()) vc.mitglDRV=mitglDRV.isSelected();
      if (mitglSRV.isVisible()) vc.mitglSRV=mitglSRV.isSelected();
      if (mitglADH.isVisible()) vc.mitglADH=mitglADH.isSelected();
      if (mitgliedsnummerDRV.isVisible()) vc.mitgliedsnummerDRV=mitgliedsnummerDRV.getText().trim();
      if (zielbereich.isVisible()) vc.zielbereich=zielbereich.getText().trim();
      if (meldenderName.isVisible()) vc.meldenderName=meldenderName.getText().trim();
      if (meldenderEmail.isVisible()) vc.meldenderEmail=meldenderEmail.getText().trim();
      if (meldenderKto.isVisible()) vc.meldenderKto=meldenderKto.getText().trim();
      if (meldenderBank.isVisible()) vc.meldenderBank=meldenderBank.getText().trim();
      if (meldenderBLZ.isVisible()) vc.meldenderBLZ=meldenderBLZ.getText().trim();
      if (versandName.isVisible()) vc.versandName=versandName.getText().trim();
      if (versandStrasse.isVisible()) vc.versandStrasse=versandStrasse.getText().trim();
      if (versandOrt.isVisible()) vc.versandOrt=versandOrt.getText().trim();
      if (userDRV.isVisible()) vc.userDRV=userDRV.getText().trim();
      if (userLRV.isVisible()) vc.userLRV=userLRV.getText().trim();
      vc.writeFile();
    }
    if (!efwErstellen) {
      if (zielbereichGeaendert && Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().ziele != null) Daten.fahrtenbuch.getDaten().ziele.checkAllZielbereiche(vc.zielbereich);
      cancel();
    } else { // Daten für efa-Wett erstellen
      if (vereinsName.isVisible()) ew.verein_name = vereinsName.getText().trim();
      if (userDRV.isVisible()) ew.verein_user = userDRV.getText().trim();
      if (userLRV.isVisible()) ew.verein_user = userLRV.getText().trim();
      if (mitgliedsnummerDRV.isVisible()) ew.verein_mitglnr = mitgliedsnummerDRV.getText().trim();
      if (vereinsOrt.isVisible()) ew.verein_ort = vereinsOrt.getText().trim();
      if (lrvname.isVisible()) ew.verein_lrv = lrvname.getText().trim();
      if (mitglDRV.isVisible() && mitglADH.isVisible() && mitglSRV.isVisible())
        ew.verein_mitgl_in = (mitglDRV.isSelected() ? "DRV" : "") + ";" +
                             (mitglSRV.isSelected() ? "SRV" : "") + ";" +
                             (mitglADH.isSelected() ? "ADH" : "");
      if (mitglieder.isVisible()) ew.verein_mitglieder = Integer.toString(EfaUtil.string2date(mitglieder.getText().trim(),EfaUtil.string2int(ew.verein_mitglieder,0),0,0).tag);
      if (meldenderName.isVisible()) ew.meld_name = meldenderName.getText().trim();
      if (meldenderEmail.isVisible()) ew.meld_email = meldenderEmail.getText().trim();
      if (meldenderKto.isVisible()) ew.meld_kto = meldenderKto.getText().trim();
      if (meldenderBank.isVisible()) ew.meld_bank = meldenderBank.getText().trim();
      if (meldenderBLZ.isVisible()) ew.meld_blz = meldenderBLZ.getText().trim();
      if (versandName.isVisible()) ew.versand_name = versandName.getText().trim();
      if (versandStrasse.isVisible()) ew.versand_strasse = versandStrasse.getText().trim();
      if (versandOrt.isVisible()) ew.versand_ort = versandOrt.getText().trim();
      cancel();
      Dialog.statistikFrame.efaWettVervollständigen2(ew);
    }
  }


  void zielbereich_KeyTyped(KeyEvent e) {
    zielbereichGeaendert = true;
  }

  void zielbereich_focusLost(FocusEvent e) {
    int i = EfaUtil.string2date(zielbereich.getText(),0,0,0).tag;
    if (i<1 || i>Zielfahrt.ANZ_ZIELBEREICHE) zielbereich.setText("");
    else zielbereich.setText(Integer.toString(i));
  }

  void efwAktualisierenButton_actionPerformed(ActionEvent e) {
    if (holeAktuelleDatenAusDemInternet()) {
      if (Daten.wettDefs != null) {
        efwLetzteAktualisierung.setText(Daten.wettDefs.efw_letzte_aktualisierung);
        efwStandDerDaten.setText(Daten.wettDefs.efw_stand_der_daten);
        Dialog.infoDialog("Die Daten wurden erfolgreich aktualisiert.");
      }
    } else {
      Dialog.error("Die Daten konnten nicht aus dem Internet geladen werden.");
    }
  }

  public static boolean holeAktuelleDatenAusDemInternet() {
    JDialog dialog = null;
    JFrame frame = null;
    try {
      dialog = (JDialog)Dialog.frameCurrent();
    } catch(Exception e) {
      try {
        frame = (JFrame)Dialog.frameCurrent();
      } catch(Exception ee) {
        return false;
      }
    }

    if (Daten.vereinsConfig == null) return false;
    String tmpfile;
    switch(Dialog.auswahlDialog("Aktuelle Konfigurationsdaten abrufen",
                                "efa kann die Konfigurationsdaten selbst aus dem Internet abrufen, oder aber\n"+
                                "eine zuvor heruntergeladene Konfigurationsdatei verarbeiten.\n"+
                                "Was möchtest Du tun?",
                                "Konfigurationsdaten im Internet abrufen","vorhandene Konfigurationsdatei verarbeiten")) {
      case 0:
        if (!Dialog.okAbbrDialog("Internet-Verbindung herstellen",
                                 "Bitte stelle eine Verbindung mit dem Internet her und klicke OK.")) return false;
        tmpfile = Daten.efaTmpDirectory+"efw.data";
        if (dialog != null) {
          if (!EfaUtil.getFile(dialog,Daten.EFW_UPDATE_DATA,tmpfile,true)) {
            return holeAktuelleDatenManuell(true);
          }
        } else {
          if (!EfaUtil.getFile(frame,Daten.EFW_UPDATE_DATA,tmpfile,true)) {
            return holeAktuelleDatenManuell(true);
          }
        }
        break;
      case 1:
        return holeAktuelleDatenManuell(false);
      default:
        return false;
    }
    if (!EfaUtil.canOpenFile(tmpfile)) return false;
    boolean success = speichereAktuelleKonfigurationsdaten(tmpfile);
    EfaUtil.deleteFile(tmpfile);
    return success;
  }

  private static boolean holeAktuelleDatenManuell(boolean autoDownloadHasFailed) {
    JDialog dialog = null;
    JFrame frame = null;
    try {
      dialog = (JDialog)Dialog.frameCurrent();
    } catch(Exception e) {
      try {
        frame = (JFrame)Dialog.frameCurrent();
      } catch(Exception ee) {
        return false;
      }
    }

    if (autoDownloadHasFailed) {
      if (Dialog.yesNoCancelDialog("Wettbewerbskonfiguration manuell aktualisieren",
                                   "Das automatische Herunterladen der Wettbewerbskonfiguration aus dem Internet ist\n"+
                                   "fehlgeschlagen. Möglicherweise wurde der Zugriff von efa durch eine Firewall\n"+
                                   "blockiert. Du kannst nun mit einem Webbrowser die Konfigurationsdaten manuell\n"+
                                   "herunterladen. Möchtest Du die Konfigurationsdaten jetzt manuell herunterladen?") != Dialog.YES) return false;
    }
    Dialog.meldung("Konfigurationsdatei herunterladen",
                   "Bitte gib in einem Webbrowser die Adresse\n"+
                   Daten.EFW_UPDATE_DATA+"\n"+
                   "ein, lade diese Datei herunter und speichere sie auf Deiner Festplatte.\n"+
                   "Klicke anschließend bitte OK und wähle im folgenden Dialog die soeben\n"+
                   "heruntergeladene Datei aus.");
    String datei = Dialog.dateiDialog(dialog,"Konfigurationsdatei auswählen","Wettbewerbskonfiguration (*.data)","data",null,false);
    if (datei == null) return false;
    if (!EfaUtil.canOpenFile(datei)) {
      Dialog.error("Die Datei "+datei+" kann nicht geöffnet werden.");
      return false;
    }
    return speichereAktuelleKonfigurationsdaten(datei);

  }

  private static boolean speichereAktuelleKonfigurationsdaten(String datei) {
    if (Daten.wettDefs == null) return false;

    WettDefs data = new WettDefs(datei);
    int sve = Daten.actionOnChecksumLoadError;
    Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_NO_ACTION;
    boolean success = false;
    try {
      success = data.readFile();
    } catch(Exception e) {
    } finally {
      Daten.actionOnChecksumLoadError = sve;
    }
    if (!success) return false;

    data.setFileName(Daten.wettDefs.getFileName());
    Daten.wettDefs = data;
    Daten.wettDefs.efw_letzte_aktualisierung = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
    sve = Daten.actionOnChecksumSaveError;
    Daten.actionOnChecksumSaveError = Daten.CHECKSUM_SAVE_NO_ACTION;
    success = false;
    try {
      success = Daten.wettDefs.writeFile();
    } catch(Exception e) {
    } finally {
      Daten.actionOnChecksumSaveError = sve;
    }
    return success;
  }

  void efaWettHomepageButton_actionPerformed(ActionEvent e) {
    Dialog.neuBrowserDlg(this,"efaWett-Homepage",Daten.EFAWETTURL);
  }

}
