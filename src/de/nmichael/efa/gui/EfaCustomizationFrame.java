/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.config.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

// @i18n complete
public class EfaCustomizationFrame extends BaseDialog {

    private CustSettings custSettings = null;
    private JCheckBox activateGermanRowingOptions;
    private JCheckBox activateBerlinRowingOptions;
    private JCheckBox activateRowingOptions;
    private JCheckBox activateCanoeingOptions;

    public EfaCustomizationFrame(Frame parent) {
        super(parent, Daten.EFA_LONGNAME, International.getStringWithMnemonic("Speichern"));
    }

    public EfaCustomizationFrame(JDialog parent) {
        super(parent, Daten.EFA_LONGNAME, International.getStringWithMnemonic("Speichern"));
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDialog() throws Exception {
        // efa Logo and Welcome Message
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new GridBagLayout());
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        logoLabel.setIcon(new ImageIcon(EfaCustomizationFrame.class.getResource(Daten.getEfaImage(2))));
        JLabel welcomeLabel = new JLabel();
        welcomeLabel.setFont(new java.awt.Font("Dialog", 1, (Dialog.getFontSize() > 0 ? Dialog.getFontSize()+12 : 24)));
        welcomeLabel.setForeground(Color.red);
        welcomeLabel.setText(International.getString("Willkommen!"));
        JLabel efaLabel = new JLabel();
        efaLabel.setFont(new java.awt.Font("Dialog", 1, (Dialog.getFontSize() > 0 ? Dialog.getFontSize()+8 : 20)));
        efaLabel.setForeground(Color.black);
        efaLabel.setText(Daten.EFA_LONGNAME);
        JLabel versionLabel = new JLabel();
        versionLabel.setFont(new java.awt.Font("Dialog", 1, (Dialog.getFontSize() > 0 ? Dialog.getFontSize()+4 : 16)));
        versionLabel.setForeground(Color.black);
        versionLabel.setText(International.getString("Version") + " " + Daten.VERSION);
        logoPanel.add(logoLabel,  new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        logoPanel.add(welcomeLabel,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 25, 0), 0, 0));
        logoPanel.add(efaLabel,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        logoPanel.add(versionLabel,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        // Customization
        JPanel custPanel = new JPanel();
        custPanel.setLayout(new GridBagLayout());
        JLabel custLabel = new JLabel();
        custLabel.setText(International.getString("Welche Funktionen von efa möchtest Du verwenden?"));
        JLabel custNoteLabel = new JLabel();
        custNoteLabel.setText(International.getString("Du kannst diese Einstellungen jederzeit in der efa-Konfiguration ändern."));

        // Customization: Regional
        JLabel custRegionalLabel = new JLabel();
        custRegionalLabel.setText(International.getString("Für welche Regionen möchtest Du efa verwenden?"));
        activateGermanRowingOptions = new JCheckBox();
        activateGermanRowingOptions.setText(International.getString("Deutschland") +
                " (" + International.getString("Rudern") + ")");
        activateGermanRowingOptions.setSelected(International.getLanguageID().startsWith("de"));
        activateBerlinRowingOptions = new JCheckBox();
        activateBerlinRowingOptions.setText(International.getString("Berlin") +
                " (" + International.getString("Rudern") + ")");
        activateBerlinRowingOptions.setSelected(International.getLanguageID().startsWith("de"));

        // Customization: Sport
        JLabel custSportsLabel = new JLabel();
        custSportsLabel.setText(International.getString("Für welche Sportarten möchtest Du efa verwenden?"));
        activateRowingOptions = new JCheckBox();
        activateRowingOptions.setText(International.getString("Rudern"));
        activateRowingOptions.setSelected(true);
        activateCanoeingOptions = new JCheckBox();
        activateCanoeingOptions.setText(International.getString("Kanufahren"));
        activateCanoeingOptions.setSelected(false);

        // Customization: build GUI
        custPanel.add(custLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 5, 0), 0, 0));
        custPanel.add(custRegionalLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        custPanel.add(activateGermanRowingOptions,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        custPanel.add(activateBerlinRowingOptions,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        custPanel.add(custSportsLabel,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        custPanel.add(activateRowingOptions,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        custPanel.add(activateCanoeingOptions,  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        custPanel.add(custNoteLabel,  new GridBagConstraints(0, 100, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(custPanel, BorderLayout.CENTER);
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        custSettings = new CustSettings();
        custSettings.activateGermanRowingOptions = activateGermanRowingOptions.isSelected();
        custSettings.activateBerlinRowingOptions = activateBerlinRowingOptions.isSelected();
        custSettings.activateRowingOptions = activateRowingOptions.isSelected();
        custSettings.activateCanoeingOptions = activateCanoeingOptions.isSelected();
        super.closeButton_actionPerformed(e);
    }

    public CustSettings getCustSettings() {
        return custSettings;
    }

}
