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
import de.nmichael.efa.gui.dataedit.*;
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
    JMenuItem menuHelp_help = new JMenuItem();
    JMenuItem menuHelp_efaConsole = new JMenuItem();
    JMenuItem menuHelp_about = new JMenuItem();
    JMenu menuAdministration = new JMenu();
    JMenuItem menuAdministration_configuration = new JMenuItem();
    JMenuItem menuAdministration_boats = new JMenuItem();
    JMenuItem menuAdministration_boatStatus = new JMenuItem();
    JMenuItem menuAdministration_boatReservations = new JMenuItem();
    JMenuItem menuAdministration_boatDamages = new JMenuItem();
    JMenuItem menuAdministration_persons = new JMenuItem();
    JMenuItem menuAdministration_status = new JMenuItem();
    JMenuItem menuAdministration_groups = new JMenuItem();
    JMenuItem menuAdministration_crews = new JMenuItem();
    JMenuItem menuAdministration_fahrtenabzeichen = new JMenuItem();
    JMenuItem menuAdministration_destinations = new JMenuItem();
    JMenuItem menuAdministration_waters = new JMenuItem();
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
    ItemTypeDate enddate;
    ItemTypeStringAutoComplete boat;
    ItemTypeStringList boatvariant;
    ItemTypeStringAutoComplete cox;
    ItemTypeStringAutoComplete[] crew;
    ItemTypeStringList boatcaptain;
    ItemTypeTime starttime;
    ItemTypeTime endtime;
    ItemTypeStringAutoComplete destination;
    ItemTypeDistance distance;
    ItemTypeString comments;
    ItemTypeStringList sessiontype;

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
    String currentBoatTypeSeats;    // boat type for currentBoat
    String currentBoatTypeCoxing;  // boat type for currentBoat
    int crewRangeSelection = 0;     // mannschAuswahl = 0: 1-8 sichtbar; 1: 9-16 sichtbar; 2: 17-24 sichtbar
    String crew1defaultText = null; // mannsch1_label_defaultText = der Standardtext, den das Label "Mannschaft 1: " normalerweise haben soll (wenn es nicht für Einer auf "Name: " gesetzt wird)
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

    public boolean isModeBase() {
        return getMode() == MODE_BASE;
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
                // menuFileExit_actionPerformed(e);
            }
        });

        // Menu: Administration
        Mnemonics.setMenuButton(this, menuAdministration, International.getStringWithMnemonic("Administration"));

        Mnemonics.setMenuButton(this, menuAdministration_configuration, International.getStringWithMnemonic("Konfiguration"));
        setIcon(menuAdministration_configuration, getIcon("menu_einst.gif"));
        menuAdministration_configuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationConfiguration_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_boats, International.getStringWithMnemonic("Boote"));
        setIcon(menuAdministration_boats, getIcon("menu_boats.gif"));
        menuAdministration_boats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationBoats_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_boatStatus, International.getStringWithMnemonic("Bootsstatus"));
        setIcon(menuAdministration_boatStatus, getIcon("menu_boatstatus.gif"));
        menuAdministration_boatStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationBoatStatus_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_boatReservations, International.getStringWithMnemonic("Bootsreservierungen"));
        setIcon(menuAdministration_boatReservations, getIcon("menu_boatreservations.gif"));
        menuAdministration_boatReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationBoatReservations_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_boatDamages, International.getStringWithMnemonic("Bootsschäden"));
        setIcon(menuAdministration_boatDamages, getIcon("menu_boatdamages.gif"));
        menuAdministration_boatDamages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationBoatDamages_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_persons, International.getStringWithMnemonic("Personen"));
        setIcon(menuAdministration_persons, getIcon("menu_persons.gif"));
        menuAdministration_persons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationPersons_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_status, International.getStringWithMnemonic("Status"));
        setIcon(menuAdministration_status, getIcon("menu_status.gif"));
        menuAdministration_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationStatus_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_groups, International.getStringWithMnemonic("Gruppen"));
        setIcon(menuAdministration_groups, getIcon("menu_groups.gif"));
        menuAdministration_groups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationGroups_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_crews, International.getStringWithMnemonic("Mannschaften"));
        setIcon(menuAdministration_crews, getIcon("menu_crews.gif"));
        menuAdministration_crews.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationCrews_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_fahrtenabzeichen, International.onlyFor("Fahrtenabzeichen", "de"));
        setIcon(menuAdministration_fahrtenabzeichen, getIcon("menu_fahrtenabzeichen.gif"));
        menuAdministration_fahrtenabzeichen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationFahrtenabzeichen_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_destinations, International.getStringWithMnemonic("Ziele"));
        setIcon(menuAdministration_destinations, getIcon("menu_destinations.gif"));
        menuAdministration_destinations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationDestinations_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuAdministration_waters, International.getStringWithMnemonic("Gewässer"));
        setIcon(menuAdministration_waters, getIcon("menu_waters.gif"));
        menuAdministration_waters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuAdministrationWaters_actionPerformed(e);
            }
        });

        // Menu: Statistics
        Mnemonics.setButton(this, menuStatistics, International.getStringWithMnemonic("Ausgabe"));

        Mnemonics.setMenuButton(this, menuStatistics_createStatistics, International.getStringWithMnemonic("Statistiken und Meldedateien erstellen"));
        setIcon(menuStatistics_createStatistics, getIcon("menu_stat.gif"));
        menuStatistics_createStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // menuItemKilometerliste_actionPerformed(e);
            }
        });

        // Menu: Help
        Mnemonics.setButton(this, menuHelp, International.getStringWithMnemonic("Info"));

        Mnemonics.setMenuButton(this, menuHelp_help, International.getStringWithMnemonic("Hilfe"));
        setIcon(menuHelp_help, getIcon("menu_help.gif"));
        menuHelp_help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Help.showHelp(null);
            }
        });

        Mnemonics.setMenuButton(this, menuHelp_efaConsole, International.getStringWithMnemonic("efa-Konsole"));
        setIcon(menuHelp_efaConsole, getIcon("menu_konsole.gif"));
        menuHelp_efaConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // menuHilfeJavaKonsole_actionPerformed(e);
            }
        });

        Mnemonics.setMenuButton(this, menuHelp_about, International.getStringWithMnemonic("Über"));
        setIcon(menuHelp_about, getIcon("menu_about.gif"));
        menuHelp_about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // menuHelpAbout_actionPerformed(e);
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
        menuAdministration.add(menuAdministration_configuration);
        menuAdministration.addSeparator();
        menuAdministration.add(menuAdministration_boats);
        menuAdministration.add(menuAdministration_boatStatus);
        menuAdministration.add(menuAdministration_boatReservations);
        menuAdministration.add(menuAdministration_boatDamages);
        menuAdministration.addSeparator();
        menuAdministration.add(menuAdministration_persons);
        menuAdministration.add(menuAdministration_status);
        menuAdministration.add(menuAdministration_groups);
        menuAdministration.add(menuAdministration_crews);
        if (Daten.efaConfig.showGermanOptions.getValue()) {
            menuAdministration.add(menuAdministration_fahrtenabzeichen);
        }
        menuAdministration.addSeparator();
        menuAdministration.add(menuAdministration_destinations);
        menuAdministration.add(menuAdministration_waters);

        // Menu: Statistics
        menuStatistics.add(menuStatistics_createStatistics);

        // Menu: Help
        menuHelp.add(menuHelp_help);
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
                createNewRecord(false);
            }
        });

        toolBar_insertButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_insertButton, International.getStringWithMnemonic("Einfügen"));
        toolBar_insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewRecord(true);
            }
        });

        toolBar_deleteButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_deleteButton, International.getStringWithMnemonic("Löschen"));
        toolBar_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
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

        // End Date
        enddate = new ItemTypeDate(LogbookRecord.ENDDATE, new DataTypeDate(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("bis"));
        enddate.showWeekday(true);
        enddate.setFieldSize(100, 19);
        enddate.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        enddate.setFieldGrid(1, GridBagConstraints.WEST, GridBagConstraints.NONE);
        enddate.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        enddate.setWeekdayGrid(1, GridBagConstraints.WEST, GridBagConstraints.NONE);
        enddate.showOptional(true);
        enddate.displayOnGui(this, mainInputPanel, 4, 1);
        enddate.registerItemListener(this);

        // Boat
        boat = new ItemTypeStringAutoComplete(LogbookRecord.BOATNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Boot"), true);
        boat.setFieldSize(200, 19);
        boat.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boat.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        boat.setAutoCompleteData(autoCompleteListBoats);
        boat.setChecks(true, true);
        boat.displayOnGui(this, mainInputPanel, 0, 2);
        boat.registerItemListener(this);

        // Boat Variant
        boatvariant = new ItemTypeStringList(LogbookRecord.BOATVARIANT, "",
                null, null,
                IItemType.TYPE_PUBLIC, null, International.getString("Variante"));
        boatvariant.setFieldSize(80, 17);
        boatvariant.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boatvariant.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        boatvariant.displayOnGui(this, mainInputPanel, 5, 2);
        boatvariant.registerItemListener(this);

        // Cox
        cox = new ItemTypeStringAutoComplete(LogbookRecord.COXNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Steuermann"), true);
        cox.setFieldSize(200, 19);
        cox.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        cox.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        cox.setAutoCompleteData(autoCompleteListPersons);
        cox.setChecks(true, true);
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
            crew[j].setChecks(true, true);
            crew[j].displayOnGui(this, mainInputPanel, (left ? 0 : 4), 4 + j%4);
            crew[j].setVisible(j < 8);
            crew[j].registerItemListener(this);
            crew1defaultText = crew[j].getDescription();
        }

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
        boatcaptain.setFieldSize(80, 17);
        boatcaptain.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boatcaptain.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        boatcaptain.displayOnGui(this, mainInputPanel, 5, 3);
        boatcaptain.registerItemListener(this);

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
        destination.setChecks(true, false);
        destination.setIgnoreEverythingAfter('+');
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
        infoLabel.setText(" ");
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
                    if (Daten.project != null) {
                        Daten.project.runAudit(); // @todo - remove again
                    }
                }
            } else {
                if (Daten.efaConfig.lastProjectEfaBase.getValue().length() > 0) {
                    Project.openProject(Daten.efaConfig.lastProjectEfaBase.getValue());
                    if (Daten.project != null) {
                        Daten.project.runAudit(); // @todo - remove again
                    }
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
        } else {
            t = LogbookRecord.getValidAtTimestamp(date.getDate(), starttime.getTime());
        }
        if (t == 0) {
            t = System.currentTimeMillis();
        }
        return t;
    }

    PersonRecord findPerson(ItemTypeString item, long preferredValidAt) {
        PersonRecord p = null;
        try {
            String s = item.toString().trim();
            if (s.length() > 0) {
                return Daten.project.getPersons(false).getPerson(s, logbookValidFrom, logbookInvalidFrom-1, preferredValidAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    BoatRecord findBoat(long preferredValidAt) {
        try {
            String s = boat.toString().trim();
            if (s.length() > 0) {
                return Daten.project.getBoats(false).getBoat(s, logbookValidFrom, logbookInvalidFrom-1, preferredValidAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    PersonRecord findPerson(int pos, long preferredValidAt) {
        return findPerson(getCrewItem(pos), preferredValidAt);
    }

    DestinationRecord findDestination(long preferredValidAt) {
        DestinationRecord d = null;
        try {
            String s = destination.toString().trim();
            int variantPos = s.indexOf('+');
            if (variantPos >= 0) {
                s = s.substring(0, variantPos).trim();
            }
            if (s.length() > 0) {
                return Daten.project.getDestinations(false).getDestination(s, logbookValidFrom, logbookInvalidFrom-1, preferredValidAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    String updateBoatVariant(BoatRecord b, int variant) {
        if (b != null) {
            int numberOfVariants = b.getNumberOfVariants();
            if (numberOfVariants < 0) {
                boatvariant.setVisible(false);
                return null;
            }
            String[] bt = new String[numberOfVariants];
            String[] bv = new String[numberOfVariants];
            for (int i = 0; i < numberOfVariants; i++) {
                bt[i] = Integer.toString(b.getTypeVariant(i));
                bv[i] = b.getQualifiedBoatTypeShortName(i);
            }
            boatvariant.setListData(bt, bv);
            if (variant > 0) {
                boatvariant.parseAndShowValue(Integer.toString(variant));
            }
            boatvariant.setVisible(numberOfVariants > 1);
            return boatvariant.getValue();
        }
        boatvariant.setListData(null, null);
        boatvariant.setVisible(false);
        return null;

    }

    void openLogbook(String logbookName) {
        if (Daten.project == null) {
            return;
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
                if (!isModeBoathouse()) {
                    Daten.project.setCurrentLogbookEfaBase(logbookName);
                }
                ProjectRecord pr = Daten.project.getLoogbookRecord(logbookName);
                if (pr != null) {
                    logbookValidFrom = pr.getStartDate().getTimestamp(null);
                    logbookInvalidFrom = pr.getEndDate().getTimestamp(null) + 24 * 60 * 60 * 1000; // @todo millis?
                    if (logbookInvalidFrom < logbookValidFrom) {
                        logbookInvalidFrom = logbookValidFrom + 24 * 60 * 60 * 1000; // @todo millis?
                    }
                }
                try {
                    iterator = logbook.data().getDynamicIterator();
                    autoCompleteListBoats.setDataAccess(Daten.project.getBoats(false).data(), logbookValidFrom, logbookInvalidFrom-1); // @todo hmmm?? logbookValidFrom?
                    autoCompleteListPersons.setDataAccess(Daten.project.getPersons(false).data(), logbookValidFrom, logbookInvalidFrom-1); // @todo hmmm?? logbookValidFrom?
                    autoCompleteListDestinations.setDataAccess(Daten.project.getDestinations(false).data(), logbookValidFrom, logbookInvalidFrom-1); // @todo hmmm?? logbookValidFrom?
                } catch (Exception e) {
                    Logger.logdebug(e);
                    iterator = null;
                }
                if (isModeFull()) {
                    try {
                        LogbookRecord r = (LogbookRecord) logbook.data().getLast();
                        setFields(r);
                        entryno.requestFocus();
                    } catch (Exception e) {
                        Logger.logdebug(e);
                        setFields(null);
                    }
                }
            } else {
                Dialog.error(International.getMessage("Fahrtenbuch {logbook} konnte nicht geöffnet werden.", logbookName));
                setFields(null);
            }
        } else {
            setFields(null);
        }
        setTitle();
    }

    boolean isLogbookReady() {
        return Daten.project != null && Daten.project.isOpen() && logbook != null && logbook.isOpen();
    }

    String getFieldValue(ItemTypeLabelValue field, LogbookRecord r) {
        try {
            if (field == entryno) {
                return (r != null && r.getEntryId() != null ? r.getEntryId().toString() : "");
            }
            if (field == date) {
                return (r != null ? r.getDate().toString() : "");
            }
            if (field == enddate) {
                return (r != null ? r.getEndDate().toString() : "");
            }
            if (field == boat) {
                return (r != null ? r.getGuiBoatName(getValidAtTimestamp(r)) : "");
            }
            if (field == boatvariant) {
                return updateBoatVariant((r != null ? r.getBoatRecord(getValidAtTimestamp(r)) : null), (r != null ? r.getBoatVariant() : 0));
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
        } catch (NullPointerException enull) {
            // this happens when field in r has not been set, no need to log this!
        } catch (Exception e) {
            Logger.logdebug(e);
        }
        return "";
    }

    void setField(ItemTypeLabelValue field, LogbookRecord r) {
        field.parseAndShowValue(getFieldValue(field, r));
    }

    void setFields(LogbookRecord r) {
        if (!isLogbookReady() && r != null) {
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
        setField(enddate,r);
        setField(boat,r);
        setField(boatvariant,r);
        setField(cox,r);
        for (int i=0; i<crew.length; i++) {
            setField(crew[i],r);
        }
        setField(boatcaptain,r);
        setField(starttime,r);
        setField(endtime,r);
        setField(destination,r);
        setField(distance,r);
        setField(comments,r);
        setField(sessiontype,r);
        setCrewRangeSelection(0);
        setEntryUnchanged();
        currentBoat = null;
        currentBoatTypeSeats = null;
        currentBoatTypeCoxing = null;
        entryNoForNewEntry = -1; // -1 bedeutet, daß beim nächsten neuen Datensatz die LfdNr "last+1" vorgegeben wird
        if (r == null) {
            date.requestFocus();
            date.setSelection(0, Integer.MAX_VALUE);
        }
    }

    LogbookRecord getFields() {
        String s;
        if (!isLogbookReady()) {
            return null;
        }

        // EntryNo
        LogbookRecord r = logbook.createLogbookRecord(DataTypeIntString.parseString(entryno.getValue()));

        // Date
        if (date.isSet()) {
            r.setDate(date.getDate());
        }

        // End Date
        if (enddate.isSet()) {
            r.setEndDate(enddate.getDate());
        }

        // Boat & Boat Variant
        BoatRecord b = findBoat(-1);
        if (b != null) {
            r.setBoatId(b.getId());
            r.setBoatVariant(Integer.parseInt(boatvariant.getValue()));
        } else {
            s = boat.toString().trim();
            r.setBoatName( (s.length() == 0 ? null : s) );
        }

        // Cox and Crew
        for (int i=0; i<=LogbookRecord.CREW_MAX; i++) {
            PersonRecord p = findPerson(i, -1);
            if (p != null) {
                if (i == 0) {
                    r.setCoxId(p.getId());
                } else {
                    r.setCrewId(i, p.getId());
                }
            } else {
                s = getCrewItem(i).toString().trim();
                if (i == 0) {
                    r.setCoxName( (s.length() == 0 ? null : s) );
                } else {
                    r.setCrewName(i, (s.length() == 0 ? null : s) );
                }
            }
        }

        // Boat Captain
        if (boatcaptain.getValue().length() > 0) {
            r.setBoatCaptainPosition(EfaUtil.stringFindInt(boatcaptain.getValue(), 0));
        }

        // Start & End Time
        if (starttime.isSet()) {
            r.setStartTime(starttime.getTime());
        }
        if (endtime.isSet()) {
            r.setEndTime(endtime.getTime());
        }

        // Destination
        DestinationRecord d = findDestination(-1);
        if (d != null) {
            r.setDestinationId(d.getId());
        } else {
            s = destination.toString().trim();
            r.setDestinationName( (s.length() == 0 ? null : s) );
        }

        // DestinationVariant
        // @todo DestinationVariant

        // Distance
        if (distance.isSet()) {
            r.setDistance(distance.getValue());
        }

        // Comments
        s = comments.toString().trim();
        if (s.length() > 0) {
            r.setComments(s);
        }

        // Session Type
        r.setSessionType(sessiontype.toString());
        
        return r;
    }

    // Datensatz speichern
    // liefert "true", wenn erfolgreich
    boolean saveEntry() {
        if (!isLogbookReady()) {
            return false;
        }

        // Da das Hinzufügen eines Eintrags in der Bootshausversion wegen des damit verbundenen
        // Speicherns lange dauern kann, könnte ein ungeduldiger Nutzer mehrfach auf den "Hinzufügen"-
        // Button klicken. "synchronized" hilft hier nicht, da sowieso erst nach Ausführung des
        // Threads der Klick ein zweites Mal registriert wird. Da aber nach Abarbeitung dieser
        // Methode der Frame "EfaFrame" vom Stack genommen wurde und bei der zweiten Methode damit
        // schon nicht mehr auf dem Stack ist, kann eine Überprüfung, ob der aktuelle Frame
        // "EfaFrame" ist, benutzt werden, um eine doppelte Ausführung dieser Methode zu verhindern.
        if (Dialog.frameCurrent() != this) {
            return false;
        }

        // run all checks before saving this entry
        if (!checkDuplicatePersons()) {
            return false;
        }
        if (!checkPersonsForBoatType()) {
            return false;
        }
        if (!checkDuplicateEntry()) {
            return false;
        }
        if (!checkDistance()) {
            return false;
        }
        if (!checkEntryNo()) {
            return false;
        }
        if (!checkBoatCaptain()) {
            return false;
        }
        if (!checkBoatPermissions()) {
            return false;
        }
        if (!checkMultiDayTours()) {
            return false;
        }
        if (!checkAllowedDateForLogbook()) {
            return false;
        }

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

        // @todo: call direktSpeichereDatensatz() or speichereDatensatz() ???

        boolean success = saveEntryInLogbook();
        if (success) {
            setEntryUnchanged();
            entryno.requestFocus();
        }
        return success;
    }

    // den Datensatz nun wirklich speichern;
    boolean saveEntryInLogbook() {
        if (!isLogbookReady()) {
            return false;
        }

        long lock = 0;
        Exception myE = null;
        try {
            if (!isNewRecord && currentRecord != null && !currentRecord.getEntryId().equals(entryno.getValue())) {
                // Datensatz mit geänderter LfdNr: Der alte Datensatz muß gelöscht werden!
                lock = logbook.data().acquireGlobalLock();
                logbook.data().delete(currentRecord.getKey(), lock);
            }
            currentRecord = getFields();
            logbook.data().add(currentRecord, lock);
        } catch (Exception e) {
            Logger.log(e);
            myE = e;
        } finally {
            if (lock != 0) {
                logbook.data().releaseGlobalLock(lock);
            }
        }
        if (myE != null) {
            Dialog.error(International.getString("Der Fahrtenbucheintrag konnte nicht gespeichert werden.") + "\n" + myE.toString());
            return false;
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
        enddate.setUnchanged();
        boat.setUnchanged();
        boatvariant.setUnchanged();
        cox.setUnchanged();
        for (int i=0; i<crew.length; i++) {
            crew[i].setUnchanged();
        }
        boatcaptain.setUnchanged();
        starttime.setUnchanged();
        endtime.setUnchanged();
        destination.setUnchanged();
        distance.setUnchanged();
        comments.setUnchanged();
        sessiontype.setUnchanged();
    }

    boolean isEntryChanged() {
        boolean changed =
                entryno.isChanged() ||
                date.isChanged() ||
                enddate.isChanged() ||
                boat.isChanged() ||
                cox.isChanged() ||
                boatcaptain.isChanged() ||
                starttime.isChanged() ||
                endtime.isChanged() ||
                destination.isChanged() ||
                distance.isChanged() ||
                comments.isChanged() ||
                sessiontype.isChanged();
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
        if (!isLogbookReady()) {
            return;
        }
        if (item.getValueFromField().trim().length() == 0) {
            return;
        }
        BoatRecord r = findBoat(getValidAtTimestamp(null));
        if (isModeBoathouse() || getMode() == MODE_ADMIN_SESSIONS) {
            if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen.getValue() || r != null) {
                return; // only add new boats (if allowed), but don't edit existing ones
            }
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        boolean newRecord = (r == null);
        if (r == null) {
            r = Daten.project.getBoats(false).createBoatRecord(UUID.randomUUID());
        }
        BoatEditDialog dlg = new BoatEditDialog(this, r, newRecord);
        dlg.showDialog();
        setFields(this.currentRecord);
        startBringToFront(false);
    }

    void editPerson(ItemTypeStringAutoComplete item) {
        if (!isLogbookReady()) {
            return;
        }
        if (item.getValueFromField().trim().length() == 0) {
            return;
        }
        PersonRecord r = findPerson(item, getValidAtTimestamp(null));
        if (isModeBoathouse() || getMode() == MODE_ADMIN_SESSIONS) {
            if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen.getValue() || r != null) {
                return; // only add new persons (if allowed), but don't edit existing ones
            }
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        boolean newRecord = (r == null);
        if (r == null) {
            r = Daten.project.getPersons(false).createPersonRecord(UUID.randomUUID());
        }
        PersonEditDialog dlg = new PersonEditDialog(this, r, newRecord);
        dlg.showDialog();
        setFields(this.currentRecord);
        startBringToFront(false);
    }

    void editDestination(ItemTypeStringAutoComplete item) {
        if (!isLogbookReady()) {
            return;
        }
        if (item.getValueFromField().trim().length() == 0) {
            return;
        }
        DestinationRecord r = findDestination(getValidAtTimestamp(null));
        if (isModeBoathouse() || getMode() == MODE_ADMIN_SESSIONS) {
            if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenNamenHinzufuegen.getValue() || r != null) {
                return; // only add new destinations (if allowed), but don't edit existing ones
            }
        }
        if (!promptSaveChangesOk()) {
            return;
        }
        boolean newRecord = (r == null);
        if (r == null) {
            r = Daten.project.getDestinations(false).createDestinationRecord(UUID.randomUUID());
        }
        DestinationEditDialog dlg = new DestinationEditDialog(this, r, newRecord);
        dlg.showDialog();
        setFields(this.currentRecord);
        startBringToFront(false);
    }

    // =========================================================================
    // Save Entry Checks
    // =========================================================================

    private boolean checkDuplicatePersons() {
        // Ruderer auf doppelte prüfen
        Hashtable h = new Hashtable();
        String s;
        String doppelt = null; // Ergebnis doppelt==null heißt ok, doppelt!=null heißt Fehler! ;-)
        while (true) { // Unsauber; aber die Alternative wäre ein goto; dies ist keine Schleife!!
            PersonRecord r;
            for (int i=0; i <= LogbookRecord.CREW_MAX; i++) {
                r = findPerson(i, getValidAtTimestamp(null));
                if (r != null) {
                    UUID id = r.getId();
                    if (h.get(id) == null) {
                        h.put(id, "");
                    } else {
                        doppelt = r.getQualifiedName();
                        break;
                    }
                }
            }
            break; // alles ok, keine doppelten --> Pseudoschleife abbrechen
        }
        if (doppelt != null) {
            Dialog.error(International.getMessage("Die Person '{name}' wurde mehrfach eingegeben!", doppelt));
            startBringToFront(false); // efaDirekt im BRC -- Workaround
            return false;
        }
        return true;
    }

    private boolean checkPersonsForBoatType() {
        // bei steuermannslosen Booten keinen Steuermann eingeben
        if (cox.getValueFromField().trim().length() > 0 && currentBoatTypeCoxing != null) {
            if (currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS)) {
                int ret = Dialog.yesNoDialog(International.getString("Steuermann"),
                        International.getString("Du hast für ein steuermannsloses Boot einen Steuermann eingetragen. "
                        + "Möchtest Du diesen Eintrag dennoch speichern?"));
                startBringToFront(false); // efaDirekt im BRC -- Workaround
                if (ret != Dialog.YES) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkDuplicateEntry() {
        // Prüfen, ob ein Doppeleintrag vorliegt
        /* @todo
        if (isDirectMode()) {
            DatenFelder dop = findDoppeleintrag(getFields(null, null, null), Daten.fahrtenbuch.getCompleteLast(), -25); // letzte 25 Einträge nach Doppeleinträgen durchsuchen
            if (dop != null) {
                Vector v = getBootsbesatzung(dop);
                String m = "";
                for (int i = 0; i < v.size(); i++) {
                    m += (m.length() > 0 ? "; " : "") + v.get(i);
                }
                switch (Dialog.auswahlDialog(International.getString("Doppeleintrag?"),
                        International.getString("efa hat einen ähnlichen Eintrag im Fahrtenbuch gefunden.") + "\n"
                        + International.getString("Eventuell hast Du oder jemand anderes die Fahrt bereits eingetragen.") + "\n\n"
                        + International.getString("Vorhandener Eintrag:") + "\n"
                        + International.getMessage("#{entry} vom {date} mit {boat}",
                        dop.get(Fahrtenbuch.LFDNR), dop.get(Fahrtenbuch.DATUM), dop.get(Fahrtenbuch.BOOT)) + ":\n"
                        + International.getString("Mannschaft") + ": " + m + "\n"
                        + International.getString("Abfahrt") + ": " + dop.get(Fahrtenbuch.ABFAHRT) + "; "
                        + International.getString("Ankunft") + ": " + dop.get(Fahrtenbuch.ANKUNFT) + "; "
                        + International.getString("Ziel") + ": " + dop.get(Fahrtenbuch.ZIEL) + " (" + dop.get(Fahrtenbuch.BOOTSKM) + " Km)" + "\n\n"
                        + International.getString("Bitte füge den aktuellen Eintrag nur hinzu, falls es sich NICHT um einen Doppeleintrag handelt.") + "\n"
                        + International.getString("Was möchtest Du tun?"),
                        International.getString("Eintrag hinzufügen")
                        + " (" + International.getString("kein Doppeleintrag") + ")",
                        International.getString("Eintrag NICHT hinzufügen")
                        + " (" + International.getString("Doppeleintrag") + ")",
                        International.getString("Zurück zum Eintrag"))) {
                    case 0: // kein Doppeleintrag: Hinzufügen
                        break;
                    case 1: // Doppeleintrag: NICHT hinzufügen
                        cancel();
                        return;
                    default: // Zurück zum Eintrag
                        startBringToFront(false); // efaDirekt im BRC -- Workaround
                        return;
                }
            }
        }
        */
        return true;
    }

    private boolean checkDistance() {
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
        return true;
    }

    private boolean checkEntryNo() {
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
                Logger.logdebug(e);
            }
            if (newEntryNo.compareTo(highestEntryNo) <= 0 && !isInsertedRecord) {
                boolean printWarnung = true;
                if (entryNoForNewEntry > 0 && newEntryNo.intValue() == entryNoForNewEntry + 1) {
                    printWarnung = false;
                }
                if (printWarnung && // nur warnen, wenn das erste Mal eine zu kleine LfdNr eingegeben wurde!
                        Dialog.yesNoDialog(International.getString("Warnung"),
                        International.getString("Die Laufende Nummer dieses Eintrags ist kleiner als die des "
                        + "letzten Eintrags.") + " " +
                        International.getString("Möchtest Du den Eintrag so speichern?")) == Dialog.NO) {
                    entryno.requestFocus();
                    startBringToFront(false); // efaDirekt im BRC -- Workaround
                    return false;
                }
            }
            entryNoForNewEntry = EfaUtil.string2date(entryno.getValue(), 1, 1, 1).tag; // lfdNr merken, nächster Eintrag erhält dann per default diese Nummer + 1
        } else { // geänderter Fahrtenbucheintrag
            if (!currentRecord.getEntryId().toString().equals(entryno.toString())) {
                if (Dialog.yesNoDialog(International.getString("Warnung"),
                        International.getString("Du hast die Laufende Nummer dieses Eintrags verändert!") + " " +
                        International.getString("Möchtest Du den Eintrag so speichern?")) == Dialog.NO) {
                    entryno.requestFocus();
                    startBringToFront(false); // efaDirekt im BRC -- Workaround
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkBoatCaptain() {
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
        return true;
    }

    private boolean checkBoatPermissions() {
        if (getMode() == MODE_BOATHOUSE_START || getMode() == MODE_BOATHOUSE_START_CORRECT) {
            // checkFahrtbeginnFuerBoot nur bei direkt_boot==null machen, da ansonsten der Check schon in EfaDirektFrame gemacht wurde
            /* @todo
            if (direkt_boot == null && !efaDirektFrame.checkFahrtbeginnFuerBoot(boot.getText().trim(), 2)) {
                return false;
            }
            */
        }
        return true;
    }

    private boolean checkMultiDayTours() {
        /* @todo
    // Prüfen, ob Eintrag einer Mehrtagesfahrt vorliegt und das Datum in den Zeitraum der Mehrtagesfahrt fällt
    Mehrtagesfahrt mtour = null;
    if (Daten.efaTypes != null &&
        (Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, fahrtart.getSelectedIndex()) == null ||
         Daten.efaTypes.getType(EfaTypes.CATEGORY_SESSION, fahrtart.getSelectedIndex()).equals(EfaTypes.TYPE_SESSION_MULTIDAY))) {
        mtour = Daten.fahrtenbuch.getMehrtagesfahrt((String)fahrtart.getSelectedItem());
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
        return true;
    }

    private boolean checkAllowedDateForLogbook() {
        long tRec = getValidAtTimestamp(null);
        if (tRec < logbookValidFrom || tRec >= logbookInvalidFrom) {
            String msg = International.getMessage("Der Eintrag kann nicht gespeichert werden, da er außerhalb des gültigen Zeitraums ({startdate} - {enddate}) " +
                    "für dieses Fahrtenbuch liegt.", logbook.getStartDate().toString(), logbook.getEndDate().toString());
            Logger.log(Logger.WARNING, Logger.MSG_EVT_ERRORADDRECORDOUTOFRANGE, msg+" (" + getFields().toString() + ")");
            Dialog.error(msg);
            date.requestFocus();
            startBringToFront(false); // efaDirekt im BRC -- Workaround
            return false;
        }
        return true;
    }

    // =========================================================================
    // Menu Actions
    // =========================================================================

    void menuFile_newProject_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        Project oldProject = Daten.project;
        NewProjectDialog dlg = new NewProjectDialog(this);
        String logbookName = dlg.createNewProjectAndLogbook();
        if (Daten.project != null && Daten.project != oldProject) {
            if (Daten.project != null && !isModeBoathouse()) {
                Daten.efaConfig.lastProjectEfaBase.setValue(Daten.project.getProjectName());
            }
            if (Daten.project != null && Daten.project.getCurrentLogbookEfaBase() != null) {
                openLogbook(Daten.project.getCurrentLogbookEfaBase());
            }
            openLogbook(logbookName);
        }
        setTitle();
        startBringToFront(false);
    }

    void menuFile_openProject_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
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
        if (!isModeFull() || !promptSaveChangesOk()) {
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

    void menuAdministrationConfiguration_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        EfaConfigDialog dlg = new EfaConfigDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationBoats_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        BoatListDialog dlg = new BoatListDialog(this, -1);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationBoatStatus_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        BoatStatusListDialog dlg = new BoatStatusListDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationBoatReservations_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        BoatReservationListDialog dlg = new BoatReservationListDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationBoatDamages_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        BoatDamageListDialog dlg = new BoatDamageListDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationPersons_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        PersonListDialog dlg = new PersonListDialog(this, -1);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationStatus_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        StatusListDialog dlg = new StatusListDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationGroups_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        GroupListDialog dlg = new GroupListDialog(this, -1);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationCrews_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        CrewListDialog dlg = new CrewListDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationFahrtenabzeichen_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        FahrtenabzeichenListDialog dlg = new FahrtenabzeichenListDialog(this);
        dlg.showDialog();
        startBringToFront(false);
    }


    void menuAdministrationDestinations_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        DestinationListDialog dlg = new DestinationListDialog(this, -1);
        dlg.showDialog();
        startBringToFront(false);
    }

    void menuAdministrationWaters_actionPerformed(ActionEvent e) {
        if (!isModeFull() || !promptSaveChangesOk()) {
            return;
        }
        WatersListDialog dlg = new WatersListDialog(this);
        dlg.showDialog();
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

    void createNewRecord(boolean insertAtCurrentPosition) {
        if (!isModeFull() || !isLogbookReady()) {
            return;
        }
        if (currentRecord == null) {
            return; // new record already created
        }
        if (!promptSaveChangesOk()) {
            return;
        }

        String currentEntryNo = null;
        if (insertAtCurrentPosition && currentRecord != null && currentRecord.getEntryId() != null) {
            currentEntryNo = currentRecord.getEntryId().toString();
            if (!isModeBase() && Daten.project.getBoatStatus(false).areBoatsOutOnTheWater()) {
                Dialog.error(International.getString("Es sind noch Boote unterwegs. "
                        + "Das Einfügen von Einträgen ist nur möglich, wenn alle laufenden Fahrten beendet sind."));
                startBringToFront(false);
                return;
            }

            int ret = Dialog.yesNoDialog(International.getString("Eintrag einfügen"),
                    International.getMessage("Soll vor dem aktuellen Eintrag (Lfd. Nr. {lfdnr}) wirklich ein neuer Eintrag eingefügt werden?\n"
                    + "Alle nachfolgenden laufenden Nummern werden dann um eins erhöht!", currentEntryNo));
            startBringToFront(false); // efaDirekt im BRC -- Workaround
            if (ret != Dialog.YES) {
                return;
            }
            long lock = -1;
            try {
                lock = logbook.data().acquireGlobalLock();
                DataKeyIterator it = logbook.data().getStaticIterator();
                DataKey k = it.getLast();
                while (k != null) {
                    LogbookRecord r = logbook.getLogbookRecord(k);

                    // calculate new entryNo
                    String entryNo = r.getEntryId().toString();
                    int    entryNoi = EfaUtil.stringFindInt(entryNo, 0);
                    String entryNoc = entryNo.substring(Integer.toString(entryNoi).length());
                    r.setEntryId(DataTypeIntString.parseString(Integer.toString(++entryNoi) + entryNoc));

                    // change entry
                    logbook.data().delete(k, lock);
                    logbook.data().add(r, lock);

                    if (currentEntryNo.equals(r.getEntryId().toString())) {
                        break;
                    }
                    k = it.getPrev();
                }
            } catch(Exception e) {
                Logger.logdebug(e);
                Dialog.error(e.toString());
            } finally {
                if (lock != -1) {
                    logbook.data().releaseGlobalLock(lock);
                }
            }
        }

        setFields(null);

        // calculate new EntryID for new record
        if (insertAtCurrentPosition) {
            entryno.parseAndShowValue(currentEntryNo);
        } else {
            String n;
            if (!isModeBoathouse() && entryNoForNewEntry > 0) {
                n = Integer.toString(entryNoForNewEntry + 1);
            } else {
                LogbookRecord lastrec = null;
                try {
                    lastrec = (LogbookRecord) logbook.data().getLast();
                } catch (Exception e) {
                    Logger.logdebug(e);
                }
                if (lastrec != null && lastrec.getEntryId() != null) {
                    n = Integer.toString(EfaUtil.stringFindInt(lastrec.getEntryId().toString(), 0) + 1);
                } else {
                    n = "1";
                }
            }
            entryno.parseAndShowValue(n);
        }

        // set Date
        String d;
        if (referenceRecord != null && referenceRecord.getDate() != null) {
            d = referenceRecord.getDate().toString();
        } else {
            d = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
        }
        date.parseAndShowValue(d);

        if (isModeAdmin()) {
            Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYADDED,
                    International.getString("Admin") + ": "
                    + International.getMessage("Neuer Fahrtenbuch-Eintrag #{lfdnr} wurde erstellt.", entryno.getValueFromField()));
        }
        startBringToFront(false);
    }

    void deleteRecord() {
        if (!isModeFull() || !isLogbookReady()) {
            return;
        }
        String entryNo = null;
        if (currentRecord != null && currentRecord.getEntryId() != null && currentRecord.getEntryId().toString().length() > 0) {
            entryNo = currentRecord.getEntryId().toString();
        }
        if (entryNo == null) {
            return;
        }
        if (Dialog.yesNoDialog(International.getString("Wirklich löschen?"),
                International.getString("Möchtest Du den aktuellen Eintrag wirklich löschen?")) == Dialog.YES) {
            try {
                logbook.data().delete(currentRecord.getKey());
                if (isModeAdmin()) {
                    Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYDELETED,
                            International.getString("Admin") + ": "
                            + International.getString("Fahrtenbuch-Eintrag") + " #" + entryNo + " wurde gelöscht.");
                }
            } catch(Exception e) {
                Logger.logdebug(e);
                Dialog.error(e.toString());
            }

            LogbookRecord r = logbook.getLogbookRecord(iterator.getCurrent());
            if (r == null) {
                r = logbook.getLogbookRecord(iterator.getLast());
            }
            setFields(r);
        }
        startBringToFront(false); // efaDirekt im BRC -- Workaround
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
            if (item == saveButton) {
                saveEntry();
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
            showHint(null);
            lastFocusedItem = item;
            if (item == boat) {
                currentBoatUpdateGui();
            }
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
            infoLabel.setText(" ");
            return;
        }
        if (s.equals(LogbookRecord.ENTRYID)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Laufende Nummer") + ">");
            return;
        }
        if (s.equals(LogbookRecord.DATE) || s.equals(LogbookRecord.ENDDATE)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Tag") + ">.<"
                    + International.getString("Monat") + ">.<"
                    + International.getString("Jahr") + ">");
            return;
        }
        if (s.equals(LogbookRecord.BOATNAME)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Bootsname") + ">");
            return;
        }
        if (s.equals(LogbookRecord.BOATVARIANT)) {
            infoLabel.setText(International.getString("Bitte auswählen")
                    + ": " + International.getString("Bootsvariante"));
            return;
        }
        if (LogbookRecord.getCrewNoFromFieldName(s) >= 0) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + (Daten.efaConfig.nameFormat.equals(EfaConfig.NAMEFORMAT_FIRSTLAST)
                    ? "<" + International.getString("Vorname") + "> <"
                    + International.getString("Nachname") + ">"
                    : "<" + International.getString("Nachname") + ">,  <"
                    + International.getString("Vorname") + ">"));
            return;
        }
        if (s.equals(LogbookRecord.BOATCAPTAIN)) {
            infoLabel.setText(International.getString("Bitte auswählen")
                    + ": " + International.getString("verantwortlichen Obmann"));
            return;
        }
        if (s.equals(LogbookRecord.STARTTIME) || s.equals(LogbookRecord.ENDTIME)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Stunde") + ">:<"
                    + International.getString("Minute") + ">");
            return;
        }
        if (s.equals(LogbookRecord.DESTINATIONNAME)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Ziel der Fahrt") + ">");
            return;
        }
        if (s.equals(LogbookRecord.DISTANCE)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Kilometer") + ">");
            return;
        }
        if (s.equals(LogbookRecord.COMMENTS)) {
            infoLabel.setText(International.getString("Bemerkungen eingeben oder frei lassen"));
            return;
        }
        if (s.equals(LogbookRecord.SESSIONTYPE)) {
            infoLabel.setText(International.getString("Bitte auswählen")
                    + ": " + International.getString("Art der Fahrt"));
            return;
        }
        if (s.equals("REMAININGCREWUP") || s.equals("REMAININGCREWDOWN")) {
            infoLabel.setText(International.getString("weitere Mannschaftsfelder anzeigen"));
            return;
        }
        if (s.equals("BOATDAMAGE")) {
            infoLabel.setText(International.getString("einen Schaden am Boot melden"));
            return;
        }
        if (s.equals("SAVE")) {
            infoLabel.setText(International.getString("<Leertaste> drücken, um den Eintrag abzuschließen"));
            return;
        }
        infoLabel.setText(" ");
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

    private void currentBoatUpdateGuiBoathouse(boolean isCoxed, int numCrew) {
        // Steuermann wird bei steuermannslosen Booten immer disabled (unabhängig von Konfigurationseinstellung)
        setFieldEnabled(isCoxed, isCoxed, cox);
        if (!isCoxed) {
            cox.parseAndShowValue("");
            if (getBoatCaptain() == 0) {
                setBoatCaptain(-1, true);
            }
        }
        if (Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl.getValue()) {
            for (int i = 1; i <= LogbookRecord.CREW_MAX; i++) {
                setFieldEnabled(i <= numCrew, i <= numCrew, crew[i-1]);
                if (i > numCrew) {
                    crew[i-1].parseAndShowValue("");
                    if (getBoatCaptain() == i) {
                        setBoatCaptain(-1, true);
                    }
                }
            }
        }

        // "Weiterere Mannschaft"-Button ggf. ausblenden
        setFieldEnabled(true, numCrew > 8 || !Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl.getValue(), remainingCrewUpButton);
        setFieldEnabled(true, numCrew > 8 || !Daten.efaConfig.efaDirekt_eintragErlaubeNurMaxRudererzahl.getValue(), remainingCrewDownButton);

        // "Obmann" ggf. ausblenden
        setFieldEnabled(true, isCoxed || numCrew > 1, boatcaptain);

        // Bezeichnung für Mannschaftsfelder anpassen
        if (numCrew != 1 || isCoxed) {
            crew[0].setDescription(crew1defaultText);
        } else {
            crew[0].setDescription(International.getString("Name"));
        }
    }

    // wird von boot_focusLost aufgerufen, sowie vom FocusManager! (irgendwie unsauber, da bei <Tab> doppelt...
    void currentBoatUpdateGui() {
        currentBoat = null;
        currentBoatTypeSeats = null;
        currentBoatTypeCoxing = null;
        if (!isLogbookReady()) {
            return;
        }

        int seats = -1;
        try {
            BoatRecord b = findBoat(getValidAtTimestamp(null));
            if (b != null) {
                currentBoat = b;
                int variant = EfaUtil.stringFindInt(boatvariant.toString(), -1);
                int idx = b.getVariantIndex(variant);
                currentBoatTypeSeats = b.getTypeSeats(idx);
                currentBoatTypeCoxing = b.getTypeCoxing(idx);
                seats = b.getNumberOfSeats(idx);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }

        // Update Boat Type selection
        updateBoatVariant(currentBoat, -1);

        if (isModeBoathouse()) {
            boolean isCoxed = (currentBoatTypeCoxing == null || currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS));
            int numCrew = (seats <= 0 ? LogbookRecord.CREW_MAX : seats);
            currentBoatUpdateGuiBoathouse(isCoxed, numCrew);
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
            Logger.logdebug(e);
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
                    Logger.logdebug(ee);
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
                Logger.logdebug(ee);
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
