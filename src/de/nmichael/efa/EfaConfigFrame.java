package de.nmichael.efa;

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

public class EfaConfigFrame extends JDialog implements ActionListener {
  EfaFrame parent;


  // parseAlias(String) parst den String und liefert -1 bei Erfolg, sonst die Position des Zeichens, das den
  // Fehler verursachte; eine Fehlermeldung steht in diesem Fall in dem String parseError
  public static String parseError="";

  String efaDirekt_vereinsLogo; // Dateiname für Vereins-Logo für efaDirekt

  String[] lookAndFeelArray;

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

    this.setTitle("Allgemeine Einstellungen");
    this.setSize(new Dimension(723, 707));
    allgemeinPanel.setLayout(gridBagLayout1);
    jLabel1.setText("Allgemeine Einstellungen");
    autogenAlias.setText("Eingabe-Kürzel automatisch beim Anlegen neuer Mitglieder generieren");
    autogenAlias.setNextFocusableComponent(aliasFormat);
    autogenAlias.setToolTipText("beim Hinzufügen neuer Mitglieder automatisch Eingabe-Kürzel generieren");
    autogenAlias.setActionCommand("Eingabe-Kürzel automatisch beim Anlegen neuer Mitglieder generieren");
    autogenAlias.setMnemonic('A');
    jLabel2.setToolTipText("nach welchem Schema die Eingabe-Kürzel generiert werden sollen...");
    jLabel2.setDisplayedMnemonic('F');
    jLabel2.setLabelFor(aliasFormat);
    jLabel2.setText("Format der Eingabe-Kürzel: ");
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
    jLabel3.setText("Sicherungskopien erstellen");
    backupPanel.setBorder(BorderFactory.createEtchedBorder());
    backupPanel.setNextFocusableComponent(bakDir);
    backupPanel.setLayout(gridBagLayout3);
    bakSave.setText("bei jedem Speichern");
    bakSave.setNextFocusableComponent(bakMonat);
    bakSave.setToolTipText("Sicherungskopie bei jedem Speichern einer Datei anlegen");
    bakSave.setMnemonic('J');
    bakMonat.setText("jeden Monat");
    bakMonat.setNextFocusableComponent(bakTag);
    bakMonat.setToolTipText("Sicherungskopie jeden Monat anlegen");
    bakMonat.setMnemonic('M');
    bakKonv.setText("beim Konvertieren");
    bakKonv.setNextFocusableComponent(saveButton);
    bakKonv.setToolTipText("Sicherungskopie vor dem Konvertieren in ein neues Format anlegen");
    bakKonv.setMnemonic('K');
    saveButton.setNextFocusableComponent(Allgemein);
    saveButton.setMnemonic('S');
    saveButton.setText("Speichern");
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveButton_actionPerformed(e);
      }
    });
    jLabel4.setToolTipText("");
    jLabel4.setDisplayedMnemonic('V');
    jLabel4.setLabelFor(bakDir);
    jLabel4.setText("Backup-Verzeichnis: ");
    bakDir.setNextFocusableComponent(bakSave);
    Dialog.setPreferredSize(bakDir,340,19);
    bakDir.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        bakDir_focusLost(e);
      }
    });
    browserLabel.setDisplayedMnemonic('W');
    browserLabel.setLabelFor(browser);
    browserLabel.setText("Webbrowser: ");
    browser.setNextFocusableComponent(browserButton);
    Dialog.setPreferredSize(browser,400,19);
    browser.setToolTipText("Pfad zum Webbrowser zum Anzeigen und Drucken der Statistiken");
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
    jLabel5.setText("Pfade zu externen Programmen");
    druckPanel.setLayout(gridBagLayout4);
    jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel6.setText("Seitenlayout für Druck");
    jLabel7.setDisplayedMnemonic('B');
    jLabel7.setLabelFor(printPageWidth);
    jLabel7.setText("Seitenbreite: ");
    printPageWidth.setNextFocusableComponent(printPageHeight);
    Dialog.setPreferredSize(printPageWidth,80,19);
    printPageWidth.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    jLabel8.setDisplayedMnemonic('H');
    jLabel8.setLabelFor(printPageHeight);
    jLabel8.setText("Seitenhöhe: ");
    printPageHeight.setNextFocusableComponent(printLeftMargin);
    Dialog.setPreferredSize(printPageHeight,80,19);
    printPageHeight.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        correctIntValue(e);
      }
    });
    jLabel9.setDisplayedMnemonic('L');
    jLabel9.setLabelFor(printLeftMargin);
    jLabel9.setText("linker und rechter Rand: ");
    jLabel10.setDisplayedMnemonic('O');
    jLabel10.setLabelFor(printTopMargin);
    jLabel10.setText("oberer und unterer Rand: ");
    jLabel11.setDisplayedMnemonic('E');
    jLabel11.setLabelFor(printOverlap);
    jLabel11.setText("Seitenüberlappung: ");
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
    jLabel17.setText("Tastenbelegungen für Bemerkungs-Feld:");
    jLabel18.setDisplayedMnemonic('6');
    jLabel18.setLabelFor(f6);
    jLabel18.setText("F6: ");
    jLabel19.setDisplayedMnemonic('7');
    jLabel19.setLabelFor(f7);
    jLabel19.setText("F7: ");
    jLabel20.setDisplayedMnemonic('8');
    jLabel20.setLabelFor(f8);
    jLabel20.setText("F8: ");
    jLabel21.setDisplayedMnemonic('9');
    jLabel21.setLabelFor(f9);
    jLabel21.setText("F9: ");
    jLabel22.setDisplayedMnemonic('0');
    jLabel22.setLabelFor(f10);
    jLabel22.setText("F10: ");
    jLabel23.setDisplayedMnemonic('1');
    jLabel23.setLabelFor(f11);
    jLabel23.setText("F11: ");
    jLabel24.setDisplayedMnemonic('2');
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
    jLabel25.setDisplayedMnemonic('A');
    jLabel25.setText("Acrobat Reader: ");
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
    autoStandardmannsch.setNextFocusableComponent(standardFahrtart);
    autoStandardmannsch.setToolTipText("wenn eine Standardmannschaft konfiguriert ist, diese bei der Eingabe " +
    "automatisch immer eintragen");
    autoStandardmannsch.setMnemonic('T');
    autoStandardmannsch.setText("Standardmannschaft automatisch eintragen");
    skipUhrzeit.setNextFocusableComponent(skipZiel);
    skipUhrzeit.setToolTipText("Bei der Eingabe die Felder \'Abfahrt\' und \'Ankunft\' überspringen");
    skipUhrzeit.setMnemonic('U');
    skipUhrzeit.setText("Eingabefelder \'Uhrzeit\' überspringen");
    skipMannschKm.setNextFocusableComponent(skipBemerk);
    skipMannschKm.setToolTipText("Bei der Eingabe das Feld \'Mannschaftskilometer\' überspringen");
    skipMannschKm.setMnemonic('M');
    skipMannschKm.setText("Eingabefeld \'Mannsch.-Km\' überspringen");
    skipBemerk.setNextFocusableComponent(f6);
    skipBemerk.setToolTipText("Bei der Eingabe das Feld \'Bemerkungen\' überspringen");
    skipBemerk.setMnemonic('B');
    skipBemerk.setText("Eingabefeld \'Bemerkungen\' überspringen");
    Dialog.setPreferredSize(bakdirButton,50,20);
    bakdirButton.setToolTipText("Backup-Verzeichnis auswählen");
    bakdirButton.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    bakdirButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bakdirButton_actionPerformed(e);
      }
    });
    bakTag.setNextFocusableComponent(bakKonv);
    bakTag.setToolTipText("Sicherungskopie jeden Tag anlegen");
    bakTag.setMnemonic('T');
    bakTag.setText("jeden Tag");
    efaDirektPanel.setLayout(borderLayout3);
    efaDirektEinstellungenPanel.setLayout(gridBagLayout5);
    efaDirekt_zielBeiFahrtbeginn.setNextFocusableComponent(efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen);
    efaDirekt_zielBeiFahrtbeginn.setToolTipText("Beim beginnen einer Fahrt muß bereits ein Fahrtziel angegeben werden");
    efaDirekt_zielBeiFahrtbeginn.setMnemonic('Z');
    efaDirekt_zielBeiFahrtbeginn.setText("Ziel muß bereits bei Fahrtbeginn angegeben werden");
    jLabel26.setText("Berechnung der vorgeschlagenen Uhrzeiten bei Abfahrt und Ankunft:");
    jLabel27.setText(" Minuten zu aktueller Zeit addieren");
    jLabel28.setDisplayedMnemonic('F');
    jLabel28.setLabelFor(efaDirekt_abfahrtMinuten);
    jLabel28.setText("Abfahrt: ");
    jLabel29.setDisplayedMnemonic('N');
    jLabel29.setLabelFor(efaDirekt_ankunftMinuten);
    jLabel29.setText("Ankunft: ");
    jLabel30.setText(" Minuten von aktueller Zeit abziehen");
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
    efaDirekt_reservierungenEditErlaubt.setNextFocusableComponent(efaDirekt_mitgliederNamenHinzufuegen);
    efaDirekt_reservierungenEditErlaubt.setMnemonic('D');
    efaDirekt_reservierungenEditErlaubt.setText("Mitglieder dürfen Bootsreservierungen verändern und löschen");
    jLabel31.setText("Einstellungen für efa beim Einsatz im Bootshaus");
    efaDirekt_MitglEfaExit.setNextFocusableComponent(efaDirekt_execOnEfaExit);
    efaDirekt_MitglEfaExit.setMnemonic('B');
    efaDirekt_MitglEfaExit.setText("Mitglieder dürfen efa beenden");
    jLabel32.setDisplayedMnemonic('O');
    jLabel32.setLabelFor(efaDirekt_execOnEfaExit);
    jLabel32.setText("Folgendes Kommando beim Beenden von efa durch Mitglieder ausführen:");
    efaDirekt_execOnEfaExit.setNextFocusableComponent(efaDirekt_exitTime);
    skipZiel.setNextFocusableComponent(skipMannschKm);
    skipZiel.setToolTipText("Bei der Eingabe das Geld \'Ziel\' überspringen");
    skipZiel.setMnemonic('Z');
    skipZiel.setText("Eingabefeld \'Ziel\' überspringen (Ziel nicht erforderlich)");
    efaDirektErscheinungsbildPanel.setLayout(gridBagLayout6);
    Dialog.setPreferredSize(jLabel33,280,13);
    jLabel33.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel33.setText("Erscheinungsbild der Buttons");
    efaDirektErscheinungsbildPanel.setBorder(BorderFactory.createEtchedBorder());
    efaDirektEinstellungenPanel.setBorder(BorderFactory.createEtchedBorder());
    efaDirektFahrtenPanel.setBorder(BorderFactory.createEtchedBorder());
    efaDirektFahrtenPanel.setLayout(gridBagLayout9);
    efaDirekt_fahrtBeginnenButton.setNextFocusableComponent(efaDirekt_FahrtBeginnenTextButton);
    Dialog.setPreferredSize(efaDirekt_fahrtBeginnenButton,200,23);
    efaDirekt_fahrtBeginnenButton.setText("Fahrt beginnen >>>");
    efaDirekt_fahrtBeginnenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtBeendenButton.setNextFocusableComponent(efaDirekt_fahrtBeendenTextButton);
    Dialog.setPreferredSize(efaDirekt_fahrtBeendenButton,200,23);
    efaDirekt_fahrtBeendenButton.setText("<<< Fahrt beenden");
    efaDirekt_fahrtBeendenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtAbbrechenButton.setNextFocusableComponent(efaDirekt_nachtragButton);
    Dialog.setPreferredSize(efaDirekt_fahrtAbbrechenButton,200,23);
    efaDirekt_fahrtAbbrechenButton.setText("Fahrt abbrechen");
    efaDirekt_fahrtAbbrechenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_nachtragButton.setNextFocusableComponent(efaDirekt_bootsreservierungenButton);
    Dialog.setPreferredSize(efaDirekt_nachtragButton,200,23);
    efaDirekt_nachtragButton.setText("Nachtrag");
    efaDirekt_nachtragButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_bootsreservierungenButton.setNextFocusableComponent(efaDirekt_bootsreservierungenAnzeigen);
    Dialog.setPreferredSize(efaDirekt_bootsreservierungenButton,200,23);
    efaDirekt_bootsreservierungenButton.setText("Bootsreservierungen");
    efaDirekt_bootsreservierungenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtenbuchAnzeigenButton.setNextFocusableComponent(efaDirekt_fahrtenbuchAnzeigenAnzeigen);
    Dialog.setPreferredSize(efaDirekt_fahrtenbuchAnzeigenButton,200,23);
    efaDirekt_fahrtenbuchAnzeigenButton.setText("Fahrtenbuch anzeigen");
    efaDirekt_fahrtenbuchAnzeigenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_statistikErstellenButton.setNextFocusableComponent(efaDirekt_statistikErstellenAnzeigen);
    Dialog.setPreferredSize(efaDirekt_statistikErstellenButton,200,23);
    efaDirekt_statistikErstellenButton.setText("Statistik erstellen");
    efaDirekt_statistikErstellenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_nachrichtAnAdminButton.setNextFocusableComponent(efaDirekt_nachrichtAnAdminAnzeigen);
    Dialog.setPreferredSize(efaDirekt_nachrichtAnAdminButton,200,23);
    efaDirekt_nachrichtAnAdminButton.setText("Nachricht an Admin");
    efaDirekt_nachrichtAnAdminButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_adminModusButton.setNextFocusableComponent(efaDirekt_adminModusAnzeigen);
    Dialog.setPreferredSize(efaDirekt_adminModusButton,200,23);
    efaDirekt_adminModusButton.setText("Admin-Modus");
    efaDirekt_adminModusButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_FahrtBeginnenTextButton.setNextFocusableComponent(efaDirekt_fahrtBeendenButton);
    efaDirekt_FahrtBeginnenTextButton.setText("Text");
    efaDirekt_FahrtBeginnenTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_FahrtBeginnenTextButton_actionPerformed(e);
      }
    });
    efaDirekt_fahrtBeendenTextButton.setNextFocusableComponent(efaDirekt_fahrtAbbrechenButton);
    efaDirekt_fahrtBeendenTextButton.setText("Text");
    efaDirekt_fahrtBeendenTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_fahrtBeendenTextButton_actionPerformed(e);
      }
    });
    efaDirekt_bootsreservierungenAnzeigen.setNextFocusableComponent(efaDirekt_fahrtenbuchAnzeigenButton);
    efaDirekt_bootsreservierungenAnzeigen.setText("anzeigen");
    efaDirekt_statistikErstellenAnzeigen.setNextFocusableComponent(efaDirekt_nachrichtAnAdminButton);
    efaDirekt_statistikErstellenAnzeigen.setText("anzeigen");
    efaDirekt_nachrichtAnAdminAnzeigen.setNextFocusableComponent(efaDirekt_adminModusButton);
    efaDirekt_nachrichtAnAdminAnzeigen.setText("anzeigen");
    efaDirekt_adminModusAnzeigen.setNextFocusableComponent(efaDirekt_spezialButton);
    efaDirekt_adminModusAnzeigen.setText("anzeigen");
    efaDirekt_fahrtenbuchAnzeigenAnzeigen.setNextFocusableComponent(efaDirekt_statistikErstellenButton);
    efaDirekt_fahrtenbuchAnzeigenAnzeigen.setText("anzeigen");
    efaDirekt_logoLabel.setBorder(BorderFactory.createEtchedBorder());
    efaDirekt_logoLabel.setPreferredSize(new Dimension(192, 64));
    efaDirekt_logoLabel.setToolTipText("Vereinslogo");
    efaDirekt_logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    efaDirekt_logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    efaDirekt_logoSelectButton.setNextFocusableComponent(efaDirekt_logoDeleteButton);
    efaDirekt_logoSelectButton.setText("auswählen");
    efaDirekt_logoSelectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_logoSelectButton_actionPerformed(e);
      }
    });
    efaDirekt_logoDeleteButton.setNextFocusableComponent(efaDirekt_maximiertStarten);
    efaDirekt_logoDeleteButton.setText("entfernen");
    efaDirekt_logoDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_logoDeleteButton_actionPerformed(e);
      }
    });
    jLabel34.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel34.setText("Vereinslogo");
    efaDirekt_reservierungenErlaubt.setNextFocusableComponent(efaDirekt_reservierungenErlaubtZyklisch);
    efaDirekt_reservierungenErlaubt.setMnemonic('R');
    efaDirekt_reservierungenErlaubt.setText("Mitglieder dürfen Boote reservieren (einmalige Reservierungen)");
    efaDirekt_maximiertStarten.setNextFocusableComponent(efaDirekt_fensterNichtVerschiebbar);
    efaDirekt_maximiertStarten.setToolTipText("bewirkt, daß efa im Vollbildmodus gestartet wird");
    efaDirekt_maximiertStarten.setMnemonic('M');
    efaDirekt_maximiertStarten.setText("efa maximiert starten");
    efaDirekt_sortBooteByAnzahl.setNextFocusableComponent(efaDirekt_showEingabeInfos);
    efaDirekt_sortBooteByAnzahl.setToolTipText("legt fest, ob die Boote in den Bootslisten nach Name oder Anzahl " +
    "der Ruderplätze sortiert sein sollen");
    efaDirekt_sortBooteByAnzahl.setMnemonic('O');
    efaDirekt_sortBooteByAnzahl.setText("sortiere Boote nach Anzahl der Ruderplätze");
    autoObmann.setNextFocusableComponent(defaultObmann);
    autoObmann.setToolTipText("Bei der Eingabe neuer Fahrten automatisch den Obmann auswählen");
    autoObmann.setMnemonic('O');
    autoObmann.setText("Obmann bei Eingabe automatisch auswählen");
    jLabel35.setDisplayedMnemonic('A');
    jLabel35.setLabelFor(efaDirekt_exitTime);
    jLabel35.setText("efa automatisch um ");
    jLabel36.setText(" Uhr beenden");
    jLabel37.setDisplayedMnemonic('M');
    jLabel37.setLabelFor(efaDirekt_execOnEfaAutoExit);
    jLabel37.setText("Folgendes Kommando beim automatischen Beenden von efa ausführen:");
    efaDirekt_exitTime.setNextFocusableComponent(efaDirekt_execOnEfaAutoExit);
    Dialog.setPreferredSize(efaDirekt_exitTime,80,19);
    efaDirekt_exitTime.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        efaDirekt_exitTime_focusLost(e);
      }
    });
    efaDirekt_execOnEfaAutoExit.setNextFocusableComponent(efaDirekt_restartTime);
    showObmann.setNextFocusableComponent(autoObmann);
    showObmann.setMnemonic('N');
    showObmann.setText("Obmann-Auswahlliste anzeigen");
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
    jLabel38.setText("Look & Feel: ");
    efaDirekt_fensterNichtVerschiebbar.setNextFocusableComponent(efaDirekt_immerImVordergrund);
    efaDirekt_fensterNichtVerschiebbar.setToolTipText("bewirkt, daß der Benutzer das Hauptfenster von efa nicht verschieben " +
    "kann");
    efaDirekt_fensterNichtVerschiebbar.setMnemonic('R');
    efaDirekt_fensterNichtVerschiebbar.setText("Hauptfenster nicht verschiebbar");
    efaDirekt_fensterNichtVerschiebbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_fensterNichtVerschiebbar_actionPerformed(e);
      }
    });
    efaDirekt_immerImVordergrund.setNextFocusableComponent(efaDirekt_sortBooteByAnzahl);
    efaDirekt_immerImVordergrund.setToolTipText("bewirkt, daß efa immer im Vordergrund angezeigt wird und nicht von " +
    "anderen Programmen verdeckt wird");
    efaDirekt_immerImVordergrund.setMnemonic('V');
    efaDirekt_immerImVordergrund.setText("efa immer im Vordergrund");
    efaDirekt_immerImVordergrund.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_immerImVordergrund_actionPerformed(e);
      }
    });
    efaDirektErscheinungsbildWeiterePanel.setLayout(gridBagLayout7);
    efaDirekt_buttonsShowHotkey.setNextFocusableComponent(efaDirekt_showFahrtzielInBooteAufFahrt);
    efaDirekt_buttonsShowHotkey.setToolTipText("bewirkt, daß auf der Buttons zusätzlich die Hotkeys zum Aktivieren " +
    "der Buttons angezeigt werden");
    efaDirekt_buttonsShowHotkey.setMnemonic('K');
    efaDirekt_buttonsShowHotkey.setText("Hotkeys für Buttons anzeigen");
    efaDirekt_buttonsShowHotkey.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_buttonsShowHotkey_actionPerformed(e);
      }
    });

    jLabel39.setDisplayedMnemonic('G');
    jLabel39.setLabelFor(efaDirekt_fontSize);
    jLabel39.setText("Schriftgröße (leer=Standard): ");
    efaDirekt_fontSize.setNextFocusableComponent(efaDirekt_fontStyle);
    efaDirekt_fontSize.setToolTipText("Schriftgröße in Punkten (6 bis 32)");
    Dialog.setPreferredSize(efaDirekt_fontSize,50,19);
    efaDirekt_fontSize.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        efaDirekt_fontSize_focusLost(e);
      }
    });
    efaDirekt_showEingabeInfos.setNextFocusableComponent(efadirekt_colorizedtextfield);
    efaDirekt_showEingabeInfos.setToolTipText("Beim Ein- und Austragen von Booten Hinweise zur Eingabe anzeigen");
    efaDirekt_showEingabeInfos.setMnemonic('H');
    efaDirekt_showEingabeInfos.setText("Eingabehinweise anzeigen");
    jLabel40.setText("Schriftstil: ");
    Dialog.setPreferredSize(efaDirekt_fontStyle,100,20);
    efaDirekt_fontStyle.addItem("Standard");
    efaDirekt_fontStyle.addItem("normal");
    efaDirekt_fontStyle.addItem("fett");
    efaDirekt_fontStyle.setNextFocusableComponent(saveButton);
    showBerlinOptions.setNextFocusableComponent(saveButton);
    showBerlinOptions.setToolTipText("Optionen, die nur für Berliner Vereine gedacht sind, anzeigen");
    showBerlinOptions.setText("Berlin-spezifische Optionen anzeigen");
    lookAndFeel.setNextFocusableComponent(showBerlinOptions);
    jLabel41.setDisplayedMnemonic('D');
    jLabel41.setLabelFor(defaultObmann);
    jLabel41.setText("Standard-Obmann für ungesteuerte Boote: ");
    defaultObmann.setNextFocusableComponent(completePopupEnabled);
    efaDirekt_showUhr.setNextFocusableComponent(efaDirekt_newsText);
    efaDirekt_showUhr.setMnemonic('U');
    efaDirekt_showUhr.setText("Uhr anzeigen");
    efaDirekt_spezialButton.setNextFocusableComponent(efaDirekt_spezialButtonAnzeigen);
    efaDirekt_spezialButton.setText("Spezial-Button");
    Dialog.setPreferredSize(efaDirekt_spezialButton,200,23);
    efaDirekt_spezialButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorButton_actionPerformed(e);
      }
    });
    efaDirekt_spezialButtonAnzeigen.setNextFocusableComponent(efaDirekt_spezielButtonTextButton);
    efaDirekt_spezialButtonAnzeigen.setText("anzeigen");
    efaDirekt_spezielButtonTextButton.setNextFocusableComponent(efaDirekt_spezialButtonCmd);
    efaDirekt_spezielButtonTextButton.setText("Text");
    efaDirekt_spezielButtonTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaDirekt_spezielButtonTextButton_actionPerformed(e);
      }
    });
    jLabel42.setText("bei Click ausführen: ");
    efaDirekt_spezialButtonCmd.setNextFocusableComponent(efaDirekt_buttonsShowHotkey);
    completePopupEnabled.setNextFocusableComponent(correctMisspelledMitglieder);
    completePopupEnabled.setMnemonic('V');
    completePopupEnabled.setText("Beim Vervollständigen Popup-Liste anzeigen");
    correctMisspelledMitglieder.setNextFocusableComponent(correctMisspelledBoote);
    correctMisspelledMitglieder.setText("Mitglieder");
    efaDirektBenachrichtigungPanel.setLayout(gridBagLayout8);
    efaDirektSunrisePanel.setLayout(gridBagLayout10);
    jLabel43.setText("Einstellungen zum email-Versand:");
    jLabel44.setDisplayedMnemonic('S');
    jLabel44.setLabelFor(efaDirekt_emailServer);
    jLabel44.setText("SMTP-Server: ");
    jLabel45.setDisplayedMnemonic('U');
    jLabel45.setLabelFor(efaDirekt_emailUsername);
    jLabel45.setText("Username: ");
    jLabel46.setDisplayedMnemonic('P');
    jLabel46.setLabelFor(efaDirekt_emailPassword);
    jLabel46.setText("Paßwort: ");
    jLabel47.setDisplayedMnemonic('A');
    jLabel47.setLabelFor(efaDirekt_emailAbsender);
    jLabel47.setText("Absender-Adresse: ");
    efaDirekt_emailServer.setNextFocusableComponent(efaDirekt_emailUsername);
    Dialog.setPreferredSize(efaDirekt_emailServer,250,19);
    efaDirekt_emailUsername.setNextFocusableComponent(efaDirekt_emailPassword);
    Dialog.setPreferredSize(efaDirekt_emailUsername,250,19);
    efaDirekt_emailPassword.setNextFocusableComponent(efaDirekt_emailAbsenderName);
    Dialog.setPreferredSize(efaDirekt_emailPassword,250,19);
    efaDirekt_emailAbsender.setNextFocusableComponent(efaDirekt_emailBetreffPraefix);
    Dialog.setPreferredSize(efaDirekt_emailAbsender,250,19);
    efaDirekt_resBooteNichtVerfuegbar.setNextFocusableComponent(efaDirekt_wafaRegattaBooteNichtVerfuegbar);
    efaDirekt_resBooteNichtVerfuegbar.setMnemonic('E');
    efaDirekt_resBooteNichtVerfuegbar.setText("Reservierte Boote als \'nicht verfügbar\' anzeigen");
    efaDirekt_showSunrise.setMnemonic('O');
    efaDirekt_showSunrise.setText("Sonnenaufgangs- und -untergangszeit anzeigen");
    jLabel48.setDisplayedMnemonic('B');
    jLabel48.setLabelFor(efaDirekt_latComboBox);
    jLabel48.setText("geographische Breite: ");
    jLabel49.setDisplayedMnemonic('L');
    jLabel49.setLabelFor(efaDirekt_lonComboBox);
    jLabel49.setText("geographische Länge: ");
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
    jLabel56.setDisplayedMnemonic('N');
    jLabel56.setText("Absender-Name: ");
    jLabel57.setDisplayedMnemonic('E');
    jLabel57.setText("Betreff (Präfix): ");
    jLabel58.setDisplayedMnemonic('I');
    jLabel58.setText("Signatur: ");
    emailSignaturScrollPane.setPreferredSize(new Dimension(200, 50));
    efaDirekt_emailAbsenderName.setNextFocusableComponent(efaDirekt_emailAbsender);
    efaDirekt_emailBetreffPraefix.setNextFocusableComponent(efaDirekt_emailSignatur);
    efaDirekt_emailSignatur.setNextFocusableComponent(efaDirekt_showSunrise);
    efaDirekt_reservierungenErlaubtZyklisch.setNextFocusableComponent(efaDirekt_reservierungenEditErlaubt);
    efaDirekt_reservierungenErlaubtZyklisch.setMnemonic('W');
    efaDirekt_reservierungenErlaubtZyklisch.setText("Mitglieder dürfen Boote reservieren (wöchentliche Reservierungen)");
    jLabel59.setText("Fahrtenbucheinträge auf Tippfehler prüfen für ");
    correctMisspelledBoote.setNextFocusableComponent(correctMisspelledZiele);
    correctMisspelledBoote.setText("Boote");
    correctMisspelledZiele.setNextFocusableComponent(skipUhrzeit);
    correctMisspelledZiele.setText("Ziele");
    jLabel60.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel60.setText("Einstellungen zum Eintragen von Fahrten");
    efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.setNextFocusableComponent(efaDirekt_nurBekannteBoote);
    efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.setMnemonic('K');
    efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.setText("Vorgeschlagene Kilometer bei bekannten Zielen können nicht geändert " +
    "werden");
    efaDirekt_eintragNichtAenderbarUhrzeit.setNextFocusableComponent(efaDirekt_zielBeiFahrtbeginn);
    efaDirekt_eintragNichtAenderbarUhrzeit.setMnemonic('V');
    efaDirekt_eintragNichtAenderbarUhrzeit.setText("Vorgeschlagene Uhrzeiten können nicht geändert werden");
    efaDirekt_eintragErlaubeNurMaxRudererzahl.setNextFocusableComponent(efaDirekt_eintragErzwingeObmann);
    efaDirekt_eintragErlaubeNurMaxRudererzahl.setMnemonic('M');
    efaDirekt_eintragErlaubeNurMaxRudererzahl.setText("Nur für das Boot maximal mögliche Anzahl an Ruderern erlauben");
    efaDirekt_eintragErzwingeObmann.setNextFocusableComponent(efaDirekt_abfahrtMinuten);
    efaDirekt_eintragErzwingeObmann.setMnemonic('O');
    efaDirekt_eintragErzwingeObmann.setText("Obmann muß ausgewählt werden");
    aliasGenButton.setText("neu generieren");
    aliasGenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        aliasGenButton_actionPerformed(e);
      }
    });
    efaDirekt_wafaRegattaBooteNichtVerfuegbar.setMnemonic('T');
    efaDirekt_wafaRegattaBooteNichtVerfuegbar.setText("Boote auf Regatta, Trainingslager oder Mehrtagesfahrt als \'nicht verfügbar\' anzeigen");
    efadirekt_colorizedtextfield.setNextFocusableComponent(efaDirekt_showUhr);
    efadirekt_colorizedtextfield.setActionCommand("aktuelles Eingabefeld farblich hervorheben");
    efadirekt_colorizedtextfield.setText("aktuelles Eingabefeld farblich hervorheben");
    jLabel61.setText("Beim Eintrag von Fahrten nur bekannte Namen erlauben für:");
    efaDirekt_nurBekannteBoote.setNextFocusableComponent(efaDirekt_nurBekannteRuderer);
    efaDirekt_nurBekannteBoote.setMnemonic('B');
    efaDirekt_nurBekannteBoote.setText("Boote");
    efaDirekt_nurBekannteRuderer.setNextFocusableComponent(efaDirekt_nurBekannteZiele);
    efaDirekt_nurBekannteRuderer.setMnemonic('R');
    efaDirekt_nurBekannteRuderer.setText("Ruderer");
    efaDirekt_nurBekannteZiele.setNextFocusableComponent(saveButton);
    efaDirekt_nurBekannteZiele.setMnemonic('E');
    efaDirekt_nurBekannteZiele.setText("Ziele");
    jLabel62.setText("efa automatisch um ");
    jLabel63.setDisplayedMnemonic('N');
    jLabel63.setLabelFor(efaDirekt_restartTime);
    jLabel63.setText(" Uhr neu starten");
    efaDirekt_restartTime.setNextFocusableComponent(saveButton);
    efaDirekt_restartTime.setText("");
    Dialog.setPreferredSize(efaDirekt_restartTime,80,19);
    efaDirekt_restartTime.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        efaDirekt_restartTime_focusLost(e);
      }
    });
    standardFahrtartLabel.setText("Standard-Fahrtart: ");
    standardFahrtart.setNextFocusableComponent(showObmann);
    efaDirekt_showFahrtzielInBooteAufFahrt.setNextFocusableComponent(efaDirekt_logoSelectButton);
    efaDirekt_showFahrtzielInBooteAufFahrt.setMnemonic('Z');
    efaDirekt_showFahrtzielInBooteAufFahrt.setText("Fahrtziel in der Liste \'Boote auf Fahrt\' anzeigen");
    efaDirekt_mitgliederNamenHinzufuegen.setNextFocusableComponent(efaDirekt_resBooteNichtVerfuegbar);
    efaDirekt_mitgliederNamenHinzufuegen.setText("Mitglieder dürfen Namen zur Mitgliederliste hinzufügen");
    jLabel64.setText("Verzeichnis für Nutzer");
    jLabel65.setText("Pfade für efa");
    jLabel66.setDisplayedMnemonic('N');
    jLabel66.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel66.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel66.setLabelFor(userdatadir);
    jLabel66.setText("Nutzerdaten: ");
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
    jLabel67.setText("Benachrichtigungen verschicken:");
    jLabel68.setText("an Admins");
    jLabel69.setText("an Bootswarte");
    jLabel70.setText("bei Fehlern (ERROR): ");
    jLabel71.setText("bei Warnungen (WARNING) einmal pro Woche:");
    jLabel72.setText("bei Bootsstatus-Änderungen:");
    efaDirekt_newsTextLabel.setDisplayedMnemonic('W');
    efaDirekt_newsTextLabel.setLabelFor(efaDirekt_newsText);
    efaDirekt_newsTextLabel.setText("News-Text: ");
    efaDirekt_newsText.setNextFocusableComponent(efaDirekt_fontSize);
    Allgemein.add(allgemeinPanel,   "Allgemein");
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
    allgemeinPanel.add(showBerlinOptions,          new GridBagConstraints(0, 24, 4, 1, 0.0, 0.0
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
    Allgemein.add(backupPanel,   "Backup");
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
    mainPanel.add(Allgemein, BorderLayout.CENTER);
    Allgemein.add(druckPanel,  "Drucken");
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
    Allgemein.add(efaDirektPane, "efa im Bootshaus");
    Allgemein.add(extProgPanel,   "Pfade");
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
    efaDirektPane.add(efaDirektEinstellungenPanel,   "Allgemeine Einstellungen");
    efaDirektPane.add(efaDirektFahrtenPanel,   "Eintrag von Fahrten");
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
    efaDirektPane.add(efaDirektErscheinungsbildPanel,   "Erscheinungsbild");

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
    efaDirektPane.add(efaDirektBenachrichtigungPanel,   "Benachrichtigungen");
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
    userdatadir.setText(Daten.efaConfigUserHome.efaUserDirectory);

    autogenAlias.setSelected(Daten.efaConfig.autogenAlias);
    aliasFormat.setText(Daten.efaConfig.aliasFormat);
    autoStandardmannsch.setSelected(Daten.efaConfig.autoStandardmannsch);
    for(int i=0; i<Daten.bezeichnungen.fahrtart.size(); i++) {
       standardFahrtart.addItem(Daten.bezeichnungen.fahrtart.get(i));
    }
    standardFahrtart.setSelectedItem(Daten.efaConfig.standardFahrtart);

    showObmann.setSelected(Daten.efaConfig.showObmann);
    autoObmann.setSelected(Daten.efaConfig.autoObmann);
    defaultObmann.addItem("Bugmann");
    defaultObmann.addItem("Schlagmann");
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
    lookAndFeelArray[0] = EfaConfig.LOOKANDFEEL_STANDARD;
    lookAndFeel.addItem(EfaConfig.LOOKANDFEEL_STANDARD);
    for (int i=0; i<info.length; i++) {
      lookAndFeelArray[i+1] = info[i].getClassName();
      String s = info[i].getClassName();
      int pos = (s != null ? s.lastIndexOf(".") : -1);
      if (pos>0 && pos+1<s.length()) s = s.substring(pos+1,s.length());
      else s = EfaConfig.LOOKANDFEEL_STANDARD;
      lookAndFeel.addItem(s);
    }
    for (int i=0; i<lookAndFeelArray.length; i++) {
      if (lookAndFeelArray[i].endsWith(Daten.efaConfig.lookAndFeel)) {
        lookAndFeel.setSelectedIndex(i); break;
      }
    }
    efaDirekt_latComboBox.addItem("Nord");
    efaDirekt_latComboBox.addItem("Süd");
    efaDirekt_lonComboBox.addItem("West");
    efaDirekt_lonComboBox.addItem("Ost");

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
            parseError = "Ungültige Variablenbezeichnung: '{' im Variablennamen nicht erlaubt";
            return i;
          }
          vari++;
          break;
        case '}':
          if (vari != 3) {
            parseError = "Ungültige Variablenbezeichnung: Variablenname muß die Form '<Feld><Position>' haben";
            return i;
          }
          vari=0;
          break;
        default:
          switch (vari) {
            case 1:
              if (s.charAt(i) != 'V' && s.charAt(i) != 'v' && s.charAt(i) != 'N' && s.charAt(i) != 'n'
                  && s.charAt(i) != 'C' && s.charAt(i) != 'c') {
                parseError = "Ungültige Variablenbezeichnung: <Feld> muß einen der Werte 'V', 'N', 'C' haben";
                return i;
              }
              vari++;
              break;
            case 2:
              if (s.charAt(i) < 49 || s.charAt(i) > 57) {
                parseError = "Ungültige Variablenbezeichnung: <Position> muß eine Ziffer sein";
                return i;
              }
              vari++;
              break;
            case 3:
              parseError = "Ungültige Variablenbezeichnung: Variablenname muß die Form '<Feld><Position>' haben";
              return i;
          }
      }
    if (vari != 0) {
      parseError = "Unvollständiger Formatstring: Variable nicht korrekt abgeschlossen (Format: {<Feld><Position>})";
      return i;
    }
    return -1;
  }

  boolean checkAliasFormat() {
    aliasFormat.setText(aliasFormat.getText().trim());
    int i;
    if ( ( i = parseAlias(aliasFormat.getText().trim())) != -1) {
      Dialog.infoDialog("Syntaxfehler",parseError);
      aliasFormat.setCaretPosition(i);
      return false;
    }
    return true;
  }

  boolean checkBakDir() {
    String s = bakDir.getText().trim();
    if (s.equals("")) return true;

    if (!new File(s).isDirectory()) {
      Dialog.infoDialog("Fehler","Das angegeben Verzeichnis\n'"+s+"'\nexistiert nicht.");
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

  // Backup-Verzeichnis setzen
  public static void setBakDir(String dir) {
    Daten.efaBakDirectory = dir;
    if (Daten.efaBakDirectory == null || Daten.efaBakDirectory.equals("") || !new File(Daten.efaBakDirectory).isDirectory())
      if (new File(Daten.efaMainDirectory+"backup"+Daten.fileSep).isDirectory())
        Daten.efaBakDirectory = Daten.efaMainDirectory+"backup"+Daten.fileSep;
        else Daten.efaBakDirectory = Daten.efaMainDirectory;
    if (!Daten.efaBakDirectory.endsWith(Daten.fileSep)) Daten.efaBakDirectory += Daten.fileSep;
  }

  void browser_focusLost(FocusEvent e) {
    if (!browser.getText().trim().equals("") && !new File(browser.getText().trim()).isFile())
      Dialog.infoDialog("Browser nicht gefunden","Das Programm\n'"+browser.getText().trim()+"'\nkonnte nicht gefunden werden!");
  }
  void browserButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,"Webbroser auswählen","Windows-Programme (*.exe)","exe",Daten.efaConfig.browser,false);
    if (dat != null)
      browser.setText(dat);
  }
  void userdatadirButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,"Nutzerdaten-Verzeichnis auswählen","Verzeichnisse",null,Daten.efaConfigUserHome.efaUserDirectory,null,null,false,true);
    if (dat != null)
      userdatadir.setText(dat);
  }

  void acrobat_focusLost(FocusEvent e) {
    if (!acrobat.getText().trim().equals("") && !new File(acrobat.getText().trim()).isFile())
      Dialog.infoDialog("Acrobat Reader nicht gefunden","Das Programm\n'"+acrobat.getText().trim()+"'\nkonnte nicht gefunden werden!");
  }

  void acrobatButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,"Acrobat Reader auswählen","Windows-Programme (*.exe)","exe",Daten.efaConfig.acrobat,false);
    if (dat != null)
      acrobat.setText(dat);
  }

  void saveButton_actionPerformed(ActionEvent e) {
    String newUserDir = userdatadir.getText().trim();
    if (newUserDir.length()>0 && !Daten.efaConfigUserHome.efaUserDirectory.equals(newUserDir)) {
      if (!Daten.efaConfigUserHome.efaCanWrite(newUserDir,true)) {
        Dialog.error("efa kann in das Nutzerdaten-Verzeichnis '" + newUserDir +
            "' nicht schreiben.\nBitte wähle ein anderes Verzeichnis aus.");
        userdatadir.requestFocus();
        userdatadir.setCaretPosition(userdatadir.getText().length());
        return;
      } else {
        Daten.efaConfigUserHome.efaUserDirectory = newUserDir;
        Daten.efaConfigUserHome.writeFile();
        Dialog.infoDialog("Neues Nutzerdaten-Verzeichnis",
                          "Das neue Nutzerdaten-Verzeichnis\n"+
                          "'"+newUserDir+"'\n"+
                          "wird erst nach einem Neustart von efa benutzt.\n"+
                          "efa kopiert keinerlei bestehende Daten von dem alten in das neue Verzeichnis\n"+
                          "und löscht auch keinerlei Daten in dem alten Verzeichnis.");
      }
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
      Dialog.infoDialog("Fehler","Die Einstellungen konnten nicht gespeichert werden, da die Datei\n'"+Daten.efaConfig.dat+"'\nnicht geschrieben werden konnte.");
    }

    setBakDir(Daten.efaConfig.bakDir);
    Daten.backup = new Backup(Daten.efaBakDirectory,Daten.efaConfig.bakSave,Daten.efaConfig.bakMonat,Daten.efaConfig.bakTag,Daten.efaConfig.bakKonv);

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
    dir = Dialog.dateiDialog(this,"Backup-Verzeichnis auswählen",null,null,dir,null,"auswählen",false,true);
    if (dir != null)
      bakDir.setText(dir);
  }

  void colorButton_actionPerformed(ActionEvent e) {
    Color color = JColorChooser.showDialog(this,"Farbe wählen",((JButton)e.getSource()).getBackground());
    if (color != null) ((JButton)e.getSource()).setBackground(color);
  }

  void efaDirekt_FahrtBeginnenTextButton_actionPerformed(ActionEvent e) {
    String s = Dialog.inputDialog(efaDirekt_fahrtBeginnenButton.getText().trim(),"Bitte Text für Button eingeben:");
    if (s != null) efaDirekt_fahrtBeginnenButton.setText(s);
  }

  void efaDirekt_fahrtBeendenTextButton_actionPerformed(ActionEvent e) {
    String s = Dialog.inputDialog(efaDirekt_fahrtBeendenButton.getText().trim(),"Bitte Text für Button eingeben:");
    if (s != null) efaDirekt_fahrtBeendenButton.setText(s);
  }

  void efaDirekt_spezielButtonTextButton_actionPerformed(ActionEvent e) {
    String s = Dialog.inputDialog(efaDirekt_spezialButton.getText().trim(),"Bitte Text für Button eingeben:");
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
    String datei = Dialog.dateiDialog(this,"Vereinslogo auswählen","Bild-Datei (*.gif, *.jpg)","gif|jpg",dir,efaDirekt_vereinsLogo,"auswählen",false,false);
    if (datei == null) return;
    if (!EfaUtil.canOpenFile(datei)) {
      Dialog.error("Datei '"+datei+"' kann nicht geöffnet werden!");
      return;
    }
    setVereinsLogo(datei);
  }

  void efaDirekt_logoDeleteButton_actionPerformed(ActionEvent e) {
    if (efaDirekt_vereinsLogo == null) return;
    if (Dialog.yesNoDialog("Logo wirklich entfernen","Soll das Logo wirklich entfernt werden?") == Dialog.YES) {
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
    dat = Dialog.dateiDialog(this,"Programm auswählen",null,null,dat,null,"auswählen",false,false);
    if (dat != null) efaDirekt_execOnEfaExit.setText(dat);
  }

  void exec2FileSelectButton_actionPerformed(ActionEvent e) {
    String dat = efaDirekt_execOnEfaAutoExit.getText().trim();
    if (dat.length() == 0 || !new File(dat).isFile()) dat = Daten.efaMainDirectory;
    dat = Dialog.dateiDialog(this,"Programm auswählen",null,null,dat,null,"auswählen",false,false);
    if (dat != null) efaDirekt_execOnEfaAutoExit.setText(dat);
  }

  void efaDirekt_immerImVordergrund_actionPerformed(ActionEvent e) {
    if (efaDirekt_immerImVordergrund.isSelected() && Daten.javaVersion.compareTo("1.5")<0)
      Dialog.infoDialog("Diese Funktion wird erst ab Java Version 1.5 unterstützt.");
  }

  void efaDirekt_fensterNichtVerschiebbar_actionPerformed(ActionEvent e) {
    if (efaDirekt_fensterNichtVerschiebbar.isSelected() && Daten.javaVersion.compareTo("1.4")<0)
      Dialog.infoDialog("Diese Funktion wird erst ab Java Version 1.4 unterstützt.");
  }

  void efaDirekt_fontSize_focusLost(FocusEvent e) {
    int size = EfaUtil.string2date(efaDirekt_fontSize.getText(),0,0,0).tag;
    if (size<6 && size != 0) size = 6;
    if (size>32) size = 32;
    if (size == 0) efaDirekt_fontSize.setText("");
    else efaDirekt_fontSize.setText(Integer.toString(size));
    if (Dialog.screenSize != null) {
      if ((Dialog.screenSize.getWidth()<=800 || Dialog.screenSize.getHeight()<=600) && size>14) {
        Dialog.infoDialog("Warnung",
                          "Für eine Auflösung von 800x600 wird eine maximale Schriftgröße\n"+
                          "von 14 empfohlen. Eine größere Schriftgröße kann dazu führen,\n"+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.");
        return;
      }
      if ((Dialog.screenSize.getWidth()<=1024 || Dialog.screenSize.getHeight()<=768) && size>16) {
        Dialog.infoDialog("Warnung",
                          "Für eine Auflösung von 1024x768 wird eine maximale Schriftgröße\n"+
                          "von 16 empfohlen. Eine größere Schriftgröße kann dazu führen,\n"+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.");
        return;
      }
      if ((Dialog.screenSize.getWidth()<=1280 || Dialog.screenSize.getHeight()<=1024) && size>18) {
        Dialog.infoDialog("Warnung",
                          "Für eine Auflösung von 1280x1024 wird eine maximale Schriftgröße\n"+
                          "von 18 empfohlen. Eine größere Schriftgröße kann dazu führen,\n"+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.");
        return;
      }
      if ((Dialog.screenSize.getWidth()>1280 && Dialog.screenSize.getHeight()>1024) && size>20) {
        Dialog.infoDialog("Warnung",
                          "Auch für hohe Auflösungen wird eine maximale Schriftgröße\n"+
                          "von 20 empfohlen. Eine größere Schriftgröße kann dazu führen,\n"+
                          "daß einige Fenster von efa nicht korrekt dargestellt werden.");
        return;
      }
    }
  }

  void efaDirekt_buttonsShowHotkey_actionPerformed(ActionEvent e) {
    if (efaDirekt_buttonsShowHotkey.isSelected() && Daten.javaVersion.compareTo("1.4")<0)
      Dialog.infoDialog("Diese Funktion wird erst ab Java Version 1.4 unterstützt.");
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
      Dialog.error("Generieren der Eingabekürzel nicht möglich:\nEs ist zur Zeit keine Mitgliederliste geöffnet.");
      return;
    }
    Mitglieder m = Daten.fahrtenbuch.getDaten().mitglieder;
    if (Dialog.yesNoCancelDialog("Eingabekürzel neu generieren",
                                 "Sollen alle Eingabekürzel für die Mitgliederliste\n"+
                                 m.getFileName()+"\n"+
                                 "neu generiert und bestehende Eingabekürzel dabei\n"+
                                 "ersetzt werden?") != Dialog.YES) {
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
    Dialog.infoDialog(c+" Eingabekürzel neu generiert.");
    if (c > 0) Daten.fahrtenbuch.getDaten().mitglieder.setChanged();
  }


}
