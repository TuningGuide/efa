package de.nmichael.efa.eddi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class ZielFehlerFrame extends JDialog implements ActionListener {
  String[] felder;
  Hashtable replStegziel;
  EddiFrame parent;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel eintragLabel = new JLabel();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField name = new JTextField();
  JTextField kilometer = new JTextField();
  JTextArea fehlerText = new JTextArea();
  JTextField zielbereiche = new JTextField();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JComboBox stegziel = new JComboBox();
  JTextField gewaesser = new JTextField();
  JCheckBox stegzielReplBox = new JCheckBox();
  JButton skipButton = new JButton();


  public ZielFehlerFrame(EddiFrame parent, String eintrag, String fehler, String[] felder, Hashtable replStegziel) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);

    this.parent = parent;
    this.felder = felder;
    this.replStegziel = replStegziel;


    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    this.eintragLabel.setText(eintrag);
    this.fehlerText.append(fehler);
    this.name.setText(felder[Ziele.NAME]);
    this.kilometer.setText(felder[Ziele.KM]);
    this.zielbereiche.setText(felder[Ziele.BEREICH]);
    if (felder[Ziele.STEGZIEL].length()==0) this.stegziel.setSelectedIndex(2);
    else if (felder[Ziele.STEGZIEL].equals("+")) this.stegziel.setSelectedIndex(1);
    else this.stegziel.setSelectedIndex(0);
    this.gewaesser.setText(felder[Ziele.GEWAESSER]);
    this.name.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      parent.abort = true;
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

    this.setSize(new Dimension(700,520));
    this.setTitle("Fehler in zu importierendem Eintrag");
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setText("Zu importierender Eintrag:");
    eintragLabel.setBackground(Color.blue);
    eintragLabel.setForeground(Color.white);
    eintragLabel.setOpaque(true);
    eintragLabel.setText("---Eintrag---");
    okButton.setNextFocusableComponent(name);
    okButton.setMnemonic('O');
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout2);
    jLabel2.setDisplayedMnemonic('Z');
    jLabel2.setLabelFor(name);
    jLabel2.setText("Zielbezeichnung: ");
    jLabel3.setDisplayedMnemonic('K');
    jLabel3.setLabelFor(kilometer);
    jLabel3.setText("Kilometer: ");
    jLabel4.setDisplayedMnemonic('B');
    jLabel4.setLabelFor(zielbereiche);
    jLabel4.setText("Zielbereiche: ");
    name.setNextFocusableComponent(kilometer);
    name.setPreferredSize(new Dimension(300, 17));
    kilometer.setNextFocusableComponent(zielbereiche);
    kilometer.setPreferredSize(new Dimension(300, 17));
    fehlerText.setBackground(new Color(204, 204, 204));
    fehlerText.setFont(new java.awt.Font("Dialog", 1, 12));
    fehlerText.setForeground(Color.red);
    fehlerText.setEditable(false);
    zielbereiche.setNextFocusableComponent(okButton);
    zielbereiche.setPreferredSize(new Dimension(300, 17));
    jLabel5.setText("Start/Ziel ist Bootshaus: ");
    jLabel6.setText("Gewässer: ");
    skipButton.setText("Eintrag überspringen");
    skipButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        skipButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(eintragLabel,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(fehlerText,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    this.getContentPane().add(okButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jLabel2,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel3,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel4,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(name,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(kilometer,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(zielbereiche,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel5,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel6,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(stegziel,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(gewaesser,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(stegzielReplBox, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(skipButton,    new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    stegziel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stegziel_actionPerformed(e);
      }
    });
    stegziel.addItem("ungültiger Wert");
    stegziel.addItem("ja");
    stegziel.addItem("nein");
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      parent.abort = true;
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


  void okButton_actionPerformed(ActionEvent e) {
    felder[Ziele.NAME] = this.name.getText().trim();
    felder[Ziele.KM] = this.kilometer.getText().trim();
    felder[Ziele.BEREICH] = this.zielbereiche.getText().trim();
    if (this.stegzielReplBox.isSelected() && this.stegzielReplBox.isEnabled()) {
      if (this.stegziel.getSelectedIndex()==1) replStegziel.put(felder[Ziele.STEGZIEL],"+");
      if (this.stegziel.getSelectedIndex()==2) replStegziel.put(felder[Ziele.STEGZIEL],"");
    }
    if (this.stegziel.getSelectedIndex()==1) felder[Ziele.STEGZIEL] = "+";
    else if (this.stegziel.getSelectedIndex()==2) felder[Ziele.STEGZIEL] = "";
    else felder[Ziele.STEGZIEL] = "?";
    felder[Ziele.GEWAESSER] = this.gewaesser.getText().trim();


    cancel();
  }

  void stegziel_actionPerformed(ActionEvent e) {
    if (stegziel.getSelectedIndex()==0) this.stegzielReplBox.setEnabled(false);
    else this.stegzielReplBox.setEnabled(true);
    if (stegziel.getSelectedIndex()>0) this.stegzielReplBox.setText("'"+felder[Ziele.STEGZIEL]+"' immer durch '"+stegziel.getSelectedItem()+"' ersetzen");
  }

  void skipButton_actionPerformed(ActionEvent e) {
    parent.skip = true;
    cancel();
  }

}
