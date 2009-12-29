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
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;
import java.io.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.FocusManager;
import de.nmichael.efa.direkt.EfaDirektFrame;
import de.nmichael.efa.direkt.AdminFrame;
import de.nmichael.efa.direkt.Admin;
import de.nmichael.efa.direkt.AdminLoginFrame;
import de.nmichael.efa.direkt.NachrichtenAnAdmin;
import de.nmichael.efa.direkt.Nachricht;
import de.nmichael.efa.direkt.NachrichtAnAdminFrame;

// @i18n complete

public class EfaFrame extends JFrame implements AutoCompletePopupWindowCallback {
  public EfaFrameFocusManager focusManager = null;
  boolean datensatzGeaendert;   // Für die Sicherheitsabfrage
  boolean neuerDatensatz;       // ob akt. Datensatz ein neuer Datensatz, oder ein bearbeiteter ist
  boolean neuerDatensatz_einf;  // ob der neue Datensatz eingefügt wird (dann beim Hinzufügen keine Warnung wegen kleiner Lfd. Nr.!)
  DatenFelder aktBoot;          // aktuelle Bootsdaten (um nächstes Eingabefeld zu ermitteln)
  DatenFelder aktDatensatz;     // aktuell angezeigter Datensatz
  DatenFelder refDatensatz=null;// Referenz-Datensatz (zuletzt angezeigter Datensatz, wenn neuer erstellt wird)
  String altesZiel = "";        // zum Vergleichen, ob Ziel geändert wurde

  int oldFahrtDauerSelectedIndex=0; // letzte Position von fahrtDauer
  boolean ignoreFahrtDauerItemStateChanges = false; // zum Unterdrücken der StateChanges beim bearbeiten der Liste nach Hinzufügen einer Fahrt
  String fahrtArt_neueMehrtagesfahrt = null;
  String fahrtArt_mehrtagesfahrtBearbeiten = null;
  String fahrtArt_mehrtagesfahrtKonfigurieren = null;

  int mannschAuswahl = 0;       // 0: 1-8 sichtbar; 1: 9-16 sichtbar; 2: 17-24 sichtbar
  boolean continueMTour;        // legt fest, ob nächster neuer Eintrag mit unverändertem MTour-Feld (d.h. gleiches Element ausgewählt) begonnen werden soll
  String refDate="";            // Referenzdatum
  boolean startEfaTour=false;   // nach dem Aufbau des Frames die efa-Tour starten
  boolean askForOpenNewFb=false;// fragen, ob ein neues FB angelegt werden soll (nur beim ersten Start)
  String startOpenFb=null;      // Fahrtenbuch, das beim Starten von efa geöffnet werden soll (per -fb <name> angegeben)
  int datumErrorCount=0;        // zum Zählen der Fehler, die beim Setzen des Datums aufgetreten sind
  int currentObmann=-1;         // aktuell ausgewählter Obmann
  int lfdNrForNewEntry = -1;    // LfdNr (zzgl. 1), die für den nächsten per "Neu" erzeugten Datensatz verwendet werden soll; wenn <0, dann wird "last+1" verwendet
  Mannschaften mannschaften = null; // Liste von Standardmannschaften, die Ruderer oder Steuerleute enthalten

  int mode;                     // Modus, in dem dieses Frame betrieben wird (siehe MODE_...-Konstanten)
  String direkt_boot;           // Bootsname, mit dem EfaFrame aufgerufen wurde
  EfaDirektFrame efaDirektFrame;
  AdminFrame efaDirektAdminFrame;
  Admin admin = null;
  NachrichtenAnAdmin nachrichtenAnAdmin;
  int positionX,positionY;      // Position des Frames, wenn aus efaDirekt aufgerufen
  private boolean _inObmannUpdate = false;
  Hashtable defaultColor = new Hashtable();

  public static final int MODE_FULL = 0;
  public static final int MODE_START = 1;
  public static final int MODE_START_KORREKTUR = 2;
  public static final int MODE_ENDE = 3;
  public static final int MODE_NACHTRAG = 4;
  public static final int MODE_ADMIN = 5;
  public static final int MODE_ADMIN_NUR_FAHRTEN = 6;

  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileOpen = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JMenuItem jMenuFileSave = new JMenuItem();
  JMenuItem jMenuFileSaveAs = new JMenuItem();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuKonfiguration = new JMenu();
  JMenuItem jMenuItem4 = new JMenuItem();
  JMenuItem jMenuItem5 = new JMenuItem();
  JMenuItem jMenuItem6 = new JMenuItem();
  JMenuItem jMenuItem7 = new JMenuItem();
  JToolBar toolBar = new JToolBar();
  JButton PrevButton = new JButton();
  JButton NextButton = new JButton();
  JButton NewButton = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton addButton = new JButton();
  JLabel lfdnrLabel = new JLabel();
  JTextField lfdnr = new JTextField();
  JLabel datumLabel = new JLabel();
  JTextField datum = new JTextField();
  JLabel bootLabel = new JLabel();
  JLabel stmLabel = new JLabel();
  JLabel abfahrtLabel = new JLabel();
  JLabel ankunftLabel = new JLabel();
  JLabel zielLabel = new JLabel();
  JLabel bootskmLabel = new JLabel();
  JLabel mannschkmLabel = new JLabel();
  JLabel bemerkLabel = new JLabel();
  JTextField stm = new JTextField();
  JTextField abfahrt = new JTextField();
  JTextField ankunft = new JTextField();
  JTextField ziel = new JTextField();
  JTextField bootskm = new JTextField();
  JTextField mannschkm = new JTextField();
  JTextField bemerk = new JTextField();
  JTextField[] mannsch = new JTextField[Fahrtenbuch.ANZ_MANNSCH];
  JLabel mannsch1_label = new JLabel();
  JLabel mannsch2_label = new JLabel();
  JLabel mannsch3_label = new JLabel();
  JLabel mannsch4_label = new JLabel();
  JLabel mannsch5_label = new JLabel();
  JLabel mannsch6_label = new JLabel();
  JLabel mannsch7_label = new JLabel();
  JLabel mannsch8_label = new JLabel();
  JTextField boot = new JTextField();
  JButton FirstButton = new JButton();
  JButton LastButton = new JButton();
  JMenuItem jMenuNew = new JMenuItem();
  JButton bootButton = new JButton();
  JButton stmButton = new JButton();
  JButton[] mannschButton = new JButton[8];
  JButton zielButton = new JButton();

  JMenu jMenuStatistik = new JMenu();
  JMenuItem jMenuItemKilometerliste = new JMenuItem();
  JButton SuchButton = new JButton();
  JLabel jLabel22 = new JLabel();
  JButton ButtonDelete = new JButton();
  JLabel jLabel23 = new JLabel();
  JComboBox fahrtDauer = new JComboBox();
  JLabel artDerFahrtLabel = new JLabel();
  JButton weitereMannschButton = new JButton();
  JButton mannschaftSelectButton = new JButton();
  JMenuItem jMenuItem2 = new JMenuItem();
  JMenuItem jMenuImport = new JMenuItem();
  JMenuItem jMenuItem1 = new JMenuItem();
  JLabel gehezuLabel = new JLabel();
  JTextField geheZu = new JTextField();
  JMenuItem jMenuHilfeJavaKonsole = new JMenuItem();
  JMenu jMenuFahrtenbuch = new JMenu();
  JMenuItem jMenuItem3 = new JMenuItem();
  JLabel wotag = new JLabel();
  JMenuItem jMenuDokumentation = new JMenuItem();
  JMenuItem jMenu_efaHomepage = new JMenuItem();
  JMenuItem jMenu_startTour = new JMenuItem();
  JMenuItem jMenu_Willkommen = new JMenuItem();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem8 = new JMenuItem();
  JMenuItem jMenuBackup = new JMenuItem();
  JMenuItem jMenuItem9 = new JMenuItem();
  JMenuItem jMenuItem10 = new JMenuItem();
  JLabel jLabel5 = new JLabel();
  JButton InsertButton = new JButton();
  JMenuItem jMenuItem11 = new JMenuItem();
  JButton efaButton = new JButton();
  JMenuItem jMenuItem12 = new JMenuItem();
  public JLabel obmannLabel = new JLabel();
  public JComboBox obmann = new JComboBox();
  JMenuItem jMenuOnlineUpdate = new JMenuItem();
  JLabel infoLabel = new JLabel();
  JButton bootsschadenButton = new JButton();
  JMenuItem jMenuItem13 = new JMenuItem();
  JMenuItem jMenuFileDatensicherung = new JMenuItem();
  JMenuItem jMenuItem14 = new JMenuItem();

  public void packFrame(String traceInfo) {
    this.pack();
  }


  /**Construct the frame from Efa */
  public EfaFrame(String fb) {
    this.mode = MODE_FULL;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      this.startOpenFb = fb;
      iniFrameData();
      jbInit();
      infoLabel.setVisible(false);
      bootsschadenButton.setVisible(false);
      packFrame("EfaFrame(...) from Efa");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    Dialog.frameOpened(this);
  }


  /**Construct the frame from direkt.EfaDirektFrame */
  public EfaFrame(EfaDirektFrame efaDirektFrame, NachrichtenAnAdmin nachrichtenAnAdmin) {
    this.efaDirektFrame = efaDirektFrame;
    this.nachrichtenAnAdmin = nachrichtenAnAdmin;
    this.mode = MODE_START;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      iniFrameData();
      jbInit();
      infoLabel.setVisible(Daten.efaConfig != null && Daten.efaConfig.efaDirekt_showEingabeInfos);
      bootsschadenButton.setVisible(Daten.efaConfig != null && Daten.efaConfig.efaDirekt_showBootsschadenButton);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setJMenuBar(null);
    contentPane.remove(toolBar);
    this.geheZu.setVisible(false);
    this.gehezuLabel.setVisible(false);
    fahrtDauerAddItems(true); // add *all* items to fahrtDauer (including MEHRTAGESFAHRT)
    this.efaButton.setVisible(false);

    // bei entspr. Einstellung Obmann-Auswahlliste ausblenden
    if (Daten.efaConfig != null && !Daten.efaConfig.showObmann) {
      this.obmannLabel.setVisible(false);
      this.obmann.setVisible(false);
    }

    this.setResizable(false);
    packFrame("EfaFrame(...) from EfaDirektFrame");
  }

  /**Construct the frame from direkt.AdminFrame */
  public EfaFrame(AdminFrame frame, EfaDirektFrame efaDirektFrame, String fb, Admin admin, int mode) {
    this.efaDirektAdminFrame = frame;
    this.efaDirektFrame = efaDirektFrame;
    this.admin = admin;
    this.mode = mode;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      this.startOpenFb = fb;
      iniFrameData();
      jbInit();
      infoLabel.setVisible(false);
      bootsschadenButton.setVisible(false);
      packFrame("EfaFrame(...) from AdminFrame");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    if (mode == MODE_ADMIN_NUR_FAHRTEN) {
      this.setJMenuBar(null);
    }
    Dialog.frameOpened(this);
  }

  private void iniFrameData() {
      if (Daten.efaTypes != null && Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_MULTIDAY)) {
          fahrtArt_neueMehrtagesfahrt = ">>> " + International.getString("neue Mehrtagesfahrt");
          if (mode == MODE_ADMIN_NUR_FAHRTEN) {
              fahrtArt_mehrtagesfahrtBearbeiten = ">>> " + International.getString("Mehrtagesfahrten bearbeiten");
          }
          fahrtArt_mehrtagesfahrtKonfigurieren = ">>> " + International.getString("Mehrtagesfahrt konfigurieren");
      }
  }

  public boolean isDirectMode() {
    return mode == MODE_START || mode == MODE_START_KORREKTUR || mode == MODE_ENDE || mode == MODE_NACHTRAG;
  }

  public boolean isAdminMode() {
    return mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN;
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      if (isDirectMode() || mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN) cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      switch(mode) {
        case MODE_START:    Help.getHelp(this,"EfaFrame_Start"); break;
        case MODE_START_KORREKTUR:    Help.getHelp(this,"EfaFrame_Start"); break;
        case MODE_ENDE:     Help.getHelp(this,"EfaFrame_Ende"); break;
        case MODE_NACHTRAG: Help.getHelp(this,"EfaFrame_Nachtrag"); break;
        default:            Help.getHelp(this,this.getClass());
      }
    }

    if (!isDirectMode() && evt.getActionCommand().equals("KEYSTROKE_ACTION_3")) { // F3
      if (Daten.fahrtenbuch == null) return;
      if (!sicherheitsabfrageDatensatz()) return;
      if (Daten.fahrtenbuch.countElements() == 0) return;
      if ( (Daten.suchMode == Daten.SUCH_NORMAL && Daten.such != null && Daten.such.length() != 0) ||
           (Daten.suchMode == Daten.SUCH_ERROR) )
        SuchFrame.search(this,this);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_4")) { // F4
      if (aktDatensatz == null && Daten.mannschaften != null && boot.getText().trim().length()!=0 &&
          Daten.mannschaften.getExact(boot.getText().trim())!= null)
        setStandardMannschaft((DatenFelder)Daten.mannschaften.getComplete());
    }
    // F10 ist nur Dummy, damit beim Vervollständigen im Bemerkungs-Feld nicht das Menü aufklappt
    if (!isDirectMode() && evt.getActionCommand().equals("KEYSTROKE_ACTION_6")) { // Alt-F10
      EfaUtil.gc();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_7")) { // F11
      Dialog.infoDialog(
          "Das ist ein sehr langer Satz, der am liebsten über den gesamten Bildschirm gehen würde und nirgends aufhören würde, wenn er nur die Gelegenheit dazu bekäme, denn das würde er wirklich gerne machen, nur leider läßt ihn efa nicht!\n"+
          "DasisteinsehrlangerSatz,deramliebstenüberdengesamtenBildschirmgehenwürdeundnirgendsaufhörenwürde,wennernurdieGelegenheitdazubekäme,denndaswürdeerwirklichgernemachen,nurleiderläßtihnefanicht!\n\n"+
          "Wenn hinter Rollen Rollen rollen, rollen Rollen Rollen nach. Wenn hinter Griechen Griechen kriechen, kriechen Griechen Griechen nach.\n\n"+
          "How much wood would a woodchuck chuck if a woodchuck could chuck wood? If a woodchuck could chuck wood, the wood that a woodchuck would chuck is the wood that a woodchuck could chuck if it could chuck wood.\n\n"+
          "A farmer has set out to market with ten donkeys, on one of which he rode. After a while, he began to wonder if any of the donkeys had strayed and he began counting -- there seemed to be only nine. Disturbed, he dismounted and walked around the herd, counting carefully -- and there were ten after all. So he remounted and went on riding, until worry beset him again. So he counted another time ... and there were nine. So, once again, he dismounted and walked about couting carefully to find ten. The process is repeated until he finally solves the problem, carrying one donkey on his back and driving the other nine before him.\n\n"+
          "2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997\n\n"+

          "Ein Mann rudert mit konstanter Geschwindigkeit den Mississippi hinauf. Unter einer Brücke rollt ihm eine halbvolle Flasche unbemerkt aus dem Boot. Nach 10 min. bemerkt er den Verlust, kehrt um und erreicht die Flasche 1 km hinter der Brücke. Wie schnell fließt der Mississippi?\n\n"+
          "I saw a man upon the stair,\nA little man who wasn't there.\nHe wasn't there again today;\nGee, I wish he'd go away.\n\n"+
          "Drei Jungen kommen in einen Laden, um einen Fußball für 30 Mark zu kaufen. Jeder gibt dem Lehrling an der Kasse 10 Mark. Doch der Chef hatte am Vormittag den Fußball um 5 Mark auf 25 Mark heruntergesetzt. Also drückt er dem Lehrling 5 Mark in die Hand und schickt ihn los, um den Jungen das zuviel bezahlte Geld zu bringen. Der Lehrling will etwas Kohle selber einsacken und gibt jedem der drei Jungen eine Mark wieder, behält also 2 Mark. Jeder der drei Jungen hat damit 9 Mark bezahlt, macht in Summe 27 Mark. Der Lehrling hat 2 Mark behalten, macht 29 Mark. Wo ist die dreißigste Mark geblieben?");
    }
  }


  /**Component initialization*/
  private void jbInit() throws Exception  {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1","F2","F3","F4","F10","shift F10","F11"},
                       new String[] {"keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    setIconImage(Toolkit.getDefaultToolkit().createImage(EfaFrame.class.getResource("/de/nmichael/efa/img/efa_icon.gif")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    this.setTitle(Daten.EFA_LONGNAME);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
      public void windowIconified(WindowEvent e) {
        this_windowIconified(e);
      }
    });

    Mnemonics.setButton(this, jMenuFile, International.getStringWithMnemonic("Datei"));
    jMenuFileOpen.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_open.gif")));
    Mnemonics.setMenuButton(this, jMenuFileOpen, International.getStringWithMnemonic("Fahrtenbuch öffnen"));
    jMenuFileOpen.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuFileOpen_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, jMenuHelp, International.getStringWithMnemonic("Info"));
    jMenuHelpAbout.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_about.gif")));
    Mnemonics.setMenuButton(this, jMenuHelpAbout, International.getStringWithMnemonic("Über"));
    jMenuHelpAbout.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jMenuFileSave.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_save.gif")));
    Mnemonics.setMenuButton(this, jMenuFileSave, International.getStringWithMnemonic("Fahrtenbuch speichern"));
    jMenuFileSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSave_actionPerformed(e);
      }
    });
    jMenuFileSaveAs.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_saveas.gif")));
    Mnemonics.setMenuButton(this, jMenuFileSaveAs, International.getStringWithMnemonic("Fahrtenbuch speichern unter"));
    jMenuFileSaveAs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveAs_actionPerformed(e);
      }
    });
    jMenuFileExit.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_exit.gif")));
    Mnemonics.setMenuButton(this, jMenuFileExit, International.getStringWithMnemonic("Programm beenden"));
    jMenuFileExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuKonfiguration, International.getStringWithMnemonic("Administration"));
    Mnemonics.setMenuButton(this, jMenuItem4, International.getStringWithMnemonic("Einstellungen zum Fahrtenbuch"));
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem4_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem5, International.getStringWithMnemonic("Mitgliederliste"));
    jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem5_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem6, International.getStringWithMnemonic("Bootsliste"));
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem6_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem7, International.getStringWithMnemonic("Zielliste"));
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem7_actionPerformed(e);
      }
    });
    PrevButton.setMargin(new Insets(3,5,3,5));
    PrevButton.setNextFocusableComponent(NextButton);
    PrevButton.setToolTipText(International.getString("zum vorherigen Eintrag springen"));
    Mnemonics.setButton(this, PrevButton, "<< "+International.getStringWithMnemonic("Vorheriger"));
    PrevButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PrevButton_actionPerformed(e);
      }
    });
    NextButton.setMargin(new Insets(3,5,3,5));
    NextButton.setNextFocusableComponent(LastButton);
    NextButton.setToolTipText(International.getString("zum nächsten Eintrag springen"));
    Mnemonics.setButton(this, NextButton, International.getStringWithMnemonic("Nächster")+" >>");
    NextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        NextButton_actionPerformed(e);
      }
    });
    NewButton.setMargin(new Insets(3,5,3,5));
    NewButton.setNextFocusableComponent(InsertButton);
    NewButton.setToolTipText(International.getString("neuen Eintrag erstellen"));
    Mnemonics.setButton(this, NewButton, International.getStringWithMnemonic("Neu"));
    NewButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        NewButton_actionPerformed(e);
      }
    });
    mainPanel.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, lfdnrLabel, International.getStringWithMnemonic("Lfd. Nr.")+": ");
    lfdnrLabel.setLabelFor(lfdnr);
    Mnemonics.setLabel(this, datumLabel, International.getStringWithMnemonic("Datum")+": ");
    datumLabel.setLabelFor(datum);
    datum.setNextFocusableComponent(boot);
    Dialog.setPreferredSize(datum,100,19);
//    datum.setPreferredSize(new Dimension(100, Dialog.TEXTFIELD_HEIGHT));
    datum.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        datum_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("datum");
      }
    });
    datum.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        datum_keyPressed(e);
      }
    });
    Mnemonics.setLabel(this, bootLabel, International.getStringWithMnemonic("Boot")+": ");
    bootLabel.setLabelFor(boot);
    Mnemonics.setLabel(this, stmLabel, International.getStringWithMnemonic("Steuermann")+": ");
    stmLabel.setLabelFor(stm);

    stmLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        obmannSelected(0);
      }
    });


    // set up mannsch Textfields
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      mannsch[i] = new JTextField();
    }
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      mannsch[i].setNextFocusableComponent( ( (i+1) % 8 == 0 ? abfahrt : mannsch[i+1]));
      Dialog.setPreferredSize(mannsch[i],200,19);
//      mannsch[i].setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
      mannsch[i].addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          mannsch_keyReleased(e);
        }
        public void keyTyped(KeyEvent e) {
          eintragGeaendert(e);
        }
        public void keyPressed(KeyEvent e) {
          mannsch_keyPressed(e);
        }
      });
      if (i == 0 ) mannsch[i].addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          mannsch1_focusLost(e);
        }
      });
      mannsch[i].addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          mannsch_focusLost(e);
        }
      });
      mannsch[i].addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          decolorize(e);
        }
        public void focusGained(FocusEvent e) {
          colorize(e);
          showHint("mannsch");
        }
      });
    }
    for (int i=8; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      mannsch[i].setVisible(false);
    }

    // set up mannschButton buttons
    for (int i=0; i<8; i++) {
      mannschButton[i] = new JButton();
      mannschButton[i].setNextFocusableComponent( (i<7 ? mannsch[i+1] : abfahrt) );
      Dialog.setPreferredSize(mannschButton[i],15,11);
//      mannschButton[i].setPreferredSize(new Dimension(15, 11));
      mannschButton[i].setToolTipText(International.getString("Ruderer in die Mitgliederliste aufnehmen"));
      mannschButton[i].addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mannschButton_actionPerformed(e);
        }
      });
    }

    Mnemonics.setLabel(this, mannsch1_label, International.getStringWithMnemonic("Mannschaft")+": 1: ");
    mannsch1_label.setLabelFor(mannsch[0]);
    mannsch2_label.setLabelFor(mannsch[1]);
    mannsch2_label.setText("2: ");
    mannsch3_label.setLabelFor(mannsch[2]);
    mannsch3_label.setText("3: ");
    mannsch4_label.setHorizontalAlignment(SwingConstants.RIGHT);
    mannsch4_label.setLabelFor(mannsch[3]);
    mannsch4_label.setText("4: ");
    mannsch5_label.setLabelFor(mannsch[4]);
    mannsch5_label.setText("  5: ");
    mannsch6_label.setLabelFor(mannsch[5]);
    mannsch6_label.setText("  6: ");
    mannsch7_label.setLabelFor(mannsch[6]);
    mannsch7_label.setText("  7: ");
    mannsch8_label.setLabelFor(mannsch[7]);
    mannsch8_label.setText("  8: ");
    mannsch1_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(1 + mannschAuswahl*8); } });
    mannsch2_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(2 + mannschAuswahl*8); } });
    mannsch3_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(3 + mannschAuswahl*8); } });
    mannsch4_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(4 + mannschAuswahl*8); } });
    mannsch5_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(5 + mannschAuswahl*8); } });
    mannsch6_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(6 + mannschAuswahl*8); } });
    mannsch7_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(7 + mannschAuswahl*8); } });
    mannsch8_label.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(MouseEvent e) { obmannSelected(8 + mannschAuswahl*8); } });
    Mnemonics.setLabel(this, abfahrtLabel, International.getStringWithMnemonic("Abfahrt")+": ");
    abfahrtLabel.setLabelFor(abfahrt);
    Mnemonics.setLabel(this, ankunftLabel, International.getStringWithMnemonic("Ankunft")+": ");
    ankunftLabel.setLabelFor(ankunft);
    Mnemonics.setLabel(this, zielLabel, International.getStringWithMnemonic("Ziel")+": ");
    zielLabel.setLabelFor(ziel);
    Mnemonics.setLabel(this, bootskmLabel, International.getStringWithMnemonic("Boots-Km")+": ");
    bootskmLabel.setLabelFor(bootskm);
    mannschkmLabel.setToolTipText("");
    Mnemonics.setLabel(this, mannschkmLabel, International.getStringWithMnemonic("Mannsch.-Km")+": ");
    mannschkmLabel.setLabelFor(mannschkm);
    Mnemonics.setLabel(this, bemerkLabel, International.getStringWithMnemonic("Bemerkungen")+": ");
    bemerkLabel.setLabelFor(bemerk);
    stm.setNextFocusableComponent(mannsch[1]);
    Dialog.setPreferredSize(stm,200,19);
//    stm.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    stm.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        stm_keyReleased(e);
      }
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        stm_keyPressed(e);
      }
    });
    stm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        stm_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("stm");
      }
    });
    abfahrt.setNextFocusableComponent(ankunft);
    Dialog.setPreferredSize(abfahrt,200,19);
//    abfahrt.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    abfahrt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        abfahrt_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        abfahrt_focusGained(e);
        showHint("abfahrt");
      }
    });
    abfahrt.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        abfahrt_keyPressed(e);
      }
    });
    ankunft.setNextFocusableComponent(ziel);
    Dialog.setPreferredSize(ankunft,200,19);
//    ankunft.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    ankunft.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        ankunft_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("ankunft");
      }
    });
    ankunft.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        ankunft_keyPressed(e);
      }
    });
    ziel.setNextFocusableComponent(bootskm);
    ziel.setMinimumSize(new Dimension(4, 19));
    Dialog.setPreferredSize(ziel,400,19);
//    ziel.setPreferredSize(new Dimension(400, Dialog.TEXTFIELD_HEIGHT));
    ziel.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        ziel_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        ziel_focusGained(e);
        showHint("ziel");
      }
    });
    ziel.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        ziel_keyReleased(e);
      }
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        ziel_keyPressed(e);
      }
    });
    bootskm.setNextFocusableComponent(mannschkm);
    Dialog.setPreferredSize(bootskm,200,19);
//    bootskm.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    bootskm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        bootskm_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("bootskm");
      }
    });
    bootskm.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        bootskm_keyPressed(e);
      }
    });
    mannschkm.setNextFocusableComponent(bemerk);
    Dialog.setPreferredSize(mannschkm,200,19);
//    mannschkm.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    mannschkm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        colorize(e);
        mannschkm_focusGained(e);
      }
      public void focusLost(FocusEvent e) {
        decolorize(e);
        mannschkm_focusLost(e);
      }
    });
    mannschkm.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        mannschkm_keyPressed(e);
      }
    });
    bemerk.setNextFocusableComponent(addButton);
    Dialog.setPreferredSize(bemerk,200,19);
//    bemerk.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    bemerk.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        bemerk_keyPressed(e);
      }
    });
    bemerk.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("bemerk");
      }
      public void focusLost(FocusEvent e) {
        decolorize(e);
      }
    });


    lfdnr.setNextFocusableComponent(datum);
    Dialog.setPreferredSize(lfdnr,200,19);
//    lfdnr.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    lfdnr.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        lfdnr_focusLost(e);
      }
    });
    lfdnr.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        lfdnr_keyPressed(e);
      }
    });
    boot.setNextFocusableComponent(stm);
    Dialog.setPreferredSize(boot,200,19);
//    boot.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    boot.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        decolorize(e);
        boot_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("boot");
      }
    });
    boot.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        boot_keyReleased(e);
      }
      public void keyTyped(KeyEvent e) {
        eintragGeaendert(e);
      }
      public void keyPressed(KeyEvent e) {
        boot_keyPressed(e);
      }
    });
    addButton.setNextFocusableComponent(FirstButton);
    addButton.setToolTipText(International.getString("aktuell angezeigten Eintrag zum Fahrtenbuch hinzufügen"));
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Eintrag zum Fahrtenbuch hinzufügen"));
    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButton_actionPerformed(e);
      }
    });
    addButton.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("addButton");
      }
      public void focusLost(FocusEvent e) {
        decolorize(e);
      }
    });
    FirstButton.setMargin(new Insets(3,5,3,5));
    FirstButton.setNextFocusableComponent(PrevButton);
    FirstButton.setToolTipText(International.getString("zum ersten Eintrag springen"));
    Mnemonics.setButton(this, FirstButton, International.getStringWithMnemonic("Erster"));
    FirstButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FirstButton_actionPerformed(e);
      }
    });
    LastButton.setMargin(new Insets(3,5,3,5));
    LastButton.setNextFocusableComponent(NewButton);
    LastButton.setToolTipText(International.getString("zum letzten Eintrag springen"));
    Mnemonics.setButton(this, LastButton, International.getStringWithMnemonic("Letzter"));
    LastButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        LastButton_actionPerformed(e);
      }
    });
    jMenuNew.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_new.gif")));
    Mnemonics.setMenuButton(this, jMenuNew, International.getStringWithMnemonic("Neues Fahrtenbuch erstellen"));
    jMenuNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuNew_actionPerformed(e);
      }
    });
    bootButton.setNextFocusableComponent(stm);
    Dialog.setPreferredSize(bootButton,15,11);
//    bootButton.setPreferredSize(new Dimension(15, 11));
    bootButton.setToolTipText(International.getString("Boot in die Bootsliste aufnehmen"));
    bootButton.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        bootButton_focusLost(e);
      }
    });
    bootButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootButton_actionPerformed(e);
      }
    });

    stmButton.setNextFocusableComponent(mannsch[0]);
    Dialog.setPreferredSize(stmButton,15,11);
//    stmButton.setPreferredSize(new Dimension(15, 11));
    stmButton.setToolTipText(International.getString("Steuermann in die Mitgliederliste aufnehmen"));
    stmButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stmButton_actionPerformed(e);
      }
    });

    zielButton.setNextFocusableComponent(bootskm);
    Dialog.setPreferredSize(zielButton,15,11);
//    zielButton.setPreferredSize(new Dimension(15, 11));
    zielButton.setToolTipText(International.getString("Ziele in die Zielliste aufnehmen"));
    zielButton.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        zielButton_focusLost(e);
      }
    });
    zielButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        zielButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, jMenuStatistik, International.getStringWithMnemonic("Ausgabe"));
    jMenuItemKilometerliste.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_stat.gif")));
    Mnemonics.setMenuButton(this, jMenuItemKilometerliste, International.getStringWithMnemonic("Statistiken und Meldedateien erstellen"));
    jMenuItemKilometerliste.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItemKilometerliste_actionPerformed(e);
      }
    });
    SuchButton.setMargin(new Insets(3,5,3,5));
    SuchButton.setNextFocusableComponent(ButtonDelete);
    SuchButton.setToolTipText(International.getString("nach einem Eintrag suchen"));
    Mnemonics.setButton(this, SuchButton, International.getStringWithMnemonic("Suchen"));
    SuchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SuchButton_actionPerformed(e);
      }
    });
    jLabel22.setText("     ");
    ButtonDelete.setMargin(new Insets(3,5,3,5));
    ButtonDelete.setToolTipText(International.getString("aktuellen Eintrag löschen"));
    Mnemonics.setButton(this, ButtonDelete, International.getStringWithMnemonic("Löschen"));
    ButtonDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ButtonDelete_actionPerformed(e);
      }
    });
    jLabel23.setText("     ");
    Dialog.setPreferredSize(fahrtDauer,200,24);
    fahrtDauer.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        fahrtDauer_itemStateChanged(e);
      }
    });
    fahrtDauer.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("fahrtDauer");
      }
      public void focusLost(FocusEvent e) {
        decolorize(e);
      }
    });
    Mnemonics.setLabel(this, artDerFahrtLabel, International.getStringWithMnemonic("Art der Fahrt")+": ");
    artDerFahrtLabel.setLabelFor(fahrtDauer);
    Dialog.setPreferredSize(weitereMannschButton,200,19);
//    weitereMannschButton.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    weitereMannschButton.setToolTipText(International.getString("weitere Mannschaftsfelder anzeigen"));
    Mnemonics.setButton(this, weitereMannschButton, International.getStringWithMnemonic("restliche Mannschaft anzeigen"));
    weitereMannschButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        weitereMannschButton_actionPerformed(e);
      }
    });
    weitereMannschButton.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        showHint("weitereMannschButton");
      }
    });
    Dialog.setPreferredSize(mannschaftSelectButton,200,19);
    Mnemonics.setButton(this, mannschaftSelectButton, International.getStringWithMnemonic("Mannschaft auswählen"));
    mannschaftSelectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mannschaftSelectButton_actionPerformed(e);
      }
    });

    contentPane.setMinimumSize(new Dimension(590, 350));
    jMenuItem2.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_efawett.gif")));
    Mnemonics.setMenuButton(this, jMenuItem2, International.getStringWithMnemonic("Elektronische Wettbewerbsmeldung"));
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem2_actionPerformed(e);
      }
    });
    jMenuImport.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_import.gif")));
    Mnemonics.setMenuButton(this, jMenuImport, International.getStringWithMnemonic("Fahrtenbuch importieren"));
    jMenuImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuImport_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem1, International.getStringWithMnemonic("Mehrtagesfahrten"));
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, gehezuLabel, International.getStringWithMnemonic("gehe zu")+": ");
    gehezuLabel.setLabelFor(geheZu);
    Dialog.setPreferredSize(geheZu,50,19);
//    geheZu.setPreferredSize(new Dimension(50, Dialog.TEXTFIELD_HEIGHT));
    geheZu.setToolTipText(International.getString("direkt zu einer Laufenden Nummer springen"));
    geheZu.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        geheZu_keyReleased(e);
      }
    });
    geheZu.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        geheZu_focusLost(e);
      }
    });
    jMenuHilfeJavaKonsole.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_konsole.gif")));
    Mnemonics.setMenuButton(this, jMenuHilfeJavaKonsole, International.getStringWithMnemonic("Java-Konsole"));
    jMenuHilfeJavaKonsole.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHilfeJavaKonsole_actionPerformed(e);
      }
    });
    jMenuFahrtenbuch.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_fbadmin.gif")));
    Mnemonics.setMenuButton(this, jMenuFahrtenbuch, International.getStringWithMnemonic("Fahrtenbuch"));
    jMenuItem3.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_einst.gif")));
    Mnemonics.setMenuButton(this, jMenuItem3, International.getStringWithMnemonic("Allgemeine Einstellungen"));
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem3_actionPerformed(e);
      }
    });
    wotag.setForeground(Color.black);
    wotag.setToolTipText("");
    wotag.setText("-");
    jMenuDokumentation.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_doc.gif")));
    Mnemonics.setMenuButton(this, jMenuDokumentation, International.getStringWithMnemonic("Dokumentation"));
    jMenuDokumentation.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuDokumentation_actionPerformed(e);
      }
    });
    jMenu_efaHomepage.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_www.gif")));
    Mnemonics.setMenuButton(this, jMenu_efaHomepage, International.getStringWithMnemonic("efa-Homepage"));
    jMenu_efaHomepage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenu_efaHomepage_actionPerformed(e);
      }
    });
    jMenu_startTour.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_tour.gif")));
    Mnemonics.setMenuButton(this, jMenu_startTour, International.getStringWithMnemonic("Tour"));
    jMenu_startTour.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenu_startTour_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenu1, International.getStringWithMnemonic("Synonymlisten"));
    jMenu1.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_syn.gif")));
    Mnemonics.setMenuButton(this, jMenuItem8, International.getStringWithMnemonic("Mitglieder"));
    jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem8_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuBackup, International.getStringWithMnemonic("Backups einspielen"));
    jMenuBackup.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_backup.gif")));
    jMenuBackup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuBackup_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem9, International.getStringWithMnemonic("Boote"));
    jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem9_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem10, International.getStringWithMnemonic("Ziele"));
    jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem10_actionPerformed(e);
      }
    });
    jLabel5.setText("     ");
    InsertButton.setNextFocusableComponent(SuchButton);
    InsertButton.setToolTipText(International.getString("neuen Eintrag vor dem aktuellen einfügen"));
    InsertButton.setMargin(new Insets(3,5,3,5));
    Mnemonics.setButton(this, InsertButton, International.getStringWithMnemonic("Einf."));
    InsertButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InsertButton_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem11, International.getStringWithMnemonic("Zugriffsschutz"));
    jMenuItem11.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_writeprotect.gif")));
    jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem11_actionPerformed(e);
      }
    });
    efaButton.setPreferredSize(new Dimension(90, 23));
    efaButton.setIcon(new ImageIcon(EfaFrame.class.getResource(Daten.getEfaImage(1))));
    efaButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaButton_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem12, International.getStringWithMnemonic("Standardmannschaften"));
    jMenuItem12.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_standardmannsch.gif")));
    jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem12_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuOnlineUpdate, International.getStringWithMnemonic("efa Online-Update"));
    jMenuOnlineUpdate.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_onlupd.gif")));
    jMenuOnlineUpdate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuOnlineUpdate_actionPerformed(e);
      }
    });
    infoLabel.setForeground(Color.blue);
    infoLabel.setHorizontalTextPosition(SwingConstants.LEFT);
    infoLabel.setText("infoLabel");
    Mnemonics.setButton(this, bootsschadenButton, International.getStringWithMnemonic("Bootsschaden melden"));
    bootsschadenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootsschadenButton_actionPerformed(e);
      }
    });
    bootsschadenButton.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        colorize(e);
        showHint("bootsschadenButton");
      }
      public void focusLost(FocusEvent e) {
        decolorize(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem13, "DRV-Fahrtenabzeichen");
    jMenuItem13.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_fahrtenabzeichen.gif")));
    jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem13_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuFileDatensicherung, International.getStringWithMnemonic("Datensicherung"));
    jMenuFileDatensicherung.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_datensicherung.gif")));
    jMenuFileDatensicherung.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileDatensicherung_actionPerformed(e);
      }
    });
    Mnemonics.setMenuButton(this, jMenuItem14, International.getStringWithMnemonic("Gruppen"));
    jMenuItem14.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_gruppen.gif")));
    jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem14_actionPerformed(e);
      }
    });
    jMenuFile.add(jMenuNew);
    jMenuFile.add(jMenuFileOpen);
    jMenuFile.add(jMenuFileSave);
    jMenuFile.add(jMenuFileSaveAs);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuBackup);
    jMenuFile.add(jMenuFileDatensicherung);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuImport);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuOnlineUpdate);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuDokumentation);
    jMenuHelp.add(jMenu_startTour);
    jMenuHelp.add(jMenu_Willkommen);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenu_efaHomepage);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHilfeJavaKonsole);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuKonfiguration);
    jMenuBar1.add(jMenuStatistik);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);
    contentPane.add(toolBar, BorderLayout.NORTH);
    toolBar.add(FirstButton, null);
    toolBar.add(PrevButton, null);
    toolBar.add(NextButton, null);
    toolBar.add(LastButton, null);
    toolBar.add(jLabel5, null);
    toolBar.add(NewButton, null);
    toolBar.add(InsertButton, null);
    toolBar.add(jLabel23, null);
    toolBar.add(SuchButton, null);
    toolBar.add(jLabel22, null);
    toolBar.add(ButtonDelete, null);
    contentPane.add(mainPanel, BorderLayout.CENTER);
    contentPane.add(addButton, BorderLayout.SOUTH);
    mainPanel.add(lfdnr,     new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mainPanel.add(datum,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(stm,   new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(abfahrt,   new GridBagConstraints(1, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(ankunft,   new GridBagConstraints(1, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(ziel,   new GridBagConstraints(1, 10, 11, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(bootskm,   new GridBagConstraints(1, 11, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannschkm,   new GridBagConstraints(1, 12, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(bemerk,     new GridBagConstraints(1, 13, 2, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));

    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      mainPanel.add(mannsch[i],  new GridBagConstraints((i>=0 && i<=3 || i>=8 && i<=11 || i>=16 && i<=19 ? 1 : 5), 4+ (i%4), (i>=0 && i<=3 || i>=8 && i<=11 || i>=16 && i<=19 ? 2 : 7), 1, 0.0, 0.0
              ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    mainPanel.add(mannsch4_label,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannsch3_label,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannsch2_label,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannsch1_label,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    mainPanel.add(stmLabel,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(bootLabel,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(datumLabel,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(lfdnrLabel,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mainPanel.add(abfahrtLabel,   new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(ankunftLabel,   new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(zielLabel,   new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(bootskmLabel,   new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannschkmLabel,    new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    mainPanel.add(bemerkLabel,     new GridBagConstraints(0, 13, 1, 2, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 10, 0), 0, 0));
    mainPanel.add(mannsch5_label,   new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannsch6_label,   new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannsch7_label,   new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannsch8_label,    new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(boot,   new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(bootButton,   new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 22), 0, 0));
        mainPanel.add(stmButton,   new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));

    for (int i=0; i<8; i++)
      mainPanel.add(mannschButton[i],  new GridBagConstraints( (i<4 ? 3 : 12) , 4+ i%4, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));

    mainPanel.add(zielButton,    new GridBagConstraints(12, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 20), 0, 0));
    mainPanel.add(fahrtDauer,     new GridBagConstraints(4, 13, 9, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0));
    mainPanel.add(artDerFahrtLabel,   new GridBagConstraints(4, 12, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(mannschaftSelectButton,    new GridBagConstraints(5, 3, 7, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(weitereMannschButton,    new GridBagConstraints(4, 8, 9, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0));
    mainPanel.add(gehezuLabel,     new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mainPanel.add(geheZu,     new GridBagConstraints(6, 0, 5, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mainPanel.add(wotag,   new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    mainPanel.add(efaButton,              new GridBagConstraints(11, 0, 1, 3, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(10, 5, 0, 0), 0, 0));
    mainPanel.add(infoLabel,          new GridBagConstraints(0, 15, 7, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
    mainPanel.add(bootsschadenButton,            new GridBagConstraints(8, 15, 4, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    if (isDirectMode() || true) { // ;-)
      Mnemonics.setLabel(this, obmannLabel, "  "+International.getStringWithMnemonic("Obmann")+": ");
      obmannLabel.setLabelFor(obmann);
      obmann.setMinimumSize(new Dimension( (isDirectMode() ? 80 : 50), 17));
      Dialog.setPreferredSize(obmann,(isDirectMode() ? 80 : 50),19);
//      obmann.setPreferredSize(new Dimension( (isDirectMode() ? 80 : 50), Dialog.TEXTFIELD_HEIGHT));
      mainPanel.add(obmannLabel,    new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0
              ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      mainPanel.add(obmann,   new GridBagConstraints(10, 2, 2, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, (isDirectMode() ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE ), new Insets(0, 0, 0, 0), 0, 0));
      obmann.addItem( (isDirectMode() ? International.getString("keine Angabe") : "--") );
      obmann.addItem( (isDirectMode() ? 
          International.getString("Steuermann") :
          International.getString("St")) );
      for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) obmann.addItem( (isDirectMode() ? International.getString("Nummer")+" " : "") + (i+1));

      obmann.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          obmann_itemStateChanged(e);
        }
      });
      obmann.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(FocusEvent e) {
          colorize(e);
          showHint("obmann");
        }
        public void focusLost(FocusEvent e) {
          decolorize(e);
        }
      });
    }

    jMenuKonfiguration.add(jMenuFahrtenbuch);
    jMenuKonfiguration.add(jMenu1);
    jMenuFahrtenbuch.add(jMenuItem4);
    jMenuFahrtenbuch.addSeparator();
    jMenuFahrtenbuch.add(jMenuItem5);
    jMenuFahrtenbuch.add(jMenuItem6);
    jMenuFahrtenbuch.add(jMenuItem7);
    jMenuFahrtenbuch.add(jMenuItem1);
    jMenuKonfiguration.add(jMenuItem12);
    jMenuKonfiguration.add(jMenuItem14);
    jMenuKonfiguration.add(jMenuItem13);
    jMenuKonfiguration.add(jMenuItem2);
    jMenuKonfiguration.add(jMenuItem11);
    jMenuKonfiguration.add(jMenuItem3);
    jMenuStatistik.add(jMenuItemKilometerliste);
    jMenu1.add(jMenuItem8);
    jMenu1.add(jMenuItem9);
    jMenu1.add(jMenuItem10);

    focusManager = new EfaFrameFocusManager(this,FocusManager.getCurrentManager());
    FocusManager.setCurrentManager(focusManager);
//focusManager = new EfaFrameFocusManager(this,FocusManager.getCurrentKeyboardFocusManager());
//FocusManager.setCurrentKeyboardFocusManager(focusManager);

    if (!isDirectMode()) appIni();
    appCommonIni();
  }


  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    // folgendes wird bereits in this_windowClosing() erledigt
//    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
//      jMenuFileExit_actionPerformed(null);
//    }
  }



// ============================= Eingabe vervollständigen ======================

  public void acpwCallback(JTextField field) {
    try {
      eintragGeaendert(null);
      vervollstaendigeForField(field);
      if (field == ziel) setZielKm();
    } catch(Exception e) {
    }
  }

  public void vervollstaendigeForField(JTextField field) {
    if (field == null || Daten.fahrtenbuch == null) return;
    if (field == boot) { vervollstaendige(boot,bootButton,Daten.fahrtenbuch.getDaten().boote,null,this,false); return; }
    if (field == stm ) { vervollstaendige(stm , stmButton,Daten.fahrtenbuch.getDaten().mitglieder,null,this,false); return; }
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      if (field == mannsch[i]) { vervollstaendige(mannsch[i],mannschButton[i],Daten.fahrtenbuch.getDaten().mitglieder,null,this,false); return; }
    }
    if (field == ziel) { vervollstaendige(ziel,zielButton,Daten.fahrtenbuch.getDaten().ziele,null,this,false); return; }
  }

  private static void setButtonColor(JButton button, Color color) {
    button.setContentAreaFilled(true);
    button.setBackground(color);
  }

  // prüft, ob der Name ges bereits in einem anderem Steuermanns- oder Mannschaftsfeld eingetragen ist
  private static boolean eingetragenInAnderemFeld(String ges, javax.swing.text.JTextComponent feld, EfaFrame efaFrame) {
    if (ges.equals(efaFrame.stm.getText().trim()) && feld != efaFrame.stm) return true;
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++)
      if (ges.equals(efaFrame.mannsch[i].getText().trim()) && feld != efaFrame.mannsch[i]) return true;
    return false;
  }

  public static void vervollstaendige(javax.swing.JTextField feld, JButton button, DatenListe liste, KeyEvent e, EfaFrame efaFrame, boolean withPopup) {
    if (liste == null) {
      if (button != null) setButtonColor(button,Color.red);
    }

    if (e != null && e.getKeyCode() == -23) return; // dieses Key-Event wurde von AutoCompletePopupWindow generiert

    boolean isMitgliederliste = false;
    if (liste != null && Mitglieder.class.isInstance(liste)) isMitgliederliste = true;

    if (feld.getText().trim().equals("")) {
      if (button != null) setButtonColor(button,Color.lightGray);
    }

    if (liste == null) {
      return;
    }

    String anf,ges;
    DatenFelder d;

    liste.ignoreCase(true); // Groß- und Kleinschreibung ignorieren

    // Auswählen, welche Variante zum Vervollständigen benutzt werden soll
    int variante = 0;
    if (e == null || (EfaUtil.isRealChar(e) && e.getKeyCode() != KeyEvent.VK_ENTER) || e.getKeyCode() == KeyEvent.VK_DOWN) variante = 1;
    else if (e.getKeyCode() == KeyEvent.VK_UP) variante = 2;
    else if (e.getKeyCode() == KeyEvent.VK_DELETE) variante = 3;
    else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) variante = 3;
    else if (e.getKeyCode() == KeyEvent.VK_ENTER) variante = 4;
    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) variante = 5;



    if (variante == 1) {

      if (feld.getSelectedText() != null)
        anf = feld.getText().toLowerCase().substring(0,feld.getSelectionStart());
      else anf = feld.getText().toLowerCase();

      if (e != null && e.getKeyCode() == KeyEvent.VK_DOWN) {
        if (withPopup && Daten.efaConfig != null && Daten.efaConfig.popupComplete && AutoCompletePopupWindow.isShowingAt(feld)) {
          ges = liste.getNext();
        } else {
          ges = liste.getNext(anf);
        }
        if (ges == null) ges = liste.getFirst(anf);
      } else {

        if (e != null) ges = liste.getFirst(anf); // Taste gedrückt --> OK, Wortanfang genügt
        else ges = liste.getExact(feld.getText().toLowerCase()); // keine Taste gedrückt --> nur richtig, wenn gesamtes Feld exakt vorhanden!

        // prüfen (falls Mitglieder), ob Anfangsstück ein Alias ist
        if (isMitgliederliste && ((Mitglieder)liste).aliases != null) {
          String s;
          if ((s = (String)((Mitglieder)liste).aliases.get(anf.toLowerCase())) != null) ges = s;
        }

        // jetzt prüfen, ob Person (falls liste == mitglieder) schon in einem anderen Feld eingetragen
        // wurde; wenn ja, dann nächsten passenden Eintrag nehmen, falls vorhanden
        if (efaFrame != null && isMitgliederliste && ges != null && button != null) {
          String tmp = "";
          while (eingetragenInAnderemFeld(ges,feld,efaFrame)  && tmp != null ) {
            tmp = liste.getNext(anf);
            if (tmp != null) ges = tmp;
          }
        }
      }
     if (e == null && ges != null) ges = liste.getExact(ges);
      if (ges != null) {
        if (e != null) { // nur bei wirklichen Eingaben
          feld.setText(ges);
          feld.select(anf.length(),ges.length());
        }
        if (button != null) setButtonColor(button,Color.green);
        d = (DatenFelder)liste.getComplete();
      } else {
        if (button != null) setButtonColor(button,Color.red);
      }
      if (withPopup && Daten.efaConfig != null && Daten.efaConfig.popupComplete && e != null) AutoCompletePopupWindow.showAndSelect((JTextField)feld,liste,(ges != null ? ges : ""),efaFrame);
    }

    if (variante == 2) {
      if (feld.getSelectedText() != null)
        anf = feld.getText().toLowerCase().substring(0,feld.getSelectionStart());
      else anf = feld.getText().toLowerCase();
      if (withPopup && Daten.efaConfig != null && Daten.efaConfig.popupComplete && AutoCompletePopupWindow.isShowingAt(feld)) {
        ges = liste.getPrev();
      } else {
        ges = liste.getPrev(anf);
      }

      if (ges == null) ges = liste.getLast(anf); // liste.getFirst(anf);
      if (ges != null) {
        feld.setText(ges);
        feld.select(anf.length(),ges.length());
        if (button != null) setButtonColor(button,Color.green);
        d = (DatenFelder)liste.getComplete();
      } else {
        if (button != null) setButtonColor(button,Color.red);
      }
      if (withPopup && Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.showAndSelect((JTextField)feld,liste,(ges != null ? ges : ""),efaFrame);
    }
    if (variante == 3) {
      if ( (ges = liste.getFirst(feld.getText().toLowerCase().trim())) == null ||
          !(ges.equals(feld.getText()))) {
        if (button != null) setButtonColor(button,Color.red);
      } else if (button != null) setButtonColor(button,Color.green);
    }
    if (variante == 4) {
      feld.select(-1,-1);
      if (withPopup && Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
    }
    if (variante == 5) {
      if (withPopup && Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
    }
    if (feld.getText().equals("") && button != null) setButtonColor(button,Color.lightGray);

    liste.ignoreCase(false);  // Groß- und Kleinschreibung anschließend wieder beachten

  }


  void boot_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch != null)
      vervollstaendige(boot,bootButton,Daten.fahrtenbuch.getDaten().boote,e,this,true);
  }

  void stm_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch != null)
      vervollstaendige(stm,stmButton,Daten.fahrtenbuch.getDaten().mitglieder,e,this,true);
  }
  void mannsch_keyReleased(KeyEvent e) {
    if (e == null) return;
    int i;
    for (i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if (e.getSource() == mannsch[i]) break;
    if (Daten.fahrtenbuch != null)
      vervollstaendige(mannsch[i],mannschButton[i%8],Daten.fahrtenbuch.getDaten().mitglieder,e,this,true);
  }

  void ziel_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch != null) {
      vervollstaendige(ziel,zielButton,Daten.fahrtenbuch.getDaten().ziele,e,this,true);
      setZielKm(); // neu in v0.85
    }
  }

  // Wert aus Feld des zuletzt angezeigten Datensatzes eintragen
  void insetLastValue(KeyEvent e, int field, JButton button, DatenListe list) {
    if (e == null) return;
    if (isDirectMode()) return;
    if (refDatensatz == null) return;
    if ( (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) || // Ctrl-F
         (e.getKeyCode() == KeyEvent.VK_F5)) {                     // F5
       ((JTextField)e.getSource()).replaceSelection(refDatensatz.get(field));

       if (button != null && list != null)
         vervollstaendige((JTextField)e.getSource(),button,list,null,this,false);
       datensatzGeaendert = true;
     }
  }
  void lfdnr_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.LFDNR,null,null);
  }
  void datum_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.DATUM,null,null);

    // Tag vor oder zurück?
    if (   e.getKeyCode() == KeyEvent.VK_MINUS
        || e.getKeyCode() == KeyEvent.VK_SUBTRACT
        || e.getKeyCode() == KeyEvent.VK_PLUS
        || e.getKeyCode() == KeyEvent.VK_ADD
        || e.getKeyCode() == KeyEvent.VK_UP
        || e.getKeyCode() == KeyEvent.VK_KP_UP
        || e.getKeyCode() == KeyEvent.VK_DOWN
        || e.getKeyCode() == KeyEvent.VK_KP_DOWN ) {
        Calendar cal = GregorianCalendar.getInstance();
        TMJ ref = EfaUtil.correctDate(refDate,
                cal.get(GregorianCalendar.DAY_OF_MONTH),
                cal.get(GregorianCalendar.MONTH)+1-cal.
                    getMinimum(GregorianCalendar.MONTH),
                cal.get(GregorianCalendar.YEAR));
        cal = new GregorianCalendar(ref.jahr,ref.monat-1,ref.tag);
        int dateModifier = 0;
        switch(e.getKeyCode()) {
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_ADD:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                dateModifier = 1;
                break;
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                dateModifier = -1;
                break;
        }
        cal.add(GregorianCalendar.DATE, dateModifier );
        refDate = cal.get(GregorianCalendar.DAY_OF_MONTH)+"."+
                 (cal.get(GregorianCalendar.MONTH)+1)+"."+
                  cal.get(GregorianCalendar.YEAR);
        datumSetText(refDate);
        updateWoTag();
        datensatzGeaendert = true;
    }

  }
  void boot_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.BOOT, bootButton, (Daten.fahrtenbuch == null ? null : Daten.fahrtenbuch.getDaten().boote) );
  }
  void stm_keyPressed(KeyEvent e) {
    if (e != null && e.getKeyCode()==e.VK_O && e.isControlDown()) obmannSelected(0);
    insetLastValue(e,Fahrtenbuch.STM, stmButton, (Daten.fahrtenbuch == null ? null : Daten.fahrtenbuch.getDaten().mitglieder) );
  }
  void mannsch_keyPressed(KeyEvent e) {
    if (e == null) return;
    int i=0;
    for (i = 0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if (e.getSource() == mannsch[i]) break;
    if (e != null && e.getKeyCode()==e.VK_O && e.isControlDown()) obmannSelected(i+1);
    insetLastValue(e,Fahrtenbuch.MANNSCH1+i, mannschButton[i%8], (Daten.fahrtenbuch == null ? null : Daten.fahrtenbuch.getDaten().mitglieder) );
  }
  void abfahrt_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.ABFAHRT,null,null);
  }
  void ankunft_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.ANKUNFT,null,null);
  }
  void ziel_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.ZIEL, zielButton, (Daten.fahrtenbuch == null ? null : Daten.fahrtenbuch.getDaten().ziele) );
  }
  void bootskm_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.BOOTSKM,null,null);
  }
  void mannschkm_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.MANNSCHKM,null,null);
  }
  void bemerk_keyPressed(KeyEvent e) {
    insetLastValue(e,Fahrtenbuch.BEMERK,null,null);

    if (e != null && this.getFocusOwner() == this.bemerk && Daten.efaConfig != null && Daten.efaConfig.keys != null) {
      Object[] k = Daten.efaConfig.keys.keySet().toArray();
      if (k != null && k.length>0) {
        for (int i=0; i<k.length; i++) {
          if (((String)k[i]).equals("F6")  && e.getKeyCode() == KeyEvent.VK_F6  && Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
          if (((String)k[i]).equals("F7")  && e.getKeyCode() == KeyEvent.VK_F7  && Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
          if (((String)k[i]).equals("F8")  && e.getKeyCode() == KeyEvent.VK_F8  && Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
          if (((String)k[i]).equals("F9")  && e.getKeyCode() == KeyEvent.VK_F9  && Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
          if (((String)k[i]).equals("F10") && e.getKeyCode() == KeyEvent.VK_F10 && Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
          if (((String)k[i]).equals("F11") && (e.getKeyCode() == KeyEvent.VK_F11 || e.getKeyCode() == KeyEvent.VK_STOP)&& Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
          if (((String)k[i]).equals("F12") && (e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_AGAIN) && Daten.efaConfig.keys.get(k[i]) != null) { bemerk.setText(bemerk.getText()+Daten.efaConfig.keys.get(k[i])); datensatzGeaendert = true; }
        }
      }
    }
  }

  void stm_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
    if (Daten.efaConfig != null && Daten.efaConfig.autoObmann && neuerDatensatz &&
        stm.getText().trim().length()>0 && getObmann()==-1) setObmann(0,true);
  }
  void mannsch1_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.autoObmann && neuerDatensatz && e != null && getObmann()==-1) {
      if (Daten.efaConfig.defaultObmann == EfaConfig.OBMANN_NR1 && e.getSource() == mannsch[0] && mannsch[0].getText().trim().length()>0) {
        setObmann(1,true);
      }
    }
  }
  void mannsch_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
  }
  void abfahrt_focusGained(FocusEvent e) {
    if (e != null) autoSetObmann();
  }

  void obmannSelected(int nr) {
    if (getObmann() == nr) setObmann(-1,true); else setObmann(nr,true);
  }

  void obmann_itemStateChanged(ItemEvent e) {
    if (_inObmannUpdate) return;
    if (obmann.getSelectedIndex()<0) return;
    setObmann(obmann.getSelectedIndex()-1,false);
  }

  JTextField getObmannTextField(int nr) {
    if (nr<0) return null;
    if (nr == 0) return stm;
    if (nr-1 < mannsch.length) return mannsch[nr-1];
    return null;
  }

  // set Obmann: -1 = kein Obmann; 0 = Stm; 1..24 = Mannschaft
  void setObmann(int nr, boolean updateObmannList) {
  if (nr == 0) {
   nr = nr + 1 - 1;
  }
    Font font = datum.getFont(); // Standard-Font ist der des Datum-Feldes (willkürlich gewählt)
    JTextField field = getObmannTextField(getObmann());
    if (field != null) field.setFont(font);
    field = getObmannTextField(nr);
    if (field != null) field.setFont(font.deriveFont(Font.BOLD));
    if (isDirectMode() || true) {
      _inObmannUpdate = true;
      if (updateObmannList && nr+1>=0 && nr+1<obmann.getItemCount()) obmann.setSelectedIndex(nr+1);
      _inObmannUpdate = false;
    }
    currentObmann = nr;
    datensatzGeaendert = true;
  }

  // akt. Obmann ermitteln: -1 = kein Obmann; 0 = Stm; 1..24 = Mannschaft
  int getObmann() {
    return currentObmann;
  }

  void autoSetObmann() {
    if (Daten.efaConfig != null && Daten.efaConfig.autoObmann && neuerDatensatz &&
        getObmann() == -1) {
      if (Daten.efaConfig.defaultObmann == EfaConfig.OBMANN_SCHLAG) {
        try {
          int anzRud = getAnzahlRuderer();
          if (anzRud > 0)
            setObmann(anzRud, true);
        }
        catch (Exception ee) {
          EfaUtil.foo();
        }
      }
    }

    // Wenn Angabe eines Obmanns Pflicht ist, soll auch im Einer immer der Obmann automatisch selektiert werden,
    // unabhängig davon, ob Daten.efaConfig.autoObmann aktiviert ist oder nicht
    if (Daten.efaConfig != null &&
        Daten.efaConfig.efaDirekt_eintragErzwingeObmann &&
        neuerDatensatz && getObmann() == -1 &&
        stm.getText().trim().length() == 0 &&
        getAnzahlRuderer() == 1) {
      try {
        setObmann(1, true);
      }
      catch (Exception ee) {
        EfaUtil.foo();
      }
    }
  }

// ==================== Menüs und Buttons ======================================

  // Menü Datei->Neu
  void jMenuNew_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (!sicherheitsabfrage()) return;

    FahrtenbuchNeuFortsetzenFrame dlg = new FahrtenbuchNeuFortsetzenFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // Menü Datei->Öffnen
  public void jMenuFileOpen_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (sicherheitsabfrage()) {
      String fb=null;
      if (Daten.fahrtenbuch != null && !Daten.fahrtenbuch.getFileName().equals("")) {
          fb = Dialog.dateiDialog(this,International.getString("Fahrtenbuch öffnen"),
                  International.getString("efa Fahrtenbuch")+" (*.efb)","efb",Daten.fahrtenbuch.getFileName(),false);
      } else {
          fb = Dialog.dateiDialog(this,International.getString("Fahrtenbuch öffnen"),
              International.getString("efa Fahrtenbuch")+" (*.efb)","efb",Daten.efaDataDirectory,false);
      }
      if (fb != null) fahrtenbuchOeffnen(fb);
    }
  }


  // Menü Datei->Fahrtenbuch speichern
  void jMenuFileSave_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (sicherheitsabfrageDatensatz()) speichereFahrtenbuch(false);
  }


  // Menü Datei->Fahrtenbuch speichern unter
  void jMenuFileSaveAs_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (sicherheitsabfrageDatensatz()) speichereFahrtenbuch(true);
  }


  // Menü Datei->Backup einspielen
  void jMenuBackup_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    RestoreBackupFrame dlg = new RestoreBackupFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Datei->Datensicherung
  void jMenuFileDatensicherung_actionPerformed(ActionEvent e) {
    DatensicherungFrame dlg;
    try {
      Vector directories = new Vector();
      Vector selected = new Vector();
      Vector inclSubdirs = new Vector();
      directories.add(Daten.efaDataDirectory); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(true));
      directories.add(Daten.efaCfgDirectory); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(true));
      directories.add(Daten.efaAusgabeDirectory+"layout"+Daten.fileSep); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(true));

      // jetzt noch alle Verzeichnisse zusammensuchen, in denen Fahrtenbücher gesichert sind
      int dirCountBefore = directories.size();
      Hashtable visitedFb = new Hashtable();
      Fahrtenbuch fb = Daten.fahrtenbuch;
      while (fb != null) {
        if (visitedFb.get(fb.getFileName()) != null) break;
        visitedFb.put(fb.getFileName(),"foo");
        datensicherungAddDirectories(directories,fb);
        String prev = fb.getPrevFb(true);
        if (prev != null && prev.length()>0 && EfaUtil.canOpenFile(prev)) {
          fb = new Fahrtenbuch(prev); fb.readFile();
        } else {
          fb = null;
        }
      }
      fb = Daten.fahrtenbuch;
      while (fb != null) {
        if (visitedFb.get(fb.getFileName()) != null) break;
        visitedFb.put(fb.getFileName(),"foo");
        datensicherungAddDirectories(directories,fb);
        String next = fb.getNextFb(true);
        if (next != null && next.length()>0 && EfaUtil.canOpenFile(next)) {
          fb = new Fahrtenbuch(next); fb.readFile();
        } else {
          fb = null;
        }
      }
      for (int i=dirCountBefore; i<directories.size(); i++) {
        selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(false));
      }


      dlg = new DatensicherungFrame(this,directories,inclSubdirs,selected);
    } catch(Exception ee) {
      return;
    }
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }
  void datensicherungAddDirectories(Vector dirs, Fahrtenbuch fb) {
    if (fb == null) return;
    if (fb.getFileName() != null && fb.getFileName().length()>0) datensicherungAddDirectory(dirs,fb.getFileName());
    if (fb.getDaten() == null) return;
    if (fb.getDaten().bootDatei != null && fb.getDaten().bootDatei.length()>0) datensicherungAddDirectory(dirs,EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fb.getFileName()),fb.getDaten().bootDatei));
    if (fb.getDaten().mitgliederDatei != null && fb.getDaten().mitgliederDatei.length()>0) datensicherungAddDirectory(dirs,EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fb.getFileName()),fb.getDaten().mitgliederDatei));
    if (fb.getDaten().zieleDatei != null && fb.getDaten().zieleDatei.length()>0) datensicherungAddDirectory(dirs,EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fb.getFileName()),fb.getDaten().zieleDatei));
    if (fb.getDaten().statistikDatei != null && fb.getDaten().statistikDatei.length()>0) datensicherungAddDirectory(dirs,EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fb.getFileName()),fb.getDaten().statistikDatei));
  }
  void datensicherungAddDirectory(Vector dirs, String filename) {
    if (filename == null || filename.indexOf(Daten.fileSep)<0) return;
    String dir = EfaUtil.getPathOfFile(filename);
    if (dir != null && dir.length()>0) {
      if (!dir.endsWith(Daten.fileSep)) dir += Daten.fileSep;
      if (!dirs.contains(dir)) dirs.add(dir);
    }
  }

  // Menü Datei->Fahrtenbuch importieren
  void jMenuImport_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null) return;
    if (!sicherheitsabfrageDatensatz()) return;
    ImportFrame dlg = new ImportFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Datei->efa Online Update
  void jMenuOnlineUpdate_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (this.mode != MODE_FULL) {
      Dialog.error(International.getString("Diese Funktion steht nur im normalen efa-Modus zur Verfügung.\n"+
                   "Bitte starte efa im normalen Modus (nicht in der Bootshausvariante), "+
                   "um ein Online-Update durchzuführen."));
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      return;
    }
    if (!sicherheitsabfrage()) return;
    OnlineUpdateFrame.runOnlineUpdate(this,Daten.ONLINEUPDATE_INFO);
  }


  // Menü Datei->Beenden
  void jMenuFileExit_actionPerformed(ActionEvent e) {
    cancel();
  }


  // Menü Konfiguration->Einstellungen zum Fahrtenbuch
  void jMenuItem4_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null) return;
    if (!sicherheitsabfrage()) return;
    NeuesFahrtenbuchFrame dlg = new NeuesFahrtenbuchFrame(this,false);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // AuswahlFrame (Mitglieder-/Boots-/Zielliste) öffnen
  void oeffneAuswahl(int art) {
    if (isDirectMode() || (mode == MODE_ADMIN_NUR_FAHRTEN && art != AuswahlFrame.MEHRTAGESFAHRTEN)) return;
    AuswahlFrame dlg = new AuswahlFrame(this,art);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // Menü Konfiguration->Mitgliederliste
  void jMenuItem5_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().mitglieder == null) return;
    oeffneAuswahl(AuswahlFrame.MITGLIEDER);
  }


  // Menü Konfiguration->Bootsliste
  void jMenuItem6_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().boote == null) return;
    oeffneAuswahl(AuswahlFrame.BOOTE);
  }


  // Menü Konfiguration->Zielliste
  void jMenuItem7_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().ziele == null) return;
    oeffneAuswahl(AuswahlFrame.ZIELE);
  }


  // Menü Konfiguration->Mehrtagesfahrten
  void jMenuItem1_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch == null) return;
    if (datensatzGeaendert && !sicherheitsabfrageDatensatz()) return;
    oeffneAuswahl(AuswahlFrame.MEHRTAGESFAHRTEN);
  }

  // Menü Konfiguration->Synonymlisten->Mitglieder
  void openSynFrame(Synonyme synonyme, DatenListe datenliste) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    SynonymFrame dlg = new SynonymFrame(this,synonyme,datenliste);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }
  void jMenuItem8_actionPerformed(ActionEvent e) {
    if (Daten.fahrtenbuch == null) return;
    openSynFrame(Daten.synMitglieder,Daten.fahrtenbuch.getDaten().mitglieder);
  }
  void jMenuItem9_actionPerformed(ActionEvent e) {
    if (Daten.fahrtenbuch == null) return;
    openSynFrame(Daten.synBoote,Daten.fahrtenbuch.getDaten().boote);
  }
  void jMenuItem10_actionPerformed(ActionEvent e) {
    if (Daten.fahrtenbuch == null) return;
    openSynFrame(Daten.synZiele,Daten.fahrtenbuch.getDaten().ziele);
  }

  // Menü Konfiguration->Standardmannschaften
  void jMenuItem12_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.mannschaften == null) return;
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.MANNSCHAFTEN);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    updateMannschaftenAndShowButton();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Konfiguration->Gruppen
  void jMenuItem14_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.gruppen == null) return;
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.GRUPPEN);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Konfiguration->DRV-Fahrtenabzeichen
  void jMenuItem13_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null) return;
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.FAHRTENABZEICHEN);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }



  // Menü Konfiguration->Elektronische Wettbewerbsteilnahme
  void jMenuItem2_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    VereinsConfigFrame dlg = new VereinsConfigFrame(this,Daten.vereinsConfig);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Konfiguration->Allgemeine Einstellungen
  void jMenuItem3_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if ((mode == MODE_ADMIN && (admin == null || !admin.allowedEfaConfig)) ||
        (mode == MODE_FULL && admin != null && !admin.allowedEfaConfig)) {
      Dialog.error(International.getString("Du hast nicht die Berechtigung, diese Funktion auszuführen!"));
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      return;
    }
    EfaConfigFrame dlg = new EfaConfigFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Konfiguration->Schreibschutz
  void jMenuItem11_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    WriteProtectFrame dlg;
    dlg = new WriteProtectFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Ausgabe->Statistik erstellen
  void jMenuItemKilometerliste_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null) jMenuFileOpen_actionPerformed(null);
    if (Daten.fahrtenbuch == null) return;
    if (!sicherheitsabfrageDatensatz()) return;
    StatistikFrame dlg = new StatistikFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // Menü Hilfe->Info
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    EfaFrame_AboutBox dlg = new EfaFrame_AboutBox(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Hilfe->Java Konsole
  void jMenuHilfeJavaKonsole_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    KonsoleFrame dlg = new KonsoleFrame(this,Daten.efaLogfile);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Menü Hilfe->Dokumentation
  void jMenuDokumentation_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (!EfaUtil.canOpenFile(Daten.efaDocDirectory+"index.html")) {
      Dialog.infoDialog(International.getString("Fehler"),
              LogString.logstring_fileNotFound(Daten.efaDocDirectory+"index.html", International.getString("Hilfedatei")));
      return;
    }
    Dialog.neuBrowserDlg(this,International.getString("Dokumentation"),"file:"+Daten.efaDocDirectory+"index.html");
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // efa-Tour starten
  void jMenu_startTour_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    Dialog.startTour(this);
  }

  // Menü Hilfe->efa-Homepage
  void jMenu_efaHomepage_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    Dialog.startBrowser(this,Daten.EFAURL);
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // Fenster schließen
  void this_windowClosing(WindowEvent e) {
    cancel();
  }

  void this_windowIconified(WindowEvent e) {
    if (isDirectMode()) {
      startBringToFront(true);
    }
  }

  // nächsten Datensatz auswählen
  void NextButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch != null) {
      if (!sicherheitsabfrageDatensatz()) return;
      DatenFelder d;
      if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null) SetFields(d);
      else Daten.fahrtenbuch.getCompleteLast(); // damit sich Button nicht "verhakt"
    }
  }


  // vorherigen Datensatz auswählen
  void PrevButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch != null) {
      if (!sicherheitsabfrageDatensatz()) return;
      DatenFelder d;
      if (neuerDatensatz) {
        if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteLast()) != null) SetFields(d);
        else Daten.fahrtenbuch.getCompleteFirst(); // damit sich Button nicht "verhakt"
      } else if ((d = (DatenFelder)Daten.fahrtenbuch.getCompletePrev()) != null) SetFields(d);
    }
  }


  // ersten Datensatz auswählen
  void FirstButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch != null) {
      if (!sicherheitsabfrageDatensatz()) return;
      DatenFelder d;
      if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst()) != null) SetFields(d);
    }
  }


  // letzten Datensatz auswählen
  void LastButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch != null) {
      if (!sicherheitsabfrageDatensatz()) return;
      DatenFelder d;
      if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteLast()) != null) SetFields(d);
    }
  }


  // neuen Datensatz beginnen
  void NewButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (aktDatensatz == null) return; // schon neuer Datensatz angezeigt
    refDatensatz = aktDatensatz;
    if (Daten.fahrtenbuch != null) {
      if (!sicherheitsabfrageDatensatz()) return;
      SetBlankFields();
      if (isAdminMode()) {
        Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYADDED,
                International.getString("Admin") + ": " +
                International.getMessage("Neuer Fahrtenbuch-Eintrag #{lfdnr} wurde erstellt.",lfdnr.getText().trim()));
      }
    }
  }


  // neuen Datensatz vor aktuellem einfügen
  void InsertButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch == null) return;
    if (aktDatensatz == null) { NewButton_actionPerformed(null); return; }
    String curlfd = aktDatensatz.get(Fahrtenbuch.LFDNR);
    if (curlfd != null) curlfd = curlfd.trim();
    if (curlfd == null || curlfd.equals("")) { NewButton_actionPerformed(null); return; }
    if (mode != MODE_FULL) {
      if (efaDirektFrame != null && efaDirektFrame.sindNochBooteUnterwegs()) {
        Dialog.error(International.getString("Es sind noch Boote unterwegs. "+
                     "Das Einfügen von Einträgen ist nur möglich, wenn alle laufenden Fahrten beendet sind."));
        startBringToFront(false);
        return;
      }
    }
    int ret = Dialog.yesNoDialog(International.getString("Eintrag einfügen"),
            International.getMessage("Soll vor dem aktuellen Eintrag (Lfd. Nr. {lfdnr}) wirklich ein neuer Eintrag eingefügt werden?\n"+
        "Alle nachfolgenden laufenden Nummern werden dann um eins erhöht!", curlfd));
    startBringToFront(false); // efaDirekt im BRC -- Workaround
    if (ret != Dialog.YES) return;
    DatenFelder d = null;
    String lfd = null;
    do {
      if (d == null) d = (DatenFelder)Daten.fahrtenbuch.getCompleteLast();
      else d = (DatenFelder)Daten.fahrtenbuch.getCompletePrev();

      // neue Lfd Nr berechnen
      lfd = d.get(Fahrtenbuch.LFDNR).trim();
      int lfd_i = EfaUtil.string2date(lfd,0,0,0).tag;
      String lfd_ch = lfd.substring(Integer.toString(lfd_i).length());
      d.set(Fahrtenbuch.LFDNR,Integer.toString(++lfd_i)+lfd_ch);

      // Eintrag ändern
      Daten.fahrtenbuch.delete(lfd);
      Daten.fahrtenbuch.add(d);
    } while (d != null && !lfd.equals(curlfd));
    NewButton_actionPerformed(null);
    neuerDatensatz_einf = true;
    lfdnr.setText(Integer.toString(EfaUtil.string2date(curlfd,0,0,0).tag));
  }

  // Datensatz Löschen
  void ButtonDelete_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch == null || aktDatensatz == null) return;
    if (aktDatensatz.get(Fahrtenbuch.LFDNR).trim().equals("")) return;
    if (Dialog.yesNoDialog(International.getString("Wirklich löschen?"),
            International.getString("Möchtest Du den aktuellen Eintrag wirklich löschen?")) == Dialog.YES) {
      Daten.fahrtenbuch.delete(aktDatensatz.get(Fahrtenbuch.LFDNR));
      if (isAdminMode()) {
        Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYDELETED,
                International.getString("Admin")+": "+
                International.getString("Fahrtenbuch-Eintrag")+" #"+(aktDatensatz != null ? aktDatensatz.get(Fahrtenbuch.LFDNR) : "$$")+" wurde gelöscht.");
      }
      DatenFelder d;
      if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null) SetFields(d);
      else if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteLast()) != null) SetFields(d);
      else SetBlankFields();
    }
   startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // Datensatz suchen
  void SuchButton_actionPerformed(ActionEvent e) {
    if (isDirectMode()) return;
    if (Daten.fahrtenbuch == null) return;
    if (!sicherheitsabfrageDatensatz()) return;
    if (Daten.fahrtenbuch.countElements() == 0) return;
    SuchFrame dlg = new SuchFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // MAnnschaftsfelder 5-8 anzeigen
  void setActiveMannsch(int nr) {
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) mannsch[i].setVisible(i / 8 == nr);
    mannsch1_label.setText(International.getString("Mannschaft")+" "+(1+(nr*8))+": ");
    mannsch2_label.setText(" "+(2+(nr*8))+": ");
    mannsch3_label.setText(" "+(3+(nr*8))+": ");
    mannsch4_label.setText(" "+(4+(nr*8))+": ");
    mannsch5_label.setText("  "+(5+(nr*8))+": ");
    mannsch6_label.setText("  "+(6+(nr*8))+": ");
    mannsch7_label.setText("  "+(7+(nr*8))+": ");
    mannsch8_label.setText("  "+(8+(nr*8))+": ");
    mannschAuswahl = nr;
    packFrame("setActiveMannsch(int)");
  }

  // zwischen Mannschaftsmitgliedern 5-8 und 9-12 umschalten
  void weitereMannschButton_actionPerformed(ActionEvent e) {
    if (Daten.fahrtenbuch == null) return;
    mannschAuswahl++;
    if (mannschAuswahl == 3) mannschAuswahl = 0;
    setActiveMannsch(mannschAuswahl);
    setColoredMannschButton();
    erkenneFelder1bis16();
  }

  void mannschaftSelectButton_actionPerformed(ActionEvent e) {
    if (Daten.fahrtenbuch == null || mannschaften == null || mannschaften.isEmpty()) return;
    MannschaftAuswahlFrame dlg = new MannschaftAuswahlFrame(this,mannschaften);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  void setColoredMannschButton() {
    String m = "";
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if (i / 8 != mannschAuswahl) m += mannsch[i].getText().trim();
    if (m.length()>0) setButtonColor(weitereMannschButton,Color.orange);
    else setButtonColor(weitereMannschButton,Color.lightGray);
  }

  // Check auf Tippfehler bei Booten, Mitgliedern und Zielen
  // basierend auf Code von Thilo Coblenzer
  // The parameters art and liste are German strings; they are passed through International.getString() by this function
  void checkNeighbours(JTextField field, JButton button, DatenListe dl, String art, String liste, boolean mitglied) {
    Vector neighbours = null;
    String name = field.getText().trim();
    if (name.length() == 0) return;

    if (dl.getExact(name) == null) {
      neighbours = dl.getNeighbours(name,3);
      if (neighbours == null && mitglied && Daten.fahrtenbuch != null) {
        String nameUmgedreht = null;
        if (Daten.fahrtenbuch.getDaten().erstVorname && name.indexOf(",")>0) {
          String[] sn = EfaUtil.zerlegeNamen(name,false);
          nameUmgedreht = EfaUtil.getFullName(sn[0],sn[1],sn[2],true);
        }
        if (!Daten.fahrtenbuch.getDaten().erstVorname && name.indexOf(",")<0) {
          String[] sn = EfaUtil.zerlegeNamen(name,true);
          nameUmgedreht = EfaUtil.getFullName(sn[0],sn[1],sn[2],false);
        }
        if (nameUmgedreht != null) neighbours = dl.getNeighbours(nameUmgedreht,3);
      }
    }
    if (neighbours != null) {
      String suggestedName;
      for (Iterator j = neighbours.iterator(); j.hasNext();) {
        DatenFelder d = (DatenFelder)j.next();
        suggestedName = dl.constructKey(d);
        if (Dialog.yesNoDialog(International.getMessage("{art} unbekannt (Tippfehler?)",
                               International.getString(art)),
                               International.getMessage("Der Name '{name}' "+
                               "konnte in der {liste} nicht gefunden werden.",
                               name, International.getString(liste)) + "\n" +
                               International.getMessage("Meintest Du '{suggestedName}'?", suggestedName)) == Dialog.YES) {
          field.setText(suggestedName);
          vervollstaendige(field,button,dl,null,this,false);
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          break;
        }
      }
    }
  }

  Vector getBootsbesatzung(DatenFelder d) {
    if (d == null) return null;
    Vector v = new Vector();
    if (d.get(Fahrtenbuch.STM).length()>0) v.add(d.get(Fahrtenbuch.STM));
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      if (d.get(Fahrtenbuch.MANNSCH1+i).length()>0) v.add(d.get(Fahrtenbuch.MANNSCH1+i));
    }
    return v;
  }

  DatenFelder findDoppeleintrag(DatenFelder d, DatenFelder fb, int range) {
    if (d == null) return null;
    if (fb == null) fb = Daten.fahrtenbuch.getCompleteFirst();

    Vector aktMannsch = getBootsbesatzung(d);
    for (int c=0; c<Math.abs(range); c++) {
      if (fb == null) break;

      if (!d.get(Fahrtenbuch.LFDNR).equals(fb.get(Fahrtenbuch.LFDNR)) &&
          d.get(Fahrtenbuch.BOOT).equals(fb.get(Fahrtenbuch.BOOT)) &&
          d.get(Fahrtenbuch.DATUM).equals(fb.get(Fahrtenbuch.DATUM))) {
        Vector mannsch = getBootsbesatzung(fb);
        int matches = 0;
        for (int i=0; i<aktMannsch.size(); i++) {
          if (mannsch.contains(aktMannsch.get(i))) matches++;
        }
        // Doppeleintrag, wenn
        // - verschiedene LfdNr
        // - gleiches Boot
        // - gleiches Datum
        // - mindestens 1 Mannschaftsmitglied gleich, maximal 2 Mannschaftsmitglieder verschieden
        if (matches > 0 && aktMannsch.size() - matches <=2) return fb;
      }

      if (range < 0) fb = Daten.fahrtenbuch.getCompletePrev();
      else fb = Daten.fahrtenbuch.getCompleteNext();
    }
    return null;
  }


  // aktuellen Datensatz hinzufügen (speichern)
  void addButton_actionPerformed(ActionEvent e) {
    if (Daten.fahrtenbuch == null) return;

    // Da das Hinzufügen eines Eintrags in der Bootshausversion wegen des damit verbundenen
    // Speicherns lange dauern kann, könnte ein ungeduldiger Nutzer mehrfach auf den "Hinzufügen"-
    // Button klicken. "synchronized" hilft hier nicht, da sowieso erst nach Ausführung des
    // Threads der Klick ein zweites Mal registriert wird. Da aber nach Abarbeitung dieser
    // Methode der Frame "EfaFrame" vom Stack genommen wurde und bei der zweiten Methode damit
    // schon nicht mehr auf dem Stack ist, kann eine Überprüfung, ob der aktuelle Frame
    // "EfaFrame" ist, benutzt werden, um eine doppelte Ausführung dieser Methode zu verhindern.
    if (Dialog.frameCurrent() != this) return;

    // Check auf Tippfehler (Code-Spende von Thilo Coblenzer)
    if (Daten.efaConfig != null) {
      if (mode != MODE_ENDE) {
        if (Daten.efaConfig.correctMisspelledBoote) {
            checkNeighbours(boot,bootButton,Daten.fahrtenbuch.getDaten().boote,
                International.getString("Boot"),
                International.getString("Bootsliste"),false); // the explicit string parameters are passed through Int'l.getString by checkNeighbours()
        }
      }
      if (Daten.efaConfig.correctMisspelledMitglieder) {
          checkNeighbours(stm,stmButton,Daten.fahrtenbuch.getDaten().mitglieder,
                  International.getString("Mitglied"),
                  International.getString("Mitgliederliste"),true);
      } // the explicit string parameters are passed through International.getString by checkNeighbours()
      if (Daten.efaConfig.correctMisspelledMitglieder) {
          for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
              checkNeighbours(mannsch[i],mannschButton[i % 8],Daten.fahrtenbuch.getDaten().mitglieder,
                      International.getString("Mitglied"),
                      International.getString("Mitgliederliste"),true);
          }
      }
      if (Daten.efaConfig.correctMisspelledZiele) {
        checkNeighbours(ziel, zielButton, Daten.fahrtenbuch.getDaten().ziele,
                        International.getString("Ziel"),
                        International.getString("Zielliste"), false);
        if (isDirectMode() && Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen &&
            ziel.getText().trim().length() > 0) {
          DatenFelder d = Daten.fahrtenbuch.getDaten().ziele.getExactComplete(ziel.getText().trim());
          if (d != null && !bootskm.getText().trim().equals(d.get(Ziele.KM))) {
            bootskm.setText(d.get(Ziele.KM));
          }
        }
      }
    }

    // Ruderer auf doppelte prüfen
    Hashtable h = new Hashtable();
    String s;
    String doppelt = null; // Ergebnis doppelt==null heißt ok, doppelt!=null heißt Fehler! ;-)
    while (true) { // Unsauber; aber die Alternative wäre ein goto; dies ist keine Schleife!!
      if (! (s = stm.getText().trim()).equals("") ) h.put(s,"");
      for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
        if (! (s = mannsch[i].getText().trim()).equals("") ) {
          if (h.get(s) == null) {
            h.put(s,"");
          } else {
            doppelt = s;
            break;
          }
        }
      }
      break; // alles ok, keine doppelten --> Pseudoschleife abbrechen
    }
    if (doppelt != null) {
      Dialog.error(International.getMessage("Die Person '{name}' wurde mehrfach eingegeben!",doppelt));
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      return;
    }

    // bei steuermannslosen Booten keinen Steuermann eingeben
    if (stm.getText().trim().length()>0 && Daten.fahrtenbuch.getDaten().boote != null &&
        Daten.efaTypes != null) {
      DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot.getText().trim());
      if (b != null && b.get(Boote.STM).equals(EfaTypes.TYPE_COXING_COXLESS)) {
        int ret = Dialog.yesNoDialog(International.getString("Steuermann"),
                               International.getString("Du hast für ein steuermannsloses Boot einen Steuermann eingetragen. "+
                               "Möchtest Du diesen Eintrag dennoch speichern?"));
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        if (ret != Dialog.YES) return;
      }
    }

    // Mannsch-Km ausfüllen, falls übersprungen
    mannschkm_focusGained(null);

    // Prüfen, ob ein Doppeleintrag vorliegt
    if (isDirectMode()) {
      DatenFelder dop = findDoppeleintrag(getFields(null,null,null), Daten.fahrtenbuch.getCompleteLast(), -25); // letzte 25 Einträge nach Doppeleinträgen durchsuchen
      if (dop != null) {
        Vector v = getBootsbesatzung(dop);
        String m = "";
        for (int i=0; i<v.size(); i++) {
          m += (m.length()>0 ? "; " : "") + v.get(i);
        }
        switch(Dialog.auswahlDialog(International.getString("Doppeleintrag?"),
                                    International.getString("Es gibt bereits einen ähnlichen Eintrag im Fahrtenbuch") + ":\n\n"+
                                    International.getMessage("#{entry} vom {date} mit {boat}",
                                    dop.get(Fahrtenbuch.LFDNR), dop.get(Fahrtenbuch.DATUM), dop.get(Fahrtenbuch.BOOT)) + ":\n"+
                                    International.getString("Mannschaft") + ": " + m + "\n"+
                                    International.getString("Abfahrt") + ": " + dop.get(Fahrtenbuch.ABFAHRT) + "; " +
                                    International.getString("Ankunft") + ": " + dop.get(Fahrtenbuch.ANKUNFT) + "; " +
                                    International.getString("Ziel") + ": " + dop.get(Fahrtenbuch.ZIEL) + "\n\n" +
                                    International.getMessage("Möglicherweise handelt es sich bei dem aktuellen Eintrag #{neue_lfdnr} um einen Doppeleintrag.",
                                    lfdnr.getText()) + "\n" +
                                    International.getString("Was möchtest Du tun?"),
                                    International.getString("Eintrag hinzufügen") +
                                    " (" + International.getString("kein Doppeleintrag") + ")",
                                    International.getString("Abbrechen"),false)) {
               case 0: break;
               case 1: return;
        }
      }
    }


    if (isDirectMode()) {
      if (mode == MODE_START || mode == MODE_START_KORREKTUR) {
        // checkFahrtbeginnFuerBoot nur bei direkt_boot==null machen, da ansonsten der Check schon in EfaDirektFrame gemacht wurde
        if (direkt_boot == null && !efaDirektFrame.checkFahrtbeginnFuerBoot(boot.getText().trim(), 2)) {
          return;
        }
      }
      direktSpeichereDatensatz();
      return;
    }

    // Prüfen, ob Eintrag einer Mehrtagesfahrt vorliegt und das Datum in den Zeitraum der Mehrtagesfahrt fällt
    Mehrtagesfahrt mtour = null;
    if (Daten.efaTypes != null &&
        (Daten.efaTypes.getType(EfaTypes.CATEGORY_TRIP, fahrtDauer.getSelectedIndex()) == null ||
         Daten.efaTypes.getType(EfaTypes.CATEGORY_TRIP, fahrtDauer.getSelectedIndex()).equals(EfaTypes.TYPE_TRIP_MULTIDAY))) {
        mtour = Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtDauer.getSelectedItem());
    }
    if (mtour != null) {
      if (EfaUtil.secondDateIsAfterFirst(datum.getText(),mtour.start) ||
          EfaUtil.secondDateIsAfterFirst(mtour.ende,datum.getText())) {
        Dialog.error(International.getMessage("Das Datum des Fahrtenbucheintrags ({entry}) liegt außerhalb des Zeitraums " +
                " ({date_from} - {date_to}), der für die ausgewählte Mehrtagesfahrt '{name}' angegeben wurde.",
                "#"+lfdnr.getText().trim()+" "+datum.getText().trim(), mtour.start, mtour.ende, mtour.name) +
                "\n\n" +
                International.getString("Falls in diesem Jahr mehrere Mehrtagesfahrten mit derselben Strecke durchgeführt wurden, " +
                "so erstelle bitte für jede einzelne Mehrtagesfahrt einen separaten Eintrag in efa. Ansonsten wähle bitte entweder " +
                "eine Mehrtagesfahrt mit passendem Datum aus oder korrigiere das Datum dieses Eintrages oder der Mehrtagesfahrt."));
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return;
      }
    }

    // Prüfen, ob neues Jahr vorliegt
    Calendar cal = GregorianCalendar.getInstance();
    TMJ tmj = EfaUtil.string2date(datum.getText().trim(),0,0,0);
    TMJ ref = EfaUtil.correctDate(refDate,cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH),cal.get(GregorianCalendar.YEAR));
    if (tmj.jahr != ref.jahr && !Daten.fahrtenbuch.isEmpty()) {
      Dialog.infoDialog(International.getString("Warnung"),
		            International.getString("Ein Fahrtenbuch sollte immer nur die Fahrten EINES Jahres enthalten! "+
                            "Bitte lösche die eben hinzugefügte Fahrt und beginne für die Fahrten "+
                            "des neuen Jahren ein neues Fahrtenbuch!\n"+
                            "Um ein neues Fahrtenbuch zu beginnen, wähle aus dem Menü 'Datei' den Punkt "+
                            "'Neues Fahrtenbuch erstellen' und dort die Option 'Fahrtenbuch fortsetzen'."));
      startBringToFront(false); // efaDirekt im BRC -- Workaround
    }

    if (speichereDatensatz() && neuerDatensatz) {
      refDatensatz = aktDatensatz;
      SetBlankFields();
    }
  }


  // Boot zur Datenbank hinzufügen
  void bootButton_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null ||
        (bootButton.getBackground() != Color.red && bootButton.getBackground() != Color.green) ||
        Daten.fahrtenbuch.getDaten().boote == null) return;
    NeuesBootFrame dlg = new NeuesBootFrame(this,boot.getText().trim(),bootButton.getBackground() == Color.red);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // Person zur Datenbank hinzufügen
  void neuesMitglied(JTextField feld, JButton button) {
    boolean directMode = false;
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) {
      if (Daten.efaConfig == null || !Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen) return;
      if (button.getBackground() != Color.red) return; // nur neue Mitglieder hinzufügen, keine Mitglieder bearbeiten!
      directMode = true;
    }
    if (Daten.fahrtenbuch == null ||
        (button.getBackground() != Color.red && button.getBackground() != Color.green) ||
        Daten.fahrtenbuch.getDaten().mitglieder == null) return;
    NeuesMitgliedFrame dlg = new NeuesMitgliedFrame(this,feld.getText().trim(),feld,button,button.getBackground() == Color.red,directMode);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // einzelne Mitgliederfelder hinzufügen
  void stmButton_actionPerformed(ActionEvent e) {
    neuesMitglied(stm,stmButton);
  }
  void mannschButton_actionPerformed(ActionEvent e) {
    if (e == null) return;
    int i=0;
    for (i=0; i<8; i++) if (e.getSource() == mannschButton[i]) break;
    neuesMitglied(mannsch[i + mannschAuswahl*8],mannschButton[i]);
  }


  // Ziel zur Datenbank hinzufügen
  void zielButton_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null ||
        (zielButton.getBackground() != Color.red && zielButton.getBackground() != Color.green)||
        Daten.fahrtenbuch.getDaten().ziele == null) return;
    NeuesZielFrame dlg = new NeuesZielFrame(this,ziel.getText().trim(),zielButton.getBackground() == Color.red);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  void setDateFromRefDate() {
    for(int iii=0; iii<2; iii++) try {
      Calendar cal = GregorianCalendar.getInstance();
      TMJ ref = EfaUtil.correctDate(refDate,cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH),cal.get(GregorianCalendar.YEAR));

      // folgende Implementation ist veraltet
      // sie sollte dafür sorgen, daß *nach* der Eingabe die Zeichen +-*/ ausgewertet werden,
      // um Folgetage zu bestimmen. Mitlerweile wird diese Aufgabe in datum_keyPressed()
      // schon während der Eingabe erledigt. Somit sind +-*/ am Anfang des Strings nicht
      // mehr möglich und folgender Code wird daher nicht mehr ausgeführt, verbleibt aber
      // vorerst im Programm, falls er doch nochmal Verwendung finden sollte...
      String s = datum.getText().trim();
      if (s.startsWith("+") || s.startsWith("-") ||
          s.startsWith("*") || s.startsWith("/")) {
        int diff = 0;
        for (int i=0; i<s.length(); i++) {
          switch(s.charAt(i)) {
            case '+': diff++;  break; // Tag vor
            case '-': diff--;  break; // Tag zurück
            case '*': diff+=7; break; // Woche vor
            case '/': diff-=7; break; // Woche zurück
          }
        }
        ref = EfaUtil.incDate(ref,diff);
      } else if (s.indexOf(" ")<0 && (s.length()==4 || s.length()==6 || s.length()==8)) {
        // Datum vielleicht als "TTMM", "TTMMJJ" oder "TTMMJJJJ" eingegeben
        boolean nurZahlen = true;
        for (int i=0; i<s.length() && nurZahlen; i++) if (s.charAt(i)<'0' || s.charAt(i)>'9') nurZahlen = false;
        if (nurZahlen) { // Datum als "TTMM", "TTMMJJ" oder "TTMMJJJJ" eingegeben
          s = s.substring(0,2) + "." + s.substring(2,4) + (s.length()>4 ? "." + s.substring(4) : "");
        }
      }

      TMJ c = EfaUtil.correctDate(s,ref.tag,ref.monat,ref.jahr);
      datumSetText(c.tag+"."+c.monat+"."+c.jahr);
      updateWoTag();
      break;
    } catch(Exception e) {
    }
  }

  // Datum überprüfen
  void datum_focusLost(FocusEvent e) {
    setDateFromRefDate();
  }


  // Anzahl der Ruderer ermitteln
  int getAnzahlRuderer() {
    int c = 0;
    if (!stm.getText().trim().equals("")) c++;
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if (!mannsch[i].getText().trim().equals("")) c++;
    return c;
  }


  // Mannschaftskilometer berechnen
  void mannschkm_focusGained(FocusEvent e) {
    int i = getAnzahlRuderer();
    if (i == 0) return;
    try {
      mannschkm.setText(EfaUtil.zehntelInt2String(EfaUtil.zehntelString2Int(bootskm.getText().trim()) * i));
    } catch(Exception ee) {
    }
  }


  // Standard-Steuermann und Mannschaft für ein Boot setzen
  void setStandardMannschaft(DatenFelder d) {
    boolean _stm = true;
    int _anzRud = Fahrtenbuch.ANZ_MANNSCH;
    if (aktBoot != null && Daten.efaTypes != null) {
      _stm = aktBoot.get(Boote.STM).equals(EfaTypes.TYPE_COXING_COXED);
      _anzRud = EfaUtil.string2date(aktBoot.get(Boote.ANZAHL),Fahrtenbuch.ANZ_MANNSCH,0,0).tag;
    }

    if (_stm && d.get(Mannschaften.STM).length()>0) stm.setText(d.get(Mannschaften.STM));
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      if (i+1<=_anzRud && d.get(Mannschaften.MANNSCH1+i).length()>0) mannsch[i].setText(d.get(Mannschaften.MANNSCH1+i));
    }
    if (d.get(Mannschaften.ZIEL).length()>0) ziel.setText(d.get(Mannschaften.ZIEL));
    try {
        if (Daten.efaTypes != null && Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_TRIP, d.get(Mannschaften.FAHRTART))) {
            fahrtDauer.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, (String)d.get(Mannschaften.FAHRTART)));
        }
    } catch(Exception e) { }

    vervollstaendigeAlleFelder();
    setZielKm();

    // Obmann
    int obm = -1;
    try {
      if (!Mannschaften.NO_OBMANN.equals(d.get(Mannschaften.OBMANN))) {
        int nr = EfaUtil.string2date(d.get(Mannschaften.OBMANN),0,0,0).tag; // 0: Steuermann, >0: Mannschaft
        if (nr == 0 && stm.getText().trim().length()>0) obm = 0;
        if (nr > 0 && nr < getAnzahlRuderer() && mannsch[nr-1].getText().trim().length()>0) obm = nr;
        if (obm >= 0) setObmann(obm,true);
      }
    } catch(Exception e) { }

    if (obm == -1 && Daten.efaConfig != null && Daten.efaConfig.autoObmann) {
      if (stm.getText().trim().length()>0) setObmann(0,true);
      else if (Daten.efaConfig.defaultObmann == EfaConfig.OBMANN_NR1) setObmann(1,true);
           else if (Daten.efaConfig.defaultObmann == EfaConfig.OBMANN_SCHLAG) try {
             int anzRud = getAnzahlRuderer();
             if (anzRud > 0) setObmann(anzRud,true);
           } catch(Exception ee) { EfaUtil.foo(); }
    }

    stm.select(0,stm.getText().length());
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) mannsch[i].select(0,mannsch[i].getText().length());
    ziel.select(0,ziel.getText().length());
    datensatzGeaendert = true;
  }


  // wird von boot_focusLost aufgerufen, sowie vom FocusManager! (irgendwie unsauber, da bei <Tab> doppelt...
  void boot_focusLostGetBoot() {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
    aktBoot = null;
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().boote != null && bootButton.getBackground() == Color.green) {
      aktBoot = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot.getText().trim());

      if (isDirectMode()) {
        setFieldEnabledStmUndMannsch(aktBoot);
      }

    } else {
      if (isDirectMode()) {
        setFieldEnabledStmUndMannsch(null);
      }
    }
    if (aktDatensatz == null && Daten.mannschaften != null &&
        Daten.efaConfig != null && Daten.efaConfig.autoStandardmannsch &&
        boot.getText().trim().length()!=0 &&
        Daten.mannschaften.getExact(boot.getText().trim())!= null) {
      // Bugfix 1.7.1: nur Standardmannschaft eintragen, wenn alle Eingabefelder (Mannschaft und Ziel) noch leer sind
      if (getAnzahlRuderer() == 0 && ziel.getText().trim().length() == 0) {
        setStandardMannschaft((DatenFelder)Daten.mannschaften.getComplete());
      }
    }
  }
  // nächstes Eingabefeld ermitteln
  void boot_focusLost(FocusEvent e) {
    boot_focusLostGetBoot();
  }
  void bootButton_focusLost(FocusEvent e) {
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().boote != null && bootButton.getBackground() != Color.red &&
        Daten.fahrtenbuch.getDaten().boote.getFirst(boot.getText().trim()) != null) {
      aktBoot = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getComplete();
    }
  }


  void ziel_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
    if (isDirectMode() && ziel.getText().trim().length()>0 && bootskm.getText().trim().length()==0) { altesZiel = ""; setZielKm(); }
  }

  void zielButton_focusLost(FocusEvent e) {
    setZielKm();
  }


  // Kilometer für Ziel ermitteln
  void setZielKm() {
    setFieldEnabledBootsKm();
    if (zielButton.getBackground() == Color.red) {
      if (!ziel.getText().trim().equals(altesZiel)) {
        // Das "Leeren" des Kilometerfeldes darf nur im DirectMode erfolgen. Im normalen Modus hätte das
        // den unschönen Nebeneffekt, daß beim korrigieren von unbekannten Zielen die eingegeben Kilometer
        // aus dem Feld verschwinden (ebenso nach der Suche nach unvollständigen Einträgen mit unbekannten
        // Zielen).
        if (isDirectMode() && (mode == MODE_START || mode == MODE_START_KORREKTUR || mode == MODE_ENDE || mode == MODE_NACHTRAG)) {
          bootskm.setText("");
          mannschkm.setText("");
        }
        altesZiel="";
      }
      return;
    }

    if (!ziel.getText().trim().equals(altesZiel) && !ziel.getText().trim().equals("") &&
        Daten.fahrtenbuch != null &&
        Daten.fahrtenbuch.getDaten().ziele != null) {
      // die folgende Zeile ist korrekt, da diese Methode nur nach "vervollstaendige" und bei
      // "zielButton.getBackground()!=Color.red" aus "ziel_keyReleased" oder "zielButton_focusLost"
      // aufgerufen wird und somit ein gültiger Datensatz bereits gefunden wurde!
      DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getComplete();
      if (d != null && ziel.getText().trim().equals(d.get(Ziele.NAME))) bootskm.setText(d.get(Ziele.KM));
      else {
        d = Daten.fahrtenbuch.getDaten().ziele.getExactComplete(ziel.getText().trim());
        if (d != null) bootskm.setText(d.get(Ziele.KM));
        else bootskm.setText("");    }
      }
    if (!bootskm.isEnabled()) bootskm_focusLost(null);
  }


  // altes Ziel speichern, um bei ziel_focusLost festzustellen, ob verändert
  void ziel_focusGained(FocusEvent e) {
    altesZiel = ziel.getText().trim();
  }


  void lfdnr_focusLost(FocusEvent e) {
    lfdnr.setText(EfaUtil.getLfdNr(lfdnr.getText()));
  }
  void bootskm_focusLost(FocusEvent e) {
      String s = EfaUtil.correctZehntelString(bootskm.getText());
      if (s.equals("0")) {
          bootskm.setText("");
      } else {
          bootskm.setText(s);
      }
    if (isDirectMode()) mannschkm_focusGained(null); // damit Mannsch-Km ausgefüllt werden
  }
  void mannschkm_focusLost(FocusEvent e) {
      String s = EfaUtil.correctZehntelString(mannschkm.getText());
      if (s.equals("0")) {
          mannschkm.setText("");
      } else {
          mannschkm.setText(s);
      }
  }
  void abfahrt_focusLost(FocusEvent e) {
  if (abfahrt.getText().trim().length()==0 && (isDirectMode())) setTime(abfahrt,0);
    abfahrt.setText(EfaUtil.correctTime(abfahrt.getText().trim()));
  }
  void ankunft_focusLost(FocusEvent e) {
  if (ankunft.getText().trim().length()==0 && (isDirectMode())) setTime(ankunft,0);
    ankunft.setText(EfaUtil.correctTime(ankunft.getText().trim()));
  }
  void geheZu_focusLost(FocusEvent e) {
    geheZu.setText("");
  }
  void geheZuNr(String nr) {
    if (Daten.fahrtenbuch == null) return;
    if (datensatzGeaendert && !sicherheitsabfrageDatensatz()) return;
    String orgPos = null;
    if (aktDatensatz != null) orgPos = aktDatensatz.get(Fahrtenbuch.LFDNR);
    String s = EfaUtil.getLfdNr(nr.trim());

    char ch = 'A' -1;
    do {
      if (Daten.fahrtenbuch.getExact( s + (ch>='A' ? ch+"" : "") ) != null) {
        SetFields((DatenFelder)Daten.fahrtenbuch.getComplete());
        break;
      } else {
        if (orgPos != null) Daten.fahrtenbuch.goTo(orgPos);
      }
    } while (++ch <= 'Z');
  }
  void geheZu_keyReleased(KeyEvent e) {
    geheZuNr(geheZu.getText().trim());
  }


  // Eintrag geändert
  void eintragGeaendert(KeyEvent e) {
    if (e == null || (e.getKeyCode() != KeyEvent.VK_TAB && e.getKeyChar() != '\t')) {
      datensatzGeaendert = true;
    }
  }

  void setFahrtDauerDefault() {
    if (Daten.efaConfig != null && Daten.efaConfig.standardFahrtart != null && Daten.efaConfig.standardFahrtart.length() > 0 &&
        Daten.efaTypes != null && Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_TRIP, Daten.efaConfig.standardFahrtart)) {
            fahrtDauer.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, Daten.efaConfig.standardFahrtart));
    } else {
        if (Daten.efaTypes != null && Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_NORMAL)) {
            fahrtDauer.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_NORMAL));
        } else {
            fahrtDauer.setSelectedIndex(0);
        }
    }
  }

  boolean isFahrtDauerMehrtagesfahrtAction(String fahrtart) {
      if (fahrtart == null) return false;
      if (fahrtart.equals(fahrtArt_mehrtagesfahrtBearbeiten) ||
          fahrtart.equals(fahrtArt_mehrtagesfahrtKonfigurieren) ||
          fahrtart.equals(fahrtArt_neueMehrtagesfahrt)) return true;
      return false;
  }


  // anderes Element der Liste fahrtDauer ausgewählt
  void fahrtDauer_itemStateChanged(ItemEvent e) {
    if (ignoreFahrtDauerItemStateChanges) return;
    if (oldFahrtDauerSelectedIndex<0) { oldFahrtDauerSelectedIndex = 9999; return; }
    if (Daten.fahrtenbuch == null) { oldFahrtDauerSelectedIndex = 9999; return; }
    if (fahrtDauer.getSelectedIndex() == oldFahrtDauerSelectedIndex) { oldFahrtDauerSelectedIndex = 9999; return; } // doppelte Aufrufe verhindern

    if (!isFahrtDauerMehrtagesfahrtAction((String)fahrtDauer.getSelectedItem())) {
        datensatzGeaendert = true;
    }
    oldFahrtDauerSelectedIndex = fahrtDauer.getSelectedIndex();

    if (isDirectMode()) return;

    if (fahrtArt_neueMehrtagesfahrt != null &&
        ((String)fahrtDauer.getSelectedItem()).equals(fahrtArt_neueMehrtagesfahrt)) {
      String mtourEnddatum = null;
      String mtourRudertage = null;
      if (aktDatensatz != null && 
          aktDatensatz.get(Fahrtenbuch.FAHRTART).startsWith(Fahrtenbuch.CONFIGURE_MTOUR+"@@")) {
        String tmp = aktDatensatz.get(Fahrtenbuch.FAHRTART);
        int pos = tmp.indexOf("@@");
        if (pos>0) {
          tmp = tmp.substring(pos+2,tmp.length());
          pos = tmp.indexOf("@@");
          if (pos>=0) {
            mtourEnddatum = tmp.substring(0,pos);
            mtourRudertage = tmp.substring(pos+2,tmp.length());
          }
        }
      }
      MehrtagestourFrame dlg = new MehrtagestourFrame(this,datum.getText().trim(),mtourEnddatum,mtourRudertage);
      Dialog.setDlgLocation(dlg,this);
      dlg.setModal(!Dialog.tourRunning);
      dlg.show();
      startBringToFront(false); // efaDirekt im BRC -- Workaround
    }
    if (fahrtArt_mehrtagesfahrtBearbeiten != null &&
        ((String)fahrtDauer.getSelectedItem()).equals(fahrtArt_mehrtagesfahrtBearbeiten)) {
      if (isDirectMode()) return;
      if (Daten.fahrtenbuch == null) return;
      if (datensatzGeaendert && !sicherheitsabfrageDatensatz()) return;
      ignoreFahrtDauerItemStateChanges=true;
      fahrtDauer.setPopupVisible(false);
      oeffneAuswahl(AuswahlFrame.MEHRTAGESFAHRTEN);
      if (aktDatensatz != null) try {
        String fa = aktDatensatz.get(Fahrtenbuch.FAHRTART);
        getAllFahrtDauer();
        if (fa.length()>0 && fa.startsWith(EfaTypes.TYPE_TRIP_MULTIDAY+":")) {
            fa = fa.substring(EfaTypes.TYPE_TRIP_MULTIDAY.length()+1);
            fahrtDauer.setSelectedItem(fa);
            fahrtDauer.setSelectedItem(fa);
        } else {
            setFahrtDauerDefault();
        }
      } catch(Exception ee) {}
      ignoreFahrtDauerItemStateChanges=false;
    }
  }


// ================================== Öffnen, Speichern, Beenden etc. ===========================

  // Programm initialisieren
  void appIni() {
    Calendar cal = GregorianCalendar.getInstance();

    datensatzGeaendert = false;
    aktBoot = null;
    continueMTour = false;

    // Prüfen, ob efa gestartet werden darf
    if (!Daten.efaSec.secFileExists() && admin == null) {
      admin = null;
      do {
        admin = AdminLoginFrame.login(null,International.getString("Zugang nur für Administratoren"));
        if (admin == null) {
            Daten.haltProgram(Daten.HALT_ADMIN);
        }
        if (!admin.allowedFahrtenbuchBearbeiten && !admin.allowedVollzugriff) {
            Dialog.error(International.getMessage("Du hast als Admin {name} keine Berechtigung, das Fahrtenbuch zu bearbeiten!", admin.name));
        }
      } while (!admin.allowedFahrtenbuchBearbeiten && !admin.allowedVollzugriff);
    }

    Daten.iniAllDataFiles();

/*
 * @todo: how to best implement this in efa2??
    if (!startEfaTour && !Daten.efaConfig.version.equals(Daten.PROGRAMMID) && Daten.efaConfig.version.compareTo(Daten.PROGRAMMID) != 0) {
      Dialog.neuBrowserDlg(this,Daten.EFA_SHORTNAME,
              "file:"+Daten.efaProgramDirectory+"html"+Daten.fileSep+"tour"+Daten.fileSep+"k06-001.html", // @todo: How to internationalize the tour?
              750,600,(int)Dialog.screenSize.getWidth()/2-375,(int)Dialog.screenSize.getHeight()/2-300);
 */

    Daten.checkEfaVersion(true);
    Daten.checkJavaVersion(true);
    Daten.checkRegister();

    if (startOpenFb != null && EfaUtil.canOpenFile(startOpenFb)) fahrtenbuchOeffnen(startOpenFb);
    else if (!Daten.efaConfig.letzteDatei.equals("") && EfaUtil.canOpenFile(Daten.efaConfig.letzteDatei)) fahrtenbuchOeffnen(Daten.efaConfig.letzteDatei);
    else if (Daten.efaConfig.letzteDatei.equals("") && Daten.efaConfig.countEfaStarts == 1)
      if (Dialog.yesNoDialog(International.getString("Neues Fahrtenbuch anlegen"),
	  International.getString("Du startest efa heute zum ersten Mal. Möchtest Du ein neues Fahrtenbuch anlegen?")
                                        ) == Dialog.YES) {
        askForOpenNewFb = true;
      }

/*
 * @todo: how to best implement this in efa2??
    // ggf. fragen, ob es sich um einen Berliner Verein handelt
    if (Daten.efaConfig.version.compareTo("EFA.141")<0 || openWelcome) {
      Daten.efaConfig.showBerlinOptions =
          Dialog.auswahlDialog(International.onlyFor("Berliner Vereine","de"),
                               International.onlyFor("efa verfügt über Funktionen (bzgl. Zielbereichen), die speziell für Berliner Vereine "+
                               "gedacht sind. Die entsprechenden Optionen sind für Nicht-Berliner Vereine nicht "+
                               "relevant und können, um die Übersichtlichkeit zu erhöhen, verborgen werden.\n"+
                               "Bitte wähle aus:","de"),
                               International.onlyFor("Optionen für Berliner Vereine anzeigen","de"),
                               International.onlyFor("Nur-Berlin-relevante Optionen verbergen","de"),
                               false) != 1;
      Daten.efaConfig.writeFile();
    }
 */

/*
 * @todo: how to best implement this in efa2??
    // ggf. nach Zielbereich, in dem der Verein liegt, fragen (wenn zum ersten Mal eine Version neuer/gleich 1.3.5 gestartet wird)
    if (Daten.efaConfig.showBerlinOptions && Daten.vereinsConfig != null &&
        (Daten.efaConfig.version.compareTo("EFA.135")<0 || openWelcome)) Daten.vereinsConfig.askForZielbereichOnStart();
*/

    // bei entspr. Einstellung Obmann-Auswahlliste ausblenden
    if (Daten.efaConfig != null && !Daten.efaConfig.showObmann) {
      this.obmannLabel.setVisible(false);
      this.obmann.setVisible(false);
    }
  }

  // Initialisierung, die sowohl im normalen als auch im Direct-Mode ausgeführt werden soll
  void appCommonIni() {
    updateMannschaftenAndShowButton();
  }

  public void updateMannschaftenAndShowButton() {
    mannschaften = new Mannschaften(null);
    if (Daten.mannschaften != null) {
      DatenFelder d = Daten.mannschaften.getCompleteFirst();
      while (d != null) {
        boolean stm_or_mannsch = false;
        if (d.get(Mannschaften.STM).trim().length()>0) stm_or_mannsch = true;
        for (int i=0; i<24 && !stm_or_mannsch; i++) if (d.get(Mannschaften.MANNSCH1+i).trim().length()>0) stm_or_mannsch = true;
        if (stm_or_mannsch) mannschaften.add(d);
        d = Daten.mannschaften.getCompleteNext();
      }
    }
    mannschaftSelectButton.setVisible(!mannschaften.isEmpty());
  }



  // Mehrtagestour zum FB und zur Auswahlliste hinzufügen (und als akt. Element auswählen)
  void addMehrtagestour(String name, String start, String ende, int rudertage, String gewaesser, boolean isEtappen) {
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getMehrtagesfahrt(name) != null) return;
    Daten.fahrtenbuch.addMehrtagesfahrt(name,start,ende,rudertage,gewaesser,isEtappen);

    ignoreFahrtDauerItemStateChanges=true;
    oldFahrtDauerSelectedIndex=-1;
    setFahrtDauerDefault();
    if (fahrtArt_mehrtagesfahrtBearbeiten != null) {
        oldFahrtDauerSelectedIndex=-1;
        fahrtDauer.removeItem(fahrtArt_mehrtagesfahrtBearbeiten); // letztes Element ("Mehrtagesfahrten bearbeiten") entfernen
    }
    if (fahrtArt_neueMehrtagesfahrt != null) {
        oldFahrtDauerSelectedIndex=-1;
        fahrtDauer.removeItem(fahrtArt_neueMehrtagesfahrt); // letztes Element ("neue Mehrtagesfahrt") entfernen
    }
    oldFahrtDauerSelectedIndex=-1; 
    fahrtDauer.addItem(name);
    if (fahrtArt_neueMehrtagesfahrt != null) {
        oldFahrtDauerSelectedIndex=-1;
        fahrtDauer.addItem(fahrtArt_neueMehrtagesfahrt);
    }
    if (fahrtArt_mehrtagesfahrtBearbeiten != null) {
         oldFahrtDauerSelectedIndex=-1;
         fahrtDauer.addItem(fahrtArt_mehrtagesfahrtBearbeiten);
    }
    oldFahrtDauerSelectedIndex=-1; 
    fahrtDauer.setSelectedItem(name);
    ignoreFahrtDauerItemStateChanges=false;
    datensatzGeaendert = true;
  }


  void fahrtDauerAddItems(boolean withMehrtagesfahrt) {
      if (Daten.efaTypes != null) {
          for (int i=0; i<Daten.efaTypes.size(EfaTypes.CATEGORY_TRIP); i++) {
              if (!withMehrtagesfahrt &&
                  Daten.efaTypes.getType(EfaTypes.CATEGORY_TRIP, i).equals(EfaTypes.TYPE_TRIP_MULTIDAY)) {
                  // nothing to do
              } else {
                  fahrtDauer.addItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, i));
              }
          }
      }
  }


  // Alle Angaben zu Mehrtagesfahrten zusammentragen
  // (nur nach FB öffnen aufrufen, da akt. Eintrag nicht gemerkt wird)
  void getAllFahrtDauer() {
    fahrtDauer.removeAllItems();
    fahrtDauerAddItems(false);
    if (fahrtArt_mehrtagesfahrtKonfigurieren != null && (mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN)) {
        fahrtDauer.addItem(fahrtArt_mehrtagesfahrtKonfigurieren);
    }
    if (Daten.fahrtenbuch != null) {
      String[] mtours = Daten.fahrtenbuch.getAllMehrtagesfahrtNamen();
      for (int i=0; mtours != null && i<mtours.length; i++)
        fahrtDauer.addItem(Daten.fahrtenbuch.getMehrtagesfahrt(mtours[i]).getDisplayName());
    }
    if (fahrtArt_neueMehrtagesfahrt != null) {
        fahrtDauer.addItem(fahrtArt_neueMehrtagesfahrt);
    }
    if (fahrtArt_mehrtagesfahrtBearbeiten != null) {
        fahrtDauer.addItem(fahrtArt_mehrtagesfahrtBearbeiten);
    }
    if (fahrtDauer.getItemCount()>0) {
        setFahrtDauerDefault();
    }
  }




  // Fahrtenbuch öffnen
  void fahrtenbuchOeffnen(String datei) {
    Daten.fahrtenbuch = new Fahrtenbuch(datei);
    this.setTitle(Daten.EFA_SHORTNAME + " - " + datei);
    continueMTour = false;
    SetBlankFields();
    datensatzGeaendert = false;
    neuerDatensatz = true; //wird auf false gesetzt, falls SetFields(null) ausgeführt wird
    neuerDatensatz_einf = false;
    if (!Daten.fahrtenbuch.readFile()) return;
    getAllFahrtDauer();
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null)
      Daten.fahrtenbuch.getDaten().mitglieder.getAliases();
    DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    SetFields(d);
    if (mode == MODE_FULL) Daten.efaConfig.letzteDatei = datei;
    lfdNrForNewEntry = -1;
  }

  private String createFahrtartKey(String fahrtart) {
    if (Daten.efaTypes != null && fahrtart != null &&
        !this.isFahrtDauerMehrtagesfahrtAction(fahrtart)) {
        String key = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_TRIP, fahrtart);
        if (key != null) {
           return key;
        } else {
           key = EfaTypes.TYPE_TRIP_MULTIDAY + ":" + Mehrtagesfahrt.getNameFromDisplayName(fahrtart);
        }
    }
    return "";
  }

  private DatenFelder getFields(String mtourEnddatum, String mtourRudertage, String mtourName) {
    String fd = createFahrtartKey((String)(fahrtDauer.getSelectedIndex() >= 0 ? fahrtDauer.getSelectedItem() : null));

    if (Daten.efaTypes != null && (mode == MODE_ENDE || mode == MODE_NACHTRAG) &&
        fd.startsWith(Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_MULTIDAY))) {
      if (mtourName != null) {
        fd = EfaTypes.TYPE_TRIP_MULTIDAY + ":" + mtourName;
      } else {
        fd = Fahrtenbuch.CONFIGURE_MTOUR +
             (mtourEnddatum != null || mtourRudertage != null ? "@@" + (mtourEnddatum != null ? mtourEnddatum : "") +
                                                              "@@" + (mtourRudertage != null ? mtourRudertage : "") : "");
      }
    }

    return Daten.fahrtenbuch.constructFields(EfaUtil.getLfdNr(lfdnr.getText())+"|"+
                          EfaUtil.removeSepFromString(datum.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(boot.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(stm.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[0].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[1].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[2].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[3].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[4].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[5].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[6].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[7].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[8].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[9].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[10].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[11].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[12].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[13].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[14].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[15].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[16].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[17].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[18].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[19].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[20].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[21].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[22].getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannsch[23].getText().trim())+"|"+
                          Integer.toString(getObmann())+"|"+
                          EfaUtil.removeSepFromString(abfahrt.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(ankunft.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(ziel.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(bootskm.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(mannschkm.getText().trim())+"|"+
                          EfaUtil.removeSepFromString(bemerk.getText().trim())+"|"+
                          fd);
  }


  // den Datensatz nun wirklich speichern;
  // fd - FAHRTART,
  void speichereDatensatzInFB(boolean _neuerDatensatz, String mtourEnddatum, String mtourRudertage, String mtourName) {
    if (Daten.fahrtenbuch == null) return;

    if (!_neuerDatensatz && aktDatensatz != null && !aktDatensatz.get(Fahrtenbuch.LFDNR).equals(EfaUtil.getLfdNr(lfdnr.getText().trim()))) {
      // Datensatz mit geänderter LfdNr: Der alte Datensatz muß gelöscht werden!
      Daten.fahrtenbuch.delete(aktDatensatz.get(Fahrtenbuch.LFDNR));
    }

    aktDatensatz = Daten.fahrtenbuch.add(getFields(mtourEnddatum, mtourRudertage, mtourName));

    if (isAdminMode()) {
      Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYMODIFIED,
              International.getString("Admin") + ": " +
              International.getMessage("Fahrtenbuch-Eintrag #{lfdnr} wurde verändert.",
    		  (aktDatensatz != null ? aktDatensatz.get(Fahrtenbuch.LFDNR) : "$$")));
    }
  }


  // Datensatz speichern
  // liefert "true", wenn erfolgreich
  boolean speichereDatensatz() {

    if (Daten.fahrtenbuch == null) return false;

    // Eintrag (Km,Mannschkm) auf Korrektheit prüfen
    if (EfaUtil.zehntelString2Int(bootskm.getText().trim()) == 0)
      if (Dialog.yesNoDialog(International.getString("Warnung"),
			International.getString("Die Bootskilometer sind 0.") + "\n" +
                        International.getString("Möchtest Du diesen Eintrag wirklich hinzufügen?")
                                             ) == Dialog.NO) {
        Dialog.infoDialog(International.getString("Information"),
                International.getString("Eintrag nicht hinzugefügt."));
        bootskm.requestFocus();
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return false;
      }
    if (EfaUtil.zehntelString2Int(bootskm.getText().trim()) * getAnzahlRuderer() != EfaUtil.zehntelString2Int(mannschkm.getText().trim()))
      if (Dialog.yesNoDialog(International.getString("Warnung"),
      International.getString("Die Mannschaftskilometer stimmen nicht mit den "+
                              "Bootskilometern und der Anzahl der Ruderer überein.") + "\n" +
                              International.getString("Möchtest Du diesen Eintrag wirklich hinzufügen?")
                                             ) == Dialog.NO) {
        Dialog.infoDialog(International.getString("Information"),
                International.getString("Eintrag nicht hinzugefügt."));
//        mannschkm.requestFocus(); // wg. Dennis auskommentiert (s. Mail vom 26.02.02)
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return false;
      }

    String neu = EfaUtil.getLfdNr(lfdnr.getText());
    if ( (Daten.fahrtenbuch.getExact(neu) != null && (neuerDatensatz || !neu.equals(aktDatensatz.get(Fahrtenbuch.LFDNR))) )
         || neu.equals("")) {
      Dialog.infoDialog(International.getString("Ungültige laufende Nummer"),
	                                     International.getString("Diese Laufende Nummer ist bereits vergeben! Jede Laufende "+
                                         "Nummer darf nur einmal verwendet werden werden. "+
                                         "Bitte korrigiere die laufende Nummer des Eintrags!\n\n"+
                                         "Hinweis: Um mehrere Einträge unter 'derselben' Nummer hinzuzufügen, "+
                                         "füge einen Buchstaben von A bis Z direkt an die Nummer an!"));
      lfdnr.requestFocus();
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      return false;
    }

    if (neuerDatensatz) {
      // erstmal prüfen, ob die Laufende Nummer korrekt ist
      String alt = " ";
      DatenFelder d;
      if ((d = (DatenFelder)Daten.fahrtenbuch.getCompleteLast()) != null)
        alt = EfaUtil.getLfdNr(d.get(Daten.fahrtenbuch.LFDNR));
      if (EfaUtil.compareIntString(neu,alt) <= 0 && !neuerDatensatz_einf) {
        boolean printWarnung = true;
        if (lfdNrForNewEntry > 0 && EfaUtil.string2int(neu,-1) == lfdNrForNewEntry+1) printWarnung = false;
        if (printWarnung && // nur warnen, wenn das erste Mal eine zu kleine LfdNr eingegeben wurde!
            Dialog.yesNoDialog(International.getString("Warnung"),
							   International.getString("Die Laufende Nummer dieses Eintrags ist kleiner als die des "+
                                           "letzten Eintrags. Bist Du sicher, daß Du den Eintrag mit einer kleineren "+
                                           "Laufenden Nummer hinzufügen möchtest?")
                                           ) == Dialog.NO) {
          Dialog.infoDialog(International.getString("Information"),
                  International.getString("Eintrag nicht hinzugefügt."));
          lfdnr.requestFocus();
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return false;
        }
      }
      lfdNrForNewEntry = EfaUtil.string2date(lfdnr.getText(),1,1,1).tag; // lfdNr merken, nächster Eintrag erhält dann per default diese Nummer + 1
    } else { // geänderter Fahrtenbucheintrag
      if (!aktDatensatz.get(Fahrtenbuch.LFDNR).equals(EfaUtil.getLfdNr(lfdnr.getText()))) {
        if (Dialog.yesNoDialog(International.getString("Warnung"),
            International.getString("Du hast die Laufende Nummer dieses Eintrags verändert!\n" +
            "Bist Du sicher, daß die Laufende Nummer geändert werden soll?")) == Dialog.NO) {
          Dialog.infoDialog(International.getString("Information"),
                  International.getString("Geänderter Eintrag nicht gespeichert."));
          lfdnr.requestFocus();
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return false;
        }
      }
    }

    // Prüfen, ob im Fall einer Mehrtagesfahrt diese im angegebenen Zeitraum der ausgewählten (vorhandenen) Fahrt liegt
    if (fahrtDauer.getSelectedIndex() >= 0 &&
        !this.isFahrtDauerMehrtagesfahrtAction(fahrtDauer.getSelectedItem().toString())) {
      String fahrtart = (String)this.fahrtDauer.getSelectedItem();
      Mehrtagesfahrt m = null;
      if (fahrtart != null) m = Daten.fahrtenbuch.getMehrtagesfahrt(fahrtart);
      if (m != null) {
        String datum = this.datum.getText();
        if (EfaUtil.secondDateIsAfterFirst(m.ende,datum) || EfaUtil.secondDateIsAfterFirst(datum,m.start)) {
            Dialog.error(International.getMessage("Das Datum des Fahrtenbucheintrags ({entry}) liegt außerhalb des Zeitraums " +
                " ({date_from} - {date_to}), der für die ausgewählte Mehrtagesfahrt '{name}' angegeben wurde.",
                datum, m.start, m.ende, m.name) +
                "\n\n" +
                International.getString("Falls in diesem Jahr mehrere Mehrtagesfahrten mit derselben Strecke durchgeführt wurden, " +
                "so erstelle bitte für jede einzelne Mehrtagesfahrt einen separaten Eintrag in efa. Ansonsten wähle bitte entweder " +
                "eine Mehrtagesfahrt mit passendem Datum aus oder korrigiere das Datum dieses Eintrages oder der Mehrtagesfahrt."));
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return false;
        }
      }
    }

    // Prüfen, ob der angegebene Obmann tatsächlich exisitert
    int obnr = getObmann();
    if (obnr>=0) {
      if ( (obnr == 0 && stm.getText().trim().length()==0) ||
           (obnr >  0 && mannsch[obnr-1].getText().trim().length() == 0) ) {
        Dialog.error(International.getString("Für die als Obmann ausgewählte Person wurde kein Name eingegeben. "+
                     "Bitte gib entweder einen Namen ein oder wählen jemand anderes als Obmann aus!"));
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return false;
      }
    }

    // Ok, alles klar: Jetzt speichern

    // Mehrtagestour fortsetzen (d.h. Fahrtart beim neuen Eintrag beibehalten)??
    // -> nur, wenn es sich um eine wirkliche Wanderfahrt handelt, die in Form mehrerer Etappen eingegeben wird
    if (Daten.efaTypes != null &&
        Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_TRIP, fahrtDauer.getSelectedItem().toString()) == null &&
        !isFahrtDauerMehrtagesfahrtAction(fahrtDauer.getSelectedItem().toString()) &&
        Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtDauer.getSelectedItem()) != null &&
        Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtDauer.getSelectedItem()).isEtappen) {
        continueMTour=true;
    } else {
        continueMTour=false;
    }

    speichereDatensatzInFB(neuerDatensatz,null,null,null);
    datensatzGeaendert = false;
    return true;
  }


  // Fahrtenbuch speichern
  // liefert "true", wenn erfolgreich gespeichert wurde
  boolean speichereFahrtenbuch(boolean unter) {
    if (Daten.fahrtenbuch == null) return false;

    speichereDatenbanken();

    boolean ok = true;
    boolean alsoSaveZusatzlistenInNewDirectory = false;
    String dat;
    String oldFilename = Daten.fahrtenbuch.getFileName();

    if (unter) {
      if (Daten.fahrtenbuch.getFileName() != null && !Daten.fahrtenbuch.getFileName().equals(""))
        dat = Dialog.dateiDialog(this,International.getString("Fahrtenbuch speichern unter"),
                International.getString("efa Fahrtenbuch")+" (*.efb)","efb",Daten.fahrtenbuch.getFileName(),true);
      else
        dat = Dialog.dateiDialog(this,International.getString("Fahrtenbuch speichern unter"),
                International.getString("efa Fahrtenbuch")+" (*.efb)","efb",null,true);

      if (dat == null) ok = false;

      if (ok) {
        if (!dat.endsWith(".") && !dat.toUpperCase().endsWith(".EFB")) {
          dat = dat+".efb";
          if (EfaUtil.canOpenFile(dat)) {
            switch (Dialog.yesNoCancelDialog(International.getString("Datei existiert bereits"),
					International.getMessage("Soll die Datei '{filename}' überschrieben werden?",dat))) {
              case Dialog.YES: break;
              default: return false;
            }
          }
        }

        if (oldFilename != null && oldFilename.length()>0 &&
            !EfaUtil.getPathOfFile(oldFilename).equals(EfaUtil.getPathOfFile(dat))) {
          // Fahrtenbuch wird in einem neuen Verzeichnis gespeichert --> Auch Zusatzdatenlisten dort speichern
          alsoSaveZusatzlistenInNewDirectory = true;
          // ggf. Pfadangaben für Zusatzdatenlisten entfernen
          FBDaten fbDaten = Daten.fahrtenbuch.getDaten();
          fbDaten.bootDatei = EfaUtil.getFilenameWithoutPath(fbDaten.bootDatei);
          fbDaten.mitgliederDatei = EfaUtil.getFilenameWithoutPath(fbDaten.mitgliederDatei);
          fbDaten.zieleDatei = EfaUtil.getFilenameWithoutPath(fbDaten.zieleDatei);
          fbDaten.statistikDatei = EfaUtil.getFilenameWithoutPath(fbDaten.statistikDatei);
        }

        Daten.fahrtenbuch.setFileName(dat);
      }
    }
    if (Daten.fahrtenbuch.getFileName() != null && !Daten.fahrtenbuch.getFileName().equals("") &&
        (!unter || ok)) {
      boolean success = Daten.fahrtenbuch.writeFile();
      String newDir = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName()); if (!newDir.endsWith(Daten.fileSep)) newDir += Daten.fileSep;
      if (success && alsoSaveZusatzlistenInNewDirectory) {
        FBDaten fbDaten = Daten.fahrtenbuch.getDaten();
        fbDaten.boote.setFileName(newDir+fbDaten.bootDatei); fbDaten.boote.writeFile();
        fbDaten.mitglieder.setFileName(newDir+fbDaten.mitgliederDatei); fbDaten.mitglieder.writeFile();
        fbDaten.ziele.setFileName(newDir+fbDaten.zieleDatei); fbDaten.ziele.writeFile();
        fbDaten.statistik.setFileName(newDir+fbDaten.statistikDatei); fbDaten.statistik.writeFile();
      }
      if (isAdminMode()) {
        if (success) {
          Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_CHANGESSAVED,
              International.getString("Admin") + ": " +
              International.getString("Änderungen am Fahrtenbuch wurden erfolgreich gespeichert."));
        } else {
          Logger.log(Logger.ERROR, Logger.MSG_ADMIN_LOGBOOK_CHANGESNOTSVD,
              International.getString("Admin") + ": " +
              International.getString("Die Änderungen am Fahrtenbuch konnten nicht gespeichert werden."));
        }
      }

      if (unter && success) {
        if (Dialog.yesNoDialog(International.getString("Gespeichertes Fahrtenbuch öffnen?"),
                               International.getMessage("Soll das soeben gespeicherte Fahrtenbuch '{filename}' jetzt benutzt werden?",Daten.fahrtenbuch.getFileName())) == Dialog.YES) {
          if (mode == MODE_FULL) Daten.efaConfig.letzteDatei = Daten.fahrtenbuch.getFileName();
          this.setTitle(Daten.EFA_SHORTNAME + " - " + Daten.fahrtenbuch.getFileName());
        } else {
          // ursprüngliches Fahrtenbuch wieder laden
          Daten.fahrtenbuch.setFileName(oldFilename);
          Daten.fahrtenbuch.readFile();
        }
      }
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      return success;
    }
    startBringToFront(false); // efaDirekt im BRC -- Workaround
    return false;
  }


  // Zusatzdatenbanken speichern
  void speichereDatenbanken() {
    if (Daten.fahrtenbuch == null) return;
    if (Daten.fahrtenbuch.getDaten().boote != null && Daten.fahrtenbuch.getDaten().boote.isChanged()) {
      if (!Daten.fahrtenbuch.getDaten().boote.writeFile()) {
        Dialog.infoDialog(  	International.getString("Fehler"),
                International.getMessage("Änderungen an der {listname} konnten nicht gespeichert werden!",
                International.getString("Bootsliste")));
      } else {
        if (isAdminMode()) {
          Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_CHANGESSAVED,
                International.getString("Admin") + ": " +
                International.getMessage("Änderungen an der {listname} wurden gespeichert.",
                International.getString("Bootsliste")));
        }
      }
      if (Daten.synBoote != null && Daten.synBoote.isChanged()) { // Auch Synonyme wegen ggf. hinzugefügter Kombiboote speichern!
        if (!Daten.synBoote.writeFile()) {
          Dialog.infoDialog(	International.getString("Fehler"),
                  International.getMessage("Änderungen an der {listname} konnten nicht gespeichert werden!",
                  International.getString("Boots-Synonymliste")));
        }
      }
    }
    if (Daten.fahrtenbuch.getDaten().mitglieder != null && Daten.fahrtenbuch.getDaten().mitglieder.isChanged()) {
      if (!Daten.fahrtenbuch.getDaten().mitglieder.writeFile()) {
        Dialog.infoDialog(		International.getString("Fehler"),
                International.getMessage("Änderungen an der {listname} konnten nicht gespeichert werden!",
                International.getString("Mitgliederliste")));
      } else {
          Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_CHANGESSAVED,
                International.getString("Admin") + ": " +
                International.getMessage("Änderungen an der {listname} wurden gespeichert.",
                International.getString("Mitgliederliste")));
      }
    }
    if (Daten.fahrtenbuch.getDaten().ziele != null && Daten.fahrtenbuch.getDaten().ziele.isChanged()) {
      if (!Daten.fahrtenbuch.getDaten().ziele.writeFile()) {
        Dialog.infoDialog(		International.getString("Fehler"),
                International.getMessage("Änderungen an der {listname} konnten nicht gespeichert werden!",
                International.getString("Zielliste")));
      } else {
          Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_CHANGESSAVED,
                International.getString("Admin") + ": " +
                International.getMessage("Änderungen an der {listname} wurden gespeichert.",
                International.getString("Zielliste")));
      }
    }
  }

  void updateWoTag() {
    wotag.setText("("+EfaUtil.getWoTag(datum.getText().trim())+")"); 
  }


  // vervollstaendige() für alle Felder aufrufen
  void vervollstaendigeAlleFelder() {
    vervollstaendige(boot,bootButton,Daten.fahrtenbuch.getDaten().boote,null,this,false);
    vervollstaendige(stm,stmButton,Daten.fahrtenbuch.getDaten().mitglieder,null,this,false);
    erkenneFelder1bis16();
    vervollstaendige(ziel,zielButton,Daten.fahrtenbuch.getDaten().ziele,null,this,false);
    setColoredMannschButton();
  }


  // Datums-Feld im try-Block setzten (Java-Bug-Workaround)
  void datumSetText(String s) {
    // die folgenden Anweisungen im try-Block, weil datum.setText() unter Linux manchmal
    // völlig unerklärlich eine Exception auslöst.
    try {
      datum.setText(s);
    } catch(Exception e) {
      try {
        datum.setText(s);
      } catch(Exception ee) {
        try {
          if (datumErrorCount++ == 0 && !datum.getText().equals(s))
            Dialog.error(International.getString("Ein Java-Fehler beim Setzen des Datums ist aufgetreten!\n"+
                         "Bitte setze den Cursor von Hand ins Datums-Feld (einfach "+
                         "einmal mit der Maus ins Datums-Feld klicken) und setze "+
                         "die Arbeit dann fort."));
        } catch(Exception eeee) {
          Dialog.error(International.getString("Ein Java-Fehler beim Setzen des Datums ist aufgetreten!\n"+
                       "Bitte setze den Cursor von Hand ins Datums-Feld (einfach "+
                       "einmal mit der Maus ins Datums-Feld klicken) und setze "+
                       "die Arbeit dann fort."));
        }
      }
    }
  }


  // Felder setzen
  void SetFields(DatenFelder d) {
   if (Daten.fahrtenbuch == null) return;
    datensatzGeaendert = false;
    refDatensatz = aktDatensatz;
    aktDatensatz = d;
    continueMTour = false;
    if (d == null) { // wenn beim Start leeres FB geöffnet
      lfdnr.setText("1");
      Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Eintrag zum Fahrtenbuch hinzufügen"));
      return;
    }
    neuerDatensatz = false;
    neuerDatensatz_einf = false;
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Änderungen speichern"));
    refDate = d.get(Fahrtenbuch.DATUM);
    Daten.fahrtenbuch.goTo(d.get(Fahrtenbuch.LFDNR)); // für "getCompleteNext" etc. akt. Datensatz erstmal einstellen

    lfdnr.setText(d.get(Fahrtenbuch.LFDNR));
    datumSetText(d.get(Fahrtenbuch.DATUM));
    updateWoTag();
    boot.setText(d.get(Fahrtenbuch.BOOT));
    stm.setText(d.get(Fahrtenbuch.STM));
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) mannsch[i].setText(d.get(Fahrtenbuch.MANNSCH1+i));
    abfahrt.setText(d.get(Fahrtenbuch.ABFAHRT));
    ankunft.setText(d.get(Fahrtenbuch.ANKUNFT));
    ziel.setText(d.get(Fahrtenbuch.ZIEL));
    bootskm.setText(d.get(Fahrtenbuch.BOOTSKM));
    mannschkm.setText(d.get(Fahrtenbuch.MANNSCHKM));
    bemerk.setText(d.get(Fahrtenbuch.BEMERK));
    Mehrtagesfahrt mtour = null;
    if (Daten.efaTypes != null && Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_MULTIDAY) &&
        d.get(Fahrtenbuch.FAHRTART).startsWith(EfaTypes.TYPE_TRIP_MULTIDAY+":")) {
        mtour = Daten.fahrtenbuch.getMehrtagesfahrt(d.get(Fahrtenbuch.FAHRTART).substring(EfaTypes.TYPE_TRIP_MULTIDAY.length()+1));
    }
    if (fahrtDauer.getItemCount()>0) {
      if (mtour != null) {
        fahrtDauer.setSelectedItem(mtour.getDisplayName());
      } else if (Mehrtagesfahrt.isVordefinierteFahrtart(d.get(Fahrtenbuch.FAHRTART))) {
        fahrtDauer.setSelectedItem(Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, d.get(Fahrtenbuch.FAHRTART)));
      } else if (d.get(Fahrtenbuch.FAHRTART).startsWith(Fahrtenbuch.CONFIGURE_MTOUR) &&
              (mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN)) {
        fahrtDauer.setSelectedItem(fahrtArt_mehrtagesfahrtKonfigurieren);
      }
    }
    setObmann(EfaUtil.string2int(d.get(Fahrtenbuch.OBMANN),-1),true);
    mannschAuswahl = 0;
    setActiveMannsch(mannschAuswahl);
    setColoredMannschButton();

    // erkennen, ob Einträge in den Feldern gefunden wurden
    vervollstaendigeAlleFelder();

    aktBoot = null;
    datensatzGeaendert = false; // Muß hier nochmal aufgerufen werden, da sonst wg. ggf. Änderung an fahrtDauer auf true gesetzt
    lfdNrForNewEntry = -1; // -1 bedeutet, daß beim nächsten neuen Datensatz die LfdNr "last+1" vorgegeben wird
  }


  // Mannschaftsfelder erkennen
  void erkenneFelder1bis16() {
    for (int i=0; i<8; i++)
      vervollstaendige(mannsch[i + mannschAuswahl * 8],mannschButton[i],Daten.fahrtenbuch.getDaten().mitglieder,null,this,false);
  }

  // Felder zurücksetzen
  void SetBlankFields() {
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Eintrag zum Fahrtenbuch hinzufügen"));
    
    lfdnr.setText("");
    datumSetText(""); updateWoTag();
    boot.setText("");
    stm.setText("");
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) mannsch[i].setText("");
    abfahrt.setText("");
    ankunft.setText("");
    ziel.setText("");
    bootskm.setText("");
    mannschkm.setText("");
    bemerk.setText("");
    setObmann(-1,true);

    mannschAuswahl = 0;
    setActiveMannsch(mannschAuswahl);
    setColoredMannschButton();

    if (fahrtDauer.getItemCount()>0 && !continueMTour) {
      setFahrtDauerDefault();
    }
    DatenFelder d;
    if (Daten.fahrtenbuch != null) d = (DatenFelder)Daten.fahrtenbuch.getCompleteLast();
    else d = null;
    try {
      if (!isDirectMode() && lfdNrForNewEntry > 0) { // Seit letztem Hinzufügen eines neuen Datensatzes wurde nicht im FB navigiert: Neue LfdNr ist die des zuletzt eingetragenen Datensatzes + 1
        lfdnr.setText(Integer.toString(lfdNrForNewEntry+1));
      } else { // noch kein Eintrag hinzugefügt oder im FB navigiert seit Hinzufügen: Neue LfdNr ist letzte + 1
        if (d != null) {
          lfdnr.setText(Integer.toString(EfaUtil.string2date(d.get(Fahrtenbuch.LFDNR),1,1,1).tag+1));
        }
      }
      if (d != null) {
        refDate = d.get(Fahrtenbuch.DATUM);
      }
      setDateFromRefDate();
      datum.requestFocus();
      datum.select(0,datum.getText().trim().length());
    } catch(Exception ee) {
      lfdnr.requestFocus();
    }
    setGrayButtons();

    aktBoot = null;
    neuerDatensatz = true;
    neuerDatensatz_einf = false;
    aktDatensatz = null;
    datensatzGeaendert = false;
  }


  // alle Buttons (neben den Feldern) auf grau setzen, sowie Ref-Felder auf ""
  void setGrayButtons() {
    setButtonColor(bootButton,Color.lightGray);
    setButtonColor(stmButton,Color.lightGray);
    for (int i=0; i<8; i++) setButtonColor(mannschButton[i],Color.lightGray);
    setButtonColor(zielButton,Color.lightGray);
    setButtonColor(weitereMannschButton,Color.lightGray);
  }


  // Abfrage, ob geänderter Datensatz gespeichert werden soll;
  // liefert "true", wenn weitergemacht werden darf
  boolean sicherheitsabfrageDatensatz() {
    if (Daten.fahrtenbuch == null) return true;
    if (isDirectMode()) return true;
    if (datensatzGeaendert) {
      String txt;
      if (neuerDatensatz) txt= International.getString("Der aktuelle Eintrag wurde verändert und noch nicht zum Fahrtenbuch hinzugefügt. "+
                               "Möchtest Du ihn jetzt hinzufügen?");
      else txt = International.getString("Änderungen an dem aktuellen Eintrag wurden noch nicht gespeichert.") +
                 "\n" + International.getString("Möchtest Du die Änderungen jetzt speichern?");
      switch(Dialog.yesNoCancelDialog(International.getString("Eintrag nicht gespeichert"),txt)) {
        case Dialog.YES: speichereDatensatz(); break;
        case Dialog.NO: break;
        default: startBringToFront(false); // efaDirekt im BRC -- Workaround
                 return false;
      }
    }
    startBringToFront(false); // efaDirekt im BRC -- Workaround
    return true;
  }


  // Sicherheitsabfrage, ob Zusatzdatenbanken schon gespeichert
  // liefert "true", wenn weitergemacht werden darf
  boolean sicherheitsabfrageZusatzdatenbanken() {
    if (Daten.fahrtenbuch == null) return true;
    if (isDirectMode()) return true;
    if ( (Daten.fahrtenbuch.getDaten().mitglieder != null && Daten.fahrtenbuch.getDaten().mitglieder.isChanged()) ||
         (Daten.fahrtenbuch.getDaten().boote != null && Daten.fahrtenbuch.getDaten().boote.isChanged()) ||
         (Daten.fahrtenbuch.getDaten().ziele != null && Daten.fahrtenbuch.getDaten().ziele.isChanged())) {
      int ret =  Dialog.yesNoCancelDialog(International.getString("Änderungen nicht gespeichert"),
						International.getString("Änderungen an den Datenlisten wurden noch nicht gespeichert.") +
                                             "\n" +  International.getString("Möchtest Du die Änderungen jetzt speichern?"));
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      switch (ret) {
        case Dialog.YES: speichereDatenbanken(); return true;
        case Dialog.NO: return true;
        default: return false;
      }
    } else return true;
  }


  // Sicherheitsabfrage, ob Fahrtenbuch schon gespeichert
  // liefert "true", wenn weitergemacht werden darf
  boolean sicherheitsabfrage() {
    if (Daten.fahrtenbuch == null) return true; // kein Fahrtenbuch angelegt -> getippte Zeichen ignorieren
    if (isDirectMode()) return true;

    if (!sicherheitsabfrageDatensatz()) return false;

    if (Daten.fahrtenbuch.isChanged()) {
      int ret = Dialog.yesNoCancelDialog(International.getString("Änderungen nicht gespeichert"),
						International.getString("Änderungen am Fahrtenbuch wurden noch nicht gespeichert.") +
                                                "\n"+
                                                International.getString("Möchtest Du die Änderungen jetzt speichern?"));
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      switch (ret) {
        case Dialog.YES: return speichereFahrtenbuch(false);
        case Dialog.NO: return sicherheitsabfrageZusatzdatenbanken();
        default: return false;
      }
    } else return sicherheitsabfrageZusatzdatenbanken();

  }


  // Eingabedialog zum Erstellen eines Fahrtenbuchs zeigen;
  // wird von FahrtenbuchNeuFortsetzenFrame aufgerufen
  public void neuesFahrtenbuchDialog(boolean neu) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    NeuesFahrtenbuchFrame dlg = new NeuesFahrtenbuchFrame(this,neu);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    if (neu && askForOpenNewFb) dlg.fahrtenbuch.setText(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR)+".efb");
    askForOpenNewFb=false;
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }


  // Neues Fahrtenbuch erstellen (wird von NeuesFahrtenbuchFrame aufgerufen)
  public void neuesFahrtenbuch() {
    if (Daten.fahrtenbuch == null) return;
    continueMTour = false;
    refDate="";
    refDatensatz=null;
    SetBlankFields();
    lfdnr.setText("1");
    this.setTitle(Daten.EFA_SHORTNAME + " - " + Daten.fahrtenbuch.getFileName());
    if (mode == MODE_FULL) Daten.efaConfig.letzteDatei = Daten.fahrtenbuch.getFileName();
    getAllFahrtDauer();
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null)
      Daten.fahrtenbuch.getDaten().mitglieder.getAliases();
    datensatzGeaendert = false;
  }

  public void userInteractionsUponStart() {
    if (startEfaTour)
      Dialog.startTour(this);
    if (askForOpenNewFb) {
      neuesFahrtenbuchDialog(true);
    }
  }


  // Programm beenden
  void cancel() {
    if (mode != MODE_FULL) {
      if (mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN) {
        if (efaDirektAdminFrame == null) return;
        if (!sicherheitsabfrage()) return;
        Dialog.frameClosed(this);
        dispose();
        efaDirektAdminFrame.fahrtenbuchClosed();
        efaDirektAdminFrame=null;
        return;
      }
      hideEfaFrame();
      efaDirektFrame.setEnabled(true);
      efaDirektFrame.toFront();
      return;
    }
    if (!sicherheitsabfrage()) return;
    if (!Daten.efaConfig.writeFile()) {
        LogString.logError_fileWritingFailed(Daten.efaConfig.getFileName(), International.getString("Konfigurationsdatei"));
    }
    Daten.haltProgram(0);
  }

  void efaButton_actionPerformed(ActionEvent e) {
    this.jMenuHelpAbout_actionPerformed(null);
  }


// ================================= METHODEN FUER DEN AUFRUF AUS EFADIREKTFRAME ==============================

  class EfaFrameBringToFrontThread extends Thread {
    EfaFrame efaFrame;
    int afterMs;

    public EfaFrameBringToFrontThread(EfaFrame efaFrame, int afterMs) {
      this.efaFrame = efaFrame;
      this.afterMs = afterMs;
    }

    public void run() {
      try {
        Thread.sleep(afterMs);
        if (efaFrame.getState() == Frame.ICONIFIED) {
          efaFrame.setState(Frame.NORMAL);
          Thread.sleep(afterMs);
        }
        efaFrame.toFront();
      } catch(Exception e) {
      }
    }

  }

  public void setFixedLocation(int x, int y) {
    if (x>=0 && y>=0) {
      this.positionX = x;
      this.positionY = y;
    }
    this.setLocation(this.positionX,this.positionY);
  }

  private void showEfaFrame(Component focusComponent) {
    if (Daten.efaConfig != null && infoLabel.isVisible() != Daten.efaConfig.efaDirekt_showEingabeInfos) {
      infoLabel.setVisible(Daten.efaConfig.efaDirekt_showEingabeInfos);
      packFrame("showEfaFrame()");
    }
    setFixedLocation(-1,-1);
    this.show();
    this.setVisible(true);
    this.toFront();
    Dialog.frameOpened(this);
    if (focusComponent != null) focusComponent.requestFocus();

    startBringToFront(true);
  }

  public void startBringToFront(boolean always) {
    // Irgendwie ist im BRC das EfaFrame immer dann, wenn zuvor eine Dialog-Box aufpoppte, noch immer nicht
    // im Vordergrund; daher dieser Workaround
    if (!always) {
      // nur im Admin-Mode nach vorne bringen
      if (!isAdminMode()) return;
      if (this.isActive()) {
        Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_GENERIC,
                "Dialog closed: EfaFrame is already active.");
        return;
      }
      Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_GENERIC,
              "Dialog closed: EfaFrame is inactive and will be brought to front.");
    }
    (new EfaFrameBringToFrontThread(this,100)).start();
  }

  private void hideEfaFrame() {
    this.hide();
    Dialog.frameClosed(this);
  }

  private void setFieldEnabled(boolean enable, JTextField field, JLabel label) {
    try {
      if (UIManager.getLookAndFeel().getName().equals("Metal")) {
        field.setDisabledTextColor(Color.darkGray);
        field.setBackground( (enable ? this.mannsch[0].getBackground() : this.getBackground()) );
      }
    } catch(Exception e) {
      EfaUtil.foo();
    }
    field.setEnabled(enable);
    if (label != null) {
      label.setForeground( (enable ? artDerFahrtLabel.getForeground() : Color.gray) ); // mannsch1_label als Referenzfarbe
    }
  }

  private void setFieldEnabledBootsKm() {
    if (mode != MODE_ENDE && mode != MODE_NACHTRAG) {
        return; // Zielabhängiges Enabled der BootsKm nur bei "Fahrt beenden" und "Nachtrag"
    }
    boolean enabled = !(zielButton.getBackground() == Color.green) ||
            Daten.efaConfig == null || !Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen;
    setFieldEnabled(enabled,bootskm,bootskmLabel);
  }

  private void setFieldEnabledStmUndMannsch(DatenFelder d) {
    boolean isstm = true;
    int anz = Fahrtenbuch.ANZ_MANNSCH;
    if (d != null) {
      isstm = d.get(Boote.STM).equals(EfaTypes.TYPE_COXING_COXED);
      anz = EfaUtil.string2date(d.get(Boote.ANZAHL),Fahrtenbuch.ANZ_MANNSCH,0,0).tag;
    }
    // Steuermann wird bei steuermannslosen Booten immer disabled (unabhängig von Konfigurationseinstellung)
    setFieldEnabled(isstm,stm,stmLabel);
    if (!isstm) {
      stm.setText("");
      if (getObmann() == 0) setObmann(-1,true);
    }
    if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl) {
      for (int i=1; i<=Fahrtenbuch.ANZ_MANNSCH; i++) {
        JLabel label = null;
        if (i > (mannschAuswahl*8) && i <= ((mannschAuswahl+1)*8)) {
          switch(i % 8) {
            case 1: label = mannsch1_label; break;
            case 2: label = mannsch2_label; break;
            case 3: label = mannsch3_label; break;
            case 4: label = mannsch4_label; break;
            case 5: label = mannsch5_label; break;
            case 6: label = mannsch6_label; break;
            case 7: label = mannsch7_label; break;
            case 0: label = mannsch8_label; break;
          }
        }
        setFieldEnabled(i<=anz,mannsch[i-1],label);
        if (i > anz) {
          mannsch[i-1].setText("");
          if (getObmann() == i) setObmann(-1,true);
        }
      }
    }
  }

  private void setTime(JTextField field, int add) {
    Calendar cal = new GregorianCalendar();
    int h = cal.get(Calendar.HOUR_OF_DAY);
    int m = cal.get(Calendar.MINUTE);
    m+=add;
    if (add != 0 && m % 5 != 0) {
      if (m % 5 < 3) m -= m % 5;
      else m += (5 - m % 5);
    }
    if (m>59) { h++; m-=60; if (h>23) { h=23; m=59; } }
    if (m< 0) { h--; m+=60; if (h< 0) { h= 0; m= 0; } }
    field.setText( (h<10?"0":"") + h + ":" + (m<10?"0":"") + m);
    field.select(0,field.getText().length());
  }

  public void direktFahrtAnfang(String boot) {
    this.direkt_boot = boot;
    Component focusComponent = null;
    this.mode = MODE_START;
    this.setTitle(International.getString("Neue Fahrt beginnen"));
    SetBlankFields();
    if (this.lfdnr.getText().trim().length()==0) this.lfdnr.setText("1");

    this.refDate=""; datumSetText(""); // damit aktuelles Datum im Datums-Feld erscheint...
    setDateFromRefDate();
    if (boot != null) this.boot.setText(boot);
    vervollstaendige(this.boot,bootButton,Daten.fahrtenbuch.getDaten().boote,null,this,false);
    setTime(abfahrt,Daten.efaConfig.efaDirekt_plusMinutenAbfahrt);
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Fahrt beginnen"));

    setFieldEnabled(false,lfdnr,lfdnrLabel);
    setFieldEnabled(true ,datum,datumLabel);
    setFieldEnabled(boot == null,this.boot,bootLabel);
    setFieldEnabled(true,stm,stmLabel);
    if (Daten.efaConfig.efaDirekt_eintragNichtAenderbarUhrzeit) {
      setFieldEnabled(false,abfahrt,abfahrtLabel);
      setFieldEnabled(false,ankunft,ankunftLabel);
    } else {
      setFieldEnabled(true ,abfahrt,abfahrtLabel);
      setFieldEnabled(false,ankunft,ankunftLabel);
    }
    setFieldEnabled(true ,ziel,zielLabel);
    setFieldEnabled(false,bootskm,bootskmLabel);
    setFieldEnabled(false,mannschkm,mannschkmLabel);
    setFieldEnabled(true ,bemerk,bemerkLabel);
    if (boot != null) {
      DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot);
      setFieldEnabledStmUndMannsch(d);
      if (Daten.mannschaften != null && Daten.efaConfig != null && Daten.efaConfig.autoStandardmannsch &&
          boot.length()!=0 &&
          Daten.mannschaften.getExact(boot)!= null) {
        setStandardMannschaft((DatenFelder)Daten.mannschaften.getComplete());
      }
      if (stm.isEnabled()) focusComponent = stm;
      else focusComponent = mannsch[0];
    } else {
      setFieldEnabledStmUndMannsch(null);
      focusComponent = this.boot;
    }
    boot_focusLostGetBoot();
    bootskm.setText(""); // falls durch "Standardmannschaft" bereits ein Fahrtziel (und somit auch die Km) eingetragen wurden
    showEfaFrame(focusComponent);
  }

  public void direktFahrtAnfangKorrektur(String boot, String lfdnr) {
    this.mode = MODE_START_KORREKTUR;
    this.direkt_boot = boot;
    this.setTitle(International.getString("Eintrag für neue Fahrt korrigieren"));
    DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getExactComplete(lfdnr);
    if (d == null) {
      Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              International.getString("Fahrtbeginn") +
              " (" + International.getString("Korrektur") + "): " +
              International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!", lfdnr, boot));
      Dialog.error(International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!", lfdnr, boot));
      Dialog.frameOpened(this); // Bugfix: Damit es nicht zur Stack-Inkonsistenz kommt!
      cancel();
      return;
    }
    SetFields(d);
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Fahrt beginnen") +
            " (" + International.getString("Korrektur") +")");
    
    setFieldEnabled(false,this.lfdnr,lfdnrLabel);
    setFieldEnabled(true ,datum,datumLabel);
    setFieldEnabled(true ,this.boot,bootLabel);
    if (Daten.efaConfig.efaDirekt_eintragNichtAenderbarUhrzeit) {
      setFieldEnabled(false,abfahrt,abfahrtLabel);
      setFieldEnabled(false,ankunft,ankunftLabel);
    } else {
      setFieldEnabled(true ,abfahrt,abfahrtLabel);
      setFieldEnabled(false,ankunft,ankunftLabel);
    }
    setFieldEnabled(true ,ziel,zielLabel);
    setFieldEnabled(false,bootskm,bootskmLabel);
    setFieldEnabled(false,mannschkm,mannschkmLabel);
    setFieldEnabled(true ,bemerk,bemerkLabel);

    if (boot != null) {
      DatenFelder db = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot);
      setFieldEnabledStmUndMannsch(db);
    } else setFieldEnabledStmUndMannsch(null);

    bootskm.setText(""); // falls durch "Standardmannschaft" bereits ein Fahrtziel (und somit auch die Km) eingetragen wurden
    mannschkm.setText("");
    showEfaFrame(datum);
  }

  public void direktFahrtEnde(String boot, String lfdnr) {
    this.mode = MODE_ENDE;
    this.direkt_boot = boot;
    this.setTitle(International.getString("Fahrt abschließen"));
    DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getExactComplete(lfdnr);
    if (d == null) {
      Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              International.getString("Fahrtende") + ": " +
              International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!", lfdnr, boot));
      Dialog.error(International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!", lfdnr, boot));
      efaDirektFrame.fahrtBeendet(boot,true);
      Dialog.frameOpened(this); // Bugfix: Damit es nicht zur Stack-Inkonsistenz kommt!
      cancel();
      return;
    }
    SetFields(d);
    setTime(ankunft,-Daten.efaConfig.efaDirekt_minusMinutenAnkunft);
    if (d.get(Fahrtenbuch.BOOTSKM).equals("0")) bootskm.setText("");
    if (d.get(Fahrtenbuch.ZIEL).length()>0 && bootskm.getText().length() == 0) {
      DatenFelder ziel = Daten.fahrtenbuch.getDaten().ziele.getExactComplete(d.get(Fahrtenbuch.ZIEL));
      if (ziel != null) {
        bootskm.setText(ziel.get(Ziele.KM));
      }
    }

    mannschkm.setText("");
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Fahrt abschließen"));

    setFieldEnabled(false,this.lfdnr,lfdnrLabel);
    setFieldEnabled(false,datum,datumLabel);
    setFieldEnabled(false,this.boot,bootLabel);
    if (Daten.efaConfig.efaDirekt_eintragNichtAenderbarUhrzeit) {
      setFieldEnabled(false,abfahrt,abfahrtLabel);
      setFieldEnabled(false,ankunft,ankunftLabel);
    } else {
      setFieldEnabled(true ,abfahrt,abfahrtLabel);
      setFieldEnabled(true ,ankunft,ankunftLabel);
    }
    setFieldEnabled(true ,ziel,zielLabel);
    setFieldEnabledBootsKm();
    setFieldEnabled(false,mannschkm,mannschkmLabel);
    setFieldEnabled(true ,bemerk,bemerkLabel);

    if (boot != null) {
      DatenFelder db = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot);
      setFieldEnabledStmUndMannsch(db);
    } else setFieldEnabledStmUndMannsch(null);

    showEfaFrame(ziel);
  }

  public void direktFahrtNachtrag(String bootPreselected) {
    this.mode = MODE_NACHTRAG;
    this.direkt_boot = bootPreselected;
    this.setTitle(International.getString("Nachtrag"));
    SetBlankFields();
    if (this.lfdnr.getText().trim().length()==0) this.lfdnr.setText("1");

    this.refDate=""; datumSetText(""); // damit aktuelles Datum im Datums-Feld erscheint...
    setDateFromRefDate();
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Nachtrag speichern"));

    setFieldEnabled(false,lfdnr,lfdnrLabel);
    setFieldEnabled(true ,datum,datumLabel);
    setFieldEnabled(true ,boot,bootLabel);
    setFieldEnabledStmUndMannsch(null);
    setFieldEnabled(true ,abfahrt,abfahrtLabel);
    setFieldEnabled(true ,ankunft,ankunftLabel);
    setFieldEnabled(true ,ziel,zielLabel);
    setFieldEnabled(true ,bootskm,bootskmLabel);
    setFieldEnabled(false ,mannschkm,mannschkmLabel);
    setFieldEnabled(true ,bemerk,bemerkLabel);
    if (bootPreselected != null) {
      boot.setText(bootPreselected);
      vervollstaendige(boot,bootButton,Daten.fahrtenbuch.getDaten().boote,null,this,false);
    }
    showEfaFrame(datum);
  }

  public String stmMannsch2String() {
    String s;
    String pers = ( (s = stm.getText().trim())       == null || s.length()==0 ? "" : s+"; " );
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) if ( (s = mannsch[i].getText().trim()).length()>0 ) pers += s+"; ";
    if (pers.length()>2) pers = pers.substring(0,pers.length()-2);
    return pers;
  }

  private boolean unbekannterName(JTextField field, DatenListe daten) {
    return (field.getText().trim().length()>0 && daten != null &&
        daten.getExactComplete(field.getText().trim()) == null);
  }

  void direktSpeichereDatensatz() {
    String direktBootOriginalName = ""; // Name des Bootes, welches bei efaDirekt ursprünglich übergeben wurde (um Änderungen des Bootsnamens bei Korrektur der Fahrt festzustellen)
    String mtourName = null; // Bei Mehrtagesfahrten der Name der Mehrtagesfahrt
    if (aktDatensatz != null) direktBootOriginalName = aktDatensatz.get(Fahrtenbuch.BOOT);

    if (boot.getText().trim().length()==0) {
      if (mode == MODE_NACHTRAG) {
        if (Dialog.yesNoDialog(International.getString("Kein Bootsname angegeben"),
                               International.getString("Du hast keinen Bootsnamen angegeben.\nMöchtest Du das Feld wirklich leer lassen?")) != Dialog.YES) {
          boot.requestFocus();
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return;
        }
      } else {
        Dialog.error(International.getString("Bitte gib einen Bootsnamen ein!"));
        boot.requestFocus();
        return;
      }
    }
    abfahrt_focusLost(null); ankunft_focusLost(null);

    String pers = stmMannsch2String();
    if (pers.length()==0) {
      Dialog.error(International.getString("Bitte trage mindestens eine Person ein!"));
      stm.requestFocus();
      return;
    }

    // falls noch nicht geschehen, ggf. automatisch Obmann auswählen
    if (Daten.efaConfig.autoObmann && getObmann() < 0) {
      autoSetObmann();
    }

    // Obmann-Auswahl (Autokorrektur, neu in 1.7.1)
    if (getObmann() == 0 && stm.getText().trim().length() == 0 && mannsch[0].getText().trim().length() > 0) setObmann(1,true);
    if (getObmann() > 0 && mannsch[getObmann()-1].getText().trim().length() == 0 && stm.getText().trim().length() > 0) setObmann(0,true);
    if (getObmann() > 0 && mannsch[getObmann()-1].getText().trim().length() == 0 && mannsch[0].getText().trim().length() > 0) setObmann(1,true);
    // Obmann-Check
    if ( (getObmann() == 0 && stm.getText().trim().length()==0) ||
         (getObmann()  > 0 && mannsch[getObmann()-1].getText().trim().length()==0) ) {
      Dialog.error(International.getString("Bitte wähle als Obmann eine Person aus, die tatsächlich im Boot sitzt!"));
      obmann.requestFocus();
      return;
    }

    if (Daten.efaConfig.efaDirekt_eintragErzwingeObmann && getObmann() < 0) {
      Dialog.error(International.getString("Bitte wähle einen Obmann aus!"));
      obmann.requestFocus();
      return;
    }

    // Prüfen, ob ggf. nur bekannte Boote/Ruderer/Ziele eingetragen wurden
    if (Daten.efaConfig.efaDirekt_eintragNurBekannteBoote && unbekannterName(boot,Daten.fahrtenbuch.getDaten().boote)) {
      Dialog.error(International.getMessage("Das Boot '{bootsname}' ist unbekannt. Bitte trage ein bekanntes Boot ein!", boot.getText().trim()));
      boot.requestFocus();
      return;
    }
    if (Daten.efaConfig.efaDirekt_eintragNurBekannteRuderer && unbekannterName(stm,Daten.fahrtenbuch.getDaten().mitglieder)) {
      Dialog.error(International.getMessage("Person '{name}' ist unbekannt. Bitte trage eine bekannte Person ein!", stm.getText().trim()));
      stm.requestFocus();
      return;
    }
    for (int i=0; i<mannsch.length; i++) {
      if (Daten.efaConfig.efaDirekt_eintragNurBekannteRuderer && unbekannterName(mannsch[i],Daten.fahrtenbuch.getDaten().mitglieder)) {
        Dialog.error(International.getMessage("Person '{name}' ist unbekannt. Bitte trage eine bekannte Person ein!", mannsch[i].getText().trim()));
        mannsch[i].requestFocus();
        return;
      }
    }
    if (Daten.efaConfig.efaDirekt_eintragNurBekannteZiele && unbekannterName(ziel,Daten.fahrtenbuch.getDaten().ziele)) {
      Dialog.error(International.getMessage("Das Ziel '{ziel}' ist unbekannt. Bitte trage ein bekanntes Ziel ein!", ziel.getText().trim()));
      ziel.requestFocus();
      return;
    }

    // ***Ersetzt durch Konfigurieren der Mehrtagesfahrt direkt durch das Mitglied***
    // ***Folgende beide Variablen werden nicht mehr genutzt und bleiben auf "null" initialisiert***
    String enddatum=null,rudertage=null;

    if (mode == MODE_START || mode == MODE_START_KORREKTUR) {
      if (ziel.getText().trim().length()==0 && Daten.efaConfig.efaDirekt_zielBeiFahrtbeginnPflicht) {
        Dialog.error(International.getString("Bitte trage ein voraussichtliches Fahrtziel ein!"));
        ziel.requestFocus();
        return;
      }

      // Auf nicht erlaubte Ruderer prüfen
      DatenFelder b = null;
      if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().boote != null) b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot.getText().trim());
      if (b != null && b.get(Boote.GRUPPEN).length() > 0 && Daten.gruppen != null) {
        String nichtErlaubt = null;
        int nichtErlaubtAnz = 0;
        Vector g = Boote.getGruppen(b);
        for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
          String m = mannsch[i].getText().trim();
          if (m.length() > 0) {
            boolean inAnyGroup = false;
            for (int j=0; j<g.size(); j++) {
              if (Daten.gruppen.isInGroup((String)g.get(j),EfaUtil.getVorname(m),EfaUtil.getNachname(m),EfaUtil.getVerein(m))) inAnyGroup = true;
            }
            if (!inAnyGroup) {
              nichtErlaubt = (nichtErlaubt == null ? m : nichtErlaubt + "\n" + m);
              nichtErlaubtAnz++;
            }
          }
        }
        if (nichtErlaubtAnz > EfaUtil.string2int(b.get(Boote.MAX_NICHT_IN_GRUPPE),0)) {
          String erlaubteGruppen = null;
          for (int j=0; j<g.size(); j++) {
            erlaubteGruppen = (erlaubteGruppen == null ? (String)g.get(j) : erlaubteGruppen + (j+1<g.size() ? ", "+g.get(j) : " " +
                    International.getString("und") + " " + g.get(j)) ); // @todo: for some languages it might be necessary to translate ", " as well, or even use a ChoiceFormat here
          }
          switch (Dialog.auswahlDialog(International.getString("Boot nur für bestimmte Gruppen freigegeben"),
                                 International.getMessage("Dieses Boot dürfen nur {list_of_valid_groups} rudern.",erlaubteGruppen) + "\n" +
                                 International.getMessage("{nichtErlaubtAnz,choice,1#Folgender Ruderer gehört diesen Gruppen nicht an und darf das Boot daher nicht rudern:|2#Folgende Ruderer gehören diesen Gruppen nicht an und dürfen das Boot daher nicht rudern:}",
                                 nichtErlaubtAnz) + " \n" + nichtErlaubt + "\n" +
                                 International.getString("Was möchtest Du tun?"),
                                 International.getString("Anderes Boot wählen"),
                                 International.getString("Mannschaft ändern"),
                                 International.getString("Trotzdem rudern"),
                                 International.getString("Eintrag abbrechen"))) {
            case 0:
              this.setFieldEnabled(true,this.boot,this.bootLabel);
              this.boot.setText("");
              this.boot.requestFocus();
              return;
            case 1:
              this.mannsch[0].requestFocus();
              return;
            case 2:
              Logger.log(Logger.INFO, Logger.MSG_EVT_UNALLOWEDBOATUSAGE,
                      International.getString("Unerlaubte Benutzung eines Bootes") + ": " +
                      International.getString("LfdNr") + "=" + lfdnr.getText().trim() + " " +
                      International.getString("Boot") + "=" + boot.getText().trim() + " " +
                      International.getString("Mannschaft") + "=" + pers);
              break;
            case 3:
              cancel();
              return;
          }
        }
      }

      // Prüfen, ob mind 1 Ruderer (oder Stm) der Gruppe "mind 1 aus Gruppe" im Boot sitzt
      if (b != null && b.get(Boote.MIND_1_IN_GRUPPE).length() > 0 && Daten.gruppen != null) {
        String gruppe = b.get(Boote.MIND_1_IN_GRUPPE);
        int gefunden = 0;

        String m = stm.getText().trim();
        if (m.length()>0 && Daten.gruppen.isInGroup(gruppe,EfaUtil.getVorname(m),EfaUtil.getNachname(m),EfaUtil.getVerein(m))) gefunden++;
        for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
          m = mannsch[i].getText().trim();
          if (m.length()>0 && Daten.gruppen.isInGroup(gruppe,EfaUtil.getVorname(m),EfaUtil.getNachname(m),EfaUtil.getVerein(m))) gefunden++;
        }

        if (gefunden == 0) {
          switch (Dialog.auswahlDialog(International.getString("Boot erfordert bestimmte Berechtigung"),
                                 International.getMessage("In diesem Boot muß mindestens ein Mitglied der Gruppe {groupname} sitzen.\n",gruppe)+
                                 International.getString("Was möchtest Du tun?"),
                                 International.getString("Anderes Boot wählen"),
                                 International.getString("Mannschaft ändern"),
                                 International.getString("Trotzdem rudern"),
                                 International.getString("Eintrag abbrechen"))){
            case 0:
              this.setFieldEnabled(true,this.boot,this.bootLabel);
              this.boot.setText("");
              this.boot.requestFocus();
              return;
            case 1:
              this.mannsch[0].requestFocus();
              return;
            case 2:
              Logger.log(Logger.INFO, Logger.MSG_EVT_UNALLOWEDBOATUSAGE,
                      International.getString("Unerlaubte Benutzung eines Bootes") + ": " +
                                "#" + lfdnr.getText().trim() + " - " + boot.getText().trim() + " " +
                                International.getMessage("mit {crew}",pers));
              break;
            case 3:
              cancel();
              return;
          }
        }
      }


      if (mode == MODE_START) {
          Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPSTART,
                  International.getString("Fahrtbeginn") + ": " +
                                "#" + lfdnr.getText().trim() + " - " + boot.getText().trim() + " " +
                                International.getMessage("mit {crew}",pers));
      } else {
          Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPSTART_CORR,
                  International.getString("Fahrtbeginn korrigiert") + ": " +
                                "#" + lfdnr.getText().trim() + " - " + boot.getText().trim() + " " +
                                International.getMessage("mit {crew}",pers));
      }
      this.bootskm.setText(""); this.mannschkm.setText(""); this.ankunft.setText(""); // wenn alle diese Werte "" sind, gilt der Eintrag als noch nicht zurückgetragen
      speichereDatensatzInFB(true,null,null,null);
      if (!Daten.fahrtenbuch.writeFileOnlyLastRecordChanged()) {
          LogString.logError_fileWritingFailed(Daten.fahrtenbuch.getFileName(), International.getString("Fahrtenbuch"));
      }
      if (mode == MODE_START) {
        efaDirektFrame.fahrtBegonnen(
                boot.getText().trim(),
                lfdnr.getText().trim(),
                datum.getText().trim(),
                abfahrt.getText().trim(),
                pers,
                createFahrtartKey((String)fahrtDauer.getSelectedItem()),
                ziel.getText().trim());
      } else {
        // Fahrtbeginn korrigiert
        if (direktBootOriginalName.equals(boot.getText().trim())) {
          // Bootsname nicht geändert
          efaDirektFrame.fahrtBeginnKorrigiert(
                  direkt_boot,
                  lfdnr.getText().trim(),
                  datum.getText().trim(),
                  abfahrt.getText().trim(),
                  pers,
                  createFahrtartKey((String)fahrtDauer.getSelectedItem()),
                  ziel.getText().trim(),
                  direkt_boot);
        } else {
          // Bootsname wurde geändert
          efaDirektFrame.fahrtBeginnKorrigiert(
                  boot.getText().trim(),
                  lfdnr.getText().trim(),
                  datum.getText().trim(),
                  abfahrt.getText().trim(),
                  pers,
                  createFahrtartKey((String)fahrtDauer.getSelectedItem()),
                  ziel.getText().trim(),
                  direkt_boot);
        }
      }
    } else { // MODE_ENDE oder MODE_NACHTRAG
      if (ziel.getText().trim().length()==0 && !Daten.efaConfig.skipZiel) {
        Dialog.error(International.getString("Bitte trage ein Fahrtziel ein!"));
        ziel.requestFocus();
        return;
      }
      String km = bootskm.getText().trim();
      if (km.length()==0 || EfaUtil.zehntelString2Int(km)==0) {
        Dialog.error(International.getString("Bitte trage die geruderten Kilometer ein!"));
        bootskm.requestFocus();
        return;
      }

      // Mehrtagesfahrt?
      if (Daten.efaTypes != null && Daten.efaTypes.isConfigured(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_MULTIDAY) &&
              EfaTypes.TYPE_TRIP_MULTIDAY.equals(Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_TRIP, fahrtDauer.getSelectedItem().toString()))) {
        mtourName = WanderfahrtSelectFrame.selectWanderfahrt(this,lfdnr.getText().trim(),datum.getText().trim(),boot.getText().trim(),stmMannsch2String(),ziel.getText().trim());
        if (mtourName == null) return;
        Mehrtagesfahrt mf = Daten.fahrtenbuch.getMehrtagesfahrt(mtourName);
        if (mf != null && mf.start != null && mf.start.length()>0) this.datum.setText(mf.start);
      }


      if (mode == MODE_ENDE) {
        Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPEND,
                International.getString("Fahrtende") + ": " +
                                "#" + lfdnr.getText().trim() + " - " + boot.getText().trim() + " " +
                                International.getMessage("mit {crew}",pers) + "; " +
                                International.getString("Abfahrt") + ": " + abfahrt.getText().trim() + ", " +
                                International.getString("Ankunft") + ": " + ankunft.getText().trim() + "; " +
                                International.getString("Ziel") + ": " + ziel.getText().trim() + "; " +
                                International.getString("Km") + ": " + bootskm.getText().trim());
        Daten.fahrtenbuch.delete(this.lfdnr.getText().trim());
        speichereDatensatzInFB(false,enddatum,rudertage,mtourName);
        if (!Daten.fahrtenbuch.writeFile()) {
          LogString.logError_fileWritingFailed(Daten.fahrtenbuch.getFileName(), International.getString("Fahrtenbuch"));
        }
        efaDirektFrame.fahrtBeendet(direkt_boot,true);
      } else if (mode == MODE_NACHTRAG) {
        Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPLATEREC,
                International.getString("Nachtrag") + ": " +
                                "#" + lfdnr.getText().trim() + " - " + boot.getText().trim() + " " +
                                International.getMessage("mit {crew}",pers) + "; " +
                                International.getString("Abfahrt") + ": " + abfahrt.getText().trim() + ", " +
                                International.getString("Ankunft") + ": " + ankunft.getText().trim() + "; " +
                                International.getString("Ziel") + ": " + ziel.getText().trim() + "; " +
                                International.getString("Km") + ": " + bootskm.getText().trim());
        speichereDatensatzInFB(true,enddatum,rudertage,mtourName);
        // wir müssen hier writeFile() verwenden anstatt writeFileOnlyLastRecordChanged(),
        // da bei einem Nachtrag (ebenso wie beim Beenden von Fahrten) u.U. auch die Liste der Mehrtagesfahrten
        // am Anfang der Datei mit verändert wird.
        if (!Daten.fahrtenbuch.writeFile()) {
          LogString.logError_fileWritingFailed(Daten.fahrtenbuch.getFileName(), International.getString("Fahrtenbuch"));
        }
        efaDirektFrame.fahrtNachgetragen();
      } else {
        Logger.log(Logger.ERROR, Logger.MSG_ERR_UNEXPECTED,
                International.getString("Programmfehler") +
                ": Unexpected Mode ["+mode+"] for direktSpeichereDatensatz()!");
      }
    }

    cancel();
  }


  void showHint(String s) {
    if (s == null) {
      infoLabel.setText("");
      return;
    }
    if (s.equals("datum")) infoLabel.setText(International.getString("Bitte eingeben")+": "+
            "<" + International.getString("Tag") + ">.<" +
            International.getString("Monat") + ">.<" +
            International.getString("Jahr") + ">");
    if (s.equals("boot")) infoLabel.setText(International.getString("Bitte eingeben")+": "+
            "<" + International.getString("Bootsname") + ">");
    if (s.equals("stm") || s.equals("mannsch")) infoLabel.setText(International.getString("Bitte eingeben")+": "+
       (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().erstVorname ? 
	   "<" + International.getString("Vorname") + "> <" +
           International.getString("Nachname") + ">":
           "<" + International.getString("Nachname") + ">,  <" +
           International.getString("Vorname") + ">"));
    if (s.equals("abfahrt") || s.equals("ankunft")) infoLabel.setText(International.getString("Bitte eingeben")+": "+
            "<" + International.getString("Stunde") + ">:<" +
            International.getString("Minute") + ">");
    if (s.equals("ziel")) infoLabel.setText(International.getString("Bitte eingeben")+": "+
            "<" + International.getString("Ziel der Fahrt") + ">");
    if (s.equals("bootskm")) infoLabel.setText(International.getString("Bitte eingeben")+": "+
            "<" + International.getString("Kilometer") + ">");
    if (s.equals("bemerk")) infoLabel.setText(International.getString("Bemerkungen eingeben oder frei lassen"));
    if (s.equals("obmann")) infoLabel.setText(International.getString("Bitte auswählen") + 
            ": " + International.getString("verantwortlichen Obmann"));
    if (s.equals("fahrtDauer")) infoLabel.setText(International.getString("Bitte auswählen") +
            ": " + International.getString("Art der Fahrt"));
    if (s.equals("weitereMannschButton")) infoLabel.setText(International.getString("weitere Mannschaftsfelder anzeigen"));
    if (s.equals("bootsschadenButton")) infoLabel.setText(International.getString("einen Schaden am Boot melden"));
    if (s.equals("addButton")) infoLabel.setText(International.getString("<Leertaste> drücken, um den Eintrag abzuschließen"));
  }

  void bootsschadenButton_actionPerformed(ActionEvent e) {
    if (efaDirektFrame == null || isAdminMode()) return;

    String boot = this.boot.getText().trim();
    NachrichtAnAdminFrame dlg = new NachrichtAnAdminFrame(this,Daten.nachrichten,Nachricht.BOOTSWART,
                      (getObmannTextField(getObmann()) != null ? getObmannTextField(getObmann()).getText() : null),
                      International.getString("Bootsschaden"),
                      International.getString("LfdNr")+": "+lfdnr.getText()+"\n"+
                      International.getString("Datum")+": "+datum.getText()+"\n"+
                      International.getString("Boot")+": "+boot+"\n"+
                      International.getString("Mannschaft")+": "+stmMannsch2String()+"\n"+
                      "-----------------------\n"+
                      International.getString("Beschreibung des Schadens")+":\n");
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    if (dlg.isGesendet()) {
      String s = this.bemerk.getText().trim();
      this.bemerk.setText(s + (s.length()>0 ? "; " : "") + International.getString("Bootsschaden gemeldet") + ".");
      Nachricht n = dlg.getLastMessage();
      if (n != null && n.nachricht != null) {
        String t = "";
        int pos = n.nachricht.indexOf(International.getString("Beschreibung des Schadens")+":");
        if (pos >= 0) {
          t = n.nachricht.substring(pos+(International.getString("Beschreibung des Schadens")+":").length());
        }
        if (t.length() == 0) {
            t = International.getString("Bootsschaden"); // generic
        }
        t = EfaUtil.replace(t,"\n"," ",true).trim();
        efaDirektFrame.setBootstatusSchaden(boot,t);
      }
    }
  }

  private void _colorize(FocusEvent e, Color col) {
    if (e == null) return;
    if (!isDirectMode()) return;
    if (Daten.efaConfig == null || !Daten.efaConfig.efaDirekt_colorizeInputField) return;
    Component c = e.getComponent();
    if (c == null) return;
    if (col == null) col = Color.white;
    c.setBackground(col);
  }

  void colorize(FocusEvent e) {
    if (e == null || e.getComponent() == null) return;
    defaultColor.put(e.getComponent(),e.getComponent().getBackground());
    _colorize(e,Color.yellow);
  }

  void decolorize(FocusEvent e) {
    if (e == null || e.getComponent() == null) return;
    _colorize(e,(Color)defaultColor.get(e.getComponent()));
  }

}
