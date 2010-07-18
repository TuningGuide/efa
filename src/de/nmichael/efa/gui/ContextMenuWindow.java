/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import de.nmichael.efa.Daten;
import de.nmichael.efa.util.Mnemonics;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Logger;

// @i18n complete

public class ContextMenuWindow extends JWindow {
    
    private Window parent;
    private IContextMenuWindowCallback callback;
    private int _buttonID = 0;
    private Hashtable<JButton,Integer> buttons = new Hashtable<JButton,Integer>();
    private boolean showing = false;

    public ContextMenuWindow(Window parent, IContextMenuWindowCallback callback, String[] buttonTxt) {
        super(parent);
        this.parent = parent;
        this.callback = callback;
        try {
            getContentPane().setLayout(new GridBagLayout());
            for (int i=0; i<buttonTxt.length; i++) {
                addButton(buttonTxt[i]);
            }
            pack();
            // Bugfix: AutoCompletePopupWindow muß unter Windows ebenfalls alwaysOnTop sein, wenn EfaDirektFrame alwaysOnTop ist, da sonst die Popup-Liste nicht erscheint
            if (Daten.osName.startsWith("Windows") && Daten.efaConfig.efaDirekt_immerImVordergrund.getValue()) {
                de.nmichael.efa.java15.Java15.setAlwaysOnTop(this, true);
            }
        if (Logger.isTraceOn(Logger.TT_GUI)) {
            Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_GUI_CONTEXTMENU,"ContextMenu "+this.getClass().getCanonicalName()+" successfully created.");
        }
        } catch (Exception e) {
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_GUI_CONTEXTMENU,"ContextMenu "+this.getClass().getCanonicalName()+" failed to create: " + e.toString());
            }
        }
    }

    private void addButton(String txt) {
        JButton button = new JButton();
        Mnemonics.setButton(this, button, txt);
        Dialog.setPreferredSize(button, 200, 19);
        button.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {
                buttonActionPerformed(e);
            }
        });
        buttons.put(button, _buttonID);
        getContentPane().add(button,
                new GridBagConstraints(0, _buttonID++, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void buttonActionPerformed(ActionEvent e) {
        Integer buttonID = (e != null ? buttons.get(e.getSource()) : null);
        if (buttonID != null) {
            callback.contextMenuWindow_actionPerformed(buttonID.intValue());
            hideWindow();
            try {
                Dialog.frameCurrent().toFront();
            } catch (Exception ee) {
            }
    }
    }

    public void showWindow(int x, int y) {
        try {
            this.setLocation(x, y);
            this.show();
            // Unter Windows bewirkt toFront(), daß der ursprüngliche Frame den Fokus verliert, daher muß unter Windows darauf verzichtet werden
            if (!Daten.osName.startsWith("Windows")) {
                this.toFront();
            }
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GUI_CONTEXTMENU, "ContextMenu " + this.getClass().getCanonicalName() + " is now showing.");
            }
        } catch (Exception e) {
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GUI_CONTEXTMENU, "ContextMenu " + this.getClass().getCanonicalName() + " failed to show: " + e.toString());
            }
        }
        showing = true;
    }
    
    public void doHide() {
        if (showing) {
            this.hide();
        }
        showing = false;
        if (Logger.isTraceOn(Logger.TT_GUI)) {
            Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_GUI_CONTEXTMENU,"ContextMenu "+this.getClass().getCanonicalName()+" is now hidden.");
        }
    }

    public void hideWindow() {
        try {
            new HideWindowThread(this).start();
        } catch (Exception e) {
        }
    }
    
    class HideWindowThread extends Thread {
        private ContextMenuWindow window;

        public HideWindowThread(ContextMenuWindow window) {
            this.window = window;
        }

        public void run() {
            try {
                Thread.sleep(10);
                window.doHide();
            } catch (Exception e) {
            }
        }

    }

}
