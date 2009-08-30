package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import javax.swing.*;

public class Help {


  private static String[][] help = {
    { "AusgabeFrame" ,                            "AusgabeFrame.html" } ,
    { "AuswahlFrame" ,                            "AuswahlFrame.html" } ,
    { "BrowserFrame" ,                            "BrowserFrame.html" } ,
    { "DownloadFrame" ,                           "DownloadFrame.html" } ,
    { "EfaConfigFrame" ,                          "EfaConfigFrame.html" } ,
    { "EfaFrame" ,                                "EfaFrame.html" } ,
    { "EfaFrame_AboutBox" ,                       "EfaFrame_AboutBox.html" } ,
    { "EfaWettFertigFrame" ,                      "EfaWettFertigFrame.html" } ,
    { "EfaWettMeldungVervollstaendigenFrame" ,    "EfaWettMeldungVervollstaendigenFrame.html" } ,
    { "EfaWettSelectFrame" ,                      "EfaWettSelectFrame.html" } ,
    { "ExceptionFrame" ,                          "ExceptionFrame.html" } ,
    { "FahrtenbuchNeuFortsetzenFrame" ,           "FahrtenbuchNeuFortsetzenFrame.html" } ,
    { "ImportFrame" ,                             "ImportFrame.html" } ,
    { "KonsoleFrame" ,                            "KonsoleFrame.html" } ,
    { "ListenausgabeFrame" ,                      "ListenausgabeFrame.html" } ,
    { "MehrtagestourFrame" ,                      "MehrtagestourFrame.html" } ,
    { "NeuesBootFrame" ,                          "NeuesBootFrame.html" } ,
    { "NeuesFahrtenbuchFrame" ,                   "NeuesFahrtenbuchFrame.html" } ,
    { "NeuesMitgliedFrame" ,                      "NeuesMitgliedFrame.html" } ,
    { "NeuesZielFrame" ,                          "NeuesZielFrame.html" } ,
    { "RestoreBackupFrame" ,                      "RestoreBackupFrame.html" } ,
    { "StatAddFrame" ,                            "StatAddFrame.html" } ,
    { "StatistikErweitertFrame" ,                 "StatistikErweitertFrame.html" } ,
    { "StatistikFrame" ,                          "StatistikFrame.html" } ,
    { "SuchFrame" ,                               "SuchFrame.html" } ,
    { "SynonymFrame" ,                            "SynonymFrame.html" } ,
    { "VereinsConfigFrame" ,                      "VereinsConfigFrame.html" } ,
    { "WriteProtectFrame" ,                       "WriteProtectFrame.html" } ,
    { "EmilFrame" ,                               "EmilFrame.html" } ,
    { "EmilFrame_AboutBox" ,                      "EmilFrame_AboutBox.html" } ,
    { "EmilConfigFrame" ,                         "EmilConfigFrame.html" } ,
    { "ElwizFrame" ,                              "ElwizFrame.html" } ,
    { "ElwizAboutFrame" ,                         "ElwizAboutFrame.html" }
    };

  private static String getURL(String name) {
    String file = Daten.efaDocDirectory+name+".html";
    if (Daten.exceptionTest) {
      file = null;
      if (file.length() > 0) { EfaUtil.foo(); }
    }
    if (!EfaUtil.canOpenFile(file)) file = Daten.efaDocDirectory+"index.html";
    return "file:"+file;
  }

  public static void getHelp(JFrame frame, String name) {
    Dialog.neuBrowserDlg(frame,"Online-Hilfe",getURL(name));
  }
  public static void getHelp(JDialog frame, String name) {
    Dialog.neuBrowserDlg(frame,"Online-Hilfe",getURL(name));
  }

  public static void getHelp(JFrame frame, Class c) {
    String name = c.toString();
    if (name == null) return;
    if (name.lastIndexOf(".")>=0) name = name.substring(name.lastIndexOf(".")+1,name.length());
    Dialog.neuBrowserDlg(frame,"Online-Hilfe",getURL(name));
  }
  public static void getHelp(JDialog frame, Class c) {
    String name = c.toString();
    if (name == null) return;
    if (name.lastIndexOf(".")>=0) name = name.substring(name.lastIndexOf(".")+1,name.length());
    Dialog.neuBrowserDlg(frame,"Online-Hilfe",getURL(name));
  }
}