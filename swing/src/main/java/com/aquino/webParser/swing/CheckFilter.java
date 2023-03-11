/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.swing;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.bookCreators.BookCreator;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author alex
 */
public class CheckFilter extends DocumentFilter {

    private static final Logger LOGGER = LogManager.getLogger();

    private Consumer<String> consumer;
    private Component component;

    private BookCreator bookCreator;

    public CheckFilter(
            Consumer<String> consumer,
            Component component,
            BookCreator bookCreator) {
        this.consumer = consumer;
        this.component = component;
        this.bookCreator = bookCreator;
    }

    private boolean checking;

    @Override
    public void replace(
            DocumentFilter.FilterBypass fb,
            int offset,
            int length,
            String text,
            AttributeSet attrs) throws BadLocationException {

        if (!checking && text.contains(bookCreator.BookPagePrefix())) {
            checking = true;
            component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    Book book = setupBook(text);
                    return String.format(
                            "%s... → Inventory: %s, oclc: %s",
                            getBookTitle(book),
                            book.isTitleExists() ? "YES" : "NO",
                            // oclc function is broken
                            //  book.getOclc() == -1L ? "NO" : "YES");
                            "BROKEN");
                } catch (Exception e) {
                    LOGGER.error("Problem setting up book.");
                    LOGGER.error(e.getMessage(), e);
                    return String.format("Error: %s", e.getMessage());
                }
            });
            completableFuture.thenAccept(s -> {
                checking = false;
                component.setCursor(null);
                try {
                    super.replace(fb, 0, fb.getDocument().getLength(), s, attrs);
                } catch (BadLocationException e) {
                    LOGGER.error("Problem setting text..");
                    LOGGER.error(e.getMessage(), e);
                }
                consumer.accept(text);
            });
        }
    }

    private Book setupBook(String text) throws IOException {
        Book result = bookCreator.createBookFromBookPage(text);
        bookCreator.checkInventoryAndOclc(result);
        
        return result;
    }
    
    private String getBookTitle(Book book){
        var titleLength = book.getTitle().length();
        var length = titleLength >= 10 ? 10 : titleLength;
        
        return book.getTitle().substring(0, length);
    }

    public void setBookCreator(BookCreator bookCreator) {
        this.bookCreator = bookCreator;
    }
}
