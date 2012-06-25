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
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.gui.ProgressDialog;
import de.nmichael.efa.util.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;

public class DataImport extends ProgressTask {

    public static final String IMPORTMODE_ADD    = "ADD";           // import as new record; fail for duplicates (also for duplicate versionized records with different validity)
    public static final String IMPORTMODE_UPD    = "UPDATE";        // update existing record; fail if record doesn't exist (for versionized: if no version exists)
    public static final String IMPORTMODE_ADDUPD = "ADD_OR_UPDATE"; // add, or if duplicate, update

    // Import Options for Logbook Import
    public static final String ENTRYNO_DUPLICATE_SKIP   = "DUPLICATE_SKIP";   // if duplicate EntryId, skip entry
    public static final String ENTRYNO_DUPLICATE_ADDEND = "DUPLICATE_ADDEND"; // if duplicate EntryId, add entry with new EntryId at end
    public static final String ENTRYNO_ALWAYS_ADDEND    = "ALWAYS_ADDEND";    // add all entries with new EntryId at end

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
    private String logbookEntryNoHandling;
    private long validAt;
    private String updMode;
    private int importCount = 0;
    private int errorCount = 0;
    private int warningCount = 0;


    public DataImport(StorageObject storageObject,
            String filename, String encoding, char csvSeparator, char csvQuotes,
            String importMode, 
            String updMode,
            String logbookEntryNoHandling,
            long validAt) {
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
        this.logbookEntryNoHandling = logbookEntryNoHandling;
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
        logInfo("\nERROR: " + LogString.operationFailed(
                International.getMessage("Import von Datensatz {record}", r.toString()),msg));
        errorCount++;
    }

    private void logImportWarning(DataRecord r, String msg) {
        logInfo("\nWARNING: " + msg + ": " + r.toString());
        warningCount++;
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
                logImportFailed(r, International.getString("Keine gültige Version des Datensatzes gefunden."));
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

            if (importMode.equals(IMPORTMODE_ADD) &&
                logbookEntryNoHandling != null &&
                logbookEntryNoHandling.equals(ENTRYNO_ALWAYS_ADDEND)) {
                // determine new EntryId for logbook
                r.set(keyFields[0], ((Logbook) storageObject).getNextEntryNo());
            }

            DataKey key = r.getKey();
            if (key.getKeyPart1() == null) {
                // first key field is *not* set
                // -> search for record by QualifiedName
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

            if (importMode.equals(IMPORTMODE_ADD) &&
                otherVersions != null && otherVersions.length > 0 &&
                logbookEntryNoHandling != null &&
                logbookEntryNoHandling.equals(ENTRYNO_DUPLICATE_ADDEND)) {
                r.set(keyFields[0], ((Logbook) storageObject).getNextEntryNo());
                otherVersions = null;
            }

            if (importMode.equals(IMPORTMODE_ADD)) {
                if (otherVersions != null && otherVersions.length > 0) {
                    logImportFailed(r, International.getString("Datensatz existiert bereits"));
                } else {
                    addRecord(r);
                }
                return;
            }
            if (importMode.equals(IMPORTMODE_UPD)) {
                if (otherVersions == null || otherVersions.length == 0) {
                    logImportFailed(r, International.getString("Datensatz nicht gefunden"));
                } else {
                    updateRecord(r);
                }
                return;
            }
            if (importMode.equals(IMPORTMODE_ADDUPD)) {
                if (otherVersions != null && otherVersions.length > 0) {
                    updateRecord(r);
                } else {
                    addRecord(r);
                }
                return;
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
                                if (!r.setFromText(header[i], value.trim())) {
                                    logImportWarning(r, "Value '" + value + "' for Field '"+header[i] + "' corrected to '" + r.getAsText(header[i]) + "'");
                                }
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
        this.logInfo("\n" + International.getMessage("{count} Warnungen.", warningCount));
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

    public class DataImportXmlParser extends XmlHandler {

        private DataImport dataImport;
        private IDataAccess dataAccess;
        private DataRecord record;

        public DataImportXmlParser(DataImport dataImport, IDataAccess dataAccess) {
            super(DataExport.FIELD_EXPORT);
            this.dataImport = dataImport;
            this.dataAccess = dataAccess;
        }

        public void startElement(String uri, String localName, String qname, Attributes atts) {
            super.startElement(uri, localName, qname, atts);

            if (localName.equals(DataRecord.ENCODING_RECORD)) {
                // begin of record
                record = dataAccess.getPersistence().createNewRecord();
                return;
            }
        }

        public void endElement(String uri, String localName, String qname) {
            super.endElement(uri, localName, qname);

            if (record != null && localName.equals(DataRecord.ENCODING_RECORD)) {
                // end of record
                dataImport.importRecord(record);
                record = null;
            }
            if (record != null && fieldValue != null) {
                // end of field
                if (!record.setFromText(fieldName, fieldValue.trim())) {
                    dataImport.logImportWarning(record, "Value '" + fieldValue + "' for Field '" + fieldName + "' corrected to '" + record.getAsText(fieldName) + "'");
                }
            }

        }
    }
}
