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

import java.util.Stack;

public class MenuMain extends MenuBase {
    
    private CLI cli;

    public MenuMain(CLI cli) {
        super(cli);
        this.cli = cli;
    }

    public void printHelpContext() {
        printUsage(CLI.MENU_BOATS,        "", "boat administration");
        printUsage(CLI.MENU_PERSONS,      "", "person administration");
        printUsage(CLI.MENU_DESTINATIONS, "", "destination administration");
        printUsage(CLI.MENU_STATISTICS  , "", "create statistics");
        printUsage(CLI.MENU_BACKUP      , "", "create backups");
    }
    
    public int runCommand(Stack<String> menuStack, String cmd, String args) {
        int ret = super.runCommand(menuStack, cmd, args);
        if (ret < 0) {
            if (cmd.equalsIgnoreCase(CLI.MENU_BOATS)) {
                if (!cli.getAdminRecord().isAllowedEditBoats()) {
                    cli.logerr("You don't have permission to access this function.");
                    return CLI.RC_NO_PERMISSION;
                }
                menuStack.push(CLI.MENU_BOATS);
                return runCommandWithArgs(args);
            }
            if (cmd.equalsIgnoreCase(CLI.MENU_PERSONS)) {
                if (!cli.getAdminRecord().isAllowedEditPersons()) {
                    cli.logerr("You don't have permission to access this function.");
                    return CLI.RC_NO_PERMISSION;
                }
                menuStack.push(CLI.MENU_PERSONS);
                return runCommandWithArgs(args);
            }
            if (cmd.equalsIgnoreCase(CLI.MENU_DESTINATIONS)) {
                if (!cli.getAdminRecord().isAllowedEditDestinations()) {
                    cli.logerr("You don't have permission to access this function.");
                    return CLI.RC_NO_PERMISSION;
                }
                menuStack.push(CLI.MENU_DESTINATIONS);
                return runCommandWithArgs(args);
            }
            if (cmd.equalsIgnoreCase(CLI.MENU_STATISTICS)) {
                if (!cli.getAdminRecord().isAllowedEditStatistics()) {
                    cli.logerr("You don't have permission to access this function.");
                    return CLI.RC_NO_PERMISSION;
                }
                menuStack.push(CLI.MENU_STATISTICS);
                return runCommandWithArgs(args);
            }
            if (cmd.equalsIgnoreCase(CLI.MENU_BACKUP)) {
                if (!cli.getAdminRecord().isAllowedCreateBackup() &&
                    !cli.getAdminRecord().isAllowedRestoreBackup()) {
                    cli.logerr("You don't have permission to access this function.");
                    return CLI.RC_NO_PERMISSION;
                }
                menuStack.push(CLI.MENU_BACKUP);
                return runCommandWithArgs(args);
            }
            return CLI.RC_UNKNOWN_COMMAND;
        } else {
            return CLI.RC_OK;
        }
    }

    private int runCommandWithArgs(String args) {
        if (args == null || args.length() == 0) {
            return CLI.RC_OK;
        }
        int ret = cli.runCommandInCurrentMenu(args);
        //if (cli.runCommandInCurrentMenu(args)) {
        //    cli.runCommandInCurrentMenu(CMD_EXIT); // up one menu again
        //    return true;
        //}
        return ret;
    }

}
