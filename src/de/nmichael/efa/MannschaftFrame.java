package de.nmichael.efa;

import java.awt.*;
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

public class MannschaftFrame extends JDialog implements ActionListener {
  AuswahlFrame auswahlFrame;
  boolean neu;
  int editnr;
  JPanel mainPanel = new JPanel();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField boot = new JTextField();
  JTextField[] mannschaft = new JTextField[25];
  JButton saveButton = new JButton();
  JLabel jLabel2 = new JLabel();
  JTextField ziel = new JTextField();
  JLabel jLabel3 = new JLabel();
  JComboBox fahrtart = new JComboBox();
  JLabel jLabel4 = new JLabel();
  JComboBox obmann = new JComboBox();


  public MannschaftFrame(JDialog parent, String bootsname, AuswahlFrame auswahlFrame, int editnr) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.auswahlFrame = auswahlFrame;
    this.editnr = editnr;

    if (bootsname != null && bootsname.length()!=0) {
      boot.setText(bootsname);
      boot.setEnabled(false);
      if (Daten.mannschaften.getExact(bootsname) != null) {
        show(bootsname);
        neu = false;
      } else neu = true;
    } else neu = true;
    EfaUtil.pack(this);
    if (neu) boot.requestFocus();
    else mannschaft[0].requestFocus();;
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

    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setDisplayedMnemonic('B');
    jLabel1.setLabelFor(boot);
    jLabel1.setText("Mannschaft: ");
    Dialog.setPreferredSize(boot,200,19);
    boot.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        boot_keyReleased(e);
      }
    });
    boot.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        inputField_focusLost(e);
      }
    });
    this.setTitle("Standardmannschaften");
    saveButton.setNextFocusableComponent(boot);
    Dialog.setPreferredSize(saveButton,300,23);
    saveButton.setMnemonic('S');
    saveButton.setText("Eintrag speichern");
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveButton_actionPerformed(e);
      }
    });
    jLabel2.setDisplayedMnemonic('Z');
    jLabel2.setLabelFor(ziel);
    jLabel2.setText("Ziel: ");
    jLabel3.setDisplayedMnemonic('F');
    jLabel3.setLabelFor(fahrtart);
    jLabel3.setText("Fahrtart: ");
    ziel.setNextFocusableComponent(fahrtart);
    Dialog.setPreferredSize(ziel,200,19);
    ziel.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        ziel_keyReleased(e);
      }
    });
    ziel.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        inputField_focusLost(e);
      }
    });
    fahrtart.setNextFocusableComponent(obmann);
    Dialog.setPreferredSize(fahrtart,200,22);
    jLabel4.setDisplayedMnemonic('O');
    jLabel4.setLabelFor(obmann);
    jLabel4.setText("Obmann: ");
    obmann.setNextFocusableComponent(saveButton);
    jPanel1.add(jLabel1,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 20, 10, 0), 0, 0));
    jPanel1.add(boot,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 20), 0, 0));
    jPanel1.add(jLabel2,    new GridBagConstraints(0, 26, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    jPanel1.add(ziel,    new GridBagConstraints(1, 26, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
    jPanel1.add(jLabel3,     new GridBagConstraints(0, 27, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
    jPanel1.add(fahrtart,        new GridBagConstraints(1, 27, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 20), 0, 0));
    jPanel1.add(jLabel4,   new GridBagConstraints(0, 28, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
    jPanel1.add(obmann,   new GridBagConstraints(1, 28, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 20), 0, 0));
    this.getContentPane().add(saveButton, BorderLayout.SOUTH);

    for (int i=0; i<mannschaft.length; i++) {
      mannschaft[i] = new JTextField();
    }
    for (int i=0; i<mannschaft.length; i++) {
      JLabel label = new JLabel();
      label.setText( (i==0? "Steuermann: " : "Mannschaft "+i+": ") );
      Dialog.setPreferredSize(mannschaft[i],200,19);
      mannschaft[i].setMinimumSize(new Dimension(200, 19));
      jPanel1.add(label,   new GridBagConstraints(0, i+1, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, (i+1==mannschaft.length?20:0), 0), 0, 0));
      jPanel1.add(mannschaft[i],  new GridBagConstraints(1, i+1, 1, 1, 0.0, 0.0
              ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, (i+1==mannschaft.length?20:0), 20), 0, 0));
      if (i == 0) boot.setNextFocusableComponent(mannschaft[0]);
      if (i+1<mannschaft.length) mannschaft[i].setNextFocusableComponent(mannschaft[i+1]);
      else mannschaft[i].setNextFocusableComponent(ziel);
      mannschaft[i].addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          vervollstaendige(e);
        }
      });
      mannschaft[i].addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          inputField_focusLost(e);
        }
      });
    }
    mainPanel.setLayout(borderLayout1);
    mainPanel.add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);

    fahrtart.addItem(Mannschaften.NO_FAHRTART);
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.fahrtart != null) {
      for (int i=0; i<Daten.bezeichnungen.fahrtart.size()-1; i++)
        fahrtart.addItem(Daten.bezeichnungen.fahrtart.get(i));
    }
    fahrtart.setSelectedIndex(0);

    obmann.addItem(Mannschaften.NO_OBMANN);
    obmann.addItem("Steuermann");
    for (int i=1; i<=Fahrtenbuch.ANZ_MANNSCH; i++) {
      obmann.addItem("Nummer "+i);
    }
    obmann.setSelectedIndex(0);
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
    if (auswahlFrame != null) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void show(String name) {
    if (Daten.mannschaften.getExact(name) == null) return;
    DatenFelder d = (DatenFelder)Daten.mannschaften.getComplete();
    boot.setText(d.get(Mannschaften.BOOT));
    for (int i=0; i<mannschaft.length; i++)
      mannschaft[i].setText(d.get(i+Mannschaften.STM));
    ziel.setText(d.get(Mannschaften.ZIEL));
    fahrtart.setSelectedItem(d.get(Mannschaften.FAHRTART));
    obmann.setSelectedItem(d.get(Mannschaften.OBMANN));
  }


  void saveButton_actionPerformed(ActionEvent e) {
    String key = EfaUtil.removeSepFromString(boot.getText().trim());

    if (key.length() == 0) {
      Dialog.error("Der Bootsname darf nicht leer sein!");
      boot.requestFocus();
      return;
    }
    if (neu && Daten.mannschaften.getExact(key) != null) {
      Dialog.error("Der Bootsname existiert bereits!");
      boot.requestFocus();
      return;
    }

    if (!neu) Daten.mannschaften.delete(key);

    String s = key+"|";
    for (int i=0; i<mannschaft.length; i++)
      s+= EfaUtil.removeSepFromString(mannschaft[i].getText().trim())+"|";
    s+= EfaUtil.removeSepFromString(ziel.getText().trim())+"|";
    s+= EfaUtil.removeSepFromString(fahrtart.getSelectedItem().toString())+"|";
    s+= EfaUtil.removeSepFromString(obmann.getSelectedItem().toString())+"|";

    Daten.mannschaften.add(s);

    if (auswahlFrame != null) {
      auswahlFrame.doEdit(editnr+1);
      editnr = 0;
    } else {
      if (!Daten.mannschaften.writeFile())
        Dialog.error(Daten.mannschaften.getFileName()+" konnte nicht geschrieben werden.");
    }

    cancel();
  }

  void vervollstaendige(KeyEvent e) {
    JTextField field = (JTextField)e.getSource();
    if (Daten.fahrtenbuch.getDaten().mitglieder == null) return;
    if (field.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(field,null,Daten.fahrtenbuch.getDaten().mitglieder,e,null,true);
  }

  void boot_keyReleased(KeyEvent e) {
    JTextField field = (JTextField)e.getSource();
    if (Daten.fahrtenbuch.getDaten().boote == null) return;
    if (field.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(field,null,Daten.fahrtenbuch.getDaten().boote,e,null,true);
  }

  void ziel_keyReleased(KeyEvent e) {
    JTextField field = (JTextField)e.getSource();
    if (Daten.fahrtenbuch.getDaten().ziele == null) return;
    if (field.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(field,null,Daten.fahrtenbuch.getDaten().ziele,e,null,true);
  }

  void inputField_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
  }


}
