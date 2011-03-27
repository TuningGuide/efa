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

import de.nmichael.efa.util.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.util.Dialog;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete

public class ItemTypeDate extends ItemTypeLabelTextfield {

    private DataTypeDate value;
    private DataTypeDate referenceDate;
    protected boolean showWeekday;
    protected JLabel weekdayLabel;
    protected int weekdayGridWidth = 1;
    protected int weekdayGridAnchor = GridBagConstraints.WEST;
    protected int weekdayGridFill = GridBagConstraints.NONE;

    public ItemTypeDate(String name, DataTypeDate value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
        this.referenceDate = new DataTypeDate();
        this.referenceDate.today();
    }

    public void showWeekday(boolean showWeekday) {
        this.showWeekday = showWeekday;
    }

    public void parseValue(String value) {
        try {
            if (value != null && value.trim().length()>0) {
                if (referenceDate != null && referenceDate.isSet()) {
                    value = EfaUtil.tmj2datestring(EfaUtil.correctDate(value, referenceDate.getDay(), referenceDate.getMonth(), referenceDate.getYear()));
                } else {
                    value = EfaUtil.correctDate(value);
                }
            }
            this.value = DataTypeDate.parseDate(value);
        } catch (Exception e) {
            if (dlg == null) {
                Logger.log(Logger.ERROR, Logger.MSG_CORE_UNSUPPORTEDDATATYPE,
                           "Invalid value for parameter "+name+": "+value);
            }
        }
    }

    public int displayOnGui(Window dlg, JPanel panel, int x, int y) {
        int plusy = super.displayOnGui(dlg, panel, x, y);
        if (showWeekday) {
            weekdayLabel = new JLabel();
            panel.add(weekdayLabel, new GridBagConstraints(x+labelGridWidth+fieldGridWidth, y, weekdayGridWidth, 1, 0.0, 0.0,
                    weekdayGridAnchor, weekdayGridFill, new Insets(padYbefore, 0, padYafter, 0), 0, 0));
        }
        return plusy;
    }

    protected void field_focusLost(FocusEvent e) {
        super.field_focusLost(e);
        if (value.isSet()) {
            referenceDate.setDate(value);
        }
    }

    protected void updateWeekday() {
        if (showWeekday && weekdayLabel != null) {
            if (value.isSet()) {
                switch(value.toCalendar().get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.MONDAY:
                        weekdayLabel.setText(" (" + International.getString("Montag") + ")");
                        break;
                    case Calendar.TUESDAY:
                        weekdayLabel.setText(" (" + International.getString("Dienstag") + ")");
                        break;
                    case Calendar.WEDNESDAY:
                        weekdayLabel.setText(" (" + International.getString("Mittwoch") + ")");
                        break;
                    case Calendar.THURSDAY:
                        weekdayLabel.setText(" (" + International.getString("Donnerstag") + ")");
                        break;
                    case Calendar.FRIDAY:
                        weekdayLabel.setText(" (" + International.getString("Freitag") + ")");
                        break;
                    case Calendar.SATURDAY:
                        weekdayLabel.setText(" (" + International.getString("Samstag") + ")");
                        break;
                    case Calendar.SUNDAY:
                        weekdayLabel.setText(" (" + International.getString("Sonntag") + ")");
                        break;
                }
            } else {
                weekdayLabel.setText("");
            }
        }
    }

    public void showValue() {
        super.showValue();
        updateWeekday();
    }

    public String toString() {
        return value.toString();
    }

    public boolean isSet() {
        return value.isSet();
    }

    public int getValueDay() {
        return value.getDay();
    }

    public int getValueMonth() {
        return value.getMonth();
    }

    public int getValueYear() {
        return value.getYear();
    }

    public DataTypeDate getDate() {
        return new DataTypeDate(value.getDay(), value.getMonth(), value.getYear());
    }

    public void setValueDay(int day) {
        value.setDay(day);
    }

    public void setValueMonth(int month) {
        value.setMonth(month);
    }

    public void setValueYear(int year) {
        value.setYear(year);
    }

    public void unset() {
        value.unset();
    }

    public boolean isValidInput() {
        if (isNotNullSet()) {
            return isSet();
        }
        return true;
    }

    public void setWeekdayGrid(int gridWidth, int gridAnchor, int gridFill) {
        weekdayGridWidth = gridWidth;
        weekdayGridAnchor = gridAnchor;
        weekdayGridFill = gridFill;
    }

    // @override
    protected void field_keyPressed(KeyEvent e) {
        if (e != null && (value.isSet() || (referenceDate != null && referenceDate.isSet()))) {
            if (!value.isSet()) {
                value.setDate(referenceDate);
            }
            switch(e.getKeyCode()) {
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_ADD:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_KP_UP:
                    value.addDays(1);
                    showValue();
                    break;
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_SUBTRACT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_KP_DOWN:
                    value.addDays(-1);
                    showValue();
                    break;
            }
        }
        super.actionEvent(e);
    }
}
