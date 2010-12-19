/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.importefa1;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.efa1.*;
import de.nmichael.efa.util.*;

public class ImportLogbook extends ImportBase {
    
    public ImportLogbook(ImportTask task, String efa1fname, ImportMetadata meta) {
        super(task, efa1fname, meta);
    }

    public boolean runImport() {
        try {
            Fahrtenbuch fahrtenbuch = new Fahrtenbuch(efa1fname);
            if (!fahrtenbuch.readFile()) {
                task.logInfo(LogString.logstring_fileOpenFailed(efa1fname, International.getString("Fahrtenbuch")));
                return false;
            }

            ProjectRecord logbookRec = Daten.project.createNewLogbookRecord(meta.name);
            logbookRec.setDescription(meta.description);
            logbookRec.setStartDate(meta.firstDate);
            logbookRec.setEndDate(meta.lastDate);
            Daten.project.addLogbookRecord(logbookRec);

            Logbook logbook = Daten.project.getLogbook(meta.name, true);
            Boats boats = Daten.project.getBoats(true);

            DatenFelder d = fahrtenbuch.getCompleteFirst();
            while (d != null) {
                LogbookRecord r = LogbookRecord.createLogbookRecord(d.get(Fahrtenbuch.LFDNR));
                r.setDate(DataTypeDate.parseDate(d.get(Fahrtenbuch.DATUM)));
                r.setBoatName(d.get(Fahrtenbuch.BOOT));
                if (d.get(Fahrtenbuch.STM).length() > 0) {
                    r.setCoxName(d.get(Fahrtenbuch.STM));
                }
                for (int i=0; i<Fahrtenbuch.ANZ_MANNSCH; i++) {
                    if (d.get(Fahrtenbuch.MANNSCH1 + i).length() > 0) {
                        r.setCrewName(i+1, d.get(Fahrtenbuch.MANNSCH1 + i));
                    }
                }
                r.setBoatCaptainPosition(EfaUtil.string2int(d.get(Fahrtenbuch.OBMANN),-1));
                r.setStartTime(DataTypeTime.parseTime(d.get(Fahrtenbuch.ABFAHRT)));
                r.setEndTime(DataTypeTime.parseTime(d.get(Fahrtenbuch.ANKUNFT)));
                r.setDestinationName(d.get(Fahrtenbuch.ZIEL));
                r.setBoatDistance(EfaUtil.string2int(d.get(Fahrtenbuch.BOOTSKM), 0), 1, null);
                if (d.get(Fahrtenbuch.BEMERK).length() > 0) {
                    r.setComments(d.get(Fahrtenbuch.BEMERK));
                }
                // set SessionType
                // set MultiDay
                logbook.data().add(r);
                d = fahrtenbuch.getCompleteNext();
            }
            logbook.close();
        } catch(Exception e) {
            task.logInfo(LogString.logstring_fileOpenFailed(efa1fname, e.toString()));
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
