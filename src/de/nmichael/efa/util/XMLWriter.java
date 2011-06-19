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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.EfaException;
import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class XMLWriter {

    private String filename;

    public XMLWriter(String filename) {
        this.filename = filename;
    }

    public void write() throws Exception {
/*
        FileOutputStream fos = new FileOutputStream(filename);
// XERCES 1 or 2 additionnal classes.
        OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
        of.setIndent(1);
        of.setIndenting(true);
        of.setDoctype(null, "users.dtd");
        XMLSerializer serializer = new XMLSerializer(fos, of);
// SAX2.0 ContentHandler.
        ContentHandler hd = serializer.asContentHandler();
        hd.startDocument();
// Processing instruction sample.
//hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
// USER attributes.
        AttributesImpl atts = new AttributesImpl();
// USERS tag.
        hd.startElement("", "", "USERS", atts);
// USER tags.
        String[] id = {"PWD122", "MX787", "A4Q45"};
        String[] type = {"customer", "manager", "employee"};
        String[] desc = {"Tim@Home", "Jack&Moud", "John D'oé"};
        for (int i = 0; i < id.length; i++) {
            atts.clear();
            atts.addAttribute("", "", "ID", "CDATA", id[i]);
            atts.addAttribute("", "", "TYPE", "CDATA", type[i]);
            hd.startElement("", "", "USER", atts);
            hd.characters(desc[i].toCharArray(), 0, desc[i].length());
            hd.endElement("", "", "USER");
        }
        hd.endElement("", "", "USERS");
        hd.endDocument();
        fos.close();
 */
    }

    

}
