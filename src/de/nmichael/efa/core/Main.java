/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import javax.swing.UIManager;
import java.awt.*;

// @i18n complete
public class Main extends Program {

    private String fb = null;

    public Main(String[] args) {
        super(args);
        Daten.initialize(Daten.APPL_EFA);

        EfaFrame frame = new EfaFrame(fb);
        frame.pack();
        //Center the window
        Dimension frameSize = frame.getSize();
        if (frameSize.height > Dialog.screenSize.height) {
            frameSize.height = Dialog.screenSize.height;
        }
        if (frameSize.width > Dialog.screenSize.width) {
            frameSize.width = Dialog.screenSize.width;
        }
        Dialog.setDlgLocation(frame);
        frame.setVisible(true);
        Daten.iniSplashScreen(false);
        frame.userInteractionsUponStart();
    }

    public void printUsage(String wrongArgument) {
        super.printUsage(wrongArgument);
        printOption("-open <file>", International.getString("Fahrtenbuch <file> öffnen"));
        System.exit(0);
    }

    public void checkArgs(String[] args) {
        super.checkArgs(args);
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue; // argument already handled by super class
            }
            if (args[i].equals("-open") && i + 1 < args.length) {
                args[i] = null;
                fb = args[++i];
                args[i] = null;
                continue;
            }
        }
        checkRemainingArgs(args);
    }

    public static void main(String[] args) {
        new Main(args);
    }

}
