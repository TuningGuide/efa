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

import de.nmichael.efa.gui.ProgressDialog;

public abstract class ProgressTask extends Thread {

    protected ProgressDialog progressDialog;
    protected volatile boolean running = false;
    protected int currentWorkDone = 0;

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void abort() {
        running = false;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setDone() {
        setRunning(false);
        setCurrentWorkDone(getAbsoluteWork());
        Dialog.infoDialog(getSuccessfullyDoneMessage());
        progressDialog.cancel();
    }

    public boolean isRunning() {
        return running;
    }

    public abstract int getAbsoluteWork();

    public int getCurrentWorkDone() {
        return currentWorkDone;
    }

    public void logInfo(String s) {
        progressDialog.logInfo(s);
    }

    public void setCurrentWorkDone(int i) {
        progressDialog.setCurrentWorkDone(i);
    }

    public abstract String getSuccessfullyDoneMessage();




}
