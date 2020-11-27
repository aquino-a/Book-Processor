package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Publisher;

public interface BookWindowService {
    Book findIds(Book book);

    String[] findPublisherId(String publisher);

    String[] findAuthorId(String author);

    boolean doesBookExist(String isbn);

    int addAuthor(Author author);
    int addPublisher(Publisher publisher);

    String getAuthorLink(String id);
}
