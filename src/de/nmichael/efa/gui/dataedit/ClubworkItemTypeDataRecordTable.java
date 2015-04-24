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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.items.IItemListenerDataRecordTable;
import de.nmichael.efa.core.items.ItemTypeDataRecordTable;
import de.nmichael.efa.data.ClubworkRecord;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.gui.util.TableItem;
import de.nmichael.efa.gui.util.TableItemHeader;
import de.nmichael.efa.util.Logger;

import java.util.Hashtable;
import java.util.Vector;

// @i18n complete

public class ClubworkItemTypeDataRecordTable extends ItemTypeDataRecordTable {

    protected boolean showNonApproved = false;

    public ClubworkItemTypeDataRecordTable(String name,
                                           TableItemHeader[] tableHeader,
                                           StorageObject persistence,
                                           long validAt,
                                           AdminRecord admin,
                                           String filterFieldName, String filterFieldValue,
                                           String[] actions, int[] actionTypes, String[] actionIcons,
                                           IItemListenerDataRecordTable itemListenerActionTable,
                                           int type, String category, String description) {
        super("TABLE",
                ((ClubworkRecord)persistence.createNewRecord()).getGuiTableHeader(admin),
                persistence, validAt, admin,
                filterFieldName, filterFieldValue, // defaults are null
                actions, actionTypes, actionIcons, // default actions: new, edit, delete
                itemListenerActionTable,
                type, category, description);
    }

    protected TableItem[] getSpecialisedGuiTableItems(DataRecord r) {
        return ((ClubworkRecord)r).getGuiTableItems(admin);
    }

    protected void updateFilter() {
        searchField.getValueFromGui();
        filterBySearch.getValueFromGui();
        boolean aggre = filterBySearch.getValue() && searchField.getValue().trim().length() >= 3;
        if(aggre) {
            filterFieldName = null;
        }
        else {
            filterFieldName = "Flag";
        }
        if (filterBySearch.isChanged() || (filterBySearch.getValue() && searchField.isChanged())) {
            updateData();
            updateAggregations(aggre);
            showValue();

        }
        filterBySearch.setUnchanged();
        searchField.setUnchanged();
    }

    public void showOnlyNonApproved(boolean showNonApproved) {
        this.showNonApproved = showNonApproved;
        updateData();
    }

    protected void updateData() {
        if (persistence == null) {
            return;
        }
        try {
            String filterByAnyText = null;
            if (filterBySearch != null && searchField != null) {
                filterBySearch.getValueFromField();
                searchField.getValueFromGui();
                if (filterBySearch.getValue() && searchField.getValue() != null && searchField.getValue().length() > 0) {
                    filterByAnyText = searchField.getValue().toLowerCase();
                }
            }
            myValidAt = (validAt >= 0 ? validAt : System.currentTimeMillis());
            data = new Vector<DataRecord>();
            IDataAccess dataAccess = persistence.data();
            boolean isVersionized = dataAccess.getMetaData().isVersionized();
            DataKeyIterator it = dataAccess.getStaticIterator();
            DataKey key = it.getFirst();
            Hashtable<DataKey, String> uniqueHash = new Hashtable<DataKey, String>();
            while (key != null) {
                // avoid duplicate versionized keys for the same record
                if (isVersionized) {
                    DataKey ukey = dataAccess.getUnversionizedKey(key);
                    if (uniqueHash.get(ukey) != null) {
                        key = it.getNext();
                        continue;
                    }
                    uniqueHash.put(ukey, "");
                }

                DataRecord r;
                if (isVersionized) {
                    r = dataAccess.getValidAt(key, myValidAt);
                    if (r == null && showAll) {
                        r = dataAccess.getValidLatest(key);
                    }
                } else {
                    r = dataAccess.get(key);
                    if (!showAll && !r.isValidAt(myValidAt)) {
                        r = null;
                    }
                }
                if (r == null && showDeleted) {
                    DataRecord[] any = dataAccess.getValidAny(key);
                    if (any != null && any.length > 0 && any[0].getDeleted()) {
                        r = any[0];
                    }
                }
                if( showNonApproved && r instanceof ClubworkRecord) {
                    try {
                        if(r.getLastModified() <= Daten.project.getCurrentClubwork().getProjectRecord().getClubworkApprovedLong()) {
                            r = null;
                        }
                    }
                    catch (NullPointerException eignore) {
                        Logger.logdebug(eignore);
                    }
                }
                if (r != null && (!r.getDeleted() || showDeleted)) {
                    if (filterFieldName == null || filterFieldValue == null
                            || filterFieldValue.equals(r.getAsString(filterFieldName))) {
                        if (filterByAnyText == null || r.getAllFieldsAsSeparatedText().toLowerCase().indexOf(filterByAnyText) >= 0) {
                            data.add(r);
                        }
                    }
                }
                key = it.getNext();
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }
    }
}
