/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.Utilities;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author alex
 */
public class Login {
    
    private static Map<String, String> cookies;
    private static final Timer TIMER = new Timer();
    
    private static void login() {
        Connection.Response res;
        try {
            Properties prop = new Properties();
            prop.load(Login.class.getClassLoader()
                    .getResourceAsStream("com/aquino/webParser/resources/config.properties"));
            
             res = Jsoup.connect("https://www.bookswindow.com/admin/login").
                data("identity",prop.getProperty("user"),"password",prop.getProperty("password")).     
                method(Connection.Method.POST).
                execute();
             cookies = res.cookies();
             setCookieResetTimer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Document getDocument(String url) {
        if(cookies == null) login();
        Document doc;
        try {
            doc = Jsoup.connect(url)
                      .cookies(cookies)
                      .get();
        } catch (IOException e) {
            doc = null;
        }
        return doc;
    }
    
    private static void setCookieResetTimer() {
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                resetCookies();
            }
        }, 1000*60*20);
    }
    
    private static void resetCookies() {
        cookies = null;
    }
    
    
    
}
