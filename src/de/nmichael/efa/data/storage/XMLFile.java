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
        return xmltagStart(tag) + value + xmltagEnd(tag);
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
            parser.setContentHandler(new XMLFileReader(this, lock));
            parser.setErrorHandler(eh);
            parser.parse(filename);
        } catch(Exception e) {
            throw new EfaException(Logger.MSG_DATA_READFAILED, LogString.logstring_fileReadFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        } finally {
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
            throw new EfaException(Logger.MSG_DATA_WRITEFAILED, LogString.logstring_fileWritingFailed(filename, storageLocation, e.toString()), Thread.currentThread().getStackTrace());
        }
    }

    protected synchronized void writeFile(BufferedWriter fw) throws EfaException {
        write(fw,0,"<?xml version=\"1.0\" encoding=\""+ENCODING+"\"?>");
        write(fw,indent,xmltagStart("efa"));
        write(fw,indent,xmltagStart("header"));
        write(fw,indent,xmltag("program","efa"));
        write(fw,indent,xmltag("version",Daten.VERSIONID));
        write(fw,indent,xmltag("name",getStorageObjectName()));
        write(fw,indent,xmltag("type",getStorageObjectType()));
        write(fw,indent,xmltag("scn",Long.toString(getSCN())));
        write(fw,indent,xmltagEnd("header"));
        writeData(fw);
        write(fw,indent,xmltagEnd("efa"));
    }

    private synchronized void writeData(BufferedWriter fw) throws EfaException {
        write(fw,indent,xmltagStart("data"));

        String[] fields = getFieldNames();
        DataKeyIterator it = getStaticIterator();
        DataRecord d = getFirst(it);
        while(d != null) {
            write(fw,indent,xmltagStart("record"));
            for (int i=0; i<fields.length; i++) {
                Object o = d.get(fields[i]);
                if (o != null) {
                    write(fw,indent,xmltag(fields[i],o.toString()));
                }
            }
            write(fw,indent,xmltagEnd("record"));
            d = getNext(it);
        }

        write(fw,indent,xmltagEnd("data"));
    }
}
