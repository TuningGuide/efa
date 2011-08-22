/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.util.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public class ItemTypeTable extends ItemType implements ActionListener {

    protected String value;

    protected Table table;
    protected TableCellRenderer renderer;
    protected JScrollPane scrollPane;
    protected EfaMouseListener mouseListener;
    protected JPopupMenu popup;
    protected TableItemHeader[] header;
    protected String[] keys;
    protected Hashtable<String,TableItem[]> items; // keys -> columns for key
    protected String[] popupActions;
    protected int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

    public ItemTypeTable(String name, TableItemHeader[] header, Hashtable<String,TableItem[]> items, String value,
            int type, String category, String description) {
        ini(name, header, items, value, type, category, description);
    }

    public ItemTypeTable(String name, String[] header, Hashtable<String,TableItem[]> items, String value,
            int type, String category, String description) {
        ini(name, createTableHeader(header), items, value, type, category, description);
    }

    public IItemType copyOf() {
        return new ItemTypeTable(name, header.clone(), (Hashtable<String,TableItem[]>)items.clone(), value, type, category, description);
    }


    private void ini(String name, TableItemHeader[] header, Hashtable<String,TableItem[]> items, String value,
            int type, String category, String description) {
        this.name = name;
        this.header = header;
        this.items = items;
        if (items != null) {
            this.keys = items.keySet().toArray(new String[0]);
            Arrays.sort(keys);
        }
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
        fieldWidth = 600;
        fieldHeight = 300;
        fieldGridAnchor = GridBagConstraints.CENTER;
        fieldGridFill = GridBagConstraints.NONE;
    }

    private static TableItemHeader[] createTableHeader(String[] header) {
        TableItemHeader[] h = new TableItemHeader[header.length];
        for (int i=0; i<h.length; i++) {
            h[i] = new TableItemHeader(header[i]);
        }
        return h;
    }

    private TableItem[][] createTableData(String[][] data) {
        TableItem[][] d = new TableItem[data.length][];
        for (int i=0; i<d.length; i++) {
            d[i] = new TableItem[data[i].length];
            for (int j=0; j<data[i].length; j++) {
                d[i][j] = new TableItem(data[i][j], false);
            }
        }
        return d;
    }

    public void showValue() {
        Rectangle currentVisibleRect = null;
        int currentSortingColumn = -1;
        boolean currentSortingAscending = true;
        if (table != null) {
            currentVisibleRect = table.getVisibleRect();
            currentSortingColumn = table.getSortingColumn();
            currentSortingAscending = table.getSortingAscending();
        }

        if (keys != null && items != null) {
            TableItem[][] data = new TableItem[keys.length][];
            for (int i = 0; i < keys.length; i++) {
                data[i] = items.get(keys[i]);
            }
            if (scrollPane != null && table != null) {
                scrollPane.remove(table);
            }
            table = Table.createTable(null, renderer, header, data);
            table.setSelectionMode(selectionMode);
            if (currentSortingColumn < 0) {
                table.sortByColumn(0);
            } else {
                table.sortByColumn(currentSortingColumn, currentSortingAscending);
            }
        }
        if (scrollPane != null && table != null) {
            scrollPane.getViewport().add(table, null);

            if (popupActions != null) {
                popup = new JPopupMenu();
                for (int i = 0; i < popupActions.length; i++) {
                    JMenuItem menuItem = new JMenuItem(popupActions[i]);
                    menuItem.setActionCommand(EfaMouseListener.EVENT_POPUP_CLICKED + "_" + i);
                    menuItem.addActionListener(this);
                    popup.add(menuItem);
                }
            } else {
                popup = null;
            }

            for (int i = 0; keys != null && value != null && i < keys.length; i++) {
                if (value.equals(keys[i])) {
                    scrollToRow(i);
                    break;
                }
            }
            table.addMouseListener(mouseListener = new EfaMouseListener(table, popup, this, false));
            table.addFocusListener(new java.awt.event.FocusAdapter() {

                public void focusGained(FocusEvent e) {
                    field_focusGained(e);
                }

                public void focusLost(FocusEvent e) {
                    field_focusLost(e);
                }
            });

            this.field = table;
        }

        if (value == null && table != null && currentVisibleRect != null) {
            table.scrollRectToVisible(currentVisibleRect);
        }
    }

    public void scrollToRow(int i) {
        table.setRowSelectionInterval(i, i);
        table.scrollRectToVisible(table.getCellRect(i, 0, true));
    }

    protected void iniDisplay() {
        scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        scrollPane.setMinimumSize(new Dimension(fieldWidth, fieldHeight));
        showValue();
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        this.dlg = dlg;
        iniDisplay();
        panel.add(scrollPane, new GridBagConstraints(x, y, fieldGridWidth, fieldGridHeight, 0.0, 0.0,
                fieldGridAnchor, fieldGridFill, new Insets(padYbefore, padXbefore, padYafter, padXafter), 0, 0));
        return 1;
    }

    public int displayOnGui(Window dlg, JPanel panel, String borderLayoutPosition) {
        this.dlg = dlg;
        iniDisplay();
        panel.add(scrollPane, borderLayoutPosition);
        return 1;
    }

    public void actionPerformed(ActionEvent e) {
        actionEvent(e);
    }

    public void setValues(Hashtable<String,TableItem[]> items) {
        this.items = items;
        if (items != null) {
            keys = items.keySet().toArray(new String[0]);
            Arrays.sort(keys);
        }
        showValue();
    }

    public void parseValue(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public void getValueFromGui() {
        if (table != null && keys != null && table.getSelectedRow() >= 0) {
            value = keys[table.getSelectedRow()];
        }
    }

    public String getValueFromField() {
        if (table != null && keys != null && table.getSelectedRow() >= 0) {
            return keys[table.getSelectedRow()];
        }
        return toString(); // otherwise a hidden field in expert mode might return null
    }

    public boolean isValidInput() {
        return true;
    }

    public void setPopupActions(String[] actions) {
        this.popupActions = actions;
    }

    public void setVisible(boolean visible) {
        table.setVisible(visible);
        scrollPane.setVisible(visible);
        super.setVisible(visible);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        table.setEnabled(enabled);
        scrollPane.setEnabled(enabled);
    }

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
    }

}
