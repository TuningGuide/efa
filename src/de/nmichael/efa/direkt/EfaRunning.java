/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.util.*;
import java.io.*;
import java.net.*;
import de.nmichael.efa.*;

public class EfaRunning {

  private static final int DEFAULT_PORT = 3834; // 3834 == EFA ;-)
  private static final String PING = "Bist Du efa?";
  private static final String PONG = "Ich bin efa!";

  private EfaRunningThread thread;

  public EfaRunning() {
  }

  private void trace(String s) {
    if (Daten.efaConfig != null && Daten.efaConfig.debugLogging) Logger.log(Logger.DEBUG,"EfaRunning: "+s);
  }


  // Als Server laufen: efa.run erzeugen und Client-Anfragen an Port beantworten
  public boolean run() {
    // Binde Socket an Port
    int port = DEFAULT_PORT;
    ServerSocket socket = null;
    while (socket == null && port < DEFAULT_PORT + 100) {
      try {
        trace("Versuche, Server an Port "+port+" zu binden ...");
        socket = new ServerSocket(port);
        trace("Server an Port "+port+" gebunden!");
      } catch(IOException e) {
        trace("Fehler beim Versuch, an Port "+port+" zu binden: "+e.toString());
        socket = null;
        port++; // nächsten Port versuchen
      } catch(SecurityException e) {
        trace("Fehler beim Versuch, an Port "+port+" zu binden: "+e.toString());
        Logger.log(Logger.WARNING,"efa konnte sich an keinen Port binden (SecurityException), um den Doppelstart-Verhinderer zu aktivieren!");
        return false;
      }
    }
    if (socket == null) {
      Logger.log(Logger.WARNING,"efa konnte sich an keinen Port binden, um den Doppelstart-Verhinderer zu aktivieren!");
      return false;
    }
    trace("Doppelstart-Verhinderer lauscht jetzt an Port "+port+".");

    // erfolgreich an Port gebunden!

    // Running-Info mit Port-Information erstellen
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG),Daten.ENCODING));
      f.write(Integer.toString(port) + " ("+EfaUtil.getCurrentTimeStamp()+")\n");
      f.close();
    } catch(Exception e) {
      Logger.log(Logger.ERROR,"Datei '"+Daten.efaProgramDirectory+Daten.EFA_RUNNUNG+"' konnte nicht erstellt werden: "+e.toString());
    }

    // Thread starten
    thread = new EfaRunningThread(socket);
    thread.start();
    return true;
  }



  // Server Port schließen (der Thread lebt trotzdem weiter!)
  public boolean closeServer() {
    if (thread == null) return true;
    try {
      thread.closeSocket();
      if (!new File(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG).delete()) return false;
    } catch(Exception e) { return false; }
    return true;
  }


  private boolean checkIfRunning() {
    int port = DEFAULT_PORT;
    if (EfaUtil.canOpenFile(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG)) {
      trace("efa.run gefunden!");
      BufferedReader f = null;
      try {
        f = new BufferedReader(new InputStreamReader(new FileInputStream(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG),Daten.ENCODING));
        port = EfaUtil.string2date(f.readLine(),DEFAULT_PORT,0,0).tag;
        trace("Port "+port+" gelesen!");
      } catch(Exception e) {
        EfaUtil.foo();
      } finally {
        try { f.close(); } catch(Exception ee) { f = null; }
      }
    } else return false;

    trace("Benutze Port "+port);
    boolean running = false;
    Socket socket = null;
    try {
      socket = new Socket("localhost",port);
      try {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        trace("SENDE "+PING);
        out.writeUTF(PING);

        trace("Warte, bis Daten anliegen ...");
        // warten, bis Daten vorliegen
        for (int i=0; in.available()==0 && i<5; i++) {
          try { Thread.sleep(1000); } catch(InterruptedException ee) {}
        }

        // wenn Daten vorliegen
        if (in.available()>0) {
          String data = in.readUTF();
          if (data != null && data.equals(PONG)) running = true;
          trace("EMPFANGE "+data);
        }
      } catch(Exception e) {
        trace("Senden oder Empfangen fehlgeschlagen");
      }
    } catch(Exception e) {
      trace("Verbindung zu Server fehlgeschlagen");
    } finally {
      try {
        trace("Schließe Socket ...");
        socket.close();
        trace("Socket geschlossen!");
      } catch(Exception e) {}
    }

    trace("RUNNING = "+running);
    return running;
  }

  // testen (als Client), ob efa läuft oder nicht
  public boolean isRunning() {
    boolean running = false;
    running = checkIfRunning(); // 1st try
    if (!running) {
      try {
        Thread.sleep(500);
      }
      catch (Exception e) {
      }
      running = checkIfRunning(); // 2nd try
    }
    trace("Doppelstart-Verhinderer: efa läuft "+(running ? "bereits" : "nicht")+".");
    return running;
  }




  class EfaRunningThread extends Thread {
    ServerSocket socket;

    public EfaRunningThread(ServerSocket socket) {
      this.socket = socket;
    }

    public void run() {
      while (true) {
        try {
          // Auf Verbindung warten
          trace("Warte auf Verbindung...");
          if (socket == null) return;
          Socket client = socket.accept();
          trace("Verbindung auf Socket "+client.getPort());

          try {
            // Data Streams erstellen
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            trace("Warte, bis Daten anliegen ...");
            // warten, bis Daten vorliegen
            for (int i=0; in.available()==0 && i<5; i++) {
              try { Thread.sleep(1000); } catch(InterruptedException ee) {}
            }

            // wenn Daten vorliegen
            if (in.available()>0) {
              trace("Daten sind da!");
              String data = in.readUTF();
              trace("EMPFANGEN: "+data);
              if (data != null && data.equals(PING)) {
                // korrekten PING empfangen, sende PONG ...
                out.writeUTF(PONG);
                trace("SENDE: "+PONG);
              } else out.writeUTF("Nööö");
            }
          } catch(Exception e) {
            // Senden oder Empfangen der Daten fehlgeschlagen
            trace("Fehler beim Senden oder Empfangen");
          } finally {
            try {
              client.close();
              trace("Verbindung geschlossen");
            } catch(Exception e) { /* Schließen des Client Sockets fehlgeschlagen */ }
          }
        } catch(Exception e) {
          // Warten auf Client-Anfrage fehlgeschlagen
          if (socket == null) return; // passiert, wenn Socket beim Programmende geschlossen wird
          trace("Fehler beim Warten auf Verbindung");
        }
      }
    }

    public boolean closeSocket() {
      try {
        trace("Schließe Server Socket ...");
        socket.close();
        socket = null;
        trace("Server Socket geschlossen!");
        return true;
      } catch(Exception e) {
        trace("Fehler beim Schließen des Server Sockets!");
        return false;
      }
    }

  }


/*

  // nur zu Testzwecken
  public static void main(String[] args) {
    Daten.efaProgramDirectory = "./";
    EfaRunning r = new EfaRunning();

    if (args[0].equals("SERVER")) {
      r.run();
      try { Thread.sleep(30000); } catch(Exception e) {}
      r.closeServer();
    } else {
      r.isRunning(args[0]);
    }
  }
*/
}