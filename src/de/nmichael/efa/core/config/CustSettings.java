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

    public boolean activateRowingOptions = true;
    public boolean activateGermanRowingOptions = true;
    public boolean activateBerlinRowingOptions = true;
    public boolean activateCanoeingOptions = false;
    public boolean activateGermanCanoeingOptions = false;

    public CustSettings() {
    }

    public CustSettings(EfaConfig efaConfig) {
        activateRowingOptions = efaConfig.useFunctionalityRowing.getValue();
        activateGermanRowingOptions = efaConfig.useFunctionalityRowingGermany.getValue();
        activateBerlinRowingOptions = efaConfig.useFunctionalityRowingBerlin.getValue();
        activateCanoeingOptions = efaConfig.useFunctionalityCanoeing.getValue();
        activateGermanCanoeingOptions = efaConfig.useFunctionalityCanoeingGermany.getValue();
    }


}
