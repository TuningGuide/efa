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

// @i18n complete

public abstract class Persistence {

    protected IDataAccess dataAccess;

    public Persistence(int storageType, String storageLocation, String storageObjectName, String storageObjectType) {
        dataAccess = DataAccess.createDataAccess(this, storageType, storageLocation, storageObjectName, storageObjectType);
    }

    public void open(boolean createNewIfNotExists) throws Exception {
        try {
            dataAccess.openStorageObject();
        } catch(Exception eOpen) {
            if (createNewIfNotExists) {
                try {
                    dataAccess.createStorageObject();
                } catch(Exception eCreate) {
                    throw eCreate;
                }
            } else {
                throw eOpen;
            }
        }
    }

    public void close() throws Exception {
        dataAccess.closeStorageObject();
    }

    public IDataAccess data() {
        return dataAccess;
    }

    public abstract DataRecord createNewRecord();


}
