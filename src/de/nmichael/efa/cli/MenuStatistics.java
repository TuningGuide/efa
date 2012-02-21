/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.cli;

import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.statistics.StatisticTask;
import de.nmichael.efa.util.Logger;
import java.util.Stack;
import java.util.UUID;

public class MenuStatistics extends MenuData {

    public static final String CMD_CREATE  = "create";

    public MenuStatistics(CLI cli) {
        super(cli);
        this.storageObject = cli.getPersistence(Statistics.class, Project.STORAGEOBJECT_STATISTICS);
        this.storageObjectDescription = "statistics";
    }

    public void printHelpContext() {
        printUsage(CMD_CREATE,  "[name|index]", "create statistic");
        super.printHelpContext();
    }

    protected int create(String args) {
        StatisticsRecord sr = (StatisticsRecord) getRecordFromArgs(args);
        if (sr == null) {
            sr = (StatisticsRecord) getRecordFromArgs(args);
        }
        if (sr == null) {
            cli.logerr("Record '"+args+"' not found.");
            return CLI.RC_COMMAND_FAILED;
        }
        
        boolean outputOk = false;
        switch (sr.getOutputTypeEnum()) {
            case html:
            case csv:
            case xml:
            case pdf:
                outputOk = true;
        }
        if (!outputOk) {
            cli.logerr("Cannot create statistic with output type '"+sr.getOutputTypeDescription()+"' in CLI.");
            return CLI.RC_COMMAND_FAILED;
        }

        try {
            cli.loginfo("Creating Statistic " + sr.getQualifiedName() + "...");
            StatisticTask.createStatisticsTask(null, null, new StatisticsRecord[] { sr }, cli.getAdminRecord());
            cli.loginfo("Done.");
            return CLI.RC_OK;
        } catch(Exception e) {
            cli.loginfo("Error creating Statistic: " + e.toString());
            Logger.logdebug(e);
            return CLI.RC_COMMAND_FAILED;
        }
    }

    public int runCommand(Stack<String> menuStack, String cmd, String args) {
        if (cmd.equalsIgnoreCase(CMD_CREATE)) {
            return create(args);
        }
        return super.runCommand(menuStack, cmd, args);
    }

}
