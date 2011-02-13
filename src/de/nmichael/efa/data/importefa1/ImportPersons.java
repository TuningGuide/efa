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

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.efa1.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.config.EfaTypes;
import java.util.*;

public class ImportPersons extends ImportBase {

    private Mitglieder mitglieder;
    private ProjectRecord logbookRec;

    public ImportPersons(ImportTask task, Mitglieder mitglieder, ProjectRecord logbookRec) {
        super(task);
        this.mitglieder = mitglieder;
        this.logbookRec = logbookRec;
    }

    public String getDescription() {
        return International.getString("Personen");
    }

    private boolean isIdentical(Object o, String s) {
        if (o == null && (s == null || s.length() == 0)) {
            return true;
        }
        if (o == null || s == null) {
            return false;
        }
        return (o.toString().equals(s));
    }

    private boolean isChanged(PersonRecord r, DatenFelder d) {
        if (!isIdentical(r.getFirstName(), d.get(Mitglieder.VORNAME))) {
            return true;
        }
        if (!isIdentical(r.getLastName(), d.get(Mitglieder.NACHNAME))) {
            return true;
        }
        if (!isIdentical(r.getGender(), d.get(Mitglieder.GESCHLECHT))) {
            return true;
        }
        if (!isIdentical(r.getBirthday(), d.get(Mitglieder.JAHRGANG))) {
            return true;
        }
        if (!isIdentical(r.getAssocitation(), d.get(Mitglieder.VEREIN))) {
            return true;
        }
        if (!isIdentical(r.getStatus(), d.get(Mitglieder.STATUS))) {
            return true;
        }
        if (!isIdentical(r.getMembershipNo(), d.get(Mitglieder.MITGLNR))) {
            return true;
        }
        if (!isIdentical(r.getPassword(), d.get(Mitglieder.PASSWORT))) {
            return true;
        }
        if (!isIdentical(r.getDisability(), Boolean.toString(d.get(Mitglieder.BEHINDERUNG).equals("+")) )) {
            return true;
        }
        if (!isIdentical(r.getExcludeFromCompetition(), Boolean.toString(!d.get(Mitglieder.KMWETT_MELDEN).equals("+")))) {
            return true;
        }
        if (!isIdentical(r.getInputShortcut(), d.get(Mitglieder.ALIAS))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse1(), d.get(Mitglieder.FREI1))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse2(), d.get(Mitglieder.FREI2))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse3(), d.get(Mitglieder.FREI3))) {
            return true;
        }
        return false;
    }

    public boolean runImport() {
        try {
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), mitglieder.getFileName()));

            Persons persons = Daten.project.getPersons(true);
            long validFrom = DataAccess.getTimestampFromDate(logbookRec.getStartDate());

            DatenFelder d = mitglieder.getCompleteFirst();
            String[] IDX = new String[] { PersonRecord.FIRSTNAME, PersonRecord.LASTNAME, PersonRecord.ASSOCIATION };
            while (d != null) {
                // First search, whether we have imported this person already
                PersonRecord r = null;
                DataKey[] keys = persons.data().getByFields(IDX,
                        new String[] {
                                        (d.get(Mitglieder.VORNAME).length() > 0 ? d.get(Mitglieder.VORNAME) : null),
                                        (d.get(Mitglieder.NACHNAME).length() > 0 ? d.get(Mitglieder.NACHNAME) : null),
                                        (d.get(Mitglieder.VEREIN).length() > 0 ? d.get(Mitglieder.VEREIN) : null) });
                if (keys != null && keys.length > 0) {
                    // We've found one or more persons with same Name and Association.
                    // Since we're importing data from efa1, these persons are all identical, i.e. have the same ID.
                    // Therefore their key is identical, so we can just retrieve one person record with keys[0], which
                    // is valid for this logbook.
                    r = (PersonRecord)persons.data().getValidAt(keys[0], validFrom);
                }

                if (r == null || isChanged(r, d)) {
                    r = persons.createPersonRecord((r != null ? r.getId() : UUID.randomUUID()));

                    if (d.get(Mitglieder.VORNAME).length() > 0) {
                        r.setFirstName(d.get(Mitglieder.VORNAME));
                    }
                    if (d.get(Mitglieder.NACHNAME).length() > 0) {
                        r.setLastName(d.get(Mitglieder.NACHNAME));
                    }
                    // TITLE does not exist in efa1, so we leave it empty
                    if (d.get(Mitglieder.GESCHLECHT).length() > 0) {
                        String gender = d.get(Mitglieder.GESCHLECHT);
                        if (gender.equals(EfaTypes.TYPE_GENDER_MALE) ||
                            gender.equals(EfaTypes.TYPE_GENDER_FEMALE)) {
                            r.setGender(gender);
                        }
                    }
                    if (d.get(Mitglieder.JAHRGANG).length() > 0) {
                        DataTypeDate birthday = new DataTypeDate();
                        birthday.setYear(d.get(Mitglieder.JAHRGANG));
                        r.setBirthday(birthday);
                    }
                    if (d.get(Mitglieder.VEREIN).length() > 0) {
                        r.setAssocitation(d.get(Mitglieder.VEREIN));
                    }
                    if (d.get(Mitglieder.STATUS).length() > 0) {
                        r.setStatus(d.get(Mitglieder.STATUS));
                    }
                    String address = task.getAddress(r.getFirstName() + " " + r.getLastName());
                    if (address != null && address.length() > 0) {
                        r.setAddressAdditional(address); // there is no such thing as an address format in efa1,
                                                         // so we just put the data into the additional address field.
                    }
                    if (d.get(Mitglieder.MITGLNR).length() > 0) {
                        r.setMembershipNo(d.get(Mitglieder.MITGLNR));
                    }
                    if (d.get(Mitglieder.PASSWORT).length() > 0) {
                        r.setPassword(d.get(Mitglieder.PASSWORT));
                    }
                    // EXTERNALID does not exist in efa1, so we leave it empty
                    if (d.get(Mitglieder.BEHINDERUNG).equals("+")) {
                        r.setDisability(true);
                    }
                    if (!d.get(Mitglieder.KMWETT_MELDEN).equals("+")) {
                        r.setExcludeFromCompetition(true);
                    }
                    if (d.get(Mitglieder.ALIAS).length() > 0) {
                        r.setInputShortcut(d.get(Mitglieder.ALIAS));
                    }
                    if (d.get(Mitglieder.FREI1).length() > 0) {
                        r.setFreeUse1(d.get(Mitglieder.FREI1));
                    }
                    if (d.get(Mitglieder.FREI2).length() > 0) {
                        r.setFreeUse1(d.get(Mitglieder.FREI2));
                    }
                    if (d.get(Mitglieder.FREI3).length() > 0) {
                        r.setFreeUse1(d.get(Mitglieder.FREI3));
                    }
                    try {
                        persons.data().addValidAt(r, validFrom);
                        logInfo(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                    } catch(Exception e) {
                        logError(International.getMessage("Import von Eintrag fehlgeschlagen (Duplikat?): {entry}", r.toString()));
                    }
                } else {
                    logInfo(International.getMessage("Identischer Eintrag: {entry}", r.toString()));
                }
                d = mitglieder.getCompleteNext();
            }
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), mitglieder.getFileName()));
            logError(e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
