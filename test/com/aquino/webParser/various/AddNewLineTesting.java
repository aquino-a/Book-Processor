/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.various;

import com.aquino.webParser.Book;
import com.aquino.webParser.OCLC.OCLCChecker;
import com.aquino.webParser.filters.CheckFilter;
import java.awt.datatransfer.Transferable;
import java.util.function.BiConsumer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author alex
 */
public class AddNewLineTesting {
    
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        
        OCLCChecker checker = new OCLCChecker();
//        checker.getHitsAndWrite(10, panel);
        System.out.println(checker.getHits());
//        Book[] books = checker.getBooks(15);
//        books = checker.checkBooks(books);
//        for (Book book : books) {
//            System.out.println(book);
//            
//        }
        
        frame.add(panel);
//        JTextField field = new JTextField(25);
//        JButton button = new JButton("Use");
//        JTextArea area = new JTextArea();
//        String link;
//        ((AbstractDocument) field.getDocument()).
//                setDocumentFilter(new CheckFilter(new BiConsumer() {
//            @Override
//            public void accept(Object t, Object u) {
//                area.append((String) t);
//                field.setText((String) u);
//            }
//        }));
        
        
    }
    
}
