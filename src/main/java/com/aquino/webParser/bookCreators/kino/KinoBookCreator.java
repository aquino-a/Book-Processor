package com.aquino.webParser.bookCreators.kino;

import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;

import java.io.IOException;
import java.util.List;

public class KinoBookCreator implements BookCreator {

    private static final String KINO_URL = "https://www.kinokuniya.co.jp";
    private static final String KINO_ISBN_URL = "https://www.kinokuniya.co.jp/f/dsg-01-%s";

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        var book = new Book();
        book.setBookPageUrl(String.format(KINO_ISBN_URL, isbn));
        return book;
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Book fillInAllDetails(Book book) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String BookPagePrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkInventoryAndOclc(Book result) {
        throw new UnsupportedOperationException();
    }
}
