package com.aquino.webParser.bookCreators.yahoo;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.utilities.Connect;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class YahooBookCreator implements BookCreator {

    private static final String SEARCH_URL_FORMAT = "https://paypaymall.yahoo.co.jp/search?p=%s&cid=&brandid=&kspec=&b=1";

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        Document doc = Connect.connectToURL(String.format(SEARCH_URL_FORMAT, isbn));
        if(doc == null)
            throw new IOException(String.format("Search Document wasn't loaded: %s",isbn));
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
