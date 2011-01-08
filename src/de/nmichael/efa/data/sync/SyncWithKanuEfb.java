/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.sync;

// @i18n complete

/*
import com.funambol.syncclient.spdm.SimpleDeviceManager;
import com.funambol.syncclient.spds.*;
import com.funambol.syncclient.spds.engine.*;
*/

import java.io.*;
import java.net.*;
import java.util.*;


public class SyncWithKanuEfb {

    private String loginurl;
    private String username;
    private String password;

    public SyncWithKanuEfb(String url, String username, String password) {
        this.loginurl = url;
        this.username = username;
        this.password = password;
    }

    public boolean login() {
        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            URL url = new URL(this.loginurl);
            URLConnection connection = url.openConnection();

            
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(true);
            //connection.setRequestProperty ("Content-Type","application/x-www-form-urlencoded");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	    out.write("username=" + username+ "&password="+password);
            out.flush();
	    //out.close();
            

            
//            connection.setRequestProperty("username", username);
//            connection.setRequestProperty("password", password);
//            connection.setUseCaches(false);
            

//            connection.connect();
            InputStream in = connection.getInputStream();
            BufferedReader buf = new BufferedReader(new InputStreamReader(in));
            String s;
            while( (s = buf.readLine()) != null) {
                System.out.println(s);
            }
            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();
            for (HttpCookie cookie : cookies) {
                System.out.println("Cookie: " + cookie);
            }
/*
            url = new URL(this.loginurl);
            connection = url.openConnection();
            obj = connection.getContent();
            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();
            for (HttpCookie cookie : cookies) {
                System.out.println("Cookie: " + cookie);
            }
 */
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        SyncWithKanuEfb sync = new SyncWithKanuEfb(
                //"http://cgi.snafu.de/nmichael/user-cgi-bin/requesttest.pl",
                "http://sid.kanu-efb.de/services/login",
                //"http://sid.kanu-efb.de/login/login",
                "efa","efa2-efa");
        sync.login();
        /*
        System.setProperty(SimpleDeviceManager.PROP_DM_DIR_BASE, "/home/nick/.efa2/tmp");
        SyncManager syncManager = SyncManager.getSyncManager("kanuefb/efa");
        syncManager.sync();
        */
    }

}
