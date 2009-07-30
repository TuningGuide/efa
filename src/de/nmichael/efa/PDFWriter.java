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

public class PDFWriter {

  StatistikDaten sd;
  int pageCount = 0;

  public PDFWriter(StatistikDaten sd, AusgabeDaten ad) {
    this.sd = sd;
  }

  public int getPageCount() {
    return pageCount;
  }


  public String run() {
    String res = null;
    FileOutputStream out=null;
    try {
      pageCount = 0;
      out = new FileOutputStream(sd.ausgabeDatei);
      org.apache.fop.apps.Driver driver =
        new org.apache.fop.apps.Driver(new org.xml.sax.InputSource(sd.ausgabeDateiTmp), out);
      driver.setRenderer(org.apache.fop.apps.Driver.RENDER_PDF);
//      org.apache.avalon.framework.logger.Logger logger = new org.apache.avalon.framework.logger.ConsoleLogger(org.apache.avalon.framework.logger.ConsoleLogger.LEVEL_INFO);
//      driver.setLogger(logger);
      driver.run();
      pageCount = driver.getResults().getPageCount();
      out.close();
    } catch(FileNotFoundException e) {
//      e.printStackTrace();
      res = e.toString();
    } catch(org.apache.fop.apps.FOPException e) {
//      e.printStackTrace();
      res = e.toString();
    } catch(IOException e) {
//      e.printStackTrace();
      res = e.toString();
    }

    if (out != null) {
      try {
        out.close();
      } catch(IOException e) {
      }
    }

    return res;
  }

}