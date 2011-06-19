/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class SearchLogbookDialog extends BaseTabbedDialog implements IItemListener {

    enum SearchMode {
        none,
        normal,
        special
    }

    private static EfaBaseFrame efaBaseFrame;
    private static SearchLogbookDialog searchLogbookDialog = null;
    private static Logbook logbook;
    private static DataKeyIterator it;
    private static SearchMode searchMode = SearchMode.none;
    
    private static final String CAT_NORMAL = "%01%" +  International.getString("normale Suche");
    private ItemTypeString  sSearchText;
    private ItemTypeBoolean sEntryno;
    private ItemTypeBoolean sDate;
    private ItemTypeBoolean sEnddate;
    private ItemTypeBoolean sBoat;
    private ItemTypeBoolean sCox;
    private ItemTypeBoolean sCrew;
    private ItemTypeBoolean sStarttime;
    private ItemTypeBoolean sEndtime;
    private ItemTypeBoolean sDestination;
    private ItemTypeBoolean sDistance;
    private ItemTypeBoolean sComments;
    private ItemTypeBoolean sSessiontype;
    private ItemTypeButton  sbAll;
    private ItemTypeButton  sbNone;

    private static final String CAT_SPECIAL = "%02%" +  International.getString("Spezialsuche");
    private ItemTypeBoolean eIncomplete;
    private ItemTypeBoolean eUnknownBoat;
    private ItemTypeBoolean eUnknownPerson;
    private ItemTypeBoolean eUnknownPersonIgnoreGuest;
    private ItemTypeBoolean eUnknownDestination;
    private ItemTypeBoolean eOpenEntry;
    private ItemTypeDistance eLargeDistance;

    private SearchLogbookDialog(EfaBaseFrame parent) {
        super(parent, International.getString("Suche"), International.getStringWithMnemonic("Suchen"));

        IItemType item;
        Vector<IItemType> items = new Vector<IItemType>();

        items.add(sSearchText = new ItemTypeString("SEARCH_STRING", "", IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Suchbegriff")));
        sSearchText.registerItemListener(this);
        items.add(item = new ItemTypeLabel("SEARCH_IN_FIELDS", IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Suche in folgenden Feldern")));
        item.setPadding(0, 0, 10, 0);
        items.add(sEntryno = new ItemTypeBoolean(LogbookRecord.ENTRYID, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Lfd. Nr.")));
        items.add(sDate = new ItemTypeBoolean(LogbookRecord.DATE, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Datum")));
        items.add(sEnddate = new ItemTypeBoolean(LogbookRecord.ENDDATE, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Enddatum")));
        items.add(sBoat = new ItemTypeBoolean(LogbookRecord.BOATNAME, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Boot")));
        items.add(sCox = new ItemTypeBoolean(LogbookRecord.COXNAME, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Steuermann")));
        items.add(sCrew = new ItemTypeBoolean(LogbookRecord.CREW1NAME, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Mannschaft")));
        items.add(sStarttime = new ItemTypeBoolean(LogbookRecord.STARTTIME, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Abfahrt")));
        items.add(sEndtime = new ItemTypeBoolean(LogbookRecord.ENDTIME, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Ankunft")));
        items.add(sDestination = new ItemTypeBoolean(LogbookRecord.DESTINATIONNAME, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Ziel")));
        items.add(sDistance = new ItemTypeBoolean(LogbookRecord.DISTANCE, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Kilometer")));
        items.add(sComments = new ItemTypeBoolean(LogbookRecord.COMMENTS, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Bemerkungen")));
        items.add(sSessiontype = new ItemTypeBoolean(LogbookRecord.SESSIONTYPE, true, IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Fahrtart")));
        items.add(sbAll = new ItemTypeButton("SEARCH_SELECT_ALL", IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("alle")));
        sbAll.setFieldGrid(2, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        sbAll.setPadding(0, 0, 10, 0);
        sbAll.registerItemListener(this);
        items.add(sbNone = new ItemTypeButton("SEARCH_SELECT_NONE", IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("keine")));
        sbNone.setFieldGrid(2, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        sbNone.registerItemListener(this);
        items.add(item = new ItemTypeLabel("CONTINUE_SEARCH1", IItemType.TYPE_PUBLIC, CAT_NORMAL, International.getString("Weitersuchen mit F3")));
        item.setPadding(0, 0, 10, 0);

        items.add(eIncomplete = new ItemTypeBoolean("ESEARCH_INCOMPLETE", true, IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("unvollständige Einträge")));
        items.add(eUnknownPersonIgnoreGuest = new ItemTypeBoolean("ESEARCH_UNKNOWNBOAT", true, IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("Einträge mit unbekannten Booten")));
        items.add(eUnknownBoat = new ItemTypeBoolean("ESEARCH_UNKNOWNPERSON", true, IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("Einträge mit unbekannten Personen")));
        items.add(eUnknownPerson = new ItemTypeBoolean("ESEARCH_UNKNOWNPERSONIGNOREGUEST", true, IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getMessage("Unbekannte Einträge mit '{guest}' ignorieren",
                Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST))));
        items.add(eUnknownDestination = new ItemTypeBoolean("ESEARCH_UNKNOWNDESTINATION", true, IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("Einträge mit unbekannten Zielen")));
        items.add(eOpenEntry = new ItemTypeBoolean("ESEARCH_OPENENTRY", true, IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("nicht zurückgetragene Einträge")));
        items.add(eLargeDistance = new ItemTypeDistance("ESEARCH_LARGEDISTANCE", DataTypeDistance.parseDistance("30 "+DataTypeDistance.KILOMETERS), IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("Einträge mit Kilometern größer als")));
        items.add(item = new ItemTypeLabel("CONTINUE_SEARCH2", IItemType.TYPE_PUBLIC, CAT_SPECIAL, International.getString("Weitersuchen mit F3")));
        item.setPadding(0, 0, 10, 0);

        setItems(items);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public static void showSearchDialog(EfaBaseFrame parent, Logbook logbook, DataKeyIterator iterator) {
        if (searchLogbookDialog == null || parent != efaBaseFrame) {
            searchLogbookDialog = new SearchLogbookDialog(parent);
        }
        efaBaseFrame = parent;
        SearchLogbookDialog.logbook = logbook;
        SearchLogbookDialog.it = iterator;
        if (CAT_NORMAL.equals(searchLogbookDialog.getSelectedPanel(searchLogbookDialog.tabbedPane))) {
            searchLogbookDialog.setRequestFocus(searchLogbookDialog.sSearchText);
        }
        searchLogbookDialog.showDialog();
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == sbAll && event.getID() == ActionEvent.ACTION_PERFORMED) {
            selectAllSearchFields(true);
        }
        if (itemType == sbNone && event.getID() == ActionEvent.ACTION_PERFORMED) {
            selectAllSearchFields(false);
        }
        if (itemType == sSearchText && event.getID() == KeyEvent.KEY_PRESSED &&
                ((KeyEvent)event).getKeyCode() == KeyEvent.VK_ENTER) {
            closeButton_actionPerformed(null);
        }
    }

    private void selectAllSearchFields(boolean selected) {
        sEntryno.parseAndShowValue(Boolean.toString(selected));
        sDate.parseAndShowValue(Boolean.toString(selected));
        sEnddate.parseAndShowValue(Boolean.toString(selected));
        sBoat.parseAndShowValue(Boolean.toString(selected));
        sCox.parseAndShowValue(Boolean.toString(selected));
        sCrew.parseAndShowValue(Boolean.toString(selected));
        sStarttime.parseAndShowValue(Boolean.toString(selected));
        sEndtime.parseAndShowValue(Boolean.toString(selected));
        sDestination.parseAndShowValue(Boolean.toString(selected));
        sDistance.parseAndShowValue(Boolean.toString(selected));
        sComments.parseAndShowValue(Boolean.toString(selected));
        sSessiontype.parseAndShowValue(Boolean.toString(selected));
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        if (getSelectedPanel(tabbedPane).equals(CAT_NORMAL)) {
            searchMode = SearchMode.normal;
        }
        if (getSelectedPanel(tabbedPane).equals(CAT_SPECIAL)) {
            searchMode = SearchMode.special;
        }
        super.closeButton_actionPerformed(e);
        search();
    }

    private static boolean tryMatch(String s, Object o, boolean selected) {
        if (!selected) {
            return false;
        }
        if (o == null) {
            return false;
        }
        String f = o.toString().trim().toLowerCase();
        if (f.length() == 0) {
            return false;
        }
        if ( (o instanceof DataTypeDate && EfaUtil.countCharInString(s,'.') == 2) ||
             (o instanceof DataTypeTime && EfaUtil.countCharInString(s,':') == 1) ) {
            return (f.equals(s));
        }
        return (f.indexOf(s) >= 0);
    }

    private static void foundMatch(LogbookRecord r, IItemType item) {
        efaBaseFrame.setFields(r);
        item.requestFocus();
    }

    public static boolean search() {
        if (searchLogbookDialog == null || efaBaseFrame == null || logbook == null || it == null) {
            return false;
        }
        String s = searchLogbookDialog.sSearchText.getValue().trim().toLowerCase();
        if (searchMode == SearchMode.normal && s.length() == 0) {
            Dialog.error(International.getString("Bitte gib einen Suchbegriff ein!"));
            return false;
        }
        try {
            DataKey k;
            while (true) {
                k = it.getNext();
                if (k == null) {
                    if (JOptionPane.showConfirmDialog(efaBaseFrame, International.getString("Keinen Eintrag gefunden!") + ""
                            + "\n" + International.getString("Suche vom Anfang an fortsetzen?"),
                            International.getString("Nicht gefunden"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        k = it.getFirst();
                    } else {
                        break;
                    }
                }

                LogbookRecord r = logbook.getLogbookRecord(k);
                if (r == null) {
                    continue;
                }
                if (searchMode == SearchMode.normal) {
                    if (tryMatch(s, r.getEntryId(), searchLogbookDialog.sEntryno.getValue())) {
                        foundMatch(r, efaBaseFrame.entryno);
                        return true;
                    }
                    if (tryMatch(s, r.getDate(), searchLogbookDialog.sDate.getValue())) {
                        foundMatch(r, efaBaseFrame.date);
                        return true;
                    }
                    if (tryMatch(s, r.getEndDate(), searchLogbookDialog.sEnddate.getValue())) {
                        foundMatch(r, efaBaseFrame.enddate);
                        return true;
                    }
                    if (tryMatch(s, r.getBoatAsName(), searchLogbookDialog.sBoat.getValue())) {
                        foundMatch(r, efaBaseFrame.boat);
                        return true;
                    }
                    if (tryMatch(s, r.getCoxAsName(), searchLogbookDialog.sCox.getValue())) {
                        foundMatch(r, efaBaseFrame.cox);
                        return true;
                    }
                    for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
                        if (tryMatch(s, r.getCrewAsName(i + 1), searchLogbookDialog.sCrew.getValue())) {
                            foundMatch(r, efaBaseFrame.crew[i]);
                            return true;
                        }
                    }
                    if (tryMatch(s, r.getStartTime(), searchLogbookDialog.sStarttime.getValue())) {
                        foundMatch(r, efaBaseFrame.starttime);
                        return true;
                    }
                    if (tryMatch(s, r.getEndTime(), searchLogbookDialog.sEndtime.getValue())) {
                        foundMatch(r, efaBaseFrame.endtime);
                        return true;
                    }
                    if (tryMatch(s, r.getDestinationAndVariantName(), searchLogbookDialog.sDestination.getValue())) {
                        foundMatch(r, efaBaseFrame.destination);
                        return true;
                    }
                    if (tryMatch(s, r.getDistance(), searchLogbookDialog.sDistance.getValue())) {
                        foundMatch(r, efaBaseFrame.distance);
                        return true;
                    }
                    if (tryMatch(s, r.getComments(), searchLogbookDialog.sComments.getValue())) {
                        foundMatch(r, efaBaseFrame.comments);
                        return true;
                    }
                    if (tryMatch(s, Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, r.getSessionType()), searchLogbookDialog.sSessiontype.getValue())) {
                        foundMatch(r, efaBaseFrame.sessiontype);
                        return true;
                    }
                }
                if (searchMode == SearchMode.special) {
                    if (searchLogbookDialog.eIncomplete.getValue()) {
                        if (r.getBoatAsName() == null || r.getBoatAsName().length() == 0) {
                            foundMatch(r, efaBaseFrame.boat);
                            return true;
                        }
                        if (r.getDestinationAndVariantName() == null || r.getDestinationAndVariantName().length() == 0) {
                            foundMatch(r, efaBaseFrame.destination);
                            return true;
                        }
                        if (r.getDistance() == null || !r.getDistance().isSet()) {
                            foundMatch(r, efaBaseFrame.distance);
                            return true;
                        }
                        if (r.getAllCoxAndCrewAsNames().size() == 0) {
                            foundMatch(r, efaBaseFrame.crew[0]);
                            return true;
                        }
                    }
                    if (searchLogbookDialog.eUnknownBoat.getValue()) {
                        if (r.getBoatId() == null) {
                            foundMatch(r, efaBaseFrame.boat);
                            return true;
                        }
                    }
                    if (searchLogbookDialog.eUnknownPerson.getValue()) {
                        for (int i = 0; i < LogbookRecord.CREW_MAX; i++) {
                            if (r.getCrewId(i) == null && r.getCrewName(i) != null && r.getCrewName(i).length() > 0) {
                                if (searchLogbookDialog.eUnknownPersonIgnoreGuest.getValue() &&
                                    r.getCrewName(i).toLowerCase().indexOf(Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST).toLowerCase())  >= 0) {
                                    continue;
                                }
                                if (i == 0) {
                                    foundMatch(r, efaBaseFrame.cox);
                                } else {
                                    foundMatch(r, efaBaseFrame.crew[i-1]);
                                }
                                return true;
                            }
                        }
                    }
                    if (searchLogbookDialog.eUnknownDestination.getValue()) {
                        if (r.getDestinationId() == null) {
                            foundMatch(r, efaBaseFrame.destination);
                            return true;
                        }
                    }
                    if (searchLogbookDialog.eOpenEntry.getValue()) {
                        if (r.getDistance() != null && (!r.getDistance().isSet() || r.getDistance().getValueInMeters() == 0)) {
                            foundMatch(r, efaBaseFrame.distance);
                            return true;
                        }
                    }
                    if (searchLogbookDialog.eLargeDistance.getValue().getValueInMeters() > 0) {
                        if (r.getDistance() != null && r.getDistance().isSet() &&
                            r.getDistance().getValueInMeters() >= searchLogbookDialog.eLargeDistance.getValue().getValueInMeters()) {
                            foundMatch(r, efaBaseFrame.distance);
                            return true;
                        }
                    }
                }

            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }


}
