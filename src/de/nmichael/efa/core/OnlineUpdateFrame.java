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

// @i18n complete

public class OnlineUpdateFrame extends JDialog implements ActionListener {
  Window parent;
  static boolean startDownload = false;
  static JFrame parentFrame = null;
  static JDialog parentDialog = null;

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton cancelButton = new JButton();
  JButton downloadButton = new JButton();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel installedVersionLabel = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel newVersionLabel = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel downloadSizeLabel = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea changes = new JTextArea();
  JLabel jLabel2 = new JLabel();


  public OnlineUpdateFrame(JFrame parent, String versionName, int downloadSize, Vector changes) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      installedVersionLabel.setText(Daten.VERSIONID);
      newVersionLabel.setText(versionName);
      downloadSizeLabel.setText(downloadSize+" Bytes");
      for (int i=0; changes != null && i<changes.size(); i++) this.changes.append((String)changes.get(i)+"\n");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.parent = parent;
  }
  public OnlineUpdateFrame(JDialog parent, String versionName, int downloadSize, Vector changes) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      installedVersionLabel.setText(Daten.VERSIONID);
      newVersionLabel.setText(versionName);
      downloadSizeLabel.setText(downloadSize+" Bytes");
      for (int i=0; changes != null && i<changes.size(); i++) this.changes.append((String)changes.get(i)+"\n");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.parent = parent;
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
      jPanel2.setLayout(flowLayout1);
      Mnemonics.setButton(this, cancelButton, International.getStringWithMnemonic("Abbruch"));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          cancelButton_actionPerformed(e);
        }
    });
    Mnemonics.setButton(this, downloadButton, International.getStringWithMnemonic("Neue Version downloaden"));
      downloadButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          downloadButton_actionPerformed(e);
        }
    });
      jPanel3.setLayout(gridBagLayout1);
      jLabel1.setText(International.getString("derzeit installierte Version")+": ");
      installedVersionLabel.setText("1.2.3_04");
      jLabel3.setText(International.getString("im Internet verfügbare Version")+": ");
      newVersionLabel.setText("5.6.7_08");
      jLabel5.setText(International.getString("Downloadgröße")+": ");
      downloadSizeLabel.setText("1234567 Bytes");
      jScrollPane1.setMinimumSize(new Dimension(300, 150));
      jScrollPane1.setPreferredSize(new Dimension(600, 150));
      jLabel2.setText(International.getString("Änderungen gegenüber der installierten Version")+": ");
      this.setTitle(International.getString("Online-Update"));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(downloadButton, null);
      jPanel2.add(cancelButton, null);
      jPanel1.add(jPanel3, BorderLayout.NORTH);
      jPanel3.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
      jPanel3.add(installedVersionLabel,     new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
      jPanel3.add(jLabel3,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      jPanel3.add(newVersionLabel,    new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(jLabel5,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(downloadSizeLabel,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(jLabel2,    new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 20, 0, 20), 0, 0));
      jPanel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(changes, null);
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


  public static boolean runOnlineUpdate(JDialog parent, String eouFile) {
    parentDialog = parent;
    parentFrame = null;
    return runOnlineUpdate(eouFile);
  }

  public static boolean runOnlineUpdate(JFrame parent, String eouFile) {
    parentFrame = parent;
    parentDialog = null;
    return runOnlineUpdate(eouFile);
  }

  public static boolean runOnlineUpdate(String eouFile) {
    String newVersion = null;

    // Online Update
    if (!Dialog.okAbbrDialog(International.getString("Online-Update"),
                             International.getString("Mit dem efa Online-Update kann efa eine neue Version aus dem "+
                             "Internet herunterladen und installieren.") + "\n\n" +
                             International.getString("Damit efa überprüfen kann, ob es eine neue Version gibt, "+
                             "stelle nun bitte eine Verbindung zum Internet her und klicke "+
                             "anschließend OK."))) return false;

    // aktuelle Versionsnummer aus dem Internet besorgen
    String versionFile = Daten.efaTmpDirectory+"efa.eou";
    if (parentFrame != null) {
      if (!EfaUtil.getFile(parentFrame,eouFile,versionFile,true)) return false;
    } else {
      if (!EfaUtil.getFile(parentDialog,eouFile,versionFile,true)) return false;
    }
    BufferedReader versionInfo = null;
    try {
      versionInfo = new BufferedReader(new InputStreamReader(new FileInputStream(versionFile),Daten.ENCODING));
      if ( (newVersion = versionInfo.readLine()) != null) {
        newVersion = newVersion.trim();
      }
    } catch(IOException ee) {
      Dialog.error(International.getString("efa konnte keine neue Versionsnummer ermitteln.")+"\n"+ee.getMessage());
      EfaUtil.deleteFile(versionFile);
      return false;
    }
    if (newVersion == null) {
      Dialog.error(International.getString("efa konnte keine neue Versionsnummer ermitteln."));
      EfaUtil.deleteFile(versionFile);
      return false;
    }

    if (Daten.efaConfig != null) {
      Daten.efaConfig.efaVersionLastCheck = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
    }

    // ist die installierte Version aktuell?
    if (Daten.VERSIONID.equals(newVersion)) {
      Dialog.infoDialog(International.getMessage("Es liegt derzeit keine neuere Version von efa vor. "+
                        "Die von Dir benutzte Version {version} ist noch aktuell.",Daten.VERSIONID));
      EfaUtil.deleteFile(versionFile);
      return true;
    }

    // ist die installierte Version neuer als das Update?
    if (Daten.VERSIONID.compareTo(newVersion)>0) {
      Dialog.infoDialog(International.getString("Deine derzeit installierte Version ist neuer als die Version im Internet."));
      EfaUtil.deleteFile(versionFile);
      return true;
    }

    // Ok, es gibt eine neue Version --> Infos über diese Version einlesen
    String downloadURL = null;
    int downloadSize = 0;
    Vector changes = new Vector();
    try {
      String s;
      while ( (s = versionInfo.readLine()) != null) {
        if (s.startsWith("DOWNLOADURL=")) downloadURL = s.substring(12);
        if (s.startsWith("DOWNLOADSIZE=")) downloadSize = EfaUtil.string2int(s.substring(13),0);

        if (s.startsWith("[") && s.endsWith("]")) {
          s = s.substring(1,s.length()-1).trim();
          if (Daten.VERSIONID.compareTo(s)>=0) break; // ab hier Infos über ältere Versionen
        }

        if (s.startsWith("*") || s.startsWith("  ")) changes.add(s);
      }
      versionInfo.close();
      EfaUtil.deleteFile(versionFile);
    } catch(IOException e) {
      Dialog.error(International.getString("Fehler beim Lesen der Versionsinformationen")+": "+e.toString());
      EfaUtil.deleteFile(versionFile);
      return false;
    }

    // Ok, Informationen gelesen: Jetzt auf dem Bildschirm anzeigen
    OnlineUpdateFrame dlg;
    if (parentFrame != null) {
      dlg = new OnlineUpdateFrame(parentFrame,newVersion,downloadSize,changes);
    } else {
      dlg = new OnlineUpdateFrame(parentDialog,newVersion,downloadSize,changes);
    }
    Dialog.setDlgLocation(dlg,(parentFrame != null ? (Window)parentFrame : (Window)parentDialog));
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
    if (!startDownload) return false;

    // Download des Updates
    String zipFile = Daten.efaTmpDirectory + "update.zip";
    ExecuteAfterDownload afterDownload = new ExecuteAfterDownloadImpl((parentFrame != null ? (Window)parentFrame : (Window)parentDialog),zipFile,downloadSize);
    if (parentFrame != null) {
      parentFrame.setEnabled(false);
      if (!EfaUtil.getFile(parentFrame,downloadURL,zipFile,afterDownload)) return false;
    } else {
      parentDialog.setEnabled(false);
      if (!EfaUtil.getFile(parentDialog,downloadURL,zipFile,afterDownload)) return false;
    }
    return true;
  }

  void downloadButton_actionPerformed(ActionEvent e) {
    startDownload = true;
    cancel();
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    startDownload = false;
    cancel();
  }


}

class ExecuteAfterDownloadImpl implements ExecuteAfterDownload {
  Window parent;
  String zipFile;
  int fileSize;

  public ExecuteAfterDownloadImpl(Window parent, String zipFile, int fileSize) {
    this.parent = parent;
    this.zipFile = zipFile;
    this.fileSize = fileSize;
  }

  public void success() {
    parent.setEnabled(true); // !!!NEW!!!
    File f = new File(zipFile);
    if (f.length() < fileSize) {
      Dialog.error(International.getString("Der Download des Updates ist nicht vollständig.")+" "+
              International.getString("Der Update-Vorgang wird daher abgebrochen."));
      return;
    }
    if (f.length() > fileSize) {
      Dialog.error(International.getString("Der Download des Updates hat eine unerwartete Dateigröße.")+" "+
              International.getString("Der Update-Vorgang wird daher abgebrochen."));
      return;
    }

    // Download war erfolgreich
    Dialog.infoDialog(International.getString("Der Download war erfolgreich. Es werden jetzt alle Daten gesichert. Anschließend wird die neue Version installiert."));

    // ZIP-Archiv mit bisherigen Daten sichern
    Vector sourceDirs = new Vector();
    Vector inclSubdirs = new Vector();
    sourceDirs.add(Daten.efaDataDirectory);
    inclSubdirs.add(new Boolean(true));
    String backup = Daten.efaBakDirectory + "Backup_"+Daten.VERSIONID+"_before_Update.zip";
    String result = EfaUtil.createZipArchive(sourceDirs,inclSubdirs,backup);
    if (result != null) {
      if (Dialog.yesNoDialog(International.getString("Fehler bei Datensicherung"),
                         International.getMessage("Sicherung der Daten in der Datei {filename} fehlgeschlagen: {error}",backup,result)+
                         "\n"+
                         International.getString("Soll der Update-Vorgang trotzdem fortgesetzt werden?")) != Dialog.YES) return;
    }

    // Neue Version entpacken
    result = EfaUtil.unzip(zipFile,Daten.efaMainDirectory);
    if (result != null) {
      if (result.length()>1000) result = result.substring(0,1000);
      Dialog.error(International.getString("Die Installation der neuen Version ist fehlgeschlagen.")+"\n"+result);
      return;
    }

    // Erfolgreich
    Dialog.infoDialog(International.getString("Installation abgeschlossen"),
                      International.getString("Die Installation des Updates wurde erfolgreich abgeschlossen.")+"\n"+
                      International.getString("efa wird nun beendet.")+"\n"+
                      International.getString("Beim nächsten Start von efa wird automatisch die neue Version gestartet."));
    System.exit(0);
  }

  public void failure(String text) {
    parent.setEnabled(true); // !!!NEW!!!
    Dialog.infoDialog(International.getString("Der Download der neuen Version ist fehlgeschlagen:")+" "+text);
  }
}