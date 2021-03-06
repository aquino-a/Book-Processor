/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.oclc;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.ExcelWriter;
import com.aquino.webParser.utilities.Connect;
import com.aquino.webParser.utilities.FileUtility;
import com.aquino.webParser.utilities.Links;
import com.aquino.webParser.bookCreators.BookCreator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author alex
 */
public class OCLCChecker {
    
    private static final Logger logger = Logger.getLogger(OCLCChecker.class.getName());
    private BookCreator bookCreator;
    private ExcelWriter writer;
//    Links link;
    int hits;

    public OCLCChecker(BookCreator bookCreator)
    {
        this.bookCreator = bookCreator;
        writer = new ExcelWriter(Connect.newWorkbookFromTemplate());
//        link = new Links();
    }
    
    
    
    public void getHitsAndWrite(int pageStart, int pageEnd, JComponent component, Consumer<ProgressData> consumer) throws IOException{
        File save = FileUtility.saveLocation(component);
        if(save == null){
            throw new IllegalArgumentException("No save file selected");
        }
        try {
            for (int i = pageStart; i <= pageEnd; i++) {
                checkAndWriteOnePage(i);
                if(consumer != null)
                    consumer.accept(new ProgressData(pageStart,i,pageEnd));
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Reached end of pages");
            throw e;
        }
        finally{
            writer.saveFile(save);
            if(consumer != null)
                consumer.accept(new ProgressData(1,1,1));
        }
    }
    
    private void checkAndWriteOnePage(int pageNumber) throws IOException {
        List<Book> books = null;
        try {
            books = setHits(getOnePageCheckedBooks(pageNumber));
            if(books != null && books.size() > 0) {
                logger.log(Level.INFO, "Books found: {0}", books.size());
                writer.writeBooks(books);
            }
        } catch (IOException e) {
            throw e;
        } 
    }
    private List<Book> getOnePageCheckedBooks(int pageNumber) throws IOException {
        return checkBooks(getBooks(pageNumber));
    }
    
    private List<Book> getBooks(int pageNumber) throws IOException {
        return bookCreator.bookListFromLink(Links.getPageofLinks(pageNumber));
//        return OldBook.retrieveBookArray(Links.getPageofLinks(pageNumber));
    }
    
    private List<Book> checkBooks(List<Book> books) {
        return books.stream()
                .filter(b -> {
                    try{ bookCreator.checkInventoryAndOclc(b); return b.getOclc() != -1 && !b.isTitleExists();}
                    catch (Exception e) {
                        logger.log(Level.WARNING, String.format("Error Checking OldBook: %s", e.getMessage()));
                        return false;}}).collect(Collectors.toList());
    }

    private List<Book> setHits(List<Book> books) {
        hits += books.size();
        return books;
    }

    public int getHits() {
        return hits;
    }
    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        Links.setType(Links.Type.BEST);
        //move test elsewhere
        OCLCChecker checker = new OCLCChecker(null);
        checker.getHitsAndWrite(1,2,panel,null);
        System.exit(0);

    }


//    public void getOCLCTitles(int pageNumber, JComponent component) {
//        File save = FileUtility.saveLocation(component);
//        OldBook[] books = setHits(checkBooks(getBooks(pageNumber)));
//        if(hits > 0) {
//            System.out.println("got hits");
//            writer.writeBooks(books);
//            writer.saveFile(save);
//        } else System.out.println("no hits");
//        
//    }
}
