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
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

// @i18n complete
public class OpenProjectOrLogbookDialog extends BaseDialog implements IItemListener {

    public enum Type {
        project,
        logbook
    }

    private String name;
    private Type type;
    private String[] keys;
    private ItemTypeHtmlList list;

    public OpenProjectOrLogbookDialog(Frame parent, Type type) {
        super(parent, 
                (type == Type.project ? 
                    International.getString("Projekt öffnen") :
                    International.getString("Fahrtenbuch öffnen")),
                International.getStringWithMnemonic("Abbruch"));
        this.type = type;
    }

    public OpenProjectOrLogbookDialog(JDialog parent, Type type) {
        super(parent,
                (type == Type.project ?
                    International.getString("Projekt öffnen") :
                    International.getString("Fahrtenbuch öffnen")),
                International.getStringWithMnemonic("Abbruch"));
        this.type = type;
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private Hashtable<String,String> getProjects() {
        Hashtable<String,String> items = new Hashtable<String,String>();
        try {
            File dir = new File(Daten.efaDataDirectory);
            if (dir.isDirectory()) {
                String[] files = dir.list();
                for (int i=0; files != null && i<files.length; i++) {
                    if (files[i] != null && files[i].length() > 0 &&
                        files[i].toLowerCase().endsWith("." + Project.DATATYPE)) {
                        int pos = files[i].lastIndexOf(".");
                        String name = files[i].substring(0,pos);
                        try {
                            Project p = new Project(name);
                            p.open(false);
                            StringBuffer description = new StringBuffer();
                            description.append("<b>" + International.getString("Projekt") + ":</b> <b style=\"color:blue\">" + name + "</b><br>");
                            if (p.getProjectDescription() != null) {
                                description.append(p.getProjectDescription()+"<br>");
                            }
                            String[] logbooks = p.getAllLogbookNames();
                            if (logbooks != null) {
                                description.append(International.getString("Fahrtenbücher") + ": ");
                                for (int j=0; j<logbooks.length; j++) {
                                    description.append( (j>0 ? ", " : "") + logbooks[j]);
                                }
                            }
                            items.put(name, description.toString());
                        } catch(Exception e1) {
                        }
                    }
                }
            }
        } catch(Exception e) {
        }
        return items;
    }

    private Hashtable<String,String> getLogbooks() {
        Hashtable<String,String> items = new Hashtable<String,String>();
        if (Daten.project != null) {
            String[] logbooks = Daten.project.getAllLogbookNames();
            for (int i=0; logbooks != null && i<logbooks.length; i++) {
                ProjectRecord r = Daten.project.getLoogbookRecord(logbooks[i]);
                if (r != null) {
                    String description = "<b>" + International.getString("Fahrtenbuch") + ":</b> <b style=\"color:blue\">" + logbooks[i] + "</b><br>" +
                            (r.getDescription() != null ? r.getDescription() : "");
                    items.put(logbooks[i], description);
                }
            }
        }
        return items;
    }

    protected void iniDialog() throws Exception {
        // create GUI items
        mainPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel();
        if (type == Type.project) {
            label.setText(International.getString("vorhandene Projekte"));
        }
        if (type == Type.logbook) {
            label.setText(International.getString("vorhandene Fahrtenbücher"));
        }
        mainPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));

        list = new ItemTypeHtmlList("LIST", null, null, null, IItemType.TYPE_PUBLIC, null, label.getText());
        String[] actions = {
            International.getString("Öffnen"),
            International.getString("Löschen")
        };
        list.setPopupActions(actions);
        list.registerItemListener(this);
        list.setFieldGrid(1, 4, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        list.setPadding(10, 10, 0, 10);
        list.displayOnGui(_parent, mainPanel, 0, 1);

        JButton newButton = new JButton();
        Mnemonics.setButton(this, newButton, International.getString("Neu"));
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newButton_actionPerformed(e);
            }
        });
        mainPanel.add(newButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0));
        JButton openButton = new JButton();
        Mnemonics.setButton(this, openButton, International.getString("Öffnen"));
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openButton_actionPerformed(e);
            }
        });
        mainPanel.add(openButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0));
        JButton deleteButton = new JButton();
        Mnemonics.setButton(this, deleteButton, International.getString("Löschen"));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteButton_actionPerformed(e);
            }
        });
        mainPanel.add(deleteButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0));

        updateGui();
    }

    public void updateGui() {
        Hashtable<String,String> items = null;

        if (type == Type.project) {
            items = getProjects();
        }
        if (type == Type.logbook) {
            items = getLogbooks();
        }

        keys = items.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        list.setValues(keys, items);
    }

    public void itemListenerAction(IItemType item, AWTEvent event) {
        if (item != null && event != null && item == list) {
            if (event instanceof ActionEvent) {
                ActionEvent e = (ActionEvent)event;
                String cmd = e.getActionCommand();
                if (cmd != null && cmd.equals(EfaMouseListener.EVENT_MOUSECLICKED_2x)) {
                    openButton_actionPerformed(e);
                }
                if (cmd != null && cmd.startsWith(EfaMouseListener.EVENT_POPUP_CLICKED)) {
                    int id = EfaUtil.string2date(cmd, -1, -1, -1).tag;
                    switch(id) {
                        case 0:
                            openButton_actionPerformed(e);
                            break;
                        case 1:
                            deleteButton_actionPerformed(e);
                            break;
                    }
                    
                }
            }
        }
    }

    void newButton_actionPerformed(ActionEvent e) {
        if (type == Type.project) {
            NewProjectDialog dlg = new NewProjectDialog(this);
            dlg.createNewProjectAndLogbook();
            updateGui();
            return;
        }
        if (type == Type.logbook) {
            NewLogbookDialog dlg = new NewLogbookDialog(this);
            dlg.newLogbookDialog();
            updateGui();
            return;
        }
    }

    void openButton_actionPerformed(ActionEvent e) {
        name = list.getValueFromField();
        if (name != null) {
            closeButton_actionPerformed(e);
        }
    }

    void deleteButton_actionPerformed(ActionEvent e) {
        Dialog.error("Not yet implemented"); // @todo
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        super.closeButton_actionPerformed(e);
    }

    public String openDialog() {
        showDialog();
        return name;
    }
    
}
