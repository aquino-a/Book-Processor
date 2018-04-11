/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.filters;



import com.aquino.webParser.Book;
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
    @Override
    public void replace(
            DocumentFilter.FilterBypass fb,  int offset, 
            int length, String text, AttributeSet attrs) throws BadLocationException{
        if (text.contains("aladin.co.kr/shop/wproduct.aspx?ItemId")) {
            Book book = new Book(text);
            String exists = (!book.titleExists()) ? "NOT" : "ALREADY";
            String output = book.getTitle() + " is " + exists + " in the inventory!";
            super.replace(fb, 0, fb.getDocument().getLength(), output, attrs);
            consumer.accept(text);
        }
    }
    
    
    
}
