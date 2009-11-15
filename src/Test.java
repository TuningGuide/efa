/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

import java.io.*;
import de.nmichael.efa.*;
import javax.swing.*;

// @i18n complete (needs no translation, just for developers)

public class Test {

  public static void main(String[] args) {
    int a = JOptionPane.showConfirmDialog(null,
                "Are you sure?",
                "Confirm close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    System.out.println("Choice: "+a);
  }
}

