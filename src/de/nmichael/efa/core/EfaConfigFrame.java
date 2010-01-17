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
import java.io.*;
import java.util.*;

// @i18n complete

public class EfaConfigFrame extends JDialog implements ActionListener {
  EfaFrame parent;

  // parseAlias(String) parst den String und liefert -1 bei Erfolg, sonst die Position des Zeichens, das den
  // Fehler verursachte; eine Fehlermeldung steht in diesem Fall in dem String parseError
  public static String parseError="";

  String efaDirekt_vereinsLogo; // Dateiname für Vereins-Logo für efaDirekt

  String[] lookAndFeelArray;
  String[] languagesArray;

  JPanel allgemeinPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JCheckBox autogenAlias = new JCheckBox();
  JLabel jLabel2 = new JLabel();
  JTextField aliasFormat = new JTextField();
  JPanel backupPanel = new JPanel();
  JLabel jLabel3 = new JLabel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JCheckBox bakSave = new JCheckBox();
  JCheckBox bakMonat = new JCheckBox();
  JCheckBox bakKonv = new JCheckBox();
  JButton saveButton = new JButton();
  JLabel jLabel4 = new JLabel();
  JTextField bakDir = new JTextField();
  JLabel browserLabel = new JLabel();
  JTextField browser = new JTextField();
  JButton browserButton = new JButton();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  JScrollPane jScrollPane = new JScrollPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JTabbedPane Allgemein = new JTabbedPane();
  JPanel druckPanel = new JPanel();
  JPanel extProgPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel5 = new JLabel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JTextField printPageWidth = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField printPageHeight = new JTextField();
  JLabel jLabel9 = new JLabel();
  JTextField printLeftMargin = new JTextField();
  JLabel jLabel10 = new JLabel();
  JTextField printTopMargin = new JTextField();
  JLabel jLabel11 = new JLabel();
  JTextField printOverlap = new JTextField();
  JLabel jLabel12 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JLabel jLabel14 = new JLabel();
  JLabel jLabel15 = new JLabel();
  JLabel jLabel16 = new JLabel();
  JLabel jLabel17 = new JLabel();
  JLabel jLabel18 = new JLabel();
  JLabel jLabel19 = new JLabel();
  JLabel jLabel20 = new JLabel();
  JLabel jLabel21 = new JLabel();
  JLabel jLabel22 = new JLabel();
  JLabel jLabel23 = new JLabel();
  JLabel jLabel24 = new JLabel();
  JTextField f6 = new JTextField();
  JTextField f7 = new JTextField();
  JTextField f8 = new JTextField();
  JTextField f9 = new JTextField();
  JTextField f10 = new JTextField();
  JTextField f11 = new JTextField();
  JTextField f12 = new JTextField();
  JLabel jLabel25 = new JLabel();
  JTextField acrobat = new JTextField();
  JButton acrobatButton = new JButton();
  JCheckBox autoStandardmannsch = new JCheckBox();
  JCheckBox skipUhrzeit = new JCheckBox();
  JCheckBox skipMannschKm = new JCheckBox();
  JCheckBox skipBemerk = new JCheckBox();
  JButton bakdirButton = new JButton();
  JCheckBox bakTag = new JCheckBox();
  JPanel efaDirektPanel = new JPanel();
  JPanel efaDirektEinstellungenPanel = new JPanel();
  JPanel efaDirektFahrtenPanel = new JPanel();
  JPanel efaDirektErscheinungsbildPanel = new JPanel();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JCheckBox efaDirekt_zielBeiFahrtbeginn = new JCheckBox();
  JLabel jLabel26 = new JLabel();
  JLabel jLabel27 = new JLabel();
  JLabel jLabel28 = new JLabel();
  JTextField efaDirekt_abfahrtMinuten = new JTextField();
  JLabel jLabel29 = new JLabel();
  JTextField efaDirekt_ankunftMinuten = new JTextField();
  JLabel jLabel30 = new JLabel();
  JCheckBox efaDirekt_reservierungenEditErlaubt = new JCheckBox();
  JLabel jLabel31 = new JLabel();
  JCheckBox efaDirekt_MitglEfaExit = new JCheckBox();
  JLabel jLabel32 = new JLabel();
  JTextField efaDirekt_execOnEfaExit = new JTextField();
  JCheckBox skipZiel = new JCheckBox();
  JLabel jLabel33 = new JLabel();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JButton efaDirekt_fahrtBeginnenButton = new JButton();
  JButton efaDirekt_fahrtBeendenButton = new JButton();
  JButton efaDirekt_fahrtAbbrechenButton = new JButton();
  JButton efaDirekt_nachtragButton = new JButton();
  JButton efaDirekt_bootsreservierungenButton = new JButton();
  JButton efaDirekt_fahrtenbuchAnzeigenButton = new JButton();
  JButton efaDirekt_statistikErstellenButton = new JButton();
  JButton efaDirekt_nachrichtAnAdminButton = new JButton();
  JButton efaDirekt_adminModusButton = new JButton();
  JButton efaDirekt_FahrtBeginnenTextButton = new JButton();
  JButton efaDirekt_fahrtBeendenTextButton = new JButton();
  JCheckBox efaDirekt_bootsreservierungenAnzeigen = new JCheckBox();
  JCheckBox efaDirekt_statistikErstellenAnzeigen = new JCheckBox();
  JCheckBox efaDirekt_nachrichtAnAdminAnzeigen = new JCheckBox();
  JCheckBox efaDirekt_adminModusAnzeigen = new JCheckBox();
  JCheckBox efaDirekt_fahrtenbuchAnzeigenAnzeigen = new JCheckBox();
  JLabel efaDirekt_logoLabel = new JLabel();
  JButton efaDirekt_logoSelectButton = new JButton();
  JButton efaDirekt_logoDeleteButton = new JButton();
  JTabbedPane efaDirektPane = new JTabbedPane();
  JLabel jLabel34 = new JLabel();
  JCheckBox efaDirekt_reservierungenErlaubt = new JCheckBox();
  JCheckBox efaDirekt_maximiertStarten = new JCheckBox();
  JCheckBox efaDirekt_sortBooteByAnzahl = new JCheckBox();
  JCheckBox autoObmann = new JCheckBox();
  JLabel jLabel35 = new JLabel();
  JTextField efaDirekt_exitTime = new JTextField();
  JLabel jLabel36 = new JLabel();
  JTextField efaDirekt_execOnEfaAutoExit = new JTextField();
  JLabel jLabel37 = new JLabel();
  JCheckBox showObmann = new JCheckBox();
  JButton exec1FileSelectButton = new JButton();
  JButton exec2FileSelectButton = new JButton();
  JLabel jLabel38 = new JLabel();
  JComboBox lookAndFeel = new JComboBox();
  JComboBox languages = new JComboBox();
  JCheckBox efaDirekt_fensterNichtVerschiebbar = new JCheckBox();
  JCheckBox efaDirekt_immerImVordergrund = new JCheckBox();
  JPanel efaDirektErscheinungsbildWeiterePanel = new JPanel();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  JCheckBox efaDirekt_buttonsShowHotkey = new JCheckBox();
  JLabel jLabel39 = new JLabel();
  JTextField efaDirekt_fontSize = new JTextField();
  JCheckBox efaDirekt_showEingabeInfos = new JCheckBox();
  JLabel jLabel40 = new JLabel();
  JComboBox efaDirekt_fontStyle = new JComboBox();
  JCheckBox showBerlinOptions = new JCheckBox();
  JLabel jLabel41 = new JLabel();
  JComboBox defaultObmann = new JComboBox();
  JCheckBox efaDirekt_showUhr = new JCheckBox();
  JLabel efaDirekt_newsTextLabel = new JLabel();
  JTextField efaDirekt_newsText = new JTextField();
  JButton efaDirekt_spezialButton = new JButton();
  JCheckBox efaDirekt_spezialButtonAnzeigen = new JCheckBox();
  JButton efaDirekt_spezielButtonTextButton = new JButton();
  JLabel jLabel42 = new JLabel();
  JTextField efaDirekt_spezialButtonCmd = new JTextField();
  JCheckBox completePopupEnabled = new JCheckBox();
  JCheckBox correctMisspelledMitglieder = new JCheckBox();
  JPanel efaDirektBenachrichtigungPanel = new JPanel();
  JPanel efaDirektSunrisePanel = new JPanel();
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  JLabel jLabel43 = new JLabel();
  JLabel jLabel44 = new JLabel();
  JLabel jLabel45 = new JLabel();
  JLabel jLabel46 = new JLabel();
  JLabel jLabel47 = new JLabel();
  JTextField efaDirekt_emailServer = new JTextField();
  JTextField efaDirekt_emailUsername = new JTextField();
  JTextField efaDirekt_emailPassword = new JTextField();
  JTextField efaDirekt_emailAbsender = new JTextField();
  JCheckBox efaDirekt_resBooteNichtVerfuegbar = new JCheckBox();
  JCheckBox efaDirekt_showSunrise = new JCheckBox();
  JLabel jLabel48 = new JLabel();
  JLabel jLabel49 = new JLabel();
  JComboBox efaDirekt_latComboBox = new JComboBox();
  JTextField efaDirekt_latHH = new JTextField();
  JLabel jLabel50 = new JLabel();
  JTextField efaDirekt_latMM = new JTextField();
  JLabel jLabel51 = new JLabel();
  JTextField efaDirekt_latSS = new JTextField();
  JLabel jLabel52 = new JLabel();
  JComboBox efaDirekt_lonComboBox = new JComboBox();
  JTextField efaDirekt_lonHH = new JTextField();
  JLabel jLabel53 = new JLabel();
  JTextField efaDirekt_lonMM = new JTextField();
  JLabel jLabel54 = new JLabel();
  JTextField efaDirekt_lonSS = new JTextField();
  JLabel jLabel55 = new JLabel();
  JLabel jLabel56 = new JLabel();
  JTextField efaDirekt_emailAbsenderName = new JTextField();
  JLabel jLabel57 = new JLabel();
  JLabel jLabel58 = new JLabel();
  JTextField efaDirekt_emailBetreffPraefix = new JTextField();
  JScrollPane emailSignaturScrollPane = new JScrollPane();
  JTextArea efaDirekt_emailSignatur = new JTextArea();
  JCheckBox efaDirekt_reservierungenErlaubtZyklisch = new JCheckBox();
  JLabel jLabel59 = new JLabel();
  JCheckBox correctMisspelledBoote = new JCheckBox();
  JCheckBox correctMisspelledZiele = new JCheckBox();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  JLabel jLabel60 = new JLabel();
  JCheckBox efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen = new JCheckBox();
  JCheckBox efaDirekt_eintragNichtAenderbarUhrzeit = new JCheckBox();
  JCheckBox efaDirekt_eintragErlaubeNurMaxRudererzahl = new JCheckBox();
  JCheckBox efaDirekt_eintragErzwingeObmann = new JCheckBox();
  JButton aliasGenButton = new JButton();
  JCheckBox efaDirekt_wafaRegattaBooteNichtVerfuegbar = new JCheckBox();
  JCheckBox efadirekt_colorizedtextfield = new JCheckBox();
  JLabel jLabel61 = new JLabel();
  JCheckBox efaDirekt_nurBekannteBoote = new JCheckBox();
  JCheckBox efaDirekt_nurBekannteRuderer = new JCheckBox();
  JCheckBox efaDirekt_nurBekannteZiele = new JCheckBox();
  JLabel jLabel62 = new JLabel();
  JTextField efaDirekt_restartTime = new JTextField();
  JLabel jLabel63 = new JLabel();
  JLabel standardFahrtartLabel = new JLabel();
  JComboBox standardFahrtart = new JComboBox();
  JCheckBox efaDirekt_showFahrtzielInBooteAufFahrt = new JCheckBox();
  JCheckBox efaDirekt_mitgliederNamenHinzufuegen = new JCheckBox();
  JLabel jLabel64 = new JLabel();
  JLabel jLabel65 = new JLabel();
  JLabel jLabel66 = new JLabel();
  JTextField userdatadir = new JTextField();
  JButton userdatadirButton = new JButton();
  JPanel benachrichtigenBeiPanel = new JPanel();
  GridBagLayout gridBagLayout11 = new GridBagLayout();
  JLabel jLabel67 = new JLabel();
  JLabel jLabel68 = new JLabel();
  JLabel jLabel69 = new JLabel();
  JLabel jLabel70 = new JLabel();
  JLabel jLabel71 = new JLabel();
  JLabel jLabel72 = new JLabel();
  JLabel jLabel73 = new JLabel();
  JCheckBox efaDirekt_bnrError_admin = new JCheckBox();
  JCheckBox efaDirekt_bnrError_bootswart = new JCheckBox();
  JCheckBox efaDirekt_bnrWarning_admin = new JCheckBox();
  JCheckBox efaDirekt_bnrWarning_bootswart = new JCheckBox();
  JCheckBox efaDirekt_bnrBootsstatus_admin = new JCheckBox();
  JCheckBox efaDirekt_bnrBootsstatus_bootswart = new JCheckBox();


  public EfaConfigFrame(EfaFrame parent) {
    super(parent);
    this.parent = parent;
    construct();
  }

  public EfaConfigFrame(JDialog parent) {
    super(parent);
    construct();
  }

  private void construct() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    iniFelder();
    saveButton.requestFocus();
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

    this.setTitle(International.getString("Konfiguration"));
    this.setSize(new Dimension(723, 707));
    saveButton.setNextFocusableComponent(Allgemein);
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveButton_actionPerformed(e);
      }
    });
    allgemeinPanel.setLayout(gridBagLayout1);
    jLabel1.setText(International.getString("Allgemeine Einstellungen"));
    Mnemonics.setButton(this, autogenAlias, International.getStringWithMnemonic("Eingabe-Kürzel automatisch beim Anlegen neuer Mitglieder generieren"));
    autogenAlias.setNextFocusableComponent(aliasFormat);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Format der Eingabe-Kürzel")+": ");
    jLabel2.setLabelFor(aliasFormat);
    aliasFormat.setNextFocusableComponent(autoStandardmannsch);
    Dialog.setPreferredSize(aliasFormat,200,19);
    aliasFormat.setToolTipText("");
    aliasFormat.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        aliasFormat_focusLost(e);
      }
    });
    allgemeinPanel.setBorder(BorderFactory.createEtchedBorder());
    allgemeinPanel.setNextFocusableComponent(autogenAlias);
    jLabel3.setText(International.getString("Sicherungskopien erstellen"));
    backupPanel.setBorder(BorderFactory.createEtchedBorder());
    backupPanel.setNextFocusableComponent(bakDir);
    backupPanel.setLayout(gridBagLayout3);
    Mnemonics.setButton(this, bakSave, International.getStringWithMnemonic("bei jedem Speichern"));
    bakSave.setNextFocusableComponent(bakMonat);
    Mnemonics.setButton(this, bakMonat, International.getStringWithMnemonic("jeden Monat"));
    bakMonat.setNextFocusableComponent(bakTag);
    Mnemonics.setButton(this, bakKonv, International.getStringWithMnemonic("beim Konvertieren"));
    bakKonv.setNextFocusableComponent(saveButton);
    Mnemonics.setButton(this, saveButton, International.getStringWithMnemonic("Speichern"));
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Backup-Verzeichnis")+": ");
    jLabel4.setLabelFor(bakDir);
    bakDir.setNextFocusableComponent(bakSave);
    Dialog.setPreferredSize(bakDir,340,19);
    bakDir.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        bakDir_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, browserLabel, International.getStringWithMnemonic("Webbrowser")+": ");
    browserLabel.setLabelFor(browser);
    browser.setNextFocusableComponent(browserButton);
    Dialog.setPreferredSize(browser,400,19);
    browser.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        browser_focusLost(e);
      }
    });
    browserButton.setNextFocusableComponent(acrobat);
    Dialog.setPreferredSize(browserButton,50,20);
    browserButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    browserButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        browserButton_actionPerformed(e);
      }
    });
    extProgPanel.setLayout(gridBagLayout2);
    jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel5.setText(International.getString("Pfade zu externen Programmen"));
    druckPanel.setLayout(gridBagLayout4);
    jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel6.setText(International.getString("Seitenlayout für Druck"));
    Mnemonics.setLabel(this, jLabel7, International.getStringWithMnemonic("Seitenbreite")+": ");
    jLabel7.setLabelFor(printPageWidth);
    printPageWidth.setNextFocusableComponent(printPageHeight);
    Dialog.setPreferredSize(printPageWidth,80,19);
    printPageWidth.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    Mnemonics.setLabel(this, jLabel8, International.getStringWithMnemonic("Seitenhöhe")+": ");
    jLabel8.setLabelFor(printPageHeight);
    printPageHeight.setNextFocusableComponent(printLeftMargin);
    Dialog.setPreferredSize(printPageHeight,80,19);
    printPageHeight.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    Mnemonics.setLabel(this, jLabel9, International.getStringWithMnemonic("linker und rechter Rand")+": ");
    jLabel9.setLabelFor(printLeftMargin);
    Mnemonics.setLabel(this, jLabel10, International.getStringWithMnemonic("oberer und unterer Rand")+": ");
    jLabel10.setLabelFor(printTopMargin);
    Mnemonics.setLabel(this, jLabel11, International.getStringWithMnemonic("Seitenüberlappung")+": ");
    jLabel11.setLabelFor(printOverlap);
    printOverlap.setNextFocusableComponent(saveButton);
    Dialog.setPreferredSize(printOverlap,80,19);
    printOverlap.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    printTopMargin.setNextFocusableComponent(printOverlap);
    Dialog.setPreferredSize(printTopMargin,80,19);
    printTopMargin.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    printLeftMargin.setNextFocusableComponent(printTopMargin);
    Dialog.setPreferredSize(printLeftMargin,80,19);
    printLeftMargin.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    jLabel12.setText(" mm");
    jLabel13.setText(" mm");
    jLabel14.setText(" mm");
    jLabel15.setText(" mm");
    jLabel16.setText(" mm");
    jLabel17.setText(International.getString("Tastenbelegungen für Bemerkungs-Feld")+":");
    jLabel18.setLabelFor(f6);
    jLabel18.setText("F6: ");
    jLabel19.setLabelFor(f7);
    jLabel19.setText("F7: ");
    jLabel20.setLabelFor(f8);
    jLabel20.setText("F8: ");
    jLabel21.setLabelFor(f9);
    jLabel21.setText("F9: ");
    jLabel22.setLabelFor(f10);
    jLabel22.setText("F10: ");
    jLabel23.setLabelFor(f11);
    jLabel23.setText("F11: ");
    jLabel24.setLabelFor(f12);
    jLabel24.setText("F12: ");
    f6.setNextFocusableComponent(f7);
    Dialog.setPreferredSize(f6,200,19);
    f7.setNextFocusableComponent(f8);
    Dialog.setPreferredSize(f7,200,19);
    f8.setNextFocusableComponent(f9);
    Dialog.setPreferredSize(f8,200,19);
    f9.setNextFocusableComponent(f10);
    Dialog.setPreferredSize(f9,200,19);
    f10.setNextFocusableComponent(f11);
    Dialog.setPreferredSize(f10,200,19);
    f11.setNextFocusableComponent(f12);
    Dialog.setPreferredSize(f11,200,19);
    f12.setNextFocusableComponent(saveButton);
    Dialog.setPreferredSize(f12,200,19);
    Mnemonics.setLabel(this, jLabel25, International.getStringWithMnemonic("Acrobat Reader")+": ");
    acrobat.setNextFocusableComponent(acrobatButton);
    Dialog.setPreferredSize(acrobat,400,19);
    acrobat.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        acrobat_focusLost(e);
      }
    });
    acrobatButton.setNextFocusableComponent(saveButton);
    Dialog.setPreferredSize(acrobatButton,50,20);
    acrobatButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    acrobatButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        acrobatButton_actionPerformed(e);
      }
    });
    extProgPanel.setNextFocusableComponent(browser);
    druckPanel.setNextFocusableComponent(printPageWidth);
    Mnemonics.setButton(this, autoStandardmannsch, International.getStringWithMnemonic("Standardmannschaft automatisch eintragen"));
    autoStandardmannsch.setNextFocusableComponent(standardFahrtart);
    Mnemonics.setButton(this, skipUhrzeit, International.getStringWithMnemonic("Eingabefelder \'Uhrzeit\' überspringen"));
    skipUhrzeit.setNextFocusableComponent(skipZiel);
    Mnemonics.setButton(this, skipMannschKm, International.getStringWithMnemonic("Eingabefeld \'Mannsch.-Km\' überspringen"));
    skipMannschKm.setNextFocusableComponent(skipBemerk);
    Mnemonics.setButton(this, skipBemerk, International.getStringWithMnemonic("Bei der Eingabe das Feld \'Bemerkungen\' überspringen"));
    skipBemerk.setNextFocusableComponent(f6);
    Dialog.setPreferredSize(bakdirButton,50,20);
    bakdirButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    bakdirButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bakdirButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, bakTag, International.getStringWithMnemonic("jeden Tag"));
    bakTag.setNextFocusableComponent(bakKonv);
    efaDirektPanel.setLayout(borderLayout3);
    efaDirektEinstellungenPanel.setLayout(gridBagLayout5);
    Mnemonics.setButton(this, efaDirekt_zielBeiFahrtbeginn, International.getStringWithMnemonic("Ziel muß bereits bei Fahrtbeginn angegeben werden"));
    efaDirekt_zielBeiFahrtbeginn.setNextFocusableComponent(efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen);
    jLabel26.setText(International.getString("Berechnung der vorgeschlagenen Uhrzeiten bei Abfahrt und Ankunft")+":");
    jLabel27.setText(" "+International.getString("Minuten zu aktueller Zeit addieren"));
    Mnemonics.setLabel(this, jLabel28, International.getStringWithMnemonic("Abfahrt")+": ");
    jLabel28.setLabelFor(efaDirekt_abfahrtMinuten);
    Mnemonics.setLabel(this, jLabel29, International.getStringWithMnemonic("Ankunft")+": ");
    jLabel29.setLabelFor(efaDirekt_ankunftMinuten);
    jLabel30.setText(" "+International.getString("Minuten von aktueller Zeit abziehen"));
    efaDirekt_abfahrtMinuten.setNextFocusableComponent(efaDirekt_ankunftMinuten);
    Dialog.setPreferredSize(efaDirekt_abfahrtMinuten,50,19);
    efaDirekt_abfahrtMinuten.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_ankunftMinuten.setNextFocusableComponent(efaDirekt_eintragNichtAenderbarUhrzeit);
    Dialog.setPreferredSize(efaDirekt_ankunftMinuten,50,19);
    efaDirekt_ankunftMinuten.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    Mnemonics.setButton(this, efaDirekt_reservierungenEditErlaubt, International.getStringWithMnemonic("Mitglieder dürfen Bootsreservierungen verändern und löschen"));
    efaDirekt_reservierungenEditErlaubt.setNextFocusableComponent(efaDirekt_mitgliederNamenHinzufuegen);
    jLabel31.setText(International.getString("Einstellungen für efa beim Einsatz im Bootshaus"));
    Mnemonics.setButton(this, efaDirekt_MitglEfaExit, International.getStringWithMnemonic("Mitglieder dürfen efa beenden"));
    efaDirekt_MitglEfaExit.setNextFocusableComponent(efaDirekt_execOnEfaExit);
    Mnemonics.setLabel(this, jLabel32, International.getStringWithMnemonic("Folgendes Kommando beim Beenden von efa durch Mitglieder ausführen")+": ");
    jLabel32.setLabelFor(efaDirekt_execOnEfaExit);
    efaDirekt_execOnEfaExit.setNextFocusableComponent(efaDirekt_exitTime);
    Mnemonics.setButton(this, skipZiel, International.getStringWithMnemonic("Eingabefeld \'Ziel\' überspringen (Ziel nicht erforderlich)"));
    skipZiel.setNextFocusableComponent(skipMannschKm);
    efaDirektErscheinungsbildPanel.setLayout(gridBagLayout6);
    Dialog.setPreferredSize(jLabel33,280,13);
    jLabel33.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel33.setText(International.getString("Erscheinungsbild der Buttons"));
    efaDirektErscheinungsbildPanel.setBorder(BorderFactory.createEtchedBorder());
    efaDirektEinstellungenPanel.setBorder(BorderFactory.createEtchedBorder());
    efaDirektFahrtenPanel.setBorder(BorderFactory.createEtchedBorder());
    efaDirektFahrtenPanel.setLayout(gridBagLayout9);
    efaDirekt_fahrtBeginnenButton.setNextFocusableComponent(efaDirekt_FahrtBeginnenTextButton);
    Dialog.setPreferredSize(efaDirekt_fahrtBeginnenButton,200,23);
    efaDirekt_fahrtBeginnenButton.setText(International.getString("Fahrt beginnen")+" >>>");
    efaDirekt_fahrtBeginnenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtBeendenButton.setNextFocusableComponent(efaDirekt_fahrtBeendenTextButton);
    Dialog.setPreferredSize(efaDirekt_fahrtBeendenButton,200,23);
    efaDirekt_fahrtBeendenButton.setText("<<< "+International.getString("Fahrt beenden"));
    efaDirekt_fahrtBeendenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtAbbrechenButton.setNextFocusableComponent(efaDirekt_nachtragButton);
    Dialog.setPreferredSize(efaDirekt_fahrtAbbrechenButton,200,23);
    Mnemonics.setButton(this, efaDirekt_fahrtAbbrechenButton, International.getStringWithMnemonic("Fahrt abbrechen"));
    efaDirekt_fahrtAbbrechenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_nachtragButton.setNextFocusableComponent(efaDirekt_bootsreservierungenButton);
    Dialog.setPreferredSize(efaDirekt_nachtragButton,200,23);
    Mnemonics.setButton(this, efaDirekt_nachtragButton, International.getStringWithMnemonic("Nachtrag"));
    efaDirekt_nachtragButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_bootsreservierungenButton.setNextFocusableComponent(efaDirekt_bootsreservierungenAnzeigen);
    Dialog.setPreferredSize(efaDirekt_bootsreservierungenButton,200,23);
    Mnemonics.setButton(this, efaDirekt_bootsreservierungenButton, International.getStringWithMnemonic("Bootsreservierungen"));
    efaDirekt_bootsreservierungenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtenbuchAnzeigenButton.setNextFocusableComponent(efaDirekt_fahrtenbuchAnzeigenAnzeigen);
    Dialog.setPreferredSize(efaDirekt_fahrtenbuchAnzeigenButton,200,23);
    Mnemonics.setButton(this, efaDirekt_fahrtenbuchAnzeigenButton, International.getStringWithMnemonic("Fahrtenbuch anzeigen"));
    efaDirekt_fahrtenbuchAnzeigenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_statistikErstellenButton.setNextFocusableComponent(efaDirekt_statistikErstellenAnzeigen);
    Dialog.setPreferredSize(efaDirekt_statistikErstellenButton,200,23);
    Mnemonics.setButton(this, efaDirekt_statistikErstellenButton, International.getStringWithMnemonic("Statistik erstellen"));
    efaDirekt_statistikErstellenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_nachrichtAnAdminButton.setNextFocusableComponent(efaDirekt_nachrichtAnAdminAnzeigen);
    Dialog.setPreferredSize(efaDirekt_nachrichtAnAdminButton,200,23);
    Mnemonics.setButton(this, efaDirekt_nachrichtAnAdminButton, International.getStringWithMnemonic("Nachricht an Admin"));
    efaDirekt_nachrichtAnAdminButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_adminModusButton.setNextFocusableComponent(efaDirekt_adminModusAnzeigen);
    Dialog.setPreferredSize(efaDirekt_adminModusButton,200,23);
    Mnemonics.setButton(this, efaDirekt_adminModusButton, International.getStringWithMnemonic("Admin-Modus"));
    efaDirekt_adminModusButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_FahrtBeginnenTextButton.setNextFocusableComponent(efaDirekt_fahrtBeendenButton);
    Mnemonics.setButton(this, efaDirekt_FahrtBeginnenTextButton, International.getStringWithMnemonic("Text"));
    efaDirekt_FahrtBeginnenTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_FahrtBeginnenTextButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtBeendenTextButton.setNextFocusableComponent(efaDirekt_fahrtAbbrechenButton);
    Mnemonics.setButton(this, efaDirekt_fahrtBeendenTextButton, International.getStringWithMnemonic("Text"));
    efaDirekt_fahrtBeendenTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_fahrtBeendenTextButton_actionPerformed(e);
      }
    });
    efaDirekt_bootsreservierungenAnzeigen.setNextFocusableComponent(efaDirekt_fahrtenbuchAnzeigenButton);
    efaDirekt_bootsreservierungenAnzeigen.setText(International.getString("anzeigen"));
    efaDirekt_statistikErstellenAnzeigen.setNextFocusableComponent(efaDirekt_nachrichtAnAdminButton);
    efaDirekt_statistikErstellenAnzeigen.setText(International.getString("anzeigen"));
    efaDirekt_nachrichtAnAdminAnzeigen.setNextFocusableComponent(efaDirekt_adminModusButton);
    efaDirekt_nachrichtAnAdminAnzeigen.setText(International.getString("anzeigen"));
    efaDirekt_adminModusAnzeigen.setNextFocusableComponent(efaDirekt_spezialButton);
    efaDirekt_adminModusAnzeigen.setText(International.getString("anzeigen"));
    efaDirekt_fahrtenbuchAnzeigenAnzeigen.setNextFocusableComponent(efaDirekt_statistikErstellenButton);
    efaDirekt_fahrtenbuchAnzeigenAnzeigen.setText(International.getString("anzeigen"));
    efaDirekt_logoLabel.setBorder(BorderFactory.createEtchedBorder());
    efaDirekt_logoLabel.setPreferredSize(new Dimension(192, 64));
    efaDirekt_logoLabel.setToolTipText(International.getString("Vereinslogo"));
    efaDirekt_logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    efaDirekt_logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    efaDirekt_logoSelectButton.setNextFocusableComponent(efaDirekt_logoDeleteButton);
    Mnemonics.setButton(this, efaDirekt_logoSelectButton, International.getStringWithMnemonic("auswählen"));
    efaDirekt_logoSelectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_logoSelectButton_actionPerformed(e);
      }
    });
    efaDirekt_logoDeleteButton.setNextFocusableComponent(efaDirekt_maximiertStarten);
    Mnemonics.setButton(this, efaDirekt_logoDeleteButton, International.getStringWithMnemonic("entfernen"));
    efaDirekt_logoDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_logoDeleteButton_actionPerformed(e);
      }
    });
    jLabel34.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel34.setText(International.getString("Vereinslogo"));
    Mnemonics.setButton(this, efaDirekt_reservierungenErlaubt, International.getStringWithMnemonic("Mitglieder dürfen Boote reservieren") + 
            " (" + International.getString("einmalige Reservierungen") + ")");
    efaDirekt_reservierungenErlaubt.setNextFocusableComponent(efaDirekt_reservierungenErlaubtZyklisch);
    Mnemonics.setButton(this, efaDirekt_maximiertStarten, International.getStringWithMnemonic("efa maximiert starten"));
    efaDirekt_maximiertStarten.setNextFocusableComponent(efaDirekt_fensterNichtVerschiebbar);
    Mnemonics.setButton(this, efaDirekt_sortBooteByAnzahl, International.getStringWithMnemonic("sortiere Boote nach Anzahl der Ruderplätze"));
    efaDirekt_sortBooteByAnzahl.setNextFocusableComponent(efaDirekt_showEingabeInfos);
    Mnemonics.setButton(this, autoObmann, International.getStringWithMnemonic("Obmann bei Eingabe automatisch auswählen"));
    autoObmann.setNextFocusableComponent(defaultObmann);
    Mnemonics.setLabel(this, jLabel35, International.getStringWithMnemonic("efa automatisch um")+" "); // das ist Scheiße zum Übersetzen! Egal, EfaConfig wird eh ersetzt!
    jLabel35.setLabelFor(efaDirekt_exitTime);
    jLabel36.setText(" "+International.getString("Uhr beenden")); // das ist Scheiße zum Übersetzen! Egal, EfaConfig wird eh ersetzt!
    Mnemonics.setLabel(this, jLabel37, International.getStringWithMnemonic("Folgendes Kommando beim automatischen Beenden von efa ausführen")+": ");
    jLabel37.setLabelFor(efaDirekt_execOnEfaAutoExit);
    efaDirekt_exitTime.setNextFocusableComponent(efaDirekt_execOnEfaAutoExit);
    Dialog.setPreferredSize(efaDirekt_exitTime,80,19);
    efaDirekt_exitTime.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        efaDirekt_exitTime_focusLost(e);
      }
    });
    efaDirekt_execOnEfaAutoExit.setNextFocusableComponent(efaDirekt_restartTime);
    Mnemonics.setButton(this, showObmann, International.getStringWithMnemonic("Obmann-Auswahlliste anzeigen"));
    showObmann.setNextFocusableComponent(autoObmann);
    Dialog.setPreferredSize(exec1FileSelectButton,50,20);
    Dialog.setPreferredSize(exec2FileSelectButton,50,20);
    exec1FileSelectButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    exec1FileSelectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exec1FileSelectButton_actionPerformed(e);
      }
    });
    exec2FileSelectButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    exec2FileSelectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exec2FileSelectButton_actionPerformed(e);
      }
    });
    jLabel38.setText(International.getString("Look & Feel")+": ");
    Mnemonics.setButton(this, efaDirekt_fensterNichtVerschiebbar, International.getStringWithMnemonic("Hauptfenster nicht verschiebbar"));
    efaDirekt_fensterNichtVerschiebbar.setNextFocusableComponent(efaDirekt_immerImVordergrund);
    efaDirekt_fensterNichtVerschiebbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_fensterNichtVerschiebbar_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, efaDirekt_immerImVordergrund, International.getStringWithMnemonic("efa immer im Vordergrund"));
    efaDirekt_immerImVordergrund.setNextFocusableComponent(efaDirekt_sortBooteByAnzahl);
    efaDirekt_immerImVordergrund.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_immerImVordergrund_actionPerformed(e);
      }
    });
    efaDirektErscheinungsbildWeiterePanel.setLayout(gridBagLayout7);
    Mnemonics.setButton(this, efaDirekt_buttonsShowHotkey, International.getStringWithMnemonic("Hotkeys für Buttons anzeigen"));
    efaDirekt_buttonsShowHotkey.setNextFocusableComponent(efaDirekt_showFahrtzielInBooteAufFahrt);
    efaDirekt_buttonsShowHotkey.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_buttonsShowHotkey_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel39, International.getStringWithMnemonic("Schriftgröße (leer=Standard)")+": ");
    jLabel39.setLabelFor(efaDirekt_fontSize);
    efaDirekt_fontSize.setNextFocusableComponent(efaDirekt_fontStyle);
    efaDirekt_fontSize.setToolTipText(International.getString("Schriftgröße in Punkten (6 bis 32, Standard: 12)"));
    Dialog.setPreferredSize(efaDirekt_fontSize,50,19);
    efaDirekt_fontSize.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        efaDirekt_fontSize_focusLost(e);
      }
    });
    Mnemonics.setButton(this, efaDirekt_showEingabeInfos, International.getStringWithMnemonic("Eingabehinweise anzeigen"));
    efaDirekt_showEingabeInfos.setNextFocusableComponent(efadirekt_colorizedtextfield);
    Mnemonics.setLabel(this, jLabel40, International.getStringWithMnemonic("Schriftstil")+": ");
    jLabel40.setLabelFor(efaDirekt_fontStyle);
    Dialog.setPreferredSize(efaDirekt_fontStyle,100,20);
    efaDirekt_fontStyle.addItem(International.getString("Standard"));
    efaDirekt_fontStyle.addItem(International.getString("normal"));
    efaDirekt_fontStyle.addItem(International.getString("fett"));
    efaDirekt_fontStyle.setNextFocusableComponent(saveButton);
    Mnemonics.setButton(this, showBerlinOptions, International.onlyFor("Berlin-spezifische Optionen anzeigen","de"));
    showBerlinOptions.setNextFocusableComponent(saveButton);
    lookAndFeel.setNextFocusableComponent(showBerlinOptions);
    Mnemonics.setLabel(this, jLabel41, International.getStringWithMnemonic("Standard-Obmann für ungesteuerte Boote")+": ");
    jLabel41.setLabelFor(defaultObmann);
    defaultObmann.setNextFocusableComponent(completePopupEnabled);
    Mnemonics.setButton(this, efaDirekt_showUhr, International.getStringWithMnemonic("Uhr anzeigen"));
    efaDirekt_showUhr.setNextFocusableComponent(efaDirekt_newsText);
    Mnemonics.setButton(this, efaDirekt_spezialButton, International.getStringWithMnemonic("Spezial-Button"));
    efaDirekt_spezialButton.setNextFocusableComponent(efaDirekt_spezialButtonAnzeigen);
    Dialog.setPreferredSize(efaDirekt_spezialButton,200,23);
    efaDirekt_spezialButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_spezialButtonAnzeigen.setNextFocusableComponent(efaDirekt_spezielButtonTextButton);
    Mnemonics.setButton(this, efaDirekt_spezialButtonAnzeigen, International.getStringWithMnemonic("anzeigen"));
    Mnemonics.setButton(this, efaDirekt_spezielButtonTextButton, International.getStringWithMnemonic("Text"));
    efaDirekt_spezielButtonTextButton.setNextFocusableComponent(efaDirekt_spezialButtonCmd);
    efaDirekt_spezielButtonTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_spezielButtonTextButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel42, International.getStringWithMnemonic("bei Click ausführen")+": ");
    jLabel42.setLabelFor(efaDirekt_spezialButtonCmd);
    efaDirekt_spezialButtonCmd.setNextFocusableComponent(efaDirekt_buttonsShowHotkey);
    Mnemonics.setButton(this, completePopupEnabled, International.getStringWithMnemonic("Beim Vervollständigen Popup-Liste anzeigen"));
    completePopupEnabled.setNextFocusableComponent(correctMisspelledMitglieder);
    Mnemonics.setButton(this, correctMisspelledMitglieder, International.getStringWithMnemonic("Mitglieder"));
    correctMisspelledMitglieder.setNextFocusableComponent(correctMisspelledBoote);
    efaDirektBenachrichtigungPanel.setLayout(gridBagLayout8);
    efaDirektSunrisePanel.setLayout(gridBagLayout10);
    jLabel43.setText(International.getString("Einstellungen zum email-Versand")+":");
    Mnemonics.setLabel(this, jLabel44, International.getStringWithMnemonic("SMTP-Server")+": ");
    jLabel44.setLabelFor(efaDirekt_emailServer);
    Mnemonics.setLabel(this, jLabel45, International.getStringWithMnemonic("Username")+": ");
    jLabel45.setLabelFor(efaDirekt_emailUsername);
    Mnemonics.setLabel(this, jLabel46, International.getStringWithMnemonic("Paßwort")+": ");
    jLabel46.setLabelFor(efaDirekt_emailPassword);
    Mnemonics.setLabel(this, jLabel47, International.getStringWithMnemonic("Absender-Adresse")+": ");
    jLabel47.setLabelFor(efaDirekt_emailAbsender);
    efaDirekt_emailServer.setNextFocusableComponent(efaDirekt_emailUsername);
    Dialog.setPreferredSize(efaDirekt_emailServer,250,19);
    efaDirekt_emailUsername.setNextFocusableComponent(efaDirekt_emailPassword);
    Dialog.setPreferredSize(efaDirekt_emailUsername,250,19);
    efaDirekt_emailPassword.setNextFocusableComponent(efaDirekt_emailAbsenderName);
    Dialog.setPreferredSize(efaDirekt_emailPassword,250,19);
    efaDirekt_emailAbsender.setNextFocusableComponent(efaDirekt_emailBetreffPraefix);
    Dialog.setPreferredSize(efaDirekt_emailAbsender,250,19);
    Mnemonics.setButton(this, efaDirekt_resBooteNichtVerfuegbar, International.getStringWithMnemonic("Reservierte Boote als \'nicht verfügbar\' anzeigen"));
    efaDirekt_resBooteNichtVerfuegbar.setNextFocusableComponent(efaDirekt_wafaRegattaBooteNichtVerfuegbar);
    Mnemonics.setButton(this, efaDirekt_showSunrise, International.getStringWithMnemonic("Sonnenaufgangs- und -untergangszeit anzeigen"));
    Mnemonics.setLabel(this, jLabel48, International.getStringWithMnemonic("geographische Breite")+": ");
    jLabel48.setLabelFor(efaDirekt_latComboBox);
    Mnemonics.setLabel(this, jLabel49, International.getStringWithMnemonic("geographische Länge")+": ");
    jLabel49.setLabelFor(efaDirekt_lonComboBox);
    efaDirekt_latHH.setNextFocusableComponent(efaDirekt_latMM);
    Dialog.setPreferredSize(efaDirekt_latHH,50,19);
    jLabel50.setText("° ");
    efaDirekt_latMM.setNextFocusableComponent(efaDirekt_latSS);
    Dialog.setPreferredSize(efaDirekt_latMM,50,19);
    jLabel51.setText("\' ");
    efaDirekt_latSS.setNextFocusableComponent(efaDirekt_lonComboBox);
    Dialog.setPreferredSize(efaDirekt_latSS,50,19);
    jLabel52.setText("\"");
    efaDirekt_lonHH.setNextFocusableComponent(efaDirekt_lonMM);
    Dialog.setPreferredSize(efaDirekt_lonHH,50,19);
    jLabel53.setText("° ");
    efaDirekt_lonMM.setNextFocusableComponent(efaDirekt_lonSS);
    Dialog.setPreferredSize(efaDirekt_lonMM,50,19);
    jLabel54.setText("\' ");
    efaDirekt_lonSS.setNextFocusableComponent(saveButton);
    Dialog.setPreferredSize(efaDirekt_lonSS,50,19);
    jLabel55.setText("\"");
    efaDirekt_latComboBox.setNextFocusableComponent(efaDirekt_latHH);
    efaDirekt_lonComboBox.setNextFocusableComponent(efaDirekt_lonHH);
    efaDirekt_latHH.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_latMM.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_latSS.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_lonHH.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_lonMM.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_lonSS.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    efaDirekt_showSunrise.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_showSunrise_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, jLabel56, International.getStringWithMnemonic("Absender-Name")+": ");
    jLabel56.setLabelFor(efaDirekt_emailAbsenderName);
    Mnemonics.setLabel(this, jLabel57, International.getStringWithMnemonic("Betreff (Präfix)")+": ");
    jLabel57.setLabelFor(efaDirekt_emailBetreffPraefix);
    Mnemonics.setLabel(this, jLabel58, International.getStringWithMnemonic("Signatur")+": ");
    jLabel58.setLabelFor(efaDirekt_emailSignatur);
    emailSignaturScrollPane.setPreferredSize(new Dimension(200, 50));
    efaDirekt_emailAbsenderName.setNextFocusableComponent(efaDirekt_emailAbsender);
    efaDirekt_emailBetreffPraefix.setNextFocusableComponent(efaDirekt_emailSignatur);
    efaDirekt_emailSignatur.setNextFocusableComponent(efaDirekt_showSunrise);
    Mnemonics.setButton(this, efaDirekt_reservierungenErlaubtZyklisch, International.getStringWithMnemonic("Mitglieder dürfen Boote reservieren") +
            " (" + International.getString("wöchentliche Reservierungen") + ")");
    efaDirekt_reservierungenErlaubtZyklisch.setNextFocusableComponent(efaDirekt_reservierungenEditErlaubt);
    jLabel59.setText(International.getString("Fahrtenbucheinträge auf Tippfehler prüfen für")+" ");
    correctMisspelledBoote.setNextFocusableComponent(correctMisspelledZiele);
    correctMisspelledBoote.setText(International.getString("Boote"));
    correctMisspelledZiele.setNextFocusableComponent(skipUhrzeit);
    correctMisspelledZiele.setText(International.getString("Ziele"));
    jLabel60.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel60.setText(International.getString("Einstellungen zum Eintragen von Fahrten"));
    Mnemonics.setButton(this, efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen, International.getStringWithMnemonic("Vorgeschlagene Kilometer bei bekannten Zielen können nicht geändert werden"));
    efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.setNextFocusableComponent(efaDirekt_nurBekannteBoote);
    Mnemonics.setButton(this, efaDirekt_eintragNichtAenderbarUhrzeit, International.getStringWithMnemonic("Vorgeschlagene Uhrzeiten können nicht geändert werden"));
    efaDirekt_eintragNichtAenderbarUhrzeit.setNextFocusableComponent(efaDirekt_zielBeiFahrtbeginn);
    Mnemonics.setButton(this, efaDirekt_eintragErlaubeNurMaxRudererzahl, International.getStringWithMnemonic("Nur für das Boot maximal mögliche Anzahl an Ruderern erlauben"));
    efaDirekt_eintragErlaubeNurMaxRudererzahl.setNextFocusableComponent(efaDirekt_eintragErzwingeObmann);
    Mnemonics.setButton(this, efaDirekt_eintragErzwingeObmann, International.getStringWithMnemonic("Obmann muß ausgewählt werden"));
    efaDirekt_eintragErzwingeObmann.setNextFocusableComponent(efaDirekt_abfahrtMinuten);
    Mnemonics.setButton(this, aliasGenButton, International.getStringWithMnemonic("neu generieren"));
    aliasGenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        aliasGenButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, efaDirekt_wafaRegattaBooteNichtVerfuegbar, International.getStringWithMnemonic("Boote auf Regatta, Trainingslager oder Mehrtagesfahrt als \'nicht verfügbar\' anzeigen"));
    Mnemonics.setButton(this, efadirekt_colorizedtextfield, International.getStringWithMnemonic("aktuelles Eingabefeld farblich hervorheben"));
    efadirekt_colorizedtextfield.setNextFocusableComponent(efaDirekt_showUhr);
    jLabel61.setText(International.getString("Beim Eintrag von Fahrten nur bekannte Namen erlauben für")+":");
    Mnemonics.setButton(this, efaDirekt_nurBekannteBoote, International.getStringWithMnemonic("Boote"));
    efaDirekt_nurBekannteBoote.setNextFocusableComponent(efaDirekt_nurBekannteRuderer);
    Mnemonics.setButton(this, efaDirekt_nurBekannteRuderer, International.getStringWithMnemonic("Ruderer"));
    efaDirekt_nurBekannteRuderer.setNextFocusableComponent(efaDirekt_nurBekannteZiele);
    Mnemonics.setButton(this, efaDirekt_nurBekannteZiele, International.getStringWithMnemonic("Ziele"));
    efaDirekt_nurBekannteZiele.setNextFocusableComponent(saveButton);
    Mnemonics.setLabel(this, jLabel62, International.getStringWithMnemonic("efa automatisch um")+" ");// das ist Scheiße zum Übersetzen! Egal, EfaConfig wird eh ersetzt!
    jLabel62.setLabelFor(efaDirekt_restartTime);
    jLabel63.setText(" "+International.getString("Uhr neu starten"));// das ist Scheiße zum Übersetzen! Egal, EfaConfig wird eh ersetzt!
    efaDirekt_restartTime.setNextFocusableComponent(saveButton);
    efaDirekt_restartTime.setText("");
    Dialog.setPreferredSize(efaDirekt_restartTime,80,19);
    efaDirekt_restartTime.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        efaDirekt_restartTime_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, standardFahrtartLabel, International.getStringWithMnemonic("Standard-Fahrtart")+": ");
    standardFahrtart.setNextFocusableComponent(showObmann);
    Mnemonics.setButton(this, efaDirekt_showFahrtzielInBooteAufFahrt, International.getMessage("Fahrtziel in der Liste {list} anzeigen",
                                                                      International.getString("Boote auf Fahrt")));
    efaDirekt_showFahrtzielInBooteAufFahrt.setNextFocusableComponent(efaDirekt_logoSelectButton);
    Mnemonics.setButton(this, efaDirekt_mitgliederNamenHinzufuegen, International.getStringWithMnemonic("Mitglieder dürfen Namen zur Mitgliederliste hinzufügen"));
    efaDirekt_mitgliederNamenHinzufuegen.setNextFocusableComponent(efaDirekt_resBooteNichtVerfuegbar);
    Mnemonics.setLabel(this, jLabel64, International.getStringWithMnemonic("Verzeichnis für Nutzer")+": ");
    Mnemonics.setLabel(this, jLabel65, International.getStringWithMnemonic("Pfade für efa")+": ");
    Mnemonics.setLabel(this, jLabel66, International.getStringWithMnemonic("Nutzerdaten")+": ");
    jLabel66.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel66.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel66.setLabelFor(userdatadir);
    userdatadirButton.setNextFocusableComponent(browser);
    Dialog.setPreferredSize(userdatadirButton,50,20);
    userdatadirButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    userdatadirButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        userdatadirButton_actionPerformed(e);
      }
    });
    userdatadir.setNextFocusableComponent(userdatadirButton);
    userdatadir.setText("");
    benachrichtigenBeiPanel.setLayout(gridBagLayout11);
    jLabel67.setText(International.getString("Benachrichtigungen verschicken")+":");
    jLabel68.setText(International.getString("an Admins"));
    jLabel69.setText(International.getString("an Bootswarte"));
    jLabel70.setText(International.getString("bei Fehlern") + " (ERROR): ");
    jLabel71.setText(International.getString("bei Warnungen (WARNING) einmal pro Woche")+":");
    jLabel72.setText(International.getString("bei Bootsstatus-Änderungen")+":");
    Mnemonics.setLabel(this, efaDirekt_newsTextLabel, International.getStringWithMnemonic("News-Text")+": ");
    Mnemonics.setLabel(this, jLabel73, International.getStringWithMnemonic("Sprache")+": ");
    jLabel73.setLabelFor(languages);
    efaDirekt_newsTextLabel.setLabelFor(efaDirekt_newsText);
    efaDirekt_newsText.setNextFocusableComponent(efaDirekt_fontSize);
    Allgemein.add(allgemeinPanel,   International.getString("Allgemein"));
    mainPanel.setLayout(borderLayout2);
    allgemeinPanel.add(jLabel1,                         new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    allgemeinPanel.add(autogenAlias,                          new GridBagConstraints(0, 2, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel2,                         new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(aliasFormat,                          new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel17,                            new GridBagConstraints(0, 15, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel18,                          new GridBagConstraints(0, 16, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel19,                          new GridBagConstraints(0, 17, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel20,                          new GridBagConstraints(0, 18, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel21,                          new GridBagConstraints(0, 19, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel22,                          new GridBagConstraints(0, 20, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel23,                          new GridBagConstraints(0, 21, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel24,                          new GridBagConstraints(0, 22, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f6,                          new GridBagConstraints(1, 16, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f7,                          new GridBagConstraints(1, 17, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f8,                          new GridBagConstraints(1, 18, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f9,                          new GridBagConstraints(1, 19, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f10,                          new GridBagConstraints(1, 20, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f11,                          new GridBagConstraints(1, 21, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(f12,                          new GridBagConstraints(1, 22, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(autoStandardmannsch,                             new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(skipUhrzeit,                        new GridBagConstraints(0, 11, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(skipMannschKm,                     new GridBagConstraints(0, 13, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(skipBemerk,                new GridBagConstraints(0, 14, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(skipZiel,             new GridBagConstraints(0, 12, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(autoObmann,             new GridBagConstraints(0, 7, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(showObmann,            new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel38,           new GridBagConstraints(0, 23, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(lookAndFeel,              new GridBagConstraints(1, 23, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel73,              new GridBagConstraints(0, 24, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(languages,              new GridBagConstraints(1, 24, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 0));
    allgemeinPanel.add(showBerlinOptions,          new GridBagConstraints(0, 25, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel41,         new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(defaultObmann,        new GridBagConstraints(2, 8, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(completePopupEnabled,       new GridBagConstraints(0, 9, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    allgemeinPanel.add(correctMisspelledMitglieder,         new GridBagConstraints(2, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel59,        new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(correctMisspelledBoote,   new GridBagConstraints(3, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(correctMisspelledZiele,   new GridBagConstraints(4, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(aliasGenButton,     new GridBagConstraints(3, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    allgemeinPanel.add(standardFahrtartLabel,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    Allgemein.add(backupPanel,   International.getString("Backup"));
    backupPanel.add(jLabel3,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    backupPanel.add(bakSave,  new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    backupPanel.add(bakMonat,  new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    backupPanel.add(bakKonv,  new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    backupPanel.add(jLabel4,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    backupPanel.add(bakDir,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    backupPanel.add(bakdirButton,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    backupPanel.add(bakTag,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    extProgPanel.add(browserLabel,    new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(browser,    new GridBagConstraints(1, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(browserButton,    new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(jLabel5,     new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(40, 0, 20, 0), 0, 0));
    extProgPanel.add(jLabel25,    new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(acrobat,    new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(saveButton, BorderLayout.SOUTH);
    jScrollPane.getViewport().add(Allgemein, null);
    mainPanel.add(jScrollPane, BorderLayout.CENTER);
    Allgemein.add(druckPanel,  International.getString("Drucken"));
    druckPanel.add(jLabel6,    new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 0), 0, 0));
    druckPanel.add(jLabel7,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(printPageWidth, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel8,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(printPageHeight, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel9,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(printLeftMargin, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel10,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(printTopMargin, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel11,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(printOverlap, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(jLabel33,                new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fahrtBeginnenButton,               new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fahrtBeendenButton,               new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fahrtAbbrechenButton,               new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_nachtragButton,               new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_bootsreservierungenButton,               new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fahrtenbuchAnzeigenButton,               new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_statistikErstellenButton,              new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_nachrichtAnAdminButton,               new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_adminModusButton,                new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_FahrtBeginnenTextButton,            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fahrtBeendenTextButton,          new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_bootsreservierungenAnzeigen,           new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_statistikErstellenAnzeigen,          new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_nachrichtAnAdminAnzeigen,           new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_adminModusAnzeigen,            new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fahrtenbuchAnzeigenAnzeigen,           new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_logoLabel,            new GridBagConstraints(3, 1, 2, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_zielBeiFahrtbeginn,               new GridBagConstraints(0, 8, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(jLabel26,             new GridBagConstraints(0, 4, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(jLabel27,             new GridBagConstraints(2, 5, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(jLabel28,            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_abfahrtMinuten,          new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(jLabel29,           new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_ankunftMinuten,          new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(jLabel30,           new GridBagConstraints(2, 6, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_reservierungenEditErlaubt,           new GridBagConstraints(0, 8, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel31,         new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_MitglEfaExit,           new GridBagConstraints(0, 12, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel32,           new GridBagConstraints(0, 13, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_execOnEfaExit,         new GridBagConstraints(0, 14, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_reservierungenErlaubt,           new GridBagConstraints(0, 6, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel35,         new GridBagConstraints(0, 15, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_exitTime,       new GridBagConstraints(3, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel36,      new GridBagConstraints(4, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_execOnEfaAutoExit,        new GridBagConstraints(0, 17, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    Allgemein.add(efaDirektPane, International.getString("efa im Bootshaus"));
    Allgemein.add(extProgPanel,   International.getString("Pfade"));
    druckPanel.add(jLabel12, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel13,  new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel14,  new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel15,  new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    druckPanel.add(jLabel16,  new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(acrobatButton,   new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(jLabel65,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    extProgPanel.add(jLabel66,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(userdatadir,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    extProgPanel.add(userdatadirButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektPane.add(efaDirektEinstellungenPanel,   International.getString("Allgemeine Einstellungen"));
    efaDirektPane.add(efaDirektFahrtenPanel,   International.getString("Eintrag von Fahrten"));
    efaDirektFahrtenPanel.add(jLabel60,      new GridBagConstraints(0, 0, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen,     new GridBagConstraints(0, 9, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_eintragNichtAenderbarUhrzeit,   new GridBagConstraints(0, 7, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_eintragErlaubeNurMaxRudererzahl,   new GridBagConstraints(0, 2, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_eintragErzwingeObmann,   new GridBagConstraints(0, 3, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(jLabel61,      new GridBagConstraints(0, 10, 6, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_nurBekannteBoote,  new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_nurBekannteRuderer,   new GridBagConstraints(4, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektFahrtenPanel.add(efaDirekt_nurBekannteZiele,  new GridBagConstraints(5, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektPane.add(efaDirektErscheinungsbildPanel,   International.getString("Erscheinungsbild"));

    efaDirektErscheinungsbildPanel.add(efaDirekt_logoSelectButton,           new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_logoDeleteButton,            new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(jLabel34,         new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_maximiertStarten,        new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_fensterNichtVerschiebbar,      new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_immerImVordergrund,       new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_showEingabeInfos,       new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_sortBooteByAnzahl,         new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_showUhr,      new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_newsTextLabel,     new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildWeiterePanel.add(efaDirekt_newsText,       new GridBagConstraints(1, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

    efaDirektErscheinungsbildWeiterePanel.add(efadirekt_colorizedtextfield,    new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(jLabel40,          new GridBagConstraints(3, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(3, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fontStyle,        new GridBagConstraints(5, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(jLabel39,             new GridBagConstraints(3, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 20, 3, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_fontSize,          new GridBagConstraints(5, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_buttonsShowHotkey,             new GridBagConstraints(0, 12, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirektErscheinungsbildWeiterePanel,          new GridBagConstraints(3, 4, 3, 5, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel37,         new GridBagConstraints(0, 16, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(exec1FileSelectButton,      new GridBagConstraints(5, 14, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(exec2FileSelectButton,      new GridBagConstraints(5, 17, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_resBooteNichtVerfuegbar,     new GridBagConstraints(0, 10, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_reservierungenErlaubtZyklisch,    new GridBagConstraints(0, 7, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_wafaRegattaBooteNichtVerfuegbar,     new GridBagConstraints(2, 11, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel62,   new GridBagConstraints(2, 18, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_restartTime,   new GridBagConstraints(3, 18, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(jLabel63,   new GridBagConstraints(4, 18, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    efaDirektEinstellungenPanel.add(efaDirekt_mitgliederNamenHinzufuegen,  new GridBagConstraints(2, 9, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_spezialButton,        new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_spezialButtonAnzeigen,         new GridBagConstraints(2, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_spezielButtonTextButton,             new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(jLabel42,           new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_spezialButtonCmd,         new GridBagConstraints(2, 11, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirekt_showFahrtzielInBooteAufFahrt,   new GridBagConstraints(0, 13, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektPane.add(efaDirektBenachrichtigungPanel,   International.getString("Benachrichtigungen"));
    efaDirektBenachrichtigungPanel.add(jLabel43,         new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel44,          new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel45,          new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel46,          new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel47,          new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(efaDirekt_emailServer,           new GridBagConstraints(1, 2, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(efaDirekt_emailUsername,           new GridBagConstraints(1, 3, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(efaDirekt_emailPassword,           new GridBagConstraints(1, 4, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(efaDirekt_emailAbsender,           new GridBagConstraints(1, 6, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_showSunrise,             new GridBagConstraints(0, 8, 8, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel48,         new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel49,        new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_latComboBox,       new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_latHH,      new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel50,     new GridBagConstraints(3, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_latMM,     new GridBagConstraints(4, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel51,     new GridBagConstraints(5, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_latSS,     new GridBagConstraints(6, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel52,      new GridBagConstraints(7, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_lonComboBox,      new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_lonHH,     new GridBagConstraints(2, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel53,     new GridBagConstraints(3, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_lonMM,     new GridBagConstraints(4, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel54,     new GridBagConstraints(5, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(efaDirekt_lonSS,     new GridBagConstraints(6, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektSunrisePanel.add(jLabel55,      new GridBagConstraints(7, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektErscheinungsbildPanel.add(efaDirektSunrisePanel,             new GridBagConstraints(0, 14, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel56,       new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(efaDirekt_emailAbsenderName,        new GridBagConstraints(1, 5, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel57,      new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(jLabel58,     new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(efaDirekt_emailBetreffPraefix,     new GridBagConstraints(1, 7, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(emailSignaturScrollPane,    new GridBagConstraints(1, 8, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    efaDirektBenachrichtigungPanel.add(benachrichtigenBeiPanel,      new GridBagConstraints(0, 0, 7, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
    benachrichtigenBeiPanel.add(jLabel67,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(jLabel68,          new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 5), 0, 0));
    benachrichtigenBeiPanel.add(jLabel69,          new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(jLabel70,        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(jLabel71,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(jLabel72,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(efaDirekt_bnrError_admin,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(efaDirekt_bnrError_bootswart,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(efaDirekt_bnrWarning_admin,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(efaDirekt_bnrWarning_bootswart,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(efaDirekt_bnrBootsstatus_admin,    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    benachrichtigenBeiPanel.add(efaDirekt_bnrBootsstatus_bootswart,    new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    emailSignaturScrollPane.getViewport().add(efaDirekt_emailSignatur, null);
    allgemeinPanel.add(standardFahrtart,    new GridBagConstraints(2, 5, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jLabel64,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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

  void iniFelder() {
    userdatadir.setText(Daten.efaBaseConfig.efaUserDirectory);

    autogenAlias.setSelected(Daten.efaConfig.autogenAlias);
    aliasFormat.setText(Daten.efaConfig.aliasFormat);
    autoStandardmannsch.setSelected(Daten.efaConfig.autoStandardmannsch);
    for(int i=0; Daten.efaTypes != null && i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++) {
       standardFahrtart.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, i));
    }
    standardFahrtart.setSelectedItem(Daten.efaConfig.standardFahrtart);

    showObmann.setSelected(Daten.efaConfig.showObmann);
    autoObmann.setSelected(Daten.efaConfig.autoObmann);
    defaultObmann.addItem(International.getString("Bugmann"));
    defaultObmann.addItem(International.getString("Schlagmann"));
    try {
      defaultObmann.setSelectedIndex(Daten.efaConfig.defaultObmann-1);
    } catch(Exception e) { EfaUtil.foo(); }
    completePopupEnabled.setSelected(Daten.efaConfig.popupComplete);
    correctMisspelledMitglieder.setSelected(Daten.efaConfig.correctMisspelledMitglieder);
    correctMisspelledBoote.setSelected(Daten.efaConfig.correctMisspelledBoote);
    correctMisspelledZiele.setSelected(Daten.efaConfig.correctMisspelledZiele);
    skipUhrzeit.setSelected(Daten.efaConfig.skipUhrzeit);
    skipZiel.setSelected(Daten.efaConfig.skipZiel);
    skipMannschKm.setSelected(Daten.efaConfig.skipMannschKm);
    skipBemerk.setSelected(Daten.efaConfig.skipBemerk);
    showBerlinOptions.setSelected(Daten.efaConfig.showBerlinOptions);
    for (int k=6; k<=12; k++) {
      String key = "F"+Integer.toString(k);
      if (Daten.efaConfig.keys != null && Daten.efaConfig.keys.get(key) != null)
        switch(k)  {
          case 6: f6.setText((String)Daten.efaConfig.keys.get(key)); break;
          case 7: f7.setText((String)Daten.efaConfig.keys.get(key)); break;
          case 8: f8.setText((String)Daten.efaConfig.keys.get(key)); break;
          case 9: f9.setText((String)Daten.efaConfig.keys.get(key)); break;
          case 10: f10.setText((String)Daten.efaConfig.keys.get(key)); break;
          case 11: f11.setText((String)Daten.efaConfig.keys.get(key)); break;
          case 12: f12.setText((String)Daten.efaConfig.keys.get(key)); break;
        }
    }

    // Look&Feel
    UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
    lookAndFeelArray = new String[info.length+1];
    lookAndFeelArray[0] = EfaConfig.DEFAULT;
    lookAndFeel.addItem(International.getString("Standard"));
    for (int i=0; i<info.length; i++) {
      lookAndFeelArray[i+1] = info[i].getClassName();
      String s = info[i].getClassName();
      int pos = (s != null ? s.lastIndexOf(".") : -1);
      if (pos>0 && pos+1<s.length()) s = s.substring(pos+1,s.length());
      else s = International.getString("Standard");
      lookAndFeel.addItem(s);
    }
    if (Daten.efaConfig.lookAndFeel.equals(EfaConfig.DEFAULT)) {
        lookAndFeel.setSelectedIndex(0);
    } else {
        for (int i=0; i<lookAndFeelArray.length; i++) {
            if (lookAndFeelArray[i].endsWith(Daten.efaConfig.lookAndFeel)) {
                lookAndFeel.setSelectedIndex(i); break;
            }
        }
    }

    // Sprachen
    Vector<String> lang = International.getLanguageBundles();
    languagesArray = new String[lang.size()+1];
    languagesArray[0] = "";
    languages.addItem(International.getString("Default")); // must be in English (in case user's language is not supported)
    int langPreselect = 0;
    for (int i=0; i<lang.size(); i++) {
        Locale loc = new Locale(lang.get(i));
        languagesArray[i+1] = lang.get(i);
        languages.addItem(loc.getDisplayName());
        if (Daten.efaBaseConfig.language != null &&  Daten.efaBaseConfig.language.equals(languagesArray[i+1])) {
            langPreselect = i+1;
        }
    }
    languages.setSelectedIndex(langPreselect);

    efaDirekt_latComboBox.addItem(International.getString("Nord"));
    efaDirekt_latComboBox.addItem(International.getString("Süd"));
    efaDirekt_lonComboBox.addItem(International.getString("West"));
    efaDirekt_lonComboBox.addItem(International.getString("Ost"));

    bakDir.setText(Daten.efaConfig.bakDir);
    bakSave.setSelected(Daten.efaConfig.bakSave);
    bakMonat.setSelected(Daten.efaConfig.bakMonat);
    bakTag.setSelected(Daten.efaConfig.bakTag);
    bakKonv.setSelected(Daten.efaConfig.bakKonv);

    browser.setText(Daten.efaConfig.browser);
    acrobat.setText(Daten.efaConfig.acrobat);

    printPageWidth.setText(Integer.toString(Daten.efaConfig.printPageWidth));
    printPageHeight.setText(Integer.toString(Daten.efaConfig.printPageHeight));
    printLeftMargin.setText(Integer.toString(Daten.efaConfig.printLeftMargin));
    printTopMargin.setText(Integer.toString(Daten.efaConfig.printTopMargin));
    printOverlap.setText(Integer.toString(Daten.efaConfig.printPageOverlap));

    efaDirekt_zielBeiFahrtbeginn.setSelected(Daten.efaConfig.efaDirekt_zielBeiFahrtbeginnPflicht);
    efaDirekt_eintragErlaubeNurMaxRudererzahl.setSelected(Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl);
    efaDirekt_eintragErzwingeObmann.setSelected(Daten.efaConfig.efaDirekt_eintragErzwingeObmann);
    efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.setSelected(Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen);
    efaDirekt_nurBekannteBoote.setSelected(Daten.efaConfig.efaDirekt_eintragNurBekannteBoote);
    efaDirekt_nurBekannteRuderer.setSelected(Daten.efaConfig.efaDirekt_eintragNurBekannteRuderer);
    efaDirekt_nurBekannteZiele.setSelected(Daten.efaConfig.efaDirekt_eintragNurBekannteZiele);
    efaDirekt_eintragNichtAenderbarUhrzeit.setSelected(Daten.efaConfig.efaDirekt_eintragNichtAenderbarUhrzeit);
    efaDirekt_abfahrtMinuten.setText(Integer.toString(Daten.efaConfig.efaDirekt_plusMinutenAbfahrt));
    efaDirekt_ankunftMinuten.setText(Integer.toString(Daten.efaConfig.efaDirekt_minusMinutenAnkunft));
    efaDirekt_reservierungenErlaubt.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren);
    efaDirekt_reservierungenErlaubtZyklisch.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch);
    efaDirekt_reservierungenEditErlaubt.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierungenEditieren);
    efaDirekt_MitglEfaExit.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenEfaBeenden);
    efaDirekt_mitgliederNamenHinzufuegen.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen);
    efaDirekt_resBooteNichtVerfuegbar.setSelected(Daten.efaConfig.efaDirekt_resBooteNichtVerfuegbar);
    efaDirekt_wafaRegattaBooteNichtVerfuegbar.setSelected(Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar);
    efaDirekt_execOnEfaExit.setText(Daten.efaConfig.efaDirekt_execOnEfaExit);
    efaDirekt_exitTime.setText( (Daten.efaConfig.efaDirekt_exitTime.tag < 0 ? "" : EfaUtil.correctTime(Daten.efaConfig.efaDirekt_exitTime.tag+":"+Daten.efaConfig.efaDirekt_exitTime.monat)) );
    efaDirekt_execOnEfaAutoExit.setText(Daten.efaConfig.efaDirekt_execOnEfaAutoExit);
    efaDirekt_restartTime.setText( (Daten.efaConfig.efaDirekt_restartTime.tag < 0 ? "" : EfaUtil.correctTime(Daten.efaConfig.efaDirekt_restartTime.tag+":"+Daten.efaConfig.efaDirekt_restartTime.monat)) );

    efaDirekt_fahrtBeginnenButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeginnenFarbe));
    efaDirekt_fahrtBeendenButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeendenFarbe));
    efaDirekt_fahrtAbbrechenButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtAbbrechenFarbe));
    efaDirekt_nachtragButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachtragFarbe));
    efaDirekt_bootsreservierungenButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butBootsreservierungenFarbe));
    efaDirekt_fahrtenbuchAnzeigenButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenFarbe));
    efaDirekt_statistikErstellenButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butStatistikErstellenFarbe));
    efaDirekt_nachrichtAnAdminButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachrichtAnAdminFarbe));
    efaDirekt_adminModusButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butAdminModusFarbe));
    efaDirekt_spezialButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butSpezialFarbe));

    efaDirekt_fahrtBeginnenButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeginnenText);
    efaDirekt_fahrtBeendenButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeendenText);
    efaDirekt_spezialButton.setText(Daten.efaConfig.efaDirekt_butSpezialText);

    efaDirekt_bootsreservierungenAnzeigen.setSelected(Daten.efaConfig.efaDirekt_butBootsreservierungenAnzeigen);
    efaDirekt_fahrtenbuchAnzeigenAnzeigen.setSelected(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenAnzeigen);
    efaDirekt_statistikErstellenAnzeigen.setSelected(Daten.efaConfig.efaDirekt_butStatistikErstellenAnzeigen);
    efaDirekt_nachrichtAnAdminAnzeigen.setSelected(Daten.efaConfig.efaDirekt_butNachrichtAnAdminAnzeigen);
    efaDirekt_adminModusAnzeigen.setSelected(Daten.efaConfig.efaDirekt_butAdminModusAnzeigen);
    efaDirekt_spezialButtonAnzeigen.setSelected(Daten.efaConfig.efaDirekt_butSpezialAnzeigen);
    efaDirekt_buttonsShowHotkey.setSelected(Daten.efaConfig.efaDirekt_showButtonHotkey);
    efaDirekt_showFahrtzielInBooteAufFahrt.setSelected(Daten.efaConfig.efaDirekt_showZielnameFuerBooteUnterwegs);

    efaDirekt_spezialButtonCmd.setText(Daten.efaConfig.efaDirekt_butSpezialCmd);

    efaDirekt_maximiertStarten.setSelected(Daten.efaConfig.efaDirekt_startMaximized);
    efaDirekt_fensterNichtVerschiebbar.setSelected(Daten.efaConfig.efaDirekt_fensterNichtVerschiebbar);
    efaDirekt_immerImVordergrund.setSelected(Daten.efaConfig.efaDirekt_immerImVordergrund);
    efaDirekt_sortBooteByAnzahl.setSelected(Daten.efaConfig.efaDirekt_sortByAnzahl);
    efaDirekt_showEingabeInfos.setSelected(Daten.efaConfig.efaDirekt_showEingabeInfos);
    efadirekt_colorizedtextfield.setSelected(Daten.efaConfig.efaDirekt_colorizeInputField);
    efaDirekt_showUhr.setSelected(Daten.efaConfig.efaDirekt_showUhr);
    efaDirekt_newsText.setText(Daten.efaConfig.efaDirekt_newsText);
    efaDirekt_fontSize.setText( (Daten.efaConfig.efaDirekt_fontSize == 0 ? "" : Integer.toString(Daten.efaConfig.efaDirekt_fontSize)) );
    switch(Daten.efaConfig.efaDirekt_fontStyle) {
      case -1:         efaDirekt_fontStyle.setSelectedIndex(0); break;
      case Font.PLAIN: efaDirekt_fontStyle.setSelectedIndex(1); break;
      case Font.BOLD:  efaDirekt_fontStyle.setSelectedIndex(2); break;
      default:         efaDirekt_fontStyle.setSelectedIndex(0);
    }

    efaDirekt_bnrError_admin.setSelected(Daten.efaConfig.efaDirekt_bnrError_admin);
    efaDirekt_bnrError_bootswart.setSelected(Daten.efaConfig.efaDirekt_bnrError_bootswart);
    efaDirekt_bnrWarning_admin.setSelected(Daten.efaConfig.efaDirekt_bnrWarning_admin);
    efaDirekt_bnrWarning_bootswart.setSelected(Daten.efaConfig.efaDirekt_bnrWarning_bootswart);
    efaDirekt_bnrBootsstatus_admin.setSelected(Daten.efaConfig.efaDirekt_bnrBootsstatus_admin);
    efaDirekt_bnrBootsstatus_bootswart.setSelected(Daten.efaConfig.efaDirekt_bnrBootsstatus_bootswart);
    efaDirekt_emailServer.setText(Daten.efaConfig.efaDirekt_emailServer);
    efaDirekt_emailUsername.setText(Daten.efaConfig.efaDirekt_emailUsername);
    efaDirekt_emailPassword.setText(Daten.efaConfig.efaDirekt_emailPassword);
    efaDirekt_emailAbsenderName.setText(Daten.efaConfig.efaDirekt_emailAbsenderName);
    efaDirekt_emailAbsender.setText(Daten.efaConfig.efaDirekt_emailAbsender);
    efaDirekt_emailBetreffPraefix.setText(Daten.efaConfig.efaDirekt_emailBetreffPraefix);
    efaDirekt_emailSignatur.setText(EfaUtil.replace(Daten.efaConfig.efaDirekt_emailSignatur,"$$","\n",true));

    efaDirekt_showSunrise.setSelected(Daten.efaConfig.efaDirekt_sunRiseSet_show);
    efaDirekt_latComboBox.setSelectedIndex( (Daten.efaConfig.efaDirekt_sunRiseSet_ll[0] == EfaConfig.LL_NORTH ? 0 : 1) );
    efaDirekt_latHH.setText(Integer.toString(Daten.efaConfig.efaDirekt_sunRiseSet_ll[1]));
    efaDirekt_latMM.setText(Integer.toString(Daten.efaConfig.efaDirekt_sunRiseSet_ll[2]));
    efaDirekt_latSS.setText(Integer.toString(Daten.efaConfig.efaDirekt_sunRiseSet_ll[3]));
    efaDirekt_lonComboBox.setSelectedIndex( (Daten.efaConfig.efaDirekt_sunRiseSet_ll[4] == EfaConfig.LL_WEST ? 0 : 1) );
    efaDirekt_lonHH.setText(Integer.toString(Daten.efaConfig.efaDirekt_sunRiseSet_ll[5]));
    efaDirekt_lonMM.setText(Integer.toString(Daten.efaConfig.efaDirekt_sunRiseSet_ll[6]));
    efaDirekt_lonSS.setText(Integer.toString(Daten.efaConfig.efaDirekt_sunRiseSet_ll[7]));

    setVereinsLogo(Daten.efaConfig.efaDirekt_vereinsLogo);
  }

  public static int parseAlias(String s) {
    int vari = 0;
    int i=0;
    for (i=0; i<s.length(); i++)
      switch (s.charAt(i)) {
        case '{':
          if (vari != 0) {
            parseError = International.getString("Ungültige Variablenbezeichnung")+": "+
                    International.getMessage("Das Zeichen '{character}' ist im Variablennamen nicht erlaubt", "{");
            return i;
          }
          vari++;
          break;
        case '}':
          if (vari != 3) {
            parseError = International.getString("Ungültige Variablenbezeichnung")+": "+
                    International.getString("Variablenname muß die Form '<Feld><Position>' haben");
            return i;
          }
          vari=0;
          break;
        default:
          switch (vari) {
            case 1:
              if (s.charAt(i) != 'V' && s.charAt(i) != 'v' && s.charAt(i) != 'N' && s.charAt(i) != 'n'
                  && s.charAt(i) != 'C' && s.charAt(i) != 'c') {
                parseError = International.getString("Ungültige Variablenbezeichnung")+": "+
                        International.getString("<Feld> muß einen der Werte 'V', 'N', 'C' haben");
                return i;
              }
              vari++;
              break;
            case 2:
              if (s.charAt(i) < 49 || s.charAt(i) > 57) {
                parseError = International.getString("Ungültige Variablenbezeichnung")+": "+
                        International.getString("<Position> muß eine Ziffer sein");
                return i;
              }
              vari++;
              break;
            case 3:
              parseError = International.getString("Ungültige Variablenbezeichnung")+": "+
                      International.getString("Variablenname muß die Form '<Feld><Position>' haben");
              return i;
          }
      }
    if (vari != 0) {
      parseError = International.getString("Unvollständiger Formatstring: Variable nicht korrekt abgeschlossen (Format: {<Feld><Position>})");
      return i;
    }
    return -1;
  }

  boolean checkAliasFormat() {
    aliasFormat.setText(aliasFormat.getText().trim());
    int i;
    if ( ( i = parseAlias(aliasFormat.getText().trim())) != -1) {
      Dialog.infoDialog(International.getString("Syntaxfehler"),parseError);
      aliasFormat.setCaretPosition(i);
      return false;
    }
    return true;
  }

  boolean checkBakDir() {
    String s = bakDir.getText().trim();
    if (s.equals("")) return true;

    if (!new File(s).isDirectory()) {
      Dialog.infoDialog(International.getString("Fehler"),
              LogString.logstring_directoryDoesNotExist(s, International.getString("Verzeichnis")));
      return false;
    }
    return true;
  }

  void aliasFormat_focusLost(FocusEvent e) {
    checkAliasFormat();
  }

  void bakDir_focusLost(FocusEvent e) {
    checkBakDir();
  }

  void browser_focusLost(FocusEvent e) {
    if (!browser.getText().trim().equals("") && !new File(browser.getText().trim()).isFile())
      Dialog.infoDialog(International.getMessage("{program} nicht gefunden",
              International.getString("Browser")),
              International.getMessage("Das Programm '{program}' konnte nicht gefunden werden!",browser.getText().trim()));
  }
  void browserButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,International.getString("Webbrowser"),
            International.getString("Windows-Programme")+" (*.exe)","exe",Daten.efaConfig.browser,false);
    if (dat != null)
      browser.setText(dat);
  }
  void userdatadirButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Nutzerdaten-Verzeichnis")),
            International.getString("Verzeichnisse"),null,Daten.efaBaseConfig.efaUserDirectory,null,null,false,true);
    if (dat != null)
      userdatadir.setText(dat);
  }

  void acrobat_focusLost(FocusEvent e) {
    if (!acrobat.getText().trim().equals("") && !new File(acrobat.getText().trim()).isFile())
      Dialog.infoDialog(International.getMessage("{program} nicht gefunden","Acrobat Reader"),
              International.getMessage("Das Programm '{program}' konnte nicht gefunden werden!",acrobat.getText().trim()));
  }

  void acrobatButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,International.getString("Acrobat Reader"),
            International.getString("Windows-Programme")+" (*.exe)","exe",Daten.efaConfig.acrobat,false);
    if (dat != null)
      acrobat.setText(dat);
  }

  void saveButton_actionPerformed(ActionEvent e) {
    String newUserDir = userdatadir.getText().trim();
    if (newUserDir.length()>0 && !Daten.efaBaseConfig.efaUserDirectory.equals(newUserDir)) {
      if (!Daten.efaBaseConfig.efaCanWrite(newUserDir,true)) {
        Dialog.error(International.getMessage("efa kann in das Nutzerdaten-Verzeichnis '{directory}' nicht schreiben. Bitte wähle ein anderes Verzeichnis aus.",newUserDir));
        userdatadir.requestFocus();
        userdatadir.setCaretPosition(userdatadir.getText().length());
        return;
      } else {
        Daten.efaBaseConfig.efaUserDirectory = newUserDir;
        Daten.efaBaseConfig.writeFile();
        Dialog.infoDialog(International.getString("Neues Nutzerdaten-Verzeichnis"),
                          International.getMessage("Das neue Nutzerdaten-Verzeichnis '{directory}' "+
                          "wird erst nach einem Neustart von efa benutzt. "+
                          "efa kopiert keinerlei bestehende Daten von dem alten in das neue Verzeichnis "+
                          "und löscht auch keinerlei Daten in dem alten Verzeichnis.",newUserDir));
      }
    }

    String newLang = languagesArray[languages.getSelectedIndex()];
    if (Daten.efaBaseConfig.language == null || !Daten.efaBaseConfig.language.equals(newLang)) {
        Daten.efaBaseConfig.language = newLang;
        Daten.efaBaseConfig.writeFile();
        Daten.efaTypes.setToLanguage(newLang);
        Dialog.infoDialog(International.getString("Sprache"),
                International.getString("Die geänderten Spracheinstellungen werden erst nach einem Neustart von efa wirksam."));
    }

    if (!checkAliasFormat()) {
      aliasFormat.requestFocus();
      aliasFormat.setCaretPosition(aliasFormat.getText().length());
      return;
    }
    if (!checkBakDir()) {
      bakDir.requestFocus();
      bakDir.setCaretPosition(bakDir.getText().length());
      return;
    }

    Daten.efaConfig.autogenAlias = autogenAlias.isSelected();
    Daten.efaConfig.aliasFormat = aliasFormat.getText().trim();
    Daten.efaConfig.autoStandardmannsch = autoStandardmannsch.isSelected();
    Daten.efaConfig.standardFahrtart = (String)standardFahrtart.getSelectedItem();
    Daten.efaConfig.showObmann = showObmann.isSelected();
    Daten.efaConfig.autoObmann = autoObmann.isSelected();
    Daten.efaConfig.defaultObmann = defaultObmann.getSelectedIndex()+1;
    Daten.efaConfig.popupComplete = completePopupEnabled.isSelected();
    Daten.efaConfig.correctMisspelledMitglieder = correctMisspelledMitglieder.isSelected();
    Daten.efaConfig.correctMisspelledBoote = correctMisspelledBoote.isSelected();
    Daten.efaConfig.correctMisspelledZiele = correctMisspelledZiele.isSelected();
    Daten.efaConfig.skipUhrzeit = skipUhrzeit.isSelected();
    Daten.efaConfig.skipZiel = skipZiel.isSelected();
    Daten.efaConfig.skipMannschKm = skipMannschKm.isSelected();
    Daten.efaConfig.skipBemerk = skipBemerk.isSelected();
    Daten.efaConfig.keys.put("F6",f6.getText());
    Daten.efaConfig.keys.put("F7",f7.getText());
    Daten.efaConfig.keys.put("F8",f8.getText());
    Daten.efaConfig.keys.put("F9",f9.getText());
    Daten.efaConfig.keys.put("F10",f10.getText());
    Daten.efaConfig.keys.put("F11",f11.getText());
    Daten.efaConfig.keys.put("F12",f12.getText());
    Daten.efaConfig.lookAndFeel = lookAndFeelArray[(lookAndFeel.getSelectedIndex()>=0 ? lookAndFeel.getSelectedIndex() : 0)];
    Daten.efaConfig.showBerlinOptions = showBerlinOptions.isSelected();
    Daten.efaConfig.bakDir = bakDir.getText().trim();
    Daten.efaConfig.bakSave = bakSave.isSelected();
    Daten.efaConfig.bakMonat = bakMonat.isSelected();
    Daten.efaConfig.bakTag = bakTag.isSelected();
    Daten.efaConfig.bakKonv = bakKonv.isSelected();
    Daten.efaConfig.browser = browser.getText().trim();
    Daten.efaConfig.acrobat = acrobat.getText().trim();
    Daten.efaConfig.printPageWidth   = EfaUtil.string2date(printPageWidth.getText(),0,0,0).tag;
    Daten.efaConfig.printPageHeight  = EfaUtil.string2date(printPageHeight.getText(),0,0,0).tag;
    Daten.efaConfig.printLeftMargin  = EfaUtil.string2date(printLeftMargin.getText(),0,0,0).tag;
    Daten.efaConfig.printTopMargin   = EfaUtil.string2date(printTopMargin.getText(),0,0,0).tag;
    Daten.efaConfig.printPageOverlap = EfaUtil.string2date(printOverlap.getText(),0,0,0).tag;

    Daten.efaConfig.efaDirekt_zielBeiFahrtbeginnPflicht = efaDirekt_zielBeiFahrtbeginn.isSelected();
    Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl = efaDirekt_eintragErlaubeNurMaxRudererzahl.isSelected();
    Daten.efaConfig.efaDirekt_eintragErzwingeObmann = efaDirekt_eintragErzwingeObmann.isSelected();
    Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen = efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.isSelected();
    Daten.efaConfig.efaDirekt_eintragNichtAenderbarUhrzeit = efaDirekt_eintragNichtAenderbarUhrzeit.isSelected();
    Daten.efaConfig.efaDirekt_eintragNurBekannteBoote = efaDirekt_nurBekannteBoote.isSelected();
    Daten.efaConfig.efaDirekt_eintragNurBekannteRuderer = efaDirekt_nurBekannteRuderer.isSelected();
    Daten.efaConfig.efaDirekt_eintragNurBekannteZiele = efaDirekt_nurBekannteZiele.isSelected();

    Daten.efaConfig.efaDirekt_plusMinutenAbfahrt = EfaUtil.string2date(efaDirekt_abfahrtMinuten.getText(),0,0,0).tag;
    Daten.efaConfig.efaDirekt_minusMinutenAnkunft = EfaUtil.string2date(efaDirekt_ankunftMinuten.getText(),0,0,0).tag;
    Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren = efaDirekt_reservierungenErlaubt.isSelected();
    Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch = efaDirekt_reservierungenErlaubtZyklisch.isSelected();
    Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierungenEditieren = efaDirekt_reservierungenEditErlaubt.isSelected();
    Daten.efaConfig.efaDirekt_mitgliederDuerfenEfaBeenden = efaDirekt_MitglEfaExit.isSelected();
    Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen = efaDirekt_mitgliederNamenHinzufuegen.isSelected();
    Daten.efaConfig.efaDirekt_resBooteNichtVerfuegbar = efaDirekt_resBooteNichtVerfuegbar.isSelected();
    Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar = efaDirekt_wafaRegattaBooteNichtVerfuegbar.isSelected();

    Daten.efaConfig.efaDirekt_execOnEfaExit = efaDirekt_execOnEfaExit.getText().trim();
    Daten.efaConfig.efaDirekt_exitTime = EfaUtil.string2date(efaDirekt_exitTime.getText(),-1,-1,-1);
    Daten.efaConfig.efaDirekt_execOnEfaAutoExit = efaDirekt_execOnEfaAutoExit.getText().trim();
    Daten.efaConfig.efaDirekt_restartTime = EfaUtil.string2date(efaDirekt_restartTime.getText(),-1,-1,-1);

    Daten.efaConfig.efaDirekt_butFahrtBeginnenFarbe = EfaUtil.getColor(efaDirekt_fahrtBeginnenButton.getBackground());
    Daten.efaConfig.efaDirekt_butFahrtBeendenFarbe = EfaUtil.getColor(efaDirekt_fahrtBeendenButton.getBackground());
    Daten.efaConfig.efaDirekt_butFahrtAbbrechenFarbe = EfaUtil.getColor(efaDirekt_fahrtAbbrechenButton.getBackground());
    Daten.efaConfig.efaDirekt_butNachtragFarbe = EfaUtil.getColor(efaDirekt_nachtragButton.getBackground());
    Daten.efaConfig.efaDirekt_butBootsreservierungenFarbe = EfaUtil.getColor(efaDirekt_bootsreservierungenButton.getBackground());
    Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenFarbe = EfaUtil.getColor(efaDirekt_fahrtenbuchAnzeigenButton.getBackground());
    Daten.efaConfig.efaDirekt_butStatistikErstellenFarbe = EfaUtil.getColor(efaDirekt_statistikErstellenButton.getBackground());
    Daten.efaConfig.efaDirekt_butNachrichtAnAdminFarbe = EfaUtil.getColor(efaDirekt_nachrichtAnAdminButton.getBackground());
    Daten.efaConfig.efaDirekt_butAdminModusFarbe = EfaUtil.getColor(efaDirekt_adminModusButton.getBackground());
    Daten.efaConfig.efaDirekt_butSpezialFarbe = EfaUtil.getColor(efaDirekt_spezialButton.getBackground());

    Daten.efaConfig.efaDirekt_butFahrtBeginnenText = efaDirekt_fahrtBeginnenButton.getText();
    Daten.efaConfig.efaDirekt_butFahrtBeendenText = efaDirekt_fahrtBeendenButton.getText();
    Daten.efaConfig.efaDirekt_butSpezialText = efaDirekt_spezialButton.getText();

    Daten.efaConfig.efaDirekt_butBootsreservierungenAnzeigen = efaDirekt_bootsreservierungenAnzeigen.isSelected();
    Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenAnzeigen = efaDirekt_fahrtenbuchAnzeigenAnzeigen.isSelected();
    Daten.efaConfig.efaDirekt_butStatistikErstellenAnzeigen = efaDirekt_statistikErstellenAnzeigen.isSelected();
    Daten.efaConfig.efaDirekt_butNachrichtAnAdminAnzeigen = efaDirekt_nachrichtAnAdminAnzeigen.isSelected();
    Daten.efaConfig.efaDirekt_butAdminModusAnzeigen = efaDirekt_adminModusAnzeigen.isSelected();
    Daten.efaConfig.efaDirekt_butSpezialAnzeigen = efaDirekt_spezialButtonAnzeigen.isSelected();
    Daten.efaConfig.efaDirekt_showButtonHotkey = efaDirekt_buttonsShowHotkey.isSelected();
    Daten.efaConfig.efaDirekt_showZielnameFuerBooteUnterwegs = efaDirekt_showFahrtzielInBooteAufFahrt.isSelected();

    Daten.efaConfig.efaDirekt_butSpezialCmd = efaDirekt_spezialButtonCmd.getText();

    Daten.efaConfig.efaDirekt_vereinsLogo = (efaDirekt_vereinsLogo == null ? "" : efaDirekt_vereinsLogo);
    Daten.efaConfig.efaDirekt_sortByAnzahl = efaDirekt_sortBooteByAnzahl.isSelected();

    Daten.efaConfig.efaDirekt_startMaximized = efaDirekt_maximiertStarten.isSelected();
    Daten.efaConfig.efaDirekt_fensterNichtVerschiebbar = efaDirekt_fensterNichtVerschiebbar.isSelected();
    Daten.efaConfig.efaDirekt_immerImVordergrund = efaDirekt_immerImVordergrund.isSelected();
    Daten.efaConfig.efaDirekt_showEingabeInfos = efaDirekt_showEingabeInfos.isSelected();
    Daten.efaConfig.efaDirekt_colorizeInputField = efadirekt_colorizedtextfield.isSelected();
    Daten.efaConfig.efaDirekt_showUhr = efaDirekt_showUhr.isSelected();
    Daten.efaConfig.efaDirekt_newsText = efaDirekt_newsText.getText().trim();

    Daten.efaConfig.efaDirekt_fontSize = EfaUtil.string2date(efaDirekt_fontSize.getText(),0,0,0).tag;
    switch(efaDirekt_fontStyle.getSelectedIndex()) {
      case 0:  Daten.efaConfig.efaDirekt_fontStyle = -1; break;
      case 1:  Daten.efaConfig.efaDirekt_fontStyle = Font.PLAIN; break;
      case 2:  Daten.efaConfig.efaDirekt_fontStyle = Font.BOLD; break;
      default: Daten.efaConfig.efaDirekt_fontStyle = -1; break;
    }

    Daten.efaConfig.efaDirekt_bnrError_admin = efaDirekt_bnrError_admin.isSelected();
    Daten.efaConfig.efaDirekt_bnrError_bootswart = efaDirekt_bnrError_bootswart.isSelected();
    Daten.efaConfig.efaDirekt_bnrWarning_admin = efaDirekt_bnrWarning_admin.isSelected();
    Daten.efaConfig.efaDirekt_bnrWarning_bootswart = efaDirekt_bnrWarning_bootswart.isSelected();
    Daten.efaConfig.efaDirekt_bnrBootsstatus_admin = efaDirekt_bnrBootsstatus_admin.isSelected();
    Daten.efaConfig.efaDirekt_bnrBootsstatus_bootswart = efaDirekt_bnrBootsstatus_bootswart.isSelected();
    Daten.efaConfig.efaDirekt_emailServer = efaDirekt_emailServer.getText().trim();
    Daten.efaConfig.efaDirekt_emailUsername = efaDirekt_emailUsername.getText().trim();
    Daten.efaConfig.efaDirekt_emailPassword = efaDirekt_emailPassword.getText().trim();
    Daten.efaConfig.efaDirekt_emailAbsenderName = efaDirekt_emailAbsenderName.getText().trim();
    Daten.efaConfig.efaDirekt_emailAbsender = efaDirekt_emailAbsender.getText().trim();
    Daten.efaConfig.efaDirekt_emailBetreffPraefix = efaDirekt_emailBetreffPraefix.getText().trim();
    Daten.efaConfig.efaDirekt_emailSignatur = EfaUtil.replace(efaDirekt_emailSignatur.getText(),"\n","$$",true);

    Daten.efaConfig.efaDirekt_sunRiseSet_show = efaDirekt_showSunrise.isSelected();
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[0] = (efaDirekt_latComboBox.getSelectedIndex() == 0 ? EfaConfig.LL_NORTH : EfaConfig.LL_SOUTH);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[1] = EfaUtil.string2int(efaDirekt_latHH.getText().trim(),0);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[2] = EfaUtil.string2int(efaDirekt_latMM.getText().trim(),0);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[3] = EfaUtil.string2int(efaDirekt_latSS.getText().trim(),0);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[4] = (efaDirekt_lonComboBox.getSelectedIndex() == 0 ? EfaConfig.LL_WEST : EfaConfig.LL_EAST);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[5] = EfaUtil.string2int(efaDirekt_lonHH.getText().trim(),0);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[6] = EfaUtil.string2int(efaDirekt_lonMM.getText().trim(),0);
    Daten.efaConfig.efaDirekt_sunRiseSet_ll[7] = EfaUtil.string2int(efaDirekt_lonSS.getText().trim(),0);

    if (!Daten.efaConfig.writeFile()) {

      Dialog.infoDialog(International.getString("Fehler"),
              International.getMessage("Die Einstellungen konnten nicht gespeichert werden, da die Datei '{filename}' nicht geschrieben werden konnte.",
              Daten.efaConfig.dat));
    }

    Daten.iniBackup();

    if (this.parent != null) {
      parent.obmannLabel.setVisible(showObmann.isSelected());
      parent.obmann.setVisible(showObmann.isSelected());
    }

    // geänderte Eigenschaften, sofern möglich, aktivieren
    if ((Daten.efaConfig.efaDirekt_fontSize != 0 || Daten.efaConfig.efaDirekt_fontStyle != -1) &&
        Daten.applID == Daten.APPL_EFADIREKT) Dialog.setGlobalFontSize(Daten.efaConfig.efaDirekt_fontSize,Daten.efaConfig.efaDirekt_fontStyle);

    cancel();
  }

  void correctIntValue(FocusEvent e) {
    String s = ((JTextField)e.getSource()).getText().trim();
    ((JTextField)e.getSource()).setText(Integer.toString(EfaUtil.string2date(s,0,0,0).tag));
  }

  void bakdirButton_actionPerformed(ActionEvent e) {
    String dir = bakDir.getText().trim();
    if (dir.length() == 0 || !new File(dir).isDirectory()) dir = Daten.efaMainDirectory;
    dir = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Backup-Verzeichnis")),
            null,null,dir,null,
            International.getString("auswählen"),false,true);
    if (dir != null)
      bakDir.setText(dir);
  }

  void colorButton_actionPerformed(ActionEvent e) {
    Color color = JColorChooser.showDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Farbe")),
            ((JButton)e.getSource()).getBackground());
    if (color != null) ((JButton)e.getSource()).setBackground(color);
  }

  void efaDirekt_FahrtBeginnenTextButton_actionPerformed(ActionEvent e) {
    String s = Dialog.inputDialog(efaDirekt_fahrtBeginnenButton.getText().trim(),
            International.getString("Bitte Text für Button eingeben")+":");
    if (s != null) efaDirekt_fahrtBeginnenButton.setText(s);
  }

  void efaDirekt_fahrtBeendenTextButton_actionPerformed(ActionEvent e) {
    String s = Dialog.inputDialog(efaDirekt_fahrtBeendenButton.getText().trim(),
            International.getString("Bitte Text für Button eingeben")+":");
    if (s != null) efaDirekt_fahrtBeendenButton.setText(s);
  }

  void efaDirekt_spezielButtonTextButton_actionPerformed(ActionEvent e) {
    String s = Dialog.inputDialog(efaDirekt_spezialButton.getText().trim(),
            International.getString("Bitte Text für Button eingeben")+":");
    if (s != null) efaDirekt_spezialButton.setText(s);
  }

  void setVereinsLogo(String datei) {
    efaDirekt_vereinsLogo = null;
    if (datei == null || datei.length()==0) {
      this.efaDirekt_logoLabel.setIcon(null);
      return;
    }
    try {
      this.efaDirekt_logoLabel.setIcon(new ImageIcon(datei));
      efaDirekt_vereinsLogo = datei;
    } catch(Exception ee) { EfaUtil.foo(); }
  }

  void efaDirekt_logoSelectButton_actionPerformed(ActionEvent e) {
    String dir = Daten.efaMainDirectory;
    if (efaDirekt_vereinsLogo != null && efaDirekt_vereinsLogo.length()>0) EfaUtil.getPathOfFile(efaDirekt_vereinsLogo);
    String datei = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Vereinslogo")),
            International.getString("Bild-Datei")+" (*.gif, *.jpg)","gif|jpg",dir,efaDirekt_vereinsLogo,
            International.getString("auswählen"),false,false);
    if (datei == null) return;
    if (!EfaUtil.canOpenFile(datei)) {
        Dialog.error(LogString.logstring_fileOpenFailed(datei, International.getString("Datei")));
      return;
    }
    setVereinsLogo(datei);
  }

  void efaDirekt_logoDeleteButton_actionPerformed(ActionEvent e) {
    if (efaDirekt_vereinsLogo == null) return;
    if (Dialog.yesNoDialog(International.getString("Logo wirklich entfernen"),
            International.getString("Soll das Logo wirklich entfernt werden?")) == Dialog.YES) {
      setVereinsLogo(null);
    }
  }


  void efaDirekt_exitTime_focusLost(FocusEvent e) {
    String s = efaDirekt_exitTime.getText().trim();
    if (s.length() == 0) efaDirekt_exitTime.setText("");
    else efaDirekt_exitTime.setText(EfaUtil.correctTime(s));
  }

  void efaDirekt_restartTime_focusLost(FocusEvent e) {
    String s = efaDirekt_restartTime.getText().trim();
    if (s.length() == 0) efaDirekt_restartTime.setText("");
    else efaDirekt_restartTime.setText(EfaUtil.correctTime(s));
  }

  void exec1FileSelectButton_actionPerformed(ActionEvent e) {
    String dat = efaDirekt_execOnEfaExit.getText().trim();
    if (dat.length() == 0 || !new File(dat).isFile()) dat = Daten.efaMainDirectory;
    dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Programm")),
            null,null,dat,null,
            International.getString("auswählen"),false,false);
    if (dat != null) efaDirekt_execOnEfaExit.setText(dat);
  }

  void exec2FileSelectButton_actionPerformed(ActionEvent e) {
    String dat = efaDirekt_execOnEfaAutoExit.getText().trim();
    if (dat.length() == 0 || !new File(dat).isFile()) dat = Daten.efaMainDirectory;
    dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Programm")),
            null,null,dat,null,
            International.getString("auswählen"),false,false);
    if (dat != null) efaDirekt_execOnEfaAutoExit.setText(dat);
  }

  void efaDirekt_immerImVordergrund_actionPerformed(ActionEvent e) {
    if (efaDirekt_immerImVordergrund.isSelected() && Daten.javaVersion.compareTo("1.5")<0)
      Dialog.infoDialog(International.getString("Diese Funktion wird erst ab Java Version {version} unterstützt."),"1.5");
  }

  void efaDirekt_fensterNichtVerschiebbar_actionPerformed(ActionEvent e) {
    if (efaDirekt_fensterNichtVerschiebbar.isSelected() && Daten.javaVersion.compareTo("1.4")<0)
      Dialog.infoDialog(International.getString("Diese Funktion wird erst ab Java Version {version} unterstützt."),"1.4");
  }

  void efaDirekt_fontSize_focusLost(FocusEvent e) {
    int size = EfaUtil.string2date(efaDirekt_fontSize.getText(),0,0,0).tag;
    if (size<6 && size != 0) size = 6;
    if (size>32) size = 32;
    if (size == 0) efaDirekt_fontSize.setText("");
    else efaDirekt_fontSize.setText(Integer.toString(size));
    if (Dialog.screenSize != null) {
      if ((Dialog.screenSize.getWidth()<=800 || Dialog.screenSize.getHeight()<=600) && size>14) {
        Dialog.infoDialog(International.getString("Warnung"),
                          International.getMessage("Für eine Auflösung von {dimension} wird eine maximale Schriftgröße "+
                          "von {size} empfohlen. Eine größere Schriftgröße kann dazu führen, "+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.","800x600",14));
        return;
      }
      if ((Dialog.screenSize.getWidth()<=1024 || Dialog.screenSize.getHeight()<=768) && size>16) {
        Dialog.infoDialog(International.getString("Warnung"),
                          International.getMessage("Für eine Auflösung von {dimension} wird eine maximale Schriftgröße "+
                          "von {size} empfohlen. Eine größere Schriftgröße kann dazu führen, "+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.","1024x768",16));
        return;
      }
      if ((Dialog.screenSize.getWidth()<=1280 || Dialog.screenSize.getHeight()<=1024) && size>18) {
        Dialog.infoDialog(International.getString("Warnung"),
                          International.getMessage("Für eine Auflösung von {dimension} wird eine maximale Schriftgröße "+
                          "von {size} empfohlen. Eine größere Schriftgröße kann dazu führen, "+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.","1280x1024",18));
        return;
      }
      if ((Dialog.screenSize.getWidth()>1280 && Dialog.screenSize.getHeight()>1024) && size>20) {
        Dialog.infoDialog(International.getString("Warnung"),
                          International.getMessage("Auch für hohe Auflösungen wird eine maximale Schriftgröße "+
                          "von {size} empfohlen. Eine größere Schriftgröße kann dazu führen, "+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.",20));
        return;
      }
    }
  }

  void efaDirekt_buttonsShowHotkey_actionPerformed(ActionEvent e) {
    if (efaDirekt_buttonsShowHotkey.isSelected() && Daten.javaVersion.compareTo("1.4")<0)
      Dialog.infoDialog(International.getString("Diese Funktion wird erst ab Java Version {version} unterstützt."),"1.4");
  }

  void efaDirekt_showSunrise_actionPerformed(ActionEvent e) {
    if (efaDirekt_showSunrise.isSelected() && !de.nmichael.efa.direkt.SunRiseSet.sunrisePluginInstalled()) {
      DownloadFrame.getPlugin("efa",Daten.PLUGIN_JSUNTIMES_NAME,Daten.PLUGIN_JSUNTIMES_FILE,Daten.PLUGIN_JSUNTIMES_HTML,"NoClassDefFoundError",null,false);
    }
  }

  void aliasGenButton_actionPerformed(ActionEvent e) {
    if (!checkAliasFormat()) return;
    String fmt = this.aliasFormat.getText().trim();
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten() == null ||
        Daten.fahrtenbuch.getDaten().mitglieder == null) {
      Dialog.error(International.getString("Generieren der Eingabekürzel nicht möglich: Es ist zur Zeit keine Mitgliederliste geöffnet."));
      return;
    }
    Mitglieder m = Daten.fahrtenbuch.getDaten().mitglieder;
    if (Dialog.yesNoCancelDialog(International.getString("Eingabekürzel neu generieren"),
                                 International.getMessage("Sollen alle Eingabekürzel für die Mitgliederliste {list} "+
                                 "neu generiert und bestehende Eingabekürzel dabei "+
                                 "ersetzt werden?",m.getFileName())) != Dialog.YES) {
      return;
    }
    DatenFelder d = m.getCompleteFirst();
    String alias;
    int c=0;
    while (d != null) {
      alias = EfaUtil.genAlias(fmt,d.get(Mitglieder.VORNAME),d.get(Mitglieder.NACHNAME),d.get(Mitglieder.VEREIN));
      if (alias != null) {
        d.set(Mitglieder.ALIAS,alias);
        c++;
      }
      d = m.getCompleteNext();
    }
    Dialog.infoDialog(International.getMessage("{count} Eingabekürzel neu generiert.",c));
    if (c > 0) Daten.fahrtenbuch.getDaten().mitglieder.setChanged();
  }


}
