/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.data;

import java.util.*;

/**
 *
 * @author nick
 */
public class DataRecord {

    private Hashtable data = new Hashtable();

    public void set(String fieldName, Object data) {
        this.data.put(fieldName, data);
    }

    public Object get(String fieldName) {
        return this.data.get(fieldName);
    }

}
