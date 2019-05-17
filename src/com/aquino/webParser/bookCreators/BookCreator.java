package com.aquino.webParser.bookCreators;

import com.aquino.webParser.Book;
import com.aquino.webParser.OldBook;

import java.io.IOException;

public interface BookCreator {
    Book createBookFromIsbn(String isbn);

    Book createBookFromBookPage(String bookPageUrl) throws IOException;

    Book fillInAllDetails(Book book);

    String BookPagePrefix();


    Book[] bookArrayFromLink(String pageofLinks);

    Book[] bookArrayFromIsbn(String pageofIsbns);

    void checkInventoryAndOclc(Book result);
}
