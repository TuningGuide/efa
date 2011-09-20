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
import de.nmichael.efa.gui.ProgressDialog;
import de.nmichael.efa.util.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class DataImport extends ProgressTask {

    public static final String IMPORTMODE_ADD    = "ADD";           // import as new record; fail for duplicates (also for duplicate versionized records with different validity)
    public static final String IMPORTMODE_UPD    = "UPDATE";        // update existing record; fail if record doesn't exist (for versionized: if no version exists)
    public static final String IMPORTMODE_ADDUPD = "ADD_OR_UPDATE"; // add, or if duplicate, update

    // only relevant for versionized storage objects
    public static final String UPDMODE_UPDATEVALIDVERSION = "UPDVERSION"; // update version which is valid at specified timestamp; fail if no version is valid
    public static final String UPPMODE_CREATENEWVERSION   = "NEWVERSION"; // always create a version at specified timestamp; fail if version for exact same timestamp exists

    private StorageObject storageObject;
    private IDataAccess dataAccess;
    private String[] fields;
    private String[] keyFields;
    private boolean versionized;
    private String filename;
    private String encoding;
    private char csvSeparator;
    private char csvQuotes;
    private String importMode;
    private long validAt;
    private String updMode;
    private int importCount = 0;
    private int errorCount = 0;


    public DataImport(StorageObject storageObject,
            String filename, String encoding, char csvSeparator, char csvQuotes,
            String importMode, long validAt, String updMode) {
        super();
        this.storageObject = storageObject;
        this.dataAccess = storageObject.data();
        this.versionized = storageObject.data().getMetaData().isVersionized();
        this.fields = dataAccess.getFieldNames();
        this.keyFields = dataAccess.getKeyFieldNames();
        this.filename = filename;
        this.encoding = encoding;
        this.csvSeparator = csvSeparator;
        this.csvQuotes = csvQuotes;
        this.importMode = importMode;
        this.validAt = validAt;
        this.updMode = updMode;
    }

    public static boolean isXmlFile(String filename) {
        try {
            BufferedReader f = new BufferedReader(new FileReader(filename));
            String s = f.readLine();
            boolean xml = (s != null && s.startsWith("<?xml"));
            f.close();
            return xml;
        } catch(Exception eignore) {
            return false;
        }
    }

    private Vector<String> splitFields(String s) {
        Vector<String> fields = new Vector<String>();
        boolean inQuote = false;
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            if (!inQuote && s.charAt(i) == csvQuotes) {
                inQuote = true;
                continue;
            }
            if (inQuote && s.charAt(i) == csvQuotes) {
                inQuote = false;
                continue;
            }
            if (!inQuote && s.charAt(i) == csvSeparator) {
                fields.add(buf.toString());
                buf = new StringBuffer();
                continue;
            }
            buf.append(s.charAt(i));
        }
        fields.add(buf.toString());
        return fields;
    }

    private void logImportFailed(DataRecord r, String msg) {
        logInfo("ERROR: " + International.getMessage("Import von Datensatz {record} fehlgeschlagen: {reason}", r.toString(), msg + "\n"));
        errorCount++;
    }

    private void addRecord(DataRecord r) {
        try {
            if (versionized) {
                dataAccess.addValidAt(r, validAt);
                setCurrentWorkDone(++importCount);
            } else {
                dataAccess.add(r);
                setCurrentWorkDone(++importCount);
            }
        } catch (Exception e) {
            logImportFailed(r, e.toString());
        }
    }

    private void updateRecord(DataRecord r) {
        try {
            DataRecord rorig = (versionized
                    ? dataAccess.getValidAt(r.getKey(), validAt)
                    : dataAccess.get(r.getKey()));
            if (rorig == null) {
                logImportFailed(r, International.getString("Keine gültige Version des Datensatzes gefunden"));
                return;
            }
            for (int i = 0; i < fields.length; i++) {
                Object o = r.get(fields[i]);
                if (o != null
                        && !r.isKeyField(fields[i])
                        && !fields[i].equals(DataRecord.LASTMODIFIED)
                        && !fields[i].equals(DataRecord.VALIDFROM)
                        && !fields[i].equals(DataRecord.INVALIDFROM)
                        && !fields[i].equals(DataRecord.INVISIBLE)
                        && !fields[i].equals(DataRecord.DELETED)) {
                    rorig.set(fields[i], o);
                }
            }

            if (!versionized || updMode.equals(UPDMODE_UPDATEVALIDVERSION)) {
                dataAccess.update(rorig);
                setCurrentWorkDone(++importCount);
            }
            if (versionized && updMode.equals(UPPMODE_CREATENEWVERSION)) {
                dataAccess.addValidAt(rorig, validAt);
                setCurrentWorkDone(++importCount);
            }
        } catch (Exception e) {
            logImportFailed(r, e.toString());
        }
    }

    private void importRecord(DataRecord r) {
        try {
            DataRecord[] otherVersions = null;

            DataKey key = r.getKey();
            if (key.getKeyPart1() == null) {
                // first key field is *not* set -> search for record by QualifiedName
                DataKey[] keys = dataAccess.getByFields(r.getQualifiedNameFields(), r.getQualifiedNameValues(r.getQualifiedName()), -1);
                if (keys != null && keys.length > 0) {
                    for (int i = 0; i < keyFields.length; i++) {
                        if (!keyFields[i].equals(DataRecord.VALIDFROM)) {
                            r.set(keyFields[i], keys[0].getKeyPart(i));
                        }
                    }
                } else {
                    for (int i = 0; i < keyFields.length; i++) {
                        if (!keyFields[i].equals(DataRecord.VALIDFROM)
                                && r.get(keyFields[i]) == null) {
                            if (dataAccess.getMetaData().getFieldType(keyFields[i]) == IDataAccess.DATA_UUID) {
                                r.set(keyFields[i], UUID.randomUUID());
                            } else {
                                logImportFailed(r, "KeyField(s) not set");
                                return;
                            }
                        }
                    }
                }
            }
            key = r.getKey();

            if (versionized) {
                otherVersions = dataAccess.getValidAny(key);
            } else {
                DataRecord r1 = dataAccess.get(key);
                otherVersions = (r1 != null ? new DataRecord[] { r1 } : null);
            }
            if (importMode.equals(IMPORTMODE_ADD)) {
                if (otherVersions != null && otherVersions.length > 0) {
                    logImportFailed(r, International.getString("Datensatz existiert bereits"));
                } else {
                    addRecord(r);
                }
            }
            if (importMode.equals(IMPORTMODE_UPD)) {
                if (otherVersions == null || otherVersions.length == 0) {
                    logImportFailed(r, International.getString("Datensatz nicht gefunden"));
                } else {
                    updateRecord(r);
                }
            }
            if (importMode.equals(IMPORTMODE_ADDUPD)) {
                if (otherVersions != null && otherVersions.length > 0) {
                    updateRecord(r);
                } else {
                    addRecord(r);
                }
            }
        } catch (Exception e) {
            logImportFailed(r, e.getMessage());
        }
    }

    private void runXmlImport() {
        try {
            XMLReader parser = EfaUtil.getXMLReader();
            DataImportXmlParser responseHandler = new DataImportXmlParser(this, dataAccess);
            parser.setContentHandler(responseHandler);
            parser.parse(new InputSource(new FileInputStream(filename)));
        } catch (Exception e) {
            logInfo(e.toString());
            errorCount++;
            Logger.log(e);
            if (Daten.isGuiAppl()) {
                Dialog.error(e.toString());
            }
        }
    }

    private void runCsvImport() {
        try {
            int linecnt = 0;
            String[] header = null;
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encoding));
            String s;
            while ( (s = f.readLine()) != null) {
                s = s.trim();
                if (s.length() == 0)  {
                    continue;
                }
                Vector<String> fields = splitFields(s);
                if (fields.size() > 0) {
                    if (linecnt == 0) {
                        // header
                        header = new String[fields.size()];
                        for (int i=0; i<header.length; i++) {
                            header[i] = fields.get(i);
                        }
                    } else {
                        // fields
                        DataRecord r = storageObject.createNewRecord();
                        for (int i=0; i<header.length; i++) {
                            String value = (fields.size() > i ? fields.get(i) : null);
                            if (value != null && value.length() > 0) {
                                r.setFromText(header[i], value);
                            }
                        }
                        importRecord(r);
                    }
                }
                linecnt++;
            }
            f.close();
        } catch(Exception e) {
            logInfo(e.toString());
            errorCount++;
            Logger.log(e);
            if (Daten.isGuiAppl()) {
                Dialog.error(e.toString());
            }
        }
    }

    public void run() {
        setRunning(true);
        this.logInfo(International.getString("Importiere Datensätze ..."));
        if (isXmlFile(filename)) {
            runXmlImport();
        } else {
            runCsvImport();
        }
        this.logInfo("\n\n" + International.getMessage("{count} Datensätze erfolgreich importiert.", importCount));
        this.logInfo("\n" + International.getMessage("{count} Fehler.", errorCount));
        setDone();
    }

    public int getAbsoluteWork() {
        return 100; // just a guess
    }

    public String getSuccessfullyDoneMessage() {
        return International.getMessage("{count} Datensätze erfolgreich importiert.", importCount);
    }

    public void runImport(ProgressDialog progressDialog) {
        this.start();
        if (progressDialog != null) {
            progressDialog.showDialog();
        }
    }

    public class DataImportXmlParser extends DefaultHandler {

        private Locator locator;
        private DataImport dataImport;
        private IDataAccess dataAccess;
        private DataRecord record;
        private String fieldName;
        private String fieldValue;

        public DataImportXmlParser(DataImport dataImport, IDataAccess dataAccess) {
            super();
            this.dataImport = dataImport;
            this.dataAccess = dataAccess;
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        String getLocation() {
            return locator.getSystemId() + ":" + EfaUtil.int2String(locator.getLineNumber(), 4) + ":" + EfaUtil.int2String(locator.getColumnNumber(), 4) + ":\t";
        }

        public void startDocument() {
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
                Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, "Positions: <SystemID>:<LineNumber>:<ColumnNumber>");
            }
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, getLocation() + "startDocument()");
            }
        }

        public void endDocument() {
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, getLocation() + "endDocument()");
            }
        }

        public void startElement(String uri, String localName, String qname, Attributes atts) {
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
                Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, getLocation() + "startElement(uri=" + uri + ", localName=" + localName + ", qname=" + qname + ")");
            }
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
                for (int i = 0; i < atts.getLength(); i++) {
                    Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, "\tattribute: uri=" + atts.getURI(i) + ", localName=" + atts.getLocalName(i) + ", qname=" + atts.getQName(i) + ", value=" + atts.getValue(i) + ", type=" + atts.getType(i));
                }
            }

            if (record != null) {
                // begin of field (inside record)
                fieldName = localName;
                fieldValue = "";
                return;
            }

            if (record == null) {
                if (localName.equals(DataRecord.ENCODING_RECORD)) {
                    // begin of record
                    record = dataAccess.getPersistence().createNewRecord();
                    fieldName = null;
                    return;
                }
            }
        }

        public void characters(char[] ch, int start, int length) {
            String s = new String(ch, start, length).trim();
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
                Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, getLocation() + "characters(" + s + ")");
            }

            if (fieldName != null) {
                fieldValue += s;
            }
        }

        public void endElement(String uri, String localName, String qname) {
            if (Logger.isTraceOn(Logger.TT_XMLFILE, 9)) {
                Logger.log(Logger.DEBUG, Logger.MSG_FILE_XMLTRACE, getLocation() + "endElement(" + uri + "," + localName + "," + qname + ")");
            }

            if (fieldName != null && localName.equals(fieldName)) {
                // end of field
                record.setFromText(fieldName, fieldValue);
                fieldName = null;
            }

            if (record != null && fieldName == null && localName.equals(DataRecord.ENCODING_RECORD)) {
                // end of record
                dataImport.importRecord(record);
                record = null;
            }
        }
    }
}
