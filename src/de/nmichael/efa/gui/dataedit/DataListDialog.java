/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.dataedit;

import de.nmichael.efa.gui.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.gui.BaseDialog;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.ex.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public abstract class DataListDialog extends BaseDialog implements IItemListener, IItemListenerDataRecordTable {

    public static final int ACTION_HIDE    =  100;
    public static final int ACTION_IMPORT  = -100; // negative actions will not be shown as popup actions
    public static final int ACTION_EXPORT  = -101; // negative actions will not be shown as popup actions
    // @todo (P4) add a generic "print list" button

    protected StorageObject persistence;
    protected long validAt;

    protected String[] actionText;
    protected int[] actionType;
    protected String filterFieldName;
    protected String filterFieldValue;
    protected String filterFieldDescription;
    protected ItemTypeDataRecordTable table;
    protected int sortByColumn = 0;
    protected boolean sortAscending = true;
    protected int tableFontSize = -1;
    protected String buttonPanelPosition = BorderLayout.EAST;
    private ItemTypeDateTime validAtDateTime;
    private ItemTypeBoolean showAll;
    private ItemTypeBoolean showDeleted;
    private JPanel tablePanel;
    private JPanel buttonPanel;
    private Hashtable<ItemTypeButton,String> actionButtons;

    public DataListDialog(Frame parent, String title, StorageObject persistence, long validAt) {
        super(parent, title, International.getStringWithMnemonic("Schließen"));
        setPersistence(persistence, validAt);
        iniActions();
    }

    public DataListDialog(JDialog parent, String title, StorageObject persistence, long validAt) {
        super(parent, title, International.getStringWithMnemonic("Schließen"));
        setPersistence(persistence, validAt);
        iniActions();
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniActions() {
        if (persistence.data().getMetaData().isVersionized()) {
            actionText = new String[] {
                ItemTypeDataRecordTable.ACTIONTEXT_NEW,
                ItemTypeDataRecordTable.ACTIONTEXT_EDIT,
                ItemTypeDataRecordTable.ACTIONTEXT_DELETE,
                International.getString("Verstecken"),
                International.getString("Importieren"),
                International.getString("Exportieren")
            };
            actionType = new int[] {
                ItemTypeDataRecordTable.ACTION_NEW,
                ItemTypeDataRecordTable.ACTION_EDIT,
                ItemTypeDataRecordTable.ACTION_DELETE,
                ACTION_HIDE,
                ACTION_IMPORT,
                ACTION_EXPORT
            };
        } else {
            actionText = new String[] {
                ItemTypeDataRecordTable.ACTIONTEXT_NEW,
                ItemTypeDataRecordTable.ACTIONTEXT_EDIT,
                ItemTypeDataRecordTable.ACTIONTEXT_DELETE,
                International.getString("Importieren"),
                International.getString("Exportieren")
            };
            actionType = new int[] {
                ItemTypeDataRecordTable.ACTION_NEW,
                ItemTypeDataRecordTable.ACTION_EDIT,
                ItemTypeDataRecordTable.ACTION_DELETE,
                ACTION_IMPORT,
                ACTION_EXPORT
            };
        }
    }


    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new BorderLayout());
        
        JPanel mainTablePanel = new JPanel();
        mainTablePanel.setLayout(new BorderLayout());

        if (filterFieldDescription != null) {
            JLabel filterName = new JLabel();
            filterName.setText(filterFieldDescription);
            filterName.setHorizontalAlignment(SwingConstants.CENTER);
            mainTablePanel.add(filterName, BorderLayout.NORTH);
            mainTablePanel.setBorder(new EmptyBorder(10,0,0,0));
        }

        table = new ItemTypeDataRecordTable("TABLE",
                persistence.createNewRecord().getGuiTableHeader(),
                persistence, validAt,
                filterFieldName, filterFieldValue, // defaults are null
                actionText, actionType, // default actions: new, edit, delete
                this,
                IItemType.TYPE_PUBLIC, "BASE_CAT", getTitle());
        table.setSorting(sortByColumn, sortAscending);
        table.setFontSize(tableFontSize);
        table.setButtonPanelPosition(buttonPanelPosition);
        table.setFieldSize(600, 500);
        table.setPadding(0, 0, 10, 0);
        table.displayOnGui(this, mainTablePanel, BorderLayout.CENTER);

        if (persistence != null && persistence.data().getMetaData().isVersionized()) {
            JPanel mainControlPanel = new JPanel();
            mainControlPanel.setLayout(new GridBagLayout());
            validAtDateTime = new ItemTypeDateTime("VALID_AT",
                    (validAt < 0 ? null : new DataTypeDate(validAt)),
                    (validAt < 0 ? null : new DataTypeTime(validAt)),
                    IItemType.TYPE_PUBLIC, "", International.getString("zeige Datensätze gültig am"));
            validAtDateTime.setPadding(0, 0, 10, 0);
            validAtDateTime.displayOnGui(this, mainControlPanel, 0, 0);
            validAtDateTime.registerItemListener(this);
            showAll = new ItemTypeBoolean("SHOW_ALL",
                    false,
                    IItemType.TYPE_PUBLIC, "", International.getString("auch derzeit ungültige Datensätze zeigen"));
            showAll.setPadding(0, 0, 0, 0);
            showAll.displayOnGui(this, mainControlPanel, 0, 1);
            showAll.registerItemListener(this);
            showDeleted = new ItemTypeBoolean("SHOW_DELETED",
                    false,
                    IItemType.TYPE_PUBLIC, "", International.getString("auch gelöschte Datensätze zeigen"));
            showDeleted.setPadding(0, 0, 0, 0);
            showDeleted.displayOnGui(this, mainControlPanel, 0, 2);
            showDeleted.registerItemListener(this);

            mainPanel.add(mainControlPanel, BorderLayout.NORTH);
        }
        mainPanel.add(mainTablePanel, BorderLayout.CENTER);

        setRequestFocus(table);
        this.validate();
    }

    public void setPersistence(StorageObject persistence, long validAt) {
        this.persistence = persistence;
        this.validAt = validAt;
    }

    public void itemListenerActionTable(int actionId, DataRecord[] records) {
        // usually nothing to be done (handled in ItemTypeDataRecordTable itself).
        // override if necessary
        switch(actionId) {
            case ACTION_HIDE:
                if (records == null || records.length == 0 || records[0] == null || !persistence.data().getMetaData().isVersionized()) {
                    return;
                }
                boolean currentlyVisible = !records[0].getInvisible();
                int res = -1;
                if (currentlyVisible) {
                    if (records.length == 1) {
                        res = Dialog.yesNoDialog(International.getString("Wirklich verstecken?"),
                                International.getMessage("Möchtest Du den Datensatz '{record}' wirklich verstecken?", records[0].getQualifiedName()));
                    } else {
                        res = Dialog.yesNoDialog(International.getString("Wirklich verstecken?"),
                                International.getMessage("Möchtest Du {count} ausgewählte Datensätze wirklich verstecken?", records.length));
                    }
                } else {
                    if (records.length == 1) {
                        res = Dialog.yesNoDialog(International.getString("Wirklich sichtbar machen?"),
                                International.getMessage("Möchtest Du den Datensatz '{record}' wirklich sichtbar machen?", records[0].getQualifiedName()));
                    } else {
                        res = Dialog.yesNoDialog(International.getString("Wirklich sichtbar machen?"),
                                International.getMessage("Möchtest Du {count} ausgewählte Datensätze wirklich sichtbar machen?", records.length));
                    }
                }
                if (res != Dialog.YES) {
                    return;
                }
                try {
                    for (int i = 0; records != null && i < records.length; i++) {
                        if (records[i] != null) {
                            if (persistence.data().getMetaData().isVersionized()) {
                                DataRecord[] allVersions = persistence.data().getValidAny(records[i].getKey());
                                for (int j=0; allVersions != null && j<allVersions.length; j++) {
                                    allVersions[j].setInvisible(currentlyVisible);
                                    persistence.data().update(allVersions[j]);
                                }
                            } else {
                                records[i].setInvisible(currentlyVisible);
                                persistence.data().update(records[i]);
                            }
                        }
                    }
                } catch (EfaModifyException exmodify) {
                    exmodify.displayMessage();
                } catch (Exception ex) {
                    Logger.logdebug(ex);
                    Dialog.error(ex.toString());
                }
                break;
            case ACTION_IMPORT:
                DataImportDialog dlg1 = new DataImportDialog(this, persistence, validAt);
                dlg1.showDialog();
                break;
            case ACTION_EXPORT:
                DataExportDialog dlg2 = new DataExportDialog(this, persistence, validAt);
                dlg2.showDialog();
                break;
        }
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == validAtDateTime) {
            if (event.getID() == FocusEvent.FOCUS_LOST ||
                (event.getID() == KeyEvent.KEY_PRESSED && ((KeyEvent)event).getKeyCode() == KeyEvent.VK_ENTER)) {
                validAtDateTime.getValueFromGui();
                validAtDateTime.parseAndShowValue(validAtDateTime.getValueFromField());
                long _validAt = (validAtDateTime.isSet() ? validAtDateTime.getTimeStamp() : -1);
                if (_validAt != validAt) {
                    validAt = _validAt;
                    showDeleted.getValueFromGui();
                    showAll.getValueFromGui();
                    table.setAndUpdateData(validAt, showAll.getValue(), showDeleted.getValue());
                    table.showValue();
                }
            }
        }
        if (itemType == showAll || itemType == showDeleted) {
            if (event.getID() == ActionEvent.ACTION_PERFORMED) {
                showAll.getValueFromGui();
                showDeleted.getValueFromGui();
                if (showAll.getValue()) {
                    showAll.saveColor();
                    showAll.setColor(Color.gray);
                } else {
                    showAll.restoreColor();
                }
                if (showDeleted.getValue()) {
                    showDeleted.saveColor();
                    showDeleted.setColor(Color.red);
                } else {
                    showDeleted.restoreColor();
                }
                table.setAndUpdateData(validAt, showAll.getValue(), showDeleted.getValue());
                table.showValue();
            }
        }
    }

/* @todo (P5) Implement a Print List functionality
 *   void printButton_actionPerformed(ActionEvent e) {
    if (drvSignatur == null) {
      Dialog.error("Kein elektronisches Fahrtenheft vorhanden!");
      return;
    }
    String tmpdatei = Daten.efaTmpDirectory+"eFahrtenheft.html";
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpdatei),Daten.ENCODING_ISO));
      f.write("<html>\n");
      f.write("<head><META http-equiv=\"Content-Type\" content=\"text/html; charset="+Daten.ENCODING_ISO+"\"></head>\n");
      f.write("<body>\n");
      f.write("<h1 align=\"center\">elektronisches Fahrtenheft<br>für "+drvSignatur.getVorname()+" "+drvSignatur.getNachname()+"</h1>\n");
      f.write("<table align=\"center\" border=\"3\" width=\"100%\">\n");
      f.write("<tr><td>DRV-Teilnehmernummer:</td><td><tt><b>"+drvSignatur.getTeilnNr()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Vorname:</td><td><tt><b>"+drvSignatur.getVorname()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Nachname:</td><td><tt><b>"+drvSignatur.getNachname()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Jahrgang:</td><td><tt><b>"+drvSignatur.getJahrgang()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Anzahl der Fahrtenabzeichen:</td><td><tt><b>"+drvSignatur.getAnzAbzeichen()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Insgesamt nachgewiesene Kilometer:</td><td><tt><b>"+drvSignatur.getGesKm()+"</b></tt></td></tr>\n");
      f.write("<tr><td>... davon Fahrtenabzeichen Jugend A/B:</td><td><tt><b>"+drvSignatur.getAnzAbzeichenAB()+"</b></tt></td></tr>\n");
      f.write("<tr><td>... davon Kilometer Jugend A/B:</td><td><tt><b>"+drvSignatur.getGesKmAB()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Jahr der letzten elektronischen Meldung:</td><td><tt><b>"+drvSignatur.getJahr()+"</b></tt></td></tr>\n");
      if (drvSignatur.getVersion() >= 3) f.write("<tr><td>Kilometer bei letzter elektronischer Meldung:</td><td><tt><b>"+drvSignatur.getLetzteKm()+"</b></tt></td></tr>\n");
      f.write("<tr><td>Ausstellungsdatum des Fahrtenhefts:</td><td><tt><b>"+drvSignatur.getSignaturDatum(true)+"</b></tt></td></tr>\n");
      f.write("<tr><td>Fahrtenabzeichen-Version:</td><td><tt><b>"+drvSignatur.getVersion()+"</b></tt></td></tr>\n");
      f.write("<tr><td>öffentlicher DRV-Schlüssel:</td><td><tt><b>"+drvSignatur.getKeyName()+"</b></tt></td></tr>\n");
      f.write("<tr><td>DRV-Signatur:</td><td><tt><b>"+drvSignatur.getSignaturString()+"</b></tt></td></tr>\n");
      f.write("<tr><td>elektronisches Fahrtenheft (zur Eingabe):</td><td><tt><b>"+drvSignatur.toString()+"</b></tt></td></tr>\n");
      if (signatureValidLabel.getForeground() == Color.red)
        f.write("<tr><td colspan=\"2\"><font color=\"red\"><b>"+signatureValidLabel.getText()+"</b></font></td></tr>\n");
      f.write("</table>\n");
      f.write("</body></html>\n");
      f.close();
      JEditorPane out = new JEditorPane();
      out.setContentType("text/html; charset="+Daten.ENCODING_ISO);
      out.setPage("file:"+tmpdatei);
      out.setSize(600,800);
      out.doLayout();
      SimpleFilePrinter sfp = new SimpleFilePrinter(out);
      if (sfp.setupPageFormat()) {
        if (sfp.setupJobOptions()) {
          sfp.printFile();
        }
      }
      EfaUtil.deleteFile(tmpdatei);
    } catch(Exception ee) {
      Dialog.error("Druckdatei konnte nicht erstellt werden: "+ee.toString());
      return;
    }
  }

 *
 */
}
