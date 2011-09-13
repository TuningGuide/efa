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

import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import java.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.direkt.*;

// @i18n complete
public class EmailSender {

    // Constructor usually not needed (everything static), except for Plungin-Test in Daten.java
    public EmailSender() {
        javax.mail.Session session = javax.mail.Session.getInstance(new Properties(), null); // just dummy statement
    }
    static volatile EmailSenderThread sendThread = null;
    static volatile boolean sendCompleted = true;

    public static synchronized void sendEmail(MessageRecord msg, String adressen) {

        if (sendThread != null) {
            return; // verhindern, daß durch Fehler sich das Senden rekursiv aufruft
        }
        if (!sendCompleted) {
            return;     // verhindern, daß durch Fehler sich das Senden rekursiv aufruft
        }
        try {
            sendCompleted = false;
            sendThread = new EmailSenderThread(msg, adressen);
            sendThread.start();
            sendCompleted = true;
        } catch (NoClassDefFoundError e1) {
            Logger.log(Logger.ERROR, Logger.MSG_ERR_SENDMAILFAILED_PLUGIN,
                    International.getMessage("Nachricht als email versenden nicht möglich, da das EMAIL-PLUGIN nicht installiert ist. "
                    + "Bitte schaue unter {url} zur Installation des Plugins.", Daten.pluginWWWdirectory + Daten.PLUGIN_EMAIL_HTML));
        } catch (Exception e2) {
        }

    }

    public static boolean emailPluginInstalled() {

        try {
            EmailSenderThread dummy = new EmailSenderThread(null, null);
            return true;
        } catch (NoClassDefFoundError e1) {
            return false;
        }
    }
}

class EmailSenderThread extends Thread {

    private MessageRecord msg;
    private String adressen;

    public EmailSenderThread(MessageRecord msg, String adressen) {
        this.msg = msg;
        this.adressen = adressen;
    }

    public void run() {
        if (Daten.efaConfig == null) {
            return;
        }
        if (Daten.efaConfig.getValueEfaDirekt_emailServer().length() == 0) {
            Logger.log(Logger.ERROR, Logger.MSG_ERR_SENDMAILFAILED_CFG,
                    International.getString("Nachricht als email versenden nicht möglich, da kein SMTP-Server konfiguriert ist."));
            return;
        }
        if (Daten.efaConfig.getValueEfaDirekt_emailAbsender().length() == 0) {
            Logger.log(Logger.ERROR, Logger.MSG_ERR_SENDMAILFAILED_CFG,
                    International.getString("Nachricht als email versenden nicht möglich, da keine Absender-Adresse konfiguriert ist."));
            return;
        }
        int retryCount = 0;
        do {
            retryCount++;
            try {
                boolean auth = (Daten.efaConfig.getValueEfaDirekt_emailUsername().length() > 0
                        && Daten.efaConfig.getValueEfaDirekt_emailPassword().length() > 0);
                Properties props = new Properties();
                props.put("mail.smtp.host", Daten.efaConfig.getValueEfaDirekt_emailServer());
                props.put("mail.smtp.port", Integer.toString(Daten.efaConfig.getValueEfaDirekt_emailPort()));
                if (auth) {
                    props.put("mail.smtp.auth", "true");
                }
                if (Logger.isDebugLoggin()) {
                    props.put("mail.debug", "true");
                }
                MailAuthenticator ma = null;
                if (auth) {
                    ma = new MailAuthenticator(Daten.efaConfig.getValueEfaDirekt_emailUsername(), Daten.efaConfig.getValueEfaDirekt_emailPassword());
                }
                String charset = "ISO-8859-1";
                javax.mail.Session session = javax.mail.Session.getInstance(props, ma);
                com.sun.mail.smtp.SMTPMessage mail = new com.sun.mail.smtp.SMTPMessage(session);
                mail.setAllow8bitMIME(true);
                mail.setHeader("X-Mailer", Daten.EFA_SHORTNAME + " " + Daten.VERSION);
                mail.setHeader("Content-Type", "text/plain; charset=" + charset);
                mail.setFrom(new javax.mail.internet.InternetAddress(
                        (Daten.efaConfig.getValueEfaDirekt_emailAbsenderName().length() > 0
                        ? Daten.efaConfig.getValueEfaDirekt_emailAbsenderName()
                        : "efa") + " <" + Daten.efaConfig.getValueEfaDirekt_emailAbsender() + ">"));
                mail.setRecipients(com.sun.mail.smtp.SMTPMessage.RecipientType.TO, javax.mail.internet.InternetAddress.parse(adressen));
                mail.setSubject((Daten.efaConfig.getValueEfaDirekt_emailBetreffPraefix().length() > 0
                        ? "[" + Daten.efaConfig.getValueEfaDirekt_emailBetreffPraefix() + "] " : "") + msg.getText(), charset);
                mail.setSentDate(new Date());
                mail.setText("## " + International.getString("Absender") + ": " + msg.getFrom() + "\n"
                        + "## " + International.getString("Betreff") + " : " + msg.getSubject() + "\n\n"
                        + msg.getText()
                        + (Daten.efaConfig.getValueEfaDirekt_emailSignatur().length() > 0 ? "\n\n-- \n"
                        + EfaUtil.replace(Daten.efaConfig.getValueEfaDirekt_emailSignatur(), "$$", "\n", true) : ""), charset);
                com.sun.mail.smtp.SMTPTransport t = (com.sun.mail.smtp.SMTPTransport) session.getTransport("smtp");
                if (auth) {
                    t.connect(Daten.efaConfig.getValueEfaDirekt_emailServer(),
                            Daten.efaConfig.getValueEfaDirekt_emailUsername(),
                            Daten.efaConfig.getValueEfaDirekt_emailPassword());
                } else {
                    t.connect();
                }
                t.send(mail, mail.getAllRecipients());
                break; // Retry-Schleife verlassen
            } catch (Exception e) {
                Logger.log((retryCount < 3 ? Logger.INFO : Logger.WARNING), Logger.MSG_ERR_SENDMAILFAILED_ERROR,
                        International.getString("Nachricht konnte nicht als email versendet werden")
                        + "(" + (retryCount < 3
                        ? International.getMessage("{n}. Versuch", retryCount)
                        : International.getString("endgültig fehlgeschlagen")) + "): "
                        + e.toString() + " " + e.getMessage());
                if (retryCount < 3) {
                    try {
                        Thread.sleep(10000);
                    } catch (Exception ee) {
                    }
                }
            }
        } while (retryCount < 3);
        EmailSender.sendThread = null;
    }
}

class MailAuthenticator extends javax.mail.Authenticator {

    String username;
    String password;

    public MailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new javax.mail.PasswordAuthentication(username, password);
    }
}
