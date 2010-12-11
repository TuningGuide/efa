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

import de.nmichael.efa.core.config.EfaConfig;

// @i18n complete

public class CustSettings {

    public boolean activateGermanRowingOptions = true;
    public boolean activateBerlinRowingOptions = true;
    public boolean activateRowingOptions = true;
    public boolean activateCanoeingOptions = false;

    public CustSettings() {
    }

    public CustSettings(EfaConfig efaConfig) {
        activateGermanRowingOptions = efaConfig.showGermanOptions.getValue();
        activateBerlinRowingOptions = efaConfig.showBerlinOptions.getValue();
        activateRowingOptions = efaConfig.useForRowing.getValue();
        activateCanoeingOptions = efaConfig.useForCanoeing.getValue();
    }


}
