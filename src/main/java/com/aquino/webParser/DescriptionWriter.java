/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author alex
 */
public class DescriptionWriter {

    private static final Logger LOGGER = LogManager.getLogger();
    private String bookDescriptions = "";

    public boolean writeBooks(List<Book> books) {
        StringBuilder sb = new StringBuilder(bookDescriptions);
        for (Book book : books) {
            sb.append(String.format("%s%n%s%s%s%n%n%n%n",
                book.getTitle(), originalTitle(book.getEnglishTitle())
                , originalAuthor(book.getAuthorOriginal()),
                book.getDescription() + "  " + book.getTranslator()));
        }
        bookDescriptions = sb.toString();
        LOGGER.info("Done setting up descriptions");
        return true;
    }

    public boolean saveBooks(File file) {
        File saveFile = new File(file.getAbsoluteFile().getParent()
            + "/" + "Descriptions- " + LocalDateTime.now()
            .format(DateTimeFormatter
                .ofPattern("yyyyMMdd-k_m"))
            + ".txt");
        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(saveFile, true), StandardCharsets.UTF_8))) {
            bw.write(LocalDateTime.now().toString());
            bw.newLine();
            bw.write(bookDescriptions);
            bookDescriptions = "";
        }
        catch (IOException ex) {
            LOGGER.error("Problem with writing the book {0}", ex.getMessage());
            return false;
        }
        LOGGER.info("Done writing book descriptions");
        return true;
    }

    private String originalTitle(String title) {
        if (!title.equals(""))
            return title + "\n";
        return title;
    }

    private String originalAuthor(String author) {
        if (!author.equals(""))
            return author + "\n";
        return author;
    }

}
