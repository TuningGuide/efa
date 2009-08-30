package de.nmichael.efa.direkt;

import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.util.SimpleFilePrinter;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class AdminNachrichtenFrame extends JDialog implements ActionListener {
  NachrichtenAnAdmin nachrichten;
  Admin admin;
  Vector nachrIds;
  int nID = -1; // ID der aktuell angezeigten Nachricht
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JCheckBox nurUngelesenCheckBox = new JCheckBox();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable uebersicht = null;
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextArea nachricht = new JTextArea();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  JCheckBox gelesenMarkierenCheckBox = new JCheckBox();
  JButton deleteButton = new JButton();
  JButton deleteAllButton = new JButton();
  JButton forwardButton = new JButton();
  JButton printButton = new JButton();


  public AdminNachrichtenFrame(AdminFrame parent, NachrichtenAnAdmin nachrichten, Admin admin) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.nachrichten = nachrichten;
    this.admin = admin;
    nurUngelesenCheckBox.setSelected(true);
    deleteButton.setVisible(admin.name.equals(EfaConfig.SUPERADMIN));
    deleteAllButton.setVisible(admin.name.equals(EfaConfig.SUPERADMIN));
    zeigeNachrichten();
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
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(nurUngelesenCheckBox);
      okButton.setMnemonic('O');
      okButton.setText("OK");
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      nurUngelesenCheckBox.setNextFocusableComponent(gelesenMarkierenCheckBox);
      nurUngelesenCheckBox.setToolTipText("nur ungelesene Nachrichten anzeigen");
      nurUngelesenCheckBox.setMnemonic('U');
      nurUngelesenCheckBox.setText("nur ungelesene");
      nurUngelesenCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          nurUngelesenCheckBox_actionPerformed(e);
        }
    });
      jScrollPane1.setPreferredSize(new Dimension(500, 200));
      jPanel3.setLayout(borderLayout2);
      jScrollPane2.setPreferredSize(new Dimension(700, 300));
      nachricht.setNextFocusableComponent(okButton);
      nachricht.setEditable(false);
      this.setTitle("Nachrichten an Admin");
      jLabel1.setText("Anzeigen:");
      gelesenMarkierenCheckBox.setNextFocusableComponent(forwardButton);
      gelesenMarkierenCheckBox.setToolTipText("die aktuelle Nachricht als gelesen markieren");
      gelesenMarkierenCheckBox.setMnemonic('G');
      gelesenMarkierenCheckBox.setText("Nachricht als gelesen markieren");
      gelesenMarkierenCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          gelesenMarkierenCheckBox_actionPerformed(e);
        }
    });
      deleteButton.setNextFocusableComponent(printButton);
      deleteButton.setMnemonic('L');
      deleteButton.setText("Nachricht(en) löschen");
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteButton_actionPerformed(e);
        }
    });
      deleteAllButton.setNextFocusableComponent(nachricht);
      deleteAllButton.setMnemonic('A');
      deleteAllButton.setText("Alle gelesenen Nachrichten löschen");
      deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteAllButton_actionPerformed(e);
        }
    });
      forwardButton.setNextFocusableComponent(deleteButton);
      forwardButton.setMnemonic('W');
      forwardButton.setText("Nachricht weiterleiten");
      forwardButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          forwardButton_actionPerformed(e);
        }
    });
      printButton.setNextFocusableComponent(deleteButton);
      printButton.setMnemonic('D');
      printButton.setText("Nachricht drucken");
      printButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          printButton_actionPerformed(e);
        }
    });
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.NORTH);
      jPanel2.add(nurUngelesenCheckBox,       new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jScrollPane1,          new GridBagConstraints(1, 0, 5, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(gelesenMarkierenCheckBox,    new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(deleteButton,        new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 0, 0));
      jPanel2.add(deleteAllButton,     new GridBagConstraints(4, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 0, 0));
      jPanel2.add(forwardButton,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(printButton,  new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jPanel3,  BorderLayout.CENTER);
      jPanel3.add(jScrollPane2,  BorderLayout.CENTER);
      jScrollPane2.getViewport().add(nachricht, null);
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

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
    nachrichten.writeFile();
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  void zeigeNachrichten() {
    nID = -1;
    Vector titel = new Vector();
    titel.add("Empfänger"); titel.add("Absender"); titel.add("Betreff"); titel.add("Datum");
    Vector nachr = new Vector();
    nachrIds = new Vector();
    for (int i=nachrichten.size()-1; i>=0; i--) {
      Nachricht n = (Nachricht)nachrichten.get(i);
      if (n.gelesen && nurUngelesenCheckBox.isSelected()) continue;
      if (n.empfaenger == Nachricht.ADMIN && !admin.allowedNachrichtenAnzeigenAdmin) continue;
      if (n.empfaenger == Nachricht.BOOTSWART && !admin.allowedNachrichtenAnzeigenBootswart) continue;
      Vector nr = new Vector();
      nr.add(Nachricht.getEmpfaengerName(n.empfaenger)); nr.add(n.name); nr.add(n.betreff); nr.add(n.datum);
      nachr.add(nr);
      nachrIds.add(new Integer(i));
    }
    if (uebersicht != null) jScrollPane1.getViewport().remove(uebersicht);
    uebersicht = new JTable(nachr,titel);
    uebersicht.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    jScrollPane1.getViewport().add(uebersicht, null);
    uebersicht.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        uebersicht_mouseClicked(e);
      }
    });
    showNachricht(-1);
  }

  void showNachricht(int id) {
    nID = id;
    if (id < 0) {
      nachricht.setText("");
      gelesenMarkierenCheckBox.setEnabled(false);
      return;
    }
    Nachricht n = (Nachricht)nachrichten.get(id);
    nachricht.setText("## Nachricht "+(id+1)+"/"+nachrichten.size()+"\n"+
                      "## Datum: "+n.datum+"\n"+
                      "## Von: "+n.name+"\n"+
                      "## An: "+Nachricht.getEmpfaengerName(n.empfaenger)+"\n"+
                      "## Betreff: "+n.betreff+"\n"+
                      "\n"+
                      n.nachricht);
    nachricht.setCaretPosition(0);

    // als gelesen markieren?
    gelesenMarkierenCheckBox.setEnabled( (n.empfaenger == Nachricht.ADMIN ? admin.nachrichtenAdminAllowedGelesenMarkieren : admin.nachrichtenBootswartAllowedGelesenMarkieren) );
    gelesenMarkierenCheckBox.setSelected(n.gelesen ||
                                         (n.empfaenger == Nachricht.ADMIN ? admin.nachrichtenAdminGelesenMarkierenDefault : admin.nachrichtenBootswartGelesenMarkierenDefault) );
    n.gelesen = gelesenMarkierenCheckBox.isSelected();
  }

  void nurUngelesenCheckBox_actionPerformed(ActionEvent e) {
    zeigeNachrichten();
  }

  void uebersicht_mouseClicked(MouseEvent e) {
    if (uebersicht == null) return; // kann wohl nicht passiere, oder?? ;-)
    if (uebersicht.getSelectedRowCount() != 1) return;
    if (uebersicht.getSelectedRow()<0) return;
    showNachricht(((Integer)(nachrIds.get(uebersicht.getSelectedRow()))).intValue());
  }

  void okButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void gelesenMarkierenCheckBox_actionPerformed(ActionEvent e) {
    if (nID < 0) return; // zur Zeit keine Nachricht angezeigt
    try {
      Nachricht n = (Nachricht)nachrichten.get(nID);
      n.gelesen = gelesenMarkierenCheckBox.isSelected();
    } catch(Exception ee) { EfaUtil.foo(); }
  }

  void deleteButton_actionPerformed(ActionEvent e) {
    if (uebersicht.getSelectedRowCount() > 1) {
      if (Dialog.yesNoDialog("Nachrichten wirklich löschen","Möchtest Du "+uebersicht.getSelectedRowCount()+" ausgewählte Nachrichten wirklich löschen?") != Dialog.YES) return;
      int[] selected = uebersicht.getSelectedRows();
      for (int i=0; selected != null && i<selected.length; i++) {
        Integer nID = (Integer)nachrIds.get(selected[i]);
        if (nID != null) nachrichten.delete(nID.intValue());
      }
    } else {
      if (nachrichten == null || nID < 0) {
        Dialog.error("Bitte wähle zuerst eine zu löschende Nachricht aus!");
        return;
      }
      if (Dialog.yesNoDialog("Nachricht wirklich löschen","Möchtest Du die Nachricht\n'"+nachrichten.get(nID).betreff+"'\nwirklich löschen?") != Dialog.YES) return;
      nachrichten.delete(nID);
    }
    zeigeNachrichten();
    showNachricht(-1);
  }

  void deleteAllButton_actionPerformed(ActionEvent e) {
    int c=0;
    for (int i=0; nachrichten != null && i<nachrichten.size(); i++) if (nachrichten.get(i).gelesen) c++;
    if (c == 0) {
      Dialog.error("Es gibt keine gelesenen Nachrichten!");
      return;
    }
    if (Dialog.yesNoDialog("Nachrichten wirklich löschen","Möchtest Du "+c+" gelesene Nachrichten wirklich löschen?") != Dialog.YES) return;
    for (int i=nachrichten.size()-1; i>=0; i--) if (nachrichten.get(i).gelesen) nachrichten.delete(i);
    zeigeNachrichten();
    showNachricht(-1);
  }

  void forwardButton_actionPerformed(ActionEvent e) {
    Nachricht n = null;
    try {
      n = (Nachricht)nachrichten.get(this.nID);
    } catch(Exception ee) {}
    if (n == null) return;

    int weiterAn = -1;
    String weiterAnS = null;
    if (n.empfaenger == Nachricht.ADMIN) {
      weiterAn = Nachricht.BOOTSWART;
      weiterAnS = "Bootswart";
    }
    if (n.empfaenger == Nachricht.BOOTSWART) {
      weiterAn = Nachricht.ADMIN;
      weiterAnS = "Admin";
    }
    if (weiterAn < 0) {
      Dialog.error("Weiterleiten dieser Nachricht nicht möglich!");
      return;
    }
    if (Dialog.yesNoCancelDialog("Nachricht weiterleiten",
                                 "Möchtest Du diese Nachricht an \""+weiterAnS+"\" weiterleiten?") != Dialog.YES) return;
    Nachricht nfwd = new Nachricht(weiterAn,n.datum,n.name,"Fwd: "+n.betreff,n.nachricht);
    nachrichten.add(nfwd);
    if (!nachrichten.writeFile()) {
      Dialog.error("Fehler beim Schreiben der Nachrichtendatei.");
      Logger.log(Logger.ERROR,"Nachrichtendatei konnte nicht gespeichert werden (Weiterleiten einer Nachricht).");
    }
  }

  void printButton_actionPerformed(ActionEvent e) {
    Nachricht n = null;
    try {
      n = (Nachricht)nachrichten.get(this.nID);
    } catch(Exception ee) {}
    if (n == null) return;

    String tmpdatei = Daten.efaTmpDirectory+"nachricht.html";
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpdatei),Daten.ENCODING));
      f.write("<html>\n");
      f.write("<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head>\n");
      f.write("<body>\n");
      f.write("<table>\n");
      f.write("<tr><td>##</td><td colspan=\"2\">Nachricht "+(nID+1)+"/"+nachrichten.size()+"</td></tr>\n");
      f.write("<tr><td>##</td><td>Datum:</td><td>"+n.datum+"</td></tr>\n");
      f.write("<tr><td>##</td><td>Von:</td><td>"+n.name+"</td></tr>\n");
      f.write("<tr><td>##</td><td>An:</td><td>"+Nachricht.getEmpfaengerName(n.empfaenger)+"</td></tr>\n");
      f.write("<tr><td>##</td><td>Betreff:</td><td>"+n.betreff+"</td></tr>\n");
      f.write("</table>\n");
      f.write("<hr>\n");
      f.write("<p>"+EfaUtil.replace(n.nachricht,"\n","<br>",true)+"</p>\n");
      f.write("</body></html>\n");
      f.close();
      JEditorPane out = new JEditorPane();
      out.setContentType("text/html; charset="+Daten.ENCODING);
      out.setPage("file:"+tmpdatei);
      out.setSize(600,800);
      out.doLayout();
      SimpleFilePrinter sfp = new SimpleFilePrinter(out);
      if (sfp.setupPageFormat()) {
        if (sfp.setupJobOptions()) {
          sfp.printFile();
        }
      }
      EfaUtil.deleteFile(tmpdatei);
    } catch(Exception ee) {
      Dialog.error("Druckdatei konnte nicht erstellt werden: "+ee.toString());
      return;
    }
  }


}
