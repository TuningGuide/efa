/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import java.util.*;

// @i18n complete
public class Test {

    public static void main(String[] args) {
        Daten.initialize(Daten.APPL_CLI);
        Logger.setDebugLogging(true, true);
        Logbook logbook = new Logbook(IDataAccess.TYPE_FILE_XML, System.getProperty("user.dir"), "testlogbook");
        try {
            logbook.open(true);
/*
            logbook.data().add(new LogbookRecord("1"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("2"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("3"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("4"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("5"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("6"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("7"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("8"));
            Thread.sleep(300);
            logbook.data().add(new LogbookRecord("9"));
            Thread.sleep(300);
*/
            logbook.data().add(new LogbookRecord("99"));
            Thread.sleep(300);

            logbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataKey[] a = new DataKey[4];
        a[0] = new DataKey<String,String,String>("foobar","asd",null);
        a[1] = new DataKey<String,String,String>("foobar",null,null);
        a[2] = new DataKey<String,String,String>("99",null,null);
        a[3] = new DataKey<String,String,String>("99",null,null);
        Arrays.sort(a);
        for (int i=0; i<a.length; i++) {
            System.out.println(a[i].toString());
        }
        System.out.println(a[0].equals(a[1]));
        System.out.println(a[1].equals(a[2]));
        System.out.println(a[2].equals(a[3]));


        DataKey x = new DataKey<String,String,String>("99",null,null);
        System.out.println("DataKey x = new DataKey<String,String,String>(\"99\",null,null);");
        DataKey y = new DataKey<String,String,String>("99",null,null);
        System.out.println("DataKey y = new DataKey<String,String,String>(\"99\",null,null);");
        System.out.println("x.hashCode() == " + x.hashCode());
        System.out.println("y.hashCode() == " + y.hashCode());
        HashMap hash = new HashMap();
        System.out.println("HashMap hash = new HashMap();");
        hash.put(x, "foo");
        System.out.println("hash.put(x, \"foo\");");
        System.out.println("x.equals(x) == " + x.equals(x));
        System.out.println("y.equals(y) == " + y.equals(y));
        System.out.println("x.equals(y) == " + x.equals(y));
        System.out.println("y.equals(x) == " + y.equals(x));
        System.out.println("hash.size() == " + hash.size());
        System.out.println("hash.containsKey(x) == " + hash.containsKey(x));
        System.out.println("hash.containsKey(y) == " + hash.containsKey(y));
        hash.put(y, "bar");
        System.out.println("hash.put(y, \"bar\");");
        System.out.println("x.equals(x) == " + x.equals(x));
        System.out.println("y.equals(y) == " + y.equals(y));
        System.out.println("x.equals(y) == " + x.equals(y));
        System.out.println("y.equals(x) == " + y.equals(x));
        System.out.println("hash.size() == " + hash.size());
        System.out.println("hash.containsKey(x) == " + hash.containsKey(x));
        System.out.println("hash.containsKey(y) == " + hash.containsKey(y));
        
        Daten.haltProgram(0);
    }

}
