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
        String firstName = EfaUtil.getVorname(name);
        String lastName = EfaUtil.getNachname(name);
        String club = EfaUtil.getVerein(name);
        return findPerson(persons, IDX, firstName, lastName, club, warnIfNotFound);
    }

    protected UUID findPerson(Persons persons, String[] IDX, String firstName, String lastName, String club, boolean warnIfNotFound) {
        try {
            DataKey[] keys = persons.data().getByFields(IDX,
                    new String[]{
                        (firstName.length() > 0 ? firstName : null),
                        (lastName.length() > 0 ? lastName : null),
                        (club.length() > 0 ? club : null)});
            if (keys != null && keys.length > 0) {
                return (UUID) keys[0].getKeyPart1();
            }
        } catch(Exception e) {
        }
        if (warnIfNotFound) {
            logWarning(International.getMessage("Person {person} nicht in der Mitgliederliste gefunden.",
                                    EfaUtil.getFullName(firstName, lastName, club, true)));
        }
        return null;
    }

    protected UUID findBoat(Boats boats, String[] IDX, String name, boolean warnIfNotFound) {
        name = name.trim();
        if (name.length() == 0) {
            return null;
        }
        String boatName = EfaUtil.getName(name);
        String clubName = EfaUtil.getVerein(name);
        return findBoat(boats, IDX, boatName, clubName, warnIfNotFound);
    }

    protected UUID findBoat(Boats boats, String[] IDX, String boatName, String clubName, boolean warnIfNotFound) {
        try {
            DataKey[] keys = boats.data().getByFields(IDX,
                    new String[]{
                        (boatName.length() > 0 ? boatName : null),
                        (clubName.length() > 0 ? clubName : null)});
            if (keys != null && keys.length > 0) {
                return (UUID) keys[0].getKeyPart1();
            }
        } catch(Exception e) {
        }
        if (warnIfNotFound) {
            logWarning(International.getMessage("Boot {boat} nicht in der Bootsliste gefunden.",
                                    boatName + (clubName.length() > 0 ? " ("+clubName+")" : "")));
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
