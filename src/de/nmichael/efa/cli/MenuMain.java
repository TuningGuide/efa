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
    }

    public void printHelpContext() {
        printUsage(CLI.MENU_BOATS,        "", "boat administration");
        printUsage(CLI.MENU_PERSONS,      "", "person administration");
        printUsage(CLI.MENU_DESTINATIONS, "", "destination administration");
    }
    
    public boolean runCommand(Stack<String> menuStack, String cmd, String args) {
        if (!super.runCommand(menuStack, cmd, args)) {
            if (cmd.equalsIgnoreCase(CLI.MENU_BOATS)) {
                menuStack.push(CLI.MENU_BOATS);
                return true;
            }
            if (cmd.equalsIgnoreCase(CLI.MENU_PERSONS)) {
                menuStack.push(CLI.MENU_PERSONS);
                return true;
            }
            if (cmd.equalsIgnoreCase(CLI.MENU_DESTINATIONS)) {
                menuStack.push(CLI.MENU_DESTINATIONS);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

}
