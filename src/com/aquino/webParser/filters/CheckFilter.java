/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.filters;



import com.aquino.webParser.Book;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author alex
 */
public class CheckFilter extends DocumentFilter { 
    Consumer<String> consumer;
    
    public CheckFilter(Consumer consumer) {
        this.consumer = consumer;
    }
    private boolean checking;

    @Override
    public void replace(
            DocumentFilter.FilterBypass fb,  int offset, 
            int length, String text, AttributeSet attrs) throws BadLocationException{

        if (!checking &&text.contains("aladin.co.kr/shop/wproduct.aspx?ItemId")) {
            checking = true;
            CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() ->{
                Book book = new Book(text);
                String exists = (!book.titleExists()) ? "NO" : "YES";
                return String.format("%s... → Inventory: %s, OCLC: %s", book.getTitle().substring(0,book.getTitle().length() >= 10 ? 10 : book.getTitle().length()),exists, book.getOCLC() == -1 ? "NO" : "YES");
            });
            completableFuture.thenAccept(s -> {
                checking = false;
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
    
    
    
}
