/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// @i18n complete

public class AutoCompletePopupWindow extends JWindow {
  private static AutoCompletePopupWindow window = null;

  private Hashtable datenlisten = new Hashtable();
  private Hashtable datenlistenSCN = new Hashtable();
  private JTextField showingAt;
  private HideWindowThread hideWindowThread;
  private AutoCompletePopupWindowCallback callback;

  BorderLayout borderLayout = new BorderLayout();
  JScrollPane scrollPane = new JScrollPane();
  JList list = new JList();



  private AutoCompletePopupWindow() {
    try {
      jbInit();
      setListSize(200,100);
      // Bugfix: AutoCompletePopupWindow muß unter Windows ebenfalls alwaysOnTop sein, wenn EfaDirektFrame alwaysOnTop ist, da sonst die Popup-Liste nicht erscheint
      if (Daten.osName.startsWith("Windows") && Daten.efaConfig.efaDirekt_immerImVordergrund.getValue()) de.nmichael.efa.java15.Java15.setAlwaysOnTop(this,true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void addMouseListeners(JScrollBar scrollBar) {
    if (scrollBar == null) return;
    scrollBar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        mousePressedEvent(e);
      }
    });
    // neben der Scrollbar selbst auch die Scrollbuttons (Pfeile) mit Listenern versorgen!
    Component[] c = scrollBar.getComponents();
    for (int i=0; c != null && i<c.length; i++) {
      try {
        scrollBar.getComponent(i).addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            mousePressedEvent(e);
          }
        });
      } catch(Exception e) {
      }
    }
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    scrollPane.getViewport().add(list, null);

    addMouseListeners(scrollPane.getHorizontalScrollBar());
    addMouseListeners(scrollPane.getVerticalScrollBar());
    list.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        listEntrySelected(e);
      }
      public void mousePressed(MouseEvent e) {
        mousePressedEvent(e);
      }
    });
  }

  public void setListSize(int x, int y) {
    this.scrollPane.setPreferredSize(new Dimension(x,y));
    this.pack();
  }

  private int setListData(DatenListe liste) {
    String[] data = (String[])datenlisten.get(liste);
    Integer scn = (Integer)datenlistenSCN.get(liste);
    if (data == null || scn == null || scn.intValue() != liste.getSCN()) {
      liste.saveLastElement();
      data = liste.getData();
      liste.restoreLastElement();
      datenlisten.put(liste,data);
      datenlistenSCN.put(liste,new Integer(liste.getSCN()));
    }
    this.list.setListData(data);
    return data.length;
  }

  private void showAtTextField(JTextField field) {
    if (showingAt == field) {
      // Unter Windows bewirkt toFront(), daß der ursprüngliche Frame den Fokus verliert, daher muß unter Windows darauf verzichtet werden
      if (!Daten.osName.startsWith("Windows")) this.toFront();
      return;
    }
    try {
      int x = (int)field.getLocationOnScreen().getX() + 10;
      int y = (int)field.getLocationOnScreen().getY() + field.getHeight();
      setListSize(field.getWidth(),field.getHeight()*5);
      this.setLocation(x,y);
      this.show();
      // Unter Windows bewirkt toFront(), daß der ursprüngliche Frame den Fokus verliert, daher muß unter Windows darauf verzichtet werden
      if (!Daten.osName.startsWith("Windows")) this.toFront();
      showingAt = field;
    } catch(Exception ee) { // nur zur Sicherheit: Es gibt seltene Exceptions in efa, die keiner Stelle im Code zugeordnet werden können und hierher kommen könnten
    }
  }

  public void doHide() {
    if (showingAt != null) this.hide();
    showingAt = null;
  }

  private void selectEintrag(String eintrag) {
    list.setSelectedValue(eintrag,true);
    try {
      list.scrollRectToVisible(list.getCellBounds(list.getSelectedIndex()-1,list.getSelectedIndex()+1));
    } catch(Exception e) {
    }
  }

  private void listEntrySelected(MouseEvent e) {
    if (showingAt != null) {
      try {
        String s = (String)list.getSelectedValue();
        if (s != null) showingAt.setText(s);

        if (callback != null) callback.acpwCallback(showingAt);
      } catch(Exception ee) {
      }
      doHide();
      try {
        Dialog.frameCurrent().toFront();
      } catch(Exception ee) {
      }
    }
  }

  private void mousePressedEvent(MouseEvent e) {
    try {
      if (hideWindowThread != null) {
        hideWindowThread.interrupt();
      }
    } catch(Exception ee) {}
  }

  public static void showAndSelect(JTextField field, DatenListe liste, String eintrag, AutoCompletePopupWindowCallback callback) {
    try {
      if (window == null) {
        window = new AutoCompletePopupWindow();
      }
      window.callback = callback;
      if (window.setListData(liste) == 0) return;
      window.showAtTextField(field);
      window.selectEintrag(eintrag);
    } catch(Exception e) {
    }
  }

  public static void hideWindow() {
    try {
      if (window != null) {
        window.hideWindowThread = new HideWindowThread(window);
        window.hideWindowThread.start();
      }
    } catch(Exception e) {
    }
  }

  public static boolean isShowingAt(JTextField field) {
    try {
      if (window != null) {
        if (window.showingAt == field) return true;
      }
    } catch(Exception e) {
    }
    return false;
  }

  public static AutoCompletePopupWindow getWindow() {
    return window;
  }


}

class HideWindowThread extends Thread {
  private AutoCompletePopupWindow window;

  public HideWindowThread(AutoCompletePopupWindow window) {
    this.window = window;
  }

  public void run() {
    try {
      Thread.sleep(100);
      window.doHide();
    } catch(Exception e) {
    }
  }

}