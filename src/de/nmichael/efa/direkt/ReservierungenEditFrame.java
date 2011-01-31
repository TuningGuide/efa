/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.efa1.BoatReservation;
import de.nmichael.efa.core.EfaFrame;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import de.nmichael.efa.*;

// @i18n complete

public class ReservierungenEditFrame extends JDialog implements ActionListener {

  BootStatusFrame parent;
  boolean admin; // Admin-Modus oder normales Mitglied
  BoatReservation data;
  int nr;
  Vector alleRes;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel vonTagLabel = new JLabel();
  JLabel vonZeitLabel = new JLabel();
  JLabel bisTagLabel = new JLabel();
  JLabel bisZeitLabel = new JLabel();
  JLabel nameLabel = new JLabel();
  JLabel grundLabel = new JLabel();
  JTextField vonTag = new JTextField();
  JTextField vonZeit = new JTextField();
  JTextField bisTag = new JTextField();
  JTextField bisZeit = new JTextField();
  JTextField name = new JTextField();
  JTextField grund = new JTextField();
  JLabel resLabel = new JLabel();
  JLabel woTagVonLabel = new JLabel();
  JLabel woTagBisLabel = new JLabel();
  JRadioButton resEinmalig = new JRadioButton();
  JRadioButton resZyklisch = new JRadioButton();
  ButtonGroup buttonGroup = new ButtonGroup();
  JLabel wochentagLabel = new JLabel();
  JComboBox wochentagList = new JComboBox();
  Vector wochentagKeys = new Vector();


  public ReservierungenEditFrame(BootStatusFrame parent, boolean admin, String boot, BoatReservation data, int nr, Vector alleRes) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    resLabel.setText(International.getMessage("Reservierung für {boat}",boot));
    this.parent = parent;
    this.admin = admin;
    this.data = data;
    this.nr = nr;
    this.alleRes = alleRes;
    frIni();
    einmaligZyklischUpdate();
    EfaUtil.pack(this);
    this.woTagVonLabel.setText("");
    this.woTagBisLabel.setText("");
    this.vonTag.requestFocus();
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
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(vonTag);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Speichern"));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      Mnemonics.setLabel(this, vonTagLabel, International.getStringWithMnemonic("Von")+
              " (" + International.getString("Tag") + "): ");
      Mnemonics.setLabel(this, vonZeitLabel, International.getStringWithMnemonic("Von")+
              " (" + International.getString("Zeit") + "): ");
      Mnemonics.setLabel(this, bisTagLabel, International.getStringWithMnemonic("Bis")+
              " (" + International.getString("Tag") + "): ");
      Mnemonics.setLabel(this, bisZeitLabel, International.getStringWithMnemonic("Bis")+
              " (" + International.getString("Zeit") + "): ");
      Mnemonics.setLabel(this, nameLabel, International.getStringWithMnemonic("Reserviert für")+": ");
      Mnemonics.setLabel(this, grundLabel, International.getStringWithMnemonic("Reservierungsgrund")+": ");
      this.setTitle(International.getString("Reservierung"));
      vonTag.setNextFocusableComponent(vonZeit);
      Dialog.setPreferredSize(okButton,400,23);
      Dialog.setPreferredSize(vonTag,150,17);
      vonTag.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          vonTag_focusLost(e);
        }
    });
      vonZeit.setNextFocusableComponent(bisTag);
      Dialog.setPreferredSize(vonZeit,150,17);
      vonZeit.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          vonZeit_focusLost(e);
        }
    });
      bisTag.setNextFocusableComponent(bisZeit);
      Dialog.setPreferredSize(bisTag,150,17);
      bisTag.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          bisTag_focusLost(e);
        }
    });
      bisZeit.setNextFocusableComponent(name);
      Dialog.setPreferredSize(bisZeit,150,17);
      bisZeit.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          bisZeit_focusLost(e);
        }
    });
      name.setNextFocusableComponent(grund);
      Dialog.setPreferredSize(name,150,17);
      name.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          name_keyReleased(e);
        }
      });
      name.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          name_focusLost(e);
        }
      });
      grund.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(grund,150,17);
      resLabel.setForeground(new Color(0, 0, 153));
      resLabel.setHorizontalAlignment(SwingConstants.CENTER);
      resLabel.setText(International.getMessage("Reservierung für {boat}","..."));
      woTagVonLabel.setText("("+International.getString("Wochentag")+")      ");
      woTagBisLabel.setText("("+International.getString("Wochentag")+")      ");
      Mnemonics.setButton(this, resEinmalig, International.getStringWithMnemonic("Einmalige Reservierung"));
      resEinmalig.setSelected(true);
      resEinmalig.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          resEinmalig_itemStateChanged(e);
        }
      });
      Mnemonics.setButton(this, resZyklisch, International.getStringWithMnemonic("Wöchentliche Reservierung"));
      resZyklisch.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          resZyklisch_itemStateChanged(e);
        }
      });
      Mnemonics.setLabel(this, wochentagLabel, International.getStringWithMnemonic("Wochentag")+": ");
      wochentagLabel.setLabelFor(wochentagList);
      wochentagList.setNextFocusableComponent(vonZeit);
      buttonGroup.add(resEinmalig);
      buttonGroup.add(resZyklisch);
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(vonTagLabel,       new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(vonZeitLabel,       new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(bisTagLabel,       new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(bisZeitLabel,       new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(nameLabel,       new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(grundLabel,       new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(vonTag,      new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(vonZeit,      new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(bisTag,      new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(bisZeit,      new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(name,      new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(grund,      new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(resLabel,       new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 0), 0, 0));
      jPanel2.add(woTagVonLabel,      new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
      jPanel2.add(woTagBisLabel,      new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
      jPanel2.add(resEinmalig,     new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(resZyklisch,     new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
      jPanel2.add(wochentagLabel,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(wochentagList,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
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
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void frIni() {
    // @todo change order depending on locale
      String[] weekdays = new String[7];
      weekdays[0] = International.getString("Montag");
      weekdays[1] = International.getString("Dienstag");
      weekdays[2] = International.getString("Mittwoch");
      weekdays[3] = International.getString("Donnerstag");
      weekdays[4] = International.getString("Freitag");
      weekdays[5] = International.getString("Samstag");
      weekdays[6] = International.getString("Sonntag");
      
      int firstDayOfWeek = (new GregorianCalendar(International.getResourceBundle().getLocale())).getFirstDayOfWeek();
      int idx = 0;
      switch(firstDayOfWeek) {
          case GregorianCalendar.MONDAY: idx = 0;
              break;
          case GregorianCalendar.TUESDAY: idx = 1;
              break;
          case GregorianCalendar.WEDNESDAY: idx = 2;
              break;
          case GregorianCalendar.THURSDAY: idx = 3;
              break;
          case GregorianCalendar.FRIDAY: idx = 4;
              break;
          case GregorianCalendar.SATURDAY: idx = 5;
              break;
          case GregorianCalendar.SUNDAY: idx = 6;
              break;
      }

      for (int i=0; i<7; i++) {
          wochentagList.addItem(weekdays[idx]);
          wochentagKeys.add(BoatReservation.WEEKDAYKEYS[idx]);
          idx = (idx+1) % 7;
      }

    if (!admin && Daten.efaConfig != null) {
      if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren.getValue() ||
          !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch.getValue()) {
        this.resEinmalig.setEnabled(false);
        this.resZyklisch.setEnabled(false);
        this.resEinmalig.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren.getValue());
        this.resZyklisch.setSelected(Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch.getValue());
      }
    }

    if (data == null) return;
    if (data.isOneTimeReservation()) {
      vonTag.setText(data.getDateFrom());
      bisTag.setText(data.getDateTo());
      resEinmalig.setSelected(true);
    } else {
      wochentagList.setSelectedItem(data.getWeekdayFrom());
      resZyklisch.setSelected(true);
    }
    vonZeit.setText(data.getTimeFrom());
    bisZeit.setText(data.getTimeTo());
    name.setText(data.getForName());
    grund.setText(data.getReason());
    vonTag_focusLost(null);
    bisTag_focusLost(null);
    vonZeit_focusLost(null);
    bisZeit_focusLost(null);
  }

  void vonTag_focusLost(FocusEvent e) {
    if (!vonTag.isVisible()) return;
    vonTag.setText(EfaUtil.correctDate(vonTag.getText().trim()));
    woTagVonLabel.setText("("+EfaUtil.getWoTag(vonTag.getText().trim())+")");
  }

  void bisTag_focusLost(FocusEvent e) {
    if (!bisTag.isVisible()) return;
    if (bisTag.getText().trim().length()==0) bisTag.setText(vonTag.getText().trim());
    bisTag.setText(EfaUtil.correctDate(bisTag.getText().trim()));
    woTagBisLabel.setText("("+EfaUtil.getWoTag(bisTag.getText().trim())+")");
  }

  void vonZeit_focusLost(FocusEvent e) {
    vonZeit.setText(EfaUtil.correctTime(vonZeit.getText().trim()));
    if (vonZeit.getText().trim().length()==0) vonZeit.setText("00:00");
  }

  void bisZeit_focusLost(FocusEvent e) {
    bisZeit.setText(EfaUtil.correctTime(bisZeit.getText().trim()));
    if (bisZeit.getText().trim().length()==0) bisZeit.setText("23:59");
  }

  void name_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().mitglieder == null) return;
    if (name.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(name,null,Daten.fahrtenbuch.getDaten().mitglieder,e,null,true);
  }

  void name_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

  boolean validateNotEmpty(JTextField field, String name) {
    if (field.getText().trim().length()>0) return true;
    Dialog.error(International.getMessage("Das Feld '{field_name}' ist leer.",name) + " " +
            International.getString("Bitte gib einen Wert ein!"));
    field.requestFocus();
    return false;
  }

  void okButton_actionPerformed(ActionEvent e) {
    bisTag_focusLost(null);
    boolean einmalig = this.resEinmalig.isSelected();
    if (einmalig) {
      if (!validateNotEmpty(vonTag,International.getString("Von") +
              " (" + International.getString("Tag") + ")")) return;
      if (!validateNotEmpty(bisTag,International.getString("Bis") +
              " (" + International.getString("Tag") + ")")) return;
    } else {
    }
    if (!validateNotEmpty(vonZeit,International.getString("Von") +
              " (" + International.getString("Zeit") + ")")) return;
    if (!validateNotEmpty(bisZeit,International.getString("Bis") +
              " (" + International.getString("Zeit") + ")")) return;
    if (!validateNotEmpty(name,International.getString("Name"))) return;
    if (grund.getText().trim().length()==0) grund.setText(International.getString("k.A."));

    Calendar vonCal = null;
    Calendar bisCal = null;
    if (einmalig) {
      TMJ von = EfaUtil.correctDate(vonTag.getText().trim(),0,0,0);
      TMJ bis = EfaUtil.correctDate(bisTag.getText().trim(),0,0,0);
      vonCal = new GregorianCalendar(von.jahr,von.monat-1,von.tag);
      bisCal = new GregorianCalendar(bis.jahr,bis.monat-1,bis.tag);
      if (vonCal.after(bisCal)) {
        Dialog.error(International.getString("Das Ende der Reservierung kann nicht vor dem Anfang liegen!"));
        bisTag.requestFocus();
        return;
      }
    }
    if (!einmalig || vonCal.equals(bisCal)) {
      TMJ von = EfaUtil.string2date(vonZeit.getText().trim(),0,0,0);
      TMJ bis = EfaUtil.string2date(bisZeit.getText().trim(),0,0,0);
      if (von.tag > bis.tag || (von.tag == bis.tag && von.monat > bis.monat)){
        Dialog.error(International.getString("Das Ende der Reservierung kann nicht vor dem Anfang liegen!"));
        bisZeit.requestFocus();
        return;
      }
    }


    BoatReservation r = new BoatReservation();
    r.setOneTimeReservation(einmalig);
    if (einmalig) {
      r.setDateFrom(vonTag.getText().trim());
      r.setDateTo(bisTag.getText().trim());
    } else {
      r.setDateFrom((String)wochentagKeys.get(wochentagList.getSelectedIndex()));
      r.setDateTo(r.getDateFrom());
    }
    r.setTimeFrom(vonZeit.getText().trim());
    r.setTimeTo(bisZeit.getText().trim());
    r.setForName(name.getText().trim());
    r.setReason(grund.getText().trim());

    if (!BootStatusFrame.keineUeberschneidung(alleRes,r,nr)) {
      Dialog.error(International.getString("Die Reservierung überschneidet sich mit einer anderen Reservierung!"));
      return;
    }

    if (data == null) {
      parent.addNewReservierung(r);
    } else {
      parent.updateReservierung(r,nr);
    }
    cancel();
  }

  void resEinmalig_itemStateChanged(ItemEvent e) {
    einmaligZyklischUpdate();
  }

  void resZyklisch_itemStateChanged(ItemEvent e) {

  }

  void einmaligZyklischUpdate() {
    boolean einmalig = resEinmalig.isSelected();
    this.wochentagLabel.setVisible(!einmalig);
    this.wochentagList.setVisible(!einmalig);
    this.vonTagLabel.setVisible(einmalig);
    this.vonTag.setVisible(einmalig);
    this.woTagVonLabel.setVisible(einmalig);
    this.bisTagLabel.setVisible(einmalig);
    this.bisTag.setVisible(einmalig);
    this.woTagBisLabel.setVisible(einmalig);
    EfaUtil.pack(this);
  }


}
