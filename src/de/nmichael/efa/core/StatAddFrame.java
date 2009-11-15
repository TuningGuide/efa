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
import de.nmichael.efa.statistics.StatistikDaten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class StatAddFrame extends JDialog implements ActionListener {

  public static final String DEFAULT = "%%DEFAULT%%"; // Standard-Statistikeinstellungen

  StatistikFrame statFrame;
  StatistikDaten d;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel nameLabel = new JLabel();
  JTextField name = new JTextField();
  JButton addButton = new JButton();
  JRadioButton defaultRadioButton = new JRadioButton();
  JCheckBox efaDirektButton = new JCheckBox();

  public StatAddFrame(StatistikFrame parent, StatistikDaten d, String statname, boolean auchInEfaDirekt) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      if (statname != null && statname.equals(DEFAULT)) statname = "";
      name.setText(statname);
      efaDirektButton.setSelected(auchInEfaDirekt);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);

    statFrame = parent;
    this.d = d;
    name.requestFocus();
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

    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, nameLabel, International.getStringWithMnemonic("Bezeichnung für aktuelle Einstellungen")+": ");
    nameLabel.setLabelFor(name);
    name.setMinimumSize(new Dimension(150, 19));
    name.setPreferredSize(new Dimension(150, 19));
    Mnemonics.setButton(this, addButton, International.getStringWithMnemonic("Statistikeinstellungen speichern"));
    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButton_actionPerformed(e);
      }
    });
    this.setTitle(International.getString("Statistikeinstellungen speichern"));
    Mnemonics.setButton(this, defaultRadioButton, International.getStringWithMnemonic("Einstellungen als Standardeinstellung speichern"));
    defaultRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        defaultRadioButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, efaDirektButton, International.getStringWithMnemonic("Statistik auch im Bootshaus verfügbar machen"));
    this.getContentPane().add(addButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(name, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(defaultRadioButton, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(efaDirektButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    name.requestFocus();
    name.setNextFocusableComponent(defaultRadioButton);
    defaultRadioButton.setNextFocusableComponent(efaDirektButton);
    efaDirektButton.setNextFocusableComponent(addButton);
    addButton.setNextFocusableComponent(name);
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


  // Statistikeinstellungen speichern
  void addButton_actionPerformed(ActionEvent e) {
    String t;
    String s = name.getText().trim();
    if (defaultRadioButton.isSelected()) s = DEFAULT;
    if (s.equals("")) return;
    if ( (t = Daten.fahrtenbuch.getDaten().statistik.getExact(s)) != null) {
      if (s.equals(DEFAULT) ||
          Dialog.yesNoDialog(International.getString("Bezeichnung bereits vergeben"),
          International.getString("Diese Bezeichnung ist bereits vergeben! Soll der alte Eintrag überschrieben werden?")
          ) == Dialog.YES) {
        if (!Daten.fahrtenbuch.getDaten().statistik.delete(t)) return;
      } else return;
    }

    statFrame.aktStatName = s;

    // NAMESTAT
    s = EfaUtil.removeSepFromString(s)+"|";

    // ART
    s = s+d.art+"|";

    // STAT
    s = s+d.stat+"|";

    // AUSGABEDATEI
    s = s+d.ausgabeDatei+"|";

    // AUSGABEOVERWRITE
    if (d.ausgabeOverwriteWarnung) s = s+"+|";
    else s = s+"-|";

    // AUSGABEART
    s = s+d.ausgabeArt+"|";

    // TABELLEHTML
    if (d.tabelleHTML) s = s+"+|";
    else s = s+"-|";

    // VON, BIS
    s = s+d.von.tag+"."+d.von.monat+"."+d.von.jahr+"|";
    s = s+d.bis.tag+"."+d.bis.monat+"."+d.bis.jahr+"|";

    // GESCHLECHT
    for (int i=0; i<d.geschlecht.length; i++)
      if (d.geschlecht[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // STATUS
    for (int i=0; i<d.status.length; i++)
      if (d.status[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // FAHRTART
    for (int i=0; i<d.fahrtart.length; i++)
      if (d.fahrtart[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // BART
    for (int i=0; i<d.bArt.length; i++)
      if (d.bArt[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // BANZAHL
    for (int i=0; i<d.bAnzahl.length; i++)
      if (d.bAnzahl[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // BRIGGER
    for (int i=0; i<d.bRigger.length; i++)
      if (d.bRigger[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // BSTM
    for (int i=0; i<d.bStm.length; i++)
      if (d.bStm[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // BVEREIN
    for (int i=0; i<d.bVerein.length; i++)
      if (d.bVerein[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // NAME
    s = s+d.name+"|";

    // NAMETEIL
    if (d.nameTeil) s = s+"+|";
    else s = s+"-|";

    // AUSGEBEN
    if (d.ausgebenNr) s = s+"+";
    else s = s+"-";
    if (d.ausgebenName) s = s+"+";
    else s = s+"-";
    if (d.ausgebenJahrgang) s = s+"+";
    else s = s+"-";
    if (d.ausgebenStatus) s = s+"+";
    else s = s+"-";
    if (d.ausgebenBezeichnung) s = s+"+";
    else s = s+"-";
    if (d.ausgebenArt) s = s+"+";
    else s = s+"-";
    if (d.ausgebenPlaetze) s = s+"+";
    else s = s+"-";
    if (d.ausgebenKm) s = s+"+";
    else s = s+"-";
    if (d.ausgebenRudKm) s = s+"+";
    else s = s+"-";
    if (d.ausgebenStmKm) s = s+"+";
    else s = s+"-";
    if (d.ausgebenFahrten) s = s+"+";
    else s = s+"-";
    if (d.ausgebenKmFahrt) s = s+"+";
    else s = s+"-";
    if (d.ausgebenZielfahrten) s = s+"+";
    else s = s+"-";
    if (d.ausgebenWWAnzVersch) s = s+"+";
    else s = s+"-";
    if (d.ausgebenDauer) s = s+"+";
    else s = s+"-";
    if (d.ausgebenKmH) s = s+"+";
    else s = s+"-";
    if (d.ausgebenXMLalle) s = s+"+"; // neu in v1.2.0
    else s = s+"-";
    if (d.ausgebenWafaKm) s = s+"+"; // neu in v1.3.5
    else s = s+"-";
    if (d.ausgebenMitglnrStattName) s = s+"+"; // neu in v1.7.0
    else s = s+"-";
    s = s+"|";

    // GRAPHISCH
    if (d.graphischKm) s = s+"+";
    else s = s+"-";
    if (d.graphischRudKm) s = s+"+";
    else s = s+"-";
    if (d.graphischStmKm) s = s+"+";
    else s = s+"-";
    if (d.graphischFahrten) s = s+"+";
    else s = s+"-";
    if (d.graphischKmFahrt) s = s+"+";
    else s = s+"-";
    if (d.graphischDauer) s = s+"+";
    else s = s+"-";
    if (d.graphischKmH) s = s+"+";
    else s = s+"-";
    s = s+"|";

    // NUMERIERE
    for (int i=0; i<d.numeriere.length; i++)
      if (d.numeriere[i]) s = s+"+";
      else s = s+"-";
    s = s+"|";

    // SORTIERKRITERIUM, SORTIERFOLGE
    s = s+d.sortierKriterium+'|';
    s = s+d.sortierFolge+'|';

    // SORTVORNACHNAME
    if (d.sortVorNachname) s = s+"+|";
    else s = s+"-|";

    // GRASIZEKM, GRASIZESTMKM, GRASIZERUDKM, GRASIZEFAHRTEN, GRASIZEKMFAHRT
    s = s+d.graSizeKm+'|';
    s = s+d.graSizeStmKm+'|';
    s = s+d.graSizeRudKm+'|';
    s = s+d.graSizeFahrten+'|';
    s = s+d.graSizeKmFahrt+'|';

    // ZUSAMMENADDIEREN
    if (d.zusammenAddieren) s = s+"+|";
    else s = s+"-|";

    // WW_OPTIONS
    if (d.ww_horiz_alle) s = s+"+|";
    else s = s+"-|";

    // AUCHNULLWERTE
    if (d.auchNullWerte)  s = s+"+|";
    else s = s+"-|";

    // KMFAHRT_GRUPPIERT
    if (d.kmfahrt_gruppiert) s = s+"+|";
    else s = s+"-|";

    // STYLESHEET
    if (d.stylesheet == null) s = s+"|";
    else s = s + EfaUtil.removeSepFromString(d.stylesheet) + "|";

    // ZIELEGRUPPIERT
    if (d.ziele_gruppiert) s = s+"+|";
    else s = s+"-|";

    // ZEITFBUEBERGREIFEND
    if (d.zeitFbUebergreifend) s = s+"+|";
    else s = s+"-|";

    // AUSWETTBEDINGUNGEN
    if (d.ausgebenWettBedingung) s = s+"+|";
    else s = s+"-|";

    // WETTPROZENT, WETTFAHRTEN
    s = s+d.wettProz+"|";
    s = s+d.wettFahrten+"|";

    // GAESTEALSEIN
    if (d.gasteAlsEinePerson) s = s+"+";
    else s = s+"-";
    if (d.gaesteVereinsweise) s = s+"+|";
    else s = s+"-|";

    // WETTOHNEDETAIL
    if (d.wettOhneDetail) s = s+"+|";
    else s = s+"-|";

    // WETTJAHR
    s = s+d.wettJahr+"|";

    // CROPTOMAXSIZE
    if (d.cropToMaxSize) s = s+"+|";
    else s = s+"-|";

    // MAXSIZEKM, MAXSIZERUDKM, MAXSIZESTMKM, MAXSIZEFAHRTEN, MAXSIZEKMFAHRT, MAXSIZEDAUER, MAXSIZEKMH
    s = s+d.maxSizeKm+'|';
    s = s+d.maxSizeStmKm+'|';
    s = s+d.maxSizeRudKm+'|';
    s = s+d.maxSizeFahrten+'|';
    s = s+d.maxSizeKmFahrt+'|';
    s = s+d.maxSizeDauer+'|';
    s = s+d.maxSizeKmH+'|';

    // GRASIZEDAUER, GRASIZEKMH
    s = s+d.graSizeDauer+'|';
    s = s+d.graSizeKmH+'|';

    // nur Bemerkung
    s = s+EfaUtil.removeSepFromString(d.nurBemerk)+"|";
    s = s+EfaUtil.removeSepFromString(d.nurBemerkNicht)+"|";

    // Zusatzwettbewerbe
    s = s+d.zusatzWett[0]+'|';
    s = s+d.zusatzWett[1]+'|';
    s = s+d.zusatzWett[2]+'|';
    s = s+d.zusatzWettjahr[0]+'|';
    s = s+d.zusatzWettjahr[1]+'|';
    s = s+d.zusatzWettjahr[2]+'|';
    if (d.zusatzWettMitAnforderung) s = s+"+|";
    else s = s+"-|";

    // nur Steg-Km?
    if (d.nurStegKm) s = s+"+|";
    else s = s+"-|";


    // ZEITVORJAHRESVERGLEICH
    if (d.vorjahresvergleich) s = s+"+|";
    else s = s+"-|";

    // nur MInd-Km
    s = s+d.nurMindKm+"|";

    // in efaDirekt verfügbar
    s = s + (efaDirektButton.isSelected() ? "+|" : "-|");

    // FAHRTENBUCHFELDER
    s = s + (d.fbLfdNr ? "+" : "-");
    s = s + (d.fbDatum ? "+" : "-");
    s = s + (d.fbBoot ? "+" : "-");
    s = s + (d.fbStm ? "+" : "-");
    s = s + (d.fbMannsch ? "+" : "-");
    s = s + (d.fbAbfahrt ? "+" : "-");
    s = s + (d.fbAnkunft ? "+" : "-");
    s = s + (d.fbZiel ? "+" : "-");
    s = s + (d.fbBootsKm ? "+" : "-");
    s = s + (d.fbMannschKm ? "+" : "-");
    s = s + (d.fbBemerkungen ? "+" : "-");
    s = s + (d.fbFahrtartInBemerkungen ? "+" : "-");
    s = s + (d.fbZielbereichInBemerkungen ? "+" : "-");
    s = s+"|";

    // CROPTOMAXSIZE
    if (d.zusammengefassteDatenOhneBalken) s = s+"+|";
    else s = s+"-|";

    // NAME_ODER_GRUPPE
    s = s+d.nameOderGruppe+"|";

    // FILE_EXEC BEFORE und AFTER
    s = s+d.fileExecBefore+"|";
    s = s+d.fileExecAfter+"|";

    // NUR_FB
    for (int i=0; d.nurFb != null && i<d.nurFb.length; i++) {
      s += d.nurFb[i] + (i+1 == d.nurFb.length ? "" : ";");
    }
    s += "|";

    // NURBOOTEFUERGRUPPE
    s += d.nurBooteFuerGruppe + "|";

    // ALLEZIELFAHRTEN
    if (d.alleZielfahrten)  s = s+"+|";
    else s = s+"-|";

    // NURGANZEKM
    if (d.nurGanzeKm) s = s+"+|";
    else s = s+"-|";

    Daten.fahrtenbuch.getDaten().statistik.add(s);
    if (Daten.fahrtenbuch.getDaten().statistik.writeFile() && Daten.fahrtenbuch.getDaten().statistik.readFile()) {
      EfaUtil.foo();
    } else {
      Dialog.infoDialog(International.getString("Fehler"),
              International.getString("Einstellungen konnten nicht gespeichert werden!"));
    }
    cancel();

  }

  // ggf. Name-Feld grau machen
  void defaultRadioButton_actionPerformed(ActionEvent e) {
    if (defaultRadioButton.isSelected()) {
      nameLabel.setEnabled(false);
      name.setEnabled(false);
      efaDirektButton.setEnabled(false);
      efaDirektButton.setSelected(false);
    } else {
      nameLabel.setEnabled(true);
      name.setEnabled(true);
      efaDirektButton.setEnabled(true);
    }
  }

/*
  public StatAddFrame() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        this_keyPressed(e);
      }
    });
  }
*/

}
