package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class SuchFrame extends JDialog implements ActionListener {
  EfaFrame parent;
  BorderLayout borderLayout1 = new BorderLayout();
  JButton suchButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JTextField such = new JTextField();
  JLabel jLabel2 = new JLabel();
  JCheckBox lfdnr = new JCheckBox();
  JCheckBox datum = new JCheckBox();
  JCheckBox stm = new JCheckBox();
  JCheckBox mannsch = new JCheckBox();
  JCheckBox boot = new JCheckBox();
  JCheckBox ziel = new JCheckBox();
  JCheckBox abfahrt = new JCheckBox();
  JCheckBox ankunft = new JCheckBox();
  JCheckBox bootskm = new JCheckBox();
  JCheckBox mannschkm = new JCheckBox();
  JCheckBox bemerk = new JCheckBox();
  JButton alleButton = new JButton();
  JButton keineButton = new JButton();
  JLabel jLabel3 = new JLabel();
  JCheckBox fahrtart = new JCheckBox();
  JTabbedPane auswahlPane = new JTabbedPane();
  JPanel normalPanel = new JPanel();
  JPanel fehlersuchPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel4 = new JLabel();
  JCheckBox err_unvollst = new JCheckBox();
  JCheckBox err_km = new JCheckBox();
  JCheckBox err_unbekRuderer = new JCheckBox();
  JCheckBox err_unbekBoot = new JCheckBox();
  JCheckBox err_unbekZiel = new JCheckBox();
  JLabel jLabel5 = new JLabel();
  JCheckBox err_nichtZurueckgetragen = new JCheckBox();
  JCheckBox err_zuUebertragendeMehrtagesfahrten = new JCheckBox();
  JCheckBox err_zielfahrten = new JCheckBox();
  JCheckBox err_unbekRudererOhneGast = new JCheckBox();
  JCheckBox err_wafa = new JCheckBox();
  JCheckBox err_vieleKm = new JCheckBox();
  JTextField err_vieleKmKm = new JTextField();
  JLabel jLabel6 = new JLabel();
  JButton alle2Button = new JButton();
  JButton keine2Button = new JButton();


  public SuchFrame(EfaFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      if (Daten.efaConfig != null && !Daten.efaConfig.showBerlinOptions) err_zielfahrten.setVisible(false);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.parent = parent;

    if (Daten.suchMode == Daten.SUCH_NORMAL) {
      auswahlPane.setSelectedIndex(0);
      such.setText(Daten.such);
      lfdnr.setSelected(Daten.such_lfdnr);
      datum.setSelected(Daten.such_datum);
      stm.setSelected(Daten.such_stm);
      mannsch.setSelected(Daten.such_mannsch);
      boot.setSelected(Daten.such_boot);
      ziel.setSelected(Daten.such_ziel);
      abfahrt.setSelected(Daten.such_abfahrt);
      ankunft.setSelected(Daten.such_ankunft);
      bootskm.setSelected(Daten.such_bootskm);
      mannschkm.setSelected(Daten.such_mannschkm);
      bemerk.setSelected(Daten.such_bemerk);
      fahrtart.setSelected(Daten.such_fahrtart);
      such.requestFocus();
    } else {
      auswahlPane.setSelectedIndex(1);
      err_unvollst.setSelected(Daten.such_errUnvollst);
      err_km.setSelected(Daten.such_errKm);
      err_unbekRuderer.setSelected(Daten.such_errUnbekRuderer);
      err_unbekRudererOhneGast.setSelected(Daten.such_errUnbekRudererOhneGast);
      err_unbekBoot.setSelected(Daten.such_errUnbekBoot);
      err_unbekZiel.setSelected(Daten.such_errUnbekZiel);
      err_wafa.setSelected(Daten.such_errWafa);
      err_zielfahrten.setSelected(Daten.such_errZielfahrten);
      err_vieleKm.setSelected(Daten.such_errVieleKm);
      err_vieleKmKm.setText(EfaUtil.zehntelInt2String(Daten.such_errVieleKmKm));
      err_nichtZurueckgetragen.setSelected(Daten.such_errNichtZurueckgetragen);
      err_zuUebertragendeMehrtagesfahrten.setSelected(Daten.such_errNichtKonfMTours);
      err_unvollst.requestFocus();
    }

    if (!parent.isAdminMode()) {
      err_zuUebertragendeMehrtagesfahrten.setEnabled(false);
      err_zuUebertragendeMehrtagesfahrten.setSelected(false);
    }


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

    suchButton.setNextFocusableComponent(such);
    suchButton.setMnemonic('S');
    suchButton.setText("Suchen");
    suchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        suchButton_actionPerformed(e);
      }
    });
    this.setTitle("Eintrag suchen");
    this.getContentPane().setLayout(borderLayout1);
    jLabel1.setDisplayedMnemonic('U');
    jLabel1.setLabelFor(such);
    jLabel1.setText("Suchbegriff: ");
    such.setNextFocusableComponent(lfdnr);
    Dialog.setPreferredSize(such,200,19);
    such.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        such_keyTyped(e);
      }
    });
    lfdnr.setText("Lfd. Nr.");
    lfdnr.setNextFocusableComponent(datum);
    lfdnr.setMnemonic('L');
    datum.setText("Datum");
    datum.setNextFocusableComponent(stm);
    datum.setMnemonic('D');
    stm.setText("Steuermann");
    stm.setNextFocusableComponent(mannsch);
    stm.setMnemonic('T');
    mannsch.setText("Mannschaft");
    mannsch.setNextFocusableComponent(boot);
    mannsch.setMnemonic('M');
    boot.setText("Boot");
    boot.setNextFocusableComponent(ziel);
    boot.setMnemonic('B');
    ziel.setText("Ziel");
    ziel.setNextFocusableComponent(abfahrt);
    ziel.setMnemonic('Z');
    abfahrt.setText("Abfahrt");
    abfahrt.setNextFocusableComponent(ankunft);
    abfahrt.setMnemonic('F');
    ankunft.setText("Ankunft");
    ankunft.setNextFocusableComponent(bootskm);
    ankunft.setMnemonic('N');
    bootskm.setText("Boots-Km");
    bootskm.setNextFocusableComponent(mannschkm);
    bootskm.setMnemonic('O');
    mannschkm.setText("Mannschafts-Km");
    mannschkm.setNextFocusableComponent(bemerk);
    mannschkm.setMnemonic('A');
    bemerk.setText("Bemerkungen");
    bemerk.setNextFocusableComponent(fahrtart);
    bemerk.setMnemonic('E');
    alleButton.setNextFocusableComponent(keineButton);
    Dialog.setPreferredSize(alleButton,89,23);
    alleButton.setText("alle");
    alleButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        alleButton_actionPerformed(e);
      }
    });
    keineButton.setNextFocusableComponent(suchButton);
    Dialog.setPreferredSize(keineButton,89,23);
    keineButton.setText("keine");
    keineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        keineButton_actionPerformed(e);
      }
    });
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel3.setText("Weitersuchen mit F3 möglich!");
    fahrtart.setNextFocusableComponent(alleButton);
    fahrtart.setSelected(true);
    fahrtart.setText("Art der Fahrt");
    normalPanel.setLayout(gridBagLayout2);
    fehlersuchPanel.setLayout(gridBagLayout3);
    jLabel4.setText("Suche nach:");
    err_unvollst.setNextFocusableComponent(err_km);
    err_unvollst.setMnemonic('U');
    err_unvollst.setSelected(true);
    err_unvollst.setText("unvollständige Einträge");
    err_km.setNextFocusableComponent(err_unbekRuderer);
    err_km.setMnemonic('K');
    err_km.setSelected(true);
    err_km.setText("Einträge mit widersprüchlichen Kilometerangaben");
    err_unbekRuderer.setNextFocusableComponent(err_unbekRudererOhneGast);
    err_unbekRuderer.setMnemonic('R');
    err_unbekRuderer.setSelected(true);
    err_unbekRuderer.setText("Einträge mit unbekannten Ruderern");
    err_unbekBoot.setNextFocusableComponent(err_unbekZiel);
    err_unbekBoot.setMnemonic('B');
    err_unbekBoot.setSelected(true);
    err_unbekBoot.setText("Einträge mit unbekannten Booten");
    err_unbekZiel.setNextFocusableComponent(err_wafa);
    err_unbekZiel.setMnemonic('Z');
    err_unbekZiel.setSelected(true);
    err_unbekZiel.setText("Einträge mit unbekannten Zielen");
    jLabel5.setText("Weitersuchen mit F3 möglich!");
    err_nichtZurueckgetragen.setNextFocusableComponent(suchButton);
    err_nichtZurueckgetragen.setMnemonic('G');
    err_nichtZurueckgetragen.setSelected(true);
    err_nichtZurueckgetragen.setText("nicht zurückgetragene Einträge (efa im Bootshaus)");
    err_zuUebertragendeMehrtagesfahrten.setMnemonic('M');
    err_zuUebertragendeMehrtagesfahrten.setSelected(true);
    err_zuUebertragendeMehrtagesfahrten.setText("noch nicht konfigurierte Mehrtagesfahrten");
    err_zielfahrten.setNextFocusableComponent(err_vieleKm);
    err_zielfahrten.setMnemonic('O');
    err_zielfahrten.setSelected(true);
    err_zielfahrten.setText("Potentielle Zielfahrten (unbek. Ziel, >= 20 Km)");
    err_unbekRudererOhneGast.setNextFocusableComponent(err_unbekBoot);
    err_unbekRudererOhneGast.setMnemonic('I');
    err_unbekRudererOhneGast.setText("Unbekannte Einträge mit \'Gast\' ignorieren");
    err_wafa.setNextFocusableComponent(err_zielfahrten);
    err_wafa.setMnemonic('W');
    err_wafa.setSelected(true);
    err_wafa.setText("Potentielle Wanderfahrten (normale Fahrt, >= 30 Km)");
    err_vieleKm.setNextFocusableComponent(err_vieleKmKm);
    err_vieleKm.setMnemonic('F');
    err_vieleKm.setText("Fahrten mit mehr als");
    jLabel6.setText(" Km");
    err_vieleKmKm.setNextFocusableComponent(err_nichtZurueckgetragen);
    Dialog.setPreferredSize(err_vieleKmKm, 50, 19);
    err_vieleKmKm.setText("");
    alle2Button.setText("alle");
    alle2Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        alle2Button_actionPerformed(e);
      }
    });
    keine2Button.setText("keine");
    keine2Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        keine2Button_actionPerformed(e);
      }
    });
    normalPanel.add(jLabel2,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(lfdnr,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(datum,   new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(stm,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(mannsch,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(boot,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(ziel,   new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(abfahrt,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(ankunft,   new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(bootskm,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(mannschkm,   new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(bemerk,    new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 30), 0, 0));
    normalPanel.add(alleButton,    new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    normalPanel.add(keineButton,    new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    normalPanel.add(jLabel3,           new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 0, 10, 0), 0, 0));
    normalPanel.add(fahrtart,    new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    normalPanel.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    normalPanel.add(such,    new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 0, 0));
    this.getContentPane().add(suchButton, BorderLayout.SOUTH);
    this.getContentPane().add(auswahlPane,  BorderLayout.CENTER);
    auswahlPane.add(normalPanel,   "normale Suche");
    auswahlPane.add(fehlersuchPanel,    "Spezialsuche");
    fehlersuchPanel.add(jLabel4,            new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    fehlersuchPanel.add(err_unvollst,            new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_km,            new GridBagConstraints(0, 2, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_unbekRuderer,            new GridBagConstraints(0, 3, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_unbekBoot,            new GridBagConstraints(0, 5, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_unbekZiel,            new GridBagConstraints(0, 6, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(jLabel5,               new GridBagConstraints(0, 13, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_nichtZurueckgetragen,           new GridBagConstraints(0, 10, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_zuUebertragendeMehrtagesfahrten,          new GridBagConstraints(0, 11, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_zielfahrten,         new GridBagConstraints(1, 8, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_unbekRudererOhneGast,        new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fehlersuchPanel.add(err_wafa,       new GridBagConstraints(1, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_vieleKm,      new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(err_vieleKmKm,   new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(jLabel6,    new GridBagConstraints(3, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(alle2Button,   new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    fehlersuchPanel.add(keine2Button,   new GridBagConstraints(3, 12, 1, 1, 0.0, 0.0
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

  public static void gefunden(EfaFrame parent, DatenFelder d, java.awt.Component f) {
    parent.SetFields(d);
    f.requestFocus();
  }

  void suchButton_actionPerformed(ActionEvent e) {
    if (auswahlPane.getSelectedIndex()==0) {
      Daten.suchMode = Daten.SUCH_NORMAL;
      Daten.such = such.getText().trim();
      if (Daten.such.equals("")) {
        Dialog.infoDialog("Fehleder Suchbegriff","Wenn Du gar nichts suchen möchtest, warum klickst Du dann 'Suchen'?\nGib doch bitte einen Suchbegriff ein!");
        return;
      }
      Daten.such_lfdnr = lfdnr.isSelected();
      Daten.such_datum = datum.isSelected();
      Daten.such_stm = stm.isSelected();
      Daten.such_mannsch = mannsch.isSelected();
      Daten.such_boot = boot.isSelected();
      Daten.such_ziel = ziel.isSelected();
      Daten.such_abfahrt = abfahrt.isSelected();
      Daten.such_ankunft = ankunft.isSelected();
      Daten.such_bootskm = bootskm.isSelected();
      Daten.such_mannschkm = mannschkm.isSelected();
      Daten.such_bemerk = bemerk.isSelected();
      Daten.such_fahrtart = fahrtart.isSelected();
      if (!Daten.such_abfahrt && !Daten.such_ankunft && !Daten.such_bemerk && !Daten.such_boot &&
          !Daten.such_bootskm && !Daten.such_datum && !Daten.such_lfdnr && !Daten.such_mannsch &&
          !Daten.such_mannschkm && !Daten.such_stm && !Daten.such_ziel && !Daten.such_fahrtart) {
        Dialog.infoDialog("Kein Feld ausgewählt","Wenn Du gar nichts suchen möchtest, warum klickst Du dann 'Suchen'?\nWähle doch bitte zumindest ein zu durchsuchendes Feld aus!");
        return;
      }
    } else {
      Daten.suchMode = Daten.SUCH_ERROR;
      Daten.such_errUnvollst = err_unvollst.isSelected();
      Daten.such_errKm = err_km.isSelected();
      Daten.such_errUnbekRuderer = err_unbekRuderer.isSelected();
      Daten.such_errUnbekRudererOhneGast = err_unbekRudererOhneGast.isSelected();
      Daten.such_errUnbekBoot = err_unbekBoot.isSelected();
      Daten.such_errUnbekZiel = err_unbekZiel.isSelected();
      Daten.such_errWafa = err_wafa.isSelected();
      Daten.such_errZielfahrten = err_zielfahrten.isSelected();
      Daten.such_errVieleKm = err_vieleKm.isSelected();
      Daten.such_errVieleKmKm = EfaUtil.zehntelString2Int(err_vieleKmKm.getText().trim());
      Daten.such_errNichtZurueckgetragen = err_nichtZurueckgetragen.isSelected();
      Daten.such_errNichtKonfMTours = err_zuUebertragendeMehrtagesfahrten.isSelected();
      if (!Daten.such_errUnvollst  && !Daten.such_errKm && !Daten.such_errUnbekRuderer && !Daten.such_errUnbekBoot &&
          !Daten.such_errUnbekZiel && !Daten.such_errWafa && !Daten.such_errZielfahrten && !Daten.such_errVieleKm &&
          !Daten.such_errNichtZurueckgetragen && !Daten.such_errNichtKonfMTours) {
        Dialog.infoDialog("Kein Suchkriterium ausgewählt","Wenn Du gar nichts suchen möchtest, warum klickst Du dann 'Suchen'?\nWähle doch bitte zumindest ein Suchkriterium aus!");
        return;
      }

    }
    search(parent,this);
    cancel();
  }

  public static boolean search(EfaFrame parent, Component _this) {
    String s = Daten.such.toUpperCase();
    DatenFelder d;
    boolean weiter = true;
    while (weiter) {
      d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext();
      if (d == null)
        if (JOptionPane.showConfirmDialog(_this,"Suchbegriff nicht gefunden!\nSuche vom Anfang an fortsetzen?",
            "Nicht gefunden",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
          d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
        else weiter = false;
      if (d != null) {
        if (Daten.suchMode == Daten.SUCH_NORMAL) {
          if (Daten.such_lfdnr && d.get(Fahrtenbuch.LFDNR).toUpperCase().equals(s)) { gefunden(parent,d,parent.lfdnr); return true; }
          if (Daten.such_datum && d.get(Fahrtenbuch.DATUM).toUpperCase().indexOf(s) >= 0)
            if (EfaUtil.countCharInString(s,'.') != 2 || // wenn zwei Punkte im Suchstring, dann nur exakt dieses Datum finden!
                d.get(Fahrtenbuch.DATUM).toUpperCase().equals(s)) { gefunden(parent,d,parent.datum); return true; }
          if (Daten.such_boot && d.get(Fahrtenbuch.BOOT).toUpperCase().indexOf(s) >= 0) { gefunden(parent,d,parent.boot); return true; }
          if (Daten.such_stm && d.get(Fahrtenbuch.STM).toUpperCase().indexOf(s) >= 0) { gefunden(parent,d,parent.stm); return true; }
          if (Daten.such_mannsch)
            for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if (d.get(Fahrtenbuch.MANNSCH1+i).toUpperCase().indexOf(s) >= 0) {
              parent.setActiveMannsch(i / 8);
              gefunden(parent,d,parent.mannsch[i]);
              return true;
            }
          if (Daten.such_abfahrt && d.get(Fahrtenbuch.ABFAHRT).toUpperCase().indexOf(s) >= 0 && // wenn ":" enthalten, dann nur exakt diese Zeit finden
              (s.indexOf(":")<0 || d.get(Fahrtenbuch.ABFAHRT).toUpperCase().equals(s))) { gefunden(parent,d,parent.abfahrt); return true; }
          if (Daten.such_ankunft && d.get(Fahrtenbuch.ANKUNFT).toUpperCase().indexOf(s) >= 0 && // wenn ":" enthalten, dann nur exakt diese Zeit finden
              (s.indexOf(":")<0 || d.get(Fahrtenbuch.ANKUNFT).toUpperCase().equals(s))) { gefunden(parent,d,parent.ankunft); return true; }
          if (Daten.such_ziel && d.get(Fahrtenbuch.ZIEL).toUpperCase().indexOf(s) >= 0) { gefunden(parent,d,parent.ziel); return true; }
          if (Daten.such_bootskm && d.get(Fahrtenbuch.BOOTSKM).toUpperCase().equals(s)) { gefunden(parent,d,parent.bootskm); return true; }
          if (Daten.such_mannschkm && d.get(Fahrtenbuch.MANNSCHKM).toUpperCase().equals(s)) { gefunden(parent,d,parent.mannschkm); return true; }
          if (Daten.such_bemerk && d.get(Fahrtenbuch.BEMERK).toUpperCase().indexOf(s) >= 0) { gefunden(parent,d,parent.bemerk); return true; }
          if (Daten.such_fahrtart && d.get(Fahrtenbuch.FAHRTART).toUpperCase().indexOf(s) >= 0) { gefunden(parent,d,parent.fahrtDauer); return true; }
        } else {
          if (Daten.such_errUnvollst && d.get(Fahrtenbuch.BOOTSKM).length()==0) { gefunden(parent,d,parent.bootskm); return true; }
          if (Daten.such_errUnvollst && d.get(Fahrtenbuch.MANNSCHKM).length()==0) { gefunden(parent,d,parent.mannschkm); parent.mannschkm.setText(d.get(Fahrtenbuch.MANNSCHKM)); return true; }
          if (Daten.such_errUnvollst && d.get(Fahrtenbuch.BOOT).length()==0) { gefunden(parent,d,parent.boot); return true; }
          if (Daten.such_errUnvollst && d.get(Fahrtenbuch.ZIEL).length()==0) { gefunden(parent,d,parent.ziel); return true; }
          if (Daten.such_errUnvollst) {
            int l = d.get(Fahrtenbuch.STM).length();
            for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) l+= d.get(Fahrtenbuch.MANNSCH1+i).length();
            if (l == 0) { gefunden(parent,d,parent.stm); return true; }
          }
          if (Daten.such_errKm &&
              EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.BOOTSKM))*getAnzahlRuderer(d) != EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.MANNSCHKM))) { gefunden(parent,d,parent.mannschkm); parent.mannschkm.setText(d.get(Fahrtenbuch.MANNSCHKM)); return true; }
          if (Daten.such_errUnbekBoot && Daten.fahrtenbuch.getDaten() != null && Daten.fahrtenbuch.getDaten().boote != null &&
              d.get(Fahrtenbuch.BOOT).length()>0 && Daten.fahrtenbuch.getDaten().boote.getExact(d.get(Fahrtenbuch.BOOT)) == null) { gefunden(parent,d,parent.boot); return true; }
          if (Daten.such_errUnbekZiel && Daten.fahrtenbuch.getDaten() != null && Daten.fahrtenbuch.getDaten().ziele != null &&
              d.get(Fahrtenbuch.ZIEL).length()>0 && Daten.fahrtenbuch.getDaten().ziele.getExact(d.get(Fahrtenbuch.ZIEL)) == null) { gefunden(parent,d,parent.ziel); return true; }
          if (Daten.such_errWafa && EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.BOOTSKM))>=Daten.WAFAKM && d.get(Fahrtenbuch.FAHRTART).length()==0) { gefunden(parent,d,parent.ziel); return true; }
          if (Daten.efaConfig != null && Daten.efaConfig.showBerlinOptions &&
              Daten.such_errZielfahrten && Daten.fahrtenbuch.getDaten() != null && Daten.fahrtenbuch.getDaten().ziele != null &&
              d.get(Fahrtenbuch.ZIEL).length()>0 && Daten.fahrtenbuch.getDaten().ziele.getExact(d.get(Fahrtenbuch.ZIEL)) == null &&
              d.get(Fahrtenbuch.BOOTSKM).length()>0 && EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.BOOTSKM))>=Daten.ZIELFAHRTKM) { gefunden(parent,d,parent.ziel); return true; }
          if (Daten.such_errVieleKm && EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.BOOTSKM))>=Daten.such_errVieleKmKm) { gefunden(parent,d,parent.bootskm); return true; }
          if (Daten.such_errUnbekRuderer && Daten.fahrtenbuch.getDaten() != null && Daten.fahrtenbuch.getDaten().mitglieder != null) {
            if (d.get(Fahrtenbuch.STM).length()>0 && Daten.fahrtenbuch.getDaten().mitglieder.getExact(d.get(Fahrtenbuch.STM)) == null) {
              if (!Daten.such_errUnbekRudererOhneGast ||
                  (Daten.bezeichnungen != null && Daten.bezeichnungen.gast != null && Daten.bezeichnungen.gast.length()>0 && d.get(Fahrtenbuch.STM).toLowerCase().indexOf(Daten.bezeichnungen.gast.toLowerCase())<0)) {
                gefunden(parent, d, parent.stm);
                return true;
              }
            }
            for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++)
              if (d.get(Fahrtenbuch.MANNSCH1+i).length()>0 && Daten.fahrtenbuch.getDaten().mitglieder.getExact(d.get(Fahrtenbuch.MANNSCH1+i)) == null) {
                if (!Daten.such_errUnbekRudererOhneGast ||
                    (Daten.bezeichnungen != null && Daten.bezeichnungen.gast != null && Daten.bezeichnungen.gast.length()>0 && d.get(Fahrtenbuch.STM).toLowerCase().indexOf(Daten.bezeichnungen.gast.toLowerCase())<0)) {
                  parent.setActiveMannsch(i / 8);
                  gefunden(parent, d, parent.mannsch[i]);
                  return true;
                }
              }
          }
          if (Daten.such_errNichtZurueckgetragen && d.get(Fahrtenbuch.BOOTSKM).equals("0")) { gefunden(parent,d,parent.bootskm); return true; }
          if (Daten.such_errNichtKonfMTours && d.get(Fahrtenbuch.FAHRTART).startsWith(parent.mehrtagesfahrtKonfigurieren))  { gefunden(parent,d,parent.fahrtDauer); return true; }
        }
      }
    }
    parent.SetFields(parent.aktDatensatz);
    return false;
  }

  static int getAnzahlRuderer(DatenFelder d) {
    int c=0;
    if (d.get(Fahrtenbuch.STM).length()>0) c++;
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if (d.get(Fahrtenbuch.MANNSCH1+i).length()>0) c++;
    return c;
  }

  void alleButton_actionPerformed(ActionEvent e) {
    lfdnr.setSelected(true);
    datum.setSelected(true);
    stm.setSelected(true);
    mannsch.setSelected(true);
    boot.setSelected(true);
    ziel.setSelected(true);
    abfahrt.setSelected(true);
    ankunft.setSelected(true);
    bootskm.setSelected(true);
    mannschkm.setSelected(true);
    bemerk.setSelected(true);
    fahrtart.setSelected(true);
  }

  void keineButton_actionPerformed(ActionEvent e) {
    lfdnr.setSelected(false);
    datum.setSelected(false);
    stm.setSelected(false);
    mannsch.setSelected(false);
    boot.setSelected(false);
    ziel.setSelected(false);
    abfahrt.setSelected(false);
    ankunft.setSelected(false);
    bootskm.setSelected(false);
    mannschkm.setSelected(false);
    bemerk.setSelected(false);
    fahrtart.setSelected(false);
  }

  void such_keyTyped(KeyEvent e) {
    if (e.getKeyChar() == '\n') suchButton_actionPerformed(null);
  }

  void alle2Button_actionPerformed(ActionEvent e) {
    this.err_unvollst.setSelected(true);
    this.err_km.setSelected(true);
    this.err_unbekRuderer.setSelected(true);
    this.err_unbekRudererOhneGast.setSelected(true);
    this.err_unbekBoot.setSelected(true);
    this.err_unbekZiel.setSelected(true);
    this.err_wafa.setSelected(true);
    this.err_zielfahrten.setSelected(true);
    this.err_vieleKm.setSelected(true);
    this.err_nichtZurueckgetragen.setSelected(true);
    this.err_zuUebertragendeMehrtagesfahrten.setSelected(true);
  }

  void keine2Button_actionPerformed(ActionEvent e) {
    this.err_unvollst.setSelected(false);
    this.err_km.setSelected(false);
    this.err_unbekRuderer.setSelected(false);
    this.err_unbekRudererOhneGast.setSelected(false);
    this.err_unbekBoot.setSelected(false);
    this.err_unbekZiel.setSelected(false);
    this.err_wafa.setSelected(false);
    this.err_zielfahrten.setSelected(false);
    this.err_vieleKm.setSelected(false);
    this.err_nichtZurueckgetragen.setSelected(false);
    this.err_zuUebertragendeMehrtagesfahrten.setSelected(false);
  }



}