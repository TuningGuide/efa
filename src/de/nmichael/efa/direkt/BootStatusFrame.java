package de.nmichael.efa.direkt;

import de.nmichael.efa.core.Fahrtenbuch;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import java.util.*;
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

public class BootStatusFrame extends JDialog implements ActionListener {
  BootStatusListeFrame parentBootStatusListeFrame; // admin != null
  EfaDirektFrame       parentEfaDirektFrame;       // admin == null
  Admin admin;
  DatenFelder boot;
  BootStatus bootStatus;
  int orgStatus = -1; // um beim Speichern Veränderungen am Status zu erkennen
  int lastSelectedStatus = -1; // um während der BEarbeitung beim Ändern des Status auch den Bemerkungstext entspr. zu setzen
  String lastBootsschaden = null;

  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel statusPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel bootsname = new JLabel();
  JComboBox status = new JComboBox();
  JTextField bemerkung = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField lfdnr = new JTextField();
  JLabel fremdesBootLabel = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JButton addResButton = new JButton();
  JButton delResButton = new JButton();
  JTable reservierungen = null;
  boolean firstclick = false;
  JButton editResButton = new JButton();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JTextField bootsschaden = new JTextField();
  JCheckBox bootRepariertCheckBox = new JCheckBox();


  public BootStatusFrame(BootStatusListeFrame parent, DatenFelder boot, BootStatus bootStatus, Admin admin) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    this.parentBootStatusListeFrame = parent;
    this.boot = boot;
    this.bootStatus = bootStatus;
    this.admin = admin;
    try {
      jbInit();
      frameIni();
      EfaUtil.pack(this);
      status.requestFocus();
      if (!admin.allowedBootsreservierung) {
          this.delResButton.setEnabled(false);
          this.editResButton.setEnabled(false);
          this.addResButton.setEnabled(false);
      }
      if (!admin.allowedBootsstatusBearbeiten) {
          this.bootRepariertCheckBox.setVisible(false);
          this.status.setEnabled(false);
          this.bemerkung.setEnabled(false); this.bemerkung.setEditable(false);
          this.bootsschaden.setEnabled(false); this.bootsschaden.setEditable(false);
          this.lfdnr.setEnabled(false); this.lfdnr.setEditable(false);
          this.addResButton.requestFocus();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public BootStatusFrame(EfaDirektFrame parent, DatenFelder boot, BootStatus bootStatus) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    this.parentEfaDirektFrame = parent;
    this.boot = boot;
    this.bootStatus = bootStatus;
    this.admin = null;
    try {
      jbInit();
      frameIni();
      okButton.setText("Schließen");
      if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierungenEditieren) {
        this.delResButton.setEnabled(false);
        this.editResButton.setEnabled(false);
      }
      if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren &&
          !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch) {
        this.addResButton.setEnabled(false);
      }
      this.bootRepariertCheckBox.setVisible(false);
      this.status.setEnabled(false);
      this.bemerkung.setEnabled(false); this.bemerkung.setEditable(false);
      this.bootsschaden.setEnabled(false); this.bootsschaden.setEditable(false);
      this.lfdnr.setEnabled(false); this.lfdnr.setEditable(false);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.addResButton.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      this.okButton_actionPerformed(null);
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

    mainPanel.setLayout(borderLayout2);
    okButton.setNextFocusableComponent(status);
    okButton.setMnemonic('S');
    okButton.setText("Speichern");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    statusPanel.setLayout(gridBagLayout1);
    jLabel1.setText("Bootsname: ");
    jLabel2.setDisplayedMnemonic('S');
    jLabel2.setLabelFor(status);
    jLabel2.setText("Status: ");
    jLabel3.setDisplayedMnemonic('M');
    jLabel3.setLabelFor(bemerkung);
    jLabel3.setText("Bemerkung: ");
    bootsname.setForeground(Color.blue);
    bootsname.setText("Bootsname");
    bemerkung.setNextFocusableComponent(addResButton);
    Dialog.setPreferredSize(bemerkung,100,17);
    this.setTitle("Bootsstatus");
    status.setMinimumSize(new Dimension(100, 22));
    status.setNextFocusableComponent(bemerkung);
    Dialog.setPreferredSize(status,100,22);
    status.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        status_itemStateChanged(e);
      }
    });
    jLabel4.setDisplayedMnemonic('R');
    jLabel4.setLabelFor(lfdnr);
    jLabel4.setText("LfdNr (nur bei Booten auf Fahrt: Nummer des Fahrtenbucheintrags): ");
    lfdnr.setNextFocusableComponent(addResButton);
    Dialog.setPreferredSize(lfdnr,40,17);
    lfdnr.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        lfdnr_focusLost(e);
      }
    });
    fremdesBootLabel.setForeground(Color.red);
    fremdesBootLabel.setText("fremdes Boot");
    addResButton.setNextFocusableComponent(editResButton);
    addResButton.setMnemonic('N');
    addResButton.setText("Neue Reservierung");
    addResButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addResButton_actionPerformed(e);
      }
    });
    delResButton.setNextFocusableComponent(okButton);
    delResButton.setMnemonic('L');
    delResButton.setText("Reservierung löschen");
    delResButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        delResButton_actionPerformed(e);
      }
    });
    jScrollPane1.setPreferredSize(new Dimension(500, 250));
    editResButton.setNextFocusableComponent(delResButton);
    editResButton.setMnemonic('B');
    editResButton.setText("Reservierung bearbeiten");
    editResButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editResButton_actionPerformed(e);
      }
    });
    jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel5.setText("Reservierungen");
    jLabel6.setDisplayedMnemonic('O');
    jLabel6.setLabelFor(bootsschaden);
    jLabel6.setText("Bootsschaden: ");
    bootRepariertCheckBox.setMnemonic('T');
    bootRepariertCheckBox.setText("Boot wurde repariert");
    bootRepariertCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootRepariertCheckBox_actionPerformed(e);
      }
    });
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(okButton, BorderLayout.SOUTH);
    mainPanel.add(statusPanel, BorderLayout.CENTER);
    statusPanel.add(jLabel1,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(jLabel2,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(jLabel3,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(bootsname,      new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(status,           new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(bemerkung,        new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(jLabel4,      new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(lfdnr,       new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(fremdesBootLabel,       new GridBagConstraints(2, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(jScrollPane1,       new GridBagConstraints(0, 7, 2, 4, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(addResButton,       new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(delResButton,        new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(editResButton,       new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(jLabel5,      new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 2, 0), 0, 0));
    statusPanel.add(jLabel6,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(bootsschaden,    new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    statusPanel.add(bootRepariertCheckBox,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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


  void frameIni() {
    this.bootsname.setText(this.boot.get(BootStatus.NAME));
    this.status.removeAllItems();
    for (int i=0; i<BootStatus.STATUSNAMES.length; i++) this.status.addItem(BootStatus.STATUSNAMES[i]);
    this.status.setSelectedIndex(EfaUtil.string2int(this.boot.get(BootStatus.STATUS),0));
    this.bemerkung.setText(this.boot.get(BootStatus.BEMERKUNG));
    this.bootsschaden.setText(this.boot.get(BootStatus.BOOTSSCHAEDEN));
    this.bootRepariertCheckBox.setVisible(this.boot.get(BootStatus.BOOTSSCHAEDEN).length()>0);
    this.lfdnr.setText(this.boot.get(BootStatus.LFDNR));
    this.fremdesBootLabel.setText( (this.boot.get(BootStatus.UNBEKANNTESBOOT).equals("+") ? "fremdes Boot" : "") );

    updateReservierungen();
    orgStatus = this.status.getSelectedIndex();
    lastSelectedStatus = this.status.getSelectedIndex();
  }

  void updateReservierungen() {
    Vector v = BootStatus.getReservierungen(this.boot);
    Vector resData = new Vector();
    for (int i=0; i<v.size(); i++) {
      Reservierung r = (Reservierung)v.get(i);
      Vector data = new Vector();
      data.add(r.vonTag+" "+r.vonZeit);
      data.add(r.bisTag+" "+r.bisZeit);
      data.add(r.name);
      data.add(r.grund);
      resData.add(data);
    }
    Vector titel = new Vector();
    titel.add("Von");  titel.add("Bis");
    titel.add("reserviert für"); titel.add("Grund");
    reservierungen = new JTable(resData,titel);
    try {
      if (reservierungen != null) jScrollPane1.getViewport().remove(reservierungen);
    } catch(Exception e) { EfaUtil.foo(); }
    jScrollPane1.getViewport().add(reservierungen, null);
    reservierungen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    reservierungen.setCellEditor(null);
    reservierungen.removeEditor();
    reservierungen.setColumnSelectionAllowed(false);

    reservierungen.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        reservierungen_mouseClicked(e);
      }
    });
    reservierungen.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        reservierungen_propertyChange(e);
      }
    });
  }

  boolean saveBootStatus(DatenFelder boot, int newStatus, String newBemerkung, String newBootsschaden, String newLfdNr) {
    String name = boot.get(BootStatus.NAME);
    if (EfaUtil.string2int(boot.get(BootStatus.STATUS),-1) == BootStatus.STAT_UNTERWEGS &&
        newStatus != BootStatus.STAT_UNTERWEGS) {
      // Bootsstatus soll von "unterwegs" auf etwas anderes geändert werden!
      if (boot.get(BootStatus.LFDNR).trim().length()>0) {
        if (Dialog.yesNoCancelDialog("Warnung",
                                               "Das Boot "+name+" befindet sich laut Liste zur Zeit auf Fahrt #"+boot.get(BootStatus.LFDNR)+"!\n"+
                                               "Wenn Du den Status jetzt änderst, bleibt der angefangene Eintrag\n"+
                                               "im Fahrtenbuch zurück.\n"+
                                               "Möchtest Du den Status dieses Bootes wirklich ändern?"
                                               ) != Dialog.YES) return false;
        Logger.log(Logger.WARNING,"Der Status der Bootes "+boot.get(BootStatus.NAME)+" wird geändert, obwohl das Boot auf Fahrt #"+
                                  boot.get(BootStatus.LFDNR)+" unterwegs ist! Der angefangene Eintrag #"+boot.get(BootStatus.LFDNR)+
                                  " bleibt im Fahrtenbuch stehen.");
      }
    }

    if (newStatus == BootStatus.STAT_UNTERWEGS) {
      // Status: Unterwegs
      if (newLfdNr.length()==0) {
        // keine LfdNr
        if (Dialog.yesNoCancelDialog("Warnung","Dem Boot "+name+" wird kein Fahrtenbucheintrag zugeordnet (LfdNr ist leer),\n"+
                                               "obwohl der Status des Boots 'unterwegs' sein soll!\n"+
                                               "Wenn Du den Status jetzt auf 'unterwegs' setzt, wird das Boot zwar in der\n"+
                                               "Liste der Boote auf Fahrt angezeigt, kann jedoch nicht 'zurückgetragen' werden, \n"+
                                               "da kein Eintrag im Fahrtenbuch zugeordnet wurde.\n"+
                                               "Möchtest Du wirklich fortfahren?"
                                               ) != Dialog.YES) return false;
        Logger.log(Logger.WARNING,"Der Status des Bootes "+name+" wird auf 'unterwegs' gesetzt, obwohl dem Boot kein Fahrtenbuch-Eintrag zugeordnet ist.");
      } else {
        // LfdNr vorhanden
        DatenFelder d = Daten.fahrtenbuch.getExactComplete(newLfdNr);
        if (d == null) {
          // LfdNr existiert nicht
          if (Dialog.yesNoCancelDialog("Warnung","Dem Boot "+name+" wird ein Fahrtenbucheintrag (#"+newLfdNr+") zugeordnet, welcher nicht existiert!\n"+
                                               "Wenn Du den Status jetzt auf 'unterwegs' setzt, wird das Boot zwar in der\n"+
                                               "Liste der Boote auf Fahrt angezeigt, kann jedoch nicht 'zurückgetragen' werden, \n"+
                                               "da der angegebene Eintrag im Fahrtenbuch nicht existiert.\n"+
                                               "Möchtest Du wirklich fortfahren?"
                                               ) != Dialog.YES) return false;
          Logger.log(Logger.WARNING,"Der Status des Bootes "+name+" wird auf 'unterwegs' gesetzt, aber der zugewiesene Fahrtenbuch-Eintrag #"+newLfdNr+" existiert nicht.");
        } else {
          // LfdNr existiert
          if (d.get(Fahrtenbuch.BOOTSKM).equals("0") && d.get(Fahrtenbuch.MANNSCHKM).equals("0") &&
              d.get(Fahrtenbuch.ANKUNFT).equals("")) {
            // gültige LfdNr
            int altstatus = EfaUtil.string2int(boot.get(BootStatus.STATUS),0);
            if (altstatus != BootStatus.STAT_UNTERWEGS) {
              // Änderung von != unterwegs auf unterwegs
              Logger.log(Logger.INFO,"Der Status der Bootes "+name+" wird von '"+BootStatus.STATUSNAMES[altstatus]+
                                     "' auf '"+BootStatus.STATUSNAMES[BootStatus.STAT_UNTERWEGS]+"' geändert mit"+
                                     " gültiger LfdNr=#"+newLfdNr);
            }
          } else {
            // ungültige LfdNr, d.h. Eintrag wurde bereits zurückgetragen
            if (Dialog.yesNoCancelDialog("Warnung","Dem Boot "+name+" wird ein Fahrtenbucheintrag (#"+newLfdNr+") zugeordnet, welcher\n"+
                                                 "bereits vollständig ist, d.h. schon zurückgetragen wurde!\n"+
                                                 "Wenn Du den Status jetzt auf 'unterwegs' setzt und das Boot zurückgetragen wird, \n"+
                                                 "wird der Fahrtenbuch-Eintrag #"+newLfdNr+" damit überschrieben.\n"+
                                                 "Möchtest Du wirklich fortfahren?"
                                                 ) != Dialog.YES) return false;
            Logger.log(Logger.WARNING,"Der Status des Bootes "+name+" wird auf 'unterwegs' gesetzt, aber der zugewiesene Fahrtenbuch-Eintrag #"+newLfdNr+" ist bereits vollständig.");
          }
        }
      }
    }
    return true;
  }

  void okButton_actionPerformed(ActionEvent e) {
    String name = boot.get(BootStatus.NAME);

    int _status = status.getSelectedIndex();
    String _bemerkung = EfaUtil.removeSepFromString(bemerkung.getText().trim());
    String _bootsschaden = EfaUtil.removeSepFromString(bootsschaden.getText().trim());
    String _lfdnr = lfdnr.getText().trim();

    // Bugfix, damit reservierte Boote, die z.B. auf "nicht verfügbar" gesetzt werden, nicht
    // nach Ende der Reservierung wieder automatisch auf "verfügbar" wechseln.
    if (_status != orgStatus &&
        _lfdnr.equals(BootStatus.RES_LFDNR) && _status != BootStatus.STAT_VERFUEGBAR) { _lfdnr = ""; }

    if (saveBootStatus(boot,_status,_bemerkung,_bootsschaden,_lfdnr)) {
      boot.set(BootStatus.STATUS,Integer.toString(_status));
      boot.set(BootStatus.BEMERKUNG,_bemerkung);
      boot.set(BootStatus.BOOTSSCHAEDEN,_bootsschaden);
      boot.set(BootStatus.LFDNR,_lfdnr);
    }

    // Wenn Status geändert wurde, dann ggf. auch für Kombiboote den Status übernehmen
    if (_status != orgStatus) {
      Vector syn = getVectorKombiBoote(name);
      if (syn != null) {
        // Kombiboot
        syn.remove(name);
        if (Dialog.yesNoDialog("Bootsstatus für Kombiboote übernehmen",
                               "Der Bootsstatus wurde geändert. Soll der neue Bootsstatus auch für\n"+
                               (syn.size() == 1 ? "das Boot " : "die Boote ") + EfaUtil.vector2string(syn,",")+"\n"+
                               "übernommen werden?") == Dialog.YES) {
          // neuen Bootsstatus für alle Kombiboote übernehmen
          for (int i=0; i<syn.size(); i++) {
            String s = (String)syn.get(i);
            if (!s.equals(name)) {
              DatenFelder d = bootStatus.getExactComplete(s);
              if (d != null && saveBootStatus(d,_status,_bemerkung,_bootsschaden,d.get(BootStatus.LFDNR))) {
                d.set(BootStatus.STATUS,Integer.toString(_status));
                d.set(BootStatus.BEMERKUNG,_bemerkung);
                d.set(BootStatus.BOOTSSCHAEDEN,_bootsschaden);
                // LFDNR wird beim Kombiboot *nicht* gesetzt
                bootStatus.setChanged();
              }
            }
          }
        }
      }
    } else {
      // sonst zumindest für alle Kombiboote den Bootsschaden anpassen
      Vector syn = getVectorKombiBoote(name);
      if (syn != null) {
        // Kombiboot
        for (int i=0; i<syn.size(); i++) {
          String s = (String)syn.get(i);
          if (!s.equals(name)) {
            DatenFelder d = bootStatus.getExactComplete(s);
            if (d != null) {
              d.set(BootStatus.BOOTSSCHAEDEN,_bootsschaden);
              bootStatus.setChanged();
            }
          }
        }
      }
    }

    // Bei Status-Änderungen durch Admin ggf. Benachrichtigung an Bootswarte (und Admins)
    if (_status != orgStatus) {
      if (Daten.efaConfig != null && Daten.nachrichten != null) {
        String txt = "Der Status des Bootes "+name+" wurde von "+(admin.name != null ? admin.name : "einem Mitglied")+" geändert.\n"+
                     "alter Status: "+BootStatus.getStatusName(orgStatus)+"; neuer Status: "+BootStatus.getStatusName(_status);
        if (Daten.efaConfig.efaDirekt_bnrBootsstatus_admin) {
          Daten.nachrichten.createNachricht("efa",Nachricht.ADMIN,"Bootsstatus-Änderung",txt);
        }
        if (Daten.efaConfig.efaDirekt_bnrBootsstatus_bootswart) {
          Daten.nachrichten.createNachricht("efa",Nachricht.BOOTSWART,"Bootsstatus-Änderung",txt);
        }
      }
    }
    if (admin != null) {
      this.parentBootStatusListeFrame.editDone(boot);
    } else {
      if (!bootStatus.writeFile()) {
        Dialog.error("Bootstatus-Liste konnte nicht geschrieben werden!");
        Logger.log(Logger.ERROR,"Bootstatus-Liste konnte nach Änderung der Reservierungen für Boot '"+boot.get(BootStatus.NAME)+"' nicht geschrieben werden.");
      }
      parentEfaDirektFrame.efaDirektBackgroundTask.interrupt();
    }
    cancel();
  }

  void status_itemStateChanged(ItemEvent e) {
    if (status.getSelectedIndex() == BootStatus.STAT_UNTERWEGS) lfdnr.setEnabled(true);
    else lfdnr.setEnabled(false);

    if (lastSelectedStatus>=0 && lastSelectedStatus<BootStatus.STATUSNAMES.length && lastSelectedStatus != status.getSelectedIndex()) {
      if (bemerkung.getText().trim().equals(BootStatus.STATUSNAMES[lastSelectedStatus])) {
        bemerkung.setText(BootStatus.STATUSNAMES[status.getSelectedIndex()]);
      }
    }
    lastSelectedStatus = status.getSelectedIndex();
  }

  void lfdnr_focusLost(FocusEvent e) {
    int nr = EfaUtil.string2int(lfdnr.getText().trim(),-1);
    if (nr != -1) lfdnr.setText(Integer.toString(nr));
    else lfdnr.setText("");
  }

  void addResButton_actionPerformed(ActionEvent e) {
    if (admin == null && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch) return;
    ReservierungenEditFrame dlg = new ReservierungenEditFrame(this,admin != null,boot.get(BootStatus.NAME),null,-1,BootStatus.getReservierungen(this.boot));
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void editResButton_actionPerformed(ActionEvent e) {
    if (admin == null && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierungenEditieren) return;
    if (reservierungen.getSelectedRow()<0) {
      Dialog.error("Bitte wähle zunächst eine Reservierung aus!");
      return;
    }
    try {
      Reservierung res = (Reservierung)BootStatus.getReservierungen(this.boot).get(reservierungen.getSelectedRow());
      if ( admin == null &&
           ((res.einmalig && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) ||
            (!res.einmalig && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch) )) {
        Dialog.error("Du darfst "+(res.einmalig ? "einmalige" : "wöchentliche")+ " Reservierungen nicht bearbeiten.");
        return;
      }
      ReservierungenEditFrame dlg =
        new ReservierungenEditFrame(this,admin != null,boot.get(BootStatus.NAME),
                                    res,
                                    reservierungen.getSelectedRow(),
                                    BootStatus.getReservierungen(this.boot));
      Dimension dlgSize = dlg.getPreferredSize();
      Dimension frmSize = getSize();
      Point loc = getLocation();
      dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
      dlg.setModal(true);
      dlg.show();
    } catch(Exception ee) { EfaUtil.foo(); }
  }

  void delResButton_actionPerformed(ActionEvent e) {
    if (admin == null && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierungenEditieren) return;
    if (reservierungen.getSelectedRow()<0) {
      Dialog.error("Bitte wähle zunächst eine Reservierung aus!");
      return;
    }

    Reservierung res = (Reservierung)BootStatus.getReservierungen(this.boot).get(reservierungen.getSelectedRow());
    if (admin == null &&
         ( (res.einmalig && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) ||
           (!res.einmalig && !Daten.efaConfig.efaDirekt_mitgliederDuerfenReservierenZyklisch) )) {
      Dialog.error("Du darfst "+(res.einmalig ? "einmalige" : "wöchentliche")+ " Reservierungen nicht löschen.");
      return;
    }

    if (Dialog.yesNoCancelDialog("Reservierung löschen","Möchtest Du die gewählte Reservierung wirklich löschen?"
                                      ) != Dialog.YES) return;
    try {
      Vector v = BootStatus.getReservierungen(this.boot);
      v.remove(reservierungen.getSelectedRow());
      BootStatus.setReservierungen(this.boot,v);
      kombiBootSetReservierungen(this.boot.get(BootStatus.NAME),v);
      updateReservierungen();
    } catch(Exception ee) { EfaUtil.foo(); }
  }

  // Edit bei Doppelklick
  void reservierungen_mouseClicked(MouseEvent e) {
    firstclick=true;
  }

  // komisch, manchmal scheine diese Methode irgendwie nicht zu ziehen.....
  void reservierungen_propertyChange(PropertyChangeEvent e) {
     // System.out.println("hi");
    if (reservierungen.isEditing()) {
      if (firstclick) editResButton_actionPerformed(null);
      firstclick=false;
    }
  }


  // true, wenn *keine* Überschneidungen; false, wenn Überschneidung
  public static boolean keineUeberschneidung(Vector v, Reservierung r, int exclude) {
    if (v == null || r == null) return true;
    long v1 = EfaUtil.dateTime2Cal(r.vonTag,r.vonZeit).getTimeInMillis()+1;
    long b1 = EfaUtil.dateTime2Cal(r.bisTag,r.bisZeit).getTimeInMillis();
    for (int i=0; i<v.size(); i++) {
      if (i == exclude) continue;
      Reservierung rr = (Reservierung)v.get(i);
      if (r.einmalig != rr.einmalig) continue; // Überschneidungen von einmaligen mit wöchentlichen Reservierungen zulassen
      if (!r.einmalig && !rr.einmalig && !r.vonTag.equals(rr.vonTag)) continue; // zyklische Reservierungen nur prüfen, wenn am selben Tag
      long v2 = EfaUtil.dateTime2Cal(rr.vonTag,rr.vonZeit).getTimeInMillis()+1;
      long b2 = EfaUtil.dateTime2Cal(rr.bisTag,rr.bisZeit).getTimeInMillis();

      if ( (v1 >= v2 && v1 <= b2) ||
           (b1 >= v2 && b1 <= b2) ||
           (v1 <= v2 && b1 >= b2) ) return false;
    }
    return true;
  }


  public void addNewReservierung(Reservierung r) {
    Vector v = BootStatus.getReservierungen(this.boot);
    if (!keineUeberschneidung(v,r,-1)) {
      Dialog.error("Die Reservierung überschneidet sich mit einer anderen Reservierung!");
      return;
    }
    v.add(r);
    BootStatus.setReservierungen(this.boot,v);
    kombiBootSetReservierungen(this.boot.get(BootStatus.NAME),v);
    updateReservierungen();
  }

  public void updateReservierung(Reservierung r, int nr) {
    try {
      Vector v = BootStatus.getReservierungen(this.boot);
      if (!keineUeberschneidung(v,r,nr)) {
        Dialog.error("Die Reservierung überschneidet sich mit einer anderen Reservierung!");
        return;
      }
      v.remove(nr);
      v.add(r);
      BootStatus.setReservierungen(this.boot,v);
      kombiBootSetReservierungen(this.boot.get(BootStatus.NAME),v);
      updateReservierungen();
    } catch(Exception ee) {}
  }

  Vector getVectorKombiBoote(String name) {
    String org = EfaUtil.syn2org(Daten.synBoote,name);
    if (org == null || org.equals(name)) return null; // kein Kombiboot

    Vector syn = EfaUtil.org2syn(Daten.synBoote,org);
    return syn;
  }


  void kombiBootSetReservierungen(String name, Vector v) {
    Vector syn = getVectorKombiBoote(name);
    if (syn == null) return; // kein Kombiboot

    for (int i=0; i<syn.size(); i++) {
      String s = (String)syn.get(i);
      if (!name.equals(s)) {
        DatenFelder d = bootStatus.getExactComplete(s);
        if (d != null && EfaUtil.string2int(d.get(BootStatus.STATUS),-1) != BootStatus.STAT_HIDE) {
          BootStatus.setReservierungen(d,v);
        }
      }
    }
  }

  void bootRepariertCheckBox_actionPerformed(ActionEvent e) {
    if (bootRepariertCheckBox.isSelected()) {
      lastBootsschaden = this.bootsschaden.getText().trim();
      this.bootsschaden.setText("");
    } else {
      if (lastBootsschaden != null) this.bootsschaden.setText(lastBootsschaden);
    }
  }

}
