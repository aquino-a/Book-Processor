package com.aquino.webParser.bookCreators.honya;

import com.aquino.webParser.model.Book;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HonyaClubBookCreatorTest {

    private HonyaClubBookCreator bc;

    @Before
    public void setUp() throws Exception {
        bc = new HonyaClubBookCreator();
    }

    @Test
    public void createBookFromIsbn() throws IOException {
        Book book = bc.createBookFromIsbn("9784838731275");
        Assert.assertEquals("https://www.honyaclub.com/shop/g/g20026713/", book.getBookPageUrl());
        Assert.assertTrue(book.getDescription().contains("これまでのアイドル写真集とは一線を画す"));
    }
}