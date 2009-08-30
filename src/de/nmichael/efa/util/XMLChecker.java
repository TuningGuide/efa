// benötigt folgende JARs:
// sax.jar
// xercesImpl.jar

package de.nmichael.efa.util;

import de.nmichael.efa.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class XMLChecker extends DefaultHandler {

  Locator locator;
  boolean verbose = false;

  public XMLChecker(boolean verbose) {
    super();
    this.verbose = verbose;
  }

  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  static String number(int i) {
    String s = Integer.toString(i);
    while (s.length()<3) s=" "+s;
    return s;
  }

  String getLocation() {
    return locator.getSystemId()+":"+number(locator.getLineNumber())+":"+number(locator.getColumnNumber())+":\t";
  }

  public void startDocument() {
    if (verbose) System.out.println("Positions: <SystemID>:<LineNumber>:<ColumnNumber>");
    if (verbose) System.out.println(getLocation()+"startDocument()");
  }

  public void endDocument() {
    if (verbose) System.out.println(getLocation()+"endDocument()");
  }

  public void startElement(String uri, String localName, String qname, Attributes atts) {
    if (verbose) System.out.println(getLocation()+"startElement(uri="+uri+", localName="+localName+", qname="+qname+")");
    if (verbose) for (int i=0; i<atts.getLength(); i++)
                   System.out.println("\tattribute: uri="+atts.getURI(i)+", localName="+atts.getLocalName(i)+", qname="+atts.getQName(i)+", value="+atts.getValue(i)+", type="+atts.getType(i));
  }

  public void endElement(String uri, String localName, String qname) {
    if (verbose) System.out.println(getLocation()+"endElement("+uri+","+localName+","+qname+")");
  }


  static void printHelp() {
    System.out.println("XMLChecker v0.01, (c) 2002 by Nicolas Michael (http://www.nmichael.de/)");
    System.out.println("XMLChecker is based on the Xerces XML parser.");
    System.out.println("This product includes software developed by the Apache Software Foundation (http://www.apache.org/).\n");
    System.out.println("XMLChecker [options] filename");
    System.out.println("    [options] are");
    System.out.println("      -validate   validate XML document using a DTD (DTD declaration required!)");
    System.out.println("      -verbose    print out all elements and attributes");
    System.out.println("      -help       print this help screen");
    System.exit(-1);
  }


  public static void main(String[] args) {
    String fname=null;
    boolean verbose = false;
    boolean validate = false;
    if (args.length == 0) printHelp();
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
	if (args[i].equals("-verbose")) verbose = true;
	else if (args[i].equals("-validate")) validate = true;
        else if (args[i].equals("-help")) printHelp();
	else { System.out.println("unrecognized option: '"+args[i]+"'"); printHelp(); }
      } else {
        fname = args[i];
      }
    }
    if (fname == null) printHelp();

    SaxErrorHandler eh = new SaxErrorHandler();
    try {
      XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
      if (validate) parser.setFeature("http://xml.org/sax/features/validation",true);
      parser.setContentHandler(new XMLChecker(verbose));
      parser.setErrorHandler(eh);
      parser.parse(fname);
    } catch(Exception e) {
      System.err.println("[EXCEPTION]: "+e.toString());
      if (eh.fatalErrors == 0) eh.fatalErrors++;
    }
    System.out.println("There were "+eh.fatalErrors+" fatal errors, "+eh.errors+" errors and "+eh.warnings+" warnings.");
    System.exit(eh.fatalErrors+eh.errors+eh.warnings);
  }

}

class SaxErrorHandler implements ErrorHandler {
  public int fatalErrors = 0;
  public int errors = 0;
  public int warnings = 0;

  public void fatalError(SAXParseException e) {
    System.out.println(e.getSystemId()+":"+XMLChecker.number(e.getLineNumber())+":"+XMLChecker.number(e.getColumnNumber())+":\t[FATALERROR]: "+e.toString());
    fatalErrors++;
  }
  public void error(SAXParseException e) {
    System.out.println(e.getSystemId()+":"+XMLChecker.number(e.getLineNumber())+":"+XMLChecker.number(e.getColumnNumber())+":\t[ERROR]: "+e.toString());
    errors++;
  }
  public void warning(SAXParseException e) {
    System.out.println(e.getSystemId()+":"+XMLChecker.number(e.getLineNumber())+":"+XMLChecker.number(e.getColumnNumber())+":\t[WARNING]: "+e.toString());
    warnings++;
  }
}
