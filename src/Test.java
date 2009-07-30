import java.io.*;
import de.nmichael.efa.*;
import javax.swing.*;

/**
 * <p>Title: efa - Elektronisches Fahrtenbuch</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Nicolas Michael
 * @version 1.0
 */

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

