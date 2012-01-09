/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.cli;

import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Logger;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class MenuData extends MenuBase {

    public static final String CMD_LIST = "list";
    public static final String CMD_SHOW = "show";

    protected StorageObject storageObject;
    protected String storageObjectDescription;
    protected Hashtable<Integer,DataRecord> lastListResult;

    public MenuData(CLI cli) {
        super(cli);
    }

    public void printHelpContext() {
        printUsage(CMD_LIST,        "[all|invisible|deleted]", "list " + storageObjectDescription);
        printUsage(CMD_SHOW,        "[index]", "show record");
    }

    public void list(String args) {
        if (storageObject == null) {
            return;
        }

        boolean all =       (args != null && args.trim().equalsIgnoreCase("all"));
        boolean invisible = (args != null && args.trim().equalsIgnoreCase("invisible"));
        boolean deleted =   (args != null && args.trim().equalsIgnoreCase("deleted"));
        boolean versionized = storageObject.data().getMetaData().isVersionized();
        boolean normal =    (!all && !invisible && !deleted && !versionized);
        long now = System.currentTimeMillis();
        lastListResult = new Hashtable<Integer,DataRecord>();
        int idx = 0;
        try {
            DataKeyIterator it = storageObject.data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                DataRecord r = storageObject.data().get(k);
                if (r != null) {
                    boolean show = all ||
                                   (invisible && r.getInvisible()) ||
                                   (deleted && r.getDeleted()) ||
                                   (versionized && !invisible && !deleted && r.isValidAt(now)) ||
                                   (normal && !r.getInvisible() && !r.getDeleted());
                    if (show) {
                        String name = r.getQualifiedName();
                        String notes = null;
                        if (r.getInvisible()) {
                            notes = (notes != null ? notes + " " : "") + "[invisible]";
                        }
                        if (r.getDeleted()) {
                            notes = (notes != null ? notes + " " : "") + "[deleted]";
                        }
                        if (versionized) {
                            notes = (notes != null ? notes + " " : "") + "[" + r.getValidRangeString() + "]";
                        }
                        String txt = (notes != null ? EfaUtil.getString(name, 40) + " " + notes : name);
                        cli.loginfo(EfaUtil.int2String(++idx, 5, false) + ": " + txt);
                        lastListResult.put(idx, r);
                    }
                }
                k = it.getNext();
            }
        } catch(Exception e) {
            Logger.log(e);
        }
    }

    public void show(String args) {
        if (storageObject == null) {
            return;
        }
        if (lastListResult == null || lastListResult.size() == 0) {
            cli.logerr("Please run a list command first.");
            return;
        }
        if (args == null || args.length() == 0) {
            printHelpContext();
            return;
        }
        int index = EfaUtil.string2int(args, -1);
        if (index < 0) {
            printHelpContext();
            return;
        }
        DataRecord r = lastListResult.get(index);
        if (r == null) {
            cli.logerr("Record with index "+index+" not found.");
            return;
        }
        Vector<IItemType> items = r.getGuiItems(cli.getAdminRecord());
        for (int i=0; items != null && i<items.size(); i++) {
            IItemType item = items.get(i);
            cli.loginfo(EfaUtil.getString(item.getName(), 25) + ": " + item.toString());
        }
    }

    public int runCommand(Stack<String> menuStack, String cmd, String args) {
        int ret = super.runCommand(menuStack, cmd, args);
        if (ret < 0) {
            if (cmd.equalsIgnoreCase(CMD_LIST)) {
                list(args);
                return CLI.RC_OK;
            }
            if (cmd.equalsIgnoreCase(CMD_SHOW)) {
                show(args);
                return CLI.RC_OK;
            }
            return CLI.RC_UNKNOWN_COMMAND;
        } else {
            return ret;
        }
    }

}
