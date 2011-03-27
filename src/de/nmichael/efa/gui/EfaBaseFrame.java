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
import de.nmichael.efa.core.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import java.beans.*;

public class EfaBaseFrame extends BaseFrame implements IItemListener {

    public static final int MODE_BASE = 0;
    public static final int MODE_BOATHOUSE = 1;
    public static final int MODE_BOATHOUSE_START = 2;
    public static final int MODE_BOATHOUSE_START_CORRECT = 3;
    public static final int MODE_BOATHOUSE_FINISH = 4;
    public static final int MODE_BOATHOUSE_LATEENTRY = 5;
    public static final int MODE_ADMIN = 6;
    public static final int MODE_ADMIN_SESSIONS = 7;
    private int mode;

    // =========================================================================
    // GUI Elements
    // =========================================================================

    // Menu Bar
    JMenuBar menuBar = new JMenuBar();
    JMenu menuFile = new JMenu();
    JMenuItem menuFile_newProject = new JMenuItem();
    JMenuItem menuFile_openProject = new JMenuItem();
    JMenuItem menuFile_logbooks = new JMenuItem();
    JMenuItem menuFile_exit = new JMenuItem();
    JMenu menuHelp = new JMenu();
    JMenuItem menuHelp_efaConsole = new JMenuItem();
    JMenuItem menuHelp_about = new JMenuItem();
    JMenu menuAdministration = new JMenu();
    JMenu menuStatistics = new JMenu();
    JMenuItem menuStatistics_createStatistics = new JMenuItem();

    // Toolbar
    JToolBar toolBar = new JToolBar();
    JButton toolBar_firstButton = new JButton();
    JButton toolBar_prevButton = new JButton();
    JButton toolBar_nextButton = new JButton();
    JButton toolBar_lastButton = new JButton();
    JButton toolBar_newButton = new JButton();
    JButton toolBar_insertButton = new JButton();
    JButton toolBar_deleteButton = new JButton();
    JButton toolBar_searchButton = new JButton();
    JTextField toolBar_goToEntry = new JTextField();

    // Data Fields
    ItemTypeString entryno;
    ItemTypeDate date;
    ItemTypeStringAutoComplete boat;
    ItemTypeStringAutoComplete cox;
    ItemTypeStringAutoComplete[] crew;
    ItemTypeTime starttime;
    ItemTypeTime endtime;
    ItemTypeStringAutoComplete destination;
    ItemTypeDistance distance;
    ItemTypeString comments;
    ItemTypeStringList sessiontype;
    ItemTypeStringList boatcaptain;

    // Supplementary Elements
    ItemTypeButton remainingCrewUpButton;
    ItemTypeButton remainingCrewDownButton;
    ItemTypeButton boatDamageButton;
    ItemTypeButton saveButton;
    JLabel infoLabel = new JLabel();

    // Internal Data Structures
    Logbook logbook;                // this logbook
    DataKeyIterator iterator;       // iterator for this logbook
    LogbookRecord currentRecord;    // aktDatensatz = aktuell angezeigter Datensatz
    LogbookRecord referenceRecord;  // refDatensatz = Referenz-Datensatz (zuletzt angezeigter Datensatz, wenn neuer erstellt wird)
    long logbookValidFrom = 0;
    long logbookInvalidFrom = 0;
    boolean isNewRecord;            // neuerDatensatz = ob akt. Datensatz ein neuer Datensatz, oder ein bearbeiteter ist
    boolean isInsertedRecord;       // neuerDatensatz_einf = ob der neue Datensatz eingefügt wird (dann beim Hinzufügen keine Warnung wegen kleiner Lfd. Nr.!)
    int entryNoForNewEntry = -1;    // lfdNrForNewEntry = LfdNr (zzgl. 1), die für den nächsten per "Neu" erzeugten Datensatz verwendet werden soll; wenn <0, dann wird "last+1" verwendet
    BoatRecord currentBoat;         // aktBoot = aktuelle Bootsdaten (um nächstes Eingabefeld zu ermitteln)
    int crewRangeSelection = 0;     // mannschAuswahl = 0: 1-8 sichtbar; 1: 9-16 sichtbar; 2: 17-24 sichtbar
    IItemType lastFocusedItem;
    AutoCompleteList autoCompleteListBoats = new AutoCompleteList();
    AutoCompleteList autoCompleteListPersons = new AutoCompleteList();
    AutoCompleteList autoCompleteListDestinations = new AutoCompleteList();


    public EfaBaseFrame(int mode) {
        super(null, Daten.EFA_LONGNAME);
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isModeFull() {
        return getMode() == MODE_BASE ||
               getMode() == MODE_ADMIN;
    }

    public boolean isModeBoathouse() {
        return getMode() == MODE_BOATHOUSE ||
               getMode() == MODE_BOATHOUSE_START ||
               getMode() == MODE_BOATHOUSE_START_CORRECT ||
               getMode() == MODE_BOATHOUSE_FINISH ||
               getMode() == MODE_BOATHOUSE_LATEENTRY;
    }

    public boolean isModeAdmin() {
        return getMode() == MODE_ADMIN ||
               getMode() == MODE_ADMIN_SESSIONS;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() {
        iniGuiBase();
        iniGuiMenu();
        if (isModeFull()) {
            iniGuiToolbar();
        }
        iniGuiMain();
        iniApplication();
    }




    private void iniGuiBase() {
        setIconImage(Toolkit.getDefaultToolkit().createImage(EfaBaseFrame.class.getResource("/de/nmichael/efa/img/efa_icon.gif")));
        mainPanel.setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
            public void windowIconified(WindowEvent e) {
                this_windowIconified(e);
            }
        });
    }




    private void iniGuiMenu() {
        // Menu: File
        Mnemonics.setButton(this, menuFile, International.getStringWithMnemonic("Datei"));

        Mnemonics.setMenuButton(this, menuFile_newProject, International.getStringWithMnemonic("Neues Projekt"));
        setIcon(menuFile_newProject, getIcon("menu_new.gif"));
        menuFile_newProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuFile_newProject_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuFile_openProject, International.getStringWithMnemonic("Projekt öffnen"));
        setIcon(menuFile_openProject, getIcon("menu_open.gif"));
        menuFile_openProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuFile_openProject_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuFile_logbooks, International.getStringWithMnemonic("Fahrtenbücher") + " ...");
        setIcon(menuFile_logbooks, getIcon("menu_files.gif"));
        menuFile_logbooks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuFile_logbooks_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuFile_exit, International.getStringWithMnemonic("Programm beenden"));
        setIcon(menuFile_exit, getIcon("menu_exit.gif"));
        menuFile_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // jMenuFileExit_actionPerformed(e);
            }
        });

        // Menu: Administration
        Mnemonics.setMenuButton(this, menuAdministration, International.getStringWithMnemonic("Administration"));

        // Menu: Statistics
        Mnemonics.setButton(this, menuStatistics, International.getStringWithMnemonic("Ausgabe"));

        Mnemonics.setMenuButton(this, menuStatistics_createStatistics, International.getStringWithMnemonic("Statistiken und Meldedateien erstellen"));
        setIcon(menuStatistics_createStatistics, getIcon("menu_stat.gif"));
        menuStatistics_createStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // jMenuItemKilometerliste_actionPerformed(e);
            }
        });

        // Menu: Help
        Mnemonics.setButton(this, menuHelp, International.getStringWithMnemonic("Info"));

        Mnemonics.setMenuButton(this, menuHelp_about, International.getStringWithMnemonic("Über"));
        setIcon(menuHelp_about, getIcon("menu_about.gif"));
        menuHelp_about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // jMenuHelpAbout_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuHelp_efaConsole, International.getStringWithMnemonic("efa-Konsole"));
        setIcon(menuHelp_efaConsole, getIcon("menu_konsole.gif"));
        menuHelp_efaConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // jMenuHilfeJavaKonsole_actionPerformed(e);
            }
        });

        // Menu: File
        menuFile.add(menuFile_newProject);
        menuFile.add(menuFile_openProject);
        menuFile.addSeparator();
        menuFile.add(menuFile_logbooks);
        menuFile.addSeparator();
        menuFile.add(menuFile_exit);

        // Menu: Administration

        // Menu: Statistics
        menuStatistics.add(menuStatistics_createStatistics);

        // Menu: Help
        menuHelp.add(menuHelp_efaConsole);
        menuHelp.addSeparator();
        menuHelp.add(menuHelp_about);

        // Menu Bar
        menuBar.add(menuFile);
        menuBar.add(menuAdministration);
        menuBar.add(menuStatistics);
        menuBar.add(menuHelp);
        this.setJMenuBar(menuBar);
    }




    private void iniGuiToolbar() {
        toolBar_firstButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_firstButton, International.getStringWithMnemonic("Erster"));
        toolBar_firstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateInLogbook(Integer.MIN_VALUE);
            }
        });

        toolBar_prevButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_prevButton, "<< " + International.getStringWithMnemonic("Vorheriger"));
        toolBar_prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateInLogbook(-1);
            }
        });

        toolBar_nextButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_nextButton, International.getStringWithMnemonic("Nächster") + " >>");
        toolBar_nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateInLogbook(1);
            }
        });

        toolBar_lastButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_lastButton, International.getStringWithMnemonic("Letzter"));
        toolBar_lastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateInLogbook(Integer.MAX_VALUE);
            }
        });

        toolBar_newButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_newButton, International.getStringWithMnemonic("Neu"));
        toolBar_newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // NewButton_actionPerformed(e);
            }
        });

        toolBar_insertButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_insertButton, International.getStringWithMnemonic("Einfügen"));
        toolBar_insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // InsertButton_actionPerformed(e);
            }
        });

        toolBar_deleteButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_deleteButton, International.getStringWithMnemonic("Löschen"));
        toolBar_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ButtonDelete_actionPerformed(e);
            }
        });

        toolBar_searchButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_searchButton, International.getStringWithMnemonic("Suchen"));
        toolBar_searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // SuchButton_actionPerformed(e);
            }
        });

        Dialog.setPreferredSize(toolBar_goToEntry, 30, 19);
        toolBar_goToEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                goToEntry(toolBar_goToEntry.getText().trim());
            }
        });
        toolBar_goToEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) {
                toolBar_goToEntry.setText("");
            }
        });
        

        toolBar.add(toolBar_firstButton, null);
        toolBar.add(toolBar_prevButton, null);
        toolBar.add(toolBar_nextButton, null);
        toolBar.add(toolBar_lastButton, null);
        JLabel toolBar_spaceLabel1 = new JLabel();
        toolBar_spaceLabel1.setText("  ");
        toolBar.add(toolBar_spaceLabel1, null);
        toolBar.add(toolBar_newButton, null);
        toolBar.add(toolBar_insertButton, null);
        toolBar.add(toolBar_deleteButton, null);
        JLabel toolBar_spaceLabel2 = new JLabel();
        toolBar_spaceLabel2.setText("  ");
        toolBar.add(toolBar_spaceLabel2, null);
        toolBar.add(toolBar_searchButton, null);
        JLabel toolBar_goToEntryLabel = new JLabel();
        toolBar_goToEntryLabel.setText("  \u21B7 "); // \u00BB \u23E9
        toolBar.add(toolBar_goToEntryLabel, null);
        toolBar.add(toolBar_goToEntry, null);
        mainPanel.add(toolBar, BorderLayout.NORTH);
    }




    private void iniGuiMain() {
        JPanel mainInputPanel = new JPanel();
        mainInputPanel.setLayout(new GridBagLayout());
        mainPanel.add(mainInputPanel, BorderLayout.CENTER);

        // EntryNo
        entryno = new ItemTypeString(LogbookRecord.ENTRYID, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Lfd. Nr."));
        entryno.setAllowedRegex("[0-9]+[A-Z]?");
        entryno.setToUpperCase(true);
        entryno.setNotNull(true);
        entryno.setFieldSize(200, 19);
        entryno.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        entryno.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        entryno.displayOnGui(this, mainInputPanel, 0, 0);
        entryno.registerItemListener(this);

        // Date
        date = new ItemTypeDate(LogbookRecord.DATE, new DataTypeDate(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Datum"));
        date.showWeekday(true);
        date.setFieldSize(100, 19);
        date.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        date.setFieldGrid(1, GridBagConstraints.WEST, GridBagConstraints.NONE);
        date.setWeekdayGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        date.displayOnGui(this, mainInputPanel, 0, 1);
        date.registerItemListener(this);

        // Boat
        boat = new ItemTypeStringAutoComplete(LogbookRecord.BOATNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Boot"), true);
        boat.setFieldSize(200, 19);
        boat.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boat.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        boat.setAutoCompleteData(autoCompleteListBoats);
        boat.displayOnGui(this, mainInputPanel, 0, 2);
        boat.registerItemListener(this);

        // Cox
        cox = new ItemTypeStringAutoComplete(LogbookRecord.COXNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Steuermann"), true);
        cox.setFieldSize(200, 19);
        cox.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        cox.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        cox.setAutoCompleteData(autoCompleteListPersons);
        cox.displayOnGui(this, mainInputPanel, 0, 3);
        cox.registerItemListener(this);

        // Crew
        crew = new ItemTypeStringAutoComplete[LogbookRecord.CREW_MAX];
        for (int i=1; i<=crew.length; i++) {
            int j = i-1;
            boolean left = ((j/4) % 2) == 0;
            crew[j] = new ItemTypeStringAutoComplete(LogbookRecord.getCrewFieldNameName(i), "", IItemType.TYPE_PUBLIC, null,
                    (i == 1 ? International.getString("Mannschaft") + " " : (i < 10 ? "  " :"")) + Integer.toString(i), true);
            crew[j].setPadding( (left ? 0 : 10), 0, 0, 0);
            crew[j].setFieldSize(200, 19);
            crew[j].setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
            crew[j].setFieldGrid((left ? 2 : 3), GridBagConstraints.WEST, GridBagConstraints.NONE);
            crew[j].setAutoCompleteData(autoCompleteListPersons);
            crew[j].displayOnGui(this, mainInputPanel, (left ? 0 : 4), 4 + j%4);
            crew[j].setVisible(j < 8);
            crew[j].registerItemListener(this);
        }

        // StartTime
        starttime = new ItemTypeTime(LogbookRecord.STARTTIME, new DataTypeTime(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Abfahrt"));
        starttime.setFieldSize(200, 19);
        starttime.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        starttime.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        starttime.enableSeconds(false);
        starttime.displayOnGui(this, mainInputPanel, 0, 8);
        starttime.registerItemListener(this);

        // EndTime
        endtime = new ItemTypeTime(LogbookRecord.ENDTIME, new DataTypeTime(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Ankunft"));
        endtime.setFieldSize(200, 19);
        endtime.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        endtime.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        endtime.enableSeconds(false);
        endtime.displayOnGui(this, mainInputPanel, 0, 9);
        endtime.registerItemListener(this);

        // Destination
        destination = new ItemTypeStringAutoComplete(LogbookRecord.DESTINATIONNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Ziel"), true);
        destination.setFieldSize(400, 19);
        destination.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        destination.setFieldGrid(7, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        destination.setAutoCompleteData(autoCompleteListDestinations);
        destination.displayOnGui(this, mainInputPanel, 0, 10);
        destination.registerItemListener(this);

        // Distance
        distance = new ItemTypeDistance(LogbookRecord.DISTANCE, null, IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Kilometer"));
        distance.setFieldSize(200, 19);
        distance.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        distance.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        distance.displayOnGui(this, mainInputPanel, 0, 11);
        distance.registerItemListener(this);

        // Comments
        comments = new ItemTypeString(LogbookRecord.COMMENTS, null, IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Bemerkungen"));
        comments.setFieldSize(400, 19);
        comments.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        comments.setFieldGrid(7, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        comments.displayOnGui(this, mainInputPanel, 0, 12);
        comments.registerItemListener(this);

        // Session Type
        sessiontype = new ItemTypeStringList(LogbookRecord.SESSIONTYPE, EfaTypes.TYPE_SESSION_NORMAL,
                EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, null, International.getString("Fahrtart"));
        sessiontype.setFieldSize(200, 19);
        sessiontype.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        sessiontype.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        sessiontype.displayOnGui(this, mainInputPanel, 0, 13);
        sessiontype.registerItemListener(this);

        // Boat Captain
        String[] _bcValues = new String[LogbookRecord.CREW_MAX + 2];
        _bcValues[0] = "";
        for (int i=0; i<=LogbookRecord.CREW_MAX; i++) {
            _bcValues[i+1] = Integer.toString(i);
        }
        String[] _bcNames = new String[LogbookRecord.CREW_MAX + 2];
        _bcNames[0] = International.getString("keine Angabe");
        for (int i=0; i<=LogbookRecord.CREW_MAX; i++) {
            _bcNames[i+1] = (i == 0 ? International.getString("Steuermann") :
                International.getString("Nummer") + " " + Integer.toString(i));
        }
        boatcaptain = new ItemTypeStringList(LogbookRecord.BOATCAPTAIN, "",
                _bcValues, _bcNames,
                IItemType.TYPE_PUBLIC, null, International.getString("Obmann"));
        boatcaptain.setFieldSize(80, 19);
        boatcaptain.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boatcaptain.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        boatcaptain.displayOnGui(this, mainInputPanel, 5, 2);
        boatcaptain.registerItemListener(this);


        // Further Fields which are not part of Data Input

        // Remaining Crew Button
        remainingCrewUpButton = new ItemTypeButton("REMAININGCREWUP", IItemType.TYPE_PUBLIC, null, "\u2191");
        remainingCrewUpButton.setFieldSize(18, 30);
        remainingCrewUpButton.setPadding(5, 0, 3, 3);
        remainingCrewUpButton.setFieldGrid(1, 2, GridBagConstraints.WEST, GridBagConstraints.VERTICAL);
        remainingCrewUpButton.displayOnGui(this, mainInputPanel, 9, 4);
        remainingCrewUpButton.registerItemListener(this);
        remainingCrewDownButton = new ItemTypeButton("REMAININGCREWDOWN", IItemType.TYPE_PUBLIC, null, "\u2193");
        remainingCrewDownButton.setFieldSize(18, 30);
        remainingCrewDownButton.setPadding(5, 0, 3, 3);
        remainingCrewDownButton.setFieldGrid(1, 2, GridBagConstraints.WEST, GridBagConstraints.VERTICAL);
        remainingCrewDownButton.displayOnGui(this, mainInputPanel, 9, 6);
        remainingCrewDownButton.registerItemListener(this);

        // Info Label
        infoLabel.setForeground(Color.blue);
        infoLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        infoLabel.setText("infoLabel");
        mainInputPanel.add(infoLabel,
                new GridBagConstraints(0, 15, 8, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 20, 10, 20), 0, 0));

        // Boat Damage Button
        boatDamageButton = new ItemTypeButton("BOATDAMAGE", IItemType.TYPE_PUBLIC, null, International.getString("Bootsschaden melden"));
        boatDamageButton.setFieldSize(200, 19);
        boatDamageButton.setFieldGrid(4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        boatDamageButton.displayOnGui(this, mainInputPanel, 4, 16);
        boatDamageButton.registerItemListener(this);

        // Save Button
        saveButton = new ItemTypeButton("SAVE", IItemType.TYPE_PUBLIC, null, International.getString("Eintrag speichern"));
        saveButton.displayOnGui(this, mainPanel, BorderLayout.SOUTH);
        saveButton.registerItemListener(this);

/*

        // Boat
        boot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) {
                boot_focusLost(e);
            }
        });
        bootButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) {
                bootButton_focusLost(e);
            }
        });

        // Destination
        ziel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) {
                ziel_focusLost(e);
            }
            public void focusGained(FocusEvent e) {
                ziel_focusGained(e);
            }
        });
        ziel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                ziel_keyReleased(e);
            }
        });
        zielButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) {
                zielButton_focusLost(e);
            }
        });

        // Session Type
        fahrtart.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                fahrtDauer_itemStateChanged(e);
            }
        });

*/
    }



    void iniApplication() {
        if (Daten.project == null) {
            if (isModeBoathouse()) {
                if (Daten.efaConfig.lastProjectEfaBoathouse.getValue().length() > 0) {
                    Project.openProject(Daten.efaConfig.lastProjectEfaBoathouse.getValue());
                }
            } else {
                if (Daten.efaConfig.lastProjectEfaBase.getValue().length() > 0) {
                    Project.openProject(Daten.efaConfig.lastProjectEfaBase.getValue());
                }
            }
        }
        if (Daten.project != null && Daten.project.getCurrentLogbookEfaBase() != null) {
            openLogbook(Daten.project.getCurrentLogbookEfaBase());
        }
    }




    void setTitle() {
        if (isModeBoathouse()) {
            setTitle(Daten.EFA_LONGNAME);
        } else {
            if (Daten.project == null) {
                setTitle(Daten.EFA_LONGNAME);
            } else {
                if (!isLogbookReady()) {
                    setTitle(Daten.project.getProjectName() + " - " + Daten.EFA_LONGNAME);
                } else {
                    setTitle(Daten.project.getProjectName() + ": " + logbook.getName() + " - " + Daten.EFA_LONGNAME);
                }
            }
        }
    }

    // =========================================================================
    // Data-related methods
    // =========================================================================

    long getValidAtTimestamp(LogbookRecord r) {
        long t = 0;
        if (r != null) {
            t = r.getValidAtTimestamp();
        }
        if (t == 0) {
            t = logbookValidFrom;
        }
        if (t == 0) {
            t = System.currentTimeMillis();
        }
        return t;
    }

    void openLogbook(String logbookName) {
        if (Daten.project == null || logbookName == null || logbookName.length() == 0) {
            return;
        }
        try {
            if (logbook != null && logbook.isOpen()) {
                logbook.close();
            }
        } catch (Exception e) {
            Dialog.error(e.toString());
        }
        logbook = Daten.project.getLogbook(logbookName, false);
        if (logbook != null) {
            if (!isModeBoathouse()) {
                Daten.project.setCurrentLogbookEfaBase(logbookName);
            }
            ProjectRecord pr = Daten.project.getLoogbookRecord(logbookName);
            if (pr != null) {
                logbookValidFrom = pr.getStartDate().getTimestamp(null);
                logbookInvalidFrom = pr.getEndDate().getTimestamp(null) + 24*60*60;
                if (logbookInvalidFrom < logbookValidFrom) {
                    logbookInvalidFrom = logbookValidFrom + 24*60*60;
                }
            }
            try {
                iterator = logbook.data().getDynamicIterator();
                autoCompleteListBoats.setDataAccess(Daten.project.getBoats(false).data(), logbookValidFrom);
                autoCompleteListPersons.setDataAccess(Daten.project.getPersons(false).data(), logbookValidFrom);
                autoCompleteListDestinations.setDataAccess(Daten.project.getDestinations(false).data(), logbookValidFrom);
            } catch(Exception e) {
                iterator = null;
            }
            if (isModeFull()) {
                try {
                    LogbookRecord r = (LogbookRecord)logbook.data().getLast();
                    setFields(r);
                } catch(Exception e) {
                    setFields(null);
                }
            }
        } else {
            Dialog.error(International.getMessage("Fahrtenbuch {logbook} konnte nicht geöffnet werden.", logbookName));
        }
        setTitle();
    }

    boolean isLogbookReady() {
        return Daten.project != null && Daten.project.isOpen() && logbook != null && logbook.isOpen();
    }

    String getFieldValue(ItemTypeLabelValue field, LogbookRecord r) {
        if (field == entryno) {
            return (r != null ? r.getEntryId().toString() : "1");
        }
        if (field == date) {
            return (r != null ? r.getDate().toString() : EfaUtil.getCurrentTimeStampDD_MM_YYYY());
        }
        if (field == boat) {
            return (r != null ? r.getGuiBoatName(getValidAtTimestamp(r)) : "");
        }
        if (field == cox) {
            return (r != null ? r.getGuiCoxName(getValidAtTimestamp(r)) : "");
        }
        for (int i = 0; i < crew.length; i++) {
            if (field == crew[i]) {
                return (r != null ? r.getGuiCrewName(i + 1, getValidAtTimestamp(r)) : "");
            }
        }
        if (field == starttime) {
            return (r != null ? r.getStartTime().toString() : "");
        }
        if (field == endtime) {
            return (r != null ? r.getEndTime().toString() : "");
        }
        if (field == destination) {
            return (r != null ? r.getGuiDestinationName(getValidAtTimestamp(r)) : "");
        }
        if (field == distance) {
            return (r != null ? r.getDistance().toString() : "");
        }
        if (field == comments) {
            return (r != null ? r.getComments() : "");
        }
        if (field == sessiontype) {
            return (r != null ? r.getSessionType() : "");
        }
        if (field == boatcaptain) {
            return (r != null ? (r.getBoatCaptainPosition() >= 0 ? Integer.toString(r.getBoatCaptainPosition()) : "") : "");
        }
        return "";
    }

    void setField(ItemTypeLabelValue field, LogbookRecord r) {
        field.parseAndShowValue(getFieldValue(field, r));
    }

    void setFields(LogbookRecord r) {
        if (!isLogbookReady()) {
            return;
        }
        referenceRecord = currentRecord;
        currentRecord = r;
        if (iterator != null && r != null) {
            iterator.goTo(r.getKey());
        }
        isNewRecord = r == null;
        isInsertedRecord = false;

        setField(entryno,r);
        setField(date,r);
        setField(boat,r);
        setField(cox,r);
        for (int i=0; i<crew.length; i++) {
            setField(crew[i],r);
        }
        setField(starttime,r);
        setField(endtime,r);
        setField(destination,r);
        setField(distance,r);
        setField(comments,r);
        setField(sessiontype,r);
        setField(boatcaptain,r);
        setCrewRangeSelection(0);
        setEntryUnchanged();
        currentBoat = null;
        entryNoForNewEntry = -1; // -1 bedeutet, daß beim nächsten neuen Datensatz die LfdNr "last+1" vorgegeben wird
    }

    LogbookRecord getFields() {
        if (!isLogbookReady()) {
            return null;
        }
        LogbookRecord r = logbook.createLogbookRecord(DataTypeIntString.parseString(entryno.getValue()));
        if (date.isSet()) {
            r.setDate(date.getDate());
        }
        // @todo other fields
        return r;
    }

    // Datensatz speichern
    // liefert "true", wenn erfolgreich
    boolean saveEntry() {
        if (!isLogbookReady()) {
            return false;
        }

        // Eintrag (Km) auf Korrektheit prüfen
        if (distance.getValue().getValueInDefaultUnit() <= 0) {
            if (Dialog.yesNoDialog(International.getString("Warnung"),
                    International.getString("Die Bootskilometer sind 0.") + "\n"
                    + International.getString("Möchtest Du diesen Eintrag wirklich hinzufügen?")) == Dialog.NO) {
                Dialog.infoDialog(International.getString("Information"),
                        International.getString("Eintrag nicht hinzugefügt."));
                distance.requestFocus();
                startBringToFront(false); // efaDirekt im BRC -- Workaround
                return false;
            }
        }

        DataTypeIntString newEntryNo = DataTypeIntString.parseString(entryno.getValue());
        if ((logbook.getLogbookRecord(newEntryNo) != null && (isNewRecord || !newEntryNo.equals(currentRecord.getEntryId())))
                || newEntryNo.length() == 0) {
            Dialog.infoDialog(International.getString("Ungültige laufende Nummer"),
                    International.getString("Diese Laufende Nummer ist bereits vergeben! Jede Laufende "
                    + "Nummer darf nur einmal verwendet werden werden.") + " "
                    + International.getString("Bitte korrigiere die laufende Nummer des Eintrags!") + "\n\n"
                    + International.getString("Hinweis") + ": "
                    + International.getString("Um mehrere Einträge unter 'derselben' Nummer hinzuzufügen, "
                    + "füge einen Buchstaben von A bis Z direkt an die Nummer an!"));
            entryno.requestFocus();
            startBringToFront(false); // efaDirekt im BRC -- Workaround
            return false;
        }

        if (isNewRecord) {
            // erstmal prüfen, ob die Laufende Nummer korrekt ist
            DataTypeIntString highestEntryNo = new DataTypeIntString(" ");
            try {
                LogbookRecord r = (LogbookRecord) (logbook.data().getLast());
                if (r != null) {
                    highestEntryNo = r.getEntryId();
                }
            } catch (Exception e) {
            }
            if (newEntryNo.compareTo(highestEntryNo) <= 0 && !isInsertedRecord) {
                boolean printWarnung = true;
                if (entryNoForNewEntry > 0 && newEntryNo.intValue() == entryNoForNewEntry + 1) {
                    printWarnung = false;
                }
                if (printWarnung && // nur warnen, wenn das erste Mal eine zu kleine LfdNr eingegeben wurde!
                        Dialog.yesNoDialog(International.getString("Warnung"),
                        International.getString("Die Laufende Nummer dieses Eintrags ist kleiner als die des "
                        + "letzten Eintrags. Bist Du sicher, daß Du den Eintrag mit einer kleineren "
                        + "Laufenden Nummer hinzufügen möchtest?")) == Dialog.NO) {
                    Dialog.infoDialog(International.getString("Information"),
                            International.getString("Eintrag nicht hinzugefügt."));
                    entryno.requestFocus();
                    startBringToFront(false); // efaDirekt im BRC -- Workaround
                    return false;
                }
            }
            entryNoForNewEntry = EfaUtil.string2date(entryno.getValue(), 1, 1, 1).tag; // lfdNr merken, nächster Eintrag erhält dann per default diese Nummer + 1
        } else { // geänderter Fahrtenbucheintrag
            if (!currentRecord.getEntryId().equals(entryno.getValue())) {
                if (Dialog.yesNoDialog(International.getString("Warnung"),
                        International.getString("Du hast die Laufende Nummer dieses Eintrags verändert!\n"
                        + "Bist Du sicher, daß die Laufende Nummer geändert werden soll?")) == Dialog.NO) {
                    Dialog.infoDialog(International.getString("Information"),
                            International.getString("Geänderter Eintrag nicht gespeichert."));
                    entryno.requestFocus();
                    startBringToFront(false); // efaDirekt im BRC -- Workaround
                    return false;
                }
            }
        }

        /* @todo
        // Prüfen, ob im Fall einer Mehrtagesfahrt diese im angegebenen Zeitraum der ausgewählten (vorhandenen) Fahrt liegt
        if (fahrtart.getSelectedIndex() >= 0
                && !this.isFahrtDauerMehrtagesfahrtAction(fahrtart.getSelectedItem().toString())) {
            String thisfahrtart = (String) this.fahrtart.getSelectedItem();
            Mehrtagesfahrt m = null;
            if (thisfahrtart != null) {
                m = Daten.fahrtenbuch.getMehrtagesfahrt(thisfahrtart);
            }
            if (m != null) {
                String datum = this.datum.getText();
                if (EfaUtil.secondDateIsAfterFirst(m.ende, datum) || EfaUtil.secondDateIsAfterFirst(datum, m.start)) {
                    Dialog.error(International.getMessage("Das Datum des Fahrtenbucheintrags ({entry}) liegt außerhalb des Zeitraums "
                            + " ({date_from} - {date_to}), der für die ausgewählte Mehrtagesfahrt '{name}' angegeben wurde.",
                            datum, m.start, m.ende, m.name)
                            + "\n\n"
                            + International.getString("Falls in diesem Jahr mehrere Mehrtagesfahrten mit derselben Strecke durchgeführt wurden, "
                            + "so erstelle bitte für jede einzelne Mehrtagesfahrt einen separaten Eintrag in efa. Ansonsten wähle bitte entweder "
                            + "eine Mehrtagesfahrt mit passendem Datum aus oder korrigiere das Datum dieses Eintrages oder der Mehrtagesfahrt."));
                    startBringToFront(false); // efaDirekt im BRC -- Workaround
                    return false;
                }
            }
        }
        */

        // Prüfen, ob der angegebene Obmann tatsächlich exisitert
        int boatCptPos = getBoatCaptain();
        if (boatCptPos >= 0) {
            if ((boatCptPos == 0 && cox.getValue().trim().length() == 0)
                    || (boatCptPos > 0 && crew[boatCptPos - 1].getValue().trim().length() == 0)) {
                Dialog.error(International.getString("Für die als Obmann ausgewählte Person wurde kein Name eingegeben. "
                        + "Bitte gib entweder einen Namen ein oder wählen jemand anderes als Obmann aus!"));
                startBringToFront(false); // efaDirekt im BRC -- Workaround
                return false;
            }
        }

        // Ok, alles klar: Jetzt speichern

        /* @todo
        // Mehrtagestour fortsetzen (d.h. Fahrtart beim neuen Eintrag beibehalten)??
        // -> nur, wenn es sich um eine wirkliche Wanderfahrt handelt, die in Form mehrerer Etappen eingegeben wird
        if (Daten.efaTypes != null
                && Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_SESSION, fahrtart.getSelectedItem().toString()) == null
                && !isFahrtDauerMehrtagesfahrtAction(fahrtart.getSelectedItem().toString())
                && Daten.fahrtenbuch.getMehrtagesfahrt((String) fahrtart.getSelectedItem()) != null
                && Daten.fahrtenbuch.getMehrtagesfahrt((String) fahrtart.getSelectedItem()).isEtappen) {
            continueMTour = true;
        } else {
            continueMTour = false;
        }
        */

        boolean success = saveEntryInLogbook();
        setEntryUnchanged();
        return success;
    }

    // den Datensatz nun wirklich speichern;
    boolean saveEntryInLogbook() {
        if (!isLogbookReady()) {
            return false;
        }

        long lock = 0;
        try {
            if (!isNewRecord && currentRecord != null && !currentRecord.getEntryId().equals(entryno.getValue())) {
                // Datensatz mit geänderter LfdNr: Der alte Datensatz muß gelöscht werden!
                lock = logbook.data().acquireGlobalLock();
                logbook.data().delete(currentRecord.getKey());
            }
            currentRecord = getFields();
            logbook.data().add(currentRecord, lock);
        } catch (Exception e) {
            return false;
        } finally {
            if (lock != 0) {
                logbook.data().releaseGlobalLock(lock);
            }
        }

        if (isModeAdmin()) {
            Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYMODIFIED,
                    International.getString("Admin") + ": "
                    + International.getMessage("Fahrtenbuch-Eintrag #{lfdnr} wurde verändert.",
                    (currentRecord != null ? currentRecord.getEntryId().toString() : "$$")));
        }
        return true;
    }
    

    void setEntryUnchanged() {
        entryno.setUnchanged();
        date.setUnchanged();
        boat.setUnchanged();
        cox.setUnchanged();
        for (int i=0; i<crew.length; i++) {
            crew[i].setUnchanged();
        }
        starttime.setUnchanged();
        endtime.setUnchanged();
        destination.setUnchanged();
        distance.setUnchanged();
        comments.setUnchanged();
        sessiontype.setUnchanged();
        boatcaptain.setUnchanged();
    }

    boolean isEntryChanged() {
        boolean changed =
                entryno.isChanged() ||
                date.isChanged() ||
                boat.isChanged() ||
                cox.isChanged() ||
                starttime.isChanged() ||
                endtime.isChanged() ||
                destination.isChanged() ||
                distance.isChanged() ||
                comments.isChanged() ||
                sessiontype.isChanged() ||
                boatcaptain.isChanged();
        for (int i=0; !changed && i<crew.length; i++) {
            changed = crew[i].isChanged();
        }
        return changed;
    }

    boolean promptSaveChangesOk() {
        if (!isLogbookReady() || isModeBoathouse()) {
            return true;
        }
        if (isEntryChanged()) {
            String txt;
            if (isNewRecord) {
                txt = International.getString("Der aktuelle Eintrag wurde verändert und noch nicht zum Fahrtenbuch hinzugefügt. "
                        + "Möchtest Du ihn jetzt hinzufügen?");
            } else {
                txt = International.getString("Änderungen an dem aktuellen Eintrag wurden noch nicht gespeichert.")
                        + "\n" + International.getString("Möchtest Du die Änderungen jetzt speichern?");
            }
            switch (Dialog.yesNoCancelDialog(International.getString("Eintrag nicht gespeichert"), txt)) {
                case Dialog.YES:
                    saveEntry();
                    break;
                case Dialog.NO:
                    break;
                default:
                    startBringToFront(false);
                    return false;
            }
        }
        startBringToFront(false);
        return true;
    }

    ItemTypeStringAutoComplete getCrewItem(int pos) {
        if (pos == 0) {
            return cox;
        }
        if (pos >= 1 && pos <= LogbookRecord.CREW_MAX) {
            return crew[pos-1];
        }
        return null;
    }

    boolean isCoxOrCrewItem(IItemType item) {
        if (item == cox) {
            return true;
        }
        for (int i=0; i<crew.length; i++) {
            if (item == crew[i]) {
                return true;
            }
        }
        return false;
    }

    int getNumberOfPersonsInBoat() {
        int c = 0;
        if (cox.getValueFromField().trim().length() > 0) {
            c++;
        }
        for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
            if (crew[i].getValueFromField().trim().length() > 0) {
                c++;
            }
        }
        return c;
    }

    void setTime(ItemTypeTime field, int addMinutes, String notBefore) {
        DataTypeTime now = DataTypeTime.now();
        now.add(addMinutes*60);
        int m = now.getMinute();
        if (m % 5 != 0) {
            if (m % 5 < 3) {
                now.delete(m % 5);
            } else {
                now.add(5 - m % 5);
            }
        }

        if (notBefore != null && notBefore.length() > 0) {
            DataTypeTime notBeforeTime = DataTypeTime.parseTime(notBefore);
            // Test: EndTime < StartTime (where EndTime is at most the configured (add+substract)*2 times smaller)
            if (now.isBefore(notBeforeTime) &&
                now.getTimeAsSeconds() + (Daten.efaConfig.efaDirekt_plusMinutenAbfahrt.getValue() + Daten.efaConfig.efaDirekt_minusMinutenAnkunft.getValue()) * 60 * 2 >
                notBeforeTime.getTimeAsSeconds()) {
                // use StartTime as EndTime instead (avoid overlapping times)
                now.setHour(notBeforeTime.getHour());
                now.setMinute(notBeforeTime.getMinute());
            }
        }
        
        field.parseAndShowValue(now.toString());
        field.setSelection(0, Integer.MAX_VALUE);
    }

    void editBoat(ItemTypeStringAutoComplete item) {
        // @todo
/*
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null ||
        (bootButton.getBackground() != Color.red && bootButton.getBackground() != Color.green) ||
        Daten.fahrtenbuch.getDaten().boote == null) return;
    NeuesBootFrame dlg = new NeuesBootFrame(this,boot.getText().trim(),bootButton.getBackground() == Color.red);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
*/
    }

    void editPerson(ItemTypeStringAutoComplete item) {
        // @todo
/*
    boolean directMode = false;
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) {
      if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen.getValue()) return;
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
*/
    }

    void editDestination(ItemTypeStringAutoComplete item) {
        // @todo
/*
    if (isDirectMode() || mode == MODE_ADMIN_NUR_FAHRTEN) return;
    if (Daten.fahrtenbuch == null ||
        (zielButton.getBackground() != Color.red && zielButton.getBackground() != Color.green)||
        Daten.fahrtenbuch.getDaten().ziele == null) return;
    NeuesZielFrame dlg = new NeuesZielFrame(this,ziel.getText().trim(),zielButton.getBackground() == Color.red);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    startBringToFront(false); // efaDirekt im BRC -- Workaround
*/
    }

    // =========================================================================
    // Menu Actions
    // =========================================================================

    void menuFile_newProject_actionPerformed(ActionEvent e) {
        if (!isModeFull()) {
            return;
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        NewProjectDialog dlg = new NewProjectDialog(this);
        String logbookName = dlg.createNewProjectAndLogbook();
        if (Daten.project != null && logbookName != null) {
            openLogbook(logbookName);
        }
        setTitle();
        startBringToFront(false);
    }

    void menuFile_openProject_actionPerformed(ActionEvent e) {
        if (!isModeFull()) {
            return;
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.project);
        String projectName = dlg.openDialog();
        Project.openProject(projectName);
        if (Daten.project != null && !isModeBoathouse()) {
            Daten.efaConfig.lastProjectEfaBase.setValue(Daten.project.getProjectName());
        }
        if (Daten.project != null && Daten.project.getCurrentLogbookEfaBase() != null) {
            openLogbook(Daten.project.getCurrentLogbookEfaBase());
        }
        setTitle();
        startBringToFront(false);
    }

    void menuFile_logbooks_actionPerformed(ActionEvent e) {
        if (!isModeFull()) {
            return;
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        if (Daten.project == null) {
            menuFile_openProject_actionPerformed(e);
            if (Daten.project == null) {
                return;
            }
        }
        OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.logbook);
        String logbookName = dlg.openDialog();
        if (logbookName != null) {
            openLogbook(logbookName);
        }
        setTitle();
        startBringToFront(false);
    }

    // =========================================================================
    // Toolbar Button Actions
    // =========================================================================

    void navigateInLogbook(int relative) {
        if (!isLogbookReady() || iterator == null) {
            return;
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        LogbookRecord r = null;
        switch(relative) {
            case Integer.MIN_VALUE:
                r = logbook.getLogbookRecord(iterator.getFirst());
                break;
            case Integer.MAX_VALUE:
                r = logbook.getLogbookRecord(iterator.getLast());
                break;
            case -1:
                r = logbook.getLogbookRecord(iterator.getPrev());
                break;
            case 1:
                r = logbook.getLogbookRecord(iterator.getNext());
                break;
        }
        if (r != null) {
            setFields(r);
        }
    }

    void goToEntry(String entryNo) {
        if (!isLogbookReady() || iterator == null) {
            return;
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        LogbookRecord r = logbook.getLogbookRecord(DataTypeIntString.parseString(entryNo));
        if (r != null) {
            setFields(r);
        }
    }


    // =========================================================================
    // Callback-related methods
    // =========================================================================

    public void itemListenerAction(IItemType item, AWTEvent event) {
        int id = event.getID();
        if (id == ActionEvent.ACTION_PERFORMED) {
            if (item == boat) {
                editBoat((ItemTypeStringAutoComplete)item);
            }
            if (item == cox) {
                editPerson((ItemTypeStringAutoComplete)item);
            }
            for (int i=0; i<LogbookRecord.CREW_MAX; i++) {
                if (item == crew[i]) {
                    editPerson((ItemTypeStringAutoComplete)item);
                }
            }
            if (item == destination) {
                editDestination((ItemTypeStringAutoComplete)item);
            }
            if (item == remainingCrewUpButton) {
                setCrewRangeSelection(crewRangeSelection - 1);
            }
            if (item == remainingCrewDownButton) {
                setCrewRangeSelection(crewRangeSelection + 1);
            }
            if (item == boatDamageButton) {
                // @todo
            }
        }
        if (id == FocusEvent.FOCUS_GAINED) {
            colorize(item);
            showHint(item.getName());
            if (lastFocusedItem != null && isCoxOrCrewItem(lastFocusedItem) &&
                    !isCoxOrCrewItem(item)) {
                autoSelectBoatCaptain();
            }
        }
        if (id == FocusEvent.FOCUS_LOST) {
            decolorize(item);
            lastFocusedItem = item;
            if (item == cox) {
                if (Daten.efaConfig.autoObmann.getValue() && isNewRecord
                        && cox.getValueFromField().trim().length() > 0 && this.getBoatCaptain() == -1) {
                    this.setBoatCaptain(0, true);
                }

            }
            if (item == crew[0]) {
                if (Daten.efaConfig.autoObmann.getValue() && isNewRecord && getBoatCaptain() == -1) {
                    if (Daten.efaConfig.defaultObmann.getValue().equals(EfaConfig.OBMANN_BOW) && crew[0].getValueFromField().trim().length() > 0) {
                        setBoatCaptain(1, true);
                    }
                }
            }
            if (isModeBoathouse() && (item == starttime || item == endtime)) {
                setTime((ItemTypeTime)item, 0, null);
            }
        }
        if (id == KeyEvent.KEY_PRESSED && event instanceof KeyEvent) {
            KeyEvent e = (KeyEvent)event;
            if ((e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) || // Ctrl-F
                (e.getKeyCode() == KeyEvent.VK_F5)) {                     // F5
                if (item instanceof ItemTypeLabelValue) {
                    insertLastValue(e, (ItemTypeLabelValue)item);
                }
            }
            if ((e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O)) { // Ctrl-O
                if (item instanceof ItemTypeLabelValue) {
                    selectBoatCaptain(item.getName());
                }
            }
            if (item == comments) {
                String[] k = Daten.efaConfig.keys.getKeysArray();
                if (k != null && k.length > 0) {
                    for (int i = 0; i < k.length; i++) {
                        if ( (((String) k[i]).equals("F6")  && e.getKeyCode() == KeyEvent.VK_F6  && Daten.efaConfig.keys.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F7")  && e.getKeyCode() == KeyEvent.VK_F7  && Daten.efaConfig.keys.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F8")  && e.getKeyCode() == KeyEvent.VK_F8  && Daten.efaConfig.keys.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F9")  && e.getKeyCode() == KeyEvent.VK_F9  && Daten.efaConfig.keys.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F10") && e.getKeyCode() == KeyEvent.VK_F10 && Daten.efaConfig.keys.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F11") && (e.getKeyCode() == KeyEvent.VK_F11 || e.getKeyCode() == KeyEvent.VK_STOP) && Daten.efaConfig.keys.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F12") && (e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_AGAIN) && Daten.efaConfig.keys.get(k[i]) != null )
                                ) {
                            comments.parseAndShowValue(comments.getValueFromField() + Daten.efaConfig.keys.get(k[i]));
                        }
                    }
                }
            }
        }
        if (id == MouseEvent.MOUSE_CLICKED) {
            if (item instanceof ItemTypeLabelValue) {
                selectBoatCaptain(item.getName());
            }
        }
        if (id == ItemEvent.ITEM_STATE_CHANGED) {
            if (item == boatcaptain) {
                setBoatCaptain(getBoatCaptain(), false);
            }
        }
    }

    void colorize(IItemType itemType) {
        if (!Daten.efaConfig.efaDirekt_colorizeInputField.getValue()) {
            return;
        }
        if (itemType != remainingCrewUpButton && itemType != remainingCrewDownButton) {
            itemType.saveBackgroundColor();
        }
        itemType.setBackgroundColor(Color.yellow);
    }

    void decolorize(IItemType itemType) {
        if (!Daten.efaConfig.efaDirekt_colorizeInputField.getValue()) {
            return;
        }
        itemType.restoreBackgroundColor();
    }

    void showHint(String s) {
        if (s == null) {
            infoLabel.setText("");
            return;
        }
        if (s.equals(LogbookRecord.DATE)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Tag") + ">.<"
                    + International.getString("Monat") + ">.<"
                    + International.getString("Jahr") + ">");
        }
        if (s.equals(LogbookRecord.BOATNAME)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Bootsname") + ">");
        }
        if (LogbookRecord.getCrewNoFromFieldName(s) >= 0) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + (Daten.efaConfig.nameFormat.equals(EfaConfig.NAMEFORMAT_FIRSTLAST)
                    ? "<" + International.getString("Vorname") + "> <"
                    + International.getString("Nachname") + ">"
                    : "<" + International.getString("Nachname") + ">,  <"
                    + International.getString("Vorname") + ">"));
        }
        if (s.equals(LogbookRecord.STARTTIME) || s.equals(LogbookRecord.ENDTIME)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Stunde") + ">:<"
                    + International.getString("Minute") + ">");
        }
        if (s.equals(LogbookRecord.DESTINATIONNAME)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Ziel der Fahrt") + ">");
        }
        if (s.equals(LogbookRecord.DISTANCE)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Kilometer") + ">");
        }
        if (s.equals(LogbookRecord.COMMENTS)) {
            infoLabel.setText(International.getString("Bemerkungen eingeben oder frei lassen"));
        }
        if (s.equals(LogbookRecord.BOATCAPTAIN)) {
            infoLabel.setText(International.getString("Bitte auswählen")
                    + ": " + International.getString("verantwortlichen Obmann"));
        }
        if (s.equals(LogbookRecord.SESSIONTYPE)) {
            infoLabel.setText(International.getString("Bitte auswählen")
                    + ": " + International.getString("Art der Fahrt"));
        }
        if (s.equals("REMAININGCREWUP") || s.equals("REMAININGCREWDOWN")) {
            infoLabel.setText(International.getString("weitere Mannschaftsfelder anzeigen"));
        }
        if (s.equals("BOATDAMAGE")) {
            infoLabel.setText(International.getString("einen Schaden am Boot melden"));
        }
        if (s.equals("SAVE")) {
            infoLabel.setText(International.getString("<Leertaste> drücken, um den Eintrag abzuschließen"));
        }
    }

    void insertLastValue(KeyEvent e, ItemTypeLabelValue item) {
        if (e == null || isModeBoathouse() || referenceRecord == null) {
            return;
        }
        if ((e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) || // Ctrl-F
                (e.getKeyCode() == KeyEvent.VK_F5)) {                 // F5
            setField(item, referenceRecord);
            //((JTextField) e.getSource()).replaceSelection(refDatensatz.get(field)); // old from efa1
        }
    }

    private void setFieldEnabled(boolean enabled, boolean visible, IItemType item) {
        if (Daten.efaConfig.efaDirekt_eintragHideUnnecessaryInputFields.getValue()) {
            item.setVisible(visible);
        }
        item.setEnabled(enabled);
    }

    private void setFieldEnabledBootsKm() {
        if (mode != MODE_BOATHOUSE_FINISH && mode != MODE_BOATHOUSE_LATEENTRY) {
            return; // Zielabhängiges Enabled der BootsKm nur bei "Fahrt beenden" und "Nachtrag"
        }
        boolean enabled = !destination.isKnown()
                || !Daten.efaConfig.efaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen.getValue();
        setFieldEnabled(enabled, true, distance);
    }

    private void setCoxCrewFieldsEnabled(BoatRecord boatRecord) {
        /* @todo
        boolean isstm = true;
        int anz = Fahrtenbuch.ANZ_MANNSCH;
        if (d != null) {
            isstm = d.get(Boote.STM).equals(EfaTypes.TYPE_COXING_COXED)
                    || d.get(Boote.STM).equals(EfaTypes.TYPE_COXING_OTHER);
            anz = EfaTypes.getNumberOfRowers(d.get(Boote.ANZAHL));
        }
        // Steuermann wird bei steuermannslosen Booten immer disabled (unabhängig von Konfigurationseinstellung)
        setFieldEnabled(isstm, isstm, stm, stmLabel, stmButton);
        if (!isstm) {
            stm.setText("");
            if (getObmann() == 0) {
                setObmann(-1, true);
            }
        }
        if (Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl.getValue()) {
            for (int i = 1; i <= Fahrtenbuch.ANZ_MANNSCH; i++) {
                JLabel label = null;
                JButton button = null;
                if (i > (mannschAuswahl * 8) && i <= ((mannschAuswahl + 1) * 8)) {
                    switch (i % 8) {
                        case 1:
                            label = mannsch1_label;
                            break;
                        case 2:
                            label = mannsch2_label;
                            break;
                        case 3:
                            label = mannsch3_label;
                            break;
                        case 4:
                            label = mannsch4_label;
                            break;
                        case 5:
                            label = mannsch5_label;
                            break;
                        case 6:
                            label = mannsch6_label;
                            break;
                        case 7:
                            label = mannsch7_label;
                            break;
                        case 0:
                            label = mannsch8_label;
                            break;
                    }
                    button = mannschButton[(i - 1) % 8];
                }
                setFieldEnabled(i <= anz, i <= anz, mannsch[i - 1], label, button);
                if (i > anz) {
                    mannsch[i - 1].setText("");
                    if (getObmann() == i) {
                        setObmann(-1, true);
                    }
                }
            }
        }

        // "Weiterere Mannschaft"-Button ggf. ausblenden
        setFieldEnabled(true, anz > 8 || !Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl.getValue(), null, null, weitereMannschButton);

        // "Obmann" ggf. ausblenden
        setFieldEnabled(true, isstm || anz > 1, obmann, obmannLabel, null);

        // Bezeichnung für Mannschaftsfelder anpassen
        if (anz != 1 || isstm) {
            if (mannsch1_label_defaultText != null) {
                this.mannsch1_label.setText(mannsch1_label_defaultText);
            } else {
                this.mannsch1_label.setText(International.getString("Mannschaft") + ": 1: "); // just in case
            }
        } else {
            this.mannsch1_label.setText(International.getString("Name") + ": ");
        }
        */
    }

    // wird von boot_focusLost aufgerufen, sowie vom FocusManager! (irgendwie unsauber, da bei <Tab> doppelt...
    void currentBoatUpdateGui() {
        currentBoat = null;
        if (!isLogbookReady()) {
            return;
        }

        String boatName = boat.getValueFromField().trim();
        try {
            Boats boats = Daten.project.getBoats(false);
            DataKey[] keys = boats.data().getByFields(
                    BoatRecord.IDX_NAME_OWNER, BoatRecord.getValuesForIndexFromQualifiedName(boatName));
            if (keys != null && keys.length > 0) {
                currentBoat = (BoatRecord)boats.data().get(keys[0]);
            }
        } catch(Exception e) {
            
        }

        if (isModeBoathouse()) {
            setCoxCrewFieldsEnabled(currentBoat);
            pack();
        }
        /* @todo
        if (currentRecord == null && Daten.mannschaften != null
                && Daten.efaConfig.autoStandardmannsch.getValue()
                && boot.getText().trim().length() != 0
                && Daten.mannschaften.getExact(boot.getText().trim()) != null) {
            // Bugfix 1.7.1: nur Standardmannschaft eintragen, wenn alle Eingabefelder (Mannschaft und Ziel) noch leer sind
            if (getAnzahlRuderer() == 0 && ziel.getText().trim().length() == 0) {
                setStandardMannschaft((DatenFelder) Daten.mannschaften.getComplete());
            }
        }
        */
    }

    void selectBoatCaptain(String field) {
        int pos = LogbookRecord.getCrewNoFromFieldName(field);
        if (pos >= 0) {
            selectBoatCaptain(pos);
        }
    }

    void selectBoatCaptain(int pos) {
        if (getBoatCaptain() == pos) {
            setBoatCaptain(-1, true);
        } else {
            setBoatCaptain(pos, true);
        }
    }

    void setBoatCaptain(int pos, boolean updateListSelection) {
        ItemTypeStringAutoComplete field;
        for (int i=0; i<= LogbookRecord.CREW_MAX; i++) {
            field = getCrewItem(i);
            if (i == pos) {
                field.setFieldFont(field.getLabelFont().deriveFont(Font.BOLD));
            } else {
                field.restoreFieldFont();
            }
        }
        if (updateListSelection) {
            if (pos >= 0 && pos <= LogbookRecord.CREW_MAX) {
                boatcaptain.parseAndShowValue(Integer.toString(pos));
            } else {
                boatcaptain.parseAndShowValue("");
            }
        }
    }

    int getBoatCaptain() {
        String val = boatcaptain.getValueFromField();
        if (val.length() == 0) {
            return -1;
        }
        try {
            return Integer.parseInt(val);
        } catch(Exception e) {
            return -1;
        }
    }

    void autoSelectBoatCaptain() {
        if (Daten.efaConfig.autoObmann.getValue() && isNewRecord
                && getBoatCaptain() == -1) {
            if (Daten.efaConfig.defaultObmann.getValue().equals(EfaConfig.OBMANN_STROKE)) {
                try {
                    int anzRud = getNumberOfPersonsInBoat();
                    if (anzRud > 0) {
                        setBoatCaptain(anzRud, true);
                    }
                } catch (Exception ee) {
                    EfaUtil.foo();
                }
            }
        }

        // Wenn Angabe eines Obmanns Pflicht ist, soll auch im Einer immer der Obmann automatisch selektiert werden,
        // unabhängig davon, ob Daten.efaConfig.autoObmann aktiviert ist oder nicht
        if (Daten.efaConfig.efaDirekt_eintragErzwingeObmann.getValue()
                && isNewRecord && getBoatCaptain() == -1
                && cox.getValueFromField().trim().length() == 0
                && getNumberOfPersonsInBoat() == 1) {
            try {
                setBoatCaptain(1, true);
            } catch (Exception ee) {
                EfaUtil.foo();
            }
        }
    }

    void setCrewRangeSelection(int nr) {
        if (nr < 0) {
            nr = (LogbookRecord.CREW_MAX / 8) - 1;
        }
        if (nr >= LogbookRecord.CREW_MAX / 8) {
            nr = 0;
        }
        for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
            crew[i].setVisible(i / 8 == nr);
        }
        crewRangeSelection = nr;
        setCrewRangeSelectionColoring();
        pack();
    }

    void setCrewRangeSelectionColoring() {
        boolean hiddenCrewFieldsSet = false;
        for (int i = 0; !hiddenCrewFieldsSet && i < LogbookRecord.CREW_MAX; i++) {
            if (i / 8 != crewRangeSelection) {
                if (crew[i].getValueFromField().trim().length() > 0) {
                    hiddenCrewFieldsSet = true;
                }
            }
        }
        if (hiddenCrewFieldsSet) {
            remainingCrewUpButton.setBackgroundColor(Color.orange);
            remainingCrewDownButton.setBackgroundColor(Color.orange);
        } else {
            remainingCrewUpButton.restoreBackgroundColor();
            remainingCrewDownButton.restoreBackgroundColor();
        }
    }

    // =========================================================================
    // Window-related methods
    // =========================================================================
    
    private void this_windowClosing(WindowEvent e) {
        cancel();
    }

    private void this_windowIconified(WindowEvent e) {
        if (isModeBoathouse()) {
            startBringToFront(true);
        }
    }

    public boolean cancel() {
        /*
        if (mode != MODE_FULL) {
            if (mode == MODE_ADMIN || mode == MODE_ADMIN_NUR_FAHRTEN) {
                if (efaDirektAdminFrame == null) {
                    return;
                }
                if (!sicherheitsabfrage()) {
                    return;
                }
                Dialog.frameClosed(this);
                dispose();
                efaDirektAdminFrame.fahrtenbuchClosed();
                efaDirektAdminFrame = null;
                return;
            }
            hideEfaFrame();
            efaDirektFrame.setEnabled(true);
            efaDirektFrame.toFront();
            return;
        }
        */

        if (!promptSaveChangesOk()) {
            return false;
        }
        if (!Daten.efaConfig.writeFile()) {
            LogString.logError_fileWritingFailed(Daten.efaConfig.getFileName(), International.getString("Konfigurationsdatei"));
        }
        super.cancel();
        Daten.haltProgram(0);
        return true;
    }

    public void startBringToFront(boolean always) {
        // Irgendwie ist im BRC das EfaFrame immer dann, wenn zuvor eine Dialog-Box aufpoppte, noch immer nicht
        // im Vordergrund; daher dieser Workaround
        if (!always) {
            // nur im Admin-Mode nach vorne bringen
            if (!isModeAdmin()) {
                return;
            }
            if (this.isActive()) {
                if (Logger.isTraceOn(Logger.TT_GUI)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GENERIC, "Dialog closed: EfaFrame is already active.");
                }
                return;
            }
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GENERIC, "Dialog closed: EfaFrame is inactive and will be brought to front.");
            }
        }
        BringToFrontThread.bringToFront(this, 100);
    }

}
