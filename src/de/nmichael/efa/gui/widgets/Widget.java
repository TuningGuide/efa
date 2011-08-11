/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.widgets;

import de.nmichael.efa.core.items.*;
import de.nmichael.efa.util.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public abstract class Widget implements IWidget {

    public static final String PARAM_ENABLED        = "ENABLED";
    public static final String PARAM_POSITION       = "POSITION";
    public static final String PARAM_UPDATEINTERVAL = "UPDATEINTERVAL";

    String name;
    String description;
    Vector<IItemType> parameters = new Vector<IItemType>();
    JPanel myPanel;
    
    public Widget(String name, String description) {
        this.name = name;
        this.description = description;

        addParameterInternal(new ItemTypeBoolean(PARAM_ENABLED, false,
                IItemType.TYPE_PUBLIC, "",
                International.getMessage("{item} anzeigen", name)));

        addParameterInternal(new ItemTypeStringList(PARAM_POSITION, POSITION_BOTTOM,
                new String[] { POSITION_TOP, POSITION_BOTTOM, POSITION_LEFT, POSITION_RIGHT, POSITION_CENTER },
                new String[] { International.getString("oben"),
                               International.getString("unten"),
                               International.getString("links"),
                               International.getString("rechts"),
                               International.getString("mitte")
                },
                IItemType.TYPE_PUBLIC, "",
                International.getString("Position")));

        addParameterInternal(new ItemTypeInteger(PARAM_UPDATEINTERVAL, 3600, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("Aktualisierungsintervall")
                + " (s)"));
    }

    public String getParameterName(String internalName) {
        return "WIDGET_" + this.name + "_" + internalName;
    }

    void addParameterInternal(IItemType p) {
        p.setName(getParameterName(p.getName()));
        parameters.add(p);
    }

    IItemType getParameterInternal(String internalName) {
        String name = getParameterName(internalName);
        for (int i=0; i<parameters.size(); i++) {
            if (parameters.get(i).getName().equals(name)) {
                return parameters.get(i);
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public IItemType[] getParameters() {
        IItemType[] a = new IItemType[parameters.size()];
        for (int i=0; i<parameters.size(); i++) {
            a[i] = parameters.get(i);
        }
        return a;
    }

    public void setParameter(IItemType param) {
        for (int i=0; i<parameters.size(); i++) {
            IItemType p = parameters.get(i);
            if (p.getName().equals(param.getName())) {
                p.parseValue(param.toString());
            }
        }
    }

    public void setEnabled(boolean enabled) {
        ((ItemTypeBoolean)getParameterInternal(PARAM_ENABLED)).setValue(enabled);
    }
    public boolean isEnabled() {
        return ((ItemTypeBoolean)getParameterInternal(PARAM_ENABLED)).getValue();
    }

    public void setPosition(String p) {
        getParameterInternal(PARAM_POSITION).parseValue(p);
    }

    public String getPosition() {
        return getParameterInternal(PARAM_POSITION).toString();
    }

    public void setUpdateInterval(int seconds) {
        ((ItemTypeInteger)getParameterInternal(PARAM_UPDATEINTERVAL)).setValue(seconds);
    }

    public int getUpdateInterval() {
        return ((ItemTypeInteger)getParameterInternal(PARAM_UPDATEINTERVAL)).getValue();
    }

    abstract void construct();
    public abstract JComponent getComponent();

    public void show(JPanel panel, int x, int y) {
        myPanel = panel;
        construct();
        panel.add(getComponent(), new GridBagConstraints(x, y, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void show(JPanel panel, String orientation) {
        myPanel = panel;
        construct();
        panel.add(getComponent(), orientation);
    }

    public static String[] getAllWidgetClassNames() {
        return new String[] {
            HTMLWidget.class.getCanonicalName(),
            MeteoAstroWidget.class.getCanonicalName()
        };
    }

    public static Vector<IWidget> getAllWidgets() {
        String[] classNames = getAllWidgetClassNames();
        Vector<IWidget> widgets = new Vector<IWidget>();
        for (int i=0; i<classNames.length; i++) {
            try {
                IWidget w = (IWidget)Widget.class.forName(classNames[i]).newInstance();
                if (w != null) {
                    widgets.add(w);
                }
            } catch(Exception e) {
                Logger.logdebug(e);
            }
        }
        return widgets;
    }
}
