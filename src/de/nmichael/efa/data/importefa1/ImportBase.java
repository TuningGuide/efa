/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.importefa1;

import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.util.*;
import java.util.*;


public abstract class ImportBase {

    protected ImportTask task;
    int cntWarning = 0;
    int cntError = 0;

    public ImportBase(ImportTask task) {
        this.task = task;
    }

    public abstract String getDescription();
    public abstract boolean runImport();

    protected void logInfo(String s) {
        task.logInfo("INFO    - " + getDescription()+ " - " + s + "\n", true, true);
    }

    protected void logDetail(String s) {
        task.logInfo("DETAIL  - " + getDescription()+ " - " + s + "\n", false, true);
    }

    protected void logWarning(String s) {
        task.logInfo("WARNING - " + getDescription()+ " - " + s + "\n", true, true);
        cntWarning++;
    }

    protected void logError(String s) {
        task.logInfo("ERROR   - " + getDescription()+ " - " + s + "\n", true, true);
        cntError++;
    }

    public int getWarningCount() {
        return cntWarning;
    }

    public int getErrorCount() {
        return cntError;
    }

    protected UUID findPerson(Persons persons, String[] IDX, String name, boolean warnIfNotFound) {
        name = name.trim();
        if (name.length() == 0) {
            return null;
        }
        String[] qname = PersonRecord.tryGetNameAndAffix(name);
        return findPerson(persons, IDX, qname[0], qname[1], warnIfNotFound);
    }

    protected UUID findPerson(Persons persons, String[] IDX, String name, String affix, boolean warnIfNotFound) {
        try {
            DataKey[] keys = persons.data().getByFields(IDX,
                    new String[]{
                        (name != null && name.length() > 0 ? name : null),
                        (affix != null && affix.length() > 0 ? affix : null)});
            if (keys != null && keys.length > 0) {
                return (UUID) keys[0].getKeyPart1();
            }
        } catch(Exception e) {
        }
        if (warnIfNotFound) {
            logWarning(International.getMessage("Person {person} nicht in der Mitgliederliste gefunden.",
                                    name + (affix.length() > 0 ? " ("+affix+")" : "")));
        }
        return null;
    }

    protected UUID findBoat(Boats boats, String[] IDX, String name, boolean warnIfNotFound) {
        name = name.trim();
        if (name.length() == 0) {
            return null;
        }
        String[] qname = BoatRecord.tryGetNameAndAffix(name);
        return findBoat(boats, IDX, qname[0], qname[1], warnIfNotFound);
    }

    protected UUID findBoat(Boats boats, String[] IDX, String boatName, String nameAffix, boolean warnIfNotFound) {
        try {
            DataKey[] keys = boats.data().getByFields(IDX,
                    new String[]{
                        (boatName != null && boatName.length() > 0 ? boatName : null),
                        (nameAffix != null && nameAffix.length() > 0 ? nameAffix : null)});
            if (keys != null && keys.length > 0) {
                return (UUID) keys[0].getKeyPart1();
            }
        } catch(Exception e) {
        }
        if (warnIfNotFound) {
            logWarning(International.getMessage("Boot {boat} nicht in der Bootsliste gefunden.",
                                    boatName + (nameAffix != null && nameAffix.length() > 0 ? " ("+nameAffix+")" : "")));
        }
        return null;
    }

    protected UUID findDestination(Destinations destinations, String[] IDX, String name, boolean warnIfNotFound) {
        name = name.trim();
        if (name.length() == 0) {
            return null;
        }
        try {
            DataKey[] keys = destinations.data().getByFields(IDX,
                    new String[]{ name });
            if (keys != null && keys.length > 0) {
                return (UUID) keys[0].getKeyPart1();
            }
        } catch(Exception e) {
        }
        if (warnIfNotFound) {
            logWarning(International.getMessage("Ziel {destination} nicht in der Zielliste gefunden.",
                                    name));
        }
        return null;
    }

}
