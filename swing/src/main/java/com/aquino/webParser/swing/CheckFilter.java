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

/**
 *
 * @author alex
 */
public class CheckFilter extends DocumentFilter { 
    private Consumer<String> consumer;
    private Component component;

    private BookCreator bookCreator;

    public CheckFilter(Consumer<String> consumer, Component component, BookCreator bookCreator) {
        this.consumer = consumer;
        this.component = component;
        this.bookCreator = bookCreator;
    }

    private boolean checking;

    @Override
    public void replace(
            DocumentFilter.FilterBypass fb,  int offset, 
            int length, String text, AttributeSet attrs) throws BadLocationException{

//        if (!checking &&text.contains("aladin.co.kr/shop/wproduct.aspx?ItemId")) {
        if (!checking &&text.contains(bookCreator.BookPagePrefix())) {
            checking = true;
            component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() ->{
                try {
                    Book book = setupBook(text);
                    String exists = (!book.isTitleExists()) ? "NO" : "YES";
                    return String.format("%s... → Inventory: %s, oclc: %s", book.getTitle().substring(0, book.getTitle().length() >= 10 ? 10 : book.getTitle().length()),exists, book.getOclc() == -1 ? "NO" : "YES");
                }
                catch (Exception e){
                    e.printStackTrace();
                    return String.format("Error: %s", e.getMessage());
                }
            });
            completableFuture.thenAccept(s -> {
                checking = false;
                component.setCursor(null);
                try {
                    super.replace(fb, 0, fb.getDocument().getLength(), s, attrs);
                } catch (BadLocationException e) {
                    //TODO properly handle this... shouldn't cause a problem.... This exception isn't caught anywhere anyway
                    e.printStackTrace();
                }
                consumer.accept(text);
            });
        }
    }

    private Book setupBook(String text) throws IOException {
        Book result = bookCreator.createBookFromBookPage(text);
        String isbn = String.valueOf(result.getIsbn());
        bookCreator.checkInventoryAndOclc(result);
        return result;
    }


    public void setBookCreator(BookCreator bookCreator) {
        this.bookCreator = bookCreator;
    }
    
    
    
}
