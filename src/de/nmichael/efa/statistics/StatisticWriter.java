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
import de.nmichael.efa.util.Dialog;

public abstract class StatisticWriter {

    protected StatisticsRecord sr;
    protected StatisticsData[] sd;

    public StatisticWriter(StatisticsRecord sr, StatisticsData[] sd) {
        this.sd = sd;
        this.sr = sr;
    }

    public static StatisticWriter getWriter(StatisticsRecord sr, StatisticsData[] sd) {
        switch(sr.sOutputType) {
            case internal:
                return new StatisticInternalWriter(sr, sd);
            case html:
                return new StatisticHTMLWriter(sr, sd);
            case pdf:
                // @todo (P5) return new StatisticPDFWriter(sr, sd);
                Dialog.error("PDF output not yet implemented");
            case csv:
                return new StatisticCSVWriter(sr, sd);
            case xml:
                return new StatisticXMLWriter(sr, sd);
            case efawett:
                // @todo (P2) return new StatisticEfaWettWriter(sr, sd);
                Dialog.error("efaWett output not yet implemented");
        }
        return new StatisticInternalWriter(sr, sd);
    }

    public abstract boolean write();

}