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

import java.io.*;
import de.nmichael.efa.util.Logger;

// @i18n complete
public class PDFWriter {

    private String inputFile;
    private String outputFile;
    int pageCount = 0;

    public PDFWriter(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String run() {
        String res = null;
        FileOutputStream out = null;
        try {
            pageCount = 0;
            out = new FileOutputStream(outputFile);
            org.apache.fop.apps.Driver driver =
                    new org.apache.fop.apps.Driver(new org.xml.sax.InputSource(inputFile), out);
            driver.setRenderer(org.apache.fop.apps.Driver.RENDER_PDF);
            if (Logger.isTraceOn(Logger.TT_PDF, 1)) {
                org.apache.avalon.framework.logger.Logger logger = new org.apache.avalon.framework.logger.ConsoleLogger(org.apache.avalon.framework.logger.ConsoleLogger.LEVEL_INFO);
                driver.setLogger(logger);
            }
            driver.run();
            pageCount = driver.getResults().getPageCount();
            out.close();
        } catch (FileNotFoundException e) {
            Logger.logdebug(e);
            res = e.toString();
        } catch (org.apache.fop.apps.FOPException e) {
            Logger.logdebug(e);
            res = e.toString();
        } catch (IOException e) {
            Logger.logdebug(e);
            res = e.toString();
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }

        return res;
    }
}
