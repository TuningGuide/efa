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

import de.nmichael.efa.*;
import java.io.*;

// @i18n complete
public class FTPClient {

    // @todo (P5) Implement FTP Upload for Statistics

    private String server;
    private String username;
    private String password;
    private String localFileWithPath;
    private String remoteDirectory;
    private String remoteFile;

    public FTPClient(String server, String username, String password,
            String localFileWithPath, String remoteDirectory, String remoteFile) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.localFileWithPath = localFileWithPath;
        this.remoteDirectory = remoteDirectory;
        this.remoteFile = remoteFile;
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
}
