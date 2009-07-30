package de.nmichael.efa;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

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
