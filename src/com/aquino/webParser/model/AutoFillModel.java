package com.aquino.webParser.model;

import org.apache.commons.lang3.tuple.Pair;

public class AutoFillModel {
    private Author author;
    private Publisher publisher;
    private Pair<Integer, Book> bookPair;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Pair<Integer, Book> getBookPair() {
        return bookPair;
    }

    public void setBookPair(Pair<Integer, Book> bookPair) {
        this.bookPair = bookPair;
    }

    public void UpdateBook() {
        bookPair.getRight().setAuthor(String.valueOf(author.getId()));
        //TODO set publisher
    }
}
