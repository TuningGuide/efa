package de.nmichael.efa.eddi;

import de.nmichael.efa.core.EfaConfigFrame;
import de.nmichael.efa.core.Mitglieder;
import de.nmichael.efa.core.Ziele;
import de.nmichael.efa.core.EfaFrame;
import de.nmichael.efa.core.Boote;
import de.nmichael.efa.core.Bezeichnungen;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.ActionHandler;
import de.nmichael.efa.util.ZielfahrtFolge;
import de.nmichael.efa.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.beans.*;

public class EddiFrame extends JFrame {
  public boolean wait;
  Vector lines = null;
  Vector fields = null;
  int lineNr = -1;
  char sep;
  boolean abort;
  boolean skip;
  Vector mitgliederCombos = null;
  Vector mitgliederValues = null;
  Vector bootsCombos = null;
  Vector bootsValues = null;
  Vector zielCombos = null;
  Vector zielValues = null;
  String currentDirectory = null;

  JMenuBar jMenuBar1 = new JMenuBar();
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextField datei = new JTextField();
  JButton openButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField fieldSeparator = new JTextField();
  JPanel jPanel2 = new JPanel();
  JButton importButton = new JButton();
  JPanel jPanel3 = new JPanel();
  JTabbedPane tabbedPane = new JTabbedPane();
  JLabel jLabel3 = new JLabel();
  JLabel startzeileLabel = new JLabel();
  JLabel zeileLabel = new JLabel();
  JButton nextLineButton = new JButton();
  JButton PrevLineButton = new JButton();
  JButton readFileButton = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel fieldPanel;
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JPanel mitgliederPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel8 = new JLabel();
  JPanel bootsPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JPanel zielPanel = new JPanel();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JMenu jMenu1 = new JMenu();
  JMenuItem menuFileOpen = new JMenuItem();
  JMenuItem menuImport = new JMenuItem();
  JMenuItem menuExit = new JMenuItem();
  JMenu menuHelp = new JMenu();
  JMenuItem jMenuItem4 = new JMenuItem();
  JMenuItem menuAbout = new JMenuItem();
  JCheckBox aliasCheckBox = new JCheckBox();
  JTextField alias = new JTextField();


  //Construct the frame
  public EddiFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    datei.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
//    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      // nothing
//    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
  }


  //Component initialization
  private void jbInit() throws Exception  {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    setIconImage(Toolkit.getDefaultToolkit().createImage(EfaFrame.class.getResource("/de/nmichael/efa/img/efa_icon.gif")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(850, 550));
    this.setTitle("eddi - efa Datenlisten-Importeur");
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setDisplayedMnemonic('T');
    jLabel1.setLabelFor(fieldSeparator);
    jLabel1.setText("Trennzeichen: ");
    jLabel2.setDisplayedMnemonic('N');
    jLabel2.setLabelFor(datei);
    jLabel2.setText("Dateiname: ");
    importButton.setMnemonic('M');
    importButton.setText("Datenliste importieren");
    importButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        importButton_actionPerformed(e);
      }
    });
    datei.setNextFocusableComponent(openButton);
    datei.setPreferredSize(new Dimension(400, 17));
    datei.setToolTipText("Name der zu importierenden Datei");
    openButton.setNextFocusableComponent(readFileButton);
    openButton.setPreferredSize(new Dimension(59, 20));
    openButton.setToolTipText("Datei auswählen");
    openButton.setIcon(new ImageIcon(EddiFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    openButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openButton_actionPerformed(e);
      }
    });
    fieldSeparator.setPreferredSize(new Dimension(20, 17));
    fieldSeparator.setToolTipText("Zeichen, welches die einzelnen Felder trennt");
    fieldSeparator.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        fieldSeparator_keyReleased(e);
      }
    });
    jPanel3.setLayout(borderLayout2);
    jLabel3.setPreferredSize(new Dimension(280, 13));
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText("Felder:");
    startzeileLabel.setText("Startzeile: ");
    zeileLabel.setBackground(Color.blue);
    zeileLabel.setForeground(Color.white);
    zeileLabel.setOpaque(true);
    zeileLabel.setPreferredSize(new Dimension(400, 20));
    zeileLabel.setText("---bitte zuerst Datei auswählen---");
    nextLineButton.setNextFocusableComponent(fieldSeparator);
    nextLineButton.setPreferredSize(new Dimension(89, 20));
    nextLineButton.setToolTipText("nächste Zeile auswählen");
    nextLineButton.setMnemonic('C');
    nextLineButton.setText("nächste");
    nextLineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nextLineButton_actionPerformed(e);
      }
    });
    PrevLineButton.setNextFocusableComponent(nextLineButton);
    PrevLineButton.setPreferredSize(new Dimension(103, 20));
    PrevLineButton.setToolTipText("vorherige Zeile auswählen");
    PrevLineButton.setMnemonic('V');
    PrevLineButton.setText("vorherige");
    PrevLineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PrevLineButton_actionPerformed(e);
      }
    });
    readFileButton.setNextFocusableComponent(PrevLineButton);
    readFileButton.setPreferredSize(new Dimension(95, 20));
    readFileButton.setToolTipText("Datei einlesen");
    readFileButton.setMnemonic('E');
    readFileButton.setText("einlesen");
    readFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        readFileButton_actionPerformed(e);
      }
    });
    mitgliederPanel.setLayout(gridBagLayout3);
    jLabel8.setText("jLabel8");
    jPanel3.setBorder(BorderFactory.createRaisedBevelBorder());
    bootsPanel.setLayout(gridBagLayout4);
    zielPanel.setLayout(gridBagLayout5);
    jMenu1.setMnemonic('D');
    jMenu1.setText("Datei");
    menuFileOpen.setMnemonic('F');
    menuFileOpen.setText("Datei öffnen");
    menuFileOpen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuFileOpen_actionPerformed(e);
      }
    });
    menuImport.setMnemonic('I');
    menuImport.setText("Datenliste importieren");
    menuImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuImport_actionPerformed(e);
      }
    });
    menuExit.setMnemonic('B');
    menuExit.setText("Beenden");
    menuExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuExit_actionPerformed(e);
      }
    });
    menuHelp.setMnemonic('I');
    menuHelp.setText("Info");
    jMenuItem4.setMnemonic('H');
    jMenuItem4.setText("Hilfe");
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem4_actionPerformed(e);
      }
    });
    menuAbout.setMnemonic('B');
    menuAbout.setText("Über");
    menuAbout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuAbout_actionPerformed(e);
      }
    });
    contentPane.setMinimumSize(new Dimension(600, 165));
    contentPane.setPreferredSize(new Dimension(800, 147));
    aliasCheckBox.setText("Generiere Eingabekürzel: ");
    alias.setPreferredSize(new Dimension(150, 17));
    alias.setText("{V1}{V2}-{N1}");
    contentPane.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(datei,    new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(openButton,      new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel1,        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel2,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(fieldSeparator,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(startzeileLabel,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(zeileLabel,   new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    contentPane.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(importButton, null);
    contentPane.add(jPanel3, BorderLayout.WEST);
    jPanel3.add(jLabel3,  BorderLayout.NORTH);
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.add(mitgliederPanel,  "Mitgliederliste");
    mitgliederPanel.add(aliasCheckBox,   new GridBagConstraints(0, 20, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mitgliederPanel.add(alias,  new GridBagConstraints(1, 20, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    tabbedPane.add(bootsPanel,  "Bootsliste");
    jPanel1.add(nextLineButton,      new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(PrevLineButton,    new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(readFileButton,   new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    tabbedPane.add(zielPanel,  "Zielliste");
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(menuHelp);
    jMenu1.add(menuFileOpen);
    jMenu1.add(menuImport);
    jMenu1.add(menuExit);
    menuHelp.add(jMenuItem4);
    menuHelp.add(menuAbout);
    this.setJMenuBar(jMenuBar1);

    iniSelectFields();
  }



  // Initialisieren der Combos und Labels für ein speziellen Panel
  void _iniSelectFields(String[] feldnamen, Vector combos, Vector values, JPanel panel, int l) {
    for (int i=0; i<feldnamen.length; i++) {
      JLabel label = new JLabel();
      label.setText(feldnamen[i]+": ");
      panel.add(label,   new GridBagConstraints(0, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      JComboBox combo = new JComboBox();
      combo.setPreferredSize(new Dimension(100,20));
      combo.setMaximumRowCount(10);
      panel.add(combo,    new GridBagConstraints(1, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      JLabel value = new JLabel();
      value.setForeground(Color.white);
      value.setBackground(Color.blue);
      value.setOpaque(true);
      value.setPreferredSize(new Dimension(200,20));
      panel.add(value,    new GridBagConstraints(2, i, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

      combos.add(combo);
      values.add(value);

      if (l == 0)
        combo.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            combo_actionPerformed(e,mitgliederCombos,mitgliederValues);
          }
        });
      if (l == 1)
        combo.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            combo_actionPerformed(e,bootsCombos,bootsValues);
          }
        });
      if (l == 2)
        combo.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            combo_actionPerformed(e,zielCombos,zielValues);
          }
        });

    }
  }


  // Initialisieren der Combos und Labels
  void iniSelectFields() {
    String[] mitglieder = { "Vorname", "Nachname", "Eingabekürzel", "Jahrgang", "Geschlecht", "Status", "Verein", "Behinderung", "MitglNr", "Paßwort", "Frei 1", "Frei 2", "Frei 3" };
    String[] boote = { "Bootsname", "Verein", "Bootsart", "Anzahl Ruderplätze", "Riggerung", "mit/ohne Stm.", "Gruppen", "Max. nicht in Gruppe", "Mind. in Gruppe", "Frei 1", "Frei 2", "Frei 3" };
    String[] ziele = { "Bezeichnung", "Entfernung", "Zielbereiche", "Start/Ziel ist Bootshaus", "Gewässer" };
    mitgliederCombos = new Vector();
    mitgliederValues = new Vector();
    _iniSelectFields(mitglieder,mitgliederCombos,mitgliederValues,mitgliederPanel,0);
    bootsCombos = new Vector();
    bootsValues = new Vector();
    _iniSelectFields(boote,bootsCombos,bootsValues,bootsPanel,1);
    zielCombos = new Vector();
    zielValues = new Vector();
    _iniSelectFields(ziele,zielCombos,zielValues,zielPanel,2);
  }


  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
  }


  // close
  void cancel() {
    Dialog.frameClosed(this);
    System.exit(0);
  }


  // Datei datei einlesen
  void readFile(String datei) {
    if (datei == null) return;
    if (!EfaUtil.canOpenFile(datei)) {
      Dialog.error("Datei "+datei+" kann nicht geöffnet werden.");
      return;
    }
    this.datei.setText(datei);
    try {
      BufferedReader f = new BufferedReader(new FileReader(datei));
      lines = new Vector();
      String s;
      while ( ( s = f.readLine()) != null) {
        lines.add(s);
      }
      f.close();
      lineNr = 0;
      fieldSeparator.setText("");
      showLine();
    } catch(IOException e) {
    }
  }


  // aktuelle Zeile anzeigen
  void showLine() {
    if (lines == null) return;
    if (lineNr<0) lineNr = 0;
    if (lineNr>=lines.size()) lineNr = lines.size()-1;
    if (lineNr<0) return;
    startzeileLabel.setText("Startzeile ("+(lineNr+1)+"): ");
    zeileLabel.setText((String)lines.get(lineNr));
    splitFields();
  }


  // Anführungsstriche aus Feldern entfernen
  void removeQuotes(Vector a) {
    if (a == null) return;
    for (int i=0; i<a.size(); i++) {
      String s = (String)a.get(i);
      if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
        a.set(i,s.substring(1,s.length()-1));
    }
  }


  // einen String anhand des Trennzeichens sep in einen Vector aufsplitten
  public Vector split(String s, char sep) {
    if (s == null) return null;
    Vector v = new Vector();
    String t = "";
    boolean inQuotes = false;
    for (int i=0; i<s.length(); i++) {
      if (s.charAt(i)==sep && !inQuotes) {
        v.add(t);
        t = "";
        continue;
      }
      if (s.charAt(i)=='"') inQuotes = !inQuotes;
      t += s.charAt(i);
    }
    v.add(t);
    removeQuotes(v);
    return v;
  }


  // aktuelle Zeile in einzelne Felder aufteilen
  void splitFields() {
    String seps = fieldSeparator.getText();
    String line = (String)lines.get(lineNr);
    if (seps.length()==0) {
      if (line.indexOf("|")>=0) seps = "|";
      else if (line.indexOf("\t")>=0) seps = "\t";
      else if (line.indexOf(";")>=0) seps = ";";
      else if (line.indexOf(",")>=0) seps = ",";
      fieldSeparator.setText(seps);
    }
    if (seps.length()==0) return;
    sep = seps.charAt(0);
    int oldfieldsize = -1;
    if (fields != null) oldfieldsize = fields.size();
    fields = split(line,sep);

    if (fieldPanel != null) jPanel3.remove(fieldPanel);
    fieldPanel = new JPanel();
    fieldPanel.setLayout(gridBagLayout2);
    jPanel3.add(fieldPanel, BorderLayout.CENTER);

    for (int i=0; i<fields.size(); i++) {
      JLabel label = new JLabel();
      label.setText("Feld "+(i+1)+": ");
      fieldPanel.add(label,   new GridBagConstraints(0, i, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      JLabel value = new JLabel();
      value.setText((String)fields.get(i));
      value.setForeground(Color.white);
      value.setBackground(Color.blue);
      value.setOpaque(true);
      value.setPreferredSize(new Dimension(200,20));
      fieldPanel.add(value,   new GridBagConstraints(1, i, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    if (oldfieldsize != fields.size()) {
      resetCombos(fields.size());
      resetComboFields();
    } else updateComboFields();
  }


  // Combos combos zurücksetzen
  void _resetCombos(Vector combos, int fields) {
    for (int i=0; i<combos.size(); i++) {
      JComboBox combo = (JComboBox)combos.get(i);
      combo.removeAllItems();
      combo.addItem("n/a");
      for (int j=0; j<fields; j++)
        combo.addItem("Feld "+(j+1));
    }
  }


  // alle Combos zurücksetzen
  void resetCombos(int fields) {
    try {
      _resetCombos(mitgliederCombos,fields);
      _resetCombos(bootsCombos,fields);
      _resetCombos(zielCombos,fields);
    } catch(Exception e) { EfaUtil.foo(); }
  }


  // ComboLabels values zurücksetzen
  void _resetComboFields(Vector values) {
    for (int i=0; i<values.size(); i++) {
      JLabel label = (JLabel)values.get(i);
      label.setText("");
    }
  }


  // alle ComboLabels zurücksetzen
  void resetComboFields() {
    try {
      _resetComboFields(mitgliederValues);
      _resetComboFields(bootsValues);
      _resetComboFields(zielValues);
    } catch(Exception e) { EfaUtil.foo(); }
  }


  // ComboLabels combos/values aktualisieren
  void _updateComboFields(Vector combos, Vector values) {
    for (int i=0; i<values.size(); i++) {
      JComboBox combo = (JComboBox)combos.get(i);
      JLabel label = (JLabel)values.get(i);
      if (combo.getSelectedIndex()>0) label.setText((String)fields.get(combo.getSelectedIndex()-1));
    }
  }


  // alle ComboLabels aktualisieren
  void updateComboFields() {
    try {
      _updateComboFields(mitgliederCombos,mitgliederValues);
      _updateComboFields(bootsCombos,bootsValues);
      _updateComboFields(zielCombos,zielValues);
    } catch(Exception e) { EfaUtil.foo(); }
  }


  // Menü: Datei->Öffnen
  void menuFileOpen_actionPerformed(ActionEvent e) {
    openButton_actionPerformed(null);
  }


  // Menü: Datei->Importieren
  void menuImport_actionPerformed(ActionEvent e) {
    importButton_actionPerformed(null);
  }


  // Menü: Datei->Beenden
  void menuExit_actionPerformed(ActionEvent e) {
    cancel();
  }


  // Menü: Info->Hilfe
  void jMenuItem4_actionPerformed(ActionEvent e) {
    Help.getHelp(this,"EddiFrame");
  }


  // Menü: Info->Über
  void menuAbout_actionPerformed(ActionEvent e) {
    EddiAboutFrame dlg = new EddiAboutFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }


  // Datei öffnen
  void openButton_actionPerformed(ActionEvent e) {
    if (currentDirectory == null || currentDirectory.length()==0) currentDirectory = System.getProperty("user.dir");
    String dat = Dialog.dateiDialog(this,null,null,null,currentDirectory,false);
    currentDirectory = EfaUtil.getPathOfFile(dat);
    readFile(dat);
  }


  // Datei einlesen
  void readFileButton_actionPerformed(ActionEvent e) {
    readFile(datei.getText().trim());
  }


  // Ändern des Separators
  void fieldSeparator_keyReleased(KeyEvent e) {
    if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE && e.getKeyCode() != KeyEvent.VK_DELETE) showLine();
  }


  // nächste Zeile auswählen
  void nextLineButton_actionPerformed(ActionEvent e) {
    lineNr++;
    showLine();
  }


  // vorherige Zeile auswählen
  void PrevLineButton_actionPerformed(ActionEvent e) {
    lineNr--;
    showLine();
  }


  // neues Feld in einer Combo auswählen
  void combo_actionPerformed(ActionEvent e, Vector combos, Vector labels) {
    JComboBox combo = (JComboBox)e.getSource();
    JLabel label = (JLabel)labels.get(combos.indexOf(combo));
    if (fields == null || combo.getSelectedIndex()<1) label.setText("");
    else label.setText((String)fields.get(combo.getSelectedIndex()-1));
  }


  // Liste importieren
  void importButton_actionPerformed(ActionEvent e) {
    if (lines == null) {
      Dialog.error("Bitte wähle zuerst eine Quelldatei aus!");
      return;
    }
    if (fields == null) {
      Dialog.error("Bitte wähle zuerst eine Startzeile aus!");
      return;
    }
    String dat = null;
    if (tabbedPane.getSelectedIndex() == 0)
      dat = Dialog.dateiDialog(this,"Dateiname für zu erstellende Mitgliederliste","Mitgliederliste (*.efbm)","efbm",Daten.efaDataDirectory,true);
    if (tabbedPane.getSelectedIndex() == 1)
      dat = Dialog.dateiDialog(this,"Dateiname für zu erstellende Bootsliste","Bootsliste (*.efbb)","efbb",Daten.efaDataDirectory,true);
    if (tabbedPane.getSelectedIndex() == 2)
      dat = Dialog.dateiDialog(this,"Dateiname für zu erstellende Zielliste","Zielliste (*.efbz)","efbz",Daten.efaDataDirectory,true);
    if (dat == null) return;
    if (tabbedPane.getSelectedIndex() == 0) importMitgliederliste(dat);
    if (tabbedPane.getSelectedIndex() == 1) importBootsliste(dat);
    if (tabbedPane.getSelectedIndex() == 2) importZielliste(dat);
  }


  // Mitgliederliste importieren
  void importMitgliederliste(String datei) {
    if (aliasCheckBox.isSelected()) {
      if (EfaConfigFrame.parseAlias(this.alias.getText().trim()) >= 0) {
        Dialog.error("Ungültiges Format des Eingabekürzels:\n"+EfaConfigFrame.parseError);
        return;
      }
    }
    if (!datei.toUpperCase().endsWith(".EFBM")) datei+=".efbm";
    Mitglieder f = new Mitglieder(datei);
    String[] feld = new String[13];
    int i;
    abort = false;
    skip = false;
    Hashtable replGeschl = new Hashtable();
    Hashtable replStatus = new Hashtable();
    Hashtable replBeh = new Hashtable();

    String status = Dialog.inputDialog("Status-Liste","Liste von Status, die ein Mitglied haben kann","Junior(in),Senior(in),Gast");
    if (status == null) return;
    status = EfaUtil.removeSepFromString(status);
    String[] statusList = EfaUtil.statusList2Arr(status.trim());

    for (int l=lineNr; l<lines.size(); l++) {
      fields = EfaUtil.split((String)lines.get(l),sep);
      removeQuotes(fields);
      for (int j=0; j<feld.length; j++) feld[j] = "";
      for (int j=0; j<feld.length; j++)
        if ( (i=((JComboBox)mitgliederCombos.get(j)).getSelectedIndex()) > 0 && i-1<fields.size()) feld[j] = (String)fields.get(i-1);

      // Werte ggf. korrigieren
      for (int j=0; j<feld.length; j++) {
        feld[j] = EfaUtil.removeSepFromString(feld[j]).trim();
      }
      TMJ tmj = EfaUtil.string2date(feld[Mitglieder.JAHRGANG],0,0,0);
      int jahr;
      if (tmj.jahr != 0) jahr = tmj.jahr;
      else jahr = tmj.tag;
      if (jahr != 0 && jahr<100) jahr+=1900;
      if (jahr != 0) feld[Mitglieder.JAHRGANG] = Integer.toString(jahr);
      if (replGeschl.get(feld[Mitglieder.GESCHLECHT]) != null) feld[Mitglieder.GESCHLECHT] = (String)replGeschl.get(feld[Mitglieder.GESCHLECHT]);
      if (replStatus.get(feld[Mitglieder.STATUS]) != null) feld[Mitglieder.STATUS] = (String)replStatus.get(feld[Mitglieder.STATUS]);
      if (replBeh.get(feld[Mitglieder.BEHINDERUNG]) != null) feld[Mitglieder.BEHINDERUNG] = (String)replBeh.get(feld[Mitglieder.BEHINDERUNG]);

      if (aliasCheckBox.isSelected() && feld[Mitglieder.ALIAS].length()==0) {
        feld[Mitglieder.ALIAS] = EfaUtil.genAlias(alias.getText().trim(),feld[Mitglieder.VORNAME],feld[Mitglieder.NACHNAME],feld[Mitglieder.VEREIN]);
      }

      String fehler;
      do {

        // Fehlerhafter Eintrag?
        fehler = "";
        if (feld[Mitglieder.VORNAME].length()==0 && feld[Mitglieder.NACHNAME].length()==0)
          fehler += "Vor- und Nachname dürfen nicht leer sein!\n";
        if (!feld[Mitglieder.GESCHLECHT].equals(Daten.bezeichnungen.geschlecht.get(Bezeichnungen.GESCHLECHT_MAENNLICH)) &&
            !feld[Mitglieder.GESCHLECHT].equals(Daten.bezeichnungen.geschlecht.get(Bezeichnungen.GESCHLECHT_WEIBLICH)))
          fehler += "Ungültiger Wert im Feld 'Geschlecht': "+feld[Mitglieder.GESCHLECHT]+"\n";
        boolean statusOk = false;
        for (int k=0; k<statusList.length; k++) if (feld[Mitglieder.STATUS].equals(statusList[k])) statusOk = true;
        if (!statusOk) fehler += "Ungültiger Wert im Feld 'Status': "+feld[Mitglieder.STATUS]+"\n";
        if (!(feld[Mitglieder.BEHINDERUNG].length()==0) && !feld[Mitglieder.BEHINDERUNG].equals("+") && !feld[Mitglieder.BEHINDERUNG].equals("-"))
          fehler += "Ungültiger Wert im Feld 'Behinderung': "+feld[Mitglieder.BEHINDERUNG]+"\n";

        if (fehler.length()>0) {
          MitgliederFehlerFrame dlg = new MitgliederFehlerFrame(this,(String)lines.get(l),fehler,feld,statusList,replGeschl,replStatus,replBeh);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (abort) {
            Dialog.meldung("Import abgebrochen.");
            return;
          }
        }

      } while(fehler.length() != 0 && !skip);


      if (!skip) f.add(feld[0]+"|"+feld[1]+"|"+feld[2]+"|"+feld[3]+"|"+feld[4]+"|"+feld[5]+"|"+feld[6]+"|"+feld[7]+"|"+feld[8]+"|"+feld[9]+"|"+feld[10]+"|"+feld[11]+"|"+feld[12]+"|+");
      skip = false;
    }
    f.writeFile();
    Dialog.meldung("Mitgliederliste erfolgreich importiert!");
  }


  // Bootsliste importieren
  void importBootsliste(String datei) {
    if (!datei.toUpperCase().endsWith(".EFBB")) datei+=".efbb";
    Boote f = new Boote(datei);
    String[] feld = new String[12];
    int i;
    abort = false;
    skip = false;
    Hashtable replArt = new Hashtable();
    Hashtable replAnzahl = new Hashtable();
    Hashtable replRigger = new Hashtable();
    Hashtable replStm = new Hashtable();

    for (int l=lineNr; l<lines.size(); l++) {
      fields = EfaUtil.split((String)lines.get(l),sep);
      removeQuotes(fields);
      for (int j=0; j<feld.length; j++) feld[j] = "";
      for (int j=0; j<feld.length; j++)
        if ( (i=((JComboBox)bootsCombos.get(j)).getSelectedIndex()) > 0 && i-1<fields.size()) feld[j] = (String)fields.get(i-1);

      // Werte ggf. korrigieren
      for (int j=0; j<feld.length; j++) {
        feld[j] = EfaUtil.removeSepFromString(feld[j]).trim();
      }
      if (replArt.get(feld[Boote.ART]) != null) feld[Boote.ART] = (String)replArt.get(feld[Boote.ART]);
      if (replAnzahl.get(feld[Boote.ANZAHL]) != null) feld[Boote.ANZAHL] = (String)replAnzahl.get(feld[Boote.ANZAHL]);
      if (replRigger.get(feld[Boote.RIGGER]) != null) feld[Boote.RIGGER] = (String)replRigger.get(feld[Boote.RIGGER]);
      if (replStm.get(feld[Boote.STM]) != null) feld[Boote.STM] = (String)replStm.get(feld[Boote.STM]);

      feld[Boote.GRUPPEN] = EfaUtil.replace(feld[Boote.GRUPPEN],",",";",true);
      feld[Boote.MAX_NICHT_IN_GRUPPE] = Integer.toString(EfaUtil.string2int(feld[Boote.MAX_NICHT_IN_GRUPPE],0));

      String fehler;
      do {

        // Fehlerhafter Eintrag?
        fehler = "";
        if (feld[Boote.NAME].length()==0 )
          fehler += "Bootsname darf nicht leer sein!\n";
        boolean ok = false;
        for (int k=0; k<Daten.bezeichnungen.bArt.size(); k++) if (feld[Boote.ART].equals(Daten.bezeichnungen.bArt.get(k))) ok = true;
        if (!ok) fehler += "Ungültiger Wert im Feld 'Bootsart': "+feld[Boote.ART]+"\n";
        ok = false;
        for (int k=0; k<Daten.bezeichnungen.bAnzahl.size(); k++) if (feld[Boote.ANZAHL].equals(Daten.bezeichnungen.bAnzahl.get(k))) ok = true;
        if (!ok) fehler += "Ungültiger Wert im Feld 'Anzahl Ruderplätze': "+feld[Boote.ANZAHL]+"\n";
        ok = false;
        for (int k=0; k<Daten.bezeichnungen.bRigger.size(); k++) if (feld[Boote.RIGGER].equals(Daten.bezeichnungen.bRigger.get(k))) ok = true;
        if (!ok) fehler += "Ungültiger Wert im Feld 'Riggerung': "+feld[Boote.RIGGER]+"\n";
        ok = false;
        for (int k=0; k<Daten.bezeichnungen.bStm.size(); k++) if (feld[Boote.STM].equals(Daten.bezeichnungen.bStm.get(k))) ok = true;
        if (!ok) fehler += "Ungültiger Wert im Feld 'mit/ohne Stm.': "+feld[Boote.STM]+"\n";

        if (fehler.length()>0) {
          BootFehlerFrame dlg = new BootFehlerFrame(this,(String)lines.get(l),fehler,feld,replArt,replAnzahl,replRigger,replStm);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (abort) {
            Dialog.meldung("Import abgebrochen.");
            return;
          }
        }

      } while(fehler.length() != 0 && !skip);


      if (!skip) f.add(feld[0]+"|"+feld[1]+"|"+feld[2]+"|"+feld[3]+"|"+feld[4]+"|"+feld[5]+"|"+feld[6]+"|"+feld[7]+"|"+feld[8]+"|"+feld[9]+"|"+feld[10]+"|"+feld[11]);
      skip = false;
    }
    f.writeFile();
    Dialog.meldung("Bootsliste erfolgreich importiert!");
  }


  // Zielliste importieren
  void importZielliste(String datei) {
    if (!datei.toUpperCase().endsWith(".EFBZ")) datei+=".efbz";
    Ziele f = new Ziele(datei);
    String[] feld = new String[5];
    int i;
    abort = false;
    skip = false;
    Hashtable replStegZiel = new Hashtable();

    for (int l=lineNr; l<lines.size(); l++) {
      fields = EfaUtil.split((String)lines.get(l),sep);
      removeQuotes(fields);
      for (int j=0; j<feld.length; j++) feld[j] = "";
      for (int j=0; j<feld.length; j++)
        if ( (i=((JComboBox)zielCombos.get(j)).getSelectedIndex()) > 0 && i-1<fields.size()) feld[j] = (String)fields.get(i-1);

      // Werte ggf. korrigieren
      for (int j=0; j<feld.length; j++) {
        feld[j] = EfaUtil.removeSepFromString(feld[j]).trim();
      }
      TMJ tmj = EfaUtil.string2date(feld[Ziele.KM],-1,0,0);
      feld[Ziele.KM] = Integer.toString(tmj.tag) + (tmj.monat>0 ? "."+tmj.monat : "");
      if (tmj.tag==-1) feld[Ziele.KM] = "";
      if (feld[Ziele.BEREICH].length()>0) feld[Ziele.BEREICH] = new ZielfahrtFolge(feld[Ziele.BEREICH]).toString();
      if (tmj.tag<20) feld[Ziele.BEREICH] = "";

      if (replStegZiel.get(feld[Ziele.STEGZIEL]) != null) feld[Ziele.STEGZIEL] = (String)replStegZiel.get(feld[Ziele.STEGZIEL]);

      String fehler;
      do {

        // Fehlerhafter Eintrag?
        fehler = "";
        if (feld[Ziele.NAME].length()==0 )
          fehler += "Zielbezeichnung darf nicht leer sein!\n";
        if (feld[Ziele.KM].length()==0 )
          fehler += "Kilometer dürfen nicht leer sein!\n";
        if (!(feld[Ziele.STEGZIEL].length()==0) && !feld[Ziele.STEGZIEL].equals("+") && !feld[Ziele.STEGZIEL].equals("-"))
          fehler += "Ungültiger Wert im Feld 'Start/Ziel ist Bootshaus': "+feld[Ziele.STEGZIEL]+"\n";

        if (fehler.length()>0) {
          ZielFehlerFrame dlg = new ZielFehlerFrame(this,(String)lines.get(l),fehler,feld,replStegZiel);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (abort) {
            Dialog.meldung("Import abgebrochen.");
            return;
          }
        }

      } while(fehler.length() != 0 && !skip);


      if (!skip) f.add(feld[0]+"|"+feld[1]+"|"+feld[2]+"|"+feld[3]+"|"+feld[4]);
      skip = false;
    }
    f.writeFile();
    Dialog.meldung("Zielliste erfolgreich importiert!");
  }



}
