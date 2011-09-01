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

import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.util.Logger;
import java.util.Stack;

public class MenuBoats extends MenuData {

    public MenuBoats(CLI cli) {
        super(cli);
        this.storageObject = cli.getPersistence(Boats.class, Project.STORAGEOBJECT_BOATS);
        this.storageObjectDescription = "boats";
    }

    public boolean runCommand(Stack<String> menuStack, String cmd, String args) {
        if (!super.runCommand(menuStack, cmd, args)) {
            return false;
        } else {
            return true;
        }
    }

}
