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

// @i18n complete

import com.funambol.syncclient.spdm.SimpleDeviceManager;
import com.funambol.syncclient.spds.*;
import com.funambol.syncclient.spds.engine.*;

public class SyncWithKanuEfb {

    public static void main(String[] args) throws Exception {
        System.setProperty(SimpleDeviceManager.PROP_DM_DIR_BASE, "/home/nick/.efa2/tmp");
        SyncManager syncManager = SyncManager.getSyncManager("kanuefb/efa");
        syncManager.sync();
    }

}
