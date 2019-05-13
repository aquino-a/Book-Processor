/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.various;

import com.aquino.webParser.OCLC.OCLCChecker;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
//        OldBook[] books = checker.getBooks(15);
//        books = checker.checkBooks(books);
//        for (OldBook book : books) {
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
