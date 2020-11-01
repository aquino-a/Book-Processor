package com.aquino.webParser;

public interface BookWindowService {
    Book findIds(Book book);

    String findPublisherId(String publisher);

    String findAuthorId(String author);

    boolean doesBookExist(String isbn);

    int addAuthor(Author author);
    int addPublisher(Publisher publisher);
}
