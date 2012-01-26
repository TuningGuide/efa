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

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

// @i18n complete
public abstract class BaseDialog extends JDialog implements ActionListener {

    protected Window _parent;
    protected String _title;
    protected String _closeButtonText;
    protected boolean _prepared = false;
    protected boolean _inCancel = false;
    private boolean doWindowStackChecks = true;

    private ActionHandler ah;
    protected String KEYACTION_ESCAPE;
    protected String KEYACTION_F1;
    protected JPanel basePanel = new JPanel();
    protected JScrollPane mainScrollPane = new JScrollPane();
    protected JPanel mainPanel = new JPanel();
    protected JButton closeButton;
    protected String helpTopic1;
    protected String helpTopic2;
    protected IItemType focusItem;
    protected boolean resultSuccess = false;

    public BaseDialog(Frame parent, String title, String closeButtonText) {
        super(parent);
        this._parent = parent;
        this._title = title;
        this._closeButtonText = closeButtonText;
    }

    public BaseDialog(JDialog parent, String title, String closeButtonText) {
        super(parent);
        this._parent = parent;
        this._title = title;
        this._closeButtonText = closeButtonText;
    }

    public boolean prepareDialog() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            iniDialogCommon(_title, _closeButtonText);
            iniDialog();
            iniDialogCommonFinish();
            EfaUtil.pack(this);
            _prepared = true;
            return true;
        } catch (Exception e) {
            Logger.log(e);
            return false;
        }
    }
    
    public void showMe() {
        showDialog();
    }

    protected void enableWindowStackChecks(boolean enable) {
        this.doWindowStackChecks = enable;
    }

    public void showDialog() {
        Daten.iniSplashScreen(false);
        if (!_prepared && !prepareDialog()) {
            return;
        }
        Dialog.setDlgLocation(this, _parent);
        setModal(true);
        if (doWindowStackChecks) {
            Dialog.frameOpened(this);
        }
        if (focusItem != null) {
            focusItem.requestFocus();
        }
        this.setVisible(true);
    }

    public void setRequestFocus(IItemType item) {
        if (item != null && isShowing()) {
            item.requestFocus();
        }
        focusItem = item;
    }

    public JDialog getParentJDialog() {
        if (_parent instanceof JDialog) {
            return (JDialog)_parent;
        }
        return null;
    }

    public Frame getParentFrame() {
        if (_parent instanceof Frame) {
            return (Frame)_parent;
        }
        return null;
    }

    public void _keyAction(ActionEvent evt) {
        if (evt == null || evt.getActionCommand() == null) {
            return;
        }

        if (evt.getActionCommand().equals(KEYACTION_ESCAPE)) {
            cancel();
        }

        if (evt.getActionCommand().equals(KEYACTION_F1)) {
            Help.showHelp(getHelpTopics());
        }
    }

    public abstract void keyAction(ActionEvent evt);

    public String addKeyAction(String key) {
        if (ah == null) {
            ah = new ActionHandler(this);
        }
        try {
            return ah.addKeyAction(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, key, "keyAction");
        } catch (NoSuchMethodException e) {
            Logger.log(Logger.ERROR, Logger.MSG_GUI_ERRORACTIONHANDLER, "Error setting up ActionHandler for "+getClass().getCanonicalName()+": "+e.toString()); // no need to translate
            return null;
        }
    }

    protected void iniDialogCommon(String title, String closeButtonText) throws Exception {
        helpTopic1 = getClass().getCanonicalName();
        helpTopic2 = getClass().getSuperclass().getCanonicalName();
        if (!helpTopic2.startsWith("de.nmichael.efa") || 
             helpTopic2.startsWith(BaseDialog.class.getCanonicalName())) {
            helpTopic2 = null;
        }
        if (Logger.isTraceOn(Logger.TT_HELP, 5)) {
            Logger.log(Logger.DEBUG, Logger.MSG_HELP_DEBUGHELPTOPICFRAMEOPENED, "Dialog Help Topic(s): "+helpTopic1 + (helpTopic2 != null ? " , " + helpTopic2 : ""));
        }

        KEYACTION_ESCAPE = addKeyAction("ESCAPE");
        KEYACTION_F1 = addKeyAction("F1");

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

        // intelligent sizing of this Dialog:
        // make it as big as necessary for display without scrollbars (plus some margin),
        // as long as it does not exceed the configured screen size.
        Dimension dim = mainPanel.getPreferredSize();
        Dimension minDim = mainPanel.getMinimumSize();
        if (minDim.width > dim.width) {
            dim.width = minDim.width;
        }
        if (minDim.height > dim.height) {
            dim.height = minDim.height;
        }
        if (dim.width < 100) {
            dim.width = 100;
        }
        if (dim.height < 50) {
            dim.height = 50;
        }
        dim.width  += mainScrollPane.getVerticalScrollBar().getPreferredSize().getWidth() + 40;
        dim.height += mainScrollPane.getHorizontalScrollBar().getPreferredSize().getHeight() + 20;
        mainScrollPane.setPreferredSize(Dialog.getMaxSize(dim));

        mainScrollPane.getViewport().add(mainPanel, null);
    }

    protected abstract void iniDialog() throws Exception;

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (cancel()) {
                // we don't need to call super.processWindowEvent(e) here!
                // otherwise we risk invoking cancel() a second time
                // super.processWindowEvent(e);
                return;
            } else {
                return;
            }
        }
        super.processWindowEvent(e);
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        cancel();
    }

    public boolean cancel() {
        _inCancel = true;
        if (doWindowStackChecks) {
            Dialog.frameClosed(this);
        }
        dispose();
        return true;
    }

    public void actionPerformed(ActionEvent e) {
    }

    // may be implemented by subclasses to take action when GUI needs to be set up new
    public void updateGui() {
    }

    protected void setDialogResult(boolean success) {
        this.resultSuccess = success;
    }

    public boolean getDialogResult() {
        return resultSuccess;
    }

    public JScrollPane getScrollPane() {
        return mainScrollPane;
    }

    public static ImageIcon getIcon(String name) {
        try {
            if (name.indexOf("/") < 0) {
                name = Daten.IMAGEPATH + name;
            }
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GUI_ICONS, "getIcon("+name+")");
            }
            return new ImageIcon(BaseDialog.class.getResource(name));
        } catch(Exception e) {
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GUI_ICONS, "getIcon("+name+"): no icon found!");
            }
            Logger.logdebug(e);
            return null;
        }
    }

    protected void setIcon(AbstractButton button, ImageIcon icon) {
        if (icon != null) {
            button.setIcon(icon);
        }
    }

    public String[] getHelpTopics() {
        return (helpTopic2 != null ? new String[] { helpTopic1, helpTopic2 } : new String[] { helpTopic1 });
    }

}
