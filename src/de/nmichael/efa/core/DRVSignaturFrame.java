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
import java.util.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

// @i18n complete (needs no internationalization -- only relevant for Germany)

public class DRVSignaturFrame extends JDialog implements ActionListener {
  private JDialog parent;
  private static DRVSignatur drvSignatur;
  private DRVSignatur orgSignatur;
  private static String meldungEingespielteFahrtenhefte;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton closeButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField fahrtenheft = new JTextField();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextField efTeilnehmernr = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField efVorname = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField efNachname = new JTextField();
  JLabel jLabel6 = new JLabel();
  JTextField efJahrgang = new JTextField();
  JLabel jLabel7 = new JLabel();
  JTextField efAnzAbzeichen = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField efGesKm = new JTextField();
  JLabel jLabel9 = new JLabel();
  JTextField efJahr = new JTextField();
  JLabel signatureValidLabel = new JLabel();
  JButton editMeldungButton = new JButton();
  JPanel fahrtenheftPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel hinweisLabel = new JLabel();
  JLabel hinweisLabelBsp = new JLabel();
  JLabel hinweisLabelAchtung = new JLabel();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel12 = new JLabel();
  JTextField efVersion = new JTextField();
  JTextField efKeyNr = new JTextField();
  JTextField efSignatur = new JTextField();
  JButton printButton = new JButton();
  JLabel jLabel13 = new JLabel();
  JLabel jLabel14 = new JLabel();
  JLabel jLabel15 = new JLabel();
  JTextField efAnzAbzeichenAB = new JTextField();
  JTextField efGesKmAB = new JTextField();
  JTextField efSigDatum = new JTextField();
  JLabel jLabel16 = new JLabel();
  JTextField efLetztKm = new JTextField();


  public DRVSignaturFrame(JDialog parent, DRVSignatur drvSignatur) {
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
    this.parent = parent;
    this.drvSignatur = drvSignatur;
    this.orgSignatur = drvSignatur;
    frIni(true);
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel(false);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
  }


  // Initialisierung des Frames
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      closeButton.setNextFocusableComponent(fahrtenheft);
      Mnemonics.setButton(this, closeButton, International.getStringWithMnemonic("Speichern"));
      closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      jLabel1.setText("elektronisches Fahrtenheft: ");
      jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel2.setText("elektronisches Fahrtenheft");
      jLabel3.setText("DRV-Teilnehmernummer: ");
      jLabel4.setText("Vorname: ");
      efVorname.setPreferredSize(new Dimension(200, 17));
      efVorname.setEditable(false);
      jLabel5.setText("Nachname: ");
      jLabel6.setText("Jahrgang: ");
      efJahrgang.setPreferredSize(new Dimension(200, 17));
      efJahrgang.setEditable(false);
      jLabel7.setText("Anzahl der Fahrtenabzeichen: ");
      jLabel8.setText("Insgesamt nachgewiesene Kilometer: ");
      efGesKm.setPreferredSize(new Dimension(200, 17));
      efGesKm.setEditable(false);
      jLabel9.setText("Jahr der letzten elektronischen Meldung: ");
      signatureValidLabel.setForeground(Daten.colorGreen);
      signatureValidLabel.setHorizontalAlignment(SwingConstants.CENTER);
      signatureValidLabel.setHorizontalTextPosition(SwingConstants.LEADING);
      signatureValidLabel.setText("Die Signatur ist gültig!");
      editMeldungButton.setNextFocusableComponent(printButton);
      editMeldungButton.setActionCommand("Neues elektronisches Fahrtenheft eingeben");
      editMeldungButton.setMnemonic('N');
      editMeldungButton.setText("Neues elektronisches Fahrtenheft eingeben");
      editMeldungButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editMeldungButton_actionPerformed(e);
        }
    });
      fahrtenheft.setNextFocusableComponent(editMeldungButton);
      fahrtenheft.setPreferredSize(new Dimension(500, 17));
      fahrtenheft.setEditable(false);
      fahrtenheft.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          fahrtenheft_focusLost(e);
        }
      });
      fahrtenheft.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          fahrtenheftKeyTyped(e);
        }
      });
      fahrtenheftPanel.setLayout(gridBagLayout2);
      fahrtenheftPanel.setBorder(BorderFactory.createEtchedBorder());
      efJahr.setPreferredSize(new Dimension(200, 17));
      efJahr.setEditable(false);
      efAnzAbzeichen.setPreferredSize(new Dimension(200, 17));
      efAnzAbzeichen.setEditable(false);
      efNachname.setPreferredSize(new Dimension(200, 17));
      efNachname.setEditable(false);
      efTeilnehmernr.setPreferredSize(new Dimension(200, 17));
      efTeilnehmernr.setEditable(false);
      this.setTitle("elektronisches Fahrtenheft");
      hinweisLabel.setForeground(Color.blue);
      hinweisLabel.setText("");
      hinweisLabelBsp.setForeground(Color.black);
      hinweisLabelBsp.setText("");
      hinweisLabelAchtung.setForeground(Color.red);
      hinweisLabelAchtung.setText("");
      jLabel10.setText("Fahrtenabzeichen-Version: ");
      jLabel11.setText("öffentlicher DRV-Schlüssel: ");
      jLabel12.setText("DRV-Signatur: ");
      efVersion.setPreferredSize(new Dimension(200, 17));
      efVersion.setEditable(false);
      efKeyNr.setPreferredSize(new Dimension(200, 17));
      efKeyNr.setEditable(false);
      efSignatur.setPreferredSize(new Dimension(200, 17));
      efSignatur.setEditable(false);
      printButton.setNextFocusableComponent(closeButton);
      printButton.setMnemonic('D');
      printButton.setText("Drucken");
      printButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          printButton_actionPerformed(e);
        }
    });
      jLabel13.setText("... davon Fahrtenabzeichen Jugend A/B: ");
      jLabel14.setText("... davon Kilometer Jugend A/B: ");
      jLabel15.setText("Ausstellungsdatum des Fahrtenhefts: ");
      efAnzAbzeichenAB.setPreferredSize(new Dimension(200, 17));
      efAnzAbzeichenAB.setEditable(false);
      efGesKmAB.setPreferredSize(new Dimension(200, 17));
      efGesKmAB.setEditable(false);
      efSigDatum.setPreferredSize(new Dimension(200, 17));
      efSigDatum.setEditable(false);
      jLabel16.setText("Kilometer bei letzter elektronischer Meldung: ");
      efLetztKm.setPreferredSize(new Dimension(200, 17));
      efLetztKm.setEditable(false);
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(closeButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
      jPanel2.add(fahrtenheft,        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
      jPanel2.add(editMeldungButton,         new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(fahrtenheftPanel,       new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 20, 0), 0, 0));

      fahrtenheftPanel.add(jLabel2,             new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
      fahrtenheftPanel.add(jLabel3,           new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efTeilnehmernr,          new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel4,           new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efVorname,          new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel5,           new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efNachname,          new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel6,           new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efJahrgang,          new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel7,           new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efAnzAbzeichen,          new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel8,           new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efGesKm,           new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel9,           new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efJahr,          new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(signatureValidLabel,              new GridBagConstraints(0, 20, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel10,           new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel11,         new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel12,        new GridBagConstraints(0, 16, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efVersion,        new GridBagConstraints(1, 14, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efKeyNr,       new GridBagConstraints(1, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efSignatur,       new GridBagConstraints(1, 16, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(hinweisLabel,          new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
      jPanel2.add(hinweisLabelBsp,     new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(hinweisLabelAchtung,    new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
      fahrtenheftPanel.add(printButton,        new GridBagConstraints(0, 21, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
      fahrtenheftPanel.add(jLabel13,      new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel14,     new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel15,    new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efAnzAbzeichenAB,   new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efGesKmAB,   new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efSigDatum,    new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(jLabel16,   new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenheftPanel.add(efLetztKm,   new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
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
  void cancel(boolean save) {
    if (save) {
      // nothing to do: drvSignatur already has correct value
    } else {
      drvSignatur = orgSignatur; // restore original signature
    }
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void frIni(boolean initial) {
    if (drvSignatur == null) {
      this.fahrtenheft.setText("");
      this.efTeilnehmernr.setText("");
      this.efVorname.setText("");
      this.efNachname.setText("");
      this.efJahrgang.setText("");
      this.efAnzAbzeichen.setText("");
      this.efGesKm.setText("");
      this.efAnzAbzeichenAB.setText("");
      this.efGesKmAB.setText("");
      this.efJahr.setText("");
      this.efLetztKm.setText("");
      this.efSigDatum.setText("");
      this.efVersion.setText("");
      this.efKeyNr.setText("");
      this.efSignatur.setText("");
      setSignatureValid(null);
      if (initial) editMeldungButton_actionPerformed(null);
    } else {
      this.fahrtenheft.setText(drvSignatur.toString());
      this.efTeilnehmernr.setText(drvSignatur.getTeilnNr());
      this.efVorname.setText(drvSignatur.getVorname());
      this.efNachname.setText(drvSignatur.getNachname());
      this.efJahrgang.setText(drvSignatur.getJahrgang());
      this.efAnzAbzeichen.setText(Integer.toString(drvSignatur.getAnzAbzeichen()));
      this.efGesKm.setText(Integer.toString(drvSignatur.getGesKm()));
      this.efAnzAbzeichenAB.setText(Integer.toString(drvSignatur.getAnzAbzeichenAB()));
      this.efGesKmAB.setText(Integer.toString(drvSignatur.getGesKmAB()));
      this.efJahr.setText(Integer.toString(drvSignatur.getJahr()));
      if (drvSignatur.getVersion()<3) this.efLetztKm.setText("- Erst ab FA-Version 3 -");
      else this.efLetztKm.setText(Integer.toString(drvSignatur.getLetzteKm()));
      this.efSigDatum.setText(drvSignatur.getSignaturDatum(true));
      this.efVersion.setText(Byte.toString(drvSignatur.getVersion()));
      this.efKeyNr.setText(drvSignatur.getKeyName());
      this.efSignatur.setText(drvSignatur.getSignaturString());
      setSignatureValid(drvSignatur);
      if (initial) this.editMeldungButton.requestFocus();
    }
    if (initial) EfaUtil.pack(this);
  }

  public void setCloseButtonText(String s) {
    this.closeButton.setText(s);
  }

  void setSignatureValid(DRVSignatur drvSignatur) {
    int state = (drvSignatur == null ? -1 : drvSignatur.getSignatureState());
    switch(state) {
      case -1:
        this.signatureValidLabel.setText("");
        break;
      case DRVSignatur.SIG_VALID:
        this.signatureValidLabel.setText("Das Fahrtenheft ist gültig!");
        this.signatureValidLabel.setForeground(Daten.colorGreen);
        break;
      case DRVSignatur.SIG_INVALID:
        this.signatureValidLabel.setText("Das Fahrtenheft ist ungültig!");
        this.signatureValidLabel.setForeground(Color.red);
        break;
      case DRVSignatur.SIG_UNKNOWN_KEY:
        this.signatureValidLabel.setText("Das Fahrtenheft kann nicht geprüft werden: Unbekannter Schlüssel!");
        this.signatureValidLabel.setForeground(Color.red);
        break;
      case DRVSignatur.SIG_UNKNOWN_VERSION:
        this.signatureValidLabel.setText("Das Fahrtenheft kann nicht geprüft werden: Unbekannte Version!");
        this.signatureValidLabel.setForeground(Color.red);
        break;
      case DRVSignatur.SIG_INCOMPLETE:
        this.signatureValidLabel.setText("Das Fahrtenheft ist unvollständig!");
        this.signatureValidLabel.setForeground(Color.red);
        break;
      case DRVSignatur.SIG_KEY_NOT_VALID_FOR_YEAR:
        this.signatureValidLabel.setText("Das Fahrtenheft ist ungültig: Schlüssel ist für "+drvSignatur.getJahr()+" nicht gültig!");
        this.signatureValidLabel.setForeground(Color.red);
        break;
      case DRVSignatur.SIG_KEY_NOT_VALID_ON_SIGDATE:
        this.signatureValidLabel.setText("Das Fahrtenheft ist ungültig: Schlüssel war zum Erstellungszeitpunkt ungültig!");
        this.signatureValidLabel.setForeground(Color.red);
        break;
      case DRVSignatur.SIG_ERROR:
        this.signatureValidLabel.setText("Beim Überprüfen des Fahrtenhefts trat ein Fehler auf: "+drvSignatur.getSignatureError());
        this.signatureValidLabel.setForeground(Color.red);
        break;
    }
    if (state == DRVSignatur.SIG_UNKNOWN_KEY) {
      if (downloadKey(drvSignatur.getKeyName())) {
        drvSignatur.checkSignature();
        frIni(false);
      }
    }
  }

  public static boolean downloadKey(String keyname) {
      if (Daten.keyStore == null) return false;
      if (Daten.javaVersion.startsWith("1.3")) {
        Dialog.error("Diese Funktionalität steht erst ab Java Version 1.4\n"+
                     "zur Verfügung. Bitte installiere eine aktuelle Java-Version.");
        return false;
      }
      String keyfile = null;
      switch(Dialog.auswahlDialog("Unbekannter Schlüssel",
                                  "Um die Signatur zu prüfen, benötigt efa den öffentlichen Schlüssel '"+keyname+"'.\n"+
                                  "efa kann diesen Schlüssel aus dem Internet herunterladen oder ihn\n"+
                                  "aus einer zuvor heruntergeladenen Datei einlesen.\n"+
                                  "Was möchtest Du tun?",
                                  "Schlüssel aus Internet herunterladen","Schlüssel aus Datei einlesen")) {
        case 0:
          String localFile = Daten.efaTmpDirectory+keyname+".cert";
          if (Daten.wettDefs == null || Daten.wettDefs.efw_drv_url_pubkeys == null ||
              Daten.wettDefs.efw_drv_url_pubkeys.length() == 0) {
            Dialog.error("Es ist keine Adresse zum Abrufen des Schlüssels konfiguriert.\n"+
                         "Bitte öffne im Menü 'Administration' den Punkt 'Wettbewerbskonfiguration'\n"+
                         "und aktualisiere die Konfigurationsdaten.");
            return false;
          }
          if (!Dialog.okAbbrDialog("Internet-Verbindung herstellen",
                                   "Bitte stelle nun eine Verbindung mit dem Internet her und klicke OK.")) return false;
          String remoteFile = Daten.wettDefs.efw_drv_url_pubkeys+"/"+keyname+".cert";
          JDialog parent = null;
          try {
            parent = (JDialog)Dialog.frameCurrent();
          } catch(Exception eee) {}
          if (!EfaUtil.getFile(parent,remoteFile,localFile,true) || !EfaUtil.canOpenFile(localFile)) {
            Dialog.error("Der Schlüssel konnte nicht heruntergeladen werden.");
            return false;
          }
          keyfile = localFile;
          break;
        case 1:
          keyfile = Dialog.dateiDialog(Dialog.frameCurrent(),"Öffentlichen Schlüssel auswählen",
                             "Öffentlicher Schlüssel (*.cert)","cert",Daten.efaMainDirectory,false);
          if (keyfile == null) return false;
          break;
        default:
          return false;
      }
      if (!EfaUtil.canOpenFile(keyfile)) return false;
      if (importKey(keyfile)) {
        EfaUtil.deleteFile(keyfile);
        return true;
      }
      return false;
  }

  public static boolean importKey(String keyfile) {
    try {
      InputStream inStream = new FileInputStream(keyfile);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
      inStream.close();
      String alias = CertInfos.getAliasName(cert);
      if (alias.endsWith(".cert")) alias = alias.substring(0,alias.length()-5);
      if (alias.length()==0) {
        Dialog.error("Der Dateiname des öffentlichen Schlüssels ist ungültig.");
        return false;
      }
      if (!Daten.keyStore.addCertificate(alias,cert)) {
        Dialog.error("Fehler beim Hinzufügen des Schlüssels: "+Daten.keyStore.getLastError());
        return false;
      }

      String info = CertInfos.getCertInfos(cert,keyfile);
      if (info != null && info.length() > 0) {
        Dialog.infoDialog("Schlüssel importiert",
                          "Der Schlüssel '"+alias+"' wurde erfolgreich importiert.\n"+
                          "\nZertifikatdaten:\n"+info);
      }

    } catch(Exception e) {
      Dialog.error("Fehler beim Hinzufügen des Schlüssels: "+e.toString());
      return false;
    }
    return true;
  }

  void editMeldungButton_actionPerformed(ActionEvent e) {
    this.hinweisLabel.setText("Bitte gib das vom DRV signierte elektronische Fahrtenheft vollständig ein, z.B.");
    this.hinweisLabelBsp.setText("12345678;Manfred;Mustermann;23.05.1966;12;54321;2004;AQEwL AIUfy ...");
    this.hinweisLabelAchtung.setText("Achtung: Es muß unbedingt jedes Zeichen exakt wie angegeben eingegeben werden!");
    this.fahrtenheft.setEditable(true);
    EfaUtil.pack(this);
    this.fahrtenheft.requestFocus();
  }

  void fahrtenheft_focusLost(FocusEvent e) {
    setFahrtenheft(fahrtenheft.getText().trim());
  }

  void fahrtenheftKeyTyped(KeyEvent e) {
    if (e.getKeyChar() == '\n') setFahrtenheft(fahrtenheft.getText().trim());
  }

  void setFahrtenheft(String s) {
    try {
      if (s.length() == 0) drvSignatur = null;
      else drvSignatur = new DRVSignatur(s);
    } catch(Exception e) {
      drvSignatur = null;
    }
    this.hinweisLabel.setText("");
    this.hinweisLabelBsp.setText("");
    this.hinweisLabelAchtung.setText("");
    this.fahrtenheft.setEditable(false);
    this.fahrtenheft.setText( (this.drvSignatur != null ? this.drvSignatur.toString() : "") );
    frIni(false);
    this.closeButton.requestFocus();
  }

  void printButton_actionPerformed(ActionEvent e) {
    if (drvSignatur == null) {
      Dialog.error("Kein elektronisches Fahrtenheft vorhanden!");
      return;
    }
    String tmpdatei = Daten.efaTmpDirectory+"eFahrtenheft.html";
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpdatei),Daten.ENCODING));
      f.write("<html>\n");
      f.write("<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head>\n");
      f.write("<body>\n");
      f.write("<h1 align=\"center\">elektronisches Fahrtenheft<br>für "+drvSignatur.getVorname()+" "+drvSignatur.getNachname()+"</h1>\n");
      f.write("<table align=\"center\" border=\"3\" width=\"100%\">\n");
      f.write("<tr><td>DRV-Teilnehmernummer:</td><td><tt><b>"+drvSignatur.getTeilnNr()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Vorname:</td><td><tt><b>"+drvSignatur.getVorname()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Nachname:</td><td><tt><b>"+drvSignatur.getNachname()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Jahrgang:</td><td><tt><b>"+drvSignatur.getJahrgang()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Anzahl der Fahrtenabzeichen:</td><td><tt><b>"+drvSignatur.getAnzAbzeichen()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Insgesamt nachgewiesene Kilometer:</td><td><tt><b>"+drvSignatur.getGesKm()+"</b></tt></td></tr>\n");
      f.write("<tr><td>... davon Fahrtenabzeichen Jugend A/B:</td><td><tt><b>"+drvSignatur.getAnzAbzeichenAB()+"</b></tt></td></tr>\n");
      f.write("<tr><td>... davon Kilometer Jugend A/B:</td><td><tt><b>"+drvSignatur.getGesKmAB()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Jahr der letzten elektronischen Meldung:</td><td><tt><b>"+drvSignatur.getJahr()+"</b></tt></td></tr>\n");
      if (drvSignatur.getVersion() >= 3) f.write("<tr><td>Kilometer bei letzter elektronischer Meldung:</td><td><tt><b>"+drvSignatur.getLetzteKm()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Ausstellungsdatum des Fahrtenhefts:</td><td><tt><b>"+drvSignatur.getSignaturDatum(true)+"</b></tt></td></tr>\n");
      f.write("<tr><td>Fahrtenabzeichen-Version:</td><td><tt><b>"+drvSignatur.getVersion()+"</b></tt></td></tr>\n");
      f.write("<tr><td>öffentlicher DRV-Schlüssel:</td><td><tt><b>"+drvSignatur.getKeyName()+"</b></tt></td></tr>\n");
      f.write("<tr><td>DRV-Signatur:</td><td><tt><b>"+drvSignatur.getSignaturString()+"</b></tt></td></tr>\n");
      f.write("<tr><td>elektronisches Fahrtenheft (zur Eingabe):</td><td><tt><b>"+drvSignatur.toString()+"</b></tt></td></tr>\n");
      if (signatureValidLabel.getForeground() == Color.red)
        f.write("<tr><td colspan=\"2\"><font color=\"red\"><b>"+signatureValidLabel.getText()+"</b></font></td></tr>\n");
      f.write("</table>\n");
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

  void closeButton_actionPerformed(ActionEvent e) {
    cancel(true);
  }

  public static DRVSignatur showDlg(JDialog _parent, DRVSignatur _drvSignatur) {
    DRVSignaturFrame dlg = new DRVSignaturFrame(_parent,_drvSignatur);
    Dialog.setDlgLocation(dlg,_parent);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    return drvSignatur;
  }

  public static boolean getSignierteFahrtenhefte() {
    String localFile = null;
    switch (Dialog.auswahlDialog("Bestätigungsdatei abrufen",
                                 "Eine 'Bestätigungsdatei' wird jedes Jahr vom DRV nach der Bearbeitung der Meldung zum\n"+
                                 "DRV-Fahrtenabzeichen erstellt und enthält die in diesem Jahr vom DRV signierten Fahrtenhefte.\n"+
                                 "Damit efa die Nachweise der elektronisch erbrachten Fahrtenabzeichen erhält, müssen die\n"+
                                 "Bestätigungsdateien nach der Bearbeitung durch den DRV in efa eingespielt werden.\n"+
                                 "\n"+
                                 "efa kann die Bestätigungsdateien selbst aus dem Internet herunterladen\n"+
                                 "oder eine bereits heruntergeladene Bestätigungsdatei einlesen.\n"+
                                 "Was möchtest Du tun?",
                                 "Bestätigungsdatei aus Internet herunterladen","vorhandene Bestätigungsdatei einlesen")) {
      case 0:
        if (Daten.vereinsConfig == null) return false;

        // erstmal alle Quittungsnummern abfragen
        String request = Daten.vereinsConfig.makeScriptRequestString(VereinsConfig.VERBAND_DRV,VereinsConfig.ACTION_QNRLIST,null,null);
        if (request == null) {
          Dialog.error("Es konnten keine Bestätigungsdateien heruntergeladen werden!");
          return false;
        }
        if (!Dialog.okAbbrDialog("Verbindung mit Internet herstellen",
                                 "Bitte stelle nun eine Verbindung mit dem Internet her und klicke OK.")) return false;
        localFile = Daten.efaTmpDirectory+"drvSigFahrtenhefte.qnrlist";
        if (!EfaUtil.getFile((JDialog)Dialog.frameCurrent(),request,localFile,true)) {
          Dialog.error("Es konnten keine Bestätigungsdateien heruntergeladen werden!");
          return false;
        }
        try {
          BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(localFile),Daten.ENCODING));
          String s;
          String[] qnr = null;
          String[] wett = null;
          while ( (s = f.readLine()) != null) {
            if (s.startsWith("ERROR")) {
              Dialog.error(s);
              return false;
            }
            if (s.startsWith("QNR=")) {
              qnr = EfaUtil.kommaList2Arr(s.substring(4),';');
            }
            if (s.startsWith("WETT=")) {
              wett = EfaUtil.kommaList2Arr(s.substring(5),';');
            }
          }
          f.close();
          EfaUtil.deleteFile(localFile);
          if (qnr == null || wett == null) {
            Dialog.error("Es konnten keine Bestätigungsdateien gefunden werden!\n"+
                         "Möglicherweise sind noch keine Meldungen eingesandt oder durch den DRV bearbeitet worden.");
            return false;
          }
          return downloadFahrtenhefte(qnr,wett,null);
        } catch(Exception e) {
          Dialog.error("Die heruntergeladene Liste der Quittungsnummern konnte nicht geöffnet werden: "+e.toString());
          return false;
        }

      case 1:
        localFile = Dialog.dateiDialog(Dialog.frameCurrent(),"Bestätigungsdatei auswählen",
                           "Bestätigungsdatei (*.efwsig)","efwsig",Daten.efaMainDirectory,false);
        if (localFile == null) return false;
        return downloadFahrtenhefte(null,null,localFile);
      default:
        return false;
    }
  }

  private static boolean downloadFahrtenhefte(String[] qnr, String[] wett, String localFile) {
    meldungEingespielteFahrtenhefte = "";

    boolean success = true;
    if (localFile != null) {
      if (!fahrtenhefteEinspielen(localFile)) success = false;
    }
    if (qnr != null && wett != null) {
      if (qnr.length == 1) {
        if (!downloadFahrtenhefte(qnr[0])) success = false;
      } else {
        int firstYear = 9999;
        int lastYear = 0;
        for (int i = 0; i < qnr.length && i < wett.length; i++) {
          int y = EfaUtil.string2date(wett[i],-1,0,0).tag;
          if (y>-1 && y < firstYear) firstYear = y;
          if (y>-1 && y > lastYear) lastYear = y;
        }
        switch(Dialog.auswahlDialog("Bestätigungsdateien herunterladen",
                                    "Es wurden Bestätigungsdateien der Jahre "+firstYear+" - "+lastYear+" gefunden.",
                                    "alle Bestätigungsdateien herunterladen","nur neueste Bestätiungsdatei herunterladen",true)) {
          case 0:
            for (int i = 0; i < qnr.length && i < wett.length && qnr[i].length()>0; i++) {
              if (!downloadFahrtenhefte(qnr[i])) success = false;
            }
            break;
          case 1:
            if (!downloadFahrtenhefte(null)) success = false;
            break;
          default:
            success = false;
        }
      }
    }
    Dialog.infoDialog("Bestätigungsdateien eingespielt",meldungEingespielteFahrtenhefte);
    return success;
  }

  private static boolean downloadFahrtenhefte(String qnr) {
    Daten.vereinsConfig.reusePasswordForNextRequest();
    String request = Daten.vereinsConfig.makeScriptRequestString(VereinsConfig.VERBAND_DRV,VereinsConfig.ACTION_ABRUFEN,(qnr != null ? "qnr="+qnr : null),null);
    if (request == null) {
      Dialog.error("Die Bestätigungsdatei konnten nicht heruntergeladen werden!");
      return false;
    }
    String localFile = Daten.efaTmpDirectory+"drvSigFahrtenhefte.efwsig";
    if (!EfaUtil.getFile((JDialog)Dialog.frameCurrent(),request,localFile,true)) {
      Dialog.error("Die Bestätigungsdatei konnte nicht heruntergeladen werden!");
      return false;
    }
    try {
      BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(localFile),Daten.ENCODING));
      String s = f.readLine();
      if (s != null && s.startsWith("ERROR")) {
        Dialog.error(s);
        return false;
      }
      f.close();
    } catch(Exception e) {
      Dialog.error("Die heruntergeladene Bestätigungsdatei konnte nicht geöffnet werden: "+e.toString());
      return false;
    }
    boolean success = fahrtenhefteEinspielen(localFile);
    EfaUtil.deleteFile(localFile);
    return success;
  }

  private static boolean fahrtenhefteEinspielen(String localFile) {
    try {
      ESigFahrtenhefte sigfile = new ESigFahrtenhefte(localFile);
      sigfile.readFile();
      Vector fahrtenhefte = sigfile.getFahrtenhefte();
      if (Daten.fahrtenabzeichen == null) {
        Dialog.error("Es ist keine Fahrtenabzeichendatei vorhanden!");
        return false;
      }

      // ggf. Schlüssel importieren
      if (Daten.keyStore != null && sigfile.keyName != null && sigfile.keyDataBase64 != null) {
        if (Daten.keyStore.getCertificate(sigfile.keyName) == null) {
          byte[] data = Base64.decode(sigfile.keyDataBase64);
          String certfile = Daten.efaTmpDirectory+sigfile.keyName+".cert";
          try {
            FileOutputStream fout = new FileOutputStream(certfile);
            fout.write(data);
            fout.close();
            importKey(certfile);
          } catch(Exception ee) {
            Dialog.error("Fehler beim Importieren des Schlüssels "+sigfile.keyName+": "+ee.toString());
          }
        }
      }

      int sigError = 0;
      int sigErrorUnknownKey = 0;
      String keyname = null;
      for (int i=0; i<fahrtenhefte.size(); i++) {
        DRVSignatur sig = (DRVSignatur)fahrtenhefte.get(i);
        DatenFelder d = Daten.fahrtenabzeichen.getExactComplete(sig.getVorname()+" "+sig.getNachname());
        if (d != null) {
          d.set(Fahrtenabzeichen.LETZTEMELDUNG,sig.toString());
          d.set(Fahrtenabzeichen.ANZABZEICHEN,Integer.toString(sig.getAnzAbzeichen()));
          d.set(Fahrtenabzeichen.GESKM,Integer.toString(sig.getGesKm()));
          d.set(Fahrtenabzeichen.ANZABZEICHENAB,Integer.toString(sig.getAnzAbzeichenAB()));
          d.set(Fahrtenabzeichen.GESKMAB,Integer.toString(sig.getGesKmAB()));
        } else {
          d = new DatenFelder(Fahrtenabzeichen._ANZ);
          d.set(Fahrtenabzeichen.VORNAME,sig.getVorname());
          d.set(Fahrtenabzeichen.NACHNAME,sig.getNachname());
          d.set(Fahrtenabzeichen.JAHRGANG,sig.getJahrgang());
          d.set(Fahrtenabzeichen.LETZTEMELDUNG,sig.toString());
          d.set(Fahrtenabzeichen.ANZABZEICHEN,Integer.toString(sig.getAnzAbzeichen()));
          d.set(Fahrtenabzeichen.GESKM,Integer.toString(sig.getGesKm()));
          d.set(Fahrtenabzeichen.ANZABZEICHENAB,Integer.toString(sig.getAnzAbzeichenAB()));
          d.set(Fahrtenabzeichen.GESKMAB,Integer.toString(sig.getGesKmAB()));
          Daten.fahrtenabzeichen.add(d);
        }
        sig.checkSignature();
        switch(sig.getSignatureState()) {
          case DRVSignatur.SIG_VALID:
            // nothing to do
            break;
          case DRVSignatur.SIG_UNKNOWN_KEY:
            sigError++;
            sigErrorUnknownKey++;
            keyname = sig.getKeyName();
            break;
          default:
            sigError++;
            break;
        }
      }
      String sigGueltigInfo;
      if (sigError == 0) {
        sigGueltigInfo = "alle Fahrtenhefte sind gültig";
      } else {
        sigGueltigInfo = sigErrorUnknownKey + " Fahrtenhefte konnten nicht geprüft werden, da der Schlüssel unbekannt ist";
        if (sigError > sigErrorUnknownKey) {
          sigGueltigInfo += "\n" + sigError + " Fahrtenhefte sind ungültig";
        }
      }
      if (Daten.fahrtenabzeichen.writeFile()) {
        meldungEingespielteFahrtenhefte += (meldungEingespielteFahrtenhefte.length()>0 ? "\n" : "") +
                                           "Quittungsnummer "+sigfile.quittungsnr+": "+fahrtenhefte.size()+" signierte Fahrtenhefte eingespielt ("+sigGueltigInfo+").";
        if (sigErrorUnknownKey>0) {
          downloadKey(keyname);
        }
      } else {
        Dialog.error("Das Speichern der elektronischen Fahrtenabzeichen ist fehlgeschlagen.");
        return false;
      }
    } catch(Exception e) {
      Dialog.error("Fehler beim Lesen der Bestätigungsdatei: "+e.toString());
      return false;
    }
    return true;
  }

}
