/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.oclc;

import com.aquino.webParser.ExcelWriter;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;
import static com.aquino.webParser.oclc.OCLCChecker.Type.*;
import com.aquino.webParser.utilities.Connect;
import com.aquino.webParser.utilities.Links;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author alex
 */
public class OCLCChecker {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NEW_ALADIN_FORMAT = "http://www.aladin.co.kr/shop/common/wnew.aspx?ViewRowsCount=50&ViewType=Detail&SortOrder=6&page=%d";
    private static final String BEST_ALADIN_FORMAT = "https://www.aladin.co.kr/shop/common/wbest.aspx?BestType=Bestseller&BranchType=1&CID=0&page=%d";
    private String currentFormat = NEW_ALADIN_FORMAT;

    private final BookCreator bookCreator;
    private final ExcelWriter writer;

    //    Links link;
    int hits;
    private Type type = NEW;

    public OCLCChecker(BookCreator bookCreator) {
        this.bookCreator = bookCreator;
        writer = new ExcelWriter(Connect.newWorkbookFromTemplate());
    }

    public void getHitsAndWrite(
        int pageStart,
        int pageEnd,
        Consumer<ProgressData> consumer,
        File save
    ) throws IOException {
        if (save == null) {
            throw new IllegalArgumentException("No save file selected");
        }
        try {
            for (int i = pageStart; i <= pageEnd; i++) {
                checkAndWriteOnePage(i);
                if (consumer != null)
                    consumer.accept(new ProgressData(pageStart, i, pageEnd));
            }
        }
        catch (IOException e) {
            LOGGER.info("Reached end of pages");
            throw e;
        }
        finally {
            writer.saveFile(save);
            if (consumer != null)
                consumer.accept(new ProgressData(1, 1, 1));
        }
    }

    private void checkAndWriteOnePage(int pageNumber) throws IOException {
        try {
            var bookLinks = getLinks(pageNumber);
            var books =  bookCreator.bookListFromLink(bookLinks);
//            var books = setHits(getOnePageCheckedBooks(pageNumber));
            if (books != null && books.size() > 0) {
                LOGGER.info("Books found: {0}", books.size());
                writer.writeBooks(books);
            }
        }
        catch (IOException e) {
            throw e;
        }
    }

    private void getLinks(int pageNumber) {
        Jsoup.connect(String.format(currentFormat, pageNumber))
            .
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
                try {
                    bookCreator.checkInventoryAndOclc(b);
                    return b.getOclc() != -1 && !b.isTitleExists();
                }
                catch (Exception e) {
                    LOGGER.error(String.format("Error Checking OldBook: %s", e.getMessage()));
                    LOGGER.error(e.getMessage(), e);
                    return false;
                }
            }).collect(Collectors.toList());
    }

    private List<Book> setHits(List<Book> books) {
        hits += books.size();
        return books;
    }

    public int getHits() {
        return hits;
    }

    public Type type() {
        return type;
    }

    public void type(Type type) {
        this.type = type;
        switch (type) {
            case BEST:
                currentFormat = BEST_ALADIN_FORMAT;
            case NEW:
                currentFormat = NEW_ALADIN_FORMAT;
        }
    }

    public enum Type {
        BEST(20), NEW(47);
        private final int pages;

        Type(int pages) {
            this.pages = pages;
        }

        public int getPages() {
            return pages;
        }

    }
}
