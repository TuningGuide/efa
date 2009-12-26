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

// @i18n complete

public class EfaRunning {

  private static final int DEFAULT_PORT = 3834; // 3834 == EFA ;-)
  private static final String PING = "Bist Du efa?";
  private static final String PONG = "Ich bin efa!";

  private EfaRunningThread thread;

  public EfaRunning() {
  }

  private void trace(String s) {
    if (Daten.efaConfig != null && Daten.efaConfig.debugLogging) {
        Logger.log(Logger.DEBUG,Logger.MSG_DEBUG_EFARUNNING,"EfaRunning: "+s);
    }
  }


  // Als Server laufen: efa.run erzeugen und Client-Anfragen an Port beantworten
  public boolean run() {
    // Binde Socket an Port
    int port = DEFAULT_PORT;
    ServerSocket socket = null;
    while (socket == null && port < DEFAULT_PORT + 100) {
      try {
        trace("Trying to bind to port "+port+" ...");
        socket = new ServerSocket(port);
        trace("Server is now listening on port "+port+"!");
      } catch(IOException e) {
        trace("Error while trying to bind to port "+port+": "+e.toString());
        socket = null;
        port++; // nächsten Port versuchen
      } catch(SecurityException e) {
        trace("Error while trying to bind to port "+port+": "+e.toString());
        Logger.log(Logger.WARNING,Logger.MSG_WARN_EFARUNNING_FAILED,
                International.getString("efa konnte sich an keinen Port binden, um den Doppelstart-Verhinderer zu aktivieren:")+" "+e.toString());
        return false;
      }
    }
    if (socket == null) {
      Logger.log(Logger.WARNING,Logger.MSG_WARN_EFARUNNING_FAILED,
                International.getString("efa konnte sich an keinen Port binden, um den Doppelstart-Verhinderer zu aktivieren:")+" socket==null");
      return false;
    }
    trace("EfaRunning now listening on port "+port+".");

    // erfolgreich an Port gebunden!

    // Running-Info mit Port-Information erstellen
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG),Daten.ENCODING));
      f.write(Integer.toString(port) + " ("+EfaUtil.getCurrentTimeStamp()+")\n");
      f.close();
    } catch(Exception e) {
      Logger.log(Logger.ERROR,Logger.MSG_ERR_EFARUNNING_FAILED,
              LogString.logstring_fileCreationFailed(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG, International.getString("Datei"))+
              "  " + e.toString());
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
      trace("Found "+Daten.EFA_RUNNUNG+"!");
      BufferedReader f = null;
      try {
        f = new BufferedReader(new InputStreamReader(new FileInputStream(Daten.efaProgramDirectory+Daten.EFA_RUNNUNG),Daten.ENCODING));
        port = EfaUtil.string2date(f.readLine(),DEFAULT_PORT,0,0).tag;
        trace("Data read from port "+port+"!");
      } catch(Exception e) {
        EfaUtil.foo();
      } finally {
        try { f.close(); } catch(Exception ee) { f = null; }
      }
    } else return false;

    trace("Using port "+port+".");
    boolean running = false;
    Socket socket = null;
    try {
      socket = new Socket("localhost",port);
      try {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        trace("SENDING "+PING);
        out.writeUTF(PING);

        trace("Waiting for data ...");
        // warten, bis Daten vorliegen
        for (int i=0; in.available()==0 && i<5; i++) {
          try { Thread.sleep(1000); } catch(InterruptedException ee) {}
        }

        // wenn Daten vorliegen
        if (in.available()>0) {
          String data = in.readUTF();
          if (data != null && data.equals(PONG)) running = true;
          trace("RECEIVING "+data);
        }
      } catch(Exception e) {
        trace("Sending or receiving failed!");
      }
    } catch(Exception e) {
      trace("Connection to server failed!");
    } finally {
      try {
        trace("Closing socket ...");
        socket.close();
        trace("Socket closed!");
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
    trace("efa is "+(running ? "already" : "not yet")+" running.");
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
          trace("Waiting for connection ...");
          if (socket == null) return;
          Socket client = socket.accept();
          trace("Got a connection on port "+client.getPort());

          try {
            // Data Streams erstellen
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            trace("Waiting for data ...");
            // warten, bis Daten vorliegen
            for (int i=0; in.available()==0 && i<5; i++) {
              try { Thread.sleep(1000); } catch(InterruptedException ee) {}
            }

            // wenn Daten vorliegen
            if (in.available()>0) {
              trace("Got data!");
              String data = in.readUTF();
              trace("RECEIVED: "+data);
              if (data != null && data.equals(PING)) {
                // korrekten PING empfangen, sende PONG ...
                out.writeUTF(PONG);
                trace("SENDING: "+PONG);
              } else out.writeUTF("Nööö");
            }
          } catch(Exception e) {
            // Senden oder Empfangen der Daten fehlgeschlagen
            trace("Error while receiving or sending.");
          } finally {
            try {
              client.close();
              trace("Connection closed.");
            } catch(Exception e) { /* Schließen des Client Sockets fehlgeschlagen */ }
          }
        } catch(Exception e) {
          // Warten auf Client-Anfrage fehlgeschlagen
          if (socket == null) return; // passiert, wenn Socket beim Programmende geschlossen wird
          trace("Error waiting for connection.");
        }
      }
    }

    public boolean closeSocket() {
      try {
        trace("Closing server socket ...");
        socket.close();
        socket = null;
        trace("Server socket closed!");
        return true;
      } catch(Exception e) {
        trace("Error while closing server socket!");
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