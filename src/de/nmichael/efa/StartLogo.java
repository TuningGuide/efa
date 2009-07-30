package de.nmichael.efa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;
import java.io.*;
import javax.swing.border.*;
import java.beans.*;

public class StartLogo {

  private String logoFile;
  private JWindow window;

  public StartLogo(String logoFile) {
    this.logoFile = logoFile;
    this.window = null;
  }

  public void show() {
    try {
      JLabel l = new JLabel();
      ImageIcon i = new ImageIcon(StartLogo.class.getResource(logoFile));
      l.setIcon(i);
      JPanel p = new JPanel();
      p.setBackground(new Color(0,0,150));
      p.setBorder(BorderFactory.createEtchedBorder());
      p.add(l);

      JWindow w = new JWindow();
      w.getContentPane().add(p);
      w.pack();

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = w.getSize();
      w.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      w.setVisible(true);
      if (Daten.javaVersion.startsWith("1.4")) {
        w.toFront(); // bei Java 1.4 im Vordergrund, da es sonst nicht sichtbar ist
      } else {
        w.toBack(); // damit bei Java 1.5 aufpoppende Fenster nicht hinter dem Logo aufpoppen!
      }

      this.window = w;
    } catch(Exception e) {
    }
  }

  public void remove() {
    if (window == null) return;
    window.setVisible(false);
    window.dispose();
    window = null;
  }

}