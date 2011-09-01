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

import de.nmichael.efa.*;
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.EfaException;
import java.util.*;
import java.io.*;
import java.net.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class RemoteEfaClient extends DataAccess {

    private String sessionId;
    private XMLReader parser;

    private DataCache cache;
    private long lastIsOpenTs = -1;
    private boolean lastIsOpen = false;
    private long lastLoginFailed = -1;

    private Hashtable<String,Long> statistics = new Hashtable<String,Long>();
    private long lastPrintStatistics = 0;

    public RemoteEfaClient(String location, String username, String password, String name, String extension, String description) {
        setStorageLocation(location);
        setStorageObjectName(name);
        setStorageObjectType(extension);
        setStorageObjectDescription(description);
        setStorageUsername(username);
        setStoragePassword(password);
        cache = new DataCache(this, Daten.efaConfig.getValueDataRemoteCacheExpiryTime() * 1000);
    }

    public int getStorageType() {
        return IDataAccess.TYPE_FILE_XML;
    }

    public String getUID() {
        return "remote:" + getStorageUsername() + "@" + getStorageLocation() + "/" + getStorageObjectName() + "." + getStorageObjectType();
    }


    // =========================== Communication Methods ===========================

    public URL getURL() {
        String url = getStorageLocation();
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        if (url.lastIndexOf(":") < 7) {
            url = url + ":" + Daten.efaConfig.getValueDataataRemoteEfaServerPort();
        }
        try {
            return new URL(url);
        } catch(Exception e) {
            Logger.log(e);
            return null;
        }
    }

    private Vector<RemoteEfaMessage> sendRequest(RemoteEfaMessage request) throws Exception {
        Vector<RemoteEfaMessage> requests = new Vector<RemoteEfaMessage>();
        requests.add(request);
        return sendRequest(requests);
    }

    private Vector<RemoteEfaMessage> sendRequest(Vector<RemoteEfaMessage> requests) throws Exception {
        URL url = getURL();
        if (requests.size() == 0) {
            return null;
        }

        // login if we don't yet have a session id
        int loginAttempts = 0;
        if (this.sessionId == null && lastLoginFailed > 0 &&
            System.currentTimeMillis() - lastLoginFailed < Daten.efaConfig.getValueDataRemoteLoginFailureRetryTime() * 1000) {
            return null;
        }
        while(this.sessionId == null && !requests.get(0).getOperationName().equals(RemoteEfaMessage.OPERATION_LOGIN)) {
            int ret = runSimpleRequest(RemoteEfaMessage.createRequestLogin(1, getStorageUsername(), getStoragePassword()));
            if (ret == RemoteEfaMessage.ERROR_INVALIDLOGIN ||
                ret == RemoteEfaMessage.ERROR_NOPERMISSION ||
                ret == RemoteEfaMessage.ERROR_SELFLOGIN) {
                loginAttempts = 99;
            }
            if (this.sessionId == null) {
                if (++loginAttempts >= 3) {
                    Logger.log(Logger.ERROR, Logger.MSG_REFA_LOGINFAILURE,
                            International.getString("Login fehlgeschlagen."));
                    lastLoginFailed = System.currentTimeMillis();
                    return null;
                }
                try {
                    Thread.sleep(1000 * loginAttempts);
                } catch(Exception eignore) {
                }
            }
        }
        lastLoginFailed = 0;

        // provide session id for all requests
        if (this.sessionId != null) {
            for (int i = 0; i < requests.size(); i++) {
                updateStatistics("Sent:" + requests.get(i).getOperationName());
                requests.get(i).addField(RemoteEfaMessage.FIELD_SESSIONID, sessionId);
            }
        }

        StringBuffer request = new StringBuffer();
        request.append("<?xml version='1.0' encoding='UTF-8' ?>");
        request.append("<" + RemoteEfaParser.XML_EFA + ">");
        for (int i=0; i<requests.size(); i++) {
            request.append(requests.get(i).toString());
        }
        request.append("</" + RemoteEfaParser.XML_EFA + ">");

        if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 5)) {
            Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "Sending Request [" + url.toString() + "]: " + request.toString());
        }

        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(true);
        connection.setRequestProperty("Content-Type", "application/xml"); //"application/x-www-form-urlencoded");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(request.toString());
        out.flush();
        out.close();

        updateStatistics("Sent:Requests");

        Vector<RemoteEfaMessage> responses = getResponse(connection, new BufferedInputStream(connection.getInputStream()));
        getGeneralDataFromResponses(responses);
        return responses;
    }

    private Vector<RemoteEfaMessage> getResponse(URLConnection connection, BufferedInputStream in) {
        if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 5)) {
            try {
                in.mark(1024 * 1024);
                if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 9)) {
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "Got RemoteEfaResponse:");
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "    -- HEADER START --");
                    Map<String, List<String>> m = connection.getHeaderFields();
                    for (String header : m.keySet()) {
                        Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "    " + header + "=" + connection.getHeaderField(header));
                    }
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "    -- HEADER END --");
                    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                    String s;
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "    -- RESPONSE START --");
                    while ((s = buf.readLine()) != null) {
                        Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "   " + s);
                    }
                    Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "    -- RESPONSE END --");
                } else {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                    String s;
                    while ((s = buf.readLine()) != null) {
                        Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION, "Got response: " + s);
                    }
                }
                in.reset();
            } catch (Exception e) {
            }
        }

        updateStatistics("Rcvd:Responses");
        try {
            if (parser == null) {
                parser = EfaUtil.getXMLReader();
            }
            RemoteEfaParser responseHandler = new RemoteEfaParser(this);
            parser.setContentHandler(responseHandler);
            parser.parse(new InputSource(in));
            if (responseHandler.isDocumentComplete()) {
                Vector<RemoteEfaMessage> responses = responseHandler.getMessages();
                if (responses != null && responses.size() > 0 && responses.get(0) != null) {
                    if (this.sessionId != null) {
                        // we have a session id, but we need to check whether the server still accepts it
                        if (responses.get(0).getResultCode() == RemoteEfaMessage.ERROR_INVALIDSESSIONID) {
                            // it seems our session id became invalid
                            // @todo (P3) if session id becomes invalid, login again and re-try
                            this.sessionId = null;
                        }
                    } else {
                        // we don't have a session id, so this might be the response for a login: get the session id
                        this.sessionId = responses.get(0).getSessionId();
                    }
                }
                updateStatistics("Rcvd:Responses:Ok");
                return responses;
            } else {
                updateStatistics("Rcvd:Responses:Err");
                return null;
            }
        } catch(Exception e) {
            Logger.log(e);
            updateStatistics("Rcvd:Responses:Err");
            return null;
        }
    }

    private void getGeneralDataFromResponses(Vector<RemoteEfaMessage> responses) {
        if (responses == null) {
            return;
        }
        for (int i=0; i<responses.size(); i++) {
            RemoteEfaMessage response = responses.get(i);
            if (response != null && response.getResultCode() == RemoteEfaMessage.RESULT_OK) {
                long scn = response.getScn();
                DataRecord[] records = response.getRecords();
                if (records != null) {
                    for (int j=0; j<records.length; j++) {
                        if (records[j] != null) {
                            cache.updateCache(records[j], scn);
                        }
                    }
                    updateStatistics("Rcvd:Records", records.length);
                } else {
                    cache.updateScn(scn);
                }
                DataKey[] keys = response.getKeys();
                if (keys != null) {
                    updateStatistics("Rcvd:Keys", keys.length);
                }
            }
        }
    }

    private String getErrorLogstring(RemoteEfaMessage request, String msg, int code) {
        String requestName = (request != null ? request.getOperationName() : "Unknown");
        return getErrorLogstring(requestName, msg, code);
    }

    private String getErrorLogstring(String requestName, String msg, int code) {
        return getStorageObjectName() + "." + getStorageObjectType() + ": " +
               International.getMessage("Remote efa Anfrage {request} fehlgeschlagen: {reason}", requestName, msg + " (Code " + code + ")");
    }

    private int runSimpleRequest(RemoteEfaMessage request) {
        try {
            int myRequestId = request.getMsgId();
            Vector<RemoteEfaMessage> responses = sendRequest(request);
            if (responses == null || responses.size() == 0 ||
                responses.get(0) == null) {
                Logger.log(Logger.ERROR, Logger.MSG_REFA_UNEXPECTEDRESPONSE, getErrorLogstring(request, "empty response", -1));
                return -1;
            }
            if (responses.size() > 1) {
                Logger.log(Logger.ERROR, Logger.MSG_REFA_UNEXPECTEDRESPONSE, getErrorLogstring(request,
                           "unexpected number of responses for simple request: " + responses.size(), -1));
                return -1;
            }
            RemoteEfaMessage response = responses.get(0);
            if (response.getResultCode() != 0) {
                Logger.log(Logger.ERROR, Logger.MSG_REFA_REQUESTFAILED, getErrorLogstring(request,
                           response.getResultText(), response.getResultCode()));
                return response.getResultCode();
            }
            return RemoteEfaMessage.RESULT_OK;
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_REFA_REQUESTFAILED, getErrorLogstring(request,
                       e.getMessage(), -1));
            return -1;
        }
    }

    private RemoteEfaMessage runDataRequest(RemoteEfaMessage request) {
        try {
            int myRequestId = request.getMsgId();
            Vector<RemoteEfaMessage> responses = sendRequest(request);
            if (responses == null || responses.size() == 0 ||
                responses.get(0) == null) {
                Logger.log(Logger.ERROR, Logger.MSG_REFA_UNEXPECTEDRESPONSE, getErrorLogstring(request, "empty response", -1));
                return null;
            }
            if (responses.size() > 1) {
                Logger.log(Logger.ERROR, Logger.MSG_REFA_UNEXPECTEDRESPONSE, getErrorLogstring(request,
                           "unexpected number of responses for simple request: " + responses.size(), -1));
                return null;
            }
            return responses.get(0);
        } catch(Exception e) {
            Logger.log(Logger.ERROR, Logger.MSG_REFA_REQUESTFAILED, getErrorLogstring(request,
                       e.getMessage(), -1));
            return null;
        }
    }

    // =========================== Storage Object Methods ===========================

    public boolean existsStorageObject() throws EfaException {
        return runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_EXISTSSTORAGEOBJECT)) == RemoteEfaMessage.RESULT_OK;
    }

    public void openStorageObject() throws EfaException {
        /*
        if (!runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_OPENSTORAGEOBJECT))) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_OPENSTORAGEOBJECT, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
        */
    }

    public void createStorageObject() throws EfaException {
        /*
        if (!runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_CREATESTORAGEOBJECT))) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_CREATESTORAGEOBJECT, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
        */
    }

    public boolean isStorageObjectOpen() {
        if (lastIsOpen && System.currentTimeMillis() - lastIsOpenTs < Daten.efaConfig.getValueDataRemoteIsOpenExpiryTime() * 1000) {
            return true;
        }
        lastIsOpen = runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_ISSTORAGEOBJECTOPEN)) == RemoteEfaMessage.RESULT_OK;
        lastIsOpenTs = System.currentTimeMillis();
        return lastIsOpen;
    }

    public void closeStorageObject() throws EfaException {
        /*
        if (!runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_CLOSESTORAGEOBJECT))) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_CLOSESTORAGEOBJECT, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
        */
    }

    public void deleteStorageObject() throws EfaException {
        /*
        if (!runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_DELETESTORAGEOBJECT))) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_DELETESTORAGEOBJECT, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
        */
    }



    // =========================== Lock Methods ===========================

    public long acquireGlobalLock() throws EfaException {
        RemoteEfaMessage response = runDataRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_ACQUIREGLOBALLOCK));
        if (response != null) {
            return response.getLockId();
        } else {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_ACQUIREGLOBALLOCK, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
    }

    public long acquireLocalLock(DataKey key) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_ACQUIRELOCALLOCK);
        request.addKey(key);
        RemoteEfaMessage response = runDataRequest(request);
        if (response != null) {
            return response.getLockId();
        } else {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_ACQUIRELOCALLOCK, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
    }

    public boolean releaseGlobalLock(long lockID) {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_RELEASEGLOBALLOCK);
        request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        RemoteEfaMessage response = runDataRequest(request);
        return (response != null && response.getResultCode() == RemoteEfaMessage.RESULT_OK);
    }

    public boolean releaseLocalLock(long lockID) {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_RELEASELOCALLOCK);
        request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        RemoteEfaMessage response = runDataRequest(request);
        return (response != null && response.getResultCode() == RemoteEfaMessage.RESULT_OK);
    }



    // =========================== Global Data Methods ===========================

    public long getNumberOfRecords() throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETNUMBEROFRECORDS);
        RemoteEfaMessage response = runDataRequest(request);
        if (response != null) {
            return response.getLongValue();
        } else {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETNUMBEROFRECORDS, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
    }

    public long getSCN() throws EfaException {
        long scn = cache.getScnIfNotTooOld();
        if (scn >= 0) {
            return scn;
        }
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETSCN);
        RemoteEfaMessage response = runDataRequest(request);
        if (response != null) {
            return response.getScn();
        } else {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETSCN, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
    }

    public void createIndex(String[] fieldNames) throws EfaException {
        // nothing to be done
    }



    // =========================== Data Modification Methods ===========================

    public void add(DataRecord record) throws EfaException {
        add(record, -1);
    }

    public void add(DataRecord record, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_ADD);
        request.addRecord(record);
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_ADD,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
    }

    public DataKey addValidAt(DataRecord record, long t) throws EfaException {
        return addValidAt(record, t, -1);
    }

    public DataKey addValidAt(DataRecord record, long t, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_ADDVALIDAT);
        request.addRecord(record);
        request.addField(RemoteEfaMessage.FIELD_TIMESTAMP, Long.toString(t));
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_ADDVALIDAT,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getKey(0);
    }

    public DataRecord get(DataKey key) throws EfaException {
        DataRecord r = cache.get(key);
        if (r != null) {
            return r;
        }
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GET);
        request.addKey(key);
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GET,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getRecord(0);
    }

    public DataKey[] getAllKeys() throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETALLKEYS);
        request.addField(RemoteEfaMessage.FIELD_PREFETCH, Boolean.toString(true)); // prefetch all records as well
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETALLKEYS,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getKeys();
    }

    public DataKey[] getByFields(String[] fieldNames, Object[] values) throws EfaException {
        return getByFields(fieldNames, values, -1);
    }

    public DataKey[] getByFields(String[] fieldNames, Object[] values, long validAt) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETBYFIELDS);
        for (int i=0; i<fieldNames.length; i++) {
            request.addFieldArrayElement(i, fieldNames[i], (values[i] != null ? values[i].toString() : ""));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETBYFIELDS,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getKeys();
    }

    public DataRecord[] getValidAny(DataKey key) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETVALIDANY);
        request.addKey(key);
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETVALIDANY,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getRecords();
    }

    public DataRecord getValidAt(DataKey key, long t) throws EfaException {
        DataRecord r = cache.getValidAt(key, t);
        if (r != null) {
            return r;
        }
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETVALIDAT);
        request.addKey(key);
        request.addField(RemoteEfaMessage.FIELD_TIMESTAMP, Long.toString(t));
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETVALIDAT,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getRecord(0);
    }

    public DataRecord getValidLatest(DataKey key) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETVALIDLATEST);
        request.addKey(key);
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETVALIDLATEST,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getRecord(0);
    }

    public boolean isValidAny(DataKey key) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_ISVALIDANY);
        request.addKey(key);
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null ||
            (response.getResultCode() != RemoteEfaMessage.RESULT_OK && response.getResultCode() != RemoteEfaMessage.RESULT_FALSE)) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_ISVALIDANY,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getResultCode() == RemoteEfaMessage.RESULT_OK;
    }

    public void update(DataRecord record) throws EfaException {
        update(record, -1);
    }

    public void update(DataRecord record, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_UPDATE);
        request.addRecord(record);
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_UPDATE,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
    }

    public void changeValidity(DataRecord record, long validFrom, long invalidFrom) throws EfaException {
        changeValidity(record, validFrom, invalidFrom, -1);
    }

    public void changeValidity(DataRecord record, long validFrom, long invalidFrom, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_CHANGEVALIDITY);
        request.addRecord(record);
        request.addField(RemoteEfaMessage.FIELD_VALIDFROM, Long.toString(validFrom));
        request.addField(RemoteEfaMessage.FIELD_INVALIDFROM, Long.toString(invalidFrom));
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_CHANGEVALIDITY,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
    }

    public void delete(DataKey key) throws EfaException {
        delete(key, -1);
    }

    public void delete(DataKey key, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_DELETE);
        request.addKey(key);
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_DELETE,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
    }

    public void deleteVersionized(DataKey key, int merge) throws EfaException {
        deleteVersionized(key, merge, -1);
    }

    public void deleteVersionized(DataKey key, int merge, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_DELETEVERSIONIZED);
        request.addKey(key);
        request.addField(RemoteEfaMessage.FIELD_MERGE, Integer.toString(merge));
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_DELETEVERSIONIZED,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
    }

    public void deleteVersionizedAll(DataKey key, long deleteAt) throws EfaException {
        deleteVersionizedAll(key, deleteAt, -1);
    }

    public void deleteVersionizedAll(DataKey key, long deleteAt, long lockID) throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_DELETEVERSIONIZEDALL);
        request.addKey(key);
        request.addField(RemoteEfaMessage.FIELD_TIMESTAMP, Long.toString(deleteAt));
        if (lockID > 0) {
            request.addField(RemoteEfaMessage.FIELD_LOCKID, Long.toString(lockID));
        }
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_DELETEVERSIONIZEDALL,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
    }

    public void truncateAllData() throws EfaException {
        if (runSimpleRequest(RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_TRUNCATEALLDATA)) != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_OPENSTORAGEOBJECT, "unknown", -1),
                    Thread.currentThread().getStackTrace());
        }
    }


    // =========================== Data Iterator Methods ===========================

    public DataKeyIterator getStaticIterator() throws EfaException {
        return new DataKeyIterator(this, getAllKeys(), false);
    }

    public DataKeyIterator getDynamicIterator() throws EfaException {
        return new DataKeyIterator(this, getAllKeys(), true);
    }

    public DataRecord getFirst() throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETFIRST);
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETFIRST,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getRecord(0);
    }

    public DataRecord getLast() throws EfaException {
        RemoteEfaMessage request = RemoteEfaMessage.createRequestData(1, getStorageObjectType(), getStorageObjectName(),
                RemoteEfaMessage.OPERATION_GETLAST);
        RemoteEfaMessage response = runDataRequest(request);
        if (response == null || response.getResultCode() != RemoteEfaMessage.RESULT_OK) {
            throw new EfaException(Logger.MSG_REFA_REQUESTFAILED,
                    getErrorLogstring(RemoteEfaMessage.OPERATION_GETLAST,
                    (response != null ? response.getResultText() : "unknown"),
                    (response != null ? response.getResultCode() : -1)),
                    Thread.currentThread().getStackTrace());
        }
        return response.getRecord(0);
    }

    private void updateStatistics(String item) {
        updateStatistics(item, 1);
    }

    private void updateStatistics(String item, int count) {
        if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 1)) {
            synchronized (statistics) {
                Long l = statistics.get(item);
                if (l == null) {
                    l = new Long(0);
                }
                l += count;
                statistics.put(item, l);
                long now = System.currentTimeMillis();
                if (now - lastPrintStatistics > 60000) {
                    if (now > 0) {
                        lastPrintStatistics = now;
                        printStatistics();
                    } else {
                        lastPrintStatistics = now;
                    }
                }
            }
        }
    }

    private void printStatistics() {
        if (Logger.isTraceOn(Logger.TT_REMOTEEFA, 1)) {
            synchronized(statistics) {
                String[] keys = statistics.keySet().toArray(new String[0]);
                if (keys != null || keys.length > 0) {
                    Arrays.sort(keys);
                    for (int i=0; i<keys.length; i++) {
                        Logger.log(Logger.DEBUG, Logger.MSG_REFA_DEBUGCOMMUNICATION,
                                "RemoteEfaClient:Statistics:" + getStorageObjectName() + "." + getStorageObjectType() + ":" + keys[i] + ": " + statistics.get(keys[i]));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Logger.setDebugLogging(true, true);
        Logger.setTraceTopic("0x4000", true);
        RemoteEfaClient client = new RemoteEfaClient("127.0.0.1:3834", "admin", "nieaibrc", "2011", Logbook.DATATYPE, "Logbook");
        try {
            System.out.println(client.isStorageObjectOpen());
            System.out.println(client.get(LogbookRecord.getKey(DataTypeIntString.parseString("170"))));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
