/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.filters;



import com.aquino.webParser.OldBook;

import java.awt.*;
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

    public CheckFilter(Consumer<String> consumer, Component component) {
        this.consumer = consumer;
        this.component = component;
    }

    private boolean checking;

    @Override
    public void replace(
            DocumentFilter.FilterBypass fb,  int offset, 
            int length, String text, AttributeSet attrs) throws BadLocationException{

        if (!checking &&text.contains("aladin.co.kr/shop/wproduct.aspx?ItemId")) {
            checking = true;
            component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() ->{
                try {
                    OldBook oldBook = new OldBook(text);
                    String exists = (!oldBook.titleExists()) ? "NO" : "YES";
                    return String.format("%s... → Inventory: %s, OCLC: %s", oldBook.getTitle().substring(0, oldBook.getTitle().length() >= 10 ? 10 : oldBook.getTitle().length()),exists, oldBook.getOCLC() == -1 ? "NO" : "YES");
                }
                catch (IllegalArgumentException e){
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
    
    
    
}
