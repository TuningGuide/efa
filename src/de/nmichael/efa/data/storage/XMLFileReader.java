/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.util.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// @i18n complete

public class XMLFileReader extends DefaultHandler {

    private XMLFile data;
    private long globalLock;
    private Locator locator;

    private boolean inDataSection = false;
    private boolean inRecord = false;
    private String currentField = null;
    private DataRecord dataRecord = null;

    public XMLFileReader(XMLFile data, long globalLock) {
        super();
        this.data = data;
        this.globalLock = globalLock;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    String getLocation() {
        return locator.getSystemId() + ":" + EfaUtil.int2String(locator.getLineNumber(),4) + ":" + EfaUtil.int2String(locator.getColumnNumber(),4) + ":\t";
    }

    public void startDocument() {
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,"Positions: <SystemID>:<LineNumber>:<ColumnNumber>");
        }
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "startDocument()");
        }
    }

    public void endDocument() {
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "endDocument()");
        }
    }

    public void startElement(String uri, String localName, String qname, Attributes atts) {
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "startElement(uri=" + uri + ", localName=" + localName + ", qname=" + qname + ")");
        }
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            for (int i = 0; i < atts.getLength(); i++) {
                Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,"\tattribute: uri=" + atts.getURI(i) + ", localName=" + atts.getLocalName(i) + ", qname=" + atts.getQName(i) + ", value=" + atts.getValue(i) + ", type=" + atts.getType(i));
            }
        }

        if (inRecord) {
            currentField = localName;
        }
        if (inDataSection && localName.equals("record")) {
            inRecord = true;
            dataRecord = data.getPersistence().createNewRecord();
        }
        if (localName.equals("data")) {
            inDataSection = true;
        }
    }

    public void characters(char[] ch, int start, int length) {
        String s = new String(ch, start, length).trim();
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "characters("+s+")");
        }

        if (currentField != null) {
            try {
                dataRecord.set(currentField, s);
            } catch(Exception e) {
                Logger.log(Logger.ERROR,Logger.MSG_FILE_PARSEERROR,"Parse Error for Field "+currentField+" = "+s+": "+e.toString());
            }
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,"Field "+currentField+" = "+s);
        }
    }

    public void endElement(String uri, String localName, String qname) {
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "endElement(" + uri + "," + localName + "," + qname + ")");
        }

        if (localName.equals("data")) {
            inDataSection = false;
        }
        if (inDataSection && localName.equals("record")) {
            try {
                data.add(dataRecord,globalLock);
            } catch(Exception e) {
                Logger.log(Logger.ERROR,Logger.MSG_FILE_PARSEERROR,"Parse Error for Data Record "+dataRecord.toString()+": "+e.toString());
            }
            dataRecord = null;
            inRecord = false;
        }
        if (inRecord) {
            currentField = null;
        }
}
}