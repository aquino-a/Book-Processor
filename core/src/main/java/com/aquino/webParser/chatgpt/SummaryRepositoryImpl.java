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

    private final String filePath;
    private final Map<String, String> summaryCache = new HashMap<>();
    private final Map<String, String> titleCache = new HashMap<>();
    private Connection connection;

    public SummaryRepositoryImpl(String filePath) {
        this.filePath = filePath;
        checkTable();
    }

    @Override
    public String get(String isbn) {
        if (summaryCache.containsKey(isbn)) {
            return summaryCache.get(isbn);
        }

        return getFromDatabase(isbn, "SUMMARY", summaryCache);
    }

    @Override
    public String getTitle(String isbn) {
        if (titleCache.containsKey(isbn)) {
            return titleCache.get(isbn);
        }

        return getFromDatabase(isbn, "TITLE", titleCache);
    }

    @Override
    public String getCategories(String isbn) {
        return getFromDatabase(isbn, "CATEGORY", new HashMap<>());
    }

    @Override
    public void save(String isbn, String summary) {
        save(isbn, summary, "SUMMARY");
    }

    @Override
    public void saveTitle(String isbn, String title) {
        save(isbn, title, "TITLE");
    }

    @Override
    public void saveCategory(String isbn, String combinedCodes) {
        save(isbn, combinedCodes, "CATEGORY");
    }

    private void save(String isbn, String value, String column) {
        Connection conn = getConnection();
        var updateStatement = String.format("UPDATE SUMMARY set %s = ? WHERE ISBN = ?", column);
        try (PreparedStatement updatePstmt = conn.prepareStatement(updateStatement)) {
            updatePstmt.setString(1, value);
            updatePstmt.setString(2, isbn);
            var result = updatePstmt.executeUpdate();

            if (result > 0) {
                LOGGER.log(Level.INFO, "Summary updated successfully.");
                return;
            }

            var insertStatement = String.format("INSERT INTO SUMMARY (ISBN, %s) VALUES (?, ?)", column);
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertStatement)) {
                insertPstmt.setString(1, isbn);
                insertPstmt.setString(2, value);
                insertPstmt.executeUpdate();
            }

            LOGGER.log(Level.INFO, "Summary inserted successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Problem saving summary", e);
        }
    }

    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:h2:" + filePath, "sa", "");
            }

            return connection;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "Problem getting connection.", ex);
            return null;
        }
    }

    private String getFromDatabase(String isbn, String column, Map<String, String> cache) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM SUMMARY WHERE ISBN = ?")) {
            String key = isbn;
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String value = rs.getString(column);
                    cache.put(isbn, value);

                    return value;
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
        try (Statement stmt = conn.createStatement();
                ResultSet rs = conn.getMetaData().getTables(null, null, "SUMMARY", null)) {
            if (!rs.next()) {
                // Create table if it doesn't exist
                stmt.executeUpdate(
                        "CREATE TABLE SUMMARY (ISBN VARCHAR2(30), TITLE VARCHAR2(300), CATEGORY VARCHAR2(400), SUMMARY VARCHAR2(4000), PRIMARY KEY (ISBN))");
                LOGGER.log(Level.INFO, "Summary table created successfully.");
            } else {
                LOGGER.log(Level.INFO, "Summary table already exists.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Problem creating Summary table", e);
        }
    }
}
