package de.nmichael.efa;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
// import javax.swing.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class EfaApplet extends Applet {
  boolean isStandalone = false;
  BorderLayout borderLayout1 = new BorderLayout();
  Button goBut = new Button();
  ScrollPane scrollPane1 = new ScrollPane();
  TextArea out = new TextArea();
  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  /**Construct the applet*/
  public EfaApplet() {
  }
  /**Initialize the applet*/
  public void init() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception {
    goBut.setLabel("Statistik erstellen");
    goBut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        goBut_actionPerformed(e);
      }
    });
    this.setSize(new Dimension(400,300));
    this.setLayout(borderLayout1);
    this.add(goBut, BorderLayout.SOUTH);
    this.add(scrollPane1, BorderLayout.CENTER);
    scrollPane1.add(out, null);
  }
  /**Start the applet*/
  public void start() {
  }
  /**Stop the applet*/
  public void stop() {
  }
  /**Destroy the applet*/
  public void destroy() {
  }
  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Get parameter info*/
  public String[][] getParameterInfo() {
    return null;
  }

  //static initializer for setting look & feel
  static {
    try {
      //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch(Exception e) {
    }
  }

  void goBut_actionPerformed(ActionEvent e) {
System.out.println("OK");
    Daten.fahrtenbuch = new Fahrtenbuch("2001.efb");
System.out.println("OK2");
    Daten.fahrtenbuch.readFile();
System.out.println("OK3");
    Daten.fahrtenbuch.getDaten().mitglieder = new Mitglieder("mitglieder.efbm");
System.out.println("OK4");
    Daten.fahrtenbuch.getDaten().mitglieder.readFile();
System.out.println("OK5");
//    Statistik.create(new StatistikDaten());
  }
}
