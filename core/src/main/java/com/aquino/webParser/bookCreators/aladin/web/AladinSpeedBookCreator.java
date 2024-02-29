package com.aquino.webParser.bookCreators.aladin.web;

import java.io.IOException;
import java.util.List;

import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.romanization.Romanizer;

public class AladinSpeedBookCreator implements BookCreator {

    private final AladinBookCreator aladinBookCreator;

    public AladinSpeedBookCreator(AladinBookCreator aladinBookCreator) {
        this.aladinBookCreator = aladinBookCreator;
    }

    @Override
    public Book createBookFromIsbn(String isbn) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBookFromIsbn'");
    }

    @Override
    public Book createBookFromBookPage(String bookPageUrl) throws IOException {
        return aladinBookCreator.createBookFromBookPage(bookPageUrl);
    }

    @Override
    public Book fillInAllDetails(Book book) {
        book.setDescription(aladinBookCreator.parseDescription(book));
        aladinBookCreator.scrapeLazyAuthor(book);
        book.setRomanizedTitle(Romanizer.hangulToRoman(book.getTitle()));

        return book;
    }

    @Override
    public String BookPagePrefix() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'BookPagePrefix'");
    }

    @Override
    public List<Book> bookListFromLink(String pageofLinks) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bookListFromLink'");
    }

    @Override
    public List<Book> bookListFromIsbn(String pageofIsbns) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bookListFromIsbn'");
    }

    @Override
    public void checkInventoryAndOclc(Book result) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkInventoryAndOclc'");
    }
    
}
