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
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

public class Backup {

    private String backupDir;
    private boolean backupProject;
    private boolean backupConfig;

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
                Logger.log(Logger.INFO, Logger.MSG_BACKUP_BACKUPINFO,
                        International.getMessage("Objekt {name} gesichert.", data.getUID()));
            } catch (Exception e) {
                Logger.log(Logger.ERROR, Logger.MSG_BACKUP_BACKUPERROR,
                        International.getMessage("Sicherung von Objekt {name} fehlgeschlagen: {reason}",
                        data.getUID(), e.toString()));
                Logger.logdebug(e);

            }
        }
        return successful;
    }

    public boolean runBackup() {
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

            Logger.log(Logger.INFO, Logger.MSG_BACKUP_BACKUPSTARTED,
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
            String zipFile = backupDir + backupName + ".zip";

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
                    cnt = backupStorageObjects(dataAccesses, zipOut, Daten.efaSubdirCFG);
                    successful += cnt;
                    errors += (dataAccesses.length - cnt);
                }
            }

            zipOut.close();
            Logger.log(Logger.INFO, Logger.MSG_BACKUP_BACKUPFINISHEDINFO,
                    International.getMessage("{n} Objekte in {filename} gesichert.",
                    successful, zipFile));
            if (errors == 0) {
                Logger.log(Logger.INFO, Logger.MSG_BACKUP_BACKUPFINISHED,
                        International.getString("Backup erfolgreich abgeschlossen."));
            } else {
                Logger.log(Logger.INFO, Logger.MSG_BACKUP_BACKUPFINISHEDWITHERRORS,
                        International.getMessage("Backup mit {n} Fehlern abgeschlossen.", errors));
            }
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_BACKUP_BACKUPFAILED,
                    International.getString("Backup fehlgeschlagen.") + e.toString());
            Logger.log(e);
            return false;
        }
        return true;
    }

}
