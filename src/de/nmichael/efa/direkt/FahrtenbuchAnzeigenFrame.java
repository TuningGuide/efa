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

import de.nmichael.efa.efa1.Fahrtenbuch;
import de.nmichael.efa.efa1.DatenFelder;
import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.Vector;
import de.nmichael.efa.*;

// @i18n complete

public class FahrtenbuchAnzeigenFrame extends JDialog implements ActionListener {

  public static final String BOLD = "[B]";

  static boolean wirdBereitsAngezeigt = false;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField anzahl = new JTextField();
  JLabel jLabel2 = new JLabel();
  JCheckBox auchUnvollstaendige = new JCheckBox();
  JButton okButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable table = null;
  TimeoutThread timeoutThread;


  class TimeoutThread extends Thread {
    private FahrtenbuchAnzeigenFrame frame;

    public TimeoutThread(FahrtenbuchAnzeigenFrame frame) {
      this.frame = frame;
    }

    public void run() {
      try {
        Thread.sleep(Daten.WINDOWCLOSINGTIMEOUT*1000);
      } catch(InterruptedException e) {
        return;
      }
      try {
        if (Dialog.frameCurrent() == frame) frame.cancel(true);
      } catch(Exception e) {
        EfaUtil.foo(); // laut Frederick Hoppe (21.07.2005) kann es hier zu einer Exception kommen
      }
    }
  }

  class MyJTable extends JTable {

    public MyJTable(TableSorter sorter) {
      super(sorter);
    }

    public boolean isCellEditable(int row, int column) { return false; }

    public void valueChanged(ListSelectionEvent e) {
      try {
        if (e != null) {
          int selected = this.getSelectedRow();
          if (selected >= 0) {
            JTable nestedTable = (JTable)this.getValueAt(selected,4);
            nestedTable.selectAll();
          }
          for (int i=0; i<this.getRowCount(); i++) {
            if (i != selected) {
              JTable nestedTable = (JTable)this.getValueAt(i,4);
              nestedTable.clearSelection();
            }
          }
        }
      } catch(Exception ee) {
      }
      super.valueChanged(e);
    }


  }

  class MyNestedJTable extends JTable {
    String toText = "";
    Object[][] data = null;
    Object[] title = null;

    public MyNestedJTable(Object[][] data, Object[] title) {
      super(data,title);
      this.data = data;
      this.title = title;
      toText = "";
      for (int i=0; i<data.length; i++) {
        for (int j=0; j<data[i].length; j++) {
          toText += data[i][j];
        }
      }
    }

    public String toString() {
      return toText;
    }

    public Object clone() {
      return new MyNestedJTable(data,title);
    }

  }




  public FahrtenbuchAnzeigenFrame(EfaDirektFrame parent) {
    super(parent);

    wirdBereitsAngezeigt = true;

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
    if (Daten.efaConfig.efaDirekt_startMaximized.getValue()) {
      this.setSize(Dialog.screenSize.width,Dialog.screenSize.height);
    } else {
      this.setSize((Dialog.screenSize.width*98)/100,(Dialog.screenSize.height*95)/100);
    }
    auchUnvollstaendige.setSelected(Daten.efaConfig.efaDirekt_FBAnzeigenAuchUnvollstaendige.getValue());
    showFahrten(getAnzahlFromField());
    tableRequestFocus();

    timeoutThread = new TimeoutThread(this);
    timeoutThread.start();
  }

  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel(false);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.showHelp(getClass().getCanonicalName());
    }
  }


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      jPanel2.setLayout(gridBagLayout1);

      int anz = 50;
      String t = International.getMessage("Nur die letzten {number} Fahrten anzeigen",anz);
      if (t != null && t.length()>0) {
          int pos = t.indexOf(Integer.toString(anz));
          if (pos >= 0) {
              String t1 = t.substring(0,pos);
              String t2 = t.substring(pos+2);
              Mnemonics.setLabel(this, jLabel1, t1);
              jLabel2.setText(t2);
          }
      }
      
      jLabel1.setLabelFor(anzahl);
      anzahl.setNextFocusableComponent(auchUnvollstaendige);
      Dialog.setPreferredSize(anzahl,50,17);
      if (Daten.efaConfig.efaDirekt_anzFBAnzeigenFahrten.getValue() > 0) {
          anz = Daten.efaConfig.efaDirekt_anzFBAnzeigenFahrten.getValue();
      }
      if (anz > Daten.efaConfig.efaDirekt_maxFBAnzeigenFahrten.getValue()) {
          anz = Daten.efaConfig.efaDirekt_maxFBAnzeigenFahrten.getValue();
      }
      anzahl.setText(Integer.toString(anz));
      anzahl.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          anzahl_keyReleased(e);
        }
      });
      anzahl.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          anzahl_focusLost(e);
        }
      });
      auchUnvollstaendige.setNextFocusableComponent(okButton);
      Mnemonics.setButton(this, auchUnvollstaendige, International.getStringWithMnemonic("auch unvollständige Fahrten (Boote, die noch unterwegs sind) zeigen"));
      auchUnvollstaendige.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          auchUnvollstaendige_actionPerformed(e);
        }
    });
      okButton.setNextFocusableComponent(anzahl);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Schließen"));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel1.setPreferredSize(new Dimension(750, 500));
      this.setTitle(International.getString("Fahrtenbuch anzeigen"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel2,  BorderLayout.NORTH);
      jPanel2.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(anzahl,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(auchUnvollstaendige,    new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel(false);
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel(boolean timeout) {
    try {
      if (!timeout && timeoutThread != null && timeoutThread.isAlive()) timeoutThread.interrupt();
    } catch(Exception e) {}
    Dialog.frameClosed(this);
    dispose();
    wirdBereitsAngezeigt = false;
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  public void tableRequestFocus() {
    if (table != null) table.requestFocus();
  }


  public void showFahrten(int max) {
    if (table != null) jScrollPane1.remove(table);

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
    DatenFelder d;
    int c;
    String s = "";
    for (c=max, d = Daten.fahrtenbuch.getCompleteLast(); c>0 && d != null; d = Daten.fahrtenbuch.getCompletePrev()) {
      if (d.get(Fahrtenbuch.BOOTSKM).equals("0") && !auchUnvollstaendige.isSelected()) continue;
      c--;

      int obmann = EfaUtil.string2int(d.get(Fahrtenbuch.OBMANN),-1);
      fahrten[c][0] = d.get(Fahrtenbuch.LFDNR);
      fahrten[c][1] = d.get(Fahrtenbuch.DATUM);
      fahrten[c][2] = d.get(Fahrtenbuch.BOOT);
      fahrten[c][3] = new TableItem(d.get(Fahrtenbuch.STM), obmann == 0); // (obmann == 0 ? BOLD : "") + d.get(Fahrtenbuch.STM);

      int mRowCount = 0;
      for (int i=Fahrtenbuch.MANNSCH1; i<=Fahrtenbuch.MANNSCH24; i++) {
        if (!d.get(i).equals("")) mRowCount++;
      }
      if (mRowCount == 0) mRowCount = 1;
      Object[][] mRowData = new Object[mRowCount][1];
      for (int i=Fahrtenbuch.MANNSCH1, ii=0; i<=Fahrtenbuch.MANNSCH24; i++) {
        if (!d.get(i).equals("")) {
          mRowData[ii][0] = new TableItem(d.get(i), obmann == ii+1); // (obmann == ii+1 ? BOLD : "") + d.get(i);
          ii++;
        }
      }
      Object[] mRowTitle = new Object[1];
      mRowTitle[0] = "foo";
      MyNestedJTable mTable = new MyNestedJTable(mRowData,mRowTitle) {
        public boolean isCellEditable(int row, int column) { return false; }
      };
      mTable.getColumn("foo").setCellRenderer(new HighlightTableCellRenderer());
      mTable.setShowGrid(false);
      fahrten[c][4] = mTable;

      fahrten[c][5] = d.get(Fahrtenbuch.ABFAHRT);
      fahrten[c][6] = d.get(Fahrtenbuch.ANKUNFT);
      fahrten[c][7] = d.get(Fahrtenbuch.ZIEL);
      fahrten[c][8] = ( (s = d.get(Fahrtenbuch.BOOTSKM)).equals("0") ? "" : s );
      fahrten[c][9] = d.get(Fahrtenbuch.BEMERK);
    }
    if (c > 0) {
      Object[][] fahrtentmp = new Object[max-c][10];
      for (int xorg=c, xnew=0; xorg<max; xorg++, xnew++)
        for (int y=0; y<10; y++) fahrtentmp[xnew][y] = fahrten[xorg][y];
      fahrten = fahrtentmp;
    }

    TableSorter sorter = new TableSorter(new DefaultTableModel(fahrten,title));
    table = new MyJTable(sorter);
    table.getColumn(International.getString("Steuermann")).setCellRenderer(new HighlightTableCellRenderer());
    table.getColumn(International.getString("Mannschaft")).setCellRenderer(new TableInTableRenderer());
//table.getColumn("Mannschaft").setCellEditor(new TableInTableEditor(new JCheckBox()));

    for (int i=0; i<fahrten.length; i++) {
      int orgHeight = table.getRowHeight(i);
      int newHeight = 0;
      try {
        newHeight = (int)((JTable)table.getValueAt(i,4)).getPreferredSize().getHeight();
      } catch(Exception e) { EfaUtil.foo(); }
      if (newHeight > orgHeight) table.setRowHeight(i,newHeight);
    }
    sorter.addMouseListenerToHeaderInTable(table);
    jScrollPane1.getViewport().add(table, null);
    try {
      table.scrollRectToVisible(table.getCellRect(fahrten.length-1,0,true));
    } catch(Exception e) {}
    table.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) cancel(false);
      }
    });

    // intelligente Spaltenbreiten
    int width = table.getSize().width;
    if (width < this.getSize().width-20 || width > this.getSize().width) { // beim ersten Aufruf steht Tabellenbreite noch nicht (korrekt) zur Verfügung, daher dieser Plausi-Check
      width = this.getSize().width-10;
    }

    int[] widths = new int[11];
    int remaining = width;
    for (int i=0; i<10; i++) {
      switch (i) {
        case 0: widths[i] =  5 * width / 100; // LfdNr
                if (widths[i] > 40) widths[i] = 40;
                break;
        case 1: widths[i] =  8 * width / 100; // Datum
                if (widths[i] > 80) widths[i] = 80;
                break;
        case 5: widths[i] =  5 * width / 100; // Abfahrt
                if (widths[i] > 50) widths[i] = 50;
                break;
        case 6: widths[i] =  5 * width / 100; // Ankunft
                if (widths[i] > 50) widths[i] = 50;
                break;
        case 8: widths[i] =  4 * width / 100; // Boots-Km
                if (widths[i] > 30) widths[i] = 30;
                break;
      }
      remaining -= widths[i];
    }

    for (int i=0; i<10; i++) {
      switch (i) {
        case 2: widths[i] = 18 * remaining / 100; break; // Boot
        case 3: widths[i] = 22 * remaining / 100; break; // Stm
        case 4: widths[i] = 22 * remaining / 100; break; // Mannsch
        case 7: widths[i] = 28 * remaining / 100; break; // Ziel
        case 9: widths[i] = 10 * remaining / 100; break; // Bemerkungen
      }
    }

    for (int i=0; i<10; i++) {
      table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    table.validate();

  }

  int getAnzahlFromField() {
    int anz = Math.abs(EfaUtil.string2int(anzahl.getText().trim(),1));
    if (anz > Daten.efaConfig.efaDirekt_maxFBAnzeigenFahrten.getValue()) {
      anz = Daten.efaConfig.efaDirekt_maxFBAnzeigenFahrten.getValue();
      anzahl.setText(Integer.toString(anz));
    }
    return anz;
  }

  void auchUnvollstaendige_actionPerformed(ActionEvent e) {
    int anz = getAnzahlFromField();
    showFahrten(anz);
  }

  void anzahl_focusLost(FocusEvent e) {
    int anz = getAnzahlFromField();
    anzahl.setText(Integer.toString(anz));
    showFahrten(anz);
  }

  void okButton_actionPerformed(ActionEvent e) {
    cancel(false);
  }

  void anzahl_keyReleased(KeyEvent e) {
    // if (e != null && e.getKeyCode() == 10)
    int anz = getAnzahlFromField();
    showFahrten(anz);
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
            if (((TableItem)value).isBold()) {
                c.setFont(c.getFont().deriveFont(Font.BOLD));
//                table.setValueAt(txt.substring(FahrtenbuchAnzeigenFrame.BOLD.length()), row, column);
            }
            return this;
        } catch (Exception e) {
            return null;
        }
    }
}
