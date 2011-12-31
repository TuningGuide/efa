/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.dataedit;

import de.nmichael.efa.util.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.DataKey;
import de.nmichael.efa.data.storage.DataKeyIterator;
import de.nmichael.efa.ex.EfaModifyException;
import de.nmichael.efa.ex.InvalidValueException;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

// @i18n complete
public class ProjectEditDialog extends UnversionizedDataEditDialog {

    Project project;
    String logbookName;

    public ProjectEditDialog(Frame parent, Project p, String logbookName, int subtype) {
        super(parent, International.getString("Projekt"), null, false);
        iniItems(p, logbookName, subtype);
    }

    public ProjectEditDialog(JDialog parent, Project p, String logbookName, int subtype) {
        super(parent, International.getString("Projekt"), null, false);
        iniItems(p, logbookName, subtype);
    }

    private void iniItems(Project p, String logbookName, int subtype) {
        this.project = p;
        this.logbookName = logbookName;
        Vector<IItemType> guiItems = new Vector<IItemType>();
        try {
            ProjectRecord r;
            if (logbookName != null) {
                r = p.getLoogbookRecord(logbookName);
                if (r != null) {
                    guiItems.addAll(r.getGuiItems(subtype, null, false));
                }
            } else {
                r = p.getProjectRecord();
                if (r != null) {
                    guiItems.addAll(r.getGuiItems(subtype, null, false));
                }
                r = p.getClubRecord();
                if (r != null) {
                    guiItems.addAll(r.getGuiItems(subtype, null, false));
                }
                String[] logbooks = p.getAllLogbookNames();
                for (int i = 0; logbooks != null && i < logbooks.length; i++) {
                    r = p.getLoogbookRecord(logbooks[i]);
                    Vector<IItemType> v = r.getGuiItems(subtype, null, false);
                    for (int j = 0; j < v.size(); j++) {
                        IItemType item = v.get(j);
                        item.setName(r.getKey().toString() + ":" + item.getName());
                        guiItems.add(item);
                    }
                }
                r = p.getConfigRecord();
                if (r != null) {
                    guiItems.addAll(r.getGuiItems(subtype, null, false));
                }
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }

        this.setItems(guiItems);
    }


    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    boolean saveRecord() throws InvalidValueException {
        for (IItemType item : getItems()) {
            if (!item.isValidInput() && item.isVisible()) {
                throw new InvalidValueException(item, item.getInvalidErrorText());
                // @todo (P4) make sure that if dates of logbook is changed, that all sessions are still within the range!
            }
        }
        try {
            // find all DataKey's of records to be updated
            Hashtable<DataKey,String> dataKeys = new Hashtable<DataKey,String>();
            for (IItemType item : getItems()) {
                dataKeys.put(item.getDataKey(), "foo");
            }
            DataKey[] keys = dataKeys.keySet().toArray(new DataKey[0]);

            // find all records and update them
            for (int i=0; i<keys.length; i++) {
                // get all items with this key
                DataKey k = keys[i];
                Vector<IItemType> ki = new Vector<IItemType>();
                for (IItemType item : getItems()) {
                    if (item.getDataKey().equals(k)) {
                        int pos = item.getName().indexOf(":");
                        if (pos >= 0) {
                            item.setName(item.getName().substring(pos+1));
                        }
                        ki.add(item);
                    }
                }
                ProjectRecord r = project.getRecord(k);
                if (r != null) {
                    // r can be null for remote projects which aren't yet open
                    r.saveGuiItems(ki);
                    project.getMyDataAccess(r.getType()).update(r);
                }
            }
            for(IItemType item : getItems()) {
                item.setUnchanged();
            }
            return true;
        } catch(EfaModifyException emodify) {
            emodify.displayMessage();
            return false;
        } catch(Exception e) {
            Logger.logdebug(e);
            Dialog.error("Die Änderungen konnten nicht gespeichert werden." + "\n" + e.toString());
            return false;
        }
    }

}
