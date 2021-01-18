package com.aquino.webParser;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.Language;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AutoFillServiceTest {

    @Test
    public void createAuthor() {
        var afs = new AutoFillService(null, null, null);
        afs.setLanguage(Language.Korean);

        var b = new Book();
        b.setAuthor("이장무");
        var a = afs.CreateAuthor(b);

        Assert.assertEquals("I", a.getEnglishFirstName());
        Assert.assertEquals("Jang Mu", a.getEnglishLastName());

    }
}