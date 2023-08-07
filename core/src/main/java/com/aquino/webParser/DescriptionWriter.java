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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;

/**
 * @author alex
 */
public class DescriptionWriter {

    private static final Logger LOGGER = LogManager.getLogger();
    private TextStringBuilder bookDescriptionBuilder = new TextStringBuilder();

    public boolean writeBooks(List<Book> books) {
        for (Book book : books) {
            bookDescriptionBuilder.appendln(book.getTitle());
            bookDescriptionBuilder.appendln(originalTitle(book.getEnglishTitle()));
            bookDescriptionBuilder.appendln(originalAuthor(book.getAuthorOriginal()));
            bookDescriptionBuilder.appendln(book.getDescription());
            bookDescriptionBuilder.appendln(book.getKoreanDescription());
            bookDescriptionBuilder.appendln(book.getSummary());
            bookDescriptionBuilder.appendln(book.getTranslator());
            bookDescriptionBuilder.appendNewLine();
            bookDescriptionBuilder.appendNewLine();
            bookDescriptionBuilder.appendNewLine();
            bookDescriptionBuilder.appendNewLine();
        }

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
            bw.write(bookDescriptionBuilder.toString());
            bookDescriptionBuilder.clear();
        }
        catch (IOException ex) {
            LOGGER.error("Problem with writing the book {0}", ex.getMessage());
            return false;
        }
        LOGGER.info("Done writing book descriptions");
        return true;
    }

    private String originalTitle(String title) {
        if (!StringUtils.isBlank(title))
            return title + "\n";
        return title;
    }

    private String originalAuthor(String author) {
        if (!StringUtils.isBlank(author))
            return author + "\n";
        return author;
    }

}
