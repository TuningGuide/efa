import de.nmichael.efa.*;
import java.io.*;

public class SecFileCreator {

  // usage: SecFileCreator <cfgDir> <efa.jar>
  public static void main(String[] args) {
    try {
      String secFile = args[0]+"/"+Daten.EFA_SECFILE;
      String efaJar = args[1];
      String sha = EfaUtil.getSHA(new File(efaJar));
      EfaSec efaSec = new EfaSec(secFile);
      efaSec.writeSecFile(sha,false);
      System.out.println("Erfolg: SHA von "+efaJar+" nach "+secFile+" geschrieben. Wert="+sha);
      System.exit(0);
    } catch(Exception e) {
      System.out.println("Fehler: "+e.toString());
      System.exit(1);
    }
  }
}