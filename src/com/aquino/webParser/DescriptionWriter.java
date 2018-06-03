/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class DescriptionWriter {
    
    private static final Logger logger = Logger.getLogger(DescriptionWriter.class.getName());
    private String bookDescriptions = "";
    
    public boolean writeBooks(Book[] books) {
        StringBuilder sb = new StringBuilder(bookDescriptions);
        for (Book book : books) { 
                sb.append(String.format("%s%n%s%s%s%n%n%n%n",
                        book.getTitle(),originalTitle(book.getEnglishTitle())
                        ,originalAuthor(book.getAuthorOriginal()),
                        book.getDescription() + "  " + book.getTranslator()));
        }
        bookDescriptions = sb.toString();
        logger.log(Level.INFO, "Done setting up descriptions");
        return true;
    }
    
    public boolean saveBooks(File file) {
        File saveFile = new File(file.getAbsoluteFile().getParent()
                +"/"+ "Descriptions- "+LocalDateTime.now()
                        .format(DateTimeFormatter
                                .ofPattern("yyyyMMdd-k_m"))
                        +".txt");
        try (BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(saveFile, true),"UTF-8"));) {
            bw.write(LocalDateTime.now().toString());
            bw.newLine();
            bw.write(bookDescriptions);
            bookDescriptions = "";
        } catch (IOException  ex) {
            logger.log(Level.SEVERE, "Problem with writing the book {0}", ex.getMessage());
            return false;
        } 
        logger.log(Level.INFO, "Done writing book descriptions");
        return true;
    }
    
    private String originalTitle(String title) {
        if(!title.equals(""))
            return title + "\n";
        return title;
    }
    
    private String originalAuthor(String author) {
        if(!author.equals(""))
            return author + "\n";
        return author;
    }
    
}
