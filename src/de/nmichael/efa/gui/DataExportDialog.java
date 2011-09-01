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
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class DataExportDialog extends BaseDialog {

    private ItemTypeDateTime validAtDateTime;
    private JScrollPane selectedFieldsScrollPane;
    private JList selectedFields;
    private ButtonGroup fileTypeGroup;
    private JRadioButton fileTypeXml;
    private JRadioButton fileTypeCsv;
    private ItemTypeStringList encoding;
    private ItemTypeFile file;

    private StorageObject persistence;
    private long validAt;
    private String[] fields;
    private String[] fieldDescription;
    private int[] selectedIndices;

    // @todo (P2) DataExport option: "export to efa", which leaves all keys as they are (only xml, add flag "<exportMode>efa</exportMode>"

    public DataExportDialog(Frame parent, StorageObject persistence, long validAt) {
        super(parent, International.getMessage("{data} exportieren", persistence.getDescription()),
                International.getStringWithMnemonic("Export starten"));
        setPersistence(persistence, validAt);
    }

    public DataExportDialog(JDialog parent, StorageObject persistence, long validAt) {
        super(parent, International.getMessage("{data} exportieren", persistence.getDescription()),
                International.getStringWithMnemonic("Export starten"));
        setPersistence(persistence, validAt);
    }

    public void setPersistence(StorageObject persistence, long validAt) {
        this.persistence = persistence;
        this.validAt = validAt;
        this.fields = persistence.data().getFieldNames();
        this.fieldDescription = new String[fields.length];
        Vector<Integer> indices = new Vector<Integer>();
        Vector<IItemType> items = persistence.createNewRecord().getGuiItems();
        for (int i=0; i<fields.length; i++) {
            IItemType item = null;
            for (int j=0; items != null && j<items.size(); j++) {
                if (items.get(j).getName().equals(fields[i])) {
                    item = items.get(j);
                    break;
                }
            }
            fieldDescription[i] = fields[i] + (item != null ? " (" + item.getDescription() + ")" : "");
            if (!fields[i].equals(DataRecord.LASTMODIFIED) &&
                !fields[i].equals(DataRecord.VALIDFROM) &&
                !fields[i].equals(DataRecord.INVALIDFROM) &&
                !fields[i].equals(DataRecord.INVISIBLE) &&
                !fields[i].equals(DataRecord.DELETED) &&
                !fields[i].equals("Id")) {
                indices.add(i);
            }
        }
        selectedIndices = new int[indices.size()];
        for (int i=0; i<selectedIndices.length; i++) {
            selectedIndices[i] = indices.get(i);
        }
    }

    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new BorderLayout());

        JPanel mainControlPanel = new JPanel();
        mainControlPanel.setLayout(new GridBagLayout());
        if (persistence != null && persistence.data().getMetaData().isVersionized()) {
            validAtDateTime = new ItemTypeDateTime("VALID_AT",
                    (validAt < 0 ? DataTypeDate.today() : new DataTypeDate(validAt)),
                    (validAt < 0 ? DataTypeTime.now() : new DataTypeTime(validAt)),
                    IItemType.TYPE_PUBLIC, "", International.getString("exportiere Datensätze gültig am"));
            validAtDateTime.setNotNull(true);
            validAtDateTime.setPadding(0, 0, 10, 0);
            validAtDateTime.displayOnGui(this, mainControlPanel, 0, 0);
        }
        JLabel fieldsLabel = new JLabel();
        Mnemonics.setLabel(this, fieldsLabel, International.getString("ausgewählte Felder") + ":");
        mainControlPanel.add(fieldsLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 0), 0, 0));
        mainPanel.add(mainControlPanel, BorderLayout.NORTH);

        selectedFields = new JList();
        selectedFields.setListData(fieldDescription);
        selectedFields.setSelectedIndices(selectedIndices);
        selectedFieldsScrollPane = new JScrollPane();
        selectedFieldsScrollPane.setPreferredSize(new Dimension(300,200));
        selectedFieldsScrollPane.getViewport().add(selectedFields);
        mainPanel.add(selectedFieldsScrollPane, BorderLayout.CENTER);

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridBagLayout());
        JLabel fileTypeLabel = new JLabel();
        Mnemonics.setLabel(this, fileTypeLabel, International.getString("Export als"));
        fileTypeXml = new JRadioButton();
        Mnemonics.setButton(this, fileTypeXml, International.getStringWithMnemonic("XML-Datei"));
        fileTypeXml.setSelected(true);
        fileTypeCsv = new JRadioButton();
        Mnemonics.setButton(this, fileTypeCsv, International.getStringWithMnemonic("CSV-Datei"));
        fileTypeGroup = new ButtonGroup();
        fileTypeGroup.add(fileTypeXml);
        fileTypeGroup.add(fileTypeCsv);
        fileTypeXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileTypeChanged();
            }
        });
        fileTypeCsv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileTypeChanged();
            }
        });
        filePanel.add(fileTypeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 0), 0, 0));
        filePanel.add(fileTypeXml, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 0), 0, 0));
        filePanel.add(fileTypeCsv, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        encoding = new ItemTypeStringList("ENCODING", Daten.ENCODING_UTF,
                new String[] { Daten.ENCODING_UTF, Daten.ENCODING_ISO },
                new String[] { Daten.ENCODING_UTF, Daten.ENCODING_ISO },
                IItemType.TYPE_PUBLIC, "",
                International.getStringWithMnemonic("Zeichensatz")
                );
        encoding.displayOnGui(this, filePanel, 2);
        file = new ItemTypeFile("FILE",
                Daten.userHomeDir + (Daten.fileSep != null && !Daten.userHomeDir.endsWith(Daten.fileSep) ? Daten.fileSep : "") +
                persistence.data().getStorageObjectName() + ".xml",
                    International.getString("Datei"),
                    International.getString("Datei") + " (*.*)",
                    null, ItemTypeFile.MODE_SAVE, ItemTypeFile.TYPE_FILE,
                    IItemType.TYPE_PUBLIC, "",
                    International.getString("Export in Datei"));
        file.setNotNull(true);
        file.setPadding(0, 0, 0, 10);
        file.displayOnGui(this, filePanel, 3);
        mainPanel.add(filePanel, BorderLayout.SOUTH);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void fileTypeChanged() {
        boolean xml = fileTypeXml.isSelected();
        String fname = file.getValueFromField();
        if (fname != null) {
            int pos = fname.lastIndexOf(".");
            if (pos > 0) {
                String ext = fname.substring(pos + 1);
                fname = fname.substring(0, pos);
                if (xml && !ext.equalsIgnoreCase("xml")) {
                    file.parseAndShowValue(fname + ".xml");
                }
                if (!xml && !ext.equalsIgnoreCase("csv")) {
                    file.parseAndShowValue(fname + ".csv");
                }
            }
        }

    }

    public void closeButton_actionPerformed(ActionEvent e) {
        long validAt = -1;
        if (validAtDateTime != null) {
            validAtDateTime.getValueFromGui();
            validAt = validAtDateTime.getTimeStamp();
        }

        int[] indices = selectedFields.getSelectedIndices();
        String[] fieldNames = new String[indices.length];
        for (int i=0; i<indices.length; i++) {
            fieldNames[i] = fields[indices[i]];
        }

        DataExport.Format format = (fileTypeXml.isSelected() ? DataExport.Format.xml : DataExport.Format.csv);
        encoding.getValueFromField();
        file.getValueFromField();
        String fname = file.getValue();

        if (fieldNames.length == 0) {
            Dialog.error(International.getString("Keine Felder selektiert."));
            return;
        }
        if (!file.isValidInput()) {
            Dialog.error(International.getString(file.getInvalidErrorText()));
            return;
        }

        if ((new File(fname).exists())) {
            if (Dialog.yesNoDialog(International.getString("Warnung"),
                    International.getMessage("Datei {file} existiert bereits. Überschreiben?", fname)) != Dialog.YES) {
                return;
            }
        }

        DataExport export = new DataExport(persistence, validAt, fieldNames, format, encoding.getValue(), fname, DataExport.EXPORT_TYPE_TEXT);
        int cnt = export.runExport();
        if (cnt >= 0) {
            Dialog.infoDialog(International.getMessage("{count} Datensätze erfolgreich exportiert.", cnt));
        } else {
            Dialog.error(export.getLastError());
        }
        
    }

}
