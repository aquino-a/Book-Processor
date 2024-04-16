package com.aquino.webParser.speed;

import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Consumer;

import com.aquino.webParser.bookCreators.BookCreator;

public class DetailDownloader {

    private final Consumer<SpeedBook> consumer;
    private final BookCreator bookCreator;
    private final BookValidator validator;

    public DetailDownloader(Consumer<SpeedBook> consumer, BookCreator bookCreator, BookValidator validator) {
        this.consumer = consumer;
        this.bookCreator = bookCreator;
        this.validator = validator;
    }

    public void Consume(SpeedBook book) throws IOException {
        var createdBook = bookCreator.createBookFromBookPage(book.itemUrl());
        book.publishDate(LocalDate.parse(createdBook.getPublishDate()));
        // set more properties as needed.

        if (!validator.isValid(book)) {
            return;
        }

        consumer.accept(book);
    }

}
