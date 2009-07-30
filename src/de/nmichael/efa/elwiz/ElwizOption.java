package de.nmichael.efa.elwiz;

import java.util.Vector;

class ElwizSingleOption {
  public String descr = null;
  public String value = null;
  public boolean selected = false;
  public String type = null;
}

public class ElwizOption {

  public static final int O_OPTIONAL = 1;
  public static final int O_SELECT = 2;
  public static final int O_VALUE = 3;

  public int type = 0;
  public String name = null;
  public String descr = null;
  public Vector options = null;
  public Vector components = null;

  public ElwizOption() {
  }
}