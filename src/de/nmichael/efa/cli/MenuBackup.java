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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.Backup;
import de.nmichael.efa.util.EfaUtil;
import java.util.Stack;
import java.util.Vector;

public class MenuBackup extends MenuBase {

    public static final String CMD_BACKUP = "create";

    public MenuBackup(CLI cli) {
        super(cli);
    }

    public void printHelpContext() {
        printUsage(CMD_BACKUP, "[project|config|all] [directory]", "create backup");
    }

    private void backup(String args) {
        if (args == null || args.length() == 0) {
            printHelpContext();
            return;
        }
        Vector<String> options = EfaUtil.split(args, ' ');
        if (options == null || options.size() < 1 || options.size() > 2) {
            printHelpContext();
            return;
        }
        
        boolean backupProject = false;
        boolean backupConfig = false;
        String backupDir = Daten.efaBakDirectory;
        for (int i=0; i<options.size(); i++) {
            String opt = options.get(i).trim();
            switch(i) {
                case 0:
                    if (opt.equalsIgnoreCase("project")) {
                        backupProject = true;
                    }
                    if (opt.equalsIgnoreCase("config")) {
                        backupConfig = true;
                    }
                    if (opt.equalsIgnoreCase("all")) {
                        backupProject = true;
                        backupConfig = true;
                    }
                    break;
                case 1:
                    backupDir = opt;
                    break;
            }
        }
        if (!backupProject && !backupConfig) {
            printHelpContext();
            return;
        }

        Backup backup = new Backup(backupDir, backupProject, backupConfig);
        backup.runBackup();
    }

    public boolean runCommand(Stack<String> menuStack, String cmd, String args) {
        if (!super.runCommand(menuStack, cmd, args)) {
            if (cmd.equalsIgnoreCase(CMD_BACKUP)) {
                backup(args);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
