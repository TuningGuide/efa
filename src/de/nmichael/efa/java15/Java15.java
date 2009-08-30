package de.nmichael.efa.java15;

import java.awt.Window;
import java.lang.management.*;
import javax.management.*;
import java.util.*;

class OOMEListener implements javax.management.NotificationListener {

  private de.nmichael.efa.direkt.EfaDirektFrame frame;

  public OOMEListener(de.nmichael.efa.direkt.EfaDirektFrame frame) {
    this.frame = frame;
  }

  public void handleNotification(Notification notification, Object handback)  {
    de.nmichael.efa.Daten.DONT_SAVE_ANY_FILES_DUE_TO_OOME = true;
    frame.exitOnLowMemory("OOMEListener:"+notification.getType(),true);
  }
}

public class Java15 {
  
  private static boolean wasRelativelyHighBefore = false;

  public static boolean setAlwaysOnTop(Window frame, boolean alwaysOnTop) {
    try {
      frame.setAlwaysOnTop(alwaysOnTop);
    } catch(NoSuchMethodError e) {
      return false;
    }
    return true;
  }
  
  public static boolean setEditorPaneAutoFormSubmissionFalse(javax.swing.JEditorPane editorPane) {
    try {
      MyPropertyChangeListener propertyChangeListener = new MyPropertyChangeListener();
      propertyChangeListener.setEditorPane(editorPane);
      editorPane.addPropertyChangeListener("editorKit",propertyChangeListener);
    } catch(NoSuchMethodError e) {
      return false;
    }
    return true;
  }

  public static boolean editorPaneHandlePostEvent(javax.swing.JEditorPane editorPane, javax.swing.event.HyperlinkEvent event) {
    try {
      javax.swing.text.html.FormSubmitEvent fevent = (javax.swing.text.html.FormSubmitEvent)event;


      java.net.HttpURLConnection conn = (java.net.HttpURLConnection)(fevent.getURL()).openConnection();
      conn.setRequestMethod("POST");
      conn.setAllowUserInteraction(false);
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
      conn.setRequestProperty("Content-length",Integer.toString(fevent.getData().length()));
      java.io.DataOutputStream out = new java.io.DataOutputStream(conn.getOutputStream ());
      out.writeBytes(fevent.getData());
      out.flush();
      out.close();
      conn.disconnect();
    } catch (Exception e) {
      return false;
    } catch(NoSuchMethodError e) {
      return false;
    }
    return true;
  }
  
  private static String printMemUsage(MemoryUsage usage) {
    if (usage != null) {
      return usage.getUsed()+"/"+usage.getMax()+"("+(usage.getUsed()*100/usage.getMax())+"%)";
    } else return "null";
  }

  public static boolean isMemoryLow(int percentageHigh, int percentageRelativelyHigh) {
    boolean memoryLow = false;
    try {
    try {
      List pools = ManagementFactory.getMemoryPoolMXBeans();
      for (int i=0; i<pools.size(); i++) {
        MemoryPoolMXBean pool = (MemoryPoolMXBean)pools.get(i);
                    de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"MemorySupervisor: Memory Pool: "+pool.getName());
                    de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"MemorySupervisor:   Current Usage      : "+printMemUsage(pool.getUsage()));
                    de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"MemorySupervisor:   Usage after last GC: "+printMemUsage(pool.getCollectionUsage()));
                    de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"MemorySupervisor:   Peak Usage         : "+printMemUsage(pool.getPeakUsage()));
      
        if (pool.getName().toLowerCase().indexOf("tenured") >= 0 ||
            pool.getName().toLowerCase().indexOf("old") >= 0) {
          MemoryUsage usage = pool.getCollectionUsage();
	  if (usage.getMax() > 0 && (usage.getUsed()*100)/usage.getMax() >= percentageHigh) {
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor: Memory Pool: "+pool.getName());
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor:   Current Usage      : "+printMemUsage(pool.getUsage()));
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor:   Usage after last GC: "+printMemUsage(pool.getCollectionUsage()));
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor:   Peak Usage         : "+printMemUsage(pool.getPeakUsage()));
	    memoryLow = true;
	    wasRelativelyHighBefore = true;
	  } else if (usage.getMax() > 0 && (usage.getUsed()*100)/usage.getMax() >= percentageRelativelyHigh && !wasRelativelyHighBefore) {
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor: Der aktuelle Speicherverbrauch ist relativ hoch.");
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor: Memory Pool: "+pool.getName());
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor:   Current Usage      : "+printMemUsage(pool.getUsage()));
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor:   Usage after last GC: "+printMemUsage(pool.getCollectionUsage()));
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor:   Peak Usage         : "+printMemUsage(pool.getPeakUsage()));
                            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.WARNING,"MemorySupervisor: Der efa zur Verfügung stehende Arbeitsspeicher kann durch eine Konfigurationsdatei hochgesetzt werden (siehe efa-FAQ).");
	    wasRelativelyHighBefore = true;
	  } else {
	    if (usage.getMax() > 0 && (usage.getUsed()*100)/usage.getMax() < percentageRelativelyHigh && wasRelativelyHighBefore) {
                                de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.INFO,"MemorySupervisor: Der aktuelle Speicherverbrauch ist wieder im unkritischen Bereich.");
                                de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.INFO,"MemorySupervisor: Memory Pool: "+pool.getName());
                                de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.INFO,"MemorySupervisor:   Current Usage      : "+printMemUsage(pool.getUsage()));
                                de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.INFO,"MemorySupervisor:   Usage after last GC: "+printMemUsage(pool.getCollectionUsage()));
                                de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.INFO,"MemorySupervisor:   Peak Usage         : "+printMemUsage(pool.getPeakUsage()));
  	      wasRelativelyHighBefore = false;
	    }
	  }
  	}
      }
    } catch(Exception e) {
                de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"Konnte Speicherverbrauch nicht ermitteln: "+e.toString());
    }
    } catch(NoClassDefFoundError ee) {
            de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"Konnte Speicherverbrauch nicht ermitteln: "+ee.toString()+" (Funktion erst ab Java 1.5 unterstützt)");
    }
    return memoryLow;
  }
  
  public static void setMemUsageListener(de.nmichael.efa.direkt.EfaDirektFrame frame, long threshold) {
    MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    NotificationEmitter emitter = (NotificationEmitter) mbean;
    OOMEListener listener = new OOMEListener(frame);
    emitter.addNotificationListener(listener, null, null);

    List pools = ManagementFactory.getMemoryPoolMXBeans();
    for (int i=0; i<pools.size(); i++) {
      MemoryPoolMXBean pool = (MemoryPoolMXBean)pools.get(i);
      if (pool.getName().toLowerCase().indexOf("tenured") >=0 ||
          pool.getName().toLowerCase().indexOf("old") >= 0) {
        if (pool.isCollectionUsageThresholdSupported()) {
          long thr = pool.getUsage().getMax() * threshold / 100;
                    de.nmichael.efa.util.Logger.log(de.nmichael.efa.util.Logger.DEBUG,"Setting CollectionUsageThreshold of pool "+pool.getName()+" to "+thr+" ...");
          pool.setCollectionUsageThreshold(thr);
        }
      }
    }
  }

}
