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

public abstract class CSVFile extends DataFile {

    public CSVFile(String directory, String filename, String extension) {
        super(directory, filename, extension);
    }

    public int getStorageType() {
        return IDataAccess.TYPE_FILE_CSV;
    }

}