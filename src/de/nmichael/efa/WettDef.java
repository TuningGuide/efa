package de.nmichael.efa;

public class WettDef {
  public int wettid=-1;
  public String name=null;
  public String kurzname=null;
  public String key=null;
  public int gueltig_von=-1;
  public int gueltig_bis=-1;
  public int gueltig_prio=0;
  public TMJ von=null, bis=null; // von/bis.jahr wird relativ zu StatistikDaten.wettJahr angegeben: Bsp: DRV: 0/0; LRV Winter: 0/1 usw.
  public WettDefGruppe[] gruppen = null;
}
