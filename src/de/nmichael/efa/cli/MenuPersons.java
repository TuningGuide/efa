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

public class MenuPersons extends MenuData {

    public MenuPersons(CLI cli) {
        super(cli);
        this.storageObject = cli.getPersistence(Persons.class, Project.STORAGEOBJECT_PERSONS);
        this.storageObjectDescription = "persons";
    }

    public boolean runCommand(Stack<String> menuStack, String cmd, String args) {
        if (!super.runCommand(menuStack, cmd, args)) {
            return false;
        } else {
            return true;
        }
    }

}
