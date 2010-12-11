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
import de.nmichael.efa.data.types.*;

public class ImportLogbook {

    public static void importData(ImportMetadata meta) {
        ProjectRecord logbookRec = Daten.project.createNewLogbookRecord(meta.name);
        logbookRec.setDescription(meta.description);
        logbookRec.setStartDate(meta.firstDate);
        logbookRec.setEndDate(meta.lastDate);
    }

}
