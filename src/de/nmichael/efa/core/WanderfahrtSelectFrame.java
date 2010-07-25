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
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.direkt.*;

// @i18n complete

public class WanderfahrtSelectFrame extends JDialog implements ActionListener {
  private static String selectedWafa = null;

  Frame parent;
  String lfdnr;
  String datum;
  String boot;
  String mannschaft;
  String ziel;
  String[] wafaNamen = null;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JTextArea info = new JTextArea();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton okButton = new JButton();
  JButton abortButton = new JButton();
  JButton addButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList list = new JList();


  public WanderfahrtSelectFrame(Frame parent, String lfdnr, String datum, String boot, String mannschaft, String ziel) {
    super(parent);
    this.lfdnr = lfdnr;
    this.datum = datum;
    this.boot = boot;
    this.mannschaft = mannschaft;
    this.ziel = ziel;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      iniWafaList();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.parent = parent;
    // this.requestFocus();
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


  // Initialisierung des Frames
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      this.setTitle(International.getString("Mehrtagesfahrt auswählen"));
      jPanel1.setLayout(borderLayout1);
      jPanel3.setLayout(gridBagLayout1);
      jPanel2.setLayout(gridBagLayout2);
      Dialog.setPreferredSize(okButton,150,23);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("OK"));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      Dialog.setPreferredSize(abortButton,150,23);
      Mnemonics.setButton(this, abortButton, International.getStringWithMnemonic("Abbruch"));
      abortButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          abortButton_actionPerformed(e);
        }
    });
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Neue Mehrtagesfahrt"));
      addButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addButton_actionPerformed(e);
        }
    });
      info.setWrapStyleWord(true);
      info.setText(International.getString("Bitte ordne den Fahrtenbucheintrag einer Mehrtagesfahrt aus der "+
                   "angezeigten Liste zu und klicke 'OK'.") + "\n" +
                   International.getString("Wenn es sich um eine neue Mehrtagesfahrt handelt, die in der Liste "+
                   "nicht aufgeführt ist, klicke bitte 'Neue Mehrtagesfahrt'."));
      info.setEditable(false);
      info.setEnabled(false);
      info.setFont(info.getFont().deriveFont(Font.BOLD));
      info.setForeground(Color.blue);
      info.setDisabledTextColor(Color.blue);
      jScrollPane1.setPreferredSize(new Dimension(450, 300));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(info, BorderLayout.NORTH);
      jPanel1.add(jPanel2, BorderLayout.EAST);
      jPanel2.add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jPanel3, BorderLayout.SOUTH);
      jPanel3.add(okButton,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(abortButton,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(list, null);
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
  }

  void iniWafaList() {
    if (Daten.fahrtenbuch == null) return;
    wafaNamen = Daten.fahrtenbuch.getAllMehrtagesfahrtNamenByDate();
    if (wafaNamen == null || wafaNamen.length == 0) return;
    String[] listdata = new String[wafaNamen.length];
    for (int i=0; i<wafaNamen.length; i++) {
      Mehrtagesfahrt m = Daten.fahrtenbuch.getMehrtagesfahrt(wafaNamen[i]);
      listdata[i] = International.getMessage("{begin} bis {end}",m.start,m.ende) + ": " + m.name;
    }
    list.setListData(listdata);
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

  void addButton_actionPerformed(ActionEvent e) {
    MehrtagestourFrame dlg = new MehrtagestourFrame(this,datum,ziel);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    if (dlg.wanderfahrtSelectFrame_selectedWafa != null) {
      this.selectedWafa = dlg.wanderfahrtSelectFrame_selectedWafa;
      nachrichtAnAdmin(true);
      cancel();
    }
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (list.getSelectedIndex()<0 || wafaNamen == null || wafaNamen.length == 0 ||
        list.getSelectedIndex() >= wafaNamen.length) {
      Dialog.error(International.getString("Bitte wähle zunächst aus der Liste der Mehrtagesfahrten eine Fahrt aus "+
                   "oder erstelle einen neuen Eintrag."));
      return;
    }
    String wafa = this.wafaNamen[list.getSelectedIndex()];;
    Mehrtagesfahrt m = Daten.fahrtenbuch.getMehrtagesfahrt(wafa);
    if (m != null) {
      if (EfaUtil.secondDateIsAfterFirst(m.ende,this.datum) ||
          EfaUtil.secondDateIsAfterFirst(this.datum,m.start)) {
        int ret = Dialog.auswahlDialog(
                International.getString("Datum außerhalb der Fahrt"),
                International.getMessage("Das Datum des Fahrtenbucheintrags ({entry}) liegt außerhalb des Zeitraums " +
                " ({date_from} - {date_to}), der für die ausgewählte Mehrtagesfahrt '{name}' angegeben wurde.",
                datum, m.start, m.ende, m.name) +
                "\n\n" +
                International.getString("Falls in diesem Jahr mehrere Mehrtagesfahrten mit derselben Strecke durchgeführt wurden, " +
                "so erstelle bitte für jede einzelne Mehrtagesfahrt einen separaten Eintrag in efa. Ansonsten wähle bitte entweder " +
                "eine Mehrtagesfahrt mit passendem Datum aus oder korrigiere das Datum dieses Eintrages oder der Mehrtagesfahrt."),
                             International.getString("Datum korrigieren"),
                             International.getString("Andere Mehrtagesfahrt"));
        if (ret != 0) return;
      }
      this.selectedWafa =  wafa;
      nachrichtAnAdmin(false);
    }

    cancel();
  }

  void abortButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  private void nachrichtAnAdmin(boolean neueFahrt) {
    if (Daten.nachrichten != null && selectedWafa != null) {
      Nachricht n = new Nachricht();
      n.empfaenger = Nachricht.ADMIN;
      n.betreff = International.getString("Neue Mehrtagesfahrt");
      n.name = Daten.EFA_SHORTNAME;
      if (neueFahrt) {
        n.nachricht = International.getString("Eine neue Mehrtagesfahrt wurde eingetragen")+":\n\n";
      } else {
        n.nachricht = International.getString("Ein Eintrag wurde einer vorhandenen Mehrtagesfahrt zugeordnet")+":\n\n";
      }
      Mehrtagesfahrt m = Daten.fahrtenbuch.getMehrtagesfahrt(selectedWafa);
      n.nachricht = n.nachricht +
                    International.getString("Eintrag im Fahrtenbuch")+":\n"+
                    International.getString("LfdNr")+": "+lfdnr+"\n"+
                    International.getString("Datum")+": "+datum+"\n"+
                    International.getString("Boot")+": "+boot+"\n"+
                    International.getString("Mannschaft")+": "+mannschaft+"\n"+
                    International.getString("Ziel")+": "+ziel+"\n\n"+
                    International.getString("Mehrtagesfahrt")+":\n"+
                    International.getString("Name")+": "+selectedWafa+"\n"+
                    (m == null ? "*** "+International.getString("Mehrtagesfahrt nicht gefunden")+" ***\n" :
                    International.getString("Startdatum")+": "+m.start+"\n"+
                    International.getString("Enddatum")+": "+m.ende+"\n"+
                    International.getString("Rudertage")+": "+m.rudertage+"\n"+
                    International.getString("Gewässer")+": "+m.gewaesser+"\n");
      Daten.nachrichten.add(n);
      Daten.nachrichten.writeFile();

      if (Daten.efaConfig.admins.size() > 0) {
        n.sendEmail(Daten.efaConfig.admins.getKeysArray());
      }
    }

  }

  public static String selectWanderfahrt(Frame frame, String lfdnr, String datum, String boot, String mannschaft, String ziel) {
    selectedWafa = null;
    WanderfahrtSelectFrame dlg = new WanderfahrtSelectFrame(frame,lfdnr,datum,boot,mannschaft,ziel);
    Dialog.setDlgLocation(dlg,frame);
    dlg.setModal(true);
    dlg.show();
    return selectedWafa;
  }


}
