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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class MessageRecord extends DataRecord {

    public static final String TO_ADMIN              = "ADMIN";
    public static final String TO_BOATMAINTENANCE    = "BOATM";

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String MESSAGEID             = "MessageId";
    public static final String DATE                  = "Date";
    public static final String TIME                  = "Time";
    public static final String TO                    = "To";
    public static final String FROM                  = "From";
    public static final String SUBJECT               = "Subject";
    public static final String TEXT                  = "Text";
    public static final String READ                  = "Read";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(MESSAGEID);                         t.add(IDataAccess.DATA_LONGINT);
        f.add(DATE);                              t.add(IDataAccess.DATA_DATE);
        f.add(TIME);                              t.add(IDataAccess.DATA_TIME);
        f.add(TO);                                t.add(IDataAccess.DATA_STRING);
        f.add(FROM);                              t.add(IDataAccess.DATA_STRING);
        f.add(SUBJECT);                           t.add(IDataAccess.DATA_STRING);
        f.add(TEXT);                              t.add(IDataAccess.DATA_STRING);
        f.add(READ);                              t.add(IDataAccess.DATA_BOOLEAN);
        MetaData metaData = constructMetaData(Messages.DATATYPE, f, t, false);
        metaData.setKey(new String[] { MESSAGEID });
    }

    public MessageRecord(Messages messages, MetaData metaData) {
        super(messages, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<Long,String,String>(getMessageId(),null,null);
    }

    public static DataKey getKey(long messageId) {
        return new DataKey<Long,String,String>(messageId,null,null);
    }

    protected void setMessageId(long messageId) {
        setLong(MESSAGEID, messageId);
    }
    public long getMessageId() {
        return getLong(MESSAGEID);
    }

    public void setDate(DataTypeDate date) {
        setDate(DATE, date);
    }
    public DataTypeDate getDate() {
        return getDate(DATE);
    }

    public void setTime(DataTypeTime time) {
        setTime(TIME, time);
    }
    public DataTypeTime getTime() {
        return getTime(TIME);
    }

    public void setTo(String to) {
        setString(TO, to);
    }
    public String getTo() {
        return getString(TO);
    }
    public String getToAsName() {
        String to = getString(TO);
        if (to != null && to.equals(TO_ADMIN)) {
            return International.getString("Administrator");
        }
        if (to != null && to.equals(TO_BOATMAINTENANCE)) {
            return International.getString("Bootswart");
        }
        return null;
    }

    public void setFrom(String from) {
        setString(FROM, from);
    }
    public String getFrom() {
        return getString(FROM);
    }

    public void setSubject(String subject) {
        setString(SUBJECT, subject);
    }
    public String getSubject() {
        return getString(SUBJECT);
    }

    public void setText(String text) {
        setString(TEXT, text);
    }
    public String getText() {
        return getString(TEXT);
    }

    public void setRead(boolean read) {
        setBool(READ, read);
    }
    public boolean getRead() {
        return getBool(READ);
    }

    public boolean sendEmail() {
        if (Daten.admins == null) {
            return false;
        }
        try {
            StringBuffer recipients = new StringBuffer();
            DataKeyIterator it = Daten.admins.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                AdminRecord admin = (AdminRecord)Daten.admins.data().get(k);
                if (admin.getEmail() != null && admin.getEmail().length() > 0 &&
                     ((getTo().equals(TO_ADMIN) && admin.isAllowedMsgReadAdmin()) ||
                      (getTo().equals(TO_BOATMAINTENANCE) && admin.isAllowedMsgReadBoatMaintenance())) ) {
                    recipients.append( (recipients.length() > 0 ? ", " : "") + admin.getName() + " <" + admin.getEmail() + ">");
                }
                k = it.getNext();
            }
            if (recipients.length() > 0) {
                EmailSender.sendEmail(this, recipients.toString());
            }
        } catch (Exception e) {
            Logger.logdebug(e);
            return false;
        }
        return true;
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Nachricht");
        boolean newMsg = getFrom() == null || getFrom().length() == 0;
        long now = System.currentTimeMillis();

        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        v.add(item = new ItemTypeDateTime(DATE+TIME, getDate(), getTime(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Datum")));
        item.setEditable(false);

        v.add(item = new ItemTypeStringList(TO, getTo(),
                new String[] { TO_ADMIN, TO_BOATMAINTENANCE },
                new String[] { International.getString("Administrator"),
                               International.getString("Bootswart")
                },
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("An")));
        item.setEditable(newMsg);
        item.setNotNull(true);

        v.add(item = new ItemTypeStringAutoComplete(FROM, getFrom(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Von"), false));
        ((ItemTypeStringAutoComplete)item).setAutoCompleteData(new AutoCompleteList(Daten.project.getPersons(false).data(), now, now));
        ((ItemTypeStringAutoComplete)item).setAlwaysReturnPlainText(true);
        item.setEditable(newMsg);
        item.setNotNull(true);

        v.add(item = new ItemTypeString(SUBJECT, getSubject(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Betreff")));
        item.setEditable(newMsg);
        item.setNotNull(true);

        v.add(item = new ItemTypeString(TEXT, getText(), // @todo (P3) message text must be TextArea instead of TextField
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Nachricht")));
        item.setEditable(newMsg);
        item.setNotNull(true);

        if (!newMsg) {
            v.add(item = new ItemTypeBoolean(READ, getRead(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    International.getString("gelesen")));
            // item.setEditable(); @todo (P4) Messages - allow to mark read depending on admin privileges
        }

        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[4];
        header[0] = new TableItemHeader(International.getString("Datum"));
        header[1] = new TableItemHeader(International.getString("Von"));
        header[2] = new TableItemHeader(International.getString("An"));
        header[3] = new TableItemHeader(International.getString("Betreff"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[4];
        items[0] = new TableItem(getDate().toString() + " " + getTime().toString());
        items[1] = new TableItem(getFrom());
        items[2] = new TableItem(getTo());
        items[3] = new TableItem(getSubject());
        return items;
    }
}
