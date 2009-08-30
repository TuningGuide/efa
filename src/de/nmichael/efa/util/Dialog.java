package de.nmichael.efa.util;

import de.nmichael.efa.core.StatistikFrame;
import de.nmichael.efa.core.ExceptionFrame;
import de.nmichael.efa.core.Main;
import de.nmichael.efa.core.AusgabeFrame;
import de.nmichael.efa.core.BrowserFrame;
import de.nmichael.efa.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.File;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class Dialog {

  public static final int INVALID = 0;
  public static final int YES = 1;
  public static final int NO = 2;
  public static final int CANCEL = 3;
  public static final int WRITE_IGNORE = 0;
  public static final int WRITE_REMOVE = 1;


  public static Stack frameStack=null;
  public static StatistikFrame statistikFrame=null;
  public static TextField appletOut = null;
  public static JTable programOut = null;
  public static JTextArea programOutText = null;
  public static AusgabeFrame programDlg = null;
  public static ProgressMonitor progress = null;

  public static Dimension screenSize = new Dimension(1024,768); // nur Default-Values..... ;-)

  public static boolean tourRunning = false;

  private static int FONT_SIZE = -1;
  private static int ORG_FONT_SIZE = 12;
  private static boolean FONT_SIZE_CHANGED = false;
  private static int FONT_STYLE = -1;
  private static int ORG_FONT_STYLE = -1;

  private static int MAX_DIALOG_WIDTH = 200; // Number of Characters
  private static int MAX_DIALOG_HEIGHT = 50; // Number of Lines

  public static void initializeScreenSize() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if ("Linux".equals(System.getProperty("os.name"))) {
      // Workaround für Linux: Fenster verschwinden oder werden falsch positioniert, wenn die die
      // volle Bildschirmgröße haben
       Dialog.screenSize = new Dimension(screenSize.width-1, screenSize.height-1);
    } else {
      Dialog.screenSize = screenSize;
    }
    initializeMaxDialogSizes();
  }

  public static void setMaxDialogSizes(int width, int height) {
    if (width>0) MAX_DIALOG_WIDTH = width;
    if (height>0) MAX_DIALOG_HEIGHT = height;
  }

  public static void initializeMaxDialogSizes() {
    if (Dialog.screenSize == null) return; // should never happen
    int fontSize = (FONT_SIZE>0 ? FONT_SIZE : 12);
    MAX_DIALOG_WIDTH  = (int)(Dialog.screenSize.width  / (fontSize*0.7));
    MAX_DIALOG_HEIGHT = (int)(Dialog.screenSize.height / (fontSize*1.6)) - 5;
  }


  public static int DateiErstellen(String dat) {
    switch (Dialog.yesNoDialog("Fehler","Datei '"+dat+"' nicht gefunden!\n"+
            "Soll die Datei neu erstellt werden?")) {
      case Dialog.YES: return YES;
      case Dialog.NO: return NO;
      default: return INVALID;
    }
  }

  public static void neuProgramDlg(JDialog parent) {
    if (parent != null) programDlg = new AusgabeFrame(parent);
    else programDlg = new AusgabeFrame(); // Parameter-Aufruf aus Efa
    Dimension dlgSize = programDlg.getPreferredSize();
    if (Daten.applID == Daten.APPL_EFADIREKT &&
            Daten.efaConfig != null && Daten.efaConfig.efaDirekt_startMaximized) {
          int width = (int)screenSize.getWidth();
          int height = (int)screenSize.getHeight();
          programDlg.setSize(width, height);
    }
    else {
        programDlg.setSize(dlgSize);
    }
    Dialog.setDlgLocation(programDlg,parent);
    programDlg.setModal(!Dialog.tourRunning);
  }

  private static void openBrowser(BrowserFrame browserDlg, String title, String url, int width, int height, int px, int py, boolean modal) {
    browserDlg.setSize(width,height);
    if (px<0 || py<0) {
      setDlgLocation(browserDlg);
    } else {
      browserDlg.setLocation(px,py);
    }
    browserDlg.setModal(modal);
    try {
      browserDlg.show();
    } catch(Exception e) { /* Java HTML Rendering Exceptions */ }
  }

  // internen Browser starten
  public static String neuBrowserDlg(JDialog parent, String title, String url) {
    return neuBrowserDlg(parent,title,url,0);
  }
  public static String neuBrowserDlg(JDialog parent, String title, String url, int closingTimeout) {
    BrowserFrame browser;
    boolean modal = !(title != null && title.equals("Online-Hilfe"));
    if (parent != null) browser = new BrowserFrame(parent, title, url, !modal);
    else browser = new BrowserFrame(title, url);
    browser.setClosingTimeout(closingTimeout);
    int width = (int)screenSize.getWidth()-100;
    int height = (int)screenSize.getHeight()-150;
    if (Daten.applID == Daten.APPL_EFADIREKT &&
        Daten.efaConfig != null && Daten.efaConfig.efaDirekt_startMaximized) {
      width = (int)screenSize.getWidth();
      height = (int)screenSize.getHeight();
    }
    openBrowser(browser,title,url,width,height,-1,-1,modal);
    return ( browser.out.getPage() != null ? browser.out.getPage().toString() : null);
  }
  public static String neuBrowserDlg(JFrame parent, String title, String url) {
    boolean modal = !(title != null && title.equals("Online-Hilfe"));
    BrowserFrame browser = new BrowserFrame(parent, title, url, !modal);
    int width = (int)screenSize.getWidth()-100;
    int height = (int)screenSize.getHeight()-150;
    if (Daten.applID == Daten.APPL_EFADIREKT &&
        Daten.efaConfig != null && Daten.efaConfig.efaDirekt_startMaximized) {
      width = (int)screenSize.getWidth();
      height = (int)screenSize.getHeight();
    }
    openBrowser(browser,title,url,width,height,-1,-1,modal);
    return ( browser.out.getPage() != null ? browser.out.getPage().toString() : null);
  }
  public static String neuBrowserDlg(JDialog parent, String title, String url, int width, int height, int px, int py) {
    BrowserFrame browser = new BrowserFrame(parent, title, url);
    openBrowser(browser,title,url,width,height,px,py,true);
    return ( browser.out.getPage() != null ? browser.out.getPage().toString() : null);
  }
  public static String neuBrowserDlg(JFrame parent, String title, String url, int width, int height, int px, int py) {
    BrowserFrame browser = new BrowserFrame(parent, title, url, false);
    openBrowser(browser,title,url,width,height,px,py,true);
    return ( browser.out.getPage() != null ? browser.out.getPage().toString() : null);
  }

  public static void startTour(JFrame parent) {
    String title = "efa Tour";
    String url = "file:"+Daten.efaProgramDirectory+"html"+Daten.fileSep+"tour.html";
    tourRunning = true;
    openBrowser(new BrowserFrame(parent, title, url, true),title,url,220,740,0,0,false);
  }

  // einen externen Browser starten; wurde keiner konfiguriert, wird der interne verwendet
  public static void startBrowser(JDialog parent, String url) {
    if (!Daten.efaConfig.browser.equals("") && !Daten.efaConfig.browser.equals("INTERN"))
      try {
        Runtime.getRuntime().exec(Daten.efaConfig.browser+" "+url);
      } catch(Exception ee) {
        Logger.log(Logger.ERROR,"Kann '"+Daten.efaConfig.browser+"' nicht starten!\nInterner Browser wird geöffnet.");
        neuBrowserDlg(parent,"Browser",url);
      }
    else neuBrowserDlg(parent,"Browser",url);
  }
  public static void startBrowser(JFrame parent, String url) {
    if (!Daten.efaConfig.browser.equals("") && !Daten.efaConfig.browser.equals("INTERN"))
      try {
        Runtime.getRuntime().exec(Daten.efaConfig.browser+" "+url);
      } catch(Exception ee) {
        Logger.log(Logger.ERROR,"Kann '"+Daten.efaConfig.browser+"' nicht starten!\nInterner Browser wird geöffnet.");
        neuBrowserDlg(parent,"Browser",url);
      }
    else neuBrowserDlg(parent,"Browser",url);
  }

  public static void error(String s) {
    if (!Main.cmdmode) {
      Dialog.infoDialog("Fehler",s);
    } else {
      System.out.println("ERROR: "+s);
    }
  }

  public static void meldung(String title, String s) {
    if (!Main.cmdmode) {
      Dialog.infoDialog(title,s);
    } else {
      System.out.println("INFO: "+s);
    }
  }

  public static void exceptionError(String error, String stacktrace) {
    int px=-1,py=-1;
    Window w = frameCurrent();
    ExceptionFrame dlg;
    if (w != null && w.getClass().getSuperclass().isInstance(new JFrame())) dlg = new ExceptionFrame((JFrame)w,error,stacktrace);
    else if (w != null && w.getClass().getSuperclass().isInstance(new JDialog())) dlg = new ExceptionFrame((JDialog)w,error,stacktrace);
    else dlg = new ExceptionFrame(error,stacktrace);
    Dimension dlgSize = dlg.getPreferredSize();
    int width = (int)dlgSize.getWidth()+50;
    int height= (int)dlgSize.getHeight()+50;
    dlg.setSize(width,height);
    Dialog.setDlgLocation(dlg);
    dlg.setModal(false);
    dlg.show();
    dlg.toFront();

  }

  public static String chopDialogString(String s) {
    if (s == null) return s;
    if (MAX_DIALOG_WIDTH  < 50) MAX_DIALOG_WIDTH  = 50;
    if (MAX_DIALOG_HEIGHT < 10) MAX_DIALOG_HEIGHT = 10;

    try {
      int lines = 1;
      int chars = 0;
      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) != '\n') {
          if (++chars > MAX_DIALOG_WIDTH) {
            int splitAt = -1;
            for (int j = i; j > i - ( (int) MAX_DIALOG_WIDTH / 2); j--) {
              if (s.charAt(j) == ' ') {
                splitAt = j;
                break;
              }
            }
            if (splitAt < 0) {
              splitAt = i;
            }
            s = s.substring(0, splitAt) + "\n" +
                s.substring(splitAt + (s.charAt(splitAt) == ' ' ? 1 : 0));
            i = splitAt;
            lines++;
            chars = 0;
          }
        }
        else {
          lines++;
          chars = 0;
        }
      }
      if (lines > MAX_DIALOG_HEIGHT) {
        Vector _s = EfaUtil.split(s,'\n');
        s = "";
        for (int i=0; i<(int)(MAX_DIALOG_HEIGHT/2); i++) s += (String)_s.get(i) + "\n";
        s += "...\n";
        int remaining = MAX_DIALOG_HEIGHT - ( (int)(MAX_DIALOG_HEIGHT/2) + 1 );
        for (int i=_s.size() - remaining; i<_s.size(); i++) s += (String)_s.get(i) + (i+1 < _s.size() ? "\n" : "");
      }
    } catch(Exception e) {
    }
    return s;
  }

  public static void meldung(String s) {
    Dialog.infoDialog("Information",s);
  }

  public static int yesNoDialog(String title, String s) {
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    if (JOptionPane.showConfirmDialog(frame,chopDialogString(s),title,JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      return YES;
    else return NO;
  }
  public static int yesNoCancelDialog(String title, String s) {
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    switch (JOptionPane.showConfirmDialog(frame,chopDialogString(s),title,JOptionPane.YES_NO_CANCEL_OPTION)) {
      case JOptionPane.YES_OPTION: return YES;
      case JOptionPane.NO_OPTION:  return NO;
      default: return CANCEL;
    }
  }

  public static int auswahlDialog(String title, String s, String option1, String option2, String option3) {
    Object[] auswahl = new String[ (option3 != null ? 3 : 2)];
    auswahl[0] = option1;
    auswahl[1] = option2;
    if (option3 != null) auswahl[2] = option3;
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    return JOptionPane.showOptionDialog(frame,chopDialogString(s),title,0,JOptionPane.QUESTION_MESSAGE,null,auswahl,option1);
  }
  public static int auswahlDialog(String title, String s, String option1, String option2, String option3, String option4) {
    Object[] auswahl = new String[ (option4 != null ? 4 : 3)];
    auswahl[0] = option1;
    auswahl[1] = option2;
    auswahl[2] = option3;
    if (option4 != null) auswahl[3] = option4;
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    return JOptionPane.showOptionDialog(frame,chopDialogString(s),title,0,JOptionPane.QUESTION_MESSAGE,null,auswahl,option1);
  }
  public static int auswahlDialog(String title, String s, String option1, String option2, String option3, String option4, String option5) {
    Object[] auswahl = new String[5];
    auswahl[0] = option1;
    auswahl[1] = option2;
    auswahl[2] = option3;
    auswahl[3] = option4;
    auswahl[4] = option5;
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    return JOptionPane.showOptionDialog(frame,chopDialogString(s),title,0,JOptionPane.QUESTION_MESSAGE,null,auswahl,option1);
  }
  public static int auswahlDialog(String title, String s, String option1, String option2) {
    return auswahlDialog(title,s,option1,option2,"Abbruch");
  }
  public static int auswahlDialog(String title, String s, String option1, String option2, boolean abbrButton) {
    return auswahlDialog(title,s,option1,option2,(abbrButton ? "Abbruch" : null));
  }


  public static boolean okAbbrDialog(String title, String s) {
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    return JOptionPane.showConfirmDialog(frame,chopDialogString(s),title,JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
  }

  public static void infoDialog(String title, String s) {
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    JOptionPane.showConfirmDialog(frame,chopDialogString(s),title,-1);
  }
  public static void infoDialog(String s) {
    Dialog.infoDialog("Information",s);
  }

  public static String inputDialog(String title, String s) {
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    return JOptionPane.showInputDialog(frame,chopDialogString(s),title,JOptionPane.PLAIN_MESSAGE);
  }
  public static String inputDialog(String title, String s, String vorbelegung) {
    Window frame = frameCurrent();
    if (frame != null && !frame.isEnabled()) frame.setEnabled(true);
    return (String)JOptionPane.showInputDialog(frame,chopDialogString(s),title,JOptionPane.PLAIN_MESSAGE,null,null,vorbelegung);
  }


  // Fragen, ob Schreibschutz aufgehoben werden soll
  public static int removeWriteProtection(String datei, boolean beimKonvertieren) {
    Object[] auswahl = new String[3];
    auswahl[0] = "Schreibschutz übergehen";
    auswahl[1] = "Schreibschutz entfernen";
    auswahl[2] = "Abbruch";
    if (!beimKonvertieren)
      return JOptionPane.showOptionDialog(null,"Die Datei '"+datei+"'\nist schreibgeschützt.",
                                          "Datei schreibgeschützt",0,JOptionPane.QUESTION_MESSAGE,null,auswahl,auswahl[0]);
    else
      return JOptionPane.showOptionDialog(null,"Die Datei '"+datei+"'\n muß in ein neues Format konvertiert werden, "+
                                               "aber sie ist schreibgeschützt.",
                                          "Datei schreibgeschützt",0,JOptionPane.QUESTION_MESSAGE,null,auswahl,auswahl[0]);
    }

  // Liefert Paßwort für Datei oder null, wenn Dialog abgebrochen wurde
  public static String getWriteProtectionPasswort(String datei, boolean firstTry) {
    return Dialog.inputDialog("Paßwort für Schreibschutz", (firstTry ? "" : "Das angegebene Paßwort war falsch!\n") +
                                           "Bitte gib das Paßwort zum Aufheben des Schreibschutzes der Datei\n'"+datei+"' an:");
  }


  // muß von jedem Frame gerufen werden, das geöffnet wird!!
  public static void frameOpened(Window w) {
    if (frameStack == null) frameStack = new Stack();
    frameStack.push(w);
    if (Daten.applID == Daten.APPL_EFADIREKT &&
        Daten.efaConfig != null && Daten.efaConfig.efaDirekt_immerImVordergrund) {
      try {
        de.nmichael.efa.java15.Java15.setAlwaysOnTop(w,true);
      } catch(UnsupportedClassVersionError e) {
        EfaUtil.foo();
      } catch(NoClassDefFoundError e) {
        EfaUtil.foo();
      }
    }
//    System.out.println(w.getClass().toString()+" geöffnet"); // !!!
  }

  // muß von jedem Frame gerufen werden, das geschlossen wird!!
  public static void frameClosed(Window w) {
    Mnemonics.clearCache(w);
    if (frameStack == null) return;
    if (frameStack.isEmpty()) {
      if (Daten.watchWindowStack)  {
        Logger.log(Logger.ERROR,
                   "Stack-Inkonsistenz: geschlossenes Fenster: " +
                   w.getClass().toString() + ", aber Stack leer.");
        Thread.dumpStack();
        (new Exception("Watch Stack Exception")).printStackTrace();
      }
      return;
    }
    Window wtop = (Window)frameStack.peek();
    if (wtop != w) {
      String s = "";
      try {
        for (int i=0; i<frameStack.size(); i++) s += (s.length()>0 ? "; " : "") + frameStack.elementAt(i).getClass().toString();
      } catch(Exception e) { EfaUtil.foo(); }
      if (Daten.watchWindowStack) {
        Logger.log(Logger.ERROR,
                   "Stack-Inkonsistenz: geschlossenes Fenster: " +
                   w.getClass().toString() + ", aber auf dem Stack: " +
                   wtop.getClass().toString() + " (Stack: " + s + ")");
        Thread.dumpStack();
        (new Exception("Watch Stack Exception")).printStackTrace();
      }
    } else {
        frameStack.pop();
    }
//    System.out.println(w.getClass().toString()+" geschlossen"); // !!!
  }

  // liefert das aktuell geöffnete Frame
  public static Window frameCurrent() {
    if (frameStack == null || frameStack.isEmpty()) return null;
    // System.out.println(((Window)(frameStack.peek())).getClass().toString()+" ist aktuelles Frame");
    return (Window)frameStack.peek();
  }




  // (this,"Fahrtenbuchdatei erstellen","efa Fahrtenbuch (*.efb)","efb",Daten.fahrtenbuch.getFileName(),true);
  public static String dateiDialog(Window frame, String titel, String typen, String extension, String startdir, boolean save) {
    return dateiDialog(frame,titel,typen,extension,startdir,null,null,save,false);
  }

  public static String dateiDialog(Window frame, String titel, String typen, String extension, String startdir, String selectedfile, String buttontxt, boolean save, boolean dirsOnly) {
    JFileChooser dlg;

    try {

      if (startdir != null) dlg = new JFileChooser(startdir);
      else dlg = new JFileChooser();

      if (typen != null && extension != null) {
        int wo;
        if ( (wo = extension.indexOf("|"))>=0) {
          String ext1,ext2;
          ext1 = extension.substring(0,wo);
          ext2 = extension.substring(wo+1,extension.length());
          dlg.setFileFilter((javax.swing.filechooser.FileFilter)new EfaFileFilter(typen,ext1,ext2));
        } else {
          dlg.setFileFilter((javax.swing.filechooser.FileFilter)new EfaFileFilter(typen,extension));
        }

      }

      if (titel != null) dlg.setDialogTitle(titel);

      if (selectedfile != null) dlg.setSelectedFile(new File(selectedfile));
      if (buttontxt != null) dlg.setApproveButtonText(buttontxt);
      if (dirsOnly) dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int ret;
      if (save) ret = dlg.showSaveDialog(frame);
      else ret = dlg.showOpenDialog(frame);

      if (ret == JFileChooser.APPROVE_OPTION) return dlg.getSelectedFile().toString();
      else return null;
    } catch(Exception e) {
      String input =
        (String)JOptionPane.showInputDialog(frame,
                                    "Beim Öffnen des Java-'Datei öffnen'-Dialogs trat ein Fehler auf:\n"+
                                    e.toString()+"\n\nBitte gib einen Dateinamen für '"+typen+"' ein:",
                                    titel,JOptionPane.QUESTION_MESSAGE,
                                    null,null,startdir);
      if (input != null && input.trim().length() == 0) input = null;
      return input;
    }
  }


  // Methoden zum Setzen der Position eines neuen JDialogs
  public static void setDlgLocation(JDialog dlg, Frame parent) {
    dlg.setLocation(getLocation(dlg.getSize(), (parent != null ? parent.getSize() : null), (parent != null ? parent.getLocation() : null)));
  }
  public static void setDlgLocation(JDialog dlg, Window parent) {
    dlg.setLocation(getLocation(dlg.getSize(), (parent != null ? parent.getSize() : null), (parent != null ? parent.getLocation() : null)));
  }
  public static void setDlgLocation(JDialog dlg) {
    dlg.setLocation(getLocation(dlg.getSize(), null, null));
  }
  public static void setDlgLocation(JFrame dlg) {
    dlg.setLocation(getLocation(dlg.getSize(), null, null));
  }
  private static Point getLocation(Dimension dlgSize, Dimension parentSize, Point loc) {
    int x,y;
    if (dlgSize.height > screenSize.height) {
      dlgSize.height = screenSize.height;
    }
    if (dlgSize.width > screenSize.width) {
      dlgSize.width = screenSize.width;
    }
    if (parentSize != null && loc != null && Daten.efaConfig != null && !Daten.efaConfig.fensterZentriert) {
      x = (parentSize.width - dlgSize.width) / 2 + loc.x;
      y = (parentSize.height - dlgSize.height) / 2 + loc.y;
    } else {
      x = (screenSize.width - dlgSize.width) / 2;
      y = (screenSize.height - dlgSize.height) / 2;
    }

    if (x<0) x = 0;
    if (y<0) y = 0;
    if (Daten.efaConfig != null && x<Daten.efaConfig.windowXOffset) x = Daten.efaConfig.windowXOffset;
    if (Daten.efaConfig != null && y<Daten.efaConfig.windowYOffset) y = Daten.efaConfig.windowYOffset;
    return new Point(x,y);
  }

  public static void setFontSize(String font, int size, int style) {
    Font orgFont = UIManager.getFont(font);
    if (orgFont == null) {
      Logger.log(Logger.WARNING,"Schrift "+font+" exisitert nicht; ihre Größe kann nicht geändert werden!");
      return;
    }
    if (!FONT_SIZE_CHANGED) {
      ORG_FONT_SIZE = orgFont.getSize();
      ORG_FONT_STYLE = orgFont.getStyle();
      if (style == -1) ORG_FONT_STYLE = -1; // Bugfix: Weil manche Schriften fett sind und andere nicht ...
    }
    Font newFont;
    if (style == -1) {
      newFont = orgFont.deriveFont((float)size);
    } else if (size <= 0) {
      newFont = orgFont.deriveFont(style);
    } else {
      newFont = orgFont.deriveFont(style,(float)size);
    }
    UIManager.put(font,newFont);
    FONT_SIZE_CHANGED = true;
  }

  public static void setGlobalFontSize(int size, int style) {
    FONT_SIZE = size;
    FONT_STYLE = style;

    UIDefaults uid = UIManager.getDefaults();
    java.util.Enumeration keys = uid.keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = uid.get(key);
      String font = ( key == null ? null : key.toString());
      if (font != null &&
          (font.endsWith(".font") ||
           (font.startsWith("OptionPane") && font.endsWith("Font"))
          )) {
        if (!font.equals("TableHeader.font") && !font.equals("Table.font")) setFontSize(font,size,style);
      }
    }
    initializeMaxDialogSizes();
  }

  public static void setPreferredSize(JComponent comp, int width, int height) {
    setPreferredSize(comp,width,height,1);
  }
  public static void setPreferredSize(JComponent comp, int width, int height, float corr) {
    if (FONT_SIZE<0) comp.setPreferredSize(new Dimension(width,height));
    else {
      float factor = ((float)FONT_SIZE) / 12.0f;
      factor = (factor-1.0f)*corr + 1.0f;
      comp.setPreferredSize(new Dimension((int)(((float)width) * factor),(int)(((float)height) * factor)));
    }
  }

  public static int getFontSize() {
    return FONT_SIZE;
  }

  public static int getFontStyle() {
    return FONT_STYLE;
  }

  public static boolean isFontSizeChanged() {
    return FONT_SIZE_CHANGED;
  }

  public static int getDefaultFontSize() {
    return ORG_FONT_SIZE;
  }

  public static int getDefaultFontStyle() {
    return ORG_FONT_STYLE;
  }

}