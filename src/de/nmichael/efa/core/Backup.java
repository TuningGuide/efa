/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.Admins;
import de.nmichael.efa.core.config.EfaConfig;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.storage.IDataAccess;
import de.nmichael.efa.data.storage.StorageObject;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.gui.ProgressDialog;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.ProgressTask;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

public class Backup {

    private String backupDir;
    private boolean backupProject;
    private boolean backupConfig;
    private String zipFile;
    private String lastErrorMsg;

    private BackupTask backupTask;
    private int totalWork = 0;
    private int totalWorkDone = 0;

    public Backup(String backupDir,
            boolean backupProject,
            boolean backupConfig) {
        this.backupDir = backupDir;
        this.backupProject = backupProject;
        this.backupConfig = backupConfig;
    }

    private int backupStorageObjects(IDataAccess[] dataAccesses,
            ZipOutputStream zipOut, String dir) {
        int successful = 0;
        for (IDataAccess data : dataAccesses) {
            try {
                data.saveToZipFile(dir, zipOut);
                successful++;
                logMsg(Logger.INFO, Logger.MSG_BACKUP_BACKUPINFO,
                        International.getMessage("Objekt {name} gesichert.", data.getUID()));
            } catch (Exception e) {
                logMsg(Logger.ERROR, Logger.MSG_BACKUP_BACKUPERROR,
                        International.getMessage("Sicherung von Objekt {name} fehlgeschlagen: {reason}",
                        data.getUID(), e.toString()));
                Logger.logdebug(e);

            }
        }
        if (backupTask != null) {
            backupTask.setCurrentWorkDone(++totalWorkDone);
        }
        return successful;
    }

    // backupTask is null for CLI backup, and set for GUI Backup
    public boolean runBackup(BackupTask backupTask) {
        this.backupTask = backupTask;
        lastErrorMsg = null;
        try {
            if (Daten.project == null || !Daten.project.isOpen() ||
                    (!backupProject && !backupConfig)) {
                return false;
            }

            IDataAccess projectDataAccess = Daten.project.data();
            String projectName = Daten.project.getProjectName();
            if (Daten.project.getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE) {
                projectDataAccess = Daten.project.getRemoteDataAccess();
                projectName = Daten.project.getProjectRemoteProjectName();
            }

            logMsg(Logger.INFO, Logger.MSG_BACKUP_BACKUPSTARTED,
                    (backupProject && backupConfig ?
                        International.getMessage("Starte Backup von Projekt {projekt} und efa-Konfiguration ...",
                        projectName) :
                    (backupProject ?
                        International.getMessage("Starte Backup von Projekt {projekt} ...",
                        projectName) :
                    (backupConfig ?
                        International.getString("Starte Backup von efa-Konfiguration ...") :
                        "ERROR"))));

            if (backupDir.length() > 0 && !backupDir.endsWith(Daten.fileSep)) {
                backupDir += Daten.fileSep;
            }
            String backupName = "Backup_" + EfaUtil.getCurrentTimeStampYYYYMMDD_HHMMSS();
            zipFile = backupDir + backupName + ".zip";

            FileOutputStream outFile = new FileOutputStream(zipFile);
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outFile));
            
            int successful = 0;
            int errors = 0;
            int cnt;

            if (backupProject) {
                cnt = backupStorageObjects(new IDataAccess[] { projectDataAccess }, zipOut, Daten.efaSubdirDATA);
                successful += cnt;
                errors += (1 - cnt);
                
                Vector<StorageObject> storageObjects = Daten.project.getAllDataAndLogbooks();
                IDataAccess[] dataAccesses = new IDataAccess[storageObjects.size()];
                for (int i=0; i<storageObjects.size(); i++) {
                    dataAccesses[i] = storageObjects.get(i).data();
                }
                totalWork = storageObjects.size() + (backupConfig ? 3 : 0);
                cnt = backupStorageObjects(dataAccesses, zipOut, Daten.efaSubdirDATA + Daten.fileSep + projectName);
                successful += cnt;
                errors += (dataAccesses.length - cnt);
            }

            if (backupConfig) {
                EfaConfig myEfaConfig = Daten.efaConfig;
                Admins myAdmins = Daten.admins;
                EfaTypes myTypes = Daten.efaTypes;
                if (Daten.project.getProjectStorageType() == IDataAccess.TYPE_EFA_REMOTE) {
                    myEfaConfig = new EfaConfig(Daten.project.getProjectStorageType(),
                                                Daten.project.getProjectStorageLocation(),
                                                Daten.project.getProjectStorageUsername(),
                                                Daten.project.getProjectStoragePassword());
                    myAdmins = new Admins(Daten.project.getProjectStorageType(),
                                                Daten.project.getProjectStorageLocation(),
                                                Daten.project.getProjectStorageUsername(),
                                                Daten.project.getProjectStoragePassword());
                    myTypes = new EfaTypes(Daten.project.getProjectStorageType(),
                                                Daten.project.getProjectStorageLocation(),
                                                Daten.project.getProjectStorageUsername(),
                                                Daten.project.getProjectStoragePassword());
                    IDataAccess[] dataAccesses = new IDataAccess[3];
                    dataAccesses[0] = myEfaConfig.data();
                    dataAccesses[1] = myAdmins.data();
                    dataAccesses[2] = myTypes.data();
                    totalWork = (totalWork > 0 ? totalWork : 3);
                    cnt = backupStorageObjects(dataAccesses, zipOut, Daten.efaSubdirCFG);
                    successful += cnt;
                    errors += (dataAccesses.length - cnt);
                }
            }

            zipOut.close();
            logMsg(Logger.INFO, Logger.MSG_BACKUP_BACKUPFINISHEDINFO,
                    International.getMessage("{n} Objekte in {filename} gesichert.",
                    successful, zipFile));
            if (errors == 0) {
                logMsg(Logger.INFO, Logger.MSG_BACKUP_BACKUPFINISHED,
                        International.getString("Backup erfolgreich abgeschlossen."));
            } else {
                logMsg(Logger.INFO, Logger.MSG_BACKUP_BACKUPFINISHEDWITHERRORS,
                        International.getMessage("Backup mit {n} Fehlern abgeschlossen.", errors));
            }
        } catch(Exception e) {
            lastErrorMsg = International.getString("Backup fehlgeschlagen.") + e.toString();
            logMsg(Logger.ERROR, Logger.MSG_BACKUP_BACKUPFAILED,
                    lastErrorMsg);
            Logger.logdebug(e);
            return false;
        }
        return true;
    }

    public String getLastErrorMessage() {
        return lastErrorMsg;
    }

    public String getZipFile() {
        return zipFile;
    }

    private void logMsg(String type, String key, String msg) {
        Logger.log(type, key, msg);
        if (backupTask != null && !type.equals(Logger.DEBUG)) {
            backupTask.logInfo(msg + "\n");
        }
    }
    
    public int getTotalWork() {
        return totalWork;
    }

    // Run Method for Creating a Backup
    public static void runAsTask(BaseDialog parentDialog,
            String backupDir,
            boolean backupProject,
            boolean backupConfig) {
        BackupTask backupTask = new BackupTask(backupDir, backupProject, backupConfig);
        ProgressDialog progressDialog = new ProgressDialog(parentDialog,
                International.getString("Backup erstellen"), backupTask, false);
        backupTask.startBackup(progressDialog);

    }


}

class BackupTask extends ProgressTask {

    private Backup backup;
    boolean success = false;

    // Constructor for Creating a Backup
    public BackupTask(String backupDir,
            boolean backupProject,
            boolean backupConfig) {
        super();
        backup = new Backup(backupDir, backupProject, backupConfig);
    }

    public void startBackup(ProgressDialog progressDialog) {
        this.start();
        if (progressDialog != null) {
            progressDialog.showDialog();
        }
    }

    public void run() {
        setRunning(true);
        success = backup.runBackup(this);
        setDone();
    }

    public int getAbsoluteWork() {
        return backup.getTotalWork();
    }

    public String getSuccessfullyDoneMessage() {
        if (success) {
            return International.getString("Backup erfolgreich abgeschlossen.") + "\n" +
                    backup.getZipFile();
        } else {
            return null;
        }
    }

    public String getErrorDoneMessage() {
        if (!success) {
            return International.getString("Backup fehlgeschlagen.");
        } else {
            return null;
        }
    }

}
