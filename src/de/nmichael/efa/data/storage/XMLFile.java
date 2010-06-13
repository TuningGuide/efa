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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// @i18n complete

public class XMLFile extends DataFile {

    private static final boolean doIndent = true;
    private int indent = 0;

    public XMLFile(String directory, String filename, String extension) {
        super(directory, filename, extension);
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

    protected void readFile(BufferedReader fr) throws Exception {
        isOpen = true;
        long lock = -1;
        try {
            lock = acquireGlobalLock();
        } finally {
            isOpen = false;
        }
        if (lock < 0) {
            throw new Exception("Cannot get Global Lock for File Reading");
        }
        try {
            SaxErrorHandler eh = new SaxErrorHandler(filename);
            XMLReader parser = EfaUtil.getXMLReader();
            parser.setContentHandler(new XMLFileReader(this, lock));
            parser.setErrorHandler(eh);
            parser.parse(filename);
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

    protected void write(BufferedWriter fw, int indent, String s) throws Exception {
        fw.write(space(indent) + s + "\n");
    }

    protected void writeFile(BufferedWriter fw) throws Exception {
        write(fw,0,"<?xml version=\"1.0\" encoding=\""+ENCODING+"\"?>");
        write(fw,indent,xmltagStart("efa"));
        write(fw,indent,xmltagStart("header"));
        write(fw,indent,xmltag("program","efa"));
        write(fw,indent,xmltag("version",Daten.VERSIONID));
        write(fw,indent,xmltag("name",getStorageObjectName()));
        write(fw,indent,xmltag("type",getStorageObjectType()));
        write(fw,indent,xmltagEnd("header"));
        writeData(fw);
        write(fw,indent,xmltagEnd("efa"));
    }

    private void writeData(BufferedWriter fw) throws Exception {
        write(fw,indent,xmltagStart("data"));

        String[] fields = getFieldNames();
        DataKeyIterator it = getIterator();
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
