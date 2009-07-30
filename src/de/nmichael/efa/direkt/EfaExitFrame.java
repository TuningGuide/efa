package de.nmichael.efa.direkt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.Dialog;
import de.nmichael.efa.*;

public class EfaExitFrame extends JFrame implements ActionListener {

  private static EfaExitFrame dlg = null;

  private boolean restart;
  private int who;
  private CountdownThread thread;
  EfaDirektFrame efaDirektFrame;
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel0 = new JLabel();
  JLabel jLabel1 = new JLabel();
  JLabel sekundenLabel = new JLabel();
  JLabel jLabel3 = new JLabel();
  JButton dontExitButton = new JButton();

  private EfaExitFrame(EfaDirektFrame efaDirektFrame) {
    this.efaDirektFrame = efaDirektFrame;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
    thread = new CountdownThread(this);
  }

  private void activateExitFrame(String reason) {
    jLabel0.setText(reason);
    this.dontExitButton.requestFocus();
    Dialog.frameOpened(dlg);
    dlg.show();
    if (thread == null) {
      thread = new CountdownThread(this);
    }
    thread.start();
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
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }
    jPanel1.setBackground(Color.red);
    jPanel1.setLayout(gridBagLayout1);
    jLabel0.setFont(new java.awt.Font("Dialog", 1, 18));
    jLabel0.setForeground(Color.black);
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 18));
    jLabel1.setForeground(Color.black);
    jLabel1.setText("efa wird in ");
    sekundenLabel.setFont(new java.awt.Font("Dialog", 1, 18));
    sekundenLabel.setForeground(Color.black);
    sekundenLabel.setText("10");
    jLabel3.setFont(new java.awt.Font("Dialog", 1, 18));
    jLabel3.setForeground(Color.black);
    jLabel3.setText(" Sekunden automatisch beendet ...");
    jLabel0.setText(" --- Grund --- ");
    dontExitButton.setText("efa noch nicht beenden");
    dontExitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dontExitButton_actionPerformed(e);
      }
    });
    this.setTitle("Automatisches Beenden von efa");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel0,     new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 20, 0, 20), 0, 0));
    jPanel1.add(jLabel1,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 20, 0, 0), 0, 0));
    jPanel1.add(sekundenLabel,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel3,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 20), 0, 0));
    jPanel1.add(dontExitButton,     new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(50, 20, 20, 20), 0, 0));
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel(false);
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel(boolean _exit) {
    thread.stopRunning = true;
    try {
      thread.interrupt();
      thread.join();
    } catch(InterruptedException e) {}
    Dialog.frameClosed(this);
    this.hide();
    if (_exit) {
      thread = null;
      Logger.log(Logger.INFO,"efa beendet sich jetzt" + (restart ? " und wird anschließend neu gestartet." : "."));
      efaDirektFrame.cancel(null,who,restart);
    } else {
      thread = new CountdownThread(this); // Thread für's nächste Mal initialisieren
      Logger.log(Logger.WARNING,"Beenden von efa wurde durch Benutzer abgebrochen.");
      Daten.DONT_SAVE_ANY_FILES_DUE_TO_OOME = false;
    }
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  void dontExitButton_actionPerformed(ActionEvent e) {
    cancel(false);
  }



  class CountdownThread extends Thread {

    public boolean stopRunning;
    int left;
    EfaExitFrame frame;
    String[] secLeft = new String[10];

    public CountdownThread(EfaExitFrame frame) {
      this.frame = frame;
      stopRunning = false;
      for (int i=0; i<10; i++) {
        secLeft[i] = Integer.toString(10-i);
      }
    }

    public void run() {
      for (int i=0; i<10; i++) {
        frame.sekundenLabel.setText(secLeft[i]);
        try { Thread.sleep(1000); } catch(InterruptedException e) { }
        if (stopRunning) return;
      }
      frame.cancel(true);
    }
  }



  public static void initExitFrame(EfaDirektFrame frame) {
    dlg = new EfaExitFrame(frame);
    Dialog.setDlgLocation(dlg);
  }



  public static void exitEfa(String reason, boolean restart, int who) {
    if (dlg == null) return;
    if (dlg.thread != null && dlg.thread.isAlive()) return; // doppelter Aufruf
    dlg.restart = restart;
    dlg.who = who;
    dlg.activateExitFrame(reason);
  }

}


