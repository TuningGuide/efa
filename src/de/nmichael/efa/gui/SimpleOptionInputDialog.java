/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.EfaMouseListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

// @i18n complete
public class SimpleOptionInputDialog extends SimpleInputDialog implements IItemListener {

    public static final int OPTION_OK = 0;
    public static final int OPTION_CANCEL = 1;
    public static final int OPTION_NONE = 2;


    private String[] optionButtonText;
    private int[] optionButtonAction;
    private Hashtable<JButton,Integer> buttonActions = new Hashtable<JButton,Integer>();

    public SimpleOptionInputDialog(Frame parent, String title, IItemType item,
            String[] optionButtonText, int[] optionButtonAction) {
        super(parent, title, item);
        ini(item, optionButtonText, optionButtonAction);
    }

    public SimpleOptionInputDialog(JDialog parent, String title, IItemType item,
            String[] optionButtonText, int[] optionButtonAction) {
        super(parent, title, item);
        ini(item, optionButtonText, optionButtonAction);
    }

    private void ini(IItemType item, String[] optionButtonText, int[] optionButtonAction) {
        this.optionButtonText = optionButtonText;
        this.optionButtonAction = optionButtonAction;
        this._closeButtonText = null;
         item.registerItemListener(this);

    }


    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        super.iniDialog();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        for (int i=0; i<optionButtonText.length; i++) {
            JButton button = new JButton();
            button.setText(optionButtonText[i]);
            button.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    optionButtonAction(e);
                }
            });
            buttonPanel.add(button);
            buttonActions.put(button, optionButtonAction[i]);
        }
        basePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void optionButtonAction(ActionEvent e) {
        if (e != null) {
            try {
                int action = buttonActions.get(e.getSource());
                switch(action) {
                    case OPTION_OK:
                        closeButton_actionPerformed(e);
                        return;
                    case OPTION_CANCEL:
                        cancel();
                        return;
                }
            } catch(Exception eignore) {
                Logger.logdebug(eignore);
            }
        }
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == super.item && event != null &&
            event instanceof ActionEvent) {
            if (((ActionEvent)event).getActionCommand().equals(EfaMouseListener.EVENT_MOUSECLICKED_2x)) {
                closeButton_actionPerformed((ActionEvent)event);
            }
        }
    }


    public static boolean showOptionInputDialog(JDialog parent, String title, IItemType item,
            String[] optionButtonText, int[] optionButtonAction) {
        SimpleOptionInputDialog dlg = new SimpleOptionInputDialog(parent, title, item,
                optionButtonText, optionButtonAction);
        dlg.showDialog();
        return dlg.resultSuccess;
    }

    public static boolean showOptionInputDialog(JFrame parent, String title, IItemType item,
            String[] optionButtonText, int[] optionButtonAction) {
        SimpleOptionInputDialog dlg = new SimpleOptionInputDialog(parent, title, item,
                optionButtonText, optionButtonAction);
        dlg.showDialog();
        return dlg.resultSuccess;
    }

    public static boolean showOptionInputDialog(Window parent, String title, IItemType item,
            String[] optionButtonText, int[] optionButtonAction) {
        if (parent instanceof JDialog) {
            return showOptionInputDialog((JDialog)parent, title, item,
                optionButtonText, optionButtonAction);
        } else {
            return showOptionInputDialog((JFrame)parent, title, item,
                optionButtonText, optionButtonAction);
        }
    }

}
