/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.oclc;

import com.aquino.webParser.ExcelWriter;
import com.aquino.webParser.bookCreators.BookCreator;
import static com.aquino.webParser.oclc.OCLCChecker.Type.*;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
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
        } catch (IOException e) {
            LOGGER.info("Reached end of pages");
            throw e;
        } finally {
            writer.saveFile(save);
            if (consumer != null)
                consumer.accept(new ProgressData(1, 1, 1));
        }
    }

    private void checkAndWriteOnePage(int pageNumber) throws IOException {
        try {
            var bookLinks = getLinks(pageNumber);
            if (bookLinks == null || StringUtils.isBlank(bookLinks)) {
                LOGGER.info(String.format("No links found on page %d", pageNumber));
                return;
            }

            var books = bookCreator.bookListFromLink(bookLinks);
            if (books == null) {
                LOGGER.error("No books returned from bookCreator");
                return;
            }

            books.forEach(b -> {
                try {
                    bookCreator.checkInventoryAndOclc(b);
                } catch (Exception e) {
                    LOGGER.error(String.format("Error checking book: %s", b.getIsbn()), e);
                }
            });

            var filteredBooks = books.stream()
                .filter(b -> b.getOclc() != -1 && !b.isTitleExists())
                .collect(Collectors.toList());

            LOGGER.info(String.format("Books found: %d", filteredBooks.size()));

            if (filteredBooks.size() > 0) {
                writer.writeBooks(filteredBooks);
            }
        } catch (IOException e) {
            LOGGER.error("Uncaught error when checking and writing a page", e);
        }
    }

    private String getLinks(int pageNumber) {
        try {
            var linksUrl = String.format(currentFormat, pageNumber);
            var doc = Jsoup.connect(linksUrl)
                .get();

//            if (doc == null) {
//                throw new IOException(String.format("Couldn't get links page: %s", linksUrl));
//            }

            var bookElements = doc.getElementsByClass("bo3");
//            if (bookElements == null || bookElements.size() < 1) {
            if (bookElements.size() < 1) {
                throw new IOException(String.format("No book elements found: %s", linksUrl));
            }

            return bookElements
                .stream()
                .map(e -> e.attr("href"))
                .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            LOGGER.error("Problem getting links.", e);
            return null;
        }
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
