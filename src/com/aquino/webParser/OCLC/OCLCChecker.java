/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.OCLC;

import com.aquino.webParser.Book;
import com.aquino.webParser.ExcelWriter;
import com.aquino.webParser.Utilities.Connect;
import com.aquino.webParser.Utilities.FileUtility;
import com.aquino.webParser.Utilities.Links;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author alex
 */
public class OCLCChecker {
    ExcelWriter writer;
//    Links link;
    int hits;

    public OCLCChecker() {
        writer = new ExcelWriter(Connect.newWorkbookFromTemplate());
//        link = new Links();
    }
    
    
    
    public void getHitsAndWrite(int pageStart, int pageEnd, JComponent component) {
        File save = FileUtility.saveLocation(component);
        try {
            for (int i = pageStart; i <= pageEnd; i++) {
            checkAndWriteOnePage(i);
            }
        } catch (IOException e) {
            System.out.println("No more pages");
        }       
        writer.saveFile(save);
    }
    
    private void checkAndWriteOnePage(int pageNumber) throws IOException {
        Book[] books = null;
        try {
             books = setHits(getOnePageCheckedBooks(pageNumber));
            
        } catch (IOException e) {
            throw e;
        } finally {
            if(books != null && books.length > 0)
                writer.writeBooks(books);
        }   
    }
    private Book[] getOnePageCheckedBooks(int pageNumber) throws IOException {
        return checkBooks(getBooks(pageNumber));
    }
    
    private Book[] getBooks(int pageNumber) throws IOException {
        return Book.retrieveBookArray(Links.getPageofLinks(pageNumber));
    }
    
    private Book[] checkBooks(Book[] books) {
        return Stream.of(books)
                .filter(b -> b.getOCLC() != -1 && !b.titleExists())
                .toArray(Book[]::new);
    }
    private Book[] setHits(Book[] books) {
        hits += books.length;
        return books;
    }
    public int getHits() {
        return hits;
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        
        OCLCChecker checker = new OCLCChecker();
        checker.getHitsAndWrite(1,80,panel);
        System.exit(0);

    }
//    public void getOCLCTitles(int pageNumber, JComponent component) {
//        File save = FileUtility.saveLocation(component);
//        Book[] books = setHits(checkBooks(getBooks(pageNumber)));
//        if(hits > 0) {
//            System.out.println("got hits");
//            writer.writeBooks(books);
//            writer.saveFile(save);
//        } else System.out.println("no hits");
//        
//    }
}
