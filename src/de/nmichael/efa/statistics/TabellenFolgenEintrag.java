/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

// @i18n complete

public class TabellenFolgenEintrag {

  public int cols = 0;
  public int colspan = 0;
  public String[] fields = null;
  public String[] colors = null;
  public boolean[] bold = null;
  public TabellenFolgenEintrag next;

  public TabellenFolgenEintrag(int cols, int colspan, String[] fields, String[] colors, boolean[] bold) {
    this.cols = cols;
    this.colspan = colspan;
    this.fields = fields;
    this.colors = colors;
    this.bold = bold;
    this.next = null;
  }

}
