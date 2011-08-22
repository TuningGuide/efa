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

public class Logbook extends Persistence {

    public static final String DATATYPE = "efa2logbook";

    private String name;
    private ProjectRecord projectRecord;

    public Logbook(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Fahrtenbuch"));
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
        if (delete) {
            assertNotReferenced(record, getProject().getBoatStatus(false), new String[] { BoatStatusRecord.ENTRYNO }, true,
                    new String[] { BoatStatusRecord.LOGBOOK }, new String[] { getName() } );
        }
    }

}
