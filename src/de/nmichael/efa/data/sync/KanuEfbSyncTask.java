/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.sync;

// @i18n complete

import java.io.*;
import java.net.*;
import java.util.*;
import org.xml.sax.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.gui.EfaConfigDialog;
import de.nmichael.efa.gui.ProgressDialog;
import de.nmichael.efa.gui.BaseTabbedDialog;
import de.nmichael.efa.gui.dataedit.ProjectEditDialog;
import javax.swing.JDialog;


public class KanuEfbSyncTask extends ProgressTask {

    private AdminRecord admin;
    private Logbook logbook;
    private String loginurl;
    private String cmdurl;
    private String username;
    private String password;
    private HttpCookie sessionCookie;
    private long lastSync;
    private long thisSync;
    private boolean loggedIn = false;

    public KanuEfbSyncTask(Logbook logbook, AdminRecord admin) {
        super();
        this.admin = admin;
        getConfigValues();
        this.logbook = logbook;
    }

    private void getConfigValues() {
        this.loginurl = Daten.efaConfig.getValueKanuEfb_urlLogin();
        this.cmdurl = Daten.efaConfig.getValueKanuEfb_urlRequest();
        this.username = Daten.project.getClubKanuEfbUsername();
        this.password = Daten.project.getClubKanuEfbPassword();
        this.lastSync = Daten.project.getClubKanuEfbLastSync();
        if (this.lastSync == IDataAccess.UNDEFINED_LONG) {
            this.lastSync = 0;
        }
    }

    private void buildRequestHeader(StringBuilder s, String requestName) {
        s.append("<?xml version='1.0' encoding='UTF-8' ?>\n");
        s.append("<xml>\n");
        s.append("<request command=\""+requestName+"\">\n");
    }

    private void buildRequestFooter(StringBuilder s) {
        s.append("</request>\n");
        s.append("</xml>\n");
    }

    private KanuEfbXmlResponse sendRequest(String request) throws Exception {
        if (Logger.isTraceOn(Logger.TT_SYNC)) {
            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Sende Synchronisierungs-Anfrage an "+cmdurl+":\n"+request);
        }
        URL url = new URL(this.cmdurl);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Cookie", (sessionCookie != null ? sessionCookie.toString() : "null"));
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write("xmlCode=" + URLEncoder.encode(request, "UTF-8"));
        out.flush();
        out.close();

        return getResponse(connection, new BufferedInputStream(connection.getInputStream()));
    }

    private KanuEfbXmlResponse getResponse(URLConnection connection, BufferedInputStream in) {
        if (Logger.isTraceOn(Logger.TT_SYNC)) {
            try {
                in.mark(1024*1024);
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Antwort von Kanu-eFB:");
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "    -- HEADER START --");
                Map<String, List<String>> m = connection.getHeaderFields();
                for (String header : m.keySet()) {
                    logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "    " + header + "=" + connection.getHeaderField(header));
                }
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "    -- HEADER END --");
                BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                String s;
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "    -- RESPONSE START --");
                while ((s = buf.readLine()) != null) {
                    logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "   " + s);
                }
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "    -- RESPONSE END --");
                in.reset();
            } catch (Exception e) {
                Logger.log(e);
            }
        }

        KanuEfbXmlResponse response = null;
        try {
            XMLReader parser = EfaUtil.getXMLReader();
            response = new KanuEfbXmlResponse(this);
            parser.setContentHandler(response);
            parser.parse(new InputSource(in));
        } catch(Exception e) {
            Logger.log(e);
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Exception:" + e.toString());
            }
            response = null;
        }

        if (Logger.isTraceOn(Logger.TT_SYNC) && response != null) {
            response.printAll();
        }

        return response;
    }

    private boolean login() {
        try {
            loggedIn = false;
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Login auf " + this.loginurl+ " mit Benutzername " + this.username + " ...");
            }
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            URL url = new URL(this.loginurl);
            URLConnection connection = url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(true);
            connection.setRequestProperty ("Content-Type","application/x-www-form-urlencoded");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	    out.write("username=" + username+ "&password="+password);
            out.flush();
            out.close();

            KanuEfbXmlResponse response = getResponse(connection, new BufferedInputStream(connection.getInputStream()));
            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();
            for (HttpCookie cookie : cookies) {
                if (Logger.isTraceOn(Logger.TT_SYNC)) {
                    logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Session Cookie: " + cookie);
                }
                sessionCookie = cookie;
            }

            int retCode = (response == null ? -1 : EfaUtil.stringFindInt(response.getValue(0, "code"), -1));
            if (retCode != 1) {
                String msg = (response == null ? "unbekannt" : response.getValue(0, "message"));
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORLOGIN, "Login fehlgeschlagen: Code "+retCode+" ("+msg+")");
                return false;
            }

        } catch (Exception e) {
            Logger.logdebug(e);
            logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORLOGIN, "Login fehlgeschlagen: "+e.toString());
            return false;
        }
        loggedIn = true;
        if (Logger.isTraceOn(Logger.TT_SYNC)) {
            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Login erfolgreich.");
        }
        return true;
    }

    private boolean syncUsers() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisiere Personen ...");
            Persons persons = Daten.project.getPersons(false);

            StringBuilder request = new StringBuilder();
            buildRequestHeader(request, "SyncUsers");
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sende Synchronisierungs-Anfrage für alle Personen ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncUsers")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisierungs-Antwort erhalten für " + response.getNumberOfRecords() + " Personen ...");
                for (int i=0; i<response.getNumberOfRecords(); i++) {
                    Hashtable<String,String> fields = response.getFields(i);
                    boolean ok = false;
                    String personName = "<unknown>";
                    String firstName = fields.get("firstname");
                    String lastName = fields.get("lastname");
                    String dateOfBirth = fields.get("dateOfBirth");
                    String efbId = fields.get("ID");
                    if (firstName != null && lastName != null) {
                        firstName = firstName.trim();
                        lastName = lastName.trim();
                        personName = PersonRecord.getFullName(firstName, lastName, "", false);
                        PersonRecord p = persons.getPerson(personName, thisSync);
                        if (efbId != null && p != null) {
                            efbId = efbId.trim();
                            if (!efbId.equals(p.getEfbId())) {
                                p.setEfbId(efbId);
                                persons.data().update(p);
                            }
                            ok = true;
                        }
                    }
                    if (Logger.isTraceOn(Logger.TT_SYNC)) {
                        if (ok) {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  Synchronisierungs-Antwort für Person: " + personName + " (EfbId=" + efbId + ")");
                        } else {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "  Synchronisierungs-Antwort für unbekannte Person: " + personName);
                        }
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Ungültige Synchronisierungs-Antwort.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncBoats() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisiere Boote ...");
            StringBuilder request = new StringBuilder();
            buildRequestHeader(request, "SyncBoats");
            Boats boats = Daten.project.getBoats(false);
            DataKeyIterator it = boats.data().getStaticIterator();
            DataKey k = it.getFirst();
            int reqCnt = 0;
            Hashtable<String,UUID> efaIds = new Hashtable<String,UUID>();
            while (k != null) {
                BoatRecord r = (BoatRecord)boats.data().get(k);
                if (r != null && r.isValidAt(thisSync) &&
                    (r.getLastModified() > lastSync || r.getEfbId() == null || r.getEfbId().length() == 0)) {
                    if (Logger.isTraceOn(Logger.TT_SYNC)) {
                        logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  erstelle Synchronisierungs-Anfrage für Boot: " + r.getQualifiedName());
                    }
                    request.append("<boat><name>"+r.getQualifiedName()+"</name></boat>\n");
                    efaIds.put(r.getQualifiedName(), r.getId());
                    reqCnt++;
                }
                k = it.getNext();
            }
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sende Synchronisierungs-Anfrage für " + reqCnt + " Boote ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncBoats")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisierungs-Antwort erhalten für " + response.getNumberOfRecords() + " Boote ...");
                for (int i=0; i<response.getNumberOfRecords(); i++) {
                    Hashtable<String,String> fields = response.getFields(i);
                    boolean ok = false;
                    String boatName = fields.get("label");
                    String efbId = fields.get("id");
                    if (boatName != null) {
                        boatName = boatName.trim();
                        UUID efaId = efaIds.get(boatName);
                        if (efaId != null && efbId != null) {
                            BoatRecord b = boats.getBoat(efaId, thisSync);
                            efbId = efbId.trim();
                            if (b != null) {
                                if (!efbId.equals(b.getEfbId())) {
                                    b.setEfbId(efbId);
                                    boats.data().update(b);
                                }
                                ok = true;
                            }
                        }
                    }
                    if (ok) {
                        if (Logger.isTraceOn(Logger.TT_SYNC)) {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  Synchronisierungs-Antwort für Boot: "+boatName+" (EfbId="+efbId+")");
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Ungültige Synchronisierungs-Antwort für Boot: "+boatName);
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Ungültige Synchronisierungs-Antwort.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncWaters() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisiere Gewässer ...");
            StringBuilder request = new StringBuilder();
            buildRequestHeader(request, "SyncWaters");
            Waters waters = Daten.project.getWaters(false);
            DataKeyIterator it = waters.data().getStaticIterator();
            DataKey k = it.getFirst();
            int reqCnt = 0;
            Hashtable<String,UUID> efaIds = new Hashtable<String,UUID>();
            while (k != null) {
                WatersRecord r = (WatersRecord)waters.data().get(k);
                if (r != null && r.isValidAt(thisSync) &&
                    (r.getLastModified() > lastSync || r.getEfbId() == null || r.getEfbId().length() == 0)) {
                    if (Logger.isTraceOn(Logger.TT_SYNC)) {
                        logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  erstelle Synchronisierungs-Anfrage für Gewässer: " + r.getQualifiedName());
                    }
                    request.append("<water><name>"+r.getQualifiedName()+"</name></water>\n");
                    efaIds.put(r.getQualifiedName(), r.getId());
                    reqCnt++;
                }
                k = it.getNext();
            }
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sende Synchronisierungs-Anfrage für " + reqCnt + " Gewässer ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncWaters")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisierungs-Antwort erhalten für " + response.getNumberOfRecords() + " Gewässer ...");
                for (int i=0; i<response.getNumberOfRecords(); i++) {
                    Hashtable<String,String> fields = response.getFields(i);
                    boolean ok = false;
                    String watersName = fields.get("label");
                    String efbId = fields.get("id");
                    if (watersName != null) {
                        watersName = watersName.trim();
                        UUID efaId = efaIds.get(watersName);
                        if (efaId != null && efbId != null) {
                            WatersRecord w = waters.getWaters(efaId);
                            efbId = efbId.trim();
                            if (w != null) {
                                if (!efbId.equals(w.getEfbId())) {
                                    w.setEfbId(efbId);
                                    waters.data().update(w);
                                }
                                ok = true;
                            }
                        }
                    }
                    if (ok) {
                        if (Logger.isTraceOn(Logger.TT_SYNC)) {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  Synchronisierungs-Antwort für Gewässer: "+watersName+" (EfbId="+efbId+")");
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Ungültige Synchronisierungs-Antwort für Gewässer: "+watersName);
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Ungültige Synchronisierungs-Antwort.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncTrips() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisiere Fahrten ...");
            StringBuilder request = new StringBuilder();
            buildRequestHeader(request, "SyncTrips");

            Boats boats = Daten.project.getBoats(false);
            Persons persons = Daten.project.getPersons(false);
            Destinations destinations = Daten.project.getDestinations(false);
            Waters waters = Daten.project.getWaters(false);

            DataKeyIterator it = logbook.data().getStaticIterator();
            DataKey k = it.getFirst();
            int reqCnt = 0;
            Hashtable<String,LogbookRecord> efaEntryIds = new Hashtable<String,LogbookRecord>();
            while (k != null) {
                LogbookRecord r = (LogbookRecord)logbook.data().get(k);
                if (r != null &&
                    (r.getLastModified() > lastSync || r.getSyncState() == IDataAccess.UNDEFINED_INT || r.getSyncState() == 0)) {
                    for (int i=0; i<=LogbookRecord.CREW_MAX; i++) {
                        UUID pId = r.getCrewId(i);
                        if (pId != null) {
                            PersonRecord p = persons.getPerson(pId, thisSync);
                            if (p != null && p.getEfbId() != null && p.getEfbId().length() > 0 &&
                                r.getDate() != null) {
                                if (Logger.isTraceOn(Logger.TT_SYNC)) {
                                    logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  erstelle Synchronisierungs-Anfrage für Fahrt: " + r.getQualifiedName()+
                                            "; Person: "+p.getQualifiedName());
                                }
                                BoatRecord b = (r.getBoatId() != null ? boats.getBoat(r.getBoatId(), thisSync) : null);
                                DestinationRecord d = (r.getDestinationId() != null ? destinations.getDestination(r.getDestinationId(), thisSync): null);
                                WatersRecord w = (d != null && d.getWatersIdList() != null && d.getWatersIdList().length() > 0 ?
                                    waters.getWaters(d.getWatersIdList().get(0)) : null); // @todo (P6) get all other waters as well, currently for test purpose only first one!
                                String startDate = r.getDate().getDateString("YYYY-MM-DD");
                                String endDate = (r.getEndDate() != null ? r.getEndDate().getDateString("YYYY-MM-DD") : startDate);
                                String tripId = logbook.getName()+"_"+r.getEntryId().toString();
                                request.append("<trip>");
                                request.append("<tripID>" + tripId + "</tripID>");
                                request.append("<userID>" + p.getEfbId() + "</userID>");
                                if (b != null && b.getEfbId() != null && b.getEfbId().length() > 0) {
                                    request.append("<boatID>" + b.getEfbId() + "</boatID>");
                                } else {
                                    request.append("<boatText>" + (b != null ? b.getQualifiedName() : r.getBoatName()) + "</boatText>");
                                }
                                request.append("<begdate>" + startDate + "</begdate>");
                                request.append("<enddate>" + endDate + "</enddate>");
                                if (r.getStartTime() != null) {
                                    // @todo (P6) request.append("<begtime>" + r.getStartTime().toString() + "</begtime>");
                                }
                                if (r.getEndTime() != null) {
                                    // @todo (P6) request.append("<endtime>" + r.getEndTime().toString() + "</endtime>");
                                }

                                request.append("<lines>");
                                request.append("<line>");
                                if (w != null) {
                                    if (w.getEfbId() != null && w.getEfbId().length() > 0) {
                                        request.append("<waterID>" + w.getEfbId() + "</waterID>");
                                    } else {
                                        request.append("<waterText>" + w.getName() + "</waterText>");
                                    }
                                } else {
                                    request.append("<waterText>"+ (r.getDestinationId() != null ? r.getDestinationAndVariantName() : r.getDestinationName()) + "</waterText>");
                                }
                                if (d != null && d.getStart() != null && d.getStart().length() > 0) {
                                    request.append("<fromText>" + d.getStart() + "</fromText>");
                                }
                                if (d != null && d.getEnd() != null && d.getEnd().length() > 0) {
                                    request.append("<toText>" + d.getEnd() + "</toText>");
                                }
                                request.append("<kilometers>" + r.getDistance().getStringValueInKilometers() + "</kilometers>");
                                request.append("</line>");
                                request.append("</lines>");

                                if (r.getComments() != null && r.getComments().length() > 0) {
                                    request.append("<comment><![CDATA[" + d.getEnd() + "]]></comment>");
                                }

                                request.append("<changeDate>" + r.getLastModified() + "</changeDate>");
                                request.append("<status>" + "1" + "</status>");
                                request.append("<deleted>" + "0" + "</deleted>");
                                request.append("</trip>\n");
                                efaEntryIds.put(tripId, r);
                                reqCnt++;
                                
                            }
                        }
                    }
                } else {
                    if (r != null) {
                        if (Logger.isTraceOn(Logger.TT_SYNC)) {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  keine Synchronisierungs-Anfrage für unveränderte Fahrt: " + r.getQualifiedName());
                        }
                    }
                }
                k = it.getNext();
            }
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sende Synchronisierungs-Anfrage für " + reqCnt + " Fahrten ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            int okCnt = 0;
            if (response != null && response.isResponseOk("SyncTrips")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisierungs-Antwort erhalten für " + response.getNumberOfRecords() + " Fahrten ...");
                for (int i=0; i<response.getNumberOfRecords(); i++) {
                    Hashtable<String,String> fields = response.getFields(i);
                    boolean ok = false;
                    String tripId = fields.get("tripID");
                    int result = EfaUtil.string2int(fields.get("result"), -1);
                    LogbookRecord r = null;
                    if (tripId != null) {
                        tripId = tripId.trim();
                        r = efaEntryIds.get(tripId);
                    }
                    String resultText = fields.get("resultText");
                    if (r != null) {
                        if (result == 0) {
                            r.setSyncState(1);
                            logbook.data().update(r);
                            ok = true;
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Fehler beim Synchronisieren von Fahrt: Trip ID "+tripId+" unbekannt ("+result+" - "+resultText+")");
                    }
                    if (ok) {
                        okCnt++;
                        if (Logger.isTraceOn(Logger.TT_SYNC)) {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  Fahrt erfolgreich synchronisiert: "+r.toString());
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Fehler beim Synchronisieren von Fahrt: "+tripId+" ("+result+" - "+resultText+")");
                    }
                }
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, okCnt + " Fahrten erfolgreich synchronisiert.");
            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Ungültige Synchronisierungs-Antwort.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncDone() {
        try {
            if (loggedIn) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Logout ...");

                StringBuilder request = new StringBuilder();
                buildRequestHeader(request, "SyncDone");
                buildRequestFooter(request);

                KanuEfbXmlResponse response = sendRequest(request.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void run() {
        setRunning(true);
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
        }
        int i = 0;
        thisSync = System.currentTimeMillis();
        logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Beginne Synchronisierung mit Kanu-eFB ...");
        logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Startzeit der Synchronisierung: " +
                EfaUtil.getTimeStamp(thisSync) + " (" + thisSync + ")");
        logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Letzte Synchronisierung: " +
                (lastSync == 0 ? "noch nie" : EfaUtil.getTimeStamp(lastSync)) + " (" + lastSync + ")");
        while(true) {
            if (!login()) {
                break;
            }
            setCurrentWorkDone(++i);
            if (!syncUsers()) {
                break;
            }
            setCurrentWorkDone(++i);
            if (!syncBoats()) {
                break;
            }
            setCurrentWorkDone(++i);
            if (!syncWaters()) {
                break;
            }
            setCurrentWorkDone(++i);
            if (!syncTrips()) {
                break;
            }
            setCurrentWorkDone(++i);
            break;
        }
        syncDone();
        setCurrentWorkDone(++i);
        if (i == getAbsoluteWork()) {
            Daten.project.setClubKanuEfbLastSync(thisSync);
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronisierung mit Kanu-eFB erfolgreich beendet.");
        } else {
            logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORABORTSYNC, "Synchronisierung mit Kanu-eFB wegen Fehlern abgebrochen.");
        }
        setDone();
    }

    public int getAbsoluteWork() {
        return 6;
    }

    public String getSuccessfullyDoneMessage() {
        return International.getString("Synchronisation beendet.");
    }

    private void logInfo(String type, String key, String msg) {
        Logger.log(type, key, msg);
        //if (!type.equals(Logger.DEBUG)) {
            logInfo(msg+"\n");
        //}
    }

    public void startSynchronization(ProgressDialog progressDialog) {
        if (Daten.isGuiAppl()) {
            if (Dialog.yesNoDialog(International.onlyFor("Mit Kanu-eFB synchronisieren", "de"),
                    International.onlyFor("Es werden alle Fahrten aus dem aktuellen Fahrtenbuch mit dem Kanu-eFB synchronisiert.", "de") + "\n" +
                    International.getString("Möchtest Du fortfahren?")) != Dialog.YES) {
                return;
            }
        }
        while (loginurl == null || loginurl.length() == 0 ||
            cmdurl == null || cmdurl.length() == 0) {
            String msg = International.getString("Fehlende Konfigurationseinstellungen");
            if (!Daten.isGuiAppl()) {
                Logger.log(Logger.ERROR, Logger.MSG_SYNC_ERRORABORTSYNC, msg);
                Daten.haltProgram(Daten.HALT_MISCONFIG);
            }
            Dialog.infoDialog(msg, International.getString("Bitte vervollständige die Konfigurationseinstellungen!"));
            EfaConfigDialog dlg = new EfaConfigDialog((JDialog)null, Daten.efaConfig, BaseTabbedDialog.makeCategory(Daten.efaConfig.CATEGORY_SYNC, Daten.efaConfig.CATEGORY_KANUEFB));
            dlg.showDialog();
            if (!dlg.getDialogResult()) {
                return;
            }
            getConfigValues();
        }
        while (username == null || username.length() == 0 ||
            password == null || password.length() == 0) {
            String msg = International.getString("Fehlende Konfigurationseinstellungen");
            if (!Daten.isGuiAppl()) {
                Logger.log(Logger.ERROR, Logger.MSG_SYNC_ERRORABORTSYNC, msg);
                Daten.haltProgram(Daten.HALT_MISCONFIG);
            }
            ProjectEditDialog dlg = new ProjectEditDialog((JDialog)null, Daten.project, null, ProjectRecord.GUIITEMS_SUBTYPE_KANUEFB, admin);
            dlg.showDialog();
            if (!dlg.getDialogResult()) {
                return;
            }
            getConfigValues();
        }
        this.start();
        if (progressDialog != null) {
            progressDialog.showDialog();
        }
    }

}
