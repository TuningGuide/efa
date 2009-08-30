package de.nmichael.efa.drv;

import de.nmichael.efa.util.Help;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.ActionHandler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.*;
import de.nmichael.efa.util.Dialog;


public class EmptyFrame extends JDialog implements ActionListener {
  Frame parent;


  public EmptyFrame(Frame parent) {
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


}
