/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.gui.util;

import de.nmichael.efa.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.Vector;

public class Table extends JTable {

    BaseDialog dlg;
    TableSorter sorter;
    TableItemHeader[] header;
    TableItem[][] data;
    private boolean dontResize = false;

    public Table(BaseDialog dlg, TableSorter sorter, TableItemHeader[] header, TableItem[][] data) {
        super(sorter);
        this.dlg = dlg;
        this.sorter = sorter;
        this.header = header;
        this.data = data;

        setDefaultRenderer(Object.class, new de.nmichael.efa.gui.util.TableCellRenderer());
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sorter.addMouseListenerToHeaderInTable(this);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancel();
                }
            }
        });
        addMouseListener(new TableMouseListener());

        validate();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void doLayout() {
        super.doLayout();
        if (!dontResize) {
            dontResize = true;
            setIntelligentColumnWidth();
            validate();
            dontResize = false;
        }
    }

    private void setIntelligentColumnWidth() {
        int width = getSize().width;
        if (width < this.getSize().width - 20 || width > this.getSize().width) { // beim ersten Aufruf steht Tabellenbreite noch nicht (korrekt) zur Verfügung, daher dieser Plausi-Check
            width = this.getSize().width - 10;
        }

        int absoluteWidth = 0;
        for (int i=0; i<header.length; i++) {
            absoluteWidth += header[i].getMaxColumnWidth();
        }

        int[] widths = new int[header.length];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = (int) Math.floor((((float)header[i].getMaxColumnWidth()) / ((float)absoluteWidth)) * ((float)width));
        }

        for (int i = 0; i < widths.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public int getSelectedRow() {
        int row = super.getSelectedRow();
        if (row >= 0) {
            return sorter.getOriginalIndex(row);
        } else {
            return row;
        }
    }

    public int[] getSelectedRows() {
        int[] rows = super.getSelectedRows();
        for (int i=0; rows != null && i<rows.length; i++) {
            rows[i] = sorter.getOriginalIndex(rows[i]);
        }
        return rows;
    }

    private void cancel() {
        if (dlg != null) {
            dlg.cancel();
        }
    }
    
    public static Table createTable(BaseDialog dlg, TableItemHeader[] header, TableItem[][] data) {
        for (int i=0; i<data.length; i++) {
            for (int j=0; j<data[i].length; j++) {
                header[j].updateColumnWidth(data[i][j].toString());
            }
        }
        TableSorter sorter = new TableSorter(new DefaultTableModel(data, header));
        Table t = new Table(dlg, sorter, header, data);
        return t;
    }

    class TableMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof Table) {
                Table t = (Table)e.getSource();
                if (e.getButton() == 3 && e.getClickCount() == 1) {
                    int row = t.rowAtPoint(new Point(e.getX(), e.getY()));
                    if (row >= 0) {
                        t.setRowSelectionInterval(row, row);
                    }
                }
            }
            super.mousePressed(e);
        }
    }

}
