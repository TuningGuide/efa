/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
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
        activateGermanRowingOptions = efaConfig.useFunctionalityRowingGermany.getValue();
        activateBerlinRowingOptions = efaConfig.useFunctionalityRowingBerlin.getValue();
        activateRowingOptions = efaConfig.useFunctionalityRowing.getValue();
        activateCanoeingOptions = efaConfig.useFunctionalityCanoeing.getValue();
    }


}
