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
import de.nmichael.efa.core.Boote;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

// @i18n complete

public class FahrtenbuchNeuFortsetzenFrame extends JDialog implements ActionListener {
  EfaFrame efaframe = null;
  int count = 0;
  boolean erstelleVerknuepfungenZwischenFbs;
  BorderLayout borderLayout1 = new BorderLayout();
  JTextArea erklaerung = new JTextArea();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton neuButton = new JButton();
  JButton FortsetzenButton = new JButton();
  JButton weiterButton = new JButton();
  JLabel dateiLabel = new JLabel();
  JTextField dateiEfb = new JTextField();
  JButton saveButton = new JButton();
  JButton openButton = new JButton();
  Fahrtenbuch altesFb = null, neuesFb = null;


  public FahrtenbuchNeuFortsetzenFrame(EfaFrame efaframe) {
    super(efaframe);
    this.efaframe = efaframe;
    construct(true);
  }

  public FahrtenbuchNeuFortsetzenFrame(JDialog parent, boolean erstelleVerknuepfungenZwischenFbs) {
    super(parent);
    construct(erstelleVerknuepfungenZwischenFbs);
  }
  public FahrtenbuchNeuFortsetzenFrame(JFrame parent, boolean erstelleVerknuepfungenZwischenFbs) {
    super(parent);
    construct(erstelleVerknuepfungenZwischenFbs);
  }

  void construct(boolean erstelleVerknuepfungenZwischenFbs) {
    this.erstelleVerknuepfungenZwischenFbs = erstelleVerknuepfungenZwischenFbs;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    FortsetzenButton.requestFocus();
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
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    neuButton.setNextFocusableComponent(FortsetzenButton);
    Dialog.setPreferredSize(neuButton,620,25);

    Mnemonics.setButton(this, neuButton, International.getStringWithMnemonic("Neues, unabhängiges Fahrtenbuch mit leere Datenlisten (leere Mitglieder-/Boots-/Ziellisten)"));
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neuButton_actionPerformed(e);
      }
    });
    FortsetzenButton.setNextFocusableComponent(neuButton);
    Dialog.setPreferredSize(FortsetzenButton,620,25);
    Mnemonics.setButton(this, FortsetzenButton, International.getStringWithMnemonic("Neues Fahrtenbuch als Fortsetzung des aktuellen Fahrtenbuchs (z.B. bei Jahreswechsel)"));
    FortsetzenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FortsetzenButton_actionPerformed(e);
      }
    });
    jPanel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jPanel1.setMinimumSize(new Dimension(700, 170));
    jPanel1.setPreferredSize(new Dimension(700, 170));
    Mnemonics.setButton(this, weiterButton, International.getStringWithMnemonic("Weiter"));
    weiterButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        weiterButton_actionPerformed(e);
      }
    });
    Mnemonics.setLabel(this, dateiLabel, International.getStringWithMnemonic("Dateiname")+": ");
    dateiLabel.setLabelFor(dateiEfb);
    Dialog.setPreferredSize(dateiEfb,300,19);
    Dialog.setPreferredSize(saveButton,59,25);
    saveButton.setIcon(new ImageIcon(FahrtenbuchNeuFortsetzenFrame.class.getResource("/de/nmichael/efa/img/prog_save.gif")));
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveButton_actionPerformed(e);
      }
    });
    Dialog.setPreferredSize(openButton,59,25);
    openButton.setIcon(new ImageIcon(FahrtenbuchNeuFortsetzenFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    openButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(erklaerung, BorderLayout.NORTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(neuButton,  new GridBagConstraints(1, 2, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(FortsetzenButton,  new GridBagConstraints(1, 3, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(weiterButton, new GridBagConstraints(1, 4, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(dateiLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(dateiEfb, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(saveButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(openButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    erklaerung.setMargin(new Insets(10, 10, 10, 10));
    erklaerung.setBackground(UIManager.getColor("control"));
    erklaerung.setEditable(false);
    erklaerung.setFont(new java.awt.Font("Dialog", 1, 12));
    erklaerung.append(Dialog.chopDialogString(
            International.getString("Soll ein neues, unabhängiges Fahrtenbuch (mit neuen, leeren Datenlisten) "+
                                    "erstellt werden, das keinen Bezug zu bestehenden Fahrtenbüchern hat? "+
                                     "Oder soll ein neues Fahrtenbuch erstellt werden, das ein bestehendes Fahrtenbuch "+
                                     "fortsetzt und die existierende Datenlisten übernimmt (z.B. beim Jahreswechsel)?")+"\n"));
    weiterButton.setVisible(false);
    dateiLabel.setVisible(false);
    dateiEfb.setVisible(false);
    saveButton.setVisible(false);
    openButton.setVisible(false);
    this.setTitle(International.getString("Neues Fahrenbuch erstellen oder fortsetzen"));
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  // Abbruch: Originaldateinamen wiederherstellen
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  // Auswahl: Neues Fahrtenbuch (leere Datenbanken)
  void neuButton_actionPerformed(ActionEvent e) {
    if (efaframe != null) efaframe.neuesFahrtenbuchDialog(true);
    else {
          NeuesFahrtenbuchFrame dlg = new NeuesFahrtenbuchFrame(this);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
    }
    cancel();
  }


  // Auswahl: Fahrtenbuch fortsetzen (Datenbanken übernehmen)
  void FortsetzenButton_actionPerformed(ActionEvent e) {
    erklaerungClear();
    erklaerung.append(International.getMessage("Schritt {n}",1)+":\n\n");
    erklaerung.append(
            International.getString("Bitte wähle jetzt ein bestehendes Fahrtenbuch aus, das "+
                                    "fortgesetzt werden soll! "+
                                    "Die Datenlisten aus diesem Fahrtenbuch werden dann automatisch übernommen."));
    neuButton.setVisible(false);
    FortsetzenButton.setVisible(false);
    openButton.setVisible(true);
    dateiEfb.setVisible(true);
    dateiLabel.setVisible(true);
    weiterButton.setVisible(true);
    if (Daten.fahrtenbuch != null) dateiEfb.setText(Daten.fahrtenbuch.getFileName());
    else dateiEfb.setText("");
  }


  // Erklärungstext löschen
  void erklaerungClear() {
    this.getContentPane().remove(erklaerung);
    erklaerung = new JTextArea();
    this.getContentPane().add(erklaerung, BorderLayout.NORTH);
    erklaerung.setMargin(new Insets(10, 10, 10, 10));
    erklaerung.setBackground(UIManager.getColor("control"));
    erklaerung.setEditable(false);
    erklaerung.setFont(new java.awt.Font("Dialog", 1, 12));
  }


  // Dateinamen aus s1 und s2 generieren, falls vorhanden ggf. Zahlen 1 bis 99 einfügen
  String createFilename(String s1, String s2) {
    String s;
    for(int i=0; i<100; i++) {
      if (i == 0) s = s1+s2;
      else s = s1+"_"+Integer.toString(i)+s2;
      if (EfaUtil.canOpenFile(s)) continue;
      return EfaUtil.makeRelativePath(s,neuesFb.getFileName());
    }
    return null;
  }


  // Weitermachen
  void weiterButton_actionPerformed(ActionEvent e) {
    switch(count) {
      case 0: // Quellfahrtenbuch auswählen
        if (EfaUtil.canOpenFile(dateiEfb.getText().trim())) {
          altesFb = new Fahrtenbuch(dateiEfb.getText().trim());
          altesFb.readFile();
          erklaerungClear();
          erklaerung.append(International.getMessage("Schritt {n}",2)+":\n\n");
          erklaerung.append(International.getString("Folgende Datenlisten werden übernommen:")+"\n");
          erklaerung.append(International.getString("Mitgliederliste")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(altesFb.getFileName()),altesFb.getDaten().mitgliederDatei)+"\n");
          erklaerung.append(International.getString("Bootsliste")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(altesFb.getFileName()),altesFb.getDaten().bootDatei)+"\n");
          erklaerung.append(International.getString("Zielliste")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(altesFb.getFileName()),altesFb.getDaten().zieleDatei)+"\n");
          erklaerung.append(International.getString("Statistikeinstellungen")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(altesFb.getFileName()),altesFb.getDaten().statistikDatei)+"\n\n");
          erklaerung.append(International.getString("Gib nun den Dateinamen für das neue Fahrtenbuch an!"));
          openButton.setVisible(false);
          saveButton.setVisible(true);

          // intelligend das Jahr für das neue Fahrtenbuch berechnen aus dem Jahr des alten Fahrtenbuchs
          String neuesFbName = altesFb.getFileName();
          TMJ tmj = EfaUtil.string2date(altesFb.getFileName(),0,0,0);
          int jahr = -1;
          if (tmj.tag>=1980) jahr = tmj.tag;
          if (tmj.monat>=1980) jahr = tmj.monat;
          if (tmj.jahr>=1980) jahr = tmj.jahr;
          if (jahr>=1980) {
            neuesFbName = EfaUtil.replace(neuesFbName,Integer.toString(jahr),Integer.toString(jahr+1));
          }

          dateiEfb.setText(neuesFbName);
        } else {
          Dialog.infoDialog(International.getString("Fehler"),
                  LogString.logstring_fileOpenFailed(dateiEfb.getText().trim(), International.getString("Fahrtenbuch")));
          dateiEfb.requestFocus();
          return;
        }
        count++;
        break;
      case 1: // Zielfahrtenbuch vorbereiten
        String s = dateiEfb.getText().trim();
        if (s.toUpperCase().indexOf(".EFB") < 0) s = s + ".efb";
        neuesFb = new Fahrtenbuch(s);
        if (EfaUtil.canOpenFile(neuesFb.getFileName())) {
          if (!(Dialog.yesNoDialog(International.getString("Warnung"),
                  LogString.logstring_fileAlreadyExists(neuesFb.getFileName(), International.getString("Neues Fahrtenbuch")) + "\n" +
                  International.getString("Soll die Datei überschrieben werden?"))
                  == Dialog.YES)) return;
        }
        FBDaten neu = new FBDaten(altesFb.getDaten());
        neu.mitgliederDatei = createFilename(neuesFb.getFileName().substring(0,neuesFb.getFileName().toUpperCase().lastIndexOf(".EFB")),".efbm");
        neu.bootDatei = createFilename(neuesFb.getFileName().substring(0,neuesFb.getFileName().toUpperCase().lastIndexOf(".EFB")),".efbb");
        neu.zieleDatei = createFilename(neuesFb.getFileName().substring(0,neuesFb.getFileName().toUpperCase().lastIndexOf(".EFB")),".efbz");
        neu.statistikDatei = createFilename(neuesFb.getFileName().substring(0,neuesFb.getFileName().toUpperCase().lastIndexOf(".EFB")),".efbs");
        neuesFb.setDaten(neu);
        erklaerungClear();
        erklaerung.append(International.getMessage("Schritt {n}",3)+":\n\n");
        erklaerung.append(International.getString("Folgende Datenlisten werden neu erstellt:")+"\n");
        erklaerung.append(International.getString("Mitgliederliste")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neu.mitgliederDatei)+"\n");
        erklaerung.append(International.getString("Bootsliste")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neu.bootDatei)+"\n");
        erklaerung.append(International.getString("Zielliste")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neu.zieleDatei)+"\n");
        erklaerung.append(International.getString("Statistikeinstellungen")+": "+EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neu.statistikDatei)+"\n\n");
        erklaerung.append(International.getString("Klicke Weiter, um Fahrtenbuch und Datenlisten zu erstellen!"));
        dateiLabel.setVisible(false);
        dateiEfb.setVisible(false);
        saveButton.setVisible(false);
        count++;
        break;
      case 2: // alles vorbereitet: Jetzt Fahrtenbuchdateien erstellen
          String anzmitgl = Dialog.inputDialog(International.getString("Mitgliederzahl"),
                  International.getString("Anzahl der Mitglieder am 01.01. des Jahres?"));
          if (anzmitgl == null) return;
          neuesFb.getDaten().anzMitglieder = EfaUtil.string2date(anzmitgl,0,0,0).tag;
          // folgende vier Zeilen werden benötigt, wenn aus irgendeinem Grund das Einlesen der Datenliste fehlschlug
          if (neuesFb.getDaten().boote == null) neuesFb.getDaten().boote = new Boote(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().bootDatei));
          if (neuesFb.getDaten().mitglieder == null) neuesFb.getDaten().mitglieder = new Mitglieder(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().mitgliederDatei));
          if (neuesFb.getDaten().ziele == null) neuesFb.getDaten().ziele = new Ziele(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().zieleDatei));
          if (neuesFb.getDaten().statistik == null) neuesFb.getDaten().statistik = new StatSave(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().statistikDatei));

          neuesFb.getDaten().boote.setFileName(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().bootDatei));
          neuesFb.getDaten().mitglieder.setFileName(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().mitgliederDatei));
          neuesFb.getDaten().ziele.setFileName(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().zieleDatei));
          neuesFb.getDaten().statistik.setFileName(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(neuesFb.getFileName()),neuesFb.getDaten().statistikDatei));
          if (neuesFb.getDaten().boote.writeFile() && neuesFb.getDaten().mitglieder.writeFile() &&
              neuesFb.getDaten().ziele.writeFile() && neuesFb.getDaten().statistik.writeFile()) {
            if (erstelleVerknuepfungenZwischenFbs) {
              if (altesFb.getNextFb(false).equals("")) { // Verweis auf neues FB setzen
                altesFb.setNextFb(EfaUtil.makeRelativePath(neuesFb.getFileName(),altesFb.getFileName()));
                altesFb.writeFile();
              } else Dialog.infoDialog(International.getString("Warnung"),
                      International.getMessage("Das ursprüngliche Fahrtenbuch {oldLogbook} "+
                                               "enthält bereits einen Verweis auf {nextLogbookFromOld} "+
                                               "als '{nextLogbook}'. Dies führt zu Problemen bei der fahrtenbuchübergreifenden "+
                                               "Auswertung. Um den Verweis zu ändern, öffne im ursprünglichen Fahrtenbuch die "+
                                               "'{LogbookSettings}'.",altesFb.getFileName(),altesFb.getNextFb(false),
                                               International.getString("nächstes Fahrtenbuch"),International.getString("Einstellungen zum Fahrtenbuch")));
              neuesFb.setPrevFb(EfaUtil.makeRelativePath(altesFb.getFileName(),neuesFb.getFileName()));  // Verweis auf altes FB
            } else neuesFb.setPrevFb("");
            neuesFb.setNextFb("");
            neuesFb.writeFile();
            Daten.fahrtenbuch = neuesFb;
            Daten.fahrtenbuch.writeFile();
            Daten.fahrtenbuch.readFile();
            if (efaframe != null) efaframe.neuesFahrtenbuch();
            cancel();
          } else return;
        break;
    }
  }


  // Dateiauswahl
  void saveButton_actionPerformed(ActionEvent e) {
    String dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Fahrtenbuch")),
            International.getString("Fahrtenbuch")+" (*.efb)","efb",altesFb.getFileName(),true);
    if (dat != null) {
      dateiEfb.setText(dat);
    }
  }
  void openButton_actionPerformed(ActionEvent e) {
    String dat;
    if (!dateiEfb.getText().trim().equals("")) dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Fahrtenbuch")),
            International.getString("Fahrtenbuch")+" (*.efb)","efb",dateiEfb.getText().trim(),false);
    else dat = Dialog.dateiDialog(this,
            International.getMessage("{item} auswählen",
            International.getString("Fahrtenbuch")),
            International.getString("Fahrtenbuch")+" (*.efb)","efb",null,false);
    if (dat != null) {
      dateiEfb.setText(dat);
    }
  }


}
