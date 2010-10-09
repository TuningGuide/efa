/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.ex.EfaException;

// @i18n complete

public abstract class Persistence {

    protected IDataAccess dataAccess;

    public Persistence(int storageType, String storageLocation, String storageObjectName, String storageObjectType, String storageObjectDescription) {
        dataAccess = DataAccess.createDataAccess(this, storageType, storageLocation, storageObjectName, storageObjectType, storageObjectDescription);
    }

    public void open(boolean createNewIfNotExists) throws EfaException {
        try {
            dataAccess.openStorageObject();
        } catch(EfaException eOpen) {
            if (createNewIfNotExists) {
                try {
                    dataAccess.createStorageObject();
                } catch(EfaException eCreate) {
                    throw eCreate;
                }
            } else {
                throw eOpen;
            }
        }
    }

    public void close() throws EfaException {
        dataAccess.closeStorageObject();
    }

    public boolean isOpen() {
        return dataAccess.isStorageObjectOpen();
    }

    public IDataAccess data() {
        return dataAccess;
    }

    public abstract DataRecord createNewRecord();


}
