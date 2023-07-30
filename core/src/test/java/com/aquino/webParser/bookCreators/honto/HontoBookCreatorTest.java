package com.aquino.webParser.bookCreators.honto;

import com.aquino.webParser.model.Book;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HontoBookCreatorTest {

    private HontoBookCreator bc;

    @Before
    public void setUp() throws Exception {
        bc = new HontoBookCreator();
    }

    @Test
    public void createBookFromIsbn() throws IOException {
        Book book = bc.createBookFromIsbn("9784309029139");
        Assert.assertEquals("https://hontox.jp/netstore/pd-book_30435617.html", book.getBookPageUrl());
    }

    @Test
    public void createBookFromIsbnLive() throws IOException {
        Book book = bc.createBookFromIsbn("9784022518965");
        
        assertThat(book.getAgeGroup(), is(nullValue()));
        assertThat(book.getPublishDateFormatted(), is("05/12/2023"));
        assertThat(book.getBookSizeFormatted(), is(nullValue()));
    }

    @Test
    public void createBookFromIsbnLive2() throws IOException {
        Book book = bc.createBookFromIsbn("9784052057069");
        
        assertThat(book.getAgeGroup(), is("幼児"));
        assertThat(book.getPublishDateFormatted(), is("06/15/2023"));
        assertThat(book.getBookSizeFormatted(), is("21 x 22"));
    }

    @Test
    public void createBookFromIsbnLive3() throws IOException {
        Book book = bc.createBookFromIsbn("9784001142587");
        
        assertThat(book.getAgeGroup(), is("小学生"));
        assertThat(book.getPublishDateFormatted(), is("06/19/2023"));
        assertThat(book.getBookSizeFormatted(), is(nullValue()));
    }

    @Test
    public void createBookFromIsbnLive4() throws IOException {
        Book book = bc.createBookFromIsbn("9784040645377");
        
        assertThat(book.getPublisher(), is("KADOKAWA"));
        assertThat(book.getAgeGroup(), is("一般"));
        assertThat(book.getPublishDateFormatted(), is("05/25/2023"));
        assertThat(book.getBookSizeFormatted(), is(nullValue()));
    }
}