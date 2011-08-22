/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import de.nmichael.efa.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.gui.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.data.*;

public class ItemTypeBoatstatusList extends ItemTypeList {

    EfaBoathouseFrame efaBoathouseFrame;

    public ItemTypeBoatstatusList(String name,
            int type, String category, String description,
            EfaBoathouseFrame efaBoathouseFrame) {
        super(name, type, category, description);
        this.efaBoathouseFrame = efaBoathouseFrame;
    }

    public void setBoatStatusData(Vector<BoatStatusRecord> v, Logbook logbook, String other) {
        Vector<ItemTypeListData> vdata = sortBootsList(v, logbook);
        if (other != null) {
            vdata.add(0, new ItemTypeListData(other, null, false, -1));
        }
        clearIncrementalSearch();
        list.setSelectedIndex(-1);
        setItems(vdata);
        showValue();
    }

    Vector<ItemTypeListData> sortBootsList(Vector<BoatStatusRecord> v, Logbook logbook) {
        if (v == null || v.size() == 0) {
            return new Vector<ItemTypeListData>();
        }

        Boats boats = Daten.project.getBoats(false);
        long now = System.currentTimeMillis();

        BoatString[] a = new BoatString[v.size()];
        for (int i = 0; i < v.size(); i++) {
            BoatStatusRecord sr = v.get(i);
            a[i] = new BoatString();
            a[i].seats = 99;
            a[i].name = sr.getBoatText();
            a[i].sortBySeats = (Daten.efaConfig.getValueEfaDirekt_sortByAnzahl());
            a[i].record = sr;

            BoatRecord r = boats.getBoat(sr.getBoatId(), now);
            if (r != null) {
                int seats = r.getNumberOfSeats(0);
                if (seats == 0) {
                    seats = 99;
                }
                if (seats < 0) {
                    seats = 0;
                }
                if (seats > 99) {
                    seats = 99;
                }
                a[i].seats = seats;
                // for BoatsOnTheWater, don't use the "real" boat name, but rather what's stored in the boat status as "BoatText"
                a[i].name = (sr.getCurrentStatus().equals(BoatStatusRecord.STATUS_ONTHEWATER) ? sr.getBoatText() : r.getQualifiedName());
                a[i].sortBySeats = (Daten.efaConfig.getValueEfaDirekt_sortByAnzahl());

                if (Daten.efaConfig.getValueEfaDirekt_showZielnameFuerBooteUnterwegs() &&
                    BoatStatusRecord.STATUS_ONTHEWATER.equals(sr.getCurrentStatus()) &&
                    sr.getEntryNo() != null && sr.getEntryNo().length() > 0) {
                    LogbookRecord lr = logbook.getLogbookRecord(sr.getEntryNo());
                    if (lr != null) {
                        String dest = lr.getDestinationAndVariantName();
                        if (dest != null && dest.length() > 0) {
                            a[i].name += "     -> " + dest;
                        }
                    };
                }
            }
        }
        Arrays.sort(a);

        Vector<ItemTypeListData> vv = new Vector<ItemTypeListData>();
        int anz = -1;
        String lastSep = null;
        for (int i = 0; i < a.length; i++) {
            if (a[i].seats != anz) {
                String s = null;
                switch (a[i].seats) {
                    case 1:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_1);
                        break;
                    case 2:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_2);
                        break;
                    case 3:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_3);
                        break;
                    case 4:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_4);
                        break;
                    case 5:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_5);
                        break;
                    case 6:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_6);
                        break;
                    case 8:
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_8);
                        break;
                }
                if (s == null || s.equals(EfaTypes.getStringUnknown())) {
                    /* @todo (P4) Doppeleinträge currently not supported in efa2
                    DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(removeDoppeleintragFromBootsname(a[i].name));
                    if (d != null) {
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, d.get(Boote.ANZAHL));
                    } else {
                    */
                        s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, EfaTypes.TYPE_NUMSEATS_OTHER);
                    //}
                }
                anz = a[i].seats;
                String newSep = "---------- " + s + " ----------";
                if (!newSep.equals(lastSep)) {
                    vv.add(new ItemTypeListData(newSep, null, true, anz));
                }
                lastSep = newSep;
            }
            vv.add(new ItemTypeListData(a[i].name, a[i].record, false, -1));
        }
        return vv;
    }

    public void setPersonStatusData(Vector<PersonRecord> v, String other) {
        Vector<ItemTypeListData> vdata = sortMemberList(v);
        if (other != null) {
            vdata.add(0, new ItemTypeListData(other, null, false, -1));
        }
        clearIncrementalSearch();
        list.setSelectedIndex(-1);
        setItems(vdata);
        showValue();
    }

    Vector sortMemberList(Vector<PersonRecord> v) {
        if (v == null || v.size() == 0) {
            return v;
        }
        BoatString[] a = new BoatString[v.size()];
        for (int i = 0; i < v.size(); i++) {
            PersonRecord pr = v.get(i);
            a[i] = new BoatString();
            a[i].seats = 99;
            a[i].name = pr.getQualifiedName();
            a[i].sortBySeats = false;
            a[i].record = pr;
        }
        Arrays.sort(a);

        Vector<ItemTypeListData> vv = new Vector<ItemTypeListData>();
        char lastChar = ' ';
        for (int i = 0; i < a.length; i++) {
            String name = a[i].name;
            if (name.length() > 0) {
                if (name.toUpperCase().charAt(0) != lastChar) {
                    lastChar = name.toUpperCase().charAt(0);
                    vv.add(new ItemTypeListData("---------- " + lastChar + " ----------", null, true, 99));
                }
                vv.add(new ItemTypeListData(name, a[i].record, false, 99));
            }
        }
        return vv;
    }

    public BoatListItem getSelectedBoatListItem() {
        if (list == null || list.isSelectionEmpty()) {
            return null;
        } else {
            BoatListItem item = new BoatListItem();
            item.list = this;
            item.text = getSelectedText();
            Object o = getSelectedValue();
            if (o != null && o instanceof BoatStatusRecord) {
                item.boatStatus = (BoatStatusRecord)o;
            }
            if (o != null && o instanceof PersonRecord) {
                item.person = (PersonRecord)o;
            }
            return item;
        }
    }

    public class BoatListItem {
        public int mode;
        public ItemTypeBoatstatusList list;
        public String text;
        public BoatRecord boat;
        public BoatStatusRecord boatStatus;
        public PersonRecord person;
    }

    class BoatString implements Comparable {

        public String name;
        public int seats;
        public boolean sortBySeats;
        public Object record;

        private String normalizeString(String s) {
            if (s == null) {
                return "";
            }
            s = s.toLowerCase();
            if (s.indexOf("ä") >= 0) {
                s = EfaUtil.replace(s, "ä", "a", true);
            }
            if (s.indexOf("Ä") >= 0) {
                s = EfaUtil.replace(s, "Ä", "a", true);
            }
            if (s.indexOf("à") >= 0) {
                s = EfaUtil.replace(s, "à", "a", true);
            }
            if (s.indexOf("á") >= 0) {
                s = EfaUtil.replace(s, "á", "a", true);
            }
            if (s.indexOf("â") >= 0) {
                s = EfaUtil.replace(s, "â", "a", true);
            }
            if (s.indexOf("ã") >= 0) {
                s = EfaUtil.replace(s, "ã", "a", true);
            }
            if (s.indexOf("æ") >= 0) {
                s = EfaUtil.replace(s, "æ", "ae", true);
            }
            if (s.indexOf("ç") >= 0) {
                s = EfaUtil.replace(s, "ç", "c", true);
            }
            if (s.indexOf("è") >= 0) {
                s = EfaUtil.replace(s, "è", "e", true);
            }
            if (s.indexOf("é") >= 0) {
                s = EfaUtil.replace(s, "é", "e", true);
            }
            if (s.indexOf("è") >= 0) {
                s = EfaUtil.replace(s, "è", "e", true);
            }
            if (s.indexOf("é") >= 0) {
                s = EfaUtil.replace(s, "é", "e", true);
            }
            if (s.indexOf("ê") >= 0) {
                s = EfaUtil.replace(s, "ê", "e", true);
            }
            if (s.indexOf("ì") >= 0) {
                s = EfaUtil.replace(s, "ì", "i", true);
            }
            if (s.indexOf("í") >= 0) {
                s = EfaUtil.replace(s, "í", "i", true);
            }
            if (s.indexOf("î") >= 0) {
                s = EfaUtil.replace(s, "î", "i", true);
            }
            if (s.indexOf("ñ") >= 0) {
                s = EfaUtil.replace(s, "ñ", "n", true);
            }
            if (s.indexOf("ö") >= 0) {
                s = EfaUtil.replace(s, "ö", "o", true);
            }
            if (s.indexOf("Ö") >= 0) {
                s = EfaUtil.replace(s, "Ö", "o", true);
            }
            if (s.indexOf("ò") >= 0) {
                s = EfaUtil.replace(s, "ò", "o", true);
            }
            if (s.indexOf("ó") >= 0) {
                s = EfaUtil.replace(s, "ó", "o", true);
            }
            if (s.indexOf("ô") >= 0) {
                s = EfaUtil.replace(s, "ô", "o", true);
            }
            if (s.indexOf("õ") >= 0) {
                s = EfaUtil.replace(s, "õ", "o", true);
            }
            if (s.indexOf("ø") >= 0) {
                s = EfaUtil.replace(s, "ø", "o", true);
            }
            if (s.indexOf("ü") >= 0) {
                s = EfaUtil.replace(s, "ü", "u", true);
            }
            if (s.indexOf("Ü") >= 0) {
                s = EfaUtil.replace(s, "Ü", "u", true);
            }
            if (s.indexOf("ù") >= 0) {
                s = EfaUtil.replace(s, "ù", "u", true);
            }
            if (s.indexOf("ú") >= 0) {
                s = EfaUtil.replace(s, "ú", "u", true);
            }
            if (s.indexOf("û") >= 0) {
                s = EfaUtil.replace(s, "û", "u", true);
            }
            if (s.indexOf("ß") >= 0) {
                s = EfaUtil.replace(s, "ß", "ss", true);
            }
            return s;
        }

        public int compareTo(Object o) {
            BoatString other = (BoatString) o;
            String sThis = (sortBySeats ? (seats < 10 ? "0" : "") + seats : "") + normalizeString(name);
            String sOther = (sortBySeats ? (other.seats < 10 ? "0" : "") + other.seats : "") + normalizeString(other.name);
            return sThis.compareTo(sOther);
        }
    }


}
