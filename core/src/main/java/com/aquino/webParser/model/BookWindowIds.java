package com.aquino.webParser.model;

import org.apache.commons.lang3.tuple.Pair;

public class BookWindowIds {
    private Author author;
    private Author author2;
    private Publisher publisher;
    private int excelRow;
    private Book book;

    public Author author() {
        return author;
    }

    public void author(Author author) {
        this.author = author;
    }

    public Author author2() {
        return author2;
    }

    public void author2(Author author2) {
        this.author2 = author2;
    }

    public Publisher publisher() {
        return publisher;
    }

    public void publisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Book book() {
        return book;
    }

    public void book(Book book) {
        this.book = book;
    }

    public int excelRow() {
        return excelRow;
    }

    public void excelRow(int excelRow) {
        this.excelRow = excelRow;
    }

    public void UpdateBook() {
        if (author != null) book.setAuthorId(author.getId());
        if (author2 != null) book.setAuthor2Id(author2.getId());
        if (publisher != null) book.setPublisherId(publisher.getId());
    }

    public boolean isMissingIds() {
        return (author != null && author.getId() < 1) ||
            (author2 != null && author2.getId() < 1) ||
            (publisher != null && publisher.getId() < 1);
    }
}
