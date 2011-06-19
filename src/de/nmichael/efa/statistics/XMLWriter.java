/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.*;
import java.io.File;

// @i18n complete

public class XMLWriter {

  StatistikDaten sd;
  AusgabeDaten ad;
  org.w3c.dom.Document doc;

  public XMLWriter(StatistikDaten sd, AusgabeDaten ad) {
    this.sd = sd;
    this.ad = ad;
  }

  org.w3c.dom.Element outXML(org.w3c.dom.Node node, String name, String value, String attr, String attrvalue, String attr2, String attrvalue2) {
    if (value != null) {
      org.w3c.dom.Element n = (org.w3c.dom.Element)node.appendChild(doc.createElement(name));
      n.appendChild(doc.createTextNode(value));
      if (attrvalue != null) n.setAttribute(attr,attrvalue);
      if (attrvalue2 != null) n.setAttribute(attr2,attrvalue2);
      return n;
    }
    return null;
  }
  org.w3c.dom.Element outXML(org.w3c.dom.Node node, String name, String value, String attr, String attrvalue) {
    return outXML(node,name,value,attr,attrvalue,null,null);
  }
  org.w3c.dom.Element outXML(org.w3c.dom.Node node, String name, String value) {
    return outXML(node,name,value,null,null,null,null);
  }

  void outXML3(org.w3c.dom.Node node, String name, String[] values, int colspan) {
    if (values != null && values[0] != null) {
      org.w3c.dom.Element n = (org.w3c.dom.Element)node.appendChild(doc.createElement(name));
      if (values[0] != null) n.appendChild(doc.createTextNode(values[0]));
      if (values[1] != null) n.setAttribute("datei",values[1]);
      if (values[2] != null) n.setAttribute("breite",values[2]);
      if (colspan != 1) n.setAttribute("colspan",Integer.toString(colspan));
    }
  }

  public String run() {
   String datei = null;
    try {
      javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
      javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.newDocument();

      org.w3c.dom.Node root = doc.appendChild(doc.createElement("kilometerliste"));
      org.w3c.dom.Node kopf = root.appendChild(doc.createElement("listenkopf"));

      // Kopf
      outXML(kopf,"titel",ad.titel);
      outXML(kopf,"auswertungsDatum",ad.ausgewertetAm);
      outXML(kopf,"auswertungsProgramm",ad.ausgewertetVon,"url",ad.ausgewertetVonURL);
      outXML(kopf,"auswertungsArt",ad.auswertungsArt);
      outXML(kopf,"auswertungsZeitraum",ad.auswertungsZeitraum);
      org.w3c.dom.Node ausgewertet = kopf.appendChild(doc.createElement("ausgewertet"));
      outXML(ausgewertet,"ausgewerteteEintraege",ad.ausgewerteteEintraege);
      for (int i=0; i<ad.auswertungFuer.length; i++)
        outXML(ausgewertet,"ausgewertetFuer",ad.auswertungFuer[i]);
      outXML(ausgewertet,"ausgewertetNurFuer",ad.auswertungNurFuer,"bezeichnung",ad.auswertungNurFuerBez);
      outXML(ausgewertet,"ausgewertetWettNur",ad.auswertungWettNur);

      AusgabeEintrag ae;

      // Wettbewerbsbedingungen
      if (ad.wett_bedingungen != null) {
        org.w3c.dom.Element wettBed = (org.w3c.dom.Element)root.appendChild(doc.createElement("wettBedingungen"));
        for (int i=0; i<ad.wett_bedingungen.length; i++)
        outXML(wettBed,"wettBedZeile",ad.wett_bedingungen[i],
                "fett", (ad.wett_bedingungen_fett.get(new Integer(i)) != null ? "true" : null ),
                "kursiv", (ad.wett_bedingungen_kursiv.get(new Integer(i)) != null ? "true" : null ));
      }

      // Normale Einträge
      if (ad.tabellenTitel != null) {
        org.w3c.dom.Node tabelle = root.appendChild(doc.createElement("tabelle"));
        org.w3c.dom.Element feldnamen = (org.w3c.dom.Element)tabelle.appendChild(doc.createElement("tabellenTitel"));
        if (sd.art == StatistikDaten.ART_FAHRTENBUCH || sd.art == StatistikDaten.BART_FAHRTENBUCH) feldnamen.setAttribute("fahrtenbuch","true");
        for (int i=0; i<ad.tabellenTitel.length; i++)
          if (ad.tabellenTitelBreite == null || ad.tabellenTitelBreite[i] == 1) outXML(feldnamen,"spaltenTitel",ad.tabellenTitel[i]);
          else outXML(feldnamen,"spaltenTitel",ad.tabellenTitel[i],"colspan",Integer.toString(ad.tabellenTitelBreite[i]));

        // Einträge auswerten
        org.w3c.dom.Element eintrag;
        for (ae = ad.ae; ae != null; ae = ae.next) {
          eintrag = (org.w3c.dom.Element)tabelle.appendChild(doc.createElement("eintrag"));
          if (ae.zusammenfassung) eintrag.setAttribute("zusammenfassung","true");

          if (ae.absnr>=0) outXML(eintrag,"absnr",Integer.toString(ae.absnr));
          outXML(eintrag,"nr",ae.nr);
          outXML(eintrag,"name",ae.name);
          outXML(eintrag,"jahrgang",ae.jahrgang);
          outXML(eintrag,"status",ae.status);
          outXML(eintrag,"bezeichnung",ae.bezeichnung);
          outXML3(eintrag,"km",ae.km,ae.colspanKm);
          outXML3(eintrag,"rudkm",ae.rudkm,ae.colspanRudKm);
          outXML3(eintrag,"stmkm",ae.stmkm,ae.colspanStmKm);
          outXML3(eintrag,"fahrten",ae.fahrten,ae.colspanFahrten);
          outXML3(eintrag,"kmfahrt",ae.kmfahrt,ae.colspanKmFahrt);
          outXML3(eintrag,"dauer",ae.dauer,ae.colspanDauer);
          outXML3(eintrag,"kmh",ae.kmh,ae.colspanKmH);
          outXML(eintrag,"mannschKm",ae.mannschKm);
          outXML(eintrag,"anzversch",ae.anzversch);
          outXML(eintrag,"wafakm",ae.wafaKm);
          outXML(eintrag,"zielfahrten",ae.zielfahrten);
          outXML(eintrag,"zusatzDRV",ae.zusatzDRV);
          outXML(eintrag,"zusatzLRVBSommer",ae.zusatzLRVBSommer);
          outXML(eintrag,"zusatzLRVBWinter",ae.zusatzLRVBWinter);
          outXML(eintrag,"zusatzLRVBrbWanderWett",ae.zusatzLRVBrbWanderWett);
          outXML(eintrag,"zusatzLRVBrbFahrtenWett",ae.zusatzLRVBrbFahrtenWett);
          if (ae.fahrtenbuch != null) {
            org.w3c.dom.Node fb = eintrag.appendChild(doc.createElement("fahrtenbuch"));
            for (int i=0; i<ae.fahrtenbuch.length; i++)
              if (ae.fahrtenbuch[i] != null) outXML(fb,"fbFeld",ae.fahrtenbuch[i]);
          }
          if (ae.ww != null) {
            org.w3c.dom.Node ww = eintrag.appendChild(doc.createElement("wwListe"));
            for (int i=0; i<ae.ww.length; i++)
              outXML(ww,"wwFeld",ae.ww[i],"selbst", (ae.ww_selbst[i] ? "true" : null) );
          }
        }
      }

      // Spezialtabelle
      if (ad.tfe != null) {
        org.w3c.dom.Node spezialtabelle = root.appendChild(doc.createElement("spezialTabelle"));

        // Spezialtabelle auswerten
        org.w3c.dom.Element subTabelle = null;
        org.w3c.dom.Element zeile;
        int anzCols=0;
        for (TabellenFolgenEintrag col = ad.tfe; col != null; col = col.next) {
          if (col.fields == null || col.fields.length == 0 || subTabelle == null) {
            subTabelle = (org.w3c.dom.Element)spezialtabelle.appendChild(doc.createElement("subTabelle"));
            anzCols = 0;
          } else {
            zeile = (org.w3c.dom.Element)subTabelle.appendChild(doc.createElement("zeile"));
            zeile.setAttribute("colspan",Integer.toString(col.colspan));

            if (col.colspan>anzCols) {
              anzCols = col.colspan;
              subTabelle.setAttribute("anzCols",Integer.toString(anzCols));
            }

            org.w3c.dom.Element spalte;
            for (int i=0; col.fields!= null && i<col.fields.length; i++) {
              spalte = (org.w3c.dom.Element)zeile.appendChild(doc.createElement("spalte"));
              spalte.setAttribute("color",col.colors[i]);
              if (col.bold[i]) spalte.setAttribute("bold","true");
              spalte.appendChild(doc.createTextNode(col.fields[i]));
            }
          }
        }
      }

      // Wettbewerbe
      if (ad.wett_gruppennamen != null) {
        outXML(root,"wettZeitraumWarnung",ad.wett_zeitraumWarnung);

        org.w3c.dom.Element gruppen;
        for (int g=0; g<ad.wett_gruppennamen.length; g++) {
          gruppen = (org.w3c.dom.Element)root.appendChild(doc.createElement("gruppe"));
          org.w3c.dom.Element gruppennamen = (org.w3c.dom.Element)gruppen.appendChild(doc.createElement("gruppenName"));
          for (int gg=0; gg<ad.wett_gruppennamen[g].length; gg++)
            if (gg==0) outXML(gruppennamen,"gruppenBez",ad.wett_gruppennamen[g][gg]);
            else if (gg==1) outXML(gruppennamen,"gruppenJahrg",ad.wett_gruppennamen[g][gg]);
            else if (gg==2) {
              org.w3c.dom.Element gr_bed = outXML(gruppennamen,"gruppenBed",ad.wett_gruppennamen[g][gg]);
              TMJ tmj = EfaUtil.string2date(ad.wett_gruppennamen[g][gg],0,0,0);
              if (tmj.tag   != 0) gr_bed.setAttribute("wert1",Integer.toString(tmj.tag));
              if (tmj.monat != 0) gr_bed.setAttribute("wert2",Integer.toString(tmj.monat));
              if (tmj.jahr  != 0) gr_bed.setAttribute("wert3",Integer.toString(tmj.jahr));
            } else outXML(gruppennamen,"gruppenZusatz",ad.wett_gruppennamen[g][gg]);

          org.w3c.dom.Element eintrag;
          for (ae = ad.wett_teilnehmerInGruppe[g]; ae != null; ae = ae.next) {
            eintrag = (org.w3c.dom.Element)gruppen.appendChild(doc.createElement("wettEintrag"));
            eintrag.setAttribute("erfuellt", (ae.w_erfuellt ? "true" : "false") );
            outXML(eintrag,"wettName",ae.w_name);
            outXML(eintrag,"wettKilometer",ae.w_kilometer);
            outXML(eintrag,"wettJahrgang",ae.w_jahrgang);
            org.w3c.dom.Element zus = outXML(eintrag,"wettZusatz",ae.w_additional);
            if (zus != null && ae.w_attr1 != null) zus.setAttribute("wert1",ae.w_attr1);
            if (zus != null && ae.w_attr2 != null) zus.setAttribute("wert2",ae.w_attr2);
            outXML(eintrag,"wettWarnung",ae.w_warnung);

            org.w3c.dom.Element detail;
            if (ae.w_detail != null)
              for (int i=0; i<ae.w_detail.length; i++) {
                detail = (org.w3c.dom.Element)eintrag.appendChild(doc.createElement("wettDetail"));
                for (int j=0; j<ae.w_detail[i].length; j++)
                  outXML(detail,"wettDetailFeld",ae.w_detail[i][j]);
              }
          }
        }
      }

      // Zusatztabelle
      if (ad.additionalTable != null) {
        org.w3c.dom.Node ztab = root.appendChild(doc.createElement("zusatzTabelle"));
        for (int i=0; i<ad.additionalTable.length; i++) {
          org.w3c.dom.Node ztabZeile = ztab.appendChild(doc.createElement("zusatzTabelleZeile"));
          for (int j=0; j<ad.additionalTable[i].length; j++) {
            outXML(ztabZeile,"zusatzTabelleSpalte",ad.additionalTable[i][j]);
          }
        }
      }

      javax.xml.transform.TransformerFactory trfac = javax.xml.transform.TransformerFactory.newInstance();
      javax.xml.transform.Transformer trans;
      if (sd.stylesheet == null) trans = trfac.newTransformer();
      else {
        if (!EfaUtil.canOpenFile(sd.stylesheet)) {
            LogString.logstring_fileOpenFailed(sd.stylesheet, International.getString("Stylesheet"));
            return "";
        }
        trans = trfac.newTransformer(new javax.xml.transform.stream.StreamSource(new File(sd.stylesheet)));
      }

      // Schreiben der Datei (wenn Tmp != null, dann wird Tmp-Datei geschrieben)
      datei = (sd.ausgabeDateiTmp == null ? sd.ausgabeDatei : sd.ausgabeDateiTmp ).trim();
      trans.transform(new javax.xml.transform.dom.DOMSource(doc),new javax.xml.transform.stream.StreamResult(new File(datei)));

    } catch(javax.xml.parsers.ParserConfigurationException e) {
      return e.toString();
    } catch(javax.xml.transform.TransformerConfigurationException e) {
      return e.toString();
    } catch(javax.xml.transform.TransformerException e) {
      return e.toString();
    } catch(javax.xml.transform.TransformerFactoryConfigurationError e) {
      return e.toString();
    } catch(javax.xml.parsers.FactoryConfigurationError e) {
      return e.toString();
    }
    return null; // korrektes Ende!
  }
}