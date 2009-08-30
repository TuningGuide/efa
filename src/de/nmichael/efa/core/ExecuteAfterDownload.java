package de.nmichael.efa.core;

public interface ExecuteAfterDownload {

  public void success();
  public void failure(String text);

}