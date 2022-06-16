package com.aquino.webParser.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LoginTest {

    @Test
    public void login() {
        var adminUrl = "https://www.bookswindow.com/admin";

        var doc = Login.RequestDocument(adminUrl, connection -> {
            try {
                return connection.get();
            } catch (IOException e) {
                Assert.fail();
                return null;
            }
        });

        var refreshUrl = doc.connection().response().header("Refresh");

        // no refresh means no problem
        Assert.assertEquals(null, refreshUrl);
    }
}
