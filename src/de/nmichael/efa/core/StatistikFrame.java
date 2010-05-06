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
import de.nmichael.efa.core.*;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.statistics.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;

// @i18n complete

public class StatistikFrame extends JDialog implements ActionListener {
  final static int ALL = 0;
  final static int WETT = 1;
  final static int BLANK = 2;

  final static int PROGRESS_TIMETOPOPUP   = 0;
  final static int PROGRESS_TIMERINTERVAL = 20;

  // Achtung: Diese Werte müssen mit den AUSGABE_* Konstanten aus StatistikDaten übereinstimmen!
  final static String[] ausgabeArten =
    { International.getString("im Programm (Grafik)"), // 0
      International.getString("im Programm (Text)"),   // 1
      International.getString("im Browser"),           // 2
      International.getString("als HTML-Datei"),       // 3
      International.getString("als PDF-Datei"),        // 4
      International.getString("als XML-Datei"),        // 5
      International.getString("als Textdatei"),        // 6
      International.getString("als CSV-Datei") };      // 7
//    "als Meldedatei"        // 8 (wird dynamisch hinzugefügt, wenn WettPanel aktiv ist)
  final static String[] ausgabeExt =
    { null, null, null, "html", "pdf", "xml", "txt", "csv", null };

  String aktStatName = ""; // "" normalerweise bzw. Name der bearbeiteten Statistik (wenn "Bearbeiten" gewählt
  boolean aktStatAuchEfaDirekt = false; // ob aktuell bearbeitete Statistik auch in efaDirekt verfügbar sein soll
  boolean statWettFromSavedValues=false; // damit wettPanel erkennt, ob es aufgrund von gesp. Einstellungen aktiviert wurde
  StatistikErweitertFrame erweitertFrame = null;

  private ProgressMonitor progressMonitor;
  private javax.swing.Timer timer = new javax.swing.Timer(PROGRESS_TIMERINTERVAL, new TimerListener());;
  private StatistikThread statistikThread;
  private DatenListe gruppen = null;


  BorderLayout borderLayout1 = new BorderLayout();
  JButton erstellenBut = new JButton();
  JTabbedPane auswahlPane = new JTabbedPane();
  JPanel rudererPanel = new JPanel();
  JPanel bootePanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel artPanel = new JPanel();
  JLabel artLabel = new JLabel();
  JComboBox art = new JComboBox();
  JPanel rest1Panel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel zeitraumPanel = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  JLabel zeitraumLabel = new JLabel();
  JLabel vonLabel = new JLabel();
  JTextField von = new JTextField();
  JLabel bisLabel = new JLabel();
  JTextField bis = new JTextField();
  JPanel auswahlPanel = new JPanel();
  JLabel auswahlLabel = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel geschlechtLabel = new JLabel();
  JScrollPane geschlechtScrollPane = new JScrollPane();
  JLabel status1Label = new JLabel();
  JScrollPane status1ScrollPane = new JScrollPane();
  JList nurGeschlecht = new JList();
  JList nurStatus1 = new JList();
  JLabel nurNameLabel = new JLabel();
  JTextField nurName = new JTextField();
  JPanel rest2Panel = new JPanel();
  JPanel ausgabePanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JCheckBox ausKm = new JCheckBox();
  JCheckBox ausName = new JCheckBox();
  JCheckBox ausJahrgang = new JCheckBox();
  JCheckBox ausStatus1 = new JCheckBox();
  JCheckBox ausZielfahrten = new JCheckBox();
  JLabel ausgabeLabel = new JLabel();
  JCheckBox ausRudKm = new JCheckBox();
  JCheckBox ausStmKm = new JCheckBox();
  JCheckBox ausFahrten = new JCheckBox();
  JCheckBox ausKmFahrt = new JCheckBox();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel graAusgabePanel = new JPanel();
  JLabel jLabel2 = new JLabel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JPanel rest3Panel = new JPanel();
  JCheckBox graKm = new JCheckBox();
  JTextField maxBalkenKm = new JTextField();
  JCheckBox graRudKm = new JCheckBox();
  JCheckBox graStmKm = new JCheckBox();
  JCheckBox graFahrten = new JCheckBox();
  JCheckBox graKmFahrt = new JCheckBox();
  JTextField maxBalkenRudKm = new JTextField();
  JTextField maxBalkenStmKm = new JTextField();
  JTextField maxBalkenFahrten = new JTextField();
  JTextField maxBalkenKmFahrt = new JTextField();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel sortPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JComboBox sortKrit = new JComboBox();
  JLabel sortFolgeLabel = new JLabel();
  JLabel sortLabel = new JLabel();
  JLabel sortKritLabel = new JLabel();
  JComboBox sortFolge = new JComboBox();
  JPanel numPanel = new JPanel();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JLabel numLabel = new JLabel();
  JScrollPane numScrollPane = new JScrollPane();
  JList numerierung = new JList();
  JCheckBox nameTeil = new JCheckBox();
  JPanel createPanel = new JPanel();
  JPanel dateiPanel = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JLabel outputLabel = new JLabel();
  JPanel gespeichertPanel = new JPanel();
  BorderLayout borderLayout7 = new BorderLayout();
  BorderLayout borderLayout8 = new BorderLayout();
  JPanel bArtPanel = new JPanel();
  JLabel bArtLabel = new JLabel();
  JComboBox bArt = new JComboBox();
  JPanel bRest1Panel = new JPanel();
  BorderLayout borderLayout9 = new BorderLayout();
  JPanel bNurPanel = new JPanel();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  JLabel bNurLabel = new JLabel();
  JLabel bNurArtLabel = new JLabel();
  JLabel bNurAnzahlLabel = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList mNurArt = new JList();
  JList mNurAnzahl = new JList();
  JCheckBox mNurSkull = new JCheckBox();
  JCheckBox mNurRiemen = new JCheckBox();
  JCheckBox mNurMitStm = new JCheckBox();
  JCheckBox mNurOhneStm = new JCheckBox();
  JLabel mNurNameLabel = new JLabel();
  JTextField mNurBoot = new JTextField();
  JPanel mRest2Panel = new JPanel();
  JPanel mAusPanel = new JPanel();
  BorderLayout borderLayout10 = new BorderLayout();
  BorderLayout borderLayout11 = new BorderLayout();
  JPanel mAusgabePanel = new JPanel();
  JPanel mGraPanel = new JPanel();
  JLabel mAusgabeLabel = new JLabel();
  JLabel mGraAusLabel = new JLabel();
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  JCheckBox mAusName = new JCheckBox();
  JCheckBox mAusArt = new JCheckBox();
  JCheckBox mAusBez = new JCheckBox();
  JCheckBox mAusKm = new JCheckBox();
  JCheckBox mAusFahrten = new JCheckBox();
  JCheckBox mAusKmFahrt = new JCheckBox();
  JCheckBox mGraAusKm = new JCheckBox();
  JCheckBox mGraAusFahrten = new JCheckBox();
  JCheckBox mGraAusKmFahrt = new JCheckBox();
  JTextField mGraSizeKm = new JTextField();
  JTextField mGraSizeFahrten = new JTextField();
  JTextField mGraSizeKmFahrt = new JTextField();
  JLabel mGraSizeLabel = new JLabel();
  JPanel mRest4Panel = new JPanel();
  BorderLayout borderLayout12 = new BorderLayout();
  JPanel mSortPanel = new JPanel();
  JPanel mNumPanel = new JPanel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  GridBagLayout gridBagLayout11 = new GridBagLayout();
  JLabel mSortKritLabel = new JLabel();
  JLabel mSortFolgeLabel = new JLabel();
  JComboBox mSortKrit = new JComboBox();
  JComboBox mSortFolge = new JComboBox();
  JCheckBox mNurEigene = new JCheckBox();
  JCheckBox mNurFremde = new JCheckBox();
  JCheckBox mNumEigene = new JCheckBox();
  JCheckBox mNumFremde = new JCheckBox();
  JCheckBox mNurAndere1 = new JCheckBox();
  JCheckBox mNurAndere2 = new JCheckBox();
  JScrollPane jScrollPane3 = new JScrollPane();
  JList statList = new JList();
  JPanel jPanel3 = new JPanel();
  JButton deleteButton = new JButton();
  JCheckBox ausAnzVersch = new JCheckBox();
  JButton editButton = new JButton();
  GridBagLayout gridBagLayout12 = new GridBagLayout();
  JCheckBox zeitFbUebergreifend = new JCheckBox();
  JPanel wettPanel = new JPanel();
  GridBagLayout gridBagLayout13 = new GridBagLayout();
  JLabel wettProzLabel = new JLabel();
  JTextField wettProz = new JTextField();
  JCheckBox ausWettBedingung = new JCheckBox();
  JLabel wettAnzLabel = new JLabel();
  JTextField wettAnz = new JTextField();
  JLabel wettTitleLabel = new JLabel();
  JPanel wettbewerbPanel = new JPanel();
  BorderLayout borderLayout13 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JComboBox wettArt = new JComboBox();
  JLabel jLabel5 = new JLabel();
  JTextField wettJahr = new JTextField();
  JCheckBox ausWettOhneDetails = new JCheckBox();
  JPanel jPanel2 = new JPanel();
  JPanel erweitertButtonPanel = new JPanel();
  JButton erweitertButton = new JButton();
  JPanel mErweitertButtonPanel = new JPanel();
  JButton bErweitertButton = new JButton();
  JLabel ausgabeArtLabel = new JLabel();
  JComboBox ausgabeArt = new JComboBox();
  JLabel ausgabeFormatLabel = new JLabel();
  JComboBox ausgabeFormat = new JComboBox();
  JLabel ausgabeDateiLabel = new JLabel();
  JTextField ausgabeDatei = new JTextField();
  JButton ausgabeDateiButton = new JButton();
  JButton speichernButton = new JButton();
  JButton bSpeichernButton = new JButton();
  JButton wSpeichernButton = new JButton();
  JButton wErweiterteAusgabe = new JButton();
  JCheckBox ausDauer = new JCheckBox();
  JCheckBox ausKmH = new JCheckBox();
  JCheckBox graDauer = new JCheckBox();
  JCheckBox graKmH = new JCheckBox();
  JTextField maxBalkenDauer = new JTextField();
  JTextField maxBalkenKmH = new JTextField();
  JCheckBox mAusDauer = new JCheckBox();
  JCheckBox mAusKmH = new JCheckBox();
  JCheckBox mGraAusDauer = new JCheckBox();
  JCheckBox mGraAusKmH = new JCheckBox();
  JTextField mGraSizeDauer = new JTextField();
  JTextField mGraSizeKmH = new JTextField();
  JLabel fahrtartLabel = new JLabel();
  JScrollPane fahrtartScrollPane = new JScrollPane();
  JList nurFahrtart = new JList();
  JLabel jLabel6 = new JLabel();
  JScrollPane jScrollPane4 = new JScrollPane();
  JList mNurFahrtart = new JList();
  JPanel rudererOptionenPanel = new JPanel();
  BorderLayout borderLayout14 = new BorderLayout();
  JPanel booteOptionenPanel = new JPanel();
  BorderLayout borderLayout15 = new BorderLayout();
  JCheckBox zeitVorjahresvergleich = new JCheckBox();
  JCheckBox bzeitVorjahresvergleich = new JCheckBox();
  JLabel separatorLabel1 = new JLabel();
  JLabel separatorLabel2 = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();
  JCheckBox ausWafaKm = new JCheckBox();
  JLabel nurNameAuswahlLabel = new JLabel();
  JRadioButton nurAuswahlName = new JRadioButton();
  JRadioButton nurAuswahlGruppe = new JRadioButton();
  ButtonGroup nurAuswahl = new ButtonGroup();


  // Konstruktor
  public StatistikFrame(EfaFrame parent) {
    super(parent);
    frIni();
  }

  // Konstruktor
  public StatistikFrame(JDialog parent) {
    super(parent);
    frIni();
  }

  private void frIni() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    Dialog.frameOpened(this);
    try {
      jbInit();
      Statistik.isCreateRunning = false; // nur zur Sicherheit, falls durch eine Exception dieser Wert noch auf "true" steht
      if (!Daten.efaConfig.showBerlinOptions.getValue()) {
          ausZielfahrten.setVisible(false);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
    Dialog.statistikFrame = this;

    erweitertFrame = new StatistikErweitertFrame(this);
    Dimension dlgSize = erweitertFrame.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    erweitertFrame.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    erweitertFrame.setModal(!Dialog.tourRunning);

    itemsIni();
    erstellenBut.requestFocus();
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


  // Initialisierung
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    bArt.setNextFocusableComponent(bzeitVorjahresvergleich);
    bArt.setMaximumRowCount(16);
    art.setNextFocusableComponent(zeitVorjahresvergleich);
    art.setMaximumRowCount(17);
    erstellenBut.setNextFocusableComponent(ausgabeFormat);
    Mnemonics.setButton(this, erstellenBut, International.getStringWithMnemonic("Statistik erstellen"));
    erstellenBut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        erstellenBut_actionPerformed(e);
      }
    });
    this.setTitle(International.getString("Statistik erstellen"));
    this.getContentPane().setLayout(borderLayout1);
    rudererPanel.setLayout(borderLayout2);
    Mnemonics.setLabel(this, artLabel, International.getStringWithMnemonic("Statistikart")+": ");
    artLabel.setLabelFor(art);
    artPanel.setLayout(flowLayout1);
    rest1Panel.setLayout(borderLayout3);
    zeitraumPanel.setLayout(flowLayout2);
    zeitraumLabel.setText(International.getString("Zeitraum für Auswertung")+": ");
    Mnemonics.setLabel(this, vonLabel, International.getStringWithMnemonic("vom")+" ");
    vonLabel.setLabelFor(von);
    von.setNextFocusableComponent(bis);
    von.setPreferredSize(new Dimension(80, 19));
    von.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        von_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, bisLabel, " "+International.getStringWithMnemonic("bis")+" ");
    bisLabel.setLabelFor(bis);
    bis.setNextFocusableComponent(zeitFbUebergreifend);
    bis.setPreferredSize(new Dimension(80, 19));
    bis.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        bis_focusLost(e);
      }
    });
    auswahlLabel.setHorizontalAlignment(SwingConstants.CENTER);
    auswahlLabel.setText(International.getString("nur Fahrten berechnen für")+":");
    auswahlPanel.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, geschlechtLabel, International.getStringWithMnemonic("Geschlecht")+": ");
    geschlechtLabel.setLabelFor(nurGeschlecht);
    rest1Panel.setMinimumSize(new Dimension(100, 40));
    rest1Panel.setPreferredSize(new Dimension(100, 40));
    geschlechtScrollPane.setMaximumSize(new Dimension(140, 120));
    geschlechtScrollPane.setPreferredSize(new Dimension(90, 120));
    Mnemonics.setLabel(this, status1Label, International.getStringWithMnemonic("Status")+": ");
    status1Label.setLabelFor(nurStatus1);
    status1ScrollPane.setPreferredSize(new Dimension(90, 120));
    Mnemonics.setLabel(this, nurNameLabel, International.getStringWithMnemonic("Nur Name")+": ");
    nurNameLabel.setLabelFor(nurName);
    nurName.setNextFocusableComponent(nameTeil);
    nurName.setPreferredSize(new Dimension(100, 19));
    nurName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        nurName_keyReleased(e);
      }
    });
    nurName.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nurName_focusLost(e);
      }
    });
    auswahlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    auswahlPane.setPreferredSize(new Dimension(750, 390));
    zeitraumPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    artPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    ausgabePanel.setLayout(gridBagLayout2);
    Mnemonics.setButton(this, ausKm, International.getStringWithMnemonic("Kilometer"));
    ausKm.setNextFocusableComponent(ausRudKm);
    ausKm.setSelected(true);
    Mnemonics.setButton(this, ausName, International.getStringWithMnemonic("Name"));
    ausName.setNextFocusableComponent(ausJahrgang);
    ausName.setSelected(true);
    Mnemonics.setButton(this, ausJahrgang, International.getStringWithMnemonic("Jahrgang"));
    ausJahrgang.setNextFocusableComponent(ausStatus1);
    Mnemonics.setButton(this, ausStatus1, International.getStringWithMnemonic("Status"));
    ausStatus1.setNextFocusableComponent(ausZielfahrten);
    Mnemonics.setButton(this, ausZielfahrten, International.onlyFor("Zielfahrten","de"));
    ausZielfahrten.setNextFocusableComponent(ausWafaKm);
    Mnemonics.setLabel(this, ausgabeLabel, International.getStringWithMnemonic("Ausgabe")+": ");
    ausgabeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    ausgabeLabel.setLabelFor(ausgabePanel);
    Mnemonics.setButton(this, ausRudKm, International.getStringWithMnemonic("Ruderkilometer"));
    ausRudKm.setNextFocusableComponent(ausStmKm);
    Mnemonics.setButton(this, ausStmKm, International.getStringWithMnemonic("Steuerkilometer"));
    ausStmKm.setNextFocusableComponent(ausFahrten);
    Mnemonics.setButton(this, ausFahrten, International.getStringWithMnemonic("Fahrten"));
    ausFahrten.setNextFocusableComponent(ausKmFahrt);
    ausFahrten.setSelected(true);
    Mnemonics.setButton(this, ausKmFahrt, International.getStringWithMnemonic("Km/Fahrt"));
    ausKmFahrt.setNextFocusableComponent(ausDauer);
    ausKmFahrt.setSelected(true);
    ausgabePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    ausgabePanel.setPreferredSize(new Dimension(245, 201));
    rest2Panel.setLayout(borderLayout4);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("graphische Ausgabe")+": ");
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setLabelFor(graAusgabePanel);
    graAusgabePanel.setLayout(gridBagLayout3);
    graAusgabePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    graAusgabePanel.setMinimumSize(new Dimension(183, 185));
    graAusgabePanel.setPreferredSize(new Dimension(183, 185));
    graKm.setNextFocusableComponent(maxBalkenKm);
    graKm.setSelected(true);
    Mnemonics.setButton(this, graKm, International.getStringWithMnemonic("Kilometer"));
    maxBalkenKm.setNextFocusableComponent(graRudKm);
    maxBalkenKm.setPreferredSize(new Dimension(40, 19));
    maxBalkenKm.setText("200");
    maxBalkenKm.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    maxBalkenKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        maxBalkenKm_focusLost(e);
      }
    });
    Mnemonics.setButton(this, graRudKm, International.getStringWithMnemonic("Ruderkilometer"));
    graRudKm.setNextFocusableComponent(maxBalkenRudKm);
    Mnemonics.setButton(this, graStmKm, International.getStringWithMnemonic("Steuerkilometer"));
    graStmKm.setNextFocusableComponent(maxBalkenStmKm);
    Mnemonics.setButton(this, graFahrten, International.getStringWithMnemonic("Fahrten"));
    graFahrten.setNextFocusableComponent(maxBalkenFahrten);
    Mnemonics.setButton(this, graKmFahrt, International.getStringWithMnemonic("Km/Fahrt"));
    graKmFahrt.setNextFocusableComponent(maxBalkenKmFahrt);
    maxBalkenRudKm.setNextFocusableComponent(graStmKm);
    maxBalkenRudKm.setPreferredSize(new Dimension(40, 19));
    maxBalkenRudKm.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    maxBalkenRudKm.setText("200");
    maxBalkenRudKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        maxBalkenRudKm_focusLost(e);
      }
    });
    maxBalkenStmKm.setNextFocusableComponent(graFahrten);
    maxBalkenStmKm.setPreferredSize(new Dimension(40, 19));
    maxBalkenStmKm.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    maxBalkenStmKm.setText("200");
    maxBalkenStmKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        maxBalkenStmKm_focusLost(e);
      }
    });
    maxBalkenFahrten.setNextFocusableComponent(graKmFahrt);
    maxBalkenFahrten.setPreferredSize(new Dimension(40, 19));
    maxBalkenFahrten.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    maxBalkenFahrten.setText("200");
    maxBalkenFahrten.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        maxBalkenFahrten_focusLost(e);
      }
    });
    maxBalkenKmFahrt.setNextFocusableComponent(graDauer);
    maxBalkenKmFahrt.setPreferredSize(new Dimension(40, 19));
    maxBalkenKmFahrt.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    maxBalkenKmFahrt.setText("200");
    maxBalkenKmFahrt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        maxBalkenKmFahrt_focusLost(e);
      }
    });
    rest3Panel.setLayout(borderLayout5);
    sortPanel.setLayout(gridBagLayout4);
    sortPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    sortPanel.setPreferredSize(new Dimension(245, 65));
    Mnemonics.setLabel(this, sortFolgeLabel, International.getStringWithMnemonic("Reihenfolge")+": ");
    sortFolgeLabel.setLabelFor(sortFolge);
    Mnemonics.setLabel(this, sortLabel, International.getStringWithMnemonic("Sortierung")+": ");
    sortLabel.setHorizontalAlignment(SwingConstants.CENTER);
    Mnemonics.setLabel(this, sortKritLabel, International.getStringWithMnemonic("Kriterium")+": ");
    sortKritLabel.setLabelFor(sortKrit);
    numPanel.setLayout(gridBagLayout5);
    Mnemonics.setLabel(this, numLabel, International.getStringWithMnemonic("Numerierung")+": ");
    numLabel.setLabelFor(numerierung);
    numPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    numPanel.setPreferredSize(new Dimension(163, 65));
    numScrollPane.setMinimumSize(new Dimension(200, 40));
    numScrollPane.setPreferredSize(new Dimension(200, 40));
    art.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        art_itemStateChanged(e);
      }
    });
    Mnemonics.setButton(this, nameTeil, International.getStringWithMnemonic("als Teil eines Namens"));
    nameTeil.setNextFocusableComponent(ausName);
    createPanel.setLayout(borderLayout6);
    dateiPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    dateiPanel.setLayout(gridBagLayout6);
    outputLabel.setText(International.getString("Daten ausgeben")+":");
    gespeichertPanel.setLayout(borderLayout7);
    bootePanel.setLayout(borderLayout8);
    Mnemonics.setLabel(this, bArtLabel, International.getStringWithMnemonic("Statistikart")+": ");
    bArtLabel.setLabelFor(bArt);
    bRest1Panel.setLayout(borderLayout9);
    bNurPanel.setLayout(gridBagLayout7);
    bNurLabel.setText(International.getString("nur Fahrten berechnen für")+":");
    Mnemonics.setLabel(this, bNurArtLabel, International.getStringWithMnemonic("Art")+": ");
    bNurArtLabel.setLabelFor(mNurArt);
    Mnemonics.setLabel(this, bNurAnzahlLabel, International.getStringWithMnemonic("Ruderplätze")+": ");
    bNurAnzahlLabel.setLabelFor(mNurAnzahl);
    jScrollPane1.setMinimumSize(new Dimension(100, 131));
    jScrollPane1.setPreferredSize(new Dimension(100, 131));
    jScrollPane2.setMinimumSize(new Dimension(100, 131));
    jScrollPane2.setPreferredSize(new Dimension(100, 131));
    mNurSkull.setNextFocusableComponent(mNurRiemen);
    mNurSkull.setSelected(true);
    mNurSkull.setText(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SCULL));
    mNurRiemen.setNextFocusableComponent(mNurAndere1);
    mNurRiemen.setSelected(true);
    mNurRiemen.setText(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, EfaTypes.TYPE_RIGGING_SWEEP));
    mNurMitStm.setNextFocusableComponent(mNurOhneStm);
    mNurMitStm.setSelected(true);
    mNurMitStm.setText(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXED));
    mNurOhneStm.setNextFocusableComponent(mNurAndere2);
    mNurOhneStm.setSelected(true);
    mNurOhneStm.setText(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, EfaTypes.TYPE_COXING_COXLESS));
    Mnemonics.setLabel(this, mNurNameLabel, International.getStringWithMnemonic("nur Boot")+": ");
    mNurNameLabel.setLabelFor(mNurBoot);
    mNurBoot.setMinimumSize(new Dimension(100, 19));
    mNurBoot.setNextFocusableComponent(mAusName);
    mNurBoot.setPreferredSize(new Dimension(150, 19));
    mNurBoot.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        mNurBoot_keyReleased(e);
      }
    });
    mNurBoot.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        mNurBoot_focusLost(e);
      }
    });
    bNurPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    bNurPanel.setPreferredSize(new Dimension(311, 260));
    mRest2Panel.setLayout(borderLayout10);
    mAusPanel.setLayout(borderLayout11);
    Mnemonics.setLabel(this, mAusgabeLabel, International.getStringWithMnemonic("Ausgabe")+": ");
    mAusgabeLabel.setLabelFor(mAusPanel);
    Mnemonics.setLabel(this, mGraAusLabel, International.getStringWithMnemonic("graphische Ausgabe")+": ");
    mGraAusLabel.setLabelFor(mGraPanel);
    mAusgabePanel.setLayout(gridBagLayout8);
    mGraPanel.setLayout(gridBagLayout9);
    Mnemonics.setButton(this, mAusName, International.getStringWithMnemonic("Name"));
    mAusName.setNextFocusableComponent(mAusArt);
    mAusName.setSelected(true);
    Mnemonics.setButton(this, mAusArt, International.getStringWithMnemonic("Art"));
    mAusArt.setNextFocusableComponent(mAusBez);
    Mnemonics.setButton(this, mAusBez, International.getStringWithMnemonic("Bootsbezeichnung"));
    mAusBez.setNextFocusableComponent(mAusKm);
    Mnemonics.setButton(this, mAusKm, International.getStringWithMnemonic("Kilometer"));
    mAusKm.setNextFocusableComponent(mAusFahrten);
    mAusKm.setSelected(true);
    Mnemonics.setButton(this, mAusFahrten, International.getStringWithMnemonic("Fahrten"));
    mAusFahrten.setNextFocusableComponent(mAusKmFahrt);
    mAusFahrten.setSelected(true);
    Mnemonics.setButton(this, mAusKmFahrt, International.getStringWithMnemonic("Km/Fahrt"));
    mAusKmFahrt.setNextFocusableComponent(mAusDauer);
    mAusKmFahrt.setSelected(true);
    mAusgabePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    Mnemonics.setButton(this, mGraAusKm, International.getStringWithMnemonic("Kilometer"));
    mGraAusKm.setNextFocusableComponent(mGraSizeKm);
    mGraAusKm.setSelected(true);
    Mnemonics.setButton(this, mGraAusFahrten, International.getStringWithMnemonic("Fahrten"));
    mGraAusFahrten.setNextFocusableComponent(mGraSizeFahrten);
    Mnemonics.setButton(this, mGraAusKmFahrt, International.getStringWithMnemonic("Km/Fahrt"));
    mGraAusKmFahrt.setNextFocusableComponent(mGraSizeKmFahrt);
    mGraSizeLabel.setText(International.getString("Größe")+":");
    mGraSizeKmFahrt.setNextFocusableComponent(mGraAusDauer);
    mGraSizeKmFahrt.setPreferredSize(new Dimension(40, 19));
    mGraSizeKmFahrt.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    mGraSizeKmFahrt.setText("200");
    mGraSizeFahrten.setNextFocusableComponent(mGraAusKmFahrt);
    mGraSizeFahrten.setPreferredSize(new Dimension(40, 19));
    mGraSizeFahrten.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    mGraSizeFahrten.setText("200");
    mGraSizeKm.setNextFocusableComponent(mGraAusFahrten);
    mGraSizeKm.setPreferredSize(new Dimension(40, 19));
    mGraSizeKm.setToolTipText(International.getString("legt die Größe des Balkens für 100% fest (Angabe in Pixeln)"));
    mGraSizeKm.setText("200");
    mGraPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    mRest4Panel.setLayout(borderLayout12);
    mNumPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    mNumPanel.setLayout(gridBagLayout11);
    mSortPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    mSortPanel.setPreferredSize(new Dimension(235, 75));
    mSortPanel.setLayout(gridBagLayout10);
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText(International.getString("Sortierung"));
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Numerierung"));
    jLabel4.setLabelFor(mNumPanel);
    Mnemonics.setLabel(this, mSortKritLabel, International.getStringWithMnemonic("Kriterium")+": ");
    mSortKritLabel.setLabelFor(mSortKrit);
    Mnemonics.setLabel(this, mSortFolgeLabel, International.getStringWithMnemonic("Reihenfolge")+": ");
    mSortFolgeLabel.setLabelFor(mSortFolge);
    Mnemonics.setButton(this, mNurEigene, International.getStringWithMnemonic("eigene"));
    mNurEigene.setNextFocusableComponent(mNumFremde);
    mNurEigene.setSelected(true);
    Mnemonics.setButton(this, mNurFremde, International.getStringWithMnemonic("fremde Boote"));
    mNurFremde.setNextFocusableComponent(mNurMitStm);
    mNurFremde.setSelected(true);
    Mnemonics.setButton(this, mNumEigene, International.getStringWithMnemonic("eigene Boote"));
    mNumEigene.setNextFocusableComponent(mNumFremde);
    mNumEigene.setSelected(true);
    Mnemonics.setButton(this, mNumFremde, International.getStringWithMnemonic("fremde Boote"));
    mNumFremde.setNextFocusableComponent(bErweitertButton);
    Mnemonics.setButton(this, mNurAndere1, International.getStringWithMnemonic("andere"));
    mNurAndere1.setNextFocusableComponent(mNumEigene);
    mNurAndere1.setSelected(true);
    Mnemonics.setButton(this, mNurAndere2, International.getStringWithMnemonic("andere"));
    mNurAndere2.setNextFocusableComponent(mNurBoot);
    mNurAndere2.setSelected(true);
    rudererPanel.setNextFocusableComponent(art);
    bootePanel.setNextFocusableComponent(bArt);
    numerierung.setNextFocusableComponent(erweitertButton);
    Mnemonics.setButton(this, deleteButton, International.getStringWithMnemonic("Löschen"));
    deleteButton.setMaximumSize(new Dimension(102, 25));
    deleteButton.setMinimumSize(new Dimension(102, 25));
    deleteButton.setNextFocusableComponent(statList);
    deleteButton.setPreferredSize(new Dimension(102, 25));
    deleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteButton_actionPerformed(e);
      }
    });
    gespeichertPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(ComponentEvent e) {
        gespeichertPanel_componentShown(e);
      }
      public void componentHidden(ComponentEvent e) {
        gespeichertPanel_componentHidden(e);
      }
    });
    Mnemonics.setButton(this, ausAnzVersch, International.getStringWithMnemonic("Anzahl Verschiedene"));
    ausAnzVersch.setNextFocusableComponent(graKm);
    Mnemonics.setButton(this, editButton, International.getStringWithMnemonic("Bearbeiten"));
    editButton.setMaximumSize(new Dimension(102, 25));
    editButton.setMinimumSize(new Dimension(102, 25));
    editButton.setNextFocusableComponent(deleteButton);
    editButton.setPreferredSize(new Dimension(102, 25));
    editButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editButton_actionPerformed(e);
      }
    });
    jPanel3.setLayout(gridBagLayout12);
    statList.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        statList_mouseClicked(e);
      }
    });
    Mnemonics.setButton(this, zeitFbUebergreifend, International.getStringWithMnemonic("fahrtenbuchübergreifend"));
    zeitFbUebergreifend.setNextFocusableComponent(auswahlPane);
    wettPanel.setLayout(gridBagLayout13);
    Mnemonics.setLabel(this, wettProzLabel, " "+International.getStringWithMnemonic("Prozent der geforderten Kilometer"));
    wettProzLabel.setLabelFor(wettProz);
    Mnemonics.setButton(this, ausWettBedingung, International.getStringWithMnemonic("Wettbewerbsbedingungen ausgeben"));
    ausWettBedingung.setNextFocusableComponent(ausWettOhneDetails);
    wettProz.setNextFocusableComponent(wettAnz);
    wettProz.setPreferredSize(new Dimension(50, 19));
    wettProz.setText("60");
    wettProz.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettProz_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, wettAnzLabel, " "+International.getStringWithMnemonic("der geforderten Fahrten"));
    wettAnzLabel.setLabelFor(wettAnz);
    wettAnz.setNextFocusableComponent(ausWettBedingung);
    wettAnz.setPreferredSize(new Dimension(50, 19));
    wettAnz.setText("4");
    wettAnz.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettAnz_focusLost(e);
      }
    });
    wettTitleLabel.setPreferredSize(new Dimension(300, 19));
    wettTitleLabel.setText(International.getString("Ausgabe nur, wenn mindestens erfüllt sind")+":");
    wettbewerbPanel.setLayout(borderLayout13);
    Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Wettbewerb")+": ");
    wettbewerbPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(ComponentEvent e) {
        wettbewerbPanel_componentShown(e);
      }
      public void componentHidden(ComponentEvent e) {
        wettbewerbPanel_componentHidden(e);
      }
    });
    wettPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    Mnemonics.setLabel(this, jLabel5, "  "+International.getStringWithMnemonic("Wettbewerbsjahr")+": ");
    jLabel5.setLabelFor(wettJahr);
    wettJahr.setNextFocusableComponent(wettProz);
    wettJahr.setPreferredSize(new Dimension(100, 19));
    wettJahr.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettJahr_focusLost(e);
      }
    });
    Mnemonics.setButton(this, ausWettOhneDetails, International.getStringWithMnemonic("nur Zusammenfassung ausgeben"));
    ausWettOhneDetails.setNextFocusableComponent(wErweiterteAusgabe);
    sortKrit.setMinimumSize(new Dimension(130, 24));
    sortKrit.setNextFocusableComponent(sortFolge);
    sortKrit.setPreferredSize(new Dimension(160, 24));
    sortKrit.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        sortKrit_itemStateChanged(e);
      }
    });
    mSortKrit.setNextFocusableComponent(mSortFolge);
    mSortKrit.setPreferredSize(new Dimension(130, 24));
    mSortKrit.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        mSortKrit_itemStateChanged(e);
      }
    });
    mSortFolge.setNextFocusableComponent(mNumEigene);
    mSortFolge.setPreferredSize(new Dimension(130, 24));
    erweitertButton.setNextFocusableComponent(speichernButton);
    Mnemonics.setButton(this, erweitertButton, International.getStringWithMnemonic("erweiterte Ausgabe ..."));
    erweitertButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        erweitertButton_actionPerformed(e);
      }
    });
    bErweitertButton.setNextFocusableComponent(bSpeichernButton);
    Mnemonics.setButton(this, bErweitertButton, International.getStringWithMnemonic("erweiterte Ausgabe ..."));
    bErweitertButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        erweitertButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, ausgabeArtLabel, International.getStringWithMnemonic("Ausgabeart")+": ");
    ausgabeArtLabel.setLabelFor(ausgabeArt);
    Mnemonics.setLabel(this, ausgabeFormatLabel, International.getStringWithMnemonic("Formatierung")+": ");
    ausgabeFormatLabel.setLabelFor(ausgabeFormat);
    Mnemonics.setLabel(this, ausgabeDateiLabel, International.getStringWithMnemonic("Ausgabe in Datei")+": ");
    ausgabeDateiLabel.setLabelFor(ausgabeDatei);
    ausgabeDateiButton.setMaximumSize(new Dimension(59, 20));
    ausgabeDateiButton.setMinimumSize(new Dimension(59, 20));
    ausgabeDateiButton.setNextFocusableComponent(erstellenBut);
    ausgabeDateiButton.setPreferredSize(new Dimension(59, 20));
    ausgabeDateiButton.setIcon(new ImageIcon(StatistikFrame.class.getResource("/de/nmichael/efa/img/prog_save.gif")));
    ausgabeDateiButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ausgabeDateiButton_actionPerformed(e);
      }
    });
    ausgabeArt.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        ausgabeArt_itemStateChanged(e);
      }
    });
    speichernButton.setNextFocusableComponent(art);
    Mnemonics.setButton(this, speichernButton, International.getStringWithMnemonic("Statistikeinstellungen speichern"));
    speichernButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        speichernButton_actionPerformed(e);
      }
    });
    bSpeichernButton.setNextFocusableComponent(bArt);
    Mnemonics.setButton(this, bSpeichernButton, International.getStringWithMnemonic("Statistikeinstellungen speichern"));
    bSpeichernButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        speichernButton_actionPerformed(e);
      }
    });
    erweitertButtonPanel.setPreferredSize(new Dimension(566, 33));
    ausgabeArt.setMinimumSize(new Dimension(130, 22));
    ausgabeArt.setNextFocusableComponent(ausgabeFormat);
    ausgabeArt.setPreferredSize(new Dimension(200, 22));
    ausgabeArt.setMaximumRowCount(10);
    ausgabeFormat.setMinimumSize(new Dimension(130, 22));
    ausgabeFormat.setNextFocusableComponent(ausgabeDatei);
    ausgabeFormat.setPreferredSize(new Dimension(200, 22));
    ausgabeFormat.setMaximumRowCount(7);
    wSpeichernButton.setNextFocusableComponent(wettArt);
    Mnemonics.setButton(this, wSpeichernButton, International.getStringWithMnemonic("Statistikeinstellungen speichern"));
    wSpeichernButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        wSpeichernButton_actionPerformed(e);
      }
    });
    wErweiterteAusgabe.setNextFocusableComponent(wSpeichernButton);
    Mnemonics.setButton(this, wErweiterteAusgabe, International.getStringWithMnemonic("erweiterte Ausgabe ..."));
    wErweiterteAusgabe.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        erweitertButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, ausDauer, International.getStringWithMnemonic("Dauer")
            + " (" + International.getString("Stunden") +"): ");
    ausDauer.setNextFocusableComponent(ausKmH);
    Mnemonics.setButton(this, ausKmH, International.getStringWithMnemonic("Km/h"));
    ausKmH.setNextFocusableComponent(ausAnzVersch);
    Mnemonics.setButton(this, graDauer, International.getStringWithMnemonic("Dauer")
            + " (" + International.getString("Stunden") +"): ");
    graDauer.setNextFocusableComponent(maxBalkenDauer);
    Mnemonics.setButton(this, graKmH, International.getStringWithMnemonic("Km/h"));
    graKmH.setNextFocusableComponent(maxBalkenKmH);
    maxBalkenDauer.setNextFocusableComponent(graKmH);
    maxBalkenDauer.setPreferredSize(new Dimension(40, 17));
    maxBalkenDauer.setText("200");
    maxBalkenKmH.setNextFocusableComponent(sortKrit);
    maxBalkenKmH.setPreferredSize(new Dimension(40, 17));
    maxBalkenKmH.setText("200");
    Mnemonics.setButton(this, mAusDauer, International.getStringWithMnemonic("Dauer"));
    mAusDauer.setNextFocusableComponent(mAusKmH);
    Mnemonics.setButton(this, mAusKmH, International.getStringWithMnemonic("Km/h"));
    mAusKmH.setNextFocusableComponent(mGraAusKm);
    Mnemonics.setButton(this, mGraAusDauer, International.getStringWithMnemonic("Dauer"));
    mGraAusDauer.setNextFocusableComponent(mGraSizeDauer);
    Mnemonics.setButton(this, mGraAusKmH, International.getStringWithMnemonic("Km/h"));
    mGraAusKmH.setNextFocusableComponent(mGraSizeKmH);
    mGraSizeDauer.setNextFocusableComponent(mGraAusKmH);
    mGraSizeDauer.setPreferredSize(new Dimension(40, 17));
    mGraSizeDauer.setText("200");
    mGraSizeKmH.setNextFocusableComponent(mSortKrit);
    mGraSizeKmH.setPreferredSize(new Dimension(40, 17));
    mGraSizeKmH.setText("200");
    Mnemonics.setLabel(this, fahrtartLabel, International.getStringWithMnemonic("Art der Fahrt")+": ");
    fahrtartLabel.setLabelFor(nurFahrtart);
    fahrtartScrollPane.setPreferredSize(new Dimension(90, 120));
    Mnemonics.setLabel(this, jLabel6, International.getStringWithMnemonic("Art der Fahrt")+": ");
    jLabel6.setLabelFor(mNurFahrtart);
    jScrollPane4.setPreferredSize(new Dimension(100, 131));
    rudererOptionenPanel.setLayout(borderLayout14);
    booteOptionenPanel.setLayout(borderLayout15);
    nurGeschlecht.setNextFocusableComponent(nurStatus1);
    nurStatus1.setNextFocusableComponent(nurFahrtart);
    nurFahrtart.setNextFocusableComponent(nurName);
    sortFolge.setNextFocusableComponent(numerierung);
    mNurArt.setNextFocusableComponent(mNurAnzahl);
    mNurAnzahl.setNextFocusableComponent(mNurFahrtart);
    mNurFahrtart.setNextFocusableComponent(mNurSkull);
    wettbewerbPanel.setNextFocusableComponent(wettArt);
    wettArt.setNextFocusableComponent(wettJahr);
    gespeichertPanel.setNextFocusableComponent(statList);
    statList.setNextFocusableComponent(editButton);
    ausgabeDatei.setNextFocusableComponent(ausgabeDateiButton);
    Mnemonics.setButton(this, zeitVorjahresvergleich, International.getStringWithMnemonic("Vorjahresvergleich"));
    zeitVorjahresvergleich.setNextFocusableComponent(nurGeschlecht);
    zeitVorjahresvergleich.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        zeitVorjahresvergleich_actionPerformed(e);
      }
    });
    bzeitVorjahresvergleich.setNextFocusableComponent(mNurArt);
    Mnemonics.setButton(this, bzeitVorjahresvergleich, International.getStringWithMnemonic("Vorjahresvergleich"));
    bzeitVorjahresvergleich.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        zeitVorjahresvergleich_actionPerformed(e);
      }
    });
    separatorLabel1.setPreferredSize(new Dimension(50, 0));
    separatorLabel2.setPreferredSize(new Dimension(50, 0));
    Mnemonics.setButton(this, ausWafaKm, International.getStringWithMnemonic("Wafa-Km"));
    ausWafaKm.setNextFocusableComponent(ausKm);
    nurNameAuswahlLabel.setText(International.getString("Auswahl")+": ");
    nurAuswahlName.setSelected(true);
    nurAuswahlName.setText(International.getString("Name"));
    nurAuswahlGruppe.setText(International.getString("Gruppe"));
    nurAuswahl.add(nurAuswahlName);
    nurAuswahl.add(nurAuswahlGruppe);
    nurAuswahlGruppe.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        nurAuswahlGruppe_itemStateChanged(e);
      }
    });
    this.getContentPane().add(zeitraumPanel, BorderLayout.NORTH);
    zeitraumPanel.add(zeitraumLabel, null);
    zeitraumPanel.add(vonLabel, null);
    zeitraumPanel.add(von, null);
    zeitraumPanel.add(bisLabel, null);
    zeitraumPanel.add(bis, null);
    zeitraumPanel.add(zeitFbUebergreifend, null);



    createPanel.add(erstellenBut, BorderLayout.SOUTH);
    this.getContentPane().add(createPanel, BorderLayout.SOUTH);
    createPanel.add(dateiPanel, BorderLayout.CENTER);
    dateiPanel.add(outputLabel,         new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    dateiPanel.add(ausgabeArtLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(ausgabeArt, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(ausgabeFormatLabel,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    dateiPanel.add(ausgabeFormat, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(auswahlPane, BorderLayout.CENTER);
    auswahlPane.add(rudererPanel, International.getString("Mannschafts-Kilometer"));
    rudererPanel.add(artPanel, BorderLayout.NORTH);
    artPanel.add(artLabel, null);
    artPanel.add(art, null);
    rudererPanel.add(rest1Panel, BorderLayout.CENTER);
    rest1Panel.add(auswahlPanel, BorderLayout.WEST);
/*
    auswahlPanel.add(auswahlLabel,    new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0));
    auswahlPanel.add(geschlechtLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(geschlechtScrollPane,    new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(status1Label,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurNameLabel,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurName,       new GridBagConstraints(1, 4, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(status1ScrollPane,      new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nameTeil,    new GridBagConstraints(1, 5, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(fahrtartLabel,     new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(fahrtartScrollPane,  new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurNameAuswahlLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
    auswahlPanel.add(jRadioButton1,  new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
    auswahlPanel.add(jRadioButton2,     new GridBagConstraints(3, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
*/
    auswahlPanel.add(auswahlLabel,    new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(geschlechtLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(geschlechtScrollPane,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(status1Label,    new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(status1ScrollPane,      new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(fahrtartLabel,      new GridBagConstraints(2, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(fahrtartScrollPane,  new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurNameAuswahlLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurAuswahlName,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurAuswahlGruppe,     new GridBagConstraints(2, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurNameLabel,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nurName,        new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(nameTeil,    new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtartScrollPane.getViewport().add(nurFahrtart, null);
    rest1Panel.add(rest2Panel, BorderLayout.CENTER);
    rudererOptionenPanel.add(ausgabePanel, BorderLayout.WEST);
    ausgabePanel.add(ausName,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausJahrgang,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausStatus1,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausZielfahrten,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausgabeLabel,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausKm,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausRudKm,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausStmKm,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausFahrten,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausKmFahrt,   new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausAnzVersch,    new GridBagConstraints(0, 11, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausDauer,   new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausKmH,  new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    ausgabePanel.add(ausWafaKm,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererOptionenPanel.add(graAusgabePanel, BorderLayout.CENTER);
    graAusgabePanel.add(jLabel2, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graKm, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenKm, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graRudKm, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graStmKm, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graFahrten, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graKmFahrt, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenRudKm, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenStmKm, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenFahrten, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenKmFahrt, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graDauer,  new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(graKmH,  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rudererOptionenPanel.add(rest3Panel, BorderLayout.SOUTH);
    rest3Panel.add(sortPanel, BorderLayout.WEST);
    sortPanel.add(sortKrit, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    sortPanel.add(sortFolgeLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    sortPanel.add(sortLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    sortPanel.add(sortKritLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    sortPanel.add(sortFolge, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rest3Panel.add(numPanel, BorderLayout.CENTER);
    numPanel.add(numLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    numPanel.add(numScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    erweitertButtonPanel.add(erweitertButton, null);
    erweitertButtonPanel.add(speichernButton, null);
    rest2Panel.add(rudererOptionenPanel, BorderLayout.CENTER);
    rest2Panel.add(erweitertButtonPanel, BorderLayout.SOUTH);
    auswahlPane.add(bootePanel, International.getString("Boots-Kilometer"));
    bootePanel.add(bArtPanel, BorderLayout.NORTH);
    bArtPanel.add(bArtLabel, null);
    bArtPanel.add(bArt, null);
    bArtPanel.add(separatorLabel2, null);
    bootePanel.add(bRest1Panel, BorderLayout.CENTER);
    bRest1Panel.add(bNurPanel, BorderLayout.WEST);
    bNurPanel.add(bNurLabel,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    bNurPanel.add(bNurArtLabel,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(bNurAnzahlLabel,     new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(jScrollPane1,   new GridBagConstraints(0, 2, 1, 6, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(jScrollPane2,     new GridBagConstraints(1, 2, 3, 6, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurSkull,    new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurNameLabel,    new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurBoot,    new GridBagConstraints(1, 11, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurRiemen,    new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurMitStm,    new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurOhneStm,    new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurEigene,     new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurFremde,      new GridBagConstraints(1, 9, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurAndere1,    new GridBagConstraints(2, 8, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(mNurAndere2,    new GridBagConstraints(2, 10, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(jLabel6,  new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    bNurPanel.add(jScrollPane4, new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane4.getViewport().add(mNurFahrtart, null);
    bRest1Panel.add(mRest2Panel, BorderLayout.CENTER);
    booteOptionenPanel.add(mAusPanel,  BorderLayout.NORTH);
    mAusPanel.add(mAusgabePanel, BorderLayout.WEST);
    mAusgabePanel.add(mAusgabeLabel,  new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    mAusgabePanel.add(mAusName,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusArt,  new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusBez,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusKm,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusFahrten,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusKmFahrt,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusDauer,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusgabePanel.add(mAusKmH,  new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mAusPanel.add(mGraPanel, BorderLayout.CENTER);
    mGraPanel.add(mGraAusLabel,  new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    mGraPanel.add(mGraAusKm,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraAusFahrten,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraAusKmFahrt,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraSizeKm,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraSizeFahrten,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraSizeKmFahrt,  new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraSizeLabel,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    booteOptionenPanel.add(mRest4Panel, BorderLayout.CENTER);
    mRest4Panel.add(mSortPanel, BorderLayout.WEST);
    mSortPanel.add(jLabel3, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    mSortPanel.add(mSortKritLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mSortPanel.add(mSortFolgeLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mSortPanel.add(mSortKrit, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mSortPanel.add(mSortFolge, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mRest4Panel.add(mNumPanel, BorderLayout.CENTER);
    mNumPanel.add(jLabel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    mNumPanel.add(mNumEigene, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mNumPanel.add(mNumFremde, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mErweitertButtonPanel.add(bErweitertButton, null);
    mErweitertButtonPanel.add(bSpeichernButton, null);
    mRest2Panel.add(booteOptionenPanel, BorderLayout.CENTER);
    mRest2Panel.add(mErweitertButtonPanel, BorderLayout.SOUTH);
    auswahlPane.add(wettbewerbPanel, International.getString("Wettbewerbe"));
    wettbewerbPanel.add(jPanel1, BorderLayout.NORTH);
    wettbewerbPanel.add(wettPanel, BorderLayout.CENTER);

    jPanel1.add(jLabel1, null);
    jPanel1.add(wettArt, null);
    jPanel1.add(jLabel5, null);
    jPanel1.add(wettJahr, null);
    wettArt.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        wettArt_itemStateChanged(e);
      }
    });
    auswahlPane.add(gespeichertPanel, International.getString("gespeicherte Statistikeinstellungen"));
    gespeichertPanel.add(jScrollPane3, BorderLayout.CENTER);
    gespeichertPanel.add(jPanel3, BorderLayout.EAST);
    jPanel3.add(editButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(deleteButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane3.getViewport().add(statList, null);
    jScrollPane2.getViewport().add(mNurAnzahl, null);
    jScrollPane1.getViewport().add(mNurArt, null);
    numScrollPane.getViewport().add(numerierung, null);
    status1ScrollPane.getViewport().add(nurStatus1, null);
    geschlechtScrollPane.getViewport().add(nurGeschlecht, null);
    wettPanel.add(wettProzLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(wettProz, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(wettAnzLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPane.setSelectedComponent(rudererPanel);
    bArt.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        bArt_itemStateChanged(e);
      }
    });
    wettPanel.add(wettAnz, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(wettTitleLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    wettPanel.add(ausWettOhneDetails, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(ausWettBedingung, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(30, 0, 0, 0), 0, 0));
    wettbewerbPanel.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(wErweiterteAusgabe, null);
    jPanel2.add(wSpeichernButton, null);
    dateiPanel.add(ausgabeDateiLabel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(ausgabeDatei,   new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(ausgabeDateiButton, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenDauer, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    graAusgabePanel.add(maxBalkenKmH, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraAusDauer,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraAusKmH,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraSizeDauer,  new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mGraPanel.add(mGraSizeKmH,   new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    artPanel.add(separatorLabel1, null);
    artPanel.add(zeitVorjahresvergleich, null);
    bArtPanel.add(bzeitVorjahresvergleich, null);
  }


  // Initialisierung bestimmter Komponenten
  public void itemsIni() {
    // Ausgabe-Frame im Hintergrund erstellen
    AusgabeFrame dlg = new AusgabeFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    Dialog.programOut = dlg.out;
    Dialog.programOutText = dlg.outText;
    Dialog.programDlg = dlg;

    sortFolge.setPreferredSize(new Dimension(160, 24));
    sortFolge.addItem(International.getString("aufsteigend"));
    sortFolge.addItem(International.getString("absteigend"));

    art.addItem(International.getString("Ruderer/Innen"));
    art.addItem(International.getString("Status"));
    art.addItem(International.getString("Jahrgang"));
    art.addItem(International.getString("Geschlecht"));
    art.addItem(International.getString("Ziele"));
    art.addItem(International.getString("Km/Fahrt"));
    art.addItem(International.getString("Monate"));
    art.addItem(International.getString("Wochentage"));
    art.addItem(International.getString("Tageszeit"));
    art.addItem(International.getString("Boote"));
    art.addItem(International.getString("Bootsart"));
    art.addItem(International.getString("Fahrtart"));
    art.addItem(International.getString("Mitruderer/Innen"));
    art.addItem(International.getString("Wer mit Wem"));
    art.addItem(International.getString("Wer Wohin"));
    art.addItem(International.getString("Wer mit Bootsart"));
    art.addItem(International.getString("Fahrtenbuch"));
    art.addItem(International.getString("Jahre"));
    art.addItem(International.getString("Monatsübersicht"));
    art.addItem(International.getString("Wer unerlaubt"));
    art.addItem(International.getString("Wer mit Fahrtart"));
    art.setSelectedIndex(0);

    nurGeschlecht.setListData(Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_GENDER));
    nurGeschlecht.setSelectionInterval(0,Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER)-1);

    nurStatus1.setListData(Daten.fahrtenbuch.getDaten().status);
    nurStatus1.setSelectionInterval(0,Daten.fahrtenbuch.getDaten().status.length-2);

    nurFahrtart.setListData(Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_SESSION));
    nurFahrtart.setSelectionInterval(0,Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION)-1);
    mNurFahrtart.setListData(Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_SESSION));
    mNurFahrtart.setSelectionInterval(0,Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION)-1);

    for (int i=0; i<ausgabeArten.length; i++)
      ausgabeArt.addItem(ausgabeArten[i]);
    ausgabeArt.setSelectedIndex(0);

    mSortFolge.addItem(International.getString("aufsteigend"));
    mSortFolge.addItem(International.getString("absteigend"));

    bArt.addItem(International.getString("Boote"));
    bArt.addItem(International.getString("Art"));
    bArt.addItem(International.getString("Ruderplätze"));
    bArt.addItem(International.getString("Art") + " - " +
            International.getString("Detail"));
    bArt.addItem(International.getString("Ziele"));
    bArt.addItem(International.getString("Km/Fahrt"));
    bArt.addItem(International.getString("Monate"));
    bArt.addItem(International.getString("Wochentage"));
    bArt.addItem(International.getString("Tageszeit"));
    bArt.addItem(International.getString("Ruderer"));
    bArt.addItem(International.getString("Welches Boot Wohin"));
    bArt.addItem(International.getString("Fahrtenbuch"));
    bArt.addItem(International.getString("Jahre"));
    bArt.setSelectedIndex(0);

    mNurArt.setListData(Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_BOAT));
    mNurArt.setSelectionInterval(0,Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT)-1);

    mNurAnzahl.setListData(Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_NUMSEATS));
    mNurAnzahl.setSelectionInterval(0,Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS)-1);

    if (Daten.wettDefs != null) {
      for (int i=0; i<WettDefs.ANZWETT; i++) {
        WettDef w = Daten.wettDefs.getWettDef(i,9999);
        if (w != null) wettArt.addItem(w.name); // Namen der Wettbewerbe für höchstmögliches Jahr (falls sich Namen ändern)!
      }
      if (wettArt.getItemCount()>0) wettArt.setSelectedIndex(0);
    }

    if (Daten.fahrtenbuch.getDaten().statistik != null && Daten.fahrtenbuch.getDaten().statistik.getExact(StatAddFrame.DEFAULT) != null) {
      DatenFelder f = (DatenFelder)Daten.fahrtenbuch.getDaten().statistik.getComplete();
      setSavedStat(f,true);
    }

  }


  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }


  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  // Fenster schließen
  public void cancel() {
    Dialog.frameClosed(this);
    Dialog.statistikFrame = null;
    dispose();
  }



  // ausgeblendete Panels wieder anzeigen
  void gespeichertPanel_componentHidden(ComponentEvent e) {
    zeitraumPanel.setVisible(true);
    dateiPanel.setVisible(true);
 }




  // Auswahl der Statistikart geändert --> Listen anpassen
  void art_itemStateChanged(ItemEvent e) {
    sortKrit.removeAllItems();
    switch (art.getSelectedIndex()) {
      case StatistikDaten.ART_MITGLIEDER:
        sortKrit.addItem(International.getString("Nachname"));
        sortKrit.addItem(International.getString("Vorname"));
        sortKrit.addItem(International.getString("Jahrgang"));
        break;
      case StatistikDaten.ART_ZIELE:
        sortKrit.addItem(International.getString("Ziel"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_WOTAGE:
        sortKrit.addItem(International.getString("Tag") +
                " (" + International.getString("alphabetisch") + ")");
        sortKrit.addItem("---");
        sortKrit.addItem(International.getString("Wochentag"));
        break;
      case StatistikDaten.ART_TAGESZEIT:
        sortKrit.addItem(International.getString("Zeit") +
                " (" + International.getString("alphabetisch") + ")");
        sortKrit.addItem("---");
        sortKrit.addItem(International.getString("Tageszeit"));
        break;
      case StatistikDaten.ART_MONATE:
        sortKrit.addItem(International.getString("Monat") +
                " (" + International.getString("alphabetisch") + ")");
        sortKrit.addItem("---");
        sortKrit.addItem(International.getString("Monat"));
        break;
      case StatistikDaten.ART_BOOTE:
        sortKrit.addItem(International.getString("Boot"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_BOOTSART:
        sortKrit.addItem(International.getString("Bootsart"));
        sortKrit.addItem(International.getString("Art"));
        sortKrit.addItem(International.getString("Ruderplätze"));
        break;
      case StatistikDaten.ART_FAHRTART:
        sortKrit.addItem(International.getString("Fahrtart"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_MITRUDERER:
        sortKrit.addItem(International.getString("Nachname"));
        sortKrit.addItem(International.getString("Vorname"));
        sortKrit.addItem(International.getString("Jahrgang"));
        break;
      case StatistikDaten.ART_STATUS:
        sortKrit.addItem(International.getString("Status"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_JAHRGANG:
        sortKrit.addItem(International.getString("Jahrgang"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_GESCHLECHT:
        sortKrit.addItem(International.getString("Geschlecht"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_WERMITWEM: case StatistikDaten.ART_WERWOHIN:
      case StatistikDaten.ART_WERMITBOOTSART: case StatistikDaten.ART_WERMITFAHRTART:
      case StatistikDaten.ART_WERUNERLAUBT:
        sortKrit.addItem(International.getString("Nachname"));
        sortKrit.addItem(International.getString("Vorname"));
        sortKrit.addItem(International.getString("Jahrgang"));
        break;
      case StatistikDaten.ART_JAHRE:
        sortKrit.addItem(International.getString("Jahr"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      case StatistikDaten.ART_FAHRTENBUCH:
        sortKrit.addItem(International.getString("Datum"));
        sortKrit.addItem("---");
        sortKrit.addItem(International.getString("Lfd. Nr."));
        break;
       case StatistikDaten.ART_MONATSUEBERSICHT:
        sortKrit.addItem(International.getString("Datum"));
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
      default:
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        sortKrit.addItem("---");
        break;
    }
    if (art.getSelectedIndex() != StatistikDaten.ART_FAHRTENBUCH && art.getSelectedIndex() != StatistikDaten.ART_MONATSUEBERSICHT) {
      sortKrit.addItem(International.getString("Kilometer"));
      sortKrit.addItem(International.getString("Fahrten"));
      sortKrit.addItem(International.getString("Km/Fahrt"));
      sortKrit.addItem(International.getString("Dauer"));
      sortKrit.addItem(International.getString("Km/h"));
    }
    if (art.getSelectedIndex() == StatistikDaten.ART_WERMITWEM ||
        art.getSelectedIndex() == StatistikDaten.ART_WERWOHIN ||
        art.getSelectedIndex() == StatistikDaten.ART_WERMITBOOTSART ||
        art.getSelectedIndex() == StatistikDaten.ART_WERMITFAHRTART ||
        art.getSelectedIndex() == StatistikDaten.ART_WERUNERLAUBT)
      sortKrit.addItem(International.getString("Anzahl Verschiedene"));

    if (art.getSelectedIndex() == StatistikDaten.ART_MITGLIEDER ||
        art.getSelectedIndex() == StatistikDaten.ART_MITRUDERER) {
      sortKrit.addItem("---");
      sortKrit.addItem(International.getString("Status"));
    }

    switch (art.getSelectedIndex()) {
      case StatistikDaten.ART_MITGLIEDER:
        sortKrit.setSelectedIndex(3);
        sortFolge.setSelectedIndex(1);
        break;
      case StatistikDaten.ART_ZIELE:
        sortKrit.setSelectedIndex(4);
        sortFolge.setSelectedIndex(1);
        break;
      case StatistikDaten.ART_WOTAGE:
      case StatistikDaten.ART_MONATE:
      case StatistikDaten.ART_TAGESZEIT:
        sortKrit.setSelectedIndex(2);
        sortFolge.setSelectedIndex(0);
        break;
      case StatistikDaten.ART_BOOTE:
      case StatistikDaten.ART_BOOTSART:
      case StatistikDaten.ART_FAHRTART:
        sortKrit.setSelectedIndex(3);
        sortFolge.setSelectedIndex(1);
        break;
      case StatistikDaten.ART_MITRUDERER:
        sortKrit.setSelectedIndex(3);
        sortFolge.setSelectedIndex(1);
        break;
      case StatistikDaten.ART_STATUS:
        sortKrit.setSelectedIndex(3);
        sortFolge.setSelectedIndex(1);
        break;
      case StatistikDaten.ART_JAHRGANG: case StatistikDaten.ART_JAHRE:
        sortKrit.setSelectedIndex(0);
        sortFolge.setSelectedIndex(0);
        break;
      case StatistikDaten.ART_GESCHLECHT:
        sortKrit.setSelectedIndex(3);
        sortFolge.setSelectedIndex(1);
        break;
      case StatistikDaten.ART_WERMITWEM: case StatistikDaten.ART_WERWOHIN:
      case StatistikDaten.ART_WERMITBOOTSART: case StatistikDaten.ART_WERMITFAHRTART:
      case StatistikDaten.ART_WERUNERLAUBT:
      case StatistikDaten.ART_FAHRTENBUCH: case StatistikDaten.ART_MONATSUEBERSICHT:
        sortKrit.setSelectedIndex(0);
        sortFolge.setSelectedIndex(0);
        break;
      case StatistikDaten.ART_KMFAHRT:
        sortKrit.setSelectedIndex(5);
        sortFolge.setSelectedIndex(0);
        break;
    }
    if (art.getSelectedIndex() != StatistikDaten.ART_MITGLIEDER &&
        art.getSelectedIndex() != StatistikDaten.ART_MITRUDERER &&
        art.getSelectedIndex() != StatistikDaten.ART_WERMITWEM &&
        art.getSelectedIndex() != StatistikDaten.ART_WERMITBOOTSART &&
        art.getSelectedIndex() != StatistikDaten.ART_WERMITFAHRTART &&
        art.getSelectedIndex() != StatistikDaten.ART_WERWOHIN &&
        art.getSelectedIndex() != StatistikDaten.ART_WERUNERLAUBT) {
      ausJahrgang.setSelected(false);
      ausStatus1.setSelected(false);
      if (art.getSelectedIndex() != StatistikDaten.ART_ZIELE) ausZielfahrten.setSelected(false);
      String[] g = new String[1];
      g[0] = International.getString("alle");
      numerierung.setListData(g);
    } else {
      numerierung.setListData(Daten.fahrtenbuch.getDaten().status);
      if (art.getSelectedIndex() != StatistikDaten.ART_WERMITWEM &&
          art.getSelectedIndex() != StatistikDaten.ART_WERWOHIN &&
          art.getSelectedIndex() != StatistikDaten.ART_WERMITBOOTSART &&
          art.getSelectedIndex() != StatistikDaten.ART_WERMITFAHRTART &&
          art.getSelectedIndex() != StatistikDaten.ART_WERUNERLAUBT)
        numerierung.setSelectionInterval(0,Daten.fahrtenbuch.getDaten().status.length-3);
    }
    if (art.getSelectedIndex() == StatistikDaten.ART_WERMITWEM ||
        art.getSelectedIndex() == StatistikDaten.ART_WERWOHIN ||
        art.getSelectedIndex() == StatistikDaten.ART_WERMITBOOTSART ||
        art.getSelectedIndex() == StatistikDaten.ART_WERMITFAHRTART ||
        art.getSelectedIndex() == StatistikDaten.ART_WERUNERLAUBT) {
      ausAnzVersch.setVisible(true);
    } else {
      ausAnzVersch.setVisible(false);
    }

    if (art.getSelectedIndex() == StatistikDaten.ART_FAHRTENBUCH || art.getSelectedIndex() == StatistikDaten.ART_MONATSUEBERSICHT) {
//      rudererOptionenPanel.setVisible(false);
      ausgabePanel.setVisible(false);
      graAusgabePanel.setVisible(false);
      numPanel.setVisible(false);
    } else {
//      rudererOptionenPanel.setVisible(true);
      ausgabePanel.setVisible(true);
      graAusgabePanel.setVisible(true);
      numPanel.setVisible(true);
    }

    if (art.getSelectedIndex() != StatistikDaten.ART_MITGLIEDER) {
      ausWafaKm.setSelected(false);
      ausWafaKm.setVisible(false);
    } else {
      ausWafaKm.setVisible(true);
    }
  }



  // Auswahlliste Art (Boote) geändert
  void bArt_itemStateChanged(ItemEvent e) {
    mSortKrit.removeAllItems();
    switch (bArt.getSelectedIndex()+100) {
      case StatistikDaten.BART_BOOTE: case StatistikDaten.BART_WELCHESWOHIN:
        mSortKrit.addItem(International.getString("Bootsname"));
        mSortKrit.addItem(International.getString("Art"));
        mSortKrit.addItem(International.getString("Ruderplätze"));
        break;
      case StatistikDaten.BART_ART:
        mSortKrit.addItem(International.getString("Art"));
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        break;
      case StatistikDaten.BART_PLAETZE:
        mSortKrit.addItem(International.getString("Ruderplätze"));
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        break;
      case StatistikDaten.BART_ARTDETAIL:
        mSortKrit.addItem(International.getString("Art") + " - " +
                International.getString("Detail"));
        mSortKrit.addItem(International.getString("Art"));
        mSortKrit.addItem(International.getString("Ruderplätze"));
        break;
      case StatistikDaten.BART_ZIELE:
        mSortKrit.addItem(International.getString("Ziel"));
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        break;
      case StatistikDaten.BART_MONATE:
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        mSortKrit.addItem(International.getString("Monat"));
        break;
      case StatistikDaten.BART_WOTAGE:
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        mSortKrit.addItem(International.getString("Wochentag"));
        break;
      case StatistikDaten.BART_TAGESZEIT:
        sortKrit.addItem(International.getString("Zeit") +
                " (" + International.getString("alphabetisch") + ")");
        sortKrit.addItem("---");
        sortKrit.addItem(International.getString("Tageszeit"));
        break;
      case StatistikDaten.BART_RUDERER:
        mSortKrit.addItem(International.getString("Name"));
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        break;
      case StatistikDaten.BART_KMFAHRT:
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        break;
      case StatistikDaten.BART_JAHRE:
        mSortKrit.addItem(International.getString("Jahr"));
        mSortKrit.addItem("---");
        mSortKrit.addItem("---");
        break;
      case StatistikDaten.BART_FAHRTENBUCH:
        mSortKrit.addItem(International.getString("Datum"));
        mSortKrit.addItem("---");
        mSortKrit.addItem(International.getString("Lfd. Nr."));
        break;
    }
    if (bArt.getSelectedIndex()+100 != StatistikDaten.BART_FAHRTENBUCH) {
      mSortKrit.addItem(International.getString("Kilometer"));
      mSortKrit.addItem(International.getString("Fahrten"));
      mSortKrit.addItem(International.getString("Km/Fahrt"));
      mSortKrit.addItem(International.getString("Dauer"));
      mSortKrit.addItem(International.getString("Km/h"));
    }
    switch (bArt.getSelectedIndex()+100) {
      case StatistikDaten.BART_MONATE:
      case StatistikDaten.BART_WOTAGE:
      case StatistikDaten.BART_TAGESZEIT:
        mSortKrit.setSelectedIndex(2);
        mSortFolge.setSelectedIndex(0);
        break;
      case StatistikDaten.BART_KMFAHRT:
        mSortKrit.setSelectedIndex(5);
        mSortFolge.setSelectedIndex(0);
        break;
      case StatistikDaten.BART_FAHRTENBUCH:
      case StatistikDaten.BART_JAHRE:
        mSortKrit.setSelectedIndex(0);
        mSortFolge.setSelectedIndex(0);
        break;
      case StatistikDaten.BART_WELCHESWOHIN:
        sortKrit.setSelectedIndex(0);
        sortFolge.setSelectedIndex(0);
        break;
      default:
        mSortKrit.setSelectedIndex(3);
        mSortFolge.setSelectedIndex(1);
    }
    if (bArt.getSelectedIndex()+100 != StatistikDaten.BART_BOOTE) {
      mAusArt.setSelected(false);
      mAusBez.setSelected(false);
    }
    if (bArt.getSelectedIndex()+100 == StatistikDaten.BART_FAHRTENBUCH) {
//      booteOptionenPanel.setVisible(false);
      mAusgabePanel.setVisible(false);
      mGraPanel.setVisible(false);
      mNumPanel.setVisible(false);
    } else {
//      booteOptionenPanel.setVisible(true);
      mAusgabePanel.setVisible(true);
      mGraPanel.setVisible(true);
      mNumPanel.setVisible(true);
    }

    if (bArt.getSelectedIndex()+100 == StatistikDaten.BART_BOOTE || bArt.getSelectedIndex()+100 == StatistikDaten.BART_RUDERER)
      mNumEigene.setSelected(true);
    else {
      mNumEigene.setSelected(false); mNumFremde.setSelected(false);
    }

  }

  // Sortierkriterium geändert --> ggf. Sortierreihenfolge anpassen
  void sortKrit_itemStateChanged(ItemEvent e) {
    if (sortKrit.getSelectedIndex()<3) sortFolge.setSelectedIndex(0);
    else sortFolge.setSelectedIndex(1);
  }

  // Sortierkriterium geändert --> ggf. Sortierreihenfolge anpassen
  void mSortKrit_itemStateChanged(ItemEvent e) {
    if (mSortKrit.getSelectedIndex()<3) mSortFolge.setSelectedIndex(0);
    else mSortFolge.setSelectedIndex(1);
  }


  // Auswahl der Statistikart geändert --> Listen anpassen
  void wettArt_itemStateChanged(ItemEvent e) {
    wettJahr.setText("");
    getWettJahr();
    if (wettArt.getSelectedIndex() + 200 == StatistikDaten.WETT_LRVBWINTER) {
      wettAnz.setVisible(true); wettAnzLabel.setVisible(true);
    } else {
      wettAnz.setVisible(false); wettAnzLabel.setVisible(false);
    }
    if (wettArt.getSelectedIndex() + 200 == StatistikDaten.WETT_LRVBWIMPEL ||
        wettArt.getSelectedIndex() + 200 == StatistikDaten.WETT_DRV_WAFASTATISTIK) {
      wettProz.setVisible(false); wettProzLabel.setVisible(false);
      wettTitleLabel.setVisible(false);
    } else {
      wettProz.setVisible(true); wettProzLabel.setVisible(true);
      wettTitleLabel.setVisible(true);
    }
  }



  // zu aktuellem Datum das beste passende Wettbewerbsjahr für einen gegebenen Wettbewerb ermitteln
  public static int getBestWettJahr(WettDef wett, int year, Calendar cal) {
    if (wett == null) return year;

    int bismon = wett.bis.monat + 2;
    int mon = cal.get(Calendar.MONTH) + 1 - cal.getMinimum(Calendar.MONTH);
    if (mon <= bismon &&
        wett.von.jahr != wett.bis.jahr) year--;
    else if (bismon>12 && mon <= bismon-12) year--;
    return year;
  }



  // Wettbewerbsjahr bestimmen
  void getWettJahr() {
    if (!wettbewerbPanel.isShowing()) return;
    if (Daten.wettDefs == null) return;

    // Wettjahr ermitteln: Immer bis 2 Monate nach Ende des Wettzeitraums noch vorigen Wettbewerb vorschlagen!
    WettDef wett = null;
    Calendar cal = GregorianCalendar.getInstance();
    int year = EfaUtil.string2date(wettJahr.getText().trim(),-1,-1,-1).tag;
    if (year == -1) {
      year = cal.get(Calendar.YEAR);
      wett = Daten.wettDefs.getWettDef(wettArt.getSelectedIndex(),year);
      if (wett == null) return;
      year = getBestWettJahr(wett,year,cal);
    }
    wett = Daten.wettDefs.getWettDef(wettArt.getSelectedIndex(),year); // anhand des ermittelten Jahres jetzt nochmal die gültigen Wettbewerbsbedinungen holen
    if (wett == null) return;

    TMJ tmj = EfaUtil.string2date(wettJahr.getText(),-1,0,0);
    if (tmj.tag>=0 && tmj.tag<100) tmj.tag += 1900;
    if (tmj.tag>=0 && tmj.tag<1980) tmj.tag += 100;
    if (tmj.tag>=0) year = tmj.tag;
    von.setText(wett.von.tag+"."+
                wett.von.monat+"."+
                (wett.von.jahr+year));
    bis.setText(wett.bis.tag+"."+
                wett.bis.monat+"."+
                (wett.bis.jahr+year));
    if (wett.von.jahr == wett.bis.jahr) {
      wettJahr.setText(Integer.toString(year+wett.von.jahr));
      zeitFbUebergreifend.setSelected(false);
    } else {
      wettJahr.setText((year+wett.von.jahr)+"/"+
                       (year+wett.bis.jahr));
      zeitFbUebergreifend.setSelected(true);
    }
  }



  // Wettbewerbsjahr vervollständigen
  void wettJahr_focusLost(FocusEvent e) {
    getWettJahr();
  }



  // Wettbewerbs-Panel sichtbar geworden oder nicht
  void wettbewerbPanel_componentShown(ComponentEvent e) {
    wettbewerbPanel.add(auswahlPanel,BorderLayout.WEST);
    wettbewerbPanel.doLayout();
    wettPanel.doLayout();
    jPanel2.doLayout();
    auswahlPanel.doLayout();
    getWettJahr();
    if (!statWettFromSavedValues)
      nurStatus1.setSelectionInterval(0,Daten.fahrtenbuch.getDaten().status.length-3); // Gast standardmäßig nicht ausgeben
    statWettFromSavedValues = false;
    ausgabeArt.addItem(International.getString("als Meldedatei"));
    zeitVorjahresvergleich.setEnabled(false);
    bzeitVorjahresvergleich.setEnabled(false);
  }
  void wettbewerbPanel_componentHidden(ComponentEvent e) {
    rest1Panel.add(auswahlPanel,BorderLayout.WEST);
    ausgabeArt.removeItem(International.getString("als Meldedatei"));
    zeitVorjahresvergleich.setEnabled(true);
    bzeitVorjahresvergleich.setEnabled(true);
  }






  // Von-Datum korrigieren
  void von_focusLost(FocusEvent e) {
    if (von.getText().trim().equals("")) return;
    Calendar cal = GregorianCalendar.getInstance();
    TMJ c = EfaUtil.correctDate(von.getText().trim(),
            cal.get(Calendar.DATE),cal.get(Calendar.MONTH)+1-cal.getMinimum(Calendar.MONTH),cal.get(Calendar.YEAR));
    von.setText(c.tag+"."+c.monat+"."+c.jahr);
  }



  // Bis-Datum korrigieren
  void bis_focusLost(FocusEvent e) {
    if (bis.getText().trim().equals("")) return;
    Calendar cal = GregorianCalendar.getInstance();
    TMJ c = EfaUtil.correctDate(bis.getText().trim(),
            cal.get(Calendar.DATE),cal.get(Calendar.MONTH)+1-cal.getMinimum(Calendar.MONTH),cal.get(Calendar.YEAR));
    bis.setText(c.tag+"."+c.monat+"."+c.jahr);
  }



  // Balken-Werte korrigieren
  void maxBalkenKm_focusLost(FocusEvent e) {
    maxBalkenKm.setText(Integer.toString(EfaUtil.string2int(maxBalkenKm.getText().trim(),200)));
  }
  void maxBalkenRudKm_focusLost(FocusEvent e) {
    maxBalkenRudKm.setText(Integer.toString(EfaUtil.string2int(maxBalkenRudKm.getText().trim(),200)));
  }
  void maxBalkenStmKm_focusLost(FocusEvent e) {
    maxBalkenStmKm.setText(Integer.toString(EfaUtil.string2int(maxBalkenStmKm.getText().trim(),200)));
  }
  void maxBalkenFahrten_focusLost(FocusEvent e) {
    maxBalkenFahrten.setText(Integer.toString(EfaUtil.string2int(maxBalkenFahrten.getText().trim(),200)));
  }
  void maxBalkenKmFahrt_focusLost(FocusEvent e) {
    maxBalkenKmFahrt.setText(Integer.toString(EfaUtil.string2int(maxBalkenKmFahrt.getText().trim(),200)));
  }



  // Namen vervollständigen
  void nurName_keyReleased(KeyEvent e) {
    if (nurAuswahlName.isSelected()) {
      if (nameTeil.isSelected()) return;
      if (Daten.fahrtenbuch.getDaten().mitglieder == null) return;
      if (nurName.getText().trim().equals("")) return;
      EfaFrame.vervollstaendige(nurName,null,Daten.fahrtenbuch.getDaten().mitglieder,e,null,true);
    }
    if (nurAuswahlGruppe.isSelected()) {
      if (Daten.gruppen == null) return;
      if (gruppen == null) {
        Vector g = Daten.gruppen.getGruppen();
        gruppen = new DatenListe("foo",1,1,false);
        for (int i=0; i<g.size(); i++) {
          DatenFelder d = new DatenFelder(1);
          d.set(0,(String)g.get(i));
          gruppen.add(d);
        }
      }
      if (nurName.getText().trim().equals("")) return;
      EfaFrame.vervollstaendige(nurName,null,gruppen,e,null,true);
    }
  }



  // Bootsnamen vervollständigen
  void mNurBoot_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch.getDaten().boote == null) return;
    if (mNurBoot.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(mNurBoot,null,Daten.fahrtenbuch.getDaten().boote,e,null,true);
  }



  // Dateibutton geklickt
  void ausgabeDateiButton_actionPerformed(ActionEvent e) {
    if (ausgabeExt[ausgabeArt.getSelectedIndex()] == null) return; // sollte eigentlich nicht vorkommen, aber sicher ist sicher...


    String ext = ausgabeExt[ausgabeArt.getSelectedIndex()];
    String dat;
    if (!ausgabeDatei.getText().trim().equals("")) {
        dat = Dialog.dateiDialog(this,ext.toUpperCase()+"-"+
                International.getMessage("{item} auswählen",
                International.getString("Datei")),
                ext.toUpperCase()+"-"+International.getString("Datei")+" (*."+ext+")",
                ext,ausgabeDatei.getText().trim(),null,
                International.getMessage("{item} auswählen",
                International.getString("Datei")),
                true,false);
    } else {
        dat = Dialog.dateiDialog(this,ext.toUpperCase()+"-"+
                International.getMessage("{item} auswählen",
                International.getString("Datei")),
                ext.toUpperCase()+"-"+
                International.getString("Datei")+" (*."+ext+")",ext,null,null,
                International.getMessage("{item} auswählen",
                International.getString("Datei")),
                true,false);
    }

    if (dat != null) {
      ausgabeDatei.setText(dat);
    }
  }




  // Wett-Felder Werte korrigieren
  void wettProz_focusLost(FocusEvent e) {
    TMJ eing = EfaUtil.string2date(wettProz.getText().trim(),0,0,0);
    if (eing.tag<0) eing.tag = 0;
    if (eing.tag>100) eing.tag = 100;
    wettProz.setText(Integer.toString(eing.tag));
  }
  void wettAnz_focusLost(FocusEvent e) {
    TMJ eing = EfaUtil.string2date(wettAnz.getText().trim(),0,0,0);
    if (eing.tag<0) eing.tag = 0;
    if (eing.tag>8) eing.tag = 8;
    wettAnz.setText(Integer.toString(eing.tag));
  }


  // erweiterte Ausgabeeinstellungen ansehen
  void erweitertButton_actionPerformed(ActionEvent e) {
    int stat = auswahlPane.getSelectedIndex();
    int art=0;
    switch (stat) {
      case StatistikDaten.STAT_MITGLIEDER: art = this.art.getSelectedIndex(); break;
      case StatistikDaten.STAT_BOOTE: art = bArt.getSelectedIndex() + 100; break;
      case StatistikDaten.STAT_WETT: art = wettArt.getSelectedIndex() + 200; break;
    }

    // je nach aktuell ausgewählter Statistik die Elemente des erweitertFrame ggf. in grau darstellen
    if (art == StatistikDaten.ART_KMFAHRT || art == StatistikDaten.BART_KMFAHRT) erweitertFrame.kilometerGruppiert.setForeground(Color.black);
    else erweitertFrame.kilometerGruppiert.setForeground(Color.gray);

    if (art == StatistikDaten.ART_MITGLIEDER || art == StatistikDaten.ART_MITRUDERER ||
        art == StatistikDaten.ART_WERMITWEM  || art == StatistikDaten.ART_WERWOHIN ||
        art == StatistikDaten.ART_WERMITBOOTSART || art == StatistikDaten.ART_WERMITFAHRTART ||
        art == StatistikDaten.ART_WERUNERLAUBT ||
        art == StatistikDaten.BART_RUDERER) {
      erweitertFrame.zusammenGaesteAndere.setForeground(Color.black);
      erweitertFrame.gaesteVereinsweiseZusammen.setForeground(Color.black);
    } else {
      erweitertFrame.zusammenGaesteAndere.setForeground(Color.gray);
      erweitertFrame.gaesteVereinsweiseZusammen.setForeground(Color.gray);
    }

    if (art == StatistikDaten.ART_WERWOHIN || art == StatistikDaten.BART_WELCHESWOHIN ||
        art == StatistikDaten.ART_ZIELE || art == StatistikDaten.BART_ZIELE) erweitertFrame.teilzieleEinzeln.setForeground(Color.black);
    else erweitertFrame.teilzieleEinzeln.setForeground(Color.gray);

    if (art == StatistikDaten.ART_WERMITWEM) erweitertFrame.horiz_alle.setForeground(Color.black);
    else erweitertFrame.horiz_alle.setForeground(Color.gray);

    if (stat != StatistikDaten.STAT_WETT) erweitertFrame.auchNullWerte.setForeground(Color.black);
    else erweitertFrame.auchNullWerte.setForeground(Color.gray);

    if (stat == StatistikDaten.STAT_WETT) erweitertFrame.alleZielfahrtenAusgeben.setForeground(Color.black);
    else erweitertFrame.alleZielfahrtenAusgeben.setForeground(Color.gray);

    if (ausgabeArt.getSelectedIndex() == StatistikDaten.AUSGABE_XML ||
        ausgabeFormat.getSelectedIndex() > 0) erweitertFrame.xmlImmerAlle.setForeground(Color.black);
    else erweitertFrame.xmlImmerAlle.setForeground(Color.gray);

    if (art == StatistikDaten.ART_MITGLIEDER ||
        art == StatistikDaten.ART_MITRUDERER ||
        art == StatistikDaten.ART_WERMITBOOTSART ||
        art == StatistikDaten.ART_WERMITFAHRTART ||
        art == StatistikDaten.ART_WERMITWEM ||
        art == StatistikDaten.ART_WERUNERLAUBT ||
        art == StatistikDaten.ART_WERWOHIN ||
        art == StatistikDaten.BART_RUDERER) erweitertFrame.mitglnrStattName.setForeground(Color.black);
    else erweitertFrame.mitglnrStattName.setForeground(Color.gray);

    if (art == StatistikDaten.ART_FAHRTENBUCH || art == StatistikDaten.BART_FAHRTENBUCH || art == StatistikDaten.ART_MONATSUEBERSICHT ||
        stat == StatistikDaten.STAT_WETT) {
      erweitertFrame.crop.setForeground(Color.gray);
      erweitertFrame.maxKmLabel.setForeground(Color.gray);
      erweitertFrame.maxRudKmLabel.setForeground(Color.gray);
      erweitertFrame.maxStmKmLabel.setForeground(Color.gray);
      erweitertFrame.maxFahrtenLabel.setForeground(Color.gray);
      erweitertFrame.maxKmFahrtLabel.setForeground(Color.gray);
      erweitertFrame.maxDauerLabel.setForeground(Color.gray);
      erweitertFrame.maxKmHLabel.setForeground(Color.gray);
      erweitertFrame.zusammengefassteWerteOhneBalken.setForeground(Color.gray);
    } else {
      erweitertFrame.crop.setForeground(Color.black);
      erweitertFrame.maxKmLabel.setForeground(Color.black);
      erweitertFrame.maxRudKmLabel.setForeground(Color.black);
      erweitertFrame.maxStmKmLabel.setForeground(Color.black);
      erweitertFrame.maxFahrtenLabel.setForeground(Color.black);
      erweitertFrame.maxKmFahrtLabel.setForeground(Color.black);
      erweitertFrame.maxDauerLabel.setForeground(Color.black);
      erweitertFrame.maxKmHLabel.setForeground(Color.black);
      erweitertFrame.zusammengefassteWerteOhneBalken.setForeground(Color.black);
    }

    erweitertFrame.okButton.requestFocus();
    erweitertFrame.show();
  }



  // Statistik bearbeiten
  void editButton_actionPerformed(ActionEvent e) {
    doEdit();
  }



  // Statistik bearbeiten bei Doppelklick
  void statList_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) doEdit();
  }


  // Ermittelt, ob eine Output-Datei, die für d erzeugt werden soll, geschrieben, ggf. überschrieben, werden darf
  // wird aus Statistik.java aufgerufen
  public boolean allowedWriteFile(StatistikDaten d) {
    if (d.ausgabeOverwriteWarnung && d.ausgabeDatei != null &&
        d.ausgabeArt != StatistikDaten.AUSGABE_INTERN_GRAFIK && d.ausgabeArt != StatistikDaten.AUSGABE_INTERN_TEXT &&
        new File(d.ausgabeDatei).isFile())
      switch (Dialog.yesNoDialog(International.getString("Datei existiert bereits"),
              International.getMessage("Soll die Datei '{filename}' überschrieben werden?",d.ausgabeDatei))) {
        case Dialog.NO: return false;
      }
    return true;
  }


    // gespeicherte Statistikeinstellungen verwenden
    void createSavedStatistik() {
        String[] s = new String[statList.getSelectedValues().length];
        StatistikDaten[] sd = new StatistikDaten[statList.getSelectedValues().length];
        for (int i = 0; i < statList.getSelectedValues().length; i++) {
            s[i] = (String) statList.getSelectedValues()[i];
        }
        for (int i = 0; i < s.length; i++) {
            try {
                Daten.fahrtenbuch.getDaten().statistik.getFirst(s[i].substring(0, s[i].lastIndexOf(" (")));
                DatenFelder f = (DatenFelder) Daten.fahrtenbuch.getDaten().statistik.getComplete();
                if (f != null) {
                    try {
                        sd[i] = getSavedValues(f);
                        sd[i].statistikFrame = this;
                        sd[i].parent = this;
                        allgStatistikDaten(sd[i]);
                    } catch (StringIndexOutOfBoundsException e) {
                        Dialog.error(International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
                    }
                } else {
                    Dialog.error(International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
                    return;
                }
            } catch (Exception ee) {
                Dialog.error(International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
                return;
            }
        }
        startStatistik(sd);
    }



  // Statistikeinstellungen speichern
  void speichernButton_actionPerformed(ActionEvent e) {
    StatistikDaten d = createStatistik();
    if (d == null) return;
    if (d.ausgabeArt == StatistikDaten.AUSGABE_EFAWETT) {
      Dialog.infoDialog(International.getString("Fehler"),
              "Meldedatei-Ausgaben können nicht als Statistikeinstellung gespeichert werden!");
      return;
    }
    StatAddFrame dlg = new StatAddFrame(this,d,aktStatName,aktStatAuchEfaDirekt);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }
  void wSpeichernButton_actionPerformed(ActionEvent e) {
    speichernButton_actionPerformed(null);
  }



  // gespeicherte Einstellungen anzeigen, restliche Panels ausblenden
  void gespeichertPanel_componentShown(ComponentEvent e) {
    showSavedStat();
  }



  // gespeicherte Einstellungen anzeigen, restliche Panels ausblenden
  void showSavedStat() {
    if (Daten.fahrtenbuch.getDaten().statistik == null) return;
    zeitraumPanel.setVisible(false);
    dateiPanel.setVisible(false);
    statList.removeAll();
    DatenFelder f = null;
    String[] felder = null;
    felder = new String[Daten.fahrtenbuch.getDaten().statistik.countElements()];
    int i=0;
    int art=0;
    do {
      if (i == 0) f = (DatenFelder)Daten.fahrtenbuch.getDaten().statistik.getCompleteFirst();
      else f = (DatenFelder)Daten.fahrtenbuch.getDaten().statistik.getCompleteNext();
      if (f != null && f.get(StatSave.NAMESTAT).equals(StatAddFrame.DEFAULT)) { i++; continue; }
      if (f != null) {
        felder[i] = f.get(StatSave.NAMESTAT);
        if (f.get(StatSave.STAT).equals(Integer.toString(StatistikDaten.STAT_MITGLIEDER)))
          felder[i] = felder[i] + " ("+International.getString("Mannschafts-Kilometer")+":";
        if (f.get(StatSave.STAT).equals(Integer.toString(StatistikDaten.STAT_BOOTE)))
          felder[i] = felder[i] + " ("+International.getString("Boots-Kilometer")+":";
        if (f.get(StatSave.STAT).equals(Integer.toString(StatistikDaten.STAT_WETT)))
          felder[i] = felder[i] + " ("+International.getString("Wettbewerb")+":";


        switch (art = EfaUtil.string2int(f.get(StatSave.ART),StatistikDaten.ART_MITGLIEDER)) {
            case StatistikDaten.ART_MITGLIEDER: case StatistikDaten.BART_RUDERER: felder[i] = felder[i] + International.getString("Ruderer/Innen")+")"; break;
            case StatistikDaten.ART_ZIELE: case StatistikDaten.BART_ZIELE: felder[i] = felder[i] + International.getString("Ziele")+")"; break;
            case StatistikDaten.ART_MONATE: case StatistikDaten.BART_MONATE: felder[i] = felder[i] + International.getString("Monate")+")"; break;
            case StatistikDaten.ART_WOTAGE: case StatistikDaten.BART_WOTAGE: felder[i] = felder[i] + International.getString("Wochentage")+")"; break;
            case StatistikDaten.ART_TAGESZEIT: case StatistikDaten.BART_TAGESZEIT: felder[i] = felder[i] + International.getString("Tageszeit")+")"; break;
            case StatistikDaten.ART_BOOTE: case StatistikDaten.BART_BOOTE: felder[i] = felder[i] + International.getString("Boote")+")"; break;
            case StatistikDaten.ART_BOOTSART: felder[i] = felder[i] + International.getString("Bootsart")+")"; break;
            case StatistikDaten.ART_FAHRTART: felder[i] = felder[i] + International.getString("Fahrtart")+")"; break;
            case StatistikDaten.ART_JAHRE: case StatistikDaten.BART_JAHRE: felder[i] = felder[i] + International.getString("Jahre")+")"; break;
            case StatistikDaten.ART_MITRUDERER: felder[i] = felder[i] + International.getString("Mitruderer")+")"; break;
            case StatistikDaten.ART_STATUS: felder[i] = felder[i] + International.getString("Status")+")"; break;
            case StatistikDaten.ART_JAHRGANG: felder[i] = felder[i] + International.getString("Jahrgang")+")"; break;
            case StatistikDaten.ART_GESCHLECHT: felder[i] = felder[i] + International.getString("Geschlecht")+")"; break;
            case StatistikDaten.ART_WERMITWEM: felder[i] = felder[i] + International.getString("Wer mit Wem")+")"; break;
            case StatistikDaten.ART_WERWOHIN: felder[i] = felder[i] + International.getString("Wer Wohin")+")"; break;
            case StatistikDaten.ART_WERMITBOOTSART: felder[i] = felder[i] + International.getString("Wer mit Bootsart")+")"; break;
            case StatistikDaten.ART_WERMITFAHRTART: felder[i] = felder[i] + International.getString("Wer mit Fahrtart")+")"; break;
            case StatistikDaten.ART_WERUNERLAUBT: felder[i] = felder[i] + International.getString("Wer unerlaubt")+")"; break;
            case StatistikDaten.ART_KMFAHRT: case StatistikDaten.BART_KMFAHRT: felder[i] = felder[i] + International.getString("Km/Fahrt")+")"; break;
            case StatistikDaten.ART_FAHRTENBUCH: case StatistikDaten.BART_FAHRTENBUCH: felder[i] = felder[i] + International.getString("Fahrtenbuch")+")"; break;
            case StatistikDaten.ART_MONATSUEBERSICHT: felder[i] = felder[i] + International.getString("Monatsübersicht")+")"; break;
            case StatistikDaten.BART_ART: felder[i] = felder[i] + International.getString("Art")+")"; break;
            case StatistikDaten.BART_ARTDETAIL: felder[i] = felder[i] + International.getString("Art") + " - " +
                    International.getString("Detail")+")"; break;
            case StatistikDaten.BART_PLAETZE: felder[i] = felder[i] + International.getString("Bootsplätze")+")"; break;
            case StatistikDaten.BART_WELCHESWOHIN: felder[i] = felder[i] + International.getString("Welches Boot Wohin")+")"; break;
          }
          if (art>=200 && art-200<WettDefs.ANZWETT && Daten.wettDefs != null) {
            WettDef w = Daten.wettDefs.getWettDef(art-200,9999);
            felder[i] = felder[i] + (w != null ? w.name : "") +")";
          }
        i++;
      }
    } while (f != null);
    statList.setListData(felder);
  }



  // gespeicherten Eintrag löschen
  void deleteButton_actionPerformed(ActionEvent e) {
    if (statList.getSelectedIndices().length == 0) return;
    switch (Dialog.yesNoDialog(International.getString("Warnung"),
            International.getString("Möchtest Du die markierten Einträge wirklich löschen?"))) {
      case Dialog.YES:
        String[] s = new String[statList.getSelectedValues().length];
        for (int i=0; i<statList.getSelectedValues().length; i++)
          s[i] = (String)statList.getSelectedValues()[i];
        for (int i=0; i<s.length; i++)
          Daten.fahrtenbuch.getDaten().statistik.delete(s[i].substring(0,s[i].lastIndexOf(" (")));
        if (Daten.fahrtenbuch.getDaten().statistik.writeFile() && Daten.fahrtenbuch.getDaten().statistik.readFile()) {}
        else Dialog.error(International.getString("Änderungen konnten nicht gespeichert werden!"));
        showSavedStat();
        break;
      case Dialog.NO: return;
      default: return;
    }
  }


  // boolean-Array zu int-Array machen für setSelectedIndices
  int[] makeIndexArr(boolean[] a) {
    int c = 0;
    for (int i=0; i<a.length; i++)
      if (a[i]) c++;
    int[] arr = new int[c];
    c = 0;
    for (int i=0; i<a.length; i++)
      if (a[i]) arr[c++] = i;
    return arr;
  }


  // Statistikberechnung beginnen
  public void startStatistik(StatistikDaten[] d) {
    // Statistikberechnung mit Progress-Bar
    statistikThread = new StatistikThread();
    progressMonitor = new ProgressMonitor(this, International.getString("Statistikberechnung"), "", 0, statistikThread.getLengthOfTask());
    progressMonitor.setProgress(0);
    progressMonitor.setMaximum(1);
    progressMonitor.setMillisToDecideToPopup(PROGRESS_TIMETOPOPUP);
      // enableFrame(...) gibt false zurück, wenn das Frame bereits disabled ist, d.h. wenn bereits eine Berechnung
      // läuft. Dies ist ein Bugfix, damit eine Statistikberechnung nicht mehrfach parallel ausgeführt werden kann
      // 13.01.2006 (Bugfix für MG)
    if (enableFrame(false,International.getString("efa berechnet die Statistik ..."),true)) {
      Thread thr = statistikThread.go(d);
      timer.start();
    }
  }


  // Fehler, wenn eines der "nur ..."-Felder komplett leer ist (d.h. keine Einträge ausgewählt wurden!)
  boolean errorIfEmpty(boolean[] b, String s) {
    boolean empty = true;
    for (int i=0; i<b.length; i++) if (b[i]) empty = false;
    if (empty)
      Dialog.error(International.getMessage("In der Auswahl {selection} wurde kein Eintrag ausgewählt. Bitte wähle zumindest einen Eintrag aus!",s));
    return empty;
  }

  // Statistik erstellen
  void doErstellen() {
    if (auswahlPane.getSelectedIndex() == 3) {
      createSavedStatistik();
      return;
    }

    StatistikDaten[] d = new StatistikDaten[1];
    d[0] = createStatistik();
    if (d[0] == null) return;
    allgStatistikDaten(d[0]);

    if (d[0].stat == StatistikDaten.STAT_MITGLIEDER || d[0].stat == StatistikDaten.STAT_WETT) {
      if (errorIfEmpty(d[0].geschlecht,International.getString("Geschlecht"))) return;
      if (errorIfEmpty(d[0].status,International.getString("Status"))) return;
      if (errorIfEmpty(d[0].fahrtart,International.getString("Art der Fahrt"))) return;
    }
    if (d[0].stat == StatistikDaten.STAT_BOOTE) {
      if (errorIfEmpty(d[0].bArt,International.getString("Art"))) return;
      if (errorIfEmpty(d[0].bAnzahl,International.getString("Ruderplätze"))) return;
      if (errorIfEmpty(d[0].fahrtart,International.getString("Art der Fahrt"))) return;
      if (errorIfEmpty(d[0].bRigger,International.getString("Riggerung"))) return;
      if (errorIfEmpty(d[0].bStm,International.getString("mit/ohne Stm."))) return;
      if (errorIfEmpty(d[0].bVerein,International.getString("Vereins- oder Gastboot"))) return;
    }

    startStatistik(d);
  }


    // Timer für ProgressBar-Aktualisierung
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {

            if (progressMonitor.isCanceled() || statistikThread.done()) {
                progressMonitor.close();
                statistikThread.stop();
                timer.stop();
                enableFrame(true,null,false);
            } else {
                progressMonitor.setNote(statistikThread.getMessage());
                progressMonitor.setMaximum(statistikThread.getLengthOfTask());
                progressMonitor.setProgress(statistikThread.getCurrent());
            }
        }
    }



  // Statistik erstellen
  void erstellenBut_actionPerformed(ActionEvent e) {
    doErstellen();
  }



  public synchronized boolean enableFrame(boolean enable, String text, boolean stopIfAlreadyDisabled) {
    if (!enable && stopIfAlreadyDisabled && !erstellenBut.isEnabled()) {
      return false; // gibt false zurück, wenn Ausführung verboten ist
    }
    setEnabled(enable);
    erstellenBut.setEnabled(enable);

    if (text != null) {
      erstellenBut.setText(text);
      if (enable) {
        erstellenBut.setForeground(Color.blue);
        try {
          Thread.sleep(2000);
        } catch(Exception e) { EfaUtil.foo(); }
        erstellenBut.setText(International.getString("Statistik erstellen"));
        erstellenBut.setForeground(Color.black);
      }
    } else erstellenBut.setText(International.getString("Statistik erstellen"));
    return true;
  }



  // Datensatz Statistikdaten erstellen
  StatistikDaten createStatistik() {
    StatistikDaten d = new StatistikDaten();
    d.statistikFrame = this;
    d.parent = this;
    d.stat = auswahlPane.getSelectedIndex();

    d.ausgabeArt = ausgabeArt.getSelectedIndex();
    if (ausgabeFormat.getSelectedIndex()>0) {
      d.stylesheet = Daten.efaStyleDirectory +
                     ( ausgabeExt[d.ausgabeArt] == null ? "html" : ausgabeExt[d.ausgabeArt] ) +
                     Daten.fileSep + ausgabeFormat.getSelectedItem();
      if (new File(d.stylesheet+".xsl").isFile()) d.stylesheet+=".xsl";
      else d.stylesheet+=".XSL";
    }
    if (d.ausgabeArt == StatistikDaten.AUSGABE_PDF && ausgabeFormat.getSelectedIndex()==0) {
      Dialog.infoDialog(International.getString("Keine Formatierung ausgewählt"),
              International.getString("Bitte wähle eine Formatierung aus!"));
      return null;
    }

    if (d.ausgabeArt == StatistikDaten.AUSGABE_HTML ||
        d.ausgabeArt == StatistikDaten.AUSGABE_INTERN_GRAFIK ||
        d.ausgabeArt == StatistikDaten.AUSGABE_BROWSER) d.ausgabeArtPrimaer = StatistikDaten.AUSGABE_HTML;
    else d.ausgabeArtPrimaer = d.ausgabeArt;
    if (d.stylesheet != null) d.ausgabeArtPrimaer = StatistikDaten.AUSGABE_XML;

    d.von = EfaUtil.string2date(von.getText().trim(),d.von.tag,d.von.monat,d.von.jahr);
    d.bis = EfaUtil.string2date(bis.getText().trim(),d.bis.tag,d.bis.monat,d.bis.jahr);
    d.vonCal = d.von.toCalendar();
    d.bisCal = d.bis.toCalendar();
    d.zeitFbUebergreifend = zeitFbUebergreifend.isSelected();
    d.ausgabeDatei = ausgabeDatei.getText().trim();
    d.tabelleHTML = erweitertFrame.nurTabelle.isSelected();
    d.ausgabeOverwriteWarnung = erweitertFrame.overwrite.isSelected();
    d.fileExecBefore = erweitertFrame.fileExecBefore.getText().trim();
    d.fileExecAfter = erweitertFrame.fileExecAfter.getText().trim();
    d.nurGanzeKm = erweitertFrame.nurGanzeKm.isSelected();
    d.ziele_gruppiert = erweitertFrame.teilzieleEinzeln.isSelected();
    d.gasteAlsEinePerson = erweitertFrame.zusammenGaesteAndere.isSelected();
    d.gaesteVereinsweise = erweitertFrame.gaesteVereinsweiseZusammen.isSelected();
    d.maxSizeKm = EfaUtil.string2int(erweitertFrame.maxKm.getText().trim(),200);
    d.maxSizeRudKm = EfaUtil.string2int(erweitertFrame.maxRudKm.getText().trim(),200);
    d.maxSizeStmKm = EfaUtil.string2int(erweitertFrame.maxStmKm.getText().trim(),200);
    d.maxSizeFahrten = EfaUtil.string2int(erweitertFrame.maxFahrten.getText().trim(),200);
    d.maxSizeKmFahrt = EfaUtil.string2int(erweitertFrame.maxKmFahrt.getText().trim(),200);
    d.maxSizeDauer = EfaUtil.string2int(erweitertFrame.maxDauer.getText().trim(),200);
    d.maxSizeKmH = EfaUtil.string2int(erweitertFrame.maxKmH.getText().trim(),200);
    d.cropToMaxSize = erweitertFrame.crop.isSelected();
    d.zusammengefassteDatenOhneBalken = erweitertFrame.zusammengefassteWerteOhneBalken.isSelected();
    d.ww_horiz_alle = erweitertFrame.horiz_alle.isSelected();
    d.kmfahrt_gruppiert = erweitertFrame.kilometerGruppiert.isSelected();
    d.auchNullWerte = erweitertFrame.auchNullWerte.isSelected();
    d.alleZielfahrten = erweitertFrame.alleZielfahrtenAusgeben.isSelected();
    d.ausgebenXMLalle = erweitertFrame.xmlImmerAlle.isSelected();
    d.ausgebenMitglnrStattName = erweitertFrame.mitglnrStattName.isSelected();
    d.nurBemerk = erweitertFrame.nurBemerk.getText().trim();
    d.nurBemerkNicht = erweitertFrame.nurBemerkNicht.getText().trim();
    d.nurStegKm = erweitertFrame.nurStegKm.isSelected();
    d.nurMindKm = EfaUtil.string2date(erweitertFrame.nurMindKm.getText(),0,0,0).tag*10 + EfaUtil.string2date(erweitertFrame.nurMindKm.getText(),0,0,0).monat;
    d.nurBooteFuerGruppe = erweitertFrame.nurBooteFuerGruppe.getText().trim();
    d.nurFb = new String[2];
    d.nurFb[0] = erweitertFrame.nurFb1.getText().trim();
    d.nurFb[1] = erweitertFrame.nurFb2.getText().trim();

    d.fbLfdNr = erweitertFrame.fbLfdNrCheckBox.isSelected();
    d.fbDatum = erweitertFrame.fbDatumCheckBox.isSelected();
    d.fbBoot = erweitertFrame.fbBootCheckBox.isSelected();
    d.fbStm = erweitertFrame.fbStmCheckBox.isSelected();
    d.fbMannsch = erweitertFrame.fbMannschCheckBox.isSelected();
    d.fbAbfahrt = erweitertFrame.fbAbfahrtCheckBox.isSelected();
    d.fbAnkunft = erweitertFrame.fbAnkunftCheckBox.isSelected();
    d.fbZiel = erweitertFrame.fbZielCheckBox.isSelected();
    d.fbBootsKm = erweitertFrame.fbBootsKmCheckBox.isSelected();
    d.fbMannschKm = erweitertFrame.fbMannschKmCheckBox.isSelected();
    d.fbBemerkungen= erweitertFrame.fbBemerkungenCheckBox.isSelected();
    d.fbFahrtartInBemerkungen = erweitertFrame.fbFahrtartInBemerkungenCheckBox.isSelected();
    d.fbZielbereichInBemerkungen = erweitertFrame.fbZielbereichInBemerkungenCheckBox.isSelected();

    if (d.stat != StatistikDaten.STAT_WETT) { // Zusatzwettbewerbe nur, wenn nicht bereits Wettbewerbsausgabe vorliegt
      createZusatzWett(d,0,erweitertFrame.wett1,erweitertFrame.wettjahr1);
      createZusatzWett(d,1,erweitertFrame.wett2,erweitertFrame.wettjahr2);
      createZusatzWett(d,2,erweitertFrame.wett3,erweitertFrame.wettjahr3);
      d.zusatzWettMitAnforderung = erweitertFrame.wettMitAnforderungen.isSelected();
    }

    switch (d.stat) {
      case 0: if (!createStatRuderer(d)) return null; break;
      case 1: if (!createStatBoote(d)) return null; break;
      case 2: if (!createStatWett(d)) return null; break;
      default: return null;
    }

    return d;
  }

  // spezielle Daten für zusätzliche Wettbewerbsausgabe
  void createZusatzWett(StatistikDaten d, int i, JComboBox wett, JTextField wettjahr) {
    if (i<0 || i>=d.zusatzWett.length) return;
    if (wett.getSelectedIndex()<=0) d.zusatzWett[i] = -1;
    else {
      d.zusatzWett[i] = wett.getSelectedIndex() - 1 + 200;
      d.zusatzWettjahr[i] = EfaUtil.string2date(wettjahr.getText(),0,0,0).tag;
    }
  }



  // spezielle Daten für Mitglieder-Statistik ermitteln
  boolean createStatRuderer(StatistikDaten d) {
    if (art.getSelectedIndex() == StatistikDaten.ART_MITRUDERER &&
        (nurName.getText().trim().equals("") || nameTeil.isSelected()) ) {
      Dialog.infoDialog(International.getString("Fehler"),
              International.getString("Wenn Statistikart 'Mitruderer' gewählt ist, muß bei 'nur Name' "+
                                         "ein konkreter Name angegeben werden, und 'als Teil eines Namens' "+
                                         "darf nicht aktiviert sein."));
      return false;
    }

    d.art = art.getSelectedIndex();
    d.vorjahresvergleich = zeitVorjahresvergleich.isSelected();

    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++) {
      d.geschlecht[i] = nurGeschlecht.isSelectedIndex(i);
    }
    for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++) {
      d.status[i] = nurStatus1.isSelectedIndex(i);
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++) {
      d.fahrtart[i] = nurFahrtart.isSelectedIndex(i);
    }

    d.name = nurName.getText().trim();
    if (d.name.length()>0 && !nameTeil.isSelected()) d.name = EfaUtil.syn2org(Daten.synMitglieder,d.name);
    d.nameTeil = nameTeil.isSelected();
    d.nameOderGruppe = (this.nurAuswahlName.isSelected() ? StatistikDaten.NG_NAME : StatistikDaten.NG_GRUPPE);

    d.ausgebenNr = (!numerierung.isSelectionEmpty());
    d.ausgebenName = ausName.isSelected();
    d.ausgebenJahrgang = ausJahrgang.isSelected();
    d.ausgebenStatus = ausStatus1.isSelected();
    d.ausgebenBezeichnung = false; // nur Boote
    d.ausgebenKm = ausKm.isSelected();
    d.ausgebenRudKm = ausRudKm.isSelected();
    d.ausgebenStmKm = ausStmKm.isSelected();
    d.ausgebenFahrten = ausFahrten.isSelected();
    d.ausgebenKmFahrt = ausKmFahrt.isSelected();
    d.ausgebenDauer = ausDauer.isSelected();
    d.ausgebenKmH = ausKmH.isSelected();
    d.ausgebenWafaKm = ausWafaKm.isSelected();
    d.ausgebenZielfahrten = ausZielfahrten.isSelected();
    if (d.art == StatistikDaten.ART_WERMITWEM ||
        d.art == StatistikDaten.ART_WERWOHIN ||
        d.art == StatistikDaten.ART_WERMITBOOTSART ||
        d.art == StatistikDaten.ART_WERMITFAHRTART ||
        d.art == StatistikDaten.ART_WERUNERLAUBT) d.ausgebenWWAnzVersch = ausAnzVersch.isSelected();
    d.ausgebenWWNamen = d.ausgebenKm || d.ausgebenRudKm || d.ausgebenStmKm ||
                        d.ausgebenFahrten || d.ausgebenKmFahrt;

    d.graphischKm = graKm.isSelected();
    d.graphischRudKm = graRudKm.isSelected();
    d.graphischStmKm = graStmKm.isSelected();
    d.graphischFahrten = graFahrten.isSelected();
    d.graphischKmFahrt = graKmFahrt.isSelected();
    d.graphischDauer = graDauer.isSelected();
    d.graphischKmH = graKmH.isSelected();

    d.sortierKriterium = sortKrit.getSelectedIndex();
    d.sortierFolge = sortFolge.getSelectedIndex();

    for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++)
      d.numeriere[i] = numerierung.isSelectedIndex(i);

    d.graSizeKm = EfaUtil.string2int(maxBalkenKm.getText().trim(),200);
    d.graSizeRudKm = EfaUtil.string2int(maxBalkenRudKm.getText().trim(),200);
    d.graSizeStmKm = EfaUtil.string2int(maxBalkenStmKm.getText().trim(),200);
    d.graSizeFahrten = EfaUtil.string2int(maxBalkenFahrten.getText().trim(),200);
    d.graSizeKmFahrt = EfaUtil.string2int(maxBalkenKmFahrt.getText().trim(),200);
    d.graSizeDauer = EfaUtil.string2int(maxBalkenDauer.getText().trim(),200);
    d.graSizeKmH = EfaUtil.string2int(maxBalkenKmH.getText().trim(),200);

    if (d.art == StatistikDaten.ART_MITGLIEDER || d.art == StatistikDaten.ART_WERMITWEM ||
        d.art == StatistikDaten.ART_WERWOHIN || d.art == StatistikDaten.ART_WERMITBOOTSART ||
        d.art == StatistikDaten.ART_WERMITFAHRTART ||
        d.art == StatistikDaten.ART_WERUNERLAUBT) d.sortVorNachname = true;
    else d.sortVorNachname = false;

    return true;
  }



  // spezielle Daten für Bootsstatiktik ermitteln
  boolean createStatBoote(StatistikDaten d) {
    d.art = bArt.getSelectedIndex() + 100;
    d.vorjahresvergleich = bzeitVorjahresvergleich.isSelected();


    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); i++) {
      d.bArt[i] = mNurArt.isSelectedIndex(i);
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); i++) {
      d.bAnzahl[i] = mNurAnzahl.isSelectedIndex(i);
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++) {
      d.fahrtart[i] = mNurFahrtart.isSelectedIndex(i);
    }
    d.bRigger[0] = mNurSkull.isSelected();
    d.bRigger[1] = mNurRiemen.isSelected();
    d.bRigger[2] = mNurAndere1.isSelected();
    d.bStm[0] = mNurMitStm.isSelected();
    d.bStm[1] = mNurOhneStm.isSelected();
    d.bStm[2] = mNurAndere2.isSelected();
    d.bVerein[0] = mNurEigene.isSelected();
    d.bVerein[1] = mNurFremde.isSelected();

    d.name = mNurBoot.getText().trim();
    if (d.name.length()>0) d.name = EfaUtil.syn2org(Daten.synBoote,d.name);
    d.nameTeil = false;
    d.nameOderGruppe = StatistikDaten.NG_NAME;

    d.ausgebenNr = (mNumEigene.isSelected() || mNumFremde.isSelected());

    d.ausgebenName = mAusName.isSelected();
    d.ausgebenStatus = mAusArt.isSelected();
    d.ausgebenBezeichnung = mAusBez.isSelected();
    d.ausgebenKm = mAusKm.isSelected();
    d.ausgebenFahrten = mAusFahrten.isSelected();
    d.ausgebenKmFahrt = mAusKmFahrt.isSelected();
    d.ausgebenDauer = mAusDauer.isSelected();
    d.ausgebenKmH = mAusKmH.isSelected();

    d.ausgebenWWNamen = d.ausgebenKm || d.ausgebenRudKm || d.ausgebenStmKm ||
                        d.ausgebenFahrten || d.ausgebenKmFahrt;

    d.graphischKm = mGraAusKm.isSelected();
    d.graphischFahrten = mGraAusFahrten.isSelected();
    d.graphischKmFahrt = mGraAusKmFahrt.isSelected();
    d.graphischDauer = mGraAusDauer.isSelected();
    d.graphischKmH = mGraAusKmH.isSelected();

    d.sortierKriterium = mSortKrit.getSelectedIndex();
    d.sortierFolge = mSortFolge.getSelectedIndex();

    d.numeriere[0] = mNumEigene.isSelected();
    d.numeriere[1] = mNumFremde.isSelected();

    d.graSizeKm = EfaUtil.string2int(mGraSizeKm.getText().trim(),200);
    d.graSizeFahrten = EfaUtil.string2int(mGraSizeFahrten.getText().trim(),200);
    d.graSizeKmFahrt = EfaUtil.string2int(mGraSizeKmFahrt.getText().trim(),200);
    d.graSizeDauer = EfaUtil.string2int(mGraSizeDauer.getText().trim(),200);
    d.graSizeKmH = EfaUtil.string2int(mGraSizeKmH.getText().trim(),200);

    if (d.art == StatistikDaten.BART_RUDERER) d.sortVorNachname = true;
    else d.sortVorNachname = false;

    return true;
  }



  // spezielle Daten für Mitglieder-Statistik ermitteln
  boolean createStatWett(StatistikDaten d) {
    d.art = wettArt.getSelectedIndex() + 200;

    if (d.ausgabeArt == StatistikDaten.AUSGABE_EFAWETT &&
        (d.art == StatistikDaten.WETT_LRVBRB_FAHRTENWETT || d.art == StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT ||
         d.art == StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT)) {
      Dialog.error(International.getString("Die Ausgabe als Meldedatei wird für diesen Wettbewerb nicht unterstützt."));
      return false;
    }



    if (d.ausgabeArt == StatistikDaten.AUSGABE_EFAWETT && Daten.wettDefs != null &&
        Daten.wettDefs.isDataOld() ) {
      if (Dialog.yesNoDialog(International.onlyFor("efaWett-Konfigurationsdaten","de"),
                             International.onlyFor("Die efaWett-Konfigurationsdaten sind schon recht alt.","de")+
                             " (" + International.onlyFor("Stand","de") + ": " + Daten.wettDefs.efw_stand_der_daten + ")\n"+
                             International.onlyFor("Sollen jetzt aktuelle Konfigurationsdaten aus dem Internet heruntergeladen werden?","de")
                             ) == Dialog.YES) {
        if (VereinsConfigFrame.holeAktuelleDatenAusDemInternet()) {
          Dialog.infoDialog(International.onlyFor("Die efaWett-Konfigurationsdaten wurden erfolgreich aktualisiert!","de"));
        } else {
          Dialog.error(International.onlyFor("Die efaWett-Konfigurationsdaten konnten nicht aktualisiert werden!","de"));
        }
      }
    }

    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_GENDER); i++) {
      d.geschlecht[i] = nurGeschlecht.isSelectedIndex(i);
    }
    for (int i=0; i<Daten.fahrtenbuch.getDaten().status.length; i++) {
      d.status[i] = nurStatus1.isSelectedIndex(i);
      if (Daten.efaTypes != null) {
        if (Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST) &&
            Daten.fahrtenbuch.getDaten().status[i].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST))) d.status[i] = false;
        if (Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_OTHER) && 
            Daten.fahrtenbuch.getDaten().status[i].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_OTHER))) d.status[i] = false;
      }
    }
    for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_SESSION); i++) {
      d.fahrtart[i] = nurFahrtart.isSelectedIndex(i);
      if (Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, i).equals(EfaTypes.TYPE_BOAT_MOTORBOAT) ||
          Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, i).equals(EfaTypes.TYPE_BOAT_ERG)) d.fahrtart[i] = false;
    }

    d.name = nurName.getText().trim();
    if (d.name.length()>0) d.name = EfaUtil.syn2org(Daten.synMitglieder,d.name);
    d.nameTeil = nameTeil.isSelected();
    d.nameOderGruppe = (this.nurAuswahlName.isSelected() ? StatistikDaten.NG_NAME : StatistikDaten.NG_GRUPPE);

    d.ausgebenWettBedingung = ausWettBedingung.isSelected();
    d.wettOhneDetail = ausWettOhneDetails.isSelected();

    if (d.art == StatistikDaten.WETT_DRV || d.art == StatistikDaten.WETT_LRVBSOMMER ||
        d.art == StatistikDaten.WETT_LRVBWINTER || d.art == StatistikDaten.WETT_LRVBWIMPEL ||
        d.art == StatistikDaten.WETT_LRVBRB_WANDERRUDERWETT || d.art == StatistikDaten.WETT_LRVBRB_FAHRTENWETT ||
        d.art == StatistikDaten.WETT_LRVMVP_WANDERRUDERWETT) {
      d.sortierFolge = StatistikDaten.SORTFOLGE_AB;
      d.sortierKriterium = StatistikDaten.SORTKRIT_KM;
    }
    if (d.art == StatistikDaten.WETT_DRV_WAFASTATISTIK) {
      d.sortierFolge = StatistikDaten.SORTFOLGE_AUF;
      d.sortierKriterium = StatistikDaten.SORTKRIT_NACHNAME;
    }

    if (d.art == StatistikDaten.WETT_LRVBSOMMER && d.ausgabeArt == StatistikDaten.AUSGABE_EFAWETT) {
      d.alleZielfahrten = true;
    }

    d.wettProz = EfaUtil.string2int(wettProz.getText().trim(),60);
    d.wettFahrten = EfaUtil.string2int(wettAnz.getText().trim(),4);

    d.wettJahr = EfaUtil.string2date(wettJahr.getText().trim(),0,0,0).tag;

    d.vorjahresvergleich = false;


    if (d.ausgabeArt == StatistikDaten.AUSGABE_EFAWETT) {
      d.ausgabeDatei = null;
      d.wettOhneDetail = false;
      d.ausgebenWettBedingung = false;
      if (!Statistik.checkWettZeitraum(d.wettJahr,d.von,d.bis,d.art-200)) {
        Dialog.infoDialog(International.getString("Ungültiger Zeitraum"),
                International.getString("Der gewählte Zeitraum entspricht nicht den Bedingungen der Ausschreibung"));
        return false;
      }
    }


    return true;
  }



  // gespeicherte Statistikeinstellungen aus Datenfeldern f in Datensatz StatistikDaten konvertieren
  public static StatistikDaten getSavedValues(DatenFelder f) {
    StatistikDaten d = new StatistikDaten();
    d.art = EfaUtil.string2int(f.get(StatSave.ART),StatistikDaten.ART_MITGLIEDER);
    d.stat = EfaUtil.string2int(f.get(StatSave.STAT),StatistikDaten.STAT_MITGLIEDER);
    d.ausgabeDatei = f.get(StatSave.AUSGABEDATEI);
    d.ausgabeArt = EfaUtil.string2int(f.get(StatSave.AUSGABEART),0);

    if (d.ausgabeArt == StatistikDaten.AUSGABE_HTML ||
        d.ausgabeArt == StatistikDaten.AUSGABE_INTERN_GRAFIK ||
        d.ausgabeArt == StatistikDaten.AUSGABE_BROWSER) d.ausgabeArtPrimaer = StatistikDaten.AUSGABE_HTML;
    else d.ausgabeArtPrimaer = d.ausgabeArt;
    if (f.get(StatSave.STYLESHEET).equals("")) d.stylesheet = null;
    else d.stylesheet = f.get(StatSave.STYLESHEET);
    if (d.stylesheet != null) d.ausgabeArtPrimaer = StatistikDaten.AUSGABE_XML;

    d.tabelleHTML = EfaUtil.isOptionSet(f.get(StatSave.TABELLEHTML),0);
    d.von = EfaUtil.string2date(f.get(StatSave.VON).trim(),d.von.tag,d.von.monat,d.von.jahr);
    d.bis = EfaUtil.string2date(f.get(StatSave.BIS).trim(),d.bis.tag,d.bis.monat,d.bis.jahr);
    d.vonCal = d.von.toCalendar();
    d.bisCal = d.bis.toCalendar();
    d.wettJahr = EfaUtil.string2int(f.get(StatSave.WETTJAHR).trim(),0);
    d.zeitFbUebergreifend = EfaUtil.isOptionSet(f.get(StatSave.ZEITFBUEBERGREIFEND),0);
    d.vorjahresvergleich = EfaUtil.isOptionSet(f.get(StatSave.ZEITVORJAHRESVERGLEICH),0);
    for (int j=0; j<d.geschlecht.length; j++)
      d.geschlecht[j] = EfaUtil.isOptionSet(f.get(StatSave.GESCHLECHT),j);
    for (int j=0; j<d.status.length; j++)
      d.status[j] = EfaUtil.isOptionSet(f.get(StatSave.STATUS),j);
    for (int j=0; j<d.fahrtart.length; j++)
      d.fahrtart[j] = EfaUtil.isOptionSet(f.get(StatSave.FAHRTART),j);
    for (int j=0; j<d.bArt.length; j++)
      d.bArt[j] = EfaUtil.isOptionSet(f.get(StatSave.BART),j);
    for (int j=0; j<d.bAnzahl.length; j++)
      d.bAnzahl[j] = EfaUtil.isOptionSet(f.get(StatSave.BANZAHL),j);
    for (int j=0; j<d.bRigger.length; j++)
      d.bRigger[j] = EfaUtil.isOptionSet(f.get(StatSave.BRIGGER),j);
    for (int j=0; j<d.bStm.length; j++)
      d.bStm[j] = EfaUtil.isOptionSet(f.get(StatSave.BSTM),j);
    for (int j=0; j<d.bVerein.length; j++)
      d.bVerein[j] = EfaUtil.isOptionSet(f.get(StatSave.BVEREIN),j);
    d.name = f.get(StatSave.NAME);
    d.nameTeil = EfaUtil.isOptionSet(f.get(StatSave.NAMETEIL),0);
    d.nameOderGruppe = EfaUtil.string2int(f.get(StatSave.NAME_ODER_GRUPPE),StatistikDaten.NG_NAME);
    d.nurBemerk = f.get(StatSave.NURBEMERK);
    d.nurBemerkNicht = f.get(StatSave.NURBEMERKNICHT);
    d.nurStegKm = EfaUtil.isOptionSet(f.get(StatSave.NURSTEGKM),0);
    d.nurMindKm = EfaUtil.string2int(f.get(StatSave.NURMINDKM),0);
    d.nurBooteFuerGruppe = f.get(StatSave.NURBOOTEFUERGRUPPE);
    d.nurFb = EfaUtil.kommaList2Arr(f.get(StatSave.NUR_FB),';');
    d.ausgebenNr = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),0);
    d.ausgebenName = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),1);
    d.ausgebenJahrgang = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),2);
    d.ausgebenStatus = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),3);
    d.ausgebenBezeichnung = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),4);
    d.ausgebenArt = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),5);
    d.ausgebenPlaetze = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),6);
    d.ausgebenKm = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),7);
    d.ausgebenRudKm = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),8);
    d.ausgebenStmKm = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),9);
    d.ausgebenFahrten = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),10);
    d.ausgebenKmFahrt = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),11);
    d.ausgebenZielfahrten = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),12);
    d.ausgebenWWAnzVersch = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),13);
    d.ausgebenWWNamen = d.ausgebenKm || d.ausgebenRudKm || d.ausgebenStmKm ||
                        d.ausgebenFahrten || d.ausgebenKmFahrt;
    d.ausgebenDauer = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),14);
    d.ausgebenKmH = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),15);
    d.ausgebenXMLalle = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),16);
    d.ausgebenWafaKm = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),17);
    d.ausgebenMitglnrStattName = EfaUtil.isOptionSet(f.get(StatSave.AUSGEBEN),18);
    d.ausgebenWettBedingung = EfaUtil.isOptionSet(f.get(StatSave.AUSWETTBEDINGUNGEN),0);
    d.wettOhneDetail = EfaUtil.isOptionSet(f.get(StatSave.WETTOHNEDETAIL),0);
    d.graphischKm = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),0);
    d.graphischRudKm = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),1);
    d.graphischStmKm = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),2);
    d.graphischFahrten = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),3);
    d.graphischKmFahrt = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),4);
    d.graphischDauer = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),5);
    d.graphischKmH = EfaUtil.isOptionSet(f.get(StatSave.GRAPHISCH),6);
    for (int j=0; j<d.numeriere.length; j++)
      d.numeriere[j] = EfaUtil.isOptionSet(f.get(StatSave.NUMERIERE),j);
    d.sortierKriterium = EfaUtil.string2int(f.get(StatSave.SORTIERKRITERIUM),StatistikDaten.SORTKRIT_KM);
    d.sortierFolge = EfaUtil.string2int(f.get(StatSave.SORTIERFOLGE),StatistikDaten.SORTFOLGE_AUF);
    d.sortVorNachname = EfaUtil.isOptionSet(f.get(StatSave.SORTVORNACHNAME),0);
    d.graSizeKm = EfaUtil.string2int(f.get(StatSave.GRASIZEKM),200);
    d.graSizeStmKm = EfaUtil.string2int(f.get(StatSave.GRASIZESTMKM),200);
    d.graSizeRudKm = EfaUtil.string2int(f.get(StatSave.GRASIZERUDKM),200);
    d.graSizeFahrten = EfaUtil.string2int(f.get(StatSave.GRASIZEFAHRTEN),200);
    d.graSizeKmFahrt = EfaUtil.string2int(f.get(StatSave.GRASIZEKMFAHRT),200);
    d.graSizeDauer = EfaUtil.string2int(f.get(StatSave.GRASIZEDAUER),200);
    d.graSizeKmH = EfaUtil.string2int(f.get(StatSave.GRASIZEKMH),200);
    d.zusammenAddieren = EfaUtil.isOptionSet(f.get(StatSave.ZUSAMMENADDIEREN),0);
    d.ww_horiz_alle = EfaUtil.isOptionSet(f.get(StatSave.WW_OPTIONS),0);
    d.ausgabeOverwriteWarnung = EfaUtil.isOptionSet(f.get(StatSave.AUSGABEOVERWRITE),0);
    d.fileExecBefore = f.get(StatSave.FILE_EXEC_BEFORE);
    d.fileExecAfter  = f.get(StatSave.FILE_EXEC_AFTER);
    d.kmfahrt_gruppiert = EfaUtil.isOptionSet(f.get(StatSave.KMFAHRT_GRUPPIERT),0);
    d.ziele_gruppiert = EfaUtil.isOptionSet(f.get(StatSave.ZIELEGRUPPIERT),0);
    d.nurGanzeKm = EfaUtil.isOptionSet( (f.get(StatSave.NURGANZEKM) == null || f.get(StatSave.NURGANZEKM).length() == 0 ? "+" : f.get(StatSave.NURGANZEKM)),0); // null="+" since this was only added with 1.8.3_04 (no change in Format)
    d.auchNullWerte = EfaUtil.isOptionSet(f.get(StatSave.AUCHNULLWERTE),0);
    d.alleZielfahrten = EfaUtil.isOptionSet(f.get(StatSave.ALLEZIELFAHRTEN),0);
    d.wettProz = EfaUtil.string2int(f.get(StatSave.WETTPROZENT),60);
    d.wettFahrten = EfaUtil.string2int(f.get(StatSave.WETTFAHRTEN),4);
    d.gasteAlsEinePerson = EfaUtil.isOptionSet(f.get(StatSave.GAESTEALSEIN),0);
    d.gaesteVereinsweise = EfaUtil.isOptionSet(f.get(StatSave.GAESTEALSEIN),1);
    d.cropToMaxSize = EfaUtil.isOptionSet(f.get(StatSave.CROPTOMAXSIZE),0);
    d.maxSizeKm = EfaUtil.string2int(f.get(StatSave.MAXSIZEKM),200);
    d.maxSizeStmKm = EfaUtil.string2int(f.get(StatSave.MAXSIZESTMKM),200);
    d.maxSizeRudKm = EfaUtil.string2int(f.get(StatSave.MAXSIZERUDKM),200);
    d.maxSizeFahrten = EfaUtil.string2int(f.get(StatSave.MAXSIZEFAHRTEN),200);
    d.maxSizeKmFahrt = EfaUtil.string2int(f.get(StatSave.MAXSIZEKMFAHRT),200);
    d.maxSizeDauer = EfaUtil.string2int(f.get(StatSave.MAXSIZEDAUER),200);
    d.maxSizeKmH = EfaUtil.string2int(f.get(StatSave.MAXSIZEKMH),200);
    d.zusammengefassteDatenOhneBalken = EfaUtil.isOptionSet(f.get(StatSave.ZUSAMMENGEFASSTEWERTEOHNEBALKEN),0);

    d.fbLfdNr = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),0);
    d.fbDatum = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),1);
    d.fbBoot = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),2);
    d.fbStm = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),3);
    d.fbMannsch = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),4);
    d.fbAbfahrt = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),5);
    d.fbAnkunft = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),6);
    d.fbZiel = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),7);
    d.fbBootsKm = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),8);
    d.fbMannschKm = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),9);
    d.fbBemerkungen = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),10);
    d.fbFahrtartInBemerkungen = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),11);
    d.fbZielbereichInBemerkungen = EfaUtil.isOptionSet(f.get(StatSave.FAHRTENBUCHFELDER),12);

    d.zusatzWett[0] = EfaUtil.string2int(f.get(StatSave.ZUSWETT1),-1);
    d.zusatzWett[1] = EfaUtil.string2int(f.get(StatSave.ZUSWETT2),-1);
    d.zusatzWett[2] = EfaUtil.string2int(f.get(StatSave.ZUSWETT3),-1);
    d.zusatzWettjahr[0] = EfaUtil.string2int(f.get(StatSave.ZUSWETTJAHR1),0);
    d.zusatzWettjahr[1] = EfaUtil.string2int(f.get(StatSave.ZUSWETTJAHR2),0);
    d.zusatzWettjahr[2] = EfaUtil.string2int(f.get(StatSave.ZUSWETTJAHR3),0);
    d.zusatzWettMitAnforderung = EfaUtil.isOptionSet(f.get(StatSave.ZUSWETTMITANFORD),0);
    return d;
  }


  // gespeicherte Einstellungen der Zusatzwettbewerbe wieder setzen
  void setSavedZusatzWett(StatistikDaten d,int i,JComboBox wett,JTextField wettjahr) {
    if (i<0 || i>=d.zusatzWett.length) return;
    if (d.zusatzWett[i]>=200 && wett.getItemCount()>d.zusatzWett[i]-200+1) {
      wett.setSelectedIndex(d.zusatzWett[i]-200+1);
      wettjahr.setText(Integer.toString(d.zusatzWettjahr[i]));
      erweitertFrame.wettChanged(wett,wettjahr);
    } else {
      wett.setSelectedIndex(0);
      wettjahr.setText("");
    }
  }


  // Gespeicherte Einstellungen wieder als aktuelle Einstellungen setzen
  void setSavedStat(DatenFelder f, boolean defaultStat) {
    if (f != null) {
      try {
        StatistikDaten d = getSavedValues(f);
        d.statistikFrame = this;
        d.parent = this;

        // häßlicher Workaround, damit wettPanel weiß, daß es nicht angeklickt wurde, sondern als gespeicherte Einstellungen
        // gesetzt wurde (denn in diesem Fall sollen alle Status-Felder erhalten bleiben, auch Gast, falls aktiviert)
      if (d.stat == StatistikDaten.STAT_WETT) statWettFromSavedValues = true;

        auswahlPane.setSelectedIndex(d.stat);
        switch (d.stat) {
          case StatistikDaten.STAT_MITGLIEDER:
            art.setSelectedIndex(d.art);
            zeitVorjahresvergleich.setSelected(d.vorjahresvergleich);
            nurGeschlecht.setSelectedIndices(makeIndexArr(d.geschlecht));
            nurStatus1.setSelectedIndices(makeIndexArr(d.status));
            nurFahrtart.setSelectedIndices(makeIndexArr(d.fahrtart));
            nurName.setText(d.name);
            nameTeil.setSelected(d.nameTeil);
            if (d.nameOderGruppe == StatistikDaten.NG_NAME) nurAuswahlName.setSelected(true);
            else nurAuswahlGruppe.setSelected(true);
            ausName.setSelected(d.ausgebenName);
            ausJahrgang.setSelected(d.ausgebenJahrgang);
            ausStatus1.setSelected(d.ausgebenStatus);
            ausKm.setSelected(d.ausgebenKm);
            ausRudKm.setSelected(d.ausgebenRudKm);
            ausStmKm.setSelected(d.ausgebenStmKm);
            ausFahrten.setSelected(d.ausgebenFahrten);
            ausKmFahrt.setSelected(d.ausgebenKmFahrt);
            ausDauer.setSelected(d.ausgebenDauer);
            ausKmH.setSelected(d.ausgebenKmH);
            ausWafaKm.setSelected(d.ausgebenWafaKm);
            ausZielfahrten.setSelected(d.ausgebenZielfahrten);
            ausAnzVersch.setSelected(d.ausgebenWWAnzVersch);
            graKm.setSelected(d.graphischKm);
            graRudKm.setSelected(d.graphischRudKm);
            graStmKm.setSelected(d.graphischStmKm);
            graFahrten.setSelected(d.graphischFahrten);
            graKmFahrt.setSelected(d.graphischKmFahrt);
            graDauer.setSelected(d.graphischDauer);
            graKmH.setSelected(d.graphischKmH);
            sortKrit.setSelectedIndex(d.sortierKriterium);
            sortFolge.setSelectedIndex(d.sortierFolge);
            numerierung.setSelectedIndices(makeIndexArr(d.numeriere));
            maxBalkenKm.setText(Integer.toString(d.graSizeKm));
            maxBalkenRudKm.setText(Integer.toString(d.graSizeRudKm));
            maxBalkenStmKm.setText(Integer.toString(d.graSizeStmKm));
            maxBalkenFahrten.setText(Integer.toString(d.graSizeFahrten));
            maxBalkenKmFahrt.setText(Integer.toString(d.graSizeKmFahrt));
            maxBalkenDauer.setText(Integer.toString(d.graSizeDauer));
            maxBalkenKmH.setText(Integer.toString(d.graSizeKmH));
            break;
          case StatistikDaten.STAT_BOOTE:
            bArt.setSelectedIndex(d.art-100);
            bzeitVorjahresvergleich.setSelected(d.vorjahresvergleich);
            mNurArt.setSelectedIndices(makeIndexArr(d.bArt));
            mNurAnzahl.setSelectedIndices(makeIndexArr(d.bAnzahl));
            mNurFahrtart.setSelectedIndices(makeIndexArr(d.fahrtart));
            mNurSkull.setSelected(d.bRigger[0]);
            mNurRiemen.setSelected(d.bRigger[1]);
            mNurAndere1.setSelected(d.bRigger[2]);
            mNurMitStm.setSelected(d.bStm[0]);
            mNurOhneStm.setSelected(d.bStm[1]);
            mNurAndere2.setSelected(d.bStm[2]);
            mNurEigene.setSelected(d.bVerein[0]);
            mNurFremde.setSelected(d.bVerein[1]);
            mNurBoot.setText(d.name);
            mAusName.setSelected(d.ausgebenName);
            mAusArt.setSelected(d.ausgebenStatus);
            mAusBez.setSelected(d.ausgebenBezeichnung);
            mAusKm.setSelected(d.ausgebenKm);
            mAusFahrten.setSelected(d.ausgebenFahrten);
            mAusKmFahrt.setSelected(d.ausgebenKmFahrt);
            mAusDauer.setSelected(d.ausgebenDauer);
            mAusKmH.setSelected(d.ausgebenKmH);
            mGraAusKm.setSelected(d.graphischKm);
            mGraAusFahrten.setSelected(d.graphischFahrten);
            mGraAusKmFahrt.setSelected(d.graphischKmFahrt);
            mGraAusDauer.setSelected(d.graphischDauer);
            mGraAusKmH.setSelected(d.graphischKmH);
            mSortKrit.setSelectedIndex(d.sortierKriterium);
            mSortFolge.setSelectedIndex(d.sortierFolge);
            mNumEigene.setSelected(d.numeriere[0]);
            mNumFremde.setSelected(d.numeriere[1]);
            mGraSizeKm.setText(Integer.toString(d.graSizeKm));
            mGraSizeFahrten.setText(Integer.toString(d.graSizeFahrten));
            mGraSizeKmFahrt.setText(Integer.toString(d.graSizeKmFahrt));
            mGraSizeDauer.setText(Integer.toString(d.graSizeDauer));
            mGraSizeKmH.setText(Integer.toString(d.graSizeKmH));
            break;
          case StatistikDaten.STAT_WETT:
            wettArt.setSelectedIndex(d.art-200);
            nurGeschlecht.setSelectedIndices(makeIndexArr(d.geschlecht));
            nurStatus1.setSelectedIndices(makeIndexArr(d.status));
            nurFahrtart.setSelectedIndices(makeIndexArr(d.fahrtart));
            nurName.setText(d.name);
            nameTeil.setSelected(d.nameTeil);
            if (d.nameOderGruppe == StatistikDaten.NG_NAME) nurAuswahlName.setSelected(true);
            else nurAuswahlGruppe.setSelected(true);
            ausWettBedingung.setSelected(d.ausgebenWettBedingung);
            ausWettOhneDetails.setSelected(d.wettOhneDetail);
            wettProz.setText(Integer.toString(d.wettProz));
            wettAnz.setText(Integer.toString(d.wettFahrten));
            wettJahr.setText(Integer.toString(d.wettJahr));
            break;
          default:
        }
        if (d.von.jahr != 1) von.setText(d.von.tag+"."+d.von.monat+"."+d.von.jahr);
        else von.setText("");
        if (d.bis.jahr != 9999) bis.setText(d.bis.tag+"."+d.bis.monat+"."+d.bis.jahr);
        else bis.setText("");
        zeitFbUebergreifend.setSelected(d.zeitFbUebergreifend);
        if (!defaultStat) ausgabeDatei.setText(d.ausgabeDatei);
        try {
          ausgabeArt.setSelectedIndex(d.ausgabeArt);
        } catch(Exception e) {
          ausgabeArt.setSelectedIndex(0);
        }
        ausgabeDatei.setText(d.ausgabeDatei);
        erweitertFrame.nurTabelle.setSelected(d.tabelleHTML);
        erweitertFrame.overwrite.setSelected(d.ausgabeOverwriteWarnung);
        erweitertFrame.fileExecBefore.setText(d.fileExecBefore);
        erweitertFrame.fileExecAfter.setText(d.fileExecAfter);
        erweitertFrame.horiz_alle.setSelected(d.ww_horiz_alle);
        erweitertFrame.kilometerGruppiert.setSelected(d.kmfahrt_gruppiert);
        erweitertFrame.teilzieleEinzeln.setSelected(d.ziele_gruppiert);
        erweitertFrame.nurGanzeKm.setSelected(d.nurGanzeKm);
        erweitertFrame.auchNullWerte.setSelected(d.auchNullWerte);
        erweitertFrame.alleZielfahrtenAusgeben.setSelected(d.alleZielfahrten);
        erweitertFrame.xmlImmerAlle.setSelected(d.ausgebenXMLalle);
        erweitertFrame.mitglnrStattName.setSelected(d.ausgebenMitglnrStattName);
        erweitertFrame.zusammenGaesteAndere.setSelected(d.gasteAlsEinePerson);
        erweitertFrame.gaesteVereinsweiseZusammen.setSelected(d.gaesteVereinsweise);
        erweitertFrame.crop.setSelected(d.cropToMaxSize);
        erweitertFrame.maxKm.setText(Integer.toString(d.maxSizeKm));
        erweitertFrame.maxRudKm.setText(Integer.toString(d.maxSizeRudKm));
        erweitertFrame.maxStmKm.setText(Integer.toString(d.maxSizeStmKm));
        erweitertFrame.maxFahrten.setText(Integer.toString(d.maxSizeFahrten));
        erweitertFrame.maxKmFahrt.setText(Integer.toString(d.maxSizeKmFahrt));
        erweitertFrame.maxDauer.setText(Integer.toString(d.maxSizeDauer));
        erweitertFrame.maxKmH.setText(Integer.toString(d.maxSizeKmH));
        erweitertFrame.zusammengefassteWerteOhneBalken.setSelected(d.zusammengefassteDatenOhneBalken);
        erweitertFrame.nurBemerk.setText(d.nurBemerk);
        erweitertFrame.nurBemerkNicht.setText(d.nurBemerkNicht);
        erweitertFrame.nurStegKm.setSelected(d.nurStegKm);
        erweitertFrame.nurMindKm.setText( (d.nurMindKm == 0 ? "" : EfaUtil.zehntelInt2String(d.nurMindKm) ) );
        erweitertFrame.nurBooteFuerGruppe.setText(d.nurBooteFuerGruppe);
        erweitertFrame.nurFb1.setText( (d.nurFb != null && d.nurFb.length>=1 ? d.nurFb[0] : "") );
        erweitertFrame.nurFb2.setText( (d.nurFb != null && d.nurFb.length>=2 ? d.nurFb[1] : "") );

        erweitertFrame.fbLfdNrCheckBox.setSelected(d.fbLfdNr);
        erweitertFrame.fbDatumCheckBox.setSelected(d.fbDatum);
        erweitertFrame.fbBootCheckBox.setSelected(d.fbBoot);
        erweitertFrame.fbStmCheckBox.setSelected(d.fbStm);
        erweitertFrame.fbMannschCheckBox.setSelected(d.fbMannsch);
        erweitertFrame.fbAbfahrtCheckBox.setSelected(d.fbAbfahrt);
        erweitertFrame.fbAnkunftCheckBox.setSelected(d.fbAnkunft);
        erweitertFrame.fbZielCheckBox.setSelected(d.fbZiel);
        erweitertFrame.fbBootsKmCheckBox.setSelected(d.fbBootsKm);
        erweitertFrame.fbMannschKmCheckBox.setSelected(d.fbMannschKm);
        erweitertFrame.fbBemerkungenCheckBox.setSelected(d.fbBemerkungen);
        erweitertFrame.fbFahrtartInBemerkungenCheckBox.setSelected(d.fbFahrtartInBemerkungen);
        erweitertFrame.fbZielbereichInBemerkungenCheckBox.setSelected(d.fbZielbereichInBemerkungen);

        setSavedZusatzWett(d,0,erweitertFrame.wett1,erweitertFrame.wettjahr1);
        setSavedZusatzWett(d,1,erweitertFrame.wett2,erweitertFrame.wettjahr2);
        setSavedZusatzWett(d,2,erweitertFrame.wett3,erweitertFrame.wettjahr3);
        erweitertFrame.wettMitAnforderungen.setSelected(d.zusatzWettMitAnforderung);
        if (d.stylesheet == null) ausgabeFormat.setSelectedIndex(0);
        else {
          String s = d.stylesheet;
          if (s.lastIndexOf(Daten.fileSep)>=0) s = s.substring(s.lastIndexOf(Daten.fileSep)+1);
          if (s.lastIndexOf(".xsl")==s.length()-4 || s.lastIndexOf(".XSL")==s.length()-4) s = s.substring(0,s.length()-4);
          ausgabeFormat.setSelectedItem(s);
        }
      } catch(Exception ee) {
        Dialog.error(International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
      }
    }
  }


  // gespeicherten Eintrag bearbeiten
  void doEdit() {
    if (statList.getSelectedIndices().length == 0) return;
    if (statList.getSelectedIndices().length>1) {
      Dialog.infoDialog(International.getString("Zu viele Einträge markiert"),
              International.getString("Es kann immer nur eine Statistikeinstellung bearbeitet werden!"));
      return;
    }
    String s = (String)statList.getSelectedValue();
    if (s.lastIndexOf(" (")<0) {
      Dialog.error(International.getString("Statistikdatei hat ungültiges Format!"));
      return;
    }
    s = s.substring(0,s.lastIndexOf(" ("));
    Daten.fahrtenbuch.getDaten().statistik.getFirst(s);
    DatenFelder f = (DatenFelder)Daten.fahrtenbuch.getDaten().statistik.getComplete();
    if (f == null) return;

    setSavedStat(f,false);
    aktStatName = s;
    aktStatAuchEfaDirekt = f.get(StatSave.AUCHINEFADIREKT).equals("+");
  }



  // allgemeine Einstellungen zur Statistik, die nicht aus der Eingabe gewonnen werden können
  public static void allgStatistikDaten(StatistikDaten d) {
    d.ausgabe = StatistikDaten.AUSGABE_APPLICATION;
    d.anzMitglieder = Daten.fahrtenbuch.getDaten().anzMitglieder;
  }


  public void efaWettVervollständigen(EfaWett efaWett) {
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen(...) - START");
    if (efaWett.meldung == null) {
      Dialog.infoDialog(International.onlyFor("Keine Meldungen","de"),
              International.onlyFor("Im gewählten Zeitraum haben keine Teilnehmer die Bedingungen erfüllt!","de"));
      return;
    }
    String dat = Daten.fahrtenbuch.getFileName();
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen(...): dat == "+dat);
    int to;
    if ((to = dat.toUpperCase().lastIndexOf(Daten.fileSep)) >= 0) {
      Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen(...): to == "+to);
      dat = dat.substring(0,to+1);
      Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen(...): dat == "+dat);
      efaWett.datei = dat+EfaUtil.replace(efaWett.allg_wett,".","_",true)+"_"+EfaUtil.replace(efaWett.allg_wettjahr,"/","-",true)+".EFW";
      Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen(...): efaWett.datei == "+efaWett.datei);
    } // anderenfalls Standardwert (StatistikDaten.dateiEfaWett)

    VereinsConfigFrame dlg = new VereinsConfigFrame(this,efaWett);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen(...) - END");
  }
  public void efaWettVervollständigen2(EfaWett efaWett) {
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen2(...) - START");
    EfaWettSelectAndCompleteFrame dlg = new EfaWettSelectAndCompleteFrame(this,efaWett);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen2(...) - END");
  }
  public void efaWettVervollständigen3(EfaWett efaWett, String meldegeld, Vector papierFahrtenhefte) {
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen3(...) - START");
    Statistik.schreibeEfaWett(efaWett);

    EfaWettFertigFrame dlg = new EfaWettFertigFrame(this,efaWett,meldegeld,papierFahrtenhefte);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_STATISTICS,"StatistikFrame.efaWettVervollständigen3(...) - END");
  }


  void readLayoutFiles(String dir) {
    File f = new File(dir);
    if (!f.isDirectory()) return;
    File[] fnames = f.listFiles();
    if (fnames == null) return;
    for (int i=0; i<fnames.length; i++) {
      if (fnames[i].isFile()) {
        String fname = fnames[i].getName();
        if (fname.toUpperCase().endsWith(".XSL"))
          ausgabeFormat.addItem(fname.substring(0,fname.length()-4));
      }
    }
  }


  void setAusgabeFormat(int nr) {
    ausgabeFormat.removeAllItems();

    if (ausgabeExt[nr] != null && ausgabeExt[nr].equals("pdf"))
      ausgabeFormat.addItem("--- " + International.getString("bitte wählen") + " ---");
    else
      ausgabeFormat.addItem(International.getString("Standard"));
    if (nr == 0 || nr == 2 ||
        (ausgabeExt[nr] != null && ausgabeExt[nr].equals("html")))
      readLayoutFiles(Daten.efaStyleDirectory+"html");
    if (ausgabeExt[nr] != null && ausgabeExt[nr].equals("pdf")) {
      readLayoutFiles(Daten.efaStyleDirectory+"pdf");
      if (ausgabeFormat.getItemCount() > 1)
        ausgabeFormat.setSelectedIndex(1);
    }
    if (ausgabeExt[nr] != null && ausgabeExt[nr].equals("xml"))
      readLayoutFiles(Daten.efaStyleDirectory+"xml");
  }

  void ausgabeArt_itemStateChanged(ItemEvent e) {
    setAusgabeFormat(ausgabeArt.getSelectedIndex());
    ausgabeDateiSetEnabeld(ausgabeArt.getSelectedIndex());
  }


  void ausgabeDateiSetEnabeld(int nr) {
    ausgabeDateiLabel.setEnabled(ausgabeExt[nr] != null);
    ausgabeDatei.setEnabled(ausgabeExt[nr] != null);
    ausgabeDateiButton.setEnabled(ausgabeExt[nr] != null);

    String fname;
    if (ausgabeExt[nr] != null) {
      if (ausgabeDatei.getText().trim().length() == 0)
        if (!Daten.fahrtenbuch.getFileName().equals(""))
          fname = Daten.fahrtenbuch.getFileName();
        else
          fname = Daten.dateiHTML;
      else
        fname = ausgabeDatei.getText().trim();
      int pos;
      if ( (pos = fname.lastIndexOf(".")) >= 0)
        fname = fname.substring(0,pos) + "." + ausgabeExt[nr];
      else
        fname += "." + ausgabeExt[nr];
      ausgabeDatei.setText(fname);
    }
  }

  void zeitVorjahresvergleich_actionPerformed(ActionEvent e) {
    if (e != null && ((JCheckBox)e.getSource()).isSelected()) zeitFbUebergreifend.setSelected(true);
  }


  public void nurAuswahlGruppe_itemStateChanged(ItemEvent e) {
    if (nurAuswahlGruppe.isSelected()) {
      nameTeil.setEnabled(false);
      nurNameLabel.setText(International.getString("Nur Gruppe")+": ");
    } else {
      nameTeil.setEnabled(true);
      nurNameLabel.setText(International.getString("Nur Name")+": ");
    }
  }

  void nurName_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

  void mNurBoot_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }


}
