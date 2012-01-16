/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.ex.EfaModifyException;
import java.util.*;

// @i18n complete

public class Logbook extends StorageObject {

    public static final String DATATYPE = "efa2logbook";

    private String name;
    private ProjectRecord projectRecord;

    public Logbook(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, 
                International.getString("Fahrtenbuch") + " " + storageObjectName);
        LogbookRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProjectRecord(ProjectRecord r) {
        this.projectRecord = r;
    }

    public DataTypeDate getStartDate() {
        return (projectRecord != null ? projectRecord.getStartDate() : null);
    }

    public DataTypeDate getEndDate() {
        return (projectRecord != null ? projectRecord.getEndDate() : null);
    }

    public LogbookRecord createLogbookRecord(DataTypeIntString entryNo) {
        LogbookRecord r = new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
        r.setEntryId(entryNo);
        return r;
    }

    public LogbookRecord getLogbookRecord(DataTypeIntString entryNo) {
        return getLogbookRecord(LogbookRecord.getKey(entryNo));
    }

    public LogbookRecord getLogbookRecord(DataKey key) {
        try {
            return (LogbookRecord)(data().get(key));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public LogbookRecord findDuplicateEntry(LogbookRecord r, int rangeFromEnd) {
        if (r == null || r.getEntryId() == null ||
            r.getDate() == null || !r.getDate().isSet() ||
            r.getBoatAsName().length() == 0) {
            return null;
        }
        Vector<String> p = r.getAllCoxAndCrewAsNames();
        try {
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getLast();
            while (k != null && rangeFromEnd-- > 0) {
                LogbookRecord r0 = getLogbookRecord(k);
                if (r0 != null &&
                    !r.getEntryId().equals(r0.getEntryId()) &&
                    r.getDate().equals(r0.getDate()) &&
                    r.getBoatAsName().equals(r0.getBoatAsName())) {
                    // Records are identical in Data and BoatName
                    Vector<String> p0 = r0.getAllCoxAndCrewAsNames();
                    int matches = 0;
                    for (int i=0; i<p0.size(); i++) {
                        if (p.contains(p0.get(i))) {
                            matches++;
                        }
                    }
                    if (matches > 0 && p.size() - matches <=2) {
                        // at least one crew member identical, and less than 2 crew members different
                        // --> this is a potentially duplicate record
                        return r0;
                    }
                }
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, LogbookRecord.ENTRYID);
            assertUnique(record, LogbookRecord.ENTRYID);
            
            LogbookRecord r = (LogbookRecord)record;

            // make sure enddate is after startdate
            if (r.getDate() != null && r.getDate().isSet() && r.getEndDate() != null && r.getEndDate().isSet() && !r.getDate().isBefore(r.getEndDate())) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getString("Das Enddatum muß nach dem Startdatum liegen."),
                        Thread.currentThread().getStackTrace());
            }

            // make sure that the entry's date fits into the selected session group
            SessionGroupRecord sg = r.getSessionGroup();
            if (sg != null && !sg.checkLogbookRecordFitsIntoRange(r)) {
                    throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                              International.getMessage("Das Datum des Fahrtenbucheintrags {entry} liegt außerhalb des Zeitraums, "
                        + "der für die ausgewählte Fahrtgruppe '{name}' angegeben wurde.",
                        r.getEntryId().toString(), sg.getName()),
                    Thread.currentThread().getStackTrace());
            }
        }
        if (delete) {
            assertNotReferenced(record, getProject().getBoatStatus(false), new String[] { BoatStatusRecord.ENTRYNO }, true,
                    new String[] { BoatStatusRecord.LOGBOOK }, new String[] { getName() } );
        }
    }

}
