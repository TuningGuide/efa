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

import de.nmichael.efa.core.FahrtenbuchNeuFortsetzenFrame;
import de.nmichael.efa.core.StatistikFrame;
import de.nmichael.efa.core.EfaConfigFrame;
import de.nmichael.efa.core.Fahrtenbuch;
import de.nmichael.efa.core.EfaFrame;
import de.nmichael.efa.core.AuswahlFrame;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
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

public class AdminFrame extends JDialog implements ActionListener {
  EfaDirektFrame parent;
  Admin admin;
  BootStatus bootstatus;
  int oldFontSize;
  int oldFontStyle;

  AdminFrame thisFrame;
  Fahrtenbuch aktFb;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton adminsButton = new JButton();
  JButton fahrtenbuchButton = new JButton();
  JButton bootsstatusButton = new JButton();
  JButton nachrichtenButton = new JButton();
  JButton logButton = new JButton();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel fbLabel = new JLabel();
  JButton selectFbButton = new JButton();
  JButton efaConfigButton = new JButton();
  JButton statistikButton = new JButton();
  JButton bootslisteButton = new JButton();
  JButton mitgliederlisteButton = new JButton();
  JButton ziellisteButton = new JButton();
  JButton gruppenButton = new JButton();
  JButton vollzugriffButton = new JButton();
  JLabel autoNewFbLabel = new JLabel();
  JButton lockButton = new JButton();
  JButton cmdButton = new JButton();



  public AdminFrame(EfaDirektFrame parent, Admin admin, BootStatus bootstatus) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.parent = parent;
    this.admin = admin;
    this.bootstatus = bootstatus;
    this.thisFrame = this;
    this.setTitle("Admin-Modus ["+admin.name+"]");
    this.fbLabel.setText("Fahrtenbuch: "+Daten.fahrtenbuch.getFileName());
    EfaUtil.pack(this);

    Daten.applMode = Daten.APPL_MODE_ADMIN;

    if (!admin.allowedAdminsVerwalten) {
        this.adminsButton.setText("Paßwort ändern");
        this.adminsButton.setVisible(admin.allowedPasswortAendern);
    }
    this.vollzugriffButton.setVisible(admin.allowedVollzugriff);
    this.efaConfigButton.setVisible(admin.allowedEfaConfig);
    this.selectFbButton.setVisible(admin.allowedFahrtenbuchAuswaehlen);
    this.fahrtenbuchButton.setVisible(admin.allowedFahrtenbuchBearbeiten);
    this.bootsstatusButton.setVisible(admin.allowedBootsstatusBearbeiten ||
            admin.allowedBootsreservierung);
    this.bootslisteButton.setVisible(admin.allowedBootslisteBearbeiten);
    this.mitgliederlisteButton.setVisible(admin.allowedMitgliederlisteBearbeiten);
    this.ziellisteButton.setVisible(admin.allowedZiellisteBearbeiten);
    this.gruppenButton.setVisible(admin.allowedGruppenBearbeiten);
    this.nachrichtenButton.setVisible(admin.allowedNachrichtenAnzeigenAdmin || admin.allowedNachrichtenAnzeigenBootswart);
    this.statistikButton.setVisible(admin.allowedStatistikErstellen);
    this.logButton.setVisible(admin.allowedLogdateiAnzeigen);
    this.lockButton.setVisible(admin.allowedEfaSperren);
    this.cmdButton.setVisible(admin.allowedExecCommand);

    updateButtons();

    this.adminsButton.requestFocus();
  }


  void updateButtons() {
    try {
      switch(parent.checkUnreadMessages()) {
        case Nachricht.ADMIN:
          nachrichtenButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mailAdmin.gif")));
          break;
        case Nachricht.BOOTSWART:
          nachrichtenButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mailBootswart.gif")));
          break;
        case Nachricht.ALLE:
          nachrichtenButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mail.gif")));
          break;
        default: nachrichtenButton.setIcon(null);
      }

      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_autoNewFb_datei != null &&
          Daten.efaConfig.efaDirekt_autoNewFb_datei.length()>0 &&
          Daten.efaConfig.efaDirekt_autoNewFb_datum != null) {
        autoNewFbLabel.setText(Daten.efaConfig.efaDirekt_autoNewFb_datum.tag+"."+
                               Daten.efaConfig.efaDirekt_autoNewFb_datum.monat+"."+Daten.efaConfig.efaDirekt_autoNewFb_datum.jahr+": "+
                               Daten.efaConfig.efaDirekt_autoNewFb_datei);
      } else {
        autoNewFbLabel.setText("");
      }

    } catch(Exception e) { EfaUtil.foo(); }
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

    jPanel1.setLayout(borderLayout1);
    okButton.setNextFocusableComponent(adminsButton);
    okButton.setMnemonic('M');
    okButton.setText("Admin-Modus verlassen");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout1);
    adminsButton.setNextFocusableComponent(vollzugriffButton);
    adminsButton.setToolTipText("");
    adminsButton.setMnemonic('V');
    adminsButton.setText("Admins verwalten");
    adminsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adminsButton_actionPerformed(e);
      }
    });
    fahrtenbuchButton.setNextFocusableComponent(bootsstatusButton);
    fahrtenbuchButton.setMnemonic('F');
    fahrtenbuchButton.setText("Fahrtenbuch bearbeiten");
    fahrtenbuchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtenbuchButton_actionPerformed(e);
      }
    });
    bootsstatusButton.setNextFocusableComponent(bootslisteButton);
    bootsstatusButton.setMnemonic('B');
    bootsstatusButton.setText("Bootsstatus bearbeiten");
    bootsstatusButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootsstatusButton_actionPerformed(e);
      }
    });
    this.setTitle("Admin-Modus");
    nachrichtenButton.setNextFocusableComponent(statistikButton);
    nachrichtenButton.setMnemonic('N');
    nachrichtenButton.setText("Nachrichten anzeigen");
    nachrichtenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nachrichtenButton_actionPerformed(e);
      }
    });
    logButton.setNextFocusableComponent(lockButton);
    logButton.setMnemonic('L');
    logButton.setText("Logdatei anzeigen");
    logButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        logButton_actionPerformed(e);
      }
    });
    jPanel3.setLayout(gridBagLayout2);
    fbLabel.setForeground(Color.blue);
    fbLabel.setText("Fahrtenbuch: ");
    selectFbButton.setNextFocusableComponent(fahrtenbuchButton);
    selectFbButton.setMnemonic('W');
    selectFbButton.setText("Fahrtenbuch auswählen");
    selectFbButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectFbButton_actionPerformed(e);
      }
    });
    efaConfigButton.setNextFocusableComponent(selectFbButton);
    efaConfigButton.setMnemonic('K');
    efaConfigButton.setText("efa konfigurieren");
    efaConfigButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaConfigButton_actionPerformed(e);
      }
    });
    statistikButton.setNextFocusableComponent(logButton);
    statistikButton.setMnemonic('S');
    statistikButton.setText("Statistiken erstellen");
    statistikButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        statistikButton_actionPerformed(e);
      }
    });
    bootslisteButton.setNextFocusableComponent(mitgliederlisteButton);
    bootslisteButton.setMnemonic('O');
    bootslisteButton.setText("Bootsliste bearbeiten");
    bootslisteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootslisteButton_actionPerformed(e);
      }
    });
    mitgliederlisteButton.setNextFocusableComponent(ziellisteButton);
    mitgliederlisteButton.setMnemonic('T');
    mitgliederlisteButton.setText("Mitgliederliste bearbeiten");
    mitgliederlisteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mitgliederlisteButton_actionPerformed(e);
      }
    });
    ziellisteButton.setNextFocusableComponent(gruppenButton);
    ziellisteButton.setMnemonic('Z');
    ziellisteButton.setText("Zielliste bearbeiten");
    ziellisteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ziellisteButton_actionPerformed(e);
      }
    });
    gruppenButton.setNextFocusableComponent(nachrichtenButton);
    gruppenButton.setMnemonic('G');
    gruppenButton.setText("Gruppen bearbeiten");
    gruppenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        gruppenButton_actionPerformed(e);
      }
    });
    vollzugriffButton.setNextFocusableComponent(efaConfigButton);
    vollzugriffButton.setMnemonic('U');
    vollzugriffButton.setText("Fahrtenbuch-Vollzugriff");
    vollzugriffButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        vollzugriffButton_actionPerformed(e);
      }
    });
    autoNewFbLabel.setHorizontalAlignment(SwingConstants.CENTER);
    autoNewFbLabel.setText("Neu zum 1.1.2008: ");
    lockButton.setNextFocusableComponent(cmdButton);
    lockButton.setMnemonic('P');
    lockButton.setText("efa sperren");
    lockButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        lockButton_actionPerformed(e);
      }
    });
    cmdButton.setNextFocusableComponent(okButton);
    cmdButton.setText("Kommando ausf�hren");
    cmdButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cmdButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(okButton, BorderLayout.SOUTH);
    jPanel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(adminsButton,            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(fahrtenbuchButton,             new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(bootsstatusButton,             new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(nachrichtenButton,           new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(fbLabel,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0));
    jPanel3.add(autoNewFbLabel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
    jPanel1.add(jPanel3, BorderLayout.NORTH);
    jPanel2.add(logButton,             new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(selectFbButton,            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(efaConfigButton,          new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(statistikButton,         new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(bootslisteButton,        new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(mitgliederlisteButton,       new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(ziellisteButton,      new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(gruppenButton,     new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(vollzugriffButton,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(lockButton,    new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
    jPanel2.add(cmdButton,   new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowActivated(WindowEvent e) {
        this_windowActivated(e);
      }
    });
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      if (!this.isEnabled()) return;
      cancel();
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel() {
    try {
      if (this != Dialog.frameCurrent()) {
        Dialog.frameCurrent().toFront();
      }
    } catch(Exception e) {}
    if (!this.isEnabled()) return; // falls noch "Fahrtenbuch bearbeiten" offen ist
    try {
      this.parent.iniBootsListen();
      this.parent.efaDirektBackgroundTask.interrupt();
      this.parent.setButtonsLookAndFeel();
      this.parent.updateUnreadMessages();
      this.parent.uhr.setVisible(Daten.efaConfig.efaDirekt_showUhr);
      if (!this.parent.sunrisePanel.isVisible() && Daten.efaConfig.efaDirekt_sunRiseSet_show) {
        this.parent.efaUhrUpdater.updateSunriseNow();
      }
      this.parent.sunrisePanel.setVisible(Daten.efaConfig.efaDirekt_sunRiseSet_show);
      this.parent.updateNews();
      this.parent.efaFrame.updateMannschaftenAndShowButton();
      this.parent.setEnabled(true);
    } catch(Exception ee) {
      EfaUtil.foo();
    }
    Dialog.frameClosed(this);
    dispose();
    Logger.log(Logger.INFO,"Admin-Modus verlassen.");
    Daten.applMode = Daten.APPL_MODE_NORMAL;
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  void okButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void adminsButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedAdminsVerwalten) {
      String pwd = NewPasswordFrame.getNewPassword(this,this.admin.name);
      if (pwd == null) return;
      this.admin .password = EfaUtil.getSHA(pwd);
      Daten.efaConfig.admins.put(admin.name,admin);
      if (!Daten.efaConfig.writeFile()) Dialog.error("Konfigurationsdatei konnte nicht geschrieben werden!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+adminsButton.getText()+"'");
    AdminVerwaltenFrame dlg = new AdminVerwaltenFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();

    this.admin = (Admin)Daten.efaConfig.admins.get(this.admin.name);
    if (this.admin == null) { // sollte nie passieren!
      Dialog.error("Der Admin wurde gelöscht!");
      cancel();
    }
  }

  void configureAutoNewFB() {
    Logger.log(Logger.INFO,"Admin: Aktion 'Automatisches Erstellen eines neuen Fahrtenbuchs konfigurieren'");
    FahrtenbuchAutoContinueFrame dlg = new FahrtenbuchAutoContinueFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void selectFbButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedFahrtenbuchAuswaehlen) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }

    int ret = Dialog.auswahlDialog("Fahrtenbuch festlegen",
                              "Möchtest Du jetzt sofort ein neues Fahrtenbuch auswählen/anlegen,\n"+
                              "oder das automatische Erstellen eines neuen Fahrtenbuchs (z.B. zum\n"+
                              "Jahreswechsel) vorbereiten?","Jetzt","Für später vorbereiten",true);
    switch(ret) {
      case 0: break; // Jetzt
      case 1: configureAutoNewFB();
              updateButtons();
              return;
      default: return;
    }

    if (parent.sindNochBooteUnterwegs()) {
      Dialog.error("Es kann kein neues Fahrtenbuch ausgewählt werden,\n"+
                   "da noch einige Boote unterwegs sind!\n"+
                   "Bitte trage zuerst alle Boote zurück.");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+selectFbButton.getText()+"'");

    String dat = null;
    switch (Dialog.auswahlDialog("Fahrtenbuch auswählen","Möchtest Du ein neues Fahrtenbuch erstellen, oder ein vorhandenes öffnen?","Neues Fahrtenbuch erstellen","Vorhandenes Fahrtenbuch öffnen")) {
      case 0: // Neues Fahrtenbuch erstellen
        FahrtenbuchNeuFortsetzenFrame dlg = new FahrtenbuchNeuFortsetzenFrame(this,false);
        Dialog.setDlgLocation(dlg,this);
        dlg.setModal(!Dialog.tourRunning);
        dlg.show();
        if (Daten.fahrtenbuch != null) {
          dat = Daten.fahrtenbuch.getFileName();
        }
        break;
      case 1: // Vorhandenes Fahrtenbuch öffnen
        dat = Dialog.dateiDialog(this,"Fahrtenbuch öffnen","efa Fahrtenbuch (*.efb)","efb",Daten.efaDataDirectory,false);
        break;
      default:
        return;
    }
    if (dat == null || dat.length()==0) return;
    Daten.efaConfig.direkt_letzteDatei = dat;
    if (!Daten.efaConfig.writeFile()) {
      Dialog.error("Datei "+Daten.efaConfig.getFileName()+" kann nicht geschrieben werden!");
      Logger.log(Logger.ERROR,"Datei "+Daten.efaConfig.getFileName()+" kann nicht geschrieben werden!");
    }
    parent.readFahrtenbuch();
    parent.iniBootsListen();
    this.fbLabel.setText("Fahrtenbuch: "+dat);
  }

  void fahrtenbuchButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedFahrtenbuchBearbeiten) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion 'Fahrtenbuch bearbeiten' (Start).");
    aktFb = Daten.fahrtenbuch;
    this.setEnabled(false);
    if (Dialog.isFontSizeChanged()) {
      oldFontSize = Dialog.getFontSize();
      oldFontStyle = Dialog.getFontStyle();
      Dialog.setGlobalFontSize(Dialog.getDefaultFontSize(),Dialog.getDefaultFontStyle());
    } else oldFontSize = -1;
    EfaFrame efaFrame = new EfaFrame(thisFrame,parent, Daten.fahrtenbuch.getFileName(),this.admin,EfaFrame.MODE_ADMIN_NUR_FAHRTEN);

    Dimension dlgSize = efaFrame.getSize();
    efaFrame.setLocation((Dialog.screenSize.width - dlgSize.width) / 2, (Dialog.screenSize.height - dlgSize.height) / 2);
    efaFrame.show();
  }

  public void fahrtenbuchClosed() {
    Logger.log(Logger.INFO,"Admin: Fahrtenbuch bearbeiten (Ende).");
    if (oldFontSize > 0) Dialog.setGlobalFontSize(oldFontSize, oldFontStyle);
    if (Daten.fahrtenbuch != aktFb) {
      Daten.fahrtenbuch = aktFb;
      Daten.fahrtenbuch.readFile();
      if (Daten.fahrtenbuch.getDaten().mitglieder != null) Daten.fahrtenbuch.getDaten().mitglieder.getAliases();
    }
    this.setEnabled(true);
  }

  void bootsstatusButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedBootsstatusBearbeiten && !this.admin.allowedBootsreservierung) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+bootsstatusButton.getText()+"'");
    BootStatusListeFrame dlg = new BootStatusListeFrame(this,bootstatus,admin);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void nachrichtenButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedNachrichtenAnzeigenAdmin && !this.admin.allowedNachrichtenAnzeigenBootswart) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+nachrichtenButton.getText()+"'");
    AdminNachrichtenFrame dlg = new AdminNachrichtenFrame(this,Daten.nachrichten,this.admin);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    updateButtons();
  }

  void logButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedLogdateiAnzeigen) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+logButton.getText()+"'");
    AdminShowLogFrame dlg = new AdminShowLogFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void efaConfigButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedEfaConfig) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+efaConfigButton.getText()+"'");
    EfaConfigFrame dlg = new EfaConfigFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();

    if (parent != null && parent.efaFrame != null && Daten.efaConfig != null) {
      parent.efaFrame.obmannLabel.setVisible(Daten.efaConfig.showObmann);
      parent.efaFrame.obmann.setVisible(Daten.efaConfig.showObmann);
    }

  }

  void statistikButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedStatistikErstellen) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+statistikButton.getText()+"'");
    if (Dialog.isFontSizeChanged()) {
      oldFontSize = Dialog.getFontSize();
      oldFontStyle = Dialog.getFontStyle();
      Dialog.setGlobalFontSize(Dialog.getDefaultFontSize(),Dialog.getDefaultFontStyle());
    } else oldFontSize = -1;
    StatistikFrame dlg = new StatistikFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    if (oldFontSize > 0) Dialog.setGlobalFontSize(oldFontSize, oldFontStyle);
  }

  void bootslisteButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedBootslisteBearbeiten) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+bootslisteButton.getText()+"'");
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.BOOTE,bootstatus);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void mitgliederlisteButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedMitgliederlisteBearbeiten) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+mitgliederlisteButton.getText()+"'");
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.MITGLIEDER);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void ziellisteButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedZiellisteBearbeiten) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+ziellisteButton.getText()+"'");
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.ZIELE);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void gruppenButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedGruppenBearbeiten) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+gruppenButton.getText()+"'");
    AuswahlFrame dlg = new AuswahlFrame(this,AuswahlFrame.GRUPPEN);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void vollzugriffButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedVollzugriff) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion 'Fahrtenbuch bearbeiten - Vollzugriff' (Start).");
    aktFb = Daten.fahrtenbuch;
    this.setEnabled(false);
    if (Dialog.isFontSizeChanged()) {
      oldFontSize = Dialog.getFontSize();
      oldFontStyle = Dialog.getFontStyle();
      Dialog.setGlobalFontSize(Dialog.getDefaultFontSize(),Dialog.getDefaultFontStyle());
    } else oldFontSize = -1;
    EfaFrame efaFrame = new EfaFrame(thisFrame,parent,Daten.fahrtenbuch.getFileName(),this.admin,EfaFrame.MODE_ADMIN);
    Dimension dlgSize = efaFrame.getSize();
    efaFrame.setLocation((Dialog.screenSize.width - dlgSize.width) / 2, (Dialog.screenSize.height - dlgSize.height) / 2);
    efaFrame.show();

  }

  void this_windowActivated(WindowEvent e) {
    if (!isEnabled()) {
      try {
        Object[] frames = Dialog.frameStack.toArray();
        boolean afterAdmin = false;
        for (int i=0; i<frames.length; i++) {
          if (afterAdmin && i+1 == frames.length) ((Window)frames[i]).toFront();
          if ( ((Window)frames[i]).getClass().isInstance(this) ) afterAdmin = true;
        }
      } catch(Exception ee) {}
    }
  }

  void lockButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedEfaSperren) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuführen!");
      return;
    }
    Logger.log(Logger.INFO,"Admin: Aktion '"+lockButton.getText()+"'");
    AdminLockEfaFrame dlg = new AdminLockEfaFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    if (Daten.efaConfig != null) {
      if (Daten.efaConfig.efaDirekt_locked) {
        cancel();
        parent.lockEfa();
      } else {
        // efaBackgroundTask updaten (auch, falls Datum == null)
        parent.lockEfaAt(Daten.efaConfig.efaDirekt_lockEfaFromDatum,Daten.efaConfig.efaDirekt_lockEfaFromZeit);
      }
    }
  }

  void cmdButton_actionPerformed(ActionEvent e) {
    if (!this.admin.allowedExecCommand) {
      Dialog.error("Du hast als Admin '"+admin.name+"' nicht die Berechtigung, diese Funktion auszuf�hren!");
      return;
    }
    String cmd = Dialog.inputDialog("Betriebssystemkommando ausf�hren","Betriebssystemkommando:",Daten.efaConfig.efadirekt_adminLastOsCommand);
    if (cmd == null || cmd.length()==0) {
      return;
    }
    try {
      Logger.log(Logger.INFO,"Admin: Starte Kommando: "+cmd);
      Runtime.getRuntime().exec(cmd);
      Daten.efaConfig.efadirekt_adminLastOsCommand = cmd;
    } catch(Exception ee) {
      Logger.log(Logger.ERROR,"Admin: Kann Kommando '"+cmd+"' nicht ausf�hren.");
    }
  }


}
