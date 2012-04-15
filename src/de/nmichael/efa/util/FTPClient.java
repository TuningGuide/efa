/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.util;

import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.gui.MultiInputDialog;
import javax.swing.JDialog;

// @i18n complete
public class FTPClient {

    private String ftpString;
    private String server;
    private String username;
    private String password;
    private String localFileWithPath;
    private String remoteDirectory;
    private String remoteFile;
    private boolean validFormat = false;

    public FTPClient(String ftpString, String localFileWithPath) {
        this.ftpString = ftpString;
        this.localFileWithPath = localFileWithPath;
        if (ftpString.toLowerCase().startsWith("ftp:")) {
            ftpString = ftpString.substring(4);
            int pos = ftpString.indexOf(":");
            if (pos >= 0) {
                username = ftpString.substring(0, pos);
                ftpString = ftpString.substring(pos+1);
                pos = ftpString.indexOf("@");
                if (pos >= 0) {
                    password = ftpString.substring(0, pos);
                    ftpString = ftpString.substring(pos + 1);
                    pos = ftpString.indexOf(":");
                    if (pos >= 0) {
                        server = ftpString.substring(0, pos);
                        ftpString = ftpString.substring(pos + 1);
                        pos = ftpString.lastIndexOf("/");
                        if (pos >= 0) {
                            remoteDirectory = ftpString.substring(0, pos);
                            remoteFile = ftpString.substring(pos + 1);
                            validFormat = true; 
                        }
                    }
                }
            }
        }
    }

    public FTPClient(String server, String username, String password,
            String localFileWithPath, String remoteDirectory, String remoteFile) {
        this.ftpString = username + ":" + password + "@" + server + ":" + remoteDirectory + "/" + remoteFile;
        this.server = server;
        this.username = username;
        this.password = password;
        this.localFileWithPath = localFileWithPath;
        this.remoteDirectory = remoteDirectory;
        this.remoteFile = remoteFile;
        validFormat = true;
    }

    public boolean isValidFormat() {
        return validFormat;
    }

    public String getFtpString() {
        return ftpString;
    }

    public String getFtpString(boolean withPassword) {
        return "ftp:" + username + (withPassword ? ":" + password : "") +
                "@" + server + ":" + remoteDirectory + "/" + remoteFile;
    }

    public String getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFile() {
        return (remoteDirectory != null && remoteFile != null ? remoteDirectory + "/" + remoteFile : null);
    }

    public String run() {
        try {
            com.enterprisedt.net.ftp.FTPClient ftpClient = new com.enterprisedt.net.ftp.FTPClient(server);
            ftpClient.setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.ACTIVE);
            ftpClient.login(username, password);
            ftpClient.chdir(remoteDirectory);
            ftpClient.put(localFileWithPath, remoteFile);
            ftpClient.quit();
            return null; // korrektes Ende!
        } catch (Exception e) {
            return e.toString();
        }
    }

    public String write() {
        if (isValidFormat()) {
            String error = run();
            if (error == null) {
                return LogString.fileSuccessfullyCreated(getFtpString(false),
                        International.getString("Statistik"));
            } else {
                return LogString.fileCreationFailed(getFtpString(false),
                        International.getString("Statistik"));

            }
        } else {
            return International.getString("Ungültiges Format") + ": " + getFtpString();
        }

    }

    public static String getFtpStringGuiDialog(String s) {
        FTPClient ftp = new FTPClient(s, null);
        IItemType[] items = new IItemType[4];
        items[0] = new ItemTypeString("SERVER", (ftp.getServer() != null ? ftp.getServer() : ""),
                IItemType.TYPE_PUBLIC, "", International.getString("FTP-Server"));
        items[0].setNotNull(true);
        items[1] = new ItemTypeString("USERNAME", (ftp.getUsername() != null ? ftp.getUsername() : ""),
                IItemType.TYPE_PUBLIC, "", International.getString("Benutzername"));
        items[2] = new ItemTypeString("PASSWORD", (ftp.getPassword() != null ? ftp.getPassword() : ""),
                IItemType.TYPE_PUBLIC, "", International.getString("Paßwort"));
        items[3] = new ItemTypeString("FILE", (ftp.getFile() != null ? ftp.getFile() : ""),
                IItemType.TYPE_PUBLIC, "", International.getString("Dateiname"));
        if (MultiInputDialog.showInputDialog((JDialog)null, International.getString("FTP-Upload"), items)) {
            ftp = new FTPClient("ftp:" + items[1].toString() + ":" + items[2].toString() +
                    "@" + items[0].toString() + ":" + items[3].toString(), null);
            if (ftp.isValidFormat()) {
                return ftp.getFtpString(true);
            }
        }
        return null;
    }

}
