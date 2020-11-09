package com.aquino.webParser;

import com.aquino.webParser.model.Author;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookWindowServiceImplTest {

    @Test
    public void addAuthor() {
        var bws = new BookWindowServiceImpl();
        var author = new Author();
        author.setEnglishFirstName("test111111");
        author.setEnglishLastName("test111111");
        var result = bws.addAuthor(author);
    }
}