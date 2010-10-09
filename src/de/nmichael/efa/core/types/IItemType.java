/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.types;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Color;
import de.nmichael.efa.gui.BaseDialog;


// @i18n complete

public interface IItemType {

    // Parameter Types (*must* match those in de.nmichael.efa.core.config.EfaConfig!)
    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXPERT = 1;
    public static final int TYPE_PUBLIC = 2;

    public String getName();
    public int getType();
    public String getCategory();
    public String getDescription();
    public void parseValue(String value);
    public String toString();
    public boolean isValidInput();
    
    public int displayOnGui(BaseDialog dlg, JPanel panel, int y);
    public void getValueFromGui();
    public void requestFocus();

    public void setColor(Color c);
    public void setPadding(int padX, int padYbefore, int padYafter);

}
