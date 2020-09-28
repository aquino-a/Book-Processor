package com.aquino.webParser.bookCreators.honto;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class HontoBookCreator implements BookCreator {

    //dyTitle
    private static final String SEARCH_URL_FORMAT = "https://honto.jp/netstore/search_10%s.html?srchf=1&tbty=0";

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if(doc == null)
            throw new IOException(String.format("Search Document wasn't loaded: %s",isbn));

        try {
            return createBookFromBookPage(
                doc.getElementsByClass("dyTitle")
                        .first().attr("href")
            );
        } catch (Exception e) {
            throw new IOException(String.format("Book page not found: %s",isbn));
        }
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        Book book = new Book();
        book.setBookPageUrl(bookPageUrl);
        return book;
    }

    @Override
    public Book fillInAllDetails(Book book) {
        return null;
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
