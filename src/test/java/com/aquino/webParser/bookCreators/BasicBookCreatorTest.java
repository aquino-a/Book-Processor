package com.aquino.webParser.bookCreators;

import com.aquino.webParser.model.Book;
import org.jsoup.nodes.Document;

public class BasicBookCreatorTest {

    protected final Document doc;
    protected final Book expected;

    public BasicBookCreatorTest(Document doc, Book expected) {
        this.doc = doc;
        this.expected = expected;
    }
}
