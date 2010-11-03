/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.types.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.importefa1.*;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.efa1.*;
import de.nmichael.efa.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ImportEfa1DataDialog extends StepwiseDialog {


    private static final String OLDEFADATADIR        = "OLDEFADATADIR";
    private static final String IMPORTDATA           = "IMPORTDATA";
    private static final String IMPORTDATALABEL      = "IMPORTDATALABEL";
    private static final String LOGBOOKRANGEFROM     = "LOGBOOKRANGEFROM";
    private static final String LOGBOOKRANGETO       = "LOGBOOKRANGETO";
    private static final String LOGBOOKRANGELABEL    = "LOGBOOKRANGELABEL";
    private HashMap<String, ImportMetadata> importData;

    public ImportEfa1DataDialog(JDialog parent) {
        super(parent, International.getString("Daten aus efa 1.x importieren"));
    }

    public ImportEfa1DataDialog(Frame parent) {
        super(parent, International.getString("Daten aus efa 1.x importieren"));
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Datenordner von efa 1.x auswählen"),
            International.getString("Zu importierende Daten auswählen"),
            International.getString("Zu importierende Fahrtenbücher auswählen"),
            International.getString("Zeitraum für Fahrtenbücher festlegen")
        };
    }

    String getDescription(int step) {
        switch(step) {
        }
        return "";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        // #####################################################################
        // Items for Step 0
        // #####################################################################

        // Find existing efa installations
        ArrayList<String> oldEfaDataDir = new ArrayList<String>();
        ArrayList<String> oldEfaDescription = new ArrayList<String>();
        try {
            File dir = new File(Daten.userHomeDir);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File f : files) {
                    if (f.isFile() && f.getName().startsWith(".efa_")) {
                        try {
                            String name = f.getName();
                            String dataDir = "";
                            String lastUsed = EfaUtil.getTimeStamp(f.lastModified());
                            BufferedReader efa1 = new BufferedReader(new InputStreamReader(new FileInputStream(f.getAbsolutePath()),Daten.ENCODING_ISO));
                            String s;
                            while ( (s = efa1.readLine()) != null) {
                                if (s.startsWith("USERHOME=")) {
                                    dataDir = s.substring(9);
                                }
                            }
                            efa1.close();
                            File fcfg = new File(dataDir+Daten.fileSep+"cfg/efa.cfg");
                            if (fcfg.isFile()) {
                                lastUsed = EfaUtil.getTimeStamp(fcfg.lastModified());
                            }
                            oldEfaDataDir.add(dataDir);
                            oldEfaDescription.add(International.getMessage("{datadir} (zuletzt genutzt {date})",
                                    dataDir, lastUsed));
                        } catch(Exception eignore2) {
                        }
                    }
                }
            }
        } catch(Exception eignore1) {
            // ignore
        }
        // Add existing efa Installations to GUI Selection
        if (oldEfaDataDir.size() > 0) {
            item = new ItemTypeStringList(OLDEFADATADIR, oldEfaDataDir.get(0),
                    oldEfaDataDir.toArray(new String[0]),
                    oldEfaDescription.toArray(new String[0]),
                IItemType.TYPE_PUBLIC, "0", International.getString("Daten importieren von"));
            ((ItemTypeStringList)item).setWidth(600);
            ((ItemTypeStringList)item).setTwoRows(true);
        } else {
            item = new ItemTypeFile(OLDEFADATADIR, "",
                    International.getString("Verzeichnis für Nutzerdaten"),
                    International.getString("Verzeichnisse"),
                null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_DIR,
                IItemType.TYPE_PUBLIC, "0",
                International.getString("Daten importieren von"));
        }
        items.add(item);

    }

    void reinitializeItems() {
        IItemType item;

        if (step == 0) {
            // #####################################################################
            // Items for Step 1 and 2
            // #####################################################################

            // remove all previous items for step 1 and 2
            int i = 0;
            while (i < items.size()) {
                if (items.get(i).getCategory().equals("1")
                        || items.get(i).getCategory().equals("2")) {
                    items.remove(i);
                } else {
                    i++;
                }
            }

            // get Data Directory
            item = getItemByName(OLDEFADATADIR);
            String dir;
            if (item instanceof ItemTypeStringList) {
                dir = ((ItemTypeStringList) item).getValue();
            } else {
                dir = ((ItemTypeFile) item).getValue();
            }

            importData = new HashMap<String, ImportMetadata>();
            ImportMetadata meta;

            // find all Data (except Logbooks)
            checkImportData(importData, dir, new Adressen("adressen.efd"), ImportMetadata.TYPE_ADRESSEN, International.getString("Adreßliste"));
            checkImportData(importData, dir, new Synonyme("boote.efs"), ImportMetadata.TYPE_SYNONYME_BOOTE, International.getString("Boots-Synonymliste"));
            checkImportData(importData, dir, new Synonyme("mitglieder.efs"), ImportMetadata.TYPE_SYNONYME_MITGLIEDER, International.getString("Mitglieder-Synonymliste"));
            checkImportData(importData, dir, new Synonyme("ziele.efs"), ImportMetadata.TYPE_SYNONYME_ZIELE, International.getString("Ziel-Synonymliste"));
            checkImportData(importData, dir, new BootStatus("bootstatus.efdb"), ImportMetadata.TYPE_BOOTSTATUS, International.getString("Bootsstatus-Liste"));
            checkImportData(importData, dir, new Fahrtenabzeichen("fahrtenabzeichen.eff"), ImportMetadata.TYPE_FAHRTENABZEICHEN, International.onlyFor("Fahrtenabzeichen", "de"));
            checkImportData(importData, dir, new Gruppen("gruppen.efg"), ImportMetadata.TYPE_GRUPPEN, International.getString("Gruppenliste"));
            checkImportData(importData, dir, new Mannschaften("mannschaften.efm"), ImportMetadata.TYPE_MANNSCHAFTEN, International.getString("Mannschaften-Liste"));

            // find all Logbooks
            getAllLogbooks(importData, dir);

            // add items to GUI
            String[] datakeys = importData.keySet().toArray(new String[0]);
            Arrays.sort(datakeys);
            for (String fname : datakeys) {
                meta = importData.get(fname);
                item = new ItemTypeBoolean(IMPORTDATA + fname, meta.numRecords > 0,
                        IItemType.TYPE_PUBLIC, (meta.type != ImportMetadata.TYPE_FAHRTENBUCH ? "1" : "2"), fname);

                items.add(item);
                item = new ItemTypeLabel(IMPORTDATALABEL + fname,
                        IItemType.TYPE_PUBLIC, (meta.type != ImportMetadata.TYPE_FAHRTENBUCH ? "1" : "2"),
                        meta.toString());
                item.setColor(meta.numRecords < 0 ? Color.red : Color.black);
                item.setPadding(25, 0, 5);
                items.add(item);
            }
        }

        if (step == 2) {
            // #####################################################################
            // Items for Step 3
            // #####################################################################

            // remove all previous items for step 3
            int i = 0;
            while (i < items.size()) {
                if (items.get(i).getCategory().equals("3")) {
                    items.remove(i);
                } else {
                    i++;
                }
            }

            String[] keys  = importData.keySet().toArray(new String[0]);
            Arrays.sort(keys);
            for (String fname : keys) {
                ImportMetadata meta = importData.get(fname);
                if (meta.type == ImportMetadata.TYPE_FAHRTENBUCH) {
                    DataTypeDate dateFrom = new DataTypeDate();
                    dateFrom.setDate(meta.firstDate);
                    dateFrom.setDay(1);
                    dateFrom.setMonth(1);
                    DataTypeDate dateTo = new DataTypeDate();
                    dateTo.setDate(meta.lastDate);
                    dateTo.setDay(31);
                    dateTo.setMonth(12);
                    item = new ItemTypeLabel(LOGBOOKRANGELABEL + "l0" + fname,
                            IItemType.TYPE_PUBLIC, "3",
                            fname);
                    item.setPadding(0, 5, 0);
                    items.add(item);
                    item = new ItemTypeLabel(LOGBOOKRANGELABEL + "l1" + fname,
                            IItemType.TYPE_PUBLIC, "3",
                            meta.toString(false));
                    item.setPadding(25, 0, 0);
                    items.add(item);
                    item = new ItemTypeDate(LOGBOOKRANGEFROM + fname,
                            dateFrom,
                            IItemType.TYPE_PUBLIC, "3",
                            International.getString("Fahrtenbuch gültig für Fahrten ab"));
                    item.setPadding(25, 0, 0);
                    items.add(item);
                    item = new ItemTypeDate(LOGBOOKRANGETO + fname,
                            dateTo,
                            IItemType.TYPE_PUBLIC, "3",
                            International.getString("Fahrtenbuch gültig für Fahrten bis"));
                    items.add(item);
                    item.setPadding(25, 0, 0);
                }
            }
            
        }
    }

    private void checkImportData(HashMap<String,ImportMetadata> importData, String dir, DatenListe datenListe, int type, String description) {
        datenListe.dontEverWrite();
        ImportMetadata meta = new ImportMetadata(type, datenListe, description);
        String fname = datenListe.getFileName();
        if (EfaUtil.canOpenFile(dir+"daten/"+fname)) {
            datenListe.setFileName(dir+"daten/"+fname);
            if (datenListe.readFile()) {
                meta.numRecords = datenListe.countElements();
            }
        } else if (EfaUtil.canOpenFile(dir+"data/"+fname)) {
            datenListe.setFileName(dir+"data/"+fname);
            if (datenListe.readFile()) {
                meta.numRecords = datenListe.countElements();
            }
        }
        importData.put(datenListe.getFileName(), meta);
    }

    private void getAllLogbooks(HashMap<String,ImportMetadata> importData, String dirname) {
        try {
            File dir = new File(dirname);
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllLogbooks(importData, f.getAbsolutePath());
                } else {
                    if (f.getName().endsWith(".efb")) {
                        recursiveAddLogbook(importData, f.getAbsolutePath());
                    }
                }
            }
        } catch(Exception eignore0) {
        }
    }

    private void recursiveAddLogbook(HashMap<String, ImportMetadata> importData, String fname) {
        if (importData.get(fname) != null) {
            return;
        }
        Fahrtenbuch fb = new Fahrtenbuch(fname);
        fb.dontEverWrite();
        if (EfaUtil.canOpenFile(fb.getFileName()) && fb.readFile()) {
            ImportMetadata meta = new ImportMetadata(ImportMetadata.TYPE_FAHRTENBUCH, fb, International.getString("Fahrtenbuch"));
            DatenFelder d = fb.getCompleteFirst();
            while (d != null) {
                meta.numRecords++;
                if (d.get(Fahrtenbuch.DATUM).length() > 0
                        && (meta.firstDate == null || EfaUtil.secondDateIsAfterFirst(d.get(Fahrtenbuch.DATUM), meta.firstDate))) {
                    meta.firstDate = d.get(Fahrtenbuch.DATUM);
                }
                if (d.get(Fahrtenbuch.DATUM).length() > 0
                        && (meta.lastDate == null || EfaUtil.secondDateIsAfterFirst(meta.lastDate, d.get(Fahrtenbuch.DATUM)))) {
                    meta.lastDate = d.get(Fahrtenbuch.DATUM);
                }
                d = fb.getCompleteNext();
            }

            // Members, Boats, Destinations, Statistics
            Boote boote = new Boote(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fname),fb.getDaten().bootDatei));
            boote.dontEverWrite();
            if (EfaUtil.canOpenFile(boote.getFileName()) && boote.readFile()) {
                meta.numRecBoats = boote.countElements();
            }
            Mitglieder mitglieder = new Mitglieder(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fname),fb.getDaten().mitgliederDatei));
            mitglieder.dontEverWrite();
            if (EfaUtil.canOpenFile(mitglieder.getFileName()) && mitglieder.readFile()) {
                meta.numRecMembers = mitglieder.countElements();
            }
            Ziele ziele = new Ziele(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fname),fb.getDaten().zieleDatei));
            ziele.dontEverWrite();
            if (EfaUtil.canOpenFile(ziele.getFileName()) && ziele.readFile()) {
                meta.numRecDests = ziele.countElements();
            }
            StatSave stat = new StatSave(EfaUtil.makeFullPath(EfaUtil.getPathOfFile(fname),fb.getDaten().statistikDatei));
            stat.dontEverWrite();
            if (EfaUtil.canOpenFile(stat.getFileName()) && stat.readFile()) {
                meta.numRecStats = stat.countElements();
            }
            importData.put(fname, meta);

            recursiveAddLogbook(importData, fb.getPrevFb(true));
            recursiveAddLogbook(importData, fb.getNextFb(true));
        }

    }

    boolean checkInput(int direction) {
        boolean ok = super.checkInput(direction);
        if (!ok) {
            return false;
        }

        if (step == 0) {
            IItemType item = getItemByName(OLDEFADATADIR);
            String dir;
            if (item instanceof ItemTypeStringList) {
                dir = ((ItemTypeStringList)item).getValue();
            } else {
                dir = ((ItemTypeFile)item).getValue();
            }
            if (!(new File(dir)).isDirectory()) {
                Dialog.error(LogString.logstring_directoryDoesNotExist(dir, International.getString("Verzeichnis")));
                item.requestFocus();
                return false;
            } else {
                reinitializeItems();
            }
        }

        if (step == 2 && direction == 1) { // going from step 2 -> 3
            reinitializeItems();
        }

        return true;
    }

    void finishButton_actionPerformed(ActionEvent e) {
        super.finishButton_actionPerformed(e);
        ImportTask importTask = new ImportTask(importData);
        ProgressDialog progressDialog = new ProgressDialog(this, International.getString("Daten importieren"), importTask);
        importTask.start();
        progressDialog.showDialog();
    }

}
