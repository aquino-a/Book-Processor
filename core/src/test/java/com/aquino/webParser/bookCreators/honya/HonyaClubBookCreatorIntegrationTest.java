package com.aquino.webParser.bookCreators.honya;

import com.aquino.webParser.model.Book;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.notNull;

public class HonyaClubBookCreatorIntegrationTest {

    private HonyaClubBookCreator bookCreator;

    @Before
    public void setUp() throws Exception {
        bookCreator = new HonyaClubBookCreator();
    }

    @Test
    public void createBookFromIsbn() throws IOException {
        Book book = bookCreator.createBookFromIsbn("9784838731275");
        Assert.assertEquals("https://www.honyaclub.com/shop/g/g20026713/", book.getBookPageUrl());
        Assert.assertTrue(book.getDescription().contains("これまでのアイドル写真集とは一線を画す"));
    }

    @Test
    public void createBookFromIsbn2() throws IOException {
        var book = bookCreator.createBookFromIsbn("9784022518965");

        assertThat(book.getCategory(), is("人文 > 社会・文化 > 地方文化"));
        assertThat(book.getOriginalPriceNumber(), is(2_420));
        assertThat(book.getDescription(), is(not(emptyOrNullString())));
    }
}