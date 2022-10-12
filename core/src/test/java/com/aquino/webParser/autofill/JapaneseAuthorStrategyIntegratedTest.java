package com.aquino.webParser.autofill;

import com.aquino.webParser.model.Book;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

public class JapaneseAuthorStrategyIntegratedTest {

    @Test
    public void createAuthor() {
        var strategy = new JapaneseAuthorStrategy();
        var book = new Book();
        book.setAuthor("ハンス・ロスリング");
        var result = strategy.createAuthor(book.getAuthor());
        Assert.assertEquals("Hansu Rosurin Gu", result.getEnglishLastName());
    }
}