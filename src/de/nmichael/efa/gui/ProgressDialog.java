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
import de.nmichael.efa.util.Dialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProgressDialog extends BaseDialog {

    private ProgressTask progressTask;
    private JTextArea loggingTextArea;
    private JProgressBar progressBar;

    public ProgressDialog(Frame parent, String title, ProgressTask progressTask, boolean autoCloseDialogWhenDone) {
        super(parent, title, International.getStringWithMnemonic("Schließen"));
        this.progressTask = progressTask;
        progressTask.setProgressDialog(this, autoCloseDialogWhenDone);
    }

    public ProgressDialog(JDialog parent, String title, ProgressTask progressTask, boolean autoCloseDialogWhenDone) {
        super(parent, title, International.getStringWithMnemonic("Schließen"));
        this.progressTask = progressTask;
        progressTask.setProgressDialog(this, autoCloseDialogWhenDone);
    }

    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new BorderLayout());

        JScrollPane loggingScrollPane = new JScrollPane();
        loggingScrollPane.setPreferredSize(new Dimension(550,200));
        loggingScrollPane.setMinimumSize(new Dimension(550,200));
        loggingTextArea = new JTextArea();
        loggingTextArea.setEditable(false);
        //loggingTextArea.setWrapStyleWord(true);
        //loggingTextArea.setLineWrap(true);
        loggingScrollPane.getViewport().add(loggingTextArea, null);
        mainPanel.add(loggingScrollPane, BorderLayout.CENTER);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridBagLayout());
        JLabel progressLabel = new JLabel();
        progressLabel.setText(International.getString("Fortschritt")+":");
        progressPanel.add(progressLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                    ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressPanel.add(progressBar,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                    ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void logInfo(String s) {
        loggingTextArea.append(s);
    }

    public void setCurrentWorkDone(int i) {
        progressBar.setMaximum(progressTask.getAbsoluteWork());
        progressBar.setValue(i);
    }

    public boolean cancel() {
        boolean _cancel = false;
        if (progressTask.isRunning()) {
            if (Dialog.yesNoDialog(International.getString("Abbruch"),
                    International.getString("Möchtest Du den Vorgang wirklich abbrechen?")) == Dialog.YES) {
                _cancel = true;
            }
        } else {
            _cancel = true;
        }
        if (_cancel) {
            progressTask.abort();
            return super.cancel();
        } else {
            return false;
        }
    }

}