/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.sync;

import com.funambol.syncclient.spds.engine.*;
import com.funambol.syncclient.spds.SyncException;
import java.security.Principal;
import java.util.Date;

// @i18n complete

public class EfaSyncSource implements SyncSource {

    public void beginSync(int i) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void commitSync() throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem[] getAllSyncItems(Principal prncpl) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem[] getDeletedSyncItems(Principal prncpl, Date date) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem[] getNewSyncItems(Principal prncpl, Date date) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSourceURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem[] getUpdatedSyncItems(Principal prncpl, Date date) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeSyncItem(Principal prncpl, SyncItem si) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem setSyncItem(Principal prncpl, SyncItem si) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
