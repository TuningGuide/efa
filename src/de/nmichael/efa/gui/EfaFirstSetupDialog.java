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

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.*;
import de.nmichael.efa.core.config.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.batik.ext.swing.GridBagConstants;

public class EfaFirstSetupDialog extends StepwiseDialog {

    static final String ADMIN_NAME           = "ADMIN_NAME";
    static final String ADMIN_PASSWORD       = "ADMIN_PASSWORD";

    static final String CUST_ROWING          = "CUST_ROWING";
    static final String CUST_ROWINGGERMANY   = "CUST_ROWINGGERMANY";
    static final String CUST_ROWINGBERLIN    = "CUST_ROWINGBERLIN";
    static final String CUST_CANOEING        = "CUST_CANOEING";
    static final String CUST_CANOEINGGERMANY = "CUST_CANOEINGGERMANY";

    private boolean createSuperAdmin;
    private boolean efaCustomization;
    private CustSettings custSettings = null;

    public EfaFirstSetupDialog(boolean createSuperAdmin, boolean efaCustomization) {
        super((JFrame)null, International.getString(Daten.EFA_LONGNAME));
        this.createSuperAdmin = createSuperAdmin;
        this.efaCustomization = efaCustomization;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Willkommen!"),
            International.getString("Super-Admin anlegen"),
            International.getString("Einstellungen")
        };
    }

    String getDescription(int step) {
        switch(step) {
            case 0:
                return International.getString("Willkommen bei efa, dem elektronischen Fahrtenbuch!") + "\n" +
                       International.getString("Dieser Dialog führt Dich durch die ersten Schritte, um efa einzurichten.");
            case 1:
                return International.getString("Alle Administrationsaufgaben in efa erfordern Administratorrechte.") + "\n" +
                       International.getString("Bitte lege ein Paßwort (mindestens 6 Zeichen) für den Hauptadministrator 'admin' fest.");
            case 2:
                return International.getString("Welche Funktionen von efa möchtest Du verwenden?") + "\n" +
                       International.getString("Du kannst diese Einstellungen jederzeit in der efa-Konfiguration ändern.");
        }
        return "";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        // Items for Step 0
        items.add(item = new ItemTypeLabel("LOGO", IItemType.TYPE_PUBLIC, "0", ""));
        ((ItemTypeLabel)item).setImage(new ImageIcon(EfaFirstSetupDialog.class.getResource(Daten.getEfaImage(2))));
        ((ItemTypeLabel)item).setFieldGrid(1, GridBagConstants.CENTER, GridBagConstants.HORIZONTAL);
        ((ItemTypeLabel)item).setPadding(10, 10, 10, 10);
        items.add(item = new ItemTypeLabel("EFA", IItemType.TYPE_PUBLIC, "0", Daten.EFA_LONGNAME));

        // Items for Step 1
        items.add(item = new ItemTypeLabel("ADMIN_LABEL", IItemType.TYPE_PUBLIC, "1", International.getString("Neuer Hauptadministrator")+
                " ('admin')"));
        items.add(item = new ItemTypeString(ADMIN_NAME, Admins.SUPERADMIN, IItemType.TYPE_PUBLIC, "1", International.getString("Name")));
        ((ItemTypeString)item).setEnabled(false);
        items.add(item = new ItemTypePassword(ADMIN_PASSWORD, "", IItemType.TYPE_PUBLIC, "1", International.getString("Paßwort")));
        ((ItemTypePassword)item).setNotNull(true);
        ((ItemTypePassword)item).setMinCharacters(6);

        // Items for Step 2
        items.add(item = new ItemTypeLabel("CUST_LABEL", IItemType.TYPE_PUBLIC, "2",
                International.getString("Welche Funktionen von efa möchtest Du verwenden?")));
        items.add(item = new ItemTypeBoolean(CUST_ROWING, true, IItemType.TYPE_PUBLIC, "2",
                International.getString("Rudern")));
        items.add(item = new ItemTypeBoolean(CUST_ROWINGGERMANY, International.getLanguageID().startsWith("de"), IItemType.TYPE_PUBLIC, "2",
                International.getString("Rudern") + " " +
                International.getMessage("in {region}",
                International.getString("Deutschland"))));
        items.add(item = new ItemTypeBoolean(CUST_ROWINGBERLIN, International.getLanguageID().startsWith("de"), IItemType.TYPE_PUBLIC, "2",
                International.getString("Rudern") + " " +
                International.getMessage("in {region}",
                International.getString("Berlin"))));
        items.add(item = new ItemTypeBoolean(CUST_CANOEING, false, IItemType.TYPE_PUBLIC, "2",
                International.getString("Kanufahren")));
        items.add(item = new ItemTypeBoolean(CUST_CANOEINGGERMANY, false, IItemType.TYPE_PUBLIC, "2",
                International.getString("Kanufahren") + " " +
                International.getMessage("in {region}",
                International.getString("Deutschland"))));
    }

    void finishButton_actionPerformed(ActionEvent e) {
        if (!checkInput(0)) {
            return;
        }
        if (createSuperAdmin) {
            createNewSuperAdmin(((ItemTypePassword)getItemByName(ADMIN_PASSWORD)).getValue());
        }
        if (efaCustomization) {
            custSettings = new CustSettings();
            custSettings.activateRowingOptions = ((ItemTypeBoolean)getItemByName(CUST_ROWING)).getValue();
            custSettings.activateGermanRowingOptions = ((ItemTypeBoolean)getItemByName(CUST_ROWINGGERMANY)).getValue();
            custSettings.activateBerlinRowingOptions = ((ItemTypeBoolean)getItemByName(CUST_ROWINGBERLIN)).getValue();
            custSettings.activateCanoeingOptions = ((ItemTypeBoolean)getItemByName(CUST_CANOEING)).getValue();
            custSettings.activateGermanCanoeingOptions = ((ItemTypeBoolean)getItemByName(CUST_CANOEINGGERMANY)).getValue();
        }
        cancel();
    }

    void createNewSuperAdmin(String password) {
        if (password == null || password.length() == 0) {
            return;
        }
        try {
            Daten.admins.open(true);
            // ok, new admin file created (or existing, empty one opened). Now add admin
            Daten.admins.data().add(Daten.admins.createAdminRecord(Admins.SUPERADMIN, password));
            //Now delete sec file
            Daten.efaSec.delete(true);
        } catch (Exception ee) {
            String msg = LogString.logstring_fileCreationFailed(((DataFile) Daten.admins.data()).getFilename(),
                    International.getString("Administratoren"));
            Logger.log(Logger.ERROR, Logger.MSG_CORE_ADMINSFAILEDCREATE, msg);
            if (Daten.isGuiAppl()) {
                Dialog.error(msg);
            }
            Daten.haltProgram(Daten.HALT_ADMIN);
        }
        String msg = LogString.logstring_fileNewCreated(((DataFile) Daten.admins.data()).getFilename(),
                International.getString("Administratoren"));
        Logger.log(Logger.WARNING, Logger.MSG_CORE_ADMINSCREATEDNEW, msg);
        Dialog.infoDialog(International.getString("Neuer Hauptadministrator"),
                International.getString("Ein neuer Administrator mit Namen 'admin' wurde angelegt. Bitte notiere Dir Name und Paßwort an einem sicheren Ort."));
    }

    public CustSettings getCustSettings() {
        return custSettings;
    }
}
