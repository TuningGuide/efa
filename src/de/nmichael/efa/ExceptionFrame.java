package de.nmichael.efa;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class ExceptionFrame extends JDialog implements ActionListener {
  String error;
  String stacktrace;
  BorderLayout borderLayout1 = new BorderLayout();
  JButton jButton1 = new JButton();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel errorLabel = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea errortext = new JTextArea();

  public ExceptionFrame(JDialog frame, String error, String stacktrace) {
    super(frame);
    ini(error,stacktrace);
  }
  public ExceptionFrame(JFrame frame, String error, String stacktrace) {
    super(frame);
    ini(error,stacktrace);
  }
  public ExceptionFrame(String error, String stacktrace) {
    ini(error,stacktrace);
  }

  void ini(String error, String stacktrace) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.error = error;
    this.stacktrace = stacktrace;
    try {
      jbInit();
    }
    catch(Exception e) {
    }
    EfaUtil.pack(this);
    jButton1.requestFocus();
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

    jButton1.setMnemonic('S');
    jButton1.setText("Schließen");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setText("Ein unbehandelter Programmfehler ist aufgetreten:");
    errorLabel.setForeground(Color.red);
    errorLabel.setText(error);
    String logfile = Daten.efaLogfile;
    if (logfile != null && Daten.fileSep != null) {
      int pos = logfile.lastIndexOf(Daten.fileSep);
      if (pos>0 && pos+1<logfile.length()) logfile = logfile.substring(pos+1,logfile.length());
      logfile = logfile.toUpperCase();
    }
    jLabel3.setText("Ein Fehlerprotokoll wurde in '"+Daten.efaLogfile+"' erstellt.");
    jLabel4.setText("Damit dieser Fehler korrigiert werden kann, schicke bitte eine email");
    jLabel5.setText("mit einer kurzen Beschreibung dessen, was diesen Fehler ausgelöst hat,");
    jLabel6.setText("an "+Daten.EFAEMAIL+". Kopiere bitte zusätzlich folgende Informationen");
    jLabel7.setText("in die email: -- Danke!");
    jLabel4.setForeground(Color.blue);
    jLabel5.setForeground(Color.blue);
    jLabel6.setForeground(Color.blue);
    jLabel7.setForeground(Color.blue);
    errortext.append("#####################################################\n# Unbehandelter Programmfehler!\n# Bitte per email an "+Daten.EFAEMAIL+" schicken!\n#####################################################\n\n");
    errortext.append("Fehler-Information:\n============================================\n");
    errortext.append("Fehlermeldung: "+error+"\n");
    errortext.append(stacktrace);
    errortext.append("\n\n");
    errortext.append("Programm-Information:\n============================================\n");
    Vector info = Daten.getEfaInfos();
    for (int i=0; info != null && i<info.size(); i++) errortext.append((String)info.get(i)+"\n");
    this.setTitle("Unerwarteter Programmfehler (Exception)");
    jScrollPane1.setPreferredSize(new Dimension(200, 200));
    this.getContentPane().add(jButton1, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(errorLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel3,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel4,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel5,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel6,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel7,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jScrollPane1,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(errortext, null);
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
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  void jButton1_actionPerformed(ActionEvent e) {
    cancel();
  }


}
