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
import de.nmichael.efa.util.*;
import javax.swing.*;

// @i18n complete (needs no translation, just for developers)

public class Test {

  public static void main(String[] args) {
//    int a = JOptionPane.showConfirmDialog(null,
//                "Are you sure?",
//                "Confirm close",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.QUESTION_MESSAGE);
//    System.out.println("Choice: "+a);
    System.out.println(International.getLanguageID());
    System.out.println(International.getString("C-Gig"));
System.out.println(International.getMessage("Da die Nachrichtendatei ihre maximale Größe erreicht hat, "+
                    "hat efa soeben alle alten Nachrichten aussortiert.\n"+
                    "{count} alte Nachrichten wurden in die Datei {bakfilename}"+
                    " verschoben.",42, "bakfilebla.txt"));
    System.out.println(International.getMessage("Der Bootsstatus wurde geändert. Soll der neue Bootsstatus auch für "+
                               "{number_of_boats,choice,1#das Boot|2#:die Boote}" +
                               " {names} übernommen werden?",
                               1,"Titanic"));
    System.out.println(International.getMessage("Bitte melde diesen Fehler an: {efaemail}", "jemand@hier.de"));
  }
}

