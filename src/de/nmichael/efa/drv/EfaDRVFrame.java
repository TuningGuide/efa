package de.nmichael.efa.drv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;

public class EfaDRVFrame extends JFrame {
  private DRVConfig drvConfig;

  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel northPanel = new JPanel();
  JPanel centerPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel titleLabel = new JLabel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton administrationButton = new JButton();
  JButton meldungenFAButton = new JButton();
  JButton beendenButton = new JButton();
  JLabel versionLabel = new JLabel();
  JButton meldungenWSButton = new JButton();
  JLabel copyLabel = new JLabel();
  JLabel modeLabel = new JLabel();

  //Construct the frame
  public EfaDRVFrame(DRVConfig drvConfig) {
    this.drvConfig = drvConfig;

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
      appIni();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    Dialog.frameOpened(this);
  }


  void cancel() {
    Dialog.frameClosed(this);
    Logger.log(Logger.INFO,"PROGRAMMENDE");
    System.exit(0);
  }

  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,"NOHELP!!!");
    }
  }


  private void appIni() {
    this.meldungenFAButton.setEnabled(false);
    this.meldungenWSButton.setEnabled(false);
    if (drvConfig.aktJahr != 0) {
      String mdat = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+DRVConfig.MELDUNGEN_FA_FILE;
      if (EfaUtil.canOpenFile(mdat)) {
        this.meldungenFAButton.setText("DRV-Fahrtenabzeichen f�r das Jahr "+drvConfig.aktJahr+" bearbeiten");
        this.meldungenFAButton.setEnabled(true);
      } else {
        Dialog.error("Die Datei\n"+mdat+"\nkonnte nicht gefunden werden.\nVorhandene Fahrtenabzeichen-Meldungen des Jahres "+drvConfig.aktJahr+" k�nnen daher nicht bearbeitet werden.");
        Logger.log(Logger.ERROR,"Die Datei\n"+mdat+"\nkonnte nicht gefunden werden.\nVorhandene Fahrtenabzeichen-Meldungen des Jahres "+drvConfig.aktJahr+" k�nnen daher nicht bearbeitet werden.");
      }
      mdat = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+DRVConfig.MELDUNGEN_WS_FILE;
      if (EfaUtil.canOpenFile(mdat)) {
        this.meldungenWSButton.setText("DRV-Wanderruderstatistik f�r das Jahr "+drvConfig.aktJahr+" bearbeiten");
        this.meldungenWSButton.setEnabled(true);
      } else {
        Dialog.error("Die Datei\n"+mdat+"\nkonnte nicht gefunden werden.\nVorhandene Wanderruderstatistik-Meldungen des Jahres "+drvConfig.aktJahr+" k�nnen daher nicht bearbeitet werden.");
        Logger.log(Logger.ERROR,"Die Datei\n"+mdat+"\nkonnte nicht gefunden werden.\nVorhandene Wanderruderstatistik-Meldungen des Jahres "+drvConfig.aktJahr+" k�nnen daher nicht bearbeitet werden.");
      }

    }
    this.meldungenFAButton.setVisible(drvConfig.darfFAbearbeiten);
    this.meldungenWSButton.setVisible(drvConfig.darfWSbearbeiten);
    if (drvConfig.testmode || drvConfig.readOnlyMode) {
      if (drvConfig.testmode) modeLabel.setText(" - Testmode - ");
      if (drvConfig.readOnlyMode) modeLabel.setText((drvConfig.testmode ? modeLabel.getText() : "") + " - ReadOnly-Mode - ");
    } else {
      modeLabel.setText("");
    }
  }

  //Component initialization
  private void jbInit() throws Exception  {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"},
                       new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    //setIconImage(Toolkit.getDefaultToolkit().createImage(EfaDRVFrame.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(516, 308));
    this.setTitle("elektronischer Fahrtenwettbewerb");
    mainPanel.setLayout(borderLayout2);
    northPanel.setLayout(gridBagLayout1);
    titleLabel.setFont(new java.awt.Font("Dialog", 1, 16));
    titleLabel.setForeground(new Color(102, 102, 255));
    titleLabel.setText("elektronischer Fahrtenwettbewerb");
    centerPanel.setLayout(gridBagLayout2);
    administrationButton.setNextFocusableComponent(beendenButton);
    administrationButton.setMnemonic('A');
    administrationButton.setText("Administration");
    administrationButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        administrationButton_actionPerformed(e);
      }
    });
    meldungenFAButton.setEnabled(false);
    meldungenFAButton.setNextFocusableComponent(meldungenWSButton);
    meldungenFAButton.setMnemonic('F');
    meldungenFAButton.setText("DRV-Fahrtenabzeichen f�r das Jahr ???? bearbeiten");
    meldungenFAButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        meldungenFAButton_actionPerformed(e);
      }
    });
    meldungenWSButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        meldungenWSButton_actionPerformed(e);
      }
    });
    beendenButton.setNextFocusableComponent(meldungenFAButton);
    beendenButton.setActionCommand("beendenButton");
    beendenButton.setMnemonic('B');
    beendenButton.setText("Beenden");
    beendenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        beendenButton_actionPerformed(e);
      }
    });
    versionLabel.setText("Version "+Daten.VERSION);
    meldungenWSButton.setEnabled(false);
    meldungenWSButton.setNextFocusableComponent(administrationButton);
    meldungenWSButton.setMnemonic('W');
    meldungenWSButton.setText("DRV-Wanderruderstatistik f�r das Jahr ???? bearbeiten");
    copyLabel.setText("Copyright (c) 2004-"+Daten.COPYRIGHTYEAR+" by Nicolas Michael");
    modeLabel.setForeground(Color.red);
    modeLabel.setText("Testmode");
    contentPane.add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(northPanel, BorderLayout.NORTH);
    mainPanel.add(centerPanel, BorderLayout.CENTER);
    northPanel.add(titleLabel,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
    northPanel.add(versionLabel,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    northPanel.add(copyLabel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    northPanel.add(modeLabel,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(administrationButton,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
    centerPanel.add(meldungenFAButton,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 20, 0, 20), 0, 0));
    centerPanel.add(beendenButton,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 20), 0, 0));
    centerPanel.add(meldungenWSButton,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void administrationButton_actionPerformed(ActionEvent e) {
    Logger.log(Logger.INFO,"START Administrationsmodus");
    DRVAdminFrame dlg = new DRVAdminFrame(this,drvConfig);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    Logger.log(Logger.INFO,"ENDE Administrationsmodus");
    appIni();
  }

  void meldungenFAButton_actionPerformed(ActionEvent e) {
    if (drvConfig.aktJahr < 1980) {
      Dialog.error("Es ist kein Wettbewerbsjahr ausgew�hlt.\nBitte w�hle zuerst �ber den Punkt 'Administration' ein Wettbewerbsjahr aus.");
      return;
    }

    drvConfig.meldungenIndex = new MeldungenIndex(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+DRVConfig.MELDUNGEN_FA_FILE);
    if (!drvConfig.meldungenIndex.readFile()) {
      Dialog.error("Die Meldungen-Indexdatei\n"+drvConfig.meldungenIndex.getFileName()+"\nkann nicht gelesen werden!");
      Logger.log(Logger.ERROR,"Die Meldungen-Indexdatei\n"+drvConfig.meldungenIndex.getFileName()+"\nkann nicht gelesen werden!");
      return;
    }

    drvConfig.teilnehmer = new Teilnehmer(Daten.efaDataDirectory+DRVConfig.TEILNEHMER_FILE);
    if (!drvConfig.teilnehmer.readFile()) {
      Dialog.error("Die Teilnehmer-Datei\n"+drvConfig.teilnehmer.getFileName()+"\nkann nicht gelesen werden!");
      Logger.log(Logger.ERROR,"Die Teilnehmer-Datei\n"+drvConfig.teilnehmer.getFileName()+"\nkann nicht gelesen werden!");
      return;
    }

    drvConfig.meldestatistik = new Meldestatistik(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+DRVConfig.MELDESTATISTIK_FA_FILE);
    if (!drvConfig.meldestatistik.readFile()) {
      Dialog.error("Die Meldestatistik-Datei\n"+drvConfig.meldestatistik.getFileName()+"\nkann nicht gelesen werden!");
      Logger.log(Logger.ERROR,"Die Meldestatistik-Datei\n"+drvConfig.meldestatistik.getFileName()+"\nkann nicht gelesen werden!");
      return;
    }

    Logger.log(Logger.INFO,"START Meldungen f�r "+drvConfig.aktJahr+" bearbeiten");
    MeldungenIndexFrame dlg = new MeldungenIndexFrame(this,drvConfig,MeldungenIndexFrame.MELD_FAHRTENABZEICHEN);
    dlg.setSize((int)Dialog.screenSize.getWidth()-100,(int)Dialog.screenSize.getHeight()-100);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    Logger.log(Logger.INFO,"ENDE Meldungen f�r "+drvConfig.aktJahr+" bearbeiten");
  }

  void meldungenWSButton_actionPerformed(ActionEvent e) {
    if (drvConfig.aktJahr < 1980) {
      Dialog.error("Es ist kein Wettbewerbsjahr ausgew�hlt.\nBitte w�hle zuerst �ber den Punkt 'Administration' ein Wettbewerbsjahr aus.");
      return;
    }

    drvConfig.meldungenIndex = new MeldungenIndex(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+DRVConfig.MELDUNGEN_WS_FILE);
    if (!drvConfig.meldungenIndex.readFile()) {
      Dialog.error("Die Meldungen-Indexdatei\n"+drvConfig.meldungenIndex.getFileName()+"\nkann nicht gelesen werden!");
      Logger.log(Logger.ERROR,"Die Meldungen-Indexdatei\n"+drvConfig.meldungenIndex.getFileName()+"\nkann nicht gelesen werden!");
      return;
    }

    drvConfig.teilnehmer = new Teilnehmer(Daten.efaDataDirectory+DRVConfig.TEILNEHMER_FILE);
    if (!drvConfig.teilnehmer.readFile()) {
      Dialog.error("Die Teilnehmer-Datei\n"+drvConfig.teilnehmer.getFileName()+"\nkann nicht gelesen werden!");
      Logger.log(Logger.ERROR,"Die Teilnehmer-Datei\n"+drvConfig.teilnehmer.getFileName()+"\nkann nicht gelesen werden!");
      return;
    }

    drvConfig.meldestatistik = new Meldestatistik(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+DRVConfig.MELDESTATISTIK_WS_FILE);
    if (!drvConfig.meldestatistik.readFile()) {
      Dialog.error("Die Meldestatistik-Datei\n"+drvConfig.meldestatistik.getFileName()+"\nkann nicht gelesen werden!");
      Logger.log(Logger.ERROR,"Die Meldestatistik-Datei\n"+drvConfig.meldestatistik.getFileName()+"\nkann nicht gelesen werden!");
      return;
    }

    Logger.log(Logger.INFO,"START Meldungen f�r "+drvConfig.aktJahr+" bearbeiten");
    MeldungenIndexFrame dlg = new MeldungenIndexFrame(this,drvConfig,MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK);
    dlg.setSize((int)Dialog.screenSize.getWidth()-100,(int)Dialog.screenSize.getHeight()-100);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    Logger.log(Logger.INFO,"ENDE Meldungen f�r "+drvConfig.aktJahr+" bearbeiten");
  }

  void beendenButton_actionPerformed(ActionEvent e) {
    cancel();
  }
}