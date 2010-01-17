/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.eddi;

import de.nmichael.efa.*;
import de.nmichael.efa.core.*;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.beans.*;

// @i18n complete

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
    this.setTitle("eddi - "+International.getString("efa Datenlisten-Importeur"));
    jPanel1.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Trennzeichen")+": ");
    jLabel1.setLabelFor(fieldSeparator);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Dateiname")+": ");
    jLabel2.setLabelFor(datei);
    Mnemonics.setButton(this, importButton, International.getStringWithMnemonic("Datenliste importieren"));
    importButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        importButton_actionPerformed(e);
      }
    });
    datei.setNextFocusableComponent(openButton);
    datei.setPreferredSize(new Dimension(400, 17));
    openButton.setNextFocusableComponent(readFileButton);
    openButton.setPreferredSize(new Dimension(59, 20));
    openButton.setIcon(new ImageIcon(EddiFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    openButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openButton_actionPerformed(e);
      }
    });
    fieldSeparator.setPreferredSize(new Dimension(20, 17));
    fieldSeparator.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        fieldSeparator_keyReleased(e);
      }
    });
    jPanel3.setLayout(borderLayout2);
    jLabel3.setPreferredSize(new Dimension(280, 13));
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText(International.getString("Felder")+":");
    startzeileLabel.setText(International.getString("Startzeile")+": ");
    zeileLabel.setBackground(Color.blue);
    zeileLabel.setForeground(Color.white);
    zeileLabel.setOpaque(true);
    zeileLabel.setPreferredSize(new Dimension(400, 20));
    zeileLabel.setText("---"+International.getString("bitte zuerst Datei auswählen")+"---");
    nextLineButton.setNextFocusableComponent(fieldSeparator);
    nextLineButton.setPreferredSize(new Dimension(89, 20));
    Mnemonics.setButton(this, nextLineButton, International.getStringWithMnemonic("nächste"));
    nextLineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nextLineButton_actionPerformed(e);
      }
    });
    PrevLineButton.setNextFocusableComponent(nextLineButton);
    PrevLineButton.setPreferredSize(new Dimension(103, 20));
    Mnemonics.setButton(this, PrevLineButton, International.getStringWithMnemonic("vorherige"));
    PrevLineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PrevLineButton_actionPerformed(e);
      }
    });
    readFileButton.setNextFocusableComponent(PrevLineButton);
    readFileButton.setPreferredSize(new Dimension(95, 20));
    Mnemonics.setButton(this, readFileButton, International.getStringWithMnemonic("einlesen"));
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
    Mnemonics.setButton(this, jMenu1, International.getStringWithMnemonic("Datei"));
    Mnemonics.setButton(this, menuFileOpen, International.getStringWithMnemonic("Datei öffnen"));
    menuFileOpen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuFileOpen_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, menuImport, International.getStringWithMnemonic("Datenliste importieren"));
    menuImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuImport_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, menuExit, International.getStringWithMnemonic("Beenden"));
    menuExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuExit_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, menuHelp, International.getStringWithMnemonic("Info"));
    Mnemonics.setButton(this, jMenuItem4, International.getStringWithMnemonic("Hilfe"));
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem4_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, menuAbout, International.getStringWithMnemonic("Über"));
    menuAbout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuAbout_actionPerformed(e);
      }
    });
    contentPane.setMinimumSize(new Dimension(600, 165));
    contentPane.setPreferredSize(new Dimension(800, 147));
    Mnemonics.setButton(this, aliasCheckBox, International.getStringWithMnemonic("Generiere Eingabekürzel")+": ");
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
    tabbedPane.add(mitgliederPanel,  International.getString("Mitgliederliste"));
    mitgliederPanel.add(aliasCheckBox,   new GridBagConstraints(0, 20, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    mitgliederPanel.add(alias,  new GridBagConstraints(1, 20, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    tabbedPane.add(bootsPanel,  International.getString("Bootsliste"));
    jPanel1.add(nextLineButton,      new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(PrevLineButton,    new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(readFileButton,   new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    tabbedPane.add(zielPanel,  International.getString("Zielliste"));
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
    String[] mitglieder = { 
        International.getString("Vorname"),
        International.getString("Nachname"),
        International.getString("Eingabekürzel"),
        International.getString("Jahrgang"),
        International.getString("Geschlecht"),
        International.getString("Status"),
        International.getString("Verein"),
        International.getString("Behinderung"),
        International.getString("MitglNr."),
        International.getString("Paßwort"),
        International.getString("Frei")+" 1",
        International.getString("Frei")+" 2",
        International.getString("Frei")+" 3"
    };
    String[] boote = { 
        International.getString("Bootsname"),
        International.getString("Verein"),
        International.getString("Bootsart"),
        International.getString("Anzahl Ruderplätze"),
        International.getString("Riggerung"),
        International.getString("mit/ohne Stm."),
        International.getString("Gruppen"),
        International.getString("max. nicht in Gruppe"),
        International.getString("mind. in Gruppe"),
        International.getString("Frei")+" 1",
        International.getString("Frei")+" 2",
        International.getString("Frei")+" 3"
    };
    String[] ziele = { 
        International.getString("Bezeichnung"),
        International.getString("Entfernung"),
        International.onlyFor("Zielbereiche","de"),
        International.getString("Start und Ziel ist eigenes Bootshaus"),
        International.getString("Gewässer")
    };
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
    Daten.haltProgram(0);
  }


  // Datei datei einlesen
  void readFile(String datei) {
    if (datei == null) return;
    if (!EfaUtil.canOpenFile(datei)) {
      Dialog.error(LogString.logstring_fileOpenFailed(datei, International.getString("Datei")));
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
    startzeileLabel.setText(International.getString("Startzeile")+" ("+(lineNr+1)+"): ");
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
      label.setText(International.getString("Feld")+" "+(i+1)+": ");
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
      combo.addItem(International.getString("n/a"));
      for (int j=0; j<fields; j++)
        combo.addItem(International.getString("Feld")+" "+(j+1));
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
      Dialog.error(International.getString("Bitte wähle zuerst eine Quelldatei aus!"));
      return;
    }
    if (fields == null) {
      Dialog.error(International.getString("Bitte wähle zuerst eine Startzeile aus!"));
      return;
    }
    String dat = null;
    if (tabbedPane.getSelectedIndex() == 0)
      dat = Dialog.dateiDialog(this,
              International.getMessage("Dateiname für {listname}",
              International.getString("Mitgliederliste")),
              International.getString("Mitgliederliste")+" (*.efbm)","efbm",Daten.efaDataDirectory,true);
    if (tabbedPane.getSelectedIndex() == 1)
      dat = Dialog.dateiDialog(this,
              International.getMessage("Dateiname für {listname}",
              International.getString("Bootsliste")),
              International.getString("Bootsliste")+" (*.efbb)","efbb",Daten.efaDataDirectory,true);
    if (tabbedPane.getSelectedIndex() == 2)
      dat = Dialog.dateiDialog(this,
              International.getMessage("Dateiname für {listname}",
              International.getString("Zielliste")),
              International.getString("Zielliste")+" (*.efbz)","efbz",Daten.efaDataDirectory,true);
    if (dat == null) return;
    if (tabbedPane.getSelectedIndex() == 0) importMitgliederliste(dat);
    if (tabbedPane.getSelectedIndex() == 1) importBootsliste(dat);
    if (tabbedPane.getSelectedIndex() == 2) importZielliste(dat);
  }


  // Mitgliederliste importieren
  void importMitgliederliste(String datei) {
    if (aliasCheckBox.isSelected()) {
      if (EfaConfigFrame.parseAlias(this.alias.getText().trim()) >= 0) {
        Dialog.error(International.getString("Ungültiges Format des Eingabekürzels")+":\n"+EfaConfigFrame.parseError);
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

    String status = Dialog.inputDialog(International.getString("Status"),
            International.getString("Liste von Status, die ein Mitglied haben kann"),
            International.getString("Junior(in)")+","+
            International.getString("Senior(in)")+","+
            International.getString("Gast"));
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
        if (feld[Mitglieder.VORNAME].length()==0 && feld[Mitglieder.NACHNAME].length()==0) {
            fehler += International.getMessage("Das Feld '{fieldname}' darf nicht leer sein!",
                      International.getString("Name"))+"\n";
        }
        if (!feld[Mitglieder.GESCHLECHT].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, EfaTypes.TYPE_GENDER_MALE)) &&
            !feld[Mitglieder.GESCHLECHT].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, EfaTypes.TYPE_GENDER_FEMALE))) {
          fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                  International.getString("Geschlecht")) + ": "+feld[Mitglieder.GESCHLECHT]+"\n";
        }
        boolean statusOk = false;
        for (int k=0; k<statusList.length; k++) if (feld[Mitglieder.STATUS].equals(statusList[k])) statusOk = true;
        if (!statusOk) {
          fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                  International.getString("Status")) + ": "+feld[Mitglieder.STATUS]+"\n";
        }
        if (!(feld[Mitglieder.BEHINDERUNG].length()==0) && !feld[Mitglieder.BEHINDERUNG].equals("+") && !feld[Mitglieder.BEHINDERUNG].equals("-")) {
          fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                  International.getString("Behinderung")) + ": "+feld[Mitglieder.BEHINDERUNG]+"\n";
        }
        if (fehler.length()>0) {
          MitgliederFehlerFrame dlg = new MitgliederFehlerFrame(this,(String)lines.get(l),fehler,feld,statusList,replGeschl,replStatus,replBeh);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (abort) {
            Dialog.meldung(International.getString("Import abgebrochen."));
            return;
          }
        }

      } while(fehler.length() != 0 && !skip);


      if (!skip) {
          DatenFelder d = new DatenFelder(Mitglieder._ANZAHL);
          d.set(Mitglieder.VORNAME, feld[0]);
          d.set(Mitglieder.NACHNAME, feld[1]);
          d.set(Mitglieder.ALIAS, feld[2]);
          d.set(Mitglieder.JAHRGANG, feld[3]);
          d.set(Mitglieder.GESCHLECHT, Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_GENDER,feld[4]));
          d.set(Mitglieder.STATUS, feld[5]);
          d.set(Mitglieder.VEREIN, feld[6]);
          d.set(Mitglieder.BEHINDERUNG, feld[7]);
          d.set(Mitglieder.MITGLNR, feld[8]);
          d.set(Mitglieder.PASSWORT, feld[9]);
          d.set(Mitglieder.FREI1, feld[10]);
          d.set(Mitglieder.FREI2, feld[11]);
          d.set(Mitglieder.FREI3, feld[12]);
          d.set(Mitglieder.KMWETT_MELDEN, feld[13]);
          f.add(d);
      }
      skip = false;
    }
    f.writeFile();
    Dialog.meldung(International.getMessage("{listname} erfolgreich importiert!",
            International.getString("Mitgliederliste")));
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
        if (feld[Boote.NAME].length()==0 ) {
            fehler += International.getMessage("Das Feld '{fieldname}' darf nicht leer sein!",
                      International.getString("Boot"))+"\n";
        }
        boolean ok = false;
        for (int k=0; k<Daten.efaTypes.size(EfaTypes.CATEGORY_BOAT); k++) if (feld[Boote.ART].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, k))) ok = true;
        if (!ok) {
            fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                      International.getString("Bootsart")) + ": "+feld[Boote.ART]+"\n";
        }
        ok = false;
        for (int k=0; k<Daten.efaTypes.size(EfaTypes.CATEGORY_NUMSEATS); k++) if (feld[Boote.ANZAHL].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, k))) ok = true;
        if (!ok) {
            fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                      International.getString("Anzahl Ruderplätze")) + ": "+feld[Boote.ANZAHL]+"\n";
        }
        ok = false;
        for (int k=0; k<Daten.efaTypes.size(EfaTypes.CATEGORY_RIGGING); k++) if (feld[Boote.RIGGER].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_RIGGING, k))) ok = true;
        if (!ok) {
            fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                      International.getString("Riggerung")) + ": "+feld[Boote.RIGGER]+"\n";
        }
        ok = false;
        for (int k=0; k<Daten.efaTypes.size(EfaTypes.CATEGORY_COXING); k++) if (feld[Boote.STM].equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_COXING, k))) ok = true;
        if (!ok) {
            fehler += International.getMessage("Ungültiger Wert im Feld '{fieldname}'",
                      International.getString("mit/ohne Stm.")) + ": "+feld[Boote.STM]+"\n";
        }

        if (fehler.length()>0) {
          BootFehlerFrame dlg = new BootFehlerFrame(this,(String)lines.get(l),fehler,feld,replArt,replAnzahl,replRigger,replStm);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (abort) {
            Dialog.meldung(International.getString("Import abgebrochen."));
            return;
          }
        }

      } while(fehler.length() != 0 && !skip);


      if (!skip) {
          DatenFelder d = new DatenFelder(Boote._ANZFELDER);
          d.set(Boote.NAME, feld[0]);
          d.set(Boote.VEREIN, feld[1]);
          d.set(Boote.ART, Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_BOAT, feld[2]));
          d.set(Boote.ANZAHL, Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_NUMSEATS, feld[3]));
          d.set(Boote.RIGGER, Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_RIGGING, feld[4]));
          d.set(Boote.STM, Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_COXING, feld[5]));
          d.set(Boote.GRUPPEN, feld[6]);
          d.set(Boote.MAX_NICHT_IN_GRUPPE, feld[7]);
          d.set(Boote.MIND_1_IN_GRUPPE, feld[8]);
          d.set(Boote.FREI1, feld[9]);
          d.set(Boote.FREI2, feld[10]);
          d.set(Boote.FREI3, feld[11]);
          f.add(d);
      }
      skip = false;
    }
    f.writeFile();
    Dialog.meldung(International.getMessage("{listname} erfolgreich importiert!",
            International.getString("Bootsliste")));
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
        if (feld[Ziele.NAME].length()==0 ) {
            fehler += International.getMessage("Das Feld '{fieldname}' darf nicht leer sein!",
                      International.getString("Ziel"))+"\n";
        }
        if (feld[Ziele.KM].length()==0 )
          fehler += International.getString("Kilometer dürfen nicht leer sein!")+"\n";
        if (!(feld[Ziele.STEGZIEL].length()==0) && !feld[Ziele.STEGZIEL].equals("+") && !feld[Ziele.STEGZIEL].equals("-"))
          fehler += "Ungültiger Wert im Feld 'Start und Ziel ist eigenes Bootshaus': "+feld[Ziele.STEGZIEL]+"\n";

        if (fehler.length()>0) {
          ZielFehlerFrame dlg = new ZielFehlerFrame(this,(String)lines.get(l),fehler,feld,replStegZiel);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (abort) {
            Dialog.meldung(International.getString("Import abgebrochen."));
            return;
          }
        }

      } while(fehler.length() != 0 && !skip);


      if (!skip) {
          DatenFelder d = new DatenFelder(Ziele._ANZFELDER);
          d.set(Ziele.NAME, feld[0]);
          d.set(Ziele.KM, feld[1]);
          d.set(Ziele.BEREICH, feld[2]);
          d.set(Ziele.STEGZIEL, feld[3]);
          d.set(Ziele.GEWAESSER, feld[4]);
          f.add(d);
      }
      skip = false;
    }
    f.writeFile();
    Dialog.meldung(International.getMessage("{listname} erfolgreich importiert!",
            International.getString("Zielliste")));
  }



}
