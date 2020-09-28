package com.aquino.webParser.bookCreators.honya;

import com.aquino.webParser.Book;
import com.aquino.webParser.BookWindowService;
import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.oclc.OclcService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;

public class HonyaClubBookCreatorTest {

    private HonyaClubBookCreator bc;

    @Before
    public void setUp() throws Exception {
        bc = new HonyaClubBookCreator();
    }

    @Test
    public void createBookFromIsbn() throws IOException {
        Book book = bc.createBookFromIsbn("9784838731275");
        Assert.assertEquals("https://www.honyaclub.com/shop/g/g20031562/", book.getBookPageUrl());
    }
}