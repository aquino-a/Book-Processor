/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.swing;

import com.aquino.webParser.model.DataType;
import com.aquino.webParser.bookCreators.BookCreator;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.StringTokenizer;

/**
 *
 * @author alex
 */
public class NewLineFilter extends DocumentFilter{


    private DataType dataType;
    private BookCreator bookCreator;

    public NewLineFilter(DataType dataType, BookCreator bookCreator) {
        this.dataType = dataType;
        this.bookCreator = bookCreator;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String str, AttributeSet a)
            throws BadLocationException {
        if (str.contains("aladin.co.kr/shop/wproduct.aspx?ItemId"))
            super.insertString(fb, offset, str, a);
            super.insertString(fb, fb.getDocument().getLength(), System.lineSeparator(), a);
    }
    @Override 
    public void replace(FilterBypass fb, int offs,int length, String str, AttributeSet a)
            throws BadLocationException {
        if(IsStringValid(str)) {
            super.replace(fb,offs,length,str,a);
            super.insertString(fb, fb.getDocument().getLength(), System.lineSeparator(), a); 
        }
        if(str == "") {
            super.replace(fb, offs, length, str, a);
        }
    }

    private boolean IsStringValid(String str) {
        StringTokenizer st = new StringTokenizer(str);
        String token;
        while(st.hasMoreTokens()){
            token = st.nextToken();
            if(dataType.equals(DataType.BookPage)){
                if(!token.contains(bookCreator.BookPagePrefix()))
                    return false;
            }
            else if(dataType.equals(DataType.Isbn)){
                try {
                    Long.parseLong(token);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            else throw new UnsupportedOperationException();
        }
        return true;
    }


    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setBookCreator(BookCreator bookCreator) {
        this.bookCreator = bookCreator;
    }
}
