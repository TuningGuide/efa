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
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeTime;

// @i18n complete

public class Messages extends Persistence {

    public static final String DATATYPE = "efa2messages";

    public Messages(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, DATATYPE, International.getString("Nachrichten"));
        MessageRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new MessageRecord(this, MetaData.getMetaData(DATATYPE));
    }

    private long getNextMessageId() {
        AutoIncrement autoIncrement = getProject().getAutoIncrement(false);
        long val = autoIncrement.nextAutoIncrementLongValue(data().getStorageObjectType());
        return val;
    }

    public MessageRecord createMessageRecord() {
        MessageRecord r = new MessageRecord(this, MetaData.getMetaData(DATATYPE));
        r.setMessageId(getNextMessageId());
        r.setDate(DataTypeDate.today());
        r.setTime(DataTypeTime.now());
        return r;
    }

    public MessageRecord createAndSaveMessageRecord(String from, String to, String subject, String text) {
        MessageRecord r = new MessageRecord(this, MetaData.getMetaData(DATATYPE));
        r.setMessageId(getNextMessageId());
        r.setDate(DataTypeDate.today());
        r.setTime(DataTypeTime.now());
        r.setTo(to);
        r.setFrom(from);
        r.setSubject(subject);
        r.setText(text);
        try {
            data().add(r);
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        r.sendEmail();
        return r;
    }

}
