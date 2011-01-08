/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
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
import java.util.*;

public class ImportBoats extends ImportBase {

    private Boote boote;
    private ProjectRecord logbookRec;

    public ImportBoats(ImportTask task, Boote boote, ProjectRecord logbookRec) {
        super(task);
        this.boote = boote;
        this.logbookRec = logbookRec;
    }

    public String getDescription() {
        return International.getString("Boote");
    }

    private boolean isIdentical(Object o, String s) {
        if (o == null && (s == null || s.length() == 0)) {
            return true;
        }
        if (o == null || s == null) {
            return false;
        }
        return (o.toString().equals(s));
    }

    private boolean isChanged(BoatRecord r, DatenFelder d) {
        if (!isIdentical(r.getName(), d.get(Boote.NAME))) {
            return true;
        }
        if (!isIdentical(r.getOwner(), d.get(Boote.VEREIN))) {
            return true;
        }
        // @todo DefaultCrewID
        // @todo AllowedGroupIdList
        if (!isIdentical( (r.getMaxNotInGroup() == DataAccess.UNDEFINED_INT ? null : r.getMaxNotInGroup()), d.get(Boote.MAX_NICHT_IN_GRUPPE))) {
            return true;
        }
        // @todo RequiredGroupId
        if (!isIdentical(r.getFreeUse1(), d.get(Boote.FREI1))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse2(), d.get(Boote.FREI2))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse3(), d.get(Boote.FREI3))) {
            return true;
        }
        return false;
    }

    public boolean runImport() {
        try {
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), boote.getFileName()));

            Boats boats = Daten.project.getBoats(true);
            long validFrom = DataAccess.getTimestampFromDate(logbookRec.getStartDate());

            DatenFelder d = boote.getCompleteFirst();
            String[] IDX = new String[] { BoatRecord.NAME, BoatRecord.OWNER };
            while (d != null) {
                // First search, whether we have imported this boat already
                BoatRecord r = null;
                DataKey[] keys = boats.data().getByFields(IDX, 
                        new String[] {
                                        d.get(Boote.NAME),
                                        (d.get(Boote.VEREIN).length() > 0 ? d.get(Boote.VEREIN) : null) });
                if (keys != null && keys.length > 0) {
                    // We've found one or more boats with same Name and Owner.
                    // Since we're importing data from efa1, these boats are all identical, i.e. have the same ID.
                    // Therefore their key is identical, so we can just retrieve one boat record with keys[0], which
                    // is valid for this logbook.
                    r = (BoatRecord)boats.data().getValidAt(keys[0], validFrom);
                }

                if (r == null || isChanged(r, d)) {
                    r = BoatRecord.createBoatRecord( (r != null ? r.getId() : UUID.randomUUID()) );
                    r.setName(d.get(Boote.NAME));
                    if (d.get(Boote.VEREIN).length() > 0) {
                        r.setOwner(d.get(Boote.VEREIN));
                    }
                    // DefaultCrewID
                    // AllowedGroupIdList
                    if (d.get(Boote.MAX_NICHT_IN_GRUPPE).length() > 0) {
                        r.setMaxNotInGroup(EfaUtil.string2int(d.get(Boote.MAX_NICHT_IN_GRUPPE), 99));
                    }
                    // RequiredGroupId
                    if (d.get(Boote.FREI1).length() > 0) {
                        r.setFreeUse1(d.get(Boote.FREI1));
                    }
                    if (d.get(Boote.FREI2).length() > 0) {
                        r.setFreeUse1(d.get(Boote.FREI2));
                    }
                    if (d.get(Boote.FREI3).length() > 0) {
                        r.setFreeUse1(d.get(Boote.FREI3));
                    }
                    boats.data().addValidAt(r, validFrom);
                    logInfo(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                } else {
                    logInfo(International.getMessage("Identischer Eintrag: {entry}", r.toString()));
                }
                d = boote.getCompleteNext();
            }
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), boote.getFileName()));
            logError(e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
