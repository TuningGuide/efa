package de.nmichael.efa.drv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.net.*;
// import java.security.interfaces.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;


public class KeysAdminFrame extends JDialog implements ActionListener {
  JDialog parent;
  DRVConfig drvConfig;
  Object[] keys;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton closeButton = new JButton();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton newButton = new JButton();
  JButton editButton = new JButton();
  JButton deleteButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable keyTable;
  JButton exportCertButton = new JButton();
  JButton setDefaultButton = new JButton();
  JButton importCertButton = new JButton();


  public KeysAdminFrame(JDialog parent, DRVConfig drvConfig) throws Exception {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    this.parent = parent;
    this.drvConfig = drvConfig;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+drvConfig.KEYSTORE_FILE)) {
      enterNewKeyPassword(drvConfig);
      if (drvConfig.keyPassword == null) {
        cancel();
        throw new Exception("Falsches Pa�wort!");
      }
    }
    if (drvConfig.keyPassword == null) enterKeyPassword(drvConfig);
    if (drvConfig.keyPassword == null || !loadKeys()) {
      cancel();
      drvConfig.keyPassword = null;
      throw new Exception("Falsches Pa�wort!");
    }
    displayKeys();

    EfaUtil.pack(this);
    // this.requestFocus();
  }

  public static void enterKeyPassword(DRVConfig drvConfig) {
    drvConfig.keyPassword = EnterPasswordFrame.enterPassword(Dialog.frameCurrent(),"Bitte Schl�ssel-Pa�wort eingeben:");
  }

  public static void enterNewKeyPassword(DRVConfig drvConfig) {
    drvConfig.keyPassword = EnterPasswordFrame.enterNewPassword(Dialog.frameCurrent(),
                            "Damit die erstellten Schl�ssel vor unbefugten Zugriffen sicher sind,\n"+
                            "werden sie durch ein Pa�wort gesch�tzt. Dieses Pa�wort mu� unter\n"+
                            "allen Umst�nden geheim bleiben, da von ihm die Sicherheit des\n"+
                            "Gesamtsystems abh�ngt.\n"+
                            "Gib daher bitte jetzt ein Pa�wort ein, das m�glichst lang und\n"+
                            "vor allem nicht zu erraten ist (kein Wort der deutschen Sprache!).\n"+
                            "Das Pa�wort mu� mindestens 8 Zeichen lang sein und mu� von den vier\n"+
                            "Zeichengruppen 'Kleinbuchstaben', 'Gro�buchstaben', 'Ziffern' und\n"+
                            "'sonstige Zeichen' mindestens drei Gruppen enthalten.");
    if (drvConfig.keyPassword != null) Logger.log(Logger.INFO,"Neues Pa�wort f�r Schl�sselspeicher festgelegt.");
  }

  boolean loadKeys() {
    if (Daten.keyStore != null && Daten.keyStore.isKeyStoreReady()) return true;
    Daten.keyStore = new EfaKeyStore(Daten.efaDataDirectory+drvConfig.KEYSTORE_FILE,drvConfig.keyPassword);
    if (!Daten.keyStore.isKeyStoreReady()) {
      Dialog.error("KeyStore kann nicht geladen werden:\n"+Daten.keyStore.getLastError());
    }
    return Daten.keyStore.isKeyStoreReady();
  }

  void displayKeys() {
    try {
      String alias;
      Vector _keys = new Vector();
      for (Enumeration e = Daten.keyStore.getAliases(); e.hasMoreElements(); ) {
        alias = (String)e.nextElement();
        _keys.add(alias);
      }

      keys = _keys.toArray();
      Arrays.sort(keys);

      String[][] tableData = new String[keys.length][5];
      for (int i=0; i<keys.length; i++) {
        X509Certificate cert = Daten.keyStore.getCertificate((String)keys[i]);
        tableData[i][0] = (String)keys[i];
        tableData[i][1] = CertInfos.getValidityYears(cert);
        tableData[i][2] = EfaUtil.date2String(cert.getNotBefore());
        tableData[i][3] = EfaUtil.date2String(cert.getNotAfter());
        tableData[i][4] = (drvConfig.schluessel.equals(keys[i]) ? "Standard" : "");
      }
      String[] tableHeader = new String[5];
      tableHeader[0] = "Schl�ssel-ID";
      tableHeader[1] = "g�ltig f�r";
      tableHeader[2] = "g�ltig von";
      tableHeader[3] = "g�ltig bis";
      tableHeader[4] = "Status";

      if (keyTable != null) jScrollPane1.getViewport().remove(keyTable);
      keyTable = new JTable(tableData,tableHeader);
      jScrollPane1.getViewport().add(keyTable, null);
    } catch(Exception e) {
      Dialog.error("Kann die Schl�sselliste nicht anzeigen: "+e.toString());
    }
  }

  void setDefaultKey(String alias) {
    try {
      X509Certificate cert = Daten.keyStore.getCertificate(alias);
      Date date = (new GregorianCalendar()).getTime();
      if (date.after(cert.getNotAfter()) || date.before(cert.getNotBefore())) {
        Dialog.error("Der Schl�ssel ist zum aktuellen Datum ung�ltig\n"+
                     "und kann nicht als Standardschl�ssel festgelegt werden!");
        return;
      }
    } catch(Exception ee) {
      Dialog.error(ee.toString());
      return;
    }

    drvConfig.schluessel = alias;
    if (!drvConfig.writeFile()) {
      Dialog.error("Konfigurationsdatei\n"+drvConfig.getFileName()+"\nkann nicht geschrieben werden!");
      drvConfig.schluessel = "";
      Logger.log(Logger.WARNING,"Kein Schl�ssel als Standardschl�ssel ausgew�hlt.");
    } else {
      Logger.log(Logger.INFO,"Schl�ssel "+alias+" als neuer Standardschl�ssel ausgew�hlt.");
    }
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
      closeButton.setText("Schlie�en");
      closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeButton_actionPerformed(e);
        }
    });
      jPanel3.setLayout(gridBagLayout2);
      newButton.setText("Neuen Schl�ssel erstellen");
      newButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newButton_actionPerformed(e);
        }
    });
      editButton.setText("Zertifikatinfos anzeigen");
      editButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editButton_actionPerformed(e);
        }
    });
      deleteButton.setText("Schl�ssel sperren");
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteButton_actionPerformed(e);
        }
    });
      exportCertButton.setText("Zertifikat exportieren");
      exportCertButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          exportCertButton_actionPerformed(e);
        }
    });
      setDefaultButton.setText("Als Standard festlegen");
      setDefaultButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setDefaultButton_actionPerformed(e);
        }
    });
      this.setTitle("Schl�sselverwaltung");
      importCertButton.setText("Zertifikat importieren");
      importCertButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          importCertButton_actionPerformed(e);
        }
    });
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(closeButton,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jPanel3,  BorderLayout.EAST);
      jPanel3.add(newButton,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(editButton,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(deleteButton,       new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(exportCertButton,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(setDefaultButton,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(importCertButton,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jScrollPane1,  BorderLayout.CENTER);
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

  void newButton_actionPerformed(ActionEvent e) {
    String _nr = Dialog.inputDialog("Schl�sselnummer","Bitte gib eine Nummer f�r den neu zu erstellenden Schl�ssel ein:");
    if (_nr == null) return;
    int nr = EfaUtil.string2int(_nr,-1);
    if (nr < 1 || nr > 99) {
      Dialog.error("Ung�ltige Nummer: Die Nummer mu� zwischen 1 und 99 liegen.");
      return;
    }
    _nr = Integer.toString(nr);
    if (_nr.length()<2) _nr = "0"+_nr;

    String alias = "drv"+_nr;
    boolean existiert = false;
    for (int i=0; keys != null && i<keys.length; i++) if (keys[i].equals(alias)) existiert = true;
    if (existiert) {
      Dialog.error("Es existiert bereits ein Schl�ssel mit dieser Nummer!");
      return;
    }

    String _validity = Dialog.inputDialog("G�ltigkeitsdauer","Bitte gib an, f�r welche Wettbewerbsjahre der Schl�ssel gelten soll (JJJJ-JJJJ):");
    if (_validity == null) return;
    TMJ tmj = EfaUtil.string2date(_validity,0,0,0);
    if (tmj.tag<2000) {
      Dialog.error("Ung�ltiges Jahr: "+tmj.tag);
      return;
    }
    if (tmj.monat > 0 && tmj.monat < 2000) {
      Dialog.error("Ung�ltiges Jahr: "+tmj.monat);
      return;
    }
    if (tmj.monat > 0 && tmj.tag > tmj.monat) {
      Dialog.error("Das Startjahr "+tmj.tag+" mu� vor dem Endjahr "+tmj.monat+" liegen!");
      return;
    }
    if (tmj.monat == 0) tmj.monat = tmj.tag;
    String cn = alias+"_"+tmj.tag;
    if (tmj.monat > tmj.tag) cn += "-"+tmj.monat;

    int tage = EfaUtil.getDateDiff(EfaUtil.getCurrentTimeStampDD_MM_YYYY(),"31.12."+(tmj.monat+1)) - 1;

    try {
      CA ca;

      try {
        ca = new CA(drvConfig);
      } catch(Exception ee) {
        Dialog.error(ee.toString());
        return;
      }

      ca.runKeytool("-genkey"+
                    " -alias "+alias+"_priv"+
                    " -keyalg DSA -keysize 1024 -sigalg SHA1withDSA"+
                    " -validity "+tage+
                    " -dname CN=Deutscher\\sRuderverband,O="+cn+",C=DE\"",drvConfig.keyPassword);

      ca.runKeytool("-certreq -alias "+alias+"_priv"+
                    " -file "+Daten.efaTmpDirectory+"certreq.csr",drvConfig.keyPassword);

      if (!ca.signRequest(Daten.efaTmpDirectory+"certreq.csr",Daten.efaTmpDirectory+"certreq.pem",tage)) {
        Dialog.error("Fehler beim Signieren des Zertifikats durch die CA.");
        Dialog.infoDialog("Der erstellte Schl�ssel wird nun wieder gel�scht.");
        ca.runKeytool("-delete"+
                      " -alias "+alias+"_priv",drvConfig.keyPassword);
        Dialog.infoDialog("Schl�ssel wurde gel�scht","Der erstellte Schl�ssel wurde erfolgreich gel�scht.");
        return;
      }

      ca.runKeytool("-import -alias "+alias+
                    " -file "+Daten.efaTmpDirectory+"certreq.pem",null);

      Logger.log(Logger.INFO,"Neuer Schl�ssel "+alias+" (und privater Schl�ssel "+alias+"_priv) erstellt.");

      (new File(Daten.efaTmpDirectory+"certreq.csr")).delete();
      (new File(Daten.efaTmpDirectory+"certreq.pem")).delete();
      Daten.keyStore.reload();
      if (keys == null || keys.length == 0) {
        // neu erzeugten Schl�ssel als Standardschl�ssel festlegen
        setDefaultKey(alias+"_priv");
      }
    } catch(Exception ee) {
      Dialog.error(ee.toString());
    }

    displayKeys();
  }

  void editButton_actionPerformed(ActionEvent e) {
    if (keyTable == null) return;
    if (keyTable.getSelectedRow() < 0) {
      Dialog.error("Bitte w�hle zuerst einen Schl�ssel aus!");
      return;
    }
    if (keyTable.getSelectedRow()>=keys.length) {
      Dialog.error("Oops! Der ausgew�hlte Schl�ssel existiert nicht!");
      return;
    }
    String alias = (String)keys[keyTable.getSelectedRow()];

    try {
      String s = CertInfos.getCertInfos(Daten.keyStore.getCertificate(alias),null);
      Dialog.infoDialog("Informationen zum Zertifikat f�r "+alias,s);
    } catch(Exception ee) {
      Dialog.error(ee.toString());
    }
  }

  void deleteButton_actionPerformed(ActionEvent e) {
    Dialog.infoDialog("Funktion noch nicht implementiert.");
  }

  void exportCertButton_actionPerformed(ActionEvent e) {
    if (keyTable == null) return;
    if (keyTable.getSelectedRow() < 0) {
      Dialog.error("Bitte w�hle zuerst einen Schl�ssel aus!");
      return;
    }
    if (keyTable.getSelectedRow()>=keys.length) {
      Dialog.error("Oops! Der ausgew�hlte Schl�ssel existiert nicht!");
      return;
    }
    String alias = (String)keys[keyTable.getSelectedRow()];
    if (alias == null) {
      Dialog.error("Oops! Der ausgew�hlte Schl�ssel ist NULL!");
      return;
    }
    if (alias.endsWith("_priv")) {
      Dialog.error("Zertifikate werden nur f�r �ffentliche Schl�ssel ausgestellt!\nBitte w�hle einen Schl�ssel mit Namen drvXX.");
      return;
    }

    String certFile = Daten.efaDataDirectory+alias+".cert";
    if ((new File(certFile)).isFile() &&
        Dialog.yesNoDialog("Datei existiert bereits",
                           "Die Zertifikatsdatei\n"+
                           certFile+"\n"+
                           "existiert bereits.\n"+
                           "Soll sie �berschrieben werden?") != Dialog.YES) return;
    CA ca;
    try {
      ca = new CA(drvConfig);
    } catch(Exception ee) {
      Dialog.error(ee.toString());
      return;
    }
    ca.runKeytool("-export -alias "+alias+
                  " -file "+certFile,null);
    Dialog.infoDialog("Zertifikat exportiert",
                      "Das Zertifikat f�r "+alias+" wurde erfolgreich in die Datei\n"+
                      certFile+"\n"+
                      "exportiert.");
    if (Dialog.yesNoDialog("Zertifikat in efaWett hinterlegen",
                           "Soll das Zertifikat jetzt hochgeladen\n"+
                           "und in efaWett hinterlegt werden?") != Dialog.YES) return;
    if (!Dialog.okAbbrDialog("Internet-Verbindung","Bitte stelle eine Verbindung zum Internet her\nund klicke dann OK.")) return;
    try {
      int filesize = (int)(new File(certFile)).length();
      byte[] buf = new byte[filesize];
      FileInputStream f = new FileInputStream(certFile);
      f.read(buf,0,filesize);
      f.close();
      String data = Base64.encodeBytes(buf);
      data = EfaUtil.replace(data,"=","**0**",true); // "=" als "**0**" maskieren

      String request = drvConfig.makeScriptRequestString(DRVConfig.ACTION_UPLCERT,"cert="+alias+".cert64","data="+data,null,null);
      int pos = request.indexOf("?");
      if (pos < 0) {
        Dialog.error("efaWett-Anfrage zum Hochladen des Zertifikats konnte nicht erstellt werden.");
        return;
      }
      String url = request.substring(0,pos);
      String content = request.substring(pos+1,request.length());

      HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
      conn.setRequestMethod("POST");
      conn.setAllowUserInteraction(false);
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
      conn.setRequestProperty("Content-length",Integer.toString(content.length()));
      DataOutputStream out = new DataOutputStream(conn.getOutputStream ());
      out.writeBytes(content);
      out.flush();
      out.close();
      conn.disconnect();
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      boolean ok = true;
      String z;
      while((z = in.readLine()) != null) {
        if (!z.equals("OK")) {
          Dialog.error("Fehler beim Hochladen des Zertifikats: "+z);
          return;
        } else {
          Dialog.infoDialog("Zertifikat erfolgreich hochgeladen.");
        }
      }
    } catch(Exception ee) {
      Dialog.error("Fehler beim Hochladen des Zertifikats: "+ee.toString());
    }
  }

  void setDefaultButton_actionPerformed(ActionEvent e) {
    if (keyTable == null) return;
    if (keyTable.getSelectedRow() < 0) {
      Dialog.error("Bitte w�hle zuerst einen Schl�ssel aus!");
      return;
    }
    if (keyTable.getSelectedRow()>=keys.length) {
      Dialog.error("Oops! Der ausgew�hlte Schl�ssel existiert nicht!");
      return;
    }
    String alias = (String)keys[keyTable.getSelectedRow()];
    if (alias == null) {
      Dialog.error("Oops! Der ausgew�hlte Schl�ssel ist NULL!");
      return;
    }
    if (!alias.endsWith("_priv")) {
      Dialog.error("Nur private Schl�ssel k�nnen als Standardschl�ssel markiert werden!\nBitte w�hle einen privaten Schl�ssel aus.");
      return;
    }

    setDefaultKey(alias);
    displayKeys();
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void importCertButton_actionPerformed(ActionEvent e) {
    String keyfile = Dialog.dateiDialog(Dialog.frameCurrent(),"�ffentlichen Schl�ssel ausw�hlen",
                       "�ffentlicher Schl�ssel (*.cert)","cert",Daten.efaDataDirectory,false);
    if (keyfile == null) return;
    DRVSignaturFrame.importKey(keyfile);
    displayKeys();
  }


}
