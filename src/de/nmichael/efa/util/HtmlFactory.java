/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.*;
import java.util.*;
import java.io.*;

// @i18n complete

public class HtmlFactory {

    public static void writeHeader(BufferedWriter f, String title, boolean withH1) throws IOException {
        f.write("<html>\n");
        f.write("<head>\n");
        f.write("<meta http-equiv=\"content-type\" content=\"text/html; charset=" + Daten.ENCODING_UTF + "\">\n");
        f.write("<title>" + title + "</title>\n");
        f.write("</head>\n");
        f.write("<body>\n");
        if (withH1) {
            f.write("<h1 align=\"center\">" + title + "</h1>\n");
        }
    }

    public static void writeFooter(BufferedWriter f) throws IOException {
        f.write("</body>\n");
        f.write("</html>\n");
    }

    public static String createMailto(String email) {
        String filename = Daten.efaTmpDirectory+"mailto.html";
        try {
            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),Daten.ENCODING_UTF));
            writeHeader(f, International.getMessage("email an {receiver}", email), true);
            f.write("<form method=\"post\" action=\"" + Daten.INTERNET_EFAMAIL + "\">\n");
            f.write("<table align=\"center\">\n");
            f.write("<tr><td><b>" + International.getString("Von") +
                    " (" + International.getString("Name") + "):</b></td><td><input type=\"text\" name=\"absender\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("Von") +
                    " (" + International.getString("email") + "):</b></td><td><input type=\"text\" name=\"email\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("Von") +
                    " (" + International.getString("Verein") + "):</b></td><td><input type=\"text\" name=\"verein\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("An") +
                    ":</b></td><td><tt>" + Daten.EFAEMAILNAME + " &lt;" +email + "&gt;</tt></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("Betreff") +
                    ":</b></td><td><input type=\"text\" name=\"betreff\" size=\"30\"></td></tr>\n");
            f.write("<tr><td colspan=\"2\"><textarea name=\"nachricht\" cols=\"50\" rows=\"10\" wrap=\"physical\"></textarea></td></tr>\n");
            f.write("<tr><td colspan=\"2\" align=\"center\"><input type=\"submit\" value=\"" +
                    International.getString("Abschicken") + "\"><br>\n");
            f.write("<font color=\"red\"><b>" +
                    International.getString("Bitte stelle vor dem Abschicken eine Verbindung zum Internet her!") +
                    "</b></font></td></tr>\n");
            f.write("</table>\n");
            f.write("</form>\n");
            writeFooter(f);
            f.close();
        } catch(Exception e) {
            return null;
        }
        return filename;
    }

    public static String createReload() {
        String filename = Daten.efaTmpDirectory+"reload.html";
        try {
            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),Daten.ENCODING_UTF));
            writeHeader(f, "Reloading Page ...", true);
            writeFooter(f);
            f.close();
        } catch(Exception e) {
            return null;
        }
        return filename;
    }

    public static String createTour() {
        String filename = Daten.efaTmpDirectory+"tour.html";
        try {
            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),Daten.ENCODING_UTF));
            writeHeader(f, International.getString("efa-Tour"), true);
            f.write("<p><b><a href=\"file:" + Daten.efaDocDirectory + "tour/index.html\">" +
                    International.getString("Tour starten") +
                    "</a></b></p>\n");
            writeFooter(f);
            f.close();
        } catch(Exception e) {
            return null;
        }
        return filename;
    }

    public static String createRegister() {
        String filename = Daten.efaTmpDirectory+"register.html";
        try {
            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),Daten.ENCODING_UTF));
            writeHeader(f, International.getString("Registrieren"), true);
            f.write("<p>" +
                    International.getString("Bitte unterstütze die Weiterentwicklung von efa, indem Du Dich kurz als Nutzer "+
                    "von efa registrierst. Mit Deinen Angaben hilfst Du zu erkennen, wo auf der Welt efa wie eingesetzt "+
                    "wird, so daß zukünftige Versionen von efa optimal auf die Anforderungen und Bedürfnisse ihrer Nutzer " +
                    "abgestimmt werden können.") +
                    "</p>\n");
            f.write("<form method=\"post\" action=\"" + Daten.INTERNET_EFAMAIL + "\">\n");
            f.write("<input type=\"hidden\" name=\"betreff\" value=\"User efa " + Daten.VERSIONID + "\">\n");
            f.write("<table align=\"center\">\n");
            f.write("<tr><td><b>" + International.getString("Name") +
                    ":</b></td><td><input type=\"text\" name=\"absender\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("email-Adresse") +
                    ":</b></td><td><input type=\"text\" name=\"email\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("Vereinsname") +
                    ":</b></td><td><input type=\"text\" name=\"verein\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("Land") +
                    ":</b></td><td><input type=\"text\" name=\"land\" size=\"30\"></td></tr>\n");
            f.write("<tr><td><b>" + International.getString("Bundesland / Region") +
                    ":</b></td><td><input type=\"text\" name=\"bundesland\" size=\"30\"></td></tr>\n");
            f.write("<tr><td colspan=\"2\">" + International.getString("Bitte wähle alle zutreffenden Punkte aus:") + "<<br>\n");
            f.write("<input type=\"checkbox\" name=\"useEvaluate\" value=\"ja\"> " +
                    International.getString("Ich/wir evaluieren efa.") + "<br>\n");
            f.write("<input type=\"checkbox\" name=\"usePrivate\" value=\"ja\"> " +
                    International.getString("Ich benutze efa privat, um meine eigenen Fahrten zu erfassen.") + "<br>\n");
            f.write("<input type=\"checkbox\" name=\"useClubHome\" value=\"ja\"> " +
                    International.getString("Ich benutze efa für meinen Ruderverein, um unsere Fahrten nachträglich auszuwerten.") + "<br>\n");
            f.write("<input type=\"checkbox\" name=\"useClubEvaluate\" value=\"ja\"> " +
                    International.getString("Wir haben efa testweise im Bootshaus installiert.") + "<br>\n");
            f.write("<input type=\"checkbox\" name=\"useClubDirect\" value=\"ja\"> " +
                    International.getString("Wir haben efa im Bootshaus installiert. Die Mitglieder tragen alle ihre Fahrten direkt in efa ein.") + "<br>\n");
            f.write("</td></tr>\n");
            f.write("<tr><td colspan=\"2\"><b>" + International.getString("Bemerkungen") +
                    ":</b><br><textarea name=\"nachricht\" cols=\"40\" rows=\"5\" wrap=\"physical\"></textarea></td></tr>\n");
            f.write("<tr><td colspan=\"2\"><input type=\"checkbox\" name=\"vereinsliste\" checked value=\"ja\"> " +
                    International.getString("Ich bin damit einverstanden, daß mein Verein in der Liste von Vereinen, die efa benutzen, erwähnt wird.") +
                    "</td></tr>\n");
            f.write("<tr><td colspan=\"2\"><input type=\"checkbox\" name=\"mailingliste\" checked value=\"ja\"> " +
                    International.getString("Ich möchte mit meiner email-Adresse in die efa-Mailingliste aufgenommen werden und immer über aktuelle " +
                    "Informationen und neue Versionen informiert werden.") +
                    "</td></tr>\n");
            f.write("<tr><td colspan=\"2\" align=\"center\"><input type=\"submit\" value=\"" +
                    International.getString("Abschicken") + "\"><br>\n");
            f.write("<font color=\"red\"><b>" +
                    International.getString("Bitte stelle vor dem Abschicken eine Verbindung zum Internet her!") +
                    "</b></font></td></tr>\n");
            f.write("</table>\n");
            f.write("</form>\n");
            writeFooter(f);
            f.close();
        } catch(Exception e) {
            return null;
        }
        return filename;
    }

}
