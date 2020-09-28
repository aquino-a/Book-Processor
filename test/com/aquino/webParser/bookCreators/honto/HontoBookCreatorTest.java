package com.aquino.webParser.bookCreators.honto;

import com.aquino.webParser.Book;
import com.aquino.webParser.bookCreators.honya.HonyaClubBookCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class HontoBookCreatorTest {

    private HontoBookCreator bc;

    @Before
    public void setUp() throws Exception {
        bc = new HontoBookCreator();
    }

    @Test
    public void createBookFromIsbn() throws IOException {
        Book book = bc.createBookFromIsbn("9784309029139");
        Assert.assertEquals("https://honto.jp/netstore/pd-book_30435617.html", book.getBookPageUrl());
    }
}