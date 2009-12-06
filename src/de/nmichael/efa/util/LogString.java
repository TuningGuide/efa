/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

// @i18n complete

public class LogString {


  public static void logWarning_fileNewCreated(String filename, String description) {
      Logger.log(Logger.WARNING, Logger.MSG_BHWARN_FILENEWCREATED,
                 International.getMessage("{filedescription} ({filename}) wurde neu erzeugt.",
                                          description,filename));
  }

  public static String logstring_fileCreationFailed(String filename, String description) {
      return International.getMessage("{filedescription} ({filename}) konnte nicht neu erzeugt werden.",
                                       description,filename);
  }

  public static void logError_fileCreationFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_BHERR_FILECREATEFAILED,
              logstring_fileCreationFailed(description,filename));
  }

  public static String logstring_fileOpenFailed(String filename, String description) {
      return International.getMessage("{filedescription} ({filename}) konnte nicht gelesen werden.",
                                       description,filename);
  }

  public static void logError_fileOpenFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_BHERR_FILEOPENFAILED,
              logstring_fileOpenFailed(description,filename));
  }

  public static String logstring_fileWritingFailed(String filename, String description) {
      return International.getMessage("{filedescription} ({filename}) konnte nicht geschrieben werden.",
                                       description,filename);
  }

  public static void logError_fileWritingFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_BHERR_FILEWRITEFAILED,
              logstring_fileWritingFailed(description,filename));
  }

  public static String logstring_fileArchivingFailed(String filename, String description) {
      return International.getMessage("{filedescription} ({filename}) konnte nicht archiviert werden.",
                                       description,filename);
  }
  public static void logError_fileArchivingFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_BHERR_FILEWRITEFAILED,
              logstring_fileWritingFailed(description,filename));
  }
  
}
