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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class AdminDialog extends BaseDialog implements IItemListener {

    private EfaBoathouseFrame efaBoathouseFrame;
    private AdminRecord admin;
    private Logbook logbook;
    private JLabel projectName;
    private JLabel logbookName;

    public AdminDialog(EfaBoathouseFrame parent, AdminRecord admin, Logbook logbook) {
        super(parent, International.getStringWithMnemonic("Admin-Modus") + " [" + admin.getName() + "]", International.getStringWithMnemonic("Logout"));
        this.efaBoathouseFrame = parent;
        this.admin = admin;
        this.logbook = logbook;
    }

    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new BorderLayout());
        iniNorthPanel();
        iniCenterPanel();
    }

    private void iniCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        JPanel menuFile = new JPanel();
        menuFile.setLayout(new GridBagLayout());
        JPanel menuAdministration = new JPanel();
        menuAdministration.setLayout(new GridBagLayout());
        JPanel menuOutput = new JPanel();
        menuOutput.setLayout(new GridBagLayout());
        JPanel menuInfo = new JPanel();
        menuInfo.setLayout(new GridBagLayout());
        JPanel panel = null;

        Vector<EfaMenuButton> menuButtons = EfaMenuButton.getAllMenuButtons(admin, true);
        String lastMenuName = null;
        int y = 0;
        int space = 0;
        for (EfaMenuButton menuButton : menuButtons) {
            if (!menuButton.getMenuName().equals(lastMenuName)) {
                // New Menu
                panel = null;
                if (menuButton.getMenuName().equals(EfaMenuButton.MENU_FILE)) {
                    panel = menuFile;
                }
                if (menuButton.getMenuName().equals(EfaMenuButton.MENU_ADMINISTRATION)) {
                    panel = menuAdministration;
                }
                if (menuButton.getMenuName().equals(EfaMenuButton.MENU_OUTPUT)) {
                    panel = menuOutput;
                }
                if (menuButton.getMenuName().equals(EfaMenuButton.MENU_INFO)) {
                    panel = menuInfo;
                }
                if (panel == null) {
                    continue;
                }
                JLabel label = new JLabel();
                Mnemonics.setLabel(this, label, menuButton.getMenuText());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                y = 0;
                panel.add(label,
                        new GridBagConstraints(0, y++, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 10, 0), 0, 0));
                lastMenuName = menuButton.getMenuName();
            }
            if (panel == null) {
                continue;
            }
            if (menuButton.getButtonName().equals(EfaMenuButton.SEPARATOR)) {
                space = 10;
            } else {
                ItemTypeButton button = new ItemTypeButton(menuButton.getButtonName(),
                        IItemType.TYPE_PUBLIC, menuButton.getMenuName(), menuButton.getButtonText());
                if (menuButton.getIcon() != null) {
                    button.setIcon(menuButton.getIcon());
                }
                button.setFieldSize(200, 20);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.registerItemListener(this);
                if (space > 0) {
                    button.setPadding(0, 0, space, 0);
                }
                button.displayOnGui(this, panel, 0, y++);
                space = 0;
            }
        }
        centerPanel.add(menuFile,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));
        centerPanel.add(menuOutput,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));
        centerPanel.add(menuInfo,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));
        centerPanel.add(menuAdministration,
                new GridBagConstraints(1, 0, 1, 3, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void iniNorthPanel() {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridBagLayout());

        projectName = new JLabel();
        northPanel.add(projectName,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 0, 10), 0, 0));
        logbookName = new JLabel();
        northPanel.add(logbookName,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 10, 10), 0, 0));

        updateInfos();
        mainPanel.add(northPanel, BorderLayout.NORTH);
    }

    private void updateInfos() {
        projectName.setText(International.getString("Projekt") + ": " +
                (Daten.project != null ? Daten.project.getProjectName() : "- " + International.getString("kein Projekt geöffnet") + " -"));
        logbookName.setText(International.getString("Fahrtenbuch") + ": " +
                (logbook != null ? logbook.getName() : "- " + International.getString("kein Fahrtenbuch geöffnet") + " -"));
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (event != null && event instanceof ActionEvent) {
            String action = itemType.getName();

            if (action.equals(EfaMenuButton.BUTTON_EXIT)) {
                if (admin.isAllowedExitEfa() &&
                    Dialog.yesNoDialog(International.getString("Beenden"),
                                       International.getString("Möchtest Du efa wirklich beenden?")) == Dialog.YES) {
                    cancel();
                    efaBoathouseFrame.cancel(null, EfaBoathouseFrame.EFA_EXIT_REASON_USER, admin, false);
                }
                return;
            }

            // now check permissions and perform the menu action
            boolean permission = EfaMenuButton.menuAction(this, action, admin, logbook);

            // Projects and Logbooks are *not* handled within EfaMenuButton
            if (action.equals(EfaMenuButton.BUTTON_PROJECTS) && permission) {
                efaBoathouseFrame.openProject(admin);
                updateInfos();
            }
            if ((action.equals(EfaMenuButton.BUTTON_PROJECTS) || action.equals(EfaMenuButton.BUTTON_LOGBOOKS)) && permission) {
                if (Daten.project == null) {
                    Dialog.error(International.getString("Kein Projekt geöffnet."));
                    return;
                }
                logbook = efaBoathouseFrame.openLogbook(admin);
                updateInfos();
            }

        }
        
    }

}
