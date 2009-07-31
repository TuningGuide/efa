package de.nmichael.efa;

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

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  HauptFrame von efa
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */


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
  int mannschAuswahl = 0;       // 0: 1-8 sichtbar; 1: 9-16 sichtbar; 2: 17-24 sichtbar
  boolean continueMTour;        // legt fest, ob nächster neuer Eintrag mit unverändertem MTour-Feld (d.h. gleiches Element ausgewählt) begonnen werden soll
  String refDate="";            // Referenzdatum
  boolean startEfaTour=false;   // nach dem Aufbau des Frames die efa-Tour starten
  boolean askForOpenNewFb=false;// fragen, ob ein neues FB angelegt werden soll (nur beim ersten Start)
  String startOpenFb=null;      // Fahrtenbuch, das beim Starten von efa geöffnet werden soll (per -fb <name> angegeben)
  String neueMehrtagesfahrt=">>> neue Mehrtagesfahrt"; // String zum Anzeigen im Feld MTOUR
  String mehrtagesfahrtenBearbeiten=null; // String zum Anzeigen im Feld MTOUR
  String mehrtagesfahrtKonfigurieren="Mehrtagesfahrt: konfigurieren!!"; // String zum Anzeigen im Feld MTOUR
  int datumErrorCount=0;        // zum Zählen der Fehler, die beim Setzen des Datums aufgetreten sind
  int currentObmann=-1;         // aktuell ausgewählter Obmann
  boolean openWelcome = false;  // true, beim ersten Start von efa
  int lfdNrForNewEntry = -1;    // LfdNr (zzgl. 1), die für den nächsten per "Neu" erzeugten Datensatz verwendet werden soll; wenn <0, dann wird "last+1" verwendet
  boolean ignoreFahrtDauerItemStateChanges = false; // zum Unterdrücken der StateChanges beim bearbeiten der Liste nach Hinzufügen einer Fahrt
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
  JLabel refDatumLabel = new JLabel();
  JTextField refTag = new JTextField();
  JTextField refMonat = new JTextField();
  JTextField refJahr = new JTextField();
  JLabel refDatumLabel2 = new JLabel();
  JLabel refDatumLabel3 = new JLabel();
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
//    System.out.println("pack(): "+traceInfo);
  }


  /**Construct the frame from Efa */
  public EfaFrame(String fb, boolean openWelcome) {
    this.mode = MODE_FULL;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      this.startOpenFb = fb;
      this.openWelcome = openWelcome;
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
//    this.fahrtDauer.setVisible(false);
//    this.artDerFahrtLabel.setVisible(false);
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.fahrtart != null) {
      for (int i=0; i<Daten.bezeichnungen.fahrtart.size(); i++) {
        fahrtDauer.addItem(Daten.bezeichnungen.fahrtart.get(i));
      }
    }
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
    if (mode == MODE_ADMIN_NUR_FAHRTEN) {
      mehrtagesfahrtenBearbeiten=">>> Mehrtagesfahrten bearbeiten";
    }
    try {
      this.startOpenFb = fb;
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
      Dialog.infoDialog(International.getMessage("Dies ist ein Internationalisierungs-Test für efa {version} von {email}.",Daten.VERSION,Daten.EFAEMAIL));
      Dialog.infoDialog(International.getMessage("Test Text {text} mit Zahl {zahl}.","foobar",123));
    }

    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_8")) { // F12
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
                       new String[] {"ESCAPE","F1","F2","F3","F4","F10","shift F10","F11","F12"},
                       new String[] {"keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    setIconImage(Toolkit.getDefaultToolkit().createImage(EfaFrame.class.getResource("/de/nmichael/efa/img/efa_icon.gif")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    this.setTitle("efa - Elektronisches Fahrtenbuch");
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
      public void windowIconified(WindowEvent e) {
        this_windowIconified(e);
      }
    });

    jMenuFile.setMnemonic('D');
    jMenuFile.setText("Datei");
    jMenuFileOpen.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_open.gif")));
    jMenuFileOpen.setMnemonic('F');
    jMenuFileOpen.setText("Fahrtenbuch öffnen");
    jMenuFileOpen.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuFileOpen_actionPerformed(e);
      }
    });
    jMenuHelp.setMnemonic('I');
    jMenuHelp.setText("Info");
    jMenuHelpAbout.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_about.gif")));
    jMenuHelpAbout.setMnemonic('B');
    jMenuHelpAbout.setText("Über");
    jMenuHelpAbout.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jMenuFileSave.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_save.gif")));
    jMenuFileSave.setMnemonic('S');
    jMenuFileSave.setText("Fahrtenbuch speichern");
    jMenuFileSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSave_actionPerformed(e);
      }
    });
    jMenuFileSaveAs.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_saveas.gif")));
    jMenuFileSaveAs.setMnemonic('U');
    jMenuFileSaveAs.setText("Fahrtenbuch speichern unter");
    jMenuFileSaveAs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveAs_actionPerformed(e);
      }
    });
    jMenuFileExit.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_exit.gif")));
    jMenuFileExit.setMnemonic('B');
    jMenuFileExit.setText("Programm beenden");
    jMenuFileExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuKonfiguration.setMnemonic('A');
    jMenuKonfiguration.setText("Administration");
    jMenuItem4.setMnemonic('F');
    jMenuItem4.setText("Einstellungen zum Fahrtenbuch");
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem4_actionPerformed(e);
      }
    });
    jMenuItem5.setMnemonic('M');
    jMenuItem5.setText("Mitgliederliste");
    jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem5_actionPerformed(e);
      }
    });
    jMenuItem6.setMnemonic('B');
    jMenuItem6.setText("Bootsliste");
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem6_actionPerformed(e);
      }
    });
    jMenuItem7.setMnemonic('Z');
    jMenuItem7.setText("Zielliste");
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem7_actionPerformed(e);
      }
    });
    PrevButton.setMargin(new Insets(3,5,3,5));
    PrevButton.setNextFocusableComponent(NextButton);
    PrevButton.setToolTipText("zum vorherigen Eintrag springen");
    PrevButton.setMnemonic('V');
    PrevButton.setText("<< Vorheriger");
    PrevButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PrevButton_actionPerformed(e);
      }
    });
    NextButton.setMargin(new Insets(3,5,3,5));
    NextButton.setNextFocusableComponent(LastButton);
    NextButton.setToolTipText("zum nächsten Eintrag springen");
    NextButton.setMnemonic('C');
    NextButton.setText("Nächster >>");
    NextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        NextButton_actionPerformed(e);
      }
    });
    NewButton.setMargin(new Insets(3,5,3,5));
    NewButton.setNextFocusableComponent(InsertButton);
    NewButton.setToolTipText("neuen Eintrag erstellen");
    NewButton.setMnemonic('N');
    NewButton.setText("Neu");
    NewButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        NewButton_actionPerformed(e);
      }
    });
    mainPanel.setLayout(gridBagLayout1);
    lfdnrLabel.setDisplayedMnemonic('F');
    lfdnrLabel.setLabelFor(lfdnr);
    lfdnrLabel.setText(International.getString("Lfd. Nr.")+": ");
    datumLabel.setDisplayedMnemonic('0');
    datumLabel.setLabelFor(datum);
    datumLabel.setText(International.getString("Datum")+": ");
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
    bootLabel.setDisplayedMnemonic('0');
    bootLabel.setLabelFor(boot);
    bootLabel.setText(International.getString("Boot")+": ");
    stmLabel.setDisplayedMnemonic('0');
    stmLabel.setLabelFor(stm);
    stmLabel.setText(International.getString("Steuermann")+": ");

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
      mannschButton[i].setToolTipText("Ruderer in die Mitgliederliste aufnehmen");
      mannschButton[i].addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mannschButton_actionPerformed(e);
        }
      });
    }

    mannsch1_label.setDisplayedMnemonic('0');
    mannsch1_label.setLabelFor(mannsch[0]);
    mannsch1_label.setText(International.getString("Mannschaft")+": 1: ");
    mannsch2_label.setDisplayedMnemonic('0');
    mannsch2_label.setLabelFor(mannsch[1]);
    mannsch2_label.setText("2: ");
    mannsch3_label.setDisplayedMnemonic('0');
    mannsch3_label.setLabelFor(mannsch[2]);
    mannsch3_label.setText("3: ");
    mannsch4_label.setDisplayedMnemonic('0');
    mannsch4_label.setHorizontalAlignment(SwingConstants.RIGHT);
    mannsch4_label.setLabelFor(mannsch[3]);
    mannsch4_label.setText("4: ");
    mannsch5_label.setDisplayedMnemonic('0');
    mannsch5_label.setLabelFor(mannsch[4]);
    mannsch5_label.setText("  5: ");
    mannsch6_label.setDisplayedMnemonic('<');
    mannsch6_label.setLabelFor(mannsch[5]);
    mannsch6_label.setText("  6: ");
    mannsch7_label.setDisplayedMnemonic('0');
    mannsch7_label.setLabelFor(mannsch[6]);
    mannsch7_label.setText("  7: ");
    mannsch8_label.setDisplayedMnemonic('0');
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
    abfahrtLabel.setDisplayedMnemonic('0');
    abfahrtLabel.setLabelFor(abfahrt);
    abfahrtLabel.setText(International.getString("Abfahrt")+": ");
    ankunftLabel.setDisplayedMnemonic('0');
    ankunftLabel.setLabelFor(ankunft);
    ankunftLabel.setText(International.getString("Ankunft")+": ");
    zielLabel.setDisplayedMnemonic('0');
    zielLabel.setLabelFor(ziel);
    zielLabel.setText(International.getString("Ziel")+": ");
    bootskmLabel.setDisplayedMnemonic('0');
    bootskmLabel.setLabelFor(bootskm);
    bootskmLabel.setText(International.getString("Boots-Km")+": ");
    mannschkmLabel.setToolTipText("");
    mannschkmLabel.setDisplayedMnemonic('0');
    mannschkmLabel.setLabelFor(mannschkm);
    mannschkmLabel.setText(International.getString("Mannsch.-Km")+": ");
    bemerkLabel.setDisplayedMnemonic('0');
    bemerkLabel.setLabelFor(bemerk);
    bemerkLabel.setText(International.getString("Bemerkungen")+": ");
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
    addButton.setToolTipText("aktuell angezeigten Eintrag zum Fahrtenbuch hinzufügen");
    addButton.setMnemonic('G');
    addButton.setText("Eintrag zum Fahrtenbuch hinzufügen");
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
    FirstButton.setToolTipText("zum ersten Eintrag springen");
    FirstButton.setMnemonic('E');
    FirstButton.setText("Erster");
    FirstButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FirstButton_actionPerformed(e);
      }
    });
    LastButton.setMargin(new Insets(3,5,3,5));
    LastButton.setNextFocusableComponent(NewButton);
    LastButton.setToolTipText("zum letzten Eintrag springen");
    LastButton.setMnemonic('Z');
    LastButton.setText("Letzter");
    LastButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        LastButton_actionPerformed(e);
      }
    });
    jMenuNew.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_new.gif")));
    jMenuNew.setMnemonic('N');
    jMenuNew.setText("Neues Fahrtenbuch erstellen");
    jMenuNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuNew_actionPerformed(e);
      }
    });
    bootButton.setNextFocusableComponent(stm);
    Dialog.setPreferredSize(bootButton,15,11);
//    bootButton.setPreferredSize(new Dimension(15, 11));
    bootButton.setToolTipText("Boot in die Bootsliste aufnehmen");
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
    stmButton.setToolTipText("Steuermann in die Mitgliederliste aufnehmen");
    stmButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stmButton_actionPerformed(e);
      }
    });

    zielButton.setNextFocusableComponent(bootskm);
    Dialog.setPreferredSize(zielButton,15,11);
//    zielButton.setPreferredSize(new Dimension(15, 11));
    zielButton.setToolTipText("Ziele in die Zielliste aufnehmen");
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
    jMenuStatistik.setMnemonic('U');
    jMenuStatistik.setText("Ausgabe");
    jMenuItemKilometerliste.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_stat.gif")));
    jMenuItemKilometerliste.setMnemonic('S');
    jMenuItemKilometerliste.setText("Statistiken und Meldedateien erstellen");
    jMenuItemKilometerliste.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItemKilometerliste_actionPerformed(e);
      }
    });
    refDatumLabel.setToolTipText("Das Referenzdatum dient dazu, unvollständige Angaben im Feld Datum " +
    "zu vervollständigen.");
    refDatumLabel.setDisplayedMnemonic('R');
    refDatumLabel.setLabelFor(refTag);
    refDatumLabel.setText("Referenzdatum: ");
    refMonat.setNextFocusableComponent(refJahr);
    refMonat.setPreferredSize(new Dimension(25, 19));
    refTag.setNextFocusableComponent(refMonat);
    refTag.setPreferredSize(new Dimension(25, 19));
    refJahr.setNextFocusableComponent(lfdnr);
    refJahr.setPreferredSize(new Dimension(40, 19));
    refDatumLabel2.setText(".");
    refDatumLabel3.setText(".");
    SuchButton.setMargin(new Insets(3,5,3,5));
    SuchButton.setNextFocusableComponent(ButtonDelete);
    SuchButton.setToolTipText("nach einem Eintrag suchen");
    SuchButton.setMnemonic('S');
    SuchButton.setText("Suchen");
    SuchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SuchButton_actionPerformed(e);
      }
    });
    jLabel22.setText("     ");
    ButtonDelete.setMargin(new Insets(3,5,3,5));
    ButtonDelete.setNextFocusableComponent(refTag);
    ButtonDelete.setToolTipText("aktuellen Eintrag löschen");
    ButtonDelete.setMnemonic('L');
    ButtonDelete.setText("Löschen");
    ButtonDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ButtonDelete_actionPerformed(e);
      }
    });
    jLabel23.setText("     ");
    Dialog.setPreferredSize(fahrtDauer,200,24);
//    fahrtDauer.setPreferredSize(new Dimension(200, 24));
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
    artDerFahrtLabel.setDisplayedMnemonic('T');
    artDerFahrtLabel.setLabelFor(fahrtDauer);
    artDerFahrtLabel.setText("Art der Fahrt:");
    Dialog.setPreferredSize(weitereMannschButton,200,19);
//    weitereMannschButton.setPreferredSize(new Dimension(200, Dialog.TEXTFIELD_HEIGHT));
    weitereMannschButton.setToolTipText("weitere Mannschaftsfelder anzeigen");
    weitereMannschButton.setMnemonic('M');
    weitereMannschButton.setText("restliche Mannschaft anzeigen");
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
    mannschaftSelectButton.setText("Mannschaft auswählen");
    mannschaftSelectButton.setMnemonic('W');
    mannschaftSelectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mannschaftSelectButton_actionPerformed(e);
      }
    });

    contentPane.setMinimumSize(new Dimension(590, 350));
    jMenuItem2.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_efawett.gif")));
    jMenuItem2.setMnemonic('W');
    jMenuItem2.setText("Elektronische Wettbewerbsmeldung");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem2_actionPerformed(e);
      }
    });
    jMenuImport.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_import.gif")));
    jMenuImport.setMnemonic('I');
    jMenuImport.setText("Fahrtenbuch importieren");
    jMenuImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuImport_actionPerformed(e);
      }
    });
    jMenuItem1.setMnemonic('E');
    jMenuItem1.setText("Mehrtagesfahrten");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });
    gehezuLabel.setDisplayedMnemonic('H');
    gehezuLabel.setLabelFor(geheZu);
    gehezuLabel.setText("gehe zu: ");
    Dialog.setPreferredSize(geheZu,50,19);
//    geheZu.setPreferredSize(new Dimension(50, Dialog.TEXTFIELD_HEIGHT));
    geheZu.setToolTipText("direkt zu einer Laufenden Nummer springen");
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
    jMenuHilfeJavaKonsole.setMnemonic('K');
    jMenuHilfeJavaKonsole.setText("Java-Konsole");
    jMenuHilfeJavaKonsole.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHilfeJavaKonsole_actionPerformed(e);
      }
    });
    jMenuFahrtenbuch.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_fbadmin.gif")));
    jMenuFahrtenbuch.setMnemonic('F');
    jMenuFahrtenbuch.setText("Fahrtenbuch");
    jMenuItem3.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_einst.gif")));
    jMenuItem3.setMnemonic('A');
    jMenuItem3.setText("Allgemeine Einstellungen");
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem3_actionPerformed(e);
      }
    });
    wotag.setForeground(Color.black);
    wotag.setToolTipText("");
    wotag.setText("-");
    jMenuDokumentation.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_doc.gif")));
    jMenuDokumentation.setMnemonic('D');
    jMenuDokumentation.setText("Dokumentation");
    jMenuDokumentation.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuDokumentation_actionPerformed(e);
      }
    });
    jMenu_efaHomepage.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_www.gif")));
    jMenu_efaHomepage.setMnemonic('H');
    jMenu_efaHomepage.setText("efa Homepage");
    jMenu_efaHomepage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenu_efaHomepage_actionPerformed(e);
      }
    });
    jMenu_startTour.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_tour.gif")));
    jMenu_startTour.setMnemonic('T');
    jMenu_startTour.setText("Tour");
    jMenu_startTour.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenu_startTour_actionPerformed(e);
      }
    });
    jMenu_Willkommen.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_welcome.gif")));
    jMenu_Willkommen.setMnemonic('W');
    jMenu_Willkommen.setText("Willkommen");
    jMenu_Willkommen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenu_Willkommen_actionPerformed(e);
      }
    });
    jMenu1.setMnemonic('S');
    jMenu1.setText("Synonymlisten");
    jMenu1.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_syn.gif")));
    jMenuItem8.setMnemonic('M');
    jMenuItem8.setText("Mitglieder");
    jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem8_actionPerformed(e);
      }
    });
    jMenuBackup.setText("Backups einspielen");
    jMenuBackup.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_backup.gif")));
    jMenuBackup.setMnemonic('A');
    jMenuBackup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuBackup_actionPerformed(e);
      }
    });
    jMenuItem9.setMnemonic('B');
    jMenuItem9.setText("Boote");
    jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem9_actionPerformed(e);
      }
    });
    jMenuItem10.setMnemonic('Z');
    jMenuItem10.setText("Ziele");
    jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem10_actionPerformed(e);
      }
    });
    jLabel5.setText("     ");
    InsertButton.setNextFocusableComponent(SuchButton);
    InsertButton.setToolTipText("neuen Eintrag vor dem aktuellen einfügen");
    InsertButton.setMargin(new Insets(3,5,3,5));
    InsertButton.setText("Einf.");
    InsertButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InsertButton_actionPerformed(e);
      }
    });
    jMenuItem11.setMnemonic('Z');
    jMenuItem11.setText("Zugriffsschutz");
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
    jMenuItem12.setText("Standardmannschaften");
    jMenuItem12.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_standardmannsch.gif")));
    jMenuItem12.setMnemonic('M');
    jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem12_actionPerformed(e);
      }
    });
    jMenuOnlineUpdate.setText("efa Online-Update");
    jMenuOnlineUpdate.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_onlupd.gif")));
    jMenuOnlineUpdate.setMnemonic('O');
    jMenuOnlineUpdate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuOnlineUpdate_actionPerformed(e);
      }
    });
    infoLabel.setForeground(Color.blue);
    infoLabel.setHorizontalTextPosition(SwingConstants.LEFT);
    infoLabel.setText("infoLabel");
    bootsschadenButton.setMnemonic('B');
    bootsschadenButton.setText("Bootsschaden melden");
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
    jMenuItem13.setText("DRV-Fahrtenabzeichen");
    jMenuItem13.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_fahrtenabzeichen.gif")));
    jMenuItem13.setMnemonic('F');
    jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem13_actionPerformed(e);
      }
    });
    obmannLabel.setDisplayedMnemonic('O');
    obmannLabel.setLabelFor(obmann);
    jMenuFileDatensicherung.setMnemonic('D');
    jMenuFileDatensicherung.setText("Datensicherung");
    jMenuFileDatensicherung.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/menu_datensicherung.gif")));
    jMenuFileDatensicherung.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileDatensicherung_actionPerformed(e);
      }
    });
    jMenuItem14.setMnemonic('G');
    jMenuItem14.setText("Gruppen");
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
    mainPanel.add(refDatumLabel,   new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(refTag,   new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(refMonat,   new GridBagConstraints(8, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(refJahr,   new GridBagConstraints(10, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(refDatumLabel2,   new GridBagConstraints(9, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(refDatumLabel3,   new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
      obmannLabel.setText("  Obmann: ");
      obmann.setMinimumSize(new Dimension( (isDirectMode() ? 80 : 50), 17));
      Dialog.setPreferredSize(obmann,(isDirectMode() ? 80 : 50),19);
//      obmann.setPreferredSize(new Dimension( (isDirectMode() ? 80 : 50), Dialog.TEXTFIELD_HEIGHT));
      mainPanel.add(obmannLabel,    new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0
              ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      mainPanel.add(obmann,   new GridBagConstraints(10, 2, 2, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, (isDirectMode() ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE ), new Insets(0, 0, 0, 0), 0, 0));
      obmann.addItem( (isDirectMode() ? "keine Angabe" : "--") );
      obmann.addItem( (isDirectMode() ? "Steuermann" : "St") );
      for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) obmann.addItem( (isDirectMode() ? "Nummer " : "") + (i+1));

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

    refTag.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        refTag_focusLost(e);
      }
    });
    refMonat.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        refMonat_focusLost(e);
      }
    });
    refJahr.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        refJahr_focusLost(e);
      }
    });

    // folgende Komponenten werden unsichtbar, da sie vorerst nicht mehr benötigt werden
    // trotzdem sollen sie als "Backup" in der Implementation erhalten bleiben, falls sie
    // doch noch benötigt werden
    refDatumLabel.setVisible(false);
    refDatumLabel2.setVisible(false);
    refDatumLabel3.setVisible(false);
    refTag.setVisible(false);
    refMonat.setVisible(false);
    refJahr.setVisible(false);

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
    if (liste != null && liste.getClass().toString().endsWith("de.nmichael.efa.Mitglieder")) isMitgliederliste = true;

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
      if (Daten.fahrtenbuch != null && !Daten.fahrtenbuch.getFileName().equals("")) fb = Dialog.dateiDialog(this,"Fahrtenbuch öffnen","efa Fahrtenbuch (*.efb)","efb",Daten.fahrtenbuch.getFileName(),false);
      else fb = Dialog.dateiDialog(this,"Fahrtenbuch öffnen","efa Fahrtenbuch (*.efb)","efb",Daten.efaDataDirectory,false);

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
      Dialog.error("Diese Funktion steht nur im normalen efa-Modus zur Verfügung.\n"+
                   "Bitte starte efa im normalen Modus (nicht in der Bootshausvariante),\n"+
                   "um ein Online-Update durchzuführen.");
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
      Dialog.error("Du hast nicht die Berechtigung, diese Funktion auszuführen!");
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
      Dialog.infoDialog("Fehler","Die Hilfedatei\n'"+Daten.efaDocDirectory+"index.html'\nkonnte nicht gefunden werden.");
      return;
    }
    Dialog.neuBrowserDlg(this,"Dokumentation","file:"+Daten.efaDocDirectory+"index.html");
    startBringToFront(false); // efaDirekt im BRC -- Workaround
  }

  // efa-Tour starten
  void jMenu_startTour_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    Dialog.startTour(this);
  }

  // Willkommen-Dialog
  void jMenu_Willkommen_actionPerformed(ActionEvent e) {
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    startWillkommen();
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
    System.out.println("windowIconified");
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
        Logger.log(Logger.INFO,"Admin: Neuer Fahrtenbuch-Eintrag #"+lfdnr.getText().trim()+" wurde erstellt.");
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
        Dialog.error("Es sind noch Boote unterwegs.\n"+
                     "Das Einfügen von Einträgen ist nur möglich, wenn alle laufenden Fahrten beendet sind.");
        startBringToFront(false);
        return;
      }
    }
    int ret = Dialog.yesNoDialog("Eintrag einfügen","Soll vor dem aktuellen Eintrag (Lfd. Nr. "+curlfd+") wirklich ein neuer Eintrag eingefügt werden?\n"+
        "Alle nachfolgenden laufenden Nummern werden dann um eins erhöht!");
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
    if (Dialog.yesNoDialog("Wirklich löschen?","Soll der aktuelle Eintrag wirklich gelöscht werden?") == Dialog.YES) {
      Daten.fahrtenbuch.delete(aktDatensatz.get(Fahrtenbuch.LFDNR));
      if (isAdminMode()) {
        Logger.log(Logger.INFO,"Admin: Fahrtenbuch-Eintrag #"+(aktDatensatz != null ? aktDatensatz.get(Fahrtenbuch.LFDNR) : "$$")+" wurde gelöscht.");
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
        if (Dialog.yesNoDialog(art+" unbekannt (Tippfehler?)",
                               "Der Name '"+name+"'\n"+
                               "konnte in der "+liste+" nicht gefunden werden.\n"+
                               "Meintest Du '"+suggestedName+"'?") == Dialog.YES) {
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
        if (Daten.efaConfig.correctMisspelledBoote) checkNeighbours(boot,bootButton,Daten.fahrtenbuch.getDaten().boote,"Boot","Bootsliste",false);
      }
      if (Daten.efaConfig.correctMisspelledMitglieder) checkNeighbours(stm,stmButton,Daten.fahrtenbuch.getDaten().mitglieder,"Mitglied","Mitgliederliste",true);
      if (Daten.efaConfig.correctMisspelledMitglieder) for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) checkNeighbours(mannsch[i],mannschButton[i % 8],Daten.fahrtenbuch.getDaten().mitglieder,"Mitglied","Mitgliederliste",true);
      if (Daten.efaConfig.correctMisspelledZiele) {
        checkNeighbours(ziel, zielButton, Daten.fahrtenbuch.getDaten().ziele,
                        "Ziel", "Zielliste", false);
        if (Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen &&
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
      Dialog.error("Die Person '"+doppelt+"' wurde mehrfach eingegeben!");
      startBringToFront(false); // efaDirekt im BRC -- Workaround
      return;
    }

    // bei steuermannslosen Booten keinen Steuermann eingeben
    if (stm.getText().trim().length()>0 && Daten.fahrtenbuch.getDaten().boote != null &&
        Daten.bezeichnungen != null) {
      DatenFelder b = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot.getText().trim());
      if (b != null && b.get(Boote.STM).equals(Daten.bezeichnungen.bStm.get(Daten.bezeichnungen.BSTM_OHNE))) {
        int ret = Dialog.yesNoDialog("Steuermann",
                               "Du hast für ein steuermannsloses Boot einen Steuermann eingetragen.\n"+
                               "Möchtest Du diesen Eintrag dennoch speichern?");
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
        switch(Dialog.auswahlDialog("Doppeleintrag?",
                                    "Es gibt bereits einen ähnlichen Eintrag im Fahrtenbuch:\n\n"+
                                    "#"+dop.get(Fahrtenbuch.LFDNR)+" vom "+dop.get(Fahrtenbuch.DATUM)+" mit "+dop.get(Fahrtenbuch.BOOT)+":\n"+
                                    "Mannschaft: "+m+"\n"+
                                    "Abfahrt: "+dop.get(Fahrtenbuch.ABFAHRT)+"; Ankunft: "+dop.get(Fahrtenbuch.ANKUNFT)+"; Ziel: "+dop.get(Fahrtenbuch.ZIEL)+"\n\n"+
                                    "Möglicherweise handelt es sich bei dem aktuellen Eintrag #"+lfdnr.getText()+" um einen Doppeleintrag.\n"+
                                    "Was möchtest Du tun?",
                                    "Eintrag hinzufügen (kein Doppeleintrag)","Abbrechen",false)) {
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
    Mehrtagesfahrt mtour = Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtDauer.getSelectedItem());
    if (mtour != null) {
      if (EfaUtil.secondDateIsAfterFirst(datum.getText(),mtour.start) ||
          EfaUtil.secondDateIsAfterFirst(mtour.ende,datum.getText())) {
        Dialog.error("Das Datum der vorliegenden Fahrt ("+datum.getText()+") liegt außerhalb des\n"+
                     "Zeitraums ("+mtour.start+" - "+mtour.ende+"), der für die Mehrtagesfahrt\n"+
                     "'"+mtour.name+"' angegeben wurde.\n\n"+
                     "Wenn die Mehrtagesfahrt '"+mtour.name+"'\n"+
                     "mehrfach in diesem Jahr durchgeführt wurde, so muß für jede einzelne\n"+
                     "Durchführung ein neuer Mehrtagesfahrt-Eintrag in efa angelegt werden:\n"+
                     "Wähle in diesem Fall bitte als Art der Fahrt '>>> neue Mehrtagesfahrt'\n"+
                     "aus und erstelle einen neuen Mehrtagesfahrt-Eintrag.\n\n"+
                     "Sollte der vorliegende Eintrag tatsächlich zu der Mehrtagesfahrt\n"+
                     "'"+mtour.name+"' gehören und lediglich\n"+
                     "der Zeitraum für diese Mehrtagesfahrt versehentlich falsch eingegeben worden sein,\n"+
                     "so kannst Du den Zeitraum unter ->Administration->Fahrtenbuch->Mehrtagesfahrten\n"+
                     "korrigieren und anschließend diesen Eintrag der Fahrt hinzufügen.");
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return;
      }
    }

    // Prüfen, ob neues Jahr vorliegt
    Calendar cal = GregorianCalendar.getInstance();
    TMJ tmj = EfaUtil.string2date(datum.getText().trim(),0,0,0);
    TMJ ref = EfaUtil.correctDate(refDate,cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH),cal.get(GregorianCalendar.YEAR));
    if (tmj.jahr != ref.jahr && !Daten.fahrtenbuch.isEmpty()) {
      Dialog.infoDialog("Warnung","Ein Fahrtenbuch sollte immer nur die Fahrten EINES Jahres enthalten!\n"+
                                         "Bitte lösche die eben hinzugefügte Fahrt und beginne für die Fahrten\n"+
                                         "des neuen Jahren ein neues Fahrtenbuch!\n"+
                                         "Um ein neues Fahrtenbuch zu beginnen, wähle aus dem Menü 'Datei' den Punkt\n"+
                                         "'Neues Fahrtenbuch erstellen' und dort die Option 'Fahrtenbuch fortsetzen'.");
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
//    TMJ ref = EfaUtil.correctDate(refTag.getText().trim()+"."+refMonat.getText().trim()+"."+refJahr.getText().trim(),1,1,2001);
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
    if (aktBoot != null && Daten.bezeichnungen != null) {
      _stm = aktBoot.get(Boote.STM).equals(Daten.bezeichnungen.bStm.get(Bezeichnungen.BSTM_MIT));
      _anzRud = EfaUtil.string2date(aktBoot.get(Boote.ANZAHL),Fahrtenbuch.ANZ_MANNSCH,0,0).tag;
    }

    if (_stm && d.get(Mannschaften.STM).length()>0) stm.setText(d.get(Mannschaften.STM));
    for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
      if (i+1<=_anzRud && d.get(Mannschaften.MANNSCH1+i).length()>0) mannsch[i].setText(d.get(Mannschaften.MANNSCH1+i));
    }
    if (d.get(Mannschaften.ZIEL).length()>0) ziel.setText(d.get(Mannschaften.ZIEL));
    try {
      if (!Mannschaften.NO_FAHRTART.equals(d.get(Mannschaften.FAHRTART)) && fahrtDauer.getItemCount()>0)
        fahrtDauer.setSelectedItem(d.get(Mannschaften.FAHRTART));
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
        // aus dem Feld verschwinden (ebenso nach der Suche nach unnvollständigen Einträgen mit unbekannten
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


  // Zahlen in Eingabefeldern korrigieren
  void refTag_focusLost(FocusEvent e) {
    int i = 1;
    try {
      i = Integer.parseInt(refTag.getText().trim());
    } catch(Exception ee) {
    }
    if (i<1 || i>31) i=1;
    refTag.setText(Integer.toString(i));
  }
  void refMonat_focusLost(FocusEvent e) {
    int i = 1;
    try {
      i = Integer.parseInt(refMonat.getText().trim());
    } catch(Exception ee) {
    }
    if (i<1 || i>12) i=1;
    refMonat.setText(Integer.toString(i));
  }
  void refJahr_focusLost(FocusEvent e) {
    int i = 1;
    try {
      i = Integer.parseInt(refJahr.getText().trim());
    } catch(Exception ee) {
    }
    if (i<1 || i>2100) i=1;
    if (i<1900) i += 1900;
    if (i<1980) i += 100;
    refJahr.setText(Integer.toString(i));
  }
  void lfdnr_focusLost(FocusEvent e) {
    lfdnr.setText(EfaUtil.getLfdNr(lfdnr.getText()));
  }
  void bootskm_focusLost(FocusEvent e) {
    TMJ hhmm = EfaUtil.string2date(bootskm.getText().trim(),0,0,0); // TMJ mißbraucht für die Auswertung von Kilometern
    if (hhmm.monat == 0)
      if (hhmm.tag != 0) bootskm.setText(Integer.toString(hhmm.tag));
      else bootskm.setText("");
    else bootskm.setText(Integer.toString(hhmm.tag)+"."+Integer.toString(EfaUtil.makeDigit(hhmm.monat)));
    if (isDirectMode()) mannschkm_focusGained(null); // damit Mannsch-Km ausgefüllt werden
  }
  void mannschkm_focusLost(FocusEvent e) {
    TMJ hhmm = EfaUtil.string2date(mannschkm.getText().trim(),0,0,0); // TMJ mißbraucht für die Auswertung von Kilometern
    if (hhmm.monat == 0)
      if (hhmm.tag != 0) mannschkm.setText(Integer.toString(hhmm.tag));
      else mannschkm.setText("");
    else mannschkm.setText(Integer.toString(hhmm.tag)+"."+Integer.toString(EfaUtil.makeDigit(hhmm.monat)));
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
    if (Daten.efaConfig != null && Daten.efaConfig.standardFahrtart != null && Daten.efaConfig.standardFahrtart.length() > 0) {
      fahrtDauer.setSelectedItem(Daten.efaConfig.standardFahrtart);
    } else {
      fahrtDauer.setSelectedIndex(0);
    }
  }


  // anderes Element der Liste fahrtDauer ausgewählt
  void fahrtDauer_itemStateChanged(ItemEvent e) {
    if (ignoreFahrtDauerItemStateChanges) return;
    if (oldFahrtDauerSelectedIndex<0) { oldFahrtDauerSelectedIndex = 9999; return; }
    if (Daten.fahrtenbuch == null) { oldFahrtDauerSelectedIndex = 9999; return; }
    if (fahrtDauer.getSelectedIndex() == oldFahrtDauerSelectedIndex) { oldFahrtDauerSelectedIndex = 9999; return; } // doppelte Aufrufe verhindern

    if (fahrtDauer.getItemCount() > Daten.bezeichnungen.fahrtart.size()-1 &&
        ( ((String)fahrtDauer.getSelectedItem()).equals(neueMehrtagesfahrt) ||
          ((String)fahrtDauer.getSelectedItem()).equals(mehrtagesfahrtenBearbeiten) ) ) {
      // nothing to do
    } else {
      datensatzGeaendert = true;
    }
    oldFahrtDauerSelectedIndex = fahrtDauer.getSelectedIndex();

    if (isDirectMode()) return;

    if (fahrtDauer.getItemCount() > Daten.bezeichnungen.fahrtart.size()-1 &&
        ((String)fahrtDauer.getSelectedItem()).equals(neueMehrtagesfahrt)) {
      String mtourEnddatum = null;
      String mtourRudertage = null;
      if (aktDatensatz != null && aktDatensatz.get(Fahrtenbuch.FAHRTART).startsWith(this.mehrtagesfahrtKonfigurieren+"@@")) {
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
    if (fahrtDauer.getItemCount() > Daten.bezeichnungen.fahrtart.size()-1 &&
        mehrtagesfahrtenBearbeiten != null &&
        ((String)fahrtDauer.getSelectedItem()).equals(mehrtagesfahrtenBearbeiten)) {
      if (isDirectMode()) return;
      if (Daten.fahrtenbuch == null) return;
      if (datensatzGeaendert && !sicherheitsabfrageDatensatz()) return;
      ignoreFahrtDauerItemStateChanges=true;
      fahrtDauer.setPopupVisible(false);
      oeffneAuswahl(AuswahlFrame.MEHRTAGESFAHRTEN);
      if (aktDatensatz != null) try {
        String fa = aktDatensatz.get(Fahrtenbuch.FAHRTART);
        getAllFahrtDauer();
        if (fa.length()>0) {
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
    refTag.setText(Integer.toString(cal.get(GregorianCalendar.DAY_OF_MONTH)));
    refMonat.setText(Integer.toString(cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH)));
    refJahr.setText(Integer.toString(cal.get(GregorianCalendar.YEAR)));

    datensatzGeaendert = false;
    aktBoot = null;
    continueMTour = false;

    if (Daten.efaMainDirectory == null || Daten.efaProgramDirectory == null || Daten.efaDataDirectory == null || Daten.efaCfgDirectory == null) return;

    // Prüfen, ob efa gestartet werden darf
    EfaSec efaSec = new EfaSec(Daten.efaProgramDirectory+Daten.EFA_SECFILE);
    if (!efaSec.secFileExists() && admin == null) {
      admin = null;
      do {
        admin = AdminLoginFrame.login(null,"Zugang nur für Administratoren");
        if (admin == null) System.exit(100);
        if (!admin.allowedFahrtenbuchBearbeiten && !admin.allowedVollzugriff) Dialog.error("Du hast als Admin '"+admin.name+"' keine Berechtigung, das Fahrtenbuch zu bearbeiten!");
      } while (!admin.allowedFahrtenbuchBearbeiten && !admin.allowedVollzugriff);
    }

    // Bezeichnungen.cfg
    Daten.bezeichnungen = new Bezeichnungen(Daten.efaCfgDirectory+Daten.BEZEICHFILE);
    Daten.bezeichnungen.createNewIfDoesntExist();
    Daten.bezeichnungen.readFile();

    // WettDefs.cfg
    Daten.wettDefs = new WettDefs(Daten.efaCfgDirectory+Daten.WETTDEFS);
    Daten.wettDefs.createNewIfDoesntExist();
    Daten.wettDefs.readFile();

    // BackupVerzeichnis bestimmen
    EfaConfigFrame.setBakDir(Daten.efaConfig.bakDir);
    Daten.backup = new Backup(Daten.efaBakDirectory,Daten.efaConfig.bakSave,Daten.efaConfig.bakMonat,Daten.efaConfig.bakTag,Daten.efaConfig.bakKonv);

    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.VEREINSCONFIG) && EfaUtil.canOpenFile(Daten.efaProgramDirectory+"verein.cfg")) {
      // verein.cfg von ./programm nach ./daten/verein.efv verschieben
      File f = new File(Daten.efaProgramDirectory+"verein.cfg");
      try {
        f.renameTo(new File(Daten.efaDataDirectory+Daten.VEREINSCONFIG));
        Logger.log(Logger.INFO,"Vereins-Konfigurationsdatei wurde von '"+Daten.efaProgramDirectory+"verein.cfg' nach '"+Daten.efaDataDirectory+Daten.VEREINSCONFIG+"' verschoben.");
      } catch (Exception e) {
        Logger.log(Logger.ERROR,"Vereins-Konfigurationsdatei konnte nicht von '"+Daten.efaProgramDirectory+"verein.cfg' nach '"+Daten.efaDataDirectory+Daten.VEREINSCONFIG+"' verschoben werden: "+e.toString());
      }
    }
    Daten.vereinsConfig = new VereinsConfig(Daten.efaDataDirectory+Daten.VEREINSCONFIG);
    boolean editVerein=false;
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.VEREINSCONFIG)) {
      Daten.vereinsConfig.writeFile();
//      editVerein=true; // Nein, lieber nicht schon beim ersten Start!!
    }
    if (!Daten.vereinsConfig.readFile()) {
      Logger.log(Logger.ERROR,"Vereins-Konfigurationsdatei '"+Daten.VEREINSCONFIG+"' konnte nicht gelesen werden.");
    } else if (editVerein) {
      // Fenster zum Eingeben der Daten öffnen
      VereinsConfigFrame dlg = new VereinsConfigFrame(this,Daten.vereinsConfig);
      Dialog.setDlgLocation(dlg);
      dlg.setModal(!Dialog.tourRunning);
      dlg.show();
      startBringToFront(false); // efaDirekt im BRC -- Workaround
    }

    Daten.adressen = new Adressen(Daten.efaDataDirectory+Daten.ADRESSENFILE);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.ADRESSENFILE)) {
      if (Daten.adressen.writeFile())
        Logger.log(Logger.INFO,"Adressendatei '"+Daten.efaDataDirectory+Daten.ADRESSENFILE+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Adressendatei '"+Daten.efaDataDirectory+Daten.ADRESSENFILE+"' konnte nicht erstellt werden.");
    }
    if (!Daten.adressen.readFile()) {
      Logger.log(Logger.ERROR,"Adressendatei '"+Daten.efaDataDirectory+Daten.ADRESSENFILE+"' konnte nicht gelesen werden.");
    }

    Daten.synMitglieder = new Synonyme(Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM)) {
      if (Daten.synMitglieder.writeFile())
        Logger.log(Logger.INFO,"Mitglieder-Synonymdatei '"+Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Mitglieder-Synonymdatei '"+Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM+"' konnte nicht erstellt werden.");
    }
    if (!Daten.synMitglieder.readFile()) {
      Logger.log(Logger.ERROR,"Mitglieder-Synonymdatei '"+Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM+"' konnte nicht gelesen werden.");
    }
    Daten.synBoote = new Synonyme(Daten.efaDataDirectory+Daten.BOOTE_SYNONYM);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.BOOTE_SYNONYM)) {
      if (Daten.synBoote.writeFile())
        Logger.log(Logger.INFO,"Boots-Synonymdatei '"+Daten.efaDataDirectory+Daten.BOOTE_SYNONYM+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Boots-Synonymdatei '"+Daten.efaDataDirectory+Daten.BOOTE_SYNONYM+"' konnte nicht erstellt werden.");
    }
    if (!Daten.synBoote.readFile()) {
      Logger.log(Logger.ERROR,"Boots-Synonymdatei '"+Daten.efaDataDirectory+Daten.BOOTE_SYNONYM+"' konnte nicht gelesen werden.");
    }
    Daten.synZiele = new Synonyme(Daten.efaDataDirectory+Daten.ZIELE_SYNONYM);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.ZIELE_SYNONYM)) {
      if (Daten.synZiele.writeFile())
        Logger.log(Logger.INFO,"Ziel-Synonymdatei '"+Daten.efaDataDirectory+Daten.ZIELE_SYNONYM+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Ziel-Synonymdatei '"+Daten.efaDataDirectory+Daten.ZIELE_SYNONYM+"' konnte nicht erstellt werden.");
    }
    if (!Daten.synZiele.readFile()) {
      Logger.log(Logger.ERROR,"Ziel-Synonymdatei '"+Daten.efaDataDirectory+Daten.ZIELE_SYNONYM+"' konnte nicht gelesen werden.");
    }

    Daten.mannschaften = new Mannschaften(Daten.efaDataDirectory+Daten.MANNSCHAFTENFILE);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.MANNSCHAFTENFILE)) {
      if (Daten.mannschaften.writeFile())
        Logger.log(Logger.INFO,"Mannschaftendatei '"+Daten.efaDataDirectory+Daten.MANNSCHAFTENFILE+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Mannschaftendatei '"+Daten.efaDataDirectory+Daten.MANNSCHAFTENFILE+"' konnte nicht erstellt werden.");
    }
    if (!Daten.mannschaften.readFile()) {
      Logger.log(Logger.ERROR,"Mannschaftendatei '"+Daten.efaDataDirectory+Daten.MANNSCHAFTENFILE+"' konnte nicht gelesen werden.");
    }

    Daten.fahrtenabzeichen = new Fahrtenabzeichen(Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN)) {
      if (Daten.fahrtenabzeichen.writeFile())
        Logger.log(Logger.INFO,"DRV-Fahrtenabezeichendatei '"+Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"DRV-Fahrtenabezeichendatei '"+Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN+"' konnte nicht erstellt werden.");
    }
    if (!Daten.fahrtenabzeichen.readFile()) {
      Logger.log(Logger.ERROR,"DRV-Fahrtenabezeichendatei '"+Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN+"' konnte nicht gelesen werden.");
    }

    Daten.gruppen = new Gruppen(Daten.efaDataDirectory+Daten.GRUPPEN);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.GRUPPEN)) {
      if (Daten.gruppen.writeFile())
        Logger.log(Logger.INFO,"Gruppendatei '"+Daten.efaDataDirectory+Daten.GRUPPEN+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Gruppendatei '"+Daten.efaDataDirectory+Daten.GRUPPEN+"' konnte nicht erstellt werden.");
    }
    if (!Daten.gruppen.readFile()) {
      Logger.log(Logger.ERROR,"Gruppendatei '"+Daten.efaDataDirectory+Daten.GRUPPEN+"' konnte nicht gelesen werden.");
    }

    Daten.keyStore = new EfaKeyStore(Daten.efaDataDirectory+Daten.PUBKEYSTORE,"efa".toCharArray());

    // nach Webbrowser suchen
    if (Daten.efaConfig.browser.equals("")) {
      String[] browsers = { "/opt/netscape/netscape", "/usr/X11R6/bin/netscape", "/usr/bin/netscape", "/usr/local/bin/netscape",
                            "/usr/local/mozilla/mozilla",
                            "/opt/kde2/bin/konqueror/",
                            "c:\\programme\\netscape\\communicator\\program\\netscape.exe",
                            "c:\\programme\\internet explorer\\iexplore.exe", "c:\\windows\\explorer.exe", "c:\\win\\explorer.exe", "c:\\winnt\\explorer.exe"};
      for (int i=0; i<browsers.length && Daten.efaConfig.browser.equals(""); i++)
        if (new File(browsers[i]).isFile()) Daten.efaConfig.browser = browsers[i];
    }


    if (openWelcome) {
      startWillkommen();
      startEfaTour = true;
    }

    // Nutzer nach Name und Verein fragen
    try {
        ++Daten.efaConfig.countEfaStarts;
        if (Daten.efaConfig.countEfaStarts <31 && Daten.efaConfig.countEfaStarts % 10 == 0)
          if (Dialog.neuBrowserDlg(this,"efa","file:"+Daten.efaProgramDirectory+"html"+Daten.fileSep+"users.html",750,600,(int)Dialog.screenSize.getWidth()/2-375,(int)Dialog.screenSize.getHeight()/2-300).endsWith(".pl"))
            Daten.efaConfig.countEfaStarts += 100000;
    } catch(Exception e) {
        //nothing to do
    }

    // Neuerungen von efa anzeigen
    // System.out.println("Daten.efaConfig.version = >>"+Daten.efaConfig.version+"<<");
    // System.out.println("DatenPROGRAMMID =         >>"+Daten.PROGRAMMID+"<<");
    // System.out.println("Daten.efaConfig.version.equals(Daten.PROGRAMMID) = "+Daten.efaConfig.version.equals(Daten.PROGRAMMID));
    // System.out.println("Daten.efaConfig.version.compareTo(Daten.PROGRAMMID) = "+Daten.efaConfig.version.compareTo(Daten.PROGRAMMID));
    // folgende Zeile liefert manchmal (?) für den equals-Vergleich "false", obwohl die Strings identisch sind; daher doppelter Boden:
    // der Dialog wird nur angezeigt, wenn auch compareTo != 0 liefert!
    if (!startEfaTour && !Daten.efaConfig.version.equals(Daten.PROGRAMMID) && Daten.efaConfig.version.compareTo(Daten.PROGRAMMID) != 0) {
      Dialog.neuBrowserDlg(this,"efa","file:"+Daten.efaProgramDirectory+"html"+Daten.fileSep+"tour"+Daten.fileSep+"k06-001.html",750,600,(int)Dialog.screenSize.getWidth()/2-375,(int)Dialog.screenSize.getHeight()/2-300);
      Daten.checkJavaVersion(true);
    } else {
      Daten.checkJavaVersion(false);
    }


    // Bei 1 Jahr alten Versionen alle 90 Tage prüfen, ob eine neue Version vorliegt
    if (EfaUtil.getDateDiff(Daten.VERSIONRELEASEDATE,EfaUtil.getCurrentTimeStampDD_MM_YYYY()) > 365 &&
        (Daten.efaConfig.efaVersionLastCheck == null || Daten.efaConfig.efaVersionLastCheck.length() == 0 ||
         EfaUtil.getDateDiff(Daten.efaConfig.efaVersionLastCheck,EfaUtil.getCurrentTimeStampDD_MM_YYYY()) > 90) ) {
      if (Dialog.yesNoDialog("Prüfen, ob neue efa-Version verfügbar",
                             "Die von Dir verwendete Version von efa ("+Daten.VERSIONID+") ist bereits\n"+
                             "über ein Jahr alt. Soll efa jetzt für Dich prüfen, ob eine\n"+
                             "neue Version von efa vorliegt?") == Dialog.YES) {
        OnlineUpdateFrame.runOnlineUpdate(this,Daten.ONLINEUPDATE_INFO);
      }
      Daten.efaConfig.efaVersionLastCheck = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
    }

    if (startOpenFb != null && EfaUtil.canOpenFile(startOpenFb)) fahrtenbuchOeffnen(startOpenFb);
    else if (!Daten.efaConfig.letzteDatei.equals("") && EfaUtil.canOpenFile(Daten.efaConfig.letzteDatei)) fahrtenbuchOeffnen(Daten.efaConfig.letzteDatei);
    else if (Daten.efaConfig.letzteDatei.equals("") && Daten.efaConfig.countEfaStarts == 1)
      if (Dialog.yesNoDialog("Neues Fahrtenbuch anlegen","Du startest efa heute zum ersten Mal.\nMöchtest Du ein neues Fahrtenbuch anlegen?"
                                        ) == Dialog.YES) {
        askForOpenNewFb = true;
      }

    // ggf. fragen, ob es sich um einen Berliner Verein handelt
    if (Daten.efaConfig.version.compareTo("EFA.141")<0 || openWelcome) {
      Daten.efaConfig.showBerlinOptions =
          Dialog.auswahlDialog("Berliner Vereine",
                               "efa verfügt über Funktionen (bzgl. Zielbereichen), die speziell für Berliner Vereine\n"+
                               "gedacht sind. Die entsprechenden Optionen sind für Nicht-Berliner Vereine nicht\n"+
                               "relevant und können, um die Übersichtlichkeit zu erhöhen, verborgen werden.\n"+
                               "Bitte wähle aus:",
                               "Optionen für Berliner Vereine anzeigen",
                               "Nur-Berlin-relevante Optionen verbergen",
                               false) != 1;
      Daten.efaConfig.writeFile();
    }

    // ggf. nach Zielbereich, in dem der Verein liegt, fragen (wenn zum ersten Mal eine Version neuer/gleich 1.3.5 gestartet wird)
    if (Daten.efaConfig.showBerlinOptions && Daten.vereinsConfig != null &&
        (Daten.efaConfig.version.compareTo("EFA.135")<0 || openWelcome)) Daten.vereinsConfig.askForZielbereichOnStart();

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
    if (mehrtagesfahrtenBearbeiten != null) {
      oldFahrtDauerSelectedIndex=-1; fahrtDauer.removeItemAt(fahrtDauer.getItemCount()-1); // letztes Element ("Mehrtagesfahrten bearbeiten") entfernen
    }
    oldFahrtDauerSelectedIndex=-1; fahrtDauer.removeItemAt(fahrtDauer.getItemCount()-1); // letztes Element ("neue Mehrtagesfahrt") entfernen
    oldFahrtDauerSelectedIndex=-1; fahrtDauer.addItem(name);
    oldFahrtDauerSelectedIndex=-1; fahrtDauer.addItem(neueMehrtagesfahrt);
    if (mehrtagesfahrtenBearbeiten != null) {
      oldFahrtDauerSelectedIndex=-1; fahrtDauer.addItem(mehrtagesfahrtenBearbeiten);
    }
    oldFahrtDauerSelectedIndex=-1; fahrtDauer.setSelectedIndex(fahrtDauer.getItemCount()- (mehrtagesfahrtenBearbeiten != null ? 3 : 2));
    ignoreFahrtDauerItemStateChanges=false;
    datensatzGeaendert = true;
  }


  // Alle Angaben zu Mehrtagesfahrten zusammentragen
  // (nur nach FB öffnen aufrufen, da akt. Eintrag nicht gemerkt wird)
  void getAllFahrtDauer() {
    fahrtDauer.removeAllItems();
    for (int i=0; i<Daten.bezeichnungen.fahrtart.size()-1; i++)
      fahrtDauer.addItem(Daten.bezeichnungen.fahrtart.get(i));
    if (mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN) fahrtDauer.addItem(mehrtagesfahrtKonfigurieren);
    if (Daten.fahrtenbuch != null) {
      String[] mtours = Daten.fahrtenbuch.getAllMehrtagesfahrtNamen();
      for (int i=0; mtours != null && i<mtours.length; i++)
        fahrtDauer.addItem(Daten.fahrtenbuch.getMehrtagesfahrt(mtours[i]).getDisplayName());
    }
    fahrtDauer.addItem(neueMehrtagesfahrt);
    if (mehrtagesfahrtenBearbeiten != null) fahrtDauer.addItem(mehrtagesfahrtenBearbeiten);
    setFahrtDauerDefault();
  }




  // Fahrtenbuch öffnen
  void fahrtenbuchOeffnen(String datei) {
    Daten.fahrtenbuch = new Fahrtenbuch(datei);
    this.setTitle("efa - "+datei);
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

  private DatenFelder getFields(String mtourEnddatum, String mtourRudertage, String mtourName) {
    String fd;
    if (fahrtDauer.getSelectedIndex() > 0 && fahrtDauer.getSelectedIndex() < fahrtDauer.getItemCount() &&
        !fahrtDauer.getSelectedItem().toString().equals(neueMehrtagesfahrt) &&
        !fahrtDauer.getSelectedItem().toString().equals(mehrtagesfahrtenBearbeiten)) {
      fd = (String)fahrtDauer.getSelectedItem();
      if (!Mehrtagesfahrt.isVordefinierteFahrtart(fd)) {
        fd = Mehrtagesfahrt.getNameFromDisplayName(fd);
      }
    } else fd = "";

    if (Daten.bezeichnungen != null && (mode == MODE_ENDE || mode == MODE_NACHTRAG) &&
        fd.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_MEHRTAGESFAHRT))) {
      if (mtourName != null) {
        fd = mtourName;
      } else {
        fd = mehrtagesfahrtKonfigurieren +
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
      Logger.log(Logger.INFO,"Admin: Fahrtenbuch-Eintrag #"+(aktDatensatz != null ? aktDatensatz.get(Fahrtenbuch.LFDNR) : "$$")+" wurde verändert.");
    }
  }


  // Datensatz speichern
  // liefert "true", wenn erfolgreich
  boolean speichereDatensatz() {

    if (Daten.fahrtenbuch == null) return false;

    // Eintrag (Km,Mannschkm) auf Korrektheit prüfen
    if (EfaUtil.zehntelString2Int(bootskm.getText().trim()) == 0)
      if (Dialog.yesNoDialog("Warnung","Die Bootskilometer sind 0.\nMöchtest Du diesen Eintrag wirklich hinzufügen?"
                                             ) == Dialog.NO) {
        Dialog.infoDialog("Information","Eintrag nicht hinzugefügt.");
        bootskm.requestFocus();
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return false;
      }
    if (EfaUtil.zehntelString2Int(bootskm.getText().trim()) * getAnzahlRuderer() != EfaUtil.zehntelString2Int(mannschkm.getText().trim()))
      if (Dialog.yesNoDialog("Warnung","Die Mannschaftskilometer stimmen nicht mit den Bootskilometern\nund der Anzahl der Ruderer überein.\nMöchtest Du diesen Eintrag wirklich hinzufügen?"
                                             ) == Dialog.NO) {
        Dialog.infoDialog("Information","Eintrag nicht hinzugefügt.");
//        mannschkm.requestFocus(); // wg. Dennis auskommentiert (s. Mail vom 26.02.02)
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return false;
      }

    String neu = EfaUtil.getLfdNr(lfdnr.getText());
    if ( (Daten.fahrtenbuch.getExact(neu) != null && (neuerDatensatz || !neu.equals(aktDatensatz.get(Fahrtenbuch.LFDNR))) )
         || neu.equals("")) {
      Dialog.infoDialog("Ungültige laufende Nummer","Diese Laufende Nummer ist bereits vergeben! Jede Laufende\n"+
                                         "Nummer darf nur einmal verwendet werden werden.\n"+
                                         "Bitte korrigiere die laufende Nummer des Eintrags!\n\n"+
                                         "Hinweis: Um mehrere Einträge unter 'derselben' Nummer hinzuzufügen,\n"+
                                         "füge einen Buchstaben von A bis Z direkt an die Nummer an!");
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
            Dialog.yesNoDialog("Warnung","Die Laufende Nummer dieses Eintrags ist kleiner als die des\n"+
                                           "letzten Eintrags. Bist Du sicher, daß Du den Eintrag mit einer kleineren\n"+
                                           "Laufenden Nummer hinzufügen möchtest?"
                                           ) == Dialog.NO) {
          Dialog.infoDialog("Information","Eintrag nicht hinzugefügt.");
          lfdnr.requestFocus();
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return false;
        }
      }
      lfdNrForNewEntry = EfaUtil.string2date(lfdnr.getText(),1,1,1).tag; // lfdNr merken, nächster Eintrag erhält dann per default diese Nummer + 1
    } else { // geänderter Fahrtenbucheintrag
      if (!aktDatensatz.get(Fahrtenbuch.LFDNR).equals(EfaUtil.getLfdNr(lfdnr.getText()))) {
        if (Dialog.yesNoDialog("Warnung",
            "Du hast die Laufende Nummer dieses Eintrags verändert!\n" +
            "Bist Du sicher, daß die Laufende Nummer geändert werden soll?") == Dialog.NO) {
          Dialog.infoDialog("Information","Geänderter Eintrag nicht gespeichert.");
          lfdnr.requestFocus();
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return false;
        }
      }
    }

    // Prüfen, ob im Fall einer Mehrtagesfahrt diese im angegebenen Zeitraum der ausgewählten (vorhandenen) Fahrt liegt
    if (fahrtDauer.getSelectedIndex() > 0 &&
        !fahrtDauer.getSelectedItem().toString().equals(neueMehrtagesfahrt) &&
        !fahrtDauer.getSelectedItem().toString().equals(mehrtagesfahrtenBearbeiten)) {
      String fahrtart = (String)this.fahrtDauer.getSelectedItem();
      Mehrtagesfahrt m = null;
      if (fahrtart != null) m = Daten.fahrtenbuch.getMehrtagesfahrt(fahrtart);
      if (m != null) {
        String datum = this.datum.getText();
        if (EfaUtil.secondDateIsAfterFirst(m.ende,datum) || EfaUtil.secondDateIsAfterFirst(datum,m.start)) {
          Dialog.error("Das Datum der aktuellen Fahrt ("+datum+") liegt außerhalb des Zeitraums,\n"+
                       "der für die gewählte Mehrtagesfahrt angegeben wurde ("+m.start+" bis "+m.ende+")!\n"+
                       "Falls in diesem Jahr mehrere gleichnamige Mehrtagesfahrten zu unterschiedlichen\n"+
                       "Zeiten (z.B. einmal im April, einmal im September) stattgefunden haben,\n"+
                       "so erstelle bitte für jede Mehrtagesfahrt einen eigenen Mehrtagesfahrt-Eintrag in\n"+
                       "efa, z.B. 'Mehrtagesfahrt XYZ (April)' und 'Mehrtagesfahrt XYZ (September)'.\n"+
                       "Falls die aktuelle Fahrt tatsächlich zu der ausgewählten Mehrtagesfahrt gehört,\n"+
                       "so ändere bitte den Zeitraum für diese Mehrtagesfahrt über das Menü\n"+
                       "->Administration->Fahrtenbuch->Mehrtagesfahrten entsprechend ab.");
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
        Dialog.error("Für die als Obmann ausgewählte Person wurde kein Name eingegeben.\n"+
                     "Bitte gib entweder einen Namen ein oder wählen jemand anderes als Obmann aus!");
        startBringToFront(false); // efaDirekt im BRC -- Workaround
        return false;
      }
    }

    // Ok, alles klar: Jetzt speichern

    // Mehrtagestour fortsetzen (d.h. Fahrtart beim neuen Eintrag beibehalten)??
    // -> nur, wenn es sich um eine wirkliche Wanderfahrt handelt, die in Form mehrerer Etappen eingegeben wird
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.fahrtart != null &&
        fahrtDauer.getSelectedIndex()>=Daten.bezeichnungen.fahrtart.size()-1 &&
        !fahrtDauer.getSelectedItem().toString().equals(neueMehrtagesfahrt) &&
        !fahrtDauer.getSelectedItem().toString().equals(mehrtagesfahrtenBearbeiten) &&
        Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtDauer.getSelectedItem()) != null &&
        Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtDauer.getSelectedItem()).isEtappen) continueMTour=true;
    else continueMTour=false;

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
        dat = Dialog.dateiDialog(this,"Fahrtenbuch speichern unter","efa Fahrtenbuch (*.efb)","efb",Daten.fahrtenbuch.getFileName(),true);
      else
        dat = Dialog.dateiDialog(this,"Fahrtenbuch speichern unter","efa Fahrtenbuch (*.efb)","efb",null,true);

      if (dat == null) ok = false;

      if (ok) {
        if (!dat.endsWith(".") && !dat.toUpperCase().endsWith(".EFB")) {
          dat = dat+".efb";
          if (EfaUtil.canOpenFile(dat)) {
            switch (Dialog.yesNoCancelDialog("Datei existiert bereits","Soll die Datei\n'"+dat+"'\nüberschrieben werden?")) {
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
          Logger.log(Logger.INFO,"Admin: Änderungen am Fahrtenbuch wurden erfolgreich gespeichert.");
        } else {
          Logger.log(Logger.WARNING,"Admin: Die Änderungen am Fahrtenbuch konnten nicht gespeichert werden.");
        }
      }

      if (unter && success) {
        if (Dialog.yesNoDialog("Gespeichertes Fahrtenbuch öffnen?",
                               "Soll das soeben gespeicherte Fahrtenbuch '"+Daten.fahrtenbuch.getFileName()+"'\njetzt benutzt werden?") == Dialog.YES) {
          if (mode == MODE_FULL) Daten.efaConfig.letzteDatei = Daten.fahrtenbuch.getFileName();
          this.setTitle("efa - "+Daten.fahrtenbuch.getFileName());
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
      if (!Daten.fahrtenbuch.getDaten().boote.writeFile())
        Dialog.infoDialog("Fehler","Änderungen an der Bootsliste konnten nicht gespeichert werden!");
      if (Daten.synBoote != null && Daten.synBoote.isChanged()) // Auch Synonyme wegen ggf. hinzugefügter Kombiboote speichern!
        if (!Daten.synBoote.writeFile())
          Dialog.infoDialog("Fehler","Änderungen an der Boots-Synonymliste konnten nicht gespeichert werden!");
      if (isAdminMode()) {
        Logger.log(Logger.INFO,"Admin: Änderungen an der Bootsliste wurden gespeichert.");
      }

    }
    if (Daten.fahrtenbuch.getDaten().mitglieder != null && Daten.fahrtenbuch.getDaten().mitglieder.isChanged()) {
      if (!Daten.fahrtenbuch.getDaten().mitglieder.writeFile())
        Dialog.infoDialog("Fehler","Änderungen an der Mitgliederliste konnten nicht gespeichert werden!");
      if (isAdminMode()) {
        Logger.log(Logger.INFO,"Admin: Änderungen an der Mitgliederliste wurden gespeichert.");
      }
    }
    if (Daten.fahrtenbuch.getDaten().ziele != null && Daten.fahrtenbuch.getDaten().ziele.isChanged()) {
      if (!Daten.fahrtenbuch.getDaten().ziele.writeFile())
        Dialog.infoDialog("Fehler","Änderungen an der Zielliste konnten nicht gespeichert werden!");
      if (isAdminMode()) {
        Logger.log(Logger.INFO,"Admin: Änderungen an der Zielliste wurden gespeichert.");
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
            Dialog.error("Ein Java-Fehler beim Setzen des Datums ist aufgetreten!\n"+
                         "Bitte setze den Cursor von Hand ins Datums-Feld (einfach\n"+
                         "einmal mit der Maus ins Datums-Feld klicken) und setze\n"+
                         "die Arbeit dann fort.");
        } catch(Exception eeee) {
          Dialog.error("Ein Java-Fehler beim Setzen des Datums ist aufgetreten!\n"+
                       "Bitte setze den Cursor von Hand ins Datums-Feld (einfach\n"+
                       "einmal mit der Maus ins Datums-Feld klicken) und setze\n"+
                       "die Arbeit dann fort.");
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
      addButton.setText("Eintrag zum Fahrtenbuch hinzufügen");
      return;
    }
    neuerDatensatz = false;
    neuerDatensatz_einf = false;
    addButton.setText("Änderungen an aktuellem Eintrag speichern");
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
    Mehrtagesfahrt mtour = Daten.fahrtenbuch.getMehrtagesfahrt(d.get(Fahrtenbuch.FAHRTART));
    if (fahrtDauer.getItemCount()>0) {
      if (mtour != null)
        fahrtDauer.setSelectedItem(mtour.getDisplayName());
      else if (Mehrtagesfahrt.isVordefinierteFahrtart(d.get(Fahrtenbuch.FAHRTART)))
        fahrtDauer.setSelectedItem(d.get(Fahrtenbuch.FAHRTART));
      else if (d.get(Fahrtenbuch.FAHRTART).startsWith(mehrtagesfahrtKonfigurieren) && (mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN))
        fahrtDauer.setSelectedItem(mehrtagesfahrtKonfigurieren);
      else
        fahrtDauer.setSelectedIndex(0);
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
    addButton.setText("Eintrag zum Fahrtenbuch hinzufügen");

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
      if (neuerDatensatz) txt= "Der aktuelle Eintrag wurde verändert und noch nicht zum Fahrtenbuch hinzugefügt.\n"+
                               "Möchtest Du ihn jetzt hinzufügen?";
      else txt = "Änderungen an dem aktuellen Eintrag wurden noch nicht gespeichert.\n"+
                 "Sollen sie jetzt gespeichert werden?";
      switch(Dialog.yesNoCancelDialog("Eintrag nicht gespeichert",txt)) {
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
      int ret =  Dialog.yesNoCancelDialog("Änderungen nicht gespeichert","Änderungen an den Datenlisten wurden noch nicht gespeichert.\n"+
                                             "Möchtest Du die Änderungen jetzt speichern?");
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
      int ret = Dialog.yesNoCancelDialog("Änderungen nicht gespeichert","Änderungen am Fahrtenbuch wurden noch nicht gespeichert.\n"+
                                             "Möchtest Du die Änderungen jetzt speichern?");
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
    this.setTitle("efa - "+Daten.fahrtenbuch.getFileName());
    if (mode == MODE_FULL) Daten.efaConfig.letzteDatei = Daten.fahrtenbuch.getFileName();
    getAllFahrtDauer();
    if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null)
      Daten.fahrtenbuch.getDaten().mitglieder.getAliases();
    datensatzGeaendert = false;
  }

  public void checkStartTour() {
    if (startEfaTour)
      Dialog.startTour(this);
    if (askForOpenNewFb) {
      neuesFahrtenbuchDialog(true);
    }
  }

  public void startWillkommen() {
    Dialog.neuBrowserDlg(this,"Willkommen","file:"+Daten.efaProgramDirectory+"html"+Daten.fileSep+"welcome.html");
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
    if (!Daten.efaConfig.writeFile())
      Logger.log(Logger.ERROR,"Konfigurationsdatei '"+Daten.CONFIGFILE+"' konnte nicht geschrieben werden.");
    System.exit(0);
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
        Logger.log(Logger.DEBUG,"Dialog closed: EfaFrame is already active.");
        return;
      }
      Logger.log(Logger.DEBUG,"Dialog closed: EfaFrame is inactive and will be brought to front.");
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
    if (mode != MODE_ENDE && mode != MODE_NACHTRAG) return; // Zielabhängiges Enabled der BootsKm nur bei "Fahrt beenden" und "Nachtrag"
    boolean enabled = !(zielButton.getBackground() == Color.green) ||
            Daten.efaConfig == null || !Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen;
    setFieldEnabled(enabled,bootskm,bootskmLabel);
  }

  private void setFieldEnabledStmUndMannsch(DatenFelder d) {
    boolean isstm = true;
    int anz = Fahrtenbuch.ANZ_MANNSCH;
    if (d != null) {
      isstm = d.get(Boote.STM).equals(Daten.bezeichnungen.bStm.get(Bezeichnungen.BSTM_MIT));
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
    this.setTitle("Neue Fahrt beginnen");
    SetBlankFields();
    if (this.lfdnr.getText().trim().length()==0) this.lfdnr.setText("1");

    this.refDate=""; datumSetText(""); // damit aktuelles Datum im Datums-Feld erscheint...
    setDateFromRefDate();
    if (boot != null) this.boot.setText(boot);
    vervollstaendige(this.boot,bootButton,Daten.fahrtenbuch.getDaten().boote,null,this,false);
    setTime(abfahrt,Daten.efaConfig.efaDirekt_plusMinutenAbfahrt);
    addButton.setText("Fahrt beginnen");

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
    this.setTitle("Eintrag für neue Fahrt korrigieren");
    DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getExactComplete(lfdnr);
    if (d == null) {
      Logger.log(Logger.ERROR,"Fahrtbeginn (Korrektur): Die gewählte Fahrt #"+lfdnr+" ("+boot+") konnte nicht gefunden werden!");
      Dialog.error("Die gewählte Fahrt #"+lfdnr+" konnte nicht gefunden werden! Bitte dem Administrator bescheid sagen!");
      Dialog.frameOpened(this); // Bugfix: Damit es nicht zur Stack-Inkonsistenz kommt!
      cancel();
      return;
    }
    SetFields(d);
    addButton.setText("Fahrt beginnen (Korrektur)");

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
    this.setTitle("Fahrt abschließen");
    DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getExactComplete(lfdnr);
    if (d == null) {
      Logger.log(Logger.ERROR,"Fahrtende: Die gewählte Fahrt #"+lfdnr+" ("+boot+") konnte nicht gefunden werden!");
      Dialog.error("Die gewählte Fahrt #"+lfdnr+" konnte nicht gefunden werden! Bitte dem Administrator bescheid sagen!");
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
    addButton.setText("Fahrt abschließen");

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
    this.setTitle("Nachtrag");
    SetBlankFields();
    if (this.lfdnr.getText().trim().length()==0) this.lfdnr.setText("1");

    this.refDate=""; datumSetText(""); // damit aktuelles Datum im Datums-Feld erscheint...
    setDateFromRefDate();
    addButton.setText("Nachtrag speichern");

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
        if (Dialog.yesNoDialog("Kein Bootsname angegeben",
                               "Du hast keinen Bootsnamen angegeben.\nMöchtest Du das Feld wirklich leer lassen?") != Dialog.YES) {
          boot.requestFocus();
          startBringToFront(false); // efaDirekt im BRC -- Workaround
          return;
        }
      } else {
        Dialog.error("Bitte gib einen Bootsnamen an!");
        boot.requestFocus();
        return;
      }
    }
    abfahrt_focusLost(null); ankunft_focusLost(null);

    String pers = stmMannsch2String();
    if (pers.length()==0) {
      Dialog.error("Bitte trage mindestens eine Person ein!");
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
      Dialog.error("Bitte wähle als Obmann eine Person aus, die tatsächlich im Boot sitzt!");
      obmann.requestFocus();
      return;
    }

    if (Daten.efaConfig.efaDirekt_eintragErzwingeObmann && getObmann() < 0) {
      Dialog.error("Bitte wähle einen Obmann aus!");
      obmann.requestFocus();
      return;
    }

    // Prüfen, ob ggf. nur bekannte Boote/Ruderer/Ziele eingetragen wurden
    if (Daten.efaConfig.efaDirekt_eintragNurBekannteBoote && unbekannterName(boot,Daten.fahrtenbuch.getDaten().boote)) {
      Dialog.error("Das Boot '"+boot.getText().trim()+"' ist unbekannt. Bitte trage ein bekanntes Boot ein!");
      boot.requestFocus();
      return;
    }
    if (Daten.efaConfig.efaDirekt_eintragNurBekannteRuderer && unbekannterName(stm,Daten.fahrtenbuch.getDaten().mitglieder)) {
      Dialog.error("Person '"+stm.getText().trim()+"' ist unbekannt. Bitte trage eine bekannte Person ein!");
      stm.requestFocus();
      return;
    }
    for (int i=0; i<mannsch.length; i++) {
      if (Daten.efaConfig.efaDirekt_eintragNurBekannteRuderer && unbekannterName(mannsch[i],Daten.fahrtenbuch.getDaten().mitglieder)) {
        Dialog.error("Person '"+mannsch[i].getText().trim()+"' ist unbekannt. Bitte trage eine bekannte Person ein!");
        mannsch[i].requestFocus();
        return;
      }
    }
    if (Daten.efaConfig.efaDirekt_eintragNurBekannteZiele && unbekannterName(ziel,Daten.fahrtenbuch.getDaten().ziele)) {
      Dialog.error("Das Ziel '"+ziel.getText().trim()+"' ist unbekannt. Bitte trage ein bekanntes Ziel ein!");
      ziel.requestFocus();
      return;
    }

    // ***Ersetzt durch Konfigurieren der Mehrtagesfahrt direkt durch das Mitglied***
    // ***Folgende beide Variablen werden nicht mehr genutzt und bleiben auf "null" initialisiert***
    String enddatum=null,rudertage=null;

    if (mode == MODE_START || mode == MODE_START_KORREKTUR) {
      if (ziel.getText().trim().length()==0 && Daten.efaConfig.efaDirekt_zielBeiFahrtbeginnPflicht) {
        Dialog.error("Bitte trage ein voraussichtliches Fahrtziel ein!");
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
            erlaubteGruppen = (erlaubteGruppen == null ? (String)g.get(j) : erlaubteGruppen + (j+1<g.size() ? ", "+g.get(j) : " und "+g.get(j)) );
          }
          switch (Dialog.auswahlDialog("Boot nur für bestimmte Gruppen freigegeben",
                                 "Dieses Boot dürfen nur "+erlaubteGruppen+" rudern.\n"+
                                 (nichtErlaubtAnz == 1 ? "Folgender Ruderer gehört diesen Gruppen nicht an und darf das Boot daher nicht rudern:\n" :
                                                         "Folgende Ruderer gehören diesen Gruppen nicht an und dürfen das Boot daher nicht rudern:\n") +
                                 nichtErlaubt+"\n"+
                                 "Was möchtest Du tun?",
                                 "Anderes Boot wählen",
                                 "Mannschaft ändern",
                                 "Trotzdem rudern",
                                 "Eintrag abbrechen")) {
            case 0:
              this.setFieldEnabled(true,this.boot,this.bootLabel);
              this.boot.setText("");
              this.boot.requestFocus();
              return;
            case 1:
              this.mannsch[0].requestFocus();
              return;
            case 2:
              Logger.log(Logger.INFO,"Unerlaubte Benutzung eines Bootes: LfdNr="+lfdnr.getText().trim()+" Boot="+boot.getText().trim()+" Mannschaft="+pers);
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
          switch (Dialog.auswahlDialog("Boot erfordert bestimmte Berechtigung",
                                 "In diesem Boot muß mindestens ein Mitglied der Gruppe "+gruppe+" sitzen.\n"+
                                 "Was möchtest Du tun?",
                                 "Anderes Boot wählen",
                                 "Mannschaft ändern",
                                 "Trotzdem rudern",
                                 "Eintrag abbrechen")) {
            case 0:
              this.setFieldEnabled(true,this.boot,this.bootLabel);
              this.boot.setText("");
              this.boot.requestFocus();
              return;
            case 1:
              this.mannsch[0].requestFocus();
              return;
            case 2:
              Logger.log(Logger.INFO,"Unerlaubte Benutzung eines Bootes: LfdNr="+lfdnr.getText().trim()+" Boot="+boot.getText().trim()+" Mannschaft="+pers);
              break;
            case 3:
              cancel();
              return;
          }
        }
      }


      if (mode == MODE_START) Logger.log(Logger.INFO,"Fahrtbeginn: #"+lfdnr.getText()+" - "+boot.getText()+" mit "+pers);
      else Logger.log(Logger.INFO,"Fahrtbeginn korrigiert: #"+lfdnr.getText()+" - "+boot.getText()+" mit "+pers);
      this.bootskm.setText(""); this.mannschkm.setText(""); this.ankunft.setText(""); // wenn alle diese Werte "" sind, gilt der Eintrag als noch nicht zurückgetragen
      speichereDatensatzInFB(true,null,null,null);
      if (!Daten.fahrtenbuch.writeFileOnlyLastRecordChanged()) {
        Logger.log(Logger.ERROR,"Fahrtenbuch kann nicht geschrieben werden!");
        Dialog.error("Fahrtenbuch kann nicht geschrieben werden! Bitte dem Administration bescheid sagen!");
      }
      if (mode == MODE_START) {
        efaDirektFrame.fahrtBegonnen(boot.getText().trim(),lfdnr.getText().trim(),datum.getText().trim(),abfahrt.getText().trim(),pers,(String)fahrtDauer.getSelectedItem(),ziel.getText().trim());
      } else {
        // Fahrtbeginn korrigiert
        if (direktBootOriginalName.equals(boot.getText().trim())) {
          // Bootsname nicht geändert
          efaDirektFrame.fahrtBeginnKorrigiert(direkt_boot,lfdnr.getText().trim(),datum.getText().trim(),abfahrt.getText().trim(),pers,(String)fahrtDauer.getSelectedItem(),ziel.getText().trim(),direkt_boot);
        } else {
          // Bootsname wurde geändert
          efaDirektFrame.fahrtBeginnKorrigiert(boot.getText().trim(),lfdnr.getText().trim(),datum.getText().trim(),abfahrt.getText().trim(),pers,(String)fahrtDauer.getSelectedItem(),ziel.getText().trim(),direkt_boot);
        }
      }
    } else { // MODE_ENDE oder MODE_NACHTRAG
      if (ziel.getText().trim().length()==0 && !Daten.efaConfig.skipZiel) {
        Dialog.error("Bitte trage ein Fahrtziel ein!");
        ziel.requestFocus();
        return;
      }
      String km = bootskm.getText().trim();
      if (km.length()==0 || EfaUtil.zehntelString2Int(km)==0) {
        Dialog.error("Bitte trage die geruderten Kilometer ein!");
        bootskm.requestFocus();
        return;
      }

      // Mehrtagesfahrt?
      if (fahrtDauer.getSelectedItem().toString().equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_MEHRTAGESFAHRT))) {
        mtourName = WanderfahrtSelectFrame.selectWanderfahrt(this,lfdnr.getText().trim(),datum.getText().trim(),boot.getText().trim(),stmMannsch2String(),ziel.getText().trim());
        if (mtourName == null) return;
        Mehrtagesfahrt mf = Daten.fahrtenbuch.getMehrtagesfahrt(mtourName);
        if (mf != null && mf.start != null && mf.start.length()>0) this.datum.setText(mf.start);
      }


      if (mode == MODE_ENDE) {
        Logger.log(Logger.INFO,"Fahrtende: #"+lfdnr.getText()+" - "+boot.getText()+" mit "+pers+"; Abfahrt: "+abfahrt.getText()+", Ankunft: "+ankunft.getText()+", Ziel: "+ziel.getText()+"; Km: "+bootskm.getText());
        Daten.fahrtenbuch.delete(this.lfdnr.getText().trim());
        speichereDatensatzInFB(false,enddatum,rudertage,mtourName);
        if (!Daten.fahrtenbuch.writeFile()) {
          Logger.log(Logger.ERROR,"Fahrtenbuch kann nicht geschrieben werden!");
          Dialog.error("Fahrtenbuch kann nicht geschrieben werden! Bitte dem Administration bescheid sagen!");
        }
        efaDirektFrame.fahrtBeendet(direkt_boot,true);
      } else if (mode == MODE_NACHTRAG) {
        Logger.log(Logger.INFO,"Nachtrag: #"+lfdnr.getText()+" - "+boot.getText()+" mit "+pers+"; Abfahrt: "+abfahrt.getText()+", Ankunft: "+ankunft.getText()+", Ziel: "+ziel.getText()+"; Km: "+bootskm.getText());
        speichereDatensatzInFB(true,enddatum,rudertage,mtourName);
        // wir müssen hier writeFile() verwenden anstatt writeFileOnlyLastRecordChanged(),
        // da bei einem Nachtrag (ebenso wie beim Beenden von Fahrten) u.U. auch die Liste der Mehrtagesfahrten
        // am Anfang der Datei mit verändert wird.
        if (!Daten.fahrtenbuch.writeFile()) {
          Logger.log(Logger.ERROR,"Fahrtenbuch kann nicht geschrieben werden!");
          Dialog.error("Fahrtenbuch kann nicht geschrieben werden! Bitte dem Administration bescheid sagen!");
        }
        efaDirektFrame.fahrtNachgetragen();
      } else {
        Logger.log(Logger.ERROR,"Programmfehler: Unerwarteter Modus ["+mode+"] für direktSpeichereDatensatz()!");
        Dialog.error("Programmfehler: Unerwarteter Modus ["+mode+"] für direktSpeichereDatensatz()! Bitte dem Administration bescheid sagen!");
      }
    }

/* ***Ersetzt durch Konfigurieren der Mehrtagesfahrt direkt durch das Mitglied***
    if (enddatum != null && rudertage != null && nachrichtenAnAdmin != null) {
      nachrichtenAnAdmin.add(new Nachricht(Nachricht.ADMIN,
             EfaUtil.getCurrentTimeStamp(),"efa","Neue Mehrtagesfahrt",
             "Es wurde eine neue Mehrtagesfahrt in efa eingetragen; bitte füge diese Mehrtagesfahrt\n"+
             "zur Liste der Mehrtagesfahrten hinzu bzw. ordne dem Fahrtenbucheintrag die richtige\n"+
             "Mehrtagesfahrt zu.\n"+
             "LfdNr: #"+lfdnr.getText()+"\n"+
             "Ziel: "+ziel.getText()+"\n"+
             "Enddatum: "+enddatum+"\n"+
             "Rudertage: "+rudertage));
      nachrichtenAnAdmin.writeFile();
    }
*/

    cancel();
  }


  void showHint(String s) {
    if (s == null) {
      infoLabel.setText("");
      return;
    }
/*
    if (s.equals("datum")) infoLabel.setText("Datum der Fahrt: <Tag>.<Monat>.<Jahr>");
    if (s.equals("boot")) infoLabel.setText("Name des Bootes");
    if (s.equals("stm")) infoLabel.setText("Name des Steuermanns / der Steuerfrau: "+
       (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().erstVorname ? "<Vorname> <Nachname>" : "<Nachname>, <Vorname>"));
    if (s.equals("mannsch")) infoLabel.setText("Namen der Mannschaft: "+
       (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().erstVorname ? "<Vorname> <Nachname>" : "<Nachname>, <Vorname>"));
    if (s.equals("abfahrt")) infoLabel.setText("Abfahrszeit: <Stunde>:<Minute>");
    if (s.equals("ankunft")) infoLabel.setText("Ankunftszeit: <Stunde>:<Minute>");
    if (s.equals("ziel")) infoLabel.setText("Ziel der Fahrt");
    if (s.equals("bootskm")) infoLabel.setText("Gesamte geruderte Strecke in Kilometern");
    if (s.equals("bemerk")) infoLabel.setText("Bemerkungen, Bootsschäden usw.");
    if (s.equals("obmann")) infoLabel.setText("Auswahl des verantwortlichen Obmanns");
    if (s.equals("fahrtDauer")) infoLabel.setText("Auswahl der Art der Fahrt");
    if (s.equals("weitereMannschButton")) infoLabel.setText("weitere Mannschaftsfelder anzeigen");
*/
    if (s.equals("datum")) infoLabel.setText("Bitte eingeben: <Tag>.<Monat>.<Jahr>");
    if (s.equals("boot")) infoLabel.setText("Bitte eingeben: <Bootsname>");
    if (s.equals("stm") || s.equals("mannsch")) infoLabel.setText("Bitte eingeben: "+
       (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().erstVorname ? "<Vorname> <Nachname>" : "<Nachname>, <Vorname>"));
    if (s.equals("abfahrt") || s.equals("ankunft")) infoLabel.setText("Bitte eingeben: <Stunde>:<Minute>");
    if (s.equals("ziel")) infoLabel.setText("Bitte eingeben: <Ziel der Fahrt>");
    if (s.equals("bootskm")) infoLabel.setText("Bitte eingeben: <Kilometer>");
    if (s.equals("bemerk")) infoLabel.setText("Bemerkungen eingeben oder frei lassen");
    if (s.equals("obmann")) infoLabel.setText("Bitte auswählen: verantwortlichen Obmann");
    if (s.equals("fahrtDauer")) infoLabel.setText("Bitte auswählen: Art der Fahrt");
    if (s.equals("weitereMannschButton")) infoLabel.setText("weitere Mannschaftsfelder anzeigen");
    if (s.equals("bootsschadenButton")) infoLabel.setText("einen Schaden am Boot melden");
    if (s.equals("addButton")) infoLabel.setText("<Leertaste> drücken, um den Eintrag abzuschließen");
  }

  void bootsschadenButton_actionPerformed(ActionEvent e) {
    if (efaDirektFrame == null || isAdminMode()) return;

    NachrichtAnAdminFrame dlg = new NachrichtAnAdminFrame(this,Daten.nachrichten,Nachricht.BOOTSWART,
                      (getObmannTextField(getObmann()) != null ? getObmannTextField(getObmann()).getText() : null),
                      "Bootsschaden",
                      "LfdNr: "+lfdnr.getText()+"\n"+
                      "Datum: "+datum.getText()+"\n"+
                      "Boot: "+boot.getText()+"\n"+
                      "Mannschaft: "+stmMannsch2String()+"\n"+
                      "-----------------------\n"+
                      "Beschreibung des Schadens:\n");
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    if (dlg.isGesendet()) {
      String s = this.bemerk.getText().trim();
      this.bemerk.setText(s + (s.length()>0 ? "; " : "") + "Bootsschaden gemeldet.");
      Nachricht n = dlg.getLastMessage();
      if (n != null && n.nachricht != null) {
        int pos = n.nachricht.indexOf("Beschreibung des Schadens:");
        if (pos >= 0) {
          String t = n.nachricht.substring(pos+26);
          t = EfaUtil.replace(t,"\n"," ",true).trim();
          if (t.length() > 0) {
            efaDirektFrame.setBootstatusSchaden(boot.getText().trim(),t);
          }
        }
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
