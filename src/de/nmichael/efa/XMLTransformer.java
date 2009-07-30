// benötigt folgende JARs:
// jaxp-api.jar
// xalan.jar
// sax.jar
// dom.jar
// xercesImpl.jar

package de.nmichael.efa;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class XMLTransformer {

  static void printHelp() {
    System.out.println("XMLTransformer v0.01, (c) 2002 by Nicolas Michael (http://www.nmichael.de/)");
    System.out.println("XMLTransformer is based on the Xerces XML parser and the Xalan XSLT processor.");
    System.out.println("This product includes software developed by the Apache Software Foundation (http://www.apache.org/).");
    System.out.println("The DOM bindings are published under the W3C Software Copyright Notice and License (http://www.w3.org).\n");
    System.out.println("XMLTransformer [options]");
    System.out.println("    [options] are");
    System.out.println("      -in filename     name of XML source file (stdin, if not specified)");
    System.out.println("      -out filename    name of output file (stdout, if not specified)");
    System.out.println("      -trans filename  name of transformation stylesheet file (mandatory)");
    System.out.println("      -help            print this help screen");
    System.exit(-1);
  }

  public static void main(String[] args) {
    String transform = null;
    String quelle = null;
    String ziel = null;
    if (args.length == 0) printHelp();
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-in") && i+1<args.length) quelle=args[++i];
      else if (args[i].equals("-out") && i+1<args.length) ziel=args[++i];
      else if (args[i].equals("-trans") && i+1<args.length) transform=args[++i];
      else printHelp();
    }
    StreamSource in;
    StreamResult out;
    if (quelle == null) {
      in = new StreamSource(System.in);
    } else {
      in = new StreamSource(quelle);
    }
    if (ziel == null) {
      out = new StreamResult(System.out);
    } else {
      out = new StreamResult(ziel);
    }
    if (transform == null) { System.out.println("no transformation file specified!\n"); printHelp(); }

    try {
      TransformerFactory f = TransformerFactory.newInstance();
      Transformer t = f.newTransformer(new StreamSource(transform));
      t.transform(in, out);
    } catch(Exception e) {
      System.err.println(e.toString());
    }
  }

}
