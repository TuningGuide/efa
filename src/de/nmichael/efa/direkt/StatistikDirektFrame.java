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

import de.nmichael.efa.core.*;
import de.nmichael.efa.statistics.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.beans.*;

// @i18n complete

public class StatistikDirektFrame extends JDialog implements ActionListener {
  EfaDirektFrame parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton createButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList list = new JList();
  TimeoutThread timeoutThread;
  JButton closeButton = new JButton();

  final static int PROGRESS_TIMETOPOPUP   = 0;
  final static int PROGRESS_TIMERINTERVAL = 20;
  private ProgressMonitor progressMonitor;
  private javax.swing.Timer timer = new javax.swing.Timer(PROGRESS_TIMERINTERVAL, new TimerListener());;
  private StatistikThread statistikThread;

  class TimeoutThread extends Thread {
    private StatistikDirektFrame frame;

    public TimeoutThread(StatistikDirektFrame frame) {
      this.frame = frame;
    }

    public void run() {
      try {
        do {
          Thread.sleep(Daten.WINDOWCLOSINGTIMEOUT*100); // absichtlich nur *100, d.h. 1/10 der sonst üblichen Zeit
        } while (Dialog.frameCurrent() != frame);
      } catch(InterruptedException e) {
        return;
      }
      if (Dialog.frameCurrent() == frame) frame.cancel(true);
    }
  }


  public StatistikDirektFrame(EfaDirektFrame parent, Vector stats) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      list.setListData(stats);

      int sizeX = (int)list.getPreferredSize().getWidth();
      int sizeY = (int)list.getPreferredSize().getHeight();
      if (sizeX < 300) sizeX = 300; if (sizeX > 600) sizeX = 600;
      if (sizeY < 200) sizeY = 200; if (sizeY > 500) sizeY = 500;
      jScrollPane1.setPreferredSize(new Dimension(sizeX, sizeY));

    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.parent = parent;
    EfaUtil.pack(this);
    list.requestFocus();
    if (stats.size()>0) list.setSelectedIndex(0);

    timeoutThread = new TimeoutThread(this);
    timeoutThread.start();
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


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
    jPanel1.setLayout(borderLayout1);
    createButton.setNextFocusableComponent(closeButton);
    Mnemonics.setButton(this, createButton, International.getStringWithMnemonic("Statistik erstellen"));
    createButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createButton_actionPerformed(e);
      }
    });
    this.setTitle(International.getString("Statistik erstellen"));
    list.setNextFocusableComponent(createButton);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    closeButton.setNextFocusableComponent(list);
    Mnemonics.setButton(this, closeButton, International.getStringWithMnemonic("Schließen"));
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(createButton, BorderLayout.SOUTH);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(closeButton, BorderLayout.SOUTH);
    jScrollPane1.getViewport().add(list, null);
    list.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) createButton_actionPerformed(null);
      }
    });

  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel(false);
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  // timeout=true, wenn cancel() vom TimeoutThread gerufen wird!
  void cancel(boolean timeout) {
    if (Statistik.isCreateRunning) return; // Bugfix: Damit nicht "Schließen" gedrückt werden kann, während die Statistikerstellung läuft
    Dialog.frameClosed(this);
    try {
      if (!timeout && timeoutThread != null && timeoutThread.isAlive()) timeoutThread.interrupt();
    } catch(Exception e) {}
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }



  void createButton_actionPerformed(ActionEvent e) {
    if (list.getSelectedValue() == null) {
      Dialog.error(International.getString("Bitte wähle eine Statistik aus!"));
      return;
    }


    DatenFelder d = null;
    try {
      d = Daten.fahrtenbuch.getDaten().statistik.getExactComplete((String)list.getSelectedValue());
    } catch(Exception ee) {
    }
    if (d == null) {
      Logger.log(Logger.ERROR, Logger.MSG_ERR_STATISTICNOTFOUND,
              International.getString("Fehler beim Erstellen der Statistik!") + " " +
              International.getString("Statistik nicht gefunden!"));
      Dialog.error(International.getString("Statistik nicht gefunden!")); // sollte nie passieren
      return;
    }

    StatistikDaten[] sd = new StatistikDaten[1];
    try {
      sd[0] = StatistikFrame.getSavedValues(d);
      sd[0].browserCloseTimeout = Daten.WINDOWCLOSINGTIMEOUT;
      StatistikFrame.allgStatistikDaten(sd[0]);
      sd[0].parent = this;
    } catch(StringIndexOutOfBoundsException ee) {
      Logger.log(Logger.ERROR, Logger.MSG_ERR_ERRORCREATINGSTATISTIC,
              International.getString("Fehler beim Erstellen der Statistik!") + " " +
              International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
      Dialog.error(International.getString("Fehler beim Lesen der gespeicherten Konfiguration!"));
    }

    try {
      startStatistik(sd);
    } catch(Exception ee) {
      Logger.log(Logger.ERROR, Logger.MSG_ERR_ERRORCREATINGSTATISTIC,
              International.getString("Fehler beim Erstellen der Statistik!") + " " +
              ee.toString());
      Dialog.error(International.getString("Fehler beim Erstellen der Statistik!"));
    }
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel(false);
  }

  // Statistikberechnung beginnen
  public void startStatistik(StatistikDaten[] d) {
    // Statistikberechnung mit Progress-Bar
    statistikThread = new StatistikThread();
    progressMonitor = new ProgressMonitor(this, International.getString("Statistikberechnung"), "", 0, statistikThread.getLengthOfTask());
    progressMonitor.setProgress(0);
    progressMonitor.setMaximum(1);
    progressMonitor.setMillisToDecideToPopup(PROGRESS_TIMETOPOPUP);
      // enableFrame(...) gibt false zurück, wenn das Frame bereits disabled ist, d.h. wenn bereits eine Berechnung
      // läuft. Dies ist ein Bugfix, damit eine Statistikberechnung nicht mehrfach parallel ausgeführt werden kann
      // 13.01.2006 (Bugfix für MG)
    if (enableFrame(false,International.getString("efa berechnet die Statistik ..."),true)) {
      Thread thr = statistikThread.go(d);
      timer.start();
    }
  }

  synchronized boolean enableFrame(boolean enable, String text, boolean stopIfAlreadyDisabled) {
    if (!enable && stopIfAlreadyDisabled && !createButton.isEnabled()) {
      return false; // gibt false zurück, wenn Ausführung verboten ist
    }
    setEnabled(enable);
    createButton.setEnabled(enable);

    if (text != null) {
      createButton.setText(text);
      if (enable) {
        // TimeoutThread neu starten!
        try {
          if (timeoutThread != null && timeoutThread.isAlive()) timeoutThread.interrupt();
          timeoutThread = new TimeoutThread(this);
          timeoutThread.start();
        } catch(Exception e) {}

        createButton.setForeground(Color.blue);
        try {
          Thread.sleep(2000);
        } catch(Exception e) { EfaUtil.foo(); }
        createButton.setText(International.getString("Statistik erstellen"));
        createButton.setForeground(Color.black);
      }
    } else createButton.setText(International.getString("Statistik erstellen"));
    return true;
  }

    // Timer für ProgressBar-Aktualisierung
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (progressMonitor.isCanceled() || statistikThread.done()) {
                progressMonitor.close();
                statistikThread.stop();
                timer.stop();
                enableFrame(true,null,false);
            } else {
                progressMonitor.setNote(statistikThread.getMessage());
                progressMonitor.setMaximum(statistikThread.getLengthOfTask());
                progressMonitor.setProgress(statistikThread.getCurrent());
            }
        }
    }

}
