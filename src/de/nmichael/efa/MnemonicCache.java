/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa;

import java.util.Hashtable;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author nick
 */

class MnemonicCache {

    private Hashtable windows = new Hashtable();

    public void put(Window w, char c, AbstractButton b, JLabel l, boolean explicit) {
        Hashtable wc = (Hashtable) windows.get(w);
        if (wc == null) {
            wc = new Hashtable();
        }
        wc.put(Character.valueOf(c), new MnemonicHolder(b, l, explicit));
        windows.put(w, wc);
    }

    public MnemonicHolder get(Window w, char c) {
        Hashtable wc = (Hashtable) windows.get(w);
        if (wc == null) {
            return null;
        }
        return (MnemonicHolder) wc.get(Character.valueOf(c));
    }

    public void clear(Window w) {
        windows.remove(w);
    }
}