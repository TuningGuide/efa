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
import de.nmichael.efa.core.AuswahlFrame;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// @i18n complete

public class MehrtagestourFrame extends JDialog implements ActionListener {
  EfaFrame efaFrame=null;
  AuswahlFrame auswahlFrame=null;
  WanderfahrtSelectFrame wanderfahrtSelectFrame=null;
  public String wanderfahrtSelectFrame_selectedWafa = null;
  String[][] tabelle = null;
  int tabIndex=-1;
  int editnr = 0;
  JButton okButton = new JButton();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  JTextField fahrt = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField start = new JTextField();
  JLabel anzTageLabel = new JLabel();
  JTextField anzTage = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField ende = new JTextField();
  JLabel jLabel4 = new JLabel();
  JCheckBox isEtappen = new JCheckBox();
  JTextField gewaesser = new JTextField();

  public MehrtagestourFrame(EfaFrame efaFrame, String startDate, String endDate, String rudertage) {
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
    this.efaFrame = efaFrame;
    fahrt.requestFocus();
    if (startDate != null && startDate.length()>0) start.setText(EfaUtil.correctDate(startDate));
    if (endDate != null && endDate.length()>0) ende.setText(EfaUtil.correctDate(endDate));
    if (rudertage != null && rudertage.length()>0) anzTage.setText(Integer.toString(EfaUtil.string2int(rudertage,0)));

    // Bezeichnung für die Fahrt ggf. aus Ziel-Feld im FB ermitteln
    if (efaFrame.ziel.getText().trim().length()>0) {
      fahrt.setText(efaFrame.ziel.getText().trim());
      fahrt.setSelectionStart(0);
      fahrt.setSelectionEnd(fahrt.getText().length());
    }
  }

  public MehrtagestourFrame(AuswahlFrame auswahlFrame, EfaFrame efaFrame, String[][] tabelle, int tabIndex, int editnr) {
    super(auswahlFrame);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.auswahlFrame = auswahlFrame;
    this.efaFrame = efaFrame;
    this.tabelle = tabelle;
    this.tabIndex = tabIndex;
    Mehrtagesfahrt mtour = Daten.fahrtenbuch.getMehrtagesfahrt(tabelle[tabIndex][0]);
    fahrt.setText(mtour.name);
    start.setText(mtour.start);
    ende.setText(mtour.ende);
    anzTage.setText(Integer.toString(mtour.rudertage));
    gewaesser.setText(mtour.gewaesser);
    isEtappen.setSelected(mtour.isEtappen);
    this.editnr = editnr;
    fahrt.requestFocus();
  }

  public MehrtagestourFrame(WanderfahrtSelectFrame frame, String datum, String ziel) {
    super(frame);
    this.wanderfahrtSelectFrame = frame;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      this.isEtappen.setVisible(false);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    if (datum != null && datum.length()>0) start.setText(EfaUtil.correctDate(datum));
    if (ziel != null && ziel.length()>0) {
      fahrt.setText(ziel.trim());
      fahrt.setSelectionStart(0);
      fahrt.setSelectionEnd(fahrt.getText().length());
    }
    fahrt.requestFocus();
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

    okButton.setNextFocusableComponent(fahrt);
    okButton.setMnemonic('O');
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel1.setLayout(gridBagLayout1);
    this.getContentPane().setLayout(borderLayout1);
    Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Bezeichnung der Fahrt (Weg und Ziel)")+": ");
    jLabel1.setLabelFor(fahrt);
    fahrt.setNextFocusableComponent(start);
    Dialog.setPreferredSize(fahrt, 250, 19);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("Beginn der Ruderfahrt") +
            " (" + International.getString("Datum") + "): ");
    jLabel2.setLabelFor(start);
    start.setNextFocusableComponent(ende);
    Dialog.setPreferredSize(start,250, 19);
    start.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        start_focusLost(e);
      }
    });
    gewaesser.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gewaesser_focusLost(e);
      }
    });
    this.setTitle(International.getString("Mehrtagesfahrt"));
    Mnemonics.setLabel(this, anzTageLabel, International.getStringWithMnemonic("Anzahl der Rudertage")+": ");
    anzTageLabel.setLabelFor(anzTage);
    anzTage.setNextFocusableComponent(gewaesser);
    Dialog.setPreferredSize(anzTage, 250, 19);
    anzTage.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        anzTage_focusLost(e);
      }
    });
    Mnemonics.setLabel(this, jLabel3, International.getStringWithMnemonic("Ende der Ruderfahrt") + 
            " (" + International.getString("Datum") + "): ");
    jLabel3.setLabelFor(ende);
    Mnemonics.setLabel(this, jLabel4, International.getStringWithMnemonic("Befahrene Gewässer (durch Kommata getrennt)")+": ");
    jLabel4.setLabelFor(gewaesser);
    isEtappen.setNextFocusableComponent(okButton);
    Mnemonics.setButton(this, isEtappen, International.getStringWithMnemonic("Diese Fahrt wird in Form von einzelnen Etappen eingetragen"));
    ende.setNextFocusableComponent(anzTage);
    Dialog.setPreferredSize(ende, 250, 17);
    ende.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ende_focusLost(e);
      }
    });
    gewaesser.setNextFocusableComponent(isEtappen);
    Dialog.setPreferredSize(gewaesser, 250, 17);
    this.getContentPane().add(okButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(fahrt,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel2,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(start,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(anzTageLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(anzTage,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel3,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(ende,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel4,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(isEtappen,   new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(gewaesser, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      if (auswahlFrame == null && efaFrame != null) efaFrame.fahrtDauer.setSelectedIndex(0);
      cancel();
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
    if (auswahlFrame != null && editnr>0) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void start_focusLost(FocusEvent e) {
    start.setText(EfaUtil.correctDate(start.getText().trim()));
    setAnzTage();
  }

  void ende_focusLost(FocusEvent e) {
    TMJ c = null;
    if (auswahlFrame == null && efaFrame != null) {
      TMJ ref = new TMJ(0,0,0);
      ref = EfaUtil.correctDate(efaFrame.datum.getText().trim()+"."+efaFrame.datum.getText().trim()+"."+efaFrame.datum.getText().trim(),1,1,2001);
      c = EfaUtil.correctDate(ende.getText().trim(),ref.tag,ref.monat,ref.jahr);
    } else {
      Calendar cal = GregorianCalendar.getInstance();
      c = EfaUtil.correctDate(ende.getText().trim(),cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1-cal.getMinimum(GregorianCalendar.MONTH),cal.get(GregorianCalendar.YEAR));
    }
    ende.setText(c.tag+"."+c.monat+"."+c.jahr);

    setAnzTage();
  }

  void gewaesser_focusLost(FocusEvent e) {
    String g = gewaesser.getText().trim();
    g = EfaUtil.replace(g,";",",",true);
    g = EfaUtil.replace(g,":",",",true);
    String[] a = EfaUtil.kommaList2Arr(g,',');
    for (int i=0; i<a.length; i++) a[i] = a[i].trim();
    Arrays.sort(a);
    g = "";
    for (int i=0; i<a.length; i++) if (a[i].length()>0) g += a[i] + (i+1<a.length ? "," : "");
    gewaesser.setText(g);
  }


  void okButton_actionPerformed(ActionEvent e) {
    if (fahrt.getText().trim().equals("")) {
      if (auswahlFrame == null && efaFrame != null) efaFrame.fahrtDauer.setSelectedIndex(0);
      cancel(); return;
    }
    this.start_focusLost(null);
    this.ende_focusLost(null);
    this.anzTage_focusLost(null);
    if (EfaUtil.secondDateIsAfterFirst(ende.getText(),start.getText())) {
      Dialog.error(International.getString("Das Ende der Fahrt muß nach dem Anfang liegen!"));
      return;
    }

    String name = EfaUtil.removeSepFromString(fahrt.getText().trim());

    if (name.length() == 0) {
      Dialog.error(International.getString("Bitte gib einen Namen für die Mehrtagesfahrt ein!"));
      fahrt.requestFocus();
      return;
    }

    if (auswahlFrame == null && efaFrame != null) { // Aufruf aus EfaFrame
      if (Daten.fahrtenbuch.getMehrtagesfahrt(name) != null) {
        Dialog.infoDialog(International.getString("Name bereits vergeben"),
                International.getString("Es gibt bereits eine Mehrtagesfahrt mit diesem Namen!") + " " +
                International.getString("Bitte wähle einen anderen Namen."));
        fahrt.requestFocus();
        return;
      }
      efaFrame.addMehrtagestour(name,
                                EfaUtil.removeSepFromString(start.getText().trim()),
                                EfaUtil.removeSepFromString(ende.getText().trim()),
                                EfaUtil.string2int(anzTage.getText().trim(),1),
                                EfaUtil.removeSepFromString(gewaesser.getText().trim()),
                                isEtappen.isSelected());

      // Wenn Fahrt ohne Etappen eingegeben und Zielfeld im FB (noch) leer, dann benutze
      // den eingegebenen Namen auch für das Zielfeld im FB
      if (!isEtappen.isSelected() && efaFrame.ziel.getText().trim().length()==0)
        efaFrame.ziel.setText(fahrt.getText().trim());
    }

    if (auswahlFrame != null) { // Aufruf aus AuswahlFrame
      if (!(fahrt.getText().equals(tabelle[tabIndex][0]))) {
        if (Daten.fahrtenbuch.getMehrtagesfahrt(name) != null) {
          Dialog.infoDialog(International.getString("Name bereits vergeben"),
                  International.getString("Es gibt bereits eine Mehrtagesfahrt mit diesem Namen!") + " " +
                  International.getString("Bitte wähle einen anderen Namen."));
          fahrt.requestFocus();
          return;
        }
      }
      updateFb(tabelle[tabIndex][0],name);
      tabelle[tabIndex][0] = name;
      tabelle[tabIndex][1] = start.getText().trim();
      tabelle[tabIndex][2] = ende.getText().trim();
      tabelle[tabIndex][3] = anzTage.getText().trim();
      auswahlFrame.doEdit(editnr+1);
      editnr = 0;
      Daten.fahrtenbuch.setChanged();
    }

    if (wanderfahrtSelectFrame != null) {
      if (Daten.fahrtenbuch.getMehrtagesfahrt(name) != null) {
        Dialog.error(International.getString("Es gibt bereits eine Mehrtagesfahrt mit diesem Namen!") + " " +
                International.getString("Bitte wähle einen anderen Namen."));
        fahrt.requestFocus();
        return;
      }
      Daten.fahrtenbuch.addMehrtagesfahrt(name,
                                EfaUtil.removeSepFromString(start.getText().trim()),
                                EfaUtil.removeSepFromString(ende.getText().trim()),
                                EfaUtil.string2int(anzTage.getText().trim(),1),
                                EfaUtil.removeSepFromString(gewaesser.getText().trim()),
                                false);
      wanderfahrtSelectFrame_selectedWafa = name;
    }

    cancel();
  }


  // Eintragungen der Mehrtagesfahrt ändern
  void updateFb(String oldName, String newName) {
    // Fahrtenbuch durchgehen
    DatenFelder d;
    d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    if (d == null) return;
    do {
      if (d.get(Daten.fahrtenbuch.FAHRTART).equals(oldName)) {
        d.set(Daten.fahrtenbuch.FAHRTART,newName);
        Daten.fahrtenbuch.delete(d.get(Daten.fahrtenbuch.LFDNR));
        Daten.fahrtenbuch.add(d);
      }
    } while( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null);
    Daten.fahrtenbuch.removeMehrtagesfahrt(oldName);
    Daten.fahrtenbuch.addMehrtagesfahrt(newName,
                                EfaUtil.removeSepFromString(start.getText().trim()),
                                EfaUtil.removeSepFromString(ende.getText().trim()),
                                EfaUtil.string2int(anzTage.getText().trim(),1),
                                EfaUtil.removeSepFromString(gewaesser.getText().trim()),
                                isEtappen.isSelected());

  }


  void setAnzTage() {
    if (start.getText().trim().length()==0 || ende.getText().trim().length()==0) return;
    int tage = Math.abs(EfaUtil.getDateDiff(start.getText().trim(),ende.getText().trim()));
    if (EfaUtil.string2int(anzTage.getText().trim(),0)>tage) anzTage.setText(Integer.toString(tage));
  }


  void anzTage_focusLost(FocusEvent e) {
    int anz = EfaUtil.string2date(anzTage.getText(),1,0,0).tag;
    if (anz<1) anz = 1;
    if (anzTage.getText().trim().length()==0) anz = 9999;
    if (start.getText().trim().length()>0 && anz>Math.abs(EfaUtil.getDateDiff(start.getText().trim(),ende.getText().trim()))) anz = Math.abs(EfaUtil.getDateDiff(start.getText().trim(),ende.getText().trim()));
    if (anz == 9999) anz = 1; // dies ist der Fall, wenn startDate leer ist
    anzTage.setText(Integer.toString(anz));
  }


}
