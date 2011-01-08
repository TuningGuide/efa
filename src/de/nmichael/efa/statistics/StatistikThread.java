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

import de.nmichael.efa.util.SwingWorker;
import de.nmichael.efa.*;
import de.nmichael.efa.statistics.Statistik;

// @i18n complete

public class StatistikThread {

  StatistikDaten[] d;
  Thread thr;


  public StatistikThread() {
  }


  public Thread go(StatistikDaten[] d) {
    this.d = d;
    final SwingWorker worker = new SwingWorker() {
      public Object construct() {
        return new ActualTask();
      }
    };
    return (thr = worker.start());
  }


  public int getLengthOfTask() {
    return Statistik.progressLength;
  }


  public int getCurrent() {
    return Statistik.progressDone + Statistik.progressCurrent;
  }


  public void stop() {
    Statistik.abort = true;
  }


  public boolean done() {
   return (Statistik.progressCurrent == -2);
  }


  public String getMessage() {
    return Statistik.progressMessage;
  }


  public void exit() {
    thr.destroy();
  }


  class ActualTask {
    ActualTask () {
      Statistik.create(d);
    }
  }

}
