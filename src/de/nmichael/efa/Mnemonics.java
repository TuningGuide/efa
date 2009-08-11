/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author nick
 */
public class Mnemonics {

  // Mnemonics
  public static Hashtable mnemonics = new Hashtable();
  public static Hashtable cachedMnemonics = new Hashtable();

  // Key Code
//  public static KeyEvent keyCode = new KeyEvent()


  private static char getCachedMnemonic(Class c, String s) {
      if (c == null || s == null) return 0x0;
      Character m = (Character)cachedMnemonics.get(c.toString()+":"+s);
      if (m == null) {
          return 0x0;
      } else {
          return m.charValue();
      }
  }

  private static void setCachedMnemonics(Class c, String s, char m) {
      if (c == null || s == null || m == 0x0) return;
      cachedMnemonics.put(c.toString()+":"+s, new Character(m));
  }

  private static boolean allowedMnemonic(char c) {
      return (c != '&' &&
              (Character.isLetter(c) || Character.isDigit(c)) );
  }

  private static char getMnemonic(Class c, String s) {
      if (c == null || s == null) return 0x0;
      s = s.toLowerCase();

      // cached Mnemonic?
      char m = getCachedMnemonic(c, s);
      if (m != 0x0) {
          return m;
      }
      
      // Mnemonics already in use
      String mlist = (String)mnemonics.get(c);

      // explicit Mnemonic?
      m = getMnemonic(s);
      if (m != 0x0) {
          // @todo: When Mnemonic already in use, grab this mnemonic if it is in use by a field without explicit assignment!
          if (mlist != null && mlist.indexOf(m)>=0) {
              return 0x0;
          }
      }

      // generic Mnemonic?
      if (m == 0x0) {
          for (int i=0; i<s.length(); i++) {
              if (allowedMnemonic(s.charAt(i)) && (mlist == null || mlist.indexOf(s.charAt(i))<0)) {
                  // charAt(i) is new mnemonic
                  m = s.charAt(i);
                  break;
              }
          }
      }

      // Mnemonic found=
      if (m != 0x0) {
          mlist = (mlist == null ? "" : mlist) + m;
          setCachedMnemonics(c, s, m);
          mnemonics.put(c, mlist);
      }

      return m;
  }

  private static char getMnemonicCode(Window w, String s) {
      char c = getMnemonic(w.getClass(),s);
      // @todo: Convert Character into KeyCode!
      return c;
  }

  /**
   * Sets the displayed text and mnemonic for a label.
   * If s contains a mnemonic marked with "&", this mnemonic is being used
   * (regardless whether such a mnemonic is already being used inside this frame!).
   * Otherwise, this method determines a unique mnemonic for this label inside this
   * frame by itself, if any unique mnemonics are available.
   * @todo Shall a specified mnemonic really be used regardless whether the same
   * mnemonic has already been used inside this frame? If not, what is the alternative?
   * Using a different, automatically chosen mnemonic could cause other labels/buttons to
   * not receive their desired mnemonics any more. Probably it would be the best to not
   * set any mnemonic at all in this case ...? Even better would be if explicitly
   * specified mnemonics are preferred over automatically chosen one, even if they appear
   * later in the source code! But how could this best be realized??
   *
   * @param w the Window containing this label
   * @param l the label
   * @param s the text to be displayed
   */
  public static void setLabel(Window w, JLabel l, String s) {
      if (w == null || l == null || s == null) return;
      l.setText(stripMnemonics(s));
      // @todo: replace char by int (see getMnemonicCode)
      char key = getMnemonicCode(w, s);
      if (key != 0x0) {
          l.setDisplayedMnemonic(key);
      }
  }

  /**
   * Sets the displayed text and mnemonic for a label.
   * If s contains a mnemonic marked with "&", this mnemonic is being used
   * (regardless whether such a mnemonic is already being used inside this frame!).
   * Otherwise, this method determines a unique mnemonic for this label inside this
   * frame by itself, if any unique mnemonics are available.
   * @todo Shall a specified mnemonic really be used regardless whether the same
   * mnemonic has already been used inside this frame? If not, what is the alternative?
   * Using a different, automatically chosen mnemonic could cause other labels/buttons to
   * not receive their desired mnemonics any more. Probably it would be the best to not
   * set any mnemonic at all in this case ...? Even better would be if explicitly
   * specified mnemonics are preferred over automatically chosen one, even if they appear
   * later in the source code! But how could this best be realized??
   *
   * @param w the Window containing this label
   * @param l the label
   * @param s the text to be displayed
   */
  public static void setButton(Window w, AbstractButton b, String s) {
      if (w == null || b == null || s == null) return;
      b.setText(stripMnemonics(s));
      // @todo: replace char by int (see getMnemonicCode)
      char key = getMnemonicCode(w, s);
      if (key != 0x0) {
          b.setMnemonic(key);
      }
  }

  /**
   * Checks whether the supplied string contains mnemonics (masked as "&").
   * @param s the string
   * @return true, if the string contains mnemonics
   */
  public static boolean containsMnemonics(String s) {
      return (getMnemonic(s) != 0x0);
  }

  /**
   * Checks whether the supplied string contains mnemonics (masked as "&") or
   * ampassants (masked as "&&").
   * @param s the string
   * @return true, if the string contains mnemonics or ampassants
   */
  public static boolean containsMnemonicsOrAmp(String s) {
      return (s.indexOf("&") >= 0);
  }

  /**
   * Retrieves the mnemonic masked with "&" from the supplied string.
   * @param s the string
   * @return the mnemonic
   */
  public static char getMnemonic(String s) {
      if (s == null) {
          return 0x0;
      }
      s = s.toLowerCase();
      int pos = 0;
      do {
          pos = s.indexOf("&",pos);
          if (pos < 0) {
              break;
          }
          if (s.length()>pos+1 && allowedMnemonic(s.charAt(pos+1))) {
              return s.charAt(pos+1);
          } else {
              pos += 2;
          }
      } while (pos < s.length());
      return 0x0;
  }

  /**
   * Strinps any mnemonics from the supplied string and transformes escaped
   * ampassants ("&&") into real ampassants ("&").
   * @param s the string
   * @return the string with stripped mnemonics
   */
  public static String stripMnemonics(String s) {
      // @todo: That's a hack! Maybe use a regex to replace?
      s = EfaUtil.replace(s, "&&", "§$$§", true);
      s = EfaUtil.replace(s, "&", "", true);
      s = EfaUtil.replace(s, "§$$§", "&", true);
      return s;
  }


}
