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
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.gui.dataedit.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EfaBaseFrame extends BaseDialog implements IItemListener {

    public static final int MODE_BASE = 0;
    public static final int MODE_BOATHOUSE = 1;
    public static final int MODE_BOATHOUSE_START = 2;
    public static final int MODE_BOATHOUSE_START_CORRECT = 3;
    public static final int MODE_BOATHOUSE_FINISH = 4;
    public static final int MODE_BOATHOUSE_LATEENTRY = 5;
    public static final int MODE_BOATHOUSE_ABORT = 6;
    public static final int MODE_ADMIN = 7;
    public static final int MODE_ADMIN_SESSIONS = 8;
    private int mode;

    // =========================================================================
    // GUI Elements
    // =========================================================================

    // Menu Bar
    JMenuBar menuBar = new JMenuBar();

    // Toolbar
    JToolBar toolBar = new JToolBar();
    JButton toolBar_firstButton = new JButton();
    JButton toolBar_prevButton = new JButton();
    JButton toolBar_nextButton = new JButton();
    JButton toolBar_lastButton = new JButton();
    JButton toolBar_newButton = new JButton();
    // @remove JButton toolBar_insertButton = new JButton();
    JButton toolBar_deleteButton = new JButton();
    JButton toolBar_searchButton = new JButton();
    JTextField toolBar_goToEntry = new JTextField();

    // Data Fields
    ItemTypeString entryno;
    ItemTypeLabel opensession;
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
    ItemTypeStringAutoComplete sessiongroup;

    // Supplementary Elements
    ItemTypeButton remainingCrewUpButton;
    ItemTypeButton remainingCrewDownButton;
    ItemTypeButton boatDamageButton;
    ItemTypeButton saveButton;
    JLabel infoLabel = new JLabel();
    String KEYACTION_F3;
    String KEYACTION_F4;

    // Internal Data Structures
    Logbook logbook;                // this logbook
    AdminRecord admin;
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
    String currentBoatTypeCoxing;   // boat type for currentBoat
    int currentBoatNumberOfSeats;   // boat type for currentBoa
    String lastDestination = "";    // zum Vergleichen, ob Ziel geändert wurde
    int crewRangeSelection = 0;     // mannschAuswahl = 0: 1-8 sichtbar; 1: 9-16 sichtbar; 2: 17-24 sichtbar
    String crew1defaultText = null; // mannsch1_label_defaultText = der Standardtext, den das Label "Mannschaft 1: " normalerweise haben soll (wenn es nicht für Einer auf "Name: " gesetzt wird)
    IItemType lastFocusedItem;
    private volatile boolean _inUpdateBoatVariant = false;
    AutoCompleteList autoCompleteListBoats = new AutoCompleteList();
    AutoCompleteList autoCompleteListPersons = new AutoCompleteList();
    AutoCompleteList autoCompleteListDestinations = new AutoCompleteList();
    EfaBaseFrameFocusManager efaBaseFrameFocusManager;

    // Internal Data Structures for EfaBoathouse
    EfaBoathouseFrame efaBoathouseFrame;
    AdminDialog adminDialog;
    ItemTypeBoatstatusList.BoatListItem efaBoathouseAction;
    int positionX,positionY;      // Position des Frames, wenn aus efaDirekt aufgerufen


    public EfaBaseFrame(int mode) {
        //super(null, Daten.EFA_LONGNAME);
        super((JFrame)null, Daten.EFA_LONGNAME, null);
        this.mode = mode;
    }

    public EfaBaseFrame(JDialog parent, int mode) {
        //super(null, Daten.EFA_LONGNAME);
        super(parent, Daten.EFA_LONGNAME, null);
        this.mode = mode;
    }

    public EfaBaseFrame(EfaBoathouseFrame efaBoathouseFrame, int mode) {
        //super(efaBoathouseFrame, Daten.EFA_LONGNAME);
        super(efaBoathouseFrame, Daten.EFA_LONGNAME, null);
        this.efaBoathouseFrame = efaBoathouseFrame;
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
               getMode() == MODE_BOATHOUSE_LATEENTRY ||
               getMode() == MODE_BOATHOUSE_ABORT;
    }

    public boolean isModeAdmin() {
        return getMode() == MODE_ADMIN ||
               getMode() == MODE_ADMIN_SESSIONS;
    }

    public void _keyAction(ActionEvent evt) {
        if (evt.getActionCommand().equals(KEYACTION_F3)) {
            SearchLogbookDialog.search();
        }
        if (evt.getActionCommand().equals(KEYACTION_F4)) {
            if (currentBoat != null && currentBoat.getDefaultCrewId() != null) {
                setDefaultCrew(currentBoat.getDefaultCrewId());
            }
        }
        super._keyAction(evt);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void packFrame(String method) {
        this.pack();
    }

    public void setFixedLocationAndSize() {
        Dialog.setDlgLocation(this);
        Dimension dlgSize = getSize();
        setMinimumSize(dlgSize);
        setMaximumSize(dlgSize);
    }
    
    protected void iniDialog() {
        if (isModeBase() && admin == null) {
            iniAdmin();
        }
        iniGuiBase();
        if (isModeBase()) {
            iniGuiMenu();
        }
        if (isModeFull()) {
            iniGuiToolbar();
        }
        iniGuiMain();
        iniGuiRemaining();
        iniApplication();
        if (isModeBase()) {
            Daten.iniSplashScreen(false);
        }
    }

    public void setAdmin(AdminRecord admin) {
        this.admin = admin;
    }

    private void iniAdmin() {
        admin = AdminLoginDialog.login(null, Daten.APPLNAME_EFA);
        if (admin == null || !admin.isAllowedEditLogbook()) {
            if (admin != null) {
                EfaMenuButton.insufficientRights(admin, International.getString("Fahrtenbuch bearbeiten"));
            }
            super.cancel();
            Daten.haltProgram(Daten.HALT_ADMINLOGIN);
        }
    }

    private void iniGuiBase() {
        setIconImage(Toolkit.getDefaultToolkit().createImage(EfaBaseFrame.class.getResource("/de/nmichael/efa/img/efa_icon.png")));
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
        KEYACTION_F3 = addKeyAction("F3");
        KEYACTION_F4 = addKeyAction("F4");
    }




    private void iniGuiMenu() {
        Vector<EfaMenuButton> menuButtons = EfaMenuButton.getAllMenuButtons(admin, false);
        String lastMenuName = null;
        JMenu menu = null;
        for (EfaMenuButton menuButton : menuButtons) {
            if (!menuButton.getMenuName().equals(lastMenuName)) {
                if (menu != null) {
                    menuBar.add(menu);
                }
                // New Menu
                menu = new JMenu();
                Mnemonics.setButton(this, menu, menuButton.getMenuText());
                lastMenuName = menuButton.getMenuName();
            }
            if (menuButton.getButtonName().equals(EfaMenuButton.SEPARATOR)) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem();
                Mnemonics.setMenuButton(this, menuItem, menuButton.getButtonText());
                if (menuButton.getIcon() != null) {
                    setIcon(menuItem, menuButton.getIcon());
                }
                menuItem.setActionCommand(menuButton.getButtonName());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        menuActionPerformed(e);
                    }
                });
                menu.add(menuItem);
            }
        }
        menuBar.add(menu);
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

        /* @remove
        toolBar_insertButton.setMargin(new Insets(2, 3, 2, 3));
        Mnemonics.setButton(this, toolBar_insertButton, International.getStringWithMnemonic("Einfügen"));
        toolBar_insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewRecord(true);
            }
        });
        */

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
                searchLogbook();
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
        // @remove toolBar.add(toolBar_insertButton, null);
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
        entryno.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        entryno.displayOnGui(this, mainInputPanel, 0, 0);
        entryno.registerItemListener(this);

        // Open Session
        opensession = new ItemTypeLabel(LogbookRecord.OPEN, IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Fahrt offen (Boot unterwegs)"));
        opensession.setColor(Color.red);
        opensession.setFieldGrid(4, 1, -1, -1);
        opensession.displayOnGui(this, mainInputPanel, 5, 0);
        opensession.setVisible(false);

        // Date
        date = new ItemTypeDate(LogbookRecord.DATE, new DataTypeDate(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Datum"));
        date.showWeekday(true);
        date.setFieldSize(100, 19);
        date.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        date.setFieldGrid(1, GridBagConstraints.WEST, GridBagConstraints.NONE);
        date.setWeekdayGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        date.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        date.displayOnGui(this, mainInputPanel, 0, 1);
        date.registerItemListener(this);

        // End Date
        enddate = new ItemTypeDate(LogbookRecord.ENDDATE, new DataTypeDate(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("bis"));
        enddate.setMustBeAfter(date, false);
        enddate.showWeekday(true);
        enddate.setFieldSize(100, 19);
        enddate.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        enddate.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        enddate.setWeekdayGrid(1, GridBagConstraints.WEST, GridBagConstraints.NONE);
        enddate.showOptional(true);
        if (isModeBoathouse()) {
            enddate.setOptionalButtonText("+ " + International.getString("Enddatum"));
        }
        enddate.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        enddate.displayOnGui(this, mainInputPanel, 4, 1);
        enddate.registerItemListener(this);

        // Boat
        boat = new ItemTypeStringAutoComplete(LogbookRecord.BOATNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Boot"), true);
        boat.setFieldSize(200, 19);
        boat.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boat.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        boat.setAutoCompleteData(autoCompleteListBoats);
        boat.setChecks(true, true);
        boat.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        boat.displayOnGui(this, mainInputPanel, 0, 2);
        boat.registerItemListener(this);

        // Boat Variant
        boatvariant = new ItemTypeStringList(LogbookRecord.BOATVARIANT, "",
                null, null,
                IItemType.TYPE_PUBLIC, null, International.getString("Variante"));
        boatvariant.setFieldSize(80, 17);
        boatvariant.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        boatvariant.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        boatvariant.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        boatvariant.displayOnGui(this, mainInputPanel, 0, 3);
        //boatvariant.displayOnGui(this, mainInputPanel, 5, 2);
        boatvariant.registerItemListener(this);

        // Cox
        cox = new ItemTypeStringAutoComplete(LogbookRecord.COXNAME, "", IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Steuermann"), true);
        cox.setFieldSize(200, 19);
        cox.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        cox.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        cox.setAutoCompleteData(autoCompleteListPersons);
        cox.setChecks(true, true);
        cox.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        cox.displayOnGui(this, mainInputPanel, 0, 4);
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
            crew[j].setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
            crew[j].displayOnGui(this, mainInputPanel, (left ? 0 : 4), 5 + j%4);
            crew[j].setVisible(j < 8);
            crew[j].registerItemListener(this);
        }
        crew1defaultText = crew[0].getDescription();

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
        boatcaptain.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        boatcaptain.displayOnGui(this, mainInputPanel, 5, 4);
        boatcaptain.registerItemListener(this);
        if (isModeBoathouse()) {
            boatcaptain.setVisible(Daten.efaConfig.getValueShowObmann());
        }

        // StartTime
        starttime = new ItemTypeTime(LogbookRecord.STARTTIME, new DataTypeTime(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Abfahrt"));
        starttime.setFieldSize(200, 19);
        starttime.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        starttime.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        starttime.enableSeconds(false);
        starttime.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        starttime.displayOnGui(this, mainInputPanel, 0, 9);
        starttime.registerItemListener(this);

        // EndTime
        endtime = new ItemTypeTime(LogbookRecord.ENDTIME, new DataTypeTime(), IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Ankunft"));
        endtime.setFieldSize(200, 19);
        endtime.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        endtime.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        endtime.enableSeconds(false);
        endtime.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        endtime.displayOnGui(this, mainInputPanel, 0, 10);
        endtime.registerItemListener(this);

        // Destination
        destination = new ItemTypeStringAutoComplete(LogbookRecord.DESTINATIONNAME, "", IItemType.TYPE_PUBLIC, null, 
                International.getStringWithMnemonic("Ziel") + " / " +
                International.getStringWithMnemonic("Strecke"), true);
        destination.setFieldSize(400, 19);
        destination.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        destination.setFieldGrid(7, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        destination.setAutoCompleteData(autoCompleteListDestinations);
        destination.setChecks(true, false);
        destination.setIgnoreEverythingAfter(DestinationRecord.DESTINATION_VARIANT_SEPARATOR.charAt(0));
        destination.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        destination.displayOnGui(this, mainInputPanel, 0, 11);
        destination.registerItemListener(this);

        // Distance
        distance = new ItemTypeDistance(LogbookRecord.DISTANCE, null, IItemType.TYPE_PUBLIC, null,
                DataTypeDistance.getDefaultUnitName());
        distance.setFieldSize(200, 19);
        distance.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        distance.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        distance.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        distance.displayOnGui(this, mainInputPanel, 0, 12);
        distance.registerItemListener(this);

        // Comments
        comments = new ItemTypeString(LogbookRecord.COMMENTS, null, IItemType.TYPE_PUBLIC, null, International.getStringWithMnemonic("Bemerkungen"));
        comments.setFieldSize(400, 19);
        comments.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        comments.setFieldGrid(7, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        comments.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        comments.displayOnGui(this, mainInputPanel, 0, 13);
        comments.registerItemListener(this);

        // Session Type
        sessiontype = new ItemTypeStringList(LogbookRecord.SESSIONTYPE, EfaTypes.TYPE_SESSION_NORMAL,
                EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeSessionTypeArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, null, International.getString("Fahrtart"));
        sessiontype.setFieldSize(200, 19);
        sessiontype.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        sessiontype.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        sessiontype.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        sessiontype.displayOnGui(this, mainInputPanel, 0, 14);
        sessiontype.registerItemListener(this);

        // Session Type
        sessiongroup = new ItemTypeStringAutoComplete(LogbookRecord.SESSIONGROUPID,
                "", IItemType.TYPE_PUBLIC, null,
                International.getStringWithMnemonic("Fahrtgruppe"), true);
        sessiongroup.setFieldSize(200, 19);
        sessiongroup.setLabelGrid(1, GridBagConstraints.EAST, GridBagConstraints.NONE);
        sessiongroup.setFieldGrid(2, GridBagConstraints.WEST, GridBagConstraints.NONE);
        sessiongroup.setEditable(false);
        sessiongroup.displayOnGui(this, mainInputPanel, 0, 15);
        sessiongroup.registerItemListener(this);
        sessiongroup.setVisible(isModeFull());

        // Further Fields which are not part of Data Input

        // Remaining Crew Button
        remainingCrewUpButton = new ItemTypeButton("REMAININGCREWUP", IItemType.TYPE_PUBLIC, null, "\u2191");
        remainingCrewUpButton.setFieldSize(18, 30);
        remainingCrewUpButton.setPadding(5, 0, 3, 3);
        remainingCrewUpButton.setFieldGrid(1, 2, GridBagConstraints.WEST, GridBagConstraints.VERTICAL);
        remainingCrewUpButton.displayOnGui(this, mainInputPanel, 9, 5);
        remainingCrewUpButton.registerItemListener(this);
        remainingCrewDownButton = new ItemTypeButton("REMAININGCREWDOWN", IItemType.TYPE_PUBLIC, null, "\u2193");
        remainingCrewDownButton.setFieldSize(18, 30);
        remainingCrewDownButton.setPadding(5, 0, 3, 3);
        remainingCrewDownButton.setFieldGrid(1, 2, GridBagConstraints.WEST, GridBagConstraints.VERTICAL);
        remainingCrewDownButton.displayOnGui(this, mainInputPanel, 9, 7);
        remainingCrewDownButton.registerItemListener(this);

        // Info Label
        infoLabel.setForeground(Color.blue);
        infoLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        infoLabel.setText(" ");
        mainInputPanel.add(infoLabel,
                new GridBagConstraints(0, 16, 8, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 20, 10, 20), 0, 0));

        // Boat Damage Button
        boatDamageButton = new ItemTypeButton("BOATDAMAGE", IItemType.TYPE_PUBLIC, null, International.getString("Bootsschaden melden"));
        boatDamageButton.setFieldSize(200, 19);
        boatDamageButton.setFieldGrid(4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        boatDamageButton.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        boatDamageButton.displayOnGui(this, mainInputPanel, 4, 17);
        boatDamageButton.registerItemListener(this);
        boatDamageButton.setVisible(isModeBoathouse() && Daten.efaConfig.getValueEfaDirekt_showBootsschadenButton());

        // Save Button
        saveButton = new ItemTypeButton("SAVE", IItemType.TYPE_PUBLIC, null, International.getString("Eintrag speichern"));
        saveButton.setBackgroundColorWhenFocused(Daten.efaConfig.getValueEfaDirekt_colorizeInputField() ? Color.yellow : null);
        saveButton.displayOnGui(this, mainPanel, BorderLayout.SOUTH);
        saveButton.registerItemListener(this);
    }

    void iniGuiRemaining() {
        efaBaseFrameFocusManager = new EfaBaseFrameFocusManager(this,FocusManager.getCurrentManager());
        FocusManager.setCurrentManager(efaBaseFrameFocusManager);
        if (isModeBoathouse()) {
            setResizable(false);
        }
        if (isModeAdmin()) {
            this.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowDeactivated(WindowEvent e) {
                    this_windowDeactivated(e);
                }
            });
        }

    }

    void iniApplication() {
        if (Daten.project == null && isModeBase()) {
            if (Daten.efaConfig.getValueLastProjectEfaBase().length() > 0) {
                Project.openProject(Daten.efaConfig.getValueLastProjectEfaBase());
            }
        }
        if (Daten.project != null && isModeBase() && Daten.project.getCurrentLogbookEfaBase() != null) {
            openLogbook(Daten.project.getCurrentLogbookEfaBase());
        }
        if (Daten.project != null && isModeAdmin() && logbook != null) {
            // What a hack... ;-) openLogbook() will only open a new logbook if it is not identical with the current one.
            // Actually, there isn't really a *current* logbook. It's only the variable which has been set in the constructor.
            // So we just tweak it a bit so that openLogbook() will accept our logbook as a new one...
            Logbook newLogbook = logbook;
            this.logbook = null;
            openLogbook(newLogbook);
        }
    }




    void setTitle() {
        if (isModeBoathouse()) {
            setTitle(Daten.EFA_LONGNAME);
        } else {
            if (Daten.project == null) {
                setTitle(Daten.EFA_LONGNAME + " [" + admin.getName() + "]");
            } else {
                if (!isLogbookReady()) {
                    setTitle(Daten.project.getProjectName() + " - " + Daten.EFA_LONGNAME + " [" + admin.getName() + "]");
                } else {
                    setTitle(Daten.project.getProjectName() + ": " + logbook.getName() + " - " + Daten.EFA_LONGNAME + " [" + admin.getName() + "]");
                }
            }
        }
    }

    private void clearAllBackgroundColors() {
        entryno.restoreBackgroundColor();
        date.restoreBackgroundColor();
        enddate.restoreBackgroundColor();
        boat.restoreBackgroundColor();
        boatvariant.restoreBackgroundColor();
        cox.restoreBackgroundColor();
        for (int i=0; i<crew.length; i++) {
            crew[i].restoreBackgroundColor();
        }
        boatcaptain.restoreBackgroundColor();
        starttime.restoreBackgroundColor();
        endtime.restoreBackgroundColor();
        destination.restoreBackgroundColor();
        distance.restoreBackgroundColor();
        comments.restoreBackgroundColor();
        sessiontype.restoreBackgroundColor();
        boatDamageButton.restoreBackgroundColor();
        saveButton.restoreBackgroundColor();
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
            String s = LogbookRecord.getDestinationNameAndVariantFromString(destination.toString())[0];
            if (s.length() > 0) {
                return Daten.project.getDestinations(false).getDestination(s, logbookValidFrom, logbookInvalidFrom-1, preferredValidAt);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    String updateBoatVariant(BoatRecord b, int variant) {
        if (_inUpdateBoatVariant) {
            return null;
        }
        _inUpdateBoatVariant = true;
        try {
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
                } else {
                    if (numberOfVariants > 0 && (boatvariant.getValue() == null || boatvariant.getValue().length() == 0)) {
                        boatvariant.parseAndShowValue(bt[0]);
                    }
                }
                boatvariant.setVisible(numberOfVariants > 1);
                return boatvariant.getValue();
            }
            boatvariant.setListData(null, null);
            boatvariant.setVisible(false);
            return null;
        } finally {
            _inUpdateBoatVariant = false;
        }
    }

    void openLogbook(String logbookName) {
        if (logbookName == null || logbookName.length() == 0) {
            setFields(null);
        } else {
            Logbook newLogbook = Daten.project.getLogbook(logbookName, false);
            if (newLogbook != null) {
                openLogbook(newLogbook);
            } else {
                Dialog.error(International.getMessage("Fahrtenbuch {logbook} konnte nicht geöffnet werden.", logbookName));
                setFields(null);
            }
        }
    }

    void openLogbook(Logbook newLogbook) {
        if (Daten.project == null) {
            return;
        }
        if (newLogbook == null) {
            return;
        }
        try {
            if (logbook != null && logbook.isOpen()) {
                if (logbook.getName().equals(newLogbook.getName()) &&
                    logbook.getProject().getProjectName().equals(Daten.project.getProjectName())) {
                    return;
                }
                logbook.close();
            }
        } catch (Exception e) {
            Logger.log(e);
            Dialog.error(e.toString());
        }
        logbook = newLogbook;
        if (!isModeBoathouse()) {
            Daten.project.setCurrentLogbookEfaBase(logbook.getName());
        }
        ProjectRecord pr = Daten.project.getLoogbookRecord(logbook.getName());
        if (pr != null) {
            logbookValidFrom = pr.getStartDate().getTimestamp(null);
            logbookInvalidFrom = pr.getEndDate().getTimestamp(null) + 24 * 60 * 60 * 1000;
            if (logbookInvalidFrom < logbookValidFrom) {
                logbookInvalidFrom = logbookValidFrom + 24 * 60 * 60 * 1000;
            }
        }
        try {
            iterator = logbook.data().getDynamicIterator();
            autoCompleteListBoats.setDataAccess(Daten.project.getBoats(false).data(), logbookValidFrom, logbookInvalidFrom - 1);
            autoCompleteListPersons.setDataAccess(Daten.project.getPersons(false).data(), logbookValidFrom, logbookInvalidFrom - 1);
            autoCompleteListDestinations.setDataAccess(Daten.project.getDestinations(false).data(), logbookValidFrom, logbookInvalidFrom - 1);
        } catch (Exception e) {
            Logger.logdebug(e);
            iterator = null;
        }
        if (isModeFull()) {
            try {
                LogbookRecord r = (LogbookRecord) logbook.data().getLast();
                if (r != null) {
                    setFields(r);
                } else {
                    createNewRecord(false);
                }
                entryno.requestFocus();
            } catch (Exception e) {
                Logger.logdebug(e);
                setFields(null);
            }
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
                return (r != null ? r.getBoatAsName(getValidAtTimestamp(r)) : "");
            }
            if (field == boatvariant) {
                return updateBoatVariant((r != null ? r.getBoatRecord(getValidAtTimestamp(r)) : null), (r != null ? r.getBoatVariant() : 0));
            }
            if (field == cox) {
                return (r != null ? r.getCoxAsName(getValidAtTimestamp(r)) : "");
            }
            for (int i = 0; i < crew.length; i++) {
                if (field == crew[i]) {
                    return (r != null ? r.getCrewAsName(i + 1, getValidAtTimestamp(r)) : "");
                }
            }
            if (field == starttime) {
                return (r != null ? r.getStartTime().toString() : "");
            }
            if (field == endtime) {
                return (r != null ? r.getEndTime().toString() : "");
            }
            if (field == destination) {
                return (r != null ? r.getDestinationAndVariantName(getValidAtTimestamp(r)) : "");
            }
            if (field == distance) {
                return (r != null ? r.getDistance().getAsFormattedString() : "");
            }
            if (field == comments) {
                return (r != null ? r.getComments() : "");
            }
            if (field == sessiontype) {
                return (r != null ? r.getSessionType() : Daten.efaConfig.getValueStandardFahrtart());
            }
            if (field == sessiongroup) {
                UUID id = (r != null ? r.getSessionGroupId() : null);
                sessiongroup.setRememberedId(id);
                return Daten.project.getSessionGroups(false).getSessionGroupName(id);
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
        setField(sessiongroup,r);
        opensession.setVisible(isModeFull() && r != null && r.getSessionIsOpen());
        currentBoatUpdateGui();
        setCrewRangeSelection(0);
        setEntryUnchanged();
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
            r.setBoatVariant(EfaUtil.stringFindInt(boatvariant.getValue(), b.getTypeVariant(0)));
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
            r.setDestinationVariantName(LogbookRecord.getDestinationNameAndVariantFromString(destination.toString())[1]);
        } else {
            s = destination.toString().trim();
            r.setDestinationName( (s.length() == 0 ? null : s) );
        }

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

        // Session Group
        r.setSessionGroupId((UUID)sessiongroup.getRememberedId());
        
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
        if (!checkDuplicatePersons() ||
            !checkPersonsForBoatType() ||
            !checkDuplicateEntry() ||
            !checkEntryNo() ||
            !checkBoatCaptain() ||
            !checkBoatStatus() ||
            !checkMultiDayTours() ||
            !checkDate() ||
            !checkAllowedDateForLogbook() ||
            !checkAllDataEntered() ||
            !checkUnknownNames() ||
            !checkAllowedPersons()) {
            return false;
        }

        boolean success = saveEntryInLogbook();

        if (isModeFull()) {
            if (success) {
                setEntryUnchanged();
                entryno.requestFocus();
            }
        } else {
            finishBoathouseAction(success);
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
            boolean changeEntryNo = false;
            if (!isNewRecord && currentRecord != null && !currentRecord.getEntryId().toString().equals(entryno.toString())) {
                // Datensatz mit geänderter LfdNr: Der alte Datensatz muß gelöscht werden!
                lock = logbook.data().acquireGlobalLock();
                logbook.data().delete(currentRecord.getKey(), lock);
                changeEntryNo = true;
            }
            currentRecord = getFields();
            
            if (mode == MODE_BOATHOUSE_START || mode == MODE_BOATHOUSE_START_CORRECT) {
                currentRecord.setSessionIsOpen(true);
            } else {
                currentRecord.setSessionIsOpen(false); // all other updates to an open entry (incl. Admin Mode) will mark it as finished
            }

            if (isNewRecord || changeEntryNo) {
                logbook.data().add(currentRecord, lock);
            } else {
                logbook.data().update(currentRecord, lock);
            }
            isNewRecord = false;
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

        if (isModeFull()) {
            logAdminEvent(Logger.INFO, (isNewRecord ? Logger.MSG_ADMIN_LOGBOOK_ENTRYADDED : Logger.MSG_ADMIN_LOGBOOK_ENTRYMODIFIED),
                    (isNewRecord ? International.getString("Eintrag hinzugefügt") : International.getString("Eintrag geändert")) , currentRecord);
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
        sessiongroup.setUnchanged();
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
                sessiontype.isChanged() ||
                sessiongroup.isChanged();
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
                txt = International.getString("Der aktuelle Eintrag wurde verändert und noch nicht zum Fahrtenbuch hinzugefügt.") + "\n" +
                      International.getString("Eintrag hinzufügen?");
            } else {
                txt = International.getString("Änderungen an dem aktuellen Eintrag wurden noch nicht gespeichert.") + "\n" +
                      International.getString("Änderungen speichern?");
            }
            switch (Dialog.yesNoCancelDialog(International.getString("Eintrag nicht gespeichert"), txt)) {
                case Dialog.YES:
                    return saveEntry();
                case Dialog.NO:
                    break;
                default:
                    return false;
            }
        }
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

    void setTime(ItemTypeTime field, int addMinutes, DataTypeTime notBefore) {
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

        if (notBefore != null) {
            // Test: EndTime < StartTime (where EndTime is at most the configured (add+substract)*2 times smaller)
            if (now.isBefore(notBefore) &&
                now.getTimeAsSeconds() + (Daten.efaConfig.getValueEfaDirekt_plusMinutenAbfahrt() +
                Daten.efaConfig.getValueEfaDirekt_minusMinutenAnkunft()) * 60 * 2 >
                notBefore.getTimeAsSeconds()) {
                // use StartTime as EndTime instead (avoid overlapping times)
                now.setHour(notBefore.getHour());
                now.setMinute(notBefore.getMinute());
            }
        }

        if (addMinutes != 0) {
            field.parseAndShowValue(now.toString());
            field.setSelection(0, Integer.MAX_VALUE);
        }
    }

    void setDesinationDistance() {
        String newDestination = DestinationRecord.tryGetNameAndVariant(destination.getValueFromField().trim())[0];
        if (isModeBoathouse() && newDestination.length()>0 && distance.getValueFromField().trim().length() == 0) {
            lastDestination = "";
        }
        setFieldEnabledDistance();
        if (!destination.isKnown()) {
            if (!newDestination.equals(lastDestination)) {
                // Das "Leeren" des Kilometerfeldes darf nur im DirectMode erfolgen. Im normalen Modus hätte das
                // den unschönen Nebeneffekt, daß beim korrigieren von unbekannten Zielen die eingegeben Kilometer
                // aus dem Feld verschwinden (ebenso nach der Suche nach unvollständigen Einträgen mit unbekannten
                // Zielen).
                if (this.isModeBoathouse() && (mode == MODE_BOATHOUSE_START || mode == MODE_BOATHOUSE_START_CORRECT || mode == MODE_BOATHOUSE_FINISH || mode == MODE_BOATHOUSE_LATEENTRY)) {
                    distance.parseAndShowValue("");
                }
                lastDestination = "";
            }
            return;
        }

        if (!newDestination.equals(lastDestination) && newDestination.length() != 0 && this.isLogbookReady()) {
            // die folgende Zeile ist korrekt, da diese Methode nur nach "vervollstaendige" und bei
            // "zielButton.getBackground()!=Color.red" aus "ziel_keyReleased" oder "zielButton_focusLost"
            // aufgerufen wird und somit ein gültiger Datensatz bereits gefunden wurde!
            DestinationRecord r = findDestination(-1);
            if (r != null) {
                distance.parseAndShowValue(r.getDistance().getAsFormattedString());
            } else {
                distance.parseAndShowValue("");
            }
        }
    }

    void editBoat(ItemTypeStringAutoComplete item) {
        if (!isLogbookReady()) {
            return;
        }
        String s = item.getValueFromField().trim();
        if (s.length() == 0) {
            return;
        }
        BoatRecord r = findBoat(getValidAtTimestamp(null));
        if (isModeBoathouse() || getMode() == MODE_ADMIN_SESSIONS) {
            if (!Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenNamenHinzufuegen() || r != null) {
                return; // only add new boats (if allowed), but don't edit existing ones
            }
        }
        boolean newRecord = (r == null);
        if (r == null) {
            r = Daten.project.getBoats(false).createBoatRecord(UUID.randomUUID());
            r.addTypeVariant("", EfaTypes.TYPE_BOAT_OTHER, EfaTypes.TYPE_NUMSEATS_OTHER, EfaTypes.TYPE_RIGGING_OTHER, EfaTypes.TYPE_COXING_OTHER);
            String[] name = BoatRecord.tryGetNameAndAffix(s);
            if (name != null && name[0] != null) {
                r.setName(name[0]);
            }
            if (name != null && name[1] != null) {
                r.setNameAffix(name[1]);
            }
        }
        BoatEditDialog dlg = new BoatEditDialog(this, r, newRecord);
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            item.parseAndShowValue(r.getQualifiedName());
            item.setChanged();
            currentBoatUpdateGui();
        }
        efaBaseFrameFocusManager.focusNextItem(item, item.getComponent());
    }

    void editPerson(ItemTypeStringAutoComplete item) {
        if (!isLogbookReady()) {
            return;
        }
        String s = item.getValueFromField().trim();
        if (s.length() == 0) {
            return;
        }
        PersonRecord r = findPerson(item, getValidAtTimestamp(null));
        if (isModeBoathouse() || getMode() == MODE_ADMIN_SESSIONS) {
            if (!Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenNamenHinzufuegen() || r != null) {
                return; // only add new persons (if allowed), but don't edit existing ones
            }
        }
        boolean newRecord = (r == null);
        if (r == null) {
            r = Daten.project.getPersons(false).createPersonRecord(UUID.randomUUID());
            String[] name = PersonRecord.tryGetFirstLastNameAndAffix(s);
            if (name != null && name[0] != null) {
                r.setFirstName(name[0]);
            }
            if (name != null && name[1] != null) {
                r.setLastName(name[1]);
            }
            if (name != null && name[2] != null) {
                r.setNameAffix(name[2]);
            }
        }
        PersonEditDialog dlg = new PersonEditDialog(this, r, newRecord);
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            item.parseAndShowValue(r.getQualifiedName());
            item.setChanged();
        }
        efaBaseFrameFocusManager.focusNextItem(item, item.getComponent());
    }

    void editDestination(ItemTypeStringAutoComplete item) {
        if (!isLogbookReady()) {
            return;
        }
        String s = item.getValueFromField().trim();
        if (s.length() == 0) {
            return;
        }
        DestinationRecord r = findDestination(getValidAtTimestamp(null));
        if (isModeBoathouse() || getMode() == MODE_ADMIN_SESSIONS) {
            if (!Daten.efaConfig.getValueEfaDirekt_mitgliederDuerfenNamenHinzufuegen() || r != null) {
                return; // only add new destinations (if allowed), but don't edit existing ones
            }
        }
        boolean newRecord = (r == null);
        if (r == null) {
            r = Daten.project.getDestinations(false).createDestinationRecord(UUID.randomUUID());
            String[] name = DestinationRecord.tryGetNameAndVariant(s);
            if (name != null && name[0] != null) {
                r.setName(name[0]);
            }
        }
        DestinationEditDialog dlg = new DestinationEditDialog(this, r, newRecord);
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            item.parseAndShowValue(r.getQualifiedName());
            item.setChanged();
            setDesinationDistance();
        }
        efaBaseFrameFocusManager.focusNextItem(item, item.getComponent());
    }

    void selectSessionGroup() {
        if (!isLogbookReady()) {
            return;
        }
        UUID id = null;
        if (currentRecord != null) {
            id = currentRecord.getSessionGroupId();
        }
        SessionGroupListDialog dlg = new SessionGroupListDialog(this, logbook.getName(), id);
        dlg.showDialog();
        if (dlg.getDialogResult()) {
            SessionGroupRecord r = dlg.getSelectedSessionGroupRecord();
            if (r == null) {
                sessiongroup.parseAndShowValue("");
                sessiongroup.setRememberedId(null);
            } else {
                sessiongroup.parseAndShowValue(r.getName());
                sessiongroup.setRememberedId(r.getId());
            }
        }
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
            return false;
        }
        return true;
    }

    private boolean checkPersonsForBoatType() {
        // bei steuermannslosen Booten keinen Steuermann eingeben
        if (cox.getValueFromField().trim().length() > 0 && currentBoatTypeCoxing != null) {
            if (currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS)) {
                int ret = Dialog.yesNoDialog(International.getString("Steuermann"),
                        International.getString("Du hast für ein steuermannsloses Boot einen Steuermann eingetragen.") + "\n" +
                        International.getString("Trotzdem speichern?"));
                if (ret != Dialog.YES) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkDuplicateEntry() {
        // Prüfen, ob ein Doppeleintrag vorliegt
        if (isModeBoathouse()) {
            LogbookRecord duplicate = logbook.findDuplicateEntry(getFields(), 25); // search last 25 logbook entries for potential duplicates
            if (duplicate != null) {
                Vector<String> v = duplicate.getAllCoxAndCrewAsNames();
                String m = "";
                for (int i = 0; i < v.size(); i++) {
                    m += (m.length() > 0 ? "; " : "") + v.get(i);
                }
                switch (Dialog.auswahlDialog(International.getString("Doppeleintrag?"),
                        International.getString("efa hat einen ähnlichen Eintrag im Fahrtenbuch gefunden.") + "\n"
                        + International.getString("Eventuell hast Du oder jemand anderes die Fahrt bereits eingetragen.") + "\n\n"
                        + International.getString("Vorhandener Eintrag:") + "\n"
                        + International.getMessage("#{entry} vom {date} mit {boat}",
                        duplicate.getEntryId().toString(), duplicate.getDate().toString(), duplicate.getBoatAsName()) + ":\n"
                        + International.getString("Mannschaft") + ": " + m + "\n"
                        + International.getString("Abfahrt") + ": " + (duplicate.getStartTime() != null ? duplicate.getStartTime().toString() : "") + "; "
                        + International.getString("Ankunft") + ": " + (duplicate.getEndTime() != null ? duplicate.getEndTime().toString() : "") + "; "
                        + International.getString("Ziel") + " / " +
                          International.getString("Strecke") + ": " + duplicate.getDestinationAndVariantName() + " (" + (duplicate.getDistance() != null ? duplicate.getDistance().getAsFormattedString() : "") + " Km)" + "\n\n"
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
                        return false;
                    default: // Zurück zum Eintrag
                        return false;
                }
            }
        }
        return true;
    }

    private boolean checkEntryNo() {
        DataTypeIntString newEntryNo = DataTypeIntString.parseString(entryno.getValue());
        if ((logbook.getLogbookRecord(newEntryNo) != null && (isNewRecord || !newEntryNo.equals(currentRecord.getEntryId())))
                || newEntryNo.length() == 0) {
            Dialog.error(International.getString("Diese Laufende Nummer ist bereits vergeben! Jede Laufende "
                    + "Nummer darf nur einmal verwendet werden werden.") + " "
                    + International.getString("Bitte korrigiere die laufende Nummer des Eintrags!") + "\n\n"
                    + International.getString("Hinweis") + ": "
                    + International.getString("Um mehrere Einträge unter 'derselben' Nummer hinzuzufügen, "
                    + "füge einen Buchstaben von A bis Z direkt an die Nummer an!"));
            entryno.requestFocus();
            return false;
        }

        if (isNewRecord || currentRecord == null) {
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
                        International.getString("Trotzdem speichern?")) == Dialog.NO) {
                    entryno.requestFocus();
                    return false;
                }
            }
            entryNoForNewEntry = EfaUtil.string2date(entryno.getValue(), 1, 1, 1).tag; // lfdNr merken, nächster Eintrag erhält dann per default diese Nummer + 1
        } else { // geänderter Fahrtenbucheintrag
            if (!currentRecord.getEntryId().toString().equals(entryno.toString())) {
                if (Dialog.yesNoDialog(International.getString("Warnung"),
                        International.getString("Du hast die Laufende Nummer dieses Eintrags verändert!") + " " +
                        International.getString("Trotzdem speichern?")) == Dialog.NO) {
                    entryno.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkBoatCaptain() {
        // falls noch nicht geschehen, ggf. automatisch Obmann auswählen
        if (Daten.efaConfig.getValueAutoObmann() && getBoatCaptain() < 0) {
            autoSelectBoatCaptain();
        }

        // Obmann-Auswahl (Autokorrektur, neu in 1.7.1)
        int boatCaptain = getBoatCaptain();
        if (boatCaptain == 0 && cox.getValue().length() == 0 && crew[0].getValue().length() > 0) {
            setBoatCaptain(1, true);
        }
        if (boatCaptain > 0 && crew[boatCaptain - 1].getValue().length() == 0 && cox.getValue().length() > 0) {
            setBoatCaptain(0, true);
        }
        if (boatCaptain > 0 && crew[boatCaptain - 1].getValue().length() == 0 && crew[0].getValue().length() > 0) {
            setBoatCaptain(1, true);
        }
        boatCaptain = getBoatCaptain();

        // Obmann-Check
        if ((boatCaptain == 0 && cox.getValue().length() == 0)
                || (boatCaptain > 0 && crew[boatCaptain - 1].getValue().length() == 0)) {
            Dialog.error(International.getString("Bitte wähle als Obmann eine Person aus, die tatsächlich im Boot sitzt!"));
            boatcaptain.requestFocus();
            return false;
        }

        if (Daten.efaConfig.getValueEfaDirekt_eintragErzwingeObmann() && boatCaptain < 0) {
            Dialog.error(International.getString("Bitte wähle einen Obmann aus!"));
            boatcaptain.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkBoatStatus() {
        if (getMode() == MODE_BOATHOUSE_START || getMode() == MODE_BOATHOUSE_START_CORRECT) {
            // checkFahrtbeginnFuerBoot nur bei direkt_boot==null machen, da ansonsten der Check schon in EfaDirektFrame gemacht wurde
            if (efaBoathouseAction != null && efaBoathouseAction.boat == null) {
                efaBoathouseAction.boat = currentBoat;
                if (currentBoat != null) {
                    efaBoathouseAction.boatStatus = currentBoat.getBoatStatus();
                }
                boolean success = efaBoathouseFrame.checkStartSessionForBoat(efaBoathouseAction, 2);
                if (!success) {
                    efaBoathouseAction.boat = null; // otherwise next check would fail
                }
                return success;
            }
        }
        return true;
    }

    private boolean checkMultiDayTours() {
        // Prüfen, ob Eintrag einer Mehrtagesfahrt vorliegt und das Datum in den Zeitraum der Mehrtagesfahrt fällt
        if (isModeBoathouse()) {
            return true;
        }
        UUID sgId = (UUID)sessiongroup.getRememberedId();
        SessionGroupRecord g = (sgId != null ? Daten.project.getSessionGroups(false).findSessionGroupRecord(sgId) : null);
        if (!date.getDate().isSet()) {
            return true; // shouldn't happen
        }
        if (g != null) {
            DataTypeDate entryStartDate = date.getDate();
            DataTypeDate entryEndDate = enddate.getDate();
            DataTypeDate groupStartDate = g.getStartDate();
            DataTypeDate groupEndDate = g.getEndDate();
            if (entryStartDate.isBefore(groupStartDate) || entryStartDate.isAfter(groupEndDate) ||
                (entryEndDate.isSet() && (entryEndDate.isBefore(groupStartDate) || entryEndDate.isAfter(groupEndDate))) ) {
                Dialog.error(International.getMessage("Das Datum des Fahrtenbucheintrags {entry} liegt außerhalb des Zeitraums, "
                        + "der für die ausgewählte Fahrtgruppe '{name}' angegeben wurde.",
                        entryno.getValue(), g.getName()));
                return false;
            }
        }
        return true;
    }

    private boolean checkDate() {
        if (date.isSet() && enddate.isSet() && !date.getDate().isBefore(enddate.getDate())) {
            String msg = International.getString("Das Enddatum des Fahrtenbucheintrags nach vor dem Startdatum liegen.");
            Dialog.error(msg);
            enddate.requestFocus();
            return false;
        }
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
            return false;
        }
        return true;
    }

    private boolean checkAllDataEntered() {
        if (isModeBoathouse()) {
            if (boat.getValue().length() == 0) {
                Dialog.error(International.getString("Bitte gib einen Bootsnamen ein!"));
                boat.requestFocus();
                return false;
            }

            if (getNumberOfPersonsInBoat() == 0) {
                Dialog.error(International.getString("Bitte trage mindestens eine Person ein!"));
                if (cox.isEditable()) {
                    cox.requestFocus();
                } else {
                    crew[0].requestFocus();
                }
                return false;
            }

            // Ziel vor Fahrtbeginn eintragen
            if ((mode == MODE_BOATHOUSE_START || mode == MODE_BOATHOUSE_START_CORRECT)
                    && Daten.efaConfig.getValueEfaDirekt_zielBeiFahrtbeginnPflicht() && destination.getValue().length() == 0) {
                Dialog.error(International.getString("Bitte trage ein voraussichtliches Fahrtziel/Strecke ein!"));
                destination.requestFocus();
                return false;
            }

            if ((mode == MODE_BOATHOUSE_FINISH || mode == MODE_BOATHOUSE_LATEENTRY) &&
                !Daten.efaConfig.getValueSkipZiel() && destination.getValue().length() == 0) {
                Dialog.error(International.getString("Bitte trage ein Fahrtziel/Strecke ein!"));
                destination.requestFocus();
                return false;
            }

            // Distance
            if ((!distance.isSet() || distance.getValue().getValueInDefaultUnit() == 0)) {
                if (mode == MODE_BOATHOUSE_FINISH || mode == MODE_BOATHOUSE_LATEENTRY) {
                    Dialog.error(International.getString("Bitte trage die gefahrenen Kilometer ein!"));
                    distance.requestFocus();
                    return false;
                }
                if (isModeFull()) {
                    if (Dialog.yesNoDialog(International.getString("Warnung"),
                            International.getString("Keine Kilometer eingetragen.") + "\n"
                            + International.getString("Trotzdem speichern?")) == Dialog.NO) {
                        distance.requestFocus();
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private boolean checkUnknownNames() {
        // Prüfen, ob ggf. nur bekannte Boote/Ruderer/Ziele eingetragen wurden
        if (isModeBoathouse()) {
            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteBoote() && currentRecord.getBoatId() == null) {
                Dialog.error(International.getMessage("Das Boot '{bootsname}' ist unbekannt. Bitte trage ein bekanntes Boot ein!", boat.getValue()));
                boat.requestFocus();
                return false;
            }
            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteRuderer() && currentRecord.getCoxId() == null) {
                Dialog.error(International.getMessage("Person '{name}' ist unbekannt. Bitte trage eine bekannte Person ein!", cox.getValue()));
                cox.requestFocus();
                return false;
            }
            for (int i = 0; i < crew.length; i++) {
                if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteRuderer() && currentRecord.getCrewId(i) == null) {
                    Dialog.error(International.getMessage("Person '{name}' ist unbekannt. Bitte trage eine bekannte Person ein!", crew[i].getValue()));
                    crew[i].requestFocus();
                    return false;
                }
            }
            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteZiele() && currentRecord.getDestinationId() == null) {
                Dialog.error(International.getMessage("Ziel/Strecke '{destination}' ist unbekannt. Bitte trage eine bekanntes Ziel/Strecke ein!", destination.getValue()));
                destination.requestFocus();
                return false;
            }
        }
        return true;
    }

    private boolean checkAllowedPersons() {
        if (mode == MODE_BOATHOUSE_START || mode == MODE_BOATHOUSE_START_CORRECT) {
            if (currentBoat == null) {
                return true;
            }

            LogbookRecord myRecord = this.getFields();
            if (myRecord == null) {
                return true;
            }

            Groups groups = Daten.project.getGroups(false);
            long tstmp = getValidAtTimestamp(myRecord);

            DataTypeList<UUID> groupIdList = currentBoat.getAllowedGroupIdList();
            if (groupIdList != null && groupIdList.length() > 0) {
                String nichtErlaubt = null;
                int nichtErlaubtAnz = 0;
                //Vector g = Boote.getGruppen(b);
                for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
                    PersonRecord p = myRecord.getCrewRecord(i, tstmp);
                    String ptext = myRecord.getCrewName(i);
                    if (p == null && ptext == null) {
                        continue;
                    }
                    boolean inAnyGroup = false;
                    if (p != null) {
                        for (int j = 0; j < groupIdList.length(); j++) {
                            GroupRecord g = groups.findGroupRecord(groupIdList.get(j), tstmp);
                            if (g != null && g.getMemberIdList() != null && g.getMemberIdList().contains(p.getId())) {
                                inAnyGroup = true;
                                break;
                            }
                        }
                    }
                    if (!inAnyGroup) {
                        String name = (p != null ? p.getQualifiedName() : ptext);
                        nichtErlaubt = (nichtErlaubt == null ? name : nichtErlaubt + "\n" + name);
                        nichtErlaubtAnz++;
                    }
                }
                if (nichtErlaubtAnz > currentBoat.getMaxNotInGroup()) {
                    String erlaubteGruppen = null;
                    for (int j = 0; j < groupIdList.length(); j++) {
                        GroupRecord g = groups.findGroupRecord(groupIdList.get(j), tstmp);
                        String name = (g != null ? g.getName() : null);
                        if (name == null) {
                            continue;
                        }
                        erlaubteGruppen = (erlaubteGruppen == null ? name : erlaubteGruppen + (j + 1 < groupIdList.length() ? ", " + name : " "
                                + International.getString("und") + " " + name));
                    }
                    switch (Dialog.auswahlDialog(International.getString("Boot nur für bestimmte Gruppen freigegeben"),
                            International.getMessage("Dieses Boot dürfen nur {list_of_valid_groups} nutzen.", erlaubteGruppen) + "\n"
                            + International.getString("Folgende Personen gehören keiner der Gruppen an und dürfen das Boot nicht benutzen:") + " \n"
                            + nichtErlaubt + "\n"
                            + International.getString("Was möchtest Du tun?"),
                            International.getString("Anderes Boot wählen"),
                            International.getString("Mannschaft ändern"),
                            International.getString("Trotzdem benutzen"),
                            International.getString("Eintrag abbrechen"))) {
                        case 0:
                            setFieldEnabled(true, true, boat);
                            boat.parseAndShowValue("");
                            boat.requestFocus();
                            return false;
                        case 1:
                            crew[0].requestFocus();
                            return false;
                        case 2:
                            logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_UNALLOWEDBOATUSAGE,
                                              International.getString("Unerlaubte Benutzung eines Bootes"),
                                              myRecord);
                            break;
                        case 3:
                            cancel();
                            return false;
                    }
                }
            }

            // Prüfen, ob mind 1 Ruderer (oder Stm) der Gruppe "mind 1 aus Gruppe" im Boot sitzt
            if (currentBoat.getRequiredGroupId() != null) {
                GroupRecord g = groups.findGroupRecord(currentBoat.getRequiredGroupId(), tstmp);
                boolean found = false;
                if (g != null && g.getMemberIdList() != null) {
                    for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
                        PersonRecord p = myRecord.getCrewRecord(i, tstmp);
                        if (p != null && g.getMemberIdList().contains(p.getId())) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    switch (Dialog.auswahlDialog(International.getString("Boot erfordert bestimmte Berechtigung"),
                            International.getMessage("In diesem Boot muß mindestens ein Mitglied der Gruppe {groupname} sitzen.", g.getName()) + "\n"
                            + International.getString("Was möchtest Du tun?"),
                            International.getString("Anderes Boot wählen"),
                            International.getString("Mannschaft ändern"),
                            International.getString("Trotzdem benutzen"),
                            International.getString("Eintrag abbrechen"))) {
                        case 0:
                            this.setFieldEnabled(true, true, boat);
                            boat.parseAndShowValue("");
                            boat.requestFocus();
                            return false;
                        case 1:
                            crew[0].requestFocus();
                            return false;
                        case 2:
                            logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_UNALLOWEDBOATUSAGE,
                                              International.getString("Unerlaubte Benutzung eines Bootes"),
                                              myRecord);
                            break;
                        case 3:
                            cancel();
                            return false;
                    }
                }
            }
        }
        return true;
    }

    // =========================================================================
    // Menu Actions
    // =========================================================================

    void menuActionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd == null) {
            return;
        }

        // check and prompt to save changes (except for Help and About)
        if (!cmd.equals(EfaMenuButton.BUTTON_HELP) &&
            !cmd.equals(EfaMenuButton.BUTTON_ABOUT)) {
            if (!isModeFull() || !promptSaveChangesOk()) {
                return;
            }
        }

        // for exit, we don't need to check permissions: every admin is allowed to exit efaBase
        if (cmd.equals(EfaMenuButton.BUTTON_EXIT)) {
            cancel();
        }

        // now check permissions and perform the menu action
        boolean permission = EfaMenuButton.menuAction(this, cmd, admin, logbook);

        // Projects and Logbooks are *not* handled within EfaMenuButton
        if (cmd.equals(EfaMenuButton.BUTTON_PROJECTS) && permission) {
            menuFileProjects(e);
        }
        if (cmd.equals(EfaMenuButton.BUTTON_LOGBOOKS) && permission) {
            menuFileLogbooks(e);
        }

    }

    void menuFileProjects(ActionEvent e) {
        OpenProjectOrLogbookDialog dlg = new OpenProjectOrLogbookDialog(this, OpenProjectOrLogbookDialog.Type.project);
        String projectName = dlg.openDialog();
        if (projectName == null) {
            return;
        }
        if (Daten.project != null && Daten.project.isOpen()) {
            try {
                Daten.project.closeAllStorageObjects();
            } catch(Exception ee) {
                Logger.log(ee);
                Dialog.error(ee.toString());
                return;
            }
        }
        Daten.project = null;
        Project.openProject(projectName);
        if (Daten.project != null && !isModeBoathouse()) {
            Daten.efaConfig.setValueLastProjectEfaBase(Daten.project.getProjectName());
        }
        if (Daten.project != null) {
            if (Daten.project.getCurrentLogbookEfaBase() != null) {
                openLogbook(Daten.project.getCurrentLogbookEfaBase());
            } else {
                menuFileLogbooks(null);
            }
        }
        setTitle();
    }

    void menuFileLogbooks(ActionEvent e) {
        if (Daten.project == null) {
            menuFileProjects(e);
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
                if (r == null) {
                    r = logbook.getLogbookRecord(iterator.getFirst());;
                }
                break;
            case 1:
                r = logbook.getLogbookRecord(iterator.getNext());
                if (r == null) {
                    r = logbook.getLogbookRecord(iterator.getLast());;
                }
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
        if (!isLogbookReady()) {
            return;
        }
        if (isModeFull() && !promptSaveChangesOk()) {
            return;
        }

        String currentEntryNo = null;
        if (insertAtCurrentPosition && currentRecord != null && currentRecord.getEntryId() != null) {
            currentEntryNo = currentRecord.getEntryId().toString();
            if (!isModeBase() && Daten.project.getBoatStatus(false).areBoatsOutOnTheWater()) {
                Dialog.error(International.getString("Es sind noch Boote unterwegs. "
                        + "Das Einfügen von Einträgen ist nur möglich, wenn alle laufenden Fahrten beendet sind."));
                return;
            }

            int ret = Dialog.yesNoDialog(International.getString("Eintrag einfügen"),
                    International.getMessage("Soll vor dem aktuellen Eintrag (Lfd. Nr. {lfdnr}) wirklich ein neuer Eintrag eingefügt werden?\n"
                    + "Alle nachfolgenden laufenden Nummern werden dann um eins erhöht!", currentEntryNo));
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
            entryno.setUnchanged();
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
            entryno.setUnchanged();
        }

        // set Date
        String d;
        if (referenceRecord != null && referenceRecord.getDate() != null) {
            d = referenceRecord.getDate().toString();
        } else {
            d = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
        }
        date.parseAndShowValue(d);
        date.setUnchanged();
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
                if (isModeFull()) {
                    logAdminEvent(Logger.INFO, Logger.MSG_ADMIN_LOGBOOK_ENTRYDELETED,
                            International.getString("Eintrag gelöscht"), currentRecord);
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
    }

    void searchLogbook() {
        SearchLogbookDialog.showSearchDialog(this, logbook, iterator);
    }



    // =========================================================================
    // Callback-related methods
    // =========================================================================

    private void this_windowDeactivated(WindowEvent e) {
        try {
            if (isEnabled() && Dialog.frameCurrent() == this) {
                this.toFront();
            }
        } catch (Exception ee) {
            Logger.logdebug(ee);
        }
    }

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
            if (item == sessiongroup) {
                selectSessionGroup();
            }
            if (item == remainingCrewUpButton) {
                setCrewRangeSelection(crewRangeSelection - 1);
            }
            if (item == remainingCrewDownButton) {
                setCrewRangeSelection(crewRangeSelection + 1);
            }
            if (item == boatDamageButton) {
                if (currentBoat != null && currentBoat.getId() != null) {
                    UUID personID = null;
                    LogbookRecord myRecord = currentRecord;
                    if (myRecord == null) {
                        myRecord = getFields();
                    }
                    if (myRecord != null) {
                        personID = myRecord.getCoxId();
                        if (personID == null) {
                            personID = myRecord.getCrewId(1);
                        }
                    }
                    BoatDamageEditDialog.newBoatDamage(this, currentBoat, personID);
                }
            }
            if (item == saveButton) {
                saveEntry();
            }
        }
        if (id == FocusEvent.FOCUS_GAINED) {
            showHint(item.getName());
            if (lastFocusedItem != null && isCoxOrCrewItem(lastFocusedItem) &&
                    !isCoxOrCrewItem(item)) {
                autoSelectBoatCaptain();
            }
            if (item == destination) {
                lastDestination = DestinationRecord.tryGetNameAndVariant(destination.getValueFromField().trim())[0];;
            }
        }
        if (id == FocusEvent.FOCUS_LOST) {
            showHint(null);
            lastFocusedItem = item;
            if (item == boat || item == boatvariant) {
                currentBoatUpdateGui();
            }
            if (item == cox) {
                if (Daten.efaConfig.getValueAutoObmann() && isNewRecord
                        && cox.getValueFromField().trim().length() > 0 && this.getBoatCaptain() == -1) {
                    this.setBoatCaptain(0, true);
                }

            }
            if (item == crew[0]) {
                if (Daten.efaConfig.getValueAutoObmann() && isNewRecord && getBoatCaptain() == -1) {
                    if (Daten.efaConfig.getValueDefaultObmann().equals(EfaConfig.OBMANN_BOW) && crew[0].getValueFromField().trim().length() > 0) {
                        setBoatCaptain(1, true);
                    }
                }
            }
            if (isModeBoathouse() && (item == starttime || item == endtime)) {
                setTime((ItemTypeTime)item, 0, null);
            }
            if (item == destination) {
                setDesinationDistance();
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
                ItemTypeHashtable hash = Daten.efaConfig.getValueKeys();
                String[] k = hash.getKeysArray();
                if (k != null && k.length > 0) {
                    for (int i = 0; i < k.length; i++) {
                        if ( (((String) k[i]).equals("F6")  && e.getKeyCode() == KeyEvent.VK_F6  && hash.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F7")  && e.getKeyCode() == KeyEvent.VK_F7  && hash.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F8")  && e.getKeyCode() == KeyEvent.VK_F8  && hash.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F9")  && e.getKeyCode() == KeyEvent.VK_F9  && hash.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F10") && e.getKeyCode() == KeyEvent.VK_F10 && hash.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F11") && (e.getKeyCode() == KeyEvent.VK_F11 || e.getKeyCode() == KeyEvent.VK_STOP) && hash.get(k[i]) != null ) ||
                             (((String) k[i]).equals("F12") && (e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_AGAIN) && hash.get(k[i]) != null )
                                ) {
                            comments.parseAndShowValue(comments.getValueFromField() + hash.get(k[i]));
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
            if (item == boatvariant) {
                int variant = EfaUtil.stringFindInt(boatvariant.getValueFromField(), -1);
                currentBoatUpdateGui(variant);
            }
        }
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
                    + (Daten.efaConfig.getValueNameFormat().equals(EfaConfig.NAMEFORMAT_FIRSTLAST)
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
                    + "<" + International.getString("Fahrtziel oder Strecke") + ">");
            return;
        }
        if (s.equals(LogbookRecord.DISTANCE)) {
            infoLabel.setText(International.getString("Bitte eingeben") + ": "
                    + "<" + International.getString("Länge der Fahrt") + ">"
                    + " (" + DataTypeDistance.getAllUnitAbbrevationsAsString(true) + ")" );
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
        if (Daten.efaConfig.getValueEfaDirekt_eintragHideUnnecessaryInputFields()) {
            if (item instanceof ItemTypeStringAutoComplete) {
                ((ItemTypeStringAutoComplete)item).setVisibleSticky(visible);
            } else {
                item.setVisible(visible);
            }
        }
        item.setEditable(enabled);
    }

    private void setFieldEnabledDistance() {
        if (mode != MODE_BOATHOUSE_FINISH && mode != MODE_BOATHOUSE_LATEENTRY) {
            return; // Zielabhängiges Enabled der BootsKm nur bei "Fahrt beenden" und "Nachtrag"
        }
        boolean enabled = !destination.isKnown()
                || !Daten.efaConfig.getValueEfaDirekt_eintragNichtAenderbarKmBeiBekanntenZielen();
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
        if (Daten.efaConfig.getValueEfaDirekt_eintragErlaubeNurMaxRudererzahl()) {
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
        setCrewRangeSelection(0);

        // "Weiterere Mannschaft"-Button ggf. ausblenden
        setFieldEnabled(true, numCrew > 8 || !Daten.efaConfig.getValueEfaDirekt_eintragErlaubeNurMaxRudererzahl(), remainingCrewUpButton);
        setFieldEnabled(true, numCrew > 8 || !Daten.efaConfig.getValueEfaDirekt_eintragErlaubeNurMaxRudererzahl(), remainingCrewDownButton);

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
        currentBoatUpdateGui(-1);
    }
    void currentBoatUpdateGui(int newvariant) {
        boat.getValueFromGui();

        currentBoat = null;
        currentBoatTypeSeats = null;
        currentBoatTypeCoxing = null;
        currentBoatNumberOfSeats = 0;
        if (!isLogbookReady()) {
            return;
        }

        try {
            BoatRecord b = findBoat(getValidAtTimestamp(null));
            if (b != null) {
                currentBoat = b;
                // Update Boat Type selection
                updateBoatVariant(currentBoat, newvariant);
                int variant = EfaUtil.stringFindInt(boatvariant.toString(), -1);
                int idx = b.getVariantIndex(variant);
                currentBoatTypeSeats = b.getTypeSeats(idx);
                currentBoatTypeCoxing = b.getTypeCoxing(idx);
                currentBoatNumberOfSeats = b.getNumberOfSeats(idx);
                if (isNewRecord) {
                    if (b.getDefaultDestinationId() != null) {
                        destination.parseAndShowValue(b.getDefaultDestinationId().toString());
                    }
                    if (b.getDefaultCrewId() != null && Daten.efaConfig.getValueAutoStandardmannsch()) {
                        setDefaultCrew(b.getDefaultCrewId());
                    }
                    if (b.getDefaultSessionType() != null) {
                        sessiontype.parseAndShowValue(b.getDefaultSessionType());
                    }
                }
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }


        if (isModeBoathouse()) {
            boolean isCoxed = (currentBoatTypeCoxing == null || currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXED));
            int numCrew = (currentBoatNumberOfSeats <= 0 ? LogbookRecord.CREW_MAX : currentBoatNumberOfSeats);
            currentBoatUpdateGuiBoathouse(isCoxed, numCrew);
            packFrame("currentBoatUpdateGui()");
        }
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
        if (Daten.efaConfig.getValueAutoObmann() && isNewRecord
                && getBoatCaptain() == -1) {
            if (Daten.efaConfig.getValueDefaultObmann().equals(EfaConfig.OBMANN_STROKE)) {
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
        if (Daten.efaConfig.getValueEfaDirekt_eintragErzwingeObmann()
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
        packFrame("setCrewRangeSelection(nr)");
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

    void setDefaultCrew(UUID crewId) {
        Crews crews = Daten.project.getCrews(false);
        CrewRecord r = crews.getCrew(crewId);
        if (r != null) {
            if (currentBoatTypeCoxing != null && !currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS) &&
                r.getCoxId() != null) {
                PersonRecord p = Daten.project.getPersons(false).getPerson(r.getCoxId(), getValidAtTimestamp(null));
                if (p != null) {
                    cox.parseAndShowValue(p.getQualifiedName());
                }
            }
            for (int i=1; i <=currentBoatNumberOfSeats && i<=LogbookRecord.CREW_MAX; i++) {
                UUID id = r.getCrewId(i);
                if (id != null) {
                    PersonRecord p = Daten.project.getPersons(false).getPerson(id, getValidAtTimestamp(null));
                    if (p != null) {
                        crew[i-1].parseAndShowValue(p.getQualifiedName());
                    }
                }
            }
            if ((r.getBoatCaptainPosition() == 0 && currentBoatTypeCoxing != null && !currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS)) ||
                (r.getBoatCaptainPosition() > 0 && r.getBoatCaptainPosition() <= currentBoatNumberOfSeats)) {
                boatcaptain.parseAndShowValue(Integer.toString(r.getBoatCaptainPosition()));
            }
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
            // @todo (P9) startBringToFront(true); not needed any more
        }
    }

    public boolean cancel() {
        if (isModeBoathouse()) {
            efaBoathouseHideEfaFrame();
            return true;
        }

        if (!promptSaveChangesOk()) {
            return false;
        }

        if (isModeAdmin()) {
            super.cancel();
            return true;
        }

        //@efaconfig if (!Daten.efaConfig.writeFile()) {
        //@efaconfig     LogString.logError_fileWritingFailed(Daten.efaConfig.getFileName(), International.getString("Konfigurationsdatei"));
        //@efaconfig }
        super.cancel();
        Daten.haltProgram(0);
        return true;
    }

    // =========================================================================
    // FocusManager
    // =========================================================================

    class EfaBaseFrameFocusManager extends DefaultFocusManager {

        private EfaBaseFrame efaBaseFrame;
        private FocusManager fm;

        public EfaBaseFrameFocusManager(EfaBaseFrame efaBaseFrame, FocusManager fm) {
            this.efaBaseFrame = efaBaseFrame;
            this.fm = fm;
        }

        private IItemType getItem(Component c) {
            if (c == null) {
                return null;
            }
            if (c == efaBaseFrame.entryno.getComponent()) {
                return efaBaseFrame.entryno;
            }
            if (c == efaBaseFrame.date.getComponent()) {
                return efaBaseFrame.date;
            }
            if (c == efaBaseFrame.enddate.getComponent()) {
                return efaBaseFrame.enddate;
            }
            if (c == efaBaseFrame.boat.getComponent() ||
                c == efaBaseFrame.boat.getButton()) {
                return efaBaseFrame.boat;
            }
            if (c == efaBaseFrame.boatvariant.getComponent()) {
                return efaBaseFrame.boatvariant;
            }
            if (c == efaBaseFrame.cox.getComponent() ||
                c == efaBaseFrame.cox.getButton()) {
                return efaBaseFrame.cox;
            }
            for (int i=0; i<efaBaseFrame.crew.length; i++) {
                if (c == efaBaseFrame.crew[i].getComponent() ||
                    c == efaBaseFrame.crew[i].getButton()) {
                    return efaBaseFrame.crew[i];
                }
            }
            if (c == efaBaseFrame.boatcaptain.getComponent()) {
                return efaBaseFrame.boatcaptain;
            }
            if (c == efaBaseFrame.starttime.getComponent()) {
                return efaBaseFrame.starttime;
            }
            if (c == efaBaseFrame.endtime.getComponent()) {
                return efaBaseFrame.endtime;
            }
            if (c == efaBaseFrame.destination.getComponent() ||
                c == efaBaseFrame.destination.getButton()) {
                return efaBaseFrame.destination;
            }
            if (c == efaBaseFrame.distance.getComponent()) {
                return efaBaseFrame.distance;
            }
            if (c == efaBaseFrame.comments.getComponent()) {
                return efaBaseFrame.comments;
            }
            if (c == efaBaseFrame.sessiontype.getComponent()) {
                return efaBaseFrame.sessiontype;
            }
            if (c == efaBaseFrame.remainingCrewUpButton.getComponent()) {
                return efaBaseFrame.remainingCrewUpButton;
            }
            if (c == efaBaseFrame.remainingCrewDownButton.getComponent()) {
                return efaBaseFrame.remainingCrewDownButton;
            }
            if (c == efaBaseFrame.boatDamageButton.getComponent()) {
                return efaBaseFrame.boatDamageButton;
            }
            if (c == efaBaseFrame.saveButton.getComponent()) {
                return efaBaseFrame.saveButton;
            }
            return null;
        }

        void focusItem(IItemType item, Component cur, int direction) {
            // fSystem.out.println("focusItem(" + item.getName() + ")");
            if (item == efaBaseFrame.starttime && Daten.efaConfig.getValueSkipUhrzeit()) {
                focusItem(efaBaseFrame.destination, cur, direction);
            } else if (item == efaBaseFrame.endtime && Daten.efaConfig.getValueSkipUhrzeit()) {
                focusItem(efaBaseFrame.destination, cur, direction);
            } else if (item == efaBaseFrame.destination && Daten.efaConfig.getValueSkipZiel()) {
                focusItem(efaBaseFrame.distance, cur, direction);
            } else if (item == efaBaseFrame.comments && Daten.efaConfig.getValueSkipBemerk()) {
                focusItem(efaBaseFrame.saveButton, cur, direction);
            } else if (item.isEnabled() && item.isVisible() && item.isEditable()) {
                item.requestFocus();
            } else {
                if (direction > 0) {
                    focusNextItem(item, cur);
                } else {
                    focusPreviousItem(item, cur);
                }
            }
        }

        public void focusNextItem(IItemType item, Component cur) {
            //System.out.println("focusNextItem(" + item.getName() + ")");

            // LFDNR
            if (item == efaBaseFrame.entryno) {
                focusItem(efaBaseFrame.date, cur, 1);
                return;
            }

            // DATUM
            if (item == efaBaseFrame.date) {
                focusItem(efaBaseFrame.boat, cur, 1);
                return;
            }

            // BOOT
            if (item == efaBaseFrame.boat) {
                efaBaseFrame.boat.getValueFromGui();
                efaBaseFrame.currentBoatUpdateGui();
                if (!(cur instanceof JButton) && efaBaseFrame.boat.getValue().length()>0 && !efaBaseFrame.boat.isKnown() && !efaBaseFrame.isModeBoathouse()) {
                    efaBaseFrame.boat.requestButtonFocus();
                } else if (efaBaseFrame.boatvariant.isVisible()) {
                    focusItem(efaBaseFrame.boatvariant, cur, 1);
                } else {
                    if (efaBaseFrame.currentBoatTypeCoxing != null && efaBaseFrame.currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS)) {
                        focusItem(efaBaseFrame.crew[0], cur, 1);
                    } else {
                        focusItem(efaBaseFrame.cox, cur, 1);
                    }
                }
                return;
            }

            // BOOTVARIANT
            if (item == efaBaseFrame.boatvariant) {
                efaBaseFrame.boatvariant.getValueFromGui();
                efaBaseFrame.currentBoatUpdateGui();
                if (efaBaseFrame.currentBoatTypeCoxing != null && efaBaseFrame.currentBoatTypeCoxing.equals(EfaTypes.TYPE_COXING_COXLESS)) {
                    focusItem(efaBaseFrame.crew[0], cur, 1);
                } else {
                    focusItem(efaBaseFrame.cox, cur, 1);
                }
                return;
            }

            // STEUERMANN
            if (item == efaBaseFrame.cox) {
                efaBaseFrame.cox.getValueFromGui();
                if (!(cur instanceof JButton) && efaBaseFrame.cox.getValue().length()>0 && !efaBaseFrame.cox.isKnown() && !efaBaseFrame.isModeBoathouse()) {
                    efaBaseFrame.cox.requestButtonFocus();
                } else {
                    focusItem(efaBaseFrame.crew[efaBaseFrame.crewRangeSelection * 8], cur, 1);
                }
                return;
            }

            // MANNSCHAFT
            for (int i = 0; i < efaBaseFrame.crew.length; i++) {
                if (item == efaBaseFrame.crew[i]) {
                    efaBaseFrame.crew[i].getValueFromGui();
                    if (!(cur instanceof JButton) && efaBaseFrame.crew[i].getValue().length()>0 && !efaBaseFrame.crew[i].isKnown() && !efaBaseFrame.isModeBoathouse()) {
                        efaBaseFrame.crew[i].requestButtonFocus();
                    } else if (efaBaseFrame.crew[i].getValueFromField().trim().length() == 0) {
                        focusItem(efaBaseFrame.starttime, cur, 1);
                    } else if (efaBaseFrame.currentBoatTypeSeats != null && i+1 < efaBaseFrame.crew.length &&
                            i+1 == EfaTypes.getNumberOfRowers(efaBaseFrame.currentBoatTypeSeats) &&
                            efaBaseFrame.crew[i+1].getValueFromField().trim().length() == 0) {
                        focusItem(efaBaseFrame.starttime, cur, 1);
                    } else if (i+1 < efaBaseFrame.crew.length) {
                        focusItem(efaBaseFrame.crew[i + 1], cur, 1);
                    } else {
                        focusItem(efaBaseFrame.starttime, cur, 1);
                    }
                    return;
                }
            }

            // ABFAHRT
            if (item == efaBaseFrame.starttime) {
                focusItem(efaBaseFrame.endtime, cur, 1);
                return;
            }

            // ANKUNFT
            if (item == efaBaseFrame.endtime) {
                focusItem(efaBaseFrame.destination, cur, 1);
                return;
            }

            // ZIEL
            if (item == efaBaseFrame.destination) {
                if (!(cur instanceof JButton) && efaBaseFrame.destination.getValue().length()>0 && !efaBaseFrame.destination.isKnown() && !efaBaseFrame.isModeBoathouse()) {
                    efaBaseFrame.destination.requestButtonFocus();
                } else {
                    focusItem(efaBaseFrame.distance, cur, 1);
                }
                return;
            }

            // BOOTS-KM
            if (item == efaBaseFrame.distance) {
                focusItem(efaBaseFrame.comments, cur, 1);
                return;
            }

            // COMMENTS
            if (item == efaBaseFrame.comments) {
                focusItem(efaBaseFrame.saveButton, cur, 1);
                return;
            }

            // ADD-BUTTON
            if (item == efaBaseFrame.saveButton) {
                focusItem(efaBaseFrame.entryno, cur, 1);
                return;
            }

            // other
            fm.focusNextComponent(cur);
        }

        public void focusPreviousItem(IItemType item, Component cur) {
            if (item == efaBaseFrame.entryno) {
                focusItem(efaBaseFrame.saveButton, cur, -1);
                return;
            }
            if (item == efaBaseFrame.cox) {
                focusItem(efaBaseFrame.boat, cur, -1);
                return;
            }
            for (int i = 0; i < efaBaseFrame.crew.length; i++) {
                if (item == efaBaseFrame.crew[i]) {
                    focusItem((i == 0 ? efaBaseFrame.cox : efaBaseFrame.crew[i - 1]), cur, -1);
                    return;
                }
            }
            if (item == efaBaseFrame.starttime) {
                for (int i = 0; i < 8; i++) {
                    if (efaBaseFrame.crew[i + efaBaseFrame.crewRangeSelection * 8].getValueFromField().trim().length() == 0 || i == 7) {
                        focusItem(efaBaseFrame.crew[i + efaBaseFrame.crewRangeSelection * 8], cur, -1);
                        return;
                    }
                }
            }
            if (item == efaBaseFrame.distance) {
                focusItem(efaBaseFrame.destination, cur, -1);
                return;
            }
            if (item == efaBaseFrame.comments) {
                focusItem(efaBaseFrame.distance, cur, -1);
                return;
            }
            if (item == efaBaseFrame.saveButton) {
                focusItem(efaBaseFrame.comments, cur, -1);
                return;
            }

            // other
            fm.focusPreviousComponent(cur);
        }

        public void focusNextComponent(Component cur) {
            //System.out.println("focusNextComponent("+cur+")");
            IItemType item = getItem(cur);
            if (item != null) {
                focusNextItem(item, cur);
            } else {
                fm.focusNextComponent(cur);
            }
        }

        public void focusPreviousComponent(Component cur) {
            //System.out.println("focusPreviousComponent("+cur+")");
            IItemType item = getItem(cur);
            if (item != null) {
                focusPreviousItem(item, cur);
            } else {
                fm.focusPreviousComponent(cur);
            }
        }
    }

    // =========================================================================
    // efaBoathouse methods
    // =========================================================================

    public void setDataForAdminAction(Logbook logbook, AdminRecord admin, AdminDialog adminDialog) {
        this.mode = MODE_ADMIN;
        this.logbook = logbook;
        this.admin = admin;
        this.adminDialog = adminDialog;
    }

    boolean setDataForBoathouseAction(ItemTypeBoatstatusList.BoatListItem action, Logbook logbook) {
        this.mode = action.mode;
        openLogbook(logbook);
        this.efaBoathouseAction = action;
        clearAllBackgroundColors();
        switch(mode) {
            case MODE_BOATHOUSE_START:
                return efaBoathouseStartSession(action);
            case MODE_BOATHOUSE_START_CORRECT:
                return efaBoathouseCorrectSession(action);
            case MODE_BOATHOUSE_FINISH:
                return efaBoathouseFinishSession(action);
            case MODE_BOATHOUSE_LATEENTRY:
                return efaBoathouseLateEntry(action);
            case MODE_BOATHOUSE_ABORT:
                return efaBoathouseAbortSession(action);
        }
        return false;
    }

    boolean efaBoathouseStartSession(ItemTypeBoatstatusList.BoatListItem item) {
        this.setTitle(International.getString("Neue Fahrt beginnen"));
        saveButton.setDescription(International.getStringWithMnemonic("Fahrt beginnen"));
        createNewRecord(false);
        date.parseAndShowValue(EfaUtil.getCurrentTimeStampDD_MM_YYYY());
        setTime(starttime, Daten.efaConfig.getValueEfaDirekt_plusMinutenAbfahrt(), null);

        setFieldEnabled(false, true, entryno);
        setFieldEnabled(true, true, date);
        setFieldEnabled(item.boat == null, true, boat);
        if (Daten.efaConfig.getValueEfaDirekt_eintragNichtAenderbarUhrzeit()) {
            setFieldEnabled(false, true, starttime);
            setFieldEnabled(false, false, endtime);
        } else {
            setFieldEnabled(true, true, starttime);
            setFieldEnabled(false, false, endtime);
        }
        setFieldEnabled(true, true, destination);
        setFieldEnabled(false, false, distance);
        setFieldEnabled(true, true, comments);
        setFieldEnabled(true, Daten.efaConfig.getValueEfaDirekt_showBootsschadenButton(), boatDamageButton);

        if (item.boat != null) {
            boat.parseAndShowValue(item.boat.getQualifiedName());
            currentBoatUpdateGui();
            if (item.boatVariant >= 0) {
                updateBoatVariant(item.boat, item.boatVariant + 1);
            }
            if (cox.isEditable()) {
                setRequestFocus(cox);
            } else {
                setRequestFocus(crew[0]);
            }
        } else {
            currentBoatUpdateGui();
            setRequestFocus(boat);
        }
        if (item.person != null) {
            crew[0].parseAndShowValue(item.person.getQualifiedName());
        }
        distance.parseAndShowValue("");
        return true;
    }

    boolean efaBoathouseCorrectSession(ItemTypeBoatstatusList.BoatListItem item) {
        this.setTitle(International.getString("Fahrt korrigieren"));
        saveButton.setDescription(International.getStringWithMnemonic("Fahrt korrigieren"));
        currentRecord = null;
        try {
            currentRecord = logbook.getLogbookRecord(item.boatStatus.getEntryNo());
        } catch(Exception e) {
            Logger.log(e);
        }
        if (currentRecord == null) {
            String msg =               International.getString("Fahrt korrigieren") + ": " +
              International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!",
              (item != null && item.boatStatus != null && item.boatStatus.getEntryNo() != null ? item.boatStatus.getEntryNo().toString(): "null"),
              (item != null && item.boat != null ? item.boat.getQualifiedName() : (item != null ? item.text : "null")));
            logBoathouseEvent(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT, msg, null);
            return false;
        }
        setFields(currentRecord);

        setFieldEnabled(false, true, entryno);
        setFieldEnabled(true, true, date);
        setFieldEnabled(true, true, boat);
        if (Daten.efaConfig.getValueEfaDirekt_eintragNichtAenderbarUhrzeit()) {
            setFieldEnabled(false, true, starttime);
            setFieldEnabled(false, false, endtime);
        } else {
            setFieldEnabled(true, true, starttime);
            setFieldEnabled(false, false, endtime);
        }
        setFieldEnabled(true, true, destination);
        setFieldEnabled(false, false, distance);
        setFieldEnabled(true, true, comments);
        setFieldEnabled(true, Daten.efaConfig.getValueEfaDirekt_showBootsschadenButton(), boatDamageButton);

        try {
            _inUpdateBoatVariant = true; // a hack to avoid that updateBoatVariant() called from within currentBoatUpdateGui() will change the focus
            currentBoatUpdateGui();
        } finally {
            _inUpdateBoatVariant = false;
        }

        setRequestFocus(boat);

        return true;
    }

    boolean efaBoathouseFinishSession(ItemTypeBoatstatusList.BoatListItem item) {
        this.setTitle(International.getString("Fahrt beenden"));
        saveButton.setDescription(International.getStringWithMnemonic("Fahrt beenden"));
        currentRecord = null;
        try {
            currentRecord = logbook.getLogbookRecord(item.boatStatus.getEntryNo());
        } catch(Exception e) {
            Logger.log(e);
        }
        if (currentRecord == null) {
            String msg =               International.getString("Fahrtende") + ": " +
              International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!",
              (item != null && item.boatStatus != null && item.boatStatus.getEntryNo() != null ? item.boatStatus.getEntryNo().toString(): "null"),
              (item != null && item.boat != null ? item.boat.getQualifiedName() : (item != null ? item.text : "null")));
            logBoathouseEvent(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT, msg, null);
            return false;
        }
        setFields(currentRecord);
        setTime(endtime, -Daten.efaConfig.getValueEfaDirekt_minusMinutenAnkunft(), currentRecord.getStartTime());
        setDesinationDistance();

        setFieldEnabled(false, true, entryno);
        setFieldEnabled(false, true, date);
        setFieldEnabled(false, true, boat);
        if (Daten.efaConfig.getValueEfaDirekt_eintragNichtAenderbarUhrzeit()) {
            setFieldEnabled(false, true, starttime);
            setFieldEnabled(false, true, endtime);
        } else {
            setFieldEnabled(true, true, starttime);
            setFieldEnabled(true, true, endtime);
        }
        setFieldEnabled(true, true, destination);
        setFieldEnabled(true, true, distance);
        setFieldEnabled(true, true, comments);
        setFieldEnabled(true, Daten.efaConfig.getValueEfaDirekt_showBootsschadenButton(), boatDamageButton);

        try {
            _inUpdateBoatVariant = true; // a hack to avoid that updateBoatVariant() called from within currentBoatUpdateGui() will change the focus
            currentBoatUpdateGui();
        } finally {
            _inUpdateBoatVariant = false;
        }

        setRequestFocus(destination);

        return true;
    }

    boolean efaBoathouseLateEntry(ItemTypeBoatstatusList.BoatListItem item) {
        this.setTitle(International.getString("Nachtrag"));
        saveButton.setDescription(International.getStringWithMnemonic("Nachtrag"));
        createNewRecord(false);

        setFieldEnabled(false, true, entryno);
        setFieldEnabled(true, true, date);
        setFieldEnabled(true, true, boat);
        setFieldEnabled(true, true, starttime);
        setFieldEnabled(true, true, endtime);
        setFieldEnabled(true, true, destination);
        setFieldEnabled(true, true, distance);
        setFieldEnabled(true, true, comments);
        setFieldEnabled(true, Daten.efaConfig.getValueEfaDirekt_showBootsschadenButton(), boatDamageButton);

        if (item.boat != null) {
            boat.parseAndShowValue(item.boat.getQualifiedName());
            currentBoatUpdateGui();
            if (item.boatVariant >= 0) {
                updateBoatVariant(item.boat, item.boatVariant + 1);
            }
            if (cox.isEditable()) {
                setRequestFocus(cox);
            } else {
                setRequestFocus(crew[0]);
            }
        } else {
            currentBoatUpdateGui();
            setRequestFocus(boat);
        }
        if (item.person != null) {
            crew[0].parseAndShowValue(item.person.getQualifiedName());
        }

        return true;
    }

    boolean efaBoathouseAbortSession(ItemTypeBoatstatusList.BoatListItem item) {
        currentRecord = null;
        try {
            currentRecord = logbook.getLogbookRecord(item.boatStatus.getEntryNo());
        } catch(Exception e) {
            Logger.log(e);
        }
        if (currentRecord == null) {
            String msg =               International.getString("Fahrtende") + ": " +
              International.getMessage("Die gewählte Fahrt #{lfdnr} ({boot}) konnte nicht gefunden werden!",
              (item != null && item.boatStatus != null && item.boatStatus.getEntryNo() != null ? item.boatStatus.getEntryNo().toString(): "null"),
              (item != null && item.boat != null ? item.boat.getQualifiedName() : (item != null ? item.text : "null")));
            logBoathouseEvent(Logger.ERROR, Logger.MSG_ERR_NOLOGENTRYFORBOAT, msg, null);
            return false;
        }
        boolean checks = logbook.data().isPreModifyRecordCallbackEnabled();
        try {
            logbook.data().setPreModifyRecordCallbackEnabled(false); // otherwise we couldn't delete the record before we change the status
            logbook.data().delete(currentRecord.getKey());
        } catch(Exception e) {
            Dialog.error(e.toString());
            return false;
        }
        logbook.data().setPreModifyRecordCallbackEnabled(checks);
        return true;
    }

    void finishBoathouseAction(boolean success) {
        // log this action
        if (success) {
            switch(mode) {
                case MODE_BOATHOUSE_START:
                    logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_TRIPSTART,
                                      International.getString("Fahrtbeginn"),
                                      currentRecord);
                    break;
                case MODE_BOATHOUSE_START_CORRECT:
                    logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_TRIPSTART_CORR,
                                      International.getString("Fahrtbeginn korrigiert"),
                                      currentRecord);
                    break;
                case MODE_BOATHOUSE_FINISH:
                    logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_TRIPEND,
                                      International.getString("Fahrtende"),
                                      currentRecord);
                    break;
                case MODE_BOATHOUSE_LATEENTRY:
                    logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_TRIPLATEREC,
                                      International.getString("Nachtrag"),
                                      currentRecord);
                    break;
                case MODE_BOATHOUSE_ABORT:
                    logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_TRIPABORT,
                                      International.getString("Fahrtabbruch"),
                                      currentRecord);
                    break;
            }
        } else {
            logBoathouseEvent(Logger.ERROR, Logger.MSG_EVT_ERRORSAVELOGBOOKENTRY,
                    International.getString("Fahrtenbucheintrag konnte nicht gespeichert werden."),
                    currentRecord);
        }

        // Update boat status
        if (success && efaBoathouseAction != null && currentRecord != null) {
            long tstmp = currentRecord.getValidAtTimestamp();
            BoatStatus boatStatus = Daten.project.getBoatStatus(false);
            BoatRecord boatRecord = currentRecord.getBoatRecord(tstmp);
            BoatStatusRecord boatStatusRecord = (boatRecord != null ? boatStatus.getBoatStatus(boatRecord.getId()) : null);

            // figure out new status information
            String newStatus = null;
            String newShowInList = null; // if not explicitly set, this boat will appear in the list determined by its status
            DataTypeIntString newEntryNo = null;
            String newComment = null;
            switch(efaBoathouseAction.mode) {
                case EfaBaseFrame.MODE_BOATHOUSE_START:
                case EfaBaseFrame.MODE_BOATHOUSE_START_CORRECT:
                    newStatus = BoatStatusRecord.STATUS_ONTHEWATER;
                    newEntryNo = currentRecord.getEntryId();
                    newComment = BoatStatusRecord.createStatusString(
                            currentRecord.getSessionType(),
                            currentRecord.getDestinationAndVariantName(tstmp),
                            currentRecord.getDate().toString(),
                            currentRecord.getStartTime().toString(),
                            currentRecord.getAllCoxAndCrewAsNameString());
                    if (Daten.efaConfig.getValueEfaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar() &&
                        ( (currentRecord.getEndDate() != null && currentRecord.getEndDate().isSet()) ||
                         currentRecord.getSessionType().equals(EfaTypes.TYPE_SESSION_TOUR) ||
                         currentRecord.getSessionType().equals(EfaTypes.TYPE_SESSION_REGATTA) ||
                         currentRecord.getSessionType().equals(EfaTypes.TYPE_SESSION_JUMREGATTA))) {
                        newShowInList = BoatStatusRecord.STATUS_NOTAVAILABLE;
                    }
                    break;
                case EfaBaseFrame.MODE_BOATHOUSE_FINISH:
                case EfaBaseFrame.MODE_BOATHOUSE_ABORT:
                    newStatus = BoatStatusRecord.STATUS_AVAILABLE;
                    newComment = "";
                    break;
                case EfaBaseFrame.MODE_BOATHOUSE_LATEENTRY:
                    break;
            }

            boolean newBoatStatusRecord = false;
            if (boatRecord != null && boatStatusRecord == null) {
                // oops, this shouldn't happen!
                String msg = International.getMessage("Kein Bootsstatus für Boot {boat} gefunden.",
                        boatRecord.getQualifiedName());
                logBoathouseEvent(Logger.ERROR, Logger.MSG_EVT_ERRORNOBOATSTATUSFORBOAT,
                        msg,currentRecord);
                Dialog.error(msg);
            } else {
                if (boatStatusRecord == null) {
                    // unknown boat
                    boatStatusRecord = efaBoathouseAction.boatStatus;
                    if (boatStatusRecord == null &&
                        (mode == EfaBaseFrame.MODE_BOATHOUSE_START || mode == EfaBaseFrame.MODE_BOATHOUSE_START_CORRECT)) {
                        // create new status record for unknown boat
                        boatStatusRecord = boatStatus.createBoatStatusRecord(UUID.randomUUID(), currentRecord.getBoatAsName());
                        newBoatStatusRecord = true;
                    }
                    if (boatStatusRecord != null) {
                        boatStatusRecord.setUnknownBoat(true);
                    }
                }
            }

            if (boatStatusRecord != null) {
                if (newStatus != null) {
                    boatStatusRecord.setCurrentStatus(newStatus);
                }
                if (newShowInList != null) {
                    boatStatusRecord.setShowInList(newShowInList);
                } else {
                    boatStatusRecord.setShowInList(null);
                }
                if (newEntryNo != null) {
                    boatStatusRecord.setEntryNo(newEntryNo);
                    boatStatusRecord.setLogbook(logbook.getName());
                } else {
                    boatStatusRecord.setEntryNo(null);
                    boatStatusRecord.setLogbook(null);
                }
                if (newComment != null) {
                    boatStatusRecord.setComment(newComment);
                }
                boatStatusRecord.setBoatText(currentRecord.getBoatAsName());
                try {
                    if (boatStatusRecord.getUnknownBoat() && newStatus != null &&
                        !newStatus.equals(BoatStatusRecord.STATUS_ONTHEWATER)) {
                        boatStatus.data().delete(boatStatusRecord.getKey());
                    } else {
                        if (newBoatStatusRecord) {
                            boatStatus.data().add(boatStatusRecord);
                        } else {
                            boatStatus.data().update(boatStatusRecord);
                            
                            // check whether we have changed the boat during this dialog (e.g. StartCorrect)
                            if (efaBoathouseAction.boatStatus != null && efaBoathouseAction.boatStatus.getBoatId() != null &&
                                !boatStatusRecord.getBoatId().equals(efaBoathouseAction.boatStatus.getBoatId())) {
                                BoatStatusRecord oldStatus = efaBoathouseAction.boatStatus;
                                oldStatus.setCurrentStatus(BoatStatusRecord.STATUS_AVAILABLE);
                                oldStatus.setEntryNo(null);
                                oldStatus.setLogbook(null);
                                oldStatus.setComment("");
                                boatStatus.data().update(oldStatus);
                            }
                        }
                    }
                } catch(Exception e) {
                    Logger.log(e);
                }
            }

        }

        efaBoathouseHideEfaFrame();
    }

    String logEventInfoText(String logType, String logKey, String msg, LogbookRecord r) {
        String infoText = null;
        if (r != null) {
            long tstmp = getValidAtTimestamp(r);
            infoText = "#" + r.getEntryId().toString() + " - " + r.getBoatAsName(tstmp) + " " +
                          International.getMessage("mit {crew}", r.getAllCoxAndCrewAsNameString(tstmp));
        }
        return msg + (infoText != null ? ": " + infoText : "");
    }
    
    void logAdminEvent(String logType, String logKey, String msg, LogbookRecord r) {
        Logger.log(logType, logKey,
                International.getString("Admin") + " " + (admin != null ? admin.getName() : "<none>") + ": " +
                logEventInfoText(logType, logKey, msg, r));
    }

    void logBoathouseEvent(String logType, String logKey, String msg, LogbookRecord r) {
        Logger.log(logType, logKey, logEventInfoText(logType, logKey, msg, r));
    }

    void efaBoathouseSetFixedLocation(int x, int y) {
        if (x >= 0 && y >= 0) {
            this.positionX = x;
            this.positionY = y;
        }
        this.setLocation(this.positionX, this.positionY);
    }

    public void efaBoathouseShowEfaFrame() {
        if (infoLabel.isVisible() != Daten.efaConfig.getValueEfaDirekt_showEingabeInfos()) {
            infoLabel.setVisible(Daten.efaConfig.getValueEfaDirekt_showEingabeInfos());
        }
        packFrame("efaBoathouseShowEfaFrame(Component)");
        efaBoathouseSetFixedLocation(-1, -1);
        showMe();
        toFront();
        if (focusItem != null) {
            focusItem.requestFocus();
        }
    }

    private void efaBoathouseHideEfaFrame() {
        if (mode != EfaBaseFrame.MODE_BOATHOUSE_ABORT) {
            this.setVisible(false);
            Dialog.frameClosed(this);
        }
        efaBoathouseFrame.showEfaBoathouseFrame();
    }


}
