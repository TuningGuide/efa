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
import java.io.*;
import org.xml.sax.*;

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

    public XMLFile(String directory, String filename, String extension, String description) {
        super(directory, filename, extension, description);
    }

    public int getStorageType() {
        return IDataAccess.TYPE_FILE_XML;
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

    protected synchronized void writeFile(OutputStream out) throws EfaException {
        writeFile(this, out);
    }

    // ============== static methods for writing XML data ==============

    protected static void writeFile(IDataAccess dataAccess, OutputStream out) throws EfaException {
        synchronized (dataAccess) {
            StaticXmlInfo data = new StaticXmlInfo(dataAccess, out);
            write(data, "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");
            write(data, xmltagStart(data, FIELD_GLOBAL));
            write(data, xmltagStart(data, FIELD_HEADER));
            write(data, xmltag(data, FIELD_HEADER_PROGRAM, Daten.EFA));
            write(data, xmltag(data, FIELD_HEADER_VERSION, Daten.VERSIONID));
            write(data, xmltag(data, FIELD_HEADER_NAME, dataAccess.getStorageObjectName()));
            write(data, xmltag(data, FIELD_HEADER_TYPE, dataAccess.getStorageObjectType()));
            write(data, xmltag(data, FIELD_HEADER_SCN, Long.toString(dataAccess.getSCN())));
            write(data, xmltagEnd(data, FIELD_HEADER));
            writeData(data);
            write(data, xmltagEnd(data, FIELD_GLOBAL));
            try {
                if (data.fout != null) {
                    data.fout.close();
                }
            } catch (IOException e) {
                Logger.log(e);
                throw new EfaException(Logger.MSG_DATA_WRITEFAILED,
                        LogString.logstring_fileWritingFailed(data.dataAccess.getUID(),
                        data.dataAccess.getStorageObjectDescription(), e.toString()), Thread.currentThread().getStackTrace());
            }
        }
   }

    private static void writeData(StaticXmlInfo data) throws EfaException {
        write(data, xmltagStart(data, FIELD_DATA));

        String[] fields = data.dataAccess.getFieldNames();
        DataKeyIterator it = data.dataAccess.getStaticIterator();
        DataKey k = it.getFirst();
        while(k != null) {
            DataRecord r = data.dataAccess.get(k);
            if (r == null) {
                continue;
            }
            write(data, xmltagStart(data, FIELD_DATA_RECORD));
            for (int i=0; i<fields.length; i++) {
                Object o = r.get(fields[i]);
                if (o != null && r.getFieldType(i) != IDataAccess.DATA_VIRTUAL && !r.isDefaultValue(i)) {
                    write(data, xmltag(data, fields[i],o.toString()));
                }
            }
            write(data, xmltagEnd(data, FIELD_DATA_RECORD));
            k = it.getNext();
        }

        write(data, xmltagEnd(data, FIELD_DATA));
    }

    private static void write(StaticXmlInfo data, String s) throws EfaException {
        try {
            String str = space(data) + s + "\n";
            if (data.fout != null) {
                data.fout.write(str);
            } else {
                data.out.write(str.getBytes(XMLFile.ENCODING));
            }
        } catch(Exception e) {
            Logger.log(e);
            throw new EfaException(Logger.MSG_DATA_WRITEFAILED, 
                    LogString.logstring_fileWritingFailed(data.dataAccess.getUID(),
                    data.dataAccess.getStorageObjectDescription(), e.toString()), Thread.currentThread().getStackTrace());
        }
    }

    private static String xmltagStart(StaticXmlInfo data, String tag) {
        data.indent++;
        return "<" + tag + ">";
    }

    private static String xmltagEnd(StaticXmlInfo data, String tag) {
        data.indent--;
        return "</" + tag + ">";
    }

    private static String xmltag(StaticXmlInfo data, String tag, String value) {
        return xmltagStart(data, tag) +
                EfaUtil.escapeXml(value) +
                xmltagEnd(data, tag);
    }
 
    private static String space(StaticXmlInfo data) {
        if (doIndent) {
            String s = "";
            for (int i=0; i<data.lastindent && i<data.indent; i++) {
                s += "  ";
            }
            data.lastindent = data.indent;
            return s;
        }
        return "";
    }

}

class StaticXmlInfo {

    IDataAccess dataAccess;
    OutputStream out;
    // BufferedWriter is more efficient for files; only used if out is an instance of FileOutputStream
    BufferedWriter fout;
    int indent, lastindent;

    public StaticXmlInfo(IDataAccess dataAccess, OutputStream out) {
        this.dataAccess = dataAccess;
        this.out = out;
        if (out instanceof FileOutputStream) {
            try {
                fout = new BufferedWriter(new OutputStreamWriter(out, XMLFile.ENCODING));
            } catch (Exception e) {
                fout = null;
            }
        }
        indent = 0;
        lastindent = 0;
    }
}
