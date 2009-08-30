package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class RestoreBackupFrame extends JDialog implements ActionListener {
  FileInfo[] fileInfo = null;
  int[] table2fileInfo = null;

  String[][] fileTypes = { { "EFB" , "Fahrtenbuch" } ,
                           { "EFBM", "Mitgliederliste" } ,
                           { "EFBB", "Bootsliste" } ,
                           { "EFBZ", "Zielliste" } ,
                           { "EFBS", "Statistikeinstellungen" } ,
                           { "SYN" , "Synonymliste" } ,
                           { "EFV" , "Vereinskonfiguration" } ,
                           { "EFD" , "Adreßliste" }
                         };

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel2 = new JPanel();
  JButton einspielenButton = new JButton();
  JButton schliessenButton = new JButton();
  JLabel jLabel2 = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JComboBox auswahlFb = new JComboBox();
  JLabel jLabel3 = new JLabel();
  JComboBox auswahlTyp = new JComboBox();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable fileList = new JTable();


  class FileInfo implements Comparable {
    String fname;
    String ftyp;
    String ftypname;
    String fdate;
    String fbak;
    String orgname;
    long time;

    public int compareTo(Object o) throws ClassCastException {
      FileInfo b = (FileInfo)o;
      if (this.time >= b.time) return -1;
      else return 1;
    }

  }


  public RestoreBackupFrame(EfaFrame efaFrame) {
    super(efaFrame);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    frIni();
    auswahlTyp.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
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

    this.setTitle("Backups einspielen");
    this.getContentPane().setLayout(borderLayout1);
    jLabel1.setText("Auswahl der anzuzeigenden Backups");
    einspielenButton.setNextFocusableComponent(schliessenButton);
    einspielenButton.setMnemonic('E');
    einspielenButton.setText("Backup einspielen");
    einspielenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        einspielenButton_actionPerformed(e);
      }
    });
    schliessenButton.setNextFocusableComponent(auswahlTyp);
    schliessenButton.setMnemonic('C');
    schliessenButton.setText("Schließen");
    schliessenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        schliessenButton_actionPerformed(e);
      }
    });
    jLabel2.setDisplayedMnemonic('F');
    jLabel2.setLabelFor(auswahlFb);
    jLabel2.setText("Backups für: ");
    jPanel1.setLayout(gridBagLayout1);
    jLabel3.setDisplayedMnemonic('T');
    jLabel3.setLabelFor(auswahlTyp);
    jLabel3.setText("Dateityp: ");
    auswahlFb.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        auswahlFb_itemStateChanged(e);
      }
    });
    auswahlTyp.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        auswahlTyp_itemStateChanged(e);
      }
    });
    jScrollPane1.setPreferredSize(new Dimension(700, 400));
    auswahlTyp.setNextFocusableComponent(auswahlFb);
    auswahlTyp.setMaximumRowCount(12);
    auswahlFb.setNextFocusableComponent(einspielenButton);
    this.getContentPane().add(jPanel1,  BorderLayout.NORTH);
    jPanel1.add(jLabel1,    new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    jPanel1.add(jLabel2,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 41), 0, 0));
    jPanel1.add(auswahlFb,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel3,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(auswahlTyp,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(einspielenButton, null);
    jPanel2.add(schliessenButton, null);
    this.getContentPane().add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(fileList, null);
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

  void frIni() {
    auswahlTyp.addItem("alle Datenlisten");
    for (int i=0; i<fileTypes.length; i++)
      auswahlTyp.addItem("nur "+fileTypes[i][1]);
    createFileList();
    auswahlFb.addItem("aktuelles Fahrtenbuch und global");
    auswahlFb.addItem("alle Fahrtenbücher");
  }

  void createFileList() {
    File dir = new File(Daten.efaBakDirectory);
    if (!dir.isDirectory()) return;
    File[] files = dir.listFiles();

    fileInfo = new FileInfo[files.length];
    int c = 0;

    int pos;
    String fn,ft,fb; fn = ft = fb = null;
    for (int i=0; i<files.length; i++) {
      fn = files[i].getName(); ft=null;
      pos = fn.lastIndexOf(".");
      if (pos>=0) {
        fb = fn.substring(pos+1).toUpperCase();
        fn = fn.substring(0,pos);
        pos = fn.lastIndexOf(".");
        if (pos>=0) ft = fn.substring(pos+1).toUpperCase();
      }

      if (ft != null) {
        int typ = -1;
        for (int j=0; j<fileTypes.length; j++)
          if (ft.equals(fileTypes[j][0])) typ = j;
        if (typ >= 0) {
          fileInfo[c] = new FileInfo();
          fileInfo[c].fname    = fn;
          fileInfo[c].ftyp     = ft;
          fileInfo[c].ftypname = fileTypes[typ][1];
          fileInfo[c].fdate    = new Date(files[i].lastModified()).toString();
          fileInfo[c].fbak     = fb;
          fileInfo[c].orgname  = files[i].getName();
          fileInfo[c].time     = files[i].lastModified();
          c++;
        }
      }
    }
    Arrays.sort(fileInfo,0,c);
  }

  boolean currentFileToBeShown(String search, FileInfo f) {
    return (f != null &&
            (search == null || f.ftyp.equals(search) ) &&
            (auswahlFb.getSelectedIndex() == 1 || Daten.fahrtenbuch == null ||
             ( f.fname.equals(EfaUtil.getNameOfFile(Daten.fahrtenbuch.getFileName())) ||
               f.fname.equals(EfaUtil.getNameOfFile(Daten.fahrtenbuch.getDaten().mitgliederDatei)) ||
               f.fname.equals(EfaUtil.getNameOfFile(Daten.fahrtenbuch.getDaten().bootDatei)) ||
               f.fname.equals(EfaUtil.getNameOfFile(Daten.fahrtenbuch.getDaten().zieleDatei)) ||
               f.fname.equals(EfaUtil.getNameOfFile(Daten.fahrtenbuch.getDaten().statistikDatei))
             )
            )
           );
  }


  void rebuildFileList() {
    if (fileInfo == null) return;
    String search=null;
    if (auswahlTyp.getSelectedIndex()>0)
      search = fileTypes[auswahlTyp.getSelectedIndex()-1][0]; // search == null für "alle Typen"

    int anz = 0;
    for (int i=0; i<fileInfo.length; i++)
      if (currentFileToBeShown(search, fileInfo[i])) anz++;
    String[][] table = new String[anz][4];
    table2fileInfo = new int[anz];

    int c=0;
    for (int i=0; i<fileInfo.length; i++) {
      if (currentFileToBeShown(search, fileInfo[i])) {
        table[c][0] = fileInfo[i].fname;
        table[c][1] = fileInfo[i].ftypname;
        table[c][2] = fileInfo[i].fdate;
        table[c][3] = fileInfo[i].fbak;
        table2fileInfo[c] = i;
        c++;
      }
    }

    String[] title = new String[4];
    title[0] = "Dateiname"; title[1] = "Dateityp"; title[2] = "Datum"; title[3] = "Backup-Typ";
    jScrollPane1.remove(fileList);
    fileList = new JTable(table,title);
    fileList.setSelectionMode(0); // SINGLE_SELECTION
    jScrollPane1.getViewport().add(fileList,null);
  }

  void auswahlFb_itemStateChanged(ItemEvent e) {
    rebuildFileList();
  }

  void auswahlTyp_itemStateChanged(ItemEvent e) {
    rebuildFileList();
    if (auswahlTyp.getSelectedIndex()>5) {
      auswahlFb.setSelectedIndex(1);
      auswahlFb.setEnabled(false);
    } else auswahlFb.setEnabled(true);
  }

  void schliessenButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void einspielenButton_actionPerformed(ActionEvent e) {
    if (fileList.getSelectedRow() < 0) {
      Dialog.infoDialog("Keine Datei ausgewählt","Bitte wähle zuerst eine Datei aus!");
      return;
    }
    String restoreDir = null;
    String filename = fileInfo[table2fileInfo[fileList.getSelectedRow()]].fname;
    if (auswahlFb.getSelectedIndex() == 0 && Daten.fahrtenbuch != null) { // nur akt. Fb
      if (filename.toUpperCase().endsWith(".EFB"))  restoreDir = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName());
      if (filename.toUpperCase().endsWith(".EFBB")) restoreDir = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getDaten().boote.getFileName());
      if (filename.toUpperCase().endsWith(".EFBM")) restoreDir = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getDaten().mitglieder.getFileName());
      if (filename.toUpperCase().endsWith(".EFBZ")) restoreDir = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getDaten().ziele.getFileName());
      if (filename.toUpperCase().endsWith(".EFBS")) restoreDir = EfaUtil.getPathOfFile(Daten.fahrtenbuch.getDaten().statistik.getFileName());
    } else { // alle Fb
      restoreDir = Daten.efaDataDirectory;
    }
    if (!restoreDir.endsWith(Daten.fileSep)) restoreDir += Daten.fileSep;

    if (Dialog.yesNoDialog("Datei wiederherstellen","Soll die Datei '"+filename+"' ("+
                                            fileInfo[table2fileInfo[fileList.getSelectedRow()]].fdate+
                                            ") im Verzeichnis\n'"+
                                            restoreDir+"'\nwiederhergestellt werden?") == Dialog.YES) {
      // Restore Datei
      String info="";
      File old = new File(restoreDir+filename);
      if (old.isFile()) {
        if (new File(restoreDir+filename+".org").isFile()) new File(restoreDir+filename+".org").delete();
        old.renameTo(new File(restoreDir+filename+".org"));
        info += "Originaldatei nach '"+restoreDir+filename+".org' umbenannt.\n";
      }

      if (EfaUtil.copyFile(Daten.efaBakDirectory+fileInfo[table2fileInfo[fileList.getSelectedRow()]].orgname, restoreDir+filename)) {

        // ggf. wiederhergestellte Datei neu einlesen
        if (Daten.fahrtenbuch != null && (restoreDir+filename).equals(Daten.fahrtenbuch.getFileName())) {
          einlesen(Daten.fahrtenbuch);
        }
        else if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().boote != null && (restoreDir+filename).equals(Daten.fahrtenbuch.getDaten().boote.getFileName())) einlesen(Daten.fahrtenbuch.getDaten().boote);
        else if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null && (restoreDir+filename).equals(Daten.fahrtenbuch.getDaten().mitglieder.getFileName())) einlesen(Daten.fahrtenbuch.getDaten().mitglieder);
        else if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().ziele != null && (restoreDir+filename).equals(Daten.fahrtenbuch.getDaten().ziele.getFileName())) einlesen(Daten.fahrtenbuch.getDaten().ziele);
        else if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().statistik != null && (restoreDir+filename).equals(Daten.fahrtenbuch.getDaten().statistik.getFileName())) einlesen(Daten.fahrtenbuch.getDaten().statistik);
        else if (Daten.vereinsConfig != null && (restoreDir+filename).equals(Daten.vereinsConfig.getFileName())) einlesen(Daten.vereinsConfig);
        else if (Daten.synMitglieder != null && (restoreDir+filename).equals(Daten.synMitglieder.getFileName())) einlesen(Daten.synMitglieder);
        else if (Daten.synBoote != null && (restoreDir+filename).equals(Daten.synBoote.getFileName())) einlesen(Daten.synBoote);
        else if (Daten.synZiele != null && (restoreDir+filename).equals(Daten.synZiele.getFileName())) einlesen(Daten.synZiele);
        else if (Daten.adressen != null && (restoreDir+filename).equals(Daten.adressen.getFileName())) einlesen(Daten.adressen);

        info += "Datei '"+filename+"' erfolgreich wiederhergestellt!";
      } else
        info += "Datei '"+filename+"' konnte NICHT wiederhergestellt werden!";



      Dialog.infoDialog("Ergebnis",info);
    }
  }

  void einlesen(DatenListe l) {
    l.readFile();
    l.writeFile();
  }


}
