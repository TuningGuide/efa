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


  public static String logstring_fileNewCreated(String filename, String description) {
      return International.getMessage("{filedescription} '{filename}' wurde neu erzeugt",
                                       description,filename) + ".";
  }



  public static String logstring_fileCreationFailed(String filename, String description, String error) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht neu erzeugt werden",
                                       description,filename) +
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_fileCreationFailed(String filename, String description) {
      return logstring_fileCreationFailed(filename, description, null);
  }



  public static String logstring_fileOpenFailed(String filename, String description, String error) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht geöffnet werden",
                                       description,filename) +
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_fileOpenFailed(String filename, String description) {
      return logstring_fileOpenFailed(filename, description, null);
  }



  public static String logstring_fileReadFailed(String filename, String description, String error) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht gelesen werden.",
                                       description,filename) + 
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_fileReadFailed(String filename, String description) {
      return logstring_fileReadFailed(filename, description, null);
  }



  public static String logstring_fileNotFound(String filename, String description) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht gefunden werden",
                                       description,filename) + ".";
  }



  public static String logstring_fileAlreadyExists(String filename, String description) {
      return International.getMessage("{filedescription} '{filename}' existiert bereits",
                                       description,filename) + ".";
  }



  public static String logstring_fileWritingFailed(String filename, String description, String error) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht geschrieben werden",
                                       description,filename) + 
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_fileWritingFailed(String filename, String description) {
      return logstring_fileWritingFailed(filename, description, null);
  }



  public static String logstring_fileCloseFailed(String filename, String description, String error) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht geschlossen werden",
                                       description,filename) +
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_fileCloseFailed(String filename, String description) {
      return logstring_fileCloseFailed(filename, description, null);
  }



  public static String logstring_fileArchivingFailed(String filename, String description, String error) {
      return International.getMessage("{filedescription} '{filename}' konnte nicht archiviert werden",
                                       description,filename) + 
                                       (error == null ? "." : ": " + error);
  }
  
  public static String logstring_fileArchivingFailed(String filename, String description) {
      return logstring_fileArchivingFailed(filename, description, null);
  }


  
  public static String logstring_fileBackupFailed(String filename, String description, String error) {
      return International.getMessage("Sicherung von {filedescription} '{filename}' konnte nicht erstellt werden",
                                       description,filename) +
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_fileBackupFailed(String filename, String description) {
      return logstring_fileBackupFailed(filename, description, null);
  }



  public static String logstring_directoryDoesNotExist(String dirname, String description) {
      return International.getMessage("{directorydescription} '{directoryname}' existiert nicht",
                                       description,dirname) + ".";
  }



  public static String logstring_cantExecCommand(String command, String description, String error) {
      return International.getMessage("{commanddescription} '{command}' kann nicht ausgeführt werden",
                                       description,command) +
                                       (error == null ? "." : ": " + error);
  }

  public static String logstring_cantExecCommand(String command, String description) {
      return logstring_cantExecCommand(command, description, null);
  }




  public static void logInfo_fileNewCreated(String filename, String description) {
      Logger.log(Logger.INFO, Logger.MSG_FILE_FILENEWCREATED,
                 logstring_fileNewCreated(description,filename));
  }

  public static void logWarning_fileNewCreated(String filename, String description) {
      Logger.log(Logger.WARNING, Logger.MSG_FILE_FILENEWCREATED,
                 logstring_fileNewCreated(description,filename));
  }

  public static void logError_fileCreationFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILECREATEFAILED,
              logstring_fileCreationFailed(description,filename));
  }

  public static void logError_fileOpenFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILEOPENFAILED,
              logstring_fileOpenFailed(description,filename));
  }

  public static void logError_fileReadFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILEREADFAILED,
              logstring_fileReadFailed(description,filename));
  }

  public static void logError_fileNotFound(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILENOTFOUND,
              logstring_fileNotFound(description,filename));
  }

  public static void logError_fileAlreadyExists(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILEALREADYEXISTS,
              logstring_fileAlreadyExists(description,filename));
  }

  public static void logError_fileWritingFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILEWRITEFAILED,
              logstring_fileWritingFailed(description,filename));
  }

  public static void logError_fileCloseFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_FILECLOSEFAILED,
              logstring_fileCloseFailed(description,filename));
  }

  public static void logError_fileArchivingFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_ARCHIVINGFAILED,
              logstring_fileArchivingFailed(description,filename));
  }

   public static void logError_fileBackupFailed(String filename, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_BACKUPFAILED,
              logstring_fileBackupFailed(description,filename));
  }
 
   public static void logError_directoryDoesNotExist(String dirname, String description) {
      Logger.log(Logger.ERROR, Logger.MSG_FILE_DIRECTORYNOTFOUND,
              logstring_directoryDoesNotExist(description,dirname));
  }
  
  public static void logWarning_cantExecCommand(String command, String description, String error) {
      Logger.log(Logger.WARNING, Logger.MSG_WARN_CANTEXECCOMMAND,
              logstring_cantExecCommand(command,description,error));
  }

}