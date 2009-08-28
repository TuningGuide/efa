/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.statistics;

/**
 *
 * @author nick
 */
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
