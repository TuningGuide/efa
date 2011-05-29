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
import de.nmichael.efa.ex.*;
import java.util.*;

// @i18n complete

public class Waters extends Persistence {

    public static final String DATATYPE = "efa2waters";

    public Waters(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Gewässer"));
        WatersRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new WatersRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public WatersRecord createWatersRecord(UUID id) {
        WatersRecord r = new WatersRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    public WatersRecord getWaters(UUID id) {
        try {
            return (WatersRecord)data().get(WatersRecord.getKey(id));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public WatersRecord findWatersByName(String name) {
        try {
            DataKey[] keys = data().getByFields(new String[] { WatersRecord.NAME }, new String[] { name });
            if (keys != null && keys.length > 0) {
                return (WatersRecord) data().get(keys[0]);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        WatersRecord wr = (WatersRecord) record;
        if (add || update) {
            if (wr.getName() == null || wr.getName().trim().length() == 0) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getMessage("Das Feld '{field}' darf nicht leer sein.", WatersRecord.NAME),
                        Thread.currentThread().getStackTrace());
            }
            WatersRecord wr0 = findWatersByName(wr.getName());
            if (wr0 != null && !wr0.getId().equals(wr.getId())) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getString("Es gibt bereits einen gleichnamigen Eintrag.") + " "  +
                        International.getMessage("Das Feld '{field}' muß eindeutig sein.", WatersRecord.NAME),
                        Thread.currentThread().getStackTrace());
            }
        }
        if (delete) {
            String refRec = null;
            try {
                Destinations destinations = getProject().getDestinations(false);
                DataKeyIterator it = destinations.data().getStaticIterator();
                DataKey key = it.getFirst();
                while (key != null) {
                    DestinationRecord r = (DestinationRecord)destinations.data().get(key);
                    if (r != null && !r.getDeleted() && r.getWatersIdList() != null && r.getWatersIdList().contains(wr.getId())) {
                        refRec = r.getQualifiedName();
                        break;
                    }
                    key = it.getNext();
                }
            } catch(Exception e) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        e.toString(),
                        Thread.currentThread().getStackTrace());
            }
            if (refRec != null) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getMessage("Der Datensatz kann nicht gelöscht werden, da er noch von {listtype} '{record}' genutzt wird.",
                        International.getString("Ziel"), refRec),
                        Thread.currentThread().getStackTrace());
            }
        }
    }

}
