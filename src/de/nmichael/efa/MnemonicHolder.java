/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author nick
 */

class MnemonicHolder {

    private AbstractButton b;
    private JLabel l;
    private boolean explicit;

    public MnemonicHolder(AbstractButton b, JLabel l, boolean explicit) {
        this.b = b;
        this.l = l;
        this.explicit = explicit;
    }

    public boolean clearMnemonics() {
        if (explicit) {
            return false;
        }
        if (b != null) {
            b.setMnemonic(0x0);
        }
        if (l != null) {
            l.setDisplayedMnemonic(0x0);
        }
        return true;
    }
}

