/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.text.*;

// @i18n complete

public class EfaFrame_AboutBox extends JDialog implements ActionListener {

  JPanel panelMain = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton okButton = new JButton();
  JLabel nameLabel = new JLabel();
  JLabel versionLabel = new JLabel();
  JLabel languageLabel = new JLabel();
  JLabel copyLabel = new JLabel();
  JLabel urlLabel0 = new JLabel();
  JLabel urlLabel = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel emailLabel0 = new JLabel();
  JLabel emailLabel = new JLabel();
  JLabel gpl1Label = new JLabel();
  JLabel logoLabel = new JLabel();
  JLabel gplLabel = new JLabel();
  JTabbedPane tabbedPane = new JTabbedPane();
  JPanel detailPanel = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea efaInfos = new JTextArea();
  BorderLayout borderLayout3 = new BorderLayout();
  JLabel efaBirthdayLabel = new JLabel();
  JPanel dankePanel = new JPanel();
  JPanel languagePanel = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout5 = new BorderLayout();
  JScrollPane jScrollPane2 = new JScrollPane();
  JScrollPane jScrollPane3 = new JScrollPane();
  JTextArea danke = new JTextArea();
  JTextArea languages = new JTextArea();
  JLabel devNoteLabel = new JLabel();
  JLabel devNote2Label = new JLabel();
  JLabel devNoteUrlLabel = new JLabel();


  public EfaFrame_AboutBox(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      frIni();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
    EfaUtil.pack(this);
    okButton.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
  }


  /**Component initialization*/
  private void jbInit() throws Exception  {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    this.setTitle(Daten.EFA_LONGNAME);
    panelMain.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    nameLabel.setFont(new java.awt.Font("Dialog", 1, (Dialog.getFontSize() > 0 ? Dialog.getFontSize()+6 : 18)));
    nameLabel.setForeground(Color.black);
    nameLabel.setText(Daten.EFA_LONGNAME);
    versionLabel.setText("Version 0.1"); // do not internationalize
    languageLabel.setText(International.getString("Sprache")+": "+
            International.getLanguageDescription());
    copyLabel.setText("Copyright (c) 2001-"+Daten.COPYRIGHTYEAR+" by Nicolas Michael"); // do not internationalize
    urlLabel0.setText(International.getString("Homepage")+": ");
    urlLabel.setForeground(Color.blue);
    urlLabel.setText(Daten.EFAURL);
    urlLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        urlLabel_mouseClicked(e);
      }
      public void mouseEntered(MouseEvent e) {
        urlLabel_mouseEntered(e);
      }
      public void mouseExited(MouseEvent e) {
        urlLabel_mouseExited(e);
      }
    });
    insetsPanel3.setLayout(gridBagLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    Mnemonics.setButton(this, okButton, International.getStringWithMnemonic("OK"));
    okButton.addActionListener(this);
    emailLabel0.setText(International.getString("email")+": ");
    emailLabel.setForeground(Color.blue);
    emailLabel.setText(Daten.EFAEMAIL);
    emailLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        emailLabel_mouseEntered(e);
      }
      public void mouseExited(MouseEvent e) {
        emailLabel_mouseExited(e);
      }
      public void mouseClicked(MouseEvent e) {
        emailLabel_mouseClicked(e);
      }
    });
    versionLabel.setForeground(Color.black);
    logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    logoLabel.setIcon(new ImageIcon(EfaFrame_AboutBox.class.getResource(Daten.getEfaImage(2))));
    gpl1Label.setText(International.getString("efa unterliegt den")+" ");
    gplLabel.setForeground(Color.blue);
    gplLabel.setText(International.getMessage("Lizenzbestimmungen der {license}","GPL v2"));
    gplLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        gplLabel_mouseClicked(e);
      }
      public void mouseEntered(MouseEvent e) {
        gplLabel_mouseEntered(e);
      }
      public void mouseExited(MouseEvent e) {
        gplLabel_mouseExited(e);
      }
    });
    devNoteLabel.setText(International.getString("Diese Version ist eine Entwicklerversion in Alpha-Qualität!"));
    devNoteLabel.setForeground(Color.red);
    devNote2Label.setText(International.getString("Bitte melde Fehler unter:")+" ");
    devNote2Label.setForeground(Color.black);
    devNoteUrlLabel.setText(Daten.EFADEVURL);
    devNoteUrlLabel.setForeground(Color.blue);
    devNoteUrlLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        devNoteUrlLabel_mouseClicked(e);
      }
      public void mouseEntered(MouseEvent e) {
        devNoteUrlLabel_mouseEntered(e);
      }
      public void mouseExited(MouseEvent e) {
        devNoteUrlLabel_mouseExited(e);
      }
    });
    detailPanel.setLayout(borderLayout3);
    jScrollPane1.setPreferredSize(new Dimension(400, 300));
    efaBirthdayLabel.setForeground(Color.red);
    efaBirthdayLabel.setText("efaBirthdayLabel"); // do not internationalize
    dankePanel.setLayout(borderLayout4);
    languagePanel.setLayout(borderLayout5);
    insetsPanel3.add(logoLabel,  new GridBagConstraints(0, 0, 1, 7, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
    insetsPanel3.add(nameLabel,   new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 93, 0));
    insetsPanel3.add(versionLabel,   new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 230, 0));
    insetsPanel3.add(languageLabel,   new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 230, 0));
    insetsPanel3.add(copyLabel,   new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 65, 0));
    insetsPanel3.add(urlLabel0,   new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 0));
    insetsPanel3.add(urlLabel,   new GridBagConstraints(2, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 0));
    insetsPanel3.add(emailLabel0,   new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    insetsPanel3.add(emailLabel,   new GridBagConstraints(2, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    insetsPanel3.add(gpl1Label,    new GridBagConstraints(1, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    insetsPanel3.add(gplLabel,     new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 20), 0, 0));
    insetsPanel3.add(devNoteLabel,     new GridBagConstraints(1, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
    insetsPanel3.add(devNote2Label,     new GridBagConstraints(1, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    insetsPanel3.add(devNoteUrlLabel,     new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
    insetsPanel3.add(efaBirthdayLabel,  new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    tabbedPane.add(panelMain,     International.getString("Über efa"));
    tabbedPane.add(detailPanel,   International.getString("Systeminformationen"));
    tabbedPane.add(languagePanel,     International.getString("Sprachen"));
    tabbedPane.add(dankePanel,    International.getString("Danksagungen"));
    detailPanel.add(jScrollPane1,  BorderLayout.CENTER);
    dankePanel.add(jScrollPane2, BorderLayout.CENTER);
    jScrollPane2.getViewport().add(danke, null);
    languagePanel.add(jScrollPane3, BorderLayout.CENTER);
    jScrollPane3.getViewport().add(languages, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    panelMain.add(insetsPanel1, BorderLayout.SOUTH);
    panelMain.add(panel2, BorderLayout.CENTER);
    this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    this.getContentPane().add(okButton, BorderLayout.SOUTH);
    versionLabel.setText(International.getString("Version")+" "+Daten.VERSION + "   (" + Daten.VERSIONID + ")");
  }


  private void frIni() {
    Vector infos = Daten.getEfaInfos();
    for (int i=0; infos != null && i<infos.size(); i++) efaInfos.append((String)infos.get(i)+"\n");
    if (infos == null) efaInfos.append(International.getString("Keine Systeminformationen verfügbar."));
    jScrollPane1.getViewport().add(efaInfos, null);
    efaInfos.setCaretPosition(0);
    efaInfos.setEditable(false);

    if (EfaUtil.getEfaBirthday() == 5) {
      efaBirthdayLabel.setText(International.getMessage("{n} Jahre efa",5) + ": " +
              International.getString("Erste Veröffentlichung am 15.07.2001"));
      efaBirthdayLabel.setVisible(true);
    } else {
      efaBirthdayLabel.setVisible(false);
    }

    danke.setEditable(false);
    danke.append(International.getString("Folgenden Personen gilt Dank für die Unterstützung, Weiterentwicklung oder das Beisteuern von Code zu efa:")+"\n"+
                 "\n"+
                 "* Apache Software Foundation (FOP-Plugin, XML-Plugin)\n"+
                 "* Dennis Klopke\n"+
                 "* Deutscher Ruderverband\n"+
                 "* Enterprise Distributed Technologies (FTP-Plugin)\n"+
                 "* Jonas Binding\n"+
                 "* Jonathan Stott (JSunrise-Plugin)\n"+
                 "* Kay Hannay\n"+
                 "* KDE-Team\n"+
                 "* Landesruderverband Berlin\n"+
                 "* Ralf Ludwig\n"+
                 "* Robert Harder (Base64)\n"+
                 "* Thilo Coblenzer\n"+
                 "* World Wide Web Consortium (XML-Plugin)"
                 );

      String translations = "";
      try {
          Vector<String> bundles = International.getLanguageBundles();
          for (int i = 0; bundles != null && i < bundles.size(); i++) {
              Locale loc = new Locale(bundles.get(i));
              ResourceBundle bundle = ResourceBundle.getBundle(International.BUNDLE_NAME, loc);
              International.getString("+++TRANSLATED_BY+++"); // dummy, just to make make_i18n_keys.pl find this key ;)
              try {
                  translations += loc.getDisplayName() + ": " + bundle.getString("+++TRANSLATED_BY+++") + "\n";
              } catch(Exception translationNotFound) {
                  translations += loc.getDisplayName() + "\n";
              }
          }
      } catch (Exception e) {
          translations = "Could not get any language information."; // no need to translate
      }
      languages.setEditable(false);
      languages.append(International.getString("efa wurde in die folgenden Sprachen übersetzt:") + "\n\n" + translations + "\n" +
              International.getString("Bitte unterstütze uns bei der Übersetzung in weitere Sprachen:") + " " + Daten.EFADEVURL);
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  /**Close the dialog*/
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
  }
  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == okButton) {
      cancel();
    }
  }

  void urlLabel_mouseClicked(MouseEvent e) {
    if (Daten.applID == Daten.APPL_EFADIREKT) return;
    Dialog.startBrowser(this,Daten.NICOLASURL);
  }

  void urlLabel_mouseEntered(MouseEvent e) {
    urlLabel.setForeground(Color.red);
  }

  void urlLabel_mouseExited(MouseEvent e) {
    urlLabel.setForeground(Color.blue);
  }

  void emailLabel_mouseEntered(MouseEvent e) {
    emailLabel.setForeground(Color.red);
  }

  void emailLabel_mouseExited(MouseEvent e) {
    emailLabel.setForeground(Color.blue);
  }

  void emailLabel_mouseClicked(MouseEvent e) {
    if (Daten.applID == Daten.APPL_EFADIREKT) return;
    Dialog.neuBrowserDlg(this,"Browser","file:"+HtmlFactory.createMailto(),700,600,(int)Dialog.screenSize.getWidth()/2-350,(int)Dialog.screenSize.getHeight()/2-300);
  }

  void gplLabel_mouseClicked(MouseEvent e) {
    Dialog.neuBrowserDlg(this,"Browser","file:"+Daten.efaDocDirectory+Daten.EFA_LICENSE,700,600,(int)Dialog.screenSize.getWidth()/2-350,(int)Dialog.screenSize.getHeight()/2-300);
  }

  void gplLabel_mouseEntered(MouseEvent e) {
    gplLabel.setForeground(Color.red);
  }

  void gplLabel_mouseExited(MouseEvent e) {
    gplLabel.setForeground(Color.blue);
  }

  void devNoteUrlLabel_mouseClicked(MouseEvent e) {
      Dialog.startBrowser(this, Daten.EFADEVURL);
//    Dialog.neuBrowserDlg(this,"Browser",,700,600,(int)Dialog.screenSize.getWidth()/2-350,(int)Dialog.screenSize.getHeight()/2-300);
  }

  void devNoteUrlLabel_mouseEntered(MouseEvent e) {
    devNoteUrlLabel.setForeground(Color.red);
  }

  void devNoteUrlLabel_mouseExited(MouseEvent e) {
    devNoteUrlLabel.setForeground(Color.blue);
  }

}
