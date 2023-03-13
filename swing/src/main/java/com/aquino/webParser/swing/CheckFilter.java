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
import javax.swing.text.JTextComponent;
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
    private JTextComponent textComponent;

    private BookCreator bookCreator;

    public CheckFilter(
            Consumer<String> consumer,
            Component component,
            JTextComponent textComponent,
            BookCreator bookCreator) {
        this.consumer = consumer;
        this.component = component;
        this.textComponent = textComponent;
        this.bookCreator = bookCreator;
    }

    private boolean checking;

    @Override
    public void replace(
            DocumentFilter.FilterBypass fb,
            int offset,
            int length,
            String input,
            AttributeSet attrs) throws BadLocationException {

        if (!checking && input.contains(bookCreator.BookPagePrefix())) {
            checking = true;
            component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            CompletableFuture<Book> completableFuture = CompletableFuture.supplyAsync(() -> getBook(input));
            completableFuture.thenAccept(b -> {
                checking = false;
                component.setCursor(null);
                acceptBook(fb, attrs, b, input);
            });
        }
    }

    private Book getBook(String text) {
        try {
            return setupBook(text);
        } catch (Exception e) {
            LOGGER.error("Problem setting up book.");
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private Book setupBook(String text) throws IOException {
        Book result = bookCreator.createBookFromBookPage(text);
        bookCreator.checkInventoryAndOclc(result);

        return result;
    }

    private void acceptBook(
            DocumentFilter.FilterBypass fb,
            AttributeSet attrs,
            Book book,
            String input) {

        var msg = getMessage(book);
        setColors(book);

        try {
            super.replace(fb, 0, fb.getDocument().getLength(), msg, attrs);
        } catch (BadLocationException e) {
            LOGGER.error("Problem setting text..");
            LOGGER.error(e.getMessage(), e);
        }

        consumer.accept(input);
    }

    private String getBookTitle(Book book) {
        var titleLength = book.getTitle().length();
        var length = titleLength >= 10 ? 10 : titleLength;

        return book.getTitle().substring(0, length);
    }

    public void setBookCreator(BookCreator bookCreator) {
        this.bookCreator = bookCreator;
    }

    private String getMessage(Book book) {
        return book == null
                ? "Problem setting text.. Check Logs."
                : String.format(
                        "%s... → %s inventory.",
                        getBookTitle(book),
                        book.isTitleExists() ? "IN" : "NOT in");
    }

    private void setColors(Book book) {
        if (book == null) {
            textComponent.setBackground(Color.white);
            textComponent.setForeground(Color.black);
        } else if (book.isTitleExists()) {
            textComponent.setBackground(Color.red);
            textComponent.setForeground(Color.black);
        } else {
            textComponent.setBackground(Color.green);
            textComponent.setForeground(Color.black);
        }
    }
}
