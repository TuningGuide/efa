/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.statistics;

import java.util.*;
import de.nmichael.efa.EfaUtil;

/**
 *
 * @author nick
 */
class AusgabeZeilen {
  public static final int FONT_NORMAL = 0;
  public static final int FONT_BOLD = 1;

  private Vector zeilen;

  public AusgabeZeilen() {
    zeilen = new Vector();
  }

  public void addZeile(String s, int columns, int font) {
    if (columns<1 || columns>9) columns = 1;
    if (font<0 || font>9) font = FONT_NORMAL;
    zeilen.add("#"+columns+"#"+font+"#"+s);
  }

  public int size() {
    return zeilen.size();
  }

  public String getZeile(int i) {
    if (i<0 || i>=zeilen.size()) return null;
    String s = (String)zeilen.get(i);
    return s.substring(5,s.length());
  }

  public int getZeileColumns(int i) {
    if (i<0 || i>=zeilen.size()) return 1;
    String s = (String)zeilen.get(i);
    return EfaUtil.string2int(s.substring(1,2),1);
  }

  public int getZeileFont(int i) {
    if (i<0 || i>=zeilen.size()) return FONT_NORMAL;
    String s = (String)zeilen.get(i);
    return EfaUtil.string2int(s.substring(3,4),FONT_NORMAL);
  }

}
