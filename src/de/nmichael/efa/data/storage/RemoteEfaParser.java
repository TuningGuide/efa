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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// @i18n complete

public class RemoteEfaParser extends DefaultHandler {

    public static String XML_EFA = "efa";

    private IDataAccess dataAccess;
    private DataRecord dummyRecord;
    private String[] keyFields;

    private Vector<RemoteEfaMessage> messages = new Vector<RemoteEfaMessage>();
    RemoteEfaMessage message;
    private DataRecord record;
    private DataKey key;

    private Locator locator;
    private boolean inXml = false;
    private boolean inRequestResponse = false;
    private boolean inRecord = false;
    private boolean inKey = false;
    private boolean documentComplete = false;

    private String fieldName;
    private String fieldValue;

    public RemoteEfaParser(IDataAccess dataAccess) {
        super();
        this.dataAccess = dataAccess;
        iniDataAccess();
    }

    private void iniDataAccess() {
        if (dataAccess == null && message != null) {
            String storageObjectName = message.getStorageObjectName();
            String storageObjectType = message.getStorageObjectType();
            if (storageObjectName != null && storageObjectName.length() > 0 &&
                storageObjectType != null && storageObjectType.length() > 0) {
                StorageObject p = null;
                if (Daten.project != null) {
                    // most often, we will be asked for a project storage object, so we check this first
                    p = Daten.project.getStorageObject(storageObjectName, storageObjectType, false);
                }
                if (p == null) {
                    if (Daten.efaConfig.data().getStorageObjectType().equals(storageObjectType)
                            && Daten.efaConfig.data().getStorageObjectName().equals(storageObjectName)) {
                        p = Daten.efaConfig;
                    } else if (Daten.efaTypes.data().getStorageObjectType().equals(storageObjectType)
                            && Daten.efaTypes.data().getStorageObjectName().equals(storageObjectName)) {
                        p = Daten.efaTypes;
                    } else if (Daten.admins.data().getStorageObjectType().equals(storageObjectType)
                            && Daten.admins.data().getStorageObjectName().equals(storageObjectName)) {
                        p = Daten.admins;
                    }
                }
                if (p != null) {
                    dataAccess = p.data();
                }
            }
        }
        if (dataAccess != null && dataAccess.getPersistence() != null && dummyRecord == null) {
            dummyRecord = dataAccess.getPersistence().createNewRecord();
            keyFields =  dummyRecord.getKeyFields();
        }
        return;
    }

    public boolean isDocumentComplete() {
        return documentComplete;
    }

    public Vector<RemoteEfaMessage> getMessages() {
        return messages;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    String getLocation() {
        return locator.getSystemId() + ":" + EfaUtil.int2String(locator.getLineNumber(),4) + ":" + EfaUtil.int2String(locator.getColumnNumber(),4) + ":\t";
    }

    public void startDocument() {
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,"Positions: <SystemID>:<LineNumber>:<ColumnNumber>");
        }
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 5)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "startDocument()");
        }
        documentComplete = false;
    }

    public void endDocument() {
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 5)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "endDocument()");
        }
        if (!documentComplete) {
            Logger.log(Logger.ERROR,Logger.MSG_FILE_XMLFILEINCOMPLETE, International.getMessage("Unvollständige oder korrupte Daten gelesen: {data}", messages.toString()));
        }
    }

    public void startElement(String uri, String localName, String qname, Attributes atts) {
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "startElement(uri=" + uri + ", localName=" + localName + ", qname=" + qname + ")");
        }
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
            for (int i = 0; i < atts.getLength(); i++) {
                Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,"\tattribute: uri=" + atts.getURI(i) + ", localName=" + atts.getLocalName(i) + ", qname=" + atts.getQName(i) + ", value=" + atts.getValue(i) + ", type=" + atts.getType(i));
            }
        }

        if (inRequestResponse && inRecord) {
            // begin of field (inside record)
            fieldName = localName;
            fieldValue = "";
            return;
        }

        if (inRequestResponse && inKey) {
            // begin of field (inside key)
            fieldName = localName;
            fieldValue = "";
            return;
        }

        if (inRequestResponse && !inRecord && !inKey) {
            if (localName.equals(DataRecord.ENCODING_RECORD)) {
                // begin of record
                iniDataAccess();
                inRecord = true;
                record = dataAccess.getPersistence().createNewRecord();
                fieldName = null;
                return;
            }
            if (localName.equals(DataKey.ENCODING_KEY)) {
                // begin of key
                iniDataAccess();
                inKey = true;
                try {
                    key = dataAccess.constructKey(null);
                } catch(Exception e) {
                    Logger.log(e);
                }
                fieldName = null;
                return;
            }
            // begin of field (outside record and key)
            inRecord = false;
            inKey = false;
            fieldName = localName;
            fieldValue = "";
            return;
        }

        if (inXml && !inRequestResponse && localName.equals(RemoteEfaMessage.TYPE_REQUEST)) {
            // begin of request
            inRequestResponse = true;
            message = new RemoteEfaMessage(
                    EfaUtil.string2int(atts.getValue(RemoteEfaMessage.TYPE_OPERATION_ID), 0),
                    RemoteEfaMessage.Type.request,
                    atts.getValue(RemoteEfaMessage.TYPE_OPERATION_NAME));
            return;
        }

        if (inXml && !inRequestResponse && localName.equals(RemoteEfaMessage.TYPE_RESPONSE)) {
            // begin of response
            inRequestResponse = true;
            message = new RemoteEfaMessage(
                    EfaUtil.string2int(atts.getValue(RemoteEfaMessage.TYPE_OPERATION_ID), 0),
                    RemoteEfaMessage.Type.response,
                    atts.getValue(RemoteEfaMessage.TYPE_OPERATION_NAME));
            return;
        }

        if (!inXml && localName.equals(XML_EFA)) {
            // begin of document
            inXml = true;
        }
    }

    public void characters(char[] ch, int start, int length) {
        String s = new String(ch, start, length); // .trim(); // trimming here would wrongly trim away spaces within strings which are composed out of several characters() invocations!
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "characters("+s+")");
        }

        if (fieldName != null) {
            fieldValue += s;
        }
    }

    public void endElement(String uri, String localName, String qname) {
        if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
            Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,getLocation() + "endElement(" + uri + "," + localName + "," + qname + ")");
        }

        if (inRequestResponse && fieldName != null && localName.equals(fieldName)) {
            // end of field
            if (inRecord) {
                record.set(fieldName, (fieldValue != null ? fieldValue.trim() : fieldValue));
            } else if (inKey) {
                int keyFieldIdx = -1;
                if (fieldName.equals(DataKey.ENCODING_KEY_PART1)) {
                    keyFieldIdx = 0;
                }
                if (fieldName.equals(DataKey.ENCODING_KEY_PART2)) {
                    keyFieldIdx = 1;
                }
                if (fieldName.equals(DataKey.ENCODING_KEY_PART3)) {
                    keyFieldIdx = 2;
                }
                if (keyFieldIdx >= 0 && keyFieldIdx <= 2) {
                    dummyRecord.set(keyFields[keyFieldIdx], fieldValue);
                    key.set(keyFieldIdx, dummyRecord.get(keyFields[keyFieldIdx]));
                }
            } else{
                message.addField(fieldName, fieldValue);
            }
            fieldName = null;
        }

        if (inRequestResponse && inRecord && fieldName == null && localName.equals(DataRecord.ENCODING_RECORD)) {
            // end of record
            message.addRecord(record);
            record = null;
            inRecord = false;
        }

        if (inRequestResponse && inKey && fieldName == null && localName.equals(DataKey.ENCODING_KEY)) {
            // end of key
            message.addKey(key);
            key = null;
            inKey = false;
        }

        if (inRequestResponse && !inRecord && !inKey && fieldName == null && localName.equals(RemoteEfaMessage.TYPE_REQUEST)) {
            // end of request
            inRequestResponse = false;
            if (dataAccess == null) {
                iniDataAccess();
            }
            message.setDataAccess(dataAccess);
            messages.add(message);
            message = null;
        }

        if (inRequestResponse && !inRecord && !inKey && fieldName == null && localName.equals(RemoteEfaMessage.TYPE_RESPONSE)) {
            // end of response
            inRequestResponse = false;
            if (dataAccess == null) {
                iniDataAccess();
            }
            message.setDataAccess(dataAccess);
            messages.add(message);
            message = null;
        }

        if (!inRequestResponse && localName.equals(XML_EFA)) {
            // end of document
            inXml = false;
            documentComplete = true;
        }
    }

}
