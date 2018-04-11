/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.filters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 *
 * @author alex
 */
public class NewLineFilter extends DocumentFilter{
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
        if(str.contains("aladin")) {
            super.replace(fb,offs,length,str,a);
            super.insertString(fb, fb.getDocument().getLength(), System.lineSeparator(), a); 
        }
        if(str == "") {
            super.replace(fb, offs, length, str, a);
        }
    }
}
