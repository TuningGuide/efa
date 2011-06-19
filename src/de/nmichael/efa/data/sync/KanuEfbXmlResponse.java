/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.sync;

import de.nmichael.efa.util.*;
import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// @i18n complete

public class KanuEfbXmlResponse extends DefaultHandler {

    public static String LOGIN = "header"; // pseudo-field, only used for internal Hashtable

    private KanuEfbSyncTask efb;
    private Locator locator;

    private Vector<Hashtable<String,String>> data = new Vector<Hashtable<String,String>>(); // Vector of (fieldName,fieldValue)
    Hashtable<String,String> fields;

    private boolean inXml = false;
    private boolean inResponse = false;
    private boolean inRecord = false;
    private boolean documentComplete = false;

    private String responseName;
    private String recordName;
    private String fieldName;
    private String fieldValue;

    public KanuEfbXmlResponse(KanuEfbSyncTask efb) {
        super();
        this.efb = efb;
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
        documentComplete = false;
    }

    public void endDocument() {
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "endDocument()");
        }
        if (!documentComplete) {
            Logger.log(Logger.ERROR,Logger.MSG_FILE_XMLFILEINCOMPLETE, International.getMessage("Unvollständige oder korrupte Daten gelesen: {data}", data.toString()));
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

        if (inResponse && inRecord) {
            fieldName = localName;
            fieldValue = "";
        }
        if (inResponse && !inRecord) {
            inRecord = true;
            recordName = localName;
            fieldName = null;
        }
        if (inXml && localName.equals("response")) {
            inResponse = true;
            responseName = atts.getValue("command");
            recordName = null;
        }
        if (inXml && !inResponse) {
            inRecord = true;
            recordName = LOGIN;
            fieldName = localName;
            fieldValue = "";
        }
        if (!inXml && localName.equals("xml")) {
            inXml = true;
        }
    }

    public void characters(char[] ch, int start, int length) {
        String s = new String(ch, start, length).trim();
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "characters("+s+")");
        }

        if (inRecord && fieldName != null) {
            fieldValue += s;
        }
    }

    public void endElement(String uri, String localName, String qname) {
        if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "endElement(" + uri + "," + localName + "," + qname + ")");
        }

        if (inRecord && recordName != null && fieldName != null && localName.equals(fieldName)) {
            // add field content
            if (fields == null) {
                fields = new Hashtable<String,String>();
            }
            fields.put(fieldName, fieldValue);
        }

        if (inRecord && recordName != null && 
                (localName.equals(recordName) || 
                 localName.equals("xml"))) { // make sure to also add record for login response, which doesn't really have a "recordName"
            data.add(fields);
            fields = null;
            inRecord = false;
            recordName = null;
        }

        if (inResponse && localName.equals("response")) {
            inResponse = false;
        }

        if (!inResponse && localName.equals("xml")) {
            inXml = false;
            documentComplete = true;
        }
    }

    public Vector<Hashtable<String,String>> getData() {
        return data;
    }

    public void printAll() {
        Logger.log(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "-- RESPONSE START --");
        Logger.log(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  ResponseName="+responseName);
        for (int i=0; data != null && i<data.size(); i++) {
            Hashtable<String,String> fields = data.get(i);
            String[] fieldNames = fields.keySet().toArray(new String[0]);
            for (int j=0; fieldNames != null && j<fieldNames.length; j++) {
                String value = fields.get(fieldNames[j]);
                Logger.log(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  " + EfaUtil.int2String(i, 4)+":"+fieldNames[j]+"="+value);
            }
        }
        Logger.log(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "-- RESPONSE END --");
    }

    public boolean isResponseOk(String requestType) {
        return documentComplete &&
                (requestType == null || requestType.equals(responseName));
    }

    public int getNumberOfRecords() {
        return (data == null ? 0 : data.size());
    }

    public Hashtable<String,String> getFields(int idx) {
        if (data == null) {
            return null;
        }
        return data.get(idx);
    }

    public String getValue(int idx, String fieldName) {
        if (data == null) {
            return null;
        }
        Hashtable<String,String> fields = data.get(idx);
        if (fields == null) {
            return null;
        }
        return fields.get(fieldName);
    }

}
