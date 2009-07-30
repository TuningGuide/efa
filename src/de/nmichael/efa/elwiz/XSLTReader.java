package de.nmichael.efa.elwiz;

import de.nmichael.efa.EfaUtil;
import de.nmichael.efa.Logger;
import java.util.Vector;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XSLTReader extends DefaultHandler {

  static Vector allOptions;


  // Konstruktor
  public XSLTReader() {
    super();
  }


  // Start des Elements
  public void startElement(String uri, String localName, String qname, Attributes atts) {
    if (uri.equals("http://www.nmichael.de/elwiz")) {

      if (!localName.equals("option")) {
        ElwizOption o = new ElwizOption();

        // Typ
        if (localName.equals("optional")) o.type = ElwizOption.O_OPTIONAL;
        if (localName.equals("select")) o.type = ElwizOption.O_SELECT;
        if (localName.equals("value")) o.type = ElwizOption.O_VALUE;

        // Attribute
        for (int i=0; i<atts.getLength(); i++) {
          // Name
          if (atts.getLocalName(i).equals("name")) o.name = atts.getValue(i);

          // Description
          if (atts.getLocalName(i).equals("descr")) o.descr = atts.getValue(i);

        }
        allOptions.add(o);

      } else {

          ElwizSingleOption eso = new ElwizSingleOption();
          for (int i=0; i<atts.getLength(); i++) {
            if (atts.getLocalName(i).equals("descr")) eso.descr = atts.getValue(i);
            if (atts.getLocalName(i).equals("value")) eso.value = atts.getValue(i);
            if (atts.getLocalName(i).equals("selected")) eso.selected = atts.getValue(i).equals("true");
            if (atts.getLocalName(i).equals("type")) eso.type = atts.getValue(i);
          }
          if (((ElwizOption)allOptions.lastElement()).options == null)
            ((ElwizOption)allOptions.lastElement()).options = new Vector();

          ((ElwizOption)allOptions.lastElement()).options.add(eso);
      }
    }
  }

  static XMLReader tryToSetup(String className) {
    XMLReader parser = null;
    try {
      if (className != null) {
        Logger.log(Logger.INFO,"Versuche XML-Parser "+className+" zu laden...");
        parser = XMLReaderFactory.createXMLReader(className);
      } else {
        Logger.log(Logger.INFO,"Versuche Standard-XML-Parser zu laden...");
        parser = XMLReaderFactory.createXMLReader();
      }
    } catch(Exception e) {
      Logger.log(Logger.ERROR,"PARSER EXCEPTION: "+e.toString());
      if (e.getClass().toString().indexOf("java.lang.ClassNotFoundException")>0) {
        Logger.log(Logger.ERROR,className+" NICHT gefunden.");
        parser = null;
      }
    }
    Logger.log(Logger.INFO,"XML-Parser erfolgreich geladen.");
    return parser;
  }


  public static Vector run(String filename) {

    allOptions = new Vector();

    Logger.log(Logger.INFO,"XSLTReader: Lese "+filename+" ...");

    XMLReader parser;
    parser = tryToSetup(null);
    if (parser == null) parser = tryToSetup("org.apache.xerces.parsers.SAXParser");
    if (parser == null) parser = tryToSetup("javax.xml.parsers.SAXParser"); // Java 1.5
    if (parser == null) parser = tryToSetup("org.apache.crimson.parser.XMLReaderImpl");
    if (parser == null) {
      Logger.log(Logger.ERROR,"Kein XML-Parser gefunden!");
      return null;
    }

    try {
      parser.setContentHandler(new XSLTReader());
      parser.parse(filename);
    } catch(Exception e) {
      Logger.log(Logger.ERROR,"PARSER EXCEPTION: "+e.toString());
    }
    Logger.log(Logger.INFO,"XSLTReader: "+allOptions.size()+" Elemente gelesen.");

/*
    for (int i=0; i<allOptions.size(); i++) {
      ElwizOption o = (ElwizOption)allOptions.get(i);
      System.out.println(o.type+": "+o.name+"; "+o.descr);
      if (o.options != null)
        for (int j=0; j<o.options.size(); j++) {
          ElwizSingleOption eso = (ElwizSingleOption)o.options.get(j);
          System.out.println(eso.descr+"; "+eso.value+"; "+eso.selected);
        }
    }
*/

    return allOptions;
  }

}