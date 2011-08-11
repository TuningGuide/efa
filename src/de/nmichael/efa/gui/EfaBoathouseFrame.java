/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.gui.widgets.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.*; // @todo remove again - currently only necessary for old BrowserFrame
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

public class EfaBoathouseFrame extends BaseFrame implements IItemListener {

    public static final int EFA_EXIT_REASON_USER = 0;
    public static final int EFA_EXIT_REASON_TIME = 1;
    public static final int EFA_EXIT_REASON_OOME = 2;
    public static final int EFA_EXIT_REASON_AUTORESTART = 3;

    String KEYACTION_F2;
    String KEYACTION_F3;
    String KEYACTION_F4;
    String KEYACTION_F5;
    String KEYACTION_F6;
    String KEYACTION_F7;
    String KEYACTION_F8;
    String KEYACTION_F9;
    String KEYACTION_altF10;
    String KEYACTION_altF11;
    String KEYACTION_F10;
    String KEYACTION_F11;
    String KEYACTION_F12;
    String KEYACTION_shiftF1;
    String KEYACTION_altX;
    String KEYACTION_shiftF4;

    // Boat List GUI Items
    JPanel boatsAvailablePanel;
    ItemTypeBoatstatusList boatsAvailableList; // booteVerfuegbar
    ItemTypeBoatstatusList personsAvailableList; // booteVerfuegbar
    ItemTypeBoatstatusList boatsOnTheWaterList; // booteAufFahrt
    ItemTypeBoatstatusList boatsNotAvailableList; // booteNichtVerfuegbar
    ButtonGroup toggleAvailableBoats = new ButtonGroup();
    JRadioButton toggleAvailableBoatsToBoats = new JRadioButton();
    JRadioButton toggleAvailableBoatsToPersons = new JRadioButton();

    // Center Panel GUI Items
    JLabel logoLabel = new JLabel();
    JButton startSessionButton = new JButton();
    JButton finishSessionButton = new JButton();
    JButton lateEntryButton = new JButton();
    JButton abortSessionButton = new JButton();
    JButton boatStatusButton = new JButton();
    JButton messageToAdminButton = new JButton();
    JButton adminButton = new JButton();
    JButton specialButton = new JButton();
    JButton efaButton = new JButton();
    JButton helpButton = new JButton();
    JButton showLogbookButton = new JButton();
    JButton statisticsButton = new JButton();

    // Widgets
    ClockMiniWidget clock;
    NewsMiniWidget news;
    Vector<IWidget> widgets;
    JPanel widgetTopPanel = new JPanel();
    JPanel widgetBottomPanel = new JPanel();
    JPanel widgetLeftPanel = new JPanel();
    JPanel widgetRightPanel = new JPanel();
    JPanel widgetCenterPanel = new JPanel();

    // South Panel GUI Items
    JLabel statusLabel = new JLabel();

    // Base GUI Items
    JPanel westPanel = new JPanel();
    JPanel eastPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel northPanel = new JPanel();
    JPanel southPanel = new JPanel();

    // Data
    Logbook logbook;
    BoatStatus boatStatus;
    volatile long lastUserInteraction = 0;
    EfaBaseFrame efaBaseFrame;
    byte[] largeChunkOfMemory = new byte[1024*1024];

    
    public EfaBoathouseFrame() {
        super(null, Daten.EFA_LONGNAME);
    }

    public void _keyAction(ActionEvent evt) {
        alive();
        if (evt == null || evt.getActionCommand() == null) {
            return;
        }

        if (evt.getActionCommand().equals(KEYACTION_ESCAPE)) {
            return; // do nothing (and don't invoke _keyAction(evt)!)
        }
        if (evt.getActionCommand().equals(KEYACTION_F2)) {
            fahrtbeginnButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F3)) {
            fahrtendeButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F4)) {
            fahrtabbruchButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F5)) {
            nachtragButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F6)) {
            bootsstatusButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F7)) {
            showFbButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F8)) {
            statButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F9)) {
            adminHinweisButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_altF10)) {
            adminButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_altF11)) {
            spezialButton_actionPerformed(null);
        }
        if (evt.getActionCommand().equals(KEYACTION_F10)) {
            if (toggleAvailableBoatsToBoats.isSelected()) {
                boatsAvailableList.requestFocus();
            } else {
                personsAvailableList.requestFocus();
            }
        }
        if (evt.getActionCommand().equals(KEYACTION_F11)) {
            boatsOnTheWaterList.requestFocus();
        }
        if (evt.getActionCommand().equals(KEYACTION_F12)) {
            boatsNotAvailableList.requestFocus();
        }
        if (evt.getActionCommand().equals(KEYACTION_shiftF1)) {
            EfaUtil.gc();
        }
        if (evt.getActionCommand().equals(KEYACTION_altX)) {
            cancel(null, EFA_EXIT_REASON_USER, false);
        }
        if (evt.getActionCommand().equals(KEYACTION_shiftF4)) {
            cancel(null, EFA_EXIT_REASON_USER, false);
        }

        super._keyAction(evt);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        iniGuiBase();
        iniGuiMain();
        iniApplication();
        iniGuiRemaining();
    }

    private void iniGuiBase() {
        setIconImage(Toolkit.getDefaultToolkit().createImage(EfaBaseFrame.class.getResource("/de/nmichael/efa/img/efa_icon.png")));
        mainPanel.setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
            public void windowDeactivated(WindowEvent e) {
                this_windowDeactivated(e);
            }
            public void windowActivated(WindowEvent e) {
                this_windowActivated(e);
            }
            public void windowIconified(WindowEvent e) {
                this_windowIconified(e);
            }
        });

        // Key Actions
        KEYACTION_F2      = addKeyAction("F2");
        KEYACTION_F3      = addKeyAction("F3");
        KEYACTION_F4      = addKeyAction("F4");
        KEYACTION_F5      = addKeyAction("F5");
        KEYACTION_F6      = addKeyAction("F6");
        KEYACTION_F7      = addKeyAction("F7");
        KEYACTION_F8      = addKeyAction("F8");
        KEYACTION_F9      = addKeyAction("F9");
        KEYACTION_altF10  = addKeyAction("alt F10");
        KEYACTION_altF11  = addKeyAction("alt F11");
        KEYACTION_F10     = addKeyAction("F10");
        KEYACTION_F11     = addKeyAction("F11");
        KEYACTION_F12     = addKeyAction("F12");
        KEYACTION_shiftF1 = addKeyAction("shift F1");
        KEYACTION_altX    = addKeyAction("alt X");
        KEYACTION_shiftF4 = addKeyAction("shift F4");

        // Mouse Actions
        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                alive();
            }
            public void mouseExited(MouseEvent e) {
                alive();
            }
            public void mouseEntered(MouseEvent e) {
                alive();
            }
            public void mouseReleased(MouseEvent e) {
                alive();
            }
            public void mousePressed(MouseEvent e) {
                alive();
            }
        });
    }

    private void iniGuiMain() {
        iniGuiBoatLists();
        iniGuiCenterPanel();
        iniGuiNorthPanel();
        iniGuiSouthPanel();
        iniGuiPanels();
        iniGuiWidgets();
    }

    private void iniGuiPanels() {
        widgetTopPanel.setLayout(new BorderLayout());
        widgetBottomPanel.setLayout(new BorderLayout());
        widgetLeftPanel.setLayout(new BorderLayout());
        widgetRightPanel.setLayout(new BorderLayout());
        widgetCenterPanel.setLayout(new BorderLayout());

        northPanel.add(widgetTopPanel, BorderLayout.CENTER);
        southPanel.add(widgetBottomPanel, BorderLayout.CENTER);
        westPanel.add(widgetLeftPanel, BorderLayout.WEST);
        eastPanel.add(widgetRightPanel, BorderLayout.EAST);
        centerPanel.add(widgetCenterPanel, new GridBagConstraints(1, 100, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 10, 10), 0, 0));

        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void iniApplication() {
        openProject();
        openLogbook();
        packFrame("iniApplication()");
        updateBoatLists();
        Logger.log(Logger.INFO, Logger.MSG_EVT_EFAREADY, International.getString("BEREIT"));
        EfaExitFrame.initExitFrame(this);
        
        // Speicher-Überwachung
        try {
            de.nmichael.efa.java15.Java15.setMemUsageListener(this, Daten.MIN_FREEMEM_COLLECTION_THRESHOLD);
        } catch (UnsupportedClassVersionError e) {
            EfaUtil.foo();
        } catch (NoClassDefFoundError e) {
            EfaUtil.foo();
        }
    }

    private void iniGuiRemaining() {
        packFrame("iniGuiRemaining()");
        boatsAvailableList.requestFocus();

        // Fenster nicht verschiebbar
        if (Daten.efaConfig.efaDirekt_fensterNichtVerschiebbar.getValue()) {
            try {
                this.setUndecorated(true);
                TitledBorder b = new TitledBorder(Daten.EFA_LONGNAME);
                b.setTitleColor(Color.white);
                mainPanel.setBackground(new Color(0, 0, 150));
                mainPanel.setBorder(b);
            } catch (NoSuchMethodError e) {
                Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION,
                        International.getMessage("Gewisse Funktionalität wird erst ab Java Version {version} unterstützt: {msg}","1.4",e.toString()));
            }
        }

        // Fenster immer im Vordergrund
        try {
            if (Daten.efaConfig.efaDirekt_immerImVordergrund.getValue()) {
                if (!de.nmichael.efa.java15.Java15.setAlwaysOnTop(this, true)) {
                    // Logger.log(Logger.WARNING,"Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterstützt.");
                    // Hier muß keine Warnung mehr ausgegeben werden, da ab v1.6.0 die Funktionalität auch für Java < 1.5
                    // durch einen Check alle 60 Sekunden nachgebildet wird.
                }
            }
        } catch (UnsupportedClassVersionError e) {
            // Java 1.3 kommt mit der Java 1.5 Klasse nicht klar
            Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION,
                        International.getMessage("Gewisse Funktionalität wird erst ab Java Version {version} unterstützt: {msg}","5",e.toString()));
        } catch (NoClassDefFoundError e) {
            Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION,
                        International.getMessage("Gewisse Funktionalität wird erst ab Java Version {version} unterstützt: {msg}","5",e.toString()));
        }

        // Fenster maximiert
        if (Daten.efaConfig.efaDirekt_startMaximized.getValue()) {
            try {
                //this.setSize(Dialog.screenSize);
                this.setMinimumSize(Dialog.screenSize);
                Dimension newsize = this.getSize();

                // breite für Scrollpanes ist (Fensterbreite - 20) / 2.
                //int width = (int) ((newsize.getWidth() - this.startSessionButton.getSize().getWidth() - 20) / 2);
                int width = (int) (newsize.getWidth() / 3);
                // die Höhe der Scrollpanes ist, da sie CENTER sind, irrelevant; nur für jScrollPane3
                // ist die Höhe ausschlaggebend.
                boatsAvailableList.setFieldSize(width, 400);
                personsAvailableList.setFieldSize(width, 400);
                boatsOnTheWaterList.setFieldSize(width, 200);
                boatsNotAvailableList.setFieldSize(width, 100); //(int) (newsize.getHeight() / 4));
                int height = (int) (20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
                toggleAvailableBoatsToBoats.setPreferredSize(new Dimension(width / 2, height));
                toggleAvailableBoatsToPersons.setPreferredSize(new Dimension(width / 2, height));

                validate();
            } catch (Exception e) {
                Logger.logdebug(e);
            }
        }

        // Lock efa?
        if (Daten.efaConfig.efaDirekt_locked.getValue()) {
            // lock efa NOW
            try {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }
                        lockEfa();
                    }
                }.start();
            } catch (Exception ee) {
            }
        } else {
            // lock efa later
            if (Daten.efaConfig.efaDirekt_lockEfaFromDatum.isSet()) {
                lockEfaAt(Daten.efaConfig.efaDirekt_lockEfaFromDatum.getDate(), Daten.efaConfig.efaDirekt_lockEfaFromZeit.getTime());
            }
        }
    }

    private void iniGuiBoatLists() {
        // Toggle between Boats and Persons
        Mnemonics.setButton(this, toggleAvailableBoatsToBoats, International.getStringWithMnemonic("Boote"));
        Mnemonics.setButton(this, toggleAvailableBoatsToPersons, International.getStringWithMnemonic("Personen"));
        toggleAvailableBoatsToBoats.setHorizontalAlignment(SwingConstants.RIGHT);
        toggleAvailableBoatsToPersons.setHorizontalAlignment(SwingConstants.LEFT);
        toggleAvailableBoats.add(toggleAvailableBoatsToBoats);
        toggleAvailableBoats.add(toggleAvailableBoatsToPersons);
        toggleAvailableBoatsToBoats.setSelected(true);
        toggleAvailableBoatsToBoats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleAvailableBoats_actionPerformed(e);
            }
        });
        toggleAvailableBoatsToPersons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleAvailableBoats_actionPerformed(e);
            }
        });
        // Update GUI Elements for Boat Lists
        toggleAvailableBoatsToBoats.setVisible(Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue());
        toggleAvailableBoatsToPersons.setVisible(Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue());

        // Boat Lists
        boatsAvailableList = new ItemTypeBoatstatusList("BOATSAVAILABLELIST", IItemType.TYPE_PUBLIC, "", International.getStringWithMnemonic("verfügbare Boote"), this);
        personsAvailableList = new ItemTypeBoatstatusList("PERSONSAVAILABLELIST", IItemType.TYPE_PUBLIC, "", International.getStringWithMnemonic("Personen"), this);
        boatsOnTheWaterList = new ItemTypeBoatstatusList("BOATSONTHEWATERLIST", IItemType.TYPE_PUBLIC, "", International.getStringWithMnemonic("Boote auf Fahrt"), this);
        boatsNotAvailableList = new ItemTypeBoatstatusList("BOATSNOTAVAILABLELIST", IItemType.TYPE_PUBLIC, "", International.getStringWithMnemonic("nicht verfügbare Boote"), this);
        boatsAvailableList.setFieldSize(200, 400);
        personsAvailableList.setFieldSize(200, 400);
        boatsOnTheWaterList.setFieldSize(200, 300);
        boatsNotAvailableList.setFieldSize(200, 100);
        boatsAvailableList.setPopupActions(getListActions(1, null));
        personsAvailableList.setPopupActions(getListActions(101, null));
        boatsOnTheWaterList.setPopupActions(getListActions(2, null));
        boatsNotAvailableList.setPopupActions(getListActions(3, null));
        boatsAvailableList.registerItemListener(this);
        personsAvailableList.registerItemListener(this);
        boatsOnTheWaterList.registerItemListener(this);
        boatsNotAvailableList.registerItemListener(this);
        iniGuiListNames();

        // add Panels to Gui
        boatsAvailablePanel = new JPanel();
        boatsAvailablePanel.setLayout(new BorderLayout());
        boatsAvailableList.displayOnGui(this, boatsAvailablePanel, BorderLayout.CENTER);
        // personsAvailableList.displayOnGui(this, boatsAvailablePanel, BorderLayout.CENTER); // Cannot be displayed here and now, only when toggled to!
        JPanel togglePanel = new JPanel();
        togglePanel.add(toggleAvailableBoatsToBoats, null);
        togglePanel.add(toggleAvailableBoatsToPersons, null);
        togglePanel.setVisible(Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue());
        boatsAvailablePanel.add(togglePanel, BorderLayout.NORTH);
        westPanel.setLayout(new BorderLayout());
        westPanel.add(boatsAvailablePanel, BorderLayout.CENTER);

        JPanel boatsNotAvailablePanel = new JPanel();
        boatsNotAvailablePanel.setLayout(new BorderLayout());
        boatsOnTheWaterList.displayOnGui(this, boatsNotAvailablePanel, BorderLayout.CENTER);
        boatsNotAvailableList.displayOnGui(this, boatsNotAvailablePanel, BorderLayout.SOUTH);
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(boatsNotAvailablePanel, BorderLayout.CENTER);
    }

    private void iniGuiListNames() {
        boolean fkey = Daten.efaConfig.efaDirekt_showButtonHotkey.getValue();
        if (!Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue() || toggleAvailableBoatsToBoats.isSelected()) {
            boatsAvailableList.setDescription(International.getString("verfügbare Boote") + (fkey ? " [F10]" : ""));
        } else {
            personsAvailableList.setDescription(International.getString("Personen") + (fkey ? " [F10]" : ""));
        }
        boatsOnTheWaterList.setDescription(International.getString("Boote auf Fahrt") + (fkey ? " [F11]" : ""));
        boatsNotAvailableList.setDescription(International.getString("nicht verfügbare Boote") + (fkey ? " [F12]" : ""));
    }
    
    private void iniGuiCenterPanel() {
        iniGuiLogo();
        iniGuiButtons();
        iniGuiClock();

        centerPanel.setLayout(new GridBagLayout());
        int logoTop = (int) (10.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
        int logoBottom = 5;
        if (Daten.efaConfig.efaDirekt_startMaximized.getValue() && Daten.efaConfig.efaDirekt_vereinsLogo.getValue().length() > 0) {
            logoBottom += (int) ((Dialog.screenSize.getHeight() - 825) / 5);
            if (logoBottom < 0) {
                logoBottom = 0;
            }
        }
        centerPanel.add(logoLabel, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(logoTop, 0, logoBottom, 0), 0, 0));
        int fahrtbeginnTop = (int) (10.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
        System.out.println(logoTop + " / " + logoBottom + " / " + fahrtbeginnTop);
        centerPanel.add(startSessionButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(fahrtbeginnTop, 0, 0, 0), 0, 0));
        centerPanel.add(finishSessionButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        centerPanel.add(abortSessionButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        centerPanel.add(lateEntryButton, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        centerPanel.add(boatStatusButton, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        centerPanel.add(showLogbookButton, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        centerPanel.add(statisticsButton, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        centerPanel.add(messageToAdminButton, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        centerPanel.add(adminButton, new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 0, 0));
        centerPanel.add(specialButton, new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        centerPanel.add(helpButton, new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
        centerPanel.add(efaButton, new GridBagConstraints(1, 14, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(10, 0, 5, 0), 0, 0));
        centerPanel.add(clock.getGuiComponent(), new GridBagConstraints(1, 15, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    }

    private void iniGuiLogo() {
        if (Daten.efaConfig.efaDirekt_vereinsLogo.getValue().length() > 0) {
            try {
                logoLabel.setIcon(new ImageIcon(Daten.efaConfig.efaDirekt_vereinsLogo.getValue()));
                logoLabel.setMinimumSize(new Dimension(200, 80));
                logoLabel.setPreferredSize(new Dimension(200, 80));
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            } catch (Exception e) {
                Logger.logdebug(e);
            }
        }
    }

    private void iniGuiButtons() {
        Mnemonics.setButton(this, startSessionButton, International.getStringWithMnemonic("Fahrt beginnen"));
        startSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fahrtbeginnButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, finishSessionButton, International.getStringWithMnemonic("Fahrt beenden"));
        finishSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fahrtendeButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, abortSessionButton, International.getStringWithMnemonic("Fahrt abbrechen"));
        abortSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fahrtabbruchButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, lateEntryButton, International.getStringWithMnemonic("Nachtrag"));
        lateEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nachtragButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, boatStatusButton, International.getStringWithMnemonic("Bootsreservierungen"));
        boatStatusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bootsstatusButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, showLogbookButton, International.getStringWithMnemonic("Fahrtenbuch anzeigen"));
        showLogbookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFbButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, statisticsButton, International.getStringWithMnemonic("Statistik erstellen"));
        statisticsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, messageToAdminButton, International.getStringWithMnemonic("Nachricht an Admin"));
        messageToAdminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adminHinweisButton_actionPerformed(e);
            }
        });

        Mnemonics.setButton(this, adminButton, International.getStringWithMnemonic("Admin-Modus"));
        adminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adminButton_actionPerformed(e);
            }
        });

        specialButton.setText(International.getString("Spezial-Button"));
        specialButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spezialButton_actionPerformed(e);
            }
        });

        helpButton.setText(International.getMessage("Hilfe mit {key}", "[F1]"));
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hilfeButton_actionPerformed(e);
            }
        });

        efaButton.setPreferredSize(new Dimension(90, 55));
        efaButton.setIcon(new ImageIcon(EfaBoathouseFrame.class.getResource(Daten.getEfaImage(1))));
        efaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                efaButton_actionPerformed(e);
            }
        });

        iniGuiButtonLAF();
        iniGuiButtonText();
    }

    private void iniGuiButtonLAF() {
        this.startSessionButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeginnen.getValueColor()));
        this.finishSessionButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeenden.getValueColor()));
        this.abortSessionButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtAbbrechen.getValueColor()));
        this.lateEntryButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachtrag.getValueColor()));
        this.boatStatusButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butBootsreservierungen.getValueColor()));
        this.showLogbookButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigen.getValueColor()));
        this.statisticsButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butStatistikErstellen.getValueColor()));
        this.messageToAdminButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachrichtAnAdmin.getValueColor()));
        this.adminButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butAdminModus.getValueColor()));
        this.specialButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butSpezial.getValueColor()));

        this.boatStatusButton.setVisible(Daten.efaConfig.efaDirekt_butBootsreservierungen.getValueShow());
        this.showLogbookButton.setVisible(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigen.getValueShow());
        this.statisticsButton.setVisible(Daten.efaConfig.efaDirekt_butStatistikErstellen.getValueShow());
        this.messageToAdminButton.setVisible(Daten.efaConfig.efaDirekt_butNachrichtAnAdmin.getValueShow());
        this.adminButton.setVisible(Daten.efaConfig.efaDirekt_butAdminModus.getValueShow());
        this.specialButton.setVisible(Daten.efaConfig.efaDirekt_butSpezial.getValueShow());
    }

    public void iniGuiButtonText() {
        boolean fkey = Daten.efaConfig.efaDirekt_showButtonHotkey.getValue();
        this.startSessionButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeginnen.getValueText() + (fkey ? " [F2]" : ""));
        this.finishSessionButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeenden.getValueText() + (fkey ? " [F3]" : ""));
        this.abortSessionButton.setText(International.getString("Fahrt abbrechen") + (fkey ? " [F4]" : ""));
        this.lateEntryButton.setText(International.getString("Nachtrag") + (fkey ? " [F5]" : ""));
        this.boatStatusButton.setText(International.getString("Bootsreservierungen") + (fkey ? " [F6]" : ""));
        this.showLogbookButton.setText(International.getString("Fahrtenbuch anzeigen") + (fkey ? " [F7]" : ""));
        this.statisticsButton.setText(International.getString("Statistik erstellen") + (fkey ? " [F8]" : ""));
        this.messageToAdminButton.setText(International.getString("Nachricht an Admin") + (fkey ? " [F9]" : ""));
        this.adminButton.setText(International.getString("Admin-Modus") + (fkey ? " [Alt-F10]" : ""));
        this.specialButton.setText(Daten.efaConfig.efaDirekt_butSpezial.getValueText() + (fkey ? " [Alt-F11]" : ""));
        if (!Daten.efaConfig.efaDirekt_startMaximized.getValue()) {
            packFrame("iniButtonText()");
        }
    }

    private void iniGuiClock() {
        if (clock == null) {
            clock = new ClockMiniWidget();
        }
        clock.getGuiComponent().setVisible(Daten.efaConfig.efaDirekt_showUhr.getValue());
    }

    private void iniGuiNews() {
        if (news == null) {
            news = new NewsMiniWidget();
        }
        news.setText(Daten.efaConfig.efaDirekt_newsText.getValue());
        news.setScrollSpeed(Daten.efaConfig.efaDirekt_newsScrollSpeed.getValue());
        news.getGuiComponent().setVisible(Daten.efaConfig.efaDirekt_showNews.getValue());
    }

    private void iniGuiWidgets() {
        widgetTopPanel.removeAll();
        widgetBottomPanel.removeAll();
        widgetLeftPanel.removeAll();
        widgetRightPanel.removeAll();
        widgetCenterPanel.removeAll();

        if (Daten.efaConfig.widgets == null) {
            return;
        }

        // stop all previously started widgets
        for (int i=0; widgets != null && i<widgets.size(); i++) {
            IWidget w = widgets.get(i);
            w.stop();
        }
        widgets = new Vector<IWidget>();

        // find all enabled widgets
        Vector<IWidget> allWidgets = Widget.getAllWidgets();
        for (int i=0; allWidgets != null && i<allWidgets.size(); i++) {
            IWidget w = allWidgets.get(i);
            IItemType enabled = Daten.efaConfig.getParameter(w.getParameterName(Widget.PARAM_ENABLED));
            if (enabled != null && enabled instanceof ItemTypeBoolean && ((ItemTypeBoolean)enabled).getValue()) {
                // set parameters for this enabled widget according to configuration
                IItemType[] params = w.getParameters();
                for (int j=0; j<params.length; j++) {
                    params[j].parseValue(Daten.efaConfig.getParameter(params[j].getName()).toString());
                }
                widgets.add(w);
            }
        }

        // show all enabled widgets
        for (int i=0; i<widgets.size(); i++) {
            IWidget w = widgets.get(i);
            String position = w.getPosition();
            if (IWidget.POSITION_TOP.equals(position)) {
                w.show(widgetTopPanel, BorderLayout.CENTER);
            }
            if (IWidget.POSITION_BOTTOM.equals(position)) {
                w.show(widgetBottomPanel, BorderLayout.CENTER);
            }
            if (IWidget.POSITION_LEFT.equals(position)) {
                w.show(widgetLeftPanel, BorderLayout.CENTER);
            }
            if (IWidget.POSITION_RIGHT.equals(position)) {
                w.show(widgetRightPanel, BorderLayout.CENTER);
            }
            if (IWidget.POSITION_CENTER.equals(position)) {
                w.show(widgetCenterPanel, BorderLayout.CENTER);
            }
        }
    }

    private void iniGuiNorthPanel() {
        iniGuiNews();
        northPanel.setLayout(new BorderLayout());
    }

    private void iniGuiSouthPanel() {
        iniGuiNews();
        southPanel.setLayout(new BorderLayout());

        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(news.getGuiComponent(), BorderLayout.SOUTH);
        statusLabelSetText(International.getString("Status"));

    }

    private void statusLabelSetText(String s) {
        statusLabel.setText(s);
        // wenn Text zu lang, dann PreferredSize verringern, damit bei pack() die zu große Label-Breite nicht
        // zum Vergrößern des Fensters führt!
        if (statusLabel.getPreferredSize().getWidth() > this.getSize().getWidth()) {
            statusLabel.setPreferredSize(new Dimension((int) this.getSize().getWidth() - 20,
                    (int) statusLabel.getPreferredSize().getHeight()));
        }
    }

    public void packFrame(String source) {
        this.pack();
    }
    
    void alive() {
        lastUserInteraction = System.currentTimeMillis();
    }

    private void this_windowClosing(WindowEvent e) {
        cancel(e,EFA_EXIT_REASON_USER,false);
    }

    private void this_windowDeactivated(WindowEvent e) {
        // nothing to do
    }

    private void this_windowActivated(WindowEvent e) {
        try {
            if (!isEnabled() && efaBaseFrame != null) {
                efaBaseFrame.toFront();
            }
        } catch (Exception ee) {
            Logger.logdebug(ee);
        }
    }

    private void this_windowIconified(WindowEvent e) {
      //super.processWindowEvent(e);
      this.setState(Frame.NORMAL);
    }

    public boolean cancel(WindowEvent e, int reason, boolean restart) {
        int exitCode = 0;
        if (!Daten.efaConfig.writeFile()) {
            LogString.logError_fileWritingFailed(Daten.efaConfig.getFileName(), International.getString("Konfigurationsdatei"));
        }
        String who = "unknown";

        switch (reason) {
            case EFA_EXIT_REASON_USER: // manuelles Beenden von efa
                boolean byUser;
                if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenEfaBeenden.getValue()) {
                    de.nmichael.efa.direkt.Admin admin = null;
                    do {
                        admin = de.nmichael.efa.direkt.AdminLoginFrame.login(this, International.getString("Beenden von efa"));
                        if (admin != null && !admin.allowedEfaBeenden) {
                            Dialog.error(International.getString("Du hast nicht die Berechtigung, um efa zu beenden!"));
                        }
                    } while (admin != null && !admin.allowedEfaBeenden);
                    if (admin == null) {
                        return false;
                    }
                    who = International.getString("Admin") + "=" + admin.name;
                    byUser = false;
                } else {
                    who = International.getString("Nutzer");
                    byUser = true;
                }
                if (Daten.efaConfig.efaDirekt_execOnEfaExit.getValue().length() > 0 && byUser) {
                    Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXITEXECCMD,
                            International.getMessage("Programmende veranlaßt; versuche, Kommando '{cmd}' auszuführen...", Daten.efaConfig.efaDirekt_execOnEfaExit.getValue()));
                    try {
                        Runtime.getRuntime().exec(Daten.efaConfig.efaDirekt_execOnEfaExit.getValue());
                    } catch (Exception ee) {
                        Logger.log(Logger.ERROR, Logger.MSG_ERR_EFAEXITEXECCMD_FAILED,
                                LogString.logstring_cantExecCommand(Daten.efaConfig.efaDirekt_execOnEfaExit.getValue(), International.getString("Kommando")));
                    }
                }
                break;
            case EFA_EXIT_REASON_TIME:
                who = International.getString("Zeitsteuerung");
                if (Daten.efaConfig.efaDirekt_execOnEfaAutoExit.getValue().length() > 0) {
                    Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXITEXECCMD,
                            International.getMessage("Programmende veranlaßt; versuche, Kommando '{cmd}' auszuführen...", Daten.efaConfig.efaDirekt_execOnEfaAutoExit.getValue()));
                    try {
                        Runtime.getRuntime().exec(Daten.efaConfig.efaDirekt_execOnEfaAutoExit.getValue());
                    } catch (Exception ee) {
                        Logger.log(Logger.ERROR, Logger.MSG_ERR_EFAEXITEXECCMD_FAILED,
                                LogString.logstring_cantExecCommand(Daten.efaConfig.efaDirekt_execOnEfaAutoExit.getValue(), International.getString("Kommando")));
                    }
                }
                break;
            case EFA_EXIT_REASON_OOME:
                who = International.getString("Speicherüberwachung");
                break;
            case EFA_EXIT_REASON_AUTORESTART:
                who = International.getString("Automatischer Neustart");
                break;
        }

        if (restart) {
            if (Daten.javaRestart) {
                exitCode = Daten.HALT_JAVARESTART;
                String restartcmd = System.getProperty("java.home") + Daten.fileSep
                        + "bin" + Daten.fileSep + "java "
                        + (Daten.efa_java_arguments != null ? Daten.efa_java_arguments
                        : "-cp " + System.getProperty("java.class.path")
                        + " " + Daten.EFADIREKT_MAINCLASS + de.nmichael.efa.direkt.Main.STARTARGS);
                Logger.log(Logger.INFO, Logger.MSG_EVT_EFARESTART,
                        International.getMessage("Neustart mit Kommando: {cmd}", restartcmd));
                try {
                    Runtime.getRuntime().exec(restartcmd);
                } catch (Exception ee) {
                    Logger.log(Logger.ERROR, Logger.MSG_ERR_EFARESTARTEXEC_FAILED,
                            LogString.logstring_cantExecCommand(restartcmd, International.getString("Kommando")));
                }
            } else {
                exitCode = Daten.HALT_SHELLRESTART;
            }
        }

//        if (e != null) {
//            super.processWindowEvent(e);
//        }
        Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXIT,
                International.getMessage("Programmende durch {originator}", who));
        super.cancel();
        Daten.haltProgram(exitCode);
        return true;
    }

    /**
     * Returns all possible actions for a list.
     * Those actions are prefixed with the following numbers representing those actions, which may be processed by processListAction(String, int):
     * 1 - start session
     * 2 - finish session
     * 3 - late entry
     * 4 - change session
     * 5 - cancel session
     * 6 - boat reservations
     * @param listnr
     * @param list
     * @param name
     * @return
     */
    private String[] getListActions(int listnr, DataRecord r) {
        if (r != null && r instanceof BoatStatusRecord) {
            // Handelt es sich um ein Boot, das auf Fahrt ist, aber trotzdem bei "nicht verfügbar" angezeigt wird?
            if (((BoatStatusRecord)r).getEntryNo() != null && ((BoatStatusRecord)r).getEntryNo().isSet()) {
                listnr = 2;
            }
        }

        String fahrtBeginnen = EfaUtil.replace(Daten.efaConfig.efaDirekt_butFahrtBeginnen.getValueText(), ">>>", "").trim();
        String fahrtBeenden = EfaUtil.replace(Daten.efaConfig.efaDirekt_butFahrtBeenden.getValueText(), "<<<", "").trim();
        if (listnr == 1 || listnr == 101) { // verfügbare Boote bzw. Personen
            if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren.getValue() && listnr == 1) {
                return new String[]{
                            "1" + fahrtBeginnen,
                            "3" + International.getString("Nachtrag"),
                            "6" + International.getString("Boot reservieren")
                        };
            } else {
                return new String[]{
                            "1" + fahrtBeginnen,
                            "3" + International.getString("Nachtrag")
                        };
            }
        }
        if (listnr == 2) { // Boote auf Fahrt
            if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren.getValue()) {
                return new String[]{
                            "2" + fahrtBeenden,
                            "4" + International.getString("Eintrag ändern"),
                            "5" + International.getString("Fahrt abbrechen"),
                            "6" + International.getString("Boot reservieren")
                        };
            } else {
                return new String[]{
                            "2" + fahrtBeenden,
                            "4" + International.getString("Eintrag ändern"),
                            "5" + International.getString("Fahrt abbrechen")
                        };
            }
        }
        if (listnr == 3) { // nicht verfügbare Boote
            if (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar.getValue()) {
                return new String[]{
                            "1" + fahrtBeginnen,
                            "2" + fahrtBeenden,
                            "3" + International.getString("Nachtrag"),
                            "6" + International.getString("Bootsreservierungen anzeigen")
                        };
            } else {
                return new String[]{
                            "1" + fahrtBeginnen,
                            "3" + International.getString("Nachtrag"),
                            "6" + International.getString("Bootsreservierungen anzeigen")
                        };
            }
        }
        return null;
    }

    // ========================================================================================================================================
    // Data-related methods
    // ========================================================================================================================================
    
    public static void haltProgram(String s, int exitCode) {
        if (s != null) {
            Dialog.error(s);
            Logger.log(Logger.ERROR, Logger.MSG_ERR_GENERIC,
                    EfaUtil.replace(s, "\n", " ", true));
        }
        Daten.haltProgram(exitCode);
    }

    private void openProject() {
        // project to open
        String projectName = null;
        if (Daten.efaConfig.lastProjectEfaBoathouse.getValue().length() > 0) {
            projectName = Daten.efaConfig.lastProjectEfaBoathouse.getValue();
        }

        // make sure a project is opened
        while (Daten.project == null) {
            if (projectName == null || projectName.length() == 0) {
                // @todo Admin-Login required
                OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.project);
                projectName = dlg.openDialog();
                if (projectName == null) {
                    break;
                }
            }
            if (!Project.openProject(projectName)) {
                projectName = null;
                Daten.project = null;
            }
        }

        if (Daten.project == null) {
            haltProgram(International.getMessage("Programmende, da kein {projectOrLogbook} geöffnet werden konnte.",
                    International.getString("Projekt")), Daten.HALT_FILEOPEN);
        }
        Daten.efaConfig.lastProjectEfaBoathouse.parseValue(projectName);

        boatStatus = Daten.project.getBoatStatus(false);
    }

    private void openLogbook() {
        // logbook to open
        String logbookName = null;
        if (Daten.project.getCurrentLogbookEfaBoathouse() != null) {
            logbookName = Daten.project.getCurrentLogbookEfaBoathouse();
        }

        // make sure a logbook is opened
        while (logbook == null) {
            if (logbookName == null || logbookName.length() == 0) {
                // @todo Admin-Login required
                OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.logbook);
                logbookName = dlg.openDialog();
                if (logbookName == null) {
                    break;
                }
            }
            if (!openLogbook(logbookName)) {
                logbookName = null;
                logbook = null;
            }
        }

        if (logbook == null) {
            haltProgram(International.getMessage("Programmende, da kein {projectOrLogbook} geöffnet werden konnte.",
                    International.getString("Fahrtenbuch")), Daten.HALT_FILEOPEN);
        }
    }

    boolean openLogbook(String logbookName) {
        if (Daten.project == null) {
            return false;
        }
        try {
            if (logbook != null && logbook.isOpen()) {
                logbook.close();
            }
        } catch (Exception e) {
            Logger.log(e);
            Dialog.error(e.toString());
        }
        if (logbookName != null && logbookName.length() > 0) {
            logbook = Daten.project.getLogbook(logbookName, false);
            if (logbook != null) {
                Daten.project.setCurrentLogbookEfaBoathouse(logbookName);
                return true;
            } else {
                Dialog.error(International.getMessage("Fahrtenbuch {logbook} konnte nicht geöffnet werden.", logbookName));
            }
        }
        return false;
    }


    void updateBoatLists() {
        if (!Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue() || toggleAvailableBoatsToBoats.isSelected()) {
            boatsAvailableList.setBoatStatusData(boatStatus.getBoats(BoatStatusRecord.STATUS_AVAILABLE), logbook, "<" + International.getString("anderes Boot") + ">");
        } else {
            Persons persons = boatStatus.getProject().getPersons(false);
            personsAvailableList.setPersonStatusData(persons.getAllPersons(System.currentTimeMillis(), false, false), "<" + International.getString("andere Person") + ">");
        }

        boatsOnTheWaterList.setBoatStatusData(boatStatus.getBoats(BoatStatusRecord.STATUS_ONTHEWATER), logbook, null);
        boatsNotAvailableList.setBoatStatusData(boatStatus.getBoats(BoatStatusRecord.STATUS_NOTAVAILABLE), logbook, null);

        /*
        Dimension dim = boatsAvailableScrollPane.getSize();
        boatsAvailableScrollPane.setPreferredSize(dim); // to make sure boatsAvailableScrollPane is not resized when toggled between persons and boats
        boatsAvailableScrollPane.setSize(dim);          // to make sure boatsAvailableScrollPane is not resized when toggled between persons and boats
        */

        if (toggleAvailableBoatsToBoats.isSelected()) {
            statusLabelSetText(International.getString("Kein Boot ausgewählt."));
            boatsAvailableList.setSelectedIndex(0);
            boatsAvailableList.requestFocus();
        } else {
            statusLabelSetText(International.getString("Keine Person ausgewählt."));
            personsAvailableList.setSelectedIndex(0);
            personsAvailableList.requestFocus();
        }
    }


    // ========================================================================================================================================
    // Callbacks and Events
    // ========================================================================================================================================
    public synchronized void exitOnLowMemory(String detector, boolean immediate) {
        largeChunkOfMemory = null;
        Logger.log(Logger.ERROR, Logger.MSG_ERR_EXITLOWMEMORY,
                International.getMessage("Der Arbeitsspeicher wird knapp [{detector}]: "
                + "efa versucht {jetzt} einen Neustart ...", detector,
                (immediate ? International.getString("jetzt").toUpperCase() : International.getString("jetzt"))));
        if (immediate) {
            this.cancel(null, EFA_EXIT_REASON_OOME, true);
        } else {
            EfaExitFrame.exitEfa(International.getString("Neustart wegen knappen Arbeitsspeichers"), true, EFA_EXIT_REASON_OOME);
        }
    }

    private int getListIdFromItem(IItemType item) {
        try {
            int listID = 0;
            if (item == boatsAvailableList) {
                listID = 1;
            }
            if (item == personsAvailableList) {
                listID = 1;
            }
            if (item == boatsOnTheWaterList) {
                listID = 2;
            }
            if (item == boatsNotAvailableList) {
                listID = 3;
            }
            return listID;
        } catch (Exception ex) {
            return 0;
        }
    }

    public void itemListenerAction(IItemType item, AWTEvent e) {
        int listID = 0;
        ItemTypeBoatstatusList list = null;
        ActionEvent ae = null;
        KeyEvent ke = null;
        try {
            listID = getListIdFromItem(item);
            list = (ItemTypeBoatstatusList)item;
        } catch (Exception eignore) {
        }
        try {
            ae = (ActionEvent)e;
        } catch (Exception eignore) {
        }
        try {
            ke = (KeyEvent)e;
        } catch (Exception eignore) {
        }

        if (listID != 0 && ae != null) {
            if (ae.getActionCommand().equals(EfaMouseListener.EVENT_MOUSECLICKED_1x)
                    || ae.getActionCommand().equals(EfaMouseListener.EVENT_MOUSECLICKED_2x)) {
                showBoatStatus(listID, (ItemTypeBoatstatusList) item, 1);
                if (ae.getActionCommand().equals(EfaMouseListener.EVENT_MOUSECLICKED_2x)) {
                    boatListDoubleClick(listID, list);
                }
            }
            if (ae.getActionCommand().equals(EfaMouseListener.EVENT_POPUP)) {
                showBoatStatus(listID, (ItemTypeBoatstatusList) item, 1);
            }
            // Popup clicked?
            if (ae.getActionCommand().startsWith(EfaMouseListener.EVENT_POPUP_CLICKED)) {
                TMJ subCmd = EfaUtil.string2date(ae.getActionCommand(), -1, -1, -1);
                if (subCmd.tag >= 0 && subCmd.monat >= 0) {
                    processListAction((DataRecord)list.getSelectedValue(), subCmd.monat);
                }
            }
        }

        if (listID != 0 && ke != null) {
            clearAllPopups();
            if (ke.getKeyCode() == KeyEvent.VK_ENTER || ke.getKeyCode() == KeyEvent.VK_SPACE) {
                boatListDoubleClick(listID, list);
                return;
            }
            showBoatStatus(listID, list, (ke != null && ke.getKeyCode() == 38 ? -1 : 1));
        }

        if (listID != 0 && e instanceof FocusEvent && e.getID() == FocusEvent.FOCUS_GAINED) {
            showBoatStatus(listID, list, 1);
        }
    }

    private void processListAction(DataRecord r, int action) {
        switch (action) {
            case 1: // start session
                fahrtbeginnButton_actionPerformed(null);
                break;
            case 2: // finish session
                fahrtendeButton_actionPerformed(null);
                break;
            case 3: // late entry
                nachtragButton_actionPerformed(null);
                break;
            case 4: // change session
                if (r == null) {
                    return;
                }
                // @todo eintragAendern(name);
                break;
            case 5: // cancel session
                fahrtabbruchButton_actionPerformed(null);
                break;
            case 6: // boat reservations
                bootsstatusButton_actionPerformed(null);
                break;
        }
    }

    void boatListDoubleClick(int listnr, ItemTypeBoatstatusList list) {
        if (list == null || list.getSelectedIndex() < 0) {
            return;
        }
        clearAllPopups();

        DataRecord r = (DataRecord)list.getSelectedValue();
        if (r == null) {
            return;
        }
        String name = (r instanceof BoatStatusRecord ? 
            ((BoatStatusRecord)r).getBoatNameAsString(System.currentTimeMillis()) :
            ((PersonRecord)r).getQualifiedName());

        if (listnr == 1
                && Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue()
                && toggleAvailableBoatsToPersons.isSelected()) {
            fahrtbeginnButton_actionPerformed(null);
            return;
        }

        String[] actions = getListActions(listnr, r);
        if (actions == null || actions.length == 0) {
            return;
        }
        String[] myActions = new String[actions.length + 1];
        for (int i = 0; i < actions.length; i++) {
            myActions[i] = actions[i].substring(1);
        }
        myActions[myActions.length - 1] = International.getString("Nichts");
        int selection = Dialog.auswahlDialog(International.getString("Boot") + " " + name,
                International.getMessage("Was möchtest Du mit dem Boot {boat} machen?", name),
                myActions);
        if (selection >= 0 && selection < actions.length) {
            processListAction(r, EfaUtil.string2int(actions[selection].substring(0, 1), -1));
        }
    }

    void clearAllPopups() {
        boatsAvailableList.clearPopup();
        personsAvailableList.clearPopup();
        boatsOnTheWaterList.clearPopup();;
        boatsNotAvailableList.clearPopup();
    }

    void showBoatStatus(int listnr, ItemTypeBoatstatusList list, int direction) {
        BoatStatusRecord r = null;
        String name = null;

        try { // list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
            if (r instanceof BoatStatusRecord) {
                r = (BoatStatusRecord)list.getSelectedValue();
                name = list.getSelectedText();
            } else {
                name = ((PersonRecord)list.getSelectedValue()).getQualifiedName();
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }

        if (list != personsAvailableList) {
            if (r == null && (name == null || name.startsWith("---"))) {
                try {
                    int i = list.getSelectedIndex() + direction;
                    if (i < 0) {
                        i = 1; // i<0 kann nur erreicht werden, wenn vorher i=0 und direction=-1; dann darf nicht auf i=0 gesprungen werden, da wir dort herkommen, sondern auf i=1
                    }
                    list.setSelectedIndex(i);
                    r = (BoatStatusRecord)list.getSelectedValue();
                } catch (Exception e) { /* just to be sure */ }
            }

            if (r != null) {
                BoatStatusRecord status = boatStatus.getBoatStatus(r.getBoatId());
                BoatRecord boat = Daten.project.getBoats(false).getBoat(r.getBoatId(), System.currentTimeMillis());
                name = (boat != null ? boat.getQualifiedName() : International.getString("anderes oder fremdes Boot"));
                String text = "";
                if (status != null) {
                    String s = status.getStatusDescription();
                    if (s != null) {
                        text = s;
                    }
                    s = status.getComment();
                    if (s != null) {
                        text = (text.length() > 0 ? text + ": " : "") + s;
                    }
                }
                String bootstyp = "";
                String rudererlaubnis = "";
                if (listnr == 1) {
                    if (boat != null) {
                        bootstyp = " (" + boat.getDetailedBoatType(0) + ")";
                        String groups = boat.getAllowedGroupsAsNameString(System.currentTimeMillis());
                        if (groups.length() > 0) {
                            rudererlaubnis = (rudererlaubnis.length() > 0 ? rudererlaubnis + ", "
                                    : "; " + International.getMessage("nur für {something}", groups));
                        }
                    }
                }
                statusLabelSetText(name + ": " + text + bootstyp + rudererlaubnis);
            }
        } else {
            statusLabelSetText(name);
        }
        if (listnr != 1) {
            boatsAvailableList.clearSelection();
            personsAvailableList.clearSelection();
        }
        if (listnr != 2) {
            boatsOnTheWaterList.clearSelection();
        }
        if (listnr != 3) {
            boatsNotAvailableList.clearSelection();
        }
    }

    void toggleAvailableBoats_actionPerformed(ActionEvent e) {
        if (!Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue()) {
            return;
        }
        iniGuiListNames();
        try {
            if (toggleAvailableBoatsToBoats.isSelected()) {
                boatsAvailablePanel.remove(personsAvailableList.getPanel());
                boatsAvailableList.displayOnGui(this, boatsAvailablePanel, BorderLayout.CENTER);
            } else {
                boatsAvailablePanel.remove(boatsAvailableList.getPanel());
                personsAvailableList.displayOnGui(this, boatsAvailablePanel, BorderLayout.CENTER);
            }
            updateBoatLists();
        } catch (Exception ee) {
        }
    }

    void fahrtbeginnButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        int status = BootStatus.STAT_VERFUEGBAR;

        String boot = null;
        String person = null;

        try {
            if (!booteVerfuegbar.isSelectionEmpty()) {
                boot = listGetSelectedValue(booteVerfuegbar);
            }
            if (boot == null) {
                if (!booteAufFahrt.isSelectionEmpty()) {
                    boot = listGetSelectedValue(booteAufFahrt);
                }
                if (boot == null) {
                    if (!booteNichtVerfuegbar.isSelectionEmpty()) {
                        boot = listGetSelectedValue(booteNichtVerfuegbar);
                    }
                }
            }
        } catch (Exception ee) {
            EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
        }

        if (boot == null) {
            Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
            this.booteVerfuegbar.requestFocus();
            this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }

        if (booteVerfuegbar.getSelectedIndex() == 0) { // <anderes Boot> oder <andere Person> ausgewählt!
            boot = null;
        } else {
            if (Daten.efaConfig.efaDirekt_listAllowToggleBoatsPersons.getValue() && toggleAvailableBoatsToPersons.isSelected()) {
                person = boot;
                boot = null;
            } else {
                if (!checkFahrtbeginnFuerBoot(boot, 1)) {
                    return;
                }
                boot = removeDoppeleintragFromBootsname(boot);
            }
        }

        setEnabled(false);
        efaFrame.direktFahrtAnfang(boot, person);
        */
    }

    void fahrtendeButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        String boot = null;
        try {
            if (!booteAufFahrt.isSelectionEmpty()) {
                boot = listGetSelectedValue(booteAufFahrt);
            }
            if (boot == null && Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar.getValue() && !booteNichtVerfuegbar.isSelectionEmpty()) {
                // prüfen, ob vielleicht ein Boot in der Liste "nicht verfügbar" auf Regatta oder Wanderfahrt unterwegs ist
                boot = listGetSelectedValue(booteNichtVerfuegbar);
                if (boot != null && bootStatus.getExact(boot) != null) {
                    DatenFelder d = (DatenFelder) bootStatus.getComplete();
                    if (EfaUtil.string2int(d.get(BootStatus.LFDNR), 0) == 0) {
                        boot = null; // keine gültige LfdNr
                    }
                }
            }
        } catch (Exception ee) {
            EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
        }
        if (boot == null) {
            Dialog.error(International.getMessage("Bitte wähle zuerst {from_the_right_list} ein Boot aus, welches unterwegs ist!",
                    (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar.getValue()
                    ? International.getString("aus einer der rechten Listen")
                    : International.getString("aus der rechten oberen Liste"))));
            this.booteAufFahrt.requestFocus();
            this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }
        if (bootStatus.getExact(boot) != null) {
            DatenFelder d = (DatenFelder) bootStatus.getComplete();
            if (d.get(BootStatus.LFDNR).trim().length() == 0) {
                // keine LfdNr eingetragen: Das kann passieren, wenn der Admin den Status der Bootes manuell geändert hat!
                String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat}.", boot)
                        + " " + International.getString("Die Fahrt kann nicht beendet werden.");
                Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT,
                        s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
                Dialog.error(s);
                return;
            }
            if (Daten.fahrtenbuch.getExact(d.get(BootStatus.LFDNR)) == null) {
                String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat} und LfdNr {lfdnr}.",
                        boot, d.get(BootStatus.LFDNR))
                        + " " + International.getString("Die Fahrt kann nicht beendet werden.");
                Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT,
                        s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
                Dialog.error(s);
                return;
            }
            setEnabled(false);
            efaFrame.direktFahrtEnde(boot, d.get(BootStatus.LFDNR));
        } else {
            if (boot.startsWith("----------")) {
                return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
            }
            String s = International.getString("Programmfehler") + ": "
                    + International.getMessage("Boot {boat} nicht in der Statusliste gefunden!", boot);
            Dialog.error(s);
            Logger.log(Logger.ERROR, Logger.MSG_ERR_BOATNOTFOUNDINSTATUS, s);
        }
        */
    }

    void fahrtabbruchButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        String boot = null;
        try {
            if (!booteAufFahrt.isSelectionEmpty()) {
                boot = listGetSelectedValue(booteAufFahrt);
            }
            if (boot == null && Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar.getValue() && !booteNichtVerfuegbar.isSelectionEmpty()) {
                // prüfen, ob vielleicht ein Boot in der Liste "nicht verfügbar" auf Regatta oder Wanderfahrt unterwegs ist
                boot = listGetSelectedValue(booteNichtVerfuegbar);
                if (boot != null && bootStatus.getExact(boot) != null) {
                    DatenFelder d = (DatenFelder) bootStatus.getComplete();
                    if (EfaUtil.string2int(d.get(BootStatus.LFDNR), 0) == 0) {
                        boot = null; // keine gülte LfdNr
                    }
                }
            }
        } catch (Exception ee) {
            EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
        }
        if (boot == null) {
            Dialog.error(International.getMessage("Bitte wähle zuerst {from_the_right_list} ein Boot aus, welches unterwegs ist!",
                    (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar.getValue()
                    ? International.getString("aus einer der rechten Listen")
                    : International.getString("aus der rechten oberen Liste"))));
            this.booteAufFahrt.requestFocus();
            this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }
        fahrtAbbruch(boot, true);
        */
    }

    void nachtragButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        setEnabled(false);

        String boot = null;
        try {
            if (!booteVerfuegbar.isSelectionEmpty() && booteVerfuegbar.getSelectedIndex() != 0) {
                boot = listGetSelectedValue(booteVerfuegbar);
            }
            if (boot == null) {
                if (!booteAufFahrt.isSelectionEmpty()) {
                    boot = listGetSelectedValue(booteAufFahrt);
                }
                if (boot == null) {
                    if (!booteNichtVerfuegbar.isSelectionEmpty()) {
                        boot = listGetSelectedValue(booteNichtVerfuegbar);
                    }
                }
            }
        } catch (Exception ee) {
        }
        if (boot != null && boot.startsWith("-")) {
            boot = null;
        }
        efaFrame.direktFahrtNachtrag(boot);
        */
    }

    void bootsstatusButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        String boot = null;
        try {
            if (!booteVerfuegbar.isSelectionEmpty()) {
                boot = listGetSelectedValue(booteVerfuegbar);
            }
            if (boot == null) {
                if (!booteAufFahrt.isSelectionEmpty()) {
                    boot = listGetSelectedValue(booteAufFahrt);
                }
                if (boot == null) {
                    if (!booteNichtVerfuegbar.isSelectionEmpty()) {
                        boot = listGetSelectedValue(booteNichtVerfuegbar);
                    }
                }
            } else {
                if (booteVerfuegbar.getSelectedIndex() == 0) {
                    Dialog.error(International.getString("Dieses Boot kann nicht reserviert werden!"));
                    return;
                }
            }
        } catch (Exception ee) {
            EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
        }
        if (boot == null) {
            Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
            return;
        }

        boot = removeDoppeleintragFromBootsname(boot);

        DatenFelder d = bootStatus.getExactComplete(boot);
        if (d == null) {
            String s = International.getMessage("Boot {boat} nicht in der Statusliste gefunden!", boot);
            Dialog.error(s);
            Logger.log(Logger.ERROR, Logger.MSG_ERR_BOATNOTFOUNDINSTATUS, s);
            return;
        }
        if (d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) {
            Dialog.error(International.getString("Dieses Boot kann nicht reserviert werden!"));
            return;
        }
        BootStatusFrame dlg = new BootStatusFrame(this, d, bootStatus);
        Dialog.setDlgLocation(dlg, this);
        dlg.setModal(true);
        dlg.show();
        */
    }

    void showFbButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        if (FahrtenbuchAnzeigenFrame.wirdBereitsAngezeigt) {
            return;
        }
        FahrtenbuchAnzeigenFrame dlg = new FahrtenbuchAnzeigenFrame(this);
        Dialog.setDlgLocation(dlg, this);
        dlg.setModal(true);
        dlg.show();
        dlg = null;
        */
    }

    void statButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().statistik == null) {
            Dialog.error(International.getString("Es sind keine Statistiken verfügbar!") + "\n\n"
                    + International.getString("Hinweis für Administratoren") + ":\n"
                    + International.getString("Damit Statistiken im Bootshaus von jedem Mitglied aufrufbar sind, "
                    + "müssen sie zuerst im Admin-Modus vorbereitet und als Statistikeinstellungen "
                    + "abgespeichert werden. Beim Abspeichern muß zusätzliche die Option "
                    + "'Statistik auch im Bootshaus verfügbar machen' aktiviert werden."));
            return;
        }

        Vector stats = new Vector();
        DatenFelder d = Daten.fahrtenbuch.getDaten().statistik.getCompleteFirst();
        while (d != null) {
            if (d.get(StatSave.AUCHINEFADIREKT).equals("+")) {
                stats.add(d.get(StatSave.NAMESTAT));
            }
            d = Daten.fahrtenbuch.getDaten().statistik.getCompleteNext();
        }
        if (stats.size() == 0) {
            Dialog.error(International.getString("Es sind keine Statistiken verfügbar!") + "\n\n"
                    + International.getString("Hinweis für Administratoren") + ":\n"
                    + International.getString("Damit Statistiken im Bootshaus von jedem Mitglied aufrufbar sind, "
                    + "müssen sie zuerst im Admin-Modus vorbereitet und als Statistikeinstellungen "
                    + "abgespeichert werden. Beim Abspeichern muß zusätzliche die Option "
                    + "'Statistik auch im Bootshaus verfügbar machen' aktiviert werden."));
            return;
        }

        try {
            StatistikDirektFrame dlg = new StatistikDirektFrame(this, stats);
            Dialog.setDlgLocation(dlg, this);
            dlg.setModal(true);
            dlg.show();
        } catch (Exception ee) {
            // HTML Reder Exception reported by Thilo Coblenzer (01.06.06)
        }
        */
    }

    void adminHinweisButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        NachrichtAnAdminFrame dlg = new NachrichtAnAdminFrame(this, Daten.nachrichten);
        Dialog.setDlgLocation(dlg, this);
        dlg.setModal(true);
        dlg.show();
        updateUnreadMessages();
        */
    }

    void adminButton_actionPerformed(ActionEvent e) {
        alive();
        clearAllPopups();

        // Prüfe, ob bereits ein Admin-Modus-Fenster offen ist
        Stack s = Dialog.frameStack;
        boolean adminOnStack = false;
        try {
            for (int i = 0; i < s.size(); i++) {
                if (s.elementAt(i).getClass().getName().equals("de.nmichael.efa.gui.AdminDialog")) {
                    adminOnStack = true;
                }
            }
        } catch (Exception ee) {
        }
        if (adminOnStack) {
            Dialog.error(International.getString("Es ist bereits ein Admin-Fenster geöffnet."));
            return;
        }

        AdminRecord admin = AdminLoginDialog.login(this, International.getString("Admin-Modus"));
        if (admin == null) {
            return;
        }
        AdminDialog dlg = new AdminDialog(this, admin);
        // Folgende Zeile *muß* auskommentiert sein, da unter Java 1.5 sonst in den im Admin-Modus geöffneten Dialogen
        // keine Eingaben möglich sind. Dies scheint ein Bug in 1.5 zu sein. Da EfaDirektFrame aktiviert bleibt, ist
        // ein Navigieren im Admin-Modus im EfaDirektFrame möglich, was aber eine vertretbare Unschönheit ist... ;-)
        //    this.setEnabled(false); //!!!
        if (Daten.getJavaVersion() > 5) {
            this.setEnabled(false);
        }
        dlg.showDialog();
        this.setEnabled(true);
        updateBoatLists();
        iniGuiWidgets();
    }

    void spezialButton_actionPerformed(ActionEvent e) {
        /*
        alive();
        clearAllPopups();
        String cmd = Daten.efaConfig.efaDirekt_butSpezialCmd.getValue().trim();
        if (cmd.length() > 0) {
            try {
                if (cmd.toLowerCase().startsWith("browser:")) {
                    Dialog.neuBrowserDlg(this, International.getString("Browser"), cmd.substring(8));
                } else {
                    Runtime.getRuntime().exec(cmd);
                }
            } catch (Exception ee) {
                LogString.logWarning_cantExecCommand(cmd, International.getString("für Spezial-Button"), ee.toString());
            }
        } else {
            Dialog.error(International.getString("Kein Kommando für diesen Button konfiguriert!"));
        }
        */
    }

    void efaButton_actionPerformed(ActionEvent e) {
        alive();
        clearAllPopups();
        EfaAboutDialog dlg = new EfaAboutDialog(this);
        dlg.showDialog();
    }

    void hilfeButton_actionPerformed(ActionEvent e) {
        clearAllPopups();
        Help.showHelp(helpTopic);
    }

    public void lockEfaAt(DataTypeDate datum, DataTypeTime zeit) {
        // @todo efaDirektBackgroundTask.setEfaLockBegin(datum, zeit);
    }

    public void lockEfa() {
        if (Daten.efaConfig == null) {
            return;
        }

        String endeDerSperrung = (Daten.efaConfig.efaDirekt_lockEfaUntilDatum.isSet() ? " " + International.getString("Ende der Sperrung") + ": "
                + Daten.efaConfig.efaDirekt_lockEfaUntilDatum.toString()
                + (Daten.efaConfig.efaDirekt_lockEfaUntilZeit.isSet() ? " " + Daten.efaConfig.efaDirekt_lockEfaUntilZeit.toString() : "") : "");

        String html = Daten.efaConfig.efaDirekt_lockEfaShowHtml.getValue();
        if (html == null || !EfaUtil.canOpenFile(html)) {
            html = Daten.efaTmpDirectory + "locked.html";
            try {
                BufferedWriter f = new BufferedWriter(new FileWriter(html));
                f.write("<html><body><h1 align=\"center\">" + International.getString("efa ist für die Benutzung gesperrt") + "</h1>\n");
                f.write("<p>" + International.getString("efa wurde vom Administrator vorübergehend für die Benutzung gesperrt.") + "</p>\n");
                if (endeDerSperrung.length() > 0) {
                    f.write("<p>" + endeDerSperrung + "</p>\n");
                }
                f.write("</body></html>\n");
                f.close();
            } catch (Exception e) {
                EfaUtil.foo();
            }
        }
        BrowserFrame browser = null; // @todo new BrowserFrame(this, Daten.efaConfig.efaDirekt_lockEfaVollbild.getValue(), "file:" + html);
        browser.setModal(true);
        if (Daten.efaConfig.efaDirekt_lockEfaVollbild.getValue()) {
            browser.setSize(Dialog.screenSize);
        }
        Dialog.setDlgLocation(browser, this);
        browser.setClosingTimeout(10); // nur um Lock-Ende zu überwachen
        Logger.log(Logger.INFO, Logger.MSG_EVT_LOCKED,
                International.getString("efa wurde vom Administrator vorübergehend für die Benutzung gesperrt.") + endeDerSperrung);
        Daten.efaConfig.efaDirekt_lockEfaFromDatum.unset(); // damit nach Entsperren nicht wiederholt gelockt wird
        Daten.efaConfig.efaDirekt_lockEfaFromZeit.unset();  // damit nach Entsperren nicht wiederholt gelockt wird
        Daten.efaConfig.efaDirekt_locked.setValue(true);
        Daten.efaConfig.writeFile();
        browser.show();
    }

}
