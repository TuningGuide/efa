/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.core.types.*;
import java.util.*;

// @i18n complete

public class Project extends Persistence {

    public Project(int storageType, String storageLocation, String storageObjectName) {
        super(storageType, storageLocation, storageObjectName, "e2prj");
        try {
            ProjectRecord.initialize();
            for (int i=0; i<ProjectRecord.getFieldCount(); i++) {
                dataAccess.registerDataField(ProjectRecord.getFieldName(i), ProjectRecord.getFieldType(i));
            }
            dataAccess.setKey(ProjectRecord.getKeyFields());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public DataRecord createNewRecord() {
        return new ProjectRecord();
    }

    public void setEmptyProject(String name) {
        try {
            dataAccess.truncateAllData();
            ProjectRecord rec;
            rec = (ProjectRecord)createNewRecord();
            rec.setType(ProjectRecord.TYPE_PROJECT);
            rec.setName(name);
            rec.setAdminName("");
            rec.setAdminEmail("");
            dataAccess.add(rec);
        } catch(Exception e) {
        }
    }

    public String getProjectName() {
        try {
            return ((ProjectRecord)dataAccess.get(ProjectRecord.getDataKey(ProjectRecord.TYPE_PROJECT, null))).getName();
        } catch(Exception e) {
            return null;
        }
    }

    public Vector<DataItem> getGuiItems() {
        Vector<DataItem> v = new Vector<DataItem>();
        try {
            DataKeyIterator it = dataAccess.getIterator();
            ProjectRecord rec = (ProjectRecord)dataAccess.getFirst(it);
            while (rec != null) {
                String type = rec.getType();
                if (type == null) {
                    continue;
                }
                if (type.equals(ProjectRecord.TYPE_PROJECT)) {
                    // Name
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.NAME,
                            rec.getName(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Name"))));

                    // Description
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.DESCRIPTION,
                            rec.getDescription(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Beschreibung"))));

                    // AdminName
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.ADMINNAME,
                            rec.getAdminName(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Dein Name"))));

                    // AdminEmail
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.ADMINEMAIL,
                            rec.getAdminEmail(),
                            IItemType.TYPE_PUBLIC, International.getString("Projekt"),
                            International.getString("Deine email-Adresse"))));
                }

                if (type.equals(ProjectRecord.TYPE_LOGBOOK)) {
                    // Name
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.NAME,
                            rec.getName(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Name"))));

                    // Description
                    v.add(new DataItem(rec.getKey(),new ItemTypeString(rec.getKey().toString()+":"+ProjectRecord.DESCRIPTION,
                            rec.getDescription(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Beschreibung"))));

                    // StartDate
                    v.add(new DataItem(rec.getKey(),new ItemTypeDate(rec.getKey().toString()+":"+ProjectRecord.STARTDATE,
                            rec.getStartDate(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Beginn des Zeitraums"))));

                    // EndDate
                    v.add(new DataItem(rec.getKey(),new ItemTypeDate(rec.getKey().toString()+":"+ProjectRecord.ENDDATE,
                            rec.getEndDate(),
                            IItemType.TYPE_PUBLIC, International.getString("Fahrtenbuch"),
                            International.getString("Ende des Zeitraums"))));

                }

                rec = (ProjectRecord)dataAccess.getNext(it);
            }
        } catch(Exception e) {
        }
        return v;
    }

}
