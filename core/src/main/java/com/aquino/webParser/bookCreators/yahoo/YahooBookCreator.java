package com.aquino.webParser.bookCreators.yahoo;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class YahooBookCreator implements BookCreator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SEARCH_URL_FORMAT = "https://paypaymall.yahoo.co.jp/search?p=%s&cid=&brandid=&kspec=&b=1";

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if (doc == null)
            throw new IOException(String.format("Search Document wasn't loaded: %s", isbn));
        Book book = new Book();
        book.setBookPageUrl(doc.location());
        return book;
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        return null;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        try {
            var searchDoc = Jsoup.connect(book.getBookPageUrl())
                    .get();

            var bookUrl = searchDoc.getElementsByClass("LoopList__item")
                    .stream()
                    .filter(e -> e.text().contains("Honya"))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No Honya Club book page."))
                    .getElementsByClass("SearchResult_SearchResult__detailsContainerLink__0hDP4")
                    .first()
                    .attr("href");

            var bookDoc = Jsoup.connect(bookUrl)
                    .get();

            var kataganaLine = bookDoc.getElementsByClass("mdItemDescription")
                    .first()
                    .child(0)
                    .textNodes()
                    .stream()
                    .reduce((a, b) -> b)
                    .orElseThrow(() -> new Exception("No lines in description."))
                    .text();

            var parts = StringUtils.split(kataganaLine, '、');

            if (parts.length > 1) {
                book.setAuthorBooks(parts[1]);
            } 
            
            if (parts.length > 2) {
                if (book.getAuthor2Id() < 0) {
                    book.setAuthor2Books(parts[2]);
                } else {
                    LOGGER.warn("Author 2 id is greater than -1. Not setting katagana for 2nd author.");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Problem getting Yahoo katagana.", e);
        }

        return book;
    }

    @Override
    public String BookPagePrefix() {
        return null;
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        return null;
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        return null;
    }

    @Override
    public void checkInventoryAndOclc(Book result) {

    }
}
