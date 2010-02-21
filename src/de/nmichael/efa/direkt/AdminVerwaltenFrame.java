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

import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import de.nmichael.efa.*;
import java.beans.*;

// @i18n complete

public class AdminVerwaltenFrame extends JDialog implements ActionListener {
  String _password; // Paßwort des aktuell angezeigten Eintrags
  boolean neuerEintrag;
  TitledBorder titledBorder1;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JButton newButton = new JButton();
  JButton deleteButton = new JButton();
  JList adminList = new JList();
  JPanel editPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel nameLabel = new JLabel();
  JTextField name = new JTextField();
  JLabel pwdLabel = new JLabel();
  JTextField password = new JTextField();
  JButton passwordButton = new JButton();
  JLabel permissionsLabel = new JLabel();
  JCheckBox adminsVerwaltenCheckBox = new JCheckBox();
  JCheckBox passwortAendernCheckBox = new JCheckBox();
  JCheckBox fahrtenbuchAuswaehlenCheckBox = new JCheckBox();
  JCheckBox fahrtenbuchAnzeigenCheckBox = new JCheckBox();
  JCheckBox bootsstatusBearbeitenCheckBox = new JCheckBox();
  JCheckBox bootsreservierungCheckBox = new JCheckBox();
  JCheckBox nachrichtenAdminAnzeigenCheckBox = new JCheckBox();
  JCheckBox logdateiAnzeigenCheckBox = new JCheckBox();
  JCheckBox nachrichtenAdminGelesenDefaultCheckBox = new JCheckBox();
  JCheckBox nachrichtenAdminAllowedGelesenMarkierenCheckBox = new JCheckBox();
  JButton saveButton = new JButton();
  JLabel adminsLabel = new JLabel();
  JCheckBox efaConfigCheckBox = new JCheckBox();
  JCheckBox efaBeendenCheckBox = new JCheckBox();
  JCheckBox nachrichtenBootswartAnzeigenCheckBox = new JCheckBox();
  JCheckBox nachrichtenBootswartAllowedGelesenMarkierenCheckBox = new JCheckBox();
  JCheckBox nachrichtenBootswartGelesenDefaultCheckBox = new JCheckBox();
  JCheckBox statistikErstellenCheckBox = new JCheckBox();
  JLabel emailLabel = new JLabel();
  JTextField email = new JTextField();
  JCheckBox bootslisteBearbeitenCheckBox = new JCheckBox();
  JCheckBox mitgliederlisteBearbeitenCheckBox = new JCheckBox();
  JCheckBox ziellisteBearbeitenCheckBox = new JCheckBox();
  JCheckBox gruppenBearbeitenCheckBox = new JCheckBox();
  JCheckBox vollzugriffCheckBox = new JCheckBox();


  public AdminVerwaltenFrame(AdminFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      listAdmins();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    adminList.requestFocus();
    EfaUtil.pack(this);
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
      titledBorder1 = new TitledBorder("");
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(adminList);
      Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("Speichern"));
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      newButton.setNextFocusableComponent(deleteButton);
      Mnemonics.setButton(this, newButton, International.getStringWithMnemonic("Neu"));
      newButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newButton_actionPerformed(e);
        }
    });
      deleteButton.setNextFocusableComponent(name);
      Mnemonics.setButton(this, deleteButton, International.getStringWithMnemonic("Löschen"));
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteButton_actionPerformed(e);
        }
    });
      editPanel.setLayout(gridBagLayout2);
      Mnemonics.setLabel(this, nameLabel, International.getStringWithMnemonic("Name")+": ");
      nameLabel.setLabelFor(name);
      pwdLabel.setText(International.getString("Paßwort")+": ");
      passwordButton.setNextFocusableComponent(email);
      Mnemonics.setButton(this, passwordButton, International.getStringWithMnemonic("Paßwort ändern"));
      passwordButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          passwordButton_actionPerformed(e);
        }
    });
      permissionsLabel.setText(International.getString("Berechtigungen")+": ");
      adminsVerwaltenCheckBox.setNextFocusableComponent(passwortAendernCheckBox);
      Mnemonics.setButton(this, adminsVerwaltenCheckBox, International.getStringWithMnemonic("Admins verwalten"));
      passwortAendernCheckBox.setNextFocusableComponent(vollzugriffCheckBox);
      Mnemonics.setButton(this, passwortAendernCheckBox, International.getStringWithMnemonic("Paßwort ändern"));
      fahrtenbuchAuswaehlenCheckBox.setNextFocusableComponent(fahrtenbuchAnzeigenCheckBox);
      Mnemonics.setButton(this, fahrtenbuchAuswaehlenCheckBox, International.getStringWithMnemonic("Fahrtenbuch auswählen"));
      fahrtenbuchAnzeigenCheckBox.setNextFocusableComponent(bootsstatusBearbeitenCheckBox);
      Mnemonics.setButton(this, fahrtenbuchAnzeigenCheckBox, International.getStringWithMnemonic("Fahrtenbuch bearbeiten"));
      bootsstatusBearbeitenCheckBox.setNextFocusableComponent(bootsreservierungCheckBox);
      Mnemonics.setButton(this, bootsstatusBearbeitenCheckBox, International.getStringWithMnemonic("Bootsstatus bearbeiten"));
      bootsreservierungCheckBox.setNextFocusableComponent(bootslisteBearbeitenCheckBox);
      Mnemonics.setButton(this, bootsreservierungCheckBox, International.getStringWithMnemonic("Bootsreservierungen vornehmen"));
      nachrichtenAdminAnzeigenCheckBox.setNextFocusableComponent(nachrichtenAdminAllowedGelesenMarkierenCheckBox);
      Mnemonics.setButton(this, nachrichtenAdminAnzeigenCheckBox, International.getStringWithMnemonic("Nachrichten an Admin lesen"));
      logdateiAnzeigenCheckBox.setNextFocusableComponent(efaBeendenCheckBox);
      Mnemonics.setButton(this, logdateiAnzeigenCheckBox, International.getStringWithMnemonic("Logdatei anzeigen"));
      nachrichtenAdminGelesenDefaultCheckBox.setNextFocusableComponent(nachrichtenBootswartAnzeigenCheckBox);
      Mnemonics.setButton(this, nachrichtenAdminGelesenDefaultCheckBox, International.getStringWithMnemonic("automatisch als gelesen markieren"));
      nachrichtenAdminAllowedGelesenMarkierenCheckBox.setNextFocusableComponent(nachrichtenAdminGelesenDefaultCheckBox);
      Mnemonics.setButton(this, nachrichtenAdminAllowedGelesenMarkierenCheckBox, International.getStringWithMnemonic("darf Nachr. als gelesen markieren"));
      name.setNextFocusableComponent(passwordButton);
      Dialog.setPreferredSize(name,150,17);
      name.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          name_focusLost(e);
        }
    });
      Dialog.setPreferredSize(password,150,17);
      password.setEditable(false);
      editPanel.setBorder(BorderFactory.createEtchedBorder());
      saveButton.setNextFocusableComponent(okButton);
      Mnemonics.setButton(this, saveButton, International.getStringWithMnemonic("Änderungen übernehmen"));
      saveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveButton_actionPerformed(e);
        }
    });
      jScrollPane1.setPreferredSize(new Dimension(150, 300));
      Mnemonics.setLabel(this, adminsLabel, International.getStringWithMnemonic("Admins")+": ");
      adminsLabel.setLabelFor(adminList);
      adminList.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          adminList_mouseClicked(e);
        }
    });
      this.setTitle(International.getString("Admins verwalten"));
      adminList.setNextFocusableComponent(newButton);
      efaConfigCheckBox.setNextFocusableComponent(fahrtenbuchAuswaehlenCheckBox);
      Mnemonics.setButton(this, efaConfigCheckBox, International.getStringWithMnemonic("efa konfigurieren"));
      efaBeendenCheckBox.setNextFocusableComponent(saveButton);
      Mnemonics.setButton(this, efaBeendenCheckBox, International.getStringWithMnemonic("efa beenden"));
      nachrichtenBootswartAnzeigenCheckBox.setNextFocusableComponent(nachrichtenBootswartAllowedGelesenMarkierenCheckBox);
      Mnemonics.setButton(this, nachrichtenBootswartAnzeigenCheckBox, International.getStringWithMnemonic("Nachrichten an Bootswart lesen"));
      nachrichtenBootswartAllowedGelesenMarkierenCheckBox.setNextFocusableComponent(nachrichtenBootswartGelesenDefaultCheckBox);
      Mnemonics.setButton(this, nachrichtenBootswartAllowedGelesenMarkierenCheckBox, International.getStringWithMnemonic("darf Nachr. als gelesen markieren"));
      nachrichtenBootswartGelesenDefaultCheckBox.setNextFocusableComponent(statistikErstellenCheckBox);
      Mnemonics.setButton(this, nachrichtenBootswartGelesenDefaultCheckBox, International.getStringWithMnemonic("automatisch als gelesen markieren"));
      statistikErstellenCheckBox.setNextFocusableComponent(logdateiAnzeigenCheckBox);
      Mnemonics.setButton(this, statistikErstellenCheckBox, International.getStringWithMnemonic("Statistiken erstellen"));
      Mnemonics.setLabel(this, emailLabel, International.getStringWithMnemonic("email-Adresse")+": ");
      emailLabel.setLabelFor(email);
      emailLabel.setMinimumSize(emailLabel.getPreferredSize()); // Bugfix
      email.setNextFocusableComponent(adminsVerwaltenCheckBox);
      Dialog.setPreferredSize(email,150,17);
      bootslisteBearbeitenCheckBox.setNextFocusableComponent(mitgliederlisteBearbeitenCheckBox);
      Mnemonics.setButton(this, bootslisteBearbeitenCheckBox, International.getStringWithMnemonic("Bootsliste bearbeiten"));
      mitgliederlisteBearbeitenCheckBox.setNextFocusableComponent(ziellisteBearbeitenCheckBox);
      Mnemonics.setButton(this, mitgliederlisteBearbeitenCheckBox, International.getStringWithMnemonic("Mitgliederliste bearbeiten"));
      ziellisteBearbeitenCheckBox.setNextFocusableComponent(gruppenBearbeitenCheckBox);
      Mnemonics.setButton(this, ziellisteBearbeitenCheckBox, International.getStringWithMnemonic("Zielliste bearbeiten"));
      gruppenBearbeitenCheckBox.setNextFocusableComponent(nachrichtenAdminAnzeigenCheckBox);
      Mnemonics.setButton(this, gruppenBearbeitenCheckBox, International.getStringWithMnemonic("Gruppen bearbeiten"));
      vollzugriffCheckBox.setNextFocusableComponent(efaConfigCheckBox);
      Mnemonics.setButton(this, vollzugriffCheckBox, International.getStringWithMnemonic("Fahrtenbuch-Vollzugriff"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.WEST);
      jPanel2.add(jScrollPane1,      new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jScrollPane1.getViewport().add(adminList, null);
      jPanel2.add(newButton,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(deleteButton,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(editPanel, BorderLayout.CENTER);
      editPanel.add(nameLabel,              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(name,            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(pwdLabel,              new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(password,            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(passwordButton,            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(permissionsLabel,              new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
      editPanel.add(adminsVerwaltenCheckBox,              new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(passwortAendernCheckBox,              new GridBagConstraints(0, 5, 4, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(fahrtenbuchAuswaehlenCheckBox,              new GridBagConstraints(0, 8, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(fahrtenbuchAnzeigenCheckBox,              new GridBagConstraints(0, 9, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(bootsstatusBearbeitenCheckBox,              new GridBagConstraints(0, 10, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(bootsreservierungCheckBox,              new GridBagConstraints(0, 11, 4, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(nachrichtenAdminAnzeigenCheckBox,              new GridBagConstraints(0, 16, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(logdateiAnzeigenCheckBox,               new GridBagConstraints(0, 21, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(nachrichtenAdminGelesenDefaultCheckBox,                 new GridBagConstraints(2, 17, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(nachrichtenAdminAllowedGelesenMarkierenCheckBox,                   new GridBagConstraints(0, 17, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
      editPanel.add(saveButton,             new GridBagConstraints(0, 23, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
      editPanel.add(efaConfigCheckBox,           new GridBagConstraints(0, 7, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(efaBeendenCheckBox,            new GridBagConstraints(0, 22, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(nachrichtenBootswartAnzeigenCheckBox,            new GridBagConstraints(0, 18, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(adminsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(nachrichtenBootswartAllowedGelesenMarkierenCheckBox,          new GridBagConstraints(0, 19, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 0), 0, 0));
      editPanel.add(nachrichtenBootswartGelesenDefaultCheckBox,          new GridBagConstraints(2, 19, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(statistikErstellenCheckBox,          new GridBagConstraints(0, 20, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(emailLabel,         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(email,       new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(bootslisteBearbeitenCheckBox,        new GridBagConstraints(0, 12, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(mitgliederlisteBearbeitenCheckBox,       new GridBagConstraints(0, 13, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(ziellisteBearbeitenCheckBox,      new GridBagConstraints(0, 14, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(gruppenBearbeitenCheckBox,     new GridBagConstraints(0, 15, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      editPanel.add(vollzugriffCheckBox,    new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
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


  void listAdmins() {
    if (Daten.efaConfig.admins.size() == 0) { cancel(); return; }
    String[] k = Daten.efaConfig.admins.getKeysArray();
    Arrays.sort(k,0,k.length);
    if (k == null || k.length == 0) { cancel(); return; }
    Vector adminNames = new Vector();
    for (int i=0; i<k.length; i++) {
      Admin a = Daten.efaConfig.admins.get(k[i]);
      adminNames.add(a.name);
    }
    adminList.setListData(adminNames);
    saveButton.setEnabled(false);
  }


  void showAdmin(String name) {
    Admin admin;
    if (name != null) {
      admin = Daten.efaConfig.admins.get(name);
      password.setText(International.getString("verschlüsselt"));
      _password = admin.password;
      neuerEintrag = false;
    } else {
      admin = new Admin("",""); // neuer Admin
      password.setText("--- "+International.getString("leer")+" ---");
      _password = null;
      neuerEintrag = true;
    }
    if (admin == null) return;
    this.name.setText(admin.name);
    this.email.setText(admin.email);
    adminsVerwaltenCheckBox.setSelected(admin.allowedAdminsVerwalten);
    passwortAendernCheckBox.setSelected(admin.allowedPasswortAendern);
    vollzugriffCheckBox.setSelected(admin.allowedVollzugriff);
    efaConfigCheckBox.setSelected(admin.allowedEfaConfig);
    fahrtenbuchAuswaehlenCheckBox.setSelected(admin.allowedFahrtenbuchAuswaehlen);
    fahrtenbuchAnzeigenCheckBox.setSelected(admin.allowedFahrtenbuchBearbeiten);
    bootsstatusBearbeitenCheckBox.setSelected(admin.allowedBootsstatusBearbeiten);
    bootsreservierungCheckBox.setSelected(admin.allowedBootsreservierung);
    bootslisteBearbeitenCheckBox.setSelected(admin.allowedBootslisteBearbeiten);
    mitgliederlisteBearbeitenCheckBox.setSelected(admin.allowedMitgliederlisteBearbeiten);
    ziellisteBearbeitenCheckBox.setSelected(admin.allowedZiellisteBearbeiten);
    gruppenBearbeitenCheckBox.setSelected(admin.allowedGruppenBearbeiten);
    nachrichtenAdminAnzeigenCheckBox.setSelected(admin.allowedNachrichtenAnzeigenAdmin);
    nachrichtenAdminGelesenDefaultCheckBox.setSelected(admin.nachrichtenAdminGelesenMarkierenDefault);
    nachrichtenAdminAllowedGelesenMarkierenCheckBox.setSelected(admin.nachrichtenAdminAllowedGelesenMarkieren);
    nachrichtenBootswartAnzeigenCheckBox.setSelected(admin.allowedNachrichtenAnzeigenBootswart);
    nachrichtenBootswartGelesenDefaultCheckBox.setSelected(admin.nachrichtenBootswartGelesenMarkierenDefault);
    nachrichtenBootswartAllowedGelesenMarkierenCheckBox.setSelected(admin.nachrichtenBootswartAllowedGelesenMarkieren);
    statistikErstellenCheckBox.setSelected(admin.allowedStatistikErstellen);
    logdateiAnzeigenCheckBox.setSelected(admin.allowedLogdateiAnzeigen);
    efaBeendenCheckBox.setSelected(admin.allowedEfaBeenden);
    if (admin.name.equals(Admin.SUPERADMIN)) { // SuperAdmin
      // bestimmte Felder des Super-Admins dürfen nicht verändert werden
      this.name.setEditable(false);
      adminsVerwaltenCheckBox.setEnabled(false);
      passwortAendernCheckBox.setEnabled(false);
      vollzugriffCheckBox.setEnabled(false);
      efaConfigCheckBox.setEnabled(false);
      fahrtenbuchAuswaehlenCheckBox.setEnabled(false);
      fahrtenbuchAnzeigenCheckBox.setEnabled(false);
      bootsstatusBearbeitenCheckBox.setEnabled(false);
      bootsreservierungCheckBox.setEnabled(false);
      bootslisteBearbeitenCheckBox.setEnabled(false);
      mitgliederlisteBearbeitenCheckBox.setEnabled(false);
      ziellisteBearbeitenCheckBox.setEnabled(false);
      gruppenBearbeitenCheckBox.setEnabled(false);
      nachrichtenAdminAnzeigenCheckBox.setEnabled(false);
      nachrichtenAdminAllowedGelesenMarkierenCheckBox.setEnabled(false);
      nachrichtenAdminGelesenDefaultCheckBox.setEnabled(true);
      nachrichtenBootswartAnzeigenCheckBox.setEnabled(true);
      nachrichtenBootswartAllowedGelesenMarkierenCheckBox.setEnabled(true);
      nachrichtenBootswartGelesenDefaultCheckBox.setEnabled(true);
      statistikErstellenCheckBox.setEnabled(false);
      logdateiAnzeigenCheckBox.setEnabled(false);
      efaBeendenCheckBox.setEnabled(false);
      deleteButton.setEnabled(false);

      // Super-Admin darf per default (fast) alles
      adminsVerwaltenCheckBox.setSelected(true);
      passwortAendernCheckBox.setSelected(true);
      vollzugriffCheckBox.setSelected(true);
      efaConfigCheckBox.setSelected(true);
      fahrtenbuchAuswaehlenCheckBox.setSelected(true);
      fahrtenbuchAnzeigenCheckBox.setSelected(true);
      bootsstatusBearbeitenCheckBox.setSelected(true);
      bootsreservierungCheckBox.setSelected(true);
      bootslisteBearbeitenCheckBox.setSelected(true);
      mitgliederlisteBearbeitenCheckBox.setSelected(true);
      ziellisteBearbeitenCheckBox.setSelected(true);
      gruppenBearbeitenCheckBox.setSelected(true);
      nachrichtenAdminAnzeigenCheckBox.setSelected(true);
      statistikErstellenCheckBox.setSelected(true);
      logdateiAnzeigenCheckBox.setSelected(true);
      efaBeendenCheckBox.setSelected(true);
      nachrichtenAdminAllowedGelesenMarkierenCheckBox.setSelected(true);
      deleteButton.setSelected(true);
    } else {
      // Bei normalen Admins dürfen alle Felder verändert werden
      this.name.setEditable(true);
      adminsVerwaltenCheckBox.setEnabled(false);
      passwortAendernCheckBox.setEnabled(true);
      vollzugriffCheckBox.setEnabled(true);
      efaConfigCheckBox.setEnabled(true);
      fahrtenbuchAuswaehlenCheckBox.setEnabled(true);
      fahrtenbuchAnzeigenCheckBox.setEnabled(true);
      bootsstatusBearbeitenCheckBox.setEnabled(true);
      bootsreservierungCheckBox.setEnabled(true);
      bootslisteBearbeitenCheckBox.setEnabled(true);
      mitgliederlisteBearbeitenCheckBox.setEnabled(true);
      ziellisteBearbeitenCheckBox.setEnabled(true);
      gruppenBearbeitenCheckBox.setEnabled(true);
      nachrichtenAdminAnzeigenCheckBox.setEnabled(true);
      nachrichtenAdminGelesenDefaultCheckBox.setEnabled(true);
      nachrichtenAdminAllowedGelesenMarkierenCheckBox.setEnabled(true);
      nachrichtenBootswartAnzeigenCheckBox.setEnabled(true);
      nachrichtenBootswartGelesenDefaultCheckBox.setEnabled(true);
      nachrichtenBootswartAllowedGelesenMarkierenCheckBox.setEnabled(true);
      statistikErstellenCheckBox.setEnabled(true);
      logdateiAnzeigenCheckBox.setEnabled(true);
      efaBeendenCheckBox.setEnabled(true);
      deleteButton.setEnabled(true);

      // normaler Admin darf Admins nicht verwalten
      adminsVerwaltenCheckBox.setSelected(false);
    }
    saveButton.setEnabled(true);
    this.name.requestFocus();
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (saveButton.isEnabled()) saveButton_actionPerformed(null);
    if (!Daten.efaConfig.writeFile()) {
      Dialog.error(LogString.logstring_fileWritingFailed(Daten.efaConfig.getFileName(), International.getString("Konfigurationsdatei")));;
    } else {
      Logger.log(Logger.INFO,Logger.MSG_ADMIN_ACTION_ADMINSMODIFIED,
              International.getMessage("Änderungen an {listname} gespeichert.",International.getString("Admins")));
    }
    cancel();
  }

  void adminList_mouseClicked(MouseEvent e) {
    if (adminList.getSelectedIndex()<0) {
      saveButton.setEnabled(false);
    } else {
      showAdmin((String)adminList.getSelectedValue());
    }
  }

  void newButton_actionPerformed(ActionEvent e) {
    showAdmin(null);
  }

  void deleteButton_actionPerformed(ActionEvent e) {
    if (adminList.getSelectedIndex()<0) {
      Dialog.error(International.getString("Bitte wähle zuerst aus der linken Liste einen Admin aus!"));
      return;
    }
    String name = (String)adminList.getSelectedValue();
    if (Dialog.yesNoDialog(International.getString("Admin löschen"),
            International.getMessage("Willst Du den Admin {name} wirklich löschen?",name)) == Dialog.YES) {
      Daten.efaConfig.admins.remove(name);
      Logger.log(Logger.INFO,Logger.MSG_ADMIN_ACTION_ADMINDELETED,
              International.getMessage("Admin '{name}' gelöscht.",name));
      listAdmins();
    }
  }

  void saveButton_actionPerformed(ActionEvent e) {
    String name = EfaUtil.removeSepFromString(this.name.getText().trim());
    String altername = "";
    if (!neuerEintrag && adminList.getSelectedIndex()>=0) altername = (String)adminList.getSelectedValue();

    if (name.length() == 0) {
      Dialog.error(International.getString("Bitte gib einen Namen für den Admin ein!"));
      this.name.requestFocus();
      return;
    }

    if (!name.equals(altername) && Daten.efaConfig.admins.get(name) != null) {
      Dialog.error(International.getString("Es gibt bereits einen Admin mit diesem Namen.") + " " +
              International.getString("Bitte wähle einen anderen Namen."));
      this.name.requestFocus();
      return;
    }

    if (_password == null) {
      Dialog.error(International.getString("Bitte gib zunächst ein Paßwort für den Admin ein!"));
      this.passwordButton.requestFocus();
      return;
    }

    if (altername.length()>0) {
      Daten.efaConfig.admins.remove(altername);
    }

    Admin admin = new Admin(name,_password);
    admin.allowedAdminsVerwalten = adminsVerwaltenCheckBox.isSelected();
    admin.allowedPasswortAendern = passwortAendernCheckBox.isSelected();
    admin.allowedVollzugriff = vollzugriffCheckBox.isSelected();
    admin.allowedEfaConfig = efaConfigCheckBox.isSelected();
    admin.allowedFahrtenbuchAuswaehlen = fahrtenbuchAuswaehlenCheckBox.isSelected();
    admin.allowedFahrtenbuchBearbeiten = fahrtenbuchAnzeigenCheckBox.isSelected();
    admin.allowedBootsstatusBearbeiten = bootsstatusBearbeitenCheckBox.isSelected();
    admin.allowedBootsreservierung = bootsreservierungCheckBox.isSelected();
    admin.allowedBootslisteBearbeiten = bootslisteBearbeitenCheckBox.isSelected();
    admin.allowedMitgliederlisteBearbeiten = mitgliederlisteBearbeitenCheckBox.isSelected();
    admin.allowedZiellisteBearbeiten = ziellisteBearbeitenCheckBox.isSelected();
    admin.allowedGruppenBearbeiten = gruppenBearbeitenCheckBox.isSelected();
    admin.allowedNachrichtenAnzeigenAdmin = nachrichtenAdminAnzeigenCheckBox.isSelected();
    admin.nachrichtenAdminGelesenMarkierenDefault = nachrichtenAdminGelesenDefaultCheckBox.isSelected();
    admin.nachrichtenAdminAllowedGelesenMarkieren = nachrichtenAdminAllowedGelesenMarkierenCheckBox.isSelected();
    admin.allowedNachrichtenAnzeigenBootswart = nachrichtenBootswartAnzeigenCheckBox.isSelected();
    admin.nachrichtenBootswartGelesenMarkierenDefault = nachrichtenBootswartGelesenDefaultCheckBox.isSelected();
    admin.nachrichtenBootswartAllowedGelesenMarkieren = nachrichtenBootswartAllowedGelesenMarkierenCheckBox.isSelected();
    admin.allowedStatistikErstellen = statistikErstellenCheckBox.isSelected();
    admin.allowedLogdateiAnzeigen = logdateiAnzeigenCheckBox.isSelected();
    admin.allowedEfaBeenden = efaBeendenCheckBox.isSelected();
    admin.email = email.getText().trim();

    Daten.efaConfig.admins.put(name,admin);

    if (neuerEintrag) {
      Logger.log(Logger.INFO,Logger.MSG_ADMIN_ACTION_ADMINCREATED,
              International.getMessage("Neuer Admin '{name}' angelegt.",name));
    } else {
      if (!name.equals(altername)) {
          Logger.log(Logger.INFO,Logger.MSG_ADMIN_ACTION_ADMINRENAMED,
                  International.getMessage("Admin '{oldname}' in '{newname}' umbenannt.",altername,name));
      }
      Logger.log(Logger.INFO,Logger.MSG_ADMIN_ACTION_ADMINCHANGED,
              International.getMessage("Daten des Admins '{name}' geändert.",name));
    }

    listAdmins();

    if (email.getText().trim().length()>0) {
      if (!EmailSender.emailPluginInstalled()) {
        DownloadFrame.getPlugin(Daten.EFA_SHORTNAME,
                Daten.PLUGIN_EMAIL_NAME,Daten.PLUGIN_EMAIL_FILE,Daten.PLUGIN_EMAIL_HTML,"NoClassDefFoundError",null,false);
      }
    }
  }

  void passwordButton_actionPerformed(ActionEvent e) {
    if (!saveButton.isEnabled()) return;

    String pwd = NewPasswordFrame.getNewPassword(this,this.name.getText().trim());
    if (pwd == null) return;
    this._password = EfaUtil.getSHA(pwd);
    password.setText(International.getString("verschlüsselt"));
  }

  void name_focusLost(FocusEvent e) {
    name.setText(name.getText().trim().toLowerCase());
  }



}
