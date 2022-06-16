/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

/**
 * @author alex
 */
public class Login {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Timer TIMER = new Timer();
    private static Map<String, String> cookies;

    private static void login() {
        Connection.Response res;
        try {
            Properties prop = new Properties();
            prop.load(Login.class.getClassLoader()
                    .getResourceAsStream("config.properties"));

            var body = toRequestBody(Map.of(
                    "identity", prop.getProperty("user"),
                    "password", prop.getProperty("password")));

            res = Jsoup.connect("https://www.bookswindow.com/admin/login")
                    .requestBody(body)
                    .method(Connection.Method.POST)
                    .execute();

            var lastSessionCookie = getLastCookie(res.header("Set-Cookie"));
            cookies = Map.of("ci_session", lastSessionCookie);

            setCookieResetTimer();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the last ci_session cookie in the 'Set-Cookie' header.
     * There are several (3+) instances of the same one and
     * only the last one works.
     *
     * @param setCookie the 'Set-Cookie' header.
     * @returns the last ci_session value.
     */
    private static String getLastCookie(String setCookie) {
        var sessionKey = "ci_session=";
        var lastIndex = setCookie.lastIndexOf(sessionKey);

        return setCookie.substring(lastIndex + sessionKey.length());
    }

    public static Document getDocument(String url) {
        return RequestDocument(url, connection -> {
            try {
                return connection.get();
            } catch (IOException e) {
                return null;
            }
        });
    }

    public static Document postDocument(String url, Map<String, String> data) {
        return RequestDocument(url, connection -> {
            try {
                if (data == null)
                    return connection.post();
                else return connection.data(data).post();
            } catch (IOException e) {
                return null;
            }
        });
    }

    /**
     * Posts data the proper way using the request body.
     * The original version put POST data in request parameters.
     *
     * @param url
     * @param data the data to post.
     * @return
     */
    public static Document postBody(String url, Map<String, String> data) {
        return RequestDocument(url, connection -> {
            try {
                if (data == null) {
                    return connection.post();
                }

                var body = toRequestBody(data);

                return connection
                        .requestBody(body)
                        .post();
            } catch (IOException e) {
                LOGGER.error(String.format("Failed post data to %s", url));
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        });
    }

    public static Document RequestDocument(String url, Function<Connection, Document> documentFunction) {
        if (cookies == null) login();
        return documentFunction.apply(Jsoup.connect(url).cookies(cookies));
    }

    private static void setCookieResetTimer() {
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                resetCookies();
            }
        }, 1000 * 60 * 20);
    }

    private static void resetCookies() {
        cookies = null;
    }

    /**
     * Gets the data in string form to be sent as a post payload.
     *
     * @param data the parameters.
     * @returns the data in the payload form.
     */
    private static String toRequestBody(Map<String, String> data) {
        return data.entrySet()
                .stream()
                .map(Object::toString)
                .collect(joining("&"));
    }


}
