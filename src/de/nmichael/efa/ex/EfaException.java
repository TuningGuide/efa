/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.ex;

import de.nmichael.efa.util.Logger;

public class EfaException extends Exception {

    String key;
    String msg;

    public EfaException(String key, String msg) {
        this.key = key;
        this.msg = msg;
    }

    public void log() {
        Logger.log(Logger.ERROR, key, msg);
    }

    public String toString() {
        return getClass().getCanonicalName()+": " + key + " (" + msg + ")";
    }

    public String getMessage() {
        return msg;
    }

}
