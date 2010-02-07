/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.config;

import javax.swing.JDialog;
import javax.swing.JPanel;

// @i18n complete

public interface IConfigValue {

    public String getName();
    public int getType();
    public String getCategory();
    public String getDescription();
    public void parseValue(String value);
    public String toString();
    
    public int displayOnGui(JDialog dlg, JPanel panel, int y);
    public void getValueFromGui();

}
