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
import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.beans.*;
import javax.swing.event.*;

// @i18n complete

public class SynonymFrame extends JDialog implements ActionListener {
  Synonyme synList;
  DatenListe baseList;
  Hashtable syn;
  boolean neuerEintrag;
  boolean changed;

  EfaFrame efaFrame;
  JButton closeButton = new JButton();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList synonymList = new JList();
  JButton neuButton = new JButton();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel2 = new JLabel();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField orgFeld = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField[] synFeld = new JTextField[10];
  JButton delButton = new JButton();
  JButton saveButton = new JButton();


  public SynonymFrame(EfaFrame parent, Synonyme synonyme, DatenListe datenliste) {
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
    this.efaFrame = parent;
    this.synList = synonyme;
    this.baseList = datenliste;
    changed = false;
    dataIni();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.showHelp(getClass().getCanonicalName());
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

    Mnemonics.setButton(this, closeButton, International.getStringWithMnemonic("Schließen"));
    closeButton.setNextFocusableComponent(synonymList);
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });
    jPanel1.setLayout(borderLayout1);
    Mnemonics.setButton(this, neuButton, International.getStringWithMnemonic("Neu") + " ...");
    neuButton.setNextFocusableComponent(delButton);
    neuButton.setPreferredSize(new Dimension(100, 23));
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neuButton_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout2);
    Mnemonics.setLabel(this, jLabel2, International.getStringWithMnemonic("vorhandene Einträge"));
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setLabelFor(synonymList);
    jScrollPane1.setPreferredSize(new Dimension(220, 131));
    jPanel3.setLayout(gridBagLayout1);
    Mnemonics.setLabel(this, jLabel1, International.getStringWithMnemonic("Hauptname")+": ");
    jLabel1.setLabelFor(orgFeld);
    orgFeld.setPreferredSize(new Dimension(200, 17));
    orgFeld.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        vervollstaendige(e);
      }
    });
    orgFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        inputField_focusLost(e);
      }
    });

    jLabel3.setText(International.getString("Synonyme")+": ");
    for (int i=0; i<synFeld.length; i++) {
      synFeld[i] = new JTextField();
      synFeld[i].setPreferredSize(new Dimension(200, 17));
      synFeld[i].addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          vervollstaendige(e);
        }
      });
      synFeld[i].addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          inputField_focusLost(e);
        }
      });
      if (i>0) synFeld[i-1].setNextFocusableComponent(synFeld[i]);
    }
    synFeld[synFeld.length-1].setNextFocusableComponent(saveButton);
    orgFeld.setNextFocusableComponent(synFeld[0]);
    this.setTitle(International.getString("Synonyme"));
    synonymList.setNextFocusableComponent(neuButton);
    synonymList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    synonymList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        synonymList_valueChanged(e);
      }
    });
    delButton.setNextFocusableComponent(orgFeld);
    delButton.setPreferredSize(new Dimension(100, 23));
    Mnemonics.setButton(this, delButton, International.getStringWithMnemonic("Löschen"));
    delButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        delButton_actionPerformed(e);
      }
    });
    Mnemonics.setButton(this, saveButton, International.getStringWithMnemonic("Speichern"));
    saveButton.setNextFocusableComponent(closeButton);
    saveButton.setPreferredSize(new Dimension(200, 23));
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(closeButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1,  BorderLayout.EAST);
    jPanel1.add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(jLabel1,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    jPanel3.add(orgFeld,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(jLabel3,       new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    jPanel3.add(saveButton,       new GridBagConstraints(0, 15, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    for (int i=0; i<synFeld.length; i++)
      jPanel3.add(synFeld[i], new GridBagConstraints(1, i+1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(jPanel2,  BorderLayout.WEST);
    jPanel2.add(jScrollPane1,      new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 123));
    jPanel2.add(neuButton,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel2,     new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
    jPanel2.add(delButton,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(synonymList, null);
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
    if (changed) {
      int c = Dialog.yesNoCancelDialog(International.getString("Änderungen speichern"),
              International.getString("Möchtest Du die Änderungen jetzt speichern?"));
      if (c == Dialog.CANCEL) return;
      if (c == Dialog.YES) {
        synList.removeAllSyns();
        Object[] keys = syn.keySet().toArray();
        for (int i=0; i<keys.length; i++) {
          Vector v = (Vector)syn.get((String)keys[i]);
          if (v != null) {
            for (int j=0; j<v.size(); j++) {
              DatenFelder d = new DatenFelder(synList._FELDERANZAHL);
              d.set(Synonyme.ORIGINAL,(String)keys[i]);
              d.set(Synonyme.SYNONYM,(String)v.get(j));
              synList.add(d);
            }
          }
        }
        if (!synList.writeFile())
          Dialog.error(International.getString("Fehler beim Speichern der Daten!"));
      }
    }
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void dataIni() {
    DatenFelder d;
    Vector v;
    syn = new Hashtable();
    d = (DatenFelder)synList.getCompleteFirst();
    while (d != null) {
      String org = d.get(Synonyme.ORIGINAL);
      if ( (v = (Vector)syn.get(org)) == null) {
        v = new Vector();
      }
      v.add(d.get(Synonyme.SYNONYM));
      syn.put(org,v);
      d = (DatenFelder)synList.getCompleteNext();
    }
    updateSynList();

    setNeuerEintrag(true);
  }

  void updateSynList() {
    Object[] keys = syn.keySet().toArray();
    Arrays.sort(keys);
    synonymList.setListData(keys);
  }

  void setNeuerEintrag(boolean neu) {
    if (neu) {
      saveButton.setText(International.getString("Neuen Eintrag hinzufügen"));
      neuerEintrag = true;
      synonymList.clearSelection();
      orgFeld.setText("");
      for (int i=0; i<synFeld.length; i++) synFeld[i].setText("");
      orgFeld.requestFocus();
    } else {
      saveButton.setText(International.getString("Änderungen speichern"));
      neuerEintrag = false;
    }
  }

  void synonymList_valueChanged(ListSelectionEvent e) {
    if (syn == null) return;
    if (synonymList.isSelectionEmpty()) return;
    String org = (String)synonymList.getSelectedValue();
    if (org == null) return;
    Vector v = (Vector)syn.get(org);
    orgFeld.setText(org);
    if (v == null) return;
    for (int i=0; i<synFeld.length; i++)
      if (i<v.size()) synFeld[i].setText((String)v.get(i));
      else synFeld[i].setText("");
    setNeuerEintrag(false);
  }

  void neuButton_actionPerformed(ActionEvent e) {
    setNeuerEintrag(true);
  }

  void saveButton_actionPerformed(ActionEvent e) {
    if (orgFeld.getText().trim().length() == 0) {
      Dialog.error(International.getMessage("Das Feld '{fieldname}' darf nicht leer sein!",
                   International.getString("Hauptname")));
      return;
    }


    if (!neuerEintrag) {
      String oldorg = (String)synonymList.getSelectedValue();
      if (oldorg != null) syn.remove(oldorg);
    }

    String org = orgFeld.getText();
    Vector v = null;
    if ( neuerEintrag && (v = (Vector)syn.get(org)) != null) {
      Dialog.error(International.getString("Ein Eintrag mit demselben Hauptnamen existiert bereits!"));
      return;
    }
    if (v == null) v = new Vector();

    String s;
    for (int i=0; i<synFeld.length; i++)
      if ( (s = synFeld[i].getText().trim()).length()>0) v.add(s);
    syn.put(org,v);
    updateSynList();
    synonymList.setSelectedValue(org,true);
    setNeuerEintrag(false);
    changed = true;
  }

  void delButton_actionPerformed(ActionEvent e) {
    if (synonymList.isSelectionEmpty()) {
      Dialog.error(International.getString("Was möchtest Du löschen? Wähle bitte einen Eintrag aus!"));
      return;
    }
    String org = (String)synonymList.getSelectedValue();
    if (syn.get(org) == null) { // sollte eigentlich nicht möglich sein..... ;-)
      Dialog.error(International.getString("Oops!") + " " +
              International.getMessage("Der Eintrag '{record}' existiert gar nicht!",org));
      return;
    }

    if (Dialog.yesNoCancelDialog(International.getString("Eintrag löschen"),
            International.getMessage("Möchtest Du alle Synonyme für '{name}' wirklich löschen?",org)) == Dialog.YES) {
      syn.remove(org);
      updateSynList();
      synonymList.clearSelection();
      setNeuerEintrag(true);
      changed = true;
    }
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void vervollstaendige(KeyEvent e) {
    if (baseList == null) return;
    efaFrame.vervollstaendige((JTextField)e.getSource(),null,baseList,e,null,true);

  }

  void inputField_focusLost(FocusEvent e) {
    if (Daten.efaConfig.popupComplete.getValue()) AutoCompletePopupWindow.hideWindow();
  }

}
