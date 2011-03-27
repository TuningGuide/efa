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

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Window;
import java.awt.AWTEvent;
import de.nmichael.efa.gui.BaseDialog;


// @i18n complete

public interface IItemType {

    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXPERT = 1;
    public static final int TYPE_PUBLIC = 2;

    public String getName();
    public int getType();
    public String getCategory();
    public String getDescription();

    public void parseValue(String value);
    public String toString();
    public void getValueFromGui();
    public void parseAndShowValue(String value);
    public void showValue();
    public String getValueFromField();
    
    public int displayOnGui(Window dlg, JPanel panel, int y);
    public int displayOnGui(Window dlg, JPanel panel, int x, int y);

    public void requestFocus();

    public void setColor(Color c);
    public void setBackgroundColor(Color c);
    public void saveBackgroundColor();
    public void restoreBackgroundColor();
    public void setVisible(boolean visible);
    public void setEnabled(boolean enabled);

    public void setPadding(int padXbefore, int padXafter, int padYbefore, int padYafter);
    public void setFieldSize(int width, int height);
    public void setFieldGrid(int gridWidth, int gridAnchor, int gridFill);
    public void setFieldGrid(int gridWidth, int gridHeight, int gridAnchor, int gridFill);

    public boolean isValidInput();
    public void setNotNull(boolean notNull);
    public boolean isNotNullSet();

    public void setUnchanged();
    public boolean isChanged();

    public void registerItemListener(IItemListener listener);
    public void actionEvent(AWTEvent e);


}
