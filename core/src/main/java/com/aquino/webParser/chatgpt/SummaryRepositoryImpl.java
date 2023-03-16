/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Uses h2 database to persist summaries and caches them in a Map.
 *
 * @author alex
 */
public class SummaryRepositoryImpl implements SummaryRepository {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, String> cache = new HashMap<>();
    private Connection connection;

    @Override
    public String get(String isbn) {
        if (cache.containsKey(isbn)) {
            return cache.get(isbn);
        }

        checkTable();

        return getFromDatabase(isbn);
    }

    @Override
    public void save(String isbn, String summary) {
        checkTable();
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO SUMMARY (ISBN, SUMMARY) VALUES (?, ?)")) {
            pstmt.setString(1, isbn);
            pstmt.setString(2, summary);
            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Data saved successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Problem saving summary", e);
        }
    }

    private Connection getConnection() {
        try {
            if (connection != null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:h2:./summaries", "sa", "");
            }

            return connection;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "Problem getting connection.", ex);
            return null;
        }
    }

    private String getFromDatabase(String isbn) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM SUMMARY WHERE ISBN = ?")) {
            String key = isbn;
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String summary = rs.getString("COL2");
                    cache.put(isbn, summary);

                    return summary;
                } else {
                    LOGGER.log(Level.INFO, "No data found for ISBN = " + key);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Problem getting summary from db.", e);
            return null;
        }
    }

    private void checkTable() {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = conn.getMetaData().getTables(null, null, "SUMMARY", null)) {
            if (!rs.next()) {
                // Create table if it doesn't exist
                stmt.executeUpdate("CREATE TABLE SUMMARY (ISBN VARCHAR2(30), SUMMARY VARCHAR2(4000))");
                LOGGER.log(Level.INFO, "Summary table created successfully.");
            } else {
                LOGGER.log(Level.INFO, "Summary table already exists.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Problem creating Summary table", e);
        }
    }

}
