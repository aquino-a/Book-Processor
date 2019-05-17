package com.aquino.webParser.bookCreators;

import com.aquino.webParser.Book;
import com.aquino.webParser.OldBook;

import java.io.IOException;
import java.util.List;

public interface BookCreator {
    Book createBookFromIsbn(String isbn);

    Book createBookFromBookPage(String bookPageUrl) throws IOException;

    Book fillInAllDetails(Book book);

    String BookPagePrefix();


    List<Book> bookListFromLink(String pageofLinks) throws IOException;

    List<Book> bookListFromIsbn(String pageofIsbns);

    void checkInventoryAndOclc(Book result);
}
