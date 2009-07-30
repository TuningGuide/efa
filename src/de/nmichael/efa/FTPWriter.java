package de.nmichael.efa;

import java.io.*;

/**
 * <p>Title: efa - Elektronisches Fahrtenbuch</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Nicolas Michael
 * @version 1.0
 */

public class FTPWriter {

  StatistikDaten sd;

  public FTPWriter(StatistikDaten sd) {
    this.sd = sd;
  }


  public String run() {
    try {
      com.enterprisedt.net.ftp.FTPClient ftpClient = new com.enterprisedt.net.ftp.FTPClient(sd.ftpServer);
      ftpClient.setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.ACTIVE);
      ftpClient.login(sd.ftpUser,sd.ftpPassword);
      ftpClient.chdir(sd.ftpDirectory);
      ftpClient.put(sd.ausgabeDatei,sd.ftpFilename);
      ftpClient.quit();
      return null; // korrektes Ende!
    } catch(Exception e) {
      return e.toString();
    }
  }

}