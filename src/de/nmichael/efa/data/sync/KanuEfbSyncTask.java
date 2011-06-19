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
import org.xml.sax.helpers.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.*;
import de.nmichael.efa.*;


public class KanuEfbSyncTask extends ProgressTask {

    private Logbook logbook;
    private String loginurl;
    private String cmdurl;
    private String username;
    private String password;
    private HttpCookie sessionCookie;
    private long lastSync;
    private long thisSync;

    public KanuEfbSyncTask(Logbook logbook) {
        super();
        this.logbook = logbook;
        this.loginurl = Daten.efaConfig.kanuEfb_urlLogin.getValue();
        this.cmdurl = Daten.efaConfig.kanuEfb_urlRequest.getValue();
        this.username = Daten.efaConfig.kanuEfb_username.getValue();
        this.password = Daten.efaConfig.kanuEfb_password.getValue();
        lastSync = Daten.efaConfig.kanuEfb_lastSync.getValue();
        thisSync = System.currentTimeMillis();
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
            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Sending Sync Request to "+cmdurl+":\n"+request);
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

        
        if (request.contains("SyncTrips")) {
            InputStream in = connection.getInputStream();
            System.out.println("-- HEADER START --");
            Map<String, List<String>> m = connection.getHeaderFields();
            for (String header : m.keySet()) {
                System.out.println(header + "=" + connection.getHeaderField(header));
            }
            System.out.println("-- HEADER END --");
            BufferedReader buf = new BufferedReader(new InputStreamReader(in));
            String s;
            System.out.println("-- RESPONSE START --");
            while ((s = buf.readLine()) != null) {
                System.out.println(s);
            }
            System.out.println("-- RESPONSE END --");
            in.close();
        }
        
        
        return getResponse(connection.getInputStream());
    }

    private KanuEfbXmlResponse getResponse(InputStream in) {
        try {
            XMLReader parser = EfaUtil.getXMLReader();
            KanuEfbXmlResponse response = new KanuEfbXmlResponse(this);
            parser.setContentHandler(response);
            parser.parse(new InputSource(in));
            return response;
        } catch(Exception e) {
            Logger.log(e);
            return null;
        }
    }

    private boolean login() {
        try {
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

            KanuEfbXmlResponse response = getResponse(connection.getInputStream());
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                response.printAll();
            }
            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();
            for (HttpCookie cookie : cookies) {
                if (Logger.isTraceOn(Logger.TT_SYNC)) {
                    logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCDEBUG, "Got Session Cookie: " + cookie);
                }
                sessionCookie = cookie;
            }

            int retCode = (response == null ? -1 : EfaUtil.stringFindInt(response.getValue(0, "code"), -1));
            if (retCode != 1) {
                String msg = (response == null ? "unknown" : response.getValue(0, "message"));
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORLOGIN, "Login failed: Code "+retCode+" ("+msg+")");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORLOGIN, "Login failed: "+e.toString());
            return false;
        }
        return true;
    }

    private boolean syncUsers() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronizing Users ...");
            Persons persons = Daten.project.getPersons(false);

            StringBuilder request = new StringBuilder();
            buildRequestHeader(request, "SyncUsers");
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sending Synchronization Request for all Records ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncUsers")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Got Synchronization Response for " + response.getNumberOfRecords() + " Records ...");
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
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  got Synchronization Response for User: " + personName + " (EfbId=" + efbId + ")");
                        } else {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "  got Synchronization Response for unknown User: " + personName);
                        }
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Got invalid Synchronization Response.");
                return false;
            }
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                response.printAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncBoats() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronizing Boats ...");
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
                        logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  adding Synchronization Request for Boat: " + r.getQualifiedName());
                    }
                    request.append("<boat><name>"+r.getQualifiedName()+"</name></boat>\n");
                    efaIds.put(r.getQualifiedName(), r.getId());
                    reqCnt++;
                }
                k = it.getNext();
            }
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sending Synchronization Request for " + reqCnt + " Records ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncBoats")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Got Synchronization Response for " + response.getNumberOfRecords() + " Records ...");
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
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  got Synchronization Response for Boat: "+boatName+" (EfbId="+efbId+")");
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Invalid Synchronization Response for Boat: "+boatName);
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Got invalid Synchronization Response.");
                return false;
            }
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                response.printAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncWaters() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronizing Waters ...");
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
                        logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  adding Synchronization Request for Water: " + r.getQualifiedName());
                    }
                    request.append("<water><name>"+r.getQualifiedName()+"</name></water>\n");
                    efaIds.put(r.getQualifiedName(), r.getId());
                    reqCnt++;
                }
                k = it.getNext();
            }
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sending Synchronization Request for " + reqCnt + " Records ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncWaters")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Got Synchronization Response for " + response.getNumberOfRecords() + " Records ...");
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
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  got Synchronization Response for Waters: "+watersName+" (EfbId="+efbId+")");
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Invalid Synchronization Response for Waters: "+watersName);
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Got invalid Synchronization Response.");
                return false;
            }
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                response.printAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncTrips() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronizing Trips ...");
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
                                    logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  adding Synchronization Request for Trip: " + r.getQualifiedName()+
                                            "; Person: "+p.getQualifiedName());
                                }
                                BoatRecord b = (r.getBoatId() != null ? boats.getBoat(r.getBoatId(), thisSync) : null);
                                DestinationRecord d = (r.getDestinationId() != null ? destinations.getDestination(r.getDestinationId(), thisSync): null);
                                WatersRecord w = (d != null && d.getWatersIdList() != null && d.getWatersIdList().length() > 0 ?
                                    waters.getWaters(d.getWatersIdList().get(0)) : null); // @todo (P4) get all other waters as well, currently for test purpose only first one!
                                String startDate = r.getDate().getDateString("YYYY-MM-DD");
                                String endDate = (r.getEndDate() != null ? r.getEndDate().getDateString("YYYY-MM-DD") : startDate);
                                request.append("<trip>");
                                request.append("<tripID>" + logbook.getName()+"_"+r.getEntryId().toString() + "</tripID>");
                                request.append("<userID>" + p.getEfbId() + "</userID>");
                                if (b != null && b.getEfbId() != null && b.getEfbId().length() > 0) {
                                    request.append("<boatID>" + b.getEfbId() + "</boatID>");
                                } else {
                                    request.append("<boatText>" + (b != null ? b.getQualifiedName() : r.getBoatName()) + "</boatText>");
                                }
                                request.append("<begdate>" + startDate + "</begdate>");
                                request.append("<enddate>" + endDate + "</enddate>");
                                if (r.getStartTime() != null) {
                                    //request.append("<begtime>" + r.getStartTime().toString() + "</begtime>");
                                }
                                if (r.getEndTime() != null) {
                                    //request.append("<endtime>" + r.getEndTime().toString() + "</endtime>");
                                }

                                request.append("<lines>");
                                request.append("<line>");
                                if (w != null && w.getEfbId() != null && w.getEfbId().length() > 0) {
                                    request.append("<waterID>"+w.getEfbId()+"</waterID>");
                                } else {
                                    request.append("<waterText>"+ (r.getDestinationId() != null ? r.getDestinationAndVariantName() : r.getDestinationName()) + "</waterText>");
                                }
                                if (d != null && d.getStart() != null && d.getStart().length() > 0) {
                                    request.append("<fromText>" + d.getStart() + "</fromText>");
                                }
                                if (d != null && d.getEnd() != null && d.getEnd().length() > 0) {
                                    request.append("<toText>" + d.getEnd() + "</toText>");
                                }
                                request.append("<kilometers>" + r.getDistance().getValueInKilometers() + "</kilometers>");
                                request.append("</line>");
                                request.append("</lines>");

                                if (r.getComments() != null && r.getComments().length() > 0) {
                                    request.append("<comment><![CDATA[" + d.getEnd() + "]]></comment>");
                                }

                                request.append("<changeDate>" + r.getLastModified() + "</changeDate>");
                                request.append("<status>" + "1" + "</status>");
                                request.append("<deleted>" + "0" + "</deleted>");
                                request.append("</trip>\n");
                                efaEntryIds.put(r.getEntryId().toString(), r);
                                reqCnt++;
                                
                            }
                        }
                    }
                }
                k = it.getNext();
                if (reqCnt >= 1) {
                    break; // @todo (PT) FOR TEST PURPOSES ABORT AFTER 1 TRIP RECORD
                }
            }
            buildRequestFooter(request);

            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Sending Synchronization Request for " + reqCnt + " Trips ...");
            KanuEfbXmlResponse response = sendRequest(request.toString());
            if (response != null && response.isResponseOk("SyncBoats")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Got Synchronization Response for " + response.getNumberOfRecords() + " Trips ...");
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
                    }
                    if (ok) {
                        if (Logger.isTraceOn(Logger.TT_SYNC)) {
                            logInfo(Logger.DEBUG, Logger.MSG_SYNC_SYNCINFO, "  successfully synchronized Trip: "+r.toString());
                        }
                    } else {
                        logInfo(Logger.WARNING, Logger.MSG_SYNC_WARNINCORRECTRESPONSE, "Failed to synchronize Trip: "+tripId+" ("+result+" - "+resultText+")");
                    }
                }

            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Got invalid Synchronization Response.");
                return false;
            }
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                response.printAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean syncDone() {
        try {
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Logging out ...");

            StringBuilder request = new StringBuilder();
            buildRequestHeader(request, "SyncDone");
            buildRequestFooter(request);

            KanuEfbXmlResponse response = sendRequest(request.toString());
            /*
            if (response != null && response.isResponseOk("SyncDone")) {
                logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Logged out.");
            } else {
                logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORINVALIDRESPONSE, "Got invalid Logout Response.");
                return false;
            }
            if (Logger.isTraceOn(Logger.TT_SYNC)) {
                response.printAll();
            }
            */

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void run() {
        setRunning(true);
        int i = 0;
        logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Starting Synchronization with Kanu-Efb at "+thisSync+" (lastSync was "+lastSync+") ...");
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
            Daten.efaConfig.kanuEfb_lastSync.setValue(thisSync);
            logInfo(Logger.INFO, Logger.MSG_SYNC_SYNCINFO, "Synchronization with Kanu-Efb successully finished.");
        } else {
            logInfo(Logger.ERROR, Logger.MSG_SYNC_ERRORABORTSYNC, "Aborting Synchronization due to Errors.");
        }
        setDone();
    }

    public int getAbsoluteWork() {
        return 6;
    }

    public String getSuccessfullyDoneMessage() {
        return International.getString("Synchronisation abgeschlossen.");
    }

    private void logInfo(String type, String key, String msg) {
        Logger.log(type, key, msg);
        if (!type.equals(Logger.DEBUG)) {
            logInfo(msg+"\n");
        }
    }

}
