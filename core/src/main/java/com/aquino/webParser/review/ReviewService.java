package com.aquino.webParser.review;

import com.aquino.webParser.model.Book;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Book> loadBooks(String filePath);

    void saveCurrentPosition(String fileName, String isbn);

    Optional<String> loadLastPosition(String fileName);
}

