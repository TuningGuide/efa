package de.nmichael.efa;

import java.io.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class EfaFileFilter extends javax.swing.filechooser.FileFilter {

  String description = "";
  String ext1 = "";
  String ext2 = "";
  int anz=0;

  public EfaFileFilter(String descr, String ext) {
    description = descr;
    ext1 = ext.toUpperCase();
    anz=1;
  }

  public EfaFileFilter(String descr, String ext1, String ext2) {
    description = descr;
    this.ext1 = ext1.toUpperCase();
    this.ext2 = ext2.toUpperCase();
    anz=2;
  }

  public boolean accept (File f) {
    return f.isDirectory() || f.getName().toUpperCase().endsWith(ext1) || (anz == 2 && f.getName().toUpperCase().endsWith(ext2));
  }

  public String getDescription() {
    return description;
  }
}
