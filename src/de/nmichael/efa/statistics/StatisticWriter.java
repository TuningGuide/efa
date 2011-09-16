/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

import de.nmichael.efa.data.*;

public abstract class StatisticWriter {

    protected StatisticsRecord sr;
    protected StatisticsData[] sd;

    public StatisticWriter(StatisticsRecord sr, StatisticsData[] sd) {
        this.sd = sd;
        this.sr = sr;
    }

    public abstract boolean write();

}