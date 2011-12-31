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

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.EfaConfig;
import de.nmichael.efa.data.Project;
import de.nmichael.efa.data.storage.EfaOnlineClient;
import de.nmichael.efa.data.storage.IDataAccess;
import de.nmichael.efa.data.storage.StorageObject;
import de.nmichael.efa.util.Logger;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.Stack;

public class CLI {

    public static final String MENU_MAIN         = "main";
    public static final String MENU_BOATS        = "boats";
    public static final String MENU_PERSONS      = "persons";
    public static final String MENU_DESTINATIONS = "destinations";
    public static final String MENU_BACKUP       = "backup";


    private String username;
    private String password;
    private String hostname;
    private String port;
    private String project;
    Console console;
    BufferedReader in;

    private EfaConfig remoteEfaConfig;
    private MenuBase lastMenu;
    private Stack<String> menuStack;

    public CLI(String username,
               String password,
               String hostname,
               String port,
               String project) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.project = project;
        console = System.console();
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void getIpAndPortFromEfaOnline() {
        String addr = EfaOnlineClient.getRemoteAddress(hostname, port);
        hostname = null;
        port = null;
        if (addr != null) {
            int pos = addr.indexOf(":");
            if (pos >= 0) {
                hostname = addr.substring(0, pos);
                port = addr.substring(pos + 1);
            }
        }
    }

    public void loginfo(String s) {
        Logger.log(Logger.INFO, Logger.MSG_CLI_INFO, s);
    }

    public void logerr(String s) {
        Logger.log(Logger.ERROR, Logger.MSG_CLI_ERROR, s);
    }

    public void loginput(String s) {
        Logger.log(Logger.INPUT, Logger.MSG_CLI_INPUT, s);
    }

    public void logoutput(String s) {
        Logger.log(Logger.OUTPUT, Logger.MSG_CLI_OUTPUT, s);
    }

    public String promptForInput(String prompt) {
        loginput((prompt != null ? prompt + ": " : "efaCLI> "));
        String s = null;
        if (console != null) {
            s = console.readLine();
        } else {
            try {
                s = in.readLine();
            } catch(Exception eignore) {
            }
        }
        if (s == null) {
            System.out.println();
            Daten.haltProgram(0);
        }
        return s;
    }

    public String promptForPassword(String prompt) {
        loginput((prompt != null ? prompt + ": " : "efaCLI> "));
        String s = null;
        if (console != null) {
            char[] pass = console.readPassword();
            if (pass != null) {
                s = new String(pass);
            }
        } else {
            try {
                s = in.readLine();
            } catch(Exception eignore) {
            }
        }
        if (s == null) {
            System.out.println();
            Daten.haltProgram(0);
        }
        return s;
    }

    public StorageObject getPersistence(Class c, String name) {
        try {
            StorageObject p;
            if (name != null) {
                p = (StorageObject) c.getConstructor(
                        int.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class).newInstance(
                        IDataAccess.TYPE_EFA_REMOTE,
                        hostname + ":" + port,
                        username,
                        password,
                        name);
            } else {
                p = (StorageObject) c.getConstructor(
                        int.class,
                        String.class,
                        String.class,
                        String.class).newInstance(
                        IDataAccess.TYPE_EFA_REMOTE,
                        hostname + ":" + port,
                        username,
                        password);

            }
            p.setProject(Daten.project);
            return p;
        } catch (Exception e) {
            Logger.log(e);
            return null;
        }
    }

    private boolean connect() {
        if (port != null && port.length() > 0 && !Character.isDigit(port.charAt(0))) {
            getIpAndPortFromEfaOnline();
        }
        if (password == null) {
            password = promptForPassword("Password for "+username);
        }
        loginfo("Connecting as "+username+" to " +hostname+":"+port+" ...");
        remoteEfaConfig = (EfaConfig)getPersistence(EfaConfig.class, null);
        try {
            if (remoteEfaConfig.isOpen()) {
                loginfo("Connected.");
                loginfo("Opening Remote Project " + project + " ...");
                Project prj = new Project(IDataAccess.TYPE_FILE_XML, Daten.efaTmpDirectory, "cli");
                prj.open(true);
                prj.setEmptyProject("cli");
                prj.setProjectDescription("dummy project created by cli");
                prj.setProjectStorageType(IDataAccess.TYPE_EFA_REMOTE);
                prj.setProjectStorageLocation(hostname + ":" + port);
                prj.setProjectStorageUsername(username);
                prj.setProjectStoragePassword(password);
                prj.setProjectRemoteProjectName(project);
                prj.close();
                Project.openProject(new Project(IDataAccess.TYPE_FILE_XML, Daten.efaTmpDirectory, "cli"), "cli");
                if (Daten.project != null) {
                    loginfo("Remote Project opened.");
                } else {
                    logerr("Failed to open Remote Project " + project + ".");
                }
                return true;
            }
        } catch(Exception e) {
            logerr(e.getMessage());
        }
        return false;
    }
    
    private String parseCommand(String s, int i) {
        s = s.trim();
        int pos = s.indexOf(" ");
        if (pos >= 0) {
            if (i == 0) {
                return s.substring(0, pos);
            } else {
                return s.substring(pos + 1);
            }
        } else {
            if (i == 0) {
                return s;
            } else {
                return null;
            }
        }
    }

    public void quit() {
        Daten.haltProgram(0);
    }

    public Class getMenu() {
        String mymenu = menuStack.peek();
        if (mymenu.equals(MENU_MAIN)) {
            return de.nmichael.efa.cli.MenuMain.class;
        }
        if (mymenu.equals(MENU_BOATS)) {
            return de.nmichael.efa.cli.MenuBoats.class;
        }
        if (mymenu.equals(MENU_PERSONS)) {
            return de.nmichael.efa.cli.MenuPersons.class;
        }
        if (mymenu.equals(MENU_DESTINATIONS)) {
            return de.nmichael.efa.cli.MenuDestinations.class;
        }
        if (mymenu.equals(MENU_BACKUP)) {
            return de.nmichael.efa.cli.MenuBackup.class;
        }
        return null;
    }

    public void run(String initialCommand) {
        if (!connect()) {
            return;
        }
        try {
            Thread.sleep(500);
        } catch(InterruptedException eignore) {
        }
        if (initialCommand != null && initialCommand.length() == 0) {
            initialCommand = null;
        }

        menuStack = new Stack<String>();
        menuStack.push(MENU_MAIN);

        while(true) {
            String command = (initialCommand != null ? 
                initialCommand : promptForInput(null));
            if (command == null || command.length() == 0) {
                continue;
            }
            runCommandInCurrentMenu(command);
            if (menuStack.isEmpty() || initialCommand != null) {
                quit();
            }
        }
    }

    public boolean runCommandInCurrentMenu(String command) {
        String cmd = parseCommand(command, 0);
        String args = parseCommand(command, 1);
        if (cmd == null || cmd.length() == 0) {
            return true;
        }
        Class c = getMenu();
        if (c != null) {
            try {
                MenuBase menu;
                if (lastMenu != null && c == lastMenu.getClass()) {
                    menu = lastMenu;
                } else {
                    menu = (MenuBase) c.getConstructor(CLI.class).newInstance(this);
                }
                lastMenu = menu;
                return menu.runCommand(menuStack, cmd, args);
            } catch (Exception e) {
                Logger.log(e);
                return false;
            }
        } else {
            logerr("Command in unknown Menu");
            return false;
        }

    }
}
