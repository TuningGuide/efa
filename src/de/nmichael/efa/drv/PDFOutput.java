/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.drv;

import de.nmichael.efa.*;
import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.io.*;
import java.util.*;

// @i18n complete (needs no internationalization -- only relevant for Germany)

public class PDFOutput {

  private static void writeRow(BufferedWriter f, String field, String value) throws IOException {
    f.write("                  <fo:table-row>\n");
    f.write("                    <fo:table-cell border=\"0.5pt #000000 solid\" padding-left=\"1pt\"><fo:block>"+field+"</fo:block></fo:table-cell>\n");
    f.write("                    <fo:table-cell border=\"0.5pt #000000 solid\" padding-left=\"1pt\"><fo:block>"+value+"</fo:block></fo:table-cell>\n");
    f.write("                  </fo:table-row>\n");
  }

  public static void printPDFbestaetigung(DRVConfig drvConfig, EfaWett ew, String qnr, int meldegeld,
                                   int gemeldet, int gewertet, int erwachsene, int jugendliche, ESigFahrtenhefte fh,
                                   Vector nichtGewerteteTeilnehmer) {
    String xslfo = Daten.efaTmpDirectory+"esigfahrtenhefte.fo";

    try {
      int netto = (int)((meldegeld * 100) / 1.07);
      int mwst = meldegeld - netto;
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xslfo),Daten.ENCODING_UTF));
      f.write("<?xml version=\"1.0\" encoding=\""+Daten.ENCODING_UTF+"\"?>\n");
      f.write("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");
      f.write("  <fo:layout-master-set>\n");
      f.write("    <fo:simple-page-master page-height=\"297mm\" page-width=\"210mm\" master-name=\"titelseite\">\n");
      f.write("      <fo:region-body margin-right=\"20mm\" margin-left=\"20mm\" margin-bottom=\"10mm\" margin-top=\"10mm\"/>\n");
      f.write("      <fo:region-after extent=\"10mm\" />\n");
      f.write("    </fo:simple-page-master>\n");
      f.write("    <fo:simple-page-master page-height=\"297mm\" page-width=\"210mm\" master-name=\"fahrtenhefte\">\n");
      f.write("      <fo:region-body margin-right=\"10mm\" margin-left=\"10mm\" margin-bottom=\"10mm\" margin-top=\"10mm\"/>\n");
      f.write("    </fo:simple-page-master>\n");
      f.write("  </fo:layout-master-set>\n");

      f.write("  <fo:page-sequence master-reference=\"titelseite\">\n");

      f.write("    <fo:static-content flow-name=\"xsl-region-after\" font-size=\"8pt\" font-family=\"Helvetica\">\n");
      f.write("      <fo:block text-align=\"center\">Bankverbindungen: Sparkasse Hannover - Konto-Nr. 123 862 - BLZ 250 501 80 -- Postbank Hannover - Konto-Nr. 8290-305 - BLZ 250 100 30</fo:block>\n");
      f.write("    </fo:static-content>\n");

      f.write("    <fo:flow font-family=\"Helvetica\" font-size=\"12pt\" flow-name=\"xsl-region-body\">\n");
      f.write("      <fo:table>\n");
      f.write("        <fo:table-column column-width=\"130mm\"/>\n");
      f.write("	       <fo:table-column column-width=\"55mm\"/>\n");
      f.write("	       <fo:table-body>\n");
      f.write("	         <fo:table-row>\n");
      f.write("            <fo:table-cell height=\"80mm\">\n");
      f.write("              <fo:block space-before=\"35mm\" font-size=\"8pt\">Deutscher Ruderverband, Ferd.-Wilh.-Fricke-Weg 10, 30169 Hannover</fo:block>\n");
      f.write("              <fo:block space-before=\"5mm\">An</fo:block>\n");
      f.write("              <fo:block>"+ew.versand_name+"</fo:block>\n");
      f.write("              <fo:block>"+ew.versand_strasse+"</fo:block>\n");
      f.write("              <fo:block>"+ew.versand_ort+"</fo:block>\n");
      f.write("            </fo:table-cell>\n");
      f.write("            <fo:table-cell height=\"80mm\">\n");
      f.write("              <fo:external-graphic src=\"url('"+Daten.efaProgramDirectory+"DRVbriefkopf.gif')\" />\n");
      f.write("              <fo:block space-before=\"7mm\" margin-left=\"4mm\">"+EfaUtil.getCurrentTimeStampDD_MM_YYYY()+"</fo:block>\n");
      f.write("            </fo:table-cell>\n");
      f.write("	         </fo:table-row>\n");
      f.write("	       </fo:table-body>\n");
      f.write("      </fo:table>\n");

      f.write("      <fo:block space-before=\"3mm\" font-size=\"12pt\" font-weight=\"bold\">Meldung für das DRV-Fahrtenabzeichen "+drvConfig.aktJahr+"</fo:block>\n");
      f.write("      <fo:block font-size=\"12pt\" font-weight=\"bold\">Verein: "+ew.verein_name+"</fo:block>\n");
      f.write("      <fo:block font-size=\"12pt\" font-weight=\"bold\">Mitgliedsnummer: "+ew.verein_mitglnr+"</fo:block>\n");
      f.write("      <fo:block font-size=\"12pt\" font-weight=\"bold\">Rechnungsnummer und -Bestätigung: "+qnr+"</fo:block>\n");

      f.write("      <fo:block space-before=\"10mm\">Sehr geehrte Damen und Herren,</fo:block>\n");
      f.write("      <fo:block space-before=\"4mm\">zu der erfolgreichen Teilnahme "+(gewertet == 1 ? "Ihres Mitglieds" : "Ihrer Mitglieder")+
              " am Fahrtenwettbewerb "+drvConfig.aktJahr+" gratulieren wir Ihnen sehr herzlich.</fo:block>\n");

      f.write("      <fo:block space-before=\"4mm\">Ihre Meldedaten:</fo:block>\n");
      f.write("      <fo:list-block>\n");
      f.write("          <fo:list-item>\n");
      f.write("            <fo:list-item-label><fo:block>gemeldete Teilnehmer:</fo:block></fo:list-item-label>\n");
      f.write("            <fo:list-item-body start-indent=\"50mm\"><fo:block>"+gemeldet+"</fo:block></fo:list-item-body>\n");
      f.write("          </fo:list-item>\n");
      f.write("          <fo:list-item>\n");
      f.write("            <fo:list-item-label><fo:block>gewertete Teilnehmer:</fo:block></fo:list-item-label>\n");
      f.write("            <fo:list-item-body start-indent=\"50mm\"><fo:block>"+gewertet+" ("+erwachsene+" Erwachsene, "+jugendliche+" Jugendliche)</fo:block></fo:list-item-body>\n");
      f.write("          </fo:list-item>\n");
      f.write("      </fo:list-block>\n");

      if (gewertet < gemeldet && nichtGewerteteTeilnehmer.size() > 0) {
        f.write("      <fo:block space-before=\"4mm\">Folgende Teilnehmer wurden nicht gewertet:</fo:block>\n");
        f.write("      <fo:list-block>\n");
        for (int i=0; i<nichtGewerteteTeilnehmer.size(); i++) {
          f.write("          <fo:list-item>\n");
          f.write("            <fo:list-item-label><fo:block>-</fo:block></fo:list-item-label>\n");
          f.write("            <fo:list-item-body start-indent=\"5mm\"><fo:block>"+nichtGewerteteTeilnehmer.get(i)+"</fo:block></fo:list-item-body>\n");
          f.write("          </fo:list-item>\n");
        }
        f.write("      </fo:list-block>\n");
      }

      f.write("      <fo:block space-before=\"4mm\">Mit diesem Schreiben erhalten Sie:</fo:block>\n");
      f.write("      <fo:list-block>\n");
      if (gewertet > 0) {
        f.write("          <fo:list-item>\n");
        f.write("            <fo:list-item-label><fo:block>-</fo:block></fo:list-item-label>\n");
        f.write("            <fo:list-item-body start-indent=\"5mm\"><fo:block>Ausdrucke der elektronischen Fahrtenhefte der gewerteten Teilnehmer</fo:block></fo:list-item-body>\n");
        f.write("          </fo:list-item>\n");
      }
      if (ew.drvint_anzahlPapierFahrtenhefte > 0) {
        f.write("          <fo:list-item>\n");
        f.write("            <fo:list-item-label><fo:block>-</fo:block></fo:list-item-label>\n");
        f.write("            <fo:list-item-body start-indent=\"5mm\"><fo:block>eingesandte Papier-Fahrtenhefte ("+ew.drvint_anzahlPapierFahrtenhefte+" Hefte)</fo:block></fo:list-item-body>\n");
        f.write("          </fo:list-item>\n");
      }
      String s = "";
      if (EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_erw_gold,',')) + EfaUtil.string2int(ew.drv_nadel_erw_silber,0) +
          EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_jug_gold,',')) + EfaUtil.string2int(ew.drv_nadel_jug_silber,0) > 0) {
        f.write("          <fo:list-item>\n");
        f.write("            <fo:list-item-label><fo:block>-</fo:block></fo:list-item-label>\n");
        f.write("            <fo:list-item-body start-indent=\"5mm\"><fo:block>bestellte Anstecknadeln:</fo:block></fo:list-item-body>\n");
        f.write("          </fo:list-item>\n");
        if (EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_erw_gold,',')) > 0) {
          int[] a = EfaUtil.kommaList2IntArr(ew.drv_nadel_erw_gold,',');
          int gold = EfaUtil.sumUpArray(a);
          String einzeln = "";
          for (int i=0; i<a.length; i++) {
            if (a[i]>0) einzeln += (einzeln.length()>0 ? "; " : "") + a[i] + "x" + ((i+1)*5) + ".";
          }
          if (a.length == 1) einzeln = null; // wenn noch altes Format, d.h. keine Information über Anzahl der einzelnen Abzeichen
          f.write("          <fo:list-item>\n");
          f.write("            <fo:list-item-label start-indent=\"10mm\"><fo:block>Erwachsene (gold):</fo:block></fo:list-item-label>\n");
          f.write("            <fo:list-item-body start-indent=\"50mm\"><fo:block>" + gold + (einzeln != null ? " ("+einzeln+")" : "") + "</fo:block></fo:list-item-body>\n");
          f.write("          </fo:list-item>\n");
        }
        if (EfaUtil.string2int(ew.drv_nadel_erw_silber,0) > 0) {
          f.write("          <fo:list-item>\n");
          f.write("            <fo:list-item-label start-indent=\"10mm\"><fo:block>Erwachsene (silber):</fo:block></fo:list-item-label>\n");
          f.write("            <fo:list-item-body start-indent=\"50mm\"><fo:block>"+ew.drv_nadel_erw_silber+"</fo:block></fo:list-item-body>\n");
          f.write("          </fo:list-item>\n");
        }
        if (EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_jug_gold,',')) > 0) {
          int[] a = EfaUtil.kommaList2IntArr(ew.drv_nadel_jug_gold,',');
          int gold = EfaUtil.sumUpArray(a);
          String einzeln = "";
          for (int i=0; i<a.length; i++) {
            if (a[i]>0) einzeln += (einzeln.length()>0 ? "; " : "") + a[i] + "x" + ((i+1)*5) + ".";
          }
          if (a.length == 1) einzeln = null; // wenn noch altes Format, d.h. keine Information über Anzahl der einzelnen Abzeichen
          f.write("          <fo:list-item>\n");
          f.write("            <fo:list-item-label start-indent=\"10mm\"><fo:block>Jugend (gold):</fo:block></fo:list-item-label>\n");
          f.write("            <fo:list-item-body start-indent=\"50mm\"><fo:block>" + gold + (einzeln != null ? " ("+einzeln+")" : "") + "</fo:block></fo:list-item-body>\n");
          f.write("          </fo:list-item>\n");
        }
        if (EfaUtil.string2int(ew.drv_nadel_jug_silber,0) > 0) {
          f.write("          <fo:list-item>\n");
          f.write("            <fo:list-item-label start-indent=\"10mm\"><fo:block>Jugend (silber):</fo:block></fo:list-item-label>\n");
          f.write("            <fo:list-item-body start-indent=\"50mm\"><fo:block>"+ew.drv_nadel_jug_silber+"</fo:block></fo:list-item-body>\n");
          f.write("          </fo:list-item>\n");
        }
        s = " und die bestellten Anstecknadeln";
      }
      f.write("      </fo:list-block>\n");

      f.write("      <fo:block space-before=\"4mm\">Für das Meldegeld"+s+" ergibt sich eine Summe in Höhe von "+EfaUtil.cent2euro(meldegeld,true)+
              " (Nettobetrag " + EfaUtil.cent2euro(netto, true) + " zzgl. " + EfaUtil.cent2euro(mwst, true) + " gesetzl. MwSt. 7% gem. § 12 Abs. 8 UStG). "+
              "Der Betrag ist innerhalb von 14 Tagen unter Angabe der Vereins-Nr. und dem Hinweis \"Fahrtenwettbewerb\" auf das Konto Nr. 123 862,"+
              "BLZ 250 501 80, Sparkasse Hannover zu überweisen. Ist dies bereits erfolgt, betrachten Sie diese Rechnung als gegenstandslos. " +
              "Unsere UID lautet DE 115665464 / St.-Nr. 25/206/21626.</fo:block>\n");

      f.write("      <fo:block space-before=\"4mm\">Zusätzlich zu den ausgedruckten Nachweisen liegen im Meldesystem efaWett"+
              " elektronische Fahrtenhefte für Sie zum Abruf bereit. Bitte rufen Sie diese ab und speichern Sie sie in Ihrer"+
              " Software (in efa: -&gt; Administration -&gt; DRV-Fahrtenabzeichen -&gt; Bestätigungsdatei abrufen),"+
              " da sie für die nächste elektronische Meldung benötigt werden.</fo:block>\n");

      f.write("      <fo:block space-before=\"4mm\">Dieses Schriftstück wurde per EDV erstellt und ist daher ohne Unterschrift.</fo:block>\n");
      f.write("      <fo:block space-before=\"4mm\">Mit freundlichen Grüßen,</fo:block>\n");
      f.write("      <fo:block space-before=\"2mm\">Deutscher Ruderverband</fo:block>\n");
      f.write("    </fo:flow>\n");
      f.write("  </fo:page-sequence>\n");

      f.write("  <fo:page-sequence initial-page-number=\"1\" master-reference=\"fahrtenhefte\">\n");
      f.write("    <fo:flow font-family=\"Helvetica\" font-size=\"11pt\" flow-name=\"xsl-region-body\">\n");

      Vector v = fh.getFahrtenhefte();
      int c = 0;
      for (int i=0; i<v.size(); i++) {
        DRVSignatur sig = (DRVSignatur)v.get(i);
        c++;
        if (c > 4 ) c = 1;

        if (c == 1) {
          f.write("      <fo:table space-after=\"20cm\">\n");
          f.write("        <fo:table-column column-width=\"95mm\"/>\n");
          f.write("	   <fo:table-column column-width=\"95mm\"/>\n");
          f.write("	   <fo:table-body>\n");
        }

        if (c == 1 || c == 3) {
          f.write("	     <fo:table-row>\n");
        }

        f.write("            <fo:table-cell border=\"1pt #000000 solid\" height=\"130mm\" padding-top=\"3mm\" padding-bottom=\"3mm\" padding-left=\"3mm\" padding-right=\"3mm\">\n");
        f.write("              <fo:block font-size=\"14pt\" font-weight=\"bold\" text-align=\"center\">elektronisches Fahrtenheft</fo:block>\n");
        f.write("              <fo:block font-size=\"14pt\" font-weight=\"bold\" text-align=\"center\">für "+sig.getVorname()+" "+sig.getNachname()+"</fo:block>\n");
        f.write("              <fo:table space-before=\"3mm\">\n");
        f.write("                <fo:table-column column-width=\"40mm\"/>\n");
        f.write("                <fo:table-column column-width=\"49mm\"/>\n");
        f.write("                <fo:table-body>\n");
        writeRow(f,"Teilnehmernummer:",sig.getTeilnNr());
        writeRow(f,"Vorname:",sig.getVorname());
        writeRow(f,"Nachname:",sig.getNachname());
        writeRow(f,"Jahrgang:",sig.getJahrgang());
        writeRow(f,"Fahrtenabzeichen:",Integer.toString(sig.getAnzAbzeichen()));
        writeRow(f,"Kilometer (ges.):",Integer.toString(sig.getGesKm()));
        writeRow(f,"FAbzeichen (Jug A/B):",Integer.toString(sig.getAnzAbzeichenAB()));
        writeRow(f,"Kilometer (Jug A/B):",Integer.toString(sig.getGesKmAB()));
        writeRow(f,"Meldejahr:",Integer.toString(sig.getJahr()));
        writeRow(f,"Kilometer "+Integer.toString(sig.getJahr())+":",Integer.toString(sig.getLetzteKm()));
        writeRow(f,"Ausstellungsdatum:",sig.getSignaturDatum(true));
        writeRow(f,"Version:",Byte.toString(sig.getVersion()));
        writeRow(f,"Schlüssel:",sig.getKeyName());
        writeRow(f,"Signatur:",sig.getSignaturString());
        writeRow(f,"elektronisches Fahrtenheft (zur Eingabe):",EfaUtil.replace(EfaUtil.replace(sig.toString(),";","~~~~~",true),"~~~~~","; ",true));
        f.write("                </fo:table-body>\n");
        f.write("              </fo:table>\n");
        f.write("            </fo:table-cell>\n");

        if (c == 2 || c == 4 || i+1 == v.size()) {
          f.write("	  </fo:table-row>\n");
        }

        if (c == 4 || i+1 == v.size()) {
	  f.write("      </fo:table-body>\n");
          f.write("    </fo:table>\n");
        }
      }

      f.write("    </fo:flow>\n");
      f.write("  </fo:page-sequence>\n");
      f.write("</fo:root>\n");
      f.close();
    } catch(Exception e) {
      Dialog.error("Beim Erstellen des Ausdrucks trat ein Fehler auf: "+e.getMessage());
      return;
    }

    int pageCount;
    String res = null;
    FileOutputStream out=null;
    String pdf = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".pdf";
    try {
      pageCount = 0;
      out = new FileOutputStream(pdf);
      org.apache.fop.apps.Driver driver =
        new org.apache.fop.apps.Driver(new org.xml.sax.InputSource(xslfo), out);
      driver.setRenderer(org.apache.fop.apps.Driver.RENDER_PDF);
      driver.run();
      pageCount = driver.getResults().getPageCount();
      out.close();
      (new File(xslfo)).delete();
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      res = e.toString();
    } catch(org.apache.fop.apps.FOPException e) {
      e.printStackTrace();
      res = e.toString();
    } catch(IOException e) {
      e.printStackTrace();
      res = e.toString();
    }

    if (out != null) {
      try {
        out.close();
      } catch(IOException e) {
      }
    }

    if (res != null) {
      Dialog.error("Beim Erstellen des PDF-Dokuments trat ein Fehler auf: "+res);
    } else {
      if (drvConfig.acrobat.length() > 0) {
        try {
          String[] cmd = new String[2];
          cmd[0] = drvConfig.acrobat;
          cmd[1] = pdf;
          Runtime.getRuntime().exec(cmd);
        } catch(Exception ee) {
          Dialog.error("Fehler: Acrobat Reader '"+drvConfig.acrobat+"' konnte nicht gestartet werden!");
        }
      } else Dialog.error("Kein Acrobat Reader konfiguriert.");
    }
  }

}