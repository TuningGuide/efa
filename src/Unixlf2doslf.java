/*
  Unix Zeilenumbr�che (LF) nach DOS (CRLF) umwandeln
*/

import java.io.*;

public class Unixlf2doslf {

  public static boolean copyFile(String quelle, String ziel) {
    FileInputStream f1;
    FileOutputStream f2;
    int c,clast;
    try {
      f1 = new FileInputStream(quelle);
      f2 = new FileOutputStream(ziel);
      clast = 0;
      while ( (c = f1.read()) >= 0) {
        if (c == 10 && clast!= 13) f2.write(13);
        f2.write(c);
      }
      f1.close();
      f2.close();
    } catch(IOException e) {
      return false;
    }
    return true;
  }


  public static void main(String[] args) {
    if (args.length != 2) System.err.println("usage: Unix2doslf <in> <out>");
    else if (copyFile(args[0],args[1])) System.out.println("done");
    else System.err.println("error");
  }
}