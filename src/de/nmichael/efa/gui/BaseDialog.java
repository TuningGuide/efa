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

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

// @i18n complete
public abstract class BaseDialog extends JDialog implements ActionListener {

    Frame parent;
    JPanel basePanel = new JPanel();
    JScrollPane mainScrollPane = new JScrollPane();
    JPanel mainPanel = new JPanel();
    JButton closeButton;

    public BaseDialog(Frame parent, String title, String closeButtonText) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        Dialog.frameOpened(this);
        try {
            iniDialogCommon(title, closeButtonText);
            iniDialog();
            iniDialogCommonFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EfaUtil.pack(this);
        this.parent = parent;
    }

    public void keyAction(ActionEvent evt) {
        if (evt == null || evt.getActionCommand() == null) {
            return;
        }
        if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
            cancel();
        }
        if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
            Help.getHelp(this, this.getClass());
        }
    }

    protected void iniDialogCommon(String title, String closeButtonText) throws Exception {
        ActionHandler ah = new ActionHandler(this);
        try {
            ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                    new String[]{"ESCAPE", "F1"}, new String[]{"keyAction", "keyAction"});
        } catch (NoSuchMethodException e) {
            Logger.log(Logger.ERROR, Logger.MSG_GUI_ERRORACTIONHANDLER, "Error setting up ActionHandler"); // no need to translate
        }

        if (title != null) {
            setTitle(title);
        }
        basePanel.setLayout(new BorderLayout());
        if (closeButtonText != null) {
            closeButton = new JButton();
            Mnemonics.setButton(this, closeButton, closeButtonText);
            closeButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    closeButton_actionPerformed(e);
                }
            });
            basePanel.add(closeButton, BorderLayout.SOUTH);
        }
    }

    protected void iniDialogCommonFinish() {
        getContentPane().add(basePanel, null);
        basePanel.add(mainScrollPane, BorderLayout.CENTER);
        mainScrollPane.setPreferredSize(new Dimension(800, 600)); // @todo
        mainScrollPane.getViewport().add(mainPanel, null);
    }

    protected abstract void iniDialog() throws Exception;

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    void closeButton_actionPerformed(ActionEvent e) {
        cancel();
    }

    void cancel() {
        Dialog.frameClosed(this);
        dispose();
    }

    public void actionPerformed(ActionEvent e) {
    }

}
