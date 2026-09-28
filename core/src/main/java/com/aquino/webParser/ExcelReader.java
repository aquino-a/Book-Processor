package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ExcelReader {
    List<Pair<Integer, Book>> readBooks();
}

