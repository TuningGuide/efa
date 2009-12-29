/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.core.*;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import de.nmichael.efa.*;
import java.beans.*;
import javax.swing.border.*;

// @i18n complete

public class EfaDirektFrame extends JFrame {
  BootStatus bootStatus = null;
  Vector booteAlle = new Vector();
  EfaFrame efaFrame;
  EfaDirektBackgroundTask efaDirektBackgroundTask = null;
  EfaUhrUpdater efaUhrUpdater = null;
  EfaNewsUpdater efaNewsUpdater = null;
  Vector booteVerfuegbarListData = null;
  Vector booteAufFahrtListData = null;
  Vector booteNichtVerfuegbarListData = null;
  long lastUserInteraction = 0;
  byte[] largeChunkOfMemory = new byte[1024*1024];

  public static final int EFA_EXIT_REASON_USER = 0;
  public static final int EFA_EXIT_REASON_TIME = 1;
  public static final int EFA_EXIT_REASON_OOME = 2;
  public static final int EFA_EXIT_REASON_AUTORESTART = 3;

  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel southPanel = new JPanel();
  JPanel centerPanel = new JPanel();
  JLabel statusLabel = new JLabel();
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton fahrtbeginnButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList booteVerfuegbar = new JList();
  JList booteAufFahrt = new JList();
  JScrollPane jScrollPane3 = new JScrollPane();
  JButton fahrtendeButton = new JButton();
  JButton nachtragButton = new JButton();
  JButton bootsstatusButton = new JButton();
  JButton adminHinweisButton = new JButton();
  JButton adminButton = new JButton();
  JButton spezialButton = new JButton();
  JLabel verfuegbareBooteLabel = new JLabel();
  JLabel aufFahrtBooteLabel = new JLabel();
  JLabel nichtVerfuegbareBooteLabel = new JLabel();
  JList booteNichtVerfuegbar = new JList();
  JButton efaButton = new JButton();
  JButton fahrtabbruchButton = new JButton();
  JButton hilfeButton = new JButton();
  JButton showFbButton = new JButton();
  JLabel logoLabel = new JLabel();
  JButton statButton = new JButton();
  JPanel westPanel = new JPanel();
  JPanel eastPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel eastCenterPanel = new JPanel();
  JPanel eastSouthPanel = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  BorderLayout borderLayout6 = new BorderLayout();
  JLabel uhr = new JLabel();
  JPanel sunrisePanel = new JPanel();
  JLabel srSRimage = new JLabel();
  JLabel srSRtext = new JLabel();
  JLabel srSSimage = new JLabel();
  JLabel srSStext = new JLabel();
  JLabel newsLabel = new JLabel();


  //Construct the frame
  public EfaDirektFrame() {
//    System.setProperty("sun.awt.noerasebackground","true"); // removed because of problems with Kubuntu 8.04

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      this.setResizable(false);
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    // Fenster nicht verschiebbar
    if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_fensterNichtVerschiebbar) try {
      this.setUndecorated(true);
      TitledBorder b = new TitledBorder(Daten.EFA_LONGNAME);
      b.setTitleColor(Color.white);
      contentPane.setBackground(new Color(0,0,150));
      contentPane.setBorder(b);
    } catch (NoSuchMethodError e) {
      Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION, 
              International.getString("Fenstereigenschaft 'nicht verschiebbar' wird erst ab Java 1.4 unterstützt."));
    }

    // Fenster immer im Vordergrund
    try {
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_immerImVordergrund) {
        if (!de.nmichael.efa.java15.Java15.setAlwaysOnTop(this,true)) {
//          Logger.log(Logger.WARNING,"Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterstützt.");
//          Hier muß keine Warnung mehr ausgegeben werden, da ab v1.6.0 die Funktionalität auch für Java < 1.5
//          durch einen Check alle 60 Sekunden nachgebildet wird.
        }
      }
    } catch(UnsupportedClassVersionError e) {
      // Java 1.3 kommt mit der Java 1.5 Klasse nicht klar
      Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION,
              International.getString("Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterstützt."));
    } catch(NoClassDefFoundError e) {
      Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION,
              International.getString("Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterstützt."));
    }


    appIni();

    packFrame("EfaDirektFrame()");
    this.booteVerfuegbar.requestFocus();

    // Fenster maximiert
    if (Daten.efaConfig.efaDirekt_startMaximized) try {
      this.setSize(Dialog.screenSize);

      Dimension newsize = this.getSize();

      // breite für Scrollpanes ist (Fensterbreite - 20) / 2.
      int width = (int)((newsize.getWidth() - this.fahrtbeginnButton.getSize().getWidth() - 20) / 2);
      // die Höhe der Scrollpanes ist, da sie CENTER sind, irrelevant; nur für jScrollPane3
      // ist die Höhe ausschlaggebend.
      jScrollPane1.setPreferredSize(new Dimension(width,500));
      jScrollPane2.setPreferredSize(new Dimension(width,300));
      jScrollPane3.setPreferredSize(new Dimension(width,(int)(newsize.getHeight()/4)));
      int height = (int)(20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
      verfuegbareBooteLabel.setPreferredSize(new Dimension(width,height));
      aufFahrtBooteLabel.setPreferredSize(new Dimension(width,height));
      nichtVerfuegbareBooteLabel.setPreferredSize(new Dimension(width,height));
      validate();
    } catch(Exception e) { EfaUtil.foo(); }


    EfaExitFrame.initExitFrame(this);
    // Speicher-Überwachung
    try {
      de.nmichael.efa.java15.Java15.setMemUsageListener(this,Daten.MIN_FREEMEM_COLLECTION_THRESHOLD);
    } catch(UnsupportedClassVersionError e) {
      EfaUtil.foo();
    } catch(NoClassDefFoundError e) {
      EfaUtil.foo();
    }

    if (Daten.efaConfig.efaDirekt_locked) {
      // lock efa NOW
      try {
        new Thread() {
          public void run() {
            try {
              Thread.sleep(1000);
            } catch(Exception e) {
            }
            lockEfa();
          }
        }.start();
      } catch(Exception ee) {
      }
    } else {
      // lock efa later
      if (Daten.efaConfig.efaDirekt_lockEfaFromDatum != null) {
        lockEfaAt(Daten.efaConfig.efaDirekt_lockEfaFromDatum,Daten.efaConfig.efaDirekt_lockEfaFromZeit);
      }
    }

    this.efaDirektBackgroundTask.interrupt(); // damit Frame nochmal gepackt werden kann (Bugfix)
  }


  public void packFrame(String source) {
    this.pack();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;

    alive();

//    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      // nothing
//    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_2")) { // F2
      this.fahrtbeginnButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_3")) { // F3
      this.fahrtendeButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_4")) { // F4
      this.fahrtabbruchButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_5")) { // F5
      this.nachtragButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_6")) { // F6
      this.bootsstatusButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_7")) { // F7
      this.showFbButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_8")) { // F8
      this.statButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_9")) { // F9
      this.adminHinweisButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_10")) { // alt-F10
      this.adminButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_11")) { // alt-F11
      this.spezialButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_12")) { // F10
      this.booteVerfuegbar.requestFocus();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_13")) { // F11
      this.booteAufFahrt.requestFocus();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_14")) { // F12
      this.booteNichtVerfuegbar.requestFocus();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_15")) { // Shift-F1
      EfaUtil.gc();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_16")) { // Alt-X
      cancel(null,EFA_EXIT_REASON_USER,false);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_17")) { // Shift-F4
      cancel(null,EFA_EXIT_REASON_USER,false);
    }
  }


  //Component initialization
  private void jbInit() throws Exception  {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1","F2","F3","F4","F5","F6","F7","F8","F9","alt F10","alt F11","F10","F11","F12","shift F1","alt X","shift F4"},
                       new String[] {"keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    this.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) { alive(); }
      public void mouseExited(MouseEvent e) { alive(); }
      public void mouseEntered(MouseEvent e) { alive(); }
      public void mouseReleased(MouseEvent e) { alive(); }
      public void mousePressed(MouseEvent e) { alive(); }
    });

    setIconImage(Toolkit.getDefaultToolkit().createImage(EfaDirektFrame.class.getResource("/de/nmichael/efa/img/efa_icon.gif")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(708, 489));
    this.setTitle(Daten.EFA_LONGNAME);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowActivated(WindowEvent e) {
        this_windowActivated(e);
      }
    });
    statusLabelSetText(International.getString("Status"));
    southPanel.setLayout(borderLayout2);
    centerPanel.setLayout(gridBagLayout1);
    fahrtbeginnButton.setBackground(new Color(204, 255, 204));
    fahrtbeginnButton.setNextFocusableComponent(fahrtendeButton);
    Mnemonics.setButton(this, fahrtbeginnButton, International.getStringWithMnemonic("Fahrt beginnen")+ ">>>");
    fahrtbeginnButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtbeginnButton_actionPerformed(e);
      }
    });
    fahrtendeButton.setBackground(new Color(204, 255, 204));
    fahrtendeButton.setNextFocusableComponent(fahrtabbruchButton);
    Mnemonics.setButton(this, fahrtendeButton, "<<< " + International.getStringWithMnemonic("Fahrt beenden"));
    fahrtendeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtendeButton_actionPerformed(e);
      }
    });
    nachtragButton.setBackground(new Color(204, 255, 255));
    nachtragButton.setNextFocusableComponent(bootsstatusButton);
    Mnemonics.setButton(this, nachtragButton, International.getStringWithMnemonic("Nachtrag"));
    nachtragButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nachtragButton_actionPerformed(e);
      }
    });
    bootsstatusButton.setBackground(new Color(255, 255, 204));
    bootsstatusButton.setNextFocusableComponent(showFbButton);
    Mnemonics.setButton(this, bootsstatusButton, International.getStringWithMnemonic("Bootsreservierungen"));
    bootsstatusButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootsstatusButton_actionPerformed(e);
      }
    });
    adminHinweisButton.setBackground(new Color(255, 241, 151));
    adminHinweisButton.setNextFocusableComponent(adminButton);
    Mnemonics.setButton(this, adminHinweisButton, International.getStringWithMnemonic("Nachricht an Admin"));
    adminHinweisButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adminHinweisButton_actionPerformed(e);
      }
    });
    adminButton.setNextFocusableComponent(booteAufFahrt);
    Mnemonics.setButton(this, adminButton, International.getStringWithMnemonic("Admin-Modus"));
    adminButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adminButton_actionPerformed(e);
      }
    });
    spezialButton.setNextFocusableComponent(booteAufFahrt);
    spezialButton.setText(International.getString("Spezial-Button"));
    spezialButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        spezialButton_actionPerformed(e);
      }
    });
    Dialog.setPreferredSize(verfuegbareBooteLabel,260,20);
    Dialog.setPreferredSize(aufFahrtBooteLabel,260,20);
    Dialog.setPreferredSize(nichtVerfuegbareBooteLabel,220,20);
    Mnemonics.setLabel(this, verfuegbareBooteLabel, International.getStringWithMnemonic("verfügbare Boote"));
    verfuegbareBooteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    verfuegbareBooteLabel.setLabelFor(booteVerfuegbar);
    Mnemonics.setLabel(this, aufFahrtBooteLabel, International.getStringWithMnemonic("Boote auf Fahrt"));
    aufFahrtBooteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    aufFahrtBooteLabel.setLabelFor(booteAufFahrt);
    Mnemonics.setLabel(this, nichtVerfuegbareBooteLabel, International.getStringWithMnemonic("nicht verfügbare Boote"));
    nichtVerfuegbareBooteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    nichtVerfuegbareBooteLabel.setLabelFor(booteNichtVerfuegbar);
    southPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    booteVerfuegbar.setNextFocusableComponent(fahrtbeginnButton);
    booteVerfuegbar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // KeyListeners entfernen, damit unter Java 1.4.x nicht automatisch gescrollt wird, sondern durch den eigenen Algorithmus
    try {
     KeyListener[] kl = booteVerfuegbar.getKeyListeners();
     for (int i=0; i<kl.length; i++) booteVerfuegbar.removeKeyListener(kl[i]);
    } catch(NoSuchMethodError e) { /* Java 1.3 kennt diese Methode nicht */ }
    booteVerfuegbar.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        booteVerfuegbar_keyReleased(e);
      }
    });
    booteVerfuegbar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        booteVerfuegbar_mouseClicked(e);
      }
    });
    booteAufFahrt.setNextFocusableComponent(booteNichtVerfuegbar);
    booteAufFahrt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // KeyListeners entfernen, damit unter Java 1.4.x nicht automatisch gescrollt wird, sondern durch den eigenen Algorithmus
    try {
      KeyListener[] kl = booteAufFahrt.getKeyListeners();
      for (int i=0; i<kl.length; i++) booteAufFahrt.removeKeyListener(kl[i]);
    } catch(NoSuchMethodError e) { /* Java 1.3 kennt diese Methode nicht */ }
    booteAufFahrt.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        booteAufFahrt_keyReleased(e);
      }
    });
    booteAufFahrt.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        booteAufFahrt_mouseClicked(e);
      }
    });
    booteNichtVerfuegbar.setNextFocusableComponent(booteVerfuegbar);
    booteNichtVerfuegbar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // KeyListeners entfernen, damit unter Java 1.4.x nicht automatisch gescrollt wird, sondern durch den eigenen Algorithmus
    try {
      KeyListener[] kl = booteNichtVerfuegbar.getKeyListeners();
      for (int i=0; i<kl.length; i++) booteNichtVerfuegbar.removeKeyListener(kl[i]);
    } catch(NoSuchMethodError e) { /* Java 1.3 kennt diese Methode nicht */ }
    booteNichtVerfuegbar.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        booteNichtVerfuegbar_keyReleased(e);
      }
    });
    booteNichtVerfuegbar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        booteNichtVerfuegbar_mouseClicked(e);
      }
    });
    efaButton.setPreferredSize(new Dimension(90, 55));
    efaButton.setIcon(new ImageIcon(EfaFrame.class.getResource(Daten.getEfaImage(1))));
    efaButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaButton_actionPerformed(e);
      }
    });
    fahrtabbruchButton.setBackground(new Color(255, 204, 204));
    fahrtabbruchButton.setNextFocusableComponent(nachtragButton);
    Mnemonics.setButton(this, fahrtabbruchButton, International.getStringWithMnemonic("Fahrt abbrechen"));
    fahrtabbruchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtabbruchButton_actionPerformed(e);
      }
    });
    hilfeButton.setText(International.getMessage("Hilfe mit {key}","[F1]"));
    hilfeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hilfeButton_actionPerformed(e);
      }
    });
    showFbButton.setBackground(new Color(204, 204, 255));
    showFbButton.setNextFocusableComponent(statButton);
    Mnemonics.setButton(this, showFbButton, International.getStringWithMnemonic("Fahrtenbuch anzeigen"));
    showFbButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showFbButton_actionPerformed(e);
      }
    });
    statButton.setBackground(new Color(204, 204, 255));
    statButton.setNextFocusableComponent(adminHinweisButton);
    Mnemonics.setButton(this, statButton, International.getStringWithMnemonic("Statistik erstellen"));
    statButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        statButton_actionPerformed(e);
      }
    });
    eastPanel.setLayout(borderLayout4);
    westPanel.setLayout(borderLayout3);
    eastSouthPanel.setLayout(borderLayout5);
    eastCenterPanel.setLayout(borderLayout6);
    uhr.setText("12:34");
    contentPane.add(southPanel, BorderLayout.SOUTH);
    contentPane.add(centerPanel, BorderLayout.CENTER);
    southPanel.add(statusLabel, BorderLayout.CENTER);
    int fahrtbeginnTop = (int)(20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
    centerPanel.add(fahrtbeginnButton,            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(fahrtbeginnTop, 0, 0, 0), 0, 0));
//    centerPanel.add(jScrollPane1,               new GridBagConstraints(0, 1, 1, 13, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(booteVerfuegbar, null);
//    centerPanel.add(jScrollPane2,            new GridBagConstraints(2, 1, 1, 9, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//    centerPanel.add(jScrollPane3,            new GridBagConstraints(2, 11, 1, 3, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane3.getViewport().add(booteNichtVerfuegbar, null);
    centerPanel.add(fahrtendeButton,           new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    centerPanel.add(nachtragButton,             new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(bootsstatusButton,            new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(adminHinweisButton,              new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(adminButton,                 new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 20, 0), 0, 0));
    centerPanel.add(spezialButton,                 new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 0), 0, 0));
//    centerPanel.add(verfuegbareBooteLabel,            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
//    centerPanel.add(aufFahrtBooteLabel,           new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
    jScrollPane2.getViewport().add(booteAufFahrt, null);
//    centerPanel.add(nichtVerfuegbareBooteLabel,            new GridBagConstraints(2, 10, 1, 1, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 0, 2, 0), 0, 0));
    centerPanel.add(efaButton,            new GridBagConstraints(1, 14, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    centerPanel.add(fahrtabbruchButton,          new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(hilfeButton,           new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    centerPanel.add(showFbButton,       new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

    // Logo-Label
    int logoTop = (int)(20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
    int logoBottom = 5;
    if (Daten.efaConfig.efaDirekt_startMaximized && Daten.efaConfig.efaDirekt_vereinsLogo != null) {
      logoBottom += (int)((Dialog.screenSize.getHeight()-825)/5);
      if (logoBottom < 0) {
          logoBottom = 0;
      }
    }
    centerPanel.add(logoLabel,           new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(logoTop, 0, logoBottom, 0), 0, 0));

    centerPanel.add(statButton,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    centerPanel.add(uhr,  new GridBagConstraints(1, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

    srSRimage.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/sunrise.gif")));
    srSSimage.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/sunset.gif")));
    srSRtext.setText("00:00");
    srSStext.setText("23:59");
    sunrisePanel.add(srSRimage);
    sunrisePanel.add(srSRtext);
    sunrisePanel.add(srSSimage);
    sunrisePanel.add(srSStext);
    centerPanel.add(sunrisePanel,  new GridBagConstraints(1, 16, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 10, 10), 0, 0));
    newsLabel.setText("+++ News +++");
    newsLabel.setForeground(Color.white);
    newsLabel.setBackground(Color.red);
    newsLabel.setOpaque(true);
    newsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    newsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    centerPanel.add(newsLabel,  new GridBagConstraints(1, 17, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));



    westPanel.add(verfuegbareBooteLabel, BorderLayout.NORTH);
    westPanel.add(jScrollPane1, BorderLayout.CENTER);
    eastCenterPanel.add(aufFahrtBooteLabel, BorderLayout.NORTH);
    eastCenterPanel.add(jScrollPane2, BorderLayout.CENTER);
    eastSouthPanel.add(nichtVerfuegbareBooteLabel, BorderLayout.NORTH);
    eastSouthPanel.add(jScrollPane3, BorderLayout.CENTER);
    contentPane.add(westPanel,  BorderLayout.WEST);
    contentPane.add(eastPanel,  BorderLayout.EAST);
    eastPanel.add(eastCenterPanel, BorderLayout.CENTER);
    eastPanel.add(eastSouthPanel,  BorderLayout.SOUTH);
    booteVerfuegbar.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        bootslist_focusGained(e);
      }
    });
    booteAufFahrt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        bootslist_focusGained(e);
      }
    });
    booteNichtVerfuegbar.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        bootslist_focusGained(e);
      }
    });
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      // Fenster schließen
      cancel(e,EFA_EXIT_REASON_USER,false);
    } else if (e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
      // Fenster deaktivieren
      super.processWindowEvent(e);
    } else if (e.getID() == WindowEvent.WINDOW_ICONIFIED) {
      // Fenster minimiert
      super.processWindowEvent(e);
      this.setState(Frame.NORMAL);
    } else super.processWindowEvent(e);
  }

  void cancel(WindowEvent e, int reason, boolean restart) {
    int exitCode = 0;
    Daten.efaConfig.writeFile();
    String wer = "unknown";

    switch(reason) {
     case EFA_EXIT_REASON_USER: // manuelles Beenden von efa
       boolean durchMitglied;
       if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenEfaBeenden) {
         Admin admin = null;
         do {
           admin = AdminLoginFrame.login(this,International.getString("Beenden von efa"));
           if (admin != null && !admin.allowedEfaBeenden)
             Dialog.error(International.getString("Du hast nicht die Berechtigung, um efa zu beenden!"));
         } while (admin != null && !admin.allowedEfaBeenden);
         if (admin == null) return;
         wer = International.getString("Admin")+"="+admin.name;
         durchMitglied = false;
       } else {
         wer = International.getString("Nutzer");
         durchMitglied = true;
       }
       if (Daten.efaConfig.efaDirekt_execOnEfaExit.length()>0 && durchMitglied) {
         Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXITEXECCMD,
                 International.getMessage("Programmende veranlaßt; versuche, Kommando '{cmd}' auszuführen...", Daten.efaConfig.efaDirekt_execOnEfaExit));
         try {
           Runtime.getRuntime().exec(Daten.efaConfig.efaDirekt_execOnEfaExit);
         } catch(Exception ee) {
           Logger.log(Logger.ERROR, Logger.MSG_ERR_EFAEXITEXECCMD_FAILED,
                   LogString.logstring_cantExecCommand(Daten.efaConfig.efaDirekt_execOnEfaExit, International.getString("Kommando")));
         }
       }
       break;
     case EFA_EXIT_REASON_TIME:
       wer = International.getString("Zeitsteuerung");
       if (Daten.efaConfig.efaDirekt_execOnEfaAutoExit.length()>0) {
         Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXITEXECCMD,
                 International.getMessage("Programmende veranlaßt; versuche, Kommando '{cmd}' auszuführen...",Daten.efaConfig.efaDirekt_execOnEfaAutoExit));
         try {
           Runtime.getRuntime().exec(Daten.efaConfig.efaDirekt_execOnEfaAutoExit);
         } catch(Exception ee) {
           Logger.log(Logger.ERROR, Logger.MSG_ERR_EFAEXITEXECCMD_FAILED,
                   LogString.logstring_cantExecCommand(Daten.efaConfig.efaDirekt_execOnEfaAutoExit, International.getString("Kommando")));
         }
       }
       break;
     case EFA_EXIT_REASON_OOME:
       wer = International.getString("Speicherüberwachung");
       break;
     case EFA_EXIT_REASON_AUTORESTART:
       wer = International.getString("Automatischer Neustart");
       break;
    }

    if (restart) {
      if (Daten.javaRestart) {
        exitCode = Daten.HALT_JAVARESTART;
        String restartcmd = System.getProperty("java.home") + Daten.fileSep +
            "bin" + Daten.fileSep + "java " +
            (Daten.efa_java_arguments != null ? Daten.efa_java_arguments :
             "-cp " + System.getProperty("java.class.path") +
             " " + Daten.EFADIREKT_MAINCLASS + Main.STARTARGS);
        Logger.log(Logger.INFO, Logger.MSG_EVT_EFARESTART,
                International.getMessage("Neustart mit Kommando: {cmd}",restartcmd));
        try {
          Runtime.getRuntime().exec(restartcmd);
        }
        catch (Exception ee) {
          Logger.log(Logger.ERROR, Logger.MSG_ERR_EFARESTARTEXEC_FAILED,
                   LogString.logstring_cantExecCommand(restartcmd, International.getString("Kommando")));
        }
      } else {
        exitCode = Daten.HALT_SHELLRESTART;
      }
    }

    if (e != null) super.processWindowEvent(e);
    Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXIT,
            International.getMessage("Programmende durch {originator}",wer));
    Daten.haltProgram(exitCode);
  }

  public synchronized void exitOnLowMemory(String detector, boolean immediate) {
    largeChunkOfMemory = null;
    Logger.log(Logger.ERROR, Logger.MSG_ERR_EXITLOWMEMORY,
            International.getMessage("Der Arbeitsspeicher wird knapp [{detector}]: "+
                                     "efa versucht {jetzt} einen Neustart ...",detector,
                                     (immediate ? International.getString("jetzt").toUpperCase() : International.getString("jetzt"))));
    if (immediate) {
      this.cancel(null,EFA_EXIT_REASON_OOME,true);
    } else {
      EfaExitFrame.exitEfa(International.getString("Neustart wegen knappen Arbeitsspeichers"), true, EFA_EXIT_REASON_OOME);
    }
  }


  void this_windowActivated(WindowEvent e) {
    try {
      if (!isEnabled() && efaFrame != null) {
        efaFrame.toFront();
      }
    } catch(Exception ee) {}
  }

  public static void haltProgram(String s, int exitCode) {
    if (s != null) {
      Dialog.error(s);
      Logger.log(Logger.ERROR, Logger.MSG_ERR_GENERIC,
              EfaUtil.replace(s,"\n"," ",true));
    }
    Daten.haltProgram(exitCode);
  }

  void appIni() {
    // Nachrichten an Admin einlesen
    Daten.nachrichten = new NachrichtenAnAdmin(Daten.efaDataDirectory+Daten.DIREKTNACHRICHTEN);
    if (!EfaUtil.canOpenFile(Daten.nachrichten.getFileName())) {
      if (!Daten.nachrichten.writeFile()) {
          haltProgram(LogString.logstring_fileCreationFailed(Daten.nachrichten.getFileName(), 
                  International.getString("Nachrichtendatei")), Daten.HALT_FILEOPEN);
      }
    } else {
      if (!Daten.nachrichten.readFile()) {
          haltProgram(LogString.logstring_fileOpenFailed(Daten.nachrichten.getFileName(), 
                  International.getString("Nachrichtendatei")), Daten.HALT_FILEOPEN);
      }
    }
    Logger.setNachrichtenAnAdmin(Daten.nachrichten);
    updateUnreadMessages();

    // Admin-Paßwort vorhanden?
    boolean neuerSuperAdmin = false;
    if (Daten.efaConfig.admins.get(EfaConfig.SUPERADMIN) == null) {
      Logger.log(Logger.INFO, Logger.MSG_ERR_NOSUPERADMIN,
              International.getString("Kein Super-Admin gefunden."));
      try {
        // gibt es noch das Sicherheitsfile?
        if (!Daten.efaSec.secFileExists()) {
          String s = International.getString("efa konnte kein Super-Admin Paßwort finden!") + " " +
                     International.getMessage("Aus Gründen der Sicherheit verweigert efa den Dienst. "+
                     "Bitte installiere efa neu oder kontaktiere den Entwickler: {email}",Daten.EFAEMAIL);
          haltProgram(s, Daten.HALT_EFASECADMIN);
        }
      } catch(Exception e) {
        String s = International.getMessage("efa konnte kein Super-Admin Paßwort finden, und bei den folgenden Tests trat ein Fehler auf: {error}",e.toString()) + " " +
                   International.getMessage("Aus Gründen der Sicherheit verweigert efa den Dienst. "+
                   "Bitte installiere efa neu oder kontaktiere den Entwickler: {email}",Daten.EFAEMAIL);
        haltProgram(s, Daten.HALT_EFASECADMIN);
      }
      String pwd = "";
      Dialog.infoDialog(International.getString("Willkommen bei der Bootshaus-Version von efa"),
                        International.getString("Mitglieder dürfen in efa nur Fahrten eintragen. Alle weiteren "+
                        "Aktionen dürfen nur von einem Administrator ausgeführt werden. "+
                        "Der Super-Administrator (Haupt-Administrator) hat uneingeschränkte "+
                        "Rechte, die anderen Administratoren können eingeschränkte Rechte "+
                        "besitzen.\n"+
                        "Der Name des Super-Administrators lautet 'admin'. Zur Zeit gibt es "+
                        "noch kein Paßwort für 'admin'. Du wirst nun gleich aufgefordert, ein "+
                        "neues Paßwort für den Super-Administrator 'admin' einzugeben."));

      pwd = NewPasswordFrame.getNewPassword(this,EfaConfig.SUPERADMIN);
      if (pwd == null) {
        haltProgram(International.getString("Paßworteingabe für Super-Admin abgebrochen."), Daten.HALT_EFASECADMIN);
      }

      Admin root = new Admin(EfaConfig.SUPERADMIN,EfaUtil.getSHA(pwd));
      Daten.efaConfig.admins.put(EfaConfig.SUPERADMIN,root);
      if (!Daten.efaConfig.writeFile()) {
          haltProgram(International.getMessage("{filename} konnte nicht geschrieben werden!",Daten.efaConfig.getFileName()),
                  Daten.HALT_EFASECADMIN);
      }
      Logger.log(Logger.INFO, Logger.MSG_EVT_SUPERADMINCREATED,
              International.getString("Neuer Super-Admin erstellt."));
      neuerSuperAdmin = true;
    }


    if (Daten.efaSec.secFileExists() && !Daten.efaSec.isDontDeleteSet()) {
      switch (Dialog.auswahlDialog(International.getString("Sicherheits-Frage"),
              International.getString("Aus Gründen der Sicherheit sollte es im Bootshaus nicht möglich sein, "+
                                   "das herkömmliche efa ohne Paßwort zu starten, da dort jeder Benutzer auch ohne "+
                                   "Admin-Rechte alle Daten manipulieren kann. Es wird daher dringend "+
                                   "geraten, das herkömmliche efa ebenfalls durch das Admin-Paßwort zu sichern! "+
                                   "Für den Einsatz zu Hause ist es natürlich nicht erforderlich, efa "+
                                   "zu sperren, da hier i.d.R. keine Mißbrauchgefahr besteht.\n\n"+
                                   "Möchtest Du, daß die herkömmliche efa-Oberfläche paßwortgeschützt wird und nur "+
                                   "noch für Admins zugänglich ist (für Bootshaus-Einsatz dringend empfohlen!)?\n"+
                                   "Herkömmliche efa-Oberfläche ..."),
                                   "... " + International.getString("durch Paßwort schützen"),
                                   "... " + International.getString("nicht schützen"))) {
        case 0: // Sperren
          if (Daten.efaSec.delete(true)) {
              String s = International.getString("Der Start des herkömmlichen efa ist nun nur noch mit Paßwort möglich!");
              Logger.log(Logger.INFO, Logger.MSG_EVT_EFASECURE, s);
              Dialog.meldung(s);
          } else {
              haltProgram(International.getMessage("efa konnte die Datei {filename} nicht löschen und wird daher beendet!",
                      Daten.efaSec.getFilename()), Daten.HALT_EFASEC);
          }
          // Standardwerte für Backups ändern
          Daten.efaConfig.bakSave = false;
          Daten.efaConfig.bakKonv = true;
          Daten.efaConfig.bakMonat = false;
          Daten.efaConfig.bakTag = true;
          Daten.efaConfig.writeFile();
          Daten.backup = new Backup(Daten.efaBakDirectory,Daten.efaConfig.bakSave,Daten.efaConfig.bakMonat,Daten.efaConfig.bakTag,Daten.efaConfig.bakKonv);
          break;
        case 1:
          if (Daten.efaSec.writeSecFile(Daten.efaSec.getSecValue(),true)) {
              String s = International.getString("Der Start des herkömmlichen efa ist auch ohne Paßwort möglich!");
              Logger.log(Logger.WARNING, Logger.MSG_WARN_EFAUNSECURE, s);
              Dialog.meldung(s);
          } else {
              haltProgram(International.getMessage("efa konnte die Datei {filename} nicht schreiben und wird daher beendet!",
                      Daten.efaSec.getFilename()), Daten.HALT_EFASEC);
          }
          break;
        default:
          haltProgram(null, Daten.HALT_EFASEC);
      }
    }

    // efaSec löschen (außer, wenn DontDelete-Flag gesetzt ist)
    if (Daten.efaSec.secFileExists() && !Daten.efaSec.delete(false)) {
      String s = International.getMessage("efa konnte die Datei {filename} nicht löschen und wird daher beendet!",Daten.efaSec.getFilename());
      haltProgram(s, Daten.HALT_EFASEC);
    }

    // Fahrtenbuch öffnen, falls keines angegeben
    if (Daten.efaConfig.direkt_letzteDatei == null || Daten.efaConfig.direkt_letzteDatei.length()==0) {

      if (neuerSuperAdmin) Dialog.infoDialog(International.getString("Fahrtenbuch auswählen"),
                                             International.getString("Bisher wurde noch kein Fahrtenbuch ausgewählt, mit dem "+
                                             "gearbeitet werden soll. Im folgenden Schritt wirst Du "+
                                             "aufgefordert, ein Fahrtenbuch auszuwählen. "+
                                             "Du mußt Dich daher zunächst als Administrator anmelden: "+
                                             "Der Name des Super-Administrators lautet 'admin', das "+
                                             "Paßwort hast Du eben selbst gewählt."));
      Admin admin = null;
      do {
        admin = AdminLoginFrame.login(this,International.getString("Kein Fahrtenbuch ausgewählt"));
        if (admin == null) {
            haltProgram(International.getString("Programmende, da kein Fahrtenbuch ausgewählt und Admin-Login nicht erfolgreich."), Daten.HALT_FILEOPEN);
        }
        if (!admin.allowedFahrtenbuchAuswaehlen) {
            Dialog.error(International.getMessage("Du hast als Admin {name} keine Berechtigung, ein Fahrtenbuch auszuwählen!",admin.name));
        }
      } while (!admin.allowedFahrtenbuchAuswaehlen);
      String dat = null;
      switch (Dialog.auswahlDialog(International.getString("Fahrtenbuch auswählen"),
              International.getString("Möchtest Du ein neues Fahrtenbuch erstellen oder ein vorhandenes öffnen?"),
              International.getString("Neues Fahrtenbuch erstellen"),
              International.getString("Vorhandenes Fahrtenbuch öffnen"))) {
        case 0: // Neues Fahrtenbuch erstellen
          FahrtenbuchNeuFortsetzenFrame dlg = new FahrtenbuchNeuFortsetzenFrame(this,false);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (Daten.fahrtenbuch != null) {
            dat = Daten.fahrtenbuch.getFileName();
          }
          break;
        case 1: // Vorhandenes Fahrtenbuch öffnen
          dat = Dialog.dateiDialog(this,International.getString("Fahrtenbuch öffnen"),
                  International.getString("efa Fahrtenbuch")+" (*.efb)","efb",Daten.efaDataDirectory,false);
          break;
        default:
          haltProgram(International.getString("Kein Fahrtenbuch ausgewählt"), Daten.HALT_FILEOPEN);
      }
      if (dat == null || dat.length()==0) {
          haltProgram(International.getString("Kein Fahrtenbuch ausgewählt"), Daten.HALT_FILEOPEN);
      }
      Daten.efaConfig.direkt_letzteDatei = dat;
      Logger.log(Logger.INFO, Logger.MSG_EVT_NEWLOGBOOKOPENED,
              International.getMessage("Neue Fahrtenbuchdatei '{filename}' ausgewählt.",dat));
      if (!Daten.efaConfig.writeFile()) {
          haltProgram(LogString.logstring_fileCreationFailed(Daten.efaConfig.getFileName(), 
                  International.getString("Konfigurationsdatei")), Daten.HALT_FILEOPEN);
      }
    }

    readFahrtenbuch();

    Daten.iniAllDataFiles();

    setButtonsLookAndFeel();

    iniBootsListen();

    Daten.checkEfaVersion(false);
    Daten.checkJavaVersion(false);

    Logger.log(Logger.INFO, Logger.MSG_EVT_EFAREADY,
            International.getString("BEREIT"));

    // EfaFrame vorbereiten
    efaFrame = new EfaFrame(this,Daten.nachrichten);
    Dimension dlgSize = efaFrame.getSize();
    efaFrame.setFixedLocation((Dialog.screenSize.width - dlgSize.width) / 2, (Dialog.screenSize.height - dlgSize.height) / 2);

    // ReservierungsChecker-Thread starten
    alive();
    efaDirektBackgroundTask = new EfaDirektBackgroundTask(this);
    efaDirektBackgroundTask.start();

    // Uhr-Thread starten
    efaUhrUpdater = new EfaUhrUpdater(this.uhr,this.srSRtext,this.srSStext,Daten.efaConfig.efaDirekt_sunRiseSet_show);
    efaUhrUpdater.start();
    uhr.setVisible(Daten.efaConfig.efaDirekt_showUhr);

    // News Text anzeigen
    updateNews();

    // Sunrise anzeigen oder nicht
    sunrisePanel.setVisible(Daten.efaConfig.efaDirekt_sunRiseSet_show);
  }


  // Fahrtenbuch einlesen
  public void readFahrtenbuch() {
    if (Daten.efaConfig == null || Daten.efaConfig.direkt_letzteDatei == null || Daten.efaConfig.direkt_letzteDatei.length()==0) {
      haltProgram(International.getString("Oops!") + " " +
              International.getString("Kein Fahrtenbuch zum Öffnen da!"), Daten.HALT_FILEOPEN);
    } else {
      Daten.fahrtenbuch = new Fahrtenbuch(Daten.efaConfig.direkt_letzteDatei);
      int sveAction = Daten.actionOnDatenlisteNotFound;
      Daten.actionOnDatenlisteNotFound = Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_RETURN_FALSE_ON_NEIN;
      while (!Daten.fahrtenbuch.readFile()) {
        Dialog.error(LogString.logstring_fileOpenFailed(Daten.efaConfig.direkt_letzteDatei, International.getString("Fahrtenbuch")));

        Admin admin = null;
        do {
          admin = AdminLoginFrame.login(this,International.getString("Fahrtenbuch konnte nicht geöffnet werden"));
          if (admin == null) {
              haltProgram(International.getString("Programmende, da Fahrtenbuch nicht geöffnet werden konnte und Admin-Login nicht erfolgreich."), Daten.HALT_FILEOPEN);
          }
          if (!admin.allowedFahrtenbuchAuswaehlen) {
              Dialog.error(International.getMessage("Du hast als Admin {name} keine Berechtigung, ein Fahrtenbuch auszuwählen!",admin.name));
          }
        } while (!admin.allowedFahrtenbuchAuswaehlen);

        String dat = Dialog.dateiDialog(this,International.getString("Fahrtenbuch öffnen"),
                International.getString("efa Fahrtenbuch")+" (*.efb)","efb",Daten.efaDataDirectory,false);
        if (dat == null || dat.length()==0) haltProgram(International.getString("Kein Fahrtenbuch ausgewählt")+".", Daten.HALT_FILEOPEN);
        Daten.efaConfig.direkt_letzteDatei = dat;
        if (!Daten.efaConfig.writeFile()) {
            haltProgram(LogString.logstring_fileCreationFailed(Daten.efaConfig.getFileName(), International.getString("Konfigurationsdatei")), Daten.HALT_FILEOPEN);
        }
        Daten.fahrtenbuch = new Fahrtenbuch(Daten.efaConfig.direkt_letzteDatei);
      }
      Daten.actionOnDatenlisteNotFound = sveAction;
      if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null)
        Daten.fahrtenbuch.getDaten().mitglieder.getAliases();

    }
    Logger.log(Logger.INFO, Logger.MSG_EVT_LOGBOOKOPENED,
            International.getMessage("Fahrtenbuch '{filename}' geöffnet.",Daten.fahrtenbuch.getFileName()));
  }

  public boolean sindNochBooteUnterwegs() {
    return bootStatus.getBoote(BootStatus.STAT_UNTERWEGS).size()>0;
  }


  void statusLabelSetText(String s) {
    statusLabel.setText(s);
    // wenn Text zu lang, dann PreferredSize verringern, damit bei pack() die zu große Label-Breite nicht
    // zum Vergrößern des Fensters führt!
    if (statusLabel.getPreferredSize().getWidth() > this.getSize().getWidth())
      statusLabel.setPreferredSize(new Dimension((int)this.getSize().getWidth()-20,
                                                 (int)statusLabel.getPreferredSize().getHeight()));
  }

  void setButtonsLookAndFeel() {
    // VereinsLogo setzen
    if (Daten.efaConfig.efaDirekt_vereinsLogo.length()>0) try {
      logoLabel.setIcon(new ImageIcon(Daten.efaConfig.efaDirekt_vereinsLogo));
      logoLabel.setMinimumSize(new Dimension(200, 80));
      logoLabel.setPreferredSize(new Dimension(200, 80));
      logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    } catch(Exception e) {}

    // Look & Feel (Buttonfarben, Text) setzen
    this.fahrtbeginnButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeginnenFarbe));
    this.fahrtendeButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeendenFarbe));
    this.fahrtabbruchButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtAbbrechenFarbe));
    this.nachtragButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachtragFarbe));
    this.bootsstatusButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butBootsreservierungenFarbe));
    this.showFbButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenFarbe));
    this.statButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butStatistikErstellenFarbe));
    this.adminHinweisButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachrichtAnAdminFarbe));
    this.adminButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butAdminModusFarbe));
    this.spezialButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butSpezialFarbe));
    this.bootsstatusButton.setVisible(Daten.efaConfig.efaDirekt_butBootsreservierungenAnzeigen);
    this.showFbButton.setVisible(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenAnzeigen);
    this.statButton.setVisible(Daten.efaConfig.efaDirekt_butStatistikErstellenAnzeigen);
    this.adminHinweisButton.setVisible(Daten.efaConfig.efaDirekt_butNachrichtAnAdminAnzeigen);
    this.adminButton.setVisible(Daten.efaConfig.efaDirekt_butAdminModusAnzeigen);
    this.spezialButton.setVisible(Daten.efaConfig.efaDirekt_butSpezialAnzeigen);

    setButtonText();
  }


  public void setButtonText() {
    if (Daten.efaConfig == null) return;
    boolean fkey = Daten.efaConfig.efaDirekt_showButtonHotkey;
    this.fahrtbeginnButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeginnenText             + (fkey ? " [F2]" : ""));
    this.fahrtendeButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeendenText                + (fkey ? " [F3]" : ""));
    this.fahrtabbruchButton.setText(International.getString("Fahrt abbrechen")                + (fkey ? " [F4]" : ""));
    this.nachtragButton.setText(International.getString("Nachtrag")                           + (fkey ? " [F5]" : ""));
    this.bootsstatusButton.setText(International.getString("Bootsreservierungen")             + (fkey ? " [F6]" : ""));
    this.showFbButton.setText(International.getString("Fahrtenbuch anzeigen")                 + (fkey ? " [F7]" : ""));
    this.statButton.setText(International.getString("Statistik erstellen")                    + (fkey ? " [F8]" : ""));
    this.adminHinweisButton.setText(International.getString("Nachricht an Admin")             + (fkey ? " [F9]" : ""));
    this.adminButton.setText(International.getString("Admin-Modus")                           + (fkey ? " [Alt-F10]" : ""));
    this.spezialButton.setText(Daten.efaConfig.efaDirekt_butSpezialText                       + (fkey ? " [Alt-F11]" : ""));
    this.verfuegbareBooteLabel.setText(International.getString("verfügbare Boote")            + (fkey ? " [F10]" : ""));
    this.aufFahrtBooteLabel.setText(International.getString("Boote auf Fahrt")                + (fkey ? " [F11]" : ""));
    this.nichtVerfuegbareBooteLabel.setText(International.getString("nicht verfügbare Boote") + (fkey ? " [F12]" : ""));
    if (!Daten.efaConfig.efaDirekt_startMaximized) packFrame("setButtonText()");
  }


  public void iniBootsListen() {
    // Bootsliste aufbauen
    booteAlle = new Vector();
    if (Daten.fahrtenbuch.getDaten().boote == null) {
      haltProgram(International.getString("Fahrtenbuch enthält keine Bootsliste!"), Daten.HALT_FILEOPEN);
    }
    for (DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteFirst();
         d != null;  d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteNext()) {
      booteAlle.add(d.get(Boote.NAME) + (d.get(Boote.VEREIN).length()>0 ? " ("+d.get(Boote.VEREIN)+")" : "") );
    }

    // Boot Status einlesen
    bootStatus = new BootStatus(Daten.efaDataDirectory+Daten.DIREKTBOOTSTATUS);
    if (!EfaUtil.canOpenFile(bootStatus.getFileName())) {
      if (!bootStatus.writeFile()) {
          haltProgram(LogString.logstring_fileCreationFailed(bootStatus.getFileName(),International.getString("Bootsstatus-Liste")), Daten.HALT_FILEOPEN);
      }
    } else {
      if (!bootStatus.readFile()) {
          haltProgram(LogString.logstring_fileOpenFailed(bootStatus.getFileName(),International.getString("Bootsstatus-Liste")), Daten.HALT_FILEOPEN);
      }
    }
    for (int i=0; i<booteAlle.size(); i++) {
      DatenFelder d;
      if (bootStatus.getExact((String)booteAlle.get(i)) != null) {
        d = (DatenFelder)bootStatus.getComplete();
      } else {
        d = new DatenFelder(BootStatus._FELDANZ);
        d.set(BootStatus.NAME,(String)booteAlle.get(i));
        if (Daten.fahrtenbuch.getDaten().boote.getExactComplete((String)booteAlle.get(i)).get(Boote.VEREIN).length()>0) {
          d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_HIDE));
          d.set(BootStatus.BEMERKUNG,International.getString("wird nicht angezeigt"));
          Logger.log(Logger.WARNING,Logger.MSG_WARN_BOATADDEDWITHSTATUS1,
                  International.getMessage("Boot {boat} in Statusliste nicht gefunden; mit Status '{status}' hinzugefügt.",
                  (String)booteAlle.get(i),BootStatus.getStatusName(BootStatus.STAT_HIDE)));
        } else {
          d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_VERFUEGBAR));
          d.set(BootStatus.BEMERKUNG,BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR));
          Logger.log(Logger.WARNING,Logger.MSG_WARN_BOATADDEDWITHSTATUS2,
                  International.getMessage("Boot {boat} in Statusliste nicht gefunden; mit Status '{status}' hinzugefügt.",
                  (String)booteAlle.get(i),BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR)));
        }
        bootStatus.add(d);
      }
    }
    // Bootstatus: nicht existierende Boot aus Statusliste entfernen
    Vector remove = new Vector();
    for (DatenFelder d = (DatenFelder)bootStatus.getCompleteFirst();
         d != null; d = (DatenFelder)bootStatus.getCompleteNext()) {
      if (!booteAlle.contains(d.get(BootStatus.NAME)) &&
          !d.get(BootStatus.STATUS).equals(BootStatus.getStatusKey(BootStatus.STAT_UNTERWEGS))) remove.add(d.get(BootStatus.NAME));
    }
    for (int i=0; i<remove.size(); i++) {
      bootStatus.delete((String)remove.get(i));
      Logger.log(Logger.WARNING,Logger.MSG_WARN_BOATDELETEDFROMLIST,
              International.getMessage("Boot {boat} existiert in Statusliste, jedoch nicht in Bootsliste, und wurde daher entfernt.",
              (String)remove.get(i)));
    }
    // Statusliste speichern und Boote anzeigen
    if (!bootStatus.writeFile()) {
        haltProgram(LogString.logstring_fileWritingFailed(bootStatus.getFileName(),International.getString("Bootsstatus-Liste")), Daten.HALT_FILEOPEN);
    }
    updateBootsListen();

  }

  class BootsString implements Comparable {
    public String name;
    public int anzahl;
    public boolean sortByAnzahl;

    private String normalizeString(String s) {
      s = s.toLowerCase();
      if (s.indexOf("ä") >= 0) s = EfaUtil.replace(s,"ä","a",true);
      if (s.indexOf("Ä") >= 0) s = EfaUtil.replace(s,"Ä","a",true);
      if (s.indexOf("à") >= 0) s = EfaUtil.replace(s,"à","a",true);
      if (s.indexOf("á") >= 0) s = EfaUtil.replace(s,"á","a",true);
      if (s.indexOf("â") >= 0) s = EfaUtil.replace(s,"â","a",true);
      if (s.indexOf("ã") >= 0) s = EfaUtil.replace(s,"ã","a",true);
      if (s.indexOf("æ") >= 0) s = EfaUtil.replace(s,"æ","ae",true);
      if (s.indexOf("ç") >= 0) s = EfaUtil.replace(s,"ç","c",true);
      if (s.indexOf("è") >= 0) s = EfaUtil.replace(s,"è","e",true);
      if (s.indexOf("é") >= 0) s = EfaUtil.replace(s,"é","e",true);
      if (s.indexOf("è") >= 0) s = EfaUtil.replace(s,"è","e",true);
      if (s.indexOf("é") >= 0) s = EfaUtil.replace(s,"é","e",true);
      if (s.indexOf("ê") >= 0) s = EfaUtil.replace(s,"ê","e",true);
      if (s.indexOf("ì") >= 0) s = EfaUtil.replace(s,"ì","i",true);
      if (s.indexOf("í") >= 0) s = EfaUtil.replace(s,"í","i",true);
      if (s.indexOf("î") >= 0) s = EfaUtil.replace(s,"î","i",true);
      if (s.indexOf("ñ") >= 0) s = EfaUtil.replace(s,"ñ","n",true);
      if (s.indexOf("ö") >= 0) s = EfaUtil.replace(s,"ö","o",true);
      if (s.indexOf("Ö") >= 0) s = EfaUtil.replace(s,"Ö","o",true);
      if (s.indexOf("ò") >= 0) s = EfaUtil.replace(s,"ò","o",true);
      if (s.indexOf("ó") >= 0) s = EfaUtil.replace(s,"ó","o",true);
      if (s.indexOf("ô") >= 0) s = EfaUtil.replace(s,"ô","o",true);
      if (s.indexOf("õ") >= 0) s = EfaUtil.replace(s,"õ","o",true);
      if (s.indexOf("ø") >= 0) s = EfaUtil.replace(s,"ø","o",true);
      if (s.indexOf("ü") >= 0) s = EfaUtil.replace(s,"ü","u",true);
      if (s.indexOf("Ü") >= 0) s = EfaUtil.replace(s,"Ü","u",true);
      if (s.indexOf("ù") >= 0) s = EfaUtil.replace(s,"ù","u",true);
      if (s.indexOf("ú") >= 0) s = EfaUtil.replace(s,"ú","u",true);
      if (s.indexOf("û") >= 0) s = EfaUtil.replace(s,"û","u",true);
      if (s.indexOf("ß") >= 0) s = EfaUtil.replace(s,"ß","ss",true);
      return s;
    }

    public int compareTo(Object o) {
      BootsString other = (BootsString)o;
      String sThis  = (sortByAnzahl ? (anzahl < 10 ? "0" : "") + anzahl : "") + normalizeString(name);
      String sOther = (sortByAnzahl ? (other.anzahl < 10 ? "0" : "") + other.anzahl : "") + normalizeString(other.name);
      return sThis.compareTo(sOther);
    }

  }

  Vector sortBootsList(Vector v) {
    if (v == null || v.size() == 0) return v;

    BootsString[] a = new BootsString[v.size()];
    for (int i=0; i<v.size(); i++) {
      a[i] = new BootsString();
      DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(removeDoppeleintragFromBootsname((String)v.get(i)));
      int anz = 99;
      if (d != null) {
          anz = EfaTypes.getNumberOfRowers(d.get(Boote.ANZAHL));
          if (anz == 0) anz = 99;
      }
      if (anz<0) anz = 0;
      if (anz>99) anz = 99;
      a[i].anzahl = anz;
      a[i].name = (String)v.get(i);
      a[i].sortByAnzahl = (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_sortByAnzahl);
    }
    Arrays.sort(a);

    Vector vv = new Vector();
    int anz = -1;
    for (int i=0; i<a.length; i++) {
      if (a[i].anzahl != anz) {
          String s = null;
          switch(a[i].anzahl) {
              case 1: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_1);
                      break;
              case 2: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_2);
                      break;
              case 3: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_3);
                      break;
              case 4: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_4);
                      break;
              case 5: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_5);
                      break;
              case 6: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_6);
                      break;
              case 8: s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_8);
                      break;
          }
          if (s == null) {
              s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMROWERS, EfaTypes.TYPE_NUMROWERS_OTHER);
          }
        vv.add("---------- " + s + " ----------");
        anz = a[i].anzahl;
      }
      vv.add(a[i].name);
    }
    return vv;
/*
    if (Daten.efaConfig == null || !Daten.efaConfig.efaDirekt_sortByAnzahl) return v;

    String[] a = new String[v.size()];
    for (int i=0; i<v.size(); i++) {
      DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(removeDoppeleintragFromBootsname((String)v.get(i)));
      int anz = 99;
      if (d != null) anz = EfaUtil.string2date(d.get(Boote.ANZAHL),99,0,0).tag;
      if (anz<0) anz = 0;
      if (anz>99) anz = 99;
      a[i] = (anz<10 ? "0" : "") + anz + v.get(i);
    }
    Arrays.sort(a);

    Vector vv = new Vector();
    int anz = -1;
    for (int i=0; i<a.length; i++) {
      int ii = EfaUtil.string2int(a[i].substring(0,2),99);
      if (ii != anz) {
        vv.add("---------- " + (ii != 99 ? ii+"er" : "andere") + " ----------");
        anz = ii;
      }
      vv.add(a[i].substring(2,a[i].length()));
    }
    return vv;
*/
  }

  void updateBootsListen() {
    booteVerfuegbarListData = sortBootsList(bootStatus.getBoote(BootStatus.STAT_VERFUEGBAR));
    booteVerfuegbarListData.add(0,"<"+International.getString("anderes Boot")+">");
    booteAufFahrtListData = sortBootsList(bootStatus.getBoote(BootStatus.STAT_UNTERWEGS));
    booteNichtVerfuegbarListData = sortBootsList(bootStatus.getBoote(BootStatus.STAT_NICHT_VERFUEGBAR));

    if (Daten.efaConfig != null && bootStatus != null && Daten.fahrtenbuch != null && Daten.efaConfig.efaDirekt_showZielnameFuerBooteUnterwegs) {
      for (int i=0; booteAufFahrtListData != null && i<booteAufFahrtListData.size(); i++) {
        String b = (String)booteAufFahrtListData.get(i);
        if (b != null) {
          DatenFelder d = bootStatus.getExactComplete(b);
          if (d != null) {
            d = Daten.fahrtenbuch.getExactComplete(d.get(BootStatus.LFDNR));
            if (d != null) {
              String ziel = d.get(Fahrtenbuch.ZIEL);
              if (ziel != null && ziel.length()>0) {
                booteAufFahrtListData.set(i,b + "     -> " + ziel);
              }
            }
          }
        }
      }
    }

    // es gibt einen komischen Bug, der manchmal aufzutreten scheint, wenn in gerade selektiertes Boot aus
    // der Statusliste verschwindet. Der Bug läßt sich manchmal reproduzieren, indem als Mitglied ein Boot
    // für sofort reserviert wird. Während dann der Focus noch auf dem Boot steht, nimmt der ReservierungsChecker
    // das Boot aus der Liste. Die Folge ist dann manchmal, daß die Liste einen riesigen Freiraum bekommt.
    // Manchmal verändert sich sogar die Größe des Frames dadurch. Vermutlich handelt es sich hierbei um einen
    // Java-Bug.
    // Um den Bug zu umgehen, wurden folgende drei Zeilen eingefügt. Es konnte jedoch leider nicht überprüft
    // werden, ob diese Zeilen den Bug beheben, da er sich leider auch ohne diese Zeilen nicht mehr reproduzieren
    // ließ.
    booteVerfuegbar.setSelectedIndex(-1);
    booteAufFahrt.setSelectedIndex(-1);
    booteNichtVerfuegbar.setSelectedIndex(-1);

    booteVerfuegbar.setListData(booteVerfuegbarListData);
    booteAufFahrt.setListData(booteAufFahrtListData);
    booteNichtVerfuegbar.setListData(booteNichtVerfuegbarListData);

    statusLabelSetText(International.getString("Kein Boot ausgewählt."));
    booteVerfuegbar.setSelectedIndex(0);
    booteVerfuegbar.requestFocus();
  }


  void eintragAendern(String boot) {
    DatenFelder d = (DatenFelder)bootStatus.getExactComplete(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      String s = International.getString("Programmfehler") + ": " +
              International.getMessage("Boot {boat} nicht in der Statusliste gefunden!",boot);
      Dialog.error(s);
      Logger.log(Logger.ERROR,Logger.MSG_ERR_BOATNOTFOUNDINSTATUS,s);
      return;
    }

    if (d.get(BootStatus.LFDNR).trim().length()==0) {
      // keine LfdNr eingetragen: Das kann passieren, wenn der Admin den Status der Bootes manuell geändert hat!
      String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat}.",boot);
      Dialog.error(s + " " + International.getString("Der Eintrag kann nicht geändert werden."));
      Logger.log(Logger.ERROR,Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
      return;
    }
    setEnabled(false);
    efaFrame.direktFahrtAnfangKorrektur(boot,d.get(BootStatus.LFDNR));
  }


  void showBootStatus(int listnr, JList list, int direction) {
    String boot = null;

    try { // list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
      if (list != null && !list.isSelectionEmpty()) boot = listGetSelectedValue(list);
    } catch(Exception e) {
      EfaUtil.foo();
    }

    if (bootStatus != null && boot != null && list != null && boot.startsWith("---------- ")) {
      try {
        int i = list.getSelectedIndex() + direction;
        if (i<0) i=1; // i<0 kann nur erreicht werden, wenn vorher i=0 und direction=-1; dann darf nicht auf i=0 gesprungen werden, da wir dort herkommen, sondern auf i=1
        list.setSelectedIndex(i);
        boot = listGetSelectedValue(list);
      } catch(Exception e) { /* just to be sure */ }
    }

    if (bootStatus != null && boot != null) {
      String stat;
      if (bootStatus.getExact(boot) != null) {
        stat = ((DatenFelder)(bootStatus.getComplete())).get(BootStatus.BEMERKUNG);
      } else {
        stat = International.getString("anderes oder fremdes Boot");
      }
      String bootstyp = "";
      String rudererlaubnis = "";
      if (listnr == 1) {
        DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot);
        if (d != null) {
          bootstyp = " ("+Boote.getDetailBezeichnung(d)+")";
          Vector gr = Boote.getGruppen(d);
          for (int i=0; i<gr.size(); i++)  {
            rudererlaubnis = (rudererlaubnis.length()>0 ? rudererlaubnis + ", " : 
                "; "+ International.getMessage("nur für {something}",(String)gr.get(i)));
          }
        }
      }
      statusLabelSetText(boot+": "+stat + bootstyp + rudererlaubnis);
    }
    if (listnr != 1) booteVerfuegbar.setSelectedIndices(new int[0]);
    if (listnr != 2) booteAufFahrt.setSelectedIndices(new int[0]);
    if (listnr != 3) booteNichtVerfuegbar.setSelectedIndices(new int[0]);
  }


  void doppelklick(int listnr, JList list) {
    if (list == null || list.getSelectedIndex() < 0) return;

    String boot = null;
    try { // list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
      if (list != null && !list.isSelectionEmpty()) boot = listGetSelectedValue(list);
    } catch(Exception e) {
      EfaUtil.foo();
    }

    if (boot == null) return;

    // Handelt es sich um ein Boot, das auf Fahrt ist, aber trotzdem bei "nicht verfügbar" angezeigt wird?
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d != null && listnr == 3 && EfaUtil.string2date(d.get(BootStatus.LFDNR),0,0,0).tag>0) listnr = 2;

    if (listnr == 1 || listnr == 3) {
      String fahrtBeginnen = EfaUtil.replace(this.fahrtbeginnButton.getText(),">>>","");
      int pos = fahrtBeginnen.indexOf(" [");
      if (pos>0) fahrtBeginnen = fahrtBeginnen.substring(0,pos-1);
      int auswahl = -1;
      if (listnr == 1) {
        if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
          auswahl = Dialog.auswahlDialog(International.getString("Boot")+" "+boot,
                  International.getMessage("Was möchtest Du mit dem Boot {boat} machen?",boot),
                                   fahrtBeginnen,
                                   International.getString("Boot reservieren"),
                                   International.getString("Nichts"));
        } else {
          auswahl = Dialog.auswahlDialog(International.getString("Boot")+" "+boot,
                  International.getMessage("Was möchtest Du mit dem Boot {boat} machen?",boot),
                                   fahrtBeginnen,
                                   International.getString("Nichts"),
                                   false);
        }
      } else {
        auswahl = Dialog.auswahlDialog(International.getString("Boot")+" "+boot,
                International.getMessage("Was möchtest Du mit dem Boot {boat} machen?",boot),
                                   fahrtBeginnen,
                                   International.getString("Bootsreservierungen anzeigen"),
                                   International.getString("Nichts"));
      }
      switch (auswahl) {
        case 0: this.fahrtbeginnButton_actionPerformed(null); break;
        case 1: if (listnr != 1 || Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
                  this.bootsstatusButton_actionPerformed(null);
                } // else: nothing to do!
                break;
        default: return;
      }
    }
    if (listnr == 2) {
      String fahrtBeenden = EfaUtil.replace(this.fahrtendeButton.getText(),"<<<","");
      int pos = fahrtBeenden.indexOf(" [");
      if (pos>0) fahrtBeenden = fahrtBeenden.substring(0,pos);
      int auswahl = -1;
      if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
        auswahl = Dialog.auswahlDialog(International.getString("Boot")+" "+boot,
                International.getMessage("Was möchtest Du mit dem Boot {boat} machen?",boot),
                                   fahrtBeenden,
                                   International.getString("Eintrag ändern"),
                                   International.getString("Fahrt abbrechen"),
                                   International.getString("Boot reservieren"),
                                   International.getString("Nichts"));
      } else {
        auswahl = Dialog.auswahlDialog(International.getString("Boot")+" "+boot,
                International.getMessage("Was möchtest Du mit dem Boot {boat} machen?",boot),
                                   fahrtBeenden,
                                   International.getString("Eintrag ändern"),
                                   International.getString("Fahrt abbrechen"),
                                   International.getString("Nichts"));
      }
      switch (auswahl) {
        case 0: this.fahrtendeButton_actionPerformed(null); break;
        case 1: this.eintragAendern(boot); break;
        case 2: this.fahrtabbruchButton_actionPerformed(null); break;
        case 3: if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
                  this.bootsstatusButton_actionPerformed(null);
                } // else: nothing to do!
                break;
        default: return;
      }
    }
  }

  void booteVerfuegbar_mouseClicked(MouseEvent e) {
    showBootStatus(1,booteVerfuegbar,1);

    if (e != null && e.getClickCount() == 2) { // Doppelklick
      doppelklick(1,booteVerfuegbar);
    }
  }
  void booteAufFahrt_mouseClicked(MouseEvent e) {
    showBootStatus(2,booteAufFahrt,1);

    if (e != null && e.getClickCount() == 2) { // Doppelklick
      doppelklick(2,booteAufFahrt);
    }
  }
  void booteNichtVerfuegbar_mouseClicked(MouseEvent e) {
    showBootStatus(3,booteNichtVerfuegbar,1);

    if (e != null && e.getClickCount() == 2) { // Doppelklick
      doppelklick(3,booteNichtVerfuegbar);
    }
  }
  void booteVerfuegbar_keyReleased(KeyEvent e) {
    if (e != null) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        doppelklick(1,booteVerfuegbar);
        return;
      }
      scrollToBoot(booteVerfuegbar,booteVerfuegbarListData,String.valueOf(e.getKeyChar()),15);
    }
    showBootStatus(1,booteVerfuegbar,(e != null && e.getKeyCode() == 38 ? -1 : 1)); // KeyCode 38 == Cursor Up
  }
  void booteAufFahrt_keyReleased(KeyEvent e) {
    if (e != null) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        doppelklick(2,booteAufFahrt);
        return;
      }
      scrollToBoot(booteAufFahrt,booteAufFahrtListData,String.valueOf(e.getKeyChar()),10);
    }
    showBootStatus(2,booteAufFahrt,(e != null && e.getKeyCode() == 38 ? -1 : 1)); // KeyCode 38 == Cursor Up
  }
  void booteNichtVerfuegbar_keyReleased(KeyEvent e) {
    if (e != null) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        doppelklick(3,booteNichtVerfuegbar);
        return;
      }
      scrollToBoot(booteNichtVerfuegbar,booteNichtVerfuegbarListData,String.valueOf(e.getKeyChar()),5);
    }
    showBootStatus(3,booteNichtVerfuegbar,(e != null && e.getKeyCode() == 38 ? -1 : 1)); // KeyCode 38 == Cursor Up
  }

  // scrolle in der Liste list (deren Inhalt der Vector boote ist), zu dem Eintrag
  // mit dem Namen such und selektiere ihn. Zeige unterhalb des Boote bis zu plus weitere Einträge.
  private void scrollToBoot(JList list, Vector boote, String such, int plus) {
    if (list == null || boote == null || such == null || such.length()==0) return;
    try {
      int start = 0;
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_sortByAnzahl) {
        if (such.charAt(0)>='0' && such.charAt(0)<='9') {
          such = "---------- "+such;
        } else {
          start = list.getSelectedIndex();
          if (start<0) start = 0;
          while (start>0 && !((String)boote.get(start)).startsWith("---------- ")) start--;
        }
      }

      int index = -1;
      for (int i=start; i<boote.size(); i++) {
        if (((String)boote.get(i)).toLowerCase().startsWith(such)) {
          index = i;
          break;
        }
      }
      if (index < 0) return;

      list.setSelectedIndex(index);
      list.scrollRectToVisible(list.getCellBounds(index, (index+plus >= boote.size() ? boote.size()-1 : index+plus) ));
    } catch(Exception ee) { /* just to be sure */ }


/*    try {
      bootStatus.ignoreCase(true);
      String boot;
      for (boot = bootStatus.getFirst(such); boot != null && !boote.contains(boot); boot = bootStatus.getNext(such));
      if (boot != null && boote.contains(boot)) {
        int i = boote.indexOf(boot)+increment;
        list.setSelectedIndex(i);
        list.scrollRectToVisible(list.getCellBounds(i, (i+plus >= boote.size()+increment ? boote.size()+increment-1 : i+plus) ));
      }
    } catch(Exception ee) {}
    bootStatus.ignoreCase(false);*/
  }

  private String listGetSelectedValue(JList list) {
    if (list == null || list.isSelectionEmpty()) return null;
    String boot = (String)list.getSelectedValue();
    int pos = boot.indexOf("->");
    if (pos > 0) {
      boot = boot.substring(0,pos).trim();
    }
    return boot;
  }

  private static String removeDoppeleintragFromBootsname(String boot) {
    if (boot == null || boot.length()==0) return boot;
    if (boot.charAt(boot.length()-1) != ']') return boot;
    int anf = boot.indexOf(" [");
    if (anf<0) return boot;
    return boot.substring(0,anf);
  }

  void fahrtbeginnButton_actionPerformed(ActionEvent e) {
    alive();
    int status = BootStatus.STAT_VERFUEGBAR;

    String boot = null;
    try {
      if (!booteVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteVerfuegbar);
      if (boot == null) {
        if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
        if (boot == null) {
          if (!booteNichtVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteNichtVerfuegbar);
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }

    if (boot == null) {
      Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
      this.booteVerfuegbar.requestFocus();
      this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
      return;
    }

    if (booteVerfuegbar.getSelectedIndex()==0) { // <anderes Boot> ausgewählt!
      boot = null;
    } else {
      if (!checkFahrtbeginnFuerBoot(boot,1)) return;
      boot = removeDoppeleintragFromBootsname(boot);
    }

    setEnabled(false);
    efaFrame.direktFahrtAnfang(boot);
  }

  // mode bestimmt die Art der Checks
  // mode==1 - alle Checks durchführen
  // mode==2 - nur solche Checks durchführen, bei denen es egal ist, ob das Boot aus der Liste direkt ausgewählt wurde
  //           oder manuell über <anders Boot> eingegeben wurde. Der Aufruf von checkFahrtbeginnFuerBoot mit mode==2
  //           erfolgt aus EfaFrame.java.
  public boolean checkFahrtbeginnFuerBoot(String boot, int mode) {
    if (boot == null) return true;
    DatenFelder d = bootStatus.getExactComplete(boot);
    String bootsname = removeDoppeleintragFromBootsname(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return false; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      if (mode == 2) return true; // anderes Boot
      String s = International.getString("Programmfehler") + ": " +
              International.getMessage("Boot {boat} nicht in der Statusliste gefunden!",boot);
      Dialog.error(s);
      Logger.log(Logger.ERROR,Logger.MSG_ERR_BOATNOTFOUNDINSTATUS,s);
      return false;
    }
    int status = BootStatus.getStatusID(d.get(BootStatus.STATUS));

    if (status == BootStatus.STAT_UNTERWEGS) {
      if (mode == 1) {
        switch (Dialog.auswahlDialog(International.getString("Boot bereits unterwegs"),
                                     International.getMessage("Das Boot {boat} ist laut Liste bereits unterwegs.",bootsname) + "\n" +
                                     (d != null ? International.getString("Bemerkung")+": " + d.get(BootStatus.BEMERKUNG) + "\n" : "") +
                                     "\n" +
                                     International.getString("Was möchtest Du tun?"),
                                     International.getString("Neue Fahrt beginnen"),
                                     International.getString("Vorhandenen Eintrag ändern"),
                                     International.getString("Nichts"))) {
          case 0:
            break;
          case 1:
            eintragAendern(boot);
            return false;
          case 2:
            return false;
          default:
            return false;
        }
      } else {
        if (Dialog.yesNoCancelDialog(International.getString("Boot bereits unterwegs"),
                                     International.getMessage("Das Boot {boat} ist laut Liste bereits unterwegs.",bootsname) + "\n" +
                                     (d != null ? International.getString("Bemerkung")+": " + d.get(BootStatus.BEMERKUNG) + "\n" : "") +
                                     "\n" +
                                     International.getString("Möchtest Du trotzdem mit dem Boot rudern?"))
                      != Dialog.YES) return false;
      }
    }
    if (status == BootStatus.STAT_NICHT_VERFUEGBAR && !d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
      if (Dialog.yesNoCancelDialog(International.getString("Boot gesperrt"),
                                     International.getMessage("Das Boot {boat} ist laut Liste nicht verfügbar.",bootsname) + "\n" +
                                     (d != null ? International.getString("Bemerkung")+": " + d.get(BootStatus.BEMERKUNG) + "\n" : "") +
                                     "\n" +
                                             International.getString("Möchtest Du trotzdem mit dem Boot rudern?"))
                    != Dialog.YES) return false;
    }


    DatenFelder d2 = bootStatus.getExactComplete(removeDoppeleintragFromBootsname(boot));
    Reservierung res = BootStatus.getReservierung(d,System.currentTimeMillis(),Daten.efaConfig.efaDirekt_resLookAheadTime);
    if (res == null && d2 != null) res = BootStatus.getReservierung(d2,System.currentTimeMillis(),Daten.efaConfig.efaDirekt_resLookAheadTime);
    if (res != null) {
      if (Dialog.yesNoCancelDialog(International.getString("Boot reserviert"),
              International.getMessage("Das Boot {boat} ist {currently_or_in_x_minutes} für {name} reserviert.",
                                       bootsname,
                                       (res.gueltigInMinuten == 0 ? 
                                           International.getString("zur Zeit") :
                                           International.getMessage("in {x} Minuten",(int)res.gueltigInMinuten)),
                                           res.name)+"\n"+
              (res.grund.length()>0 ? " ("+International.getString("Grund")+": "+res.grund+")\n" : "") +
              International.getMessage("Die Reservierung liegt {from_time_to_time} vor.",BootStatus.makeReservierungText(res))+"\n"+
              International.getString("Möchtest Du trotzdem mit dem Boot rudern?"))
                    != Dialog.YES) return false;
    }
    if (d.get(BootStatus.BOOTSSCHAEDEN).trim().length() > 0 || (d2 != null && d2.get(BootStatus.BOOTSSCHAEDEN).trim().length() > 0)) {
      if (Dialog.yesNoDialog(International.getString("Bootsschaden gemeldet"),
              International.getMessage("Für das Boot {boat} wurde folgender Bootsschaden gemeldet:",bootsname)+"\n"+
              "\""+
              (d.get(BootStatus.BOOTSSCHAEDEN).trim().length() > 0 ? d.get(BootStatus.BOOTSSCHAEDEN).trim() : d2.get(BootStatus.BOOTSSCHAEDEN).trim())
              +"\"\n\n"+
              International.getString("Möchtest Du trotzdem mit dem Boot rudern?"))
                             != Dialog.YES) return false;
    }
    return true;
  }

  private String createStatusString(String fahrttype, String ziel, String datum, String zeit, String person) {
    String aufFahrtart = "";
    if (Daten.efaTypes != null && fahrttype != null) {
      if (fahrttype.equals(EfaTypes.TYPE_TRIP_REGATTA)) {
          aufFahrtart = " " + 
          International.getMessage("auf {trip_type}",Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_REGATTA));
      }
      if (fahrttype.equals(EfaTypes.TYPE_TRIP_JUMREGATTA)) {
          aufFahrtart = " " +
          International.getMessage("auf {trip_type}",Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_JUMREGATTA));
      }
      if (fahrttype.equals(EfaTypes.TYPE_TRIP_TRAININGCAMP)) {
          aufFahrtart = " " +
          International.getMessage("auf {trip_type}",Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_TRAININGCAMP));
      }
      if (fahrttype.startsWith(EfaTypes.TYPE_TRIP_MULTIDAY)) {
          aufFahrtart = " " +
          International.getMessage("auf {trip_type}",Daten.efaTypes.getValue(EfaTypes.CATEGORY_TRIP, EfaTypes.TYPE_TRIP_MULTIDAY));
      }
    }
    String nachZiel = "";
    if (aufFahrtart.length() == 0 && ziel.length() > 0) {
      nachZiel = " " + International.getMessage("nach {destination}",ziel);
    }
    return  International.getString("unterwegs")+aufFahrtart+nachZiel+
            " " + International.getMessage("seit {date}",datum) +
            (zeit.trim().length()>0 ? " " + International.getMessage("um {time}",zeit) : "") +
            " "+International.getMessage("mit {crew}",person);
  }

  private boolean isMultiDayFahrtart(String fahrttype) {
      if (fahrttype.equals(EfaTypes.TYPE_TRIP_REGATTA)) return true;
      if (fahrttype.equals(EfaTypes.TYPE_TRIP_JUMREGATTA)) return true;
      if (fahrttype.equals(EfaTypes.TYPE_TRIP_TRAININGCAMP)) return true;
      if (fahrttype.startsWith(EfaTypes.TYPE_TRIP_MULTIDAY)) return true;
      return false;
  }

  public void fahrtBegonnen(String boot, String lfdNr, String datum, String zeit, String person, String fahrttype, String ziel) {
    int status = BootStatus.STAT_VERFUEGBAR;
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPUNKNOWNBOAT,
              International.getString("Fahrtbeginn eines unbekannten Bootes")+": "+boot);
      d = new DatenFelder(BootStatus._FELDANZ);
      d.set(BootStatus.NAME,boot);
      d.set(BootStatus.UNBEKANNTESBOOT,"+");
    } else {
      status = BootStatus.getStatusID(d.get(BootStatus.STATUS));
    }

    if (status != BootStatus.STAT_VERFUEGBAR && !d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
      String tmp = null;

      // Bootsnamen um Timestamp erweitern, der bislang noch nicht verwendet wurde
      for (char c = 'A'-1; c<='Z' && tmp == null; c++) {
        tmp = boot + " ["+EfaUtil.getCurrentTimeStampDD_MM_HH_MM() + (c<'A' ? "" : ""+c) + "]";
        if (bootStatus.getExact(tmp) != null) tmp = null;
      }

      if (tmp == null) { // alle 27 Timestamps für dieses Boot schon vergeben: sollte niemals passieren ...
        Logger.log(Logger.ERROR, Logger.MSG_ERR_TRIPSTARTNOTPOSSIBLE1,
                International.getMessage("Fahrtbeginn des Bootes {boat} nicht möglich!",boot));
        return;
      }

      boot = tmp;
      Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPSTART_BNA,
              International.getMessage("Fahrtbeginn eines Bootes, welches laut Liste nicht verfügbar ist (Status [{status}]: {notes}).",
                                       status,d.get(BootStatus.BEMERKUNG)) +
              International.getMessage("Neuer Eintrag als Boot: {boat}",boot));
      DatenFelder old_d = d;
      d = new DatenFelder(BootStatus._FELDANZ);
      d.set(BootStatus.NAME,boot);
      d.set(BootStatus.UNBEKANNTESBOOT,"+"); // Doppeleinträge sind immer "unbekannte" Boote!!
      // Bugfix: auch Bootsschäden und Reservierungen müssen übernommen werden, da sonst diese Daten u.U. verloren gehen
      // könnten, wenn der andere Eintrag als erstes und dieser als zweites beendet wird.
      d.set(BootStatus.BOOTSSCHAEDEN,old_d.get(BootStatus.BOOTSSCHAEDEN));
      d.set(BootStatus.RESERVIERUNGEN,old_d.get(BootStatus.RESERVIERUNGEN));
    }

    bootStatus.delete(boot);
    d.set(BootStatus.LFDNR,lfdNr);
    if (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && isMultiDayFahrtart(fahrttype)) {
        d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_NICHT_VERFUEGBAR));
    } else {
        d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_UNTERWEGS));
    }
    d.set(BootStatus.BEMERKUNG,createStatusString(fahrttype,ziel,datum,zeit,person));
    bootStatus.add(d);
    setKombiBootStatus(boot,"",BootStatus.STAT_VORUEBERGEHEND_VERSTECKEN,
            International.getString("vorübergehend von efa versteckt"));
    if (!bootStatus.writeFile()) {
        LogString.logError_fileWritingFailed(bootStatus.getFileName(), International.getString("Bootsstatus-Liste"));
    }
    updateBootsListen();
  }

  public void fahrtBeginnKorrigiert(String boot, String lfdNr, String datum, String zeit, String person, String fahrttype, String ziel, String ursprBoot) {
    if (!boot.equals(ursprBoot)) {
      // Bootsname wurde geändert
      Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPSTART_CORR,
              International.getString("Fahrtbeginn korrigiert")+": #"+lfdNr+" - "+
              International.getMessage("Änderung des Bootsnamens von {original_name} in {new_name}.",ursprBoot,boot));
      fahrtBeendet(ursprBoot,true);
      fahrtBegonnen(boot,lfdNr,datum,zeit,person,fahrttype,ziel);
      return;
    }

    // Bootsname wurde nicht geändert
    int status = BootStatus.STAT_VERFUEGBAR;
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPSTART_CORRUKNW,
              International.getString("Fahrtbeginn korrigiert")+": "+
              International.getString("Korrektur des Fahrtbeginns eines unbekannten Bootes")+": "+boot);
      return;
    }
    status = BootStatus.getStatusID(d.get(BootStatus.STATUS));
    if (status != BootStatus.STAT_UNTERWEGS) {
      Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPSTART_CORRSNOT,
              International.getString("Fahrtbeginn korrigiert")+": "+
              International.getMessage("Korrektur des Fahrtbeginns des Bootes {boat}, das nicht unterwegs ist [Status: {status}]",boot,status));
      return;
    }
    bootStatus.delete(boot);
    d.set(BootStatus.LFDNR,lfdNr);
    if (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && isMultiDayFahrtart(fahrttype)) {
        d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_NICHT_VERFUEGBAR));
    } else {
        d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_UNTERWEGS));
    }
    d.set(BootStatus.BEMERKUNG,createStatusString(fahrttype,ziel,datum,zeit,person));
    bootStatus.add(d);
    setKombiBootStatus(boot,"",BootStatus.STAT_VORUEBERGEHEND_VERSTECKEN,
            International.getString("vorübergehend von efa versteckt"));
    if (!bootStatus.writeFile()) {
        LogString.logError_fileWritingFailed(bootStatus.getFileName(), International.getString("Bootsstatus-Liste"));
    }
    updateBootsListen();
  }

  void fahrtendeButton_actionPerformed(ActionEvent e) {
    alive();
    String boot = null;
    try {
      if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
      if (boot == null && Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && !booteNichtVerfuegbar.isSelectionEmpty()) {
        // prüfen, ob vielleicht ein Boot in der Liste "nicht verfügbar" auf Regatta oder Wanderfahrt unterwegs ist
        boot = listGetSelectedValue(booteNichtVerfuegbar);
        if (boot != null  && bootStatus.getExact(boot) != null) {
          DatenFelder d = (DatenFelder)bootStatus.getComplete();
          if (EfaUtil.string2int(d.get(BootStatus.LFDNR),0) == 0) boot = null; // keine gültige LfdNr
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }
    if (boot == null) {
      Dialog.error(International.getMessage("Bitte wähle zuerst {from_the_right_list} ein Boot aus, welches unterwegs ist!",
              (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar ? 
                  International.getString("aus einer der rechten Listen") : 
                  International.getString("aus der rechten oberen Liste"))));
      this.booteAufFahrt.requestFocus();
      this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
      return;
    }
    if (bootStatus.getExact(boot) != null) {
      DatenFelder d = (DatenFelder)bootStatus.getComplete();
      if (d.get(BootStatus.LFDNR).trim().length()==0) {
        // keine LfdNr eingetragen: Das kann passieren, wenn der Admin den Status der Bootes manuell geändert hat!
        String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat}.",boot)
                 + " " + International.getString("Die Fahrt kann nicht beendet werden.");
        Logger.log(Logger.ERROR,Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
        Dialog.error(s);
        return;
      }
      if (Daten.fahrtenbuch.getExact(d.get(BootStatus.LFDNR)) == null) {
        String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat} und LfdNr {lfdnr}.",
                boot,d.get(BootStatus.LFDNR))
                 + " " + International.getString("Die Fahrt kann nicht beendet werden.");
        Logger.log(Logger.ERROR,Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
        Dialog.error(s);
        return;
      }
      setEnabled(false);
      efaFrame.direktFahrtEnde(boot,d.get(BootStatus.LFDNR));
    } else {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      String s = International.getString("Programmfehler") + ": " +
              International.getMessage("Boot {boat} nicht in der Statusliste gefunden!",boot);
      Dialog.error(s);
      Logger.log(Logger.ERROR,Logger.MSG_ERR_BOATNOTFOUNDINSTATUS,s);
    }
  }

  public void fahrtBeendet(String boot, boolean interaktiv) {
    DatenFelder d = (DatenFelder)bootStatus.getExactComplete(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      String s = International.getString("Programmfehler") + ": " +
              International.getMessage("Boot {boat} nicht in der Statusliste gefunden!",boot);
      if (interaktiv) {
          Dialog.error(s);
      }
      Logger.log(Logger.ERROR,Logger.MSG_ERR_BOATNOTFOUNDINSTATUS,s);
      return;
    }

    // Boot aus Statustliste löschen
    bootStatus.delete(boot);

    if (!d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) {
      // es handelt sich um *kein* unbekanntes Boot, also auch nicht um einen Doppelaustrag

      // gibt es einen zu diesem Bootsnamen passenden Doppelaustrag?
      DatenFelder dd = bootStatus.getCompleteFirst(boot+" [");
      if (dd != null) {
        // Ja, Doppeleintrag existiert: Dann eckige Klammern dort löschen und Boot *nicht* als verfügbar markieren
        bootStatus.delete(dd.get(BootStatus.NAME));
        dd.set(BootStatus.NAME,boot);
        dd.set(BootStatus.UNBEKANNTESBOOT,"-");
        bootStatus.add(dd);
      } else {
        // Nein, Doppeleintrag existiert nicht: Boot als verfügbar markieren
        d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_VERFUEGBAR));
        d.set(BootStatus.BEMERKUNG,BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR));
        d.set(BootStatus.LFDNR,"");
        bootStatus.add(d);
        setKombiBootStatus(boot,"",BootStatus.STAT_VERFUEGBAR,BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR));
      }
    }

    if (!bootStatus.writeFile()) {
        LogString.logError_fileWritingFailed(bootStatus.getFileName(), International.getString("Bootsstatus-Liste"));
    }
    if (interaktiv) {
      updateBootsListen();
      efaDirektBackgroundTask.interrupt();
    }
  }

  void fahrtabbruchButton_actionPerformed(ActionEvent e) {
    alive();
    String boot = null;
    try {
      if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
      if (boot == null && Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && !booteNichtVerfuegbar.isSelectionEmpty()) {
        // prüfen, ob vielleicht ein Boot in der Liste "nicht verfügbar" auf Regatta oder Wanderfahrt unterwegs ist
        boot = listGetSelectedValue(booteNichtVerfuegbar);
        if (boot != null  && bootStatus.getExact(boot) != null) {
          DatenFelder d = (DatenFelder)bootStatus.getComplete();
          if (EfaUtil.string2int(d.get(BootStatus.LFDNR),0) == 0) boot = null; // keine gülte LfdNr
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }
    if (boot == null) {
      Dialog.error(International.getMessage("Bitte wähle zuerst {from_the_right_list} ein Boot aus, welches unterwegs ist!",
              (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar ?
                  International.getString("aus einer der rechten Listen") :
                  International.getString("aus der rechten oberen Liste"))));
      this.booteAufFahrt.requestFocus();
      this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
      return;
    }
    fahrtAbbruch(boot,true);
  }

  void fahrtAbbruch(String boot, boolean interaktiv) {
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      String s = International.getString("Programmfehler") + ": " +
              International.getMessage("Boot {boat} nicht in der Statusliste gefunden!",boot);
      if (interaktiv) {
          Dialog.error(s);
      }
      Logger.log(Logger.ERROR,Logger.MSG_ERR_BOATNOTFOUNDINSTATUS,s);
      return;
    }
    if (interaktiv && Dialog.yesNoDialog(International.getString("Fahrt abbrechen"),
                                         International.getMessage("Die Fahrt des Bootes {boat} sollte nur abgebrochen werden, "+
                                           "wenn sie nie stattgefunden hat. In diesem Fall wird der begonnene Eintrag wieder entfernt.",
                                           removeDoppeleintragFromBootsname(boot))+
                                           "\n"+
                                           International.getString("Möchtest Du die Fahrt wirklich abbrechen?")) != Dialog.YES) return;
    if (d.get(BootStatus.LFDNR).trim().length() == 0) {
        String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat}.",d.get(BootStatus.NAME))
                 + " " + International.getString("Die Fahrt kann nicht abgebrochen werden.");
        Logger.log(Logger.ERROR,Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
        if (interaktiv) Dialog.error(s);
        return;
    }
    if (Daten.fahrtenbuch.getExact(d.get(BootStatus.LFDNR)) == null) {
        String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat} und LfdNr {lfdnr}.",
                d.get(BootStatus.NAME),d.get(BootStatus.LFDNR))
                 + " " + International.getString("Die Fahrt kann nicht abgebrochen werden.");
        Logger.log(Logger.ERROR,Logger.MSG_ERR_NOLOGENTRYFORBOAT,
              s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
        if (interaktiv) Dialog.error(s);
      return;
    }
    Logger.log(Logger.INFO, Logger.MSG_EVT_TRIPABORT,
            International.getString("Fahrtabbruch")+": #"+d.get(BootStatus.LFDNR)+" - "+d.get(BootStatus.NAME)+" ("+d.get(BootStatus.BEMERKUNG)+")");
    Daten.fahrtenbuch.delete(d.get(BootStatus.LFDNR));
    if (!Daten.fahrtenbuch.writeFile()) {
        LogString.logError_fileWritingFailed(Daten.fahrtenbuch.getFileName(), International.getString("Fahrtenbuch"));
    }
    fahrtBeendet(boot,interaktiv);
  }

  void nachtragButton_actionPerformed(ActionEvent e) {
    alive();
    setEnabled(false);

    String boot = null;
    try {
      if (!booteVerfuegbar.isSelectionEmpty() && booteVerfuegbar.getSelectedIndex() != 0) boot = listGetSelectedValue(booteVerfuegbar);
      if (boot == null) {
        if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
        if (boot == null) {
          if (!booteNichtVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteNichtVerfuegbar);
        }
      }
    } catch(Exception ee) {}
    if (boot != null && boot.startsWith("-")) boot = null;
    efaFrame.direktFahrtNachtrag(boot);
  }

  public void fahrtNachgetragen() {
  }

  void bootsstatusButton_actionPerformed(ActionEvent e) {
    alive();
    String boot = null;
    try {
      if (!booteVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteVerfuegbar);
      if (boot == null) {
        if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
        if (boot == null) {
          if (!booteNichtVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteNichtVerfuegbar);
        }
      } else {
        if (booteVerfuegbar.getSelectedIndex()==0) {
          Dialog.error(International.getString("Dieses Boot kann nicht reserviert werden!"));
          return;
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }
    if (boot == null) {
      Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
      return;
    }

    boot = removeDoppeleintragFromBootsname(boot);

    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      String s = International.getMessage("Boot {boat} nicht in der Statusliste gefunden!",boot);
      Dialog.error(s);
      Logger.log(Logger.ERROR,Logger.MSG_ERR_BOATNOTFOUNDINSTATUS,s);
      return;
    }
    if (d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) {
      Dialog.error(International.getString("Dieses Boot kann nicht reserviert werden!"));
      return;
    }
    BootStatusFrame dlg = new BootStatusFrame(this,d,bootStatus);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void showFbButton_actionPerformed(ActionEvent e) {
    alive();
    if (FahrtenbuchAnzeigenFrame.wirdBereitsAngezeigt) return;
    FahrtenbuchAnzeigenFrame dlg = new FahrtenbuchAnzeigenFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    dlg = null;
  }

  void statButton_actionPerformed(ActionEvent e) {
    alive();
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().statistik == null) {
      Dialog.error(International.getString("Es sind keine Statistiken verfügbar!")+"\n\n"+
                   International.getString("Hinweis für Administratoren")+":\n"+
                   International.getString("Damit Statistiken im Bootshaus von jedem Mitglied aufrufbar sind, "+
                   "müssen sie zuerst im Admin-Modus vorbereitet und als Statistikeinstellungen "+
                   "abgespeichert werden. Beim Abspeichern muß zusätzliche die Option "+
                   "'Statistik auch im Bootshaus verfügbar machen' aktiviert werden."));
      return;
    }

    Vector stats = new Vector();
    DatenFelder d = Daten.fahrtenbuch.getDaten().statistik.getCompleteFirst();
    while (d != null) {
      if (d.get(StatSave.AUCHINEFADIREKT).equals("+")) stats.add(d.get(StatSave.NAMESTAT));
      d = Daten.fahrtenbuch.getDaten().statistik.getCompleteNext();
    }
    if (stats.size() == 0) {
      Dialog.error(International.getString("Es sind keine Statistiken verfügbar!")+"\n\n"+
                   International.getString("Hinweis für Administratoren")+":\n"+
                   International.getString("Damit Statistiken im Bootshaus von jedem Mitglied aufrufbar sind, "+
                   "müssen sie zuerst im Admin-Modus vorbereitet und als Statistikeinstellungen "+
                   "abgespeichert werden. Beim Abspeichern muß zusätzliche die Option "+
                   "'Statistik auch im Bootshaus verfügbar machen' aktiviert werden."));
      return;
    }

    try {
      StatistikDirektFrame dlg = new StatistikDirektFrame(this,stats);
      Dialog.setDlgLocation(dlg,this);
      dlg.setModal(true);
      dlg.show();
    } catch(Exception ee) {
      // HTML Reder Exception reported by Thilo Coblenzer (01.06.06)
    }
  }

  void adminHinweisButton_actionPerformed(ActionEvent e) {
    alive();
    NachrichtAnAdminFrame dlg = new NachrichtAnAdminFrame(this,Daten.nachrichten);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    updateUnreadMessages();
  }

  void adminButton_actionPerformed(ActionEvent e) {
    alive();

    // Prüfe, ob bereits ein Admin-Modus-Fenster offen ist
    Stack s = Dialog.frameStack;
    boolean adminOnStack = false;
    try {
      for (int i=0; i<s.size(); i++) {
        if (s.elementAt(i).getClass().getName().equals("de.nmichael.efa.direkt.AdminFrame")) adminOnStack = true;
      }
    } catch(Exception ee) {}
    if (adminOnStack) {
      Dialog.error(International.getString("Es ist bereits ein Admin-Fenster geöffnet."));
      return;
    }

    Admin admin = AdminLoginFrame.login(this,International.getString("Admin-Modus"));
    if (admin == null) return;
    AdminFrame dlg = new AdminFrame(this,admin,bootStatus);
    Dialog.setDlgLocation(dlg,this);
    // Folgende Zeile *muß* auskommentiert sein; statt dessen wird "this.setEnabled(false)" verwendet.
    //    dlg.setModal(!Dialog.tourRunning);
    // Folgende Zeile *muß* auskommentiert sein, da unter Java 1.5 sonst in den im Admin-Modus geöffneten Dialogen
    // keine Eingaben möglich sind. Dies scheint ein Bug in 1.5 zu sein. Da EfaDirektFrame aktiviert bleibt, ist
    // ein Navigieren im Admin-Modus im EfaDirektFrame möglich, was aber eine vertretbare Unschönheit ist... ;-)
    //    this.setEnabled(false); //!!!
    dlg.show();
  }

  void spezialButton_actionPerformed(ActionEvent e) {
    alive();
    String cmd = Daten.efaConfig.efaDirekt_butSpezialCmd.trim();
    if (cmd.length() > 0) {
      try {
        if (cmd.toLowerCase().startsWith("browser:")) {
          Dialog.neuBrowserDlg(this,International.getString("Browser"),cmd.substring(8));
        } else {
          Runtime.getRuntime().exec(cmd);
        }
      } catch(Exception ee) {
          LogString.logWarning_cantExecCommand(cmd, International.getString("für Spezial-Button"), ee.toString());
      }
    } else {
      Dialog.error(International.getString("Kein Kommando für diesen Button konfiguriert!"));
    }
  }

  void efaButton_actionPerformed(ActionEvent e) {
    alive();
    EfaFrame_AboutBox dlg = new EfaFrame_AboutBox(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void hilfeButton_actionPerformed(ActionEvent e) {
    Help.getHelp(this,this.getClass());
  }



  public int checkUnreadMessages() {
    boolean admin = false;
    boolean bootswart = false;

    if (Daten.nachrichten != null) {
      // durchsuche die letzten 50 Nachrichten nach ungelesenen (aus Performancegründen immer nur die letzen 50)
      for (int i=Daten.nachrichten.size()-1; i>=0 && i>Daten.nachrichten.size()-50; i--) {
        if (!Daten.nachrichten.get(i).gelesen && Daten.nachrichten.get(i).empfaenger == Nachricht.ADMIN) admin = true;
        if (!Daten.nachrichten.get(i).gelesen && Daten.nachrichten.get(i).empfaenger == Nachricht.BOOTSWART) bootswart = true;
        if (admin && bootswart) return Nachricht.ALLE;
      }
    }
    if (admin) return Nachricht.ADMIN;
    if (bootswart) return Nachricht.BOOTSWART;
    return -1;
  }


  void updateUnreadMessages() {
    try {
    switch(checkUnreadMessages()) {
      case Nachricht.ADMIN:
        adminButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mailAdmin.gif")));
        break;
      case Nachricht.BOOTSWART:
        adminButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mailBootswart.gif")));
        break;
      case Nachricht.ALLE:
        adminButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mail.gif")));
        break;
      default: adminButton.setIcon(null);
    }
    } catch(Exception e) { EfaUtil.foo(); }
    if (!Daten.efaConfig.efaDirekt_startMaximized) packFrame("updateUnredMessages()");
  }


  void alive() {
    lastUserInteraction = System.currentTimeMillis();
  }


  void bootslist_focusGained(FocusEvent e) {
    JList list = null;
    if (e != null) list = ((JList)e.getSource());
    if (list != null && list.getFirstVisibleIndex()>=0 && list.getSelectedIndex()<0) list.setSelectedIndex(0);
    if (list != null) {
      int nr = 0;
      if (list == this.booteVerfuegbar) nr = 1;
      if (list == this.booteAufFahrt) nr = 2;
      if (list == this.booteNichtVerfuegbar) nr = 3;
      showBootStatus(nr,list,1);
    }
  }


  public void lockEfaAt(TMJ datum, TMJ zeit) {
    efaDirektBackgroundTask.setEfaLockBegin(datum,zeit);
  }

  public void lockEfa() {
    if (Daten.efaConfig == null) return;

    String endeDerSperrung = (Daten.efaConfig.efaDirekt_lockEfaUntilDatum != null ? " "+International.getString("Ende der Sperrung")+": "+
                Daten.efaConfig.efaDirekt_lockEfaUntilDatum.tag+"."+Daten.efaConfig.efaDirekt_lockEfaUntilDatum.monat+"."+Daten.efaConfig.efaDirekt_lockEfaUntilDatum.jahr+
                (Daten.efaConfig.efaDirekt_lockEfaUntilZeit != null ? " "+
                 (Daten.efaConfig.efaDirekt_lockEfaUntilZeit.tag < 10 ? "0" : "") + Daten.efaConfig.efaDirekt_lockEfaUntilZeit.tag+":"+
                 (Daten.efaConfig.efaDirekt_lockEfaUntilZeit.monat < 10 ? "0" : "") + Daten.efaConfig.efaDirekt_lockEfaUntilZeit.monat : "") : "");

    String html = Daten.efaConfig.efaDirekt_lockEfaShowHtml;
    if (html == null || !EfaUtil.canOpenFile(html)) {
      html = Daten.efaTmpDirectory+"locked.html";
      try {
        BufferedWriter f = new BufferedWriter(new FileWriter(html));
        f.write("<html><body><h1 align=\"center\">"+International.getString("efa ist für die Benutzung gesperrt")+"</h1>\n");
        f.write("<p>"+International.getString("efa wurde vom Administrator vorübergehend für die Benutzung gesperrt.")+"</p>\n");
        if (endeDerSperrung.length() > 0) f.write("<p>"+endeDerSperrung+"</p>\n");
        f.write("</body></html>\n");
        f.close();
      } catch(Exception e) {
        EfaUtil.foo();
      }
    }
    BrowserFrame browser = new BrowserFrame(this,
                                            Daten.efaConfig.efaDirekt_lockEfaVollbild,
                                            "file:" + html);
    browser.setModal(true);
    if (Daten.efaConfig.efaDirekt_lockEfaVollbild) {
      browser.setSize(Dialog.screenSize);
    }
    Dialog.setDlgLocation(browser, this);
    browser.setClosingTimeout(10); // nur um Lock-Ende zu überwachen
    Logger.log(Logger.INFO, Logger.MSG_EVT_LOCKED,
            International.getString("efa wurde vom Administrator vorübergehend für die Benutzung gesperrt.")+endeDerSperrung);
    Daten.efaConfig.efaDirekt_lockEfaFromDatum = null; // damit nach Entsperren nicht wiederholt gelockt wird
    Daten.efaConfig.efaDirekt_lockEfaFromZeit = null;  // damit nach Entsperren nicht wiederholt gelockt wird
    Daten.efaConfig.efaDirekt_locked = true;
    Daten.efaConfig.writeFile();
    browser.show();
  }

  void setKombiBootStatus(String boot, String lfdnr, int status, String bemerk) {
    if (bootStatus == null) return;

    String org = EfaUtil.syn2org(Daten.synBoote,boot);
    if (org == null || org.equals(boot)) return; // kein Kombiboot

    Vector syn = EfaUtil.org2syn(Daten.synBoote,org);
    if (syn == null) return; // kein Kombiboot

    for (int i=0; i<syn.size(); i++) {
      String s = (String)syn.get(i);
      if (!boot.equals(s)) {
        DatenFelder d = bootStatus.getExactComplete(s);
        if (d != null && !d.get(BootStatus.STATUS).equals(BootStatus.getStatusKey(BootStatus.STAT_HIDE))) {
          d.set(BootStatus.LFDNR,lfdnr);
          d.set(BootStatus.STATUS,BootStatus.getStatusKey(status));
          d.set(BootStatus.BEMERKUNG,bemerk);
          bootStatus.delete(s);
          bootStatus.add(d);
        }
      }
    }
  }

  public void bringFrameToFront() {
    this.toFront();
  }

  public void setBootstatusSchaden(String boot, String s) {
    if (boot == null || s == null || boot.length() == 0 || s.length() == 0) return;

    String org = EfaUtil.syn2org(Daten.synBoote,boot);
    if (org == null) org = boot;
    Vector syn = EfaUtil.org2syn(Daten.synBoote,org);
    if (syn == null) syn = new Vector();
    if (syn.size() == 0) syn.add(org);

    for (int i=0; i<syn.size(); i++) {
      String b = (String)syn.get(i);
      DatenFelder d = bootStatus.getExactComplete(b);
      if (d != null) {
        d.set(BootStatus.BOOTSSCHAEDEN,s);
        bootStatus.delete(d.get(BootStatus.NAME));
        bootStatus.add(d);
      }
    }

    bootStatus.writeFile();
  }

  String makeSureFileDoesntExist(String f) {
    int cnt = 0;
    int punkt = f.lastIndexOf(".");
    String head;
    String tail;
    if (punkt >= 0) {
      head = f.substring(0,punkt);
      tail = f.substring(punkt+1);
    } else {
      head = f;
      tail = "";
    }
    while ((new File(f)).exists()) {
      cnt++;
      f = head + "_" + cnt + "." + tail;
    }
    return f;
  }

  void autoCreateNewFb() {
    String fnameEfb = Daten.efaConfig.efaDirekt_autoNewFb_datei.trim();
    Daten.efaConfig.efaDirekt_autoNewFb_datum = null;
    Daten.efaConfig.efaDirekt_autoNewFb_datei = "";

    fnameEfb = EfaUtil.makeFullPath(EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName()),fnameEfb);
    Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLOGBOOK,
            International.getString("Automatisches Anlegen eines neuen Fahrtenbuchs wird begonnen ..."));


    FBDaten fbDaten = null;
    Fahrtenbuch neuesFb = null;
    String oldNextFb = null;
    String oldFnameEfbb = null;
    String oldFnameEfbm = null;
    String oldFnameEfbz = null;
    String oldFnameEfbs = null;

    boolean abgebrocheneFahrten = false;

    int level = 0;
    try {
      if (!fnameEfb.toUpperCase().endsWith(".EFB")) fnameEfb = fnameEfb + ".efb";
      fnameEfb = makeSureFileDoesntExist(fnameEfb);
      String fnameBase = fnameEfb.substring(0,fnameEfb.lastIndexOf("."));
      String fnameEfbb = makeSureFileDoesntExist(fnameBase+".efbb");
      String fnameEfbm = makeSureFileDoesntExist(fnameBase+".efbm");
      String fnameEfbz = makeSureFileDoesntExist(fnameBase+".efbz");
      String fnameEfbs = makeSureFileDoesntExist(fnameBase+".efbs");
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              International.getString("Name für neue Fahrtenbuchdatei")+": "+fnameEfb);
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              International.getMessage("Name für neue {list}",
              International.getString("Bootsliste"))+": "+fnameEfbb);
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              International.getMessage("Name für neue {list}",
              International.getString("Mitgliederliste"))+": "+fnameEfbm);
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              International.getMessage("Name für neue {list}",
              International.getString("Zielliste"))+": "+fnameEfbz);
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              International.getMessage("Name für neue {list}",
              International.getString("Statistikeinstellungen"))+": "+fnameEfbs);

      oldFnameEfbb = Daten.fahrtenbuch.getDaten().boote.getFileName();
      oldFnameEfbm = Daten.fahrtenbuch.getDaten().mitglieder.getFileName();
      oldFnameEfbz = Daten.fahrtenbuch.getDaten().ziele.getFileName();
      oldFnameEfbs = Daten.fahrtenbuch.getDaten().statistik.getFileName();
      fbDaten = new FBDaten(Daten.fahrtenbuch.getDaten());
      fbDaten.boote.setFileName(fnameEfbb);
      fbDaten.mitglieder.setFileName(fnameEfbm);
      fbDaten.ziele.setFileName(fnameEfbz);
      fbDaten.statistik.setFileName(fnameEfbs);
      fbDaten.bootDatei = EfaUtil.makeRelativePath(fnameEfbb,fnameEfb);
      fbDaten.mitgliederDatei = EfaUtil.makeRelativePath(fnameEfbm,fnameEfb);
      fbDaten.zieleDatei = EfaUtil.makeRelativePath(fnameEfbz,fnameEfb);
      fbDaten.statistikDatei = EfaUtil.makeRelativePath(fnameEfbs,fnameEfb);

      // Neue Datenlisten erstellen
      level = 1;
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              "L1-START: " + International.getString("Erstelle neue Datenlisten ..."));
      if (!fbDaten.boote.writeFile()) {
          LogString.logError_fileCreationFailed(fbDaten.boote.getFileName(), International.getString("Bootsliste"));
          throw new Exception("Level 1");
      }
      if (!fbDaten.mitglieder.writeFile()) {
          LogString.logError_fileCreationFailed(fbDaten.mitglieder.getFileName(), International.getString("Mitgliederliste"));
          throw new Exception("Level 1");
      }
      if (!fbDaten.ziele.writeFile()) {
          LogString.logError_fileCreationFailed(fbDaten.ziele.getFileName(), International.getString("Zielliste"));
          throw new Exception("Level 1");
      }
      if (!fbDaten.statistik.writeFile()) {
          LogString.logError_fileCreationFailed(fbDaten.statistik.getFileName(), International.getString("Statistikeinstellungen"));
          throw new Exception("Level 1");
      }
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              "L1-DONE: " + International.getString("Fertig mit dem Erstellen der Datenlisten."));

      // Neue Fahrtenbuchdatei erstellen
      level = 2;
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              "L2-START: " + International.getString("Erstelle neues Fahrtenbuch ..."));
      neuesFb = new Fahrtenbuch(fnameEfb);
      neuesFb.setDaten(fbDaten);
      neuesFb.setPrevFb(EfaUtil.makeRelativePath(Daten.fahrtenbuch.getFileName(),neuesFb.getFileName()));
      neuesFb.setNextFb("");
      if (!neuesFb.writeFile()) {
          LogString.logError_fileCreationFailed(neuesFb.getFileName(), International.getString("Fahrtenbuch"));
          throw new Exception("Level 2");
      }
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              "L2-DONE: " + International.getString("Fertig mit dem Erstellen des Fahrtenbuchs."));

      // Fahrten für Boote, die noch unterwegs sind, abbrechen
      level = 3;
      Vector unterwegs = bootStatus.getBoote(BootStatus.STAT_UNTERWEGS);
      if (unterwegs.size()>0) {
        abgebrocheneFahrten = true;
        Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
                "L3-START: " + International.getString("Breche bestehende Fahrten ab ..."));
        if (!bootStatus.writeFile()) {
            LogString.logError_fileCreationFailed(bootStatus.getFileName(), International.getString("Bootsstatus-Liste"));
            throw new Exception("Level 3");
        }
        for (int i=0; i<unterwegs.size(); i++) {
          fahrtAbbruch((String)unterwegs.get(i),false);
        }
        level = 4;
        if (!bootStatus.writeFile()) {
            LogString.logError_fileCreationFailed(bootStatus.getFileName(), International.getString("Bootsstatus-Liste"));
            throw new Exception("Level 4");
        }
        Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
                "L4-DONE: " + International.getString("Abbrechen der Fahrten beendet."));
      }

      // Änderungen an altem Fahrtenbuch speichern
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              "L5-START: " + International.getString("Speichere Änderungen an altem Fahrtenbuch ..."));
      oldNextFb = Daten.fahrtenbuch.getNextFb(false);
      level = 5;
      Daten.fahrtenbuch.setNextFb(EfaUtil.makeRelativePath(neuesFb.getFileName(),Daten.fahrtenbuch.getFileName()));
      if (!Daten.fahrtenbuch.writeFile()) {
          LogString.logError_fileCreationFailed(Daten.fahrtenbuch.getFileName(), International.getString("Fahrtenbuch"));
          throw new Exception("Level 5");
      }
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLB_LX,
              "L5-DONE: " + International.getString("Änderungen an Fahrtenbuch gespeichert."));

      level = 6;
      Daten.fahrtenbuch = neuesFb;
      Daten.efaConfig.direkt_letzteDatei = Daten.fahrtenbuch.getFileName();
      Daten.efaConfig.writeFile();

      level = 7;
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLBDONE,
              International.getString("Automatisches Anlegen des neuen Fahrtenbuchs erfolgreich abgeschlossen."));
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTOSTARTNEWLBDONE,
              International.getMessage("Aktuelles Fahrtenbuch ist jetzt: {filename}",Daten.fahrtenbuch.getFileName()));

      Nachricht n = new Nachricht();
      n.name = Daten.EFA_SHORTNAME;
      n.empfaenger = Nachricht.ADMIN;
      n.betreff = International.getString("Neues Fahrtenbuch angelegt");
      n.nachricht = International.getString("efa hat soeben wie konfiguriert ein neues Fahrtenbuch angelegt.")+"\n"+
                    International.getMessage("Die neue Fahrtenbuchdatei ist: {filename}",Daten.fahrtenbuch.getFileName())+"\n"+
                    International.getString("Der Vorgang wurde ERFOLGREICH abgeschlossen.")+"\n\n"+
                    (abgebrocheneFahrten ? International.getString("Zum Zeitpunkt des Fahrtenbuchwechsels befanden sich noch einige Boote "+
                                           "auf dem Wasser. Diese Fahrten wurden ABGEBROCHEN. Die abgebrochenen "+
                                           "Fahrten sind in der Logdatei verzeichnet.")+"\n\n" : "") +
                    International.getString("Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden.");
      Daten.nachrichten.add(n);
      Daten.nachrichten.writeFile();

//      this.efaButton.requestFocus();
//      this.booteVerfuegbar.setSelectedIndex(-1);
      EfaUtil.sleep(500);
      updateBootsListen();
      EfaUtil.sleep(500);
      efaDirektBackgroundTask.interrupt();
    } catch(Exception e) {
      Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTOSTARTNEWLOGBOOK,
              International.getString("Beim Versuch, ein neues Fahrtenbuch anzulegen, trat ein Fehler auf. Alle Änderungen werden rückgängig gemacht ..."));
      switch (level) {
        case 0: break; // nothing to do
        case 7: break; // nothing to do
        case 6: break; // nothing to do
        case 5: Logger.log(Logger.WARNING, Logger.MSG_WARN_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} ...",5));
                Daten.fahrtenbuch.setNextFb(oldNextFb);
                Daten.fahrtenbuch.writeFile(); // egal, ob dies fehlschlägt oder nicht
                Logger.log(Logger.INFO, Logger.MSG_EVT_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} erfolgreich.",5));
        case 4: Logger.log(Logger.WARNING, Logger.MSG_WARN_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} ...",4));
                // nothing to do
                Logger.log(Logger.INFO, Logger.MSG_EVT_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} erfolgreich.",4));
        case 3: Logger.log(Logger.WARNING, Logger.MSG_WARN_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} ...",3));
                if (!bootStatus.readFile()) {
                  Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                          International.getMessage("Rollback von Level {n} fehlgeschlagen: {msg}",
                          3,International.getString("Bootsstatus konnte nicht wiederhergestellt werden.")));
                } else {
                  Logger.log(Logger.INFO, Logger.MSG_EVT_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} erfolgreich.",3));
                }
        case 2: Logger.log(Logger.WARNING, Logger.MSG_WARN_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} ...",2));
                // nothing to do
                Logger.log(Logger.INFO, Logger.MSG_EVT_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} erfolgreich.",2));
        case 1: Logger.log(Logger.WARNING, Logger.MSG_WARN_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} ...",1));
                Daten.fahrtenbuch.getDaten().boote.setFileName(oldFnameEfbb);
                Daten.fahrtenbuch.getDaten().mitglieder.setFileName(oldFnameEfbm);
                Daten.fahrtenbuch.getDaten().ziele.setFileName(oldFnameEfbz);
                Daten.fahrtenbuch.getDaten().statistik.setFileName(oldFnameEfbs);
                int errors = 0;
                if (!Daten.fahrtenbuch.getDaten().boote.writeFile()) {
                  Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                          LogString.logstring_fileCreationFailed(Daten.fahrtenbuch.getDaten().boote.getFileName(),International.getString("Bootsliste")));
                  errors++;
                }
                if (!Daten.fahrtenbuch.getDaten().mitglieder.writeFile()) {
                  Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                          LogString.logstring_fileCreationFailed(Daten.fahrtenbuch.getDaten().mitglieder.getFileName(),International.getString("Mitgliederliste")));
                  errors++;
                }
                if (!Daten.fahrtenbuch.getDaten().ziele.writeFile()) {
                  Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                          LogString.logstring_fileCreationFailed(Daten.fahrtenbuch.getDaten().ziele.getFileName(),International.getString("Zielliste")));
                  errors++;
                }
                if (!Daten.fahrtenbuch.getDaten().statistik.writeFile()) {
                  Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                          LogString.logstring_fileCreationFailed(Daten.fahrtenbuch.getDaten().statistik.getFileName(),International.getString("Statistikeinstellungen")));
                  errors++;
                }
                if (errors == 0) {
                  Logger.log(Logger.INFO, Logger.MSG_EVT_AUTONEWLOGROLLBACK,
                        International.getMessage("Rollback von Level {n} erfolgreich.",1));
                } else {
                  Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                          International.getMessage("Rollback von Level {level} mit {n} Fehlern abgeschlossen.",1,errors));
                }
                break;
        default: Logger.log(Logger.ERROR, Logger.MSG_ERR_AUTONEWLOGROLLBACK,
                International.getString("Rollback nicht möglich: efa kann den Originalzustand nicht wiederherstellen!"));
                 Logger.log(Logger.ERROR, Logger.MSG_ERR_INCONSISTENTSTATE,
                         International.getString("Kritischer Fehler")+": "+
                         International.getString("efa befindet sich in einem undefinierten Zustand! Überprüfung durch Administrator erforderlich!"));
      }
      Nachricht n = new Nachricht();
      n.name = Daten.EFA_SHORTNAME;
      n.empfaenger = Nachricht.ADMIN;
      n.betreff = International.getString("FEHLER beim Anlegen eines neuen Fahrtenbuchs");
      n.nachricht = International.getString("efa hat soeben versucht, wie konfiguriert ein neues Fahrtenbuch anzulegen.")+"\n"+
                    International.getString("Bei diesem Vorgang traten jedoch FEHLER auf.")+"\n\n"+
                    International.getString("Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden.");
      Daten.nachrichten.add(n);
      Daten.nachrichten.writeFile();

      Daten.efaConfig.writeFile();
      Logger.log(Logger.INFO, Logger.MSG_EVT_AUTONEWLOGROLLBACK,
              International.getString("Rückgängigmachen aller Änderungen abgeschlossen."));
    }

  }

  void updateNews() {
    if (efaNewsUpdater != null) {
      efaNewsUpdater.stopRunning();
    }
    efaNewsUpdater = new EfaNewsUpdater(this.newsLabel,Daten.efaConfig.efaDirekt_newsText);
    efaNewsUpdater.start();
    newsLabel.setVisible(Daten.efaConfig.efaDirekt_newsText.length()>0);
  }


  class EfaDirektBackgroundTask extends Thread {
    private static final int CHECK_INTERVAL = 60;
    private static final int ONCE_AN_HOUR = 60;
    EfaDirektFrame efaDirektFrame;
    int onceAnHour;
    Date date;
    Calendar cal;
    Calendar lockEfa;
    boolean framePacked;

    public EfaDirektBackgroundTask(EfaDirektFrame efaDirektFrame) {
      this.efaDirektFrame = efaDirektFrame;
      this.onceAnHour = 5; // initial nach 5 Schleifendurchläufen zum ersten Mal hier reingehen
      this.cal = new GregorianCalendar();
      this.lockEfa = null;
      this.date = new Date();
      this.framePacked = false;
    }

    private void lockEfaThread() {
      new Thread() {
        public void run() {
          efaDirektFrame.lockEfa();
        }
      }.start();
    }

    public void setEfaLockBegin(TMJ datum, TMJ zeit) {
      if (datum == null) {
        lockEfa = null;
      } else {
        if (zeit != null) lockEfa = new GregorianCalendar(datum.jahr,datum.monat-1,datum.tag,zeit.tag,zeit.monat);
        else lockEfa = new GregorianCalendar(datum.jahr,datum.monat-1,datum.tag);
      }
    }

    private void mailWarnings() {
      try {
        BufferedReader f = new BufferedReader(new FileReader(Daten.efaLogfile));
        String s;
        Vector warnings = new Vector();
        while ( (s = f.readLine()) != null) {
          if (Logger.isWarningLine(s) && Logger.getLineTimestamp(s) > Daten.efaConfig.efaDirekt_bnrWarning_lasttime) {
            warnings.add(s);
          }
        }
        f.close();
        if (warnings.size() == 0) {
          Logger.log(Logger.INFO, Logger.MSG_EVT_CHECKFORWARNINGS,
                  International.getMessage("Seit {date} sind keinerlei Warnungen in efa verzeichnet worden.",EfaUtil.getTimeStamp(Daten.efaConfig.efaDirekt_bnrWarning_lasttime)));
        } else {
          Logger.log(Logger.INFO, Logger.MSG_EVT_CHECKFORWARNINGS,
                  International.getMessage("Seit {date} sind {n} Warnungen in efa verzeichnet worden.",
                  EfaUtil.getTimeStamp(Daten.efaConfig.efaDirekt_bnrWarning_lasttime),warnings.size()));
          String txt = International.getMessage("Folgende Warnungen sind seit {date} in efa verzeichnet worden:",
                  EfaUtil.getTimeStamp(Daten.efaConfig.efaDirekt_bnrWarning_lasttime))+"\n"+
                  International.getMessage("{n} Warnungen",warnings.size())+"\n\n";
          for (int i=0; i<warnings.size(); i++) {
            txt += ((String)warnings.get(i)) + "\n";
          }
          if (Daten.nachrichten != null && Daten.efaConfig != null) {
            if (Daten.efaConfig.efaDirekt_bnrWarning_admin) {
              Daten.nachrichten.createNachricht(Daten.EFA_SHORTNAME, Nachricht.ADMIN,International.getString("Warnungen"), txt);
            }
            if (Daten.efaConfig.efaDirekt_bnrWarning_bootswart) {
              Daten.nachrichten.createNachricht(Daten.EFA_SHORTNAME, Nachricht.BOOTSWART,International.getString("Warnungen"), txt);
            }
          }
        }
        if (Daten.efaConfig != null) {
          Daten.efaConfig.efaDirekt_bnrWarning_lasttime = System.currentTimeMillis();
          Daten.efaConfig.writeFile();
        }

      } catch(Exception e) {
        Logger.log(Logger.ERROR, Logger.MSG_ERR_CHECKFORWARNINGS,
                International.getMessage("Benachrichtigung über WARNING's im Logfile ist fehlgeschlagen: {msg}",e.toString()));
      }
    }

    public void run() {
      // Diese Schleife läuft i.d.R. einmal pro Minute
      while(true) {

        // Reservierungs-Checker
        if (Dialog.frameCurrent() == efaDirektFrame // aktueller Frame ist EfaDirektFrame!
            && bootStatus != null) {
          boolean changes = false;
          DatenFelder d;
          for (d = bootStatus.getCompleteFirst(); d != null; d = bootStatus.getCompleteNext()) {
            // prüfen, ob für dieses Boot Reservierungen möglich sind
            if (BootStatus.getStatusID(d.get(BootStatus.STATUS)) == BootStatus.STAT_HIDE) continue;
            if (d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) continue;

            // derzeit gültige Reservierungen finden
            Reservierung reservierung = BootStatus.getReservierung(d,System.currentTimeMillis(),0);

            // verfallene Reservierungen löschen
            if (BootStatus.deleteObsoleteReservierungen(d)) {
              // Ok, alte Reservierungen wurden gelöscht: Jetzt prüfen, ob das Boot zur Zeit reserviert
              // ist. Falls ja, muß es verfügbar gemacht werden, damit ggf. neue Reservierungen zum Tragen
              // kommen können.
              if ( (BootStatus.getStatusID(d.get(BootStatus.STATUS)) == BootStatus.STAT_VERFUEGBAR ||
                    BootStatus.getStatusID(d.get(BootStatus.STATUS)) == BootStatus.STAT_NICHT_VERFUEGBAR) &&
                  d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
                d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_VERFUEGBAR));
                d.set(BootStatus.BEMERKUNG,BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR));
                d.set(BootStatus.LFDNR,"");
                Logger.log(Logger.INFO, Logger.MSG_EVT_RESCHECK_AVAIL,
                        "ReservatioChecker: "+
                        International.getMessage("Boot {boat} auf '{status}' gesetzt: {notes}",
                                       d.get(BootStatus.NAME),BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR),
                                       International.getString("Alte Reservierungen gelöscht")));
              }
              changes = true;
            }

            if (reservierung != null) {
              // Reservierung liegt vor: Jetzt prüfen, ob das Boot zur Zeit *nicht* reserviert ist; nur
              // in diesem Fall kommt die gefundene Reservierung zum Tragen
              if (BootStatus.getStatusID(d.get(BootStatus.STATUS)) == BootStatus.STAT_VERFUEGBAR &&
                  !d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
                if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_resBooteNichtVerfuegbar) {
                  d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_NICHT_VERFUEGBAR));
                }
                d.set(BootStatus.BEMERKUNG,
                        International.getMessage("reserviert für {name} ({reason}) {from_to}",
                        reservierung.name,reservierung.grund,BootStatus.makeReservierungText(reservierung)));
                d.set(BootStatus.LFDNR,BootStatus.RES_LFDNR); // Kennzeichnung dafür, daß es sich um eine *Reservierung* handelt (und nicht Sperrung des Bootes o.ä.)
                Logger.log(Logger.INFO, Logger.MSG_EVT_RESCHECK_RESFOUND,
                        "ReservatioChecker: "+
                        International.getMessage("Für Boot {boat} wurde eine Reservierung gefunden (neuer Status: '{status}')",
                                       d.get(BootStatus.NAME),BootStatus.getStatusName(BootStatus.getStatusID(d.get(BootStatus.STATUS))))+
                                       ": " + d.get(BootStatus.BEMERKUNG));
                changes = true;
              }
            } else {
              // Reservierung liegt nicht vor: Jetzt prüfen, ob das Boot zur Zeit reserviert ist; nur
              // in diesem Fall wird die aktuelle Reservierung gelöscht
              if ( (BootStatus.getStatusID(d.get(BootStatus.STATUS)) == BootStatus.STAT_VERFUEGBAR ||
                    BootStatus.getStatusID(d.get(BootStatus.STATUS)) == BootStatus.STAT_NICHT_VERFUEGBAR) &&
                  d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
                d.set(BootStatus.STATUS,BootStatus.getStatusKey(BootStatus.STAT_VERFUEGBAR));
                d.set(BootStatus.BEMERKUNG,BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR));
                d.set(BootStatus.LFDNR,"");
                Logger.log(Logger.INFO, Logger.MSG_EVT_RESCHECK_AVAIL,
                        "ReservatioChecker: "+
                        International.getMessage("Boot {boat} auf '{status}' gesetzt: {notes}",
                        d.get(BootStatus.NAME),BootStatus.getStatusName(BootStatus.STAT_VERFUEGBAR),
                        International.getString("Reservierungszeitraum beendet.")));
                changes = true;
              }
            }
          } // end: for all boats
          if (changes) {
            if (!bootStatus.writeFile()) {
                LogString.logError_fileWritingFailed(bootStatus.getFileName(), International.getString("Bootsstatus-Liste"));
            }
            efaDirektFrame.updateBootsListen();
          }
        }

        // Nach ungelesenen Nachrichten für den Admin suchen
        updateUnreadMessages();

        // automatisches, zeitgesteuertes Beenden von efa ?
        if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_exitTime != null && Daten.efaConfig.efaDirekt_exitTime.tag>=0
            && System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME+1)*60*1000
            ) {
          date.setTime(System.currentTimeMillis());
          cal.setTime(date);
          int now = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE);
          int exitTime = Daten.efaConfig.efaDirekt_exitTime.tag*60 + Daten.efaConfig.efaDirekt_exitTime.monat;
          if ( (now >= exitTime && now < exitTime+Daten.AUTO_EXIT_MIN_RUNTIME) || (now+(24*60) >= exitTime && now+(24*60) < exitTime+Daten.AUTO_EXIT_MIN_RUNTIME) ) {
            Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDEXIT,
                    International.getString("Eingestellte Uhrzeit zum Beenden von efa erreicht!"));
            if (System.currentTimeMillis() - efaDirektFrame.lastUserInteraction < Daten.AUTO_EXIT_MIN_LAST_USED*60*1000) {
              Logger.log(Logger.INFO, Logger.MSG_EVT_TIMEBASEDEXITDELAY,
                      International.getMessage("Beenden von efa wird verzögert, da efa innerhalb der letzten {n} Minuten noch benutzt wurde ...",
                      Daten.AUTO_EXIT_MIN_LAST_USED));
            } else {
              EfaExitFrame.exitEfa(International.getString("Zeitgesteuertes Beenden von efa"),false,EFA_EXIT_REASON_TIME);
            }
          }
        }

        // automatischer, zeitgesteuerter Neustart von efa ?
        if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_restartTime != null && Daten.efaConfig.efaDirekt_restartTime.tag>=0
            && System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME +1)*60*1000
            ) {
          date.setTime(System.currentTimeMillis());
          cal.setTime(date);
          int now = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE);
          int restartTime = Daten.efaConfig.efaDirekt_restartTime.tag*60 + Daten.efaConfig.efaDirekt_restartTime.monat;
          if ( (now >= restartTime && now < restartTime+Daten.AUTO_EXIT_MIN_RUNTIME) || (now+(24*60) >= restartTime && now+(24*60) < restartTime+Daten.AUTO_EXIT_MIN_RUNTIME) ) {
            Logger.log(Logger.INFO,"Automatischer Neustart von efa (einmal täglich).");
            if (System.currentTimeMillis() - efaDirektFrame.lastUserInteraction < Daten.AUTO_EXIT_MIN_LAST_USED*60*1000) {
              Logger.log(Logger.INFO,"Neustart von efa wird verzögert, da efa innerhalb der letzten "+Daten.AUTO_EXIT_MIN_LAST_USED+" Minuten noch benutzt wurde ...");
            } else {
              EfaExitFrame.exitEfa("Automatischer Neustart von efa",true,EFA_EXIT_REASON_AUTORESTART);
            }
          }
        }

        // efa zeitgesteuert sperren
        if (lockEfa != null) {
          date.setTime(System.currentTimeMillis());
          cal.setTime(date);
          if (cal.after(lockEfa)) {
            lockEfaThread();
            lockEfa = null;
          }
        }

        // automatisches Beginnen eines neuen Fahrtenbuchs (z.B. zum Jahreswechsel)
        if (Daten.applMode == Daten.APPL_MODE_NORMAL &&
            Daten.efaConfig != null && Daten.efaConfig.efaDirekt_autoNewFb_datum != null &&
            Daten.efaConfig.efaDirekt_autoNewFb_datei.length() > 0) {
          if (EfaUtil.secondDateIsEqualOrAfterFirst(EfaUtil.tmj2datestring(Daten.efaConfig.efaDirekt_autoNewFb_datum),EfaUtil.getCurrentTimeStampDD_MM_YYYY())) {
            efaDirektFrame.autoCreateNewFb();
          }
        }

        // immer im Vordergrund
        if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_immerImVordergrund && this.efaDirektFrame != null &&
            Dialog.frameCurrent() == this.efaDirektFrame) {
          Window[] windows = this.efaDirektFrame.getOwnedWindows();
          boolean topWindow = true;
          if (windows != null) {
            for (int i=0; i<windows.length; i++) {
              if (windows[i] != null && windows[i].isVisible()) topWindow = false;
            }
          }
          if (topWindow && Daten.efaConfig.efaDirekt_immerImVordergrundBringToFront) {
            this.efaDirektFrame.bringFrameToFront();
          }
        }

        // Fokus-Kontrolle
        if (this.efaDirektFrame != null && this.efaDirektFrame.getFocusOwner() == this.efaDirektFrame) {
          // das Frame selbst hat den Fokus: Das soll nicht sein! Gib einer Liste den Fokus!
          if (this.efaDirektFrame.booteVerfuegbar != null && this.efaDirektFrame.booteVerfuegbar.getSelectedIndex()>=0) this.efaDirektFrame.booteVerfuegbar.requestFocus();
          else if (this.efaDirektFrame.booteAufFahrt != null && this.efaDirektFrame.booteAufFahrt.getSelectedIndex()>=0) this.efaDirektFrame.booteAufFahrt.requestFocus();
          else if (this.efaDirektFrame.booteNichtVerfuegbar != null && this.efaDirektFrame.booteNichtVerfuegbar.getSelectedIndex()>=0) this.efaDirektFrame.booteNichtVerfuegbar.requestFocus();
          else if (this.efaDirektFrame.booteVerfuegbar != null) this.efaDirektFrame.booteVerfuegbar.requestFocus();
        }

        // Aktivitäten einmal pro Stunde
        if (--onceAnHour <= 0) {
          System.gc(); // Damit Speicherüberwachung funktioniert (anderenfalls wird CollectionUsage nicht aktualisiert; Java-Bug)
          onceAnHour = ONCE_AN_HOUR;
          Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_EFABACKGROUNDTASK,
                  "EfaDirektBackgroundTask: alive!");

          // WARNINGs aus Logfile an Admins verschicken
          if (Daten.efaConfig != null && System.currentTimeMillis() >= Daten.efaConfig.efaDirekt_bnrWarning_lasttime + 7l*24l*60l*60l*1000l &&
              (Daten.efaConfig.efaDirekt_bnrWarning_admin || Daten.efaConfig.efaDirekt_bnrWarning_bootswart) && Daten.efaLogfile != null) {
            mailWarnings();
          }
        }

        // Speicher-Überwachung
        try {
//          System.gc(); // !!! ONLY ENABLE FOR DEBUGGING PURPOSES !!!
          if (de.nmichael.efa.java15.Java15.isMemoryLow(Daten.MIN_FREEMEM_PERCENTAGE,Daten.WARN_FREEMEM_PERCENTAGE)) {
            efaDirektFrame.exitOnLowMemory("EfaDirektBackgroundTask: MemoryLow",false);
          }
        } catch(UnsupportedClassVersionError e) {
          EfaUtil.foo();
        } catch(NoClassDefFoundError e) {
          EfaUtil.foo();
        }

        try {
          Thread.sleep(CHECK_INTERVAL * 1000);
        } catch(Exception e) {
          // wenn unterbrochen, dann versuch nochmal, 2 Sekunden zu schlafen, und arbeite dann weiter!! ;-)
          try {
            Thread.sleep(2 * 1000);
          } catch(Exception ee) { EfaUtil.foo(); }

          // Bugfix, da efa unter manchen Versionen beim Start nicht richtig gepackt wird.
          if (!framePacked) {
            if (/*Daten.javaVersion.startsWith("1.3")  && */ efaDirektFrame != null) {
              if (Daten.efaConfig != null) {
                if (!Daten.efaConfig.efaDirekt_startMaximized) efaDirektFrame.packFrame("EfaDirektBackgroundTask");
                else {
                  if (efaDirektFrame.jScrollPane1 != null && efaDirektFrame.westPanel != null && efaDirektFrame.contentPane != null) {
                    efaDirektFrame.jScrollPane1.setSize(efaDirektFrame.jScrollPane1.getPreferredSize());
                    efaDirektFrame.westPanel.validate();
                    efaDirektFrame.contentPane.validate();
                  }
                }
              }
            }
            if (efaDirektFrame != null && efaDirektFrame.efaFrame != null) {
              efaDirektFrame.efaFrame.packFrame("EfaDirektBackgroundTask.run()");
            }
            framePacked = true; // nicht nochmal machen, sondern nur einmal beim Start
          }

        }
      } // end: while(true)
    } // end: run
  }


  class EfaUhrUpdater extends Thread {
    JLabel uhr;
    JLabel sunrise;
    JLabel sunset;
    boolean updateSunrise;
    int sunriseUpdated;

    public EfaUhrUpdater(JLabel uhr, JLabel sunrise, JLabel sunset, boolean updateSunrise) {
      this.uhr = uhr;
      this.sunrise = sunrise;
      this.sunset = sunset;
      this.updateSunrise = updateSunrise;
      this.sunriseUpdated = -1;
    }

    private TMJ getTime() {
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(new Date(System.currentTimeMillis()));
      return new TMJ(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
    }

    private String twoDigits(int i) {
      if (i<10) return "0"+i;
      else return Integer.toString(i);
    }

    private String getTimeString(TMJ tmj) {
      return twoDigits(tmj.tag)+":"+twoDigits(tmj.monat);
    }

    public void updateSunriseNow() {
      try {
        String sun[] = SunRiseSet.getSunRiseSet();
        sunrise.setText(sun[0]);
        sunset.setText(sun[1]);
      } catch (NoClassDefFoundError e) {
        sunrise.setText("--:--");
        sunset.setText("--:--");
      } catch (Exception ee) {
        sunrise.setText("--:--");
        sunset.setText("--:--");
      }
      sunriseUpdated = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public void run() {
      uhr.setText(getTimeString(getTime()));
      if (updateSunrise) updateSunriseNow();
      while (getTime().jahr != 1) {
        try { Thread.sleep(900); } catch(Exception e) { EfaUtil.foo(); }
      }
      while(true) {
        TMJ hhmmss = getTime();
        uhr.setText(getTimeString(hhmmss));
        if (updateSunrise && (sunriseUpdated == -1 || (hhmmss.tag==0 && hhmmss.monat<=1 ))) updateSunriseNow();
        try {
          Thread.sleep(60000);
        } catch(Exception e) {
          EfaUtil.foo();
        }
      }
    } // end: run
  }

  class EfaNewsUpdater extends Thread {
    private static final int MAX = 20;
    JLabel news;
    String text;
    int showing;
    int length;
    volatile boolean running;

    public EfaNewsUpdater(JLabel news, String text) {
      this.news = news;
      this.text = text;
      this.length = text.length();
      this.showing = 0;
      this.running = true;
    }

    private String getText(String s, int pos) {
      String t;
      if (pos == length+2) {
        t = " ";
      } else if (pos == length+1) {
        t = "  ";
      } else if (pos == length) {
        t = "   ";
      } else {
        t = s.substring(pos, Math.min(pos + MAX, length));
      }
      int l = t.length();
      if (l+3 < MAX) {
        t = t + (pos < length ? "   " : "") + s.substring(0,MAX-l-3);
      }
      return t;
    }

    public void stopRunning() {
      this.running = false;
    }

    public void run() {
      if (length <= MAX) {
        news.setText(text);
        return;
      }
      while(running) {
        try {
          news.setText(getText(text,showing));
          showing = (showing + 1) % (length+3);
          Thread.sleep(250);
        } catch(Exception e) {
          EfaUtil.foo();
        }
      }
    } // end: run
  }

}
