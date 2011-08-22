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
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.core.config.Admins;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AdminLoginDialog extends BaseDialog {

    private String KEYACTION_ENTER;
    private String reason;
    private ItemTypeString name;
    private ItemTypePassword password;
    private AdminRecord adminRecord;

    public AdminLoginDialog(Frame parent, String reason) {
        super(parent, International.getStringWithMnemonic("Admin-Login"), International.getStringWithMnemonic("Login"));
        this.reason = reason;
    }

    public AdminLoginDialog(JDialog parent, String reason) {
        super(parent, International.getStringWithMnemonic("Admin-Login"), International.getStringWithMnemonic("Login"));
        this.reason = reason;
    }

    protected void iniDialog() throws Exception {
        KEYACTION_ENTER      = addKeyAction("ENTER");

        mainPanel.setLayout(new GridBagLayout());

        JLabel reasonLabel = new JLabel();
        reasonLabel.setText(reason);
        reasonLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel infoLabel = new JLabel();
        infoLabel.setText(International.getString("Admin-Login erforderlich."));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (reason != null && reason.length() > 0) {
            mainPanel.add(reasonLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
        }
        mainPanel.add(infoLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

        name = new ItemTypeString("NAME", "", IItemType.TYPE_PUBLIC, "", International.getStringWithMnemonic("Admin-Name"));
        name.setAllowedCharacters("abcdefghijklmnopqrstuvwxyz0123456789_");
        name.setFieldSize(120, 20);
        password = new ItemTypePassword("PASSWORD", "", IItemType.TYPE_PUBLIC, "", International.getStringWithMnemonic("Paßwort"));
        password.setFieldSize(120, 20);
        
        name.displayOnGui(this, mainPanel, 0, 2);
        password.displayOnGui(this, mainPanel, 0, 3);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void _keyAction(ActionEvent evt) {
        if (evt.getActionCommand().equals(KEYACTION_ENTER)) {
            closeButton_actionPerformed(evt);
        }
        super._keyAction(evt);
    }

    public void setLoginOnlyAdmin(String adminName) {
        name.parseAndShowValue(adminName);
        name.setEnabled(false);
        password.requestFocus();
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        name.getValueFromGui();
        password.getValueFromGui();
        if (!name.isValidInput()) {
            Dialog.error(International.getString("Kein Admin-Name eingegeben!"));
            name.requestFocus();
            return;
        }
        if (!password.isValidInput()) {
            Dialog.error(International.getString("Kein Paßwort eingegeben!"));
            password.requestFocus();
            return;
        }
        if ((adminRecord = Daten.admins.login(name.getValue(), password.getValue())) == null) {
            Dialog.error(International.getString("Admin-Name oder Paßwort ungültig!"));
            Logger.log(Logger.WARNING, Logger.MSG_ADMIN_LOGINFAILURE, International.getString("Admin-Login") + ": "
                    + International.getMessage("Name {name} oder Paßwort ungültig!", name.getValue()));
            password.requestFocus();
            adminRecord = null;
            return;
        }
        if (adminRecord != null) {
            Logger.log(Logger.INFO, Logger.MSG_ADMIN_LOGIN, International.getString("Admin-Login") + ": "
                    + International.getString("Name") + ": " + adminRecord.getName());
        } else {
            return;
        }
        super.closeButton_actionPerformed(e);
    }

    public AdminRecord getResult() {
        return adminRecord;
    }

    public static AdminRecord login(Window parent, String reason) {
        return login(parent, reason, null);
    }

    public static AdminRecord login(Window parent, String reason, String admin) {
        AdminLoginDialog dlg = null;
        if (parent == null) {
            dlg = new AdminLoginDialog((JDialog)null, reason);
        } else {
            try {
                dlg = new AdminLoginDialog((JDialog) parent, reason);
            } catch (ClassCastException e) {
                dlg = new AdminLoginDialog((JFrame) parent, reason);
            }
        }
        return login(dlg, reason, admin);
    }

    public static AdminRecord login(Frame parent, String grund, String admin) {
        AdminLoginDialog dlg = null;
        if (parent != null) {
            dlg = new AdminLoginDialog(parent, grund);
        } else {
            dlg = new AdminLoginDialog((JFrame)null, grund);
        }
        return login(dlg, grund, admin);
    }

    public static AdminRecord login(AdminLoginDialog dlg, String grund, String adminName) {
        //dlg.setModal(true);
        if (adminName != null) {
            dlg.setLoginOnlyAdmin(adminName);
        }
        dlg.showDialog();
        return dlg.getResult();
    }



}
