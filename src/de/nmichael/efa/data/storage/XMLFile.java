/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.EfaException;
import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// @i18n complete

public class XMLFile extends DataFile {

    public static final String FIELD_GLOBAL = "efa";
    public static final String FIELD_HEADER = "header";
    public static final String FIELD_HEADER_PROGRAM = "program";
    public static final String FIELD_HEADER_VERSION = "version";
    public static final String FIELD_HEADER_NAME = "name";
    public static final String FIELD_HEADER_TYPE = "type";
    public static final String FIELD_HEADER_SCN = "scn";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_DATA_RECORD = "record";

    private static final boolean doIndent = true;
    private int indent = 0;

    public XMLFile(String directory, String filename, String extension, String description) {
        super(directory, filename, extension, description);
    }

    public int getStorageType() {
        return IDataAccess.TYPE_FILE_XML;
    }

    private String xmltagStart(String tag) {
        indent++;
        return "<" + tag + ">";
    }

    private String xmltagEnd(String tag) {
        indent--;
        return "</" + tag + ">";
    }

    private String xmltag(String tag, String value) {
        return xmltagStart(tag) + EfaUtil.escapeXml(value) + xmltagEnd(tag);
    }


    protected synchronized void readFile(BufferedReader fr) throws EfaException {
        isOpen = true;
        long lock = -1;
        try {
            lock = acquireGlobalLock();
        } finally {
            isOpen = false;
        }
        if (lock < 0) {
            throw new EfaException(Logger.MSG_DATA_READFAILED, LogString.logstring_fileReadFailed(filename, storageLocation, "Cannot get Global Lock for File Reading"), Thread.currentThread().getStackTrace());
        }
        try {
            SaxErrorHandler eh = new SaxErrorHandler(filename);
            XMLReader parser = EfaUtil.getXMLReader();
            XMLFileReader xmlFileReader = new XMLFileReader(this, lock);
            parser.setContentHandler(xmlFileReader);
            parser.setErrorHandler(eh);
            inOpeningStorageObject = true; // don't update LastModified Timestamps, don't increment SCN, don't check assertions!
            parser.parse(filename);
            if (xmlFileReader.getDocumentReadError() != null) {
                throw new EfaException(Logger.MSG_DATA_INVALIDHEADER, xmlFileReader.getDocumentReadError(), Thread.currentThread().getStackTrace());
            }
        } catch(Exception e) {
            Logger.log(e);
            throw new EfaException(Logger.MSG_DATA_READFAILED, LogString.logstring_fileReadFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        } finally {
            inOpeningStorageObject = false;
            releaseGlobalLock(lock);
        }
    }

    protected String space(int indent) {
        if (doIndent) {
            String s = "";
            for (int i=0; i<indent && i<this.indent; i++) {
                s += "  ";
            }
            return s;
        }
        return "";
    }

    protected synchronized void write(BufferedWriter fw, int indent, String s) throws EfaException {
        try {
            fw.write(space(indent) + s + "\n");
        } catch(Exception e) {
            Logger.log(e);
            throw new EfaException(Logger.MSG_DATA_WRITEFAILED, LogString.logstring_fileWritingFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        }
    }

    protected synchronized void writeFile(BufferedWriter fw) throws EfaException {
        write(fw,0,"<?xml version=\"1.0\" encoding=\""+ENCODING+"\"?>");
        write(fw,indent,xmltagStart(FIELD_GLOBAL));
        write(fw,indent,xmltagStart(FIELD_HEADER));
        write(fw,indent,xmltag(FIELD_HEADER_PROGRAM,Daten.EFA));
        write(fw,indent,xmltag(FIELD_HEADER_VERSION,Daten.VERSIONID));
        write(fw,indent,xmltag(FIELD_HEADER_NAME,getStorageObjectName()));
        write(fw,indent,xmltag(FIELD_HEADER_TYPE,getStorageObjectType()));
        write(fw,indent,xmltag(FIELD_HEADER_SCN,Long.toString(getSCN())));
        write(fw,indent,xmltagEnd(FIELD_HEADER));
        writeData(fw);
        write(fw,indent,xmltagEnd(FIELD_GLOBAL));
    }

    private synchronized void writeData(BufferedWriter fw) throws EfaException {
        write(fw,indent,xmltagStart(FIELD_DATA));

        String[] fields = getFieldNames();
        DataKeyIterator it = getStaticIterator();
        DataRecord d = getFirst(it);
        while(d != null) {
            write(fw,indent,xmltagStart(FIELD_DATA_RECORD));
            for (int i=0; i<fields.length; i++) {
                Object o = d.get(fields[i]);
                if (o != null && d.getFieldType(i) != IDataAccess.DATA_VIRTUAL && !d.isDefaultValue(i)) {
                    write(fw,indent,xmltag(fields[i],o.toString()));
                }
            }
            write(fw,indent,xmltagEnd(FIELD_DATA_RECORD));
            d = getNext(it);
        }

        write(fw,indent,xmltagEnd(FIELD_DATA));
    }
}
