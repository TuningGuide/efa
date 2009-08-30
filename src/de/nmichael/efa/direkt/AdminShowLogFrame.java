package de.nmichael.efa.direkt;

import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import java.util.*;
import java.io.*;
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

public class AdminShowLogFrame extends JDialog implements ActionListener {
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JRadioButton zeitHeute = new JRadioButton();
  JLabel jLabel2 = new JLabel();
  JCheckBox artInfo = new JCheckBox();
  JRadioButton zeit7Tage = new JRadioButton();
  JRadioButton zeitAlle = new JRadioButton();
  JCheckBox artWarn = new JCheckBox();
  JCheckBox artErr = new JCheckBox();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea log = new JTextArea();
  ButtonGroup buttonGroupZeit = new ButtonGroup();


  public AdminShowLogFrame(AdminFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      updateLog();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
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
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(zeitHeute);
      okButton.setMnemonic('O');
      okButton.setText("OK");
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      jLabel1.setText("Zeitraum: ");
      zeitHeute.setNextFocusableComponent(zeit7Tage);
      zeitHeute.setMnemonic('H');
      zeitHeute.setText("heute");
      zeitHeute.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          zeitHeute_actionPerformed(e);
        }
    });
      jLabel2.setText("Art der Einträge: ");
      artInfo.setNextFocusableComponent(artWarn);
      artInfo.setMnemonic('I');
      artInfo.setSelected(true);
      artInfo.setText("Informationen (INFO)");
      artInfo.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          artInfo_actionPerformed(e);
        }
    });
      zeit7Tage.setNextFocusableComponent(zeitAlle);
      zeit7Tage.setMnemonic('7');
      zeit7Tage.setSelected(true);
      zeit7Tage.setText("die letzten 7 Tage");
      zeit7Tage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          zeit7Tage_actionPerformed(e);
        }
    });
      zeitAlle.setNextFocusableComponent(artInfo);
      zeitAlle.setMnemonic('A');
      zeitAlle.setText("alle");
      zeitAlle.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          zeitAlle_actionPerformed(e);
        }
    });
      artWarn.setNextFocusableComponent(artErr);
      artWarn.setMnemonic('W');
      artWarn.setSelected(true);
      artWarn.setText("Warnungen (WARNING)");
      artWarn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          artWarn_actionPerformed(e);
        }
    });
      artErr.setNextFocusableComponent(log);
      artErr.setMnemonic('F');
      artErr.setSelected(true);
      artErr.setText("Fehler (ERROR)");
      artErr.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          artErr_actionPerformed(e);
        }
    });
      jScrollPane1.setPreferredSize(new Dimension(600, 500));
      this.setTitle("Logdatei");
      log.setNextFocusableComponent(okButton);
      jPanel1.setPreferredSize(new Dimension(750, 550));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.NORTH);
      jPanel2.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(zeitHeute,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,     new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      jPanel2.add(artInfo,     new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(zeit7Tage,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(zeitAlle,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(artWarn,   new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(artErr,   new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(log, null);
      buttonGroupZeit.add(zeitHeute);
      buttonGroupZeit.add(zeit7Tage);
      buttonGroupZeit.add(zeitAlle);
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


  void updateLog() {
    String now = EfaUtil.getCurrentTimeStamp().substring(0,10);
    BufferedReader f = null;
    try {
      log.setText("");
      f = new BufferedReader(new InputStreamReader(new FileInputStream(Daten.efaLogfile)));
      String s;
      String time="";
      String type="";
      while ( (s=f.readLine()) != null) {
        StringTokenizer tok = new StringTokenizer(s,"-");
        if (tok.countTokens()<3) {
          type="ERROR";
          s = time + " - " + type + " - " + s;
        } else {
          try {
            time = tok.nextToken().trim();
            type = tok.nextToken().trim();
          } catch(Exception ee) {}
        }
        // ist diese Art von Nachricht ausgewählt?
        boolean typeok = false;
        if (type.trim().equals(Logger.INFO)) {
          if (artInfo.isSelected()) typeok=true;
        } else if (type.trim().equals(Logger.WARNING)) {
          if (artWarn.isSelected()) typeok=true;
        } else if (type.trim().equals(Logger.DEBUG)) {
          typeok = false; // DEBUG nie anzeigen
        } else if (artErr.isSelected()) typeok=true;

        if (typeok) {
          // ist die Nachricht im Zeitraum?
          boolean timeok = false;
          if (zeitAlle.isSelected()) timeok=true;
          else {
            int diff = EfaUtil.getDateDiff(time.substring(1,11),now);
            if (zeit7Tage.isSelected() && diff<=7) timeok=true;
            else if (diff<=1) timeok=true;
          }

          if (timeok) log.append(s+"\n");
        }
      }
    } catch(Exception e) {
      log.append("FEHLER BEIM LESEN DER LOGDATEI: "+e.toString());
    } finally {
      try { f.close(); } catch(Exception ee) { f = null; }
    }
  }

  void zeitHeute_actionPerformed(ActionEvent e) {
    updateLog();
  }

  void zeit7Tage_actionPerformed(ActionEvent e) {
    updateLog();
  }

  void zeitAlle_actionPerformed(ActionEvent e) {
    updateLog();
  }

  void artInfo_actionPerformed(ActionEvent e) {
    updateLog();
  }

  void artWarn_actionPerformed(ActionEvent e) {
    updateLog();
  }

  void artErr_actionPerformed(ActionEvent e) {
    updateLog();
  }

  void okButton_actionPerformed(ActionEvent e) {
    cancel();
  }


}
