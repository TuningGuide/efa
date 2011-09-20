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
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.dataedit.*;
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
    EfaBoathouseBackgroundTask efaBoathouseBackgroundTask;
    EfaBaseFrame efaBaseFrame;
    Logbook logbook;
    BoatStatus boatStatus;
    volatile long lastUserInteraction = 0;
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
            actionStartSession();
        }
        if (evt.getActionCommand().equals(KEYACTION_F3)) {
            actionFinishSession();
        }
        if (evt.getActionCommand().equals(KEYACTION_F4)) {
            actionAbortSession();
        }
        if (evt.getActionCommand().equals(KEYACTION_F5)) {
            actionLateEntry();
        }
        if (evt.getActionCommand().equals(KEYACTION_F6)) {
            actionBoatReservations();
        }
        if (evt.getActionCommand().equals(KEYACTION_F7)) {
            actionShowLogbook();
        }
        if (evt.getActionCommand().equals(KEYACTION_F8)) {
            actionStatistics();
        }
        if (evt.getActionCommand().equals(KEYACTION_F9)) {
            actionMessageToAdmin();
        }
        if (evt.getActionCommand().equals(KEYACTION_altF10)) {
            actionAdminMode();
        }
        if (evt.getActionCommand().equals(KEYACTION_altF11)) {
            actionSpecial();
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
            cancel();
        }
        if (evt.getActionCommand().equals(KEYACTION_shiftF4)) {
            cancel();
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
        updateGuiWidgets();
    }

    public void updateGuiElements() {
        updateGuiWidgets();
        updateGuiClock();
        updateGuiNews();
        updateGuiButtonText();
        updateGuiLogo();
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
        openProject((AdminRecord)null);
        if (Daten.project == null) {
            Logger.log(Logger.ERROR, Logger.MSG_ERR_NOPROJECTOPENED, International.getString("Kein Projekt geöffnet."));
        } else {
            openLogbook((AdminRecord) null);
            if (logbook == null) {
                Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGBOOKOPENED, International.getString("Kein Fahrtenbuch geöffnet."));
            }
        }

        updateBoatLists(true);

        EfaExitFrame.initExitFrame(this);
        
        // Speicher-Überwachung
        try {
            de.nmichael.efa.java15.Java15.setMemUsageListener(this, Daten.MIN_FREEMEM_COLLECTION_THRESHOLD);
        } catch (UnsupportedClassVersionError e) {
            EfaUtil.foo();
        } catch (NoClassDefFoundError e) {
            EfaUtil.foo();
        }
        
        // Background Task
        efaBoathouseBackgroundTask = new EfaBoathouseBackgroundTask(this);
        efaBoathouseBackgroundTask.start();

        alive();
        Logger.log(Logger.INFO, Logger.MSG_EVT_EFAREADY, International.getString("BEREIT"));
    }

    private void iniGuiRemaining() {
        // Fenster nicht verschiebbar
        if (Daten.efaConfig.getValueEfaDirekt_fensterNichtVerschiebbar()) {
            try {
                // must be called before any packing of the frame, since packing makes the frame displayable!
                this.setUndecorated(true);
                Color bgColor = new Color(0, 0, 170);

                EmptyBorder b = new EmptyBorder(2,2,2,2);
                mainPanel.setBackground(bgColor);
                mainPanel.setBorder(b);

                JMenuBar menuBar = new JMenuBar();
                menuBar.setLayout(new BorderLayout());
                menuBar.setBackground(bgColor);
                menuBar.setForeground(Color.white);
                JLabel efaLabel = new JLabel();
                efaLabel.setIcon(getIcon("efa_icon_small.png"));
                JLabel titleLabel = new JLabel();
                titleLabel.setText(Daten.EFA_LONGNAME);
                titleLabel.setForeground(Color.white);
                titleLabel.setFont(titleLabel.getFont().deriveFont(12f));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                JButton closeButton = new JButton();
                closeButton.setIcon(getIcon("frame_close.png"));
                closeButton.setBackground(bgColor);
                closeButton.setForeground(Color.white);
                closeButton.setFont(closeButton.getFont().deriveFont(10f));
                closeButton.setBorder(null);
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancel();
                    }
                });
                menuBar.add(efaLabel, BorderLayout.WEST);
                menuBar.add(titleLabel, BorderLayout.CENTER);
                menuBar.add(closeButton, BorderLayout.EAST);
                menuBar.setBorder(new EmptyBorder(2,5,2,5));
                menuBar.validate();
                this.setJMenuBar(menuBar);
            } catch (NoSuchMethodError e) {
                Logger.log(Logger.WARNING, Logger.MSG_WARN_JAVA_VERSION,
                        International.getMessage("Gewisse Funktionalität wird erst ab Java Version {version} unterstützt: {msg}","1.4",e.toString()));
            }
        }

        // Fenster immer im Vordergrund
        try {
            if (Daten.efaConfig.getValueEfaDirekt_immerImVordergrund()) {
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
        if (Daten.efaConfig.getValueEfaDirekt_startMaximized()) {
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
        if (Daten.efaConfig.getValueEfaDirekt_locked()) {
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
            if (Daten.efaConfig.getValueEfaDirekt_lockEfaFromDatum().isSet()) {
                lockEfaAt(Daten.efaConfig.getValueEfaDirekt_lockEfaFromDatum(), Daten.efaConfig.getValueEfaDirekt_lockEfaFromZeit());
            }
        }

        // note: packing must happen at the very end, since it makes the frame "displayable", which then
        // does not allow to change any window settings like setUndecorated()
        packFrame("iniGuiRemaining()");
        boatsAvailableList.requestFocus();
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
        toggleAvailableBoatsToBoats.setVisible(Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons());
        toggleAvailableBoatsToPersons.setVisible(Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons());

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
        togglePanel.setVisible(Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons());
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
        boolean fkey = Daten.efaConfig.getValueEfaDirekt_showButtonHotkey();
        if (!Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons() || toggleAvailableBoatsToBoats.isSelected()) {
            boatsAvailableList.setDescription(International.getString("verfügbare Boote") + (fkey ? " [F10]" : ""));
        } else {
            personsAvailableList.setDescription(International.getString("Personen") + (fkey ? " [F10]" : ""));
        }
        boatsOnTheWaterList.setDescription(International.getString("Boote auf Fahrt") + (fkey ? " [F11]" : ""));
        boatsNotAvailableList.setDescription(International.getString("nicht verfügbare Boote") + (fkey ? " [F12]" : ""));
    }
    
    private void iniGuiCenterPanel() {
         updateGuiLogo();
        iniGuiButtons();
        updateGuiClock();

        centerPanel.setLayout(new GridBagLayout());
        int logoTop = (int) (10.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
        int logoBottom = 5;
        if (Daten.efaConfig.getValueEfaDirekt_startMaximized() && Daten.efaConfig.getValueEfaDirekt_vereinsLogo().length() > 0) {
            logoBottom += (int) ((Dialog.screenSize.getHeight() - 825) / 5);
            if (logoBottom < 0) {
                logoBottom = 0;
            }
        }
        centerPanel.add(logoLabel, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(logoTop, 0, logoBottom, 0), 0, 0));
        int fahrtbeginnTop = (int) (10.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
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

    private void updateGuiLogo() {
        if (Daten.efaConfig.getValueEfaDirekt_vereinsLogo().length() > 0) {
            try {
                logoLabel.setIcon(new ImageIcon(Daten.efaConfig.getValueEfaDirekt_vereinsLogo()));
                logoLabel.setMinimumSize(new Dimension(200, 80));
                logoLabel.setPreferredSize(new Dimension(200, 80));
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            } catch (Exception e) {
                Logger.logdebug(e);
            }
        } else {
            logoLabel.setIcon(null);
        }
    }

    private void iniGuiButtons() {
        Mnemonics.setButton(this, startSessionButton, International.getStringWithMnemonic("Fahrt beginnen"));
        startSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionStartSession();
            }
        });

        Mnemonics.setButton(this, finishSessionButton, International.getStringWithMnemonic("Fahrt beenden"));
        finishSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionFinishSession();
            }
        });

        Mnemonics.setButton(this, abortSessionButton, International.getStringWithMnemonic("Fahrt abbrechen"));
        abortSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionAbortSession();
            }
        });

        Mnemonics.setButton(this, lateEntryButton, International.getStringWithMnemonic("Nachtrag"));
        lateEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionLateEntry();
            }
        });

        Mnemonics.setButton(this, boatStatusButton, International.getStringWithMnemonic("Bootsreservierungen"));
        boatStatusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionBoatReservations();
            }
        });

        Mnemonics.setButton(this, showLogbookButton, International.getStringWithMnemonic("Fahrtenbuch anzeigen"));
        showLogbookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionShowLogbook();
            }
        });

        Mnemonics.setButton(this, statisticsButton, International.getStringWithMnemonic("Statistik erstellen"));
        statisticsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionStatistics();
            }
        });

        Mnemonics.setButton(this, messageToAdminButton, International.getStringWithMnemonic("Nachricht an Admin"));
        messageToAdminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionMessageToAdmin();
            }
        });

        Mnemonics.setButton(this, adminButton, International.getStringWithMnemonic("Admin-Modus"));
        adminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionAdminMode();
            }
        });

        specialButton.setText(International.getString("Spezial-Button"));
        specialButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSpecial();
            }
        });

        helpButton.setText(International.getMessage("Hilfe mit {key}", "[F1]"));
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hilfeButton_actionPerformed(e);
            }
        });

        efaButton.setPreferredSize(new Dimension(90, 55));
        efaButton.setIcon(getIcon(Daten.getEfaImage(1)));
        efaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                efaButton_actionPerformed(e);
            }
        });

        updateGuiButtonText();
    }

    private void setButtonLAF(JButton button, ItemTypeConfigButton config, String icon) {
        if (config != null) {
            button.setBackground(EfaUtil.getColor(config.getValueColor()));
        }
        if (icon != null && button.isVisible()) {
            button.setIcon(getIcon(icon));
            button.setIconTextGap(10);
            button.setHorizontalAlignment(SwingConstants.LEFT);
        }
    }

    private void updateGuiButtonLAF() {
        this.boatStatusButton.setVisible(Daten.efaConfig.getValueEfaDirekt_butBootsreservierungen().getValueShow());
        this.showLogbookButton.setVisible(Daten.efaConfig.getValueEfaDirekt_butFahrtenbuchAnzeigen().getValueShow());
        this.statisticsButton.setVisible(Daten.efaConfig.getValueEfaDirekt_butStatistikErstellen().getValueShow());
        this.messageToAdminButton.setVisible(Daten.efaConfig.getValueEfaDirekt_butNachrichtAnAdmin().getValueShow());
        this.adminButton.setVisible(Daten.efaConfig.getValueEfaDirekt_butAdminModus().getValueShow());
        this.specialButton.setVisible(Daten.efaConfig.getValueEfaDirekt_butSpezial().getValueShow());

        setButtonLAF(startSessionButton, Daten.efaConfig.getValueEfaDirekt_butFahrtBeginnen(), "action_startSession.png");
        setButtonLAF(finishSessionButton, Daten.efaConfig.getValueEfaDirekt_butFahrtBeenden(), "action_finishSession.png");
        setButtonLAF(abortSessionButton, Daten.efaConfig.getValueEfaDirekt_butFahrtAbbrechen(), "action_abortSession.png");
        setButtonLAF(lateEntryButton, Daten.efaConfig.getValueEfaDirekt_butNachtrag(), "action_lateEntry.png");
        setButtonLAF(boatStatusButton, Daten.efaConfig.getValueEfaDirekt_butBootsreservierungen(), "action_boatReservations.png");
        setButtonLAF(showLogbookButton, Daten.efaConfig.getValueEfaDirekt_butFahrtenbuchAnzeigen(), "action_logbook.png");
        setButtonLAF(statisticsButton, Daten.efaConfig.getValueEfaDirekt_butStatistikErstellen(), "action_statistics.png");
        setButtonLAF(messageToAdminButton, Daten.efaConfig.getValueEfaDirekt_butNachrichtAnAdmin(), "action_message.png");
        setButtonLAF(adminButton, Daten.efaConfig.getValueEfaDirekt_butAdminModus(), "action_admin.png");
        setButtonLAF(specialButton, Daten.efaConfig.getValueEfaDirekt_butSpezial(), "action_special.png");
        setButtonLAF(helpButton, null, "action_help.png");

    }

    public void updateGuiButtonText() {
        boolean fkey = Daten.efaConfig.getValueEfaDirekt_showButtonHotkey();
        this.startSessionButton.setText(Daten.efaConfig.getValueEfaDirekt_butFahrtBeginnen().getValueText() + (fkey ? " [F2]" : ""));
        this.finishSessionButton.setText(Daten.efaConfig.getValueEfaDirekt_butFahrtBeenden().getValueText() + (fkey ? " [F3]" : ""));
        this.abortSessionButton.setText(International.getString("Fahrt abbrechen") + (fkey ? " [F4]" : ""));
        this.lateEntryButton.setText(International.getString("Nachtrag") + (fkey ? " [F5]" : ""));
        this.boatStatusButton.setText(International.getString("Bootsreservierungen") + (fkey ? " [F6]" : ""));
        this.showLogbookButton.setText(International.getString("Fahrtenbuch anzeigen") + (fkey ? " [F7]" : ""));
        this.statisticsButton.setText(International.getString("Statistik erstellen") + (fkey ? " [F8]" : ""));
        this.messageToAdminButton.setText(International.getString("Nachricht an Admin") + (fkey ? " [F9]" : ""));
        this.adminButton.setText(International.getString("Admin-Modus") + (fkey ? " [Alt-F10]" : ""));
        this.specialButton.setText(Daten.efaConfig.getValueEfaDirekt_butSpezial().getValueText() + (fkey ? " [Alt-F11]" : ""));
        updateGuiButtonLAF();
        if (!Daten.efaConfig.getValueEfaDirekt_startMaximized() && isDisplayable()) {
            packFrame("iniButtonText()");
        }
    }

    private void updateGuiClock() {
        if (clock == null) {
            clock = new ClockMiniWidget();
        }
        clock.getGuiComponent().setVisible(Daten.efaConfig.getValueEfaDirekt_showUhr());
    }

    private void updateGuiNews() {
        if (news == null) {
            news = new NewsMiniWidget();
        }
        news.setText(Daten.efaConfig.getValueEfaDirekt_newsText());
        news.setScrollSpeed(Daten.efaConfig.getValueEfaDirekt_newsScrollSpeed());
        news.getGuiComponent().setVisible(Daten.efaConfig.getValueEfaDirekt_showNews());
        if (isDisplayable()) {
            packFrame("updateGuiNews()");
        }
    }

    private void updateGuiWidgets() {
        widgetTopPanel.removeAll();
        widgetBottomPanel.removeAll();
        widgetLeftPanel.removeAll();
        widgetRightPanel.removeAll();
        widgetCenterPanel.removeAll();

        if (Daten.efaConfig.getWidgets() == null) {
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
            IItemType enabled = Daten.efaConfig.getExternalGuiItem(w.getParameterName(Widget.PARAM_ENABLED));
            if (enabled != null && enabled instanceof ItemTypeBoolean && ((ItemTypeBoolean)enabled).getValue()) {
                // set parameters for this enabled widget according to configuration
                IItemType[] params = w.getParameters();
                for (int j=0; j<params.length; j++) {
                    params[j].parseValue(Daten.efaConfig.getExternalGuiItem(params[j].getName()).toString());
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
        updateGuiNews();
        northPanel.setLayout(new BorderLayout());
    }

    private void iniGuiSouthPanel() {
        updateGuiNews();
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
    
    public void bringFrameToFront() {
        this.toFront();
    }

    // i == 0 - automatically try to find correct list to focus
    // i == 1 - boats/persons available
    // i == 2 - boats on the water
    // i == 3 - boats not available
    public void boatListRequestFocus(int i) {
        if (i == 0) {
            if (boatsAvailableList != null && boatsAvailableList.getSelectedIndex() >= 0) {
                boatsAvailableList.requestFocus();
            } else if (personsAvailableList != null && personsAvailableList.getSelectedIndex() >= 0) {
                personsAvailableList.requestFocus();
            } else if (boatsOnTheWaterList != null && boatsOnTheWaterList.getSelectedIndex() >= 0) {
                boatsOnTheWaterList.requestFocus();
            } else if (boatsNotAvailableList != null && boatsNotAvailableList.getSelectedIndex() >= 0) {
                boatsNotAvailableList.requestFocus();
            } else if (boatsAvailableList != null) {
                boatsAvailableList.requestFocus();
            }
        }
        if (i == 1) {
            if (toggleAvailableBoatsToBoats.isSelected()) {
                boatsAvailableList.requestFocus();
            } else {
                personsAvailableList.requestFocus();
            }
        }
        if (i == 2) {
            boatsOnTheWaterList.requestFocus();
        }
        if (i == 3) {
            boatsNotAvailableList.requestFocus();
        }
    }
    
    void alive() {
        lastUserInteraction = System.currentTimeMillis();
    }

    public long getLastUserInteraction() {
        return lastUserInteraction;
    }

    private void this_windowClosing(WindowEvent e) {
        cancel(e, EFA_EXIT_REASON_USER, null, false);
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

    public boolean cancel() {
        return cancel(null, EFA_EXIT_REASON_USER, null, false);
    }

    public boolean cancel(WindowEvent e, int reason, AdminRecord admin, boolean restart) {
        int exitCode = 0;
        String who = "unknown";

        switch (reason) {
            case EFA_EXIT_REASON_USER: // manuelles Beenden von efa
                boolean byUser;
                if (admin != null || !Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenEfaBeenden()) {
                    while (admin == null || !admin.isAllowedExitEfa()) {
                        if (admin == null) {
                            admin = AdminLoginDialog.login(this, International.getString("Beenden von efa"));
                            if (admin == null) {
                                return false;
                            }
                        }
                        if (admin != null && !admin.isAllowedExitEfa()) {
                            EfaMenuButton.insufficientRights(admin, EfaMenuButton.BUTTON_EXIT);
                            admin = null;
                        }
                    }
                    who = International.getString("Admin") + "=" + admin.getName();
                    byUser = false;
                } else {
                    who = International.getString("Nutzer");
                    byUser = true;
                }
                if (Daten.efaConfig.getValueEfaDirekt_execOnEfaExit().length() > 0 && byUser) {
                    Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXITEXECCMD,
                            International.getMessage("Programmende veranlaßt; versuche, Kommando '{cmd}' auszuführen...", Daten.efaConfig.getValueEfaDirekt_execOnEfaExit()));
                    try {
                        Runtime.getRuntime().exec(Daten.efaConfig.getValueEfaDirekt_execOnEfaExit());
                    } catch (Exception ee) {
                        Logger.log(Logger.ERROR, Logger.MSG_ERR_EFAEXITEXECCMD_FAILED,
                                LogString.logstring_cantExecCommand(Daten.efaConfig.getValueEfaDirekt_execOnEfaExit(), International.getString("Kommando")));
                    }
                }
                break;
            case EFA_EXIT_REASON_TIME:
                who = International.getString("Zeitsteuerung");
                if (Daten.efaConfig.getValueEfaDirekt_execOnEfaAutoExit().length() > 0) {
                    Logger.log(Logger.INFO, Logger.MSG_EVT_EFAEXITEXECCMD,
                            International.getMessage("Programmende veranlaßt; versuche, Kommando '{cmd}' auszuführen...", Daten.efaConfig.getValueEfaDirekt_execOnEfaAutoExit()));
                    try {
                        Runtime.getRuntime().exec(Daten.efaConfig.getValueEfaDirekt_execOnEfaAutoExit());
                    } catch (Exception ee) {
                        Logger.log(Logger.ERROR, Logger.MSG_ERR_EFAEXITEXECCMD_FAILED,
                                LogString.logstring_cantExecCommand(Daten.efaConfig.getValueEfaDirekt_execOnEfaAutoExit(), International.getString("Kommando")));
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
            // this boat may have been sorted into a "wrong" list... fix its status first
            String s = ((BoatStatusRecord)r).getCurrentStatus();
            if (s != null && s.equals(BoatStatusRecord.STATUS_AVAILABLE)) {
                listnr = 1;
            }
            if (s != null && s.equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
                listnr = 2;
            }
            if (s != null && s.equals(BoatStatusRecord.STATUS_NOTAVAILABLE)) {
                listnr = 3;
            }
        }

        String fahrtBeginnen = EfaUtil.replace(Daten.efaConfig.getValueEfaDirekt_butFahrtBeginnen().getValueText(), ">>>", "").trim();
        String fahrtBeenden = EfaUtil.replace(Daten.efaConfig.getValueEfaDirekt_butFahrtBeenden().getValueText(), "<<<", "").trim();
        if (listnr == 1 || listnr == 101) { // verfügbare Boote bzw. Personen
            if (Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservieren() && listnr == 1) {
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
            if (Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservieren()) {
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
            if (Daten.efaConfig.getValueEfaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar()) {
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

    public Project openProject(AdminRecord admin) {
        // close any other projects first
        if (Daten.project != null) {
            try {
                Daten.project.closeAllStorageObjects();
            } catch(Exception e) {
                String msg = LogString.logstring_fileCloseFailed(Daten.project.getProjectName(), International.getString("Projekt"), e.getMessage());
                Logger.log(Logger.ERROR, Logger.MSG_DATA_CLOSEFAILED, msg);
                Dialog.error(msg);
            }
        }
        Daten.project = null;
        logbook = null;

        // project to open
        String projectName = null;
        if (admin == null && Daten.efaConfig.getValueLastProjectEfaBoathouse().length() > 0) {
            projectName = Daten.efaConfig.getValueLastProjectEfaBoathouse();
        }

        if (projectName == null || projectName.length() == 0) {
            if (admin != null && admin.isAllowedAdministerProjectLogbook()) {
                OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.project);
                projectName = dlg.openDialog();
            }
        }
        
        if (projectName == null || projectName.length() == 0) {
            return null;
        }
        if (!Project.openProject(projectName)) {
            Daten.project = null;
            return null;
        }

        Daten.efaConfig.setValueLastProjectEfaBoathouse(projectName);
        boatStatus = Daten.project.getBoatStatus(false);
        Logger.log(Logger.INFO, Logger.MSG_EVT_PROJECTOPENED, LogString.logstring_fileOpened(projectName, International.getString("Projekt")));

        if (efaBoathouseBackgroundTask != null) {
            efaBoathouseBackgroundTask.interrupt();
        }
        return Daten.project;
    }

    public Logbook openLogbook(AdminRecord admin) {
        if (Daten.project == null) {
            return null;
        }

        // close any other logbook first
        if (logbook != null) {
            try {
                logbook.close();
            } catch (Exception e) {
                String msg = LogString.logstring_fileCloseFailed(Daten.project.getProjectName(), International.getString("Fahrtenbuch"), e.toString());
                Logger.log(Logger.ERROR, Logger.MSG_DATA_CLOSEFAILED, msg);
                Logger.logdebug(e);
                Dialog.error(msg);
                logbook = null;
            }
        }

        // logbook to open
        String logbookName = null;
        if (admin == null && Daten.project.getCurrentLogbookEfaBoathouse() != null) {
            logbookName = Daten.project.getCurrentLogbookEfaBoathouse();
        }

        if (logbookName == null || logbookName.length() == 0) {
            if (admin != null && admin.isAllowedAdministerProjectLogbook()) {
                OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.logbook);
                logbookName = dlg.openDialog();
            }
        }
        if (logbookName == null || logbookName.length() == 0) {
            return null;
        }
        if (!openLogbook(logbookName)) {
            logbook = null;
            return null;
        }
        return logbook;
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
                Logger.log(Logger.INFO, Logger.MSG_EVT_LOGBOOKOPENED, LogString.logstring_fileOpened(logbookName, International.getString("Fahrtenbuch")));
                if (efaBoathouseBackgroundTask != null) {
                    efaBoathouseBackgroundTask.interrupt();
                }
                return true;
            } else {
                Dialog.error(International.getMessage("Fahrtenbuch {logbook} konnte nicht geöffnet werden.", logbookName));
            }
        }
        return false;
    }

    public Logbook getLogbook() {
        return logbook;
    }


    public void updateBoatLists(boolean listChanged) {
        if (!isEnabled()) {
            return;
        }
  
        if (Daten.project == null || boatStatus == null) {
            boatsAvailableList.setItems(null);
            personsAvailableList.setItems(null);
            boatsOnTheWaterList.setItems(null);
            boatsNotAvailableList.setItems(null);
            if (Daten.project == null) {
                boatsAvailableList.addItem("*** " + International.getString("Kein Projekt geöffnet.") + " ***", null, false, '\0');
                personsAvailableList.addItem("*** " + International.getString("Kein Projekt geöffnet.") + " ***", null, false, '\0');
            }
            boatsAvailableList.showValue();
            personsAvailableList.showValue();
            boatsOnTheWaterList.showValue();
            boatsNotAvailableList.showValue();
        } else {
            if (boatsAvailableList.size() == 0) {
                listChanged = true;
            }

            if (listChanged) {
                if (!Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons() || toggleAvailableBoatsToBoats.isSelected()) {
                    boatsAvailableList.setBoatStatusData(boatStatus.getBoats(BoatStatusRecord.STATUS_AVAILABLE, true), logbook, "<" + International.getString("anderes Boot") + ">");
                } else {
                    Persons persons = boatStatus.getProject().getPersons(false);
                    personsAvailableList.setPersonStatusData(persons.getAllPersons(System.currentTimeMillis(), false, false), "<" + International.getString("andere Person") + ">");
                }

                boatsOnTheWaterList.setBoatStatusData(boatStatus.getBoats(BoatStatusRecord.STATUS_ONTHEWATER, true), logbook, null);
                boatsNotAvailableList.setBoatStatusData(boatStatus.getBoats(BoatStatusRecord.STATUS_NOTAVAILABLE, true), logbook, null);
            }
        }

        /*
        Dimension dim = boatsAvailableScrollPane.getSize();
        boatsAvailableScrollPane.setPreferredSize(dim); // to make sure boatsAvailableScrollPane is not resized when toggled between persons and boats
        boatsAvailableScrollPane.setSize(dim);          // to make sure boatsAvailableScrollPane is not resized when toggled between persons and boats
        */

        if (toggleAvailableBoatsToBoats.isSelected()) {
            statusLabelSetText(International.getString("Kein Boot ausgewählt."));
        } else {
            statusLabelSetText(International.getString("Keine Person ausgewählt."));
        }
        boatsAvailableList.clearSelection();
        personsAvailableList.clearSelection();
        boatsOnTheWaterList.clearSelection();
        boatsNotAvailableList.clearSelection();
        if (boatsAvailableList.isFocusOwner()) {
            boatsAvailableList.setSelectedIndex(0);
        } else if (personsAvailableList.isFocusOwner()) {
            personsAvailableList.setSelectedIndex(0);
        } else if (boatsOnTheWaterList.isFocusOwner()) {
            boatsOnTheWaterList.setSelectedIndex(0);
        } else if (boatsNotAvailableList.isFocusOwner()) {
            boatsNotAvailableList.setSelectedIndex(0);
        } else {
            if (toggleAvailableBoatsToBoats.isSelected()) {
                boatsAvailableList.requestFocus();
                boatsAvailableList.setSelectedIndex(0);
            } else {
                personsAvailableList.requestFocus();
                personsAvailableList.setSelectedIndex(0);
            }

        }
    }


    // ========================================================================================================================================
    // Callbacks and Events
    // ========================================================================================================================================
    public void setUnreadMessages(boolean admin, boolean boatmaintenance) {
        String iconName = "action_admin.png";
        if (admin && boatmaintenance) {
            iconName = "action_admin_mailAdminBoat.png";
        } else if (admin) {
            iconName = "action_admin_mailAdmin.png";
        } else if (boatmaintenance) {
            iconName = "action_admin_mailBoat.png";
        }
        adminButton.setIcon(getIcon(iconName));
    }

    public synchronized void exitOnLowMemory(String detector, boolean immediate) {
        largeChunkOfMemory = null;
        Logger.log(Logger.ERROR, Logger.MSG_ERR_EXITLOWMEMORY,
                International.getMessage("Der Arbeitsspeicher wird knapp [{detector}]: "
                + "efa versucht {jetzt} einen Neustart ...", detector,
                (immediate ? International.getString("jetzt").toUpperCase() : International.getString("jetzt"))));
        if (immediate) {
            this.cancel(null, EFA_EXIT_REASON_OOME, null, true);
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
                int subCmd = EfaUtil.stringFindInt(ae.getActionCommand(), -1);
                if (subCmd >= 0) {
                    ItemTypeBoatstatusList.BoatListItem blitem = list.getSelectedBoatListItem();
                    if (blitem != null) {
                        processListAction((blitem.boatStatus != null ? blitem.boatStatus : blitem.person), subCmd);
                    }
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
                actionStartSession();
                break;
            case 2: // finish session
                actionFinishSession();
                break;
            case 3: // late entry
                actionLateEntry();
                break;
            case 4: // change session
                actionStartSessionCorrect();
                break;
            case 5: // cancel session
                actionAbortSession();
                break;
            case 6: // boat reservations
                actionBoatReservations();
                break;
        }
    }

    void boatListDoubleClick(int listnr, ItemTypeBoatstatusList list) {
        if (list == null || list.getSelectedIndex() < 0) {
            return;
        }
        clearAllPopups();

        ItemTypeBoatstatusList.BoatListItem blitem = list.getSelectedBoatListItem();
        DataRecord r = null;
        String name = null;
        if (blitem != null) {
            if (blitem.boatStatus != null) {
                r = blitem.boatStatus;
                name = blitem.boatStatus.getBoatNameAsString(System.currentTimeMillis());
            } else if (blitem.person != null) {
                r = blitem.person;
                name = blitem.person.getQualifiedName();
            }
        }
        if (r == null) {
            return;
        }

        if (listnr == 1
                && Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons()
                && toggleAvailableBoatsToPersons.isSelected()) {
            actionStartSession();
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
        if (Daten.project == null) {
            return;
        }
        String name = null;

        ItemTypeBoatstatusList.BoatListItem item = null;
        while (item == null) {
            try {
                item = list.getSelectedBoatListItem();
                if (list != personsAvailableList) {
                    name = item.text;
                } else {
                    name = item.person.getQualifiedName();
                }
            } catch (Exception e) {
            }
            if (name == null || name.startsWith("---")) {
                item = null;
                try {
                    int i = list.getSelectedIndex() + direction;
                    if (i < 0) {
                        i = 1; // i<0 kann nur erreicht werden, wenn vorher i=0 und direction=-1; dann darf nicht auf i=0 gesprungen werden, da wir dort herkommen, sondern auf i=1
                        direction = 1;
                    }
                    if (i >= list.size()) {
                        return;
                    }
                    list.setSelectedIndex(i);
                } catch (Exception e) { /* just to be sure */ }
            }
        }
        if (item == null) {
            return;
        }

        if (list != personsAvailableList) {
            if (item.boatStatus != null) {
                BoatStatusRecord status = boatStatus.getBoatStatus(item.boatStatus.getBoatId());
                BoatRecord boat = Daten.project.getBoats(false).getBoat(item.boatStatus.getBoatId(), System.currentTimeMillis());
                name = (boat != null ? boat.getQualifiedName() :
                    (status != null ? status.getBoatText() : International.getString("anderes oder fremdes Boot")));
                String text = "";
                if (status != null) {
                    String s = status.getStatusDescription(status.getCurrentStatus());
                    if (s != null) {
                        text = s;
                    }
                    s = status.getComment();
                    if (s != null && s.length() > 0) {
                        text = s; // if a comment is set, then *don't* display the current status, but only the comment
                    }
                }
                String bootstyp = "";
                String rudererlaubnis = "";
                if (listnr == 1) {
                    if (boat != null) {
                        bootstyp = " (" + boat.getDetailedBoatType(item.boatVariant) + ")";
                        String groups = boat.getAllowedGroupsAsNameString(System.currentTimeMillis());
                        if (groups.length() > 0) {
                            rudererlaubnis = (rudererlaubnis.length() > 0 ? rudererlaubnis + ", "
                                    : "; " + International.getMessage("nur für {something}", groups));
                        }
                    }
                }
                statusLabelSetText(name + ": " + text + bootstyp + rudererlaubnis);
            } else {
                statusLabelSetText(International.getString("anderes oder fremdes Boot"));
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
        if (!Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons()) {
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
            updateBoatLists(true);
        } catch (Exception ee) {
        }
    }

    private ItemTypeBoatstatusList.BoatListItem getSelectedListItem() {
        ItemTypeBoatstatusList.BoatListItem item = null;
        if (Daten.efaConfig.getValueEfaDirekt_listAllowToggleBoatsPersons() && toggleAvailableBoatsToPersons.isSelected()) {
            if (item == null && personsAvailableList != null) {
                item = personsAvailableList.getSelectedBoatListItem();
            }
        } else {
            if (item == null && boatsAvailableList != null) {
                item = boatsAvailableList.getSelectedBoatListItem();
            }
        }
        if (item == null && boatsOnTheWaterList != null) {
            item = boatsOnTheWaterList.getSelectedBoatListItem();
        }
        if (item == null && boatsNotAvailableList != null) {
            item = boatsNotAvailableList.getSelectedBoatListItem();
        }
        if (item != null && item.boatStatus != null) {
            // update saved boat status in GUI list with current boat status from Persistence
            item.boatStatus = boatStatus.getBoatStatus(item.boatStatus.getBoatId());
            if (item.boatStatus != null) {
                item.boat = item.boatStatus.getBoatRecord(System.currentTimeMillis());
            }
            if (item.boatStatus == null) {
                String s = International.getMessage("Boot {boat} nicht in der Statusliste gefunden!", item.text);
                Dialog.error(s);
                Logger.log(Logger.ERROR, Logger.MSG_ERR_BOATNOTFOUNDINSTATUS, s);
            }
        }

        return item;
    }

    // mode bestimmt die Art der Checks
    // mode==1 - alle Checks durchführen
    // mode==2 - nur solche Checks durchführen, bei denen es egal ist, ob das Boot aus der Liste direkt ausgewählt wurde
    //           oder manuell über <anders Boot> eingegeben wurde. Der Aufruf von checkFahrtbeginnFuerBoot mit mode==2
    //           erfolgt aus EfaFrame.java.
    boolean checkStartSessionForBoat(ItemTypeBoatstatusList.BoatListItem item, int mode) {
        if (item == null || item.boatStatus == null || item.boatStatus.getCurrentStatus() == null) {
            return true;
        }
        if (item.boatStatus.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
            if (mode == 1) {
                actionStartSessionCorrect();
                return false;
            }
            if (mode == 2) {
                Dialog.error(International.getMessage("Das Boot {boat} ist bereits unterwegs.", item.boatStatus.getBoatText()));
                return false;
            }
        }
        if (item.boatStatus.getCurrentStatus().equals(BoatStatusRecord.STATUS_NOTAVAILABLE)) {
            if (Dialog.yesNoCancelDialog(International.getString("Boot gesperrt"),
                    International.getMessage("Das Boot {boat} ist laut Liste nicht verfügbar.", item.boatStatus.getBoatText()) + "\n"
                    + International.getString("Bemerkung") + ": " + item.boatStatus.getComment() + "\n"
                    + "\n"
                    + International.getString("Möchtest Du trotzdem das Boot benutzen?"))
                    != Dialog.YES) {
                return false;
            }
        }


        long now = System.currentTimeMillis();
        BoatReservations boatReservations = Daten.project.getBoatReservations(false);
        BoatReservationRecord[] reservations = (item.boatStatus.getBoatId() != null ? 
            boatReservations.getBoatReservations(item.boatStatus.getBoatId(), now, Daten.efaConfig.getValueEfaDirekt_resLookAheadTime()) : null);
        if (reservations != null && reservations.length > 0) {
            long validInMinutes = reservations[0].getReservationValidInMinutes(now, Daten.efaConfig.getValueEfaDirekt_resLookAheadTime());
            if (Dialog.yesNoCancelDialog(International.getString("Boot reserviert"),
                    International.getMessage("Das Boot {boat} ist {currently_or_in_x_minutes} für {name} reserviert.",
                    item.boatStatus.getBoatText(),
                    (validInMinutes == 0
                    ? International.getString("zur Zeit")
                    : International.getMessage("in {x} Minuten", (int) validInMinutes)),
                    reservations[0].getPersonAsName()) + "\n"
                    + (reservations[0].getReason().length() > 0 ? " (" + International.getString("Grund") + ": " + reservations[0].getReason() + ")\n" : "")
                    + International.getMessage("Die Reservierung liegt {from_time_to_time} vor.", reservations[0].getReservationTimeDescription()) + "\n"
                    + International.getString("Möchtest Du trotzdem das Boot benutzen?"))
                    != Dialog.YES) {
                return false;
            }
        }

        BoatDamages boatDamages = Daten.project.getBoatDamages(false);
        BoatDamageRecord[] damages = (item.boatStatus.getBoatId() != null ?
            boatDamages.getBoatDamages(item.boatStatus.getBoatId(), true, true) : null);
        if (damages != null && damages.length > 0) {
            if (Dialog.yesNoDialog(International.getString("Bootsschaden gemeldet"),
                    International.getMessage("Für das Boot {boat} wurde folgender Bootsschaden gemeldet:", item.boatStatus.getBoatText()) + "\n"
                    + "\""
                    + damages[0].getDescription()
                    + "\"\n"
                    + International.getString("Schwere des Schadens") + ": " + damages[0].getSeverityDescription()
                    + "\n\n"
                    + International.getString("Möchtest Du trotzdem das Boot benutzen?"))
                    != Dialog.YES) {
                return false;
            }

        }

        return true;
    }

    boolean checkBoatStatusOnTheWater(ItemTypeBoatstatusList.BoatListItem item) {
        if (item == null || item.boatStatus == null || item.boatStatus.getCurrentStatus() == null
                || !item.boatStatus.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
            item = null;
        }

        if (item == null) {
            Dialog.error(International.getMessage("Bitte wähle zuerst {from_the_right_list} ein Boot aus, welches unterwegs ist!",
                    (Daten.efaConfig.getValueEfaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar()
                    ? International.getString("aus einer der rechten Listen")
                    : International.getString("aus der rechten oberen Liste"))));
            boatsAvailableList.requestFocus();
            efaBoathouseBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return false;
        }

        if (item.boatStatus.getEntryNo() == null || !item.boatStatus.getEntryNo().isSet()) {
            // keine LfdNr eingetragen: Das kann passieren, wenn der Admin den Status der Bootes manuell geändert hat!
            String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat}.", item.boatStatus.getBoatText())
                    + " " + International.getString("Die Fahrt kann nicht beendet werden.");
            Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT,
                    s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
            Dialog.error(s);
            return false;
        }

        if (logbook.getLogbookRecord(item.boatStatus.getEntryNo()) == null) {
            String s = International.getMessage("Es gibt keine offene Fahrt im Fahrtenbuch mit dem Boot {boat} und LfdNr {lfdnr}.",
                    item.boatStatus.getBoatText(), (item.boatStatus.getEntryNo() != null ? item.boatStatus.getEntryNo().toString() : "null"))
                    + " " + International.getString("Die Fahrt kann nicht beendet werden.");
            Logger.log(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT,
                    s + " " + International.getString("Bitte korrigiere den Status des Bootes im Admin-Modus."));
            Dialog.error(s);
            return false;
        }

        return true;
    }


    void showEfaBaseFrame(int mode, ItemTypeBoatstatusList.BoatListItem action) {
        if (efaBaseFrame == null) {
            efaBaseFrame = new EfaBaseFrame(this, EfaBaseFrame.MODE_BOATHOUSE);
            efaBaseFrame.prepareDialog();
            efaBaseFrame.setFixedLocationAndSize();
        }
        action.mode = mode;
        if (!efaBaseFrame.setDataForBoathouseAction(action, logbook)) {
            return;
        }
        if (mode != EfaBaseFrame.MODE_BOATHOUSE_ABORT) {
            efaBaseFrame.efaBoathouseShowEfaFrame();
        } else {
            efaBaseFrame.finishBoathouseAction(true);
        }
    }

    // Callback from EfaBaseFrame
    void showEfaBoathouseFrame() {
        bringFrameToFront();
        updateBoatLists(true); // must be explicitly called here! only efaBoathouseBackgroundTask.interrupt() is NOT sufficient.
        efaBoathouseBackgroundTask.interrupt();
        if (focusItem != null) {
            focusItem.requestFocus();
        }
        alive();
    }

    void actionStartSession() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        ItemTypeBoatstatusList.BoatListItem item = getSelectedListItem();
        if (item == null) {
            Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
            boatListRequestFocus(1);
            efaBoathouseBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }

        if (!checkStartSessionForBoat(item, 1)) {
            return;
        }

        showEfaBaseFrame(EfaBaseFrame.MODE_BOATHOUSE_START, item);
    }

    void actionStartSessionCorrect() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        ItemTypeBoatstatusList.BoatListItem item = getSelectedListItem();
        if (!checkBoatStatusOnTheWater(item)) {
            return;
        }

        showEfaBaseFrame(EfaBaseFrame.MODE_BOATHOUSE_START_CORRECT, item);
    }


    void actionFinishSession() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        ItemTypeBoatstatusList.BoatListItem item = getSelectedListItem();
        if (!checkBoatStatusOnTheWater(item)) {
            return;
        }

        showEfaBaseFrame(EfaBaseFrame.MODE_BOATHOUSE_FINISH, item);
    }

    void actionAbortSession() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        ItemTypeBoatstatusList.BoatListItem item = getSelectedListItem();
        if (!checkBoatStatusOnTheWater(item)) {
            return;
        }

        BoatRecord boat = item.boat;

        /*
        if (Dialog.yesNoDialog(International.getString("Fahrt abbrechen"),
                International.getMessage("Die Fahrt des Bootes {boat} sollte nur abgebrochen werden, "
                + "wenn sie nie stattgefunden hat. In diesem Fall wird der begonnene Eintrag wieder entfernt.",
                item.boatStatus.getBoatText())
                + "\n"
                + International.getString("Möchtest Du die Fahrt wirklich abbrechen?")) != Dialog.YES) {
            return;
        }
        */
        switch (Dialog.auswahlDialog(International.getString("Fahrt abbrechen"),
                International.getMessage("Die Fahrt des Bootes {boat} sollte nur abgebrochen werden, "
                + "wenn sie nie stattgefunden hat.",
                item.boatStatus.getBoatText())
                + "\n"
                + International.getString("Was möchtest Du tun?"),
                International.getString("Fahrt abbrechen"),
                International.getString("Fahrt abbrechen") +
                " (" + International.getString("Bootsschaden") + ")",
                International.getString("Nichts")
                )) {
            case 0:
                break;
            case 1:
                if (boat != null) {
                    BoatDamageEditDialog.newBoatDamage(this, boat);
                }
                break;
            case 2:
                return;
        }

        showEfaBaseFrame(EfaBaseFrame.MODE_BOATHOUSE_ABORT, item);
    }

    void actionLateEntry() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        ItemTypeBoatstatusList.BoatListItem item = getSelectedListItem();
        if (item == null) {
            Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
            boatListRequestFocus(1);
            efaBoathouseBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }

        showEfaBaseFrame(EfaBaseFrame.MODE_BOATHOUSE_LATEENTRY, item);
    }

    void actionBoatReservations() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        ItemTypeBoatstatusList.BoatListItem item = getSelectedListItem();
        if (item == null) {
            Dialog.error(International.getString("Bitte wähle zuerst ein Boot aus!"));
            boatListRequestFocus(1);
            efaBoathouseBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }
        if (item.boat == null || item.boatStatus == null || item.boatStatus.getUnknownBoat() || item.boatStatus.getBoatId() == null) {
            // Dialog.error(International.getString("Dieses Boot kann nicht reserviert werden!"));
            // boatListRequestFocus(1);
            BoatReservationListDialog dlg = new BoatReservationListDialog(this, null,
                Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservieren(),
                Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservierenZyklisch(),
                Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservierungenEditieren());
            dlg.showDialog();
            efaBoathouseBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
            return;
        }

        BoatReservationListDialog dlg = new BoatReservationListDialog(this, item.boatStatus.getBoatId(),
                Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservieren(),
                Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservierenZyklisch(),
                Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenReservierungenEditieren());
        dlg.showDialog();
        efaBoathouseBackgroundTask.interrupt();
    }

    void actionShowLogbook() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }
        ShowLogbookDialog dlg = new ShowLogbookDialog(this, logbook);
        dlg.showDialog();
    }

    void actionStatistics() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }
        StatisticsListDialog dlg = new StatisticsListDialog(this, null);
        dlg.showDialog();
    }

    void actionMessageToAdmin() {
        alive();
        clearAllPopups();
        if (Daten.project == null) {
            return;
        }

        MessageRecord msg = null;
        try {
            msg = Daten.project.getMessages(false).createMessageRecord();
            msg.setTo(Daten.efaConfig.getValueEfaDirekt_bnrMsgToAdminDefaultRecipient());
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        if (msg != null) {
            MessageEditDialog dlg = new MessageEditDialog(this, msg, true, null);
            dlg.showDialog();
            efaBoathouseBackgroundTask.interrupt();
        }
    }

    void actionAdminMode() {
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
        dlg.showDialog();
        efaBoathouseBackgroundTask.interrupt();
        updateBoatLists(true);
        updateGuiElements();
    }

    void actionSpecial() {
        Dialog.infoDialog("Not yet implemented");
        /* @todo (P4) actionSpecial()
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
        Help.showHelp(getHelpTopics());
    }

    public void lockEfaAt(DataTypeDate date, DataTypeTime time) {
        this.efaBoathouseBackgroundTask.setEfaLockBegin(date, time);
    }

    public void lockEfa() {
        if (Daten.efaConfig == null) {
            return;
        }

        String endeDerSperrung = (Daten.efaConfig.getValueEfaDirekt_lockEfaUntilDatum().isSet() ? " " + International.getString("Ende der Sperrung") + ": "
                + Daten.efaConfig.getValueEfaDirekt_lockEfaUntilDatum().toString()
                + (Daten.efaConfig.getValueEfaDirekt_lockEfaUntilZeit().isSet() ? " " + Daten.efaConfig.getValueEfaDirekt_lockEfaUntilZeit().toString() : "") : "");

        String html = Daten.efaConfig.getValueEfaDirekt_lockEfaShowHtml();
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
        de.nmichael.efa.core.BrowserFrame browser = null; // @todo (P4) new BrowserFrame(this, Daten.efaConfig.efaDirekt_lockEfaVollbild.getValue(), "file:" + html);
        browser.setModal(true);
        if (Daten.efaConfig.getValueEfaDirekt_lockEfaVollbild()) {
            browser.setSize(Dialog.screenSize);
        }
        Dialog.setDlgLocation(browser, this);
        browser.setClosingTimeout(10); // nur um Lock-Ende zu überwachen
        Logger.log(Logger.INFO, Logger.MSG_EVT_LOCKED,
                International.getString("efa wurde vom Administrator vorübergehend für die Benutzung gesperrt.") + endeDerSperrung);
        Daten.efaConfig.setValueEfaDirekt_lockEfaFromDatum(new DataTypeDate()); // damit nach Entsperren nicht wiederholt gelockt wird
        Daten.efaConfig.setValueEfaDirekt_lockEfaFromZeit(new DataTypeTime());  // damit nach Entsperren nicht wiederholt gelockt wird
        Daten.efaConfig.setValueEfaDirekt_locked(true);
        browser.show(); // @todo (P4) implement new BrowserDialog and replace show() by showDialog()
    }

}
