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

public class Persistence {

    protected IDataAccess dataAccess;

    public Persistence(int storageType, String storageLocation, String storageObjectName, String storageObjectType) {
        dataAccess = DataAccess.createDataAccess(storageType, storageLocation, storageObjectName, storageObjectType);
    }


}
