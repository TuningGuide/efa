package de.nmichael.efa.util;

import java.util.*;
import java.io.*;

public class CaptureOutputPrintStream extends PrintStream {

  private Vector lines;

  public CaptureOutputPrintStream(OutputStream f) {
    super(f);
    lines = new Vector();
  }

  public void print(Object o) {
    lines.add(o);
  }

  public void print(String s) {
    lines.add(s);
  }

  public Vector getLines() {
    return lines;
  }


}