package de.nmichael.efa.drv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;
import java.util.Vector;


public class DRVAdminFrame extends JDialog implements ActionListener {
  Frame parent;
  DRVConfig drvConfig;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton wettJahrButton = new JButton();
  JButton configButton = new JButton();
  JButton closeButton = new JButton();
  JButton keysButton = new JButton();
  JButton updateButton = new JButton();
  JButton datensicherungButton = new JButton();


  public DRVAdminFrame(Frame parent, DRVConfig drvConfig) {
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
    this.drvConfig = drvConfig;
    // this.requestFocus();
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


  // Initialisierung des Frames
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      jPanel2.setLayout(gridBagLayout1);
      wettJahrButton.setNextFocusableComponent(configButton);
      wettJahrButton.setMnemonic('W');
      wettJahrButton.setText("Wettbewerbsjahr festlegen");
      wettJahrButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          wettJahrButton_actionPerformed(e);
        }
    });
      configButton.setNextFocusableComponent(keysButton);
      configButton.setMnemonic('K');
      configButton.setText("Konfiguration");
      configButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          configButton_actionPerformed(e);
        }
    });
      closeButton.setNextFocusableComponent(wettJahrButton);
      closeButton.setMnemonic('C');
      closeButton.setText("Schlie�en");
      closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeButton_actionPerformed(e);
        }
    });
      keysButton.setNextFocusableComponent(datensicherungButton);
      keysButton.setMnemonic('S');
      keysButton.setText("Schl�sselverwaltung");
      keysButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          keysButton_actionPerformed(e);
        }
    });
      this.setTitle("Administration");
      updateButton.setNextFocusableComponent(closeButton);
      updateButton.setMnemonic('U');
      updateButton.setText("Online-Update");
      updateButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          updateButton_actionPerformed(e);
        }
    });
      datensicherungButton.setNextFocusableComponent(updateButton);
      datensicherungButton.setMnemonic('D');
      datensicherungButton.setText("Datensicherung");
      datensicherungButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          datensicherungButton_actionPerformed(e);
        }
    });
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(wettJahrButton,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 20, 0, 20), 0, 0));
      jPanel2.add(configButton,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
      jPanel1.add(closeButton,  BorderLayout.SOUTH);
      jPanel2.add(keysButton,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
      jPanel2.add(updateButton,     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 20), 0, 0));
      jPanel2.add(datensicherungButton,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
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

  void wettJahrButton_actionPerformed(ActionEvent e) {
    String jahr = Dialog.inputDialog("Wettbewerbsjahr eingeben",
                                     "Bitte gib das Jahr ein, f�r welches Meldungen bearbeitet werden sollen!");
    if (jahr == null) return;
    int j = EfaUtil.string2int(jahr,-1);
    if (j <= -1 || j >= 2100) {
      Dialog.error("Der eingegebene Wert stellt kein g�ltiges Jahr dar!");
      return;
    }
    if (j < 1900) j += 1900;
    if (j < 1980) j += 100;
    if (j >= 2100) {
      Dialog.error("Der eingegebene Wert stellt kein g�ltiges Jahr dar!");
      return;
    }

    String mdir = Daten.efaDataDirectory+j+Daten.fileSep;
    String mdatFA = mdir+DRVConfig.MELDUNGEN_FA_FILE;
    String mdatWS = mdir+DRVConfig.MELDUNGEN_WS_FILE;

    if (!EfaUtil.canOpenFile(mdatFA) || !EfaUtil.canOpenFile(mdatWS)) {
      // mind. eine der Indexdateien fehlt
      try {
        boolean neuesDir = false;
        if (!(new File(mdir)).isDirectory()) {
          if (!(new File(mdir)).mkdir()) {
            Dialog.error("Das Verzeichnis\n"+mdir+"\n konnte nicht erstellt werden!");
            return;
          }
          Logger.log(Logger.INFO,"Neues Verzeichnis "+mdir+" f�r Wettbewerbsjahr "+j+" erstellt.");
          neuesDir = true;
        }
        MeldungenIndex mIndex;
        mIndex = new MeldungenIndex(mdatFA);
        if (!mIndex.readFile()) {
          if (!mIndex.writeFile()) {
            Dialog.error("Die Datei\n"+mIndex.getFileName()+"\n konnte nicht erstellt werden!");
            return;
          }
          Logger.log(Logger.INFO,"Neue Meldungen-Indexdatei "+mdatFA+" f�r Wettbewerbsjahr "+j+" (Fahrtenabzeichen) erstellt.");
        } else {
          Logger.log(Logger.INFO,"Meldungen-Indexdatei "+mdatFA+" f�r Wettbewerbsjahr "+j+" (Fahrtenabzeichen) ge�ffnet.");
        }
        mIndex = new MeldungenIndex(mdatWS);
        if (!mIndex.readFile()) {
          if (!mIndex.writeFile()) {
            Dialog.error("Die Datei\n"+mIndex.getFileName()+"\n konnte nicht erstellt werden!");
            return;
          }
          Logger.log(Logger.INFO,"Neue Meldungen-Indexdatei "+mdatWS+" f�r Wettbewerbsjahr "+j+" (Wanderruderstatistik) erstellt.");
        } else {
          Logger.log(Logger.INFO,"Meldungen-Indexdatei "+mdatFA+" f�r Wettbewerbsjahr "+j+" (Wanderruderstatistik) ge�ffnet.");
        }

      } catch(Exception ee) {
        Dialog.error("Es ist ein Fehler aufgetreten: "+e.toString());
        return;
      }
      Logger.log(Logger.INFO,"Neues Wettbewerbsjahr "+j+" ausgew�hlt.");
    } else {
      // vorhandenes Jahr
      MeldungenIndex mIndex;
      mIndex = new MeldungenIndex(mdatFA);
      if (!mIndex.readFile()) {
        Dialog.error("Die Datei\n"+mIndex.getFileName()+"\n konnte nicht ge�ffnet werden!");
        return;
      }
      mIndex = new MeldungenIndex(mdatWS);
      if (!mIndex.readFile()) {
        Dialog.error("Die Datei\n"+mIndex.getFileName()+"\n konnte nicht ge�ffnet werden!");
        return;
      }
      Logger.log(Logger.INFO,"Vorhandenes Wettbewerbsjahr "+j+" ausgew�hlt.");
    }
    drvConfig.aktJahr = j;
    drvConfig.writeFile();
    Dialog.infoDialog("Wettbewerbsjahr ausgew�hlt","Das ausgew�hlte Jahr f�r die Erfassung von Meldungen ist jetzt "+j+".");
  }

  void configButton_actionPerformed(ActionEvent e) {
    DRVConfigFrame dlg = new DRVConfigFrame(this,drvConfig);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();

  }

  void keysButton_actionPerformed(ActionEvent e) {
    KeysAdminFrame dlg;
    try {
      dlg = new KeysAdminFrame(this,drvConfig);
    } catch(Exception ee) {
      return;
    }
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void updateButton_actionPerformed(ActionEvent e) {
    OnlineUpdateFrame.runOnlineUpdate(this,Daten.ONLINEUPDATE_INFO_DRV);
  }

  void datensicherungButton_actionPerformed(ActionEvent e) {
    DatensicherungFrame dlg;
    try {
      Vector directories = new Vector();
      Vector selected = new Vector();
      Vector inclSubdirs = new Vector();
      directories.add(Daten.efaMainDirectory); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(false));
      directories.add(Daten.efaDataDirectory); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(false));
      if (drvConfig.aktJahr != 0) {
        directories.add(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(true));
      }
      if ((new File(Daten.efaDataDirectory+"CA"+Daten.fileSep)).isDirectory()) {
        directories.add(Daten.efaDataDirectory+"CA"+Daten.fileSep); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(true));
      }
      directories.add(Daten.efaCfgDirectory); selected.add(new Boolean(true)); inclSubdirs.add(new Boolean(true));
      dlg = new DatensicherungFrame(this,directories,inclSubdirs,selected);
    } catch(Exception ee) {
      return;
    }
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }


}
