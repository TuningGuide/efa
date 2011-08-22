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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.DataKey;
import de.nmichael.efa.data.storage.DataKeyIterator;
import de.nmichael.efa.gui.util.TableSorter;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ShowLogbookDialog extends BaseDialog implements IItemListener {

    private Logbook logbook;
    private JScrollPane scrollPane;
    private JTable table;
    private ItemTypeInteger showOnlyNumber;
    private ItemTypeBoolean showAlsoIncomplete;

    public ShowLogbookDialog(Frame parent, Logbook logbook) {
        super(parent, International.getStringWithMnemonic("Fahrtenbuch"), International.getStringWithMnemonic("Schließen"));
        this.logbook = logbook;
    }

    public ShowLogbookDialog(JDialog parent, Logbook logbook) {
        super(parent, International.getStringWithMnemonic("Fahrtenbuch"), International.getStringWithMnemonic("Schließen"));
        this.logbook = logbook;
    }

    protected void iniDialog() throws Exception {
        if (Daten.efaConfig.getValueEfaDirekt_startMaximized()) {
            this.setSize(Dialog.screenSize.width, Dialog.screenSize.height);
        } else {
            this.setSize((Dialog.screenSize.width * 98) / 100, (Dialog.screenSize.height * 95) / 100);
        }

        mainPanel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());

        showOnlyNumber = new ItemTypeInteger("SHOWONLYNUMBER",
                (Daten.efaConfig.getValueEfaDirekt_anzFBAnzeigenFahrten() > 0 ? Daten.efaConfig.getValueEfaDirekt_anzFBAnzeigenFahrten() : 50), 1,
                (Daten.efaConfig.getValueEfaDirekt_maxFBAnzeigenFahrten() > 0 ? Daten.efaConfig.getValueEfaDirekt_maxFBAnzeigenFahrten() : 100),
                IItemType.TYPE_PUBLIC, "", International.getString("Anzahl der anzuzeigenden Fahrten"));
        showOnlyNumber.registerItemListener(this);

        showAlsoIncomplete = new ItemTypeBoolean("SHOWALSOINCOMPLETE", Daten.efaConfig.getValueEfaDirekt_FBAnzeigenAuchUnvollstaendige(),
                IItemType.TYPE_PUBLIC, "", International.getString("auch Fahrten von Booten anzeigen, die noch unterwegs sind"));
        showAlsoIncomplete.registerItemListener(this);

        showOnlyNumber.displayOnGui(this, controlPanel, 0, 0);
        showAlsoIncomplete.displayOnGui(this, controlPanel, 0, 1);

        scrollPane = new JScrollPane();

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        updateTable(showOnlyNumber.getValue(), showAlsoIncomplete.getValue());
        table.requestFocus();
    }

    public void updateTable(int max, boolean alsoIncomplete) {
        if (max < 1) {
            max = 1;
        }
        if (table != null) {
            scrollPane.remove(table);
        }

        Object[] title = new Object[10];
        title[0] = International.getString("LfdNr");
        title[1] = International.getString("Datum");
        title[2] = International.getString("Boot");
        title[3] = International.getString("Steuermann");
        title[4] = International.getString("Mannschaft");
        title[5] = International.getString("Abfahrt");
        title[6] = International.getString("Ankunft");
        title[7] = International.getString("Ziel");
        title[8] = International.getString("Km");
        title[9] = International.getString("Bemerkungen");

        Object[][] fahrten = new Object[max][10];
        //String s = "";

        int c = max - 1;
        try {
            DataKeyIterator it = logbook.data().getStaticIterator();
            DataKey k = it.getLast();
            while (k != null) {
                LogbookRecord r = (LogbookRecord)logbook.data().get(k);
                if (r.getSessionIsOpen() && !alsoIncomplete) {
                    k = it.getPrev();
                    continue;
                }

                int obmann = r.getBoatCaptainPosition();

                fahrten[c][0] = r.getEntryId();
                fahrten[c][1] = r.getDate();
                fahrten[c][2] = r.getBoatAsName();
                fahrten[c][3] = new TableItem(r.getCoxAsName(), obmann == 0); // (obmann == 0 ? BOLD : "") + d.get(Fahrtenbuch.STM);

                int mRowCount = r.getNumberOfCrewMembers();
                if (mRowCount == 0) {
                    mRowCount = 1;
                }
                Object[][] mRowData = new Object[mRowCount][1];
                for (int j = 0, i = 1; i <= LogbookRecord.CREW_MAX; i++) {
                    String s = r.getCrewAsName(i);
                    if (s != null && s.length() > 0) {
                        mRowData[j++][0] = new TableItem(s, obmann == i + 1); // (obmann == ii+1 ? BOLD : "") + d.get(i);
                    }
                }
                Object[] mRowTitle = new Object[1];
                mRowTitle[0] = "foo";
                MyNestedJTable mTable = new MyNestedJTable(mRowData, mRowTitle) {

                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                mTable.getColumn("foo").setCellRenderer(new HighlightTableCellRenderer());
                mTable.setShowGrid(false);
                fahrten[c][4] = mTable;

                fahrten[c][5] = r.getStartTime();
                fahrten[c][6] = r.getEndTime();
                fahrten[c][7] = r.getDestinationAndVariantName();
                fahrten[c][8] = r.getDistance();
                fahrten[c][9] = r.getComments();

                k = it.getPrev();
                if (c-- == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }

        if (c > 0) {
            Object[][] fahrtentmp = new Object[max - c][10];
            for (int xorg = c, xnew = 0; xorg < max; xorg++, xnew++) {
                for (int y = 0; y < 10; y++) {
                    fahrtentmp[xnew][y] = fahrten[xorg][y];
                }
            }
            fahrten = fahrtentmp;
        }

        TableSorter sorter = new TableSorter(new DefaultTableModel(fahrten, title));
        table = new MyJTable(sorter);
        table.getColumn(International.getString("Steuermann")).setCellRenderer(new HighlightTableCellRenderer());
        table.getColumn(International.getString("Mannschaft")).setCellRenderer(new TableInTableRenderer());
        //table.getColumn("Mannschaft").setCellEditor(new TableInTableEditor(new JCheckBox()));

        for (int i = 0; i < fahrten.length; i++) {
            int orgHeight = table.getRowHeight(i);
            int newHeight = 0;
            try {
                newHeight = (int) ((JTable) table.getValueAt(i, 4)).getPreferredSize().getHeight();
            } catch (Exception e) {
                EfaUtil.foo();
            }
            if (newHeight > orgHeight) {
                table.setRowHeight(i, newHeight);
            }
        }
        sorter.addMouseListenerToHeaderInTable(table);
        scrollPane.getViewport().add(table, null);
        try {
            table.scrollRectToVisible(table.getCellRect(fahrten.length - 1, 0, true));
        } catch (Exception e) {
        }
        table.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancel();
                }
            }
        });

        // intelligente Spaltenbreiten
        int width = table.getSize().width;
        if (width < this.getSize().width - 20 || width > this.getSize().width) { // beim ersten Aufruf steht Tabellenbreite noch nicht (korrekt) zur Verfügung, daher dieser Plausi-Check
            width = this.getSize().width - 10;
        }

        int[] widths = new int[11];
        int remaining = width;
        for (int i = 0; i < 10; i++) {
            switch (i) {
                case 0:
                    widths[i] = 5 * width / 100; // LfdNr
                    if (widths[i] > 40) {
                        widths[i] = 40;
                    }
                    break;
                case 1:
                    widths[i] = 8 * width / 100; // Datum
                    if (widths[i] > 80) {
                        widths[i] = 80;
                    }
                    break;
                case 5:
                    widths[i] = 5 * width / 100; // Abfahrt
                    if (widths[i] > 50) {
                        widths[i] = 50;
                    }
                    break;
                case 6:
                    widths[i] = 5 * width / 100; // Ankunft
                    if (widths[i] > 50) {
                        widths[i] = 50;
                    }
                    break;
                case 8:
                    widths[i] = 4 * width / 100; // Boots-Km
                    if (widths[i] > 30) {
                        widths[i] = 30;
                    }
                    break;
            }
            remaining -= widths[i];
        }

        for (int i = 0; i < 10; i++) {
            switch (i) {
                case 2:
                    widths[i] = 18 * remaining / 100;
                    break; // Boot
                case 3:
                    widths[i] = 22 * remaining / 100;
                    break; // Stm
                case 4:
                    widths[i] = 22 * remaining / 100;
                    break; // Mannsch
                case 7:
                    widths[i] = 28 * remaining / 100;
                    break; // Ziel
                case 9:
                    widths[i] = 10 * remaining / 100;
                    break; // Bemerkungen
            }
        }

        for (int i = 0; i < 10; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.validate();

    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        System.out.println(event);
        if (event != null && itemType != null) {
            if ((event instanceof KeyEvent && itemType == showOnlyNumber && event.getID() == KeyEvent.KEY_RELEASED) ||
                (event instanceof ActionEvent && itemType == showAlsoIncomplete)) {
                showOnlyNumber.getValueFromGui();
                showAlsoIncomplete.getValueFromGui();
                updateTable(showOnlyNumber.getValue(), showAlsoIncomplete.getValue());
            }
        }
    }

    class MyJTable extends JTable {

        public MyJTable(TableSorter sorter) {
            super(sorter);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void valueChanged(ListSelectionEvent e) {
            try {
                if (e != null) {
                    int selected = this.getSelectedRow();
                    if (selected >= 0) {
                        JTable nestedTable = (JTable) this.getValueAt(selected, 4);
                        nestedTable.selectAll();
                    }
                    for (int i = 0; i < this.getRowCount(); i++) {
                        if (i != selected) {
                            JTable nestedTable = (JTable) this.getValueAt(i, 4);
                            nestedTable.clearSelection();
                        }
                    }
                }
            } catch (Exception ee) {
            }
            super.valueChanged(e);
        }
    }

    class MyNestedJTable extends JTable {

        String toText = "";
        Object[][] data = null;
        Object[] title = null;

        public MyNestedJTable(Object[][] data, Object[] title) {
            super(data, title);
            this.data = data;
            this.title = title;
            toText = "";
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    toText += data[i][j];
                }
            }
        }

        public String toString() {
            return toText;
        }

        public Object clone() {
            return new MyNestedJTable(data, title);
        }
    }

    class TableItem {

        private String txt;
        private boolean bold;

        public TableItem(String txt, boolean bold) {
            this.txt = txt;
            this.bold = bold;
        }

        public String toString() {
            return txt;
        }

        public boolean isBold() {
            return bold;
        }
    }

    class TableInTableRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            try {
                if (value == null) {
                    return null;
                }
                return (Component) value;
            } catch (Exception e) {
                return null;
            }
        }
    }

    class HighlightTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            try {
                if (value == null) {
                    return null;
                }
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String txt = value.toString();
//            if (txt.startsWith(FahrtenbuchAnzeigenFrame.BOLD)) {
                if (((TableItem) value).isBold()) {
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
//                table.setValueAt(txt.substring(FahrtenbuchAnzeigenFrame.BOLD.length()), row, column);
                }
                return this;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
