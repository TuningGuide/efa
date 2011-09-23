/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import com.sun.net.httpserver.*;
import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.gui.EfaBoathouseFrame;
import de.nmichael.efa.util.Base64;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.security.*;
import java.util.concurrent.Executors;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class RemoteEfaServer {

    private static SecureRandom prng;
    private static MessageDigest sha;


    private int serverPort;
    private XMLReader parser;
    private Hashtable<String,AdminRecord> sessions = new Hashtable<String,AdminRecord>();

    public RemoteEfaServer(int port) {
        serverPort = port;
        try {
            InetSocketAddress addr = new InetSocketAddress(port);
            HttpServer server = HttpServer.create(addr, 0);

            server.createContext("/", new MyHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            Logger.log(Logger.INFO, Logger.MSG_REFA_SERVERSTATUS,
                    International.getMessage("efaRemote Server läuft auf Port {port}", serverPort));
            (new EfaOnlineThread()).start();
        } catch (Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_REFA_SERVERERROR,
                    International.getString("efaRemote Server konnte nicht gestartet werden.") + " " + e.getMessage());
            Logger.logdebug(e);
        }
    }

    class MyHandler implements HttpHandler {

        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                Headers responseHeaders = exchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, 0);

                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(new String("<html><body>").getBytes());
                responseBody.write(new String("<h1 align=\"center\">" + Daten.EFA_LONGNAME + "</h1>").getBytes());
                responseBody.write(new String("<h2 align=\"center\">efaRemote Server running on port " + serverPort).getBytes());
                responseBody.close();
            }
            if (requestMethod.equalsIgnoreCase("POST")) {
                Headers responseHeaders = exchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "application/xml");
                exchange.sendResponseHeaders(200, 0);

                Vector<RemoteEfaMessage> responses = new Vector<RemoteEfaMessage>();
                try {
                    Vector<RemoteEfaMessage> requests = getRequests(new BufferedInputStream(exchange.getRequestBody()), exchange.getRemoteAddress());
                    responses = handleRequests(requests, exchange.getRemoteAddress());
                } catch(Exception e) {
                    responses.add(RemoteEfaMessage.createResponseResult(0, RemoteEfaMessage.ERROR_UNKNOWN, e.getMessage()));
                    Logger.log(e);
                }
                StringBuilder response = new StringBuilder();
                response.append("<?xml version='1.0' encoding='UTF-8' ?><" + RemoteEfaParser.XML_EFA + ">");
                for (int i=0; i<responses.size(); i++) {
                    response.append(responses.get(i).toString());
                }
                response.append("</" + RemoteEfaParser.XML_EFA + ">");
                if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 5)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "Sending Response [" + exchange.getRemoteAddress().toString() + "]: " + response.toString());
                }
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(response.toString().getBytes());
                responseBody.close();
            }
        }
    }

    private Vector<RemoteEfaMessage> getRequests(BufferedInputStream in, InetSocketAddress peerAddress) {
        if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 5)) {
            try {
                in.mark(1024*1024); // tracing will break messages if they are larger than 1 MB
                if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 9)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "Got RemoteEfaRequest:");
                    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                    String s;
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "-- REQUEST START --");
                    while ((s = buf.readLine()) != null) {
                        Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "   " + s);
                    }
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "-- REQUEST END --");
                    in.reset();
                } else {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                    String s;
                    while ((s = buf.readLine()) != null) {
                        Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "Got Request [" + peerAddress.toString() + "]:" + s);
                    }
                }
            } catch (Exception e) {
            }
        }

        try {
            if (parser == null) {
                parser = EfaUtil.getXMLReader();
            }
            RemoteEfaParser responseHandler = new RemoteEfaParser(null);
            parser.setContentHandler(responseHandler);
            parser.parse(new InputSource(in));
            if (responseHandler.isDocumentComplete()) {
                return responseHandler.getMessages();
            } else {
                return null;
            }
        } catch(Exception e) {
            Logger.log(e);
            return null;
        }
    }

    private Vector<RemoteEfaMessage> handleRequests(Vector<RemoteEfaMessage> requests, InetSocketAddress peerAddress) {
        Vector<RemoteEfaMessage> responses = new Vector<RemoteEfaMessage>();
        try {
            for (int i=0; i<requests.size(); i++) {
                RemoteEfaMessage request = requests.get(i);
                if (request == null) {
                    responses.add(RemoteEfaMessage.createResponseResult(0, RemoteEfaMessage.ERROR_INVALIDREQUEST, "Invalid Request: <null>"));
                    break;
                }
                String operation = request.getOperationName();
                int msgId = request.getMsgId();
                if (msgId < 1) {
                    responses.add(RemoteEfaMessage.createResponseResult(0, RemoteEfaMessage.ERROR_INVALIDREQUEST, "Invalid Request ID: " + msgId));
                    break;
                }
                if (operation == null) {
                    responses.add(RemoteEfaMessage.createResponseResult(msgId, RemoteEfaMessage.ERROR_INVALIDREQUEST, "Invalid Operation: <null>"));
                    break;
                }

                // is this a login?
                if (operation.equals(RemoteEfaMessage.OPERATION_LOGIN)) {
                    responses.add(requestLogin(request, peerAddress));
                    break;
                }

                // if not a login, then this request must provide a valid session id
                AdminRecord admin = getSessionAdmin(request.getSessionId());
                if (admin == null) {
                    responses.add(RemoteEfaMessage.createResponseResult(msgId, RemoteEfaMessage.ERROR_INVALIDSESSIONID, "Invalid SessionID"));
                    break;
                }

                // find the storage object referenced in this request
                String storageObjectType = request.getStorageObjectType();
                String storageObjectName = request.getStorageObjectName();
                if (storageObjectType == null || storageObjectType.length() == 0 ||
                    storageObjectName == null || storageObjectName.length() == 0) {
                    responses.add(RemoteEfaMessage.createResponseResult(msgId, RemoteEfaMessage.ERROR_NOSTORAGEOBJECT, "No StorageObject specified"));
                    break;
                }
                IDataAccess dataAccess = request.getDataAccesss();
                StorageObject p = (dataAccess != null ? dataAccess.getPersistence() : null);
                if (p == null && !RemoteCommand.DATATYPE.equals(request.getStorageObjectType())) {
                    responses.add(RemoteEfaMessage.createResponseResult(msgId, RemoteEfaMessage.ERROR_UNKNOWNSTORAGEOBJECT,
                            "StorageObject not found: " + storageObjectName + "." + storageObjectType));
                    break;
                }

                while(true) { // not a loop

                    // now try to find the operation for this request
                    if (operation.equals(RemoteEfaMessage.OPERATION_EXISTSSTORAGEOBJECT)) {
                        responses.add(requestExistsStorageObject(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_OPENSTORAGEOBJECT)) {
                        responses.add(requestOpenStorageObject(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_CREATESTORAGEOBJECT)) {
                        responses.add(requestCreateStorageObject(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_ISSTORAGEOBJECTOPEN)) {
                        responses.add(requestIsStorageObjectOpen(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_CLOSESTORAGEOBJECT)) {
                        responses.add(requestCloseStorageObject(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_DELETESTORAGEOBJECT)) {
                        responses.add(requestDeleteStorageObject(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_ACQUIREGLOBALLOCK)) {
                        responses.add(requestAcquireGlobalLock(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_ACQUIRELOCALLOCK)) {
                        responses.add(requestAcquireLocalLock(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_RELEASEGLOBALLOCK)) {
                        responses.add(requestReleaseGlobalLock(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_RELEASELOCALLOCK)) {
                        responses.add(requestReleaseLocalLock(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETNUMBEROFRECORDS)) {
                        responses.add(requestGetNumberOfRecords(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETSCN)) {
                        responses.add(requestGetSCN(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_ADD)) {
                        responses.add(requestAdd(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_ADDVALIDAT)) {
                        responses.add(requestAddValidAt(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GET)) {
                        responses.add(requestGet(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETALLKEYS)) {
                        responses.add(requestGetAllKeys(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETBYFIELDS)) {
                        responses.add(requestGetByFields(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETVALIDANY)) {
                        responses.add(requestGetValidAny(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETVALIDAT)) {
                        responses.add(requestGetValidAt(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETVALIDLATEST)) {
                        responses.add(requestGetValidLatest(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETVALIDNEAREST)) {
                        responses.add(requestGetValidNearest(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_ISVALIDANY)) {
                        responses.add(requestIsValidAny(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_UPDATE)) {
                        responses.add(requestUpdate(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_CHANGEVALIDITY)) {
                        responses.add(requestChangeValidity(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_DELETE)) {
                        responses.add(requestDelete(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_DELETEVERSIONIZED)) {
                        responses.add(requestDeleteVersionized(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_DELETEVERSIONIZEDALL)) {
                        responses.add(requestDeleteVersionizedAll(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_TRUNCATEALLDATA)) {
                        responses.add(requestTruncateAllData(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETFIRST)) {
                        responses.add(requestGetFirst(request, admin, p));
                        break;
                    }
                    if (operation.equals(RemoteEfaMessage.OPERATION_GETLAST)) {
                        responses.add(requestGetLast(request, admin, p));
                        break;
                    }

                    if (operation.equals(RemoteEfaMessage.OPERATION_CMD_EXITEFA)) {
                        responses.add(requestCmdExitEfa(request, admin));
                        break;
                    }

                    // unknown request
                    responses.add(RemoteEfaMessage.createResponseResult(msgId, RemoteEfaMessage.ERROR_INVALIDREQUEST, "Unsupported Request: " + request.toString()));
                    break;
                }

                if (responses.size() > 0 && p != null) {
                    // add SCN to response
                    RemoteEfaMessage lastResponse = responses.get(responses.size() - 1);
                    if (lastResponse != null && lastResponse.getResultCode() == RemoteEfaMessage.RESULT_OK) {
                        lastResponse.addField(RemoteEfaMessage.FIELD_SCN, Long.toString(p.data().getSCN()));
                    }
                }

            }
        } catch (Exception e) {
            Logger.log(e);
            responses.add(RemoteEfaMessage.createResponseResult(0, RemoteEfaMessage.ERROR_UNKNOWN, e.getMessage()));
        }
        return responses;
    }

    private String createSessionId(AdminRecord admin) {

        String sid = null;
        try {
            synchronized (sessions) {
                if (prng == null) {
                    prng = SecureRandom.getInstance("SHA1PRNG");
                }
                if (sha == null) {
                    sha = MessageDigest.getInstance("SHA-1");
                }
                byte[] idBytes = new byte[8];
                prng.nextBytes(idBytes);
                byte[] result = sha.digest(idBytes);
                sid = Base64.encodeBytes(result);
                sessions.put(sid, admin);
            }
        } catch (Exception e) {
            Logger.log(e);
        }
        return sid;
    }

    private AdminRecord getSessionAdmin(String sessionId) {
        if (sessionId == null || sessionId.length() == 0) {
            return null;
        }
        synchronized(sessions) {
            // @todo (P3) check for session timeout and clean up dead sessions
            return sessions.get(sessionId);
        }
    }

    // ===================================== Login =====================================

    private RemoteEfaMessage requestLogin(RemoteEfaMessage request, InetSocketAddress peerAddress) {
        String username = request.getUsername();
        String password = request.getPassword();
        String pid = request.getPid();
        if (Daten.applPID.equals(pid)) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_SELFLOGIN, "Self Login Attempt");
        }
        if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 1)) {
            Logger.log(Logger.DEBUG, Logger.MSG_REFA_SERVERLOG,
                    "efaRemote Login Attempt from " + peerAddress + ": Username=" + username + " Password=" + password);
        }
        if (Daten.admins != null && Daten.admins.isOpen()) {
            AdminRecord admin = Daten.admins.login(username, password);
            if (admin == null) {
                Logger.log(Logger.WARNING, Logger.MSG_REFA_SERVERLOG,
                    International.getMessage("efaRemote Login von {ipaddress} fehlgeschlagen", peerAddress.toString()) +
                    ": " + International.getString("Admin") + "=" + username);
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_INVALIDLOGIN, "Invalid Login");
            } else {
                if (!admin.isAllowedRemoteAccess()) {
                    Logger.log(Logger.WARNING, Logger.MSG_REFA_SERVERLOG,
                            International.getMessage("efaRemote Login von {ipaddress} mit ungenügenden Rechten", peerAddress.toString())
                            + ": " + International.getString("Admin") + "=" + username);
                    return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_NOPERMISSION, "No Permission");
                }
                Logger.log(Logger.INFO, Logger.MSG_REFA_SERVERLOG,
                    International.getMessage("efaRemote Login von {ipaddress} erfolgreich", peerAddress.toString()) +
                    ": " + International.getString("Admin") + "=" + username);
                RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK,
                        Daten.VERSIONID);
                response.addField(RemoteEfaMessage.FIELD_SESSIONID, createSessionId(admin));
                return response;
            }
        } else {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNABLETOCOMPLY, "Unable to comply");
        }
    }


    // =========================== Storage Object Methods ===========================

    private RemoteEfaMessage requestExistsStorageObject(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            if (p.dataAccess.existsStorageObject()) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            } else {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, "StorageObject does not exists");
            }
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestOpenStorageObject(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_NOTYETSUPPORTED, "Operation not supported yet");
    }

    private RemoteEfaMessage requestCreateStorageObject(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_NOTYETSUPPORTED, "Operation not supported yet");
    }

    private RemoteEfaMessage requestIsStorageObjectOpen(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            if (p.dataAccess.isStorageObjectOpen()) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            } else {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, "StorageObject is not open");
            }
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestCloseStorageObject(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_NOTYETSUPPORTED, "Operation not supported yet");
    }

    private RemoteEfaMessage requestDeleteStorageObject(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_NOTYETSUPPORTED, "Operation not supported yet");
    }


    // =========================== Lock Methods ===========================

    private RemoteEfaMessage requestAcquireGlobalLock(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            long lockId = p.dataAccess.acquireGlobalLock();
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            response.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockId));
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, e.getMessage());
        }
    }

    private RemoteEfaMessage requestAcquireLocalLock(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataKey k = request.getKey(0);
            if (k == null) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_INVALIDREQUEST, "No DataKey given");
            }
            long lockId = p.dataAccess.acquireLocalLock(k);
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            response.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockId));
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, e.getMessage());
        }
    }

    private RemoteEfaMessage requestReleaseGlobalLock(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            long lockId = request.getLockId();
            if (lockId < 0) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_INVALIDREQUEST, "No Lock given");
            }
            if (p.dataAccess.releaseGlobalLock(lockId)) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            } else {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, "Global Lock not released");
            }
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.getMessage());
        }
    }

    private RemoteEfaMessage requestReleaseLocalLock(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            long lockId = request.getLockId();
            if (lockId < 0) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_INVALIDREQUEST, "No Lock given");
            }
            if (p.dataAccess.releaseLocalLock(lockId)) {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            } else {
                return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, "Local Lock not released");
            }
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.getMessage());
        }
    }


    // =========================== Global Data Methods ===========================

    private RemoteEfaMessage requestGetNumberOfRecords(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            response.addField(RemoteEfaMessage.FIELD_LONGVALUE, Long.toString(p.dataAccess.getNumberOfRecords()));
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetSCN(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            response.addField(RemoteEfaMessage.FIELD_SCN, Long.toString(p.dataAccess.getSCN()));
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    // =========================== Data Modification Methods ===========================

    private RemoteEfaMessage requestAdd(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().add(request.getRecord(0), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestAddValidAt(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().addValidAt(request.getRecord(0), request.getTimestamp(), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGet(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord r = p.data().get(request.getKey(0));
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            if (r != null) {
                response.addRecord(r);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetAllKeys(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            boolean prefetch = request.getPrefetch();
            DataKey[] keys = p.data().getAllKeys();
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            for (int i=0; keys != null && i<keys.length; i++) {
                response.addKey(keys[i]);

                if (prefetch) {
                    // also send record (prefetch for cache)
                    try {
                        DataRecord r = p.data().get(keys[i]);
                        if (r != null) {
                            response.addRecord(r);
                        }
                    } catch (Exception eignore) {
                    }
                }
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetByFields(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataKey[] keys = p.data().getByFields(request.getFieldArrayNames(), request.getFieldArrayValues(), request.getTimestamp());
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            for (int i=0; keys != null && i<keys.length; i++) {
                response.addKey(keys[i]);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetValidAny(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord[] records = p.data().getValidAny(request.getKey(0));
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            for (int i=0; records != null && i<records.length; i++) {
                response.addRecord(records[i]);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetValidAt(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord r = p.data().getValidAt(request.getKey(0), request.getTimestamp());
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            if (r != null) {
                response.addRecord(r);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetValidLatest(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord r = p.data().getValidLatest(request.getKey(0));
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            if (r != null) {
                response.addRecord(r);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetValidNearest(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord r = p.data().getValidNearest(request.getKey(0), request.getValidFrom(), request.getInvalidFrom(), request.getTimestamp());
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            if (r != null) {
                response.addRecord(r);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestIsValidAny(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            boolean validAny = p.data().isValidAny(request.getKey(0));
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), (validAny ? RemoteEfaMessage.RESULT_OK : RemoteEfaMessage.RESULT_FALSE), null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestUpdate(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().update(request.getRecord(0), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestChangeValidity(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().changeValidity(request.getRecord(0), request.getValidFrom(), request.getInvalidFrom(), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestDelete(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().delete(request.getKey(0), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestDeleteVersionized(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().deleteVersionized(request.getKey(0), request.getMerge(), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestDeleteVersionizedAll(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().deleteVersionizedAll(request.getKey(0), request.getTimestamp(), request.getLockId());
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestTruncateAllData(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            p.data().truncateAllData();
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetFirst(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord r = p.data().getFirst();
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            if (r != null) {
                response.addRecord(r);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestGetLast(RemoteEfaMessage request, AdminRecord admin, StorageObject p) {
        try {
            DataRecord r = p.data().getLast();
            RemoteEfaMessage response = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            if (r != null) {
                response.addRecord(r);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }

    private RemoteEfaMessage requestCmdExitEfa(RemoteEfaMessage request, AdminRecord admin) {
        try {
            final boolean restart = request.getBoolean();
            Logger.log(Logger.INFO, Logger.MSG_EVT_REMOTEEFAEXIT, International.getString("Beenden von efa durch Remote-Kommando"));
            final AdminRecord _admin = admin;
            RemoteEfaMessage response;
            if (EfaBoathouseFrame.efaBoathouseFrame != null) {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch(Exception e) {}
                        EfaBoathouseFrame.efaBoathouseFrame.cancel(null, EfaBoathouseFrame.EFA_EXIT_REASON_USER, _admin, restart);
                    }
                }.start();
                response  = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_OK, null);
            } else {
                response  = RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.RESULT_FALSE, null);
            }
            return response;
        } catch(Exception e) {
            return RemoteEfaMessage.createResponseResult(request.getMsgId(), RemoteEfaMessage.ERROR_UNKNOWN, e.toString());
        }
    }


    public static void main(String[] args) throws IOException {
        Logger.setDebugLogging(true, true);
        Logger.setTraceTopic("0x4000", true);
        new RemoteEfaServer(3834);
    }

}
