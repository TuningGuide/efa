/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

// @i18n complete

class Entry implements Comparable {

  int sort;
  String[] data;

  public Entry(int sort) {
    this.sort = sort;
  }

  public int compareTo(Object o) throws ClassCastException {
    try {
      float f1 = Float.parseFloat(this.data[sort]);
      float f2 = Float.parseFloat(((Entry)o).data[sort]);
      return (f1 < f2 ? -1 : (f1 > f2 ? 1 : 0));
//      Integer.parseInt(this.data[sort]);
//      Integer.parseInt(((Entry)o).data[sort]);
//      return EfaUtil.compareIntString(this.data[sort], ((Entry)o).data[sort] ); // Integer-Vergleich
    } catch(NumberFormatException e) {
      return this.data[sort].toLowerCase().compareTo( ((Entry)o).data[sort].toLowerCase() ); // String-Vergleich
    }
  }

}


public class ListenausgabeFrame extends JDialog implements ActionListener {
  String listenname;
  DatenListe datenListe;
  String[] felder;
  boolean[] selected;
  int sort;
  String[] nur;
  int[] nurCheck;
  JCheckBox[] check;
  JRadioButton[] radio;
  Entry[] entries;
  BorderLayout borderLayout1 = new BorderLayout();
  JButton createButton = new JButton();
  JPanel mainPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  ButtonGroup buttongroup = new ButtonGroup();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList nurList = new JList();


  public ListenausgabeFrame(JDialog parent, String listenname, DatenListe datenListe, String[] felder, boolean[] selected, int sort, String[] nur, int[] nurCheck) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);

    this.listenname = listenname; // Name der Liste
    this.datenListe = datenListe; // Datenliste
    this.felder = felder;         // Feldbezeichnungen
    this.selected = selected;     // Liste von selektierten Feldern
    this.sort = sort;             // Nummer des Sortierkrieteriums in felder-Liste
    this.nur = nur;               // Liste von Bezeichnungen für "nur ausgeben"-Liste
    this.nurCheck = nurCheck;     // Array von Feldern, die vor der Ausgabe auf Vorhandensein in der nur-Liste überprüft werden müssen

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    iniFrame();
    EfaUtil.pack(this);
    createButton.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
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
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    createButton.setText(International.getMessage("{listname} erstellen",listenname));
    createButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createButton_actionPerformed(e);
      }
    });
    this.getContentPane().setLayout(borderLayout1);
    mainPanel.setLayout(gridBagLayout1);
    jLabel1.setText(International.getString("Felder ausgeben")+": ");
    jLabel2.setText(International.getString("Sortieren nach")+": ");
    this.setTitle(International.getMessage("Erstellen einer {listname}",listenname));
    this.getContentPane().add(createButton, BorderLayout.SOUTH);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
    mainPanel.add(jLabel2,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 50, 0, 10), 0, 0));
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  void iniFrame() {
    check = new JCheckBox[felder.length];
    radio = new JRadioButton[felder.length];
    int i;
    for (i=0; i<felder.length; i++) {
      JCheckBox checkBox = new JCheckBox();
      checkBox.setText(felder[i]);
      checkBox.setSelected(selected[i]);
      mainPanel.add(checkBox,  new GridBagConstraints(0, i+1, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      check[i] = checkBox;
      JRadioButton radioButton = new JRadioButton();
      radioButton.setText(felder[i]);
      radioButton.setSelected(i == sort);
      buttongroup.add(radioButton);
      mainPanel.add(radioButton,   new GridBagConstraints(1, i+1, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 50, 0, 10), 0, 0));
      radio[i] = radioButton;
    }
    if (nur != null) {
      JLabel label = new JLabel();
      label.setText(International.getString("nicht ausgeben")+": ");
      mainPanel.add(label,          new GridBagConstraints(0, i+2, 2, 1, 0.0, 0.0
              ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 10, 0, 10), 0, 0));
      mainPanel.add(jScrollPane1,   new GridBagConstraints(0, i+3, 2, 1, 0.0, 0.0
              ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
      jScrollPane1.getViewport().add(nurList, null);
      nurList.setListData(nur);
    }
  }


  void createButton_actionPerformed(ActionEvent e) {
    for (int i=0; i<selected.length; i++)
      selected[i] = check[i].isSelected();
    for (int i=0; i<radio.length; i++)
      if (radio[i].isSelected()) sort = i;

    int length = readListe();

    if (length == 0) {
      Dialog.error(International.getString("Die Liste ist leer!"));
      return;
    }

    Arrays.sort(entries,0,length);

    ausgabeListe(length);
    cancel();
  }

  int readListe() {
    Hashtable nur = new Hashtable();
    if (this.nur != null)
      for (int i=0; i<this.nur.length; i++)
        if (!nurList.isSelectedIndex(i)) nur.put(this.nur[i],"+");

    DatenFelder eintrag = (DatenFelder)datenListe.getCompleteFirst();
    entries = new Entry[datenListe.countElements()];
    int i=0;
    String s;
    boolean skip;
    if (eintrag != null) do {
       // prüfen, ob dieser Eintrag ausgegeben werden soll
      if (nurCheck != null) {
        skip = false;
        for (int j=0; j<nurCheck.length; j++)
          if (nur.get( (s=eintrag.get(nurCheck[j]))) == null) {
            if (nurCheck[j] == Boote.VEREIN && // Wenn Vereinsname bei Booten (sehr häßliche Lösung, quick und dirty, aber funktioniert)
                 ( (s.equals("") && nur.get(International.getString("eigene Boote"))!=null) ||
                   (!s.equals("") && nur.get(International.getString("fremde Boote"))!=null) )) continue; // Ok, kein Skip
            skip = true;
          }
        if (skip) continue;
      }

      entries[i] = new Entry(sort);
      entries[i].data = new String[selected.length];
      for (int j=0; j<selected.length; j++)
        entries[i].data[j] = eintrag.get(j);
      ++i;
    } while ( (eintrag = (DatenFelder)datenListe.getCompleteNext()) != null);
    return i;
  }

  void ausgabeListe(int length) {
    try {
      String filename = Daten.efaTmpDirectory+"liste.html";

      String title = datenListe.getFileName();
      if (title.lastIndexOf(Daten.fileSep)>0 && !title.endsWith(Daten.fileSep)) title = title.substring(title.lastIndexOf(Daten.fileSep)+1,title.length());
      if (title.lastIndexOf(".")>0) title = title.substring(0,title.lastIndexOf("."));
      title = listenname+" "+title;
      EfaUtil.getNameOfFile(title);

      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),Daten.ENCODING_UTF));
      f.write("<html>\n");
      f.write("<head>\n");
      f.write("<meta http-equiv=\"content-type\" content=\"text/html; charset="+Daten.ENCODING_UTF+"\">\n");
      f.write("<title>"+title+"</title>\n");
      f.write("</head>\n");
      f.write("<body>\n");

      f.write("<h1 align=\"center\">"+title+"</h1>\n");
      f.write("<table align=\"center\" border=\"3\">\n");
      f.write("<tr>");
      for (int j=0; j<selected.length; j++)
        if (selected[j]) f.write("<th>"+felder[j]+"</th>");
      f.write("</tr>\n");
      for (int i=0; i<length; i++) {
        f.write("<tr>");
        for (int j=0; j<selected.length; j++)
          if (selected[j]) f.write("<td>"+entries[i].data[j]+"</td>");
        f.write("</tr>\n");
      }
      f.write("</table>\n");
      f.write("</body></html>\n");
      f.close();

      Dialog.neuBrowserDlg(this,International.getString("Ausgabe"),"file:"+filename);
      EfaUtil.deleteFile(filename);
    } catch(IOException e) {
      Dialog.error(International.getMessage("Liste konnte nicht erstellt werden: {error}",e.toString()));
    }
  }


}
